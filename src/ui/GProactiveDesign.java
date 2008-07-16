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
 * Class GProactiveDesign
 * Translation of graphical proactive designs into TURTLE modeling
 * Creation: 10/07/2006
 * @version 1.0 10/07/2006
 * @author Ludovic APVRILLE
 * @see
 */


package ui;

import java.util.*;

import fr.inria.oasis.vercors.cttool.model.Attribute;
import fr.inria.oasis.vercors.cttool.model.AttributeImpl;
import fr.inria.oasis.vercors.cttool.model.Component;
import fr.inria.oasis.vercors.cttool.model.ComponentImpl;
import fr.inria.oasis.vercors.cttool.model.InPort;
import fr.inria.oasis.vercors.cttool.model.Interface;
import fr.inria.oasis.vercors.cttool.model.InterfaceImpl;
import fr.inria.oasis.vercors.cttool.model.Message;
import fr.inria.oasis.vercors.cttool.model.MessageImpl;
import fr.inria.oasis.vercors.cttool.model.OutPort;
import fr.inria.oasis.vercors.cttool.model.Port;

import translator.*;

import ui.procsd.*;
import ui.prosmd.*;
import ui.prosmd.util.CorrespondanceSMDManager;

public class GProactiveDesign  {
    private TURTLEModeling tm;
    private ProactiveDesignPanel pdp;
    private LinkedList portsList=new LinkedList(); //list of ProCSDPort 
    private LinkedList connectorsList =new LinkedList(); //list of TGConnectorProCSD
    private LinkedList connectorsPortInterfacesList=new LinkedList();
    private LinkedList interfacesList =new LinkedList(); //list of ProCSDInterface
    private LinkedList ProCSDComponentsList=new LinkedList();
    private Vector checkingErrors=new Vector(); 
  //  private CorrespondanceTGElement listE;
    private CorrespondanceSMDManager corespManager;
    private boolean buildErrors;
    //Added by Solange ...and removed by Emil ..
  //  private ProCSDPort puerto, puerto2;
    private Component mainModelComp;
    
    public GProactiveDesign(ProactiveDesignPanel _pdp) {
        pdp = _pdp;
        init();
     
        //System.out.println(mainModelComp.prettyPrint());
   }
    

    
    private void init()
    {   checkingErrors = new Vector();
      //  listE = new CorrespondanceTGElement();
    	portsList=new LinkedList(); //list of ProCSDPort 
        connectorsList =new LinkedList(); //list of TGConnectorProCSD
        interfacesList =new LinkedList(); //list of ProCSDInterface
        ProCSDComponentsList=new LinkedList();
    	ProactiveCSDPanel csd = (ProactiveCSDPanel)(pdp.panels.elementAt(0));
     	LinkedList list = csd.getComponentList();
     	boolean cyclefound=false;
     	  for (int i=0;i<list.size();i++)
        {
        	TGComponent tmp=(TGComponent)list.get(i);
//        	listElement(tmp);
          if  (!parseStructureAndFillUpLists(tmp)) cyclefound=true;
        }
        
    	  
     	  
     	  
        if (cyclefound)
        	addCheckingError(new CheckingError(CheckingError.STRUCTURE_ERROR,"Cycle found in component diagrams structure."));
     	
        updatePortsInformation();
    
     	  
   //     updateAllMembranes(); 
     	  
     	  
    }
    
    
    public void updateAllMembranes()
    {
    	this.pdp.updateAllMembranes();    	
    }
    

    
    private Component createModelComp(ProCSDComponent comp)
    {
    	String modelCompName=comp.getValue();
    	
   	if (comp.getThisCompDesign()!=null)
    	 {comp=comp.getThisCompDesign();
   // 	  modelCompName+="_"+comp.getValue();
    	 }
   
    	
    	Component modelComp=new ComponentImpl(modelCompName,isPrimitive(comp));
    	    	
    	
    	
     //we add attributes
      LinkedList attribs=comp.getMyAttributes();
	  for (int at=0;at<attribs.size();at++)
		{
			TAttribute a=(TAttribute)attribs.get(at);
			Attribute attrib=new fr.inria.oasis.vercors.cttool.model.AttributeImpl(a.getId(),a.getType(),a.getAccess(),a.getInitialValue());		
			modelComp.addAttribute(attrib);			
		}
			  
	  
	  //we add ports
	  //we do not set the ports bindings at this time as all ports in the model
	  //are not already
	  //created
	  
	  Vector portsList=comp.getPortsList();
	  for (int k=0;k<portsList.size();k++)
	  {
		  ProCSDPort p =(ProCSDPort)portsList.get(k);
		  Port modelPort=null;
		  if (p instanceof ProCSDInPort)
		  {
			  modelPort=new InPort(p.getValue(),modelComp);
	  	  }
		  if (p instanceof ProCSDOutPort)
		  {
			  modelPort=new OutPort(p.getValue(),modelComp);
		  }		  
		  if (p.getMyInterface()!=null)
		  {
			  ProCSDInterface proI=p.getMyInterface();
			  Interface modelI=new InterfaceImpl(proI.getValue(),proI.isMandatory());
			  LinkedList proMsgs=proI.getMyMessages();
			  
			  for(int i=0; i<proMsgs.size(); i++) 
			  {
	               TAttribute a = (TAttribute)(proMsgs.get(i));
	               Message m=new MessageImpl(a.getId());
	               modelI.addMessage(m);	            		   
			  }
			  modelPort.setInterface(modelI);
		  }
		  modelComp.addPort(modelPort);
	  }
  
	  //we add the behaviour Panel
	  modelComp.setBehaviour(comp.getMySMD());
	  
	  
    	Vector subComps=comp.getComponentList();
		for (int k=0;k<subComps.size();k++)
		{
			ProCSDComponent subComp=(ProCSDComponent)subComps.get(k);
			Component sc=createModelComp(subComp);
			
			
			modelComp.addSubComponent(sc);
			sc.setFather(modelComp);				
		}
		return modelComp;
    }

    
    
    
    
    private Component initModel(ProactiveCSDPanel mainCsd) throws Exception
    {
    	Component mainAppComp=new ComponentImpl("Application");
    	LinkedList comps = mainCsd.getComponentList();
    	for (int k=0;k<comps.size();k++)
    	{
    		TGComponent t=(TGComponent)comps.get(k);
    		if (t instanceof ProCSDComponent)
    		{
    			ProCSDComponent comp=(ProCSDComponent)t;
    			Component modelComp=createModelComp(comp);
    		    mainAppComp.addSubComponent(modelComp);
    		    modelComp.setFather(mainAppComp);
    		}
    	}
    	
    	mainAppComp=updatePortsBindingInModel(mainAppComp,mainCsd);
    	return mainAppComp;
    }
    
