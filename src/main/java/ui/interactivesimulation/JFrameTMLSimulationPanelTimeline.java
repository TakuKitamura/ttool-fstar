package ui.interactivesimulation;

import myutil.FileUtils;
import myutil.TraceManager;
import ui.ColorManager;
import ui.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class JFrameTMLSimulationPanelTimeline extends JFrame implements ActionListener {

    public InteractiveSimulationActions[] actions;
    private Vector<SimulationTransaction> trans;

    private static final int BIG_IDLE = 50;
    private static String htmlContent;
    private int count = 0;
    protected JComboBox<String> units;

    private JTextPane sdpanel;
    protected JLabel status;

    private MainGUI mgui;

    public JFrameTMLSimulationPanelTimeline(Frame _f, MainGUI _mgui, Vector<SimulationTransaction> _trans, String _title) {
        super(_title);
        mgui = _mgui;
        initActions();
        trans = new Vector<SimulationTransaction>(_trans);
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
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());

        // Top panel
        JPanel topPanel = new JPanel();
        JButton buttonClose = new JButton(actions[InteractiveSimulationActions.ACT_QUIT_SD_WINDOW]);
        topPanel.add(buttonClose);
        JButton buttonHtml = new JButton(actions[InteractiveSimulationActions.ACT_SAVE_TIMELINE_HTML]);
        topPanel.add(buttonHtml);

        // classify trans base on CPU and Core
        Map<String, Vector<SimulationTransaction>> map = new HashMap<String, Vector<SimulationTransaction>>();
        Map<String, String> taskColors = new HashMap<String, String>();
        int taskIndex = 0;
        int endTime = 0;
        for (int i = 0; i < trans.size(); i++) {
            //HW and task
            String hwnode = (trans.get(i).deviceName.contains("Bus") ? trans.get(i).deviceName : trans.get(i).deviceName + "_core_" + trans.get(i).coreNumber);
            if (map.get(hwnode) == null) {
                map.put(hwnode, new Vector<SimulationTransaction>());
            }
            map.get(hwnode).add(trans.get(i));
            // task color
            String taskname = trans.get(i).taskName;
            if (taskColors.get(taskname) == null) {
                int cellIndex = taskIndex % 15;
                taskIndex++;
                String cellClass = "t" + String.valueOf(cellIndex);
                taskColors.put(taskname, cellClass);
            }
        }
        for (String i : map.keySet()) {
            if (Integer.valueOf(map.get(i).lastElement().endTime) > endTime) endTime = Integer.valueOf(map.get(i).lastElement().endTime);
        }

        framePanel.add(topPanel, BorderLayout.NORTH);

        // Simulation panel
        sdpanel = new JTextPane();
        sdpanel.setEditable(false);
        sdpanel.setContentType("text/html");
        htmlContent = "<!DOCTYPE html>\n" + "<html>\n";
        htmlContent += "<head>\n" +
                "<style>\n";
        htmlContent += "table{\n" +
                " \tborder-collapse: collapse;\n" +
                " \tempty-cells: show;\n" +
                " \tmargin: 0.4cm;\n" +
                " }\n" +
                " td{\n" +
                " \tpadding: 10px 5px;\n" +
                " \tborder: 1px solid black;\n" +
                " \tmax-width: 5px;\n" +
                " }\n" +
                " th{\n" +
                " \tpadding: 5px;\n" +
                " }\n" +
                " .sc{\n" +
                " \tborder-style: none;\n" +
                " \tpadding: 0px;\n" +
                " \tfont-size: small;\n" +
                "     transform: rotate(45deg);\n" +
                "     transform-origin: left top;\n" +
                " }\n" +
                " .sc1{\n" +
                " \tborder-style: none;\n" +
                " \tpadding: 0px;\n" +
                " \tfont-size: small;\n" +
                " \tcolor: red;\n" +
                "     transform: rotate(45deg);\n" +
                "     transform-origin: left top;\n" +
                " }\n" +
                " h2 {\n" +
                " \tborder-bottom: 1px solid #666;\n" +
                " }\n" +
                " h2 span {\n" +
                " \tposition: relative;\n" +
                " \tleft: -0.3em;\n" +
                " \tbottom: -0.6em;\n" +
                " \tpadding: 1px 0.5em;\n" +
                " \tborder-style: solid;\n" +
                " \tborder-width: 1px 1px 1px 0.8em;\n" +
                " \tborder-color: #666 #666 #666 #008;\n" +
                " \tbackground-color: #ddd;\n" +
                " }\n" +
                " .space{border-style: none;}\n" +
                " .not{background-color: white; text-align: center}\n" +
                ".notfirst{\n" +
                " \tbackground-color: white;\n" +
                " \tborder-width: 2px;\n" +
                " \tborder-color: red;\n" +
                " \tborder-style: none solid none none;\n" +
                " }\n" +
                " .notmid {\n" +
                " \tbackground-color: white;\n" +
                " \ttext-align: right;\n" +
                " \tborder-style: solid none none none;\n" +
                " }\n" +
                " .notlast {\n" +
                " \tbackground-color: white;\n" +
                " \twidth: 5px;\n" +
                " \tborder-style: none none none none;\n" +
                " }\n" +
                " .t0{background-color: yellow;}\n" +
                " \n" +
                " .t1{background-color: purple;}\n" +
                " \n" +
                " .t2{background-color: red;}\n" +
                " \n" +
                " .t3{background-color: silver;}\n" +
                " \n" +
                " .t4{background-color: teal;}\n" +
                " \n" +
                " .t5{background-color: aqua;}\n" +
                " \n" +
                " .t6{background-color: olive;}\n" +
                " \n" +
                " .t7{background-color: navy;}\n" +
                "\n" +
                " .t8{background-color: maroon;}\n" +
                " \n" +
                " .t9{background-color: lime;}\n" +
                " \n" +
                " .t10{background-color: green;}\n" +
                " \n" +
                " .t11{background-color: gray;}\n" +
                "\n" +
                " .t12{background-color: fuchsia;}\n" +
                " \n" +
                " .t13{background-color: blue;}\n" +
                " \n" +
                " .t14{background-color: LightGoldenRodYellow;}\n" +
                " \n" +
                " .wrapper {\n" +
                " \twidth: 256px;\n" +
                " \theight: 256px;\n" +
                " }\n" +
                " \n" +
                " .clear {\n" +
                " \tclear:both\n" +
                " }\n";
        htmlContent += "</style>\n" +
                "</head>\n<body>\n<table style=\"float: left;position: relative;\">";
        htmlContent += "<tr><td width=\"170px\" style=\"max-width: 170px;min-width: 170px;border-style: none none none none;\"></td>\n" +
                "<td class=\"notfirst\"></td>\n" +
                "<td style=\"border-style: solid none none none; border-width: 2px;border-color: red;text-align: right\" colspan=\"" + endTime +
                "\"><b>Time</b></td>\n</tr>\n" +
                "<tr><th></th><th class=\"notfirst\"></th></tr>\n" +
                "<div class = \"clear\"></div>";
        for (String i : map.keySet()) {
            count ++;
            Vector<String> listScale = new Vector<String>();
            Vector<String> listScaleTime = new Vector<String>();
            listScale.add("0");
            listScaleTime.add("0");
            htmlContent += "<tr><td width=\"170px\" style=\"max-width: 170px;min-width: 170px;background-color: aqua;\">" + i + "</td>\n<td class=\"notfirst\"></td>\n<td class=\"notlast\"></td>\n";
            for (int j = 0; j < map.get(i).size(); j++) {

                if (j == 0 && Integer.valueOf(map.get(i).get(j).startTime) != 0) {
                    if(Integer.valueOf(map.get(i).get(j).startTime) > BIG_IDLE) {
                        htmlContent += "<td title=\"idle time" + "\" class = \"not\" colspan=\"10\"> <-IDLE " + map.get(i).get(j).startTime + "-> </td>\n";
                        listScale.add("10");
                    } else {
                        htmlContent += "<td title=\"idle time" + "\" class = \"not\" colspan=\"" + map.get(i).get(j).startTime + "\"></td>\n";
                        listScale.add(map.get(i).get(j).startTime);
                    }

                    if (Integer.valueOf(map.get(i).get(j).startTime) > Integer.valueOf(listScaleTime.lastElement())) {
                        listScaleTime.add(map.get(i).get(j).startTime);
                    }
                    if (Integer.valueOf(map.get(i).get(j).endTime) > Integer.valueOf(listScaleTime.lastElement())) {
                        listScaleTime.add(map.get(i).get(j).endTime);
                    }
                } else if ((j != 0 && (Integer.valueOf(map.get(i).get(j).startTime) > Integer.valueOf(map.get(i).get(j - 1).endTime)))) {
                    int sub = Integer.valueOf(map.get(i).get(j).startTime) - Integer.valueOf(map.get(i).get(j - 1).endTime);
                    if (sub > BIG_IDLE) {
                        htmlContent += "<td title=\"idle time" + "\" class = \"not\" colspan=\"10\"> <-IDLE " + String.valueOf(sub) + "-> </td>\n";
                        listScale.add("10");
                    } else if (sub > 0) {
                        htmlContent += "<td title=\"idle time" + "\" class = \"not\" colspan=\"" + String.valueOf(sub) + "\"></td>\n";
                        listScale.add(String.valueOf(sub));
                    }

                }
                int sub1 = Integer.valueOf(map.get(i).get(j).endTime) - Integer.valueOf(map.get(i).get(j).startTime);
                if (sub1 > BIG_IDLE) {
                    htmlContent += "<td title=\"" + map.get(i).get(j).command + "\" class = \"" + (map.get(i).get(j).command.contains("Idle") ? "not" : taskColors.get(map.get(i).get(j).taskName)) + "\" colspan=\"10\">" + map.get(i).get(j).command.substring(0, 1) + "</td>\n";
                    listScale.add("10");
                } else if (sub1 > 0) {
                    htmlContent += "<td title=\"" + map.get(i).get(j).command + "\" class = \"" + (map.get(i).get(j).command.contains("Idle") ? "not" : taskColors.get(map.get(i).get(j).taskName)) + "\" colspan=\"" + String.valueOf(sub1) + "\">" + map.get(i).get(j).command.substring(0, 1) + "</td>\n";
                    listScale.add(String.valueOf(sub1));
                }

                if (Integer.valueOf(map.get(i).get(j).startTime) > Integer.valueOf(listScaleTime.lastElement())) {
                    listScaleTime.add(map.get(i).get(j).startTime);
                }
                if (Integer.valueOf(map.get(i).get(j).endTime) > Integer.valueOf(listScaleTime.lastElement())) {
                    listScaleTime.add(map.get(i).get(j).endTime);
                }
            }
            htmlContent += "</tr>\n<tr>";
            for (int k = 0; k < Integer.valueOf(map.get(i).lastElement().endTime) + 2; k++) {
                if( k == 1) {
                    htmlContent += "<th class=\"notfirst\">";
                } else {
                    htmlContent += "<th></th>";
                }
            }
            htmlContent += "</tr>\n<tr><td width=\"170px\" style=\"max-width: 170px;min-width: 170px;border-style: none none none none;\"></td>\n" +
                    "<td class=\"notfirst\"></td>\n<td class=\"notlast\"></td>";
            for (int l = 0; l < listScale.size(); l++) {
                if (l + 1 >= listScale.size()) {
                    htmlContent += "<td title=\"" + listScaleTime.get(l) + "\" class = \"sc\" colspan=\"" + "5" + "\">" + listScaleTime.get(l) + "</td>\n";
                } else {
                    htmlContent += "<td title=\"" + listScaleTime.get(l) + "\" class = \"sc\" colspan=\"" + listScale.get(l + 1) + "\">" + listScaleTime.get(l) + "</td>\n";
                }
            }
            if( count >= map.size()){
                htmlContent += "</tr>\n<tr><th>HW</th><th class=\"notfirst\"></th></tr>\n<div class = \"clear\"></div>\n";
            } else {
                htmlContent += "</tr>\n<tr><th></th><th class=\"notfirst\"></th></tr>\n<div class = \"clear\"></div>\n";
            }


        }
        htmlContent += "</table>\n<table>\n<tr><td width=\"170px\" style=\"max-width: 170px;min-width: 170px;border-style: none none none none;\"></td>\n" +
                "<td class=\"notlast\"></td>\n";
        for (String colors : taskColors.keySet()) {
            htmlContent += "<td  class = \"" + taskColors.get(colors) + "\" style=\"max-width: 170px;min-width: 170px;\">" + colors + "</td>";
            htmlContent += "<td class=\"space\"></td>";
        }

        htmlContent += "</tr>\n</table>\n</body>\n" + "</html>";
//        System.out.println(htmlContent);
        sdpanel.setText(htmlContent);

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
    }

    public void close() {
        dispose();
        setVisible(false);
    }

    private void saveHTML() {
        TraceManager.addDev("Saving in html format");
        File file = null;
        JFileChooser jfcimg = new JFileChooser();
//        jfcimg.setCurrentDirectory(new File(""));
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
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(htmlContent.getBytes());
            fos.close();
            JOptionPane.showMessageDialog(getContentPane(), "The capture was correctly performed and saved in " + file.getAbsolutePath(), "Screen capture ok", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            TraceManager.addDev("Error during save trace: " + e.getMessage());
            status.setText("Error during save trace: " + e.getMessage());
            return;
        }
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
}
