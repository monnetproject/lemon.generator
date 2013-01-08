package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=en,service.ranking:Integer=-1") 
public class EnglishSnowballStemmer extends SnowballStemmerWrap {
	public EnglishSnowballStemmer() {
		super(new EnglishStemmer());
	}
	public Language getLanguage() { return Language.ENGLISH; }
}

