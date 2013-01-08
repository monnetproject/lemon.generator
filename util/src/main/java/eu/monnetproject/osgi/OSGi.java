package eu.monnetproject.osgi;

import org.osgi.framework.Bundle;

/**
 * Aan embedded running OSGi framework
 * @author John McCrae
 */
public interface OSGi {

    /**
     * Get a set of services from the OSGi. Note this method blocks until all services are simultaneously available in the
     * context, if sufficient services never become avaliable this method will throw an exception if the timeout has been reached
     *
     * @param requests An array of object requests
     * @return The objects in the same order as the requests are given, or a java.util.Collection for each multiple binding
     * @throws ServiceNotFoundException If the requests could not be satisfied
     * @see ServiceRequest
     */
    Object[] getRefs(ServiceRequest... requests) throws ServiceNotFoundException;

    /**
     * Get a single service from the embedded OSGi framework. Note this method blocks until the service is available
     * @param request The service request
     * @return The service object
     * @throws ServiceNotFoundException If the service could not be found
     */
    <Service> Service getRef(ServiceRequest<Service> request) throws ServiceNotFoundException;
    
    /**
     * Stop the embedded OSGi framework. Don't forget to call me or your program may never end
     */
    void stop();
    
    /**
     * Set the timeout on getRefs
     * @param milliseconds The number of milliseconds to wait for services to be available or -1 for no timeout, default is 10,000.
     */
    void setTimeout(long milliseconds);
    
    /**
     * Print to the log the set of available services
     */
    void logServices();
    
    /**
     * Print to the log the set of available components and their satisfaction
     * @throws RuntimeException If the classes to inspect components could not be found
     */
    void logComponents();
    
    /**
     * Block the current thread until the framework stops itself
     * @param timeout The maximum time to wait
     */
    void waitForStop(long timeout) throws InterruptedException;
}
