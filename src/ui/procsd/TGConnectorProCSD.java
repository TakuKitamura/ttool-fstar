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
 * Class TGConnectorProSMD
 * Basic connector with a full arrow at the end. Used in ProActive composite structure diagrams.
 * Creation: 05/07/2006
 * @version 1.0 05/07/2006
 * @author Ludovic APVRILLE
 * @see
 */

/**
 * We have limited the possibilities of a Connector 
 * 
 */

package ui.procsd;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.*;

import javax.swing.JPopupMenu;

import myutil.*;
import ui.*;
import ui.cd.TGConnectorAssociation;

//this is a binding 
//from a port to another port
public  class TGConnectorProCSD extends TGConnectorAssociationProCSD {
    protected int arrowLength = 10;
    
    public TGConnectorProCSD(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, new Vector(0));
       // System.out.println("constructor"); 
      //  System.out.println("list points "+_listPoint.toString());
        myImageIcon = IconManager.imgic2110;
        automaticDrawing = false;
        
       // System.out.println(p1.getFather().toString());
        //is it in the wrong direction?
    
        //_tdp.finishAddingConnector(_p1);
    
   // System.out.println("Connector created: from "+p1.getFather().toString()+" to "+p2.getFather().toString());
  
         updateConnectedPorts();
    
    }
  
  
    public void doPostLoading()
    {
    	updateConnectedPorts();
    }
        
    /*
     * if the connector has been drown in the opposite way 
     * (from an in port to an out port for example)
     * we will change it in the right way.
     * Then we set toPort and fromPort attributes of the ports we have just connected  
     * 
     */
    private void updateConnectedPorts()
    {
        if  (p1.getFather() instanceof ProCSDInPort) 
        {
      	if ((p2.getFather() instanceof ProCSDOutPort))
		        {  
		        	TGConnectingPoint tmp=p1;
		        	p1=p2;
		        	p2=tmp;
		          }
      	else 
      	 if ((p2.getFather() instanceof ProCSDInPort))
      	 {
      		 
      		 TGComponent father1 =((ProCSDPort)p1.getFather()).getFather();
      		 TGComponent father2 =((ProCSDPort)p2.getFather()).getFather();
      		 
      		 if (father1.getFather() == father2)
      		 {
      		    TGConnectingPoint tmp=p1;
		        	p1=p2;
		        	p2=tmp;
      		 }
      	 }
       }
      else
      	if  (p1.getFather() instanceof ProCSDOutPort) 
          {
        	 if ((p2.getFather() instanceof ProCSDOutPort))
        	 {
        		 
        		 TGComponent father1 =((ProCSDPort)p1.getFather()).getFather();
        		 TGComponent father2 =((ProCSDPort)p2.getFather()).getFather();
        		 
        		 if (father2.getFather() == father1)
        		 {
        		    TGConnectingPoint tmp=p1;
 		        	p1=p2;
 		        	p2=tmp;
        		 }
        	 }

        }
    
    try{
   	    
    	
    	if  ( (! (p1.getFather() instanceof ProCSDPort)) || ( !(p2.getFather() instanceof ProCSDPort))  ) return;
    	
    	
    	ProCSDPort port1 =(ProCSDPort)p1.getFather();
		ProCSDPort port2 =(ProCSDPort)p2.getFather();
		
		if ((port1==null) || (port2==null)) return;
		
		port1.setToPort(port2);
		port2.setFromPort(port1);
    }
    catch (Exception e)
    {
    	//we have an exception here when loading an xml file
    	System.out.println("Error in connectors's connecting points. ");
    }
    
    
    }
    
    
    public void myActionWhenRemoved()
    {
    	ProCSDPort port1=this.getMyPort1();
    	ProCSDPort port2=this.getMyPort2();
    	
    	if ((port1)!=null) port1.setToPort(null);
    	if (port2!=null) port2.setFromPort(null);
    }
    
    
    /*
     * checks if this connector is ok
     * goes from a out port to an in port
     * or between two ports of the same type at the same level
     */
    public boolean check()
    {
    	return true;
    }
    
  protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
  super.drawLastSegment( g,  x1, y1,  x2,  y2);  
    }

  
  /* Commented by Solange
  private boolean isMyAttribute(TGConnectorAttribute tgc)
	{
		for (int i=0;i<nbConnectingPoint;i++)
		{
			if ( tgc.getTGConnectingPointP1().equals(this.getTGConnectingPointAtIndex(i)) || tgc.getTGConnectingPointP2().equals(this.getTGConnectingPointAtIndex(i)))
		        return true;
		}
		return false;
	}
	
  */
  
 /* Commented by Solange
  private TGConnectorAttribute getMyTGConnectorAttribute()
  {
	//  TGConnectingPoint cp;
	// if (this.nbConnectingPoint>1)
	// {
	//	cp =connectingPoint[1];
	// }
  //	 else {
	//	 System.out.println("Error!!!!!");
  //		 return null;
  //	 }
	  
	 	LinkedList cmps=this.tdp.getComponentList();
	 	for (int i=0;i<cmps.size();i++)
	 		{
	 		TGComponent tgc = (TGComponent)cmps.get(i);
			if (tgc.getType()== TGComponentManager.CONNECTOR_ATTRIBUTE)
				if (isMyAttribute((TGConnectorAttribute)tgc)) return (TGConnectorAttribute)tgc;	 			 		
	 		}
	 
	 return null;
	  
  }
  
  */
