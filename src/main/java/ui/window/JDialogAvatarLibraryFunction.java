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

import myutil.GraphicLib;
import ui.AvatarMethod;
import ui.AvatarSignal;
import ui.TAttribute;
import ui.avatarbd.AvatarBDLibraryFunction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Dialog for managing parameters, return values, attributes, methods and
 * signals of Avatar Library Functions
 *
 * @author Florian Lugou
 * @version 1.0 04.11.2016
 */
public class JDialogAvatarLibraryFunction extends JDialogBase implements ActionListener, ListSelectionListener {
  private AvatarBDLibraryFunction bdElement;

  private boolean checkKeyword;
  private boolean checkJavaKeyword;

  // General
  private JTabbedPane tabbedPane;

  private JButton removeButtons[];
  private JButton modifyButtons[];
  private JButton upButtons[];
  private JButton downButtons[];
  private HashMap<Integer, JList<Object>> listAttribute;
  private ArrayList<LinkedList<Object>> attributes;

  // Parameters Tab
  private JComboBox<String> parametersAccessBox;
  private JTextField parametersIdentifierText;
  private JTextField parametersInitialValue;
  private JComboBox<String> parametersTypeBox;

  // Signals Tab
  private JComboBox<String> signalInOutBox;
  private JTextField signalText;

  // Return Attributes Tab
  private JComboBox<String> returnAttributesAccessBox;
  private JTextField returnAttributesIdentifierText;
  private JTextField returnAttributesInitialValue;
  private JComboBox<String> returnAttributesTypeBox;

  // Attributes Tab
  private JComboBox<String> attributesAccessBox;
  private JTextField attributesIdentifierText;
  private JTextField attributesInitialValue;
  private JComboBox<String> attributesTypeBox;

  // Methods Tab
  private JTextField methodText;

  public JDialogAvatarLibraryFunction(AvatarBDLibraryFunction bdElement, JFrame frame, String title, String attrib) {
    super(frame, title, true);

    this.bdElement = bdElement;
    this.checkKeyword = true;
    this.checkJavaKeyword = true;

    this.removeButtons = new JButton[5];
    this.modifyButtons = new JButton[5];
    this.upButtons = new JButton[5];
    this.downButtons = new JButton[5];
    this.listAttribute = new HashMap<>();
    this.attributes = new ArrayList<>();

    LinkedList<Object> l = new LinkedList<>();
    for (TAttribute attr : this.bdElement.getParameters())
      l.add(attr.makeClone());
    this.attributes.add(l);

    l = new LinkedList<>();
    for (AvatarSignal signal : this.bdElement.getSignals())
      l.add(signal.makeClone());
    this.attributes.add(l);

    l = new LinkedList<>();
    for (TAttribute attr : this.bdElement.getReturnAttributes())
      l.add(attr.makeClone());
    this.attributes.add(l);

    l = new LinkedList<>();
    for (TAttribute attr : this.bdElement.getAttributes())
      l.add(attr.makeClone());
    this.attributes.add(l);

    l = new LinkedList<>();
    for (AvatarMethod meth : this.bdElement.getMethods())
      l.add(meth.makeClone());
    this.attributes.add(l);

    this.initComponents();

    this.addAccess(TAttribute.getStringAccess(TAttribute.PRIVATE));
    this.addType(TAttribute.getStringAvatarType(TAttribute.BOOLEAN));
    this.addType(TAttribute.getStringAvatarType(TAttribute.INTEGER));
    this.addType(TAttribute.getStringType(TAttribute.TIMER));
    for (String s : this.bdElement.getDiagramPanel().getAllDataTypes())
      this.addType(s);
    /*
     * pack();
     */
  }

