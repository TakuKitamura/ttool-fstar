package help;

import ui.MainGUI;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class JFrameHelp
 * Creation: 03/04/2019
 * version 1.0 03/04/2019
 * @author Minh Hiep PHAM
 */

public class CPUNodeHelp extends JFrame {
    private HelpEntry he;
    private JButton helpBut;
    private JEditorPane pane;

    private MainGUI mgui = new MainGUI(false, false, false,false,
            false,false,false,false,false,
            false,false,false,false);

    public CPUNodeHelp(String title, HelpEntry _he) {
        super(title);
        he = _he;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
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
        helpBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mgui.openHelpFrame(he);
            }
        });

        JPanel jp = new JPanel();
        jp.add(helpBut);
        framePanel.add(jp, BorderLayout.SOUTH);

        setSize(400, 400);
        setVisible(true);
    }
}
