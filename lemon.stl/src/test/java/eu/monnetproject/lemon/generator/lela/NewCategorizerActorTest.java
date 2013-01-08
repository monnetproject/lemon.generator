package eu.monnetproject.lemon.generator.lela;

import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.generator.ActorGenerationReport;
import eu.monnetproject.lemon.model.Argument;
import eu.monnetproject.lemon.model.Frame;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.Node;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.ontology.AnnotationProperty;
import eu.monnetproject.ontology.AnnotationValue;
import eu.monnetproject.ontology.Class;
import eu.monnetproject.ontology.ClassOrDatatype;
import eu.monnetproject.ontology.Entity;
import eu.monnetproject.ontology.Ontology;
import eu.monnetproject.ontology.Property;
import java.net.URI;
import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.LemonElementNamer;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.Constituent;
import eu.monnetproject.lemon.model.Edge;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.SynArg;
import eu.monnetproject.ontology.ObjectProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import net.lexinfo.LexInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jmccrae
 */
public class NewCategorizerActorTest {
    public static final String WORKSAS = "file:test#works+as";
    
    public NewCategorizerActorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of perform method, of class NewCategorizerActor.
     */
    @Test
    public void testPerform() {
        System.out.println("perform");
        final LemonSerializer simpleLemonSerializer = LemonSerializer.newInstance();
        final LemonModel model = simpleLemonSerializer.create();
        final LemonFactory factory = model.getFactory();
        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon"), Language.ENGLISH.toString());
        final URI entityUR = URI.create("file:ontology#worksAs");
        final LexicalEntry worksAs = LemonModels.addEntryToLexicon(lexicon, URI.create(WORKSAS), "work as", entityUR);
        final LexicalEntry work = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#work"), "work", null);
        final LexicalEntry as = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#as"), "as", null);
        final LinkedList<Component> decomp = new LinkedList<Component>();
        final Component workComp = factory.makeComponent(URI.create("file:test#work/comp"));
        workComp.setElement(work);
        decomp.add(workComp);
        final Component asComp = factory.makeComponent(URI.create("file:test#as/comp"));
        asComp.setElement(as);
        decomp.add(asComp);
        worksAs.addDecomposition(decomp);
        final Node headNode = factory.makeNode(URI.create("file:test#workAs/headNode"));
        headNode.setConstituent(new Cons("FRAG"));
        final Node VPNode = factory.makeNode(URI.create("file:test#workAs/vpNode"));
        VPNode.setConstituent(new Cons("VP"));
        final Node VVNode = factory.makeNode(URI.create("file:test#workAs/bbNode"));
        VVNode.setConstituent(new Cons("VV"));
        VVNode.setLeaf(workComp);
        VPNode.addEdge(Edge.edge, VVNode);
        final Node PPNode = factory.makeNode(URI.create("file:test#workAs/ppNode2"));
        PPNode.setConstituent(new Cons("PP"));
        final Node INNode = factory.makeNode(URI.create("file:test#workAs/inNode"));
        INNode.setConstituent(new Cons("IN"));
        INNode.setLeaf(asComp);
        PPNode.addEdge(Edge.edge, INNode);
        headNode.addEdge(Edge.edge, VPNode);
        headNode.addEdge(Edge.edge, PPNode);
        worksAs.addPhraseRoot(headNode);
        
        NewCategorizerActor instance = new NewCategorizerActor();
        final State gs = new State(entityUR, lexicon, model,work);
        instance.perform(worksAs, gs);
        
        assertFalse(work.getSynBehaviors().isEmpty());
        final Frame frame = work.getSynBehaviors().iterator().next();
        final URI frameClass = gs.getLingOnto().getFrameClass("IntransitivePPFrame");
        final Collection<SynArg> synArgsForFrame = gs.getLingOnto().getSynArgsForFrame(frameClass);
        for(SynArg synArg : synArgsForFrame) {
            System.err.println(synArg.getURI());
        }
        Argument arg = null;
        for(SynArg synArg : frame.getSynArgs().keySet()) {
            System.err.println(synArg.getURI());
            assertFalse(frame.getSynArg(synArg).isEmpty());
            if(synArg.getURI().getFragment().startsWith("p")) {
                arg = frame.getSynArg(synArg).iterator().next();
            }
        }
        assertNotNull(arg);
        assertFalse(arg.getMarker() == null);
        assertEquals("as",((LexicalEntry)arg.getMarker()).getCanonicalForm().getWrittenRep().value);
        
        
        final LexicalEntry worksAs2 = LemonModels.addEntryToLexicon(lexicon, URI.create(WORKSAS), "work as", entityUR);
        final LinkedList<Component> decomp2 = new LinkedList<Component>();
        final Component workComp2 = factory.makeComponent(URI.create(WORKSAS+"/workComp2"));
        workComp2.setElement(work);
        decomp2.add(workComp2);
        final Component asComp2 = factory.makeComponent(URI.create(WORKSAS+"/asComp2"));
        asComp2.setElement(as);
        decomp2.add(asComp2);
        worksAs2.addDecomposition(decomp2);
        final Node headNode2 = factory.makeNode(URI.create(WORKSAS+"/headNode2"));
        headNode2.setConstituent(new Cons("NP"));
        final Node NPNode = factory.makeNode(URI.create(WORKSAS+"/npnode"));
        NPNode.setConstituent(new Cons("NP"));
        final Node NNNode = factory.makeNode(URI.create(WORKSAS+"/npnode2"));
        NNNode.setConstituent(new Cons("NN"));
        NNNode.setLeaf(workComp2);
        NPNode.addEdge(Edge.edge, NNNode);
        final Node PPNode2 = factory.makeNode(URI.create(WORKSAS+"/ppnode"));
        PPNode2.setConstituent(new Cons("PP"));
        final Node INNode2 = factory.makeNode(URI.create(WORKSAS+"/innode"));
        INNode2.setConstituent(new Cons("IN"));
        INNode2.setLeaf(asComp2);
        PPNode2.addEdge(Edge.edge, INNode2);
        headNode2.addEdge(Edge.edge, NPNode);
        headNode2.addEdge(Edge.edge, PPNode2);
        worksAs2.addPhraseRoot(headNode2);
        
        instance.perform(worksAs2, gs);
    }
    
