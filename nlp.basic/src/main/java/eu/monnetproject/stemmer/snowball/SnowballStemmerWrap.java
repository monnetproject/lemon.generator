package eu.monnetproject.stemmer.snowball;

import eu.monnetproject.morph.*;
import eu.monnetproject.pos.POSTag;
import eu.monnetproject.pos.SynPair;
import eu.monnetproject.pos.POSToken;
import java.util.*;
import org.tartarus.snowball.SnowballProgram;

public abstract class SnowballStemmerWrap implements Stemmer {
	private final SnowballProgram stemmer;
	
	public SnowballStemmerWrap(SnowballProgram stemmer) {
		this.stemmer = stemmer;
	}
	
	@Deprecated
    public StemmerResult stem(String word, POSTag tag) {
    	synchronized(stemmer) {
    		stemmer.setCurrent(word);
    		if(stemmer.stem()) {
    			return new SnowballStemmerResult(stemmer.getCurrent());
    		} else {
    			return null;
    		}
    	}
    }		
    
    public StemmerResult stem(POSToken token) {
    	return stem(token.getValue(),token.getPOSTag());
    }
    
    private static class SnowballStemmerResult implements StemmerResult {
    	private final String lemma;
    	
    	public SnowballStemmerResult(String lemma) {
    		this.lemma = lemma;
    	}
    	
		public String getStem() { return lemma; }
		public String getLemma() { return lemma; }
		public Collection<SynPair> getSyntacticProperties() { return Collections.EMPTY_LIST; }
		@Override public String toString() { return lemma; }
    }
}
