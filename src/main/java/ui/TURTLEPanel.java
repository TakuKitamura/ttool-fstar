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


package ui;

import myutil.DraggableTabbedPaneCallbackInterface;
import myutil.GenericTree;
import myutil.TraceManager;
import ui.tmlcp.TMLCPRefAD;
import ui.tmlcp.TMLCPRefSD;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Class TURTLEPanel
 * Management of TURTLE panels
 * Creation: 14/01/2005
 * @version 1.0 14/01/2005
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
public abstract class TURTLEPanel implements GenericTree, DraggableTabbedPaneCallbackInterface {
    public JTabbedPane tabbedPane;
    protected MainGUI mgui;
    public Vector<TToolBar> toolbars;
    public JPanel toolBarPanel;
    public Vector<TDiagramPanel> panels;
    protected ChangeListener cl;
    protected TDiagramPanel tdp;

    public TURTLEPanel(MainGUI _mgui) {
        mgui = _mgui;
        toolbars = new Vector<TToolBar>();
        panels = new Vector<TDiagramPanel>();
    }

    public abstract void init();
    public abstract String saveHeaderInXml(String extensionToName);
    public abstract String saveTailInXml();

    public TDiagramPanel panelAt(int index) {
        return panels.elementAt(index);
    }

    public boolean hasTDiagramPanel(TDiagramPanel _tdp) {
        for(int i=0; i<panels.size(); i++) {
            if (panels.get(i) == _tdp) {
                return true;
            }
        }

        return false;
    }

    public void getAllCheckedTGComponent(ArrayList<TGComponent> _list) {
        for(int i=0; i<panels.size(); i++) {
            panelAt(i).getAllCheckedTGComponent(_list);
        }
    }
    
    public void getAllCheckableTGComponent(ArrayList<TGComponent> _list) {
        for(int i=0; i<panels.size(); i++) {
            panelAt(i).getAllCheckableTGComponent(_list);
        }
    }
    
    
    public void getAllLatencyChecks(ArrayList<TGComponent> _list){
        for(int i=0; i<panels.size(); i++) {
            panelAt(i).getAllLatencyChecks(_list);
        }
    }


    public void removeElementAt(int index) {
        panels.removeElementAt(index);
        toolbars.removeElementAt(index);
    }

    public StringBuffer saveInXML() {
        return saveInXML(null);
    }

    public StringBuffer saveInXML(String extensionToName) {

        TDiagramPanel tdp;
        StringBuffer sb = new StringBuffer();
        sb.append(saveHeaderInXml(extensionToName));
        StringBuffer s;
        for(int i=0; i<panels.size(); i++) {
            tdp = panels.elementAt(i);
            s = tdp.saveInXML();
            if (s == null) {
                //
                return null;
            }
            sb.append(s);
            sb.append("\n\n");
        }
        sb.append(saveTailInXml());
        return sb;
    }

    public StringBuffer saveInXML(int indexOfDiagram) {

        TDiagramPanel tdp;
        StringBuffer sb = new StringBuffer();
        sb.append(saveHeaderInXml(null));
        StringBuffer s;

        tdp = panels.elementAt(indexOfDiagram);
        s = tdp.saveInXML();
        if (s == null) {
            //
            return null;
        }
        sb.append(s);
        sb.append("\n\n");

        sb.append(saveTailInXml());
        return sb;
    }

    public String toString() {
        return "TURTLE Modeling";
    }

    public int getChildCount() {
        return panels.size();
    }

    public Object getChild(int index) {
        return panels.elementAt(index);
    }

    public int getIndexOfChild(Object child) {
        int index = panels.indexOf(child);
        if (index > -1) {
            return index;
        }
        return panels.size();
    }

    public Vector<TDiagramPanel> getPanels()    {
        return panels;
    }

