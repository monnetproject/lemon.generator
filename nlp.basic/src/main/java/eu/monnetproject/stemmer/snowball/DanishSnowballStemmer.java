package eu.monnetproject.stemmer.snowball;

import aQute.bnd.annotation.component.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.morph.*;
//import org.apache.felix.scr.annotations.*;
import org.tartarus.snowball.ext.*;

//@Component()
//@Service(value=Stemmer.class)
//@Property(name="language",value="da")
@Component(provide=Stemmer.class,properties="language=da")
public class DanishSnowballStemmer extends SnowballStemmerWrap {
	public DanishSnowballStemmer() {
		super(new DanishStemmer());
	}
	
	public Language getLanguage() { return Language.DANISH; }
}

