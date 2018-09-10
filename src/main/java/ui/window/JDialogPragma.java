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

import ui.util.IconManager;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class JDialogPragma
 * Dialog for entering a note
 * Creation: 06/12/2003
 *
 * @author Ludovic APVRILLE, Letitia LI
 * @version 1.0 06/12/2003
 */
public class JDialogPragma extends JDialogBase implements ActionListener {

    protected String text;

    //components
    private JTextArea textarea;
    protected JButton close;
    protected JButton cancel;
    protected JMenu help;
    private JPopupMenu helpPopup;
    public Map<String, List<String>> blockAttributeMap = new HashMap<>();
    public Map<String, List<String>> blockStateMap = new HashMap<>();

    /**
     * Creates new form
     */
    public JDialogPragma(Frame f, String title, String _text) {
        super(f, title, true);
        text = _text;

        initComponents();
        pack();
    }
//Suggestion Panel code from: http://stackoverflow.com/questions/10873748/how-to-show-autocomplete-as-i-type-in-jtextarea

    public class SuggestionPanel {
        private final String[] pragma = {"#Authenticity", "#Confidentiality", "#PublicConstant", "#PrivateConstant", "#InitialSessionKnowledge", "#InitialSystemKnowledge", "#PrivatePublicKeys", "#Public", "#SecrecyAssumption", "#Secret"};
        private JList<String> list;
        private JPopupMenu popupMenu;
        private String subWord;
        private final int insertionPosition;

        public SuggestionPanel(JTextArea textarea, int position, String subWord, Point location, String header) {
            this.insertionPosition = position;
            this.subWord = subWord;
            popupMenu = new JPopupMenu();
            popupMenu.removeAll();
            popupMenu.setOpaque(false);
            popupMenu.setBorder(null);
            popupMenu.add(list = createSuggestionList(position, subWord, header), BorderLayout.CENTER);
            //Show popupMenu only if there are matching suggestions
            if (list.getModel().getSize() > 0) {
                popupMenu.show(textarea, location.x, textarea.getBaseline(0, 0) + location.y);
            }
        }

        public void hide() {
            popupMenu.setVisible(false);
            if (suggestion == this) {
                suggestion = null;
            }
        }

