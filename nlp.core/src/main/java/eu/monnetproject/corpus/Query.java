package eu.monnetproject.corpus;

import eu.monnetproject.lang.Language;

public interface Query {

	public CorpusObjectType getCorpusObjectType();
	public CorpusQueryType getQueryType();
	public Object getSearchValue();
	public Language getLanguage();
	public String getAttributeName();
	
}
