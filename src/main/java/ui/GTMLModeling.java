/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici, Matteo Bertolino
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
 * matteo.bertolino AT telecom-paristech.fr
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

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import avatartranslator.AvatarSpecification;
import myutil.TraceManager;
import tmltranslator.HwA;
import tmltranslator.HwBridge;
import tmltranslator.HwRouter;
import tmltranslator.HwBus;
import tmltranslator.HwCPU;
import tmltranslator.HwCommunicationNode;
import tmltranslator.HwCrossbar;
import tmltranslator.HwDMA;
import tmltranslator.HwExecutionNode;
import tmltranslator.HwFPGA;
import tmltranslator.HwLink;
import tmltranslator.HwMemory;
import tmltranslator.HwNode;
import tmltranslator.HwVGMN;
import tmltranslator.SecurityPattern;
import tmltranslator.TMLActivity;
import tmltranslator.TMLActivityElement;
import tmltranslator.TMLActivityElementChannel;
import tmltranslator.TMLArchitecture;
import tmltranslator.TMLAttribute;
import tmltranslator.TMLCP;
import tmltranslator.TMLCPError;
import tmltranslator.TMLCPLib;
import tmltranslator.TMLCPLibArtifact;
import tmltranslator.TMLCPSyntaxChecking;
import tmltranslator.TMLChannel;
import tmltranslator.TMLCheckingError;
import tmltranslator.TMLElement;
import tmltranslator.TMLError;
import tmltranslator.TMLEvent;
import tmltranslator.TMLExecI;
import tmltranslator.TMLMapping;
import tmltranslator.TMLModeling;
import tmltranslator.TMLPort;
import tmltranslator.TMLRequest;
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.TMLTask;
import tmltranslator.TMLType;
import tmltranslator.modelcompiler.ArchUnitMEC;
import tmltranslator.tmlcp.TMLCPElement;
import tmltranslator.tmlcp.TMLSDAction;
import tmltranslator.tmlcp.TMLSDEvent;
import tmltranslator.tmlcp.TMLSDMessage;
import translator.CheckingError;
import ui.tmlad.TMLADEncrypt;
import ui.tmlad.TMLADReadChannel;
import ui.tmlad.TMLADWriteChannel;
import ui.tmlad.TMLActivityDiagramPanel;
import ui.tmlcd.TMLChannelOperator;
import ui.tmlcd.TMLEventOperator;
import ui.tmlcd.TMLRequestOperator;
import ui.tmlcd.TMLTaskOperator;
import ui.tmlcompd.TMLCChannelOutPort;
import ui.tmlcompd.TMLCFork;
import ui.tmlcompd.TMLCJoin;
import ui.tmlcompd.TMLCPath;
import ui.tmlcompd.TMLCPortConnector;
import ui.tmlcompd.TMLCPrimitiveComponent;
import ui.tmlcompd.TMLCPrimitivePort;
import ui.tmlcompd.TMLCRecordComponent;
import ui.tmldd.TMLArchiArtifact;
import ui.tmldd.TMLArchiBUSNode;
import ui.tmldd.TMLArchiBridgeNode;
import ui.tmldd.TMLArchiRouterNode;
import ui.tmldd.TMLArchiCPNode;
import ui.tmldd.TMLArchiCPUNode;
import ui.tmldd.TMLArchiCommunicationArtifact;
import ui.tmldd.TMLArchiCommunicationNode;
import ui.tmldd.TMLArchiConnectorNode;
import ui.tmldd.TMLArchiCrossbarNode;
import ui.tmldd.TMLArchiDMANode;
import ui.tmldd.TMLArchiEventArtifact;
import ui.tmldd.TMLArchiFPGANode;
import ui.tmldd.TMLArchiFirewallNode;
import ui.tmldd.TMLArchiHWANode;
import ui.tmldd.TMLArchiKey;
import ui.tmldd.TMLArchiMemoryNode;
import ui.tmldd.TMLArchiNode;
import ui.tmldd.TMLArchiPortArtifact;
import ui.tmldd.TMLArchiVGMNNode;
import ui.tmlsd.TGConnectorMessageTMLSD;
import ui.tmlsd.TMLSDControllerInstance;
import ui.tmlsd.TMLSDStorageInstance;
import ui.tmlsd.TMLSDTransferInstance;

/**
 * Class GTMLModeling
 * Use to translate graphical TML modeling to  "tmlmodeling"
 * Creation: 23/11/2005
 *
 * @author Ludovic APVRILLE, Andrea ENRICI, Matteo Bertolino
 * @version 1.2 07/02/2018
 */
public class GTMLModeling {
    private TMLDesignPanel tmldp;
    private TMLComponentDesignPanel tmlcdp;
    private TMLArchiPanel tmlap;
    private TMLModeling<TGComponent> tmlm;
    private List<CheckingError> checkingErrors, warnings;
    private List<? extends TGComponent> tasksToTakeIntoAccount;
    private List<? extends TGComponent> componentsToTakeIntoAccount;
    private List<? extends TGComponent> components;
    private List<String> removedChannels, removedRequests, removedEvents;
    private static CorrespondanceTGElement listE;
    private Map<String, String> table;
    public AvatarSpecification avspec;
    //private ArrayList<HwNode> nodesToTakeIntoAccount;
    private List<TGComponent> nodesToTakeIntoAccount;

    private TMLMapping<TGComponent> map;
    private TMLArchitecture archi;

    //Attributes specific to Communication Patterns
    private TMLCP tmlcp;
    private TMLCommunicationPatternPanel tmlcpp;
    //   private Vector<TDiagramPanel> diagramPanelsToTakeIntoAccount;
    //    private Vector<TDiagramPanel> panels;

    private Map<String, SecurityPattern> securityPatterns = new HashMap<String, SecurityPattern>();

    private boolean putPrefixName = false;

    public GTMLModeling(TMLDesignPanel _tmldp, boolean resetList) {
        tmldp = _tmldp;
        table = new Hashtable<String, String>();
        if (resetList) {
            listE = new CorrespondanceTGElement();
        }
    }

    public GTMLModeling(TMLComponentDesignPanel _tmlcdp, boolean resetList) {
        tmlcdp = _tmlcdp;
        table = new Hashtable<String, String>();
        if (resetList) {
            listE = new CorrespondanceTGElement();
        }
    }

    public GTMLModeling(TMLArchiPanel _tmlap, boolean resetList) {
        tmlap = _tmlap;
        //
        //TURTLEPanel tup = (TURTLEPanel)(tmlap.getMainGUI().getTURTLEPanel(namePanel));
        //if (tup instanceof TMLDesignPanel) {
        //tmldp = tmlap.getMainGUI().
        table = new Hashtable<String, String>();
        if (resetList) {
            listE = new CorrespondanceTGElement();
        }
    }

    public GTMLModeling(TMLCommunicationPatternPanel _tmlcpp, boolean resetList) {
        tmlcpp = _tmlcpp;
        table = new Hashtable<String, String>();
        if (resetList) {
            listE = new CorrespondanceTGElement();
        }
    }

    public TMLModeling<TGComponent> translateToTMLModeling(boolean _resetID) {
        return translateToTMLModeling(false, _resetID);
    }

    public void putPrefixName(boolean _b) {
        putPrefixName = _b;
    }

    public void processAttacker() {
        //
        //
        if (tmlm == null) {
            return;
        }
        List<TMLTask> attackers = tmlm.getAttackerTasks();
        for (TMLTask attacker : attackers) {
            //
            TMLCPrimitiveComponent atcomp = tmlcdp.getPrimitiveComponentByName(attacker.getName().split("__")[1]);
            //
            if (atcomp != null) {
                //Find activity diagram
                TMLActivityDiagramPanel tadp = tmlcdp.getTMLActivityDiagramPanel(attacker.getName().split("__")[1]);
                List<TGComponent> list = tadp.getComponentList();
                //
                for (TGComponent tgc : list) {
                    if (tgc instanceof TMLADWriteChannel) {
                        TMLADWriteChannel wr = (TMLADWriteChannel) tgc;
                        if (wr.isAttacker()) {
                            //
                            String channelToAdd = wr.getChannelName();


                            //Find ports to attach
                            List<TMLCPrimitivePort> ports = tmlcdp.tmlctdp.getPortsByName(channelToAdd);
                            //
                            if (ports.size() != 2) {
                                //throw error
                                //
                                continue;
                            }

                            //Remove Port Connector
                            tmlcdp.tmlctdp.removeOneConnector(ports.get(0).getTGConnectingPointAtIndex(0));


                            //Add write port to attacker component
                            TMLCChannelOutPort originPort = new TMLCChannelOutPort(atcomp.getX(), atcomp.getY(), tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, atcomp, tmlcdp.tmlctdp);
                            originPort.commName = channelToAdd;
                            tmlcdp.tmlctdp.addComponent(originPort, atcomp.getX(), atcomp.getY(), true, true);

                            //Add fork/join to Component Diagram
                            TMLCJoin join = new TMLCJoin(atcomp.getX(), atcomp.getY(), tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp);
                            tmlcdp.tmlctdp.addComponent(join, atcomp.getX(), atcomp.getY(), false, true);

                            //Add 3 connectors, from each port to the join
                            TMLCPortConnector conn = new TMLCPortConnector(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp, originPort.getTGConnectingPointAtIndex(0), join.getTGConnectingPointAtIndex(1), new Vector<Point>());
                            tmlcdp.tmlctdp.addComponent(conn, 0, 0, false, true);
                            if (!ports.get(0).isOrigin()) {
                                conn = new TMLCPortConnector(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp, ports.get(0).getTGConnectingPointAtIndex(0), join.getTGConnectingPointAtIndex(0), new Vector<Point>());
                                tmlcdp.tmlctdp.addComponent(conn, 0, 0, false, true);
                                conn = new TMLCPortConnector(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp, ports.get(1).getTGConnectingPointAtIndex(0), join.getTGConnectingPointAtIndex(6), new Vector<Point>());
                                tmlcdp.tmlctdp.addComponent(conn, 0, 0, false, true);
                            } else {
                                conn = new TMLCPortConnector(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp, ports.get(0).getTGConnectingPointAtIndex(0), join.getTGConnectingPointAtIndex(6), new Vector<Point>());
                                tmlcdp.tmlctdp.addComponent(conn, 0, 0, false, true);
                                conn = new TMLCPortConnector(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp, ports.get(1).getTGConnectingPointAtIndex(0), join.getTGConnectingPointAtIndex(0), new Vector<Point>());
                                tmlcdp.tmlctdp.addComponent(conn, 0, 0, false, true);
                            }
                        }
                    } else if (tgc instanceof TMLADReadChannel) {
                        TMLADReadChannel rd = (TMLADReadChannel) tgc;
                        if (rd.isAttacker()) {
                            //
                            String channelToAdd = rd.getChannelName();


                            //Find ports to attach
                            List<TMLCPrimitivePort> ports = tmlcdp.tmlctdp.getPortsByName(channelToAdd);
                            //
                            if (ports.size() != 2) {
                                //throw error
                                //
                                continue;
                            }

                            //Remove Port Connector
                            tmlcdp.tmlctdp.removeOneConnector(ports.get(0).getTGConnectingPointAtIndex(0));


                            //Add write port to attacker component
                            TMLCChannelOutPort destPort = new TMLCChannelOutPort(atcomp.getX(), atcomp.getY(), tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, atcomp, tmlcdp.tmlctdp);
                            destPort.commName = channelToAdd;
                            destPort.isOrigin = false;
                            tmlcdp.tmlctdp.addComponent(destPort, atcomp.getX(), atcomp.getY(), true, true);

                            //Add fork/join to Component Diagram
                            TMLCFork fork = new TMLCFork(atcomp.getX(), atcomp.getY(), tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp);
                            tmlcdp.tmlctdp.addComponent(fork, atcomp.getX(), atcomp.getY(), false, true);

                            //Add 3 connectors, from each port to the join
                            TMLCPortConnector conn = new TMLCPortConnector(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp, destPort.getTGConnectingPointAtIndex(0), fork.getTGConnectingPointAtIndex(1), new Vector<Point>());
                            tmlcdp.tmlctdp.addComponent(conn, 0, 0, false, true);
                            if (ports.get(0).isOrigin()) {
                                conn = new TMLCPortConnector(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp, ports.get(0).getTGConnectingPointAtIndex(0), fork.getTGConnectingPointAtIndex(0), new Vector<Point>());
                                tmlcdp.tmlctdp.addComponent(conn, 0, 0, false, true);
                                conn = new TMLCPortConnector(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp, ports.get(1).getTGConnectingPointAtIndex(0), fork.getTGConnectingPointAtIndex(6), new Vector<Point>());
                                tmlcdp.tmlctdp.addComponent(conn, 0, 0, false, true);
                            } else {
                                conn = new TMLCPortConnector(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp, ports.get(0).getTGConnectingPointAtIndex(0), fork.getTGConnectingPointAtIndex(6), new Vector<Point>());
                                tmlcdp.tmlctdp.addComponent(conn, 0, 0, false, true);
                                conn = new TMLCPortConnector(0, 0, tmlcdp.tmlctdp.getMinX(), tmlcdp.tmlctdp.getMaxX(), tmlcdp.tmlctdp.getMinY(), tmlcdp.tmlctdp.getMaxX(), true, null, tmlcdp.tmlctdp, ports.get(1).getTGConnectingPointAtIndex(0), fork.getTGConnectingPointAtIndex(0), new Vector<Point>());
                                tmlcdp.tmlctdp.addComponent(conn, 0, 0, false, true);
                            }

                        }
                    }
                }
            }
        }
    }

    public TMLModeling<TGComponent> translateToTMLModeling(boolean onlyTakenIntoAccount, boolean _resetID) {
        tmlm = new TMLModeling<>(_resetID);
        checkingErrors = new LinkedList<CheckingError>();
        warnings = new LinkedList<CheckingError>();

        //boolean b;

        if (tmldp != null) {
            components = tmldp.tmltdp.getComponentList();
            if (tasksToTakeIntoAccount == null) {
                tasksToTakeIntoAccount = components;
            }
            removedChannels = new LinkedList<String>();
            removedRequests = new LinkedList<String>();
            removedEvents = new LinkedList<String>();

            try {


                addTMLTasks();
                addTMLChannels();
                addTMLEvents();
                addTMLRequests();
                //addTMLPragmas();
                //TraceManager.addDev("At line 151");
                generateTasksActivityDiagrams();
                removeActionsWithDollars();
                removeActionsWithRecords();
            } catch (MalformedTMLDesignException mtmlde) {
                TraceManager.addDev("Modeling error:" + mtmlde.getMessage());
            }

            /*TMLTextSpecification spec = new TMLTextSpecification();
              spec.toTextFormat(tmlm);
              TraceManager.addDev("TMLModeling=\n" + spec.toString());*/

            // Cheking syntax
            //TraceManager.addDev("Checking syntax 1 of TML");
            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmlm);
            syntax.checkSyntax();

            int type;
            TGComponent tgc;

            if (syntax.hasErrors() > 0) {
                for (TMLError error : syntax.getErrors()) {
                    //TraceManager.addDev("Adding checking error");
                    if (error.type == TMLError.ERROR_STRUCTURE) {
                        type = CheckingError.STRUCTURE_ERROR;
                    } else {
                        type = CheckingError.BEHAVIOR_ERROR;
                    }

                    tgc = listE.getTG(error.element);
                    if (tgc != null) {
                        UICheckingError ce = new UICheckingError(type, error.message);
                        ce.setTDiagramPanel(tgc.getTDiagramPanel());
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    } else {
                        TMLCheckingError ce = new TMLCheckingError(type, error.message);
                        ce.setTMLTask(error.task);
                        checkingErrors.add(ce);
                    }
                }
            }
        } else if (tmlcdp != null) {
            if (onlyTakenIntoAccount) {
                components = componentsToTakeIntoAccount;
            } else {
                components = tmlcdp.tmlctdp.getPrimitiveComponentList();
                if (componentsToTakeIntoAccount == null) {
                    componentsToTakeIntoAccount = components;
                }
            }

            removedChannels = new LinkedList<String>();
            removedRequests = new LinkedList<String>();
            removedEvents = new LinkedList<String>();

            try {

                // Checking paths
                if (tmlcdp != null) {
                    if (tmlcdp.tmlctdp != null) {
                        List<TMLCPath> faultyPaths = tmlcdp.tmlctdp.updatePorts();
                        for (TMLCPath fp : faultyPaths) {
                            if (fp != null) {
                                // There is a faulty path
                                // Create an error
                                UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, fp.getErrorMessage());
                                ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                ce.setTGComponent(fp.getFaultyComponent());
                                checkingErrors.add(ce);
                                //throw new MalformedTMLDesignException("Bad path:" + path.getErrorMessage());
                            }
                        }
                    }
                }

                addTMLComponents();
                //Adapt for attacker
                //TraceManager.addDev("Processing attacker");
                processAttacker();
                //TraceManager.addDev("Adding channels");
                addTMLCChannels();
                //TraceManager.addDev("Adding events");
                addTMLCEvents();
                //TraceManager.addDev("Adding requests");
                addTMLCRequests();
                //addTMLPragmas();
                //TraceManager.addDev("At line 211");
                generateTasksActivityDiagrams();
                removeActionsWithDollars();
                removeActionsWithRecords();
            } catch (MalformedTMLDesignException mtmlde) {
                TraceManager.addDev("Modeling error:" + mtmlde.getMessage());
            }

