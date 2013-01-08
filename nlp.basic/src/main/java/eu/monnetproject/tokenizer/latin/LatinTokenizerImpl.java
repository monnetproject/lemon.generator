package eu.monnetproject.tokenizer.latin;

import eu.monnetproject.lang.Script;
import eu.monnetproject.tokens.Token;
import eu.monnetproject.tokenizer.Tokenizer;
import java.util.*;
import java.io.*;
import aQute.bnd.annotation.component.*;

/**
 * Roman Standard Tokenizer. This is based on Lucene's 
 *
 * @author John McCrae
 */
 @Component(provide=Tokenizer.class,properties={"script=Latn"},immediate=true)
public class LatinTokenizerImpl implements Tokenizer {

	public LatinTokenizerImpl() {}
	
	private static final ArrayList<Script> supportedScripts = new ArrayList<Script>();
	
	static {
		supportedScripts.add(Script.LATIN);
		supportedScripts.add(Script.GREEK);
		supportedScripts.add(Script.CYRILLIC);
	}
	
    public Script getScript() {
        return Script.LATIN;
    }
    
    @Override
    public List<Token> tokenize(String input) {
		try {
			List<Token> rval = new ArrayList<Token>();
			LatinTokenizerCC cc = new LatinTokenizerCC(new StringReader(input));
			while(true) {
				Token tk = cc.next();
				if(tk == null)
					break;
				if(tk.getValue() == null)
					continue;
				rval.add(tk);
			}
			return rval;
		} catch(IOException x) {
			x.printStackTrace();
			return Collections.EMPTY_LIST;
		} catch(ParseException x) {
			x.printStackTrace();
			return Collections.EMPTY_LIST;
		}
    }
}

