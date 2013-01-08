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
package eu.monnetproject.parser.combinator;

import eu.monnetproject.parser.combinator.impl.AlternativeParser;
import eu.monnetproject.parser.combinator.impl.ConversionParser;
import eu.monnetproject.parser.combinator.impl.DropLeftSequentialParser;
import eu.monnetproject.parser.combinator.impl.DropRightSequentialParser;
import eu.monnetproject.parser.combinator.impl.FileInput;
import eu.monnetproject.parser.combinator.impl.LiteralParser;
import eu.monnetproject.parser.combinator.impl.NewLineParser;
import eu.monnetproject.parser.combinator.impl.OptionalParser;
import eu.monnetproject.parser.combinator.impl.RegexParser;
import eu.monnetproject.parser.combinator.impl.RepetitionParser;
import eu.monnetproject.parser.combinator.impl.SequentialCompositionParser;
import eu.monnetproject.parser.combinator.impl.StringInput;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Simple combinator parsers, that work for LL(1) grammars. This is based on the
 * similar class in scala.util.parsing.combinator
 * 
 * @author John McCrae
 */
public abstract class Parser<E> {
    
    /**
     * Create a parser that accepts only a single literal.
     * @param str The string
     * @return The parser
     */
    public static Parser<String> literal(String str) {
        return new LiteralParser(str);
    }
    
    /**
     * Create a parser that accepts the regex
     * @param str The regular expression
     * @return The parser
     */
    public static Parser<String> regex(String str) {
        return new RegexParser(str);
    }
    
    /**
     * Create a parser that accepts a new line. Note that this parser is blocking,
     * in the sense that it will be impossible to backtrack from this if it fails. i.e.,
     * The following parser ln().or(literal("x")) will not match the input "x", however
     * literal("x").or(ln()) will.
     * @return 
     */
    public static Parser<String> ln() {
        return new NewLineParser();
    }
    
    /**
     * Apply this parser followed by the given parser
     * @param parser The second parser
     * @return A parser matching both this and the second parser in sequence
     */
    public <F> Parser<Then<E,F>> then(Parser<F> parser) {
        return new SequentialCompositionParser(this, parser);
    }
    
    /**
     * Apply this parser followed by the given parser, but the result will not
     * contain the object matched by this parser
     * @param <F> The type of the second parser
     * @param parser The second parser
     * @return A parser matching this and the second parser in sequence, but discards the result of this parser
     */
    public <F> Parser<F> dropLeft(Parser<F> parser) {
        return new DropLeftSequentialParser<F>(this, parser);
    }
    
    /**
     * Apply this parser followed by the given parser, but the result will not
     * contain the object matched by the other parser
     * @param parser The second parser
     * @return A parser matching this and the second parser in sequence, but discards the result of the passed parser
     */
    public Parser<E> dropRight(Parser parser) {
        return new DropRightSequentialParser(this, parser);
    }
    
    /**
     * Return a parser that matches either this parser of the given parser
     * @param parser The alternative parser
     * @return A parser matching either this parser or the passed parser
     */
    public Parser<E> or(Parser<E> parser) {
        return new AlternativeParser<E>(this, parser);
    }
    
    /**
     * Return a parser that optionally matches as this parser
     */
    public Parser<E> opt() {
        return new OptionalParser<E>(this);
    }
    
    /**
     * Return a parser that matches this parser repeated at least once
     */
    public Parser<List<E>> rep() {
        return new RepetitionParser<E>(this,1,Integer.MAX_VALUE);
    }
    
    /**
     * Return a parser that matches this parser repeated any number of times (including zero)
     */
    public Parser<List<E>> repOpt() {
        return new RepetitionParser<E>(this,0,Integer.MAX_VALUE);
    } 
    
    /**
     * Return a parser that matches this parser repeated at least min times and at most max times.
     * Note the following calls are equivalent: <br/>
     *   literal(x).opt() == literal(x).rep(0,1) <br/>
     *   literal(x).rep() == literal(x).rep(1,Integer.MAX_VALUE) <br/>
     *   literal(x).repOpt() == literal(x).rep(0,Integer.MAX_VALUE) <br/>
     * @param min The minimum number of times to match
     * @param max The maximum number of times to match
     */
    public Parser<List<E>> rep(int min, int max) {
        return new RepetitionParser<E>(this,min,max);
    }
    
    /**
     * Return a parser that matches the same input as this parser, but maps the resulting object
     * @param <F> The target type
     * @param mapper The mapping SAM interface
     */
    public <F> Parser<F> map(ParseMap<E,F> mapper) {
        return new ConversionParser<E, F>(this, mapper);
    }
    
    /**
     * (EXPERT) Override this to create new parsers
     * @param input The input
     * @return The result, must be one of Success or Failure
     * @throws ParseException If something went wrong, generally a bad new line rule or an EOF
     */
    public abstract ParserResult<E> accept(Input input) throws ParseException;
    
    /**
     * Parse a string
     * @param s The string to parser
     * @return The result of the parsing
     * @throws ParseException If the parse failed
     */
    public E parse(String s) throws ParseException {
        ParserResult<E> res = accept(new StringInput(s));
        if(res instanceof Success) {
            return res.getObject();
        } else {
            throw new ParseException(res.getMessage());
        }
    }
    
    /**
     * Parse a file
     * @param f The file
     * @return The result of parsing
     * @throws ParseException If the parse failed
     * @throws IOException If an error occurred reading the file
     */
    public E parse(File f) throws ParseException, IOException {
        ParserResult<E> res = accept(new FileInput(f));
        if(res instanceof Success) {
            return res.getObject();
        } else {
            throw new ParseException(res.getMessage());
        }
    }
}
