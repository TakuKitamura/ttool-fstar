/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class ThreadGUIElement
 *
 * Creation: 27/04/2007
 * @version 1.0 27/04/2007
 * @author Ludovic APVRILLE
 * @see
 */


package ui;

import myutil.ExternalCall;
import myutil.GraphicLib;
import myutil.TraceManager;
import ui.graph.AUTGraph;
import ui.graph.RG;
import ui.util.IconManager;
import ui.window.JDialogCancel;
import ui.window.JFrameStatistics;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;


public class ThreadGUIElement extends Thread {
    private String param0, param1, param2, param3;
    private Object obj0, obj1, obj2, obj3;
    private int function;
    private StoppableGUIElement sge;
    private JFrameStatistics jfs;
    private DocumentationGenerator docgen;
    private JDialogCancel jdc;
    private Frame frame;
    private ExternalCall ec;
    private AUTGraph graph;
    private RG rg;
    private boolean showStat;

    public ThreadGUIElement (Frame _frame, int _function, String _param0, String _param1, String _param2, AUTGraph _graph, RG _rg, boolean _showStat) {
        frame = _frame;
        function = _function;
        param0 = _param0;
        param1 = _param1;
        param2 = _param2;
        graph = _graph;
        rg = _rg;
        showStat = _showStat;
    }

    public ThreadGUIElement (Frame _frame, int _function, String _param0, String _param1, String _param2) {
        frame = _frame;
        function = _function;
        param0 = _param0;
        param1 = _param1;
        param2 = _param2;
    }

    public ThreadGUIElement (Frame _frame, int _function, Object _obj0, Object _obj1, Object _obj2, Object _obj3, String _param0, String _param2) {
        frame = _frame;
        function = _function;
        obj0 = _obj0;
        obj1 = _obj1;
        obj2 = _obj2;
        obj3 = _obj3;
        param0 = _param0;
        param2 = _param2;
    }

    public void setExternalCall(ExternalCall _ec) {
        ec = _ec;
    }

    public void setStoppableGUIElement(StoppableGUIElement _sge) {
        sge = _sge;
    }

    public void go() {
        if (ec != null) {
            jdc = new JDialogCancel(frame, param0, param1, sge);
	    start();
            GraphicLib.centerOnParent(jdc, 300, 200 );
            //   jdc.setSize(300, 200);
            jdc.setVisible(true);
            //jdc = null;
        } else {
            switch(function) {
            case 1:
                docgen = new DocumentationGenerator((Vector)obj0, (JTabbedPane)obj1, (String)obj2, (String)obj3);
                docgen.setFirstHeadingNumber(2);
                sge = (StoppableGUIElement)docgen;
                break;
            case 0:
            default:
		TraceManager.addDev("Creating jframe statistics");
                jfs = new JFrameStatistics(param0, param1, graph);
                sge = (StoppableGUIElement)jfs;
            }
	    TraceManager.addDev("Dialog creation");
            jdc = new JDialogCancel(frame, param0, param2, sge);
	    start();
            //  jdc.setSize(400, 200);
            GraphicLib.centerOnParent(jdc, 400, 200 );
            jdc.setVisible(true);
            //jdc = null;
        }
    }

    public void run() {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 1);
        if (ec != null) {
            //TraceManager.addDev("Starting computing function");
            ec.computeFunction(function);
            //TraceManager.addDev("Ending computing function");
            if (jdc != null) {
		//TraceManager.addDev("Stopping jdc");
                jdc.stopAll();
		jdc = null;
            }
        } else {
            switch(function) {
            case 1:
                boolean res = docgen.generateDocumentation();
                if (jdc != null) {
                    jdc.stopAll();
                }
                if (res && !docgen.hasBeenStopped()) {
                    JOptionPane.showMessageDialog(frame,
                                                  "All done!",
                                                  "Documentation generation",
                                                  JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                                                  "The documentation generation could not be performed",
                                                  "     Error",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case 0:
            default:
		//TraceManager.addDev("JFrameStatistics go Element");
                jfs.goElement();
                //TraceManager.addDev("go is done");

                if (jfs.hasBeenStopped()) {
                    //TraceManager.addDev("Stopped: not showing");
                    return;
                }
                if (jdc != null) {
                    jdc.stopAll();
                }

                //  jfs.setSize(600, 600);
                if (showStat) {
                    jfs.setIconImage(IconManager.img8);
                    GraphicLib.centerOnParent(jfs, 600, 600 );
                    jfs.setVisible(true);
                } else {
                    // Display graph
                    jfs.displayGraph();

                }
                if (rg != null) {
                    rg.graph = jfs.getGraph();
                    if (rg.graph != null) {
                        rg.data = null;
                    }
                }
            }
        }

    }
}
