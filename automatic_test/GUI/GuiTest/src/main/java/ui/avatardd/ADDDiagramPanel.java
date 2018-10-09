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


package ui.avatardd;


import myutil.TraceManager;
import org.w3c.dom.Element;
import ui.*;

import java.util.ListIterator;
import java.util.Vector;

/**
 * Class ADDDiagramPanel
 * Panel for drawing an avatar dd
 * Creation: 30/06/2014
 *
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @version 1.0 30/06/2014
 */
public class ADDDiagramPanel extends TDiagramPanel implements TDPWithAttributes {
    private int masterClockFrequency = 200; // in MHz

    public ADDDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        /*TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
          addMouseListener(tdmm);
          addMouseMotionListener(tdmm);*/
    }

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

    public boolean actionOnAdd(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
          TCDTClass tgcc = (TCDTClass)(tgc);
          //
          mgui.addTClass(tp, tgcc.getClassName());
          return true;
          }*/
        return false;
    }

    public boolean actionOnRemove(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
          TCDTClass tgcc = (TCDTClass)(tgc);
          mgui.removeTClass(tp, tgcc.getClassName());
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

    public int getMasterClockFrequency() {
        return masterClockFrequency;
    }

    public void setMasterClockFrequency(int _masterClockFrequency) {
        masterClockFrequency = _masterClockFrequency;
    }

    public String getXMLHead() {
        return "<ADDDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() + displayClock() + " >";
    }

    public String getXMLTail() {
        return "</ADDDiagramPanel>";
    }

    public String getXMLSelectedHead() {
        return "<ADDDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }

    public String getXMLSelectedTail() {
        return "</ADDDiagramPanelCopy>";
    }

    public String getXMLCloneHead() {
        return "<ADDDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }

    public String getXMLCloneTail() {
        return "</ADDDiagramPanelCopy>";
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


    public boolean isMapped(String _ref, String _name) {
        ListIterator iterator = componentList.listIterator();
        TGComponent tgc;
        ADDCPUNode node;
        Vector v;
        ADDBlockArtifact artifact;
        int i;
        String name = _ref + "::" + _name;

        while (iterator.hasNext()) {
            tgc = (TGComponent) (iterator.next());
            if (tgc instanceof ADDCPUNode) {
                v = ((ADDCPUNode) (tgc)).getArtifactList();
                for (i = 0; i < v.size(); i++) {
                    artifact = (ADDBlockArtifact) (v.get(i));
                    if (artifact.getValue().equals(name)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isChannelMapped(String _ref, String _name) {
        ListIterator iterator = componentList.listIterator();
        TGComponent tgc;
        ADDMemoryNode node;
        Vector v;
        ADDChannelArtifact artifact;
        int i;
        String name = _ref + "::" + _name;

        while (iterator.hasNext()) {
            tgc = (TGComponent) (iterator.next());
            if (tgc instanceof ADDRAMNode) {
                v = ((ADDRAMNode) (tgc)).getArtifactList();
                for (i = 0; i < v.size(); i++) {
                    artifact = (ADDChannelArtifact) (v.get(i));
                    TraceManager.addDev("Comparing " + artifact.getLongChannelName() + " with " + name);
                    if (artifact.getLongChannelName().equals(name)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /*public void renameMapping(String oldName, String newName) {
      ListIterator iterator = getListOfNodes().listIterator();
      TMLArchiNode node;
      Vector v;
      TMLArchiArtifact artifact;
      ArrayList<TMLArchiCommunicationArtifact> ChannelList;
      ArrayList<TMLArchiEventArtifact> EventList;
      int i;

      while(iterator.hasNext()) {
      node = (TMLArchiNode)(iterator.next());

      // Task mapping

      if ((node instanceof TMLArchiCPUNode) || (node instanceof TMLArchiHWANode)) {
      if (node instanceof TMLArchiCPUNode) {
      v =  ((TMLArchiCPUNode)(node)).getArtifactList();
      //
      } else {
      v =  ((TMLArchiHWANode)(node)).getArtifactList();
      //
      }

      for(i=0; i<v.size(); i++) {
      artifact = (TMLArchiArtifact)(v.get(i));
      if (artifact.getReferenceTaskName().compareTo(oldName) == 0) {
      artifact.setReferenceTaskName(newName);
      }
      }
      }

      // Channel, request mapping
      if( node instanceof TMLArchiCommunicationNode ) {
      ChannelList = ( (TMLArchiCommunicationNode)node ).getChannelArtifactList();
      for( TMLArchiCommunicationArtifact arti: ChannelList )    {
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

      ListIterator iterator = getListOfNodes().listIterator();
      TMLArchiNode node;
      Vector v;
      TMLArchiArtifact artifact;
      ArrayList<TMLArchiCommunicationArtifact> ChannelList;
      ArrayList<TMLArchiEventArtifact> EventList;
      int i;

      while(iterator.hasNext()) {
      node = (TMLArchiNode)(iterator.next());


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

      ListIterator iterator = getListOfNodes().listIterator();
      TMLArchiNode node;
      Vector v;
      TMLArchiArtifact artifact;
      ArrayList<TMLArchiCommunicationArtifact> ChannelList;
      ArrayList<TMLArchiEventArtifact> EventList;
      int i;
      int prio = 0;

      while(iterator.hasNext()) {
      node = (TMLArchiNode)(iterator.next());
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
      }*/

}//End of class
