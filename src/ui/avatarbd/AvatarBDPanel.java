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
   * Class AvatarBDPanel
   * Panel for drawing AVATAR blocks
   * Creation: 06/04/2010
   * @version 1.0 06/04/2010
   * @author Ludovic APVRILLE
   * @see
   */

package ui.avatarbd;


import org.w3c.dom.*;

import ui.*;
import ui.avatardd.*;
import java.util.*;
import ui.avatarsmd.AvatarSMDPanel;
import myutil.*;

public class AvatarBDPanel extends TDiagramPanel {
    private LinkedList<AvatarBDStateMachineOwner> validated, ignored;
    private String val = null, ign = null;
    private boolean optimized = true;

    private static final String DEFAULT_MAIN = "void __user_init() {\n}\n\n";

    private String mainCode;


    public  AvatarBDPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        mainCode = DEFAULT_MAIN;
        /*TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
          addMouseListener(tdmm);
          addMouseMotionListener(tdmm);*/
    }

    public boolean actionOnDoubleClick(TGComponent tgc) {
        //System.out.println("Action");
        if (tgc instanceof AvatarBDBlock) {
            AvatarBDBlock b = (AvatarBDBlock)tgc;
            //System.out.println("oldValue:" + b.oldValue);
            return this.changeStateMachineTabName (b.oldValue, b.getValue());
        } else if (tgc instanceof AvatarBDLibraryFunction) {
            return true;
        } else if (tgc instanceof AvatarBDDataType) {
            return true;
        }
        //return false; // because no change made on any diagram
        //}
        return false;
    }

    public boolean actionOnAdd(TGComponent tgc) {
        if (tgc instanceof AvatarBDBlock) {
            AvatarBDBlock abdb = (AvatarBDBlock)(tgc);
            //TraceManager.addDev(" *** add Avatar block *** name=" + abdb.getBlockName());
            ((AvatarDesignPanel) this.tp).addAvatarStateMachineDiagramPanel(abdb.getBlockName());
            this.mgui.setPanelMode ();
            return true;
        } else if (tgc instanceof AvatarBDLibraryFunction) {
            ((AvatarDesignPanel) this.tp).addAvatarStateMachineDiagramPanel(((AvatarBDLibraryFunction) tgc).getFunctionName ());
            this.mgui.setPanelMode ();
            return true;
        }
        return false;
    }

    public boolean actionOnRemove(TGComponent tgc) {
        if (tgc instanceof AvatarBDStateMachineOwner) {
            AvatarBDStateMachineOwner abdb = (AvatarBDStateMachineOwner) tgc;
            this.mgui.removeAvatarBlock (tp, abdb.getOwnerName());
            if (tgc instanceof AvatarBDBlock) {
                LinkedList<AvatarBDBlock> list  = ((AvatarBDBlock) abdb).getFullBlockList();
                for(AvatarBDBlock b: list)
                    mgui.removeAvatarBlock(tp, b.getBlockName());

                for(AvatarBDLibraryFunction b: ((AvatarBDBlock) abdb).getFullLibraryFunctionList())
                    mgui.removeAvatarBlock (tp, b.getFunctionName());
            }

            return true;
        }

        return false;
    }

    public void updateSignalAttachement(AvatarBDBlock _b, AvatarBDPortConnector _conn) {

	
	// Set all signals of B as non connected
	_b.setSignalsAsNonAttached();
	
	
	Iterator iterator = getComponentList().listIterator();
	while(iterator.hasNext()) {
            TGComponent tgc = (TGComponent)(iterator.next());
            if (tgc instanceof AvatarBDPortConnector) {
		AvatarBDPortConnector port = (AvatarBDPortConnector)tgc;
                AvatarBDBlock block1 = port.getAvatarBDBlock1();
                AvatarBDBlock block2 = port.getAvatarBDBlock2();
		if ((block1 != null) && (block2 != null)) {
		    if ((_b == block1) || (_b == block2)) {
			//TraceManager.addDev("Relation found");
			LinkedList<String> l;
			if (_b == block1) {
			    l = port.getListOfSignalsOrigin();
			} else {
			    l = port.getListOfSignalsDestination();
			}

			for(String name: l) {
			    name = AvatarSignal.getSignalNameFromFullSignalString(name);
			    //TraceManager.addDev("Searching for " + name + " in block " + _b);
			    AvatarSignal sig = _b.getAvatarSignalFromName(name);
			    if (sig != null) {
				//TraceManager.addDev("Sig " + name + " is attached to a relation");
				sig.attachedToARelation = true;
			    }
			}
		    }
		}
	    }
	}
    }
    

    public boolean actionOnValueChanged(TGComponent tgc) {
        if (tgc instanceof AvatarBDBlock) {
            //updateAllSignalsOnConnectors();
            return actionOnDoubleClick(tgc);
        }
        return false;
    }

    public boolean areAttributesVisible() {
        return attributesVisible;
    }

    public String getXMLHead() {
        return "<AVATARBlockDiagramPanel name=\"" + name + "\"" + sizeParam()  +" >\n" + displayParam();
    }

    public String getXMLTail() {
        return "</AVATARBlockDiagramPanel>";
    }

    public String getXMLSelectedHead() {
        return "<AVATARBlockDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }

    public String getXMLSelectedTail() {
        return "</AVATARBlockDiagramPanelCopy>";
    }

    public String getXMLCloneHead() {
        return "<AVATARBlockDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }

    public String getXMLCloneTail() {
        return "</AVATARBlockDiagramPanelCopy>";
    }

    public void setConnectorsToFront() {
        ArrayList<TGComponent> list = new ArrayList<TGComponent>();

        for (TGComponent tgc: this.componentList)
            if (!(tgc instanceof TGConnector))
                list.add(tgc);

        //System.out.println("Putting to back ...");
        for(TGComponent tgc1: list) {
            //System.out.println("Putting to back: " + tgc1);
            componentList.remove(tgc1);
            componentList.add(tgc1);
        }
    }

    /*public boolean areAttributesVisible() {
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
      }*/

    public String displayParam() {
        String s = "";
        String [] tmp =  Conversion.wrapText(mainCode);
        String tmps;
        int i;

        for(i=0; i<tmp.length; i++) {
            s += "<MainCode value=\"" + GTURTLEModeling.transformString(tmp[i]) + "\"/>\n";
        }

        if (optimized) {
            s += "<Optimized value=\"true\" />\n";
        } else {
            s += "<Optimized value=\"false\" />\n";
        }

        if (validated == null) {
            s += "<Validated value=\"\" />\n";
        } else {
            s+= "<Validated value=\"";
            for (AvatarBDStateMachineOwner o: validated)
                s += o.getOwnerName() + ";";
            s += "\" />\n";
        }

        if (ignored == null) {
            s += "<Ignored value=\"\" />\n";
        } else {
            s+= "<Ignored value=\"";
            for (AvatarBDStateMachineOwner o: ignored)
                s += o.getOwnerName() + ";";
            s += "\" />\n";
        }


        return s;
    }




    public void loadExtraParameters(Element elt) {
        String s;

        // Main code
        NodeList nl = elt.getElementsByTagName("MainCode");
        //TraceManager.addDev("Extra parameter of block diagram nbOfElements: " + nl.getLength());
        Node n;

        try {
            if (nl.getLength()>0) {
                mainCode = "";
            }
            for(int i=0; i<nl.getLength(); i++) {
                n = nl.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    s = ((Element)n).getAttribute("value");
                    //TraceManager.addDev("Found value=" + s);
                    if (s != null) {
                        mainCode += s + "\n";
                    }
                }

            }
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            TraceManager.addDev("Exception when loading parameter of block diagram:" + e.getMessage());

        }

        // Optimized
        nl = elt.getElementsByTagName("Optimized");
        try {
            if (nl.getLength()>0) {
                n = nl.item(0);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    s = ((Element)n).getAttribute("value");
                    //TraceManager.addDev("Found value=" + s);
                    if (s != null) {
                        if (s.compareTo("true") == 0) {
                            optimized = true;
                        } else {
                            optimized = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            TraceManager.addDev("Exception when loading parameter of block diagram:" + e.getMessage());

        }

        // Validated
        nl = elt.getElementsByTagName("Validated");
        try {
            if (nl.getLength()>0) {
                n = nl.item(0);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    s = ((Element)n).getAttribute("value");
                    //TraceManager.addDev("Found value=" + s);
                    if (s != null) {
                        val = s;
                    }
                }
            }
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            TraceManager.addDev("Exception when loading parameter of block diagram:" + e.getMessage());

        }

        // Ignored
        nl = elt.getElementsByTagName("Ignored");
        try {
            if (nl.getLength()>0) {
                n = nl.item(0);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    s = ((Element)n).getAttribute("value");
                    //TraceManager.addDev("Found value=" + s);
                    if (s != null) {
                        ign = s;
                    }
                }
            }
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            TraceManager.addDev("Exception when loading parameter of block diagram:" + e.getMessage());
        }
    }

    public void updateAllSignalsOnConnectors() {
        for (TGComponent tgc: this.componentList)
            if (tgc instanceof AvatarBDPortConnector) {
                AvatarBDPortConnector port = (AvatarBDPortConnector)tgc;
                port.updateAllSignals();
            }
    }

    public LinkedList<AvatarSignal> getListOfAvailableSignals(AvatarBDBlock _block) {
        LinkedList<AvatarSignal> v = new LinkedList<AvatarSignal> ();

        LinkedList<AvatarSignal> listOfBlock = _block.getSignalList();
        if (listOfBlock.size() == 0)
            return v;

        v.addAll(listOfBlock);

        for (TGComponent tgc: this.componentList)
            if (tgc instanceof AvatarBDPortConnector) {
                AvatarBDPortConnector port = (AvatarBDPortConnector) tgc;
                if (port.getAvatarBDBlock1() == _block) {
                    LinkedList<String> ll = port.getListOfSignalsOrigin();
                    removeSignals(v, ll);
                }
                if (port.getAvatarBDBlock2() == _block) {
                    LinkedList<String> ll = port.getListOfSignalsDestination();
                    removeSignals(v, ll);
                }
            }

        return v;
    }

    public LinkedList<AvatarSignal> getListOfAvailableOutSignals (AvatarBDBlock _block) {
        LinkedList<AvatarSignal> v = new LinkedList<AvatarSignal> ();

        LinkedList<AvatarSignal> listOfBlock = _block.getOutSignalList();
        if (listOfBlock.size() == 0)
            return v;

        v.addAll(listOfBlock);

        for (TGComponent tgc: this.componentList)
            if (tgc instanceof AvatarBDPortConnector) {
                AvatarBDPortConnector port = (AvatarBDPortConnector)tgc;
                if (port.getAvatarBDBlock1() == _block) {
                    LinkedList<String> ll = port.getListOfSignalsOrigin();
                    removeSignals(v, ll);
                }
                if (port.getAvatarBDBlock2() == _block) {
                    LinkedList<String> ll = port.getListOfSignalsDestination();
                    removeSignals(v, ll);
                }
            }

        return v;
    }

    public LinkedList<AvatarSignal> getListOfAvailableInSignals(AvatarBDBlock _block) {
        LinkedList<AvatarSignal> v = new LinkedList<AvatarSignal> ();

        LinkedList<AvatarSignal> listOfBlock = _block.getInSignalList();
        if (listOfBlock.size() == 0)
            return v;

        v.addAll(listOfBlock);

        for (TGComponent tgc: this.componentList)
            if (tgc instanceof AvatarBDPortConnector) {
                AvatarBDPortConnector port = (AvatarBDPortConnector)tgc;
                if (port.getAvatarBDBlock1() == _block) {
                    LinkedList<String> ll = port.getListOfSignalsOrigin();
                    removeSignals(v, ll);
                }
                if (port.getAvatarBDBlock2() == _block) {
                    LinkedList<String> ll = port.getListOfSignalsDestination();
                    removeSignals(v, ll);
                }
            }

        return v;
    }

    // Remove AvatarSignals of v which name is provided in list
    private static void removeSignals(List<AvatarSignal> v, LinkedList<String> list) {
        for(String s: list) {
            Iterator<AvatarSignal> iterator = v.iterator ();
            while (iterator.hasNext ()) {
                AvatarSignal as = iterator.next ();
                if (as.toString().equals (s)) {
                    iterator.remove ();
                    break;
                }
            }
        }
    }

	public HashMap<String, List<String>> getBlockStrings(boolean addAttributes, boolean addStates){
		HashMap<String,List<String>> blockStringMap = new HashMap<String, List<String>>();
		for (AvatarBDBlock block: getFullBlockList()){
			List<String> strs = new ArrayList<String>();
			if (addAttributes){
				for (TAttribute attr: getAllAttributesOfBlock(block.getBlockName())){
					strs.add(attr.getId());
				}
			}
			if (addStates){
				AvatarSMDPanel smd = block.getAvatarSMDPanel();
				strs.addAll(smd.getAllStates());
			}
			blockStringMap.put(block.getBlockName(), strs);		
		}
		return blockStringMap;
	}

    public LinkedList<AvatarBDStateMachineOwner> getFullStateMachineOwnerList() {
        LinkedList<AvatarBDStateMachineOwner> list = new LinkedList<AvatarBDStateMachineOwner>();

        for (TGComponent tgc: this.componentList)
            if (tgc instanceof AvatarBDStateMachineOwner) {
                list.add ((AvatarBDStateMachineOwner) tgc);
                if (tgc instanceof AvatarBDBlock)
                    list.addAll (((AvatarBDBlock) tgc).getFullBlockList());
            }

        return list;
    }

    public LinkedList<AvatarBDBlock> getFullBlockList () {
        LinkedList<AvatarBDBlock> list = new LinkedList<AvatarBDBlock> ();

        for (TGComponent tgc: this.componentList)
            if (tgc instanceof AvatarBDBlock) {
                list.add ((AvatarBDBlock) tgc);
                list.addAll (((AvatarBDBlock) tgc).getFullBlockList());
            }

        return list;
    }

    public LinkedList<AvatarBDLibraryFunction> getFullLibraryFunctionList () {
        LinkedList<AvatarBDLibraryFunction> list = new LinkedList<AvatarBDLibraryFunction> ();

        for (TGComponent tgc: this.componentList)
            if (tgc instanceof AvatarBDLibraryFunction)
                list.add ((AvatarBDLibraryFunction) tgc);

        return list;
    }

    public LinkedList<AvatarBDLibraryFunction> getAllLibraryFunctionsForBlock (String _name) {
        for(AvatarBDStateMachineOwner block: getFullStateMachineOwnerList())
            if (block.getOwnerName().equals (_name))
                return block.getAllLibraryFunctionList ();

        return null;
    }

    public TAttribute getAttributeByBlockName(String _blockName, String attributeName) {
        TAttribute a;
        for(AvatarBDStateMachineOwner block: getFullStateMachineOwnerList())
            if (block.getOwnerName().equals (_blockName))
                return block.getAttributeByName(attributeName);

        return null;
    }

    public LinkedList<TAttribute> getAllAttributesOfBlock (String _name) {
        LinkedList<AvatarBDStateMachineOwner> list = getFullStateMachineOwnerList ();
        for(AvatarBDStateMachineOwner block: list)
            if (block.getOwnerName ().equals (_name))
                return block.getAttributeList ();

        return new LinkedList<TAttribute> ();
    }

    public LinkedList<AvatarMethod> getAllMethodsOfBlock(String _name) {
        for(AvatarBDStateMachineOwner block: getFullStateMachineOwnerList())
            if (block.getOwnerName().equals (_name))
                return block.getAllMethodList();

        return null;
    }

    public LinkedList<AvatarSignal> getAllSignalsOfBlock(String _name) {
        for(AvatarBDStateMachineOwner block: getFullStateMachineOwnerList())
            if (block.getOwnerName().equals (_name))
                return block.getAllSignalList();

        return null;
    }

    public LinkedList<String> getAllTimersOfBlock(String _name) {
        for(AvatarBDStateMachineOwner block: getFullStateMachineOwnerList())
            if (block.getOwnerName().equals (_name))
                return block.getAllTimerList();

        return null;
    }

    public LinkedList<TAttribute> getAttributesOfDataType(String _name) {
        for (TGComponent tgc: this.componentList)
            if (tgc instanceof AvatarBDDataType) {
                AvatarBDDataType adt = (AvatarBDDataType)tgc;
                if (adt.getDataTypeName().compareTo(_name) == 0)
                    return adt.getAttributeList();
            }

        return null;
    }

    public TAttribute getAttribute(String _name, String _nameOfBlock) {
        return this.getAttributeByBlockName(_nameOfBlock, _name);
    }

    public void setMainCode(String s) {
        if (s == null) {
            s = "";
        }
        mainCode = s;
    }

    public String getMainCode() {
        return mainCode;
    }

    public LinkedList<AvatarBDStateMachineOwner> getValidated() {
        if ((val != null) && (validated == null)) {
            makeValidated();
        }
        return validated;
    }

    public LinkedList<AvatarBDStateMachineOwner> getIgnored() {
        if ((ign != null) && (ignored == null)) {
            makeIgnored();
        }
        return ignored;
    }

    public boolean getOptimized() {
        return optimized;
    }


    public void setValidated(LinkedList<AvatarBDStateMachineOwner> _validated) {
        validated = _validated;
    }

    public void setIgnored(LinkedList<AvatarBDStateMachineOwner> _ignored) {
        ignored = _ignored;
    }

    public void setOptimized(boolean _optimized) {
        optimized = _optimized;
    }

    public void makeValidated() {
        TraceManager.addDev("Making validated with val=" + val);
        validated = new LinkedList<AvatarBDStateMachineOwner> ();
        LinkedList<AvatarBDStateMachineOwner> list = getFullStateMachineOwnerList();
        String tmp;

        String split[] = val.split(";");
        for(int i=0; i<split.length; i++) {
            tmp = split[i].trim();
            if (tmp.length() > 0) {
                for (AvatarBDStateMachineOwner block: list) {
                    if (block.getOwnerName().equals (tmp)) {
                        validated.add(block);
                        break;
                    }
                }
            }
        }
        val = null;
    }

    public void makeIgnored() {
        TraceManager.addDev("Making ignored with ign=" + val);
        ignored = new LinkedList<AvatarBDStateMachineOwner> ();
        LinkedList<AvatarBDStateMachineOwner> list = getFullStateMachineOwnerList();
        String tmp;

        String split[] = ign.split(";");
        for(int i=0; i<split.length; i++) {
            tmp = split[i].trim();
            if (tmp.length() > 0) {
                for (AvatarBDStateMachineOwner block: list) {
                    if (block.getOwnerName().equals (tmp)) {
                        ignored.add(block);
                        break;
                    }
                }
            }
        }
        ign = null;
    }

    public ArrayList<String> getAllNonMappedAvatarBlockNames(String _topName, ADDDiagramPanel _tadp, boolean ref, String _name) {

        //Iterator iterator = componentList.listIterator();
        ArrayList<String> list = new ArrayList<String>();
        String name;


        for(AvatarBDBlock block: getFullBlockList()) {

            name = block.getBlockName();
            if (ref && name.equals(_name)) {
                list.add(_topName + "::" + name);
            } else {
                if (!_tadp.isMapped(_topName,  name)) {
                    list.add(_topName + "::" + name);
                }
            }

        }

        return list;
    }

    
    public ArrayList<String> getAllNonMappedAvatarChannelNames(String _topName, ADDDiagramPanel _tadp) {
        ArrayList<String> list = new ArrayList<String>();

        for (TGComponent tgc: this.componentList)
            if (tgc instanceof AvatarBDPortConnector) {
                AvatarBDPortConnector port = (AvatarBDPortConnector)tgc;
		if (port.getListOfSignalsOrigin().size() > 0) {
                    String name = port.getChannelName();
		    TraceManager.addDev("[CHA] Channel name=" + name);
		    if (!_tadp.isChannelMapped(_topName,  name))
			list.add(_topName + "::" + name);
		}
            }

        return list;
    }
}
