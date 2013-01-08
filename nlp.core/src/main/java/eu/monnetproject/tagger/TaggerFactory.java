package eu.monnetproject.tagger;

import eu.monnetproject.lang.Language;

/**
 * A factory for generating taggers for the appropriate language
 *
 * @author John McCrae
 */
public interface TaggerFactory {
	/**
	 * Create a tagger for the given language
	 * @param language The language
	 * @throws Exception If the tagger model does not exist or can't be found
	 */
	Tagger makeTagger(Language language) throws Exception;
}