  /*
   * if the father of this port is designed in other diagram, 
   * it returns the port in that diagram corresponding to this port
   * if not it return this port
   */
    private ProCSDPort getRealPort(ProCSDPort p)
    {
    	if (p==null) return null;
    	ProCSDComponent father=(ProCSDComponent)p.getFather();
    	ProCSDComponent fatherDesign=father.getThisCompDesign();
    	if (fatherDesign!=null)
    	{
    		return fatherDesign.getPortByName(p.getValue());
    	}
    	else
    		return p;
     }
    
    
    private void updatePortsBindingInModel(Component modelComp, ProCSDComponent proComp) throws Exception
    {
    	//we consider pairs of  (model Out Port, ProCSDOutPort)
    	Vector ports=proComp.getPortsList();
    	for (int k=0;k<ports.size();k++)
    	{
    		ProCSDPort p=(ProCSDPort)ports.get(k);
    		if (p instanceof ProCSDOutPort)
    		{
    			ProCSDOutPort proPort=(ProCSDOutPort)p;
    			Port modelPort=modelComp.getPortByName(proPort.getValue());
    			
    			ProCSDPort proToPort=getRealPort(proPort.getToPort());
    			if (proToPort!=null)
    			{
    				Port modelToPort=null;
    				ProCSDComponent proToPortFather =(ProCSDComponent)proToPort.getFather();
    				Component modelToPortFather=null;
    				if (proToPortFather==proComp.getFather())
    				{
    					 modelToPortFather=modelComp.getFather();
    				}
    				else
    				{
    					String modelToPortFatherName=proPort.getToPort().getFather().getValue();
    				
    					
    					
    					//	if (proToPortFather.getThisCompDesign()!=null)
    				//		modelToPortFatherName+="_"+proToPortFather.getThisCompDesign().getValue();
    					
    					modelToPortFather=modelComp.getFather().getSubComponentByName(modelToPortFatherName); 
    				}
    				modelToPort=modelToPortFather.getPortByName(proToPort.getValue());
				    modelPort.setToPort(modelToPort);
				    modelToPort.setFromPort(modelPort);
    				
    			}//if proToPort!=null

    			ProCSDPort proFromPort=getRealPort(proPort.getFromPort());
    			if (proFromPort!=null)
    			{
    				Port modelFromPort=null;
    				ProCSDComponent proFromPortFather =(ProCSDComponent)proFromPort.getFather();
    				
    				String modelFromPortFatherName=proPort.getFromPort().getFather().getValue();
					if (proFromPort!=proPort.getFromPort())
					{
						modelFromPortFatherName=proPort.getFromPort().getFather().getValue();
					}
    				
    				//if (proFromPortFather.getThisCompDesign()!=null)
					//	modelFromPortFatherName+="_"+proFromPortFather.getThisCompDesign().getValue();
    				
    				Component modelFromPortFather=modelComp.getSubComponentByName(modelFromPortFatherName);
    				modelFromPort=modelFromPortFather.getPortByName(proFromPort.getValue());
    				modelPort.setFromPort(modelFromPort);
    				modelFromPort.setToPort(modelPort);  
    				
    			}//if proFromPort!=null
    		}//if p is ProCSDOutPort
    		else
    			if (p instanceof ProCSDInPort)  
    		{
    				ProCSDInPort proPort=(ProCSDInPort)p;
        			Port modelPort=modelComp.getPortByName(proPort.getValue());
        			
        			ProCSDPort proFromPort=getRealPort(proPort.getFromPort());
        			if (proFromPort!=null)
        			{
        				if (proFromPort instanceof ProCSDInPort)
        				{
        					Port modelFromPort=null;
        					ProCSDComponent proFromPortFather=(ProCSDComponent)proFromPort.getFather();
        					Component modelFromPortFather=null;
        					if (proFromPortFather==proComp.getFather())
            				{
            					 modelFromPortFather=modelComp.getFather();
            				}
            				else
            				{
            	
            					String modelFromPortFatherName=proPort.getFromPort().getFather().getValue();
            					if (proFromPortFather.getThisCompDesign()!=null)
            						modelFromPortFatherName=proFromPortFather.getThisCompDesign().getValue();
                				modelFromPortFather=modelComp.getFather().getSubComponentByName(modelFromPortFatherName); 
            				}
        					
        				modelFromPort=modelFromPortFather.getPortByName(proFromPort.getValue());
        				modelPort.setFromPort(modelFromPort);
        				modelFromPort.setToPort(modelPort);        				
        			}//if fromport=ProCSDInPort
       			}//if proFromPort!=null
    		}//if p instanceof ProCSDInport
    	}//for all ports
    	
    	if (proComp.getThisCompDesign()!=null)
    		proComp=proComp.getThisCompDesign();
    	Vector v=proComp.getComponentList();
    	for (int k=0;k<v.size();k++)
    	{
    		ProCSDComponent proSubComp=(ProCSDComponent)v.get(k);
    		String proSubCompName=proSubComp.getValue();
    		//if (proSubComp.getThisCompDesign()!=null)
    			//proSubCompName+="_"+proSubComp.getThisCompDesign().getValue();
    		
    		Component modelSubComp=modelComp.getSubComponentByName(proSubCompName);
            updatePortsBindingInModel(modelSubComp,proSubComp);
    	
    	}
    	
    	
    	
    }//method update ports bindings in model
    
    
   private Component updatePortsBindingInModel(Component mainComp, ProactiveCSDPanel mainCsd ) throws Exception
   {
	   //we consider pairs of components (Component c, ProCSDComponent pc) and update potrs bindings in 
	   
	  LinkedList comps = mainCsd.getComponentList();
   		for (int k=0;k<comps.size();k++)
   		{
   			TGComponent t=(TGComponent)comps.get(k);
   			if (t instanceof ProCSDComponent)
   				{
   					ProCSDComponent proComp=(ProCSDComponent)t;
   					String proCompName=proComp.getValue();
   					//if (proComp.getThisCompDesign()!=null) 
   					// 	proCompName+="_"+proComp.getThisCompDesign().getValue();
   					Component modelComp=mainComp.getSubComponentByName(proCompName);
   					if (modelComp==null) {System.out.println("This is a fatal problem.");
   										  addCheckingError(new CheckingError(CheckingError.STRUCTURE_ERROR,"Fatal error in the model translator. Please excuse us for this problem. "));	
   										}
   					
   					updatePortsBindingInModel(modelComp,proComp);
   				}
   		}
   		
   		
	   
	   return mainComp;
   }
    
    
    
    
    
    
    
    public TURTLEModeling generateTURTLEModeling() {
        tm = new TURTLEModeling();
        init();
   	 if (checkingErrors.size()==0)
       try{
        mainModelComp=initModel((ProactiveCSDPanel)(pdp.panels.elementAt(0)));
   	 }
   	  catch (Exception e)
   	  {
   		  System.out.println("This is probably just litle nice bug: Could not initializate model. Exception is: \n");
   		  e.printStackTrace();
   	  }
      if (checkingErrors.size()!=0) return null; 
        	addTClasses(tm,mainModelComp);
      if (checkingErrors.size()==0)	addSynchronisations(tm,mainModelComp);
        return tm;
    }
    
    
    
    public boolean checkSyntax() {

    	if (checkingErrors.size()>0) {
    		return false;
    	}
        else return true ;
    }
    
   
    
    
    /*
     * for each port :
     *  update the interface wich contains methods who pass through this port
     *  "push" the connection into the inside ports in order to have 
     *  the connections between the primitives 
     *  
     */
    
    private void updatePortsInformation()
    {
    	
    	for (int k=0;k<connectorsList.size();k++)
    	{
    		TGConnectorProCSD c=(TGConnectorProCSD)connectorsList.get(k);
    		c.doPostLoading();
    	}
    	
    	for (int k=0;k<connectorsPortInterfacesList.size();k++)
    	{
    		TGConnectorPortInterface c=(TGConnectorPortInterface)connectorsPortInterfacesList.get(k);
    		c.doPostLoading();
    	}
    	
    	//ok 
    	//we need to update the toPort and fromPort for the ports 
    	//of the components who are designed in a diffrent diagram:
    	
/*    	for (int k=0;k<portsList.size();k++)
    	{
    		ProCSDPort p=(ProCSDPort)portsList.get(k);
    		ProCSDComponent portFather=(ProCSDComponent)p.getFather();
    		ProCSDPort pOkBindings=portFather.getThisCompDesign().getPort(p.getValue());
    	    if(pOkBindings!=null)
    	    {
    	    	p.setToPort(pOkBindings.getToPort());
    	    	p.setFromPort(pOkBindings.getFromPort());
    	    }
    	}
  */  	
    	
    	
    	
    	//all this code is just used for compatibility with old CTTool versions.
    	//normaly we shouldn't do this
    	//at least I think...
    	//needs to be verified
    	//and verry well improved before
    	//all this very-badly-designed-software will be well packed
    	//and nicelly put into a trash bin 
    	
    	
    	//Added by Solange
    	int isCompatible;  //0: OK
    					   //1: Port error
    					   //2: compatibility error
    	ProCSDInterface myInterface=null;
    	TGConnectorAttribute myInterfaceConnector;
    	
 	
    	//first we consider all out ports	
    	 for (int i=0;i<portsList.size();i++)
    	 {
    		 ProCSDPort op=(ProCSDPort)portsList.get(i);
    		 if (op.getType()==TGComponentManager.PROCSD_OUT_PORT)
    		 {  
    			
    			 try {
    				 myInterface=op.getMyInterface(interfacesList);
    				 //Added by Solange
    				 op.setMyInterface(myInterface);
    				 // Added by Solange
    				 myInterfaceConnector=op.getTGConnectorInterface();
    				 // Added by Solange
    				 myInterface.setMyConnector(myInterfaceConnector);
        			 }
    			 catch (Exception e)
    			 {
    						 
    				 if (op.getFather()==null)
    				 {
    					 this.addCheckingError(new CheckingError(CheckingError.STRUCTURE_ERROR,"Port "+op.getValue()+" doesn't belong to a component"));
    					 return;
    				 }
    				 if (((ProCSDComponent)op.getFather()).getThisCompDesign()==null) 
    				 {this.addCheckingError(new CheckingError(CheckingError.STRUCTURE_ERROR,"No interface found corresponding to port "+op.getValue()));
    				 return;
    				 }
    			 
    			 }
    			 
    			 ProCSDPort toP=op.getToPort();
    			// if (toP==null) toP=getConnectedProCSDPort(op);
    			 //if(toP==null) toP=getDelegateConnectorProCSDPort(op);
    			 //Commented because is not always an error to be free, by Solange
    			 /* if (toP==null)
    			 {
    				addCheckingError(CheckingError.STRUCTURE_ERROR,"Error: out connection not found for "+op.toString()+" in component "+op.getFather().getValue());
    				//System.out.println("Error: no connection found for "+op.toString());
    				  return; 
    			 }*/
    			  
    			 if (toP!=null)
    			 {
    			  op.setToPort(toP);	 
    			  toP.setFromPort(op);
    			 }
    			 
    			 //Added by Solange
    			 if (myInterface!=null)
    			 { 
    			 isCompatible=op.Compatibility(op,myInterface,interfacesList);
				 //Added by Solange
				 switch(isCompatible)
				 {
					 case 0:// System.out.println("Compatibility test Out port... OK");
					 		 break;
					 case 1: this.addCheckingError(new CheckingError(CheckingError.STRUCTURE_ERROR,"Port "+ op.toString() + " not connected"));
					 		 break;
					 case 2: this.addCheckingError(new CheckingError(CheckingError.STRUCTURE_ERROR,"No Interface Compatibility"));
					 		 break;
				 }
    			 } 
                /* Delegate ports removed, by Solange
    			 ProCSDPort dp = getConnectedDelegatePort(op);
    			 if (dp!=null)
    			 {   dp.setMyInterface(myInterface);
    				 dp.setToPort(toP);
    				 toP.setFromPort(dp);
    				 
    				 ProCSDPort ddp=getFromInsideConnectedPort(dp);
    				 while (ddp!=null)
    				 {   ddp.setMyInterface(myInterface);
    					 ddp.setToPort(toP);
    					 toP.setFromPort(ddp);
    					 ddp=getFromInsideConnectedPort(ddp);
    				 }//while ddp!=null
    			 }//if dp!=null
    			*/
    		 }//if this port is an out port
    	 }//for all ports (out)
    
         //now we consider all in ports
    	 for (int i=0;i<portsList.size();i++)
    	 {
    		 ProCSDPort ip=(ProCSDPort)portsList.get(i);
    		 if (ip.getType()==TGComponentManager.PROCSD_IN_PORT)
    		 {    myInterface=ip.getMyInterface(interfacesList);	
    		  if (myInterface!=null)
    		  {
    		  //Added by Solange
    		      ip.setMyInterface(myInterface);
    		      // Added by Solange
 				 myInterfaceConnector=ip.getTGConnectorInterface();
 				 // Added by Solange
 				 myInterface.setMyConnector(myInterfaceConnector);
    		      //
    		  }
 				 ProCSDPort fromPort=ip.getFromPort();
                  //Added by Solange 
			  //    if(fromPort==null) fromPort=getDelegateConnectorProCSDPort(ip);
			      
			      if (fromPort!=null)
	    			 {
	    			  ip.setFromPort(fromPort);	 
	    			  fromPort.setToPort(ip);
	    			 }
		    		 
                //Commented because is not always an error to be free, by Solange
                /*if (fromPort==null)
                {
                	addCheckingError(CheckingError.STRUCTURE_ERROR,"Error: in connection not found for "+ip.toString()+" in component "+ip.getFather().getValue());
                	return;
                }*/
                //Added by Solange
                isCompatible=ip.Compatibility(ip, myInterface, interfacesList);
				//Added by Solange
                switch(isCompatible)
				 {
					 case 0: //System.out.println("Compatibility test In port... OK");
					 		 break;
					 case 1: this.addCheckingError(new CheckingError(CheckingError.STRUCTURE_ERROR,"Port "+ ip.toString() + " not connected"));
					 		 break;
					 case 2: this.addCheckingError(new CheckingError(CheckingError.STRUCTURE_ERROR,"No Interface Compatibility"));
					 		 break;
				 }
                                
                /* Delegate ports removed, by Solange
    			ProCSDPort dp=getConnectedDelegatePort(ip);
    			if (dp!=null)
    			{   dp.setMyInterface(myInterface);
    				dp.setFromPort(fromPort);
    				fromPort.setToPort(dp);
                    ProCSDPort ddp=getToInsideConnectedPort(dp);
                    while (ddp!=null)
                    {   ddp.setMyInterface(myInterface);
                    	ddp.setFromPort(fromPort);
                    	fromPort.setToPort(ddp);
                    	ddp=getToInsideConnectedPort(ddp);
                    }//while ddp!=null
    			
    			}//dp!=null
    			*/
    		 }// ip is an input port
    	 } // for all ports in		 		 
    }
    
