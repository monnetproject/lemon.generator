/**
 * ********************************************************************************
 * Copyright (c) 2011, Monnet Project All rights reserved.
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
 ********************************************************************************
 */
package eu.monnetproject.lemon.generator.lela;

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.generator.ActorGenerationReport.Status;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.MorphPattern;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.util.ResourceFinder;
import eu.monnetproject.util.Logger;
import eu.monnetproject.util.Logging;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 *
 * @author John McCrae
 */
public class MorphPatternActor implements GeneratorActor {

    private final Logger log = Logging.getLogger(this);

    @Override
    public void perform(LexicalEntry le, GenerationState state) {
        final HashSet<MorphPattern> patterns = new HashSet<MorphPattern>();
        // Load avail morph pattersn
        if (state.getLexicon().getPatterns().isEmpty()) {
            final Reader resource;
            try {
                resource = ResourceFinder.getResourceAsReader("patterns/" + state.getLanguage().getLanguageOnly() + ".lemon.ttl");

                if (resource != null) {
                    final LemonModel model = LemonSerializer.newInstance().read(resource);
                    if (model != null) {
                        patterns.addAll(model.getPatterns());
                        if (patterns == null || patterns.isEmpty()) {
                            state.report(new ActorGenerationReportImpl("Morph Pattern", Status.NO_INFO, "Lexicon has no patterns"));
                            throw new RuntimeException();
                        } 
                    }
                }
            } catch (IOException ex) {
                log.stackTrace(ex);
                state.report(new ActorGenerationReportImpl("Morph Pattern", Status.EXCEPTION, "IOException: " + ex.getMessage()));
                return;
            }

        }
        /*
         * try { log.info("Checking for patterns " +
         * le.getProperty(state.getLingOnto().getProperty("partOfSpeech")).iterator().next().getURI().toString());
         * } catch(Exception x){ log.info("Checking for pattern NOPOS");
        }
         */
        //log.warning(le.getDecompositions().isEmpty() + " " + le.getPatterns().isEmpty());
        if (le.getPatterns().isEmpty() && le.getDecompositions().isEmpty()) {
       //     boolean success = false;
            // Check patterns in lexicon
            PATTERNS:
            for (MorphPattern pattern : patterns) {
                for (Entry<Property, Collection<PropertyValue>> props : pattern.getPropertys().entrySet()) {
                    if (!le.getProperty(props.getKey()).containsAll(props.getValue())) {
//                        try {
//                        log.warning(le.getProperty(state.getLingOnto().getProperty("partOfSpeech")).iterator().next().getURI().toString()+ "!=" +
//                                props.getValue().iterator().next().getURI().toString());
//                        } catch(Exception x) {
//                            log.warning("not there");
//                        }
                        continue PATTERNS;
                    }
                }
//                log.info("Adding pattern " + pattern.getURI());
        //        success = true;
                state.report(new ActorGenerationReportImpl("Morph Pattern", Status.OK, "Adding pattern: " + pattern.getURI()));
                le.addPattern(pattern);
                return;
            }
            //if (!success) {
                state.report(new ActorGenerationReportImpl("Morph Pattern", Status.FAILED, "No pattern was added"));
      //      }
        } else {
            state.report(new ActorGenerationReportImpl("Morph Pattern", Status.UNNECESSARY, "Entry already has pattern"));
            // Skipping due to existing pattern
        }
    }

    @Override
    public double getPriority() {
        return 35;
    }
}
