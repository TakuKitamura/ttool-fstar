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



package ui.procsd;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;

import java.awt.*;
import java.util.Vector;

public class TGConnectorPortInterface extends TGConnectorAttribute{

	public TGConnectorPortInterface(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector<Point> _listPoint) {
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
		//
		
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
		
	
	//	
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
	
	 @Override
	 public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
	        try {
	            NodeList nli;
	            Node n1, n2;
	            Element elt;
	 //           int access, type;
	   //         String typeOther;
	     //       String id, valueAtt;
	            
	            for(int i=0; i<nl.getLength(); i++) {
	                n1 = nl.item(i);
	                //
	                if (n1.getNodeType() == Node.ELEMENT_NODE) {
	                    nli = n1.getChildNodes();
	                    for(int j=0; j<nli.getLength(); j++) {
	                        n2 = nli.item(j);
	                        //
	                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
	                            elt = (Element) n2;
	                            
	               
	                            if (elt.getTagName().equals("Show")) {
		                              int m = Integer.decode(elt.getAttribute("value")).intValue();
                                    show = m == 1;
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
