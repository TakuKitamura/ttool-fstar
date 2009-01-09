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
* Class NCTraffic
* Creation: 14/11/2008
* @version 1.0 14/11/2008
* @author Ludovic APVRILLE
* @see
*/

package nc;



public class NCTraffic extends NCElement  {
	protected int periodicType = 0; // 0: periodic ; 1: aperiodic
	protected int deadline = 10;
	protected NCTimeUnit deadlineUnit;
	protected int minPacketSize = 20;
	protected int maxPacketSize = 40;
	protected int priority = 0; // 0 to 3
	
	public NCTraffic() {}
	
	public void setPeriodicType(int _periodicType) {
		periodicType = _periodicType;
		deadlineUnit = new NCTimeUnit();
	}
	
	public void setDeadline(int _deadline) {
		deadline = _deadline;
	}
	
	public void setDeadlineUnit(NCTimeUnit _deadlineUnit) {
		deadlineUnit = _deadlineUnit;
	}
	
	public void setMaxPacketSize(int _maxPacketSize) {
		maxPacketSize = _maxPacketSize;
	}
	
	public void setMinPacketSize(int _minPacketSize) {
		minPacketSize = _minPacketSize;
	}
	
	public void setPriority(int _priority) {
		priority = _priority;
	}
	
	 public int getPeriodicType() {
        return periodicType;
    }
	
	public static String getStringPeriodicType(int periodicType) {
		if (periodicType == 0) {
			return "periodic";
		} else {
			return "aperiodic";
		}
	}
	
	public int getDeadline() {
        return deadline;
    }
	
	public NCTimeUnit getDeadlineUnit() {
		return deadlineUnit;
	}
	
	public int getMinPacketSize() {
        return minPacketSize;
    }
	
	public int getMaxPacketSize() {
        return maxPacketSize;
    }
	
	public int getPriority() {
        return priority;
    }
	
	public NCTraffic cloneTraffic() {
		NCTraffic traffic = new NCTraffic();
		NCTimeUnit unit = new NCTimeUnit();
		unit.setUnit(deadlineUnit.getStringUnit());
		traffic.setPeriodicType(periodicType);
		traffic.setDeadline(deadline);
		traffic.setMinPacketSize(minPacketSize);
		traffic.setMaxPacketSize(maxPacketSize);
		traffic.setPriority(priority);
		traffic.setDeadlineUnit(unit);
		//System.out.println("Traffic unit=" + traffic.getDeadlineUnit().getStringUnit());
		return traffic;
	}
}