/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 *
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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
 * Class GTURTLEModeling
 * Creation: 09/12/2003
 * Version: 1.1 02/06/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
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
import ui.atd.*;
import ui.cd.*;
import ui.dd.*;
import ui.iod.*;
import ui.ebrdd.*;
import ui.req.*;
import ui.sd.*;
import ui.ucd.*;
import ui.tree.*;
import ui.window.*;
import ui.ConfigurationTTool;

// AVATAR
import ui.avatardd.*;
import ui.avatarbd.*;
import ui.avatarsmd.*;
import ui.avatarrd.*;
import ui.avatarmad.*;
import ui.avatarpd.*;
import ui.avatarcd.*;
import ui.avatarad.*;

import avatartranslator.*;
import avatartranslator.toturtle.*;
import avatartranslator.touppaal.*;
import avatartranslator.toproverif.*;
import avatartranslator.totpn.*;

import tpndescription.*;

import ui.diplodocusmethodology.*;
import ui.avatarmethodology.*;
import ui.tmlad.*;
import ui.tmlcd.*;
import ui.tmlcompd.*;
import ui.tmldd.*;
import ui.tmlcp.*;
import ui.tmlsd.*;
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

import proverifspec.*;

import req.ebrdd.*;

import tmltranslator.ctranslator.*;

//Communication Pattern javaCC parser
//import compiler.tmlCPparser.*;

public class GTURTLEModeling {

    //Added by Solange
	
    public GProactiveDesign gpdtemp;


    //
    private Vector panels; /* analysis, design, deployment, tml design */
    private TURTLEModeling tm;
    private AvatarSpecification avatarspec;
    private AVATAR2UPPAAL avatar2uppaal;
    private AVATAR2ProVerif avatar2proverif;
    private boolean optimizeAvatar;
    private int tmState; // 0:generated, 1: to be generated from mapping, 2: to be generated from TML modeling
    private TMLModeling tmlm;
    private TMLMapping artificialtmap;
    private TMLMapping tmap;
    private TMLCP tmlcp;
    private RequirementModeling rm;
    private NCStructure ncs;
    private MainGUI mgui;
    private CorrespondanceTGElement listE;
    private String rtlotos;
	
    private EBRDD ebrdd;
	
    private UPPAALSpec uppaal;
    private RelationTIFUPPAAL uppaalTIFTable;
    private RelationTMLUPPAAL uppaalTMLTable;
	
    private ProVerifSpec proverif;
	
    private AVATAR2TPN avatar2tpn;
    private TPN tpnFromAvatar;
	
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
    private int nbSuggestedAnalysis;
    private int nbTPN;

    //private ValidationDataTree vdt;
    private SearchTree st;
    private SyntaxAnalysisTree mcvdt;
    private InvariantDataTree idt;

    private Vector checkingErrors;
    private Vector warnings;
	
    private LinkedList<Invariant> invariants;
	
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
    public final static int PROVERIF = 6;
	
    private boolean undoRunning = false;


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
		
	invariants = new LinkedList<Invariant>();

	//vdt = new ValidationDataTree(mgui);
	mcvdt = new SyntaxAnalysisTree(mgui);
	idt = new InvariantDataTree(mgui);
	st = new SearchTree(mgui);

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
	
    public LinkedList<Invariant> getInvariants() {
    	return invariants;
    }
    
    public void addInvariant(Invariant _inv) {
    	invariants.add(_inv);
    	TraceManager.addDev("Adding invariant: " + _inv.toString());
    }
    