    private boolean isPrimitive(ProCSDComponent comp)
    {
    	LinkedList l=getSubComponents(comp,TGComponentManager.PROCSD_COMPONENT);
    	if (l==null) return true;
    	if (l.size()==0) return true;
    	else return false;	
    }
    
    
    /*
     * get subcomponents of type given as parameter
     * @param type The type id from TGComponentManager
     * @param tgc A component
     * @return selectedSubComps A LinkedList of subcomponents of the type
     */
    private LinkedList getSubComponents(TGComponent tgc, int type)
    {
    	if (!(tgc.getType()==TGComponentManager.PROCSD_COMPONENT)) return null;
    	LinkedList subcompList=getSubComponents(tgc);
    	LinkedList selectedSubComps=new LinkedList();
        for (int i=0;i<subcompList.size();i++)
        {
        	TGComponent tmp=(TGComponent)subcompList.get(i);
        	if (tmp.getType()==type)
        		selectedSubComps.add(tmp);
        	
        }
    	return selectedSubComps;
    	    	
    }
    
    
    
    /*
     * get subcomponents of all types
     * @param tgc A component
     * @return subcompList A LinkedList of subcomponents
     */
    private LinkedList getSubComponents(TGComponent tgc)
    {
    	LinkedList subcompList=new LinkedList();
    	int nb=tgc.getNbInternalTGComponent();
    	for (int j=0;j<nb;j++)
		{
			TGComponent tmp=tgc.getInternalTGComponent(j);
			subcompList.add(tmp);

		}
    	return subcompList;
    }
    
    /*
     * returns all ports of this component
     * @param tgc A component
     * @return portsList A LinkedList with ports of the component
     */
    private LinkedList getPorts(TGComponent tgc)
    {
    	if (!(tgc.getType()==TGComponentManager.PROCSD_COMPONENT)) return null;
    	LinkedList subcompList=getSubComponents(tgc);
    	LinkedList portsList=new LinkedList();
        for (int i=0;i<subcompList.size();i++)
        {
        	TGComponent tmp=(TGComponent)subcompList.get(i);
            //Remove option delegate ports, by Solange
        	if ((tmp.getType()==TGComponentManager.PROCSD_IN_PORT) || (tmp.getType()==TGComponentManager.PROCSD_OUT_PORT))// || ( tmp.getType()==TGComponentManager.PROCSD_DELEGATE_PORT))
        		portsList.add(tmp);
        }
    	return portsList;
    }
    
    /*
	 * gets the port connected to the port in parameter via a TGConnector ProCSD if there is one, null if not
	 * @param port A port
	 * @return p1 (or p2) The connected port, or null if not connected 
	 */
    private ProCSDPort getConnectedProCSDPort(ProCSDPort port)
    {
         //Remove option delegate ports, by Solange
    	//if (port.getType()==TGComponentManager.PROCSD_DELEGATE_PORT) return null;
    	
    	 //System.out.println("cherche un port pour le port " +port.getValue()); comented by Emil 
    	
    		TGConnectorProCSD myConnector=port.getTGConnector();
    		
    		if (myConnector==null)
    		{
                //Commented because is not always an error to be free, by Solange 
    			//addCheckingError(CheckingError.STRUCTURE_ERROR,"We didn't find any connector for the port " +port.getValue());
    			return null;
    		}
    		
   // 		System.out.println("...... (ProCSDPort).. my connector is "+myConnector.toString());
    	
    		//ProCSDPort p1=myConnector.getMyPort1(portsList);
    		ProCSDPort p1=myConnector.getMyPort1();
            
   /* 		if (p1!=null) System.out.println(p1.toString());
    		else 
    			System.out.println("NULL!!!!!!!!!");
    */
    		
    		//System.out.println("......... my connector's Port1 is "+p1.toString());
    		
    				if ((p1!=null) && (!p1.equals(port))) return p1;
    		 
    		 ProCSDPort p2=myConnector.getMyPort2();
    			
    		 if ((p2!=null) && (!p2.equals(port))) return p2;
    			
    		return null;	
    }
    
    //Added by Solange for the subcomponents
    /*
     * Method to find the port connected to a port when there are subcomponents involved
     * @param port The port to be analyzed
     * @return p1 (or p2) The port connected or null if not connected
     */
    /*
    public ProCSDPort getDelegateConnectorProCSDPort(ProCSDPort port)
    {
    	TGConnectorDelegateProCSD myDelegateConnector=port.getTGConnectorDelegateIn();
		if (myDelegateConnector==null)
			myDelegateConnector=port.getTGConnectorDelegateOut();
		
		if (myDelegateConnector==null)
		{
		//	System.out.println("We didn't find any delegate connector for the port " +port.getValue());
			return null;
		}

		ProCSDPort p1=myDelegateConnector.getMyPort1(portsList);
		 if ((p1!=null) && (!p1.equals(port))) return p1;
		 ProCSDPort p2=myDelegateConnector.getMyPort2(portsList);
		 if ((p2!=null) && (!p2.equals(port))) return p2;
		return null;	
    }
    */
    /*
	 * gets the delegate port connected to the port in parameter via a TGConnectorDelegateProCSD if there is one, null if not
	 * returns null for delegate ports
	 * 
	 */
    
    //Delegate ports removes. Method is no t needed. By Solange
    /*
    private ProCSDPort getConnectedDelegatePort(ProCSDPort port)
    {
    	if (port.getType()==TGComponentManager.PROCSD_DELEGATE_PORT) return null;
    	 //System.out.println("cherche un port pour le port " +port.getValue()); 
    	
    		TGConnectorDelegateProCSD myDelegateConnector=port.getTGConnectorDelegateIn();
    		if (myDelegateConnector==null)
    			myDelegateConnector=port.getTGConnectorDelegateOut();
    		
    		if (myDelegateConnector==null)
    		{
    			 //System.out.println("We didn't find any connector for the port " +port.getValue());
    			return null;
    		}
    		
   // 		System.out.println("...... (ProCSDPort).. my connector is "+myConnector.toString());
    
    		ProCSDPort p1=myDelegateConnector.getMyPort1(portsList);

    		//if (p1!=null) System.out.println(p1.toString());
    		//else 
    		//	System.out.println("NULL!!!!!!!!!");
    
    		
    		//System.out.println("......... my connector's Port1 is "+p1.toString());
    		
    				if ((p1!=null) && (!p1.equals(port))) return p1;
    		 
    		 ProCSDPort p2=myDelegateConnector.getMyPort2(portsList);
    			
    		 if ((p2!=null) && (!p2.equals(port))) return p2;
    			
    		return null;	
    	
    	
    	
    }
    */
    
