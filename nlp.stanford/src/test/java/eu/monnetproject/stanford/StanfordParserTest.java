package eu.monnetproject.stanford;

import static org.junit.Assert.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.parser.Parser;
import eu.monnetproject.parser.ParserFactory;
import eu.monnetproject.parser.TreeNode;
import eu.monnetproject.parser.stanford.StanfordParserFactory;
import eu.monnetproject.tokens.Token;
import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author John McCrae
 */
public class StanfordParserTest {

    @BeforeClass
    public static void setUp() throws Exception {
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testParser() {
        final ParserFactory parserFactory = new StanfordParserFactory();
        Parser parser = parserFactory.getParser(Language.ENGLISH);
        List<Token> tokens = new LinkedList<Token>();
        tokens.add(new Tk("this"));
        tokens.add(new Tk("is"));
        tokens.add(new Tk("a"));
        tokens.add(new Tk("test"));
        TreeNode result = parser.parse(tokens);
        assertEquals("S", result.getTag().getPOS().getValue());

        parser = parserFactory.getParser(Language.GERMAN);
        tokens.clear();
        tokens.add(new Tk("das"));
        tokens.add(new Tk("ist"));
        tokens.add(new Tk("ein"));
        tokens.add(new Tk("Test"));
        result = parser.parse(tokens);
        assertEquals("NUR", result.getTag().getPOS().getValue());
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
