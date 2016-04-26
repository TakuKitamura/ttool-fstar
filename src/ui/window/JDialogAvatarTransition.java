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
   * Class JDialogAvatarTransition
   * Dialog for managing transitions between states
   * Creation: 12/04/2010
   * @version 1.0 12/04/2010
   * @author Ludovic APVRILLE
   * @see
   */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import myutil.*;
import ui.*;

public class JDialogAvatarTransition extends javax.swing.JDialog implements ActionListener  {

    private Vector<String> actions;
    private String guard, afterMin, afterMax, computeMin, computeMax;
    private LinkedList<TAttribute> myAttributes;
    private LinkedList<AvatarMethod> myMethods;
    private Vector<String> allElements, insertElements;

    protected String [] filesToInclude;
    protected String [] codeToInclude;

    private boolean cancelled = false;

    private JPanel panel1;
    private JPanel panel2;

    // Panel1
    private JTextField guardT, afterMinT, afterMaxT, computeMinT, computeMaxT;
    private JTextArea actionsT;
    private JComboBox elements;
    private JButton insertElement;

    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;

    // Panel of code and files
    protected JTextArea jtaCode, jtaFiles;


    /** Creates new form  */
    // arrayDelay: [0] -> minDelay ; [1] -> maxDelay
    public JDialogAvatarTransition(Frame _f, String _title, String _guard, String _afterMin, String _afterMax, String _computeMin, String _computeMax, Vector<String> _actions, LinkedList<TAttribute> _myAttributes, LinkedList<AvatarMethod> _myMethods, String[] _filesToInclude, String[] _codeToInclude) {

        super(_f, _title, true);

        guard = _guard;
        afterMin = _afterMin;
        afterMax = _afterMax;
        computeMin = _computeMin;
        computeMax = _computeMax;
        actions = _actions;

        myAttributes = _myAttributes;
        myMethods = _myMethods;

        filesToInclude = _filesToInclude;
        codeToInclude = _codeToInclude;

        makeElements();

        initComponents();
        myInitComponents();
        pack();
    }

    private void makeElements() {
        int i;

        allElements = new Vector<String>();
        insertElements = new Vector<String>();

        for (TAttribute ta: myAttributes) {
            allElements.add(ta.toString());
            insertElements.add(ta.getId());
        }

        for (AvatarMethod am: myMethods) {
            allElements.add(am.toString());
            insertElements.add(am.getUseDescription());
        }
    }


    private void myInitComponents() {
    }

