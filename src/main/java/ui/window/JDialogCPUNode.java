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

import help.HelpEntry;
import help.HelpManager;
import myutil.GraphicLib;
import myutil.TraceManager;
import tmltranslator.modelcompiler.ArchUnitMEC;
import ui.ColorManager;
import ui.MainGUI;
import ui.TGComboBoxWithHelp;
import ui.TGTextFieldWithHelp;
import ui.util.IconManager;
import ui.interactivesimulation.SimulationTransaction;
import ui.tmldd.TMLArchiCPUNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * Class JDialogCPUNode
 * Dialog for managing attributes of cpu nodes
 * Creation: 19/09/2007
 * @version 1.0 19/09/2007
 * @author Ludovic APVRILLE
 */
public class JDialogCPUNode extends JDialogBase implements ActionListener  {
    //private static String[] tracemodeTab = {"vcd trace", "VCI logger", "VCI stats"};
//    private static String[] tracemodeTab = {"VCI logger"};


    public static final String[] helpStrings = {"cpuname.html", "schedulingpolicy.html", "slicetime.html",
            "numbercores.html", "datasize.html", "pipelinesize.html", "taskswitchingtime.html",
            "misbrandingprediction.html", "cachemiss.html", "goidletime.html", "maxconsecutivecycles.html",
            "execi.html", "execc.html", "clockdivider.html", "encryption.html", "cpuextension.html", "operation.html"};


    protected MainGUI mgui;


    private boolean regularClose;

    private JPanel panel2, panel4, panel5;
 //   private Frame frame;
    private TMLArchiCPUNode node;

    private ArchUnitMEC MECType;

    protected JComboBox<String> tracemode;
 //   private static int selectedTracemode = 0;
    // Panel1
    protected TGTextFieldWithHelp nodeName;

    // Panel2
    protected TGTextFieldWithHelp sliceTime, nbOfCores, byteDataSize, pipelineSize, goIdleTime, maxConsecutiveIdleCycles,
            taskSwitchingTime, branchingPredictionPenalty, cacheMiss, clockRatio, execiTime, execcTime, monitored,
        operation;

    protected TGComboBoxWithHelp<String> schedulingPolicy, MECTypeCB, encryption;

    // Tabbed pane for panel1 and panel2
    private JTabbedPane tabbedPane;

    //
    private java.util.List<SimulationTransaction> transactions;

    //issue 183
    private List<JButton>  buttons;
    private List<HelpEntry> helpEntries;
    private JDialogTGComponentHelp cpuHelp;