    public boolean nameInUse(String s) {
        for(int i = 0; i<tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).compareTo(s) == 0) {
                return true;
            }
        }
        return false;
    }

    public String generateNameIfInUse(String s) {
        if (!nameInUse(s)) {
            return s;
        }

        String tmp;
        for(int i=0; i<100000; i++) {
            tmp = s + "_" + i;
            if (!nameInUse(tmp)) {
                return tmp;
            }
        }
        return null;
    }

    public void requestRemoveTab(int index) {
        if (index >= panels.size()) {
            return;
        }

        panels.removeElementAt(index);
        tabbedPane.remove(index);
        mgui.changeMade(null, -1);
    }

    public void requestMoveRightTab(int index) {
        //
        if (index > panels.size()-2) {
            return;
        }
        requestMoveTabFromTo(index, index+1);
        mgui.changeMade(null, -1);
    }

    public void requestMoveLeftTab(int index) {
        //
        if (index < 1) {
            return;
        }
        requestMoveTabFromTo(index, index-1);
        mgui.changeMade(null, -1);
    }

    public void requestMoveTabFromTo(int src, int dst) {

        // Get all the properties
        Component comp = tabbedPane.getComponentAt(src);
        String label = tabbedPane.getTitleAt(src);
        Icon icon = tabbedPane.getIconAt(src);
        Icon iconDis = tabbedPane.getDisabledIconAt(src);
        String tooltip = tabbedPane.getToolTipTextAt(src);
        boolean enabled = tabbedPane.isEnabledAt(src);
        int keycode = tabbedPane.getMnemonicAt(src);
        int mnemonicLoc = tabbedPane.getDisplayedMnemonicIndexAt(src);
        Color fg = tabbedPane.getForegroundAt(src);
        Color bg = tabbedPane.getBackgroundAt(src);

        // Remove the tab
        tabbedPane.remove(src);

        // Add a new tab
        tabbedPane.insertTab(label, icon, comp, tooltip, dst);

        // Restore all properties
        tabbedPane.setDisabledIconAt(dst, iconDis);
        tabbedPane.setEnabledAt(dst, enabled);
        tabbedPane.setMnemonicAt(dst, keycode);
        tabbedPane.setDisplayedMnemonicIndexAt(dst, mnemonicLoc);
        tabbedPane.setForegroundAt(dst, fg);
        tabbedPane.setBackgroundAt(dst, bg);

        Object o = panels.elementAt(src);
        panels.removeElementAt(src);
        panels.insertElementAt((TDiagramPanel)o, dst);

        tabbedPane.setSelectedIndex(dst);
    }

    public void requestRenameTab(int index) {
        String s = (String)JOptionPane.showInputDialog(mgui.frame, "TTool modeling:", "Name=", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101, null, tabbedPane.getTitleAt(index));
        TraceManager.addDev("Testing new name:" + s);
        if ((s != null) && (s.length() > 0)){
            // name already in use?
            // Test if valid name
            if (!mgui.isAValidTabName(s)) {
                JOptionPane.showMessageDialog(this.mgui.frame,
                        "Error: the new name is not valid",
                        "Name modification",
                        JOptionPane.ERROR_MESSAGE);
            } else {

                if (!nameInUse(s) && !refNameUsed(s)) {
                    for (TDiagramPanel tdpTmp : panels)
                        for (TGComponent tgc : tdpTmp.componentList) {
                            if ((tgc instanceof TMLCPRefSD || tgc instanceof TMLCPRefAD) && tgc.name.equals(tabbedPane.getTitleAt(index)))
                                tgc.name = s;
                        }
                    tabbedPane.setTitleAt(index, s);
                    panels.elementAt(index).setName(s);
                    mgui.changeMade(null, -1);
                } else {
                    JOptionPane.showMessageDialog(this.mgui.frame,
                            "Error: the name is already in use",
                            "Name modification",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public boolean refNameUsed(String s) {
        for (TDiagramPanel tdpTmp: this.panels)
            for (TGComponent tgc: tdpTmp.componentList) {
                if ((tgc instanceof TMLCPRefSD || tgc instanceof TMLCPRefAD) && tgc.name.equals(s))
                    return true;
            }
        return false;
    }

    public boolean canFirstDiagramBeMoved() {
        return false;
    }

    public boolean removeEnabled(int index) {
        return false;
    }

    public boolean renameEnabled(int index) {
        return false;
    }

    public boolean isUCDEnabled() {
        return false;
    }

    public boolean isSDEnabled() {
        return false;
    }

    public boolean isReqEnabled() {
        return false;
    }

    public boolean isProSMDEnabled() {
        return false;
    }

    public boolean isATDEnabled() {
        return false;
    }

    public boolean isFTDEnabled() {
        return false;
    }

    public boolean isAvatarRDEnabled() {
        return false;
    }

    public boolean isAvatarPDEnabled() {
        return false;
    }

    public boolean isAvatarCDEnabled() {
        return false;
    }

    public boolean isAvatarADEnabled() {
        return false;
    }

    public boolean isAvatarMADEnabled() {
        return false;
    }

    public boolean isDiplodocusMethodologyEnabled() {
        return false;
    }

    public boolean isSystemCAMSEnabled() {
    	return false;
    }
    
    public boolean isELNEnabled() {
    	return false;
    }
    
    public MainGUI getMainGUI() {
        return mgui;
    }

    public void resetAllDIPLOIDs() {
        for(int i=0; i<panels.size(); i++) {
            panelAt(i).resetAllDIPLOIDs();
        }
    }

    public void resetReachability() {
        for (int i = 0; i < panels.size(); i++) {
            //TraceManager.addDev("Reset reachability of " + panels.get(i));
            panelAt(i).resetReachability();
        }
    }

    public void repaintAll() {
        for (int i = 0; i < panels.size(); i++) {
            panelAt(i).repaint();
        }
    }

    public void resetLiveness() {
        for (int i = 0; i < panels.size(); i++) {
            panelAt(i).resetLiveness();
        }
    }

    public void searchForText(String text, Vector<Object> elements) {
        if (panelAt(0) != null) {
            String s = saveHeaderInXml(null).toLowerCase();
            if (s.indexOf(text) >= 0) {
                elements.add(this);
                /*CheckingError ce = new CheckingError(CheckingError.INFO, "Diagram");
                  ce.setTDiagramPanel(this.panelAt(0));
                  elements.add(ce);*/
            }
        }


        for(TDiagramPanel tdp: panels) {
            tdp.searchForText(text, elements);
        }
    }

    public boolean supportUppaalFormalVerification() {
        return false;
    }

    public boolean supportLotosFormalVerification() {
        return false;
    }

    public boolean supportCPPSimulation() {
        return false;
    }

    public boolean supportTMLGeneration() {
        return false;
    }

    // if draggable tabbed pane

    public void hasBeenDragged(int initialPosition, int destinationPosition) {
        TToolBar toolbar = toolbars.get(initialPosition);
        TDiagramPanel panel = panels.get(initialPosition);

        toolbars.removeElementAt(initialPosition);
        toolbars.insertElementAt(toolbar, destinationPosition);

        panels.removeElementAt(initialPosition);
        panels.insertElementAt(panel, destinationPosition);
    }

}

