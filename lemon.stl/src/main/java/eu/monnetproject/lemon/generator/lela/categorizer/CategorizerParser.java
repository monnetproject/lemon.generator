/****************************************************************************
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
 ********************************************************************************/
package eu.monnetproject.lemon.generator.lela.categorizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author John McCrae
 */
public class CategorizerParser {

    public static List<CategorizerRule> parser(Reader reader) throws IOException, CategorizerParseException {
        final BufferedReader in = new BufferedReader(reader);
        String s;
        int lineNo = 1;
        final LinkedList<CategorizerRule> rules = new LinkedList<CategorizerRule>();
        CategorizerRule current = null;
        while ((s = in.readLine()) != null) {
            if (s.startsWith("Tree:")) {
                if (current != null) {
                    rules.add(current);
                }
                current = new CategorizerRule();
                final int pos = enterTree(s,current,5,lineNo);
                if(!s.substring(pos).matches("\\s*")) {
                    throw new CategorizerParseException("Unexpected " + s.substring(pos) + " l"+lineNo+" c"+pos);
                }
            } else if(s.startsWith("Semantics:")) {
                if(current == null) {
                    throw new CategorizerParseException("Semantics line found but Tree expected l" + lineNo);
                }
                final int pos = enterSemantics(s,current,10,lineNo);
                if(!s.substring(pos).matches("\\s*")) {
                    throw new CategorizerParseException("Unexpected " + s.substring(pos) + " l"+lineNo+" c"+pos);
                }
            } else if(s.startsWith("Frame:")) {
                if(current == null) {
                    throw new CategorizerParseException("Frame line found but Tree expected l"+lineNo);
                }
                final int pos = enterFrame(s,current,6,lineNo);
                if(!s.substring(pos).matches("\\s*")) {
                    throw new CategorizerParseException("Unexpected " + s.substring(pos) + " l"+lineNo+" c"+pos);
                }
            }
            lineNo++;
        }
        return rules;
    }

    private static int enterTree(String s, CategorizerRule current, int col, int lineNo) throws CategorizerParseException {
        while(col < s.length()) {
            if(s.charAt(col) == ' ' || s.charAt(col) == '\u0009') {
                col++;
            } else if(s.charAt(col) == '(') {
                final Tree tree = new Tree();
                current.tree = tree;
                return tree(s,tree,col,lineNo);
            } else {
                throw new CategorizerParseException("Unexpected " + s.substring(col) + " l"+lineNo + " c"+col);
            }
        }
        throw new CategorizerParseException("Tree block does not contain tree l" +lineNo);
    }

    private static final String OWL = "http://www.w3.org/2002/07/owl#";
    private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    
    private static int enterSemantics(String s, CategorizerRule current, int col, int lineNo) throws CategorizerParseException {
        final String semantics = s.substring(col).replaceAll("\\s", "");
        for(String uri : semantics.split(",")) {
            if(uri.startsWith("owl:")) {
                current.semantics.add(URI.create(OWL+uri.substring(4)));
            } else if(uri.startsWith("rdfs:")) {
                current.semantics.add(URI.create(RDFS+uri.substring(5)));
            } else if(uri.startsWith("rdf:")) {
                current.semantics.add(URI.create(RDF+uri.substring(4)));
            } else if(uri.startsWith("<") && uri.endsWith(">")) {
                current.semantics.add(URI.create(uri.substring(1, uri.length()-1)));
            } else {
                throw new CategorizerParseException("Unrecognized semantic type " + uri+ " l"+lineNo + " c"+col);
            }
        }
        return s.length();
    }

    private static int enterFrame(String s, CategorizerRule current, int col, int lineNo) throws CategorizerParseException {
        final String frame = s.substring(col).replaceAll("\\s", "");
        if(!frame.matches("\\w+")) {
            throw new CategorizerParseException("Bad frame: " + frame + " l"+lineNo + " c"+col);
        }
        current.frame = frame;
        return s.length();
    }

    private static int tree(String s, Tree tree, int col, int lineNo) throws CategorizerParseException {
        if(s.charAt(col) != '(') {
            throw new CategorizerParseException("Expected (" + " l"+lineNo + " c"+col);
        }
        col++;
        while(col < s.length()) {
            if(s.charAt(col) >= '0' && s.charAt(col) <= 'z') {
                col = constituent(s,tree,col,lineNo);
            } else if(s.charAt(col) == '{') {
                col = marker(s,tree,col,lineNo);
            } else if(s.charAt(col) == ')') {
                return col+1;
            } else if(s.charAt(col) == '(') {
                final Tree newTree = new Tree();
                col = tree(s,newTree,col,lineNo);
                tree.subnodes.add(newTree);
            } else if(s.charAt(col) == '"') {
                col = literal(s,tree,col,lineNo);
            } else if(s.charAt(col) == ' ' || s.charAt(col) == '\u0009') {
                col++;
            }  else {
                throw new CategorizerParseException("Unexpected "+s.substring(col)+ " l"+lineNo + " c"+col);
            }
        }
        throw new CategorizerParseException("Expected ) "+ " l"+lineNo + " c"+col);
    }

    private static int constituent(String s, Tree tree, int col, int lineNo) {
        final int start = col;
        while(s.charAt(col) >= '0' && s.charAt(col) <= 'z') {
            col++;
        }
        tree.constituent = s.substring(start, col);
        return col;
    }

    private static int marker(String s, Tree tree, int col, int lineNo) throws CategorizerParseException {
        if(s.charAt(col) != '{') {
            throw new CategorizerParseException("Expected {" + " l"+lineNo + " c"+col);
        }
        final int start = ++col;
        while(s.charAt(col) != '}' && col < s.length()) {
            col++;
        }
        if(col == s.length()) {
            throw new CategorizerParseException("Expected } "+ " l"+lineNo + " c"+col);
        }
        final String marker = s.substring(start,col);
        if(marker.equals("head")) {
            tree.head = true;
        } else if(marker.equals("_")) {
            tree.ignore = true;
        } else if(marker.matches(".*\\.marker")) {
            tree.marker = marker.substring(0, marker.indexOf("."));
        } else {
            throw new CategorizerParseException("Unknown marker type "+ marker + " "+ " l"+lineNo + " c"+col);
        }
        return col+1;
    }

    private static int literal(String s, Tree tree, int col, int lineNo) throws CategorizerParseException {
        if(s.charAt(col) != '"') {
            throw new CategorizerParseException("Expected \"" + " l"+lineNo + " c"+col);
        }
        final int start = ++col;
        while(s.charAt(col) != '"' && col < s.length()) {
            col++;
        }
        if(col == s.length()) {
            throw new CategorizerParseException("Expected \" "+ " l"+lineNo + " c"+col);
        }
        tree.literal = s.substring(start,col);
        return col+1;
    }
}
