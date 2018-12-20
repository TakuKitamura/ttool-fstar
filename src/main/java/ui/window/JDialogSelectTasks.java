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


import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.Collections;


/**
 * Class JDialogSelectStrings
 * Dialog for managing tasks in sim trace panel
 * Creation: 28/03/2008
 *
 * @author Ludovic APVRILLE
 * @version 1.0 28/03/2008
 */
public class JDialogSelectTasks extends JDialogBase implements ActionListener, ListSelectionListener {
    public static Vector<String> validated, ignored;


    private Vector<String> val;
    private Vector<String> ign, back;

    //subpanels
    private JPanel panel1, panel2, panel3, panel4, panel5, panel6;
    private JList<String> listIgnored;
    private JList<String> listValidated;
    private JButton allValidated;
    private JButton addOneValidated;
    private JButton addOneIgnored;
    private JButton allIgnored;
    
    private JButton shiftUp;
    private JButton shiftDown;

    /*
     * Creates new form
     */
    public JDialogSelectTasks(Frame f, Vector<String> _back, List<String> componentList, String title) {
        super(f, title, true);

        back = _back;



        if ((validated == null) || (ignored == null)) {
            val = makeNewVal(componentList);
            ign = new Vector<String>();
        } else {
            val = validated;
            ign = ignored;
            checkTask(val, componentList);
            checkTask(ign, componentList);
            addNewTask(val, componentList, ign);
        }

        initComponents();
        myInitComponents();
        pack();
    }



    private Vector<String> makeNewVal(List<String> list) {
        Vector<String> v = new Vector<String>();
        String tgc;

        for (int i = 0; i < list.size(); i++) {
            tgc = list.get(i);
            //
            
            v.addElement(tgc);
            
        }
        return v;
    }

    private void checkTask(Vector<? extends String> tobeChecked, List<String> source) {
        String t;

        for (int i = 0; i < tobeChecked.size(); i++) {
            t = tobeChecked.elementAt(i);

            if (!source.contains(t)) {
                tobeChecked.removeElementAt(i);
                i--;
            }
        }
    }

    public void addNewTask(Vector<String> added, List<String> source, Vector<String> notSource) {
        String tgc;

        for (int i = 0; i < source.size(); i++) {
            tgc = source.get(i);

            if ((!added.contains(tgc)) && (!notSource.contains(tgc))) {
                added.addElement(tgc);
                //
            }
        }
    }

    private void myInitComponents() {
        setButtons();
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
        setFont(new Font("Helvetica", Font.PLAIN, 14));

        c.setLayout(gridbag2);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;

        // ignored list
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.setBorder(new javax.swing.border.TitledBorder("Ignored Tasks"));
        listIgnored = new JList<String>(ign);
        //listIgnored.setPreferredSize(new Dimension(200, 250));
        listIgnored.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listIgnored.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listIgnored);
        panel1.add(scrollPane1, BorderLayout.CENTER);
        panel1.setPreferredSize(new Dimension(200, 250));
        c.add(panel1, c2);


        // central buttons
        panel3 = new JPanel();
        panel3.setLayout(gridbag1);

        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridheight = 1;

        allValidated = new JButton(IconManager.imgic50);
        allValidated.setPreferredSize(new Dimension(50, 25));
        allValidated.addActionListener(this);
        allValidated.setActionCommand("allValidated");
        panel3.add(allValidated, c1);

        addOneValidated = new JButton(IconManager.imgic48);
        addOneValidated.setPreferredSize(new Dimension(50, 25));
        addOneValidated.addActionListener(this);
        addOneValidated.setActionCommand("addOneValidated");
        panel3.add(addOneValidated, c1);

        panel3.add(new JLabel(" "), c1);

        addOneIgnored = new JButton(IconManager.imgic46);
        addOneIgnored.addActionListener(this);
        addOneIgnored.setPreferredSize(new Dimension(50, 25));
        addOneIgnored.setActionCommand("addOneIgnored");
        panel3.add(addOneIgnored, c1);

        allIgnored = new JButton(IconManager.imgic44);
        allIgnored.addActionListener(this);
        allIgnored.setPreferredSize(new Dimension(50, 25));
        allIgnored.setActionCommand("allIgnored");
        panel3.add(allIgnored, c1);

        c.add(panel3, c2);


        shiftUp = new JButton(IconManager.imgic78);
        shiftUp.addActionListener(this);
        shiftUp.setPreferredSize(new Dimension(50, 25));
        shiftUp.setActionCommand("shiftUp");
        panel3.add(shiftUp, c1);

