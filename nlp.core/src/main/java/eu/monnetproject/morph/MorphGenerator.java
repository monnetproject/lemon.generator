package eu.monnetproject.morph;

import eu.monnetproject.pos.SynPair;
import java.util.Collection;

/**
 * Wraps a morphological generator
 * @author John McCrae
 */
public interface MorphGenerator {
    /**
     * Get a inflected form of a word
     * @param lemma The base lemma
     * @param synProps The set of syntactic properites
     * @return The inflected form
     */
    String getWordForm(String lemma, Collection<SynPair> synProps);
}
