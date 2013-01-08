package eu.monnetproject.pos;

import java.util.Collection;

/**
 * A single part-of-spech tag with syntactic properties,
 * @author John McCrae
 */
public interface POSTag {

    /**
     * Get the part-of-speech
     * @return
     */
    POS getPOS();

    /**
     * Get the set of syntactic properties
     * @return
     */
    Collection<SynPair> getSynProps();
}
