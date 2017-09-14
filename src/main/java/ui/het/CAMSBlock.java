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




package ui.het;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogCAMSBlocks;
import heterogeneoustranslator.systemCAMStranslator.*;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Vector;


/**
* Class CAMSBlock
* Block for SystemC-AMS Diagrams
* Creation: 27/06/2017
* @version 1.0 27/06/2017
* @author Côme DEMARIGNY
 */
public class CAMSBlock extends TGComponent {
    private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 2;
    private int derivationy = 3;
    private String stereotype = "block";
    
    private int nbOfIn = 0;
    private int nbOfOut = 0;
    private int nbOfHybridIn = 0;
    private int nbOfHybridOut = 0;
    private int totIn = 0;
    private int totOut = 0;

    protected int index = 0;
    
    private int maxFontSize = 12;
    private int minFontSize = 4;
    private int currentFontSize = -1;
    private boolean displayText = true;
    private int textX = 7;
	
    private int limitName = -1;
    private int limitAttr = -1;
    private int limitMethod = -1;
    
    public String name;
    public String Value= "Block0";
    private JDialogCAMSBlocks dialog;

    // TAttribute, ProcessCode, CAMSSignal
    protected LinkedList<TAttribute> myAttributes;
    protected LinkedList<CAMSSignal> mySignals;
    protected String [] processCode;

    //Simulation Object
    private CAMSBlocks SBlock;
    
    public CAMSBlock (int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
	tdp = _tdp;
        width = 250;
        height = 200;
        minWidth = 150;
        minHeight = 100;
        
	createConnectingPoints();
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
	getBlockName();
        
        myImageIcon = IconManager.imgic700;

	if(this.myAttributes == null){this.myAttributes = new LinkedList<TAttribute>();}
	if(this.mySignals == null){this.mySignals = new LinkedList<CAMSSignal>();}
	
	SBlock= new CAMSBlocks(name, nbOfIn, nbOfOut, nbOfHybridIn, nbOfHybridOut, myAttributes, mySignals, processCode);
    }

    public void createConnectingPoints(){
	int i;
	double h;
	
	resetInOut();

	if(this.mySignals!=null){
	    for(i=0;i<mySignals.size();i++){	
		if(mySignals.get(i).getInout()==0){
		    nbOfIn++;
		} else if(mySignals.get(i).getInout()==1) {
		    nbOfOut++;
		} else if(mySignals.get(i).getInout()==2) {
		    nbOfHybridIn++;
		} else{
		    nbOfHybridOut++;
		}
	    }
	}

	nbConnectingPoint = nbOfIn + nbOfOut + nbOfHybridIn + nbOfHybridOut;
	totIn =  nbOfIn + nbOfHybridIn;
	totOut = nbOfOut + nbOfHybridOut;

        connectingPoint = new CAMSConnectingPoint[nbConnectingPoint];
        
	for (i = 1; i<= totIn; i++){
	    h = i/(totIn + 1.0);
	    if((i-1)<nbOfIn){
		connectingPoint[i-1] = new CAMSConnectingPoint(this, 0, 0, true, false, false, 0.0, h);
	    } else{
		connectingPoint[i-1] = new CAMSConnectingPoint(this, 0, 0, true, false, true, 0.0, h);
	    }
	}
	
	for (i = 1; i<=totOut; i++){
	    h = i/(totOut + 1.0);
	    if ((i+totIn-1)<(nbOfOut + totIn)){
		connectingPoint[i+totIn-1] = new CAMSConnectingPoint(this, 0, 0, false, true, false, 1.0, h);
	    } else {
		connectingPoint[i+totIn-1] = new CAMSConnectingPoint(this, 0, 0, false, true, true, 1.0, h);
	    }
	}
	
        addTGConnectingPointsComment();
    }
    
     public void internalDrawing(Graphics g)  {
	 Color c = g.getColor();
	 g.draw3DRect(x, y, width, height, true);
	 
	 
	 // Top lines
	 g.drawLine(x, y, x + derivationx, y - derivationy);
	 g.drawLine(x + width, y, x + width + derivationx, y - derivationy);
	 g.drawLine(x + derivationx, y - derivationy, x + width + derivationx, y - derivationy);
	 
	 // Right lines
	 g.drawLine(x + width, y + height, x + width + derivationx, y - derivationy + height);
	 g.drawLine(x + derivationx + width, y - derivationy, x + width + derivationx, y - derivationy + height);
	 
	 // Filling color
	 g.setColor(ColorManager.HWA_BOX);
	 g.fill3DRect(x+1, y+1, width-1, height-1, true);
	 g.setColor(c);
	
	 // Strings
	 String ster = "<<" + stereotype + ">>";
	 int w  = g.getFontMetrics().stringWidth(ster);
	 Font f = g.getFont();
	 g.setFont(f.deriveFont(Font.BOLD));
	 g.drawString(ster, x + (width - w)/2, y + textY1);
	 g.setFont(f);
	 w  = g.getFontMetrics().stringWidth(name);
	 g.drawString(name, x + (width - w)/2, y + textY2);
	 

	 actionOnAdd();
     }
    
