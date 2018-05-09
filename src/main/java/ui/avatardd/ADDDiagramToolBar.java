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

import ui.MainGUI;
import ui.TGUIAction;
import ui.TToolBar;
import myutil.TraceManager;

import javax.swing.*;

//import java.awt.*;
//import java.awt.event.*;

/**
 * Class ADDDiagramToolBar
 * Implements the toolbar to be used in conjunction with the panel of an avatar deployment diagram
 * Creation: 30/06/2014
 *
 * @author Ludovic APVRILLE
 * @author Ludovic APVRILLE (update by Julien HENON, Daniela GENIUS)
 * @version 2.0 08/07/2015
 * @see ADDDiagramPanel
 */
public class ADDDiagramToolBar extends TToolBar {

    public ADDDiagramToolBar(MainGUI _mgui) {
        super(_mgui);

    }

    protected void setActive(boolean b) {

        TraceManager.addDev("Active ADDtoolbar b=" +b);

        mgui.actions[TGUIAction.ADD_EDIT].setEnabled(b);
        mgui.actions[TGUIAction.UML_NOTE].setEnabled(b);
        mgui.actions[TGUIAction.CONNECTOR_COMMENT].setEnabled(b);
        mgui.actions[TGUIAction.ADD_LINK].setEnabled(b);
        mgui.actions[TGUIAction.ADD_CPUNODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_BUSNODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_BRIDGENODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_VGMNNODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_CROSSBARNODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_BLOCKARTIFACT].setEnabled(b);
        mgui.actions[TGUIAction.ADD_CHANNELARTIFACT].setEnabled(b);
        mgui.actions[TGUIAction.ADD_TTYNODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_RAMNODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_ROMNODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_DMANODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_ICUNODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_COPROMWMRNODE].setEnabled(b);
        mgui.actions[TGUIAction.ADD_TIMERNODE].setEnabled(b);
        mgui.actions[TGUIAction.ACT_TOGGLE_ATTR].setEnabled(b);

        mgui.actions[TGUIAction.ACT_SHOW_ZOOM].setEnabled(false);

        // julien -------------------------------------------------

        mgui.actions[TGUIAction.DEPLOY_AVATAR_DIAGRAM].setEnabled(b);
        mgui.actions[TGUIAction.EXTRAC_DEPLOY_PARAM_TO_FILE].setEnabled(b);


        // --------------------------------------------------------

        mgui.actions[TGUIAction.ACT_MODEL_CHECKING].setEnabled(b);
        mgui.actions[TGUIAction.ACT_ONECLICK_LOTOS_RG].setEnabled(b);
        mgui.actions[TGUIAction.ACT_SIMU_SYSTEMC].setEnabled(b);

        mgui.updateZoomInfo();
    }

    protected void setButtons() {
        JButton button;

        button = this.add(mgui.actions[TGUIAction.ADD_EDIT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.UML_NOTE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.CONNECTOR_COMMENT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.ADD_LINK]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.ADD_CPUNODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.ADD_BLOCKARTIFACT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.ADD_DMANODE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.ADD_ICUNODE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.ADD_COPROMWMRNODE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.ADD_TIMERNODE]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.ADD_TTYNODE]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.ADD_BUSNODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.ADD_BRIDGENODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.ADD_VGMNNODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.ADD_CROSSBARNODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.ADD_RAMNODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.ADD_CHANNELARTIFACT]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.ADD_ROMNODE]);
        button.addMouseListener(mgui.mouseHandler);


        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_ATTR]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.EXTRAC_DEPLOY_PARAM_TO_FILE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.DEPLOY_AVATAR_DIAGRAM]);
        button.addMouseListener(mgui.mouseHandler);

        // -----------------------------------------------------

    }

} // Class
