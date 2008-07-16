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
 * Class JFrameText
 * Creation: 15/12/2003
 * version 1.0 15/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import myutil.*;

import ui.*;
import ui.file.*;


public	class JFrameText extends JFrame	implements ActionListener {
    private JFileChooser jfc;
    private String theText;
    private String theTextWithLineNumber;
    
    
    public JFrameText(String title, String _theText) {
        super(title);
        theText = _theText;
        makeTextWithLineNumber();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        
        JTextArea jta = new JTextArea(theTextWithLineNumber);
        jta.setEditable(true);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        JScrollPane jsp = new JScrollPane(jta);
        
        framePanel.add(jsp, BorderLayout.CENTER);
        
        JButton button1 = new JButton("Close", IconManager.imgic27);
        JButton button2 = new JButton("Save", IconManager.imgic25);
        
        button1.addActionListener(this);
        button2.addActionListener(this);
        
        JPanel jp = new JPanel();
        jp.add(button1);
        jp.add(button2);
        
        framePanel.add(jp, BorderLayout.SOUTH);
        
        
        if (ConfigurationTTool.LOTOSPath.length() > 0) {
            jfc = new JFileChooser(ConfigurationTTool.LOTOSPath);
        } else {
            jfc = new JFileChooser();
        }
        jfc.setApproveButtonText("Save");
        RTLFileFilter filter = new RTLFileFilter();
        jfc.setFileFilter(filter);
        
        pack();
        //setBounds(0,0,800, 600);
        
        // jdk 1.4 or more
        //setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
    
    private void makeTextWithLineNumber() {
        StringBuffer sb = new StringBuffer("1\t" + theText);
        
        int line = 2;
        int index = 0;
        
        while(index < sb.length() - 1) {
            if(sb.charAt(index) == '\n') {
                sb.insert(index+1, "" + line + "\t");
                line ++;
            }
            index ++;
        }
        
        theTextWithLineNumber = new String(sb);
    }
    
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        //System.out.println("Command:" + command);
        
        if (command.equals("Close")) {
            dispose();
            return;
        }
        
        if (command.equals("Save")) {
            File file = null;
            int returnVal = jfc.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                file = jfc.getSelectedFile();
                file = FileUtils.addFileExtensionIfMissing(file, RTLFileFilter.getExtension());
            }
            
            if(checkFileForSave(file)) {
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(theText.getBytes());
                    fos.close();
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(this, "File could not be saved because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    
    public boolean checkFileForSave(File file) {
        boolean ok = true;
        String pb = "";
        
        if (file == null) {
            return false;
        }
        
        try {
            if (file != null) {
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        pb  = "File could not be created";
                        ok = false;
                    }
                    if (!file.canWrite()) {
                        pb  = "File is write protected";
                        ok = false;
                    }
                }
            }
        } catch (Exception e) {
            ok = false;
            pb = e.getMessage();
        }
        if (ok == false) {
            file = null;
            JOptionPane.showMessageDialog(this, pb, "File Error", JOptionPane.INFORMATION_MESSAGE);
        }
        return ok;
    }
    
} // Class

