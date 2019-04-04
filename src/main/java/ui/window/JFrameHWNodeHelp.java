
package ui.window;
import help.HelpEntry;
import help.HelpManager;
import ui.MainGUI;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class JFrameHWNodeHelp
 * Creation: 03/04/2019
 * version 1.0 03/04/2019
 * @author Minh Hiep PHAM
 */

public class JFrameHWNodeHelp extends JFrame implements ActionListener{
    private HelpEntry he;
    private JButton helpBut;
    private JEditorPane pane;

    private MainGUI mgui;

    public JFrameHWNodeHelp(MainGUI _mgui, String title, HelpEntry _he) {
        super(title);
        mgui = _mgui;
        he = _he;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        Font f = new Font("Courrier", Font.BOLD, 12);

        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new BorderLayout());
        helpPanel.setBorder(new javax.swing.border.TitledBorder("Help of " + he.getMasterKeyword()));
        pane = new JEditorPane("text/html;charset=UTF-8", "");
        pane.setEditable(false);
        pane.setText(he.getHTMLContent());

        JScrollPane jsp = new JScrollPane(pane);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        helpPanel.add(jsp, BorderLayout.CENTER);

        framePanel.add(helpPanel, BorderLayout.CENTER);

        helpBut = new JButton("Help", IconManager.imgic32);

        HelpManager hm = new HelpManager();
        if(hm.loadEntries()) {
            mgui.setHelpManager(hm);
        }

        helpBut.addActionListener(this);

        JPanel jp = new JPanel();
        jp.add(helpBut);
        framePanel.add(jp, BorderLayout.SOUTH);

        setSize(400, 400);
        setVisible(true);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == helpBut) {
            mgui.openHelpFrame(he);
        }
    }
}
