package eu.monnetproject.stemmer.german;

import eu.monnetproject.pos.POS;
import eu.monnetproject.pos.POSSet;

public class Noun implements POS {

	@Override
	public String getValue() {
		return "NN";
	}

	@Override
	public POSSet getPOSSet() {
		// TODO Auto-generated method stub
		return null;
	}

}
