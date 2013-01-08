/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.stanford;

import java.util.Collection;
import eu.monnetproject.lang.Language;
import eu.monnetproject.pos.POSToken;
import eu.monnetproject.tokens.Token;
import java.util.LinkedList;
import java.util.List;
import eu.monnetproject.tagger.stanford.StanfordTaggerFactory;
import eu.monnetproject.osgi.OSGi;
import eu.monnetproject.osgi.ServiceArity;
import eu.monnetproject.osgi.ServiceNotFoundException;
import eu.monnetproject.osgi.ServiceRequest;
import eu.monnetproject.osgi.embedded.EmbeddedOSGi;
import eu.monnetproject.tagger.Tagger;
import eu.monnetproject.tagger.TaggerFactory;
import eu.monnetproject.util.Logging;
import eu.monnetproject.util.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John McCrae
 */
public class StanfordTaggerTest {

    private static Logger log = Logging.getLogger(StanfordTokenizerTest.class);

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testTagger() throws Exception {
        final TaggerFactory taggerFactory = new StanfordTaggerFactory();
        testEnglish(taggerFactory.makeTagger(Language.ENGLISH));
        testGerman(taggerFactory.makeTagger(Language.GERMAN));

    }

    public void testGerman(Tagger tagger) {
        List<Token> tokens = new LinkedList<Token>();
        tokens.add(new Tk("ein"));
        tokens.add(new Tk("Mann"));
        tokens.add(new Tk("bisst"));
        tokens.add(new Tk("einen"));
        tokens.add(new Tk("Hund"));
        List<POSToken> result = tagger.tag(tokens);
        assertEquals("ART", result.get(0).getPOSTag().getPOS().getValue());
        assertEquals("NN", result.get(1).getPOSTag().getPOS().getValue());
        assertEquals("VVFIN", result.get(2).getPOSTag().getPOS().getValue());
        assertEquals("ART", result.get(3).getPOSTag().getPOS().getValue());
        assertEquals("NN", result.get(4).getPOSTag().getPOS().getValue());
    }

    public void testEnglish(Tagger tagger) {
        List<Token> tokens = new LinkedList<Token>();
        tokens.add(new Tk("a"));
        tokens.add(new Tk("man"));
        tokens.add(new Tk("bites"));
        tokens.add(new Tk("a"));
        tokens.add(new Tk("dog"));
        List<POSToken> result = tagger.tag(tokens);
        assertEquals("DT", result.get(0).getPOSTag().getPOS().getValue());
        assertEquals("NN", result.get(1).getPOSTag().getPOS().getValue());
        assertEquals("VBZ", result.get(2).getPOSTag().getPOS().getValue());
        assertEquals("DT", result.get(3).getPOSTag().getPOS().getValue());
        assertEquals("NN", result.get(4).getPOSTag().getPOS().getValue());
    }

    private static class Tk implements Token {

        private final String tk;

        public Tk(String tk) {
            this.tk = tk;
        }

        public String getValue() {
            return tk;
        }
    }
}
