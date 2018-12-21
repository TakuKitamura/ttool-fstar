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

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import myutil.*;
import tpndescription.TPN;
import ui.*;
import ui.avatarsmd.AvatarSMDReceiveSignal;
import ui.avatarsmd.AvatarSMDSendSignal;
import ui.avatarsmd.AvatarSMDStartState;
import ui.avatarsmd.AvatarSMDState;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.BitSet;


/**
* Class JDialogInvariantAnalysis
* Dialog for managing the generation of ProVerif code and execution of 
* ProVerif
* Creation: 09/02/2012
* @version 1.0 09/01/2012
* @author Ludovic APVRILLE
 */
public class JDialogInvariantAnalysis extends JDialog implements ActionListener, Runnable  {
    
	private static boolean IGNORE = true;
	private static boolean ALL_MUTEX = true;
	private static boolean FARKAS_SELECTED = false;
	
    protected MainGUI mgui;
    
    private JTabbedPane jp1;
    
    
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    int mode;
    
    //components
    protected JRadioButton farkasButton, farkasHeuristicsButton, PIPEButton;
    protected JTextArea jta, jtatpn, jtamatrix, jtamatrixafterfarkas, jtainvariants;
    protected JLabel info;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    protected JCheckBox ignoreInvariants, computeAllMutualExclusions;
    
    
    private Thread t;
    private boolean go = false;
//    private boolean hasError = false;
	protected boolean startProcess = false;
    private IntMatrix im;
    
    
    /* Creates new form  */
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
        
        // Issue #41 Ordering of tabbed panes 
        jp1 = GraphicLib.createTabbedPane();//new JTabbedPane();
        
        JPanel panelCompute = new JPanel(new BorderLayout());
        JPanel panelCheck = new JPanel(new BorderLayout());
        ignoreInvariants = new JCheckBox("Ignore invariants concerning only one block", IGNORE);
        panelCheck.add(ignoreInvariants, BorderLayout.NORTH);
        computeAllMutualExclusions = new JCheckBox("Compute mutual exclusions for all states", ALL_MUTEX);
        panelCheck.add(computeAllMutualExclusions, BorderLayout.CENTER);
        info = new JLabel("");
        panelCheck.add(info, BorderLayout.EAST);
        
        JPanel radioButtonsForAlgo = new JPanel(new BorderLayout());
        farkasButton = new JRadioButton("Farkas algorithm");
        radioButtonsForAlgo.add(farkasButton, BorderLayout.NORTH);
        farkasHeuristicsButton = new JRadioButton("Farkas algorithm with heuristics (much faster, less complete)");
        radioButtonsForAlgo.add(farkasHeuristicsButton, BorderLayout.CENTER);
        PIPEButton = new JRadioButton("PIPE algorithm");
        //radioButtonsForAlgo.add(PIPEButton, BorderLayout.SOUTH);
      
         panelCheck.add(radioButtonsForAlgo, BorderLayout.SOUTH);
        ButtonGroup group = new ButtonGroup();
        group.add(farkasButton);
    	group.add(farkasHeuristicsButton);
    	 if (FARKAS_SELECTED) {
        	farkasButton.setSelected(true);
        } else {
        	farkasHeuristicsButton.setSelected(true);
        }
        
