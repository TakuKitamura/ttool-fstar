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
 * Class JFrameCode
 * Creation: 20/04/2005
 * version 1.0 20/04/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import ui.*;


public	class JFrameCode extends JFrame implements ActionListener {
    private String textPre;
    private String textPost;
    private JTextArea jtaPre, jtaPost;
    
    public JFrameCode(String title, String _textPre, String _textPost) {
        super(title);
        textPre = _textPre;
        textPost = _textPost;
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        Font f = new Font("Courrier", Font.BOLD, 12);
        
        JPanel jp1 = new JPanel();
        jp1.setLayout(new BorderLayout());
        JPanel jp01 = new JPanel();
        jp01.setLayout(new BorderLayout());
        jp01.setBorder(new javax.swing.border.TitledBorder("Pre Code"));
        jtaPre = new JTextArea(textPre);
        jtaPre.setEditable(false);
        jtaPre.setMargin(new Insets(10, 10, 10, 10));
        jtaPre.setTabSize(3);
        jtaPre.setFont(f);
        jtaPre.setPreferredSize(new Dimension(250, 100));
        JScrollPane jsp1 = new JScrollPane(jtaPre);
        jp01.add(jsp1, BorderLayout.CENTER);
        
        JPanel jp02 = new JPanel();
        jp02.setLayout(new BorderLayout());
        jp02.setBorder(new javax.swing.border.TitledBorder("Post Code"));
        jtaPost = new JTextArea(textPost);
        jtaPost.setEditable(false);
        jtaPost.setMargin(new Insets(10, 10, 10, 10));
        jtaPost.setTabSize(3);
        jtaPost.setFont(f);
        jtaPost.setPreferredSize(new Dimension(250, 100));
        JScrollPane jsp2 = new JScrollPane(jtaPost);
        jp02.add(jsp2, BorderLayout.CENTER);
        
        jp1.add(jp01, BorderLayout.NORTH);
        jp1.add(jp02, BorderLayout.SOUTH);
        framePanel.add(jp1, BorderLayout.CENTER);
        
        JButton button1 = new JButton("Close", IconManager.imgic27);
        button1.addActionListener(this);
        JPanel jp = new JPanel();
        jp.add(button1);
        framePanel.add(jp, BorderLayout.SOUTH);
        
        pack();
    }
    
    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();        
        if (command.equals("Close")) {
            dispose();
            return;
        }
    }
    
    public void setPreCode(String code) {
        jtaPre.setText(code);
    }
    
    public void setPostCode(String code) {
        jtaPost.setText(code);
    }
    
} // Class

