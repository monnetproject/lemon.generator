package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=hu")
public class HungarianSnowballStemmer extends SnowballStemmerWrap {
	public HungarianSnowballStemmer() {
		super(new HungarianStemmer());
	}
	public Language getLanguage() { return Language.HUNGARIAN; }
}

