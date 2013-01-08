package eu.monnetproject.doc;

import java.io.File;
import java.util.List;

import eu.monnetproject.lang.Language;

public interface DocumentExtractor {

	public Document getDocument(File file,Language lang);
	public List<Document> getDocuments(List<File> files,Language lang);
	
}
