package eu.monnetproject.morph;

import eu.monnetproject.pos.SynPair;
import java.util.Collection;

/**
 * The result of the stemmer
 * @author John McCrae
 */
public interface StemmerResult {
    /**
     * Get the stem. E.g., for german verb "spielen" this would be "spiel"-
     * @return The stem
     */
    String getStem();
    /**
     * Get the lemma form. E.g., "spielen"
     * @return The lemma
     */
    String getLemma();
    /**
     * Get the set of syntactic properties that inflected the root word
     * @return The set of syntactic properties, or null if this cannot be determined.
     */
    Collection<SynPair> getSyntacticProperties();
}
