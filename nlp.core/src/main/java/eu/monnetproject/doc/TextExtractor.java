package eu.monnetproject.doc;

import java.io.File;

public interface TextExtractor {

	String getText(File file);
	DocumentType getDocumentType();
	
}
