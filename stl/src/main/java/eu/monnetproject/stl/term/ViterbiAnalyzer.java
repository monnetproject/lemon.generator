package eu.monnetproject.stl.term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViterbiAnalyzer {
    
    private double MINPROB = .001;
    private final static int MAXWORDLENGTH = 100;
    Set<String> terms = new HashSet<String>();
    Map<String,Double> weightedTerms = new HashMap<String,Double>();
    
    public void setMinProp(double minprob){
        this.MINPROB = minprob;
    }
    
    public void addTerm(String term) {
        this.terms.add(term);
    }
    
    public void addTerm(String term,double ridgeWeight) {
        this.terms.add(term);
        this.weightedTerms.put(term, ridgeWeight);
    }
    
    //	private double word_prob(String word) {
    //		if (this.terms.contains(word))
    //				return 0;
    //		return -word.length();
    //	}
    
    private double word_prob(String word) {
        double ridge = 1.0;
        if (weightedTerms.keySet().contains(word))
            return weightedTerms.get(word);
        if (terms.contains(word))
            return (Double)1.0/terms.size()*7;
        else
            return MINPROB;
    }
    
    public boolean containsTerm(String text) {
        for(String term:terms)
            if (text.contains(term))
                return true;
        return false;
    }
    
    private Integer numberOfComponents(List<Integer> list,Integer cutoff) {
        HashSet<Integer> distinct = new HashSet<Integer>();
        for (int i = 0; i < cutoff; i++)
            distinct.add(list.get(i));
        return distinct.size();
    }
    
    public List<String> decompose(String text) {
        List<Double> probs = new ArrayList<Double>();
        List<Integer> lasts = new ArrayList<Integer>();
        probs.add(1.0);
        lasts.add(0);
        for (int i = 1; i < text.length()+1; i++) {
            Double current_prob = 0.0;
            int previous_best = 0;
            int previous_comp_min = 100;
            for (int j = Math.max(0,i-MAXWORDLENGTH); j < i; j++) {
                String word = text.substring(j,i);
                if (!text.equals(word)) {
                    double word_prob = word_prob(word);
                    double prob_k = probs.get(j) * word_prob;
                    int curr_comp = numberOfComponents(lasts, j);
                    //System.out.println(" j="+j+",i="+i+"  "+word_prob+" "+curr_comp);
                    // combinational probabilty of term
                    if(current_prob <= prob_k) {
                        // greedy: a preference towards a lower number of used components
                        if (curr_comp<=previous_comp_min) { 
                            current_prob = prob_k;
                            if (prob_k>MINPROB) {
                                previous_best = j;
                                //System.out.println(current_prob+"  "+word);
                                previous_comp_min = numberOfComponents(lasts, j);
                            }
                        }
                    }
                }
            }
            probs.add(current_prob);
            lasts.add(previous_best);
            //System.out.println("-- previous best="+previous_best+"\n  "+lasts+" "+distinct.size());
            
        }
        //System.out.println("probs "+probs);
        //System.out.println("lasts "+lasts);
        List<String> words = new ArrayList<String>();
        int i = text.length();
        while ( 0<i ) {
            //System.out.println(lasts.get(i)+"-"+i);
            words.add(text.substring(lasts.get(i), i));
            i = lasts.get(i);
        }
        Collections.reverse(words);
        return words;
    }
    
    private static void Test1() {
        ViterbiAnalyzer analysis = new ViterbiAnalyzer();
        analysis.addTerm("my");
        analysis.addTerm("phd");
        analysis.addTerm("island");
        List<String> segment = analysis.decompose("myphdisland");
        System.out.println("components "+segment);
    }
    
    private static void Test2() {
        double weight = 1;
        ViterbiAnalyzer analysis = new ViterbiAnalyzer();
        analysis.addTerm("FL");
        analysis.addTerm("F",weight);
        analysis.addTerm("L",weight);
        analysis.addTerm("AtAC");
        analysis.addTerm(" ");
        List<String> segment = analysis.decompose("FL AtAC");
        System.out.println("components "+segment);
    }
    
    private static void Test3() {
        double weight = 5;
        ViterbiAnalyzer analysis = new ViterbiAnalyzer();
        analysis.addTerm("financial liabilities");
        analysis.addTerm("financial",weight);
        analysis.addTerm("liabilities",weight);
        analysis.addTerm("at amortized cost");
        analysis.addTerm(" ",1);
        List<String> segment = analysis.decompose("financial liabilities at amortized cost");
        System.out.println("components "+segment);
    }
    
    private static void Test4() {
        double weight = 5;
        ViterbiAnalyzer analysis = new ViterbiAnalyzer();
        analysis.addTerm("financial liabilities",6);
        analysis.addTerm("liabilities at",weight);
        analysis.addTerm("financial",weight);
        analysis.addTerm("amortized cost", weight);
        analysis.addTerm(" ");
        List<String> segment = analysis.decompose("financial liabilities at amortized cost");
        System.out.println("components "+segment);
        // output: components [financial,  , liabilities at,  , amortized cost]
    }
    
    private static void Test6() {
        double weight = 5;
        ViterbiAnalyzer analysis = new ViterbiAnalyzer();
        analysis.addTerm("financial liabilities");
        analysis.addTerm("liabilities at");
        analysis.addTerm("financial");
        analysis.addTerm("amortized cost");
        analysis.addTerm(" ");
        List<String> segment = analysis.decompose("financial liabilities at amortized cost");
        System.out.println("components "+segment);
        // output: components [financial,  , liabilities at,  , amortized cost]
    }
    
    private static void Test5() {
        ViterbiAnalyzer analyzer = new ViterbiAnalyzer();
        analyzer.addTerm("assets");
        analyzer.addTerm("intangible fixed assets");
        analyzer.addTerm("fixed assets");
        analyzer.addTerm("intangible");
        analyzer.addTerm("fixed");
        analyzer.addTerm(" ",1);
        System.out.println(analyzer.decompose("intangible fixed assets"));
    }
    
    public static void main(String[] args) {
        
        Test5();
        
    }
    
    public Set<String> getTerms() {
        return terms;
    }

}
