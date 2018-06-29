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


package ui.networkmodelloader;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.*;
import ui.JTextAreaWriter;
import ui.MainGUI;
import ui.file.TFileFilter;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Class JDialogNetworkModelPanel
 * Dialog for managing the loading of network models
 * Creation: 28/05/2017
 *
 * @author Ludovic APVRILLE
 * @author Ludovic Apvrille
 * @version 1.1 28/05/2017
 */
public class JDialogLoadingNetworkModel extends javax.swing.JFrame implements ActionListener, Runnable, LoaderFacilityInterface, CallbackLoaderInterface {

    public final static String[] FEATURES = {"all", "diplodocus", "avatar", "sysml-sec", "assumptions", "requirements", "attacktrees", "properties", "partitioning", "analysis", "design", "prototyping", "securityprotocol", "codegeneration"};

    public final static String[] PROPS = {"safety", "security", "performance"};

    private ArrayList<NetworkModel> listOfModels;

    protected Frame f;
    protected MainGUI mgui;

    protected final static int NOT_LISTED = 1;
    protected final static int LISTED = 2;
    protected final static int SELECTED = 3;

    private int mode;

    //components
    protected JTextArea jta;
    private JTextAreaWriter textAreaWriter;
    protected JButton start;
    protected JButton stop;
    protected JComboBox<String> featureList;
    protected JCheckBox[] props;

    protected JScrollPane jsp;

    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
    protected boolean startProcess = false;

    private String url;
    private NetworkModelPanel panel;
    private String filePath;
    private JFileChooser jfc;


    /**
     * Creates new form
     */
    public JDialogLoadingNetworkModel(Frame _f, MainGUI _mgui, String title, String _url) {
        super(title);

        f = _f;
        mgui = _mgui;

        url = _url;

        listOfModels = new ArrayList<NetworkModel>();


        initComponents();
        myInitComponents();
        pack();
        Thread t = new Thread(this);
        t.start();


        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }


    protected void myInitComponents() {

        if (ConfigurationTTool.DownloadedFILEPath.length() > 0) {
            jfc = new JFileChooser(ConfigurationTTool.DownloadedFILEPath);
        } else {
            jfc = new JFileChooser();
        }

        mode = NOT_LISTED;
        setButtons();
    }

    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        JPanel lowPart = new JPanel(new BorderLayout());

        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setLineWrap(true);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Connecting to " + url + ".\n Please wait ...\n\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        textAreaWriter = new JTextAreaWriter(jta);

        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setPreferredSize(new Dimension(400, 200));

        JPanel options = new JPanel();

        JLabel infoModels = new JLabel("Not loaded");
        options.add(infoModels);

        featureList = new JComboBox<String>(FEATURES);
        featureList.addActionListener(this);
        options.add(featureList);

        props = new JCheckBox[PROPS.length];
        for (int i = 0; i < props.length; i++) {
            props[i] = new JCheckBox(PROPS[i]);
            props[i].addActionListener(this);
            props[i].setSelected(true);
            options.add(props[i]);
        }

        lowPart.add(options, BorderLayout.NORTH);


        lowPart.add(jsp, BorderLayout.CENTER);

        start = new JButton("Load", IconManager.imgic23);
        stop = new JButton("Cancel", IconManager.imgic55);

        start.setPreferredSize(new Dimension(200, 30));
        stop.setPreferredSize(new Dimension(200, 30));

        start.addActionListener(this);
        stop.addActionListener(this);

        JPanel jp2 = new JPanel();
        jp2.add(stop);
        //jp2.add(start);

        lowPart.add(jp2, BorderLayout.SOUTH);

        c.add(lowPart, BorderLayout.SOUTH);

        panel = new NetworkModelPanel(this, listOfModels, this, jta, infoModels);
        jsp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.setJSP(jsp);

        c.add(jsp, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent evt) {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == stop) {
            cancel();
            return;
        } else if (evt.getSource() == featureList) {
            featureSelectionMade();
            return;
        }

