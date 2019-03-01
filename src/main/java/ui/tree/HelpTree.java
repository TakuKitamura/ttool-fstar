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




package ui.tree;

import help.HelpEntry;
import myutil.GenericTree;
import myutil.TraceManager;
import ui.MainGUI;
import graph.RG;

import java.util.List;

/**
 * Class HelpTree
 * Creation: 19/03/2019
 * Version 2.0 19/03/2019
 * @author Ludovic APVRILLE
 */
public class HelpTree implements GenericTree {
    
    private MainGUI mgui;
    private String name = "Help";
 
    
    public HelpTree(MainGUI _mgui) {
        mgui = _mgui;
    }
    
    // TREE MANAGEMENT
    public String toString() {
        //TraceManager.addDev("To String HelpTree");

        if (mgui == null) {
            return "Not loaded";
        }

        if (mgui.getHelpManager() == null) {
            return "Not loaded";
        }
        return mgui.getHelpManager().getMasterKeyword();
    }
    
    public int getChildCount() {
        //TraceManager.addDev("GetChild count of HelpTree");


        int nb =  mgui.getHelpManager().getNbOfKids();
        if (nb == 0) {
            //TraceManager.addDev("GetChild count of HelpTree: returning" + 1);
        	return 1;
        }


        //TraceManager.addDev("GetChild count of HelpTree:" + nb);
        return nb;
    }
    
    public Object getChild(int index) {
        //TraceManager.addDev("GetChild HelpTree with index=" + index);

        int nb =  mgui.getHelpManager().getNbOfKids();

        if (nb == 0) {
            return "help not loaded yet";
        }

        HelpEntry he = mgui.getHelpManager().getKid(index);
    	if (he == null) {
    		return "Help not loaded";
    	}
    	return he;
    }
    
    public int getIndexOfChild(Object child) {
        if (child instanceof String) {
            return 0;
        }
        if (child instanceof HelpEntry) {
            return mgui.getHelpManager().getIndexOfKid((HelpEntry)child);
        }

        return 0;
    }
}
