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
import ui.MainGUI;
import graph.AUTGraph;
import graph.RG;
import ui.util.IconManager;

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
 * Class JFrameMinimize
 * Frame for handling the minimization of graphs
 * Creation: 09/12/2016
 *
 * @author Ludovic APVRILLE
 * @version 1.0 09/12/2016
 */
public class JFrameMinimize extends javax.swing.JFrame implements ActionListener, ListSelectionListener, Runnable {
    /*private static boolean isAldebaranSelected = false;
    private static boolean isOminSelected = false;
    private static boolean isStrongSelected = true;*/

    protected static boolean graphSelected = true;

    private MainGUI mgui;
    private RG rg;
    private RG newRG;

    protected Thread t;
    protected boolean listOfActionsComputed = false;
    java.util.List<String> sortedListProjected;
    java.util.List<String> sortedListIgnored;

    private int mode;

    protected final static int NO_DATA = 0;
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    //subpanels
    private JPanel panel1, panel2, panel3, panel4;
    private JList<String> listIgnored;
    private JList<String> listProjected;
    private JButton allProjected;
    private JButton addOneProjected;
    private JButton addOneIgnored;
    private JButton allIgnored;
    protected JTextArea jta;

    // Panel for graph saving
    private JCheckBox saveGraphAUT;
    private JTextField graphPath;
    private String graphDir;

    private JCheckBox removeInternalActions;
    private JRadioButton tauOnly;
    private JRadioButton allMinimization;


    // Main Panel
    private JButton start, stop, close;

    /*
     * Creates new form
     */
    public JFrameMinimize(Frame _f, MainGUI _mgui, String _title, RG _rg, String _graphDir) {
        super(_title);

        mgui = _mgui;
        rg = _rg;

        if (graphDir == null) {
            graphDir = _graphDir + File.separator + "minimized$.aut";
        }

        initComponents();
        myInitComponents();
        pack();

        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }


