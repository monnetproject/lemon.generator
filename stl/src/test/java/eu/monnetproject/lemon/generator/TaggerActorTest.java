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
package eu.monnetproject.lemon.generator;

import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.generator.lela.TaggerActor;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.ontology.Entity;
import eu.monnetproject.tagger.TaggerFactory;
import java.net.URI;
import java.util.Collection;
import net.lexinfo.LexInfo;
import eu.monnetproject.framework.services.Services;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John McCrae
 */
public class TaggerActorTest {

    @Test
    public void testTaggerActor() throws Exception {
        final TaggerActor taggerActor = new TaggerActor(Services.get(TaggerFactory.class));
        final LemonSerializer serializer = LemonSerializer.newInstance();
        final LinguisticOntology lingOnto = new LexInfo();
        final LemonModel model = serializer.create();

        final Lexicon lexicon = model.addLexicon(URI.create("file:test#lexicon_en"), Language.ENGLISH.toString());

        final LexicalEntry entry = LemonModels.addEntryToLexicon(lexicon, URI.create("file:test#yellow"), "yellow", null);

        taggerActor.perform(entry, new State(model, lexicon, lingOnto));
        final Collection<PropertyValue> props = entry.getProperty(lingOnto.getProperty("partOfSpeech"));
        assertFalse(props.isEmpty());
    }

    private static class State implements GenerationState {

        private final LemonModel model;
        private final Lexicon lexicon;
        private final LinguisticOntology lingOnto;

        public State(LemonModel model, Lexicon lexicon, LinguisticOntology lingOnto) {
            this.model = model;
            this.lexicon = lexicon;
            this.lingOnto = lingOnto;
        }

        @Override
        public Lexicon getLexicon() {
            return lexicon;
        }

        @Override
        public LemonModel getModel() {
            return model;
        }

        @Override
        public Entity getEntity() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Language getLanguage() {
            return Language.ENGLISH;
        }

        @Override
        public LexicalEntry addLexicalEntry(String string, Language lng) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public LemonElementNamer namer() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public LinguisticOntology getLingOnto() {
            return lingOnto;
        }

        @Override
        public String getEntryName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getLexiconName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        @Override
        public void report(ActorGenerationReport report) {
            System.err.println(report.toString());
        }
    }
}
