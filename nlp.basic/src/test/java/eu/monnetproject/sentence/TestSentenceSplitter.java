/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.sentence;

import eu.monnetproject.framework.services.Services;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author tobwun
 */
public class TestSentenceSplitter {
    
    @Test
    public void TestSentenceSplitter() {
        String sentences[] = {"This is the first sentence.","This is the second sentence."};
        String text = sentences[0]+" "+sentences[1];
        SentenceSplitterFactory factory = Services.get(SentenceSplitterFactory.class);
        SentenceSplitter splitter = factory.getService();
        List<Sentence> split = splitter.split(text);
        int idx = 0;
        for(Sentence sen:split) {
            String actual = sen.getText();
            String expected = sentences[idx];
            Assert.assertEquals(expected, actual);
            idx++;
        }
    }

}