  private void fillGenericAttributesTab(JPanel tab, int tabIndex, String tabTitle, JComboBox<String> accessBox,
      JTextField identifierText, JTextField initialValue, JComboBox<String> typeBox) {
    // West Panel

    GridBagConstraints c0 = new GridBagConstraints();

    c0.gridwidth = 1;
    c0.gridheight = 1;
    c0.weighty = 1.0;
    c0.weightx = 1.0;
    c0.fill = GridBagConstraints.BOTH;

    JPanel panelWest = new JPanel();
    tab.add(panelWest, c0);
    panelWest.setLayout(new GridBagLayout());
    panelWest.setBorder(new javax.swing.border.TitledBorder("Adding " + tabTitle + "s"));
    panelWest.setPreferredSize(new Dimension(300, 450));

    // first line of west panel (field titles)
    GridBagConstraints gridConstraints = new GridBagConstraints();
    gridConstraints.gridheight = 1;
    gridConstraints.weighty = 1.0;
    gridConstraints.weightx = 1.0;
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 3;

    panelWest.add(new JLabel(" "), gridConstraints);

    gridConstraints.gridwidth = 1;
    gridConstraints.anchor = GridBagConstraints.CENTER;
    panelWest.add(new JLabel("access"), gridConstraints);
    panelWest.add(new JLabel("identifier"), gridConstraints);
    panelWest.add(new JLabel(" "), gridConstraints);
    panelWest.add(new JLabel("initial value"), gridConstraints);
    panelWest.add(new JLabel(" "), gridConstraints);

    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    panelWest.add(new JLabel("type"), gridConstraints);

    // second line west panel (input fields)
    gridConstraints.gridwidth = 1;
    gridConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridConstraints.anchor = GridBagConstraints.CENTER;
    panelWest.add(accessBox, gridConstraints);

    identifierText.setColumns(15);
    identifierText.setEditable(true);
    panelWest.add(identifierText, gridConstraints);

    panelWest.add(new JLabel(" = "), gridConstraints);

    initialValue.setColumns(5);
    initialValue.setEditable(true);
    panelWest.add(initialValue, gridConstraints);

    panelWest.add(new JLabel(" : "), gridConstraints);

    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    typeBox.addActionListener(this);
    panelWest.add(typeBox, gridConstraints);

    // third line west panel (empty line)
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 3;
    panelWest.add(new JLabel(" "), gridConstraints);

    // fourth line west panel (Add and modify buttons)
    gridConstraints.gridheight = 1;
    gridConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridConstraints.weightx = 1.0;
    gridConstraints.gridwidth = 3;
    this.modifyButtons[tabIndex] = new JButton("Modify " + tabTitle);
    this.modifyButtons[tabIndex].addActionListener(this);
    this.modifyButtons[tabIndex].setEnabled(false);
    panelWest.add(this.modifyButtons[tabIndex], gridConstraints);

    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    JButton addButton = new JButton("Add " + tabTitle);
    addButton.addActionListener(this);
    panelWest.add(addButton, gridConstraints);

    // East Panel
    c0.gridwidth = GridBagConstraints.REMAINDER;
    JPanel panelEast = new JPanel();
    tab.add(panelEast, c0);
    panelEast.setLayout(new GridBagLayout());
    panelEast.setBorder(new javax.swing.border.TitledBorder("Managing " + tabTitle + "s"));
    panelEast.setPreferredSize(new Dimension(300, 450));

    // first line east panel
    this.listAttribute.put(tabIndex, new JList<>(this.attributes.get(tabIndex).toArray()));
    this.listAttribute.get(tabIndex).setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.listAttribute.get(tabIndex).addListSelectionListener(this);
    JScrollPane scrollPane = new JScrollPane(this.listAttribute.get(tabIndex));
    scrollPane.setSize(300, 250);
    gridConstraints = new GridBagConstraints();
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 5;
    gridConstraints.weighty = 10.0;
    gridConstraints.weightx = 10.0;
    panelEast.add(scrollPane, gridConstraints);

    // second line east panel
    gridConstraints.weighty = 1.0;
    gridConstraints.weightx = 1.0;
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 1;
    panelEast.add(new JLabel(""), gridConstraints);

    // third line east panel
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.HORIZONTAL;
    this.upButtons[tabIndex] = new JButton("Up");
    this.upButtons[tabIndex].setEnabled(false);
    this.upButtons[tabIndex].addActionListener(this);
    panelEast.add(this.upButtons[tabIndex], gridConstraints);

    this.downButtons[tabIndex] = new JButton("Down");
    this.downButtons[tabIndex].setEnabled(false);
    this.downButtons[tabIndex].addActionListener(this);
    panelEast.add(this.downButtons[tabIndex], gridConstraints);

    this.removeButtons[tabIndex] = new JButton("Remove " + tabTitle);
    this.removeButtons[tabIndex].setEnabled(false);
    this.removeButtons[tabIndex].addActionListener(this);
    panelEast.add(this.removeButtons[tabIndex], gridConstraints);
  }