        private JList<String> createSuggestionList(final int position, final String subWord, String header) {
            List<String> matches = new ArrayList<>();
            if (subWord.startsWith("#")) {
                for (String p : pragma) {
                    if (p.startsWith(subWord)) {
                        matches.add(p);
                    }
                }
            } else if (header.contains("#")) {
                //Find instances of '.'
                Pattern p = Pattern.compile("\\.");
                Matcher m = p.matcher(subWord);
                int count = 0;
                while (m.find()) {
                    count += 1;
                }
                if (count == 0) {
                    //Suggest block names
                    for (String block : blockAttributeMap.keySet()) {
                        if (block.startsWith(subWord)) {
                            matches.add(block);
                        }
                    }
                } else if (count == 1) {
                    if (header.contains("Authenticity")) {
                        //Suggest state names
                        String block = subWord.split("\\.")[0];
                        for (String st : blockStateMap.get(block)) {
                            if (st.startsWith(subWord.split("\\.")[1])) {
                                matches.add(block + "." + st);
                            }
                        }
                    } else {
                        String block = subWord.split("\\.")[0];
                        for (String attr : blockAttributeMap.get(block)) {
                            if (attr.startsWith(subWord.split("\\.")[1])) {
                                matches.add(block + "." + attr);
                            }
                        }
                    }
                } else {
                    String block = subWord.split("\\.")[0];
                    String state = subWord.split("\\.")[1];
                    for (String attr : blockAttributeMap.get(block)) {
                        if (attr.startsWith(subWord.split("\\.")[2])) {
                            matches.add(block + "." + state + "." + attr);
                        }
                    }

                }
            }
            String[] data = new String[matches.size()];
            data = matches.toArray(data);
            JList<String> list = new JList<>(data);
            list.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setSelectedIndex(0);
            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        insertSelection();
                    }
                }
            });

            return list;
        }


        public boolean insertSelection() {
            if (!popupMenu.isVisible()) {
                return false;
            }
            if (list.getSelectedValue() != null) {
                try {
                    final String selectedSuggestion = ( list.getSelectedValue()).substring(subWord.length());
                    textarea.getDocument().insertString(insertionPosition, selectedSuggestion, null);
                    return true;
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                hideSuggestion();
            }
            return false;
        }

        public void moveUp() {
            int index = Math.max(list.getSelectedIndex() - 1, 0);
            selectIndex(index);
        }

        public void moveDown() {
            int index = Math.min(list.getSelectedIndex() + 1, list.getModel().getSize() - 1);
            selectIndex(index);
        }

        private void selectIndex(int index) {
            final int position = textarea.getCaretPosition();
            list.setSelectedIndex(index);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textarea.setCaretPosition(position);
                }
            });
        }
    }

    private SuggestionPanel suggestion;

    protected void showSuggestionLater() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showSuggestion();
            }

        });
    }

    protected void showSuggestion() {
        hideSuggestion();
        final int position = textarea.getCaretPosition();
        Point location;
        try {
            location = textarea.modelToView(position).getLocation();
        } catch (BadLocationException e2) {
            e2.printStackTrace();
            return;
        }
        String text = textarea.getText();
        int start = Math.max(0, position - 1);
        while (start > 0) {
            if (!Character.isWhitespace(text.charAt(start))) {
                start--;
            } else {
                start++;
                break;
            }
        }
        if (start > position) {
            return;
        }
        final String subWord = text.substring(start, position);
        if (subWord.length() < 1) {
            return;
        }
        String header;
        //Find the most recent pragma name
        start = Math.max(0, position - 1);
        while (start > 0) {
            //Find previous new line position
            if (!String.valueOf(text.charAt(start)).matches(".")) {
                break;
            } else {
                start--;
            }
        }
        if (start == 0) {
            header = text.substring(start, position).split(" ")[0];
        } else {
            header = text.substring(start + 1, position).split(" ")[0];
        }
        suggestion = new SuggestionPanel(textarea, position, subWord, location, header);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textarea.requestFocusInWindow();
            }
        });
    }

    private void hideSuggestion() {
        if (suggestion != null) {
            suggestion.hide();
        }
    }

    protected void initComponents() {
        Container c = getContentPane();
        Font f = new Font("Helvetica", Font.PLAIN, 14);
        setFont(f);
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
        helpPopup = new JPopupMenu();
        JTextArea jft = new JTextArea("Pragma Guidelines: \n #Authenticity: Compare if two Attributes are equal at given states \n #Confidentiality: Query whether the attacker knows the value of this attribute. \n #PublicConstant: Declare string as public constant \n #PrivateConstant: Declare string as private constant \n #InitialSessionKnowledge: Knowledge at the start of each session\n #InitialSystemKnowledge: Knowledge at the start of the system \n #PrivatePublicKeys: Set two attribute of a block as Private and Public Key respectively \n #Public: Declare variable public \n #SecrecyAssumption: Assume attribute confidential, but query to verify \n #Secret: See #Confidentiality");
        helpPopup.add(jft);
        textarea = new JTextArea();

        textarea.setEditable(true);
        textarea.setMargin(new Insets(10, 10, 10, 10));
        textarea.setTabSize(3);
        textarea.append(text);
        textarea.setFont(new Font("times", Font.PLAIN, 12));
        JMenuBar menuBar = new JMenuBar();
        menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        help = new JMenu("?");
        menuBar.add(help);
        setJMenuBar(menuBar);
        textarea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_TAB) {
                    if (suggestion != null) {
                        if (suggestion.insertSelection()) {
                            e.consume();
                            final int position = textarea.getCaretPosition();
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        textarea.getDocument().remove(position - 1, 1);
                                    } catch (BadLocationException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && suggestion != null) {
                    suggestion.moveDown();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && suggestion != null) {
                    suggestion.moveUp();
                } else if (Character.isWhitespace(e.getKeyChar())) {
                    hideSuggestion();
                } else if (Character.isLetter(e.getKeyChar()) || e.getKeyChar() == '#') {
                    showSuggestionLater();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && suggestion != null) {
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && suggestion != null) {
                    e.consume();
                }
            }
        });

        JScrollPane jsp = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setPreferredSize(new Dimension(300, 300));
        c.add(jsp, BorderLayout.CENTER);

        close = new JButton("Ok", IconManager.imgic25);
        cancel = new JButton("Cancel", IconManager.imgic27);

        help.setPreferredSize(new Dimension(30, 30));

        close.setPreferredSize(new Dimension(150, 30));
        cancel.setPreferredSize(new Dimension(150, 30));

        close.addActionListener(this);
        cancel.addActionListener(this);
        help.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                help();
            }
        });
        JPanel jp = new JPanel();
        jp.add(cancel);
        jp.add(close);

        c.add(jp, BorderLayout.SOUTH);

    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Cancel")) {
            cancel();
        } else if (command.equals("Ok")) {
            close();
        }
    }

    public void cancel() {
        dispose();
    }

    public void close() {
        text = textarea.getText();
        dispose();
    }

    public void help() {
        if (!helpPopup.isVisible()) {
            helpPopup.show(help, 20, 20);
        } else {
            helpPopup.setVisible(false);
        }
    }

    public String getText() {
        return text;
    }
}
