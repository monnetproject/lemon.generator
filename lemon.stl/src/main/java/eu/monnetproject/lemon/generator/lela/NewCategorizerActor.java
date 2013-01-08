/****************************************************************************
 * Copyright (c) 2011, Monnet Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Monnet Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/
package eu.monnetproject.lemon.generator.lela;

import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.generator.ActorGenerationReport.Status;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.generator.lela.categorizer.CategorizerParseException;
import eu.monnetproject.lemon.generator.lela.categorizer.CategorizerParser;
import eu.monnetproject.lemon.generator.lela.categorizer.CategorizerRule;
import eu.monnetproject.lemon.generator.lela.categorizer.Tree;
import eu.monnetproject.lemon.model.Argument;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.Constituent;
import eu.monnetproject.lemon.model.Edge;
import eu.monnetproject.lemon.model.Frame;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.model.Node;
import eu.monnetproject.lemon.model.SynArg;
import eu.monnetproject.ontology.DatatypeProperty;
import eu.monnetproject.ontology.Individual;
import eu.monnetproject.ontology.ObjectProperty;
import eu.monnetproject.ontology.Property;
import eu.monnetproject.util.ResourceFinder;
import eu.monnetproject.util.Logger;
import eu.monnetproject.util.Logging;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author John McCrae
 */
public class NewCategorizerActor implements GeneratorActor {

    private final Logger log = Logging.getLogger(this);
    private final HashMap<Language, List<CategorizerRule>> rules = new HashMap<Language, List<CategorizerRule>>();