  private JPanel initParametersTab() {
    GridBagLayout gridbag0 = new GridBagLayout();
    JPanel panelParameters = new JPanel(gridbag0);
    this.parametersAccessBox = new JComboBox<String>();
    this.parametersIdentifierText = new JTextField();
    this.parametersInitialValue = new JTextField();
    this.parametersTypeBox = new JComboBox<String>();

    this.fillGenericAttributesTab(panelParameters, 0, "Parameter", this.parametersAccessBox,
        this.parametersIdentifierText, this.parametersInitialValue, this.parametersTypeBox);

    return panelParameters;
  }

  private JPanel initSignalsTab() {
    GridBagLayout gridbag0 = new GridBagLayout();
    GridBagConstraints c0 = new GridBagConstraints();
    JPanel panelSignals = new JPanel(gridbag0);

    c0.gridwidth = 1;
    c0.gridheight = 1;
    c0.weighty = 1.0;
    c0.weightx = 1.0;
    c0.fill = GridBagConstraints.BOTH;

    // West Panel
    JPanel panelWest = new JPanel();
    panelSignals.add(panelWest, c0);
    panelWest.setLayout(new GridBagLayout());
    panelWest.setBorder(new javax.swing.border.TitledBorder("Adding Signals"));
    panelWest.setPreferredSize(new Dimension(300, 250));

    // first line west panel
    GridBagConstraints gridConstraints = new GridBagConstraints();
    gridConstraints.weighty = 1.0;
    gridConstraints.weightx = 1.0;
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 3;
    panelWest.add(new JLabel(" "), gridConstraints);

    gridConstraints.gridheight = 1;
    gridConstraints.anchor = GridBagConstraints.CENTER;
    panelWest.add(new JLabel("signal:"), gridConstraints);

    // second line west panel
    gridConstraints.gridwidth = 1;
    gridConstraints.fill = GridBagConstraints.HORIZONTAL;
    this.signalInOutBox = new JComboBox<String>(new String[] { "in", "out" });
    panelWest.add(this.signalInOutBox, gridConstraints);

    this.signalText = new JTextField();
    this.signalText.setColumns(50);
    this.signalText.setEditable(true);
    panelWest.add(this.signalText, gridConstraints);

    // third line west panel
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 3;
    panelWest.add(new JLabel(" "), gridConstraints);

    // fourth line west panel
    gridConstraints.gridheight = 1;
    gridConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridConstraints.gridwidth = 1;
    this.modifyButtons[1] = new JButton("Modify Signal");
    this.modifyButtons[1].addActionListener(this);
    this.modifyButtons[1].setEnabled(false);
    panelWest.add(this.modifyButtons[1], gridConstraints);

    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    JButton addSignalButton = new JButton("Add Signal");
    addSignalButton.addActionListener(this);
    panelWest.add(addSignalButton, gridConstraints);

    // East Panel
    c0.gridwidth = GridBagConstraints.REMAINDER;
    JPanel panelEast = new JPanel();
    panelSignals.add(panelEast, c0);
    panelEast.setLayout(new GridBagLayout());
    panelEast.setBorder(new javax.swing.border.TitledBorder("Managing Signals"));
    panelEast.setPreferredSize(new Dimension(300, 250));

    // first line east panel
    this.listAttribute.put(1, new JList<>(this.attributes.get(1).toArray()));
    this.listAttribute.get(1).setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.listAttribute.get(1).addListSelectionListener(this);
    JScrollPane scrollPane = new JScrollPane(this.listAttribute.get(1));
    scrollPane.setSize(300, 250);
    gridConstraints = new GridBagConstraints();
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 5;
    gridConstraints.weighty = 10.0;
    gridConstraints.weightx = 10.0;
    panelEast.add(scrollPane, gridConstraints);

    // second line east panel
    gridConstraints.weighty = 1.0;
    gridConstraints.weightx = 1.0;
    gridConstraints.gridheight = 1;
    panelEast.add(new JLabel(""), gridConstraints);

    // third line east panel
    gridConstraints.fill = GridBagConstraints.HORIZONTAL;
    this.upButtons[1] = new JButton("Up");
    this.upButtons[1].setEnabled(false);
    this.upButtons[1].addActionListener(this);
    panelEast.add(this.upButtons[1], gridConstraints);

    this.downButtons[1] = new JButton("Down");
    this.downButtons[1].setEnabled(false);
    this.downButtons[1].addActionListener(this);
    panelEast.add(this.downButtons[1], gridConstraints);

    this.removeButtons[1] = new JButton("Remove Signal");
    this.removeButtons[1].setEnabled(false);
    this.removeButtons[1].addActionListener(this);
    panelEast.add(this.removeButtons[1], gridConstraints);

    return panelSignals;
  }

