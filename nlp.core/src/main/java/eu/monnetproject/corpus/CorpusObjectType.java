package eu.monnetproject.corpus;

import eu.monnetproject.corpus.annotation.Annotation;
import eu.monnetproject.corpus.annotation.TextAnnotation;
import eu.monnetproject.pos.POSToken;
import eu.monnetproject.sentence.Sentence;
import eu.monnetproject.sentence.Chunk;
import eu.monnetproject.tokens.Token;

/**
 * Corpus object types
 * 
 * @author Tobias Wunner
 *
 */
public enum CorpusObjectType {

	Annotation,TextAnnotation,Document, Sentence, Chunk, Token, POSToken, Other;

	public static CorpusObjectType get(Object corpusObject) {
		if (corpusObject instanceof TextAnnotation) {
			return TextAnnotation;
		}
		if (corpusObject instanceof Annotation) {
			return Annotation;
		}
		if (corpusObject instanceof eu.monnetproject.doc.Document) {
			return Document;
		}
		if (corpusObject instanceof Sentence) {
			return Sentence;
		}
		if (corpusObject instanceof Chunk) {
			return Chunk;
		}
		if (corpusObject instanceof Token) {
			return Token;
		}
		if (corpusObject instanceof POSToken) {
			return POSToken;
		}
		return Other;
	}

	public static CorpusObjectType get(String corpusObjectType) {
		if (corpusObjectType.equals("Annotation"))
			return Annotation;
		if (corpusObjectType.equals("TextAnnotation"))
			return TextAnnotation;
		if (corpusObjectType.equals("Document"))
			return Document;
		if (corpusObjectType.equals("Sentence"))
			return Sentence;
		if (corpusObjectType.equals("Chunk"))
			return Chunk;
		if (corpusObjectType.equals("Token"))
			return Token;
		if (corpusObjectType.equals("POSToken"))
			return POSToken;
		return null;
	}
}