        for (int i = 0; i < props.length; i++) {
            if (evt.getSource() == props[i]) {
                panel.setProperty(i, props[i].isSelected());
                return;
            }
        }
    }

    private void featureSelectionMade() {
        panel.setFeatureSelectedIndex(featureList.getSelectedIndex());
    }

    public void cancel() {
        if (panel != null) panel.stopLoading();
        dispose();
    }

    public void alertMessage() {
        jta.append("Could not establish a connection to the TTool server\n");
        jta.append("Error when retrieving file: " + url + "\n No internet connection?\n Certificates not installed on your computer?\n\n");
    }

    public void run() {
        // Loading main file describing models, giving information on this, and filling the array of models
        // Accessing the main file
        try {


            int delay = 10000; //milliseconds
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    alertMessage();
                }
            };
            javax.swing.Timer timer = new javax.swing.Timer(delay, taskPerformer);
            timer.start();

            BufferedReader in = URLManager.getBufferedReader(url);
            if (in == null) {
                //alertMessage();

            } else {
                timer.stop();
                jta.append("Connection established...\n");
                String inputLine = null;
                NetworkModel nm = null;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("#FILE")) {
                        nm = new NetworkModel(inputLine.substring(5, inputLine.length()).trim());
                        listOfModels.add(nm);
                    }

                    if (inputLine.startsWith("-FEATURES")) {
                        if (nm != null) {
                            String tmp = inputLine.substring(9, inputLine.length()).trim().toLowerCase();
                            for (int i = 1; i < FEATURES.length; i++) {
                                nm.features[i] = tmp.indexOf(FEATURES[i]) != -1;
                            }
                            //nm.type = NetworkModel.stringToNetworkModelType(inputLine.substring(5, inputLine.length()).trim());
                        }
                    }

                    if (inputLine.startsWith("-PROPS")) {
                        if (nm != null) {
                            String tmp = inputLine.substring(6, inputLine.length()).trim().toLowerCase();
                            for (int i = 0; i < PROPS.length; i++) {
                                nm.props[i] = tmp.indexOf(PROPS[i]) != -1;
                            }
                            //nm.type = NetworkModel.stringToNetworkModelType(inputLine.substring(5, inputLine.length()).trim());
                        }
                    }

                    if (inputLine.startsWith("-AUTHOR")) {
                        if (nm != null) {
                            nm.author = inputLine.substring(7, inputLine.length()).trim();
                        }
                    }


                    if (inputLine.startsWith("-DESCRIPTION")) {
                        if (nm != null) {
                            nm.description = inputLine.substring(12, inputLine.length()).trim();
                        }
                    }

                    if (inputLine.startsWith("-IMG")) {
                        if (nm != null) {
                            nm.image = inputLine.substring(4, inputLine.length()).trim();
                            //TraceManager.addDev("Dealing with image:" + nm.image);
                            //nm.bi = URLManager.getBufferedImageFromURL(URLManager.getBaseURL(url) + nm.image);
                        }
                    }

                    //

                }

                jta.append("\n" + listOfModels.size() + " remote models found.\nSelect a model to download it locally and open it.\n\n");
                mode = LISTED;
                panel.preparePanel(url);
                panel.repaint();
                in.close();

                // Wait 5seconds before refreshing panel
                Thread.sleep(5000);
                panel.repaint();
            }

        } catch (Exception e) {
            alertMessage();
            TraceManager.addDev("Exception trace in loading network model:");
            e.printStackTrace();
        }
    }

    protected void checkMode() {
        mode = NOT_LISTED;
    }

    protected void setButtons() {
        switch (mode) {
            case NOT_LISTED:
                start.setEnabled(false);
                stop.setEnabled(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
            case LISTED:
                start.setEnabled(false);
                stop.setEnabled(true);
                getGlassPane().setVisible(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                break;
            case SELECTED:
            default:
                start.setEnabled(true);
                stop.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
        }
    }


    // LoaderFacilityInterface
    public void load(int index) {
        String fileName = listOfModels.get(index).fileName;
        if (panel != null) panel.stopLoading();
        jta.append("Loading model: " + fileName);
        String urlToLoad = URLManager.getBaseURL(url) + fileName;
        URLManager urlm = new URLManager();
        jfc.setSelectedFile(new File(FileUtils.removeFileExtension(fileName)));
        int returnVal = jfc.showSaveDialog(f);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filePath = jfc.getSelectedFile().getAbsolutePath();
            filePath = FileUtils.addFileExtensionIfMissing(filePath, "xml");
            boolean ok = urlm.downloadFile(filePath, urlToLoad, this);
            if (!ok) {
                loadFailed();
            }
        } else {
            panel.reactivateSelection();
        }
    }

    // CallbackLoaderInterface
    public void loadDone() {
        jta.append("Model transferred, opening it in TTool\n");
        this.dispose();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File dir = new File(filePath.replace(".xml", ""));
                dir = FileUtils.addFileExtensionIfMissing(dir, "ttool");
                dir.mkdir();
                SpecConfigTTool.setDirConfig(dir);
                File config = SpecConfigTTool.createProjectConfig(dir);
                try {
                    SpecConfigTTool.loadConfigFile(config);
                } catch (MalformedConfigurationException e) {
                    System.err.println(e.getMessage() + " : Can't load config file.");
                }
                File file = new File(filePath);
                file = FileUtils.addFileExtensionIfMissing(file, TFileFilter.getExtension());
                try {
                    FileUtils.moveFileToDirectory(file, dir, false);
                } catch (IOException e) {
                    System.err.println(e.getMessage() + " : Network loading failed");
                }
                mgui.openProjectFromFile(dir);
                // Here, we can safely update the GUI
                // because we'll be called from the
                // event dispatch thread
                //statusLabel.setText("Query: " + queryNo);
            }
        });
        //mgui.openProjectFromFile(new File(filePath));
    }

    @Override
    public void loadFailed() {
        jta.append("Model transfer failed\nPlease, select another model, or retry\n");
        panel.reactivateSelection();
    }

    @Override
    public void loadFailed(Exception e) {
        jta.append("Model transfer failed with message:\n");
        jta.append(e.getMessage());
        jta.append("\n\nPlease, select another model, or retry\n");
        panel.reactivateSelection();
    }


    // JTA manipulation by external objects
    public void appendOut(String s) {
        jta.append(s);
    }

}
