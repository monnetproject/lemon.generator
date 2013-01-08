package eu.monnetproject.stemmer.snowball;

import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.Stemmer;
import eu.monnetproject.morph.StemmerFactory;

/**
 *
 * @author John McCrae
 */
public class SnowballStemmerFactory implements StemmerFactory {

    public Stemmer getStemmer(Language lng) {
        if(lng.equals(Language.DANISH)) {
            return new DanishSnowballStemmer();
        } else if(lng.equals(Language.DUTCH)) {
            return new DutchSnowballStemmer();
        } else if(lng.equals(Language.FINNISH)) {
            return new FinnishSnowballStemmer();
        } else if(lng.equals(Language.HUNGARIAN)) {
            return new HungarianSnowballStemmer();
        } else if(lng.equals(Language.ITALIAN)) {
            return new ItalianSnowballStemmer();
        } else if(lng.equals(Language.NORWEGIAN)) {
            return new NorwegianSnowballStemmer();
        } else if(lng.equals(Language.PORTUGUESE)) {
            return new PortugueseSnowballStemmer();
        } else if(lng.equals(Language.ROMANIAN)) {
            return new RomanianSnowballStemmer();
        } else if(lng.equals(Language.RUSSIAN)) {
            return new RussianSnowballStemmer();
        } else if(lng.equals(Language.SPANISH)) {
            return new SpanishSnowballStemmer();
        } else if(lng.equals(Language.SWEDISH)) {
            return new SwedishSnowballStemmer();
        } else if(lng.equals(Language.TURKISH)) {
            return new TurkishSnowballStemmer();
        } else {
            return null;
        }
    }
}
