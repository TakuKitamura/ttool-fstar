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

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import launcher.RTLLauncher;
import myutil.PluginManager;
import myutil.TraceManager;
import ui.MainGUI;
import ui.util.IconManager;
import ui.window.JDialogSystemCGeneration;
import ui.*;

import java.io.File;
import java.util.BitSet;
import java.util.*;


/**
 * Class HelpEntry
 * Creation: 28/02/2019
 * Version 2.0 28/02/2019
 *
 * @author Ludovic APVRILLE
 */
public class HelpEntry  {
    public String pathToHTMLFile;
    public String masterKeyword;
    public String[] keywords;
    public String htmlContent;
    public HelpEntry linkToParent;

    Vector<HelpEntry> entries;


    public HelpEntry() {

    }

    // infos are: file of name, master key, list of keywords
    public boolean fillInfos(String infos) {
        infos = infos.trim();
        infos = myutil.Conversion.replaceAllString(infos, "  ", " ");

        String[] splitted = infos.split(" ");
        if (splitted.length < 3) {
            return false;
        }

        pathToHTMLFile = splitted[0] + ".html";
        masterKeyword = splitted[1];
        keywords = new String[splitted.length-2];
        for(int i = 2; i<splitted.length; i++) {
            keywords[i] = splitted[i+2];
        }

       return true;
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

    public HelpEntry getFather() {
        return linkToParent;
    }

    public String toString() {
        return masterKeyword + " " + keywords.toString();
    }

    public String printHierarchy(int n) {
        String ret = "";
        for (int i=0; i<n; i++){
            ret += "  ";
        }
        ret += toString() + "\n";
        if (entries != null) {
            for(HelpEntry he: entries) {
                ret += he.printHierarchy(n+1);
            }
        }
        return ret;
    }




}
