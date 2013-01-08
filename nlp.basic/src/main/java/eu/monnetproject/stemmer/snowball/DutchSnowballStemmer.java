package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=nl")
public class DutchSnowballStemmer extends SnowballStemmerWrap {
	public DutchSnowballStemmer() {
		super(new DutchStemmer());
	}
	public Language getLanguage() { return Language.DUTCH; }
}

