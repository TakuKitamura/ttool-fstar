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
 * Class VCDContent
 * Creation: 13/07/2009
 * @version 1.0 13/07/2009
 * @author Ludovic APVRILLE
 * @see
 */

package vcd;

import java.util.*;
import java.text.*;

import ui.*;

public class VCDContent  {
    private String timeScale = "1 ns";
	private ArrayList<VCDVariable> variables;
	private ArrayList<VCDTimeChange> changes;
    
    public VCDContent() {
       variables = new ArrayList<VCDVariable>();
	   changes = new ArrayList<VCDTimeChange>();
	   VCDVariable.reinitShortcut();
    }
	
	public void addVariable(VCDVariable _variable) {
		variables.add(_variable);
	}
	
	public void addTimeChange(VCDTimeChange _change) {
		changes.add(_change);
	}
	
	public VCDVariable getVariableByName(String _name) {
		for(VCDVariable var: variables) {
			if (var.getName().equals(_name)) {
				return var;
			}
		}
		return null;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		
		// Header
		GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
		Date date = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		String formattedDate = formatter.format(date);

		sb.append("$date " + formattedDate +  " $end\n");
		sb.append("$version TTool VCD generator " + DefaultText.getFullVersion() + " $end\n");
		sb.append("$timescale " + timeScale + "$end\n");
		sb.append("$scope module Simulation $end\n");
		
		// Variables
		for(VCDVariable v: variables) {
			sb.append(v.decToString());
		}
		
		// End definitions
		sb.append("$upscope $end\n");
		sb.append("$enddefinitions $end\n");
		
		// Time with value changes
		for(VCDTimeChange tc: changes) {
			sb.append(tc.toString());
		}
		
		// All done
		return sb.toString();
	}
	

/*$var wire 8 # data $end
$var wire 1 $ data_valid $end
$var wire 1 % en $end
$var wire 1 & rx_en $end
$var wire 1 ' tx_en $end
$var wire 1 ( empty $end
$var wire 1 ) underrun $end
$upscope $end
$enddefinitions $end
#0
b10000001 #
0$
1%
0&
1'
0(
0)
#2211
0'
#2296
b0 #
1$
#2302
0$
#2303*/

    
  
}