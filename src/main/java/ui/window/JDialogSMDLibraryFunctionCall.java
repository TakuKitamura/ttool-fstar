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

import ui.AvatarSignal;
import ui.util.IconManager;
import ui.TAttribute;
import ui.avatarbd.AvatarBDLibraryFunction;
import ui.avatarsmd.AvatarSMDLibraryFunctionCall;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;


/**
* Dialog for managing Avatar library function calls in State Machine Diagrams.
* @version 1.0 04.21.2016
* @author Florian LUGOU
*/
public class JDialogSMDLibraryFunctionCall extends JDialogBase implements ActionListener, ListSelectionListener {
    private AvatarSMDLibraryFunctionCall functionCall;
    
    private AvatarBDLibraryFunction libraryFunction;
    private TAttribute[] parameters;
    private AvatarSignal[] signals;
    private TAttribute[] returnAttributes;

    private static class FunctionWrapper {
        AvatarBDLibraryFunction inner;

        FunctionWrapper (AvatarBDLibraryFunction inner) {
            this.inner = inner;
        }

        @Override
        public String toString () {
            if (this.inner == null)
                return "";
            return this.inner.getFullyQualifiedName ();
        }
    }
    JComboBox<FunctionWrapper> functionNameBox;

    private static class AttrSigWrapper {
        Object inner1;
        Object inner2;

        AttrSigWrapper (Object inner1, Object inner2) {
            this.inner1 = inner1;
            this.inner2 = inner2;
        }

        @Override
        public String toString () {
            if (inner2 == null)
                return inner1.toString ();

            return inner1.toString () + "  <--  " + inner2.toString ();
        }
    }
    JList<AttrSigWrapper> listParameters, listSignals, listReturnAttributes;

    JComboBox<Object> parametersComboBox, signalsComboBox, returnAttributesComboBox;

    /**
     * Creates new dialog.
     */
    public JDialogSMDLibraryFunctionCall (AvatarSMDLibraryFunctionCall functionCall, JFrame f, String title) {
        super (f, title, true);

        this.functionCall = functionCall;

        this.libraryFunction = this.functionCall.getLibraryFunction ();
        if (this.libraryFunction != null) {
            this.parameters = new TAttribute [this.libraryFunction.getParameters ().size ()];
            int i=0;
            for (TAttribute attr: this.functionCall.getParameters ())
                this.parameters[i++] = attr;

            this.signals = new AvatarSignal [this.libraryFunction.getSignals ().size ()];
            i=0;
            for (AvatarSignal signal: this.functionCall.getSignals ())
                this.signals[i++] = signal;

            this.returnAttributes = new TAttribute [this.libraryFunction.getReturnAttributes ().size ()];
            i=0;
            for (TAttribute attr: this.functionCall.getReturnAttributes ())
                this.returnAttributes[i++] = attr;
        }

        this.initComponents();
    }

    private void initComponents() {
        this.setFont (new Font("Helvetica", Font.PLAIN, 14));
        this.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);

        Container c = this.getContentPane ();
        c.setLayout (new GridBagLayout());

        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.weighty = 3.0;
        gridConstraints.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridConstraints.fill = GridBagConstraints.BOTH;

        JPanel panelMethod = new JPanel();
        panelMethod.setLayout(new GridBagLayout());
        panelMethod.setBorder(new javax.swing.border.TitledBorder ("Function name"));
        c.add (panelMethod, gridConstraints);

        gridConstraints.weightx = 1.0;
        gridConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridConstraints.gridwidth = 1;
        panelMethod.add(new JLabel("Function:  "), gridConstraints);

