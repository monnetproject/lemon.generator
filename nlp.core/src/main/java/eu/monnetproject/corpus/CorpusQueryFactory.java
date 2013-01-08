package eu.monnetproject.corpus;

import java.net.URI;
import java.util.List;

import eu.monnetproject.lang.Language;

/**
 * Corpus Query Factory. A factory to generate corpus queries.
 * 
 * @author Tobias Wunner
 *
 */
public class CorpusQueryFactory {

	public static Query getCorpusObectTypeQuery(final CorpusObjectType cot) {
		return getCorpusObectTypeQuery(cot, null);
	}

	public static Query getCorpusObectTypeQuery(final CorpusObjectType cot,final Language lang) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return cot;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.CorpusObjectTypeQuery;
			}
			@Override
			public Object getSearchValue() {
				return null;
			}
			@Override
			public Language getLanguage() {
				return lang;
			}
			@Override
			public String getAttributeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public static Query getCorpusObectIDQuery(final String corpusObjectID) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return null;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.CorpusObjectIDQuery;
			}
			@Override
			public Object getSearchValue() {
				return corpusObjectID;
			}
			@Override
			public Language getLanguage() {
				return null;
			}
			@Override
			public String getAttributeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public static Query getTextQuery(final CorpusObjectType cot, final String textQuery,final Language lang) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return cot;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.TextQuery;
			}
			@Override
			public Object getSearchValue() {
				return textQuery;
			}
			@Override
			public Language getLanguage() {
				return lang;
			}
			@Override
			public String getAttributeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	/** 
	 * A query to retrieve all sentences of a document
	 * 
	 * @param documentID
	 * @return
	 */
	public static Query getSentenceDocumentQuery(final String documentID) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return CorpusObjectType.Sentence;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.SentenceDocumentQuery;
			}
			@Override
			public Object getSearchValue() {
				return documentID;
			}
			@Override
			public Language getLanguage() {
				return null;
			}
			@Override
			public String getAttributeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public static Query getAnnotationQuery(final CorpusObjectType cot, final URI annotationURI,final Language lang) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return cot;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.AnnotationQuery;
			}
			@Override
			public Object getSearchValue() {
				return annotationURI;
			}
			@Override
			public Language getLanguage() {
				return lang;
			}
			@Override
			public String getAttributeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	/**
	 * Get all annotation and text annotations of a corpus object given its id
	 * 
	 * @param corpusObjectID
	 * @param lang
	 * @return
	 */
	public static Query getAnnotationCOTIDQuery(final String corpusObjectID,final Language lang) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return null;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.AnnotationCorpusObjectIDQuery;
			}
			@Override
			public Object getSearchValue() {
				return corpusObjectID;
			}
			@Override
			public Language getLanguage() {
				return lang;
			}
			@Override
			public String getAttributeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	/**
	 * Annotation class query. Retrieves all annotation objects of a annotation class uri.
	 * 
	 * @param annotationClassURI
	 * @param lang
	 * @return
	 */
	public static Query getAnnotationClassQuery(final URI annotationClassURI,final Language lang) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return null;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.AnnotationClassQuery;
			}
			@Override
			public Object getSearchValue() {
				return annotationClassURI;
			}
			@Override
			public Language getLanguage() {
				return lang;
			}
			@Override
			public String getAttributeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
	/**
	 * Annotation class query. Retrieves all annotation objects of a list of annotation class uris
	 * 
	 * @param annotationClassURI
	 * @param lang
	 * @return
	 */
	public static Query getAnnotationClassListQuery(final List<URI> annotationClassURIList,final Language lang) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return null;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.AnnotationClassListQuery;
			}
			@Override
			public Object getSearchValue() {
				return annotationClassURIList;
			}
			@Override
			public Language getLanguage() {
				return lang;
			}
			@Override
			public String getAttributeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
	/**
	 * Annotation class query. Retrieves all annotation objects of a list of annotation class uris
	 * 
	 * @param annotationClassURI
	 * @param lang
	 * @return
	 */
	public static Query getAnnotationClassListCorpusObjectQuery(final List<URI> annotationClassURIList,final CorpusObjectType cot, final Language lang) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return cot;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.AnnotationClassListReferencedCorpusObjectQuery;
			}
			@Override
			public Object getSearchValue() {
				return annotationClassURIList;
			}
			@Override
			public Language getLanguage() {
				return lang;
			}
			@Override
			public String getAttributeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
	/**
	 * Annotation class corpus object query. Retrieves all corpus objects with a annotation of a annotation class uri.
	 * 
	 * @param annotationClassURI
	 * @param lang
	 * @return
	 */
	public static Query getAnnotationClassCorpusObjectQuery(final URI annotationClassURI, final CorpusObjectType cot, final Language lang) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return cot;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.AnnotationClassReferencedCorpusObjectQuery;
			}
			@Override
			public Object getSearchValue() {
				return annotationClassURI;
			}
			@Override
			public Language getLanguage() {
				return lang;
			}
			@Override
			public String getAttributeName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public static Query getAttributeQuery(final Object searchValue, final String attributeName,final CorpusObjectType cot, final Language lang) {
		return new Query() {
			@Override
			public CorpusObjectType getCorpusObjectType() {
				return cot;
			}
			@Override
			public CorpusQueryType getQueryType() {
				return CorpusQueryType.AttributeQuery;
			}
			@Override
			public Object getSearchValue() {
				return searchValue;
			}
			@Override
			public Language getLanguage() {
				return lang;
			}
			@Override
			public String getAttributeName() {
				return attributeName;
			}
		};
	}

}