    public void addSignal(CAMSSignal sig){
	mySignals.add(sig);
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        return null;
    }

    public void setState(int _s){
    }

    public TGComponent isOnMe(int _x, int _y){
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
    	return null;
    }
    
    public String getStereotype() {
        return stereotype;
        
    }
    
    public boolean editOndoubleClick(JFrame frame) {
	
	boolean error = false;
	String errors = "";
	int tmp;
	String tmpName;
	boolean changeMade=false;
	
	if(dialog ==null){
	dialog = new JDialogCAMSBlocks(this.myAttributes, this.mySignals, null, frame, "Setting Block attributes", "Attributes", this, processCode, true);
	}
	setJDialogOptions(dialog);
	GraphicLib.centerOnParent(dialog, 1050, 700);
	dialog.setVisible( true ); // blocked until dialog has been closed
	mySignals = dialog.getSignals();
	createConnectingPoints();
	getBlockName();
	setSimulationBlock();
	
	
        if (error) {
            JOptionPane.showMessageDialog(frame,
                                          "Invalid value for the following attributes: " + errors,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }

    
    @Override
	public void loadExtraParam(NodeList bl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
	    
            NodeList bli;
            Node b1, b2;
            Element elt;
            String sstereotype = null, sblockName = null;
	    
            for(int i=0; i<bl.getLength(); i++) {
                b1 = bl.item(i);
                //System.out.println(n1);
                if (b1.getNodeType() == Node.ELEMENT_NODE) {
                    bli = b1.getChildNodes();
                    for(int j=0; j<bli.getLength(); j++) {
                        b2 = bli.item(j);
                        if (b2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) b2;
                            if (elt.getTagName().equals("info")) {
                                sstereotype = elt.getAttribute("stereotype");
                                sblockName = elt.getAttribute("blockName");
                            }
                            if (sstereotype != null) {
                                stereotype = sstereotype;
                            }
                            if (sblockName != null){
                                name = sblockName;
                            }
			    
                            if (elt.getTagName().equals("attributes")) {
				
                                nbOfIn = Integer.decode(elt.getAttribute("nbOfIn")).intValue();
                                nbOfOut =Integer.decode(elt.getAttribute("nbOfOut")).intValue();
				
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
	    System.out.println("load failure");
            throw new MalformedModelingException();
        }
    }

    public void setSimulationBlock(){
	SBlock.setBlockName(name);
	SBlock.setNbOfIn(nbOfIn);
	SBlock.setNbOfOut(nbOfOut);
	SBlock.setNbOfHybridIn(nbOfHybridIn);
	SBlock.setNbOfHybridOut(nbOfHybridOut);
	SBlock.setMyAttributes(myAttributes);
	SBlock.setMySignals(mySignals);
	SBlock.setProcessCode(processCode);
    }

    protected void setJDialogOptions(JDialogCAMSBlocks _jdab) {
        _jdab.addAccess(TAttribute.getStringAccess(TAttribute.PRIVATE));
	_jdab.addAccess(TAttribute.getStringAccess(TAttribute.PUBLIC));
        _jdab.addType(TAttribute.getStringCAMSType(TAttribute.BOOLEAN), true);
        _jdab.addType(TAttribute.getStringCAMSType(TAttribute.DOUBLE), true);
        _jdab.enableInitialValue(true);
        _jdab.enableRTLOTOSKeyword(false);
        _jdab.enableJavaKeyword(false);
    }
    
    public String getAttributes() {
        String attr = "";
        attr += "Nb of in = " + totIn + "\n";
        attr += "Nb of out = " + totOut + "\n";

        return attr;
    }

    public int getType() {
        return TGComponentManager.CAMS_BLOCK;
    }
    
    public boolean hasBlockWithName(){
	return true;
    }
    
    public void getBlockName() {
    	if(dialog != null) {
    	    name = dialog.getBlockName();
	    if (name.length()==0){
		name = tdp.findCAMSBlockName("Block");
		return ;
	    }
	    return ;
	}
	name = Value;
    }

    public void resetInOut(){
	nbOfIn = 0;
	nbOfOut= 0;
	nbOfHybridIn = 0;
	nbOfHybridOut= 0;
	totIn = 0;
	totOut= 0;
    }

    public int getNbOfIn() {
        return totIn;
    }

    public int getNbOfOut() {
        return totOut;
    }

   public int getNbOfHybridIn() {
        return nbOfHybridIn;
    }

    public int getNbOfHybridOut() {
        return nbOfHybridOut;
    }

   public int getNbOfNonHybridIn() {
        return nbOfIn;
    }

    public int getNbOfNonHybridOut() {
        return nbOfOut;
    }
        
    public int getDefaultConnector() {
        return TGComponentManager.CAMS_CONNECTOR;
    }

}
