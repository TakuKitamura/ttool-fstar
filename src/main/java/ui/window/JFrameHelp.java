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


import help.HelpEntry;
import help.HelpManager;
import myutil.TraceManager;
import ui.MainGUI;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


/**
 * Class JFrameCode
 * Creation: 20/04/2005
 * version 1.0 20/04/2005
 * @author Ludovic APVRILLE
 */
public	class JFrameHelp extends JFrame implements ActionListener {
    private JEditorPane pane;
    private HelpEntry he;
    private HelpManager hm;
    private JPanel jp01;
    
    public JFrameHelp(String title, HelpManager hm, HelpEntry he) {
        super(title);
        this.he = he;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        Font f = new Font("Courrier", Font.BOLD, 12);

        jp01 = new JPanel();
        jp01.setLayout(new BorderLayout());
        jp01.setBorder(new javax.swing.border.TitledBorder("Help of: " + he.getMasterKeyword()));
        pane = new JEditorPane("text/html;charset=UTF-8", he.getHTMLContent());
        pane.setEditable(false);
        pane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    URL url = e.getURL();
                    if (url == null) {
                        return;
                    }

                    String link = e.getURL().toString();
                   if (link.startsWith("file://")) {
                       // Open the corresponding file in TTool
                       String fileToOpen = link.substring(7, link.length());
                       TraceManager.addDev("File to open:" + fileToOpen);
                       if (hm == null) {
                           return;
                       }
                       HelpEntry he = hm.getHelpEntryWithHTMLFile(fileToOpen);
                       if (he != null) {
                           setHelpEntry(he);
                       } else {
                           TraceManager.addDev("Null HE");
                       }
                   }
                }
            }
        });


        //TraceManager.addDev("HMLTContent:" + he.getHTMLContent());
        JScrollPane jsp1 = new JScrollPane(pane);
        jsp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jp01.add(jsp1, BorderLayout.CENTER);

        framePanel.add(jp01, BorderLayout.CENTER);

        JButton button1 = new JButton("Close", IconManager.imgic27);
        button1.addActionListener(this);
        JPanel jp = new JPanel();
        jp.add(button1);
        framePanel.add(jp, BorderLayout.SOUTH);

        pack();
        setSize(500,600);
    }

    public void setHelpEntry(HelpEntry he) {
        this.he = he;
        jp01.setBorder(new javax.swing.border.TitledBorder("Help of: " + he.getMasterKeyword()));
        pane.setText(he.getHTMLContent());
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();        
        if (command.equals("Close")) {
            setVisible (false);
            return;
        }
    }

    
} // Class

