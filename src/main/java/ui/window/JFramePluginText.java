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

import myutil.Plugin;
import myutil.PluginManager;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Class JFramePluginText Creation: 10/09/2020 version 1.0 10/09/2020
 * 
 * @author Ludovic APVRILLE
 */
public class JFramePluginText extends JFrame implements ActionListener, Runnable {

    protected JTextArea jta;
    protected PluginManager pm;

    public JFramePluginText(String title, ImageIcon imgic) {
        super(title);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());

        jta = new JTextArea("List of detected plugins:\n");
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        JScrollPane jsp = new JScrollPane(jta);

        framePanel.add(jsp, BorderLayout.CENTER);

        JButton button1 = new JButton("Close", IconManager.imgic27);

        button1.addActionListener(this);

        JPanel jp = new JPanel();
        jp.add(button1);

        framePanel.add(jp, BorderLayout.SOUTH);

        if (imgic != null) {
            JButton button2 = new JButton(imgic);
            jp = new JPanel();
            jp.add(button2);
            framePanel.add(jp, BorderLayout.NORTH);
        }

        pack();

        Thread t = new Thread(this);
        t.start();

        button1.setName("Close Configuration");
        jsp.setName("Jsp Configuration");
    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        if (command.equals("Close")) {
            dispose();
            return;
        }
    }

    public void run() {
        // List all plugins
        if (PluginManager.pluginManager == null) {
            jta.append("No plugin detected");
            return;
        }

        ArrayList<Plugin> plugins = PluginManager.pluginManager.getPlugins();
        for (Plugin p : plugins) {
            if (p.getPackageName().length() > 0) {
                jta.append("\n\n- Plugin " + p.getPath() + " " + p.getPackageName() + "." + p.getName());
            } else {
                jta.append("\n\n- Plugin " + p.getPath() + " " + p.getName());
            }
            jta.append("\n    code generator for AVATAR? ");
            if (p.hasAvatarCodeGenerator()) {
                yes();
            } else {
                no();
            }
            jta.append("\n    code generator for DIPLODOCUS? ");
            if (p.hasDiplodocusCodeGenerator()) {
                yes();
            } else {
                no();
            }
            jta.append("\n    FPGA scheduling? ");
            if (p.hasFPGAScheduling()) {
                yes();
            } else {
                no();
            }
            jta.append("\n    Has a command line interface? ");
            if (p.hasCommandLineInterface()) {
                yes();
            } else {
                no();
            }
            jta.append("\n    Adds graphical components? ");
            if (p.hasGraphicalComponent()) {
                yes();
            } else {
                no();
            }

        }

    }

    private void yes() {
        jta.append("yes");
    }

    private void no() {
        jta.append("no");
    }

} // Class
