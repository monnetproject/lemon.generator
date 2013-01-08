/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.generator;

import eu.monnetproject.framework.services.Services;
import eu.monnetproject.label.LabelExtractorFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.generator.lela.LeLAManager;
import eu.monnetproject.lemon.generator.lela.MergeActor;
import eu.monnetproject.lemon.generator.lela.SubtermActor;
import eu.monnetproject.lemon.generator.lela.TokenizerActor;
import eu.monnetproject.lemon.model.*;
import eu.monnetproject.ontology.Ontology;
import eu.monnetproject.ontology.OntologyFactory;
import eu.monnetproject.ontology.OntologySerializer;
import eu.monnetproject.tokenizer.TokenizerFactory;
import eu.monnetproject.util.ResourceFinder;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author tobwun
 */
public class TestSubtermAnalysisPerformance {

    @Test
    public void test() {
        // Tobi, Unit tests are not for testing performance!
    }
    
   // @org.junit.Test
    public void TestPerformance() throws Exception {
        
        // load ontology
        OntologySerializer serializer = Services.get(OntologySerializer.class);
        Reader reader = ResourceFinder.getResourceAsReader("xebr.rdf");
        final Ontology ontology = serializer.read(reader);
        
        // do standard lemon
        LinkedList<GeneratorActor> actors = new LinkedList<GeneratorActor>();
        actors.add(new TokenizerActor(Services.get(TokenizerFactory.class)));
        actors.add(new MergeActor());
        LabelExtractorFactory lef = Services.get(LabelExtractorFactory.class);
        final LeLAManager lelaManager = new LeLAManager(actors,lef);
        LemonGeneratorConfig cnf = new LemonGeneratorConfig();
        cnf.customLabel = URI.create("http://www.w3.org/2000/01/rdf-schema#label");
        final LemonModel model = lelaManager.doGeneration(ontology, cnf);
        
        //
        LinkedList<GeneratorActor> actors2 = new LinkedList<GeneratorActor>();
        SubtermActor subtermActor = new SubtermActor();
        subtermActor.init(model, true, true);
        actors2.add(subtermActor);
        final LeLAManager lelaManager2 = new LeLAManager(actors2,Services.get(LabelExtractorFactory.class));
        lelaManager2.doGeneration(model, ontology, new LemonGeneratorConfig());
        
        // serialize
        //LemonSerializer lemonSerializer = Services.get(LemonSerializer.class);
        //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out,"UTF-8"));
        //lemonSerializer.write(model, writer);
        //writer.close();
        
        // test decomposition of "Intangible Fixed Assets" into "Intangible"+"Fixed Assets"
        String expected = 
                "unknown:/lexicon__en/Tangible\n" +
                "unknown:/lexicon__en/Fixed+assets\n";
        String actual = "";
        for(Lexicon l:model.getLexica())
            for(LexicalEntry entry:l.getEntrys()) {
                if (entry.getURI().toString().equals("unknown:/lexicon__en/Tangible+fixed+assets")) {
                    for(List<Component> comps:entry.getDecompositions()) {
                       for(Component comp:comps)
                           actual = actual + comp.getElement().getURI() + "\n";
                    }
                }
            }
        //System.out.println(actual);
        Assert.assertEquals(expected, actual);
        
    }

}
