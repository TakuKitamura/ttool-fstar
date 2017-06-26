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

import avatartranslator.AvatarPragma;
import avatartranslator.AvatarPragmaAuthenticity;
import avatartranslator.AvatarPragmaReachability;
import avatartranslator.AvatarPragmaSecret;
import launcher.LauncherException;
import launcher.RshClient;
import launcher.RshClientReader;
import myutil.GraphicLib;
import myutil.MasterProcessInterface;
import myutil.TraceManager;
import proverifspec.ProVerifOutputAnalyzer;
import proverifspec.ProVerifQueryAuthResult;
import proverifspec.ProVerifQueryResult;
import proverifspec.ProVerifResultTraceStep;
import proverifspec.ProVerifOutputListener;
import ui.AvatarDesignPanel;
import ui.util.IconManager;
import ui.MainGUI;
import ui.interactivesimulation.JFrameSimulationSDPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.Map;
import java.util.LinkedList;
import java.util.Collections;


/**
 * Class JDialogProverifVerification
 * Dialog for managing the generation of ProVerif code and execution of
 * ProVerif
 * Creation: 19/02/2017
 * @version 1.0 19/02/2017
 * @author Ludovic APVRILLE
 */

public class JDialogProverifVerification extends javax.swing.JDialog implements ActionListener, ListSelectionListener, MouseListener, Runnable, MasterProcessInterface, ProVerifOutputListener {

    private static final Insets insets = new Insets(0, 0, 0, 0);
    private static final Insets WEST_INSETS = new Insets(0, 0, 0, 0);
    private static final Insets EAST_INSETS = new Insets(0, 0, 0, 0);

    protected MainGUI mgui;
    private AvatarDesignPanel adp;

    private String textC1 = "Generate ProVerif code in: ";
    private String textC2 = "Execute ProVerif as: ";

    protected static String pathCode;
    protected static String pathExecute;


    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    public final static int REACHABILITY_ALL        = 1;
    public final static int REACHABILITY_SELECTED   = 2;
    public final static int REACHABILITY_NONE       = 3;

    int mode;

    private ProVerifOutputAnalyzer pvoa;

    //components
    protected JPanel jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    protected JPopupMenu popup;

    private class MyMenuItem extends JMenuItem {
        AvatarPragma pragma;
        ProVerifQueryResult result;

        public MyMenuItem(String text)
        {
            super(text);
        }
    }

    protected MyMenuItem menuItem;


    //protected JRadioButton exe, exeint;
    //protected ButtonGroup exegroup;
    protected JLabel gen, comp, exe;
    protected JTextField code1, code2, unitcycle, compiler1, exe1, exe2, exe3, exe2int, loopLimit;
    //protected JTabbedPane jp1;
    protected JScrollPane jsp;
    protected JCheckBox typedLanguage;
    protected JRadioButton stateReachabilityAll, stateReachabilitySelected, stateReachabilityNone;
    protected ButtonGroup stateReachabilityGroup;
    protected JComboBox versionSimulator;

    private JList<AvatarPragma> reachableEventsList;
    private JList<AvatarPragma> nonReachableEventsList;
    private JList<AvatarPragma> secretTermsList;
    private JList<AvatarPragma> nonSecretTermsList;
    private JList<AvatarPragma> satisfiedStrongAuthList;
    private JList<AvatarPragma> satisfiedWeakAuthList;
    private JList<AvatarPragma> nonSatisfiedAuthList;
    private JList<AvatarPragma> nonProvedList;
    private Map<AvatarPragma, ProVerifQueryResult> results;
	private boolean limit;
    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
    //protected boolean startProcess = false;

    private String hostProVerif;

    protected RshClient rshc;

    private class ProVerifVerificationException extends Exception {
        private String message;

        public ProVerifVerificationException(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return this.message;
        }
    }

    /** Creates new form  */
    public JDialogProverifVerification(Frame f, MainGUI _mgui, String title, String _hostProVerif, String _pathCode, String _pathExecute, AvatarDesignPanel adp, boolean lim) {
        super(f, title, Dialog.ModalityType.DOCUMENT_MODAL);

        mgui = _mgui;
        this.adp = adp;
        this.pvoa = null;
		this.limit=lim;
        if (pathCode == null) {
            pathCode = _pathCode;
        }

        if (pathExecute == null)
            pathExecute = _pathExecute;


        hostProVerif = _hostProVerif;

        initComponents();
        myInitComponents();
        pack();

        // getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
    }

