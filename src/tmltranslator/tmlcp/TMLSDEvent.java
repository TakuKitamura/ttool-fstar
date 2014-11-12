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
* Class TMLSDEvent. An event is either a message or an action. This class is used to produce the TML code corresponding to messages
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

public class TMLSDEvent implements Comparable<TMLSDEvent>  {

	private final static String MESSAGE_EVENT = "message";
	private final static String ACTION_EVENT = "action";
	private final static String ERROR = "ERROR IN EVENT";
	private String type;
	private int yCoord;
	private Object ref;
	
    public TMLSDEvent( Object _referenceObject, String _type, int _yCoord ) {
			this.ref = _referenceObject;
			this.yCoord = _yCoord;
			this.type = _type;
    }

		public int getYCoord()	{
			return yCoord;
		}

		@Override public int compareTo( TMLSDEvent _event )	{

			int compareValue = ((TMLSDEvent) _event).getYCoord();
			return this.yCoord - compareValue;	//sort in ascending order
		}

		/*public static Comparator<TMLSDEvent> yCoordComparator = new Comparator<TMLSDEvent>()	{
			public int compare( TMLSDEvent _item1, TMLSDEvent _item2 )	{
				int yCoord1 = _item1.getYCoord(); 
				int yCoord2 = _item2.getYCoord(); 

				//ascending order
				return yCoord1.compareTo( yCoord2 );
			}
		};*/

	@Override public String toString()	{
		if( type.equals( MESSAGE_EVENT ) )	{
			TMLSDMessage msg = ( (TMLSDMessage) ref);
			return msg.toString();
		}
		if( type.equals( ACTION_EVENT ) )	{
			TMLSDAction action = ( (TMLSDAction) ref);
			return action.toString();
		}
		return ERROR;
	}
}	//End of class
