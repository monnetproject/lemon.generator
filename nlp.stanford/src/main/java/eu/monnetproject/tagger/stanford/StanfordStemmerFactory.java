package eu.monnetproject.tagger.stanford;

import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.Stemmer;
import eu.monnetproject.morph.StemmerFactory;

/**
 *
 * @author John McCrae
 */
public class StanfordStemmerFactory implements StemmerFactory {

    public Stemmer getStemmer(Language lng) {
        if(lng.equals(Language.ENGLISH)) {
            return new StanfordEnglishLemmatizer();
        } else {
            return null;
        }
    }
    
}
