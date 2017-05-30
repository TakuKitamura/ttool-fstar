/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class JDialogNetworkModelPanel
 * Dialog for managing the loading of network models
 * Creation: 28/05/2017
 * @version 1.1 28/05/2017
 * @author Ludovic APVRILLE
 * @author Ludovic Apvrille
 * @see
 */

package ui.networkmodelloader;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;

import ui.*;
import myutil.*;

public class JDialogLoadingNetworkModel extends javax.swing.JFrame implements ActionListener, Runnable  {

    private static ArrayList<NetworkModel> listOfModels;

    protected Frame f;
    protected MainGUI mgui;

     protected final static int NOT_LISTED = 1;
    protected final static int LISTED = 2;
    protected final static int SELECTED = 3;

    private int mode;

    //components
    protected JTextArea jta;
    private JTextAreaWriter textAreaWriter;
    protected JButton start;
    protected JButton stop;

    protected JScrollPane jsp;

    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
    protected boolean startProcess = false;

    private String url;
    private NetworkModelPanel panel;
    

    /** Creates new form  */
    public JDialogLoadingNetworkModel(Frame _f, MainGUI _mgui, String title, String _url) {
        super(title);

        f = _f;
        mgui = _mgui;

	url = _url;


        initComponents();
        myInitComponents();
        pack();

        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }


    protected void myInitComponents() {
        mode = NOT_LISTED;
        setButtons();
      }

    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	panel = new NetworkModelPanel();
	jsp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.NORTH);


        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to launch code generation / compilation / execution\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        textAreaWriter = new JTextAreaWriter( jta );

        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.CENTER);

        start = new JButton("Load", IconManager.imgic53);
        stop = new JButton("Cancel", IconManager.imgic55);

        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));

        start.addActionListener(this);
        stop.addActionListener(this);

        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);

        c.add(jp2, BorderLayout.SOUTH);

    }

    public void actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == start)  {
            loadFile();
        } else if (evt.getSource() == stop) {
            cancel();
        } 
    }


    public void loadFile() {
	// Run the retreiver + analyzer
    }

    public void cancel() {
	dispose();
    }
 
   
    public void run() {
        
    }

    protected void checkMode() {
        mode = NOT_LISTED;
    }

    protected void setButtons() {
        switch(mode) {
        case NOT_LISTED:
            start.setEnabled(true);
            stop.setEnabled(false);
            //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            getGlassPane().setVisible(false);
            break;
        case LISTED:
            start.setEnabled(false);
            stop.setEnabled(true);
            getGlassPane().setVisible(true);
            //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            break;
        case SELECTED:
        default:
            start.setEnabled(false);
            stop.setEnabled(false);
            getGlassPane().setVisible(false);
            break;
        }
    }


    public void appendOut(String s) {
        jta.append(s);
    }


}
