package eu.monnetproject.pos;

/**
 * Represents a syntactic property pair.
 *
 * @author John McCrae
 */
public interface SynPair {

    /**
     * Get the name of the property
     * @return The name
     */
    public String getName();

    /**
     * Get the value of the property
     * @return The value
     */
    public String getValue();
}
