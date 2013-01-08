package eu.monnetproject.tokenizer.stanford;

import eu.monnetproject.lang.Script;
import eu.monnetproject.tokenizer.Tokenizer;
import eu.monnetproject.tokenizer.TokenizerFactory;

/**
 *
 * @author John McCrae
 */
public class StanfordTokenizerFactory implements TokenizerFactory {

    public Tokenizer getTokenizer(Script script) {
        if(script.equals(Script.LATIN)) {
            return new StanfordTokenizer();
        } else {
            return null;
        }
    }
    
}
