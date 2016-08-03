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
 * Class JDialogCPUNode
 * Dialog for managing attributes of cpu nodes
 * Creation: 19/09/2007
 * @version 1.0 19/09/2007
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.math.*;
import java.awt.event.*;
import javax.swing.*;
import ui.*;
import ui.tmlcd.*;
import java.util.*;
import tmltranslator.ctranslator.*;
import ui.*;
import ui.tmldd.*;
import ui.interactivesimulation.*;

import myutil.*;

public class JDialogCPUNode extends javax.swing.JDialog implements ActionListener  {

    private boolean regularClose;

    private JPanel panel2, panel4, panel5;
    private Frame frame;
    private TMLArchiCPUNode node;

    private ArchUnitMEC MECType;


    // Panel1
    protected JTextField nodeName;

    // Panel2
    protected JTextField sliceTime, nbOfCores, byteDataSize, pipelineSize, goIdleTime, maxConsecutiveIdleCycles, taskSwitchingTime, branchingPredictionPenalty, cacheMiss, clockRatio, execiTime, execcTime;
    protected JComboBox schedulingPolicy, MECTypeCB, encryption;

    // Tabbed pane for panel1 and panel2
    private JTabbedPane tabbedPane;

    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    public ArrayList<SimulationTransaction> transactions;
    /** Creates new form  */
    public JDialogCPUNode(Frame _frame, String _title, TMLArchiCPUNode _node, ArchUnitMEC _MECType, ArrayList<SimulationTransaction> _transactions) {
        super(_frame, _title, true);
        frame = _frame;
        node = _node;
        MECType = _MECType;
	transactions = _transactions;
        initComponents();
        myInitComponents();
        pack();
    }

