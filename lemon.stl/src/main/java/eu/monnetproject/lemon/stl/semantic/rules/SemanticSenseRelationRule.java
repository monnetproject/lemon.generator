package eu.monnetproject.lemon.stl.semantic.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.model.SenseRelation;
import eu.monnetproject.rule.Rule;

/**
 * A rule to match groups in a string via a regular expression on the left hand side and
 * add new relations defined in the right hand side. 
 * 
 * @author Tobias Wunner
 *
 */
public class SemanticSenseRelationRule implements Rule  {

	Pattern pattern;
	Integer groupLHS;
	Integer groupRHS;
	SenseRelation relation;
	private Language lang;
	
	public SemanticSenseRelationRule(String regex,Integer groupLHS, Integer groupRHS, Language lang, SenseRelation relation) {
		super();
		this.pattern = Pattern.compile(regex);
		this.groupLHS = groupLHS;
		this.groupRHS = groupRHS;
		this.relation = relation;
		this.lang = lang;
	}
	
	public Matcher getMatcher(String text) {
		return pattern.matcher(text);
	}

	public Pattern getPattern() {
		return pattern;
	}

	public Integer getGroupLHS() {
		return groupLHS;
	}

	public Integer getGroupRHS() {
		return groupRHS;
	}

	public SenseRelation getSenseRelation() {
		return relation;
	}
	
	public Language getLang() {
		return this.lang;
	}

}
