/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.parser.combinator;

import eu.monnetproject.parser.combinator.impl.StringInput;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static eu.monnetproject.parser.combinator.Parser.*;

/**
 *
 * @author jmccrae
 */
public class ParserTest {
    
    public ParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of literal method, of class Parser.
     */
    @Test
    public void testLiteral() throws ParseException {
        System.out.println("literal");
        String str = "literal";
        Parser instance = literal(str);
        assertTrue(instance.accept(new StringInput(str)) instanceof Success);
    }

    /**
     * Test of then method, of class Parser.
     */
    @Test
    public void testThen() throws ParseException {
        System.out.println("then");
        String str1 = "first this ";
        String str2 = " and then this";
        Parser parser = literal(str1).then(literal(str2));
        assertTrue(parser.accept(new StringInput(str1+str2)) instanceof Success);
    }

    /**
     * Test of regex method, of class Parser.
     */
    @Test
    public void testRegex() throws ParseException {
        System.out.println("regex");
        String regex = "aa*b..";
        Parser parser = regex(regex);
        String str = "aaabcc";
        assertTrue(parser.accept(new StringInput(str)) instanceof Success);
    }

    /**
     * Test of dropLeft method, of class Parser.
     */
    @Test
    public void testDropLeft() throws ParseException {
        System.out.println("dropLeft");
        Parser<String> parser = literal("left ").dropLeft(literal("right"));
        String str = "left right";
        ParserResult<String> res = parser.accept(new StringInput(str));
        assertTrue(res instanceof Success);
        assertEquals("right",res.getObject());
    }

    /**
     * Test of dropRight method, of class Parser.
     */
    @Test
    public void testDropRight() throws ParseException {
        System.out.println("dropRight");
        Parser<String> parser = literal("left").dropRight(literal(" right"));
        String str = "left right";
        ParserResult<String> res = parser.accept(new StringInput(str));
        assertTrue(res instanceof Success);
        assertEquals("left",res.getObject());
    }

    /**
     * Test of or method, of class Parser.
     */
    @Test
    public void testOr() throws ParseException {
        System.out.println("or");
        Parser<String> parser = literal("one").or(literal("two"));
        String str = "two";
        ParserResult<String> res = parser.accept(new StringInput(str));
        assertTrue(res instanceof Success);
        assertEquals("two", res.getObject());
    }

    /**
     * Test of opt method, of class Parser.
     */
    @Test
    public void testOpt() throws ParseException {
        System.out.println("opt");
        Parser<String> parser = literal("one").opt();
        String str = "";
        ParserResult<String> res = parser.accept(new StringInput(str));
        assertTrue(res instanceof Success);
        str = "one";
        res = parser.accept(new StringInput(str));
        assertTrue(res instanceof Success);
    }

    /**
     * Test of rep method, of class Parser.
     */
    @Test
    public void testRep_0args() throws ParseException {
        System.out.println("rep");
        Parser<List<String>> parser = literal("one ").rep();
        String str = "one one one ";
        ParserResult<List<String>> res = parser.accept(new StringInput(str));
        assertTrue(res instanceof Success);
        assertEquals(3, res.getObject().size());
        str = "";
        res = parser.accept(new StringInput(str));
        assertFalse(res instanceof Success);
    }

    /**
     * Test of repOpt method, of class Parser.
     */
    @Test
    public void testRepOpt() throws ParseException {
        System.out.println("repOpt");
        Parser<List<String>> parser = literal("one ").repOpt();
        String str = "one one one ";
        ParserResult<List<String>> res = parser.accept(new StringInput(str));
        assertTrue(res instanceof Success);
        assertEquals(3, res.getObject().size());
        str = "";
        res = parser.accept(new StringInput(str));
        assertTrue(res instanceof Success);
    }

    /**
     * Test of rep method, of class Parser.
     */
    @Test
    public void testRep_int_int() throws ParseException {
        System.out.println("rep");
        int min = 3;
        int max = 5;
        Parser<List<String>> parser = literal("one ").rep(min,max);
        String str = "one one one ";
        ParserResult<List<String>> res = parser.accept(new StringInput(str));
        assertTrue(res instanceof Success);
        str = "one one one one one ";
        res = parser.accept(new StringInput(str));
        assertTrue(res instanceof Success);
        str = "one one one one one one ";
        res = parser.accept(new StringInput(str));
        assertFalse(res instanceof Success);
    }

    /**
     * Test of map method, of class Parser.
     */
    @Test
    public void testMap() throws ParseException {
        System.out.println("map");
        ParseMap<String, Integer> mapper = new ParseMap<String, Integer>() {

            @Override
            public Integer convert(String e) {
                return Integer.parseInt(e);
            }
        };
        Parser<Integer> instance = regex("[0-9]+").map(mapper);
        ParserResult<Integer> res = instance.accept(new StringInput("39"));
        assertTrue(res instanceof Success);
        assertEquals(39, res.getObject().intValue());
    }

}
