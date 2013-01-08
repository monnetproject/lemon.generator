package eu.monnetproject.tagger.stanford;

import eu.monnetproject.pos.*;
import java.util.*;

public class StanfordPOSTag implements POSTag {
	private final POS pos;
	
	public StanfordPOSTag(POS pos) {
		this.pos = pos;
	}
	
	/*public StanfordPOSTag(String pos) {
		this.pos = StanfordPOSSet.getPOS(pos);
	}*/
	
	public POS getPOS() { return pos; }

    
    public Collection<SynPair> getSynProps() {
    	return Collections.EMPTY_LIST;
    }
}
