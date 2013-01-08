package eu.monnetproject.osgi;

/**
 * A requst for a service from an embedded OSGi framework
 *
 * @author John McCrae
 */
public class ServiceRequest<ServiceInterface> {
	/** The class that the requested service must implement */
	public final Class<ServiceInterface> clazz;
	/** The arity of the service 
	 * @see ServiceArity
	 */
	public final ServiceArity serviceArity;
	/** A target string filtering on service properties. Target strings are of the format: prop1=val1,prop2=val2 */
	public final String target;
	
	/** Create a service request for a single service of the given type */
	public ServiceRequest(Class<ServiceInterface> clazz) {
		this.clazz = clazz;
		this.serviceArity = ServiceArity.single;
		this.target = null;
	}
	
	/** Create a service request for a service of the given type and arity */
	public ServiceRequest(Class<ServiceInterface> clazz, ServiceArity serviceArity) {
		this.clazz = clazz;
		this.serviceArity = serviceArity;
		this.target = null;
	}
	
	/** Create a service request for a service of the given type, arity and matching some target string */
	public ServiceRequest(Class<ServiceInterface> clazz, ServiceArity serviceArity, String target) {
		this.clazz = clazz;
		this.serviceArity = serviceArity;
		this.target = target;
	}
	
	/** Create a service request for a single service of the given type and matching some target string */
	public ServiceRequest(Class<ServiceInterface> clazz, String target) {
		this.clazz = clazz;
		this.serviceArity = ServiceArity.single;
		this.target = target;
	}
}
