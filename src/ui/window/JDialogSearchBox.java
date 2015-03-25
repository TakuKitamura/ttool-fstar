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
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;

import myutil.GoogleSearch;
import myutil.GraphicLib;

import java.lang.Object;
import java.awt.Desktop;
import java.net.URI;



//import javax.swing.event.*;
import java.util.*;

import javax.swing.table.DefaultTableModel;

import myutil.CheckConnection;

//TODO : change display to tab.
//TODO: decorate the text box
//TODO : click on Search does not change status immediately
public class JDialogSearchBox extends javax.swing.JFrame  {
    public static final String bold= "bold";
    public static final String normal = "normal";

	private javax.swing.JList ListKeywords;
    private javax.swing.JComboBox combobox_Score;
    private javax.swing.JComboBox combobox_System;
    private javax.swing.JComboBox combobox_Year;
    private javax.swing.JCheckBox databaseCb;
    private javax.swing.JTextPane detailText_db;
    private javax.swing.JTextPane detailText_google;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JButton removeBt;
    private javax.swing.JTable resultTable_db;
    private javax.swing.JTable resultTable_google;
    //private TableSorter resultTable_google;
    private javax.swing.JTextField searchBox;
    private javax.swing.JButton searchBt;

    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;

    private String search;



    private DefaultListModel listModel;
    ArrayList<GoogleSearch> resultGoogle;
    int searchGoogle;
    ArrayList<GoogleSearch> resultGoogleScholar;
    int searchGoogleScholar;
    ArrayList<GoogleSearch> resultDatabase;
    int searchDatabase;

    
    private ArrayList<Object[]> rows;
    /** Creates new form  */
    public JDialogSearchBox(Frame _frame, String _title, ArrayList<String> l) {
    	 //super(_frame, _title, true);
         initComponents();
         GraphicLib.centerOnParent(this);
         String s="";
         for (int i =0; i< l.size(); i++){
        	 addValueListKeyword(l.get(i));
         }
         
        // this.setLocationRelativeTo(_frame);
         pack();
      
         this.setVisible(true);

    }
    
   //DAN
    
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
        jScrollPane4 = new javax.swing.JScrollPane();
        resultTable_google = new javax.swing.JTable();
        listModel = new DefaultListModel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();


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
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
        setResizable(false);
        searchGoogle=0;
        searchGoogle = 0;
        searchDatabase =0;
        rows=new ArrayList<Object[]>();


        jLabel_System.setText("System");

        jLabel_Score.setText("Score");

        jLabel_Year.setText("Year");

        jButton_Setting.setText("Setting");

        jButton_Statistic.setText("Statistic");

        
        ListKeywords.setModel(listModel);
        jScrollPane1.setViewportView(ListKeywords);

        removeBt.setText("Remove");
        removeBt.setActionCommand("Remove");
        

        searchBt.setText("Search");

        searchBox.setText("SearchBox");

        googleCb.setText("Google");
      
        googleScholarCb.setText("Google Scholar");

        databaseCb.setText("Database");

        detailText_google.setBounds(0, 0, 20, 5);
        detailText_google.setEditable(false);
        jScrollPane3.setViewportView(detailText_google);