  /* private ProCSDPort getToOutsideConnectedPort(ProCSDPort dp)
    {
    	TGComponent father=dp.getFather().getFather();
    	if (father==null)
    	{
    		addCheckingError(CheckingError.STRUCTURE_ERROR,"Error: forbiden delegate port at this level "+dp.getValue());
    		return null;
    	}
    	
    	LinkedList fatherPorts=getPorts(father);
    	
    	
    	TGConnectorDelegateProCSD myDelegateConnectorOut=dp.getTGConnectorDelegateOut();
    	if (myDelegateConnectorOut==null) return null;
    	ProCSDPort p2=myDelegateConnectorOut.getMyPort2(fatherPorts);
			
		 if ((p2!=null) && (!p2.equals(dp))) return p2;
    	
    	return null;
    }
    */
    
/*   
    private ProCSDPort getFromOutsideConnectedPort(ProCSDPort dp)
    {
    	TGComponent father=dp.getFather().getFather();
    	if (father==null)
    	{
    		addCheckingError(CheckingError.STRUCTURE_ERROR,"Error: forbiden delegate port at this level "+dp.getValue());
    		return null;
    	}
    
    	LinkedList fatherPorts=getPorts(father);
    	TGConnectorDelegateProCSD myDelegateConnectorIn=dp.getTGConnectorDelegateIn();
    	if (myDelegateConnectorIn==null) return null;
    	ProCSDPort p2=myDelegateConnectorIn.getMyPort1(fatherPorts);
			
		 if ((p2!=null) && (!p2.equals(dp))) return p2;
    	
    	return null;
    }
    
  */  

//    private ProCSDPort getToInsideConnectedPort(ProCSDPort dp)
//    {
//    	//we're looking for a port who's not a father port
//    	TGComponent father=dp.getFather().getFather();
//    	if (father==null)
//    	{
//    		addCheckingError(CheckingError.STRUCTURE_ERROR,"Error: forbiden delegate port at this level "+dp.getValue());
//    		return null;
//    	}
//    	LinkedList fatherPorts=getPorts(father);
//    	
//    	TGConnectorDelegateProCSD myDelegateConnectorOut=dp.getTGConnectorDelegateOut();
//    	if (myDelegateConnectorOut==null) return null;
//    	ProCSDPort p2=myDelegateConnectorOut.getMyPort2(portsList);
//			
//		 if ((p2!=null) && (!p2.equals(dp)) && !fatherPorts.contains(p2)) return p2;
//    	
//    	return null;
//    	
//   	
//    
//    }
    
//    private ProCSDPort getFromInsideConnectedPort(ProCSDPort dp)
//    {
//    	
////    	we're looking for a port who's not a father's port
//    	TGComponent father=dp.getFather().getFather();
//    	if (father==null)
//    	{
//    		addCheckingError(CheckingError.STRUCTURE_ERROR,"Error: forbiden delegate port at this level "+dp.getValue());
//    	
//    		return null;
//    	}
//    	LinkedList fatherPorts=getPorts(father);
//    	
//    	TGConnectorDelegateProCSD myDelegateConnectorIn=dp.getTGConnectorDelegateIn();
//    	if (myDelegateConnectorIn==null) return null;
//    	ProCSDPort p1=myDelegateConnectorIn.getMyPort1(portsList);
//			
//		 if ((p1!=null) && (!p1.equals(dp)) && !fatherPorts.contains(p1)) return p1;
//    	
//    	return null;
//    	
//    }
    
   
   /*
    * we are dealing with a comp specification within a 
    * ProActiveCompSpecificationCSDPanel
    * 
    */
   private boolean parseStructureAndFillUpListsForCompDefinition(TGComponent t)
   {  
   	
   	if (t.getType()==TGComponentManager.PROCSD_INTERFACE) 
   		{
   		  interfacesList.add(t);
   		}
   
   	if (t.getType()==TGComponentManager.CONNECTOR_PROCSD) 
   	  {
   		connectorsList.add(t);
   	  }
   	
   	if (t.getType()==TGComponentManager.CONNECTOR_PROCSD_PORT_INTERFACE) 
 	  {
 		connectorsPortInterfacesList.add(t);
 	  }
 	 	
   /*	
   	//Delegate ports removed, by Solange
   	if ( (t.getType()==TGComponentManager.PROCSD_IN_PORT) || (t.getType()==TGComponentManager.PROCSD_OUT_PORT))// || (t.getType()==TGComponentManager.PROCSD_DELEGATE_PORT))
     		 portsList.add(t);      	    	
 
   */
   	
   	if ((t.getType()==TGComponentManager.PROCSD_COMPONENT) )
//   	if (ProCSDComponentsList.contains(t))
//   		return false;
//   		else
   	{
   		ProCSDComponentsList.add(t);
	
	
   		int nb=t.getNbInternalTGComponent();
   	
   		for (int j=0;j<nb;j++)
   		{
   				TGComponent tgc=t.getInternalTGComponent(j);
   				if (!parseStructureAndFillUpListsForCompDefinition(tgc)) return false;  				
   	    }
   	
   	
   		if (((ProCSDComponent)t).getMyDesignPanel()!=null)
		{
			ProActiveCompSpecificationCSDPanel specPanel=((ProCSDComponent)t).getMyDesignPanel();
			
			
		    for (int q=0;q<specPanel.componentList.size();q++)
		    {
		    	TGComponent component=(TGComponent)specPanel.componentList.get(q);
		    	if (!parseStructureAndFillUpListsForCompDefinition(component)) return false;
		    }
		    }
	}
   	
   	return true;
   	
   	}
  
   
    
   /*
    * Add a component to the corresponding list according to his type (interface, connector, port, component)
    * @param t The component
    */ 
    private boolean parseStructureAndFillUpLists(TGComponent t)
    {  
    	
    	if (t.getType()==TGComponentManager.PROCSD_INTERFACE) 
    		{
    		  interfacesList.add(t);
    		}
    
    	if (t.getType()==TGComponentManager.CONNECTOR_PROCSD) 
    	  {
    		connectorsList.add(t);
    	  }
    	
    	if (t.getType()==TGComponentManager.CONNECTOR_PROCSD_PORT_INTERFACE) 
  	  {
  		connectorsPortInterfacesList.add(t);
  	  }
  	 	
    	
    	//Delegate ports removed, by Solange
    	if ( (t.getType()==TGComponentManager.PROCSD_IN_PORT) || (t.getType()==TGComponentManager.PROCSD_OUT_PORT))// || (t.getType()==TGComponentManager.PROCSD_DELEGATE_PORT))
      		 portsList.add(t);      	    	
  
    
    	if ((t.getType()==TGComponentManager.PROCSD_COMPONENT) )
    	{
//    		if (ProCSDComponentsList.contains(t))
//    		{
//    			System.out.println("Cycle found.");
//    			return false;
//    		}
    	
    		
    		ProCSDComponentsList.add(t);
	
	
    		int nb=t.getNbInternalTGComponent();
    	
    		for (int j=0;j<nb;j++)
    		{
    				TGComponent tgc=t.getInternalTGComponent(j);
    				parseStructureAndFillUpLists(tgc);  
    	    }
    		
    		if (((ProCSDComponent)t).getMyDesignPanel()!=null)
    		{
    			ProActiveCompSpecificationCSDPanel specPanel=((ProCSDComponent)t).getMyDesignPanel();
    			
    			
    		    for (int q=0;q<specPanel.componentList.size();q++)
    		    {
    		    	TGComponent component=(TGComponent)specPanel.componentList.get(q);
    		    	if (!parseStructureAndFillUpListsForCompDefinition(component))
    		    		return false;
    		    }
    		    }
    	}
    return true;
    }
 /*   
    private void listElement(TGComponent t)
    {
    	System.out.println(t.toString());
    	    	
        	
    	if (t.getType()==TGComponentManager.PROCSD_COMPONENT)
        	{
    		System.out.println("ports : ");
    		LinkedList ports=getPorts(t);
    		for(int k=0;k<ports.size();k++)
    			{ ProCSDPort myPort=(ProCSDPort)ports.get(k);
    			  System.out.print(myPort.toString());
    			  ProCSDPort cp=getConnectedProCSDPort(myPort);
    			  if (cp==null)
    				  System.out.println(" no connected port has been found for the port "+myPort.getValue());
    			  else 
    				  System.out.println(" connected to "+getConnectedProCSDPort(myPort).toString());
    			}
  
    		System.out.println("inner interfaces : ");
    		LinkedList innerInterfaces=getSubComponents(t,TGComponentManager.PROCSD_INTERFACE);
    		for(int k=0;k<innerInterfaces.size();k++)
    			System.out.println(((TGComponent)innerInterfaces.get(k)).toString()+" ");
  
    		
    		System.out.println("< subcomponents of "+t.getValue()+" >");
    		  int nb=t.getNbInternalTGComponent();
        		//LinkedList list1=new LinkedList();
        		for (int j=0;j<nb;j++)
        			{
        				TGComponent tgc=t.getInternalTGComponent(j);
        				listElement(tgc);
        			
        	     				
        	}	
        		System.out.println("<\\subcomponents of "+t.getValue()+">");
        	}
    }
   
    */
    
