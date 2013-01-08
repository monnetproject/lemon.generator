package eu.monnetproject.lemon.stl.semantic;

import java.net.URI;

public class SKOS {

	private static final String PREFIX = "http://www.w3.org/2004/02/skos/core#";
	
	public static final URI narrower = URI.create(PREFIX+"narrower");
	public static final URI broader = URI.create(PREFIX+"broader");
	
}
