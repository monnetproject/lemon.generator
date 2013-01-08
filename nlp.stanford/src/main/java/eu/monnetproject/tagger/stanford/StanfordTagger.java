package eu.monnetproject.tagger.stanford;

import eu.monnetproject.pos.POSToken;
import eu.monnetproject.tokens.Token;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.tagger.maxent.*;
import eu.monnetproject.lang.Language;
import eu.monnetproject.pos.*;
import eu.monnetproject.tagger.*;
import eu.monnetproject.tokenizer.*;
import eu.monnetproject.util.Logger;
import eu.monnetproject.util.Logging;
import java.io.File;
import java.util.*;
import java.util.regex.*;

/**
 *
 * @author John McCrae
 */
public class StanfordTagger implements Tagger {

    private final Logger log = Logging.getLogger(this);
    private MaxentTagger tagger;
    private File modelFile;
    private final String fileName;
    private StanfordPOSSet posSet;
    private final Language lang;

    public StanfordTagger(File modelFile) {
        this(modelFile,modelFile.getName());
    }
    
    public StanfordTagger(File file, String fileName) {
        this.modelFile = file;
        this.fileName = fileName;
        Matcher m = Pattern.compile("(.*)\\.(.+)\\.tagger").matcher(fileName);
        if (m.matches()) {
            lang = Language.get(m.group(1));
        } else {
            lang = null;
        }
    }

    public String getTagSet() {
        Matcher m = Pattern.compile("(.*)\\.(.+)\\.tagger").matcher(fileName);
        if (m.matches()) {
            return m.group(2);
        } else {
            log.warning("Could not extract tag set from file name " + fileName);
            return "ERROR";
        }
    }

    public Language getLanguage() {
        return lang;
    }

    private void init() {
        try {
            tagger = new MaxentTagger(modelFile.getPath());
            posSet = new StanfordPOSSet(tagger.getTags());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSize(List<Token> tokens) {
        int cnt = 0;
        Iterator<Token> it = tokens.iterator();
        while (it.hasNext()) {
            it.next();
            cnt++;
        }
        return cnt;
    }

    public List<POSToken> tag(List<Token> tokens) {
        if (tagger == null) {
            init();
        }
        //Sentence sentence = Sentence.toSentence(convert(tokens));
        //Sentence<ProbTaggedWord> taggedSentence = tagger.getProbs(sentence);
        //ArrayList<POSToken> rval = new ArrayList<POSToken>(taggedSentence.size());

        //for(int i = 0; i < taggedSentence.size(); i++) {
        //		ProbTaggedWord word = taggedSentence.get(i);
        //	   /* POSToken posToken = new POSToken(convertProbList(word.getTagProbs()),
        //				word.word(), convertLemmas(word));*/
        //		rval.add(word);
        //}
        //
        //return rval;

        List<Word> sentence = new ArrayList<Word>(getSize(tokens));
        for (Token t : tokens) {
            sentence.add(new Word(t.getValue()));
        }
        ArrayList<TaggedWord> tagged = tagger.tagSentence(sentence);
        if (tagged.size() != getSize(tokens)) {
            throw new RuntimeException("Tag result length does not match input");
        }
        List<POSToken> rval = new ArrayList<POSToken>(tagged.size());
        for (TaggedWord tw : tagged) {
            rval.add(new StanfordPOSToken(tw.word(), new StanfordPOSTag(posSet.getPOS(tw.tag()))));
        }
        return rval;
    }

    private static class StanfordPOSToken implements POSToken {

        final String token;
        final POSTag posTag;

        public StanfordPOSToken(String token, POSTag posTag) {
            this.token = token;
            this.posTag = posTag;
        }

        public String getValue() {
            return token;
        }

        public POSTag getPOSTag() {
            return posTag;
        }

        @Deprecated
        public String getLemma() {
            return new StanfordEnglishLemmatizer().stem(this).getLemma();
        }

        public String toString() {
            return token + "/" + posTag;
        }
    }
}
