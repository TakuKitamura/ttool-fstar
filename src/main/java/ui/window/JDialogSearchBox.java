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

import myutil.*;
import myutil.externalSearch.Client;
import myutil.externalSearch.Message;
import myutil.externalSearch.Record;
import common.ConfigurationTTool;
import ui.util.IconManager;
import myutil.MalformedConfigurationException;
import ui.TDiagramMouseManager;
import web.crawler.WebCrawler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

//TODO : change display to tab.
//TODO: decorate the text box
//TODO : click on Search does not change status immediately
/**
 * JDialogSearchBox dialog for external search with key words Creation:
 * 11/03/2015
 * 
 * @version 1.1 24/03/2016
 * @author Huy TRUONG, Ludoivc Apvrille
 */
public class JDialogSearchBox extends javax.swing.JFrame {
    public static final String bold = "bold";
    public static final String normal = "normal";

    public static final String GOOGLE = "Google";
    public static final String GOOGLE_SCHOLAR = "Google Scholar";
    public static final String DB = "Database";
    public static final String ERROR_URL_INVALID = "URL is invalid";
    public static final String SETUP_ADDRESS_DB = "Setup the address of database";
    public static final int MAXLENGTH_INPUT = 100;
    public static final String ERROR_INPUT_TOO_LONG = "The input is too long.";

    private javax.swing.JList<String> ListKeywords;
    private javax.swing.JComboBox<String> combobox_Score;
    private javax.swing.JComboBox<String> combobox_System;
    private javax.swing.JComboBox<String> combobox_Year;
    private javax.swing.JComboBox<String> combobox_Diagram;
    private javax.swing.JComboBox<String> combobox_Num;
    private javax.swing.JCheckBox databaseCb;
    private javax.swing.JTextPane detailText_db;
    private javax.swing.JTextPane detailText_google;
    private javax.swing.JTextPane detailText_googleScholar;
    private javax.swing.JCheckBox googleCb;
    private javax.swing.JCheckBox googleScholarCb;
    private javax.swing.JButton jButton_Setting;
    private javax.swing.JButton jButton_Statistic;
    private javax.swing.JLabel jLabel_Status;
    private javax.swing.JLabel jLabel_System;
    private javax.swing.JLabel jLabel_Number;
    private javax.swing.JLabel jLabel_Score;
    private javax.swing.JLabel jLabel_Year;
    private javax.swing.JPanel jPanel_DBTab;
    private javax.swing.JPanel jPanel_GoogleTab;
    private javax.swing.JPanel jPanel_GoogleScholarTab;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JButton removeBt;
    private javax.swing.JTable resultTable_db;
    private javax.swing.JTable resultTable_google;
    private javax.swing.JTable resultTable_googleScholar;
    // private TableSorter resultTable_google;
    private javax.swing.JTextField searchBox;
    private javax.swing.JButton searchBt;
    private javax.swing.JButton drawBt;

    private javax.swing.JLabel jLabel_Keyword;
    private javax.swing.JLabel jLabel_Result;
    private JTextField jTextaddressDB;

    // private String search;

    private TableSorter sortTableGS;
    private TableSorter sortTableGSc;
    private TableSorter sortTableDB;

    private String dbaddress;
    private int dbport;
    private int tabCounter = 0;

    private DefaultListModel<String> listModel;
    // ArrayList<GoogleSearch> resultGoogle;
    int searchGoogle;
    // ArrayList<GoogleSearch> resultGoogleScholar;
    int searchGoogleScholar;
    // ArrayList<GoogleSearch> resultDatabase;
    int searchDatabase;

    TDiagramMouseManager tdmm;

    private ArrayList<Object[]> rowsGoogle;
    private ArrayList<Object[]> rowsGoogleScholar;
    private ArrayList<Object[]> rowsDB;

    /* Creates new form */
    public JDialogSearchBox(Frame _frame, String _title, java.util.List<String> l, TDiagramMouseManager tdmm) {
        // super(_frame, _title, true);
        initComponents();
        this.tdmm = tdmm;
        this.setTitle("External Search");
        GraphicLib.centerOnParent(this);
        // String s="";
        for (int i = 0; i < l.size(); i++) {
            addValueListKeyword(l.get(i));
        }
        pack();

        this.setVisible(true);

    }

