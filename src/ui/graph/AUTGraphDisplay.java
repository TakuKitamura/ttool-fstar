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
   * Class AUTGraphDisplay
   * Creation : 01/12/2016
   ** @version 1.0 01/12/2016
   * @author Ludovic APVRILLE
   * @see
   */

package ui.graph;

import ui.*;
import ui.file.*;
import java.util.*;
import java.io.*;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


import myutil.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.*;
import org.graphstream.ui.view.Viewer.*;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;


public class AUTGraphDisplay  implements MouseListener, ViewerListener, Runnable {

    protected AUTGraph graph;
    protected Viewer viewer;
    protected MultiGraph vGraph;
    protected boolean loop;
    protected MultiNode firstNode;
    protected ArrayList<AbstractEdge> edges;

    public static String STYLE_SHEET =
        "node {" +
        "       fill-color: blue; text-color: white; " +
        "} " +
        //          "edge.defaultedge {" +
        //  "   shape: cubic-curve;" +
        //   "}" +
        //    "edge {shape: cubic-curve}" +
        "edge.external {" +
        "       text-style: bold;" +
        "} " +
        "node.deadlock {" +
        "       fill-color: green; text-color: black; " +
	
        "} " +
        "node.init {" +
        "       fill-color: red;" +
        "} ";

    public AUTGraphDisplay(AUTGraph _graph) {
        graph = _graph;
    }



