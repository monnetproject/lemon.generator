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

import eu.monnetproject.tokens.Token;
import eu.monnetproject.lemon.generator.*;
import eu.monnetproject.lemon.*;
import eu.monnetproject.lang.*;
import eu.monnetproject.lemon.model.Component;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Text;
import eu.monnetproject.tokenizer.*;
import java.util.*;
import eu.monnetproject.util.Logging;
import eu.monnetproject.util.Logger;

/**
 * The actor that tokenizes the string into words
 * @author John McCrae
 */
public class TokenizerActor implements GeneratorActor {

    private final TokenizerFactory tokenizerFactory;
    private Logger log = Logging.getLogger(this);

    public TokenizerActor(TokenizerFactory tokenizerFactory) {
        this.tokenizerFactory = tokenizerFactory;
    }

    @Override
    public double getPriority() {
        return 10;
    }

    private static boolean checkScript(Text text, Script targetScript) {
        return targetScript == null
                || (targetScript == Script.LATIN && Language.get(text.language).getScript() == null)
                || targetScript.equals(Language.get(text.language).getScript());
    }

    static Text getForm(LexicalEntry entry, Script targetScript) {
        if (entry.getCanonicalForm() != null) {
            if (entry.getCanonicalForm().getWrittenRep() != null) {
                Text text = entry.getCanonicalForm().getWrittenRep();
                if (checkScript(text, targetScript)) {
                    return text;
                } else {
                    return null;
                }
            } else {
                for (Collection<Text> texts : entry.getCanonicalForm().getRepresentations().values()) {
                    for (Text text : texts) {
                        if (checkScript(text, targetScript)) {
                            return text;
                        }
                    }
                }
            }
        } else {
            for (LexicalForm form : entry.getForms()) {
                if (form.getWrittenRep() != null) {
                    Text text = form.getWrittenRep();
                    if (checkScript(text, targetScript)) {
                        return text;
                    }
                } else {
                    for (Collection<Text> texts : form.getRepresentations().values()) {
                        for (Text text : texts) {
                            if (checkScript(text, targetScript)) {
                                return text;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void perform(LexicalEntry entry, GenerationState state) {
        final LemonModel model = state.getModel();
        final Language language = state.getLanguage();
        final LemonFactory factory = model.getFactory();
        //log.info("Tokenizer Actor: START");
        if (!entry.getDecompositions().isEmpty()) {
            log.info("Already tokenized skipping");
            state.report(new ActorGenerationReportImpl("Tokenizer", ActorGenerationReport.Status.UNNECESSARY, "Already tokenized"));
            return;
        }
        Script script = language.getScript();
        if (script == null) {
            Script[] knownScripts = Script.getKnownScriptsForLanguage(language);
            if (knownScripts != null) {
                //log.info("Script for " + language + " is " + knownScripts[0]);
                script = knownScripts[0];
            }
        }
        if (script == null) {
            log.warning("Could not deduce script for language " + language + " defaulting to Latin");
            state.report(new ActorGenerationReportImpl("Tokenizer", ActorGenerationReport.Status.FAILED, "Script defaulted to Latin"));
            script = Script.LATIN;
        }
        Tokenizer tokenizer = tokenizerFactory.getTokenizer(script);
        if (tokenizer == null) {
            log.warning("No support for script " + script);
            state.report(new ActorGenerationReportImpl("Tokenizer", ActorGenerationReport.Status.FAILED, "No support for script " + script));
            return;
        }
        Text text = getForm(entry, script);
        if (text == null) {
            state.report(new ActorGenerationReportImpl("Tokenizer", ActorGenerationReport.Status.NO_INFO, "Could not get form"));
            return;
        }
        String label = text.value;


        List<Token> tokens = tokenizer.tokenize(label);
        //log.info("Tokenizer Actor: Tokenized!");
        List<Component> components;
        if (!tokens.isEmpty()) {

            if (tokens.size() > 1) {
                components = new LinkedList<Component>();

                for (Token tk : tokens) {
                    if (tk.getValue().matches("\\W*")
                            || tk.getValue().matches("_+")) {
                        continue;
                    }
                    log.info("Tokenizer Actor: Adding " + tk.getValue());
                    Component comp = factory.makeComponent(state.namer().name(state.getLexiconName(), state.getEntryName(), "comp"));
                    //log.info("made component");
                    components.add(comp);
                    //log.info("added component");
                    LexicalEntry entry2 = state.addLexicalEntry(tk.getValue(), language);
                    //log.info("added entry for element");
                    comp.setElement(entry2);
                    //log.info("setted element");
                }
                //log.info("Adding decomposition");
                entry.addDecomposition(components);
                state.report(new ActorGenerationReportImpl("Tokenizer", ActorGenerationReport.Status.OK, "Adding decomposition"));
                
                if (entry.getCanonicalForm() == null) {
                    LexicalForm form = entry.getForms().iterator().next();
                    entry.removeForm(form);
                    entry.setCanonicalForm(form);
                }
            } else {
                state.report(new ActorGenerationReportImpl("Tokenizer", ActorGenerationReport.Status.UNNECESSARY, "Single word"));
            }
        } else {
            state.report(new ActorGenerationReportImpl("Tokenizer", ActorGenerationReport.Status.EXCEPTION, "Tokenizer returned empty list"));
        }
        //log.info("Tokenizer Actor: END");
    }

    public LemonModel getAuxiliaryLexicon() {
        return null;
    }
}
