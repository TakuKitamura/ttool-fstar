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

import myutil.Conversion;
import myutil.GenericTree;
import myutil.TraceManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Class HelpEntry Creation: 28/02/2019 Version 2.0 28/02/2019
 *
 * @author Ludovic APVRILLE
 */
public class HelpEntry implements GenericTree {

    protected static final int KEYWORD_SEARCH_IMPORTANCE = 5;

    protected HelpEntry linkToParent;
    protected Vector<HelpEntry> entries;

    protected String masterKeyword;
    protected String[] keywords;

    protected String pathToHTMLFile;
    protected String htmlContent;
    protected String htmlContentLowerCase;

    public HelpEntry() {

    }

    public String getMasterKeyword() {
        if (masterKeyword == null) {
            return "Help not loaded";
        }
        return masterKeyword;
    }

    public String getPathToHTMLFile() {
        return pathToHTMLFile;
    }

    // Infos are: file of name, master key, list of keywords
    public boolean fillInfos(String infos) {
        infos = infos.trim();
        infos = myutil.Conversion.replaceAllString(infos, "  ", " ");

        String[] splitted = infos.split(" ");
        if (splitted.length < 3) {
            TraceManager.addDev("Split too small");
            return false;
        }

        pathToHTMLFile = splitted[0] + ".html";

        masterKeyword = splitted[1].replaceAll("_", " ");

        keywords = new String[splitted.length - 1];
        for (int i = 0; i < splitted.length - 1; i++) {
            keywords[i] = splitted[i + 1].replaceAll("_", " ");
            ;
        }

        // TraceManager.addDev("Infos ok");
        return true;
    }

    public String getToolTip() {
        if (keywords == null) {
            return "";
        }

        if (keywords.length == 0) {
            return "";
        }

        String ret = "";
        for (int i = 1; i < keywords.length; i++) {
            ret += keywords[i] + " ";
        }
        return ret;
    }

    public int getNbInHierarchy() {
        if (linkToParent == null) {
            return 0;
        }
        return 1 + linkToParent.getNbInHierarchy();
    }

    public void addKid(HelpEntry he) {
        if (entries == null) {
            entries = new Vector<>();
        }
        entries.add(he);
    }

    public boolean hasKids() {
        if (entries == null) {
            return false;
        }

        return entries.size() > 0;
    }

    public int getNbOfKids() {
        if (entries == null) {
            return 0;
        }
        return entries.size();
    }

    public HelpEntry getKid(int index) {
        if (entries == null) {
            return null;
        }
        return entries.get(index);
    }

    public int getIndexOfKid(HelpEntry he) {
        if (entries == null) {
            return 0;
        }
        return entries.indexOf(he);
    }

    public String getHTMLContent() {
        if (htmlContent == null) {
            try {
                URL url = HelpManager.getURL(pathToHTMLFile);
                URLConnection conn = url.openConnection();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                htmlContent = reader.lines().collect(Collectors.joining("\n"));
                // TraceManager.addDev("htmlcontent=" + getHTMLContent());

                htmlContent = filterHTMLContent(htmlContent);

            } catch (Exception e) {
                TraceManager.addDev("Exception when retreiving HTML of " + pathToHTMLFile);
                return "";
            }
        }

        return htmlContent;
    }

    public void setHTMLContent(String HTMLContent) {
        htmlContentLowerCase = HTMLContent;
    }

    private String filterHTMLContent(String input) {

        int index = input.indexOf("<meta http-equiv=");
        if (index == -1) {
            return input;
        }
        String ret1 = input.substring(0, index);
        String ret2 = input.substring(index + 17, input.length());

        index = ret2.indexOf("/>");
        if (index == -1) {
            return input;
        }

        ret2 = ret2.substring(index + 2, ret2.length());

        return ret1 + ret2;
    }

    public HelpEntry getFather() {
        return linkToParent;
    }

    public String toString() {
        if ((masterKeyword == null) || (keywords == null)) {
            return "Help";
        }

        /*
         * String ret = masterKeyword + " ("; for(int i=1; i<keywords.length; i++) { ret
         * += keywords[i] + " "; } ret += ")";
         */

        return masterKeyword.substring(0, 1).toUpperCase() + masterKeyword.substring(1, masterKeyword.length());
    }

