package eu.monnetproject.translator;

import java.util.Collection;
import java.util.List;

import eu.monnetproject.lang.Language;
import eu.monnetproject.tokens.Token;
import eu.monnetproject.util.Pair;

@Deprecated
public interface TokenTranslator extends Translator {

	List<Pair<Token, Collection<Translation>>> translate(List<Token> tokens, Language src, Language trg);
}
