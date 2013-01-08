/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.generator.lela;

import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.generator.ActorGenerationReport;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.LemonElementNamer;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.ontology.Entity;
import java.net.URI;
import net.lexinfo.LexInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author jmccrae
 */
public class MergeActorTest {
    
    public MergeActorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of perform method, of class MergeActor.
     */
    @Test
    public void testPerform() {
        System.out.println("perform");
        final LemonSerializer serializer = LemonSerializer.newInstance();
        final LemonModel model = serializer.create();
        final Lexicon lexicon = model.addLexicon(URI.create("http://www.example.com/lexicon#test"), "en");
        final LexicalEntry work = LemonModels.addEntryToLexicon(lexicon, URI.create("http://www.example.com/lexicon#work"), "work", null);
        
        final LexicalEntry work2 = LemonModels.addEntryToLexicon(lexicon, URI.create("http://www.example.com/lexicon#work2"), "work", null);
        assertEquals(2, lexicon.getEntrys().size());
        LexicalEntry entry = work2;
        GenerationState state = new State(lexicon, model, work);
        MergeActor instance = new MergeActor();
        instance.perform(entry, state);
        assertEquals(1, lexicon.getEntrys().size());
    }
    
    
    private static class State implements GenerationState {
        private final Lexicon lexicon;
        private final LemonModel model;
        private final LinguisticOntology lingOnto = new LexInfo();
        private final LexicalEntry work;

        public State(Lexicon lexicon, LemonModel model, LexicalEntry work) {
            this.lexicon = lexicon;
            this.model = model;
            this.work = work;
        }

        
        @Override
        public Entity getEntity() {
            return null;
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
}
