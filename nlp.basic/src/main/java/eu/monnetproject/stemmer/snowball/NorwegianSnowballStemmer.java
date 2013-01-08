package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=no")
public class NorwegianSnowballStemmer extends SnowballStemmerWrap {
	public NorwegianSnowballStemmer() {
		super(new NorwegianStemmer());
	}
	public Language getLanguage() { return Language.NORWEGIAN; }
}

