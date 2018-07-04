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
import ui.AvatarMethod;
import ui.AvatarSignal;
import ui.TAttribute;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;


/**
 * Class JDialogAvatarBlock
 * Dialog for managing attributes, methods and signals of Avatar Blocks
 * Creation: 08/04/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 08/04/2010
 */
public class JDialogAvatarBlock extends JDialogBase implements ActionListener, ListSelectionListener {

    private LinkedList<TAttribute> attributes, attributesPar, forbidden;
    private LinkedList<Boolean> initValues;
    private LinkedList<AvatarMethod> methods, methodsPar;
    private LinkedList<AvatarSignal> signals, signalsPar;
    private boolean checkKeyword, checkJavaKeyword;

    private boolean cancelled = true;

    protected String[] globalCode;
    protected JTextArea jtaGlobalCode;
    protected boolean hasGlobalCode;
    protected String mainCode;
    protected JTextArea jtaMainCode;

    private JPanel panel1, panel2;

    private Frame frame;
    private int tab;

    private String attrib; // "Attributes", "Gates", etc.

    // Panel1
    private JComboBox<String> accessBox, typeBox;
    private JTextField identifierText;
    private JTextField initialValue;
    private JButton addButton;

    //Panel2
    private JList<TAttribute> listAttribute;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;

    // Method
    private boolean hasMethods = true;
    private JPanel panel3, panel4;
    private JTextField methodText;
    private JButton addMethodButton;
    private JList<AvatarMethod> listMethod;
    private JButton upMethodButton;
    private JButton downMethodButton;
    private JButton removeMethodButton;
    private JCheckBox implementationProvided;

    // Signals
    private boolean hasSignals = true;
    private JPanel panel5, panel6;
    private JComboBox<String> signalInOutBox;
    private JTextField signalText;
    private JButton addSignalButton;
    private JList<AvatarSignal> listSignal;
    private JButton upSignalButton;
    private JButton downSignalButton;
    private JButton removeSignalButton;

