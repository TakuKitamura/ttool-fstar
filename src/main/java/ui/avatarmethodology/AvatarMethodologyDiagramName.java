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




package ui.avatarmethodology;

import myutil.GraphicLib;
import myutil.TraceManager;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;


/**
 * Class AvatarMethodologyDiagramName
 * Internal component that shows the diagram name and validation/simu
 * references
 * Creation: 26/08/2014
 * @version 1.0 26/08/2014
 * @author Ludovic APVRILLE
 */
public class AvatarMethodologyDiagramName extends TGCScalableWithoutInternalComponent implements SwallowedTGComponent {
    //protected boolean emptyText;
    
    public final static int X_MARGIN = 5;
    public final static int Y_MARGIN = 3;
    
    protected final static int SIM_ANIM = 0;
    protected final static int UPP = 1;
    protected final static int PROVERIF = 2;
	protected final static int INVARIANTS = 3;
	protected final static int PROTO = 4;
	
	
	protected final String[] SHORT_ACTION_NAMES = {
	"simu", "upp", "proverif", "inv", 
	"code-gen"
	};
	
	protected final String[] LONG_ACTION_NAMES = {
	/*0*/ "Simulation and animate the model", 
	"Verify safety propeties on the model with UPPAAL", 
	"Verify security properties on the model with ProVerif",
	"Verify mutual exclusions on the model with invariants",
	"Generate executable code", 
	};
	
	protected int[] validations;
	protected int[] valMinX;
	protected int[] valMaxX;
	
	protected int indexOnMe; // -1 -> on main element. -2: on not precise element; Other: on a validations item.
	
	private int myWidth, myHeight, widthAppli;
	
	
    public AvatarMethodologyDiagramName(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        //Issue #31
        minWidth = 10;
        minHeight = lineLength;
        initScaling(10, 10);
        
        nbConnectingPoint = 0;
        minWidth = 10;
        nbInternalTGComponent = 0;
        
        moveable = false;
        editable = true;
        removable = false;
        
        name = "value ";
        


        myImageIcon = IconManager.imgic302;
    }

    /*Issue #31 Was used in the previous internalDrawing refactoring that was done, now its useless
    private boolean canTextGoIntoTheFatherBox(Graphics g)
    {
        int widthText = g.getFontMetrics().stringWidth(value);
        int widthFather = getFather().getWidth();
        return widthFather >= widthText + (2 * X_MARGIN);
    }*/
    /**
     * Set the font style to "fontStyle" when the boolean "pointerOnMe" is true and indexOnMe is equal to the index
     * @param g
     * @param fontStyle
     * @param pointerOnMe
     * @param font
     * @param index
     */
    private void setFontStyleWhenPointerIsOnMe(Graphics g, int fontStyle, boolean pointerOnMe, Font font, int index) {
    	if (pointerOnMe && indexOnMe == index)
        	g.setFont(font.deriveFont(fontStyle));
    }
    