        c.add(panel3, c2);


        shiftDown = new JButton(IconManager.imgic79);
        shiftDown.addActionListener(this);
        shiftDown.setPreferredSize(new Dimension(50, 25));
        shiftDown.setActionCommand("shiftDown");
        panel3.add(shiftDown, c1);

        c.add(panel3, c2);

        // validated list
        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.setBorder(new javax.swing.border.TitledBorder("Displayed Tasks"));
        listValidated = new JList<String>(val);
        //listValidated.setPreferredSize(new Dimension(200, 250));
        listValidated.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listValidated.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(listValidated);
        panel2.add(scrollPane2, BorderLayout.CENTER);
        panel2.setPreferredSize(new Dimension(200, 250));
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c2);

        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridwidth = 1; //end row
        initMainButtons(c2, c, this, false, "Show trace", "Cancel");


    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        if (evt.getSource() == closeButton) {
            closeDialog();
            return;
        } else if (evt.getSource() == cancelButton) {
            cancelDialog();
            return;
        }

        // Compare the action command to the known actions.
       if (command.equals("addOneIgnored")) {
            addOneIgnored();
        } else if (command.equals("addOneValidated")) {
            addOneValidated();
        } else if (command.equals("allValidated")) {
            allValidated();
        } else if (command.equals("allIgnored")) {
            allIgnored();
        } else if (command.equals("shiftUp")) {
            shiftUp();
        } else if (command.equals("shiftDown")) {
            shiftDown();
        }
    }

	private void shiftUp(){
	   int index = listValidated.getSelectedIndices()[0];
	   
	   Collections.swap(val, index, index-1);
       listValidated.setListData(val);
       setButtons();
	}


	private void shiftDown(){
	   int index = listValidated.getSelectedIndices()[0];
	   
	   Collections.swap(val, index, index+1);
       listValidated.setListData(val);
       setButtons();
	}



    private void addOneIgnored() {
        int[] list = listValidated.getSelectedIndices();
        Vector<String> v = new Vector<String>();
        String o;
        for (int i = 0; i < list.length; i++) {
            o = val.elementAt(list[i]);
            ign.addElement(o);
            v.addElement(o);
        }

        val.removeAll(v);
        listIgnored.setListData(ign);
        listValidated.setListData(val);
        setButtons();
    }

    private void addOneValidated() {
        int[] list = listIgnored.getSelectedIndices();
        Vector<String> v = new Vector<String>();
        String o;
        for (int i = 0; i < list.length; i++) {
            o = ign.elementAt(list[i]);
            val.addElement(o);
            v.addElement(o);
        }

        ign.removeAll(v);
        listIgnored.setListData(ign);
        listValidated.setListData(val);
        setButtons();
    }

    private void allValidated() {
        val.addAll(ign);
        ign.removeAllElements();
        listIgnored.setListData(ign);
        listValidated.setListData(val);
        setButtons();
    }

    private void allIgnored() {
        ign.addAll(val);
        val.removeAllElements();
        listIgnored.setListData(ign);
        listValidated.setListData(val);
        setButtons();
    }


    public void closeDialog() {
        back.removeAllElements();
        for (int i = 0; i < val.size(); i++) {
            back.addElement(val.elementAt(i));
        }
        validated = val;
        ignored = ign;
        dispose();
    }

    public void cancelDialog() {
        dispose();
    }

    private void setButtons() {
        int i1 = listIgnored.getSelectedIndex();
        int i2 = listValidated.getSelectedIndex();

        if (i1 == -1) {
            addOneValidated.setEnabled(false);
        } else {
            addOneValidated.setEnabled(true);
            //listValidated.clearSelection();
        }

        if (i2 == -1) {
            addOneIgnored.setEnabled(false);
        } else {
            addOneIgnored.setEnabled(true);
            //listIgnored.clearSelection();
        }

        if (ign.size() == 0) {
            allValidated.setEnabled(false);
        } else {
            allValidated.setEnabled(true);
        }

        if (val.size() == 0) {
            allIgnored.setEnabled(false);
            closeButton.setEnabled(false);
        } else {
            allIgnored.setEnabled(true);
            closeButton.setEnabled(true);
        }
        if (i2 < 1){
        	shiftUp.setEnabled(false);
        	
		}
		else {
			shiftUp.setEnabled(true);
		}
		
		if (i2==val.size()-1 || i2==-1){
			shiftDown.setEnabled(false);
		}
		else {
			shiftDown.setEnabled(true);
		}
    }


    public void valueChanged(ListSelectionEvent e) {
        setButtons();
    }

}
