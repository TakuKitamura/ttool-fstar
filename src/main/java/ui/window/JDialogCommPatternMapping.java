/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea ENRICI
 *
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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

import myutil.GraphicLib;
import myutil.TraceManager;
import tmltranslator.TMLAttribute;
import tmltranslator.TMLCP;
import tmltranslator.TMLType;
import tmltranslator.modelcompiler.CPMEC;
import tmltranslator.tmlcp.TMLCPSequenceDiagram;
import ui.GTMLModeling;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TMLCommunicationPatternPanel;
import ui.tmldd.TMLArchiCPNode;
import ui.tmldd.TMLArchiNode;
import ui.tmlsd.TMLSDControllerInstance;
import ui.tmlsd.TMLSDPanel;
import ui.tmlsd.TMLSDStorageInstance;
import ui.tmlsd.TMLSDTransferInstance;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Class JDialogReferenceCP Dialog for mapping CPs onto the architecture
 * Creation: 22/08/2014
 *
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @version 1.0 22/08/2014
 */
public class JDialogCommPatternMapping extends JDialog /* implements ActionListener, ListSelectionListener */ {

    private final static int STORAGE = 0;
    private final static int TRANSFER = 1;
    private final static int CONTROLLER = 2;
    private final static String EMPTY_MAPPABLE_ARCH_UNITS_LIST = "No units to map";
    private final static String EMPTY_CPS_LIST = "No CPs to reference";
    private final static String EMPTY_INSTANCES_LIST = "No instances to map";

    // Issue #36 only the cancel boolean is needed
    // private boolean regularClose;

    private Frame frame;
    private TMLArchiCPNode cp;
    protected JTextField nameOfCP;
    private String name = "";
    // private LinkedList<TMLArchiNode> availableUnits;
    private Vector<String> mappedUnitsSL = new Vector<String>();

    private java.util.List<TMLCommunicationPatternPanel> listCPs = new ArrayList<TMLCommunicationPatternPanel>();
    private Vector<String> communicationPatternsSL = new Vector<String>();

    private java.util.List<Set<String>> listInstancesHash = new ArrayList<Set<String>>(); // the list of AVAILABLE
                                                                                          // instances
    // and array list containing the SD instances for each CP. The array list is
    // indexed the same way as listCPs
    private java.util.List<Set<String>> listOfMappedInstances = new ArrayList<Set<String>>();
    private java.util.List<Set<String>> sdStorageInstances = new ArrayList<Set<String>>();
    private java.util.List<Set<String>> sdTransferInstances = new ArrayList<Set<String>>();
    private java.util.List<Set<String>> sdControllerInstances = new ArrayList<Set<String>>();

    private Vector<String> mappableArchUnitsSL;
    private Vector<String> sdInstancesSL;

    // private int indexListCPsNames = 0;

    private boolean emptyCPsList = false;
    private boolean emptyListOfMappedUnits = true; // true if there is no mapping info

    private boolean cancelled = true;

    // Panel1
    private JPanel pnlComPatternStruct;
    private JComboBox<String> sdInstancesCB, /* mappableArchUnitsCB, */
            communicationPatternsCB;
    private ActionListener sdInstancesCBActionListener, communicationPatternsCBActionListener;
    private JButton mapButton;
    private JList<String> mappableArchUnitsJL;
    private JScrollPane mappableArchUnitsSP;

    // Panel2
    private JPanel pnmManageStruct;
    private JList<String> listMappedUnitsJL;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;
    private JScrollPane scrollPane;

    private JPanel pnlRootContainerInstances;
    private JPanel pnlRootContainerAttributes;

    // Panel3: assign a value to CP attributes
    private JPanel pnlAttributeValues;
    private JButton attributeButton, addressButton;
    private JComboBox<String> attributesList_CB/* , applicationAttributesList_CB */, addressList_CB;
    private JTextField attributesValue_TF, addressValue_TF;
    private Vector<String> attributesVector, applicationAttributesVector, addressVector;

    // Panel4: assign a value to CP attributes
    private JPanel pnlManageAttributes;
    private JScrollPane scrollPaneAttributes;
    private JList<String> scrollPaneAttributes_JL;
    private Vector<String> assignedAttributes/* , assignedAddresses */;
    private JButton removeAttributeButton;

    private JTabbedPane tabbedPane;

    // Panel5, code generation
    private JPanel pnlCodeGen;
    private JComboBox<String> cpMECsCB, transferTypeCB1, transferTypeCB2;
    // private JList<String> cpMECsList;
    private String cpMEC;
    private int transferType1, transferType2;

    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;

    /*
     * Creates new form
     */
    public JDialogCommPatternMapping(JFrame _frame, String _title, TMLArchiCPNode _cp, Vector<String> _mappedUnits,
            String _name, String _cpMEC, Vector<String> _assignedAttributes, int _transferType1, int _transferType2) {
        super(_frame, _title, true);

        frame = _frame;
        cp = _cp;
        name = _name;
        cpMEC = _cpMEC;
        transferType1 = _transferType1;
        transferType2 = _transferType2;

        mappedUnitsSL = new Vector<String>(); // take into account the elements already mapped

        if (_mappedUnits.size() > 0) { // the validity of _mappedUnits is checked when initializing components
            mappedUnitsSL.addAll(_mappedUnits);
            emptyListOfMappedUnits = false;
        }
        // else {
        // mappedUnitsSL = new Vector<String>();
        // }

        if (_assignedAttributes.size() > 0) { // the validity of _assignedAttributes is checked when initializing
                                              // components
            assignedAttributes = new Vector<String>();
            assignedAttributes.addAll(0, _assignedAttributes);
        } else {
            assignedAttributes = new Vector<String>();
            // assignedAddresses = new Vector<String>();
        }

        initComponents();
        // valueChanged( null );
        pack();
    }

    // private void myInitComponents() {
    // removeButton.setEnabled( false );
    // upButton.setEnabled( false );
    // downButton.setEnabled( false );
    // if( mappableArchUnitsSL.size() > 0 ) {
    // mapButton.setEnabled( true );
    // }
    // else {
    // mapButton.setEnabled( false );
    // }
    // }

