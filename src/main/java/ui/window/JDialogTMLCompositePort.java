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

import ui.TGComponent;
import ui.TType;
import ui.tmlcd.TMLDataFlowType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;


/**
 * Class JDialogTMLCompositePort
 * Dialog for managing port properties
 * Creation: 26/03/2008
 *
 * @author Ludovic APVRILLE
 * @version 1.0 26/03/2008
 */

public class JDialogTMLCompositePort extends JDialogBase implements ActionListener {

    private JPanel panel3;
    private Frame frame;

    private String name;
    private String dataFlowType = "VOID";
    private String associatedEvent = "VOID";
    private TType type1, type2, type3, type4, type5;
    private boolean isFinite, isBlocking, isOrigin;
    private String maxInFIFO, widthSamples;

    private boolean isLossy, isPrex, isPostex;
    private int lossPercentage;
    private int maxNbOfLoss; //-1 means no max

    private int vc;

    public boolean data;
    public boolean checkConf;
    public boolean checkAuth;
    // Panel1
    private JTextField nameText, maxText, widthText, associatedEventJT, vcText;
    private JComboBox<String> typePort, typeList1, typeList2, typeList3, typeList4, typeList5;
    private JComboBox<String> origin, finite, blocking, dfType;
    private JComboBox<TGComponent> refReq;
    private JLabel lossPercentageLabel, maxNbOfLossLabel;
    private int portIndex;
    private Vector<String> origins, finites, blockings, portTypes, types1, types2, types3, types4, types5;
    private Vector<String> types;
    private Vector<TGComponent> refs;
    private TGComponent reference;

    // Robustness
    private JCheckBox isLossyBox, isPrexCB, isPostexCB, confCheckBox, authCheckBox;
    private JTextField lossPercentageText, maxNbOfLossText;


    public JDialogTMLCompositePort(String _name, int _portIndex, TType _type1, TType _type2, TType _type3, TType _type4,
                                   TType _type5, boolean _isOrigin, boolean _isFinite, boolean _isBlocking,
                                   String _maxInFIFO, String _widthSamples, boolean _isLossy,
                                   int _lossPercentage, int _maxNbOfLoss, Frame f,
                                   String title, Vector<String> _types,
                                   String _dataFlowType, String _associatedEvent, boolean _isPrex,
                                   boolean _isPostex, boolean _checkConf,
                                   boolean _checkAuth, TGComponent _reference, Vector<TGComponent> _refs,
                                   int _vc) {
        super(f, title, true);
        frame = f;

        name = _name;
        portIndex = _portIndex;
        type1 = _type1;
        type2 = _type2;
        type3 = _type3;
        type4 = _type4;
        type5 = _type5;

        types = _types;

        data = false;

        dataFlowType = _dataFlowType;
        associatedEvent = _associatedEvent;
        maxInFIFO = _maxInFIFO;
        widthSamples = _widthSamples;
        isOrigin = _isOrigin;
        isFinite = _isFinite;
        isBlocking = _isBlocking;

        isPrex = _isPrex;
        isPostex = _isPostex;
        isLossy = _isLossy;
        lossPercentage = _lossPercentage;
        maxNbOfLoss = _maxNbOfLoss;
        checkConf = _checkConf;
        checkAuth = _checkAuth;
        refs = _refs;
        vc = _vc;
        reference = _reference;
        myInitComponents();
        initComponents();
        checkMode();

        pack();
    }

    private void myInitComponents() {

        portTypes = new Vector<>();
        portTypes.add("Channel");
        portTypes.add("Event");
        portTypes.add("Request");

        types1 = new Vector<>();
        types2 = new Vector<>();
        types3 = new Vector<>();
        types4 = new Vector<>();
        types5 = new Vector<>();
        types1.add(TType.getStringType(0));
        types1.add(TType.getStringType(1));
        types1.add(TType.getStringType(2));

        types2.add(TType.getStringType(0));
        types2.add(TType.getStringType(1));
        types2.add(TType.getStringType(2));

        types3.add(TType.getStringType(0));
        types3.add(TType.getStringType(1));
        types3.add(TType.getStringType(2));

        types4.add(TType.getStringType(0));
        types4.add(TType.getStringType(1));
        types4.add(TType.getStringType(2));

        types5.add(TType.getStringType(0));
        types5.add(TType.getStringType(1));
        types5.add(TType.getStringType(2));

        addTypes(types1, types);
        addTypes(types2, types);
        addTypes(types3, types);
        addTypes(types4, types);
        addTypes(types5, types);

        origins = new Vector<>();
        origins.add("Origin");
        origins.add("Destination");

        finites = new Vector<>();
        finites.add("Finite FIFO");
        finites.add("Infinite FIFO");

        blockings = new Vector<>();
        blockings.add("Blocking");
        blockings.add("Non-blocking FIFO");
    }

