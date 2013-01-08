package eu.monnetproject.tokens;


/**
 * A token. This also supports compositionality allowing parse structures to be
 * represented, or terms to be decomposed
 * @author John McCrae
 */
public interface Token {
	/**
	 * Get the value of this token i.e., the word
	 * @return The string value of the token
	 */
	public String getValue();	
	
	/**
	 * Get any sub tokens in this token
	 * @return The list of sub tokens, or an empty list if there are not sub tokens
	 * @deprecated Tokenizers with children are parsers!
	 */
	//@Deprecated
	// public  List<Token> getChildren();
}
