package ui.interactivesimulation;

import myutil.FileUtils;
import myutil.GraphicLib;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import ui.ColorManager;
import ui.MainGUI;
import ui.TGComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;

public class JFrameTMLSimulationPanelTimeline extends JFrame implements ActionListener {

    public InteractiveSimulationActions[] actions;
    private JEditorPane sdpanel;
    protected JLabel status;
    private Container framePanel;
    private ProgressMonitor pm;
    private String filePath;
    private MainGUI mgui;
    private JTabbedPane commandTab;
    private JFrameInteractiveSimulation jfis;
    private String[] cpuIDs, busIDs, memIDs, taskIDs, chanIDs;
    protected JComboBox<String> cpus, busses, mems, tasks, chans;
    private TMLMapping<TGComponent> tmap;
    private JTextField paramMainCommand;
    private MainCommandsToolBar mctb;

    public JFrameTMLSimulationPanelTimeline(Frame _f, MainGUI _mgui,JFrameInteractiveSimulation _jfis, String _title, String _path) {
        super(_title);
        mgui = _mgui;
        tmap =  mgui.gtm.getTMLMapping();
        filePath = _path;
        jfis = _jfis;
        initActions();
        makeComponents();
    }

    private JLabel createStatusBar() {
        status = new JLabel("Ready...");
        status.setForeground(ColorManager.InteractiveSimulationText);
        status.setBorder(BorderFactory.createEtchedBorder());
        return status;
    }

    public void makeComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());

        // Top panel
        JPanel timelinePane = new JPanel();
        JPanel topPanel = new JPanel();
        JButton buttonClose = new JButton(actions[InteractiveSimulationActions.ACT_QUIT_SD_WINDOW]);
        topPanel.add(buttonClose);
        JButton buttonHtml = new JButton(actions[InteractiveSimulationActions.ACT_SAVE_TIMELINE_HTML]);
        topPanel.add(buttonHtml);
        timelinePane.add(topPanel,BorderLayout.NORTH);
        //Main control
        JPanel jp01, jp02;
        jp01 = new JPanel(new BorderLayout());
        commandTab = GraphicLib.createTabbedPaneRegular();//new JTabbedPane();
        commandTab.addTab("Control", null, jp01, "Main control commands");

        mctb = new MainCommandsToolBar(jfis);
        jp01.add(mctb, BorderLayout.NORTH);

        jp02 = new JPanel();
        //jp01.setPreferredSize(new Dimension(375, 400));
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp02.setLayout(gridbag01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        jp02.add(new JLabel("Command parameter: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        paramMainCommand = new JTextField("1", 30);
        paramMainCommand.setEditable(false);
        jp02.add(paramMainCommand, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("CPUs and HwA: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (cpuIDs == null) {
            cpus = new JComboBox<String>();
        } else {
            cpus = new JComboBox<String>(cpuIDs);
        }
        jp02.add(cpus, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Buses: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (busIDs == null) {
            busses = new JComboBox<String>();
        } else {
            busses = new JComboBox<String>(busIDs);
        }
        jp02.add(busses, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Memories: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (memIDs == null) {
            mems = new JComboBox<String>();
        } else {
            mems = new JComboBox<String>(memIDs);
        }
        jp02.add(mems, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Tasks: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (taskIDs == null) {
            tasks = new JComboBox<String>();
        } else {
            tasks = new JComboBox<String>(taskIDs);
        }
        jp02.add(tasks, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Channels: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (chanIDs == null) {
            chans = new JComboBox<String>();
        } else {
            chans = new JComboBox<String>(chanIDs);
        }
        jp02.add(chans, c01);

        jp01.add(jp02, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, topPanel, commandTab);
        //split.setBackground(ColorManager.InteractiveSimulationBackground);
        timelinePane.add(split, BorderLayout.CENTER);
        timelinePane.add(commandTab, BorderLayout.SOUTH);
        framePanel.add(timelinePane, BorderLayout.NORTH);
        // Simulation panel
        sdpanel = new JEditorPane();
        sdpanel.setEditable(false);
        sdpanel.setContentType("text/html");
//        sdpanel.setText(filePath);

        JScrollPane jsp = new JScrollPane(sdpanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(MainGUI.INCREMENT);
        framePanel.add(jsp, BorderLayout.CENTER);

        // statusBar
        status = createStatusBar();
        framePanel.add(status, BorderLayout.SOUTH);

        pack();
    }

    private void initActions() {
        actions = new InteractiveSimulationActions[InteractiveSimulationActions.NB_ACTION];
        for (int i = 0; i < InteractiveSimulationActions.NB_ACTION; i++) {
            actions[i] = new InteractiveSimulationActions(i);
            actions[i].addActionListener(this);
        }

        cpuIDs = makeCPUIDs();
        busIDs = makeBusIDs();
        memIDs = makeMemIDs();
        taskIDs = makeTasksIDs();
        chanIDs = makeChanIDs();
    }

    public String[] makeCPUIDs() {
        if (tmap == null) {
            return null;
        }

        return tmap.getCPUandHwAIDs();
    }

    public String[] makeBusIDs() {
        if (tmap == null) {
            return null;
        }

        return tmap.getBusIDs();
    }

    public String[] makeMemIDs() {
        if (tmap == null) {
            return null;
        }

        return tmap.getMemIDs();
    }

    public String[] makeTasksIDs() {
        if (tmap == null) {
            return null;
        }

        return tmap.getTasksIDs();
    }

    public String[] makeChanIDs() {
        if (tmap == null) {
            return null;
        }

        return tmap.getChanIDs();
    }


    public void close() {
        dispose();
        setVisible(false);
    }

    private void saveHTML() {
        TraceManager.addDev("Saving in html format");
        File file = null;
        JFileChooser jfcimg = new JFileChooser();
        int returnVal = jfcimg.showSaveDialog(getContentPane());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfcimg.getSelectedFile();
            file = FileUtils.addFileExtensionIfMissing(file, "html");
        }
        boolean ok = true;

        try {
            ok = FileUtils.checkFileForSave(file);
        } catch (Exception e) {
            ok = false;
        }
        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "The capture could not be performed: the specified file is not valid",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(filePath);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            JOptionPane.showMessageDialog(getContentPane(), "The capture was correctly performed and saved in " + file.getAbsolutePath(), "Screen capture ok", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            TraceManager.addDev("Error during save trace: " + e.getMessage());
            status.setText("Error during save trace: " + e.getMessage());
            return;
        }
    }

    public void setParam(String param) {
        paramMainCommand.setText(param);
    }

    public void setContentPaneEnable(boolean x) {
        mctb.setActive(x);
    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        if (command.equals(actions[InteractiveSimulationActions.ACT_QUIT_SD_WINDOW].getActionCommand())) {
            sdpanel = null;
            close();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_TIMELINE_HTML].getActionCommand())) {
            saveHTML();
        }
    }

    public void setServerReply(String content) {
        try {
            filePath = content;
            sdpanel.setText(content);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void setStatusBar (String s) {
        status.setText("Ready ... Time: " + s);
    }
}
