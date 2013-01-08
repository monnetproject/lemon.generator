package eu.monnetproject.tokenizer.latin;

import eu.monnetproject.lang.Script;
import eu.monnetproject.tokenizer.Tokenizer;
import eu.monnetproject.tokenizer.TokenizerFactory;

/**
 *
 * @author jmccrae
 */
public class LatinTokenizerFactory implements TokenizerFactory {

    public Tokenizer getTokenizer(Script script) {
        if(script.equals(Script.LATIN)) {
            return new LatinTokenizerImpl();
        } else {
            return null;
        }
    }
    
}
