package eu.monnetproject.corpus.annotation;

import eu.monnetproject.lang.Language;

public interface TextAnnotation extends Annotation {

	public Integer getTextStartPosition();
	public Integer getTextEndPosition();
	public String getAnnotatedValue();
	public Language getLanguage();

}