        gridConstraints.gridwidth = GridBagConstraints.REMAINDER; //end row
        java.util.List<AvatarBDLibraryFunction> libFunctions = this.functionCall.getTDiagramPanel ().getMGUI ().getAllLibraryFunctions ();
        Collections.sort (libFunctions);
        {
        FunctionWrapper[] l = new FunctionWrapper[libFunctions.size ()+1];
        l[0] = new FunctionWrapper (null);
        int i=1, selected = 0;
        for (AvatarBDLibraryFunction fun: libFunctions) {
            if (fun == this.libraryFunction)
                selected = i;
            l[i++] = new FunctionWrapper (fun);
        }
        this.functionNameBox = new JComboBox<FunctionWrapper> (l);
        this.functionNameBox.setSelectedIndex (selected);
        }
        this.functionNameBox.addActionListener (this);
        panelMethod.add (this.functionNameBox, gridConstraints);

        if (this.libraryFunction != null) {
            // Parameters
            gridConstraints = new GridBagConstraints();
            gridConstraints.weighty = 8.0;
            gridConstraints.gridwidth = GridBagConstraints.REMAINDER; //end row
            gridConstraints.fill = GridBagConstraints.BOTH;

            JPanel panelParameters = new JPanel();
            panelParameters.setLayout(new GridBagLayout());
            panelParameters.setBorder(new javax.swing.border.TitledBorder("Parameters"));
            c.add (panelParameters, gridConstraints);

            LinkedList<TAttribute> expectedParameters = this.libraryFunction.getParameters ();
            if (!expectedParameters.isEmpty ()) {
                AttrSigWrapper[] l = new AttrSigWrapper[expectedParameters.size ()];
                int i=0;
                for (TAttribute inner1: expectedParameters)
                    l[i] = new AttrSigWrapper (inner1, this.parameters[i++]);
                this.listParameters = new JList<AttrSigWrapper> (l);
                this.listParameters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                this.listParameters.setSelectedIndex (0);
                this.listParameters.addListSelectionListener(this);
                JScrollPane scrollPane = new JScrollPane(listParameters);
                scrollPane.setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                gridConstraints.fill = GridBagConstraints.BOTH;
                gridConstraints.gridwidth = 1;
                gridConstraints.gridheight = 5;
                gridConstraints.weightx = 1.0;
                gridConstraints.anchor = GridBagConstraints.NORTH;
                panelParameters.add(scrollPane, gridConstraints);

                this.parametersComboBox = new JComboBox<Object> ();
                this.fillParametersComboBox (expectedParameters.get (0));
                this.parametersComboBox.addActionListener (this);
                gridConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridConstraints.gridwidth = GridBagConstraints.REMAINDER; //end row
                gridConstraints.gridheight = 1;
                gridConstraints.weightx = 0.5;
                panelParameters.add (this.parametersComboBox, gridConstraints);
            }

            // Signals
            gridConstraints = new GridBagConstraints();
            gridConstraints.weighty = 8.0;
            gridConstraints.gridwidth = GridBagConstraints.REMAINDER; //end row
            gridConstraints.fill = GridBagConstraints.BOTH;

            JPanel panelSignals = new JPanel();
            panelSignals.setLayout(new GridBagLayout());
            panelSignals.setBorder(new javax.swing.border.TitledBorder("Signals"));
            c.add (panelSignals, gridConstraints);

            LinkedList<AvatarSignal> expectedSignals = this.libraryFunction.getSignals ();
            if (!expectedSignals.isEmpty ()) {
                AttrSigWrapper[] l = new AttrSigWrapper[expectedSignals.size ()];
                int i=0;
                for (AvatarSignal inner1: expectedSignals)
                    l[i] = new AttrSigWrapper (inner1, this.signals[i++]);
                this.listSignals = new JList<AttrSigWrapper> (l);
                this.listSignals.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                this.listSignals.setSelectedIndex (0);
                this.listSignals.addListSelectionListener(this);
                JScrollPane scrollPane = new JScrollPane(listSignals);
                scrollPane.setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                gridConstraints.fill = GridBagConstraints.BOTH;
                gridConstraints.gridwidth = 1;
                gridConstraints.gridheight = 5;
                gridConstraints.weightx = 1.0;
                gridConstraints.anchor = GridBagConstraints.NORTH;
                panelSignals.add(scrollPane, gridConstraints);

                this.signalsComboBox = new JComboBox<Object> ();
                this.fillSignalsComboBox (expectedSignals.get (0));
                this.signalsComboBox.addActionListener (this);
                gridConstraints.gridwidth = GridBagConstraints.REMAINDER; //end row
                gridConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridConstraints.gridheight = 1;
                gridConstraints.gridy = 0;
                panelSignals.add (this.signalsComboBox, gridConstraints);
            }

            // Return Values
            gridConstraints = new GridBagConstraints();
            gridConstraints.weighty = 8.0;
            gridConstraints.gridwidth = GridBagConstraints.REMAINDER; //end row
            gridConstraints.fill = GridBagConstraints.BOTH;

            JPanel panelReturnAttributes = new JPanel();
            panelReturnAttributes.setLayout(new GridBagLayout());
            panelReturnAttributes.setBorder(new javax.swing.border.TitledBorder("Return Values"));
            c.add (panelReturnAttributes, gridConstraints);

            LinkedList<TAttribute> expectedReturnAttributes = this.libraryFunction.getReturnAttributes ();
            if (!expectedReturnAttributes.isEmpty ()) {
                AttrSigWrapper[] l = new AttrSigWrapper[expectedReturnAttributes.size ()];
                int i=0;
                for (TAttribute inner1: expectedReturnAttributes)
                    l[i] = new AttrSigWrapper (inner1, this.returnAttributes[i++]);
                this.listReturnAttributes = new JList<AttrSigWrapper> (l);
                this.listReturnAttributes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                this.listReturnAttributes.setSelectedIndex (0);
                this.listReturnAttributes.addListSelectionListener(this);
                JScrollPane scrollPane = new JScrollPane(listReturnAttributes);
                scrollPane.setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                gridConstraints.fill = GridBagConstraints.BOTH;
                gridConstraints.gridwidth = 1;
                gridConstraints.gridheight = 5;
                gridConstraints.weightx = 1.0;
                gridConstraints.anchor = GridBagConstraints.NORTH;
                panelReturnAttributes.add(scrollPane, gridConstraints);

                this.returnAttributesComboBox = new JComboBox<Object> ();
                this.fillReturnAttributesComboBox (expectedReturnAttributes.get (0));
                this.returnAttributesComboBox.addActionListener (this);
                gridConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridConstraints.gridwidth = GridBagConstraints.REMAINDER; //end row
                gridConstraints.gridheight = 1;
                gridConstraints.gridy = 0;
                panelReturnAttributes.add (this.returnAttributesComboBox, gridConstraints);
            }
        } else {
            gridConstraints = new GridBagConstraints();
            gridConstraints.weighty = 24.0;
            gridConstraints.gridwidth = GridBagConstraints.REMAINDER; //end row
            gridConstraints.fill = GridBagConstraints.BOTH;

            JPanel panelReturnAttributes = new JPanel();
            c.add (panelReturnAttributes, gridConstraints);
        }

