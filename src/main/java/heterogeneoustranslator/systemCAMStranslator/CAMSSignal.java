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




package heterogeneoustranslator.systemCAMStranslator;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.het.*;
import ui.util.IconManager;
import ui.window.JDialogCAMSBlocks;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Vector;


/**
* Class CAMSSignal
* Signals for SystemC-AMS Diagrams
* Creation: 27/06/2017
* @version 1.0 27/06/2017
* @author Côme DEMARIGNY
 */
public class CAMSSignal {
    
    public final static int IN = 0;
    public final static int OUT= 1;
    public final static int HYBRID_IN = 2;
    public final static int HYBRID_OUT= 3;
    public final static int INCORRECT=-1;

    private static int i=0;

    private String name;
    
    public int inout;
    
    public CAMSSignal (String _name, int _inout) {
	inout = _inout;
    }
    

    public CAMSSignal makeclone(){
	return this;
    }

    public static CAMSSignal isAValidSignal(String _name, int _inout) {
        if (_inout==INCORRECT) {
            return null;
        }
	String s = signalName(_name);
        CAMSSignal cs = new CAMSSignal(s, _inout);

        return cs;
    }
    
    public int getInout(){
	return inout;
    }

    public static String signalName(String _n){
	String s="";
	s+= _n + ": " + signalID();
	return s;
    }

    public static int signalID(){i++;return i;}    

}