package eu.monnetproject.sentence;

import java.util.List;

import eu.monnetproject.lang.Language;

/**
 * Representation of a corpus sentence object
 * 
 * @author Tobias Wunner
 */
public interface Sentence {
	
	public String getText();
	public List<Chunk> getChunks();
	public Language getLang();
	
}
