package eu.monnetproject.sentence;


/**
 * A chunk defines a start and end of the text of a sentence
 * 
 * @author Tobias Wunner
 *
 */
public interface Chunk {

	int getBegin();
	int getEnd();
	
}
