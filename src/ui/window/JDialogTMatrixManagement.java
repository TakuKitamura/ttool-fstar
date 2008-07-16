/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class JDialogTMatrixManagement
 * Dialog for managing remote processes call on traceability matrices
 * Creation: 16/08/2006
 * @version 1.0 16/08/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import launcher.*;
import myutil.*;
import tmatrix.*;
import ui.*;

public class JDialogTMatrixManagement extends JFrame implements ActionListener, Runnable  {
    
    protected MainGUI mgui;
    protected RequirementModeling rm;
    protected TMatrixTableModel tm;
    protected TableSorter sorter;
    
    protected String cmdRTL;
    protected String cmdDTA2DOT;
    protected String cmdRGSTRAP;
    protected String cmdRG2TLSA;
    protected String fileName;
    protected String spec;
    protected String host;
    protected String hostAldebaran;
    protected String bcgioPath;
    protected int mode;
    protected RshClient rshc;
    protected Thread t;
    
    protected final static int NO_OPTIONS = 0;
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    //components
    protected JTable jtable;
    protected JScrollPane jsp;
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    
    protected Vector toBeChecked;
    
    /** Creates new form  */
    public JDialogTMatrixManagement(Frame f, MainGUI _mgui, String title, RequirementModeling _rm, String _cmdRTL, String _cmdDTA2DOT, String _cmdRGSTRAP, String _cmdRG2TLSA, String _fileName, String _host, String _aldebaranHost, String _bcgioPath) {
        super(title);
        //super(f, title, true);
        
        mgui = _mgui;
        rm = _rm;
        
        cmdRTL = _cmdRTL;
        cmdDTA2DOT = _cmdDTA2DOT;
        cmdRGSTRAP = _cmdRGSTRAP;
        cmdRG2TLSA = _cmdRG2TLSA;
        fileName = _fileName;
        host = _host;
        
        hostAldebaran = _aldebaranHost;
        bcgioPath = _bcgioPath;
        
        initComponents();
        myInitComponents();
        pack();
        
        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    
    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
    }
    
    protected void initComponents() {
        
        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel jp1 = new JPanel();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        
        jp1.setLayout(gridbag1);
        jp1.setBorder(new javax.swing.border.TitledBorder("Current matrix"));
        jp1.setPreferredSize(new Dimension(400, 150));
        
        // first line panel1
        //c1.gridwidth = 3;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        
        tm = new TMatrixTableModel(rm);
        sorter = new TableSorter(tm);
        jtable = new JTable(sorter);
        jtable.addMouseListener(new PopupListener(this));
        sorter.setTableHeader(jtable.getTableHeader());
        ((jtable.getColumnModel()).getColumn(0)).setPreferredWidth(Math.max(maxLengthColumn(jp1, tm, 0) + 20, 100));
        ((jtable.getColumnModel()).getColumn(1)).setPreferredWidth(Math.max(maxLengthColumn(jp1, tm, 1) + 15, 10));
        ((jtable.getColumnModel()).getColumn(2)).setPreferredWidth(Math.max(maxLengthColumn(jp1, tm, 2) + 15, 100));
        ((jtable.getColumnModel()).getColumn(3)).setPreferredWidth(Math.max(maxLengthColumn(jp1, tm, 3) + 15, 100));
        jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jsp = new JScrollPane(jtable);
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(10);
        
        jp1.add(jsp, c1);
        
        c.add(jp1, BorderLayout.NORTH);
        
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Waiting for commands\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        JSplitPane jsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jp1, jsp);
        c.add(jsplit, BorderLayout.CENTER);
        
        start = new JButton("Check", IconManager.imgic53);
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
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        //System.out.println("Actions");
        
