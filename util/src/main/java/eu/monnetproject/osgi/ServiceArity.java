package eu.monnetproject.osgi;

/**
 * Enumeration for service arity
 *
 * @Author John McCrae
 */
public enum ServiceArity {
	/** A single instance of a service */
		single,
	/** A collection containing all available instances of the service */
		multiple,
	/** A single optional service */	
		optional,
	/** A possibly empty collection of all available instances of the service */
		optionalMultiple
	}
