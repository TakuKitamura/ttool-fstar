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

import myutil.Conversion;
import myutil.GraphicLib;
import ui.util.IconManager;
import ui.TGComponent;
import ui.het.CAMSBlock;
import heterogeneoustranslator.systemCAMStranslator.CAMSSignal;
import ui.TAttribute;
import ui.TDiagramPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.LinkedList;


/**
   * Class JDialogCAMSBlocks
   * Dialog for managing nodes to be validated
   * Creation: 23/06/2017
   * @version 0.1 23/06/2017
   * @author Côme DEMARIGNY
 */

public class JDialogCAMSBlocks extends JDialog implements ActionListener, ListSelectionListener {

    private LinkedList<TAttribute> attributes, attributesPar, forbidden;
    private LinkedList<Boolean> initValues;
    private LinkedList<CAMSSignal> signals, signalsPar;
    private boolean checkKeyword, checkJavaKeyword;

    private boolean cancelled = true;
    private boolean regularClose;

    protected String [] processCode;
    protected JTextArea jtaProcessCode;
    protected boolean hasProcessCode;

    private JPanel panel1, panel2;

    private TDiagramPanel tdp;
    private Frame frame;
    private int tab;
    private String defaultName;
    private String previousName;

    private String attrib; // "Attributes", "Gates", etc.

    // Panel1
    private JComboBox<String> accessBox, typeBox;
    private JTextField identifierText;
    private JTextField initialValue;
    private JTextField blockName;
    private JButton addButton;

    //Panel2
    private JList<TAttribute> listAttribute;
    private JTextField nbOfIn, nbOfOut;
    private CAMSBlock block;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;


    // Signals
    private boolean hasSignals = true;
    private JPanel panel5, panel6;
    private JComboBox<String> signalInOutBox;
    private JTextField signalText;
    private JButton addSignalButton;
    private JList<CAMSSignal> listSignal;
    private JButton upSignalButton;
    private JButton downSignalButton;
    private JButton removeSignalButton;

    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;

    /** Creates new form  */
    public JDialogCAMSBlocks(LinkedList<TAttribute> _attributes, LinkedList<CAMSSignal> _signals, LinkedList<TAttribute> _forbidden, Frame _frame, String _title, String _attrib, CAMSBlock _block, String []_processCode, boolean _hasProcessCode) {
        super(_frame, _title, true);
	frame = _frame;
	block = _block;
	attributesPar = _attributes;
	signalsPar = _signals;
	processCode = _processCode;
	attrib = _attrib;

        if (signalsPar == null) {
            signalsPar = new LinkedList<CAMSSignal> ();
            hasSignals = false;
        }

        hasProcessCode = _hasProcessCode;
        if (processCode == null) {
            processCode = new String[1];
            processCode[0] = "";
        }


        forbidden = _forbidden;
        initValues = new LinkedList<Boolean> ();
        this.attrib = attrib;

        attributes = new LinkedList<TAttribute> ();
        signals = new LinkedList<CAMSSignal> ();

        for(TAttribute attr: this.attributesPar)
            this.attributes.add (attr.makeClone());

        // for(CAMSSignal sig: this.signalsPar)
        //     this.signals.add (sig.makeClone());
	pack();
        initComponents();
        myInitComponents();
        pack();
    }


    private void myInitComponents() {
	removeButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
    }

