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

import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonModel;
import java.net.URI;
import java.util.Set;

/**
 * The configuration object for a lemon generation run
 *
 * @author John McCrae
 */
public final class LemonGeneratorConfig {

    public static String modelContext(LemonModel model) {
        if (model == null || model.getContext() == null) {
            return "unknown:";
        } else {
            String mcont = model.getContext().toString();
            while (mcont.matches(".*#.*|.*/")) {
                if (mcont.matches(".*#.*")) {
                    mcont = mcont.substring(0, mcont.lastIndexOf("#"));
                } else {
                    mcont = mcont.substring(0, mcont.lastIndexOf("/"));
                }
            }
            return mcont;
        }
    }
    
    public final LemonElementNamer namer(LemonModel model) {
        final String lexiconPattern = lexiconNamingPattern != null ? 
                lexiconNamingPattern : modelContext(model)+"/${lexiconName}";
        final String entryPattern = entryNamingPattern != null ?
                entryNamingPattern : modelContext(model)+"/${lexiconName}/${entryName}";
        final String otherPattern = otherNamingPattern != null ?
                otherNamingPattern : modelContext(model) + "/${lexiconName}/${entryName}#${identifier}";
        return new LemonElementNamer(lexiconPattern, entryPattern, otherPattern);
    }
    
    /**
     * Pattern for naming lexica
     */
    public String lexiconNamingPattern = null;
    /**
     * Pattern for naming entries
     */
    public String entryNamingPattern = null;
    /**
     * Pattern for naming other elements of the model
     */
    public String otherNamingPattern = null;
    
    /**
     * The name of the lexicon
     */
    public String lexiconName = "lexicon";
    /**
     * A custom URI to use to extract labels
     */
    public URI customLabel = null;
    /**
     * Extract labels from URIs?
     */
    public boolean useDefaultLEP = true;
    /**
     * Guess language
     */
    public boolean inferLang = true;
    /**
     * If non-null generate for only the specific language
     */
    public Set<Language> languages = null;
    /**
     * Default language if not specified in the ontology
     */
    public Language unlanged = Language.ENGLISH;
    ///**
    // * The model, null for default
    // */
    //public LemonModel model = null;

    @Override
    public String toString() {
        return "LemonGeneratorConfig{" + "lexiconNamingPattern=" + lexiconNamingPattern + ", entryNamingPattern=" + entryNamingPattern + ", otherNamingPattern=" + otherNamingPattern + ", lexiconNamePrefix=" + lexiconName + ", customLabel=" + customLabel + ", useDefaultLEP=" + useDefaultLEP + ", inferLang=" + inferLang + ", languages=" + languages + ", unlanged=" + unlanged + '}';
    }

    
}
