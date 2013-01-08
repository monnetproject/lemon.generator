package eu.monnetproject.corpus;

import eu.monnetproject.lang.Language;

public interface Result {

	public Language getLang();
	public CorpusObjectType getType();
	public String getCorpusObjectID();
	
}
