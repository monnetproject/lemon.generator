package eu.monnetproject.doc;

import java.net.URL;

import eu.monnetproject.lang.Language;

/**
 * Representation of a immutable text document
 * 
 * @author John McCrae
 */
public interface TextDocument {

	/**
	 * Get the contents of the document
	 */
	public String getText();
	/**
	 * Get the name of the document. If the document does not have a name this 
	 * should be the final part of the URL
	 */
	public String getName();
	/**
	 * Get the language of the document
	 * @return The document or null if the language is not known
	 */
	public Language getLang();
	/**
	 * Get the URL indicating the location of the document
	 */
	public URL getSourceURL();
	
}
