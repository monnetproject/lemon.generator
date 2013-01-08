package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=tr")
public class TurkishSnowballStemmer extends SnowballStemmerWrap {
	public TurkishSnowballStemmer() {
		super(new TurkishStemmer());
	}
	public Language getLanguage() { return Language.TURKISH; }
}

