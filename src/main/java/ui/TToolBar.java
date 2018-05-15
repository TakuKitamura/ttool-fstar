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

import javax.swing.*;
import java.util.*;
import java.awt.event.*;

import myutil.*;
import common.*;


/**
 * Class TToolBar
 * Abstract toolbar to be used by TURTLE diagrams
 * Creation: 21/12/2003
 * @version 1.0 21/12/2003
 * @author Ludovic APVRILLE
 * @see TGComponent
 */
public abstract class TToolBar extends JToolBar implements ActionListener {

    protected ArrayList<TGUIAction> pluginActions;
    protected ArrayList<Plugin> plugins;

    //protected ActionListener buttonTB;
    protected MainGUI mgui;
    /*protected int typeSelected = TGComponentManager.EDIT;
      protected int idSelected = TGComponentManager.EDIT;*/

    public TToolBar(MainGUI _mgui) {
        super();
        mgui = _mgui;
        setOrientation(SwingConstants.HORIZONTAL);
        setFloatable(true) ;
        setButtons();
    }

    // asbtract operations
    protected abstract void setButtons();

    protected abstract void setActive(boolean b);

    protected void setPluginButtons(String diag) {
        pluginActions = new ArrayList<TGUIAction>();
        plugins = new ArrayList<Plugin>();
        this.addSeparator();
	LinkedList<Plugin> list = PluginManager.pluginManager.getPluginGraphicalComponent(diag);
	TraceManager.addDev("List of " + list.size() + " graphical components");
	for(Plugin p: list) {
	    //Plugin p = PluginManager.pluginManager.getPluginOrCreate(ConfigurationTTool.PLUGIN_GRAPHICAL_COMPONENT[i]);
	    String shortText = p.executeRetStringMethod(p.getClassGraphicalComponent(), "getShortText");
	    String longText = p.executeRetStringMethod(p.getClassGraphicalComponent(), "getLongText");
	    String veryShortText = p.executeRetStringMethod(p.getClassGraphicalComponent(), "getVeryShortText");
	    ImageIcon img = p.executeRetImageIconMethod(p.getClassGraphicalComponent(), "getImageIcon");
	    if ((img != null)  && (shortText != null)) {
		TraceManager.addDev("Plugin: " + p.getName() + " short name:" + shortText);
		TAction t = new TAction("command-" + p.getName(), shortText, img, img, veryShortText, longText, 0);
		TGUIAction tguia = new TGUIAction(t);
		pluginActions.add(tguia);
		plugins.add(p);
		JButton button = add(tguia);
		button.addMouseListener(mgui.mouseHandler);
		tguia.addActionListener(this);
		//button.addActionListener(this);
		
		/*JButton toto = new JButton("Test");
		  toto.addActionListener(this);
		  add(toto);*/
		//TraceManager.addDev("Action listener...");
	    }
        }
    }

    public void actionPerformed(ActionEvent evt) {
        //TraceManager.addDev("Action listener of TToolBar");
        //TraceManager.addDev("Action on event " + e + "\n\nsource=" + e.getSource());
        //Object o = e.getSource();
        int index = 0;
        Plugin p = null;
        TGUIAction act = null;
        String command = evt.getActionCommand();
        //TraceManager.addDev("command=" + command);
        for(TGUIAction t: pluginActions) {
            //TraceManager.addDev(" command of action:" + t.getActionCommand());
            if (t.getActionCommand().compareTo(command) == 0) {
                p = plugins.get(index);
                act = t;
                break;
            }
            index ++;
        }

        if ((act != null) && (p != null)) {
            TraceManager.addDev("Action on plugin " + p);
            mgui.actionOnButton(TGComponentManager.COMPONENT, p);
        }

    }

} // Class

