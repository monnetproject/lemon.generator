package eu.monnetproject.lemon.stl;

import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.generator.ActorGenerationReport;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.LemonElementNamer;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.ontology.Entity;

class GenerationStateImpl implements GenerationState {
    private final Language language;
    private final LemonModel model;

    public GenerationStateImpl(Language language, LemonModel model) {
        this.language = language;
        this.model = model;
    }

    @Override
    public Language getLanguage() {
        return language;
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
    public Lexicon getLexicon() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LexicalEntry addLexicalEntry(String string, Language lng) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public LemonElementNamer namer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LinguisticOntology getLingOnto() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void report(ActorGenerationReport report) {
        System.err.println(report.toString());
    }
    
    
}