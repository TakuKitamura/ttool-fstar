package ui;
/**
 * Class HSMGeneration
 * HSM Generation in separate thread
 * Creation: 1/06/2016
 *
 * @author Letitia LI
 * @version 1.1 5/30/2018
 */
 
 import avatartranslator.*;
import myutil.BoolExpressionEvaluator;
import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import myutil.TraceManager;

import tmltranslator.*;
import tmltranslator.toavatarsec.TML2Avatar;

import avatartranslator.*;
import avatartranslator.toproverif.AVATAR2ProVerif;


import launcher.LauncherException;
import launcher.RemoteExecutionThread;
import launcher.RshClient;

import ui.tmlad.*;
import ui.tmlcd.TMLTaskDiagramPanel;
import ui.tmlcd.TMLTaskOperator;
import ui.tmlcompd.*;
import ui.tmlcp.TMLCPPanel;
import ui.tmldd.*;
import common.ConfigurationTTool;

import ui.tmlsd.TMLSDPanel;

import proverifspec.ProVerifOutputAnalyzer;
import proverifspec.ProVerifQueryAuthResult;
import proverifspec.ProVerifQueryResult;
import proverifspec.ProVerifSpec;

import java.awt.Point;

import java.util.*;
import java.io.*;
 
public class HSMGeneration implements Runnable {
	MainGUI gui;
	Map<String, List<String>> selectedCpuTasks;
	
	AVATAR2ProVerif avatar2proverif;
	AvatarSpecification avatarspec;
	ProVerifSpec proverif;
	TMLMapping<TGComponent> tmap;
	
	public HSMGeneration(MainGUI gui, Map<String, List<String>> selectedCpuTasks, TMLMapping<TGComponent> tmap){
		this.gui = gui;
		this.selectedCpuTasks = selectedCpuTasks;
		this.tmap = tmap;
	}
	public void proverifAnalysis(TMLMapping<TGComponent> map, List<String> nonAuthChans, List<String> nonSecChans) {
        if (map == null) {
            TraceManager.addDev("No mapping");
            return;
        }
        
        //Perform ProVerif Analysis
        TML2Avatar t2a = new TML2Avatar(map, false, true);
        AvatarSpecification avatarspec = t2a.generateAvatarSpec("1");
        if (avatarspec == null) {
            TraceManager.addDev("No avatar spec");
            return;
        }

        avatar2proverif = new AVATAR2ProVerif(avatarspec);
        try {
            proverif = avatar2proverif.generateProVerif(true, true, 3, true, true);
            //warnings = avatar2proverif.getWarnings();

            if (!avatar2proverif.saveInFile("pvspec")) {
                return;
            }

            RshClient rshc = new RshClient(ConfigurationTTool.ProVerifVerifierHost);

            rshc.setCmd(ConfigurationTTool.ProVerifVerifierPath + " -in pitype pvspec");
            rshc.sendExecuteCommandRequest();
            Reader data = rshc.getDataReaderFromProcess();

            ProVerifOutputAnalyzer pvoa = avatar2proverif.getOutputAnalyzer();
            pvoa.analyzeOutput(data, true);
            if (pvoa.getResults().size() ==0){
            	TraceManager.addDev("ERROR: No security results");
            }
            
            Map<AvatarPragmaSecret, ProVerifQueryResult> confResults = pvoa.getConfidentialityResults();
            for (AvatarPragmaSecret pragma : confResults.keySet()) {
                if (confResults.get(pragma).isProved() && !confResults.get(pragma).isSatisfied()) {
                    nonSecChans.add(pragma.getArg().getBlock().getName() + "__" + pragma.getArg().getName());
                    TraceManager.addDev(pragma.getArg().getBlock().getName() + "." + pragma.getArg().getName() + " is not secret");
                    TMLChannel chan = map.getTMLModeling().getChannelByShortName(pragma.getArg().getName().replaceAll("_chData", ""));
                    for (String block : chan.getTaskNames()) {
                        nonSecChans.add(block + "__" + pragma.getArg().getName());
                    }
                }
            }
            Map<AvatarPragmaAuthenticity, ProVerifQueryAuthResult> authResults = pvoa.getAuthenticityResults();
            for (AvatarPragmaAuthenticity pragma : authResults.keySet()) {
                if (authResults.get(pragma).isProved() && !authResults.get(pragma).isSatisfied()) {
                    nonAuthChans.add(pragma.getAttrA().getAttribute().getBlock().getName() + "__" + pragma.getAttrA().getAttribute().getName().replaceAll("_chData", ""));
                    nonAuthChans.add(pragma.getAttrB().getAttribute().getBlock().getName() + "__" + pragma.getAttrB().getAttribute().getName().replaceAll("_chData", ""));
                }
            }
            TraceManager.addDev("nonsecchans " + nonSecChans);
            TraceManager.addDev("nonauthchans " + nonAuthChans);
            TraceManager.addDev("all results displayed");

        } catch (Exception e) {
            
        }
    }
    
    
	public void startThread(){
		Thread t = new Thread(this);
		t.start();
		try {
			t.join();
		}
		catch (Exception e){
			TraceManager.addDev("Error in HSM Generation Thread");
		}
		return;
	}
    