    private void manageValidations(Graphics g, Font font)  {

    	if (getFather() == null) {
    		return;
		}

        int widthText = g.getFontMetrics().stringWidth(value);
        int widthFather = getFather().getWidth();
        int curWidth = Math.max(width, myWidth);
        
    	if (validations == null)
        	if (getFather() instanceof AvatarMethodologyDiagramReference)
        		((AvatarMethodologyDiagramReference)(getFather())).makeValidationInfos(this);
        
        if ((validations != null) && (valMinX == null)) {
        	valMinX = new int[validations.length];
        	valMaxX = new int[validations.length];
        } 
        
        int currentMaxWidthX = widthFather + x - 2 * (X_MARGIN);
        int saveCurrentMaxX = currentMaxWidthX;
    	boolean oneWritten = false;
        int saveWidth = 0;
        g.setFont(font.deriveFont(Font.ITALIC));
        boolean pointerIsOnMe = tdp.componentPointed() == this ? true : false;
        
        if ((validations != null) & (validations.length > 0)) {
			for (int i = validations.length - 1; i >= 0; i--) {
				saveWidth = g.getFontMetrics().stringWidth(SHORT_ACTION_NAMES[validations[i]]);

//				if ((pointerIsOnMe && indexOnMe == i))
//					g.setFont(font.deriveFont(Font.ITALIC));
				setFontStyleWhenPointerIsOnMe(g,Font.ITALIC, pointerIsOnMe, font, i);
				if ((currentMaxWidthX - saveWidth) > (x + widthText)) 
				{
					//if ((pointerIsOnMe && indexOnMe == i))
					//	g.setFont(font.deriveFont(Font.BOLD));
					setFontStyleWhenPointerIsOnMe(g, Font.BOLD, pointerIsOnMe, font, i);
//					g.drawString(SHORT_ACTION_NAMES[validations[i]], currentMaxWidthX - saveWidth, y);
					drawSingleString(g, SHORT_ACTION_NAMES[validations[i]], currentMaxWidthX - saveWidth, y);
					
					g.setFont(font.deriveFont(Font.ITALIC));
					valMinX[i] = currentMaxWidthX-saveWidth;
					valMaxX[i] = currentMaxWidthX;
					oneWritten = true;
					currentMaxWidthX = currentMaxWidthX - saveWidth - 5;
				} else
					break;
			}
        }
        g.setFont(font);
        if (oneWritten)
        	makeScale(g, saveCurrentMaxX - x);
        else
        	makeScale(g, widthText);
        if (pointerIsOnMe) //Issue #31: The rectangle was not around the text when zoom: with scale it works better
        	//g.drawRect(x - 2, y - 12, curWidth + 5, 15);
        	g.drawRect(x - 2, y - scale(12), curWidth + 5, scale(15));
    }
    
    @Override
    protected boolean canTextGoInTheBox(Graphics g, int fontSize, String text, int iconSize) 
    {
    	myHeight = g.getFontMetrics().getHeight();
    	//int txtWidth = g.getFontMetrics().stringWidth(text) + (X_MARGIN * 2);
    	//int spaceTakenByIcon = iconSize + X_MARGIN;
    	return (fontSize + (Y_MARGIN * 2) < myHeight); // enough space in height
    			//&& (txtWidth + spaceTakenByIcon < myWidth) // enough space in width
    			//;
    }
    
