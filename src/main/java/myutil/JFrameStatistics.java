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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.*;
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
    protected JCheckBox checkHistogram, checkPieChart, checkTimeValueChart, checkTimeValueBlockChart,
    checkValueEvolution;
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        checkHistogram = new JCheckBox("Histogram chart");
        checkHistogram.setSelected(true);
        topPanel.add(checkHistogram);

        checkPieChart = new JCheckBox("Pie chart");
        checkPieChart.setSelected(true);
        topPanel.add(checkPieChart);

        checkTimeValueChart = new JCheckBox("Value = f(t) chart");
        checkTimeValueChart.setSelected(true);
        topPanel.add(checkTimeValueChart);

        checkTimeValueBlockChart = new JCheckBox("Value = f(t) block chart");
        checkTimeValueBlockChart.setSelected(true);
        //topPanel.add(checkTimeValueBlockChart);

        checkValueEvolution = new JCheckBox("Value = f(t) per simulation");
        checkValueEvolution.setSelected(true);
        topPanel.add(checkValueEvolution);

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

        if (checkHistogram.isSelected()) {
            showHistogram(de);
        }

        if (checkPieChart.isSelected()) {
            showPieChart(de);
        }

        if (checkTimeValueChart.isSelected()) {
            showTimeValueChart(de);
        }

        /*if (checkTimeValueBlockChart.isSelected()) {
            showTimeValueBlockChart(de);
        }*/

        if (checkValueEvolution.isSelected()) {
            showValueEvolutionChart(de);
        }

    }

    public void showHistogram(DataElement de) {
        if ((de.data == null) || (de.data.length == 0)) {
            return;
        }

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

        XYPlot plot = (XYPlot) histogram.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        ChartPanel myChart = new ChartPanel(histogram);
        // Adding histogram to tabbed pane
        addChart(title, myChart, true);

    }

    @SuppressWarnings("unchecked")
    public void showPieChart(DataElement de) {
        if ((de.data == null) || (de.data.length == 0)) {
            return;
        }

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
                map.put(de.data[i], 1);
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

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("({1}) {2}"));

        ChartPanel myChart = new ChartPanel(pieChart);
        addChart(title, myChart);


    }

    @SuppressWarnings("unchecked")
    public void showTimeValueLineChart(DataElement de) {
        if ((de.times == null) || (de.data.length != de.times.length)) {
            return;
        }

        String title = "Value per Time of " + de.toString();

        // Tab already exist?
        if (mainPane.indexOfTab(title) > -1) {
            mainPane.setSelectedIndex(mainPane.indexOfTab(title));
            return;
        }

        XYSeries series = new XYSeries("Value per Time");

        for(int i=0; i<de.data.length; i++) {
            series.add(de.times[i], de.data[i]);
        }


        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Value = f(t) chart", // Title
                "Time", // x-axis Label
                "Value", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        ChartPanel myChart = new ChartPanel(chart);
        addChart(title, myChart, true);


    }

    @SuppressWarnings("unchecked")
    public void showTimeValueChart(DataElement de) {
        if ((de.times == null) || (de.data.length != de.times.length)) {
            return;
        }

        String title = "Value per Time of " + de.toString();

        // Tab already exist?
        if (mainPane.indexOfTab(title) > -1) {
            mainPane.setSelectedIndex(mainPane.indexOfTab(title));
            return;
        }

        XYSeries series = new XYSeries("Value per Time");

        for(int i=0; i<de.data.length; i++) {
            series.add(de.times[i], de.data[i]);
        }


        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Value = f(t) chart", // Title
                "Time", // x-axis Label
                "Value", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        ChartPanel myChart = new ChartPanel(chart);
        addChart(title, myChart, true);


    }

    @SuppressWarnings("unchecked")
    public void showTimeValueBlockChart(DataElement de) {
        if ((de.times == null) || (de.data.length != de.times.length)) {
            return;
        }

        String title = "Block Value per Time of " + de.toString();

        // Tab already exist?
        if (mainPane.indexOfTab(title) > -1) {
            mainPane.setSelectedIndex(mainPane.indexOfTab(title));
            return;
        }

        TraceManager.addDev("Time value block chart");

        DefaultXYZDataset datasetXYZ = new DefaultXYZDataset();
        double[] xvalues = new double[de.data.length];
        double[] yvalues = new double[de.data.length];
        double[] zvalues = new double[de.data.length];
        double[][] data = new double[][] {xvalues, yvalues, zvalues};

        for(int i=0; i<de.data.length; i++) {
            xvalues[i] = de.times[i];
            yvalues[i] = de.data[i];
            zvalues[i] = 2.5;
        }

        datasetXYZ.addSeries("Series 1", data);

        JFreeChart myChart = createChart(datasetXYZ);

        ChartPanel myChartPanel = new ChartPanel(myChart);
        TraceManager.addDev("Adding XYZChart: " + title);

        XYPlot plot = (XYPlot) myChart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        addChart(title, myChartPanel, true);

    }

    @SuppressWarnings("unchecked")
    public void showValueEvolutionChart(DataElement de) {

        if ((de.setOfValues == null) || (de.setOfValues.size() == 0)) {
            return;
        }


        String title = "Value per Time and per simulation index " + de.toString();

        // Tab already exist?
        if (mainPane.indexOfTab(title) > -1) {
            mainPane.setSelectedIndex(mainPane.indexOfTab(title));
            return;
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        int cpt = 1;
        for(DataElement deEv: de.setOfValues) {
            XYSeries series = new XYSeries("Serie " + cpt);
            //TraceManager.addDev("Handling serie " + cpt);
            for(int i=0; i<deEv.data.length; i++) {
                series.add(deEv.times[i], deEv.data[i]);
                //TraceManager.addDev("ADD Serie " + deEv.times[i] + "," + deEv.data[i]);
            }
            dataset.addSeries(series);
            cpt ++;
        }


        JFreeChart chart = ChartFactory.createXYLineChart(
                title, // Title
                "Time", // x-axis Label
                "Value", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
        );

        ChartPanel myChart = new ChartPanel(chart);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        addChart(title, myChart, true);

    }

    public void addChart(String title, ChartPanel myChart) {
        addChart(title, myChart, false);
    }

    public void addChart(String title, ChartPanel myChart, boolean draggable) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(myChart, BorderLayout.CENTER);
        if (draggable) {
            JLabel label = new JLabel("Use CTRL or ALT + mouse drag to move over the chart");
            panel.add(label, BorderLayout.SOUTH);
        }

        myChart.setMouseWheelEnabled(true);
        mainPane.addTab(title, panel);
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

    @SuppressWarnings("unchecked")
    private static JFreeChart createChart(XYZDataset dataset) {
        NumberAxis xAxis = new NumberAxis("X");
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        NumberAxis yAxis = new NumberAxis("Y");
        yAxis.setAutoRangeIncludesZero(false);
        yAxis.setInverted(true);
        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        XYBlockRenderer renderer = new XYBlockRenderer();
        LookupPaintScale paintScale = new LookupPaintScale(0.5, 3.5,
                Color.black);
        paintScale.add(0.5, Color.green);
        paintScale.add(1.5, Color.orange);
        paintScale.add(2.5, Color.red);
        renderer.setPaintScale(paintScale);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setForegroundAlpha(0.66f);
        plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
        JFreeChart chart = new JFreeChart("XYBlockChartDemo3", plot);
        chart.removeLegend();
        chart.setBackgroundPaint(Color.white);
        SymbolAxis scaleAxis = new SymbolAxis(null, new String[] {"", "No value",
                "Freq < 50%", ">= 50%"});
        scaleAxis.setRange(0.5, 3.5);
        scaleAxis.setPlot(new PiePlot());
        scaleAxis.setGridBandsVisible(false);
        PaintScaleLegend psl = new PaintScaleLegend(paintScale, scaleAxis);
        psl.setAxisOffset(5.0);
        psl.setPosition(RectangleEdge.BOTTOM);
        psl.setMargin(new RectangleInsets(5, 5, 5, 5));
        chart.addSubtitle(psl);
        return chart;
    }


} // Class
