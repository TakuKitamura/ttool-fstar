/**Copyright or � or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
 * Class  ProSMDSubmachine
 * 
 * Creation: 25/07/2006
 * @version 1.0 25/07/2006
 * @author Emil Salageanu, Ludovic APVRILLE
 * @see
 */

package ui.prosmd;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.procsd.ProActiveCompSpecificationCSDPanel;
import ui.window.*;

public class  ProSMDSubmachine extends TGCOneLineText implements ActionListener {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    /*protected int arc = 5;*/
    protected int linebreak = 10;
    private String code; //each sub-machine has an unique code in a proActive design:
    // code = (PanelName+subMachineIndexInThisPanel+SubMachineValue)

    //  Edited by PV - BEGIN
	private ProactiveSMDPanel mySMD = null;
	// Edited by PV - END
    
	
	public  ProSMDSubmachine(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointProSMD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointProSMD(this, 0, lineLength, false, true, 0.5, 1.0);
        
        moveable = true;
        editable = true;
        removable = true;
        
        name = "submachine";
        value = "";
        
        myImageIcon = IconManager.imgic2008;
    }
    
    public void internalDrawing(Graphics g) {
     
    	  if (this.x<=0)
			  this.x=1;
		  if (this.y<=0)
			  this.y=1;
    	int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX + linebreak);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;
            //updateConnectingPoints();
        }
        
        // Lines to connecting points
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        
        // Drawing in clockwise fashion
       g.setColor(Color.BLUE);
        g.drawRoundRect(x,y,width,height,20,20);
        g.drawRoundRect(x,y,width-1,height-1,20,20);
        g.drawRoundRect(x,y,width-2,height-2,20,20);
        //g.drawString("chl", x+(width-w) / 2, y);
        g.drawString("s:"+value, x + textX , y + textY);
        g.setColor(Color.BLACK);
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    
    public String getAction() {
        return value;
    }
    

    public int getType() {
        return TGComponentManager.PROSMD_SUBMACHINE;
    } 
    
    
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
 	   
    	  
    	JMenuItem rn = new JMenuItem("rename");
        rn.addActionListener(this);
        componentMenu.add(rn);
        
    	
    	componentMenu.addSeparator();
        JMenuItem sm = new JMenuItem("create/edit state machine");
        sm.addActionListener(this);
        componentMenu.add(sm);
        
        
        
    
    
    }
    

    public void actionPerformed(ActionEvent e)
    {
    	if (e.getActionCommand().equals("create/edit state machine"))
    	if  (!tdp.getGUI().selectTab(value))
    	{
    	   tdp.getGUI().createProActiveSMD(tdp.tp,value); 		
    	   tdp.getGUI().selectTab(value);
    	}
       
    	if (e.getActionCommand().equals("rename"))
    	{
    		super.editOndoubleClick(null);
    	}
    	
    }
 
  public void setValue(String v) {
        String oldValue=value;
    	value = v;
        
        
        ProactiveDesignPanel pd = (ProactiveDesignPanel) tdp.getGUI().getCurrentTURTLEPanel();
        ProactiveSMDPanel psmdp=pd.getSMDPanel(oldValue);
  
  if (psmdp!=null)
  {
    
     pd.renamePanel(psmdp,v);
     
  }
     repaint = true;
   
    }

public String getCode() {
	return code;
}

public void setCode(String id) {
	this.code = id;
}
    

	public boolean editOndoubleClick(JFrame frame) {	
		return tdp.getGUI().selectTab(value);
	}

	//Edited by PV - BEGIN
	public void setAs(String policy){

		String s = ((ProactiveDesignPanel)tdp.tp).addSMD(value);
		mySMD = ((ProactiveDesignPanel)tdp.tp).getSMDPanel(s);

		
		ProSMDStartState start_state = (ProSMDStartState)TGComponentManager.addComponent(110, 10, TGComponentManager.PROSMD_START_STATE,tdp);
		mySMD.addBuiltComponent(start_state);

		
		ProSMDState begin_state01 = (ProSMDState)TGComponentManager.addComponent(100, 70, TGComponentManager.PROSMD_STATE,tdp);
		begin_state01.setName("begin");
		begin_state01.setValue("begin");
		begin_state01.resize(70, 20);
		mySMD.addBuiltComponent(begin_state01);

		
		TGConnectingPoint p1 = start_state.getTGConnectingPointAtIndex(0);
		TGConnectingPoint p2 = begin_state01.getTGConnectingPointAtIndex(0);
		TGConnector conn01 = TGComponentManager.addConnector(p1.getX(), p2.getY(), TGComponentManager.CONNECTOR_PROSMD, tdp, p1, p2, new Vector());
		mySMD.addBuiltConnector(conn01);

		
		ProSMDSubmachine policy_submachine = (ProSMDSubmachine)TGComponentManager.addComponent(100, 110, TGComponentManager.PROSMD_SUBMACHINE,tdp);
		// TODO: fifo and other policies
		if(policy == "fifo") {
			policy_submachine.setName("FIFO");
			policy_submachine.setValue("FIFO");
		}
		policy_submachine.resize(70, 20);
		mySMD.addBuiltComponent(policy_submachine);

		
		p1 = begin_state01.getTGConnectingPointAtIndex(1);
		p2 = policy_submachine.getTGConnectingPointAtIndex(0);
		TGConnector conn02 = TGComponentManager.addConnector(p1.getX(), p2.getY(), TGComponentManager.CONNECTOR_PROSMD, tdp, p1, p2, new Vector());
		mySMD.addBuiltConnector(conn02);

		
		ProSMDState begin_state02 = (ProSMDState)TGComponentManager.addComponent(100, 170, TGComponentManager.PROSMD_STATE,tdp);
		begin_state02.setName("begin");
		begin_state02.setValue("begin");
		begin_state02.resize(70, 20);
		mySMD.addBuiltComponent(begin_state02);

		
		p1 = policy_submachine.getTGConnectingPointAtIndex(1);
		p2 = begin_state02.getTGConnectingPointAtIndex(0);
		TGConnector conn03 = TGComponentManager.addConnector(p1.getX(), p2.getY(), TGComponentManager.CONNECTOR_PROSMD, tdp, p1, p2, new Vector());
		mySMD.addBuiltConnector(conn03);


		ProSMDStopState stop_state = (ProSMDStopState)TGComponentManager.addComponent(110, 240, TGComponentManager.PROSMD_STOP_STATE,tdp);
		mySMD.addBuiltComponent(stop_state);

		
		p1 = begin_state02.getTGConnectingPointAtIndex(1);
		p2 = stop_state.getTGConnectingPointAtIndex(0);
		TGConnector conn04 = TGComponentManager.addConnector(p1.getX(), p2.getY(), TGComponentManager.CONNECTOR_PROSMD, tdp, p1, p2, new Vector());
		mySMD.addBuiltConnector(conn04);
	}

//	Edited by PV - END

}