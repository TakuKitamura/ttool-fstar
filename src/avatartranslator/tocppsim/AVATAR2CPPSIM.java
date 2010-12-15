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
	private String header, declaration, mainFile, src;
	private ArrayList<AvatarBlockCppSim> transBlocks = new ArrayList<AvatarBlockCppSim>();
	
	private final static String DOTH = ".h";
	private final static String DOTCPP = ".cpp";
	private final static String SYSTEM_INCLUDE = "#include \"systemc.h\"";
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
	private final static String SCCR = ";\n";
	private final static String EFCR = "}\n";
	private final static String EFCR2 = "}\n\n";
	private final static String EF = "}";
	private final static String MAINFILE = "appmodel.cpp";

	public AVATAR2CPPSIM(AvatarSpecification _avspec) {
		avspec = _avspec;
	}
	
	public void saveFile(String path, String filename) throws FileException {  
		generateTaskFiles(path);
        	//FileUtils.saveFile(path + filename + ".cpp", mainFile);
        	FileUtils.saveFile(path + MAINFILE, mainFile);
		//src += filename + ".cpp";
		src += MAINFILE;
		FileUtils.saveFile(path + "Makefile.src", src);
	}
	
	public Vector getWarnings() {
		return warnings;
	}
	
	public void generateCPPSIM(boolean _debug, boolean _optimize) {
		debug = _debug;
		optimize = _optimize;
		warnings = new Vector();
		avspec.removeCompositeStates();
		transBlocks.clear();
		//REMOVE ALL RANDOM SEQUENCES? removeAllRandomSequences();
		//TraceManager.addDev("->   Spec:" + avspec.toString());
		//TraceManager.addDev("Enhancing graphical representation ...");
		generateMainFile();
		generateMakefileSrc();
	}
	
	private void generateMainFile() {
		makeHeader();
		makeDeclarations();
		mainFile = header + declaration;
		mainFile = Conversion.indentString(mainFile, 4);
	}
	
	private void generateMakefileSrc() {
		src = "SRCS = ";
		for(AvatarBlock block: avspec.getListOfBlocks()) {
			src += block.getName() + ".cpp ";
		}
	}
	
	private void makeHeader() {
		// System headers
		header = "#include <Simulator.h>" + CR;
		// Generate tasks header
		for(AvatarBlock block: avspec.getListOfBlocks()) {
			//header += "#include <" + mst.getReference() + ".h>" + CR;
			header += "#include <" + block.getName() + ".h>" + CR;
		}
		header += CR;
	}
	
	private void makeDeclarations() {
		declaration = "class ThisDesign: public Simulator{\npublic:\nThisDesign():Simulator(){\n";
		// Declaration of events
		String channelType="", size="";
		declaration += "EventQueueCallback::setSimulator(this);\n";
		declaration += "//Declaration of signals" + CR;
		for(AvatarRelation relation: avspec.getRelations()) {		
			if(relation.isAsynchronous()){
				if(relation.isBlocking()){
					channelType = "AvAsyncSignalB";
				}else{
					channelType = "AvAsyncSignal";
					//AvAsyncSignal(ID iID, std::string iName, unsigned int iSize);
				}
				size = ", " + relation.getSizeOfFIFO();
			}else{
				size="";
				channelType = "AvSyncSignal";
			}
			for(int i=0; i<relation.nbOfSignals(); i++){
				String name = relation.getSignal1(i).getName() + "2" + relation.getSignal2(i).getName();
				declaration+= channelType + "* " + name + " = new " + channelType + "(" + relation.getID() + ", \"" + name + "\"" + size + ");\n";
			}
		}
		
		declaration += CR;
		

		//Declaration of Tasks
		declaration += "//Declaration of blocks" + CR;
		for(AvatarBlock block: avspec.getListOfBlocks()) {
			//AvBlock(ID iID, std::string iName);
			AvatarBlockCppSim transblock = new AvatarBlockCppSim(block, avspec.getRelations());
			//mst.generateSystemC(debug, optimize, dependencies);
			transblock.generateCPPSIM(debug, optimize);
			transBlocks.add(transblock);
			//String signals="";
			//int nbOfSignals=0;
			declaration += block.getName() + "* block__" + block.getName() + " = new " + block.getName() + "("+ block.getID() +", \""+ block.getName() + "\"\n";
			for(AvatarRelation relation: avspec.getRelations()) {
				for(int i=0; i<relation.nbOfSignals(); i++){
					//if (relation.block1==block || relation.block2==block){
					if (AvatarBlockCppSim.isBlockHierarchyReferredToInRel(relation,block)!=0){
						declaration += ", (AvSignal*)" + relation.getSignal1(i).getName() + "2" + relation.getSignal2(i).getName() + CR;
						//nbOfSignals++;
					}
				}
			}
			declaration += ");\naddBlock(block__"+ block.getName() +")"+ SCCR;
		}
		declaration += "\n}\n};\n\n#include <main.h>\n";
  	}

	private void generateTaskFiles(String path) throws FileException {
		for(AvatarBlockCppSim block: transBlocks) {
			block.saveInFiles(path);
		}
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
