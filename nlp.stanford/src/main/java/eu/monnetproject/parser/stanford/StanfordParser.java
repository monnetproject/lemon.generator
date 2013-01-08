package eu.monnetproject.parser.stanford;

import eu.monnetproject.lang.Language;
import eu.monnetproject.parser.*;
import eu.monnetproject.tokens.Token;
import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.util.ScoredObject;
import java.io.File;
import java.util.*;
import eu.monnetproject.util.Logger;
import java.util.regex.*;
import eu.monnetproject.util.Logging;

public class StanfordParser implements Parser, DependencyParser {

    private final Logger log = Logging.getLogger(this);
    private final File file;
    private final String fileName;
    private final Language language;

    public StanfordParser(File fileName) {
        this(fileName,fileName.getName());
    }
    
    public StanfordParser(File file, String fileName) {
        this.file = file;
        this.fileName = fileName;
        Matcher m = Pattern.compile("(.*)\\.(.+)\\.ser(\\.gz)?").matcher(fileName);
        if (m.matches()) {
            language = Language.get(m.group(1));
        } else {
            language = null;
        }
    }
    private LexicalizedParser lp;
    private TreebankLanguagePack tlp;

    private void init() {
        lp = new LexicalizedParser(file.getPath());
        lp.setOptionFlags(new String[]{"-maxLength", "80"/*, "-retainTmpSubcategories"*/});
        tlp = new PennTreebankLanguagePack();
        //Collection tdl = gs.typedDependenciesCollapsed();
    }

    private int getSize(Iterable<Token> tokens) {
        int cnt = 0;
        Iterator<Token> it = tokens.iterator();
        while (it.hasNext()) {
            it.next();
            cnt++;
        }
        return cnt;
    }

    @Override
    public String getTagSet() {
        Matcher m = Pattern.compile("(.*)\\.(.+)\\.ser(\\.gz)?").matcher(fileName);
        if (m.matches()) {
            return m.group(2);
        } else {
            log.severe("Could not extract tag set from file name " + fileName);
            return "ERROR";
        }
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    private GrammaticalStructure doParse(Iterable<Token> tokens) {
        if (lp == null) {
            init();
        }
        List<Word> tks = new ArrayList<Word>(getSize(tokens));
        // Copy the token list in case caller modifies it!
        List<Token> tksCopy = new ArrayList<Token>(getSize(tokens));
        for (Token tk : tokens) {
            tks.add(new Word(tk.getValue()));
            tksCopy.add(tk);
        }

        Tree parse = (Tree) lp.apply(tks);
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        return gsf.newGrammaticalStructure(parse);

    }

    @Override
    public TreeNode parse(Iterable<Token> tokens) {
        if (lp == null) {
            init();
        }
        List<Word> tks = new ArrayList<Word>(getSize(tokens));
        // Copy the token list in case caller modifies it!
        List<Token> tksCopy = new ArrayList<Token>(getSize(tokens));
        for (Token tk : tokens) {
            tks.add(new Word(tk.getValue()));
            tksCopy.add(tk);
        }

        Tree parse = (Tree) lp.apply(tks);
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        StanfordPOSSet posSet = new StanfordPOSSet(tlp);

        if (parse.children().length != 1) {
            log.warning("Multiple head nodes");
        }
        return new TreeNodeWrap(parse.children()[0], posSet, tksCopy, 0);
    }

    @Override
    public TreeNode depParse(Iterable<Token> tokens) {
        GrammaticalStructure gs = doParse(tokens);
        StanfordPOSSet posSet = new StanfordPOSSet(tlp);

        return new DepTreeWrap(gs.typedDependenciesCollapsed(), gs.root(), posSet);
    }

    @Override
    public List<TreeNode> bestParses(Iterable<Token> tokens, int k) {
        if (lp == null) {
            init();
        }
        List<Word> tks = new ArrayList<Word>(getSize(tokens));
        // Copy the token list in case caller modifies it!
        List<Token> tksCopy = new ArrayList<Token>(getSize(tokens));
        for (Token tk : tokens) {
            tks.add(new Word(tk.getValue()));
            tksCopy.add(tk);
        }

        lp.parse(tks);
        List<ScoredObject<Tree>> parses = lp.getKBestPCFGParses(k);
        List<TreeNode> rval = new ArrayList<TreeNode>(k);
        for (ScoredObject<Tree> scoredParse : parses) {
            double score = scoredParse.score();
            Tree parse = scoredParse.object();
            GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
            GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);

            StanfordPOSSet posSet = new StanfordPOSSet(tlp);

            if (parse.children().length != 1) {
                throw new RuntimeException("Stanford Parser returned more than one parse from top node?");
            }
            rval.add(new TreeNodeWrap(parse.children()[0], posSet, tksCopy, 0, score));
        }
        return rval;
    }
}