  private JPanel initReturnAttributesTab() {
    GridBagLayout gridbag0 = new GridBagLayout();
    JPanel panelReturnAttributes = new JPanel(gridbag0);
    this.returnAttributesAccessBox = new JComboBox<String>();
    this.returnAttributesIdentifierText = new JTextField();
    this.returnAttributesInitialValue = new JTextField();
    this.returnAttributesTypeBox = new JComboBox<String>();

    this.fillGenericAttributesTab(panelReturnAttributes, 2, "Return Value", this.returnAttributesAccessBox,
        this.returnAttributesIdentifierText, this.returnAttributesInitialValue, this.returnAttributesTypeBox);

    return panelReturnAttributes;
  }

  private JPanel initAttributesTab() {
    GridBagLayout gridbag0 = new GridBagLayout();
    JPanel panelAttributes = new JPanel(gridbag0);
    this.attributesAccessBox = new JComboBox<String>();
    this.attributesIdentifierText = new JTextField();
    this.attributesInitialValue = new JTextField();
    this.attributesTypeBox = new JComboBox<String>();

    this.fillGenericAttributesTab(panelAttributes, 3, "Local Attribute", this.attributesAccessBox,
        this.attributesIdentifierText, this.attributesInitialValue, this.attributesTypeBox);

    return panelAttributes;
  }

  private JPanel initMethodsTab() {
    GridBagLayout gridbag0 = new GridBagLayout();
    GridBagConstraints c0 = new GridBagConstraints();
    JPanel panelMethods = new JPanel(gridbag0);

    c0.gridwidth = 1;
    c0.gridheight = 1;
    c0.weighty = 1.0;
    c0.weightx = 1.0;
    c0.fill = GridBagConstraints.BOTH;

    // Panel West
    JPanel panelWest = new JPanel();
    panelMethods.add(panelWest, c0);
    panelWest.setLayout(new GridBagLayout());
    panelWest.setBorder(new javax.swing.border.TitledBorder("Adding Methods"));
    // panelWest.setPreferredSize(new Dimension(300, 250));

    // first line west panel
    GridBagConstraints gridConstraints = new GridBagConstraints();
    gridConstraints.weighty = 1.0;
    gridConstraints.weightx = 1.0;
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 3;
    panelWest.add(new JLabel(" "), gridConstraints);

    gridConstraints.gridheight = 1;
    gridConstraints.anchor = GridBagConstraints.CENTER;
    panelWest.add(new JLabel("method:"), gridConstraints);

    // second line west panel
    gridConstraints.fill = GridBagConstraints.HORIZONTAL;
    this.methodText = new JTextField();
    this.methodText.setColumns(50);
    this.methodText.setEditable(true);
    panelWest.add(this.methodText, gridConstraints);

    // third line west panel
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 3;
    panelWest.add(new JLabel(" "), gridConstraints);

    // fourth line west panel
    gridConstraints.gridheight = 1;
    gridConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridConstraints.gridwidth = 1;
    this.modifyButtons[4] = new JButton("Modify Method");
    this.modifyButtons[4].addActionListener(this);
    this.modifyButtons[4].setEnabled(false);
    panelWest.add(this.modifyButtons[4], gridConstraints);

    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    JButton addMethodButton = new JButton("Add Method");
    addMethodButton.addActionListener(this);
    panelWest.add(addMethodButton, gridConstraints);

    // Panel East
    c0.gridwidth = GridBagConstraints.REMAINDER; // end row
    JPanel panelEast = new JPanel();
    panelMethods.add(panelEast, c0);
    panelEast.setLayout(new GridBagLayout());
    panelEast.setBorder(new javax.swing.border.TitledBorder("Managing Methods"));
    // panelEast.setPreferredSize(new Dimension(300, 250));

    // first line east panel
    this.listAttribute.put(4, new JList<>(this.attributes.get(4).toArray()));
    this.listAttribute.get(4).setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.listAttribute.get(4).addListSelectionListener(this);
    JScrollPane scrollPane = new JScrollPane(this.listAttribute.get(4));
    scrollPane.setSize(300, 250);
    gridConstraints = new GridBagConstraints();
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 5;
    gridConstraints.weighty = 10.0;
    gridConstraints.weightx = 10.0;
    panelEast.add(scrollPane, gridConstraints);

    // second line east panel
    gridConstraints.weighty = 1.0;
    gridConstraints.weightx = 1.0;
    gridConstraints.fill = GridBagConstraints.BOTH;
    gridConstraints.gridheight = 1;
    panelEast.add(new JLabel(""), gridConstraints);

    // third line east panel
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.HORIZONTAL;
    this.upButtons[4] = new JButton("Up");
    this.upButtons[4].setEnabled(false);
    this.upButtons[4].addActionListener(this);
    panelEast.add(this.upButtons[4], gridConstraints);

    this.downButtons[4] = new JButton("Down");
    this.downButtons[4].setEnabled(false);
    this.downButtons[4].addActionListener(this);
    panelEast.add(this.downButtons[4], gridConstraints);

    this.removeButtons[4] = new JButton("Remove Method");
    this.removeButtons[4].setEnabled(false);
    this.removeButtons[4].addActionListener(this);
    panelEast.add(this.removeButtons[4], gridConstraints);

    return panelMethods;
  }

