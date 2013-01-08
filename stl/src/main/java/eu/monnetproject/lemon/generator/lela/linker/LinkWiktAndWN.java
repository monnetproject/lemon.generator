package eu.monnetproject.lemon.generator.lela.linker;

import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.framework.services.Services;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.util.Logging;
import eu.monnetproject.util.Logger;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Collections;

/**
 *
 * @author John McCrae
 */
public class LinkWiktAndWN {

    private final static Logger log = Logging.getLogger(LinkWiktAndWN.class);
    
    public static void main(String[] args) throws Exception {
        final LemonSerializer serializer = LemonSerializer.newInstance();
        log.info(serializer.getClass().toString());
        final LinguisticOntology lingOnto = Services.get(LinguisticOntology.class);
        final LemonModel wnModel = LemonModels.sparqlEndpoint(new URL("http://localhost:8080/sparql"), Collections.singleton(URI.create("http://monnetproject.deri.ie/lemonsourcepublic")), lingOnto);
        //final LemonModel wiktModel = serializer.create();
        //final LemonModel wnModel = serializer.create();
       // log.info(wiktModel.getClass().getName());
        //log.info(wiktModel.getLexica().isEmpty() ? "empty" : "not empty");
        //assert(!wiktModel.getLexica().isEmpty());
        assert(!wnModel.getLexica().isEmpty());
        //Lexicon wiktLex = null;
        //for(Lexicon lex : wiktModel.getLexica()) {
         //   log.info(lex.getURI().toString());
         //   if(lex.getURI().toString().equals("http://monnetproject.deri.ie/lemonsource/wiktionary/wiktionary__lexicon__en")) {
          //      wiktLex = lex;
          //  }
        //}
       // assert(wiktLex != null);
        Lexicon wnLex = null;
        for(Lexicon lex : wnModel.getLexica()) {
            if(lex.getURI().toString().equals("http://monnetproject.deri.ie/lemonsource/wordnet")) {
                wnLex = lex;
            } 
            System.err.println(lex.getURI());
        }
        if(wnLex == null) {
            throw new RuntimeException();
        }
        final LemonDictionaryLinker lemonDictionaryLinker = new LemonDictionaryLinker();
       // lemonDictionaryLinker.link(wnLex, wiktLex, lingOnto);
        lemonDictionaryLinker.linkToWikt(wnLex, new File("wiktionary__en/"), lingOnto);
    }
}
