package eu.monnetproject.doc;

import eu.monnetproject.rule.Rule;

/**
 * Removes clutter from text to improve further NLP steps (sentence splitter, tokenizer)
 * 
 * @author Tobias Wunner
 *
 */
public interface TextCleaner {
	
	public String clean(String text);
	//public void addRule(Rule rule);
	//public void removeTables(boolean flag);

}
