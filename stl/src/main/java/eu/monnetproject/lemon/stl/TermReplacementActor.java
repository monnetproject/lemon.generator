/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.stl;

import eu.monnetproject.lemon.stl.ReplacementRule;
import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonFactory;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.LexicalSense;
import eu.monnetproject.lemon.model.Text;
import eu.monnetproject.util.Logger;
import eu.monnetproject.util.Logging;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Term Replacement Actor performs a set of term replacement rules on the written form
 * @author tobwun
 */
public class TermReplacementActor implements GeneratorActor {
    
    private Logger log = Logging.getLogger(this);
    private List<ReplacementRule> rules = new LinkedList<ReplacementRule>();
    public void addRule(ReplacementRule rule) {
        rules.add(rule);
    }
    
    @Override
    public void perform(LexicalEntry entry, GenerationState state) {
        //log.info("\n\n"+entry.getURI());
        Collection<LexicalForm> forms = entry.getForms();
        LemonFactory factory = state.getModel().getFactory();
        for(ReplacementRule rule:rules) {
            //log.info("RULE "+rule.toString());
            for(LexicalForm form:forms) {
                String value = form.getWrittenRep().value;
                String lang = form.getWrittenRep().language;
                //log.info("form "+value);
                Pattern pattern = rule.getPattern();
                Matcher m = pattern.matcher(value);
                if (m.matches()) {
                    String newValue = null;
                    if (rule.removeRule())
                        newValue = value.replace(m.group(rule.getSourceGroup()), "");
                    else
                        newValue = value.replace(m.group(rule.getSourceGroup()), m.group(rule.getTargetGroup()));
                    //log.info("group0 "+m.group(0));
                    //log.info("group "+m.group(rule.getSourceGroup()));
                    if (rule.updateForm()) {
                        // update existing lexical entry with new form (not recommended)
                        log.info("update form: "+value+" -> "+newValue);
                        entry.removeForm(form);
                        if(entry.getCanonicalForm() == form) {
                            entry.setCanonicalForm(null);
                        }
                        entry.removeOtherForm(form);
                        entry.removeAbstractForm(form);
                        entry.removeForm(form);
                        form.setWrittenRep(new Text(newValue,lang));
                        entry.addForm(form);
                    } else {
                        // create new lexical entry with modified form
                        try {
                            String uriEntry = "unknown:/lexicon__"+lang+"/"+URLEncoder.encode(newValue, "UTF-8");
                            LexicalEntry newEntry = factory.makeLexicalEntry(URI.create(uriEntry));
                            String uriForm = "unknown:/lexicon__"+lang+"/form/"+URLEncoder.encode(newValue, "UTF-8");
                            LexicalForm newForm = factory.makeForm(URI.create(uriForm));
                            newForm.setWrittenRep(new Text(newValue,lang));
                            newEntry.addForm(newForm);
                            state.getLexicon().addEntry(newEntry);
                            // link senses
                            for(LexicalSense sense:entry.getSenses())
                                newEntry.addSense(sense);
                            log.info("add new entry: "+newValue+" uri="+uriEntry);
                        } catch (UnsupportedEncodingException ex) {
                            java.util.logging.Logger.getLogger(TermReplacementActor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public double getPriority() {
        return 70;
    }

}
