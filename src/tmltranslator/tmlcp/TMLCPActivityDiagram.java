/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

ludovic.apvrille AT enst.fr
andrea.enrici AT enstr.fr

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
* Class TMLCPActivityDiagram
* Creation: 18/02/2014
* @version 1.0 21/05/2014
* @author Ludovic APVRILLE, Andrea ENRICI
* @see
*/

package tmltranslator.tmlcp;;

import java.util.*;
import tmltranslator.*;
import myutil.*;
//import compiler.tmlCPparser.myexceptions.*;

public class TMLCPActivityDiagram  extends TMLElement {
	
	private TMLCPStart start;
  private ArrayList<TMLCPElement> elements; // Including the start element
  private ArrayList<TMLAttribute> globalVariables; 
	private ArrayList<String> ads;	//a list of the activity diagrams declared in a section (for parsing of text)
	private ArrayList<String> sds;	//a list of the sequence diagrams declated in a section (for parsing of text)
	
	private int hashCode;
	private boolean hashCodeComputed = false;
	
    
	private boolean definedVariable( TMLAttribute _var )	{

		TMLAttribute var;
		int i;

		for(i = 0; i < globalVariables.size(); i++ )	{
			var = globalVariables.get(i);
			//Attention, control is done when variables have not been initialized yet: do not use TMLAttribute.equals!
			String tempName = var.getName();
			TMLType tempType = var.getType();
			if( tempName.equals( _var.getName()) && tempType.equals( _var.getType() ) )	{
				return true;
			}
		}
		return false;
	}
	
  private void init() {
  	globalVariables = new ArrayList<TMLAttribute>();
    elements = new ArrayList<TMLCPElement>();
		ads = new ArrayList<String>();
		sds = new ArrayList<String>();
  }
    
  public TMLCPActivityDiagram(String _name, Object _referenceObject) {
  	super(_name, _referenceObject);
    init();
  }

  public void addVariable( TMLAttribute _var ) throws UndeclaredVariableException	{

		if( !definedVariable( _var ) )	{
  		globalVariables.add( _var );
		}
		else	{
			String errorMessage = "TMLCP COMPILER ERROR: variable " + _var.getName() + " is defined multiple times in diagram " + this.name;
			throw new UndeclaredVariableException( errorMessage );
		}
  }

	public void addADname( String _name )	throws MultipleDiagDeclarationsException, RecursionException	{

		if( _name.equals( this.name ) )	{
			String errorMessage = "TMLCP COMPILER ERROR: detected recursion of " + _name + " in diagram " + this.name;
			throw new RecursionException( errorMessage );
		}
		else	{
			if( !containsADDiagram( _name ) )	{
				ads.add( _name );
			}
			else	{
				String errorMessage = "TMLCP COMPILER ERROR: " + _name + " diagram is declared multiple times in diagram " + this.name;
				throw new MultipleDiagDeclarationsException( errorMessage );
			}
		}
	}

	public void addSDname( String _name ) throws MultipleDiagDeclarationsException	{

		if( !containsSDDiagram( _name ) )	{
			sds.add( _name );
		}
		else	{
			String errorMessage = "TMLCP COMPILER ERROR: " + _name + " diagram is declared multiple times in diagram " + this.name;
			throw new MultipleDiagDeclarationsException( errorMessage );
		}
	}

	public boolean checkVariableNoType( TMLAttribute _attr )	{
			
		int i = 0;
		String str;
		TMLAttribute tempAttr;
		TMLType tempType, _attrType;

		for( i = 0; i < globalVariables.size(); i++ )	{
			tempAttr = globalVariables.get(i);
			str = tempAttr.getName();
			if( str.equals(_attr.getName()) )	{
				tempType = tempAttr.getType();
				_attrType = _attr.getType();
				if( tempType.getType() == _attrType.getType() )	{
					return true;
			}
			}
		}
		return false;
	}