    /*
    
    private void listPorts()
    {
    	System.out.println("\n\n Ports :");
    	 for (int i=0;i<portsList.size();i++)
    	 {
    	ProCSDPort p=(ProCSDPort)portsList.get(i);
    	     		 
    	 if ((p.getType()==TGComponentManager.PROCSD_IN_PORT)||(p.getType()==TGComponentManager.PROCSD_OUT_PORT))
    	  { System.out.print(p.toString());
    	    ProCSDPort cp=getConnectedProCSDPort(p);
    	    if (cp!=null)      
    		  System.out.println(" connected to "+cp.toString());
    	    
    	    //Delegate ports removed, by Solange
    	    //ProCSDPort dp=getConnectedDelegatePort(p);
    	    //if (dp!=null)      
     		  //System.out.println("    ........ delegates "+dp.toString());
    	  
    	   ProCSDInterface pi=p.getMyInterface(interfacesList);
    	   if (pi!=null)
    		   System.out.println("my interface : "+pi.getValue());
    	  }
    	
    	
    	ProCSDPort toPort=p.getToPort();
	      if (toPort!=null)
	          System.out.println("-----toPort--------> : "+toPort.getValue());
	 
	      ProCSDPort fromPort=p.getFromPort();
	      if (fromPort!=null)
	          System.out.println("<-----fromPort-------- : "+fromPort.getValue());
    	 
    	 }	
    }
    
    */
    
    
    
    /*
     * Prints the information of primitive components
     */
    
    
    /*
     private void listPrimitives()
     {
    	  for (int i=0;i<ProCSDComponentsList.size();i++)
          {
          	ProCSDComponent comp=(ProCSDComponent)ProCSDComponentsList.get(i);
          	
          	  if (isPrimitive(comp))
          	   {   
          		  System.out.println(comp.toString());
          	  LinkedList ports=getPorts(comp);
          	    for (int j=0;j<ports.size();j++)
          	    {
          	    	ProCSDPort p=(ProCSDPort)ports.get(j);
          	        ProCSDPort toP=p.getToPort();
          	        ProCSDPort fromP=p.getFromPort();
          	    
          	       if (toP!=null)
          	       {//this port is an out one
          	    	  System.out.println("Synchronisation with ->  "+toP.getFather().toString());
          	    	  System.out.println("Synchro gates :");
          	    	  ProCSDInterface myInterface=p.getMyInterface(interfacesList);
          	    	  LinkedList gates=myInterface.getMyMessages();
          	    	  for (int k=0;k<gates.size();k++)
          	    	  {
          	    		TAttribute a = (TAttribute)(gates.get(k));
          	    		System.out.println(a.toString());		
          	    	  }
          	    	   
          	       } else if (fromP!=null)
          	     {//this port is an out one
           	    	  System.out.println("Synchronisation with <-  "+fromP.getFather().toString());
           	    	  System.out.println("Synchro gates :");
           	    	  ProCSDInterface myInterface=p.getMyInterface(interfacesList);
           	    	  LinkedList gates=myInterface.getMyMessages();
           	    	  for (int k=0;k<gates.size();k++)
           	    	  {
           	    		TAttribute a = (TAttribute)(gates.get(k));
           	    		System.out.println(a.toString());		
           	    	  }
           	    	   
           	       }   
          	    }
          	   }//if is primitive
          }//for all components
        	
     }
   */  
    /*
     * Fill the primitive LinkedList
     * @return primitives The List of primitive components
     */
     private LinkedList getPrimitives()
     {
       LinkedList primitives=new LinkedList();	 
     
    	 for (int i=0;i<ProCSDComponentsList.size();i++)
         {
         	ProCSDComponent comp=(ProCSDComponent)ProCSDComponentsList.get(i);
        	  if (isPrimitive(comp)) primitives.add(comp);
         }       
      return primitives;
     }
     
     
     /*
      * Method implementing the algorithm used to create TClasses from a CTTool Component Model
      * @param tm TurtleModeling to be updated with TClasses
      * @param the main coponent of the aplication - the container of all components
      */
     private void addTClasses(TURTLEModeling tm, Component appComp)
     {
    	Collection<Component> primitives=appComp.getAllPrimitives();
    	
    	Iterator<Component> primitivesIterator=primitives.iterator();
    	while (primitivesIterator.hasNext())
    	{
    		Component comp=primitivesIterator.next();
    		TClass tclass =new TClass(comp.getPath().replaceAll("[.]","_"),true);
    		 // System.out.println("tClass created: "+comp.getValue());  
    		Collection<Attribute> attribs=comp.getAttributes();
    		
    		Iterator<Attribute> attribsIterator=attribs.iterator();
    		while (attribsIterator.hasNext())
    		{
    			Attribute modelAt=attribsIterator.next();
    			TAttribute a=new TAttribute(modelAt.getAccess(),modelAt.getName(),modelAt.getInitialValue(),modelAt.getType());
    			 //if (a.getType() == TAttribute.NATURAL) 
    			   {
    	                Param p = new Param(a.getId(), Param.NAT, a.getInitialValue());
    	                p.setAccess(a.getAccessString());
    	                tclass.addParameter(p);
    	            }
                
    		}
    		
    		Collection<Port> ports=comp.getPorts();    		
    		Iterator<Port> portsIterator=ports.iterator();
    		
     	    	while (portsIterator.hasNext())
     	             {
     	            	Port p = portsIterator.next();
     	            	Interface myInterface=p.getInterface();
     	            	if (myInterface==null)
     	            	   {
     	            		 addCheckingError(CheckingError.STRUCTURE_ERROR,"No interface found for the port " +p.getName()+" in component "+p.getFather().getPath());
     	            		 return;
     	            	 }


     	            	Collection<Message> messages= myInterface.getMessages();
     	            	//LinkedList gates=myInterface.getMyMessages();
           	    	  
     	            	Iterator<Message> msgsIterator =messages.iterator();
     	            	
           	    	  while (msgsIterator.hasNext())
           	    	  {
           	    		  Message msg=msgsIterator.next();
           	    		   //!!!! to see:
           	    		 // the gate type
           	    		  // internal gates           	    		
           	    		  Gate gt=new Gate(p.getName()+"_"+msg.getName(),Gate.GATE,false);	  
           	    	      tclass.addGate(gt);
           	    	  }           	    	 
     	             }//for ports
    	  tm.addTClass(tclass);
    	  
    	  ProactiveSMDPanel psmdp=(ProactiveSMDPanel)comp.getBehaviour();
    	  buildErrors=false;
    	  buildActivityDiagram(tclass,psmdp,false,comp.getName()+"_SMD");    	  
    	}//for all primitives
     }
     
     
     
     
     
     /*
      * Method implementing the algorithm used to create TClasses from a ProCSDComponent
      * @param tm TurtleModeling to be updated with TClasses
      */
     private void addTClasses(TURTLEModeling tm)
     {
    	LinkedList primitives=getPrimitives();
    	for (int i=0;i<primitives.size();i++)
    	{
    		ProCSDComponent comp=(ProCSDComponent)primitives.get(i);
    		 TClass tclass =new TClass(comp.getValue(),true);
    		 // System.out.println("tClass created: "+comp.getValue());  
    		LinkedList attribs=comp.getMyAttributes();
    		
    		for (int at=0;at<attribs.size();at++)
    		{
    			TAttribute a=(TAttribute)attribs.get(at);
    			 //if (a.getType() == TAttribute.NATURAL) 
    			   {
    	                Param p = new Param(a.getId(), Param.NAT, a.getInitialValue());
    	                p.setAccess(a.getAccessString());
    	                tclass.addParameter(p);
    	            }
                
    		}
    		LinkedList ports=getPorts(comp);
     	             for (int j=0;j<ports.size();j++)
     	             {
     	            	ProCSDPort p=(ProCSDPort)ports.get(j);
     	            	 ProCSDInterface myInterface=p.getMyInterface(interfacesList);
     	            	 if (myInterface==null)
     	            	 {
     	            		 addCheckingError(CheckingError.STRUCTURE_ERROR,"No interface found for the port " +p.getValue()+" in component "+p.getFather().getValue());
     	            		 return;
     	            		 
     	            	 }
           	    	  LinkedList gates=myInterface.getMyMessages();
           	    	  
           	    	  for (int g=0;g<gates.size();g++)
           	    	  {
           	    		  TAttribute ta=(TAttribute)gates.get(g);
           	    		 //!!!! to see:
           	    		 // the gate type
           	    		  // internal gates           	    		
           	    		  Gate gt=new Gate(p.getValue()+"_"+ta.getId(),Gate.GATE,false);	  
           	    	      tclass.addGate(gt);
           	    	  }
           	    	 
     	             }//for ports
    	  tm.addTClass(tclass);
    	  ProactiveSMDPanel psmdp=pdp.getSMDPanel(tclass.getName());
    	  buildErrors=false;
    	  buildActivityDiagram(tclass,psmdp,false,comp.getName()+"_SMD");    	  
    	}//for all primitives
     }
    
     
     
