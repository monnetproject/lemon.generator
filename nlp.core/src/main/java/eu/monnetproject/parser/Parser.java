package eu.monnetproject.parser;

import eu.monnetproject.lang.Language;
import eu.monnetproject.tokens.Token;
import java.util.List;

/**
 * Interface to parsers
 * @author John McCrae
 */
public interface Parser {
	/**
	 * Parse a sequence of tokens
	 * @return The tree root node
	 */
	public TreeNode parse(Iterable<Token> tokens);
	
	/**
	 * Get the best parses of a sequence of tokens
	 * @param k A limit on the number of parses
	 * @return The k-best parses as an ordered list
	 */
	public List<TreeNode> bestParses(Iterable<Token> tokens, int k);
	
	/**
	 * Get the tagset used by this parser
	 */
	public String getTagSet();
	
    /**
     * Get the language this tagger is trained on
     */
   Language getLanguage();
}
