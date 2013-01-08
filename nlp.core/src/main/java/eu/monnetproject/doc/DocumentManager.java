package eu.monnetproject.doc;

import java.io.File;
import java.util.List;

import eu.monnetproject.lang.Language;

public interface DocumentManager {

	public void addFile(File file) throws FormatNotSupportedException;
	public void addFiles(List<File> files) throws FormatNotSupportedException;
	public List<Document> getDocuments();
	public void addDocument(Document document) throws FormatNotSupportedException;
	public void addDocuments(List<Document> documents) throws FormatNotSupportedException;
	
	/**
	 * Returns all document of a specific language
	 * 
	 * @param lang
	 * @return
	 */
	public List<Document> getDocumentsInLang(Language lang);
	
	/**
	 * Returns all documents which have a specific field
	 * @param field
	 * @return
	 */
	public List<Document> getDocumentsWithField(String field);
	
	/**
	 * Return all documents of a specific type
	 * @param type
	 * @return
	 */
	public List<Document> getDocumentsOfType(DocumentType type);
	
	/**
	 * Return all documents of a specific name
	 * 
	 * Note: This should only return one document.
	 * @param name
	 * @return
	 */
	public List<Document> getDocumentsWithName(String name);
	
}
