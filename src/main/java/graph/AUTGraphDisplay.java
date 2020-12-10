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


package graph;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.TraceManager;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.MultiNode;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.camera.Camera;
import ui.file.PNGFilter;
import ui.util.IconManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;


/**
 * Class AUTGraphDisplay
 * Creation : 01/12/2016
 * * @version 1.0 01/12/2016
 *
 * @author Ludovic APVRILLE
 */
public class AUTGraphDisplay implements MouseListener, ViewerListener, Runnable {

    protected AUTGraph graph;
    protected SwingViewer viewer;
    protected MultiGraph vGraph;
    protected boolean loop;
    protected Node firstNode;
    protected ArrayList<Edge> edges;
    protected boolean exitOnClose = false;
    protected BasicFrame bf;

    // see http://graphstream-project.org/doc/Advanced-Concepts/GraphStream-CSS-Reference/


    protected static String STYLE_SHEET = "node {fill-color: #B1CAF1; text-color: black; size: 20px, 20px; text-size:14;}     " +
            "edge {text-color: black; shape: cubic-curve; text-size:10;}    " +
            "edge.defaultedge {text-size:10; text-color:black;}  " +
            "edge.external {text-color:blue; text-size:14;}    " +
            "node.deadlock {fill-color: red; text-color: white; size: 20px, 20px; text-size:16;}    " +
            "node.init { fill-color: green; text-color: black; size: 20px, 20px; text-size:16;}";

    protected static String STYLE_SHEET2 = "graph {  canvas-color: white; fill-mode: gradient-vertical; fill-color: white, #004; padding: 20px;  } " +
            "node { shape: circle; size-mode: dyn-size; size: 10px; fill-mode: gradient-radial; fill-color: #FFFC, #FFF0; stroke-mode: none; " +
            "shadow-mode: gradient-radial; shadow-color: #FFF5, #FFF0; shadow-width: 5px; shadow-offset: 0px, 0px; } " +
            "node: clicked { fill-color: #F00A, #F000;  } node: selected { fill-color: #00FA, #00F0;  } " +
            "edge { shape: angle; size: 1px; fill-color: red; fill-mode: plain; arrow-shape: circle; } " +
            "edge.defaultedge { shape: curve-cubic; size: 1px; fill-color: #FFF3; fill-mode: plain; arrow-shape: none; } " +
            "edge.external { shape: L-square-line; size: 3px; fill-color: #AAA3; fill-mode: plain; arrow-shape: circle; } " +
            "sprite { shape: circle; fill-mode: gradient-radial; fill-color: #FFF8, #FFF0; }";

    /*public static String STYLE_SHEET =
            "node {" +
                    "       fill-color: #B1CAF1; text-color: black; size: 11px, 11px;" +
                    "} " +
                    "edge.default edge {" + "text-color: blue;" + " shape: cubic-curve; }" +
                    "edge.external {" +
                    //"       arrow-shape: circle" +
                    "       text-style: bold;" +
                    "} " +
                    "node.deadlock {" +
                    "       fill-color: red; text-color: white; size: 15px, 15px;" +

                    "} " +
                    "node.init {" +
                    "       fill-color: green; text-color: black; size: 15px, 15px;" +
                    "} ";*/

    public AUTGraphDisplay(AUTGraph _graph, boolean _exitOnClose) {
        graph = _graph;
        exitOnClose = _exitOnClose;
    }


