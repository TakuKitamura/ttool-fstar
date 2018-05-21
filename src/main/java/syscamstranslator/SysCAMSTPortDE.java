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

/**
 * Creation: 07/05/2018
 * @version 1.0 07/05/2018
 * @author Irina Kit Yan LEE
*/

public class SysCAMSTPortDE extends SysCAMSTComponent {

	private String name;
	private int period;
	private int rate;
	private int delay;
	private int origin;
	private String DEType;
	
	private SysCAMSTBlockDE blockDE;
	
	public SysCAMSTPortDE(String _name, int _period, int _rate, int _delay, int _origin, String _DEType, SysCAMSTBlockDE _blockDE) {
		name = _name;
		period = _period;
		rate = _rate;
		delay = _delay;
		origin = _origin;
		DEType = _DEType;
		blockDE = _blockDE;
	}

	public String getName() {
		return name;
	}

	public void setName(String _name) {
		name = _name;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int _period) {
		period = _period;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int _rate) {
		rate = _rate;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int _delay) {
		delay = _delay;
	}

	public int getOrigin() {
		return origin;
	}

	public void setOrigin(int _origin) {
		origin = _origin;
	}

	public String getDEType() {
		return DEType;
	}

	public void setDEType(String _DEType) {
		DEType = _DEType;
	}

	public SysCAMSTBlockDE getBlockTDF() {
		return blockDE;
	}

	public void setBlockDE(SysCAMSTBlockDE _blockDE) {
		blockDE = _blockDE;
	}
}
