package eu.monnetproject.parser.stanford;

import eu.monnetproject.parser.*;
import edu.stanford.nlp.trees.*;
import java.util.*;
import eu.monnetproject.tokens.Token;
import eu.monnetproject.pos.POSTag;
import eu.monnetproject.util.Logging;
import eu.monnetproject.util.Logger;

public class TreeNodeWrap implements TreeNode {
    private static final Logger log = Logging.getLogger(TreeNodeWrap.class);
	private final Tree underlying;
	private final StanfordPOSSet posSet;
	private final List<Token> tokens;
	private final int leafCountOffset;
	private final double score;
	
	public TreeNodeWrap(Tree tree, StanfordPOSSet posSet, List<Token> tokens, int leafCountOffset) {
		this.underlying = tree;
		this.posSet = posSet;
		this.tokens = tokens;
		this.leafCountOffset = leafCountOffset;
		this.score = Double.NaN;
	}
	
	public TreeNodeWrap(Tree tree, StanfordPOSSet posSet, List<Token> tokens, int leafCountOffset, double score) {
		this.underlying = tree;
		this.posSet = posSet;
		this.tokens = tokens;
		this.leafCountOffset = leafCountOffset;
		this.score = score;
	}

	private List<Child> children = null;
	
	public List<Child> getChildren() {
		if(children == null) {
			Tree[] children2 = underlying.children();
			children = new ArrayList<Child>(children2.length);
			int loc = leafCountOffset;
			for(Tree t : children2) {
				TreeNodeWrap tn = new TreeNodeWrap(t,posSet,tokens,loc);
				children.add(new ChildWrap(tn));
				loc += tn.getLeafCount();
			}
		}
		return children;
	}
	
	public boolean isLeaf() { return underlying.isLeaf(); }
	
	public Token getToken() {
		if(underlying.isLeaf()) {
			return tokens.get(leafCountOffset);
		} else {
			return null;
		}
	}
	
	public POSTag getTag() {
		if(underlying.isLeaf()) {
                    
			return null;
		} else {
                    if(underlying instanceof TreeGraphNode) {
			return new StanfordPOSTag(posSet.getPOS(((TreeGraphNode)underlying).label().tag()));
                    } else if(underlying instanceof LabeledScoredTreeNode) {
                        return new StanfordPOSTag(posSet.getPOS(underlying.label().value()));
                    } else {
                        log.severe("Could not get tag for " + underlying + " of  type " + underlying.getClass().getName());
                        log.info("Label="+underlying.label().value());
                        return null;
                    }
		}
	}
	
	int getLeafCount() {
		if(isLeaf()) {
			return 1;
		} else {
			int rval = 0;
			for(Child child : getChildren()) {
				rval += ((TreeNodeWrap)child.node()).getLeafCount();
			}
			return rval;
		}
	}
	
	public double getScore() { return score; }
	
	@Override
	public String toString() {
		if(isLeaf()) {
			return getToken().toString();
		} else {
			return getTag().toString();
		}
	}
	
	private class ChildWrap implements TreeNode.Child {
		private final TreeNodeWrap child;
		public Edge edge() { return Edge.UNNAMED; }
		public TreeNode node() { return child; }
		public ChildWrap(TreeNodeWrap child) { this.child = child; }
	}
			
}
