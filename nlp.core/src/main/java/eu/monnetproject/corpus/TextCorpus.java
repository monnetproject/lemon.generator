package eu.monnetproject.corpus;

import eu.monnetproject.doc.*;
import eu.monnetproject.lang.Language;
import java.net.*;

/**
 * A simple corpus consisting of immutable text documents
 * @author John McCrae
 */
public interface TextCorpus {
	/** Get all the documents in the corpus */
	Iterable<TextDocument> getDocuments();
	/** Get the URL that gives the location of the corpus */
	URL	getURL(); 
}
