package eu.monnetproject.lemon.stl;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;

import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.SenseRelation;
import eu.monnetproject.lemon.stl.semantic.rules.SemanticSenseRelationRule;
import java.io.FileReader;
import java.io.FileWriter;

public class TestSemanticAnalysis {


	@Test
	public void TestSemanticAnalyser() throws FileNotFoundException, IOException{
		
		// name of lexicon
		String TEST_LEXICON = "finance_ex2";
		
		// init semantic analyzer
		SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

		// init lemon stuff and read lexicon
                
		LemonSerializer lemonSerializer = LemonSerializer.newInstance();
		FileReader fileSource = new FileReader(new File("src/test/resources/data/lexica/"+TEST_LEXICON+".ttl"));
                System.err.println(LemonSerializer.class.getProtectionDomain().getCodeSource().getLocation());
		LemonModel model = lemonSerializer.read(fileSource);

		// add rule (conjunctive or)
		semanticAnalyzer.addRule(new SemanticSenseRelationRule("(([a-zA-Z]+) or ([a-zA-Z]+))", 1, 2, null, SenseRelation.narrower));
		semanticAnalyzer.addRule(new SemanticSenseRelationRule("(([a-zA-Z]+) or ([a-zA-Z]+))", 1, 3, null, SenseRelation.narrower));
		
//		// add rule (parenthesis)
//		semanticAnalyzer.addRule(new SemanticSenseRelationRule("(([a-zA-Z]+) \\(([a-zA-Z]+)\\))", 1, 2, null, SenseRelation.narrower));
//		semanticAnalyzer.addRule(new SemanticSenseRelationRule("(([a-zA-Z]+) \\(([a-zA-Z]+)\\))", 1, 3, null, SenseRelation.narrower));

		// do semantic analysis
		for(Lexicon l:model.getLexica())
			for(LexicalEntry entry:l.getEntrys())
				semanticAnalyzer.perform(entry, new GenerationStateImpl(Language.ENGLISH, model));
		
		// serialize model
		File turtleFile = new File("src/test/resources/data/lexica/"+TEST_LEXICON+"_enriched.ttl");
		FileWriter fileDataTarget = new FileWriter(turtleFile);
		lemonSerializer.write(model, fileDataTarget);
		
	}
	
}
