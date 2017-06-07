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
* Class NCEquipment
* Creation: 14/11/2008
* @version 1.0 14/11/2008
* @author Ludovic APVRILLE
* @see
*/

package nc;


public class NCEquipment extends NCLinkedElement  {
	
	// Scheduling policies
	public static String FCFS = "FCFS"; // FIFO
	public static String SP = "SP"; // Static priority
	public static String WFQ = "WFQ"; // Weight Fair Queuing
	public static String[] SchedulingPolicies = {FCFS, SP, WFQ};
	
	// Types
	public static String STANDARD = "Standard"; // FIFO
	public static String[] Types = {STANDARD};
	
	private int schedulingPolicy = 0;
	private int type = 0;
	
	public NCEquipment() {
	}
	
	public void setSchedulingPolicy(int _sp) {
		schedulingPolicy = _sp;
	}
	
	public int getSchedulingPolicy() {
		return schedulingPolicy;
	}
	
	public static String getStringSchedulingPolicy(int sp) {
			return SchedulingPolicies[sp];
	}
	
	public static int getFromStringSchedulingPolicy(String _sp) {
		for(int i=0; i<SchedulingPolicies.length; i++) {
			if (SchedulingPolicies[i].compareTo(_sp) == 0) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void setType(int _type) {
		type = _type;
	}
	
	public int getType() {
		return type;
	}
	
	public static String getStringType(int _type) {
		//System.out.println("type=" + _type);
		return Types[_type];
	}
	
	public static int getFromStringType(String _type) {
		for(int i=0; i<Types.length; i++) {
			if (Types[i].compareTo(_type) == 0) {
				return i;
			}
		}
		
		return -1;
	}
	
}