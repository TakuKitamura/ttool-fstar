/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT telecom-paristech.fr
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

package ui;

import avatartranslator.*;
import avatartranslator.toproverif.AVATAR2ProVerif;
import avatartranslator.totpn.AVATAR2TPN;
import avatartranslator.toturtle.AVATAR2TURTLE;
import avatartranslator.touppaal.AVATAR2UPPAAL;
import common.ConfigurationTTool;
import ddtranslator.DDSyntaxException;
import ddtranslator.DDTranslator;
import launcher.LauncherException;
import launcher.RemoteExecutionThread;
import launcher.RshClient;
import myutil.*;
import nc.NCStructure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import proverifspec.ProVerifOutputAnalyzer;
import proverifspec.ProVerifQueryAuthResult;
import proverifspec.ProVerifQueryResult;
import proverifspec.ProVerifSpec;
import req.ebrdd.EBRDD;
import sddescription.HMSC;
import sddescription.MSC;
import sddescription.SDExchange;
import sdtranslator.SDTranslationException;
import sdtranslator.SDTranslator;
import tmatrix.RequirementModeling;
import tmltranslator.*;
import tmltranslator.modelcompiler.TMLModelCompiler;
import tmltranslator.toautomata.TML2AUT;
import tmltranslator.toautomata.TML2AUTviaLOTOS;
import tmltranslator.toavatar.TML2Avatar;
import tmltranslator.tosystemc.TML2SystemC;
import tmltranslator.toturtle.Mapping2TIF;
import tmltranslator.toturtle.TML2TURTLE;
import tmltranslator.touppaal.RelationTMLUPPAAL;
import tmltranslator.touppaal.TML2UPPAAL;
import tpndescription.TPN;
import translator.*;
import translator.totpn.TURTLE2TPN;
import translator.touppaal.RelationTIFUPPAAL;
import translator.touppaal.TURTLE2UPPAAL;
import ui.ad.TActivityDiagramPanel;
import ui.atd.AttackTreeDiagramPanel;
import ui.avatarad.AvatarADPanel;
import ui.avatarbd.*;
import ui.avatarcd.AvatarCDPanel;
import ui.avatardd.ADDDiagramPanel;
import ui.avatarmad.AvatarMADPanel;
import ui.avatarmethodology.AvatarMethodologyDiagramPanel;
import ui.avatarpd.AvatarPDPanel;
import ui.avatarrd.AvatarRDPanel;
import ui.avatarsmd.*;
import ui.cd.TCDTClass;
import ui.cd.TCDTObject;
import ui.cd.TClassDiagramPanel;
import ui.dd.TDDArtifact;
import ui.dd.TDDNode;
import ui.dd.TDeploymentDiagramPanel;
import ui.diplodocusmethodology.DiplodocusMethodologyDiagramPanel;
import ui.ebrdd.EBRDDPanel;
import ui.graph.RG;
import ui.iod.InteractionOverviewDiagramPanel;
import ui.ncdd.NCDiagramPanel;
import ui.osad.TURTLEOSActivityDiagramPanel;
import ui.oscd.TOSClass;
import ui.oscd.TURTLEOSClassDiagramPanel;
import ui.procsd.ProCSDComponent;
import ui.procsd.ProactiveCSDPanel;
import ui.prosmd.ProactiveSMDPanel;
import ui.req.Requirement;
import ui.req.RequirementDiagramPanel;
import ui.sysmlsecmethodology.SysmlsecMethodologyDiagramPanel;
import ui.tmlad.*;
import ui.tmlcd.TMLTaskDiagramPanel;
import ui.tmlcd.TMLTaskOperator;
import ui.tmlcompd.*;
import ui.tmlcp.TMLCPPanel;
import ui.tmldd.*;
import ui.tmlsd.TMLSDPanel;
import ui.tree.GraphTree;
import ui.tree.InvariantDataTree;
import ui.tree.SearchTree;
import ui.tree.SyntaxAnalysisTree;
import ui.ucd.UseCaseDiagramPanel;
import ui.util.DefaultText;
import ui.util.IconManager;
import ui.window.JFrameSimulationTrace;
import uppaaldesc.UPPAALSpec;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

// AVATAR
// AVATAR

//Communication Pattern javaCC parser
//import compiler.tmlCPparser.*;

/**
 * Class GTURTLEModeling
 * Creation: 09/12/2003
 * Version: 1.1 02/06/2014
 * @author Ludovic APVRILLE
 */
public class GTURTLEModeling {

    //Added by Solange

    public GProactiveDesign gpdtemp;


    //
    private Vector<TURTLEPanel> panels; /* analysis, design, deployment, tml design */
    private TURTLEModeling tm;
    private AvatarSpecification avatarspec;
    //  private AttackTree attackTree;
    private AVATAR2UPPAAL avatar2uppaal;
    private AVATAR2ProVerif avatar2proverif;
    private boolean optimizeAvatar;
    private int tmState; // 0:generated, 1: to be generated from mapping, 2: to be generated from TML modeling

    private TMLModeling<TGComponent> tmlm;
    private TMLMapping<TGComponent> artificialtmap;
    private TMLMapping<TGComponent> tmap;
    private TMLCP tmlcp;
    private TML2Avatar t2a;
    private RequirementModeling rm;
    private NCStructure ncs;
    private final MainGUI mgui;
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

    private List<RG> graphs;
    private GraphTree gt;

    private int nbRTLOTOS;
    private int nbSuggestedDesign;
    //  private int nbSuggestedAnalysis;
    //  private int nbTPN;

    //private ValidationDataTree vdt;
    private SearchTree st;
    private SyntaxAnalysisTree mcvdt;
    private InvariantDataTree idt;

    private List<CheckingError> checkingErrors;
    private List<CheckingError> warnings;

    private List<Invariant> invariants;

    List<TGConnectorInfo> pendingConnectors;

    private Vector<String> savedOperations;
    private Vector<Point> savedPanels;
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



    boolean hasCrypto=false;
    //private Charset chset1, chset2;

    public GTURTLEModeling(MainGUI _mgui, Vector<TURTLEPanel> _panels) {
        mgui = _mgui;
        panels = _panels;
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            dbf = null;
            db = null;
        }
        savedOperations = new Vector<String>();
        savedPanels = new Vector<Point>();
        pointerOperation = -1;

        graphs = new ArrayList<RG>();
        invariants = new LinkedList<Invariant>();

        //vdt = new ValidationDataTree(mgui);
        mcvdt = new SyntaxAnalysisTree(mgui);
        idt = new InvariantDataTree(mgui);
        st = new SearchTree(mgui);
        gt = new GraphTree(mgui);

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

    public List<RG> getRGs() {
        return graphs;
    }

    public void addRG(RG newGraph) {
        //TraceManager.addDev("Adding new graph " + newGraph);
        graphs.add(newGraph);
    }

    public void removeRG(RG oldGraph) {
        //TraceManager.addDev("Adding new graph " + newGraph);
        graphs.remove(oldGraph);
    }


    public List<Invariant> getInvariants() {
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
        tpn = t2tpn.generateTPN().toString();
        warnings = t2tpn.getWarnings();

        //  nbTPN ++;
        if (f != null) {
            TraceManager.addDev("Saving in file: " + f);
            saveInFile(f, tpn);
        }
        languageID = TPN;

        // For debug purpose
        //TraceManager.addDev(tpn);

        mgui.setMode(MainGUI.RTLOTOS_OK);
    }

    public List<String> generateAUT(String path) {
        TML2AUT tml2aut = new TML2AUT(tmlm);
        tml2aut.generateAutomatas(true);
        try {
            return tml2aut.saveInFiles(path);
        } catch (FileException fe) {
            return null;
        }
    }

    public boolean generateCCode( String _title )          {

        //CheckingError ce;
        //int type;
        // TGComponent tgc;
        String applicationName;
        TMLModelCompiler CCode;

        if( tmap == null )        {
            JOptionPane.showMessageDialog( mgui.frame, "C code is only generated from an architecture diagram with mapping information", "Control code generation failed", JOptionPane.INFORMATION_MESSAGE );
            return true;
        }
        // Get the file from DiplodocusPECPragma
        //List<TGComponent> components = mgui.getCurrentArchiPanel().tmlap.getComponentList();
        // Parse the PEC file and the library of code snippets for each DIPLODOCUS unit
        applicationName = tmap.getMappedTasks().get(0).getName().split("__")[0];        // Remember that it works only for one application
        CCode = new TMLModelCompiler( _title, applicationName, mgui.frame, mgui.getAllTMLCP(), tmap );
        CCode.toTextFormat();
        try {
            if( ConfigurationTTool.CCodeDirectory.equals("") )  {
                JOptionPane.showMessageDialog(  mgui.frame,
                                                "No directory for C code generation found in config.xml. The C code cannot be generated.",
                                                "Control code generation failed", JOptionPane.INFORMATION_MESSAGE );
                return true;
            }
            else                {
                CCode.saveFile( ConfigurationTTool.CCodeDirectory + File.separator, applicationName );
            }
        }
        catch( Exception e ) {
            JOptionPane.showMessageDialog(  mgui.frame, "The application C files could not be saved: " + e.getMessage(), "Control code generation failed", JOptionPane.INFORMATION_MESSAGE );
            return true;
        }
        return false;
    }

