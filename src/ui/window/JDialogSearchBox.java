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
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;

import myutil.GoogleSearch;
import myutil.GraphicLib;

import java.lang.Object;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import myutil.GraphicLib;


import javax.swing.table.AbstractTableModel;
import javax.swing.*;

//import javax.swing.event.*;
import java.util.*;

import myutil.TableSorter;

import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;


public class JDialogSearchBox extends javax.swing.JFrame  {
    
	private javax.swing.JList ListKeywords;
    private javax.swing.JCheckBox databaseCb;
    private javax.swing.JTextArea detailText;
    private javax.swing.JCheckBox googleCb;
    private javax.swing.JCheckBox googleScholarCb;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton removeBt;
    private javax.swing.JTable resultTable;
    //private TableSorter resultTable;
    private javax.swing.JTextField searchBox;
    private javax.swing.JButton searchBt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
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
        detailText = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        listModel = new DefaultListModel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
        searchGoogle=0;
        searchGoogle = 0;
        searchDatabase =0;
        rows=new ArrayList<Object[]>();
        
        ListKeywords.setModel(listModel);
        jScrollPane1.setViewportView(ListKeywords);

        removeBt.setText("Remove");
        removeBt.setActionCommand("Remove");
        

        searchBt.setText("Search");

        searchBox.setText("SearchBox");

        googleCb.setText("Google");
      
        googleScholarCb.setText("Google Scholar");

        databaseCb.setText("Database");

        detailText.setColumns(20);
        detailText.setLineWrap(true);
        detailText.setRows(5);
        jScrollPane3.setViewportView(detailText);

        resultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},new String [] {
                "No", "Title", "Author", "Link","Source"}){
	        @Override
	        public boolean isCellEditable(int rowIndex, int columnIndex) {
	            return false;
	        }
        });
        resultTable.setSelectionMode(
                ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        jScrollPane4.setViewportView(resultTable);
        
        resultTable.getColumnModel().getColumn(0).setMaxWidth(40);
        resultTable.getColumnModel().getColumn(4).setMaxWidth(120);
        jLabel1.setText("Keywords");

        jLabel2.setText("Sources");

        jLabel3.setText("Search Box");

        jLabel4.setText("Results");
        jLabel5.setText("Status");
        

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
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
        );
        
        removeBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBtActionPerformed(evt);
            }
        });
        
        searchBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
					searchBtActionPerformed(evt);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
        
        resultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub	
				selectrow(e);
			}
		});
        resultTable.addMouseListener(new MouseAdapter() {
        	  public void mouseClicked(MouseEvent e) {
        		  int row = resultTable.getSelectedRow();
        	      int col = resultTable.getSelectedColumn();
        	      if (col == 3)
        	      {
        	    	  String st=(String) resultTable.getValueAt(row, col);
        	    	  URI uri = URI.create(st);
        	    	  Desktop d=Desktop.getDesktop();
        	    	  try {
						d.browse(uri);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
        	    	  
        	      }
        	        //see below
        	       
        	  }
        });
        pack();
    }//
    
    protected void open(URI uri) {
		// TODO Auto-generated method stub
		
	}

	private void removeBtActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
    	int index = this.ListKeywords.getSelectedIndex();
        listModel.remove(index);
        
        String query = "";
    	
    	if (this.listModel.getSize()>0)
    		query = (String) this.listModel.elementAt(0);
	    	for (int i=1; i< this.listModel.getSize(); i++ ){
	    		query= query + " + " +(String) this.listModel.elementAt(i);
	    	}
	    	
    	this.searchBox.setText(query);
        
        int size = listModel.getSize();

        if (size == 0) { //Nobody's left, disable firing.
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
    
    private void searchBtActionPerformed(java.awt.event.ActionEvent evt) throws UnsupportedEncodingException, IOException {                                         
        // TODO add your handling code here:
    	this.jLabel5.setText("The crawler is running ....");
    	ArrayList<GoogleSearch> resultGoogle=null;
    	ArrayList<GoogleSearch> resultGoogleScholar=null;
    	int id;
    	 rows=new ArrayList<Object[]>();
    	String query = this.searchBox.getText();
    	if (query != ""){
    		if (this.searchGoogle==1)
    			resultGoogle = GoogleSearch.getGoogleResult(this.searchBox.getText());
    		if (this.searchGoogleScholar==1)
    			resultGoogleScholar= GoogleSearch.getGoogleScholarResult(this.searchBox.getText());
    	}
    	DefaultTableModel model = (DefaultTableModel) this.resultTable.getModel();
    	model.setRowCount(0);
    	this.detailText.setText("");
    	if (resultGoogleScholar!=null)
    		putGoogleScholarToTable(resultGoogleScholar);
    	if (resultGoogle!=null)
    		putGoogleToTable(resultGoogle);
    	for (Object[] o :rows)
    	{
			id = (int) (o[0]);
    		GoogleSearch gs = (GoogleSearch)(o[1]);
    		String source = (String)(o[2]);
    		model.addRow(new Object[]{id,gs.getTitle(),gs.getAuthors(),gs.getUrl(),source});
    	}
    	this.jLabel5.setText("Finished");
    }    
    
    private void selectrow(ListSelectionEvent evt) {                                         
        // TODO add your handling code here:
    	
    	DefaultTableModel model = (DefaultTableModel) this.resultTable.getModel();
    	int rowindex = resultTable.getSelectedRow();
    	int id =0;
    	
    	if(rowindex >=0)
    		id= (int) resultTable.getValueAt(rowindex, 0);
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
        // TODO add your handling code here:
    	if (this.googleCb.isSelected())
    		this.searchGoogle=1;
    	else this.searchGoogle=0;
    }    
    
    private void googleScholarCbActionPerformed(java.awt.event.ActionEvent evt) {                                                
        // TODO add your handling code here:
    	if (this.googleScholarCb.isSelected())
    		this.searchGoogleScholar=1;
    	else this.searchGoogleScholar=0;
    }                                               

    private void databaseCbActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
    	if (this.databaseCb.isSelected())
    		this.searchDatabase=1;
    	else this.searchDatabase=0;
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
	    	String detail = gs.getTitle()+"\n\n";
	    	detail = detail + gs.getAuthors()+"\n";
	    	detail = detail + gs.getUrl()+"\n";
	    	detail = detail + gs.getCitedNumber()+"\n\n\n";
	    	
	    	detail = detail + gs.getDesc()+"\n";
	    	
	    	this.detailText.setText(detail);
    	}
    }
    
    
    
}
