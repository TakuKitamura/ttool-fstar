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

package ui.eln;

import ui.MainGUI;
import ui.TGUIAction;
import ui.TToolBar;

import javax.swing.*;

/**
 * Class ELNDiagramToolBar 
 * Implements the toolbar to be used in conjunction with the panel of a ELN diagram 
 * Creation: 11/06/2018
 * @version 1.0 11/06/2018
 * @author Irina Kit Yan LEE
 * @see ELNDiagramPanel
 */

@SuppressWarnings("serial")

public class ELNDiagramToolBar extends TToolBar {

	public ELNDiagramToolBar(MainGUI _mgui) {
		super(_mgui);
	}

	protected void setActive(boolean b) {
		mgui.actions[TGUIAction.ACT_ZOOM_MORE].setEnabled(b);
		mgui.actions[TGUIAction.ACT_ZOOM_LESS].setEnabled(b);

		mgui.actions[TGUIAction.ELN_EDIT].setEnabled(b);
		mgui.actions[TGUIAction.UML_NOTE].setEnabled(b);
		mgui.actions[TGUIAction.ELN_RESISTOR].setEnabled(b);
		mgui.actions[TGUIAction.ELN_CAPACITOR].setEnabled(b);
		mgui.actions[TGUIAction.ELN_INDUCTOR].setEnabled(b);
		mgui.actions[TGUIAction.ELN_VOLTAGE_CONTROLLED_VOLTAGE_SOURCE].setEnabled(b);
		mgui.actions[TGUIAction.ELN_VOLTAGE_CONTROLLED_CURRENT_SOURCE].setEnabled(b);
		mgui.actions[TGUIAction.ELN_IDEAL_TRANSFORMER].setEnabled(b);
		mgui.actions[TGUIAction.ELN_TRANSMISSION_LINE].setEnabled(b);
		mgui.actions[TGUIAction.ELN_INDEPENDENT_VOLTAGE_SOURCE].setEnabled(b);
		mgui.actions[TGUIAction.ELN_INDEPENDENT_CURRENT_SOURCE].setEnabled(b);
		mgui.actions[TGUIAction.ELN_NODE_REF].setEnabled(b);
		mgui.actions[TGUIAction.ELN_TDF_VOLTAGE_SINK].setEnabled(b);
		mgui.actions[TGUIAction.ELN_TDF_CURRENT_SINK].setEnabled(b);
		mgui.actions[TGUIAction.ELN_MODULE].setEnabled(b);
		mgui.actions[TGUIAction.ELN_MODULE_TERMINAL].setEnabled(b);
		mgui.actions[TGUIAction.ELN_TOGGLE_ATTR].setEnabled(b);
		mgui.actions[TGUIAction.ELN_TDF_VOLTAGE_SOURCE].setEnabled(b);
		mgui.actions[TGUIAction.ELN_TDF_CURRENT_SOURCE].setEnabled(b);
		mgui.actions[TGUIAction.ELN_CONNECTOR].setEnabled(b);
		mgui.actions[TGUIAction.ELN_CLUSTER].setEnabled(b);
		mgui.actions[TGUIAction.CAMS_PORT_DE].setEnabled(b);
		mgui.actions[TGUIAction.CAMS_PORT_TDF].setEnabled(b);
		mgui.actions[TGUIAction.ELN_GENCODE].setEnabled(b);
		
		mgui.actions[TGUIAction.ACT_MODEL_CHECKING].setEnabled(b);
		mgui.actions[TGUIAction.ACT_ONECLICK_LOTOS_RG].setEnabled(b);
		mgui.actions[TGUIAction.ACT_SIMU_SYSTEMC].setEnabled(b);
		
		mgui.actions[TGUIAction.ACT_SHOW_ZOOM].setEnabled(b);
		mgui.updateZoomInfo();
	}

	protected void setButtons() {
		JButton button;

		button = this.add(mgui.actions[TGUIAction.ELN_EDIT]);
		button.addMouseListener(mgui.mouseHandler);

		button = this.add(mgui.actions[TGUIAction.UML_NOTE]);
		button.addMouseListener(mgui.mouseHandler);

		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ELN_CONNECTOR]);
		button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ELN_CLUSTER]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.CAMS_PORT_DE]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.CAMS_PORT_TDF]);
		button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ELN_MODULE]);
		button.addMouseListener(mgui.mouseHandler);
	
		button = this.add(mgui.actions[TGUIAction.ELN_MODULE_TERMINAL]);
		button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ELN_RESISTOR]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_CAPACITOR]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_INDUCTOR]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_VOLTAGE_CONTROLLED_VOLTAGE_SOURCE]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_VOLTAGE_CONTROLLED_CURRENT_SOURCE]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_IDEAL_TRANSFORMER]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_TRANSMISSION_LINE]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_INDEPENDENT_VOLTAGE_SOURCE]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_INDEPENDENT_CURRENT_SOURCE]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_NODE_REF]);
		button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ELN_TDF_VOLTAGE_SOURCE]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_TDF_CURRENT_SOURCE]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_TDF_VOLTAGE_SINK]);
		button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ELN_TDF_CURRENT_SINK]);
		button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ELN_GENCODE]);
		button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ELN_TOGGLE_ATTR]);
		button.addMouseListener(mgui.mouseHandler);
	}
}