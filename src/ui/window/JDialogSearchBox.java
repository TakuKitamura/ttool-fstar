/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * JDialogSearchBox
 * dialog for external search with key words
 * Creation: 11/03/2015
 * @version 1.0 11/03/2015
 * @author Huy TRUONG
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import myutil.GoogleSearch;
import myutil.GraphicLib;

import java.lang.Object;
import java.awt.Desktop;
import java.lang.reflect.Array;
import java.net.URI;

import myutil.TableSorter;

//import javax.swing.event.*;
import java.util.*;

import javax.swing.table.DefaultTableModel;

import myutil.CheckConnection;
//import myutil.Message;
//import myutil.Client;
import myutil.externalSearch.Record;
import ui.TDiagramMouseManager;
import myutil.externalSearch.Message;
import myutil.externalSearch.Client;

//TODO : change display to tab.
//TODO: decorate the text box
//TODO : click on Search does not change status immediately
public class JDialogSearchBox extends javax.swing.JFrame  {
    public static final String bold= "bold";
    public static final String normal = "normal";
    public static String default_address = "localhost";
    public static int default_port = 9999;

	private javax.swing.JList ListKeywords;
    private javax.swing.JComboBox combobox_Score;
    private javax.swing.JComboBox combobox_System;
    private javax.swing.JComboBox combobox_Year;
    private javax.swing.JCheckBox databaseCb;
    private javax.swing.JTextPane detailText_db;
    private javax.swing.JTextPane detailText_google;
    private javax.swing.JTextPane detailText_googleScholar;
    private javax.swing.JCheckBox googleCb;
    private javax.swing.JCheckBox googleScholarCb;
    private javax.swing.JButton jButton_Setting;
    private javax.swing.JButton jButton_Statistic;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel_System;
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
    //private TableSorter resultTable_google;
    private javax.swing.JTextField searchBox;
    private javax.swing.JButton searchBt;

    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private JTextField jTextaddressDB;

    private String search;

    private TableSorter sortTable;
    private TableSorter sortTable1;
    private String dbaddress;
    private int dbport;





    private DefaultListModel listModel;
    ArrayList<GoogleSearch> resultGoogle;
    int searchGoogle;
    ArrayList<GoogleSearch> resultGoogleScholar;
    int searchGoogleScholar;
    ArrayList<GoogleSearch> resultDatabase;
    int searchDatabase;

    TDiagramMouseManager tdmm ;

    private ArrayList<Object[]> rowsGoogle;
    private ArrayList<Object[]> rowsGoogleScholar;
    private ArrayList<Object[]> rowsDB;
    /** Creates new form  */
    public JDialogSearchBox(Frame _frame, String _title, ArrayList<String> l, TDiagramMouseManager tdmm) {
    	 //super(_frame, _title, true);
         initComponents();
        this.tdmm = tdmm;
        this.setTitle("External Search");
         GraphicLib.centerOnParent(this);
         String s="";
         for (int i =0; i< l.size(); i++){
        	 addValueListKeyword(l.get(i));
         }

        // this.setLocationRelativeTo(_frame);
         pack();

         this.setVisible(true);

    }

