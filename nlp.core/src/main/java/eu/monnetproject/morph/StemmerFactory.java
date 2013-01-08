package eu.monnetproject.morph;

import eu.monnetproject.lang.Language;

/**
 * A factory for stemmers
 * 
 * @author John McCrae
 */
public interface StemmerFactory {
    Stemmer getStemmer(Language language);
}
