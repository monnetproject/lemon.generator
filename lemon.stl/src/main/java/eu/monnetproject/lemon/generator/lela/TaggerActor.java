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
import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.URIElement;
import eu.monnetproject.lemon.URIValue;
import eu.monnetproject.lemon.generator.ActorGenerationReport.Status;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Text;
import java.net.URI;
import eu.monnetproject.pos.POSToken;
import eu.monnetproject.tagger.Tagger;
import eu.monnetproject.tagger.TaggerFactory;
import eu.monnetproject.tokens.Token;
import eu.monnetproject.util.Logging;
import eu.monnetproject.util.Logger;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * The actor that tags the words
 * @author John McCrae
 */
public class TaggerActor implements GeneratorActor {

    private final TaggerFactory taggerFactory;// = Services.getFactory(TaggerFactory.class);
    static final URIProperty tagURI = new URIProperty(URI.create("jar:eu.monnetproject.lemon.lela#posTag"));
    static final String tagPrefix = "jar:eu.monnetproject.lemon.lela#";
    private static final Logger log = Logging.getLogger(TaggerActor.class);

    public TaggerActor(TaggerFactory taggerFactory) {
        this.taggerFactory = taggerFactory;
    }


    @Override
    public double getPriority() {
        return 30;
    }

    public LemonModel getAuxiliaryLexicon() {
        return null;
    }

    public static void augmentEntry(LexicalEntry entry2, Map<URI, Collection<URI>> extraProps, POSToken tk, LemonFactory factory) {
        log.config("Tagger Actor: Augmenting " + entry2.getURI());
        if (entry2.getCanonicalForm() == null) {
            for (LexicalForm form : entry2.getForms()) {
                if (form.getWrittenRep().value.equalsIgnoreCase(tk.getValue())) {
                    for (URI prop : extraProps.keySet()) {
                        for (URI val : extraProps.get(prop)) {
                            form.addProperty(prop(prop), val(val));
                        }
                    }
                    log.config("Tagger Actor: Adding tag " + tk.getPOSTag().getPOS().getValue());
                    entry2.addProperty(tagURI, val(URI.create("jar:eu.monnetproject.lemon.lela#"
                            + tk.getPOSTag().getPOS().getValue())));
                }
            }
        }
    }

    @Override
    public void perform(LexicalEntry entry, GenerationState state) {
        final LemonModel model = state.getModel();
        final Language targetLanguage = state.getLanguage();
        final LemonFactory factory = model.getFactory();
        log.config("Tagger Actor: Starting...");
        Tagger tagger;
        try {
            tagger = taggerFactory.makeTagger(targetLanguage);
            if (tagger == null) {
                log.warning("Tagger Actor: Unsupported language (" + targetLanguage + ")");
                state.report(new ActorGenerationReportImpl("Tagger", Status.FAILED, "Unsupported language " + targetLanguage));
                return;
            }
        } catch (Exception ex) {
            log.warning("Tagger Actor: Unsupported language (" + targetLanguage + ")");
            state.report(new ActorGenerationReportImpl("Tagger", Status.EXCEPTION, "Exception during tagger creation"));
            return;
        }
        String tagSet = tagger.getTagSet();
        TagConverter tagConverter = TagConverter.getConverter(targetLanguage, tagSet, state.getLingOnto());
        if (tagConverter == null) {
            log.warning("No tag converter for tagset " + tagSet);
            state.report(new ActorGenerationReportImpl("Tagger", Status.FAILED, "No tag converter"));
            return;
        }
        if (!entry.getProperty(tagURI).isEmpty()) {
            log.config("Tagger Actor: Already tagged");
            state.report(new ActorGenerationReportImpl("Tagger", Status.UNNECESSARY, "Already tagged"));
            return;
        }
        List<Token> tokens = new LinkedList<Token>();
        List<Component> components = null;
        if (!entry.getDecompositions().isEmpty()) {
            components = entry.getDecompositions().iterator().next();
            for (Component comp : components) {
                LexicalEntry entry2 = comp.getElement();
                Text text = TokenizerActor.getForm(entry2, targetLanguage.getScript());
                if (text == null) {
                    continue;
                }
                tokens.add(new SimpleToken(text.value));

            }
        } else {
            Text text = TokenizerActor.getForm(entry, targetLanguage.getScript());
            if (text == null) {
                log.warning("Tagger Actor: Null text");
                state.report(new ActorGenerationReportImpl("Tagger", Status.EXCEPTION, "Tokenizer returned null"));
                return;
            }
            tokens.add(new SimpleToken(text.value));
        }

        List<POSToken> taggedTokens = tagger.tag(tokens);
        log.config("Tagger Actor: Got " + taggedTokens.size() + " tokens");
        int j = 0;
        for (POSToken tk : taggedTokens) {
            if (components != null) {
                Component comp = components.get(j);

                Map<URI, Collection<URI>> extraProps = tagConverter.getFormProperties(tk.getPOSTag());
                for (URI prop : extraProps.keySet()) {
                    for (URI val : extraProps.get(prop)) {
                        comp.addProperty(prop(prop), val(val));
                    }
                }

                LexicalEntry entry2 = comp.getElement();
                augmentEntry(entry2, extraProps, tk, factory);
            } else {
                augmentEntry(entry, tagConverter.getFormProperties(tk.getPOSTag()), tk, factory);
                Map<URI, Collection<URI>> entryProps = tagConverter.getEntryProperties(tk.getPOSTag());
                for (URI prop : entryProps.keySet()) {
                    for (URI val : entryProps.get(prop)) {
                        entry.addProperty(prop(prop), val(val));
                    }
                }
            }
        }
        if(taggedTokens.size() > 0) {
            state.report(new ActorGenerationReportImpl("Tagger", Status.OK, "Added tags"));
        } else {
            state.report(new ActorGenerationReportImpl("Tagger", Status.EXCEPTION, "Tagger returned empty tag list"));
        }
    }
    private static HashMap<URI, Property> props = new HashMap<URI, Property>();

    public static Property prop(URI uri) {
        if (props.containsKey(uri)) {
            return props.get(uri);
        } else {
            Property p = new URIProperty(uri);
            props.put(uri, p);
            return p;
        }
    }
    private static HashMap<URI, PropertyValue> vals = new HashMap<URI, PropertyValue>();

    public static PropertyValue val(URI uri) {
        if (vals.containsKey(uri)) {
            return vals.get(uri);
        } else {
            PropertyValue p = new URIPropertyValue(uri);
            vals.put(uri, p);
            return p;
        }
    }
}
class URIProperty extends URIValue implements Property {

    public URIProperty(URI uri) {
        super(uri);
    }
}

class URIPropertyValue extends URIElement implements PropertyValue {

    public URIPropertyValue(URI uri) {
        super(uri);
    }
}

class SimpleToken implements Token {

    final String val;

    public SimpleToken(String val) {
        this.val = val;
    }

    public List<Token> getChildren() {
        return null;
    }

    @Override
    public String getValue() {
        return val;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimpleToken other = (SimpleToken) obj;
        if ((this.val == null) ? (other.val != null) : !this.val.equals(other.val)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.val != null ? this.val.hashCode() : 0);
        return hash;
    }
}