    private void initComponents() {

    	// Issue #41 Ordering of tabbed panes 
        JTabbedPane tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();
        Container c = getContentPane();

        JPanel panelAttr = new JPanel(new BorderLayout());
        JPanel panelSignal = new JPanel(new BorderLayout());
        JPanel panelCode;
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
	GridBagLayout gridbag2 = new GridBagLayout();
        // GridBagLayout gridbag3 = new GridBagLayout();
       // GridBagLayout gridbag4 = new GridBagLayout();
        GridBagLayout gridbag5 = new GridBagLayout();
        GridBagLayout gridbag6 = new GridBagLayout();
        GridBagLayout gridbag7 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        // GridBagConstraints c3 = new GridBagConstraints();
        // GridBagConstraints c4 = new GridBagConstraints();
        GridBagConstraints c5 = new GridBagConstraints();
        GridBagConstraints c6 = new GridBagConstraints();
        GridBagConstraints c7 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Adding " + attrib));
        panel1.setPreferredSize(new Dimension(500, 500));
	panel1.setMinimumSize(new Dimension(500, 500));

        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Managing " + attrib));
        panel2.setPreferredSize(new Dimension(500, 500));
	panel2.setMinimumSize(new Dimension(500, 500));

        // first line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel("Block Name: "), c1);
	blockName = new JTextField();
	blockName.setColumns(5);
	blockName.setEditable(true);
	panel1.add(blockName, c1);

        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
	c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("access"), c1);
        panel1.add(new JLabel("identifier"), c1);
        if (attrib.equals("Attribute") || attrib.equals("Variable")) {
            panel1.add(new JLabel(" "), c1);
            panel1.add(new JLabel("initial value"), c1);
        }
        panel1.add(new JLabel(" "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel1.add(new JLabel("type"), c1);

        // second line panel1
        c1.gridwidth = 2;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        accessBox = new JComboBox<String>();
        panel1.add(accessBox, c1);
        identifierText = new JTextField();
        identifierText.setColumns(15);
        identifierText.setEditable(true);
        panel1.add(identifierText, c1);

        initialValue = new JTextField();
        initialValue.setColumns(5);
        initialValue.setEditable(true);

        if (attrib.equals("Attribute") || attrib.equals("Variable")) {
            panel1.add(new JLabel(" = "), c1);
            panel1.add(initialValue, c1);
        }

        panel1.add(new JLabel(" : "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeBox = new JComboBox<String>();
        typeBox.addActionListener(this);
        panel1.add(typeBox, c1);

        // third line panel1
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);

        // fourth line panel1
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        addButton = new JButton("Add / Modify " + attrib);
        addButton.addActionListener(this);
        panel1.add(addButton, c1);

        // 1st line panel2
        listAttribute = new JList<TAttribute> (this.attributes.toArray (new TAttribute[0]));
        listAttribute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAttribute.addListSelectionListener(this); //fixme
        JScrollPane scrollPane = new JScrollPane(listAttribute);
        scrollPane.setSize(500, 500);
        c2.gridwidth = 2; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 5;
        c2.weighty = 10.0;
        c2.weightx = 10.0;
        panel2.add(scrollPane, c2);

        // 2nd line panel2
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        panel2.add(new JLabel(""), c2);

        // third line panel2
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
        upButton = new JButton("  Up  ");
        upButton.addActionListener(this);
        panel2.add(upButton, c2);

        downButton = new JButton("Down");
        downButton.addActionListener(this);
        panel2.add(downButton, c2);

        removeButton = new JButton("Remove " + attrib);
        removeButton.addActionListener(this);
        panel2.add(removeButton, c2);

        // Signals
        panel5 = new JPanel();
        panel5.setLayout(gridbag5);
        panel5.setBorder(new javax.swing.border.TitledBorder("Adding signals"));
        panel5.setPreferredSize(new Dimension(500, 500));

        panel6 = new JPanel();
        panel6.setLayout(gridbag6);
        panel6.setBorder(new javax.swing.border.TitledBorder("Managing signals"));
        panel6.setPreferredSize(new Dimension(500, 500));

        // first line panel5
        c5.gridwidth = 1;
        c5.gridheight = 1;
        c5.weighty = 1.0;
        c5.weightx = 1.0;
        c5.gridwidth = GridBagConstraints.REMAINDER; //end row
        c5.fill = GridBagConstraints.BOTH;
        c5.gridheight = 3;
        panel5.add(new JLabel(" "), c5);

        c5.gridwidth = 1;
        c5.gridheight = 1;
        c5.weighty = 1.0;
        c5.weightx = 1.0;
        c5.anchor = GridBagConstraints.CENTER;
        c5.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel5.add(new JLabel("signal:"), c5);

        // second line panel5
        c5.gridwidth = 1;
        c5.fill = GridBagConstraints.HORIZONTAL;
        String[] v = new String[4];
        v[0] = "in";
        v[1] = "out";
	v[2] = "hybrid in";
	v[3] = "hybrid out";
        signalInOutBox = new JComboBox<String>(v);
        panel5.add(signalInOutBox, c5);
        signalText = new JTextField();
        signalText.setColumns(50);
        signalText.setEditable(true);
        panel5.add(signalText, c5);

        // third line panel5
        c5.gridwidth = GridBagConstraints.REMAINDER; //end row
        c5.fill = GridBagConstraints.BOTH;
        c5.gridheight = 3;
        panel5.add(new JLabel(" "), c5);

        // fourth line panel5
        c5.gridheight = 1;
        c5.fill = GridBagConstraints.HORIZONTAL;
        addSignalButton = new JButton("Add signal");
        addSignalButton.addActionListener(this);
        panel5.add(addSignalButton, c5);

        // 1st line panel6
        listSignal = new JList<CAMSSignal> (this.signals.toArray (new CAMSSignal[0]));
        listSignal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSignal.addListSelectionListener(this);
        scrollPane = new JScrollPane(listSignal);
        scrollPane.setSize(500, 500);
        c6.gridwidth = GridBagConstraints.REMAINDER; //end row
        c6.fill = GridBagConstraints.BOTH;
        c6.gridheight = 5;
        c6.weighty = 10.0;
        c6.weightx = 10.0;
        panel6.add(scrollPane, c6);

        // 2nd line panel4
        c6.weighty = 1.0;
        c6.weightx = 1.0;
        c6.fill = GridBagConstraints.BOTH;
        c6.gridheight = 1;
        panel6.add(new JLabel(""), c6);

        // third line panel4
        c6.gridwidth = GridBagConstraints.REMAINDER; //end row
        c6.fill = GridBagConstraints.HORIZONTAL;
        upSignalButton = new JButton("Up");
        upSignalButton.addActionListener(this);
        panel6.add(upSignalButton, c6);

        downSignalButton = new JButton("Down");
        downSignalButton.addActionListener(this);
        panel6.add(downSignalButton, c6);

        removeSignalButton = new JButton("Remove signal");
        removeSignalButton.addActionListener(this);
        panel6.add(removeSignalButton, c6);

        // Prototyping
        panelCode = new JPanel();
        panelCode.setLayout(gridbag7);

        panelCode.setBorder(new javax.swing.border.TitledBorder("Process code"));
        // guard
        c7.weighty = 1.0;
        c7.weightx = 1.0;
        c7.gridwidth = 1;
        c7.gridheight = 1;
        c7.fill = GridBagConstraints.BOTH;
        c7.gridwidth = GridBagConstraints.REMAINDER;
        c7.gridheight = 1;


        panelCode.add(new JLabel("Process code of block:"), c7);
	//panelCode.add(new JLabel("To implement a method m of block B: \"userImplemented_B_m(...){...}\""), c7);
        jtaProcessCode = new JTextArea();
        jtaProcessCode.setEditable(true);
        jtaProcessCode.setMargin(new Insets(10, 10, 10, 10));
        jtaProcessCode.setTabSize(3);
        String files = "";
        if (processCode != null) {
            for(int i=0; i<processCode.length; i++) {
                files += processCode[i] + "\n";
            }
        }
        jtaProcessCode.append(files);
        jtaProcessCode.setFont(new Font("times", Font.PLAIN, 12));
        JScrollPane jsp = new JScrollPane(jtaProcessCode, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setPreferredSize(new Dimension(500, 500));
        panelCode.add(jsp, c7);


        // main panel;
        panelAttr.add(panel1, BorderLayout.WEST);
        panelAttr.add(panel2, BorderLayout.EAST);
        tabbedPane.addTab("Attributes", panelAttr);

        if (hasSignals) {
            panelSignal.add(panel5, BorderLayout.WEST);
            panelSignal.add(panel6, BorderLayout.EAST);
            tabbedPane.addTab("Signals", panelSignal);
        }

        if (hasProcessCode) {
            tabbedPane.addTab("Process", panelCode);
        }

        tabbedPane.setSelectedIndex(tab);

        //c.add(panel1, c0);
        //c.add(panel2, c0);

        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.HORIZONTAL;
        c.add(tabbedPane, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        
        closeButton = new JButton("Save and Close", IconManager.imgic25);
        //closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        c.add(closeButton, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        c.add(cancelButton, c0);
    }

    public void actionPerformed(ActionEvent evt)  {
        if (evt.getSource() == typeBox) {
            boolean b = initValues.get (typeBox.getSelectedIndex()).booleanValue();
            initialValue.setEnabled(b);
            return;
        }


        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == addButton) {
            addAttribute();
        } else if (evt.getSource() == cancelButton) {
            cancelDialog();
        } else if (evt.getSource() == removeButton) {
            removeAttribute();
        } else if (evt.getSource() == downButton) {
            downAttribute();
        } else if (evt.getSource() == upButton) {
            upAttribute();
        } else if (evt.getSource() == downSignalButton) {
            downSignal();
        } else if (evt.getSource() == upSignalButton) {
            upSignal();
        } else if (evt.getSource() == removeSignalButton) {
            removeSignal();
        } else if (evt.getSource() == addSignalButton) {
            addSignal();
	}
    }

    public void addAccess(String s) {
        accessBox.addItem(s);
    }

    public void addType(String s) {
        initValues.add(new Boolean(true));
        typeBox.addItem(s);
    }

    public void addType(String s, boolean b) {
        initValues.add(new Boolean(b));
        typeBox.addItem(s);
    }

    public void enableInitialValue(boolean b) {
        initialValue.setEnabled(b);
    }

    public void enableRTLOTOSKeyword(boolean b) {
        checkKeyword = !b;
    }

    public void enableJavaKeyword(boolean b) {
        checkJavaKeyword = !b;
    }



    public void addAttribute() {
         Object o1 = accessBox.getSelectedItem();
         Object o2 = typeBox.getSelectedItem();
         String s = identifierText.getText();
         String value = initialValue.getText();
         TAttribute a;

         if (s.length()>0) {
             if ((TAttribute.isAValidId(s, checkKeyword, checkJavaKeyword)) && (TAttribute.notIn(s, forbidden))){
                 int i = TAttribute.getAccess(o1.toString());
                 int j = TAttribute.getCAMSType(o2.toString());
		 if ((j == TAttribute.ARRAY_NAT) && (value.length() < 1)) {
                     value = "2";
                }

                if ((i != -1) && (j!= -1)) {

                    if ((value.length() < 1) || (initialValue.isEnabled() == false)){

                        value = "";
                    } else {
                        if (!TAttribute.isAValidInitialValue(j, value)) {
                            JOptionPane.showMessageDialog(frame,
                                                          "The initial value is not valid",
                                                          "Error",
                                                          JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    }
                    if (j == TAttribute.OTHER) {
                        a = new TAttribute(i, s, value, o2.toString());
                        a.isCAMS = true;
                        //System.out.println("New attribute: " + o2.toString());
                    } else {
                        a = new TAttribute(i, s, value, j);
                        a.isCAMS = true;
                    }
                    //checks whether the same attribute already belongs to the list
                    int index = attributes.size();
                    if (attributes.contains(a)) {
                        index = attributes.indexOf(a);
                        a = attributes.get (index);
                        a.setAccess(i);
                        if (j == TAttribute.OTHER) {
                            a.setTypeOther(o2.toString());
                        }
                        a.setType(j);
                        a.setInitialValue(value);
                    } else {
                        attributes.add(index, a);
                    }
                    listAttribute.setListData(attributes.toArray (new TAttribute[0]));
                    identifierText.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame,
                                                  "Bad access / type",
                                                  "Error",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(frame,
                                              "Bad identifier: identifier already in use, or invalid identifier",
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                                          "Bad identifier",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

 
    public void addSignal() {
        String s = signalText.getText();
        CAMSSignal cs = CAMSSignal.isAValidSignal(s, signalInOutBox.getSelectedIndex());
        CAMSSignal cstmp;
	int inout;

        if (cs != null) {
            // Checks whether the same signal already belongs to the list
            int index = -1;
            for(int i=0; i<signals.size(); i++) {
                cstmp = signals.get(i);
                // Same id?
                if (cstmp.equals(cs)) {
                    index = i;
                    break;
                }
            }  
	    if(signalInOutBox.getSelectedIndex()==0){
		inout=0;
	    }else if(signalInOutBox.getSelectedIndex()==1){
		inout=1;
	    }else if(signalInOutBox.getSelectedIndex()==2){
		inout=2;
	    }else if(signalInOutBox.getSelectedIndex()==3){
		inout=3;
	    }else{
		inout=-1;
	    }
	    cs = new CAMSSignal(s,inout);
            if (index == -1) {
                signals.add(cs);
            } else {
                signals.remove (index);
                signals.add (index, cs);
            }
            listSignal.setListData(signals.toArray (new CAMSSignal[0]));
            signalText.setText("");

        } else {
            JOptionPane.showMessageDialog(frame,
                                          "Badly formatted signal declaration",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    public void removeAttribute() {
        int i = listAttribute.getSelectedIndex() ;
        if (i!= -1) {
            TAttribute a = attributes.get (i);
            a.setAccess(-1);
            attributes.remove (i);
            listAttribute.setListData(attributes.toArray (new TAttribute[0]));
        }
    }

    public void downAttribute() {
        int i = listAttribute.getSelectedIndex();
        if ((i!= -1) && (i != attributes.size() - 1)) {
            TAttribute o = attributes.get (i);
            attributes.remove (i);
            attributes.add (i+1, o);
            listAttribute.setListData(attributes.toArray (new TAttribute[0]));
            listAttribute.setSelectedIndex(i+1);
        }
    }

    public void upAttribute() {
        int i = listAttribute.getSelectedIndex();
        if (i > 0) {
            TAttribute o = attributes.get (i);
            attributes.remove (i);
            attributes.add (i-1, o);
            listAttribute.setListData(attributes.toArray (new TAttribute[0]));
            listAttribute.setSelectedIndex(i-1);
        }
    }

    public void removeSignal() {
        int i = listSignal.getSelectedIndex() ;
        if (i!= -1) {
            signals.remove (i);
            listSignal.setListData(signals.toArray (new CAMSSignal [0]));
        }
    }

    public void upSignal() {
        int i = listSignal.getSelectedIndex();
        if (i > 0) {
            CAMSSignal o = signals.get (i);
            signals.remove (i);
            signals.add (i-1, o);
            listSignal.setListData(signals.toArray (new CAMSSignal [0]));
            listSignal.setSelectedIndex(i-1);
        }
    }

    public void downSignal() {
        int i = listSignal.getSelectedIndex();
        if ((i!= -1) && (i != signals.size() - 1)) {
            CAMSSignal o = signals.get (i);
            signals.remove(i);
            signals.add (i+1, o);
            listSignal.setListData(signals.toArray (new CAMSSignal [0]));
            listSignal.setSelectedIndex(i+1);
        }
    }


    public void closeDialog() {
        cancelled = false;
        attributesPar.clear ();
        for(TAttribute attr: this.attributes)
            attributesPar.add (attr);

        signalsPar.clear ();
        for(CAMSSignal sig: this.signals)
            signalsPar.add (sig);

        processCode =  Conversion.wrapText(jtaProcessCode.getText());
        dispose();
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public boolean isRegularClose() {
	return regularClose;
    }

    public void cancelDialog() {
        dispose();
    }

    public void valueChanged(ListSelectionEvent e) {
        int i = listAttribute.getSelectedIndex() ;
        if (i == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            identifierText.setText("");
            //initialValue.setText("");
        } else {
            TAttribute a = attributes.get (i);
            identifierText.setText(a.getId());
            initialValue.setText(a.getInitialValue());
            select(accessBox, TAttribute.getStringAccess(a.getAccess()));
            if (a.getType() == TAttribute.OTHER) {
                select(typeBox, a.getTypeOther());
            } else {
		//select(typeBox, TAttribute.getStringCAMSType(a.getType())); //fixme
            }
            removeButton.setEnabled(true);
            if (i > 0) {
                upButton.setEnabled(true);
            } else {
                upButton.setEnabled(false);
            }
            if (i != attributes.size() - 1) {
                downButton.setEnabled(true);
            } else {
                downButton.setEnabled(false);
            }
        }

        i = listSignal.getSelectedIndex() ;
        if (i == -1) {
            removeSignalButton.setEnabled(false);
            upSignalButton.setEnabled(false);
            downSignalButton.setEnabled(false);
            signalText.setText("");
            //initialValue.setText("");
        } else {
            CAMSSignal as = signals.get (i);
            //signalText.setText(as.toBasicString()); //fixme
	    // signalInOutBox.setSelectedIndex(as.getInOut());
            removeSignalButton.setEnabled(true);
            if (i > 0) {
                upSignalButton.setEnabled(true);
            } else {
                upSignalButton.setEnabled(false);
            }
            if (i != signals.size() - 1) {
                downSignalButton.setEnabled(true);
            } else {
                downSignalButton.setEnabled(false);
            }
        }
    }

    public void select(JComboBox<String> jcb, String text) {
        String s;
        for(int i=0; i<jcb.getItemCount(); i++) {
            s = jcb.getItemAt(i);
            if (s.equals(text)) {
                jcb.setSelectedIndex(i);
                return;
            }
        }
    }


    public String getBlockName() {
	if(blockName.getText() == null || blockName.getText() == "" || blockName.getText() == " ") {
	    return "Block0";
	} else {
	    return blockName.getText();
	}
    }

    public LinkedList<CAMSSignal> getSignals(){
	return signals;
    }

    public String[] getProcessCode() {
        return processCode;
    }

}