    @Override
    public void perform(LexicalEntry le, GenerationState gs) {
        // Skip if the entry does not have phrase information
        if (le.getDecompositions().isEmpty() || le.getPhraseRoots().isEmpty() || gs.getEntity() == null) {
            log.warning(le.getDecompositions().isEmpty() + " " + le.getPhraseRoots().isEmpty() + " " + gs.getEntity());
            gs.report(new ActorGenerationReportImpl("Categorizer", Status.NO_INFO, "Decomposition, phrase root or sense missing"));
            return;
        }
        final Language language = gs.getLanguage();
        // Read rules if not already loaded
        if (!rules.containsKey(language)) {
            try {
                final Reader reader = ResourceFinder.getResourceAsReader(language + ".newlela");
                if (reader == null) {
                    gs.report(new ActorGenerationReportImpl("Categorizer", Status.NO_INFO, "Could not locate rule file"));
                    return;
                }
                final List<CategorizerRule> rules2 = CategorizerParser.parser(reader);
                rules.put(language, rules2);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (CategorizerParseException x) {
                throw new RuntimeException(x);
            }
        }
        boolean success = false;
        // Apply for each rule
        for (CategorizerRule rule : rules.get(language)) {
            for (Node node : le.getPhraseRoots()) {
                success = (applyRule(rule, node, le, gs) != null) || success;
            }
        }
        if (!success) {
            log.warning("Could not categorize: " + printTrees(le));
            gs.report(new ActorGenerationReportImpl("Categorizer", Status.FAILED, "No rule applied"));
        } else {
            gs.report(new ActorGenerationReportImpl("Categorizer", Status.OK, "Exited OK"));
        }
    }

    private String constFrag(Constituent constituent) {
        final String constURI = constituent.getURI().toString();
        return constURI.substring(constURI.lastIndexOf("/") + 1);
    }

    private void printNode(Node n, StringBuilder sb) {
        if (n.getConstituent() != null) {
            sb.append(constFrag(n.getConstituent()));
        } else {
            sb.append("#UNK");
        }
        for (Map.Entry<Edge, Collection<Node>> n2s : n.getEdges().entrySet()) {
            for (Node n2 : n2s.getValue()) {
                sb.append("(");
                printNode(n2, sb);
                sb.append(")");
            }
        }
    }

    private String printTrees(LexicalEntry entry) {
        StringBuilder sb = new StringBuilder();
        for (Node node : entry.getPhraseRoots()) {
            sb.append("[");
            printNode(node, sb);
            sb.append("]");
        }
        return sb.toString();
    }

    @Override
    public double getPriority() {
        return 50;
    }
    private static final URI OWL_CLASS = URI.create("http://www.w3.org/2002/07/owl#Class");
    private static final URI OWL_OBJECT_PROPERTY = URI.create("http://www.w3.org/2002/07/owl#ObjectProperty");
    private static final URI OWL_DATATYPE_PROPERTY = URI.create("http://www.w3.org/2002/07/owl#DatatypeProperty");
    private static final URI OWL_INDIVIDUAL = URI.create("http://www.w3.org/2002/07/owl#Individual");
    private static final URI OWL_NAMED_INDIVIDUAL = URI.create("http://www.w3.org/2002/07/owl#NamedIndividudal");
    private static final URI RDFS_CLASS = URI.create("http://www.w3.org/2000/01/rdf-schema#Class");
    private static final URI RDF_PROPERTY = URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");

    private List<LexicalEntry> applyRule(CategorizerRule rule, Node node, LexicalEntry le, GenerationState gs) {
        if (checkSemantics(gs, rule)) {
            log.info("semantics failed " + rule.frame);
            return null;
        }
        // Check the tree
        RuleApplication app = applyRule(rule.tree, node, le, gs);
        if (app == null) {
            log.info("tree failed " + rule.frame);
            return null;
        }
        // Check to see if we need to introduce a new lexical entry
        boolean newDecomp = true;
        for (List<Component> decomp : le.getDecompositions()) {
            if (decomp.size() == app.entries.size()) {
                newDecomp = false;
            }
        }
        if (app.entries.isEmpty()) {
            throw new RuntimeException("Empty decomposition");
        }
        LexicalEntry entry;
        if (newDecomp) {
            log.info("new decomposition size=" + app.entries.size());
            entry = createNewEntry(app.entries, gs);
        } else {
            entry = le;
        }

        log.info("making frame " + rule.frame);
        // Make the frame
        final LemonFactory factory = gs.getModel().getFactory();
        Frame frame = factory.makeFrame(gs.namer().name(gs.getLexiconName(), gs.getEntryName(), "frame"));
        final URI frameURI = gs.getLingOnto().getFrameClass(rule.frame);
        frame.addType(frameURI);
        entry.addSynBehavior(frame);
        final int arity = gs.getLingOnto().getSynArgsForFrame(frameURI).size();
        for (SynArg synArg : gs.getLingOnto().getSynArgsForFrame(frameURI)) {
            Argument arg = factory.makeArgument(gs.namer().name(gs.getLexiconName(), gs.getEntryName(), "arg"));
            frame.addSynArg(synArg, arg);
            // Also bind any approriate sem arg map
            for (LexicalSense sense : entry.getSenses()) {
                String frag = synArg.getURI().getFragment();
                if (arity == 2) {
                    if (frag.contains("obj")) {
                        sense.addObjOfProp(arg);
                    } else {
                        sense.addSubjOfProp(arg);
                    }
                } else if (arity == 1) {
                    sense.addIsA(arg);
                }
            }
            if (app.markers.containsKey(synArg.getURI().getFragment())) {
                log.info("Adding marker:" + synArg.getURI().getFragment());
                arg.setMarker(app.markers.get(synArg.getURI().getFragment()).get(0));
            }
        }
        return app.entries;
    }

    private boolean checkSemantics(GenerationState gs, CategorizerRule rule) {
        // Check the semantics
        if (gs.getEntity() instanceof eu.monnetproject.ontology.Class) {
            if (!rule.semantics.contains(OWL_CLASS)
                    && !rule.semantics.contains(RDFS_CLASS)) {
                boolean ok = false;
                for (eu.monnetproject.ontology.Class superClass : ((eu.monnetproject.ontology.Class) gs.getEntity()).getSuperClassOf()) {
                    if (superClass.getURI() != null && rule.semantics.contains(superClass.getURI())) {
                        ok = true;
                    }
                }
                if (!ok) {
                    return true;
                }
            }
        } else if (gs.getEntity() instanceof ObjectProperty) {
            if (!rule.semantics.contains(OWL_OBJECT_PROPERTY)
                    && !rule.semantics.contains(RDF_PROPERTY)) {
                boolean ok = false;
                for (Property superProp : ((ObjectProperty) gs.getEntity()).getSuperPropertyOf()) {
                    if (superProp.getURI() != null && rule.semantics.contains(superProp.getURI())) {
                        ok = true;
                    }
                }
                if (!ok) {
                    return true;
                }
            }
        } else if (gs.getEntity() instanceof DatatypeProperty) {
            if (!rule.semantics.contains(OWL_DATATYPE_PROPERTY)
                    && !rule.semantics.contains(RDF_PROPERTY)) {
                boolean ok = false;
                for (Property superProp : ((DatatypeProperty) gs.getEntity()).getSuperPropertyOf()) {
                    if (superProp.getURI() != null && rule.semantics.contains(superProp.getURI())) {
                        ok = true;
                    }
                }
                if (!ok) {
                    return true;
                }
            }
        } else if (gs.getEntity() instanceof Individual) {
            if (!rule.semantics.contains(OWL_INDIVIDUAL) && !rule.semantics.contains(OWL_NAMED_INDIVIDUAL)) {
                boolean ok = false;
                for (eu.monnetproject.ontology.Class type : ((Individual) gs.getEntity()).getType()) {
                    if (type.getURI() != null && rule.semantics.contains(type.getURI())) {
                        ok = true;
                    }
                }
                if (!ok) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    private boolean match(Tree tree, Node node) {
        if (tree.literal == null) {
            return tree.constituent.equals(constFrag(node.getConstituent()));
        } else {
            return tree.constituent.equals(constFrag(node.getConstituent()))
                    && node.getLeaf() != null
                    && node.getLeaf() instanceof LexicalEntry
                    && tree.literal.equals(((LexicalEntry) node.getLeaf()).getCanonicalForm().getWrittenRep().value);
        }
    }

    private RuleApplication applyRule(Tree tree, Node node, LexicalEntry le, GenerationState gs) {
        final RuleApplication app = new RuleApplication();
        log.info("applyRule " + tree.constituent + tree.subnodes.size());
        if (match(tree, node) || tree.constituent.length() == 0) {
            if (tree.subnodes.size() > node.getEdge(Edge.edge).size()) {
                log.warning("failing on node size diff");
                return null;
            }
            TREE:
            for (Tree subtree : tree.subnodes) {
                for (Node subnode : node.getEdge(Edge.edge)) {
                    if (match(subtree, subnode)) {
                        RuleApplication application = applyRule(subtree, subnode, le, gs);
                        if (application != null) {
                            if (!subtree.ignore) {
                                app.add(application);
                            } else {
                                app.markers.putAll(application.markers);
                            }
                            continue TREE;
                        }
                    }
                }
                log.warning("failing on node " +subtree.constituent);
                return null;
            }
            if (tree.subnodes.isEmpty()) {
                log.info("empty subnodes");
                if (node.getLeaf() != null) {
                    log.info("this is a leaf");
                    app.entries.add(((Component) node.getLeaf()).getElement());
                } else {
                    log.info("starting to collect leaves");
                    for (Collection<Node> subnodes : node.getEdges().values()) {
                        for (Node subnode : subnodes) {
                            final RuleApplication applyRule = collectLeaves(subnode, le, gs);
                            if (applyRule != null) {
                                app.add(applyRule);
                            }
                        }
                    }
                }
            }
            if (tree.head) {
                addHead(app, le, gs);
            }
            if (tree.marker != null) {
                addMarker(tree.marker, app, le, gs);
            }
            return app;
        } else {
            log.warning("failing on mismatched type");
            return null;
        }
    }

    private LexicalEntry createNewEntry(List<LexicalEntry> newDecomp, GenerationState gs) {
        StringBuilder sb = new StringBuilder();
        for (LexicalEntry entry : newDecomp) {
            sb.append(entry.getCanonicalForm().getWrittenRep());
        }
        LexicalEntry newEntry = gs.addLexicalEntry(sb.toString(), gs.getLanguage());
        final LemonFactory factory = gs.getModel().getFactory();
        final LexicalSense sense = factory.makeSense(gs.namer().name(gs.getLexiconName(), gs.getEntryName(), "sense"));
        sense.setReference(gs.getEntity().getURI());
        newEntry.addSense(sense);
        return newEntry;
    }

    private void addHead(RuleApplication app, LexicalEntry le, GenerationState gs) {
        if(app.entries.size() != 1) {
            log.warning("Head of size more than 1, skipping");
        }
        for (List<Component> comps : le.getDecompositions()) {
            for (Component component : comps) {
                if(component.getElement().equals(app.entries.get(0))) {
                    le.setHead(component);
                }
            }
        }
    }

    private void addMarker(String marker, RuleApplication app, LexicalEntry le, GenerationState gs) {
        app.markers.put(marker, app.entries);
    }

    private RuleApplication collectLeaves(Node subnode, LexicalEntry le, GenerationState gs) {
        final RuleApplication app = collectLeaves(subnode, le);
        for (List<Component> comps : le.getDecompositions()) {
            int i = 0;
            for (Component comp : comps) {
                if (app.entries.contains(comp.getElement()) && comps.size() >= i + app.entries.size()) {
                    final int appSize = app.entries.size();
                    app.entries.clear();
                    for (int j = i; j < i + appSize; j++) {
                        app.entries.add(comps.get(j).getElement());
                    }
                    return app;
                }
                i++;
            }
        }
        throw new RuntimeException("Could not locate proposed leaves in an entry decomposition");
    }

    private RuleApplication collectLeaves(Node subnode, LexicalEntry le) {
        log.info("Collecting leaves");
        final RuleApplication app = new RuleApplication();
        if (subnode.getLeaf() != null) {
            app.entries.add(((Component) subnode.getLeaf()).getElement());
        } else {
            for (Collection<Node> subnodes : subnode.getEdges().values()) {
                for (Node node : subnodes) {
                    final RuleApplication app2 = collectLeaves(node, le);
                    app.add(app2);
                }
            }
        }
        log.info("no of leaves: " + app.entries.size());
        return app;
    }

    private static class RuleApplication {

        final List<LexicalEntry> entries = new LinkedList<LexicalEntry>();
        final Map<String, List<LexicalEntry>> markers = new HashMap<String, List<LexicalEntry>>();

        private void add(RuleApplication application) {
            this.entries.addAll(application.entries);
            this.markers.putAll(application.markers);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RuleApplication other = (RuleApplication) obj;
            if (this.entries != other.entries && (this.entries == null || !this.entries.equals(other.entries))) {
                return false;
            }
            if (this.markers != other.markers && (this.markers == null || !this.markers.equals(other.markers))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + (this.entries != null ? this.entries.hashCode() : 0);
            hash = 97 * hash + (this.markers != null ? this.markers.hashCode() : 0);
            return hash;
        }
    }
}
