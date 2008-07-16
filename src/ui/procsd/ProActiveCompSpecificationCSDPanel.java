package ui.procsd;

import java.util.Vector;

import ui.MainGUI;
import ui.TDiagramMouseManager;
import ui.TGConnectingPoint;
import ui.TToolBar;

public class ProActiveCompSpecificationCSDPanel extends ProactiveCSDPanel{

	/*
	 * the component in the main diagram that is designed in this panel
	 */

	
	public  ProActiveCompSpecificationCSDPanel(MainGUI mgui, TToolBar _ttb, String name) {
	        super(mgui, _ttb);
	       this.setName(name);
	       
	    }
	    
	 /*
	  * returns the component disigned within this diagram
	  */
	 public ProCSDComponent getProCSDComponent()
	 {
		
		 for (int k=0;k<componentList.size();k++)
		 {
			 if (componentList.get(k) instanceof ProCSDComponent)
				 return (ProCSDComponent)componentList.get(k);
		 }
		return null; 
	 }
	 
	  
     /*
      * Creates the blackBox corresponding to the component given as parameter
      * the black box contains: 
      * 	- copies of the ports of comp with their interfaces
      * 	- copies of the attributes of comp
      */
     
     public ProCSDComponent createBlackBoxComp(ProCSDComponent comp)
 	{
 		ProCSDComponent designComp=new ProCSDComponent(comp.getX(),comp.getY(),comp.getMinHeight(),comp.getMaxHeight(),comp.getMinWidth(),comp.getMaxHeight(),false,null,this);
 		
 		designComp.setWidth(comp.getWidth());
 		designComp.setHeight(comp.getHeight());
 		
 		designComp.setValue(comp.getValue()+"Design");
 		
 		
 		Vector<ProCSDPort> ports=comp.getPortsList();
 		for (int k=0;k<ports.size();k++)
 		{
 			ProCSDPort p=ports.get(k);
 			ProCSDPort newPort=null;
 			if (p instanceof ProCSDInPort)
 					newPort=new ProCSDInPort(p.getX(),p.getY(),p.getMinHeight(),p.getMaxHeight(),p.getMinWidth(),p.getMaxWidth(),false,designComp,this);
 			else
 				if (p instanceof ProCSDOutPort)
 					newPort=new ProCSDOutPort(p.getX(),p.getY(),p.getMinHeight(),p.getMaxHeight(),p.getMinWidth(),p.getMaxWidth(),false,designComp,this);
 					
 			if (newPort!=null)
 			{
 				newPort.setValue(p.getValue());
 				//newPort is a part of the designComponent
 				//p is the part of an instance comp
 				//p will have the portCode of newPort
 				p.setPortCode(newPort.getPortCode());
 				
 				designComp.addSwallowedTGComponent(newPort,newPort.getX(),newPort.getY());
 				
 				ProCSDInterface pI=p.getMyInterface();
 				if (pI!=null)
 				{
 					//ProCSDInterface newInterface = new ProCSDInterface(pI.getX(),pI.getY(),pI.getMinHeight(),pI.getMaxHeight(),pI.getMinWidth(),pI.getMaxWidth(),false,null,this);
 					//newInterface.setValue(pI.getValue());
 					//newInterface.setManda(pI.isMandatory());
 					//newInterface.setMessages(pI.getMyMessages());
 					
 				//	ProCSDInterface newInterface=new ProCSDInterface(pI,this);
 				//	this.addBuiltComponent(newInterface);
 				//	newPort.connectInterface(newInterface);
 					
 					
 					
 					//TGConnectingPoint point1=newPort.getTGConnectingPointAtIndex(0);
 					//TGConnectingPoint point2=newInterface.getTGConnectingPointAtIndex(0);
 					//TGConnectorPortInterface connector=new TGConnectorPortInterface(0,0,0,0,0,0,false,null,this,point1,point2,new Vector());
 					
 					
 					//this.addBuiltComponent(connector);										
 				}//if interface !=null								
 			}//if newPort!=null	
 		}
 	
 		
 		designComp.setAttributes(comp.getMyAttributes());
 		
 		this.addBuiltComponent(designComp);
 		
 		return designComp;
 	}
     
}
