package eu.monnetproject.tokenizer.stanford;

import aQute.bnd.annotation.component.Component;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import eu.monnetproject.lang.Script;
import eu.monnetproject.tokens.Token;
import eu.monnetproject.tokenizer.Tokenizer;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author John McCrae
 */
@Component(provide=Tokenizer.class)
public class StanfordTokenizer implements Tokenizer {

    public Script getScript() {
        return Script.LATIN;
    }

    public List<Token> tokenize(String string) {
        PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(new StringReader(string));
        List<Token> rval = new LinkedList<Token>();
        while (tokenizer.hasNext()) {
            Word w = tokenizer.next();
            rval.add(new StanfordToken(w.value()));
        }
        return rval;
    }

    private static class StanfordToken implements Token {

        private final String value;

        public StanfordToken(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
