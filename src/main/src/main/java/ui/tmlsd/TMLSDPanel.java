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




package ui.tmlsd;

//import java.awt.*;

import ui.*;

import java.util.Iterator;

/**
 * Class TMLSDPanel
 * Panel for drawing a TML sequence diagram
 * Creation: 17/02/2004
 * @version 1.0 17/02/2004
 * @author Ludovic APVRILLE
 */
public class TMLSDPanel extends TDiagramPanel {
    
    public  TMLSDPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        /*TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);*/
    }
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
        //
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
        return "<TMLSDPanel name=\"" + name + "\"" + sizeParam() + " >";
    }
    
    public String getXMLTail() {
        return "</TMLSDPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<TMLSDPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</TMLSDPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<TMLSDPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</TMLSDPanelCopy>";
    }
//    
//    public void makePostLoadingProcessing() throws MalformedModelingException {
//        TGComponent tgc;
//        
//        /*for(int i=0; i<componentList.size(); i++) {
//            tgc = (TGComponent)(componentList.elementAt(i));
//            if (tgc instanceof TCDTObject) {
//                ((TCDTObject)tgc).postLoadingProcessing();
//            }
//        }*/
//    }
//    
    public TMLSDTransferInstance getTMLSDTransferInstance(String name) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.iterator();
        
        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLSDTransferInstance) {
                if (tgc.getValue().compareTo(name) ==0) {
                    return (TMLSDTransferInstance)tgc;
                }
            }
        }
        return null;
    }			

    public TMLSDControllerInstance getTMLSDControllerInstance(String name) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.iterator();
        
        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLSDControllerInstance) {
                if (tgc.getValue().compareTo(name) ==0) {
                    return (TMLSDControllerInstance)tgc;
                }
            }
        }
        return null;
    }			

    public TMLSDStorageInstance getTMLSDStorageInstance(String name) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLSDStorageInstance) {
                if (tgc.getValue().compareTo(name) ==0) {
                    return (TMLSDStorageInstance)tgc;
                }
            }
        }
        return null;
    }			
    
    
    public TGComponent getSecondTGComponent(TGConnector tgco) {
        TGComponent tmp;
        TGComponent tmp1;
        TGConnectingPoint p2 = tgco.getTGConnectingPointP2();
        Iterator<TGComponent> iterator = componentList.iterator();
        
        while(iterator.hasNext()) {
            tmp = iterator.next();
            tmp1 = tmp.belongsToMeOrSon(p2);
            if (tmp1 != null) {
                return tmp1;
            }
        }
        
        return null;
    }
    
		//TMLSDStorage,Controller,Transfer inherit from TMLSDInstance so they are also of type TMLSDInstance
    public TGConnector messageActionCloserTo(TGComponent tgc, TMLSDInstance sd) {
        int distance = 25;
        TGConnector found = null;
        TGComponent tmp;
        TGConnectingPoint p;
        Iterator<TGComponent> iterator = componentList.iterator();
        
        while(iterator.hasNext()) {
            tmp =iterator.next();
            if (tmp instanceof TGConnectorMessageTMLSD){
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
    
		//TMLSDStorage,Controller,Transfer inherit from TMLSDInstance so they are also of type TMLSDInstance
    public TGConnectingPoint TGConnectingPointActionCloserTo(TGComponent tc1, TGConnector tgco, TMLSDInstance sdi) {
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
    
		//TMLSDStorage,Controller,Transfer inherit from TMLSDInstance so they are also of type TMLSDInstance
    public TGComponent getActionCloserTo(int y, TMLSDInstance sdi) {
        int distance = 25;
        TGComponent tgc, found = null;
        //
        for(int i=0; i<sdi.getNbInternalTGComponent(); i++) {
            tgc = sdi.getInternalTGComponent(i) ;
            //
            if (tgc instanceof TMLSDActionState) {
                if (Math.abs(y-tgc.getY()) < distance) {
                    //
                    found = tgc;
                    distance = Math.abs(y-tgc.getY());
                } else {
                    //
                }
            }
        }
        return found;
    }
	
	//TMLSDStorage,Controller,Transfer inherit from TMLSDInstance so they are also of type TMLSDInstance
	public void increaseInstanceSize(int size) {
		Iterator<TGComponent> iterator = componentList.iterator();
        TGComponent tgc;
		int maxYH = 0;
		
        while(iterator.hasNext()) {
            tgc = iterator.next();
			
			if (tgc instanceof TMLSDInstance) {
				tgc.setUserResize(tgc.getX(), tgc.getY(), tgc.getWidth(), tgc.getHeight() + size);
			}
			
			maxYH = Math.max(maxYH, tgc.getY() + tgc.getHeight());
		}
		
		if (maxYH > getMaxY()) {
			setMaxY(getMaxY() + increment);
            updateSize();
		}
	}
	
	
	//TMLSDStorage,Controller,Transfer inherit from TMLSDInstance so they are also of type TMLSDInstance
	public void alignInstances() {
        TMLSDInstance ontheLeft = null;//, sdi;
        int x = getMaxX(),xtmp;
        int y;
      //  int i;
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.iterator();
        
        // search for the instances which is the most on the left
        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLSDInstance) {
                xtmp = tgc.getX();
                if (xtmp < x) {
                    x = xtmp;
                    ontheLeft = (TMLSDInstance)tgc;
                }
            }
        }
        
        if (ontheLeft == null)
            return;
        
        // move accordingly other instances
        y = ontheLeft.getY();
        iterator = componentList.listIterator();
        while(iterator.hasNext()) {
            tgc = iterator.next();
            if ((tgc instanceof TMLSDInstance) && (tgc !=  ontheLeft)){
                tgc.setCd(tgc.getX(), y);
            }
        }
        
    }

	/*public void changeName( int i, String s ){
		mgui.requestRenameTab( i, s);
	}*/
}
