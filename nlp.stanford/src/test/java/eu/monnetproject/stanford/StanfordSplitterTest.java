/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.stanford;

import eu.monnetproject.osgi.ServiceNotFoundException;
import eu.monnetproject.sentence.Sentence;
import eu.monnetproject.sentence.SentenceSplitter;
import eu.monnetproject.splitter.stanford.StanfordSplitter;
import eu.monnetproject.util.Logging;
import java.util.List;
import eu.monnetproject.util.Logger;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author John McCrae
 */
public class StanfordSplitterTest {

    private static Logger log = Logging.getLogger(StanfordTokenizerTest.class);

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testSplitter() {
        SentenceSplitter splitter = new StanfordSplitter();
        List<Sentence> sents = splitter.split("This is a test. This is a second test. And a third.");
        assertEquals(sents.size(), 3);
    }
}
