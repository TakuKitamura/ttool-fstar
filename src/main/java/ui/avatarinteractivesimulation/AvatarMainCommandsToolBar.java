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




package ui.avatarinteractivesimulation;

import ui.MainGUI;

import javax.swing.*;

//import java.awt.*;
//import java.awt.event.*;

/**
   * Class AvatarMainCommandsToolBar
   * Toolbar associated with interactive simulation (main commands)
   * Creation: 21/01/2011
   * @version 1.0 21/01/2011
   * @author Ludovic APVRILLE
 */
public class AvatarMainCommandsToolBar extends AvatarInteractiveSimulationBar {


    public AvatarMainCommandsToolBar(JFrameAvatarInteractiveSimulation _jfais) {
        super(_jfais);
    }

    protected void setActive(boolean b) {
        jfais.actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(b);
        jfais.actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU_MAX_TRANS].setEnabled(b);
        //jfais.actions[InteractiveSimulationActions.ACT_RUN_X_TIME_UNITS].setEnabled(b);
        //jfais.actions[InteractiveSimulationActions.ACT_RUN_TO_TIME].setEnabled(b);
        //jfais.actions[InteractiveSimulationActions.ACT_RUN_X_TRANSACTIONS].setEnabled(b);
        //jfais.actions[InteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(b);
        //jfais.actions[InteractiveSimulationActions.ACT_RUN_EXPLORATION].setEnabled(b);
        //jfais.actions[InteractiveSimulationActions.ACT_RUN_TO_NEXT_BUS_TRANSFER].setEnabled(b);
        //jfais.actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CPU_EXECUTES].setEnabled(b);
        //jfais.actions[InteractiveSimulationActions.ACT_RUN_UNTIL_TASK_EXECUTES].setEnabled(b);
        //jfais.actions[InteractiveSimulationActions.ACT_RUN_UNTIL_MEMORY_ACCESS].setEnabled(b);
        //jfais.actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CHANNEL_ACCESS].setEnabled(b);
        jfais.actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(b);
        jfais.actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(b);
        jfais.actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(b);
        jfais.actions[AvatarInteractiveSimulationActions.ACT_RUN_TRACE].
                setEnabled(b&&JFrameAvatarInteractiveSimulation.SELECTED_SIMULATION_TRACE != null);
        jfais.actions[AvatarInteractiveSimulationActions.ACT_REMOVE_ALL_TRANS].setEnabled(b);



    }

    protected void setButtons() {
        JButton button;

        button = this.add(jfais.actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU]);
        button.addMouseListener(jfais.mouseHandler);

        button = this.add(jfais.actions[AvatarInteractiveSimulationActions.ACT_REMOVE_ALL_TRANS]);
        button.addMouseListener(jfais.mouseHandler);

        this.addSeparator();

        button = this.add(jfais.actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE]);
        button.addMouseListener(jfais.mouseHandler);

        this.addSeparator();

        button = this.add(jfais.actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU]);
        button.addMouseListener(jfais.mouseHandler);

        button = this.add(jfais.actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU_MAX_TRANS]);
        button.addMouseListener(jfais.mouseHandler);

        button = this.add(jfais.actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU]);
        button.addMouseListener(jfais.mouseHandler);



        this.addSeparator();

        button = this.add(jfais.actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS]);
        button.addMouseListener(jfais.mouseHandler);

        if (MainGUI.experimentalOn) {
            this.addSeparator();

            button = this.add(jfais.actions[AvatarInteractiveSimulationActions.ACT_RUN_TRACE]);
            button.addMouseListener(jfais.mouseHandler);
        }

        this.addSeparator();
        this.addSeparator();

        button = this.add(jfais.actions[AvatarInteractiveSimulationActions.ACT_ZOOM_OUT]);
        button.addMouseListener(jfais.mouseHandler);

        button = this.add(jfais.actions[AvatarInteractiveSimulationActions.ACT_ZOOM_IN]);
        button.addMouseListener(jfais.mouseHandler);


        /*this.addSeparator();

          button = this.add(jfis.actions[InteractiveSimulationActions.ACT_RUN_TO_TIME]);
          button.addMouseListener(jfis.mouseHandler);

          button = this.add(jfis.actions[InteractiveSimulationActions.ACT_RUN_X_TIME_UNITS]);
          button.addMouseListener(jfis.mouseHandler);

          this.addSeparator();

          button = this.add(jfis.actions[InteractiveSimulationActions.ACT_RUN_X_TRANSACTIONS]);
          button.addMouseListener(jfis.mouseHandler);

          button = this.add(jfis.actions[InteractiveSimulationActions.ACT_RUN_X_COMMANDS]);
          button.addMouseListener(jfis.mouseHandler);

          this.addSeparator();

          button = this.add(jfis.actions[InteractiveSimulationActions.ACT_RUN_EXPLORATION]);
          button.addMouseListener(jfis.mouseHandler);

          this.addSeparator();

          button = this.add(jfis.actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CPU_EXECUTES]);
          button.addMouseListener(jfis.mouseHandler);

          button = this.add(jfis.actions[InteractiveSimulationActions.ACT_RUN_TO_NEXT_BUS_TRANSFER]);
          button.addMouseListener(jfis.mouseHandler);

          button = this.add(jfis.actions[InteractiveSimulationActions.ACT_RUN_UNTIL_MEMORY_ACCESS]);
          button.addMouseListener(jfis.mouseHandler);

          this.addSeparator();

          button = this.add(jfis.actions[InteractiveSimulationActions.ACT_RUN_UNTIL_TASK_EXECUTES]);
          button.addMouseListener(jfis.mouseHandler);

          button = this.add(jfis.actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CHANNEL_ACCESS]);
          button.addMouseListener(jfis.mouseHandler);*/


    }
} // Class
