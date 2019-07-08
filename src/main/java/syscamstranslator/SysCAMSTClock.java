/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */

package syscamstranslator;

import java.util.LinkedList;

import javax.swing.DefaultListModel;

/** 
 * Class SysCAMSTClock 
 * Parameters of a SystemC-AMS Clock
 * Creation: 08/07/2019
 * @version 1.0 08/07/2019
 * @author Daniela Genius
*/

public class SysCAMSTClock extends SysCAMSTComponent{
    private String name;
    private String frequency;
    private String unit;
    private String dutyCycle;
    private String startTime;
    private String posFirst;
	
    //private SysCAMSTCluster cluster;	
    private LinkedList<SysCAMSTPortClock> portClock;

    
    public SysCAMSTClock(String _name, String _frequency,  String _unit, String _dutyCycle, String _startTime, String _posFirst) {
     
    	name = _name;
	frequency	= _frequency;
	unit	= _unit;
	dutyCycle	= _dutyCycle;
	startTime	= _startTime;
	posFirst	= _posFirst;
	//cluster = _cluster;
	portClock = new LinkedList<SysCAMSTPortClock>();
    }


	public String getName() {
		return name;
	}

    	public String getFrequency() {
		return frequency;
	}

	public String getUnit() {
		return unit;
	}

        public String getDutyCycle() {
		return dutyCycle;
	}

    public String getStartTime() {
		return startTime;
	}
    
    
    	public String getPosFirst() {
		return posFirst;
	}

    
	public void setName(String _name) {
		name = _name;
	}
 
    	public void setFrequency(String _frequency) {
		 frequency = _frequency;
	}

	public void setUnit(String _unit) {
		unit = _unit;
	}

        public void setDutyCycle(String _dutyCycle) {
		dutyCycle = _dutyCycle;
	}


        public void setStartTime(String _startTime) {
	       startTime = _startTime;
	}
    
    
    	public void setPosFirst(String _posFirst) {
		posFirst = _posFirst;
	}

	public LinkedList<SysCAMSTPortClock> getPortClock(){
		return portClock;
	}

	public void addPortClock(SysCAMSTPortClock cl){
		portClock.add(cl);
	}

    
}