  private void initComponents() {
    // Get container
    Container c = this.getContentPane();
    c.setLayout(new GridBagLayout());
    c.setLayout(new BorderLayout());

    this.setFont(new Font("Helvetica", Font.PLAIN, 14));
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Add the tabs panel
    // Issue #41 Ordering of tabbed panes
    this.tabbedPane = GraphicLib.createTabbedPane();// new JTabbedPane();

    tabbedPane.addTab("Parameters", this.initParametersTab()); // Parameters Tab
    tabbedPane.addTab("Signals", this.initSignalsTab()); // Signals Tab
    tabbedPane.addTab("Return Values", this.initReturnAttributesTab()); // Return Attributes Tab
    tabbedPane.addTab("Attributes", this.initAttributesTab()); // Attributes Tab
    tabbedPane.addTab("Methods", this.initMethodsTab()); // Methods Tab

    GridBagConstraints gridConstraints = new GridBagConstraints();
    gridConstraints.gridwidth = 1;
    gridConstraints.gridheight = 20;
    gridConstraints.weighty = 1.0;
    gridConstraints.weightx = 1.0;
    gridConstraints.gridwidth = GridBagConstraints.REMAINDER; // end row
    gridConstraints.fill = GridBagConstraints.BOTH; // end row
    // c.add(tabbedPane, gridConstraints);
    c.add(tabbedPane, BorderLayout.CENTER);

    // Add Save & Close button
    gridConstraints.gridwidth = 1;
    gridConstraints.gridheight = 1;
    gridConstraints.fill = GridBagConstraints.HORIZONTAL;

    c.add(initBasicButtons(this), BorderLayout.SOUTH);

  }

  public void selectTabIndex(int tab) {
    this.tabbedPane.setSelectedIndex(tab);
  }

