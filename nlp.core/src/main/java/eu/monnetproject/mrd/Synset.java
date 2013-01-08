package eu.monnetproject.mrd;

import java.util.Collection;

/**
 * A set of synonyms.
 *
 * @author John McCrae
 */
public interface Synset {
    /**
     * Get the elements that use are included in this synset
     * @return The elements that have this synset
     */
    Collection<MRDEntry> getElements();
    /**
     * Get the sense of this synset (if applicable)
     * @return The sense, or null if no sense exists
     */
    Sense getSense();
    /**
     * The senses of the elements in the synset. It is assumed that each element
     * at least one of its senses is in this list. e.g.,
     *    <code>elementSenses[i] = elements[j].senses[k]</code>  for unique i,j,k
     * @return The collection of senses
     */
    Collection<Sense> getElementSenses();
}
