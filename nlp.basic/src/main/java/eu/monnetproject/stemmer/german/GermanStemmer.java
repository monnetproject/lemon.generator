package eu.monnetproject.stemmer.german;

import eu.monnetproject.pos.POSToken;
import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import eu.monnetproject.pos.*;
import eu.monnetproject.tagger.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;

@Component(provide={Stemmer.class,CompoundSplitter.class},properties="language=de")
public class GermanStemmer implements Stemmer, CompoundSplitter {
	public HashMap<String,String> perfects = new HashMap<String,String>();
	public HashMap<String,String> praterites = new HashMap<String,String>();
	public HashMap<String,String> thirdPS = new HashMap<String,String>();
	public HashMap<String,String> nouns = new HashMap<String,String>();
	public HashMap<String,String> comps = new HashMap<String,String>();
	public HashMap<String,String> sups = new HashMap<String,String>();
	public HashSet<String> adjs = new HashSet<String>();
	public HashSet<String> nounSings = new HashSet<String>(); 
	public HashSet<String> verbs = new HashSet<String>();
	
	private static final String[] der = { "der","die","das","den","dem","des" };
	
	private static final String resPrefix = "/stemmer.german/eu/monnetproject/stemmer/german/";
	
	public GermanStemmer() { try {
		init();
	} catch(IOException x) { throw new RuntimeException(x); } }
	
	public Language getLanguage() { return Language.GERMAN; }
	
	private void init() throws IOException {
		InputStream stream = GermanStemmer.class.getResourceAsStream(resPrefix+"strong.table");
		if(stream == null) {
			throw new IllegalArgumentException("Could not locate " + resPrefix + "strong.table");
		}
		BufferedReader strong = new BufferedReader(new InputStreamReader(stream));
		String s;
		while((s = strong.readLine()) != null) {
			if(s.matches("\\s*")) continue;
			String[] ss = s.split("\\s+");
			perfects.put(ss[3],ss[0]);
			praterites.put(ss[2],ss[0]);
			thirdPS.put(ss[1],ss[0]);
		}
		BufferedReader nounsIn = new BufferedReader(new InputStreamReader(GermanStemmer.class.getResourceAsStream(resPrefix+"nouns.table")));
		while((s = nounsIn.readLine()) != null) {
			if(s.matches("\\s*")) continue;
			String[] ss = s.split("\\s+");
			nouns.put(ss[1].toLowerCase(),ss[0].toLowerCase());
			nounSings.add(ss[0].toLowerCase());
		}
		BufferedReader verbsIn = new BufferedReader(new InputStreamReader(GermanStemmer.class.getResourceAsStream(resPrefix+"verbs.table")));
		while((s = verbsIn.readLine()) != null) {
			if(s.matches("\\s*")) continue;
			verbs.add(s);
		}
		BufferedReader adjsIn = new BufferedReader(new InputStreamReader(GermanStemmer.class.getResourceAsStream(resPrefix+"adjs.table")));
		while((s = adjsIn.readLine()) != null) {
			if(s.matches("\\s*")) continue;
			String[] ss = s.split("\\s+");
			comps.put(ss[1],ss[0]);
			sups.put(ss[2],ss[0]);
			adjs.add(ss[0]);
		}
	}
	
	public StemmerResult stem(String word, String tag) {
		if(tag.matches("^V.*")) {
			return stemVerb(word.toLowerCase());
		} else if(tag.matches("^N.*")) {
			return stemNoun(word.toLowerCase());
		} else if(tag.matches("^[JA].*") && 
			!tag.matches("^AR.*")) {
			return stemAdjective(word.toLowerCase());
		} else if(tag.matches("^AR.*")) {
			return stemArticle(word.toLowerCase());
		} else {
			return new StemmerResultImpl(word.toLowerCase(),word.toLowerCase());
		}
	}
	
	public List<StemmerResult> split(POSToken token) {
		return split(token.getValue(),token.getPOSTag().getPOS().getValue());
	}
	
	
	public List<StemmerResult> split(String word, String tag) {
		List<StemmerResult> rval = new LinkedList<StemmerResult>();
		do {
			StemmerResult res = stem(word,tag);
			if(res.getStem().matches(".*\\+.*")) {
				word = res.getStem().substring(0,res.getStem().indexOf('+'));
				rval.add(0,new StemmerResultImpl(res.getStem().substring(res.getStem().indexOf('+')+1),
					res.getLemma().substring(res.getStem().indexOf('+'))));
				if(tag.matches("^V.*")) {
					tag = "PTKVZ";
				}
			} else {
				word = null;
				rval.add(0,res);
			}
		} while(word != null);
		return rval;
	}
	
	public StemmerResult stem(POSToken token) {
		return stem(token.getValue(),token.getPOSTag());
	}
	
