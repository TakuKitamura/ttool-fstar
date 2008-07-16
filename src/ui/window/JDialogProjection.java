/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class JDialogProjection
 * Dialog for managing Tclasses to be validated
 * Creation: 30/06/2004
 * @version 1.0 30/06/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import launcher.*;
import myutil.*;
import translator.*;
import ui.*;
import ui.cd.*;


public class JDialogProjection extends javax.swing.JDialog implements ActionListener, ListSelectionListener, Runnable  {
    private static boolean isAldebaranSelected = false;
    private static boolean isOminSelected = false;
    private static boolean isStrongSelected = true;


    private Vector gatesIgnored;
    private Vector gatesProjected;
    
    private MainGUI mgui;
    
    private String aldebaranHost;
    private String aldebaranPath;
    private String bcgioPath;
    private String bcgminPath;
    
    private String inputData;
    private String fileName;
    
    
    // mode and thred management
    protected int mode;
    protected RshClient rshc;
    protected Thread t;
    
    protected final static int NO_OPTIONS = 0;
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    //subpanels
    private JPanel panel1, panel2, panel3, panel4;
    private JList listIgnored;
    private JList listProjected;
    private JButton allProjected;
    private JButton addOneProjected;
    private JButton addOneIgnored;
    private JButton allIgnored;
    protected JTextArea jta;
    
    private JRadioButton omin, imin;
    private JRadioButton aldebaran, bcgmin;
    private JRadioButton strong, branching;
    
    // Main Panel
    private JButton start, stop, close;
    
