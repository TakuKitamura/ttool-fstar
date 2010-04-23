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
* Class AvatarRDProperty
* Avatar property: to be used in avatar requirement diagrams
* Creation: 20/04/2010
* @version 1.0 20/04/2010
* @author Ludovic APVRILLE
* @see
*/

package ui.avatarrd;



import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class AvatarRDProperty extends TGCScalableWithInternalComponent implements TGAutoAdjust {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 22;
	protected int lineHeight = 30;
	private double dlineHeight = 0.0;
    //protected int startFontSize = 10;
    protected Graphics graphics;
    //protected int iconSize = 30;
	
	private Font myFont, myFontB;
	private int maxFontSize = 30;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
	
    protected final static String PROPERTY = "<<Property>>";
	
	protected String diagramText;
	protected String violatedAction = "noAction";
	
	private int iconSize = 18;
	private boolean iconIsDrawn = false;
    
    public AvatarRDProperty(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        initScaling(150, lineHeight);
		oldScaleFactor = tdp.getZoom();
		dlineHeight = lineHeight * oldScaleFactor;
		lineHeight = (int)dlineHeight;
		dlineHeight = dlineHeight - lineHeight;
		
		minWidth = 10;
        minHeight = lineHeight;
        
        nbConnectingPoint = 12;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 0.0, 0.25);
        connectingPoint[1] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 0.0, 0.5);
        connectingPoint[2] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 0.0, 0.75);
        connectingPoint[3] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 1.0, 0.25);
        connectingPoint[4] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 1.0, 0.5);
        connectingPoint[5] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 1.0, 0.75);
        connectingPoint[6] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 0.25, 0.0);
        connectingPoint[7] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 0.5, 0.0);
        connectingPoint[8] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 0.75, 0.0);
        connectingPoint[9] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 0.25, 1.0);
		connectingPoint[10] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 0.5, 1.0);
		connectingPoint[11] = new AvatarRDConnectingPointVerify(this, 0, 0, false, true, 0.75, 1.0);
		
        addTGConnectingPointsCommentTop();    
        
        nbInternalTGComponent = 0;
        //tgcomponent = new TGComponent[nbInternalTGComponent];
        
        int h = 1;
        //TAttributeRequirement tgc0;
        //tgc0 = new TAttributeRequirement(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        //tgcomponent[0] = tgc0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
		multieditable = true;
		
        
        // Name of the observer
        name = "AvatarProperty";
        value = "AvatarProperty";
		//value = tdp.findRequirementName("Requirement_");
        oldValue = value;
        
        myImageIcon = IconManager.imgic5100;
		
		diagramText = "no diagram";
        
        actionOnAdd();
    }
	
    
	public void internalDrawing(Graphics g) {
		Font f = g.getFont();
		Font fold = f;
		int w, c;
		int size;
		
        if (!tdp.isScaled()) {
            graphics = g;
        }
		
		if (((rescaled) && (!tdp.isScaled())) || myFont == null) {
			currentFontSize = tdp.getFontSize();
			//System.out.println("Rescaled, font size = " + currentFontSize + " height=" + height);
			myFont = f.deriveFont((float)currentFontSize);
			myFontB = myFont.deriveFont(Font.BOLD);
			
			if (rescaled) {
				rescaled = false;
			}
		}
		
		if(currentFontSize <minFontSize) {
			displayText = false;
		} else {
			displayText = true;
		}
		
		int h  = g.getFontMetrics().getHeight();
        
		g.drawRect(x, y, width, height);
        
		g.drawLine(x, y+lineHeight, x+width, y+lineHeight);
		g.setColor(ColorManager.AVATAR_REQUIREMENT_TOP);
        g.fillRect(x+1, y+1, width-1, lineHeight-1);
		g.setColor(ColorManager.AVATAR_REQUIREMENT_ATTRIBUTES);
		g.fillRect(x+1, y+1+lineHeight, width-1, height-1-lineHeight);
		ColorManager.setColor(g, getState(), 0);
		if ((lineHeight > 23) && (width > 23)){
			g.drawImage(IconManager.img5100, x + width - iconSize + 1, y + 3, Color.yellow, null);
		}
		
		if (displayText) {
			size = currentFontSize - 2;
			g.setFont(myFont.deriveFont((float)(myFont.getSize() - 2)));
			drawLimitedString(g, PROPERTY, x, y + size, width, 1);
			size += currentFontSize;
			g.setFont(myFontB);
			w = g.getFontMetrics().stringWidth(value);
			drawLimitedString(g, value, x, y + size, width, 1);
			
		}
		
		/*g.setFont(myFont);
		
		size = lineHeight + currentFontSize;
		if (size < (height - 2)) {
			drawLimitedString(g, "Diagram=\"" + diagramText + "\"", x + textX, y + size, width, 0);
			size += currentFontSize;
			// Violated action
			if (size < (height - 2)) {
				drawLimitedString(g, "Violated_Action=\"" + violatedAction + "\"", x + textX, y + size, width, 0);
			}
		}*/
        g.setFont(f);
    }
    
    public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
        oldValue = value;
        
		if ((displayText) && (_y <= (y + lineHeight))) {
			String texti = getName() + ": ";
			if (hasFather()) {
				texti = getTopLevelName() + " / " + diagramText;
			}
			String s = (String)JOptionPane.showInputDialog(frame, texti,
				"setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
				null,
				getValue());
			
			if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
				//boolean b;
				if (!TAttribute.isAValidId(s, false, false)) {
					JOptionPane.showMessageDialog(frame,
						"Could not change the name of the Property: the new name is not a valid name",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				
				if (!tdp.isRequirementNameUnique(s)) {
					JOptionPane.showMessageDialog(frame,
						"Could not change the name of the Property: the new name is already in use",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				
				
				int size = graphics.getFontMetrics().stringWidth(s) + iconSize + 5;
				minDesiredWidth = Math.max(size, minWidth);
				if (minDesiredWidth != width) {
					newSizeForSon(null);
				}
				setValue(s);
				
				if (tdp.actionOnDoubleClick(this)) {
					return true;
				} else {
					JOptionPane.showMessageDialog(frame,
						"Could not change the name of the Property: this name is already in use",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
					setValue(oldValue);
				}
			}
			return false;
		} else {
			//return editAttributes();
			return false;
		}
        
    }
	
	/*public boolean editAttributes() {
		JDialogObserver jdo = new JDialogObserver(tdp.getGUI().getFrame(), "Setting diagrams of Observer " + getRequirementObserverName(), diagramText, violatedAction);
		jdo.setSize(750, 400);
		GraphicLib.centerOnParent(jdo);
		jdo.show();
		
		if (!jdo.isRegularClose()) {
			return false;
		}
		
		diagramText = jdo.getText();
		violatedAction = jdo.getViolatedAction();
		
		return true;
	}*/
	
	public void rescale(double scaleFactor){
		dlineHeight = (lineHeight + dlineHeight) / oldScaleFactor * scaleFactor;
		lineHeight = (int)(dlineHeight);
		dlineHeight = dlineHeight - lineHeight; 
		minHeight = lineHeight;
		
		super.rescale(scaleFactor);
	}
    
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public String getRequirementObserverName() {
        return value;
    }
    
    
    public  int getType() {
        return TGComponentManager.AVATARRD_PROPERTY;
    }
    
	/* public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
	componentMenu.addSeparator();
	JMenuItem generate = null;
	// Should verify first whether it is connected to a formal requirement with a verify relation, or not
	generate = new JMenuItem("Generate on diagrams");
	
	generate.addActionListener(menuAL);
	componentMenu.add(generate);
	
	JMenuItem editAttributes = new JMenuItem("Edit attributes");
	editAttributes.addActionListener(menuAL);
	componentMenu.add(editAttributes);
    }
    
    public boolean eventOnPopup(ActionEvent e) {
	String s = e.getActionCommand();
	if (s.indexOf("diagrams") > -1) {
	// To be implemented!
	} else {
	return editAttributes();
	}
	return true;
    }*/
    
    public String toString() {
        String ret =  getValue() + PROPERTY;
        return ret;
    }
	
	public void autoAdjust(int mode) {
		//System.out.println("Auto adjust in mode = " + mode);
		
		if (graphics == null) {
			return;
		}
		
		Font f = graphics.getFont();
		Font f0 = f.deriveFont((float)currentFontSize);
		Font f1 = f0.deriveFont(Font.BOLD);
		Font f2 = f.deriveFont((float)(currentFontSize - 2));
		
		// Must find for both modes which width is desirable
		String s0, s1;
		
		s0 = PROPERTY;
		
		graphics.setFont(f2);
		int w0 = graphics.getFontMetrics().stringWidth(s0);
		graphics.setFont(f1);
		int w1 = graphics.getFontMetrics().stringWidth(value);
		int w2 = Math.max(w0, w1) + (2 * iconSize);
		graphics.setFont(f0);
		/*int w3 = graphics.getFontMetrics().stringWidth("Diagram=\"" + diagramText + "\"") + textX;
		int w4 = graphics.getFontMetrics().stringWidth("Violated_Action=\"" + violatedAction + "\"") + textX;
		graphics.setFont(f);
		
		w2 = Math.max(w2, w3);
		w2 = Math.max(w2, w4);*/
		if (mode == 1) {
			resize(w2, lineHeight);
			return;
		}
		
		int h = (3 * currentFontSize) + lineHeight;
		
		resize(w2, h);
		
	}
	
}