/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
*
* /**
* Class ProVerifOutputAnalyzer
* Creation: 16/09/2010
* @version 1.0 16/09/2010
* @author Ludovic APVRILLE
* @see
*/

package proverifspec;

import java.util.*;

import myutil.*;
import java.io.*;


public class ProVerifOutputAnalyzer {
	
	
	private LinkedList<String> reachableEvents;
	private LinkedList<String> nonReachableEvents;
	private LinkedList<String> secretTerms;
	private LinkedList<String> nonSecretTerms;
	private LinkedList<String> errors;
	
	
	
    public ProVerifOutputAnalyzer() {
		reachableEvents = new LinkedList<String>();
		nonReachableEvents = new LinkedList<String>();
		secretTerms = new LinkedList<String>();
		nonSecretTerms = new LinkedList<String>();
		
		errors = new LinkedList<String>();
    }
	
	public void analyzeOutput(String _s) {
		String str, previous="";
		int index0, index1;
        
		BufferedReader reader = new BufferedReader(new StringReader(_s));
        
		try {
			while ((str = reader.readLine()) != null) {
                index0 = str.indexOf("RESULT not ev:");
				index1 = str.indexOf("() is false");
				if ((index0 < index1) && (index0 != -1) && (index1 != -1)) {
					reachableEvents.add(str.substring(index0+14, index1));
				}
				index0 = str.indexOf("RESULT not ev:");
				index1 = str.indexOf("() is true");
				if ((index0 < index1) && (index0 != -1) && (index1 != -1)) {
					nonReachableEvents.add(str.substring(index0+14, index1));
				}
				index0 = str.indexOf("RESULT not attacker:");
				index1 = str.indexOf("[] is true");
				if ((index0 < index1) && (index0 != -1) && (index1 != -1)) {
					secretTerms.add(str.substring(index0+20, index1));
				}
				index0 = str.indexOf("RESULT not attacker:");
				index1 = str.indexOf("[] is false");
				if ((index0 < index1) && (index0 != -1) && (index1 != -1)) {
					nonSecretTerms.add(str.substring(index0+20, index1));
				}
				
				index0 = str.indexOf("Error:");
				if (index0 != -1) {
					errors.add(str + ": " + previous);
				}
				
				previous = str;
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
    public LinkedList<String> getReachableEvents() {
		return reachableEvents;
	}
	
	public LinkedList<String> getNonReachableEvents() {
		return nonReachableEvents;
	}
	
	public LinkedList<String> getSecretTerms() {
		return secretTerms;
	}
	
	public LinkedList<String> getNonSecretTerms() {
		return nonSecretTerms;
	}
	
	public LinkedList<String> getErrors() {
		return errors;
	}
	
}