    public void display() {
        MultiNode node;
        AbstractEdge edge;

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        vGraph = new MultiGraph("TTool graph");
        vGraph.addAttribute("ui.stylesheet", STYLE_SHEET);
        int cpt = 0;
        for(AUTState state: graph.getStates()) {
            node = vGraph.addNode("" + state.id);
            node.addAttribute("ui.label", "" + state.id);
            if (state.getNbOutTransitions() == 0) {
                node.addAttribute("ui.class", "deadlock");
            }
            if (cpt == 0) {
                node.addAttribute("ui.class", "init");
                firstNode = node;
            }
            cpt ++;
        }
        cpt = 0;
	edges = new ArrayList<AbstractEdge>(graph.getTransitions().size());
        for(AUTTransition transition: graph.getTransitions()) {
            edge = vGraph.addEdge(""+cpt, ""+transition.origin, ""+transition.destination, true);
            /*TraceManager.addDev("Transition=" + transition.transition);
              String tmp = Conversion.replaceAllChar(transition.transition, '(', "$");
              tmp = Conversion.replaceAllChar(tmp, ')', "$");
              TraceManager.addDev("Transition=" + tmp);*/
            edge.addAttribute("ui.label", transition.transition);
            edge.addAttribute("ui.class", "defaultedge");
            if (!(transition.transition.startsWith("i("))) {
                edge.addAttribute("ui.class", "external");
            }
	    edges.add(edge);
            cpt ++;
        }
        //viewer = vGraph.display();
        //viewer = new Viewer(vGraph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);

        viewer  = new Viewer(vGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        //SwingUtilities.invokeLater(new InitializeApplication(viewer, vGraph));
        viewer.enableAutoLayout();
        //View   vi = viewer.addDefaultView(true);

        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
        BasicFrame bf = new BasicFrame(viewer, vGraph, graph, edges);

        //vi.addMouseListener(this);



        loop = true;

        Thread t = new Thread(this);
        t.start();

    }



    public void run() {
        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(vGraph);

        // Then we need a loop to do our work and to wait for events.
        // In this loop we will need to call the
        // pump() method before each use of the graph to copy back events
        // that have already occurred in the viewer thread inside
        // our thread

        int cpt = 0;
        TraceManager.addDev("Starting loop:" + cpt);
        while(loop) {
            try {
                //TraceManager.addDev("beg of loop:" + cpt);
                fromViewer.blockingPump(); // or fromViewer.blockingPump(); in the nightly builds
            } catch (Exception e) {TraceManager.addDev("Exception in pump:" + e);}

            if (vGraph.hasAttribute("ui.viewClosed")) {
                TraceManager.addDev("View was closed");
                loop = false;
            } else if (firstNode.hasAttribute("ui.clicked")) {
                TraceManager.addDev("Init node was clicked");
                firstNode.removeAttribute("ui.clicked");
            }

            // here your simulation code.
            //TraceManager.addDev("End of loop" + cpt);
            cpt ++;

            // You do not necessarily need to use a loop, this is only an example.
            // as long as you call pump() before using the graph. pump() is non
            // blocking.  If you only use the loop to look at event, use blockingPump()
            // to avoid 100% CPU usage. The blockingPump() method is only available from
            // the nightly builds.
        }
    }


    public void buttonPushed(String id) {
        TraceManager.addDev("Button pushed on node "+id);
    }

    public void buttonReleased(String id) {
        TraceManager.addDev("Button released on node "+id);
    }

    public void viewClosed(String id) {
        TraceManager.addDev("View closed");
        loop = false;
    }



    public void displaySwing() {
        vGraph  = new MultiGraph("mg");
        vGraph.addAttribute("ui.stylesheet", STYLE_SHEET);
        viewer = new Viewer(vGraph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
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

    class InitializeApplication extends JFrame implements Runnable {
        private static final long serialVersionUID = - 804177406404724792L;
        protected MultiGraph vGraph;
        protected Viewer viewer;
        protected AUTGraph graph;
        protected MultiNode firstNode;


        public InitializeApplication(Viewer viewer, MultiGraph vGraph, AUTGraph autgraph) {
            this.viewer = viewer;
            this.vGraph = vGraph;
            this.graph = autgraph;
        }

        public void run() {
            int cpt = 0;
            MultiNode node;
            AbstractEdge edge;
            for(AUTState state: graph.getStates()) {
                node = vGraph.addNode("" + state.id);
                node.addAttribute("ui.label", "" + state.id);
                if (state.getNbOutTransitions() == 0) {
                    node.addAttribute("ui.class", "deadlock");
                }
                if (cpt == 0) {
                    node.addAttribute("ui.class", "init");
                    firstNode = node;
                }
                cpt ++;
            }
            cpt = 0;
            for(AUTTransition transition: graph.getTransitions()) {
                edge = vGraph.addEdge(""+cpt, ""+transition.origin, ""+transition.destination, true);
                /*TraceManager.addDev("Transition=" + transition.transition);
                  String tmp = Conversion.replaceAllChar(transition.transition, '(', "$");
                  tmp = Conversion.replaceAllChar(tmp, ')', "$");
                  TraceManager.addDev("Transition=" + tmp);*/
                edge.addAttribute("ui.label", transition.transition);
                edge.addAttribute("ui.class", "defaultedge");
                if (!(transition.transition.startsWith("i("))) {
                    edge.addAttribute("ui.class", "external");
                }
                cpt ++;
            }

            viewer.enableAutoLayout();

            add(viewer.addDefaultView( true ), BorderLayout.CENTER );
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(800, 600);
            setVisible(true);
        }

    }

    class BasicFrame extends JFrame implements ActionListener  {
        protected MultiGraph vGraph;
        protected Viewer viewer;
        protected AUTGraph graph;
	protected ArrayList<AbstractEdge> edges;
	
        protected JButton close;
	protected JButton screenshot;
	protected JCheckBox internalActions;
	protected JCheckBox readActions;
	protected JCheckBox higherQuality, antialiasing;
	protected JLabel help;
	

        public BasicFrame(Viewer viewer, MultiGraph vGraph, AUTGraph autgraph, ArrayList<AbstractEdge>_edges) {
            this.viewer = viewer;
            this.vGraph = vGraph;
            this.graph = autgraph;
	    edges = _edges;
            makeComponents();

        }

        public void makeComponents() {
            add(viewer.addDefaultView( false ), BorderLayout.CENTER );
	    //add(viewer, BorderLayout.CENTER );
	    close = new JButton("Close", IconManager.imgic27);
            close.addActionListener(this);
	    screenshot = new JButton("Screenshot in png", IconManager.imgic28);
            close.addActionListener(this);
	    help = new JLabel("Zoom with PageUp/PageDown, move with cursor keys");
	    internalActions = new JCheckBox("Display internal actions", true);
	    internalActions.addActionListener(this);
	    readActions = new JCheckBox("Display read/write actions", true);
	    readActions.addActionListener(this);
	    higherQuality =  new JCheckBox("Higher drawing quality", false);
	    higherQuality.addActionListener(this);
	    antialiasing =  new JCheckBox("Anti aliasing", false);
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
	    infoPanel.add(help, BorderLayout.NORTH);
	    infoPanel.add(close, BorderLayout.SOUTH);
	    infoPanel.add(jp01, BorderLayout.CENTER);

	    
	    add(infoPanel, BorderLayout.SOUTH);
            //setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(800, 600);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent evt)  {
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
            dispose();
        }

	public void screenshot() {
	    JFileChooser jfcggraph;
            if (ConfigurationTTool.GGraphPath.length() > 0) {
		jfcggraph = new JFileChooser(ConfigurationTTool.GGraphPath);
	    } else {
		jfcggraph = new JFileChooser();
	    }
	    PNGFilter filter = new PNGFilter();
	    jfcggraph.setFileFilter(filter);
        }

	public void manageInternalActions() {
	    if (edges == null) {
		return;
	    }
	    int cpt =0;
	    for(AUTTransition transition: graph.getTransitions()) {
		if (transition.transition.startsWith("i(")) {
		    if (internalActions.isSelected()) {
			edges.get(cpt).addAttribute("ui.label", transition.transition);
		    } else {
			edges.get(cpt).addAttribute("ui.label", "");
		    }
		}
		cpt ++;
	    }
	}

	public void manageReadActions() {
	    if (edges == null) {
		return;
	    }
	    int cpt =0;
	    for(AUTTransition transition: graph.getTransitions()) {
		if (transition.transition.contains("?")) {
		    if (readActions.isSelected()) {
			edges.get(cpt).addAttribute("ui.label", transition.transition);
		    } else {
			edges.get(cpt).addAttribute("ui.label", "");
		    }
		}
		cpt ++;
	    }
	}

	public void manageHigherQuality() {
	    viewer.disableAutoLayout();
	    if (higherQuality.isSelected()) {
		vGraph.addAttribute("ui.quality");
	    } else {
		vGraph.removeAttribute("ui.quality");
	    }
	    viewer.enableAutoLayout();
	}

	public void manageAntialiasing() {
	    viewer.disableAutoLayout();
	    if (antialiasing.isSelected()) {
		vGraph.addAttribute("ui.antialias");
	    } else {
		vGraph.removeAttribute("ui.antialias");
	    }
	    viewer.enableAutoLayout();
	}

	
	
    } // Basic Frame
    
    
} // Main class