    private void initComponents() {
        Container contentPane = getContentPane();
        // GridBagLayout gridbag0 = new GridBagLayout();
        // GridBagLayout gridbag1 = new GridBagLayout();
        // GridBagLayout gridbag2 = new GridBagLayout();
        // GridBagLayout gridbag3 = new GridBagLayout();
        // GridBagLayout gridbag4 = new GridBagLayout();
        // GridBagLayout gridbag5 = new GridBagLayout();
        // GridBagLayout gridbag125 = new GridBagLayout();
        // GridBagConstraints c0 = new GridBagConstraints();
        // GridBagConstraints c1 = new GridBagConstraints();
        // GridBagConstraints c2 = new GridBagConstraints();
        // GridBagConstraints c3 = new GridBagConstraints();
        // GridBagConstraints c4 = new GridBagConstraints();
        // GridBagConstraints c5 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        contentPane.setLayout(new GridBagLayout());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        pnlComPatternStruct = new JPanel();
        pnlComPatternStruct.setLayout(new GridBagLayout());
        pnlComPatternStruct.setBorder(new TitledBorder("Communication Pattern Structure"));
        pnlComPatternStruct.setPreferredSize(new Dimension(325, 350));

        pnmManageStruct = new JPanel();
        pnmManageStruct.setLayout(new GridBagLayout());
        pnmManageStruct.setBorder(new TitledBorder("Managing Structure"));
        pnmManageStruct.setPreferredSize(new Dimension(325, 350));

        pnlCodeGen = new JPanel();
        pnlCodeGen.setLayout(new GridBagLayout());
        pnlCodeGen.setBorder(new TitledBorder("Code Generation"));
        // pnlCodeGen.setPreferredSize(new Dimension(200, 80));

        pnlRootContainerInstances = new JPanel();
        pnlRootContainerInstances.setPreferredSize(new Dimension(700, 1000));

        // Issue #36
        pnlRootContainerInstances.setLayout(new GridBagLayout());

        pnlAttributeValues = new JPanel();
        pnlAttributeValues.setLayout(new GridBagLayout());
        pnlAttributeValues.setBorder(new TitledBorder("Assign Value to Parameters"));
        // pnlComPatternValues.setPreferredSize(new Dimension(325, 300));

        pnlManageAttributes = new JPanel();
        pnlManageAttributes.setLayout(new GridBagLayout());
        pnlManageAttributes.setBorder(new TitledBorder("Managing Attributes"));

        // Issue #41 Ordering of tabbed panes
        tabbedPane = GraphicLib.createTabbedPane();// new JTabbedPane();

        // second line panel1
        final int defaultMargin = 3;
        final Insets lblInsets = new Insets(defaultMargin, defaultMargin, 0, defaultMargin);
        final Insets tfdInsets = new Insets(0, defaultMargin, defaultMargin, defaultMargin);

        final GridBagConstraints c1 = new GridBagConstraints();
        c1.gridwidth = GridBagConstraints.REMAINDER;
        c1.weighty = 0.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        c1.fill = GridBagConstraints.HORIZONTAL;
        // c1.anchor = GridBagConstraints.CENTER;
        c1.insets = lblInsets;

        // third line panel1
        pnlComPatternStruct.add(new JLabel("Name"), c1);
        // c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.insets = tfdInsets;
        nameOfCP = new JTextField(name);
        // nameOfCP.setPreferredSize( new Dimension(150, 30) );
        pnlComPatternStruct.add(nameOfCP, c1);

        // fourth line panel1
        // c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c1.fill = GridBagConstraints.BOTH;
        // c1.gridheight = 3;
        // pnlComPatternStruct.add(new JLabel(" "), c1); //adds some vertical space in
        // between two JLabels

        communicationPatternsSL = createListCPsNames(); // fill listCPs and return the string version of the list of all
                                                        // CPs
        /*
         * if( !emptyCPsList ) { indexListCPsNames = indexOf( cp.getReference() ); }
         */

        // fifth line panel1
        c1.insets = lblInsets;
        pnlComPatternStruct.add(new JLabel("Available Communication Patterns"), c1);

        communicationPatternsCB = new JComboBox<String>(communicationPatternsSL);

        if (!emptyListOfMappedUnits) {
            communicationPatternsCB.setSelectedItem(cp.getReference());
        } else {
            communicationPatternsCB.setSelectedIndex(0);
        }

        communicationPatternsCBActionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                freezeAllComboBoxes();
                freeMappedUnits();
                updateSDInstancesList();
                updateMappableArchUnits();
                unfreezeAllComboBoxes();
                manageMapButton();
            }
        };
        communicationPatternsCB.addActionListener(communicationPatternsCBActionListener);
        // communicationPatternsCB.setPreferredSize( new Dimension(150, 30) );
        c1.insets = tfdInsets;
        pnlComPatternStruct.add(communicationPatternsCB, c1);

        // sixth line panel1
        // c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c1.fill = GridBagConstraints.BOTH;
        // c1.gridheight = 3;
        // c1.insets = lblInsets;
        // pnlComPatternStruct.add(new JLabel(" "), c1);

        sdInstancesSL = new Vector<String>();
        // Create the array lists of HashSet listInstancesHash, sdControllerInstances,
        // sdStorageInstances and sdTransferInstances
        createListsOfInstances();

        if (sdInstancesSL.size() == 0) { // protect against the case of a CP with no SDs
            sdInstancesSL.add(EMPTY_INSTANCES_LIST);
        }

        // seventh line panel1
        // c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c1.fill = GridBagConstraints.BOTH;
        // c1.gridheight = 3;
        c1.insets = lblInsets;
        pnlComPatternStruct.add(new JLabel("Available Instances"), c1);

        sdInstancesCB = new JComboBox<String>(sdInstancesSL);
        sdInstancesCB.setSelectedIndex(0);
        sdInstancesCBActionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                freezeSDInstancesCB();
                updateMappableArchUnits();
                unfreezeSDInstancesCB();
                manageMapButton();
            }
        };

        sdInstancesCB.addActionListener(sdInstancesCBActionListener);
        // sdInstancesCB.setPreferredSize( new Dimension(150, 30) );
        c1.insets = tfdInsets;
        pnlComPatternStruct.add(sdInstancesCB, c1);

        // eigth line panel1
        // c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c1.fill = GridBagConstraints.BOTH;
        // c1.gridheight = 3;
        // pnlComPatternStruct.add(new JLabel(" "), c1);

        mappableArchUnitsSL = new Vector<String>(); // the string list used in the architecture units combo box

        checkValidityOfMappingInformation(); // checks the validity of both CP and mapped arch units

        makeListOfMappableArchUnitsSL();

        // nineth line panel1
        mappableArchUnitsJL = new JList<String>(mappableArchUnitsSL);
        mappableArchUnitsJL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mappableArchUnitsJL.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                manageMapButton();
            }
        });

        mappableArchUnitsSP = new JScrollPane(mappableArchUnitsJL);
        mappableArchUnitsSP.setSize(300, 400);

        c1.insets = lblInsets;
        pnlComPatternStruct.add(new JLabel("Available Platform Units"), c1);
        // c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        // c1.gridheight = 5;
        c1.weighty = 1.0;
        // c1.weightx = 10.0;
        c1.insets = tfdInsets;
        pnlComPatternStruct.add(mappableArchUnitsSP, c1);

        // tenth line panel1
        // c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c1.fill = GridBagConstraints.BOTH;
        // c1.gridheight = 3;
        // pnlComPatternStruct.add(new JLabel(" "), c1);

        // eleventh line panel1
        // c1.gridheight = 1;
        c1.weighty = 0.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.insets = lblInsets;
        mapButton = new JButton("Map");
        mapButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                freezeSDInstancesCB();
                mapInstance();
                unfreezeSDInstancesCB();
                sdInstancesCB.setSelectedIndex(0);
                updateMappableArchUnits();
                manageMapButton();
                manageManagingStructureButtons();
            }
        });
        pnlComPatternStruct.add(mapButton, c1);

        // 1st line panel2
        listMappedUnitsJL = new JList<String>(mappedUnitsSL);
        listMappedUnitsJL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listMappedUnitsJL.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                manageManagingStructureButtons();
            }
        });

        scrollPane = new JScrollPane(listMappedUnitsJL);
        scrollPane.setSize(300, 250);
        final GridBagConstraints c2 = new GridBagConstraints();
        c2.gridwidth = GridBagConstraints.REMAINDER; // end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.insets = new Insets(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        pnmManageStruct.add(scrollPane, c2);

        // 2nd line panel2
        c2.weighty = 0.0;
        // c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        // c2.gridheight = 1;
        // pnmManageStruct.add(new JLabel(""), c2);

        // third line panel2
        // c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c2.fill = GridBagConstraints.HORIZONTAL;
        upButton = new JButton("Up");
        upButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                upMappedInstance();
            }
        });
        pnmManageStruct.add(upButton, c2);

        downButton = new JButton("Down");
        downButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                downMappedInstance();
            }
        });
        pnmManageStruct.add(downButton, c2);

        removeButton = new JButton("Remove Unit");
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                freezeSDInstancesCB();
                removeMappedInstance();
                sdInstancesCB.setSelectedIndex(0);
                updateMappableArchUnits();
                unfreezeSDInstancesCB();
                manageMapButton();
            }
        });

        pnmManageStruct.add(removeButton, c2);

        // panel3
        // c3.weighty = 1.0;
        // c3.weightx = 1.0;
        // c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c3.fill = GridBagConstraints.BOTH;
        // c3.gridheight = 3;
        // pncComPatternValues.add( new JLabel(" "), c3 );

        // get the attributes from the selected CP
        createAttributesAndAddressVector();
        createApplicationAttributesVector();

        if (assignedAttributes.size() > 0) {
            filterOutAssignedAttributes(attributesVector); // eliminate the attributes that have already been assigned a
                                                           // value
        }

        final GridBagConstraints c3 = new GridBagConstraints();
        c3.gridwidth = GridBagConstraints.REMAINDER;
        c3.gridheight = 1;
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.anchor = GridBagConstraints.SOUTHWEST;
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.insets = lblInsets;
        pnlAttributeValues.add(new JLabel("Attribute"), c3);
        attributesList_CB = new JComboBox<String>(attributesVector);
        attributesList_CB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                manageRemoveAttributeButton();
            }
        });
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.weighty = 0.0;
        c3.insets = tfdInsets;
        pnlAttributeValues.add(attributesList_CB, c3);

        // c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        // pnlComPatternValues.add( new JLabel(" "), c3 );
        // c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        // pnlComPatternValues.add( new JLabel(" "), c3 );

        /*
         * panel3.add( new JLabel("Application attribute:"), c3 );
         * applicationAttributesList_CB = new JComboBox( applicationAttributesVector );
         * applicationAttributesList_CB.addActionListener(this); panel3.add(
         * applicationAttributesList_CB, c3 );
         */

        // c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c3.fill = GridBagConstraints.BOTH;
        // c3.gridheight = 3;
        // pnlComPatternValues.add( new JLabel(" "), c3 ); //adds some vertical space in
        // between two JLabels

        c3.insets = lblInsets;
        pnlAttributeValues.add(new JLabel("Attribute Value"), c3);
        attributesValue_TF = new JTextField("", 5);
        // attributesValue_TF.setPreferredSize( new Dimension(150, 30) );
        c3.insets = tfdInsets;
        pnlAttributeValues.add(attributesValue_TF, c3);

        // c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c3.fill = GridBagConstraints.BOTH;
        // c3.gridheight = 3;
        // pnlComPatternValues.add( new JLabel(" "), c3 ); //adds some vertical space in
        // between two JLabels

        c3.insets = lblInsets;
        c3.weighty = 1.0;
        attributeButton = new JButton("Assign Attribute Value");
        attributeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                assignValueToAttribute();
            }
        });

        pnlAttributeValues.add(attributeButton, c3);

        // c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c3.fill = GridBagConstraints.BOTH;
        // c3.gridheight = 3;
        // pnlComPatternValues.add( new JLabel(" "), c3 ); //adds some vertical space in
        // between two JLabels

        if (assignedAttributes.size() > 0) {
            filterOutAssignedAddresses(addressVector); // eliminate the addresses that have already been assigned a
                                                       // value
        }

        c3.insets = lblInsets;
        c3.weighty = 0.0;
        pnlAttributeValues.add(new JLabel("Address"), c3);
        addressList_CB = new JComboBox<String>(addressVector);
        addressList_CB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }
        });
        c3.insets = tfdInsets;
        pnlAttributeValues.add(addressList_CB, c3);

        // c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        // c3.fill = GridBagConstraints.BOTH;
        // c3.gridheight = 3;
        // pnlComPatternValues.add( new JLabel(" "), c3 );

        c3.insets = lblInsets;
        pnlAttributeValues.add(new JLabel("Address Value"), c3);
        addressValue_TF = new JTextField("", 5);
        c3.insets = tfdInsets;
        pnlAttributeValues.add(addressValue_TF, c3);

        addressButton = new JButton("Assign Address Value");
        addressButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                assignValueToAddress();
            }
        });

        c3.insets = lblInsets;
        c3.weighty = 1.0;
        pnlAttributeValues.add(addressButton, c3);

        scrollPaneAttributes_JL = new JList<String>(assignedAttributes);
        scrollPaneAttributes_JL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPaneAttributes_JL.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                manageRemoveAttributeButton();
            }
        });
        scrollPaneAttributes = new JScrollPane(scrollPaneAttributes_JL);
        scrollPaneAttributes.setSize(300, 250);

        final GridBagConstraints c4 = new GridBagConstraints();
        c4.gridwidth = GridBagConstraints.REMAINDER; // end row
        c4.fill = GridBagConstraints.BOTH;
        // c4.gridheight = 5;
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.insets = lblInsets;
        pnlManageAttributes.add(scrollPaneAttributes, c4);
        // c4.gridheight = 1;
        // pnlManageAttributes.add(new JLabel(""), c4);
        // third line panel2
        // c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.weighty = 0.0;
        c4.fill = GridBagConstraints.HORIZONTAL;
        removeAttributeButton = new JButton("Remove Attribute");
        removeAttributeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int indexToRemove = scrollPaneAttributes_JL.getSelectedIndex();
                final String attr = assignedAttributes.get(indexToRemove);
                final String attrType = attr.split(" ")[0]; // get the attribute type, differentiate between addr and
                                                            // int/bool

                if (attrType.equals(TMLType.ADDRESS_STRING)) {
                    removeAssignedAddress(indexToRemove, attr);
                } else {
                    removeAssignedAttribute(indexToRemove, attr);
                }

                scrollPaneAttributes_JL.setSelectedIndex(
                        indexToRemove >= assignedAttributes.size() ? assignedAttributes.size() - 1 : indexToRemove);
            }
        });

        pnlManageAttributes.add(removeAttributeButton, c4);

        final GridBagConstraints c5 = new GridBagConstraints();
        c5.gridwidth = 1;
        c5.gridheight = 1;
        c5.weighty = 1.0;
        c5.weightx = 1.0;
        c5.fill = GridBagConstraints.HORIZONTAL;
        c5.anchor = GridBagConstraints.LINE_START;
        c5.insets = lblInsets;
        pnlCodeGen.add(new JLabel("Extension Construct:"), c5);
        cpMECsCB = new JComboBox<String>(new Vector<String>(Arrays.asList(CPMEC.CP_TYPES)));

        if (cpMEC.equals("VOID") || cpMEC.equals("")) {
            cpMECsCB.setSelectedIndex(0);
            cpMEC = CPMEC.MEMORY_COPY;
        } else {
            cpMECsCB.setSelectedIndex(new Vector<String>(Arrays.asList(CPMEC.CP_TYPES)).indexOf(cpMEC));
        }

        cpMECsCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                enableDisableTransferTypeCBs();
            }
        });
        // cpMECsCB.setMinimumSize( new Dimension(150, 50) );
        c5.gridwidth = GridBagConstraints.REMAINDER;
        pnlCodeGen.add(cpMECsCB, c5);
        //
        // c5.gridwidth = GridBagConstraints.REMAINDER; //end row
        // pnlCodeGen.add(new JLabel(""), c5);
        c5.gridwidth = 1;
        // c5.gridheight = 1;
        // c5.weighty = 1.0;
        // c5.weightx = 1.0;
        // c5.fill = GridBagConstraints.HORIZONTAL;
        pnlCodeGen.add(new JLabel("Type of DMA Transfer n.1:"), c5);
        transferTypeCB1 = new JComboBox<String>(new Vector<String>(Arrays.asList(CPMEC.TRANSFER_TYPES)));

        if (transferType1 == -1) {
            transferTypeCB1.setSelectedIndex(0);
        } else {
            transferTypeCB1.setSelectedIndex(transferType1);
        }

        transferTypeCB1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }
        });

        // transferTypeCB1.setMinimumSize( new Dimension(150, 50) );
        c5.gridwidth = GridBagConstraints.REMAINDER;
        pnlCodeGen.add(transferTypeCB1, c5);
        //
        // c5.gridwidth = GridBagConstraints.REMAINDER; //end row
        // pnlCodeGen.add(new JLabel(""), c5);
        // c5.gridwidth = 1;
        // c5.gridheight = 1;
        // c5.weighty = 1.0;
        // c5.weightx = 1.0;
        // c5.fill = GridBagConstraints.HORIZONTAL;
        // c5.anchor = GridBagConstraints.LINE_START;
        c5.gridwidth = 1;
        pnlCodeGen.add(new JLabel("Type of DMA Transfer n.2:"), c5);
        transferTypeCB2 = new JComboBox<String>(new Vector<String>(Arrays.asList(CPMEC.TRANSFER_TYPES)));

        if (transferType2 == -1) {
            transferTypeCB2.setSelectedIndex(0);
        } else {
            transferTypeCB2.setSelectedIndex(transferType2);
        }

        transferTypeCB2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                manageMapButton();
            }
        });
        // transferTypeCB2.setMinimumSize( new Dimension(150, 50) );
        c5.gridwidth = GridBagConstraints.REMAINDER;
        pnlCodeGen.add(transferTypeCB2, c5);
        enableDisableTransferTypeCBs();

        // main panel;
        final GridBagConstraints c0 = new GridBagConstraints();
        c0.gridwidth = 1; // num columns
        c0.gridheight = 1; // num rows
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.fill = GridBagConstraints.BOTH;
        pnlRootContainerInstances.add(pnlComPatternStruct, c0);
        pnlRootContainerInstances.add(pnmManageStruct, c0);

        tabbedPane.addTab("Instances", pnlRootContainerInstances);

        pnlRootContainerAttributes = new JPanel();
        pnlRootContainerAttributes.setLayout(new GridBagLayout());
        pnlRootContainerAttributes.setPreferredSize(new Dimension(700, 1000));
        pnlRootContainerAttributes.add(pnlAttributeValues, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; // end row
        pnlRootContainerAttributes.add(pnlManageAttributes, c0);

        tabbedPane.addTab("Attributes", pnlRootContainerAttributes);
        tabbedPane.addTab("Code Generation", pnlCodeGen);
        tabbedPane.setSelectedIndex(0);
        contentPane.add(tabbedPane, c0);

        c0.gridwidth = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        c0.weighty = 0.0;
        c0.insets = new Insets(10, defaultMargin, 10, defaultMargin);
        closeButton = new JButton("Save and Close", IconManager.imgic25);
        // closeButton.setPreferredSize(new Dimension(200, 50));
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });

        contentPane.add(closeButton, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; // end row
        cancelButton = new JButton("Cancel", IconManager.imgic27);
        // cancelButton.setPreferredSize(new Dimension(200, 50));
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cancelDialog();
            }
        });

        contentPane.add(cancelButton, c0);

        manageManagingStructureButtons();
        manageMapButton();
        manageRemoveAttributeButton();
    }

    private void makeListOfMappableArchUnitsSL() {
        int j = getIndexOfSelectedCP();

        if (!sdInstancesSL.get(0).equals(EMPTY_INSTANCES_LIST)) {
            if (sdStorageInstances.get(j).contains(sdInstancesSL.get(0))) {
                mappableArchUnitsSL = makeListOfMappableArchUnits(STORAGE);
            } else {
                if (sdTransferInstances.get(j).contains(sdInstancesSL.get(0))) {
                    mappableArchUnitsSL = makeListOfMappableArchUnits(TRANSFER);
                } else {
                    if (sdControllerInstances.get(j).contains(sdInstancesSL.get(0))) {
                        mappableArchUnitsSL = makeListOfMappableArchUnits(CONTROLLER);
                    }
                }
            }
        }

        if (mappableArchUnitsSL.size() == 0) {
            mappableArchUnitsSL.add(EMPTY_MAPPABLE_ARCH_UNITS_LIST);
        }
    }

    private void checkValidityOfMappingInformation() {

        java.util.List<String> mappingStringSplitted; // Will contain: info[0] = CPName, info[1] = instanceName, info[2]
                                                      // =
                                                      // archUnitName
        boolean removedCP = false;
        boolean removedInstance = false;

        Iterator<String> it = mappedUnitsSL.iterator();

        while (it.hasNext()) {
            mappingStringSplitted = splitMappingString(it.next());
            String CPname = mappingStringSplitted.get(0);
            String instanceName = mappingStringSplitted.get(1);

            // first check that the mapped CP is still part of the current design
            if (!doesCPexist(CPname)) {
                it.remove();
                removedCP = true;
            } else { // the CP exists, then check the single instances: if the instance exists,
                     // remove it from listInstancesHash and add it to the list of mapped instances
                if (!checkAndRemoveIfInstanceExists(CPname, instanceName)) {
                    it.remove();
                    removedInstance = true;
                }
            }

            // then check if the mapped units have not been changed
            if (!removedCP && !removedInstance) {
                for (int i = 2; i < mappingStringSplitted.size(); i++) {
                    TraceManager.addDev("Testing Architecture Units for String: " + mappingStringSplitted.toString());
                    if (!doesArchUnitExist(mappingStringSplitted.get(i))) {
                        TraceManager.addDev(mappingStringSplitted.get(i) + " does not exist and will be removed");
                        it.remove();
                        restoreInstanceName(CPname, instanceName); // release the mapped instance in listInstancesHash
                    }
                }
            }

            removedCP = false;
            removedInstance = false;
        }
    }

    private java.util.List<String> splitMappingString(String s) {

        java.util.List<String> info = new ArrayList<String>();
        String[] firstPart = s.split(" : ");
        String[] secondPart = firstPart[0].split("\\.");
        String[] otherUnits = firstPart[1].split("\\, ");

        if (otherUnits.length > 1) { // a transfer instance mapped on more than one arch unit
            info.add(secondPart[0]);
            info.add(secondPart[1]);

            for (String st : otherUnits) {
                info.add(st); // { CPName, instanceName, archUnitNameS };
            }

            // return info;
        } else {
            info.add(secondPart[0]);
            info.add(secondPart[1]);
            info.add(firstPart[1]); // { CPName, instanceName, archUnitName };
        }

        return info;
    }

    private void restoreInstanceName(String CPName, String instanceName) {
        for (int i = 0; i < listCPs.size(); i++) {
            if (listCPs.get(i).getName().equals(CPName)) {
                Set<String> tempHash = listInstancesHash.get(i);
                tempHash.add(instanceName);
                listInstancesHash.set(i, tempHash);
                freezeSDInstancesCB();
                makeSDInstancesComboBox(new Vector<String>(tempHash));
                unfreezeSDInstancesCB();
                return;
            }
        }
    }

    private boolean doesCPexist(String CPName) {

        for (String s : communicationPatternsSL) {
            if (s.equals(CPName)) {
                // TraceManager.addDev( "CPName: " + CPName + " exists" );
                return true;
            }
        }
        return false;
    }

    private boolean checkAndRemoveIfInstanceExists(String CPname, String instanceName) {
        for (int i = 0; i < listCPs.size(); i++) {
            if (listCPs.get(i).getName().equals(CPname)) {
                Set<String> tempHash = listInstancesHash.get(i);

                if (tempHash.contains(instanceName)) {
                    tempHash.remove(instanceName);
                    listInstancesHash.set(i, tempHash);
                    freezeSDInstancesCB();

                    if (tempHash.size() == 0) {
                        tempHash.add(EMPTY_INSTANCES_LIST);
                    }

                    makeSDInstancesComboBox(new Vector<String>(tempHash));
                    unfreezeSDInstancesCB();
                    Set<String> oldListOfMappedInstances = listOfMappedInstances.get(i);
                    oldListOfMappedInstances.remove("VOID");
                    oldListOfMappedInstances.add(instanceName);
                    listOfMappedInstances.set(i, oldListOfMappedInstances);

                    return true;
                }
            }
        }

        return false;
    }

    private boolean doesArchUnitExist(String archUnitName) {

        if (makeListOfMappableArchUnits(STORAGE).contains(archUnitName)) {
            // TraceManager.addDev( "ArchUnit: " + archUnitName + " exists" );
            return true;
        }
        if (makeListOfMappableArchUnits(CONTROLLER).contains(archUnitName)) {
            // TraceManager.addDev( "ArchUnit: " + archUnitName + " exists" );
            return true;
        }
        return makeListOfMappableArchUnits(TRANSFER).contains(archUnitName);
    }
    //
    // @Override
    // public void actionPerformed( ActionEvent evt ) {
    //
    // //String command = evt.getActionCommand();
    // String attr, attrType;
    //
    // // Compare the action command to the known actions.
    // if( evt.getSource() == attributeButton ) {
    // assignValueToAttribute();
    // }
    // if( evt.getSource() == addressButton ) {
    // assignValueToAddress();
    // }
    // if( evt.getSource() == removeAttributeButton ) {
    // int indexToRemove = scrollPaneAttributes_JL.getSelectedIndex();
    // attr = assignedAttributes.get( indexToRemove );
    // attrType = attr.split(" ")[0]; //get the attribute type, differentiate
    // between addr and int/bool
    // if( attrType.equals( TMLType.ADDRESS_STRING ) ) {
    // removeAssignedAddress( indexToRemove, attr );
    // }
    // else {
    // removeAssignedAttribute( indexToRemove, attr );
    // }
    // }
    //
    // if( evt.getSource() == closeButton ) {
    // closeDialog();
    // }
    // else if( evt.getSource() == cancelButton ) {
    // cancelDialog();
    // }
    // else if( evt.getSource() == downButton ) {
    // downMappedInstance();
    // }
    // else if( evt.getSource() == upButton ) {
    // upMappedInstance();
    // }
    // else if( evt.getSource() == mapButton ) {
    // freezeSDInstancesCB();
    // mapInstance();
    // unfreezeSDInstancesCB();
    // sdInstancesCB.setSelectedIndex(0);
    // updateMappableArchUnits();
    // valueChanged( null );
    // manageManagingStructureButtons();
    // }
    // else if( evt.getSource() == removeButton ) {
    // freezeSDInstancesCB();
    // removeMappedInstance();
    // sdInstancesCB.setSelectedIndex(0);
    // updateMappableArchUnits();
    // unfreezeSDInstancesCB();
    // valueChanged( null );
    // }
    // else if( evt.getSource() == sdInstancesCB ) { //user has selected another
    // instance
    // freezeSDInstancesCB();
    // updateMappableArchUnits();
    // unfreezeSDInstancesCB();
    // valueChanged( null );
    // }
    // else if( evt.getSource() == communicationPatternsCB ) { //user has selected
    // another CP. Previous mapping will be deleted
    // freezeAllComboBoxes();
    // freeMappedUnits();
    // updateSDInstancesList();
    // updateMappableArchUnits();
    // unfreezeAllComboBoxes();
    // valueChanged( null );
    // }
    // else if( evt.getSource() == scrollPane ) {
    // manageManagingStructureButtons();
    // }
    // else if( evt.getSource() == cpMECsCB ) {
    // enableDisableTransferTypeCBs();
    // }
    // } //End of method

    private void enableDisableTransferTypeCBs() {
        if (cpMECsCB.getSelectedIndex() == 0) { // selected memoryCopy
            transferTypeCB1.setEnabled(false);
            transferTypeCB2.setEnabled(false);
            transferType1 = 0;
            transferType2 = 0;
        } else if (cpMECsCB.getSelectedIndex() == 1) { // selected SingleDma
            transferTypeCB1.setEnabled(true);
            transferType1 = 0;
            transferTypeCB2.setEnabled(false);
            transferType2 = 0;
        } else if (cpMECsCB.getSelectedIndex() == 2) { // selected DoubleDma
            transferTypeCB1.setEnabled(true);
            transferType1 = 0;
            transferTypeCB2.setEnabled(true);
            transferType2 = 0;
        }
    }

    private void mapInstance() {
        String instanceToMap = sdInstancesCB.getSelectedItem().toString();

        int j = getIndexOfSelectedCP();

        if (listInstancesHash.get(communicationPatternsCB.getSelectedIndex()).size() > 0) {
            int[] indices = mappableArchUnitsJL.getSelectedIndices();

            if (indices.length > 1) { // selecting more than one unit/instance
                if (sdTransferInstances.get(j).contains(instanceToMap)) {
                    StringBuffer sb = new StringBuffer(
                            communicationPatternsCB.getSelectedItem().toString() + "." + instanceToMap + " : ");
                    for (int i = 0; i < indices.length; i++) {
                        sb.append(mappableArchUnitsSL.get(indices[i]) + ", ");
                    }

                    mappedUnitsSL.add(sb.toString().substring(0, sb.length() - 2));
                } else { // only transfer instances can be mapped on more than one architecture unit
                    JOptionPane.showMessageDialog(frame, "More than one architecture unit selected for mapping",
                            "Error", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            } else { // selecting only one unit/instance
                mappedUnitsSL.add(communicationPatternsCB.getSelectedItem().toString() + "." + instanceToMap + " : "
                        + mappableArchUnitsSL.get(mappableArchUnitsJL.getSelectedIndex()));
            }

            // add the mapped instance to the list of mapped instances
            Set<String> oldListOfMappedInstances;
            oldListOfMappedInstances = listOfMappedInstances.get(j);
            oldListOfMappedInstances.remove("VOID");
            oldListOfMappedInstances.add(instanceToMap);
            listOfMappedInstances.set(j, oldListOfMappedInstances);

            // remove the mapped instance from the list of available instances
            Set<String> SDinstancesHash = listInstancesHash.get(j);
            Iterator<String> i = SDinstancesHash.iterator();

            while (i.hasNext()) {
                String element = i.next();
                // TraceManager.addDev( "Comparing " + element + " with " +
                // sdInstancesCB.getSelectedItem().toString() );
                if (element.equals(instanceToMap)) {
                    i.remove();
                    // TraceManager.addDev( "Removing instance: " + element );
                    break;
                }
            }

            listMappedUnitsJL.setListData(mappedUnitsSL);
            // removeButton.setEnabled( true );
            if (SDinstancesHash.size() == 0) { // if the last instance has just being mapped
                // mapButton.setEnabled( false );
                sdInstancesSL.removeAllElements();
                sdInstancesSL.add(EMPTY_INSTANCES_LIST);
                freezeAllComboBoxes();
                makeSDInstancesComboBox(sdInstancesSL);
                mappableArchUnitsSL.removeAllElements();
                mappableArchUnitsSL.add(EMPTY_MAPPABLE_ARCH_UNITS_LIST);
                makeArchitectureUnitsScrollPane(mappableArchUnitsSL);
                unfreezeAllComboBoxes();
                TraceManager.addDev("The DS after removing instance: " + SDinstancesHash.toString());
                listInstancesHash.set(j, SDinstancesHash);
                // TraceManager.addDev("Nex list done");
            } else { // update the list with the removed element
                sdInstancesSL = new Vector<String>(SDinstancesHash);
                listInstancesHash.set(j, SDinstancesHash);
                freezeSDInstancesCB();
                makeSDInstancesComboBox(sdInstancesSL);
                unfreezeSDInstancesCB();
            }
        }
    }

    private void removeMappedInstance() {

        String /* archUnitName, */ CPName, instanceName;
        final int selIndex = listMappedUnitsJL.getSelectedIndex();

        if (selIndex >= 0) {
            java.util.List<String> info = splitMappingString(mappedUnitsSL.get(selIndex));
            mappedUnitsSL.removeElementAt(selIndex);
            CPName = info.get(0);
            instanceName = info.get(1);
            int indexCP;
            for (indexCP = 0; indexCP < listCPs.size(); indexCP++) {
                if (listCPs.get(indexCP).getName().equals(CPName)) {
                    break;
                }
            }

            Set<String> oldListOfMappedInstances = listOfMappedInstances.get(indexCP);
            oldListOfMappedInstances.remove(instanceName);
            listOfMappedInstances.set(indexCP, oldListOfMappedInstances);
            // TraceManager.addDev( "The DS of mapped instances: " +
            // oldListOfMappedInstances.toString() );

            Set<String> oldList = listInstancesHash.get(indexCP); // it is the list of all instances for a given CP
            // TraceManager.addDev( "Adding " + instanceName + " to oldList: " +
            // oldList.toString() );
            oldList.add(instanceName);
            listInstancesHash.set(indexCP, oldList);
            // TraceManager.addDev( "sdInstancesL: " + sdInstancesSL.toString() );
            sdInstancesSL = new Vector<String>(oldList);
            makeSDInstancesComboBox(sdInstancesSL);
            listMappedUnitsJL.setListData(mappedUnitsSL);

            if (mappedUnitsSL.isEmpty()) {
                removeButton.setEnabled(false);
            }

            listMappedUnitsJL.setSelectedIndex(selIndex >= mappedUnitsSL.size() ? mappedUnitsSL.size() - 1 : selIndex);
        }
    }

    private void downMappedInstance() {
        int index = listMappedUnitsJL.getSelectedIndex();

        if (index < mappedUnitsSL.size() - 1) {
            final int newIndex = index + 1;
            Collections.swap(mappedUnitsSL, index, newIndex);
            listMappedUnitsJL.setListData(mappedUnitsSL);
            listMappedUnitsJL.setSelectedIndex(newIndex);
        }
    }

    private void upMappedInstance() {
        int index = listMappedUnitsJL.getSelectedIndex();

        if (index > 0) {
            final int newIndex = index - 1;
            Collections.swap(mappedUnitsSL, index, newIndex);
            listMappedUnitsJL.setListData(mappedUnitsSL);
            listMappedUnitsJL.setSelectedIndex(newIndex);
        }
    }

    private void updateSDInstancesList() {

        if (listInstancesHash.get(communicationPatternsCB.getSelectedIndex()).size() > 0) {
            makeSDInstancesComboBox(
                    new Vector<String>(listInstancesHash.get(communicationPatternsCB.getSelectedIndex())));
        } else {
            Vector<String> emptyList = new Vector<String>();
            emptyList.add(EMPTY_INSTANCES_LIST);
            makeSDInstancesComboBox(emptyList);
        }
    }

    // Updates mappableArchUnitsSL and the comboBox of the architecture units scroll
    // pane
    private void updateMappableArchUnits() {

        String selectedInstance = "";
        if (sdInstancesCB.getSelectedItem() != null) {
            selectedInstance = sdInstancesCB.getSelectedItem().toString();
        } else {
            selectedInstance = sdInstancesSL.get(0);
        }
        // TraceManager.addDev( "Selected instance: " + selectedInstance );

        // get the CP index
        int j = getIndexOfSelectedCP();

        if (sdStorageInstances.get(j).contains(selectedInstance)) {
            mappableArchUnitsSL = makeListOfMappableArchUnits(STORAGE);
            // TraceManager.addDev( "Found a storage instance: " +
            // mappableArchUnitsSL.toString() );
        } else {
            if (sdTransferInstances.get(j).contains(selectedInstance)) {
                mappableArchUnitsSL = makeListOfMappableArchUnits(TRANSFER);
                // TraceManager.addDev( "Found a transfer instance: " +
                // mappableArchUnitsSL.toString() );
            } else {
                if (sdControllerInstances.get(j).contains(selectedInstance)) {
                    mappableArchUnitsSL = makeListOfMappableArchUnits(CONTROLLER);
                    // TraceManager.addDev( "Found a controller instance: " +
                    // mappableArchUnitsSL.toString() );
                } else { // is there is no instance to map
                    mappableArchUnitsSL = new Vector<String>();
                    mappableArchUnitsSL.add(EMPTY_MAPPABLE_ARCH_UNITS_LIST);
                    // TraceManager.addDev( "Found OTHER instance: " +
                    // mappableArchUnitsSL.toString() );
                }
            }
        }

        makeArchitectureUnitsScrollPane(mappableArchUnitsSL);
    }

    // Returns the index of the selected CP in the combo box, otherwise returns -1
    private int getIndexOfSelectedCP() {

        if (listCPs.size() > 0) {
            for (int j = 0; j < listCPs.size(); j++) {
                if (listCPs.get(j).getName().equals(communicationPatternsCB.getSelectedItem())) {
                    return j;
                }
            }
        }
        return -1;
    }

    private void freeMappedUnits() {

        // before eliminating the list of mapped units, put the instances back in the
        // general data structure
        for (int i = 0; i < mappedUnitsSL.size(); i++) {
            java.util.List<String> info = splitMappingString(mappedUnitsSL.get(i));
            restoreInstanceName(info.get(0), info.get(1));
        }
        mappedUnitsSL.clear();
        listMappedUnitsJL.setListData(mappedUnitsSL);
    }

    private void makeArchitectureUnitsScrollPane(Vector<String> newList) {

        mappableArchUnitsSL = new Vector<String>(newList);
        mappableArchUnitsJL.setListData(mappableArchUnitsSL);
    }

    private void makeSDInstancesComboBox(Vector<String> newList) {

        if ((newList.size() > 1) && (newList.contains(EMPTY_INSTANCES_LIST))) {
            newList.removeElementAt(newList.indexOf(EMPTY_INSTANCES_LIST));
        }
        sdInstancesCB.removeAllItems();
        for (String s : newList) {
            sdInstancesCB.addItem(s);
        }
        sdInstancesSL = new Vector<String>(newList);
    }

    private void assignValueToAttribute() {

        String attrValue = attributesValue_TF.getText();
        if (attrValue.length() > 0) {
            String natRegex = "[0-9]+";
            String boolRegex = "true|TRUE|false|FALSE";
            String attrType = ((String) attributesList_CB.getSelectedItem()).split(" ")[0];
            if (attrType.equals("int")) {
                if (!attrValue.matches(natRegex)) {
                    JOptionPane.showMessageDialog(frame, "Attribute must be of type Natural",
                            "Badly formatted parameter", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            if (attrType.equals("bool")) {
                if (!attrValue.matches(boolRegex)) {
                    JOptionPane.showMessageDialog(frame, "Attribute is of type boolean", "Badly formatted parameter",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            String attrName = ((String) attributesList_CB.getSelectedItem()).split(" ")[1];
            int indexToDelete = attributesVector.indexOf(attrType + " " + attrName);
            if (indexToDelete != -1) {
                String assignement = attrType + " " + attrName + " = " + attrValue + ";";
                assignedAttributes.add(assignement);

                // update JComboBox
                Vector<String> newList = new Vector<String>(attributesVector);
                newList.remove(indexToDelete);
                attributesList_CB.removeAllItems();
                for (String s : newList) {
                    attributesList_CB.addItem(s);
                }
                attributesVector = new Vector<String>(newList);

                // clear text
                attributesValue_TF.setText("");

                // update scrollPaneAttributes
                scrollPaneAttributes_JL.setListData(assignedAttributes);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter a value to the selected attribute",
                    "No value for attribute", JOptionPane.INFORMATION_MESSAGE);

            return;
        }
    }

    private void assignValueToAddress() {

        String natRegex = "[0-9]+";
        String addrValue = addressValue_TF.getText();
        Vector<String> assignedAddresses = new Vector<String>();

        if (addrValue.length() <= 2 && addrValue.length() > 0) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid base address", "Badly formatted parameter",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (addrValue.length() > 2) {
            if (!(addrValue.substring(0, 2).equals("0x") || addrValue.substring(0, 2).equals("0X"))
                    || !(addrValue.substring(2, addrValue.length()).matches(natRegex))) {
                JOptionPane.showMessageDialog(frame, "Base address must be expressed in hexadecimal",
                        "Badly formatted parameter", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        String addrName = ((String) addressList_CB.getSelectedItem()).split(" ")[1];
        int indexToDelete = addressVector.indexOf("addr " + addrName);
        if (indexToDelete != -1) {
            String assignement = "addr " + addrName + " = " + addrValue + ";";
            assignedAddresses.add(assignement);

            // update JComboBox
            Vector<String> newList = new Vector<String>(addressVector);
            newList.remove(indexToDelete);
            addressList_CB.removeAllItems();
            for (String s : newList) {
                addressList_CB.addItem(s);
            }
            addressVector = new Vector<String>(newList);

            // clear text
            addressValue_TF.setText("");

            // update scrollPaneAttributes
            assignedAttributes.addAll(assignedAddresses);
            scrollPaneAttributes_JL.setListData(assignedAttributes);
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter a value for the selected address",
                    "No value for address", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    private void removeAssignedAttribute(int indexToRemove, String attr) {

        if (assignedAttributes.size() > 0) {
            assignedAttributes.remove(indexToRemove);
            scrollPaneAttributes_JL.setListData(assignedAttributes);

            // attribute must be put back in list of attributes to be mapped...
            String s = attr.split(" = ")[0];
            Vector<String> newList = new Vector<String>(attributesVector);
            newList.add(s);

            attributesList_CB.removeAllItems();
            for (String st : newList) {
                attributesList_CB.addItem(st);
            }
            attributesVector = new Vector<String>(newList);
        }
    }

    private void removeAssignedAddress(int indexToRemove, String attr) {

        if (assignedAttributes.size() > 0) {
            // first remove the address from the list of attributes
            assignedAttributes.remove(indexToRemove);
            scrollPaneAttributes_JL.setListData(assignedAttributes);

            // address must be put back in list of addresses to be mapped
            String s = attr.split(" = ")[0];
            Vector<String> newList = new Vector<String>(addressVector);
            newList.add(s);

            addressList_CB.removeAllItems();
            for (String st : newList) {
                addressList_CB.addItem(st);
            }
            addressVector = new Vector<String>(newList);
        }
    }

    public void closeDialog() {
        // regularClose = true;
        cancelled = false;
        name = nameOfCP.getText();
        cpMEC = (String) cpMECsCB.getSelectedItem();
        if (cpMEC.equals("VOID") || cpMEC.equals("")) {
            cpMEC = CPMEC.MEMORY_COPY;
        }
        transferType1 = Arrays.asList(CPMEC.TRANSFER_TYPES).indexOf(transferTypeCB1.getSelectedItem());
        transferType2 = Arrays.asList(CPMEC.TRANSFER_TYPES).indexOf(transferTypeCB2.getSelectedItem());

        dispose();
    }

    public void cancelDialog() {
        dispose();
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    private void manageMapButton() { // this methos is abstract and must be implemented

        // Enable or disable the mapping button. Do not use &&, || as they are
        // short-circuit operators
        if (listCPs.size() > 0) {
            if (sdInstancesSL.size() > 0) {
                if (!sdInstancesSL.get(0).equals(EMPTY_INSTANCES_LIST)) {
                    if (mappableArchUnitsSL.size() > 0) {
                        if (!mappableArchUnitsJL.isSelectionEmpty()
                                && !mappableArchUnitsSL.get(0).equals(EMPTY_MAPPABLE_ARCH_UNITS_LIST)) {
                            mapButton.setEnabled(true);
                        } else {
                            mapButton.setEnabled(false);
                        }
                    } else {
                        mapButton.setEnabled(false);
                    }
                } else {
                    mapButton.setEnabled(false);
                }
            } else {
                mapButton.setEnabled(false);
            }
        } else {
            mapButton.setEnabled(false);
        }
    }

    private void manageRemoveAttributeButton() {
        removeAttributeButton.setEnabled(!scrollPaneAttributes_JL.isSelectionEmpty());
    }

    private void manageManagingStructureButtons() {
        int selIndex = listMappedUnitsJL.getSelectedIndex();

        if (selIndex == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        } else {
            removeButton.setEnabled(true);

            if (selIndex == 0) { // the first element
                upButton.setEnabled(false);
                downButton.setEnabled(mappedUnitsSL.size() > 1);
            } else if (selIndex == mappedUnitsSL.size() - 1) { // the last element
                upButton.setEnabled(true);
                downButton.setEnabled(false);
            } else { // the remaining cases
                upButton.setEnabled(true);
                downButton.setEnabled(true);
            }
        }
    }

    public String getNodeName() {
        return name;
    }

    public String getCPReference() {
        if (emptyCPsList) {
            return "";
        }
        return (String) (communicationPatternsCB.getSelectedItem());
    }
    //
    // public boolean isRegularClose() {
    // return regularClose;
    // }

    public Vector<String> getMappedUnits() {
        return mappedUnitsSL;
    }

    public int indexOf(String name) {

        int i = 0;
        if (communicationPatternsSL.size() > 0) {
            for (String s : communicationPatternsSL) {
                if (s.equals(name)) {
                    return i;
                }
                i++;
            }
        }
        return 0;
    }

    private Vector<String> createListCPsNames() {

        Vector<String> list = new Vector<String>();
        listCPs = cp.getTDiagramPanel().getMGUI().getAllTMLCP();
        if (listCPs.size() > 0) {
            for (int i = 0; i < listCPs.size(); i++) {
                list.add(listCPs.get(i).getName());
            }
            emptyCPsList = false;
        } else {
            list.add(EMPTY_CPS_LIST);
            emptyCPsList = true;
        }
        return list;
    }

    // Create the array lists of HashSet listInstancesHash, sdControllerInstances,
    // sdStorageInstances and sdTransferInstances
    private void createListsOfInstances() {

        HashSet<String> sdInstancesNames = new HashSet<String>();
        HashSet<String> sdControllerInstances_local = new HashSet<String>();
        HashSet<String> sdStorageInstances_local = new HashSet<String>();
        HashSet<String> sdTransferInstances_local = new HashSet<String>();
        HashSet<String> mappedSDInstances_local = new HashSet<String>(); // just to initialize the data structure

        // j indexes the CP and k indexes the components within a TMLSDPanel
        if (listCPs.size() > 0) {
            for (int j = 0; j < listCPs.size(); j++) {
                Vector<TDiagramPanel> panelList = listCPs.get(j).getPanels(); // the list of AD and SD panels for a
                                                                              // given CP
                for (TDiagramPanel panel : panelList) {
                    // TraceManager.addDev( "Into createListInstances, panel name: " +
                    // panel.getName() );
                    if (panel instanceof TMLSDPanel) {
                        // TraceManager.addDev( "Found TMLSDPanel named: " + panel.getName() );
                        java.util.List<TGComponent> componentsList = panel.getComponentList();
                        for (int k = 0; k < componentsList.size(); k++) {
                            TGComponent elem = componentsList.get(k);
                            if (elem instanceof ui.tmlsd.TMLSDInstance) {
                                sdInstancesNames.add(elem.getName());
                                if (elem instanceof TMLSDStorageInstance) {
                                    sdStorageInstances_local.add(elem.getName());
                                }
                                if (elem instanceof TMLSDTransferInstance) {
                                    sdTransferInstances_local.add(elem.getName());
                                }
                                if (elem instanceof TMLSDControllerInstance) {
                                    sdControllerInstances_local.add(elem.getName());
                                }
                            }
                        } /* end of for over k */
                    }
                } /* end of examining all diagrams for a CP */
                for (String s : sdInstancesNames) {
                    if (listCPs.get(j).getName().equals(communicationPatternsCB.getSelectedItem())) {
                        TraceManager.addDev("Found a TMLSDInstance named: " + s);
                        if (!isInstanceMapped(s)) {
                            sdInstancesSL.add(s); // the string list displayed in the combo box
                            TraceManager.addDev("Instance " + s + " is un-mapped. Adding to SL list");
                        }
                    }
                }
                listInstancesHash.add(j, sdInstancesNames); // for each CP the list of instances
                sdStorageInstances.add(j, sdStorageInstances_local); // for each CP the list of storage instances
                sdTransferInstances.add(j, sdTransferInstances_local); // for each CP the list of controller instances
                sdControllerInstances.add(j, sdControllerInstances_local); // for each CP the list of transfer instances
                mappedSDInstances_local.add("VOID");
                listOfMappedInstances.add(j, mappedSDInstances_local); // just to initialize the data structure
                TraceManager.addDev("CP name: " + listCPs.get(j).getName());
                TraceManager.addDev("List of storage instances: " + sdStorageInstances.get(j).toString());
                TraceManager.addDev("List of transfer instances: " + sdTransferInstances.get(j).toString());
                TraceManager.addDev("List of controller instances: " + sdControllerInstances.get(j).toString());
                sdInstancesNames = new HashSet<String>(); // better than using clear method
                sdStorageInstances_local = new HashSet<String>();
                sdTransferInstances_local = new HashSet<String>();
                sdControllerInstances_local = new HashSet<String>();
                mappedSDInstances_local = new HashSet<String>();
            }
        }
    }

    private boolean isInstanceMapped(String instanceName) {

        java.util.List<String> info;
        for (String st : mappedUnitsSL) {
            info = splitMappingString(st);
            if (info.get(1).equals(instanceName)) {
                TraceManager.addDev("Instance " + info.get(1) + " is mapped");
                return true;
            }
        }
        return false;
    }

    private Vector<String> makeListOfMappableArchUnits(int instanceType) {

        // 0 = storage, 1 = transfer, 2 = controller
        java.util.List<TGComponent> componentList = cp.getTDiagramPanel().getComponentList();
        Vector<String> list = new Vector<String>();

        for (int k = 0; k < componentList.size(); k++) {
            if (componentList.get(k) instanceof TMLArchiNode) {
                if (((TMLArchiNode) componentList.get(k)).getComponentType() == instanceType) {
                    list.add(componentList.get(k).getName());
                }
            }
        }

        return list;
    }

    private void freezeSDInstancesCB() {
        sdInstancesCB.removeActionListener(sdInstancesCBActionListener);
    }

    private void unfreezeSDInstancesCB() {
        sdInstancesCB.addActionListener(sdInstancesCBActionListener);
    }

    private void freezeAllComboBoxes() {
        sdInstancesCB.removeActionListener(sdInstancesCBActionListener);
        communicationPatternsCB.removeActionListener(communicationPatternsCBActionListener);
    }

    private void unfreezeAllComboBoxes() {
        sdInstancesCB.addActionListener(sdInstancesCBActionListener);
        communicationPatternsCB.addActionListener(communicationPatternsCBActionListener);
    }

    public String getCPMEC() {
        return cpMEC;
    }

    private void filterOutAssignedAttributes(Vector<String> attributesVector) {

        // ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (String s : assignedAttributes) {
            String token = s.split(" = ")[0];
            for (Iterator<String> iterator = attributesVector.iterator(); iterator.hasNext();) {
                String s1 = iterator.next();
                if (token.equals(s1)) {
                    iterator.remove();
                }
            }
        }
    }

    private void filterOutAssignedAddresses(Vector<String> addressVector) {

        // ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (String s : assignedAttributes) {
            String token = s.split(" = ")[0];
            for (Iterator<String> iterator = addressVector.iterator(); iterator.hasNext();) {
                String s1 = iterator.next();
                if (token.equals(s1)) {
                    iterator.remove();
                }
            }
        }
    }

    public Vector<String> getAssignedAttributes() {
        return assignedAttributes;
    }

    private void createAttributesAndAddressVector() {

        // String selectedCPName = (String)communicationPatternsCB.getSelectedItem();
        int index = getIndexOfSelectedCP(); // returns -1 upon error

        TraceManager.addDev("The selected CP has index: " + index);
        if (index >= 0) {
            java.util.List<TMLCP> tmlcpsList = new ArrayList<TMLCP>();
            for (TMLCommunicationPatternPanel panel : listCPs) {
                GTMLModeling gtmlm = new GTMLModeling(panel, true);
                TMLCP tmlcp = gtmlm.translateToTMLCPDataStructure(panel.getName());
                tmlcpsList.add(tmlcp);
            }

            Set<TMLAttribute> attributesHS = new HashSet<TMLAttribute>();
            Set<TMLAttribute> addressHS = new HashSet<TMLAttribute>();
            attributesVector = new Vector<String>();
            addressVector = new Vector<String>();
            // get the attributes of all SDs
            for (TMLCPSequenceDiagram sd : tmlcpsList.get(index).getCPSequenceDiagrams()) {
                for (TMLAttribute attr : sd.getAttributes()) {
                    if (attr.isNat() || attr.isBool()) {
                        attributesHS.add(attr);
                    }
                    if (attr.isAddress()) {
                        addressHS.add(attr);
                    }
                }
            }
            for (TMLAttribute attr : attributesHS) {
                attributesVector.add(attr.getType() + " " + attr.getName());
            }
            for (TMLAttribute attr : addressHS) {
                addressVector.add(attr.getType() + " " + attr.getName());
            }
        } else { // Data structures attributesVector and addressVector are not initialized
            attributesVector = new Vector<String>();
            attributesVector.add("No attribute found");
            addressVector = new Vector<String>();
            addressVector.add("No address found");
        }
    }

    private void createApplicationAttributesVector() {
        applicationAttributesVector = new Vector<String>();
        // I have to get all the attributes of all tasks in the application model AND
        // their values
        Vector<String> listAttributes = cp.getTDiagramPanel().getMGUI().getAllApplicationTMLTasksAttributes();

        for (String s : listAttributes) {
            // String s = o.toString();
            TraceManager.addDev("Attribute *" + s + "*");
            String attrName = s.split(" ")[0];
            String attrType = s.split(" : ")[1];

            if (attrType.contains("Natural")) {
                attrType = TMLType.NATURAL_STRING;
            } else if (attrType.contains("Bool")) {
                attrType = TMLType.BOOLEAN_STRING;
            }

            if (s.contains("=")) {
                applicationAttributesVector.add(attrType + " " + attrName + " : " + s.split(" ")[2]);
            } else {
                applicationAttributesVector.add(attrType + " " + attrName);
            }
        }
    }

    public java.util.List<Integer> getTransferTypes() {
        java.util.List<Integer> transferTypes = new ArrayList<Integer>();
        transferTypes.add(transferType1);
        transferTypes.add(transferType2);

        return transferTypes;
    }

} // End of class
