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
* Class JDialogScheduling
* Dialog for managing scheduling options
* Creation: 22/02/2008
* @version 1.0 22/02/2008
* @author Ludovic APVRILLE
* @see
*/

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import launcher.*;
import myutil.*;
import ui.*;

public class JDialogScheduling extends javax.swing.JDialog implements ActionListener, Runnable  {
	private static boolean sampleChecked=false, channelChecked= false, eventChecked = false, requestChecked = false, execChecked = false, busTransferChecked = false, schedulingChecked = false, taskStateChecked = false, channelStateChecked = false, branchingChecked = false, terminateCPUChecked = false, terminateCPUsChecked = true, clockedChecked = false, clockedEndChecked = false, countTickChecked=false, maxCountTickChecked=false, randomTaskChecked = true;
	private static String tickIntervalValue = "1", maxCountTickValue = "1000";
	
	protected MainGUI mgui;
	
	protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    int mode;
	
	//components
	protected JTextArea jta;
	protected JButton start;
    protected JButton stop;
    protected JButton close;
	protected JScrollPane jsp;
	protected JButton checkAll, uncheckAll;
	
	protected JCheckBox sample, channel, event, request, exec, busTransfer, scheduling, taskState, channelState, branching, terminateCPU, terminateCPUs, clocked, endClocked, countTick, maxCountTick, randomTask;
	protected JTextField tickIntervalValueText, maxCountTickText;
	
	public boolean cancelled = false;
	
	private Thread t;
    private boolean go = false;
    private ProcessThread pt;
    private boolean hasError = false;
	
	private int generator;
	
