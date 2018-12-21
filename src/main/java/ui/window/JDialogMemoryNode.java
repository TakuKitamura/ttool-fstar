/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.aptvrille AT enst.fr
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
import tmltranslator.modelcompiler.Buffer;
import ui.tmldd.TMLArchiMemoryNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;


/**
 * Class JDialogMemoryNode
 * Dialog for managing attributes of memory nodes
 * Creation: 23/11/2007
 * @version 1.0 23/11/2007
 * @author Ludovic APVRILLE
 */
public class JDialogMemoryNode extends JDialogBase implements ActionListener  {

    private static String[] tracemodeTab = {"VCI logger", "VCI stats"};

    private boolean regularClose;

    private JPanel panel2, panel3;
 //   private Frame frame;
    private TMLArchiMemoryNode node;

    protected JComboBox<String> tracemode;
    private static int selectedTracemode = 0;

    // Panel1
    protected JTextField nodeName;

    // Panel2
    protected JTextField byteDataSize, memorySize, monitored, clockRatio;

    //Panel3: code generation
    protected int bufferType = 0;       //it is the index in the ArrayList of String
    protected JComboBox<String> bufferTypesCB;

    private JTabbedPane tabbedPane;

    /* Creates new form  */
    public JDialogMemoryNode( Frame _frame, String _title, TMLArchiMemoryNode _node, int _bufferType ) {
        super(_frame, _title, true);
      //  frame = _frame;
        node = _node;
        bufferType = _bufferType;

        initComponents();
        myInitComponents();
        pack();
    }

    private void myInitComponents() {
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
       // GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagLayout gridbag3 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        GridBagConstraints c3 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    	// Issue #41 Ordering of tabbed panes 
        tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();

        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Memory attributes"));
        panel2.setPreferredSize(new Dimension(300, 200));

        panel3 = new JPanel();
        panel3.setLayout(gridbag3);
        panel3.setBorder(new javax.swing.border.TitledBorder("Code generation"));
        panel3.setPreferredSize(new Dimension(300, 200));

        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Memory name:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        nodeName = new JTextField(node.getNodeName(), 30);
        nodeName.setEditable(true);
        nodeName.setFont(new Font("times", Font.PLAIN, 12));
        panel2.add(nodeName, c1);

        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;

        c2.gridwidth = 1;
        panel2.add(new JLabel("Data size (in byte):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        byteDataSize = new JTextField(""+node.getByteDataSize(), 15);
        panel2.add(byteDataSize, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Monitored:"), c2);
        tracemode = new JComboBox<String>(tracemodeTab);
        tracemode.setSelectedIndex(selectedTracemode);
        tracemode.addActionListener(this);
        panel2.add(tracemode, c2);

        monitored = new JTextField("", 15);
        panel2.add(monitored, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Clock divider:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        clockRatio = new JTextField(""+node.getClockRatio(), 15);
        panel2.add(clockRatio, c2);

        //code generation
        c3.gridwidth = 1;
        c3.gridheight = 1;
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        panel3.add(new JLabel("<html>Memory Extension<br>Construct:</html>"), c3);
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        bufferTypesCB = new JComboBox<String>( new Vector<String>( Arrays.asList( Buffer.MEMORY_TYPES ) ) );
        bufferTypesCB.setSelectedIndex( bufferType  );
        panel3.add( bufferTypesCB, c3 );

        c3.gridwidth = 1;
        panel3.add( new JLabel( "Memory size (in byte):" ), c3 );
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        memorySize = new JTextField( "" + node.getMemorySize(), 15 );
        panel3.add( memorySize, c3 );

        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.BOTH;

        tabbedPane.addTab( "Simulation", panel2 );
        tabbedPane.addTab( "Code generation", panel3 );
        tabbedPane.setSelectedIndex(0);
        /*c.add(panel2, c0);
          c.add(panel3, c0);*/
        c.add( tabbedPane, c0 );

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        
        initButtons(c0, c, this);
    }

    public void actionPerformed(ActionEvent evt)  {
        /* if (evt.getSource() == typeBox) {
           boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
           initialValue.setEnabled(b);
           return;
           }*/

    	if (evt.getSource() == tracemode) {
           selectedTracemode = tracemode.getSelectedIndex();                   
        }

        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
    }

    public void closeDialog() {
        regularClose = true;
        bufferType = bufferTypesCB.getSelectedIndex();
        dispose();
    }

    public void cancelDialog() {
        dispose();
    }

    public boolean isRegularClose() {
        return regularClose;
    }

    public String getNodeName() {
        return nodeName.getText();
    }

    public String getByteDataSize() {
        return byteDataSize.getText();
    }

    public String getMemorySize() {
        return memorySize.getText();
    }

    public int getMonitored() {
	return tracemode.getSelectedIndex();
        //return monitored.getText();
    }
    public String getClockRatio() {
        return clockRatio.getText();
    }

    public int getBufferType()  {
        return bufferType;
    }
}
