package eu.monnetproject.osgi;

/**
 * Indicated that a service was not found in an embedded OSGi instance
 * 
 * @author John McCrae
 */
public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(Throwable thrwbl) {
        super(thrwbl);
    }

    public ServiceNotFoundException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
    
    /**
     * Creates a new instance of <code>ServiceNotFoundException</code> without detail message.
     */
    public ServiceNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ServiceNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ServiceNotFoundException(String msg) {
        super(msg);
    }
}
