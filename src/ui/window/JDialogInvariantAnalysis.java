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
* Class JDialogInvariantAnalysis
* Dialog for managing the generation of ProVerif code and execution of 
* ProVerif
* Creation: 09/02/2012
* @version 1.0 09/01/2012
* @author Ludovic APVRILLE
* @see
*/

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import myutil.*;
import avatartranslator.totpn.*;
import avatartranslator.*;
import tpndescription.*;
import ui.*;
import ui.avatarsmd.*;
import launcher.*;


public class JDialogInvariantAnalysis extends javax.swing.JDialog implements ActionListener, Runnable  {
    
	private static boolean IGNORE = true;
	
    protected MainGUI mgui;
    
    private JTabbedPane jp1;
    
    
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    int mode;
    
    //components
    protected JTextArea jta, jtatpn, jtamatrix, jtainvariants;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    protected JCheckBox ignoreInvariants;
    
    
    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
	//protected boolean startProcess = false;
    
    private String hostProVerif;
    
    protected RshClient rshc;
    
    
    /** Creates new form  */
    public JDialogInvariantAnalysis(Frame f, MainGUI _mgui, String title) {
        super(f, title, true);
        
        mgui = _mgui;
		
        initComponents();
        myInitComponents();
        pack();
        
        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
    }
    
    protected void initComponents() {
        JScrollPane jsp;
        
        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        Font f = new Font("Courrier", Font.BOLD, 12);
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        jp1 = new JTabbedPane();
        
        JPanel panelCompute = new JPanel(new BorderLayout());
        ignoreInvariants = new JCheckBox("Ignore invariants concerning only one block", IGNORE);
        panelCompute.add(ignoreInvariants, BorderLayout.NORTH);
        
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Press start to compute invariants\n");
        
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelCompute.add(jsp, BorderLayout.CENTER);
        jp1.add("Compute invariants", panelCompute);
        
        
        jtatpn = new ScrolledJTextArea();
        jtatpn.setEditable(false);
        jtatpn.setMargin(new Insets(10, 10, 10, 10));
        jtatpn.setTabSize(3);
        //jta.append("Press start to compute invariants\n");
        jtatpn.setFont(f);
        jsp = new JScrollPane(jtatpn, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jp1.add("Petri net", jsp);  
        
        jtamatrix = new ScrolledJTextArea();
        jtamatrix.setEditable(false);
        jtamatrix.setMargin(new Insets(10, 10, 10, 10));
        jtamatrix.setTabSize(3);
        //jtamatrix.append("Press start to compute invariants\n");
        jtamatrix.setFont(f);
        jsp = new JScrollPane(jtamatrix, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jp1.add("Incidence matrix", jsp);
        
        jtainvariants = new ScrolledJTextArea();
        jtainvariants.setEditable(false);
        jtainvariants.setMargin(new Insets(10, 10, 10, 10));
        jtainvariants.setTabSize(3);
        //jtamatrix.append("Press start to compute invariants\n");
        jtainvariants.setFont(f);
        jsp = new JScrollPane(jtainvariants, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jp1.add("Invariants", jsp);
        
        c.add(jp1, BorderLayout.CENTER);
		
        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);
        
        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(100, 30));
        
        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);
        
        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);
        
        c.add(jp2, BorderLayout.SOUTH);
        
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
		