    public boolean generateTMLTxt( String _title ) {


        //This branch is activated if doing the syntax check from the architecture panel.
        //It generates the text TML for the architecture and the application + mapping information
        if (tmap != null) {
            TMLMappingTextSpecification<TGComponent> spec = new TMLMappingTextSpecification<>( _title );
            spec.toTextFormat( tmap );    //TMLMapping
            try {
                //TraceManager.addDev( "*** " + ConfigurationTTool.TMLCodeDirectory + File.separator );
                spec.saveFile( ConfigurationTTool.TMLCodeDirectory + File.separator, "spec" );
            }
            catch( Exception e ) {
                TraceManager.addError( "Files could not be saved: " + e.getMessage() );
                return false;
            }
        }

        if( tmlcp != null )      {         //Use the data structure filled by translateToTML... and pass it to the appropriate toTextFormat()
            TraceManager.addError( "About to generate the TMLText for CPs" );
            TMLCPTextSpecification specCP = new TMLCPTextSpecification( _title );

            //get the architecture panel and the nodes
            TMLArchiDiagramPanel tmlap = mgui.getTMLArchiDiagramPanels().get(0).tmlap;
            List<TGComponent> components = tmlap.getComponentList();
            Iterator<TGComponent> iterator = components.listIterator();
            TGComponent tgc;

            while(iterator.hasNext()) {
                tgc = iterator.next();

                if (tgc instanceof TMLArchiCPNode) {
                    TMLArchiCPNode node = (TMLArchiCPNode) tgc;
                    TraceManager.addDev( "Found CP node: " + node.getName() );
                    TraceManager.addDev( "with mapping info: " + node.getMappedUnits() );
                }
            }

            List<TMLCommunicationPatternPanel> tmlcpPanelsList = new ArrayList<TMLCommunicationPatternPanel>();
            //get the TMLCommunicationPatternPanels :)
            for( int i = 0; i < mgui.tabs.size(); i++ ) {
                TURTLEPanel panel = mgui.tabs.get(i);

                if( panel instanceof TMLCommunicationPatternPanel )      {
                    tmlcpPanelsList.add( (TMLCommunicationPatternPanel) panel );
                    TraceManager.addDev( "Found TMLCommunicationPatternPanel: " + panel.toString() );
                }
            }

            specCP.toTextFormat( tmlcp );          // the data structure tmlcp is filled with the info concerning the CP panel
            // from which the button is pressed. If there are multiple CP panels this operation must be repeated for each panel. It
            // should be no difficult to implement.
            try {
                specCP.saveFile( ConfigurationTTool.TMLCodeDirectory + File.separator, "spec.tmlcp" );
            }
            catch( Exception e ) {
                TraceManager.addError( "Writing TMLText for CPs, file could not be saved: " + e.getMessage() );
                return false;
            }

        } else if (tmap == null) {
            //This branch is activated if doing the syntax check from the application panel.
            //It only generates the application TML text
            if( tmap == null ) {
                TMLTextSpecification<TGComponent> spec = new TMLTextSpecification<>( _title );
                spec.toTextFormat( tmlm );        //TMLModeling
                try {
                    spec.saveFile( ConfigurationTTool.TMLCodeDirectory + File.separator, "spec.tml" );
                }
                catch( Exception e ) {
                    TraceManager.addError( "File could not be saved: " + e.getMessage() );
                    return false;
                }
            }


        }
        return true;    //temporary, just to check functionality
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
        TraceManager.addDev("Generate UPPAAL from TML");
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
        if (avatarspec == null) {
            TraceManager.addDev("Null avatar spec");
            return false;
        }
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

    public AVATAR2UPPAAL getAvatar2Uppaal(){
        return avatar2uppaal;
    }

    public ProVerifOutputAnalyzer getProVerifOutputAnalyzer () {
        return this.avatar2proverif.getOutputAnalyzer ();
    }

    public boolean generateProVerifFromAVATAR(String _path, int _stateReachability, boolean _typed, boolean allowPrivateChannelDuplication){
        return generateProVerifFromAVATAR(_path, _stateReachability, _typed, allowPrivateChannelDuplication, "1");
    }

    public int calcSec(){
        int overhead=0;
        //count # of insecure channels?
        return overhead;
    }

    public boolean channelAllowed(TMLMapping<TGComponent> map, TMLChannel chan){
        TMLTask orig = chan.getOriginTask();
        TMLTask dest = chan.getDestinationTask();
        List<HwNode> path = getPath(map,orig, dest);
        for (HwNode node:path){
            if (node instanceof HwBridge){
                for (String rule:((HwBridge) node).firewallRules){
                    String t1 = rule.split("->")[0];
                    String t2 = rule.split("->")[1];
                    if (t1.equals(orig.getName().replaceAll("__","::")) || t1.equals("*")){
                        if (t2.equals(dest.getName().replaceAll("__","::")) || t2.equals("*")){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public List<HwNode> getPath(TMLMapping<TGComponent> map, TMLTask t1, TMLTask t2){
        HwNode node1 = map.getHwNodeOf(t1);
        HwNode node2 = map.getHwNodeOf(t2);
        List<HwNode> path = new ArrayList<HwNode>();
        if (node1==node2){
            return path;
        }
        if (node1!=node2){
            //Navigate architecture for node
            List<HwLink> links = map.getTMLArchitecture().getHwLinks();
            //  HwNode last = node1;
            List<HwNode> found = new ArrayList<HwNode>();
            List<HwNode> done = new ArrayList<HwNode>();
            Map<HwNode, List<HwNode>> pathMap = new HashMap<HwNode, List<HwNode>>();
            for (HwLink link: links){
                if (link.hwnode == node1){
                    found.add(link.bus);
                    List<HwNode> tmp = new ArrayList<HwNode>();
                    tmp.add(link.bus);
                    pathMap.put(link.bus, tmp);
                }
            }
            outerloop:
            while (found.size()>0){
                HwNode curr = found.remove(0);
                for (HwLink link: links){
                    if (curr == link.bus){
                        if (link.hwnode == node2){
                            path = pathMap.get(curr);
                            break outerloop;
                        }
                        if (!done.contains(link.hwnode) && !found.contains(link.hwnode) && link.hwnode instanceof HwBridge){
                            found.add(link.hwnode);
                            List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
                            tmp.add(link.hwnode);
                            pathMap.put(link.hwnode, tmp);
                        }
                    }
                    else if (curr == link.hwnode){
                        if (!done.contains(link.bus) && !found.contains(link.bus)){
                            found.add(link.bus);
                            List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
                            tmp.add(link.bus);
                            pathMap.put(link.bus, tmp);
                        }
                    }
                }
                done.add(curr);
            }
        }
        return path;
    }
    public TMLMapping<TGComponent> drawFirewall(TMLMapping<TGComponent> map){
        System.out.println("DRAWING FIREWALL");
		TGComponent comp= map.getTMLModeling().getTGComponent();
		TMLComponentDesignPanel tmlcdp = (TMLComponentDesignPanel) comp.getTDiagramPanel().tp;
       // TMLComponentDesignPanel tmlcdp = map.getTMLCDesignPanel();
        TMLModeling<TGComponent> tmlm = map.getTMLModeling();
		TMLArchitecture archi = map.getArch();
		TURTLEPanel tmlap = map.getCorrespondanceList().getTG(archi.getFirstCPU()).getTDiagramPanel().tp;
        TMLActivityDiagramPanel firewallADP = null;
        TMLComponentTaskDiagramPanel tcdp = tmlcdp.tmlctdp;
        if (TraceManager.devPolicy == TraceManager.TO_CONSOLE){
            MainGUI gui = tmlcdp.getMainGUI();

            int arch = mgui.tabs.indexOf(tmlap);
            gui.cloneRenameTab(arch,"firewallArch");
            TMLArchiPanel newarch = (TMLArchiPanel) gui.tabs.get(gui.tabs.size()-1);
            int ind = gui.tabs.indexOf(tmlcdp);
            String tabName = gui.getTitleAt(tmlcdp);
            gui.cloneRenameTab(ind, "firewallDesign");
            //  TMLComponentDesignPanel tcp = (TMLComponentDesignPanel) gui.tabs.get(gui.tabs.size()-1);
            newarch.renameMapping(tabName, tabName+"_firewallDesign");

        }
        for (HwBridge firewallNode:map.getTMLArchitecture().getFirewalls()){
            TraceManager.addDev("Found firewall " + firewallNode.getName());
            TMLCPrimitiveComponent firewallComp = null;
            TMLADStartState adStart=null;
            TMLADForEverLoop adLoop = null;
            TMLADChoice adChoice =null;
            TMLADReadChannel adRC=null;
            TMLADExecI exec=null;
            TMLADWriteChannel adWC=null;
            TMLADStopState adStop =null;



            int links = map.getArch().getLinkByHwNode(firewallNode).size();
            TraceManager.addDev("Links " + links);

            HwCPU cpu = new HwCPU(firewallNode.getName());
            map.getTMLArchitecture().replaceFirewall(firewallNode,cpu);
            for (int link =0; link< links/2; link++){
                HashMap<TMLChannel, TMLChannel> inChans = new HashMap<TMLChannel, TMLChannel>();
                HashMap<TMLChannel, TMLChannel> outChans = new HashMap<TMLChannel, TMLChannel>();
                if (TraceManager.devPolicy == TraceManager.TO_CONSOLE){
                    firewallComp = new TMLCPrimitiveComponent(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxY(), false, null, tmlcdp.tmlctdp);
                    tmlcdp.tmlctdp.addComponent(firewallComp,0,0,false,true);
                    firewallComp.setValueWithChange(firewallNode.getName()+link);
                    firewallADP = tmlcdp.getTMLActivityDiagramPanel(firewallNode.getName()+link);
                }

                List<TMLChannel> channelsCopy = tmlm.getChannels();
                List<TMLChannel> toAdd = new ArrayList<TMLChannel>();

                TMLTask firewall = new TMLTask("TASK__"+firewallNode.getName()+"_"+link, firewallComp,firewallADP);


                tmlm.addTask(firewall);
                map.addTaskToHwExecutionNode(firewall,cpu);
                TMLActivity act = firewall.getActivityDiagram();

                TraceManager.addDev("FirewallADP " + firewallADP);
                for (TMLChannel chan: channelsCopy){
                    TMLTask orig = chan.getOriginTask();
                    TMLTask dest = chan.getDestinationTask();
                    TMLPort origPort = chan.getOriginPort();
                    TMLPort destPort = chan.getDestinationPort();
                    TMLChannel wr = new TMLChannel(chan.getName()+"_firewallIn"+link,chan.getReferenceObject());
                    //Specify new channel attributes
                    wr.setSize(chan.getSize());
                    wr.setMax(chan.getMax());
                    wr.setPorts(origPort,destPort);
                    wr.setType(TMLChannel.BRBW);
                    wr.setPriority(chan.getPriority());
                    wr.setTasks(orig, firewall);
                    TMLChannel rd = new TMLChannel(chan.getName()+"_firewallOut"+link, chan.getReferenceObject());
                    rd.setTasks(firewall,dest);
                    rd.setSize(chan.getSize());
                    rd.setMax(chan.getMax());
                    rd.setPorts(origPort,destPort);
                    rd.setType(TMLChannel.BRBW);
                    rd.setPriority(chan.getPriority());
                    inChans.put(chan,wr);
                    outChans.put(chan,rd);
                    toAdd.add(rd);
                    toAdd.add(wr);
                    map.getCorrespondanceList().addCor(rd, (TGComponent) rd.getReferenceObject());
                    map.getCorrespondanceList().addCor(wr, (TGComponent) wr.getReferenceObject());
                }
                tmlm.removeAllChannels();
                for (TMLChannel c:toAdd){
                    tmlm.addChannel(c);
                }


                if (TraceManager.devPolicy == TraceManager.TO_CONSOLE){
                    //Build activity diagram
                    //Get start state
                    adStart = (TMLADStartState) firewallADP.getComponentList().get(0);
                    //add loop
                    adLoop = new TMLADForEverLoop(400,150,firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false,null, firewallADP);
                    firewallADP.addComponent(adLoop,400,150,false,true);
                    TGConnector tmp =new TGConnectorTMLAD(adLoop.getX(), adLoop.getY(), firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false, null,firewallADP,adStart.getTGConnectingPointAtIndex(0),adLoop.getTGConnectingPointAtIndex(0), new Vector<Point>());
                    firewallADP.addComponent(tmp, adLoop.getX(),adLoop.getY(),false,true);
                    //add choice
                    adChoice = new TMLADChoice(400,300, firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false,null, firewallADP);
                    firewallADP.addComponent(adChoice, 400,300,false,true);

                    tmp =new TGConnectorTMLAD(adChoice.getX(), adChoice.getY(), firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false, null,firewallADP,adLoop.getTGConnectingPointAtIndex(1), adChoice.getTGConnectingPointAtIndex(0), new Vector<Point>());
                    firewallADP.addComponent(tmp, adChoice.getX(),adChoice.getY(),false,true);
                    for (TMLChannel chan: inChans.keySet()){
                        TMLChannel newChan = inChans.get(chan);
                        TMLCChannelOutPort originPort = new TMLCChannelOutPort(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp);
                        TMLCChannelOutPort destPort = new TMLCChannelOutPort(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp);
                        for (TGComponent tg: tcdp.getComponentList()){
                            if (tg instanceof TMLCPrimitiveComponent){
                                if (tg.getValue().equals(newChan.getOriginTask().getName().split("__")[1])){
                                    originPort = new TMLCChannelOutPort(tg.getX(), tg.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, tg, tcdp);
                                    originPort.commName=newChan.getName();
                                    tcdp.addComponent(originPort,tg.getX(), tg.getY(),true,true);
                                }
                                else if (tg.getValue().equals(firewallNode.getName())){
                                    destPort = new TMLCChannelOutPort(tg.getX(), tg.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, tg, tcdp);
                                    destPort.isOrigin=false;
                                    destPort.commName=newChan.getName();
                                    tcdp.addComponent(destPort,tg.getX(), tg.getY(),true,true);
                                }
                            }
                        }
                        TMLCPortConnector conn = new TMLCPortConnector(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp, originPort.getTGConnectingPointAtIndex(0), destPort.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tcdp.addComponent(conn, 0,0,false,true);

                        TMLChannel wrChan = outChans.get(chan);
                        for (TGComponent tg: tcdp.getComponentList()){
                            if (tg instanceof TMLCPrimitiveComponent){
                                if (tg.getValue().equals(firewallNode.getName())){
                                    originPort = new TMLCChannelOutPort(tg.getX(), tg.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, tg, tcdp);
                                    originPort.commName=wrChan.getName();
                                    tcdp.addComponent(originPort,tg.getX(), tg.getY(),true,true);
                                }
                                else if (tg.getValue().equals(wrChan.getDestinationTask().getName().split("__")[1])){
                                    destPort = new TMLCChannelOutPort(tg.getX(), tg.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, tg, tcdp);
                                    destPort.isOrigin=false;
                                    destPort.commName=wrChan.getName();
                                    tcdp.addComponent(destPort,tg.getX(), tg.getY(),true,true);
                                }
                            }
                        }
                        conn = new TMLCPortConnector(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp, originPort.getTGConnectingPointAtIndex(0), destPort.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tcdp.addComponent(conn, 0,0,false,true);
                    }
                    int xpos=200;
                    int i=1;
                    for (TMLChannel chan: inChans.keySet()){
                        TMLChannel newChan = inChans.get(chan);
                        adRC = new TMLADReadChannel(xpos,350, firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false,null, firewallADP);
                        adRC.setChannelName(newChan.getName());
                        adRC.setSamples("1");

                        tmp =new TGConnectorTMLAD(adRC.getX(), adRC.getY(), firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false, null,firewallADP,adChoice.getTGConnectingPointAtIndex(i), adRC.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        firewallADP.addComponent(tmp, adRC.getX(),adRC.getY(),false,true);

                        firewallADP.addComponent(adRC,xpos,350,false,true);

                        //Execute for latency value
                        exec = new TMLADExecI(xpos,400,firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false,null, firewallADP);

                        exec.setDelayValue(Integer.toString(firewallNode.latency));
                        firewallADP.addComponent(exec,400,200,false,true);

                        tmp =new TGConnectorTMLAD(exec.getX(), exec.getY(), firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false, null,firewallADP,adRC.getTGConnectingPointAtIndex(1), exec.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        firewallADP.addComponent(tmp, exec.getX(),exec.getY(),false,true);

                        if (channelAllowed(map,chan)){
                            TMLChannel wrChan = outChans.get(chan);

                            adWC = new TMLADWriteChannel(xpos,400, firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false,null, firewallADP);
                            adWC.setChannelName(wrChan.getName());
                            adWC.setSamples("1");
                            firewallADP.addComponent(adWC, xpos,400, false,true);

                            tmp =new TGConnectorTMLAD(exec.getX(), exec.getY(), firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false, null,firewallADP,exec.getTGConnectingPointAtIndex(1), adWC.getTGConnectingPointAtIndex(0), new Vector<Point>());
                            firewallADP.addComponent(tmp, exec.getX(),exec.getY(),false,true);

                            adStop = new TMLADStopState(xpos,500, firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false,null, firewallADP);
                            firewallADP.addComponent(adStop, xpos,500, false,true);
                            tmp =new TGConnectorTMLAD(adStop.getX(), adStop.getY(), firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false, null,firewallADP,adWC.getTGConnectingPointAtIndex(1), adStop.getTGConnectingPointAtIndex(0), new Vector<Point>());
                            firewallADP.addComponent(tmp, adStop.getX(),adStop.getY(),false,true);
                        }
                        else {
                            adStop = new TMLADStopState(xpos,500, firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false,null, firewallADP);
                            firewallADP.addComponent(adStop, xpos,500, false,true);

                            tmp =new TGConnectorTMLAD(adStop.getX(), adStop.getY(), firewallADP.getMinX(), firewallADP.getMaxX(), firewallADP.getMinY(), firewallADP.getMaxY(), false, null,firewallADP,exec.getTGConnectingPointAtIndex(1), adStop.getTGConnectingPointAtIndex(0), new Vector<Point>());
                            firewallADP.addComponent(tmp, adStop.getX(),adStop.getY(),false,true);
                        }
                        xpos+=100;
                        i++;
                    }
                }

                TMLStartState start = new TMLStartState("start", adStart);
                act.setFirst(start);
                map.getCorrespondanceList().addCor(start,adStart);

                //Add infinite loop

                TMLForLoop loop = new TMLForLoop("infiniteloop",adLoop);
                loop.setInit("");
                loop.setCondition("");
                loop.setIncrement("");
                loop.setInfinite(true);
                start.addNext(loop);
                act.addElement(loop);
                //Add choice

                TMLChoice choice = new TMLChoice("chooseChannel", adChoice);
                act.addElement(choice);
                loop.addNext(choice);
                map.getCorrespondanceList().addCor(choice,adChoice);




                for (TMLChannel chan: inChans.keySet()){
                    TMLChannel newChan = inChans.get(chan);
                    TMLChannel wrChan = outChans.get(chan);
                    //Add channels into firewall activity diagram
                    TMLReadChannel rd = new TMLReadChannel(newChan.getName(), adRC);
                    rd.setNbOfSamples("1");
                    rd.addChannel(newChan);
                    choice.addNext(rd);
                    choice.addGuard("");

                    act.addElement(rd);

                    //Execute for latency value

                    TMLExecI ex = new TMLExecI("execi", exec);
                    ex.setAction(Integer.toString(firewallNode.latency));
                    act.addElement(ex);
                    rd.addNext(ex);

                    if (channelAllowed(map,chan)){

                        TMLWriteChannel wr = new TMLWriteChannel(wrChan.getName(), adWC);
                        wr.setNbOfSamples("1");
                        wr.addChannel(wrChan);
                        ex.addNext(wr);
                        act.addElement(wr);

                        TMLStopState stop = new TMLStopState("stop", adStop);
                        wr.addNext(stop);
                        act.addElement(stop);

                    }
                    else {
                        TMLStopState stop = new TMLStopState("stop", adStop);
                        ex.addNext(stop);
                        act.addElement(stop);
                    }
                    for (TMLTask t:tmlm.getTasks()){
                        TMLActivity actd = t.getActivityDiagram();
                        actd.replaceWriteChannelWith(chan,newChan);
                        actd.replaceReadChannelWith(chan, outChans.get(chan));
                    }
                }

            }

        }
        //Redo all reference objects

        map.getCorrespondanceList().useDIPLOIDs();
        return map;
    }

    class ChannelData {
        public String name;
        public boolean isOrigin;
        public boolean isChan;
        public ChannelData(String n, boolean orig, boolean isCh){
            name=n;
            isOrigin=orig;
            isChan=isCh;
        }

    }
    class HSMChannel {
        public String name;
        public static final int SENC=0;
        public static final int NONCE_ENC=1;
        public static final int MAC=2;
        public static final int DEC=3;
        public static final int AENC=4;
        public static final int NONCE=5;
        public String task;
        public String securityContext="";
        public int secType;
        public String nonceName="";
        public HSMChannel (String n, String t, int type){
            name=n;
            task=t;
            secType=type;
        }
    }

    public HashMap<String, HashSet<String>> getCPUTaskMap(){
        HashMap<String, HashSet<String>> cpuTaskMap = new HashMap<String, HashSet<String>>();
        if (tmap ==null){
            return cpuTaskMap;
        }

        for (HwNode node: tmap.getArch().getCPUs()){
            if (tmap.getMappedTasks(node).size()>0){
                cpuTaskMap.put(node.getName(), tmap.getMappedTasks(node));
            }
        }

        return cpuTaskMap;
    }

    public void addHSM(MainGUI gui, Map<String, List<String>> selectedCpuTasks){
        System.out.println("Adding HSM");
        String encComp="100";
        String decComp="100";
        String overhead ="0";
        String name="hsm";
        if (tmap==null){
            return;
        }
        //Clone diagrams
		TURTLEPanel tmlap = tmap.getCorrespondanceList().getTG(tmap.getArch().getFirstCPU()).getTDiagramPanel().tp;
        int arch = gui.tabs.indexOf(tmlap);
        gui.cloneRenameTab(arch,"hsm");
        TMLArchiPanel newarch = (TMLArchiPanel) gui.tabs.get(gui.tabs.size()-1);

		TGComponent tgcomp= tmap.getTMLModeling().getTGComponent();
		TMLComponentDesignPanel tmlcdp= (TMLComponentDesignPanel) tgcomp.getTDiagramPanel().tp;

//        TMLComponentDesignPanel tmlcdp = tmap.getTMLCDesignPanel();
        int ind = gui.tabs.indexOf(tmlcdp);
        String tabName = gui.getTitleAt(tmlcdp);
        gui.cloneRenameTab(ind, name);
        TMLComponentDesignPanel t = (TMLComponentDesignPanel) gui.tabs.get(gui.tabs.size()-1);
        TMLComponentTaskDiagramPanel tcdp = t.tmlctdp;
        //Create clone of architecture panel and map tasks to it
        newarch.renameMapping(tabName, tabName+"_"+name);


        //ProVerif analysis
        List<String> nonAuthChans = new ArrayList<String>();
        List<String> nonSecChans = new ArrayList<String>();

        proverifAnalysis(tmap, nonAuthChans, nonSecChans);

        TGConnector fromStart;
        Map<String, HSMChannel> secChannels = new HashMap<String, HSMChannel>();
        //Add a HSM for each selected CPU on the component diagram
        for (String cpuName: selectedCpuTasks.keySet()){
            Map<String, HSMChannel> hsmChannels = new HashMap<String, HSMChannel>();
            TMLCPrimitiveComponent hsm = new TMLCPrimitiveComponent(0, 500, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxY(), false, null, tcdp);
            TAttribute isEnc = new TAttribute(2, "isEnc", "true", 4);
            hsm.getAttributes().add(isEnc);
            tcdp.addComponent(hsm, 0,500,false,true);
            hsm.setValueWithChange("HSM_"+cpuName);
            //Find all associated components
            List<TMLCPrimitiveComponent> comps = new ArrayList<TMLCPrimitiveComponent>();
            //Find the component to add a HSM to

            for (TGComponent tg: tcdp.getComponentList()){
                if (tg instanceof TMLCPrimitiveComponent){
                    for (String compName: selectedCpuTasks.get(cpuName)){
                        if (tg.getValue().equals(compName)){
                            comps.add((TMLCPrimitiveComponent) tg);
                            break;
                        }
                    }
                }
                else if (tg instanceof TMLCCompositeComponent){
                    TMLCCompositeComponent cc = (TMLCCompositeComponent) tg;
                    List<TMLCPrimitiveComponent> pcomps =cc.getAllPrimitiveComponents();
                    for (TMLCPrimitiveComponent pc: pcomps){
                        for (String compName: selectedCpuTasks.get(cpuName)){
                            if (pc.getValue().equals(compName)){
                                comps.add(pc);
                                break;
                            }
                        }
                    }
                }
            }
            if (comps.size()==0){
                //System.out.println("No Components found");
                continue;
            }
            //  System.out.println("nonAuthChans " + nonAuthChans);
            //System.out.println("nonSecChans "+ nonSecChans);
            for (TMLCPrimitiveComponent comp: comps){

                Map<String, HSMChannel> compChannels = new HashMap<String, HSMChannel>();
                String compName=comp.getValue();
                TMLActivityDiagramPanel tad = t.getTMLActivityDiagramPanel(compName);
                Set<TGComponent> channelInstances = new HashSet<TGComponent>();
                Set<TGComponent> secOperators = new HashSet<TGComponent>();
                isEnc = new TAttribute(2, "isEnc", "true", 4);
                comp.getAttributes().add(isEnc);
                //Find all unsecured channels
                //For previously secured channels, relocate encryption to the hsm

                for (TGComponent tg: tad.getComponentList()){
                    if (tg instanceof TMLADWriteChannel){
                        TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
                        if (writeChannel.getSecurityContext().equals("")){
                            String nonceName="";
                            int type=-1;
                            if (nonSecChans.contains(compName+"__"+writeChannel.getChannelName()+"_chData")){
                                type=HSMChannel.SENC;
                                if (nonAuthChans.contains(compName+"__"+writeChannel.getChannelName())){
                                    nonceName="nonce_"+writeChannel.getChannelName();
                                }
                            }
                            else if (nonAuthChans.contains(compName+"__"+writeChannel.getChannelName())){
                                type=HSMChannel.MAC;
                            }
                            HSMChannel ch=new HSMChannel(writeChannel.getChannelName(), compName, type);
                            ch.securityContext="hsmSec_"+writeChannel.getChannelName();
                            ch.nonceName=nonceName;
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart!=null){
                                if (type!=-1){
                                    compChannels.put(writeChannel.getChannelName(),ch);
                                    channelInstances.add(tg);
                                }
                            }
                        }
                        else {
                            //System.out.println("security context:"+writeChannel.securityContext);
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart!=null){
                                channelInstances.add(tg);
                                SecurityPattern sp = tmap.getSecurityPatternByName(writeChannel.getSecurityContext());
                                int type=-1;
                                if (sp.type.equals("Symmetric Encryption")){
                                    type=HSMChannel.SENC;
                                }
                                else if (sp.type.equals("Asymmetric Encryption")){
                                    type=HSMChannel.AENC;
                                }
                                else if (sp.type.equals("MAC")){
                                    type=HSMChannel.MAC;
                                }
                                else if (sp.type.equals("Nonce")){
                                    type=HSMChannel.NONCE;
                                }
                                HSMChannel ch = new HSMChannel(writeChannel.getChannelName(), compName, type);
                                ch.securityContext=writeChannel.getSecurityContext();
                                compChannels.put(writeChannel.getChannelName(),ch);
                                //chanNames.add(writeChannel.getChannelName()+compName);
                            }
                        }
                    }
                    if (tg instanceof TMLADReadChannel){
                        TMLADReadChannel readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getSecurityContext().equals("")){
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart!=null){
                                if(nonSecChans.contains(compName+"__"+readChannel.getChannelName()+"_chData") || nonAuthChans.contains(compName+"__"+readChannel.getChannelName())){
                                    channelInstances.add(tg);
                                    HSMChannel ch = new HSMChannel(readChannel.getChannelName(), compName, HSMChannel.DEC);
                                    ch.securityContext="hsmSec_"+readChannel.getChannelName();
                                    compChannels.put(readChannel.getChannelName(),ch);
                                    if (nonSecChans.contains(compName+"__"+readChannel.getChannelName()+"_chData") && nonAuthChans.contains(compName+"__"+readChannel.getChannelName())){
                                        ch.nonceName="nonce_"+readChannel.getChannelName();
                                    }
                                }
                            }
                        }
                        else {
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart!=null){
                                channelInstances.add(tg);
                                HSMChannel ch = new HSMChannel(readChannel.getChannelName(), compName, HSMChannel.DEC);
                                ch.securityContext=readChannel.getSecurityContext();
                                compChannels.put(readChannel.getChannelName(),ch);
                            }
                        }
                    }
                    if (tg instanceof TMLADEncrypt){
                        //      TMLADEncrypt enc = (TMLADEncrypt) tg;
                        secOperators.add(tg);
                        //}
                    }
                    if (tg instanceof TMLADDecrypt){
                        //      TMLADDecrypt dec = (TMLADDecrypt) tg;
                        secOperators.add(tg);
                        //}
                    }
                }
                //System.out.println("compchannels " +compChannels);
                List<ChannelData> hsmChans = new ArrayList<ChannelData>();
                ChannelData chd = new ChannelData("startHSM_"+cpuName,false,false);
                hsmChans.add(chd);
                for (String s: compChannels.keySet()){
                    hsmChannels.put(s,compChannels.get(s));
                    chd = new ChannelData("data_"+s+"_"+compChannels.get(s).task,false,true);
                    hsmChans.add(chd);
                    chd = new ChannelData("retData_"+s+"_"+compChannels.get(s).task,true,true);
                    hsmChans.add(chd);
                }
                for (ChannelData hsmChan: hsmChans){
                    TMLCChannelOutPort originPort =new TMLCChannelOutPort(comp.getX(), comp.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, hsm, tcdp);
                    TMLCChannelOutPort destPort = new TMLCChannelOutPort(comp.getX(), comp.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, comp, tcdp);
                    originPort.commName=hsmChan.name;
                    originPort.isOrigin=hsmChan.isOrigin;
                    tcdp.addComponent(originPort,hsm.getX(), hsm.getY(),true,true);
                    destPort.commName=hsmChan.name;
                    if (!hsmChan.isChan){
                        originPort.typep=2;
                        destPort.typep=2;
                        originPort.setParam(0, new TType(2));
                    }
                    destPort.isOrigin=!hsmChan.isOrigin;

                    tcdp.addComponent(destPort,comp.getX(), comp.getY(),true,true);

                    TMLCPortConnector conn = new TMLCPortConnector(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp, originPort.getTGConnectingPointAtIndex(0), destPort.getTGConnectingPointAtIndex(0), new Vector<Point>());
                    tcdp.addComponent(conn, 0,0,false,true);
                }
                int xpos=0;
                int ypos=0;

                //Remove existing security elements
                for (TGComponent op: secOperators){
                    TGConnector prev = tad.findTGConnectorEndingAt(op.getTGConnectingPointAtIndex(0));
                    //TGConnectingPoint point = prev.getTGConnectingPointP1();
                    TGConnector end = tad.findTGConnectorStartingAt(op.getTGConnectingPointAtIndex(1));
                    TGConnectingPoint point2 = end.getTGConnectingPointP2();
                    tad.removeComponent(op);
                    tad.removeComponent(end);
                    tad.addComponent(prev,0,0,false,true);
                    prev.setP2(point2);
                }

                //Modify component activity diagram to add read/write to HSM

                //Add actions before Write Channel
                for (TGComponent chan: channelInstances){
                    String chanName="";
                    if (!(chan instanceof TMLADWriteChannel)){
                        continue;
                    }
                    TMLADWriteChannel writeChannel = (TMLADWriteChannel) chan;
                    chanName=writeChannel.getChannelName();
                    HSMChannel ch = hsmChannels.get(chanName);
                    writeChannel.setSecurityContext(ch.securityContext);
                    xpos = chan.getX();
                    ypos = chan.getY();
                    fromStart = tad.findTGConnectorEndingAt(chan.getTGConnectingPointAtIndex(0));
                    TGConnectingPoint point = fromStart.getTGConnectingPointP2();

                    //set isEnc to true
                    int yShift=50;

                    TMLADSendRequest req = new TMLADSendRequest(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    req.setRequestName("startHSM_"+cpuName);
                    req.setParam(0,"isEnc");
                    tad.addComponent(req, xpos, ypos+yShift, false,true);

                    fromStart.setP2(req.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, xpos, ypos, false, true);

                    //Add connection
                    fromStart=new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(req.getTGConnectingPointAtIndex(1));
                    TMLADWriteChannel wr =new TMLADWriteChannel(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

                    yShift+=50;
                    //Add write channel operator
                    wr = new TMLADWriteChannel(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    wr.setChannelName("data_"+chanName+"_"+compName);
                    wr.setSecurityContext(ch.securityContext);
                    tad.addComponent(wr, xpos, ypos+yShift, false,true);


                    fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, xpos, ypos, false, true);

                    fromStart=new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(wr.getTGConnectingPointAtIndex(1));







                    //Add read channel operator

                    yShift+=60;
                    TMLADReadChannel rd = new TMLADReadChannel(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd.setChannelName("retData_"+chanName+"_"+compName);
                    rd.setSecurityContext(ch.securityContext);
                    tad.addComponent(rd, xpos, ypos+yShift, false,true);

                    fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
                    yShift+=50;

                    //Add connector
                    fromStart=new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                    yShift+=50;

                    //Direct the last TGConnector back to the start of the write channel operator


                    fromStart.setP2(point);
                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg:tad.getComponentList()){
                        if (tg.getY() >= ypos && tg !=wr && tg!=req && tg!=rd){
                            tg.setCd(tg.getX(), tg.getY()+yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY()+yShift);
                    tad.repaint();

                }
                //Add actions after Read Channel
                for (TGComponent chan: channelInstances){
                    String chanName="";
                    if (!(chan instanceof TMLADReadChannel)) {
                        continue;
                    }
                    TMLADReadChannel readChannel = (TMLADReadChannel) chan;
                    chanName=readChannel.getChannelName();
                    HSMChannel ch= hsmChannels.get(chanName);
                    readChannel.setSecurityContext(ch.securityContext);
                    xpos = chan.getX()+10;
                    ypos = chan.getY();
                    fromStart = tad.findTGConnectorStartingAt(chan.getTGConnectingPointAtIndex(1));
                    if (fromStart==null){
                        fromStart=new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(chan.getTGConnectingPointAtIndex(1));
                        tad.addComponent(fromStart, xpos,ypos,false,true);
                    }
                    TGConnectingPoint point = fromStart.getTGConnectingPointP2();

                    //Set isEnc to false
                    int yShift=50;
                    TMLADActionState act = new TMLADActionState(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    act.setValue("isEnc=false");
                    tad.addComponent(act, xpos, ypos+yShift, false,true);
                    fromStart.setP2(act.getTGConnectingPointAtIndex(0));


                    //Add send request operator

                    yShift+=50;
                    TMLADSendRequest req = new TMLADSendRequest(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    req.setRequestName("startHSM_"+cpuName);
                    req.setParam(0,"isEnc");
                    req.makeValue();
                    tad.addComponent(req, xpos, ypos+yShift, false,true);


                    //Add connection
                    fromStart=new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(act.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(req.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, xpos, ypos, false, true);




                    yShift+=50;
                    //Add write channel operator
                    TMLADWriteChannel wr = new TMLADWriteChannel(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    wr.setChannelName("data_"+chanName+"_"+compName);
                    wr.setSecurityContext(ch.securityContext);
                    tad.addComponent(wr, xpos, ypos+yShift, false,true);


                    //Add connection
                    fromStart=new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(req.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, xpos, ypos, false, true);

                    //Add read channel operator

                    yShift+=60;
                    TMLADReadChannel rd = new TMLADReadChannel(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd.setChannelName("retData_"+chanName+"_"+compName);
                    rd.setSecurityContext(ch.securityContext);
                    tad.addComponent(rd, xpos, ypos+yShift, false,true);

                    fromStart=new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
                    yShift+=50;

                    if (point!=null){
                        fromStart=new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        //Direct the last TGConnector back to the start of the write channel operator

                        fromStart.setP2(point);
                    }
                    yShift+=50;

                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg:tad.getComponentList()){
                        if (tg.getY() >= ypos && tg !=wr && tg!=req && tg!=rd && tg!=chan && tg!=act){
                            tg.setCd(tg.getX(), tg.getY()+yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY()+yShift);
                    tad.repaint();
                }
                //for (String chan: chanNames){
                //                        hsmChannels.put(chan, compName);
                //}
            }

            int xpos=0;
            int ypos=0;



            //Build HSM Activity diagram

            TMLActivityDiagramPanel tad = t.getTMLActivityDiagramPanel("HSM_"+cpuName);

            TMLADStartState start = (TMLADStartState) tad.getComponentList().get(0);
            fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());


            TMLADReadRequestArg req = new TMLADReadRequestArg(300, 100, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
            tad.addComponent(req, 300,100,false,true);
            req.setParam(0, "isEnc");
            req.makeValue();

            //Connect start and readrequest
            fromStart.setP1(start.getTGConnectingPointAtIndex(0));
            fromStart.setP2(req.getTGConnectingPointAtIndex(0));
            tad.addComponent(fromStart, 300,200,false,true);



            TMLADChoice choice = new TMLADChoice(300, 200, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
            tad.addComponent(choice, 300,200,false,true);


            //Connect readrequest and choice
            fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
            fromStart.setP1(req.getTGConnectingPointAtIndex(1));
            fromStart.setP2(choice.getTGConnectingPointAtIndex(0));
            tad.addComponent(fromStart, 300,200,false,true);


            int xc = 150;
            //Allows 9 channels max to simplify the diagram

            //If more than 3 channels, build 2 levels of choices
            TMLADChoice choice2= new TMLADChoice(xc, 400, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
            if (hsmChannels.keySet().size()>3){
                int i=0;
                for (String chan: hsmChannels.keySet()){
                    HSMChannel ch = hsmChannels.get(chan);
                    if (i%3==0){
                        //Add a new choice every third channel
                        choice2= new TMLADChoice(xc, 250, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        tad.addComponent(choice2, xc, 400,false,true);
                        //Connect new choice operator to top choice
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(choice.getTGConnectingPointAtIndex(i/3+1));
                        fromStart.setP2(choice2.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300,200,false,true);
                    }
                    TMLADReadChannel rd = new TMLADReadChannel(xc, 300, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd.setChannelName("data_"+chan+"_"+hsmChannels.get(chan).task);
                    rd.setSecurityContext(ch.securityContext);
                    tad.addComponent(rd, xc,300,false,true);
                    //Connect choice and readchannel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(choice2.getTGConnectingPointAtIndex(i%3+1));
                    fromStart.setP2(rd.getTGConnectingPointAtIndex(0));

                    tad.addComponent(fromStart, 300,200,false,true);
                    TMLADWriteChannel wr = new TMLADWriteChannel(xc, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    wr.setChannelName("retData_"+chan+"_"+hsmChannels.get(chan).task);
                    tad.addComponent(wr, xc, 600,false,true);
                    wr.setSecurityContext(ch.securityContext);


                    if (hsmChannels.get(chan).secType==HSMChannel.DEC){
                        TMLADDecrypt dec = new TMLADDecrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        dec.securityContext =  ch.securityContext;
                        tad.addComponent(dec, xc, 500,false,true);
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300,200,false,true);

                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300,200,false,true);

                        //Connect encrypt and writechannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(dec.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300,200,false,true);
                    }
                    else {
                        TMLADEncrypt enc = new TMLADEncrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        enc.securityContext = ch.securityContext;
                        if (hsmChannels.get(chan).secType==HSMChannel.SENC){
                            enc.type = "Symmetric Encryption";
                        }
                        else if (hsmChannels.get(chan).secType==HSMChannel.AENC){
                            enc.type="Asymmetric Encryption";
                        }
                        else if (hsmChannels.get(chan).secType==HSMChannel.MAC){
                            enc.type="MAC";
                        }
                        else if (hsmChannels.get(chan).secType==HSMChannel.NONCE){
                            enc.type="Nonce";
                        }

                        enc.message_overhead = overhead;
                        enc.encTime= encComp;
                        enc.decTime=decComp;
                        enc.nonce=hsmChannels.get(chan).nonceName;
                        tad.addComponent(enc, xc, 500,false,true);

                        //Connect encrypt and readchannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300,200,false,true);

                        //Connect encrypt and writechannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(enc.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300,200,false,true);

                    }
                    xc+=300;
                    i++;
                }
            }
            else {

                int i=1;

                for (String chan: hsmChannels.keySet()){
                    HSMChannel ch = hsmChannels.get(chan);
                    TMLADReadChannel rd = new TMLADReadChannel(xc, 300, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd.setChannelName("data_"+chan+"_"+hsmChannels.get(chan).task);
                    rd.setSecurityContext(ch.securityContext);
                    tad.addComponent(rd, xc,300,false,true);
                    //Connect choice and readchannel

                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(choice.getTGConnectingPointAtIndex(i));
                    fromStart.setP2(rd.getTGConnectingPointAtIndex(0));

                    tad.addComponent(fromStart, 300,200,false,true);

                    TMLADWriteChannel wr = new TMLADWriteChannel(xc, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    wr.setChannelName("retData_"+chan+"_"+hsmChannels.get(chan).task);
                    tad.addComponent(wr, xc, 600,false,true);
                    wr.setSecurityContext(ch.securityContext);


                    if (hsmChannels.get(chan).secType==HSMChannel.DEC){
                        TMLADDecrypt dec = new TMLADDecrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        dec.securityContext = ch.securityContext;
                        tad.addComponent(dec, xc, 500,false,true);

                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300,200,false,true);

                        //Connect encrypt and writechannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(dec.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300,200,false,true);
                    }
                    else {
                        TMLADEncrypt enc = new TMLADEncrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        enc.securityContext = ch.securityContext;
                        if (hsmChannels.get(chan).secType==HSMChannel.SENC){
                            enc.type = "Symmetric Encryption";
                        }
                        else if (hsmChannels.get(chan).secType==HSMChannel.AENC){
                            enc.type="Asymmetric Encryption";
                        }
                        else if (hsmChannels.get(chan).secType==HSMChannel.MAC){
                            enc.type="MAC";
                        }
                        else if (hsmChannels.get(chan).secType==HSMChannel.NONCE){
                            enc.type="Nonce";
                        }

                        enc.message_overhead = overhead;
                        enc.encTime= encComp;
                        enc.decTime=decComp;
                        tad.addComponent(enc, xc, 500,false,true);

                        //Connect encrypt and readchannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300,200,false,true);

                        //Connect encrypt and writechannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(enc.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300,200,false,true);


                    }


                    /*MLADChoice choice2 = new TMLADChoice(xc, 400, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                      tad.addComponent(choice2, xc, 400,false,true);
                      choice2.setGuard("[isEnc]", 0);
                      choice2.setGuard("[else]",1);

                      //connect readchannel and choice 2
                      fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector());
                      fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                      fromStart.setP2(choice2.getTGConnectingPointAtIndex(0));
                      tad.addComponent(fromStart, 300,200,false,true);

                      TMLADEncrypt enc = new TMLADEncrypt(xc-75, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                      enc.securityContext = "hsmSec_"+chan;
                      enc.type = "Symmetric Encryption";
                      enc.message_overhead = overhead;
                      enc.encTime= encComp;
                      enc.decTime=decComp;
                      tad.addComponent(enc, xc-75, 500,false,true);

                      //Connect choice 2 and encrypt
                      fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector());
                      fromStart.setP1(choice2.getTGConnectingPointAtIndex(1));
                      fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                      tad.addComponent(fromStart, 300,200,false,true);

                      TMLADWriteChannel wr = new TMLADWriteChannel(xc-75, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                      wr.setChannelName("retData_"+chan+hsmChannels.get(chan));
                      tad.addComponent(wr, xc-75, 600,false,true);
                      wr.securityContext = "hsmSec_"+chan;

                      //Connect encrypt and writeChannel
                      fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector());
                      fromStart.setP1(enc.getTGConnectingPointAtIndex(1));
                      fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                      tad.addComponent(fromStart, 300,200,false,true);


                      TMLADDecrypt dec = new TMLADDecrypt(xc+75, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                      dec.securityContext = "hsmSec_"+chan;
                      tad.addComponent(dec, xc+75, 500,false,true);

                      //Connect choice2 and decrypt

                      fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector());
                      fromStart.setP1(choice2.getTGConnectingPointAtIndex(2));
                      fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                      tad.addComponent(fromStart, 300,200,false,true);

                      TMLADWriteChannel wr2 = new TMLADWriteChannel(xc+75, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                      wr2.setChannelName("retData_"+chan+hsmChannels.get(chan));
                      wr2.securityContext = "hsmSec_"+chan;

                      //Connect decrypt and writeChannel
                      fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector());
                      fromStart.setP1(dec.getTGConnectingPointAtIndex(1));
                      fromStart.setP2(wr2.getTGConnectingPointAtIndex(0));
                      tad.addComponent(fromStart, 300,200,false,true);
                      tad.addComponent(wr2, xc+75, 600,false,true);

                    */
                    xc+=300;
                    i++;
                }

            }

            secChannels.putAll(hsmChannels);
        }
        //For all the tasks that receive encrypted data, decrypt it, assuming it has no associated HSM
        for (TMLTask task: tmap.getTMLModeling().getTasks()){
            int xpos, ypos;
            //System.out.println("loop 2");
            TMLActivityDiagramPanel tad = t.getTMLActivityDiagramPanel(task.getName());
            HashSet<TGComponent> channelInstances = new HashSet<TGComponent>();
            for (String chan: secChannels.keySet()){
                HSMChannel ch = secChannels.get(chan);
                channelInstances.clear();
                for (TGComponent tg: tad.getComponentList()){
                    if (tg instanceof TMLADReadChannel){
                        TMLADReadChannel readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getChannelName().equals(chan) && readChannel.getSecurityContext().equals("")){
                            fromStart = tad.findTGConnectorStartingAt(tg.getTGConnectingPointAtIndex(1));
                            if (fromStart!=null){
                                channelInstances.add(tg);
                            }
                        }
                    }
                }
                for (TGComponent chI: channelInstances){
                    TMLADReadChannel readChannel = (TMLADReadChannel) chI;
                    readChannel.setSecurityContext(ch.securityContext);
                    xpos = chI.getX();
                    ypos = chI.getY()+10;
                    fromStart = tad.findTGConnectorStartingAt(chI.getTGConnectingPointAtIndex(1));
                    if (fromStart==null){
                        fromStart=new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(chI.getTGConnectingPointAtIndex(1));
                        tad.addComponent(fromStart, xpos,ypos,false,true);
                    }
                    TGConnectingPoint point = fromStart.getTGConnectingPointP2();
                    //Add decryption operator
                    int yShift=100;
                    TMLADDecrypt dec = new TMLADDecrypt(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    dec.securityContext = ch.securityContext;
                    tad.addComponent(dec, xpos,ypos+yShift, false, true);


                    fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                    if (point!=null){
                        fromStart=new TGConnectorTMLAD(dec.getX(), dec.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        fromStart.setP1(dec.getTGConnectingPointAtIndex(1));

                        //Direct the last TGConnector back to the next action

                        fromStart.setP2(point);
                    }
                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg:tad.getComponentList()){
                        if (tg.getY() >= ypos && tg!=dec){
                            tg.setCd(tg.getX(), tg.getY()+yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY()+yShift);
                    tad.repaint();

                }
            }
            //Next find channels that send encrypted data, and add the encryption operator
            for (String chan: secChannels.keySet()){
                channelInstances.clear();
                HSMChannel ch = secChannels.get(chan);
                for (TGComponent tg: tad.getComponentList()){
                    if (tg instanceof TMLADWriteChannel){
                        TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
                        if (writeChannel.getChannelName().equals(chan) && writeChannel.getSecurityContext().equals("")){
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart!=null){
                                channelInstances.add(tg);
                            }
                        }
                    }
                }
                for (TGComponent chI: channelInstances){
                    TMLADWriteChannel writeChannel = (TMLADWriteChannel) chI;
                    writeChannel.setSecurityContext(ch.securityContext);
                    xpos = chI.getX();
                    ypos = chI.getY()-10;
                    fromStart = tad.findTGConnectorEndingAt(chI.getTGConnectingPointAtIndex(0));
                    TGConnectingPoint point = fromStart.getTGConnectingPointP2();
                    //Add encryption operator
                    int yShift=100;

                    TMLADEncrypt enc = new TMLADEncrypt(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    tad.addComponent(enc,xpos, ypos, false,true);
                    enc.securityContext = ch.securityContext;
                    enc.type = "Symmetric Encryption";
                    enc.message_overhead = overhead;
                    enc.encTime= encComp;
                    enc.decTime=decComp;
                    enc.size=overhead;


                    fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                    fromStart=new TGConnectorTMLAD(enc.getX(), enc.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(enc.getTGConnectingPointAtIndex(1));

                    //Direct the last TGConnector back to the start of the write channel operator

                    fromStart.setP2(point);
                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg:tad.getComponentList()){
                        if (tg.getY() >= ypos && tg !=enc){
                            tg.setCd(tg.getX(), tg.getY()+yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY()+yShift);
                    tad.repaint();

                }
            }

        }
        for (String cpuName: selectedCpuTasks.keySet()){
            //Add a private bus to Hardware Accelerator with the task for hsm

            //Find the CPU the task is mapped to
            TMLArchiDiagramPanel archPanel = newarch.tmlap;
            TMLArchiCPUNode cpu=null;
            String refTask="";
            for (TGComponent tg: archPanel.getComponentList()){
                if (tg instanceof TMLArchiCPUNode){
                    if (tg.getName().equals(cpuName)){
                        cpu=(TMLArchiCPUNode) tg;
                        TMLArchiArtifact art = cpu.getArtifactList().get(0);
                        refTask=art.getReferenceTaskName();
                        break;

                    }
                }
            }

            if (cpu==null){
                return;
            }

            //Add new memory
            TMLArchiMemoryNode mem = new TMLArchiMemoryNode(cpu.getX()+100, cpu.getY()+100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel);
            archPanel.addComponent(mem, cpu.getX()+100, cpu.getY()+100, false, true);
            mem.setName("HSMMemory_"+cpuName);
            //Add Hardware Accelerator

            TMLArchiHWANode hwa = new TMLArchiHWANode(cpu.getX()+100, cpu.getY()+100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel);
            archPanel.addComponent(hwa, cpu.getX()+100, cpu.getY()+100, false, true);
            hwa.setName("HSM_"+cpuName);
            //Add hsm task to hwa


            TMLArchiArtifact hsmArt = new TMLArchiArtifact(cpu.getX()+100, cpu.getY()+100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, hwa, archPanel);
            archPanel.addComponent(hsmArt, cpu.getX()+100, cpu.getY()+100, true, true);
            hsmArt.setFullName("HSM_"+cpuName, refTask);
            //Add bus connecting the cpu and HWA

            TMLArchiBUSNode bus = new TMLArchiBUSNode(cpu.getX()+100, cpu.getY()+100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel);
            bus.setPrivacy(1);
            bus.setName("HSMBus_"+cpuName);
            archPanel.addComponent(bus, cpu.getX()+200, cpu.getY()+200, false, true);

            //Connect Bus and CPU
            TMLArchiConnectorNode connect =new  TMLArchiConnectorNode(cpu.getX()+100, cpu.getY()+100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel, null, null, new Vector<Point>());
            TGConnectingPoint p1 = bus.findFirstFreeTGConnectingPoint(true,true);
            p1.setFree(false);
            connect.setP2(p1);



            TGConnectingPoint p2 = cpu.findFirstFreeTGConnectingPoint(true,true);
            p1.setFree(false);
            connect.setP1(p2);
            archPanel.addComponent(connect, cpu.getX()+100, cpu.getY()+100, false, true);
            //Connect Bus and HWA

            connect = new TMLArchiConnectorNode(cpu.getX()+100, cpu.getY()+100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel, null, null, new Vector<Point>());
            p1 = bus.findFirstFreeTGConnectingPoint(true,true);
            p1.setFree(false);
            connect.setP2(p1);

            p2 = hwa.findFirstFreeTGConnectingPoint(true,true);
            p1.setFree(false);
            connect.setP1(p2);

            archPanel.addComponent(connect, cpu.getX()+100, cpu.getY()+100, false, true);
            //Connect Bus and Memory

            connect = new TMLArchiConnectorNode(cpu.getX()+100, cpu.getY()+100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel, null, null, new Vector<Point>());
            p1 = bus.findFirstFreeTGConnectingPoint(true,true);
            p1.setFree(false);
            connect.setP2(p1);

            p2 = mem.findFirstFreeTGConnectingPoint(true,true);
            p1.setFree(false);
            connect.setP1(p2);
            archPanel.addComponent(connect, cpu.getX()+100, cpu.getY()+100, false, true);
        }

    }



    public TMLMapping<TGComponent> autoSecure(MainGUI gui, boolean autoConf, boolean autoWeakAuth, boolean autoStrongAuth){
        //TODO add more options
        //
        if (tmap==null){
            return null;
        }
		TURTLEPanel tmlap = tmap.getCorrespondanceList().getTG(tmap.getArch().getFirstCPU()).getTDiagramPanel().tp;
        int arch = gui.tabs.indexOf(tmlap);
        gui.cloneRenameTab(arch,"enc");
        TMLArchiPanel newarch = (TMLArchiPanel) gui.tabs.get(gui.tabs.size()-1);
        
        return autoSecure(gui, "enc", tmap,newarch,autoConf,autoWeakAuth, autoStrongAuth);
    }
    
    public TMLMapping<TGComponent> autoSecure(MainGUI gui, String name, TMLMapping<TGComponent> map, TMLArchiPanel newarch){
        return autoSecure(gui,name,map,newarch,"100","0","100",true,false,false);
    }
    
    public TMLMapping<TGComponent> autoSecure(MainGUI gui, String name, TMLMapping<TGComponent> map, TMLArchiPanel newarch, boolean autoConf, boolean autoWeakAuth, boolean autoStrongAuth){
        return autoSecure(gui,name,map,newarch,"100","0","100",autoConf,autoWeakAuth, autoStrongAuth);
    }

    public TMLMapping<TGComponent> autoSecure(MainGUI gui, String encComp, String overhead, String decComp){
        if (tmap==null){
            return null;
        }
		TURTLEPanel tmlap = tmap.getCorrespondanceList().getTG(tmap.getArch().getFirstCPU()).getTDiagramPanel().tp;
        int arch = gui.tabs.indexOf(tmlap);
        gui.cloneRenameTab(arch,"enc");
        TMLArchiPanel newarch = (TMLArchiPanel) gui.tabs.get(gui.tabs.size()-1);
        return autoSecure(gui,"enc", tmap,newarch,encComp, overhead,decComp,true,false,false);
    }

    public TMLMapping<TGComponent> autoSecure(MainGUI gui, String encComp, String overhead, String decComp, boolean autoConf, boolean autoWeakAuth, boolean autoStrongAuth){
        if (tmap==null){
            return null;
        }
		TURTLEPanel tmlap = tmap.getCorrespondanceList().getTG(tmap.getArch().getFirstCPU()).getTDiagramPanel().tp;
        int arch = gui.tabs.indexOf(tmlap);
        gui.cloneRenameTab(arch,"enc");
        TMLArchiPanel newarch = (TMLArchiPanel) gui.tabs.get(gui.tabs.size()-1);
        return autoSecure(gui,"enc", tmap,newarch,encComp, overhead,decComp,autoConf,autoWeakAuth, autoStrongAuth);
    }
    
    public TMLMapping<TGComponent> autoSecure(MainGUI gui, String name, TMLMapping<TGComponent> map, TMLArchiPanel newarch, String encComp, String overhead, String decComp){
        return autoSecure(gui,name, tmap,newarch,encComp, overhead,decComp,true,false, false);
    }

    public void proverifAnalysis(TMLMapping<TGComponent> map,  List<String> nonAuthChans, List<String> nonSecChans){
        if (map==null){
            TraceManager.addDev("No mapping");
            return;
        }
        //Perform ProVerif Analysis
        TML2Avatar t2a = new TML2Avatar(map,false,true);
        AvatarSpecification avatarspec = t2a.generateAvatarSpec("1");
        if (avatarspec == null){
            TraceManager.addDev("No avatar spec");
            return;
        }

        avatar2proverif = new AVATAR2ProVerif(avatarspec);
        try {
            proverif = avatar2proverif.generateProVerif(true, true, 1, true, false);
            warnings = avatar2proverif.getWarnings();

            if (!avatar2proverif.saveInFile("pvspec")){
                return;
            }

            RshClient rshc = new RshClient(ConfigurationTTool.ProVerifVerifierHost);

            rshc.setCmd(ConfigurationTTool.ProVerifVerifierPath + " -in pitype pvspec");
            rshc.sendExecuteCommandRequest();
            Reader data  = rshc.getDataReaderFromProcess();

            ProVerifOutputAnalyzer pvoa = getProVerifOutputAnalyzer ();
            pvoa.analyzeOutput(data, true);
            HashMap<AvatarPragmaSecret, ProVerifQueryResult> confResults = pvoa.getConfidentialityResults();
            for (AvatarPragmaSecret pragma: confResults.keySet()) {
                if (confResults.get(pragma).isProved() && !confResults.get(pragma).isSatisfied())
                    {
                        nonSecChans.add(pragma.getArg().getBlock().getName() + "__" + pragma.getArg().getName());
                        TraceManager.addDev(pragma.getArg().getBlock().getName() + "." + pragma.getArg().getName()+ " is not secret");
                        TMLChannel chan = map.getTMLModeling().getChannelByShortName(pragma.getArg().getName().replaceAll("_chData",""));
                        for (String block:chan.getTaskNames()){
                            nonSecChans.add(block+"__"+pragma.getArg().getName());
                        }
                    }
            }
            HashMap<AvatarPragmaAuthenticity, ProVerifQueryAuthResult> authResults = pvoa.getAuthenticityResults();
            for (AvatarPragmaAuthenticity pragma: authResults.keySet()) {
                if (authResults.get(pragma).isProved() && !authResults.get(pragma).isSatisfied()) {
                    nonAuthChans.add(pragma.getAttrA().getAttribute().getBlock().getName() + "__" + pragma.getAttrA().getState().getName().replaceAll("_chData", ""));
                    nonAuthChans.add(pragma.getAttrB().getAttribute().getBlock().getName() + "__" + pragma.getAttrB().getState().getName().replaceAll("_chData", ""));
                }
            }
            TraceManager.addDev("all results displayed");

        }
        catch (Exception e){
            System.out.println("ProVerif Analysis Failed " + e);
        }
    }

    public TMLMapping<TGComponent> autoSecure(MainGUI gui, String name, TMLMapping<TGComponent> map, TMLArchiPanel newarch, String encComp, String overhead, String decComp, boolean autoConf, boolean autoWeakAuth, boolean autoStrongAuth){
        Map<TMLTask, List<TMLTask>> toSecure = new HashMap<TMLTask, List<TMLTask>>();
        Map<TMLTask, List<TMLTask>> toSecureRev = new HashMap<TMLTask, List<TMLTask>>();
        Map<TMLTask, List<String>> secOutChannels = new HashMap<TMLTask, List<String>>();
        Map<TMLTask, List<String>> secInChannels = new HashMap<TMLTask, List<String>>();
        Map<TMLTask, List<String>> nonceOutChannels = new HashMap<TMLTask, List<String>>();
        Map<TMLTask, List<String>> nonceInChannels = new HashMap<TMLTask, List<String>>();
        Map<TMLTask, List<String>> macOutChannels = new HashMap<TMLTask, List<String>>();
        Map<TMLTask, List<String>> macInChannels = new HashMap<TMLTask, List<String>>();
        Map<TMLTask, List<String>> macNonceOutChannels = new HashMap<TMLTask, List<String>>();
        Map<TMLTask, List<String>> macNonceInChannels = new HashMap<TMLTask, List<String>>();
        TraceManager.addDev("mapping " + map.getSummaryTaskMapping());
        List<String> nonAuthChans = new ArrayList<String>();
        List<String> nonSecChans = new ArrayList<String>();

        proverifAnalysis(map, nonAuthChans, nonSecChans);

        TMLModeling<TGComponent> tmlmodel = map.getTMLModeling();
        List<TMLChannel> channels = tmlmodel.getChannels();
        for (TMLChannel channel: channels){
            for (TMLCPrimitivePort p: channel.ports){
                channel.checkConf = channel.checkConf || p.checkConf;
                channel.checkAuth = channel.checkAuth || p.checkAuth;
            }
        }

        //Create clone of Component Diagram + Activity diagrams to secure
		TGComponent tgcomp= map.getTMLModeling().getTGComponent();
		TMLComponentDesignPanel tmlcdp= (TMLComponentDesignPanel) tgcomp.getTDiagramPanel().tp;
//        TMLComponentDesignPanel tmlcdp = map.getTMLCDesignPanel();
        int ind = gui.tabs.indexOf(tmlcdp);
        if (ind==-1){
            TraceManager.addDev("No Component Design Panel");
            return null;
        }
        String tabName = gui.getTitleAt(tmlcdp);
        gui.cloneRenameTab(ind, name);
        TMLComponentDesignPanel t = (TMLComponentDesignPanel) gui.tabs.get(gui.tabs.size()-1);

        TMLComponentTaskDiagramPanel tcdp = t.tmlctdp;
        //Create clone of architecture panel and map tasks to it
        newarch.renameMapping(tabName, tabName+"_"+name);

        for (TMLTask task: map.getTMLModeling().getTasks()){
            List<String> tmp = new ArrayList<String>();
            List<String> tmp2 = new ArrayList<String>();
            List<TMLTask> tmp3 = new ArrayList<TMLTask>();
            List<TMLTask> tmp4 = new ArrayList<TMLTask>();
            List<String> tmp5 = new ArrayList<String>();
            List<String> tmp6 = new ArrayList<String>();
            List<String> tmp7 = new ArrayList<String>();
            List<String> tmp8 = new ArrayList<String>();
            List<String> tmp9 = new ArrayList<String>();
            List<String> tmp10 = new ArrayList<String>();
            secInChannels.put(task, tmp);
            secOutChannels.put(task, tmp2);
            toSecure.put(task,tmp3);
            toSecureRev.put(task,tmp4);
            nonceInChannels.put(task,tmp5);
            nonceOutChannels.put(task,tmp6);
            macInChannels.put(task,tmp7);
            macOutChannels.put(task,tmp8);
            macNonceOutChannels.put(task,tmp9);
            macNonceInChannels.put(task,tmp10);
        }
        //With the proverif results, check which channels need to be secured
        for (TMLTask task: map.getTMLModeling().getTasks()){
            //Check if all channel operators are secured
            TMLActivityDiagramPanel tad = t.getTMLActivityDiagramPanel(task.getName());
            for (TGComponent tg:tad.getComponentList()){
                if (tg instanceof TMLADWriteChannel){
                    TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
                    if (writeChannel.getSecurityContext().equals("")){

                        TMLChannel chan = tmlmodel.getChannelByName(tabName+"__"+writeChannel.getChannelName());
                        //System.out.println("channel " + chan);
                        if (chan!=null){
                            if (chan.checkConf){
                                //        System.out.println(chan.getOriginTask().getName().split("__")[1]);
                                if (nonSecChans.contains(chan.getOriginTask().getName().split("__")[1]+"__"+writeChannel.getChannelName()+"_chData") && !secInChannels.get(chan.getDestinationTask()).contains(writeChannel.getChannelName())){
                                    //                                                                                            if (!securePath(map, chan.getOriginTask(), chan.getDestinationTask())){
                                    secOutChannels.get(chan.getOriginTask()).add(writeChannel.getChannelName());
                                    secInChannels.get(chan.getDestinationTask()).add(writeChannel.getChannelName());
                                    toSecure.get(chan.getOriginTask()).add(chan.getDestinationTask());
                                    if (chan.checkAuth && autoStrongAuth){
                                        toSecureRev.get(chan.getDestinationTask()).add(chan.getOriginTask());
                                        nonceOutChannels.get(chan.getOriginTask()).add(writeChannel.getChannelName());
                                        nonceInChannels.get(chan.getDestinationTask()).add(writeChannel.getChannelName());
                                    }
                                }
                            }
                            else if (chan.checkAuth && autoWeakAuth){
                                if (nonAuthChans.contains(chan.getDestinationTask().getName().split("__")[1]+"__"+writeChannel.getChannelName())){
                                    toSecure.get(chan.getOriginTask()).add(chan.getDestinationTask());
                                    macOutChannels.get(chan.getOriginTask()).add(writeChannel.getChannelName());
                                    macInChannels.get(chan.getDestinationTask()).add(writeChannel.getChannelName());
                                    if (autoStrongAuth){
                                        toSecureRev.get(chan.getDestinationTask()).add(chan.getOriginTask());
                                        macNonceInChannels.get(chan.getDestinationTask()).add(writeChannel.getChannelName());
                                        macNonceOutChannels.get(chan.getOriginTask()).add(writeChannel.getChannelName());
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        TraceManager.addDev("macoutchans "+ macOutChannels);
        TraceManager.addDev("macinchans " +macInChannels);
        TraceManager.addDev("nonsecin " +secInChannels);
        TraceManager.addDev("nonsecout " +secOutChannels);
        TraceManager.addDev("noncein " +nonceInChannels);
        TraceManager.addDev("nonceout " +nonceOutChannels);

        //        System.out.println(secOutChanannels.toString());
        //        int num=0;
        //int nonceNum=0;
        //Create reverse channels on component diagram to send nonces if they don't already exist

        for (TMLTask task: toSecureRev.keySet()){
            TraceManager.addDev("Adding nonces to " + task.getName());
            List<TMLChannel> chans = tmlmodel.getChannelsFromMe(task);

            for (TMLTask task2: toSecureRev.get(task)){
                boolean addChan = true;
                for (TMLChannel chan:chans){
                    if (chan.getDestinationTask()==task2){
                        addChan=false;
                    }
                }

                if (addChan){
                    TMLCChannelOutPort originPort = new TMLCChannelOutPort(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp);
                    TMLCChannelOutPort destPort = new TMLCChannelOutPort(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp);
                    for (TGComponent tg: tcdp.getComponentList()){
                        if (tg instanceof TMLCPrimitiveComponent){
                            if (tg.getValue().equals(task.getName().split("__")[1])){
                                originPort = new TMLCChannelOutPort(tg.getX(), tg.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, tg, tcdp);
                                originPort.commName="nonceCh"+task.getName().split("__")[1] + "_"+task2.getName().split("__")[1];
                                tcdp.addComponent(originPort,tg.getX(), tg.getY(),true,true);
                            }
                            else if (tg.getValue().equals(task2.getName().split("__")[1])){
                                destPort = new TMLCChannelOutPort(tg.getX(), tg.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, tg, tcdp);
                                destPort.isOrigin=false;
                                destPort.commName="nonceCh"+task.getName().split("__")[1] + "_"+ task2.getName().split("__")[1];
                                tcdp.addComponent(destPort,tg.getX(), tg.getY(),true,true);
                            }
                        }
                    }
                    tmlmodel.addChannel(new TMLChannel("nonceCh"+task.getName().split("__")[1] + "_"+ task2.getName().split("__")[1], originPort));
                    //Add connection
                    TMLCPortConnector conn = new TMLCPortConnector(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp, originPort.getTGConnectingPointAtIndex(0), destPort.getTGConnectingPointAtIndex(0), new Vector<Point>());
                    tcdp.addComponent(conn, 0,0,false,true);
                }
            }
        }
        //  }
        //Add encryption/nonces to activity diagram
        for (TMLTask task:toSecure.keySet()){
            String title = task.getName().split("__")[0];
            TraceManager.addDev("Securing task " + task.getName());
            TMLActivityDiagramPanel tad = t.getTMLActivityDiagramPanel(task.getName());
            //Get start state position, shift everything down
            int xpos=0;
            int ypos=0;
            TGConnector fromStart= new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
            TGConnectingPoint point = new TGConnectingPoint(null, 0, 0, false, false);
            //Find states immediately before the write channel operator

            //For each occurence of a write channel operator, add encryption/nonces before it

            for (String channel: secOutChannels.get(task)){
                Set<TGComponent> channelInstances = new HashSet<TGComponent>();
                int yShift=50;
                TMLChannel tmlc = tmlmodel.getChannelByName(title +"__"+channel);
                //First, find the connector that points to it. We will add the encryption, nonce operators directly before the write channel operator
                for (TGComponent tg: tad.getComponentList()){
                    if (tg instanceof TMLADWriteChannel){
                        TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
                        if (writeChannel.getChannelName().equals(channel) && writeChannel.getSecurityContext().equals("")){

                            if (fromStart!=null){
                                channelInstances.add(tg);
                            }
                        }
                    }
                }
                for (TGComponent comp: channelInstances){
                    //TMLADWriteChannel writeChannel = (TMLADWriteChannel) comp;
                    xpos = comp.getX();
                    ypos = comp.getY();
                    fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();
                    //Add encryption operator
                    TMLADEncrypt enc = new TMLADEncrypt(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    TMLADReadChannel rd=new TMLADReadChannel(0, 0, 0, 0, 0, 0, false, null, tad);
                    if (nonceOutChannels.get(task).contains(channel)){
                        //Receive any nonces if ensuring authenticity
                        rd = new TMLADReadChannel(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size()>0){
                            rd.setChannelName(matches.get(0).getName().replaceAll(title+"__",""));
                        }
                        else {
                            rd.setChannelName("nonceCh"+tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        rd.setSecurityContext("nonce_"+ tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(rd, xpos, ypos+yShift, false,true);
                        fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
                        fromStart=new TGConnectorTMLAD(enc.getX(), enc.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        yShift+=60;
                        //Move encryption operator after receive nonce component
                        enc.setCd(xpos, ypos+yShift);
                        if (tmlc!=null){
                            enc.nonce= "nonce_"+ tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1];
                        }
                    }

                    enc.securityContext = "autoEncrypt_"+channel;
                    enc.type = "Symmetric Encryption";
                    enc.message_overhead = overhead;
                    enc.encTime= encComp;
                    enc.decTime=decComp;
                    tad.addComponent(enc, xpos ,ypos+yShift, false, true);
                    yShift+=60;
                    fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                    fromStart=new TGConnectorTMLAD(enc.getX(), enc.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(enc.getTGConnectingPointAtIndex(1));

                    //Direct the last TGConnector back to the start of the write channel operator

                    fromStart.setP2(point);
                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg:tad.getComponentList()){
                        if (tg instanceof TMLADWriteChannel){
                            TMLADWriteChannel wChannel = (TMLADWriteChannel) tg;
                            TraceManager.addDev("Inspecting write channel " + wChannel.getChannelName());
                            if (channel.equals(wChannel.getChannelName()) && wChannel.getSecurityContext().equals("")){
                                TraceManager.addDev("Securing write channel " + wChannel.getChannelName());
                                wChannel.setSecurityContext("autoEncrypt_"+wChannel.getChannelName());

                            }
                        }
                        if (tg.getY() >= ypos && tg !=enc && tg!=rd){
                            tg.setCd(tg.getX(), tg.getY()+yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY()+yShift);
                    tad.repaint();
                }
            }

            for (String channel: macOutChannels.get(task)){
                //Add MAC before writechannel
                int yShift=50;
                TMLChannel tmlc = tmlmodel.getChannelByName(title +"__"+channel);
                //First, find the connector that points to it. We will add the encryption, nonce operators directly before the write channel operator
                Set<TGComponent> channelInstances = new HashSet<TGComponent>();
                for (TGComponent tg: tad.getComponentList()){
                    if (tg instanceof TMLADWriteChannel){
                        TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
                        if (writeChannel.getChannelName().equals(channel) && writeChannel.getSecurityContext().equals("")){
                            xpos = tg.getX();
                            ypos = tg.getY();
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart!=null){
                                channelInstances.add(tg);
                            }
                        }
                    }
                }
                for (TGComponent comp: channelInstances){
                    //TMLADWriteChannel writeChannel = (TMLADWriteChannel) comp;
                    xpos = comp.getX();
                    ypos = comp.getY();
                    fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();

                    TMLADEncrypt enc = new TMLADEncrypt(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

                    //If we need to receive a nonce
                    TMLADReadChannel rd=new TMLADReadChannel(0, 0, 0, 0, 0, 0, false, null, tad);
                    if (macNonceOutChannels.get(task).contains(channel)){
                        //Receive any nonces if ensuring authenticity
                        rd = new TMLADReadChannel(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size()>0){
                            rd.setChannelName(matches.get(0).getName().replaceAll(title+"__",""));
                        }
                        else {
                            rd.setChannelName("nonceCh"+tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        rd.setSecurityContext("nonce_"+ tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(rd, xpos, ypos+yShift, false,true);
                        fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
                        fromStart=new TGConnectorTMLAD(enc.getX(), enc.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        yShift+=60;
                        //Move encryption operator after receive nonce component
                        enc.setCd(xpos, ypos+yShift);
                        if (tmlc!=null){
                            enc.nonce= "nonce_"+ tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1];
                        }
                    }

                    //Add encryption operator

                    enc.securityContext = "autoEncrypt_"+channel;
                    enc.type = "MAC";
                    enc.message_overhead = overhead;
                    enc.encTime= encComp;
                    enc.decTime=decComp;
                    enc.size=overhead;
                    tad.addComponent(enc, xpos ,ypos+yShift, false, true);
                    yShift+=60;
                    fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                    fromStart=new TGConnectorTMLAD(enc.getX(), enc.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(enc.getTGConnectingPointAtIndex(1));

                    //Direct the last TGConnector back to the start of the write channel operator

                    fromStart.setP2(point);
                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg:tad.getComponentList()){
                        if (tg instanceof TMLADWriteChannel){
                            TMLADWriteChannel wChannel = (TMLADWriteChannel) tg;
                            TraceManager.addDev("Inspecting write channel " + wChannel.getChannelName());
                            if (channel.equals(wChannel.getChannelName()) && wChannel.getSecurityContext().equals("")){
                                TraceManager.addDev("Securing write channel " + wChannel.getChannelName());
                                wChannel.setSecurityContext("autoEncrypt_"+wChannel.getChannelName());
                                tad.repaint();
                            }
                        }
                        if (tg.getY() >= ypos && tg !=enc && tg!=rd){
                            tg.setCd(tg.getX(), tg.getY()+yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY()+yShift);
                }
            }
            for (String channel: macInChannels.get(task)){
                //Add decryptmac after readchannel
                int yShift=50;
                Set<TGComponent> channelInstances = new HashSet<TGComponent>();
                TGConnector conn =new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
                TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);
                //Find read channel operator

                for (TGComponent tg: tad.getComponentList()){
                    if (tg instanceof TMLADReadChannel){
                        TMLADReadChannel readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getChannelName().equals(channel) && readChannel.getSecurityContext().equals("")){
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart!=null){
                                channelInstances.add(tg);
                            }
                        }
                    }
                }


                for (TGComponent comp: channelInstances){

                    fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();
                    conn = tad.findTGConnectorStartingAt(comp.getTGConnectingPointAtIndex(1));
                    next= conn.getTGConnectingPointP2();
                    xpos = fromStart.getX();
                    ypos = fromStart.getY();


                    TMLADReadChannel readChannel = (TMLADReadChannel) comp;
                    TraceManager.addDev("Securing read channel " + readChannel.getChannelName());
                    readChannel.setSecurityContext("autoEncrypt_"+readChannel.getChannelName());
                    tad.repaint();

                    TMLADWriteChannel wr = new TMLADWriteChannel(0, 0, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    //Create nonce and send it
                    TMLChannel tmlc = tmlmodel.getChannelByName(title +"__"+channel);
                    if (macNonceInChannels.get(task).contains(channel)){
                        //Create a nonce operator and a write channel operator
                        TMLADEncrypt nonce = new TMLADEncrypt(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        nonce.securityContext = "nonce_"+ tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1];
                        nonce.type = "Nonce";
                        nonce.message_overhead = overhead;
                        nonce.encTime= encComp;
                        nonce.decTime=decComp;
                        tad.addComponent(nonce, xpos ,ypos+yShift, false, true);
                        fromStart.setP2(nonce.getTGConnectingPointAtIndex(0));
                        yShift+=50;
                        wr = new TMLADWriteChannel(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        //Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
                        //Find matching channels
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size()>0){
                            wr.setChannelName(matches.get(0).getName().replaceAll(title+"__",""));
                        }
                        else {
                            wr.setChannelName("nonceCh"+tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        //send the nonce along the channel
                        wr.setSecurityContext("nonce_"+tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(wr,xpos,ypos+yShift,false,true);
                        wr.makeValue();
                        TGConnector tmp =new TGConnectorTMLAD(wr.getX(), wr.getY()+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null,tad,nonce.getTGConnectingPointAtIndex(1), wr.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tad.addComponent(tmp, xpos,ypos,false,true);
                        fromStart=new TGConnectorTMLAD(wr.getX(), wr.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, wr.getTGConnectingPointAtIndex(1), null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        //Connect created write channel operator to start of read channel operator
                        fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(point);
                        //Shift everything from the read channel on down
                        for (TGComponent tg:tad.getComponentList()){
                            if (tg.getY() >= ypos && tg!=nonce && tg!=wr){
                                tg.setCd(tg.getX(), tg.getY()+yShift);
                            }
                        }
                    }

                    //Add decryption operator if it does not already exist
                    xpos = conn.getX();
                    ypos = conn.getY();

                    TMLADDecrypt dec = new TMLADDecrypt(xpos+10, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    dec.securityContext = "autoEncrypt_" + readChannel.getChannelName();
                    tad.addComponent(dec, dec.getX(), dec.getY(), false, true);
                    conn.setP2(dec.getTGConnectingPointAtIndex(0));
                    yShift+=60;
                    conn = new TGConnectorTMLAD(xpos,ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, dec.getTGConnectingPointAtIndex(1), next, new Vector<Point>());
                    conn.setP1(dec.getTGConnectingPointAtIndex(1));
                    conn.setP2(next);
                    tad.addComponent(conn, conn.getX(), conn.getY(), false,true);
                    //Shift everything down
                    for (TGComponent tg:tad.getComponentList()){
                        if (tg instanceof TMLADReadChannel){
                            readChannel = (TMLADReadChannel) tg;
                            TraceManager.addDev("Inspecting read channel " + readChannel.getChannelName());
                            if (channel.equals(readChannel.getChannelName()) && readChannel.getSecurityContext().equals("")){
                                TraceManager.addDev("Securing read channel " + readChannel.getChannelName());
                                readChannel.setSecurityContext("autoEncrypt_"+readChannel.getChannelName());

                            }
                        }
                        if (tg.getY() > ypos && tg!=dec && tg!=comp){

                            tg.setCd(tg.getX(), tg.getY()+yShift);
                        }
                    }


                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY()+yShift);
                    tad.repaint();
                }
            }
            for (String channel: secInChannels.get(task)){
                TraceManager.addDev("securing channel "+channel);
                int yShift=20;
                //        String title = task.getName().split("__")[0];
                TMLChannel tmlc = tmlmodel.getChannelByName(title +"__"+channel);
                TGConnector conn =new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
                TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);
                //Find read channel operator
                TMLADReadChannel readChannel = new TMLADReadChannel(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                HashSet<TGComponent> channelInstances = new HashSet<TGComponent>();
                for (TGComponent tg: tad.getComponentList()){
                    if (tg instanceof TMLADReadChannel){
                        readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getChannelName().equals(channel) && readChannel.getSecurityContext().equals("")){
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart!=null){
                                channelInstances.add(tg);
                            }
                        }
                    }
                }

                for (TGComponent comp: channelInstances){

                    fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();
                    conn = tad.findTGConnectorStartingAt(comp.getTGConnectingPointAtIndex(1));
                    next = conn.getTGConnectingPointP2();
                    xpos = fromStart.getX();
                    ypos = fromStart.getY();
                    TMLADWriteChannel wr = new TMLADWriteChannel(0, 0, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    if (nonceInChannels.get(task).contains(channel)){
                        //Create a nonce operator and a write channel operator
                        TMLADEncrypt nonce = new TMLADEncrypt(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        nonce.securityContext = "nonce_"+ tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1];
                        nonce.type = "Nonce";
                        nonce.message_overhead = overhead;
                        nonce.encTime= encComp;
                        nonce.decTime=decComp;
                        tad.addComponent(nonce, xpos ,ypos+yShift, false, true);
                        fromStart.setP2(nonce.getTGConnectingPointAtIndex(0));
                        yShift+=50;
                        wr = new TMLADWriteChannel(xpos, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        //Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
                        //Find matching channels
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size()>0){
                            wr.setChannelName(matches.get(0).getName().replaceAll(title+"__",""));
                        }
                        else {
                            wr.setChannelName("nonceCh"+tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        //send the nonce along the channel
                        wr.setSecurityContext("nonce_"+tmlc.getDestinationTask().getName().split("__")[1] + "_"+tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(wr,xpos,ypos+yShift,false,true);
                        wr.makeValue();
                        TGConnector tmp =new TGConnectorTMLAD(wr.getX(), wr.getY()+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null,tad,nonce.getTGConnectingPointAtIndex(1), wr.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tad.addComponent(tmp, xpos,ypos,false,true);
                        fromStart=new TGConnectorTMLAD(wr.getX(), wr.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, wr.getTGConnectingPointAtIndex(1), null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        //Connect created write channel operator to start of read channel operator
                        fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(point);
                        //Shift everything from the read channel on down
                        for (TGComponent tg:tad.getComponentList()){
                            if (tg.getY() >= ypos && tg!=nonce && tg!=wr){
                                tg.setCd(tg.getX(), tg.getY()+yShift);
                            }
                        }
                    }
                    //tad.repaint();

                    //Now add the decrypt operator
                    yShift=40;
                    TraceManager.addDev("Securing read channel " + readChannel.getChannelName());
                    readChannel.setSecurityContext("autoEncrypt_"+readChannel.getChannelName());
                    tad.repaint();
                    //Add decryption operator if it does not already exist
                    xpos = readChannel.getX();
                    ypos = readChannel.getY();
                    TMLADDecrypt dec = new TMLADDecrypt(xpos+10, ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    dec.securityContext = "autoEncrypt_" + readChannel.getChannelName();
                    tad.addComponent(dec, dec.getX(), dec.getY(), false, true);
                    conn.setP2(dec.getTGConnectingPointAtIndex(0));
                    yShift+=100;
                    conn = new TGConnectorTMLAD(xpos,ypos+yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, dec.getTGConnectingPointAtIndex(1), next, new Vector<Point>());
                    conn.setP1(dec.getTGConnectingPointAtIndex(1));

                    conn.setP2(next);
                    tad.addComponent(conn, conn.getX(), conn.getY(), false,true);
                    //Shift everything down
                    for (TGComponent tg:tad.getComponentList()){
                        if (tg instanceof TMLADReadChannel){
                            readChannel = (TMLADReadChannel) tg;
                            TraceManager.addDev("Inspecting read channel " + readChannel.getChannelName());
                            if (channel.equals(readChannel.getChannelName()) && readChannel.getSecurityContext().equals("")){
                                TraceManager.addDev("Securing read channel " + readChannel.getChannelName());
                                readChannel.setSecurityContext("autoEncrypt_"+readChannel.getChannelName());

                            }
                        }
                        if (tg.getY() > ypos && tg!=dec){

                            tg.setCd(tg.getX(), tg.getY()+yShift);
                        }
                    }

                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY()+yShift);

                    tad.repaint();
                }
            }
        }
        GTMLModeling gtm = new GTMLModeling(t, false);
        TMLModeling<TGComponent> newmodel = gtm.translateToTMLModeling(false,false);
        for (TMLTask task:newmodel.getTasks()){
            task.setName(tabName+"_"+name+"__"+task.getName());
        }
        for (TMLTask task: tmlmodel.getTasks()){
            HwExecutionNode node =(HwExecutionNode) map.getHwNodeOf(task);
            if (newmodel.getTMLTaskByName(task.getName().replace(tabName,tabName+"_"+name))!=null){
                map.addTaskToHwExecutionNode(newmodel.getTMLTaskByName(task.getName().replace(tabName,tabName+"_"+name)), node);
                map.removeTask(task);
            }
            else {
                System.out.println("Can't find " + task.getName());
            }
        }
        //map.setTMLModeling(newmodel);
        //System.out.println(map);
        //TMLMapping newMap = gtm.translateToTMLMapping();
        map.setTMLModeling(newmodel);
        return map;
    }


    public boolean securePath(TMLMapping<TGComponent> map, TMLTask t1, TMLTask t2){
        //Check if a path between two tasks is secure
        boolean secure=true;
        List<HwLink> links = map.getTMLArchitecture().getHwLinks();
        HwExecutionNode node1 = (HwExecutionNode) map.getHwNodeOf(t1);
        HwExecutionNode node2 = (HwExecutionNode) map.getHwNodeOf(t2);
        List<HwNode> found = new ArrayList<HwNode>();
        List<HwNode> done = new ArrayList<HwNode>();
        List<HwNode> path = new ArrayList<HwNode>();
        Map<HwNode, List<HwNode>> pathMap = new HashMap<HwNode, List<HwNode>>();
        TraceManager.addDev("Links " + links);
        if (node1==node2){
            return true;
        }
        for (HwLink link: links){
            if (link.hwnode == node1){
                found.add(link.bus);
                List<HwNode> tmp = new ArrayList<HwNode>();
                tmp.add(link.bus);
                pathMap.put(link.bus, tmp);
            }
        }
        outerloop:
        while (found.size()>0){
            HwNode curr = found.remove(0);
            for (HwLink link: links){
                if (curr == link.bus){
                    if (link.hwnode == node2){
                        path = pathMap.get(curr);
                        break outerloop;
                    }
                    if (!done.contains(link.hwnode) && !found.contains(link.hwnode) && link.hwnode instanceof HwBridge){
                        found.add(link.hwnode);
                        List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
                        tmp.add(link.hwnode);
                        pathMap.put(link.hwnode, tmp);
                    }
                }
                else if (curr == link.hwnode){
                    if (!done.contains(link.bus) && !found.contains(link.bus)){
                        found.add(link.bus);
                        List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
                        tmp.add(link.bus);
                        pathMap.put(link.bus, tmp);
                    }
                }
            }
            done.add(curr);
        }
        if (path.size() ==0){
            System.out.println("no path");
            return true;
        }
        else {
            HwBus bus;
            //Check if all buses and bridges are private
            for (HwNode n: path){
                if (n instanceof HwBus){
                    bus = (HwBus) n;
                    if (bus.privacy ==0){
                        return false;
                    }
                }
            }
        }
        return secure;
    }

    public void autoMapKeys(){
        TraceManager.addDev("auto map keys");
        if (tmap==null){
            return;
        }
        List<HwLink> links = tmap.getArch().getHwLinks();
        //Find all Security Patterns, if they don't have an associated memory at encrypt and decrypt, map them
        TMLModeling<TGComponent> tmlm = tmap.getTMLModeling();
        if (tmlm.securityTaskMap ==null){
            return;
        }
        //      System.out.println(tmlm.securityTaskMap);
        for (SecurityPattern sp: tmlm.securityTaskMap.keySet()){
            if (sp.type.contains("Encryption") || sp.type.equals("MAC")){
                TraceManager.addDev("Finding security "+sp);
                for (TMLTask t:tmlm.securityTaskMap.get(sp)){
                    ArrayList<HwMemory> mems = new ArrayList<HwMemory>();
                    boolean keyFound=false;
                    HwExecutionNode node1 = (HwExecutionNode) tmap.getHwNodeOf(t);
                    //Try to find memory using only private buses
                    List<HwNode> toVisit = new ArrayList<HwNode>();
                    //  List<HwNode> toMemory = new ArrayList<HwNode>();
                    List<HwNode> complete = new ArrayList<HwNode>();
                    for (HwLink link:links){
                        if (link.hwnode==node1){
                            if (link.bus.privacy==1){
                                toVisit.add(link.bus);
                            }
                        }
                    }
                    memloop:
                    while (toVisit.size()>0){
                        HwNode curr = toVisit.remove(0);
                        for (HwLink link: links){
                            if (curr == link.bus){
                                if (link.hwnode instanceof HwMemory){
                                    mems.add((HwMemory) link.hwnode);
                                    TMLArchiMemoryNode memNode= (TMLArchiMemoryNode) listE.getTG(link.hwnode);
                                    ArrayList<TMLArchiKey> keys = memNode.getKeyList();
                                    String patternString= "";
                                    for (TMLArchiKey key: keys){
                                        if (key.getValue().equals(sp.name)){

                                            keyFound=true;
                                            break memloop;
                                        }
                                        patternString += key.getValue();
                                        patternString += " ";
                                    }
                                    TraceManager.addDev("Memory "+ link.hwnode.getName() + " has currently mapped: " + patternString);
                                }
                                if (!complete.contains(link.hwnode) && !toVisit.contains(link.hwnode) && link.hwnode instanceof HwBridge){
                                    toVisit.add(link.hwnode);
                                }
                            }
                            else if (curr == link.hwnode){
                                if (!complete.contains(link.bus) && !toVisit.contains(link.bus)){
                                    toVisit.add(link.bus);
                                }
                            }
                        }
                        complete.add(curr);
                    }
                    if (!keyFound){
                        if (mems.size()>0){
                            TMLArchiMemoryNode memNode= (TMLArchiMemoryNode) listE.getTG(mems.get(0));
                            TMLArchiKey key = new TMLArchiKey(memNode.x, memNode.y, memNode.tdp.getMinX(), memNode.tdp.getMaxX(), memNode.tdp.getMinY(), memNode.tdp.getMaxY(), false, memNode, memNode.tdp);
                            key.setReferenceKey(sp.name);
                            key.makeFullValue();
                            TraceManager.addDev("Adding " +sp.name+ " key to " +memNode.getName());
                            TraceManager.addDev("Adding " +sp + " key to " +memNode.getName());
                            memNode.tdp.addComponent(key, memNode.x, memNode.y, true,true);
                            memNode.tdp.repaint();
                        }
                        else {
                            System.out.println("Can't map key to memory for " + sp.name + " on task " + t.getName());
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Cannot map key in memory for " + sp.name + " on task " + t.getName());
                            ce.setTDiagramPanel(tmap.getCorrespondanceList().getTG(tmap.getArch().getFirstCPU()).getTDiagramPanel());
                            ce.setTGComponent(null);
                            checkingErrors.add(ce);
                        }
                    }
                }
            }
        }
        TraceManager.addDev("Mapping finished");
    }

    public void generateAvatarFromTML(boolean mc, boolean security){
        TraceManager.addDev("Generating Avatar from TML");
        if (tmlm!=null && tmap==null){
            tmap = tmlm.getDefaultMapping();
        }
        if (avatarspec!=null){
            return;
        }
        else if (tmap!=null){
            t2a = new TML2Avatar(tmap, mc, security);
            TraceManager.addDev("Avatar spec generation");
            avatarspec = t2a.generateAvatarSpec("1");
            TraceManager.addDev("Avatar spec generation: done");
        }
    }

    public boolean generateProVerifFromAVATAR(String _path, int _stateReachability, boolean _typed, boolean allowPrivateChannelDuplication, String loopLimit) {
        //      System.out.println(avatarspec);
        if (avatarspec !=null){
            //use avspec
        }
        else if (tmap!=null){
            t2a = new TML2Avatar(tmap,false,true);
            avatarspec = t2a.generateAvatarSpec(loopLimit);
            drawPanel(avatarspec, mgui.getFirstAvatarDesignPanelFound());

        }
        else if (tmlm!=null){
            //Generate default mapping
            tmap = tmlm.getDefaultMapping();
          
            t2a=new TML2Avatar(tmap,false,true);
            avatarspec = t2a.generateAvatarSpec(loopLimit);
        }
        else if (avatarspec == null){
            return false;
        }

        avatar2proverif = new AVATAR2ProVerif(avatarspec);
        //tml2uppaal.setChoiceDeterministic(choices);
        //tml2uppaal.setSizeInfiniteFIFO(_size);
        proverif = avatar2proverif.generateProVerif(true, true, _stateReachability, _typed, allowPrivateChannelDuplication);
        warnings = avatar2proverif.getWarnings();
        languageID = PROVERIF;
        mgui.setMode(MainGUI.EDIT_PROVERIF_OK);
        //uppaalTable = tml2uppaal.getRelationTIFUPPAAL(_debug);
        try {
            if (avatar2proverif.saveInFile(_path)){
                TraceManager.addDev("Specification generated in " + _path);
                return true;
            }
            return false;
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


    public ArrayList<TGComponentAndUPPAALQuery> getUPPAALQueries() {
        return getUPPAALQueries(mgui.getCurrentTURTLEPanel());
    }

    public ArrayList<TGComponentAndUPPAALQuery> getUPPAALQueries(TURTLEPanel tp) {
        TraceManager.addDev("Searching for queries on " + mgui.getTabName(tp));
        ArrayList<TGComponent> list = new ArrayList<TGComponent>();
        ArrayList<TClass> tclasses;
        tp.getAllCheckableTGComponent(list);
        TGComponentAndUPPAALQuery tmpQ;

        ArrayList<TGComponentAndUPPAALQuery> listQ = new ArrayList<TGComponentAndUPPAALQuery>();

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
                                tmpQ = new TGComponentAndUPPAALQuery(null, s + "$" + adc);
                                listQ.add(tmpQ);
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
                            // Object ref;
                            if (elt.getReferenceObject() instanceof TGComponent) {
                                tmpQ = new TGComponentAndUPPAALQuery((TGComponent)(elt.getReferenceObject()), s + "$" + elt);
                            } else {
                                tmpQ = new TGComponentAndUPPAALQuery(null, s + "$" + elt);
                            }
                            listQ.add(tmpQ);
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
                    listQ.add(new TGComponentAndUPPAALQuery(tgc, s + "$" + block.getName() + "." + tgc));
                } else {
                    TraceManager.addDev("Could not make query for " + tgc);
                }
            }
        } else if ((avatar2uppaal != null) && (tp instanceof AttackTreePanel)) {
            TraceManager.addDev("Making UPPAAL queries");
            for(TGComponent tgc: list) {
                TraceManager.addDev("Making UPPAAL query for " + tgc);
                String s = avatar2uppaal.getUPPAALIdentification(tgc);
                TraceManager.addDev("Query: " + s);
                if ((s!= null) && (s.length() > 0)) {
                    AvatarBlock block = avatar2uppaal.getBlockFromReferenceObject(tgc);
                    listQ.add(new TGComponentAndUPPAALQuery(tgc, s + "$" + block.getName() + "." + tgc));
                } else {
                    TraceManager.addDev("Could not make query for " + tgc);
                }
            }
        }

        return listQ;
    }

    public List<String> generateLOTOSAUT(String path) {
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
        rshc.sendExecuteCommandRequest();
        s = rshc.getDataFromProcess();
        return s;
    }

    public void showSIM(int type) {
        if (sim != null) {
            JFrameSimulationTrace jfst = new JFrameSimulationTrace("Last simulation trace", sim, type);
            jfst.setIconImage(IconManager.img8);
            //  jfst.setSize(900, 600);
            GraphicLib.centerOnParent( jfst, 900, 600 );
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
        //TraceManager.addDev("Show diplodocus graph located in " + ConfigurationTTool.GGraphPath + "/tree.dot");
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
        return  rtlotos;
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

    public TMLModeling<TGComponent> getTMLModeling() {
        return tmlm;
    }
    public TML2Avatar getTML2Avatar(){
        return t2a;
    }
    public TMLMapping<TGComponent> getArtificialTMLMapping() {
        return artificialtmap;
    }

    public TMLMapping<TGComponent> getTMLMapping() {
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
        return panels.size() + 4;
    }

    public Object getChild(int index) {
        if (index < panels.size()) {
            return panels.elementAt(index);
        } else if (index == panels.size()) {
            return mcvdt;
        } else if (index == (panels.size() + 1)) {
            return gt;
        }  else if (index == (panels.size() + 2)) {
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

        if (child == gt) {
            return panels.size() + 1;
        }

        if (child == idt) {
            return panels.size() + 2;
        }

        return panels.size()+3;
    }

    // Projection management

    public MasterGateManager getNewMasterGateManager() {
        return new MasterGateManager(tm);
    }

    // Assume the inputData is in AUT format: generated by RTL or CADP
    public String performProjection(String inputData, LinkedList<TClassAndGateDS> gates) {
        StringBuffer result = new StringBuffer("");
        StringReader sr = new StringReader(inputData);
        BufferedReader br = new BufferedReader(sr);
        String s;
        String actionName, actionName1;
        int index, index1, index2;
        MasterGateManager mgm = new MasterGateManager(tm, 1);
        //   Gate g;
        GroupOfGates gog;
        Hashtable <String, GroupOfGates> hashtable = new Hashtable<String, GroupOfGates>();

        //   int cpt = 0;

        //TraceManager.addDev("input data=" + inputData);

        // Fill Hashtable
        // int j;
        for (TClassAndGateDS tag: gates) {
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
                //   cpt ++;

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
        Map<String, Gate> ht = mgm.getGatesUpperCaseHashTable();
        warnings = new LinkedList<CheckingError> ();

        //TraceManager.addDev("input data=" + inputData);

        //   int cpt1 = 0;

        try {
            while((s = br.readLine()) != null) {
                //   cpt1 ++;
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
                            g = ht.get(actionName);

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


    public boolean belongTo(GroupOfGates gog, Vector<TClassAndGateDS> gates) {
        int i, j;
        TClassAndGateDS tcg;
        String nameTClass, nameGate;
        for(i=0; i<gog.size(); i++) {
            nameTClass = gog.getTClassAt(i).getName();
            nameGate = gog.getGateAt(i).getName();
            for(j=0; j<gates.size(); j++) {
                tcg = gates.elementAt(j);

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
            Point p1  = savedPanels.elementAt(size - 1); // panels are saved under the form of a point -> x = analysis/design, y = panel
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
        if ((pointerOperation < 1)        || (savedOperations.size() < 2)) {
            TraceManager.addDev("Undo not possible");
            undoRunning = false;
            return;
        }

        removeAllComponents();
        mgui.reinitMainTabbedPane();

        // Issue #42: the selected tabs should be memorized before decrementing the pointer
        final Point prevSelectedTabs = savedPanels.elementAt( pointerOperation );

        try {
            pointerOperation --;
            TraceManager.addDev("Decrementing pointer =" + pointerOperation);
            loadModelingFromXML(savedOperations.elementAt(pointerOperation));

        } catch (Exception e) {
            TraceManager.addError("Exception in backward: " + e.getMessage());
        }

        TraceManager.addDev("Selecting tab");

        // Issue #42:
        //Point p = savedPanels.elementAt(pointerOperation);

        if ( prevSelectedTabs != null ) {
            TraceManager.addDev("Selecting tab panel=" + prevSelectedTabs.getX() + " diagram=" + prevSelectedTabs.getY());
            TDiagramPanel tdp = mgui.selectTab( prevSelectedTabs );
            tdp.mode = TDiagramPanel.NORMAL;
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
        if ((pointerOperation < 0) || (pointerOperation >          savedOperations.size() - 2)) {
            return;
        }

        undoRunning = true;
        removeAllComponents();
        mgui.reinitMainTabbedPane();

        // Issue #42: the selected tabs should be memorized before incrementing the pointer
        final Point prevSelectedTabs = savedPanels.elementAt( pointerOperation );

        try {
            pointerOperation ++;
            loadModelingFromXML(savedOperations.elementAt(pointerOperation));
        } catch (Exception e) {
            TraceManager.addError("Exception in forward: " + e.getMessage());
        }

        //Point prevSelectedTabs = savedPanels.elementAt(pointerOperation);
        if ( prevSelectedTabs != null ) {
            TDiagramPanel tdp = mgui.selectTab( prevSelectedTabs );
            tdp.mode = TDiagramPanel.NORMAL;
            tdp.setDraw(true);
            tdp.repaint();
        }

        selectBackwardMode();
        undoRunning = false;
    }


    // BUILDING A TURTLE MODELING AND CHECKING IT
    public boolean checkTURTLEModeling(LinkedList<TClassInterface> tclasses, DesignPanel dp, boolean overideSyntaxChecking) {
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
    public boolean checkAvatarDesign(LinkedList<AvatarBDStateMachineOwner> blocks, AvatarDesignPanel adp, boolean _optimize) {
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
        return !((checkingErrors != null) && (checkingErrors.size() > 0));

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

        if (adp == null) {
            return;
        }

        adp.removeAllMutualExclusionWithMasterMutex();



    }


    // Returns the number of states found
    // Returns -1 in case of error
    public int computeMutexStatesWith(AvatarSMDState state) {
        Vector<TGComponent> list = new Vector<TGComponent>();

        if (state == null) {
            // DB Issue 17 this will cause null pointer exception
            //                  state.setMutexWith(TGComponent.MUTEX_UNKNOWN);
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

    public List<CheckingError> getCheckingErrors() {
        return checkingErrors;
    }

    public List<CheckingError> getCheckingWarnings() {
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

        if (tgc instanceof AvatarBDLibraryFunction) {
            AvatarSMDPanel asmdp = mgui.getAvatarSMDPanel(mgui.getCurrentSelectedIndex(), ((AvatarBDLibraryFunction) tgc).getFunctionName ());
            s.append(asmdp.saveInXML());
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
            List<TMLCPrimitiveComponent> list =  ((TMLCCompositeComponent)tgc).getAllPrimitiveComponents();

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

        final Vector<TCDTClass> classes = tdp.selectedTclasses();

        if ((classes != null) && (classes.size() > 0)) {
            TCDTClass t;
            TActivityDiagramPanel tadp;
            for(int i=0; i<classes.size(); i++) {
                t = classes.elementAt(i);
                tadp = mgui.getActivityDiagramPanel(mgui.getCurrentSelectedIndex(), t.getValue());
                s.append(tadp.saveInXML());
            }
        }

        //Added by Solange
        //bug removed by Emil
        if (tdp instanceof ProactiveCSDPanel) {
            final Vector<ProCSDComponent> comp =((ProactiveCSDPanel)tdp).selectedProCSDComponent(null);

            if ((comp != null) && (comp.size() > 0)) {
                ProCSDComponent t;
                ProactiveSMDPanel psmd;
                for(int i=0; i<comp.size(); i++) {
                    t = comp.elementAt(i);
                    psmd = mgui.getSMDPanel(mgui.getCurrentSelectedIndex(), t.getValue());
                    if (psmd!=null)
                        s.append(psmd.saveInXML());
                }
            }
        }
        //until here


        final Vector<TOSClass> toClasses = tdp.selectedTURTLEOSClasses();

        if ((toClasses != null) && (toClasses.size() > 0)) {
            //TraceManager.addDev("Saving TURTLEOS activity diagram Panel...");
            TOSClass t;
            TURTLEOSActivityDiagramPanel tosadp;
            for(int i=0; i<toClasses.size(); i++) {
                t = toClasses.elementAt(i);
                tosadp = mgui.getTURTLEOSActivityDiagramPanel(mgui.getCurrentSelectedIndex(), t.getValue());
                s.append(tosadp.saveInXML());
            }
        }

        final Vector<TMLTaskOperator> operators = tdp.selectedTMLTasks();
        if ((operators != null) && (operators.size() > 0)) {
            //TraceManager.addDev("Saving TML activity diagram Panel...");
            TMLTaskOperator t;
            TMLActivityDiagramPanel tmladp;
            for(int i=0; i<operators.size(); i++) {
                t = operators.elementAt(i);
                tmladp = mgui.getTMLActivityDiagramPanel(mgui.getCurrentSelectedIndex(), t.getValue());
                s.append(tmladp.saveInXML());
            }
        }

        final Vector<AvatarBDBlock> blocks = tdp.selectedAvatarBDBlocks();
        if ((blocks != null) && (blocks.size() > 0)) {
            //TraceManager.addDev("Saving TML activity diagram Panel...");
            AvatarBDBlock abdb;
            AvatarSMDPanel asmdp;
            for(int i=0; i<blocks.size(); i++) {
                abdb = blocks.elementAt(i);
                asmdp = mgui.getAvatarSMDPanel(mgui.getCurrentSelectedIndex(), abdb.getBlockName());
                s.append(asmdp.saveInXML());

            }
        }

        final Vector<TMLCPrimitiveComponent> primComps = tdp.selectedCPrimitiveComponent();

        if ((primComps != null) && (primComps.size() > 0)) {
            //TraceManager.addDev("Saving TML activity diagram Panel...");
            TMLCPrimitiveComponent ct;
            TMLActivityDiagramPanel tmladp;
            for(int i=0; i<primComps.size(); i++) {
                ct = primComps.elementAt(i);
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
        return makeXMLFromTurtleModeling(index, null);
    }

    public String makeXMLFromTurtleModeling(int index, String extensionToName) {
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
                tp = panels.elementAt(i);
                s = tp.saveInXML(extensionToName);
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
        Vector<TDiagramPanel> panelss;
        // search for diagram panels
        for(i=0; i<panels.size(); i++) {
            panelss = (panels.elementAt(i).panels);
            for(j=0; j<panelss.size(); j++) {
                tdp = panelss.elementAt(j);
                tdp.removeAll();
            }
        }
    }


    private void prepareErrors() {
        checkingErrors = new ArrayList<CheckingError>();
        warnings = new ArrayList<CheckingError>();
    }

    public void copyModelingFromXML(TDiagramPanel tdp, String s, int X, int Y) throws MalformedModelingException {
        //TraceManager.addDev("copyModelingFromXML: " + s);
        //TraceManager.addDev("tdp: " + tdp);

        //TraceManager.addDev(s);
        //TraceManager.addDev("copyModelingFromXML:");
        //LinkedList ComponentsList=tdp.getComponentList();

        prepareErrors();


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
            } else if (tdp instanceof ui.sd.SequenceDiagramPanel) {
                //TraceManager.addDev("Sequence diagram!");
                nl = doc.getElementsByTagName("SequenceDiagramPanelCopy");

                if (nl == null) {
                    return;
                }

                ui.sd.SequenceDiagramPanel sdp = (ui.sd.SequenceDiagramPanel)tdp;

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
            } else if (tdp instanceof ui.sd2.SequenceDiagramPanel) {
                //TraceManager.addDev("Sequence diagram!");
                nl = doc.getElementsByTagName("SequenceDiagramPanelCopy");

                if (nl == null) {
                    return;
                }

                ui.sd2.SequenceDiagramPanel sdp = (ui.sd2.SequenceDiagramPanel)tdp;

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
                nl = doc.getElementsByTagName("AvatarMethodologyDiagramPanelCopy");
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
            }else if (tdp instanceof SysmlsecMethodologyDiagramPanel) {
                nl = doc.getElementsByTagName("SysmlsecMethodologyDiagramPanelCopy");
                docCopy = doc;

                if (nl == null) {
                    return;
                }

                //TraceManager.addDev("Toto 1");


                SysmlsecMethodologyDiagramPanel amdp = (SysmlsecMethodologyDiagramPanel)tdp;


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


                                //                                                                                        int xSel = Integer.decode(elt.getAttribute("xSel")).intValue();
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

                                //                                                                                        int xSel = Integer.decode(elt.getAttribute("minX")).intValue();
                                //int ySel = Integer.decode(elt.getAttribute("maxX")).intValue(); // - mgui.getCurrentTDiagramPanel().currentX;
                                //                                                                                        int widthSel = Integer.decode(elt.getAttribute("minY")).intValue(); // - mgui.getCurrentTDiagramPanel().currentY;;
                                //                                                                                        int heightSel = Integer.decode(elt.getAttribute("maxY")).intValue(); // - mgui.getCurrentTDiagramPanel().currentY;;

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
        StringBuffer sb = new StringBuffer("");
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

        prepareErrors();

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
                        TraceManager.addDev("Error when loading diagram: " + elt + " " +type);
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
        } else if (type.compareTo("Sysmlsec Methodology") == 0) {
            loadSysmlsecMethodology(node);
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

        /* First load all Block diagrams, then all state machines
         * This is done this way so that state machines can rely on
         * informations provided by blocks. For instance we can check
         * that an attribute used in a state machine really exists.
         */
        for(int j=0; j<diagramNl.getLength(); j++) {
            node = diagramNl.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elt = (Element) node;
                if (elt.getTagName().compareTo("AVATARBlockDiagramPanel") == 0)
                    // Class diagram
                    loadAvatarBD(elt, indexDesign);
            }
        }
        for(int j=0; j<diagramNl.getLength(); j++) {
            node = diagramNl.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elt = (Element) node;
                if (elt.getTagName().compareTo("AVATARStateMachineDiagramPanel") == 0)
                    // Managing activity diagrams
                    loadAvatarSMD(elt, indexDesign);
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

    public void loadAttackTree(Node node) throws  MalformedModelingException, SAXException {
        Element elt = (Element) node;
        String nameTab;
        NodeList diagramNl;
        int indexTree;
        int cpttdp = 0;


        nameTab = elt.getAttribute("nameTab");

        indexTree = mgui.createAttackTree(nameTab);

        diagramNl = node.getChildNodes();

        for(int j=0; j<diagramNl.getLength(); j++) {
            //TraceManager.addDev("Deployment nodes: " + j);
            node = diagramNl.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elt = (Element)node;
                if (elt.getTagName().compareTo("AttackTreeDiagramPanel") == 0) {
                    loadAttackTreeDiagram(elt, indexTree, cpttdp);
                    cpttdp ++;
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
                    } else if (elt.getTagName().compareTo("SequenceDiagramPanelZV") == 0) {
                        loadSequenceDiagramZV(elt, indexAnalysis);
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
        //  int cpt = 0;

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
                    // cpt ++;
                } else { // Managing sequence diagrams
                    if (elt.getTagName().compareTo("TMLSDPanel") == 0) {
                        loadTMLSDDiagram(elt, indexTMLCP);
                        //       cpt ++;
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
                } else if (elt.getTagName().compareTo("SequenceDiagramPanelZV") == 0) {
                    //TraceManager.addDev("Loading seq diag");
                    loadSequenceDiagramZV(elt, indexAnalysis);
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
                    TraceManager.addDev("Loading Avatar methodo");
                    loadAvatarMethodologyDiagram(elt, indexDesign);
                    TraceManager.addDev("End Loading avatar methodo");
                }
            }
        }
    }

    public void loadSysmlsecMethodology(Node node) throws  MalformedModelingException, SAXException {
        Element elt = (Element) node;
        String nameTab;
        NodeList diagramNl;
        int indexDesign;


        nameTab = elt.getAttribute("nameTab");

        indexDesign = mgui.createSysmlsecMethodology(nameTab);

        diagramNl = node.getChildNodes();

        for(int j=0; j<diagramNl.getLength(); j++) {
            //TraceManager.addDev("Design nodes: " + j);
            node = diagramNl.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elt = (Element)node;
                if (elt.getTagName().compareTo("SysmlsecMethodologyDiagramPanel") == 0) {
                    // Class diagram
                    TraceManager.addDev("Loading SysMLSec methodo");
                    loadSysmlsecMethodologyDiagram(elt, indexDesign);
                    TraceManager.addDev("End loading SysMLSec methodo");
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
            //TraceManager.addDev("TML Architecture nodes: " + j);
            node = diagramNl.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elt = (Element)node;
                if (elt.getTagName().compareTo("TMLArchiDiagramPanel") == 0) {
                    //TraceManager.addDev("Loading TML DD" + elt.getTagName() );
                    loadTMLArchitectureDiagram(elt, indexDesign);
                    //TraceManager.addDev("End loading TML DD");
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

        mgui.selectDummyTab (indexDesign);
        loadDiagram(elt, asmdp);
        mgui.forgetDummyTab ();
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
            // Try to get the first diagram of the panel
            tdp = mgui.getAvatarADDPanelByIndex(indexAnalysis, indexTab);
            if (tdp == null) {
                TraceManager.addDev("ADD 3.2");
                throw new MalformedModelingException();
            }
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

        //TraceManager.addDev("tdp=" + tdp.getName());

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

    public void loadSysmlsecMethodologyDiagram(Element elt, int indexDesign) throws  MalformedModelingException, SAXException {

        String name;
        TDiagramPanel tdp;

        // class diagram name
        name = elt.getAttribute("name");
        mgui.setSysmlsecMethodologyDiagramName(indexDesign, name);
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

    public void loadAttackTreeDiagram(Element elt, int indexDiag, int indexTab) throws  MalformedModelingException, SAXException {
        String name;

        //TraceManager.addDev("indexDiag=" + indexDiag);

        name = elt.getAttribute("name");
        mgui.createAttackTreeDiagram(indexDiag, name);

        TDiagramPanel tdp = mgui.getAttackTreeDiagramPanel(indexDiag, indexTab, name);

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
        ui.sd.SequenceDiagramPanel sdp = mgui.getSequenceDiagramPanel(indexAnalysis, name);
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

    public void loadSequenceDiagramZV(Element elt, int indexAnalysis) throws  MalformedModelingException, SAXException {
        String name;

        name = elt.getAttribute("name");
        if (!(mgui.isSDZVCreated(indexAnalysis, name))) {
            mgui.createSequenceDiagramZV(indexAnalysis, name);
        }
        //TraceManager.addDev("Loading seq diag1");
        ui.sd2.SequenceDiagramPanel sdp = mgui.getSequenceDiagramPanelZV(indexAnalysis, name);
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
            tp = panels.elementAt(i);
            for(j=0; j<tp.panels.size(); j++) {
                tdp = tp.panels.elementAt(j);
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

        List<TGComponent> list = tdp.getComponentList();

        for(int i=0; i<list.size()-beginIndex; i++) {
            tgc = list.get(i);
            //TraceManager.addDev(tgc.getName());
            //TraceManager.addDev(tgc.getValue());
            tgc.makePostLoading(decId);
        }
        /*SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
          mgui.repaintAll();
          }
          });*/


        //TraceManager.addDev("Post loading of diagram " + tdp.toString() + " achieved");
    }

    public void makeXMLComponents(NodeList nl, TDiagramPanel tdp) throws SAXException, MalformedModelingException {
        Node n;
        //Element elt;
        TGComponent tgc;

        if (tdp == null) {
            throw new MalformedModelingException();
        }
        //boolean error = false;

        tdp.drawable = false;

        try {

            for(int i=0; i<nl.getLength(); i++) {
                n = nl.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    try {
                        tgc = makeXMLComponent(n, tdp);
                        //TraceManager.addDev("About to add component= " + tgc);
                        if ((tgc != null) && (tgc.getFather() == null)) {
                            //TraceManager.addDev("Component added to diagram tgc=" + tgc);
                            tdp.addBuiltComponent(tgc);
                        } else if (tgc == null) {
                            TraceManager.addDev("Component not added to diagram:" + tgc);
                        }
                    } catch (MalformedModelingException mme) {
                        int type = getTypeOfComponentNode(n);
                        String t = "unknown";
                        if (type > 0) {
                            t = "" + type;
                        }
                        TraceManager.addDev ("A badly formed component could not be created in the diagram");

                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "A component could not be correctly loaded - type=" + t);
                        ce.setTDiagramPanel(tdp);
                        checkingErrors.add(ce);
                        //error = true;
                    }
                }
            }

        } catch (SAXException se) {
            tdp.drawable = true;
            throw se;
        } catch (Exception e) {
            tdp.drawable = true;
            throw e;
        }

        tdp.drawable = true;

        /*if (error) {
          throw new MalformedModelingException();
          }*/
    }

    public int getTypeOfComponentNode(Node n) {
        try {
            //NodeList nl = n.getChildNodes();
            Element elt = (Element)n;
            return Integer.decode(elt.getAttribute("type")).intValue();
        } catch (Exception e){
        }
        return -1;
    }


    public TGComponent makeXMLComponent(Node n, TDiagramPanel tdp) throws SAXException, MalformedModelingException {
        Element elt;
        Element elt1;
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
            Vector<Point> tgcpList = new Vector<Point>();
            Point p;
            int i, x, y;
            int fatherId = -1, fatherNum = -1;
            String pre = "", post = "";
            String internalComment = "";
            boolean accessibility = false;
            boolean latencyCheck = false;
            boolean invariant = false;
            boolean breakpoint = false;
            boolean hidden = false;
            boolean masterMutex = false;
            boolean enable = true;

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
                    } else if (elt.getTagName().equals("enabled")) {
                        enable = elt.getAttribute("value").equals("true");
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
                    } else if (elt.getTagName().equals("latencyCheck")) {
                        latencyCheck = true;
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
            // TraceManager.addDev("TGComponent (" + tgc + ") built " + myType);

            if (tgc == null) {
                throw new MalformedModelingException();
            }

            if (myName != null) {
                tgc.setName(myName);
            }

            tgc.setHidden(hidden);
            tgc.setEnabled(enable);

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

            if (latencyCheck) {
                tgc.setCheckLatency(latencyCheck);

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
            // TraceManager.addDev("Extra params" + tgc.getClass());
            // TraceManager.addDev("My value = " + tgc.getValue());
            tgc.loadExtraParam(elt1.getElementsByTagName("extraparam"), decX, decY, decId);
            // TraceManager.addDev("Extra param ok");

            if ((tgc instanceof TCDTObject) && (decId > 0)) {
                TCDTObject to = (TCDTObject)tgc;
                //TraceManager.addDev("Setting TObject name to: " + to.getObjectName());
                //TraceManager.addDev("Setting TObject name to: " + tdp.findTObjectName(to.getObjectName()));
                to.setObjectName(tdp.findTObjectName(to.getObjectName()));
            }

            //TraceManager.addDev(tgc.toString());

            //TraceManager.addDev("Making connecting points " + tgcpList.size());
            for(i=0; i<tgcpList.size(); i++) {
                p = tgcpList.elementAt(i);
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
            // TraceManager.addError("Exception XML Component "/* + e.getMessage() + "trace=" + e.getStackTrace()*/);
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
        List<TGComponent> list = tdp.getComponentList();
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
            tgc = list.get(i);
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
            Vector<Point> pointList = new Vector<Point>();

            Vector<Point> tgcpList = new Vector<Point>();
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
                        //TraceManager.addDev("set to true");
                        automaticDrawing = elt.getAttribute("data").compareTo("true") == 0;
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
                p = tgcpList.elementAt(i);
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
        checkingErrors = new LinkedList<CheckingError> ();

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
        warnings = new LinkedList<CheckingError> ();
        //TraceManager.addDev("Step 02");

        mgui.setMode( MainGUI.VIEW_SUGG_DESIGN_KO);

        //TraceManager.addDev("Step 1");
        try {
            tm = sd.toTURTLEModeling();
            tmState = 0;
        } catch (SDTranslationException e) {
            checkingErrors = new LinkedList<CheckingError> ();
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
        checkingErrors = new LinkedList<CheckingError> ();
        warnings = new LinkedList<CheckingError> ();
        mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
        tm = new TURTLEModeling();
        tmState = 0;
        listE = new CorrespondanceTGElement();
        mgui.reinitCountOfPanels();

        List<TDDNode> ll;
        Iterator<TDDNode> iterator;//, iterator2;

        // First step: adding all necessary classes + their ad
        ll = dp.tddp.getListOfNodes();
        iterator = ll.listIterator();
        TDDNode node;
        Vector<TDDArtifact> artifacts;
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
            node = iterator.next();

            // Loop on artifact
            artifacts = node.getArtifactList();
            for(i=0; i<artifacts.size(); i++) {
                art = artifacts.elementAt(i);
                dp2 = art.getDesignPanel();

                final Iterator<TGComponent> iterator2 = dp2.tcdp.getComponentList().listIterator();
                LinkedList<TClassInterface> tclasses = new LinkedList<TClassInterface> ();
                while(iterator2.hasNext()) {
                    tgc = iterator2.next();
                    if (tgc instanceof TClassInterface) {
                        TraceManager.addDev("Found tclass: " + tgc.getValue());
                        tclasses.add((TClassInterface) tgc);
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
                        tgc = (TGComponent) tclasses.get (j);
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

    public boolean translateAttackTreePanel(AttackTreePanel atp) {
        AttackTreePanelTranslator att = new AttackTreePanelTranslator(atp);
        /*attackTree =*/ att.translateToAttackTreeDataStructure();
        checkingErrors = att.getCheckingErrors();
        warnings = att.getWarnings();
        if ((checkingErrors != null) && (checkingErrors.size() > 0)){
            return false;
        }
        avatarspec = att.generateAvatarSpec();
        TraceManager.addDev("Avatar spec:" + avatarspec);
        return true;
    }

    public boolean translateNC(NCPanel ncp) {
        TraceManager.addDev("Translating NC");
        checkingErrors = new LinkedList<CheckingError> ();
        warnings = new LinkedList<CheckingError> ();
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

    private void nullifyTMLModeling() {
        tmlm = null;
        artificialtmap = null;
        tmap = null;
        tmlcp = null;
    }


    public boolean translateTMLDesign(Vector<? extends TGComponent> tasksToTakeIntoAccount, TMLDesignPanel tmldp, boolean optimize) {
        nullifyTMLModeling();
        //  List<TMLError> warningsOptimize = new ArrayList<TMLError>();
        warnings = new LinkedList<CheckingError> ();
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
        warnings = gtmlm.getCheckingWarnings();

        if ((checkingErrors != null) && (checkingErrors.size() > 0)){
            analyzeErrors();

            return false;
        } else {
            //                  if (optimize) {
            //                          warningsOptimize = tmlm.optimize();
            //                  }

            tmState = 2;
            mgui.resetAllDIPLOIDs();
            listE.useDIPLOIDs();
            return true;
            //TraceManager.addDev("tm generated:");
            //tm.print();
        }
    }

    public Vector<CheckingError> convertToCheckingErrorTMLErrors( List<TMLError> warningsOptimize, TDiagramPanel _tdp) {
        Vector<CheckingError> v = new Vector<CheckingError>();
        UICheckingError warning;

        for(TMLError error: warningsOptimize) {
            warning = new UICheckingError(CheckingError.BEHAVIOR_ERROR, error.message);
            warning.setTDiagramPanel(_tdp);
            v.add(warning);
        }

        return v;
    }

    public boolean translateTMLComponentDesign(Vector<? extends TGComponent> componentsToTakeIntoAccount, TMLComponentDesignPanel tmlcdp, boolean optimize) {
        nullifyTMLModeling();
        //      ArrayList<TMLError> warningsOptimize = new ArrayList<TMLError>();
        warnings = new LinkedList<CheckingError> ();
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
        warnings = gctmlm.getCheckingWarnings();

        if ((checkingErrors != null) && (checkingErrors.size() > 0)){
            analyzeErrors();
            return false;
        }
        else {
            //                  if (optimize) {
            //                          //TraceManager.addDev("OPTIMIZE");
            //                          warningsOptimize = tmlm.optimize();
            //                  }

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
            TraceManager.addDev("Error in TURTLE generation");
            analyzeErrors();
            return false;
        } else {
            // Optimize
            TraceManager.addDev("Optimize");
            tm.optimize();
            TraceManager.addDev("Optimize done");
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

    public boolean checkSyntaxTMLMapping(Vector<TGComponent> nodesToTakeIntoAccount, TMLArchiPanel tmlap, boolean optimize) {
        List<TMLError> warningsOptimize = new ArrayList<TMLError>();
        warnings = new LinkedList<CheckingError> ();
        mgui.setMode(MainGUI.VIEW_SUGG_DESIGN_KO);
        //TraceManager.addDev("New TML Mapping");
        GTMLModeling gtmlm = new GTMLModeling(tmlap, true);


        gtmlm.setNodes(nodesToTakeIntoAccount); //simply transforms the parameter from a Vector to LinkedList
        nullifyTMLModeling();
        tmlm = null;
        tm = null;
        tmState = 1;
        tmap = gtmlm.translateToTMLMapping();

        listE = gtmlm.getCorrespondanceTable();

        checkingErrors = gtmlm.getCheckingErrors();
        warnings = gtmlm.getCheckingWarnings();

        avatarspec = gtmlm.avspec;
        if ((checkingErrors != null) && (checkingErrors.size() > 0)){
            analyzeErrors();
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
    public boolean checkSyntaxTMLCP( Vector<TGComponent> nodesToTakeIntoAccount, TMLCommunicationPatternPanel tmlcpp, boolean optimize ) {

        //nodesToTakeIntoAccount is the list of SDs and ADs corresponding that compose the CP selected for syntax checking
        //      List<TMLError> warningsOptimize = new ArrayList<TMLError>();
        warnings = new LinkedList<CheckingError> ();
        mgui.setMode( MainGUI.VIEW_SUGG_DESIGN_KO );
        GTMLModeling gtmlm = new GTMLModeling( tmlcpp, true );

        TraceManager.addDev( "NodesToTakeIntoAccount :" + nodesToTakeIntoAccount.toString() );

        //Useless because nodesToTakeIntoAccount does not include the mainCP!
        //gtmlm.setDiagramPanels( nodesToTakeIntoAccount );      //passes the list of nodes (SDs and ADs) to gtml as a LinkedList
        tmlm = null;
        tm = null;
        tmState = 1;
        nullifyTMLModeling();
        //tmlcp is the data structure for a CP corresponding to the graphical description with diagrams
        tmlcp = gtmlm.translateToTMLCPDataStructure( tmlcpp.getName() );
        //tmlcp.toString();
        /*TraceManager.addDev( "I AM ABOUT TO GENERATE THE TMLtxt CODE!" );
          mgui.generateTMLTxt();                //Now generating the TMLtxt code
          TraceManager.addDev( "TMLtxt CODE GENERATION DONE" );*/
        listE = gtmlm.getCorrespondanceTable();
        //for( CorrespondanceTGElement element : listE.getNames() )      {
        TraceManager.addDev( "Printing listE.getNames: " + listE.getNames().toString() );
        TraceManager.addDev( "Printing listE.getTG: " + listE.getTG().toString() );
        TraceManager.addDev( "Printing listE.getPanelNames: " + listE.getPanelNames().toString() );
        TraceManager.addDev( "Printing listE.getData: " + listE.getData().toString() );
        //}
        checkingErrors = gtmlm.getCheckingErrors();

        if( (checkingErrors != null) && (checkingErrors.size() > 0) )   {
            //analyzeErrors();

            return false;
        }
        /*else {
        //tmcp.removeAllRandomSequences();
        if( optimize )  {
        warningsOptimize = tmap.optimize();
        //warningsOptimize = tmcp.optimize();
        }
        warnings.addAll( convertToCheckingErrorTMLErrors(warningsOptimize, tmlcpp.tmlcpp ) );
        mgui.resetAllDIPLOIDs();
        listE.useDIPLOIDs();
        mgui.setMode( MainGUI.GEN_DESIGN_OK );
        return true;
        }*/
        return true;    //It means that there are no errors
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
        warnings = new LinkedList<CheckingError> ();
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


    public void addStates(AvatarStateMachineElement asme, int x, int y, AvatarSMDPanel smp, AvatarBDBlock bl, Map<AvatarStateMachineElement, TGComponent> SMDMap, Map<AvatarStateMachineElement, TGComponent> locMap, Map<AvatarTransition, AvatarStateMachineElement> tranDestMap, Map<AvatarTransition, TGComponent> tranSourceMap){
        // TGConnectingPoint tp = new TGConnectingPoint(null, x, y, false, false);
        //Create dummy tgcomponent
        TGComponent tgcomp = new AvatarSMDStartState(x,y,x,x*2,y,y*2,false,null,smp);
        if (asme==null){
            return;
        }
        if (asme instanceof AvatarStartState){
            AvatarSMDStartState smdss = new AvatarSMDStartState(x, y, x, x*2, y, y*2, false, null, smp);
            tgcomp = smdss;
            smp.addComponent(smdss, x, y, false, true);
            SMDMap.put(asme, smdss);
            //   tp = smdss.tgconnectingPointAtIndex(0);
            locMap.put(asme, smdss);
        }
        if (asme instanceof AvatarTransition){
            //
        }
        if (asme instanceof AvatarRandom){
            AvatarSMDRandom smdr = new AvatarSMDRandom(x, y, x, x*2, y, y*2, false, null, smp);
            smdr.setVariable(((AvatarRandom)asme).getVariable());
            smp.addComponent(smdr, x, y, false, true);
            tgcomp=smdr;
            SMDMap.put(asme, smdr);
            locMap.put(asme, smdr);
        }
        if (asme instanceof AvatarActionOnSignal){
            avatartranslator.AvatarSignal sig = ((AvatarActionOnSignal) asme).getSignal();
            if (sig.isIn()){
                AvatarSMDReceiveSignal smdrs = new AvatarSMDReceiveSignal(x, y, x, x*2, y, y*2, false, null, smp);
                tgcomp=smdrs;
                smp.addComponent(smdrs, x, y, false, true);
                //                              String name=sig.minString();
                //System.out.println("signal values" +((AvatarActionOnSignal)asme).getValues());
                String parameters="";
                if (((AvatarActionOnSignal)asme).getValues().size()>0){
                    parameters+=((AvatarActionOnSignal)asme).getValues().get(0);
                    for (int i=1; i < ((AvatarActionOnSignal)asme).getValues().size(); i++){
                        parameters=parameters+","+((AvatarActionOnSignal)asme).getValues().get(i);
                    }
                }
                String name=sig.getName()+"("+parameters+")";
                smdrs.setValue(name);
                // sig.setName(name);
                smdrs.recalculateSize();
                SMDMap.put(asme, smdrs);
                //   tp = smdrs.getFreeTGConnectingPoint(x+smdrs.getWidth()/2,y+smdrs.getHeight());
                //  TGConnectingPoint tp2 = smdrs.getFreeTGConnectingPoint(x+smdrs.getWidth()/2,y);
                locMap.put(asme, smdrs);
                if (bl.getAvatarSignalFromName(name) ==null){
                    //bl.addSignal(new ui.AvatarSignal(0, name, new String[0], new String[0]));
                }

            }
            else {
                AvatarSMDSendSignal smdss = new AvatarSMDSendSignal(x, y, x, x*2, y, y*2, false, null, smp);
                tgcomp=smdss;
                smp.addComponent(smdss, x, y, false, true);
                String parameters="";
                if (((AvatarActionOnSignal)asme).getValues().size()>0){
                    parameters+=((AvatarActionOnSignal)asme).getValues().get(0);
                    for (int i=1; i < ((AvatarActionOnSignal)asme).getValues().size(); i++){
                        parameters=parameters+","+((AvatarActionOnSignal)asme).getValues().get(i);
                    }
                }
                String name=sig.getName()+"("+parameters+")";
                //String name=sig.minString();
                smdss.setValue(name);
                smdss.recalculateSize();
                SMDMap.put(asme, smdss);
                //  tp = smdss.getFreeTGConnectingPoint(x+smdss.getWidth()/2,y+smdss.getHeight());
                //      TGConnectingPoint tp2 = smdss.getFreeTGConnectingPoint(x+smdss.getWidth()/2,y);
                locMap.put(asme, smdss);
                if (bl.getAvatarSignalFromName(name)  == null){
                    // bl.addSignal(new ui.AvatarSignal(1, name, new String[0], new String[0]));
                }
            }

        }
        if (asme instanceof AvatarStopState){
            AvatarSMDStopState smdstop = new AvatarSMDStopState(x, y, x, x*2, y, y*2, false, null, smp);
            tgcomp=smdstop;
            SMDMap.put(asme, smdstop);
            smp.addComponent(smdstop, x, y, false, true);
            //  tp = smdstop.tgconnectingPointAtIndex(0);
            locMap.put(asme, smdstop);
        }
        if (asme instanceof AvatarState){
            //check if empty checker state
            /* if (asme.getName().contains("signalstate_")){
            //don't add the state, ignore next transition,
            if (asme.getNexts().size()==1){
            AvatarStateMachineElement next = asme.getNext(0).getNext(0);
            //Reroute transition
            for (AvatarTransition at: tranDestMap.keySet()){
            if (tranDestMap.get(at) == asme){
            tranDestMap.put(at, next);
            }
            }
            addStates(next, x, y, smp,bl, SMDMap, locMap, tranDestMap, tranSourceMap);
            return;
            }
            }*/
            AvatarSMDState smdstate = new AvatarSMDState(x, y, x, x*2, y, y*2, false, null, smp);
            tgcomp=smdstate;
            smp.addComponent(smdstate, x, y, false, true);
            smdstate.setValue(asme.getName());
            smdstate.recalculateSize();
            SMDMap.put(asme, smdstate);
            //   tp = smdstate.getFreeTGConnectingPoint(x+smdstate.getWidth()/2,y+smdstate.getHeight());
            //  TGConnectingPoint tp2 = smdstate.getFreeTGConnectingPoint(x+smdstate.getWidth()/2,y);
            locMap.put(asme, smdstate);
        }
        int i=0;
        int diff=100;
        int ydiff=50;
        int num = asme.nbOfNexts();
        if (!(asme instanceof AvatarTransition)){
            for (AvatarStateMachineElement el:asme.getNexts()){
                if (!(el instanceof AvatarTransition)){
                    System.out.println("ERROR: non-Transition " + asme + " connected to non-Transition " + el);
                }
            }
        }
        for (AvatarStateMachineElement el:asme.getNexts()){
            if (el instanceof AvatarTransition){
                tranSourceMap.put((AvatarTransition) el, tgcomp);
            }
            else {
                if (asme instanceof AvatarTransition){
                    AvatarTransition t = (AvatarTransition) asme;
                    tranDestMap.put(t, el);
                }
            }
            if (!SMDMap.containsKey(el)){
                addStates(el, x+diff*(i-num/2), y+ydiff, smp, bl, SMDMap, locMap, tranDestMap, tranSourceMap);
            }
            i++;
        }
        return;
    }

    public void drawBlockProperties(AvatarBlock ab, AvatarBDBlock bl){
        for (avatartranslator.AvatarSignal sig:ab.getSignals()){
            String name=sig.getName().split("__")[sig.getName().split("__").length-1];
            //           sig.setName(name);
            String[] types = new String[sig.getListOfAttributes().size()];
            String[] typeIds = new String[sig.getListOfAttributes().size()];
            int i=0;
            for (AvatarAttribute attr: sig.getListOfAttributes()){
                types[i]=attr.getType().getStringType();
                typeIds[i]=attr.getName();
                i++;
            }
            TraceManager.addDev("Adding signal "+sig);
            bl.addSignal(new ui.AvatarSignal(sig.getInOut(), name, types, typeIds));
        }

        bl.setValueWithChange(ab.getName().split("__")[ab.getName().split("__").length-1]);

        for (AvatarAttribute attr: ab.getAttributes()){
            int type=5;
            if (attr.getType()==AvatarType.BOOLEAN){
                type=4;
            }
            if (attr.getType()==AvatarType.INTEGER){
                type=0;
            }
            if (attr.hasInitialValue()){
                bl.addAttribute(new TAttribute(0, attr.getName(), attr.getInitialValue(), type));
            }
            else {
                bl.addAttribute(new TAttribute(0, attr.getName(), attr.getType().getDefaultInitialValue(), type));
            }
            if (attr.getName().contains("key_")){
                hasCrypto=true;
                bl.addCryptoElements();
            }
        }
        for (avatartranslator.AvatarMethod method: ab.getMethods()){
            bl.addMethodIfApplicable(method.toString().replaceAll(" = 0",""));
        }
    }
    public void drawPanel(AvatarSpecification avspec, AvatarDesignPanel adp){
        //System.out.println(avspec.toString());
        hasCrypto=false;
        Map<String, Set<String>> originDestMap = new HashMap<String, Set<String>>();
        Map<String, AvatarBDBlock> blockMap = new HashMap<String, AvatarBDBlock>();
        if (adp ==null){
            return;
        }
        if (avspec==null){
            return;
        }
        AvatarBDPanel abd = adp.abdp;

        //Find all blocks, create nested blocks starting from top left
        int xpos=10;
        int ypos=40;

        //Create blocks recursively, starting from top level ones with no father
        //Lowest level blocks should be 100x100, next should be 100x(number of children*100+50)...etc,
        //Find level #, 0 refers to no father, etc
        Map<AvatarBlock, Integer> blockLevelMap = new HashMap<AvatarBlock, Integer>();
        Map<AvatarBlock, Integer> blockSizeMap = new HashMap<AvatarBlock, Integer>();
        Map<AvatarBlock, Integer> blockIncMap = new HashMap<AvatarBlock, Integer>();
        int maxLevel=0;
        for (AvatarBlock ab: avspec.getListOfBlocks()){
            int level=0;
            AvatarBlock block=ab;
            while (block.getFather()!=null){
                if (blockSizeMap.containsKey(block.getFather())){
                    blockSizeMap.put(block.getFather(), blockSizeMap.get(block.getFather())+1);
                }
                else {
                    blockSizeMap.put(block.getFather(),1);
                    blockIncMap.put(block.getFather(), 10);
                }
                level++;
                block=block.getFather();
            }
            if (level>maxLevel){
                maxLevel=level;
            }
            if (!blockSizeMap.containsKey(block)){
                blockSizeMap.put(block, 0);
                blockIncMap.put(block,10);
            }
            blockLevelMap.put(ab, level);
        }


        for (int level=0; level<maxLevel+1; level++){
            for (AvatarBlock ab:avspec.getListOfBlocks()){
                if (blockLevelMap.get(ab)==level){
                    if (level==0){
                        AvatarBDBlock bl = new AvatarBDBlock(xpos, ypos, abd.getMinX(), abd.getMaxX(), abd.getMinY(), abd.getMaxY(), false, null, abd);
                        abd.addComponent(bl, xpos, ypos, false, true);
                        bl.resize(100*blockSizeMap.get(ab)+100, 100+(maxLevel-level)*50);
                        drawBlockProperties(ab,bl);
                        AvatarSMDPanel smp = adp.getAvatarSMDPanel(bl.getValue());
                        buildStateMachine(ab, bl, smp);
                        blockMap.put(bl.getValue().split("__")[bl.getValue().split("__").length-1], bl);
                        xpos+=100*blockSizeMap.get(ab)+200;
                    }
                    else {

                        AvatarBDBlock father= blockMap.get(ab.getFather().getName().split("__")[ab.getFather().getName().split("__").length-1]);
                        //System.out.println("blockmap " + blockMap);
                        if (father==null){
                            //System.out.println("Missing father block " + ab.getFather().getName());
                            continue;
                        }
                        AvatarBDBlock bl = new AvatarBDBlock(father.getX()+blockIncMap.get(ab.getFather()), father.getY()+10, abd.getMinX(), abd.getMaxX(), abd.getMinY(), abd.getMaxY(), false, father, abd);
                        abd.addComponent(bl, father.getX()+blockIncMap.get(ab.getFather()), father.getY()+10, false, true);
                        int size=100;
                        if (blockSizeMap.containsKey(ab)){
                            size=100*blockSizeMap.get(ab)+50;
                        }
                        bl.resize(size, 100+(maxLevel-level)*50);
                        drawBlockProperties(ab,bl);
                        abd.attach(bl);
                        AvatarSMDPanel smp = adp.getAvatarSMDPanel(bl.getValue());
                        buildStateMachine(ab, bl, smp);
                        blockMap.put(bl.getValue().split("__")[bl.getValue().split("__").length-1], bl);
                        blockIncMap.put(ab.getFather(), blockIncMap.get(ab.getFather())+size+10);
                    }
                }
            }
        }


        for (AvatarRelation ar: avspec.getRelations()){
            String bl1 = ar.block1.getName();
            String bl2 = ar.block2.getName();
            if (originDestMap.containsKey(bl1.split("__")[bl1.split("__").length-1])){
                originDestMap.get(bl1.split("__")[bl1.split("__").length-1]).add(bl2.split("__")[bl2.split("__").length-1]);
            } else if (originDestMap.containsKey(bl2.split("__")[bl2.split("__").length-1])){
                originDestMap.get(bl2.split("__")[bl2.split("__").length-1]).add(bl1.split("__")[bl1.split("__").length-1]);
            } else {
                Set<String> hs= new HashSet<String>();
                hs.add(bl2.split("__")[bl2.split("__").length-1]);
                originDestMap.put(bl1.split("__")[bl1.split("__").length-1], hs);
            }
        }
        //Add Relations

        for (String bl1: originDestMap.keySet()){
            for (String bl2:originDestMap.get(bl1)){
                Vector<Point> points=new Vector<Point>();
                //      System.out.println("Finding " + bl1 + " and bl2 "+ bl2);
                if (blockMap.get(bl1)==null || blockMap.get(bl2)==null){
                    continue;
                }
                TGConnectingPoint p1= blockMap.get(bl1).findFirstFreeTGConnectingPoint(true,true);
                p1.setFree(false);

                TGConnectingPoint p2= blockMap.get(bl2).findFirstFreeTGConnectingPoint(true,true);
                p2.setFree(false);

                if (bl2.equals(bl1)){
                    //Add 2 point so the connection looks square
                    Point p = new Point(p1.getX(), p1.getY()-10);
                    points.add(p);
                    p = new Point(p2.getX(), p2.getY()-10);
                    points.add(p);
                }
                AvatarBDPortConnector conn = new AvatarBDPortConnector(0, 0, 0, 0, 0, 0, true, null, abd, p1, p2, points);
                abd.addComponent(conn, 0,0,false,true);

                //Add Relations to connector
                for (AvatarRelation ar:avspec.getRelations()){
                    if (ar.block1.getName().contains(bl1) && ar.block2.getName().contains(bl2) || ar.block1.getName().contains(bl2) && ar.block2.getName().contains(bl1)){

                        //TGConnectingPoint p1= blockMap.get(bl1).getFreeTGConnectingPoint(blockMap.get(bl1).getX(), blockMap.get(bl1).getY());

                        conn.setAsynchronous(ar.isAsynchronous());
                        conn.setBlocking(ar.isBlocking());
                        conn.setPrivate(ar.isPrivate());
                        conn.setSizeOfFIFO(ar.getSizeOfFIFO());
                        //System.out.println(bl1 +" "+ ar.block1.getName() + " "+ ar.block2.getName());
                        for (int i =0; i< ar.nbOfSignals(); i++){
                            //System.out.println("Adding relation " + ar.getSignal1(i).toString() + " " + ar.getSignal2(i).toBasicString());
                            conn.addSignal(ar.getSignal1(i).toString(),ar.getSignal1(i).getInOut()==0,ar.block1.getName().contains(bl1));
                            conn.addSignal(ar.getSignal2(i).toString(), ar.getSignal2(i).getInOut()==0,!ar.block1.getName().contains(bl1));
                            //  System.out.println("adding signal " +ar.getSignal1(i).toBasicString());
                        }
                        //System.out.println("Added Signals");
                        conn.updateAllSignals();


                    }
                    conn.updateAllSignals();
                }

                /*for (ui.AvatarSignal sig:blockMap.get(bl1).getSignalList()){
                  for (ui.AvatarSignal sig2: blockMap.get(bl2).getSignalList()){
                  if (sig.getId().equals(sig2.getId())){
                  conn.addSignal("in "+sig.getId(), true, true);
                  conn.addSignal("out "+sig.getId(), false, false);
                  }
                  }
                  }*/
            }
        }
        ypos+=100;
        //Add Pragmas
        AvatarBDPragma pragma=new AvatarBDPragma(xpos, ypos, xpos, xpos*2, ypos, ypos*2, false, null,abd);
        //  String[] arr = new String[avspec.getPragmas().size()];
        String s="";
        // int i=0;
        for (AvatarPragma p: avspec.getPragmas()){

            //    arr[i] = p.getName();
            String t= "";
            String[] split = p.getName().split(" ");
            if (p.getName().contains("#Confidentiality")){
                for (String str:split){
                    if (str.contains(".")){
                        String tmp = str.split("\\.")[0];
                        String tmp2 = str.split("\\.")[1];
                        t=t.concat(tmp.split("__")[tmp.split("__").length-1] + "." + tmp2.split("__")[tmp2.split("__").length-1] + " ");
                    }
                    else {
                        t=t.concat(str+" ");
                    }
                }
            }
            else if (p.getName().contains("Authenticity")){
                t=p.getName();
            }
            else if (p.getName().contains("Initial")){
                t=p.getName();
            }
            else {
                t=p.getName();
            }
            s=s.concat(t+"\n");
            //  i++;
        }
        pragma.setValue(s);
        pragma.makeValue();
        abd.addComponent(pragma, xpos, ypos, false,true);
        //Add message and key datatype if there is a cryptoblock

        xpos=50;
        ypos+=200;
        if (hasCrypto){
            AvatarBDDataType message = new AvatarBDDataType(xpos, ypos, xpos, xpos*2, ypos, ypos*2, false, null,abd);
            message.setValue("Message");

            abd.addComponent(message, xpos, ypos, false,true);
            message.resize(200,100);
            xpos+=400;

            AvatarBDDataType key = new AvatarBDDataType(xpos, ypos, xpos, xpos*2, ypos, ypos*2, false, null,abd);
            key.setValue("Key");
            TAttribute attr = new TAttribute(2, "data", "0", 8);
            message.addAttribute(attr);
            key.addAttribute(attr);
            key.resize(200,100);
            abd.addComponent(key, xpos, ypos, false,true);
        }
    }

    public void buildStateMachine(AvatarBlock ab, AvatarBDBlock bl, AvatarSMDPanel smp){
        Map<AvatarTransition, TGComponent> tranSourceMap = new HashMap<AvatarTransition, TGComponent>();
        Map<AvatarTransition, AvatarStateMachineElement> tranDestMap = new HashMap<AvatarTransition, AvatarStateMachineElement>();
        Map<AvatarStateMachineElement, TGComponent> locMap = new HashMap<AvatarStateMachineElement, TGComponent>();
        Map<AvatarStateMachineElement, TGComponent> SMDMap = new HashMap<AvatarStateMachineElement, TGComponent>();

        //Build the state machine
        int smx=400;
        int smy=40;

        if (smp==null){
            System.out.println("can't find");
            return;
        }
        smp.removeAll();
        AvatarStateMachine asm = ab.getStateMachine();
        //Remove the empty check states

        AvatarStartState start= asm.getStartState();
        addStates(start, smx, smy, smp, bl, SMDMap, locMap, tranDestMap, tranSourceMap);
        //Add transitions
        for (AvatarTransition t: tranSourceMap.keySet()){
            if (tranSourceMap.get(t)==null || tranDestMap.get(t)==null){
                continue;
            }
            int x=tranSourceMap.get(t).getX()+tranSourceMap.get(t).getWidth()/2;
            int y=tranSourceMap.get(t).getY()+tranSourceMap.get(t).getHeight();

            //    TGConnectingPoint p1 = tranSourceMap.get(t).findFirstFreeTGConnectingPoint(true,false);
            TGConnectingPoint p1 = tranSourceMap.get(t).closerFreeTGConnectingPoint(x, y, true, false);
            if (p1==null){
                //  p1= tranSourceMap.get(t).findFirstFreeTGConnectingPoint(true,true);
                p1=tranSourceMap.get(t).closerFreeTGConnectingPoint(x,y,true, true);
            }
            x= locMap.get(tranDestMap.get(t)).getX()+ locMap.get(tranDestMap.get(t)).getWidth()/2;
            y = locMap.get(tranDestMap.get(t)).getY();
            if (tranSourceMap.get(t).getY() > locMap.get(tranDestMap.get(t)).getY()){
                y=locMap.get(tranDestMap.get(t)).getY()+locMap.get(tranDestMap.get(t)).getHeight()/2;
                if (tranSourceMap.get(t).getX() < locMap.get(tranDestMap.get(t)).getX()){
                    x = locMap.get(tranDestMap.get(t)).getX();
                }
                else {
                    x= locMap.get(tranDestMap.get(t)).getX()+locMap.get(tranDestMap.get(t)).getWidth();
                }
            }
            TGConnectingPoint p2 = locMap.get(tranDestMap.get(t)).closerFreeTGConnectingPoint(x,y,false, true);
            if (p2==null){
                p2=locMap.get(tranDestMap.get(t)).closerFreeTGConnectingPoint(x,y,true, true);
            }
            Vector<Point> points = new Vector<Point>();
            if (p1==null || p2 ==null){
                System.out.println(tranSourceMap.get(t)+" "+locMap.get(tranDestMap.get(t)));

                System.out.println("Missing point "+ p1 + " "+p2);
                return;
            }
            AvatarSMDConnector SMDcon = new AvatarSMDConnector(p1.getX(), p1.getY(), p1.getX(), p1.getY(), p1.getX(), p1.getY(), true, null, smp, p1, p2, points);
            //System.out.println(tranSourceMap.get(t)+" "+locMap.get(tranDestMap.get(t)));
            ///System.out.println("FREE " +p1.isFree() + " "+ p2.isFree());
            p1.setFree(false);
            p2.setFree(false);
            String action="";
            if (t.getActions().size()==0){
                action="";
            }
            else {
                action=t.getActions().get(0).toString().replaceAll(" ","");
            }
            SMDcon.setTransitionInfo(t.getGuard().toString(), action);
            for (int i=1; i<t.getActions().size(); i++){
                SMDcon.setTransitionInfo("", t.getActions().get(i).toString().replaceAll(" ",""));
            }
            smp.addComponent(SMDcon, p1.getX(), p1.getY(), false, true);
        }
    }

    // Generates for all observers, a TURTLE modeling for checking it
    public boolean generateTMsForRequirementAnalysis(Vector<Requirement> reqs, RequirementDiagramPanel rdp) {
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
            ce = checkingErrors.get(i);
            if (ce != null && ce instanceof UICheckingError) {
                tgc = ((UICheckingError) ce).getTGComponent();
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
