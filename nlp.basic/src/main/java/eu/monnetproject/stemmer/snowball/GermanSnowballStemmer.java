package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=de,service.ranking:Integer=-1") 
public class GermanSnowballStemmer extends SnowballStemmerWrap {
	public GermanSnowballStemmer() {
		super(new GermanStemmer());
	}
	public Language getLanguage() { return Language.GERMAN; }
}