    public String printHierarchy(int n) {
        String s = getHTMLContent();
        String ret = "";
        for (int i = 0; i < n; i++) {
            ret += "  ";
        }
        ret += toString() + "\n";
        if (entries != null) {
            for (HelpEntry he : entries) {
                ret += he.printHierarchy(n + 1);
            }
        }
        return ret;
    }

    public int getChildCount() {
        // TraceManager.addDev("GetChild count in " + toString() + " = " +
        // getNbOfKids());
        return getNbOfKids();
    }

    public Object getChild(int index) {
        return getKid(index);
    }

    public int getIndexOfChild(Object child) {
        return getIndexOfKid((HelpEntry) child);
    }

    public HelpEntry getFromMasterKeyword(String search) {
        if (masterKeyword != null) {
            if (masterKeyword.compareTo(search) == 0) {
                return this;
            }
        }
        if (entries != null) {
            HelpEntry ret;
            for (HelpEntry he : entries) {
                ret = he.getFromMasterKeyword(search);
                if (ret != null) {
                    return ret;
                }
            }
        }
        return null;
    }

    public void addEntries(Vector<HelpEntry> list) {
        list.add(this);
        if (entries != null) {
            for (HelpEntry he : entries) {
                he.addEntries(list);
            }
        }
    }

    public boolean hasMasterKeyword(String word) {
        return masterKeyword.compareTo(word) == 0;
    }

    public int hasSimilarWords(String[] words) {
        int result = 0;
        for (String s : words) {
            if (hasKeyword(s)) {
                result++;
            }
        }
        return result;
    }

    public boolean hasKeyword(String word) {
        for (String s : keywords) {
            if (s.compareTo(word) == 0) {
                return true;
            }
        }
        return false;
    }

    public int hasInContent(String[] words) {
        int score = 0;
        for (String word : words) {
            score += hasInContentAWord(word);
        }
        return score;
    }

    public int hasInContentAWord(String word) {
        if (htmlContentLowerCase == null) {
            if (htmlContent == null) {
                htmlContent = getHTMLContent();
            }
            if (htmlContent == null) {
                return 0;
            }
            htmlContentLowerCase = htmlContent.toLowerCase();
        }
        // TraceManager.addDev("Computing nb Ofs:" + word);
        return Conversion.nbOf(htmlContentLowerCase, word);
    }

    public String getKeywords() {
        String ret = "";
        if (keywords != null) {
            for (int i = 0; i < keywords.length; i++) {
                ret += keywords[i] + " ";
            }
        }
        return ret;
    }

    public String getKidsInHTML() {
        String s = "";

        if (entries != null) {
            for (HelpEntry he : entries) {
                s += "<li> ";
                s += "<a href=\"file://" + he.getMasterKeyword() + ".html\"/>" + he.getMasterKeyword() + "</a>  "
                        + he.getKeywords();
                s += " </li>\n<br>\n";
            }
        }

        return s;
    }

    public void searchInKeywords(String[] words, HelpEntry father, Vector<ScoredHelpEntry> scores) {
        int score = hasSimilarWords(words);
        if (score > 0) {
            // father.addKid(this);
            scores.add(new ScoredHelpEntry(KEYWORD_SEARCH_IMPORTANCE * score, this));
            // TraceManager.addDev("Found in keyword:" + toString() + " score=" + score);
        }

        if (entries != null) {
            for (HelpEntry he : entries) {
                he.searchInKeywords(words, father, scores);
            }
        }
    }

    public void searchInContent(String[] words, HelpEntry father, Vector<ScoredHelpEntry> scores) {
        int score = hasInContent(words);
        if (score > 0) {
            // father.addKid(this);
            scores.add(new ScoredHelpEntry(KEYWORD_SEARCH_IMPORTANCE * score, this));
            // TraceManager.addDev("Found in content:" + toString() + " score=" + score);
        }

        if (entries != null) {
            for (HelpEntry he : entries) {
                he.searchInContent(words, father, scores);
            }
        }
    }

}
