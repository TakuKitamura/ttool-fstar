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

package ui.interactivesimulation;

import myutil.TraceManager;
import ui.ColorManager;
import ui.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;

/**
 * Class JFrameSimulationSDPanel Creation: 26/05/2011 version 1.0 26/05/2011
 * 
 * @author Ludovic APVRILLE
 */
public class JFrameSimulationSDPanel extends JFrame implements ActionListener {

  public InteractiveSimulationActions[] actions;

  private static String[] unitTab = { "sec", "msec", "usec", "nsec" };
  private static int[] clockDivisers = { 1000000000, 1000000, 1000, 1 };
  protected JComboBox<String> units;

  private JSimulationSDPanel sdpanel;
  protected JLabel status;
  // , buttonStart, buttonStopAndClose;
  // protected JTextArea jta;
  // protected JScrollPane jsp;

  public JFrameSimulationSDPanel(Frame _f, MainGUI _mgui, String _title) {
    super(_title);

    initActions();
    makeComponents();
    // setComponents();
    this.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        if (JFrameSimulationSDPanel.this.sdpanel != null)
          JFrameSimulationSDPanel.this.sdpanel.resized();
      }
    });
  }

  private JLabel createStatusBar() {
    status = new JLabel("Ready...");
    status.setForeground(ColorManager.InteractiveSimulationText);
    status.setBorder(BorderFactory.createEtchedBorder());
    return status;
  }

  public void makeComponents() {
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    Container framePanel = getContentPane();
    framePanel.setLayout(new BorderLayout());

    // Top panel
    JPanel topPanel = new JPanel();
    JButton buttonClose = new JButton(actions[InteractiveSimulationActions.ACT_QUIT_SD_WINDOW]);
    topPanel.add(buttonClose);
    topPanel.add(new JLabel(" time unit:"));
    units = new JComboBox<>(unitTab);
    units.setSelectedIndex(1);
    units.addActionListener(this);
    topPanel.add(units);
    JButton buttonRefresh = new JButton(actions[InteractiveSimulationActions.ACT_REFRESH]);
    topPanel.add(buttonRefresh);
    framePanel.add(topPanel, BorderLayout.NORTH);

    // Simulation panel
    sdpanel = new JSimulationSDPanel(this);
    JScrollPane jsp = new JScrollPane(sdpanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    sdpanel.setMyScrollPanel(jsp);
    jsp.setWheelScrollingEnabled(true);
    jsp.getVerticalScrollBar().setUnitIncrement(MainGUI.INCREMENT);
    framePanel.add(jsp, BorderLayout.CENTER);

    // statusBar
    status = createStatusBar();
    framePanel.add(status, BorderLayout.SOUTH);

    // Mouse handler
    // mouseHandler = new MouseHandler(status);

    pack();

    //
    //
  }

  private void initActions() {
    actions = new InteractiveSimulationActions[InteractiveSimulationActions.NB_ACTION];
    for (int i = 0; i < InteractiveSimulationActions.NB_ACTION; i++) {
      actions[i] = new InteractiveSimulationActions(i);
      actions[i].addActionListener(this);
      // actions[i].addKeyListener(this);
    }
  }

  public void close() {
    dispose();
    setVisible(false);

  }

  private void refresh() {
    if (sdpanel != null) {
      sdpanel.refresh();
    }
  }

  public void actionPerformed(ActionEvent evt) {
    String command = evt.getActionCommand();
    // TraceManager.addDev("Command:" + command);

    if (command.equals(actions[InteractiveSimulationActions.ACT_QUIT_SD_WINDOW].getActionCommand())) {
      close();
    } else if (command.equals(actions[InteractiveSimulationActions.ACT_REFRESH].getActionCommand())) {
      refresh();
    } else if (evt.getSource() == units) {
      if (sdpanel != null) {
        switch (units.getSelectedIndex()) {
          case 0:

        }
        sdpanel.setClockDiviser(clockDivisers[units.getSelectedIndex()]);
      }
    }
  }

  public void setFileReference(String _fileReference) {
    if (sdpanel != null) {
      // TraceManager.addDev("Setting file:" + _fileReference);
      sdpanel.setFileReference(_fileReference);
    } else {
      // TraceManager.addDev("Null SD Panel");
    }
  }

  public void setFileReference(BufferedReader inputStream) {
    if (sdpanel != null) {
      // TraceManager.addDev("Setting input stream");
      sdpanel.setFileReference(inputStream);
    } else {
      // TraceManager.addDev("Null SD Panel");
    }
  }

  public void setCurrentTime(long timeValue) {
    status.setText("time = " + timeValue);
  }

  public void setStatus(String _status) {
    status.setText(_status);
  }

  public void setNbOfTransactions(int x, long minTime, long maxTime) {
    status.setText("" + x + " transactions, min time=" + minTime + ", max time=" + maxTime);
  }

  public void setLimitEntity(boolean limit) {
    sdpanel.setLimitEntity(limit);
  }

} // Class
