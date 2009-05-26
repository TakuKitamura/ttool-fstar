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
 * Class TClassDiagramPanel
 * Panel for drawing a class diagram
 * Creation: 28/08/2003
 * @version 1.0 28/08/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.cd;


import java.util.*;

import org.w3c.dom.*;


import ui.*;
import translator.*;

public class TClassDiagramPanel extends TDiagramPanel implements ClassDiagramPanelInterface {
    
    public  TClassDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);
    }
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
        //System.out.println("Action");
        if (tgc instanceof TCDTClass) {
            TCDTClass t = (TCDTClass)tgc;
            return mgui.newTClassName(tp, t.oldValue, t.getValue());
        } else if (tgc instanceof TCDActivityDiagramBox) {
            if (tgc.getFather() instanceof TCDTClass) {
                mgui.selectTab(tp, tgc.getFather().getValue());
            } else if (tgc.getFather() instanceof TCDTObject) {
                TCDTObject to = (TCDTObject)(tgc.getFather());
                TCDTClass t = to.getMasterTClass();
                if (t != null) {
                    mgui.selectTab(tp, t.getValue());
                }
            }
            return false; // because no change made on any diagram
        }
        return false;
    }
    
    public boolean actionOnAdd(TGComponent tgc) {
        if (tgc instanceof TCDTClass) {
            TCDTClass tgcc = (TCDTClass)(tgc);
            //System.out.println(" *** add tclass *** name=" + tgcc.getClassName());
            mgui.addTClass(tp, tgcc.getClassName());
            return true;
        }
        return false;
    }
    
    public boolean actionOnRemove(TGComponent tgc) {
        if (tgc instanceof TCDTClass) {
            TCDTClass tgcc = (TCDTClass)(tgc);
            mgui.removeTClass(tp, tgcc.getClassName());
            resetAllInstancesOf(tgcc);
            return true;
        }
        return false;
    }
    
    public boolean actionOnValueChanged(TGComponent tgc) {
        if (tgc instanceof TCDTClass) {
            return actionOnDoubleClick(tgc);
        }
        return false;
    }
    
    public String getXMLHead() {
        return "<TClassDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() +" >";
    }
    
    public String getXMLTail() {
        return "</TClassDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<TClassDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</TClassDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<TClassDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</TClassDiagramPanelCopy>";
    }
    
 
    
    public void makePostLoadingProcessing() throws MalformedModelingException {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TCDTObject) {
                ((TCDTObject)tgc).postLoadingProcessing();
            }
        }
    }
    
    public TCDTData findTData(String name) {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TCDTData) {
                if (tgc.getValue().equals(name)) {
                    return (TCDTData)tgc;
                }
            }
        }
        
        return null;
    }
    
    public TCDTClass getTCDTClass(String name) {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TCDTClass) {
                if (((TCDTClass)tgc).getClassName().equals(name)) {
                    return (TCDTClass)tgc;
                }
            }
        }
        
        return null;
    }
    
    public boolean areAttributesVisible() {
        return attributesVisible;
    }
    
    public boolean areGatesVisible() {
        return gatesVisible;
    }
    
    public boolean areSynchroVisible() {
        return synchroVisible;
    }
    
    public void setAttributesVisible(boolean b) {
        attributesVisible = b;
    }
    
    public void setGatesVisible(boolean b) {
        gatesVisible = b;
    }
    
    public void setSynchroVisible(boolean b) {
        synchroVisible = b;
    }
    
    public String displayParam() {
        String s = "";
        if (attributesVisible) {
            s += " attributes=\"true\"";
        } else {
            s += " attributes=\"false\"";
        }
        if (gatesVisible) {
            s += " gates=\"true\"";
        } else {
            s += " gates=\"false\"";
        }
        if (synchroVisible) {
            s += " synchro=\"true\"";
        } else {
            s += " synchro=\"false\"";
        }
        
        return s;
    }
    
    public void loadExtraParameters(Element elt) {
        String s;
        //System.out.println("Extra parameter");
        try {
            s = elt.getAttribute("attributes");
            //System.out.println("S=" + s);
            if (s.compareTo("false") ==0) {
                setAttributesVisible(false);
            } else {
                setAttributesVisible(true);
            }
            s = elt.getAttribute("gates");
            if (s.compareTo("false") ==0) {
                setGatesVisible(false);
            } else {
                setGatesVisible(true);
            }
            s = elt.getAttribute("synchro");
            if (s.compareTo("false") ==0) {
                setSynchroVisible(false);
            } else {
                setSynchroVisible(true);
            }
            
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            //System.out.println("older format");
            setAttributesVisible(true);
            setGatesVisible(true);
            setSynchroVisible(true);
        }
    }
	
	public void makeStateActionsOf(TClassInterface tgc) {
		Vector tclasses = new Vector();
		tclasses.add(tgc);
		
		DesignPanel dp = (DesignPanel)(mgui.getCurrentTURTLEPanel());
		DesignPanelTranslator dpt = new DesignPanelTranslator(dp);
		TURTLEModeling tm = dpt.generateTURTLEModeling(tclasses, "");
		
		/*String name = tgc.getClassName();
		tdp = (TDiagramPanel)(tgc.getBehaviourDiagramPanel());
		
		LinkedList list = tdp.getComponentList();
		Iterator iterator = list.listIterator();
		
		TADActionState action;
		
		TGComponent comp;
		while(iterator.hasNext()) {
			comp = (TGComponent)(iterator.next());
			if (comp instanceof TADActionState) {
				action = (TADActionState)comp;
				makeStateActionOf(tgc, action);
			}
		}*/
	}
	
	/*public void makeStateActionOf(TClassInterface tgc, TADActionState action) {
		String tmp;
		int index;
		
		String value = action.getAction();
		index = value.indexOf("=")
		if (index == -1) {
			// Can be only a gate
		} else {
			// Can be only a parameter
			tmp = value.substring(0, index).trim();
			
		}
	}*/
    
}
