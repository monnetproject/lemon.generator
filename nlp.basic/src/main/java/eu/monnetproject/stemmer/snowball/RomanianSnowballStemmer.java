package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=ro")
public class RomanianSnowballStemmer extends SnowballStemmerWrap {
	public RomanianSnowballStemmer() {
		super(new RomanianStemmer());
	}
	public Language getLanguage() { return Language.ROMANIAN; }
}

