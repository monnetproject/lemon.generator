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
package eu.monnetproject.lemon.stl.web;

import eu.monnetproject.label.LabelExtractorFactory;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.generator.GenerationReport;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.generator.LemonGenerator;
import eu.monnetproject.lemon.generator.LemonGeneratorConfig;
import eu.monnetproject.lemon.generator.LemonGeneratorListener;
import eu.monnetproject.lemon.generator.lela.LeLAManager;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.ontology.Ontology;
import java.util.Random;

/**
 *
 * @author John McCrae
 */
public class LemonGenerationFuture implements Runnable {
    private final Ontology ontology;
    private final LemonGeneratorConfig config;
    private final Iterable<GeneratorActor> generatorActors;
    private final LabelExtractorFactory lef;
    private final String id = "g" + Math.abs(new Random().nextLong());
    private LemonGenerator generator;
    private final Listener listener = new Listener();
    
    public LemonGenerationFuture(Ontology ontology, LemonGeneratorConfig config, Iterable<GeneratorActor> generatorActors, LabelExtractorFactory lef) {
        this.ontology = ontology;
        this.config = config;
        this.generatorActors = generatorActors;
        this.lef = lef;
    }

    public String id() {
        return id;
    }
    
    private LemonModel model;
    
    @Override
    public void run() {
        generator = new LeLAManager(generatorActors, lef);
        generator.addListener(listener);
        model = generator.doGeneration(ontology, config);
    }

    public LemonModel getModel() {
        return model;
    }
    
    
    public String lastMessage() {
        return listener.lastMessage;
    }
    
    public float lastProgress() {
        return listener.lastProgress;
    }
    
    public GenerationReport report() {
        return listener.report;
    }

    private class Listener implements LemonGeneratorListener {
        public String lastMessage = "Starting";
        public float lastProgress = 0.0f; 
        public GenerationReport report;
        
        @Override
        public void entryGenerated(LexicalEntry lexicalEntry, float progress) {
            lastMessage = "LexicalEntry: " + lexicalEntry.getURI();
            lastProgress = progress;
        }

        @Override
        public void lexiconAdded(Lexicon lexicon) {
            lastMessage  = "Lexicon: " + lexicon.getURI();
        }

        @Override
        public void onComplete(GenerationReport report) {
            lastProgress = 1.0f;
            this.report = report;
        }
        
    }
    
}
