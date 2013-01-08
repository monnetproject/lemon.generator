package eu.monnetproject.morph;

import eu.monnetproject.pos.POSToken;
import java.util.List;

/**
 * Models a compound word splitter
 *
 * @author John McCrae
 */
public interface CompoundSplitter {
    
    /**
     * Split a word.
     * @param token The pos-tagged token
     * @return The result
     */
     List<StemmerResult> split(POSToken token);
    
}