    public void display() {
        Node node;
        Edge edge;

        Logger l0 = Logger.getLogger("");
        try {
            if (l0 != null) {
                l0.removeHandler(l0.getHandlers()[0]);
            }
        } catch (Exception e) {
        }

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        vGraph = new MultiGraph("TTool graph");
        if ((ConfigurationTTool.RGStyleSheet != null) && (ConfigurationTTool.RGStyleSheet.trim().length() > 0)) {
            TraceManager.addDev("Adding stylesheet:" + ConfigurationTTool.RGStyleSheet + "\n\nvs default:" + STYLE_SHEET);
            vGraph.setAttribute("ui.stylesheet", ConfigurationTTool.RGStyleSheet);
        } else {
            vGraph.setAttribute("ui.stylesheet", STYLE_SHEET);
        }
        //vGraph.setAttribute("ui.stylesheet", STYLE_SHEET);


        //vGraph.addAttribute("layout.weight", 0.5);
        int cpt = 0;
        graph.computeStates();
        for (AUTState state : graph.getStates()) {
            node = vGraph.addNode("" + state.id);
            node.setAttribute("ui.label", "" + state.id);
            if (state.getNbOutTransitions() == 0) {
                node.setAttribute("ui.class", "deadlock");
            }
            if (cpt == 0) {
                node.setAttribute("ui.class", "init");
                firstNode = node;
            }
            cpt++;
        }
        cpt = 0;
        //TraceManager.addDev("Here we are!");
        edges = new ArrayList<Edge>(graph.getTransitions().size());
        HashSet<AUTTransition> transitionsMet = new HashSet<>();
        for (AUTTransition transition : graph.getTransitions()) {
            edge = vGraph.addEdge("" + cpt, "" + transition.origin, "" + transition.destination, true);
            /*TraceManager.addDev("Transition=" + transition.transition);
              String tmp = Conversion.replaceAllChar(transition.transition, '(', "$");
              tmp = Conversion.replaceAllChar(tmp, ')', "$");
              TraceManager.addDev("Transition=" + tmp);*/
            edge.setAttribute("ui.label", graph.removeOTime(graph.removeSameSignal(graph.getCompoundString(transition, transitionsMet))));
            //edge.setAttribute("ui.style", "text-offset: -50, -50;");
            //edge.addAttribute("ui.class", "edge");
            //edge.addAttribute("shape", "cubic-curve");
            //edge.addAttribute("arrow-shape", "circle");
            edge.setAttribute("layout.weight", 0.4);
            if (!(transition.transition.startsWith("i("))) {
                edge.setAttribute("ui.class", "external");
            } else {
                edge.setAttribute("ui.class", "defaultedge");
            }
            edges.add(edge);
            cpt++;
        }
        //viewer = vGraph.display();
        //viewer = new Viewer(vGraph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);

        viewer = new SwingViewer(vGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        //SwingUtilities.invokeLater(new InitializeApplication(viewer, vGraph));
        viewer.enableAutoLayout();
        //View   vi = viewer.addDefaultView(true);

        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
        bf = new BasicFrame(this, viewer, vGraph, graph, edges, exitOnClose);
        //vi.addMouseListener(this);


        loop = true;

        Thread t = new Thread(this);
        t.start();

    }

    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        /*ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(vGraph);*/


        // Then we need a loop to do our work and to wait for events.
        // In this loop we will need to call the
        // pump() method before each use of the graph to copy back events
        // that have already occurred in the viewer thread inside
        // our thread

        int cpt = 0;
        ViewerPipe pipeIn = viewer.newViewerPipe();
        pipeIn.addSink(vGraph);
        pipeIn.addViewerListener(this);



        while (loop) {
            try {
                pipeIn.blockingPump(50);
            } catch (Exception e) {

            }


            if (vGraph.hasAttribute("ui.viewClosed")) {
                //TraceManager.addDev("View was closed");
                loop = false;
                if (exitOnClose) {
                    System.exit(1);
                }
            }

            bf.updateMe();
        }


        //TraceManager.addDev("Starting loop:" + cpt);
        /*while (loop) {
            try {
                //TraceManager.addDev("beg of loop:" + cpt);
                fromViewer.blockingPump(); // or fromViewer.blockingPump(); in the nightly builds
            } catch (Exception e) {//TraceManager.addDev("Exception in pump:" + e);
            }

            if (vGraph.hasAttribute("ui.viewClosed")) {
                //TraceManager.addDev("View was closed");
                loop = false;
                if (exitOnClose) {
                    System.exit(1);
                }
            } */

        // here your simulation code.
        //TraceManager.addDev("End of loop" + cpt);
        //cpt ++;

        // You do not necessarily need to use a loop, this is only an example.
        // as long as you call pump() before using the graph. pump() is non
        // blocking.  If you only use the loop to look at event, use blockingPump()
        // to avoid 100% CPU usage. The blockingPump() method is only available from
        // the nightly builds.
    }


