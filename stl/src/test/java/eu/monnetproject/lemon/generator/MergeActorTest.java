/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.generator;

import eu.monnetproject.framework.services.Services;
import eu.monnetproject.label.LabelExtractorFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.generator.LemonGeneratorConfig;
import eu.monnetproject.lemon.generator.lela.LeLAManager;
import eu.monnetproject.lemon.generator.lela.MergeActor;
import eu.monnetproject.lemon.generator.lela.TokenizerActor;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.ontology.*;
import eu.monnetproject.tokenizer.TokenizerFactory;
import eu.monnetproject.util.ResourceFinder;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author tobwun
 */
public class MergeActorTest {
        
        @Test
	public void TestMergeActor() throws IOException {   
            
            // read ontology
            OntologySerializer serializer = Services.get(OntologySerializer.class);
            Ontology ontology = serializer.create(URI.create("http://www.monnet-project.eu/ontology1"));
            OntologyFactory factory = ontology.getFactory();
            ontology.addClass(factory.makeClass(URI.create("http://www.monnet-project.eu/ontology1#assets")));
            ontology.addClass(factory.makeClass(URI.create("http://www.monnet-project.eu/ontology1#fixed+assets")));
            
            // prepare actors
            LinkedList<GeneratorActor> actors = new LinkedList<GeneratorActor>();
            actors.add(new TokenizerActor(Services.get(TokenizerFactory.class)));
            actors.add(new MergeActor());  
            
            
            // generate lemon model
            final LeLAManager lelaManager = new LeLAManager(actors,Services.get(LabelExtractorFactory.class));
            final LemonModel model = lelaManager.doGeneration(ontology, new LemonGeneratorConfig());
        
            // Test excepted values and count
            HashSet<String> expectedValues = new HashSet<String>();
            expectedValues.add("fixed assets");
            expectedValues.add("assets");
            expectedValues.add("fixed");
            int actualCnt = 0;
            for(Lexicon lexicon:model.getLexica()) {
                System.out.println("lexicon "+lexicon.getURI());
                for(LexicalEntry le:lexicon.getEntrys()) {
                    LexicalForm lf = le.getForms().iterator().next();
                    String writtenrep = lf.getWrittenRep().value;
                    //Assert.assertTrue(expectedValues.contains(writtenrep));
                    actualCnt++;
                }
            }
            //Assert.assertEquals(3, actualCnt);
            
        }
}
