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

import eu.monnetproject.parser.combinator.Input;
import eu.monnetproject.parser.combinator.ParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author John McCrae
 */
public class FileInput implements Input {

    private final BufferedReader reader;
    private final String currentLine;
    private final int offset;

    public FileInput(File file) throws IOException {
        reader = new BufferedReader(new FileReader(file));
        currentLine = reader.readLine();
        offset = 0;
    }

    protected FileInput(BufferedReader reader, String currentLine, int offset) {
        this.reader = reader;
        this.currentLine = currentLine;
        this.offset = offset;
    }

    @Override
    public String nextN(int n) throws ParseException {
        if (n + offset < currentLine.length()) {
            return currentLine.substring(offset, n + offset);
        } else if (n + offset == currentLine.length()) {
            return currentLine.substring(offset, n + offset) + System.getProperty("line.separator");
        } else {
            throw new ParseException("Literal attempting to extend over new line");
        }
    }

    @Override
    public Input advance(int n) throws ParseException {
        if (n + offset < currentLine.length()) {
            return new FileInput(reader, currentLine, offset + n);
        } else {
            throw new ParseException("Extending too far over line");
        }
    }

    @Override
    public CharSequence asCharSequence() {
        return currentLine.substring(offset, currentLine.length()) + System.getProperty("line.separator");
    }

    @Override
    public Input nextLine() throws ParseException {
        if(offset == currentLine.length()) {
            String line;
            try {
                line = reader.readLine();
            } catch (IOException x) {
                throw new ParseException(x.getMessage());
            }
            if (line != null) {
                return new FileInput(reader, line, 0);
            } else {
                throw new ParseException("Unexpected EOF");
            }
        } else {
            throw new ParseException("Expected new line but found " + currentLine.substring(offset,Math.min(offset+5, currentLine.length())));
        }
    }
}
