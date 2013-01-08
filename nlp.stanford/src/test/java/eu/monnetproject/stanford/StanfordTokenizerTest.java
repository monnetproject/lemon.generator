package eu.monnetproject.stanford;

import eu.monnetproject.tokens.Token;
import eu.monnetproject.tokenizer.Tokenizer;
import eu.monnetproject.tokenizer.stanford.StanfordTokenizer;
import eu.monnetproject.util.Logging;
import java.util.List;
import eu.monnetproject.util.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author John McCrae
 */
public class StanfordTokenizerTest {

    private static Logger log = Logging.getLogger(StanfordTokenizerTest.class);

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testTokenizer() {
        Tokenizer tokenizer = new StanfordTokenizer();
        List<Token> rv = tokenizer.tokenize("This is a test");
        Assert.assertEquals(rv.size(), 4);
    }
}
