package eu.monnetproject.doc;

import java.util.List;

import eu.monnetproject.sentence.Sentence;

/**
 * Representation of a corpus document object
 * 
 * @author Tobias Wunner
 */
public interface Document extends MutableTextDocument {

	public void addSentence(Sentence sentence);
	public List<Sentence> getSentences();
	public boolean isEmpty();
}
