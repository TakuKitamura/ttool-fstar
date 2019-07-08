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

import elntranslator.*;

/**
 * Class SysCAMSTPortClock
 * Parameters of a SystemC-AMS port Clock
 * Creation: 07/05/2018
 * @version 1.0 07/05/2018
 * @author Irina Kit Yan LEE
*/

public class SysCAMSTPortClock extends SysCAMSTComponent {

	private String name;
//	private int period;
//	private String time;
//	private int rate;
//	private int delay;
	private int origin;
	private String ClockType;
	private boolean sensitive;
	private String sensitiveMethod;
	
	private SysCAMSTBlockClock blockClock;
	private SysCAMSTBlockGPIO2VCI blockGPIO2VCI;
	private ELNTCluster cluster;
	private ELNTModule module;
	
	public SysCAMSTPortClock(String _name, int _origin, String _ClockType, boolean _sensitive, String _sensitiveMethod, SysCAMSTBlockClock _blockClock) {
		name = _name;
//		period = _period;
//		time = _time;
//		rate = _rate;
//		delay = _delay;
		origin = _origin;
		ClockType = _ClockType;
		sensitive = _sensitive;
		sensitiveMethod = _sensitiveMethod;
		blockClock = _blockClock;
	}
	
	public SysCAMSTPortClock(String _name, int _origin, String _ClockType, boolean _sensitive, String _sensitiveMethod, SysCAMSTBlockGPIO2VCI _blockGPIO2VCI) {
		name = _name;
//		period = _period;
//		time = _time;
//		rate = _rate;
//		delay = _delay;
		origin = _origin;
		ClockType = _ClockType;
		sensitive = _sensitive;
		sensitiveMethod = _sensitiveMethod;
		blockGPIO2VCI = _blockGPIO2VCI;
	}
	
	public SysCAMSTPortClock(String _name, int _origin, String _ClockType, boolean _sensitive, String _sensitiveMethod, ELNTCluster _cluster) {
		name = _name;
//		period = _period;
//		time = _time;
//		rate = _rate;
//		delay = _delay;ELNTCluster
		origin = _origin;
		ClockType = _ClockType;
		sensitive = _sensitive;
		sensitiveMethod = _sensitiveMethod;
		cluster = _cluster;
	}

	public SysCAMSTPortClock(String _name, int _origin, String _ClockType, boolean _sensitive, String _sensitiveMethod, ELNTModule _module) {
		name = _name;
//		period = _period;
//		time = _time;
//		rate = _rate;
//		delay = _delay;ELNTCluster
		origin = _origin;
		ClockType = _ClockType;
		sensitive = _sensitive;
		sensitiveMethod = _sensitiveMethod;
		module = _module;
	}
	
	public String getName() {
		return name;
	}

//	public int getPeriod() {
//		return period;
//	}
//
//	public String getTime() {
//		return time;
//	}
//
//	public int getRate() {
//		return rate;
//	}
//
//	public int getDelay() {
//		return delay;
//	}

	public int getOrigin() {
		return origin;
	}

	public String getClockType() {
		return ClockType;
	}

	public boolean getSensitive() {
		return sensitive;
	}

	public String getSensitiveMethod() {
		return sensitiveMethod;
	}

	public SysCAMSTBlockClock getBlockClock() {
		return blockClock;
	}
	
	public SysCAMSTBlockGPIO2VCI getBlockGPIO2VCI() {
		return blockGPIO2VCI;
	}

	public ELNTCluster getCluster() {
		return cluster;
	}

	public ELNTModule getModule() {
		return module;
	}
}
