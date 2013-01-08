/**********************************************************************************
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
 *********************************************************************************/
package eu.monnetproject.lemon.generator.lela;

import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.URIElement;
import eu.monnetproject.lemon.URIValue;
import eu.monnetproject.lemon.generator.ActorGenerationReport.Status;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Node;
import eu.monnetproject.lemon.model.Text;
import eu.monnetproject.parser.Parser;
import eu.monnetproject.parser.ParserFactory;
import eu.monnetproject.parser.TreeNode;
import eu.monnetproject.pos.POSTag;
import eu.monnetproject.pos.POSToken;
import eu.monnetproject.tokens.Token;
import java.net.URI;
import eu.monnetproject.util.Logging;
import eu.monnetproject.util.Logger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * The actor that parses the entry
 * @author John McCrae
 */
public class ParserActor implements GeneratorActor {

    private static final String[] S_TAGS = {"S"};
    private static final String[] BETTER_TAGS = {"NP", "VP", "ADJP", "FRAG"};
    private static final int ALT_PARSE_COUNT = 50;
    private final ParserFactory parserFactory;
    private Logger log = Logging.getLogger(this);

    public ParserActor(ParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }

    @Override
    public void perform(LexicalEntry entry, GenerationState state) {
        final LemonModel model = state.getModel();
        final Language targetLanguage = state.getLanguage();
        final LemonFactory factory = model.getFactory();
        if (!entry.getPhraseRoots().isEmpty()) {
            log.info("Parser Actor: Already parsed, skipping");
            state.report(new ActorGenerationReportImpl("Parser", Status.NO_INFO, "Already parsed"));
            return;
        }
        Parser parser = parserFactory.getParser(targetLanguage);
        if (parser == null) {
           log.warning("No parser for language: " + targetLanguage);
           state.report(new ActorGenerationReportImpl("Parser", Status.FAILED, "No parser for " + targetLanguage.getName()));
           return;
        }
        final String tagset = parser.getTagSet();
        final ParseConverter parseConverter = new ParseConverter(tagset);
        if (parseConverter == null) {
            log.warning("No suitable parse converter");
            state.report(new ActorGenerationReportImpl("Parser", Status.FAILED, "No suitable parse converter"));
            return;
        }
        log.config("Parser actor: started (" + entry.getURI() + ")");
        List<Token> tokens = new LinkedList<Token>();
        List<Component> components = null;
        if (!entry.getDecompositions().isEmpty()) {
            components = entry.getDecompositions().iterator().next();
            for (Component comp : components) {
                LexicalEntry entry2 = comp.getElement();
                Text text = TokenizerActor.getForm(entry2, targetLanguage.getScript());
                if (text == null) {
                    log.info("No form for component");
                    continue;
                }
                tokens.add(new SimpleToken(text.value));

            }
        } else {
            if (!entry.getProperty(TaggerActor.tagURI).isEmpty()) {
                log.config("Already tagged! No need to parse");
                return;
            }
            Text text = TokenizerActor.getForm(entry, targetLanguage.getScript());
            if (text == null) {
                log.warning("Could not locate text");
                return;
            }
            tokens.add(new SimpleToken(text.value));
        }

        if (tokens.isEmpty()) {
            log.warning("No tokens");
            return;
        }

        log.info("Looking for parse");
        addParseInfo(tokens, components, entry, factory, parser, parseConverter, TagConverter.getConverter(state.getLanguage(), tagset, state.getLingOnto()),state);
        log.config("Parser actor: ended");
    }

    private static boolean isSentenceParse(TreeNode root) {
        for (String S : S_TAGS) {
            if (root.getTag().getPOS().getValue().equals(S)) {
                return true;
            }
        }
        return false;
    }

    private void addParseInfo(List<Token> tokens, List<Component> components, LexicalEntry entry, LemonFactory factory, Parser parser, ParseConverter parseConverter, TagConverter tagConverter, GenerationState state) {
        if(tagConverter == null) {
            log.warning("No tag converter");
            state.report(new ActorGenerationReportImpl("Parser", Status.FAILED, "No tag converter"));
            return;
        }
        TreeNode root = parser.parse(tokens);

        // Hack here as many parsers prefer to produce parse trees with S roots
        if (isSentenceParse(root)) {
            List<TreeNode> parses = parser.bestParses(tokens, ALT_PARSE_COUNT);
            TreeNode[] best = new TreeNode[BETTER_TAGS.length];
            for (TreeNode root2 : parses) {
                for (int i = 0; i < BETTER_TAGS.length; i++) {
                    if (best[i] == null && root2.getTag().getPOS().getValue().equals(BETTER_TAGS[i])) {
                        best[i] = root2;
                    }
                }
            }
            for (TreeNode better : best) {
                if (better != null) {
                    root = better;
                    break;
                }
            }
        }

        Node node = factory.makeNode(state.namer().name(state.getLexiconName(), state.getEntryName(), "node"));

        log.info("Parser actor: Adding parse");
        entry.addPhraseRoot(node);

        // Recurse through the tree
        addParseInfo(components, factory, entry, node, root, 0, parseConverter, tagConverter,state);
        if(node.getConstituent() != null) {
            state.report(new ActorGenerationReportImpl("Parser", Status.OK, "Added parser " + node.getConstituent().getURI().getFragment()));
        } else {
            state.report(new ActorGenerationReportImpl("Parser", Status.FAILED, "Added node as direct leaf"));
        }
    }

