/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille,
 * Nokia Bell Labs France, Andrea ENRICI
 *
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT nokia.com
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ui.TGComponent;
import ui.tmldd.TMLArchiMemoryNode;
import ui.tmldd.TMLArchiPortArtifact;


/**
 * Class JDialogTMLTaskArtifact
 * Dialog for managing artifact to map ports onto CPs
 * Creation: 19/09/2007
 * @version 1.0 19/09/2007
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class JDialogPortArtifact extends JDialogBase implements ActionListener  {

    private boolean regularClose;
    private boolean emptyPortsList = false;

    private JPanel panel2;
    //private Frame frame;
    private TMLArchiPortArtifact artifact;
    private String mappedMemory = "VOID";

    protected JComboBox<String> mappedPortCB, memoryCB;
    protected JTextField baseAddressTF, numSamplesTF, bitsPerSymbolTF;
    protected String baseAddress, mappedPort, sampleLength, numSamples, bitsPerSymbol;
    protected String bank, dataType, symmetricalValue;
    protected JComboBox<String> dataTypeCB, bankCB, symmetricalValueCB;

    //Intl Data In
    protected JTextField widthIntl_TF, bitInOffsetIntl_TF, inputOffsetIntl_TF;
    protected String widthIntl, bitInOffsetIntl, inputOffsetIntl, packedBinaryInIntl;
    protected JComboBox<String> packedBinaryInIntl_CB;

    //Intl Data Out
    protected JTextField bitOutOffsetIntl_TF, outputOffsetIntl_TF;
    protected JComboBox<String> packedBinaryOutIntl_CB;
    protected String packedBinaryOutIntl, bitOutOffsetIntl, outputOffsetIntl;

    //Intl Perm
    protected JTextField lengthPermIntl_TF, offsetPermIntl_TF;
    protected String lengthPermIntl, offsetPermIntl;

    //Mapper Data In
    protected JTextField baseAddressDataInMapp_TF, numSamplesDataInMapp_TF, bitsPerSymbolDataInMapp_TF;
    protected String baseAddressDataInMapp, numSamplesDataInMapp, bitsPerSymbolDataInMapp, symmetricalValueDataInMapp;
    protected JComboBox<String> symmetricalValueDataInMapp_CB;
    //Mapper Data Out
    protected JTextField baseAddressDataOutMapp_TF;
    protected String baseAddressDataOutMapp;
    //Mapper LUT
    protected JTextField baseAddressLUTMapp_TF;
    protected String baseAddressLUTMapp;

    //Code generation
    private JPanel panel3;//, panel4, panel5;
    //private JTabbedPane tabbedPane;
    //private String HALUnitName = "";
    private Vector<String> portsList;
    //private String appName = "";

    /** Creates new form  */
    public JDialogPortArtifact(Frame _frame, String _title, TMLArchiPortArtifact _artifact, String _mappedMemory, Vector<String> _portsList, String _mappedPort ) {
        super(_frame, _title, true);
        //frame = _frame;
        artifact = _artifact;
        mappedMemory = _mappedMemory;
        portsList = _portsList;
        mappedPort = _mappedPort;
        //appName = mappedPort.split("::")[0];
        initComponents();
        pack();
    }

    private void initComponents() {

        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        //GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Artifact attributes"));
        panel2.setPreferredSize(new Dimension(650, 350));

        panel3 = new JPanel();
        panel3.setLayout(gridbag2);
        panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: memory configuration"));
        panel3.setPreferredSize(new Dimension(650, 350));

        // Issue #41 Ordering of tabbed panes
        // tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();
        //panel4 = new JPanel();
        //   panel5 = new JPanel();

        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        int mappedPortIndex;
        if( portsList.size() == 0 ) {
            portsList.add( "No available port" );
            mappedPortIndex = 0;
        }
        else    {
            mappedPortIndex = portsList.indexOf( mappedPort );
        }

        mappedPortCB = new JComboBox<String>( portsList );
        mappedPortCB.setSelectedIndex( mappedPortIndex );
        panel2.add( new JLabel( "Port:" ), c2 );
        mappedPortCB.addActionListener(this);
        panel2.add( mappedPortCB, c1 );

        //Make the list of memories that are available for being mapped
        List<TGComponent> componentList = artifact.getTDiagramPanel().getComponentList();
        Vector<String> memoryList = new Vector<String>();
        for( int k = 0; k < componentList.size(); k++ ) {
            if( componentList.get(k) instanceof TMLArchiMemoryNode )    {
                memoryList.add( componentList.get(k).getName() );
            }
        }
        if( memoryList.size() == 0 )    { // In case there are no memories in the design
            memoryList.add( "No available memory" );
        }

        memoryCB = new JComboBox<String>( memoryList );
        if( !mappedMemory.equals( "VOID" ) && !mappedMemory.equals( "" ) )      {
            memoryCB.setSelectedIndex( memoryList.indexOf( mappedMemory ) );
        }
        else    {
            memoryCB.setSelectedIndex( 0 );
        }
        panel2.add( new JLabel( "Memory: "),  c2 );
        memoryCB.addActionListener(this);
        panel2.add( memoryCB, c1 );

        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.BOTH;
        c.add( panel2, c0 );

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;

        initButtons(c0, c, this);
    }
    //
    //  private String getBufferTypeFromSelectedMemory( String mappedMemory )   {
    //
    //          List<TGComponent> componentList = artifact.getTDiagramPanel().getComponentList();
    //          //Vector<String> list = new Vector<String>();
    //
    //          for( int k = 0; k < componentList.size(); k++ ) {
    //                  if( componentList.get(k) instanceof TMLArchiMemoryNode )        {
    //                          TMLArchiMemoryNode memoryNode = (TMLArchiMemoryNode)componentList.get(k);
    //                          if( memoryNode.getName().equals( mappedMemory ) )       {
    //                                  return memoryNode.getName();
    //                          }
    //                  }
    //          }
    //          return "NO MEC";        //default: the main memory buffer
    //  }

    public void actionPerformed(ActionEvent evt)  {

        /*if( evt.getSource() == memoryCB )     {
          updateBufferPanel();
          }*/
        String command = evt.getActionCommand();
        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
    }
    //
    //  private void updateBufferPanel()        {
    //
    //          GridBagConstraints c1 = new GridBagConstraints();
    //          GridBagConstraints c2 = new GridBagConstraints();
    //
    //          c1.gridwidth = 1;
    //          c1.gridheight = 1;
    //          c1.weighty = 1.0;
    //          c1.weightx = 1.0;
    //          c1.fill = GridBagConstraints.HORIZONTAL;
    //    c1.gridwidth = GridBagConstraints.REMAINDER; //end row
    //
    //          //flushBuffersStrings();
    //          HALUnitName = getBufferTypeFromSelectedMemory( (String)memoryCB.getItemAt( memoryCB.getSelectedIndex() ) );
    //          ArrayList<JPanel> panelsList;
    //
    //          switch( HALUnitName )   {
    //                  case "FEP_BUFFER":
    //                          tabbedPane.removeAll();
    //                          panelsList = FepBuffer.makePanel( c1, c2 );
    //                          panel3 = panelsList.get(0);
    //                          tabbedPane.addTab( "Data", panel3 );
    //                          break;
    //                  case "MAPPER_BUFFER":
    //                          tabbedPane.removeAll();
    //                          panelsList = MapperBuffer.makePanel( c1, c2 );
    //                          tabbedPane.addTab( "Data In", panelsList.get(0) );
    //                          tabbedPane.addTab( "Data Out", panelsList.get(1) );
    //                          tabbedPane.addTab( "Look Up Table", panelsList.get(2) );
    //                          tabbedPane.setSelectedIndex(0);
    //                          break;
    //                  case "ADAIF_BUFFER":
    //                          tabbedPane.removeAll();
    //                          panelsList = AdaifBuffer.makePanel( c1, c2 );
    //                          panel3 = panelsList.get(0);
    //                          tabbedPane.addTab( "Data", panel3 );
    //                          break;
    //                  case "INTERLEAVER_BUFFER":
    //                          tabbedPane.removeAll();
    //                          panelsList = InterleaverBuffer.makePanel( c1, c2 );
    //                          tabbedPane.addTab( "Data In", panelsList.get(0) );
    //                          tabbedPane.addTab( "Data Out", panelsList.get(1) );
    //                          tabbedPane.addTab( "Permutation Table", panelsList.get(2) );
    //                          tabbedPane.setSelectedIndex(0);
    //                          break;
    //                  case "MAIN_MEMORY_BUFFER":
    //                          tabbedPane.removeAll();
    //                          panelsList = MMBuffer.makePanel( c1, c2 );
    //                          panel3 = panelsList.get(0);
    //                          tabbedPane.addTab( "Data", panel3 );
    //                          break;
    //                  default:        //the main memory buffer
    //                          tabbedPane.removeAll();
    //                          panelsList = FepBuffer.makePanel( c1, c2 );
    //                          panel3 = panelsList.get(0);
    //                          tabbedPane.addTab( "Data", panel3 );
    //                          break;
    //          }
    //  }

    public void closeDialog() {
        regularClose = true;
        mappedMemory = memoryCB.getItemAt( memoryCB.getSelectedIndex() );
        //        HALUnitName = getBufferTypeFromSelectedMemory(memoryCB.getItemAt( memoryCB.getSelectedIndex() ));
        //        switch ( HALUnitName )        {
        //                                      case "FEP_BUFFER":
        //                                              if( !FepBuffer.closePanel( frame ) )    {
        //                                                      return;
        //                                              }
        //                                              break;
        //                                      case "MAPPER_BUFFER":
        //                                              if( !MapperBuffer.closePanel( frame ) ) {
        //                                                      return;
        //                                              }
        //                                              break;
        //                                      case "ADAIF_BUFFER":
        //                                              if( !AdaifBuffer.closePanel( frame ) )  {
        //                                                      return;
        //                                              }
        //                                              break;
        //                                      case "INTERLEAVER_BUFFER":
        //                                              if( !InterleaverBuffer.closePanel( frame ) )    {
        //                                                      return;
        //                                              }
        //                                              break;
        //                                      case "MAIN_MEMORY_BUFFER":
        //                                              if( !MMBuffer.closePanel( frame ) )     {
        //                                                      return;
        //                                              }
        //                                              break;
        //                                      default:        //the main memory buffer
        //                                              if( !FepBuffer.closePanel( frame ) )    {
        //                                                      return;
        //                                              }
        //                                              break;
        //                              }
        dispose();
    }

    public String getMappedPort()       {
        return mappedPort;
    }

    public String getMappedMemory()     {
        return mappedMemory;
    }

    public String getStartAddress()     {
        return baseAddress;
    }

    public void cancelDialog() {
        dispose();
    }

    public boolean isRegularClose() {
        return regularClose;
    }

    public String getReferenceCommunicationName() {
        if (emptyPortsList) {
            return null;
        }
        String tmp = (String)( mappedPortCB.getSelectedItem() );
        if( tmp.length() > 0 )    {
            int index = tmp.indexOf("::");
            if (index == -1) {
                return tmp;
            }
            return tmp.substring(0, index);
        }
        else    {
            return "ERROR EMPTY PORT NAME";
        }
    }

    public String getCommunicationName() {
        String tmp = (String)( mappedPortCB.getSelectedItem() );
        int index = tmp.indexOf("::");
        if (index == -1) {
            return tmp;
        }
        tmp = tmp.substring(index+2, tmp.length());

        index =  tmp.indexOf("(");
        if (index > -1) {
            tmp = tmp.substring(0, index).trim();
        }
        return tmp;
    }

    public String getTypeName() {
        String tmp = (String)( mappedPortCB.getSelectedItem() );
        int index1 = tmp.indexOf("(");
        int index2 = tmp.indexOf(")");
        if ((index1 > -1) && (index2 > index1)) {
            return tmp.substring(index1+1, index2);
        }
        return "";
    }

    public int indexOf(Vector<String> _list, String name) {
        int i = 0;
        for(String s : _list) {
            if (s.equals(name)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    //  public List<String> getBufferParameters()       {
    //
    //          List<String> params = new ArrayList<String>();
    //params.add( String.valueOf( HALUnitName ) );
    //          switch( HALUnitName )   {
    //                  case "FEP_BUFFER":
    //                          params = FepBuffer.getBufferParameters();
    //                          break;
    //            case "INTERLEAVER_BUFFER":
    //                          params = InterleaverBuffer.getBufferParameters();
    //                          break;
    //                  case "ADAIF_BUFFER":
    //                          params = AdaifBuffer.getBufferParameters();
    //                          break;
    //            case "MAPPER_BUFFER":
    //                          params = MapperBuffer.getBufferParameters();
    //                          break;
    //                  case "MAIN_MEMORY_BUFFER":
    //                          params = MMBuffer.getBufferParameters();
    //                          break;
    //                  default:        //the main memory buffer
    //                          params = FepBuffer.getBufferParameters();
    //                          break;
    //          }
    //          return params;
    //  }
    //
    //  private void cleanPanels()      {
    //          panel3.removeAll();
    //          panel4.removeAll();
    //          panel5.removeAll();
    //          tabbedPane.removeAll();
    //  }

    //  private void revalidateAndRepaintPanels()       {
    //          panel3.revalidate();
    //          panel3.repaint();
    //          panel4.revalidate();
    //          panel4.repaint();
    //          panel5.revalidate();
    //          panel5.repaint();
    //  }

}       //End of class
