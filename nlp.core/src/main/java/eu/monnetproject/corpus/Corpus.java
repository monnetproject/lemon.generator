package eu.monnetproject.corpus;

import java.util.List;

import eu.monnetproject.corpus.annotation.Annotation;
import eu.monnetproject.corpus.annotation.TextAnnotation;
import eu.monnetproject.doc.Document;
import eu.monnetproject.lang.Language;
import eu.monnetproject.sentence.Sentence;

/**
 * A corpus with different corpus objects (documents, sentences, annotations)
 * 
 * @author Tobias Wunner
 *
 */
public interface Corpus {

	/** 
	 * Add a document corpus object to the corpus
	 *
	 * @param doc
	 */
	public void addDocument(Document doc);
	
	/**
	 * Adds a annotation corpus object to the corpus
	 * 
	 * @param annotation
	 * @param corpusObjectID
	 */
	public void addAnnotation(Annotation annotation);

	/**
	 * Adds a text annotation corpus object to the corpus
	 * 
	 * @param textAnnotation
	 * @param corpusObjectID
	 */
	public void addTextAnnotation(TextAnnotation textAnnotation);
	
	/**
	 * Queries the corpus for different corpus query types
	 * 
	 * @param query
	 * @return
	 * @throws UnimplementedQueryType
	 */
	public Iterable<Result> search( Query query ) throws UnimplementedQueryType, UnsupportedQueryObjectException;

	/**
	 * Returns a corpus object (document, sentence) given its id 
	 * 
	 * @param corpusObjectID
	 * @return
	 */
	public Object getCorpusObject(String corpusObjectID);
	
	/**
	 * Deletes a corpus object given its id
	 * 
	 * @param corpusObjectID
	 */
	public void deleteCorpusObject(String corpusObjectID);
	public void deleteCorpusObjects(List<String> corpusObjectID);
	
	
	public void addSentences(List<Sentence> sentences, String docID, Language docLang);
}