    /** Issue #31: Refactored internalDrawing for more comprehension
     * Draws the text of the diagram references and the eventual validations
     * @param g
     */
    @Override
    public void internalDrawing(Graphics g)
    {
    	// Strings
    	String textDiagramRef = value;
    	//int fontSize = g.getFont().getSize();
    	//boolean tooBig = !canTextGoInTheBox(g, fontSize, textDiagramRef, 0);
//    	if (!isTextReadable(g) /*||  canTextGoInTheBox(g, fontSize, textDiagramRef, 0)*/)
//    		return;
    	Font f = g.getFont();
    	//g.drawString(textDiagramRef, x, y);
    	drawSingleString(g, textDiagramRef, x, y);
    	//validation and String
    	manageValidations(g, f);
    }
    /*
    @Override
    public void internalDrawing(Graphics g) 
    {
        	if ((y + Y_MARGIN) > (getFather().getY() + getFather().getHeight()))
        		return;

            int widthText = g.getFontMetrics().stringWidth(value);
            int widthFather = getFather().getWidth();
            //String diagramRefTextName = value;
            //if (!canTextGoIntoTheFatherBox(g))
            //    diagramRefTextName = ".";
            String diagramRefTextName = canTextGoIntoTheFatherBox(g) ? value : ".";

            Font font = g.getFont();
            boolean pointerIsOnMe = tdp.componentPointed() == this ? true : false;
            //if (pointerIsonMe && indexOnMe == -1)
            //	g.setFont(font.deriveFont(Font.BOLD));
            setFontStyleWhenPointerIsOnMe(g, Font.BOLD, pointerIsOnMe, font, -1);
            int curWidth = Math.max(width, myWidth); //int curWidth = myWidth; curWidth = Math.max(widthAppli, curWidth);
            g.drawString(diagramRefTextName, x, y);
            g.setFont(font);
            
            if (validations == null)
            	if (getFather() instanceof AvatarMethodologyDiagramReference)
            		((AvatarMethodologyDiagramReference)(getFather())).makeValidationInfos(this);
            
            if ((validations != null) && (valMinX == null)) {
            	valMinX = new int[validations.length];
            	valMaxX = new int[validations.length];
            } 
            
            int currentMaxWidthX = widthFather + x - 2 * (X_MARGIN);
            int saveCurrentMaxX = currentMaxWidthX;
            
            if (!canTextGoIntoTheFatherBox(g)) {
            	makeScale(g, widthText + (2 * X_MARGIN));
            	return;
            }

            boolean oneWritten = false;
            int saveWidth = 0;
            g.setFont(font.deriveFont(Font.ITALIC));
            
            if ((validations != null) & (validations.length > 0)) {
    			for (int i = validations.length - 1; i >= 0; i--) {
    				saveWidth = g.getFontMetrics().stringWidth(SHORT_ACTION_NAMES[validations[i]]);

//    				if ((pointerIsOnMe && indexOnMe == i))
//    					g.setFont(font.deriveFont(Font.ITALIC));
    				setFontStyleWhenPointerIsOnMe(g,Font.ITALIC, pointerIsOnMe, font, i);
    				if ((currentMaxWidthX - saveWidth) > (x + widthText)) 
    				{
    					//if ((pointerIsOnMe && indexOnMe == i))
    					//	g.setFont(font.deriveFont(Font.BOLD));
    					setFontStyleWhenPointerIsOnMe(g, Font.BOLD, pointerIsOnMe, font, i);
    					g.drawString(SHORT_ACTION_NAMES[validations[i]], currentMaxWidthX - saveWidth, y);
    					g.setFont(font.deriveFont(Font.ITALIC));
    					valMinX[i] = currentMaxWidthX-saveWidth;
    					valMaxX[i] = currentMaxWidthX;
    					oneWritten = true;
    					currentMaxWidthX = currentMaxWidthX - saveWidth - 5;
    				} else
    					break;
    			}
            }
            g.setFont(font);
            if (oneWritten)
            	makeScale(g, saveCurrentMaxX - x);
            else
            	makeScale(g, widthText);
            if (pointerIsOnMe)
            	g.drawRect(x - 2, y - 12, curWidth + 5, 15);
            
            return; 
        }
    */
    /* Issue #31
    public void internalDrawing(Graphics g) {
    	if ((y + Y_MARGIN) > (getFather().getY() + getFather().getHeight()))
    		return;
    	
    	boolean onMe = tdp.componentPointed() == this ? true : false;
    	
    	String val = value;
        int w = g.getFontMetrics().stringWidth(value);
        int wf = getFather().getWidth();

        if (wf < w + (2 * X_MARGIN))
        	val = ".";

        Font f = g.getFont();
        
        if (onMe && indexOnMe == -1)
        	g.setFont(f.deriveFont(Font.BOLD));
        
        widthAppli = g.getFontMetrics().stringWidth(val);
        int curWidth = Math.max(width, myWidth); //int curWidth = myWidth; curWidth = Math.max(widthAppli, curWidth);
        g.drawString(val, x, y);
        g.setFont(f);
        
        if (validations == null)
        	if (getFather() instanceof AvatarMethodologyDiagramReference)
        		((AvatarMethodologyDiagramReference)(getFather())).makeValidationInfos(this);
        
        if ((validations != null) && (valMinX == null)) {
        	valMinX = new int[validations.length];
        	valMaxX = new int[validations.length];
        } 
        
        int currentMaxX = wf + x - 2 * (X_MARGIN);
        int saveCurrentMaxX = currentMaxX;
        
        if (wf < w + (2 * X_MARGIN)) {
        	makeScale(g, w + (2 * X_MARGIN));
        	return;
        }

        boolean oneWritten = false;
        int w1 = 0;
        g.setFont(f.deriveFont(Font.ITALIC));
        
        if ((validations != null) & (validations.length > 0)) {
			for (int i = validations.length - 1; i >= 0; i--) {
				w1 = g.getFontMetrics().stringWidth(SHORT_ACTION_NAMES[validations[i]]);

				if ((onMe && indexOnMe == i))
					g.setFont(f.deriveFont(Font.ITALIC));
				
				if ((currentMaxX - w1) > (x + w)) 
				{
					if ((onMe && indexOnMe == i))
						g.setFont(f.deriveFont(Font.BOLD));
				
					g.drawString(SHORT_ACTION_NAMES[validations[i]], currentMaxX - w1, y);
					g.setFont(f.deriveFont(Font.ITALIC));
					valMinX[i] = currentMaxX-w1;
					valMaxX[i] = currentMaxX;
					oneWritten = true;
					currentMaxX = currentMaxX - w1 - 5;
				} else
					break;
			}
        }
        g.setFont(f);
        if (oneWritten)
        	makeScale(g, saveCurrentMaxX - x);
        else
        	makeScale(g, w);
        if (onMe)
        	g.drawRect(x-2, y-12, curWidth+5, 15);
        
        return; 
    }
    */
    
