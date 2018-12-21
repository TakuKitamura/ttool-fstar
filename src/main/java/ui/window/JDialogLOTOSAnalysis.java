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

  import launcher.LauncherException;
  import launcher.RshClient;
  import myutil.FileUtils;
  import myutil.ScrolledJTextArea;
  import ui.util.IconManager;
  import ui.MainGUI;

  import javax.swing.*;
  import java.awt.*;
  import java.awt.event.ActionEvent;
  import java.awt.event.ActionListener;
  
/**
   * Class JDialogLOTOSAnalysis
   * Dialog for managing the syntax analysis of LOTOS specifications
   * Creation: 10/03/2006
   * @version 1.0 10/03/2006
   * @author Ludovic APVRILLE
 */
  public class JDialogLOTOSAnalysis extends JDialog implements ActionListener, Runnable  {
      private static boolean warningChecked = false;
      
      protected MainGUI mgui;
      
      protected String cmdCaesar;
      protected String fileName;
      protected String spec;
      protected String host;
      protected int mode;
      protected RshClient rshc;
      protected Thread t;
      
      protected int simuTime = 0;
      
      protected final static int NOT_STARTED = 1;
      protected final static int STARTED = 2;
      protected final static int STOPPED = 3;
      
      //components
      protected JTextArea jta;
      protected JButton start;
      protected JButton stop;
      protected JButton close;
      
      protected JCheckBox warning;
      
      /* Creates new form  */
      public JDialogLOTOSAnalysis(Frame f, MainGUI _mgui, String title, String _cmdCaesar, String _fileName, String _spec, String _host) {
          super(f, title, true);
          
          mgui = _mgui;
          
          cmdCaesar = _cmdCaesar;
          fileName = _fileName;
          spec = _spec;
          host = _host;
          
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
          jp1.setBorder(new javax.swing.border.TitledBorder("Analysis options"));
          //jp1.setPreferredSize(new Dimension(300, 150));
          
          // first line panel1
          //c1.gridwidth = 3;
          c1.gridheight = 1;
          c1.weighty = 1.0;
          c1.weightx = 1.0;
          c1.gridwidth = GridBagConstraints.REMAINDER; //end row
          c1.fill = GridBagConstraints.BOTH;
          c1.gridheight = 1;
          
          warning = new JCheckBox("Show warnings");
          warning.addActionListener(this);
          
          jp1.add(warning, c1);
          warning.setSelected(warningChecked);
          
          c.add(jp1, BorderLayout.NORTH);
          
          jta = new ScrolledJTextArea();
          jta.setEditable(false);
          jta.setMargin(new Insets(10, 10, 10, 10));
          jta.setTabSize(3);
          jta.append("Select options and then, click on 'start' to start analysis\n");
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
          warningChecked = warning.isSelected();
          dispose();
      }
      
      public void stopProcess() {
          try {
              rshc.stopCommand();
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
      
  
      public void run() {
          
          String cmd1 = "";
          String data;
          int id = 0;
          rshc = new RshClient(host);
		  RshClient rshctmp = rshc;

          try {
            id = rshc.getId();
            fileName = FileUtils.addBeforeFileExtension(fileName, "_" + id);
              jta.append("Sending LOTOS specification data in file " + fileName + "\n");
              

              
              // file data
              rshc.sendFileData(fileName, spec);
              
              // Command for types
              if (warning.isSelected()) {
                  cmd1 = cmdCaesar + ".adt -english " + fileName;
              } else {
                  cmd1 = cmdCaesar + ".adt -warning -english " + fileName;
              }
              jta.append("\nGenerating types\n");
              data = processCmd(cmd1);
              jta.append(data);
              
              // Command for analysis
              if (warning.isSelected()) {
                  cmd1 = cmdCaesar + " -english -analysis -error " + fileName;
              } else {
                  cmd1 = cmdCaesar + " -warning -english -analysis -error " + fileName;
              }
              cmd1 += " 2>&1 ";
              jta.append("\nAnalysing LOTOS specification\n");
              data = processCmd(cmd1);
              jta.append(data);
              rshc.deleteFile(fileName);
              rshc.freeId(id);
              
              jta.append("\nAnalysis done\n");
              
          } catch (LauncherException le) {
              jta.append(le.getMessage() + "\n");
              mode = 	STOPPED;
              setButtons();
			  try{
				if (rshctmp != null) {
					rshctmp.freeId(id);
				}
			} catch (LauncherException leb) {}
              return;
          } catch (Exception e) {
              mode = 	STOPPED;
              setButtons();
			  try{
				if (rshctmp != null) {
					rshctmp.freeId(id);
				}
			} catch (LauncherException leb) {}
              return;
          }
          
          mode = STOPPED;
          setButtons();
      }
      
      protected String processCmd(String cmd) throws LauncherException {
          rshc.setCmd(cmd);
          String s = null;
          rshc.sendExecuteCommandRequest();
          s = rshc.getDataFromProcess();
          return s;
      }
      
      protected void setButtons() {
          switch(mode) {
              case NOT_STARTED:
                  warning.setEnabled(true);
                  start.setEnabled(true);
                  stop.setEnabled(false);
                  close.setEnabled(true);
                  getGlassPane().setVisible(false);
                  break;
              case STARTED:
                  warning.setEnabled(false);
                  start.setEnabled(false);
                  stop.setEnabled(true);
                  close.setEnabled(false);
                  getGlassPane().setVisible(true);
                  break;
              case STOPPED:
              default:
                  warning.setEnabled(false);
                  start.setEnabled(false);
                  stop.setEnabled(false);
                  close.setEnabled(true);
                  getGlassPane().setVisible(false);
                  break;
          }
      }
  }
