package eu.monnetproject.mrd;

import eu.monnetproject.lang.Language;
import eu.monnetproject.pos.POS;
import java.util.Collection;

/**
 * Interface to a machine readable dictionary. This should at least define a set of
 * words with known written form, language and part-of-speech. It may also
 * contain multiple senses, synsets, lexico-semantic relations and translations.
 * @author John McCrae
 */
public interface MachineReadableDictionary {
    /**
     * Get all entries of a particular word in a given language. It is dependent
     * on the MRD implementation if this is case-sensitive.
     * @param wordForm The form
     * @param lang The language
     * @return The set of entries in this dictionary
     */
    Collection<MRDEntry> getEntries(String wordForm, Language lang);
    /**
     * Get all entries of a particular word in a given language, with part-of-speech information. It is dependent
     * on the MRD implementation if this is case-sensitive.
     * @param wordForm The form
     * @param lang The language
     * @param partOfSpeech The part of speech
     * @return The set of entries in this dictionary
     */
    Collection<MRDEntry> getEntries(String wordForm, Language lang, POS partOfSpeech);
    /**
     * Get all lexico-semantic relations in the dictionary
     * @return The set of lexical-semantic relations, empty set if no lexical relations are in
     * the dictionary
     */
    Collection<LexicalRelation> getSupportedRelations();
}
