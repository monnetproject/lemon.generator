package eu.monnetproject.stemmer.german;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.monnetproject.morph.StemmerResult;


public class TestGermanStemmer {

	
	private static final String GERMAN_COMPOUND_NOUN = "Autobahnkreuz";
	private static final String[] GERMAN_COMPOUND_NOUN_SPLIT_LEMMAS = {"autobahn","kreuz"};
	
	@Test
	public void TestSplit_word_pos() {		
		GermanStemmer instance = new GermanStemmer();
		String noun2split = GERMAN_COMPOUND_NOUN;
		String[] expectedResults = GERMAN_COMPOUND_NOUN_SPLIT_LEMMAS;
		List<StemmerResult> results = instance.split(noun2split, "N");
		int idx = 0;
		for(StemmerResult result:results) {
			String expected = expectedResults[idx];
			String actual = result.getLemma();
			Assert.assertEquals(expected, actual);
			idx++;
		}
	}
	
	@Test
	public void TestSplit_postoken() {		
		GermanStemmer instance = new GermanStemmer();
		String noun2split = GERMAN_COMPOUND_NOUN;
		String[] expectedResults = GERMAN_COMPOUND_NOUN_SPLIT_LEMMAS;
		List<StemmerResult> results = instance.split(new NounPOSToken(noun2split, noun2split));
		int idx = 0;
		for(StemmerResult result:results) {
			String expected = expectedResults[idx];
			String actual = result.getLemma();
			Assert.assertEquals(expected, actual);
			idx++;
		}
	}
	
}