    private void makeScale(Graphics g, int _size)
    {
    	//TraceManager.addDev("----- Make SCale ----");
    	if (!tdp.isScaled()) 
    	{
            myWidth = _size;
            myHeight = g.getFontMetrics().getHeight();
        }
    }
    
    @Override
    public TGComponent isOnMe(int _x, int _y) {
    	int oldIndex = indexOnMe;
        if (GraphicLib.isInRectangle(_x, _y, x, y - height, Math.max(myWidth, minWidth), myHeight)) 
        {
        	indexOnMe = -2;
        	if (_x <= (x + widthAppli)) 
        	{
        		indexOnMe = -1;
        		tdp.getMGUI().setStatusBarText("Open the " + value + " model");
        	}
        	if ((validations != null) && (validations.length > 0))
        	{
        		for (int i = 0; i < validations.length; i++) 
        		{
        			if ((_x >= valMinX[i]) && (_x <= valMaxX[i]))
        			{
        				indexOnMe = i;
        				tdp.getMGUI().setStatusBarText(LONG_ACTION_NAMES[validations[i]]);
        				break;
        			}
        		}
        	}
        	
        	if (oldIndex != indexOnMe)
        		tdp.repaint();
            return this;
        }
        return null;
    }
    
    @Override
    public boolean editOndoubleClick(JFrame frame) 
    {
        if (indexOnMe == -1) {
        	// Opening the diagram
        	if (!tdp.getMGUI().selectMainTab(value))
        	{
        		TraceManager.addDev("Diagram removed?");
        		return false;
        	}
        }

        if (indexOnMe > -1) {
        	AvatarMethodologyDiagramReference ref = ((AvatarMethodologyDiagramReference)(getFather()));
        	ref.makeCall(value, indexOnMe);
        } 
        return true;
    }
    
    @Override
    public  int getType() 
    {
        return TGComponentManager.AVATARMETHODOLOGY_DIAGRAM_NAME;
    }
    
    @Override
   	public int getDefaultConnector() 
   	{
      return TGComponentManager.AVATARMETHODOLOGY_CONNECTOR;
    }
    
    public void setValidationsNumber(int size) 
    {
    	validations = new int[size];
    }
    
    public void setValidationsInfo(int _index, int _val) 
    {
    	validations[_index] = _val;
    }
    
    @Override
    public void rescale(double scaleFactor) {
		if ((valMinX != null) && (valMinX.length > 0)) 
		{
			for (int i = 0; i < valMinX.length; i++) 
			{
				valMinX[i] = (int)(valMinX[i] / oldScaleFactor * scaleFactor);
				valMaxX[i] = (int)(valMaxX[i] / oldScaleFactor * scaleFactor);
			}
		}
		super.rescale(scaleFactor);
	}
}