    public void run(){
    	Map<String, Integer> channelIndexMap = new HashMap<String, Integer>();
    	int channelIndex=0;
    	TraceManager.addDev("Adding HSM");
        
        String encComp = "100";
        String decComp = "100";
        String overhead = "0";
        String name = "hsm";
        if (tmap == null) {
            return;
        }
        //Clone diagrams
        TURTLEPanel tmlap = tmap.getCorrespondanceList().getTG(tmap.getArch().getFirstCPU()).getTDiagramPanel().tp;
        int arch = gui.tabs.indexOf(tmlap);
        gui.cloneRenameTab(arch, "hsm");
        TMLArchiPanel newarch = (TMLArchiPanel) gui.tabs.get(gui.tabs.size() - 1);

        TGComponent tgcomp = tmap.getTMLModeling().getTGComponent();
        TMLComponentDesignPanel tmlcdp = (TMLComponentDesignPanel) tgcomp.getTDiagramPanel().tp;

//        TMLComponentDesignPanel tmlcdp = tmap.getTMLCDesignPanel();
        int ind = gui.tabs.indexOf(tmlcdp);
        String tabName = gui.getTitleAt(tmlcdp);
        gui.cloneRenameTab(ind, name);
        TMLComponentDesignPanel t = (TMLComponentDesignPanel) gui.tabs.get(gui.tabs.size() - 1);
        TMLComponentTaskDiagramPanel tcdp = t.tmlctdp;
        //Create clone of architecture panel and map tasks to it
        newarch.renameMapping(tabName, tabName + "_" + name);


        //ProVerif analysis
        List<String> nonAuthChans = new ArrayList<String>();
        List<String> nonSecChans = new ArrayList<String>();

        proverifAnalysis(tmap, nonAuthChans, nonSecChans);

        TGConnector fromStart;
        Map<String, HSMChannel> secChannels = new HashMap<String, HSMChannel>();
        //Add a HSM Task for each selected CPU on the component diagram
        for (String cpuName : selectedCpuTasks.keySet()) {
            Map<String, HSMChannel> hsmChannels = new HashMap<String, HSMChannel>();
            TMLCPrimitiveComponent hsm = new TMLCPrimitiveComponent(0, 500, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxY(), false, null, tcdp);
            TAttribute index = new TAttribute(2, "channelIndex", "0", 0);
            hsm.getAttributeList().add(index);
            tcdp.addComponent(hsm, 0, 500, false, true);
            hsm.setValueWithChange("HSM_" + cpuName);
            //Find all associated components
            List<TMLCPrimitiveComponent> comps = new ArrayList<TMLCPrimitiveComponent>();
            //Find the component to add a HSM to

            for (TGComponent tg : tcdp.getComponentList()) {
                if (tg instanceof TMLCPrimitiveComponent) {
                    for (String compName : selectedCpuTasks.get(cpuName)) {
                        if (tg.getValue().equals(compName)) {
                            comps.add((TMLCPrimitiveComponent) tg);
                            break;
                        }
                    }
                } else if (tg instanceof TMLCCompositeComponent) {
                    TMLCCompositeComponent cc = (TMLCCompositeComponent) tg;
                    List<TMLCPrimitiveComponent> pcomps = cc.getAllPrimitiveComponents();
                    for (TMLCPrimitiveComponent pc : pcomps) {
                        for (String compName : selectedCpuTasks.get(cpuName)) {
                            if (pc.getValue().equals(compName)) {
                                comps.add(pc);
                                break;
                            }
                        }
                    }
                }
            }
            if (comps.size() == 0) {
                //
                continue;
            }
            for (TMLCPrimitiveComponent comp : comps) {

                Map<String, HSMChannel> compChannels = new HashMap<String, HSMChannel>();
                String compName = comp.getValue();
                TMLActivityDiagramPanel tad = t.getTMLActivityDiagramPanel(compName);
                Set<TGComponent> channelInstances = new HashSet<TGComponent>();
                Set<TGComponent> secOperators = new HashSet<TGComponent>();
              //  isEnc = new TAttribute(2, "isEnc", "true", 4);
                //comp.getAttributeList().add(isEnc);
                //Find all unsecured channels
                //For previously secured channels, relocate encryption to the hsm

                for (TGComponent tg : tad.getComponentList()) {
                    if (tg instanceof TMLADWriteChannel) {
                        TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
                        if (writeChannel.getSecurityContext().equals("")) {
                            String nonceName = "";
                            int type = -1;
                            if (nonSecChans.contains(compName + "__" + writeChannel.getChannelName() + "_chData")) {
                                type = HSMChannel.SENC;
                                if (nonAuthChans.contains(compName + "__" + writeChannel.getChannelName())) {
                                    nonceName = "nonce_" + writeChannel.getChannelName();
                                }
                            } else if (nonAuthChans.contains(compName + "__" + writeChannel.getChannelName())) {
                                type = HSMChannel.MAC;
                            }
                            HSMChannel ch = new HSMChannel(writeChannel.getChannelName(), compName, type);
                            ch.securityContext = "hsmSec_" + writeChannel.getChannelName();
                            ch.nonceName = nonceName;
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                if (type != -1) {
                                    compChannels.put(writeChannel.getChannelName(), ch);
                                    channelInstances.add(tg);
                                    if (!channelIndexMap.containsKey(writeChannel.getChannelName())){
	                                    channelIndexMap.put(writeChannel.getChannelName(),channelIndex);
	                                    channelIndex++;
									}   
                                }
                            }
                        } else {
                            //
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                channelInstances.add(tg);
                                SecurityPattern sp = tmap.getSecurityPatternByName(writeChannel.getSecurityContext());
                                int type = -1;
                                if (sp.type.equals("Symmetric Encryption")) {
                                    type = HSMChannel.SENC;
                                } else if (sp.type.equals("Asymmetric Encryption")) {
                                    type = HSMChannel.AENC;
                                } else if (sp.type.equals("MAC")) {
                                    type = HSMChannel.MAC;
                                } else if (sp.type.equals("Nonce")) {
                                    type = HSMChannel.NONCE;
                                }
                                HSMChannel ch = new HSMChannel(writeChannel.getChannelName(), compName, type);
                                ch.securityContext = writeChannel.getSecurityContext();
                                compChannels.put(writeChannel.getChannelName(), ch);
                                if (!channelIndexMap.containsKey(writeChannel.getChannelName())){
	                            	channelIndexMap.put(writeChannel.getChannelName(),channelIndex);
	                                channelIndex++;
								}   
                                //chanNames.add(writeChannel.getChannelName()+compName);
                            }
                        }
                    }
                    if (tg instanceof TMLADReadChannel) {
                        TMLADReadChannel readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getSecurityContext().equals("")) {
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                if (nonSecChans.contains(compName + "__" + readChannel.getChannelName() + "_chData") || nonAuthChans.contains(compName + "__" + readChannel.getChannelName())) {
                                    channelInstances.add(tg);
                                    HSMChannel ch = new HSMChannel(readChannel.getChannelName(), compName, HSMChannel.DEC);
                                    ch.securityContext = "hsmSec_" + readChannel.getChannelName();
                                    compChannels.put(readChannel.getChannelName(), ch);
                                    if (!channelIndexMap.containsKey(readChannel.getChannelName())){
	                            		channelIndexMap.put(readChannel.getChannelName(),channelIndex);
	                                	channelIndex++;
									}   
                                    if (nonSecChans.contains(compName + "__" + readChannel.getChannelName() + "_chData") && nonAuthChans.contains(compName + "__" + readChannel.getChannelName())) {
                                        ch.nonceName = "nonce_" + readChannel.getChannelName();
                                    }
                                }
                            }
                        } else {
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                channelInstances.add(tg);
                                HSMChannel ch = new HSMChannel(readChannel.getChannelName(), compName, HSMChannel.DEC);
                                ch.securityContext = readChannel.getSecurityContext();
                                compChannels.put(readChannel.getChannelName(), ch);
                                if (!channelIndexMap.containsKey(readChannel.getChannelName())){
	                            	channelIndexMap.put(readChannel.getChannelName(),channelIndex);
	                                channelIndex++;
								}   
                            }
                        }
                    }
                    if (tg instanceof TMLADEncrypt) {
                        //      TMLADEncrypt enc = (TMLADEncrypt) tg;
                        secOperators.add(tg);
                        //}
                    }
                    if (tg instanceof TMLADDecrypt) {
                        //      TMLADDecrypt dec = (TMLADDecrypt) tg;
                        secOperators.add(tg);
                        //}
                    }
                }
                
               // 
                //
                List<ChannelData> hsmChans = new ArrayList<ChannelData>();
                ChannelData chd = new ChannelData("startHSM_" + cpuName, false, false);
                hsmChans.add(chd);
                for (String s : compChannels.keySet()) {
                    hsmChannels.put(s, compChannels.get(s));
                    chd = new ChannelData("data_" + s + "_" + compChannels.get(s).task, false, true);
                    hsmChans.add(chd);
                    chd = new ChannelData("retData_" + s + "_" + compChannels.get(s).task, true, true);
                    hsmChans.add(chd);
                }
                for (ChannelData hsmChan : hsmChans) {
                    TMLCChannelOutPort originPort = new TMLCChannelOutPort(comp.getX(), comp.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, hsm, tcdp);
                    TMLCChannelOutPort destPort = new TMLCChannelOutPort(comp.getX(), comp.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, comp, tcdp);
                    originPort.commName = hsmChan.name;
                    originPort.isOrigin = hsmChan.isOrigin;
                    tcdp.addComponent(originPort, hsm.getX(), hsm.getY(), true, true);
                    destPort.commName = hsmChan.name;
                    if (!hsmChan.isChan) {
                        originPort.typep = 2;
                        destPort.typep = 2;
                        originPort.setParam(0, new TType(1));
                    }
                    destPort.isOrigin = !hsmChan.isOrigin;

                    tcdp.addComponent(destPort, comp.getX(), comp.getY(), true, true);

                    TMLCPortConnector conn = new TMLCPortConnector(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp, originPort.getTGConnectingPointAtIndex(0), destPort.getTGConnectingPointAtIndex(0), new Vector<Point>());
                    tcdp.addComponent(conn, 0, 0, false, true);
                }
                int xpos = 0;
                int ypos = 0;

                //Remove existing security elements
                for (TGComponent op : secOperators) {
                    TGConnector prev = tad.findTGConnectorEndingAt(op.getTGConnectingPointAtIndex(0));
                    //TGConnectingPoint point = prev.getTGConnectingPointP1();
                    TGConnector end = tad.findTGConnectorStartingAt(op.getTGConnectingPointAtIndex(1));
                    TGConnectingPoint point2 = end.getTGConnectingPointP2();
                    tad.removeComponent(op);
                    tad.removeComponent(end);
                    tad.addComponent(prev, 0, 0, false, true);
                    prev.setP2(point2);
                }

                //Modify component activity diagram to add read/write to HSM

                //Add actions before Write Channel
                for (TGComponent chan : channelInstances) {
                    String chanName = "";
                    if (!(chan instanceof TMLADWriteChannel)) {
                        continue;
                    }
                    TMLADWriteChannel writeChannel = (TMLADWriteChannel) chan;
                    chanName = writeChannel.getChannelName();
                    HSMChannel ch = hsmChannels.get(chanName);
                    writeChannel.setSecurityContext(ch.securityContext);
                    xpos = chan.getX();
                    ypos = chan.getY();
                    fromStart = tad.findTGConnectorEndingAt(chan.getTGConnectingPointAtIndex(0));
                    TGConnectingPoint point = fromStart.getTGConnectingPointP2();

                    int yShift = 50;

                    TMLADSendRequest req = new TMLADSendRequest(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    req.setRequestName("startHSM_" + cpuName);
                    
                                        
                    
                    req.setParam(0, Integer.toString(channelIndexMap.get(chanName)));
                    req.makeValue();
                    tad.addComponent(req, xpos, ypos + yShift, false, true);

                    fromStart.setP2(req.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, xpos, ypos, false, true);

                    //Add connection
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(req.getTGConnectingPointAtIndex(1));
                    TMLADWriteChannel wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

                    yShift += 50;
                    //Add write channel operator
                    wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    wr.setChannelName("data_" + chanName + "_" + compName);
                    wr.setSecurityContext(ch.securityContext);
                    tad.addComponent(wr, xpos, ypos + yShift, false, true);


                    fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, xpos, ypos, false, true);

                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(wr.getTGConnectingPointAtIndex(1));


                    //Add read channel operator

                    yShift += 60;
                    TMLADReadChannel rd = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd.setChannelName("retData_" + chanName + "_" + compName);
                    rd.setSecurityContext(ch.securityContext);
                    tad.addComponent(rd, xpos, ypos + yShift, false, true);

                    fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
                    yShift += 50;

                    //Add connector
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                    yShift += 50;

                    //Direct the last TGConnector back to the start of the write channel operator


                    fromStart.setP2(point);
                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg : tad.getComponentList()) {
                        if (tg.getY() >= ypos && tg != wr && tg != req && tg != rd) {
                            tg.setCd(tg.getX(), tg.getY() + yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);
                    tad.repaint();

                }
                //Add actions after Read Channel
                for (TGComponent chan : channelInstances) {
                    String chanName = "";
                    if (!(chan instanceof TMLADReadChannel)) {
                        continue;
                    }
                    TMLADReadChannel readChannel = (TMLADReadChannel) chan;
                    chanName = readChannel.getChannelName();
                    HSMChannel ch = hsmChannels.get(chanName);
                    readChannel.setSecurityContext(ch.securityContext);
                    xpos = chan.getX() + 10;
                    ypos = chan.getY();
                    fromStart = tad.findTGConnectorStartingAt(chan.getTGConnectingPointAtIndex(1));
                    if (fromStart == null) {
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(chan.getTGConnectingPointAtIndex(1));
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                    }
                    TGConnectingPoint point = fromStart.getTGConnectingPointP2();


                    int yShift = 50;


                    yShift += 50;
                    TMLADSendRequest req = new TMLADSendRequest(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    req.setRequestName("startHSM_" + cpuName);
                    
                    req.setParam(0, Integer.toString(channelIndexMap.get(chanName)));
                    
                    req.makeValue();
                    tad.addComponent(req, xpos, ypos + yShift, false, true);


                    fromStart.setP2(req.getTGConnectingPointAtIndex(0));



                    yShift += 50;
                    //Add write channel operator
                    TMLADWriteChannel wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    wr.setChannelName("data_" + chanName + "_" + compName);
                    wr.setSecurityContext(ch.securityContext);
                    tad.addComponent(wr, xpos, ypos + yShift, false, true);


                    //Add connection
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(req.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, xpos, ypos, false, true);

                    //Add read channel operator

                    yShift += 60;
                    TMLADReadChannel rd = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd.setChannelName("retData_" + chanName + "_" + compName);
                    rd.setSecurityContext(ch.securityContext);
                    tad.addComponent(rd, xpos, ypos + yShift, false, true);

                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
                    yShift += 50;

                    if (point != null) {
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        //Direct the last TGConnector back to the start of the write channel operator

                        fromStart.setP2(point);
                    }
                    yShift += 50;

                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg : tad.getComponentList()) {
                        if (tg.getY() >= ypos && tg != wr && tg != req && tg != rd && tg != chan) {
                            tg.setCd(tg.getX(), tg.getY() + yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);
                    tad.repaint();
                }
            }

            int xpos = 0;
            int ypos = 0;


            //Build HSM Activity diagram

            TMLActivityDiagramPanel tad = t.getTMLActivityDiagramPanel("HSM_" + cpuName);

            TMLADStartState start = (TMLADStartState) tad.getComponentList().get(0);
            fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());


            TMLADReadRequestArg req = new TMLADReadRequestArg(300, 100, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
            tad.addComponent(req, 300, 100, false, true);
            req.setParam(0, "channelIndex");
            req.makeValue();

            //Connect start and readrequest
            fromStart.setP1(start.getTGConnectingPointAtIndex(0));
            fromStart.setP2(req.getTGConnectingPointAtIndex(0));
            tad.addComponent(fromStart, 300, 200, false, true);


            TMLADChoice choice = new TMLADChoice(300, 200, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
            tad.addComponent(choice, 300, 200, false, true);


            //Connect readrequest and choice
            fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
            fromStart.setP1(req.getTGConnectingPointAtIndex(1));
            fromStart.setP2(choice.getTGConnectingPointAtIndex(0));
            tad.addComponent(fromStart, 300, 200, false, true);


            int xc = 150;
            //Allows 9 channels max to simplify the diagram

            //If more than 3 channels, build 2 levels of choices

            if (hsmChannels.keySet().size() > 3) {
                TMLADChoice choice2 = new TMLADChoice(xc, 400, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                int i = 0;
                for (String chan : hsmChannels.keySet()) {
                    HSMChannel ch = hsmChannels.get(chan);
                    if (i % 3 == 0) {
                        //Add a new choice every third channel
                        choice2 = new TMLADChoice(xc, 250, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        tad.addComponent(choice2, xc, 400, false, true);
                        //Connect new choice operator to top choice
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(choice.getTGConnectingPointAtIndex(i / 3 + 1));
                        fromStart.setP2(choice2.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);
                    }
                    TMLADReadChannel rd = new TMLADReadChannel(xc, 300, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd.setChannelName("data_" + chan + "_" + hsmChannels.get(chan).task);
                    rd.setSecurityContext(ch.securityContext);
                    tad.addComponent(rd, xc, 300, false, true);
                    //Connect choice and readchannel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(choice2.getTGConnectingPointAtIndex(i % 3 + 1));
                    fromStart.setP2(rd.getTGConnectingPointAtIndex(0));

                    tad.addComponent(fromStart, 300, 200, false, true);
                    TMLADWriteChannel wr = new TMLADWriteChannel(xc, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    wr.setChannelName("retData_" + chan + "_" + hsmChannels.get(chan).task);
                    tad.addComponent(wr, xc, 600, false, true);
                    wr.setSecurityContext(ch.securityContext);


                    if (hsmChannels.get(chan).secType == HSMChannel.DEC) {
                        TMLADDecrypt dec = new TMLADDecrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        dec.securityContext = ch.securityContext;
                        tad.addComponent(dec, xc, 500, false, true);
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);

                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);

                        //Connect encrypt and writechannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(dec.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);
                    } else {
                        TMLADEncrypt enc = new TMLADEncrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        enc.securityContext = ch.securityContext;
                        if (hsmChannels.get(chan).secType == HSMChannel.SENC) {
                            enc.type = "Symmetric Encryption";
                        } else if (hsmChannels.get(chan).secType == HSMChannel.AENC) {
                            enc.type = "Asymmetric Encryption";
                        } else if (hsmChannels.get(chan).secType == HSMChannel.MAC) {
                            enc.type = "MAC";
                        } else if (hsmChannels.get(chan).secType == HSMChannel.NONCE) {
                            enc.type = "Nonce";
                        }

                        enc.message_overhead = overhead;
                        enc.encTime = encComp;
                        enc.decTime = decComp;
                        enc.nonce = hsmChannels.get(chan).nonceName;
                        tad.addComponent(enc, xc, 500, false, true);

                        //Connect encrypt and readchannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);

                        //Connect encrypt and writechannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(enc.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);
                        
                        //Add Stop
                        TMLADStopState stop = new TMLADStopState(xc, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        tad.addComponent(stop, xc, 700, false, true);


						//Connext stop and write channel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(stop.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);

                    }
                    xc += 300;
                    i++;
                }
            } else {

                int i = 1;

                for (String chan : hsmChannels.keySet()) {
                
                	//Add guard as channelindex
                	choice.setGuard("[channelIndex=="+channelIndexMap.get(chan)+"]",i-1);
                	
                    HSMChannel ch = hsmChannels.get(chan);
                    TMLADReadChannel rd = new TMLADReadChannel(xc, 300, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd.setChannelName("data_" + chan + "_" + hsmChannels.get(chan).task);
                    rd.setSecurityContext(ch.securityContext);
                    tad.addComponent(rd, xc, 300, false, true);
                    //Connect choice and readchannel

                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(choice.getTGConnectingPointAtIndex(i));
                    fromStart.setP2(rd.getTGConnectingPointAtIndex(0));

                    tad.addComponent(fromStart, 300, 200, false, true);

                    TMLADWriteChannel wr = new TMLADWriteChannel(xc, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    wr.setChannelName("retData_" + chan + "_" + hsmChannels.get(chan).task);
                    tad.addComponent(wr, xc, 600, false, true);
                    wr.setSecurityContext(ch.securityContext);


                    if (hsmChannels.get(chan).secType == HSMChannel.DEC) {
                    	//Add Decrypt operator
                        TMLADDecrypt dec = new TMLADDecrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        dec.securityContext = ch.securityContext;
                        tad.addComponent(dec, xc, 500, false, true);

						//Connect decrypt and readchannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);

                        //Connect encrypt and writechannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(dec.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);
                        
                         //Add Stop
                        TMLADStopState stop = new TMLADStopState(xc, 700, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        tad.addComponent(stop, xc, 700, false, true);


						//Connect stop and write channel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(stop.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);
                        
                    } else {
                        TMLADEncrypt enc = new TMLADEncrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        enc.securityContext = ch.securityContext;
                        if (hsmChannels.get(chan).secType == HSMChannel.SENC) {
                            enc.type = "Symmetric Encryption";
                        } else if (hsmChannels.get(chan).secType == HSMChannel.AENC) {
                            enc.type = "Asymmetric Encryption";
                        } else if (hsmChannels.get(chan).secType == HSMChannel.MAC) {
                            enc.type = "MAC";
                        } else if (hsmChannels.get(chan).secType == HSMChannel.NONCE) {
                            enc.type = "Nonce";
                        }

                        enc.message_overhead = overhead;
                        enc.encTime = encComp;
                        enc.decTime = decComp;
                        tad.addComponent(enc, xc, 500, false, true);

                        //Connect encrypt and readchannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);

                        //Connect encrypt and writechannel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(enc.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);
                        
                        //Add Stop
                        TMLADStopState stop = new TMLADStopState(xc, 700, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        tad.addComponent(stop, xc, 700, false, true);


						//Connect stop and write channel
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(stop.getTGConnectingPointAtIndex(0));
                        tad.addComponent(fromStart, 300, 200, false, true);


                    }

               
                    xc += 300;
                    i++;
                }

            }

            secChannels.putAll(hsmChannels);
        }
        //For all the tasks that receive encrypted data, decrypt it, assuming it has no associated HSM
        for (TMLTask task : tmap.getTMLModeling().getTasks()) {
            int xpos, ypos;
            //
            TMLActivityDiagramPanel tad = t.getTMLActivityDiagramPanel(task.getName());
            HashSet<TGComponent> channelInstances = new HashSet<TGComponent>();
            for (String chan : secChannels.keySet()) {
                HSMChannel ch = secChannels.get(chan);
                channelInstances.clear();
                for (TGComponent tg : tad.getComponentList()) {
                    if (tg instanceof TMLADReadChannel) {
                        TMLADReadChannel readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getChannelName().equals(chan) && readChannel.getSecurityContext().equals("")) {
                            fromStart = tad.findTGConnectorStartingAt(tg.getTGConnectingPointAtIndex(1));
                            if (fromStart != null) {
                                channelInstances.add(tg);
                            }
                        }
                    }
                }
                for (TGComponent chI : channelInstances) {
                    TMLADReadChannel readChannel = (TMLADReadChannel) chI;
                    readChannel.setSecurityContext(ch.securityContext);
                    xpos = chI.getX();
                    ypos = chI.getY() + 10;
                    fromStart = tad.findTGConnectorStartingAt(chI.getTGConnectingPointAtIndex(1));
                    if (fromStart == null) {
                        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        fromStart.setP1(chI.getTGConnectingPointAtIndex(1));
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                    }
                    TGConnectingPoint point = fromStart.getTGConnectingPointP2();
                    //Add decryption operator
                    int yShift = 100;
                    TMLADDecrypt dec = new TMLADDecrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    dec.securityContext = ch.securityContext;
                    tad.addComponent(dec, xpos, ypos + yShift, false, true);


                    fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                    if (point != null) {
                        fromStart = new TGConnectorTMLAD(dec.getX(), dec.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        fromStart.setP1(dec.getTGConnectingPointAtIndex(1));

                        //Direct the last TGConnector back to the next action

                        fromStart.setP2(point);
                    }
                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg : tad.getComponentList()) {
                        if (tg.getY() >= ypos && tg != dec) {
                            tg.setCd(tg.getX(), tg.getY() + yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);
                    tad.repaint();

                }
            }
            //Next find channels that send encrypted data, and add the encryption operator
            for (String chan : secChannels.keySet()) {
                channelInstances.clear();
                HSMChannel ch = secChannels.get(chan);
                for (TGComponent tg : tad.getComponentList()) {
                    if (tg instanceof TMLADWriteChannel) {
                        TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
                        if (writeChannel.getChannelName().equals(chan) && writeChannel.getSecurityContext().equals("")) {
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                channelInstances.add(tg);
                            }
                        }
                    }
                }
                for (TGComponent chI : channelInstances) {
                    TMLADWriteChannel writeChannel = (TMLADWriteChannel) chI;
                    writeChannel.setSecurityContext(ch.securityContext);
                    xpos = chI.getX();
                    ypos = chI.getY() - 10;
                    fromStart = tad.findTGConnectorEndingAt(chI.getTGConnectingPointAtIndex(0));
                    TGConnectingPoint point = fromStart.getTGConnectingPointP2();
                    //Add encryption operator
                    int yShift = 100;

                    TMLADEncrypt enc = new TMLADEncrypt(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    tad.addComponent(enc, xpos, ypos, false, true);
                    enc.securityContext = ch.securityContext;
                    enc.type = "Symmetric Encryption";
                    enc.message_overhead = overhead;
                    enc.encTime = encComp;
                    enc.decTime = decComp;
                    enc.size = overhead;


                    fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                    fromStart = new TGConnectorTMLAD(enc.getX(), enc.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(enc.getTGConnectingPointAtIndex(1));

                    //Direct the last TGConnector back to the start of the write channel operator

                    fromStart.setP2(point);
                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg : tad.getComponentList()) {
                        if (tg.getY() >= ypos && tg != enc) {
                            tg.setCd(tg.getX(), tg.getY() + yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);
                    tad.repaint();

                }
            }

        }
        for (String cpuName : selectedCpuTasks.keySet()) {
            //Add a private bus to Hardware Accelerator with the task for hsm

            //Find the CPU the task is mapped to
            TMLArchiDiagramPanel archPanel = newarch.tmlap;
            TMLArchiCPUNode cpu = null;
            String refTask = "";
            for (TGComponent tg : archPanel.getComponentList()) {
                if (tg instanceof TMLArchiCPUNode) {
                    if (tg.getName().equals(cpuName)) {
                        cpu = (TMLArchiCPUNode) tg;
                        TMLArchiArtifact art = cpu.getArtifactList().get(0);
                        refTask = art.getReferenceTaskName();
                        break;

                    }
                }
            }

            if (cpu == null) {
                return;
            }

            //Add new memory
            TMLArchiMemoryNode mem = new TMLArchiMemoryNode(cpu.getX() + 100, cpu.getY() + 100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel);
            archPanel.addComponent(mem, cpu.getX() + 100, cpu.getY() + 100, false, true);
            mem.setName("HSMMemory_" + cpuName);
            //Add Hardware Accelerator

            TMLArchiHWANode hwa = new TMLArchiHWANode(cpu.getX() + 100, cpu.getY() + 100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel);
            archPanel.addComponent(hwa, cpu.getX() + 100, cpu.getY() + 100, false, true);
            hwa.setName("HSM_" + cpuName);
            //Add hsm task to hwa


            TMLArchiArtifact hsmArt = new TMLArchiArtifact(cpu.getX() + 100, cpu.getY() + 100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, hwa, archPanel);
            archPanel.addComponent(hsmArt, cpu.getX() + 100, cpu.getY() + 100, true, true);
            hsmArt.setFullName("HSM_" + cpuName, refTask);
            //Add bus connecting the cpu and HWA

            TMLArchiBUSNode bus = new TMLArchiBUSNode(cpu.getX() + 100, cpu.getY() + 100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel);
            bus.setPrivacy(1);
            bus.setName("HSMBus_" + cpuName);
            archPanel.addComponent(bus, cpu.getX() + 200, cpu.getY() + 200, false, true);

            //Connect Bus and CPU
            TMLArchiConnectorNode connect = new TMLArchiConnectorNode(cpu.getX() + 100, cpu.getY() + 100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel, null, null, new Vector<Point>());
            TGConnectingPoint p1 = bus.findFirstFreeTGConnectingPoint(true, true);
            p1.setFree(false);
            connect.setP2(p1);


            TGConnectingPoint p2 = cpu.findFirstFreeTGConnectingPoint(true, true);
            p1.setFree(false);
            connect.setP1(p2);
            archPanel.addComponent(connect, cpu.getX() + 100, cpu.getY() + 100, false, true);
            //Connect Bus and HWA

            connect = new TMLArchiConnectorNode(cpu.getX() + 100, cpu.getY() + 100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel, null, null, new Vector<Point>());
            p1 = bus.findFirstFreeTGConnectingPoint(true, true);
            p1.setFree(false);
            connect.setP2(p1);

            p2 = hwa.findFirstFreeTGConnectingPoint(true, true);
            p1.setFree(false);
            connect.setP1(p2);

            archPanel.addComponent(connect, cpu.getX() + 100, cpu.getY() + 100, false, true);
            //Connect Bus and Memory

            connect = new TMLArchiConnectorNode(cpu.getX() + 100, cpu.getY() + 100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel, null, null, new Vector<Point>());
            p1 = bus.findFirstFreeTGConnectingPoint(true, true);
            p1.setFree(false);
            connect.setP2(p1);

            p2 = mem.findFirstFreeTGConnectingPoint(true, true);
            p1.setFree(false);
            connect.setP1(p2);
            archPanel.addComponent(connect, cpu.getX() + 100, cpu.getY() + 100, false, true);
        }
	}
	class HSMChannel {
        public String name;
        public static final int SENC = 0;
        public static final int NONCE_ENC = 1;
        public static final int MAC = 2;
        public static final int DEC = 3;
        public static final int AENC = 4;
        public static final int NONCE = 5;
        public String task;
        public String securityContext = "";
        public int secType;
        public String nonceName = "";

        public HSMChannel(String n, String t, int type) {
            name = n;
            task = t;
            secType = type;
        }
    }
    
    class ChannelData {
        public String name;
        public boolean isOrigin;
        public boolean isChan;

        public ChannelData(String n, boolean orig, boolean isCh) {
            name = n;
            isOrigin = orig;
            isChan = isCh;
        }

    }


}
