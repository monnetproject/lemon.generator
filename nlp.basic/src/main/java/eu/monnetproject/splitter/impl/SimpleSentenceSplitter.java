package eu.monnetproject.splitter.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.monnetproject.sentence.Sentence;
import eu.monnetproject.sentence.SentenceSplitter;
import eu.monnetproject.tokens.Token;
import eu.monnetproject.tokenizer.Tokenizer;

@Component(provide=SentenceSplitter.class)
public class SimpleSentenceSplitter implements SentenceSplitter {

	private static final String[] SENTENCE_DELIMITER_SET = {".","?","!"};
	Set<String> delimiters = new HashSet<String>();
	private Tokenizer tokenizer;

	@Reference
	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	public SimpleSentenceSplitter() {
		super();
		init();
	}

	private void init() {
		String[] delims = SENTENCE_DELIMITER_SET;
		for (int i = 0; i < delims.length; i++) {
			delimiters.add(delims[i]);
		}
	}

	/**
	 * Splits a sentence on any delimiter
	 * 
	 * 
	 */
	@Override
	public List<Sentence> split(String text) {
		// tokenizer text
		List<Token> tokens = tokenizer.tokenize(text);
		return split(tokens);
	}

	public List<Sentence> split(List<Token> tokens) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		List<Token> sentenceTokens = new LinkedList<Token>();
		for(Token token:tokens) {
			String value = token.getValue();
			if (!value.equals("")) {
				sentenceTokens.add(token);
				String lastChar = value.substring(value.length()-1);
				if ( delimiters.contains(lastChar) ) {
					sentences.add(SentenceFactory.makeSentence(sentenceTokens));
					sentenceTokens = new LinkedList<Token>();
				}
			}
		}
		if (!sentenceTokens.isEmpty()) {
			sentences.add(SentenceFactory.makeSentence(sentenceTokens));
		}
		return sentences;
	}

}
