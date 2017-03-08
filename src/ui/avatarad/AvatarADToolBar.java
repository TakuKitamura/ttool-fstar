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
   * Class AvatarADToolBar
   * Implements the toolbar to be used in conjunction with the panel of
   * AVATAR Activity Diagram
   * Creation: 01/09/2011
   * @version 1.0 01/09/2011
   * @author Ludovic APVRILLE
   * @see TClassDiagramPanel
   */

package ui.avatarad;

import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;

import ui.*;

public class AvatarADToolBar extends TToolBar {

    public AvatarADToolBar(MainGUI _mgui) {
        super(_mgui);

    }

    protected void setActive(boolean b) {
        mgui.actions[TGUIAction.ACT_ZOOM_MORE].setEnabled(b);
	mgui.actions[TGUIAction.ACT_ZOOM_LESS].setEnabled(b);
	
	mgui.actions[TGUIAction.ACT_SHOW_ZOOM].setEnabled(b);
	mgui.updateZoomInfo();

        mgui.actions[TGUIAction.AAD_EDIT].setEnabled(b);
        mgui.actions[TGUIAction.UML_NOTE].setEnabled(b);
        mgui.actions[TGUIAction.CONNECTOR_COMMENT].setEnabled(b);
        mgui.actions[TGUIAction.AAD_START_STATE].setEnabled(b);
        mgui.actions[TGUIAction.AAD_STOP_STATE].setEnabled(b);
        mgui.actions[TGUIAction.AAD_CHOICE].setEnabled(b);
        mgui.actions[TGUIAction.AAD_JUNCTION].setEnabled(b);
        mgui.actions[TGUIAction.AAD_PARALLEL].setEnabled(b);
        mgui.actions[TGUIAction.AAD_ACTIVITY].setEnabled(b);
        mgui.actions[TGUIAction.AAD_ACTION].setEnabled(b);
        mgui.actions[TGUIAction.AAD_STOP_FLOW].setEnabled(b);
        mgui.actions[TGUIAction.AAD_SEND_SIGNAL_ACTION].setEnabled(b);
        mgui.actions[TGUIAction.AAD_ACCEPT_EVENT_ACTION].setEnabled(b);
        mgui.actions[TGUIAction.AAD_PARTITION].setEnabled(b);
        mgui.actions[TGUIAction.AAD_ASSOCIATION_CONNECTOR].setEnabled(b);
        mgui.actions[TGUIAction.AAD_ALIGN_PARTITION].setEnabled(b);
    }

    protected void setButtons() {
        JButton button;

        button = this.add(mgui.actions[TGUIAction.AAD_EDIT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.UML_NOTE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.CONNECTOR_COMMENT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.AAD_ACTIVITY]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.AAD_PARTITION]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.AAD_START_STATE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.AAD_STOP_STATE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.AAD_STOP_FLOW]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.AAD_SEND_SIGNAL_ACTION]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.AAD_ACCEPT_EVENT_ACTION]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();



        button = this.add(mgui.actions[TGUIAction.AAD_ACTION]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.AAD_CHOICE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.AAD_PARALLEL]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.AAD_JUNCTION]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.AAD_ASSOCIATION_CONNECTOR]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.AAD_ALIGN_PARTITION]);
        button.addMouseListener(mgui.mouseHandler);


        /*this.addSeparator();
          this.addSeparator();

          button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_ATTR]);
          button.addMouseListener(mgui.mouseHandler);*/


    }

} // Class
