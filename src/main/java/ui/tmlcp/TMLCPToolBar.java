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

package ui.tmlcp;

import ui.MainGUI;
import ui.TGUIAction;
import ui.TToolBar;

import javax.swing.*;

/**
 * Class TMLCPToolBar Implements the toolbar to be used in conjunction with the
 * panel of an interaction overview diagram Creation: 02/06/2014
 * 
 * @version 1.2 03/06/2015
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @see ui.cd.TClassDiagramPanel
 */
public class TMLCPToolBar extends TToolBar {

    public TMLCPToolBar(MainGUI _mgui) {
        super(_mgui);
    }

    @Override
    protected void setActive(boolean b) {
        mgui.actions[TGUIAction.TMLCP_EDIT].setEnabled(b);
        mgui.actions[TGUIAction.UML_NOTE].setEnabled(b);
        // mgui.actions[TGUIAction.CONNECTOR_COMMENT].setEnabled(b);
        mgui.actions[TGUIAction.TMLCP_CONNECTOR].setEnabled(b);
        mgui.actions[TGUIAction.TMLCP_REF_SD].setEnabled(b);
        mgui.actions[TGUIAction.TMLCP_REF_CP].setEnabled(b);
        mgui.actions[TGUIAction.TMLCP_FORK].setEnabled(b);
        mgui.actions[TGUIAction.TMLCP_JOIN].setEnabled(b);
        mgui.actions[TGUIAction.TMLCP_CHOICE].setEnabled(b);
        mgui.actions[TGUIAction.TMLCP_START].setEnabled(b);
        mgui.actions[TGUIAction.TMLCP_STOP].setEnabled(b);
        // mgui.actions[TGUIAction.TMLCP_JUNCTION].setEnabled(b);
        mgui.actions[TGUIAction.TMLCP_FOR_LOOP].setEnabled(b);
        mgui.actions[TGUIAction.ACT_ENHANCE].setEnabled(b);

        // Issue #31
        mgui.actions[TGUIAction.ACT_ZOOM_MORE].setEnabled(b);
        mgui.actions[TGUIAction.ACT_ZOOM_LESS].setEnabled(b);
        mgui.actions[TGUIAction.ACT_SHOW_ZOOM].setEnabled(b);
        // mgui.actions[TGUIAction.ACT_ZOOM_MORE].setEnabled(false);
        // mgui.actions[TGUIAction.ACT_ZOOM_LESS].setEnabled(false);
        // mgui.actions[TGUIAction.ACT_SHOW_ZOOM].setEnabled(false);
        mgui.actions[TGUIAction.ACT_MODEL_CHECKING].setEnabled(b);
        mgui.updateZoomInfo();

    }

    @Override
    protected void setButtons() {
        JButton button;

        button = this.add(mgui.actions[TGUIAction.TMLCP_EDIT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.UML_NOTE]);
        button.addMouseListener(mgui.mouseHandler);

        // button = this.add(mgui.actions[TGUIAction.CONNECTOR_COMMENT]);
        // button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLCP_CONNECTOR]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLCP_START]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.TMLCP_STOP]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLCP_REF_SD]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.TMLCP_REF_CP]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.TMLCP_FORK]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.TMLCP_JOIN]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.TMLCP_CHOICE]);
        button.addMouseListener(mgui.mouseHandler);

        /*
         * button = this.add(mgui.actions[TGUIAction.TMLCP_JUNCTION]);
         * button.addMouseListener(mgui.mouseHandler);
         */

        button = this.add(mgui.actions[TGUIAction.TMLCP_FOR_LOOP]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.ACT_ENHANCE]);
        button.addMouseListener(mgui.mouseHandler);
    }
} // Class
