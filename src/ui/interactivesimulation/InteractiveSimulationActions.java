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
 * Class InteractiveSimulationActions
 *
 * Creation: 26/05/2009
 * @version 1.0 26/05/2009
 * @author Ludovic APVRILLE
 * @see TGComponent
 */

package ui.interactivesimulation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import ui.*;

public class InteractiveSimulationActions extends AbstractAction {
    // Actions
    public static final int ACT_RUN_SIMU = 0;
	public static final int ACT_STOP_SIMU = 1;
	public static final int ACT_RESET_SIMU = 2;
	
	public static final int ACT_START_ALL = 3; 
	public static final int ACT_STOP_ALL = 4;
	public static final int ACT_STOP_AND_CLOSE_ALL = 5;
   
    public static final int NB_ACTION = 6;


    private  static final TAction [] actions = new TAction[NB_ACTION];
    
    private EventListenerList listeners;
    
    public static final String JLF_IMAGE_DIR = "";
    
    public static final String LARGE_ICON = "LargeIcon";
    

    
    public InteractiveSimulationActions(int id) {
        if (actions[0] == null) {
            init();
        }
        if (actions[id] == null) {
            return ;
        }
        
        putValue(Action.NAME, actions[id].NAME);
        putValue(Action.SMALL_ICON, actions[id].SMALL_ICON);
        putValue(LARGE_ICON, actions[id].LARGE_ICON);
        putValue(Action.SHORT_DESCRIPTION, actions[id].SHORT_DESCRIPTION);
        putValue(Action.LONG_DESCRIPTION, actions[id].LONG_DESCRIPTION);
        //putValue(Action.MNEMONIC_KEY, new Integer(actions[id].MNEMONIC_KEY));
        if (actions[id].MNEMONIC_KEY != 0) {
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(actions[id].MNEMONIC_KEY, java.awt.event.InputEvent.CTRL_MASK));
        }
        putValue(Action.ACTION_COMMAND_KEY, actions[id].ACTION_COMMAND_KEY);
        
    }
    
    public void setName(int index, String name) {
        actions[index].NAME = name;
        putValue(Action.NAME, actions[index].NAME);
    }
    
    public void init() {
        actions[ACT_RUN_SIMU] = new TAction("run-simu", "Run simulation", IconManager.imgic53, IconManager.imgic53, "Run simulation", "Run simulation. works only if the simulator is \"ready\"", 'R');
        actions[ACT_STOP_SIMU] = new TAction("stop-simu", "Stop simulation", IconManager.imgic55, IconManager.imgic55, "Stop simulation", "Stop simulation. Works only if the simulator is \"busy\"", 'S');
		actions[ACT_RESET_SIMU] = new TAction("reset-simu", "Reset simulation", IconManager.imgic45, IconManager.imgic45, "Reset simulation", "Reset simulation", 'T');
		
		actions[ACT_START_ALL] = new TAction("start-all", "Connect to simulator", IconManager.imgic53, IconManager.imgic53, "Connect", "Start the server - if it is not yet running - and connect to it", 'C');
        actions[ACT_STOP_ALL] = new TAction("stop-all", "Quit simulation window", IconManager.imgic27, IconManager.imgic27, "Quit simulation window", "Quit the simulation window without terminating the simulation", 'Q');
        actions[ACT_STOP_AND_CLOSE_ALL] = new TAction("stop-and-close-all", "Terminate simulation and quit", IconManager.imgic27, IconManager.imgic27, "Terminate simulation and quit", "Terminate the simulation and quit the simulation window", 'T');
        
    }
    
    
    public String getActionCommand()  {
        return (String)getValue(Action.ACTION_COMMAND_KEY);
    }

    public String getShortDescription()  {
        return (String)getValue(Action.SHORT_DESCRIPTION);
    }
    
    public String getLongDescription()  {
        return (String)getValue(Action.LONG_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent evt)  {
        //System.out.println("Action performed");
        if (listeners != null) {
            Object[] listenerList = listeners.getListenerList();
            
            // Recreate the ActionEvent and stuff the value of the ACTION_COMMAND_KEY
            ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(),
            (String)getValue(Action.ACTION_COMMAND_KEY));
            for (int i = 0; i <= listenerList.length-2; i += 2) {
                ((ActionListener)listenerList[i+1]).actionPerformed(e);
            }
        }
    }
    
    public void addActionListener(ActionListener l)  {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(ActionListener.class, l);
    }
    
    public void removeActionListener(ActionListener l)  {
        if (listeners == null) {
            return;
        }
        listeners.remove(ActionListener.class, l);
    }    
}
