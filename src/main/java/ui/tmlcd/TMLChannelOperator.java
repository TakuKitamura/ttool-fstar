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




package ui.tmlcd;

import ui.*;
import ui.util.IconManager;

import java.awt.*;

/**
 * Class TCDParallelOperator
 * Parallel composition operator
 * To be used in class diagrams
 * Creation: 28/10/2005
 * @version 1.0 28/10/2005
 * @author Ludovic APVRILLE
 */
public class TMLChannelOperator extends TMLCompositionOperator {
    
    
    public TMLChannelOperator(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        nbInternalTGComponent = 1;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        TMLChannelProperties tgc = new TMLChannelProperties(x, y+40, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, this, _tdp);
        tgc.setName("Channel properties");
        tgc.setMoveWithFather(false);
        tgcomponent[0] = tgc;
        
        name = "Channel TML composition operator";
        value = "Channel";
        
        myImageIcon = IconManager.imgic802;
    }
    
    public void internalDrawing(Graphics g) {
        if (((TMLTaskDiagramPanel)(tdp)).areChannelsVisible()) {
            g.drawRect(x, y, width, height);
            g.setColor(getMyColor());
            g.fillRect(x+1, y+1, width-1, height-1);
            //g.drawImage(IconManager.img8, x + width - 20, y + 3, ColorManager.COMPOSITION_OPERATOR, null);
            ColorManager.setColor(g, getState(), 0);
            g.setFont((g.getFont()).deriveFont(Font.BOLD));
            g.drawString(value, x + textX, y + textY);
            g.setFont((g.getFont()).deriveFont(Font.PLAIN));
        }
    }
    
    public boolean isToggledVisible() {
        return (((TMLTaskDiagramPanel)(tdp)).areChannelsVisible());
    }
    
    public int getType() {
        return TGComponentManager.TMLTD_CHANNEL_OPERATOR;
    }
    
    public TMLChannelProperties getTMLChannelProperties() {
        return (TMLChannelProperties)tgcomponent[0];
    }
    
    public String getChannelName() {
        return getTMLChannelProperties().getChannelName();
    }
    
    public int getChannelSize() {
        return getTMLChannelProperties().getChannelSize();
    }
    
    public int getChannelMax() {
        return getTMLChannelProperties().getChannelMax();
    }
    
    public int getChannelType() {
        return getTMLChannelProperties().getChannelType();
    }
	
	public Color getMyColor() {
		return ColorManager.TML_PORT_CHANNEL;
	}
}
