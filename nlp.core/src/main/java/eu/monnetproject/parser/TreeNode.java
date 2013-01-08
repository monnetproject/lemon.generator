package eu.monnetproject.parser;

import java.util.List;
import eu.monnetproject.tokens.Token;
import eu.monnetproject.pos.POSTag;

public interface TreeNode {
	
	/**
	 * Get the children of this node
	 * @return The children or an empty list if a leaf node
	 */
	List<Child> getChildren();
	
	/**
	 * Is this a leaf node, equivalent to getChildren().isEmpty()
	 */
	boolean isLeaf();
	
	/**
	 * Get the token of a leaf node
	 * @return The token or null if not a leaf node
	 */
	Token getToken();
	
	/**
	 * Get the tag of a non-leaf node
	 * @return The tag or null if a leaf node
	 */
	POSTag getTag();	
	
	/**
	 * Get the score of this tag
	 * @return The score as a value between 0 and 1, or Double.NaN if no score is available 
	 * or this node is not the root of the parse
	 */
	double getScore();
	
	public interface Child {
		Edge edge();
		TreeNode node();
	}
	
}
