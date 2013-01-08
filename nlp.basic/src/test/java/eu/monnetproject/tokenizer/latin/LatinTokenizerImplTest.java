package eu.monnetproject.tokenizer.latin;

import eu.monnetproject.lang.Script;
import eu.monnetproject.tokens.Token;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author john
 */
public class LatinTokenizerImplTest {

    public LatinTokenizerImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of tokenize method, of class LatinTokenizerImpl.
     */
    @Test
    public void testTokenize() {
        System.out.println("tokenize1");
        String input = "this is a test sentence, it shouldn't remove punctuation and follow roughly penn (-like) rules, and get things like Prof. correct and I.B.M.";
        LatinTokenizerImpl instance = new LatinTokenizerImpl();
        List<eu.monnetproject.tokens.Token> expResult = Arrays.asList(
                tk("this"),tk("is"),tk("a"),tk("test"),tk("sentence"),tk(","),
                tk("it"),tk("shouldn't"),tk("remove"),tk("punctuation"),
                tk("and"),tk("follow"),tk("roughly"),tk("penn"),tk("("),tk("-"),tk("like"),tk(")"),tk("rules"),
                tk(","),tk("and"),tk("get"),tk("things"),tk("like"),tk("Prof."),
                tk("correct"),tk("and"),tk("I.B.M."));
        final Iterator<Token> iter = expResult.iterator();
        List<eu.monnetproject.tokens.Token> result = instance.tokenize(input);
        for(eu.monnetproject.tokens.Token tk : result) {
            assertEquals(iter.next(), tk);
        }
    }
    
    private eu.monnetproject.tokens.Token tk(String s) { 
        return new SimpleToken(s);
    }
            

}