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
 * Class JDialogNote
 * Dialog for entering a note
 * Creation: 06/12/2003
 * @version 1.0 06/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ui.*;


public class JDialogNote extends javax.swing.JDialog implements ActionListener {
    
    protected String text;
    
    
    //components
    protected JTextArea jta;
    protected JButton close;
    protected JButton cancel;
    
    /** Creates new form  */
    public JDialogNote(Frame f, String title, String _text) {
        super(f, title, true);
        text = _text;
        
        initComponents();
        pack();
    }
    
    
    protected void initComponents() {
        
        Container c = getContentPane();
        Font f = new Font("Helvetica", Font.PLAIN, 14);
        setFont(f);
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        jta = new JTextArea();
        jta.setEditable(true);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append(text);
        jta.setFont(new Font("times", Font.PLAIN, 12));
        jta.setPreferredSize(new Dimension(300, 300));
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        c.add(jsp, BorderLayout.CENTER);
        
        close = new JButton("Ok", IconManager.imgic25);
        cancel = new JButton("Cancel", IconManager.imgic27);
        
        close.setPreferredSize(new Dimension(150, 30));
        cancel.setPreferredSize(new Dimension(150, 30));
        
        close.addActionListener(this);
        cancel.addActionListener(this);
        
        JPanel jp = new JPanel();
        jp.add(close);
        jp.add(cancel);
        
        c.add(jp, BorderLayout.SOUTH);
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (command.equals("Cancel"))  {
            cancel();
        } else if (command.equals("Ok")) {
            close();
        }
    }
    
    public void cancel() {
        dispose();
    }
    
    public void close() {
        text = jta.getText();
        dispose();
    }
    
    public String getText() {
        return text;
    }
    
    
}
