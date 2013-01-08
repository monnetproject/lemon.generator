package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=fr")
public class FrenchSnowballStemmer extends SnowballStemmerWrap {
	public FrenchSnowballStemmer() {
		super(new FrenchStemmer());
	}
	public Language getLanguage() { return Language.FRENCH; }
}