    public void buttonPushed(String id) {
        TraceManager.addDev("Button pushed on node " + id);
    }

    public void buttonReleased(String id) {
        TraceManager.addDev("Button released on node " + id);
    }

    public void viewClosed(String id) {
        //TraceManager.addDev("View closed and closed !");
        loop = false;
        if (viewer != null) {
            viewer.close();
            viewer.disableAutoLayout();
        }
        viewer = null;
        vGraph.clear();
        if (exitOnClose) {
            System.exit(1);
        }
    }


    public void displaySwing() {
        vGraph = new MultiGraph("mg");
        vGraph.setAttribute("ui.stylesheet", STYLE_SHEET);
        viewer = new SwingViewer(vGraph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        SwingUtilities.invokeLater(new InitializeApplication(viewer, vGraph, graph));
    }

    public void mousePressed(MouseEvent e) {
        TraceManager.addDev("Mouse pressed; # of clicks: "
                + e.getClickCount());
    }

    public void mouseReleased(MouseEvent e) {
        TraceManager.addDev("Mouse released; # of clicks: "
                + e.getClickCount());
    }

    public void mouseEntered(MouseEvent e) {
        TraceManager.addDev("Mouse entered");
    }

    public void mouseExited(MouseEvent e) {
        TraceManager.addDev("Mouse exited");
    }

    public void mouseClicked(MouseEvent e) {
        TraceManager.addDev("Mouse clicked (# of clicks: "
                + e.getClickCount() + ")");
    }


    public void mouseOver(java.lang.String id) {
        TraceManager.addDev("Mouse over: " + id);
    }


    public void mouseLeft(java.lang.String id) {
        TraceManager.addDev("Mouse over: " + id);
    }


class InitializeApplication extends JFrame implements Runnable {
    private static final long serialVersionUID = -804177406404724792L;
    protected MultiGraph vGraph;
    protected SwingViewer viewer;
    protected AUTGraph graph;
    protected Node firstNode;


    public InitializeApplication(SwingViewer viewer, MultiGraph vGraph, AUTGraph autgraph) {
        this.viewer = viewer;
        this.vGraph = vGraph;
        this.graph = autgraph;
    }

    public void run() {
        int cpt = 0;
        Node node;
        Edge edge;
        for (AUTState state : graph.getStates()) {
            node = vGraph.addNode("" + state.id);
            node.setAttribute("ui.label", "" + state.id);
            if (state.getNbOutTransitions() == 0) {
                node.setAttribute("ui.class", "deadlock");
            }
            if (cpt == 0) {
                node.setAttribute("ui.class", "init");
                firstNode = node;
            }
            cpt++;
        }
        cpt = 0;
        // We must merge the transitions with the same starting and ending state
        HashSet<AUTTransition> transitionsMet = new HashSet<>();
        for (AUTTransition transition : graph.getTransitions()) {
            edge = vGraph.addEdge("" + cpt, "" + transition.origin, "" + transition.destination, true);

            transitionsMet.add(transition);
            edge = vGraph.addEdge("" + cpt, "" + transition.origin, "" + transition.destination, true);
                /*TraceManager.addDev("Transition=" + transition.transition);
                  String tmp = Conversion.replaceAllChar(transition.transition, '(', "$");
                  tmp = Conversion.replaceAllChar(tmp, ')', "$");
                  TraceManager.addDev("Transition=" + tmp);*/
            edge.setAttribute("ui.label", transition.transition);
            edge.setAttribute("ui.class", "classic");
            if (!(transition.transition.startsWith("i("))) {
                edge.setAttribute("ui.class", "external");
            }
            cpt++;

        }

        viewer.enableAutoLayout();
        DefaultView dv = new DefaultView(viewer, "Graph", new SwingGraphRenderer());
        //ViewPanel vp = viewer.addDefaultView(true, new SwingGraphRenderer());
        add(dv, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);
    }

}

class BasicFrame extends JFrame implements ActionListener {
    protected MultiGraph vGraph;
    protected SwingViewer viewer;
    protected JPanel viewerPanel;
    protected AUTGraph graph;
    protected ArrayList<Edge> edges;

    protected JButton close;
    protected JButton screenshot;
    protected JCheckBox internalActions;
    protected JCheckBox readActions;
    protected JCheckBox higherQuality, antialiasing;
    protected JLabel help, info;

    protected boolean exitOnClose;

    private AUTGraphDisplay autD;

    private DefaultView dv;


    public BasicFrame(AUTGraphDisplay autD, SwingViewer viewer, MultiGraph vGraph, AUTGraph autgraph, ArrayList<Edge> _edges,
                      boolean _exitOnClose) {
        this.autD = autD;
        this.viewer = viewer;
        this.vGraph = vGraph;
        this.graph = autgraph;
        edges = _edges;
        exitOnClose = _exitOnClose;
        makeComponents();
        if (exitOnClose) {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

    }

    public void makeComponents() {
        dv = new DefaultView(viewer, "Graph", new SwingGraphRenderer());
        //viewerPanel = viewer.addDefaultView(false);
        add(dv, BorderLayout.CENTER);
        //add(viewer, BorderLayout.CENTER );
        close = new JButton("Close", IconManager.imgic27);
        close.addActionListener(this);
        screenshot = new JButton("Save in png", IconManager.imgic28);
        screenshot.addActionListener(this);
        close.addActionListener(this);
        help = new JLabel("Zoom with PageUp/PageDown, move with cursor keys");
        info = new JLabel("Graph: " + graph.getNbOfStates() + " states, " + graph.getNbOfTransitions() + " transitions");
        internalActions = new JCheckBox("Display internal actions", true);
        internalActions.addActionListener(this);
        readActions = new JCheckBox("Display read/write actions", true);
        readActions.addActionListener(this);
        higherQuality = new JCheckBox("Higher drawing quality", false);
        higherQuality.addActionListener(this);
        antialiasing = new JCheckBox("Anti aliasing", false);
        antialiasing.addActionListener(this);


        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Options"));
        //c01.gridwidth = 1;
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.fill = GridBagConstraints.HORIZONTAL;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp01.add(screenshot);
        jp01.add(internalActions);
        jp01.add(readActions);
        jp01.add(higherQuality);
        jp01.add(antialiasing);

        JPanel infoPanel = new JPanel(new BorderLayout());
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(help, BorderLayout.EAST);
        labelPanel.add(info, BorderLayout.WEST);
        infoPanel.add(labelPanel, BorderLayout.NORTH);
        infoPanel.add(close, BorderLayout.SOUTH);
        infoPanel.add(jp01, BorderLayout.CENTER);


        add(infoPanel, BorderLayout.SOUTH);
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == close) {
            closeFrame();
        } else if (evt.getSource() == screenshot) {
            screenshot();
        } else if (evt.getSource() == internalActions) {
            manageInternalActions();
        } else if (evt.getSource() == readActions) {
            manageReadActions();
        } else if (evt.getSource() == higherQuality) {
            manageHigherQuality();
        } else if (evt.getSource() == antialiasing) {
            manageAntialiasing();
        }
    }

    public void closeFrame() {
        if (autD != null) {
            autD.viewClosed("closed pushed");
        }
        if (exitOnClose) {
            System.exit(1);
        }
        dispose();
    }

    public void takeScreenshot(Component component, File file) {
        BufferedImage image = new BufferedImage(
                component.getWidth(),
                component.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        // call the Component's paint method, using
        // the Graphics object of the image.
        component.paint(image.getGraphics()); // alternately use .printAll(..)

        try {
            // save captured image to PNG file
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
        }

    }

    public void screenshot() {
        TraceManager.addDev("Screenshot");
        JFileChooser jfcggraph;
        if (SpecConfigTTool.GGraphPath.length() > 0) {
            jfcggraph = new JFileChooser(SpecConfigTTool.GGraphPath);
        } else {
            jfcggraph = new JFileChooser();
        }
        PNGFilter filter = new PNGFilter();
        jfcggraph.setFileFilter(filter);
        int returnVal = jfcggraph.showDialog(this, "Graph capture (in png)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File pngFile = jfcggraph.getSelectedFile();
        TraceManager.addDev("Making the screenshot in " + pngFile.getAbsolutePath());


        //vGraph.addAttribute("ui.screenshot", pngFile.getAbsolutePath());
        //vGraph.addAttribute("ui.screenshot", "/homes/apvrille/tmp/toto.png");

        takeScreenshot(dv, pngFile);

            /*FileSinkImages pic = new FileSinkImages(OutputType.PNG, Resolutions.UXGA);
            //pic.setQuality();
            //pic.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
            pic.setLayoutPolicy(LayoutPolicy.COMPUTED_IN_LAYOUT_RUNNER);
            try {
                pic.writeAll(vGraph, pngFile.getAbsolutePath());
            } catch (IOException e) {
                TraceManager.addDev("Capture could not be performed: " + e.getMessage());
            }*/

        //vGraph.addAttribute("ui.screenshot", "/tmp/toto.png");
        TraceManager.addDev("Screenshot performed");
    }


    public synchronized void manageInternalActions() {
        if (edges == null) {
            return;
        }
        int cpt = 0;
        for (AUTTransition transition : graph.getTransitions()) {
            if (transition.transition.startsWith("i(")) {
                if (internalActions.isSelected()) {
                    edges.get(cpt).setAttribute("ui.label", transition.transition);
                } else {
                    edges.get(cpt).setAttribute("ui.label", "");
                }
            }
            cpt++;
        }
        //dv.repaint();
    }

    public synchronized void manageReadActions() {
        if (edges == null) {
            return;
        }
        int cpt = 0;
        for (AUTTransition transition : graph.getTransitions()) {
            if (transition.transition.contains("?")) {
                if (readActions.isSelected()) {
                    edges.get(cpt).setAttribute("ui.label", transition.transition);
                } else {
                    edges.get(cpt).setAttribute("ui.label", "");
                }
            }
            cpt++;
        }
        //dv.repaint();
    }

    public synchronized void manageHigherQuality() {
        //viewer.disableAutoLayout();
        if (higherQuality.isSelected()) {
            vGraph.setAttribute("ui.quality");
        } else {
            vGraph.removeAttribute("ui.quality");
        }
        try {
            //viewer.enableAutoLayout();
            //dv.repaint();
        } catch (Exception e) {
        }
    }


        /*public void updateCameraView() {
            Camera camera = controller.getPanel().getCamera();
            cameraAutoFitView.setSelected(autoFit);
            viewCenterX.setText(camera.getViewCenter().x+"");
            viewCenterY.setText(camera.getViewCenter().y+"");
            scale.setText(camera.getViewPercent()+"");
        }*/

    public synchronized void manageAntialiasing() {
        //viewer.disableAutoLayout();
        if (antialiasing.isSelected()) {
            vGraph.setAttribute("ui.antialias");
        } else {
            vGraph.removeAttribute("ui.antialias");
        }
        try {
            //viewer.enableAutoLayout();
            //dv.repaint();
        } catch (Exception e) {
        }
    }

    public synchronized void updateMe() {
        dv.repaint();
    }


} // Basic Frame


} // Main class
