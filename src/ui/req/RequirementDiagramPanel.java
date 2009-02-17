/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 * Class RequirementDiagramPanel
 * Panel for drawing requirement diagrams
 * Creation: 15/05/2006
 * @version 1.0 15/05/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.req;

//import java.awt.*;
import java.util.*;

import ui.*;

public class RequirementDiagramPanel extends TDiagramPanel implements TDPWithAttributes {
    public Vector validated, ignored;
    
    public  RequirementDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);
    }
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
        return true;
    }
    
    public boolean actionOnAdd(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
            TCDTClass tgcc = (TCDTClass)(tgc);
            mgui.addTClass(tgcc.getClassName());
            return true;
        }*/
        return false;
    }
    
    public boolean actionOnRemove(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
            TCDTClass tgcc = (TCDTClass)(tgc);
            mgui.removeTClass(tgcc.getClassName());
            resetAllInstancesOf(tgcc);
            return true;
        }*/
        return false;
    }
    
    public boolean actionOnValueChanged(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
            return actionOnDoubleClick(tgc);
        }*/
        return false;
    }
    
    public String getXMLHead() {
        return "<TRequirementDiagramPanel name=\"" + name + "\"" + sizeParam() + zoomParam() + " >";
    }
    
    public String getXMLTail() {
        return "</TRequirementDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<TRequirementDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</TRequirementDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<TRequirementDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</TRequirementDiagramPanelCopy>";
    }
    
    
    public void makePostLoadingProcessing() throws MalformedModelingException {
        
    }
    
    /*public boolean isSDCreated(String name) {
        return mgui.isSDCreated(tp, name);
    }
     
    public boolean isIODCreated(String name) {
        return mgui.isIODCreated(tp, name);
    }
     
   public boolean openSequenceDiagram(String name) {
       return mgui.openSequenceDiagram(name);
   }
     
   public boolean openIODiagram(String name) {
       return mgui.openIODiagram(name);
   }
     
   public boolean createSequenceDiagram(String name) {
       boolean b = mgui.createSequenceDiagram(tp, name);
       //mgui.changeMade(mgui.getSequenceDiagramPanel(name), TDiagramPanel.NEW_COMPONENT);
       return b;
   }*/
    
   /* public boolean createIODiagram(String name) {
       boolean b = mgui.createIODiagram(tp, name);
       //mgui.changeMade(mgui.getSequenceDiagramPanel(name), TDiagramPanel.NEW_COMPONENT);
       return b;
   }*/
    
    public int nbOfVerifyStartingAt(TGComponent tgc) {
        ListIterator iterator = getComponentList().listIterator();
        TGComponent tgc1, tgc2;
        TGConnectingPoint p;
        
        int cpt = 0;
        
        while(iterator.hasNext()) {
            tgc1 = (TGComponent)(iterator.next());
            if (tgc1 instanceof TGConnectorVerify) {
                p = ((TGConnectorVerify)(tgc1)).getTGConnectingPointP1();
                if (tgc.belongsToMeOrSon(p) != null) {
                    cpt ++;
                }
            }
        }
        
        return cpt;
    }
	
	public LinkedList<Requirement> getAllRequirements() {
		LinkedList<Requirement> list = new LinkedList<Requirement>();
		TGComponent tgc;
		
		ListIterator iterator = getComponentList().listIterator();
		
		while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof Requirement) {
				list.add((Requirement)tgc);
			}
		}
		
		return list;
		
	}
    
    public boolean isLinkedByVerifyTo(TGComponent tgc1, TGComponent tgc2) {
        ListIterator iterator = getComponentList().listIterator();
        TGComponent tgc;
        TGConnectingPoint p1, p2;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TGConnectorVerify) {
                p1 = ((TGConnectorVerify)(tgc)).getTGConnectingPointP1();
                p2 = ((TGConnectorVerify)(tgc)).getTGConnectingPointP2();
                if ((tgc1.belongsToMeOrSon(p1) != null) && (tgc2.belongsToMeOrSon(p2)!=null)) {
                    return true;
                }
            }
        }
        
        return false;
    }
	
	public void enhance() {
		autoAdjust();
    }
    
}