    public static void main(String[] args) {
        new NewCategorizerActorTest().testPerform();
    }

    /**
     * Test of getPriority method, of class NewCategorizerActor.
     */
    @Test
    public void testGetPriority() {
    }
    
    private static class State implements GenerationState {
        private final URI entityURI;
        private final Lexicon lexicon;
        private final LemonModel model;
        private final LinguisticOntology lingOnto = new LexInfo();
        private final LexicalEntry work;

        public State(URI entityURI, Lexicon lexicon, LemonModel model, LexicalEntry work) {
            this.entityURI = entityURI;
            this.lexicon = lexicon;
            this.model = model;
            this.work = work;
        }

        
        @Override
        public Entity getEntity() {
            return new OP(entityURI);
        }

        @Override
        public Language getLanguage() {
            return Language.ENGLISH;
        }

        @Override
        public Lexicon getLexicon() {
            return lexicon;
        }

        @Override
        public LemonModel getModel() {
            return model;
        }

        @Override
        public LinguisticOntology getLingOnto() {
            return lingOnto;
        }

        @Override
        public LexicalEntry addLexicalEntry(String string, Language lng) {
            return work;
        }

        @Override
        public String getEntryName() {
            return "entry";
        }

        @Override
        public String getLexiconName() {
            return "lexicon";
        }

        
        
        @Override
        public LemonElementNamer namer() {
            return LemonElementNamer.defaultForURL("http://www.example.com/");
        }

        

        @Override
        public void report(ActorGenerationReport report) {
            System.err.println(report.toString());
        }
        
    }
    
    private static class OP implements ObjectProperty {
        private final URI uri;

        public OP(URI uri) {
            this.uri = uri;
        }

        @Override
        public URI getURI() {
            return uri;
        }

        @Override
        public Collection<Class> getRange() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<ObjectProperty> getInverseOf() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addInverseOf(ObjectProperty op) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeInverseOf(ObjectProperty op) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<Class> getDomain() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addDomain(Class type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeDomain(Class type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addRange(ClassOrDatatype cod) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeRange(ClassOrDatatype cod) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<Property> getEquivalentProperty() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addEquivalentProperty(Property prprt) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeEquivalentProperty(Property prprt) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<Property> getSubPropertyOf() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addSubPropertyOf(Property prprt) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeSubPropertyOf(Property prprt) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<Property> getSuperPropertyOf() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public String getID() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Map<AnnotationProperty, Collection<AnnotationValue>> getAnnotations() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<AnnotationValue> getAnnotationValues(AnnotationProperty ap) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addAnnotation(AnnotationProperty ap, AnnotationValue av) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeAnnotation(AnnotationProperty ap, AnnotationValue av) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Ontology getOntology() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<Entity> getPuns() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        
    }
    
    private static class Cons implements Constituent {
        private final String tag;

        public Cons(String tag) {
            this.tag = tag;
        }

        
        
        @Override
        public Map<eu.monnetproject.lemon.model.Property, Collection<PropertyValue>> getPropertys() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<PropertyValue> getProperty(eu.monnetproject.lemon.model.Property prprt) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addProperty(eu.monnetproject.lemon.model.Property prprt, PropertyValue pv) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeProperty(eu.monnetproject.lemon.model.Property prprt, PropertyValue pv) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getID() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<URI> getTypes() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addType(URI uri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeType(URI uri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public URI getURI() {
            return URI.create("file:tag/"+tag);
        }

        @Override
        public Map<URI, Collection<Object>> getAnnotations() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<Object> getAnnotations(URI uri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addAnnotation(URI uri, Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeAnnotation(URI uri, Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
