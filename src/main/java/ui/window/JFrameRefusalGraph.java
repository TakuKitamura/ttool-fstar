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


package ui.window;

import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.ScrolledJTextArea;
import ui.util.IconManager;
import ui.MainGUI;
import graph.AUTGraph;
import graph.RG;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;


/**
 * Class JFrameRefusalGraph
 * Frame for handling the construction of the refusal graph
 * Creation: 18/08/2017
 *
 * @author Ludovic APVRILLE
 * @version 1.0 18/08/2017
 */
public class JFrameRefusalGraph extends javax.swing.JFrame implements ActionListener, Runnable {
    /*private static boolean isAldebaranSelected = false;
    private static boolean isOminSelected = false;
    private static boolean isStrongSelected = true;*/


    protected static boolean graphSelected = true;

    private MainGUI mgui;
    private RG rg;
    private RG newRG;
    private RG newRGTS;

    protected Thread t;

    private int mode;

    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    //subpanels & components
    private JPanel panel2;
    private JCheckBox saveGraphAUT;
    private JTextField graphPath;
    private String graphDir;

    // Main Panel
    private ScrolledJTextArea jta;
    private JButton start, stop, close;

    /**
     * Creates new form
     */
    public JFrameRefusalGraph(Frame _f, MainGUI _mgui, String _title, RG _rg, String _graphDir) {
        super(_title);

        mgui = _mgui;
        rg = _rg;

        if (graphDir == null) {
            graphDir = _graphDir + File.separator + "testsequences$.aut";
        }

        initComponents();
        myInitComponents();
        pack();

        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }


    private void myInitComponents() {
        mode = NOT_STARTED;
    }

    private void initComponents() {
        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel compPanel = new JPanel(new BorderLayout());
        saveGraphAUT = new JCheckBox("Save the generated sequences in AUT format", graphSelected);
        compPanel.add(saveGraphAUT, BorderLayout.NORTH);
        //saveGraphAUT.addSelectionListener(this);
        graphPath = new JTextField(graphDir);
        compPanel.add(graphPath, BorderLayout.SOUTH);
        c.add(compPanel, BorderLayout.NORTH);


        // textarea panel
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Click on 'start' to generate test sequences\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.CENTER);


        // Button panel;
        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);

        start.setPreferredSize(new Dimension(150, 30));
        stop.setPreferredSize(new Dimension(150, 30));
        close.setPreferredSize(new Dimension(150, 30));

        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);

        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);

        c.add(jp2, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Start")) {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        }
    }


    private void setButtons() {
        switch (mode) {
            case NOT_STARTED:
                start.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(false);
                break;
            case STOPPED:
            default:
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
        }
    }


    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        graphSelected = saveGraphAUT.isSelected();
        dispose();
    }

    public void stopProcess() {
        mode = STOPPED;
        setButtons();
    }

    public void startProcess() {
        t = new Thread(this);
        mode = STARTED;
        setButtons();
        t.start();
    }


    public void run() {

        jta.append("\nBuilding refusal graph...\n");

        newRG = rg.generateRefusalGraph();

        if (newRG != null) {
            newRG.nbOfStates = newRG.graph.getNbOfStates();
            newRG.nbOfTransitions = newRG.graph.getTransitions().size();
            //mgui.addRG(newRG);
            jta.append("\nRefusal Graph: " + newRG.nbOfStates + " states, " + newRG.nbOfTransitions + " transitions\n");
            jta.append("Generating test sequences\n");
            newRGTS = newRG.generateTestSequences();
            newRGTS.nbOfStates = newRGTS.graph.getNbOfStates();
            newRGTS.nbOfTransitions = newRGTS.graph.getTransitions().size();
            mgui.addRG(newRGTS);
            jta.append("\nTest sequences: " + newRGTS.nbOfStates + " states, " + newRGTS.nbOfTransitions + " transitions\n");

            if (saveGraphAUT.isSelected()) {
                DateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
                Date date = new Date();
                String dateAndTime = dateFormat.format(date);
                String graphAUT = newRGTS.graph.toAUTStringFormat();
                String autfile;

                if (graphPath.getText().indexOf("$") != -1) {
                    autfile = Conversion.replaceAllChar(graphPath.getText(), '$', dateAndTime);
                } else {
                    autfile = graphPath.getText();
                }

                try {
                    FileUtils.saveFile(autfile, graphAUT);
                } catch (FileException e) {
                    jta.append("Graph could not be saved in " + autfile + "\n");
                }
                jta.append("Graph saved in " + autfile + "\n");
            }
        } else {
            jta.append("\nCould not build Refusal Graph\n");
        }

        mode = STOPPED;
        setButtons();
    }


}
