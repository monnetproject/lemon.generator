package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=ru")
public class RussianSnowballStemmer extends SnowballStemmerWrap {
	public RussianSnowballStemmer() {
		super(new RussianStemmer());
	}
	public Language getLanguage() { return Language.RUSSIAN; }
}