	@Deprecated
	public StemmerResult stem(String word, POSTag tag) {
		return stem(word,tag.getPOS().getValue());
	}
	
	private String tryStem(String word, String pattern) {
		Matcher m = Pattern.compile(pattern).matcher(word);
		if(m.matches()) {
			return m.group(1);
		} else {
			return null;
		}
	}
	
	private String inf2stem(String verb) {
		return verb.replaceAll("e?n$","");
	}
	
	private String stem2inf(String stem) {
		if(stem.matches(".*(el|e|er)$")) {
			return stem + "n";
		} else {
			return stem + "en";
		}
	}
	
	private StemmerResult findCompoundVerb(String key, HashMap<String,String> map) {
		StringBuffer buf = new StringBuffer(key);
		while(buf.length() != 0) {
			if(map.containsKey(buf.toString())) {
					String prefix = key.substring(0,key.length() - buf.length());
					return new StemmerResultImpl(prefix + (prefix.isEmpty() ? "" : "+") + 
						inf2stem(map.get(buf.toString())), prefix + map.get(buf.toString()));
			}
			buf = buf.deleteCharAt(0);
		}
		return null;
	}
	
	private StemmerResult findCompoundVerbInf(String verb, HashSet<String> set) {
		String inf = stem2inf(verb);
		StringBuffer buf = new StringBuffer(inf);
		while(buf.length() != 0) {
			// e.g. wiedergemacht
			if(buf.toString().matches("ge.*en") &&
				set.contains(buf.substring(2))) {
				String prefix = inf.substring(0,inf.length() - buf.length());
				return new StemmerResultImpl(prefix + (prefix.isEmpty() ? "" : "+") + 
						buf.substring(2), prefix + buf.substring(2));
				}
			if(set.contains(buf.toString())) {
					String prefix = inf.substring(0,inf.length() - buf.length());
					return new StemmerResultImpl(prefix + (prefix.isEmpty() ? "" : "+") + 
						buf.toString(), inf);
			}
			buf = buf.deleteCharAt(0);
		}
		return null;
	}
	
	private StemmerResult stemVerb(String verb) {
		// Check sein
		if(verb.equals("bist") || verb.equals("bin") || verb.equals("seid")
			|| verb.equals("sei")) {
			return new StemmerResultImpl("sei","sein");
		}
		
		// Check against known irregulars
		StemmerResult res = findCompoundVerb(verb,perfects);
		if(res != null) return res;
		res = findCompoundVerb(verb,praterites);
		if(res != null) return res;
		res = findCompoundVerb(verb,thirdPS);
		if(res != null) return res;
		
		// Get regular stem
		String preStem = preStemVerb(verb);
		
		
		// Is regular stem a preterite (e.g. "gab-st")
		res = findCompoundVerb(preStem,perfects);
		if(res != null) return res;
		res = findCompoundVerb(preStem,praterites);
		if(res != null) return res;
		// e.g., woll-test
		res = findCompoundVerb(preStem+"te",praterites);
		if(res != null) return res;
		// e.g., mag-st
		res = findCompoundVerb(preStem,thirdPS);
		if(res != null) return res;
		// e.g., spich-st
		res = findCompoundVerb(preStem+"t",thirdPS);
		if(res != null) return res;
		
		// Does infinitive exist?
		res = findCompoundVerbInf(preStem,verbs);
		if(res != null) return res;
		// Have we chopped off a t?
		res = findCompoundVerbInf(preStem+"t",verbs);
		if(res != null) return res;
		// Have we chopped off a ge?
		res = findCompoundVerbInf("ge"+preStem,verbs);
		if(res != null) return res;
		
		
		// Best guess
		return new StemmerResultImpl(preStem,stem2inf(preStem));
	}
		
