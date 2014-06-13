/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

ludovic.apvrille AT telecom-paristech.fr
andrea.enrici AT telecom-paristech.fr

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
* Class CPSequenceDiagram. The class contains the da structure for the graphical2TMLtext compiler as well as for the TMLCP parser
* Creation: 18/02/2014
* @version 1.0 18/02/2014
* @author Ludovic APVRILLE, Andrea ENRICI
* @see
*/

package tmltranslator.tmlcp;;

import java.util.*;

import tmltranslator.*;
import myutil.*;

public class CPSequenceDiagram  extends TMLElement {

	//Nested classes for the data structure of the compiler graphical2TMLText
	
	public static class TMLCPGraphicalSDInstance	{
		String name;
		String type;

		public TMLCPGraphicalSDInstance( String _name, String _type )	{
			this.name = _name;
			this.type = _type;
		}

		public void setName( String _name )	{
			this.name = _name;
		}

		public void setType( String _type )	{
			this.type = _type;
		}

		public String getName()	{
			return this.name;
		}

		public String getType()	{
			return this.type;
		}

		@Override public String toString()	{
			return this.type + " " + this.name;
		}
	}	//End of class TMLCPGraphicalSDInstance


/*#############################################################################*/

	public static class GraphicalSDElement	{
		String value;
		int yCoord;

		public GraphicalSDElement( String _value, int _yCoord )	{
			value = _value;
			yCoord = _yCoord;
		}

		public void setValue( String _value )	{
			value = _value;
		}

		public void setyCoord( int _yCoord )	{
			yCoord = _yCoord;
		}

		public String getValue()	{
			return value;
		}

		public int getyCoord()	{
			return yCoord;
		}

		@Override public String toString()	{
			return value + " " + Integer.toString( yCoord );
		}

	}	//End of class GraphicalSD

/*#############################################################################*/

	public static class TMLCPGraphicalSD	{

		private String name;
		private ArrayList<TMLCPGraphicalSDInstance> TMLCPGraphicalSDInstanceList;
		private ArrayList<GraphicalSDElement> GraphicalSDElementList;

		public TMLCPGraphicalSD( String _name )	{
			this.name = _name;
			GraphicalSDElementList = new ArrayList<GraphicalSDElement>();
			TMLCPGraphicalSDInstanceList = new ArrayList<TMLCPGraphicalSDInstance>();
		}

		public void addGraphicalSDElement( String _value, int _yCoord )	{
			GraphicalSDElementList.add( new GraphicalSDElement( _value, _yCoord ) );
		}

		public ArrayList<GraphicalSDElement> getGraphicalSDElements()	{
			return GraphicalSDElementList;
		}
	
		public void addTMLCPGraphicalSDInstance( String _name, String _type )	{
			TMLCPGraphicalSDInstanceList.add( new TMLCPGraphicalSDInstance( _name, _type ) );
		}

		public ArrayList<TMLCPGraphicalSDInstance> getTMLCPGraphicalSDInstances()	{
			return TMLCPGraphicalSDInstanceList;
		}

		@Override public String toString()	{
			return "TMLCPGraphicalSD " + this.name;
		}

		public String getName()	{
			return this.name;
		}
	}	//End of class

/*#############################################################################*/

	private ArrayList<TMLSDInstanceDS> instances; 
	private ArrayList<TMLSDInstanceDS> mappingInstances;
	private ArrayList<TMLAttribute> globalVariables;
	private ArrayList<TMLSDMessage> messages; 
	
	private int hashCode;
	private boolean hashCodeComputed = false;
	
    
	public CPSequenceDiagram( String _name, Object _referenceObject )	{
		super( _name, _referenceObject );
		init();
	}

	/*public CPSequenceDiagram()	{
		super( "DefaultName", null );
		init();
	}*/

	private void init() {
		globalVariables = new ArrayList<TMLAttribute>();
		instances = new ArrayList<TMLSDInstanceDS>();
		messages = new ArrayList<TMLSDMessage>();
	}
    
 	public void addVariable( TMLAttribute _attr ) throws MultipleVariableDeclarationException	{

		if( !checkVariableUniqueness( _attr.getName() ) )	{
			String errorMessage = "TMLCOMPILER ERROR: variable " + _attr.getName() + " in diagram " + this.name + " has mutliple declarations";
			throw new MultipleVariableDeclarationException( errorMessage );
		}
		else	{
      globalVariables.add(_attr);
    }
	}
	
