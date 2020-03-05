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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import ui.TURTLEPanel;
import ui.util.IconManager;
/**
 * Class JDialogToChosePanel: provide the names of tabs in dropdown format so user can chose
 * 
 * 
 * 20/02/2020
 *
 * @author Maysam Zoor
 */
public class JDialogToChosePanel extends JDialogBase implements ActionListener, ListSelectionListener {

    private JPanel panel1, panel2, panel3, panel6;
    private JList<String> listIgnored;
    private JList<String> listSelected;
    private JButton allSelected;
    private JButton addOneSelected;
    private JButton addOneIgnored;
    private JButton allIgnored;

    private JComboBox<String> tasksDropDownCombo1 = new JComboBox<String>();

    private Vector<TURTLEPanel> tabs;
    private TURTLEPanel selectedTab;

    private Container c;

    public JDialogToChosePanel(JFrame frame, Vector<TURTLEPanel> allTabs, String title) {

        super(frame, title, true);
        tabs = allTabs;
        c = getContentPane();
        initComponents();
        pack();

        // TODO Auto-generated constructor stub
    }
    @SuppressWarnings("unchecked")
    private void initComponents() {

        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        // setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GridBagConstraints c01 = new GridBagConstraints();

        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 0;
        // c01.fill = GridBagConstraints.BOTH;

        JLabel xmlLabel = new JLabel("Simulation trace as XML File ", JLabel.LEFT);

        panel1.add(xmlLabel, c01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 1;
        c01.gridy = 0;
        tasksDropDownCombo1.addActionListener(this);

        ComboBoxModel[] models = new ComboBoxModel[1];

        models[0] = new DefaultComboBoxModel(loadDropDowns());

        tasksDropDownCombo1.setModel(models[0]);
        panel1.add(tasksDropDownCombo1, c01);
        c.add(panel1, BorderLayout.NORTH);

        // main panel;
        panel6 = new JPanel();
        panel6.setLayout(new FlowLayout());
        closeButton = new JButton("OK", IconManager.imgic37);
        // closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        closeButton.setPreferredSize(new Dimension(200, 30));

        cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        cancelButton.setPreferredSize(new Dimension(200, 30));
        panel6.add(cancelButton);
        panel6.add(closeButton);

        c.add(panel6, BorderLayout.SOUTH);

    }

    public Vector<String> loadDropDowns() {

        Vector<String> allLatencyTasks = new Vector<String>();

        for (int i = 0; i < tabs.size(); i++) {
            allLatencyTasks.add(tabs.get(i).getNameOfTab());

        }

        return allLatencyTasks;

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Compare the action command to the known actions.
        if (e.getSource() == cancelButton) {

            closeDialog();
        } else if (e.getSource() == closeButton) {

            selectedTab = tabs.get(tasksDropDownCombo1.getSelectedIndex());

            dispose();

        }

    }

    public TURTLEPanel getSelectedTab() {
        return selectedTab;
    }

    public void closeDialog() {

        dispose();
    }

}
