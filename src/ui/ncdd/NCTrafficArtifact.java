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
 * Class NCTrafficArtifact
 * Traffic artifact of a network calculus diagram
 * Creation: 18/11/2008
 * @version 1.0 18/11/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.ncdd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class NCTrafficArtifact extends TGCWithoutInternalComponent implements SwallowedTGComponent, WithAttributes {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int textY2 =  40;
    protected int space = 5;
    protected int fileX = 15;
    protected int fileY = 20;
    protected int cran = 5;
	
    protected String oldValue = "";
	
	protected int periodicType = 0; // 0: periodic ; 1: aperiodic
	protected int deadline = 10;
	protected int period = 10;
	protected String periodUnit = "ms"; // "us", "ms";
	protected String deadlineUnit = "ms"; // "us", "ms";
	protected int minPacketSize = 20;
	protected int maxPacketSize = 40;
	protected int priority = 0; // 0 to 3
    
    public NCTrafficArtifact(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		width = 60;
        height = 38;
        minWidth = 60;
        
        nbConnectingPoint = 0;
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = tdp.findNodeName("T");;
        
        //makeFullValue();
        
        myImageIcon = IconManager.imgic702;
    }
    
    public void internalDrawing(Graphics g) {
        
        if (oldValue.compareTo(value) != 0) {
            setValue(value, g);
        }
        
        g.drawRect(x, y, width, height);
		Color c = g.getColor();
		g.setColor(ColorManager.CPU_BOX_2);
		g.fillRect(x+1, y+1, width-1, height-1);
        g.setColor(c);
		
        //g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawLine(x+width-space-fileX, y + space, x+width-space-fileX, y+space+fileY);
        g.drawLine(x+width-space-fileX, y + space, x+width-space-cran, y+space);
        g.drawLine(x+width-space-cran, y+space, x+width-space, y+space + cran);
        g.drawLine(x+width-space, y+space + cran, x+width-space, y+space+fileY);
        g.drawLine(x+width-space, y+space+fileY, x+width-space-fileX, y+space+fileY);
        g.drawLine(x+width-space-cran, y+space, x+width-space-cran, y+space+cran);
        g.drawLine(x+width-space-cran, y+space+cran, x + width-space, y+space+cran);
        
        g.drawString(value, x + textX , y + textY);
        
    }
    
    public void setValue(String val, Graphics g) {
        oldValue = value;
        int w  = g.getFontMetrics().stringWidth(value);
		int w1 = Math.max(minWidth, w + 2 * textX + fileX + space);
		
        //System.out.println("width=" + width + " w1=" + w1 + " w2=" + w2 + " value=" + value);
        if (w1 != width) { 
            width = w1;
            resizeWithFather();
        }
        //System.out.println("width=" + width + " w1=" + w1 + " value=" + value);
    }
    
    public void resizeWithFather() {
        if ((father != null) && ((father instanceof NCEqNode) || (father instanceof NCSwitchNode))) {
            //System.out.println("cdRect comp");
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }
    
    
     public boolean editOndoubleClick(JFrame frame) {
		String tmp;
		boolean error = false;
		String oldValue = value;
		
		JDialogNCTraffic dialog = new JDialogNCTraffic(frame, "Setting traffic attributes", value, periodicType, period, periodUnit, deadline, deadlineUnit, minPacketSize, maxPacketSize, priority);
		dialog.setSize(300, 350);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (dialog.hasBeenCancelled()) {
			return false;
		}
		
		tmp = dialog.getValue().trim();
		
		if (tmp == null) {
			error = true;
		}
		
		if ((tmp != null) && (tmp.length() > 0) && (!tmp.equals(oldValue))) {
			if (!TAttribute.isAValidId(tmp, false, false)) {
                JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Traffic: the new name is not a valid name",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
                error = true;
            }
            
            if (!tdp.isNCNameUnique(tmp)) {
                JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Traffic: the new name is already in use",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
                error = true;
            }
		}
		
		if (!error) {
			value = tmp;
		}
		
		periodicType = dialog.getPeriodicType();
		priority = dialog.getPriority();
		period = dialog.getPeriod();
		periodUnit = dialog.getPeriodUnit();
		deadline = dialog.getDeadline();
		deadlineUnit = dialog.getDeadlineUnit();
		maxPacketSize = dialog.getMaxPacketSize();
		minPacketSize = dialog.getMinPacketSize();
			
		return !error;
      
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
        return TGComponentManager.NCDD_TRAFFIC_ARTIFACT;
    }
    
   protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info value=\"" + value + "\" periodicType=\"");
        sb.append(periodicType);
		sb.append("\" period=\"");
		sb.append(period);
		sb.append("\" periodUnit=\"");
		sb.append(deadlineUnit);
		sb.append("\" deadline=\"");
		sb.append(deadline);
		sb.append("\" deadlineUnit=\"");
		sb.append(deadlineUnit);
		sb.append("\" minPacketSize=\"");
		sb.append(minPacketSize);
		sb.append("\" maxPacketSize=\"");
		sb.append(maxPacketSize);
		sb.append("\" priority=\"");
		sb.append(priority);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            int t1id;
            String svalue = null, s0 = null, s1 = null, s2 = null, s3 = null, s4 = null, s5 = null, s6 = null, s7 = null;
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; i<nli.getLength(); i++) {
                        n2 = nli.item(i);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                svalue = elt.getAttribute("value");
                                s0 = elt.getAttribute("periodicType");
                                s1 = elt.getAttribute("deadline");
								s4 = elt.getAttribute("deadlineUnit");
								s5 = elt.getAttribute("minPacketSize");
								s2 = elt.getAttribute("maxPacketSize");
								s3 = elt.getAttribute("priority");
								s6 = elt.getAttribute("period");
								s7 = elt.getAttribute("periodUnit");
                            }
							//System.out.println("Decoding traffic s0=" + s0 + " s1=" + s1 + " s2=" + s2 + " s3=" + s3);
                            if (svalue != null) {
                                value = svalue;
                            } 
							//System.out.println("Decoding traffic s0=" + s0 + " s1=" + s1 + " s2=" + s2 + " s3=" + s3);
                           
                            if (s0 != null){
								periodicType = Integer.decode(s0).intValue();
                            }
							//System.out.println("Decoding traffic s0=" + s0 + " s1=" + s1 + " s2=" + s2 + " s3=" + s3);
                           
							if (s6 != null){
								period = Integer.decode(s6).intValue();
                            }
							//System.out.println("Decoding traffic s0=" + s0 + " s1=" + s1 + " s2=" + s2 + " s3=" + s3);
                           
							if ((s7 != null) && (s7.length() > 0)) {
								periodUnit = s7;
							}
							
							if (s1 != null){
								deadline = Integer.decode(s1).intValue();
                            }
							//System.out.println("Decoding traffic s0=" + s0 + " s1=" + s1 + " s2=" + s2 + " s3=" + s3);
                           
							if ((s4 != null) && (s4.length() > 0)) {
								deadlineUnit = s4;
							}
							
							if ((s5 != null) && (s5.length() > 0)) {
								minPacketSize = Integer.decode(s5).intValue();
                            }
							
							if (s2 != null){
								maxPacketSize = Integer.decode(s2).intValue();
                            }
							//System.out.println("Decoding traffic s0=" + s0 + " s1=" + s1 + " s2=" + s2 + " s3=" + s3);
                           
							if (s3 != null){
								priority = Integer.decode(s3).intValue();
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
			System.out.println("Decoding traffic: failed");
                           
            throw new MalformedModelingException();
        }
        //makeFullValue();
    }
    
    
    public int getPeriodicType() {
        return periodicType;
    }
	
	public int getPeriod() {
        return period;
    }
	
	public String getPeriodUnit() {
        return periodUnit;
    }
	
	public int getDeadline() {
        return deadline;
    }
	
	public String getDeadlineUnit() {
        return deadlineUnit;
    }
	
	public int getMaxPacketSize() {
        return maxPacketSize;
    }     
	
	public int getMinPacketSize() {
        return minPacketSize;
    }
	
	public int getPriority() {
        return priority;
    }
	
	public String getAttributes() {
		String ret = "";
		if (periodicType == 0) {
			ret += "Periodic\n";
		} else {
			ret += "Aperioridic\n";
		}
		ret += "Period = " + period + " " + periodUnit + "\n";
		ret += "Deadline = " + deadline + " " + deadlineUnit + "\n";
		ret += "Min packet size = " + minPacketSize + " B\n";
		ret += "Max packet size = " + maxPacketSize + " B\n";
		ret += "Priority = " + priority; 
		
		return ret;
	}

    
}
