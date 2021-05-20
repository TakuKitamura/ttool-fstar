package ui.interactivesimulation;

import myutil.FileUtils;
import myutil.GraphicLib;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import ui.ColorManager;
import ui.MainGUI;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGState;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

public class JFrameTMLSimulationPanelTimeline extends JFrame implements ActionListener {

    public InteractiveSimulationActions[] actions;
    private JEditorPane sdpanel;
    private JLabel status, time, info;;
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
    private JScrollPane jsp;
    private String zoomIndex = "";
    private String toolTipText = null;
    private int X = 0, Y = 0, Y_AXIS_START = 0, maxPos = 0, minPos = 0;
    private HashMap<Integer, Integer> timeMarkedPosition;

    private enum TransType {
        NONE, WRITE, READ, SEND, WAIT
    };

    public JFrameTMLSimulationPanelTimeline(Frame _f, MainGUI _mgui, JFrameInteractiveSimulation _jfis, String _title,
            String _path) {
        super(_title);
        mgui = _mgui;
        tmap = mgui.gtm.getTMLMapping();
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
        JTextField zoomIn = new JTextField("Zoom In:");
        zoomIn.setEditable(false);
        topPanel.add(zoomIn);
        String[] zoomFactor = new String[] { "50%", "75%", "100%", "125%", "150%", "175%", "200%" };
        JComboBox comboBoxUpdateView = new JComboBox<String>(zoomFactor);
        comboBoxUpdateView.setSelectedIndex(2);

        comboBoxUpdateView.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    zoomIndex = (String) comboBoxUpdateView.getSelectedItem();
                } catch (Exception e) {
                    // TraceManager.addDev(nbOfTransactions.getText());
                    // TraceManager.addDev("Invalid default transaction");
                    zoomIndex = "100%";
                }
                sdpanel.getDocument().putProperty("ZOOM_FACTOR", Double.parseDouble(zoomIndex.replace("%", "")) / 100);
                TraceManager.addDev("Scale: " + Double.parseDouble(zoomIndex.replace("%", "")) / 100);
                if (filePath.length() < 10000) {
                    sdpanel.setText(filePath);
                    sdpanel.setCaretPosition(0);
                    jsp.getVerticalScrollBar().setValue(0);
                    jsp.getHorizontalScrollBar().setValue(0);
                }
                jsp.repaint();
            }
        });
        topPanel.add(comboBoxUpdateView);
        timelinePane.add(topPanel, BorderLayout.NORTH);
        // Main control
        JPanel jp01, jp02;
        jp01 = new JPanel(new BorderLayout());
        commandTab = GraphicLib.createTabbedPaneRegular();// new JTabbedPane();
        commandTab.addTab("Control", null, jp01, "Main control commands");

        mctb = new MainCommandsToolBar(jfis);
        jp01.add(mctb, BorderLayout.NORTH);

        jp02 = new JPanel();
        // jp01.setPreferredSize(new Dimension(375, 400));
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
        c01.gridwidth = GridBagConstraints.REMAINDER; // end row
        paramMainCommand = new JTextField("1", 30);
        // paramMainCommand.setEditable(false);
        jp02.add(paramMainCommand, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("CPUs and HwA: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; // end row
        if (cpuIDs == null) {
            cpus = new JComboBox<String>();
        } else {
            cpus = new JComboBox<String>(cpuIDs);
        }
        jp02.add(cpus, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Buses: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; // end row
        if (busIDs == null) {
            busses = new JComboBox<String>();
        } else {
            busses = new JComboBox<String>(busIDs);
        }
        jp02.add(busses, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Memories: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; // end row
        if (memIDs == null) {
            mems = new JComboBox<String>();
        } else {
            mems = new JComboBox<String>(memIDs);
        }
        jp02.add(mems, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Tasks: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; // end row
        if (taskIDs == null) {
            tasks = new JComboBox<String>();
        } else {
            tasks = new JComboBox<String>(taskIDs);
        }
        jp02.add(tasks, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Channels: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; // end row
        if (chanIDs == null) {
            chans = new JComboBox<String>();
        } else {
            chans = new JComboBox<String>(chanIDs);
        }
        jp02.add(chans, c01);

        jp01.add(jp02, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, topPanel, commandTab);
        // split.setBackground(ColorManager.InteractiveSimulationBackground);
        timelinePane.add(split, BorderLayout.CENTER);
        timelinePane.add(commandTab, BorderLayout.SOUTH);
        framePanel.add(timelinePane, BorderLayout.NORTH);

        // Simulation panel
        sdpanel = new JEditorPane() {
            @Override
            public String getToolTipText(MouseEvent evt) {
                toolTipText = null;
                // viewToModel will be Deprecated. replaced by viewToModel2D(JTextComponent,
                // Point2D, Position.Bias[]) in java 9
                int pos = viewToModel(evt.getPoint());
                if (pos >= 0) {
                    HTMLDocument hdoc = (HTMLDocument) sdpanel.getDocument();
                    javax.swing.text.Element e = hdoc.getCharacterElement(pos);
                    AttributeSet a = e.getAttributes();
                    if (a != null) {
                        String href = (String) a.getAttribute(HTML.Attribute.TITLE);
                        if (href != null) {
                            toolTipText = href;
                        }
                    }
                }
                return toolTipText;
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (timeMarkedPosition != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    float dash[] = { 10.0f };
                    g2.setStroke(
                            new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                    g2.setColor(new Color(2, 68, 2));
                    if (Y_AXIS_START != 0) {
                        int drawPoint = 0;
                        try {
                            // modelToView will be Deprecated. replaced by modelToView2D(JTextComponent,
                            // int, Position.Bias) in java 9
                            drawPoint = modelToView(Y_AXIS_START).y;
                        } catch (BadLocationException e) {
                            drawPoint = 130;
                        }
                        g2.drawLine(X, drawPoint, X, drawPoint + sdpanel.getHeight());
                    } else {
                        g2.drawLine(X, 130, X, 130 + sdpanel.getHeight());
                    }

                    // viewToModel will be Deprecated. replaced by viewToModel2D(JTextComponent,
                    // Point2D, Position.Bias[]) in java 9
                    int pos = viewToModel(new Point(X, Y));
                    if (pos >= 0) {

                        try {
                            // modelToView will be Deprecated. replaced by modelToView2D(JTextComponent,
                            // int, Position.Bias) in java 9
                            if (timeMarkedPosition.keySet().contains(pos) && Math.abs(modelToView(pos).x - X) < 3) {
                                g2.drawString("Time: " + timeMarkedPosition.get(pos), X, Y);
                                g2.dispose();
                            } else if (!timeMarkedPosition.keySet().contains(pos)) {
                                int postStart = pos - 1;
                                int postEnd = pos + 1;
                                while (!timeMarkedPosition.keySet().contains(postStart) && postStart > minPos) {
                                    postStart--;
                                }

                                while (!timeMarkedPosition.keySet().contains(postEnd) && postEnd < maxPos) {
                                    postEnd++;
                                }

                                if (timeMarkedPosition.keySet().contains(postStart)
                                        && timeMarkedPosition.keySet().contains(postEnd)
                                        && timeMarkedPosition.get(postStart) < timeMarkedPosition.get(postEnd)) {
                                    // modelToView will be Deprecated. replaced by modelToView2D(JTextComponent,
                                    // int, Position.Bias) in java 9
                                    int value = timeMarkedPosition.get(postStart)
                                            + (int) (((float) (timeMarkedPosition.get(postEnd)
                                                    - timeMarkedPosition.get(postStart))
                                                    / (modelToView(postEnd).x - modelToView(postStart).x))
                                                    * (X - modelToView(postStart).x));
                                    g2.drawString("Time: " + value, X, Y);
                                    g2.dispose();
                                }
                            }

                        } catch (BadLocationException e) {
                            TraceManager.addDev("Position not found.");
                        }
                    }
                }

            }
        };

        sdpanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    if (toolTipText != null) {
                        String diag = "";
                        String tab = "";
                        int index1 = -1;
                        int index = toolTipText.indexOf(": ");
                        if (index != -1) {
                            tab = toolTipText.substring(0, index);
                            index1 = tab.indexOf("__");
                        }
                        if (index1 != -1) {
                            diag = tab.substring(0, index1);
                            tab = tab.substring(index1 + 2, tab.length());
                            // TraceManager.addDev(diag + "\n" + tab);
                        }
                        mgui.openTMLTaskActivityDiagram(diag, tab);
                        mgui.getFrame().toFront();
                        mgui.getFrame().requestFocus();
                        mgui.getFrame().repaint();
                        TDiagramPanel tp = mgui.getCurrentTDiagramPanel();
                        for (int z = 0; z < tp.getComponentList().size(); z++) {
                            String temp = tp.getComponentList().get(z).toString();
                            TransType typeTooltip = getTypeOfTransactions(toolTipText);
                            TransType typeTemp = getTypeOfTransactions(temp);
                            int indexTemp1 = temp.indexOf(": ");
                            int indexTemp2 = temp.indexOf("(");

                            if (indexTemp1 != -1 && indexTemp2 != -1) {
                                temp = temp.substring(indexTemp1 + 2, indexTemp2);
                            }

                            if (toolTipText.toLowerCase().contains(temp.toLowerCase()) && typeTooltip == typeTemp) {
                                tp.getComponentList().get(z).setState(TGState.POINTED);
                            } else {
                                tp.getComponentList().get(z).setState(TGState.NORMAL);
                            }
                        }
                    }
                }
            }
        });

        sdpanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                X = e.getX();
                Y = e.getY();
                repaint();
                return;
            }
        });

        sdpanel.setEditable(false);
        sdpanel.setContentType("text/html");
        sdpanel.setEditorKit(new LargeHTMLEditorKit());
        ToolTipManager.sharedInstance().registerComponent(sdpanel);

        jsp = new JScrollPane(sdpanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(MainGUI.INCREMENT);
        framePanel.add(jsp, BorderLayout.CENTER);

        // statusBar
        jp02 = new JPanel();
        // infos.add(jp02, BorderLayout.SOUTH);
        framePanel.add(jp02, BorderLayout.SOUTH);
        jp02.add(new JLabel("Status:"));
        status = new JLabel("Unknown");
        status.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
        jp02.add(status);
        jp02.add(new JLabel(" "));
        jp02.add(new JLabel("Time:"));
        time = new JLabel("Unknown");
        time.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
        jp02.add(time);
        jp02.add(new JLabel(" "));
        jp02.add(new JLabel("Sim. interrupt reason:"));
        info = new JLabel("Unknown");
        info.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
        jp02.add(info);

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
            JOptionPane.showMessageDialog(this, "The capture could not be performed: the specified file is not valid",
                    "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(filePath);
            myWriter.close();
            TraceManager.addDev("Successfully wrote to the file.");
            JOptionPane.showMessageDialog(getContentPane(),
                    "The capture was correctly performed and saved in " + file.getAbsolutePath(), "Screen capture ok",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            TraceManager.addDev("Error during save trace: " + e.getMessage());
            status.setText("Error during save trace: " + e.getMessage());
            return;
        }
    }

    public void setParam(String param) {
        paramMainCommand.setText(param);
    }

    public String getParam() {
        return paramMainCommand.getText().trim();
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

    private void collectTimeStamp() {
        // for store all element
        if (sdpanel != null && sdpanel.getDocument() != null) {
            timeMarkedPosition = new HashMap<>();
            HTMLDocument hdoc = (HTMLDocument) sdpanel.getDocument();
            ElementIterator iterator = new ElementIterator(hdoc);
            Element element;
            maxPos = 0;
            minPos = Integer.MAX_VALUE;
            Y_AXIS_START = 0;
            while ((element = iterator.next()) != null) {
                int startOffset = element.getStartOffset();
                int endOffset = element.getEndOffset();
                int length = endOffset - startOffset;
                try {
                    String temp = hdoc.getText(startOffset, length);
                    if (temp.equals("Time")) {
                        Y_AXIS_START = startOffset;
                    }
                    int num = Integer.parseInt(temp);
                    if (num > maxPos)
                        maxPos = num;
                    if (num < minPos)
                        minPos = num;
                    timeMarkedPosition.put(startOffset, num);
                } catch (NumberFormatException e) {
                    // not an integer.
                } catch (BadLocationException e) {
                    TraceManager.addDev("Some content was not retrieved: " + e.getMessage());
                }
            }
        }
    }

    public void setServerReply(String content) {
        try {
            filePath = content;
            sdpanel.setText(content);
            sdpanel.setCaretPosition(0);
            collectTimeStamp();
            jsp.getVerticalScrollBar().setValue(0);
            jsp.getHorizontalScrollBar().setValue(0);
            jsp.repaint();
        } catch (Exception e) {
            TraceManager.addDev("Error during writing html content: " + e.getMessage());
        }
    }

    public void setStatusBar(String s, String s1, String s2) {
        status.setText(s);
        time.setText(s1);
        info.setText(s2);
        if (s.equals("Terminated")) {
            status.setForeground(ColorManager.InteractiveSimulationText_TERM);
            time.setForeground(ColorManager.InteractiveSimulationText_TERM);
            info.setForeground(ColorManager.InteractiveSimulationText_TERM);
        } else if (s.equals("Busy")) {
            status.setForeground(ColorManager.InteractiveSimulationText_BUSY);
            time.setForeground(ColorManager.InteractiveSimulationText_BUSY);
            info.setForeground(ColorManager.InteractiveSimulationText_BUSY);
        } else {
            status.setForeground(ColorManager.InteractiveSimulationText_READY);
            time.setForeground(ColorManager.InteractiveSimulationText_READY);
            info.setForeground(ColorManager.InteractiveSimulationText_READY);
        }
    }

    public TransType getTypeOfTransactions(String trans) {
        if (trans.toLowerCase().contains("write"))
            return TransType.WRITE;
        else if (trans.toLowerCase().contains("read"))
            return TransType.READ;
        else if (trans.toLowerCase().contains("send"))
            return TransType.SEND;
        else if (trans.toLowerCase().contains("wait"))
            return TransType.WAIT;
        return TransType.NONE;
    }
}
