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

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import dseengine.DSEConfiguration;
import launcher.LauncherException;
import launcher.RshClient;
import myutil.GraphicLib;
import myutil.ScrolledJTextArea;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import tmltranslator.TMLModeling;
import tmltranslator.dsez3engine.InputInstance;
import tmltranslator.dsez3engine.OptimizationModel;
import tmltranslator.dsez3engine.OptimizationResult;
import tmltranslator.tonetwork.TMAP2Network;
import ui.util.IconManager;
import ui.MainGUI;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;


import com.microsoft.z3.*;
import tmltranslator.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import myutil.TraceManager;


/**
 * Class JDialogNoCManagement
 * Dialog for managing NoCs in DIPLODOCUS diagrams
 * Creation: 05/03/2019
 *
 * @author Ludovic APVRILLE
 * @version 1.0 05/03/2019
 */
public class JDialogNoCManagement extends JDialog implements ActionListener, ListSelectionListener, Runnable {

    protected MainGUI mgui;


    protected final static int NOT_SELECTED = 0;
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    int mode;



    protected JButton start;
    protected JButton stop;
    protected JButton close;

    protected JTextArea outputText;
    protected String output = "";



    private Thread t;
    private boolean go = false;


    protected RshClient rshc;

    private TMLMapping map;
    private InputInstance inputInstance;
    private OptimizationModel optimizationModel;


    /*
     * Creates new form
     */
    public JDialogNoCManagement(Frame f, MainGUI _mgui, String title, TMLMapping map) {
        super(f, title, true);

        mgui = _mgui;

        this.map = map;

        initComponents();
        myInitComponents();

        pack();


        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
        handleStartButton();
    }

    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());



        JPanel jp03 = new JPanel();
        GridBagLayout gridbag03 = new GridBagLayout();
        GridBagConstraints c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        jp03.setBorder(new javax.swing.border.TitledBorder("NoC Management Options"));
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;




        JPanel jp04 = new JPanel();

        GridBagLayout gridbag04 = new GridBagLayout();
        GridBagConstraints c04 = new GridBagConstraints();
        jp04.setLayout(gridbag04);

        c04.weighty = 1.0;
        c04.weightx = 1.0;
        c04.gridwidth = GridBagConstraints.REMAINDER; //end row
        c04.fill = GridBagConstraints.BOTH;
        c04.gridheight = 1;

        //jp04.setBorder(new javax.swing.border.TitledBorder("DSE Output"));
        //jp04.add(new JLabel("Design Space Exploration Output"), c04);


        outputText = new ScrolledJTextArea();
        outputText.setEditable(false);
        outputText.setMargin(new Insets(10, 10, 10, 10));
        outputText.setTabSize(3);
        outputText.append("How to start?" +
                "\n - Simply click on start ^^\n");
        JScrollPane jsp = new JScrollPane(outputText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setPreferredSize(new Dimension(300, 300));
        Font f = new Font("Courrier", Font.BOLD, 12);
        outputText.setFont(f);
        jp04.add(jsp, c04);
        //jp1.add("Results", jp04);

        c.add(jp03, BorderLayout.NORTH);
        c.add(jp04, BorderLayout.CENTER);

        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);


        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(120, 30));


        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);

        JPanel jp2 = new JPanel();

        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);


        c.add(jp2, BorderLayout.SOUTH);


    }



    private void handleStartButton() {
        //TraceManager.addDev("Handle start button");

        /*boolean b = dseButton.isSelected() || dseButtonFromFile.isSelected();
        nbOfMappings.setEnabled(b);
        infoNbOfMappings.setEnabled(b);
        randomMappingBox.setEnabled(b);
        randomMappingNb.setEnabled(b);
        outputTML.setEnabled(b);
        outputGUI.setEnabled(b);*/
        //dseOptions.repaint();

        if (mode != NOT_STARTED && mode != NOT_SELECTED) {
            return;
        }


        setButtons();

    }

    public void valueChanged(ListSelectionEvent e) {
    }


    public void actionPerformed(ActionEvent evt) {

        if (evt.getSource() == start) {
            startProcess();
        } else if (evt.getSource() == stop) {
            stopProcess();
        } else if (evt.getSource() == close) {
            closeDialog();
        }
    }




    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
    }

    public void stopProcess() {
        mode = STOPPED;
        setButtons();
        go = false;

    }


    public void startProcess() {
        t = new Thread(this);
        mode = STARTED;
        setButtons();
        go = true;
        t.start();
    }
    //
    //    private void testGo() throws InterruptedException {
    //        if (go == false) {
    //            throw new InterruptedException("Stopped by user");
    //        }
    //    }

    public void run() {
        //      String cmd;
        //    String list, data;
        //  int cycle = 0;
        output = "";

        //  hasError = false;
        //try {


        TraceManager.addDev("Thread started");
        outputText.append("\nPreparing model\n");

        TMAP2Network  t2n = new TMAP2Network<>(map, 2);
        t2n.removeAllRouterNodes();

        outputText.append("\nAll done\n");

        stopProcess();

    }





    protected void checkMode() {
        mode = NOT_SELECTED;
    }

    protected void setButtons() {
        switch (mode) {
            case NOT_SELECTED:
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                //setCursor(CursoretPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
            case NOT_STARTED:
                start.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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



    public boolean hasToContinue() {
        return (go == true);
    }
    //
    //    public void setError() {
    //        hasError = true;
    //    }
    //
}