		public boolean declaredDiagram( String _name )	{
			if( containsADDiagram( _name ) )	{
				return true;
			}
			else	{
				if( containsSDDiagram( _name ) )	{
					return true;
				}
			}
			return false;
		}

		public boolean containsSDDiagram( String _name )	{
			return sds.contains( _name );
		}

		public boolean containsADDiagram( String _name )	{
			return ads.contains( _name );
		}

		public void insertInitialValue( TMLAttribute _attr, String value ) throws UninitializedVariableException {
			
			int i = 0;
			String str;
			TMLAttribute tempAttr;
			TMLType tempType;

			for( i = 0; i < globalVariables.size(); i++ )	{
				tempAttr = globalVariables.get(i);
				str = tempAttr.getName();
				if( str.equals( _attr.getName() ) )	{
					tempType = tempAttr.getType();
					if( tempType.equals( _attr.getType() ) )	{
						_attr.initialValue = value;
						globalVariables.set( i, _attr );
						return;
					}
				}
			}
			//The variable trying to be initialized was not declared
			String errorMessage = "TMLCP COMPILER ERROR: variable " + _attr.getName() + " declared but not defined in diagram " + this.name;
			throw new UninitializedVariableException( errorMessage );
		}

	public ArrayList<TMLCPElement> getElements() {
		return elements;
	}
	
	public ArrayList<TMLAttribute> getAttributes() {
		return globalVariables;
	}

	public ArrayList<String> getADlist()	{
		return ads;
	}

	public ArrayList<String> getSDlist()	{
		return sds;
	}

	public void setStartElement(TMLCPStart _elt) {
        start = _elt;
  }
	
	public void addTMLCPElement(TMLCPElement _elt) {
        elements.add(_elt);
  }
  
	public void checkVariable( TMLAttribute _var ) throws UndefinedVariableException	{
		
		if( !definedVariable( _var ) )	{
			String errorMessage = "TMLCP COMPILER ERROR: variable " + _var.getName() + " undeclared in diagram " + this.name;
			throw new UndefinedVariableException( errorMessage );
		}
	}

	public boolean definedBoolVariable( String _name )	{

		TMLAttribute var;
		int i;

		for(i = 0; i < globalVariables.size(); i++ )	{
			var = globalVariables.get(i);
			if( var.isBool() )	{
				if( _name.equals( var.getName() ) )	{
					return true;
				}
			}
		}
		return false;
	}

	public void correctReferences( TMLCP _refTopCP )	{
		
		TMLCPElement tempElem;
		TMLCPActivityDiagram tempCP;
		TMLCPSequenceDiagram tempSD;
		String tempString;
		int i, j, k;

		ArrayList<TMLCPActivityDiagram> activityList = _refTopCP.getCPActivityDiagrams();
		for( i = 0; i < elements.size(); i++ )	{
			tempElem = elements.get(i);
			tempString = tempElem.getName();
			if( tempElem instanceof TMLCPRefAD )	{
				for( j = 0; j < activityList.size(); j++ )	{
					tempCP = activityList.get(j);
					if( tempString.equals( tempCP.getName() ) )	{
						TMLCPRefAD CPRef = new TMLCPRefAD( tempCP, tempElem.getName(), new Object() );
						elements.set( i, CPRef );
						break;
					}
				}
			}
			else	{ //A reference to a sequence diagram must be inserted instead
				ArrayList<TMLCPSequenceDiagram> sequenceList = _refTopCP.getCPSequenceDiagrams();
				for( k = 0; k < sequenceList.size(); k++ )	{
					tempSD = sequenceList.get(k);
					if( tempString.equals( tempSD.getName() ) )	{
						TMLCPRefSD SDRef = new TMLCPRefSD( tempSD, tempElem.getName(), new Object() );
						elements.set( i, SDRef );
						break;
					}
				}
			}
		}
	}
	
}	//End of class
