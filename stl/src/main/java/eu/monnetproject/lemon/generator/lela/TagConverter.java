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
package eu.monnetproject.lemon.generator.lela;

import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.util.ResourceFinder;
import eu.monnetproject.pos.POSTag;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author John McCrae
 */
public class TagConverter {

    private final HashMap<String, HashMap<URI, Collection<URI>>> formProperties = new HashMap<String, HashMap<URI, Collection<URI>>>();
    private final HashMap<String, HashMap<URI, Collection<URI>>> entryProperties = new HashMap<String, HashMap<URI, Collection<URI>>>();
    private final String tagSet;

    public TagConverter(String tagSet) {
        this.tagSet = tagSet;
    }

    private static Map<String,TagConverter> converters = new HashMap<String, TagConverter>();
    
    public static TagConverter getConverter(Language lang, String tagSet, LinguisticOntology lingOnto) {
        final String key = lang + tagSet;
        if(converters.containsKey(key)) {
            return converters.get(key);
        }
        try {
            final Reader entries = ResourceFinder.getResourceAsReader("tagconversions/" + lang + "." + tagSet + ".entries");
            final Reader forms = ResourceFinder.getResourceAsReader("tagconversions/" + lang + "." + tagSet + ".forms");
            if (entries == null || forms == null) {
                return null;
            }
            final TagConverter tagConverter = new TagConverter(tagSet);
            final BufferedReader entryIn = new BufferedReader(entries);
            String s;
            while ((s = entryIn.readLine()) != null) {
                if (s.matches("\\s*") || s.startsWith("//")) {
                    continue;
                }
                final String[] split1 = s.split("=>");
                if (split1.length != 2) {
                    throw new ConverterFormatException("non empty line without =>");
                }
                final HashMap<URI, Collection<URI>> map = new HashMap<URI, Collection<URI>>();
                for (String prop : split1[1].split(",")) {
                    final String[] split = prop.split("=");
                    if (split.length != 2) {
                        throw new ConverterFormatException("property lacks =");
                    }
                    final URI propURI = lingOnto.getProperty(split[0]).getURI();
                    final URI valURI = lingOnto.getPropertyValue(split[1].replaceAll("\\s", "")).getURI();
                    if (!map.containsKey(propURI)) {
                        map.put(propURI, new LinkedList<URI>());
                    }
                    map.get(propURI).add(valURI);
                }
                tagConverter.entryProperties.put(split1[0], map);
            }
            final BufferedReader formIn = new BufferedReader(entries);
            while ((s = formIn.readLine()) != null) {
                if (s.matches("\\s*") || s.startsWith("//")) {
                    continue;
                }
                final String[] split1 = s.split("=>");
                if (split1.length != 2) {
                    throw new ConverterFormatException("non empty line without =>");
                }
                final HashMap<URI, Collection<URI>> map = new HashMap<URI, Collection<URI>>();
                for (String prop : split1[1].split(",")) {
                    final String[] split = prop.split("=");
                    if (split.length != 2) {
                        throw new ConverterFormatException("property lacks =");
                    }
                    final URI propURI = lingOnto.getProperty(split[0]).getURI();
                    final URI valURI = lingOnto.getPropertyValue(split[1].replaceAll("\\s", "")).getURI();
                    if (!map.containsKey(propURI)) {
                        map.put(propURI, new LinkedList<URI>());
                    }
                    map.get(propURI).add(valURI);
                }
                tagConverter.formProperties.put(split1[0], map);
            }
            converters.put(key, tagConverter);
            return tagConverter;
        } catch (IOException x) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<URI, Collection<URI>> getFormProperties(POSTag tag) {
        final HashMap<URI, Collection<URI>> rv = formProperties.get(tag.getPOS().getValue());
        if(rv != null) {
            return rv;
        } else {
            return Collections.EMPTY_MAP;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<URI, Collection<URI>> getEntryProperties(POSTag tag) {
        final HashMap<URI, Collection<URI>> rv = entryProperties.get(tag.getPOS().getValue());
        
        if(rv != null) {
            return rv;
        } else {
            return Collections.EMPTY_MAP;
        }
    }

    public String getTagSet() {
        return tagSet;
    }
}
