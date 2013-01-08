
package eu.monnetproject.lemon.generator.lela.categorizer;

/**
 *
 * @author John McCrae
 */
public class CategorizerParseException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>CategorizerParseException</code> without detail message.
     */
    public CategorizerParseException() {
    }

    /**
     * Constructs an instance of <code>CategorizerParseException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CategorizerParseException(String msg) {
        super(msg);
    }
}
