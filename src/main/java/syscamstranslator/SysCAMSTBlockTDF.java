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

import ui.syscams.SysCAMSPortConverter;
import ui.syscams.SysCAMSPortTDF;

/**
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
*/

public class SysCAMSTBlockTDF extends SysCAMSTComponent {

	private String blockTDFName;
	private int period;
	private String processCode;
	private java.util.List<SysCAMSPortTDF> tdfports;
	private java.util.List<SysCAMSPortConverter> convports;
	
	public SysCAMSTBlockTDF(String _blockTDFName, int _period, String _processCode, java.util.List<SysCAMSPortTDF> _tdfports, java.util.List<SysCAMSPortConverter> _convports) {
		blockTDFName = _blockTDFName;
		period = _period;
		processCode = _processCode;
		tdfports = _tdfports;
		convports = _convports;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int _period) {
		period = _period;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String _processCode) {
		processCode = _processCode;
	}

	public String getBlockTDFName() {
		return blockTDFName;
	}

	public void setBlockTDFName(String _blockTDFName) {
		blockTDFName = _blockTDFName;
	}

	public java.util.List<SysCAMSPortTDF> getTdfports() {
		return tdfports;
	}

	public void setTdfports(java.util.List<SysCAMSPortTDF> tdfports) {
		this.tdfports = tdfports;
	}

	public java.util.List<SysCAMSPortConverter> getConvports() {
		return convports;
	}

	public void setConvports(java.util.List<SysCAMSPortConverter> convports) {
		this.convports = convports;
	}
}