        // Compare the action command to the known actions.
        if (command.equals("Check"))  {
            fillCheckVector();
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        }
    }
    
    public void fillCheckVector() {
        toBeChecked = new Vector();
        if (jtable.getSelectedRowCount() == 0) {
            // Checking all
            toBeChecked.addAll(rm.getMatrix());
        } else {
            // Checking only selected ones
            int [] tab = jtable.getSelectedRows();
            for(int i=0; i<tab.length; i++) {
                toBeChecked.add(rm.getRequirements(sorter.modelIndex(i)));
            }
            
        }
        
    }
    
    
    
    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
    }
    
    public void stopProcess() {
        try {
            rshc.stopFillJTA();
        } catch (LauncherException le) {
            
        }
        rshc = null;
        mode = 	STOPPED;
        setButtons();
    }
    
    public void startProcess() {
        t = new Thread(this);
        mode = STARTED;
        setButtons();
        t.start();
    }
    
    public JTable getJTable() {
        return jtable;
    }
    
    public Requirements getRequirements(int index) {
        return rm.getRequirements(sorter.modelIndex(index));
    }
    
    public void run() {
        Requirements reqs;
        String cmd1;
        //String cmd2, cmd3, cmd4;
        String baseFileName = fileName.substring(0, fileName.length() - 4);
        String fileDTA = baseFileName + ".dta";
        String fileDTADOT = fileDTA + ".dot";
        //String fileRG = baseFileName + ".dta";
        //String fileRGDOT = fileRG + ".dot";
        //String fileTLSA = baseFileName + ".tlsa";
        //String fileTLSADOT = fileTLSA + ".dot";
        String data;
        
        rshc = new RshClient(host);
        Point p;
        
        try {
            jta.append("Checking requirements with observers\n");
            
            for(int i=0; i<toBeChecked.size(); i++) {
                reqs = (Requirements)(toBeChecked.get(i));
                jta.append("#" + i + ": Dealing with observer " + reqs.ro.getValue() + "\n");
                
                
                if (reqs.formalSpec == null) {
                    jta.append("Property #" + i + "has no formal specification. Skipping.\n");
                } else {
                    
                    rshc.deleteFile(fileName);
                    rshc.deleteFile(fileDTA);
                    rshc.deleteFile(fileDTADOT);
                    
                    rshc.sendFileData(fileName, reqs.formalSpec);
                    jta.append("Data sent\n");
                    
                    rshc.deleteFile(fileName + ".rg0.aut");
                    rshc.deleteFile(fileName + ".rg0.aut.dot");
                    cmd1 = cmdRTL + " -ATG -AUT";
                    cmd1 += " -TG2 " + fileName;
                    //cmd3 = "cat " + fileRG;
                    //cmd4 = cmdDTA2DOT;
                    
                    //.RG
                    jta.append("Making RG format AUT\n");
                    processCmd(cmd1);
                    //jta.append(data);
                    //mgui.gtm.setRGAut(data);
                    //mgui.saveRGAut();
                    jta.append("RG Done\n");
                    
                    // removing useless files
                    rshc.deleteFile(fileName + ".tg0.aut");
                    rshc.deleteFile(fileName + "tg0.fc2");
                    rshc.deleteFile(fileName + ".rg0.fc2");
                    rshc.deleteFile(fileName + ".rg0.ren");
                    
                    jta.append("Getting data from " + fileName + ".rg0.aut" + "\n");
                    // Getting data
                    data = rshc.getFileData(fileName + ".rg0.aut");
                    //jta.append("\ndata done\n");
                    p = FormatManager.nbStateTransitionRGAldebaran(data);
                    jta.append("" + p.x + " state(s), " + p.y + " transition(s)\n");
                    //jta.append(data);
                    reqs.graphAut = data;
                    
                    // AUT  dot
                    jta.append("Converting to dotty format\n");
                    rshc = new RshClient(hostAldebaran);
                    // Sending data > if rtlhost != aldebaranhost
                    if (host.compareTo(hostAldebaran) != 0) {
                        // Need to send data generated by rtl
                        rshc.sendFileData(fileName + ".rg0.aut", data);
                        jta.append("Sending data to aldebaran host\n");
                    }
                    
                    // Bcgio command
                    cmd1 = bcgioPath + " -aldebaran " + fileName + ".rg0.aut" + " -graphviz " + fileName + ".rg0.aut.dot";
                    data = processCmd(cmd1);
                    data = rshc.getFileData(fileName + ".rg0.aut.dot");
                    reqs.graphDot = data;
                }
                
                rshc.deleteFile(fileName);
                
                // Satisfiability
                reqs.setGraphAut(reqs.graphAut);
                if (reqs.satisfied) {
                    jta.append("OK: property is satisfied\n");
                } else {
                    jta.append("KO: property is NOT satisfied\n");
                }
                jta.append("All Done for property #" + i + "\n\n");
                jtable.repaint();
            }
            jta.append("\nAll Done\n");
            
        } catch (LauncherException le) {
            jta.append("Error: " + le.getMessage() + "\n");
            mode = 	NOT_STARTED;
            setButtons();
            return;
        } catch (Exception e) {
            jta.append("Error: " + e.getMessage() + "\n");
            mode = 	NOT_STARTED;
            setButtons();
            return;
        }
        
        mode = NOT_STARTED;
        setButtons();
    }
    
    protected String processCmd(String cmd) throws LauncherException {
        rshc.setCmd(cmd);
        String s = null;
        rshc.sendProcessRequest();
        s = rshc.getDataFromProcess();
        return s;
    }
    
    protected String processPipedCmd(String cmd1, String cmd2) throws LauncherException {
        String s = null;
        rshc.sendProcessRequest(cmd1, cmd2);
        s = rshc.getDataFromProcess();
        return s;
    }
    
    protected void checkMode() {
        
        mode = NOT_STARTED;
    }
    
    protected void setButtons() {
        switch(mode) {
            case NOT_STARTED:
                jtable.setEnabled(true);
                start.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                jtable.setEnabled(false);
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                break;
            case STOPPED:
            default:
                jtable.setEnabled(false);
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
        }
    }
    
    private int maxLengthColumn(Component c, AbstractTableModel tm, int index) {
        int w = 0, wtmp;
        FontMetrics fm = c.getFontMetrics(c.getFont());
        if (fm == null) {
            return 0;
        }
        
        String s;
        
        for(int i=0; i<tm.getRowCount(); i++) {
            s = tm.getValueAt(i, index).toString();
            wtmp = fm.stringWidth(s);
            w = Math.max(w, wtmp);
        }
        return w;
    }
    
    public void drawRequirements(Requirements reqs) {
        if (reqs != null) {
            mgui.gtm.generateDesign(reqs.tm);
        }
    }
    
    public void viewRequirementsFormalSpecification(Requirements reqs) {
        if (reqs != null) {
            mgui.showFormalSpecification(reqs.ro.getValue() + "'s formal specification", reqs.formalSpec);
        }
    }
    
    public void viewRG(Requirements reqs) {
        if (reqs != null) {
            mgui.gtm.runDOTTY(reqs.graphDot);
        }
    }
    
     public void check(Requirements reqs) {
        if (reqs != null) {
            toBeChecked = new Vector();
            toBeChecked.add(reqs);
            startProcess();
        }
    }
    
    
    private  class PopupListener extends MouseAdapter /* popup menus onto tabs */ {
        private JDialogTMatrixManagement jdtmm;
        private JPopupMenu menu;
        private Requirements reqs;
        
        private JMenuItem draw, viewfs, viewg, check;
        
        public PopupListener(JDialogTMatrixManagement _jdtmm) {
            jdtmm = _jdtmm;
            createMenu();
        }
        
        public void mousePressed(MouseEvent e) {
            checkForPopup(e);
        }
        public void mouseReleased(MouseEvent e) {
            checkForPopup(e);
        }
        public void mouseClicked(MouseEvent e) {
            checkForPopup(e);
        }
        
        private void checkForPopup(MouseEvent e) {
            if(e.isPopupTrigger()) {
                Component c = e.getComponent();
                //System.out.println("e =" + e + " Component=" + c);
                updateMenu(e.getPoint());
                menu.show(c, e.getX(), e.getY());
            }
        }
        
        private void createMenu() {
            draw = createMenuItem("Draw corresponding design");
            viewfs = createMenuItem("View formal specification");
            viewg = createMenuItem("View reachability graph");
            check = createMenuItem("Check for satisfiability");
            
            menu = new JPopupMenu("TMatrix management");
            menu.add(draw);
            menu.add(viewfs);
            menu.add(viewg);
            
            menu.addSeparator();
            
            menu.add(check);
        }
        
        
        private JMenuItem createMenuItem(String s) {
            JMenuItem item = new JMenuItem(s);
            item.setActionCommand(s);
            item.addActionListener(listener);
            return item;
        }
        
        
        private void updateMenu(Point p) {
            //System.out.println("UpdateMenu index=" + index);
            jtable = jdtmm.getJTable();
            // None is selected -> everything is set to pointed row
            
            reqs = jdtmm.getRequirements(jtable.rowAtPoint(p));
            
            if (reqs == null) {
                draw.setEnabled(false);
                viewfs.setEnabled(false);
                viewg.setEnabled(false);
                check.setEnabled(false);
                return;
            }
            
            draw.setEnabled(reqs.tm!=null);
            viewfs.setEnabled(reqs.formalSpec != null);
            viewg.setEnabled(reqs.graphDot != null);
            check.setEnabled(true);
            
        }
        
        private Action listener = new AbstractAction() {
            
            public void actionPerformed(ActionEvent e) {
                JMenuItem item = (JMenuItem)e.getSource();
                String ac = item.getActionCommand();
                if(ac.equals("Draw corresponding design")) {
                    jdtmm.drawRequirements(reqs);
                } else if(ac.equals("View formal specification")) {
                    jdtmm.viewRequirementsFormalSpecification(reqs);
                } else if(ac.equals("View reachability graph")) {
                    jdtmm.viewRG(reqs);
                } else if(ac.equals("Check for satisfiability")) {
                    jdtmm.check(reqs);
                }
            }
        };
    }
}