    private void myInitComponents() {
        mode = NO_DATA;
        setButtons();
        t = new Thread(this);
        t.start();
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagLayout gridbag4 = new GridBagLayout();
        GridBagConstraints c4 = new GridBagConstraints();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ignored list

        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BorderLayout());

        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.setBorder(new javax.swing.border.TitledBorder("Actions ignored"));
        listIgnored = new JList<String>();
        //listIgnored.setPreferredSize(new Dimension(200, 250));
        listIgnored.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listIgnored.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listIgnored);
        panel1.add(scrollPane1, BorderLayout.CENTER);
        panel1.setPreferredSize(new Dimension(300, 250));
        panelTop.add(panel1, BorderLayout.WEST);

        // validated list
        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.setBorder(new javax.swing.border.TitledBorder("Actions taken into account"));
        listProjected = new JList<String>();
        //listProjected.setPreferredSize(new Dimension(200, 250));
        listProjected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listProjected.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(listProjected);
        panel2.add(scrollPane2, BorderLayout.CENTER);
        panel2.setPreferredSize(new Dimension(300, 250));
        panelTop.add(panel2, BorderLayout.EAST);

        // radio buttons
        panel4 = new JPanel();
        panel4.setLayout(gridbag4);
        panel4.setBorder(new javax.swing.border.TitledBorder("Minimization: tools and options"));


        saveGraphAUT = new JCheckBox("Save the minimized graph in AUT format", graphSelected);
        //saveGraphAUT.addSelectionListener(this);
        graphPath = new JTextField(graphDir);

        removeInternalActions = new JCheckBox("Remove internal actions");
        removeInternalActions.setEnabled(true);
        tauOnly = new JRadioButton("Only remove tau transitions");
        tauOnly.setEnabled(true);
        allMinimization = new JRadioButton("Complete minimization [Experimental]");
        allMinimization.setEnabled(true);
        ButtonGroup bt = new ButtonGroup();
        bt.add(tauOnly);
        bt.add(allMinimization);
        allMinimization.setSelected(true);

        //c4.anchor = GridBagConstraints.EAST;
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.HORIZONTAL;
        c4.gridheight = 1;
        panel4.add(saveGraphAUT, c4);
        panel4.add(graphPath, c4);
        panel4.add(allMinimization, c4);
        panel4.add(removeInternalActions, c4);
        panel4.add(tauOnly, c4);
        panel4.add(allMinimization, c4);

        panelTop.add(panel4, BorderLayout.SOUTH);


        // central buttons
        panel3 = new JPanel();
        panel3.setLayout(gridbag1);

        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridheight = 1;

        allProjected = new JButton(IconManager.imgic50);
        allProjected.setPreferredSize(new Dimension(50, 25));
        allProjected.addActionListener(this);
        allProjected.setActionCommand("All");
        panel3.add(allProjected, c1);

        addOneProjected = new JButton(IconManager.imgic48);
        addOneProjected.setPreferredSize(new Dimension(50, 25));
        addOneProjected.addActionListener(this);
        addOneProjected.setActionCommand("Add one");
        panel3.add(addOneProjected, c1);

        panel3.add(new JLabel(" "), c1);

        addOneIgnored = new JButton(IconManager.imgic46);
        addOneIgnored.addActionListener(this);
        addOneIgnored.setPreferredSize(new Dimension(50, 25));
        addOneIgnored.setActionCommand("One Ignored");
        panel3.add(addOneIgnored, c1);

        allIgnored = new JButton(IconManager.imgic44);
        allIgnored.addActionListener(this);
        allIgnored.setPreferredSize(new Dimension(50, 25));
        allIgnored.setActionCommand("All Ignored");
        panel3.add(allIgnored, c1);

        panelTop.add(panel3, BorderLayout.CENTER);

        c.add(panelTop, BorderLayout.NORTH);

        // textarea panel
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select actions and then, click on 'start' to start minimization\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.CENTER);


        // Button panel;

        JPanel southPanel = new JPanel(new BorderLayout());




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

        southPanel.add(jp2, BorderLayout.CENTER);

        c.add(southPanel, BorderLayout.SOUTH);
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
        } else if (evt.getSource() == addOneIgnored) {
            addOneIgnored();
        } else if (evt.getSource() == addOneProjected) {
            addOneProjected();
        } else if (evt.getSource() == allProjected) {
            allProjected();
        } else if (evt.getSource() == allIgnored) {
            allIgnored();
        }
    }


    private void updateListsFromModels() {
        Collections.sort(sortedListProjected);
        Collections.sort(sortedListIgnored);
        String[] strarray = new String[sortedListProjected.size()];
        sortedListProjected.toArray(strarray);
        listProjected.setListData(strarray);
        strarray = new String[sortedListIgnored.size()];
        sortedListIgnored.toArray(strarray);
        listIgnored.setListData(strarray);
        setButtonsList();
    }


    private void addOneIgnored() {
        java.util.List<String> ll = listProjected.getSelectedValuesList();
        for (String o : ll) {
            sortedListProjected.remove(o);
            sortedListIgnored.add(o);
        }
        updateListsFromModels();
        setButtons();
    }

    private void addOneProjected() {
        java.util.List<String> ll = listIgnored.getSelectedValuesList();
        for (String o : ll) {
            sortedListIgnored.remove(o);
            sortedListProjected.add(o);
        }
        updateListsFromModels();
        setButtons();
    }

    private void allProjected() {
        sortedListProjected.addAll(sortedListIgnored);
        sortedListIgnored.clear();
        updateListsFromModels();

        setButtons();
    }

    private void allIgnored() {
        sortedListIgnored.addAll(sortedListProjected);
        sortedListProjected.clear();
        updateListsFromModels();
        setButtons();
    }
    
    /*private void moveSynchronizedGatesAsWell(LinkedList<TClassAndGateDS> toCheck, LinkedList<TClassAndGateDS> toPickup) {
        TClassAndGateDS tcg1;
        MasterGateManager mgm = mgui.gtm.getNewMasterGateManager();
        //Gate g;
        GroupOfGates gog, gog1;
        

        for (TClassAndGateDS tcg: toCheck) {
            gog = mgm.groupOf(tcg.getTClassName(), tcg.getGateName());
            if (gog != null) {
                for(int j=0; j<toPickup.size(); j++) {
                    tcg1 = toPickup.get (j);
                    gog1 = mgm.groupOf(tcg1.getTClassName(), tcg1.getGateName());
                    if (gog1 == gog) {
                        toCheck.add (tcg1);
                        toPickup.remove (j);
                        j--;
                    }
                }
            }
        }
	}*/


    private void setButtons() {
        switch (mode) {
            case NO_DATA:
                listProjected.setEnabled(false);
                listIgnored.setEnabled(false);
                setButtonsList();
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
            case NOT_STARTED:
                listProjected.setEnabled(true);
                listIgnored.setEnabled(true);
                setButtonsList();
                start.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                listProjected.setEnabled(false);
                listIgnored.setEnabled(false);
                unsetButtonsList();
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                break;
            case STOPPED:
            default:
                listProjected.setEnabled(false);
                listIgnored.setEnabled(false);
                unsetButtonsList();
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
        }
    }

    private void unsetButtonsList() {
        addOneProjected.setEnabled(false);
        addOneIgnored.setEnabled(false);
        allProjected.setEnabled(false);
        allIgnored.setEnabled(false);
    }

    private void setButtonsList() {
        int i1 = listIgnored.getSelectedIndex();
        int i2 = listProjected.getSelectedIndex();

        if (i1 == -1) {
            addOneProjected.setEnabled(false);
        } else {
            addOneProjected.setEnabled(true);
        }

        if (i2 == -1) {
            addOneIgnored.setEnabled(false);
        } else {
            addOneIgnored.setEnabled(true);
        }

        if (sortedListIgnored == null) {
            allProjected.setEnabled(false);
        } else {
            if (sortedListIgnored.size() == 0) {
                allProjected.setEnabled(false);
            } else {
                allProjected.setEnabled(true);
            }
        }

        if (sortedListProjected == null) {
            allIgnored.setEnabled(false);
        } else {
            if (sortedListProjected.size() == 0) {
                allIgnored.setEnabled(false);
                //closeButton.setEnabled(false);
                //closeButton.setEnabled(false);
            } else {
                allIgnored.setEnabled(true);
                //closeButton.setEnabled(true);
            }
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        setButtons();
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
    }

    public void startProcess() {
        t = new Thread(this);
        mode = STARTED;
        setButtons();
        t.start();
    }


    public void computeListOfActions() {
        jta.append("Computing list of Actions\n");
        jta.append("\t1. Cloning graph\n");
        if (rg.graph == null) {
            if (rg.data == null) {
                jta.append("ERROR: invalid graph\n");
                return;
            }

            rg.graph = new AUTGraph();
            jta.append("\t\t Buillding graph in memory\n");
            rg.graph.buildGraph(rg.data);
            jta.append("\t\t Finishing graph in memory\n");
            rg.graph.computeStates();
        }
        newRG = new RG("minimized" + rg.name);
        newRG.data = rg.data;
        newRG.graph = rg.graph.cloneMe();
        jta.append("\t2. Making list of actions\n");
        HashSet<String> hs = newRG.graph.getAllActions();
        jta.append("\t3. Sorting actions, and setting graphical lists\n");

        sortedListProjected = new ArrayList<>(hs);
        sortedListIgnored = new ArrayList<>();
        updateListsFromModels();

        jta.append("All done\n");

        jta.append("\nSelect actions and then, click on 'start' to start minimization\n");
        listOfActionsComputed = true;
        mode = NOT_STARTED;
        setButtons();
    }

    public void run() {

        if (!listOfActionsComputed) {
            computeListOfActions();
            return;
        }

        jta.append("\nMinimizing graph...\n");
        String[] strarray = new String[sortedListIgnored.size()];
        sortedListIgnored.toArray(strarray);
        if (removeInternalActions.isSelected()) {
            int toBeRemoved = 0;
            for (String s : sortedListProjected) {
                if (s.startsWith("i(")) {
                    toBeRemoved++;
                }
            }
            if (toBeRemoved > 0) {
                String[] allstr = new String[strarray.length + toBeRemoved];
                for (int i = 0; i < strarray.length; i++) {
                    allstr[i] = strarray[i];
                }
                int index = strarray.length;
                for (String s : sortedListProjected) {
                    if (s.startsWith("i(")) {
                        allstr[index] = s;
                        index++;
                    }

                }
                strarray = allstr;

            }
        }
        newRG.graph = newRG.graph.minimize(strarray, tauOnly.isSelected());

        newRG.nbOfStates = newRG.graph.getNbOfStates();
        newRG.nbOfTransitions = newRG.graph.getTransitions().size();
        mgui.addRG(newRG);

        jta.append("\nGraph minimized: " + newRG.nbOfStates + " states, " + newRG.nbOfTransitions + " transitions\n");

        if (saveGraphAUT.isSelected()) {
            DateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
            Date date = new Date();
            String dateAndTime = dateFormat.format(date);
            String graphAUT = newRG.graph.toAUTStringFormat();
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

        mode = STOPPED;
        setButtons();
    }


}