    private void myInitComponents() {
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagLayout gridbag4 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        //GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        GridBagConstraints c4 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("CPU attributes"));
        panel2.setPreferredSize(new Dimension(400, 300));

        tabbedPane = new JTabbedPane();

        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("CPU name:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        nodeName = new JTextField(node.getNodeName(), 30);
        nodeName.setEditable(true);
        nodeName.setFont(new Font("times", Font.PLAIN, 12));
        panel2.add(nodeName, c2);

        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Scheduling policy:"), c2);

        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        schedulingPolicy = new JComboBox();
        schedulingPolicy.addItem("Round Robin");
        schedulingPolicy.addItem("Round Robin - Priority Based");
        schedulingPolicy.setSelectedIndex(node.getSchedulingPolicy());
        panel2.add(schedulingPolicy, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Slice time (in microseconds):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        sliceTime = new JTextField(""+node.getSliceTime(), 15);
        panel2.add(sliceTime, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Nb of cores:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfCores = new JTextField(""+node.getNbOfCores(), 15);
        panel2.add(nbOfCores, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Data size (in byte):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        byteDataSize = new JTextField(""+node.getByteDataSize(), 15);
        panel2.add(byteDataSize, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Pipeline size (num. stages):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        pipelineSize = new JTextField(""+node.getPipelineSize(), 15);
        panel2.add(pipelineSize, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Task switching time (in cycle):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        taskSwitchingTime = new JTextField(""+node.getTaskSwitchingTime(), 15);
        panel2.add(taskSwitchingTime, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Mis-Branching prediction (in %):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        branchingPredictionPenalty = new JTextField(""+node.getBranchingPredictionPenalty(), 15);
        panel2.add(branchingPredictionPenalty, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Cache-miss (in %):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        cacheMiss = new JTextField(""+node.getCacheMiss(), 15);
        panel2.add(cacheMiss, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Go idle time (in cycle):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        goIdleTime = new JTextField(""+node.getGoIdleTime(), 15);
        panel2.add(goIdleTime, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Max consecutive cycles before idle (in cycle):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        maxConsecutiveIdleCycles = new JTextField(""+node.getMaxConsecutiveIdleCycles(), 15);
        panel2.add(maxConsecutiveIdleCycles, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("EXECI execution time (in cycle):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        execiTime = new JTextField(""+node.getExeciTime(), 15);
        panel2.add(execiTime, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("EXECC execution time (in cycle):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        execcTime = new JTextField(""+node.getExeccTime(), 15);
        panel2.add(execcTime, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Clock diviser:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        clockRatio = new JTextField(""+node.getClockRatio(), 15);
        panel2.add(clockRatio, c2);

        // Code generation
        panel4 = new JPanel();
        panel4.setLayout( gridbag4 );
        panel4.setBorder( new javax.swing.border.TitledBorder("Code generation") );
        panel4.setPreferredSize( new Dimension(400, 300) );
        c4.gridwidth = 1;
        c4.gridheight = 1;
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.fill = GridBagConstraints.HORIZONTAL;
        /*c4.fill = GridBagConstraints.BOTH;
          c4.gridheight = 3;
          panel4.add( new JLabel(" "), c4 );
          c4.gridwidth = 1;
          c4.fill = GridBagConstraints.HORIZONTAL;
          c4.anchor = GridBagConstraints.CENTER;*/
        panel4.add(new JLabel("Encryption:"), c4);
        c4.gridwidth = GridBagConstraints.REMAINDER;
        encryption = new JComboBox();
        encryption.addItem("None");
        encryption.addItem("Software Encryption");
        encryption.addItem("Hardware Security Module");
        encryption.setSelectedIndex(node.getEncryption());
        panel4.add(encryption, c4);
        c4.gridwidth = 1;
        panel4.add(new JLabel("CPU Extension Construct:"), c4);
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        MECTypeCB = new JComboBox( ArchUnitMEC.stringTypes );
        if( MECType == null )   {
            MECTypeCB.setSelectedIndex( 0 );
        }
        else    {
            MECTypeCB.setSelectedIndex( MECType.getIndex() );
        }
        MECTypeCB.addActionListener(this);
        panel4.add( MECTypeCB, c4);
	if (transactions.size()!=0) {
		panel5=new JPanel();
		panel5.setPreferredSize(new Dimension(400,300));
		MyFrame f =new MyFrame();
		f.setPreferredSize(new Dimension(400,300));
		panel5.add(f,c4);
		tabbedPane.addTab("Simulation Transactions", panel5);
	//Draw from transactions
 	}

        tabbedPane.addTab( "Simulation", panel2 );
        tabbedPane.addTab( "Code generation", panel4 );

        tabbedPane.setSelectedIndex(0);

        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.BOTH;
        /*c.add(panel2, c0);
          c.add(panel4, c0);*/
        c.add( tabbedPane, c0 );

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        closeButton = new JButton("Save and Close", IconManager.imgic25);
        //closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        c.add(closeButton, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        c.add(cancelButton, c0);
    }

    public void actionPerformed(ActionEvent evt)  {
        /* if (evt.getSource() == typeBox) {
           boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
           initialValue.setEnabled(b);
           return;
           }*/


        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
    }

    public void closeDialog() {
        regularClose = true;
        MECType = ArchUnitMEC.Types.get( MECTypeCB.getSelectedIndex() );
        dispose();
    }

    public void cancelDialog() {
        dispose();
    }

    public boolean isRegularClose() {
        return regularClose;
    }

    public String getNodeName() {
        return nodeName.getText();
    }

    public String getSliceTime() {
        return sliceTime.getText();
    }

    public String getNbOfCores() {
        return nbOfCores.getText();
    }

    public String getByteDataSize() {
        return byteDataSize.getText();
    }

    public String getPipelineSize(){
        return pipelineSize.getText();
    }

    public String getGoIdleTime(){
        return goIdleTime.getText();
    }

    public String getMaxConsecutiveIdleCycles(){
        return maxConsecutiveIdleCycles.getText();
    }

    public String getExeciTime(){
        return execiTime.getText();
    }

    public String getExeccTime(){
        return execcTime.getText();
    }

    public String getTaskSwitchingTime(){
        return taskSwitchingTime.getText();
    }

    public String getBranchingPredictionPenalty(){
        return branchingPredictionPenalty.getText();
    }

    public String getCacheMiss(){
        return cacheMiss.getText();
    }

    public String getClockRatio(){
        return clockRatio.getText();
    }

    public int getSchedulingPolicy() {
        return schedulingPolicy.getSelectedIndex();
    }

    public int getEncryption(){
        return encryption.getSelectedIndex();
    }
    public ArchUnitMEC getMECType()     {
        return MECType;
    }
  class Range {
	int xi, yi, xf, yf;
	public Range(int xa, int ya, int xb, int yb){
	    xi=xa;
	    yi=ya;
	    xf=xb;
	    yf=yb;
	}
	public boolean inRange(int x, int y){
	    if (y>yi && y<yf){
	    	if (x>xi && x<xf){
		    return true;
	    	}
	    }
	    return false;
	}
  }
  class MyFrame extends JPanel implements MouseMotionListener{
	Map<Range, String> toolMap = new HashMap<Range, String>();
	public MyFrame(){
	    addMouseMotionListener(this);
	}
	public void mouseDragged(MouseEvent e) {
       //do something
        }
    	public void mouseMoved(MouseEvent e) {
	    setToolTipText(null);
	    for (Range r:toolMap.keySet()){
		int x=e.getX();
		int y=e.getY();
		if (r.inRange(x,y)){
		    setToolTipText(toolMap.get(r));
		    break;
		}
	    }
    	}
    public void paint(Graphics g) {
	
	//Draw Axis
	g.drawLine(70,0,70,300);
	int i=0;
	java.util.List<String> tasks=new ArrayList<String>();
	Map<String, java.util.List<SimulationTransaction>> tasktrans = new HashMap<String, java.util.List<SimulationTransaction>>();
	double incr=0.0;
	BigDecimal maxtime = new BigDecimal("0");
	BigDecimal mintime=new BigDecimal("9999999999999999999999999999");
	//Colors
	//Exec- ColorManager.EXEC
	//Channel - TML_PORT_CHANNEL
	Collections.sort(transactions, new Comparator<SimulationTransaction>(){
     	public int compare(SimulationTransaction o1, SimulationTransaction o2){
	     BigDecimal t1 = new BigDecimal(o1.startTime);
	     BigDecimal t2 = new BigDecimal(o2.startTime);
	     return t1.compareTo(t2);
	     }
	});
	ArrayList<SimulationTransaction> tranList = new ArrayList<SimulationTransaction>(transactions);
    	for (SimulationTransaction st: transactions){
	    if (!tasks.contains(st.taskName)){
		tasks.add(st.taskName);
		java.util.List<SimulationTransaction> tmp = new ArrayList<SimulationTransaction>();
		tasktrans.put(st.taskName, tmp);
	    }
	    if (tasktrans.get(st.taskName).size()==0 || !(tasktrans.get(st.taskName).get(tasktrans.get(st.taskName).size()-1).command+tasktrans.get(st.taskName).get(tasktrans.get(st.taskName).size()-1).startTime).equals(st.command+st.startTime)){
		tasktrans.get(st.taskName).add(st);
	    }
	    else {
		tranList.remove(st);
	    }
	    BigDecimal start = new BigDecimal(st.startTime);
	    BigDecimal end = new BigDecimal(st.endTime);
	    if (start.compareTo(mintime)==-1){
		mintime=start;
	    }
	    if (end.compareTo(maxtime)==1){
		maxtime=end;
	    }
	}
	String commandName="";
	for (String s:tasks){
	    i++;
	    g.drawString(s.split("__")[1],0, i*50+50);
	    for (SimulationTransaction tran: tasktrans.get(s)){
		//Fill rectangle with color
		if (tran.command.contains("Read")) {
		    commandName="RD";
		    g.setColor(ColorManager.TML_PORT_CHANNEL);
		}
		else if (tran.command.contains("Write")){
		    g.setColor(ColorManager.TML_PORT_CHANNEL);
		    commandName="WR";
		}
		else if (tran.command.contains("Send")){
		    g.setColor(ColorManager.TML_PORT_EVENT);
		    commandName="SND";
		}
		else if (tran.command.contains("Wait")){
		    g.setColor(ColorManager.TML_PORT_EVENT);
		    commandName="WT";
		}
		else if (tran.command.contains("Request")){
		    g.setColor(ColorManager.TML_PORT_REQUEST);
		    commandName="REQ";
		}
		else {
		    commandName="EX";
		    g.setColor(ColorManager.EXEC);
		}
		int start = 30*tranList.indexOf(tran)+70;
		g.fillRect(start, i*50+40, 30, 20);
		g.setColor(Color.black);
		g.drawRect(start, i*50+40, 30, 20);
		g.drawString(commandName, start+2, i*50+55);
		toolMap.put(new Range(start, i*50+40, start+30, i*50+40+20), tran.command+ " Time "+ tran.startTime + "-" + tran.endTime);
	    }
	}
	//g.drawString(Integer.toString(mintime), 70, 250);
	//g.drawString(Integer.toString(maxtime), 350, 250);
     // g.setColor(Color.red);
      //g.fillRect(10,10,100,100);
    }
  }
}