        resultTable_google.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{}, new String[]{
                "No", "Title", "Author", "Link", "Source"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        resultTable_google.setSelectionMode(
                ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        jScrollPane4.setViewportView(resultTable_google);
        
        resultTable_google.getColumnModel().getColumn(0).setMaxWidth(40);
        resultTable_google.getColumnModel().getColumn(4).setMaxWidth(120);
        jLabel1.setText("Keywords");

        jLabel2.setText("Sources");

        jLabel3.setText("Search Box");

        jLabel4.setText("Results");

        jLabel5.setText("Status");

        jLabel2.setText("Score");

        jLabel3.setText("Year");

        jButton_Setting.setText("Setting");

        jButton_Statistic.setText("Statistic");

        jLabel_System.setText("System");

        this.jButton_Setting.setEnabled(false);
        this.jButton_Statistic.setEnabled(false);
        this.combobox_System.setEnabled(false);
        this.combobox_Score.setEnabled(false);
        this.combobox_Year.setEnabled(false);

        javax.swing.GroupLayout jPanel_GoogleTabLayout = new javax.swing.GroupLayout(jPanel_GoogleTab);
        jPanel_GoogleTab.setLayout(jPanel_GoogleTabLayout);
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

        resultTable_db.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{}, new String[]{
                "No", "ID CVE", "System", "Score", "Year"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        resultTable_db.setSelectionMode(
                ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jScrollPane5.setViewportView(resultTable_db);
        resultTable_db.getColumnModel().getColumn(0).setMaxWidth(40);
        resultTable_db.getColumnModel().getColumn(4).setMaxWidth(120);
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

        combobox_System.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Linux/Unix", "Windows", "Others" }));

        combobox_Year.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "5-7", "7-8", "8-9" }));

        combobox_Score.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Last year", "Last 5 years", "Last 10 years" }));

       /* javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(googleCb)
                            .addComponent(databaseCb)
                            .addComponent(googleScholarCb)
                            .addComponent(removeBt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchBt, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(searchBox, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(googleCb)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(googleScholarCb)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(searchBt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(databaseCb)
                                .addGap(12, 12, 12)
                                .addComponent(removeBt)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(5, 5, 5)
                .addComponent(searchBox, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(8, 8, 8)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );*/

        //jScrollPane4.setViewportView(resultTable_google);

        //detailText_google.setColumns(20);
        //detailText_google.setLineWrap(true);
        //detailText_google.setRows(5);
        //jScrollPane3.setViewportView(detailText_google);

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
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(googleCb)
                                                        .addComponent(databaseCb)
                                                        .addComponent(jLabel_System)
                                                        .addComponent(jButton_Setting)
                                                        .addComponent(combobox_System, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(googleScholarCb)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                                                                .addComponent(removeBt))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jButton_Statistic)
                                                                .addGap(0, 0, Short.MAX_VALUE))))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(35, 35, 35)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(combobox_Score, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel2))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(combobox_Year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel3))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
        
        searchBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLabel5.setText("The crawler is running ....");
				searchBtActionPerformed(evt);

            }
        });
        
        googleCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                googleCbActionPerformed(evt);
            }
        });
        
        databaseCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseCbActionPerformed(evt);
            }
        });
        
        googleScholarCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                googleScholarCbActionPerformed(evt);
            }
        });

        resultTable_google.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectrow(e);
            }
        });
        resultTable_google.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = resultTable_google.getSelectedRow();
                int col = resultTable_google.getSelectedColumn();
                if (col == 3) {
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
                //see below

            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                WindowClosing(evt);
            }
        });
        pack();
    }//

    //clear everything when closing
    //TODO: bug: clear values when closing, in order to display new value for the next open
    private void WindowClosing(WindowEvent evt) {
        //this.initComponents();
        // this.searchBox.setText("");
        //  DefaultTableModel model = (DefaultTableModel) this.resultTable_google.getModel();
        //  model.setRowCount(0);
        //   this.detailText_google.setText("");
        //    this.ListKeywords.removeAll();
        //   this.listModel.removeAllElements();
        //    this.databaseCb.setSelected(false);
        //   this.googleCb.setSelected(false);
        //   this.googleScholarCb.setSelected(false);
        //   this.dispose();
    }


    private void removeBtActionPerformed(java.awt.event.ActionEvent evt) {
        this.jLabel5.setText("Ready");
        this.jLabel5.updateUI();
        int index = this.ListKeywords.getSelectedIndex();
        listModel.remove(index);

        String query = "";

        if (this.listModel.getSize() > 0)
            query = (String) this.listModel.elementAt(0);
        for (int i = 1; i < this.listModel.getSize(); i++) {
            query = query + " + " + (String) this.listModel.elementAt(i);
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

    private void searchBtActionPerformed(java.awt.event.ActionEvent evt)  {

        //reset content of table
        DefaultTableModel model = (DefaultTableModel) this.resultTable_google.getModel();
        model.setRowCount(0);
        this.detailText_google.setText("");
        this.detailText_google.updateUI();

        // ensure there is at least the resources for crawling.
        if (this.searchGoogle == 0 && this.searchGoogleScholar == 0 && this.searchDatabase == 0) {
            JOptionPane.showMessageDialog(null, "Please select the resource to search","Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        int id;
        rows = new ArrayList<Object[]>();

        // check internet connection before crawling from google or google scholar
        Boolean internetConnectionAvailable = null;
        if (this.searchGoogle == 1 || this.searchGoogleScholar == 1) {
            internetConnectionAvailable = CheckConnection.checkInternetConnection();
            if (internetConnectionAvailable) {

                ArrayList<GoogleSearch> resultGoogle = null;
                ArrayList<GoogleSearch> resultGoogleScholar = null;


                //get the content of searhBox
                String query = this.searchBox.getText();
                if (query != "") {
                    if (this.searchGoogle == 1) {
                        jLabel5.setText("Retrieving data from Google");
                        jLabel5.updateUI();
                        resultGoogle = GoogleSearch.getGoogleResult(this.searchBox.getText());
                        jLabel5.setText("Done");
                        jLabel5.updateUI();
                        if (resultGoogle == null) {
                            JOptionPane.showMessageDialog(null, "Can't get the result from Google\n"
                                    , "Retrieving data is failed",
                                    JOptionPane.ERROR_MESSAGE);
                        }else if (resultGoogle != null) {

                            if(resultGoogle.get(0).getTitle() == GoogleSearch.IOEx) {
                                JOptionPane.showMessageDialog(null, "Can't connect to Google\n " +
                                                "Please check the internet connection","Connection Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                putGoogleToTable(resultGoogle);
                            }
                        }
                    }

                    if (this.searchGoogleScholar == 1) {
                        jLabel5.setText("Retrieving data from Google Scholar");
                        jLabel5.updateUI();
                        resultGoogleScholar = GoogleSearch.getGoogleScholarResult(this.searchBox.getText());
                        jLabel5.setText("Done");
                        jLabel5.updateUI();
                        if (resultGoogleScholar == null) {
                            JOptionPane.showMessageDialog(null, "Can't get the result from Google Scholar \n"
                                    , "Retrieving data is failed",
                                    JOptionPane.ERROR_MESSAGE);
                        }else if (resultGoogleScholar != null) {
                            if (resultGoogleScholar.get(0).getTitle() == GoogleSearch.IOEx) {
                                JOptionPane.showMessageDialog(null, "Can't connect to Google Scholar\n " +
                                                "Please check the internet connection","Connection Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                putGoogleScholarToTable(resultGoogleScholar);
                            }
                        }
                    }
                }




            }else{
                JOptionPane.showMessageDialog(null, "No internet connection",
                               "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }


        //TODO : cralwer data from DB, must check the connection.
        if (this.searchDatabase ==1) {
            this.jLabel5.setText("Retrieving data from DB");
            this.repaint();
            for (long i = 1; i < 100000000; i++) ;
        }




        //Show table
        for (Object[] o : rows) {
            id = (Integer) (o[0]);
            GoogleSearch gs = (GoogleSearch) (o[1]);
            String source = (String) (o[2]);
            model.addRow(new Object[]{id, gs.getTitle(), gs.getAuthors(), gs.getUrl(), source});
        }
        this.jLabel5.setText("Finished");
        this.jLabel5.updateUI();

    }
    
    private void selectrow(ListSelectionEvent evt) {

    	
    	DefaultTableModel model = (DefaultTableModel) this.resultTable_google.getModel();
    	int rowindex = resultTable_google.getSelectedRow();
    	int id =0;
    	
    	if(rowindex >=0)
    		id= (Integer) resultTable_google.getValueAt(rowindex, 0);
	    	GoogleSearch selected=null;
	    	for (Object[] o : this.rows){
	    		if (o[0].equals(id)){
	    			selected=(GoogleSearch)o[1];
	    			break;
	    		}
	    	}
	    	presentDataInDetail(selected);
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
        		query = (String) this.listModel.elementAt(0);
    	    	for (int i=1; i< this.listModel.getSize(); i++ ){
    	    		query= query + " + " + (String) this.listModel.elementAt(i);
    	    	}
    	    	
        	this.searchBox.setText(query);
        }
    	
    }


    public void removeValueListKeyword(){
        this.listModel.clear();
    }
    
    public void putGoogleToTable(ArrayList<GoogleSearch> a)
    {
    	int i = this.rows.size()+1;
    	for (GoogleSearch gs : a){
    		this.rows.add(new Object[]{i, gs,"Google"});
    		i=i+1;
    	}
    }
    
    public void putGoogleScholarToTable(ArrayList<GoogleSearch> a)
    {
    	int i = this.rows.size()+1;
    	for (GoogleSearch gs : a){
    		rows.add(new Object[]{i, gs,"GoogleScholar"});
    		i=i+1;
    	}
    }
    
    public void presentDataInDetail(GoogleSearch gs){

        if (gs != null)
    	{

            String detail = "";
            if (gs.getTitle()!=null)
                detail = detail+ gs.getTitle()+"\n";
            if (gs.getAuthors()!=null)
	    	    detail = detail + gs.getAuthors()+"\n";
            if (gs.getUrl()!=null)
	    	    detail = detail + gs.getUrl()+"\n";
            if (gs.getCitedNumber()!=null)
	    	    detail = detail + gs.getCitedNumber()+"\n";
            if (gs.getDesc()!=null)
	    	    detail = detail + gs.getDesc()+"\n";
	    	
	    	this.detailText_google.setText(detail);
    	}


    }
    
    
    
}