    /**
     * Creates new form
     */
    public JDialogAvatarBlock(LinkedList<TAttribute> _attributes, LinkedList<AvatarMethod> _methods, LinkedList<AvatarSignal> _signals, LinkedList<TAttribute> _forbidden, Frame f, String title, String attrib, int _tab, String[] _globalCode, boolean _hasGlobalCode, String _mainCode) {
        super(f, title, true);
        frame = f;
        attributesPar = _attributes;
        methodsPar = _methods;
        signalsPar = _signals;
        globalCode = _globalCode;
        mainCode = _mainCode;


        if (methodsPar == null) {
            methodsPar = new LinkedList<AvatarMethod>();
            hasMethods = false;
        }

        if (signalsPar == null) {
            signalsPar = new LinkedList<AvatarSignal>();
            hasSignals = false;
        }

        hasGlobalCode = _hasGlobalCode;
        if (globalCode == null) {
            globalCode = new String[1];
            globalCode[0] = "";
        }


        forbidden = _forbidden;
        initValues = new LinkedList<Boolean>();
        this.attrib = attrib;
        tab = _tab;

        attributes = new LinkedList<TAttribute>();
        methods = new LinkedList<AvatarMethod>();
        signals = new LinkedList<AvatarSignal>();

        for (TAttribute attr : this.attributesPar)
            this.attributes.add(attr.makeClone());

        for (AvatarMethod meth : this.methodsPar)
            this.methods.add(meth.makeClone());

        for (AvatarSignal sig : this.signalsPar)
            this.signals.add(sig.makeClone());

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

        //JPanel panelAttr = new JPanel(new FlowLayout());
        //JPanel panelMethod = new JPanel(new BorderLayout());
        //JPanel panelSignal = new JPanel(new BorderLayout());
        JPanel panelCode;
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagLayout gridbag3 = new GridBagLayout();
        // GridBagLayout gridbag4 = new GridBagLayout();
        GridBagLayout gridbag5 = new GridBagLayout();
        GridBagLayout gridbag6 = new GridBagLayout();
        GridBagLayout gridbag7 = new GridBagLayout();
        GridBagLayout gridbag8 = new GridBagLayout();
        GridBagLayout gridbag9 = new GridBagLayout();
        GridBagLayout gridbag10 = new GridBagLayout();

        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        GridBagConstraints c3 = new GridBagConstraints();
        GridBagConstraints c4 = new GridBagConstraints();
        GridBagConstraints c5 = new GridBagConstraints();
        GridBagConstraints c6 = new GridBagConstraints();
        GridBagConstraints c7 = new GridBagConstraints();
        GridBagConstraints c8 = new GridBagConstraints();
        GridBagConstraints c9 = new GridBagConstraints();
        GridBagConstraints c10 = new GridBagConstraints();

        JPanel panelAttr = new JPanel(gridbag8);
        JPanel panelMethod = new JPanel(gridbag9);
        JPanel panelSignal = new JPanel(gridbag10);


        setFont(new Font("Helvetica", Font.PLAIN, 14));
        //c.setLayout(gridbag0);
        c.setLayout(new BorderLayout());


        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Adding " + attrib + "s"));
        panel1.setPreferredSize(new Dimension(300, 550));
        panel1.setMinimumSize(new Dimension(300, 200));

        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Managing " + attrib + "s"));
        panel2.setPreferredSize(new Dimension(300, 550));
        panel2.setMinimumSize(new Dimension(300, 200));

        // first line panel1
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
        c1.gridwidth = 1;
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

        // fourth line panel2
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        addButton = new JButton("Add / Modify " + attrib);
        addButton.addActionListener(this);
        panel1.add(addButton, c1);

        // 1st line panel2
        listAttribute = new JList<TAttribute>(this.attributes.toArray(new TAttribute[0]));
        //listAttribute.setFixedCellWidth(150);
        //listAttribute.setFixedCellHeight(20);
        listAttribute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAttribute.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(listAttribute);
        scrollPane.setSize(300, 450);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
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
        upButton = new JButton("Up");
        upButton.addActionListener(this);
        panel2.add(upButton, c2);

        downButton = new JButton("Down");
        downButton.addActionListener(this);
        panel2.add(downButton, c2);

        removeButton = new JButton("Remove " + attrib);
        removeButton.addActionListener(this);
        panel2.add(removeButton, c2);

        // Methods
        panel3 = new JPanel();
        panel3.setLayout(gridbag3);
        panel3.setBorder(new javax.swing.border.TitledBorder("Adding methods"));
        panel3.setPreferredSize(new Dimension(300, 550));

        panel4 = new JPanel();
        panel4.setLayout(gridbag2);
        panel4.setBorder(new javax.swing.border.TitledBorder("Managing methods"));
        panel4.setPreferredSize(new Dimension(300, 550));

        // first line panel3
        c3.gridwidth = 1;
        c3.gridheight = 1;
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        panel3.add(new JLabel(" "), c3);

        c3.gridwidth = 1;
        c3.gridheight = 1;
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.anchor = GridBagConstraints.CENTER;
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel3.add(new JLabel("method:"), c3);

        // second line panel3
        c3.fill = GridBagConstraints.HORIZONTAL;
        methodText = new JTextField();
        methodText.setColumns(50);
        methodText.setEditable(true);
        panel3.add(methodText, c3);

        // third line panel3
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        panel3.add(new JLabel(" "), c3);

        // fourth line panel3
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        implementationProvided = new JCheckBox("Implementation provided by user");
        implementationProvided.setSelected(false);
        panel3.add(implementationProvided, c3);


        // fifth line panel3
        c3.gridheight = 1;
        c3.fill = GridBagConstraints.HORIZONTAL;
        addMethodButton = new JButton("Add method");
        addMethodButton.addActionListener(this);
        panel3.add(addMethodButton, c3);

        // 1st line panel4
        listMethod = new JList<AvatarMethod>(this.methods.toArray(new AvatarMethod[0]));
        listMethod.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listMethod.addListSelectionListener(this);
        scrollPane = new JScrollPane(listMethod);
        scrollPane.setSize(300, 550);
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.BOTH;
        c4.gridheight = 5;
        c4.weighty = 10.0;
        c4.weightx = 10.0;
        panel4.add(scrollPane, c4);

        // 2nd line panel4
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.fill = GridBagConstraints.BOTH;
        c4.gridheight = 1;
        panel4.add(new JLabel(""), c4);

        // third line panel4
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.HORIZONTAL;
        upMethodButton = new JButton("Up");
        upMethodButton.addActionListener(this);
        panel4.add(upMethodButton, c4);

        downMethodButton = new JButton("Down");
        downMethodButton.addActionListener(this);
        panel4.add(downMethodButton, c4);

        removeMethodButton = new JButton("Remove method");
        removeMethodButton.addActionListener(this);
        panel4.add(removeMethodButton, c4);

        // Signals
        panel5 = new JPanel();
        panel5.setLayout(gridbag5);
        panel5.setBorder(new javax.swing.border.TitledBorder("Adding signals"));
        panel5.setPreferredSize(new Dimension(300, 550));

        panel6 = new JPanel();
        panel6.setLayout(gridbag6);
        panel6.setBorder(new javax.swing.border.TitledBorder("Managing signals"));
        panel6.setPreferredSize(new Dimension(300, 550));

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
        String[] v = new String[2];
        v[0] = "in";
        v[1] = "out";
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
        listSignal = new JList<AvatarSignal>(this.signals.toArray(new AvatarSignal[0]));
        listSignal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSignal.addListSelectionListener(this);
        scrollPane = new JScrollPane(listSignal);
        scrollPane.setSize(300, 450);
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

        panelCode.setBorder(new javax.swing.border.TitledBorder("Global code"));
        // guard
        c7.weighty = 1.0;
        c7.weightx = 1.0;
        c7.gridwidth = 1;
        c7.gridheight = 1;
        c7.fill = GridBagConstraints.BOTH;
        c7.gridwidth = GridBagConstraints.REMAINDER;
        c7.gridheight = 1;

        panelCode.add(new JLabel("Global code of application:"), c7);
        jtaMainCode = new JTextArea();
        jtaMainCode.setEditable(true);
        jtaMainCode.setMargin(new Insets(10, 10, 10, 10));
        jtaMainCode.setTabSize(3);
        String tmp = "";
        if (mainCode != null) {
            tmp = mainCode;
        }

        jtaMainCode.append(tmp);
        if (mainCode == null) {
            jtaMainCode.setEnabled(false);
        }
        jtaMainCode.setFont(new Font("times", Font.PLAIN, 12));
        JScrollPane jsp = new JScrollPane(jtaMainCode, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setPreferredSize(new Dimension(300, 200));
        panelCode.add(jsp, c2);

        panelCode.add(new JLabel("Global code of block:"), c7);
        panelCode.add(new JLabel("To implement a method m of block B: \"__userImplemented__B__m(...){...}\""), c7);
        jtaGlobalCode = new JTextArea();
        jtaGlobalCode.setEditable(true);
        jtaGlobalCode.setMargin(new Insets(10, 10, 10, 10));
        jtaGlobalCode.setTabSize(3);
        String files = "";
        if (globalCode != null) {
            for (int i = 0; i < globalCode.length; i++) {
                files += globalCode[i] + "\n";
            }
        }
        jtaGlobalCode.append(files);
        jtaGlobalCode.setFont(new Font("times", Font.PLAIN, 12));
        jsp = new JScrollPane(jtaGlobalCode, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setPreferredSize(new Dimension(300, 200));
        panelCode.add(jsp, c2);


        // main panel;
        //panelAttr.add(panel1, BorderLayout.WEST);
        //panelAttr.add(panel2, BorderLayout.EAST);
        c8.gridwidth = 1;
        c8.gridheight = 10;
        c8.weighty = 1.0;
        c8.weightx = 1.0;
        c8.fill = GridBagConstraints.BOTH;
        panelAttr.add(panel1, c8);
        c8.gridwidth = GridBagConstraints.REMAINDER; //end row
        //c.add(tabbedPane, c0);

        c8.gridwidth = 1;
        c8.gridheight = 10;
        panelAttr.add(panel2, c8);

        tabbedPane.addTab("Attributes", panelAttr);

        if (hasMethods) {
            //panelMethod.add(panel3, BorderLayout.WEST);
            //panelMethod.add(panel4, BorderLayout.EAST);
            c9.gridwidth = 1;
            c9.gridheight = 10;
            c9.weighty = 1.0;
            c9.weightx = 1.0;
            c9.fill = GridBagConstraints.BOTH;
            panelMethod.add(panel3, c9);
            c9.gridwidth = GridBagConstraints.REMAINDER; //end row
            //c.add(tabbedPane, c0);

            c9.gridwidth = 1;
            c9.gridheight = 10;
            panelMethod.add(panel4, c9);
            tabbedPane.addTab("Methods", panelMethod);
        }

        if (hasSignals) {
            //panelSignal.add(panel5, BorderLayout.WEST);
            //panelSignal.add(panel6, BorderLayout.EAST);
            c10.gridwidth = 1;
            c10.gridheight = 10;
            c10.weighty = 1.0;
            c10.weightx = 1.0;
            c10.fill = GridBagConstraints.BOTH;
            panelSignal.add(panel5, c10);
            c10.gridwidth = GridBagConstraints.REMAINDER; //end row
            //c.add(tabbedPane, c0);

            c10.gridwidth = 1;
            c10.gridheight = 10;
            panelSignal.add(panel6, c10);

            tabbedPane.addTab("Signals", panelSignal);
        }

        if (hasGlobalCode) {
            tabbedPane.addTab("Prototyping", panelCode);
        }

        tabbedPane.setSelectedIndex(tab);

        //c.add(panel1, c0);
        //c.add(panel2, c0);

        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        //c.add(tabbedPane, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;

        JPanel panel4Buttons = new JPanel();
        panel4Buttons.setLayout(gridbag0);
        initButtons(c0, panel4Buttons, this);

        c.add(tabbedPane, BorderLayout.CENTER);
        c.add(panel4Buttons, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == typeBox) {
            boolean b = initValues.get(typeBox.getSelectedIndex()).booleanValue();
            initialValue.setEnabled(b);
            return;
        }


        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton) {
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
        } else if (evt.getSource() == upMethodButton) {
            upMethod();
        } else if (evt.getSource() == downMethodButton) {
            downMethod();
        } else if (evt.getSource() == removeMethodButton) {
            removeMethod();
        } else if (evt.getSource() == addMethodButton) {
            addMethod();
        } else if (evt.getSource() == downSignalButton) {
            downSignal();
        } else if (evt.getSource() == upSignalButton) {
            upSignal();
        } else if (evt.getSource() == removeSignalButton) {
            removeSignal();
        } else if (evt.getSource() == addSignalButton) {
            addSignal();
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

        if (s.length() > 0) {
            if ((TAttribute.isAValidId(s, checkKeyword, checkJavaKeyword)) && (TAttribute.notIn(s, forbidden))) {
                int i = TAttribute.getAccess(o1.toString());
                int j = TAttribute.getAvatarType(o2.toString());

                if ((j == TAttribute.ARRAY_NAT) && (value.length() < 1)) {
                    value = "2";
                }

                if ((i != -1) && (j != -1)) {

                    if ((value.length() < 1) || (initialValue.isEnabled() == false)) {

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
                        a.isAvatar = true;
                        //
                    } else {
                        a = new TAttribute(i, s, value, j);
                        a.isAvatar = true;
                    }
                    //checks whether the same attribute already belongs to the list
                    int index = attributes.size();
                    if (attributes.contains(a)) {
                        index = attributes.indexOf(a);
                        a = attributes.get(index);
                        a.setAccess(i);
                        if (j == TAttribute.OTHER) {
                            a.setTypeOther(o2.toString());
                        }
                        a.setType(j);
                        a.setInitialValue(value);
                    } else {
                        attributes.add(index, a);
                    }
                    listAttribute.setListData(attributes.toArray(new TAttribute[0]));
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

    public void addMethod() {
        //TraceManager.addDev("addMethod");
        String s = methodText.getText();
        AvatarMethod am = AvatarMethod.isAValidMethod(s);

        AvatarMethod amtmp;

        if (am != null) {
            am.setImplementationProvided(implementationProvided.isSelected());

            // Checks whether the same method already belongs to the list
            int index = -1;
            for (int i = 0; i < methods.size(); i++) {
                amtmp = methods.get(i);
                // Same id?
                if (amtmp.equals(am)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                methods.add(am);
            } else {
                methods.remove(index);
                methods.add(index, am);
            }
            listMethod.setListData(methods.toArray(new AvatarMethod[0]));
            methodText.setText("");

        } else {
            JOptionPane.showMessageDialog(frame,
                    "Badly formatted method declaration",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    public void addSignal() {
        //TraceManager.addDev("addSignal");
        String s = signalText.getText();
        AvatarSignal as = AvatarSignal.isAValidSignal(signalInOutBox.getSelectedIndex(), s);
        AvatarSignal astmp;

        if (as != null) {
            // Checks whether the same signal already belongs to the list
            int index = -1;
            for (int i = 0; i < signals.size(); i++) {
                astmp = signals.get(i);
                // Same id?
                if (astmp.equals(as)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                signals.add(as);
            } else {
                signals.remove(index);
                signals.add(index, as);
            }
            listSignal.setListData(signals.toArray(new AvatarSignal[0]));
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
        int i = listAttribute.getSelectedIndex();
        if (i != -1) {
            TAttribute a = attributes.get(i);
            a.setAccess(-1);
            attributes.remove(i);
            listAttribute.setListData(attributes.toArray(new TAttribute[0]));
        }
    }

    public void downAttribute() {
        int i = listAttribute.getSelectedIndex();
        if ((i != -1) && (i != attributes.size() - 1)) {
            TAttribute o = attributes.get(i);
            attributes.remove(i);
            attributes.add(i + 1, o);
            listAttribute.setListData(attributes.toArray(new TAttribute[0]));
            listAttribute.setSelectedIndex(i + 1);
        }
    }

    public void upAttribute() {
        int i = listAttribute.getSelectedIndex();
        //TraceManager.addDev("Selected index = " + i);
        if (i > 0) {
            //TraceManager.addDev("Modifying ...");
            TAttribute o = attributes.get(i);
            attributes.remove(i);
            attributes.add(i - 1, o);
            listAttribute.setListData(attributes.toArray(new TAttribute[0]));
            listAttribute.setSelectedIndex(i - 1);
        }
    }

    public void removeMethod() {
        int i = listMethod.getSelectedIndex();
        if (i != -1) {
            methods.remove(i);
            listMethod.setListData(methods.toArray(new AvatarMethod[0]));
        }
    }

    public void upMethod() {
        int i = listMethod.getSelectedIndex();
        //TraceManager.addDev("Selected index method = " + i);
        if (i > 0) {
            AvatarMethod o = methods.get(i);
            methods.remove(i);
            methods.add(i - 1, o);
            listMethod.setListData(methods.toArray(new AvatarMethod[0]));
            listMethod.setSelectedIndex(i - 1);
        }
    }

    public void downMethod() {
        int i = listMethod.getSelectedIndex();
        if ((i != -1) && (i != methods.size() - 1)) {
            AvatarMethod o = methods.get(i);
            methods.remove(i);
            methods.add(i + 1, o);
            listMethod.setListData(methods.toArray(new AvatarMethod[0]));
            listMethod.setSelectedIndex(i + 1);
        }
    }

    public void removeSignal() {
        int i = listSignal.getSelectedIndex();
        if (i != -1) {
            signals.remove(i);
            listSignal.setListData(signals.toArray(new AvatarSignal[0]));
        }
    }

    public void upSignal() {
        int i = listSignal.getSelectedIndex();
        if (i > 0) {
            AvatarSignal o = signals.get(i);
            signals.remove(i);
            signals.add(i - 1, o);
            listSignal.setListData(signals.toArray(new AvatarSignal[0]));
            listSignal.setSelectedIndex(i - 1);
        }
    }

    public void downSignal() {
        int i = listSignal.getSelectedIndex();
        if ((i != -1) && (i != signals.size() - 1)) {
            AvatarSignal o = signals.get(i);
            signals.remove(i);
            signals.add(i + 1, o);
            listSignal.setListData(signals.toArray(new AvatarSignal[0]));
            listSignal.setSelectedIndex(i + 1);
        }
    }


    public void closeDialog() {
        cancelled = false;
        attributesPar.clear();
        for (TAttribute attr : this.attributes)
            attributesPar.add(attr);

        methodsPar.clear();
        for (AvatarMethod meth : this.methods)
            methodsPar.add(meth);

        signalsPar.clear();
        for (AvatarSignal sig : this.signals)
            signalsPar.add(sig);

        globalCode = Conversion.wrapText(jtaGlobalCode.getText());
        mainCode = jtaMainCode.getText();
        cancelled = false;
        dispose();
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }


    public void cancelDialog() {
        dispose();
    }

    public void valueChanged(ListSelectionEvent e) {
        int i = listAttribute.getSelectedIndex();
        if (i == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            identifierText.setText("");
            //initialValue.setText("");
        } else {
            TAttribute a = attributes.get(i);
            identifierText.setText(a.getId());
            initialValue.setText(a.getInitialValue());
            select(accessBox, TAttribute.getStringAccess(a.getAccess()));
            if (a.getType() == TAttribute.OTHER) {
                select(typeBox, a.getTypeOther());
            } else {
                select(typeBox, TAttribute.getStringAvatarType(a.getType()));
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

        i = listMethod.getSelectedIndex();
        if (i == -1) {
            removeMethodButton.setEnabled(false);
            upMethodButton.setEnabled(false);
            downMethodButton.setEnabled(false);
            methodText.setText("");
            //initialValue.setText("");
        } else {
            AvatarMethod am = methods.get(i);
            methodText.setText(am.toString());
            //TraceManager.addDev("Implementation of " + am + " is: " +  am.isImplementationProvided());
            implementationProvided.setSelected(am.isImplementationProvided());
            removeMethodButton.setEnabled(true);
            if (i > 0) {
                upMethodButton.setEnabled(true);
            } else {
                upMethodButton.setEnabled(false);
            }
            if (i != methods.size() - 1) {
                downMethodButton.setEnabled(true);
            } else {
                downMethodButton.setEnabled(false);
            }
        }

        i = listSignal.getSelectedIndex();
        if (i == -1) {
            removeSignalButton.setEnabled(false);
            upSignalButton.setEnabled(false);
            downSignalButton.setEnabled(false);
            signalText.setText("");
            //initialValue.setText("");
        } else {
            AvatarSignal as = signals.get(i);
            signalText.setText(as.toBasicString());
            signalInOutBox.setSelectedIndex(as.getInOut());
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
        for (int i = 0; i < jcb.getItemCount(); i++) {
            s = jcb.getItemAt(i);
            //
            if (s.equals(text)) {
                jcb.setSelectedIndex(i);
                return;
            }
        }
    }

    public String[] getGlobalCode() {
        return globalCode;
    }

    public String getMainCode() {
        return mainCode;
    }

}
