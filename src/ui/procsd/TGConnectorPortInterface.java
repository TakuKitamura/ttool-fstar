package ui.procsd;

import java.awt.Graphics;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ui.IconManager;
import ui.MalformedModelingException;
import ui.TAttribute;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.TGConnectingPoint;
import ui.TGConnectorAttribute;

public class TGConnectorPortInterface extends TGConnectorAttribute{

	public TGConnectorPortInterface(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
       // myImageIcon = IconManager.imgic108;
        actionOnAdd();
	
	}
	
	public int getType() {
	        return TGComponentManager.CONNECTOR_PROCSD_PORT_INTERFACE;
	    }

	
	public void doPostLoading()
	{
		actionOnAdd();
	}
	
	public boolean actionOnAdd()
	{
		//System.out.println("Added: connector from  "+p1.getFather().toString()+" to "+p2.getFather().toString() );
		
		ProCSDInterface theInterface=null;
		ProCSDPort thePort=null;
		
		
		if ( (p1.getFather() instanceof ProCSDInterface) &&( p2.getFather() instanceof ProCSDPort))
		{
		 theInterface=(ProCSDInterface)p1.getFather();
		 thePort=(ProCSDPort)p2.getFather();
		}
		else
			if ( (p2.getFather() instanceof ProCSDInterface) &&( p1.getFather() instanceof ProCSDPort))
			{
				theInterface=(ProCSDInterface)p2.getFather();
				 thePort=(ProCSDPort)p1.getFather();
			}
		
		if ( (thePort==null)||(theInterface==null) ) return false;
		
	
	//	System.out.println("Added: connector from  "+thePort.toString()+" to "+theInterface.toString() );
		thePort.setMyInterface(theInterface);
		
		theInterface.setMyConnector(this);
		
		return true;
	}

	
	public void myActionWhenRemoved()
	{
		
		ProCSDInterface theInterface=null;
		ProCSDPort thePort=null;
		if ( (p1.getFather() instanceof ProCSDInterface) &&( p2.getFather() instanceof ProCSDPort))
		{
		 theInterface=(ProCSDInterface)p1.getFather();
		 thePort=(ProCSDPort)p2.getFather();
		}
		else
			if ( (p2.getFather() instanceof ProCSDInterface) &&( p1.getFather() instanceof ProCSDPort))
			{
				theInterface=(ProCSDInterface)p2.getFather();
				 thePort=(ProCSDPort)p1.getFather();
			}
		
		if ( (thePort==null)||(theInterface==null) ) return;
		thePort.setMyInterface(null);
		theInterface.setMyConnector(null);
	}
	
	
	
	 protected String translateExtraParam() {
	     	 
			        
	        StringBuffer sb = new StringBuffer("<extraparam>\n");
         
	        sb.append("<Show value=\"");
	        if (show) sb.append("1");
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
	            int access, type;
	            String typeOther;
	            String id, valueAtt;
	            
	            for(int i=0; i<nl.getLength(); i++) {
	                n1 = nl.item(i);
	                //System.out.println(n1);
	                if (n1.getNodeType() == Node.ELEMENT_NODE) {
	                    nli = n1.getChildNodes();
	                    for(int j=0; j<nli.getLength(); j++) {
	                        n2 = nli.item(j);
	                        //System.out.println(n2);
	                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
	                            elt = (Element) n2;
	                            
	               
	                            if (elt.getTagName().equals("Show")) {
		                              int m = Integer.decode(elt.getAttribute("value")).intValue();
		                               if (m==1) show=true;
		                               else show=false;
	                            }
	                        }
	                    }
	                }
	            }
	            
	        } catch (Exception e) {
	            throw new MalformedModelingException();
	        }
	        
	    }
	
	 
 /*	 
	 public void internalDrawing(Graphics g)
	 {
		 
		 if ((p1.getFather()==null) || (p2.getFather()==null))
			 this.tdp.removeComponent(this);
		 else
			super.internalDrawing(g);
	 }
	 
	*/ 
}
