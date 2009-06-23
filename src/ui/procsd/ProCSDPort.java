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
 * Class ProCSDPort
 * Node. To be used in Proactive Composite Structure Diagram
 * Creation: 11/07/2006
 * @version 1.0 11/07/2006
 * @author Emil Salageanu
 * @see
 */


package ui.procsd;

import ui.CheckingError;
import ui.IconManager;
import ui.MalformedModelingException;
import ui.TDiagramPanel;
import ui.TGCWithoutInternalComponent;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.TGConnectingPoint;
import ui.cd.TGConnectorAssociation;
//Added by Solange
import ui.TGConnectorAttribute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;
import ui.TAttribute;
import java.util.LinkedList;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
public abstract class ProCSDPort  extends TGCWithoutInternalComponent implements ActionListener {

	private ProCSDInterface myInterface=null;
    private ProCSDPort toPort=null;
    private ProCSDPort fromPort=null;
    //Changed by Solange, before hidden=false
    public boolean hidden=false;
	private String portCode;
	
	private Class javaInterface=null;
	
    
	public String getPortCode() {
		return portCode;
	}



	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}



	public ProCSDPort(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  
	{
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
       if (father!=null)
        portCode=((ProCSDComponent)_father).getNewPortCode();
     }
	
	
	
	public void setMyInterface(ProCSDInterface pi)
	{
		// No more delegate ports, by Solange
		/*
		if (this.getType()!=TGComponentManager.PROCSD_DELEGATE_PORT)
		 System.out.println("operation allowed only for delegate ports !!!!!");
		else */
			myInterface=pi;
		 
	}

	
	
	
	//Rewritten by Emil:
	//Method Added by Solange
	/*
	 * returns the interface connected to the port. If NO interface, the value is null.
	 * @return myInterface The interface connected to this port
	 */
	public ProCSDInterface getMyInterface()
	{
		if (myInterface==null) return null;		
		
		if (!myInterface.isRemovedFromPanel())
			return(myInterface);
		else
		{
			myInterface=null;
			return null;
		}
	}
	
	
	
	public boolean connectInterface(ProCSDInterface newInterface)
	{
		if (this.getMyInterface()!=null)
		{
			return false;
		}
	
		TGConnectingPoint point1=this.getTGConnectingPointAtIndex(0);
		TGConnectingPoint point2=newInterface.getTGConnectingPointAtIndex(0);
		TGConnectorPortInterface connector=new TGConnectorPortInterface(0,0,0,0,0,0,false,null,this.tdp,point1,point2,new Vector());
	    this.tdp.addBuiltConnector(connector);
		return true;
	
	}
	
	
	
	/*
	 * returns an interface wich contains the messages who pass through this port
	 * for the IN or OUT port we look for this interface in the list of interfaces 
	 * for the delegate ports we return myInterface 
	 */
	
	public ProCSDInterface getMyInterface(LinkedList interfacesList)
	{

		if (myInterface!=null)
		{
			return myInterface;
		}
		
		//No more delegate ports, by Solange
		/*
		if (this.getType()==TGComponentManager.PROCSD_DELEGATE_PORT)
		   return myInterface;
		*/
		
		//No needed, by Solange
		//TGConnectorProCSD tgc=this.getTGConnector();
		//if (tgc==null) return null;
		
		//Changed, by Solange
		//ProCSDInterface pi=tgc.getMyInterface(interfacesList);
        ProCSDInterface pi=getMyInterface2(interfacesList);
		return pi;
	}
	
    //Added by Solange from TGConnectorProCSD.java
	
	private boolean isMyAttribute(TGConnectorAttribute tgc)
	{
		for (int i=0;i<nbConnectingPoint;i++)
		{
			if ( tgc.getTGConnectingPointP1().equals(this.getTGConnectingPointAtIndex(i)) || tgc.getTGConnectingPointP2().equals(this.getTGConnectingPointAtIndex(i)))
		        return true;
		}
		return false;
	}
		
	//Added by Solange from TGConnectorProCSD.java
	 private TGConnectorAttribute getMyTGConnectorAttribute()
	 {

		 	LinkedList cmps=this.tdp.getComponentList();
		 	for (int i=0;i<cmps.size();i++)
		 		{
		 		TGComponent tgc = (TGComponent)cmps.get(i);
				if ( (tgc.getType()== TGComponentManager.CONNECTOR_ATTRIBUTE) || (tgc.getType()== TGComponentManager.CONNECTOR_PROCSD_PORT_INTERFACE))
					if (isMyAttribute((TGConnectorAttribute)tgc)) return (TGConnectorAttribute)tgc;	 			 		
		 		}
		 
		 return null;
		  
	 }
	
	 // Added by Solange from TGConnectorProCSD.java
	 private boolean isInterfaceConnected(ProCSDInterface pci,TGConnectorAttribute tgca)
	  	{
	 	 	for (int i=0;i<pci.getNbConnectingPoint();i++)
	 	 	{
	 	 		if (tgca.getTGConnectingPointP1().equals(pci.getTGConnectingPointAtIndex(i)) || (tgca.getTGConnectingPointP2().equals(pci.getTGConnectingPointAtIndex(i))))
	 	 				return true;
	 	 		
	 	 	}
	 	 
	 	 return false;
	  	}
	
		
	//Added by Solange from TGConnectorProCSD.java
	public ProCSDInterface getMyInterface2(LinkedList interfacesList)
	{
	  TGConnectorAttribute tgca=getMyTGConnectorAttribute();
	  if(tgca!=null)
	  {
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
	  else
	  {
		  //System.out.println(toString() + "doesn't have an Interface connected");
		  return null;
	  }
	 
		
	}
	//
	
	
	public void setToPort(ProCSDPort toP)
	{
		toPort=toP;
	}	
	
	public ProCSDPort getToPort()
	{
		if (toPort==null)
		{
		 ProCSDPort p=this;
		 ProCSDComponent portFather=(ProCSDComponent)p.getFather();
		 ProCSDComponent designComp=portFather.getThisCompDesign();
	    if (designComp==null) return null;
		ProCSDPort copyOfP=designComp.getPortByName(p.getValue());
		
		return copyOfP.getToPort();
		}
		else
		return toPort;
		
	}
	
	public void setFromPort(ProCSDPort fromP)
	{
		fromPort=fromP;
	}	
	
	public ProCSDPort getFromPort()
	{

		if (fromPort==null)
		{
		 ProCSDPort p=this;
		 ProCSDComponent portFather=(ProCSDComponent)p.getFather();
		 ProCSDComponent designComp=portFather.getThisCompDesign();
	    if (designComp==null) return null;
		ProCSDPort copyOfP=designComp.getPortByName(p.getValue());
		
		return copyOfP.getFromPort();
		}
		else
		return fromPort;
	}
	
	
	/*
	 * gets the last port this port is connected to through an out connexion (binding)
	 */
	public ProCSDPort getToFinalPort()
	{
		ProCSDPort myToPort=this.getToPort();
		if (myToPort==null) return null;
		ProCSDPort finalPort=myToPort;
		while (finalPort.getToPort()!=null)
		{
			finalPort=finalPort.getToPort();
		}
	return finalPort;
	}
	
	/*
	 * gets the last port this port is connected to through an in connexion(binding)
	 */
	public ProCSDPort getFromFinalPort()
	{
		ProCSDPort myFromPort=this.getFromPort();
		if (myFromPort==null) return null;
		ProCSDPort finalPort=myFromPort;
		while (finalPort.getFromPort()!=null)
		{
			finalPort=finalPort.getFromPort();
		}
	return finalPort;
	}
	
	
	
	private boolean isMyConnectorIn(TGConnectorAssociationProCSD tgc)
	{
		for (int i=0;i<nbConnectingPoint;i++)
		{
			if ( tgc.getTGConnectingPointP2().equals(this.getTGConnectingPointAtIndex(i)) )
		        return true;
		}
		return false;
	}
	
	
	private boolean isMyConnectorOut(TGConnectorAssociationProCSD tgc)
	{
		for (int i=0;i<nbConnectingPoint;i++)
		{
			if ( tgc.getTGConnectingPointP1().equals(this.getTGConnectingPointAtIndex(i)) )
		        return true;
		}
		return false;
	}
	

	private boolean isMyConnector(TGConnectorAssociationProCSD tgc)
	{
		for (int i=0;i<nbConnectingPoint;i++)
		{
			if ( tgc.getTGConnectingPointP1().equals(this.getTGConnectingPointAtIndex(i)) ||  tgc.getTGConnectingPointP2().equals(this.getTGConnectingPointAtIndex(i)) )
		        return true;
		}
		return false;
	}
	
	
	/*
	 * gets the connector for this port. it might be one or no connector at all  
	 * @return
	 */
	public TGConnectorProCSD getTGConnector()
	{
		
		LinkedList cmps=this.tdp.getComponentList();
		for (int i=0;i<cmps.size();i++)
		{
			TGComponent tgc = (TGComponent)cmps.get(i);
			if (tgc.getType()== TGComponentManager.CONNECTOR_PROCSD)
			{
				if (isMyConnector((TGConnectorProCSD)tgc)) return (TGConnectorProCSD)tgc;
     	}
    }
	
		return null;
  }
	
 // Method added by Solange
	/*
	 * gets the connector between the Interface and this port. it might be one or no connector at all  
	 * @return tgc the connector
	 */
	public TGConnectorAttribute getTGConnectorInterface()
	{
		LinkedList cmps=this.tdp.getComponentList();
		for (int i=0;i<cmps.size();i++)
		{
			TGComponent tgc = (TGComponent)cmps.get(i);
			if ((tgc.getType()== TGComponentManager.CONNECTOR_ATTRIBUTE)|| (tgc.getType()== TGComponentManager.CONNECTOR_PROCSD_PORT_INTERFACE))
			{
				if (isMyAttribute((TGConnectorAttribute)tgc)) return (TGConnectorAttribute)tgc;
     	}
    }
	
		return null;
  }
		
	/*
	 * 
	 * @return a connector in direction "out" if there is one, null if not
	 */
	public TGConnectorDelegateProCSD getTGConnectorDelegateOut()
	{
		LinkedList cmps=this.tdp.getComponentList();
		for (int i=0;i<cmps.size();i++)
		{
			TGComponent tgc = (TGComponent)cmps.get(i);
			if (tgc.getType()== TGComponentManager.CONNECTOR_DELEGATE_PROCSD)
			{
				if (isMyConnectorOut((TGConnectorDelegateProCSD)tgc)) return (TGConnectorDelegateProCSD)tgc;
     	}
    }
	
		return null;
  }
	

	
	/*
	 * 
	 * @return a TGConnectorProCSD in direction "in" if there is one, null if not
	 */
	public TGConnectorProCSD getBindingIn()
	{
		LinkedList cmps=this.tdp.getComponentList();
		for (int i=0;i<cmps.size();i++)
		{
			TGComponent tgc = (TGComponent)cmps.get(i);
			if (tgc instanceof TGConnectorProCSD)
			{
				if (isMyConnectorIn((TGConnectorProCSD)tgc)) return (TGConnectorProCSD)tgc;
     	}
    }
		return null;
  }
	
	/*
	 * 
	 * @return a TGConnectorProCSD in direction "out" if there is one, null if not
	 */
	public TGConnectorProCSD getBindingOut()
	{
		LinkedList cmps=this.tdp.getComponentList();
		for (int i=0;i<cmps.size();i++)
		{
			TGComponent tgc = (TGComponent)cmps.get(i);
			if (tgc instanceof TGConnectorProCSD)
			{
				if (isMyConnectorOut((TGConnectorProCSD)tgc)) return (TGConnectorProCSD)tgc;
     	}
    }
		return null;
  }
	
	/*
	 * 
	 * @return a connector in direction "in" if there is one, null if not
	 */
	public TGConnectorDelegateProCSD getTGConnectorDelegateIn()
	{
		LinkedList cmps=this.tdp.getComponentList();
		for (int i=0;i<cmps.size();i++)
		{
			TGComponent tgc = (TGComponent)cmps.get(i);
			if (tgc.getType()== TGComponentManager.CONNECTOR_DELEGATE_PROCSD)
			{
				if (isMyConnectorIn((TGConnectorDelegateProCSD)tgc)) return (TGConnectorDelegateProCSD)tgc;
     	}
    }
	
		return null;
  }
	
	
	
	public abstract String myType();
	
	
	public ProCSDDelegatePort getDelegatePort()
	{
		return null;
	}
	
	
    public LinkedList getMyMessages()
    {
    	return null;
    }

    
    //Method added by Solange
    public boolean isMandatory(ProCSDInterface it)
    {
    	if(it.manda) //If the interface IS mandatory, by Solange
    	{
    		//System.out.println("Founded mandatory interface: " + it.toString());
    		return(true);
    	}
    	else
    	{
         //System.out.println("Interface " + it.toString() + " IS NOT mandatory");
         return(false);
    	}
    }
    
    //Method added by Solange
    /** Allows to get all the messages of
	* a mandatory interface.
	* @param p port connected to the interface
	* @return msgMandatory Vector of messages
	**/
    public Vector getMsgMandatory(ProCSDPort p, ProCSDInterface it){
	Vector msgMandatory=new Vector();
	if(it==null)
		{
		 System.out.println("Interface not found for this port");
		 return(null);
		}
	LinkedList myMessages= it.getMyMessages();
	TAttribute a;
	if(isMandatory(it))
	{
	 for(int i=0; i<myMessages.size(); i++)
	 {
        a = (TAttribute)(myMessages.get(i));
		 msgMandatory.addElement(a);
	 }
	}
	return(msgMandatory);
	}
	
    //Method Added by Solange
    /*
     * Method with rules of compatibility between interfaces
     * @param p port connected to the interface
     * @param it the interface
     * @param interfacelist A LinkedList of interfaces
     */
    public int Compatibility(ProCSDPort p, ProCSDInterface it,LinkedList interfacelist)
    {
    LinkedList myMessages1;
    TGConnectorProCSD tgc;
    ProCSDPort p2;
    ProCSDInterface it2;
    LinkedList myMessages2;
    
    if(it!=null)
    {
    	myMessages1= it.getMyMessages();
    	if (isMandatory(it))
    	{
    		tgc=p.getTGConnector(); //reviso si puerto esta conectado
    		if (tgc==null)  //no conectado
    		{
    		//	System.out.println(p.toString() + " not connected");
    			return(1);
    		}
    		else
    		{
    			p2=p.getFromPort();
    			if(p2!=null)
    			{
    				it2=p2.getMyInterface(interfacelist);
    				//it2=p2.getMyInterface();
    				if(it2!=null)
    				{
    					myMessages2= it2.getMyMessages();
    					if(myMessages2.containsAll(myMessages1))
    					{
    					//	System.out.println(it2.toString() + " is compatible with mandatory " + it.toString());
    						return(0);
    					}
    					else
    					{
    				//		System.out.println("ERROR: " + it2.toString() + " is not compatible with mandatory " + it.toString());
    						return(2);
    					}
    				}
    				else
    				{
    				//	System.out.println(p2.toString() + " doesn't have an Interface connected. No compatibility with mandatory " + it.toString());
						return(2);
    				}
    			}
    			else
    			{
    			 	p2=p.getToPort();
    			 	if(p2!=null)
    			 	{
    					it2=p2.getMyInterface(interfacelist);
    					//it2=p2.getMyInterface();
    					if(it2!=null)
    					{
    						myMessages2= it2.getMyMessages();
    						if(myMessages2.containsAll(myMessages1))
    						{
    					//		System.out.println(it2.toString() + " is compatible with mandatory " + it.toString());
    							return(0);
    						}
    						else
    						{
    					//		System.out.println("ERROR: " + it2.toString() + " is not compatible with mandatory " + it.toString());
    							return(2);
    						}
    					}
    					else
    					{
    				//		System.out.println("ERROR!!!!!");
    						return(3);
    					}
    			 	}
    			 	else
    			 	{
    			 	//	System.out.println(p2.toString() + " doesn't have an Interface connected. No compatibility with mandatory " + it.toString());
						return(2);
    			 	}
    			}
    		}
    	}
    	else			//not mandatory
    	{
    //		System.out.println(it.toString() + " is OPTIONAL so can be connected or not, and is compatible with any interface");
    		return(0);
    	}
    }
    else
    {
 //   	System.out.println(p.toString() + " doesn't have an Interface connected");
		return(2);
    }
   }//end method
    
    //Method added by Solange to put this option in the right click button menu of the mouse
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
    
    	
    	componentMenu.addSeparator();
        
    	JMenuItem ji = new JMenuItem("bind java interface");
    	ji.addActionListener(this);
        componentMenu.add(ji);
   
    	
    	JMenuItem hi = new JMenuItem("hide/show interface");
        hi.addActionListener(this);
        componentMenu.add(hi);
   
        
        
        componentMenu.addSeparator();
        JMenuItem pp = new JMenuItem("pretty print");
        pp.addActionListener(this);
        componentMenu.add(pp);
   
    }
    
    
    
    public void actionPerformed(ActionEvent e)
    {
    	Class javaInt=null;
    	if (e.getActionCommand().equals("bind java interface"))
    	{
    		String currentInterface="";
    		if (javaInt!=null) currentInterface=javaInt.getName();
    		String className = (String) JOptionPane.showInputDialog(tdp, "java interface name",
					"choosing java interface", JOptionPane.PLAIN_MESSAGE,
					IconManager.imgic101, null, currentInterface);
    	
    	
		    if (className==null) return;
		    
    		try{
		    		javaInt=Class.forName(className);
		    	}
		    	catch(ClassNotFoundException ex)
		    	{
		    		JOptionPane.showMessageDialog(tdp,"Class "+className+" not found. Verify the class is in your classpath");
		    		return;
		    	}
    	

		    	
		    	if (!javaInt.isInterface())
		    	{
		    		JOptionPane.showMessageDialog(tdp,"Class "+className+" is not an interface. Please choose an interface");
		    		return;
		    	}
		    	
		    	this.javaInterface=javaInt;
		    	if (this.getMyInterface()!=null)
		    	{
		    	
		    		
		    		// setting messages for myInterface
		    		Vector<TAttribute> msgs=new Vector<TAttribute>();
		    		
		    		Method[] mthds=javaInt.getMethods();
			    	for (int k=0;k<mthds.length;k++)
			    	{
			    		Method m=mthds[k];
			    		TAttribute ta=new TAttribute(TAttribute.PUBLIC,m.getName(),"",m.getReturnType().getName());
			    		msgs.add(ta);
			    	}	
			    	
			    	myInterface.setMessages(msgs);			    
		    	}		    			    	
    	}

    	
    	if (e.getActionCommand().equals("hide/show interface"))
    	{
    		if (myInterface==null) return;
    		
    		if(myInterface.shome==true)
    			{
    			 myInterface.shome=false;
    			 myInterface.getMyConnector().show=false;
    			 hidden=true;
    			
    			}
    		else
    		{
    			myInterface.shome=true;
    			myInterface.getMyConnector().show=true;
    			hidden=false;
    		}
    	}
    	if (e.getActionCommand().equals("pretty print"))
    	{
    		System.out.println(this.prettyPrint());
    	}
    	
    }
  
    
    
    public String prettyPrint()
    {
    	String out="";
    	out+=this.toString()+"\n";
    	
    	if (this.getFromPort()!=null)
    	{
    		out+="      from port: "+getFromPort().getFather().getValue()+"."+getFromPort().getValue()+"("+getFromPort().getName()+")\n";
    		out+="      last from port: "+getFromFinalPort().getFather().getValue()+"."+getFromFinalPort().getValue()+"("+getFromFinalPort().getName()+")\n";
    		
    	}
    	
    	if (this.getToPort()!=null)
    	{
    		out+="      to port: "+getToPort().getFather().getValue()+"."+getToPort().getValue()+"("+getToPort().getName()+")\n";
    		out+="      to final port: "+getToFinalPort().getFather().getValue()+"."+getToFinalPort().getValue()+"("+getToFinalPort().getName()+")\n";
    	}
    	
    	if (myInterface!=null)
    	{
    		out+="      My interface: "+myInterface.getValue();
    		
    	}
    	
    	
    	return out;
    	
    }
    
    public void setFather(TGComponent _father)
    {
    	super.setFather(_father);
    	if ((_father!=null)&&(_father instanceof ProCSDComponent)) //a priori this should allways be true
    	 
    		if (portCode==null) portCode=((ProCSDComponent)_father).getNewPortCode();
    }
    
    
    public String toString()
    {
    	String out=super.toString();
    	out+="("+portCode+")";
    	return out;
    }
    
    
    public void myActionWhenRemoved()
    {
    	super.myActionWhenRemoved();
    	ProCSDInterface myI=this.getMyInterface();
    	if (myI!=null)
    	{
    		this.tdp.removeComponent(myI);
    	}    	    	    	    	
    }
    
    
    
    protected String translateExtraParam() {
    	  
    	StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<PortCode code=\"");
        sb.append(portCode);
        sb.append("\" />\n");
        
        sb.append("<Show value=\"");
        if (hidden) sb.append("1");
         else sb.append("0");
        sb.append("\" />\n");
        
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    

    
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
             
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                          if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("PortCode")) {
                            	 portCode = (new String (elt.getAttribute("code")));                                
                            }
                            
                            if (elt.getTagName().equals("Show")) {
	                              int m = Integer.decode(elt.getAttribute("value")).intValue();
	                               if (m==1) hidden=true;
	                               else hidden=false;
                          }
                            
                            
                            
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
     
    }
    
    
    
}