            //TraceManager.addDev("Checking syntax 2 of TML");
            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmlm);
            syntax.checkSyntax();

            int type;
            TGComponent tgc;

            if (syntax.hasErrors() > 0) {
                for (TMLError error : syntax.getErrors()) {
                    //TraceManager.addDev("Adding checking error");
                    if (error.type == TMLError.ERROR_STRUCTURE) {
                        type = CheckingError.STRUCTURE_ERROR;
                    } else {
                        type = CheckingError.BEHAVIOR_ERROR;
                    }
                    tgc = listE.getTG(error.element);
                    if (tgc != null) {
                        UICheckingError ce = new UICheckingError(type, error.message);
                        ce.setTDiagramPanel(tgc.getTDiagramPanel());
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    } else {
                        TMLCheckingError ce = new TMLCheckingError(type, error.message);
                        ce.setTMLTask(error.task);
                        checkingErrors.add(ce);
                    }
                }
            }
        }

        return tmlm;
    }

    public TMLDesignPanel getTMLDesignPanel() {
        return tmldp;
    }

    public CorrespondanceTGElement getCorrespondanceTable() {
        return listE;
    }

    public void setTasks(Vector<? extends TGComponent> tasks) {
        tasksToTakeIntoAccount = new LinkedList<TGComponent>(tasks);
    }

    public void setComponents(Vector<? extends TGComponent> components) {
        componentsToTakeIntoAccount = new LinkedList<TGComponent>(components);
    }

    public void setNodes(Vector<TGComponent> nodes) {
        nodesToTakeIntoAccount = new LinkedList<TGComponent>(nodes);
    }

    //    public void setDiagramPanels( Vector<TDiagramPanel> panels ) {
    //        diagramPanelsToTakeIntoAccount = new Vector<TDiagramPanel>( panels );
    //    }

    public List<CheckingError> getCheckingErrors() {
        return checkingErrors;
    }

    public List<CheckingError> getCheckingWarnings() {
        return warnings;
    }

    //
    //    private void addTMLPragmas(){
    //  TGComponent tgc;
    //  components = tmlap.tmlap.getComponentList();
    //  ListIterator iterator = components.listIterator();
    //  while(iterator.hasNext()) {
    //      tgc = (TGComponent)(iterator.next());
    //      if (tgc instanceof TGCNote){
    //          TGCNote note = (TGCNote) tgc;
    //          String[] vals = note.getValues();
    //          for (String s: vals){
    //              TraceManager.addDev("Val " + s);
    //              if (s.contains("#") && s.contains(" ")){
    //                          map.addPragma(s.split(" "));
    //              }
    //          }
    //      }
    //        }
    //    }
    private void addTMLTasks() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLTask tmlt;
        TMLTaskOperator tmlto;
        TMLActivityDiagramPanel tmladp;

        Iterator<? extends TGComponent> iterator = tasksToTakeIntoAccount.listIterator();
        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLTaskOperator) {
                tmlto = (TMLTaskOperator) tgc;
                tmladp = tmldp.getTMLActivityDiagramPanel(tmlto.getValue());
                if (tmladp == null) {
                    String msg = tmlto.getValue() + " has no activity diagram";
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                    ce.setTDiagramPanel(tmldp.tmltdp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                    throw new MalformedTMLDesignException(tmlto.getValue() + " msg");
                }

                if (tmlm.getTMLTaskByName(tmlto.getValue()) != null) {
                    String msg = "Two tasks have the same name: " + tmlto.getValue() + " (mapping problem?)";
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                    ce.setTDiagramPanel(tmldp.tmltdp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                    throw new MalformedTMLDesignException(tmlto.getValue() + " msg");
                }

                tmlt = new TMLTask(makeName(tgc, tmlto.getValue()), tmlto, tmladp);
                listE.addCor(tmlt, tgc);
                tmlm.addTask(tmlt);
                tmlt.setExit(tmlto.isExit());
                addAttributesTo(tmlt, tmlto);
            }
        }
    }

    private void addTMLComponents() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLCPrimitiveComponent tmlcpc;
        TMLActivityDiagramPanel tmladp;
        TMLTask tmlt;
        TMLComponentDesignPanel tmlcdptmp;

        Iterator<? extends TGComponent> iterator = componentsToTakeIntoAccount.listIterator();
        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLCPrimitiveComponent) {
                tmlcpc = (TMLCPrimitiveComponent) tgc;
                tmlcdptmp = (TMLComponentDesignPanel) (tmlcpc.getTDiagramPanel().getMGUI().getTURTLEPanelOfTDiagramPanel(tmlcpc.getTDiagramPanel()));
                tmladp = tmlcdptmp.getReferencedTMLActivityDiagramPanel(tmlcpc.getTDiagramPanel(), tmlcpc.getValue());
                if (tmladp == null) {
                    String msg = " has no activity diagram";
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                    throw new MalformedTMLDesignException(tmlcpc.getValue() + " msg");
                }
                if (tmlm.getTMLTaskByName(tmlcpc.getValue()) != null) {
                    String msg = "Two components have the same name: " + tmlcpc.getValue() + " (mapping problem?)";
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                    throw new MalformedTMLDesignException(tmlcpc.getValue() + " msg");
                }
                tmlt = new TMLTask(makeName(tgc, tmlcpc.getValue()), tmlcpc, tmladp);
                tmlt.setAttacker(tmlcpc.isAttacker());
                //TraceManager.addDev("Task added:" + tmlt.getName() + " with tadp=" + tmladp + " major=" + tmladp.getMGUI().getMajorTitle(tmladp));
                listE.addCor(tmlt, tgc);
                tmlm.addTask(tmlt);
                tmlt.setExit(false);
                //tmlt.setExit(tmlcpc.isExit());
                addAttributesTo(tmlt, tmlcpc);
            }
        }
    }

    private void addTMLChannels() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLChannelOperator tmlco;
        Iterator<? extends TGComponent> iterator = components.listIterator();
        TMLTaskInterface t1, t2;
        TMLChannel channel;
        TMLTask tt1, tt2;
        String name;

        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLChannelOperator) {
                tmlco = (TMLChannelOperator) tgc;
                //TraceManager.addDev("Found channel: " + tmlco.getChannelName());
                t1 = tmldp.tmltdp.getTask1ToWhichIamConnected(tmlco);
                t2 = tmldp.tmltdp.getTask2ToWhichIamConnected(tmlco);
                if ((t1 != null) && (t2 != null) && (tasksToTakeIntoAccount.contains(t1)) && (tasksToTakeIntoAccount.contains(t2))) {
                    name = makeName(tgc, tmlco.getChannelName());
                    channel = new TMLChannel(name, tmlco);
                    //TraceManager.addDev("name=" + name + " makeName1 =" + makeName(tgc, t1.getTaskName())  + " chname=" + tmlco.getChannelName());
                    addToTable(makeName(tgc, t1.getTaskName()) + "/" + tmlco.getChannelName(), name);
                    addToTable(makeName(tgc, t2.getTaskName()) + "/" + tmlco.getChannelName(), name);
                    channel.setSize(tmlco.getChannelSize());
                    channel.setType(tmlco.getChannelType());
                    channel.setMax(tmlco.getChannelMax());
                    if (tmlm.hasSameChannelName(channel)) {
                        if (tmlm.hasAlmostSimilarChannel(channel)) {
                            String msg = " channel " + tmlco.getChannelName() + " is declared several times differently";
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmldp.tmltdp);
                            ce.setTGComponent(tgc);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(tmlco.getChannelName() + " msg");
                        }
                    } else {
                        tt1 = tmlm.getTMLTaskByName(makeName((TGComponent) t1, t1.getTaskName()));
                        tt2 = tmlm.getTMLTaskByName(makeName((TGComponent) t2, t2.getTaskName()));
                        channel.setTasks(tt1, tt2);
                        tmlm.addChannel(channel);
                        listE.addCor(channel, tgc);
                        //TraceManager.addDev("Adding channel " + channel.getName());
                    }
                } else {
                    removedChannels.add(new String(tmlco.getChannelName()));
                    UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Channel " + tmlco.getChannelName() + " has been removed");
                    ce.setTDiagramPanel(tmldp.tmltdp);
                    ce.setTGComponent(tmlco);
                    warnings.add(ce);
                }
            }
        }
    }

    private void addTMLEvents() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLEventOperator tmleo;
        Iterator<? extends TGComponent> iterator = components.listIterator();
        TMLTaskInterface t1, t2;
        TMLEvent event;
        TType tt;
        TMLType tmlt;
        TMLTask tt1, tt2;
        String name;

        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLEventOperator) {
                tmleo = (TMLEventOperator) tgc;
                //TraceManager.addDev("Found event: " + tmleo.getEventName());
                t1 = tmldp.tmltdp.getTask1ToWhichIamConnected(tmleo);
                t2 = tmldp.tmltdp.getTask2ToWhichIamConnected(tmleo);
                if ((t1 != null) && (t2 != null) && (tasksToTakeIntoAccount.contains(t1)) && (tasksToTakeIntoAccount.contains(t2))) {
                    name = makeName(tgc, tmleo.getEventName());
                    event = new TMLEvent(name, tmleo, tmleo.getMaxSamples(), tmleo.isBlocking());
                    addToTable(makeName(tgc, t1.getTaskName()) + "/" + tmleo.getEventName(), name);
                    addToTable(makeName(tgc, t2.getTaskName()) + "/" + tmleo.getEventName(), name);
                    for (int i = 0; i < tmleo.getEventMaxParam(); i++) {
                        tt = tmleo.getParamAt(i);
                        if ((tt != null) && (tt.getType() != TType.NONE)) {
                            tmlt = new TMLType(tt.getType());
                            event.addParam(tmlt);
                            //TraceManager.addDev("Event " + event.getName() + " add param");
                        }
                    }
                    if (tmlm.hasSameEventName(event)) {
                        if (tmlm.hasAlmostSimilarEvent(event)) {
                            String msg = " event " + tmleo.getEventName() + " is declared several times differently";
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmldp.tmltdp);
                            ce.setTGComponent(tgc);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(tmleo.getEventName() + " msg");
                        }
                    } else {
                        tt1 = tmlm.getTMLTaskByName(makeName((TGComponent) t1, t1.getTaskName()));
                        tt2 = tmlm.getTMLTaskByName(makeName((TGComponent) t2, t2.getTaskName()));
                        event.setTasks(tt1, tt2);
                        tmlm.addEvent(event);
                        listE.addCor(event, tgc);
                        //TraceManager.addDev("Adding event " + event.getName());
                    }
                } else {
                    //TraceManager.addDev( "Removing event "+ new String(tmleo.getEventName()) );
                    removedEvents.add(new String(tmleo.getEventName()));
                    UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Event " + tmleo.getEventName() + " has been removed");
                    ce.setTDiagramPanel(tmldp.tmltdp);
                    ce.setTGComponent(tmleo);
                    warnings.add(ce);
                }
            }
        }
    }

    private void addTMLRequests() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLRequestOperator tmlro;
        Iterator<? extends TGComponent> iterator = components.listIterator();
        TMLTaskInterface t1, t2;
        TMLRequest request;
        TType tt;
        TMLType tmlt;
        TMLTask task;
        TMLAttribute tmlattr;
        TMLType tmltt;
        String name;

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLRequestOperator) {
                tmlro = (TMLRequestOperator) tgc;
                //TraceManager.addDev("Found request: " + tmlro.getRequestName());
                t1 = tmldp.tmltdp.getTask1ToWhichIamConnected(tmlro);
                t2 = tmldp.tmltdp.getTask2ToWhichIamConnected(tmlro);
                if ((t1 != null) && (t2 != null) && (tasksToTakeIntoAccount.contains(t1)) && (tasksToTakeIntoAccount.contains(t2))) {
                    name = makeName(tgc, tmlro.getRequestName());
                    addToTable(makeName(tgc, t1.getTaskName()) + "/" + tmlro.getRequestName(), name);
                    addToTable(makeName(tgc, t2.getTaskName()) + "/" + tmlro.getRequestName(), name);
                    // Check whether there is another request having a different name but with the same destination task
                    request = tmlm.getRequestByDestinationTask(tmlm.getTMLTaskByName(makeName((TGComponent) t2, t2.getTaskName())));

                    if (request != null) {
                        if (request.getName().compareTo(name) != 0) {
                            String msg = "Two requests declared with different names have the same destination task: " + tmlro.getRequestName();
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmldp.tmltdp);
                            ce.setTGComponent(tgc);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(tmlro.getRequestName() + " msg");
                        }
                    }

                    request = tmlm.getRequestNamed(name);
                    if (request == null) {
                        request = new TMLRequest(name, tmlro);

                        request.setDestinationTask(tmlm.getTMLTaskByName(makeName((TGComponent) t2, t2.getTaskName())));
                        tmlm.addRequest(request);
                        for (int i = 0; i < tmlro.getRequestMaxParam(); i++) {
                            tt = tmlro.getParamAt(i);
                            if ((tt != null) && (tt.getType() != TType.NONE)) {
                                tmlt = new TMLType(tt.getType());
                                request.addParam(tmlt);
                            }
                        }
                    } else {
                        // Must check whether the destination task is the same
                        if (request.getDestinationTask() != tmlm.getTMLTaskByName(makeName((TGComponent) t2, t2.getTaskName()))) {
                            String msg = "Two requests are declared with the same name but with two different destination tasks: " + tmlro.getRequestName();
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmldp.tmltdp);
                            ce.setTGComponent(tgc);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(tmlro.getRequestName() + " msg");
                        }

                        // Must check whether the two requests are compatible (parameters)
                        int nbOfParamsCurrent = 0;
                        for (int i = 0; i < tmlro.getRequestMaxParam(); i++) {
                            tt = tmlro.getParamAt(i);
                            if ((tt != null) && (tt.getType() != TType.NONE)) {
                                nbOfParamsCurrent++;
                            }
                        }
                        if (request.getNbOfParams() != nbOfParamsCurrent) {
                            String msg = "request " + tmlro.getRequestName() + " is declared several times with different parameters";
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmldp.tmltdp);
                            ce.setTGComponent(tgc);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(tmlro.getRequestName() + " msg");
                        }

                        // Must check param types as well
                        int cpti = 0;
                        for (int i = 0; i < tmlro.getRequestMaxParam(); i++) {
                            tt = tmlro.getParamAt(i);
                            if ((tt != null) && (tt.getType() != TType.NONE)) {
                                tmlt = new TMLType(tt.getType());
                                if (request.getType(cpti).getType() != tmlt.getType()) {
                                    String msg = "request " + tmlro.getRequestName() + " is declared several times with different types in parameters";
                                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                    ce.setTDiagramPanel(tmldp.tmltdp);
                                    ce.setTGComponent(tgc);
                                    checkingErrors.add(ce);
                                    throw new MalformedTMLDesignException(tmlro.getRequestName() + " msg");
                                }
                                cpti++;
                            }
                        }
                    }

                    // More: test the compatibility of the request!
                    if (request.getDestinationTask() != tmlm.getTMLTaskByName(makeName((TGComponent) t2, t2.getTaskName()))) {
                        String msg = "request " + tmlro.getRequestName() + " is declared several times differently (compatibility issue)";
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                        ce.setTDiagramPanel(tmldp.tmltdp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                        throw new MalformedTMLDesignException(tmlro.getRequestName() + " msg");
                    }
                    request.addOriginTask(tmlm.getTMLTaskByName(makeName((TGComponent) t1, t1.getTaskName())));

                    task = tmlm.getTMLTaskByName(makeName((TGComponent) t2, t2.getTaskName()));
                    task.setRequested(true);
                    task.setRequest(request);

                    // Request attributes
                    //TraceManager.addDev("Requests attributes");
                    String attname;
                    for (int j = 0; j < request.getNbOfParams(); j++) {
                        attname = "arg" + (j + 1) + "__req";
                        if (task.getAttributeByName(attname) == null) {
                            tmltt = new TMLType(request.getType(j).getType());
                            tmlattr = new TMLAttribute(attname, tmltt);
                            tmlattr.initialValue = tmlattr.getDefaultInitialValue();
                            //TraceManager.addDev("Adding " + tmlattr.getName() + " to " + task.getName() + "with value =" + tmlattr.initialValue);
                            task.addAttribute(tmlattr);
                        }
                    }


                } else {
                    removedRequests.add(new String(tmlro.getRequestName()));
                    UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Request " + tmlro.getRequestName() + " has been removed");
                    ce.setTDiagramPanel(tmldp.tmltdp);
                    ce.setTGComponent(tmlro);
                    warnings.add(ce);
                }
            }
        }
    }

    private void addTMLCChannels() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLCPrimitiveComponent tmlc;
        Iterator<? extends TGComponent> iterator = components.listIterator();
        Iterator<TMLCPrimitivePort> li;//, li2;
        List<TMLCPrimitivePort> ports, portstome;
        String name, name1, name2;
        TMLCPrimitivePort port1, port2;

        int j;

        //TMLTaskInterface t1, t2;
        TMLChannel channel;
        TMLTask tt1, tt2;

        List<TGComponent> alreadyConsidered = new ArrayList<TGComponent>();

        //TraceManager.addDev("*** Adding channels ***");

        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLCPrimitiveComponent) {
                tmlc = (TMLCPrimitiveComponent) tgc;
                //TraceManager.addDev("Component:" + tmlc.getValue());
                ports = tmlc.getAllChannelsOriginPorts();
                //TraceManager.addDev("Ports size:" + ports.size());
                li = ports.listIterator();
                while (li.hasNext()) {
                    port1 = li.next();
                    if (!(alreadyConsidered.contains(port1))) {
                        portstome = tmlcdp.tmlctdp.getPortsConnectedTo(port1, componentsToTakeIntoAccount);

                        if (portstome.size() < 1) {
                            String msg = "port " + port1.getPortName() + " is not correctly connected";
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmlcdp.tmlctdp);
                            ce.setTGComponent(tgc);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(msg);
                        }

                        if (portstome.size() == 1) {
                            port2 = portstome.get(0);
                            alreadyConsidered.add(port1);
                            alreadyConsidered.add(port2);

                            String[] text1 = port1.getPortName().split(",");
                            String[] text2 = port2.getPortName().split(",");

                            for (j = 0; j < Math.min(text1.length, text2.length); j++) {
                                name1 = text1[j].trim();
                                name2 = text2[j].trim();

                                /*if (name1.equals(name2)) {
                                  name = makeName(tgc, name1);
                                  } else {
                                  name = makeName(tgc, name1 + "__" + name2);
                                  }*/

                                name = makeName(port1, name1) + "__" + makeName(port2, name2);

                                if (makeName(port1, name1).compareTo(makeName(port2, name2)) == 0) {
                                    name = makeName(port1, name1);
                                }

                                //TraceManager.addDev("Adding to table : " + makeName(port1, port1.getFather().getValue()) + "/" + name1);
                                addToTable(makeName(port1, port1.getFather().getValue()) + "/" + name1, name);
                                //TraceManager.addDev("Adding to table : " + makeName(port2, port2.getFather().getValue()) + "/" + name2);
                                addToTable(makeName(port2, port2.getFather().getValue()) + "/" + name2, name);

                                channel = new TMLChannel(name, port1);
                                channel.setSize(port1.getSize());
                                channel.setMax(port1.getMax());
                                channel.ports.add(port1);
                                channel.ports.add(port2);
                                if (port1.isBlocking() && port2.isBlocking()) {
                                    channel.setType(TMLChannel.BRBW);
                                } else if (!port1.isBlocking() && port2.isBlocking()) {
                                    channel.setType(TMLChannel.BRNBW);
                                } else if (!port1.isBlocking() && !port2.isBlocking()) {
                                    channel.setType(TMLChannel.NBRNBW);
                                } else {
                                    String msg = "Ports " + name1 + " and " + name2 + " are not compatible (NBRBW)";
                                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                    ce.setTGComponent(port1);
                                    checkingErrors.add(ce);
                                    throw new MalformedTMLDesignException(msg);
                                }

                                if (tmlm.hasSameChannelName(channel)) {
                                    if (tmlm.hasAlmostSimilarChannel(channel)) {
                                        String msg = " channel " + name + " is declared several times differently";
                                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                        ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                        ce.setTGComponent(tgc);
                                        checkingErrors.add(ce);
                                        throw new MalformedTMLDesignException(msg);
                                    }
                                } else {
                                    tt1 = tmlm.getTMLTaskByName(makeName(port1, port1.getFather().getValue()));
                                    tt2 = tmlm.getTMLTaskByName(makeName(port2, port2.getFather().getValue()));
                                    channel.setTasks(tt1, tt2);



                                    // Complex channels are used only for transformation towards the simulator
                                    TMLPort tmlport1, tmlport2;
                                    tmlport1 = new TMLPort(port1.getPortName(), port1);
                                    tmlport1.setPrex(port1.isPrex());
                                    tmlport1.setPostex(port1.isPostex());
                                    tmlport1.setAssociatedEvent(port1.getAssociatedEvent());
                                    tmlport2 = new TMLPort(port2.getPortName(), port2);
                                    tmlport2.setPrex(port2.isPrex());
                                    tmlport2.setPostex(port2.isPostex());
                                    tmlport2.setAssociatedEvent(port2.getAssociatedEvent());
                                    channel.setPorts(tmlport1, tmlport2);

                                    if (port1.isLossy()) {
                                        channel.setLossy(true, port1.getLossPercentage(), port1.getMaxNbOfLoss());
                                    }
                                    tmlm.addChannel(channel);
                                    listE.addCor(channel, tgc);
                                    //TraceManager.addDev("Adding channel " + channel.getName());
                                }
                            }
                        } else {
                            // Complex channel "1 -> many" or "many -> 1"
                            TMLCPrimitivePort port;


                            // Only one channel per port
                            if (port1.getPortName().indexOf(",") != -1) {
                                String msg = "Multiple definition of channels with more than one output port is not allowed: " + port1.getPortName();
                                UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                ce.setTGComponent(port1);
                                checkingErrors.add(ce);
                                throw new MalformedTMLDesignException(msg);
                            }
                            for (j = 0; j < portstome.size(); j++) {
                                port = portstome.get(j);
                                if (port.getPortName().indexOf(",") != -1) {
                                    String msg = "Multiple definition of channels with more than one output port is not allowed: " + port.getPortName();
                                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                    ce.setTGComponent(port);
                                    checkingErrors.add(ce);
                                    throw new MalformedTMLDesignException(msg);
                                }
                            }

                            // Name of port
                            name = makeName(port1, port1.getPortName());
                            for (j = 0; j < portstome.size(); j++) {
                                name += "__" + portstome.get(j).getPortName();
                            }

                            // Correspondence table
                            alreadyConsidered.add(port1);
                            addToTable(makeName(port1, port1.getFather().getValue()) + "/" + port1.getPortName(), name);
                            for (j = 0; j < portstome.size(); j++) {
                                port = portstome.get(j);
                                alreadyConsidered.add(port);
                                addToTable(makeName(port, port.getFather().getValue()) + "/" + port.getPortName(), name);
                            }

                            // Channel attributes
                            port = portstome.get(0);
                            TraceManager.addDev("Fork sample?");
                            channel = new TMLChannel(name, port1);
                            TMLCPath path = tmlc.findPathWith(port);
                            if (path != null) {
                                TMLCFork fork = path.getFork(0);
                                if (fork != null) {
                                    TraceManager.addDev("Setting fork sample");
                                    channel.setNumberOfSamples(fork.getNumberOfSamples());
                                }
                                TMLCJoin join = path.getJoin(0);
                                if (join != null) {
                                    TraceManager.addDev("Setting join sample");
                                    channel.setNumberOfSamples(join.getNumberOfSamples());
                                }
                            }
                            channel.ports.add(port1);
                            for (j = 0; j < portstome.size(); j++) {
                                TMLCPrimitivePort p = portstome.get(j);
                                channel.ports.add(p);
                            }
                            channel.setSize(port1.getSize());
                            channel.setMax(port1.getMax());
                            if (port1.isBlocking() && port.isBlocking()) {
                                channel.setType(TMLChannel.BRBW);
                            } else if (!port1.isBlocking() && port.isBlocking()) {
                                channel.setType(TMLChannel.BRNBW);
                            } else if (!port1.isBlocking() && !port.isBlocking()) {
                                channel.setType(TMLChannel.NBRNBW);
                            } else {
                                String msg = "Ports " + port1.getPortName() + " and " + port.getPortName() + " are not compatible (NBRBW)";
                                UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                ce.setTGComponent(port1);
                                checkingErrors.add(ce);
                                throw new MalformedTMLDesignException(msg);
                            }
                            if (port1.isLossy()) {
                                channel.setLossy(true, port1.getLossPercentage(), port1.getMaxNbOfLoss());
                            }

                            if (tmlm.hasSameChannelName(channel)) {
                                if (tmlm.hasAlmostSimilarChannel(channel)) {
                                    String msg = " channel " + name + " is declared several times differently";
                                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                    ce.setTGComponent(tgc);
                                    checkingErrors.add(ce);
                                    throw new MalformedTMLDesignException(msg);
                                }
                            } else {
                                TMLPort tmlport;
                                tt1 = tmlm.getTMLTaskByName(makeName(port1, port1.getFather().getValue()));
                                tmlport = new TMLPort(port1.getPortName(), port1);
                                tmlport.setAssociatedEvent(port1.getAssociatedEvent());
                                channel.addTaskPort(tt1, tmlport, true);
                                for (j = 0; j < portstome.size(); j++) {
                                    port = portstome.get(j);
                                    tmlport = new TMLPort(port.getPortName(), port);
                                    tmlport.setPrex(port.isPrex());
                                    tmlport.setPostex(port.isPostex());
                                    tmlport.setAssociatedEvent(port.getAssociatedEvent());
                                    tt2 = tmlm.getTMLTaskByName(makeName(port, port.getFather().getValue()));
                                    channel.addTaskPort(tt2, tmlport, port.isOrigin());
                                }

                                tmlm.addChannel(channel);
                                listE.addCor(channel, tgc);
                                //TraceManager.addDev("Adding channel " + channel.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    private void addTMLCEvents() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLCPrimitiveComponent tmlc;
        Iterator<? extends TGComponent> iterator = components.listIterator();
        Iterator<TMLCPrimitivePort> li;//, li2;
        List<TMLCPrimitivePort> ports, portstome;
        String name;
        TMLCPrimitivePort port1, port2;

        int i, j;
        String name1, name2;

        //TMLTaskInterface t1, t2;
        TMLEvent event;
        TMLTask tt1, tt2;
        TType tt;
        TMLType tmlt;
        TMLCRecordComponent record;
        TAttribute ta;

        List<TGComponent> alreadyConsidered = new ArrayList<TGComponent>();

        //TraceManager.addDev("*** Adding Events ***");

        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLCPrimitiveComponent) {
                tmlc = (TMLCPrimitiveComponent) tgc;
                //TraceManager.addDev("Component:" + tmlc.getValue());
                ports = tmlc.getAllEventsOriginPorts();
                //TraceManager.addDev("Ports size:" + ports.size());
                li = ports.listIterator();
                while (li.hasNext()) {
                    port1 = li.next();
                    if (!(alreadyConsidered.contains(port1))) {
                        portstome = tmlcdp.tmlctdp.getPortsConnectedTo(port1, componentsToTakeIntoAccount);
                        //TraceManager.addDev("Considering port1 = " +port1.getPortName() + " size of connecting ports:" + portstome.size());
                       // Iterator<?> ite = portstome.listIterator();
                        /*while(ite.hasNext()) {
                            TraceManager.addDev("port=" + ((TMLCPrimitivePort)(ite.next())).getPortName());
                        }*/

                        if (portstome.size() < 1) {
                            String msg = "port " + port1.getPortName() + " is not correctly connected";
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmlcdp.tmlctdp);
                            ce.setTGComponent(tgc);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(msg);
                        }
                        // Same parameters than in destination ports?


                        if (portstome.size() == 1) {
                            port2 = portstome.get(0);
                            if (!port2.hasSameParametersThan(port1)) {
                                String msg = "port " + port1.getPortName() + " does not define the same parameters as port " + port2.getName();
                                UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                ce.setTGComponent(port1);
                                checkingErrors.add(ce);
                                throw new MalformedTMLDesignException(msg);
                            }

                            alreadyConsidered.add(port1);
                            alreadyConsidered.add(port2);

                            // Useless loop. Loop because the algo evolves all the time ;-)
                            for (int kk = 0; kk < portstome.size(); kk++) {
                                port2 = portstome.get(kk);

                                String[] text1 = port1.getPortName().split(",");
                                String[] text2 = port2.getPortName().split(",");

                                /*for (i=0; i<text1.length; i++) {
                                  TraceManager.addDev("text1[" + i + "] = " + text1[i]);
                                  }

                                  for (i=0; i<text2.length; i++) {
                                  TraceManager.addDev("text2[" + i + "] = " + text2[i]);
                                  }*/

                                for (j = 0; j < Math.min(text1.length, text2.length); j++) {
                                    name1 = text1[j].trim();
                                    name2 = text2[j].trim();
                                    //TraceManager.addDev("name1=" + name1 + " name2=" + name2);
                                    if (kk == 0) {
                                        name = makeName(port1, name1) + "__" + makeName(port2, name2);
                                    } else {
                                        name = makeName(port1, name1) + "__" + makeName(port2, name2) + "__FORK" + kk;
                                    }

                                    //TraceManager.addDev("Adding to table : " + makeName(port1, port1.getFather().getValue()) + "/" + name1);
                                    addToTable(makeName(port1, port1.getFather().getValue()) + "/" + name1, name);
                                    //TraceManager.addDev("Adding to table : " + makeName(port2, port2.getFather().getValue()) + "/" + name2);
                                    addToTable(makeName(port2, port2.getFather().getValue()) + "/" + name2, name);

                                    if (port1.isFinite()) {
                                        event = new TMLEvent(name, port1, port1.getMax(), port1.isBlocking());
                                    } else {
                                        event = new TMLEvent(name, port1, -1, port1.isBlocking());
                                    }
                                    event.port = port1;
                                    event.port2 = port2;
                                    for (i = 0; i < port1.getNbMaxAttribute(); i++) {
                                        tt = port1.getParamAt(i);
                                        if ((tt != null) && (tt.getType() != TType.NONE)) {
                                            if (tt.getType() == TType.OTHER) {
                                                // Record
                                                // Search for the record
                                                record = tmlc.getRecordNamed(tt.getTypeOther());
                                                if (record == null) {
                                                    String msg = " event " + name + " is declared as using an unknown type: " + tt.getTypeOther();
                                                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                                    ce.setTGComponent(tgc);
                                                    checkingErrors.add(ce);
                                                    throw new MalformedTMLDesignException(msg);
                                                } else {
                                                    for (int k = 0; k < record.getAttributes().size(); k++) {
                                                        ta = record.getAttributes().get(k);
                                                        if (ta.getType() == TAttribute.NATURAL) {
                                                            tmlt = new TMLType(TMLType.NATURAL);
                                                        } else if (ta.getType() == TAttribute.BOOLEAN) {
                                                            tmlt = new TMLType(TMLType.BOOLEAN);
                                                        } else {
                                                            tmlt = new TMLType(TMLType.OTHER);
                                                        }
                                                        event.addParam(tmlt);
                                                    }
                                                }
                                            } else {
                                                tmlt = new TMLType(tt.getType());
                                                event.addParam(tmlt);
                                            }
                                            //TraceManager.addDev("Event " + event.getName() + " add param");
                                        }
                                    }

                                    if (tmlm.hasSameEventName(event)) {
                                        if (tmlm.hasAlmostSimilarEvent(event)) {
                                            String msg = " event " + name + " is declared several times differently";
                                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                            ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                            ce.setTGComponent(tgc);
                                            checkingErrors.add(ce);
                                            throw new MalformedTMLDesignException(msg);
                                        } else {
                                            //TraceManager.addDev("Same evt : not added");
                                        }
                                    } else {
                                        tt1 = tmlm.getTMLTaskByName(makeName(port1, port1.getFather().getValue()));
                                        tt2 = tmlm.getTMLTaskByName(makeName(port2, port2.getFather().getValue()));
                                        //TraceManager.addDev("Tasks of event: t1=" + tt1.getName() + " t2=" + tt2.getName());
                                        event.setTasks(tt1, tt2);

                                        if (port1.isLossy()) {
                                            event.setLossy(true, port1.getLossPercentage(), port1.getMaxNbOfLoss());
                                        }
                                        tmlm.addEvent(event);
                                        listE.addCor(event, tgc);
                                        //TraceManager.addDev("Adding event " + event.getName());
                                    }
                                }
                            }
                            // 1 -> many
                            // Complex event
                        } else {
                            //TraceManager.addDev("One to many event");
                            TMLCPrimitivePort port;

                            // Only one channel per port
                            if (port1.getPortName().indexOf(",") != -1) {
                                String msg = "Multiple definition of events with more than one output port is not allowed: " + port1.getPortName();
                                UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                ce.setTGComponent(port1);
                                checkingErrors.add(ce);
                                throw new MalformedTMLDesignException(msg);
                            }
                            for (j = 0; j < portstome.size(); j++) {
                                port = portstome.get(j);
                                if (port.getPortName().indexOf(",") != -1) {
                                    String msg = "Multiple definition of events with more than one output port is not allowed: " + port.getPortName();
                                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                    ce.setTGComponent(port);
                                    checkingErrors.add(ce);
                                    throw new MalformedTMLDesignException(msg);
                                }
                                if (!port.hasSameParametersThan(port1)) {
                                    String msg = "port " + port1.getPortName() + " does not define the same parameters as port " + port.getName();
                                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                    ce.setTGComponent(port1);
                                    checkingErrors.add(ce);
                                    throw new MalformedTMLDesignException(msg);
                                }
                            }
                            // Name of port
                            name = makeName(port1, port1.getPortName());
                            for (j = 0; j < portstome.size(); j++) {
                                name += "__" + portstome.get(j).getPortName();
                            }

                            // Correspondance table
                            alreadyConsidered.add(port1);
                            addToTable(makeName(port1, port1.getFather().getValue()) + "/" + port1.getPortName(), name);
                            for (j = 0; j < portstome.size(); j++) {
                                port = portstome.get(j);
                                alreadyConsidered.add(port);
                                addToTable(makeName(port, port.getFather().getValue()) + "/" + port.getPortName(), name);
                            }

                            // Channel attributes
                            port = portstome.get(0);
                            if (port.isFinite()) {
                                event = new TMLEvent(name, port1, port1.getMax(), port1.isBlocking());
                            } else {
                                event = new TMLEvent(name, port1, -1, port1.isBlocking());
                            }
                            event.ports.add(port1);
                            for (j = 0; j < portstome.size(); j++) {
                                TMLCPrimitivePort p = portstome.get(j);
                                event.ports.add(p);
                            }
                            for (i = 0; i < port1.getNbMaxAttribute(); i++) {
                                tt = port1.getParamAt(i);
                                if ((tt != null) && (tt.getType() != TType.NONE)) {
                                    if (tt.getType() == TType.OTHER) {
                                        // Record
                                        // Search for the record
                                        record = tmlc.getRecordNamed(tt.getTypeOther());
                                        if (record == null) {
                                            String msg = " event " + name + " is declared as using an unknown type: " + tt.getTypeOther();
                                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                            ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                            ce.setTGComponent(tgc);
                                            checkingErrors.add(ce);
                                            throw new MalformedTMLDesignException(msg);
                                        } else {
                                            for (int k = 0; k < record.getAttributes().size(); k++) {
                                                ta = record.getAttributes().get(k);
                                                if (ta.getType() == TAttribute.NATURAL) {
                                                    tmlt = new TMLType(TMLType.NATURAL);
                                                } else if (ta.getType() == TAttribute.BOOLEAN) {
                                                    tmlt = new TMLType(TMLType.BOOLEAN);
                                                } else {
                                                    tmlt = new TMLType(TMLType.OTHER);
                                                }
                                                event.addParam(tmlt);
                                            }
                                        }
                                    } else {
                                        tmlt = new TMLType(tt.getType());
                                        event.addParam(tmlt);
                                    }
                                    //TraceManager.addDev("Event " + event.getName() + " add param");
                                }
                            } // For
                            if (tmlm.hasSameEventName(event)) {
                                if (tmlm.hasAlmostSimilarEvent(event)) {
                                    String msg = " event " + name + " is declared several times differently";
                                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                    ce.setTGComponent(tgc);
                                    checkingErrors.add(ce);
                                    throw new MalformedTMLDesignException(msg);
                                } else {
                                    //TraceManager.addDev("Same evt : not added");
                                }
                            } else {
                                TMLPort tmlport;
                                tt1 = tmlm.getTMLTaskByName(makeName(port1, port1.getFather().getValue()));
                                tmlport = new TMLPort(port1.getPortName(), port1);
                                tmlport.setAssociatedEvent(port1.getAssociatedEvent());
                                event.addTaskPort(tt1, tmlport, true);
                                for (j = 0; j < portstome.size(); j++) {
                                    port = portstome.get(j);
                                    tmlport = new TMLPort(port.getPortName(), port);
                                    tmlport.setPrex(port.isPrex());
                                    tmlport.setPostex(port.isPostex());
                                    tmlport.setAssociatedEvent(port.getAssociatedEvent());
                                    tt2 = tmlm.getTMLTaskByName(makeName(port, port.getFather().getValue()));
                                    event.addTaskPort(tt2, tmlport, port.isOrigin());
                                }

                                /*tt1 = tmlm.getTMLTaskByName(makeName(port1, port1.getFather().getValue()));
                                  tt2 = tmlm.getTMLTaskByName(makeName(port2, port2.getFather().getValue()));
                                  TraceManager.addDev("Tasks of event: t1=" + tt1.getName() + " t2=" + tt2.getName());
                                  event.setTasks(tt1, tt2);*/

                                if (port1.isLossy()) {
                                    event.setLossy(true, port1.getLossPercentage(), port1.getMaxNbOfLoss());
                                }
                                tmlm.addEvent(event);
                                listE.addCor(event, tgc);
                                //TraceManager.addDev("Adding event " + event.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    private void addTMLCRequests() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLCPrimitiveComponent tmlc;
        Iterator<? extends TGComponent> iterator = components.listIterator();
        Iterator<TMLCPrimitivePort> li;//, li2;
        List<TMLCPrimitivePort> ports, portstome;
        String name;
        TMLCPrimitivePort port1, port2, port3;

        //TMLTaskInterface t1, t2;
        TMLRequest request;
        TMLTask tt1, tt2;
        TType tt;
        TMLType tmlt;

        TMLAttribute tmlattr;
        TMLType tmltt;

        TMLCRecordComponent record;
        TAttribute ta;
        int i;

        //TraceManager.addDev("*** Adding requests ***");

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCPrimitiveComponent) {
                tmlc = (TMLCPrimitiveComponent) tgc;
                //TraceManager.addDev("Component:" + tmlc.getValue());
                ports = tmlc.getAllRequestsDestinationPorts();
                //TraceManager.addDev("Ports size:" + ports.size());
                li = ports.listIterator();
                while (li.hasNext()) {
                    port1 = li.next();
                    portstome = tmlcdp.tmlctdp.getPortsConnectedTo(port1, componentsToTakeIntoAccount);
                    //TraceManager.addDev("Considering port1 = " +port1.getPortName() + " size of connecting ports:" + portstome.size());

                    //                   ListIterator ite = portstome.listIterator();
                    //while(ite.hasNext()) {
                    //TraceManager.addDev("port=" + ((TMLCPrimitivePort)(ite.next())).getPortName());
                    //}

                    if (portstome.size() == 0) {
                        String msg = "port " + port1.getPortName() + " is not correctly connected";
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                        ce.setTDiagramPanel(tmlcdp.tmlctdp);
                        ce.setTGComponent(port1);
                        checkingErrors.add(ce);
                        throw new MalformedTMLDesignException(msg);
                    }

                    for (i = 0; i < portstome.size(); i++) {
                        port3 = portstome.get(i);
                        if (!port3.isOrigin()) {
                            String msg = "port " + port1.getPortName() + " is not correctly connected to port " + port3.getName();
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmlcdp.tmlctdp);
                            ce.setTGComponent(port1);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(msg);
                        }
                        // Same parameters than in destination ports?
                        if (!port3.hasSameParametersThan(port1)) {
                            String msg = "port " + port1.getPortName() + " does not define the same parameters as port " + port3.getName();
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmlcdp.tmlctdp);
                            ce.setTGComponent(port1);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(msg);
                        }

                    }

                    String name1 = port1.getPortName();
                    name = makeName(port1, name1);
                    addToTable(makeName(port1, port1.getFather().getValue()) + "/" + name1, name);

                    request = new TMLRequest(name, port1);
                    request.ports.add(port1);
                    for (i = 0; i < portstome.size(); i++) {
                        port2 = portstome.get(i);
                        request.ports.add(port2);
                        //TraceManager.addDev("Add add add to table request : " + makeName(port2, port2.getFather().getValue()) + "/" + port2.getName() + " name =" + name);
                        addToTable(makeName(port2, port2.getFather().getValue()) + "/" + port2.getPortName(), name);
                    }

                    for (i = 0; i < port1.getNbMaxAttribute(); i++) {
                        tt = port1.getParamAt(i);
                        if ((tt != null) && (tt.getType() != TType.NONE)) {
                            if (tt.getType() == TType.OTHER) {
                                // Record
                                // Search for the record
                                record = tmlc.getRecordNamed(tt.getTypeOther());
                                if (record == null) {
                                    String msg = " request " + name + " is declared as using an unknown type: " + tt.getTypeOther();
                                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                                    ce.setTGComponent(tgc);
                                    checkingErrors.add(ce);
                                    throw new MalformedTMLDesignException(msg);
                                } else {
                                    for (int k = 0; k < record.getAttributes().size(); k++) {
                                        ta = record.getAttributes().get(k);
                                        if (ta.getType() == TAttribute.NATURAL) {
                                            tmlt = new TMLType(TMLType.NATURAL);
                                        } else if (ta.getType() == TAttribute.BOOLEAN) {
                                            tmlt = new TMLType(TMLType.BOOLEAN);
                                        } else {
                                            tmlt = new TMLType(TMLType.OTHER);
                                        }
                                        request.addParam(tmlt);
                                    }
                                }
                            } else {
                                tmlt = new TMLType(tt.getType());
                                request.addParam(tmlt);
                            }
                            //TraceManager.addDev("Event " + event.getName() + " add param");
                        }
                    }

                    tt1 = tmlm.getTMLTaskByName(makeName(port1, port1.getFather().getValue()));
                    tt1.setRequested(true);
                    tt1.setRequest(request);
                    request.setDestinationTask(tt1);

                    // Request attributes
                    //TraceManager.addDev("Requests attributes");
                    String attname;
                    for (int j = 0; j < request.getNbOfParams(); j++) {
                        attname = "arg" + (j + 1) + "__req";
                        if (tt1.getAttributeByName(attname) == null) {
                            tmltt = new TMLType(request.getType(j).getType());
                            tmlattr = new TMLAttribute(attname, tmltt);
                            tmlattr.initialValue = tmlattr.getDefaultInitialValue();
                            //TraceManager.addDev("Adding " + tmlattr.getName() + " to " + tt1.getName() + "with value =" + tmlattr.initialValue);
                            tt1.addAttribute(tmlattr);
                        }
                    }

                    for (i = 0; i < portstome.size(); i++) {
                        port2 = portstome.get(i);
                        tt2 = tmlm.getTMLTaskByName(makeName(port2, port2.getFather().getValue()));
                        if (tt2 == null) {
                            //TraceManager.addDev(" NO NO NO NO Destination taskin request!");
                        }
                        request.addOriginTask(tt2);
                        //TraceManager.addDev("LOSS?");
                        if (port2.isLossy()) {
                            //TraceManager.addDev("LOSS***** Lossy request port" + port2.getLossPercentage() + " maxLoss=" + port2.getMaxNbOfLoss());
                            request.setLossy(true, port2.getLossPercentage(), port2.getMaxNbOfLoss());
                        }
                    }

                    // Check whether there is another request having a different name but with the same destination task
                    TMLRequest request1 = tmlm.getRequestByDestinationTask(request.getDestinationTask());
                    if (request1 != null) {
                        if (request1.getName().compareTo(name) != 0) {
                            String msg = "Two requests port declared on the same destination task have different names: " + name;
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmlcdp.tmlctdp);
                            ce.setTGComponent(tgc);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(msg);
                        }
                    }

                    if (tmlm.hasSameRequestName(request)) {
                        TMLRequest otherReq = tmlm.hasSimilarRequest(request);
                        if (otherReq == null) {
                            String msg = " request " + name + " is declared several times differently";
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmlcdp.tmlctdp);
                            ce.setTGComponent(port1);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(msg);
                        }
                        for (i = 0; i < request.getOriginTasks().size(); i++) {
                            otherReq.addOriginTask(request.getOriginTasks().get(i));
                        }
                    } else {

                        tmlm.addRequest(request);
                        listE.addCor(request, tgc);
                        //TraceManager.addDev("Adding request " + request.getName());
                    }
                }
            }
        }
    }

    private void addAttributesTo(TMLTask tmltask, TMLTaskOperator tmlto) {
        List<TAttribute> attributes = tmlto.getAttributes();
        addAttributesTo(tmlto, tmltask, attributes);
    }

    private void addAttributesTo(TMLTask tmltask, TMLCPrimitiveComponent tmlcpc) {
        java.util.List<TAttribute> attributes = tmlcpc.getAttributeList();
        addAttributesTo(tmlcpc, tmltask, attributes);
    }

    private void addAttributesTo(TGComponent tgc, TMLTask tmltask, List<TAttribute> attributes) {
        TMLType tt;
        //  String name;
        TMLAttribute tmlt;
        //  TMLRequest req;
        TMLCRecordComponent rc;

        for (TAttribute ta : attributes) {
            rc = null;
            tt = null;
            if (ta.getType() == TAttribute.NATURAL) {
                tt = new TMLType(TMLType.NATURAL);
            } else if (ta.getType() == TAttribute.BOOLEAN) {
                tt = new TMLType(TMLType.BOOLEAN);
            } else {
                // Must be a record
                if (tgc instanceof TMLCPrimitiveComponent) {
                    //TraceManager.addDev("Searching for record named: " + ta.getTypeOther());
                    rc = ((TMLCPrimitiveComponent) tgc).getRecordNamed(ta.getTypeOther());
                    if (rc == null) {
                        tt = new TMLType(TMLType.OTHER);
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown record: " + ta.getTypeOther() + " of attribute " + ta.getId() + " declared in task " + tmltask.getName());
                        ce.setTDiagramPanel(tmlcdp.tmlctdp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    }
                } else {
                    // Classical TML design
                    // Error!
                    tt = new TMLType(TMLType.OTHER);
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown type in variable " + ta.getId() + " of task " + tmltask.getName());
                    ce.setTDiagramPanel(tmldp.tmltdp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                }

            }
            if (rc == null) {
                tmlt = new TMLAttribute(ta.getId(), tt);
                tmlt.initialValue = ta.getInitialValue();
                //TraceManager.addDev("ta =" + ta.getId() + " value=" + ta.getInitialValue());
                tmltask.addAttribute(tmlt);

            } else {
                // Adding all elements of record
                //TraceManager.addDev("Found a record named: " + rc.getValue());
                List<TAttribute> attr = rc.getAttributes();
                for (TAttribute tat : attr) {
                    if (tat.getType() == TAttribute.NATURAL) {
                        tt = new TMLType(TMLType.NATURAL);
                    } else if (tat.getType() == TAttribute.BOOLEAN) {
                        tt = new TMLType(TMLType.BOOLEAN);
                    } else {
                        tt = new TMLType(TMLType.OTHER);
                    }
                    tmlt = new TMLAttribute(ta.getId() + "__" + tat.getId(), tt);
                    tmlt.initialValue = tat.getInitialValue();
                    //TraceManager.addDev("record variable ta =" + tmlt.getName() + " value=" + tmlt.getInitialValue());
                    tmltask.addAttribute(tmlt);

                }
            }
        }
    }

    private void createSecurityPatterns(TMLTask tmltask) {
        TMLActivity activity = tmltask.getActivityDiagram();
        TMLActivityDiagramPanel tadp = (TMLActivityDiagramPanel) (activity.getReferenceObject());
        TGComponent tgc;
        //TraceManager.addDev("Generating activity diagram of:" + tmltask.getName());

        // search for start state
        List<TGComponent> list = tadp.getComponentList();
        Iterator<TGComponent> iterator = list.listIterator();
        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLADEncrypt) {
                if (!((TMLADEncrypt) tgc).securityContext.isEmpty()) {
                    SecurityPattern securityPattern = new SecurityPattern(((TMLADEncrypt) tgc).securityContext, ((TMLADEncrypt) tgc).type, ((TMLADEncrypt) tgc).message_overhead, ((TMLADEncrypt) tgc).size, ((TMLADEncrypt) tgc).encTime, ((TMLADEncrypt) tgc).decTime, ((TMLADEncrypt) tgc).nonce, ((TMLADEncrypt) tgc).formula, ((TMLADEncrypt) tgc).key);
                    securityPatterns.put(securityPattern.name, securityPattern);
                    tmlm.addSecurityPattern(securityPattern);
                    ArrayList<TMLTask> l = new ArrayList<TMLTask>();
                    tmlm.securityTaskMap.put(securityPattern, l);
                    //TraceManager.addDev("Adding Security Pattern " + securityPattern.name);
                }
            }
        }
    }

    private void generateTasksActivityDiagrams() throws MalformedTMLDesignException {
        TMLTask tmltask;

        //First generate security patterns over all tasks
        Iterator<TMLTask> iterator = tmlm.getTasks().listIterator();

        while (iterator.hasNext()) {
            tmltask = iterator.next();
            createSecurityPatterns(tmltask);
        }

        iterator = tmlm.getTasks().listIterator();

        while (iterator.hasNext()) {
            tmltask = iterator.next();

            // Issue #69: Component  disabling
            ActivityDiagram2TMLTranslator.INSTANCE.generateTaskActivityDiagrams(	tmltask,
																            		checkingErrors,
																					warnings,
																					listE,
																					tmlm,
																					securityPatterns,
																					table,
																					removedChannels,
																					removedEvents,
																					removedRequests );
//            generateTaskActivityDiagrams(tmltask);
        }
        //TraceManager.addDev( "errors: " + checkingErrors.size() );
        if (checkingErrors.size() > 0) {
            throw new MalformedTMLDesignException("Error(s) found in activity diagrams");
        }
    }

