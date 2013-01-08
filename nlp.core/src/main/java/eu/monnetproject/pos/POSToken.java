package eu.monnetproject.pos;

import eu.monnetproject.tokens.Token;

/**
 * A part-of-speech tagged token
 * @author John McCrae
 */
public interface POSToken extends Token {
    /**
     * The lemmatized form of the token
     * @return The lemma
     * @deprecated Not all POS-taggers return lemmatized forms: use a Stemmer instead 
     */
     @Deprecated
    String getLemma();
    /**
     * The part-of-speech tag
     * @return The part of speech tag
     */
    POSTag getPOSTag();
    /**
     * A probability associated with this part-of-speech tagging
     * @return The log probability of the part-of-speech tagging (i.e., a value <= 0)
     */
    //double getProbability();
    
}
