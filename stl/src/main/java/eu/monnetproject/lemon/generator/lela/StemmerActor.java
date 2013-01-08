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

import eu.monnetproject.pos.POSToken;
import eu.monnetproject.lang.*;
import eu.monnetproject.lemon.*;
import eu.monnetproject.lemon.generator.*;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Text;
import eu.monnetproject.morph.*;
import eu.monnetproject.pos.*;
import java.util.*;
import eu.monnetproject.util.Logging;
import eu.monnetproject.util.Logger;

/**
 * The actor that stems and lemmatizes words
 * @author John McCrae
 */
public class StemmerActor implements GeneratorActor {
    private final StemmerFactory stemmerFactory;
    //private final HashMap<Stemmer,Integer> stemmerRanking = new HashMap<Stemmer,Integer>();
    private static final POSTag undefinedTag = new POSTagImpl("XX");
    private Logger log = Logging.getLogger(this);

    public StemmerActor(StemmerFactory stemmerFactory) {
        this.stemmerFactory = stemmerFactory;
    }

    @Override
    public double getPriority() {
        return 35;
    }

    @Override
    public void perform(LexicalEntry entry, GenerationState state) {
        final LemonModel model = state.getModel();
        final Language targetLanguage = state.getLanguage();
        final LemonFactory factory = model.getFactory();
        Stemmer stemmer = stemmerFactory.getStemmer(targetLanguage);
        if (stemmer == null) {
            log.warning("Stemmer Actor: Unsupported language (" + targetLanguage + ")");
            state.report(new ActorGenerationReportImpl("Stemmer", ActorGenerationReport.Status.FAILED, "Unsupported language " + targetLanguage));
            return;
        }
        if (entry.getDecompositions().isEmpty()) {
            if (entry.getCanonicalForm() == null) {
                boolean lemmaFound = false;
                String lemmaForm = null;
                POSTag posTag = undefinedTag;
                Collection<PropertyValue> tags = entry.getProperty(TaggerActor.tagURI);
                if (!tags.isEmpty()) {
                    posTag = new POSTagImpl(tags.iterator().next().getURI().getFragment());
                }
                log.config("Stemmer actor:" + posTag.getPOS().getValue());
                POSToken tk = new POSTokenImpl(entry.getForms().iterator().next().getWrittenRep().value, posTag);
                if (stemmer != null) {
                    lemmaForm = stemmer.stem(tk).getLemma();
                }
                for (LexicalForm form : entry.getForms()) {
                    if (lemmaForm != null
                            && form.getWrittenRep().value.equalsIgnoreCase(lemmaForm)) {
                        entry.removeForm(form);
                        entry.setCanonicalForm(form);
                        log.config("Stemmer Actor: Moving to canonical " + form.getWrittenRep().value);
                        lemmaFound = true;
                        state.report(new ActorGenerationReportImpl("Stemmer", ActorGenerationReport.Status.OK, "Set canonical to " + lemmaForm));
                    } else if (form.getWrittenRep().value.equalsIgnoreCase(tk.getValue())) {
                        entry.removeForm(form);
                        entry.addOtherForm(form);
                        log.config("Stemmer Actor: Moving to other " + form.getWrittenRep().value);
                        state.report(new ActorGenerationReportImpl("Stemmer", ActorGenerationReport.Status.OK, "Moving form to other"));
                    }
                }
                if (!lemmaFound && lemmaForm != null) {
                    LexicalForm canForm = factory.makeForm(state.namer().name(state.getLexiconName(), state.getEntryName(), "form"));
                    canForm.setWrittenRep(new Text(LabelExtractorActor.lowerCaseFirst(lemmaForm), targetLanguage.toString()));
                    entry.setCanonicalForm(canForm);
                    state.report(new ActorGenerationReportImpl("Stemmer", ActorGenerationReport.Status.OK, "Moved other form to canonical"));
                } 
            } else {
                state.report(new ActorGenerationReportImpl("Stemmer", ActorGenerationReport.Status.NO_INFO, "No canonical form"));
            }
        } else {
            state.report(new ActorGenerationReportImpl("Stemmer", ActorGenerationReport.Status.NO_INFO, "No decomposition"));
        }
    }

    public LemonModel getAuxiliaryLexicon() {
        return null;
    }

    private static class POSTagImpl implements POSTag {

        private final POS pos;

        public POSTagImpl(String pos) {
            this.pos = new POSImpl(pos);
        }

        @Override
        public POS getPOS() {
            return pos;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<SynPair> getSynProps() {
            return Collections.EMPTY_LIST;
        }
    }

    private static class POSImpl implements POS {

        private final String pos;

        public POSImpl(String pos) {
            this.pos = pos;
        }

        @Override
        public String getValue() {
            return pos;
        }

        @Override
        public POSSet getPOSSet() {
            return null;
        }
    }

    private static class POSTokenImpl implements POSToken {

        private final String value;
        private final POSTag tag;

        public POSTokenImpl(String value, POSTag tag) {
            this.value = value;
            this.tag = tag;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Deprecated
        @Override
        public String getLemma() {
            return value;
        }

        @Override
        public POSTag getPOSTag() {
            return tag;
        }
    }
}