     private boolean verifyAllParamsDefined(TClass t,String action, String machineName)
     {
    	
    	 String msgDescription=action;
    	 int iSend=action.indexOf("!");
    	 int iReceived=action.indexOf("?");
    	 int end=action.length();
    	 boolean finished=false;
    	
    	if (iSend==-1) iSend=end;
 		if(iReceived==-1) iReceived=end;
 		int i1=Math.min(iSend,iReceived);
    	 
 		if (iSend==iReceived) return true;
    	 
    	 while (!action.equals(""))
    	 {
    		
    	//	if (current==end) break;
    		//action=action.substring(current+1);
    		 
    		action=action.substring(i1+1);
    		//if (action.equals("")) return true;
    		end=action.length();
    		iSend=action.indexOf("!");
       	    iReceived=action.indexOf("?");
       	    if (iSend==-1) iSend=end;
       	    if(iReceived==-1) iReceived=end;
    		
    		int i2=Math.min(iSend,iReceived);
    		String paramName=action.substring(0,i2);
    	//check if the param is a number. if not, check if it is defined as an attribute
    		try{ Integer.parseInt(paramName);
    			}
    		 catch(NumberFormatException ex)
    		 {
    			 if (t.getParamByName(paramName)==null)
    			 {
    				 this.addCheckingError(new CheckingError(CheckingError.BEHAVIOR_ERROR,"Error in state machine "+machineName+". In message ("+msgDescription+") parameter "+paramName+" not known."));
    				 return false;
    			 }
    		 }
    		 
    		if (i2==end) return true;
    		i1=i2;
    		
    		 
    	 }
    	 
    	 
    	 
    	 return false;
     }
     
     
     /*
      * Big and ugly Method implementing the algorithm used to create Activity Diagram from a State Machine Diagram
      * @param t TClass that owns the Activity Diagram
      * @param psmdp Proactive State Machine Diagram to be converted
      * @param isSubmachine Boolean value to see if it is a submachine
      */
     private void buildActivityDiagram(TClass t, ProactiveSMDPanel psmdp, boolean isSubmachine, String machineCode)
     {  
    	 int subMachineIndex=0;  
    	 
    	 if (!isSubmachine)
    		 //listE=new CorrespondanceTGElement();
    	  corespManager = new CorrespondanceSMDManager();
    		 
    	 if (psmdp==null)
    	 {
    	   
    		 CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "No state machine diagram found for primitive " +t.getName());
    	     addCheckingError(ce);
    	     return;
    	 }
    	 
    	 ActivityDiagram ad;
    	// System.out.println("building activity diagram for "+t.getName() + " from panel "+psmdp.getName());
    	 String name=t.getName();
         
         
         if (psmdp == null) {
             return;
         }
   
         LinkedList list = psmdp.getComponentList();
         Iterator iterator = list.listIterator();
         TGComponent tgc;
         ProSMDStartState proStart = null;
         int cptStart = 0;
        // System.out.println(t.getName()+" smd elements: ");
         while(iterator.hasNext()) {
             tgc = (TGComponent)(iterator.next());
             //System.out.println(tgc.getName()+":"+tgc.getValue());
             if (tgc instanceof ProSMDStartState) {
                 proStart = (ProSMDStartState) tgc;
                 cptStart ++;
             }
        
           //generating a unique code for each subMachine:  
           if (tgc instanceof ProSMDSubmachine) {
                 ProSMDSubmachine psm = (ProSMDSubmachine) tgc;
                // psm.setCode(psmdp.getName()+subMachineIndex+psm.getValue());
               psm.setCode(machineCode+subMachineIndex+psm.getValue());
                 subMachineIndex++;
             }
         
         
         }
    	 
