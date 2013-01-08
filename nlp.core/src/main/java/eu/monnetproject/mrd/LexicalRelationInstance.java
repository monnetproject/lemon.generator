package eu.monnetproject.mrd;

/**
 * An instance of a given lexical relationship between two elements
 * @author John McCrae
 */
public interface LexicalRelationInstance {
    /**
     * Get the subject of this triple
     */
    Sense getSubject();

    /**
     * Get the relation predicate of this triple
     */
    LexicalRelation getRelation();

    /**
     * Get the object of this triple
     */
    Sense getObject();
}