    private void addComponent(Container container, Component component, int gridx, int gridy,
            int gridwidth, int gridheight, int anchor, int fill) {
        GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0,
                anchor, fill, insets, 0, 0);
        container.add(component, gbc);
    }

    private GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        gbc.fill = (x == 0) ? GridBagConstraints.BOTH
            : GridBagConstraints.HORIZONTAL;

        gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = (x == 0) ? 0.1 : 1.0;
        gbc.weighty = 1.0;
        return gbc;
    }

    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());

        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Verification options"));


        gen = new JLabel(textC1);
        addComponent(jp01, gen, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        code1 = new JTextField(pathCode, 100);
        addComponent(jp01, code1, 1, 0, 3, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH);

        exe = new JLabel(textC2);
        addComponent(jp01, exe, 0, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH);

        exe2 = new JTextField(pathExecute, 100);
        addComponent(jp01, exe2, 1, 1, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        addComponent(jp01, new JLabel("Compute state reachability: "), 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);



        stateReachabilityGroup = new ButtonGroup ();


        stateReachabilityAll = new JRadioButton("all");
        addComponent(jp01, stateReachabilityAll, 1, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);


        stateReachabilitySelected = new JRadioButton("selected");
        addComponent(jp01, stateReachabilitySelected, 2, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        stateReachabilityNone = new JRadioButton("none");
        addComponent(jp01, stateReachabilityNone, 3, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        stateReachabilityGroup.add (stateReachabilityAll);
        stateReachabilityGroup.add (stateReachabilitySelected);
        stateReachabilityGroup.add (stateReachabilityNone);
        stateReachabilityAll.setSelected(true);

        typedLanguage = new JCheckBox("Generate typed Pi calculus");
        typedLanguage.setSelected(true);
        addComponent(jp01, typedLanguage, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

   	    loopLimit = new JTextField("1", 3);
		if (limit){
	        addComponent(jp01, new JLabel("Limit on loop iterations:"), 0, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    	    addComponent(jp01, loopLimit, 1, 5, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		}
        c.add(jp01, BorderLayout.NORTH);


        jta = new JPanel();
        jta.setLayout(new GridBagLayout());
        jta.setBorder(new javax.swing.border.TitledBorder("Verification results"));
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setPreferredSize(new Dimension(300,300));
        c.add(jsp, BorderLayout.CENTER);

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

        this.popup = new JPopupMenu();
        this.menuItem = new MyMenuItem("Show trace");
        this.menuItem.addActionListener(this);
        popup.add(this.menuItem);
    }

    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();

        if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        } else if (command.equals("Show trace")) {
            if (evt.getSource() == this.menuItem)
            {
                PipedOutputStream pos = new PipedOutputStream();
                try {
                    PipedInputStream pis = new PipedInputStream(pos, 4096);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(pos));

                    JFrameSimulationSDPanel jfssdp = new JFrameSimulationSDPanel(null, this.mgui, this.menuItem.pragma.toString());
                    jfssdp.setIconImage(IconManager.img8);
                    GraphicLib.centerOnParent(jfssdp, 600, 600);
                    jfssdp.setFileReference(new BufferedReader(new InputStreamReader(pis)));
                    jfssdp.setVisible(true);
                    jfssdp.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
                    jfssdp.toFront();

                    // TraceManager.addDev("\n--- Trace ---");
                    int i=0;
					if (adp!=null){
	                    for (ProVerifResultTraceStep step: this.menuItem.result.getTrace().getTrace()) {
	                        step.describeAsSDTransaction(this.adp, bw, i);
	                        i++;
	                        // TraceManager.addDev(step.describeAsString(this.adp));
	                    }
					}
					else {
						for (ProVerifResultTraceStep step: this.menuItem.result.getTrace().getTrace()) {
	                        step.describeAsTMLSDTransaction(bw, i);
	                        i++;
	                        // TraceManager.addDev(step.describeAsString(this.adp));
	                    }
					}
                    bw.close();
                } catch(IOException e) {
                    TraceManager.addDev("Error when writing trace step SD transaction");
                } finally {
                    try {
                        pos.close();
                    } catch(IOException e) {}
                }
                // TraceManager.addDev("");
            }
        }
    }

    public void closeDialog() {
        if (this.pvoa != null) {
            this.pvoa.removeListener(this);
        }
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
    }

    public void stopProcess() {
        if (rshc != null ){
            try {
                rshc.stopCommand();
            } catch (LauncherException le) {
            }
        }
        rshc = null;
        mode =  STOPPED;
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

    private void testGo() throws InterruptedException {
        if (go == false) {
            throw new InterruptedException("Stopped by user");
        }
    }

    class ProVerifResultSection {
        String title;
        LinkedList<AvatarPragma> results;
        JList<AvatarPragma> jlist;

        ProVerifResultSection(String title, LinkedList<AvatarPragma> results, JList<AvatarPragma> jlist)
        {
            this.title = title;
            this.results = results;
            this.jlist = jlist;
        }
    }

    public void run() {
        String list;
        int cycle = 0;

        hasError = false;

        TraceManager.addDev("Thread started");
        File testFile;
        try {

            testGo();
            pathCode = code1.getText().trim ();

            if (pathCode.isEmpty() || pathCode.endsWith(File.separator)) {
                pathCode += "pvspec";
            }

            testFile = new File(pathCode);

            if (testFile != null && testFile.isDirectory()){
                pathCode += File.separator;
                pathCode += "pvspec";
                testFile = new File(pathCode);
            }

            File dir = null;
            if (testFile != null)
            {
                dir = testFile.getParentFile();
            }

            if (testFile == null || dir == null || !dir.exists()) {
                mode = STOPPED;
                setButtons();
                throw new ProVerifVerificationException("Error: invalid file: " + pathCode);
            }


            if (testFile.exists()){
                // FIXME Raise error if modified since last
                System.out.println("FILE EXISTS!!!");
            }

            if (
                    mgui.gtm.generateProVerifFromAVATAR(
                        pathCode,
                        stateReachabilityAll.isSelected () ? REACHABILITY_ALL : stateReachabilitySelected.isSelected () ? REACHABILITY_SELECTED : REACHABILITY_NONE,
                        typedLanguage.isSelected(),
                        loopLimit.getText())
               ) {
            } else {
                this.hasError = true;
                throw new ProVerifVerificationException("Could not generate proverif code");
            }

            String cmd = exe2.getText().trim();

            if (this.typedLanguage.isSelected())
            {
                cmd += " -in pitype ";
            }
            else
            {
                cmd += " -in pi ";
            }

            cmd += pathCode;
            //jta.append("" +  mgui.gtm.getCheckingWarnings().size() + " warning(s)\n");
            testGo();

            this.rshc = new RshClient(hostProVerif);
            this.rshc.setCmd(cmd);
            this.rshc.sendExecuteCommandRequest();
            RshClientReader reader = this.rshc.getDataReaderFromProcess();

            if (this.pvoa == null) {
                this.pvoa = mgui.gtm.getProVerifOutputAnalyzer ();
                this.pvoa.addListener(this);
            }
            this.pvoa.analyzeOutput(reader, typedLanguage.isSelected());

            mgui.modelBacktracingProVerif(pvoa);

            mode = NOT_STARTED;

        } catch (LauncherException le) {
            JLabel label = new JLabel("Error: " + le.getMessage());
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.jta.add(label, this.createGbc(0, 0));
            mode = STOPPED;
        } catch (InterruptedException ie) {
            mode = NOT_STARTED;
        } catch (ProVerifVerificationException pve) {
            JLabel label = new JLabel("Error: " + pve.getMessage());
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.jta.add(label, this.createGbc(0, 0));
            mode = STOPPED;
        } catch (Exception e) {
            mode = STOPPED;
            throw e;
        }


        setButtons();

    }

    protected void setButtons() {
        switch(mode) {
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
                getGlassPane().setVisible(true);
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

    @Override
    public void setError()
    {
        this.hasError = true;
    }

    @Override
    public void appendOut(String s)
    {
    }

    @Override
    public boolean hasToContinue()
    {
        return this.go;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        this.maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        this.maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e)
    {
        if (e.isPopupTrigger() && e.getComponent() instanceof JList)
        {
            JList curList = (JList) e.getComponent();
            int row = curList.locationToIndex(e.getPoint());
            curList.clearSelection();
            curList.setSelectedIndex(row);
            Object o = curList.getModel().getElementAt(row);
            if (o instanceof AvatarPragma) {
                this.menuItem.pragma = (AvatarPragma) o;
                this.menuItem.result = this.results.get(this.menuItem.pragma);
             //   this.menuItem.setEnabled(this.adp != null && this.menuItem.result.getTrace() != null);
				this.menuItem.setEnabled(this.menuItem.result.getTrace() != null);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        // TODO: unselect the other lists
    }

    @Override
    public void proVerifOutputChanged()
    {
        JLabel label;
        this.jta.removeAll();

        if (pvoa.getErrors().size() != 0) {
            int y = 0;

            label = new JLabel("Errors found in the generated code:");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.jta.add(label, this.createGbc(0, y++));
            label = new JLabel("----------------");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.jta.add(label, this.createGbc(0, y++));
            this.jta.add(Box.createRigidArea(new Dimension(0,5)), this.createGbc(0, y++));
            for(String error: pvoa.getErrors()) {
                label = new JLabel(error);
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                this.jta.add(label,this.createGbc(0, y++));
            }
        } else {
            LinkedList<AvatarPragma> reachableEvents = new LinkedList<AvatarPragma> ();
            LinkedList<AvatarPragma> nonReachableEvents = new LinkedList<AvatarPragma> ();
            LinkedList<AvatarPragma> secretTerms = new LinkedList<AvatarPragma> ();
            LinkedList<AvatarPragma> nonSecretTerms = new LinkedList<AvatarPragma> ();
            LinkedList<AvatarPragma> satisfiedStrongAuth = new LinkedList<AvatarPragma> ();
            LinkedList<AvatarPragma> satisfiedWeakAuth = new LinkedList<AvatarPragma> ();
            LinkedList<AvatarPragma> nonSatisfiedAuth = new LinkedList<AvatarPragma> ();
            LinkedList<AvatarPragma> nonProved = new LinkedList<AvatarPragma> ();

            this.results = this.pvoa.getResults();
            for (AvatarPragma pragma: this.results.keySet())
            {
                if (pragma instanceof AvatarPragmaReachability)
                {
                    ProVerifQueryResult r = this.results.get(pragma);
                    if (r.isProved())
                    {
                        if (r.isSatisfied())
                            reachableEvents.add(pragma);
                        else
                            nonReachableEvents.add(pragma);
                    }
                    else
                        nonProved.add(pragma);
                }

                else if (pragma instanceof AvatarPragmaSecret)
                {
                    ProVerifQueryResult r = this.results.get(pragma);
                    if (r.isProved())
                    {
                        if (r.isSatisfied())
                            secretTerms.add(pragma);
                        else
                            nonSecretTerms.add(pragma);
                    }
                    else
                        nonProved.add(pragma);
                }

                else if (pragma instanceof AvatarPragmaAuthenticity)
                {
                    ProVerifQueryAuthResult r = (ProVerifQueryAuthResult) this.results.get(pragma);
                    if (!r.isWeakProved())
                    {
                        nonProved.add(pragma);
                    }
                    else
                    {
                        if (!r.isProved())
                            nonProved.add(pragma);
                        if (r.isProved() && r.isSatisfied())
                            satisfiedStrongAuth.add(pragma);
                        else if (r.isWeakSatisfied())
                            satisfiedWeakAuth.add(pragma);
                        else
                            nonSatisfiedAuth.add(pragma);
                    }
                }
            }

            LinkedList<ProVerifResultSection> sectionsList = new LinkedList<ProVerifResultSection> ();
            Collections.sort(reachableEvents);
            Collections.sort(nonReachableEvents);
            Collections.sort(secretTerms);
            Collections.sort(nonSecretTerms);
            Collections.sort(satisfiedStrongAuth);
            Collections.sort(satisfiedWeakAuth);
            Collections.sort(nonSatisfiedAuth);
            Collections.sort(nonProved);
            sectionsList.add(new ProVerifResultSection("Reachable states:", reachableEvents, this.reachableEventsList));
            sectionsList.add(new ProVerifResultSection("Non reachable states:", nonReachableEvents, this.nonReachableEventsList));
            sectionsList.add(new ProVerifResultSection("Confidential Data:", secretTerms, this.secretTermsList));
            sectionsList.add(new ProVerifResultSection("Non confidential Data:", nonSecretTerms, this.nonSecretTermsList));
            sectionsList.add(new ProVerifResultSection("Satisfied Strong Authenticity:", satisfiedStrongAuth, this.satisfiedStrongAuthList));
            sectionsList.add(new ProVerifResultSection("Satisfied Weak Authenticity:", satisfiedWeakAuth, this.satisfiedWeakAuthList));
            sectionsList.add(new ProVerifResultSection("Non Satisfied Authenticity:", nonSatisfiedAuth, this.nonSatisfiedAuthList));
            sectionsList.add(new ProVerifResultSection("Not Proved Queries:", nonProved, this.nonProvedList));

            int y = 0;

            for (ProVerifResultSection section: sectionsList)
            {
                if (!section.results.isEmpty())
                {
                    label = new JLabel(section.title);
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    this.jta.add(label, this.createGbc(0, y++));
                    this.jta.add(Box.createRigidArea(new Dimension(0,5)), this.createGbc(0, y++));
                    section.jlist = new JList<AvatarPragma> (section.results.toArray (new AvatarPragma[0]));
                    section.jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    section.jlist.addMouseListener(this);
                    section.jlist.setAlignmentX(Component.LEFT_ALIGNMENT);
                    this.jta.add(section.jlist, this.createGbc(0, y++));
                    this.jta.add(Box.createRigidArea(new Dimension(0,10)), this.createGbc(0, y++));
                }
            }
        }

        this.repaint();
        this.revalidate();
    }
}
