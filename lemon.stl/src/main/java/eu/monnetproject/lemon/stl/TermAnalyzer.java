package eu.monnetproject.lemon.stl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.Edge;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.Node;
import java.net.URI;


/**
 * @author nitagg
 *
 */

public class TermAnalyzer{

	private Node makeNode(Component component, Node root, LemonFactory factory){
		if(root==null){
			System.out.println( "add a new node ");
			return factory.makeNode(component.getElement().getURI());
		}
		
		if(root.getEdges()==null){
			System.out.println( "add a new edge ");
			root.addEdge(Edge.edge,factory.makeNode(component.getElement().getURI()));
			return root;
		}
		Collection<Collection<Node>> collection = root.getEdges().values();

		for(Collection<Node> nodes : collection){
			for(Node node: nodes){
				if( isSubterm(node.getURI().getRawFragment(),component.getURI().getRawFragment())){
					return makeNode(component, node, factory);
				}
				else
					return factory.makeNode(component.getElement().getURI());
			}
		}

		return null;
	}


	private String toresourse(String uri){
		if(uri.toLowerCase().contains("xebr"))
			return "xebr";
		if(uri.toLowerCase().contains("ifrs"))
			return "ifrs";

		return null;
	}


	private Component getPrioritizedEntry(Component c1, Component c2,List<String> resources ){
		//		System.out.println(" in prioritezed  :");
		String r1 = toresourse(c1.getElement().getSenses().iterator().next().getReference().toString());
		String r2 = toresourse(c2.getElement().getSenses().iterator().next().getReference().toString());
		if(resources.indexOf(r1)>resources.indexOf(r2))
			return c1;
		else
			return c2;

	}

	public void resolveConflict(List<Component> components,Component component, List<String> resources){
		//		System.out.println(" in resolve  ");

		String subterm1 = component.getElement().getCanonicalForm().getWrittenRep().value;
		//		System.out.println(" sub => "+ subterm1);
		Iterator<Component> it = components.iterator();
		while(it.hasNext()){	
			Component next = it.next();
			String subterm2 = next.getElement().getCanonicalForm().getWrittenRep().value;
			//			System.out.println("1: "+subterm1 +"  2:" +subterm2);
			if(subterm1.equalsIgnoreCase(subterm2)){
				components.remove(next);
				Component prioritizedEntry = getPrioritizedEntry(component,next, resources);
				components.add(prioritizedEntry);
			}
		}
	}


	private boolean isSubterm(String term, String subterm){
		if(term.toLowerCase().contains(subterm.toLowerCase()))
			return true;

		return false;
	} 

	private boolean hasReferance(LexicalEntry entry){
		for(LexicalSense sen: entry.getSenses()){
			if(!sen.getReference().toString().isEmpty()){
				//				System.out.println("sense is : "+ sen.getReference());
				return true;
			}
		}

		return false;
	}

	public void addTermTree(LexicalEntry termEntry, LemonModel lemonModel, List<String> resources) {
		HashSet<String> set = new HashSet<String>();

		//check
		if (lemonModel != null) {
			LemonFactory factory = lemonModel.getFactory();
			Collection<Lexicon> lexica = lemonModel.getLexica();


			for(LexicalForm termForms:termEntry.getForms()){
				for (Lexicon lexicon : lexica) {
					String term = termForms.getWrittenRep().value;
					List<Component> components = new ArrayList<Component>();
					//subterms for each entry
					for (LexicalEntry subtermEntry : lexicon.getEntrys()){
						for(LexicalForm subtermForm:subtermEntry.getForms()) {
							String subterm = subtermForm.getWrittenRep().value;
							if(isSubterm(term, subterm)){
								if(!term.equalsIgnoreCase(subterm)){
									Component comp1 = factory.makeComponent(URI.create(subtermEntry.getURI()+"/comp1"));
									comp1.setElement(subtermEntry);
									//System.out.println("  subterentry is  : "+subterm);
									if(hasReferance(subtermEntry)){
										if(set.contains(subterm))
											resolveConflict(components, comp1, resources);
										else
											components.add(comp1);
										set.add(subterm);
									}
								}
							}
						}
					}
										
					
					Node root = factory.makeNode(termEntry.getURI());
					for(Component comp:components){
					root = makeNode(comp, root, factory);				
					}
					termEntry.addPhraseRoot(root);
					//termEntry.addDecomposition((components));

				}

			}

		} else {
			System.out.println("Corrput lemon lexicon");
		}
	}


}