	/** Creates new form  */
	public JDialogScheduling(Frame f, MainGUI _mgui, String title, int _generator) {
		super(f, title, true);
		
		mgui = _mgui;
		generator = _generator;
		
		initComponents();
		myInitComponents();
		pack();
		
		//getGlassPane().addMouseListener( new MouseAdapter() {});
		//getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
		jp1.setBorder(new javax.swing.border.TitledBorder("RG generation options"));
		//jp1.setPreferredSize(new Dimension(300, 150));
		
		// first line panel1
		//c1.gridwidth = 3;
		c1.gridheight = 1;
		c1.weighty = 1.0;
		c1.weightx = 1.0;
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		c1.fill = GridBagConstraints.BOTH;
		c1.gridheight = 1;
		
		sample = new JCheckBox("Show sample read / write in channels");
		sample.addActionListener(this);
		jp1.add(sample, c1);
		sample.setSelected(sampleChecked);
		
		channel = new JCheckBox("Show read / write in channels");
		channel.addActionListener(this);
		jp1.add(channel, c1);
		channel.setSelected(channelChecked);
		
		event = new JCheckBox("Show send / notify events");
		event.addActionListener(this);
		jp1.add(event, c1);
		event.setSelected(eventChecked);
		
		request = new JCheckBox("Show send / accept requests");
		request.addActionListener(this);
		jp1.add(request, c1);
		request.setSelected(requestChecked);
		
		exec = new JCheckBox("Show EXEC instructions");
		exec.addActionListener(this);
		jp1.add(exec, c1);
		exec.setSelected(execChecked);
		
		busTransfer = new JCheckBox("Show bus transfers");
		busTransfer.addActionListener(this);
		jp1.add(busTransfer, c1);
		busTransfer.setSelected(busTransferChecked);
		
		scheduling = new JCheckBox("Show scheduling");
		scheduling.addActionListener(this);
		jp1.add(scheduling, c1);
		scheduling.setSelected(schedulingChecked);
		
		taskState = new JCheckBox("Show task state");
		taskState.addActionListener(this);
		jp1.add(taskState, c1);
		taskState.setSelected(taskStateChecked);
		
		channelState = new JCheckBox("Show channel state");
		channelState.addActionListener(this);
		jp1.add(channelState, c1);
		channelState.setSelected(channelStateChecked);
		
		branching = new JCheckBox("Show branching penalty");
		branching.addActionListener(this);
		jp1.add(branching, c1);
		branching.setSelected(branchingChecked);
		
		terminateCPU = new JCheckBox("Show when each CPU is blocked");
		terminateCPU.addActionListener(this);
		jp1.add(terminateCPU, c1);
		terminateCPU.setSelected(terminateCPUChecked);
		
		terminateCPUs = new JCheckBox("Show when all CPUs are terminated");
		terminateCPUs.addActionListener(this);
		jp1.add(terminateCPUs, c1);
		terminateCPUs.setSelected(terminateCPUsChecked);
		
		clocked = new JCheckBox("Tick begin information");
		clocked.addActionListener(this);
		jp1.add(clocked, c1);
		clocked.setSelected(clockedChecked);
		
		tickIntervalValueText = new JTextField(tickIntervalValue, 10);
		tickIntervalValueText.addActionListener(this);
		jp1.add(tickIntervalValueText, c1);
		tickIntervalValueText.setEnabled(clocked.isSelected());
		
		endClocked = new JCheckBox("Tick end information");
		endClocked.addActionListener(this);
		jp1.add(endClocked, c1);
		endClocked.setSelected(clockedEndChecked);
		
		countTick = new JCheckBox("Count ticks before end");
		countTick.addActionListener(this);
		jp1.add(countTick, c1);
		countTick.setSelected(countTickChecked);
		
		maxCountTick = new JCheckBox("Maximum nb of ticks:");
		maxCountTick.addActionListener(this);
		jp1.add(maxCountTick, c1);
		maxCountTick.setSelected(maxCountTickChecked);
		
		maxCountTickText = new JTextField(maxCountTickValue, 10);
		maxCountTickText.addActionListener(this);
		jp1.add(maxCountTickText, c1);
		maxCountTickText.setEnabled(maxCountTick.isSelected());
		
		randomTask = new JCheckBox("First task is a random task");
		randomTask.addActionListener(this);
		jp1.add(randomTask, c1);
		randomTask.setSelected(randomTaskChecked);
		
		checkAll = new JButton("Check All");
		checkAll.addActionListener(this);
		jp1.add(checkAll, c1);
		
		uncheckAll = new JButton("Uncheck All");
		uncheckAll.addActionListener(this);
		jp1.add(uncheckAll, c1);
		
		c.add(jp1, BorderLayout.NORTH);
		
		jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
		if (generator == MainGUI.LOTOS) {
			jta.append("Select options and then, click on 'start' for generating a LOTOS specification\n");
		} else {
			jta.append("Select options and then, click on 'start' for generating a TIF specification\n");
		}
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        c.add(jsp, BorderLayout.CENTER);
		
		start = new JButton("Start", IconManager.imgic53);
		stop = new JButton("Stop", IconManager.imgic27);
		close = new JButton("Close", IconManager.imgic37);
		
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
		
		cancelled = true;
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
        } else if (evt.getSource() == checkAll) {
			checkAll(true);
		} else if (evt.getSource() == uncheckAll) {
			checkAll(false);
		} else if (evt.getSource() == maxCountTick) {
			maxCountTickText.setEnabled(maxCountTick.isSelected());
		} else if (evt.getSource() == clocked) {
			tickIntervalValueText.setEnabled(clocked.isSelected());
		}
	}
	
	public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
    }
	
	public void stopProcess() {
        go = false;
        if (pt != null) {
            pt.stopProcess();
        }
        mode = 	STOPPED;
        setButtons();
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
	
	public void run() {
        String cmd;
		
		boolean debug, choices;
		int nb = 0;
		int nb1;
		
		int size=0, size1;
		
		jta.append("Generating TIF specification, please wait\n");
		sampleChecked = sample.isSelected();
		channelChecked = channel.isSelected();
		eventChecked = event.isSelected();
		requestChecked = request.isSelected();
		execChecked = exec.isSelected();
		busTransferChecked = busTransfer.isSelected();
		schedulingChecked = scheduling.isSelected();
		taskStateChecked = taskState.isSelected();
		channelStateChecked = channelState.isSelected();
		branchingChecked = branching.isSelected();
		terminateCPUChecked = terminateCPU.isSelected();
		terminateCPUsChecked = terminateCPUs.isSelected();
		clockedChecked = clocked.isSelected();
		tickIntervalValue = tickIntervalValueText.getText();
		clockedEndChecked = endClocked.isSelected();
		countTickChecked = countTick.isSelected();
		maxCountTickChecked = maxCountTick.isSelected();
		maxCountTickValue = maxCountTickText.getText();
		randomTaskChecked = randomTask.isSelected();
		debug = mgui.gtm.translateTMLMapping(getSample(), getChannel(), getEvent(), getRequest(), getExec(), getBusTransfer(), getScheduling(), getTaskState(), getChannelState(), getBranching(), getTerminateCPU(), getTerminateCPUs(), getClocked(), getTickIntervalValue(), getEndClocked(), getCountTick(), getMaxCountTick(), getMaxCountTickValue(), getRandomTask());
		if (!debug) {
			setError();
			jta.append("*** TIF specification generation failed: ***\n"); 
			jta.append("the TML mapping contains several errors\n\n");
		} else {
			hasError = false;
			jta.append("TIF specification generated\n");
			
			if (generator == MainGUI.LOTOS) {
				cmd = "LOTOS";
				jta.append("Generating " + cmd + " specification, please wait\n");
				mgui.generateFullLOTOS();
				jta.append(cmd + " specification generated\n");
			}
			
			jta.append("\n");
		}
		
		jta.append("\nReady to process next command\n");
		cancelled = false;
		checkMode();
		setButtons();
	}
	
	/*public boolean hasToContinue() {
		return (go == true);
	}*/
	
	protected void checkMode() {
		mode = NOT_STARTED;
	}
	
	/*public void appendOut(String s) {
		jta.append(s);
	}*/
	
	public void setError() {
		hasError = true;
	}
	
	public boolean hasError() {
		return hasError;
	}
	
	protected void setButtons() {
		switch(mode) {
		case NOT_STARTED:
			start.setEnabled(true);
			stop.setEnabled(false);
			close.setEnabled(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			getGlassPane().setVisible(false);
			break;
		case STARTED:
			start.setEnabled(false);
			stop.setEnabled(true);
			close.setEnabled(false);
			getGlassPane().setVisible(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			break;
		case STOPPED:
		default:
			start.setEnabled(false);
			stop.setEnabled(false);
			close.setEnabled(true);
			getGlassPane().setVisible(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			break;
		}
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void checkAll(boolean b) {
		sample.setSelected(b);
		channel.setSelected(b);
		event.setSelected(b);
		request.setSelected(b);
		exec.setSelected(b);
		busTransfer.setSelected(b);
		scheduling.setSelected(b);
		taskState.setSelected(b);
		channelState.setSelected(b);
		branching.setSelected(b);
		terminateCPU.setSelected(b);
		terminateCPUs.setSelected(b);
		clocked.setSelected(b);
		endClocked.setSelected(b);
		countTick.setSelected(b);
		maxCountTick.setSelected(b);
		randomTask.setSelected(b);
		maxCountTickText.setEnabled(maxCountTick.isSelected());
		tickIntervalValueText.setEnabled(clocked.isSelected());
	}
	
	public boolean getSample() {
		return sampleChecked;
	}
	
	public boolean getChannel() {
		return channelChecked;
	}
	
	public boolean getEvent() {
		return eventChecked;
	}
	
	public boolean getRequest() {
		return requestChecked;
	}
	
	public boolean getExec() {
		return execChecked;
	}
	
	public boolean getBusTransfer() {
		return busTransferChecked;
	}
	
	public boolean getScheduling() {
		return schedulingChecked;
	}
	
	public boolean getTaskState() {
		return taskStateChecked;
	}
	
	public boolean getChannelState() {
		return channelStateChecked;
	}
	
	public boolean getBranching() {
		return branchingChecked;
	}
	
	public boolean getTerminateCPU() {
		return terminateCPUChecked;
	}   
	
	public boolean getTerminateCPUs() {
		return terminateCPUsChecked;
	}   
	
	public boolean getClocked() {
		return clockedChecked;
	}
	
	public String getTickIntervalValue() {
		return tickIntervalValue;
	}
	
	public boolean getEndClocked() {
		return clockedEndChecked;
	}
	
	public boolean getCountTick() {
		return countTickChecked;
	}
	
	public boolean getMaxCountTick() {
		return maxCountTickChecked;
	}
	
	public String getMaxCountTickValue() {
		return maxCountTickValue;
	}
	
	public boolean getRandomTask() {
		return randomTaskChecked;
	}
	
	
	
}
