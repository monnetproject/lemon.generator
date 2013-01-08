package eu.monnetproject.tagger;

import eu.monnetproject.pos.POSToken;
import java.util.List;
import eu.monnetproject.lang.Language;
import eu.monnetproject.tokens.Token;

/**
 * Interface to a part-of-speech tagger
 * @author John McCrae
 */
public interface Tagger {
    /**
     * Tag a set of token
     * @param tokens The list of tokens
     * @return The list of part-of-speech annotated tokens
     */
    List<POSToken> tag(List<Token> tokens);
    
    /**
     * Get the tag set used by this tagger
     */
    String getTagSet();
    
    /**
     * Get the language this tagger is trained on
     */
    Language getLanguage();
}
