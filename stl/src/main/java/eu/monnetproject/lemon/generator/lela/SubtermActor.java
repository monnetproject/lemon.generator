/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.generator.lela;

import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.stl.term.SubtermBruteForce;
import eu.monnetproject.util.Logger;
import eu.monnetproject.util.Logging;
import java.net.URI;
import java.util.*;

/**
 *
 * @author tobwun
 */
public class SubtermActor implements GeneratorActor {
    
    private Logger log = Logging.getLogger(this);
    //ViterbiAnalyzer analyzer = new ViterbiAnalyzer();
    SubtermBruteForce analyzer = new SubtermBruteForce();
    public Map<String,URI> urimap = new HashMap<String,URI>();
    Boolean removeDecompositions = true;
    Boolean ignoreCase = true;
    public void addTerm(String term,URI lexicalEntryURI) {
        analyzer.addTerm(term);
        urimap.put(term, lexicalEntryURI);
    }
    //public void init(LemonModel model, Boolean removeDecompositions, Double minprob) {
    public void init(LemonModel model, Boolean removeDecompositions, Boolean ignoreCase) {
        //if (minprob!=null)
        //    analyzer.setMinProp(minprob);
        this.removeDecompositions = removeDecompositions;
        addTerm(" ", null);
        for(Lexicon lexicon:model.getLexica()) {
            for(LexicalEntry le:lexicon.getEntrys()) {
                LexicalForm lf = le.getForms().iterator().next();
                String writtenrep = lf.getWrittenRep().value;
                if (ignoreCase)
                    writtenrep = writtenrep.toLowerCase();
                //log.info("ADD "+writtenrep);
                addTerm(writtenrep, le.getURI());
            }
        }
    }
    
    @Override
    public void perform(LexicalEntry entry, GenerationState state) {
        Collection<LexicalForm> forms = entry.getForms();
        for(LexicalForm lf:forms) {
            String writtenrep = lf.getWrittenRep().value;
            if (ignoreCase)
                writtenrep = writtenrep.toLowerCase();
            //log.info("analyze "+entry.getURI()+" writtenRep="+writtenrep);
            if (writtenrep.equals("tangible fixed assets"))
                System.out.println(writtenrep);
            List<String> stringDecomposition = analyzer.decomposeBest(writtenrep);
            //log.info("DEBUG "+stringDecomposition+" -- "+urimap.keySet());
            if (stringDecomposition.size()>1) {
                //log.info("decomposition "+stringDecomposition);
                LemonFactory factory = state.getModel().getFactory();
                List<Component> decomposition = new LinkedList<Component>();
                for(String stringComponent:stringDecomposition) {
                    URI leURI = urimap.get(stringComponent);
                    if (leURI!=null) {
                        LexicalEntry componentEntry = factory.makeLexicalEntry(leURI);
                        Component component = factory.makeComponent();
                        component.setElement(componentEntry);
                        //components.add(componentEntry);
                        //log.info("add component "+leURI);
                        decomposition.add(component);
                    }
                }
                // remove
                if (removeDecompositions)
                    for(List<Component> decomp:entry.getDecompositions())
                        entry.removeDecomposition(decomp);
                if (writtenrep.equals("tangible fixed assets"))
                    System.out.println(writtenrep);
                entry.addDecomposition(decomposition);
            }
            break; // add only decomposition for one form
        }
    }
    
    @Override
    public double getPriority() {
        return 20;
    }

}