    private int addParseInfo(List<Component> components, LemonFactory factory, LexicalEntry entry, Node node, TreeNode root, int leafOffset, ParseConverter parseConverter, TagConverter tagConverter, GenerationState state) {
        int lo = leafOffset;

        // For each child add a new node and recurse
        for (TreeNode.Child child : root.getChildren()) {
            Node subNode = factory.makeNode(state.namer().name( state.getLexiconName(), state.getEntryName(), "node"));

            eu.monnetproject.lemon.model.Edge edge;
            if (parseConverter.getEdge(child.edge().getName()).equals(eu.monnetproject.lemon.model.Edge.edge.getURI())) {
                edge = eu.monnetproject.lemon.model.Edge.edge;
            } else {
                edge = new EdgeImpl(parseConverter.getEdge(child.edge().getName()));
            }

            node.addEdge(edge, subNode);

            lo = addParseInfo(components, factory, entry, subNode, child.node(), lo, parseConverter, tagConverter,state);
        }

        // If a leaf connect to the component
        if (root.isLeaf()) {
            if (components != null && !root.getToken().getValue().matches("\\W*")) {
                node.setLeaf(components.get(lo));
                lo++;
            }
            // If a preterminal add parse information
        } else if (isPreTerminal(root)) {
            String token = root.getChildren().get(0).node().getToken().getValue();
            log.config("Parser Actor: Got pre-terminal: " + token + " components: " + (components != null));
            if (!token.matches("\\W*")) {
                LexicalEntry entry2;
                if (components != null) {
                    entry2 = components.get(lo - 1).getElement();
                } else {
                    entry2 = entry;
                }
                TaggerActor.augmentEntry(entry2, tagConverter.getFormProperties(root.getTag()), new POSTokenImpl(token, root.getTag()), factory);
                if (tagConverter != null) {
                    Map<URI, Collection<URI>> entryProps = tagConverter.getEntryProperties(root.getTag());
                    for (URI prop : entryProps.keySet()) {
                        for (URI val : entryProps.get(prop)) {
                            log.config("Parser Actor: Adding tag " + prop + " -> " + val);
                            entry2.addProperty(TaggerActor.prop(prop), TaggerActor.val(val));
                        }
                    }
                } else {
                    log.warning("Parser Actor: No tag converter");
                }
            }
            node.setConstituent(new ConstituentImpl(
                    parseConverter.getNode(root.getTag().getPOS().getValue())));
            // Otherwise just add the constituent			
        } else {
            node.setConstituent(new ConstituentImpl(
                    parseConverter.getNode(root.getTag().getPOS().getValue())));
        }

        // Return the new leaf offset
        return lo;
    }

    public LemonModel getAuxiliaryLexicon() {
        return null;
    }

    private static boolean isPreTerminal(TreeNode node) {
        List<TreeNode.Child> children = node.getChildren();
        if (children.size() == 1) {
            return children.get(0).node().isLeaf();
        } else {
            return false;
        }
    }

    private static class POSTokenImpl implements POSToken {

        private final String value;
        private final POSTag tag;

        public POSTokenImpl(String value, POSTag tag) {
            this.value = value;
            this.tag = tag;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Deprecated
        @Override
        @SuppressWarnings("deprecation")
        public String getLemma() {
            return value;
        }

        @Override
        public POSTag getPOSTag() {
            return tag;
        }
    }

    private static String parseString(TreeNode head) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        if (head.getTag() != null) {
            buf.append(head.getTag().toString()).append(" ");
        } else {
            buf.append("\"").append(head.getToken().getValue()).append("\" ");
        }
        for (TreeNode.Child child : head.getChildren()) {
            buf.append(parseString(child.node()));
        }
        buf.append(")");
        return buf.toString();
    }

    @Override
    public double getPriority() {
        return 20;
    }
}
class EdgeImpl extends URIValue implements eu.monnetproject.lemon.model.Edge {

    public EdgeImpl(URI uri) {
        super(uri);
    }
}

class ConstituentImpl extends URIElement implements eu.monnetproject.lemon.model.Constituent {

    public ConstituentImpl(URI uri) {
        super(uri);
    }
}
