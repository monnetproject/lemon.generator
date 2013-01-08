package eu.monnetproject.splitter.stanford;

import aQute.bnd.annotation.component.Component;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import eu.monnetproject.lang.Language;
import eu.monnetproject.sentence.Chunk;
import eu.monnetproject.sentence.Sentence;
import eu.monnetproject.sentence.SentenceSplitter;
import eu.monnetproject.tokens.Token;
import eu.monnetproject.tokenizer.Tokenizer;
import eu.monnetproject.tokenizer.stanford.StanfordTokenizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author John McCrae
 */
@Component(provide=SentenceSplitter.class)
public class StanfordSplitter implements SentenceSplitter {

    private final Tokenizer tokenizer = new StanfordTokenizer();
    
    public List<Sentence> split(String string) {
        WordToSentenceProcessor wtsp = new WordToSentenceProcessor<String>();
                
        List<Token> tokens = tokenizer.tokenize(string);
        List<String> tokens2 = new ArrayList<String>(tokens.size());
        for(Token tk : tokens) {
            tokens2.add(tk.getValue());
        }
        
        List<List<String>> sents = wtsp.wordsToSentences(tokens2);
        List<Sentence> rval = new ArrayList<Sentence>(sents.size());
        for(List<String> sent : sents) {
            StringBuilder sb  = new StringBuilder();
            for(String w : sent) {
                if(sb.length() >0) {
                    sb.append(" ");
                }
                sb.append(w);
            }
            rval.add(new StanfordSentence(sb.toString()));
        }
        return rval;
    }
    
    
    private static class StanfordSentence implements Sentence {
        private final String text;

        public StanfordSentence(String text) {
            this.text = text;
        }
        
        public List<Chunk> getChunks() {
            return Collections.EMPTY_LIST;
        }

        public Language getLang() {
            return null;
        }

        public String getText() {
            return text;
        }
        
    }
    
}
