/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to almalow the analysis of formal validation traces
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
 * Class GTURTLEModeling
 * Creation: 09/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
//import java.nio.charset.*;
//import java.nio.*;

import tmatrix.*;
import translator.*;
//import translator.tojava.*;
import translator.totpn.*;
import translator.touppaal.*;
import launcher.*;
import myutil.*;
import nc.*;
import ui.ad.*;
import ui.cd.*;
import ui.dd.*;
import ui.iod.*;
import ui.req.*;
import ui.sd.*;
import ui.ucd.*;
import ui.tree.*;
import ui.window.*;

import ui.tmlad.*;
import ui.tmlcd.*;
import ui.tmlcompd.*;
import ui.tmldd.*;
import tmltranslator.*;
import tmltranslator.tosystemc.*;
import tmltranslator.toturtle.*;
import tmltranslator.toautomata.*;
import tmltranslator.touppaal.*;

import ui.oscd.*;
import ui.osad.*;

import ui.procsd.*;
import ui.prosmd.*;

import ui.ncdd.*;

import ui.graph.*;

import ddtranslator.*;
import sddescription.*;
import sdtranslator.*;

import uppaaldesc.*;

public class GTURTLEModeling {

	//Added by Solange
	
	public GProactiveDesign gpdtemp;


	//
	private Vector panels; /* analysis, design, deployment, tml design */
	private TURTLEModeling tm;
	private int tmState; // 0:generated, 1: to be generated from mapping
	private TMLModeling tmlm;
	private TMLMapping artificialtmap;
	private TMLMapping tmap;
	private RequirementModeling rm;
	private NCStructure ncs;
	private MainGUI mgui;
	private CorrespondanceTGElement listE;
	private String rtlotos;
	
	private UPPAALSpec uppaal;
	private RelationTIFUPPAAL uppaalTIFTable;
	private RelationTMLUPPAAL uppaalTMLTable;
	
	private String tpn;
	private String sim;
	private String dta;
	private String dtadot;
	private String rg;
	private String rgdot;
	private String rgaut;
	private String rgautdot;
	private String rgautproj;
	private String rgautprojdot;
	private String tlsa;
	private String tlsadot;

	private int nbRTLOTOS;
	private int nbSuggestedDesign;
	private int nbTPN;

	private ValidationDataTree vdt;

	private Vector checkingErrors;
	private Vector warnings;
	
	ArrayList<TGConnectorInfo> pendingConnectors;

	private Vector savedOperations;
	private Vector savedPanels;
	private int nbMaxSavedOperations = 10;
	private int pointerOperation;

	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	private Document docCopy;

	private int decX, decY, decId;

	private static int graphId = 0;

	private int languageID;
	public final static int RT_LOTOS = 0;
	public final static int LOTOS = 1;
	public final static int AUT = 2;
	public final static int TPN = 3;
	public final static int MATRIX = 4;
	public final static int UPPAAL = 5;


	//private Charset chset1, chset2;

	public GTURTLEModeling(MainGUI _mgui, Vector _panels) {
		mgui = _mgui;
		panels = _panels;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			dbf = null;
			db = null;
		}
		savedOperations = new Vector();
		savedPanels = new Vector();
		pointerOperation = -1;

		vdt = new ValidationDataTree(mgui);

