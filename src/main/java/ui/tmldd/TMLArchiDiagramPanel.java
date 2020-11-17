/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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

package ui.tmldd;

import myutil.TraceManager;
import org.w3c.dom.Element;
import ui.*;

import java.util.*;

/**
   * Class TMLArchiDiagramPanel
   * Panel for drawing an architecture diagram
   * Creation: 19/09/2007
   * @version 1.1 30/05/2014
   * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLArchiDiagramPanel extends TDiagramPanel implements TDPWithAttributes {

    public static final int VIEW_TASK_MAPPING = 16;
    public static final int VIEW_CHANNEL_MAPPING = 8;
    public static final int VIEW_COMM_PATTERN = 4;
    public static final int VIEW_PORT_INTERFACE = 2;
    public static final int VIEW_SECURITY_MAPPING = 1;
    public HashMap<TGComponent,List<PairOfTGC>> listOfCP;

    private int masterClockFrequency = 200; // in MHz

    protected int view = 31;

    public  TMLArchiDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        /*TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
          addMouseListener(tdmm);
          addMouseMotionListener(tdmm);*/
    }

    @Override
    public boolean actionOnDoubleClick(TGComponent tgc) {
        //
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

    @Override
    public boolean actionOnAdd(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
          TCDTClass tgcc = (TCDTClass)(tgc);
          //
          mgui.addTClass(tp, tgcc.getClassName());
          return true;
          }*/
        return false;
    }

    @Override
    public boolean actionOnRemove(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
          TCDTClass tgcc = (TCDTClass)(tgc);
          mgui.removeTClass(tp, tgcc.getClassName());
          resetAllInstancesOf(tgcc);
          return true;
          }*/
        return false;
    }

    public void handleCPOnDoubleClick(TGComponent node) {
        if (node != null && node instanceof TMLArchiCPNode) {
            listOfCP = getListOfCPNodes();
        } else if (node != null) {
            for (TGComponent node1 : listOfCP.keySet()) {
                Vector <String> tempList = new Vector<>();
                boolean isContain = false;
                for (int i = 0; i < listOfCP.get(node1).size(); i++) {
                    tempList.add(listOfCP.get(node1).get(i).getName() + " " + listOfCP.get(node1).get(i).getTGC().getName());
                    if(listOfCP.get(node1).get(i).getTGC() == node) isContain = true;
                }
                if (!isContain)
                    continue;
                else
                    ((TMLArchiCPNode) node1).setMappedUnits(tempList);
            }
        } else {
            System.out.println(" Nothing is selected");
        }
    }
    
	public void replaceArchComponent(TGComponent tgc, TGComponent newtgc){
		fatherOfRemoved = tgc.getFather();

        for (TGComponent t : this.componentList) {
            if (t == tgc) {  
                //Reroute connectors to new component
            	for (int i = 0; i < tgc.getNbConnectingPoint(); i++) {
            		TGConnectingPoint cp = tgc.tgconnectingPointAtIndex(i);
            		Iterator<TGComponent> iterator = this.componentList.iterator();
            		while (iterator.hasNext()) {

                		TGComponent tconn = iterator.next();
                		if (tconn instanceof TMLArchiConnectorNode) {
                    		TMLArchiConnectorNode tgcon = (TMLArchiConnectorNode) tconn;
                    		if (cp == tgcon.getTGConnectingPointP1()){
                    			tgcon.setP1(newtgc.findFirstFreeTGConnectingPoint(true, true));
                    			tgcon.getTGConnectingPointP1().setFree(false);
                    		}
                    		if (cp == tgcon.getTGConnectingPointP2()) {
                    			tgcon.setP2(newtgc.findFirstFreeTGConnectingPoint(true, true));
                 			    tgcon.getTGConnectingPointP2().setFree(false);
                        	}
                       	}
                    }
                }
            }
       }
                
       componentList.remove(tgc);
       actionOnRemove(tgc);
       tgc.actionOnRemove();
       componentList.add(newtgc);
       actionOnRemove(newtgc);
       newtgc.actionOnRemove();
       return;
	}

    @Override
    public boolean actionOnValueChanged(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
          return actionOnDoubleClick(tgc);
          }*/
        listOfCP = getListOfCPNodes();
        return false;
    }

    public int getMasterClockFrequency() {
        return masterClockFrequency;
    }

    public void setMasterClockFrequency(int _masterClockFrequency) {
        masterClockFrequency = _masterClockFrequency;
    }

    @Override
    public String getXMLHead() {
        return "<TMLArchiDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() + displayClock() + zoomParam() + " >"; // Issue #31
    }

    @Override
    public String getXMLTail() {
        return "</TMLArchiDiagramPanel>";
    }

    @Override
    public String getXMLSelectedHead() {
        return "<TMLArchiDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }

    @Override
    public String getXMLSelectedTail() {
        return "</TMLArchiDiagramPanelCopy>";
    }

    @Override
    public String getXMLCloneHead() {
        return "<TMLArchiDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }

    @Override
    public String getXMLCloneTail() {
        return "</TMLArchiDiagramPanelCopy>";
    }

    public String displayParam() {
        String s = " attributes=\"";
        s += getAttributeState();
        s += "\"";
        return s;
    }

    public String displayClock() {
        String s = " masterClockFrequency=\"";
        s += masterClockFrequency;
        s += "\"";
        return s;
    }

    public void loadExtraParameters(Element elt) {
        String s;
        //
        try {
            s = elt.getAttribute("attributes");
            //
            int attr = Integer.decode(s).intValue();
            setAttributes(attr % 3);
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            //
            setAttributes(0);
        }

        try {
            s = elt.getAttribute("masterClockFrequency");
            //
            masterClockFrequency = Math.abs(Integer.decode(s).intValue());
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            //
            masterClockFrequency = 200;
        }
    }

    /*public boolean isFree(ArtifactTClassGate atg) {
      TGConnectorLinkNode tgco;
      TGComponent tgc;
      Iterator iterator = componentList.listIterator();

      while(iterator.hasNext()) {
      tgc = (TGComponent)(iterator.next());
      if (tgc instanceof TGConnectorLinkNode) {
      tgco = (TGConnectorLinkNode)tgc;
      if (tgco.hasArtifactTClassGate(atg)) {
      return false;
      }
      }
      }

      return true;
      }*/

    public HashMap<TGComponent, List<PairOfTGC>> getListOfCPNodes() {
        HashMap<TGComponent,List<PairOfTGC>> listOfCP = new HashMap<TGComponent,List<PairOfTGC>>();
        List<TGComponent> ll = new LinkedList<TGComponent>();
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        while(iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLArchiCPNode) {
                listOfCP.put(tgc, new ArrayList<PairOfTGC>());
                for (String name : ((TMLArchiCPNode) tgc).getMappedUnits()) {
                    for (int j = 0; j < componentList.size(); j++) {
                        if (name.contains(componentList.get(j).getName())) {
                            PairOfTGC temp = new PairOfTGC(componentList.get(j),name.substring(0,name.indexOf(":")+1));
                            listOfCP.get(tgc).add(temp);
                            break;
                        }
                    }
                }
            }
        }
        return listOfCP;
    }

    public List<TGComponent> getListOfNodes() {
        List<TGComponent> ll = new LinkedList<TGComponent>();
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();

        while(iterator.hasNext()) {
            tgc = iterator.next();
            
            if (tgc instanceof TMLArchiCPUNode) {
                ll.add( (TMLArchiCPUNode) tgc );
            }

            if (tgc instanceof TMLArchiHWANode) {
                ll.add( (TMLArchiHWANode) tgc );
            }

            if (tgc instanceof TMLArchiFPGANode) {
                ll.add( (TMLArchiFPGANode) tgc );
            }

            if (tgc instanceof TMLArchiCommunicationNode) {
                ll.add( (TMLArchiCommunicationNode) tgc );
            }

		    if (tgc instanceof TGComponentPlugin) {
		    	ll.add(tgc);
		    }
        }

        return ll;
    }

    public List<TGComponent> getListOfMappableExecutionNodes() {
        List<TGComponent> ll = new LinkedList<TGComponent>();
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();

        while(iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLArchiCPUNode) {
                ll.add( (TMLArchiCPUNode) tgc );
            }

            if (tgc instanceof TMLArchiHWANode) {
                TMLArchiHWANode node = (TMLArchiHWANode)tgc;

                if (node.getNbInternalTGComponent() == 0) {
                    ll.add((TMLArchiHWANode) tgc);
                }
            }

            if (tgc instanceof TMLArchiFPGANode) {
                ll.add( (TMLArchiFPGANode) tgc );
            }
        }

        return ll;
    }

    public List<TGComponent> getListOfLinks() {
        List<TGComponent> ll = new LinkedList<TGComponent> ();
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();

        while(iterator.hasNext()) {
            tgc = iterator.next();
            
            if (tgc instanceof TMLArchiConnectorNode) {
                ll.add(tgc);
            }
        }

        return ll;
    }

    public boolean isMapped(String _ref, String _name) {

        //TraceManager.addDev("isMapped Ref:" + _ref + " name=" + _name);

        Iterator<TGComponent> iterator = getListOfNodes().listIterator();
        TGComponent node;
        Vector<TMLArchiArtifact> v;
        TMLArchiArtifact artifact;
        int i;
        String name = _ref + "::" + _name;

        while(iterator.hasNext()) {
            node = iterator.next();

            //TraceManager.addDev("Testing node: " + node.getName());
            
            if (node instanceof TMLArchiCPUNode) {
                v =  ((TMLArchiCPUNode) node ).getArtifactList();
                
                for(i=0; i<v.size(); i++) {
                    artifact = v.get(i);
                    
                    if (artifact.getValue().equals(name)) {
                        return true;
                    }
                }
            }

            if (node instanceof TMLArchiFPGANode) {

                v =  ((TMLArchiFPGANode) node ).getArtifactList();
                //TraceManager.addDev("FPGANode: " + ((TMLArchiFPGANode) node).getNodeName() + " artifact list:" + v.size());

                for(i=0; i<v.size(); i++) {
                    artifact = v.get(i);

                    if (artifact.getValue().equals(name)) {
                        return true;
                    }
                }
            }

            if (node instanceof TMLArchiHWANode) {
                v =  ((TMLArchiHWANode) node ).getArtifactList();

                for(i=0; i<v.size(); i++) {
                    artifact = v.get(i);

                    if (artifact.getValue().equals(name)) {
                        return true;
                    }
                }
            }
        }

        //TraceManager.addDev("Not mapped");

        return false;
    }

    public void renameMapping(String oldName, String newName) {
        Iterator<TGComponent> iterator = getListOfNodes().listIterator();
        TGComponent node;
        Vector<TMLArchiArtifact> v;
        TMLArchiArtifact artifact;
        List<TMLArchiCommunicationArtifact> ChannelList;
        List<TMLArchiEventArtifact> EventList;
        int i;

        while(iterator.hasNext()) {
            node = iterator.next();

            // Task mapping
            if ((node instanceof TMLArchiCPUNode) || (node instanceof TMLArchiHWANode) || (node instanceof TMLArchiFPGANode)) {
                if (node instanceof TMLArchiCPUNode) {
                    v =  ((TMLArchiCPUNode)(node)).getArtifactList();
                    //
                } else if (node instanceof TMLArchiHWANode){
                    v =  ((TMLArchiHWANode)(node)).getArtifactList();
                    //
                } else {
                    v =  ((TMLArchiFPGANode)(node)).getArtifactList();
                }

                for(i=0; i<v.size(); i++) {
                    artifact = v.get(i);
                    if (artifact.getReferenceTaskName().compareTo(oldName) == 0) {
                        artifact.setReferenceTaskName(newName);
                    }
                }
            }

            // Channel, request mapping
            if( node instanceof TMLArchiCommunicationNode ) {
                ChannelList = ( (TMLArchiCommunicationNode)node ).getChannelArtifactList();
                for( TMLArchiCommunicationArtifact arti: ChannelList )  {
                    if( arti.getReferenceCommunicationName().compareTo( oldName ) == 0) {
                        arti.setReferenceCommunicationName( newName );
                    }
                }
                //Event mapping
                EventList = ((TMLArchiCommunicationNode)node).getEventArtifactList();
                for(TMLArchiEventArtifact arti: EventList) {
                    if( arti.getReferenceEventName().compareTo( oldName ) == 0 ) {
                        arti.setReferenceEventName( newName );
                    }
                }
            }
        }
    }

    public void setPriority( String _name, int _priority ) {
        Iterator<TGComponent> iterator = getListOfNodes().iterator();
        TGComponent node;
        //Vector v;
       // TMLArchiArtifact artifact;
        List<TMLArchiCommunicationArtifact> ChannelList;
        List<TMLArchiEventArtifact> EventList;
       // int i;

        while(iterator.hasNext()) {
            node = iterator.next();


            // Channel, request mapping
            if( node instanceof TMLArchiCommunicationNode ) {
                ChannelList = ( (TMLArchiCommunicationNode)node ).getChannelArtifactList();
                for( TMLArchiCommunicationArtifact arti: ChannelList ) {
                    if( arti.getFullValue().compareTo( _name ) == 0) {
                        arti.setPriority(_priority);
                    }
                }
                //Event mapping
                EventList = ( (TMLArchiCommunicationNode)node ).getEventArtifactList();
                for( TMLArchiEventArtifact arti: EventList ) {
                    if( arti.getFullValue().compareTo( _name ) == 0) {
                        arti.setPriority( _priority );
                    }
                }
            }
        }
    }

    public int getMaxPriority( String _name ) {

        Iterator<TGComponent> iterator = getListOfNodes().iterator();
        TGComponent node;
       // Vector v;
        //TMLArchiArtifact artifact;
        List<TMLArchiCommunicationArtifact> ChannelList;
        List<TMLArchiEventArtifact> EventList;
        //int i;
        int prio = 0;

        while(iterator.hasNext()) {
            node = iterator.next();
            //Channel, request mapping
            if( node instanceof TMLArchiCommunicationNode ) {
                ChannelList = ( (TMLArchiCommunicationNode)node ).getChannelArtifactList();
                for( TMLArchiCommunicationArtifact arti: ChannelList ) {
                    if( arti.getFullValue().compareTo( _name ) == 0) {
                        prio = Math.max(prio, arti.getPriority());
                    }
                }
                //Event mapping
                EventList = ((TMLArchiCommunicationNode)node).getEventArtifactList();
                for( TMLArchiEventArtifact arti: EventList) {
                    if( arti.getFullValue().compareTo( _name ) == 0) {
                        prio = Math.max( prio, arti.getPriority() );
                    }
                }
            }
        }
        
        return prio;
    }

    public void setCurrentView(int _index) {
		TraceManager.addDev("SelectedView=" + _index);
		view = _index;
		repaint();
    }

    public boolean inCurrentView(TGComponent tgc) {
    	
    	boolean res = (tgc instanceof TMLArchiElementInterface);
    	int tmp = view;
    	
    	if (view >= VIEW_TASK_MAPPING) {
    		res = res || (tgc instanceof TMLArchiTaskInterface);
    		view -= 16;
    	}
    	
    	if (view >= VIEW_CHANNEL_MAPPING) {
    		res = res || (tgc instanceof TMLArchiChannelInterface);
    		view -= 8;
    	}
    	
    	if (view >= VIEW_COMM_PATTERN) {
    		res = res || (tgc instanceof TMLArchiCPInterface);
    		view -= 4;
    	}
    	
    	if (view >= VIEW_PORT_INTERFACE) {
    		res = res || (tgc instanceof TMLArchiPortInterface);
    		view -= 2;
    	}
    	
    	if (view >= VIEW_SECURITY_MAPPING) {
    		res = res || (tgc instanceof TMLArchiSecurityInterface);
    		view -= 1;
    	}
    	
    	view = tmp;
    	return res;
    }

    public void removeAllArtifacts() {
        for(TGComponent tgc: componentList) {
            if (tgc instanceof SwallowTGComponent) {
                tgc.removeAllInternalComponents();
            }
        }
    }

    // Task name is build as diagram__taskname
    public boolean addTaskToNode(String nodeName, String fullTaskName) {

        TraceManager.addDev("Adding task " + fullTaskName + " to " + nodeName);

        if (fullTaskName.indexOf("__") == -1) {
            return false;
        }
        String[] tasksID = fullTaskName.split("__");


        int ID = 10;
        for(TGComponent tgc: componentList) {
            if (tgc instanceof TMLArchiNode) {
                TMLArchiNode node = (TMLArchiNode)tgc;
                if (node.getName().compareTo(nodeName) == 0) {
                    if ((node instanceof TMLArchiCPUNode) || (node instanceof TMLArchiFPGANode) ||  (node instanceof TMLArchiHWANode)) {
                        TMLArchiArtifact arti = new TMLArchiArtifact(node.getX() + ID, node.getY() + ID,
                                node.getCurrentMinX(), node.getCurrentMaxX(), node.getCurrentMinY(), node.getCurrentMaxY(), true, node, this);
                        ID += 5;
                        arti.setReferenceTaskName(tasksID[0]);
                        arti.setTaskName(tasksID[1]);
                        arti.makeFullValue();
                        node.addSwallowedTGComponent(arti, 5, 5);
                        return true;
                    }
                }
            }
        }
        return false;
    }



    public boolean addRandomTasks(String []taskNames) {
        for(String taskName: taskNames) {
            if (!addRandomTask(taskName)) {
                return false;
            }
        }

        return true;
    }

    public boolean addRandomTask(String taskName) {
        List<TGComponent> list = getListOfMappableExecutionNodes();
        if (list.size() == 0){
            return false;
        }

        int max = list.size();
        Random r = new Random();
        int nb =  r.nextInt(max);

        return addTaskToNode(list.get(nb).getName(), taskName);
    }

    public class PairOfTGC {
        private TGComponent tgc;
        private String name;
        public PairOfTGC(TGComponent _tgc, String _name) {
            tgc = _tgc;
            name = _name;
        }
        public TGComponent getTGC() {
            return tgc;
        }
        public String getName() {
            return name;
        }
    }

}//End of class
