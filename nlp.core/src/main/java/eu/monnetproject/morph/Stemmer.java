package eu.monnetproject.morph;

import eu.monnetproject.lang.Language;
import eu.monnetproject.pos.POSTag;
import eu.monnetproject.pos.POSToken;
/**
 * Models a stemmer
 *
 * @author John McCrae
 */
public interface Stemmer {
    /**
     * Stem a word.
     * @param word The word
     * @param tag The tag assigned to the word
     * @return The result
     * @deprecated Prefer to use simpler declaration
     */
     @Deprecated
    StemmerResult stem(String word, POSTag tag);
    
    /**
     * Stem a word.
     * @param token The pos-tagged token
     * @return The result
     */
     StemmerResult stem(POSToken token);
     
     /**
      * Get the language that this stemmer supports
      */
     Language getLanguage();
}