    public JDialogSearchBox(Frame _frame, String _title, ArrayList<String> l) {
        // super(_frame, _title, true);
        initComponents();
        // this.tdmm = tdmm;
        this.setTitle("External Search");
        GraphicLib.centerOnParent(this);
        // String s="";
        for (int i = 0; i < l.size(); i++) {
            addValueListKeyword(l.get(i));
        }
        pack();

        this.setVisible(true);

    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        ListKeywords = new javax.swing.JList<>();
        removeBt = new javax.swing.JButton();
        searchBt = new javax.swing.JButton();
        searchBox = new javax.swing.JTextField();
        googleCb = new javax.swing.JCheckBox();
        googleScholarCb = new javax.swing.JCheckBox();
        databaseCb = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        // StyledDocument doc = new DefaultStyledDocument();
        StyledDocument doc = new HTMLDocument();
        detailText_google = new javax.swing.JTextPane(doc);
        detailText_googleScholar = new javax.swing.JTextPane(doc);
        jScrollPane4 = new javax.swing.JScrollPane();
        resultTable_google = new javax.swing.JTable();
        resultTable_googleScholar = new javax.swing.JTable();
        listModel = new DefaultListModel<>();
        jLabel_Keyword = new javax.swing.JLabel();

        jLabel_Result = new javax.swing.JLabel();
        jLabel_Status = new javax.swing.JLabel();

        sortTableGS = new TableSorter();
        sortTableGSc = new TableSorter();

        jPanel_GoogleScholarTab = new javax.swing.JPanel();
        jPanel_GoogleTab = new javax.swing.JPanel();
        jPanel_DBTab = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        combobox_Score = new javax.swing.JComboBox<>();
        combobox_System = new javax.swing.JComboBox<>();
        combobox_Year = new javax.swing.JComboBox<>();
        combobox_Num = new javax.swing.JComboBox<>();
        jLabel_System = new javax.swing.JLabel();
        jLabel_Score = new javax.swing.JLabel();
        jLabel_Number = new javax.swing.JLabel();
        jLabel_Year = new javax.swing.JLabel();
        jButton_Setting = new javax.swing.JButton();
        jButton_Statistic = new javax.swing.JButton();
        detailText_db = new JTextPane(doc);
        resultTable_db = new JTable();
        jScrollPane5 = new JScrollPane();
        jScrollPane7 = new JScrollPane();
        jScrollPane8 = new JScrollPane();
        jScrollPane9 = new JScrollPane();
        jTextaddressDB = new JTextField();

        combobox_Diagram = new javax.swing.JComboBox<>();
        drawBt = new JButton();
        String NUM_LIST[] = { "10", "15", "20", "30", "40", "50" };
        String DIAGRAM_LIST[] = { "None", "Statistic", "Histogram" };
        String SYSTEM_LIST[] = { "all", "linux", "windows", "others" };
        String TIME_LIST[] = { "all", "this-year", "last-year" };
        String SCORE_LIST[] = { "all", "8-10", "7-8", "5-7", "0-5" };
        String COLUMNTITLE_G[] = { "No", "Title", "Link" };
        String COLUMNTITLE_GS[] = { "No", "Title", "Author", "Link" };
        String COLUMNTITLE_DB[] = { "No", "ID CVE", "Title", "Score" };

        detailText_google.setContentType("text/html");
        detailText_db.setContentType("text/html");
        detailText_googleScholar.setContentType("text/html");
        try {
            jTextaddressDB.setText(ConfigurationTTool.ExternalServer);
            dbaddress = jTextaddressDB.getText().split(":")[0];
            dbport = Integer.parseInt(jTextaddressDB.getText().split(":")[1]);
        } catch (ArrayIndexOutOfBoundsException exception) {
            dbaddress = "localhost";
            dbport = WebCrawler.PORT;
            jTextaddressDB.setText(dbaddress + ":" + Integer.toString(dbport));
        }

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        searchGoogle = 0;
        searchGoogle = 0;
        searchDatabase = 0;
        rowsGoogle = new ArrayList<>();
        rowsGoogleScholar = new ArrayList<>();

        jLabel_System.setText("System");
        jLabel_Number.setText("Number of result");
        jLabel_Score.setText("Score");
        jLabel_Year.setText("Year");
        jButton_Setting.setText("Setting");
        jButton_Statistic.setText("Statistic");

        ListKeywords.setModel(listModel);
        jScrollPane1.setViewportView(ListKeywords);

        removeBt.setText("Remove");
        removeBt.setActionCommand("Remove");
        removeBt.setEnabled(false);
        drawBt.setText("Draw");
        drawBt.setEnabled(false);
        searchBt.setText("Search");
        combobox_Diagram.setEnabled(false);

        searchBox.setText("Key words");

        googleCb.setText(GOOGLE);

        googleScholarCb.setText(GOOGLE_SCHOLAR);

        databaseCb.setText(DB);

        jLabel_Keyword.setText("Keywords");
        // jLabel3.setText("Search Box");
        jLabel_Result.setText("Results");
        jLabel_Status.setText("Status");
        jButton_Setting.setText("Setting");

        jButton_Statistic.setText("Statistic");

        jLabel_System.setText("System");

        this.jButton_Setting.setEnabled(false);
        this.jButton_Statistic.setEnabled(false);
        this.combobox_System.setEnabled(false);
        this.combobox_Score.setEnabled(false);
        this.combobox_Year.setEnabled(false);

        // --------------------------------------------------------

        detailText_google.setBounds(0, 0, 20, 5);
        detailText_google.setEditable(false);
        jScrollPane3.setViewportView(detailText_google);

        sortTableGS = new TableSorter(new javax.swing.table.DefaultTableModel(new Object[][] {}, COLUMNTITLE_G) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });

        resultTable_google = new JTable(sortTableGS);
        sortTableGS.setTableHeader(resultTable_google.getTableHeader());

        resultTable_google.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jScrollPane4.setViewportView(resultTable_google);

        resultTable_google.getColumnModel().getColumn(0).setMaxWidth(40);

        resultTable_google.getColumnModel().getColumn(2).setMinWidth(400);

        javax.swing.GroupLayout jPanel_GoogleTabLayout = new javax.swing.GroupLayout(jPanel_GoogleTab);
        jPanel_GoogleTab.setLayout(jPanel_GoogleTabLayout);

