package ui.interactivesimulation;

import myutil.FileUtils;
import myutil.TraceManager;
import myutilsvg.SVGGeneration;
import ui.ColorManager;
import ui.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class JFrameTMLSimulationPanelHtml extends JFrame implements ActionListener {

    public InteractiveSimulationActions [] actions;
    private Vector<SimulationTransaction> trans;

    private static String[] unitTab = {"sec", "msec", "usec", "nsec"};
    private static int[] clockDivisers = {1000000000, 1000000, 1000, 1};
    protected JComboBox<String> units;

    private JEditorPane sdpanel;
    protected JLabel status;
    //, buttonStart, buttonStopAndClose;
    //protected JTextArea jta;
    //protected JScrollPane jsp;

    private MainGUI mgui;

    public JFrameTMLSimulationPanelHtml(Frame _f, MainGUI _mgui, Vector<SimulationTransaction> _trans, String _title) {
        super(_title);
        mgui = _mgui;
        initActions();
        trans = _trans;
        makeComponents();
        //setComponents();
//        this.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e)
//            {
//                if (JFrameTMLSimulationPanelHtml.this.sdpanel != null)
//                    JFrameTMLSimulationPanelHtml.this.sdpanel.resized();
//            }
//        });
    }
    private JLabel createStatusBar()  {
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
        JButton buttonSVG = new JButton(actions[InteractiveSimulationActions.ACT_SAVE_SD_SVG]);
        topPanel.add(buttonSVG);
        JButton buttonPNG = new JButton(actions[InteractiveSimulationActions.ACT_SAVE_SD_PNG]);
        topPanel.add(buttonPNG);

        /*topPanel.add(new JLabel(" time unit:"));
        units = new JComboBox<>(unitTab);
        units.setSelectedIndex(1);
        units.addActionListener(this);
        topPanel.add(units);
        JButton buttonRefresh = new JButton(actions[InteractiveSimulationActions.ACT_REFRESH]);
        topPanel.add(buttonRefresh);*/
        framePanel.add(topPanel, BorderLayout.NORTH);

        // Simulation panel
        sdpanel = new JEditorPane();
        sdpanel.setEditable(true);
        sdpanel.setContentType("text/html");
        String temp = "<!DOCTYPE html>\n" + "<html>\n";
        temp += "<head>\n" +
                "<style>";
        temp += "table{\n" +
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
                " \tborder-left: 1px dotted black;\n" +
                " \tborder-right: 1px dotted black;\n" +
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
                " .not{background-color: white;}\n" +
                " .notfirst {\n" +
                " \tbackground-color: white;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .notmid {\n" +
                " \tbackground-color: white;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .notlast {\n" +
                " \tbackground-color: white;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t0{background-color: yellow;}\n" +
                " .t0first {\n" +
                " \tbackground-color: yellow;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t0mid {\n" +
                " \tbackground-color: yellow;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t0last {\n" +
                " \tbackground-color: yellow;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t1{background-color: purple;}\n" +
                " .t1first {\n" +
                " \tbackground-color: purple;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t1mid {\n" +
                " \tbackground-color: purple;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t1last {\n" +
                " \tbackground-color: purple;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t2{background-color: red;}\n" +
                " .t2first {\n" +
                " \tbackground-color: red;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t2mid {\n" +
                " \tbackground-color: red;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t2last {\n" +
                " \tbackground-color: red;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t3{background-color: silver;}\n" +
                " .t3first {\n" +
                " \tbackground-color: silver;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t3mid {\n" +
                " \tbackground-color: silver;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t3last {\n" +
                " \tbackground-color: silver;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t4{background-color: teal;}\n" +
                " .t4first {\n" +
                " \tbackground-color: teal;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t4mid {\n" +
                " \tbackground-color: teal;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t4last {\n" +
                " \tbackground-color: teal;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t5{background-color: aqua;}\n" +
                " .t5first {\n" +
                " \tbackground-color: aqua;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t5mid {\n" +
                " \tbackground-color: aqua;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t5last {\n" +
                " \tbackground-color: aqua;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t6{background-color: olive;}\n" +
                " .t6first {\n" +
                " \tbackground-color: olive;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t6mid {\n" +
                " \tbackground-color: olive;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t6last {\n" +
                " \tbackground-color: olive;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t7{background-color: navy;}\n" +
                " .t7first {\n" +
                " \tbackground-color: navy;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t7mid {\n" +
                " \tbackground-color: navy;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t7last {\n" +
                " \tbackground-color: navy;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t8{background-color: maroon;}\n" +
                " .t8first {\n" +
                " \tbackground-color: maroon;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t8mid {\n" +
                " \tbackground-color: maroon;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t8last {\n" +
                " \tbackground-color: maroon;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t9{background-color: lime;}\n" +
                " .t9first {\n" +
                " \tbackground-color: lime;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t9mid {\n" +
                " \tbackground-color: lime;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t9last {\n" +
                " \tbackground-color: lime;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t10{background-color: green;}\n" +
                " .t10first {\n" +
                " \tbackground-color: green;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t10mid {\n" +
                " \tbackground-color: green;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t10last {\n" +
                " \tbackground-color: green;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t11{background-color: gray;}\n" +
                " .t11first {\n" +
                " \tbackground-color: gray;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t11mid {\n" +
                " \tbackground-color: gray;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t11last {\n" +
                " \tbackground-color: gray;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t12{background-color: fuchsia;}\n" +
                " .t12first {\n" +
                " \tbackground-color: fuchsia;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t12mid {\n" +
                " \tbackground-color: fuchsia;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t12last {\n" +
                " \tbackground-color: fuchsia;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t13{background-color: blue;}\n" +
                " .t13first {\n" +
                " \tbackground-color: blue;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t13mid {\n" +
                " \tbackground-color: blue;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t13last {\n" +
                " \tbackground-color: blue;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .t14{\n" +
                " \tbackground-color: LightGoldenRodYellow;\n" +
                "}\n" +
                " .t14first {\n" +
                " \tbackground-color: LightGoldenRodYellow;\n" +
                " \tborder-style: solid none solid solid;\n" +
                " }\n" +
                " .t14mid {\n" +
                " \tbackground-color: LightGoldenRodYellow;\n" +
                " \tborder-style: solid none solid none;\n" +
                " }\n" +
                " .t14last {\n" +
                " \tbackground-color: LightGoldenRodYellow;\n" +
                " \tborder-style: solid solid solid none;\n" +
                " }\n" +
                " .wrapper {\n" +
                " \twidth: 256px;\n" +
                " \theight: 256px;\n" +
                " }\n" +
                " .pie-chart-container {\n" +
                " \twidth : 256px;\n" +
                " \theight : 256px;\n" +
                " \tfloat : left;\n" +
                " \tmargin-left : 2em;\n" +
                " }\n" +
                " .clear {\n" +
                " \tclear:both\n" +
                " }";
        temp += "</style>\n" +
                "</head>\n <body>";
        temp += "<table style=\"float: left\">\n<tr>";

        for (int i = 0; i < 11; i ++){
//            temp += "<h1> --Device name " + trans.get(i).toString() + "--Task name " + trans.get(i).startTime + "--Command " + trans.get(i).endTime +
//                    "--end </h1>\n";

            temp += "<td title=\"" + trans.get(i).command +"\" style = \"background-color: red;\" colspan=\"" + String.valueOf(Integer.valueOf(trans.get(i).endTime) - Integer.valueOf(trans.get(i).startTime)) + "\"></td>\n";

        }
        temp += "</tr>\n<tr><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th></tr>\n</body>\n" + "</html>";
        System.out.println(temp);
        sdpanel.setText(temp);

        JScrollPane jsp	= new JScrollPane(sdpanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(MainGUI.INCREMENT);
//        try {
//            File file = new File("/home/levan/Desktop/TTool/simulators/c++2/test.html");
//            sdpanel.setPage(file.toURI().toURL());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        framePanel.add(jsp, BorderLayout.CENTER);

        // statusBar
        status = createStatusBar();
        framePanel.add(status, BorderLayout.SOUTH);

        // Mouse handler
        //mouseHandler = new MouseHandler(status);

        pack();

        //
        //
    }
    private	void initActions() {
        actions = new InteractiveSimulationActions[InteractiveSimulationActions.NB_ACTION];
        for(int	i=0; i<InteractiveSimulationActions.NB_ACTION; i++) {
            actions[i] = new InteractiveSimulationActions(i);
            actions[i].addActionListener(this);
            //actions[i].addKeyListener(this);
        }
    }


    public void close() {
        dispose();
        setVisible(false);
    }

    private void saveSVG() {
        TraceManager.addDev("Saving in svg format");
        sdpanel.setText("<html>Saving in SVG format</html>");
        //newSVGSave(fileName);
    }

    private void savePNG() {
        TraceManager.addDev("Saving in png format");
        sdpanel.setText("<html>Saving in PNG format</html>");
        //newSVGSave(fileName);
    }


    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        //TraceManager.addDev("Command:" + command);

        if (command.equals(actions[InteractiveSimulationActions.ACT_QUIT_SD_WINDOW].getActionCommand()))  {
            sdpanel=null;
            close();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_SD_SVG].getActionCommand())) {
            saveSVG();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_SD_PNG].getActionCommand())) {
            savePNG();
        }
    }
}
