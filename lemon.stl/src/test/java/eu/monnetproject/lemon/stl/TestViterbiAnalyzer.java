package eu.monnetproject.lemon.stl;

import eu.monnetproject.framework.services.Services;
import eu.monnetproject.lemon.*;
import java.io.File;

import java.io.FileNotFoundException;
import org.junit.Test;

import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.model.LexicalEntry;
import eu.monnetproject.lemon.model.LexicalForm;
import eu.monnetproject.lemon.model.Lexicon;
import eu.monnetproject.stl.term.ViterbiAnalyzer;
import eu.monnetproject.util.ResourceFinder;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;


public class TestViterbiAnalyzer {
    
    
    @Test
    public void TestTermAnalyser() {
        
        ViterbiAnalyzer analyzer = new ViterbiAnalyzer();
        analyzer.addTerm(" ");
        analyzer.addTerm("fixed assets");
        String term = "intangible fixed assets";
        for(String token:term.split(" "))
            analyzer.addTerm(token);
        List<String> decomposition = analyzer.decompose(term);
        //System.out.println(decomposition);
        Iterator<String> it = decomposition.iterator();
        Assert.assertEquals("intangible",it.next());
        Assert.assertEquals(" ",it.next());
        Assert.assertEquals("fixed assets",it.next());
        
    }
    
    @Test
    public void TestTermAnalyser2() {
        
        // init analzer
        ViterbiAnalyzer analyzer = new ViterbiAnalyzer();
        analyzer.addTerm(" ");
        analyzer.addTerm("fixed assets");
        analyzer.addTerm("another word one");
        analyzer.addTerm("another word two");
        analyzer.addTerm("another word three");
        analyzer.addTerm("another word four");
        analyzer.addTerm("another word five");
        analyzer.addTerm("another word six");
        analyzer.addTerm("another word seven");
        analyzer.addTerm("another word eight");
        analyzer.addTerm("another word nine");
        analyzer.addTerm("another word ten");
        analyzer.addTerm("another word eleven");
        analyzer.addTerm("another word twelve");
        analyzer.addTerm("another word thirteen");
        analyzer.addTerm("another word fourteen");
        analyzer.addTerm("another word fiveteen");
        String term = "intangible fixed assets";
        for(String token:term.split(" "))
            analyzer.addTerm(token);
        
        // analyze and test
        List<String> decomposition = analyzer.decompose(term);
        //System.out.println(decomposition);
        Iterator<String> it = decomposition.iterator();
        Assert.assertEquals("intangible",it.next());
        Assert.assertEquals(" ",it.next());
        Assert.assertEquals("fixed assets",it.next());
        
    }
    
    @Test
    public void TestTermAnalyser3() throws IOException {
        
        // init analzer with text file
        ViterbiAnalyzer analyzer = new ViterbiAnalyzer();
        Reader resource = ResourceFinder.getResourceAsReader("xebr.sorted.txt");
        BufferedReader bufferedReader = new BufferedReader(resource);
        String line;
        while((line=bufferedReader.readLine())!=null) {
            analyzer.addTerm(line.toLowerCase());
            for(String token:line.split(" ")) {
                if (!token.matches("in"))
                    analyzer.addTerm(token);
            }
        }
        analyzer.addTerm(" ");
        String term = "intangible fixed assets";
        for(String token:term.split(" "))
            analyzer.addTerm(token);
        analyzer.setMinProp(.0001);
        
        // analyze and test
        List<String> decomposition = analyzer.decompose(term);
        //System.out.println(decomposition);
        Iterator<String> it = decomposition.iterator();
        Assert.assertEquals("intangible",it.next());
        Assert.assertEquals(" ",it.next());
        Assert.assertEquals("fixed assets",it.next());
        
    }
    
    @Test
    public void TestTermAnalyser4() throws IOException {
        
        // read lemon model
        LemonSerializer serializer = Services.get(LemonSerializer.class);
        Reader resource = ResourceFinder.getResourceAsReader("xebr.lemon.light.rdf");
        LemonModel model = serializer.read(resource);
        
        // init analyzer
        ViterbiAnalyzer analyzer = new ViterbiAnalyzer();
        analyzer.addTerm(" ");
        for(Lexicon lexicon:model.getLexica()) {
            for(LexicalEntry le:lexicon.getEntrys()) {
                LexicalForm lf = le.getForms().iterator().next();
                String writtenrep = lf.getWrittenRep().value.toLowerCase();
                if (!writtenrep.matches("in")) {
                    analyzer.addTerm(writtenrep);
                    //System.out.println("add "+writtenrep);
                }
            }
        }
        analyzer.setMinProp(.00001);
        
        // analyze and test
        String term = "intangible fixed assets";
        List<String> decomposition = analyzer.decompose(term);
        System.out.println(decomposition);
        Iterator<String> it = decomposition.iterator();
        Assert.assertEquals("intangible",it.next());
        Assert.assertEquals(" ",it.next());
        Assert.assertEquals("fixed assets",it.next());
        
    }

}
