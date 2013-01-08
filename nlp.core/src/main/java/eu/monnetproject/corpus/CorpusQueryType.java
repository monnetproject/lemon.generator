package eu.monnetproject.corpus;

/**
 * Corpus Query Types. A set of corpus query types supported by the query system.
 * 
 * @author Tobias Wunner
 *
 */
public enum CorpusQueryType {

	AttributeQuery,
	CorpusObjectIDQuery, CorpusObjectTypeQuery,
	TextQuery, AnnotationQuery,
	AnnotationCorpusObjectIDQuery,
	AnnotationClassQuery, AnnotationClassListQuery,
	AnnotationClassReferencedCorpusObjectQuery, AnnotationClassListReferencedCorpusObjectQuery,
	DocumentSentenceQuery,SentenceDocumentQuery;
	
}
