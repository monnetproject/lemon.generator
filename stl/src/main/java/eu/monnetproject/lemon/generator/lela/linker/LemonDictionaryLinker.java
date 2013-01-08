package eu.monnetproject.lemon.generator.lela.linker;

import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonModels;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.LinguisticOntology;
import eu.monnetproject.lemon.model.LemonElement;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.lemon.model.Property;
import eu.monnetproject.lemon.model.PropertyValue;
import eu.monnetproject.util.Logging;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import eu.monnetproject.util.Logger;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author John McCrae
 */
public class LemonDictionaryLinker {

    private final Logger log = Logging.getLogger(this);

    public Map<LexicalEntry, LexicalEntry> link(Lexicon lexicon1, Lexicon lexicon2, LinguisticOntology lingOnto) {
        int ambiguous = 0, matched = 0, notMatched = 0;
        LemonModel model1 = lexicon1.getModel();
        LemonModel model2 = lexicon2.getModel();
        Map<LexicalEntry, LexicalEntry> entries = new HashMap<LexicalEntry, LexicalEntry>();
        for (LexicalEntry le1 : lexicon1.getEntrys()) {
            final Collection<PropertyValue> le1POSs = le1.getProperty(lingOnto.getProperty("partOfSpeech"));
            final List<LexicalEntry> entries2 = LemonModels.getEntriesByForm(model2, le1.getCanonicalForm().getWrittenRep().value, le1.getCanonicalForm().getWrittenRep().language);
            LexicalEntry bestLe2 = null;
            LE2_LOOP:
            for (LexicalEntry le2 : entries2) {
                final Collection<PropertyValue> le2POSs = le2.getProperty(lingOnto.getProperty("partOfSpeech"));
                if (!le1POSs.isEmpty() && !le2POSs.isEmpty()) {
                    if (!le1POSs.iterator().next().equals(le2POSs.iterator().next())) {
                        continue;
                    }
                } else {
                    continue;
                }
                if (isContradictory(le1, le2)) {
                    continue LE2_LOOP;
                }
                for (LexicalForm f1 : le1.getForms()) {
                    for (LexicalForm f2 : le2.getForms()) {
                        if (f1.getWrittenRep().equals(f2.getWrittenRep()) && isContradictory(f1, f2)) {
                            continue LE2_LOOP;
                        }
                    }
                }
                // OK safe :)
                if (bestLe2 != null) {
                    ambiguous++;
                }
                bestLe2 = le2;
            }
            if (bestLe2 == null) {
                notMatched++;
            } else {
                log.info(le1.getURI() + " == " + bestLe2.getURI());
                matched++;
                entries.put(le1, bestLe2);
            }
        }
        log.info("Matched: " + matched);
        log.info("Not Matched: " + notMatched);
        log.info("Ambiguous: " + ambiguous);
        return entries;
    }

    public Map<LexicalEntry, LexicalEntry> linkToWikt(Lexicon lexicon1, File wiktFolder, LinguisticOntology lingOnto) {
        try {
            final PrintWriter out = new PrintWriter("wikt-wordnet-mapping.ttl");
            out.println("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .");
            int ambiguous = 0, matched = 0, notMatched = 0;
            LemonModel model1 = lexicon1.getModel();
            final LemonSerializer serializer = LemonSerializer.newInstance();
            Map<LexicalEntry, LexicalEntry> entries = new HashMap<LexicalEntry, LexicalEntry>();
            for (LexicalEntry le1 : lexicon1.getEntrys()) {
                System.err.println(le1.getURI());
                final Collection<PropertyValue> le1POSs = le1.getProperty(lingOnto.getProperty("partOfSpeech"));
                final File wiktEntryFile = new File(wiktFolder, le1.getCanonicalForm().getWrittenRep().value + ".ttl");
                if(!wiktEntryFile.exists())
                    continue;
                //final List<LexicalEntry> entries2 = LemonModels.getEntriesByForm(model2, le1.getCanonicalForm().getWrittenRep().value, le1.getCanonicalForm().getWrittenRep().language);
                
                LexicalEntry bestLe2 = null;
                //LE2_LOOP:
                //for (LexicalEntry le2 : entries2) {
                LexicalEntry le2 = serializer.readEntry(new FileReader(wiktEntryFile));
                    final Collection<PropertyValue> le2POSs = le2.getProperty(lingOnto.getProperty("partOfSpeech"));
                    if (!le1POSs.isEmpty() && !le2POSs.isEmpty()) {
                        if (!le1POSs.iterator().next().equals(le2POSs.iterator().next())) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                    if (isContradictory(le1, le2)) {
                        continue;// LE2_LOOP;
                    }
                    for (LexicalForm f1 : le1.getForms()) {
                        for (LexicalForm f2 : le2.getForms()) {
                            if (f1.getWrittenRep().equals(f2.getWrittenRep()) && isContradictory(f1, f2)) {
                                continue;// LE2_LOOP;
                            }
                        }
                    }
                    // OK safe :)
                    if (bestLe2 != null) {
                        ambiguous++;
                    }
                    bestLe2 = le2;
                //}
                if (bestLe2 == null) {
                    notMatched++;
                } else {
                    out.println("<" + le1.getURI() + "> rdfs:seeAlso <" + bestLe2.getURI() + "> . ");
                    log.info(le1.getURI() + " == " + bestLe2.getURI());
                    matched++;
                    entries.put(le1, bestLe2);
                }
            }
            out.flush();
            out.close();
            log.info("Matched: " + matched);
            log.info("Not Matched: " + notMatched);
            log.info("Ambiguous: " + ambiguous);
            return entries;
        } catch (IOException x) {
            x.printStackTrace();
            return null;
        }
    }

    private boolean isContradictory(LemonElement le1, LemonElement le2) {
        for (Map.Entry<Property, Collection<PropertyValue>> propEntries : le1.getPropertys().entrySet()) {
            final Collection<PropertyValue> propVals2 = le2.getProperty(propEntries.getKey());
            if (propVals2.size() == 1 && propEntries.getValue().size() == 1) {
                if (!propVals2.iterator().next().equals(propEntries.getValue().iterator().next())) {
                    return true;
                }
            }
        }
        return false;
    }
}
