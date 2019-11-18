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

import myutil.GenericTree;
import myutil.TraceManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * Class SearchResultHelpEntry
 * Creation: 07/03/2019
 * Version 1.0 07/03/2019
 *
 * @author Ludovic APVRILLE
 */
public class SearchResultHelpEntry extends HelpEntry {
    public static String SEARCH_HEADER = "<h1>Search result</h1>\n<br>\n";

    private Vector<AtomicInteger> scores;

    public SearchResultHelpEntry() {

    }

    public void setScores(Vector<AtomicInteger> _scores) {
        scores = _scores;
    }

    @Override
    public String getHTMLContent() {

        String ret = super.getHTMLContent();
        String kids = getKidsInHTML();
        int index1 = ret.indexOf("<body>");
        if (index1 > -1) {
            index1 += 6;
            ret = ret.substring(0, index1) + "\n" + SEARCH_HEADER + getNbOfKids() + " result(s) found:<br>\n" +
                    kids + ret.substring(index1+1, ret.length());
        }

        //TraceManager.addDev("Resulting HTML of search:" + ret);

        return ret;
    }

    @Override
    public String getKidsInHTML() {
        String s = "";
        int maxScore = getMaxScore();


        if (entries != null) {
            int cpt = 0;
            for (HelpEntry he : entries) {
                s += "<li> ";
                int score = (int)(scores.get(cpt).intValue() * 100.0/maxScore);
                s += "<a href=\"file://" + he.getPathToHTMLFile() + "\"/>" +  he.getMasterKeyword
                        () + "</a>  " +  "(" + score + "%)  " +
                he.getKeywords();
                s += " </li>\n<br>\n";
                cpt ++;
            }
        }

        return s;
    }

    private int getMaxScore() {
        int maxScore = -1;
        if (scores == null) {
            return maxScore;
        }
        for(AtomicInteger i: scores) {
            int val = i.intValue();
            if (val > maxScore) {
                maxScore = val;
            }
        }
        return maxScore;
    }

    public void mergeResults() {
        // Find duplicate sons, and compute final scores for sons
        Vector<HelpEntry> tmp = new Vector<>();
        Vector<AtomicInteger> tmpScores = new Vector<>();
        int cpt = 0;

        if (entries != null) {
            for (HelpEntry he : entries) {
                int indexOriginal = tmp.indexOf(he);
                if (indexOriginal > -1) {
                    AtomicInteger right = tmpScores.get(indexOriginal);
                    right.addAndGet(scores.get(cpt).intValue());
                } else {
                    tmp.add(he);
                    tmpScores.add(new AtomicInteger(scores.get(cpt).intValue()));
                }
                cpt++;
            }
        }
        scores = tmpScores;
        entries = tmp;
    }

    public void sortResults() {
        HashMap<AtomicInteger, HelpEntry> mapScore = new HashMap<>();

        for(int i=0; i<scores.size(); i++) {
            mapScore.put(scores.get(i), entries.get(i));
        }
        Vector<Integer> vect = new Vector<>();
        HashMap<Integer, AtomicInteger> mapInt = new HashMap<>();
        for(int i=0; i<scores.size(); i++) {
            Integer newInt = new Integer(scores.get(i).intValue());
            vect.add(newInt);
            mapInt.put(newInt, scores.get(i));
        }

        Collections.sort(vect, Collections.reverseOrder());
        Vector<HelpEntry> tmp = new Vector<>();
        Vector<AtomicInteger> tmpScores = new Vector<>();
        for(int i=0; i<vect.size(); i++) {
            AtomicInteger ai = mapInt.get(vect.get(i));
            tmpScores.add(ai);
            tmp.add(mapScore.get(ai));
        }
        scores = tmpScores;
        entries = tmp;
    }
}
