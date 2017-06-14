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
 * Class TMLTaskDiagramPanel
 * Panel for drawing TML tasks
 * Creation: 27/10/2005
 * @version 1.0 27/10/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcd;


import org.w3c.dom.Element;
import ui.*;
import ui.tmldd.TMLArchiDiagramPanel;

import java.util.ArrayList;
import java.util.Iterator;

public class TMLTaskDiagramPanel extends TDiagramPanel {
    
    public  TMLTaskDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        /*TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);*/
    }
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
        //System.out.println("Action");
        if (tgc instanceof TMLTaskOperator) {
            TMLTaskOperator t = (TMLTaskOperator)tgc;
            return mgui.newTMLTaskName(tp, t.oldValue, t.getValue());
        } else if (tgc instanceof TMLActivityDiagramBox) {
            if (tgc.getFather() instanceof TMLTaskOperator) {
                mgui.selectTab(tp, tgc.getFather().getValue());
            }
            return false; // because no change made on any diagram
        }
        return false;
    }
    
    public boolean actionOnAdd(TGComponent tgc) {
        //System.out.println("Action on add!");
        if (tgc instanceof TMLTaskOperator) {
            TMLTaskOperator tmlt = (TMLTaskOperator)(tgc);
            //System.out.println(" *** add tclass *** name=" + tmlt.getTaskName());
            mgui.addTMLTask(tp, tmlt.getTaskName());
            return true;
        } else if (tgc instanceof TMLChannelOperator) {
			setChannelsVisible(true);
		} else if (tgc instanceof TMLEventOperator) {
			setEventsVisible(true);
		} else if (tgc instanceof TMLRequestOperator) {
			setRequestsVisible(true);
		}
        return false;
    }
    
    public boolean actionOnRemove(TGComponent tgc) {
        if (tgc instanceof TMLTaskOperator) {
            TMLTaskOperator tgcc = (TMLTaskOperator)(tgc);
            mgui.removeTMLTask(tp, tgcc.getTaskName());
            //resetAllInstancesOf(tgcc);
            return true;
        }
        return false;
    }
    
    public boolean actionOnValueChanged(TGComponent tgc) {
        if (tgc instanceof TMLTaskOperator) {
            return actionOnDoubleClick(tgc);
        }
        return false;
    }
    
    public String getXMLHead() {
        return "<TMLTaskDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() +" >";
    }
    
    public String getXMLTail() {
        return "</TMLTaskDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<TMLTaskDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</TMLTaskDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<TMLTaskDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</TMLTaskDiagramPanelCopy>";
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
    
    public boolean isConnectedToTasks(TMLCompositionOperator co) {
        return (getTask1ToWhichIamConnected(co) != null) && (getTask2ToWhichIamConnected(co) != null);
    }
    
    public TMLTaskInterface getTask1ToWhichIamConnected(TMLCompositionOperator co) {
        TGConnectorTMLAssociationNav tgctmlan = getTGConnectorAssociationOf(co);
        TGComponent tgc;
        //System.out.println("tmlan t1?");
        if (tgctmlan != null) {
            //System.out.println("tmlan found t1");
            tgc = getTopComponentToWhichBelongs(tgctmlan.getTGConnectingPointP1());
            if ((tgc != null) && (tgc instanceof TMLTaskInterface)) {
                return (TMLTaskInterface) tgc;
            }
        }
        return null;
    }
    
    public TMLTaskInterface getTask2ToWhichIamConnected(TMLCompositionOperator co) {
        TGConnectorTMLAssociationNav tgctmlan = getTGConnectorAssociationOf(co);
        TGComponent tgc;
        //System.out.println("tmlan t2?");
        if (tgctmlan != null) {
            //System.out.println("tmlan found t2");
            tgc = getTopComponentToWhichBelongs(tgctmlan.getTGConnectingPointP2());
            if ((tgc != null) && (tgc instanceof TMLTaskInterface)) {
                return (TMLTaskInterface) tgc;
            }
        }
        return null;
    }
    
    public TGConnectorTMLAssociationNav getTGConnectorAssociationOf(TMLCompositionOperator tcd) {
        int i;
        TGConnectingPoint p1, p2;
        TGConnector tgco;
        TGConnectorTMLCompositionOperator tgcoco;
        TGComponent tgc;
        
        for(i=0; i<tcd.getNbConnectingPoint(); i++) {
            //System.out.println("titi");
            p1 = tcd.tgconnectingPointAtIndex(i);
            tgco = getConnectorConnectedTo(p1);
            if (tgco != null) {
                //System.out.println("Found tgco");
            }
            if ((tgco != null) && (tgco instanceof TGConnectorTMLCompositionOperator)){
                //System.out.println("toto");
                tgcoco = (TGConnectorTMLCompositionOperator)tgco;
                if (p1 == tgcoco.getTGConnectingPointP1()) {
                    p2 = tgcoco.getTGConnectingPointP2();
                } else {
                    p2 = tgcoco.getTGConnectingPointP1();
                }
                
                // p2 now contains the connecting point of a association
                tgc = getComponentToWhichBelongs(p2);
                if ((tgc != null) && (!p2.isFree()) && (tgc instanceof TGConnectorTMLAssociationNav)) {
                     //System.out.println("tutu");
                    return (TGConnectorTMLAssociationNav)tgc;
                }
            }
        }
        return null;
    }
    
    public boolean connectedToVisible(TGConnectorTMLAssociationNav tgconav) {
        TGConnectorTMLCompositionOperator tgcoco = tgconav.getTGConnectorTMLCompositionOperator();
        if (tgcoco == null) {
            return true;
        }
        return connectedToVisible(tgcoco);
    }
    
    public boolean connectedToVisible(TGConnectorTMLCompositionOperator tgcoco) {
        TGConnectingPoint p2 = tgcoco.getTGConnectingPointP2();
        TGComponent tgc = getComponentToWhichBelongs(p2);
        if (tgc instanceof TMLCompositionOperator) {
            return ((TMLCompositionOperator)tgc).isToggledVisible();
        }
        return false;
    }
    
    /*public void makePostLoadingProcessing() throws MalformedModelingException {
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
    }*/
    
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
    
    public void loadExtraParameters(Element elt) {
        String s;
        //System.out.println("Extra parameter");
        try {
            s = elt.getAttribute("channels");
            //System.out.println("S=" + s);
            if (s.compareTo("false") ==0) {
                setChannelsVisible(false);
            } else {
                setChannelsVisible(true);
            }
            s = elt.getAttribute("events");
            if (s.compareTo("false") ==0) {
                setEventsVisible(false);
            } else {
                setEventsVisible(true);
            }
            s = elt.getAttribute("requests");
            if (s.compareTo("false") ==0) {
                setRequestsVisible(false);
            } else {
                setRequestsVisible(true);
            }
            
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            //System.out.println("older format");
            setChannelsVisible(true);
            setEventsVisible(true);
            setRequestsVisible(true);
        }
    }
	
	public ArrayList<String> getAllTMLTaskNames(String _topname) {
		TGComponent tgc;
        Iterator iterator = componentList.listIterator();
		ArrayList<String> list = new ArrayList<String>();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLTaskOperator) {
				list.add(_topname + "::" + ((TMLTaskOperator)tgc).getTaskName());
            }
        }
		
		return list;
	}

	public ArrayList<String> getAllTMLCommunicationNames(String _topname) {
		TGComponent tgc;
        Iterator iterator = componentList.listIterator();
		ArrayList<String> list = new ArrayList<String>();
		String name = "";
		String type = "";
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLCompositionOperator) {
				if (tgc instanceof TMLEventOperator) {
					name = ((TMLEventOperator)tgc).getEventName();
					type = "Event";
				}
				if (tgc instanceof TMLChannelOperator) {
					name = ((TMLChannelOperator)tgc).getChannelName();
					type = "Channel";
				}
				if (tgc instanceof TMLRequestOperator) {
					name = ((TMLRequestOperator)tgc).getRequestName();
					type = "Request";
				}
				
				list.add(_topname + "::" + name + " (" + type + ")");
            }
        }
		
		return list;
	}

    public ArrayList<String> getAllTMLChannelNames( String _topname )   {
		TGComponent tgc;
        Iterator iterator = componentList.listIterator();
		ArrayList<String> list = new ArrayList<String>();
		String name = "";
		String type = "";
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLCompositionOperator) {
				if (tgc instanceof TMLChannelOperator) {
					name = ((TMLChannelOperator)tgc).getChannelName();
					type = "Channel";
				}
				list.add(_topname + "::" + name );
            }
        }
		
		return list;
    }

	public ArrayList<String> getAllTMLEventNames( String _topname ) {
		TGComponent tgc;
   	Iterator iterator = componentList.listIterator();
		ArrayList<String> list = new ArrayList<String>();
		String name = "";
		String type = "";
        
        while( iterator.hasNext() ) {
					tgc = (TGComponent)( iterator.next() );
          if (tgc instanceof TMLCompositionOperator) {
						if (tgc instanceof TMLEventOperator) {
							name = ((TMLEventOperator)tgc).getEventName();
							type = "Event";
						}
						list.add( _topname + "::" + name + " (" + type + ")" );
          }
        }
		return list;
	}
	
	public ArrayList<String> getAllNonMappedTMLTaskNames(String _topName, TMLArchiDiagramPanel _tadp, boolean ref, String _name) {
		TGComponent tgc;
        Iterator iterator = componentList.listIterator();
		ArrayList<String> list = new ArrayList<String>();
		String name;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLTaskOperator) {
				name = ((TMLTaskOperator)tgc).getTaskName();
				if (ref && name.equals(_name)) {
					list.add(_topName + "::" + name);
				} else {
					if (!_tadp.isMapped(_topName,  name)) {
							list.add(_topName + "::" + name);
					}
				}
            }
        }
		
		return list;
	}
	
	public TMLTaskOperator getTaskByName(String _name) {
		TGComponent tgc;
        Iterator iterator = componentList.listIterator();
		ArrayList<String> list = new ArrayList<String>();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLTaskOperator) {
				if (((TMLTaskOperator)tgc).getTaskName().equals(_name)) {
					return ((TMLTaskOperator)tgc);
				}
            }
        }
		
		return null;
	}
    
}