    private void addTypes(Vector<String> v, Vector<String> types) {
        v.addAll(types);
    }

    private void initComponents() {
        int i;

        Vector<String> dataFlowTypes = new Vector<>();
        dataFlowTypes.add(TMLDataFlowType.INT_16);
        dataFlowTypes.add(TMLDataFlowType.INT_32);
        dataFlowTypes.add(TMLDataFlowType.INT_64);

        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
        GridBagLayout gridbag3 = new GridBagLayout();
        GridBagConstraints c3 = new GridBagConstraints();
        GridBagConstraints c4 = new GridBagConstraints();
        GridBagLayout gridbag4 = new GridBagLayout();
        GridBagConstraints c5 = new GridBagConstraints();
        GridBagLayout gridbag5 = new GridBagLayout();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Name, type and parameters"));
        panel1.setPreferredSize(new Dimension(300, 150));

        // First line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);

        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Name:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        nameText = new JTextField(name);
        panel1.add(nameText, c1);

        // Type of port
        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Type:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typePort = new JComboBox<>(portTypes);
        typePort.setSelectedIndex(portIndex);
        typePort.addActionListener(this);
        panel1.add(typePort, c1);

        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Origin:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        origin = new JComboBox<>(origins);
        if (isOrigin) {
            origin.setSelectedIndex(0);
        } else {
            origin.setSelectedIndex(1);
        }
        origin.addActionListener(this);
        panel1.add(origin, c1);

        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Type #1"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeList1 = new JComboBox<>(types1);
        //TraceManager.addDev("type1= " + type1);
        if (type1.getType() < TType.OTHER) {
            typeList1.setSelectedIndex(type1.getType());
        } else {
            for (i = TType.OTHER; i < types1.size(); i++) {
                //TraceManager.addDev("Looking for:"  + type1.getTypeOther());
                //TraceManager.addDev("Current type:"  +  types1.get(i));
                if ((types1.get(i)).compareTo(type1.getTypeOther()) == 0) {
                    typeList1.setSelectedIndex(i);
                    break;
                }
            }
        }
        panel1.add(typeList1, c1);

        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Type #2"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeList2 = new JComboBox<>(types2);
        if (type2.getType() < TType.OTHER) {
            typeList2.setSelectedIndex(type2.getType());
        } else {
            for (i = TType.OTHER; i < types2.size(); i++) {
                //TraceManager.addDev("Looking for:"  + type1.getTypeOther());
                //TraceManager.addDev("Current type:"  +  types1.get(i));
                if ((types2.get(i)).compareTo(type2.getTypeOther()) == 0) {
                    typeList2.setSelectedIndex(i);
                    break;
                }
            }
        }
        panel1.add(typeList2, c1);

        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Type: #3"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeList3 = new JComboBox<>(types3);
        if (type3.getType() < TType.OTHER) {
            typeList3.setSelectedIndex(type3.getType());
        } else {
            for (i = TType.OTHER; i < types3.size(); i++) {
                //TraceManager.addDev("Looking for:"  + type1.getTypeOther());
                //TraceManager.addDev("Current type:"  +  types1.get(i));
                if ((types3.get(i)).compareTo(type3.getTypeOther()) == 0) {
                    typeList3.setSelectedIndex(i);
                    break;
                }
            }
        }
        panel1.add(typeList3, c1);

        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Type: #4"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeList4 = new JComboBox<>(types4);
        if (type4.getType() < TType.OTHER) {
            typeList4.setSelectedIndex(type4.getType());
        } else {
            for (i = TType.OTHER; i < types4.size(); i++) {
                //TraceManager.addDev("Looking for:"  + type1.getTypeOther());
                //TraceManager.addDev("Current type:"  +  types1.get(i));
                if ((types4.get(i)).compareTo(type4.getTypeOther()) == 0) {
                    typeList4.setSelectedIndex(i);
                    break;
                }
            }
        }
        panel1.add(typeList4, c1);


        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Type: #5"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeList5 = new JComboBox<>(types5);
        if (type5.getType() < TType.OTHER) {
            typeList5.setSelectedIndex(type5.getType());
        } else {
            for (i = TType.OTHER; i < types5.size(); i++) {
                //TraceManager.addDev("Looking for:"  + type1.getTypeOther());
                //TraceManager.addDev("Current type:"  +  types1.get(i));
                if ((types5.get(i)).compareTo(type5.getTypeOther()) == 0) {
                    typeList5.setSelectedIndex(i);
                    break;
                }
            }
        }
        panel1.add(typeList5, c1);

        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Blocking?"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        blocking = new JComboBox<>(blockings);
        if (isBlocking) {
            blocking.setSelectedIndex(0);
        } else {
            blocking.setSelectedIndex(1);
        }
        blocking.addActionListener(this);
        panel1.add(blocking, c1);

        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Finite?"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        finite = new JComboBox<>(finites);
        if (isFinite) {
            finite.setSelectedIndex(0);
        } else {
            finite.setSelectedIndex(1);
        }
        finite.addActionListener(this);
        panel1.add(finite, c1);

        c1.gridwidth = 1;
        panel1.add(new JLabel("Width (in Byte)="), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        widthText = new JTextField(widthSamples);
        panel1.add(widthText, c1);

        c1.gridwidth = 1;
        panel1.add(new JLabel("Capacity="), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        maxText = new JTextField(maxInFIFO);
        panel1.add(maxText, c1);



        JPanel panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Properties and Verification "));
        panel2.setPreferredSize(new Dimension(300, 300));

        //If related to security requirement, allow reference to the requirement
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.CENTER;
        panel2.add(new JLabel("Reference Requirement"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        refReq = new JComboBox<>(refs);
        panel2.add(refReq, c2);
        if (reference != null) {
            refReq.setSelectedItem(reference);
        }


        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 3;
        panel2.add(new JLabel(" "), c2);

        //Security Properties to check
        confCheckBox = new JCheckBox("Check Confidentiality");
        panel2.add(confCheckBox, c2);
        confCheckBox.addActionListener(this);
        confCheckBox.setSelected(checkConf);
        authCheckBox = new JCheckBox("Check Authenticity");
        authCheckBox.addActionListener(this);
        panel2.add(authCheckBox, c2);
        authCheckBox.setSelected(checkAuth);






        // Code generation
        JPanel panel4 = new JPanel();
        panel4.setLayout(gridbag4);
        panel4.setBorder(new javax.swing.border.TitledBorder("Code generation "));
        panel4.setPreferredSize(new Dimension(300, 300));
        c4.gridwidth = 1;
        c4.gridheight = 1;
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.BOTH;
        c4.gridheight = 3;
        panel4.add(new JLabel(" "), c4);
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.CENTER;
        panel4.add(new JLabel("Dataflow type"), c2);
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        dfType = new JComboBox<>(dataFlowTypes);
        if ((dataFlowType == null) || (dataFlowType.equals("VOID")) || (dataFlowType.equals(""))) {
            dfType.setSelectedIndex(0);
        } else {
            dfType.setSelectedIndex(dataFlowTypes.indexOf(dataFlowType));
        }
        dfType.addActionListener(this);
        panel4.add(dfType, c4);

        c4.gridwidth = 1;
        if (associatedEvent.equals("VOID") || associatedEvent.equals("")) {
            associatedEventJT = new JTextField("", 15);
        } else {
            associatedEventJT = new JTextField(associatedEvent, 15);
        }
        panel4.add(new JLabel("Associate to event"), c4);
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel4.add(associatedEventJT, c4);

        c4.gridwidth = 1;
        //c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        isPrexCB = new JCheckBox("Prex");
        isPrexCB.setSelected(isPrex);
        panel4.add(isPrexCB, c4);

        c4.gridwidth = 1;
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        isPostexCB = new JCheckBox("Postex");
        isPostexCB.setSelected(isPostex);
        panel4.add(isPostexCB, c4);


        
        /*c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.CENTER;
        blocking = new JCheckBox("Blocking FIFO");
        blocking.setSelected(isBlocking);
        blocking.addActionListener(this);
        panel2.add(blocking, c1);*/

        // Robustness
        panel3 = new JPanel();
        panel3.setLayout(gridbag3);
        panel3.setBorder(new javax.swing.border.TitledBorder("Robustness "));
        panel3.setPreferredSize(new Dimension(300, 300));
        c3.gridwidth = 1;
        c3.gridheight = 1;
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        panel3.add(new JLabel(" "), c3);

        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.CENTER;
        isLossyBox = new JCheckBox("Lossy");
        isLossyBox.setSelected(isLossy);
        panel3.add(isLossyBox, c3);

        c3.gridwidth = 1;
        lossPercentageLabel = new JLabel("Loss percentage");
        panel3.add(lossPercentageLabel, c3);
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        lossPercentageText = new JTextField("" + lossPercentage);
        panel3.add(lossPercentageText, c3);

        c3.gridwidth = 1;
        maxNbOfLossLabel = new JLabel("Max nb of loss");
        panel3.add(maxNbOfLossLabel, c3);
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        maxNbOfLossText = new JTextField("" + maxNbOfLoss);
        panel3.add(maxNbOfLossText, c3);

        JPanel panel5 = new JPanel();
        panel5.setLayout(gridbag5);
        panel5.setBorder(new javax.swing.border.TitledBorder("Network"));
        panel5.setPreferredSize(new Dimension(300, 300));
        c5.gridwidth = 1;
        c5.gridheight = 1;
        c5.weighty = 1.0;
        c5.weightx = 1.0;
        c5.gridwidth = GridBagConstraints.REMAINDER; //end row
        c5.fill = GridBagConstraints.BOTH;
        c5.gridheight = 5;
        panel5.add(new JLabel(" "), c5);



        c5.gridwidth = 1;
        lossPercentageLabel = new JLabel("VC:");
        panel5.add(lossPercentageLabel, c5);
        c5.gridwidth = GridBagConstraints.REMAINDER; //end row
        vcText = new JTextField("" + vc);
        panel5.add(vcText, c5);



        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.BOTH;

        c.add(panel1, c0);
        c.add(panel2, c0);
        c.add(panel4, c0);
        c.add(panel3, c0);
        c.add(panel5, c0);


        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;

        initButtons(c0, c, this);
        repaint();
    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        checkMode();
        
        /*if (evt.getSource() == finite) {
            if (finite.getSelectedIndex() == 1) {
				blocking.setSelectedIndex(1);
			}
           checkMode();
        }*/

        // Compare the action command to the known actions.
        if (command.equals("Save and Close")) {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
    }


    public void closeDialog() {
        data = true;
        dataFlowType = dfType.getItemAt(dfType.getSelectedIndex());
        associatedEvent = associatedEventJT.getText();
        isPrex = isPrexCB.isSelected();
        isPostex = isPostexCB.isSelected();
        checkConf = confCheckBox.isSelected();
        checkAuth = authCheckBox.isSelected();
        if (isPrex && isPostex) {
            JOptionPane.showMessageDialog(frame, "A channel cannot be marked as both prex and postex", "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        dispose();
    }

    public String getDataFlowType() {
        return dataFlowType;
    }

    public String getAssociatedEvent() {
        return associatedEvent;
    }

    public void cancelDialog() {
        dispose();
    }

    public TGComponent getReference() {
        return (TGComponent) refReq.getSelectedItem();
    }

    public void checkMode() {
        int index = typePort.getSelectedIndex();
        if (index == 0) {
            // channel
            origin.setEnabled(true);
            if (origin.getSelectedIndex()==0){
            	confCheckBox.setEnabled(true);
  	            authCheckBox.setEnabled(false);          	
            }
            else {
	            authCheckBox.setEnabled(true);
            	confCheckBox.setEnabled(false);	            
	        }
            typeList1.setEnabled(false);
            typeList2.setEnabled(false);
            typeList3.setEnabled(false);
            typeList4.setEnabled(false);
            typeList5.setEnabled(false);
            blocking.setEnabled(true);
            finite.setEnabled(false);
            widthText.setEnabled(true);
            maxText.setEnabled((origin.getSelectedIndex() == 0) && (blocking.getSelectedIndex() == 0));

        } else if (index == 1) {
            // Event
            origin.setEnabled(true);
            confCheckBox.setEnabled(false);
            authCheckBox.setEnabled(false);
            typeList1.setEnabled(true);
            typeList2.setEnabled(true);
            typeList3.setEnabled(true);
            typeList4.setEnabled(true);
            typeList5.setEnabled(true);
            dfType.setEnabled(false);
            associatedEventJT.setEnabled(false);
            isPrexCB.setEnabled(false);
            isPostexCB.setEnabled(false);
            if (origin.getSelectedIndex() == 0) {
                blocking.setEnabled(true);
                finite.setEnabled(true);
                if (blocking.getSelectedIndex() == 0) {
                    finite.setSelectedIndex(0);
                }
            } else {
                blocking.setEnabled(false);
                blocking.setSelectedIndex(0);
                finite.setEnabled(false);
            }

            widthText.setEnabled(false);
            maxText.setEnabled(finite.getSelectedIndex() == 0);

        } else {
            // Request
            origin.setEnabled(true);
            
            confCheckBox.setEnabled(false);
            authCheckBox.setEnabled(false);
            typeList1.setEnabled(true);
            typeList2.setEnabled(true);
            typeList3.setEnabled(true);
            typeList4.setEnabled(true);
            typeList5.setEnabled(true);
            dfType.setEnabled(false);
            associatedEventJT.setEnabled(false);
            isPrexCB.setEnabled(false);
            isPostexCB.setEnabled(false);
            blocking.setEnabled(false);
            if (origin.getSelectedIndex() == 0) {
                blocking.setSelectedIndex(1);
            } else {
                blocking.setSelectedIndex(0);
            }
            finite.setEnabled(false);
            finite.setSelectedIndex(1);
            widthText.setEnabled(false);
            maxText.setEnabled(false);

        }
           /*maxText.setEnabled(finite.isSelected());
           blocking.setEnabled(finite.isSelected());*/

        panel3.setEnabled(origin.getSelectedIndex() == 0);
        isLossyBox.setEnabled(origin.getSelectedIndex() == 0);
        lossPercentageText.setEnabled(origin.getSelectedIndex() == 0);
        maxNbOfLossText.setEnabled(origin.getSelectedIndex() == 0);
        lossPercentageLabel.setEnabled(origin.getSelectedIndex() == 0);
        vcText.setEnabled(origin.getSelectedIndex() == 0);
        maxNbOfLossLabel.setEnabled(origin.getSelectedIndex() == 0);
        if (confCheckBox.isSelected() || authCheckBox.isSelected()) {
            refReq.setEnabled(true);
        } else {
            refReq.setEnabled(false);
        }
    }

    public boolean hasNewData() {
        return data;
    }

    public String getParamName() {
        return nameText.getText();
    }

    public String getMaxSamples() {
        return maxText.getText();
    }

    public String getWidthSamples() {
        return widthText.getText();
    }

    public boolean isOrigin() {
        return (origin.getSelectedIndex() == 0);
    }

    public boolean isFinite() {
        return (finite.getSelectedIndex() == 0);
    }

    public boolean isBlocking() {
        return (blocking.getSelectedIndex() == 0);
    }

    public boolean isChannelPrex() {
        return isPrex;
    }

    public boolean isChannelPostex() {
        return isPostex;
    }

    public int getPortType() {
        return typePort.getSelectedIndex();
    }

    public int getType(int i) {
        switch (i) {
            case 0:
                return typeList1.getSelectedIndex();
            case 1:
                return typeList2.getSelectedIndex();
            case 2:
                return typeList3.getSelectedIndex();
            case 3:
                return typeList4.getSelectedIndex();
            case 4:
                return typeList5.getSelectedIndex();
            default:
                return typeList1.getSelectedIndex();
        }

    }

    public String getStringType(int i) {
        int index = getType(i);
        if (index < 3) {
            return TType.getStringType(index);
        }

        return types.get(index - 3);
    }

    public boolean isLossy() {
        return isLossyBox.isSelected();
    }

    public int getLossPercentage() {
        try {
            return Integer.decode(lossPercentageText.getText().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    public int getMaxNbOfLoss() {
        try {
            return Integer.decode(maxNbOfLossText.getText().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    public int getVC() {
        try {
            return Integer.decode(vcText.getText().trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
