/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.stl.term;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author tobwun
 */
public class SubtermBruteForce {
    
    public Set<String> termbase = new HashSet<String>();
    public Set<String> termbaseFiltered = new HashSet<String>();

    public void setTermbase(Set<String> termbase) {
        this.termbase = termbase;
    }

    public void addTerm(String term) {
        termbase.add(term);
    }

    public List<String> decomposeBest(String term) {
        Set<List<String>> decompositions = decompose(term);
        List<String> best = new LinkedList<String>();
        double bestscore = 0;
        for(List<String> decomposition:decompositions) {
            double score = 1/(double)decomposition.size();
            if (bestscore<score) {
                bestscore = score;
                best = decomposition;
            }
        }
        return best;
    }
    
    public Set<List<String>> decompose(String term) {
        termbaseFiltered = new HashSet<String>();
        for (String t : termbase) {
            if ( (term.contains(t)) && (!term.equals(t)) ) {
                termbaseFiltered.add(t);
            }
        }
        return analyze(term, 0, 0, 0, new LinkedList<String>(), new HashSet<List<String>>());
    }

    private Set<List<String>> analyze(String term, int pos, int poslast, int subtermcnt, List<String> decomp_last, Set<List<String>> decompositions) {
        pos = pos + 1;

        // subterm found
        String subterm = term.substring(poslast, pos);
        if (termbaseFiltered.contains(subterm)) {
            //System.out.println(" OK");
            List<String> decomp = new LinkedList<String>();
            for (String t : decomp_last) {
                decomp.add(t);
            }
            decomp.add(subterm);
            int newsubtermcnt = subtermcnt + subterm.length();
            // fork with subterm found
            if (pos < term.length()) {
                analyze(term, pos, pos, newsubtermcnt, decomp, decompositions);
            } else { // last character
                if (newsubtermcnt == pos) {
                    decompositions.add(decomp);
                }
            }
        }

        // fork with next character
        if (pos < term.length()) {
            analyze(term, pos, poslast, subtermcnt, decomp_last, decompositions);
        }

        return decompositions;
    }
    
    public static void main(String[] args) {
        String term = "intangible assets";
        SubtermBruteForce subtermBruteForce = new SubtermBruteForce();
        String[] TERMBASE = {" ", "fixed", "in", "intangible assets", "tangible", "intangible", "assets", "asset"};
        for (int i = 0; i < TERMBASE.length; i++)
            subtermBruteForce.addTerm(TERMBASE[i]);
        Set<List<String>> decompositions = subtermBruteForce.decompose(term);
        for (List<String> decomp : decompositions)
            System.out.println(decomp);
        List<String> decomp = subtermBruteForce.decomposeBest(term);
        System.out.println("best "+decomp);
    }
    
}
