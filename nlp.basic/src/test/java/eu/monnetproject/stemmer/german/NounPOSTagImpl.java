package eu.monnetproject.stemmer.german;

import java.util.Collection;

import eu.monnetproject.pos.POS;
import eu.monnetproject.pos.POSTag;
import eu.monnetproject.pos.SynPair;

public class NounPOSTagImpl implements POSTag {

	@Override
	public POS getPOS() {
		return new Noun();
	}

	@Override
	public Collection<SynPair> getSynProps() {
		// TODO Auto-generated method stub
		return null;
	}

}
