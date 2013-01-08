package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
import org.tartarus.snowball.ext.*;

@Component(provide=Stemmer.class,properties="language=it")
public class ItalianSnowballStemmer extends SnowballStemmerWrap {
	public ItalianSnowballStemmer() {
		super(new ItalianStemmer());
	}
	public Language getLanguage() { return Language.ITALIAN; }
}

