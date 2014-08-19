/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

ludovic.apvrille AT enst.fr
andrea.enrici AT enst.fr

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
* Class TMLSDItem. An item is either a message or an action. This class is used to produce the TML code corresponding to messages
* and actions that are sorted according to the graphical version of a SD diagram.
* Creation: 18/02/2014
* @version 1.0 26/06/2014
* @author Ludovic APVRILLE, Andrea ENRICI
* @see
*/

package tmltranslator.tmlcp;;

import java.util.*;

import tmltranslator.*;
import myutil.*;

public class TMLSDItem implements Comparable<TMLSDItem>  {

	//mind the difference between TMLSDAttribute and TMLAttribute!
	private String value;
	private String receiverName;
	private String instanceName;
	private String senderName;
	private int yCoord;
	
		//for messages
    public TMLSDItem( String _value, String _senderName, String _receiverName, int _yCoord ) {
			this.value = _value;
			this.senderName = _senderName;
			this.receiverName = _receiverName;
			this.instanceName = "";
			this.yCoord = _yCoord;
    }

		//for attributes
    public TMLSDItem( String _value, String _instanceName, int _yCoord ) {
			this.value = _value;
			this.senderName = "";//_senderName;
			this.receiverName = "";//_receiverName;
			this.instanceName = _instanceName;
			this.yCoord = _yCoord;
    }

		public String getReceiverName()	{
			return this.receiverName;
		}

		public String getSenderName()	{
			return this.senderName;
		}

		public String getInstanceName()	{
			return this.instanceName;
		}

		public String getValue()	{
			return this.value;
		}

		public int getYCoord()	{
			return this.yCoord;
		}

		public void setYCoord( int _coord )	{
			this.yCoord = _coord;
		}

		public void setValue( String _value )	{
			this.value = _value;
		}
	
		public int compareTo( TMLSDItem _item )	{

			int compareValue = ((TMLSDItem) _item).getYCoord();
			//sort in ascending order
			return this.yCoord - compareValue;
		}

		/*public static Comparator<TMLSDItem> yCoordComparator = new Comparator<TMLSDItem>()	{
			public int compare( TMLSDItem _item1, TMLSDItem _item2 )	{
				int yCoord1 = _item1.getYCoord(); 
				int yCoord2 = _item2.getYCoord(); 

				//ascending order
				return yCoord1.compareTo( yCoord2 );
			}
		};*/

	@Override public String toString()	{
		return "TMLSDItem " + this.value + " " + this.yCoord;
	}
}	//End of class