    private void initComponents(){
    	jScrollPane1 = new javax.swing.JScrollPane();
        ListKeywords = new javax.swing.JList();
        removeBt = new javax.swing.JButton();
        searchBt = new javax.swing.JButton();
        searchBox = new javax.swing.JTextField();
        googleCb = new javax.swing.JCheckBox();
        googleScholarCb = new javax.swing.JCheckBox();
        databaseCb = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        StyledDocument doc =  new DefaultStyledDocument();
        detailText_google = new javax.swing.JTextPane(doc);
        detailText_googleScholar = new javax.swing.JTextPane(doc);
        jScrollPane4 = new javax.swing.JScrollPane();
        resultTable_google = new javax.swing.JTable();
        resultTable_googleScholar = new javax.swing.JTable();
        listModel = new DefaultListModel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        sortTable = new TableSorter();
        sortTable1 = new TableSorter();

        jPanel_GoogleScholarTab = new javax.swing.JPanel();
        jPanel_GoogleTab = new javax.swing.JPanel();
        jPanel_DBTab = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        combobox_Score = new javax.swing.JComboBox();
        combobox_System = new javax.swing.JComboBox();;
        combobox_Year = new javax.swing.JComboBox();
        jLabel_System = new javax.swing.JLabel();
        jLabel_Score = new javax.swing.JLabel();
        jLabel_Year = new javax.swing.JLabel();
        jButton_Setting = new javax.swing.JButton();
        jButton_Statistic = new javax.swing.JButton();
        detailText_db = new JTextPane(doc);
        resultTable_db = new JTable();
        jScrollPane5= new JScrollPane();
        jScrollPane7= new JScrollPane();
        jScrollPane8= new JScrollPane();
        jScrollPane9= new JScrollPane();
        jTextaddressDB = new JTextField();

        dbaddress = default_address;
        dbport = default_port;


        jTextaddressDB.setText(dbaddress + ":" + dbport);


        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        searchGoogle=0;
        searchGoogle = 0;
        searchDatabase =0;
        rowsGoogle=new ArrayList<Object[]>();
        rowsGoogleScholar=new ArrayList<Object[]>();

        jLabel_System.setText("System");
        jLabel_Score.setText("Score");
        jLabel_Year.setText("Year");
        jButton_Setting.setText("Setting");
        jButton_Statistic.setText("Statistic");

        ListKeywords.setModel(listModel);
        jScrollPane1.setViewportView(ListKeywords);

        removeBt.setText("Remove");
        removeBt.setActionCommand("Remove");
        removeBt.setEnabled(false);

        searchBt.setText("Search");

        searchBox.setText("Key words");

        googleCb.setText("Google");

        googleScholarCb.setText("Google Scholar");

        databaseCb.setText("Database");

        jLabel1.setText("Keywords");
        jLabel3.setText("Search Box");
        jLabel4.setText("Results");
        jLabel5.setText("Status");
        jLabel2.setText("Year");
        jLabel3.setText("Score");

        jButton_Setting.setText("Setting");

        jButton_Statistic.setText("Statistic");

        jLabel_System.setText("System");

        this.jButton_Setting.setEnabled(false);
        this.jButton_Statistic.setEnabled(false);
        this.combobox_System.setEnabled(false);
        this.combobox_Score.setEnabled(false);
        this.combobox_Year.setEnabled(false);




        //--------------------------------------------------------

        detailText_google.setBounds(0, 0, 20, 5);
        detailText_google.setEditable(false);
        jScrollPane3.setViewportView(detailText_google);
        resultTable_google = new JTable(sortTable);


        resultTable_google.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{}, new String[]{
                "No", "Title", "Link"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        resultTable_google.setSelectionMode(
                ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jScrollPane4.setViewportView(resultTable_google);

        resultTable_google.getColumnModel().getColumn(0).setMaxWidth(40);
       //resultTable_google.getColumnModel().getColumn(2).setMaxWidth(400);
        resultTable_google.getColumnModel().getColumn(2).setMinWidth(400);
        //resultTable_google.getColumnModel().getColumn(2).setMaxWidth(120);

        sortTable.setTableHeader(resultTable_google.getTableHeader());

        javax.swing.GroupLayout jPanel_GoogleTabLayout = new javax.swing.GroupLayout(jPanel_GoogleTab);
        jPanel_GoogleTab.setLayout(jPanel_GoogleTabLayout);

        GroupLayout.ParallelGroup group = jPanel_GoogleTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        jPanel_GoogleTabLayout.setHorizontalGroup(
                jPanel_GoogleTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_GoogleTabLayout.setVerticalGroup(
                jPanel_GoogleTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_GoogleTabLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Google", jPanel_GoogleTab);


    ///
        detailText_googleScholar.setBounds(0, 0, 20, 5);
        detailText_googleScholar.setEditable(false);
        jScrollPane8.setViewportView(detailText_googleScholar);
        resultTable_googleScholar = new JTable(sortTable1);


        resultTable_googleScholar.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{}, new String[]{
                "No", "Title", "Author", "Link"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        resultTable_googleScholar.setSelectionMode(
                ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jScrollPane9.setViewportView(resultTable_googleScholar);

        resultTable_googleScholar.getColumnModel().getColumn(0).setMaxWidth(40);
        //resultTable_googleScholar.getColumnModel().getColumn(4).setMaxWidth(120);

        sortTable1.setTableHeader(resultTable_googleScholar.getTableHeader());

        javax.swing.GroupLayout jPanel_GoogleScholarTabLayout = new javax.swing.GroupLayout(jPanel_GoogleScholarTab);
        jPanel_GoogleScholarTab.setLayout(jPanel_GoogleScholarTabLayout);

        GroupLayout.ParallelGroup groupScholar = jPanel_GoogleScholarTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        jPanel_GoogleScholarTabLayout.setHorizontalGroup(
                jPanel_GoogleScholarTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_GoogleScholarTabLayout.setVerticalGroup(
                jPanel_GoogleScholarTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_GoogleScholarTabLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Google Scholar", jPanel_GoogleScholarTab);

    /////

        resultTable_db.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{}, new String[]{
                "No", "ID CVE", "Title"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        resultTable_db.setSelectionMode(
                ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jScrollPane5.setViewportView(resultTable_db);
        resultTable_db.getColumnModel().getColumn(0).setMaxWidth(40);
        resultTable_db.getColumnModel().getColumn(2).setMinWidth(120);
        jScrollPane7.setViewportView(detailText_db);


        javax.swing.GroupLayout jPanel_DBTabLayout = new javax.swing.GroupLayout(jPanel_DBTab);
        jPanel_DBTab.setLayout(jPanel_DBTabLayout);
        jPanel_DBTabLayout.setHorizontalGroup(
                jPanel_DBTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                        .addComponent(jScrollPane7)
        );
        jPanel_DBTabLayout.setVerticalGroup(
                jPanel_DBTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_DBTabLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Database CVE", jPanel_DBTab);

        combobox_System.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"All", "Linux/Unix", "Windows", "Others"}));

        combobox_Year.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"All", "5-7", "7-8", "8-9"}));

        combobox_Score.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"All", "Last year", "Last 5 years", "Last 10 years"}));


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTabbedPane2)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(googleCb)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(databaseCb)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(googleScholarCb)
                                                                        .addComponent(removeBt))
                                                                .addGap(100, 100, 100)))
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jButton_Setting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jButton_Statistic, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel_System))
                                                .addGap(37, 37, 37)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(combobox_Score, 0, 142, Short.MAX_VALUE)
                                                        .addComponent(combobox_Year, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(combobox_System, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(searchBox)
                                                .addGap(18, 18, 18)
                                                .addComponent(searchBt, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel_System, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(googleCb)
                                                                .addComponent(databaseCb)
                                                                .addComponent(jButton_Setting)
                                                                .addComponent(combobox_System, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(googleScholarCb)
                                                        .addComponent(jButton_Statistic))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(removeBt))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(35, 35, 35)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(combobox_Score, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel2))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(combobox_Year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel3))))
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(searchBox, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(searchBt))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTabbedPane2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addGap(6, 6, 6))
        );

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
                if (searchBox.getText().length() <= 0) {
                    searchBt.setEnabled(false);
                } else
                    searchBt.setEnabled(true);
            }
        });

        searchBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtActionPerformed(evt);

            }
        });

        googleCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (googleCb.isSelected() && ListKeywords.getModel().getSize()>0)
                    searchBt.setEnabled(true);
                googleCbActionPerformed(evt);
            }
        });

        databaseCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (databaseCb.isSelected() && ListKeywords.getModel().getSize()>0)
                    searchBt.setEnabled(true);
                databaseCbActionPerformed(evt);
            }
        });

        googleScholarCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (googleScholarCb.isSelected() && ListKeywords.getModel().getSize()>0)
                    searchBt.setEnabled(true);
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
                int col = resultTable_google.getSelectedColumn();
                if (col == 2) {
                    String st = (String) resultTable_google.getValueAt(row, col);
                    URI uri = URI.create(st);
                    Desktop d = Desktop.getDesktop();
                    try {
                        d.browse(uri);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "URL is invalid", "Warning",
                                JOptionPane.WARNING_MESSAGE);
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
                int col = resultTable_googleScholar.getSelectedColumn();
                if (col == 3) {
                    String st = (String) resultTable_googleScholar.getValueAt(row, col);
                    URI uri = URI.create(st);
                    Desktop d = Desktop.getDesktop();
                    try {
                        d.browse(uri);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "URL is invalid", "Warning",
                                JOptionPane.WARNING_MESSAGE);
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
                int row = resultTable_db.getSelectedRow();
                int col = resultTable_db.getSelectedColumn();
                if (col == 2) {
                    String st = (String) resultTable_db.getValueAt(row, col);

                    Message msg = new Message(Message.CMD_DETAIL)   ;
                    msg.addKeywordMessage(st);

                    Client cl = new Client();
                    //Message returnMsg = cl.send(msg);
                    Message returnMsg  = new Message(Message.RESULT_DETAIL);
                    Record r = (Record)cl.parserAnswerMessage(returnMsg);
                    printDetailRecord(r);
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
              final JComponent[] inputs = new JComponent[]{
                      new JLabel("Address"),
                      jTextaddressDB
              };

              JOptionPane joptionpane = new JOptionPane();
              int i = joptionpane.showOptionDialog(null, inputs, "Setup the address of database",
                      joptionpane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                      null, new Object[]{"OK", "Cancel"}, "OK");
              if (i == joptionpane.OK_OPTION) {
                  while (! (jTextaddressDB.getText().contains(":") && jTextaddressDB.getText().split(":").length == 2)) {
                      JOptionPane.showMessageDialog(null, "Address:Port", "Wrong format",
                              JOptionPane.WARNING_MESSAGE);
                      i = joptionpane.showOptionDialog(null, inputs, "Setup the address of database",
                              joptionpane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                              null, new Object[]{"OK", "Cancel"}, "OK");
                  }
              } else if (i == joptionpane.CLOSED_OPTION) {
              }
          }
      }

        );

            jButton_Statistic.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    doStatistic();
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

            public boolean isAddressDBFormatted() {
                if (jTextaddressDB.getText().contains(":") && jTextaddressDB.getText().split(":").length == 2)
                    return true;
                return false;
            }
        });

        jTabbedPane2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                detailText_db.setDocument(new DefaultStyledDocument());
                detailText_google.setDocument(new DefaultStyledDocument());
            }
        });

        pack();
    }//

    private void WindowClosing(WindowEvent evt) {
        tdmm.clearSelectComponents();
        this.dispose();
    }




    private void ListKeywordsComponentAdded(java.awt.event.ContainerEvent evt) {
        this.removeBt.setEnabled(true);
    }

    private void removeBtActionPerformed(java.awt.event.ActionEvent evt) {
        this.jLabel5.setText("Ready");
        if (listModel.getSize()!=0){
            int index = this.ListKeywords.getSelectedIndex();
            listModel.remove(index);

            String query = "";
            //first value
            if (this.listModel.getSize() > 0){
                String element = (String) this.listModel.elementAt(0);
                String value = splitAndConcat(element);
                query = value;
            }

            for (int i = 1; i < this.listModel.getSize(); i++) {

                String element = (String) this.listModel.elementAt(i);
                String value = splitAndConcat(element);

                query = query + " + " + value; //(String) this.listModel.elementAt(i);
            }

            this.searchBox.setText(query);

            int size = listModel.getSize();

            if (size == 0) {
                this.removeBt.setEnabled(false);

            } else { //Select an index.
                if (index == listModel.getSize()) {
                    //removed item in last position
                    index--;
                }

                this.ListKeywords.setSelectedIndex(index);
                this.ListKeywords.ensureIndexIsVisible(index);
            }

        }

    }

    private void searchBtActionPerformed(java.awt.event.ActionEvent evt)  {

        //reset Tab title
        jTabbedPane2.setTitleAt(0, "Google");
        jTabbedPane2.setTitleAt(1, "Google Scholar");
        jTabbedPane2.setTitleAt(2, "Database");



        if (searchGoogle == 0 && searchGoogleScholar == 0 && searchDatabase == 0) {
            JOptionPane.showMessageDialog(null, "Please select the resource to search","Warning",
                    JOptionPane.WARNING_MESSAGE);
        }


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                jLabel5.setText("The crawler is running ....");
                searchBt.setEnabled(false);

                //reset content of table
                DefaultTableModel modelGoogle = (DefaultTableModel) resultTable_google.getModel();
                DefaultTableModel modelGoogleScholar = (DefaultTableModel) resultTable_googleScholar.getModel();
                DefaultTableModel modelDB = (DefaultTableModel) resultTable_db.getModel();

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
                rowsDB =new ArrayList<Object[]>();
                // check internet connection before crawling from google or google scholar
                Boolean internetConnectionAvailable = null;
                if (searchGoogle == 1 || searchGoogleScholar == 1) {
                    internetConnectionAvailable = CheckConnection.checkInternetConnection();
                    if (internetConnectionAvailable) {

                        ArrayList<GoogleSearch> resultGoogle = null;
                        ArrayList<GoogleSearch> resultGoogleScholar = null;
                        //get the content of searhBox
                        String query = searchBox.getText();
                        if (query != "") {
                            if (searchGoogle == 1) {
                                jLabel5.setText("Retrieving data from Google");

                                resultGoogle = GoogleSearch.getGoogleResult(searchBox.getText());
                                jLabel5.setText("Done");

                                if (resultGoogle == null) {
                                    JOptionPane.showMessageDialog(null, "Can't get the result from Google\n"
                                            , "Retrieving data is failed",
                                            JOptionPane.ERROR_MESSAGE);
                                    jLabel5.setText("Failed to retrieving data from Google");
                                }else if (resultGoogle != null) {

                                    if(resultGoogle.get(0).getTitle() == GoogleSearch.IOEx) {
                                        JOptionPane.showMessageDialog(null, "Can connect to Google\n " +
                                                        "Please check the internet connection","Connection Error",
                                                JOptionPane.ERROR_MESSAGE);
                                        jLabel5.setText("Failed to retrieving data from Google");
                                    } else {
                                        putGoogleToTable(resultGoogle);
                                        showtable(rowsGoogle,modelGoogle,0);
                                    }

                                }
                            }

                            if (searchGoogleScholar == 1) {
                                jLabel5.setText("Retrieving data from Google Scholar");
                                //jLabel5.updateUI();
                                resultGoogleScholar = GoogleSearch.getGoogleScholarResult(searchBox.getText());
                                jLabel5.setText("Done");
                                //jLabel5.updateUI();
                                if (resultGoogleScholar == null) {
                                    JOptionPane.showMessageDialog(null, "Cannot get the result from Google Scholar \n"
                                            , "Retrieving data is failed",
                                            JOptionPane.ERROR_MESSAGE);
                                    jLabel5.setText("Failed to retrieving data from Google Scholar");
                                }else if (resultGoogleScholar != null) {
                                    if (resultGoogleScholar.get(0).getTitle() == GoogleSearch.IOEx) {
                                        JOptionPane.showMessageDialog(null, "Can't connect to Google Scholar\n " +
                                                        "Please check the internet connection","Connection Error",
                                                JOptionPane.ERROR_MESSAGE);
                                        jLabel5.setText("Failed to retrieving data from Google Scholar");
                                    } else {
                                        putGoogleScholarToTable(resultGoogleScholar);
                                        showtable(rowsGoogleScholar, modelGoogleScholar,1);
                                    }
                                }
                            }
                        }


                    }else{
                        jLabel5.setText("Failed to connect to resource ");
                        JOptionPane.showMessageDialog(null, "Cannot connect to Google",
                                "Connection Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }


                //TODO : cralwer data from DB, must check the connection.
                if (searchDatabase ==1) {
                    jLabel5.setText("Retrieving data from DB");
                    Message msg = new Message(Message.CMD_SEARCH);
                    msg.addOptionValueMessage(Message.OPTION_YEAR,(String)combobox_Year.getSelectedItem());
                    //msg.addOptionValueMessage(Message.OPTION_SCORE,(String)combobox_Score.getSelectedItem());
                    //msg.addOptionValueMessage(Message.OPTION_SYSTEM,(String)combobox_System.getSelectedItem());
                    //msg.addOptionValueMessage(Message.OPTION_NUMBER, Integer.toString(10));
                    //msg.addKeywordMessage(searchBox.getText());
                    //create client
                    Client client = new Client();
                    Message msg1 = new Message(Message.RESULT_SEARCH);
                    //Message returnMsg = client.send(msg1);

                    //Message msg1 = new Message(Message.RESULT_SEARCH);

                    //System.out.print("truoc khi vao parser");
                    ArrayList<Record> re =  (ArrayList<Record>)client.parserAnswerMessage(msg1);
                    putDBToTable(re);
                    showtable(rowsDB, modelDB,2);

                    try {
                       // Message rtMsg = client.send(msg);
                        //ArrayList<Record> = client.parserAnswerMessage(rtMsg);
                    }catch (Exception e){
                        System.out.println("Loi");
                    }
                }

            }
        });
        t.start();
    }


    public void showtable(ArrayList<Object[]> list, DefaultTableModel model,int index){
        int id=0;
        if (index ==1 || index ==0) {
            for (Object[] o : list) {
                id = (Integer) (o[0]);
                GoogleSearch gs = (GoogleSearch) (o[1]);
                //String source = (String) (o[2]);
                //model.addRow(new Object[]{id, gs.getTitle(), gs.getAuthors(), gs.getUrl(), source});
                if (index == 0)
                    model.addRow(new Object[]{id, gs.getTitle(), gs.getUrl()});
                else if (index == 1)
                    model.addRow(new Object[]{id, gs.getTitle(), gs.getAuthors(), gs.getUrl()});

            }
        } else if (index == 2){
            for (Object[] o : list) {
                id = (Integer) (o[0]);
                Record r = (Record) (o[1]);
                model.addRow(new Object[]{id, r.getCve_id(), r.getName()});
            }
        }


        if (index ==0 ){
            jTabbedPane2.setTitleAt(index, "Google" + " [" + model.getRowCount() + "]");
        }
        else if (index ==1 ){
            jTabbedPane2.setTitleAt(index, "Google Scholar" + " [" + model.getRowCount() + "]");
        }
        else if (index ==2 ){
            jTabbedPane2.setTitleAt(index, "Database" + " [" + model.getRowCount() + "]");
        }
        searchBt.setEnabled(true);
        jLabel5.setText("Finished");
        //jTabbedPane2.updateUI();
    }

    private void selectrow(ListSelectionEvent evt,ArrayList<Object[]> rows,JTable resultTable,JTextPane textpane,int typeObject) {
    	int rowindex = resultTable.getSelectedRow();
    	int id =0;

    	if(rowindex >=0)
    		id= (Integer) resultTable.getValueAt(rowindex, 0);
            if (typeObject==1 || typeObject==0){
                GoogleSearch selected=null;
                for (Object[] o : rows){
                    if (o[0].equals(id)){
                        selected=(GoogleSearch)o[1];
                        break;
                    }
                }
                presentDataInDetail(selected, 0,textpane);
            }else{
                    Record selected=null;
                    for (Object[] o : rows){
                        if (o[0].equals(id)){
                            selected=(Record)o[1];
                            break;
                        }
                    }
                    presentDataInDetail(selected,2,  textpane);
            }



    }    
    
    private void googleCbActionPerformed(java.awt.event.ActionEvent evt) {
        this.jLabel5.setText("Ready");
        this.jLabel5.updateUI();
    	if (this.googleCb.isSelected())
    		this.searchGoogle=1;
    	else this.searchGoogle=0;
    }    
    
    private void googleScholarCbActionPerformed(java.awt.event.ActionEvent evt) {
        this.jLabel5.setText("Ready");
        this.jLabel5.updateUI();
    	if (this.googleScholarCb.isSelected())
    		this.searchGoogleScholar=1;
    	else this.searchGoogleScholar=0;
    }                                               

    private void databaseCbActionPerformed(java.awt.event.ActionEvent evt) {
        this.jLabel5.setText("Ready");
        this.jLabel5.updateUI();
    	if (this.databaseCb.isSelected()) {
            this.searchDatabase = 1;
            this.jButton_Setting.setEnabled(true);
            this.jButton_Statistic.setEnabled(true);
            this.combobox_System.setEnabled(true);
            this.combobox_Score.setEnabled(true);
            this.combobox_Year.setEnabled(true);

        } else {
            this.searchDatabase=0;
            this.jButton_Setting.setEnabled(false);
            this.jButton_Statistic.setEnabled(false);
            this.combobox_System.setEnabled(false);
            this.combobox_Score.setEnabled(false);
            this.combobox_Year.setEnabled(false);
        }
    }    
    
    public void addValueListKeyword(String st){
        if  (! this.listModel.contains(st)){
        	this.listModel.addElement(st);
        	
        	String query = "";
        	
        	if (this.listModel.getSize()>0)
                query = splitAndConcat((String) this.listModel.elementAt(0));
                System.out.println(query);
    	    	for (int i=1; i< this.listModel.getSize(); i++ ){
                    if (query != "")
    	    		    query= query + " + " + splitAndConcat((String) this.listModel.elementAt(i));
                    else
                        query = splitAndConcat((String) this.listModel.elementAt(i));
    	    	}

            System.out.println(query);
    	    	
        	this.searchBox.setText(query);
        }
    	
    }


    public void removeValueListKeyword(){
        this.listModel.clear();
    }
    
    public void putGoogleToTable(ArrayList<GoogleSearch> a)
    {
    	int i = this.rowsGoogle.size()+1;
    	for (GoogleSearch gs : a){
    		this.rowsGoogle.add(new Object[]{i, gs});
    		i=i+1;
    	}
    }

    public void putDBToTable(ArrayList<Record> a)
    {
        int i = this.rowsGoogle.size()+1;
        for (Record record : a){
            this.rowsDB.add(new Object[]{i, record});
            i=i+1;
        }
    }
    
    public void putGoogleScholarToTable(ArrayList<GoogleSearch> a)
    {
    	int i = this.rowsGoogleScholar.size()+1;
    	for (GoogleSearch gs : a){
            rowsGoogleScholar.add(new Object[]{i, gs});
    		i=i+1;
    	}
    }

    private void printDetailRecord(Record r){

        //String detail = r.getSummary();
        this.detailText_db.setText(r.toString());
    }




    public void presentDataInDetail(Object obj, int typeObject, JTextPane textPane){
        StyledDocument  doc = textPane.getStyledDocument();
        textPane.setText("");



        SimpleAttributeSet bold = new SimpleAttributeSet();
        StyleConstants.setBold(bold, true);
        StyleConstants.setFontSize(bold, 15);
        SimpleAttributeSet underline = new SimpleAttributeSet();
        StyleConstants.setUnderline(underline, true);
        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setBold(normal, false);
        StyleConstants.setFontSize(normal, 13);

        if (typeObject ==1 || typeObject ==0 ){
            int offset = 0;
            GoogleSearch gs =  (GoogleSearch)obj;
            if (gs != null)
            {
                String detail = "";
                if (gs.getTitle()!=null) {
                    //detail = detail + gs.getTitle() + "\n";
                    try {
                        doc.insertString(offset, gs.getTitle() + "\n", bold);
                        offset = offset+ gs.getTitle().length()+1;
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }



                if (gs.getAuthors()!=null) {
                    //detail = detail + gs.getAuthors() + "\n";
                    try {
                        doc.insertString(offset, gs.getAuthors() + "\n", normal);
                        offset =  offset+gs.getAuthors().length()+1;
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
                if (gs.getUrl()!=null){
                    try {
                        detail = detail + gs.getUrl()+"\n";
                        doc.insertString(offset, gs.getUrl() + "\n", underline);
                        offset =  offset+gs.getUrl().length()+1;
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }

                if (gs.getCitedNumber()!=null){
                    try {
                        //detail = detail + gs.getCitedNumber()+"\n";
                        doc.insertString(offset, gs.getCitedNumber() + "\n", normal);
                        offset =  offset+gs.getCitedNumber().length()+1;
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }

                if (gs.getDesc()!=null){
                    try {
                        //detail = detail + gs.getCitedNumber()+"\n";
                        //detail = detail + gs.getDesc()+"\n";
                        doc.insertString(offset, gs.getDesc() + "\n", normal);
                        offset =  offset+gs.getDesc().length()+1;
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }


                //textPane.setText(detail);
            }
        }else{
            Record r = (Record)obj;
            String detail = r.getSummary();
            textPane.setText(detail);
        }

    }

    //==========================================
    //database functions

    public void doStatistic() {
        BufferedImage img= null;
        try {
            img = ImageIO.read(new File("test.jpg"));
            ImageIcon icon=new ImageIcon(img);
            JFrame frame=new JFrame();
            frame.setLayout(new FlowLayout());
            frame.setSize(200,300);
            JLabel lbl=new JLabel();
            lbl.setIcon(icon);
            frame.add(lbl);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void doQueryDB(){
       /* Message msg = new Message();
        msg.setCmd(Message.CMD_SEARCH);
        ArrayList<String> option = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        option.add("System");
        values.add(this.combobox_System.getSelectedItem().toString());
        option.add(this.combobox_Year.getSelectedItem().toString());
        option.add(this.combobox_Score.getSelectedItem().toString());*/
    }

    //TODO: implement function to get database.
    public void getResultFromDatabase(){

    }

    /**
     *
     * @param input: a string without space and words are seperated by uper character.
     * @return a splited, then concaternated with space.
     */
    public String splitAndConcat(String input){
        String[] splitValue = input.split("(?=\\p{Lu})");
        String value = "";
        if (splitValue.length>0){
            value = splitValue[0];
            for (int i =1 ; i < splitValue.length; i ++ ){
                value = value + " " + splitValue[i];
            }
        }
        return value;
    }
    
    
}
