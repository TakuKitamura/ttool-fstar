/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class TURTLEOSClassDiagramPanel
 * Panel for drawing a TURTLE-OS class diagram
 * Creation: 29/09/2006
 * @version 1.0 29/09/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.oscd;

//import java.awt.*;
import java.util.*;

//import org.w3c.dom.*;
//import org.xml.sax.*;
//import javax.xml.parsers.*;

import ui.*;

public class TURTLEOSClassDiagramPanel extends TDiagramPanel implements ClassDiagramPanelInterface {
    
    public TURTLEOSClassDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);
    }
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
        //System.out.println("Action");
        if (tgc instanceof TOSClass) {
            TOSClass t = (TOSClass)tgc;
            return mgui.newTOSClassName(tp, t.oldValue, t.getValue());
        } else if (tgc instanceof TOSCDActivityDiagramBox) {
            if (tgc.getFather() instanceof TOSClass) {
                mgui.selectTab(tp, tgc.getFather().getValue());
            }
            return false; // because no change made on any diagram
        }
        return false;
    }
    
    public boolean actionOnAdd(TGComponent tgc) {
        if (tgc instanceof TOSClass) {
            TOSClass tgcc = (TOSClass)(tgc);
            //System.out.println(" *** add tclass *** name=" + tgcc.getClassName());
            mgui.addTOSClass(tp, tgcc.getClassName());
            return true;
        }
        return false;
    }
    
    public boolean actionOnRemove(TGComponent tgc) {
       if (tgc instanceof TOSClass) {
            TOSClass tgcc = (TOSClass)(tgc);
            mgui.removeTOSClass(tp, tgcc.getClassName());
            return true;
        }
        return false;
    }
    
    public boolean actionOnValueChanged(TGComponent tgc) {
        if (tgc instanceof TOSClass) {
            return actionOnDoubleClick(tgc);
        }
        return false;
    }
    
    public String getXMLHead() {
        return "<TURTLEOSClassDiagramPanel name=\"" + name + "\"" + sizeParam() + " >";
    }
    
    public String getXMLTail() {
        return "</TURTLEOSClassDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<TURTLEOSClassDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</TURTLEOSClassDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<TURTLEOSClassDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</TURTLEOSClassDiagramPanelCopy>";
    }
    
    public Vector getAllClasses() {
      Vector v = new Vector();
      
      ListIterator iterator = getComponentList().listIterator();
      
      while(iterator.hasNext()) {
        TGComponent tgc = (TGComponent)(iterator.next());
        if (tgc instanceof TClassInterface) {
           v.add(tgc);
        }
      }
      
      return v;
      
    }
    
}
