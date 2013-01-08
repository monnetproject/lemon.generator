package eu.monnetproject.stemmer.german;

import eu.monnetproject.pos.POSTag;
import eu.monnetproject.pos.POSToken;

public class NounPOSToken implements POSToken{

	String value;
	String lemma;
	POSTag postag = new NounPOSTagImpl();
	
	public NounPOSToken(String value, String lemma) {
		super();
		this.value = value;
		this.lemma = lemma;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getLemma() {
		return lemma;
	}

	@Override
	public POSTag getPOSTag() {
		return postag;
	}

}
