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





package ui.avatarinteractivesimulation;

import avatartranslator.*;
import avatartranslator.directsimulation.*;
import myutil.GraphicLib;
import myutil.TraceManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ui.ColorManager;
import ui.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
   * Class AvatarSimulationStatisticsPanel
   * Avatar: panel for performing simulations and displaying statistics
   * Creation: 08/01/2021
   * @version 1.0 08/01/2021
   * @author Ludovic APVRILLE
 */
public class AvatarSimulationStatisticsPanel extends JPanel implements ActionListener, AvatarSimulationRunnerListener {
    private static String NB_OF_SIMULATIONS = "100";

    private MainGUI mgui;

    // Graphical components
    private JButton runSimulations;
    private JTextField nbOfSimulationsText;
    private JButton stopSimulationsButton;


    private JLabel simulationsDone;
    private JLabel totalSimulations;

    private JLabel minSimulationTime;
    private JLabel averageSimulationTime;
    private JLabel maxSimulationTime;

    private JButton showSimulationTimeHistogram;


    // Simulation data structures
    private int totalNbOfSimulations;
    private AvatarSimulationRunner sr;
    private Thread simuExecutor;




    public AvatarSimulationStatisticsPanel(MainGUI _mgui) {
      mgui = _mgui;
      makeComponents();
    }

    public void makeComponents() {


        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
        setLayout(gridbag2);
        //setBorder(new javax.swing.border.TitledBorder("Managing transactions"));

        // Simulations
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridheight = 1;
        c2.gridwidth = 1;
        runSimulations = new JButton("Execute");
        runSimulations.addActionListener(this);
        add(runSimulations, c2);
        nbOfSimulationsText = new JTextField(NB_OF_SIMULATIONS, 10);
        add(nbOfSimulationsText, c2);
        c2.gridwidth = GridBagConstraints.REMAINDER;
        add(new JLabel("simulations"), c2);
        stopSimulationsButton = new JButton("Stop simulation");
        stopSimulationsButton.addActionListener(this);
        stopSimulationsButton.setEnabled(false);
        add(stopSimulationsButton, c2);

        add(new JLabel(""), c2);
        add(new JLabel("Simulation progression"), c2);
        c2.gridwidth = 1;
        simulationsDone = new JLabel ("-");
        totalSimulations = new JLabel("-");
        add(simulationsDone, c2);
        add(new JLabel("/"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER;
        add(totalSimulations, c2);

        // **** Stats ****

        // Simulation time
        add(new JLabel("Simulation time (min, max, average)"), c2);
        c2.gridwidth = 1;
        minSimulationTime = new JLabel("-");
        add(minSimulationTime, c2);
        averageSimulationTime = new JLabel("-");
        add(averageSimulationTime, c2);
        c2.gridwidth = GridBagConstraints.REMAINDER;
        maxSimulationTime = new JLabel("-");
        add(maxSimulationTime, c2);

        showSimulationTimeHistogram = new JButton("Show simulation time stats");
        add(showSimulationTimeHistogram, c2);
        showSimulationTimeHistogram.setEnabled(false);
        showSimulationTimeHistogram.addActionListener(this);


    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == runSimulations) {
            runSimulations();
        } else if (ae.getSource() == showSimulationTimeHistogram) {
            showTimeHistogram();
        } else if (ae.getSource() == stopSimulationsButton) {
            stopSimulation();
        }
    }

    private void showTimeHistogram() {
        TraceManager.addDev("Show time histogram");
        JPanel panel = new JPanel(new BorderLayout());

        /*XYSeries series = new XYSeries("XYGraph");
        series.add(1, 1);
        series.add(1, 2);
        series.add(2, 1);
        series.add(3, 9);
        series.add(4, 10);

// Add the series to your data set
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

// Generate the graph
        JFreeChart chart = ChartFactory.createXYLineChart(
                "XY Chart", // Title
                "x-axis", // x-axis Label
                "y-axis", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
        );*/

        HistogramDataset dataset = new HistogramDataset();
        double []values = sr.getSimulationTimes();
        dataset.addSeries("key", values, 20);

        JFreeChart histogram = ChartFactory.createHistogram("Histogram: Simulation times",
                "Simulation times", "Frequency", dataset);

        ChartPanel myChart = new ChartPanel(histogram);
        myChart.setMouseWheelEnabled(true);
        panel.add(myChart,BorderLayout.CENTER);
        panel.validate();
        JFrame frame = new JFrame();
        frame.setTitle("Simulation times");
        frame.setSize(500, 500);
        frame.setContentPane(panel);
        frame.setVisible(true);
        //TraceManager.addDev("Frame is now visible");
    }

    private void stopSimulation () {
        TraceManager.addDev("Stopping simulation");
        if (sr != null) {
            Thread t = new Thread(() -> sr.stopSimulation());
            t.start();
        }
        stopSimulationsButton.setEnabled(false);
        sr = null;
        simulationsDone.setText("-");
        totalSimulations.setText("-");
        runSimulations.setEnabled(true);
        reinitStats();
        //TraceManager.addDev("End of stop");
    }


    private void runSimulations() {
        if (mgui.gtm.getAvatarSpecification() == null) {
            return;
        }



        int nbOfsimulations;

        try {
            nbOfsimulations = new Integer(nbOfSimulationsText.getText()).intValue();
        } catch (Exception e) {
            return;
        }

        if (nbOfsimulations < 1) {
            return;
        }

        NB_OF_SIMULATIONS = nbOfSimulationsText.getText();

        reinitStats();

        //TraceManager.addDev("Going to start simulations");

        runSimulations.setEnabled(false);
        sr = new AvatarSimulationRunner(mgui.gtm.getAvatarSpecification());

        totalNbOfSimulations = nbOfsimulations;
        totalSimulations.setText(""+nbOfsimulations);

        simuExecutor = new Thread(() -> sr.runXSimulation(nbOfsimulations, this));
        simuExecutor.start();
        stopSimulationsButton.setEnabled(true);

    }

    public void printStats() {
        long minSimTime = sr.getMinSimulationTime();
        minSimulationTime.setText(""+minSimTime);

        double averageSimTime = sr.getAverageSimulationTime();
        averageSimulationTime.setText(""+averageSimTime);

        long maxSimTime = sr.getMaxSimulationTime();
        maxSimulationTime.setText(""+maxSimTime);

        showSimulationTimeHistogram.setEnabled(true);
    }

    public void reinitStats() {
        minSimulationTime.setText("-");
        averageSimulationTime.setText("-");
        maxSimulationTime.setText("-");
        showSimulationTimeHistogram.setEnabled(false);
    }


    @Override
    public void setSimulationDone(int nb) {
        //TraceManager.addDev("Simulation done for n=" + nb);
        simulationsDone.setText(""+(nb+1));
        if (nb >= (totalNbOfSimulations-1)) {
            stopSimulationsButton.setEnabled(false);
            runSimulations.setEnabled(true);
            printStats();
        }
    }
}
