/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

   ludovic.apvrille AT telecom-paristech.fr
   andrea.enrici AT telecom-paristech.fr

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
   * Class TMLArchiDiagramToolBar
   * Implements the toolbar to be used in conjunction with the panel of a TML Architecture diagram
   * Creation: 18/09/2007
   * @version 1.0 18/09/2007
   * @author Ludovic APVRILLE
   * @see TClassDiagramPanel
   */

package ui.tmldd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import ui.*;
import myutil.*;

public class TMLArchiDiagramToolBar extends TToolBar  implements ActionListener {

    protected JComboBox box;
    protected static String[] viewInfos = { "View all", "View architecture only", "View task mapping", "View channel mapping", "View Comm. Pattern", "View Comm. Pattern mapping", "Security mapping"};
    protected TMLArchiDiagramPanel panel;

    public TMLArchiDiagramToolBar(MainGUI _mgui) {
        super(_mgui);
    }

    protected void setActive(boolean b) {
        mgui.actions[TGUIAction.TMLARCHI_EDIT].setEnabled(b);
        mgui.actions[TGUIAction.UML_NOTE].setEnabled(b);
        mgui.actions[TGUIAction.CONNECTOR_COMMENT].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_LINK].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_CPUNODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_HWANODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_BUSNODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_CPNODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_BRIDGENODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_ARTIFACT].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_COMMUNICATION_ARTIFACT].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_KEY].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_PORT_ARTIFACT].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_EVENT_ARTIFACT].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_MEMORYNODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_DMANODE].setEnabled(b);
        mgui.actions[TGUIAction.ACT_TOGGLE_ATTR].setEnabled(b);
        mgui.actions[TGUIAction.ACT_TOGGLE_DIPLO_ID].setEnabled(b);

        mgui.actions[TGUIAction.ACT_SHOW_ZOOM].setEnabled(false);

        mgui.actions[TGUIAction.ACT_MODEL_CHECKING].setEnabled(b);
        mgui.actions[TGUIAction.ACT_ONECLICK_LOTOS_RG].setEnabled(b);
        mgui.actions[TGUIAction.ACT_SIMU_SYSTEMC].setEnabled(b);

        mgui.updateZoomInfo();
    }

    protected void setButtons() {
        JButton button;

        button = this.add(mgui.actions[TGUIAction.TMLARCHI_EDIT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.UML_NOTE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.CONNECTOR_COMMENT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLARCHI_LINK]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLARCHI_CPUNODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_HWANODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_ARTIFACT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLARCHI_DMANODE]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLARCHI_BUSNODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_BRIDGENODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_MEMORYNODE]);
        button.addMouseListener(mgui.mouseHandler);

        if (MainGUI.experimentalOn) {
            button = this.add(mgui.actions[TGUIAction.TMLARCHI_CPNODE]);
            button.addMouseListener(mgui.mouseHandler);
            button = this.add(mgui.actions[TGUIAction.TMLARCHI_EVENT_ARTIFACT]);
            button.addMouseListener(mgui.mouseHandler);
        }
		button = this.add(mgui.actions[TGUIAction.TMLARCHI_COMMUNICATION_ARTIFACT]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[TGUIAction.TMLARCHI_PORT_ARTIFACT]);
        button.addMouseListener(mgui.mouseHandler);
		button = this.add(mgui.actions[TGUIAction.TMLARCHI_KEY]);
        button.addMouseListener(mgui.mouseHandler);
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_ATTR]);
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_COMMUNICATION_ARTIFACT]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_PORT_ARTIFACT]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_KEY]);
        button.addMouseListener(mgui.mouseHandler);
        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_ATTR]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_DIPLO_ID]);
        button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_DIPLO_ID]);
        button.addMouseListener(mgui.mouseHandler);

	this.addSeparator();
	if (viewInfos == null) {
	    TraceManager.addDev("null viewInfos");   
	}
	box = new JComboBox(viewInfos);
	this.add(box);
	box.addActionListener(this);
    }

    public void setPanel(TMLArchiDiagramPanel _panel) {
	panel = _panel;
    }

    
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == box) {
	    if (panel != null) {
		panel.setCurrentView(box.getSelectedIndex());
	    }
	}
    }
}
    
} // Class