		if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        }
    }
    
    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        IGNORE = ignoreInvariants.isSelected();
        dispose();
    }
    
    public void stopProcess() {
        mode = 	STOPPED;
        setButtons();
        go = false;
        if (t != null) {
        	t.interrupt();
        }
    }
    
    public void startProcess() {
		t = new Thread(this);
		mode = STARTED;
		setButtons();
		go = true;
		t.start();
    }
    
    private void testGo() throws InterruptedException {
        if (go == false) {
            throw new InterruptedException("Stopped by user");
        }
    }
    
    public void run() {
    	TPN tpn;
        hasError = false;
		
		TraceManager.addDev("Thread started");
        
        try {
            jta.append("Generating Petri Net\n");
            tpn = mgui.gtm.generateTPNFromAvatar();
            jtatpn.append("Petri Net:\n" + tpn.toString() + "\n\n");
            testGo();
            
            jta.append("Computing incidence matrix\n");
            IntMatrix im = tpn.getIncidenceMatrix();
            jtamatrix.append("Incidence matrix:\n" + im.toString() + "\n\n");
            jta.append("Incidence matrix computed\n");
            testGo();
            
            jta.append("Computing invariants\n");
            im.Farkas(true);
            //jtainvariants.append("All invariants:\n" + im.namesOfRowToString() + "\n\n");
            
            mgui.gtm.clearInvariants();
            
            Invariant inv;
            String name;
            String[] elts;
            String tmp, tmp1, tmp2;
            String[] tmps;
            int myid;
            AvatarSpecification avspec = mgui.gtm.getAvatarSpecification();
            AvatarBlock ab = null;
            AvatarBlock ab1, ab2;
            Object o;
            int state;
            int valToken = 0;
            
            boolean sameBlock;
            AvatarBlock prevBlock, prevBlock1;
            int ignored = 0;
            TGComponent tgc1, tgc2;
            
            
            jtainvariants.append("Computed invariants:\n-----------------\n");
            
            for(int i=0; i<im.getNbOfLines(); i++) {
            	prevBlock = null;
            	prevBlock1 = null;
            	sameBlock = true;
            	name =  im.getNameOfLine(i);
       
            	inv = new Invariant("#" + ((i+1)-ignored) + " " + name);
            	inv.setValue(im.getValueOfLine(i));
            	
            	// Putting components
            	name = Conversion.replaceAllString(name, " + ", "&"); 
            	elts = name.split("&");
            	state = 0;
            	for(int j=0; j<elts.length; j++) {
            		tmp = elts[j].trim();
            		//TraceManager.addDev("#" + j + "=" + elts[j] + " tmp=" + tmp);
            		
            		if (tmp.startsWith("Synchro from ")) {
            			tmp =tmp.substring(13, tmp.length()).trim();
            			int index = tmp.indexOf(" to ");
            			if (index != -1) {
            				tmp1 = tmp.substring(0, index).trim();
            				tmp2 = tmp.substring(index+4, tmp.length()).trim();
            				//TraceManager.addDev("Found synchro: " + tmp1 + ", " + tmp2);
            				tgc1 = null;
            				tgc2 = null;
            				ab1 = null;
            				ab2 = null;
            				
            				//tmp1
            				tmp1 = Conversion.replaceAllString(tmp1, "__", "&");
            				tmps = tmp1.split("&");
            				if (tmps.length > 2) {
            					ab = avspec.getBlockWithName(tmps[0]);
            					ab1 = ab;
            					
            					try {
            						myid = Integer.decode(tmps[tmps.length-1]).intValue();
            						o = ab.getStateMachine().getReferenceObjectFromID(myid);
            						tgc1 = (TGComponent)o;
            						
            					} catch (Exception e) {
            						tgc1 = null;
            						TraceManager.addDev("Exception invariants tmp1:" + e.getMessage() + "tmps[end]=" + tmps[tmps.length-1] + " inv=" + name);
            					}
            				}
            				
            				
            				//tmp2
            				tmp2 = Conversion.replaceAllString(tmp2, "__", "&");
            				tmps = tmp2.split("&");
            				if (tmps.length > 2) {
            					ab = avspec.getBlockWithName(tmps[0]);
            					ab2 = ab;
            					
            					try {
            						myid = Integer.decode(tmps[tmps.length-1]).intValue();
            						o = ab.getStateMachine().getReferenceObjectFromID(myid);
            						tgc2 = (TGComponent)o;
            						
            					} catch (Exception e) {
            						tgc2 = null;
            						TraceManager.addDev("Exception invariants tm2:" + e.getMessage() + "tmps[end]=" + tmps[tmps.length-1] + " inv=" + name);
            					}
            				}
            				
            				
            				if ((ab1 != null ) && (ab2 != null)) {
            					if (prevBlock == null) {
            						prevBlock = ab1;
            						prevBlock1 = ab2;
            					} else {
            						if ((prevBlock != ab1) && (prevBlock != ab2)) {
            							sameBlock = false;
            						}
            					}
            				}
            				
            				// Can create synchro
            				//TraceManager.addDev("tg1=" + tgc1 + " tgc2=" + tgc2);
            				if ((tgc1 != null) && (tgc2 != null)) {
            					InvariantSynchro is = new InvariantSynchro(elts[j].trim(), tgc1, tgc2);
            					inv.addSynchro(is);
            					//TraceManager.addDev("Ading synchro: " + is);
            				}
            				
            			}
            		} else {
            			
            			tmp = Conversion.replaceAllString(tmp, "__", "&");
            			tmps = tmp.split("&");
            			if (tmps.length > 2) {
            				ab = avspec.getBlockWithName(tmps[0]);
            				if (prevBlock == null) {
            					prevBlock = ab;
            				} else {
            					if (prevBlock != ab) {
            						if (prevBlock1 != null) {
            							if (prevBlock1 != ab) {
            								sameBlock = false;
            							}
            						} else {
            							sameBlock = false;
            						}
            					}
            				}
            				prevBlock = ab;
            				prevBlock1 = null;
            				
            				try {
            					myid = Integer.decode(tmps[tmps.length-1]).intValue();
            					o = ab.getStateMachine().getReferenceObjectFromID(myid);
            					//TraceManager.addDev("Adding component to inv   block=" + ab.getName() + " id=" + myid + " object=" + o);
            					if (!((o instanceof AvatarSMDReceiveSignal) || (o instanceof AvatarSMDSendSignal))) {
            						//TraceManager.addDev("Adding component to inv   block=" + ab.getName() + " id=" + myid + " object=" + o);
            						inv.addComponent((TGComponent)o);
            					}
            					//TraceManager.addDev("Component added:" + o);
            					if (o instanceof AvatarSMDStartState) {
            						valToken ++;
            					}
            				} catch (Exception e) {
            					TraceManager.addDev("Exception invariants:" + e.getMessage() + "tmps[end]=" + tmps[tmps.length-1] + " inv=" + name);
            				}
            			}
            		}
            	}
            	inv.setTokenValue(valToken);
            	inv.computeValue();
            	
            	if (!(ignoreInvariants.isSelected() && sameBlock)) {
            		mgui.gtm.addInvariant(inv);
            		jtainvariants.append(inv + "\n");
            	} else {
            		//TraceManager.addDev("Invariant ignored " + inv);
            		jtainvariants.append("Ignored invariant: " + inv + "\n");
            		ignored ++;
            	}
            	
            }
            
            jta.append("Invariants computed\n");
            testGo();
            
            jta.append("All done\n");
            
            
            
        } catch (InterruptedException ie) {
        	jta.append("Interrupted\n");
        }
        
        jta.append("\n\nReady to process next command\n");
        
        checkMode();
        setButtons();
        
        //System.out.println("Selected item=" + selectedItem);
    }
    
    protected void checkMode() {
    	mode = NOT_STARTED;
    }
    
    protected void setButtons() {
    	switch(mode) {
    	case NOT_STARTED:
    		start.setEnabled(true);
    		stop.setEnabled(false);
    		close.setEnabled(true);
    		//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		getGlassPane().setVisible(false);
    		break;
    	case STARTED:
    		start.setEnabled(false);
    		stop.setEnabled(true);
    		close.setEnabled(false);
    		getGlassPane().setVisible(true);
    		//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    		break;
    	case STOPPED:
    	default:
    		start.setEnabled(false);
    		stop.setEnabled(false);
    		close.setEnabled(true);
    		getGlassPane().setVisible(false);
    		break;
    	}
    }
    
    public boolean hasToContinue() {
    	return (go == true);
    }
    
    public void setError() {
    	hasError = true;
    }
}
