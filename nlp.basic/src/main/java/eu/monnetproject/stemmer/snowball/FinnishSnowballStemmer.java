package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=fi")
public class FinnishSnowballStemmer extends SnowballStemmerWrap {
	public FinnishSnowballStemmer() {
		super(new FinnishStemmer());
	}
	public Language getLanguage() { return Language.FINNISH; }
}