    private void initComponents() {
        int i;

        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        //c.setLayout(gridbag0);
	c.setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel1 = new JPanel();
        panel1.setLayout(gridbag1);

        panel1.setBorder(new javax.swing.border.TitledBorder("Transition parameters"));

        //panel1.setPreferredSize(new Dimension(350, 350));

        // guard
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        panel1.add(new JLabel("guard = "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        guardT = new JTextField(guard);
        panel1.add(guardT, c1);

        // After
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        panel1.add(new JLabel("after ("), c1);
        afterMinT = new JTextField(afterMin, 10);
        panel1.add(afterMinT, c1);
        panel1.add(new JLabel(","), c1);
        afterMaxT = new JTextField(afterMax, 10);
        panel1.add(afterMaxT, c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel1.add(new JLabel(")"), c1);

        // Compute
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        panel1.add(new JLabel("compute for ("), c1);
        computeMinT = new JTextField(computeMin, 10);
        panel1.add(computeMinT, c1);
        panel1.add(new JLabel(","), c1);
        computeMaxT = new JTextField(computeMax, 10);
        panel1.add(computeMaxT, c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel1.add(new JLabel(")"), c1);


        // actions

        elements = new JComboBox(allElements);
        panel1.add(elements, c1);

        insertElement = new JButton("Insert");
        insertElement.setEnabled(allElements.size() > 0);
        insertElement.addActionListener(this);
        panel1.add(insertElement, c1);

        c1.gridheight = 10;
        c1.weighty = 10.0;
        c1.weightx = 10.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        actionsT = new JTextArea();
        actionsT.setEditable(true);
        actionsT.setMargin(new Insets(10, 10, 10, 10));
        actionsT.setTabSize(3);
        actionsT.setFont(new Font("times", Font.PLAIN, 12));
        //actionsT.setPreferredSize(new Dimension(350, 250));
        JScrollPane jsp = new JScrollPane(actionsT, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        for(i=0; i<actions.size(); i++) {
            actionsT.append(actions.get(i) + "\n");
        }
        panel1.add(jsp, c1);


        panel2 = new JPanel();
        panel2.setLayout(gridbag2);

        panel2.setBorder(new javax.swing.border.TitledBorder("Code"));
        // guard
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridwidth = GridBagConstraints.REMAINDER;
        c2.gridheight = 1;
        panel2.add(new JLabel("Files to include:"), c2);
        jtaFiles = new JTextArea();
        jtaFiles.setEditable(true);
        jtaFiles.setMargin(new Insets(10, 10, 10, 10));
        jtaFiles.setTabSize(3);
        String files = "";
        if (filesToInclude != null) {
            for(i=0; i<filesToInclude.length; i++) {
                files += filesToInclude[i] + "\n";
            }
        }
        jtaFiles.append(files);
        jtaFiles.setFont(new Font("times", Font.PLAIN, 12));
        jsp = new JScrollPane(jtaFiles, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //jsp.setPreferredSize(new Dimension(300, 300));
        panel2.add(jsp, c2);
        panel2.add(new JLabel("Code to execute at the end of the transition"), c2);
        jtaCode = new JTextArea();
        jtaCode.setEditable(true);
        jtaCode.setMargin(new Insets(10, 10, 10, 10));
        jtaCode.setTabSize(3);
        String code = "";
        if (codeToInclude != null) {
            for(i=0; i<codeToInclude.length; i++) {
                code += codeToInclude[i] + "\n";
            }
        }
        jtaCode.append(code);
        jtaCode.setFont(new Font("times", Font.PLAIN, 12));
        jsp = new JScrollPane(jtaCode, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //jsp.setPreferredSize(new Dimension(300, 300));
        panel2.add(jsp, c2);


        // button panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row



        JTabbedPane jtp = new JTabbedPane();
        jtp.add("General", panel1);
        //jtp.add("Prototyping", panel2);
        //c.add(jtp, c0);
	c.add(jtp, BorderLayout.CENTER);

	JPanel buttons = new JPanel();
	buttons.setLayout(gridbag0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        closeButton = new JButton("Save and Close", IconManager.imgic25);
        //closeButton.setPreferredSize(new Dimension(600, 50));


        closeButton.addActionListener(this);
        buttons.add(closeButton, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        buttons.add(cancelButton, c0);

	c.add(buttons, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == cancelButton)  {
            cancelDialog();
        } else if (evt.getSource() == insertElement)  {
            insertElements();
        }
    }

    public void insertElements() {
        int index = elements.getSelectedIndex();
        int caretPos = actionsT.getCaretPosition ();
        String str = insertElements.get(index);
        String text = actionsT.getText ();
        if (caretPos > 0 && text.charAt (caretPos-1) != ' ' && text.charAt (caretPos-1) != '(')
            str = " " + str;
        if (caretPos == text.length () || (text.charAt (caretPos) != ' ' && text.charAt (caretPos) != ')'))
            str = str + " ";
        actionsT.insert (str, caretPos);
        actionsT.setCaretPosition (caretPos + str.length ());
        actionsT.requestFocusInWindow ();
    }

    public void closeDialog() {
        actions.removeAllElements();
        String[] act = actionsT.getText().split("\n");
        for(int i=0; i<act.length; i++) {
            if (act[0].length() > 0) {
                actions.add(act[i]);
            }
        }
        filesToInclude =  Conversion.wrapText(jtaFiles.getText());
        codeToInclude =  Conversion.wrapText(jtaCode.getText());
        dispose();
    }

    /*public String getActions() {
      return signal.getText();
      }*/

    public String getGuard() {
        return guardT.getText();
    }

    public String getAfterMin() {
        return afterMinT.getText();
    }

    public String getAfterMax() {
        return afterMaxT.getText();
    }

    public String getComputeMin() {
        return computeMinT.getText();
    }

    public String getComputeMax() {
        return computeMaxT.getText();
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public void cancelDialog() {
        cancelled = true;
        dispose();
    }

    public String[] getFilesToInclude() {
        return filesToInclude;
    }

    public String[] getCodeToInclude() {
        return codeToInclude;
    }

}