    /* Creates new form  */
    public JDialogCPUNode(MainGUI _mgui, Frame _frame, String _title, TMLArchiCPUNode _node, ArchUnitMEC _MECType,
                          java.util.List<SimulationTransaction> _transactions) {
        super(_frame, _title, true);

        mgui = _mgui;
        node = _node;
        MECType = _MECType;
        transactions = _transactions;

        initComponents();
        pack();
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
        panel2.setBorder(new javax.swing.border.TitledBorder("Attributes"));
        panel2.setPreferredSize(new Dimension(500, 300));

        // Issue #41 Ordering of tabbed panes 
        tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();

        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;

        panel2.add(new JLabel("CPU name:"), c2);
        //-------
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        nodeName = new TGTextFieldWithHelp(node.getNodeName(), 30);
        nodeName.setEditable(true);
        nodeName.setFont(new Font("times", Font.PLAIN, 12));
        panel2.add(nodeName, c2);
        nodeName.makeEndHelpButton(helpStrings[0], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Scheduling policy:"), c2);

        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        schedulingPolicy = new TGComboBoxWithHelp<String>();
        schedulingPolicy.addItem("Round Robin");
        schedulingPolicy.addItem("Round Robin - Priority Based");
        schedulingPolicy.setSelectedIndex(node.getSchedulingPolicy());
        panel2.add(schedulingPolicy, c2);
        schedulingPolicy.makeEndHelpButton(helpStrings[1], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("Slice time (in microseconds):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        sliceTime = new TGTextFieldWithHelp(""+node.getSliceTime(), 15);
        panel2.add(sliceTime, c2);
        sliceTime.makeEndHelpButton(helpStrings[2], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("Nb of cores:"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfCores = new TGTextFieldWithHelp(""+node.getNbOfCores(), 15);
        panel2.add(nbOfCores, c2);
        nbOfCores.makeEndHelpButton(helpStrings[3], mgui, mgui.getHelpManager(), panel2, c2);

        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("Data size (in byte):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        byteDataSize = new TGTextFieldWithHelp(""+node.getByteDataSize(), 15);
        panel2.add(byteDataSize, c2);
        byteDataSize.makeEndHelpButton(helpStrings[4], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("Pipeline size (num. stages):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        pipelineSize = new TGTextFieldWithHelp(""+node.getPipelineSize(), 15);
        panel2.add(pipelineSize, c2);
        pipelineSize.makeEndHelpButton(helpStrings[5], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("Task switching time (in cycle):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        taskSwitchingTime = new TGTextFieldWithHelp(""+node.getTaskSwitchingTime(), 15);
        panel2.add(taskSwitchingTime, c2);
        pipelineSize.makeEndHelpButton(helpStrings[6], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("Mis-Branching prediction (in %):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        branchingPredictionPenalty = new TGTextFieldWithHelp(""+node.getBranchingPredictionPenalty(), 15);
        panel2.add(branchingPredictionPenalty, c2);
        branchingPredictionPenalty.makeEndHelpButton(helpStrings[7], mgui, mgui.getHelpManager(), panel2, c2);



        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("Cache-miss (in %):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        cacheMiss = new TGTextFieldWithHelp(""+node.getCacheMiss(), 15);
        panel2.add(cacheMiss, c2);
        cacheMiss.makeEndHelpButton(helpStrings[8], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("Go idle time (in cycle):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        goIdleTime = new TGTextFieldWithHelp(""+node.getGoIdleTime(), 15);
        panel2.add(goIdleTime, c2);
        goIdleTime.makeEndHelpButton(helpStrings[9], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("Max consecutive cycles before idle (in cycle):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        maxConsecutiveIdleCycles = new TGTextFieldWithHelp(""+node.getMaxConsecutiveIdleCycles(), 15);
        panel2.add(maxConsecutiveIdleCycles, c2);
        maxConsecutiveIdleCycles.makeEndHelpButton(helpStrings[10], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("EXECI execution time (in cycle):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        execiTime = new TGTextFieldWithHelp(""+node.getExeciTime(), 15);
        panel2.add(execiTime, c2);
        execiTime.makeEndHelpButton(helpStrings[11], mgui, mgui.getHelpManager(), panel2, c2);


        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("EXECC execution time (in cycle):"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        execcTime = new TGTextFieldWithHelp(""+node.getExeccTime(), 15);
        panel2.add(execcTime, c2);
        execcTime.makeEndHelpButton(helpStrings[12], mgui, mgui.getHelpManager(), panel2, c2);



        c2.gridwidth = 1;
        //issue 183
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        panel2.add(new JLabel("Clock divider:"), c2);
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        clockRatio = new TGTextFieldWithHelp(""+node.getClockRatio(), 15);
        panel2.add(clockRatio, c2);
        execcTime.makeEndHelpButton(helpStrings[13], mgui, mgui.getHelpManager(), panel2, c2);



        // Code generation
        panel4 = new JPanel();
        panel4.setLayout( gridbag4 );
        panel4.setBorder( new javax.swing.border.TitledBorder("Attributes") );
        panel4.setPreferredSize( new Dimension(500, 300) );
        c4.gridwidth = 1;
        c4.gridheight = 1;
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(new JLabel("Encryption:"), c4);
        //c4.gridwidth = GridBagConstraints.REMAINDER;
        encryption = new TGComboBoxWithHelp<String>();
        encryption.addItem("None");
        encryption.addItem("Software Encryption");
        encryption.addItem("Hardware Security Module");
        encryption.setSelectedIndex(node.getEncryption());
        panel4.add(encryption, c4);
        encryption.makeEndHelpButton(helpStrings[14], mgui, mgui.getHelpManager(), panel4, c4);


        c4.weighty = 1.0;
        c4.weightx = 1.0;
        
        // operation
        c4.gridwidth = 1;
        panel4.add(new JLabel("Operation:"), c4);
        //c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        operation = new TGTextFieldWithHelp(""+node.getOperation(), 15);
        panel4.add(operation, c4);
        operation.makeEndHelpButton(helpStrings[16], mgui, mgui.getHelpManager(), panel4, c4);


        c4.weighty = 1.0;
        c4.weightx = 1.0;
        // extension constructs
        c4.gridwidth = 1;
        panel4.add(new JLabel("CPU Extension Construct:"), c4);
        //c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        MECTypeCB = new TGComboBoxWithHelp<String>( ArchUnitMEC.stringTypes );
        if( MECType == null )   {
            MECTypeCB.setSelectedIndex( 0 );
        }
        else    {
            MECTypeCB.setSelectedIndex( MECType.getIndex() );
        }
        MECTypeCB.addActionListener(this);
        panel4.add( MECTypeCB, c4);
        MECTypeCB.makeEndHelpButton(helpStrings[15], mgui, mgui.getHelpManager(), panel4, c4);


        TraceManager.addDev("Transactions size=" + transactions.size());
        if (transactions.size()!=0) {
            TraceManager.addDev("On going simulation");

            panel5 = new JPanel();
            panel5.setPreferredSize(new Dimension(400,300));
            MyFrame simulationFrame = new MyFrame();
            TraceManager.addDev("Adding simulation frame");
            simulationFrame.setPreferredSize(new Dimension(400,300));
            panel5.add(simulationFrame,c4);
            tabbedPane.addTab("Simulation Transactions", panel5);
            //Draw from transactions
        }
        else {
            tabbedPane.addTab( "Main attributes", panel2 );
            tabbedPane.addTab( "Security & operation type", panel4 );
            tabbedPane.setSelectedIndex(0);
        }
        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.BOTH;
       /* c.add(panel2, c0);
        c.add(panel4, c0);*/
        c.add( tabbedPane, c0 );

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        
        initButtons(c0, c, this);
    }

    public void actionPerformed(ActionEvent evt)  {

        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
    }

    public void closeDialog() {
        //TraceManager.addDev("Save and close");
        regularClose = true;
        MECType = ArchUnitMEC.Types.get( MECTypeCB.getSelectedIndex() );
        dispose();
        if ((cpuHelp != null) && cpuHelp.isVisible()) {
            cpuHelp.setVisible(false);
        }
    }

    public void cancelDialog() {

        //TraceManager.addDev("Cancel dialog");
        dispose();
        if ((cpuHelp != null) && cpuHelp.isVisible()) {
            cpuHelp.setVisible(false);
        }
    }

    public boolean isRegularClose() {
        //TraceManager.addDev("regularclose=" + regularClose);
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

    public int getMonitored() {
        return tracemode.getSelectedIndex();
        //return monitored.getText();
    }

    public String getOperation() {
        return operation.getText();
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

    
    class MyFrame extends JPanel implements MouseMotionListener, MouseListener{
        Map<Range, String> toolMap = new HashMap<Range, String>();
        public MyFrame(){
            ToolTipManager.sharedInstance().setInitialDelay(0);

            addMouseMotionListener(this);
            addMouseListener(this);
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            //do something
        }

        @Override
        public void mouseMoved(MouseEvent e){
            drawToolTip(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            drawToolTip(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            drawToolTip(e);
        }

        @Override
        public void mouseExited(MouseEvent e){
            ///
        }

        @Override
        public void mouseReleased(MouseEvent e){
            ///
        }

        @Override
        public void mouseEntered(MouseEvent e){
        }

        public void drawToolTip(MouseEvent e){

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

        // If simulation is ongoing
        public void paint(Graphics g) {

            //Draw Axis
            g.drawLine(70,0,70,300);
            int i=0;
            java.util.List<String> tasks=new ArrayList<String>();
            Map<String, java.util.List<SimulationTransaction>> tasktrans = new HashMap<String, java.util.List<SimulationTransaction>>();
           // double incr=0.0;
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
            TreeSet<Integer> tree = new TreeSet<>();
            for(int j = 0; j < tranList.size(); j++) {
                tree.add(Integer.valueOf(tranList.get(j).startTime));
            }
            Integer[] arr = new Integer[tree.size()];
            tree.toArray(arr);
            for (int t = 0; t < tranList.size(); t ++){
                for (int k = 0; k < arr.length; k++){
                    if(tranList.get(t).startTime.equals(String.valueOf(arr[k]))){
                        tranList.get(t).index = k;
                        break;
                    }
                }
            }

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
                    else if (tran.command.contains("Delay")){
                        g.setColor(ColorManager.TML_PORT_CHANNEL);
                        commandName="DL";
                    }
                    else if (tran.command.contains("Execi")){
                        commandName="EX";
                        g.setColor(ColorManager.EXEC);
                    }
                    else {
                        continue;
                    }
                    int start = 30*tran.index+70;
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
