package eu.monnetproject.mrd;

import eu.monnetproject.lang.Language;
import java.util.Collection;

/**
 * A sense that is attached to a generated lexical result.
 *
 * @author John McCrae
 */
public interface Sense {
    /**
     * Get any known lexical relations
     * @return A set of instances, empty if no relations exist
     */
    Collection<LexicalRelationInstance> getRelations(LexicalRelation relation);

    /**
     * Get a sting definition of this sense in a given language
     * @param language The language of the definition
     * @return Get the defintion of this sense, or null if no definition exists for this language
     */
    String getDefinition(Language language);

    /**
     * Get the set of languages for which defintions exist
     * @return The set of languages for which definitions exist, or an empty collection if no definitions are known
     */
    Collection<Language> getDefinitionLanguages();

    /**
     * Get the entry that contains this sense
     * @return The entry
     */
    MRDEntry getEntry();
}