         if (proStart == null) {
             CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "No start state in the state machine diagram of " + name);
             ce.setTClass(t);
             ce.setTDiagramPanel(psmdp);
             addCheckingError(ce);
             return;
         }
         
         if (cptStart > 1) {
             CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in the activity diagram of " + name);
             ce.setTClass(t);
             ce.setTDiagramPanel(psmdp);
             addCheckingError(ce);
             return;
         }
         
         //we treat differently the start state if it's a sub machine:
         //we put a junction for this start state and connect the connector to the submachine to this junction
         if (!isSubmachine)
         {
          ADStart ads = new ADStart();
          
          //listE.addCorInPanel(ads, proStart,psmdp.name);
          corespManager.addCorrespondance(ads,proStart,psmdp.getName(),machineCode);
          
          ad = new ActivityDiagram(ads);
          t.setActivityDiagram(ad);
         }

         else
        	 //we treat differently the start state if it's a sub machine:
             //we put a junction for this start state and connect the connector to the submachine to this junction
             
         {
        	 ad=t.getActivityDiagram();
        	 ADJunction adj=new ADJunction();
        	 proStart.setValue(psmdp.getName()+"_start");
        	 ad.addElement(adj);
        	 //listE.addCorInPanel(adj, proStart,psmdp.name);
        	 corespManager.addCorrespondance(adj,proStart,psmdp.getName(),machineCode);
         }
          
         iterator = list.listIterator();
         while(iterator.hasNext()) {
             tgc = (TGComponent)(iterator.next());
           
             if (tgc instanceof ProSMDGetMsg) {
               ProSMDGetMsg getMsg= (ProSMDGetMsg) tgc;
               String action=getMsg.getAction();
               
               
               
               
               //t.getParamFromActionState("");
               
               String viaPort=getMsg.getViaPort();
               Gate g=t.getGateFromActionState(viaPort+"_"+action);
           
               
               //   System.out.println("looking for gate "+viaPort+"_"+action+" in class" +t.getName());
               if (g==null) 
               {
            	   CheckingError ce=new CheckingError(CheckingError.BEHAVIOR_ERROR,psmdp.getName()+" smd :"+action+" via port "+viaPort+" not found. Verify method name and port.");
              	 //System.out.println("!!!!!!!!!!!!!!!!!1Error! no gate found");
                  addCheckingError(ce); 
            	    buildErrors=true;
                  return;
               }
             
              verifyAllParamsDefined(t,action,psmdp.getName());
               
               //  System.out.println("gate created: "+g.getName());
               ADActionStateWithGate adag = new ADActionStateWithGate(g);
               ad.addElement(adag);
               String params = t.getActionValueFromActionState(action);
               params = TURTLEModeling.manageGateDataStructures(t, params);
               params = TURTLEModeling.addTypeToDataReceiving(t, params);
               
               adag.setActionValue(params);
             
               //listE.addCorInPanel(adag, tgc,psmdp.name);
               corespManager.addCorrespondance(adag, tgc,psmdp.getName(),machineCode);
               
               
             }//if prosmdGetMessage 
             else
            	 if (tgc instanceof ProSMDSendMsg) {
                     ProSMDSendMsg sendMsg= (ProSMDSendMsg) tgc;
                     String action=sendMsg.getAction();
                     String viaPort=sendMsg.getViaPort();
                     Gate g=t.getGateFromActionState(viaPort+"_"+action);
               //     System.out.println("looking for gate "+viaPort+"_"+action+" in class" +t.getName());
                     if (g==null) 
                     {
                  	    CheckingError ce=new CheckingError(CheckingError.BEHAVIOR_ERROR,psmdp.getName()+" smd :"+action+" via port "+viaPort+" not found. Verify method name and port.");
                    	 //System.out.println("!!!!!!!!!!!!!!!!!1Error! no gate found");
                        addCheckingError(ce); 
                  	    buildErrors=true;
                        return;
                     }
              
                     verifyAllParamsDefined(t,action,psmdp.getName());
                     
                     //   System.out.println("gate created: "+g.getName());
                     ADActionStateWithGate adag = new ADActionStateWithGate(g);
                     ad.addElement(adag);
                     String params = t.getActionValueFromActionState(action);
                     params = TURTLEModeling.manageGateDataStructures(t, params);
                     params = TURTLEModeling.addTypeToDataReceiving(t, params);
                     
                     adag.setActionValue(params);
                  //   listE.addCorInPanel(adag, tgc,psmdp.name);
                     corespManager.addCorrespondance(adag, tgc,psmdp.getName(),machineCode);
                   }//if prosmdGetMessage 
                  
            	 else if (tgc instanceof ProSMDAction)
            	 {
            		String s = ((ProSMDAction)tgc).getAction();
            		 s = s.trim();
            		 s = TURTLEModeling.manageDataStructures(t, s);
            		 Param p = t.getParamFromActionState(s);
            		 if (p == null) 
            		  {
            			 CheckingError ce=new CheckingError(CheckingError.BEHAVIOR_ERROR,psmdp.getName()+" smd : Error in action "+tgc.getValue());
                    	 //System.out.println("!!!!!!!!!!!!!!!!!1Error! no gate found");
                        addCheckingError(ce); 
                  	    buildErrors=true;
                        return;
            	     }
            		
            		// System.out.println("Action state with param found " + p.getName() + " value:" + t.getExprValueFromActionState(s));
            		 ADActionStateWithParam adap = new ADActionStateWithParam(p);
            		ad.addElement(adap);
            		adap.setActionValue(TURTLEModeling.manageDataStructures(t, t.getExprValueFromActionState(s)));
            		// System.out.println("action:"+adap.getParam()+"="+adap.getActionValue());
            		//listE.addCorInPanel(adap, tgc,psmdp.name);
            		corespManager.addCorrespondance(adap, tgc,psmdp.getName(),machineCode);
            	 }
             else if (tgc instanceof ProSMDChoice) {
                 ADChoice adch = new ADChoice();
                 ad.addElement(adch);
                 //listE.addCorInPanel(adch, tgc,psmdp.name);
                 corespManager.addCorrespondance(adch, tgc,psmdp.getName(),machineCode);
             } 
             else if (tgc instanceof ProSMDJunction) {
                ADJunction adj = new ADJunction();
                 ad.addElement(adj);
               //  listE.addCorInPanel(adj, tgc,psmdp.name);
                 corespManager.addCorrespondance(adj, tgc,psmdp.getName(),machineCode);
             }
             else if (tgc instanceof ProSMDState) {
              //   System.out.println("state found :" +tgc.getValue());

                // ADComponent adc=listE.getADComponentByName(tgc.getValue(),psmdp.name);
                 ADComponent adc = corespManager.getADComp((ProSMDState)tgc,psmdp.getName(),machineCode);
                 
                 if (adc==null)
                  { ADJunction adj = new ADJunction();
                    ad.addElement(adj);                    
                 
                    //listE.addCorInPanel(adj, tgc,psmdp.name);                 
                    corespManager.addCorrespondance(adj, tgc,psmdp.getName(),machineCode);
                   // System.out.println("first instance of  :" +tgc.getValue());
                  }
                  else
                  {
                	  if (!(adc instanceof ADJunction)) 
                	  {
                		  
                   //   	System.out.println("Error (bug): to the state "+tgc.getValue()+" no coresponding junction founded. Please send your xml file to the producer of this software." );
                        addCheckingError(new CheckingError(CheckingError.BEHAVIOR_ERROR,"This error should never happen. This is a bug. Please send your model to the producer. Thank you"));
                      	buildErrors=true;
                      	return;
                	  }
                        //listE.addCorInPanel((ADJunction)adc,tgc,psmdp.name);
                	   corespManager.addCorrespondance(adc,tgc,psmdp.getName(),machineCode);
                       // System.out.println("other instance of  :" +tgc.getValue());
                  }
             }
            //else if (tgc instanceof ProSMDParallel) 
             else if (tgc.getType()==TGComponentManager.TAD_PARALLEL)
              //we translate a paralel operator as a non deterministic choice: 
            	 
            	 /* {
                System.out.println("!!!!!!!!!!!!!!!!1Parallel found ");
            	 ADParallel adp = new ADParallel();
                 ad.addElement(adp);
                 adp.setValueGate(((TADParallel)tgc).getValueGate());
                 listE.addCorInPanel(adp, tgc,psmdp.name);
             } */
             {
                 ADChoice adch = new ADChoice();
                 ad.addElement(adch);
                 //listE.addCorInPanel(adch, tgc,psmdp.name);
                 corespManager.addCorrespondance(adch, tgc,psmdp.getName(),machineCode);
             } 
             else if (tgc instanceof ProSMDStopState) {
               if(!isSubmachine)
               {
            	 ADStop adst = new ADStop();
                 ad.addElement(adst);
                 //listE.addCorInPanel(adst, tgc,psmdp.name);
                 corespManager.addCorrespondance(adst, tgc,psmdp.getName(),machineCode);
               }
               else
               {
            	   //for a submachine we put a junction for it's stop state
            	   ADJunction adj=new ADJunction();
            	   tgc.setValue(psmdp.getName()+"_stop");
            	   ad.addElement(adj);
            	 //  listE.addCorInPanel(adj,tgc,psmdp.name);
            	   corespManager.addCorrespondance(adj,tgc,psmdp.getName(),machineCode);            	   
               }
              }       
             
             else if (tgc instanceof ProSMDSubmachine) {
                 //System.out.println("Submachine found: " +tgc.getValue());
                 ProactiveSMDPanel subMachinePanel=pdp.getSMDPanel(tgc.getValue());
                 //System.out.println("Panel found: " +subMachinePanel.getName());
                //recursive:
              if (subMachinePanel==null)
              {
        		 CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "StateMachine "+t.getName()+": No state machine diagram found for submachine " +tgc.getValue());
        	     addCheckingError(ce);
                 return;
              }
        	     buildActivityDiagram(t,subMachinePanel,true,((ProSMDSubmachine)tgc).getCode());
        	     if (buildErrors) return;
             }       
             
         }//while hasnext - creating elements
         
         // Connecting elements
         iterator = list.listIterator();
         while(iterator.hasNext()) {
             tgc = (TGComponent)(iterator.next());
             if (tgc instanceof TGConnectorProSMD) {
                 TGConnectorProSMD con=(TGConnectorProSMD) tgc;
                 TGConnectingPoint p1 = con.getTGConnectingPointP1();
                 TGConnectingPoint p2 = con.getTGConnectingPointP2();
                 
                 // identification of connected components
                 TGComponent tgc1 = null; 
                 TGComponent tgc2 = null;
                 
                 for(int j=0; j<list.size(); j++) {
                     TGComponent tmp = 	(TGComponent)(list.get(j));
                     if (tmp.belongsToMe(p1)) {
                         tgc1 = tmp;
                     }
                     if (tmp.belongsToMe(p2)) {
                         tgc2 = tmp;
                     }
                 }//for
                 
                 // connecting turtle modeling components
                 if ((tgc1 != null) && (tgc2 != null)) {
                     //ADComponent ad1, ad2;
                    
                	// System.out.println("tgc1: "+tgc1.toString()+"-----tgc2: "+tgc2.toString());
                	 //ADComponent ad1 = listE.getADComponentInPanel(tgc1, psmdp.name);
                    // ADComponent ad2 = listE.getADComponentInPanel(tgc2, psmdp.name);
                     
                	 ADComponent ad1 = corespManager.getADComp(tgc1, psmdp.getName(),machineCode);
                	 ADComponent ad2 = corespManager.getADComp(tgc2, psmdp.getName(),machineCode);
                     
                     if (tgc2 instanceof ProSMDSubmachine)
                     {
                   	//   System.out.println("connetor to a submachine. Looking for "+ tgc2.getValue()+"_start");
                    	 //ad2=listE.getADComponentByName(tgc2.getValue()+"_start",tgc2.getValue()); 
                    	//should look for the start of the subMachine
                    	 TGComponent startTGComp=getStartComp((ProSMDSubmachine) tgc2);
                    	 if (startTGComp==null)
                    	 { addCheckingError(CheckingError.BEHAVIOR_ERROR,"No start state found in state machine "+tgc2.getValue());
                    	  return;
                    	 }
                    	 ad2=corespManager.getADComp(startTGComp,tgc2.getValue(),((ProSMDSubmachine)tgc2).getCode());
                     }                     
                     
                     if (tgc1 instanceof ProSMDSubmachine)
                     {  //System.out.println("connetor from a submachine. Looking for "+ tgc2.getValue()+"_stop");
                   	   //ad1=listE.getADComponentByName(tgc1.getValue()+"_stop",tgc1.getValue()); 
                         TGComponent stopTGComp=getStopComp((ProSMDSubmachine)tgc1); 
                    	 
                         if (stopTGComp==null)
                    	 { addCheckingError(CheckingError.BEHAVIOR_ERROR,"No stop state found in state machine "+tgc1.getValue()+". "+tgc2.toString()+" in panel "+psmdp.getName()+" is not reachable.");
                    	  return;
                    	 }
                         
                         ad1=corespManager.getADComp(stopTGComp,tgc1.getValue(),((ProSMDSubmachine)tgc1).getCode());
                     }                     
                     
                     if ((ad1 == null) || (ad2 == null)) {
                         System.out.println("Correspondance issue");
                     }
                     
                     int index = 0; 
                      if (tgc1 instanceof ProSMDChoice) {
                         ProSMDChoice tadch = (ProSMDChoice)tgc1;
                         index = tgc1.indexOf(p1) - 1;
                         ((ADChoice)ad1).addGuard(TURTLEModeling.manageGateDataStructures(t, tadch.getGuard(index)));
                         ad1.addNext(ad2);
                      }
                      
                        else 
                             ad1.addNextAtIndex(ad2, index);
                  }//if ((tgc1 != null) && (tgc2 != null)) 
             }//if TGConnector
         }//while - connecting elements
         
     psmdp.count++;
     }
     
     
     private TGComponent getStartComp(ProSMDSubmachine subMachine)
     {
    	   ProactiveSMDPanel subMachinePanel=pdp.getSMDPanel(subMachine.getValue());
    	   LinkedList list = subMachinePanel.getComponentList();
           Iterator iterator = list.listIterator();
           TGComponent tgc;
           ProSMDStartState proStart = null;
            while(iterator.hasNext()) {
               tgc = (TGComponent)(iterator.next());
                 if (tgc instanceof ProSMDStartState) {
                   proStart = (ProSMDStartState) tgc;
                  return proStart;  
                 }//if
            }//while
            	
         return null; 
    	   
    	   
     }
     
    
     

     private TGComponent getStopComp(ProSMDSubmachine subMachine)
     {
    	   ProactiveSMDPanel subMachinePanel=pdp.getSMDPanel(subMachine.getValue());
    	   LinkedList list = subMachinePanel.getComponentList();
           Iterator iterator = list.listIterator();
           TGComponent tgc;
           ProSMDStopState proStop = null;
            while(iterator.hasNext()) {
               tgc = (TGComponent)(iterator.next());
                 if (tgc instanceof ProSMDStopState) {
                   proStop = (ProSMDStopState) tgc;
                  return proStop;  
                 }//if
            }//while
            	
         return null; 
    	   
    	   
     }
    
     
     
     /*
      * Method used when converting from ProactiveDesign to TurtleModeling
      * that implements the algorithm to see if creates a Synchronisation between
      * Tclasses.
      * @param tm TurtleModeling to be updated
      */
 
    /*
	private void addSynchronisationsOld(TURTLEModeling tm)
     {
    	 String gname1="", gname2="";
    	 LinkedList primitives=getPrimitives();
     	for (int i=0;i<primitives.size();i++)
     	{
     		ProCSDComponent comp=(ProCSDComponent)primitives.get(i);
         	      LinkedList ports=getPorts(comp);
         	      
         	       for (int j=0;j<ports.size();j++)
         	    {
         	    	ProCSDPort p=(ProCSDPort)ports.get(j);
         	        ProCSDPort toP=p.getToPort();
         	        ProCSDPort fromP=p.getFromPort();
         	        //Added by Solange
         	        TClass t1=tm.getTClassWithName(p.getFather().getValue());
         	        //Added by Solange
         	        puerto=p;
         	        //Added by Solange
         	        if (t1==null)
      	    	    {
         	        t1=searchInport(fromP);
             	    //flag=1;
      	    	    }
         	        
         	       if (t1==null)
     	    	    {
        	        t1=searchOutport(toP);
            	    //flag=2;
     	    	    }
         	       puerto2=puerto; //Added by Solange to save this value    	    
      	    	   // until here
         	       ProCSDPort tmpP; 
         	        if (toP!=null) tmpP=toP;
         	        else{
         	        	if(fromP!=null)
         	        	 tmpP=fromP;
         	        	else
         	        	{
         	        	 System.out.println("Port " + p.toString() + " is not connected");
         	        	 break;
         	        	}
         	        }
         	        
         	        {//this port is an out one
         	    	  //System.out.println("Synchronisation with ->  "+toP.getFather().toString());
         	    	  //System.out.println("Synchro gates :");
         	          puerto=tmpP; //Added by Solange
         	          TClass t2=tm.getTClassWithName(tmpP.getFather().getValue());
         	    	 //Added by Solange this two if 
         	    	 if (t2==null)
      	    	     {
         	          t2=searchOutport(puerto.getToPort());
      	    	     }
         	    	 if (t2==null)
       	    	     {
          	          t2=searchInport(puerto.getFromPort());
       	    	     }
          	           	   //
         	          ProCSDInterface myInterface=p.getMyInterface(interfacesList);
         	    	  LinkedList gates=myInterface.getMyMessages();
         	    	  for (int k=0;k<gates.size();k++)
         	    	  {
         	    		TAttribute a = (TAttribute)(gates.get(k));
         	    		gname1=puerto2.getValue()+"_"+a.getId();
         	    		gname2=puerto.getValue()+"_"+a.getId();
         	    		//i want to see the value, by Solange
                        // Added by Solange to handle messages not received
         	    		if((t1!=null)&&(t2!=null))
         	    		{
         	    		Gate h=t1.getGateByName(gname1);
         	    		Gate y=t2.getGateByName(gname2);
         	    		tm.addSynchroRelation(t1,h,t2,y);
         	    		}
         	    	  }
          	       }
         	    }
         }//for all components
     }
    
	*/
	
     
     
     /*
      * Method used when converting from ProactiveDesign to TurtleModeling
      * that implements the algorithm to see if creates a Synchronisation between
      * Tclasses.
      * @param tm TurtleModeling to be updated
      */
     private void addSynchronisations(TURTLEModeling tm, Component appComp)
     {
    	 String gname1="", gname2="";
    
    	 Collection<Component> primitives=appComp.getAllPrimitives();
     	
     	Iterator<Component> primitivesIterator=primitives.iterator();
     	while (primitivesIterator.hasNext())
     	{
     		Component comp=primitivesIterator.next();
     	    
     		Collection<Port> ports=comp.getPorts();    		
    		Iterator<Port> portsIterator=ports.iterator();
    		
     	    	while (portsIterator.hasNext())
     	             {
     	            	Port p = portsIterator.next();
     	                Port toP=p.getLastToPort();
         	            Port fromP=p.getLastFromPort();
         	            TClass t1=tm.getTClassWithName(p.getFather().getPath().replaceAll("[.]","_"));
         	        
         	            TClass t2=null;
         	            Port connectedPort=null; 
         	       
         	            if (toP!=null)
         	                	connectedPort=toP;
         	    	       else if (fromP!=null)
         	       	        	connectedPort=fromP;
         	           if (connectedPort==null)
         	        {
         	        	
         	           System.out.println("Problem in finding conection for "+p.toString());
         	           //TODO add an error here
         	          Interface myInterface=p.getInterface();
         	          
         	          if ( (myInterface!=null) && (myInterface.isMandatory())) 
         	           {this.addCheckingError(CheckingError.STRUCTURE_ERROR,"No connection found for mandatory port "+p.getFather().getPath()+"->"+p.getName());
         	            return;		   
         	           }//if is mandatory
         	          }//if connectedPort!=null
         	           else
         	           {
         	         
         	          
         	        	   
         	          t2=tm.getTClassWithName(connectedPort.getFather().getPath().replaceAll("[.]","_"));     	        
         	    	  Interface myInterface=p.getInterface();
         	    	  Interface theOtherInterface=connectedPort.getInterface();
         	    	  
         	    	  Iterator<Message> msgIt = myInterface.getMessages().iterator();
         	    	 while (msgIt.hasNext())
         	    	  {
         	    		Message m=msgIt.next();
         	    		if (theOtherInterface.getMessageByName(m.getName())==null)
         	    		{
         	    			addCheckingError(new CheckingError(CheckingError.STRUCTURE_ERROR,"Interfaces incompatible. Interface1:"+p.getFather().getPath()+"_"+p.getName()+"->"+myInterface.getName()+". Interface2: "+connectedPort.getFather().getPath()+"_"+connectedPort.getName()+"->"+theOtherInterface.getName()));
         	    		}
         	    		gname1=p.getName()+"_"+m.getName();
         	    		gname2=connectedPort.getName()+"_"+m.getName();
         	    		//i want to see the value, by Solange
                        // Added by Solange to handle messages not received
         	    		if((t1!=null)&&(t2!=null))
         	    		{
         	    		Gate h=t1.getGateByName(gname1);
         	    		Gate y=t2.getGateByName(gname2);
         	    		tm.addSynchroRelation(t1,h,t2,y);
         	    		}
         	    	  }
         	         }//connectedPort!=null
         	        }

         }//for all components
     }
     
     
     
     
     /*
      * Method used when converting from ProactiveDesign to TurtleModeling
      * that implements the algorithm to see if creates a Synchronisation between
      * Tclasses.
      * @param tm TurtleModeling to be updated
      */
     private void addSynchronisations(TURTLEModeling tm)
     {
    	 String gname1="", gname2="";
    	 LinkedList primitives=getPrimitives();
     	for (int i=0;i<primitives.size();i++)
     	{
     		ProCSDComponent comp=(ProCSDComponent)primitives.get(i);
         	      LinkedList ports=getPorts(comp);
         	      
         	       for (int j=0;j<ports.size();j++)
         	    {
         	    	ProCSDPort p=(ProCSDPort)ports.get(j);
         	        ProCSDPort toP=p.getToFinalPort();
         	        ProCSDPort fromP=p.getFromFinalPort();
         	        
         	        //Added by Solange
         	        TClass t1=tm.getTClassWithName(p.getFather().getValue());
         	        //Added by Solange
         	       TClass t2=null;
         	       ProCSDPort connectedPort=null; 
         	       
         	       if (toP!=null)
         	        {
         	        	connectedPort=toP;
         	    	    }
         	        else if (fromP!=null)
         	       {
         	        	connectedPort=fromP;
         	        	         	        }	
         	        else
         	        {
         	      //  	System.out.println("Problem in finding conection for "+p.toString());
         	      addCheckingError(CheckingError.STRUCTURE_ERROR,"Problem in finding conection for "+p.toString()+" in component "+p.getFather().toString());
         	        	//TODO add an error here
         	        }
         	         t2=tm.getTClassWithName(connectedPort.getFather().getValue());     	        
         	    	 ProCSDInterface myInterface=p.getMyInterface();
         	    	  LinkedList gates=myInterface.getMyMessages();
         	    	  for (int k=0;k<gates.size();k++)
         	    	  {
         	    		TAttribute a = (TAttribute)(gates.get(k));
         	    		gname1=p.getValue()+"_"+a.getId();
         	    		gname2=connectedPort.getValue()+"_"+a.getId();
         	    		//i want to see the value, by Solange
                        // Added by Solange to handle messages not received
         	    		if((t1!=null)&&(t2!=null))
         	    		{
         	    		Gate h=t1.getGateByName(gname1);
         	    		Gate y=t2.getGateByName(gname2);
         	    		tm.addSynchroRelation(t1,h,t2,y);
         	    		}
         	    	  }
          	       }

         }//for all components
     }
   
  //Method created by Solange
