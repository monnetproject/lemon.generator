/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.generator;

import eu.monnetproject.framework.services.Services;
import eu.monnetproject.label.LabelExtractorFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.generator.lela.LeLAManager;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.stl.ReplacementRule;
import eu.monnetproject.lemon.stl.TermReplacementActor;
import eu.monnetproject.ontology.Ontology;
import eu.monnetproject.ontology.OntologyFactory;
import eu.monnetproject.ontology.OntologySerializer;
import java.net.URI;
import java.util.LinkedList;
import org.junit.Test;

/**
 *
 * @author tobwun
 */
public class TestReplacementActor {
    @Test
    public void doTest() throws Exception  {
        
        // read ontology
        OntologySerializer serializer = Services.get(OntologySerializer.class);
        Ontology ontology = serializer.create(URI.create("http://www.monnet-project.eu/ontology1"));
        OntologyFactory factory = ontology.getFactory();
        ontology.addClass(factory.makeClass(URI.create("http://www.monnet-project.eu/ontology1#assets+%5BTOREPLACE%5D")));
        ontology.addClass(factory.makeClass(URI.create("http://www.monnet-project.eu/ontology1#profit+(loss)+other")));
        
        // init actor with rules
        TermReplacementActor termReplacementActor = new TermReplacementActor();
        termReplacementActor.addRule(new ReplacementRule("^.*( \\[.*\\])$", 1,null,true, false));
        termReplacementActor.addRule(new ReplacementRule("^[a-z]+( \\(.*\\)).*$", 1,-1,true, false));
        termReplacementActor.addRule(new ReplacementRule("^([a-z]+ \\((.*)\\)).*", 1,2,false, false));
        termReplacementActor.addRule(new ReplacementRule("^.*( \\[.*\\])$", 1,null,true, true)); // update form not recommended !!
        
        // run lela with term replacement actor
        LinkedList<GeneratorActor> actors = new LinkedList<GeneratorActor>();
        actors.add(termReplacementActor);
        final LeLAManager lelaManager = new LeLAManager(actors,Services.get(LabelExtractorFactory.class));
        final LemonModel model = lelaManager.doGeneration(ontology, new LemonGeneratorConfig());
       
        // print
        System.out.println("\n\nLemon model");
        for(Lexicon l:model.getLexica())
            for(LexicalEntry entry:l.getEntrys()) {
                System.out.println(entry.getURI());
                for(LexicalForm form:entry.getForms())
                    System.out.println(" form->"+form.getWrittenRep().toString());
                for(LexicalSense sense:entry.getSenses())
                    System.out.println(" sense->"+sense.getReference());
            }
    }
}
