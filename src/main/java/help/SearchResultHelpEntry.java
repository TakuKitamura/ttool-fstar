/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package help;

import myutil.TraceManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class SearchResultHelpEntry Creation: 07/03/2019 Version 1.0 07/03/2019
 *
 * @author Ludovic APVRILLE
 */
public class SearchResultHelpEntry extends HelpEntry {
    public static String SEARCH_HEADER = "<h1>Search result</h1>\n<br>\n";

    public Vector<ScoredHelpEntry> scores;

    public SearchResultHelpEntry() {

    }

    public void setScores(Vector<ScoredHelpEntry> _scores) {
        scores = _scores;
    }

    @Override
    public String getHTMLContent() {

        String ret = super.getHTMLContent();
        String kids = getKidsInHTML();
        int index1 = ret.indexOf("<body>");
        if (index1 > -1) {
            index1 += 6;
            ret = ret.substring(0, index1) + "\n" + SEARCH_HEADER + scores.size() + " result(s) found:<br>\n" + kids
                    + ret.substring(index1 + 1, ret.length());
        }

        // TraceManager.addDev("Resulting HTML of search:" + ret);

        return ret;
    }

    @Override
    public String getKidsInHTML() {
        String s = "";
        int maxScore = getMaxScore();

        if (scores != null) {
            for (ScoredHelpEntry entry : scores) {
                s += "<li> ";
                int score = (int) (entry.score * 100.0 / maxScore);
                s += "<a href=\"file://" + entry.he.getPathToHTMLFile() + "\"/>" + entry.he.getMasterKeyword()
                        + "</a>  " + "(" + score + "%)  " + entry.he.getKeywords();
                s += " </li>\n<br>\n";
            }
        }

        return s;
    }

    private int getMaxScore() {
        int maxScore = -1;
        if (scores == null) {
            return maxScore;
        }
        for (ScoredHelpEntry score : scores) {
            if (score.score > maxScore) {
                maxScore = score.score;
            }
        }
        return maxScore;
    }

    public void mergeResults() {
        // Find duplicate sons, and compute final scores for sons
        // Vector<HelpEntry> tmp = new Vector<>();
        HashMap<HelpEntry, ScoredHelpEntry> mergeMap = new HashMap<>();
        Vector<ScoredHelpEntry> tmpScores = new Vector<>();
        int cpt = 0;

        if (scores != null) {
            for (ScoredHelpEntry score : scores) {
                ScoredHelpEntry sc = mergeMap.get(score.he);
                if (sc == null) {
                    mergeMap.put(score.he, score);
                } else {
                    // An entry already exists: accumulate score
                    sc.score += score.score;
                }

            }
        }

        // Compute a mergedList
        scores = new Vector<>(mergeMap.values());
    }

    public void sortResults() {
        Collections.sort(scores, Collections.reverseOrder());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("");
        for (ScoredHelpEntry he : scores) {
            sb.append(he.score + ": " + he.he.getPathToHTMLFile() + "\n");
        }
        return sb.toString();
    }

}
