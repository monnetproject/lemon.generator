package eu.monnetproject.parser.stanford;

import eu.monnetproject.tokens.Token;
import eu.monnetproject.parser.*;
import eu.monnetproject.pos.*;
import eu.monnetproject.tokenizer.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import java.util.*;

public class DepTreeWrap implements TreeNode {
	private final Collection<TypedDependency> depList;
	private final TreeGraphNode root;
	private final StanfordPOSSet posSet;
	
	public DepTreeWrap(Collection<TypedDependency> depList, TreeGraphNode root, StanfordPOSSet posSet) {
		this.depList = depList;
		this.root = root;
		this.posSet = posSet;
	}
	
	public double getScore() { return Double.NaN; }
	
	public POSTag getTag() { 
		return new StanfordPOSTag(posSet.getPOS(root.label().tag()));
	}
	
	public Token getToken() {
		return new Token() {
			public String getValue() {
				return root.label().word();
			}
		};
	}
	
	public boolean isLeaf() {
		return getChildren().isEmpty();
	}
	
	public List<TreeNode.Child> getChildren() {
		List<TreeNode.Child> rval = new LinkedList<TreeNode.Child>();
		for(TypedDependency td : depList) {
			if(td.gov().equals(root)) {
				rval.add(new DepChild(td));
			}	
		}
		return rval;
	}	
	
	private class DepChild implements TreeNode.Child {
		private final TypedDependency td;
		
		public DepChild(TypedDependency td) {
			this.td = td;
		}
		
		
		public Edge edge() {
			return new Edge() {
				public String getName() { return td.reln().toString(); }
			};
		}
		
		public TreeNode node() {
			return new DepTreeWrap(depList,td.dep(),posSet);
		}
	}
}
