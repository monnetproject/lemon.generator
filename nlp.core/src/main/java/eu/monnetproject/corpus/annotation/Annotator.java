package eu.monnetproject.corpus.annotation;

import eu.monnetproject.corpus.Corpus;

/**
 * Annotates a corpus
 * 
 * An annotator takes corpus objectes (eg document, sentence, token, etc.), annotates
 * these calling NLP or Semantic processors and feeds them back into the corpus. The
 * annotator also creates URIs for each of them used by the corpus to index them.
 * 
 * @author Tobias Wunner
 */
public interface Annotator {

	public void annotate(Corpus corpus);
	
}
