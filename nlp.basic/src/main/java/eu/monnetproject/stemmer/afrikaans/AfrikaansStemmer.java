package eu.monnetproject.stemmer.afrikaans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.StemmerResult;

public class AfrikaansStemmer {

	public HashMap<String,String> nouns = new HashMap<String,String>();
	public HashSet<String> nounSings = new HashSet<String>();
	public HashSet<String> verbs = new HashSet<String>();
	public HashSet<String> adjs = new HashSet<String>();
	
	private final String resPrefix = "/eu/monnetproject/stemmer/afrikaans/";
	
	public AfrikaansStemmer() {
		super();
		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Language getLanguage() { return Language.AFRIKAANS; }
	
	private void init() throws IOException {
		//BufferedReader nounsIn = new BufferedReader(new InputStreamReader(AfrikaansStemmer.class.getResourceAsStream(resPrefix+"nouns_sing.table")));
		File file = new File("resources/stemmer.afrikaans/"+resPrefix+"/nouns.table");
		BufferedReader nounsIn = new BufferedReader(new FileReader(file));
		String s;
		while((s = nounsIn.readLine()) != null) {
			if(s.matches("\\s*")) continue;
			String[] ss = s.split("\\s+");
			nouns.put(ss[1].toLowerCase(),ss[0].toLowerCase());
			nounSings.add(ss[0].toLowerCase());
		}
		// nouns singular only
		file = new File("resources/stemmer.afrikaans/"+resPrefix+"/nouns_sing.table");
		nounsIn = new BufferedReader(new FileReader(file));
		while((s = nounsIn.readLine()) != null) {
			if(s.matches("\\s*")) continue;
			String[] ss = s.split("\\s+");
			nounSings.add(ss[0].toLowerCase());
		}
		// verbs infinitiv only
		file = new File("resources/stemmer.afrikaans/"+resPrefix+"/verbs.table");
		nounsIn = new BufferedReader(new FileReader(file));
		while((s = nounsIn.readLine()) != null) {
			if(s.matches("\\s*")) continue;
			String[] ss = s.split("\\s+");
			verbs.add(ss[0].toLowerCase());
		}
		// adjectives
		file = new File("resources/stemmer.afrikaans/"+resPrefix+"/adjs.table");
		nounsIn = new BufferedReader(new FileReader(file));
		while((s = nounsIn.readLine()) != null) {
			if(s.matches("\\s*")) continue;
			String[] ss = s.split("\\s+");
			adjs.add(ss[0].toLowerCase());
		}
	}
	
	public StemmerResult stemNoun(String noun) {
		// Do we know the word
		StemmerResultImpl res = findCompound(noun, nounSings);
		if(res != null) return res;
		
		// Do we know the plural form?
		res = findCompound(noun,nouns);
		if(res != null) return res;
		
		// adjective
		res = findCompound(noun,adjs);
		if(res != null) return res;
		
		// Give up
		return new StemmerResultImpl(noun,noun);
	}
	
	private StemmerResultImpl findCompound(String key, HashSet<String> set) {
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
	
	private StemmerResultImpl findCompound(String key, HashMap<String,String> map) {
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

//	private List<StemmerResult> stemtNoun(String word) {
//		List<StemmerResult> rval = new LinkedList<StemmerResult>();
//		do {
//			StemmerResult res = stemNoun(word);
//			word = null;
//			if (res!=null)
//				if(res.getStem().matches(".*\\+.*")) {
//					word = res.getStem().substring(0,res.getStem().indexOf('+'));
//					rval.add(0,new StemmerResultImpl(res.getStem().substring(res.getStem().indexOf('+')+1),
//							res.getLemma().substring(res.getStem().indexOf('+'))));
//				} else {
//					word = null;
//					rval.add(0,res);
//				}
//		} while(word != null);
//		return rval;
//	}
	
//	private String splitcompound(String word,Set<String> nouns) {
//		String split = null;
//		for(String noun:nouns)
//			if(word.endsWith(noun)) {
//				String p1 = word.substring(0,word.length()-noun.length());
//				String p2 = word.substring(word.length()-noun.length(), word.length());
//				System.out.println(p1+"-"+p2);
//				split=p1+"-"+p2;
//			}
//		return split;
//	}
	
}
