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

import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.tmlad.TMLActivityDiagramPanel;

/**
 * Class ModelParameters Used as an abstraction of TMLTask and TMLTaskObject
 * Creation: 19/06/2019
 * 
 * @version 1.0 19/06/2019
 * @author Ludovic APVRILLE
 */
public class ModelParameters {
  private static String[] ids = { "ANIMATE_INTERACTIVE_SIMULATION", // Diplodocus animate simulation
      "ACTIVATE_PENALTIES", // Diplodocus penalties in C++ simulation
      "UPDATE_INFORMATION_DIPLO_SIM", // Diplo simulator
      "ANIMATE_WITH_INFO_DIPLO_SIM", // Diplo simulator
      "OPEN_DIAG_DIPLO_SIM", // Diplo simulator
      "LAST_SELECTED_MAIN_TAB", "LAST_SELECTED_SUB_TAB" };
  private static String[] values = { "true", "true", "true", "true", "false", "0", "0" };

  public static boolean getBooleanValueFromID(String value) {
    for (int i = 0; i < ids.length; i++) {
      if (ids[i].compareTo(value) == 0) {
        return values[i].compareTo("true") == 0;
      }
    }
    return false;
  }

  public static int getIntegerValueFromID(String value) {
    for (int i = 0; i < ids.length; i++) {
      if (ids[i].compareTo(value) == 0) {
        return Integer.decode(values[i]);
      }
    }
    return 0;
  }

  public static String toXML() {
    String ret = "";
    for (int i = 0; i < ids.length; i++) {
      ret += " " + ids[i] + "=\"" + values[i] + "\"";
    }
    return ret;
  }

  public static void loadValuesFromXML(org.w3c.dom.Node node) {
    String tmp;
    Element elt;

    TraceManager.addDev("Loading parameters from XML");

    if (node.getNodeType() == Node.ELEMENT_NODE) {
      elt = (Element) node;
      for (int i = 0; i < ids.length; i++) {
        try {
          tmp = elt.getAttribute(ids[i]);
          if ((tmp != null) && (tmp.length() > 0)) {
            // TraceManager.addDev("Setting value " + tmp + " to id " + ids[i]);
            values[i] = tmp;
          }
        } catch (Exception e) {

        }
      }
    }

  }

  public static void setValueForID(String id, String value) {
    // TraceManager.addDev("Trying to set value " + value + " to " + id);

    for (int i = 0; i < ids.length; i++) {
      if (ids[i].compareTo(id) == 0) {
        // TraceManager.addDev("Setting value " + value + " to " + id);
        values[i] = value;
        return;
      }
    }
    return;
  }
}