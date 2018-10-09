/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enst.fr
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




package tmltranslator.tmlcp;

import tmltranslator.TMLAttribute;
import tmltranslator.TMLElement;

import java.util.ArrayList;

/**
* Class TMLSDMessage
* Creation: 18/02/2014
* @version 1.1 15/05/2014
* @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLSDMessage extends TMLElement  {

	//mind the difference between TMLAttribute and TMLAttribute!
	private ArrayList<TMLAttribute> attributeList;	
	private String senderName = "";
	private String receiverName = "";
	private int yCoord;

  //public TMLSDMessage( String _name, /*String _senderName, String _receiverName,*/ Object _referenceObject ) {
  	/*super( _name, _referenceObject );
		this.yCoord = -1;
		this.senderName = "";//_senderName;
		this.receiverName = "";//_receiverName;
		attributeList = new ArrayList<TMLAttribute>();
	}*/
	
  //public TMLSDMessage( String _name, /*String _senderName, String _receiverName,*/ int _yCoord, Object _referenceObject ) {
  	/*super( _name, _referenceObject );
		this.senderName = "";//_senderName;
		this.receiverName = "";//_receiverName;
		this.yCoord = _yCoord;
		attributeList = new ArrayList<TMLAttribute>();
	}*/

	public TMLSDMessage( String _name, String _senderName, String _receiverName, int _yCoord,
												Object _referenceObject, ArrayList<String> _params )	{
		super( _name, _referenceObject );
		this.yCoord = _yCoord;
		this.senderName = _senderName;
		this.receiverName = _receiverName;
		attributeList = new ArrayList<TMLAttribute>();
		for( String p: _params )	{
			attributeList.add( new TMLAttribute(p) );
		}
	}
	
	// Constructor used for the TMLCPparser where in the TMLCP code there is no notion of yCoord and of referenceObject
	public TMLSDMessage( String _name, String _senderName, String _receiverName, ArrayList<String> _params )	{
		super( _name, null );
		this.yCoord = -1;
		this.senderName = _senderName;
		this.receiverName = _receiverName;
		attributeList = new ArrayList<TMLAttribute>();
		for( String p: _params )	{
			attributeList.add( new TMLAttribute(p) );
		}
	}

	public String getSenderName()	{
		return senderName;
	}

	public String getReceiverName()	{
		return receiverName;
	}
    
	public void addAttribute( TMLAttribute _attribute )	{
		if( _attribute != null )
			attributeList.add( _attribute );
	}

	public ArrayList<TMLAttribute> getAttributes()	{
		return attributeList;
	}

	public int getYCoord()	{
		return this.yCoord;
	}

	public void setYCoord( int _coord )	{
		this.yCoord = _coord;
	}
	
	@Override public String toString()	{

		String s = this.name + "(";
		if( attributeList.size() > 0 )	{
			for( TMLAttribute attribute: attributeList )	{
				s += attribute.getName() + ",";
			}
			String newS = s.substring( 0, s.length() - 1 );
			s = newS;
		}
		s += ")";
		return s;
	}
}	//End of class
