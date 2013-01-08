package eu.monnetproject.parser.stanford;

import java.util.*;
import edu.stanford.nlp.trees.*;
import eu.monnetproject.pos.*;

public class StanfordPOSSet extends AbstractCollection<POS> implements POSSet {
	
	private final HashMap<String,POS> underlying = new HashMap<String,POS>();
	private final TreebankLanguagePack tlp;
	
	public StanfordPOSSet(TreebankLanguagePack tlp) {
		super();
		this.tlp = tlp;
		/*for(int i = 0; i < tags.getSize(); i++) {
			underlying.put(tags.getTag(i),new StanfordPOS(tags.getTag(i),this));
		}*/
	}
	
	public String getPOSSetID() { return tlp.getClass().getName(); }
		
	public POS getPOS(String value) {
		if(!underlying.containsKey(value)) {
			underlying.put(value,new StanfordPOS(value,this));
		}
		return underlying.get(value);
	}
	
	public int size() { throw new IllegalArgumentException("Sorry, stanford tagger won't say how many tags it has"); }
	
	public boolean isEmpty() { throw new IllegalArgumentException("Sorry, stanford tagger won't say how many tags it has"); }
	
	public Iterator<POS> iterator() { return underlying.values().iterator(); }
}