	private String preStemVerb(String verb) {
		String stem = tryStem(verb,"ge(.*)et");
		if(stem != null) return stem;
		stem = tryStem(verb,"ge(.*)t");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)te");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)test");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)tet");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)ten");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)e");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)est");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)st");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)et");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)t");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)en");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)n");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)end");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)nd");
		if(stem != null) return stem;
		stem = tryStem(verb,"(.*)");
		return stem;
	}
	
	
	private StemmerResult findCompound(String key, HashMap<String,String> map) {
		StringBuffer buf = new StringBuffer(key);
		while(buf.length() != 0) {
			if(map.containsKey(buf.toString())) {
					String prefix = key.substring(0,key.length() - buf.length());
					return new StemmerResultImpl(prefix + (prefix.isEmpty() ? "" : "+") + 
						map.get(buf.toString()), prefix + map.get(buf.toString()));
			}
			buf = buf.deleteCharAt(0);
		}
		return null;
	}
	
	private StemmerResult findCompound(String key, HashSet<String> set) {
		StringBuffer buf = new StringBuffer(key);
		while(buf.length() != 0) {
			if(set.contains(buf.toString())) {
					String prefix = key.substring(0,key.length() - buf.length());
					return new StemmerResultImpl(prefix + (prefix.isEmpty() ? "" : "+") + 
						buf.toString(), prefix + buf.toString());
			}
			buf = buf.deleteCharAt(0);
		}
		return null;
	}
	
	private StemmerResult stemAdjective(String adj) {
		// Do we know the form?
		StemmerResult res = findCompound(adj,adjs);
		if(res != null) return res;
		res = findCompound(adj,sups);
		if(res != null) return res;
		res = findCompound(adj,comps);
		if(res != null) return res;
		
		// Remove agreement and check again
		String stem = tryStem(adj,"(.*?)(|e|es|er|en|em)");
		res = findCompound(stem,adjs);
		if(res != null) return res;
		res = findCompound(stem,sups);
		if(res != null) return res;
		res = findCompound(stem,comps);
		if(res != null) return res;
		stem = null;
		
		// Check regular comparative
		stem = tryStem(adj,"(.*)er(|e|es|er|en|em)");
		if(stem != null) return new StemmerResultImpl(stem,stem);
		// Check regular superlative
		stem = tryStem(adj,"(.*)est(|e|es|er|en|em)");
		if(stem != null) return new StemmerResultImpl(stem,stem);
		stem = tryStem(adj,"(.*)st(|e|es|er|en|em)");
		if(stem != null) return new StemmerResultImpl(stem,stem);
		
		// Default
		stem = tryStem(adj,"(.*?)(|e|es|er|en|em)");
		return new StemmerResultImpl(stem,stem);
	}
		
	private StemmerResult stemNoun(String noun) {
		// Do we know the word
		StemmerResult res = findCompound(noun,nounSings);
		if(res != null) {
                    //System.out.println("1  "+res);
                    return res;
                }
		
		// Do we know the plural form?
		res = findCompound(noun, nouns);
		if(res != null) return res;
		// Dative plural form (e.g., "den Reitern")
		if(noun.matches(".*n")) {
			res = findCompound(noun.substring(0,noun.length()-1),nouns);
			if(res != null) return res;
		}
		// Genetive form
		if(noun.matches(".*es")) {
			res = findCompound(noun.substring(0,noun.length()-2),nounSings);
			if(res != null) return res;
		}
		if(noun.matches(".*s")) {
			res = findCompound(noun.substring(0,noun.length()-1),nounSings);
			if(res != null) return res;
		}
		// Dative singular
		if(noun.matches(".*e")) {
			res = findCompound(noun.substring(0,noun.length()-1),nounSings);
			if(res != null) return res;
		}
		// Typical suffices for neologisms
		if(noun.matches(".*nissen")) {
			String n = noun.substring(0,noun.length()-3);
			return new StemmerResultImpl(n,n);
		}
		if(noun.matches(".*nisse") || noun.matches(".*ungen") || noun.matches(".*keiten") || noun.matches(".*schaften")) {
			String n = noun.substring(0,noun.length()-2);
			return new StemmerResultImpl(n,n);
		}
		if(noun.matches(".*nis") || noun.matches(".*ismus")) { 
			return new StemmerResultImpl(noun,noun);	
		}
		if(noun.matches(".*ismen")) {
			String n = noun.substring(0,noun.length()-2)+ "us";
			return new StemmerResultImpl(n,n);
		}
		// Loan word?
		if(noun.matches(".*s")) {
			String n = noun.substring(0,noun.length()-1);
			return new StemmerResultImpl(n,n);
		}
		
		// Give up
		return new StemmerResultImpl(noun,noun);
	}
	
	private StemmerResultImpl stemArticle(String art) {
		for(String d : der) {
			if(art.equals(d))
				return new StemmerResultImpl("der","der");
		}
		String stem = tryStem(art,"(.*)e[rnms]?");
		if(stem == null) {
			stem = art;
		}
		if(stem.equals("dies") || stem.equals("jen") || stem.equals("welch")) {
			return new StemmerResultImpl(stem+"e",stem+"e");
		} else {
			return new StemmerResultImpl(stem,stem);
		}
	}	
	
	private class StemmerResultImpl implements StemmerResult {
		private final String stem, lemma;
		
		public StemmerResultImpl(String stem, String lemma) {
			this.stem = stem;
			this.lemma = lemma;
		}
		
		public String getStem() { return stem; }
    
		public String getLemma() { return lemma; }
		
		public Collection<SynPair> getSyntacticProperties() { return Collections.EMPTY_LIST; }
		
		public String toString() {
			if(stem.equals(lemma)) {
				return lemma;
			} else {
				return lemma + " ("+stem+")";
			}
		}
	}
}
