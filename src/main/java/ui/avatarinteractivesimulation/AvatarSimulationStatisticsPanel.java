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
import myutil.DataElement;
import ui.window.JFrameDataElementStatistics;
import myutil.TraceManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import ui.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class AvatarSimulationStatisticsPanel Avatar: panel for performing
 * simulations and displaying statistics Creation: 08/01/2021
 * 
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

  private JButton showStatsOnCurrentSimulation, showStats;

  // Simulation data structures
  private int totalNbOfSimulations;
  private AvatarSimulationRunner sr;
  private AvatarSpecification as; // For one simulation
  private AvatarSpecificationSimulation ass; // For one simulation
  private Thread simuExecutor;

  public AvatarSimulationStatisticsPanel(MainGUI _mgui, AvatarSpecification _as, AvatarSpecificationSimulation _ass) {
    mgui = _mgui;
    as = _as;
    ass = _ass;
    makeComponents();
  }

  public void makeComponents() {

    GridBagLayout gridbag2 = new GridBagLayout();
    GridBagConstraints c2 = new GridBagConstraints();
    setLayout(gridbag2);
    // setBorder(new javax.swing.border.TitledBorder("Managing transactions"));

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
    simulationsDone = new JLabel("-");
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

    showStats = new JButton("Show statistics");
    add(showStats, c2);
    showStats.setEnabled(false);
    showStats.addActionListener(this);

    showStatsOnCurrentSimulation = new JButton("Show stats on current simulation");
    add(showStatsOnCurrentSimulation, c2);
    showStatsOnCurrentSimulation.setEnabled(true);
    showStatsOnCurrentSimulation.addActionListener(this);

  }

  public void actionPerformed(ActionEvent ae) {
    if (ae.getSource() == runSimulations) {
      runSimulations();
    } else if (ae.getSource() == showStatsOnCurrentSimulation) {
      showStatsCurrentSimulation();
    } else if (ae.getSource() == showStats) {
      showStats();
    } else if (ae.getSource() == stopSimulationsButton) {
      stopSimulation();
    }
  }

  private void showStatsCurrentSimulation() {
    if ((as != null) && (ass != null)) {
      sr = new AvatarSimulationRunner(as);
      sr.setAvatarSpecificationSimulation(ass);
      showStats();
    }
  }

  private void showStats() {
    // Extracting and filling data
    Thread stats = new Thread(() -> {
      // Simulation time
      ArrayList<DataElement> elts = new ArrayList<DataElement>();

      DataElement simulationTime = new DataElement("Simulation time");
      simulationTime.data = sr.getSimulationTimes();
      elts.add(simulationTime);

      // Block
      for (AvatarBlock ab : sr.getBlocksOfTransactions()) {
        DataElement deBlock = new DataElement("Block " + ab.getName());
        elts.add(deBlock);

        DataElement attributes = new DataElement("Attributes");
        deBlock.addChild(attributes);
        DataElement states = new DataElement("States");
        deBlock.addChild(states);

        // Time of last transaction
        DataElement de = new DataElement("Time of last transaction of Block " + ab.getName());
        deBlock.addChild(de);
        de.data = sr.getTimesOfLastTransactionOfBlock(ab);

        // Variables values and time
        int cpt = 0;
        for (AvatarAttribute aa : ab.getAttributes()) {
          de = new DataElement(aa.getName() + " (all values)");
          attributes.addChild(de);
          ArrayList<Double> dataAndTimes = sr.getDataTimesOfAttributesOfBlock(ab, aa, cpt);
          de.data = new double[dataAndTimes.size() / 2];
          de.times = new long[dataAndTimes.size() / 2];
          for (int i = 0; i < de.data.length; i++) {
            de.data[i] = dataAndTimes.get(2 * i).doubleValue();
            de.times[i] = dataAndTimes.get(2 * i + 1).longValue();
          }

          de = new DataElement(aa.getName() + " (last values)");
          attributes.addChild(de);
          dataAndTimes = sr.getLastValueAndTimeOfAttributesOfBlock(ab, aa, cpt);
          de.data = new double[dataAndTimes.size() / 2];
          de.times = new long[dataAndTimes.size() / 2];
          for (int i = 0; i < de.data.length; i++) {
            de.data[i] = dataAndTimes.get(2 * i).doubleValue();
            de.times[i] = dataAndTimes.get(2 * i + 1).longValue();
          }

          de = new DataElement(aa.getName() + " (individual evolution)");
          attributes.addChild(de);
          for (int j = 0; j < totalNbOfSimulations; j++) {
            dataAndTimes = sr.getValueAndTimeOfAttributesOfBlockBySimNumber(ab, aa, cpt, j);
            DataElement deEvolution = new DataElement(aa.getName() + " (evolution #" + j + ")");
            deEvolution.data = new double[dataAndTimes.size() / 2];
            deEvolution.times = new long[dataAndTimes.size() / 2];
            for (int i = 0; i < deEvolution.data.length; i++) {
              deEvolution.data[i] = dataAndTimes.get(2 * i).doubleValue();
              deEvolution.times[i] = dataAndTimes.get(2 * i + 1).longValue();
            }
            de.addSetOfValue(deEvolution);
            de.addChild(deEvolution);
          }

          cpt++;
        }

        // States
        for (AvatarStateMachineElement asme : ab.getStateMachine().getListOfElements()) {
          if (asme instanceof AvatarState) {
            DataElement stateOccurrence = new DataElement(asme.getExtendedName());
            HashMap<Long, Integer> map = new HashMap<>();
            // Getting occurences of states for each time
            int simuIndex = 0;
            for (AvatarSpecificationSimulation ass : sr.listOfSimulations) {
              DataElement stateOccurrenceOneSimulation = new DataElement(
                  asme.getExtendedName() + "_simu #" + (simuIndex + 1));
              HashMap<Long, Integer> mapOne = new HashMap<>();
              for (AvatarSimulationTransaction ast : ass.getAllTransactions()) {
                if ((ast.executedElement == asme) || (ast.executedElement.getNext(0) == asme)) {
                  Integer occ = map.get(ast.initialClockValue);
                  if (occ == null) {
                    occ = 1;
                  } else {
                    occ = occ + 1;
                  }
                  map.put(ast.initialClockValue, occ);
                  // TraceManager.addDev("Putting in table " + ast.initialClockValue + " " +
                  // occ.intValue());
                  occ = mapOne.get(ast.initialClockValue);
                  if (occ == null) {
                    occ = 1;
                  } else {
                    occ = occ + 1;
                  }
                  mapOne.put(ast.initialClockValue, occ);

                }
              }

              int size = mapOne.size();
              stateOccurrenceOneSimulation.data = new double[size];
              stateOccurrenceOneSimulation.times = new long[size];
              cpt = 0;
              for (Long l : mapOne.keySet()) {
                stateOccurrenceOneSimulation.times[cpt] = l;
                stateOccurrenceOneSimulation.data[cpt] = mapOne.get(l);
                // TraceManager.addDev("Adding in data element " + l.longValue() + " " +
                // map.get(l).intValue() + " for state " + asme
                // .getExtendedName());
                cpt++;
              }
              stateOccurrence.addChild(stateOccurrenceOneSimulation);
              simuIndex++;
            }
            int size = map.size();
            stateOccurrence.data = new double[size];
            stateOccurrence.times = new long[size];
            cpt = 0;
            for (Long l : map.keySet()) {
              stateOccurrence.times[cpt] = l;
              stateOccurrence.data[cpt] = map.get(l);
              // TraceManager.addDev("Adding in data element " + l.longValue() + " " +
              // map.get(l).intValue() + " for state " + asme
              // .getExtendedName());
              cpt++;
            }

            // de.addSetOfValue(stateOccurrence);
            states.addChild(stateOccurrence);
          }
        }
      }

      // Opening stat window
      JFrameDataElementStatistics stats1 = new JFrameDataElementStatistics("Simulation stats", mgui, elts);
      stats1.setSize(1200, 800);
      stats1.setVisible(true);
    });
    stats.start();
  }

  /*
   * private void showTimeHistogram() {
   * TraceManager.addDev("Show time histogram"); JPanel panel = new JPanel(new
   * BorderLayout());
   * 
   * HistogramDataset dataset = new HistogramDataset(); double []values =
   * sr.getSimulationTimes(); dataset.addSeries("Time value", values, 20);
   * 
   * JFreeChart histogram =
   * ChartFactory.createHistogram("Histogram: Simulation times",
   * "Simulation times", "Frequency", dataset);
   * 
   * ChartPanel myChart = new ChartPanel(histogram);
   * myChart.setMouseWheelEnabled(true); panel.add(myChart,BorderLayout.CENTER);
   * panel.validate(); JFrame frame = new JFrame();
   * frame.setTitle("Simulation times"); frame.setSize(500, 500);
   * frame.setContentPane(panel); frame.setVisible(true);
   * //TraceManager.addDev("Frame is now visible"); }
   */

  private void stopSimulation() {
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
    // TraceManager.addDev("End of stop");
  }

  private void runSimulations() {
    if (mgui.gtm.getAvatarSpecification() == null) {
      return;
    }

    int nbOfSimulations;

    try {
      nbOfSimulations = Integer.parseInt(nbOfSimulationsText.getText());
    } catch (Exception e) {
      return;
    }

    if (nbOfSimulations < 1) {
      return;
    }

    NB_OF_SIMULATIONS = nbOfSimulationsText.getText();

    reinitStats();

    runSimulations.setEnabled(false);
    sr = new AvatarSimulationRunner(mgui.gtm.getAvatarSpecification());

    totalNbOfSimulations = nbOfSimulations;
    totalSimulations.setText("" + nbOfSimulations);

    simuExecutor = new Thread(() -> sr.runXSimulation(nbOfSimulations, this));
    simuExecutor.start();
    stopSimulationsButton.setEnabled(true);

  }

  public void printStats() {
    long minSimTime = sr.getMinSimulationTime();
    minSimulationTime.setText("" + minSimTime);

    double averageSimTime = sr.getAverageSimulationTime();
    averageSimulationTime.setText("" + averageSimTime);

    long maxSimTime = sr.getMaxSimulationTime();
    maxSimulationTime.setText("" + maxSimTime);

    showStatsOnCurrentSimulation.setEnabled(true);
    showStats.setEnabled(true);
  }

  public void reinitStats() {
    minSimulationTime.setText("-");
    averageSimulationTime.setText("-");
    maxSimulationTime.setText("-");
    // showStatsOnCurrentSimulation.setEnabled(false);
    showStats.setEnabled(false);
  }

  @Override
  public void setSimulationDone(int nb) {
    // TraceManager.addDev("Simulation done for n=" + nb);
    simulationsDone.setText("" + (nb + 1));
    if (nb >= (totalNbOfSimulations - 1)) {
      stopSimulationsButton.setEnabled(false);
      runSimulations.setEnabled(true);
      printStats();
    }
  }
}
