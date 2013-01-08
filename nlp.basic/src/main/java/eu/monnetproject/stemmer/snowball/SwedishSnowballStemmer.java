package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=sv")
public class SwedishSnowballStemmer extends SnowballStemmerWrap {
	public SwedishSnowballStemmer() {
		super(new SwedishStemmer());
	}
	public Language getLanguage() { return Language.SWEDISH; }
}