        panelCompute.add(panelCheck, BorderLayout.NORTH);
        
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
        jtatpn.setFont(f);
        jsp = new JScrollPane(jtatpn, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jp1.add("Petri net", jsp);  
        
        jtamatrix = new ScrolledJTextArea();
        jtamatrix.setEditable(false);
        jtamatrix.setMargin(new Insets(10, 10, 10, 10));
        jtamatrix.setTabSize(3);
        jtamatrix.setFont(f);
        jsp = new JScrollPane(jtamatrix, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jp1.add("Incidence matrix", jsp);
        
        jtamatrixafterfarkas = new ScrolledJTextArea();
        jtamatrixafterfarkas.setEditable(false);
        jtamatrixafterfarkas.setMargin(new Insets(10, 10, 10, 10));
        jtamatrixafterfarkas.setTabSize(3);
        jtamatrixafterfarkas.setFont(f);
        jsp = new JScrollPane(jtamatrixafterfarkas, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jp1.add("Incidence matrix after Farkas", jsp);
        
        jtainvariants = new ScrolledJTextArea();
        jtainvariants.setEditable(false);
        jtainvariants.setMargin(new Insets(10, 10, 10, 10));
        jtainvariants.setTabSize(3);
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
        ALL_MUTEX = computeAllMutualExclusions.isSelected();
        FARKAS_SELECTED = farkasButton.isSelected();
        dispose();
    }
    
    public void stopProcess() {
        mode = 	STOPPED;
        setButtons();
        if (im != null) {
        	im.stopComputation();
        }
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
    
   
    public void pipeInvariants(TPN tpn, IntMatrix im) throws InterruptedException {
    	/*String[] elts;
    	
    	mgui.gtm.clearInvariants();
    	
    	TraceManager.addDev("Computing invariants with PIPE approach");
    	Matrix pipeM = new Matrix(im.matrice).transpose();
    	InvariantAnalysis ia = new InvariantAnalysis();
    	Matrix result = ia.findVectors(pipeM);
    	TraceManager.addDev("PN with " + tpn.getNbOfPlaces() + " places and " +  tpn.getNbOfTransitions() + " transitions");
    	TraceManager.addDev("Basic print:\n" + result.basicPrint());
    	TraceManager.addDev("Advanced print:\n" + ia.findMyPEquations(result, tpn.getPlaces()) + "\n");
    	
    	jtainvariants.append("Computed invariants:\n--------------------\n");
    	
    	int[][] arrayOfInv = result.getArray();
    	Invariant [] invs;
    	Invariant inv;
    	String name;
    	
    	invs = ia.findMyPEquationsStrings(result, tpn.getPlaces());
    	if (invs == null) {
    		// No invariants!
    		return;
    	}
    	
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
    	//Working by invariant
    	for(int i=0; i<invs.length; i++) {
    		valToken = 0;
    		prevBlock = null;
            prevBlock1 = null;
            sameBlock = true;
            	
    		inv = invs[i];
    		name = inv.getName();
    		TraceManager.addDev("Invariant:" + name + "\n");
    		
    		name = Conversion.replaceAllString(name, " + ", "&"); 
    		elts = name.split("&");
    		
    		for(int j=0; j<elts.length; j++) {
    			tmp = elts[j].trim();
    			
    			
    			
    			if (tmp.startsWith("Synchro_from_")) {
    				tmp =tmp.substring(13, tmp.length()).trim();
    				int index = tmp.indexOf("_to_");
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
    							if (o != null) {
    								tgc1 = (TGComponent)o;
    							} else {
    								tgc1 = null;
    							}
    							
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
    							if (o != null) {
    								tgc2 = (TGComponent)o;
    							} else {
    								tgc2 = null;
    							}
    							
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
    					//TraceManager.addDev("Getting block with name=" + tmps[0]);
    					ab = avspec.getBlockWithName(tmps[0]);
    					if (ab != null) {
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
    							//TraceManager.addDev("trying ... tmps=" + tmps[tmps.length-1] + "ab=" + ab);
    							myid = Integer.decode(tmps[tmps.length-1]).intValue();
    							//TraceManager.addDev("Adding component to inv   block=" + ab.getName() + " id=" + myid);
    							o = ab.getStateMachine().getReferenceObjectFromID(myid);
    							//TraceManager.addDev("Adding component to inv   block=" + ab.getName());
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
    		}
    		//inv.setTokenValue(valToken);
    		
    		inv.computeValue();
    		
    		if (!(ignoreInvariants.isSelected() && sameBlock)) {
            		mgui.gtm.addInvariant(inv);
            		jtainvariants.append(inv + "\n");
            	} else {
            		//TraceManager.addDev("Invariant ignored " + inv);
            		jtainvariants.append("Ignored invariant: " + inv + "\n");
            		ignored ++;
            	}
    	}*/
    }
    
    public void farkasInvariants(IntMatrix im, boolean heuristics) throws InterruptedException {
    	TraceManager.addDev("Computing invariants with Farkas");
    	 int nbOfColumn = im.sizeColumn;
    	 String names[] = new String[im.sizeRow];
    	 for(int k=0; k<im.sizeRow; k++) {
    	 	 names[k] = im.getNameOfLine(k);
    	 }
    	 
    	 im.putShortNames();
    	 
    	 
    	 im.startFarkas(true, heuristics);
    	 boolean cont = true;
    	 double perc;
    	 String percS;
    	 while(cont) {
    	 	 try {
    	 	 	 perc = (double)(im.getPercentageCompetion());
    	 	 	 percS = String.format("%.2f", perc/100);
    	 	 	 //TraceManager.addDev("PercS=" + percS);
    	 	 	 info.setText(percS+" %, matrix:" + im.sizeRow + "x" + im.sizeColumn);
    	 	 	 Thread.sleep(100);
    	 	 	 if (im.isFinished()) {
    	 	 	 	 cont = false;
    	 	 	 }
    	 	 } catch (Exception e) {
    	 	 	 cont = false;
    	 	 }
    	 }
    	
    	 
    	 if (im.wasInterrupted()) {
    	 	 return;
    	 }
    	 
    	 testGo();
    	 
    	  
    	 TraceManager.addDev("Invariants computed. Analyzing them.");
    	 
    	 info.setText("");
    	 
    	 	if ((im.sizeRow < 100) && (im.sizeColumn<100)) {
    	 		jtamatrixafterfarkas.append("Incidence matrix after Farkas: " + im.sizeRow + "x" + im.sizeColumn +"\n" + im.toString() + "\n\n");
            } else {
            	jtamatrixafterfarkas.append("Incidence matrix after Farkas: " + im.sizeRow + "x" + im.sizeColumn +"\n" + "(matrix is tool arge to be displayed)" + "\n\n");
            }
            
            jta.append("Farkas applied to incidence matrix\n");
            testGo();
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
         //   int state;
            int valToken = 0;
            
            boolean sameBlock;
            AvatarBlock prevBlock, prevBlock1;
         //   int ignored = 0;
            TGComponent tgc1, tgc2;
            
            int valLine;
            int cptInv = 1;
            BitSet bs;
            int cptBs;
            
            
            jtainvariants.append("Computed invariants:\n-----------------\n");
            testGo();
            
            
            // We are interested only in minimal invariants, that is, invariants with at most one token
            // That is, we ignore lines of the matrix for which more than one start state is present
            
            for(int i=0; i<im.getNbOfLines(); i++) {
            	prevBlock = null;
            	prevBlock1 = null;
            	sameBlock = true;
            	
            	// With names of lines
            	//name =  im.getNameOfLine(i);
            	//name = Conversion.replaceAllString(name, "+", "&");
            	//elts = name.split("&");
            	
            	// With bits sets
            	name = "";
            	bs = im.bitSetOfMatrix[i];
            	elts = new String[bs.cardinality()];
            	
            	cptBs = 0;
            	for(int k=0; k<bs.size(); k++) {
            		if (bs.get(k)) {
            			elts[cptBs] = ""+k;
            			cptBs++;
            		}
            	}
            	
            	
            	valToken = 0;
       
            	inv = new Invariant("#" + cptInv);
            	inv.setValue(im.getValueOfLineFromColumn(nbOfColumn, i));
            	
            	// Putting components

            //	state = 0;
            	for(int j=0; j<elts.length; j++) {
            		
            		tmp = elts[j].trim();
            		try {
            			valLine = Integer.decode(tmp).intValue();
            			tmp = names[valLine];
            			elts[j] = tmp;
            		} catch (Exception e) {
            			TraceManager.addDev("Line badly formatted:" + tmp);
            		}
            		
            		
            		//TraceManager.addDev("#" + j + "=" + elts[j] + " tmp=" + tmp);
            		
            		if (tmp.startsWith("Synchro_from_")) {
            			tmp =tmp.substring(13, tmp.length()).trim();
            			int index = tmp.indexOf("_to_");
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
            						if (o != null) {
            							tgc1 = (TGComponent)o;
            						} else {
            							tgc1 = null;
            						}
            						
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
            						if (o != null) {
            							tgc2 = (TGComponent)o;
            						} else {
            							tgc2 = null;
            						}
            						
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
            				//TraceManager.addDev("Getting block with name=" + tmps[0]);
            				ab = avspec.getBlockWithName(tmps[0]);
            				if (ab != null) {
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
									//TraceManager.addDev("trying ... tmps=" + tmps[tmps.length-1] + "ab=" + ab);
									myid = Integer.decode(tmps[tmps.length-1]).intValue();
									//TraceManager.addDev("Adding component to inv   block=" + ab.getName() + " id=" + myid);
									o = ab.getStateMachine().getReferenceObjectFromID(myid);
									//TraceManager.addDev("Adding component to inv   block=" + ab.getName());
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
            	}
            	inv.setTokenValue(valToken);
            	
            	inv.computeValue();
            	
            	if (valToken == 1) {
					if (!(ignoreInvariants.isSelected() && sameBlock)) {
						mgui.gtm.addInvariant(inv);
						cptInv ++;
						jtainvariants.append(inv + "\n");
					} else {
						//TraceManager.addDev("Invariant ignored " + inv);
						jtainvariants.append("Ignored invariant: " + inv + "\n");
					//	ignored ++;
					}
            	}
            	
            }
    }
    
    public void run() {
    	TPN tpn;
      //  hasError = false;
		
        
		TraceManager.addDev("Invariants Thread started");
		

        
        try {
        	jta.append("\n*** WARNING: Invariants do NOT take into account variables nor time constraints ***\n");
        	jta.append("Clearing invariants on diagrams\n");
        	mgui.gtm.clearInvariants();
        	mgui.gtm.clearGraphicalInfoOnInvariants();
            jta.append("Generating Petri Net\n");
            tpn = mgui.gtm.generateTPNFromAvatar();
            if ((tpn.getNbOfPlaces() <100) && (tpn.getNbOfTransitions() < 100)) {
            	jtatpn.append("Petri Net (" + tpn.getNbOfPlaces() + " places, " + tpn.getNbOfTransitions() + " transitions):\n" + tpn.toString() + "\n\n");
            } else {
            	jtatpn.append("Petri Net (" + tpn.getNbOfPlaces() + " places, " + tpn.getNbOfTransitions() + " transitions):\n" + "(Petri net is too large to be displayed)" + "\n\n");
            }
            String ret = mgui.saveTPNNDRFormat(tpn.toNDRFormat());
            jta.append(ret + "\n");
            testGo();
            
            
            jta.append("Computing incidence matrix\n");
            im = tpn.getIncidenceMatrix();
         //   int nbOfColumn = im.sizeColumn;
            if ((im.sizeRow < 100) && (im.sizeColumn<100)) {
    	 		jtamatrix.append("Incidence matrix: " + im.sizeRow + "x" + im.sizeColumn +"\n" + im.toString() + "\n\n");
            } else {
            	jtamatrix.append("Incidence matrix: " + im.sizeRow + "x" + im.sizeColumn +"\n" + "(matrix is tool arge to be displayed)" + "\n\n");
            }
            jta.append("Incidence matrix computed\n");
            jta.append("Computing minimal invariants\n");
            testGo();
            
            if (PIPEButton.isSelected()) {
            	pipeInvariants(tpn, im);
            } else {
            	farkasInvariants(im, farkasHeuristicsButton.isSelected());
            }
            
            im = null;
            
            jta.append("Invariants computed: *" + mgui.gtm.getInvariants().size() + "* invariants\n");
            testGo();
            jta.append("Computing mutual exclusions\n");
            int mutex = mgui.gtm.computeMutex();
            mgui.getCurrentTDiagramPanel().repaint();
            switch(mutex) {
            case -1:
            	jta.append("Error when computing mutual exclusion\n");
            	break;
            case -2:
            	jta.append("No mutual exclusion to study\n");
            	break;
            case -3:
            	jta.append("Mutual exclusion cannot be proved\n");
            	break;
            default:
            	jta.append("Mutual exclusion is satisfied\n");
            }
            //jta.append("Mutual exclusions return value: " + mutex + "\n");
            
            TGComponent tgc = mgui.hasCheckableMasterMutex();
            if ((tgc != null) && (tgc instanceof AvatarSMDState)){
            	AvatarSMDState astate = (AvatarSMDState)tgc;
            	jta.append("Search for states in mutual exclusion with: " + astate.getStateName() + "\n");
            	int nbOfStates = mgui.gtm.computeMutexStatesWith(astate);
            	if (nbOfStates == -1) {
            		jta.append("Error when computing mutual exclusion\n");
            	} else {
            		jta.append("" + nbOfStates + " state(s) found\n");
            	}
            	
            }
            
            if (computeAllMutualExclusions.isSelected()) {
            	jta.append("Computing mutual exclusions for all states\n");
            	
            	mgui.gtm.computeAllMutualExclusions();
            }
            
            jta.append("All done\n");
            
            
            
        } catch (InterruptedException ie) {
        	jta.append("Interrupted\n");
        }
        
        jta.append("\n\nReady to process next command\n");
        
        checkMode();
        setButtons();
        
        //
         
    	 TraceManager.addDev("Invariants thread completed");
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
//    
//    public void setError() {
//    	hasError = true;
//    }
}