    /** Creates new form  */
    public JDialogProjection(Frame f, MainGUI _mgui, TClassDiagramPanel tcd, TURTLEModeling tm, String _aldebaranHost, String _aldebaranPath, String _bcgminPath, String _bcgioPath, String _inputData, String _fileName, String title) {
        super(f, title, true);
        
        mgui = _mgui;
        
        aldebaranHost = _aldebaranHost;
        aldebaranPath = _aldebaranPath;
        bcgioPath = _bcgioPath;
        bcgminPath = _bcgminPath;
        inputData = _inputData;
        fileName = _fileName;
        
        if (tcd != null) {
            initGates(tcd);
        }
        
        if (tm != null) {
            initGates(tm);
        }
        
        initComponents();
        myInitComponents();
        pack();
        
        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    private void initGates(TClassDiagramPanel tcd) {
        gatesIgnored = new Vector();
        gatesProjected = new Vector();
        
        TGComponent tgc;
        TClassInterface tci;
        TClassAndGateDS tcg;
        int i, j;
        Vector gates;
        TAttribute ta;
        
        LinkedList list = tcd.getComponentList();
        
        for(i=0; i<list.size(); i++) {
            tgc = (TGComponent)(list.get(i));
            if (tgc instanceof TClassInterface) {
                tci = (TClassInterface)tgc;
                gates = tci.getGates();
                for(j=0; j<gates.size(); j++) {
                    ta = (TAttribute)(gates.elementAt(j));
                    tcg = new TClassAndGateDS(tci, ta);
                    gatesIgnored.addElement(tcg);
                }
            }
        }
        
        Collections.sort(gatesIgnored);
    }
    
    private void initGates(TURTLEModeling tm) {
        //System.out.println("*** init gates tm ***");
        gatesIgnored = new Vector();
        gatesProjected = new Vector();
        TClass t;
        Vector gateList;
        Gate g;
        int j;
        TClassAndGateDS tcg;
        
        for(int i=0; i<tm.classNb(); i++) {
            t = tm.getTClassAtIndex(i);
            gateList = t.getGateList();
            for(j=0; j<gateList.size(); j++) {
                g = (Gate)(gateList.elementAt(j));
                tcg = new TClassAndGateDS(t, g);
                gatesIgnored.addElement(tcg);
            }
            
        }
        
        Collections.sort(gatesIgnored);
    }
    
    
    private void myInitComponents() {
        mode = NO_OPTIONS;
        setButtons();
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
        panel1.setBorder(new javax.swing.border.TitledBorder("Gates ignored"));
        listIgnored = new JList(gatesIgnored);
        //listIgnored.setPreferredSize(new Dimension(200, 250));
        listIgnored.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        listIgnored.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listIgnored);
        panel1.add(scrollPane1, BorderLayout.CENTER);
        panel1.setPreferredSize(new Dimension(400, 250));
        panelTop.add(panel1, BorderLayout.WEST);
        
        // validated list
        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.setBorder(new javax.swing.border.TitledBorder("Gates taken into account"));
        listProjected = new JList(gatesProjected);
        //listProjected.setPreferredSize(new Dimension(200, 250));
        listProjected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        listProjected.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(listProjected);
        panel2.add(scrollPane2, BorderLayout.CENTER);
        panel2.setPreferredSize(new Dimension(400, 250));
        panelTop.add(panel2, BorderLayout.EAST);
        
        // radio buttons
        panel4 = new JPanel();
        panel4.setLayout(gridbag4);
        panel4.setBorder(new javax.swing.border.TitledBorder("Minimization: tools and options"));
        aldebaran = new JRadioButton("aldebaran");
        aldebaran.setSelected(isAldebaranSelected);
        aldebaran.setEnabled(true);
        aldebaran.addActionListener(this);
        omin = new JRadioButton("-omin");
        omin.setEnabled(true);
        omin.setSelected(isOminSelected);
        imin = new JRadioButton("-imin");
        imin.setEnabled(true);
        imin.setSelected(!isOminSelected);
        //c4.anchor = GridBagConstraints.EAST;
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.HORIZONTAL;
        c4.gridheight = 1;
        panel4.add(aldebaran, c4);
        c4.gridwidth = 1;
        panel4.add(new JLabel("       "));
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel4.add(imin, c4);
        c4.gridwidth = 1;
        panel4.add(new JLabel("       "));
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel4.add(omin, c4);

        bcgmin = new JRadioButton("bcg_min");
        bcgmin.setSelected(!isAldebaranSelected);
        bcgmin.setEnabled(true);
        bcgmin.addActionListener(this);
        strong = new JRadioButton("-strong");
        strong.setEnabled(true);
        strong.setSelected(isStrongSelected);
        branching = new JRadioButton("-branching");
        branching.setEnabled(true);
        branching.setSelected(!isStrongSelected);
        imin.setSelected(true);
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.HORIZONTAL;
        c4.gridheight = 1;
        panel4.add(bcgmin, c4);
        c4.gridwidth = 1;
        panel4.add(new JLabel("       "));
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel4.add(strong, c4);
        c4.gridwidth = 1;
        panel4.add(new JLabel("       "));
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel4.add(branching, c4);

        panelTop.add(panel4, BorderLayout.SOUTH);
        activeAldebaran(isAldebaranSelected);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(aldebaran);
        bg.add(bcgmin);
        
        bg = new ButtonGroup();
        bg.add(omin);
        bg.add(imin);
        
        bg = new ButtonGroup();
        bg.add(strong);
        bg.add(branching);
        
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
        allProjected.setActionCommand("allProjected");
        panel3.add(allProjected, c1);
        
        addOneProjected = new JButton(IconManager.imgic48);
        addOneProjected.setPreferredSize(new Dimension(50, 25));
        addOneProjected.addActionListener(this);
        addOneProjected.setActionCommand("addOneProjected");
        panel3.add(addOneProjected, c1);
        
        panel3.add(new JLabel(" "), c1);
        
        addOneIgnored = new JButton(IconManager.imgic46);
        addOneIgnored.addActionListener(this);
        addOneIgnored.setPreferredSize(new Dimension(50, 25));
        addOneIgnored.setActionCommand("addOneIgnored");
        panel3.add(addOneIgnored, c1);
        
        allIgnored = new JButton(IconManager.imgic44);
        allIgnored.addActionListener(this);
        allIgnored.setPreferredSize(new Dimension(50, 25));
        allIgnored.setActionCommand("allIgnored");
        panel3.add(allIgnored, c1);
        
        panelTop.add(panel3, BorderLayout.CENTER);
        
        c.add(panelTop, BorderLayout.NORTH);
        
        // textarea panel
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select gates and then, click on 'start' to start minimization\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        c.add(jsp, BorderLayout.CENTER);
        
        
        // Button panel;
        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);
        
        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(100, 30));
        
        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);
        
        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);
        
        c.add(jp2, BorderLayout.SOUTH);
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (evt.getSource() == aldebaran) {
          activeAldebaran(true);
        } else if (evt.getSource() == bcgmin) {
          activeAldebaran(false);
        } else if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        } else if (command.equals("addOneIgnored")) {
            addOneIgnored();
        } else if (command.equals("addOneProjected")) {
            addOneProjected();
        } else if (command.equals("allProjected")) {
            allProjected();
        } else if (command.equals("allIgnored")) {
            allIgnored();
        }
    }

    public void activeAldebaran(boolean b) {
      omin.setEnabled(b);
      imin.setEnabled(b);
      strong.setEnabled(!b);
      branching.setEnabled(!b);
    }
    
    
    private void addOneIgnored() {
        int [] list = listProjected.getSelectedIndices();
        Vector v = new Vector();
        Object o;
        for (int i=0; i<list.length; i++){
            o = gatesProjected.elementAt(list[i]);
            gatesIgnored.addElement(o);
            v.addElement(o);
        }
        
        gatesProjected.removeAll(v);
        moveSynchronizedGatesAsWell(gatesIgnored, gatesProjected);
        Collections.sort(gatesIgnored);
        listIgnored.setListData(gatesIgnored);
        listProjected.setListData(gatesProjected);
        checkMode();
        setButtons();
    }
    
    private void addOneProjected() {
        int [] list = listIgnored.getSelectedIndices();
        Vector v = new Vector();
        Object o;
        for (int i=0; i<list.length; i++){
            o = gatesIgnored.elementAt(list[i]);
            gatesProjected.addElement(o);
            v.addElement(o);
        }
        
        gatesIgnored.removeAll(v);
        moveSynchronizedGatesAsWell(gatesProjected, gatesIgnored);
        Collections.sort(gatesProjected);
        listIgnored.setListData(gatesIgnored);
        listProjected.setListData(gatesProjected);
        checkMode();
        setButtons();
    }
    
    private void allProjected() {
        gatesProjected.addAll(gatesIgnored);
        Collections.sort(gatesProjected);
        gatesIgnored.removeAllElements();
        listIgnored.setListData(gatesIgnored);
        listProjected.setListData(gatesProjected);
        checkMode();
        setButtons();
    }
    
    private void allIgnored() {
        gatesIgnored.addAll(gatesProjected);
        Collections.sort(gatesIgnored);
        gatesProjected.removeAllElements();
        listIgnored.setListData(gatesIgnored);
        listProjected.setListData(gatesProjected);
        checkMode();
        setButtons();
    }
    
    private void moveSynchronizedGatesAsWell(Vector toCheck, Vector toPickup) {
        TClassAndGateDS tcg, tcg1;
        MasterGateManager mgm = mgui.gtm.getNewMasterGateManager();
        //Gate g;
        GroupOfGates gog, gog1;
        
        for (int i=0; i<toCheck.size(); i++){
            tcg = (TClassAndGateDS)(toCheck.elementAt(i));
            gog = mgm.groupOf(tcg.getTClassName(), tcg.getGateName());
            if (gog != null) {
                for(int j=0; j<toPickup.size(); j++) {
                    tcg1 = (TClassAndGateDS)(toPickup.elementAt(j));
                    gog1 = mgm.groupOf(tcg1.getTClassName(), tcg1.getGateName());
                    if (gog1 == gog) {
                        toCheck.addElement(tcg1);
                        toPickup.removeElementAt(j);
                        j--;
                    }
                }
            }
        }
    }
    
    public void checkMode() {
        if (gatesProjected.size() > 0) {
            mode = NOT_STARTED;
        } else {
            mode = NO_OPTIONS;
        }
    }
    
    private void setButtons() {
        switch(mode) {
            case NO_OPTIONS:
                listProjected.setEnabled(true);
                listIgnored.setEnabled(true);
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
                aldebaran.setEnabled(false);
                bcgmin.setEnabled(false);
                omin.setEnabled(false);
                imin.setEnabled(false);
                strong.setEnabled(false);
                branching.setEnabled(false);
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
        
        if (gatesIgnored.size() ==0) {
            allProjected.setEnabled(false);
        } else {
            allProjected.setEnabled(true);
        }
        
        if (gatesProjected.size() ==0) {
            allIgnored.setEnabled(false);
            //closeButton.setEnabled(false);
            //closeButton.setEnabled(false);
        } else {
            allIgnored.setEnabled(true);
            //closeButton.setEnabled(true);
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
        if (rshc != null) {
            try {
                rshc.stopFillJTA();
            } catch (LauncherException le) {
                
            }
            rshc = null;
        }
        mode = 	STOPPED;
        setButtons();
    }
    
    public void startProcess() {
        t = new Thread(this);
        mode = STARTED;
        setButtons();
        t.start();
    }
    
    public void run() {
        String cmd1 = "";
        String autproj;
        String autprojdot;
        String data;
        int id = 0;
        String filenamebcg;
        Point p;
		
		RshClient rshctmp = null;
		

        try {
          // saving current options
          isAldebaranSelected = aldebaran.isSelected();
          isOminSelected = omin.isSelected();
          isStrongSelected = strong.isSelected();

            mgui.gtm.reinitRGAUTPROJDOT();
            jta.append("Modifying original RG\n");
            //System.out.println("Input data = " + inputData);
            
            autproj = mgui.gtm.performProjection(inputData, gatesProjected);
            
            if (autproj == null) {
                jta.append("\nError: the RG could not be prepared for minimization\n");
            } else {
              jta.append("\nRemoving ignored gates\n");
                if (isAldebaranSelected) {

                //System.out.println("Output data = " + autproj);
                
                //jta.append(autproj);

                //mgui.gtm.setRGAUTPROJ(autproj);
                //mgui.saveRGAutProj();
                
                if ((aldebaranHost == null) || (aldebaranHost.length() == 0)) {
                    jta.append("No Aldebaran installed -> cannot performed minimization\n");
                    mgui.gtm.setRGAUTPROJ(autproj);
                    mgui.saveRGAutProj();
                } else {
                    jta.append("Minimization with Aldebaran\n");
                    rshc = new RshClient(aldebaranHost);
					rshctmp = rshc;
                    id = rshc.getId();
                    fileName = FileUtils.addBeforeFileExtension(fileName, "_" + id);
                    jta.append("Session id on launcher="+id + " ; working on " + fileName + "\n");

                    rshc.deleteFile(fileName);
                    jta.append("Sending data\n");
                    rshc.sendFileData(fileName, autproj);
                    cmd1 = aldebaranPath;
                    if (omin.isSelected()) {
                        cmd1 +=  " -omin ";
                    } else {
                        cmd1 +=  " -imin ";
                    }
                    cmd1 += fileName;
                    jta.append("Performing minimization\n");
                    autproj = processCmd(cmd1);

                    // Print info on minimized graph
                    p = FormatManager.nbStateTransitionRGAldebaran(autproj);
                    jta.append("\n" + p.x + " state(s), " + p.y + " transition(s)\n\n");

                    mgui.gtm.setRGAUTPROJ(autproj);
                    mgui.saveRGAutProj();
                    jta.append("Minimization done\n");
                    //jta.append(autproj);
                    
                    // AUT proj dot
                    jta.append("\nConverting to dotty format\n");
                    rshc.sendFileData(fileName, autproj);
                    cmd1 = bcgioPath + " -aldebaran " + fileName + " -graphviz " + fileName + ".dot";
                    autproj = processCmd(cmd1);
                    autprojdot = rshc.getFileData(fileName + ".dot");
                    mgui.gtm.setRGAUTPROJDOT(autprojdot);
                    mgui.saveRGAutProjDOT();
                    rshc.deleteFile(fileName);
                    rshc.deleteFile(fileName + ".dot");
                    rshc.freeId(id);
                }
                } else {
                  // BCGMIN
                  if ((aldebaranHost == null) || (aldebaranHost.length() == 0) ||(bcgminPath.length() == 0)) {
                    jta.append("No bcgmin installed -> cannot performed minimization\n");
                    mgui.gtm.setRGAUTPROJ(autproj);
                    mgui.saveRGAutProj();
                } else {
                    jta.append("Minimization with bcgmin\n");
                    rshc = new RshClient(aldebaranHost);
                    id = rshc.getId();
					rshctmp = rshc;
                    fileName = FileUtils.addBeforeFileExtension(fileName, "_" + id);
                    filenamebcg = FileUtils.changeFileExtension(fileName, "bcg");
                    jta.append("Session id on launcher="+id + " ; working on " + fileName + "\n");

                    jta.append("Sending data\n");
                    rshc.sendFileData(fileName, autproj);
                    
                    // Converting to bcg format
                    jta.append("Converting data to bcg format\n");
                    cmd1 = bcgioPath + " -aldebaran " + fileName + " -bcg " + filenamebcg;
                    data = processCmd(cmd1);

                    cmd1 = bcgminPath;
                    if (isStrongSelected) {
                        cmd1 +=  " -strong ";
                    } else {
                        cmd1 +=  " -branching ";
                    }
                    cmd1 += filenamebcg+ " " + filenamebcg;
                    jta.append("Performing minimization\n");
                    data = processCmd(cmd1);
                    jta.append("Minimization done\n");

                    jta.append("Converting data to aut format\n");
                    cmd1 = bcgioPath + " -bcg " + filenamebcg+ " -aldebaran " + fileName;
                    data = processCmd(cmd1);

                    jta.append("Getting aut data\n");
                    data = rshc.getFileData(fileName);
                    
                    // Print info on minimized graph
                    p = FormatManager.nbStateTransitionRGAldebaran(autproj);
                    jta.append("\n" + p.x + " state(s), " + p.y + " transition(s)\n\n");

                    mgui.gtm.setRGAUTPROJ(autproj);
                    mgui.saveRGAutProj();
                    //jta.append(autproj);

                    // AUT proj dot
                    jta.append("\nConverting to dotty format\n");
                    rshc.sendFileData(fileName, autproj);
                    cmd1 = bcgioPath + " -bcg " + filenamebcg + " -graphviz " + fileName + ".dot";
                    data = processCmd(cmd1);
                    jta.append("Getting dot data\n");
                    autprojdot = rshc.getFileData(fileName + ".dot");
                    mgui.gtm.setRGAUTPROJDOT(autprojdot);
                    mgui.saveRGAutProjDOT();
                    
                    fileName = FileUtils.removeFileExtension(fileName);
                    rshc.deleteFile(fileName + ".aut");
                    rshc.deleteFile(fileName + ".bcg");
                    rshc.deleteFile(fileName + ".aut.dot");
                    rshc.deleteFile(fileName + ".o");
                    rshc.deleteFile(fileName + "@1.o");
                    rshc.freeId(id);
                }
                }
            }

            jta.append("Done\n");
            
        } catch (LauncherException le) {
            jta.append("Error:" + le.getMessage() + "\n");
			try{
				if (rshctmp != null) {
					rshctmp.freeId(id);
				}
			} catch (LauncherException leb) {}
            mode = STOPPED;
            setButtons();
            return;
        } catch (Exception e) {
            mode = STOPPED;
			try{
				if (rshctmp != null) {
					rshctmp.freeId(id);
				}
			} catch (LauncherException leb) {}
            setButtons();
            return;
        }
        
        mode = STOPPED;
        setButtons();
    }
    
    protected String processCmd(String cmd) throws LauncherException {
        rshc.setCmd(cmd);
        String s = null;
        rshc.sendProcessRequest();
        s = rshc.getDataFromProcess();
        return s;
    }
}
