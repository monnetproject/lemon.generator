package eu.monnetproject.pos;

import java.util.Collection;

/**
 * A fixed (ideally immutable) set of part-of-speech tags that can be used with taggers and other tools
 */
public interface POSSet extends Collection<POS> {
	/**
	 * Get an ID that identifies this POS set, e.g., PennTreebank
	 */
	public String getPOSSetID();
}
	
