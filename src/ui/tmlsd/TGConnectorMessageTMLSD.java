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
* Class TGConnectorMessageTMLSD
* Connector used in SD for exchanging messages between instances of TML SD
 * Creation: 17/02/2004
 * @version 1.0 17/02/2004
* @author Ludovic APVRILLE
* @see
*/

package ui.tmlsd;

//import java.awt.*;
//import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

import myutil.*;
import ui.*;
import ui.window.*;
import org.w3c.dom.*;

public abstract class TGConnectorMessageTMLSD extends TGConnector {
    protected int arrowLength = 10;
    protected int widthValue, heightValue;
    protected int nParam = 5;
    protected String[] params = new String[nParam];
    
    public TGConnectorMessageTMLSD(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "msg?";
				name = value;
        editable = true;
    }
    
    public String getMessage() {
        return value;
    }
	
	// Part before '()' section
	public String getFirstPartMessage() {
		int index0 = value.indexOf('(');
			if (index0 == -1) {
				return value;
			} else {
				return value.substring(0, index0);
			}		
	}
	
	public String getSecondPartMessage() {
		String tmp = value.trim();
		int index0 = tmp.indexOf('(');
			if (index0 == -1) {
				return "";
			} else {
				return tmp.substring(index0, tmp.length());
			}		
	}
	
	public boolean isMessageWellFormed() {
		//System.out.println("Analyzing message:" + value);
		
		int index0 = value.indexOf('(');
		String name;
		
		if (index0 == -1) {
			name = value;
		} else {
			name = value.substring(0, index0);
		}
		
		if (!TAttribute.isAValidId(name, false, false)) {
			return false;
		}
			
		if (index0 == -1) {
			return true;
		}
		
		String tmp = value.trim();
		if (!tmp.endsWith(")")) {
			return false;
		}
		
		// Check for individual parameters
		index0 = tmp.indexOf('(');
		tmp = tmp.substring(index0+1, tmp.length()-1);
		
		String[] params = tmp.split(",");
		for(int i=0; i<nParam; i++) {
			tmp = params[i].trim();
			//System.out.println("First=" + tmp);
			if (!TAttribute.isAValidId(tmp, false, false)) {
				return false;
			}
		}
		
		return true;
	}
	
    @Override public boolean editOndoubleClick( JFrame frame ) {
			
			String [] labels = new String[nParam + 1];
      String [] values = new String[nParam + 1];
      labels[0] = "Message name";
      values[0] = this.name;
      for( int i = 0; i< nParam; i++ ) {
				labels[i+1] = "Param #" + (i+1);
				if( params[i] != "" && params[i] != null )	{
        	values[i+1] = params[i];
				}
      }
         
      JDialogMultiString jdms = new JDialogMultiString( frame, "Setting message properties", nParam+1, labels, values );
      jdms.setSize( 350, 300 );
      GraphicLib.centerOnParent(jdms);
      jdms.show(); // blocked until dialog has been closed
       
      if(jdms.hasBeenSet() && (jdms.hasValidString(0))) {
				this.name = jdms.getString(0);
        for( int i = 0; i < nParam; i++ ) {
					params[i] = jdms.getString(i+1);
        }
      makeValue();
      return true;
      }
    	return false;
    }

    public void makeValue() {
			
			boolean first = true;
      value = this.name + "(";
			for( int i = 0; i < nParam; i++ ) {
				if( params[i].length() > 0 ) {
					if( !first ) {
						value += ", " + params[i];
					}
					else {
						first = false;
						value += params[i];
					}
        }
			}
			value += ")";
    }

	public ArrayList<String> getParams()	{

		ArrayList<String> toBeReturned = new ArrayList<String>();
		for( int i = 0; i < nParam; i++ )	{
			if( ( params[i] != "" ) && ( params[i] != "null") )	{
				toBeReturned.add( params[i] );
			}
		}
		return toBeReturned;
	}

	/*public int getNumberParams()	{

		int counter = 0;
		for( int i = 0; i < nParam; i++	)	{
			if( params[i] != null )	{
				counter++;
			}
		}
		return counter;
	}*/
	
    public TGComponent extraIsOnOnlyMe(int x1, int y1) {  
        //System.out.println("Extra");
        if (GraphicLib.isInRectangle(x1, y1, ((p1.getX() + p2.getX()) / 2)-widthValue/2, ((p1.getY() + p2.getY()) / 2) - 5 - heightValue, widthValue, heightValue)) {
            return this;
        }
        return null;
    }

	 protected String translateExtraParam() {
        String a;
        StringBuffer sb = new StringBuffer( "<extraparam>\n" );
        for( int i = 0; i < nParam; i++ )	{
					//TraceManager.addDev("Attribute:" + i);
					if( params[i] != "" )	{
    	      a = params[i];
  	        //TraceManager.addDev("Attribute:" + i + " = " + a.getId());
	          //value = value + a + "\n";
        	  sb.append( "<Parameter" );
      	    //sb.append( /*a.getAccess()*/ );
    	      sb.append( " id=\"" );
  	        sb.append( a /*a.getAccess()*/ );
	          //sb.append( "\" value=\"" );
          	//sb.append( /*a.getInitialValue()*/ );
        	  //sb.append( "\" type=\"" );
      	    //sb.append( /*a.getType()*/ );
    	      //sb.append( "\" typeOther=\"" );
  	        //sb.append( /*a.getTypeOther()*/ );
	          sb.append( "\" />\n" );
					}
        }
        sb.append( "</extraparam>\n" );
        return new String(sb);
    }

    public void loadExtraParam( NodeList nl, int decX, int decY, int decId ) throws MalformedModelingException{
    	//System.out.println("*** load extra synchro ***");
      try {
          NodeList nli;
          Node n1, n2;
          Element elt;
          int access, type, counter = 0;
          String typeOther;
          String id, valueAtt;
          
          for( int i = 0; i < nl.getLength(); i++ ) {
              n1 = nl.item(i);
              //System.out.println(n1);
              if( n1.getNodeType() == Node.ELEMENT_NODE ) {
							nli = n1.getChildNodes();
							for( int j = 0; i < nli.getLength(); i++ ) {
								n2 = nli.item(i);
								//System.out.println(n2);
								if( n2.getNodeType() == Node.ELEMENT_NODE ) {
									elt = (Element) n2;
									TraceManager.addDev( "I am analyzing " + elt.getTagName() );	
									if( elt.getTagName().equals("Parameter") )	{
										TraceManager.addDev("Analyzing parameter");
										/*access = Integer.decode(elt.getAttribute("access")).intValue();
										type = Integer.decode(elt.getAttribute("type")).intValue();
										try {
											typeOther = elt.getAttribute("typeOther");
										}
										catch ( Exception e )	{
											typeOther = "";
										}*/
										id = elt.getAttribute("id");
										/*valueAtt = elt.getAttribute("value");
										if( valueAtt.equals("null") )	{
											valueAtt = "";
										}*/
										if( (TAttribute.isAValidId(id, false, false) ) /*&& ( TAttribute.isAValidInitialValue(type, valueAtt))*/ )	{
											//TraceManager.addDev("Adding parameter " + id + " typeOther=" + typeOther);
											//TAttribute ta = new TAttribute(access, id, valueAtt, type, typeOther);
											//myAttributes.addElement(ta);
											params[counter] = id;
											counter++;
											}
                    }
                  }
                }
              }
						}
				}
				catch ( Exception e ) {
					throw new MalformedModelingException();
				}
    }
}
