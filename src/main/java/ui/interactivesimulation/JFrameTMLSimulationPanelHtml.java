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
        sdpanel.setEditable(false);
//        String temp = "<!DOCTYPE html>\n" + "<html>\n" + "<body>";
//        sdpanel.setContentType("text/html");
//
//        for (int i = 0; i < trans.size(); i ++){
//            temp += "<h1> --Device name" + trans.get(i).deviceName + "--Task name " + trans.get(i).taskName + "--Command " + trans.get(i).command +
//                    "--end </h1>\n";
//
//        }
//        temp += "</body>\n" + "</html>";
//        sdpanel.setText(temp);

        JScrollPane jsp	= new JScrollPane(sdpanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(MainGUI.INCREMENT);
        try {
            File file = new File("/home/levan/Desktop/TTool/simulators/c++2/test.html");
            sdpanel.setPage(file.toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
