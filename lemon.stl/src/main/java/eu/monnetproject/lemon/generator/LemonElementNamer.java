/**
 * **************************************************************************
 * /* Copyright (c) 2011, Monnet Project All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Monnet Project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ******************************************************************************
 */
package eu.monnetproject.lemon.generator;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

/**
 * Interface for ensuring elements are named consistently
 *
 * @author John McCrae
 */
public class LemonElementNamer {

    private final String lexiconPattern;
    private final String entryPattern;
    private final String otherPattern;

    /**
     * Create a new lemon element namer
     *
     * @param lexiconPattern The pattern for naming lexica, e.g.,
     * http://server.com/${lexiconName}
     * @param entryPattern The pattern for naming lexical entries, e.g.,
     * http://server.com/${lexiconName}/${entryName}
     * @param otherPattern The pattern for naming lexica, e.g.,
     * http://server.com/${lexiconName}/${entryName}#${identifier}
     */
    public LemonElementNamer(String lexiconPattern, String entryPattern, String otherPattern) {
        this.lexiconPattern = lexiconPattern;
        this.entryPattern = entryPattern;
        this.otherPattern = otherPattern;
    }

    public static LemonElementNamer defaultForURL(String url) {
        final String url2 = url.endsWith("/") ? url : url + "/";
        return new LemonElementNamer(url2 + "${lexiconName}",
                url2 + "${lexiconName}/${entryName}",
                url2 + "${lexiconName}/${entryName}#${identifier}");
    }
    private final Set<String> used = new HashSet<String>();

    private URI deduplicate(String s) {
        if (!used.contains(s)) {
            used.add(s);
            return URI.create(s);
        } else {
            int i = 2;
            String s2 = s+"__"+i;
            while (used.contains(s2)) {
                i++;
                s2 = s2.replaceAll("__\\d+$", "");
                s2 = s2 + "__" + i;
            }
            used.add(s2);
            return URI.create(s2);
        }
    }

    /**
     * Name an element
     *
     * @param lexiconName The name of the lexicon
     * @param entryName The name of the entry this element belongs to, e.g.,
     * "cat" (for Lexicon.class this is the language of the lexicon)
     * @param identifier The identifier for element, e.g., "sense1", may be null
     * if clazz is LexicalEntry or Lexicon
     * @return The URI as a string e.g.,
     * http://www.example.com/lexicon#cat/sense1 or null if the element should
     * be blank
     */
    public URI name(String lexiconName, String entryName, String identifier) {
        try {
            if (entryName == null && identifier == null) {
                return deduplicate(lexiconPattern.replaceAll("\\$\\{lexiconName\\}", quote(lexiconName)).
                        replaceAll("\\$\\{entryName\\}", quote(URLEncoder.encode(entryName == null ? "" : entryName, "UTF-8"))).
                        replaceAll("\\$\\{identifier\\}", quote(URLEncoder.encode(identifier == null ? "" : identifier, "UTF-8"))));
            } else if (identifier == null) {
                return deduplicate(entryPattern.replaceAll("\\$\\{lexiconName\\}", quote(lexiconName)).
                        replaceAll("\\$\\{entryName\\}", quote(URLEncoder.encode(entryName == null ? "" : entryName, "UTF-8"))).
                        replaceAll("\\$\\{identifier\\}", quote(URLEncoder.encode(identifier == null ? "" : identifier, "UTF-8"))));
            } else {
                return deduplicate(otherPattern.replaceAll("\\$\\{lexiconName\\}", quote(lexiconName)).
                        replaceAll("\\$\\{entryName\\}", quote(URLEncoder.encode(entryName == null ? "" : entryName, "UTF-8"))).
                        replaceAll("\\$\\{identifier\\}", quote(URLEncoder.encode(identifier == null ? "" : identifier, "UTF-8"))));
            }
        } catch (Exception x) {
            x.printStackTrace();
            throw new RuntimeException(entryPattern + "//" + lexiconName + "//" + entryName + "//" + identifier);

        }
    }

    private static String quote(String s) {
        return s.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$");
    }
}
