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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class JFrameStatistics
 * Creation: 11/01/2021
 * version 1.0 11/01/2021
 *
 * @author Ludovic APVRILLE
 */
public class JFrameStatistics extends JFrame implements ActionListener, GenericTree {
    protected JButton buttonClose, buttonCloseAllTabs;
    protected JCheckBox checkHistogram, checkPieChart;
    protected JScrollPane jsp;
    protected JTabbedPane mainPane;

    protected Thread t;
    protected int threadMode = 0;
    protected boolean go;
    ArrayList<DataElement> elements;
    private String title;


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

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftTreePanel, showStat);
        split.setDividerLocation(0.80);
        mainPanel.add(split, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();

        checkHistogram = new JCheckBox("Use histogram");
        checkHistogram.setSelected(true);
        topPanel.add(checkHistogram);
        checkPieChart = new JCheckBox("Use Pie Chart");
        checkPieChart.setSelected(true);
        topPanel.add(checkPieChart);
        buttonCloseAllTabs = new JButton("Close all tabs", IconManager.imgic27);
        buttonCloseAllTabs.addActionListener(this);
        topPanel.add(buttonCloseAllTabs);
        buttonClose = new JButton("Close Window", IconManager.imgic27);
        buttonClose.addActionListener(this);
        topPanel.add(buttonClose);
        mainPanel.add(topPanel, BorderLayout.SOUTH);


        this.add(mainPanel);

    }


    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == buttonClose) {
            quit();
        } else if (ae.getSource() == buttonCloseAllTabs) {
            closeAllTabs();
        }
    }

    public void quit() {
        dispose();
    }

    public void closeAllTabs() {
        mainPane.removeAll();
    }


    // Showing stats
    public void showStats(DataElement de) {
        if ((de.data == null) || (de.data.length == 0)) {
            return;
        }

        if (checkHistogram.isSelected()) {
            showHistogram(de);
        }

        if (checkPieChart.isSelected()) {
            showPieChart(de);
        }

    }

    public void showHistogram(DataElement de) {
        String title = "Histogram of " + de.toString();

        // Tab already exist?
        if (mainPane.indexOfTab(title) > -1) {
            mainPane.setSelectedIndex(mainPane.indexOfTab(title));
            return;
        }

        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        dataset.addSeries(title, de.data, 100);

        JFreeChart histogram = ChartFactory.createHistogram("Histogram: " + de.toString(),
                de.toString(), "Frequency", dataset);

        ChartPanel myChart = new ChartPanel(histogram);
        // Adding histogram to tabbed pane
        addChart(title, myChart);

    }

    @SuppressWarnings("unchecked")
    public void showPieChart(DataElement de) {
        String title = "PieChart of " + de.toString();

        // Tab already exist?
        if (mainPane.indexOfTab(title) > -1) {
            mainPane.setSelectedIndex(mainPane.indexOfTab(title));
            return;
        }

        DefaultPieDataset dataset = new DefaultPieDataset();

        HashMap<Double, Integer> map = new HashMap<>();
        for (int i = 0; i < de.data.length; i++) {
            if (map.containsKey(de.data[i])) {
                Integer myInt = map.get(de.data[i]);
                map.put(de.data[i], new Integer(myInt.intValue() + 1));
            } else {
                map.put(de.data[i], 0);
            }
        }

        for (Double d : map.keySet()) {
            dataset.setValue(d, map.get(d));
        }


        JFreeChart pieChart = ChartFactory.createPieChart(
                title,   // chart title
                dataset,          // data
                true,             // include legend
                true,
                false);

        ChartPanel myChart = new ChartPanel(pieChart);
        addChart(title, myChart);


    }

    public void addChart(String title, ChartPanel myChart) {
        myChart.setMouseWheelEnabled(true);
        mainPane.addTab(title, myChart);
        ButtonTabComponent ctb = new ButtonTabComponent(mainPane);
        mainPane.setTabComponentAt(mainPane.getTabCount() - 1, ctb);
        mainPane.setSelectedIndex(mainPane.getTabCount() - 1);

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
