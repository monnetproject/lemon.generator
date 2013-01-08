package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=es")
public class SpanishSnowballStemmer extends SnowballStemmerWrap {
	public SpanishSnowballStemmer() {
		super(new SpanishStemmer());
	}
	public Language getLanguage() { return Language.SPANISH; }
}

