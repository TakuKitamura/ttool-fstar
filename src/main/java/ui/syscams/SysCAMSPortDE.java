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

package ui.syscams;

import java.awt.Graphics;

import ui.TDiagramPanel;
import ui.TGComponent;

/**
 * Class SysCAMSPortDE
 * Primitive port. To be used in SystemC-AMS diagrams
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
 * @version 1.1 10/06/2019
 * @author Irina Kit Yan LEE, Daniela GENIUS
*/

public class SysCAMSPortDE extends SysCAMSPrimitivePort {
//	private int period;
//	private String time;
//	private int rate;
//	private int delay;
        private int nbits;//DG
	private String DEType;
	private boolean sensitive;
	private String sensitiveMethod;
	
	public SysCAMSPortDE(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
	}

//	public int getPeriod() {
//		return period;
//	}
//
//	public void setPeriod(int period) {
//		this.period = period;
//	}
//
//	public String getTime() {
//		return time;
//	}
//
//	public void setTime(String time) {
//		this.time = time;
//	}
//
//	public int getRate() {
//		return rate;
//	}
//
//	public void setRate(int rate) {
//		this.rate = rate;
//	}
//
//	public int getDelay() {
//		return delay;
//	}
//
//	public void setDelay(int delay) {
//		this.delay = delay;
//	}

	public void drawParticularity(Graphics g) {
	}

	public String getDEType() {
		return DEType;
	}

	public void setDEType(String _DEType) {
		DEType = _DEType;
	}
	
	public boolean getSensitive() {
		return sensitive;
	}
    
	public int getNbits() {
		return nbits;
	}

        public void setNbits(int _nbits) {
		nbits = _nbits;
	}

    
	public void setSensitive(boolean _sensitive) {
		sensitive = _sensitive;
	}

	public String getSensitiveMethod() {
		return sensitiveMethod;
	}

	public void setSensitiveMethod(String _sensitiveMethod) {
		sensitiveMethod = _sensitiveMethod;
	}	
}
