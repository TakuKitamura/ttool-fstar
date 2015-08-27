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
   * Class AvatarDesignPanel
   * Management of Avatar block panels
   * Creation: 06/04/2010
   * @version 1.0 06/04/2010
   * @author Ludovic APVRILLE
   * @see MainGUI
   */

package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import ui.avatarbd.*;
import ui.avatardd.*;
import ui.avatarsmd.*;


import proverifspec.*;

import myutil.*;

public class AvatarDesignPanel extends TURTLEPanel {
    public AvatarBDPanel abdp;
    //   public Vector validated, ignored;


    public AvatarDesignPanel(MainGUI _mgui) {
        super(_mgui);
        tabbedPane = new JTabbedPane();
        cl = new ChangeListener() {
                public void stateChanged(ChangeEvent e){
                    mgui.paneDesignAction(e);
                }
            };
        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));
    }

    public void setValidated(Vector _validated) {
        if (abdp != null) {
            abdp.setValidated(_validated);
        }
    }

    public void setIgnored(Vector _ignored) {
        if (abdp != null) {
            abdp.setIgnored(_ignored);
        }
    }

    public void setOptimized(boolean _optimized) {
        if (abdp != null) {
            abdp.setOptimized(_optimized);
        }
    }

    public Vector getValidated() {
        if (abdp != null) {
            return abdp.getValidated();
        }
        return null;
    }

    public Vector getIgnored() {
        if (abdp != null) {
            return abdp.getIgnored();
        }
        return null;
    }

    public boolean getOptimized() {
        if (abdp != null) {
            return abdp.getOptimized();
        }
        return true;
    }

    public AvatarBDPanel getAvatarBDPanel() {
        return abdp;
    }

    public AvatarSMDPanel getAvatarSMDPanel(String name) {
        AvatarSMDPanel asmdp;
        for(int i=1; i<panels.size(); i++) {
            asmdp = (AvatarSMDPanel)(panels.elementAt(i));
            if (asmdp.getName().compareTo(name) ==0) {
                return asmdp;
            }
        }
        return null;
    }

    public void addAvatarStateMachineDiagramPanel(String s) {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        AvatarSMDToolBar toolBarActivity = new AvatarSMDToolBar(mgui);
        toolbars.add(toolBarActivity);

        AvatarSMDPanel asmdp = new AvatarSMDPanel(mgui, toolBarActivity);
        asmdp.tp = this;
        asmdp.setName(s);
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(asmdp);
        asmdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarActivity, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        panels.add(asmdp);
        tabbedPane.addTab(s, IconManager.imgic63, toolBarPanel, "Opens the state machine of " + s);
        //tabbedPane.setMnemonicAt(tabbedPane.getTabCount()-1, '^');
        return;
    }

    public void init() {

        //  Class Diagram toolbar
        AvatarBDToolBar toolBarAvatarBD = new AvatarBDToolBar(mgui);
        toolbars.add(toolBarAvatarBD);

        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        //Class diagram
        abdp = new AvatarBDPanel(mgui, toolBarAvatarBD);
        abdp.setName("AVATAR Block Diagram");
        abdp.tp = this;
        tdp = abdp;
        panels.add(abdp); // Always first in list
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(abdp);
        abdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarAvatarBD, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("AVATAR Design", IconManager.imgic80, toolBarPanel, "Opens the AVATAR Design");
        tabbedPane.setSelectedIndex(0);
        //tabbedPane.setMnemonicAt(tabbedPane.getTabCount()-1, '^');
        mgui.changeMade(abdp, TDiagramPanel.NEW_COMPONENT);

        //jsp.setVisible(true);

    }

    public Vector getAllAttributes(String _name) {
        return abdp.getAllAttributesOfBlock(_name);
    }

    public Vector getAllMethods(String _name) {
        return abdp.getAllMethodsOfBlock(_name);
    }

    public Vector getAllSignals(String _name) {
        return abdp.getAllSignalsOfBlock(_name);
    }

    public Vector getAllTimers(String _name) {
        return abdp.getAllTimersOfBlock(_name);
    }

    public String saveHeaderInXml() {
        return "<Modeling type=\"AVATAR Design\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
    }

    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }

    public String toString() {
        return mgui.getTitleAt(this) + " (AVATAR Design)";
    }

    public void resetMetElements() {
        //TraceManager.addDev("Reset met elements");
        TGComponent tgc;

        for(int i=0; i<panels.size(); i++) {
            ListIterator iterator = ((TDiagramPanel)(panels.get(i))).getComponentList().listIterator();
            while(iterator.hasNext()) {
                tgc = (TGComponent)(iterator.next());
                tgc.setAVATARMet(0);
                tgc.setInternalAvatarMet(0);

            }
        }

    }



    public LinkedList<TGComponent> getListOfComponentsInMutex() {
        TGComponent tgc;
        TDiagramPanel tdp;

        LinkedList<TGComponent> list = new LinkedList<TGComponent>();

        for(int i=0; i<panels.size(); i++) {
            tdp = (TDiagramPanel)(panels.get(i));
            if (tdp instanceof AvatarSMDPanel) {
                ListIterator iterator = ((TDiagramPanel)(panels.get(i))).getComponentList().listIterator();
                while(iterator.hasNext()) {
                    tgc = (TGComponent)(iterator.next());
                    tgc.getAllCheckableInvariant(list);
                }
            }
        }

        return list;

    }

    public TGComponent hasCheckableMasterMutex() {
        TGComponent tgc, tgctmp;
        for(int i=0; i<panels.size(); i++) {
            tdp = (TDiagramPanel)(panels.get(i));
            if (tdp instanceof AvatarSMDPanel) {
                ListIterator iterator = ((TDiagramPanel)(panels.get(i))).getComponentList().listIterator();
                while(iterator.hasNext()) {
                    tgc = (TGComponent)(iterator.next());
                    tgctmp = tgc.hasCheckableMasterMutex();
                    if (tgctmp != null) {
                        //TraceManager.addDev("Found element with master mutex: " + tgctmp);
                        return tgctmp;
                    }
                }
            }
        }

        return null;
    }

    public void removeAllMutualExclusionWithMasterMutex() {
        TGComponent tgc;
        for(int i=0; i<panels.size(); i++) {
            tdp = (TDiagramPanel)(panels.get(i));
            if (tdp instanceof AvatarSMDPanel) {
                ListIterator iterator = ((TDiagramPanel)(panels.get(i))).getComponentList().listIterator();
                while(iterator.hasNext()) {
                    tgc = (TGComponent)(iterator.next());
                    tgc.removeAllMutualExclusionWithMasterMutex();
                }
            }
        }
    }

    public void reinitMutualExclusionStates() {
        TGComponent tgc;
        for(int i=0; i<panels.size(); i++) {
            tdp = (TDiagramPanel)(panels.get(i));
            if (tdp instanceof AvatarSMDPanel) {
                ListIterator iterator = ((TDiagramPanel)(panels.get(i))).getComponentList().listIterator();
                while(iterator.hasNext()) {
                    tgc = (TGComponent)(iterator.next());
                    if (tgc instanceof AvatarSMDState) {
                        ((AvatarSMDState)tgc).reinitMutualExclusionStates();
                    }
                }
            }
        }
    }

    public void clearGraphicalInfoOnInvariants() {

    }


    public void resetModelBacktracingProVerif() {
        if (abdp == null) {
            return;
        }

        // Reset confidential attributes
        for(AvatarBDBlock block1: abdp.getFullBlockList()) {
            block1.resetConfidentialityOfAttributes();
        }

        // Reset reachable states
        for(int i=0; i<panels.size(); i++) {
            tdp = (TDiagramPanel)(panels.get(i));
            if (tdp instanceof AvatarSMDPanel) {
                ((AvatarSMDPanel)tdp).resetStateSecurityInfo();
            }
        }
    }

    public void modelBacktracingProVerif(ProVerifOutputAnalyzer pvoa) {

        if (abdp == null) {
            return;
        }

        resetModelBacktracingProVerif();

        String block, attr, state;
        int index;
        TAttribute a;
        int i;
        ListIterator iterator;
        TGComponent tgc;

        // Confidential attributes
        for(String s: pvoa.getSecretTerms()) {
            index = s.indexOf("__");
            if (index != -1) {
                block = s.substring(0, index);
                attr = s.substring(index+2, s.length());
                index = attr.indexOf("__");
                if (index != -1) {
                    attr = attr.substring(0, index);
                }
                //TraceManager.addDev("Analyzing block=" + block + " attr=" + attr);
                a = abdp.getAttributeByBlockName(block, attr);
                if (a != null) {
                    //TraceManager.addDev("Setting conf to ok");
                    a.setConfidentialityVerification(TAttribute.CONFIDENTIALITY_OK);
                }
            }
        }

        for(String s: pvoa.getNonSecretTerms()) {
            index = s.indexOf("__");
            if (index != -1) {
                block = s.substring(0, index);
                attr = s.substring(index+2, s.length());
                index = attr.indexOf("__");
                if (index != -1) {
                    attr = attr.substring(0, index);
                }
                //TraceManager.addDev("Analyzing block=" + block + " attr=" + attr);
                a = abdp.getAttributeByBlockName(block, attr);
                if (a != null) {
                    //TraceManager.addDev("Setting conf to ok");
                    a.setConfidentialityVerification(TAttribute.CONFIDENTIALITY_KO);
                }
            }
        }

        // Reachable states
        for(String s: pvoa.getReachableEvents()) {
            index = s.indexOf("__");
            if (index != -1) {
                block = s.substring(index+2, s.length());
                index = block.indexOf("__");
                if (index != -1) {
                    state = block.substring(index+2, block.length());
                    block = block.substring(0, index);
                    TraceManager.addDev("Block=" + block + " state=" + state);
                    for(i=0; i<panels.size(); i++) {
                        tdp = (TDiagramPanel)(panels.get(i));
                        if ((tdp instanceof AvatarSMDPanel) && (tdp.getName().compareTo(block) == 0)){
                            iterator = ((TDiagramPanel)(panels.get(i))).getComponentList().listIterator();
                            while(iterator.hasNext()) {
                                tgc = (TGComponent)(iterator.next());
                                if (tgc instanceof AvatarSMDState) {
                                    ((AvatarSMDState)tgc).setSecurityInfo(AvatarSMDState.REACHABLE, state);
                                }
                            }

                        }
                    }
                }
            }
        }

        for(String s: pvoa.getNonReachableEvents()) {
            index = s.indexOf("__");
            if (index != -1) {
                block = s.substring(index+2, s.length());
                index = block.indexOf("__");
                if (index != -1) {
                    state = block.substring(index+2, block.length());
                    block = block.substring(0, index);
                    TraceManager.addDev("Block=" + block + " state=" + state);
                    for(i=0; i<panels.size(); i++) {
                        tdp = (TDiagramPanel)(panels.get(i));
                        if ((tdp instanceof AvatarSMDPanel) && (tdp.getName().compareTo(block) == 0)){
                            iterator = ((TDiagramPanel)(panels.get(i))).getComponentList().listIterator();
                            while(iterator.hasNext()) {
                                tgc = (TGComponent)(iterator.next());
                                if (tgc instanceof AvatarSMDState) {
                                    ((AvatarSMDState)tgc).setSecurityInfo(AvatarSMDState.NOT_REACHABLE, state);
                                }
                            }

                        }
                    }
                }
            }
        }

    }

    public ArrayList<String> getAllNonMappedAvatarBlockNames(String _name, ADDDiagramPanel _tadp, boolean ref, String name) {
        return abdp.getAllNonMappedAvatarBlockNames(_name, _tadp, ref, name);
    }

    public ArrayList<String> getAllNonMappedAvatarChannelNames(String _name, ADDDiagramPanel _tadp, boolean ref, String name) {
        return abdp.getAllNonMappedAvatarChannelNames(_name, _tadp);
    }


}
