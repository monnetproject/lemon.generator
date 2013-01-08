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

import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.lemon.URIElement;
import eu.monnetproject.lemon.URIValue;
import eu.monnetproject.lemon.generator.ActorGenerationReport;
import eu.monnetproject.lemon.generator.GenerationState;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.lemon.model.Text;
import eu.monnetproject.util.Logging;
import eu.monnetproject.util.Logger;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The actor that removes duplicate entries
 * @author John McCrae
 */
public class MergeActor implements GeneratorActor {

    public static final String lexinfo = "http://www.lexinfo.net/ontology/2.0/lexinfo#";
    public static final Property partOfSpeech = new PropertyImpl(URI.create(lexinfo + "partOfSpeech"));
    private Logger log = Logging.getLogger(this);

    @Override
    public void perform(LexicalEntry entry, GenerationState state) {

        // Clean-up
        LinkedList<PropertyValue> toRemove = new LinkedList<PropertyValue>();
        for (PropertyValue pv : entry.getProperty(TaggerActor.tagURI)) {
            toRemove.add(pv);
        }
        for(PropertyValue pv : toRemove) {
            entry.removeProperty(TaggerActor.tagURI, pv);
        }
        // Merging
        LexicalForm canForm = entry.getCanonicalForm();
        String rep;
        String lang;
        if (canForm != null) {
            Text text = canForm.getWrittenRep();
            rep = text.value;
            lang = text.language;
        } else if(entry.getForms().size() == 1) {
            final LexicalForm newCanForm = entry.getForms().iterator().next();
            Text text = newCanForm.getWrittenRep();
            rep = text.value;
            lang = text.language;
            entry.removeForm(newCanForm);
            entry.setCanonicalForm(newCanForm);
        } else {
        //    log.warning("No canonical form for " + entry.getURI());
            state.report(new ActorGenerationReportImpl("Merge", ActorGenerationReport.Status.NO_INFO, "No canonical form"));
            return;
        }
        Collection<PropertyValue> pos = entry.getProperty(partOfSpeech);
        if (pos.size() > 1) {
        //    log.warning(entry.getURI() + " has multiple parts of speech");
            state.report(new ActorGenerationReportImpl("Merge", ActorGenerationReport.Status.FAILED, "Multiple parts of speech"));
            return;
        }
        Map<Property, PropertyValue> propMap = new HashMap<Property, PropertyValue>();
        for (PropertyValue pv : pos) {
            propMap.put(partOfSpeech, pv);
        }
        List<LexicalEntry> results = LemonModels.getEntriesByFormAndProps(
                state.getModel(), rep, lang, propMap);
        for (LexicalEntry entry2 : results) {
            if (!entry2.equals(entry)) {
        //        log.warning("Merge Actor: " + entry.getURI() + " -> " + entry2.getURI());
                // Merge this entry into the previously generated one
                entry.removeAnnotation(LeLAManager.reviewstatus, LeLAManager.autoreview);
                state.getModel().merge(entry, entry2);
                state.report(new ActorGenerationReportImpl("Merge", ActorGenerationReport.Status.OK, "Merged " + entry.getURI() + " with " + entry.getURI()));
            }
        }
        //log.info("exit merge actor");
    }

    @Override
    public double getPriority() {
        return 50;
    }

    private static class PropertyImpl extends URIValue implements Property {

        public PropertyImpl(URI uri) {
            super(uri);
        }
    }

    private static class PropertyValueImpl extends URIElement implements PropertyValue {

        public PropertyValueImpl(URI uri) {
            super(uri);
        }
    }
}