    public void clearInvariants() {
    	invariants.clear();
    }
    

	
    public String saveTIF() {
	if (tm == null) {
	    TraceManager.addDev("NO TIF to save");
	    return null;
	}
		
	TIFExchange tif = new TIFExchange();
	tif.setTURTLEModeling(tm);
	String ret = tif.saveInXMLTIF();
	TraceManager.addDev("TIF=\n" +  ret);
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
		TraceManager.addDev("Got TIF");
		generateDesign();
	    }
	} catch (Exception e) {
	    TraceManager.addDev("Exception on TIF: " + e.getMessage());
	}
	return ret;
    }
	
    public boolean openSD(String s) {
	SDExchange sde = new SDExchange();
	boolean ret = false;
		
	try {
	    ret = sde.loadFromXMLSD(s);
	    if (ret) {
		//tm = tif.getTURTLEModeling();
		//tmState = 0;
		TraceManager.addDev("Got SD");
		generateIOD(sde.getHMSC(), sde.getMSC());
	    }
	} catch (Exception e) {
	    TraceManager.addDev("Exception on SD: " + e.getMessage());
	}
	return ret;
    }
	
    /*public void mergeChoices(boolean nonDeterministic) {
      if (tm != null) {
      tm.mergeChoices(nonDeterministic);
      }
      }*/
	
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
	//TraceManager.addDev("generate LOTOS");
        generateLOTOS(f);
    }

    public void generateLOTOS(File f) {
	//tm.print();
	TraceManager.addDev("Generating Lotos");
	TURTLETranslator tt = new TURTLETranslator(tm);
	rtlotos = tt.generateLOTOS(true);
	warnings = tt.getWarnings();
	TraceManager.addDev("Lotos generated");

		
		
	nbRTLOTOS ++;
	if (f != null) {
	    saveInFile(f, rtlotos);
	}
	TraceManager.addDev("LOTOS to file done");
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
	    TraceManager.addDev("Saving in file: " + f);
	    saveInFile(f, tpn);
	}
	languageID = TPN;

	// For debug purpose
	//TraceManager.addDev(tpn);

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

	public boolean generateCcode( String _title )	{

   	CheckingError ce;
   	int type;
   	TGComponent tgc;
		String applicationName;
		TMLCCodeGeneration Ccode;

		if( tmap == null )	{
			JOptionPane.showMessageDialog(mgui.frame, "C code is only generated from an architecture diagram with mapping information", "Code generation failed", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		TraceManager.addDev( "ABOUT TO CHECK ERRORS FOR CODE GENERATION" );
		TMLCCodeGenerationSyntaxCheck syntax = new TMLCCodeGenerationSyntaxCheck( tmap, tmap.getTMLModeling(), tmap.getTMLArchitecture() );
		syntax.check();
		if( syntax.hasErrors() )	{
 			for( TMLCCodeGenerationError error: syntax.getErrors() ) {
				if( error.type == TMLCCodeGenerationError.ERROR_STRUCTURE ) {
					type = CheckingError.STRUCTURE_ERROR;
				}
				else {
					type = CheckingError.BEHAVIOR_ERROR;
				}
				ce = new CheckingError( type, error.message );
				tgc = listE.getTG( error.element );
				if ( tgc != null ) {
					ce.setTDiagramPanel( tgc.getTDiagramPanel() );
					ce.setTGComponent( tgc );
				}
				ce.setTMLTask( error.task );
				checkingErrors.add( ce );
			}
			JOptionPane.showMessageDialog( 	mgui.frame,
																			"The system design contains several errors: the application C code could not be generated",
																			"Code generation failed", JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		applicationName = tmap.getMappedTasks().get(0).getName().split("__")[0];
		Ccode = new TMLCCodeGeneration( _title, applicationName, mgui.frame, mgui.getAllTMLCP(), tmap );
		Ccode.toTextFormat();
		try {
				if( ConfigurationTTool.CcodeDirectory.equals("") )	{
					JOptionPane.showMessageDialog(	mgui.frame,
																					"No directory for C code generation found in config.xml. The C code cannot be generated.",
																					"Code generation failed", JOptionPane.INFORMATION_MESSAGE );
					return false;
				}
				else 	{
			    Ccode.saveFile( ConfigurationTTool.CcodeDirectory + File.separator, applicationName );
					JOptionPane.showMessageDialog( 	mgui.frame, "The application C code has been successfully generated in: "
																					+ ConfigurationTTool.CcodeDirectory + "/", "C code generation successful",
																					JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch( Exception e ) {
		    TraceManager.addError( "Application C files could not be saved: " + e.getMessage() );
		    return false;
		}
		return true;
	}
	
    public boolean generateTMLTxt( String _title ) {

	if( tmlcp != null )	{	//Use the data structure filled by translateToTML... and pass it to the appropriate toTextFormat()
	    TraceManager.addError( "About to generate the TMLText for CPs" );
	    TMLCPTextSpecification specCP = new TMLCPTextSpecification( _title );
			
			//get the architecture panel and the nodes
			TMLArchiDiagramPanel tmlap = mgui.getTMLArchiDiagramPanels().get(0).tmlap;
			LinkedList components = tmlap.getComponentList();
			ListIterator iterator = components.listIterator();
			TGComponent tgc;

			while(iterator.hasNext()) {
				tgc = (TGComponent)(iterator.next());
				if (tgc instanceof TMLArchiCPNode) {
					TMLArchiCPNode node = (TMLArchiCPNode) tgc;
					TraceManager.addDev( "Found CP node: " + node.getName() );
					TraceManager.addDev( "with mapping info: " + node.getMappedUnits() );
				}
			}
			
			ArrayList<TMLCommunicationPatternPanel> tmlcpPanelsList = new ArrayList<TMLCommunicationPatternPanel>();
			//get the TMLCommunicationPatternPanels :)
			for( int i = 0; i < mgui.tabs.size(); i++ )	{
				TURTLEPanel panel = (TURTLEPanel)( mgui.tabs.get(i) );
				if( panel instanceof TMLCommunicationPatternPanel )	{
					tmlcpPanelsList.add( (TMLCommunicationPatternPanel) panel );
					TraceManager.addDev( "Found TMLCommunicationPatternPanel: " + ((TMLCommunicationPatternPanel)panel).toString() );
				}
			}

	    specCP.toTextFormat( tmlcp );	// the data structure tmlcp is filled with the info concerning the CP panel
			// from which the button is pressed. If there are multiple CP panels this operation must be repeated for each panel. It
			// should be no difficult to implement.
	    try	{
				specCP.saveFile( ConfigurationTTool.TMLCodeDirectory + File.separator, "spec.tmlcp" );
	    }
	    catch( Exception e ) {
				TraceManager.addError( "Writing TMLText for CPs, file could not be saved: " + e.getMessage() );
				return false;
	    }
	}
	else	{
	    //This branch is activated if doing the syntax check from the application panel.
	    //It only generates the application TML text
	    if( tmap == null ) {
		TMLTextSpecification spec = new TMLTextSpecification( _title );
		spec.toTextFormat( tmlm );	//TMLModeling
		try {
		    spec.saveFile( ConfigurationTTool.TMLCodeDirectory + File.separator, "spec.tml" );
		}
		catch( Exception e ) {
		    TraceManager.addError( "File could not be saved: " + e.getMessage() );
		    return false;
		}
	    }
	    //This branch is activated if doing the syntax check from the architecture panel.
	    //It generates the text TML for the architecture and the application + mapping information
	    else {
		TMLMappingTextSpecification spec = new TMLMappingTextSpecification( _title );
		spec.toTextFormat( tmap );	//TMLMapping
		try {
		    spec.saveFile( ConfigurationTTool.TMLCodeDirectory + File.separator, "spec" );
		}
		catch( Exception e ) {
		    TraceManager.addError( "Files could not be saved: " + e.getMessage() );
		    return false;
		}
	    }
	}
	return true;	//temporary, just to check functionality
    }
	
    public boolean generateUPPAALFromTIF(String path, boolean debug, int nb, boolean choices, boolean variables) {
	TURTLE2UPPAAL turtle2uppaal = new TURTLE2UPPAAL(tm);
	turtle2uppaal.setChoiceDeterministic(choices);
	turtle2uppaal.setVariablesAsActions(variables);
	uppaal = turtle2uppaal.generateUPPAAL(debug, nb);
	TraceManager.addDev("Building relation table");
	uppaalTIFTable = turtle2uppaal.getRelationTIFUPPAAL();
	TraceManager.addDev("Building relation table done");
	uppaalTMLTable = null;
		
	languageID = UPPAAL;
	mgui.setMode(MainGUI.UPPAAL_OK);
		
	try {
	    TraceManager.addDev("Saving specification in " + path + "\n");
	    turtle2uppaal.saveInFile(path);
	    TraceManager.addDev("UPPAAL specification has been generated in " + path + "\n");
	    return true;
	} catch (FileException fe) {
	    TraceManager.addError("Exception: " + fe.getMessage());
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
	    TraceManager.addError("Exception: " + fe.getMessage());
	    return false;
	}
    }
	
    public boolean generateUPPAALFromAVATAR(String _path) {
	avatar2uppaal = new AVATAR2UPPAAL(avatarspec);
	//tml2uppaal.setChoiceDeterministic(choices);
	//tml2uppaal.setSizeInfiniteFIFO(_size);
	uppaal = avatar2uppaal.generateUPPAAL(true, optimizeAvatar);
	warnings = avatar2uppaal.getWarnings();
	uppaalTMLTable = null;
	uppaalTIFTable = null;
	languageID = UPPAAL;
	mgui.setMode(MainGUI.UPPAAL_OK);
	//uppaalTable = tml2uppaal.getRelationTIFUPPAAL(_debug);
	try {
	    avatar2uppaal.saveInFile(_path);
	    return true;
	} catch (FileException fe) {
	    TraceManager.addError("Exception: " + fe.getMessage());
	    return false;
	}
    }
	
    public AvatarSpecification getAvatarSpecification() {
	return avatarspec;
    }
	
    public boolean generateProVerifFromAVATAR(String _path, boolean _stateReachability, boolean _advancedTranslation) {
	avatar2proverif = new AVATAR2ProVerif(avatarspec);
	//tml2uppaal.setChoiceDeterministic(choices);
	//tml2uppaal.setSizeInfiniteFIFO(_size);
	proverif = avatar2proverif.generateProVerif(true, true, _stateReachability, _advancedTranslation);
	languageID = PROVERIF;
	mgui.setMode(MainGUI.EDIT_PROVERIF_OK);
	//mgui.setMode(MainGUI.MODEL_PROVERIF_OK);
	//uppaalTable = tml2uppaal.getRelationTIFUPPAAL(_debug);
	try {
	    avatar2proverif.saveInFile(_path);
	    TraceManager.addDev("Specification generated in " + _path);
	    return true;
	} catch (FileException fe) {
	    TraceManager.addError("Exception: " + fe.getMessage());
	    return false;
	}
    }
	
    public TPN generateTPNFromAvatar() {
	avatar2tpn = new AVATAR2TPN(avatarspec);
	//tml2uppaal.setChoiceDeterministic(choices);
	//tml2uppaal.setSizeInfiniteFIFO(_size);
	tpnFromAvatar = avatar2tpn.generateTPN(true, true);
	languageID = TPN;
	return tpnFromAvatar;
    }
	
    /*IntMatrix im = tpnFromAvatar.getIncidenceMatrix();
      TraceManager.addDev("Farkas computing on " + im.toString());
      im.Farkas();
      TraceManager.addDev("Farkas done:" + im.toString());
		
		
		
      languageID = TPN;
      mgui.setMode(MainGUI.EDIT_PROVERIF_OK);
      //mgui.setMode(MainGUI.MODEL_PROVERIF_OK);
      //uppaalTable = tml2uppaal.getRelationTIFUPPAAL(_debug);
      return true;
      /*try {
      avatar2tpn.saveInFile(_path);
      TraceManager.addDev("Specification generated in " + _path);
      return true;
      } catch (FileException fe) {
      TraceManager.addError("Exception: " + fe.getMessage());
      return false;
      }*/
		
    public ArrayList<String> getUPPAALQueries() {
	return getUPPAALQueries(mgui.getCurrentTURTLEPanel());
    }
	
    public ArrayList<String> getUPPAALQueries(TURTLEPanel tp) {
	TraceManager.addDev("Searching for queries on " + mgui.getTabName(tp));
	ArrayList<TGComponent> list = new ArrayList<TGComponent>();
	ArrayList<TClass> tclasses;
	tp.getAllCheckableTGComponent(list);
		
	ArrayList<String> listQ = new ArrayList<String>();
		
	if (tp instanceof DesignPanel) {
	    ArrayList<ADComponent> listAD = listE.getADComponentCorrespondance(list);
			
	    //TraceManager.addDev("List size:" + listAD.size());
			
	    if (listAD == null) {
		return null;
	    }
			
	    TClass t;
	    String s;
	    for(ADComponent adc:listAD) {
		if (adc != null) {
		    t = tm.findTClass(adc);
		    //TraceManager.addDev("Found class:" + t.getName());
		    if (t!= null) {
			tclasses = new ArrayList<TClass>();
			tclasses.add(t);
			// For handling tobjects
			tm.addAllTClassesEndingWith(tclasses, "_" + t.getName());
			for(TClass tc: tclasses) {
			    //TraceManager.addDev("Analyzing class:" + tc.getName());
			    s = uppaalTIFTable.getRQuery(tc, adc);
			    if (s != null) {
				//TraceManager.addDev("Adding query:" + s);
				listQ.add(s + "$" + adc);
			    }
			}
		    }
		}
	    }
	} else if ((tp instanceof TMLComponentDesignPanel) || (tp instanceof TMLDesignPanel)) {
	    //TraceManager.addDev("uppaalTMLTable");
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
			    //TraceManager.addDev("Adding query:" + s);
			    listQ.add(s + "$" + elt);
			}
		    }
		}
	    }
			
	} else if ((avatar2uppaal != null) && (tp instanceof AvatarDesignPanel)) {
	    TraceManager.addDev("Making UPPAAL queries");
	    for(TGComponent tgc: list) {
		TraceManager.addDev("Making UPPAAL query for " + tgc);
		String s = avatar2uppaal.getUPPAALIdentification(tgc);
		TraceManager.addDev("Query: " + s);
		if ((s!= null) && (s.length() > 0)) {
		    AvatarBlock block = avatar2uppaal.getBlockFromReferenceObject(tgc);
		    listQ.add(s + "$" + block.getName() + "." + tgc);
		} else {
		    TraceManager.addDev("Could not make query for " + tgc);
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
	    TraceManager.addDev("No cpp files were deleted\n");
	} else {
	    TraceManager.addDev("Files deleted:\n" + list + "\n");
	}

	list = FileUtils.deleteFiles(path, ".x");

	if (list.length() == 0) {
	    TraceManager.addDev("No x files were deleted\n");
	} else {
	    TraceManager.addDev("Files deleted:\n" + list + "\n");
	}

	TML2SystemC tml2systc = new TML2SystemC(tmlm);
	tml2systc.generateSystemC(true);
	//tml2systc.print();
	try {
	    tml2systc.saveFile(path, "appmodel");
	} catch (FileException fe) {
	    TraceManager.addError("File could not be saved (SystemC)");
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
	  TraceManager.addDev("Building graph");
	  graph.buildGraph(rgautproj);
	  TraceManager.addDev("Renaming transitions");
	  graph.renameTransitions();
	  TraceManager.addDev("Merging transitions 23/4=" + (23/4) + "23%4="  + (23%4));
	  graph.mergeWriteTransitions();
	  graph.mergeReadTransitions();
	  graph.removeInternalTransitions();
	  TraceManager.addDev("Printing graph:\n" + graph.toAUTStringFormat());
	  TraceManager.addDev("Splitting transitions");
	  graph.splitTransitions();
	  modifiedaut = graph.toAUTStringFormat();
	  TraceManager.addDev("Printing graph:\n" + modifiedaut);
	  TraceManager.addDev("Translation in DOT format");
		
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
	  TraceManager.addDev("All done");
	  } catch (LauncherException le) {
	  TraceManager.addDev("Error: conversion failed");
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
	
    public String showRGDiplodocus() {
	TraceManager.addDev("Show diplodocus graph located in " + ConfigurationTTool.GGraphPath + "/tree.dot");
	RemoteExecutionThread ret = new RemoteExecutionThread(ConfigurationTTool.DOTTYHost, null, null, ConfigurationTTool.DOTTYPath + " " + ConfigurationTTool.GGraphPath + "/tree.dot");
	ret.start();
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
	TraceManager.addDev("Saving in file " + file.getAbsolutePath() + " size of file=" + s.length());
	//TraceManager.addDev("Length of s=" + s.length());

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
	    TraceManager.addError("Specification could not be saved " + e.getMessage());
	}

	/*try {
	  FileOutputStream fos = new FileOutputStream(file);
	  fos.write(s.getBytes());
	  fos.close();
	  } catch(Exception e) {
	  JOptionPane.showMessageDialog(mgui.frame, "Specification could not be saved " + e.getMessage(), "Lotos File Error", JOptionPane.INFORMATION_MESSAGE);
	  TraceManager.addDev("Specification could not be saved " + e.getMessage());
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
	
    public String getLastProVerifSpecification() {
	if (proverif == null) {
	    return "";
	}
		
	return proverif.getStringSpec();
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
	return panels.size() + 3;
    }

    public Object getChild(int index) {
	if (index < panels.size()) {
	    return panels.elementAt(index);
	} else if (index == panels.size()) {
	    return mcvdt;
	} else if (index == (panels.size() + 1)) {
	    return idt;
	} else {
	    return st;
	}

    }

    public int getIndexOfChild(Object child) {
	int index = panels.indexOf(child);

	if (index > -1) {
	    return index;
	}

	if (child == mcvdt) {
	    return panels.size();
	}
		
	if (child == idt) {
	    return panels.size() + 1;
	}
		
	return panels.size()+2;
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

	//TraceManager.addDev("input data=" + inputData);

	// Fill Hashtable
	TClassAndGateDS tag;
	int j;
	for(int i=0; i<gates.size(); i++) {
	    tag = (TClassAndGateDS)(gates.get(i));
	    //TraceManager.addDev("TClass:" + tag.getTClassName() + " Gate:" + tag.getGateName());
	    //actionName = tag.getGateName();
	    //g = mgm.getGate(tag.getTClassName(), actionName);
	    //TraceManager.addDev("actionName = " + actionName + " gateName = " + g.getName()); 
	    //if (g != null) {
	    //gog = mgm.getGroupOfGatesByGate(g);
	    gog = mgm.groupOf(tag.getTClassName(), tag.getGateName());
	    if (gog != null) {
		//TraceManager.addDev("Found a gog: >" + gog.getMasterGateName() + "<");
		hashtable.put(gog.getMasterGateName().getName(), gog);
		/*for(j=0;j<gog.size();j++) {
		  g = gog.getGateAt(j);
		  TraceManager.addDev("Putting: " + g.getName());
		  hashtable.put(g.getName(), g);
		  }*/
	    }
	    //}
	}

	try {
	    while((s = br.readLine()) != null) {
		/*if (cpt % 10000 == 0) {
		  TraceManager.addDev("cpt=" + cpt);
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
			    TraceManager.addDev("Action = >" + actionName1 + "<");

			    gog = hashtable.get(actionName1);
			    if (gog == null) {
				TraceManager.addDev("Not in hash");
				result.append(makeIAction(s) + "\n");
			    } else {
				TraceManager.addDev("In hash");
				result.append(makeAction(s, actionName) + "\n");
			    }

			    // action to ignored or to project ?
			    /*g = mgm.getGate(actionName1);
			      if (g == null) {
			      //TraceManager.addDev("null1");
			      result.append(makeIAction(s) + "\n");
			      } else {
			      gog = mgm.getGroupOfGatesByGate(g);
			      if (gog == null) {
			      //TraceManager.addDev("null2");
			      result.append(makeIAction(s) + "\n");
			      } else {
			      if (!belongTo(gog, gates)) {
			      // Check if directly a master Gate!
			      // A completer ...
			      //TraceManager.addDev("null3");
			      result.append(makeIAction(s) + "\n");
			      } else {
			      //TraceManager.addDev("action added: " + actionName);
			      result.append(makeAction(s, actionName) + "\n");
			      }
			      }
			      }*/
			}

		    }
		}
	    }
	} catch (Exception e) {
	    TraceManager.addError("Exception during projection" + e.getMessage());
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

	//TraceManager.addDev("input data=" + inputData);

	int cpt1 = 0;

	try {
	    while((s = br.readLine()) != null) {
		cpt1 ++;
		//if (cpt1 % 100000 == 0) {
		//TraceManager.addDev("=" + cpt1 + " / " + transi);
		//}
		if (s.charAt(0) == '(') {
		    index1 = s.indexOf(",");
		    if ((index1 > -1) && ((index1+1) < s.length())) {
			g1 = s.substring(0, index1 + 1);
			s = s.substring(index1+1, s.length());

			//TraceManager.addDev("g1=" + g1 + " s=" + s);

			index2 = s.indexOf(",");
			if ((index2 > -1) && ((index2+1) < s.length())) {
			    g2 = s.substring(index2, s.length());
			    s = s.substring(0, index2);
			    s = s.trim();

			    //TraceManager.addDev("g2=" + g2 + " s=" + s);

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
				//TraceManager.addDev("actionName = " + g.getName());
				/*if (mgm.nbOfPossibleGatesLowerCase(actionName1) > 1) {
				  CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Action " + actionName1 + " has several possible candidates ; " + actionName + " has been chosen");
				  warnings.add(ce);
				  }*/
			    } else {
				TraceManager.addDev("actionName is not in hashtable: ->" + actionName + "<- length=" + actionName.length());
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
		    //TraceManager.addDev("nb of transitions=" + s);
		    transi = Integer.decode(s1).intValue();
		    if (transi > max) {
			return null;
		    }
		    result.append(s + "\n");
		}
	    }
	} catch (Exception e) {
	    TraceManager.addError("Exception convert0" + e.getMessage());
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

      //TraceManager.addDev("input data=" + inputData);

      int cpt1 = 0;

      try {
      while((s = br.readLine()) != null) {
      cpt1 ++;
      if (cpt1 % 100000 == 0) {
      TraceManager.addDev("=" + cpt1 + " / " + transi);
      }
      if (s.charAt(0) == '(') {
      index1 = s.indexOf(",");
      if ((index1 > -1) && ((index1+1) < s.length())) {
      g1 = s.substring(0, index1 + 1);
      s = s.substring(index1+1, s.length());

      //TraceManager.addDev("g1=" + g1 + " s=" + s);

      index2 = s.indexOf(",");
      if ((index2 > -1) && ((index2+1) < s.length())) {
      g2 = s.substring(index2, s.length());
      s = s.substring(0, index2);
      s = s.trim();

      //TraceManager.addDev("g2=" + g2 + " s=" + s);

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
      //TraceManager.addDev("nb of transitions=" + s);
      transi = Integer.decode(s).intValue();
      if (transi > max) {
      return null;
      }
      result.append(s + "\n");
      }
      }
      } catch (Exception e) {
      TraceManager.addError("Exception09545 " + e.getMessage());
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
		    //TraceManager.addDev("Projected gate");
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
	
    public void enableUndo(boolean b) {
	undoRunning = !b;
    }
	
    public boolean isUndoEnable() {
	return !undoRunning;
    }

    // UNDO MANAGEMENT

    // This function is not be performed when executing an undo
    // if variable undoRunnin has been set to true
    public void saveOperation(Point p) {
	if (undoRunning) {
	    return;
	}

	//TraceManager.addDev("Save operation");
	
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
	    /*if ((p1.x != p.x) || (p1.y != p.y)){
		savedOperations.add(savedOperations.elementAt(size - 1));
		savedPanels.add(p);
		if (savedOperations.size() > nbMaxSavedOperations) {
		    savedOperations.removeElementAt(0);
		    savedPanels.removeElementAt(0);
		}
		}*/
	}
	
	savedOperations.add(s);
	savedPanels.add(p);
	if (savedOperations.size() > nbMaxSavedOperations) {
	    savedOperations.removeElementAt(0);
	    savedPanels.removeElementAt(0);
	}
	pointerOperation = savedOperations.size() - 1;
	//TraceManager.addDev("Setting pointer to " + pointerOperation);
	
	selectBackwardMode();
    }

    public void backward() {
	undoRunning = true;
	TraceManager.addDev("Nb Of saved operations:" + savedOperations.size() + " pointer=" + pointerOperation);
	if ((pointerOperation < 1)	|| (savedOperations.size() < 2)) {
	    TraceManager.addDev("Undo not possible");
	    undoRunning = false;
	    return;
	}
	
	removeAllComponents();
	mgui.reinitMainTabbedPane();
	try {
	    pointerOperation --;
	    TraceManager.addDev("Decrementing pointer =" + pointerOperation);
	    loadModelingFromXML((String)(savedOperations.elementAt(pointerOperation)));
	    
	} catch (Exception e) {
	    TraceManager.addError("Exception in backward: " + e.getMessage());
	}
	
	TraceManager.addDev("Selecting tab");
	
	Point p = (Point)(savedPanels.elementAt(pointerOperation));
	if (p != null) {
	    TraceManager.addDev("Selecting tab panel=" + p.getX() + " diagram=" + p.getY());
	    TDiagramPanel tdp = mgui.selectTab(p);
	    tdp.mode = tdp.NORMAL;
	    tdp.setDraw(true);
	    tdp.repaint();
	}
	
	
	TraceManager.addDev("Selecting backward mode");
	selectBackwardMode();
	undoRunning = false;
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

		undoRunning = true;
		removeAllComponents();
		mgui.reinitMainTabbedPane();

		try {
			pointerOperation ++;
			loadModelingFromXML((String)(savedOperations.elementAt(pointerOperation)));
		} catch (Exception e) {
			TraceManager.addError("Exception in forward: " + e.getMessage());
		}

		Point p = (Point)(savedPanels.elementAt(pointerOperation));
		if (p != null) {
			TDiagramPanel tdp = mgui.selectTab(p);
			tdp.mode = TDiagramPanel.NORMAL;
			tdp.setDraw(true);
			tdp.repaint();
		}

		selectBackwardMode();
		undoRunning = false;
	}


	// BUILDING A TURTLE MODELING AND CHECKING IT
	public boolean checkTURTLEModeling(Vector tclasses, DesignPanel dp, boolean overideSyntaxChecking) {
		// Builds a TURTLE modeling from diagrams
		//warnings = new Vector();
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
		warnings = dpt.getWarnings();
		if ((checkingErrors != null) && (checkingErrors.size() >0)){
			return false;
		}

		// modeling is built
		// Now check it !
		if (!overideSyntaxChecking) {
			TURTLEModelChecker tmc = new TURTLEModelChecker(tm, listE);
	
			checkingErrors = tmc.syntaxAnalysisChecking();
			warnings.addAll(tmc.getWarnings());
	
			if ((checkingErrors != null) && (checkingErrors.size() > 0)){
				analyzeErrors();
				return false;
			} else {
				return true;
			}
		}
		
		return true;
	}
	
	// BUILDING An AVATAR Design AND CHECKING IT
	public boolean checkAvatarDesign(Vector blocks, AvatarDesignPanel adp, boolean _optimize) {
		// Builds a TURTLE modeling from diagrams
		//warnings = new Vector();
		//checkingErrors = null;
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		//tm = new TURTLEModeling();
		//listE = new CorrespondanceTGElement();
		mgui.reinitCountOfPanels();
		
		//avatarspec = new AvatarSpecification("avatarspecification", adp);

		AvatarDesignPanelTranslator adpt = new AvatarDesignPanelTranslator(adp);
		avatarspec = adpt.generateAvatarSpecification(blocks);
		avatarspec.setInformationSource(adp);
		optimizeAvatar = _optimize;
		//TraceManager.addDev("AvatarSpec:" + avatarspec.toString() + "\n\n");
		tmState = 3;

		listE = adpt.getCorrespondanceTGElement();
		checkingErrors = adpt.getErrors();
		warnings = adpt.getWarnings();
		if ((checkingErrors != null) && (checkingErrors.size() >0)){
			return false;
		}
		return true;

		// Modeling is built
		// Now check it !
		/*if (!overideSyntaxChecking) {
			TURTLEModelChecker tmc = new TURTLEModelChecker(tm, listE);
	
			checkingErrors = tmc.syntaxAnalysisChecking();
			warnings.addAll(tmc.getWarnings());
	
			if ((checkingErrors != null) && (checkingErrors.size() > 0)){
				analyzeErrors();
				return false;
			} else {
				return true;
			}
		}
		
		return true;*/
	}
	
	// Return values
	// -1: error
	// -2: no mutex
	// -3: invariant for mutex not found
	// else: invariant found! -> invariant index
	public int computeMutex() {
		if (avatarspec == null) {
			return -1;
		}
		
		AvatarDesignPanel adp = null;
		
		try {
			 adp = (AvatarDesignPanel)(avatarspec.getInformationSource());
		} catch (Exception e) {
			TraceManager.addDev("Exception gtm: " + e.getMessage());
			return -1;
		}
		
		// Building the list of all states in the mutex
		LinkedList<TGComponent> compInMutex = adp.getListOfComponentsInMutex();
		TraceManager.addDev("Nb of elements in mutex:" + compInMutex.size());
		
		if (compInMutex.size() ==0) {
			return -2;
		}
		
		LinkedList<TGComponent> comps;
		boolean found;
		int nbOfFound;
		int cpt = 0;
		// Go thru invariants, and see whether one contains
		for(Invariant inv: invariants) {
			comps = inv.getComponents();
			nbOfFound = 0;
			for(TGComponent tgc_mutex: compInMutex) {
				found = false;
				for(TGComponent tgc_inv: comps) {
					if (tgc_mutex == tgc_inv) {
						found = true;
						break;
					}
				}
				if (!found) {
					break;
				}
				nbOfFound ++;
			}
			if (nbOfFound == compInMutex.size()) {
				TraceManager.addDev("Mutex found in inv:" + cpt);
				for(TGComponent tgc: compInMutex) {
					tgc.setMutexResult(TGComponent.MUTEX_OK);
				}
				return cpt;
			}
			cpt ++;
		}
		
		
		for(TGComponent tgc: compInMutex) {
			tgc.setMutexResult(TGComponent.MUTEX_UNKNOWN);
		}
				
		return -3;
		
		
    }
    
    public void clearGraphicalInfoOnInvariants() {
    	if (avatarspec == null) {
			return;
		}
		
		AvatarDesignPanel adp = null;
		
		try {
			 adp = (AvatarDesignPanel)(avatarspec.getInformationSource());
		} catch (Exception e) {
			TraceManager.addDev("Exception gtm: " + e.getMessage());
			return;
		}
		
		adp.removeAllMutualExclusionWithMasterMutex();
		
		
		
    }
    
    
    // Returns the number of states found
    // Returns -1 in case of error
    public int computeMutexStatesWith(AvatarSMDState state) {
    	Vector<TGComponent> list = new Vector<TGComponent>();
    	
    	if (state == null) {
    		state.setMutexWith(TGComponent.MUTEX_UNKNOWN);
    		return -1;
    	}
    	
    	for(Invariant inv: invariants) {
    		if (inv.containsComponent(state)) {
    			// All other states are in mutual exclusion
    			for(TGComponent tgc: inv.getComponents()) {
    				if ((tgc instanceof AvatarSMDState) && (tgc != state)) {
    					if (tgc.getTDiagramPanel() != state.getTDiagramPanel()) {
    					if (!(list.contains(tgc))) {
    						tgc.setMutualExclusionWithMasterMutex(state.getTDiagramPanel().getName() + "/" + state.getStateName());
    						list.add(tgc);
    						
    					}
    					}
    				}
    			}
    		}
    	}
    	
    	if (list.size() > 0) {
    	  state.setMutexWith(TGComponent.MUTEX_OK);
    	} else {
    		state.setMutexWith(TGComponent.MUTEX_UNKNOWN);
    	}
    	
    	return list.size();
    	
    }
    
    
    public void computeAllMutualExclusions() {
    	TURTLEPanel tp = mgui.getCurrentTURTLEPanel();
    	
    	if (tp == null) {
    		return;
    	}
    	
    	if (!(tp instanceof AvatarDesignPanel)) {
    		return;
    	}
    	
    	AvatarDesignPanel adp = (AvatarDesignPanel)(tp);
    	adp.reinitMutualExclusionStates();
    	
    	// First step: build a list of all states being in invariants
    	Vector<AvatarSMDState> v = new Vector<AvatarSMDState>();
    	for(Invariant inv: invariants) {
    		for(TGComponent tgc: inv.getComponents()) {
    			if (tgc instanceof AvatarSMDState) {
    				if (!(v.contains(tgc))) {
    					v.add((AvatarSMDState)tgc);
    				}
    			}
    		}
    	}
    	
    	// Then, add to all states its list of mutually exclusive states
    	
    	for(AvatarSMDState s: v) {
    		Vector<AvatarSMDState> v0 = new Vector<AvatarSMDState>();
    		for(Invariant inv: invariants) {
    			if (inv.containsComponent(s)) {
					for(TGComponent tgc: inv.getComponents()) {
						if ((tgc instanceof AvatarSMDState) && (tgc != s)) {
							if (tgc.getTDiagramPanel() != s.getTDiagramPanel()) {
								if (!(v0.contains(tgc))) {
									v0.add((AvatarSMDState)tgc);
								}
							}
						}
					}
    			}
    		}
    		TraceManager.addDev("State " + s.getStateName() + " has " + v0.size() + " mutually eclusive states");
    		
    		for(AvatarSMDState s0: v0) {
    			s.addMutexState(s0);
    		}
    	}
    	
    	
    }
	
	// From AVATAR to TURTLEModeling
	public boolean translateAvatarSpecificationToTIF() {
		AVATAR2TURTLE att = new AVATAR2TURTLE(avatarspec);
		tm = att.generateTURTLEModeling();
		
		TURTLEModelChecker tmc = new TURTLEModelChecker(tm, listE);
		
		checkingErrors = tmc.syntaxAnalysisChecking();
		warnings.addAll(tmc.getWarnings());
		
		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			analyzeErrors();
			return false;
		} else {
			return true;
		}
		
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
		
		if (tgc instanceof AvatarBDBlock) {
			AvatarSMDPanel asmdp = mgui.getAvatarSMDPanel(mgui.getCurrentSelectedIndex(), tgc.getValue());
			s.append(asmdp.saveInXML());
			LinkedList<AvatarBDBlock> list = ((AvatarBDBlock)tgc).getFullBlockList();
			for(AvatarBDBlock b:list) {
				asmdp = mgui.getAvatarSMDPanel(mgui.getCurrentSelectedIndex(), b.getValue());
				s.append(asmdp.saveInXML());
			}
			
		}

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
		//TraceManager.addDev("Making copy");

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
			//TraceManager.addDev("Saving TURTLEOS activity diagram Panel...");
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
			//TraceManager.addDev("Saving TML activity diagram Panel...");
			TMLTaskOperator t;
			TMLActivityDiagramPanel tmladp;
			for(int i=0; i<v.size(); i++) {
				t = (TMLTaskOperator)(v.elementAt(i));
				tmladp = mgui.getTMLActivityDiagramPanel(mgui.getCurrentSelectedIndex(), t.getValue());
				s.append(tmladp.saveInXML());
			}
		}
		
		v = tdp.selectedAvatarBDBlocks();
		if ((v != null) && (v.size() > 0)) {
			//TraceManager.addDev("Saving TML activity diagram Panel...");
			AvatarBDBlock abdb;
			AvatarSMDPanel asmdp;
			for(int i=0; i<v.size(); i++) {
				abdb = (AvatarBDBlock)(v.elementAt(i));
				asmdp = mgui.getAvatarSMDPanel(mgui.getCurrentSelectedIndex(), abdb.getBlockName());
				s.append(asmdp.saveInXML());
				
			}
		}
		
		v = tdp.selectedCPrimitiveComponent();
		if ((v != null) && (v.size() > 0)) {
			//TraceManager.addDev("Saving TML activity diagram Panel...");
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

		TraceManager.addDev("Copy done");
		//TraceManager.addDev(str);

		return str;
	}
	
	public String makeOneDiagramXMLFromGraphicalModel(TURTLEPanel tp, int indexOfDiagram) {
		StringBuffer sb = new StringBuffer();
		//sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<TURTLEGMODELING>\n\n");
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n<TURTLEGMODELING version=\"" + DefaultText.getVersion() + "\">\n\n");

		StringBuffer s;
		String str;

		
		s = tp.saveInXML(indexOfDiagram);
		
		sb.append(s);
		sb.append("\n\n");
	

		sb.append("</TURTLEGMODELING>");

		str = new String(sb);
		str = encodeString(str);

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
		//TraceManager.addDev("copyModelingFromXML: " + s);
		//TraceManager.addDev("tdp: " + tdp);
		
		//TraceManager.addDev(s);
		//TraceManager.addDev("copyModelingFromXML:");
		//LinkedList ComponentsList=tdp.getComponentList();
		int beginIndex = tdp.getComponentList().size();

		//Added by Solange
		int cuenta=1;

		s = decodeString(s);
		
		//TraceManager.addDev("copy=" + s);
		
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
				TraceManager.addDev("TClassDiagramPanel copy");
				
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

						//TraceManager.addDev("Class diagram : " + tcdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tcdp);
						makePostProcessing(tcdp);
						//TraceManager.addDev("Class diagram : " + tcdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tcdp);
						//TraceManager.addDev("Class diagram : " + tcdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tcdp);
						//TraceManager.addDev("Class diagram : " + tcdp.getName() + " real points");
						connectConnectorsToRealPoints(tcdp);
						tcdp.structureChanged();
						//TraceManager.addDev("Class diagram : " + tcdp.getName() + " post loading " + beginIndex);
						makePostLoading(tcdp, beginIndex);
						//TraceManager.addDev("Class diagram : " + tcdp.getName() + " post loading done");
					}
				}
				docCopy = null;

			} else if (tdp instanceof TActivityDiagramPanel) {
				TraceManager.addDev("TActivityDiagramPanel copy");
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

						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tadp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tadp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tadp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(tadp);
						tadp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " post loading");
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

						//TraceManager.addDev("Activity diagram : " + iodp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), iodp);
						//TraceManager.addDev("Activity diagram : " + iodp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), iodp);
						//TraceManager.addDev("Activity diagram : " + iodp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), iodp);
						//TraceManager.addDev("Activity diagram : " + iodp.getName() + " real points");
						connectConnectorsToRealPoints(iodp);
						iodp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + iodp.getName() + " post loading");
						makePostLoading(iodp, beginIndex);
					}
				}
			} else if (tdp instanceof SequenceDiagramPanel) {
				//TraceManager.addDev("Sequence diagram!");
				nl = doc.getElementsByTagName("SequenceDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				SequenceDiagramPanel sdp = (SequenceDiagramPanel)tdp;
				
				//TraceManager.addDev("Sequence diagram!");

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

						//TraceManager.addDev("Sequence diagram: " + sdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), sdp);
						//TraceManager.addDev("Sequence diagram: " + sdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), sdp);
						//TraceManager.addDev("Sequence diagram: " + sdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), sdp);
						//TraceManager.addDev("Sequence diagram: " + sdp.getName() + " real points");
						connectConnectorsToRealPoints(sdp);
						sdp.structureChanged();
						//TraceManager.addDev("Sequence diagram: " + sdp.getName() + " post loading");
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

						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), ucdp);
						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), ucdp);
						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), ucdp);
						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " real points");
						connectConnectorsToRealPoints(ucdp);
						ucdp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + iodp.getName() + " post loading");
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

						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tddp);
						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tddp);
						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tddp);
						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " real points");
						connectConnectorsToRealPoints(tddp);
						tddp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + iodp.getName() + " post loading");
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

						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), ncdp);
						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), ncdp);
						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), ncdp);
						//TraceManager.addDev("Activity diagram : " + sdp.getName() + " real points");
						connectConnectorsToRealPoints(ncdp);
						ncdp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + iodp.getName() + " post loading");
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
			} else if (tdp instanceof EBRDDPanel) {
				nl = doc.getElementsByTagName("EBRDDPanelCopy");

				if (nl == null) {
					return;
				}

				EBRDDPanel ebrddp = (EBRDDPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (ebrddp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), ebrddp);
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), ebrddp);
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), ebrddp);
						connectConnectorsToRealPoints(ebrddp);
						ebrddp.structureChanged();
						makePostLoading(ebrddp, beginIndex);
					}
				}
			} else if (tdp instanceof AttackTreeDiagramPanel) {
				nl = doc.getElementsByTagName("AttackTreeDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				AttackTreeDiagramPanel atdp = (AttackTreeDiagramPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (atdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), atdp);
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), atdp);
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), atdp);
						connectConnectorsToRealPoints(atdp);
						atdp.structureChanged();
						makePostLoading(atdp, beginIndex);
					}
				}
			} else if (tdp instanceof TMLTaskDiagramPanel) {
				nl = doc.getElementsByTagName("TMLTaskDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//TraceManager.addDev("Toto 1");


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

						//TraceManager.addDev("Toto 2");

						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmltdp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(tmltdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmltdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmltdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(tmltdp);
						tmltdp.structureChanged();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(tmltdp, beginIndex);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
			}else if (tdp instanceof DiplodocusMethodologyDiagramPanel) {
				nl = doc.getElementsByTagName("DiplodocusMethodologyDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//TraceManager.addDev("Toto 1");


				DiplodocusMethodologyDiagramPanel tmltdp = (DiplodocusMethodologyDiagramPanel)tdp;


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

						//TraceManager.addDev("Toto 2");

						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmltdp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(tmltdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmltdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmltdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(tmltdp);
						tmltdp.structureChanged();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(tmltdp, beginIndex);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
				
				
			}else if (tdp instanceof AvatarMethodologyDiagramPanel) {
				nl = doc.getElementsByTagName("DiplodocusMethodologyDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//TraceManager.addDev("Toto 1");


				AvatarMethodologyDiagramPanel amdp = (AvatarMethodologyDiagramPanel)tdp;


				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (amdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						amdp.loadExtraParameters(elt);

						//TraceManager.addDev("Toto 2");

						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), amdp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(amdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), amdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), amdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(amdp);
						amdp.structureChanged();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(amdp, beginIndex);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
			}  else if (tdp instanceof TMLComponentTaskDiagramPanel) {
				nl = doc.getElementsByTagName("TMLComponentTaskDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//TraceManager.addDev("Toto 1");


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

						//TraceManager.addDev("Toto 2");

						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmlctdp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(tmlctdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmlctdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmlctdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(tmlctdp);
						tmlctdp.structureChanged();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(tmlctdp, beginIndex);
						tmlctdp.hideConnectors();
						tmlctdp.updatePorts();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading done");
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

						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmladp);
						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmladp);
						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmladp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(tmladp);
						tmladp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(tmladp, beginIndex);
					}
				}
			}  else if (tdp instanceof TMLCPPanel) {
				nl = doc.getElementsByTagName("CommunicationPatternDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//TraceManager.addDev("Toto 1");

				TMLCPPanel tmlcpp = (TMLCPPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (tmlcpp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//tmlcpp.loadExtraParameters(elt);

						//TraceManager.addDev("Toto 2");

						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmlcpp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(tmlcpp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmlcpp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmlcpp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(tmlcpp);
						tmlcpp.structureChanged();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(tmlcpp, beginIndex);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
			} else if (tdp instanceof TMLSDPanel) {
				nl = doc.getElementsByTagName("TMLSDPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//TraceManager.addDev("Toto 1");

				TMLSDPanel tmlsdp = (TMLSDPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (tmlsdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//tmlcpp.loadExtraParameters(elt);

						//TraceManager.addDev("Toto 2");

						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmlsdp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(tmlsdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmlsdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmlsdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(tmlsdp);
						tmlsdp.structureChanged();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(tmlsdp, beginIndex);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
			} else if (tdp instanceof TMLArchiDiagramPanel) {
				nl = doc.getElementsByTagName("TMLArchiDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//TraceManager.addDev("Toto 1");

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

						//TraceManager.addDev("Toto 2");

						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmadp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(tmadp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmadp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmadp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(tmadp);
						tmadp.structureChanged();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(tmadp, beginIndex);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading done");
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
						//TraceManager.addDev("Toto 2");
						//TraceManager.addDev("TURTLEOS task diagram : " + toscdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), toscdp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(toscdp);
						//TraceManager.addDev("TURTLEOS task diagram : " + toscdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), toscdp);
						//TraceManager.addDev("TURTLEOS task diagram : " + toscdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), toscdp);
						//TraceManager.addDev("TURTLEOS task diagram : " + toscdp.getName() + " real points");
						connectConnectorsToRealPoints(toscdp);
						toscdp.structureChanged();
						//TraceManager.addDev("TURTLEOS task diagram : " + toscdp.getName() + " post loading " + beginIndex);
						makePostLoading(toscdp, beginIndex);
						//TraceManager.addDev("TURTLEOS task diagram : " + toscdp.getName() + " post loading done");
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

						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tosadp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tosadp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tosadp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(tosadp);
						tosadp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(tosadp, beginIndex);
					}
				}
			} else if (tdp instanceof ProactiveCSDPanel) {
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
						//TraceManager.addDev("Toto 2");
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), pcsdp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(pcsdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), pcsdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), pcsdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(pcsdp);
						pcsdp.structureChanged();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(pcsdp, beginIndex);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading done");
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
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), psmdp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), psmdp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), psmdp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(psmdp);
						psmdp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " post loading");
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

						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), psmdp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), psmdp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), psmdp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(psmdp);
						psmdp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(psmdp, beginIndex);
					}
				}
				
			// AVATAR
			} else if (tdp instanceof AvatarBDPanel) {
				nl = doc.getElementsByTagName("AVATARBlockDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//TraceManager.addDev("Toto 1");


				AvatarBDPanel abdp = (AvatarBDPanel)tdp;


				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (abdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						abdp.loadExtraParameters(elt);

						//TraceManager.addDev("Toto 2");

						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), abdp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(abdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), abdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), abdp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(abdp);
						abdp.structureChanged();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(abdp, beginIndex);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
				
				
			} else if (tdp instanceof ADDDiagramPanel) {
				nl = doc.getElementsByTagName("ADDDiagramPanelCopy");
				docCopy = doc;

				if (nl == null) {
					return;
				}

				//TraceManager.addDev("Toto 1");


				ADDDiagramPanel addp = (ADDDiagramPanel)tdp;


				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (addp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						addp.loadExtraParameters(elt);

						//TraceManager.addDev("Toto 2");

						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), addp);
						//TraceManager.addDev("Toto 3");
						makePostProcessing(addp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), addp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), addp);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " real points");
						connectConnectorsToRealPoints(addp);
						addp.structureChanged();
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading " + beginIndex);
						makePostLoading(addp, beginIndex);
						//TraceManager.addDev("TML task diagram : " + tmltdp.getName() + " post loading done");
					}
				}
				
			} else if (tdp instanceof AvatarSMDPanel) {
				nl = doc.getElementsByTagName("AVATARStateMachineDiagramPanelCopy");

				if (nl == null) {
					return;
				}

				AvatarSMDPanel asmdp = (AvatarSMDPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (asmdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						//tmladp.loadExtraParameters(elt);

						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), asmdp);
						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), asmdp);
						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), asmdp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(asmdp);
						asmdp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(asmdp, beginIndex);
					}
				}
			} else if (tdp instanceof AvatarRDPanel) {
				nl = doc.getElementsByTagName("AvatarRDPanelCopy");

				if (nl == null) {
					return;
				}

				AvatarRDPanel ardp = (AvatarRDPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (ardp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), ardp);
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), ardp);
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), ardp);
						connectConnectorsToRealPoints(ardp);
						ardp.structureChanged();
						makePostLoading(ardp, beginIndex);
					}
				}
				
			} else if (tdp instanceof AvatarMADPanel) {
				nl = doc.getElementsByTagName("AvatarMADPanelCopy");

				if (nl == null) {
					return;
				}

				AvatarMADPanel amadp = (AvatarMADPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (amadp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), amadp);
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), amadp);
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), amadp);
						connectConnectorsToRealPoints(amadp);
						amadp.structureChanged();
						makePostLoading(amadp, beginIndex);
					}
				}
				
			} else if (tdp instanceof AvatarPDPanel) {
				nl = doc.getElementsByTagName("AvatarPDPanelCopy");

				if (nl == null) {
					return;
				}

				AvatarPDPanel apdp = (AvatarPDPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (apdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), apdp);
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), apdp);
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), apdp);
						connectConnectorsToRealPoints(apdp);
						apdp.structureChanged();
						makePostLoading(apdp, beginIndex);
					}
				}
			
			} else if (tdp instanceof AvatarCDPanel) {
				nl = doc.getElementsByTagName("AvatarCDPanelCopy");

				if (nl == null) {
					return;
				}

				AvatarCDPanel acdp = (AvatarCDPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (acdp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), acdp);
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), acdp);
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), acdp);
						connectConnectorsToRealPoints(acdp);
						acdp.structureChanged();
						makePostLoading(acdp, beginIndex);
					}
				}
			} else if (tdp instanceof AvatarADPanel) {
				nl = doc.getElementsByTagName("AvatarADPanelCopy");

				if (nl == null) {
					return;
				}

				AvatarADPanel aadp = (AvatarADPanel)tdp;

				for(i=0; i<nl.getLength(); i++) {
					adn = nl.item(i);
					if (adn.getNodeType() == Node.ELEMENT_NODE) {
						elt = (Element) adn;

						if (aadp == null) {
							throw new MalformedModelingException();
						}

						//int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
						//int ySel = Integer.decode(elt.getAttribute("ySel")).intValue();
						//int widthSel = Integer.decode(elt.getAttribute("widthSel")).intValue();
						//int heightSel = Integer.decode(elt.getAttribute("heightSel")).intValue();

						decX = _decX;
						decY = _decY;

						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), aadp);
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), aadp);
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), aadp);
						connectConnectorsToRealPoints(aadp);
						aadp.structureChanged();
						makePostLoading(aadp, beginIndex);
					}
				}
			}
			
		} catch (IOException e) {
			TraceManager.addError("Loading 500: " + e.getMessage());
			throw new MalformedModelingException();
		} catch (SAXException saxe) {
			TraceManager.addError("Loading 501 " + saxe.getMessage());
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

		//TraceManager.addDev("Got:" + sb);

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
			
			//TraceManager.addDev("nb de design=" + designPanelNl.getLength() + " nb d'analyse=" + analysisNl.getLength());
			boolean error = false;
			for(i=0; i<panelNl.getLength(); i++) {
				node = panelNl.item(i);
				//TraceManager.addDev("Node = " + dnd);
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					// create design, and get an index for it
					try {
						loadModeling(node);
					} catch (MalformedModelingException mme) {
						Element elt = (Element) node;
						String type = elt.getAttribute("type");
						TraceManager.addDev("Error when loading diagram: " + type);
						error = true;
					}
				}
			}
			if (error == true) {
				throw new MalformedModelingException();
			}

		} catch (NumberFormatException nfe) {
			TraceManager.addError("Loading 400 " + nfe.getMessage());
			throw new MalformedModelingException();
		} catch (IOException e) {
			TraceManager.addError("Loading 600 " + e.getMessage());
			throw new MalformedModelingException();
		} catch (SAXException saxe) {
			TraceManager.addError("Loading 601 " + saxe.getMessage());
			throw new MalformedModelingException();
		}
		//TraceManager.addDev("making IDs");
		makeLastLoad();
		makeLovelyIds();
		//TraceManager.addDev("IDs done");
	}

	public void loadModeling(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String type = elt.getAttribute("type");
		// AVATAR
		if (type.compareTo("ADD") == 0) {
			loadAvatarDeployment(node);
		} else if (type.compareTo("AVATAR Design") == 0) {
			loadAvatarDesign(node);
		} else if (type.compareTo("Avatar Requirement") == 0) {
			loadAvatarRequirement(node);
		} else if (type.compareTo("Avatar MAD") == 0) {
			loadAvatarMADs(node);
		} else if (type.compareTo("Avatar Analysis") == 0) {
			loadAvatarAnalysis(node);
			
			
		// TURTLE 
		} else if (type.compareTo("Design") == 0) {
			loadDesign(node);
		} else if (type.compareTo("Analysis") == 0) {
			loadAnalysis(node);
		} else if (type.compareTo("Deployment") == 0) {
			loadDeployment(node);
		} else if (type.compareTo("NC diagram") == 0) {
			loadNC(node);
		} else if (type.compareTo("Requirement") == 0) {
			loadRequirement(node);
		} else if (type.compareTo("AttackTree") == 0) {
			loadAttackTree(node);
		} else if (type.compareTo("Diplodocus Methodology") == 0) {
			loadDiplodocusMethodology(node);
		} else if (type.compareTo("Avatar Methodology") == 0) {
			loadAvatarMethodology(node);
		} else if (type.compareTo("TML Design") == 0) {
			loadTMLDesign(node);
		} else if (type.compareTo("TML Component Design") == 0) {
			loadTMLComponentDesign(node);
		} else if (type.compareTo("TML CP") == 0) {
			loadTMLCP(node);
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
	
	public void loadAvatarDesign(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexDesign;


		nameTab = elt.getAttribute("nameTab");

		indexDesign = mgui.createAvatarDesign(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//TraceManager.addDev("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("AVATARBlockDiagramPanel") == 0) {
					// Class diagram
					loadAvatarBD(elt, indexDesign);
				} else { // Managing activity diagrams
					if (elt.getTagName().compareTo("AVATARStateMachineDiagramPanel") == 0) {
						// Managing activity diagrams
						loadAvatarSMD(elt, indexDesign);
					}
				}
			}
		}
	}
	
	public void loadAvatarDeployment(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexReq;
		int cpt_req = 0;

		TraceManager.addDev("Loading ADD 0");

		nameTab = elt.getAttribute("nameTab");

		indexReq = mgui.createADD(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//TraceManager.addDev("Deployment nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("ADDDiagramPanel") == 0) {
						TraceManager.addDev("Loading ADD 1");
					loadADDDiagram(elt, indexReq, cpt_req);
					cpt_req ++;
				} 
			}
		}
	}
	
	public void loadAvatarRequirement(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexReq;
		int cpt_req = 0;


		nameTab = elt.getAttribute("nameTab");

		indexReq = mgui.createAvatarRequirement(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//TraceManager.addDev("Deployment nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("AvatarRDPanel") == 0) {
					loadAvatarRD(elt, indexReq, cpt_req);
					cpt_req ++;
				} else if (elt.getTagName().compareTo("AvatarPDPanel") == 0) {
					loadAvatarPD(elt, indexReq, cpt_req);
					cpt_req ++;
				}
			}
		}
	}
	
	public void loadAvatarMADs(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexReq;
		int cpt_req = 0;


		nameTab = elt.getAttribute("nameTab");
		
		TraceManager.addDev("Creating MAD panel ");

		indexReq = mgui.createAvatarMADs(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			TraceManager.addDev("MADs nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("AvatarMADPanel") == 0) {
					loadAvatarMAD(elt, indexReq, cpt_req);
					cpt_req ++;
				} 
			}
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
			//TraceManager.addDev("Design nodes: " + j);
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
			//TraceManager.addDev("Design nodes: " + j);
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
					} /*else if (elt.getTagName().compareTo("AvatarCDPanel") == 0) {
						// Managing use case diagrams
						loadAvatarCD(elt, indexAnalysis, cpt);
						cpt ++;
					} else if (elt.getTagName().compareTo("AvatarADPanel") == 0) {
						// Managing use case diagrams
						loadAvatarAD(elt, indexAnalysis, cpt);
						cpt ++;
					}*/
				}
			}
		}
	}
	
	public void loadTMLCP(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexTMLCP;
		int cpt = 0;

		nameTab = elt.getAttribute("nameTab");

		indexTMLCP = mgui.createTMLCP(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//TraceManager.addDev("Nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("CommunicationPatternDiagramPanel") == 0) {
					// CP
					loadTMLCPDiagram(elt, indexTMLCP);
					cpt ++;
				} else { // Managing sequence diagrams
					if (elt.getTagName().compareTo("TMLSDPanel") == 0) {
						loadTMLSDDiagram(elt, indexTMLCP);
						cpt ++;
					} 
				}
			}
		}
	}
	
	public void loadAvatarAnalysis(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexAnalysis;
		int cpt = 0;
		
		//TraceManager.addDev("Loading Avatar analysis");
		
		nameTab = elt.getAttribute("nameTab");
		
		indexAnalysis = mgui.createAvatarAnalysis(nameTab);
		
		diagramNl = node.getChildNodes();
		
		for(int j=0; j<diagramNl.getLength(); j++) {
			//TraceManager.addDev("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				
				if (elt.getTagName().compareTo("SequenceDiagramPanel") == 0) {
					//TraceManager.addDev("Loading seq diag");
					loadSequenceDiagram(elt, indexAnalysis);
					//TraceManager.addDev("Loading seq diag done");
					cpt ++;
				} else if (elt.getTagName().compareTo("UseCaseDiagramPanel") == 0) {
					// Managing use case diagrams
					//TraceManager.addDev("Loading ucd diag");
					loadUseCaseDiagram(elt, indexAnalysis, cpt);
					//TraceManager.addDev("Loading ucd diag done");
					
					cpt ++;
				} else if (elt.getTagName().compareTo("AvatarCDPanel") == 0) {
					// Managing use case diagrams
					//TraceManager.addDev("Loading cd diag");
					loadAvatarCD(elt, indexAnalysis, cpt);
					//TraceManager.addDev("Loading cd diag done");
					cpt ++;
				} else if (elt.getTagName().compareTo("AvatarADPanel") == 0) {
					// Managing use case diagrams
					//TraceManager.addDev("Loading ad diag");
					loadAvatarAD(elt, indexAnalysis, cpt);
					//TraceManager.addDev("Loading ad diag done");
					cpt ++;
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
			//TraceManager.addDev("Deployment nodes: " + j);
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
			//TraceManager.addDev("Deployment nodes: " + j);
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
			//TraceManager.addDev("Deployment nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TRequirementDiagramPanel") == 0) {
					loadRequirementDiagram(elt, indexReq, cpt_req);
					cpt_req ++;
				} else if (elt.getTagName().compareTo("EBRDDPanel") == 0) {
					loadEBRDD(elt, indexReq, cpt_req);
					cpt_req ++;
				}
			}
		}
	}
	
	public void loadAttackTree(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexTree;

		nameTab = elt.getAttribute("nameTab");

		indexTree = mgui.createAttackTree(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//TraceManager.addDev("Deployment nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("AttackTreeDiagramPanel") == 0) {
					loadAttackTreeDiagram(elt, indexTree);
				}
			}
		}
	}
	
	public void loadDiplodocusMethodology(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexDesign;


		nameTab = elt.getAttribute("nameTab");

		indexDesign = mgui.createDiplodocusMethodology(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//TraceManager.addDev("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("DiplodocusMethodologyDiagramPanel") == 0) {
					// Class diagram
					//TraceManager.addDev("Loading TML CD");
					loadDiplodocusMethodologyDiagram(elt, indexDesign);
					//TraceManager.addDev("End loading TML CD");
				}
			}
		}
	}
	
	public void loadAvatarMethodology(Node node) throws  MalformedModelingException, SAXException {
		Element elt = (Element) node;
		String nameTab;
		NodeList diagramNl;
		int indexDesign;


		nameTab = elt.getAttribute("nameTab");

		indexDesign = mgui.createAvatarMethodology(nameTab);

		diagramNl = node.getChildNodes();

		for(int j=0; j<diagramNl.getLength(); j++) {
			//TraceManager.addDev("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("AvatarMethodologyDiagramPanel") == 0) {
					// Class diagram
					//TraceManager.addDev("Loading TML CD");
					loadAvatarMethodologyDiagram(elt, indexDesign);
					//TraceManager.addDev("End loading TML CD");
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
			//TraceManager.addDev("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TMLTaskDiagramPanel") == 0) {
					// Class diagram
					//TraceManager.addDev("Loading TML CD");
					loadTMLTaskDiagram(elt, indexDesign);
					//TraceManager.addDev("End loading TML CD");
				} else { // Managing activity diagrams
					if (elt.getTagName().compareTo("TMLActivityDiagramPanel") == 0) {
						// Managing activity diagrams
						//TraceManager.addDev("Loading TML AD");
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
			//TraceManager.addDev("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TMLComponentTaskDiagramPanel") == 0) {
					// Component diagram
					//TraceManager.addDev("Loading TML Component diagram");
					loadTMLComponentTaskDiagram(elt, indexDesign);
					//TraceManager.addDev("End loading TML CD");
				} else { // Managing activity diagrams
					if (elt.getTagName().compareTo("TMLActivityDiagramPanel") == 0) {
						// Managing activity diagrams
						//TraceManager.addDev("Loading TML AD");
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
			TraceManager.addDev("TML Architecture nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TMLArchiDiagramPanel") == 0) {
					TraceManager.addDev("Loading TML DD" + elt.getTagName() );
					loadTMLArchitectureDiagram(elt, indexDesign);
					TraceManager.addDev("End loading TML DD");
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
			//TraceManager.addDev("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("TURTLEOSClassDiagramPanel") == 0) {
					// Class diagram
					//TraceManager.addDev("Loading TURTLEOS CD");
					loadTURTLEOSClassDiagram(elt, indexDesign);
					//TraceManager.addDev("End loading TML CD");
				} else { // Managing activity diagrams
					if (elt.getTagName().compareTo("TURTLEOSActivityDiagramPanel") == 0) {
						// Managing activity diagrams
						//TraceManager.addDev("Loading TURTLEOS AD");
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
			//TraceManager.addDev("Design nodes: " + j);
			node = diagramNl.item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elt = (Element)node;
				if (elt.getTagName().compareTo("ProactiveCSDPanel") == 0) {
					// Class diagram
					//TraceManager.addDev("Loading TML CD");
					loadProactiveCSD(elt, indexDesign);
					//TraceManager.addDev("End loading TML CD");
				} else { // Managing activity diagrams
					if (elt.getTagName().compareTo("ProactiveSMDPanel") == 0) {
						// Managing activity diagrams
						//TraceManager.addDev("Loading TML AD");
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
		
		
		if (tdp instanceof AvatarBDPanel) {
			((AvatarBDPanel)tdp).loadExtraParameters(elt);
		}

		//TraceManager.addDev("Element" + elt.toString());
		// Loads components of the class diagram
		//TraceManager.addDev("Components");
		makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tdp);
		//TraceManager.addDev("Post processing");
		makePostProcessing(tdp);
		//TraceManager.addDev("Connectors");
		makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tdp);
		//TraceManager.addDev("Subcomponents");
		makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tdp);
		//TraceManager.addDev("RealPoints");
		connectConnectorsToRealPoints(tdp);
		//TraceManager.addDev("Structure changed");
		tdp.structureChanged();
		//TraceManager.addDev("Post loading");
		makePostLoading(tdp, 0);
		
		//TraceManager.addDev("Test connectors");
		if (tdp instanceof TMLComponentTaskDiagramPanel) {
			//TraceManager.addDev("Connectors...");
			((TMLComponentTaskDiagramPanel)tdp).setConnectorsToFront();
		}
		
		if (tdp instanceof EBRDDPanel) {
			//TraceManager.addDev("Connectors...");
			((EBRDDPanel)tdp).setConnectorsToFront();
		}
		
		if (tdp instanceof AttackTreeDiagramPanel) {
			//TraceManager.addDev("Connectors...");
			((AttackTreeDiagramPanel)tdp).setConnectorsToFront();
		}
		
		if (tdp instanceof AvatarBDPanel) {
			//TraceManager.addDev("Connectors...");
			((AvatarBDPanel)tdp).setConnectorsToFront();
		}
		
		if (tdp instanceof AvatarSMDPanel) {
			//TraceManager.addDev("Connectors...");
			((AvatarSMDPanel)tdp).setConnectorsToFront();
		}
		
		if (tdp instanceof AvatarPDPanel) {
			//TraceManager.addDev("Connectors...");
			((AvatarPDPanel)tdp).setConnectorsToFront();
		}
		
		if (tdp instanceof AvatarCDPanel) {
			//TraceManager.addDev("Connectors...");
			((AvatarCDPanel)tdp).setConnectorsToFront();
		}
		
		if (tdp instanceof AvatarADPanel) {
			//TraceManager.addDev("Connectors...");
			((AvatarADPanel)tdp).setConnectorsToFront();
		}
	}

	// AVATAR
	public void loadAvatarBD(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// class diagram name
		name = elt.getAttribute("name");
		mgui.setAvatarBDName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);

		loadDiagram(elt, tdp);
	}
	
	public void loadAvatarSMD(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		//TraceManager.addDev("Loading SMD of:" + name);
		AvatarSMDPanel asmdp = mgui.getAvatarSMDPanel(indexDesign, name);

		if (asmdp == null) {
			throw new MalformedModelingException();
		}

		asmdp.removeAll();

		loadDiagram(elt, asmdp);
	}
	
	public void loadAvatarRD(Element elt, int indexAnalysis, int indexTab) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		mgui.createAvatarRD(indexAnalysis, name);


		TDiagramPanel tdp = mgui.getAvatarRDPanel(indexAnalysis, indexTab, name);

		if (tdp == null) {
			throw new MalformedModelingException();
		}
		tdp.removeAll();

		loadDiagram(elt, tdp);
	}
	
	public void loadAvatarMAD(Element elt, int indexAnalysis, int indexTab) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		mgui.createAvatarMAD(indexAnalysis, name);


		TDiagramPanel tdp = mgui.getAvatarMADPanel(indexAnalysis, indexTab, name);

		if (tdp == null) {
			throw new MalformedModelingException();
		}
		tdp.removeAll();

		loadDiagram(elt, tdp);
	}
	
	public void loadADDDiagram(Element elt, int indexAnalysis, int indexTab) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		
		TraceManager.addDev("ADD 2");
		mgui.createAvatarPD(indexAnalysis, name);

		TraceManager.addDev("ADD 3");
		TDiagramPanel tdp = mgui.getAvatarADDPanel(indexAnalysis, indexTab, name);
		
		TraceManager.addDev("ADD 3.1");

		if (tdp == null) {
				TraceManager.addDev("ADD 3.2");
			throw new MalformedModelingException();
		}
		tdp.removeAll();
		TraceManager.addDev("ADD 4");

		loadDiagram(elt, tdp);
		TraceManager.addDev("ADD 5");
	}
	
	public void loadAvatarPD(Element elt, int indexAnalysis, int indexTab) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		mgui.createAvatarPD(indexAnalysis, name);

		TDiagramPanel tdp = mgui.getAvatarPDPanel(indexAnalysis, indexTab, name);

		if (tdp == null) {
			throw new MalformedModelingException();
		}
		tdp.removeAll();

		loadDiagram(elt, tdp);
	}
	
	public void loadAvatarCD(Element elt, int indexAnalysis, int indexTab) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		mgui.createAvatarCD(indexAnalysis, name);

		TDiagramPanel tdp = mgui.getAvatarCDPanel(indexAnalysis, indexTab, name);

		if (tdp == null) {
			throw new MalformedModelingException();
		}
		tdp.removeAll();

		loadDiagram(elt, tdp);
	}
	
	public void loadAvatarAD(Element elt, int indexAnalysis, int indexTab) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		mgui.createAvatarAD(indexAnalysis, name);

		TDiagramPanel tdp = mgui.getAvatarADPanel(indexAnalysis, indexTab, name);

		if (tdp == null) {
			throw new MalformedModelingException();
		}
		tdp.removeAll();

		loadDiagram(elt, tdp);
	}

	// TURTLE Design
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

	public void loadDiplodocusMethodologyDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// Diagram name
		name = elt.getAttribute("name");
		mgui.setDiplodocusMethodologyDiagramName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);
		tdp.setName(name);

		TraceManager.addDev("tdp=" + tdp.getName());

		loadDiagram(elt, tdp);
	}
	
	public void loadAvatarMethodologyDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// class diagram name
		name = elt.getAttribute("name");
		mgui.setAvatarMethodologyDiagramName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);
		tdp.setName(name);

		//TraceManager.addDev("tdp=" + tdp.getName());

		loadDiagram(elt, tdp);
	}
	
	public void loadTMLTaskDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// class diagram name
		name = elt.getAttribute("name");
		mgui.setTMLTaskDiagramName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);

		//TraceManager.addDev("tdp=" + tdp.getName());

		loadDiagram(elt, tdp);
	}
	
	public void loadTMLComponentTaskDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

		String name;
		TDiagramPanel tdp;

		// Diagram name
		name = elt.getAttribute("name");
		mgui.setTMLComponentTaskDiagramName(indexDesign, name);
		tdp = mgui.getMainTDiagramPanel(indexDesign);

		//TraceManager.addDev("tdp=" + tdp.getName());

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

		//TraceManager.addDev("tdp=" + tdp.getName());

		loadDiagram(elt, tdp);
	}

	public void loadTMLActivityDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		
		//TraceManager.addDev("getting tmladp: " + name);
		TMLActivityDiagramPanel tmladp = mgui.getTMLActivityDiagramPanel(indexDesign, name);
		//TraceManager.addDev("Got tmladp");
		
		
		if (tmladp == null) {
			//TraceManager.addDev("null tmladp");
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

		//TraceManager.addDev("tdp=" + tdp.getName());

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
	
	public void loadTMLCPDiagram(Element elt, int indexAnalysis) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		if (!(mgui.isTMLCPCreated(indexAnalysis, name))) {
			mgui.createTMLCPDiagram(indexAnalysis, name);
		}

		TDiagramPanel tdp = mgui.getTMLCPDiagramPanel(indexAnalysis, name);

		if (tdp == null) {
			throw new MalformedModelingException();
		}
		tdp.removeAll();

		loadDiagram(elt, tdp);
	}
	
	public void loadTMLSDDiagram(Element elt, int indexAnalysis) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		if (!(mgui.isTMLCPSDCreated(indexAnalysis, name))) {
			mgui.createTMLCPSequenceDiagram(indexAnalysis, name);
		}

		TDiagramPanel tdp = mgui.getTMLCPSDDiagramPanel(indexAnalysis, name);

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
	
	public void loadEBRDD(Element elt, int indexAnalysis, int indexTab) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		mgui.createEBRDD(indexAnalysis, name);


		TDiagramPanel tdp = mgui.getEBRDDPanel(indexAnalysis, indexTab, name);

		if (tdp == null) {
			throw new MalformedModelingException();
		}
		tdp.removeAll();

		loadDiagram(elt, tdp);
	}
	
	public void loadAttackTreeDiagram(Element elt, int indexDiag) throws  MalformedModelingException, SAXException {
		String name;

		name = elt.getAttribute("name");
		mgui.createAttackTreeDiagram(indexDiag, name);
		
		TDiagramPanel tdp = mgui.getAttackTreeDiagramPanel(indexDiag, name);

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
		//TraceManager.addDev("Loading seq diag1");
		SequenceDiagramPanel sdp = mgui.getSequenceDiagramPanel(indexAnalysis, name);
		//TraceManager.addDev("Loading seq diag2");

		if (sdp == null) {
			throw new MalformedModelingException();
		}
		//TraceManager.addDev("Loading seq diag3");

		sdp.removeAll();
		//TraceManager.addDev("Loading seq diag4");

		loadDiagram(elt, sdp);
		//TraceManager.addDev("Loading seq diag5");
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
				//TraceManager.addDev("Lovely id =" + id);
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
		//TraceManager.addDev("---> Load activity diagram");
		try {
			NodeList activityDiagramNl = docCopy.getElementsByTagName("TActivityDiagramPanel");

			TraceManager.addDev("Loading activity diagram of " + newValue + "Before : " + oldValue);
			TraceManager.addDev(""+docCopy);

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
					//TraceManager.addDev("Name of activity diagram=" + name);

					if (name.equals(oldValue)) {
						int indexDesign = mgui.getMajorIndexOf(tdp);

						if (indexDesign < 0) {
							throw new MalformedModelingException();
						}

						tadp = mgui.getActivityDiagramPanel(indexDesign, newValue);

						//TraceManager.addDev("Searching panel");

						if (tadp == null) {
							throw new MalformedModelingException();
						}

						//TraceManager.addDev("Panel ok");

						decX = 0; decY = 0; decId = 0;
						
						

						tadp.removeAll();
						
						loadDiagramInformation(elt, tadp);
						
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tadp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tadp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tadp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(tadp);
						tadp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(tadp, 0);
					}
				}
			}
			decX = decXTmp;
			decY = decYTmp;
			decId = decIdTmp;
		} catch (SAXException saxe) {
			TraceManager.addError("Loading 701 " + saxe.getMessage());
			throw new MalformedModelingException();
		}
	}
	
	public void loadAvatarSMD(TDiagramPanel tdp, String oldValue, String newValue) throws MalformedModelingException {
		TraceManager.addDev("---> Load activity diagram of old=" + oldValue + " new=" + newValue);
		try {
			NodeList smdNl = docCopy.getElementsByTagName("AVATARStateMachineDiagramPanel");

			//TraceManager.addDev("Loading state machine diagram of " + newValue + " Before : " + oldValue);
			//TraceManager.addDev("smdNL: " + smdNl);

			if (smdNl == null) {
				TraceManager.addDev("AVATAR: null doc");
				throw new MalformedModelingException();
			}

			Node adn;
			Element elt;
			AvatarSMDPanel asmdp;
			String name;
			int decXTmp = decX;
			int decYTmp = decY;
			int decIdTmp = decId;

			for(int i=0; i<smdNl.getLength(); i++) {
				adn = smdNl.item(i);
				if (adn.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element) adn;
					// class diagram name
					name = elt.getAttribute("name");
					TraceManager.addDev("Name of activity diagram=" + name);

					if (name.equals(oldValue)) {
						int indexDesign = mgui.getMajorIndexOf(tdp);

						if (indexDesign < 0) {
							throw new MalformedModelingException();
						}

						asmdp = mgui.getAvatarSMDPanel(indexDesign, newValue);

						TraceManager.addDev("Searching panel: " + newValue);

						if (asmdp == null) {
							throw new MalformedModelingException();
						}

						TraceManager.addDev("Panel ok");

						decX = 0; decY = 0; decId = 0;
						
						
						asmdp.removeAll();
						
						loadDiagramInformation(elt, asmdp);
						
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), asmdp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), asmdp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), asmdp);
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " real points");
						connectConnectorsToRealPoints(asmdp);
						asmdp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + tadp.getName() + " post loading");
						makePostLoading(asmdp, 0);
					}
				}
			}
			decX = decXTmp;
			decY = decYTmp;
			decId = decIdTmp;
		} catch (SAXException saxe) {
			TraceManager.addError("Loading 801 " + saxe.getMessage());
			throw new MalformedModelingException();
		}
	}

	public void loadTMLActivityDiagram(TDiagramPanel tdp, String oldValue, String newValue) throws MalformedModelingException {
		//TraceManager.addDev("---> Load TML activity diagram");
		try {
			if (docCopy == null) {
				TraceManager.addDev("Null doc copy");
			}
			NodeList activityDiagramNl = docCopy.getElementsByTagName("TMLActivityDiagramPanel");

			//TraceManager.addDev("Loading activity diagram of " + newValue + "Before : " + oldValue);

			if (activityDiagramNl == null) {
				//TraceManager.addDev("Null");
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
					//TraceManager.addDev("Name of activity diagram=" + name);

					if (name.equals(oldValue)) {
						int indexDesign = mgui.getMajorIndexOf(tdp);

						if (indexDesign < 0) {
							throw new MalformedModelingException();
						}

						tmladp = mgui.getTMLActivityDiagramPanel(indexDesign, newValue);

						//TraceManager.addDev("Searching panel");

						if (tmladp == null) {
							throw new MalformedModelingException();
						}

						//TraceManager.addDev("Panel ok");

						decX = 0; decY = 0; decId = 0;

						tmladp.removeAll();
						
						loadDiagramInformation(elt, tmladp);
						
						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " components");
						makeXMLComponents(elt.getElementsByTagName("COMPONENT"), tmladp);
						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " connectors");
						makeXMLConnectors(elt.getElementsByTagName("CONNECTOR"), tmladp);
						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " subcomponents");
						makeXMLComponents(elt.getElementsByTagName("SUBCOMPONENT"), tmladp);
						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " real points");
						connectConnectorsToRealPoints(tmladp);
						tmladp.structureChanged();
						//TraceManager.addDev("Activity diagram : " + tmladp.getName() + " post loading");
						makePostLoading(tmladp, 0);
					}
				}
			}
			decX = decXTmp;
			decY = decYTmp;
			decId = decIdTmp;
		} catch (SAXException saxe) {
			TraceManager.addError("Loading 901 " + saxe.getMessage());
			throw new MalformedModelingException();
		}
	}

	public void makePostLoading(TDiagramPanel tdp, int beginIndex) throws MalformedModelingException{
		TGComponent tgc;

		//TraceManager.addDev("Post loading of diagram " + tdp.toString());

		LinkedList list = tdp.getComponentList();

		for(int i=0; i<list.size()-beginIndex; i++) {
			tgc = (TGComponent)(list.get(i));
			//TraceManager.addDev(tgc.getName());
			//TraceManager.addDev(tgc.getValue());
			tgc.makePostLoading(decId);
		}

		//TraceManager.addDev("Post loading of diagram " + tdp.toString() + " achieved");
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
					//TraceManager.addDev("About to add component= " + tgc);
					if ((tgc != null) && (tgc.getFather() == null)) {
						//TraceManager.addDev("Component added to diagram tgc=" + tgc);
						tdp.addBuiltComponent(tgc);
					} else {
						//TraceManager.addDev("Component not added to diagram");
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
		//TraceManager.addDev(n.toString());
		TGComponent tgc = null;
		TGComponent father;

		//
		try {

			NodeList nl = n.getChildNodes();
			elt = (Element)n;
			elt1 = elt;
			//TraceManager.addDev("elt=" + elt);

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
			boolean invariant = false;
			boolean breakpoint = false;
			boolean hidden = false;
			boolean masterMutex = false;

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
					} else if (elt.getTagName().equals("invariant")) {
						invariant = true;
					} else if (elt.getTagName().equals("mastermutex")) {
						masterMutex = true;
					} else if (elt.getTagName().equals("breakpoint")) {
						breakpoint = true;
					}
				}
			}

			if ((myId == -1) || (myX == -1) || (myY == -1) || (myWidth == -1) || (myHeight == -1)) {
				throw new MalformedModelingException();
			}

			//TraceManager.addDev("Making TGComponent of type " + myType + " and of name " + myName);
			//TGComponent is ready to be built
			if(fatherId != -1) {
				fatherId += decId;
				// internal component
				//TraceManager.addDev("I am " + myName);
				//TraceManager.addDev("Searching for component with id " + fatherId);
				father = tdp.findComponentWithId(fatherId);
				if (father == null) {
					throw new MalformedModelingException();
				}

				//TraceManager.addDev("Done");
				//TraceManager.addDev("Father My value is " + father.getValue());
				//TraceManager.addDev("My class is " + father.getClass());

				//TraceManager.addDev("Searching for component " + fatherNum + " at " + tgc.getName());
				tgc = father.getInternalTGComponent(fatherNum);

				if (tgc == null) {
					// To be added to its father -> swallow component
					if (father instanceof SwallowTGComponent) {
						//TraceManager.addDev("1 Must add the component to its father:");
						tgc = TGComponentManager.addComponent(myX, myY, myType, tdp);
						//TraceManager.addDev("2 Must add the component to its father:" + tgc);
						if (tgc instanceof SwallowedTGComponent) {
							//TraceManager.addDev("3 Must add the component to its father:");
							((SwallowTGComponent)father).addSwallowedTGComponent(tgc, myX, myY);
							//TraceManager.addDev("Swallowed to father = " + father.getValue() + ". My name=" + myName + " decId=" + decId);
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
					//TraceManager.addDev("set cd of " + tgc.getName());
				}
			} else {
				tgc = TGComponentManager.addComponent(myX, myY, myType, tdp);
			}
			//TraceManager.addDev("TGComponent built " + myType);

			if (tgc == null) {
				throw new MalformedModelingException();
			}

			if (myName != null) {
				tgc.setName(myName);
			}
			
			tgc.setHidden(hidden);

			/*if (tgc instanceof TCDTObject) {
                TraceManager.addDev("Loading " + myValue);
            }*/

			String oldClassName = myValue;
			//TraceManager.addDev("Old class name=" + oldClassName);
			//Added by Solange
			if ((myValue != null) && (!myValue.equals(null))){
				if (tgc instanceof ProCSDComponent)
				{
					//Added by Solange
					//And removed by emil 
					//myValue=generateNameIfInUse(myValue); 
					//  tgc.setValueWithChange(myValue);
					//TraceManager.addDev("myValue=" + myValue);
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
				
				if ((tgc instanceof AvatarBDBlock) && (decId >0)){
					if (tdp.isAlreadyAnAvatarBDBlockName(myValue)) {
						myValue = tdp.findAvatarBDBlockName(myValue+"_");
					}
				}
				
				if ((tgc instanceof TMLCPrimitiveComponent) && (decId >0)){
					if (tdp.isAlreadyATMLPrimitiveComponentName(myValue)) {
						myValue = tdp.findTMLPrimitiveComponentName(myValue+"_");
					}
					//TraceManager.addDev("MyValue=" + myValue);
				}
				if ((tgc instanceof TOSClass) && (decId >0)){
					if (tdp.isAlreadyATOSClassName(myValue)) {
						myValue = tdp.findTOSClassName(myValue+"_");
					}
				}
				//TraceManager.addDev("myValue=" + myValue);
				tgc.setValueWithChange(myValue);
				//TraceManager.addDev("value done");
				if ((tgc instanceof TCDTClass) && (decId >0)){
					loadActivityDiagram(tdp, oldClassName, myValue);
				}

				if ((tgc instanceof AvatarBDBlock) && (decId >0)){
					//TraceManager.addDev("Going to load ad of task " + oldClassName + " myValue=" + myValue);
					loadAvatarSMD(tdp, oldClassName, myValue);
				}
				
				if ((tgc instanceof TMLTaskOperator) && (decId >0)){
					//TraceManager.addDev("Going to load ad of task " + oldClassName + " myValue=" + myValue);
					loadTMLActivityDiagram(tdp, oldClassName, myValue);
				}
				
				if ((tgc instanceof TMLCPrimitiveComponent) && (decId >0)){
					//TraceManager.addDev("Going to load ad of component " + oldClassName + " myValue=" + myValue);
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
			
			//TraceManager.addDev("Options set");

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
			
			if (invariant) {
				tgc.setCheckableInvariant(invariant);
			}
			
			if (masterMutex) {
				tgc.setMasterMutex(true);
			}
			
			if (breakpoint) {
				tgc.setBreakpoint(breakpoint);
			}

			//extra param
			//TraceManager.addDev("Extra params" + tgc.getClass());
			//TraceManager.addDev("My value = " + tgc.getValue());
			tgc.loadExtraParam(elt1.getElementsByTagName("extraparam"), decX, decY, decId);
			//TraceManager.addDev("Extra param ok");

			if ((tgc instanceof TCDTObject) && (decId > 0)) {
				TCDTObject to = (TCDTObject)tgc;
				//TraceManager.addDev("Setting TObject name to: " + to.getObjectName());
				//TraceManager.addDev("Setting TObject name to: " + tdp.findTObjectName(to.getObjectName()));
				to.setObjectName(tdp.findTObjectName(to.getObjectName()));
			}

			//TraceManager.addDev(tgc.toString());

			//TraceManager.addDev("Making connecting points " + tgcpList.size());
			for(i=0; i<tgcpList.size(); i++) {
				p = (Point)(tgcpList.elementAt(i));
				if (!tgc.setIdTGConnectingPoint(p.x, p.y)) {
					//TraceManager.addDev("Warning: a connecting point has been removed");
					//throw new MalformedModelingException();
				}
			}

			//TraceManager.addDev("Not yet except!");
			if (decId >0) {
				tdp.bringToFront(tgc);
			}
			//TraceManager.addDev("Connecting points done " + myType);

			/*if (tgc instanceof TCDTObject) {
                TraceManager.addDev("getValue " + tgc.getValue());
            }*/

		} catch (Exception e) {
			TraceManager.addError("Exception XML Component "/* + e.getMessage() + "trace=" + e.getStackTrace()*/);
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
		//TraceManager.addDev("Make post processing!");
		if (tdp instanceof TClassDiagramPanel) {
			((TClassDiagramPanel)tdp).makePostLoadingProcessing();
		}
		//TraceManager.addDev("Post processing is over");
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
					TraceManager.addDev("Connector error");
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
		//TraceManager.addDev("Valid connectors ?");
		for(i=0; i<list.size(); i++) {
			tgc = (TGComponent)(list.get(i));
			if (tgc instanceof TGConnector) {
				tgco = (TGConnector)tgc;
				p1 = tgco.getTGConnectingPointP1();
				p2 = tgco.getTGConnectingPointP2();
				if ((p1 instanceof TGConnectingPointTmp) && (p2 instanceof TGConnectingPointTmp)){
					//TraceManager.addDev("Searching for id " + p1.getId());
					p3 = tdp.findConnectingPoint(p1.getId());
					//TraceManager.addDev("Searching for id " + p2.getId());
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
								//TraceManager.addDev("Error on first id");
							}
							if (p4 == null) {
								//TraceManager.addDev("Error on second id");
							}
							tgcoinfo = new TGConnectorInfo();
							tgcoinfo.connector = tgco;
							pendingConnectors.add(tgcoinfo);
							TraceManager.addDev("One connector added to pending list");
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
			TraceManager.addDev("Connecting error: " + connectorsToRemove.size()  + " connectors have been removed");
			throw new MalformedModelingException();
		}*/
	}
	
	public void makeLastLoad() {
		// Update references on all diagrams
		//TraceManager.addDev("Updating ports");
		//mgui.updateAllPorts();
		
		// Update ports on all diagrams
		//TraceManager.addDev("Updating references / ports");
		mgui.updateAllReferences();
		
		mgui.updateAllPorts();
		
		//TraceManager.addDev("Pending connectors");
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
						TraceManager.addDev("Searching for id " + p1.getId());
						p3 = tdp.findConnectingPoint(p1.getId());
						TraceManager.addDev("Searching for id " + p2.getId());
						p4 = tdp.findConnectingPoint(p2.getId());
						if ((p3 == null) ||(p4 == null)) {
							//warning = true;
							if (p3 == null) {
								TraceManager.addDev("Error on first id");
							}
							if (p4 == null) {
								TraceManager.addDev("Error on second id");
							}
							TraceManager.addDev("One connector ignored");
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
		//TraceManager.addDev("Last load done");
	}

	public TGConnector makeXMLConnector(Node n, TDiagramPanel tdp) throws SAXException, MalformedModelingException {
		Element elt, elt1;
		TGConnector tgco = null;
		//TGComponent tgc = null;

		//TraceManager.addDev(n.toString());

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
							//TraceManager.addDev("maxWidth = " +  elt.getAttribute("maxWidth"));
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
						//TraceManager.addDev("P1id = " + tmpid);
					} else if (elt.getTagName().equals("P2")) {
						tmpx = Integer.decode(elt.getAttribute("x")).intValue() + decX;
						tmpy = Integer.decode(elt.getAttribute("y")).intValue() + decY;
						tmpid = Integer.decode(elt.getAttribute("id")).intValue() + decId;
						TGComponent tgc2 = TGComponentManager.addComponent(tmpx, tmpy, TGComponentManager.TAD_START_STATE, tdp);
						p2 = new TGConnectingPointTmp(tgc2, tmpx, tmpy, tmpid);
						//TraceManager.addDev("P2id = " + tmpid);
					} else if (elt.getTagName().equals("Point")) {
						tmpx = Integer.decode(elt.getAttribute("x")).intValue() + decX;
						tmpy = Integer.decode(elt.getAttribute("y")).intValue() + decY;
						pointList.add(new Point(tmpx, tmpy));
					} else if (elt.getTagName().equals("TGConnectingPoint")) {
						x = Integer.decode(elt.getAttribute("num")).intValue();
						y = Integer.decode(elt.getAttribute("id")).intValue() + decId;
						tgcpList.add(new Point(x, y));
						//TraceManager.addDev(" adding Connecting point !");
					} else if (elt.getTagName().equals("AutomaticDrawing")) {
						//TraceManager.addDev("AutomaticDrawing=" + elt.getAttribute("data"));
						if (elt.getAttribute("data").compareTo("true") == 0) {
							automaticDrawing = true;
							//TraceManager.addDev("set to true");
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
			//TraceManager.addDev("Making TGConnector of type " + myType);
			tgco = TGComponentManager.addConnector(myX, myY, myType, tdp, p1, p2, pointList);
			//TraceManager.addDev("TGConnector built " + myType);

			if (tgco == null) {
				TraceManager.addDev( "TGCO is null myType: " + myType );
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

			//TraceManager.addDev("Making connecting points " + myType);
			for(i=0; i<tgcpList.size(); i++) {
				p = (Point)(tgcpList.elementAt(i));
				if (!tgco.setIdTGConnectingPoint(p.x, p.y)) {
					throw new MalformedModelingException();
				}
			}

			if (decId >0) {
				tdp.bringToFront(tgco);
			}

			//TraceManager.addDev("Connecting points done " + myType);

		} catch (Exception e) {
			TraceManager.addError("Exception on connectors: HERE I AM");
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
		//TraceManager.addDev("Step 02");

		mgui.setMode(mgui.VIEW_SUGG_DESIGN_KO);

		//TraceManager.addDev("Step 1");
		try {
			tm = sd.toTURTLEModeling();
			tmState = 0;
		} catch (SDTranslationException e) {
			checkingErrors = new Vector();
			CheckingError error = new CheckingError(CheckingError.STRUCTURE_ERROR, e.getMessage());
			checkingErrors.add(error);

			throw new AnalysisSyntaxException("Problem during translation to a design TURTLE modeling");
		}

		//TraceManager.addDev("Step 2");

		if (checkingErrors != null) {
			return false;
		}

		// modeling is built
		// Now check it !
		//TraceManager.addDev("Step 3");
		TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
		checkingErrors = tmc.syntaxAnalysisChecking();
		//TraceManager.addDev("Step 4");

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
	
	public void generateIOD(HMSC _hmsc, MSC _msc) {
		MSCDrawer mscd = new MSCDrawer(mgui);
		mscd.setHMSC(_hmsc);
		mscd.setMSC(_msc);
		mscd.drawFromMSC(nbSuggestedDesign);
		nbSuggestedDesign ++;
		mgui.changeMade(null, -1);
	}
	

	public boolean translateDeployment(DeploymentPanel dp) {
		// Builds a TURTLE modeling from a deployment diagram
		TraceManager.addDev("deployement");
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
						TraceManager.addDev("Found tclass: " + tgc.getValue());
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
							TraceManager.addDev("Setting package name of " + t.getName() + " to " + node.getNodeName());
							t.setPackageName(node.getNodeName()+"_"+art.getValue());
						}
					}
				}
			}
		}

		// Second step : dealing with links!

		DDTranslator ddt = new DDTranslator(dp, tm, listE);

		try {
			TraceManager.addDev("Dealing with links!");
			ddt.translateLinks();
		} catch (DDSyntaxException e) {
			//throw new AnalysisSyntaxException("Problem during translation to a design TURTLE modeling");
			TraceManager.addDev("Error during translation: " + e.getMessage());
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
		TraceManager.addDev("Translating NC");
		checkingErrors = new Vector();
		warnings = new Vector();
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		
		GNCModeling gncm = new GNCModeling(ncp);
		ncs = gncm.translateToNCStructure();
		listE = gncm.getCorrespondanceTable();
		
		checkingErrors = gncm.getCheckingErrors();
		warnings = gncm.getCheckingWarnings();
		
		TraceManager.addDev("errors:" + checkingErrors.size() + " warnings:" + warnings.size());
		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {
			// Generate XML file
			try {
				String fileName = "network.xml";
				if (ConfigurationTTool.NCDirectory != null) {
					fileName = ConfigurationTTool.NCDirectory + fileName;
				}
				TraceManager.addDev("Saving in network structure in file: " + fileName);
				FileUtils.saveFile(fileName, ncs.toISAENetworkXML());
				fileName = "traffics.xml";
				if (ConfigurationTTool.NCDirectory != null) {
					fileName = ConfigurationTTool.NCDirectory + fileName;
				}
				TraceManager.addDev("Saving in traffics in file: " + fileName);
				FileUtils.saveFile(fileName, ncs.toISAETrafficsXML());
				TraceManager.addDev("Save done");
			} catch (FileException fe) {
				TraceManager.addError("Could not save NC in file:" + fe.getMessage());
			}
			mgui.setMode(MainGUI.NC_OK);
			return true;
		}
		
	}


	public boolean translateTMLDesign(Vector tasksToTakeIntoAccount, TMLDesignPanel tmldp, boolean optimize) {
		ArrayList<TMLError> warningsOptimize = new ArrayList<TMLError>();
		warnings = new Vector();
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		
		GTMLModeling gtmlm = new GTMLModeling(tmldp, true);
		gtmlm.setTasks(tasksToTakeIntoAccount);
		tmlm = gtmlm.translateToTMLModeling(true);
		//tmlm.removeAllRandomSequences();
		TraceManager.addDev("New TML Modeling:" + tmlm.toString());
		mgui.generateTMLTxt();
		artificialtmap = tmlm.getDefaultMapping();
		tmap = null;
		listE = gtmlm.getCorrespondanceTable();

		//TraceManager.addDev("TML Modeling translated");
		//TraceManager.addDev("----- TML Modeling -----");
		//TraceManager.addDev(tmlm.toString());
		//TraceManager.addDev("------------------------");
		checkingErrors = gtmlm.getCheckingErrors();

		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			analyzeErrors();
			return false;
		} else {
			if (optimize) {
				warningsOptimize = tmlm.optimize();
			}
			
			tmState = 2;
			mgui.resetAllDIPLOIDs();
			listE.useDIPLOIDs();
			return true;
			//TraceManager.addDev("tm generated:");
			//tm.print();
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
		
		GTMLModeling gctmlm = new GTMLModeling(tmlcdp, true);
        gctmlm.putPrefixName(true);
		gctmlm.setComponents(componentsToTakeIntoAccount);
		tmlm = gctmlm.translateToTMLModeling(true);
		mgui.generateTMLTxt();
		artificialtmap = tmlm.getDefaultMapping();
		tmap = null;
		listE = gctmlm.getCorrespondanceTable();
		
		//TraceManager.addDev("TML Modeling translated");
		//TraceManager.addDev("----- TML Modeling -----");
		//TraceManager.addDev(tmlm.toString());
		//TraceManager.addDev("------------------------");
		//mgui.generateTMLTxt();
		checkingErrors = gctmlm.getCheckingErrors();

		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			analyzeErrors();
			return false;
		} else {
			if (optimize) {
				//TraceManager.addDev("OPTIMIZE");
				warningsOptimize = tmlm.optimize();
			}

			tmState = 2;
			//TraceManager.addDev("tm generated:");
			mgui.resetAllDIPLOIDs();
			listE.useDIPLOIDs();
			return true;
			//tm.print();
		}
	}
	
	public boolean translateTMLModeling() {
		TML2TURTLE tt = new TML2TURTLE(tmlm);
		tm = tt.generateTURTLEModeling();
		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			analyzeErrors();
			return false;
		} else {
			// Optimize
			//TraceManager.addDev("Optimize");
			tm.optimize();
			//TraceManager.addDev("Optimize done");
			TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
			checkingErrors = tmc.syntaxAnalysisChecking();
			if ((checkingErrors != null) && (checkingErrors.size() > 0)){
				analyzeErrors();
				return false;
			} else {
				mgui.setMode(MainGUI.GEN_DESIGN_OK);
				return true;
			}
		}
	}
	
	public boolean checkSyntaxTMLMapping(Vector nodesToTakeIntoAccount, TMLArchiPanel tmlap, boolean optimize) {
		ArrayList<TMLError> warningsOptimize = new ArrayList<TMLError>();		
		warnings = new Vector();
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		//TraceManager.addDev("New TML Mapping");
		GTMLModeling gtmlm = new GTMLModeling(tmlap, true);
		
		
		gtmlm.setNodes(nodesToTakeIntoAccount);	//simply transforms the parameter from a Vector to LinkedList
		tmlm = null;
		tm = null;
		tmState = 1;
		tmap = gtmlm.translateToTMLMapping();
		
		listE = gtmlm.getCorrespondanceTable();
		
		checkingErrors = gtmlm.getCheckingErrors();
		
		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			analyzeErrors();
			warnings = gtmlm.getCheckingWarnings();
			return false;
		} else {
			//tmap.removeAllRandomSequences();
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

	//Newly introduced to perform Syntax check of CP diagrams. Actually the mapping of CPs onto the architecture is done via SDs,
	//onto the application is done onto blocks in the architecture. It would be better to have all the mapping information in one
	//diagram. Up to now, not taking the mapping information into account
	public boolean checkSyntaxTMLCP( Vector nodesToTakeIntoAccount, TMLCommunicationPatternPanel tmlcpp, boolean optimize ) {

		//nodesToTakeIntoAccount is the list of SDs and ADs corresponding that compose the CP selected for syntax checking
		ArrayList<TMLError> warningsOptimize = new ArrayList<TMLError>();		
		warnings = new Vector();
		mgui.setMode( MainGUI.VIEW_SUGG_DESIGN_KO );
		GTMLModeling gtmlm = new GTMLModeling( tmlcpp, true );

		TraceManager.addDev( "NodesToTakeIntoAccount :" + nodesToTakeIntoAccount.toString() );
		
		//Useless because nodesToTakeIntoAccount does not include the mainCP!
		//gtmlm.setDiagramPanels( nodesToTakeIntoAccount );	//passes the list of nodes (SDs and ADs) to gtml as a LinkedList
		tmlm = null;
		tm = null;
		tmState = 1;
		//tmlcp is the data structure for a CP corresponding to the graphical description with diagrams
		tmlcp = gtmlm.translateToTMLCPDataStructure( tmlcpp.getName() );
		//tmlcp.toString();
		/*TraceManager.addDev( "I AM ABOUT TO GENERATE THE TMLtxt CODE!" );
		mgui.generateTMLTxt();	//Now generating the TMLtxt code
		TraceManager.addDev( "TMLtxt CODE GENERATION DONE" );*/
		listE = gtmlm.getCorrespondanceTable();
		//for( CorrespondanceTGElement element : listE.getNames() )	{
			TraceManager.addDev( "Printing listE.getNames: " + listE.getNames().toString() );
			TraceManager.addDev( "Printing listE.getTG: " + listE.getTG().toString() );
			TraceManager.addDev( "Printing listE.getPanelNames: " + listE.getPanelNames().toString() );
			TraceManager.addDev( "Printing listE.getData: " + listE.getData().toString() );
		//}
		checkingErrors = gtmlm.getCheckingErrors();
		
		if( (checkingErrors != null) && (checkingErrors.size() > 0) )	{
			//analyzeErrors();
			
			return false;
		}
		/*else {
			//tmcp.removeAllRandomSequences();
			if( optimize )	{
				warningsOptimize = tmap.optimize();
				//warningsOptimize = tmcp.optimize();
			}
			warnings.addAll( convertToCheckingErrorTMLErrors(warningsOptimize, tmlcpp.tmlcpp ) );
			mgui.resetAllDIPLOIDs();
			listE.useDIPLOIDs();
			mgui.setMode( MainGUI.GEN_DESIGN_OK );
			return true;
		}*/
		return true;	//It means that there are no errors
	}
	
	public boolean translateTMLMapping(boolean _sample, boolean _channel, boolean _event, boolean _request, boolean _exec, boolean _busTransfers, boolean _scheduling, boolean _taskState, boolean _channelState, boolean _branching, boolean _terminateCPU, boolean _terminateCPUs, boolean _clocked, String _tickValue, boolean _endClocked, boolean _countTick, boolean _maxCountTick, String _maxCountTickValue, boolean _randomTask) {
		//TraceManager.addDev("TML=" + tmap.toString());
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
		//TraceManager.addDev("tm=" + sb);
		
		TraceManager.addDev("tm generated from TMAP");
		checkingErrors = m2tif.getCheckingErrors();
		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		}
		mgui.setMode(MainGUI.GEN_DESIGN_OK);
		return true;
	}

	//Added by Solange
	public void generateLists(ProactiveDesignPanel pdp) {
		gpdtemp = new GProactiveDesign(pdp);	
	}
	//

	public boolean translateTURTLEOSDesign(TURTLEOSDesignPanel tosdp) {
		warnings = new Vector();
		mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
		//TraceManager.addDev("New TML Modeling");
		GTURTLEOSModeling gosm = new GTURTLEOSModeling(tosdp);
		//gtmlm.setTasks(tasksToTakeIntoAccount);
		//tmlm = gosm.translateToTMLModeling();
		//TraceManager.addDev("TML Modeling translated");
		//TraceManager.addDev("----- TML Modeling -----");
		//TraceManager.addDev(tmlm.toString());
		//TraceManager.addDev("------------------------");
		tm = gosm.generateTURTLEModeling();
		tmState = 0;
		checkingErrors = gosm.getCheckingErrors();

		if ((checkingErrors != null) && (checkingErrors.size() > 0)){
			return false;
		} else {

			//TraceManager.addDev("Optimize");
			tm.optimize();
			//TraceManager.addDev("Optimize done");
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
			TraceManager.addDev("Errors found");
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
	
	private void analyzeErrors() {
		CheckingError ce;
		TGComponent tgc;
		
		for(int i=0; i<checkingErrors.size(); i++) {
			ce = (CheckingError)(checkingErrors.get(i));
			if (ce != null) {
				tgc = ce.getTGComponent();
				if (tgc != null) {
					analyzeErrorOnComponent(tgc);
				}
			}
		}
	}
	
	private void analyzeErrorOnComponent(TGComponent _tgc) {
		if (_tgc instanceof BasicErrorHighlight) {
			((BasicErrorHighlight)_tgc).setStateAction(ErrorHighlight.UNKNOWN);
		} else if (_tgc instanceof ActionStateErrorHighlight) {
			((ActionStateErrorHighlight)_tgc).setStateAction(ErrorHighlight.UNKNOWN_AS);
		}
	}
	
	public boolean makeEBRDD(EBRDDPanel tdp) {
		EBRDDTranslator ebrddt = new EBRDDTranslator();
		ebrdd = ebrddt.generateEBRDD(tdp, tdp.getName());
		warnings = ebrddt.getWarnings();
		checkingErrors = ebrddt.getErrors();
		if (checkingErrors.size() > 0) {
			return false;
		}
		TraceManager.addDev("the EBRDD:\n" + ebrdd.toString());
		return true;
	}
	
	public void setElementsOfSearchTree(Vector<Object> elements) {
		st.setElements(elements);
	}

}