//   and removed by emil 
       /*
  public TClass searchOutport(ProCSDPort p)
  {
	  puerto=p; //Added by Solange
	  TClass t1=tm.getTClassWithName(p.getFather().getValue());
	  if(t1==null)
	  {
		puerto=p.getToPort();  
	    t1=searchOutport(puerto);
	  }
	  return(t1);
  }
 */
  
  //Method created by Solange
  //and removed by emil 
  
  /*
  public TClass searchInport(ProCSDPort p)
  {
	  puerto=p;
	  TClass t1=tm.getTClassWithName(p.getFather().getValue());
	  if(t1==null)
	  {
		puerto=p.getFromPort();
	    t1=searchInport(puerto);
	  }
	  return(t1);
  }
 */
  
  // 
     /*
     private void listClasses(TURTLEModeling tm )     
     {
    	 int nb=tm.classNb();
    	 for (int i=0;i<nb;i++)
    	 {
    		 TClass t=tm.getTClassAtIndex(i);
    		 System.out.println("TClass:"+ t.getName());
    		 System.out.println("gates: ");
    		 Vector gates=t.getGateList(); 
    		 for (int j=0;j<gates.size();j++)
    		 {
    			 Gate g=(Gate)gates.elementAt(j);
    		     System.out.println(g.toString());
    		 }
    	      
    	 }
    	 System.out.println("--------Relations : ---------");
    	for (int r=0;r<tm.relationNb();r++)
    	{
    		Relation rel=tm.getRelationAtIndex(r);
    		rel.print();
    		
    	}
    	
     }
    */ 
   
    
    public Vector getCheckingWarnings() {
        return null;
    }
    
    public Vector getCheckingErrors() {
    	return checkingErrors;
    }
    
    private void addCheckingError(CheckingError ce) {
       checkingErrors.add(ce);
    }
    private void addCheckingError(int t,String s)
    {
    	checkingErrors.add(new CheckingError(t,s));
    }
}