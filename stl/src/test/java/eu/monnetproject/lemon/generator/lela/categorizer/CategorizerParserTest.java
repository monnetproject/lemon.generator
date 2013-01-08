/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.generator.lela.categorizer;

import java.io.StringReader;
import java.io.Reader;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jmccrae
 */
public class CategorizerParserTest {
    
    public CategorizerParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of parser method, of class CategorizerParser.
     */
    @Test
    public void testParser() throws Exception {
        System.out.println("parser");
        String file = "Tree: (FRAG (VP{head}) (PP{_} (IN{obj.marker})))\n"+
"Semantics: owl:ObjectProperty, owl:DatatypeProperty, rdf:Property\n"+
"Frame: IntransitivePPFrame\n"+
"\n"+
"Tree: (VP)\n"+
"Semantics: owl:ObjectProperty, owl:DatatypeProperty, rdf:Property\n"+
"Frame: TransitiveFrame\n";
        Reader reader = new StringReader(file);
        final List<CategorizerRule> result = CategorizerParser.parser(reader);
        assertFalse(result.isEmpty());
    }
}