/* Commented by Solange
 private boolean isInterfaceConnected(ProCSDInterface pci,TGConnectorAttribute tgca)
  	{
 	 	for (int i=0;i<pci.getNbConnectingPoint();i++)
 	 	{
 	 		if (tgca.getTGConnectingPointP1().equals(pci.getTGConnectingPointAtIndex(i)) || (tgca.getTGConnectingPointP2().equals(pci.getTGConnectingPointAtIndex(i))))
 	 				return true;
 	 		
 	 	}
 	 
 	 return false;
  	}
*/
  
 /* Commented by Solange 
  public ProCSDInterface getMyInterface(LinkedList interfacesList)
	{
	  TGConnectorAttribute tgca=getMyTGConnectorAttribute();
	  
	  LinkedList cmps=interfacesList;
	 	for (int i=0;i<cmps.size();i++)
	 		{
	 		TGComponent tgc = (TGComponent)cmps.get(i);
	 			if (tgc.getType()== TGComponentManager.PROCSD_INTERFACE)
	 				{
	 					if (isInterfaceConnected((ProCSDInterface)tgc,tgca)) return  (ProCSDInterface)tgc;	
	 				}
	 		}
	  
	  return null;	
		
	}
  */
  
  /*
   * gets the port I am conected to through my P1
   * 
   *
   */
  

  /*
   * gets the port I am conected to through my P2
   */
  
  
  public ProCSDPort getMyPort1()
  {
	  ProCSDPort port1= (ProCSDPort) ((TGConnectingPointProCSD)p1).getMyOwnerComponent();
      return port1;
  }
  
  public ProCSDPort getMyPort2()
  {
	  ProCSDPort port2= (ProCSDPort) ((TGConnectingPointProCSD)p2).getMyOwnerComponent();
      return port2;
  }
  
  
  /*private boolean addTGCPointOfConnector(int x, int y) {
	  return false;
  }
    */
  
  public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
	 
  }
  
 

  
  
  public void internalDrawing(Graphics g)
    {
    	//see the figure1 in my notebook :)
    	
    	//int x1=Math.min(p1.getX(),p2.getX());
    	//int x2=Math.max(p1.getX(),p2.getX());
    	//int y1=Math.min(p1.getY(),p2.getY());
    	//int y2=Math.max(p1.getY(),p2.getY());
	  
	  
	 
	 /* 
	  if ((p1.getFather()==null) || (p2.getFather()==null))
			 {this.tdp.removeComponent(this);
			 	return;
			 }
	*/	 
    
	    int x1=p1.getX();
	    int x2=p2.getX();
	    int y1=p1.getY();
	    int y2=p2.getY();
	    
	    int R=15; //rayon mini cercle
	    
    	int xm=Math.min(x1,x2)+Math.abs(x2-x1)/2;
    	int ym=Math.min(y1,y2)+Math.abs(y2-y1)/2;
    	
    	double  MA_double=new Double(Math.sqrt(Math.pow(xm-x1,2)+Math.pow(ym-y1,2))).doubleValue();
    	int MA=new Double(MA_double).intValue();
    	
    	double AC=Math.abs(y2-y1);
    	double sinABC=AC/(2*MA_double);
    
    	double ABC=Math.asin(sinABC);
    	
    	double angle=0;
    	 int myAngle=0;
    	 
    	if ( (x1<=x2) && (y1<=y2) )
    	{ angle=Math.PI/2-ABC;
              	} 
    	
    	if ( (x1<=x2) && (y1>=y2) )
    	{ angle=-(3*Math.PI/2-ABC);
              	} 
    	
    	
    	if ( (x1>=x2) && (y1<=y2) )
    	{
    		angle=- (Math.PI/2-ABC);
    	}
    
   	if ( (x1>=x2) && (y1>=y2) )
    	{
    		angle= (3*Math.PI/2-ABC);
    	}
    
    	
    	myAngle=new Double(angle/Math.PI*180).intValue();
    	
    	
    	int xPrim,yPrim,xSec,ySec;
    	if (Math.abs(xm-x1)>10)
    	{
    	  xPrim=xm-R/2*(xm-x1)/MA;
    	  yPrim=ym-( (xm-xPrim)*(ym-y1)/(xm-x1));
    	
          xSec=xm+R/2*(xm-x1)/MA;
          ySec=ym+( (xm-xPrim)*(ym-y1)/(xm-x1));
    	}
    	
    	else 
    	{
    		xPrim=xm;
    		xSec=xm;
    		
    			if (y1<y2)
    				{
    					yPrim=ym-R/2;
    					ySec=ym+R/2;
    				}
    				else
    					{
    						yPrim=ym+R/2;
    						ySec=ym-R/2;
    		
    					}
    	}
    	 	//System.out.println("x1="+x1+" y1="+y1+" x2="+x2+" y2="+y2);
    	
    	//super.internalDrawing(g);
    	// drawLastSegment(g, p1.getX(), p1.getY(), p2.getX(), p2.getY());
        g.setColor(Color.BLUE);
       // g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        g.drawLine(p1.getX(), p1.getY(), xPrim, yPrim);
        
        //g.drawOval(xPrim-R/2,yPrim-R/2,R,R);
        g.setColor(Color.WHITE);
        g.fillArc(xPrim-R/2,yPrim-R/2,R,R,myAngle,180);
        g.setColor(Color.BLUE);
        g.drawArc(xPrim-R/2,yPrim-R/2,R,R,myAngle,180);
        
 
       // g.drawOval(xSec-5,ySec-5,10,10);
        g.fillOval(xSec-R/2,ySec-R/2,R,R);
        g.drawLine(xSec, ySec, p2.getX(), p2.getY());

        //GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        g.setColor(Color.BLACK);
    
     
	  
 //	  GraphicLib.arrowWithLine(g,1,0,10,x1,y1,x2,y2,true);
     
     }
    
  
    public int getType() {
        return TGComponentManager.CONNECTOR_PROCSD;
    }
}