        GroupLayout.ParallelGroup group = jPanel_GoogleTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        jPanel_GoogleTabLayout.setHorizontalGroup(
                jPanel_GoogleTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING));
        jPanel_GoogleTabLayout.setVerticalGroup(jPanel_GoogleTabLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_GoogleTabLayout.createSequentialGroup().addContainerGap()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 105,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)));

        jTabbedPane2.addTab(GOOGLE, jPanel_GoogleTab);

        ///
        detailText_googleScholar.setBounds(0, 0, 20, 5);
        detailText_googleScholar.setEditable(false);
        jScrollPane8.setViewportView(detailText_googleScholar);

        sortTableGSc = new TableSorter(new javax.swing.table.DefaultTableModel(new Object[][] {}, COLUMNTITLE_GS) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });

        resultTable_googleScholar = new JTable(sortTableGSc);
        sortTableGSc.setTableHeader(resultTable_googleScholar.getTableHeader());

        resultTable_googleScholar.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jScrollPane9.setViewportView(resultTable_googleScholar);

        resultTable_googleScholar.getColumnModel().getColumn(0).setMaxWidth(40);

        javax.swing.GroupLayout jPanel_GoogleScholarTabLayout = new javax.swing.GroupLayout(jPanel_GoogleScholarTab);
        jPanel_GoogleScholarTab.setLayout(jPanel_GoogleScholarTabLayout);

        // GroupLayout.ParallelGroup groupScholar =
        // jPanel_GoogleScholarTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        jPanel_GoogleScholarTabLayout.setHorizontalGroup(
                jPanel_GoogleScholarTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING));
        jPanel_GoogleScholarTabLayout.setVerticalGroup(jPanel_GoogleScholarTabLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_GoogleScholarTabLayout.createSequentialGroup().addContainerGap()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 105,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)));

        jTabbedPane2.addTab(GOOGLE_SCHOLAR, jPanel_GoogleScholarTab);

        /////

        sortTableDB = new TableSorter(new javax.swing.table.DefaultTableModel(new Object[][] {}, COLUMNTITLE_DB) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });

        resultTable_db = new JTable(sortTableDB);
        sortTableDB.setTableHeader(resultTable_db.getTableHeader());

        resultTable_db.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jScrollPane5.setViewportView(resultTable_db);
        resultTable_db.getColumnModel().getColumn(0).setMaxWidth(40);
        resultTable_db.getColumnModel().getColumn(1).setMaxWidth(200);
        resultTable_db.getColumnModel().getColumn(1).setMinWidth(150);
        resultTable_db.getColumnModel().getColumn(2).setMinWidth(90);
        resultTable_db.getColumnModel().getColumn(3).setMaxWidth(60);
        resultTable_db.getColumnModel().getColumn(3).setMinWidth(60);
        jScrollPane7.setViewportView(detailText_db);

        javax.swing.GroupLayout jPanel_DBTabLayout = new javax.swing.GroupLayout(jPanel_DBTab);
        jPanel_DBTab.setLayout(jPanel_DBTabLayout);
        jPanel_DBTabLayout
                .setHorizontalGroup(jPanel_DBTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                        .addComponent(jScrollPane7));
        jPanel_DBTabLayout.setVerticalGroup(jPanel_DBTabLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_DBTabLayout.createSequentialGroup().addContainerGap()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 105,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)));

        jTabbedPane2.addTab(DB, jPanel_DBTab);

        combobox_System.setModel(new javax.swing.DefaultComboBoxModel<>(SYSTEM_LIST));

        combobox_Year.setModel(new javax.swing.DefaultComboBoxModel<>(TIME_LIST));

        combobox_Score.setModel(new javax.swing.DefaultComboBoxModel<>(SCORE_LIST));

        combobox_Diagram.setModel(new javax.swing.DefaultComboBoxModel<>(DIAGRAM_LIST));

        combobox_Num.setModel(new javax.swing.DefaultComboBoxModel<>(NUM_LIST));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jTabbedPane2)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup().addComponent(googleCb)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)

                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(databaseCb).addComponent(googleScholarCb)
                                                        .addComponent(removeBt))))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jButton_Setting, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)

                                )
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel_Score).addComponent(jLabel_Year)
                                        .addComponent(jLabel_System).addComponent(jLabel_Number)
                                        .addComponent(combobox_Diagram))
                                .addGap(37, 37, 37)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(combobox_Score, 0, 142, Short.MAX_VALUE)
                                        .addComponent(combobox_Year, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(combobox_System, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(combobox_Num, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)))
                        .addGroup(layout.createSequentialGroup().addComponent(jLabel_Status).addGap(0, 0,
                                Short.MAX_VALUE))

                        .addGroup(layout.createSequentialGroup().addComponent(searchBox).addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(searchBt, javax.swing.GroupLayout.PREFERRED_SIZE, 89,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(drawBt, javax.swing.GroupLayout.PREFERRED_SIZE, 89,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))

                        )).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 122,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel_System, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(googleCb)

                                                .addComponent(combobox_System, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(googleScholarCb)
                                // .addComponent(combobox_Diagram, javax.swing.GroupLayout.PREFERRED_SIZE,
                                // javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                )
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(removeBt))
                        .addGroup(layout.createSequentialGroup().addGap(35, 35, 35)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(combobox_Score, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel_Score))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(databaseCb).addComponent(jButton_Setting)
                                        .addComponent(combobox_Year, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel_Year))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(combobox_Num, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel_Number))))
                .addGap(10, 10, 10)

                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(combobox_Diagram).addComponent(drawBt))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(searchBox, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(searchBt))

                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jTabbedPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel_Status)
                .addGap(6, 6, 6)));

        removeBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBtActionPerformed(evt);
            }
        });

        searchBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                checkandsetSearchBt();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                checkandsetSearchBt();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                checkandsetSearchBt();
            }

            public void checkandsetSearchBt() {

                if (searchBox.getText().length() <= 0 || searchBox.getText().length() > MAXLENGTH_INPUT) {
                    if (searchBox.getText().length() > MAXLENGTH_INPUT)
                        JOptionPane.showMessageDialog(null, ERROR_INPUT_TOO_LONG, "Warning",
                                JOptionPane.WARNING_MESSAGE);
                    searchBt.setEnabled(false);
                    drawBt.setEnabled(false);
                } else if (combobox_Diagram.getSelectedItem() != "None")
                    drawBt.setEnabled(true);
                searchBt.setEnabled(true);

                if (!isPrintableString(searchBox.getText())) {
                    JOptionPane.showMessageDialog(null, "Not printable character", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    searchBt.setEnabled(false);
                    drawBt.setEnabled(false);
                }
            }
        });

        searchBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtActionPerformed(evt);

            }
        });

        googleCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (googleCb.isSelected() && ListKeywords.getModel().getSize() > 0)
                    searchBt.setEnabled(true);
                googleCbActionPerformed(evt);
            }
        });

        databaseCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (databaseCb.isSelected()) {
                    combobox_Diagram.setEnabled(true);
                    if (combobox_Diagram.getSelectedItem() != "None")
                        drawBt.setEnabled(true);
                    else
                        drawBt.setEnabled(false);
                } else {
                    combobox_Diagram.setEnabled(false);
                    drawBt.setEnabled(false);
                }
                if (databaseCb.isSelected() && ListKeywords.getModel().getSize() > 0) {
                    searchBt.setEnabled(true);
                }
                databaseCbActionPerformed(evt);
            }
        });

        googleScholarCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (googleScholarCb.isSelected() && ListKeywords.getModel().getSize() > 0) {
                    searchBt.setEnabled(true);
                    drawBt.setEnabled(true);
                } else {
                    // searchBt.setEnabled(false);
                    drawBt.setEnabled(false);
                }
                googleScholarCbActionPerformed(evt);
            }
        });

        resultTable_google.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectrow(e, rowsGoogle, resultTable_google, detailText_google, 0);
            }
        });
        resultTable_google.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = resultTable_google.getSelectedRow();
                // int col = resultTable_google.getSelectedColumn();

                // if (col == 2) {
                if (e.getClickCount() == 2) {
                    String st = (String) resultTable_google.getValueAt(row, 2);
                    URI uri = URI.create(st);
                    Desktop d = Desktop.getDesktop();
                    try {
                        d.browse(uri);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, ERROR_URL_INVALID, "Warning", JOptionPane.WARNING_MESSAGE);
                    }

                }

            }
        });

        resultTable_googleScholar.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectrow(e, rowsGoogleScholar, resultTable_googleScholar, detailText_googleScholar, 1);
            }
        });

        resultTable_googleScholar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = resultTable_googleScholar.getSelectedRow();
                // int col = resultTable_googleScholar.getSelectedColumn();

                if (e.getClickCount() == 2) {
                    String st = (String) resultTable_googleScholar.getValueAt(row, 3);
                    URI uri = URI.create(st);
                    Desktop d = Desktop.getDesktop();
                    try {
                        d.browse(uri);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, ERROR_URL_INVALID, "Warning", JOptionPane.WARNING_MESSAGE);
                    }

                }

            }
        });
        resultTable_db.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectrow(e, rowsDB, resultTable_db, detailText_db, 2);
            }
        });

        resultTable_db.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                // int col = resultTable_db.getSelectedColumn();

                if (e.getClickCount() == 2) {

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int row = resultTable_db.getSelectedRow();
                            String st = (String) resultTable_db.getValueAt(row, 1);

                            Message msg = new Message(Message.CMD_DETAIL);
                            msg.addKeywordMessage(st);

                            // Client cl = new Client();)
                            Message returnMsg = sendMessage(msg);

                            if (returnMsg != null) {
                                Record r = parserMessage(returnMsg).get(0);
                                printDetailRecord(r);
                            }
                        }
                    });
                    t.start();

                }

            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                WindowClosing(evt);
            }
        });

        ListKeywords.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                ListKeywordsComponentAdded(evt);
            }
        });

        ListKeywords.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (((JList) listSelectionEvent.getSource()).getSelectedIndex() != -1)
                    removeBt.setEnabled(true);
                else
                    removeBt.setEnabled(false);
            }
        });

        jButton_Setting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                final JComponent[] inputs = new JComponent[] { new JLabel("Address"), jTextaddressDB };

                JOptionPane joptionpane = new JOptionPane();
                int i = JOptionPane.showOptionDialog(null, inputs, SETUP_ADDRESS_DB, JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, new Object[] { "Save", "Cancel" }, "OK");
                if (i == JOptionPane.OK_OPTION) {
                    while (!isAddressDBFormatted()) {
                        JOptionPane.showMessageDialog(null, "Address:Port", "Wrong format",
                                JOptionPane.WARNING_MESSAGE);
                        i = JOptionPane.showOptionDialog(null, inputs, SETUP_ADDRESS_DB, JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE, null, new Object[] { "Save", "Cancel" }, "OK");
                    }
                    ConfigurationTTool.ExternalServer = jTextaddressDB.getText();
                    dbaddress = jTextaddressDB.getText().split(":")[0];
                    dbport = Integer.parseInt(jTextaddressDB.getText().split(":")[1]);
                    try {
                        if (tdmm.getTdp().getMainGUI().getDir() != null)
                            tdmm.getTdp().getMainGUI().saveConfig();
                        ConfigurationTTool.saveConfiguration();
                    } catch (MalformedConfigurationException e) {
                        e.printStackTrace();
                    }
                } else if (i == JOptionPane.CLOSED_OPTION) {
                }
            }

            public boolean isAddressDBFormatted() {
                return jTextaddressDB.getText().contains(":") && jTextaddressDB.getText().split(":").length == 2
                        && isNum(jTextaddressDB.getText().split(":")[1]);
            }
        }

        );

        drawBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // System.out.print(combobox_Diagram.getSelectedItem());

                if (combobox_Diagram.getSelectedItem() == "Statistic") {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            doStatistic();
                        }
                    });
                    t.start();
                }
                if (combobox_Diagram.getSelectedItem() == "Histogram") {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            doHistogram();
                        }
                    });
                    t.start();
                }

            }
        });

        jTextaddressDB.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {

            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {

            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {

            }

        });

        combobox_Diagram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (combobox_Diagram.getSelectedItem() == "None" || searchBox.getText().length() <= 0)
                    drawBt.setEnabled(false);
                else if ((combobox_Diagram.getSelectedItem() != "None" && searchBox.getText().length() > 0))
                    drawBt.setEnabled(true);
            }
        });

        pack();
    }//

    private void WindowClosing(WindowEvent evt) {
        if (tdmm != null)
            tdmm.clearSelectComponents();
        this.dispose();
    }

    /**
     *
     * @param msg : message
     * @return message to send back to client
     */
    public Message sendMessage(Message msg) {
        Client cl = new Client();
        try {
            return cl.send(msg, dbaddress, dbport, true);
        } catch (IOException e) {
            TraceManager.addDev("Connection error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Cannot connect to server !!!", "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        } catch (ClassNotFoundException e) {
            TraceManager.addDev("Classe error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Cannot parse message!!!", "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }

    }

    public ArrayList<Record> parserMessage(Message msg) {
        Client cl = new Client();
        return cl.parserAnswerMessage(msg);
    }

    public byte[] parserMessageAsBytes(Message msg) {
        Client cl = new Client();
        return cl.parserAnswerMessageAsBytes(msg);
    }

    private void ListKeywordsComponentAdded(java.awt.event.ContainerEvent evt) {
        this.removeBt.setEnabled(true);
    }

    private void removeBtActionPerformed(java.awt.event.ActionEvent evt) {
        this.jLabel_Status.setText("Ready");
        if (listModel.getSize() != 0) {
            int index = this.ListKeywords.getSelectedIndex();
            listModel.remove(index);

            String query = "";
            // first value
            if (this.listModel.getSize() > 0) {
                String element = this.listModel.elementAt(0);
                String value = splitAndConcat(element);
                query = value;
            }

            for (int i = 1; i < this.listModel.getSize(); i++) {

                String element = this.listModel.elementAt(i);
                String value = splitAndConcat(element);

                query = query + " + " + value; // (String) this.listModel.elementAt(i);
            }

            this.searchBox.setText(query);

            int size = listModel.getSize();

            if (size == 0) {
                this.removeBt.setEnabled(false);

            } else { // Select an index.
                if (index == listModel.getSize()) {
                    // removed item in last position
                    index--;
                }

                this.ListKeywords.setSelectedIndex(index);
                this.ListKeywords.ensureIndexIsVisible(index);
            }

        }

    }

    /**
     *
     * @param evt
     */
    private void searchBtActionPerformed(java.awt.event.ActionEvent evt) {

        // reset Tab title
        jTabbedPane2.setTitleAt(0, GOOGLE);
        jTabbedPane2.setTitleAt(1, GOOGLE_SCHOLAR);
        jTabbedPane2.setTitleAt(2, DB);

        if (searchGoogle == 0 && searchGoogleScholar == 0 && searchDatabase == 0) {
            JOptionPane.showMessageDialog(null, "Please select the resource to search", "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                jLabel_Status.setText("The crawler is running ....");
                searchBt.setEnabled(false);

                // reset content of table
                // DefaultTableModel modelGoogle = (DefaultTableModel)
                // resultTable_google.getModel();
                DefaultTableModel modelGoogle = (DefaultTableModel) sortTableGS.getTableModel();
                DefaultTableModel modelGoogleScholar = (DefaultTableModel) sortTableGSc.getTableModel();
                DefaultTableModel modelDB = (DefaultTableModel) sortTableDB.getTableModel();

                modelGoogle.setRowCount(0);
                modelGoogleScholar.setRowCount(0);
                modelDB.setRowCount(0);

                detailText_google.setText("");
                detailText_googleScholar.setText("");
                detailText_db.setText("");

                // ensure there is at least the resources for crawling.

                int id;
                rowsGoogle = new ArrayList<Object[]>();
                rowsGoogleScholar = new ArrayList<Object[]>();
                rowsDB = new ArrayList<Object[]>();
                // check internet connection before crawling from google or google scholar
                Boolean internetConnectionAvailable = null;
                if (searchGoogle == 1 || searchGoogleScholar == 1) {
                    internetConnectionAvailable = CheckConnection.checkInternetConnection();
                    if (internetConnectionAvailable) {

                        ArrayList<GoogleSearch> resultGoogle = null;
                        ArrayList<GoogleSearch> resultGoogleScholar = null;
                        // get the content of searhBox
                        String query = searchBox.getText();
                        if (query != "") {
                            if (searchGoogle == 1) {
                                jLabel_Status.setText("Retrieving data from Google");

                                resultGoogle = GoogleSearch.getGoogleResult(searchBox.getText(),
                                        (String) combobox_Num.getSelectedItem());
                                jLabel_Status.setText("Done");

                                if (resultGoogle == null) {
                                    JOptionPane.showMessageDialog(null, "Can't get the result from Google\n",
                                            "Retrieving data is failed", JOptionPane.ERROR_MESSAGE);
                                    jLabel_Status.setText("Failed to retrieving data from Google");
                                } else if (resultGoogle != null) {
                                    if (resultGoogle.size() == 0) {
                                        JOptionPane.showMessageDialog(null,
                                                "No result\n " + "Please check the keywords you have entered",
                                                "No result", JOptionPane.ERROR_MESSAGE);
                                        jLabel_Status.setText("Google returned no data");
                                    } else {
                                        if (resultGoogle.get(0).getTitle() == GoogleSearch.IOEx) {
                                            JOptionPane.showMessageDialog(null,
                                                    "Can connect to Google\n " + "Please check the internet connection",
                                                    "Connection Error", JOptionPane.ERROR_MESSAGE);
                                            jLabel_Status.setText("Failed to retrieving data from Google");
                                        } else {
                                            putGoogleToTable(resultGoogle);
                                            showtable(rowsGoogle, modelGoogle, 0);
                                        }
                                    }

                                }
                            }

                            if (searchGoogleScholar == 1) {
                                jLabel_Status.setText("Retrieving data from Google Scholar");
                                // jLabel_Status.updateUI();
                                resultGoogleScholar = GoogleSearch.getGoogleScholarResult(searchBox.getText(),
                                        (String) combobox_Num.getSelectedItem());
                                jLabel_Status.setText("Done");
                                // jLabel_Status.updateUI();
                                if (resultGoogleScholar == null) {
                                    JOptionPane.showMessageDialog(null, "Cannot get the result from Google Scholar \n",
                                            "Retrieving data is failed", JOptionPane.ERROR_MESSAGE);
                                    jLabel_Status.setText("Failed to retrieving data from Google Scholar");
                                } else if ((resultGoogleScholar != null) && (resultGoogleScholar.size() > 0)) {
                                    if (resultGoogleScholar.get(0).getTitle() == GoogleSearch.IOEx) {
                                        JOptionPane.showMessageDialog(null,
                                                "Can't connect to Google Scholar\n "
                                                        + "Please check the internet connection",
                                                "Connection Error", JOptionPane.ERROR_MESSAGE);
                                        jLabel_Status.setText("Failed to retrieving data from Google Scholar");
                                    } else {
                                        putGoogleScholarToTable(resultGoogleScholar);
                                        showtable(rowsGoogleScholar, modelGoogleScholar, 1);
                                    }
                                }
                            }
                        }

                    } else {
                        jLabel_Status.setText("Failed to connect to resource");
                        JOptionPane.showMessageDialog(null, "Cannot connect to Google", "Connection Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

                // TODO : cralwer data from DB, must check the connection.
                if (searchDatabase == 1) {
                    jLabel_Status.setText("Retrieving data from DB");
                    Message msg = new Message(Message.CMD_SEARCH);

                    msg.addKeywordMessage(searchBox.getText());
                    msg.addOptionValueMessage(Message.OPTION_YEAR, (String) combobox_Year.getSelectedItem());
                    msg.addOptionValueMessage(Message.OPTION_SYSTEM, (String) combobox_System.getSelectedItem());
                    msg.addOptionValueMessage(Message.OPTION_SCORE, (String) combobox_Score.getSelectedItem());
                    msg.addOptionValueMessage(Message.OPTION_NUMBER, (String) combobox_Num.getSelectedItem());

                    Message returnMsg = sendMessage(msg);

                    ArrayList<Record> re = parserMessage(returnMsg);
                    putDBToTable(re);
                    showtable(rowsDB, modelDB, 2);

                }

            }
        });
        t.start();
    }

    /**
     * Display content in table.
     * 
     * @param list       : content
     * @param model      : Default table model
     * @param objectType 0: google 1: google scholar 2: database
     */
    public void showtable(ArrayList<Object[]> list, DefaultTableModel model, int objectType) {
        int id = 0;
        if (objectType == 1 || objectType == 0) {
            for (Object[] o : list) {
                id = (Integer) (o[0]);
                GoogleSearch gs = (GoogleSearch) (o[1]);
                if (objectType == 0)
                    model.addRow(new Object[] { id, gs.getTitle(), gs.getUrl() });
                else if (objectType == 1)
                    model.addRow(new Object[] { id, gs.getTitle(), gs.getAuthors(), gs.getUrl() });

            }
        } else if (objectType == 2) {
            for (Object[] o : list) {
                id = (Integer) (o[0]);
                Record r = (Record) (o[1]);
                model.addRow(new Object[] { id, r.getCve_id(), r.getName(), r.getScore() });
            }
        }

        if (objectType == 0) {
            jTabbedPane2.setTitleAt(objectType, GOOGLE + " [" + model.getRowCount() + "]");
        } else if (objectType == 1) {
            jTabbedPane2.setTitleAt(objectType, GOOGLE_SCHOLAR + " [" + model.getRowCount() + "]");
        } else if (objectType == 2) {
            jTabbedPane2.setTitleAt(objectType, DB + " [" + model.getRowCount() + "]");
        }
        searchBt.setEnabled(true);
        jLabel_Status.setText("Finished");
    }

    /**
     *
     * @param evt         : {@link ListSelectionEvent}
     * @param rows        : list of rows
     * @param resultTable : {@link JTable}
     * @param textpane    : {@link JTextPane}
     * @param typeObject  0: google 1: google scholar 2: database
     */
    private void selectrow(ListSelectionEvent evt, ArrayList<Object[]> rows, JTable resultTable, JTextPane textpane,
            int typeObject) {
        int rowindex = resultTable.getSelectedRow();
        int id = 0;

        if (rowindex >= 0)
            id = (Integer) resultTable.getValueAt(rowindex, 0);
        if (typeObject == 1 || typeObject == 0) {
            GoogleSearch selected = null;
            for (Object[] o : rows) {
                if (o[0].equals(id)) {
                    selected = (GoogleSearch) o[1];
                    break;
                }
            }
            if (typeObject == 0)
                presentDataInDetail(selected, 0, textpane);
            else
                presentDataInDetail(selected, 1, textpane);
        } else {
            Record selected = null;
            for (Object[] o : rows) {
                if (o[0].equals(id)) {
                    selected = (Record) o[1];
                    break;
                }
            }
            presentDataInDetail(selected, 2, textpane);
        }
    }

    private void googleCbActionPerformed(java.awt.event.ActionEvent evt) {
        this.jLabel_Status.setText("Ready");
        this.jLabel_Status.updateUI();
        if (this.googleCb.isSelected())
            this.searchGoogle = 1;
        else
            this.searchGoogle = 0;
    }

    private void googleScholarCbActionPerformed(java.awt.event.ActionEvent evt) {
        this.jLabel_Status.setText("Ready");
        this.jLabel_Status.updateUI();
        if (this.googleScholarCb.isSelected())
            this.searchGoogleScholar = 1;
        else
            this.searchGoogleScholar = 0;
    }

    private void databaseCbActionPerformed(java.awt.event.ActionEvent evt) {
        this.jLabel_Status.setText("Ready");
        this.jLabel_Status.updateUI();
        if (this.databaseCb.isSelected()) {
            this.searchDatabase = 1;
            this.jButton_Setting.setEnabled(true);
            this.jButton_Statistic.setEnabled(true);
            this.combobox_System.setEnabled(true);
            this.combobox_Score.setEnabled(true);
            this.combobox_Year.setEnabled(true);

        } else {
            this.searchDatabase = 0;
            this.jButton_Setting.setEnabled(false);
            this.jButton_Statistic.setEnabled(false);
            this.combobox_System.setEnabled(false);
            this.combobox_Score.setEnabled(false);
            this.combobox_Year.setEnabled(false);
        }
    }

    /**
     * add new value into Keyword list.
     * 
     * @param st : new value into Keyword list
     */
    public void addValueListKeyword(String st) {
        if (!this.listModel.contains(st)) {
            this.listModel.addElement(st);

            String query = "";
            if (this.listModel.getSize() > 0)
                // split the string
                query = splitAndConcat(this.listModel.elementAt(0));
            for (int i = 1; i < this.listModel.getSize(); i++) {
                if (query != "")
                    query = query + " + " + splitAndConcat(this.listModel.elementAt(i));
                else
                    query = splitAndConcat(this.listModel.elementAt(i));
            }
            this.searchBox.setText(query);
        }

    }

    public void removeValueListKeyword() {
        this.listModel.clear();
    }

    public void putGoogleToTable(ArrayList<GoogleSearch> a) {
        if (a != null) {
            int i = this.rowsGoogle.size() + 1;
            for (GoogleSearch gs : a) {
                this.rowsGoogle.add(new Object[] { i, gs });
                i = i + 1;
            }
        }

    }

    public void putDBToTable(ArrayList<Record> a) {
        if (a != null) {
            int i = this.rowsDB.size() + 1;
            for (Record record : a) {
                this.rowsDB.add(new Object[] { i, record });
                i = i + 1;
            }
        }

    }

    public void putGoogleScholarToTable(ArrayList<GoogleSearch> a) {
        if (a != null) {
            int i = this.rowsGoogleScholar.size() + 1;
            for (GoogleSearch gs : a) {
                rowsGoogleScholar.add(new Object[] { i, gs });
                i = i + 1;
            }
        }
    }

    private void printDetailRecord(Record r) {
        if (r != null)
            this.detailText_db.setText(formatOutput_DB_DETAIL(r));
    }

    /**
     *
     * @param obj        : {@link Object}
     * @param typeObject 0: obj from google. 1: obj from googleScholar 2: obj from
     *                   database
     * @param textPane   : {@link JTextPane}
     */
    public void presentDataInDetail(Object obj, int typeObject, JTextPane textPane) {

        textPane.setContentType("text/html");
        if (typeObject == 0) {
            GoogleSearch gs = (GoogleSearch) obj;
            if (gs != null)
                textPane.setText(formatOutput_Google(gs));
        }
        if (typeObject == 1) {
            GoogleSearch gs = (GoogleSearch) obj;
            if (gs != null)
                textPane.setText(formatOutput_GoogleScholar(gs));
        }
        if (typeObject == 2) {
            Record r = (Record) obj;
            if (r != null) {
                textPane.setText(formatOutput_DB_SHORT(r));
            }
        }
    }

    // ==========================================
    // database functions

    public void doStatistic() {
        BufferedImage img = null;
        try {
            Message msg = new Message(Message.CMD_STATISTIC);
            msg.addKeywordMessage(this.searchBox.getText());
            Message ret = sendMessage(msg);
            if (ret != null) {
                byte[] b = parserMessageAsBytes(ret);
                if (b != null) {
                    ByteArrayInputStream in = new ByteArrayInputStream(b);
                    img = ImageIO.read(in);
                    ImageIcon icon = new ImageIcon(img);
                    Image scaleImage = icon.getImage().getScaledInstance(650, 300, java.awt.Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaleImage);
                    addTab("Statistic", icon);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot display diagram !!!", "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void doHistogram() {
        BufferedImage img = null;
        try {
            Message msg = new Message(Message.CMD_HISTOGRAM);
            msg.addKeywordMessage(this.searchBox.getText());
            Message ret = sendMessage(msg);
            if (ret != null) {
                byte[] b = parserMessageAsBytes(ret);
                if (b != null) {
                    ByteArrayInputStream in = new ByteArrayInputStream(b);
                    img = ImageIO.read(in);
                    ImageIcon icon = new ImageIcon(img);
                    Image scaleImage = icon.getImage().getScaledInstance(700, 300, java.awt.Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaleImage);
                    addTab("Histogram", icon);

                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot display diagram !!!", "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void removeTab(String name) {
        for (int i = 0; i < jTabbedPane2.getTabCount(); i++) {
            if (jTabbedPane2.getTitleAt(i).equals(name)) {
                jTabbedPane2.remove(i);
            }
        }
    }

    private void addTab(String name, ImageIcon icon) {
        tabCounter++;
        JLabel imageLabel = new JLabel(icon);
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        removeTab(name);
        jTabbedPane2.addTab(name, scrollPane);
        // jTabbedPane2.setTabComponentAt(3, tabCloseButton);

        int index = jTabbedPane2.indexOfTab(name);
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(name);
        // JButton btnClose = new JButton("x");
        JButton btnClose = new JButton();

        btnClose.setOpaque(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        // btnClose.setPreferredSize(new Dimension(25, 25));
        btnClose.setMargin(new Insets(0, 0, 0, 0));
        btnClose.setIcon(IconManager.imgic26);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        pnlTab.add(lblTitle, gbc);

        gbc.gridx++;
        gbc.gridx++;
        gbc.gridy--;
        gbc.weightx = 0;
        pnlTab.add(btnClose, gbc);

        jTabbedPane2.setTabComponentAt(index, pnlTab);
        btnClose.setActionCommand("" + tabCounter);

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JButton btn = (JButton) actionEvent.getSource();
                String s1 = btn.getActionCommand();
                for (int i = 1; i < jTabbedPane2.getTabCount(); i++) {
                    JPanel pnl = (JPanel) jTabbedPane2.getTabComponentAt(i);
                    if (pnl != null) {
                        btn = (JButton) pnl.getComponent(1);
                        String s2 = btn.getActionCommand();
                        if (s1.equals(s2)) {
                            jTabbedPane2.removeTabAt(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     *
     * @param input a string without space and words are seperated by uper
     *              character.
     * @return a splited, then concaternated with space.
     */
    public String splitAndConcat(String input) {
        String value = "";
        if (input.contains("_")) {
            String[] splitValue = input.split("_");

            if (splitValue.length > 0) {
                value = splitValue[0];
                for (int i = 1; i < splitValue.length; i++) {
                    value = value + " " + splitValue[i];
                }
            }
        } else {
            String[] splitValue = input.split("(?=\\p{Lu})");
            if (splitValue.length > 0) {
                value = splitValue[0];
                for (int i = 1; i < splitValue.length; i++) {
                    value = value + " " + splitValue[i];
                }
            }
        }

        return value;
    }

    /**
     * Check a string if it contains only number.
     * 
     * @param str : string
     * @return : true if the contains only number
     */
    public boolean isNum(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * create html format for content from Google Scholar
     * 
     * @param gs : {@link GoogleSearch}
     * @return String
     */
    public String formatOutput_GoogleScholar(GoogleSearch gs) {
        String html = "<html>" + "<title>" + gs.getTitle() + "</title>" + "<body style=\"font-size: 14pt\">"
                + "   <b>Title :  </b>" + gs.getTitle() + "<br>" + "   <b>Author: </b>" + gs.getAuthors() + "<br>"
                + "   <b>Link :  </b><u>" + gs.getUrl() + "</u><br>" + "   <b>Cited : </b>" + gs.getCitedNumber()
                + "<br>" + "   <b>Description : </b>" + gs.getDesc() + "<br>" + "</body>" + "</html>";
        return html;
    }

    /**
     * create html format for content from Google
     * 
     * @param gs {@link GoogleSearch}
     * @return String
     */
    public String formatOutput_Google(GoogleSearch gs) {
        String html = "<html>" + "<title>" + gs.getTitle() + "</title>" + "<body style=\"font-size: 14pt\">"
                + "   <b>Title :  </b>" + gs.getTitle() + "<br>" + "   <b>Link :  </b><u><i>" + gs.getUrl()
                + "</i></u><br>" + "   <b>Description : </b>" + gs.getDesc() + "<br>" + "</body>" + "</html>";
        return html;
    }

    /**
     * create html format for content from Database (not detail)
     * 
     * @param r {@link Record}
     * @return String
     */
    public String formatOutput_DB_SHORT(Record r) {
        String html = "<html>" + "<body  style=\"font-size: 14pt\">" + "   <b>Title:  </b>" + r.getName() + "<br>"
                + "   <b>ID : </b>" + r.getCve_id() + "<br>" + "   <b>Score : </b>" + r.getScore() + "<br>"
                + "   <b>Summary : </b>" + r.getSummary() + "<br>" +

                "</body>" + "</html>";
        return html;
    }

    /**
     * create html format for content from Database (detail)
     * 
     * @param r {@link Record}
     * @return String
     */
    public String formatOutput_DB_DETAIL(Record r) {
        String html = "<html>" + "<body  style=\"font-size: 14pt\">" + "   <b>Title:  </b>" + r.getName() + "<br>"
                + "   <b>CVE ID : </b>" + r.getCve_id() + "<br>" + "   <b>CWE ID : </b>" + r.getCwe_id() + "<br>"
                + "   <b>Link : </b>" + r.getLink() + "<br>" + "   <b>Public date:  </b>" + r.getPub_date() + "<br>"
                + "   <b>Modification date : </b>" + r.getMod_date() + "<br>" + "   <b>Gen_date : </b>"
                + r.getGen_date() + "<br>" + "   <b>Score:  </b>" + r.getScore() + "<br>" + "   <b>ID : </b>"
                + r.getCve_id() + "<br>" + "   <b>Confidentiality impact : </b>" + r.getConfidentiality_impact()
                + "<br>" + "   <b>Integrity impact:  </b>" + r.getIntegrity_impact() + "<br>"
                + "   <b>Availability impact : </b>" + r.getAvailability_impact() + "<br>" + "   <b>Summary : </b>"
                + r.getSummary() + "<br>" + "</body>" + "</html>";
        return html;
    }

    /**
     * Check the printable character.
     * 
     * @param c : character
     * @return if the character is printable
     */
    public boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) && c != KeyEvent.CHAR_UNDEFINED && block != null
                && block != Character.UnicodeBlock.SPECIALS;
    }

    /**
     * Check the printable string
     * 
     * @param s string
     * @return true if the string is printable
     */
    public boolean isPrintableString(String s) {
        if (s == null)
            return false;
        else {
            int l = s.length();
            for (int i = 0; i < l; i++) {
                if (isPrintableChar(s.charAt(i)) == false) {
                    return false;
                }
            }
            return true;
        }
    }

}
