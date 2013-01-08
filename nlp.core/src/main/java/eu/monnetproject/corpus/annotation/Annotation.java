package eu.monnetproject.corpus.annotation;

import java.net.URI;

import eu.monnetproject.lang.Language;

/** 
 * Corpus object annotation
 * 
 * author: Tobias Wunner
 **/
public interface Annotation {

	public URI getAnnotationClass();
	public String getReferencedCorpusObjectID();
	public Language getLanguage();
	
}
