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


package cli;

import common.ConfigurationTTool;
import launcher.RTLLauncher;
import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import myutil.PluginManager;
import myutil.TraceManager;
import ui.MainGUI;
import ui.util.IconManager;
import avatartranslator.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.BitSet;
import java.util.*;


/**
 * Class Set
 * Creation: 19/03/2019
 * Version 2.0 19/03/2019
 *
 * @author Ludovic APVRILLE
 */
public class BFTask  {

    public String name;
    public int type; //0: HW 1:SW
    public int clb, dsp, mem; // Used only for HW tasks
    public int timing; // i.e. max duration
    private Vector<BFTask> next;
    private Vector<BFTask> previous;

    // Translation to AVATAR
    public AvatarBlock block;


    public BFTask(String name) {
        this.name = name;
        next = new Vector<>();
        previous = new Vector<>();
    }

    public boolean isHW() {
        return type == 0;
    }

    public boolean isSW() {
        return type == 1;
    }

    public void addNext(BFTask t) {
        next.add(t);
    }

    public void addPrevious(BFTask t) {
        previous.add(t);
    }


    public Vector<BFTask> getNext() {
        return next;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(name + ": ");
        if (isHW()) {
            sb.append("hw " + clb + " " + dsp + " " + mem + " " + timing);
        } else {
            sb.append("sw " + timing);
        }

        sb.append(" previous:");
        for(BFTask t: previous) {
            sb.append(t.name + " ");
        }

        sb.append(" next:");
        for(BFTask t: next) {
            sb.append(t.name + " ");
        }

       return sb.toString();
    }

    public void makeBlock(AvatarSpecification avspec) {

        TraceManager.addDev("Making block of " + name);

        Vector<String> unblockedBy = new Vector<>();
        for(BFTask t: previous) {
            String s = "unblockFrom" + t.name;
            unblockedBy.add(s);
        }
        Vector<String> unblock = new Vector<>();
        for(BFTask t: next) {
            String s = "unblock" + t.name;
            unblock.add(s);
        }
        if(isHW()) {
            block = AvatarBlockTemplate.getHWGraphBlock(name, avspec, this, timing, unblockedBy, unblock);
        } else {
            block = AvatarBlockTemplate.getSWGraphBlock(name, avspec, this, timing, unblockedBy, unblock);
        }
    }


}
