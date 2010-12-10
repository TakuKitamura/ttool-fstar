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
* Class AVATAR2UPPAAL
* Creation: 10/12/2010
* @version 1.1 25/05/2010
* @author Daniel Knoreck
* @see
*/

package avatartranslator.tocppsim;

import java.awt.*;
import java.util.*;

import myutil.*;
import avatartranslator.*;
import ui.CheckingError;

public class AVATAR2CPPSIM{
	
	private AvatarSpecification avspec;
	private Vector warnings;
	private boolean debug, optimize;

	public AVATAR2CPPSIM(AvatarSpecification _avspec) {
		avspec = _avspec;
	}
	
	public void saveFile(String path, String filename) throws FileException {  
		/*generateTaskFiles(path);
        	FileUtils.saveFile(path + filename + ".cpp", getFullCode());
		src += filename + ".cpp";
		FileUtils.saveFile(path + "Makefile.src", src);*/
	}
	
	/*public String getFullCode() {
		//return mainFile;
	}*/

	public Vector getWarnings() {
		return warnings;
	}
	
	public void generateCPPSIM(boolean _debug, boolean _optimize) {
		debug = _debug;
		optimize = _optimize;
		warnings = new Vector();
		avspec.removeCompositeStates();
		TraceManager.addDev("->   Spec:" + avspec.toString());
		// Deal with blocks
		translateBlocks();		
		if (_optimize) {
		}
		TraceManager.addDev("Enhancing graphical representation ...");
	}
	

	private void translateBlocks() {
		for(AvatarBlock block: avspec.getListOfBlocks()) {
			translateBlock(block);
		}
	}
	
	private void translateBlock(AvatarBlock _block) {
		
	}
		
	/*private void makeElementBehavior(AvatarBlock _block, AvatarStateMachineElement _elt) {
		AvatarAttribute aa;
		AvatarState state;
		AvatarRandom arand;
		if (_elt instanceof AvatarStartState) {
		} else if (_elt instanceof AvatarStopState) {
		} else if (_elt instanceof AvatarRandom) {
		} else if (_elt instanceof AvatarActionOnSignal) {
		} else if (_elt instanceof AvatarState) {
		} else if (_elt instanceof AvatarTransition) {
	}
		
	
	private String modifyString(String _input) {
		try {
			//_input = Conversion.replaceAllString(_input, "&&", "&amp;&amp;");
			//_input = Conversion.changeBinaryOperatorWithUnary(_input, "div", "/");
			//_input = Conversion.changeBinaryOperatorWithUnary(_input, "mod", "%");
			//_input = Conversion.replaceAllChar(_input, '<', "&lt;");
			//_input = Conversion.replaceAllChar(_input, '>', "&gt;");
			_input = Conversion.replaceAllStringNonAlphanumerical(_input, "mod", "%");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception when changing binary operator in " + _input);
		}
		//System.out.println("Modified string=" + _input);
		return _input;
	}
	
	private String convertGuard(String g) {
		if (g == null) {
			return "";
		}
		
		if (g.compareTo("null") == 0) {
			return "";
		}
		String action = Conversion.replaceAllChar(g, '[', "");
		action = Conversion.replaceAllChar(action, ']', "");
		return modifyString(action.trim());
	}*/
}
