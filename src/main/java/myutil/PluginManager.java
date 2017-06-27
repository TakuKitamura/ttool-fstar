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




package myutil;

import java.lang.reflect.Method;
import java.util.ArrayList;

import java.awt.*;

/**
   * Class PluginManager
   * Creation: 24/05/2017
   * Version 1.0 24/05/2017
   * @author Ludovic APVRILLE
 */
public class PluginManager  {
    public ArrayList<Plugin> plugins;
    public static PluginManager pluginManager;

    public PluginManager() {
	plugins = new ArrayList<Plugin>();
    }

    public void addPlugin(Plugin _plugin) {
	plugins.add(_plugin);
    }

    public Plugin getPluginOrCreate(String _name) {
	Plugin plug = getPlugin(_name);
	if (plug != null) {
	    return plug;
	}

	return createPlugin(_name);
    }

    public Plugin getPlugin(String _name) {
	for(Plugin plugin: plugins) {
	    if (plugin.getName().compareTo(_name) == 0) {
		return plugin;
	    }
	}
	return null;
    }

    public Plugin createPlugin(String _name) {
	Plugin plugin = new Plugin(_name);
	addPlugin(plugin);
	return plugin;
    }

    public void executeGraphics(Plugin _plugin, String _className, String _methodName, Graphics g) {
	if (_plugin == null) {
	    return;
	}

	Method m = _plugin.getMethod(_className, _methodName);
	if (m == null) {
	    return;
	}

	try {
	    m.invoke(g);
	} catch (Exception e) {
	    TraceManager.addDev("Exception occured when executing method " + _methodName);
	}
    }
    
    public String executeString(String _pluginName, String _className, String _methodName) {
	Plugin plugin = getPlugin(_pluginName);
	if (plugin == null) {
	    plugin = createPlugin(_pluginName);
	    if (plugin == null) {
		return null;
	    }
	}

	return plugin.executeRetStringMethod(_className, _methodName);

	
    }


}
