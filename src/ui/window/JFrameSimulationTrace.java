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
 * Class JFrameSimulationTrace
 * Creation: 14/12/2003
 * version 1.0 14/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import translator.*;
import ui.*;

public	class JFrameSimulationTrace extends JFrame	implements ActionListener {
    private Vector trace;
    
    private JSimulationPanelInterface jsimu;
    private JScrollPane jsp;
    private JTextField time;
    private JTextField rtlotos;
    private JTextField turtle;
    private JTextField action;
    private JTextField values;
    
    private JButton buttonZL;
    private JButton buttonZP;
    
    private int type = 0; // 1 -> function of time  // 2-> ordering
    
    
    public JFrameSimulationTrace(String title, String _simuData) {
        super(title);
        type = 1;
        makeTraceSimu(_simuData);
        makeComponents();
    }
    
    public JFrameSimulationTrace(String title, String _simuData, int _type) {
        super(title);
        type = _type;
        makeTraceSimu(_simuData);
        makeComponents();
    }
    
    public boolean makeTraceSimu(String simuData) {
        trace = new Vector();
        
        StringReader sr = new StringReader(simuData);
        BufferedReader br = new BufferedReader(sr);
        String s;
        int ind0, ind1, ind2, ind4, ind5;
        String gateId;
        String timeId;
        String actionNumber;
        String values;
        int t, act;
        MasterGateManager mgm = new MasterGateManager();
        Gate g;
        GroupOfGates gog;
        GateSimulationTrace gst;
        
        try {
            while((s = br.readLine()) != null) {
                ind0 = s.indexOf(' ');
                ind1 = s.indexOf('(');
                ind2 = s.indexOf(')');
                if ((ind0 > -1) && (ind1 > ind0) && (ind2 > ind1)) {
                    timeId = s.substring(0, ind0);
                    gateId = s.substring(ind1 + 1, ind2);
                    
                    ind4 = gateId.indexOf('<');
                    ind5 = gateId.indexOf('>');
                    if ((ind4 > -1) && (ind5 > ind4)) {
                        values = gateId.substring(ind4 + 1, ind5);
                        gateId = gateId.substring(0, ind4);
                    } else {
                        values = "";
                    }
                    
                    actionNumber = s.substring(ind2+2);
                    //System.out.println("Action *" + gateId + "* time *" + timeId + "* id*" + gateId + "* num*" + actionNumber);
                    t = Integer.decode(timeId).intValue();
                    act = Integer.decode(actionNumber).intValue();
                    g = mgm.getGate(gateId);
                    
                    if (g != null) {
                        gog = mgm.getGroupOfGatesByGate(g);
                        if (gog != null ) {
                            gst = gstOfGate(g);
                            if (gst == null) {
                                //System.out.println("New GST : " + g.getName() + " gog " + gog.printAll());
                                gst = new GateSimulationTrace(g, gog);
                                trace.add(gst);
                            }
                            gst.addTimeAction(t, act, values);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception when reading simulation trace: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    public GateSimulationTrace gstOfGate(Gate g) {
        GateSimulationTrace gst;
        
        for(int i=0; i<trace.size(); i++) {
            gst = (GateSimulationTrace)(trace.elementAt(i));
            if (gst.getGate() == g) {
                return gst;
            }
        }
        
        return null;
    }
    
    public void makeComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        
        if (type == 1) {
            jsimu = new JSimulationPanel(this, trace);
        } else {
            jsimu = new JSimulationPanelChrono(this, trace);
        }
        jsp = new JScrollPane((JPanel)jsimu);
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(10);
        
        framePanel.add(jsp, BorderLayout.CENTER);
        
        // Buttons
        buttonZL = new JButton("Zoom -", IconManager.imgic315);
        buttonZL.addActionListener(this);
        
        buttonZP = new JButton("Zoom +", IconManager.imgic317);
        buttonZP.addActionListener(this);
        
        JButton button1 = new JButton("Close", IconManager.imgic27);
        button1.addActionListener(this);
        
        JPanel jp = new JPanel();
        jp.add(buttonZL);
        jp.add(buttonZP);
        jp.add(button1);
        
        framePanel.add(jp, BorderLayout.SOUTH);
        
        // upper information
        jp = new JPanel();
        
        jp.add(new JLabel("Time:"));
        time = new JTextField(5);
        time.setEditable(false);
        jp.add(time);
        
        jp.add(new JLabel("TURTLE action(s):"));
        turtle = new JTextField(15);
        turtle.setEditable(false);
        jp.add(turtle);
        
        jp.add(new JLabel("Values:"));
        values = new JTextField(10);
        values.setEditable(false);
        jp.add(values);
        
        jp.add(new JLabel("RT-LOTOS action:"));
        rtlotos = new JTextField(10);
        rtlotos.setEditable(false);
        jp.add(rtlotos);
        
        jp.add(new JLabel("Action No:"));
        action = new JTextField(5);
        action.setEditable(false);
        jp.add(action);
        
        framePanel.add(jp, BorderLayout.NORTH);
        
        pack();
    }
    
    public void setTime(int _time) {
        time.setText(String.valueOf(_time));
    }
    
    public void reinitTime() {
        time.setText("");
    }
    
    public void setAction(int _action, Gate g, GroupOfGates gog, String _values) {
        action.setText(String.valueOf(_action));
        rtlotos.setText(g.getLotosName());
        turtle.setText(gog.printAll());
        values.setText(_values);
    }
    
    private void zoomOut() {
        jsimu.zoomOut();
        //jsp.revalidate();
        //repaint();
        //jsp.paint(jsp.getGraphics());
                /*revalidate();
                repaint();*/
    }
    
    private void zoomIn() {
        jsimu.zoomIn();
        //jsp.revalidate();
        //repaint();
        //jsp.paint(jsp.getGraphics());
        //repaint();
    }
    
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        //System.out.println("Command:" + command);
        
        if (command.equals("Close")) {
            dispose();
            return;
        } else if (command.equals("Zoom -")) {
            zoomOut();
            return;
        } else if (command.equals("Zoom +")) {
            zoomIn();
            return;
        }
    }
    
} // Class