	public ArrayList<TMLAttribute> getAttributes() {
		return globalVariables;
	}
	
	public void addInstance( TMLSDInstanceDS _elt ) throws MultipleInstanceDeclarationException {
		
		if( declaredInstance( _elt ) )	{
			String errorMessage = "TMLCP COMPILER ERROR: instance " + _elt.getName() + " in diagram " + this.name + " declared multiple times";
			throw new MultipleInstanceDeclarationException( errorMessage );
		}
		else	{
	    instances.add( _elt );
		}
 	}

	public void addMappingInstance( TMLSDInstanceDS _elt ) {
    mappingInstances.add( _elt );
 	}
   
	public ArrayList<TMLSDInstanceDS> getInstances()	{
		return instances;
	}
	
	public ArrayList<TMLSDInstanceDS> getMappingInstances()	{
		return mappingInstances;
	}
	
	public void addMessage( TMLSDMessage _elt ) {
  	messages.add( _elt );
  }
    
	public void insertInitialValue( String _name, String value ) throws UninitializedVariableException	{
			
		int i = 0;
		String str;
		TMLAttribute tempAttr;
		TMLType tempType, _attrType;
		TMLAttribute _attr = new TMLAttribute( _name, new TMLType(1) );

		for( i = 0; i < globalVariables.size(); i++ )	{
			tempAttr = globalVariables.get(i);
			str = tempAttr.getName();
			if( str.equals( _attr.getName() ) )	{
				tempType = tempAttr.getType();
				_attrType = _attr.getType();
				if( tempType.getType() == _attrType.getType() )	{
					_attr.initialValue = value;
					globalVariables.set( i, _attr );
					return;
				}
			}
		}
		String errorMessage = "TMLCOMPILER ERROR: variable " + _name + " in diagram " + this.name + " is not initialized";
		throw new UninitializedVariableException( errorMessage );
	}

	public boolean containsInstance( String _name )	{
		
		int i, instCounter = 0;
		TMLSDInstanceDS inst;

		for( i = 0; i < instances.size(); i++ )	{
			inst = instances.get(i);
			if( _name.equals( inst.getName() ) )	{
				instCounter++;
			}
		}
		return ( instCounter != 0 );
	}

	public int isVariableInitialized( String _name )	{

		int i, countNotDecl = 0;
		TMLAttribute attr;

		for( i = 0; i < globalVariables.size(); i++ )	{
			attr = globalVariables.get(i);
			if( _name.equals( attr.getName() ) )	{
				countNotDecl++;
				if( attr.getInitialValue() == null )	{
					return 0;
				}
			}
		}
		if( countNotDecl > 1 )	{
			return 1;	//declared multiple times
		}
		else	{
			if( countNotDecl == 0 )	{
				return 2;	//not declared
			}
			else	{
				return 3;		//everything is okay
			}
		}
	}

	private boolean declaredInstance( TMLSDInstanceDS _inst )	{
		
		int i;
		String instName;
		ArrayList<TMLSDInstanceDS> list;
		TMLSDInstanceDS inst;

		list = getInstances();
		if( list.size() == 0 )	{
			return false;
		}
		else	{
			for( i = 0 ; i < list.size(); i++ )	{
				inst = list.get(i);
				instName = inst.getName();
				if( instName.equals( _inst.getName() ) )	{
					return true;
				}
			}
			return false;
		}
	}

	private boolean checkVariableUniqueness( String _name )	{
		
		int i;
		ArrayList<TMLAttribute> list;
		TMLAttribute attr;

		list = getAttributes();
		for( i = 0 ; i < list.size(); i++ )	{
			attr = list.get(i);
			if( _name.equals( attr.getName() ) )	{
				return false;
			}
		}
		return true;
	}

	public ArrayList<TMLSDMessage> getMessages()	{
		return messages;
	}

	public TMLSDInstanceDS retrieveInstance( String _name )	{
			
			ArrayList<TMLSDInstanceDS> instList;
			TMLSDInstanceDS inst = new TMLSDInstanceDS( "error", new Object() );
			int i;

			for( i = 0; i < instances.size(); i++ )	{
				inst = instances.get(i);
				if( _name.equals( inst.getName() ) )	{
					instances.remove(i);
					return inst;
				}
			}
			return inst;
	}

}	//End of class