		/*if (!Charset.isSupported("UTF-8")) {
                        ErrorGUI.exit(ErrorGUI.ERROR_CHARSET);
                }

                chset1 = Charset.forName("UTF-8");*/
	}

	public int getLanguageID() {
		return languageID;
	}
	
	public boolean isRegularTM() {
		if (tm == null ){
			return false;
		}
		return tm.isARegularTIFSpec();
	}
	
	public String saveTIF() {
		if (tm == null) {
			System.out.println("NO TIF to save");
			return null;
		}
		
		TIFExchange tif = new TIFExchange();
		tif.setTURTLEModeling(tm);
		String ret = tif.saveInXMLTIF();
		System.out.println("TIF=\n" +  ret);
		return ret;
	}
	
	public boolean openTIF(String s) {
		TIFExchange tif = new TIFExchange();
		boolean ret = false;
		
		try {
			ret = tif.loadFromXMLTIF(s);
			if (ret) {
				tm = tif.getTURTLEModeling();
				tmState = 0;
				System.out.println("Got TIF");
				generateDesign();
			}
		} catch (Exception e) {
			System.out.println("Exception on TIF: " + e.getMessage());
		}
		return ret;
	}
	
	public void mergeChoices(boolean nonDeterministic) {
		if (tm != null) {
			tm.mergeChoices(nonDeterministic);
		}
	}
	
	public NCStructure getNCS() {
		return ncs;
	}

	public void generateRTLOTOS(File f) {
		TURTLETranslator tt = new TURTLETranslator(tm);
		rtlotos = tt.generateRTLOTOS();
		warnings = tt.getWarnings();
		nbRTLOTOS ++;
		if (f != null) {
			saveInFile(f, rtlotos);
		}
		languageID = RT_LOTOS;
		mgui.setMode(MainGUI.RTLOTOS_OK);
	}
	
	public void generateFullLOTOS(File f) {
		reinitSIM();
        reinitDTA();
        reinitRG();
        reinitRGAUT();
        reinitRGAUTPROJDOT();
		//System.out.println("generate LOTOS");
        generateLOTOS(f);
	}

	public void generateLOTOS(File f) {
		//tm.print();
		TURTLETranslator tt = new TURTLETranslator(tm);
		rtlotos = tt.generateLOTOS(true);
		warnings = tt.getWarnings();

		nbRTLOTOS ++;
		if (f != null) {
			saveInFile(f, rtlotos);
		}
		languageID = LOTOS;
		mgui.setMode(MainGUI.RTLOTOS_OK);
	}

	public void generateTPN(File f) {
		//tm.print();
		TURTLE2TPN t2tpn = new TURTLE2TPN(tm);
		tpn = t2tpn.generateTPN(false).toString();
		warnings = t2tpn.getWarnings();

		nbTPN ++;
		if (f != null) {
			System.out.println("Saving in file: " + f);
			saveInFile(f, tpn);
		}
		languageID = TPN;

		// For debug purpose
		//System.out.println(tpn);

		mgui.setMode(MainGUI.RTLOTOS_OK);
	}

	public LinkedList generateAUT(String path) {
		TML2AUT tml2aut = new TML2AUT(tmlm);
		tml2aut.generateAutomatas(true);
		try {
			return tml2aut.saveInFiles(path);
		} catch (FileException fe) {
			return null;
		}
	}
	
	public void generateTMLTxt(String _title) {
		if (tmap == null) {
			TMLTextSpecification spec = new TMLTextSpecification(_title);
			spec.toTextFormat(tmlm);
			try {
				spec.saveFile(ConfigurationTTool.TMLCodeDirectory, "spec.tml");
			} catch (Exception e) {
				System.out.println("File could not be saved: " + e.getMessage());
			}
		} else {
			TMLMappingTextSpecification spec = new TMLMappingTextSpecification(_title);
			spec.toTextFormat(tmap);
			try {
				spec.saveFile(ConfigurationTTool.TMLCodeDirectory, "spec");
			} catch (Exception e) {
				System.out.println("Files could not be saved: " + e.getMessage());
			}
		}
	}
	
	/*public void setUPPAALData(String _uppaal, RelationTIFUPPAAL _uppaalTable) {
		uppaal = _uppaal;
		uppaalTable = _uppaalTable;
	}*/

	public boolean generateUPPAALFromTIF(String path, boolean debug, int nb, boolean choices) {
		TURTLE2UPPAAL turtle2uppaal = new TURTLE2UPPAAL(tm);
		turtle2uppaal.setChoiceDeterministic(choices);
		uppaal = turtle2uppaal.generateUPPAAL(debug, nb);
		System.out.println("Building relation table");
		uppaalTIFTable = turtle2uppaal.getRelationTIFUPPAAL();
		System.out.println("Building relation table done");
		uppaalTMLTable = null;
		
		languageID = UPPAAL;
		mgui.setMode(MainGUI.UPPAAL_OK);
		
		try {
			System.out.println("Saving specification in " + path + "\n");
			turtle2uppaal.saveInFile(path);
			System.out.println("UPPAAL specification has been generated in " + path + "\n");
			return true;
		} catch (FileException fe) {
			System.out.println("Exception: " + fe.getMessage());
			return false;
		}
	}

	public boolean generateUPPAALFromTML(String _path, boolean _debug, int _size, boolean choices) {
		TML2UPPAAL tml2uppaal = new TML2UPPAAL(tmlm);
		//tml2uppaal.setChoiceDeterministic(choices);
		tml2uppaal.setSizeInfiniteFIFO(_size);
		uppaal = tml2uppaal.generateUPPAAL(_debug);
		uppaalTMLTable = tml2uppaal.getRelationTMLUPPAAL();
		uppaalTIFTable = null;
		languageID = UPPAAL;
		mgui.setMode(MainGUI.UPPAAL_OK);
		//uppaalTable = tml2uppaal.getRelationTIFUPPAAL(_debug);
		try {
			tml2uppaal.saveInFile(_path);
			return true;
		} catch (FileException fe) {
			System.out.println("Exception: " + fe.getMessage());
			return false;
		}
	}
	
	public ArrayList<String> getUPPAALQueries() {
		//System.out.println("Searching for queries");
		TURTLEPanel tp = mgui.getCurrentTURTLEPanel();
		ArrayList<TGComponent> list = new ArrayList<TGComponent>();
		ArrayList<TClass> tclasses;
		tp.getAllCheckableTGComponent(list);
		
		ArrayList<String> listQ = new ArrayList<String>();
		
		if (uppaalTIFTable != null) {
			ArrayList<ADComponent> listAD = listE.getADComponentCorrespondance(list);
			
			//System.out.println("List size:" + listAD.size());
			
			if (listAD == null) {
				return null;
			}
			
			TClass t;
			String s;
			for(ADComponent adc:listAD) {
				if (adc != null) {
					t = tm.findTClass(adc);
					//System.out.println("Found class:" + t.getName());
					if (t!= null) {
						tclasses = new ArrayList<TClass>();
						tclasses.add(t);
						// For handling tobjects
						tm.addAllTClassesEndingWith(tclasses, "_" + t.getName());
						for(TClass tc: tclasses) {
							//System.out.println("Analyzing class:" + tc.getName());
							s = uppaalTIFTable.getRQuery(tc, adc);
							if (s != null) {
								//System.out.println("Adding query:" + s);
								listQ.add(s + "$" + adc);
							}
						}
					}
				}
			}
		} else if (uppaalTMLTable != null) {
			//System.out.println("uppaalTMLTable");
			ArrayList<TMLActivityElement> listAE = listE.getTMLActivityElementCorrespondance(list);
			
			if (listAE == null) {
				return null;
			}
			
			TMLTask task;
			String s;
			for(TMLActivityElement elt:listAE) {
				if (elt != null) {
					task = tmlm.findTMLTask(elt);
					if (task!= null) {
						s = uppaalTMLTable.getRQuery(task, elt);
						if (s != null) {
							//System.out.println("Adding query:" + s);
							listQ.add(s + "$" + elt);
						}
					}
				}
			}
			
		}
		
		return listQ;
	}

	public LinkedList generateLOTOSAUT(String path) {
		TML2AUTviaLOTOS tml2aut = new TML2AUTviaLOTOS(tmlm, tm);
		tml2aut.generateLOTOS(true);
		return tml2aut.getSpecs();
		/*try {
            return tml2aut.saveInFiles(path);
        } catch (FileException fe) {
            return null;
        }*/
	}

	public void generateSystemC() {
		String path = ConfigurationTTool.SystemCCodeDirectory;
		String list = FileUtils.deleteFiles(path, ".cpp");
		if (list.length() == 0) {
			System.out.println("No cpp files were deleted\n");
		} else {
			System.out.println("Files deleted:\n" + list + "\n");
		}

		list = FileUtils.deleteFiles(path, ".x");

		if (list.length() == 0) {
			System.out.println("No x files were deleted\n");
		} else {
			System.out.println("Files deleted:\n" + list + "\n");
		}

		TML2SystemC tml2systc = new TML2SystemC(tmlm);
		tml2systc.generateSystemC(true);
		//tml2systc.print();
		try {
			tml2systc.saveFile(path, "appmodel");
		} catch (FileException fe) {
			System.out.println("File could not be saved");
		}

	}


	public void saveSIM(File f) {
		if ((sim != null) && (f != null)) {
			saveInFile(f, sim);
		}
	}

	public void saveDTA(File f) {
		if ((dta != null) && (f != null)) {
			saveInFile(f, dta);
		}
	}

	public void saveDTADOT(File f) {
		if ((dtadot != null) && (f != null)) {
			saveInFile(f, dtadot);
		}
	}

	public void saveRG(File f) {
		if ((rg != null) && (f != null)) {
			saveInFile(f, rg);
		}
	}

	public void saveTLSA(File f) {
		if ((rg != null) && (f != null)) {
			saveInFile(f, tlsa);
		}
	}

	public void saveRGAut(File f) {
		if ((rgaut != null) && (f != null)) {
			saveInFile(f, rgaut);
		}
	}

	public void saveRGDOT(File f) {
		if ((rgdot != null) && (f != null)) {
			saveInFile(f, rgdot);
		}
	}

	public void saveTLSADOT(File f) {
		if ((rgdot != null) && (f != null)) {
			saveInFile(f, tlsadot);
		}
	}

	public void saveRGAutDOT(File f) {
		if ((rgautdot != null) && (f != null)) {
			saveInFile(f, rgautdot);
		}
	}

	public void saveRGAutProj(File f) {
		if ((rgautproj != null) && (f != null)) {
			saveInFile(f, rgautproj);
		}
	}

	public void saveRGAutProjDOT(File f) {
		if ((rgautprojdot != null) && (f != null)) {
			saveInFile(f, rgautprojdot);
		}
	}
	
	public void modifyMinimizedGraph() {
		/*AUTMappingGraph graph = new AUTMappingGraph();
		System.out.println("Building graph");
		graph.buildGraph(rgautproj);
		System.out.println("Renaming transitions");
		graph.renameTransitions();
		System.out.println("Merging transitions 23/4=" + (23/4) + "23%4="  + (23%4));
		graph.mergeWriteTransitions();
		graph.mergeReadTransitions();
		graph.removeInternalTransitions();
		System.out.println("Printing graph:\n" + graph.toAUTStringFormat());
		System.out.println("Splitting transitions");
		graph.splitTransitions();
		modifiedaut = graph.toAUTStringFormat();
		System.out.println("Printing graph:\n" + modifiedaut);
		System.out.println("Translation in DOT format");
		
		// AUT  2 dot
		String fileName = "graph";
		try {
			RshClient rshc = new RshClient(getHostAldebaran());
			int id = rshc.getId();
			fileName = FileUtils.addBeforeFileExtension(fileName, "_" + id);   
			String data = rgautproj;
			rshc.sendFileData(fileName + ".aut", data);
			String cmd1 = getPathBcgio() + " -aldebaran " + fileName + ".aut" + " -graphviz " + fileName + ".dot";
			data = processCmd(rshc, cmd1);
			data = rshc.getFileData(fileName + ".dot");
			modifiedautdot = data;
			System.out.println("All done");
		} catch (LauncherException le) {
			System.out.println("Error: conversion failed");
		}*/
	}
	
	protected String processCmd(RshClient rshc, String cmd) throws LauncherException {
        rshc.setCmd(cmd);
        String s = null;
        rshc.sendProcessRequest();
        s = rshc.getDataFromProcess();
        return s;
    }

	public void showSIM(int type) {
		if (sim != null) {
			JFrameSimulationTrace jfst = new JFrameSimulationTrace("Last simulation trace", sim, type);
			jfst.setIconImage(IconManager.img8);
			jfst.setSize(900, 600);
			GraphicLib.centerOnParent(jfst);
			jfst.setVisible(true);
		}
	}

	public String showDTA() {
		if (dta != null) {
			return runDOTTY(dtadot);
		}
		return null;
	}

	public String showRG() {
		if (rg != null) {
			return runDOTTY(rgdot);
		}
		return null;
	}

	public String showTLSA() {
		if (rg != null) {
			return runDOTTY(tlsadot);
		}
		return null;
	}

	public String showRGAut() {
		if (rgaut != null) {
			return runDOTTY(rgautdot);
		}
		return null;
	}

	public String showRGAutProj() {
		if (rgaut != null) {
			return runDOTTY(rgautprojdot);
		}
		return null;
	}

	public static String showGGraph(String ggraph) {
		if (ggraph != null) {
			return runDOTTY(ggraph);
		}
		return null;
	}

	public static String runDOTTY(String data) {
		String fileName = "graph" + graphId + ".dot";
		graphId ++;

		RemoteExecutionThread ret = new RemoteExecutionThread(ConfigurationTTool.DOTTYHost, fileName, data, ConfigurationTTool.DOTTYPath + " " + fileName);
		ret.start();

		return null;
	}

	public boolean useDynamicStructure(String data) {
		int index1 = data.indexOf("behaviour");
		if (index1 == -1) {
			return false;
		}

		data = data.substring(index1, data.length());

		return (data.indexOf("Queue_nat") != -1);

	}



	public void saveInFile(File file, String s) {
		System.out.println("Saving in file " + file.getAbsolutePath() + " size of file=" + s.length());
		//System.out.println("Length of s=" + s.length());

		int index1 = 0, index2;
		int step = 1048576;
		int length = s.length();
		//String sub;

		try {
			FileOutputStream fos = new FileOutputStream(file);
			while(index1<length) {
				index2 = Math.min(index1+step, length);
				fos.write(s.substring(index1, index2).getBytes());
				index1 += step;
			}
			fos.close();
		} catch(Exception e) {
			JOptionPane.showMessageDialog(mgui.frame, "Specification could not be saved " + e.getMessage(), "Lotos File Error", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("Specification could not be saved " + e.getMessage());
		}

		/*try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(s.getBytes());
            fos.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(mgui.frame, "Specification could not be saved " + e.getMessage(), "Lotos File Error", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Specification could not be saved " + e.getMessage());
        }*/
	}

	public String getLastRTLOTOSSpecification() {
		return 	rtlotos;
	}

	public String getLastTextualDTA() {
		return dta;
	}

	public String getLastGraphicalDTA() {
		return dtadot;
	}

	public String getLastTextualRG() {
		return rg;
	}

	public String getLastGraphicalRG() {
		return rgdot;
	}

	public String getLastTextualTLSA() {
		return tlsa;
	}

	public String getLastGraphicalTLSA() {
		return tlsadot;
	}

	public String getLastTextualRGAUT() {
		return rgaut;
	}

	public String getLastGraphicalRGAUT() {
		return rgautdot;
	}

	public String getLastTextualRGAUTProj() {
		return rgautproj;
	}

	public String getLastGraphicalRGAUTProj() {
		return rgautprojdot;
	}

	public int getNbRTLOTOS() {
		return nbRTLOTOS;
	}

	public String getLastTextualDesign() {
		if (tm == null) {
			return "";
		} else {
			return tm.printToStringBuffer().toString();
		}
	}

	public int getNbSuggestedDesign() {
		return nbSuggestedDesign;
	}

	// formal validation
	public void reinitSIM() {
		sim = null;
		mgui.setMode(MainGUI.SIM_KO);
	}

	public void reinitDTA() {
		dta = null;
		dtadot = null;
		mgui.setMode(MainGUI.DTADOT_KO);
	}

	public void reinitRG() {
		rg = null;
		rgdot = null;
		mgui.setMode(MainGUI.RGDOT_KO);
	}

	public void reinitRGAUT() {
		rgaut = null;
		rgautdot = null;
		mgui.setMode(MainGUI.RGAUTDOT_KO);
		mgui.setMode(MainGUI.RGAUT_KO);
	}

	public void reinitRGAUTPROJDOT() {
		rgautprojdot = null;
		mgui.setMode(MainGUI.RGAUTPROJDOT_KO);
	}

	public void setSIM(String data) {
		sim = data;
		mgui.setMode(MainGUI.SIM_OK);
	}

	public void setDTA(String data) {
		dta = data;
	}

	public void setDTADOT(String data) {
		dtadot = data;
		mgui.setMode(MainGUI.DTADOT_OK);
	}

	public void setRG(String data) {
		rg = data;
		mgui.setMode(MainGUI.RGDOT_OK);
	}

	public void setTLSA(String data) {
		tlsa = data;
		//mgui.setMode(MainGUI.TLSADOT_OK);
	}

	public void setTLSADOT(String data) {
		tlsadot = data;
		mgui.setMode(MainGUI.TLSADOT_OK);
	}

	public void setRGAut(String data) {
		rgaut = data;
		mgui.setMode(MainGUI.RGAUT_OK);
	}

	public String getLastRGAUT() {
		return rgaut;
	}

	public void setRGDOT(String data) {
		rgdot = data;
		mgui.setMode(MainGUI.RGDOT_OK);
	}

	public void setRGAutDOT(String data) {
		rgautdot = data;
		mgui.setMode(MainGUI.RGAUTDOT_OK);
	}

	public void setRGAUTPROJ(String data) {
		rgautproj = data;
	}

	public void setRGAUTPROJDOT(String data) {
		rgautprojdot = data;
		mgui.setMode(MainGUI.RGAUTPROJDOT_OK);
	}

	// Configuration

	public String getPathRTL() {
		return ConfigurationTTool.RTLPath;
	}

	public String getPathCaesar() {
		return ConfigurationTTool.CaesarPath;
	}

	public String getPathCaesarOpen() {
		return ConfigurationTTool.CaesarOpenPath;
	}

	public String getPathDTA2DOT() {
		return ConfigurationTTool.DTA2DOTPath;
	}

	public String getPathRGSTRAP() {
		return ConfigurationTTool.RGSTRAPPath;
	}

	public String getPathRG2TLSA() {
		return ConfigurationTTool.RG2TLSAPath;
	}

	public String getHost() {
		return ConfigurationTTool.RTLHost;
	}

	public static String getCaesarHost() {
		return ConfigurationTTool.AldebaranHost;
	}

	public static String getHostAldebaran() {
		return ConfigurationTTool.AldebaranHost;
	}

	public static String getPathAldebaran() {
		return ConfigurationTTool.AldebaranPath;
	}

	public static String getPathBcgio() {
		return ConfigurationTTool.BcgioPath;
	}
	
	public static String getPathBisimulator() {
		return ConfigurationTTool.BisimulatorPath;
	}

	public String getPathBcgmerge() {
		return ConfigurationTTool.BcgmergePath;
	}

	public String getPathBcgmin() {
		return ConfigurationTTool.BcgminPath;
	}
	
	public String getPathVerifyta() {
		return ConfigurationTTool.UPPAALVerifierPath;
	}
	
	public String getPathUPPAALVerifier() {
		return ConfigurationTTool.UPPAALVerifierPath;
	}
	
	public String getPathUPPAALFile() {
		return ConfigurationTTool.UPPAALCodeDirectory;
	}
	
	public String getUPPAALVerifierHost() {
		return ConfigurationTTool.UPPAALVerifierHost;
	}
	
	

	public TURTLEModeling getTURTLEModeling() {
		return tm;
	}
	
	public int getTURTLEModelingState() {
		return tmState;
	}

	public TMLModeling getTMLModeling() {
		return tmlm;
	}
	
	public TMLMapping getArtificialTMLMapping() {
		return artificialtmap;
	}
	
	public TMLMapping getTMLMapping() {
		return tmap;
	}
	
	public UPPAALSpec getLastUPPAALSpecification() {
		return uppaal;
	}

	// TREE MANAGEMENT

	public String toString() {
		return mgui.getTitle();
	}

	public int getChildCount() {
		return panels.size() + 1;
	}

	public Object getChild(int index) {
		if (index < panels.size()) {
			return panels.elementAt(index);
		} else {
			return vdt;
		}

	}

	public int getIndexOfChild(Object child) {
		int index = panels.indexOf(child);

		if (index > -1) {
			return index;
		}

		return panels.size();
	}

	// Projection management

	public MasterGateManager getNewMasterGateManager() {
		return new MasterGateManager(tm);
	}

	// Assume the inputData is in AUT format: generated by RTL or CADP
	public String performProjection(String inputData, Vector gates) {
		StringBuffer result = new StringBuffer("");
		StringReader sr = new StringReader(inputData);
		BufferedReader br = new BufferedReader(sr);
		String s;
		String actionName, actionName1;
		int index, index1, index2;
		MasterGateManager mgm = new MasterGateManager(tm, 1);
		Gate g;
		GroupOfGates gog;
		Hashtable <String, GroupOfGates> hashtable = new Hashtable<String, GroupOfGates>();

		int cpt = 0;

		//System.out.println("input data=" + inputData);

		// Fill Hashtable
		TClassAndGateDS tag;
		int j;
		for(int i=0; i<gates.size(); i++) {
			tag = (TClassAndGateDS)(gates.get(i));
			//System.out.println("TClass:" + tag.getTClassName() + " Gate:" + tag.getGateName());
			//actionName = tag.getGateName();
			//g = mgm.getGate(tag.getTClassName(), actionName);
			//System.out.println("actionName = " + actionName + " gateName = " + g.getName()); 
			//if (g != null) {
				//gog = mgm.getGroupOfGatesByGate(g);
				gog = mgm.groupOf(tag.getTClassName(), tag.getGateName());
				if (gog != null) {
					//System.out.println("Found a gog: >" + gog.getMasterGateName() + "<");
					hashtable.put(gog.getMasterGateName().getName(), gog);
					/*for(j=0;j<gog.size();j++) {
						g = gog.getGateAt(j);
						System.out.println("Putting: " + g.getName());
						hashtable.put(g.getName(), g);
					}*/
				}
			//}
		}

		try {
			while((s = br.readLine()) != null) {
				/*if (cpt % 10000 == 0) {
                 System.out.println("cpt=" + cpt);
              }*/
              cpt ++;

              if (s.startsWith("des")) {
            	  result.append(s + "\n");
              } else if (s.startsWith("(")) {
            	  index = s.indexOf("\"t\"");
            	  if (index > 0) {
            		  // temporal action
            		  // replace t with i
            		  s = s.replaceAll("\"t\"", "i");
            		  result.append(s + "\n");
            	  } else {
            		  //exit action?
            		  index = s.indexOf("\"exit\"");
            		  if (index > 0) {
            			  // exit action
            			  // replace t with i
            			  s = s.replaceAll("\"exit\"", "i");
            			  result.append(s + "\n");
            		  } else {
            			  // regular action
            			  // find the name of this action
            			  index1 = s.indexOf("i(");
            			  index2 = s.indexOf(")");
            			  actionName = s.substring(index1 + 2, index2);
            			  index = actionName.indexOf("<");
            			  if (index < 0) {
            				  actionName1 = actionName;
            			  } else {
            				  actionName1 = actionName.substring(0, index);
            			  }
            			  System.out.println("Action = >" + actionName1 + "<");

						  gog = hashtable.get(actionName1);
            			  if (gog == null) {
							  System.out.println("Not in hash");
            				  result.append(makeIAction(s) + "\n");
            			  } else {
							  System.out.println("In hash");
            				  result.append(makeAction(s, actionName) + "\n");
            			  }

            			  // action to ignored or to project ?
            			  /*g = mgm.getGate(actionName1);
                            if (g == null) {
                                //System.out.println("null1");
                                result.append(makeIAction(s) + "\n");
                            } else {
                                gog = mgm.getGroupOfGatesByGate(g);
                                if (gog == null) {
                                    //System.out.println("null2");
                                    result.append(makeIAction(s) + "\n");
                                } else {
                                    if (!belongTo(gog, gates)) {
                                        // Check if directly a master Gate!
                                        // A completer ...
                                        //System.out.println("null3");
                                        result.append(makeIAction(s) + "\n");
                                    } else {
                                        //System.out.println("action added: " + actionName);
                                        result.append(makeAction(s, actionName) + "\n");
                                    }
                                }
                            }*/
            		  }

            	  }
              }
			}
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
			return null;
		}
		return new String(result);
	}

	// Assume the inputData is in AUT format and has been generated by CADP
	// Note: might not work because of case sensitive problem...
	public String convertCADP_AUT_to_RTL_AUT(String inputData, int max) {
		StringBuffer result = new StringBuffer("");
		StringReader sr = new StringReader(inputData);
		BufferedReader br = new BufferedReader(sr);
		String s, s1;
		String actionName;
		int index1, index2, index3, index4;
		Gate g;
		String g0, g1, g2;
		int cpt, transi=0;
		MasterGateManager mgm = new MasterGateManager(tm, 1);
		Hashtable ht = mgm.getGatesUpperCaseHashTable();
		warnings = new Vector();

		//System.out.println("input data=" + inputData);

		int cpt1 = 0;

		try {
			while((s = br.readLine()) != null) {
				cpt1 ++;
				//if (cpt1 % 100000 == 0) {
				//System.out.println("=" + cpt1 + " / " + transi);
				//}
				if (s.charAt(0) == '(') {
					index1 = s.indexOf(",");
					if ((index1 > -1) && ((index1+1) < s.length())) {
						g1 = s.substring(0, index1 + 1);
						s = s.substring(index1+1, s.length());

						//System.out.println("g1=" + g1 + " s=" + s);

						index2 = s.indexOf(",");
						if ((index2 > -1) && ((index2+1) < s.length())) {
							g2 = s.substring(index2, s.length());
							s = s.substring(0, index2);
							s = s.trim();

							//System.out.println("g2=" + g2 + " s=" + s);

							// Get action id
							// Most common case: no data
							index3 = s.indexOf('"');
							if (index3 == -1) { // no data
								actionName = s;
								g0 = "";
							} else {
								// Extract action name
								actionName = s.substring(index3+1, s.indexOf('!')).trim();

								// Format data
								g0 = "<";
								cpt = 0;
								while((index4 = s.indexOf('!')) > -1) {
									s = s.substring(index4+1, s.length());
									if (cpt > 0) {
										g0 += ",";
									}
									cpt ++;
									index4 = s.indexOf('!');
									if (index4 > -1) {
										g0 += s.substring(0, index4);
									} else {
										g0 += s.substring(0, s.indexOf('"')).trim();
									}
								}
								g0 += ">";
							}

							// Working on action name!
							//g = mgm.getGateLowerCase(actionName);
							g = (Gate)(ht.get(actionName));

							if (g != null) {
								//actionName1 = actionName;
								actionName = g.getName();
								//System.out.println("actionName = " + g.getName());
								/*if (mgm.nbOfPossibleGatesLowerCase(actionName1) > 1) {
                                    CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Action " + actionName1 + " has several possible candidates ; " + actionName + " has been chosen");
                                    warnings.add(ce);
                                }*/
							} else {
								System.out.println("actionName is not in hashtable: ->" + actionName + "<- length=" + actionName.length());
							}

							// Store result
							result.append(g1 + "\"i(" + actionName + g0 + ")\"" + g2 + "\n");
						}
					}
				} else if (s.startsWith("des")) {
					index1 = s.indexOf(",");
					s1 = s.substring(index1+1, s.length());
					index1 = s1.indexOf(",");
					s1 = s1.substring(0, index1).trim();
					//System.out.println("nb of transitions=" + s);
					transi = Integer.decode(s1).intValue();
					if (transi > max) {
						return null;
					}
					result.append(s + "\n");
				}
			}
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
			return null;
		}
		return new String(result);
	}

	/*public String convertCADP_AUT_to_RTL_AUT(String inputData, int max) {
        StringBuffer result = new StringBuffer("");
        StringReader sr = new StringReader(inputData);
        BufferedReader br = new BufferedReader(sr);
        String s;
        String actionName, actionName1;
        int index, index1, index2, index3, index4, index5;
        Gate g;
        String g0, g1, g2;
        int cpt, transi=0;
        MasterGateManager mgm = new MasterGateManager(tm);
        warnings = new Vector();

        //System.out.println("input data=" + inputData);

        int cpt1 = 0;

        try {
            while((s = br.readLine()) != null) {
              cpt1 ++;
              if (cpt1 % 100000 == 0) {
                System.out.println("=" + cpt1 + " / " + transi);
              }
                if (s.charAt(0) == '(') {
                    index1 = s.indexOf(",");
                    if ((index1 > -1) && ((index1+1) < s.length())) {
                        g1 = s.substring(0, index1 + 1);
                        s = s.substring(index1+1, s.length());

                        //System.out.println("g1=" + g1 + " s=" + s);

                        index2 = s.indexOf(",");
                        if ((index2 > -1) && ((index2+1) < s.length())) {
                            g2 = s.substring(index2, s.length());
                            s = s.substring(0, index2);
                            s = s.trim();

                            //System.out.println("g2=" + g2 + " s=" + s);

                            // Get action id
                            // Most common case: no data
                            index3 = s.indexOf('"');
                            if (index3 == -1) { // no data
                                actionName = s;
                                g0 = "";
                            } else {
                                // Extract action name
                                actionName = s.substring(index3+1, s.indexOf('!')).trim();

                                // Format data
                                g0 = "<";
                                cpt = 0;
                                while((index4 = s.indexOf('!')) > -1) {
                                    s = s.substring(index4+1, s.length());
                                    if (cpt > 0) {
                                        g0 += ",";
                                    }
                                    cpt ++;
                                    index4 = s.indexOf('!');
                                    if (index4 > -1) {
                                        g0 += s.substring(0, index4);
                                    } else {
                                        g0 += s.substring(0, s.indexOf('"')).trim();
                                    }
                                }
                                g0 += ">";
                            }

                            // Working on action name!
                            g = mgm.getGateLowerCase(actionName);

                            if (g != null) {
                                actionName1 = actionName;
                                actionName = g.getName();
                                if (mgm.nbOfPossibleGatesLowerCase(actionName1) > 1) {
                                    CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Action " + actionName1 + " has several possible candidates ; " + actionName + " has been chosen");
                                    warnings.add(ce);
                                }
                            }

                            // Store result
                            result.append(g1 + "\"i(" + actionName + g0 + ")\"" + g2 + "\n");
                        }
                    }
                } else if (s.startsWith("des")) {
                  index1 = s.indexOf(",");
                  s = s.substring(index1+1, s.length());
                  index1 = s.indexOf(",");
                  s = s.substring(0, index1).trim();
                  //System.out.println("nb of transitions=" + s);
                  transi = Integer.decode(s).intValue();
                  if (transi > max) {
                    return null;
                  }
                  result.append(s + "\n");
                }
            }
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
            return null;
        }
        return new String(result);
    }*/

	public boolean belongTo(GroupOfGates gog, Vector gates) {
		int i, j;
		TClassAndGateDS tcg;
		String nameTClass, nameGate;
		for(i=0; i<gog.size(); i++) {
			nameTClass = gog.getTClassAt(i).getName();
			nameGate = gog.getGateAt(i).getName();
			for(j=0; j<gates.size(); j++) {
				tcg = (TClassAndGateDS)(gates.elementAt(j));
				if ((tcg.getTClassName().compareTo(nameTClass) == 0) && (tcg.getGateName().compareTo(nameGate) == 0)) {
					//System.out.println("Projected gate");
					return true;
				}
			}

		}
		return false;

	}

	public String makeIAction(String s) {
		int index1, index2;
		index1 = s.indexOf("i(");
		index2 = s.indexOf(")");
		return s.substring(0, index1-1) + "i" + s.substring(index2+2, s.length());
	}

	public String makeAction(String s, String actionName) {
		int index1, index2;
		index1 = s.indexOf("i(");
		index2 = s.indexOf(")");
		return s.substring(0, index1) + actionName + s.substring(index2+1, s.length());
	}

	// UNDO MANAGEMENT

	public void saveOperation(Point p) {

		String s = makeXMLFromTurtleModeling(-1);

		if ((pointerOperation > -1) && (pointerOperation < savedOperations.size() - 1)) {
			// some save operations must be erased
			for (int i = pointerOperation +1; i<savedOperations.size(); i++) {
				savedOperations.removeElementAt(i);
				savedPanels.removeElementAt(i);
				i --;
			}
		}

		// save actions on tab
		int size = savedPanels.size();
		if (size > 0) {
			Point p1  = (Point)(savedPanels.elementAt(size - 1)); // panels are saved under the form of a point -> x = analysis/design, y = panel
			if (p == null)
				p = p1;
			if ((p1.x != p.x) || (p1.y != p.y)){
				savedOperations.add(savedOperations.elementAt(size - 1));
				savedPanels.add(p);
				if (savedOperations.size() > nbMaxSavedOperations) {
					savedOperations.removeElementAt(0);
					savedPanels.removeElementAt(0);
				}
			}
		}

		savedOperations.add(s);
		savedPanels.add(p);
		if (savedOperations.size() > nbMaxSavedOperations) {
			savedOperations.removeElementAt(0);
			savedPanels.removeElementAt(0);
		}
		pointerOperation = savedOperations.size() - 1;

		selectBackwardMode();
	}

	public void backward() {
		if ((pointerOperation < 1)	|| (savedOperations.size() < 2)) {
			return;
		}

		removeAllComponents();
		mgui.reinitMainTabbedPane();
		try {
			pointerOperation --;
			loadModelingFromXML((String)(savedOperations.elementAt(pointerOperation)));
		} catch (Exception e) {
			System.out.println("****** Exception ******");
		}

		Point p = (Point)(savedPanels.elementAt(pointerOperation));
		if (p != null) {
			TDiagramPanel tdp = mgui.selectTab(p);
			tdp.mode = tdp.NORMAL;
			tdp.setDraw(true);
			tdp.repaint();
		}

		selectBackwardMode();
	}

	public void selectBackwardMode() {
		if (pointerOperation <0) {
			mgui.setMode(MainGUI.NO_BACKWARD);
			mgui.setMode(MainGUI.NO_FORWARD);
		} else {

			// forward
			if (pointerOperation < savedOperations.size() - 1) {
				mgui.setMode(MainGUI.FORWARD);
			}  else {
				mgui.setMode(MainGUI.NO_FORWARD);
			}

			// backward
			if (pointerOperation > 0) {
				mgui.setMode(MainGUI.BACKWARD);
			} else {
				mgui.setMode(MainGUI.NO_BACKWARD);
			}
		}
	}


	public void forward() {
		if ((pointerOperation < 0) || (pointerOperation > 	savedOperations.size() - 2)) {
			return;
		}

		removeAllComponents();
		mgui.reinitMainTabbedPane();

		try {
			pointerOperation ++;
			loadModelingFromXML((String)(savedOperations.elementAt(pointerOperation)));
		} catch (Exception e) {
			System.out.println("****** Exception ******");
		}

		Point p = (Point)(savedPanels.elementAt(pointerOperation));
		if (p != null) {
			TDiagramPanel tdp = mgui.selectTab(p);
			tdp.mode = TDiagramPanel.NORMAL;
			tdp.setDraw(true);
			tdp.repaint();
		}

		selectBackwardMode();
	}


	// BUILDING A TURTLE MODELING AND CHECKING IT

	public boolean checkTURTLEModeling(Vector tclasses, DesignPanel dp, boolean overideSyntaxChecking) {
		// Builds a TURTLE modeling from diagrams
		warnings = new Vector();
		//checkingErrors = null;
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		//tm = new TURTLEModeling();
		//listE = new CorrespondanceTGElement();
		mgui.reinitCountOfPanels();

		DesignPanelTranslator dpt = new DesignPanelTranslator(dp);
		tm = dpt.generateTURTLEModeling(tclasses, "");
		tmState = 0;

		listE = dpt.getCorrespondanceTGElement();
		checkingErrors = dpt.getErrors();
		if ((checkingErrors != null) && (checkingErrors.size() >0)){
			return false;
		}

		// modeling is built
		// Now check it !
		if (!overideSyntaxChecking) {
			TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
	
			checkingErrors = tmc.syntaxAnalysisChecking();
			warnings = tmc.getWarnings();
	
			if ((checkingErrors != null) && (checkingErrors.size() > 0)){
				return false;
			} else {
				return true;
			}
		}
		
		return true;
	}

	public Vector getCheckingErrors() {
		return checkingErrors;
	}

	public Vector getCheckingWarnings() {
		return warnings;
	}


	// SAVING AND LOADING IN XML

	public static String transformString(String s) {
		if (s != null) {
			s = Conversion.replaceAllChar(s, '&', "&amp;");
			s = Conversion.replaceAllChar(s, '<', "&lt;");
			s = Conversion.replaceAllChar(s, '>', "&gt;");
			s = Conversion.replaceAllChar(s, '"', "&quot;");
			s = Conversion.replaceAllChar(s, '\'', "&apos;");
		}
		return s;
	}

	public static String encodeString(String s) {
		return s;
	}

	public static String decodeString(String s) throws MalformedModelingException {
		if (s == null)
			return s;
		byte b[] = null;
		try {
			b = s.getBytes("ISO-8859-1");
			return new String(b);
		} catch (Exception e) {
			throw new MalformedModelingException();
		}
	}
	
	public String mergeTURTLEGModeling(String modeling1, String modeling2) {
		int index1 = modeling1.indexOf("</TURTLEGMODELING");
		int index2 = modeling2.indexOf("<TURTLEGMODELING");
		if ((index1 == -1) || (index2 == -1)) {
			return null;
		}
		
		String modeling = modeling1.substring(0, index1);
		String tmp = modeling2.substring(index2, modeling2.length());
		index2 = modeling2.indexOf('<');
		if (index2 == -1) {
			return null;
		}
		
		tmp = tmp.substring(index2+1, tmp.length());
		
		return modeling + tmp;
	}

	public String makeXMLFromComponentOfADiagram(TDiagramPanel tdp, TGComponent tgc, int copyMaxId, int _decX, int _decY) {
		StringBuffer sb = new StringBuffer();

		//sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<TURTLEGMODELING>\n\n");
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n<TURTLEGSELECTEDCOMPONENTS ");
		sb.append("version=\"" + DefaultText.getVersion());
		sb.append("\" copyMaxId=\"" + copyMaxId);
		sb.append("\" decX=\"" + _decX);
		sb.append("\" decY=\"" + _decY);
		sb.append("\" >\n\n");

		StringBuffer s;
		String str;

		s = tdp.saveComponentInXML(tgc);

		if (tgc instanceof TCDTClass) {
			TActivityDiagramPanel tadp = mgui.getActivityDiagramPanel(mgui.getCurrentSelectedIndex(), tgc.getValue());
			s.append(tadp.saveInXML());
		}

		if (tgc instanceof TOSClass) {
			TURTLEOSActivityDiagramPanel tosadp = mgui.getTURTLEOSActivityDiagramPanel(mgui.getCurrentSelectedIndex(), tgc.getValue());
			s.append(tosadp.saveInXML());
		}

		if (tgc instanceof TMLTaskOperator) {
			TMLActivityDiagramPanel tmladp1 = mgui.getTMLActivityDiagramPanel(mgui.getCurrentSelectedIndex(), tgc.getValue());
			s.append(tmladp1.saveInXML());
		}
		
		if (tgc instanceof TMLCPrimitiveComponent) {
			TMLActivityDiagramPanel tmladp2 = mgui.getTMLActivityDiagramPanel(mgui.getCurrentSelectedIndex(), tgc.getValue());
			s.append(tmladp2.saveInXML());
		}
		
		if (tgc instanceof TMLCCompositeComponent) {
			TMLActivityDiagramPanel tmladp3;
			ArrayList<TMLCPrimitiveComponent> list =  ((TMLCCompositeComponent)tgc).getAllPrimitiveComponents();
			for (TMLCPrimitiveComponent comp: list) {
				tmladp3 =  mgui.getTMLActivityDiagramPanel(mgui.getCurrentSelectedIndex(), comp.getValue());
				s.append(tmladp3.saveInXML());
			}
		}

		if (s == null) {
			return null;
		}
		sb.append(s);
		sb.append("\n\n");
		sb.append("</TURTLEGSELECTEDCOMPONENTS>");

		str = new String(sb);
		str = encodeString(str);

		return str;
	}


	public String makeXMLFromSelectedComponentOfADiagram(TDiagramPanel tdp, int copyMaxId, int _decX, int _decY) {
		StringBuffer sb = new StringBuffer();
		//System.out.println("Making copy");

		//sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<TURTLEGMODELING>\n\n");
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n<TURTLEGSELECTEDCOMPONENTS ");
		sb.append("version=\"" + DefaultText.getVersion());
		sb.append("\" copyMaxId=\"" + copyMaxId);
		sb.append("\" decX=\"" + _decX);
		sb.append("\" decY=\"" + _decY);
		sb.append("\" >\n\n");

		StringBuffer s;
		String str;

		s = tdp.saveSelectedInXML();

		Vector v = tdp.selectedTclasses();

		if ((v != null) && (v.size() > 0)) {
			TCDTClass t;
			TActivityDiagramPanel tadp;
			for(int i=0; i<v.size(); i++) {
				t = (TCDTClass)(v.elementAt(i));
				tadp = mgui.getActivityDiagramPanel(mgui.getCurrentSelectedIndex(), t.getValue());
				s.append(tadp.saveInXML());
			}
		}

		//Added by Solange
		//bug removed by Emil
		if (tdp instanceof ProactiveCSDPanel)
		{
			v=((ProactiveCSDPanel)tdp).selectedProCSDComponent(null);
			if ((v != null) && (v.size() > 0)) {
				ProCSDComponent t;
				ProactiveSMDPanel psmd;
				for(int i=0; i<v.size(); i++) {
					t = (ProCSDComponent)(v.elementAt(i));
					psmd = mgui.getSMDPanel(mgui.getCurrentSelectedIndex(), t.getValue());
					if (psmd!=null)
						s.append(psmd.saveInXML());
				}
			}
		}
		//until here


		v = tdp.selectedTURTLEOSClasses();
		if ((v != null) && (v.size() > 0)) {
			//System.out.println("Saving TURTLEOS activity diagram Panel...");
			TOSClass t;
			TURTLEOSActivityDiagramPanel tosadp;
			for(int i=0; i<v.size(); i++) {
				t = (TOSClass)(v.elementAt(i));
				tosadp = mgui.getTURTLEOSActivityDiagramPanel(mgui.getCurrentSelectedIndex(), t.getValue());
				s.append(tosadp.saveInXML());
			}
		}

		v = tdp.selectedTMLTasks();
		if ((v != null) && (v.size() > 0)) {
			//System.out.println("Saving TML activity diagram Panel...");
			TMLTaskOperator t;
			TMLActivityDiagramPanel tmladp;
			for(int i=0; i<v.size(); i++) {
				t = (TMLTaskOperator)(v.elementAt(i));
				tmladp = mgui.getTMLActivityDiagramPanel(mgui.getCurrentSelectedIndex(), t.getValue());
				s.append(tmladp.saveInXML());
			}
		}
		
		v = tdp.selectedCPrimitiveComponent();
		if ((v != null) && (v.size() > 0)) {
			//System.out.println("Saving TML activity diagram Panel...");
			TMLCPrimitiveComponent ct;
			TMLActivityDiagramPanel tmladp;
			for(int i=0; i<v.size(); i++) {
				ct = (TMLCPrimitiveComponent)(v.elementAt(i));
				tmladp = mgui.getTMLActivityDiagramPanel(mgui.getCurrentSelectedIndex(), ct.getValue());
				s.append(tmladp.saveInXML());
			}
		}

		if (s == null) {
			return null;
		}
		sb.append(s);
		sb.append("\n\n");
		sb.append("</TURTLEGSELECTEDCOMPONENTS>");

		str = new String(sb);
		str = encodeString(str);

		//System.out.println("Copy done");
		//System.out.println(str);

		return str;
	}

	public String makeXMLFromTurtleModeling(int index) {
		StringBuffer sb = new StringBuffer();
		//sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<TURTLEGMODELING>\n\n");
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n<TURTLEGMODELING version=\"" + DefaultText.getVersion() + "\">\n\n");

		StringBuffer s;
		String str;

		TURTLEPanel tp;
		int i;
		// search for diagram panels (Design)
		for(i=0; i<panels.size(); i++) {
			if ((index == -1) || (i == index)) {
				tp = (TURTLEPanel)(panels.elementAt(i));
				s = tp.saveInXML();
				if (s == null) {
					return null;
				}
				sb.append(s);
				sb.append("\n\n");
			}
		}

		sb.append("</TURTLEGMODELING>");

		str = new String(sb);
		str = encodeString(str);

		return str;
	}

	public void removeAllComponents() {
		TDiagramPanel tdp;
		int i, j;
		Vector panelss;
		// search for diagram panels
		for(i=0; i<panels.size(); i++) {
			panelss = (Vector)(((TURTLEPanel)(panels.elementAt(i))).panels);
			for(j=0; j<panelss.size(); j++) {
				tdp = (TDiagramPanel)(panelss.elementAt(j));
				tdp.removeAll();
			}
		}
	}

	public void copyModelingFromXML(TDiagramPanel tdp, String s, int X, int Y) throws MalformedModelingException {
		System.out.println("copyModelingFromXML");
		//System.out.println(s);
		//System.out.println("copyModelingFromXML:");
		//LinkedList ComponentsList=tdp.getComponentList();
		int beginIndex = tdp.getComponentList().size();

		//Added by Solange
		int cuenta=1;

		s = decodeString(s);
		
		//System.out.println("copy=" + s);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
		if ((dbf == null) || (db == null)) {
			throw new MalformedModelingException();
		}

		int i;
		//int copyMaxId;
		int _decX = 0, _decY = 0;

		try {
			// building nodes from xml String
			Document doc = db.parse(bais);
			NodeList nl;

			decId = tdp.getMaxId() + 1;
			TGComponent.setGeneralId(TGComponent.getGeneralId() + decId + 2);
			nl = doc.getElementsByTagName("TURTLEGSELECTEDCOMPONENTS");

			if (nl == null) {
				return;
			}

			Node adn;
			Element elt;

			for(i=0; i<nl.getLength(); i++) {
				adn = nl.item(i);
				if (adn.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element) adn;
					_decX = X - Integer.decode(elt.getAttribute("decX")).intValue();
					_decY = Y - Integer.decode(elt.getAttribute("decY")).intValue();
					//copyMaxId = Integer.decode(elt.getAttribute("copyMaxId")).intValue();
				}
			}

			// Managing diagrams
			if (tdp instanceof TClassDiagramPanel) {
				System.out.println("TClassDiagramPanel copy");
				
				nl = doc.getElementsByTagName("TClassDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}


				TClassDiagramPanel tcdp = (TClassDiagramPanel)tdp;


				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (tcdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						tcdp.loadExtraParameters(elt);

						//System.out.println("Class diagram : " + tcdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tcdp);
						makePostProcessing(tcdp);
						//System.out.println("Class diagram : " + tcdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tcdp);
						//System.out.println("Class diagram : " + tcdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tcdp);
						//System.out.println("Class diagram : " + tcdp.getName() + " real points");
						connectConnectorsToRealPoints(tcdp);
						tcdp.structureChanged();
						//System.out.println("Class diagram : " + tcdp.getName() + " post loading " + beginIndex);
						makePostLoading(tcdp, beginIndex);
						//System.out.println("Class diagram : " + tcdp.getName() + " post loading done");
					}
				}
				docCopy = null;

			} else if (tdp instanceof TActivityDiagramPanel) {
				System.out.println("TActivityDiagramPanel copy");
				nl = doc.getElementsByTagName("TActivityDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				TActivityDiagramPanel tadp = (TActivityDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (tadp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						tadp.loadExtraParameters(elt);

						//System.out.println("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tadp);
						//System.out.println("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tadp);
						//System.out.println("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tadp);
						//System.out.println("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(tadp);
						tadp.structureChanged();
						//System.out.println("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(tadp, beginIndex);
					}
				}
			} else if (tdp instanceof InteractionOverviewDiagramPanel) {

				nl = doc.getElementsByTagName("InteractionOverviewDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				InteractionOverviewDiagramPanel iodp = (InteractionOverviewDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (iodp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//System.out.println("Activity diagram : " + iodp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), iodp);
						//System.out.println("Activity diagram : " + iodp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), iodp);
						//System.out.println("Activity diagram : " + iodp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), iodp);
						//System.out.println("Activity diagram : " + iodp.getName() + " real points");
						connectConnectorsToRealPoints(iodp);
						iodp.structureChanged();
						//System.out.println("Activity diagram : " + iodp.getName() + " post loading");
						makePostLoading(iodp, beginIndex);
					}
				}
			} else if (tdp instanceof SequenceDiagramPanel) {
				nl = doc.getElementsByTagName("SequenceDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				SequenceDiagramPanel sdp = (SequenceDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (sdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//System.out.println("Sequence diagram: " + sdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), sdp);
						//System.out.println("Sequence diagram: " + sdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), sdp);
						//System.out.println("Sequence diagram: " + sdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), sdp);
						//System.out.println("Sequence diagram: " + sdp.getName() + " real points");
						connectConnectorsToRealPoints(sdp);
						sdp.structureChanged();
						//System.out.println("Sequence diagram: " + sdp.getName() + " post loading");
						makePostLoading(sdp, beginIndex);
					}
				}
			} else if (tdp instanceof UseCaseDiagramPanel) {
				nl = doc.getElementsByTagName("UseCaseDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				UseCaseDiagramPanel ucdp = (UseCaseDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (ucdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//System.out.println("Activity diagram : " + sdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), ucdp);
						//System.out.println("Activity diagram : " + sdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), ucdp);
						//System.out.println("Activity diagram : " + sdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), ucdp);
						//System.out.println("Activity diagram : " + sdp.getName() + " real points");
						connectConnectorsToRealPoints(ucdp);
						ucdp.structureChanged();
						//System.out.println("Activity diagram : " + iodp.getName() + " post loading");
						makePostLoading(ucdp, beginIndex);
					}
				}
			} else if (tdp instanceof TDeploymentDiagramPanel) {
				nl = doc.getElementsByTagName("TDeploymentDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				TDeploymentDiagramPanel tddp = (TDeploymentDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (tddp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//System.out.println("Activity diagram : " + sdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tddp);
						//System.out.println("Activity diagram : " + sdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tddp);
						//System.out.println("Activity diagram : " + sdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tddp);
						//System.out.println("Activity diagram : " + sdp.getName() + " real points");
						connectConnectorsToRealPoints(tddp);
						tddp.structureChanged();
						//System.out.println("Activity diagram : " + iodp.getName() + " post loading");
						makePostLoading(tddp, beginIndex);
					}
				}
			} else if (tdp instanceof NCDiagramPanel) {
				nl = doc.getElementsByTagName("NCDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				NCDiagramPanel ncdp = (NCDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (ncdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//System.out.println("Activity diagram : " + sdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), ncdp);
						//System.out.println("Activity diagram : " + sdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), ncdp);
						//System.out.println("Activity diagram : " + sdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), ncdp);
						//System.out.println("Activity diagram : " + sdp.getName() + " real points");
						connectConnectorsToRealPoints(ncdp);
						ncdp.structureChanged();
						//System.out.println("Activity diagram : " + iodp.getName() + " post loading");
						makePostLoading(ncdp, beginIndex);
					}
				}
			} else if (tdp instanceof RequirementDiagramPanel) {
				nl = doc.getElementsByTagName("TRequirementDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				RequirementDiagramPanel rdp = (RequirementDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (rdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), rdp);
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), rdp);
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), rdp);
						connectConnectorsToRealPoints(rdp);
						rdp.structureChanged();
						makePostLoading(rdp, beginIndex);
					}
				}
			} else if (tdp instanceof TMLTaskDiagramPanel) {
				nl = doc.getElementsByTagName("TMLTaskDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//System.out.println("Toto 1");


				TMLTaskDiagramPanel tmltdp = (TMLTaskDiagramPanel)tdp;


				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (tmltdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						tmltdp.loadExtraParameters(elt);

						//System.out.println("Toto 2");

						//System.out.println("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmltdp);
						//System.out.println("Toto 3");
						makePostProcessing(tmltdp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmltdp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmltdp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(tmltdp);
						tmltdp.structureChanged();
						//System.out.println("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(tmltdp, beginIndex);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
			} else if (tdp instanceof TMLComponentTaskDiagramPanel) {
				nl = doc.getElementsByTagName("TMLComponentTaskDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//System.out.println("Toto 1");


				TMLComponentTaskDiagramPanel tmlctdp = (TMLComponentTaskDiagramPanel)tdp;
				//tmlctdp.updateReferences();


				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (tmlctdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						tmlctdp.loadExtraParameters(elt);

						//System.out.println("Toto 2");

						//System.out.println("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmlctdp);
						//System.out.println("Toto 3");
						makePostProcessing(tmlctdp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmlctdp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmlctdp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(tmlctdp);
						tmlctdp.structureChanged();
						//System.out.println("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(tmlctdp, beginIndex);
						tmlctdp.hideConnectors();
						tmlctdp.updatePorts();
						//System.out.println("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
				tmlctdp.updatePorts();
			} else if (tdp instanceof TMLActivityDiagramPanel) {
				nl = doc.getElementsByTagName("TMLActivityDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				TMLActivityDiagramPanel tmladp = (TMLActivityDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (tmladp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//tmladp.loadExtraParameters(elt);

						//System.out.println("Activity diagram : " + tmladp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmladp);
						//System.out.println("Activity diagram : " + tmladp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmladp);
						//System.out.println("Activity diagram : " + tmladp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmladp);
						//System.out.println("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(tmladp);
						tmladp.structureChanged();
						//System.out.println("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(tmladp, beginIndex);
					}
				}
			}  else if (tdp instanceof TMLArchiDiagramPanel) {
				nl = doc.getElementsByTagName("TMLArchiDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//System.out.println("Toto 1");

				TMLArchiDiagramPanel tmadp = (TMLArchiDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (tmadp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						tmadp.loadExtraParameters(elt);

						//System.out.println("Toto 2");

						//System.out.println("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmadp);
						//System.out.println("Toto 3");
						makePostProcessing(tmadp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmadp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmadp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(tmadp);
						tmadp.structureChanged();
						//System.out.println("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(tmadp, beginIndex);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
			} else if (tdp instanceof TURTLEOSClassDiagramPanel) {
				nl = doc.getElementsByTagName("TURTLEOSClassDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				TURTLEOSClassDiagramPanel toscdp = (TURTLEOSClassDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (toscdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//toscdp.loadExtraParameters(elt);
						//System.out.println("Toto 2");
						//System.out.println("TURTLEOS task diagram : " + toscdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), toscdp);
						//System.out.println("Toto 3");
						makePostProcessing(toscdp);
						//System.out.println("TURTLEOS task diagram : " + toscdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), toscdp);
						//System.out.println("TURTLEOS task diagram : " + toscdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), toscdp);
						//System.out.println("TURTLEOS task diagram : " + toscdp.getName() + " real points");
						connectConnectorsToRealPoints(toscdp);
						toscdp.structureChanged();
						//System.out.println("TURTLEOS task diagram : " + toscdp.getName() + " post loading " + beginIndex);
						makePostLoading(toscdp, beginIndex);
						//System.out.println("TURTLEOS task diagram : " + toscdp.getName() + " post loading done");
					}
				}
			} else if (tdp instanceof TURTLEOSActivityDiagramPanel) {
				nl = doc.getElementsByTagName("TURTLEOSActivityDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				TURTLEOSActivityDiagramPanel tosadp = (TURTLEOSActivityDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (tosadp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//tmladp.loadExtraParameters(elt);

						//System.out.println("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tosadp);
						//System.out.println("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tosadp);
						//System.out.println("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tosadp);
						//System.out.println("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(tosadp);
						tosadp.structureChanged();
						//System.out.println("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(tosadp, beginIndex);
					}
				}
			} else if (tdp instanceof ProactiveCSDPanel)
			{
				//cuenta=beginIndex+1;
				cuenta=mgui.tabs.size()-1;
				nl = doc.getElementsByTagName("ProactiveCSDPanelCopy");
				if (nl.getLength()==0) 
				{
					nl=doc.getElementsByTagName("ProactiveCSDPanel");
				}
				docCopy = doc;
				if (nl == null)
				{
					return;
				}
				ProactiveCSDPanel pcsdp = (ProactiveCSDPanel)tdp;
				for(i=0; i<nl.getLength(); i++)
				{
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE)
					{
						elt = (Element) adn;
						if (pcsdp == null)
						{
							throw new MalformedModelingException();
						}


//						int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//pcsdp.loadExtraParameters(elt);
						//System.out.println("Toto 2");
						//System.out.println("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), pcsdp);
						//System.out.println("Toto 3");
						makePostProcessing(pcsdp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), pcsdp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), pcsdp);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(pcsdp);
						pcsdp.structureChanged();
						//System.out.println("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(pcsdp, beginIndex);
						//System.out.println("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
				// Added by Solange
				nl = doc.getElementsByTagName("ProactiveSMDPanel");
				if (nl == null)
				{
					return;
				}
				String name="";
				ProactiveSMDPanel psmdp;
				for(i=0; i<nl.getLength(); i++) //Erased cuenta++ by Solange at the end condition of the for
				{
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE)
					{
						elt = (Element) adn;
						name=elt.getAttribute("name");
						//Added by Solange name at the beginning and cuenta
						name=mgui.createProActiveSMD(cuenta,name);
						psmdp=mgui.getSMDPanel(cuenta, name);
						if (psmdp == null)
						{
							throw new MalformedModelingException();
						}

//						int xSel = Integer.decode(elt.getAttribute("minX")).intValue();
//int ySel = Integer.decode(elt.getAttribute("maxX")).intValue(); // - mgui.getCurrentTDiagramPanel().currentX;
//						int widthSel = Integer.decode(elt.getAttribute("minY")).intValue(); // - mgui.getCurrentTDiagramPanel().currentY;;
//						int heightSel = Integer.decode(elt.getAttribute("maxY")).intValue(); // - mgui.getCurrentTDiagramPanel().currentY;;

						decX = _decX;
						decY = _decY;

						//tmladp.loadExtraParameters(elt);
						//System.out.println("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), psmdp);
						//System.out.println("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), psmdp);
						//System.out.println("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), psmdp);
						//System.out.println("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(psmdp);
						psmdp.structureChanged();
						//System.out.println("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(psmdp, beginIndex);
						//until here
					}
				}
			} else if (tdp instanceof ProactiveSMDPanel) {
				//Changed by Solange, before it was like the first line
				//nl = doc.getElementsByTagName("ProactiveSMDPanelCopy");
				nl = doc.getElementsByTagName("ProactiveSMDPanelCopy");

				if (nl == null) {
					return;
				}

				ProactiveSMDPanel psmdp = (ProactiveSMDPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (psmdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//tmladp.loadExtraParameters(elt);

						//System.out.println("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), psmdp);
						//System.out.println("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), psmdp);
						//System.out.println("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), psmdp);
						//System.out.println("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(psmdp);
						psmdp.structureChanged();
						//System.out.println("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(psmdp, beginIndex);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("500 ");
			throw new MalformedModelingException();
		} catch (SAXException saxe) {
			System.out.println("501 " + saxe.getMessage());
			throw new MalformedModelingException();
		}
	}

	// Returns null if s is not a saved TURTLE modeling of an older format
	public String upgradeSaved(String s) {
		int index1, index2, index3;
		StringBuffer sb = new StringBuffer("");;
		//String tmp;

		index1 = s.indexOf("<TClassDiagramPanel");
		index2 = s.indexOf("<InteractionOverviewDiagramPanel ");
		index3 = s.indexOf("</TURTLEGMODELING>");

		if ((index1 <0) ||(index3 < 0)){
			return null;
		}

		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n<TURTLEGMODELING version=\"" + DefaultText.getVersion() + "\">\n\n");

		if (index2 > -1) {
			sb.append("<Modeling type=\"Analysis\" nameTab=\"Analysis\" >\n");
			sb.append(s.substring(index2, index3));
			sb.append("</Modeling>\n\n");
		}

		if (index2 < 0) {
			index2 = index3;
		}
		sb.append("<Modeling type=\"Design\" nameTab=\"Design\" >\n");
		sb.append(s.substring(index1, index2));
		sb.append("</Modeling>\n\n");

		sb.append(s.substring(index3, s.length()));

		//System.out.println("Got:" + sb);

		return sb.toString();
	}

	public void loadModelingFromXML(String s) throws MalformedModelingException {
		
		if (s == null) {
			return;
		}

		s = decodeString(s);

		decX = 0;
		decY = 0;
		decId = 0;
		TGComponent.setGeneralId(100000);

		ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());

		if ((dbf == null) || (db == null)) {
			throw new MalformedModelingException();
		}

		try {
			// building nodes from xml String
			Document doc = db.parse(bais);
			NodeList panelNl;
			//NodeList designPanelNl; 
			//NodeList analysisNl;

			int i;
			//Element elt;
			Node node;

			// Managing design panels
			panelNl = doc.getElementsByTagName("Modeling");

			if (panelNl.getLength() == 0) {
				// Modeling saved in old format?
				s = upgradeSaved(s);
				if (s != null) {
					JOptionPane.showMessageDialog(mgui.frame, "The modeling has been converted to this new version of TTool", "Loading information", JOptionPane.INFORMATION_MESSAGE);
				}
				loadModelingFromXML(s);
				return;

			}
			//designPanelNl = doc.getElementsByTagName("Design");
			//analysisNl = doc.getElementsByTagName("Analysis");

			pendingConnectors = new ArrayList<TGConnectorInfo>();
			
			//System.out.println("nb de design=" + designPanelNl.getLength() + " nb d'analyse=" + analysisNl.getLength());
			boolean error = false;
			for(i=0; i<panelNl.getLength(); i++) {
				node = panelNl.item(i);
				//System.out.println("Node = " + dnd);
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					// create design, and get an index for it
					try {
						loadModeling(node);
					} catch (MalformedModelingException mme) {
						Element elt = (Element) node;
						String type = elt.getAttribute("type");
						System.out.println("Error when loading diagram:" + type);
						error = true;
					}
				}
			}
			if (error == true) {
				throw new MalformedModelingException();
			}

		} catch (NumberFormatException nfe) {
			System.out.println("400 ");
			throw new MalformedModelingException();
		} catch (IOException e) {
			System.out.println("500 ");
			throw new MalformedModelingException();
		} catch (SAXException saxe) {
			System.out.println("501 " + saxe.getMessage());
			throw new MalformedModelingException();
		}
		//System.out.println("making IDs");
		makeLastLoad();
		makeLovelyIds();
		//System.out.println("IDs done");
	}

	public void loadModeling(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String type = elt.getAttribute("type");
		if (type.compareTo("Design") == 0) {
			loadDesign(node);
		} else if (type.compareTo("Analysis") == 0) {
			loadAnalysis(node);
		} else if (type.compareTo("Deployment") == 0) {
			loadDeployment(node);
		} else if (type.compareTo("NC diagram") == 0) {
			loadNC(node);
		} else if (type.compareTo("Requirement") == 0) {
			loadRequirement(node);
		} else if (type.compareTo("TML Design") == 0) {
			loadTMLDesign(node);
		} else if (type.compareTo("TML Component Design") == 0) {
			loadTMLComponentDesign(node);
		} else if (type.compareTo("TML Architecture") == 0) {
			loadTMLArchitecture(node);
		} else if (type.compareTo("TURTLE-OS Design") == 0) {
			loadTURTLEOSDesign(node);
		} else if (type.compareTo("ProActive Design") == 0) {
			loadProActiveDesign(node);
		} else {
			throw new MalformedModelingException();
		}
	}

	public void loadDesign(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexDesign;


		nameTab = elt.getAttribute("nameTab");

		indexDesign = mgui.createDesign(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//System.out.println("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TClassDiagramPanel") == 0) {
					// Class diagram
					loadTClassDiagram(elt, indexDesign);
				} else { // Managing activity diagrams
					if (elt.getTagName().compareTo("TActivityDiagramPanel") == 0) {
						// Managing activity diagrams
						loadTActivityDiagram(elt, indexDesign);
					}
				}
			}
		}
	}

	public void loadAnalysis(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexAnalysis;
		int cpt = 0;

		nameTab = elt.getAttribute("nameTab");

		indexAnalysis = mgui.createAnalysis(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//System.out.println("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("InteractionOverviewDiagramPanel") == 0) {
					// IOD
					loadIODiagram(elt, indexAnalysis);
					cpt ++;
				} else { // Managing sequence diagrams
					if (elt.getTagName().compareTo("SequenceDiagramPanel") == 0) {
						loadSequenceDiagram(elt, indexAnalysis);
						cpt ++;
					} else if (elt.getTagName().compareTo("UseCaseDiagramPanel") == 0) {
						// Managing use case diagrams
						loadUseCaseDiagram(elt, indexAnalysis, cpt);
						cpt ++;
					}
				}
			}
		}
	}

	public void loadDeployment(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexAnalysis;


		nameTab = elt.getAttribute("nameTab");

		indexAnalysis = mgui.createDeployment(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//System.out.println("Deployment nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TDeploymentDiagramPanel") == 0) {
					// IOD
					loadTDeploymentDiagram(elt, indexAnalysis);
				}
			}
		}
	}
	
	public void loadNC(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexAnalysis;


		nameTab = elt.getAttribute("nameTab");

		indexAnalysis = mgui.createNC(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//System.out.println("Deployment nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("NCDiagramPanel") == 0) {
					// IOD
					loadNCDiagram(elt, indexAnalysis);
				}
			}
		}
	}

	public void loadRequirement(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexReq;
		int cpt_req = 0;


		nameTab = elt.getAttribute("nameTab");

		indexReq = mgui.createRequirement(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//System.out.println("Deployment nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TRequirementDiagramPanel") == 0) {
					loadRequirementDiagram(elt, indexReq, cpt_req);
					cpt_req ++;
				}
			}
		}
	}

	public void loadTMLDesign(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexDesign;


		nameTab = elt.getAttribute("nameTab");

		indexDesign = mgui.createTMLDesign(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//System.out.println("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TMLTaskDiagramPanel") == 0) {
					// Class diagram
					//System.out.println("Loading TML CD");
					loadTMLTaskDiagram(elt, indexDesign);
					//System.out.println("End loading TML CD");
				} else { // Managing activity diagrams
					if (elt.getTagName().compareTo("TMLActivityDiagramPanel") == 0) {
						// Managing activity diagrams
						//System.out.println("Loading TML AD");
						loadTMLActivityDiagram(elt, indexDesign);
					}
				}
			}
		}
	}
	
	public void loadTMLComponentDesign(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexDesign;


		nameTab = elt.getAttribute("nameTab");

		indexDesign = mgui.createTMLComponentDesign(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//System.out.println("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TMLComponentTaskDiagramPanel") == 0) {
					// Component diagram
					//System.out.println("Loading TML Component diagram");
					loadTMLComponentTaskDiagram(elt, indexDesign);
					//System.out.println("End loading TML CD");
				} else { // Managing activity diagrams
					if (elt.getTagName().compareTo("TMLActivityDiagramPanel") == 0) {
						// Managing activity diagrams
						//System.out.println("Loading TML AD");
						loadTMLActivityDiagram(elt, indexDesign);
					}
				}
			}
		}
	}
	
	public void loadTMLArchitecture(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexDesign;


		nameTab = elt.getAttribute("nameTab");

		indexDesign = mgui.createTMLArchitecture(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//System.out.println("TML Architecture nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TMLArchiDiagramPanel") == 0) {
					//System.out.println("Loading TML DD");
					loadTMLArchitectureDiagram(elt, indexDesign);
					//System.out.println("End loading TML DD");
				}
			}
		}
	}

	public void loadTURTLEOSDesign(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexDesign;

		nameTab = elt.getAttribute("nameTab");

		indexDesign = mgui.createTURTLEOSDesign(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//System.out.println("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TURTLEOSClassDiagramPanel") == 0) {
					// Class diagram
					//System.out.println("Loading TURTLEOS CD");
					loadTURTLEOSClassDiagram(elt, indexDesign);
					//System.out.println("End loading TML CD");
				} else { // Managing activity diagrams
					if (elt.getTagName().compareTo("TURTLEOSActivityDiagramPanel") == 0) {
						// Managing activity diagrams
						//System.out.println("Loading TURTLEOS AD");
						loadTURTLEOSActivityDiagram(elt, indexDesign);
					}
				}
			}
		}
	}

	public void loadProActiveDesign(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexDesign;


		nameTab = elt.getAttribute("nameTab");

		indexDesign = mgui.createProActiveDesign(nameTab);


		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//System.out.println("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("ProactiveCSDPanel") == 0) {
					// Class diagram
					//System.out.println("Loading TML CD");
					loadProactiveCSD(elt, indexDesign);
					//System.out.println("End loading TML CD");
				} else { // Managing activity diagrams
					if (elt.getTagName().compareTo("ProactiveSMDPanel") == 0) {
						// Managing activity diagrams
						//System.out.println("Loading TML AD");
						loadProactiveSMD(elt, indexDesign);
					}
				}
			}
		}
	}

	public void loadDiagram(Element elt, TDiagramPanel tdp) throws  MalformedModelingException, SAXException {
		int x, y;
		double zoom = 0;
		try {
			x = Integer.decode(elt.getAttribute("minX")).intValue();
			tdp.setMinX(x);
			x = Integer.decode(elt.getAttribute("maxX")).intValue();
			tdp.setMaxX(x);
			y = Integer.decode(elt.getAttribute("minY")).intValue();
			tdp.setMinY(y);
			y = Integer.decode(elt.getAttribute("maxY")).intValue();
			tdp.setMaxY(y);
			tdp.updateSize();
			zoom = Double.parseDouble(elt.getAttribute("zoom"));
			if (zoom != 0) {
				tdp.setZoom(zoom);
				mgui.updateZoomInfo();
			}
		} catch (Exception e) {
			// Model was saved in an older version of TTool
		}

		// for TClassdiagram Panel
		if (tdp instanceof TClassDiagramPanel) {
			((TClassDiagramPanel)tdp).loadExtraParameters(elt);
		}

		if (tdp instanceof TActivityDiagramPanel) {
			((TActivityDiagramPanel)tdp).loadExtraParameters(elt);
		}

		if (tdp instanceof TMLTaskDiagramPanel) {
			((TMLTaskDiagramPanel)tdp).loadExtraParameters(elt);
		}
		
		if (tdp instanceof TMLComponentTaskDiagramPanel) {
			((TMLComponentTaskDiagramPanel)tdp).loadExtraParameters(elt);
		}
		
		if (tdp instanceof TMLArchiDiagramPanel) {
			((TMLArchiDiagramPanel)tdp).loadExtraParameters(elt);
		}

		//System.out.println("Element" + elt.toString());
		// Loads components of the class diagram
		//System.out.println("Components");
		makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tdp);
		//System.out.println("Post processing");
		makePostProcessing(tdp);
		//System.out.println("Connectors");
		makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tdp);
		//System.out.println("Subcomponents");
		makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tdp);
		//System.out.println("RealPoints");
		connectConnectorsToRealPoints(tdp);
		//System.out.println("Structure changed");
		tdp.structureChanged();
		//System.out.println("Post loading");
		makePostLoading(tdp, 0);
		
		//System.out.println("Test connectors");
		if (tdp instanceof TMLComponentTaskDiagramPanel) {
			//System.out.println("Connectors...");
			((TMLComponentTaskDiagramPanel)tdp).setConnectorsToFront();
		}
	}


	public void loadTClassDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// class diagram name
		name = elt.getAttribute("name");
		mgui.setClassDiagramName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);

		loadDiagram(elt, tdp);
	}

	public void loadTActivityDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		TActivityDiagramPanel tadp = mgui.getActivityDiagramPanel(indexDesign, name);

		if (tadp == null) {
			throw new MalformedModelingException();
		}

		tadp.removeAll();

		loadDiagram(elt, tadp);
	}

	public void loadTMLTaskDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// class diagram name
		name = elt.getAttribute("name");
		mgui.setTMLTaskDiagramName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);

		//System.out.println("tdp=" + tdp.getName());

		loadDiagram(elt, tdp);
	}
	
	public void loadTMLComponentTaskDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// Diagram name
		name = elt.getAttribute("name");
		mgui.setTMLComponentTaskDiagramName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);

		//System.out.println("tdp=" + tdp.getName());

		loadDiagram(elt, tdp);
		
		((TMLComponentTaskDiagramPanel)tdp).hideConnectors();
		((TMLComponentTaskDiagramPanel)tdp).updatePorts();
	}
	
	public void loadTMLArchitectureDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// Diagram name
		name = elt.getAttribute("name");
		mgui.setTMLArchitectureDiagramName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);

		//System.out.println("tdp=" + tdp.getName());

		loadDiagram(elt, tdp);
	}

	public void loadTMLActivityDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		
		//System.out.println("getting tmladp: " + name);
		TMLActivityDiagramPanel tmladp = mgui.getTMLActivityDiagramPanel(indexDesign, name);
		//System.out.println("Got tmladp");
		
		
		if (tmladp == null) {
			//System.out.println("null tmladp");
			throw new MalformedModelingException();
		}

		tmladp.removeAll();

		loadDiagram(elt, tmladp);
	}

	public void loadTURTLEOSClassDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// class diagram name
		name = elt.getAttribute("name");
		mgui.setTMLTaskDiagramName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);
		loadDiagram(elt, tdp);
	}

	public void loadTURTLEOSActivityDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		TURTLEOSActivityDiagramPanel tosadp = mgui.getTURTLEOSActivityDiagramPanel(indexDesign, name);

		if (tosadp == null) {
			throw new MalformedModelingException();
		}

		tosadp.removeAll();

		loadDiagram(elt, tosadp);
	}

	public void loadProactiveCSD(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// class diagram name
		name = elt.getAttribute("name");
		//  mgui.setProacticeCSDName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);
		//ProactiveDesignPanel pdp=(ProactiveDesignPanel) mgui.getCurrentTURTLEPanel();
		ProactiveDesignPanel pdp=(ProactiveDesignPanel) tdp.tp;
		if (!tdp.getName().equals(name))
		{

			//tdp=pdp.addCompositeStructureDiagram(name);
			tdp=pdp.addProActiveCompSpecificationPanel(name);
		}

		//System.out.println("tdp=" + tdp.getName());

		loadDiagram(elt, tdp);

	}

	public void loadProactiveSMD(Element elt, int indexAnalysis) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		if (!(mgui.isProActiveSMDCreated(indexAnalysis, name))) {
			mgui.createProActiveSMD(indexAnalysis, name);
		}
		ProactiveSMDPanel smd = mgui.getSMDPanel(indexAnalysis, name);

		if (smd == null) {
			throw new MalformedModelingException();
		}

		smd.removeAll();

		loadDiagram(elt, smd);
	}

	public void loadIODiagram(Element elt, int indexAnalysis) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		//mgui.setIODName(indexAnalysis, name);
		if (!(mgui.isIODCreated(indexAnalysis, name))) {
			mgui.createIODiagram(indexAnalysis, name);
		}

		TDiagramPanel tdp = mgui.getIODiagramPanel(indexAnalysis, name);

		if (tdp == null) {
			throw new MalformedModelingException();
		}
		tdp.removeAll();

		loadDiagram(elt, tdp);
	}

	public void loadRequirementDiagram(Element elt, int indexAnalysis, int indexTab) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		mgui.createRequirementDiagram(indexAnalysis, name);


		TDiagramPanel tdp = mgui.getRequirementDiagramPanel(indexAnalysis, indexTab, name);

		if (tdp == null) {
			throw new MalformedModelingException();
		}
		tdp.removeAll();

		loadDiagram(elt, tdp);
	}

	public void loadSequenceDiagram(Element elt, int indexAnalysis) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		if (!(mgui.isSDCreated(indexAnalysis, name))) {
			mgui.createSequenceDiagram(indexAnalysis, name);
		}
		SequenceDiagramPanel sdp = mgui.getSequenceDiagramPanel(indexAnalysis, name);

		if (sdp == null) {
			throw new MalformedModelingException();
		}

		sdp.removeAll();

		loadDiagram(elt, sdp);
	}

	public void loadUseCaseDiagram(Element elt, int indexAnalysis, int indexTab) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		/*if (!(mgui.isUCDCreated(indexAnalysis, name))) {*/
		mgui.createUseCaseDiagram(indexAnalysis, name);
		//}
		UseCaseDiagramPanel ucdp = mgui.getUseCaseDiagramPanel(indexAnalysis, indexTab, name);

		if (ucdp == null) {
			throw new MalformedModelingException();
		}

		ucdp.removeAll();

		loadDiagram(elt, ucdp);
	}

	public void loadTDeploymentDiagram(Element elt, int indexDeployment) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		mgui.setDeploymentName(indexDeployment, name);
		TDiagramPanel tdp = mgui.getMainTDiagramPanel(indexDeployment);

		loadDiagram(elt, tdp);
	}
	
	public void loadNCDiagram(Element elt, int indexNC) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		mgui.setNCName(indexNC, name);
		TDiagramPanel tdp = mgui.getMainTDiagramPanel(indexNC);

		loadDiagram(elt, tdp);
	}

	// reinit the id of all components
	public void makeLovelyIds() {
		TDiagramPanel tdp;
		int id = 1;
		int i, j;
		TURTLEPanel tp;
		// search for diagram panels
		for(i=0; i<panels.size(); i++) {
			tp = (TURTLEPanel)(panels.elementAt(i));
			for(j=0; j<tp.panels.size(); j++) {
				tdp = (TDiagramPanel)(tp.panels.elementAt(j));
				id = tdp.makeLovelyIds(id);
				//System.out.println("Lovely id =" + id);
			}
		}

		TGComponent.setGeneralId(id + 1);
	}
	
	public void loadDiagramInformation(Element elt, TDiagramPanel tdp) throws  MalformedModelingException {
		int x, y;
		double zoom = 0;
		try {
			x = Integer.decode(elt.getAttribute("minX")).intValue();
			tdp.setMinX(x);
			x = Integer.decode(elt.getAttribute("maxX")).intValue();
			tdp.setMaxX(x);
			y = Integer.decode(elt.getAttribute("minY")).intValue();
			tdp.setMinY(y);
			y = Integer.decode(elt.getAttribute("maxY")).intValue();
			tdp.setMaxY(y);
			tdp.updateSize();
			zoom = Double.parseDouble(elt.getAttribute("zoom"));
			if (zoom != 0) {
				tdp.setZoom(zoom);
				mgui.updateZoomInfo();
			}
		} catch (Exception e) {
			// Model was saved in an older version of TTool
		}
		
		if (tdp instanceof TActivityDiagramPanel) {
			((TActivityDiagramPanel)tdp).loadExtraParameters(elt);
		}
	}

	public void loadActivityDiagram(TDiagramPanel tdp, String oldValue, String newValue) throws MalformedModelingException {
		//System.out.println("---> Load activity diagram");
		try {
			NodeList activityDiagramNl = docCopy.getElementsByTagName("TActivityDiagramPanel");

			System.out.println("Loading activity diagram of " + newValue + "Before : " + oldValue);
			System.out.println(docCopy);

			if (activityDiagramNl == null) {
				throw new MalformedModelingException();
			}

			Node adn;
			Element elt;
			TActivityDiagramPanel tadp;
			String name;
			int decXTmp = decX;
			int decYTmp = decY;
			int decIdTmp = decId;

			for(int i=0; i<activityDiagramNl.getLength(); i++) {
				adn = activityDiagramNl.item(i);
				if (adn.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element) adn;
					// class diagram name
					name = elt.getAttribute("name");
					//System.out.println("Name of activity diagram=" + name);

					if (name.equals(oldValue)) {
						int indexDesign = mgui.getMajorIndexOf(tdp);

						if (indexDesign < 0) {
							throw new MalformedModelingException();
						}

						tadp = mgui.getActivityDiagramPanel(indexDesign, newValue);

						//System.out.println("Searching panel");

						if (tadp == null) {
							throw new MalformedModelingException();
						}

						//System.out.println("Panel ok");

						decX = 0; decY = 0; decId = 0;
						
						

						tadp.removeAll();
						
						loadDiagramInformation(elt, tadp);
						
						//System.out.println("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tadp);
						//System.out.println("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tadp);
						//System.out.println("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tadp);
						//System.out.println("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(tadp);
						tadp.structureChanged();
						//System.out.println("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(tadp, 0);
					}
				}
			}
			decX = decXTmp;
			decY = decYTmp;
			decId = decIdTmp;
		} catch (SAXException saxe) {
			System.out.println("501 " + saxe.getMessage());
			throw new MalformedModelingException();
		}
	}

	public void loadTMLActivityDiagram(TDiagramPanel tdp, String oldValue, String newValue) throws MalformedModelingException {
		//System.out.println("---> Load TML activity diagram");
		try {
			if (docCopy == null) {
				System.out.println("Null doc copy");
			}
			NodeList activityDiagramNl = docCopy.getElementsByTagName("TMLActivityDiagramPanel");

			//System.out.println("Loading activity diagram of " + newValue + "Before : " + oldValue);

			if (activityDiagramNl == null) {
				//System.out.println("Null");
				throw new MalformedModelingException();
			}

			Node adn;
			Element elt;
			TMLActivityDiagramPanel tmladp;
			String name;
			int decXTmp = decX;
			int decYTmp = decY;
			int decIdTmp = decId;

			for(int i=0; i<activityDiagramNl.getLength(); i++) {
				adn = activityDiagramNl.item(i);
				if (adn.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element) adn;
					// class diagram name
					name = elt.getAttribute("name");
					//System.out.println("Name of activity diagram=" + name);

					if (name.equals(oldValue)) {
						int indexDesign = mgui.getMajorIndexOf(tdp);

						if (indexDesign < 0) {
							throw new MalformedModelingException();
						}

						tmladp = mgui.getTMLActivityDiagramPanel(indexDesign, newValue);

						//System.out.println("Searching panel");

						if (tmladp == null) {
							throw new MalformedModelingException();
						}

						//System.out.println("Panel ok");

						decX = 0; decY = 0; decId = 0;

						tmladp.removeAll();
						
						loadDiagramInformation(elt, tmladp);
						
						//System.out.println("Activity diagram : " + tmladp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmladp);
						//System.out.println("Activity diagram : " + tmladp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmladp);
						//System.out.println("Activity diagram : " + tmladp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmladp);
						//System.out.println("Activity diagram : " + tmladp.getName() + " real points");
						connectConnectorsToRealPoints(tmladp);
						tmladp.structureChanged();
						//System.out.println("Activity diagram : " + tmladp.getName() + " post loading");
						makePostLoading(tmladp, 0);
					}
				}
			}
			decX = decXTmp;
			decY = decYTmp;
			decId = decIdTmp;
		} catch (SAXException saxe) {
			System.out.println("501 " + saxe.getMessage());
			throw new MalformedModelingException();
		}
	}

	public void makePostLoading(TDiagramPanel tdp, int beginIndex) throws MalformedModelingException{
		TGComponent tgc;

		//System.out.println("Post loading of diagram " + tdp.toString());

		LinkedList list = tdp.getComponentList();

		for(int i=0; i<list.size()-beginIndex; i++) {
			tgc = (TGComponent)(list.get(i));
			//System.out.println(tgc.getName());
			//System.out.println(tgc.getValue());
			tgc.makePostLoading(decId);
		}

		//System.out.println("Post loading of diagram " + tdp.toString() + " achieved");
	}

	public void makeXMLComponents(NodeList nl, TDiagramPanel tdp) throws SAXException, MalformedModelingException {
		Node n;
		//Element elt;
		TGComponent tgc;

		if (tdp == null) {
			throw new MalformedModelingException();
		}
		boolean error = false;
		
		for(int i=0; i<nl.getLength(); i++) {
			n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				try {
					tgc = makeXMLComponent(n, tdp);
					if ((tgc != null) && (tgc.getFather() == null)) {
						//System.out.println("Component added to diagram tgc=" + tgc);
						tdp.addBuiltComponent(tgc);
					} else {
						//System.out.println("Component not added to diagram");
					}
				} catch (MalformedModelingException mme) {
					error = true;
				}
			}
		}
		
		if (error) {
			throw new MalformedModelingException();
		}
	}


	public TGComponent makeXMLComponent(Node n, TDiagramPanel tdp) throws SAXException, MalformedModelingException {
		Element elt;
		Element elt1;
		//System.out.println(n.toString());
		TGComponent tgc = null;
		TGComponent father;

		//
		try {

			NodeList nl = n.getChildNodes();
			elt = (Element)n;
			elt1 = elt;
			//System.out.println("elt=" + elt);

			int myType = Integer.decode(elt.getAttribute("type")).intValue();
			int myId = Integer.decode(elt.getAttribute("id")).intValue() + decId;

			int myX = -1, myY = -1, myWidth = -1, myHeight =-1;
			int myMinWidth = -1, myMinHeight = -1, myMinDesiredWidth = -1, myMinDesiredHeight = -1;
			int myMinX = -1, myMaxX = -1, myMinY = -1, myMaxY = -1;
			String myName = null, myValue = null;
			Vector tgcpList = new Vector();
			Point p;
			int i, x, y;
			int fatherId = -1, fatherNum = -1;
			String pre = "", post = "";
			String internalComment = "";
			boolean accessibility = false;
			boolean breakpoint = false;
			boolean hidden = false;

			for(i=0; i<nl.getLength(); i++) {
				n = nl.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element) n;
					if (elt.getTagName().equals("cdparam")) {
						myX = Integer.decode(elt.getAttribute("x")).intValue() + decX;
						myY = Integer.decode(elt.getAttribute("y")).intValue() + decY;
					} else if (elt.getTagName().equals("sizeparam")) {
						myWidth = Integer.decode(elt.getAttribute("width")).intValue();
						myHeight = Integer.decode(elt.getAttribute("height")).intValue();
						myMinWidth = Integer.decode(elt.getAttribute("minWidth")).intValue();
						myMinHeight = Integer.decode(elt.getAttribute("minHeight")).intValue();
						myMinDesiredWidth = Integer.decode(elt.getAttribute("minDesiredWidth")).intValue();
						myMinDesiredHeight = Integer.decode(elt.getAttribute("minDesiredHeight")).intValue();
					} else if (elt.getTagName().equals("cdrectangleparam")) {
						myMinX = Integer.decode(elt.getAttribute("minX")).intValue();
						myMaxX = Integer.decode(elt.getAttribute("maxX")).intValue();
						myMinY = Integer.decode(elt.getAttribute("minY")).intValue();
						myMaxY = Integer.decode(elt.getAttribute("maxY")).intValue();
					} else if (elt.getTagName().equals("infoparam")) {
						myName = elt.getAttribute("name");
						myValue = elt.getAttribute("value");
					} else if (elt.getTagName().equals("hidden")) {
						hidden = elt.getAttribute("value").equals("true");
					} else if (elt.getTagName().equals("TGConnectingPoint")) {
						x = Integer.decode(elt.getAttribute("num")).intValue();
						y = Integer.decode(elt.getAttribute("id")).intValue() + decId;
						tgcpList.add(new Point(x, y));
					} else if (elt.getTagName().equals("father")) {
						fatherId = Integer.decode(elt.getAttribute("id")).intValue();
						fatherNum = Integer.decode(elt.getAttribute("num")).intValue();
					} else if (elt.getTagName().equals("prejavacode")) {
						pre += elt.getAttribute("value") + "\n";
					} else if (elt.getTagName().equals("postjavacode")) {
						post += elt.getAttribute("value") + "\n";
					} else if (elt.getTagName().equals("InternalComment")) {
						internalComment += elt.getAttribute("value") + "\n";
					} else if (elt.getTagName().equals("accessibility")) {
						accessibility = true;
					}else if (elt.getTagName().equals("breakpoint")) {
						breakpoint = true;
					}
				}
			}

			if ((myId == -1) || (myX == -1) || (myY == -1) || (myWidth == -1) || (myHeight == -1)) {
				throw new MalformedModelingException();
			}

			//System.out.println("Making TGComponent of type " + myType + " and of name " + myName);
			//TGComponent is ready to be built
			if(fatherId != -1) {
				fatherId += decId;
				// internal component
				//System.out.println("I am " + myName);
				//System.out.println("Searching for component with id " + fatherId);
				father = tdp.findComponentWithId(fatherId);
				if (father == null) {
					throw new MalformedModelingException();
				}

				//System.out.println("Done");
				//System.out.println("My value is " + father.getValue());
				//System.out.println("My class is " + father.getClass());

				//System.out.println("Searching for component " + fatherNum + " at " + tgc.getName());
				tgc = father.getInternalTGComponent(fatherNum);

				if (tgc == null) {
					// to be added to its father -> swallow component
					if (father instanceof SwallowTGComponent) {
						tgc = TGComponentManager.addComponent(myX, myY, myType, tdp);
						if (tgc instanceof SwallowedTGComponent) {
							((SwallowTGComponent)father).addSwallowedTGComponent(tgc, myX, myY);
						} else {
							throw new MalformedModelingException();
						}
					} else {
						throw new MalformedModelingException();
					}
				}

				if (tgc != null) {
					tgc.setCdRectangle(myMinX, myMaxX, myMinY, myMaxY);
					tgc.setCd(myX, myY);
					//System.out.println("set cd of " + tgc.getName());
				}
			} else {
				tgc = TGComponentManager.addComponent(myX, myY, myType, tdp);
			}
			//System.out.println("TGComponent built " + myType);

			if (tgc == null) {
				throw new MalformedModelingException();
			}

			if (myName != null) {
				tgc.setName(myName);
			}
			
			tgc.setHidden(hidden);

			/*if (tgc instanceof TCDTObject) {
                System.out.println("Loading " + myValue);
            }*/

			String oldClassName = myValue;
			//System.out.println("Old class name=" + oldClassName);
			//Added by Solange
			if ((myValue != null) && (!myValue.equals(null))){
				if (tgc instanceof ProCSDComponent)
				{
					//Added by Solange
					//And removed by emil 
					//myValue=generateNameIfInUse(myValue); 
					//  tgc.setValueWithChange(myValue);
					//System.out.println("myValue=" + myValue);
				}
				//until here            	           	           		
				if ((tgc instanceof TCDTClass) && (decId >0)){
					if (tdp.isAlreadyATClassName(myValue)) {
						myValue = tdp.findTClassName(myValue+"_");
					}
				}
				if ((tgc instanceof TMLTaskOperator) && (decId >0)){
					if (tdp.isAlreadyATMLTaskName(myValue)) {
						myValue = tdp.findTMLTaskName(myValue+"_");
					}
				}
				
				if ((tgc instanceof TMLCPrimitiveComponent) && (decId >0)){
					if (tdp.isAlreadyATMLPrimitiveComponentName(myValue)) {
						myValue = tdp.findTMLPrimitiveComponentName(myValue+"_");
					}
					//System.out.println("MyValue=" + myValue);
				}
				if ((tgc instanceof TOSClass) && (decId >0)){
					if (tdp.isAlreadyATOSClassName(myValue)) {
						myValue = tdp.findTOSClassName(myValue+"_");
					}
				}
				//System.out.println("myValue=" + myValue);
				tgc.setValueWithChange(myValue);
				//System.out.println("value done");
				if ((tgc instanceof TCDTClass) && (decId >0)){
					loadActivityDiagram(tdp, oldClassName, myValue);
				}

				if ((tgc instanceof TMLTaskOperator) && (decId >0)){
					//System.out.println("Going to load ad of task " + oldClassName + " myValue=" + myValue);
					loadTMLActivityDiagram(tdp, oldClassName, myValue);
				}
				
				if ((tgc instanceof TMLCPrimitiveComponent) && (decId >0)){
					//System.out.println("Going to load ad of component " + oldClassName + " myValue=" + myValue);
					loadTMLActivityDiagram(tdp, oldClassName, myValue);
				}
			}

			tgc.forceId(myId);
			tgc.setLoaded(true);
			tgc.setInternalLoaded(false);
			tgc.setMinSize(myMinWidth, myMinHeight);
			tgc.setMinDesiredSize(myMinDesiredWidth, myMinDesiredHeight);
			tgc.resize(myWidth, myHeight);
			tgc.hasBeenResized();
			
			//System.out.println("Options set");

			if (pre.compareTo("") != 0) {
				tgc.setPreJavaCode(pre);
			}
			if (post.compareTo("") != 0) {
				tgc.setPostJavaCode(post);
			}
			
			if (internalComment.compareTo("") != 0) {
				tgc.setInternalComment(internalComment);
			}
			
			if (accessibility) {
				tgc.setCheckableAccessibility(accessibility);
			}
			
			if (breakpoint) {
				tgc.setBreakpoint(breakpoint);
			}

			//extra param
			//System.out.println("Extra params");
			//System.out.println("My value = " + tgc.getValue());
			tgc.loadExtraParam(elt1.getElementsByTagName("extraparam"), decX, decY, decId);
			//System.out.println("Extra param ok");

			if ((tgc instanceof TCDTObject) && (decId > 0)) {
				TCDTObject to = (TCDTObject)tgc;
				//System.out.println("Setting TObject name to: " + to.getObjectName());
				//System.out.println("Setting TObject name to: " + tdp.findTObjectName(to.getObjectName()));
				to.setObjectName(tdp.findTObjectName(to.getObjectName()));
			}

			//System.out.println(tgc.toString());

			//System.out.println("Making connecting points " + tgcpList.size());
			for(i=0; i<tgcpList.size(); i++) {
				p = (Point)(tgcpList.elementAt(i));
				if (!tgc.setIdTGConnectingPoint(p.x, p.y)) {
					//System.out.println("Warning: a connecting point has been removed");
					//throw new MalformedModelingException();
				}
			}

			//System.out.println("Not yet except!");
			if (decId >0) {
				tdp.bringToFront(tgc);
			}
			//System.out.println("Connecting points done " + myType);

			/*if (tgc instanceof TCDTObject) {
                System.out.println("getValue " + tgc.getValue());
            }*/

		} catch (Exception e) {
			System.out.println("Exception XML Component " + e.getMessage());
			throw new MalformedModelingException();
		}
		return tgc;
	}

	//method added by Solange
	    
	public String generateNameIfInUse(String myName)
	{
		if (!(mgui.getCurrentTURTLEPanel().nameInUse(myName)))
		{
			return myName;
		}

		String other;
		for(int w=0; w<100000; w++)
		{
			other = myName + "_" + w;
			if (!(mgui.getCurrentTURTLEPanel().nameInUse(other)))
			{
				return other;
			}
		}
		return null;
	}
	//until here 	

	public void makePostProcessing(TDiagramPanel tdp) throws MalformedModelingException{
		//System.out.println("Make post processing!");
		if (tdp instanceof TClassDiagramPanel) {
			((TClassDiagramPanel)tdp).makePostLoadingProcessing();
		}
		//System.out.println("Post processing is over");
	}

	public void makeXMLConnectors(NodeList nl, TDiagramPanel tdp) throws SAXException, MalformedModelingException {
		Node n;
		//Element elt;
		TGConnector tgco = null;
		int i;

		if (tdp == null) {
			throw new MalformedModelingException();
		}

		for(i=0; i<nl.getLength(); i++) {
			n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				tgco = makeXMLConnector(n, tdp);
				if (tgco != null) {
					tdp.addBuiltConnector(tgco);
				} else {
					System.out.println("Connector error");
					throw new MalformedModelingException();
				}
			}
		}
	}

	public void connectConnectorsToRealPoints(TDiagramPanel tdp) throws MalformedModelingException {
		LinkedList list = tdp.getComponentList();
		TGConnectingPoint p1, p2, p3, p4;
		//TGConnectingPointTmp p;
		int i;
		TGComponent tgc;
		//TGComponent tgctmp;
		TGConnector tgco = null;
		//boolean error = false;
		TGConnectorInfo tgcoinfo;
		

		//connect connectors to their real connecting point
		//System.out.println("Valid connectors ?");
		for(i=0; i<list.size(); i++) {
			tgc = (TGComponent)(list.get(i));
			if (tgc instanceof TGConnector) {
				tgco = (TGConnector)tgc;
				p1 = tgco.getTGConnectingPointP1();
				p2 = tgco.getTGConnectingPointP2();
				if ((p1 instanceof TGConnectingPointTmp) && (p2 instanceof TGConnectingPointTmp)){
					//System.out.println("Searching for id " + p1.getId());
					p3 = tdp.findConnectingPoint(p1.getId());
					//System.out.println("Searching for id " + p2.getId());
					p4 = tdp.findConnectingPoint(p2.getId());
					if (((p3 ==null) || (p4 == null)) &&(decId != 0)) {
						if (list.remove(tgc)) {
							i --;
						} else {
							throw new MalformedModelingException();
						}
					} else {
						if ((p3 == null) ||(p4 == null)) {
							//warning = true;
							if (p3 == null) {
								//System.out.println("Error on first id");
							}
							if (p4 == null) {
								//System.out.println("Error on second id");
							}
							tgcoinfo = new TGConnectorInfo();
							tgcoinfo.connector = tgco;
							pendingConnectors.add(tgcoinfo);
							System.out.println("One connector added to pending list");
						} else {
							tgco.setP1(p3);
							p3.setFree(false);
							tgco.setP2(p4);
							p4.setFree(false);
						}
					}
				}
			}
		}
		
		/*for(TGConnector con: connectorsToRemove) {
			list.remove(con);
		}*/
		
		/*if (error) {
			System.out.println("Connecting error: " + connectorsToRemove.size()  + " connectors have been removed");
			throw new MalformedModelingException();
		}*/
	}
	
	public void makeLastLoad() {
		// Update references on all diagrams
		//System.out.println("Updating ports");
		//mgui.updateAllPorts();
		
		// Update ports on all diagrams
		System.out.println("Updating references / ports");
		mgui.updateAllReferences();
		
		mgui.updateAllPorts();
		
		System.out.println("Pending connectors");
		// Make use of pending connectors
		TGConnectingPoint p1, p2, p3, p4;
		TDiagramPanel tdp;
		TGConnector tgco;
		for(TGConnectorInfo info: pendingConnectors) {
			tgco = info.connector;
			if (tgco != null) {
				tdp = tgco.getTDiagramPanel();
				if (tdp != null) {
					p1 = tgco.getTGConnectingPointP1();
					p2 = tgco.getTGConnectingPointP2();
					if ((p1 instanceof TGConnectingPointTmp) && (p2 instanceof TGConnectingPointTmp)){
						System.out.println("Searching for id " + p1.getId());
						p3 = tdp.findConnectingPoint(p1.getId());
						System.out.println("Searching for id " + p2.getId());
						p4 = tdp.findConnectingPoint(p2.getId());
						if ((p3 == null) ||(p4 == null)) {
							//warning = true;
							if (p3 == null) {
								System.out.println("Error on first id");
							}
							if (p4 == null) {
								System.out.println("Error on second id");
							}
							System.out.println("One connector ignored");
						} else {
							tgco.setP1(p3);
							p3.setFree(false);
							tgco.setP2(p4);
							p4.setFree(false);
						}
					}
				}
			}
		}
		pendingConnectors.clear();
		System.out.println("Last load done");
	}

	public TGConnector makeXMLConnector(Node n, TDiagramPanel tdp) throws SAXException, MalformedModelingException {
		Element elt, elt1;
		TGConnector tgco = null;
		//TGComponent tgc = null;

		//System.out.println(n.toString());

		try {

			NodeList nl = n.getChildNodes();
			elt = (Element)n;
			elt1 = elt;

			int myType = Integer.decode(elt.getAttribute("type")).intValue();
			int myId = Integer.decode(elt.getAttribute("id")).intValue() + decId;

			int myX = -1, myY = -1, myWidth = -1, myHeight =-1;
			int myMinWidth = -1, myMinHeight = -1, myMinDesiredWidth = -1, myMinDesiredHeight = -1;
			int myMaxWidth = -1, myMaxHeight = -1;
			String myName = null, myValue = null;
			int tmpx, tmpy, tmpid;
			TGConnectingPoint p1 = null, p2=null;
			Vector pointList = new Vector();

			Vector tgcpList = new Vector();
			Point p;
			int i, x, y;
			//int fatherId = -1, fatherNum = -1;
			boolean automaticDrawing = true;

			for(i=0; i<nl.getLength(); i++) {
				n = nl.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element) n;
					if (elt.getTagName().equals("cdparam")) {
						myX = Integer.decode(elt.getAttribute("x")).intValue() + decX;
						myY = Integer.decode(elt.getAttribute("y")).intValue() + decY;
					} else if (elt.getTagName().equals("sizeparam")) {
						myWidth = Integer.decode(elt.getAttribute("width")).intValue();
						myHeight = Integer.decode(elt.getAttribute("height")).intValue();
						myMinWidth = Integer.decode(elt.getAttribute("minWidth")).intValue();
						myMinHeight = Integer.decode(elt.getAttribute("minHeight")).intValue();
						if ((elt.getAttribute("maxWidth") != null) && (elt.getAttribute("maxWidth").length() > 0)) { // Test is made for compatibility with old versions
							//System.out.println("maxWidth = " +  elt.getAttribute("maxWidth"));
							myMaxWidth = Integer.decode(elt.getAttribute("maxWidth")).intValue();
							myMaxHeight = Integer.decode(elt.getAttribute("maxHeight")).intValue();
						}
						myMinDesiredWidth = Integer.decode(elt.getAttribute("minDesiredWidth")).intValue();
						myMinDesiredHeight = Integer.decode(elt.getAttribute("minDesiredHeight")).intValue();
					} else if (elt.getTagName().equals("infoparam")) {
						myName = elt.getAttribute("name");
						myValue = elt.getAttribute("value");
					} else if (elt.getTagName().equals("P1")) {
						tmpx = Integer.decode(elt.getAttribute("x")).intValue() + decX;
						tmpy = Integer.decode(elt.getAttribute("y")).intValue() + decY;
						tmpid = Integer.decode(elt.getAttribute("id")).intValue() + decId;
						TGComponent tgc1 = TGComponentManager.addComponent(tmpx, tmpy, TGComponentManager.TAD_START_STATE, tdp);
						p1 = new TGConnectingPointTmp(tgc1, tmpx, tmpy, tmpid);
						//System.out.println("P1id = " + tmpid);
					} else if (elt.getTagName().equals("P2")) {
						tmpx = Integer.decode(elt.getAttribute("x")).intValue() + decX;
						tmpy = Integer.decode(elt.getAttribute("y")).intValue() + decY;
						tmpid = Integer.decode(elt.getAttribute("id")).intValue() + decId;
						TGComponent tgc2 = TGComponentManager.addComponent(tmpx, tmpy, TGComponentManager.TAD_START_STATE, tdp);
						p2 = new TGConnectingPointTmp(tgc2, tmpx, tmpy, tmpid);
						//System.out.println("P2id = " + tmpid);
					} else if (elt.getTagName().equals("Point")) {
						tmpx = Integer.decode(elt.getAttribute("x")).intValue() + decX;
						tmpy = Integer.decode(elt.getAttribute("y")).intValue() + decY;
						pointList.add(new Point(tmpx, tmpy));
					} else if (elt.getTagName().equals("TGConnectingPoint")) {
						x = Integer.decode(elt.getAttribute("num")).intValue();
						y = Integer.decode(elt.getAttribute("id")).intValue() + decId;
						tgcpList.add(new Point(x, y));
						//System.out.println(" adding Connecting point !");
					} else if (elt.getTagName().equals("AutomaticDrawing")) {
						//System.out.println("AutomaticDrawing=" + elt.getAttribute("data"));
						if (elt.getAttribute("data").compareTo("true") == 0) {
							automaticDrawing = true;
							//System.out.println("set to true");
						} else {
							automaticDrawing = false;
						}
						//automaticDrawing = Boolean.getBoolean(elt.getAttribute("data"));
					}
				}
			}

			if ((myType == -1) || (myId == -1) || (myX == -1) || (myY == -1) || (myWidth == -1) || (myHeight == -1) || (p1 == null) || (p2 == null)) {
				throw new MalformedModelingException();
			}

			//TGConnector is ready to be built
			//System.out.println("Making TGConnector of type " + myType);
			tgco = TGComponentManager.addConnector(myX, myY, myType, tdp, p1, p2, pointList);
			//System.out.println("TGConnector built " + myType);

			if (tgco == null) {
				throw new MalformedModelingException();
			}

			tgco.setAutomaticDrawing(automaticDrawing);

			if (myName != null) {
				tgco.setName(myName);
			}

			if ((myValue != null) && (!myValue.equals(null))){
				tgco.setValueWithChange(myValue);
			}

			tgco.forceId(myId);
			tgco.setLoaded(true);
			tgco.setInternalLoaded(false);
			tgco.setMinSize(myMinWidth, myMinHeight);
			tgco.setMaxSize(myMaxWidth, myMaxHeight);
			tgco.setMinDesiredSize(myMinDesiredWidth, myMinDesiredHeight);
			tgco.resize(myWidth, myHeight);

			tgco.loadExtraParam(elt1.getElementsByTagName("extraparam"), decX, decY, decId);

			//System.out.println("Making connecting points " + myType);
			for(i=0; i<tgcpList.size(); i++) {
				p = (Point)(tgcpList.elementAt(i));
				if (!tgco.setIdTGConnectingPoint(p.x, p.y)) {
					throw new MalformedModelingException();
				}
			}

			if (decId >0) {
				tdp.bringToFront(tgco);
			}

			//System.out.println("Connecting points done " + myType);

		} catch (Exception e) {
			System.out.println("Exception generale connector");
			throw new MalformedModelingException();
		}
		return tgco;
	}



	public boolean buildTURTLEModelingFromAnalysis(AnalysisPanel ap) throws AnalysisSyntaxException {

		HMSC h;
		//listE = new CorrespondanceTGElement();
		checkingErrors = new Vector();

		AnalysisPanelTranslator apt = new AnalysisPanelTranslator(ap, mgui);

		try {
			h = apt.translateHMSC();
			listE = apt.getCorrespondanceTGElement();
			checkingErrors = apt.getErrors();
			apt.translateMSCs(h);
			listE = apt.getCorrespondanceTGElement();
			checkingErrors = apt.getErrors();
		} catch (AnalysisSyntaxException ase) {
			CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, ase.getMessage());
			checkingErrors.add(ce);
			throw ase;
		}

		SDTranslator sd = new SDTranslator(h);
		checkingErrors = null;
		warnings = new Vector();
		//System.out.println("Step 02");

		mgui.setMode(mgui.VIEW_SUGG_DESIGN_KO);

		//System.out.println("Step 1");
		try {
			tm = sd.toTURTLEModeling();
			tmState = 0;
		} catch (SDTranslationException e) {
			checkingErrors = new Vector();
			CheckingError error = new CheckingError(CheckingError.STRUCTURE_ERROR, e.getMessage());
			checkingErrors.add(error);

			throw new AnalysisSyntaxException("Problem during translation to a design TURTLE modeling");
		}

		//System.out.println("Step 2");

		if (checkingErrors != null) {
			return false;
		}

		// modeling is built
		// Now check it !
		//System.out.println("Step 3");
		TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
		checkingErrors = tmc.syntaxAnalysisChecking();
		//System.out.println("Step 4");

		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
			return false;
		} else {
			mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_OK);
			return true;
		}
	}

	public void generateDesign() {
		generateDesign(tm);
	}

	public void generateDesign(TURTLEModeling tm) {
		//TURTLEPanel tp = mgui.getCurrentTURTLEPanel();
		nbSuggestedDesign ++;
		TURTLEModelingDrawer tmd = new TURTLEModelingDrawer(mgui);
		tmd.setTURTLEModeling(tm);
		tmd.draw(nbSuggestedDesign);
		mgui.changeMade(null, -1);
	}
	

	public boolean translateDeployment(DeploymentPanel dp) {
		// Builds a TURTLE modeling from a deployment diagram
		System.out.println("deployement");
		checkingErrors = new Vector();
		warnings = new Vector();
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		tm = new TURTLEModeling();
		tmState = 0;
		listE = new CorrespondanceTGElement();
		mgui.reinitCountOfPanels();

		Vector tclasses;

		LinkedList ll;
		ListIterator iterator, iterator2;

		// First step: adding all necessary classes + their ad
		ll = dp.tddp.getListOfNodes();
		iterator = ll.listIterator();
		TDDNode node;
		Vector artifacts;
		TDDArtifact art;
		int i;
		DesignPanel dp2;
		TGComponent tgc;
		//TCDTClass tc;
		String name;
		TClass t;
		DesignPanelTranslator dpt;

		// Loop on nodes
		while(iterator.hasNext()) {
			node = (TDDNode)(iterator.next());

			// Loop on artifact
			artifacts = node.getArtifactList();
			for(i=0; i<artifacts.size(); i++) {
				art = (TDDArtifact)(artifacts.elementAt(i));
				dp2 = art.getDesignPanel();

				iterator2 = dp2.tcdp.getComponentList().listIterator();
				tclasses = new Vector();
				while(iterator2.hasNext()) {
					tgc = (TGComponent)(iterator2.next());
					if (tgc instanceof TClassInterface) {
						System.out.println("Found tclass: " + tgc.getValue());
						tclasses.add(tgc);
					}
				}
				if (tclasses.size() > 0) {
					name = node.getNodeName() + "__" + art.getValue() + "__";
					dpt = new DesignPanelTranslator(dp2);
					dpt.addTClasses(dp2, tclasses, name,tm);
					dpt.addRelations(dp2, name,tm);

					listE.merge(dpt.getCorrespondanceTGElement());
					checkingErrors.addAll(dpt.getErrors());

					// Set package name of tclasses
					for(int j=0; j<tclasses.size(); j++) {
						tgc = (TGComponent)(tclasses.elementAt(j));
						t = listE.getTClass(tgc);
						if (t != null) {
							System.out.println("Setting package name of " + t.getName() + " to " + node.getNodeName());
							t.setPackageName(node.getNodeName()+"_"+art.getValue());
						}
					}
				}
			}
		}

		// Second step : dealing with links!

		DDTranslator ddt = new DDTranslator(dp, tm, listE);

		try {
			System.out.println("Dealing with links!");
			ddt.translateLinks();
		} catch (DDSyntaxException e) {
			//throw new AnalysisSyntaxException("Problem during translation to a design TURTLE modeling");
			System.out.println("Error during translation: " + e.getMessage());
			return false;
		}

		mgui.setMode(MainGUI.GEN_DESIGN_OK);

		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		}

		// modeling is built
		// Now check it !
		TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
		checkingErrors = tmc.syntaxAnalysisChecking();

		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {
			mgui.setMode(MainGUI.GEN_DESIGN_OK);
			return true;
		}
	}
	
	public boolean translateNC(NCPanel ncp) {
		System.out.println("Translating NC");
		checkingErrors = new Vector();
		warnings = new Vector();
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		
		GNCModeling gncm = new GNCModeling(ncp);
		ncs = gncm.translateToNCStructure();
		listE = gncm.getCorrespondanceTable();
		
		checkingErrors = gncm.getCheckingErrors();
		warnings = gncm.getCheckingWarnings();
		
		System.out.println("errors:" + checkingErrors.size() + " warnings:" + warnings.size());
		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {
			// Generate XML file
			System.out.println("Saving in nc.xml file");
			try {
				FileUtils.saveFile("nc.xml", ncs.toXML());
				System.out.println("Save done");
			} catch (FileException fe) {
				System.out.println("Could not save in file:" + fe.getMessage());
			}
			mgui.setMode(MainGUI.NC_OK);
			return true;
		}
		
	}


	public boolean translateTMLDesign(Vector tasksToTakeIntoAccount, TMLDesignPanel tmldp, boolean optimize) {
		ArrayList<TMLError> warningsOptimize = new ArrayList<TMLError>();
		warnings = new Vector();
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		//System.out.println("New TML Modeling");
		GTMLModeling gtmlm = new GTMLModeling(tmldp, true);
		gtmlm.setTasks(tasksToTakeIntoAccount);
		tmlm = gtmlm.translateToTMLModeling(true);
		artificialtmap = tmlm.getDefaultMapping();
		tmap = null;
		listE = gtmlm.getCorrespondanceTable();
		
		
		
		//System.out.println("TML Modeling translated");
		//System.out.println("----- TML Modeling -----");
		//System.out.println(tmlm.toString());
		//System.out.println("------------------------");
		checkingErrors = gtmlm.getCheckingErrors();

		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {
			if (optimize) {
				warningsOptimize = tmlm.optimize();
			}
			
			TML2TURTLE tt = new TML2TURTLE(tmlm);
			tm = tt.generateTURTLEModeling();
			tmState = 0;
			System.out.println("tm generated:");
			//tm.print();
			checkingErrors = tt.getCheckingErrors();
			if ((checkingErrors != null) && (checkingErrors.size() > 0)){
				return false;
			} else {
				// Optimize
				//System.out.println("Optimize");
				tm.optimize();
				//System.out.println("Optimize done");
				TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
				checkingErrors = tmc.syntaxAnalysisChecking();
				if ((checkingErrors != null) && (checkingErrors.size() > 0)){
					return false;
				} else {
					warnings = gtmlm.getCheckingWarnings();
					warnings.addAll(tmc.getWarnings());
					warnings.addAll(convertToCheckingErrorTMLErrors(warningsOptimize, tmldp.tmltdp));
					mgui.resetAllDIPLOIDs();
					listE.useDIPLOIDs();
					mgui.setMode(MainGUI.GEN_DESIGN_OK);
					return true;
				}
			}
		}
	}
	
	public Vector convertToCheckingErrorTMLErrors(ArrayList<TMLError> warningsOptimize, TDiagramPanel _tdp) {
		Vector v = new Vector();
		CheckingError warning;
		for(TMLError error: warningsOptimize) {
			warning = new CheckingError(CheckingError.BEHAVIOR_ERROR, error.message);
			warning.setTDiagramPanel(_tdp);
			warning.setTMLTask(error.task);
			v.add(warning);
		}
		return v;
	}
	
	public boolean translateTMLComponentDesign(Vector componentsToTakeIntoAccount, TMLComponentDesignPanel tmlcdp, boolean optimize) {
		ArrayList<TMLError> warningsOptimize = new ArrayList<TMLError>();
		warnings = new Vector();
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		//System.out.println("New TML Component Modeling");
		GTMLModeling gctmlm = new GTMLModeling(tmlcdp, true);
		gctmlm.setComponents(componentsToTakeIntoAccount);
		tmlm = gctmlm.translateToTMLModeling(true);
		artificialtmap = tmlm.getDefaultMapping();
		tmap = null;
		listE = gctmlm.getCorrespondanceTable();
		//listE.useDIPLOIDs();
		//System.out.println("TML Modeling translated");
		//System.out.println("----- TML Modeling -----");
		//System.out.println(tmlm.toString());
		//System.out.println("------------------------");
		checkingErrors = gctmlm.getCheckingErrors();

		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {
			if (optimize) {
				warningsOptimize = tmlm.optimize();
			}
			
			TML2TURTLE tt = new TML2TURTLE(tmlm);
			tm = tt.generateTURTLEModeling();
			tmState = 0;
			System.out.println("tm generated:");
			//tm.print();
			checkingErrors = tt.getCheckingErrors();
			if ((checkingErrors != null) && (checkingErrors.size() > 0)){
				return false;
			} else {
				// Optimize
				//System.out.println("Optimize");
				tm.optimize();
				//System.out.println("Optimize done");
				TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
				checkingErrors = tmc.syntaxAnalysisChecking();
				if ((checkingErrors != null) && (checkingErrors.size() > 0)){
					return false;
				} else {
					warnings = gctmlm.getCheckingWarnings();
					warnings.addAll(tmc.getWarnings());
					warnings.addAll(convertToCheckingErrorTMLErrors(warningsOptimize, tmlcdp.tmlctdp));

					mgui.setMode(MainGUI.GEN_DESIGN_OK);
					mgui.resetAllDIPLOIDs();
					listE.useDIPLOIDs();
					return true;
				}
			}
		}
	}
	
	public boolean checkSyntaxTMLMapping(Vector nodesToTakeIntoAccount, TMLArchiPanel tmlap, boolean optimize) {
		ArrayList<TMLError> warningsOptimize = new ArrayList<TMLError>();		
		warnings = new Vector();
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		//System.out.println("New TML Mapping");
		GTMLModeling gtmlm = new GTMLModeling(tmlap, true);
		
		gtmlm.setNodes(nodesToTakeIntoAccount);
		tmlm = null;
		tm = null;
		tmState = 1;
		tmap = gtmlm.translateToTMLMapping();
		
		listE = gtmlm.getCorrespondanceTable();
		
		checkingErrors = gtmlm.getCheckingErrors();
		
		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {
			if (optimize) {
				warningsOptimize = tmap.optimize();
			}
			warnings.addAll(convertToCheckingErrorTMLErrors(warningsOptimize, tmlap.tmlap));
			mgui.resetAllDIPLOIDs();
			listE.useDIPLOIDs();
			mgui.setMode(MainGUI.GEN_DESIGN_OK);
			return true;
		}
	}
	
	public boolean translateTMLMapping(boolean _sample, boolean _channel, boolean _event, boolean _request, boolean _exec, boolean _busTransfers, boolean _scheduling, boolean _taskState, boolean _channelState, boolean _branching, boolean _terminateCPU, boolean _terminateCPUs, boolean _clocked, String _tickValue, boolean _endClocked, boolean _countTick, boolean _maxCountTick, String _maxCountTickValue, boolean _randomTask) {
		//System.out.println("TML=" + tmap.toString());
		Mapping2TIF m2tif = new Mapping2TIF(tmap);
		m2tif.setShowSampleChannels(_sample);
		m2tif.setShowChannels(_channel);
		m2tif.setShowEvents(_event);
		m2tif.setShowRequests(_request);
		m2tif.setShowExecs(_exec);
		m2tif.setShowBusTransfers(_busTransfers);
		m2tif.setShowScheduling(_scheduling);
		m2tif.setIsClocked(_clocked);
		m2tif.setTickValue(_tickValue);
		m2tif.setIsEndClocked(_endClocked);
		m2tif.setIsCountTick(_countTick);
		m2tif.hasMaxCountTick(_maxCountTick);
		if (_maxCountTick) {
			m2tif.setMaxCountTickValue(_maxCountTickValue);
		}
		m2tif.setShowTaskState(_taskState);
		m2tif.setShowChannelState(_channelState);
		m2tif.setShowBlockedCPU(_terminateCPU);
		m2tif.setShowTerminateCPUs(_terminateCPUs);
		m2tif.setShowBranching(_branching);
		m2tif.setRandomTasks(_randomTask);
		tm = m2tif.generateTURTLEModeling();
		//StringBuffer sb = tm.printToStringBuffer();
		//System.out.println("tm=" + sb);
		
		
		
		return true;
		/*tmState = 1;
		System.out.println("tm generated from TMAP");
		checkingErrors = m2tif.getCheckingErrors();
		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {
			// Optimize
			System.out.println("Optimize TIF");
			return true;
			/*tm.optimize();
			//System.out.println("Optimize done");
			//tm.print();
			TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
			checkingErrors = tmc.syntaxAnalysisChecking();
			if ((checkingErrors != null) && (checkingErrors.size() == 0)){
				mgui.setMode(MainGUI.GEN_DESIGN_OK);
				//System.out.println("true");
				return true;				
			} else {
				System.out.println("false");
				return false;
			}
		}*/
	}

	//Added by Solange
	public void generateLists(ProactiveDesignPanel pdp) {
		gpdtemp = new GProactiveDesign(pdp);	
	}
	//

	public boolean translateTURTLEOSDesign(TURTLEOSDesignPanel tosdp) {
		warnings = new Vector();
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		//System.out.println("New TML Modeling");
		GTURTLEOSModeling gosm = new GTURTLEOSModeling(tosdp);
		//gtmlm.setTasks(tasksToTakeIntoAccount);
		//tmlm = gosm.translateToTMLModeling();
		//System.out.println("TML Modeling translated");
		//System.out.println("----- TML Modeling -----");
		//System.out.println(tmlm.toString());
		//System.out.println("------------------------");
		tm = gosm.generateTURTLEModeling();
		tmState = 0;
		checkingErrors = gosm.getCheckingErrors();

		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {

			//System.out.println("Optimize");
			tm.optimize();
			//System.out.println("Optimize done");
			TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
			checkingErrors = tmc.syntaxAnalysisChecking();
			if ((checkingErrors != null) && (checkingErrors.size() > 0)){
				return false;
			} else {
				warnings = gosm.getCheckingWarnings();
				warnings.addAll(tmc.getWarnings());
				mgui.setMode(MainGUI.GEN_DESIGN_OK);
				return true;
			}
		}
	}


	public boolean translateProactiveDesign(ProactiveDesignPanel pdp) {
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		GProactiveDesign gpd = new GProactiveDesign(pdp);

		tm = gpd.generateTURTLEModeling();
		tmState = 0;

		if (gpd.checkSyntax() == false) {
			System.out.println("Errors found");
			warnings = gpd.getCheckingWarnings();
			checkingErrors = gpd.getCheckingErrors();
			return false;
		}
		TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
		checkingErrors = tmc.syntaxAnalysisChecking();
		warnings = tmc.getWarnings();
		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {
			//A faire:
				//  tm.optimize();
			//  warnings = gpd.getCheckingWarnings();
			//  warnings.addAll(tmc.getWarnings());
			mgui.setMode(MainGUI.GEN_DESIGN_OK);
			return true;
		}

	}

	// Generates for all observers, a TURTLE modeling for checking it
	public boolean generateTMsForRequirementAnalysis(Vector reqs, RequirementDiagramPanel rdp) {
		rm = new RequirementModeling(reqs, rdp, mgui);
		checkingErrors = rm.getCheckingErrors();
		warnings = rm.getWarnings();
		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {
			//mgui.setMode(mgui.GEN_DESIGN_OK);
			languageID = MATRIX;
			return true;
		}
	}

	public RequirementModeling getRequirementModeling() {
		return rm;
	}
	
	public void removeBreakpoint(Point p) {
		if (listE == null) {
			return;
		}
		
		listE.removeBreakpoint(p);
	}
	
	public void addBreakpoint(Point p) {
		if (listE == null) {
			return;
		}
		
		listE.addBreakpoint(p);
	}

}
