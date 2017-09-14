/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enst.fr
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




package ui.het;


import myutil.TraceManager;
import org.w3c.dom.Element;
import ui.*;

import java.util.*;

/**
   * Class CAMSBlockDiagramPanel
   * Panel for drawing TML Blocks
   * Creation: 26/06/2017
   * @version 1.0 26/06/2017
   * @author Côme DEMARIGNY
 */
public class CAMSBlockDiagramPanel extends TDiagramPanel implements TDPWithAttributes {

    public  CAMSBlockDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
    }

    public void initFromDiplodocus(){
	TGComponentManager.addComponent(100,100,TGComponentManager.CAMS_BLOCK,this);
	TGComponentManager.addComponent(600,100,TGComponentManager.CAMS_BLOCK,this);
    }

    public boolean actionOnDoubleClick(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
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
	    }*/
         return false;
     }

     public boolean actionOnAdd(TGComponent tgc) {
          return false;
      }

     public boolean actionOnRemove(TGComponent tgc) {
         return false;
     }

    public boolean actionOnValueChanged(TGComponent tgc) {
        return false;
    }

	public void setConnectorsToFront() {
		TGComponent tgc;
		
		//System.out.println("list size=" + componentList.size());
		
        Iterator iterator = componentList.listIterator();
        
		ArrayList<TGComponent> list = new ArrayList<TGComponent>();
		
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			if (!(tgc instanceof TGConnector)) {
				list.add(tgc);
			}
		}
		
		//System.out.println("Putting to back ...");
		for(TGComponent tgc1: list) {
			//System.out.println("Putting to back: " + tgc1);
			componentList.remove(tgc1);
			componentList.add(tgc1);
		}
	}

    public String getXMLHead() {
        return "<CAMSBlockDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() + zoomParam() +" >";
    }

    public String getXMLTail() {
        return "</CAMSBlockDiagramPanel>";
    }

    public String getXMLSelectedHead() {
        return "<CAMSBlockDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }

    public String getXMLSelectedTail() {
        return "</CAMSBlockDiagramPanelCopy>";
    }

    public String getXMLCloneHead() {
        return "<CAMSBlockDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }

    public String getXMLCloneTail() {
        return "</CAMSBlockDiagramPanelCopy>";
    }



    public boolean areAttributesVisible() {
        return attributesVisible;
    }


    public boolean areChannelVisible() {
        return synchroVisible;
    }

    public void setAttributesVisible(boolean b) {
        attributesVisible = b;
    }


    public void setChannelVisible(boolean b) {
        channelVisible = b;
    }

    public String displayParam() {
        String s = "";
        if (channelsVisible) {
            s += " channels=\"true\"";
        } else {
            s += " channels=\"false\"";
        }
        if (eventsVisible) {
            s += " events=\"true\"";
        } else {
            s += " events=\"false\"";
        }
        if (requestsVisible) {
            s += " requests=\"true\"";
        } else {
            s += " requests=\"false\"";
        }

        return s;
    }

    public boolean areAllVisible() {
        return channelsVisible && eventsVisible && requestsVisible;
    }

    public boolean areChannelsVisible() {
        return channelsVisible;
    }

    public boolean areEventsVisible() {
        return eventsVisible;
    }

    public boolean areRequestsVisible() {
        return requestsVisible;
    }

    public void setChannelsVisible(boolean b) {
        channelsVisible = b;
    }

    public void setEventsVisible(boolean b) {
        eventsVisible = b;
    }

    public void setRequestsVisible(boolean b) {
        requestsVisible = b;
    }
}
