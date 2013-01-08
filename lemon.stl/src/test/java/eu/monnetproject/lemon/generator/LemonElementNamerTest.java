/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.generator;

import eu.monnetproject.lemon.LemonModel;
import java.net.URI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author jmccrae
 */
public class LemonElementNamerTest {
    
    public LemonElementNamerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of name method, of class LemonElementNamer.
     */
    @Test
    public void testName() {
        System.out.println("name");
        
        String lexiconName = "lexicon";
        String entryName = "entry";
        String identifier = "id";
        LemonElementNamer instance = LemonElementNamer.defaultForURL("http://www,example.com/");
        URI expResult = URI.create("http://www,example.com/lexicon/entry#id");
        URI result = instance.name(lexiconName, entryName, identifier);
        assertEquals(expResult, result);
        URI expResult2 = URI.create("http://www,example.com/lexicon/entry#id__2");
        URI result2 = instance.name(lexiconName, entryName, identifier);
        assertEquals(expResult2, result2);
        URI expResult3 = URI.create("http://www,example.com/lexicon/entry#id__3");
        URI result3 = instance.name(lexiconName, entryName, identifier);
        assertEquals(expResult3, result3);
        final LemonElementNamer len = new LemonElementNamer("unknown:/${lexiconName}", "unknown:/${lexiconName}/${entryName}", "unknown:/${lexiconName}/${entryName}#identifier");
        len.name("lexicon__en", "height (mm)", null);
    }
}
