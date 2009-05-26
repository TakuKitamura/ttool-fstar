/**Copyright or  or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class TActivityDiagramPanel
 * Panel used for drawing activity diagrams
 * Creation: 12/12/2003
 * @version 1.0 12/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.ad;

//import java.awt.*;
import java.util.*;

import org.w3c.dom.*;
//import org.xml.sax.*;
//import javax.xml.parsers.*;

import ui.*;

public class TActivityDiagramPanel extends TDiagramPanel implements ActivityDiagramPanelInterface {
    
    public  TActivityDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        addComponent(400, 50, TGComponentManager.TAD_START_STATE, false);
        TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);
    }
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
        return false;
    }
    
    public boolean actionOnAdd(TGComponent tgc) {
		System.out.println("Adding " + tgc);
        return false;
    }
    public boolean actionOnValueChanged(TGComponent tgc) {
        return false;
    }
    
    public  boolean actionOnRemove(TGComponent tgc) {
        return false;
    }
    
    public String getXMLHead() {
        return "<TActivityDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() + " >";
    }
    
    public String getXMLTail() {
        return "</TActivityDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<TActivityDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</TActivityDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<TActivityDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</TActivityDiagramPanelCopy>";
    }
    
    public void makeGraphicalOptimizations() {
        // Segments of connector that mask components
        
        // Components over others
        
        // Position correctly guards of choice
    }
    
    public boolean isJavaVisible() {
        return javaVisible;
    }
    
    public void setJavaVisible(boolean b) {
        javaVisible = b;
    }
    
    public String displayParam() {
        String s = "";
        if (javaVisible) {
            s += " java=\"true\"";
        } else {
            s += " java=\"false\"";
        }
        return s;
    }
    
    public void loadExtraParameters(Element elt) {
        String s;
        //System.out.println("Extra parameter");
        try {
            s = elt.getAttribute("java");
            //System.out.println("S=" + s);
            if (s.compareTo("true") ==0) {
                setJavaVisible(true);
            } else {
                setJavaVisible(false);
            }
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            //System.out.println("older format");
            setJavaVisible(false);
        }
    }
    
    public void enhance() {
        //System.out.println("enhance");
        Vector v = new Vector();
        Object o;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = iterator.next();
            if (o instanceof TADStartState){
                enhance(v, (TADStartState)o);
            }
        }
        
        mgui.changeMade(this, MOVE_CONNECTOR);
        repaint();
    }
    
    public void enhance(Vector v, TGComponent tgc) {
        TGComponent tgc1;
        TGConnector tgcon;
        int i;
        
        //System.out.println("Enhancing: " + tgc);
        
        if (tgc == null) {
            return;
        }
        
        if (v.contains(tgc)) {
            return;
        }
        
        v.add(tgc);
        
        //System.out.println("Nb of nexts: " + tgc.getNbNext());
        if (!(tgc instanceof TADStartState)) {
            for(i=0; i<tgc.getNbNext(); i++) {
                tgc1 = getNextTGComponent(tgc, i);
                tgcon = getNextTGConnector(tgc, i);
                if (tgcon.getAutomaticDrawing()) {
                    if ((tgc1 != null) && (tgcon != null)) {
                        tgcon.alignOrMakeSquareTGComponents();
                    }
                }
            }
        }
        
        // Explore next elements
        for(i=0; i<tgc.getNbNext(); i++) {
            tgc1 = getNextTGComponent(tgc, i);
            enhance(v, tgc1);
        }
    }
	
	public boolean hasAutoConnect() {
		return true;
	}
}
