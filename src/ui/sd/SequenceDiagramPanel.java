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
 * Class SequenceDiagramPanel
 * Panel for drawing a sequence diagram
 * Creation: 30/09/2004
 * @version 1.0 30/09/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.sd;

//import java.awt.*;
import java.util.*;

import ui.*;

public class SequenceDiagramPanel extends TDiagramPanel {
    
    public  SequenceDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);
    }
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
        //System.out.println("Action");
        /*if (tgc instanceof TCDTClass) {
            TCDTClass t = (TCDTClass)tgc;
            return mgui.newTClassName(t.oldValue, t.getValue());
        } else if (tgc instanceof TCDActivityDiagramBox) {
            if (tgc.getFather() instanceof TCDTClass) {
                mgui.selectTab(tgc.getFather().getValue());
            } else if (tgc.getFather() instanceof TCDTObject) {
                TCDTObject to = (TCDTObject)(tgc.getFather());
                TCDTClass t = to.getMasterTClass();
                if (t != null) {
                    mgui.selectTab(t.getValue());
                }
            }
            return false; // because no change made on any diagram
        }*/
        return false;
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
        return "<SequenceDiagramPanel name=\"" + name + "\"" + sizeParam() + " >";
    }
    
    public String getXMLTail() {
        return "</SequenceDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<SequenceDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</SequenceDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<SequenceDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</SequenceDiagramPanelCopy>";
    }
    
    public void makePostLoadingProcessing() throws MalformedModelingException {
        TGComponent tgc;
        
        /*for(int i=0; i<componentList.size(); i++) {
            tgc = (TGComponent)(componentList.elementAt(i));
            if (tgc instanceof TCDTObject) {
                ((TCDTObject)tgc).postLoadingProcessing();
            }
        }*/
    }
    
    public SDInstance getSDInstance(String name) {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof SDInstance) {
                if (tgc.getValue().compareTo(name) ==0) {
                    return (SDInstance)tgc;
                }
            }
        }
        return null;
    }
    
    
    public void alignInstances() {
        SDInstance ontheLeft = null, sdi;
        int x = getMaxX(),xtmp;
        int y;
        int i;
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        // search for the instances which is the most on the left
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof SDInstance) {
                xtmp = tgc.getX();
                if (xtmp < x) {
                    x = xtmp;
                    ontheLeft = (SDInstance)tgc;
                }
            }
        }
        
        if (ontheLeft == null)
            return;
        
        // move accordingly other instances
        y = ontheLeft.getY();
        iterator = componentList.listIterator();
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if ((tgc instanceof SDInstance) && (tgc !=  ontheLeft)){
                tgc.setCd(tgc.getX(), y);
            }
        }
        
    }
    
    public TGConnectorRelativeTimeSD firstAndConnectedSDRelativeTimeConstraint(TGComponent tgc) {
        TGComponent tmp;
        TGConnectingPoint p1;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tmp = (TGComponent)(iterator.next());
            if (tmp instanceof TGConnectorRelativeTimeSD){
                p1 = ((TGConnector)tmp).getTGConnectingPointP1();
                if (tgc.belongsToMe(p1)) {
                    return (TGConnectorRelativeTimeSD)tmp;
                }
            }
        }
        return null;
    }
    
    public TGComponent getSecondTGComponent(TGConnector tgco) {
        TGComponent tmp;
        TGComponent tmp1;
        TGConnectingPoint p2 = tgco.getTGConnectingPointP2();
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tmp = (TGComponent)(iterator.next());
            tmp1 = tmp.belongsToMeOrSon(p2);
            if (tmp1 != null) {
                return tmp1;
            }
        }
        
        return null;
    }
    
    public TGConnector messageActionCloserTo(TGComponent tgc, SDInstance sd) {
        int distance = 25;
        TGConnector found = null;
        TGComponent tmp;
        TGConnectingPoint p;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tmp = (TGComponent)(iterator.next());
            if (tmp instanceof TGConnectorMessageSD){
                p = ((TGConnector)tmp).getTGConnectingPointP1();
                if (sd.belongsToMe(p)) {
                    if (Math.abs(p.getY() - tgc.getY()) < distance) {
                        distance = Math.abs(p.getY() - tgc.getY());
                        found = (TGConnector)tmp;
                    }
                }
                p = ((TGConnector)tmp).getTGConnectingPointP2();
                if (sd.belongsToMe(p)) {
                    if (Math.abs(p.getY() - tgc.getY()) < distance) {
                        distance = Math.abs(p.getY() - tgc.getY());
                        found = (TGConnector)tmp;
                    }
                }
            }
        }
        return found;
    }
    
    public TGConnectingPoint TGConnectingPointActionCloserTo(TGComponent tc1, TGConnector tgco, SDInstance sdi) {
        TGConnectingPoint p1, p2;
        p1 = tgco.getTGConnectingPointP1();
        p2 = tgco.getTGConnectingPointP2();
        
        boolean hasp1 = sdi.belongsToMe(p1);
        boolean hasp2 = sdi.belongsToMe(p2);
        
        if ((!hasp1) && (!hasp2)) {
            return null;
        }
        
        if ((hasp1) && (!hasp2)) {
            return p1;
        }
        
        if ((!hasp1) && (hasp2)) {
            return p2;
        }
        
        // both belongs to the sdinstance
        int y1 = p1.getY();
        int y2 = p2.getY();
        
        if ((Math.abs(y2-tc1.getY())) < ((Math.abs(y1-tc1.getY())))) {
            return p2;
        } else {
            return p1;
        }
    }
    
    public TGComponent getActionCloserTo(int y, SDInstance sdi) {
        int distance = 25;
        TGComponent tgc, found = null;
        //System.out.println("GetActionCloserTo y=" + y);
        for(int i=0; i<sdi.getNbInternalTGComponent(); i++) {
            tgc = sdi.getInternalTGComponent(i) ;
            //System.out.println("tgc=" + tgc.getName() + " y=" + tgc.getY());
            if (tgc instanceof SDActionState) {
                if (Math.abs(y-tgc.getY()) < distance) {
                    //System.out.println("Found!");
                    found = tgc;
                    distance = Math.abs(y-tgc.getY());
                } else {
                    //System.out.println("Not found!");
                }
            }
        }
        return found;
    }
}