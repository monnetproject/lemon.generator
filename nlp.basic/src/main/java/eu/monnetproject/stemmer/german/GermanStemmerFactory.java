package eu.monnetproject.stemmer.german;

import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.Stemmer;
import eu.monnetproject.morph.StemmerFactory;

/**
 *
 * @author John McCrae
 */
public class GermanStemmerFactory implements StemmerFactory {

    public Stemmer getStemmer(Language lng) {
        if(lng.equals(Language.GERMAN)) {
            return new GermanStemmer();
        } else {
            return null;
        }
    }
    
}
