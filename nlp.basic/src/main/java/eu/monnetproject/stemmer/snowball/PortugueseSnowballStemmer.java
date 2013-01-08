package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=pt")
public class PortugueseSnowballStemmer extends SnowballStemmerWrap {
	public PortugueseSnowballStemmer() {
		super(new PortugueseStemmer());
	}
	public Language getLanguage() { return Language.PORTUGUESE; }
}

