package eu.monnetproject.tokenizer.latin;

import java.util.List;
/*
import eu.monnetproject.corpus.Corpus;
import eu.monnetproject.corpus.NoSuchCorpusObjectException;
import eu.monnetproject.corpus.UnimplementedQueryType;
import eu.monnetproject.corpus.annotation.Annotator;
import eu.monnetproject.corpus.search.query.CorpusObjectType;
import eu.monnetproject.corpus.search.query.impl.CorpusObjectTypeQuery;
import eu.monnetproject.corpus.search.result.Result;
import eu.monnetproject.corpus.search.result.impl.SentenceResultImpl;
import eu.monnetproject.lang.UnsupportedLanguageException;
import eu.monnetproject.tokens.Token;
import eu.monnetproject.tokenizer.Tokenizer;
*/

public class LatinTokenAnnotator /*implements Annotator*/ {
/*
	Tokenizer tokenizer = getInstance();

	private Tokenizer getInstance(){
		Tokenizer tokenizer = new LatinTokenizerImpl();
		return tokenizer;
	}

	@Override
	public void annotate(Corpus corpus) {
		Iterable<Result> results;
		try {
			results = corpus.search(new CorpusObjectTypeQuery(CorpusObjectType.Sentence));
			for(Result result:results) {
				SentenceResultImpl sentenceResult = (SentenceResultImpl)result;
				// TODO: check if sentence has already tokens
				String text = sentenceResult.getText();
				// annotate
				List<Token> tokens = tokenizer.tokenize(text);
				corpus.addTokens(tokens, sentenceResult.getCorpusObjectID());
			}
		} catch (UnimplementedQueryType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLanguageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchCorpusObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
}
