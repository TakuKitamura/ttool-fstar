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
 * Class JDialogGenAUTS
 * Dialog for managing remote processes call for generating AUT automatas
 * Creation: 20/10/2006
 * @version 1.0 20/10/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

import launcher.*;
import myutil.*;
import ui.*;

public class JDialogGenAUTS extends javax.swing.JDialog implements ActionListener, Runnable  {
    
    private static String path = "";
    private static boolean fc2Checked = false;
    private static boolean dotChecked = false;
    
    protected MainGUI mgui;
    
    protected String cmdCaesar, cmdBcgio;
    protected String fileName;
    protected String host;
    protected RshClient rshc;
    protected Thread t;
    protected int mode;
    
    protected static boolean dot = false;
    
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    //components
    protected JTextArea jta;
    protected JTextField jtf;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    
    protected JCheckBox makeDOT, fc2;
    
    protected SortedVector files;
    
    /** Creates new form  */
    public JDialogGenAUTS(Frame f, MainGUI _mgui, String title, String _cmdCaesar, String _cmdBcgio, String _fileName, String _host, String _path) {
        super(f, title, true);
        
        mgui = _mgui;
        
        cmdCaesar = _cmdCaesar;
        cmdBcgio = _cmdBcgio;
        fileName = _fileName;
        host = _host;
        path = _path;
        
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
        jp1.setBorder(new javax.swing.border.TitledBorder("Automata options"));
        //jp1.setPreferredSize(new Dimension(300, 150));
        
        // first line panel1
        //c1.gridwidth = 3;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        
        makeDOT = new JCheckBox("Save graphs in AUT and DOT format");
        makeDOT.setSelected(dot);
        makeDOT.addActionListener(this);
        makeDOT.setSelected(dotChecked);
        jp1.add(makeDOT, c1);

        fc2 = new JCheckBox("Save graphs in fc2 format");
        fc2.addActionListener(this);
        jp1.add(fc2, c1);
        fc2.setSelected(fc2Checked);
        
        c1.gridwidth = 1;
        jp1.add(new JLabel("Path where graphs shall be generated:"), c1);
        
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jtf = new JTextField(path, 25);
        jp1.add(jtf, c1);
        
        c.add(jp1, BorderLayout.NORTH);
        
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to start automata generation\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        c.add(jsp, BorderLayout.CENTER);
        
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
        if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        }
    }
    
    
    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        fc2Checked = fc2.isSelected();
        dotChecked = makeDOT.isSelected();
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
    
    public SortedVector getFiles() {
        return files;
    }
    
    public void run() {
        
        String cmd1 = "";
        String data;
        LinkedList ll;
        ListIterator iterator;
        path = jtf.getText();
        files =  new SortedVector();
        Point p;
        String taskName;
        String saveFileName;
        String spec;
        int i = 0;
        
        rshc = new RshClient(host);
        String basicFileName = fileName.substring(0, fileName.length()-4);
        
        try {
            jta.append("Generating LOTOS from TMLTasks\n");
            ll = mgui.generateAllLOTOS(path);
            
            if (ll == null) {
                jta.append("Generation failed\n");
            } else {
                jta.append("Generation of LOTOS specifications successful\n");
                iterator = ll.listIterator();

                while(iterator.hasNext()) {
                  taskName =(String)(iterator.next());
                  jta.append("Generating RG of name=" + taskName + "\n");
                  spec =(String)(iterator.next());
                  //jta.append("spec=" + (String)(iterator.next()));
                  jta.append("Sending LOTOS specification data\n");
                  saveFileName = basicFileName + "_" + taskName;
                  i ++;
                  rshc.deleteFile(basicFileName+".lot");
            
            // file data
            rshc.sendFileData(basicFileName+".lot", spec);

            //Removing old graph
            rshc.deleteFile(basicFileName + ".bcg");
            rshc.deleteFile(basicFileName + ".aut");
            rshc.deleteFile(basicFileName + ".aut.dot");
            
            // Command for RG
            cmd1 = cmdCaesar + " -english -warning -error ";
            cmd1 += basicFileName + ".lot";
            
            jta.append("Generating RG with cmd=" + cmd1 + "\n");
            data = processCmd(cmd1);

            // Getting graph
            jta.append("Getting RG from " + basicFileName + ".bcg" + "\n");

            // AUT  dot
            if (makeDOT.isSelected()) {
            jta.append("Converting to aut format and saving it in " + path + saveFileName + ".aut\n");

            cmd1 = cmdBcgio + " -bcg " + basicFileName + ".bcg" + " -aldebaran " + saveFileName + ".aut";
            data = processCmd(cmd1);
            data = rshc.getFileData(saveFileName + ".aut");
            mgui.gtm.saveInFile(new File(path, saveFileName + ".aut"), data);
            jta.append("Converting to dot format and saving it in " + path + saveFileName + ".dot\n");

            cmd1 = cmdBcgio + " -bcg " + basicFileName + ".bcg" + " -graphviz " + saveFileName + ".aut.dot";
            data = processCmd(cmd1);
            data = rshc.getFileData(saveFileName + ".aut.dot");
            mgui.gtm.saveInFile(new File(path, saveFileName + ".aut.dot"), data);
            }
            
            if (fc2.isSelected()) {
                String path = ConfigurationTTool.GGraphPath;
                if ((path == null) || (path.length() == 0)) {
                   path = new File("").getAbsolutePath();
                } 
                jta.append("Converting to fc2 format and saving it in " + path + saveFileName + ".fc2\n");
                //rshc.sendFileData(fileName + ".aut", data);
                cmd1 = cmdBcgio + " -bcg " + basicFileName + ".bcg" + " -fc2 " + saveFileName + ".fc2";
                data = processCmd(cmd1);
                data = rshc.getFileData(saveFileName + ".fc2");
                //System.out.println("Got data!");
                mgui.gtm.saveInFile(new File(path, saveFileName + ".fc2"), data);
            }
            jta.append("Done for " + taskName + "\n\n");
            }
            jta.append("\nAll done\n");
            }
            
        } catch (LauncherException le) {
            jta.append("LauncherException\n");
            jta.append(le.getMessage() + "\n");
            mode = 	STOPPED;
            setButtons();
            return;
        } catch (Exception e) {
            jta.append("Exception\n" + e.getMessage());
            mode = 	STOPPED;
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
    
    protected void setButtons() {
        switch(mode) {
            case NOT_STARTED:
                makeDOT.setEnabled(true);
                fc2.setEnabled(true);
                jtf.setEnabled(true);
                start.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                makeDOT.setEnabled(false);
                fc2.setEnabled(false);
                jtf.setEnabled(false);
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                break;
            case STOPPED:
            default:
                makeDOT.setEnabled(false);
                fc2.setEnabled(false);
                jtf.setEnabled(false);
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
        }
    }
}
