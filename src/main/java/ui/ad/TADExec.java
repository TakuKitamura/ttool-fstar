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

package ui.ad;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;

import myutil.GraphicLib;
import ui.AllowedBreakpoint;
import ui.BasicErrorHighlight;
import ui.CDElement;
import ui.ColorManager;
import ui.EmbeddedComment;
import ui.ErrorHighlight;
import ui.TDiagramPanel;
import ui.TGCOneLineText;
import ui.TGComponent;
import ui.TGConnectingPoint;
import ui.TGScalableComponent;
import ui.util.IconManager;

/**
 * Class TMLADExecC
 * Fixed custom duration operator. To be used in TML activity diagrams
 * Creation: 21/05/2008
 * @version 1.0 21/05/2008
 * @author Ludovic APVRILLE
 */
public abstract class TADExec extends TADComponentWithSubcomponents implements EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {

   // private int ilength;// = 10;
  //  private int lineLength1;// = 2;
	
	protected int stateOfError = 0; // Not yet checked
    
    public TADExec(	int _x,
    				int _y,
    				int _minX,
    				int _maxX,
    				int _minY,
    				int _maxY,
    				boolean _pos,
    				TGComponent _father,
    				TDiagramPanel _tdp,
    				final String value,
    				final String name )  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
       
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = createConnectingPoint(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = createConnectingPoint(this, 0, + lineLength, false, true, 0.5, 1.0);

        initSize( 10, 30 );
//        ilength = 10;
//        lineLength1 = 2;
        textX = width + scale( 5 );
        textY = height/2 + scale( 5 );
        
        nbInternalTGComponent = 1;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        TGScalableComponent tgc = createInternalComponent();
        tgc.setValue( value );
        tgc.setName( name );
        tgcomponent[0] = tgc;
        
        moveable = true;
        editable = false;
        removable = true;
        
        myImageIcon = IconManager.imgic214;
    }

    protected TGScalableComponent createInternalComponent() {
    	return new TGCOneLineText( x+textX, y+textY, -75, 30, textY - 10, textY + 10, true, this, tdp );
    }
    
    protected abstract TGConnectingPointAD createConnectingPoint(	final CDElement _container,
    																final int _x,
    																final int _y,
    																final boolean _in,
    																final boolean _out,
    																final double _w, 
    																final double _h );
    
    @Override
    protected void internalDrawing(Graphics g) {
		if (stateOfError > 0)  {
			Color c = g.getColor();
			switch(stateOfError) {
				case ErrorHighlight.OK:
					g.setColor(ColorManager.EXEC);
					break;
				default:
					g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}

			g.fillRect(x, y, width, height);
			g.setColor(c);
		}
		
		g.drawRect(x, y, width, height);
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        
        drawInternalSymbol( g, scale( 2 ), scale( 10 ) );
    }
    
    protected abstract void drawInternalSymbol( Graphics g,
    											int symbolWidth,
    											int symbolHeight );
    
    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        
        if ((int)(Line2D.ptSegDistSq(x +width/2, y- lineLength,  x+width/2, y + lineLength + height, x1, y1)) < distanceSelected) {
			return this;	
		}
        
        return null;
    }
    
    public String getDelayValue() {
        return tgcomponent[0].getValue();
    }
    
    public void setDelayValue(String value) {
        tgcomponent[0].setValue(value);
    }
	
    @Override
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
}
