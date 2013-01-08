/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.splitter.impl;

import eu.monnetproject.sentence.SentenceSplitter;
import eu.monnetproject.sentence.SentenceSplitterFactory;
import eu.monnetproject.tokenizer.latin.LatinTokenizerImpl;

/**
 *
 * @author tobwun
 */
public class SentenceSplitterFactoryImpl implements SentenceSplitterFactory {

    @Override
    public SentenceSplitter getService() {
        SimpleSentenceSplitter splitter = new SimpleSentenceSplitter();
        splitter.setTokenizer(new LatinTokenizerImpl());
        return splitter;
    }
    
}
