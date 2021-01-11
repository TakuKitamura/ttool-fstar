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

import avatartranslator.*;
import avatartranslator.directsimulation.*;
import common.ConfigurationTTool;
import myutil.*;
import myutilsvg.SVGGeneration;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import ui.*;
import ui.avatarbd.AvatarBDPortConnector;
import ui.interactivesimulation.LatencyTableModel;
import ui.interactivesimulation.SimulationLatency;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Class JFrameStatistics
 * Creation: 11/01/2021
 * version 1.0 11/01/2021
 *
 * @author Ludovic APVRILLE
 */
public class JFrameStatistics extends JFrame implements ActionListener, GenericTree {
    protected JButton buttonClose;
    protected JScrollPane jsp;
    protected JTabbedPane mainPane;

    protected Thread t;
    protected int threadMode = 0;
    protected boolean go;

    private String title;

    ArrayList<DataElement> elements;



    public JFrameStatistics(String _title, ArrayList<DataElement> _elements) {
        super(_title);

        elements = _elements;
        title = _title;

        makePanelsAndComponents();

    }

    private void makePanelsAndComponents() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setIconImage(IconManager.img5100);
        setBackground(Color.WHITE);

        try {
            setBackground(new Color(50, 40, 40, 200));
        } catch (Exception e) {
            setBackground(new Color(50, 40, 40));
        }

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Tree panel
        JPanel leftTreePanel = new JPanel();
        JTreeStats statTree = new JTreeStats(this);
        JScrollPane scrollPane = new JScrollPane(statTree);
        scrollPane.setPreferredSize(new Dimension(200, 600));
        scrollPane.setMinimumSize(new Dimension(25, 200));
        leftTreePanel.add(scrollPane);

        // Stat panel
        JPanel showStat = new JPanel();
        mainPane = new JTabbedPane();
        showStat.add(mainPane);

        JSplitPane split =new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftTreePanel, showStat);
        split.setDividerLocation(0.80);
        mainPanel.add(split, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        buttonClose = new JButton("Close", IconManager.imgic27);
        buttonClose.addActionListener(this);
        topPanel.add(buttonClose);
        mainPanel.add(topPanel, BorderLayout.SOUTH);


        this.add(mainPanel);

    }



    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == buttonClose) {
            quit();
        }
    }

    public void quit() {
        dispose();
    }


    // Showing stats
    public void showStats(DataElement de) {
        if ((de.data == null) || (de.data.length == 0)) {
            return;
        }

        // Tab already exist?

        String title = "Histogram of " + de.toString()  + ": value";
        if (mainPane.indexOfTab(title) > -1) {
            mainPane.setSelectedIndex(mainPane.indexOfTab(title));
            return;
        }

        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(title, de.data, 20);

        JFreeChart histogram = ChartFactory.createHistogram("Histogram: " + de.toString(),
                de.toString(), "Frequency", dataset);

        // Adding histogram to tabbed pane
        ChartPanel myChart = new ChartPanel(histogram);
        myChart.setMouseWheelEnabled(true);
        mainPane.addTab(title, myChart);
        ButtonTabComponent ctb = new ButtonTabComponent(mainPane);
        mainPane.setTabComponentAt(mainPane.getTabCount()-1, ctb);
        mainPane.setSelectedIndex(mainPane.getTabCount()-1);

        mainPane.validate();
    }



    // tree
    public int getChildCount() {
        return elements.size();
    }


    public Object getChild(int index) {
        if (index < getChildCount()) {
            return elements.get(index);
        }
        return null;
    }

    public int getIndexOfChild(Object child) {
        if (child == null) {
            return -1;
        }
        return elements.indexOf(child);
    }

    public String toString() {
        return title;
    }






} // Class
