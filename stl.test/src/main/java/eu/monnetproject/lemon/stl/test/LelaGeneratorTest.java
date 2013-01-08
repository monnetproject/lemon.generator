package eu.monnetproject.lemon.stl.test;


import java.io.File;
import java.net.URI;
import java.util.Collection;

import eu.monnetproject.data.FileDataSource;
import eu.monnetproject.framework.services.Services;
import eu.monnetproject.label.LabelExtractorFactory;
import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.generator.lela.LeLAManager;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.ontology.Ontology;
import eu.monnetproject.ontology.OntologySerializer;
import eu.monnetproject.framework.test.TestMonitor;
import eu.monnetproject.framework.test.annotation.TestCase;
import eu.monnetproject.framework.test.annotation.TestSuite;
import eu.monnetproject.lemon.generator.*;
import eu.monnetproject.lemon.generator.*;
import eu.monnetproject.util.Logger;
import eu.monnetproject.util.Logging;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

@TestSuite(label="LelaGeneratorTest")
public class LelaGeneratorTest {
 
    private final List<GeneratorActor> actors;
    private final OntologySerializer serializer;
    private final LabelExtractorFactory lef;

    public LelaGeneratorTest(List<GeneratorActor> actors, OntologySerializer serializer, LabelExtractorFactory lef) {
        this.actors = actors;
        this.serializer = serializer;
        this.lef = lef;
    }
    
    
    
    private final static Logger log = Logging.getLogger(LelaGeneratorTest.class);
    private static final String testOntology = "src/test/resources/junit/FoodOntologySmall.owl";
    
    public Ontology getOntology(File ontologyFile, TestMonitor monitor) {
        // setup repo

        // read ontology file with serializer
        if (!ontologyFile.exists()) {
            ontologyFile = new File("eu.monnetproject.lemon.stl.test/" + testOntology);
            if(!ontologyFile.exists()) {
                monitor.fail(System.getProperty("user.dir") + System.getProperty("file.separator") + "src/test/resources/" + testOntology + " does not exist");
            }
        }
        try {
            return serializer.read(new FileReader(ontologyFile));
        } catch(IOException x) {
            throw new RuntimeException(x);
        }
    }

    @TestCase(label="doGeneration",identifier="doGeneration")
    public void TestDoGeneration(TestMonitor monitor) throws Exception {
        LemonGenerator lela = new LeLAManager(actors, lef);
        Ontology source = getOntology(new File(System.getProperty("user.dir") + System.getProperty("file.separator") + testOntology),monitor);
        final LemonSerializer lemonSerializer = LemonSerializer.newInstance();
        LemonModel lemonModel = lemonSerializer.create(URI.create("http://www.example.com/lexicon"));
        final LemonGeneratorConfig config = new LemonGeneratorConfig();
        config.inferLang = false;
        config.unlanged = Language.ENGLISH;
        lela.doGeneration(lemonModel, source, config);
        final Collection<Lexicon> lexica = lemonModel.getLexica();
        monitor.assertion(1 == lexica.size(), "Lexica size not 1");
        final Lexicon lexicon = lexica.iterator().next();
        System.err.println(lexicon.getEntrys().size());
        boolean dessertWineGenerated = false;
        for(LexicalEntry entry : lexicon.getEntrys()) {
            if(entry.getURI().toString().endsWith("#egg")) {
                System.err.println(entry.getURI());
                monitor.assertion(0 == entry.getSenses().size(),"Not empty senses");
                //assertEquals(0,entry.getSenses().size());
            } else if(entry.getURI().toString().endsWith("wine")) {
                System.err.println(entry.getURI());
                dessertWineGenerated = true;
            }
        }
        if(!dessertWineGenerated) {
            monitor.fail("Subnode not generated");
        }
    }
    
    public static void main(String[] args) throws Exception {
        new LelaGeneratorTest(new LinkedList(Services.getAll(GeneratorActor.class)), Services.get(OntologySerializer.class), Services.get(LabelExtractorFactory.class)).TestDoGeneration(new TestMonitor() {

            @Override
            public void beginTestRun() {
            }

            @Override
            public void beginTestSuite(eu.monnetproject.framework.test.TestSuite suite) {
            }

            @Override
            public void beginTest(eu.monnetproject.framework.test.TestCase testCase) {
            }

            @Override
            public void assertion(boolean condition, String messageOnFailure) {
            }

            @Override
            public void error(String message, Throwable exception) {
            }

            @Override
            public void fail(String message) {
            }

            @Override
            public void endTest(eu.monnetproject.framework.test.TestCase testCase) {
            }

            @Override
            public void endTestSuite(eu.monnetproject.framework.test.TestSuite suite) {
            }

            @Override
            public void endTestRun() {
            }
        });
    }
}
