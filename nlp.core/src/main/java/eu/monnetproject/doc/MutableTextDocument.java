package eu.monnetproject.doc;

import java.net.URL;

/**
 * Mutable version of a text document
 * 
 * @author John McCrae
 */
public interface MutableTextDocument extends TextDocument {
	/**
	 * Set the contents of the text document
	 */
	public void setText(String text);
	/**
	 * Set the name of the document
	 */
	public void setName(String name);
	/**
	 * Set the source URL of the document
	 */
	public void setSourceURL(URL sourceURL);
		
}
