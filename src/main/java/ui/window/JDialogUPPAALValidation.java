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

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.touppaal.AVATAR2UPPAAL;
import common.ConfigurationTTool;
import launcher.LauncherException;
import launcher.RshClient;
import myutil.FileException;
import myutil.FileUtils;
import myutil.ScrolledJTextArea;
import myutil.TraceManager;
import ui.MainGUI;
import ui.TGComponent;
import ui.TGComponentAndUPPAALQuery;
import ui.TURTLEPanel;
import ui.util.IconManager;
import uppaaldesc.UPPAALSpec;
import uppaaldesc.UPPAALTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

/**
 * Class JDialogUPPAALValidation
 * Dialog for managing the syntax analysis of LOTOS specifications
 * Creation: 16/05/2007
 *
 * @author Ludovic APVRILLE
 * @version 1.0 16/05/2007
 */
public class JDialogUPPAALValidation extends javax.swing.JDialog implements ActionListener, Runnable {

    // Issue #35: Handle the different output message labels of different versions of UPPAAL
    private static final java.util.Set<String> PROP_VERIFIED_LABELS = new HashSet<String>();
    private static final java.util.Set<String> PROP_NOT_VERIFIED_LABELS = new HashSet<String>();

    private static final String UPPAAL_INSTALLATION_ERROR ="The verifier of UPPAAL could not be started.\nProbably, " +
            "UPPAAL is badly installed, or TTool is badly configured:\nCheck " +
            "for UPPAALVerifierPath and UPPAALVerifierHost configurations.";

    static {
        for (final String label : ConfigurationTTool.UPPAALPropertyVerifMessage.split(",")) {
            if (!label.trim().isEmpty()) {
                PROP_VERIFIED_LABELS.add(label.trim());
            }
        }

        // Handle the case where nothing is defined in the configuration
        if (PROP_VERIFIED_LABELS.isEmpty()) {
            PROP_VERIFIED_LABELS.add("Property is satisfied");
            PROP_VERIFIED_LABELS.add("Formula is satisfied");
        }

        for (final String label : ConfigurationTTool.UPPAALPropertyNotVerifMessage.split(",")) {
            if (!label.trim().isEmpty()) {
                PROP_NOT_VERIFIED_LABELS.add(label.trim());
            }
        }

        // Handle the case where nothing is defined in the configuration
        if (PROP_NOT_VERIFIED_LABELS.isEmpty()) {
            PROP_NOT_VERIFIED_LABELS.add("Property is NOT satisfied");
            PROP_NOT_VERIFIED_LABELS.add("Formula is NOT satisfied");
        }
    }

    private static boolean deadlockAChecked/*, deadlockEChecked*/, generateTraceChecked, customChecked, stateR_NoneChecked, stateR_SelectedChecked, stateR_AllChecked, stateL_NoneChecked, stateL_SelectedChecked, stateL_AllChecked, stateLe_NoneChecked, stateLe_SelectedChecked, showDetailsChecked;//, translateChecked;

    protected MainGUI mgui;

    protected String cmdVerifyta;
    protected String fileName;
    protected String pathTrace;
    protected String spec;
    protected String host;
    protected int mode;
    protected RshClient rshc;
    protected Thread t;

    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    //components

    protected JTextArea jta;

    protected JButton start;
    protected JButton stop;
    protected JButton close;
    protected JButton eraseAll;
    protected JButton checkUncheckAllPragmas;

    protected JCheckBox deadlockE, deadlockA, generateTrace, custom, showDetails;
    protected JRadioButton stateR_None, stateR_Selected, stateR_All;
    protected ButtonGroup reachabilities;
    protected JRadioButton stateL_None, stateL_Selected, stateL_All;
    protected ButtonGroup liveness;
    protected JRadioButton stateLe_None, stateLe_Selected;
    protected ButtonGroup leadsto;
    protected JTextField customText;
    protected JTextField translatedText;
    protected TURTLEPanel tp;
    protected java.util.List<JCheckBox> customChecks;
    protected boolean hasFiniteSize;
    //    protected static String sizeInfiniteFIFO = "8";
    protected JTextField sizeOfInfiniteFIFO;

    protected java.util.List<String> customQueries;
    public Map<String, Integer> verifMap;
    protected int status = -1;
    private boolean expectedResult;

    private  AvatarSpecification aspec;

