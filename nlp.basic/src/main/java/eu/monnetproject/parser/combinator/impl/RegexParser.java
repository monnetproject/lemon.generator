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
package eu.monnetproject.parser.combinator.impl;

import eu.monnetproject.parser.combinator.Failure;
import eu.monnetproject.parser.combinator.Input;
import eu.monnetproject.parser.combinator.ParseException;
import eu.monnetproject.parser.combinator.Parser;
import eu.monnetproject.parser.combinator.ParserResult;
import eu.monnetproject.parser.combinator.Success;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author John McCrae
 */
public class RegexParser extends Parser<String> {
    private final Pattern pattern;

    public RegexParser(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }
    
    
    @Override
    public ParserResult<String> accept(Input input) {
        final Matcher matcher = pattern.matcher(input.asCharSequence());
        if(matcher.lookingAt()) {
            try {
                return new Success(input.advance(matcher.end()),input.nextN(matcher.end()));
            } catch(ParseException x) {
                x.printStackTrace();
                return new Failure<String>(input,"Internal error");
            }
        } else {
            return new Failure(input,"Expected regular expression \"" + pattern.pattern() + "\"");
        }
    }

    @Override
    public String toString() {
        return "RegexParser{" + "pattern=" + pattern.pattern() + '}';
    }
    
}
