/**********************************************************************************
 * Copyright (c) 2011, Monnet Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Monnet Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************************/
package eu.monnetproject.splitter.impl;

import eu.monnetproject.lang.Language;
import eu.monnetproject.sentence.Chunk;
import eu.monnetproject.sentence.Sentence;
import eu.monnetproject.pos.POSToken;
import eu.monnetproject.tokens.Token;
import java.util.Iterator;
import java.util.List;

/**
 * A factory to make sentences
 * 
 * @author Tobias Wunner
 *
 */
public class SentenceFactory {

	public static class SentenceImpl implements Sentence {
		private String text;
		private List<Token> tokens;
		private List<POSToken> postokens;
		private List<Chunk> chunks;
		@Override
		public String getText() {
			return text;
		}
//		@Override
//		public List<Token> getTokens() {
//			return tokens;
//		}
//		@Override
//		public List<POSToken> getPOSTokens() {
//			return postokens;
//		}
		@Override
		public List<Chunk> getChunks() {
			return chunks;
		}
		@Override
		public Language getLang() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * Makes a sentence with a text (implemented by getText method) 
	 * 
	 * @param text
	 * @return
	 */
	public static Sentence makeSentence(final String text) {
		return new SentenceImpl() {
			public String getText() {
				return text;
			}
		};
	}

	/**
	 * Makes a sentence with a text and tokens (postokens) implemented
	 * by the methods getText, getTokens (getPOSTokens)
	 * 
	 * Note: if the tokens are of type postokens the returned
	 *       sentence implements both methods getTokens and
	 *       getPOSTokens
	 * 
	 * @param tokens
	 * @return
	 */
	public static Sentence makeSentence(final List<? extends Token> tokens) {
		final String text = token2Text(tokens);
		if (tokens instanceof Token) {
			return new SentenceImpl() {
				public String getText() {
					return text;
				}
				public List<Token> getTokens() {
					return (List<Token>) tokens;
				}
			};
		} else {
			return new SentenceImpl() {
				public String getText() {
					return text;
				}
				public List<Token> getTokens() {
					return (List<Token>) tokens;
				}
				public List<POSToken> getPOSTokens() {
					return (List<POSToken>) tokens;
				}
			};			
		}
	}

	/**
	 * Makes a sentence with a text and chunks (implemented by getText and getChunks methods) 
	 * 
	 * @param text
	 * @return
	 */
	public static Sentence makeSentence(final String text,final List<Chunk> chunks) {
		return new SentenceImpl() {
			public String getText() {
				return text;
			}
			public List<Chunk> getChunks(){
				return chunks;
			}
		};
	}

	/**
	 * Build text String from postokens
	 * 
	 * @param tokens
	 * @return
	 */
	private static String token2Text(List<? extends Token> tokens) {
		String text = "";
		Iterator<? extends Token> it = tokens.iterator();
		while(it.hasNext()) {
			//for(Token token:tokens) {
			Token token = it.next();
			String value = token.getValue();
			if (text.isEmpty())
				text = value;
			else {
				if (it.hasNext()) {
					text = text + " " + value;
				} else {
					// last token is stop character
					if (value.matches("[\\.\\?\\!]"))
						text = text + value;
					// last token was word
					else
						text = text + value + " " + ".";
				}
			}
		}
		return text;
	}

}