    /*
     * Creates new form
     */
    public JDialogUPPAALValidation(Frame f, MainGUI _mgui, String title, String _cmdVerifyta, String _pathTrace, String _fileName, String _spec,
                                   String _host, TURTLEPanel _tp) {
        super(f, title, true);

        mgui = _mgui;

        cmdVerifyta = _cmdVerifyta;
        fileName = _fileName;
        pathTrace = _pathTrace;
        spec = _spec;
        host = _host;
        tp = _tp;
        aspec = mgui.gtm.getAvatarSpecification();
        if (aspec != null) {
            customQueries = aspec.getSafetyPragmas();
        }

        //TraceManager.addDev("Panel in UPPAAL Validation: " + mgui.getTabName(tp));
        customChecks = new LinkedList<JCheckBox>();
        initComponents();
        myInitComponents();
        verifMap = new HashMap<String, Integer>();
        pack();

        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }


    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
    }

    protected void initComponents() {
        int index = spec.indexOf("DEFAULT_INFINITE_SIZE");
        String size = "1024";
        hasFiniteSize = (index > -1);
        if (hasFiniteSize) {
            String subspec = spec.substring(index + 24, spec.length());
            int indexEnd = subspec.indexOf(";");
            //TraceManager.addDev("indexEnd = " + indexEnd + " subspec=" + subspec);
            if (indexEnd == -1) {
                hasFiniteSize = false;
            } else {
                size = subspec.substring(0, indexEnd);
                TraceManager.addDev("size=" + size);
            }
        }

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel jp1 = new JPanel();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        jp1.setLayout(gridbag1);
        jp1.setBorder(new javax.swing.border.TitledBorder("Verify with UPPAAL: options"));
        //jp1.setPreferredSize(new Dimension(300, 150));

        // first line panel1
        //c1.gridwidth = 3;
        c1.gridheight = 3;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;

        /*deadlockE = new JCheckBox("Search for absence of deadock situations");
          deadlockE.addActionListener(this);
          jp1.add(deadlockE, c1);
          deadlockE.setSelected(deadlockEChecked);*/

        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Options of UPPAAL Specification"));


        // first line panel01
        //c1.gridwidth = 3;

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;


        sizeOfInfiniteFIFO = new JTextField(size, 10);
        c01.gridwidth = 1;
        jp01.add(new JLabel("Size of \"infinite FIFO\":"), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp01.add(sizeOfInfiniteFIFO, c01);

        generateTrace = new JCheckBox("Generate simulation trace");
        generateTrace.addActionListener(this);
        jp01.add(generateTrace, c01);
        generateTrace.setSelected(generateTraceChecked);

        showDetails = new JCheckBox("Show verification details");
        showDetails.addActionListener(this);
        jp01.add(showDetails, c01);
        showDetails.setSelected(showDetailsChecked);

        jp1.add(jp01, c1);

        c1.gridheight = 1;

        JPanel jpbasic = new JPanel();
        GridBagLayout gridbagbasic = new GridBagLayout();
        GridBagConstraints cbasic = new GridBagConstraints();
        cbasic.anchor = GridBagConstraints.WEST;
        cbasic.gridheight = 1;
        cbasic.weighty = 1.0;
        cbasic.weightx = 1.0;
        jpbasic.setLayout(gridbagbasic);
        jpbasic.setBorder(new javax.swing.border.TitledBorder("Basic properties"));
        cbasic.fill = GridBagConstraints.BOTH;

        cbasic.gridwidth = GridBagConstraints.REMAINDER; //end row
        deadlockA = new JCheckBox("No deadlocks?");
        deadlockA.addActionListener(this);
        jpbasic.add(deadlockA, cbasic);
        deadlockA.setSelected(deadlockAChecked);


        // Reachability
        cbasic.gridwidth = 1;
        jpbasic.add(new JLabel("Reachability:"), cbasic);
        stateR_None = new JRadioButton("None");
        stateR_None.addActionListener(this);
        stateR_None.setToolTipText("Won't study reachability properties");
        jpbasic.add(stateR_None, cbasic);

        stateR_Selected = new JRadioButton("Selected");
        stateR_Selected.addActionListener(this);
        stateR_Selected.setToolTipText("Study the fact that selected states may be reachable i.e. in at least one path");
        jpbasic.add(stateR_Selected, cbasic);

        cbasic.gridwidth = GridBagConstraints.REMAINDER; //end row
        stateR_All = new JRadioButton("All");
        stateR_All.addActionListener(this);
        stateR_All.setToolTipText("Study the fact that all states may be reachable i.e. in at least one path");
        jpbasic.add(stateR_All, cbasic);


        // Making the button group.
        // then selecting the button according to the previous user selection
        reachabilities = new ButtonGroup();
        reachabilities.add(stateR_None);
        reachabilities.add(stateR_Selected);
        reachabilities.add(stateR_All);
        stateR_Selected.setSelected(stateR_NoneChecked);
        stateR_Selected.setSelected(stateR_SelectedChecked);
        stateR_All.setSelected(stateR_AllChecked);

        // Liveness
        cbasic.gridwidth = 1;
        jpbasic.add(new JLabel("Liveness:"), cbasic);
        stateL_None = new JRadioButton("None");
        stateL_None.addActionListener(this);
        stateL_None.setToolTipText("Won't study liveness properties");
        jpbasic.add(stateL_None, cbasic);

        stateL_Selected = new JRadioButton("Selected");
        stateL_Selected.addActionListener(this);
        stateL_Selected.setToolTipText("Study the fact that selected states are always reachable i.e. in all possible paths");
        jpbasic.add(stateL_Selected, cbasic);

        cbasic.gridwidth = GridBagConstraints.REMAINDER; //end row
        stateL_All = new JRadioButton("All");
        stateL_All.addActionListener(this);
        stateL_All.setToolTipText("Study the fact that selected states are always reachable i.e. in all possible paths");
        jpbasic.add(stateL_All, cbasic);


        // Making the button group.
        // then selecting the button according to the previous user selection
        liveness = new ButtonGroup();
        liveness.add(stateL_None);
        liveness.add(stateL_Selected);
        liveness.add(stateL_All);
        stateL_None.setSelected(stateL_NoneChecked);
        stateL_Selected.setSelected(stateL_SelectedChecked);
        stateL_All.setSelected(stateL_AllChecked);

        // LeadsTo
        cbasic.gridwidth = 1;
        jpbasic.add(new JLabel("Leads to:"), cbasic);
        stateLe_None = new JRadioButton("None");
        stateLe_None.addActionListener(this);
        stateLe_None.setToolTipText("No leads to properties");
        jpbasic.add(stateLe_None, cbasic);

        cbasic.gridwidth = GridBagConstraints.REMAINDER; //end row
        stateLe_Selected = new JRadioButton("Selected");
        stateLe_Selected.addActionListener(this);
        stateLe_Selected.setToolTipText("Study the fact that selected states lead to one another");
        jpbasic.add(stateLe_Selected, cbasic);

        // Making the button group.
        // then selecting the button according to the previous user selection
        leadsto = new ButtonGroup();
        leadsto.add(stateLe_None);
        leadsto.add(stateLe_Selected);
        stateLe_None.setSelected(stateLe_NoneChecked);
        stateLe_Selected.setSelected(stateLe_SelectedChecked);

        c1.anchor = GridBagConstraints.WEST;
        c1.fill = GridBagConstraints.BOTH;
        jp1.add(jpbasic, c1);

        /*stateA = new JCheckBox("Liveness of selected states");
          stateA.addActionListener(this);
          stateA.setToolTipText("Study the fact that a given state is always reachable i.e. in all paths");
          jp1.add(stateA, c1);
          stateA.setSelected(stateAChecked);*/

        /*stateL = new JCheckBox("Leads to");
        stateL.addActionListener(this);
        stateL.setToolTipText("Study the fact that, if accessed,  a given state is eventually followed by another one");
        jp1.add(stateL, c1);
        stateL.setSelected(stateLChecked);*/


        JPanel jpadvanced = new JPanel();
        GridBagLayout gridbagadvanced = new GridBagLayout();
        GridBagConstraints cadvanced = new GridBagConstraints();
        cadvanced.anchor = GridBagConstraints.WEST;
        cadvanced.gridheight = 1;
        cadvanced.weighty = 1.0;
        cadvanced.weightx = 1.0;
        jpadvanced.setLayout(gridbagadvanced);
        jpadvanced.setBorder(new javax.swing.border.TitledBorder("Advanced properties"));
        cadvanced.fill = GridBagConstraints.BOTH;

        cadvanced.gridwidth = 1; //GridBagConstraints.REMAINDER;
        custom = new JCheckBox("Safety pragmas");
        custom.addActionListener(this);
        if ((customQueries != null) && (customQueries.size() > 0)) {
            jpadvanced.add(custom, cadvanced);
            custom.setSelected(customChecked);
        }

        cadvanced.gridwidth = GridBagConstraints.REMAINDER;
        checkUncheckAllPragmas = new JButton("Check / uncheck all");
        checkUncheckAllPragmas.addActionListener(this);

        if ((customQueries != null) && (customQueries.size() > 0)) {
            cadvanced.fill = GridBagConstraints.VERTICAL;
            jpadvanced.add(checkUncheckAllPragmas, cadvanced);
        }




        //jp1.add(custom, c1);
        //custom.setSelected(customChecked);
        if (customQueries != null) {
            JPanel jpadvancedQ = new JPanel();
            GridBagConstraints cadvancedQ = new GridBagConstraints();
            GridBagLayout gridbagadvancedQ = new GridBagLayout();
            cadvancedQ.anchor = GridBagConstraints.WEST;
            cadvancedQ.gridheight = 1;
            cadvancedQ.weighty = 1.0;
            cadvancedQ.weightx = 1.0;
            jpadvancedQ.setLayout(gridbagadvancedQ);
            cadvancedQ.fill = GridBagConstraints.BOTH;


            for (String s : customQueries) {
                cadvancedQ.gridwidth = GridBagConstraints.RELATIVE;
                JLabel space = new JLabel("   ");
                cadvancedQ.weightx = 0.0;
                jpadvancedQ.add(space, cadvancedQ);
                cadvancedQ.gridwidth = GridBagConstraints.REMAINDER; //end row
                JCheckBox cqb = new JCheckBox(s);
                cqb.addActionListener(this);
                cadvancedQ.weightx = 1.0;
                jpadvancedQ.add(cqb, cadvancedQ);
                customChecks.add(cqb);

            }
            JScrollPane jsp = new JScrollPane(jpadvancedQ, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            jsp.setPreferredSize(new Dimension(500, 150));
            cadvanced.gridheight = 10;
            jpadvanced.add(jsp, cadvanced);
        }
        jp1.add(jpadvanced, c1);
        /*  jp1.add(new JLabel("Custom formula to translate = "), c1);
            c1.gridwidth = GridBagConstraints.REMAINDER; //end row
            customText = new JTextField("Type your CTL formulae here!", 80);
            customText.addActionListener(this);
            jp1.add(customText, c1);

            c1.gridwidth = 1;
            translateCustom = new JCheckBox("Use translated custom verification");
            translateCustom.addActionListener(this);
            jp1.add(translateCustom, c1);
            custom.setSelected(translateChecked);
            c1.gridwidth = GridBagConstraints.REMAINDER; //end row
            translatedText = new JTextField("Translated CTL formula here", 80);
            customText.addActionListener(this);
            jp1.add(translatedText,c1);
        */


        jp1.setMinimumSize(jp1.getPreferredSize());
        c.add(jp1, BorderLayout.NORTH);

        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to start generation of RG\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.CENTER);

        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);
        eraseAll = new JButton("Del", IconManager.imgic337);

        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(110, 30));
        eraseAll.setPreferredSize(new Dimension(100, 30));

        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);
        eraseAll.addActionListener(this);

        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);
        jp2.add(eraseAll);
        c.add(jp2, BorderLayout.SOUTH);


    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == eraseAll) {
            eraseTextArea();
        } else if (evt.getSource() == checkUncheckAllPragmas) {
            checkUncheckAllPragmas();
        } else if (command.equals("Start")) {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        } else {
            setButtons();
        }

    }

    public void eraseTextArea() {
        jta.setText("");
    }

    private void checkUncheckAllPragmas() {
        if (customChecks != null) {
            int nb = 0;
            for(JCheckBox cb: customChecks) {
                nb = cb.isSelected() ? nb + 1 : nb ;
            }
            boolean check = (nb * 2) < customChecks.size();
            for(JCheckBox cb: customChecks) {
                cb.setSelected(check);
            }
            setButtons();
        }
    }


    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        //deadlockEChecked = deadlockE.isSelected();
        deadlockAChecked = deadlockA.isSelected();
        stateR_NoneChecked = stateR_None.isSelected();
        stateR_SelectedChecked = stateR_Selected.isSelected();
        stateR_AllChecked = stateR_All.isSelected();
        stateL_NoneChecked = stateL_None.isSelected();
        stateL_SelectedChecked = stateL_Selected.isSelected();
        stateL_AllChecked = stateL_All.isSelected();
        //stateAChecked = stateA.isSelected();
        stateLe_NoneChecked = stateLe_None.isSelected();
        stateLe_SelectedChecked = stateLe_Selected.isSelected();
        //stateLChecked = stateL_Selected.isSelected();
        customChecked = custom.isSelected();
        generateTraceChecked = generateTrace.isSelected();
        showDetailsChecked = showDetails.isSelected();
        dispose();
    }

    public void stopProcess() {
        try {
            rshc.stopCommand();
        } catch (LauncherException le) {
        }

        mode = NOT_STARTED;
        setButtons();
    }

    public void startProcess() {
        // hack spec if necessary.

        if (hasFiniteSize) {
            try {
                int sizeDef = Integer.decode(sizeOfInfiniteFIFO.getText()).intValue();
                int index = spec.indexOf("DEFAULT_INFINITE_SIZE");
                String specEnd = spec.substring(index + 24, spec.length());
                String specbeg = spec.substring(0, index + 24);
                specbeg += sizeDef;
                specEnd = specEnd.substring(specEnd.indexOf(";"), specEnd.length());
                spec = specbeg + specEnd;
            } catch (Exception e) {
                jta.append("Non valid size for infinite FIFO");
                jta.append("Using default size");
            }

            //TraceManager.addDev("spec=" + spec);
        }

        t = new Thread(this);
        mode = STARTED;
        setButtons();
        t.start();
    }

    @Override
    public void run() {
        long timeBeg = 0;
        long timeSendSpecBeg = 0;

        //  String cmd1 = "";
        // String data1;
        int id = 0;
        String query;
        String name;
        int trace_id = 0;
        int index;
        String fn;
        int result;

        timeSendSpecBeg = System.currentTimeMillis();
        rshc = new RshClient(host);
        RshClient rshctmp = rshc;
        
        expectedResult = true;


        try {
            // checking UPPAAL installation
            File uppaalVerifier= new File(cmdVerifyta);
            if (!uppaalVerifier.exists()) {
                jta.append(UPPAAL_INSTALLATION_ERROR);

                mode = NOT_STARTED;
                setButtons();

                return;
            }


            id = rshc.getId();
            //jta.append("Session id on launcher=" + id + "\n");

            fn = fileName.substring(0, fileName.length() - 4) + "_" + id;

            jta.append("Sending UPPAAL specification data\n");
            rshc.sendFileData(fn + ".xml", spec);

            /*if (deadlockE.isSelected()) {
              jta.append("Searching for absence of deadlock situations\n");
              workQuery("A[] not deadlock", fileName, trace_id, rshc);
              trace_id++;
              }*/

            timeBeg = System.currentTimeMillis();

            if (deadlockA.isSelected() && (mode != NOT_STARTED)) {
                jta.append("\n\n--------------------------------------------\n");
                jta.append("No deadlocks?\n");
                workQuery("A[] not deadlock", fn, trace_id, rshc);
                trace_id++;
            }

            if ((stateR_Selected.isSelected() || stateR_All.isSelected()) && (mode != NOT_STARTED)) {
                jta.append("\nNow working on Reachabilities\n");
                mgui.resetReachability();
                java.util.List<TGComponentAndUPPAALQuery> list = mgui.gtm.getUPPAALQueries(tp, stateR_All.isSelected());
                TraceManager.addDev("List of queries size: " + list.size());

                if ((list != null) && (list.size() > 0)) {
                    for (TGComponentAndUPPAALQuery cq : list) {
                        String s = cq.uppaalQuery;
                        index = s.indexOf('$');
                        if (cq.tgc != null) {
                            cq.tgc.setReachability(TGComponent.ACCESSIBILITY_UNKNOWN);
                        }
                        if ((index != -1) && (mode != NOT_STARTED)) {
                            name = s.substring(index + 1, s.length());
                            //TraceManager.addDev("****\n name=" + name + " list=" + list + "\n****\n");
                            query = s.substring(0, index);
                            //jta.append("\n\n--------------------------------------------\n");
                            jta.append("\nReachability of: " + name + "\n");
                            result = workQuery("E<> " + query, fn, trace_id, rshc);
                            if (cq.tgc != null) {
                                if (result == 0) {
                                    cq.tgc.setReachability(TGComponent.ACCESSIBILITY_KO);
                                    cq.tgc.setLiveness(TGComponent.ACCESSIBILITY_KO);
                                } else if (result == 1) {
                                    cq.tgc.setReachability(TGComponent.ACCESSIBILITY_OK);
                                }
                            }
                            trace_id++;
                        } else {
                            if (mode == NOT_STARTED) {
                                //jta.append("Process stopped\n");
                                break;
                            } else {
                                jta.append("A property could not be studied (internal error)\n");
                            }
                        }
                    }
                } else {
                    jta.append("Accessibility: No selected component found on diagrams\n");
                }
            }

            if ((stateL_Selected.isSelected() || stateL_All.isSelected()) && (mode != NOT_STARTED)) {
                mgui.resetLiveness();
                java.util.List<TGComponentAndUPPAALQuery> list = mgui.gtm.getUPPAALQueries(tp, stateL_All.isSelected());
                if ((list != null) && (list.size() > 0)) {
                    for (TGComponentAndUPPAALQuery cq : list) {
                        if (cq.tgc != null) {
                            cq.tgc.setLiveness(TGComponent.ACCESSIBILITY_UNKNOWN);
                        }
                        String s = cq.uppaalQuery;
                        index = s.indexOf('$');
                        if ((index != -1) && (mode != NOT_STARTED)) {
                            name = s.substring(index + 1, s.length());
                            query = s.substring(0, index);
                            //jta.append("\n--------------------------------------------\n");
                            jta.append("\nLiveness of: " + name + "\n");
                            result = workQuery("A<> " + query, fn, trace_id, rshc);
                            if (cq.tgc != null) {
                                if (result == 0) {
                                    cq.tgc.setLiveness(TGComponent.ACCESSIBILITY_KO);
                                } else if (result == 1) {
                                    cq.tgc.setLiveness(TGComponent.ACCESSIBILITY_OK);
                                }
                            }
                            trace_id++;
                        } else {
                            if (mode == NOT_STARTED) {
                                //jta.append("Process stopped\n");
                                break;
                            } else {
                                jta.append("A property could not be studied (internal error)\n");
                            }
                        }
                    }
                } else {
                    jta.append("Liveness: No selected component found on diagrams\n\n");
                }
            }

            if (stateLe_Selected.isSelected() && (mode != NOT_STARTED)) {
                java.util.List<TGComponentAndUPPAALQuery> list = mgui.gtm.getUPPAALQueries(tp);
                String s1, s2, name1, name2, query1, query2;
                int index1, index2;
                if ((list != null) && (list.size() > 0)) {
                    for (int i = 0; i < list.size() - 1; i++) {
                        for (int j = i + 1; j < list.size(); j++) {
                            s1 = list.get(i).uppaalQuery;
                            s2 = list.get(j).uppaalQuery;
                            index1 = s1.indexOf('$');
                            index2 = s2.indexOf('$');
                            //TraceManager.addDev("\n******\n\n\n");
                            //TraceManager.addDev("s1=" + s1 + "\ns2=" + s2);
                            if ((index1 != -1) && (index2 != -1) && (mode != NOT_STARTED)) {
                                name1 = s1.substring(index1 + 1, s1.length());
                                query1 = s1.substring(0, index1);
                                name2 = s2.substring(index2 + 1, s2.length());
                                query2 = s2.substring(0, index2);
                                //TraceManager.addDev("name1=" + name1 + "\nname2=" + name2);
                                //TraceManager.addDev("query1=" + s1 + "\nquery2=" + s2);
                                if ((name1.compareTo(name2) != 0) && (name1.length() > 0) && (name2.length() > 0)) {
                                    if (!(showDetails.isSelected())) {
                                        int indexName = name1.indexOf(":");
                                        if (indexName != -1) {
                                            name1 = name1.substring(indexName + 1, name1.length()).trim();
                                        }
                                        indexName = name2.indexOf(":");
                                        if (indexName != -1) {
                                            name2 = name2.substring(indexName + 1, name2.length()).trim();
                                        }
                                    }
                                    jta.append("\nLeads to: " + name1 + "--> " + name2 + "\n");
                                    workQuery(query1 + " --> " + query2, fn, trace_id, rshc);
                                    trace_id++;
                                    jta.append("\nLeads to: " + name2 + "--> " + name1 + "\n");
                                    workQuery(query2 + " --> " + query1, fn, trace_id, rshc);
                                    trace_id++;
                                }
                            } else {
                                if (mode == NOT_STARTED) {
                                    //jta.append("Process stopped\n");
                                    break;
                                } else {
                                    jta.append("A property could not be studied (internal error)\n");
                                }
                            }
                        }
                    }
                } else {
                    jta.append("Liveness: No selected component found on diagrams\n\n");
                }
            }

            if (custom.isSelected() && (mode != NOT_STARTED)) {
                jta.append("\n\n--------------------------------------------\n");

                jta.append("Studying selected safety pragmas\n");
                for(int i=0; i< customChecks.size(); i++) {
                    JCheckBox j = customChecks.get(i);
                    if (j.isSelected()) {
                        jta.append(j.getText() + "\n");
                        String translation = checkExpectedResult(j.getText()); 
                        translation = translateCustomQuery(translation);
                        jta.append(translation);
                        status = -1;
                        workQuery(translation, fn, trace_id, rshc);
                        verifMap.put(aspec.getSafetyPragmasRefs().get(j.getText()), status);
                        trace_id++;
                    }
                }

                mgui.modelBacktracingUPPAAL(verifMap);
            }

            long timeEnd = System.currentTimeMillis();
            TraceManager.addDev("************** Send spec time: " + (timeBeg - timeSendSpecBeg) + " ms");
            TraceManager.addDev("************** Overall time: " + (timeEnd - timeBeg) + " ms");

            //Removing files
            deleteFile(fn + ".xml");
            deleteFile(fn + ".q");
            deleteFile(fn + ".res");
            deleteFile(fn + ".xtr");

            rshc.freeId(id);
            jta.append("\nAll Done\n");

        } catch (LauncherException le) {
            jta.append(le.getMessage() + "\n");
            mode = NOT_STARTED;
            setButtons();

            try {
                if (rshctmp != null) {
                    rshctmp.freeId(id);
                }
            } catch (LauncherException le1) {
            }
            return;

        } catch (NullPointerException npe) {
            TraceManager.addError(npe);
            mode = NOT_STARTED;
            setButtons();
            try {
                if (rshctmp != null) {
                    rshctmp.freeId(id);
                }
            } catch (LauncherException le1) {

                return;
            }

        } catch (Exception e) {
            TraceManager.addError(e);
            mode = NOT_STARTED;
            setButtons();
            try {
                if (rshctmp != null) {
                    rshctmp.freeId(id);
                }
            } catch (LauncherException le1) {

                return;
            }
        }




        mode = NOT_STARTED;
        setButtons();
    }
    
    private String checkExpectedResult(String query) {
        query = query.trim();
        expectedResult = true;
        
        if (query.startsWith("T ") || query.startsWith("t ")) {
            return query.substring(2).trim();
        } else if (query.startsWith("F ") || query.startsWith("f ")) {
            expectedResult = false;
            return query.substring(2).trim();
        }
        
        return query;
    }


    private String translateCustomQuery(String query) {
        UPPAALSpec spec = mgui.gtm.getLastUPPAALSpecification();
        AVATAR2UPPAAL avatar2uppaal = mgui.gtm.getAvatar2Uppaal();
        AvatarSpecification avspec = mgui.gtm.getAvatarSpecification();
        Map<String, String> hash = avatar2uppaal.getHash();
        String finQuery = query + " ";
        /*      String[] split = query.split("[\\s-()=]+");
                for (String s: split){
                
                } */
        /*      Pattern p = Pattern.compile("[\\s-()=]+");
                Matcher m = p.matcher(query);
                int index1=0;
                int index2=m.start();
                while (m.find()){
                
                index2=m.start();
                String rep = hash.get(finQuery.substring(index1, index2));
                if (rep !=null){
                
                finQuery = finQuery.substring(0,index1) + rep + finQuery.substring(index2, finQuery.length());
                }
                index1=index2;
                }*/
        for (String str : hash.keySet()) {
            finQuery = finQuery.replaceAll(str + "\\s", hash.get(str));
            finQuery = finQuery.replaceAll(str + "\\)", hash.get(str) + "\\)");
            finQuery = finQuery.replaceAll(str + "\\-", hash.get(str) + "\\-");
        }
        if (avspec == null) {
            return "";
        }

        java.util.List<AvatarBlock> blocks = avspec.getListOfBlocks();
        java.util.List<String> matches = new java.util.ArrayList<String>();
        for (AvatarBlock block : blocks) {
            UPPAALTemplate temp = spec.getTemplateByName(block.getName());
            if (temp != null) {
                if (finQuery.contains(block.getName() + ".")) {
                    matches.add(block.getName());
                }


            }
        }


        for (String match : matches) {
            boolean ignore = false;
            for (String posStrings : matches) {
                if (!posStrings.equals(match) && posStrings.contains(match)) {
                    ignore = true;
                }
            }
            if (!ignore) {
                UPPAALTemplate temp = spec.getTemplateByName(match);
                int index = avatar2uppaal.getIndexOfTranslatedTemplate(temp);
                finQuery = finQuery.replaceAll(match, match + "__" + index);
            }
        }
        //translatedText.setText(finQuery);
        return finQuery;
    }

    private static boolean checkAnalysisResult(final String resultData,
                                               final Collection<String> labels) {

        for (final String verifiedLabel : labels) {
            if (resultData.contains(verifiedLabel)) {
                return true;
            }
        }

        return false;
    }

    // return: -1: error
    // return: 0: property is NOt satisfied
    // return: 1: property is satisfied
    private int workQuery(String query, String fn, int trace_id, RshClient rshc)
            throws LauncherException {

        int ret = -1;
        TraceManager.addDev("Working on query: " + query);


        String cmd1, data;
        if (showDetails.isSelected()) {
            jta.append("-> " + query + "\n");
        }


        rshc.sendFileData(fn + ".q", query);

        cmd1 = cmdVerifyta + " -u ";
        if (generateTrace.isSelected()) {
            cmd1 += "-t1 -f " + fn + " ";
        }
        cmd1 += fn + ".xml " + fn + ".q";
        //jta.append("--------------------------------------------\n");
        //TraceManager.addDev("Query:>" + cmd1 + "<");
        data = processCmd(cmd1);
        //TraceManager.addDev("Results:>" + data + "<");
        if (showDetails.isSelected()) {
            jta.append(data);
        }

        //NOTE: [error] is only visible if Error Stream is parsed
        if (mode != NOT_STARTED) {
            if (data.trim().length() == 0) {
                //jta.append("The verifier of UPPAAL could not be started: error\n");
                throw new LauncherException(UPPAAL_INSTALLATION_ERROR);
            }


            // Issue #35: Different labels for UPPAAL 4.1.19
            else if (checkAnalysisResult(data, PROP_VERIFIED_LABELS)) {
                //            else if (data.indexOf("Property is satisfied") >-1){
                if (expectedResult) {
                    jta.append("-> property is satisfied\n");
                    status = 1;
                } else {
                    jta.append("-> property is NOT satisfied\n");
                    status = 0;
                }
                ret = 1;
            }
            // Issue #35: Different labels for UPPAAL 4.1.19
            else if (checkAnalysisResult(data, PROP_NOT_VERIFIED_LABELS)) {
                //            else if (data.indexOf("Property is NOT satisfied") > -1) {
                if (!expectedResult) {
                    jta.append("-> property is satisfied\n");
                    status = 1;
                } else {
                    jta.append("-> property is NOT satisfied\n");
                    status = 0;
                }
                ret = 1;
            } else {
                jta.append("ERROR -> property could not be studied\n");
                status = 2;


            }
        } else {
            jta.append("** verification stopped **\n");
        }

        if (generateTrace.isSelected()) {
            generateTraceFile(fn, trace_id, rshc);
        }

        return ret;
    }

    private void generateTraceFile(String fn, int trace_id, RshClient rshc) throws LauncherException {
        //jta.append("Going to generate trace file\n");
        String data, name;
        try {
            data = rshc.getFileData(fn + "-1.xtr");
            rshc.deleteFile(fn + "-1.xtr");
        } catch (Exception e) {
            jta.append("(no simulation trace was generated)\n");
            return;
        }
        name = pathTrace + fn + "_" + trace_id + ".xtr";
        //jta.append("Trying to generate trace file in" + name + "\n");
        try {
            FileUtils.saveFile(name, data);
            jta.append("Trace has been generated in " + name + "\n");
        } catch (FileException fe) {
            jta.append("Trace could not be generated in " + name + "\n");
            jta.append("Exception: " + fe.getMessage() + "\n");
        }
    }

    protected String processCmd(String cmd) throws LauncherException {
        try {
            rshc.setCmd(cmd);

            // Issue #35: Test the value of return code and display appropriate error message
            rshc.sendExecuteCommandRequest(true);

            final String data = rshc.getDataFromProcess();

            final Integer retCode = rshc.getProcessReturnCode();

            if (retCode == null || retCode != 0) {
                final String message;

                if (data == null || data.isEmpty()) {
                    message = "Error executing command '" + cmd + "' with return code " + retCode;
                } else {
                    message = data;
                }

                throw new LauncherException(System.lineSeparator() + message);
            }

            return data;
        } catch (Exception e) {
            return "";
        }
        //        String s = null;
        //        rshc.sendExecuteCommandRequest();
        //        s = rshc.getDataFromProcess();
        //        return s;
    }

    protected void setButtons() {
        switch (mode) {
            case NOT_STARTED:
                custom.setEnabled(true);
                //deadlockE.setEnabled(true);
                deadlockA.setEnabled(true);
                stateR_None.setEnabled(true);
                stateR_Selected.setEnabled(true);
                stateR_All.setEnabled(true);
                stateL_None.setEnabled(true);
                stateL_Selected.setEnabled(true);
                stateL_All.setEnabled(true);
                //stateA.setEnabled(true);
                stateLe_None.setEnabled(true);
                stateLe_Selected.setEnabled(true);

                generateTrace.setEnabled(true);
                showDetails.setEnabled(true);
                for (JCheckBox cb : customChecks) {
                    cb.setEnabled(custom.isSelected());
                }
                if (deadlockA.isSelected() || stateR_Selected.isSelected() || stateR_All.isSelected() || stateL_Selected.isSelected() || stateL_All.isSelected() || stateLe_Selected.isSelected()) {
                    start.setEnabled(true);
                } else {
                    if (custom.isSelected()) {
                        if (customChecks == null) {
                            start.setEnabled(false);
                        } else {
                            boolean selected = false;
                            for (JCheckBox box : customChecks) {
                                if (box.isSelected()) {
                                    selected = true;
                                    break;
                                }
                            }
                            start.setEnabled(selected);
                        }

                    } else {
                        start.setEnabled(false);
                    }
                }

                stop.setEnabled(false);
                close.setEnabled(true);
                eraseAll.setEnabled(true);
                getGlassPane().setVisible(false);


                break;
            case STARTED:
                custom.setEnabled(false);
                //deadlockE.setEnabled(false);
                deadlockA.setEnabled(false);
                stateR_None.setEnabled(false);
                stateR_Selected.setEnabled(false);
                stateR_All.setEnabled(false);
                stateL_None.setEnabled(false);
                stateL_Selected.setEnabled(false);
                stateL_All.setEnabled(false);
                //stateA.setEnabled(false);
                stateLe_None.setEnabled(false);
                stateLe_Selected.setEnabled(false);
                generateTrace.setEnabled(false);
                showDetails.setEnabled(false);
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                eraseAll.setEnabled(false);
                for (JCheckBox cb : customChecks) {
                    cb.setEnabled(false);
                }
                getGlassPane().setVisible(true);
                break;
            case STOPPED:
            default:
                custom.setEnabled(false);
                //deadlockE.setEnabled(false);
                deadlockA.setEnabled(false);
                stateR_None.setEnabled(false);
                stateR_Selected.setEnabled(false);
                stateR_All.setEnabled(false);
                stateL_None.setEnabled(false);
                stateL_Selected.setEnabled(false);
                stateL_All.setEnabled(false);
                //stateA.setEnabled(false);
                stateLe_None.setEnabled(false);
                stateLe_Selected.setEnabled(false);
                generateTrace.setEnabled(false);
                showDetails.setEnabled(false);
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                eraseAll.setEnabled(true);
                getGlassPane().setVisible(false);
                for (JCheckBox cb : customChecks) {
                    cb.setEnabled(false);
                }
                break;
        }
    }


    public synchronized void nullRSHC() {
        rshc = null;
    }

    public synchronized void deleteFile(String name) throws LauncherException {
        if (rshc == null) {
            throw new LauncherException("Stopped by used");
        }

        rshc.deleteFile(name);
    }

    public synchronized void sendFileData(String filename, String query) throws LauncherException {
        if (rshc == null) {
            throw new LauncherException("Stopped by used");
        }

        rshc.sendFileData(filename, query);
    }

    public synchronized void freeId(int id) throws LauncherException {
        if (rshc == null) {
            throw new LauncherException("Stopped by used");
        }
        rshc.freeId(id);
    }


}
