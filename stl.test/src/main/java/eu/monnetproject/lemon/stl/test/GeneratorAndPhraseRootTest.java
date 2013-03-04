package eu.monnetproject.lemon.stl.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import com.beinformed.framework.osgi.osgitest.TestMonitor;
import com.beinformed.framework.osgi.osgitest.annotation.TestCase;
import com.beinformed.framework.osgi.osgitest.annotation.TestSuite;

import eu.monnetproject.label.LabelExtractorFactory;
import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.generator.LemonGenerator;
import eu.monnetproject.lemon.generator.LemonGeneratorConfig;
import eu.monnetproject.lemon.generator.lela.LeLAManager;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.Node;
import eu.monnetproject.ontology.Ontology;
import eu.monnetproject.ontology.OntologySerializer;
import eu.monnetproject.util.Logger;
import eu.monnetproject.util.Logging;

@TestSuite(label = "GeneratorAndPhraseRootTest")
public class GeneratorAndPhraseRootTest {

    private final List<GeneratorActor> actors;
    private final OntologySerializer serializer;
    private final LabelExtractorFactory lef;
    private final static Logger log = Logging.getLogger(GeneratorAndPhraseRootTest.class);
    private static final String testOntology = "src/test/resources/junit/ToyOntologyIFRS.owl";

    public GeneratorAndPhraseRootTest(List<GeneratorActor> actors, OntologySerializer serializer, LabelExtractorFactory lef) {
        this.actors = actors;
        this.serializer = serializer;
        this.lef = lef;
    }
    
    

    public Ontology getOntology(File ontologyFile, TestMonitor monitor) {
        // setup repo

        // read ontology file with serializer
        if (!ontologyFile.exists()) {
            ontologyFile = new File("eu.monnetproject.lemon.stl.test/" + testOntology);
            if(!ontologyFile.exists()) {
                monitor.error(System.getProperty("user.dir") + System.getProperty("file.separator") + "src/test/resources/" + testOntology + " does not exist", null);
            }
        }
        try {
            return serializer.read(new FileReader(ontologyFile));
        } catch(IOException x) {
            throw new RuntimeException(x);
        }
    }

    @TestCase(identifier = "doGeneration", label = "doGeneration")
    public void TestDoGeneration(TestMonitor monitor) throws Exception {
        LemonGenerator lela = new LeLAManager(actors, lef);
        Ontology source = getOntology(new File(System.getProperty("user.dir") + System.getProperty("file.separator") + testOntology), monitor);
        final LemonSerializer lemonSerializer = LemonSerializer.newInstance();
        LemonModel lemonModel = lemonSerializer.create(URI.create("http://www.example.com/lexicon"));
        final LemonGeneratorConfig config = new LemonGeneratorConfig();
        config.inferLang = false;
        config.unlanged = Language.ENGLISH;
        lela.doGeneration(lemonModel, source, config);

        final Collection<Lexicon> lexica = lemonModel.getLexica();
        monitor.assertion(1 == lexica.size(), "Lexica size is not 1");
        final Lexicon lexicon = lexica.iterator().next();
        System.err.println(lexicon.getEntrys().size());

        for (LexicalEntry entry : lexicon.getEntrys()) {
            Collection<Node> phraseRoots = entry.getPhraseRoots();
            if (!entry.getDecompositions().isEmpty()) {
                monitor.assertion(!phraseRoots.isEmpty(), "No phrase roots");
            }
            System.out.println(phraseRoots.size());

            for (Node node : phraseRoots) {
                //Map<Edge,Collection<Node>> edges = node.getEdges();
                System.out.println(node.getID());
                System.out.println(node.getConstituent());
                System.out.println(node.getLeaf());
            }
        }
    }
}
