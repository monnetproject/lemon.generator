package eu.monnetproject.sentence;

import java.util.List;

/**
 * 
 * @author Tobias Wunner
 *
 */
 
public interface SentenceSplitter {

	public List<Sentence> split(String text);
	
}
