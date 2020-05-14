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

package ui.simulationtraceanalysis;

import ui.util.IconManager;
import ui.TAction;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class LatencyDetailedAnalysisActions: actions related to the latency detail
 * analysis and directed graph generation
 * 
 * 23/09/2019
 *
 * @author Maysam Zoor
 */
public class LatencyDetailedAnalysisActions extends AbstractAction {
    // Actions

    public static final int ACT_SHOW_GRAPH = 0;
    public static final int ACT_SAVE_TRACE_PNG = 1;
    public static final int ACT_STOP_AND_CLOSE_ALL = 2;
    public static final int ACT_SAVE_TRACE_GRAPHML = 3;
    public static final int ACT_LOAD_SIMULATION_TRACES = 4;
    public static final int ACT_DETAILED_ANALYSIS = 5;
    public static final int ACT_Import_ANALYSIS = 6;
    
    public static final int ACT_SHOW_GRAPH_FILE_1 = 7;
    public static final int ACT_SHOW_GRAPH_FILE_2 = 8;
    public static final int ACT_COMPARE_IN_DETAILS = 9;

    public static final int NB_ACTION = 10;

    private static final TAction[] actions = new TAction[NB_ACTION];

    private EventListenerList listeners;

    public static final String JLF_IMAGE_DIR = "";

    public static final String LARGE_ICON = "LargeIcon";

    public LatencyDetailedAnalysisActions(int id) {
        if (actions[0] == null) {
            init();
        }
        if (actions[id] == null) {
            return;
        }

        putValue(Action.NAME, actions[id].NAME);
        putValue(Action.SMALL_ICON, actions[id].SMALL_ICON);
        putValue(LARGE_ICON, actions[id].LARGE_ICON);
        putValue(Action.SHORT_DESCRIPTION, actions[id].SHORT_DESCRIPTION);
        putValue(Action.LONG_DESCRIPTION, actions[id].LONG_DESCRIPTION);
        // putValue(Action.MNEMONIC_KEY, new Integer(actions[id].MNEMONIC_KEY));
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
        actions[ACT_SAVE_TRACE_PNG] = new TAction("save-trace_as_PNG", "Save Directed Graph", IconManager.imgic341, IconManager.imgic341,
                "Save Directed Graph", "Save Directed Graph", 'S');
        actions[ACT_SAVE_TRACE_GRAPHML] = new TAction("save-trace_as_graphml", "Save Directed Graph as graphml", IconManager.imgic341,
                IconManager.imgic341, "Save Directed Graph as graphml", "Save Directed Graph as graphml", 'S');

        actions[ACT_SHOW_GRAPH] = new TAction("show_graph", "Show Directed Graph", IconManager.imgic53, IconManager.imgic53, "Show Directed Graph",
                "Show Directed Graph", 'C');
        actions[ACT_STOP_AND_CLOSE_ALL] = new TAction("stop-and-close-all", "Terminate Latency Analysis", IconManager.imgic27, IconManager.imgic27,
                "Terminate Latency Analysis", "Terminate Latency Analysis window", 'T');
        actions[ACT_LOAD_SIMULATION_TRACES] = new TAction("load_simulation_traces", "Load Simulation Traces", IconManager.imgic29,
                IconManager.imgic29, "Load Simulation Traces", "Load Simulation Traces", 'T');
        actions[ACT_DETAILED_ANALYSIS] = new TAction("start_detailed_analysis", "Detailed Analysis", IconManager.imgic29, IconManager.imgic29,
                "show detailed analysis tables", "show detailed analysis tables", 'T');
        actions[ACT_Import_ANALYSIS] = new TAction("import-trace", "Save Directed Graph", IconManager.imgic341, IconManager.imgic341,
                "import Directed Graph", "Import Directed Graph", 'S');
        
        actions[ACT_SHOW_GRAPH_FILE_1] = new TAction("show_graph_file_1", "Show Directed Graph 1", IconManager.imgic341, IconManager.imgic341,
                "Show Directed Graph", "Show Directed Graph", 'S');
        
        actions[ACT_SHOW_GRAPH_FILE_2] = new TAction("show_graph_file_2", "Show Directed Graph 2", IconManager.imgic341, IconManager.imgic341,
                "Show Directed Graph", "Show Directed Graph", 'S');
        
        actions[ACT_COMPARE_IN_DETAILS] = new TAction("compare_in_details", "Compare In Detials", IconManager.imgic53, IconManager.imgic53,
                "Compare In Detials", "Compare In Detials", 'S');


    }

    public String getActionCommand() {
        return (String) getValue(Action.ACTION_COMMAND_KEY);
    }

    public String getShortDescription() {
        return (String) getValue(Action.SHORT_DESCRIPTION);
    }

    public String getLongDescription() {
        return (String) getValue(Action.LONG_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent evt) {
        //
        if (listeners != null) {
            Object[] listenerList = listeners.getListenerList();

            // Recreate the ActionEvent and stuff the value of the ACTION_COMMAND_KEY
            ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(), (String) getValue(Action.ACTION_COMMAND_KEY));
            for (int i = 0; i <= listenerList.length - 2; i += 2) {
                ((ActionListener) listenerList[i + 1]).actionPerformed(e);
            }
        }
    }

    public void addActionListener(ActionListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        if (listeners == null) {
            return;
        }
        listeners.remove(ActionListener.class, l);
    }
}
