package eu.monnetproject.tagger.stanford;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import eu.monnetproject.pos.POSTag;
import eu.monnetproject.pos.SynPair;
import eu.monnetproject.pos.POSToken;
import edu.stanford.nlp.ling.*;
import java.util.*;

@Component(provide=Stemmer.class,properties="language=en")
public class StanfordEnglishLemmatizer implements Stemmer {
	
	@Deprecated
    public StemmerResult stem(String word, POSTag tag) {
    	//System.out.println("Stanford Stemmer:" + word +"/"+tag.getPOS().getValue());
    	return new StanfordStemmerResult(new WordLemmaTag(word,tag.getPOS().getValue()).lemma());
    }
    
    public Language getLanguage() { return Language.ENGLISH; }
    
    public StemmerResult stem(POSToken token) {
    	//System.out.println("Stanford Stemmer:" + token.getValue() +"/"+token.getPOSTag().getPOS().getValue());
    	return new StanfordStemmerResult(new WordLemmaTag(token.getValue(),token.getPOSTag().getPOS().getValue()).lemma());
    }
    
    private static class StanfordStemmerResult implements StemmerResult {
    	String lemma;
    	
    	public StanfordStemmerResult(String lemma) {
    		this.lemma = lemma;
    	}
    	
    	public String getStem() {
    		return lemma;
    	}
    
    	public String getLemma() {
    		return lemma;
    	}
    	
    	public Collection<SynPair> getSyntacticProperties() {
    		return Collections.EMPTY_LIST;
    	}
    }
}