  private void highlightField(final JTextField textField) {
    /*
     * (new Thread () { public void run () { Color color = textField.getBackground
     * (); try { for (int i=0; i<5; i++) { textField.setBackground (Color.RED);
     * Thread.sleep (50);
     * 
     * textField.setBackground (color); Thread.sleep (100); } } catch
     * (InterruptedException e) { textField.setBackground (color); } } }).start ();
     */
  }

  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == this.closeButton) {
      this.save();
      this.dispose();
    } else if (source == this.cancelButton)
      this.dispose();
    else if (source instanceof JButton) {
      String label = ((JButton) source).getText().split(" ", 2)[0];
      switch (label) {
        case "Add":
          this.handleAdd();
          break;
        case "Modify":
          this.handleModify();
          break;
        case "Up":
          this.handleUp();
          break;
        case "Down":
          this.handleDown();
          break;
        case "Remove":
          this.handleRemove();
          break;
        default:
          // Should not arrive here
      }
    }
    /*
     * if (evt.getSource() == typeBox) { boolean b =
     * ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
     * initialValue.setEnabled(b); return; }
     */
  }

  private void save() {
    this.bdElement.resetParameters();
    for (Object o : this.attributes.get(0))
      this.bdElement.addParameter((TAttribute) o);
    this.bdElement.resetSignals();
    for (Object o : this.attributes.get(1))
      this.bdElement.addSignal((AvatarSignal) o);
    this.bdElement.resetReturnAttributes();
    for (Object o : this.attributes.get(2))
      this.bdElement.addReturnAttribute((TAttribute) o);
    this.bdElement.resetAttributes();
    for (Object o : this.attributes.get(3))
      this.bdElement.addAttribute((TAttribute) o);
    this.bdElement.resetMethods();
    for (Object o : this.attributes.get(4))
      this.bdElement.addMethod((AvatarMethod) o);
  }

  private void handleAdd() {
    int selectedTab = this.tabbedPane.getSelectedIndex();
    switch (selectedTab) {
      case 0:
        this.addAttribute(0, this.parametersAccessBox, this.parametersIdentifierText, this.parametersInitialValue,
            this.parametersTypeBox, false);
        break;
      case 1:
        this.addSignal(false);
        break;
      case 2:
        this.addAttribute(2, this.returnAttributesAccessBox, this.returnAttributesIdentifierText,
            this.returnAttributesInitialValue, this.returnAttributesTypeBox, false);
        break;
      case 3:
        this.addAttribute(3, this.attributesAccessBox, this.attributesIdentifierText, this.attributesInitialValue,
            this.attributesTypeBox, false);
        break;
      case 4:
        this.addMethod(false);
        break;
      default:
        // Should not arrive here
    }
  }

  private void addAttribute(int tabIndex, JComboBox<String> accessBox, JTextField identifierText,
      JTextField initialValue, JComboBox<String> typeBox, boolean modify) {
    String identifier = identifierText.getText();
    if (identifier.isEmpty()) {
      // Identifier is empty
      this.highlightField(identifierText);
      identifierText.requestFocus();
      return;
    }

    if (!TAttribute.isAValidId(identifier, this.checkKeyword, this.checkJavaKeyword, this.checkJavaKeyword)) {
      // Identifier should not be used
      JOptionPane.showMessageDialog(this, "Bad identifier '" + identifier + "': invalid identifier", "Error",
          JOptionPane.ERROR_MESSAGE);
      identifierText.requestFocus();
      return;
    }

    int accessIndex = TAttribute.getAccess((String) accessBox.getSelectedItem());
    if (accessIndex < 0) {
      // Unknown access modifier
      JOptionPane.showMessageDialog(this, "Bad access modifier", "Error", JOptionPane.ERROR_MESSAGE);
      accessBox.requestFocus();
      return;
    }

    int typeIndex = TAttribute.getAvatarType((String) typeBox.getSelectedItem());
    if (typeIndex < 0) {
      // Unknown type
      JOptionPane.showMessageDialog(this, "Bad type", "Error", JOptionPane.ERROR_MESSAGE);
      typeBox.requestFocus();
      return;
    }

    String value = initialValue.getText().trim();
    if (typeIndex == TAttribute.ARRAY_NAT && value.isEmpty())
      value = "2";
    if (!initialValue.isEnabled())
      value = "";

    if (!TAttribute.isAValidInitialValue(typeIndex, value)) {
      // Bad initial value
      JOptionPane.showMessageDialog(this, "The initial value is not valid", "Error", JOptionPane.ERROR_MESSAGE);
      initialValue.requestFocus();
      return;
    }

    TAttribute a;
    if (typeIndex == TAttribute.OTHER)
      a = new TAttribute(accessIndex, identifier, value, (String) typeBox.getSelectedItem());
    else
      a = new TAttribute(accessIndex, identifier, value, typeIndex);
    a.isAvatar = true;

    int index;
    Object old = null;
    if (modify) {
      index = this.listAttribute.get(tabIndex).getSelectedIndex();
      old = this.attributes.get(tabIndex).remove(index);
    } else
      index = this.attributes.get(tabIndex).size();

    // checks whether an attribute with this identifier already belongs to the list
    if (this.attributes.get(0).contains(a) || this.attributes.get(2).contains(a)
        || this.attributes.get(3).contains(a)) {
      if (modify)
        this.attributes.get(tabIndex).add(index, old);
      JOptionPane.showMessageDialog(this, "Bad Identifier: another attribute or parameter already has the same name.",
          "Error", JOptionPane.ERROR_MESSAGE);
      identifierText.requestFocus();
      return;
    }

    this.attributes.get(tabIndex).add(index, a);
    this.listAttribute.get(tabIndex).setListData(this.attributes.get(tabIndex).toArray());
    this.listAttribute.get(tabIndex).setSelectedIndex(index);
    this.listAttribute.get(tabIndex).requestFocus();
  }

  private void addMethod(boolean modify) {
    String s = this.methodText.getText().trim();
    if (s.isEmpty()) {
      this.methodText.requestFocus();
      return;
    }

    AvatarMethod am = AvatarMethod.isAValidMethod(s);

    if (am == null) {
      JOptionPane.showMessageDialog(this, "Badly formatted method declaration", "Error", JOptionPane.ERROR_MESSAGE);
      this.methodText.requestFocus();
      return;
    }

    am.setImplementationProvided(false);

    int index;
    Object old = null;
    if (modify) {
      index = this.listAttribute.get(4).getSelectedIndex();
      old = this.attributes.get(4).remove(index);
    } else
      index = this.attributes.get(4).size();

    // Checks whether the same method already belongs to the list
    if (this.attributes.get(4).contains(am)) {
      if (modify)
        this.attributes.get(4).add(index, old);
      JOptionPane.showMessageDialog(this, "This method already exists", "Error", JOptionPane.ERROR_MESSAGE);
      this.methodText.requestFocus();
      return;
    }

    this.attributes.get(4).add(index, am);
    this.listAttribute.get(4).setListData(this.attributes.get(4).toArray());
    this.listAttribute.get(4).setSelectedIndex(index);
    this.listAttribute.get(4).requestFocus();
  }

  private void addSignal(boolean modify) {
    String s = this.signalText.getText().trim();
    if (s.isEmpty()) {
      this.signalText.requestFocus();
      return;
    }

    AvatarSignal as = AvatarSignal.isAValidSignal(this.signalInOutBox.getSelectedIndex(), s);

    if (as == null) {
      JOptionPane.showMessageDialog(this, "Badly formatted signal declaration", "Error", JOptionPane.ERROR_MESSAGE);
      this.signalText.requestFocus();
      return;
    }

    int index;
    Object old = null;
    if (modify) {
      index = this.listAttribute.get(1).getSelectedIndex();
      old = this.attributes.get(1).remove(index);
    } else
      index = this.attributes.get(1).size();

    // Checks whether the same signal already belongs to the list
    if (this.attributes.get(1).contains(as)) {
      if (modify)
        this.attributes.get(1).add(index, old);
      JOptionPane.showMessageDialog(this, "This signal already exists", "Error", JOptionPane.ERROR_MESSAGE);
      this.signalText.requestFocus();
      return;
    }

    this.attributes.get(1).add(index, as);
    this.listAttribute.get(1).setListData(this.attributes.get(1).toArray());
    this.listAttribute.get(1).setSelectedIndex(index);
    this.listAttribute.get(1).requestFocus();
  }

  private void handleModify() {
    int selectedTab = this.tabbedPane.getSelectedIndex();
    switch (selectedTab) {
      case 0:
        this.addAttribute(0, this.parametersAccessBox, this.parametersIdentifierText, this.parametersInitialValue,
            this.parametersTypeBox, true);
        break;
      case 1:
        this.addSignal(true);
        break;
      case 2:
        this.addAttribute(2, this.returnAttributesAccessBox, this.returnAttributesIdentifierText,
            this.returnAttributesInitialValue, this.returnAttributesTypeBox, true);
        break;
      case 3:
        this.addAttribute(3, this.attributesAccessBox, this.attributesIdentifierText, this.attributesInitialValue,
            this.attributesTypeBox, true);
        break;
      case 4:
        this.addMethod(true);
        break;
      default:
        // Should not arrive here
    }
  }

  private void handleUp() {
    int selectedTab = this.tabbedPane.getSelectedIndex();
    int i = this.listAttribute.get(selectedTab).getSelectedIndex();
    if (i != -1 && i != 0) {
      Collections.swap(this.attributes.get(selectedTab), i, i - 1);
      this.listAttribute.get(selectedTab).setListData(this.attributes.get(selectedTab).toArray());
      this.listAttribute.get(selectedTab).setSelectedIndex(i - 1);
    }
  }

  private void handleDown() {
    int selectedTab = this.tabbedPane.getSelectedIndex();
    int i = this.listAttribute.get(selectedTab).getSelectedIndex();
    if (i != -1 && i != this.attributes.get(selectedTab).size() - 1) {
      Collections.swap(this.attributes.get(selectedTab), i, i + 1);
      this.listAttribute.get(selectedTab).setListData(this.attributes.get(selectedTab).toArray());
      this.listAttribute.get(selectedTab).setSelectedIndex(i + 1);
    }
  }

  private void handleRemove() {
    int selectedTab = this.tabbedPane.getSelectedIndex();
    switch (selectedTab) {
      case 0:
        this.removeAttribute(0);
        break;
      case 1:
        this.removeSignal();
        break;
      case 2:
        this.removeAttribute(2);
        break;
      case 3:
        this.removeAttribute(3);
        break;
      case 4:
        this.removeMethod();
        break;
      default:
        // Should not arrive here
    }
  }

  private void removeAttribute(int tabIndex) {
    int i = this.listAttribute.get(tabIndex).getSelectedIndex();
    if (i != -1) {
      ((TAttribute) this.attributes.get(tabIndex).get(i)).setAccess(-1);
      this.attributes.get(tabIndex).remove(i);
      this.listAttribute.get(tabIndex).setListData(this.attributes.get(tabIndex).toArray());
    }
  }

  private void removeSignal() {
    int i = this.listAttribute.get(1).getSelectedIndex();
    if (i != -1) {
      this.attributes.get(1).remove(i);
      this.listAttribute.get(1).setListData(this.attributes.get(1).toArray());
    }
  }

  private void removeMethod() {
    int i = this.listAttribute.get(4).getSelectedIndex();
    if (i != -1) {
      this.attributes.get(4).remove(i);
      this.listAttribute.get(4).setListData(this.attributes.get(4).toArray());
    }
  }

  public void valueChanged(ListSelectionEvent e) {
    int selectedTab = this.tabbedPane.getSelectedIndex();
    int i = this.listAttribute.get(selectedTab).getSelectedIndex();

    this.removeButtons[selectedTab].setEnabled(i != -1);
    this.modifyButtons[selectedTab].setEnabled(i != -1);
    this.upButtons[selectedTab].setEnabled(i > 0);
    this.downButtons[selectedTab].setEnabled(i != -1 && i < this.attributes.get(selectedTab).size() - 1);

    if (selectedTab == 1) { // Signals
      if (i == -1)
        this.signalText.setText("");
      else {
        AvatarSignal as = (AvatarSignal) (this.attributes.get(1).get(i));
        this.signalText.setText(as.toBasicString());
        this.signalInOutBox.setSelectedIndex(as.getInOut());
      }
    } else if (selectedTab == 4) { // Methods
      if (i == -1)
        this.methodText.setText("");
      else {
        AvatarMethod am = (AvatarMethod) (this.attributes.get(4).get(i));
        this.methodText.setText(am.toString());
      }
    } else { // Attributes
      JTextField textField, initialValue;
      JComboBox<String> accessBox, typeBox;

      if (selectedTab == 0) {
        textField = this.parametersIdentifierText;
        initialValue = this.parametersInitialValue;
        accessBox = this.parametersAccessBox;
        typeBox = this.parametersTypeBox;
      } else if (selectedTab == 2) {
        textField = this.returnAttributesIdentifierText;
        initialValue = this.returnAttributesInitialValue;
        accessBox = this.returnAttributesAccessBox;
        typeBox = this.returnAttributesTypeBox;
      } else {
        textField = this.attributesIdentifierText;
        initialValue = this.attributesInitialValue;
        accessBox = this.attributesAccessBox;
        typeBox = this.attributesTypeBox;
      }

      if (i == -1) {
        textField.setText("");
        initialValue.setText("");
        accessBox.setSelectedIndex(0);
        typeBox.setSelectedIndex(0);
      } else {
        TAttribute a = (TAttribute) (this.attributes.get(selectedTab).get(i));
        textField.setText(a.getId());
        initialValue.setText(a.getInitialValue());
        this.select(accessBox, TAttribute.getStringAccess(a.getAccess()));
        if (a.getType() == TAttribute.OTHER)
          this.select(typeBox, a.getTypeOther());
        else
          this.select(typeBox, TAttribute.getStringAvatarType(a.getType()));
      }
    }
  }

  public void addAccess(String s) {
    this.parametersAccessBox.addItem(s);
    this.attributesAccessBox.addItem(s);
    this.returnAttributesAccessBox.addItem(s);
  }

  public void addType(String s) {
    this.parametersTypeBox.addItem(s);
    this.attributesTypeBox.addItem(s);
    this.returnAttributesTypeBox.addItem(s);
  }

  public void select(JComboBox<String> jcb, String text) {
    for (int i = 0; i < jcb.getItemCount(); i++) {
      String s = jcb.getItemAt(i);
      if (s.equals(text)) {
        jcb.setSelectedIndex(i);
        return;
      }
    }
  }
}