//
//    private String modifyActionString(String _input) {
//        int index = _input.indexOf("++");
//        boolean b1, b2;
//        String tmp;
//
//        if (index > -1) {
//            tmp = _input.substring(0, index).trim();
//
//            b1 = (tmp.substring(0, 1)).matches("[a-zA-Z]");
//            b2 = tmp.matches("\\w*");
//            if (b1 && b2) {
//                return tmp + " = " + tmp + " + 1";
//            }
//        }
//
//        index = _input.indexOf("--");
//        if (index > -1) {
//            tmp = _input.substring(0, index).trim();
//
//            b1 = (tmp.substring(0, 1)).matches("[a-zA-Z]");
//            b2 = tmp.matches("\\w*");
//            if (b1 && b2) {
//                return tmp + " = " + tmp + " - 1";
//            }
//        }
//
//        return modifyString(_input);
//    }
//
//    private String modifyString(String _input) {
//        return Conversion.replaceAllChar(_input, '.', "__");
//    }

    // Issue #69: Moved to class ActivityDiagram2TMLTranslator
//    private void generateTaskActivityDiagrams(TMLTask tmltask) throws MalformedTMLDesignException {
//        TMLActivity activity = tmltask.getActivityDiagram();
//        TMLActivityDiagramPanel tadp = (TMLActivityDiagramPanel) (activity.getReferenceObject());
//
//        //TraceManager.addDev("Generating activity diagram of:" + tmltask.getName());
//
//        // search for start state
//        List<TGComponent> list = tadp.getComponentList();
//        Iterator<TGComponent> iterator = list.listIterator();
//        TGComponent tgc;
//        TMLADStartState tss = null;
//        int cptStart = 0;
//        //    boolean rndAdded = false;
//
//        while (iterator.hasNext()) {
//            tgc = iterator.next();
//
//            if (tgc instanceof TMLADStartState) {
//                tss = (TMLADStartState) tgc;
//                cptStart++;
//            }
//        }
//
//        if (tss == null) {
//            TMLCheckingError ce = new TMLCheckingError(CheckingError.BEHAVIOR_ERROR, "No start state in the TML activity diagram of " + tmltask.getName());
//            ce.setTMLTask(tmltask);
//            checkingErrors.add(ce);
//            return;
//        }
//
//        if (cptStart > 1) {
//            TMLCheckingError ce = new TMLCheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in the TML activity diagram of " + tmltask.getName());
//            ce.setTMLTask(tmltask);
//            checkingErrors.add(ce);
//            return;
//        }
//
//        // Adding start state
//        TMLStartState tmlss = new TMLStartState("start", tss);
//        listE.addCor(tmlss, tss);
//        activity.setFirst(tmlss);
//
//        // Creation of other elements
//        TMLChannel channel;
//        String[] channels;
//        TMLEvent event;
//        TMLRequest request;
//
//        TMLADRandom tmladrandom;
//        TMLRandom tmlrandom;
//        TMLActionState tmlaction;
//        TMLChoice tmlchoice;
//        TMLExecI tmlexeci;
//        TMLExecIInterval tmlexecii;
//        TMLExecC tmlexecc;
//        TMLExecCInterval tmlexecci;
//        TMLForLoop tmlforloop;
//        TMLReadChannel tmlreadchannel;
//        TMLSendEvent tmlsendevent;
//        TMLSendRequest tmlsendrequest;
//        TMLStopState tmlstopstate;
//        TMLWaitEvent tmlwaitevent;
//        TMLNotifiedEvent tmlnotifiedevent;
//        TMLWriteChannel tmlwritechannel;
//        TMLSequence tmlsequence;
//        TMLRandomSequence tmlrsequence;
//        TMLSelectEvt tmlselectevt;
//        TMLDelay tmldelay;
//        int staticLoopIndex = 0;
//        String sl = "", tmp;
//        TMLType tt;
//        TMLAttribute tmlt;
//
//        iterator = list.listIterator();
//        while (iterator.hasNext()) {
//            tgc = iterator.next();
//            if (tgc.getCheckLatency()) {
//                String name = tmltask.getName() + ":" + tgc.getName();
//                name = name.replaceAll(" ", "");
//                //TraceManager.addDev("To check " + name);
//                if (tgc.getValue().contains("(")) {
//                    tmlm.addCheckedActivity(tgc, name + ":" + tgc.getValue().split("\\(")[0]);
//                } else {
//                    if (tgc instanceof TMLADExecI) {
//                        tmlm.addCheckedActivity(tgc, ((TMLADExecI) tgc).getDelayValue());
//                    }
//                }
//            }
//            if (tgc instanceof TMLADActionState) {
//                tmlaction = new TMLActionState("action", tgc);
//                tmp = ((TMLADActionState) (tgc)).getAction();
//                tmp = modifyActionString(tmp);
//                tmlaction.setAction(tmp);
//                activity.addElement(tmlaction);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlaction, tgc);
//
//            } else if (tgc instanceof TMLADRandom) {
//                tmladrandom = (TMLADRandom) tgc;
//                tmlrandom = new TMLRandom("random" + tmladrandom.getValue(), tgc);
//                tmp = tmladrandom.getVariable();
//                tmp = modifyActionString(tmp);
//                tmlrandom.setVariable(tmp);
//                tmp = tmladrandom.getMinValue();
//                tmp = modifyActionString(tmp);
//                tmlrandom.setMinValue(tmp);
//                tmp = tmladrandom.getMaxValue();
//                tmp = modifyActionString(tmp);
//                tmlrandom.setMaxValue(tmp);
//                tmlrandom.setFunctionId(tmladrandom.getFunctionId());
//                activity.addElement(tmlrandom);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlrandom, tgc);
//
//            } else if (tgc instanceof TMLADChoice) {
//                tmlchoice = new TMLChoice("choice", tgc);
//                // Guards are added at the same time as next activities
//                activity.addElement(tmlchoice);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlchoice, tgc);
//
//            } else if (tgc instanceof TMLADSelectEvt) {
//                tmlselectevt = new TMLSelectEvt("select", tgc);
//                activity.addElement(tmlselectevt);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlselectevt, tgc);
//
//            } else if (tgc instanceof TMLADExecI) {
//                tmlexeci = new TMLExecI("execi", tgc);
//                tmlexeci.setAction(modifyString(((TMLADExecI) tgc).getDelayValue()));
//                tmlexeci.setValue(((TMLADExecI) tgc).getDelayValue());
//                activity.addElement(tmlexeci);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlexeci, tgc);
//
//            } else if (tgc instanceof TMLADExecIInterval) {
//                tmlexecii = new TMLExecIInterval("execi", tgc);
//                tmlexecii.setValue(tgc.getValue());
//                tmlexecii.setMinDelay(modifyString(((TMLADExecIInterval) tgc).getMinDelayValue()));
//                tmlexecii.setMaxDelay(modifyString(((TMLADExecIInterval) tgc).getMaxDelayValue()));
//                activity.addElement(tmlexecii);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlexecii, tgc);
//
//            } else if (tgc instanceof TMLADEncrypt) {
//                tmlexecc = new TMLExecC("encrypt_" + ((TMLADEncrypt) tgc).securityContext, tgc);
//                activity.addElement(tmlexecc);
//                SecurityPattern sp = securityPatterns.get(((TMLADEncrypt) tgc).securityContext);
//                if (sp == null) {
//                    //Throw error for missing security pattern
//                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Security Pattern " + ((TMLADEncrypt) tgc).securityContext + " not found");
//                    ce.setTDiagramPanel(tadp);
//                    ce.setTGComponent(tgc);
//                    checkingErrors.add(ce);
//                } else {
//                    tmlexecc.securityPattern = sp;
//                    tmlexecc.setAction(Integer.toString(sp.encTime));
//                    ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                    tmlm.securityTaskMap.get(sp).add(tmltask);
//                    listE.addCor(tmlexecc, tgc);
//                }
//            } else if (tgc instanceof TMLADDecrypt) {
//                tmlexecc = new TMLExecC("decrypt_" + ((TMLADDecrypt) tgc).securityContext, tgc);
//                activity.addElement(tmlexecc);
//                SecurityPattern sp = securityPatterns.get(((TMLADDecrypt) tgc).securityContext);
//                if (sp == null) {
//                    //Throw error for missing security pattern
//                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Security Pattern " + ((TMLADDecrypt) tgc).securityContext + " not found");
//                    ce.setTDiagramPanel(tadp);
//                    ce.setTGComponent(tgc);
//                    checkingErrors.add(ce);
//                } else {
//                    tmlexecc.securityPattern = sp;
//                    tmlexecc.setAction(Integer.toString(sp.decTime));
//                    ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                    listE.addCor(tmlexecc, tgc);
//                    tmlm.securityTaskMap.get(sp).add(tmltask);
//                }
//
//            } else if (tgc instanceof TMLADExecC) {
//                tmlexecc = new TMLExecC("execc", tgc);
//                tmlexecc.setValue(((TMLADExecC) tgc).getDelayValue());
//                tmlexecc.setAction(modifyString(((TMLADExecC) tgc).getDelayValue()));
//                activity.addElement(tmlexecc);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlexecc, tgc);
//
//            } else if (tgc instanceof TMLADExecCInterval) {
//                tmlexecci = new TMLExecCInterval("execci", tgc);
//                tmlexecci.setMinDelay(modifyString(((TMLADExecCInterval) tgc).getMinDelayValue()));
//                tmlexecci.setMaxDelay(modifyString(((TMLADExecCInterval) tgc).getMaxDelayValue()));
//                activity.addElement(tmlexecci);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlexecci, tgc);
//
//            } else if (tgc instanceof TMLADDelay) {
//                tmldelay = new TMLDelay("d-delay", tgc);
//                tmldelay.setMinDelay(modifyString(((TMLADDelay) tgc).getDelayValue()));
//                tmldelay.setMaxDelay(modifyString(((TMLADDelay) tgc).getDelayValue()));
//                tmldelay.setUnit(((TMLADDelay) tgc).getUnit());
//                activity.addElement(tmldelay);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmldelay, tgc);
//
//            } else if (tgc instanceof TMLADDelayInterval) {
//                tmldelay = new TMLDelay("nd-delay", tgc);
//                tmldelay.setMinDelay(modifyString(((TMLADDelayInterval) tgc).getMinDelayValue()));
//                tmldelay.setMaxDelay(modifyString(((TMLADDelayInterval) tgc).getMaxDelayValue()));
//                tmldelay.setUnit(((TMLADDelayInterval) tgc).getUnit());
//                activity.addElement(tmldelay);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmldelay, tgc);
//
//            } else if (tgc instanceof TMLADForLoop) {
//                tmlforloop = new TMLForLoop("loop", tgc);
//                tmlforloop.setInit(modifyString(((TMLADForLoop) tgc).getInit()));
//                tmp = ((TMLADForLoop) tgc).getCondition();
//                /*if (tmp.trim().length() == 0) {
//                  tmp = "true";
//                  }*/
//                tmlforloop.setCondition(modifyString(tmp));
//                tmlforloop.setIncrement(modifyActionString(((TMLADForLoop) tgc).getIncrement()));
//
//                activity.addElement(tmlforloop);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlforloop, tgc);
//
//            } else if (tgc instanceof TMLADForStaticLoop) {
//                sl = "loop__" + staticLoopIndex;
//                tt = new TMLType(TMLType.NATURAL);
//                tmlt = new TMLAttribute(sl, tt);
//                tmlt.initialValue = "0";
//                tmltask.addAttribute(tmlt);
//                tmlforloop = new TMLForLoop(sl, tgc);
//                tmlforloop.setInit(sl + " = 0");
//                tmlforloop.setCondition(sl + "<" + modifyString(tgc.getValue()));
//                tmlforloop.setIncrement(sl + " = " + sl + " + 1");
//                activity.addElement(tmlforloop);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlforloop, tgc);
//                staticLoopIndex++;
//
//            } else if (tgc instanceof TMLADForEverLoop) {
//                /*sl = "loop__" + staticLoopIndex;
//                  tt = new TMLType(TMLType.NATURAL);
//                  tmlt = new TMLAttribute(sl, tt);
//                  tmlt.initialValue = "0";
//                  tmltask.addAttribute(tmlt);*/
//                tmlforloop = new TMLForLoop("infiniteloop", tgc);
//                tmlforloop.setInit("");
//                tmlforloop.setCondition("");
//                tmlforloop.setIncrement("");
//                tmlforloop.setInfinite(true);
//                activity.addElement(tmlforloop);
//                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                listE.addCor(tmlforloop, tgc);
//                staticLoopIndex++;
//
//                tmlstopstate = new TMLStopState("Stop after infinite loop", null);
//                activity.addElement(tmlstopstate);
//                tmlforloop.addNext(tmlstopstate);
//
//            } else if (tgc instanceof TMLADSequence) {
//                tmlsequence = new TMLSequence("seq", tgc);
//                activity.addElement(tmlsequence);
//                listE.addCor(tmlsequence, tgc);
//
//            } else if (tgc instanceof TMLADUnorderedSequence) {
//                tmlrsequence = new TMLRandomSequence("rseq", tgc);
//                activity.addElement(tmlrsequence);
//                listE.addCor(tmlrsequence, tgc);
//
//            } else if (tgc instanceof TMLADReadChannel) {
//                // Get the channel
//                //TMLADReadChannel rd = (TMLADReadChannel) tgc;
//                channel = tmlm.getChannelByName(getFromTable(tmltask, ((TMLADReadChannel) tgc).getChannelName()));
//                /*if (rd.isAttacker()){
//                    channel = tmlm.getChannelByName(getAttackerChannel(((TMLADReadChannel)tgc).getChannelName()));
//				}*/
//                if (channel == null) {
//                    if (Conversion.containsStringInList(removedChannels, ((TMLADReadChannel) tgc).getChannelName())) {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADReadChannel) tgc).getChannelName() + " has been removed because the corresponding channel is not taken into account");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        warnings.add(ce);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                        activity.addElement(new TMLJunction("void junction", tgc));
//                    } else {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADReadChannel) tgc).getChannelName() + " is an unknown channel");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                        checkingErrors.add(ce);
//                    }
//
//                } else {
//                    tmlreadchannel = new TMLReadChannel("read channel", tgc);
//                    tmlreadchannel.setNbOfSamples(modifyString(((TMLADReadChannel) tgc).getSamplesValue()));
//                    tmlreadchannel.setEncForm(((TMLADReadChannel) tgc).getEncForm());               
//                    tmlreadchannel.addChannel(channel);
//                    //security pattern
//                    if (securityPatterns.get(((TMLADReadChannel) tgc).getSecurityContext()) != null) {
//                        tmlreadchannel.securityPattern = securityPatterns.get(((TMLADReadChannel) tgc).getSecurityContext());
//                        //NbOfSamples will increase due to extra overhead from MAC
//                        int cur = 1;
//                        try {
//                            cur = Integer.valueOf(modifyString(((TMLADReadChannel) tgc).getSamplesValue()));
//                        } catch (NumberFormatException e) {
//                        } catch (NullPointerException e) {
//                        }
//                        int add = Integer.valueOf(tmlreadchannel.securityPattern.overhead);
//                        if (!tmlreadchannel.securityPattern.nonce.equals("")) {
//                            SecurityPattern nonce = securityPatterns.get(tmlreadchannel.securityPattern.nonce);
//                            if (nonce != null) {
//                                add = Integer.valueOf(nonce.overhead);
//                            }
//                        }
//                        cur = cur + add;
//                        tmlreadchannel.setNbOfSamples(Integer.toString(cur));
//                    } else if (!((TMLADReadChannel) tgc).getSecurityContext().isEmpty()) {
//                        //Throw error for missing security pattern
//                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Security Pattern " + ((TMLADReadChannel) tgc).getSecurityContext() + " not found");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                    }
//                    if (tmltask.isAttacker()) {
//                        tmlreadchannel.setAttacker(true);
//                    }
//                    activity.addElement(tmlreadchannel);
//                    ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                    listE.addCor(tmlreadchannel, tgc);
//                }
//            } else if (tgc instanceof TMLADSendEvent) {
//                event = tmlm.getEventByName(getFromTable(tmltask, ((TMLADSendEvent) tgc).getEventName()));
//                if (event == null) {
//                    if (Conversion.containsStringInList(removedEvents, ((TMLADSendEvent) tgc).getEventName())) {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADSendEvent) tgc).getEventName() + " has been removed because the corresponding event is not taken into account");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        warnings.add(ce);
//                        activity.addElement(new TMLJunction("void junction", tgc));
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                    } else {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADSendEvent) tgc).getEventName() + " is an unknown event");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                    }
//                } else {
//                    tmlsendevent = new TMLSendEvent("send event", tgc);
//                    tmlsendevent.setEvent(event);
//
//                    for (int i = 0; i < ((TMLADSendEvent) tgc).realNbOfParams(); i++) {
//                        tmp = modifyString(((TMLADSendEvent) tgc).getRealParamValue(i));
//                        Vector<String> allVariables = tmltask.getAllAttributesStartingWith(tmp + "__");
//                        if (allVariables.size() > 0) {
//                            for (int k = 0; k < allVariables.size(); k++) {
//                                //TraceManager.addDev("Adding record: " + allVariables.get(k));
//                                tmlsendevent.addParam(allVariables.get(k));
//                            }
//                        } else {
//                            //TraceManager.addDev("Adding param: " + tmp);
//                            tmlsendevent.addParam(tmp);
//                        }
//                    }
//                    if (event.getNbOfParams() != tmlsendevent.getNbOfParams()) {
//                        //TraceManager.addDev("ERROR : event#:" + event.getNbOfParams() + " sendevent#:" + tmlsendevent.getNbOfParams());
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADSendEvent) tgc).getEventName() + ": wrong number of parameters");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                    } else {
//                        activity.addElement(tmlsendevent);
//                        listE.addCor(tmlsendevent, tgc);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                    }
//                }
//
//            } else if (tgc instanceof TMLADSendRequest) {
//                request = tmlm.getRequestByName(getFromTable(tmltask, ((TMLADSendRequest) tgc).getRequestName()));
//                if (request == null) {
//                    if (Conversion.containsStringInList(removedRequests, ((TMLADSendRequest) tgc).getRequestName())) {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADSendRequest) tgc).getRequestName() + " has been removed because the corresponding request is not taken into account");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        warnings.add(ce);
//                        activity.addElement(new TMLJunction("void junction", tgc));
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                    } else {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADSendRequest) tgc).getRequestName() + " is an unknown request");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                    }
//                } else {
//                    tmlsendrequest = new TMLSendRequest("send request", tgc);
//                    tmlsendrequest.setRequest(request);
//                    for (int i = 0; i < ((TMLADSendRequest) tgc).realNbOfParams(); i++) {
//                        tmp = modifyString(((TMLADSendRequest) tgc).getRealParamValue(i));
//                        Vector<String> allVariables = tmltask.getAllAttributesStartingWith(tmp + "__");
//                        if (allVariables.size() > 0) {
//                            for (int k = 0; k < allVariables.size(); k++) {
//                                TraceManager.addDev("Adding record: " + allVariables.get(k));
//                                tmlsendrequest.addParam(allVariables.get(k));
//                                request.addParamName(allVariables.get(k));
//                            }
//                        } else {
//                            //TraceManager.addDev("Adding param: " + tmp);
//                            tmlsendrequest.addParam(tmp);
//                            request.addParamName(tmp);
//                        }
//                    }
//                    if (request.getNbOfParams() != tmlsendrequest.getNbOfParams()) {
//                        //TraceManager.addDev("ERROR : request#:" + request.getNbOfParams() + " sendrequest#:" + tmlsendrequest.getNbOfParams());
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADSendRequest) tgc).getRequestName() + ": wrong number of parameters");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                    } else {
//                        activity.addElement(tmlsendrequest);
//                        listE.addCor(tmlsendrequest, tgc);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                    }
//                }
//
//            } else if (tgc instanceof TMLADReadRequestArg) {
//                request = tmlm.getRequestToMe(tmltask);
//                if (request == null) {
//                    UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "This task is not requested: cannot use \"reading request arg\" operator");
//                    ce.setTDiagramPanel(tadp);
//                    ce.setTGComponent(tgc);
//                    checkingErrors.add(ce);
//                    ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                } else {
//                    tmlaction = new TMLActionState("action reading args", tgc);
//                    String act = "";
//                    int cpt = 1;
//                    for (int i = 0; i < ((TMLADReadRequestArg) tgc).realNbOfParams(); i++) {
//                        tmp = modifyString(((TMLADReadRequestArg) tgc).getRealParamValue(i));
//                        Vector<String> allVariables = tmltask.getAllAttributesStartingWith(tmp + "__");
//
//                        if (allVariables.size() > 0) {
//                            for (int k = 0; k < allVariables.size(); k++) {
//                                //TraceManager.addDev("Adding record: " + allVariables.get(k));
//                                if (cpt != 1) {
//                                    act += "$";
//                                }
//                                act += allVariables.get(k) + " = arg" + cpt + "__req";
//                                cpt++;
//                            }
//                        } else {
//                            //TraceManager.addDev("Adding param: " + tmp);
//                            if (cpt != 1) {
//                                act += "$";
//                            }
//                            act += tmp + " = arg" + cpt + "__req";
//                            cpt++;
//                        }
//                    }
//                    if (request.getNbOfParams() != (cpt - 1)) {
//                        //TraceManager.addDev("ERROR : request#:" + request.getNbOfParams() + " read request arg#:" + (cpt-1));
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Wrong number of parameters in \"reading request arg\" operator");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                    } else {
//                        //TraceManager.addDev("Adding action = " + act);
//                        tmlaction.setAction(act);
//                        activity.addElement(tmlaction);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                        listE.addCor(tmlaction, tgc);
//                    }
//
//
//                }
//
//            } else if (tgc instanceof TMLADStopState) {
//                tmlstopstate = new TMLStopState("stop state", tgc);
//                activity.addElement(tmlstopstate);
//                listE.addCor(tmlstopstate, tgc);
//
//            } else if (tgc instanceof TMLADNotifiedEvent) {
//                event = tmlm.getEventByName(getFromTable(tmltask, ((TMLADNotifiedEvent) tgc).getEventName()));
//                if (event == null) {
//                    if (removedEvents.size() > 0) {
//                        if (Conversion.containsStringInList(removedEvents, ((TMLADNotifiedEvent) tgc).getEventName())) {
//                            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADNotifiedEvent) tgc).getEventName() + " has been removed because the corresponding event is not taken into account");
//                            ce.setTDiagramPanel(tadp);
//                            ce.setTGComponent(tgc);
//                            warnings.add(ce);
//                            activity.addElement(new TMLJunction("void junction", tgc));
//                            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                        } else {
//                            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADNotifiedEvent) tgc).getEventName() + " is an unknown event");
//                            ce.setTDiagramPanel(tadp);
//                            ce.setTGComponent(tgc);
//                            checkingErrors.add(ce);
//                            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                        }
//                    } else {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADNotifiedEvent) tgc).getEventName() + " is an unknown event");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                    }
//                } else {
//                    event.setNotified(true);
//                    tmlnotifiedevent = new TMLNotifiedEvent("notified event", tgc);
//                    tmlnotifiedevent.setEvent(event);
//                    tmlnotifiedevent.setVariable(modifyString(((TMLADNotifiedEvent) tgc).getVariable()));
//                    activity.addElement(tmlnotifiedevent);
//                    ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                    listE.addCor(tmlnotifiedevent, tgc);
//                }
//
//            } else if (tgc instanceof TMLADWaitEvent) {
//                event = tmlm.getEventByName(getFromTable(tmltask, ((TMLADWaitEvent) tgc).getEventName()));
//                if (event == null) {
//                    if (removedEvents.size() > 0) {
//                        if (Conversion.containsStringInList(removedEvents, ((TMLADWaitEvent) tgc).getEventName())) {
//                            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADWaitEvent) tgc).getEventName() + " has been removed because the corresponding event is not taken into account");
//                            ce.setTDiagramPanel(tadp);
//                            ce.setTGComponent(tgc);
//                            warnings.add(ce);
//                            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                            activity.addElement(new TMLJunction("void junction", tgc));
//                        } else {
//                            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADWaitEvent) tgc).getEventName() + " is an unknown event");
//                            ce.setTDiagramPanel(tadp);
//                            ce.setTGComponent(tgc);
//                            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                            checkingErrors.add(ce);
//                        }
//                    } else {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADWaitEvent) tgc).getEventName() + " is an unknown event");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                        checkingErrors.add(ce);
//                    }
//                } else {
//                    //TraceManager.addDev("Nb of param of event:" + event.getNbOfParams());
//                    tmlwaitevent = new TMLWaitEvent("wait event", tgc);
//                    tmlwaitevent.setEvent(event);
//                    for (int i = 0; i < ((TMLADWaitEvent) tgc).realNbOfParams(); i++) {
//                        tmp = modifyString(((TMLADWaitEvent) tgc).getRealParamValue(i));
//                        Vector<String> allVariables = tmltask.getAllAttributesStartingWith(tmp + "__");
//                        if (allVariables.size() > 0) {
//                            for (int k = 0; k < allVariables.size(); k++) {
//                                //TraceManager.addDev("Adding record: " + allVariables.get(k));
//                                tmlwaitevent.addParam(allVariables.get(k));
//                            }
//                        } else {
//                            //TraceManager.addDev("Adding param: " + tmp);
//                            tmlwaitevent.addParam(tmp);
//                        }
//                    }
//                    if (event.getNbOfParams() != tmlwaitevent.getNbOfParams()) {
//                        //TraceManager.addDev("ERROR : event#:" + event.getNbOfParams() + " waitevent#:" + tmlwaitevent.getNbOfParams());
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADWaitEvent) tgc).getEventName() + ": wrong number of parameters");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                    } else {
//                        activity.addElement(tmlwaitevent);
//                        listE.addCor(tmlwaitevent, tgc);
//                        ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                    }
//
//                }
//
//            } else if (tgc instanceof TMLADWriteChannel) {
//                // Get channels
//                //TMLADWriteChannel wr = (TMLADWriteChannel) tgc;
//                channels = ((TMLADWriteChannel) tgc).getChannelsByName();
//                boolean error = false;
//                for (int i = 0; i < channels.length; i++) {
//                    //TraceManager.addDev("Getting from table " + tmltask.getName() + "/" +channels[i]);
//                    channel = tmlm.getChannelByName(getFromTable(tmltask, channels[i]));
//                    if (channel == null) {
//                        if (Conversion.containsStringInList(removedChannels, ((TMLADWriteChannel) tgc).getChannelName(i))) {
//                            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADWriteChannel) tgc).getChannelName(i) + " has been removed because the corresponding channel is not taken into account");
//                            ce.setTDiagramPanel(tadp);
//                            ce.setTGComponent(tgc);
//                            warnings.add(ce);
//                            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                            activity.addElement(new TMLJunction("void junction", tgc));
//                        } else {
//                            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADWriteChannel) tgc).getChannelName(i) + " is an unknown channel");
//                            ce.setTDiagramPanel(tadp);
//                            ce.setTGComponent(tgc);
//                            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
//                            checkingErrors.add(ce);
//                        }
//                        error = true;
//
//                    }
//                }
//                if (!error) {
//                    tmlwritechannel = new TMLWriteChannel("write channel", tgc);
//                    tmlwritechannel.setNbOfSamples(modifyString(((TMLADWriteChannel) tgc).getSamplesValue()));
//                    tmlwritechannel.setEncForm(((TMLADWriteChannel) tgc).getEncForm());
//                    for (int i = 0; i < channels.length; i++) {
//                        channel = tmlm.getChannelByName(getFromTable(tmltask, channels[i]));
//                        tmlwritechannel.addChannel(channel);
//                    }
//                    //if (wr.isAttacker()){
//                    //channel = tmlm.getChannelByName(getAttackerChannel(channels[0]));
//                    //tmlwritechannel.addChannel(channel);
//                    //}
//                    //add sec pattern
//                    if (securityPatterns.get(((TMLADWriteChannel) tgc).getSecurityContext()) != null) {
//                        tmlwritechannel.securityPattern = securityPatterns.get(((TMLADWriteChannel) tgc).getSecurityContext());
//                        int cur = Integer.valueOf(modifyString(((TMLADWriteChannel) tgc).getSamplesValue()));
//                        int add = Integer.valueOf(tmlwritechannel.securityPattern.overhead);
//                        if (!tmlwritechannel.securityPattern.nonce.equals("")) {
//                            SecurityPattern nonce = securityPatterns.get(tmlwritechannel.securityPattern.nonce);
//                            if (nonce != null) {
//                                add = Integer.valueOf(nonce.overhead);
//                            }
//                        }
//                        cur = cur + add;
//                        tmlwritechannel.setNbOfSamples(Integer.toString(cur));
//                    } else if (!((TMLADWriteChannel) tgc).getSecurityContext().isEmpty()) {
//                        //Throw error for missing security pattern
//                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Security Pattern " + ((TMLADWriteChannel) tgc).getSecurityContext() + " not found");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                    }
//                    if (tmltask.isAttacker()) {
//                        tmlwritechannel.setAttacker(true);
//                    }
//                    activity.addElement(tmlwritechannel);
//                    ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
//                    listE.addCor(tmlwritechannel, tgc);
//                }
//            }
//        }
//
//        // Interconnection between elements
//        TGConnectorTMLAD tgco;
//        TGConnectingPoint p1, p2;
//        TMLActivityElement ae1, ae2;
//        TGComponent tgc1, tgc2, tgc3;
//        int j, index;
//
//        iterator = list.listIterator();
//        while (iterator.hasNext()) {
//            tgc = iterator.next();
//            if (tgc instanceof TGConnectorTMLAD) {
//                tgco = (TGConnectorTMLAD) tgc;
//                p1 = tgco.getTGConnectingPointP1();
//                p2 = tgco.getTGConnectingPointP2();
//
//                // Identification of connected components
//                tgc1 = null;
//                tgc2 = null;
//                for (j = 0; j < list.size(); j++) {
//                    tgc3 = list.get(j);
//                    if (tgc3.belongsToMe(p1)) {
//                        tgc1 = tgc3;
//                    }
//                    if (tgc3.belongsToMe(p2)) {
//                        tgc2 = tgc3;
//                    }
//                }
//
//                // Connecting tml modeling components
//                if ((tgc1 != null) && (tgc2 != null)) {
//                    //ADComponent ad1, ad2;
//                    ae1 = activity.findReferenceElement(tgc1);
//                    ae2 = activity.findReferenceElement(tgc2);
//
//                    if ((ae1 != null) && (ae2 != null)) {
//                        //Special case if "for loop" or if "choice"
//
//                        if (ae1 instanceof TMLForLoop) {
//                            index = tgc1.indexOf(p1) - 1;
//                            if (index == 0) {
//                                ae1.addNext(0, ae2);
//                            } else {
//                                ae1.addNext(ae2);
//                            }
//
//                        } else if (ae1 instanceof TMLChoice) {
//                            index = tgc1.indexOf(p1) - 1;
//                            //TraceManager.addDev("Adding next:" + ae2);
//                            ae1.addNext(ae2);
//                            //TraceManager.addDev("Adding guard:" + ((TMLADChoice)tgc1).getGuard(index));
//                            ((TMLChoice) ae1).addGuard(modifyString(((TMLADChoice) tgc1).getGuard(index)));
//
//                        } else if (ae1 instanceof TMLSequence) {
//                            index = tgc1.indexOf(p1) - 1;
//                            ((TMLSequence) ae1).addIndex(index);
//                            ae1.addNext(ae2);
//                            //TraceManager.addDev("Adding " + ae2 + " at index " + index);
//
//                        } else if (ae1 instanceof TMLRandomSequence) {
//                            index = tgc1.indexOf(p1) - 1;
//                            ((TMLRandomSequence) ae1).addIndex(index);
//                            ae1.addNext(ae2);
//                            //TraceManager.addDev("Adding " + ae2 + " at index " + index);
//
//                        } else {
//                            ae1.addNext(ae2);
//                        }
//                    }
//                }
//            }
//        }
//
//
//        // Check that each "for" has two nexts
//        // Check that TMLChoice have compatible guards
//        // Check TML select evts
//        iterator = list.listIterator();
//        while (iterator.hasNext()) {
//            tgc = iterator.next();
//
//            if ((tgc instanceof TMLADForLoop) || (tgc instanceof TMLADForStaticLoop)) {
//                ae1 = activity.findReferenceElement(tgc);
//                if (ae1 != null) {
//                    if (ae1.getNbNext() != 2) {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formatted for loop: a loop must have an internal behavior, and an exit behavior ");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                    }
//                }
//            } else if (tgc instanceof TMLADChoice) {
//                tmlchoice = (TMLChoice) (activity.findReferenceElement(tgc));
//                tmlchoice.orderGuards();
//
//                int nbNonDeter = tmlchoice.nbOfNonDeterministicGuard();
//                int nbStocha = tmlchoice.nbOfStochasticGuard();
//                if ((nbNonDeter > 0) && (nbStocha > 0)) {
//                    UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formatted choice: it has both non-determinitic and stochastic guards");
//                    ce.setTDiagramPanel(tadp);
//                    ce.setTGComponent(tgc);
//                    checkingErrors.add(ce);
//                }
//                int nb = Math.max(nbNonDeter, nbStocha);
//                if (nb > 0) {
//                    nb = nb + tmlchoice.nbOfElseAndAfterGuards();
//                    if (nb != tmlchoice.getNbGuard()) {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formatted choice: it has both non-determinitic/ stochastic and regular guards)");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                    }
//                }
//
//                if (tmlchoice.nbOfNonDeterministicGuard() > 0) {
//                    /*if (!rndAdded) {
//                      TMLAttribute tmlt = new TMLAttribute("rnd__0", new TMLType(TMLType.NATURAL));
//                      tmlt.initialValue = "";
//                      tmltask.addAttribute(tmlt);
//                      rndAdded = true;
//                      }*/
//                }
//                if (tmlchoice.hasMoreThanOneElse()) {
//                    UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Choice should have only one [else] guard");
//                    ce.setTDiagramPanel(tadp);
//                    ce.setTGComponent(tgc);
//                    checkingErrors.add(ce);
//                } else if ((index = tmlchoice.getElseGuard()) > -1) {
//                    index = tmlchoice.getElseGuard();
//                    if (index == 0) {
//                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Choice should have a regular guard");
//                        ce.setTDiagramPanel(tadp);
//                        ce.setTGComponent(tgc);
//                        checkingErrors.add(ce);
//                    }
//                }
//                if (tmlchoice.hasMoreThanOneAfter()) {
//                    UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Choice should have only one [after] guard");
//                    ce.setTDiagramPanel(tadp);
//                    ce.setTGComponent(tgc);
//                    checkingErrors.add(ce);
//                }
//            }
//            if (tgc instanceof TMLADSelectEvt) {
//                tmlselectevt = (TMLSelectEvt) (activity.findReferenceElement(tgc));
//                if (!tmlselectevt.isARealSelectEvt()) {
//                    UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "'Select events'  should be followed by only event receiving operators");
//                    ce.setTDiagramPanel(tadp);
//                    ce.setTGComponent(tgc);
//                    checkingErrors.add(ce);
//                }
//            }
//
//        }
//
//        // Sorting nexts elements of Sequence
//        for (j = 0; j < activity.nElements(); j++) {
//            ae1 = activity.get(j);
//            if (ae1 instanceof TMLSequence) {
//                ((TMLSequence) ae1).sortNexts();
//            }
//            if (ae1 instanceof TMLRandomSequence) {
//                ((TMLRandomSequence) ae1).sortNexts();
//            }
//        }
//    }


    public TMLMapping<TGComponent> translateToTMLMapping() {
        tmlm = new TMLModeling<>(true);
        archi = new TMLArchitecture();  //filled by makeArchitecture
        map = new TMLMapping<>(tmlm, archi, false);
        checkingErrors = new LinkedList<CheckingError>();
        warnings = new LinkedList<CheckingError>();
        //listE = new CorrespondanceTGElement();

        //TraceManager.addDev("Making architecture");
        makeArchitecture();     //fills archi
        //TraceManager.addDev("Making TML modeling");
        if (!makeTMLModeling()) {
            return null;
        }
        //TraceManager.addDev("Making mapping");
        makeMapping();  //fills map

        TMLSyntaxChecking syntax = new TMLSyntaxChecking(map, true);
        syntax.checkSyntax();

        int type;
        TGComponent tgc;

        if (syntax.hasErrors() > 0) {
            for (TMLError error : syntax.getErrors()) {
                //TraceManager.addDev("Adding checking error");
                if (error.type == TMLError.ERROR_STRUCTURE) {
                    type = CheckingError.STRUCTURE_ERROR;
                } else {
                    type = CheckingError.BEHAVIOR_ERROR;
                }
                tgc = listE.getTG(error.element);
                if (tgc != null) {
                    UICheckingError ce = new UICheckingError(type, error.message);
                    ce.setTDiagramPanel(tgc.getTDiagramPanel());
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                } else {
                    TMLCheckingError ce = new TMLCheckingError(type, error.message);
                    ce.setTMLTask(error.task);
                    checkingErrors.add(ce);
                }
            }
        }


        processAttackerScenario();
        map.setCorrespondanceList(listE);
        //  map.securityPatterns.addAll(securityPatterns.keySet());
        //TraceManager.addDev("Making TMLCPLib");
        makeTMLCPLib();

        //TraceManager.addDev("<--- TML modeling:");
        //TraceManager.addDev("TML: " + tmlm.toString());
        //TraceManager.addDev("End of TML modeling --->");
        /*if (!tmlcdp.getMainGUI().getTitleAt(tmlap).contains("_enc")){
          autoSecure();
          }
          autoMapKeys();*/
        removeActionsWithRecords();
        if (map.firewall) {
            tmlap.getMainGUI().gtm.drawFirewall(map);
        }
        return map;     // the data structure map is returned to CheckSyntaxTMLMapping in GTURTLEModeling
    }

    // public SystemCAMSPanel<TGComponent> translateToSystemCAMS() { //ajout CD 04/07 FIXME
	/*tous est a changé et a créé ici*/
    // tmlm = new TMLModeling<>(true);
    // archi = new TMLArchitecture();  //filled by makeArchitecture
    // cams = new TMLSystemCAMS<>(tmlm, archi, false);
    // cams.tmlscp = tmlscp;
    // checkingErrors = new LinkedList<CheckingError> ();
    // warnings = new LinkedList<CheckingError> ();

    // TraceManager.addDev("Making architecture");
    // makeArchitecture();     //fills archi
    // TraceManager.addDev("Making TML modeling");
    // if (!makeTMLModeling()) {
    //     return null;
    // }
    // TraceManager.addDev("Making SystemC-AMS");
    // makeMapping();  //fills cams
    // cams.listE = listE;
    // TraceManager.addDev("Making TMLSCPlib");
    // makeTMLCPLib();

    // removeActionsWithRecords();
    // cams.setTMLDesignPanel(this.tmlcdp);//a ajouter
    // if (cams.firewall){ // j'espère pas besoin de tous ca
    //     tmlscp.getMainGUI().gtm.drawFirewall(cams);
    // }
    //  return cams;
    // }

    public void processAttackerScenario() {
        //Scan tasks and activity diagrams for attacker read/write channels
        for (TMLTask task : tmlm.getTasks()) {
            if (task.isAttacker()) {
                TMLActivity act = task.getActivityDiagram();
                List<TMLActivityElement> toRemove = new ArrayList<TMLActivityElement>();
                for (TMLActivityElement elem : act.getElements()) {
                    if (elem instanceof TMLActivityElementChannel) {
                        TMLActivityElementChannel elemChannel = (TMLActivityElementChannel) elem;
                        if (elemChannel.isAttacker()) {
                            TMLChannel chan = elemChannel.getChannel(0);
                            if (!map.isAttackerAccessible(chan)) {
                                toRemove.add(elem);
                                //Remove read/writechannel
                            }
                        }
                    }
                }
                for (TMLActivityElement elem : toRemove) {
                    TMLExecI exec = new TMLExecI("100", elem.getReferenceObject());
                    exec.setAction("100");
                    exec.setValue("100");
                    act.replaceElement(elem, exec);
                }
                //
            }
        }

    }

    public TMLCP translateToTMLCPDataStructure(String _cpName) {
        tmlcp = new TMLCP(_cpName);
        checkingErrors = new LinkedList<CheckingError>();
        warnings = new LinkedList<CheckingError>();
        //listE = new CorrespondanceTGElement();

        if (tmlcpp != null) {
            try {
                //TraceManager.addDev( "Making Communication Pattern data structure to check the syntax" );
                makeCPDataStructure();  //fill the data structure tmlcp
            } catch (MalformedTMLDesignException mtmlde) {
                TraceManager.addDev("Modeling error: " + mtmlde.getMessage());
            }
        }

        //TraceManager.addDev( "About to check the syntax of CPs" );
        TMLCPSyntaxChecking syntax = new TMLCPSyntaxChecking(tmlcp);
        syntax.checkSyntax();
        //Takes the data structure tmlcp passed to the constructor and checks the syntax of the components of a cp. If there are errors (these
        //are filled inside the class syntax), then CheckingErrors are filled according to the errors in class syntax

        int type;
        TGComponent tgc;

        if (syntax.hasErrors() > 0) {
            for (TMLCPError error : syntax.getErrors()) {
                if (error.type == TMLCPError.ERROR_STRUCTURE) {
                    type = CheckingError.STRUCTURE_ERROR;
                } else {
                    type = CheckingError.BEHAVIOR_ERROR;
                }

                tgc = listE.getTG(error.element);
                if (tgc != null) {
                    UICheckingError ce = new UICheckingError(type, error.message);
                    ce.setTDiagramPanel(tgc.getTDiagramPanel());
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                } else {
                    TMLCheckingError ce = new TMLCheckingError(type, error.message);
                    ce.setTMLTask(error.task);
                    checkingErrors.add(ce);
                }
            }
        }

        /*makeCPDataStructure();
          if (!makeTMLModeling()) {
          return null;
          }
          TraceManager.addDev("Making mapping");
          makeCPMapping();      //Inspect the architecture Deployment Diagram to retrieve mapping information, that is now located in one
          //place only: the architecture DD

          // Syntax has been checked -> splitting ads
          // The splitting works only if there is no other operations than sequences and references to ADs/SDs
          // between forks and joins
    	
    	// Issue #69; Unused TMLCPJunction
          tmlcp.splitADs();

          TraceManager.addDev("<--- TMLCP modeling:");
          TraceManager.addDev("TMLCP: " + tmlcp.toString());
          TraceManager.addDev("End of TMLCP modeling --->");

          removeActionsWithRecords();*/

        return tmlcp;
    }

    private boolean nameInUse(List<String> _names, String _name) {
        for (String s : _names) {
            if (s.equals(_name)) {
                return true;
            }
        }
        return false;
    }

    private void makeArchitecture() {
        archi.setMasterClockFrequency(tmlap.tmlap.getMasterClockFrequency());

        if (nodesToTakeIntoAccount == null) {
            components = tmlap.tmlap.getComponentList();
        } else {
            // DB: TODO this is a bug. Stuff in nodesToTakeIntoAccount are not components
            components = nodesToTakeIntoAccount;
        }
        Iterator<? extends TGComponent> iterator = components.listIterator();
        TGComponent tgc;

        TMLArchiCPUNode node;
        TMLArchiFPGANode fpgaNode;
        TMLArchiHWANode hwanode;
        TMLArchiBUSNode busnode;
        TMLArchiVGMNNode vgmnnode;
        TMLArchiCrossbarNode crossbarnode;
        TMLArchiBridgeNode bridgenode;
        TMLArchiMemoryNode memorynode;
        TMLArchiDMANode dmanode;
        TMLArchiFirewallNode firewallnode;
        TMLArchiRouterNode routerNode;
        HwCPU cpu;
        HwFPGA fpga;
        HwA hwa;
        HwBus bus;
        HwVGMN vgmn;
        HwCrossbar crossbar;
        HwBridge bridge;
        HwRouter router;
        HwMemory memory;
        HwDMA dma;

        List<String> names = new ArrayList<String>();

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLArchiCPUNode) {
                node = (TMLArchiCPUNode) tgc;
                if (nameInUse(names, node.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + node.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(node);
                    checkingErrors.add(ce);
                } else {
                    names.add(node.getName());
                    cpu = new HwCPU(node.getName());
                    cpu.nbOfCores = node.getNbOfCores();
                    cpu.byteDataSize = node.getByteDataSize();
                    cpu.pipelineSize = node.getPipelineSize();
                    cpu.goIdleTime = node.getGoIdleTime();
                    cpu.maxConsecutiveIdleCycles = node.getMaxConsecutiveIdleCycles();
                    cpu.taskSwitchingTime = node.getTaskSwitchingTime();
                    cpu.branchingPredictionPenalty = node.getBranchingPredictionPenalty();
                    cpu.cacheMiss = node.getCacheMiss();
                    cpu.schedulingPolicy = node.getSchedulingPolicy();
                    cpu.sliceTime = node.getSliceTime();
                    cpu.execiTime = node.getExeciTime();
                    cpu.execcTime = node.getExeccTime();
                    cpu.clockRatio = node.getClockRatio();
                    cpu.MEC = node.getMECType();
                    cpu.encryption = node.getEncryption();
                    listE.addCor(cpu, node);
                    archi.addHwNode(cpu);
                    //TraceManager.addDev("CPU node added: " + cpu.getName());
                }
            }

            if (tgc instanceof TMLArchiFPGANode) {
                fpgaNode = (TMLArchiFPGANode) tgc;
                if (nameInUse(names, fpgaNode.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + fpgaNode.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(fpgaNode);
                    checkingErrors.add(ce);
                } else {
                    names.add(fpgaNode.getName());
                    fpga = new HwFPGA(fpgaNode.getName());
                    fpga.capacity = fpgaNode.getCapacity();
                    fpga.byteDataSize = fpgaNode.getByteDataSize();
                    fpga.mappingPenalty = fpgaNode.getMappingPenalty();
                    fpga.goIdleTime = fpgaNode.getGoIdleTime();
                    fpga.maxConsecutiveIdleCycles = fpgaNode.getMaxConsecutiveIdleCycles();
                    fpga.reconfigurationTime = fpgaNode.getReconfigurationTime();
                    fpga.execiTime = fpgaNode.getExeciTime();
                    fpga.execcTime = fpgaNode.getExeccTime();
                    fpga.clockRatio = fpgaNode.getClockRatio();

                    listE.addCor(fpga, fpgaNode);
                    archi.addHwNode(fpga);
                    //TraceManager.addDev("FPGA node added: " + fpgaNode.getName());
                }
            }

            if (tgc instanceof TMLArchiHWANode) {
                hwanode = (TMLArchiHWANode) tgc;
                if (nameInUse(names, hwanode.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + hwanode.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(hwanode);
                    checkingErrors.add(ce);
                } else {
                    names.add(hwanode.getName());
                    hwa = new HwA(hwanode.getName());
                    hwa.byteDataSize = hwanode.getByteDataSize();
                    hwa.execiTime = hwanode.getExeciTime();
                    hwa.clockRatio = hwanode.getClockRatio();
                    listE.addCor(hwa, hwanode);
                    archi.addHwNode(hwa);
                    //TraceManager.addDev("HWA node added: " + hwa.getName());
                }
            }

            if (tgc instanceof TMLArchiBUSNode) {
                busnode = (TMLArchiBUSNode) tgc;
                if (nameInUse(names, busnode.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + busnode.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(busnode);
                    checkingErrors.add(ce);
                } else {
                    names.add(busnode.getName());
                    bus = new HwBus(busnode.getName());
                    bus.byteDataSize = busnode.getByteDataSize();
                    bus.pipelineSize = busnode.getPipelineSize();
                    bus.arbitration = busnode.getArbitrationPolicy();
                    bus.clockRatio = busnode.getClockRatio();
                    bus.sliceTime = busnode.getSliceTime();
                    bus.privacy = busnode.getPrivacy();
                    listE.addCor(bus, busnode);
                    archi.addHwNode(bus);
                    //TraceManager.addDev("BUS node added:" + bus.getName());
                }
            }

            if (tgc instanceof TMLArchiVGMNNode) {
                vgmnnode = (TMLArchiVGMNNode) tgc;
                if (nameInUse(names, vgmnnode.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + vgmnnode.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(vgmnnode);
                    checkingErrors.add(ce);
                } else {
                    names.add(vgmnnode.getName());
                    vgmn = new HwVGMN(vgmnnode.getName());
                    vgmn.byteDataSize = vgmnnode.getByteDataSize();
                    /*   vgmn.pipelineSize = vgmnnode.getPipelineSize();
                         vgmn.arbitration = vgmnnode.getArbitrationPolicy();
                         vgmn.clockRatio = vgmnnode.getClockRatio();
                         vgmn.sliceTime = vgmnnode.getSliceTime();
                         vgmn.privacy = vgmnnode.getPrivacy();*/
                    listE.addCor(vgmn, vgmnnode);
                    archi.addHwNode(vgmn);
                    //TraceManager.addDev("VGMN node added:" + vgmn.getName());
                }
            }

            if (tgc instanceof TMLArchiCrossbarNode) {
                crossbarnode = (TMLArchiCrossbarNode) tgc;
                if (nameInUse(names, crossbarnode.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + crossbarnode.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(crossbarnode);
                    checkingErrors.add(ce);
                } else {
                    names.add(crossbarnode.getName());
                    crossbar = new HwCrossbar(crossbarnode.getName());
                    crossbar.byteDataSize = crossbarnode.getByteDataSize();
                    /*crossbar.pipelineSize = crossbarnode.getPipelineSize();
                      crossbar.arbitration = crossbarnode.getArbitrationPolicy();
                      crossbar.clockRatio = crossbarnode.getClockRatio();
                      crossbar.sliceTime = crossbarnode.getSliceTime();
                      crossbar.privacy = crossbarnode.getPrivacy();*/
                    listE.addCor(crossbar, crossbarnode);
                    archi.addHwNode(crossbar);
                    //TraceManager.addDev("Crossbar node added:" + crossbar.getName());
                }
            }

            if (tgc instanceof TMLArchiBridgeNode) {
                bridgenode = (TMLArchiBridgeNode) tgc;
                if (nameInUse(names, bridgenode.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + bridgenode.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(bridgenode);
                    checkingErrors.add(ce);
                } else {
                    names.add(bridgenode.getName());

                    bridge = new HwBridge(bridgenode.getName());
                    bridge.bufferByteSize = bridgenode.getBufferByteDataSize();
                    bridge.clockRatio = bridgenode.getClockRatio();
                    listE.addCor(bridge, bridgenode);
                    archi.addHwNode(bridge);
                    //TraceManager.addDev("Bridge node added:" + bridge.getName());
                }
            }

            if (tgc instanceof TMLArchiRouterNode) {
                routerNode = (TMLArchiRouterNode) tgc;
                if (nameInUse(names, routerNode.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + routerNode.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(routerNode);
                    checkingErrors.add(ce);
                } else {
                    names.add(routerNode.getName());
                    router = new HwRouter(routerNode.getName());
                    router.bufferByteSize = routerNode.getBufferByteDataSize();
                    router.clockRatio = routerNode.getClockRatio();
                    listE.addCor(router, routerNode);
                    archi.addHwNode(router);
                    //TraceManager.addDev("Router node added:" + router.getName());
                }
            }

            if (tgc instanceof TMLArchiFirewallNode) {
                firewallnode = (TMLArchiFirewallNode) tgc;
                if (nameInUse(names, firewallnode.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + firewallnode.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(firewallnode);
                    checkingErrors.add(ce);
                } else {
                    names.add(firewallnode.getName());
                    bridge = new HwBridge(firewallnode.getName());
                    bridge.isFirewall = true;
                    bridge.firewallRules = ((TMLArchiFirewallNode) tgc).getRules();
                    bridge.latency = ((TMLArchiFirewallNode) tgc).getLatency();
                    bridge.bufferByteSize = 1;
                    bridge.clockRatio = 1;
                    listE.addCor(bridge, firewallnode);
                    archi.addHwNode(bridge);
                    //TraceManager.addDev("Firewall node added:" + bridge.getName());
                }
            }


            if (tgc instanceof TMLArchiMemoryNode) {
                memorynode = (TMLArchiMemoryNode) tgc;
                if (nameInUse(names, memorynode.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + memorynode.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(memorynode);
                    checkingErrors.add(ce);
                } else {
                    names.add(memorynode.getName());
                    memory = new HwMemory(memorynode.getName());
                    memory.byteDataSize = memorynode.getByteDataSize();
                    memory.clockRatio = memorynode.getClockRatio();
                    memory.bufferType = memorynode.getBufferType();
                    listE.addCor(memory, memorynode);
                    archi.addHwNode(memory);
                    //TraceManager.addDev("Memory node added:" + memory.getName());
                }
            }

            if (tgc instanceof TMLArchiDMANode) {
                dmanode = (TMLArchiDMANode) tgc;
                if (nameInUse(names, dmanode.getName())) {
                    // Node with the same name
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two nodes have the same name: " + dmanode.getName());
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(dmanode);
                    checkingErrors.add(ce);
                } else {
                    names.add(dmanode.getName());
                    dma = new HwDMA(dmanode.getName());
                    dma.byteDataSize = dmanode.getByteDataSize();
                    dma.nbOfChannels = dmanode.getNbOfChannels();
                    dma.clockRatio = dmanode.getClockRatio();
                    listE.addCor(dma, dmanode);
                    archi.addHwNode(dma);
                    //TraceManager.addDev("************************************ DMA node added:" + dma.getName());
                }
            }
        }

        // Links between nodes
        TGComponent tgc1, tgc2;
        TGConnectingPoint p1, p2;
        TMLArchiConnectorNode connector;
        HwLink hwlink;
        HwNode originNode;

        iterator = tmlap.tmlap.getComponentList().listIterator();
        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLArchiConnectorNode) {
                connector = (TMLArchiConnectorNode) tgc;
                tgc1 = null;
                tgc2 = null;
                p1 = connector.getTGConnectingPointP1();
                p2 = connector.getTGConnectingPointP2();
                tgc1 = tgc.getTDiagramPanel().getComponentToWhichBelongs(p1);
                tgc2 = tgc.getTDiagramPanel().getComponentToWhichBelongs(p2);
                if ((tgc1 != null) && (tgc2 != null)) {
                    //TraceManager.addDev("Not null");
                    if (components.contains(tgc1) && components.contains(tgc2)) {
                        //TraceManager.addDev("Getting closer");

                        if (tgc2 instanceof TMLArchiBUSNode) {
                            originNode = listE.getHwNode(tgc1);
                            bus = (HwBus) (listE.getHwNode(tgc2));
                            if ((originNode != null) && (bus != null)) {
                                hwlink = new HwLink("link_" + originNode.getName() + "_to_" + bus.getName());
                                hwlink.setPriority(connector.getPriority());
                                hwlink.bus = bus;
                                hwlink.hwnode = originNode;
                                listE.addCor(hwlink, connector);
                                archi.addHwLink(hwlink);
                                //TraceManager.addDev("Link added");
                            }
                        }

                        // DG added VGMN and crossbar
                        if (tgc2 instanceof TMLArchiVGMNNode) {
                            originNode = listE.getHwNode(tgc1);
                            vgmn = (HwVGMN) (listE.getHwNode(tgc2));
                            if ((originNode != null) && (vgmn != null)) {
                                hwlink = new HwLink("link_" + originNode.getName() + "_to_" + vgmn.getName());
                                hwlink.setPriority(connector.getPriority());
                                hwlink.vgmn = vgmn;
                                hwlink.hwnode = originNode;
                                listE.addCor(hwlink, connector);
                                archi.addHwLink(hwlink);
                                //TraceManager.addDev("Link added");
                            }
                        }

                        if (tgc2 instanceof TMLArchiCrossbarNode) {
                            originNode = listE.getHwNode(tgc1);
                            crossbar = (HwCrossbar) (listE.getHwNode(tgc2));
                            if ((originNode != null) && (crossbar != null)) {
                                hwlink = new HwLink("link_" + originNode.getName() + "_to_" + crossbar.getName());
                                hwlink.setPriority(connector.getPriority());
                                hwlink.crossbar = crossbar;
                                hwlink.hwnode = originNode;
                                listE.addCor(hwlink, connector);
                                archi.addHwLink(hwlink);
                                //TraceManager.addDev("Link added");
                            }
                        }


                    }
                }
            }
        }
    }

    private void makeCPDataStructure() throws MalformedTMLDesignException {

        // TGComponent tgc;
        //  ui.tmlsd.TMLSDPanel SDpanel;
        //   ui.tmlcp.TMLCPPanel ADpanel;
        List<String> names = new ArrayList<String>();
        //   TMLCPSequenceDiagram SD;
        //  TMLCPActivityDiagram AD;

        Vector<TDiagramPanel> panelList = tmlcpp.getPanels();

        tmlcp.setMainCP(createActivityDiagramDataStructure((ui.tmlcp.TMLCPPanel) panelList.get(0), names)); //Special case for main CP

        for (int panelCounter = 1; panelCounter < panelList.size(); panelCounter++) {
            if (panelList.get(panelCounter) instanceof ui.tmlsd.TMLSDPanel) {
                tmlcp.addCPSequenceDiagram(createSequenceDiagramDataStructure((ui.tmlsd.TMLSDPanel) panelList.get(panelCounter), names));
            } else if (panelList.get(panelCounter) instanceof ui.tmlcp.TMLCPPanel) {
                tmlcp.addCPActivityDiagram(createActivityDiagramDataStructure((ui.tmlcp.TMLCPPanel) panelList.get(panelCounter), names));
            }
            //TraceManager.addDev( "PANEL number: " + panelCounter + " " + panelList.get( panelCounter ) );
        }

        tmlcp.correctReferences(); //Update references to the right activity and sequence diagrams
        //tmlcp.generateNexts(); // Add nexts elements to CPElements
        //tmlcp.removeADConnectors(); // Remove connectors since nexts have been filled
        // Issue #69; Unused TMLCPJunction
        //tmlcp.splitADs(); // Splitting ADs so as to remove junctions -> new ADs are introduced for each junction inside an AD

        /*for( TMLCPSequenceDiagram seqDiag: tmlcp.getCPSequenceDiagrams() )      {
          TraceManager.addDev( "**********" );
          TraceManager.addDev( "DIAGRAM " + seqDiag.getName() );
          for( tmltranslator.tmlcp.TMLSDInstance instance: seqDiag.getInstances() )   {
          TraceManager.addDev( "INSTANCE: " + instance.getName() + "\n" + instance.getAttributeList() );
          }
          }*/

    }   //End of method


    private tmltranslator.tmlcp.TMLCPActivityDiagram createActivityDiagramDataStructure(ui.tmlcp.TMLCPPanel panel,
                                                                                        List<String> names)
            throws MalformedTMLDesignException {
        tmltranslator.tmlcp.TMLCPStart start;
        tmltranslator.tmlcp.TMLCPStop stop;
      //  tmltranslator.tmlcp.TMLCPJunction junction;
        tmltranslator.tmlcp.TMLCPJoin join;
        tmltranslator.tmlcp.TMLCPFork fork;
        tmltranslator.tmlcp.TMLCPChoice choice;
        // tmltranslator.tmlcp.TMLCPConnector TMLCPconnector;
        tmltranslator.tmlcp.TMLCPRefAD refAD;
        tmltranslator.tmlcp.TMLCPRefSD refSD;
        tmltranslator.tmlcp.TMLCPForLoop loop;

        /*if (tmladp == null) {
          String msg = tmlto.getValue() + " has no activity diagram";
          CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
          ce.setTDiagramPanel(tmldp.tmltdp);
          ce.setTGComponent(tgc);
          checkingErrors.add(ce);
          throw new MalformedTMLDesignException(tmlto.getValue() + " msg");
          }*/

        List<TGComponent> components = panel.getComponentList();

        if (nameInUse(names, panel.getName())) {
            String msg = panel.getName() + " already exists";
            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two diagrams have the same name: " + panel.getName());
            ce.setTDiagramPanel(tmlcpp.tmlcpp);
            //ce.setTGComponent( components );
            checkingErrors.add(ce);
            throw new MalformedTMLDesignException(msg);
            /*TraceManager.addDev( "ERROR: two diagrams have the same name!" );
              return new tmltranslator.tmlcp.TMLCPActivityDiagram( "ERROR", panel );*/
        } else {
            names.add(panel.getName());
            //TraceManager.addDev("Lenght of elements: " + components.size() );
            tmltranslator.tmlcp.TMLCPActivityDiagram AD = new tmltranslator.tmlcp.TMLCPActivityDiagram(panel.getName(), panel);
            int k;
            TGComponent component;
            String compID;

            for (k = 0; k < components.size(); k++) {
                component = components.get(k);
                compID = component.getName() + "_#" + AD.getSize();
                if (component instanceof ui.tmlcp.TMLCPStartState) {
                    //TraceManager.addDev( k + " " + component.getName() + "\t" + component.getValue() );
                    start = new tmltranslator.tmlcp.TMLCPStart(compID, component);
                    AD.setStartElement(start);
                    AD.addTMLCPElement(start);        //CAREFUL: the elements are not added in the same order as they appear in the GUI
                }
                if (component instanceof ui.tmlcp.TMLCPStopState) {
                    //TraceManager.addDev( k + " " + component.getName() + "\t" + component.getValue() + "\t" + component.getY() );
                    stop = new tmltranslator.tmlcp.TMLCPStop(compID, component);
                    AD.addTMLCPElement(stop);
                }
                if (component instanceof ui.tmlcp.TMLCPRefSD) {
                    //TraceManager.addDev( k + " " + component.getName() + "\t" + component.getValue() + "\t" + component.getY() );
                    refSD = new tmltranslator.tmlcp.TMLCPRefSD(compID, component);
                    AD.addTMLCPElement(refSD);
                }
                if (component instanceof ui.tmlcp.TMLCPRefAD) {
                    //TraceManager.addDev( k + " " + component.getName() + "\t" + component.getValue() + "\t" + component.getY() );
                    refAD = new tmltranslator.tmlcp.TMLCPRefAD(compID, component);
                    AD.addTMLCPElement(refAD);
                }
                // Issue #69; Unused TMLCPJunction
//                if (component instanceof ui.tmlcp.TMLCPJunction) {
//                    //TraceManager.addDev( k + " " + component.getName() + "\t" + component.getValue() + "\t" + component.getY() );
//                    junction = new tmltranslator.tmlcp.TMLCPJunction(compID, component);
//                    AD.addTMLCPElement(junction);
//                }
                if (component instanceof ui.tmlcp.TMLCPJoin) {
                    //TraceManager.addDev( k + " " + component.getName() + "\t" + component.getValue() + "\t" + component.getY() );
                    join = new tmltranslator.tmlcp.TMLCPJoin(compID, component);
                    AD.addTMLCPElement(join);
                }
                if (component instanceof ui.tmlcp.TMLCPFork) {
                    //TraceManager.addDev( k + " " + component.getName() + "\t" + component.getValue() + "\t" + component.getY() );
                    fork = new tmltranslator.tmlcp.TMLCPFork(compID, component);
                    AD.addTMLCPElement(fork);
                }
                if (component instanceof ui.tmlcp.TMLCPForLoop) {
                    //TraceManager.addDev( k + " " + component.getName() + "\t" + component.getValue() + "\t" + component.getY() );
                    loop = new tmltranslator.tmlcp.TMLCPForLoop(compID, component);
                    AD.addTMLCPElement(loop);
                    loop.setInit(((ui.tmlcp.TMLCPForLoop) (component)).getInit());
                    loop.setCondition(((ui.tmlcp.TMLCPForLoop) (component)).getCondition());
                    loop.setIncrement(((ui.tmlcp.TMLCPForLoop) (component)).getIncrement());

                }
                if (component instanceof ui.tmlcp.TMLCPChoice) {
                    //TraceManager.addDev( k + component.getName() + "\t" + component.getValue() + "\t" + component.getY());
                    // old way: adding guards choice = new tmltranslator.tmlcp.TMLCPChoice( component.getName(), ((ui.tmlcp.TMLCPChoice) component).getGuards(), component );
                    choice = new tmltranslator.tmlcp.TMLCPChoice(compID, null, component);
                    AD.addTMLCPElement(choice);
                }
                /*if( component instanceof ui.tmlcp.TGConnectorTMLCP)     {
                //TraceManager.addDev( k + " " + ((ui.TGConnector)component).getTGConnectingPointP1().getFather().getName() + "\t" +
                //                                                                          ((ui.TGConnector)component).getTGConnectingPointP2().getFather().getName() + "\t" + component.getY() );
                TMLCPconnector = new tmltranslator.tmlcp.TMLCPConnector(
                ((ui.tmlcp.TGConnectorTMLCP)component).getTGConnectingPointP1().getFather().getName(),
                ((ui.tmlcp.TGConnectorTMLCP)component).getTGConnectingPointP2().getFather().getName(), ((ui.tmlcp.TGConnectorTMLCP)component).getGuard(),
                ((ui.tmlcp.TGConnectorTMLCP)component).getY(), component );
                AD.addTMLCPElement( TMLCPconnector );
                }*/
            }   //End of for loop over components

            // Handling connectors
            for (k = 0; k < components.size(); k++) {
                component = components.get(k);
                //compID = component.getName() + "__" + elements.size();
                if (component instanceof ui.tmlcp.TGConnectorTMLCP) {
                    //TraceManager.addDev( k + " " + ((ui.TGConnector)component).getTGConnectingPointP1().getFather().getName() + "\t" +
                    //                                                                          ((ui.TGConnector)component).getTGConnectingPointP2().getFather().getName() + "\t" + component.getY() );
                    //Gathering start and end names;
                    //String st, en;
                    TGComponent stc, enc;
                    TMLCPElement source, dest;

                    stc = (TGComponent) (((ui.tmlcp.TGConnectorTMLCP) component).getTGConnectingPointP1().getFather());
                    enc = (TGComponent) (((ui.tmlcp.TGConnectorTMLCP) component).getTGConnectingPointP2().getFather());
                    source = AD.getElementByReference(stc);
                    dest = AD.getElementByReference(enc);

                    if ((source != null) && (dest != null)) {
                        source.addNextElement(dest);
                        if (source instanceof tmltranslator.tmlcp.TMLCPChoice) {
                            ((tmltranslator.tmlcp.TMLCPChoice) source).addGuard(((ui.tmlcp.TGConnectorTMLCP) component).getGuard());
                        }
                    }

                    /*TMLCPconnector = new tmltranslator.tmlcp.TMLCPConnector(
                      ((ui.tmlcp.TGConnectorTMLCP)component).getTGConnectingPointP1().getFather().getName(),
                      ((ui.tmlcp.TGConnectorTMLCP)component).getTGConnectingPointP2().getFather().getName(), ((ui.tmlcp.TGConnectorTMLCP)component).getGuard(),
                      ((ui.tmlcp.TGConnectorTMLCP)component).getY(), component );
                      AD.addTMLCPElement( TMLCPconnector );*/
                }
            }


            return AD;
        }       //End of else name does not exist yet
    }   //End of method createActivityDiagramDataStructure


    private tmltranslator.tmlcp.TMLCPSequenceDiagram createSequenceDiagramDataStructure(ui.tmlsd.TMLSDPanel panel,
                                                                                        List<String> names) throws MalformedTMLDesignException {

        List<TAttribute> attributes;
        int index1;
        int index2;
        TGComponent[] components;
        TMLType type;
        //  String toParse;
        TAttribute attribute;
        TGConnectorMessageTMLSD connector;
        TMLSDMessage message;
        tmltranslator.tmlcp.TMLSDInstance instance;
        //    String[] tokens;                                                        //used to get the tokens of the string for a SD attribute
        //      String delims = "[ +=:;]+";             //the delimiter chars used to parse attributes of SD instance

        //TraceManager.addDev( "ADDING TO DATA STRUCTURE THE DIAGRAM " + panel.getName() );
        if (nameInUse(names, panel.getName())) {
            String msg = panel.getName() + " already exists";
            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two diagrams have the same name: " + panel.getName());
            ce.setTDiagramPanel(tmlcpp.tmlcpp);
            //ce.setTGComponent( components );
            checkingErrors.add(ce);
            throw new MalformedTMLDesignException(msg);
            /*TraceManager.addDev( "ERROR: two diagrams have the same name!" );
              return new tmltranslator.tmlcp.TMLCPSequenceDiagram( "ERROR", panel );*/
        } else {
            names.add(panel.getName());
            tmltranslator.tmlcp.TMLCPSequenceDiagram SD = new tmltranslator.tmlcp.TMLCPSequenceDiagram(panel.getName(), panel);
            List<TGComponent> elemList = panel.getComponentList();
            //TraceManager.addDev("Adding to the data structure the elements of: " + panel.getName() );
            //order messages according to the inverse of Y coordinate
            int j;
            TGComponent elem;
            for (j = 0; j < elemList.size(); j++) {
                elem = elemList.get(j);
                //include the package name of the class to avoid confusion with the graphical TMLSDInstance
                if (elem instanceof TMLSDStorageInstance) {
                    instance = new tmltranslator.tmlcp.TMLSDInstance(elem.getName(), elem, "STORAGE");
                    TMLSDStorageInstance storage = (TMLSDStorageInstance) elemList.get(j);
                    attributes = storage.getAttributes();
                    for (index1 = 0; index1 < attributes.size(); index1++) {       // an attribute is a variable declaration
                        attribute = attributes.get(index1);
                        if (attribute.getType() == TAttribute.NATURAL) {
                            type = new TMLType(TMLType.NATURAL);
                        } else {
                            if (attribute.getType() == TAttribute.BOOLEAN) {
                                type = new TMLType(TMLType.BOOLEAN);
                            } else {
                                type = new TMLType(TMLType.ADDRESS);
                            }
                        }
                        instance.addAttribute(new TMLAttribute(attribute.getId(), storage.getName(), type, attribute.getInitialValue()));
                    }
                    if (storage.getNumberInternalComponents() > 0) {       // action states are stored as internal components of an instance
                        components = storage.getInternalComponents();
                        for (index2 = 0; index2 < storage.getNumberInternalComponents(); index2++) {
                            instance.addAction(new TMLSDAction(components[index2].getValue(), storage.getName(), components[index2], components[index2].getY()));
                        }
                    }
                    SD.addInstance(instance);
                }
                if (elem instanceof TMLSDControllerInstance) {
                    TMLSDControllerInstance controller = (TMLSDControllerInstance) elemList.get(j);
                    instance = new tmltranslator.tmlcp.TMLSDInstance(controller.getName(), elem, "CONTROLLER");
                    attributes = controller.getAttributes();
                    for (index1 = 0; index1 < attributes.size(); index1++) {       // an attribute is a variable declaration
                        attribute = attributes.get(index1);
                        if (attribute.getType() == TAttribute.NATURAL) {
                            type = new TMLType(TMLType.NATURAL);
                        } else {
                            if (attribute.getType() == TAttribute.BOOLEAN) {
                                type = new TMLType(TMLType.BOOLEAN);
                            } else {
                                type = new TMLType(TMLType.ADDRESS);
                            }
                        }
                        instance.addAttribute(new TMLAttribute(attribute.getId(), controller.getName(), type, attribute.getInitialValue()));
                    }
                    if (controller.getNumberInternalComponents() > 0) {       //Action states are stored as internal components of an instance
                        components = controller.getInternalComponents();
                        for (index2 = 0; index2 < controller.getNumberInternalComponents(); index2++) {       //get action states
                            instance.addAction(new TMLSDAction(components[index2].getValue(), controller.getName(), components[index2], components[index2].getY()));
                        }
                    }
                    SD.addInstance(instance);
                }
                if (elem instanceof TMLSDTransferInstance) {
                    TMLSDTransferInstance transfer = (TMLSDTransferInstance) elemList.get(j);
                    instance = new tmltranslator.tmlcp.TMLSDInstance(transfer.getName(), elem, "TRANSFER");
                    attributes = transfer.getAttributes();
                    for (index1 = 0; index1 < attributes.size(); index1++) {       // an attribute is a variable declaration
                        attribute = attributes.get(index1);
                        if (attribute.getType() == TAttribute.NATURAL) {
                            type = new TMLType(TMLType.NATURAL);
                        } else {
                            if (attribute.getType() == TAttribute.BOOLEAN) {
                                type = new TMLType(TMLType.BOOLEAN);
                            } else {
                                type = new TMLType(TMLType.ADDRESS);
                            }
                        }
                        instance.addAttribute(new TMLAttribute(attribute.getId(), transfer.getName(), type, attribute.getInitialValue()));
                    }
                    if (transfer.getNumberInternalComponents() > 0) {       //Action states are stored as internal components of an instance
                        components = transfer.getInternalComponents();
                        for (index2 = 0; index2 < transfer.getNumberInternalComponents(); index2++) {       //get action states
                            instance.addAction(new TMLSDAction(components[index2].getValue(), transfer.getName(), components[index2], components[index2].getY()));
                        }
                    }
                    SD.addInstance(instance);
                }

            }   //End of for over internal elements
            for (j = 0; j < elemList.size(); j++) {
                elem = elemList.get(j);
                if (elem instanceof TGConnectorMessageTMLSD) {
                    //TraceManager.addDev("Analyzing message:" + elem);
                    connector = (TGConnectorMessageTMLSD) elemList.get(j);
                    String sender = connector.getTGConnectingPointP1().getFather().getName();
                    String receiver = connector.getTGConnectingPointP2().getFather().getName();
                    //Should check that instances do not have multiple names
                    message = new TMLSDMessage(connector.getName(), sender, receiver, connector.getY(), connector, connector.getParams());
                    for (tmltranslator.tmlcp.TMLSDInstance tempInstance : SD.getInstances()) {
                        if (tempInstance.getName().equals(sender)) {
                            //TraceManager.addDev( "Adding message " + message.toString() + " to instance " + tempInstance.toString() );
                            tempInstance.addMessage(message, TMLSDEvent.SEND_MESSAGE_EVENT);
                            //break;
                        }
                        if (tempInstance.getName().equals(receiver)) {
                            //TraceManager.addDev( "Adding message " + message.toString() + " to instance " + tempInstance.toString() );
                            tempInstance.addMessage(message, TMLSDEvent.RECEIVE_MESSAGE_EVENT);
                            //break;
                        }
                    }
                }

            }
            return SD;
        }//End else name does not exist yet
    }   //End of method createSequenceDiagramDataStructure

    private boolean makeTMLModeling() {
        // Determine all TML Design to be used -> TMLDesignPanels
        List<TMLDesignPanel> panels = new ArrayList<TMLDesignPanel>();
        List<TMLComponentDesignPanel> cpanels = new ArrayList<TMLComponentDesignPanel>();
        Vector<Vector<TGComponent>> taskss = new Vector<Vector<TGComponent>>();
        Vector<TMLCPrimitiveComponent> allcomp = new Vector<TMLCPrimitiveComponent>();
        int index;
	//	System.out.println("nodes " + nodesToTakeIntoAccount);
        if (nodesToTakeIntoAccount == null) {
            components = tmlap.tmlap.getComponentList();
        } else {
            components = nodesToTakeIntoAccount;
        }

        Iterator<? extends TGComponent> iterator = components.listIterator();

        TGComponent tgc;//, tgctask;
        //  TMLArchiNode node;
        List<TMLArchiArtifact> artifacts;
        String namePanel;
        TMLDesignPanel tmldp;
        TURTLEPanel tup;
        TMLComponentDesignPanel tmlcdp;
        TMLTaskOperator task;
        TMLCPrimitiveComponent pc;

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLArchiNode) {
                Vector<TGComponent> tmp;

                artifacts = ((TMLArchiNode) (tgc)).getAllTMLArchiArtifacts();
                for (TMLArchiArtifact artifact : artifacts) {
                    namePanel = artifact.getReferenceTaskName();

                    try {
                        tup = tmlap.getMainGUI().getTURTLEPanel(namePanel);

                        // Regular design panel
                        if (tup instanceof TMLDesignPanel) {
                            tmldp = (TMLDesignPanel) tup;
                            if (panels.contains(tmldp)) {
                                index = panels.indexOf(tmldp);
                                tmp = taskss.get(index);
                            } else {
                                panels.add(tmldp);
                                this.tmldp = tmldp;
                                tmp = new Vector<TGComponent>();
                                taskss.add(tmp);
                            }

                            // Search for the corresponding TMLTask
                            task = tmldp.getTaskByName(artifact.getTaskName());

                            if (task != null) {
                                tmp.add(task);
                            } else {
                                UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Task " + artifact.getTaskName() + " referenced by artifact " + artifact.getValue() + " is unknown");
                                ce.setTDiagramPanel(tmlap.tmlap);
                                ce.setTGComponent(tgc);
                                checkingErrors.add(ce);
                            }

                            // Component design panel
                        } else if (tup instanceof TMLComponentDesignPanel) {
                            tmlcdp = (TMLComponentDesignPanel) (tup);
                            if (cpanels.contains(tmlcdp)) {
                                index = cpanels.indexOf(tmlcdp);
                                tmp = taskss.get(index);
                            } else {
                                cpanels.add(tmlcdp);
                                tmp = new Vector<TGComponent>();
                                taskss.add(tmp);

                            }

                            // Search for the corresponding TMLTask
                            pc = tmlcdp.getPrimitiveComponentByName(artifact.getTaskName());
                            if (pc != null) {
                                tmp.add(pc);
                                allcomp.add(pc);
                            } else {
                                UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Component " + artifact.getTaskName() + " referenced by artifact " + artifact.getValue() + " is unknown");
                                ce.setTDiagramPanel(tmlap.tmlap);
                                ce.setTGComponent(tgc);
                                checkingErrors.add(ce);
                            }
                        }
                    } catch (Exception e) {
                        // Just in case the mentionned panel is not a TML design Panel
                    }
                }
            }
        }

        //TraceManager.addDev("Nb of panels regular:" + panels.size() + " components" + cpanels.size());

        // For each panel, construct a TMLModeling
        TMLModeling<TGComponent> tmpm;
        GTMLModeling gtml;
        String s;
        index = 0;
        for (TMLDesignPanel panel : panels) {
            gtml = new GTMLModeling(panel, false);
            gtml.putPrefixName(true);
            gtml.setTasks(taskss.get(index));
            index++;
            tmpm = gtml.translateToTMLModeling(false);
            warnings.addAll(gtml.getCheckingWarnings());
            if (gtml.getCheckingErrors().size() > 0) {
                checkingErrors.addAll(gtml.getCheckingErrors());
                return false;
            }
            //s = tmlap.getMainGUI().getTitleAt(panel);
            //s = s.replaceAll("\\s", "");
            //tmpm.prefixAllNamesWith(s + "__");
            //TraceManager.addDev("Intermediate TMLModeling: ");
            tmlm.advancedMergeWith(tmpm);
        }

        if (cpanels.size() > 0) {
            for (TMLComponentDesignPanel panel : cpanels) {
                this.tmlcdp = panel;
                gtml = new GTMLModeling(panel, false);
                gtml.setComponents(allcomp);
                gtml.putPrefixName(true);
                tmpm = gtml.translateToTMLModeling(true, false);
                warnings.addAll(gtml.getCheckingWarnings());
                if (gtml.getCheckingErrors().size() > 0) {
                    checkingErrors.addAll(gtml.getCheckingErrors());
                    return false;
                }
                //s = tmlap.getMainGUI().getTitleAt(panel);
                //s = s.replaceAll("\\s", "");
                //tmpm.prefixAllNamesWith(s + "__");
                //TraceManager.addDev("-> -> Intermediate TMLModeling: " + tmpm);
                tmlm.advancedMergeWith(tmpm);
            }
            //TraceManager.addDev("-> -> Final TMLModeling: " + tmlm);
            /*TMLComponentDesignPanel panel = cpanels.get(cpanels.size()-1);
              gtml =  new GTMLModeling(panel, false);
              gtml.setComponents(allcomp);
              tmpm = gtml.translateToTMLModeling(true, false);
              warnings.addAll(gtml.getCheckingWarnings());
              if (gtml.getCheckingErrors().size() >0) {
              checkingErrors.addAll(gtml.getCheckingErrors());
              return false;
              }
              s = tmlap.getMainGUI().getTitleAt(panel);
              s = s.replaceAll("\\s", "");
              tmpm.prefixAllNamesWith(s + "__");
              //TraceManager.addDev("Intermediate TMLModeling: " + tmpm);
              tmlm.mergeWith(tmpm);*/
        }

        //TraceManager.addDev("TMLModeling tasks: " + tmlm.tasksToString());

        /*for(TMLComponentDesignPanel panel: cpanels) {
          gtml =  new GTMLModeling(panel);
          gtml.setComponents((Vector)(taskss.get(index)));
          index ++;
          tmpm = gtml.translateToTMLModeling(true);
          warnings.addAll(gtml.getCheckingWarnings());
          if (gtml.getCheckingErrors().size() >0) {
          checkingErrors.addAll(gtml.getCheckingErrors());
          return false;
          }
          s = tmlap.getMainGUI().getTitleAt(panel);
          s = s.replaceAll("\\s", "");
          tmpm.prefixAllNamesWith(s + "__");
          //TraceManager.addDev("Intermediate TMLModeling: " + tmpm);
          tmlm.mergeWith(tmpm);
          }*/

        // Properties of artifacts
        iterator = components.listIterator();
        TMLTask ttask;
        while (iterator.hasNext()) {
            //TraceManager.addDev("next");
            tgc = iterator.next();
            if (tgc instanceof TMLArchiNode) {
                artifacts = ((TMLArchiNode) (tgc)).getAllTMLArchiArtifacts();
                for (TMLArchiArtifact artifact : artifacts) {
                    s = artifact.getReferenceTaskName() + "__" + artifact.getTaskName();
                    //TraceManager.addDev( "Exploring " + s );
                    s = s.replaceAll("\\s", "");
                    ttask = tmlm.getTMLTaskByName(s);
                    if (ttask != null) {
                        ttask.setPriority(artifact.getPriority());
                    }
                }
            }
        }


        //TraceManager.addDev("TMLModeling: " + tmlm);

        return true;
    }

    //Inspect the architecture diagrams and retrieve mapping of channels onto CPs
    //    private void makeCPMapping()        {
    //
    //        //Why this code?
    //        //if( nodesToTakeIntoAccount == null ) {
    //
    //        //take the architecture panel if it exists, otherwise return
    //        Vector<TDiagramPanel> panelList = tmlap.getPanels();
    //        for( TDiagramPanel panel: panelList )   {
    //            TraceManager.addDev( "Name of Panel: " + panel.getName() );
    //        }
    //        //}
    //        //else  {
    //        //      components = nodesToTakeIntoAccount;
    //        //}
    //        ListIterator iterator = components.listIterator();
    //
    //        TGComponent tgc;
    //        ArrayList<TMLArchiArtifact> artifacts;
    //        ArrayList<TMLArchiCommunicationArtifact> artifactscomm;
    //        ArrayList<TMLArchiEventArtifact> artifactsEvt;
    //        HwNode node;
    //        TMLTask task;
    //        TMLElement elt;
    //        String s;
    //
    //        while( iterator.hasNext() ) {
    //            TraceManager.addDev( "makeCPMapping 1" );
    //            tgc = (TGComponent)( iterator.next() );
    //            if( tgc instanceof TMLArchiCPNode ) {
    //                TraceManager.addDev( "makeCPMapping 2" );
    //                node = archi.getHwNodeByName( tgc.getName() );
    //                if ( ( node != null ) && ( node instanceof HwCommunicationNode ) ) {
    //                    TraceManager.addDev( "makeCPMapping 3" );
    //                    artifactscomm = ( (TMLArchiCommunicationNode)(tgc) ).getChannelArtifactList();
    //                    for( TMLArchiCommunicationArtifact artifact: artifactscomm )        {
    //                        TraceManager.addDev("Exploring artifact " + artifact.getValue());
    //                        s = artifact.getReferenceCommunicationName();
    //                        s = s.replaceAll("\\s", "");
    //                        s = s + "__" + artifact.getCommunicationName();
    //                        TraceManager.addDev("Searching for:" + s);
    //                        elt = tmlm.getCommunicationElementByName(s);
    //                        TraceManager.addDev("comm elts:" + tmlm.getStringListCommunicationElements());
    //                        if( elt instanceof TMLChannel ) {
    //                            //TraceManager.addDev("Setting priority");
    //                            ( (TMLChannel)(elt) ).setPriority( artifact.getPriority() );
    //                        }
    //                        if (elt != null) {
    //                            map.addCommToHwCommNode( elt, (HwCommunicationNode)node );
    //                        } else {
    //                            TraceManager.addDev("Null mapping: no element named: " + artifact.getCommunicationName());
    //                        }
    //                    }
    //
    //
    //                }
    //            }
    //        }
    //    }   //End of method

    private void makeTMLCPLib() {
        if (nodesToTakeIntoAccount == null) {
            components = tmlap.tmlap.getComponentList();
        } else {
            components = nodesToTakeIntoAccount;
        }
        Iterator<? extends TGComponent> iterator = components.listIterator();

        TGComponent tgc;
        //        ArrayList<TMLArchiArtifact> artifacts;
        //        ArrayList<TMLArchiCommunicationArtifact> artifactscomm;
        //        ArrayList<TMLArchiEventArtifact> artifactsEvt;
        //     HwNode node;
        //    TMLTask task;
        //    TMLElement elt;
        //     String s;
        TMLArchiCPNode cp = null;

        while (iterator.hasNext()) {
            tgc = iterator.next();
            //TraceManager.addDev("---------------- tgc=" + tgc);
            if (tgc instanceof TMLArchiCPNode) {
                try {
                    cp = (TMLArchiCPNode) tgc;
                    TMLCPLib tmlcplib = new TMLCPLib(cp.getCompleteName(), cp.getReference(), tgc, cp.getCPMEC());
                    map.addTMLCPLib(tmlcplib);
                    tmlcplib.setMappedUnits(cp.getMappedUnits());
                    tmlcplib.setAssignedAttributes(cp.getAssignedAttributes());

                    tmlcplib.setTransferTypes(cp.getTransferTypes());

                    // Handling mapped artifacts
                    for (TMLArchiPortArtifact artifact : cp.getPortArtifactList()) {
                        TMLCPLibArtifact arti = new TMLCPLibArtifact(artifact.getName(), artifact, artifact.getValue(), artifact.getPortName(), artifact.getMappedMemory(), artifact.getPriority(), artifact.getBufferParameters());
                        tmlcplib.addArtifact(arti);
                        //TraceManager.addDev("Adding CP artifact:" + arti);
                    }
                } catch (Exception e) {
                    TraceManager.addDev("\n\n==========> Badly formed TMLCPLib:" + cp + "\nADDING WARNING\n");
                    UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "CP " + cp.getCompleteName() + " has been removed (invalid CP)");
                    ce.setTDiagramPanel(tmlap.tmlap);
                    ce.setTGComponent(cp);
                    warnings.add(ce);
                }
            }
        }
    }

    private void makeMapping() {
        if (nodesToTakeIntoAccount == null) {
            components = tmlap.tmlap.getComponentList();

        } else {
            components = nodesToTakeIntoAccount;
        }

        Iterator<? extends TGComponent> iterator = components.listIterator();

        TGComponent tgc;
        List<TMLArchiArtifact> artifacts;
        List<TMLArchiCommunicationArtifact> artifactscomm;
        List<TMLArchiEventArtifact> artifactsEvt;
        HwNode node;
        TMLTask task;
        TMLElement elt;
        String s;

        while (iterator.hasNext()) {
            tgc = iterator.next();
            //TraceManager.addDev(" is custom component?" + tgc + " class=" + tgc.getClass());

            // Custom values (for plugin)
            if (tgc instanceof TGComponentPlugin) {
                //TraceManager.addDev("custom component found:" + tgc);
                String val = ((TGComponentPlugin) (tgc)).getCustomValue();
                if (val != null) {
                    //TraceManager.addDev("Adding custom value:" +  val);
                    map.addCustomValue(val);
                }
            }

            // Execution nodes
            node = archi.getHwNodeByName(tgc.getName());
            if ((node != null) && (node instanceof HwExecutionNode)) {     //why checking this instanceof?
                artifacts = ((TMLArchiNode) (tgc)).getAllTMLArchiArtifacts();
                for (TMLArchiArtifact artifact : artifacts) {
                    //TraceManager.addDev("Exploring artifact " + artifact.getValue());
                    s = artifact.getReferenceTaskName();
                    ArchUnitMEC mec = artifact.getArchUnitMEC();
                    int operationType = artifact.getOperationType();
                    //TraceManager.addDev("1) Trying to get task named:" + s);
                    s = s.replaceAll("\\s", "");
                    //TraceManager.addDev("2) Trying to get task named:" + s);
                    s = s + "__" + artifact.getTaskName();
                    //TraceManager.addDev("3) Trying to get task named:" + s);
                    task = tmlm.getTMLTaskByName(s);
                    if (task != null) {
                        if (operationType != -1) {
                            task.addOperationType(operationType);
                        }
                        node.addMECToHwExecutionNode(mec);
                        map.addTaskToHwExecutionNode(task, (HwExecutionNode) node);
                    } else {
                        TraceManager.addDev("Null task. Raising an error");
                        String msg = "The task named " + artifact.getTaskName() + " was not found";
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, msg);
                        ce.setTDiagramPanel(tmlap.tmlap);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    }
                }
            }

            // Other nodes (memory, bridge, bus, VGMN, crossbar)
            //}
            if ((tgc instanceof TMLArchiBUSNode) || (tgc instanceof TMLArchiVGMNNode) || (tgc instanceof TMLArchiCrossbarNode) || (tgc instanceof TMLArchiBridgeNode) || (tgc instanceof TMLArchiMemoryNode) || (tgc instanceof TMLArchiDMANode) || (tgc instanceof TMLArchiFirewallNode)) {
                node = archi.getHwNodeByName(tgc.getName());
                if ((node != null) && (node instanceof HwCommunicationNode)) {
                    if (tgc instanceof TMLArchiFirewallNode) {
                        map.firewall = true;
                    }
                    artifactscomm = ((TMLArchiCommunicationNode) (tgc)).getChannelArtifactList();
                    for (TMLArchiCommunicationArtifact artifact : artifactscomm) {
                        //TraceManager.addDev("Exploring artifact " + artifact.getValue());
                        s = artifact.getReferenceCommunicationName();
                        s = s.replaceAll("\\s", "");
                        s = s + "__" + artifact.getCommunicationName();
                        //TraceManager.addDev( "s: " + s );
                        String[] vectChNames = artifact.getCommunicationName().split("__");
                        if (vectChNames.length > 1) {
                            s = artifact.getReferenceCommunicationName() + "__" + vectChNames[0] + "__" + artifact.getReferenceCommunicationName()
                                    + "__" + vectChNames[1];
                        } else {
                            s = artifact.getReferenceCommunicationName() + "__" + vectChNames[0];
                        }
                        s = s.replaceAll("\\s", "");
                        //TraceManager.addDev("Searching for " + s + " in " );
                        elt = tmlm.getCommunicationElementByName(s);
                        //TraceManager.addDev("comm elts: " + tmlm.getStringListCommunicationElements());
                        if (elt instanceof TMLChannel) {
                            //TraceManager.addDev("Setting priority of " + elt + " to " + artifact.getPriority() );
                            ((TMLChannel) (elt)).setPriority(artifact.getPriority());
                        }
                        if (elt != null) {
                            //TraceManager.addDev( "Adding communication " + s + " to Hardware Communication Node " + node.getName() );
                            map.addCommToHwCommNode(elt, (HwCommunicationNode) node);
                        } else {
                            //TraceManager.addDev("Null mapping: no element named: " + s );
                        }
                    }
                    artifactsEvt = ((TMLArchiCommunicationNode) (tgc)).getEventArtifactList();
                    for (TMLArchiEventArtifact artifact : artifactsEvt) {
                        //TraceManager.addDev("Exploring artifact " + artifact.getValue());
                        s = artifact.getReferenceEventName();
                        s = s.replaceAll("\\s", "");
                        s = s + "__" + artifact.getEventArtifactName();
                        //TraceManager.addDev( "Searching for:" + s );
                        elt = tmlm.getCommunicationElementByName(s);
                        //TraceManager.addDev( "evt elts:" + tmlm.getStringListCommunicationElements() );

                        if (elt instanceof TMLChannel) {
                            //TraceManager.addDev("Setting priority");
                            ((TMLChannel) (elt)).setPriority(artifact.getPriority());
                        }
                        if (elt != null) {
                            map.addCommToHwCommNode(elt, (HwCommunicationNode) node);
                        } else {
                            //TraceManager.addDev( "Null mapping: no element named: " + artifact.getEventArtifactName() );
                        }
                    }
                    //Map keys
                    ArrayList<TMLArchiKey> keys = ((TMLArchiCommunicationNode) (tgc)).getKeyList();
                    for (TMLArchiKey key : keys) {
                        //TraceManager.addDev("Exploring key " + key.getValue());
                        SecurityPattern sp = tmlm.getSecurityPattern(key.getValue());
                        if (sp != null && node instanceof HwMemory) {
                            if (map.mappedSecurity.containsKey(sp)) {
                                map.mappedSecurity.get(sp).add((HwMemory) node);
                            } else {
                                ArrayList<HwMemory> mems = new ArrayList<HwMemory>();
                                mems.add((HwMemory) node);
                                map.mappedSecurity.put(sp, mems);
                                //TraceManager.addDev("Added key of " + key.getValue());
                            }
                        } else {
                            //
                        }
                    }
                }
            }

        }
    }

    public void addToTable(String s1, String s2) {
        //TraceManager.addDev("Adding to Table s1= "+ s1 + " s2=" + s2);
        table.put(s1, s2);
    }

    public String getAttackerChannel(String s) {
        for (String channelName : table.keySet()) {
            if (channelName.split("/")[1].equals(s)) {
                return table.get(channelName);
            }
        }
        return "";
    }
//
//    public String getFromTable(TMLTask task, String s) {
//        //TraceManager.addDev("TABLE GET: Getting from task=" + task.getName() + " element=" + s);
//
//        if (table == null) {
//            return s;
//        }
//
//        String ret = table.get(task.getName() + "/" + s);
//        //TraceManager.addDev("Returning=" + ret);
//
//        if (ret == null) {
//            return s;
//        }
//
//        return ret;
//    }

    public void removeActionsWithRecords() {
        //TraceManager.addDev("Reworking actions with records");
        tmlm.splitActionStatesWithUnderscoreVariables();
    }

    public void removeActionsWithDollars() {
        //TraceManager.addDev("Reworking actions with records");
        tmlm.splitActionStatesWithDollars();
    }

    public String makeName(TGComponent tgc, String _name) {
        if (!putPrefixName) {
            return _name;
        }
        //TraceManager.addDev("Making name of " + tgc + " from name = " + _name);
        String s = tgc.getTDiagramPanel().getMGUI().getTitleOf(tgc.getTDiagramPanel());
        s = s.replaceAll("\\s", "");
        s = s + "__" + _name;
        //TraceManager.addDev("Making name=" + s);
        return s;
    }
}
