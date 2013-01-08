package eu.monnetproject.lemon.stl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.SenseRelation;
import eu.monnetproject.lemon.model.Text;
import eu.monnetproject.lemon.stl.semantic.rules.SemanticSenseRelationRule;
import eu.monnetproject.rule.Rule;
import java.io.FileReader;

public class SemanticAnalyzer implements GeneratorActor {

	LemonFactory factory;
	private List<Rule> rules = new ArrayList<Rule>();

	public void addRule(Rule rule) {
		this.rules.add(rule);
	}

	private LexicalEntry makeEntry(String writtenRep,Language lang,URI uri) {
		LexicalEntry entry = factory.makeLexicalEntry(uri);
		LexicalForm form = factory.makeForm(URI.create(uri+"#form"));
		form.setWrittenRep(new Text(writtenRep, Language.ENGLISH.toString()));
		entry.addForm(form);
		return entry;
	}

	private void applySemanticSenseRelationRule(LemonModel model,LexicalEntry sourceEntry, Text text, SemanticSenseRelationRule rule, GenerationState state) {
		Matcher m = rule.getMatcher(text.value);
		if (m.find()) {

			// apply rule to get new written representation
			//String sourceString = m.group(rule.getGroupLHS()).replaceAll("(", "\\\\(").replaceAll(")", "\\\\)");
			String sourceString = m.group(rule.getGroupLHS());
			String replaceString = m.group(rule.getGroupRHS());
			String writtenRep = text.value.replaceAll(sourceString, replaceString);

			// create new lexical entry
			//final LexicalEntry newEntry = state.addLexicalEntry(writtenRep, state.getLanguage());

			// make sense and add sense relations
			LexicalSense senseTarget = factory.makeSense(URI.create(sourceEntry.getURI()+"#sense"));
			for(LexicalSense senseSource:sourceEntry.getSenses())
				senseTarget.addSenseRelation(rule.getSenseRelation(), senseSource);

		}
	}

	@Override
	public void perform(LexicalEntry entry, GenerationState state) {
		final LemonModel model = state.getModel();
		this.factory = model.getFactory();

		// for all forms
		for(LexicalForm form:entry.getForms()) {
			Text writtenRep = form.getWrittenRep();

			// for all rules
			for(Rule rule:rules) {

				// match semantic sense relation rule
				if (rule instanceof SemanticSenseRelationRule)
					applySemanticSenseRelationRule(model, entry, writtenRep, (SemanticSenseRelationRule)rule,state);

			}
		}		
	}

	@Override
	public double getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}


	public static void main(String[] args) throws FileNotFoundException {

		// init semantic analyzer
		SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

		// init lemon stuff and read lexicon
		LemonSerializer serializer = LemonSerializer.newInstance();
		FileReader fileSource = new FileReader(new File("data/lexica/finance_semantic_analysis.ttl"));
		LemonModel model = serializer.read(fileSource);

		// prepare rules
		semanticAnalyzer.addRule(new SemanticSenseRelationRule("(([a-zA-Z]+) or ([a-zA-Z]+))", 1, 2, null, SenseRelation.narrower));
		semanticAnalyzer.addRule(new SemanticSenseRelationRule("(([a-zA-Z]+) or ([a-zA-Z]+))", 1, 3, null, SenseRelation.narrower));

		// do semantic analysis
		for(Lexicon l:model.getLexica())
			for(LexicalEntry entry:l.getEntrys()) {
				System.out.println(entry.getURI().toString().replaceAll("^.*#", ""));
				//semanticAnalyzer.perform(entry, null, Language.ENGLISH, model, null, null);
			}


	}

}