        // Add Save & Close button
        gridConstraints = new GridBagConstraints();
        gridConstraints.weightx = 1.0;
        gridConstraints.weighty = 1.0;
        gridConstraints.fill = GridBagConstraints.BOTH;
        
        initButtons(gridConstraints, c, this);
    }

    private void fillParametersComboBox (TAttribute attr) {
        LinkedList<Object> availAttr = new LinkedList<Object> ();
        for (Object o: this.functionCall.getTDiagramPanel ().getMGUI ().getAllAttributes ()) {
            TAttribute ta = (TAttribute) o;
            if (attr.hasSameType (ta))
                availAttr.add (o);
        }

        Object[] l = new Object[availAttr.size ()+1];
        l[0] = new Object () { public String toString () { return ""; } };
        int i=1;
        for (Object inner1: availAttr)
            l[i++] = inner1;

        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>(l);
        this.parametersComboBox.setModel (model);

        if (this.listParameters.getSelectedIndex () < 0)
            this.parametersComboBox.setSelectedIndex (0);
        else {
            Object o = this.listParameters.getSelectedValue ().inner2;
            if (o != null)
                this.parametersComboBox.setSelectedItem (o);
            else
                this.parametersComboBox.setSelectedIndex (0);
        }
    }

    private void fillSignalsComboBox (AvatarSignal signal) {
        LinkedList<Object> availSignals = new LinkedList<Object> ();
        for (Object o: this.functionCall.getTDiagramPanel ().getMGUI ().getAllSignals ()) {
            AvatarSignal otherSignal = (AvatarSignal) o;
            if (signal.hasSamePrototype (otherSignal) && signal.getInOut () == otherSignal.getInOut ())
                availSignals.add (o);
        }

        Object[] l = new Object[availSignals.size ()+1];
        l[0] = new Object () { public String toString () { return ""; } };
        int i=1;
        for (Object inner1: availSignals)
            l[i++] = inner1;

        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>(l);
        this.signalsComboBox.setModel (model);

        if (this.listSignals.getSelectedIndex () < 0)
            this.signalsComboBox.setSelectedIndex (0);
        else {
            Object o = this.listSignals.getSelectedValue ().inner2;
            if (o != null)
                this.signalsComboBox.setSelectedItem (o);
            else
                this.signalsComboBox.setSelectedIndex (0);
        }
    }

    private void fillReturnAttributesComboBox (TAttribute attr) {
        LinkedList<Object> availAttr = new LinkedList<Object> ();
        for (Object o: this.functionCall.getTDiagramPanel ().getMGUI ().getAllAttributes ()) {
            TAttribute ta = (TAttribute) o;
            if (attr.hasSameType (ta))
                availAttr.add (o);
        }

        Object[] l = new Object[availAttr.size ()+1];
        l[0] = new Object () { public String toString () { return ""; } };
        int i=1;
        for (Object inner1: availAttr)
            l[i++] = inner1;

        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>(l);
        this.returnAttributesComboBox.setModel (model);

        if (this.listReturnAttributes.getSelectedIndex () < 0)
            this.returnAttributesComboBox.setSelectedIndex (0);
        else {
            Object o = this.listReturnAttributes.getSelectedValue ().inner2;
            if (o != null)
                this.returnAttributesComboBox.setSelectedItem (o);
            else
                this.returnAttributesComboBox.setSelectedIndex (0);
        }
    }

    public void actionPerformed(ActionEvent evt)  {
        // Compare the action command to the known actions.
        if (evt.getSource() == this.closeButton) {
            this.save ();
            this.dispose ();
        } else if (evt.getSource() == this.cancelButton)
            this.dispose ();
        else if (evt.getSource () == this.functionNameBox)
            this.functionSelected ();
        else if (evt.getSource () == this.parametersComboBox)
            this.selectParameterValue ();
        else if (evt.getSource () == this.signalsComboBox)
            this.selectSignalValue ();
        else if (evt.getSource () == this.returnAttributesComboBox)
            this.selectReturnAttributeValue ();
    }

    private void save () {
        this.functionCall.setLibraryFunction (this.libraryFunction);
        this.functionCall.setParameters (new LinkedList<TAttribute> (Arrays.asList(this.parameters)));
        this.functionCall.setSignals (new LinkedList<AvatarSignal> (Arrays.asList(this.signals)));
        this.functionCall.setReturnAttributes (new LinkedList<TAttribute> (Arrays.asList(this.returnAttributes)));
    }

    private void functionSelected () {
        FunctionWrapper wrap = (FunctionWrapper) this.functionNameBox.getSelectedItem ();
        if (wrap.inner == this.libraryFunction)
            return;

        this.libraryFunction = wrap.inner;

        this.parameters = new TAttribute [this.libraryFunction.getParameters ().size ()];
        this.signals = new AvatarSignal [this.libraryFunction.getSignals ().size ()];
        this.returnAttributes = new TAttribute [this.libraryFunction.getReturnAttributes ().size ()];

        this.getContentPane ().removeAll ();
        this.initComponents ();

        this.revalidate ();
        this.repaint ();
    }

    private void selectParameterValue () {
        if (this.libraryFunction == null)
            return;

        int i = this.listParameters.getSelectedIndex ();
        if (i < 0)
            return;

        Object o = this.parametersComboBox.getSelectedItem ();
        TAttribute ta;
        if (o instanceof TAttribute)
            ta = (TAttribute) o;
        else
            ta = null;

        this.parameters[i] = ta;

        LinkedList<TAttribute> expectedParameters = this.libraryFunction.getParameters ();
        AttrSigWrapper[] l = new AttrSigWrapper[expectedParameters.size ()];
        int j=0;
        for (TAttribute inner1: expectedParameters)
            l[j] = new AttrSigWrapper (inner1, this.parameters[j++]);

        this.listParameters.setListData(l);
        this.listParameters.setSelectedIndex (i);
        // this.listParameters.addListSelectionListener(this);
    }

    private void selectSignalValue () {
        if (this.libraryFunction == null)
            return;

        int i = this.listSignals.getSelectedIndex ();
        if (i < 0)
            return;

        Object o = this.signalsComboBox.getSelectedItem ();
        AvatarSignal sig;
        if (o instanceof AvatarSignal)
            sig = (AvatarSignal) o;
        else
            sig = null;

        this.signals[i] = sig;

        LinkedList<AvatarSignal> expectedSignals = this.libraryFunction.getSignals ();
        AttrSigWrapper[] l = new AttrSigWrapper[expectedSignals.size ()];
        int j=0;
        for (AvatarSignal inner1: expectedSignals)
            l[j] = new AttrSigWrapper (inner1, this.signals[j++]);

        this.listSignals.setListData(l);
        this.listSignals.setSelectedIndex (i);
        // this.listSignals.addListSelectionListener(this);
    }

    private void selectReturnAttributeValue () {
        if (this.libraryFunction == null)
            return;

        int i = this.listReturnAttributes.getSelectedIndex ();
        if (i < 0)
            return;

        Object o = this.returnAttributesComboBox.getSelectedItem ();
        TAttribute ta;
        if (o instanceof TAttribute)
            ta = (TAttribute) o;
        else
            ta = null;

        this.returnAttributes[i] = ta;

        LinkedList<TAttribute> expectedReturnAttributes = this.libraryFunction.getReturnAttributes ();
        AttrSigWrapper[] l = new AttrSigWrapper[expectedReturnAttributes.size ()];
        int j=0;
        for (TAttribute inner1: expectedReturnAttributes)
            l[j] = new AttrSigWrapper (inner1, this.returnAttributes[j++]);

        this.listReturnAttributes.setListData(l);
        this.listReturnAttributes.setSelectedIndex (i);
        // this.listReturnAttributes.addListSelectionListener(this);
    }

    public void valueChanged (ListSelectionEvent e) {
        if (!(e.getSource() instanceof JList))
            return;

        JList src = (JList) e.getSource ();
        if (src.getSelectedIndex () < 0)
            src.setSelectedIndex (0);

        Object o = src.getSelectedValue ();
        if (o instanceof AttrSigWrapper) {
            o = ((AttrSigWrapper) o).inner1;
            if (src == this.listParameters)
                this.fillParametersComboBox ((TAttribute) o);
            else if (src == this.listSignals)
                this.fillSignalsComboBox ((AvatarSignal) o);
            else
                this.fillReturnAttributesComboBox ((TAttribute) o);
        }
    }
}
