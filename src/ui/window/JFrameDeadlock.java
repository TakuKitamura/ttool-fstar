/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class JFrameDeadlock
 * Creation: 15/09/2004
 * version 1.0 15/09/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

//import java.io.*;
import javax.swing.*;
//import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
//import java.util.*;
import ui.graph.*;


import myutil.*;
import ui.*;


public	class JFrameDeadlock extends JFrame implements ActionListener {
    private String data;
    //private int nbState;
    //private int nbTransition;
    
    //private JStatisticsPanel jstat;
    private JScrollPane jsp;
    private JTextField state;
    private JTextField transition;
    
    public JFrameDeadlock(String title, String dataAUT) {
        super(title);
        data = dataAUT;
        makeComponents();
    }
    
    public void makeComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        
        AUTGraph graph = new AUTGraph();
        graph.buildGraph(data);
        DeadlockTableModel tm = new DeadlockTableModel(graph, 25000);
        TableSorter sorter = new TableSorter(tm);
        JTable jtable = new JTable(sorter);             
        sorter.setTableHeader(jtable.getTableHeader()); 
        
        ((jtable.getColumnModel()).getColumn(0)).setPreferredWidth(Math.max(maxLengthColumn(framePanel, tm, 0) + 20, 50));
        ((jtable.getColumnModel()).getColumn(1)).setPreferredWidth(Math.max(maxLengthColumn(framePanel, tm, 1) + 15, 100));
        ((jtable.getColumnModel()).getColumn(2)).setPreferredWidth(Math.max(maxLengthColumn(framePanel, tm, 1) + 15, 400));
        jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        jsp = new JScrollPane(jtable);
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(10);
        //jsp.setMaximumSize(new Dimension(250, 50));
        
        framePanel.add(jsp, BorderLayout.CENTER);
        
        // Buttons
        
        JButton button1 = new JButton("Close", IconManager.imgic27);
        button1.addActionListener(this);
        JPanel jp = new JPanel();
        jp.add(button1);
        
        framePanel.add(jp, BorderLayout.SOUTH);
        
        // upper information
        Point p = FormatManager.nbStateTransitionRGAldebaran(data);
        jp = new JPanel();
        
        jp.add(new JLabel("States:"));
        state = new JTextField(5);
        state.setEditable(false);
        state.setText(String.valueOf(p.x));
        jp.add(state);
        
        jp.add(new JLabel("Transitions:"));
        transition = new JTextField(15);
        transition.setEditable(false);
        transition.setText(String.valueOf(p.y));
        jp.add(transition);
        
        framePanel.add(jp, BorderLayout.NORTH);
        
        pack();
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        //System.out.println("Command:" + command);
        
        if (command.equals("Close")) {
            dispose();
            return;
        }
    }
    
    private int maxLengthColumn(Component c, AbstractTableModel tm, int index) {
        int w = 0, wtmp;
        FontMetrics fm = c.getFontMetrics(c.getFont());
        if (fm == null) {
            return 0;
        }
        
        String s;
        
        for(int i=0; i<tm.getRowCount(); i++) {
            s = tm.getValueAt(i, index).toString();
            wtmp = fm.stringWidth(s);
            w = Math.max(w, wtmp);
        }
        return w;
    }
    
} // Class