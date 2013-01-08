package eu.monnetproject.mrd;

import eu.monnetproject.lang.Language;
import eu.monnetproject.pos.POS;
import java.util.Collection;

/**
 * A single entry in an MRD. This represents a word with several senses, the actual
 * seperation of an entry from a sense is dependent on the structure of the underlying
 * MRD, but the intuition is that words with clear seperate meanings e.g., &quot;bank&quot;
 * should be seperate for &quot;river <u>bank</u>&quot; and &quot;merchant <u>bank</u>&quot;. At
 * the least MRDEntries can have only a single set of syntactic information, namely part-of-speech,
 * grammatical gender, inflectional forms.
 *
 * @author John McCrae
 */
public interface MRDEntry {
    /**
     * Get the sense of this word
     * @return The set of senses, this should be a non-empty set!
     */
    Collection<Sense> getSenses();
    /**
     * Get the known forms of this entry. This may include term variants (e.g., abbreviations) and
     * inflectional variants.
     * @return The word forms.
     */
    Collection<String> getWordForms();
    /**
     * Get the part of speech
     * @return The part of speech
     */
    POS getPartOfSpeech();
    /**
     * Get any synsets attached to this entry
     * @return The synsets or an empty list if the MRD does not support synsets
     */
    Collection<Synset> getSynSets();
    /**
     * The language of the entry
     * @return The language
     */
    Language getLanguage();
}
