package eu.monnetproject.parser.stanford;

import eu.monnetproject.pos.*;

class StanfordPOS implements POS {
	private final POSSet posset;
	private final String value;
	
	public StanfordPOS(String value, POSSet posset) {
		this.posset = posset;
		this.value = value;
	}
	
	public String getValue() { return value; }
	
	public POSSet getPOSSet() { return posset; }
	
	@Override
	public String toString() { return value; }
}
