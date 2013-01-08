package eu.monnetproject.tagger.stanford;

import eu.monnetproject.pos.*;
import edu.stanford.nlp.tagger.maxent.TTags;
import java.util.*;

public class StanfordPOSSet extends AbstractCollection<POS> implements POSSet {
	
	private final HashMap<String,POS> underlying = new HashMap<String,POS>();
	
	public StanfordPOSSet(TTags tags) {
		super();
		for(int i = 0; i < tags.getSize(); i++) {
			underlying.put(tags.getTag(i),new StanfordPOS(tags.getTag(i),this));
		}
	}
	
	public String getPOSSetID() { return "Stanford"; }
		
	public POS getPOS(String value) {
		return underlying.get(value);
	}
	
	public int size() { return underlying.size(); }
	
	public boolean isEmpty() { return underlying.isEmpty(); }
	
	public Iterator<POS> iterator() { return underlying.values().iterator(); }
}
