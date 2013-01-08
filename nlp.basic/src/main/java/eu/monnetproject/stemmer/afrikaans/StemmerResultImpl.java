package eu.monnetproject.stemmer.afrikaans;

import java.util.Collection;
import java.util.Collections;

import eu.monnetproject.morph.StemmerResult;
import eu.monnetproject.pos.SynPair;

public class StemmerResultImpl implements StemmerResult{
	
		private final String stem, lemma;
		
		public StemmerResultImpl(String stem, String lemma) {
			this.stem = stem;
			this.lemma = lemma;
		}
		
		public String getStem() { return stem; }
    
		public String getLemma() { return lemma; }
		
		public Collection<SynPair> getSyntacticProperties() { return Collections.EMPTY_LIST; }
		
		public String toString() {
			if(stem.equals(lemma)) {
				return lemma;
			} else {
				return lemma + " ("+stem+")";
			}
		}
	
}
