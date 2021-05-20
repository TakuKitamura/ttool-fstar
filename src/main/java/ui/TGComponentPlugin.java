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

package ui;

import myutil.*;
import common.*;

import java.awt.*;
import javax.swing.*;
import java.lang.reflect.Method;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class TGComponentPlugin Graphical Component with a plugin facility Creation:
 * 27/06/2017
 * 
 * @version 1.0 27/06/2017
 * @author Ludovic APVRILLE
 */
public class TGComponentPlugin extends TGComponent implements ComponentPluginInterface {

    private Plugin componentPlugin;
    private String className;
    private Class<?> classRef;
    private Object instance;
    private Method methodInternalDrawing;
    private Method methodGetWidth;
    private Method methodGetHeight;
    private Method methodGetCustomValue;
    private Method methodIsOnMe;
    private Method methodEditOnDoubleClick;

    public TGComponentPlugin(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
            TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        width = 0;
        height = 0;

        value = "custom component";
    }

    public void setPlugin(Plugin _plugin) {
        componentPlugin = _plugin;
    }

    public String getCustomValue() {
        if (componentPlugin == null) {
            return value;
        }

        try {
            if (methodGetCustomValue == null) {
                createInstance();
                Class[] cArg = new Class[2];
                cArg[0] = String.class;
                cArg[1] = String.class;
                methodGetCustomValue = classRef.getMethod("getCustomValue", cArg);
                // TraceManager.addDev("Method =" + methodGetWidth);
            }

            String ret = (String) (methodGetCustomValue.invoke(instance, value, tdp.getName()));

            return ret;
        } catch (Exception e) {
            return null;
        }
    }

    public void internalDrawing(Graphics g) {
        try {

            if (methodGetWidth == null) {
                createInstance();
                Class[] cArg = new Class[2];
                cArg[0] = Graphics.class;
                cArg[1] = String.class;
                methodGetWidth = classRef.getMethod("getWidth", cArg);
                // TraceManager.addDev("Method =" + methodGetWidth);
            }

            width = (int) (methodGetWidth.invoke(instance, g, value));

            if (methodGetHeight == null) {
                createInstance();
                Class[] cArg = new Class[2];
                cArg[0] = Graphics.class;
                cArg[1] = String.class;
                methodGetHeight = classRef.getMethod("getHeight", cArg);
                // TraceManager.addDev("Method =" + methodGetHeight);
            }

            height = (int) (methodGetHeight.invoke(instance, g, value));

            if (methodInternalDrawing == null) {
                createInstance();
                // TraceManager.addDev("instance =" + instance);
                Class[] cArg = new Class[7];
                cArg[0] = Graphics.class;
                cArg[1] = int.class;
                cArg[2] = int.class;
                cArg[3] = int.class;
                cArg[4] = int.class;
                cArg[5] = String.class;
                cArg[6] = String.class;
                methodInternalDrawing = classRef.getMethod("internalDrawing", cArg);
                // TraceManager.addDev("Method =" + methodInternalDrawing);
            }
            methodInternalDrawing.invoke(instance, g, x, y, width, height, value, tdp.getName());

        } catch (Exception e) {
            TraceManager.addDev("Exception method:" + e.getMessage());
            g.drawString("No plugin available.", x, y);
        }

    }

    @SuppressWarnings("unchecked")
    private void createInstance() {
        try {
            if (componentPlugin == null) {
                // TraceManager.addDev("null component Plugin");
            }
            if (instance == null) {
                // TraceManager.addDev("[create instance] Name of the plugin:" +
                // componentPlugin.getName());
                // String className =
                // componentPlugin.executeRetStringMethod(componentPlugin.getClassGraphicalComponent(),
                // "getGraphicalComponentClassName");
                // classRef = componentPlugin.getClass(className);
                // TraceManager.addDev("[create instance] classRef:" + classRef);
                classRef = componentPlugin.getClassGraphicalComponent();
                instance = componentPlugin.getClassGraphicalComponent().getDeclaredConstructor().newInstance();
                if (width == 0) {
                    width = Plugin.executeIntMethod(instance, "getWidth");
                }
                if (height == 0) {
                    height = Plugin.executeIntMethod(instance, "getHeight");
                }
                moveable = Plugin.executeBoolMethod(instance, "isMoveable");
                removable = Plugin.executeBoolMethod(instance, "isRemovable");
                userResizable = Plugin.executeBoolMethod(instance, "isUserResizable");
                editable = Plugin.executeBoolMethod(instance, "isEditable");

                // TraceManager.addDev("Moveable=" + moveable);
            }
        } catch (Exception e) {
            TraceManager.addDev("No class with Plugin name");
        }
    }

    public TGComponent isOnMe(int _x, int _y) {
        try {
            if (methodIsOnMe == null) {
                createInstance();
                // TraceManager.addDev("instance =" + instance);
                Class[] cArg = new Class[6];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = int.class;
                cArg[3] = int.class;
                cArg[4] = int.class;
                cArg[5] = int.class;
                methodIsOnMe = classRef.getMethod("isOnMe", cArg);
                // TraceManager.addDev("Method=" + methodIsOnMe);
            }
            if ((boolean) (methodIsOnMe.invoke(instance, x, y, width, height, _x, _y))) {
                return this;
            }
            return null;
        } catch (Exception e) {
            TraceManager.addDev("Error when executing isOnMe method in Plugin: " + e.getMessage());
            return null;
        }
    }

    public boolean editOnDoubleClick(JFrame frame) {
        try {
            if (methodEditOnDoubleClick == null) {
                createInstance();
                // TraceManager.addDev("instance / editOnDoubleClick =" + instance);
                Class[] cArg = new Class[2];
                cArg[0] = JFrame.class;
                cArg[1] = String.class;
                // TraceManager.addDev("Getting method");
                methodEditOnDoubleClick = classRef.getMethod("editOnDoubleClick", cArg);
                // TraceManager.addDev("Method methodEditOnDoubleClick =" +
                // methodEditOnDoubleClick);
            }
            String tmp = (String) (methodEditOnDoubleClick.invoke(instance, frame, value));
            if (tmp != null) {
                value = tmp;
                return true;
            }
            return false;
        } catch (Exception e) {
            TraceManager.addDev("Error when executing editOnDoubleClick method in Plugin: " + e.getMessage());
            return false;
        }
    }

    public void setState(int s) {
        state = s;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        if (componentPlugin != null) {
            sb.append("<PluginName value=\"" + componentPlugin.getName() + "\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {

        String s;
        String tmpGlobalCode = "";

        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String pName;
            // boolean mustAddCryptoFunctions = false;

            // TraceManager.addDev("LEP Begin Block = " + this + " trace=");
            // Thread.currentThread().dumpStack();

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("PluginName")) {
                                pName = elt.getAttribute("value");
                                componentPlugin = PluginManager.pluginManager.getPlugin(pName);
                                if (componentPlugin != null) {
                                    if (componentPlugin.hasGraphicalComponent()) {
                                        createInstance();
                                    }
                                } else {
                                    TraceManager.addDev("No corresponding plugin");
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            TraceManager.addDev("Error when loading plugin component");
            throw new MalformedModelingException();
        }
    }

    public int getType() {
        return TGComponentManager.COMPONENT_PLUGIN;
    }

}
