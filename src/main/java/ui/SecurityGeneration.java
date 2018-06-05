package ui;

import avatartranslator.*;
import myutil.BoolExpressionEvaluator;
import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import myutil.TraceManager;

import tmltranslator.*;
import tmltranslator.toavatar.TML2Avatar;

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


public class SecurityGeneration implements Runnable {
	MainGUI gui;
	String name;
	TMLMapping<TGComponent> map;
	TMLArchiPanel newarch;
	String encComp;
	String overhead;
	String decComp;
	boolean autoConf;
	boolean autoWeakAuth;
	boolean autoStrongAuth;
	
	AVATAR2ProVerif avatar2proverif;
	AvatarSpecification avatarspec;
	ProVerifSpec proverif;
	
	
	
	public SecurityGeneration(MainGUI gui, String name, TMLMapping<TGComponent> map, TMLArchiPanel newarch, String encComp, String overhead, String decComp, boolean autoConf, boolean autoWeakAuth, boolean autoStrongAuth){
	
		this.gui = gui;
		this.name=name;
		this.map=map;
		this.newarch=newarch;
		this.encComp = encComp;
		this.overhead = overhead;
		this.decComp = decComp;
		this.autoConf=autoConf;
		this.autoWeakAuth = autoWeakAuth;
		this.autoStrongAuth = autoStrongAuth;
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
            proverif = avatar2proverif.generateProVerif(true, true, 3, true, false);
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
            System.out.println("ProVerif Analysis Failed " + e);
        }
    }
	
	public TMLMapping<TGComponent> startThread(){
		Thread t = new Thread(this);
		t.start();
		try {
			t.join();
		}
		catch (Exception e){
			TraceManager.addDev("Error in Security Generation Thread");
		}
		return map;
	}
	public void run(){
		
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
        for (TMLChannel channel : channels) {
            for (TMLCPrimitivePort p : channel.ports) {
                channel.checkConf = channel.checkConf || p.checkConf;
                channel.checkAuth = channel.checkAuth || p.checkAuth;
            }
        }

        //Create clone of Component Diagram + Activity diagrams to secure
        TGComponent tgcomp = map.getTMLModeling().getTGComponent();
        TMLComponentDesignPanel tmlcdp = (TMLComponentDesignPanel) tgcomp.getTDiagramPanel().tp;
//        TMLComponentDesignPanel tmlcdp = map.getTMLCDesignPanel();
        int ind = gui.tabs.indexOf(tmlcdp);
        if (ind == -1) {
            TraceManager.addDev("No Component Design Panel");
            return;
        }
        String tabName = gui.getTitleAt(tmlcdp);
        gui.cloneRenameTab(ind, name);
        TMLComponentDesignPanel t = (TMLComponentDesignPanel) gui.tabs.get(gui.tabs.size() - 1);

        TMLComponentTaskDiagramPanel tcdp = t.tmlctdp;
        //Create clone of architecture panel and map tasks to it
        newarch.renameMapping(tabName, tabName + "_" + name);

        for (TMLTask task : map.getTMLModeling().getTasks()) {
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
            toSecure.put(task, tmp3);
            toSecureRev.put(task, tmp4);
            nonceInChannels.put(task, tmp5);
            nonceOutChannels.put(task, tmp6);
            macInChannels.put(task, tmp7);
            macOutChannels.put(task, tmp8);
            macNonceOutChannels.put(task, tmp9);
            macNonceInChannels.put(task, tmp10);
        }
        //With the proverif results, check which channels need to be secured
        for (TMLTask task : map.getTMLModeling().getTasks()) {
            //Check if all channel operators are secured
            TMLActivityDiagramPanel tad = t.getTMLActivityDiagramPanel(task.getName());
            for (TGComponent tg : tad.getComponentList()) {
                if (tg instanceof TMLADWriteChannel) {
                    TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
                    if (writeChannel.getSecurityContext().equals("")) {

                        TMLChannel chan = tmlmodel.getChannelByName(tabName + "__" + writeChannel.getChannelName());
                        //System.out.println("channel " + chan);
                        if (chan != null) {
                            if (chan.checkConf && autoConf) {
                                //        System.out.println(chan.getOriginTask().getName().split("__")[1]);
                                if (nonSecChans.contains(chan.getOriginTask().getName().split("__")[1] + "__" + writeChannel.getChannelName() + "_chData") && !secInChannels.get(chan.getDestinationTask()).contains(writeChannel.getChannelName())) {
                                    //                                                                                            if (!securePath(map, chan.getOriginTask(), chan.getDestinationTask())){
                                    secOutChannels.get(chan.getOriginTask()).add(writeChannel.getChannelName());
                                    secInChannels.get(chan.getDestinationTask()).add(writeChannel.getChannelName());
                                    toSecure.get(chan.getOriginTask()).add(chan.getDestinationTask());
                                    if (chan.checkAuth && autoStrongAuth) {
                                        toSecureRev.get(chan.getDestinationTask()).add(chan.getOriginTask());
                                        nonceOutChannels.get(chan.getOriginTask()).add(writeChannel.getChannelName());
                                        nonceInChannels.get(chan.getDestinationTask()).add(writeChannel.getChannelName());
                                    }
                                }
                            } else if (chan.checkAuth && autoWeakAuth) {
                                if (nonAuthChans.contains(chan.getDestinationTask().getName().split("__")[1] + "__" + writeChannel.getChannelName())) {
                                    toSecure.get(chan.getOriginTask()).add(chan.getDestinationTask());
                                    macOutChannels.get(chan.getOriginTask()).add(writeChannel.getChannelName());
                                    macInChannels.get(chan.getDestinationTask()).add(writeChannel.getChannelName());
                                    if (autoStrongAuth) {
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
        TraceManager.addDev("macoutchans " + macOutChannels);
        TraceManager.addDev("macinchans " + macInChannels);
        TraceManager.addDev("nonsecin " + secInChannels);
        TraceManager.addDev("nonsecout " + secOutChannels);
        TraceManager.addDev("noncein " + nonceInChannels);
        TraceManager.addDev("nonceout " + nonceOutChannels);

        //        System.out.println(secOutChanannels.toString());
        //        int num=0;
        //int nonceNum=0;
        //Create reverse channels on component diagram to send nonces if they don't already exist

        for (TMLTask task : toSecureRev.keySet()) {
            TraceManager.addDev("Adding nonces to " + task.getName());
            List<TMLChannel> chans = tmlmodel.getChannelsFromMe(task);

            for (TMLTask task2 : toSecureRev.get(task)) {
                boolean addChan = true;
                for (TMLChannel chan : chans) {
                    if (chan.getDestinationTask() == task2) {
                        addChan = false;
                    }
                }

                if (addChan) {
                    TMLCChannelOutPort originPort = new TMLCChannelOutPort(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp);
                    TMLCChannelOutPort destPort = new TMLCChannelOutPort(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp);
                    for (TGComponent tg : tcdp.getComponentList()) {
                        if (tg instanceof TMLCPrimitiveComponent) {
                            if (tg.getValue().equals(task.getName().split("__")[1])) {
                                originPort = new TMLCChannelOutPort(tg.getX(), tg.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, tg, tcdp);
                                originPort.commName = "nonceCh" + task.getName().split("__")[1] + "_" + task2.getName().split("__")[1];
                                tcdp.addComponent(originPort, tg.getX(), tg.getY(), true, true);
                            } else if (tg.getValue().equals(task2.getName().split("__")[1])) {
                                destPort = new TMLCChannelOutPort(tg.getX(), tg.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, tg, tcdp);
                                destPort.isOrigin = false;
                                destPort.commName = "nonceCh" + task.getName().split("__")[1] + "_" + task2.getName().split("__")[1];
                                tcdp.addComponent(destPort, tg.getX(), tg.getY(), true, true);
                            }
                        }
                        else if (tg instanceof TMLCCompositeComponent){
                        	for (TGComponent internalComp: tg.getRecursiveAllInternalComponent()){
                        		if (internalComp instanceof TMLCPrimitiveComponent){
                        			 if (internalComp.getValue().equals(task.getName().split("__")[1])) {
                                		originPort = new TMLCChannelOutPort(internalComp.getX(), internalComp.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, internalComp, tcdp);
                                		originPort.commName = "nonceCh" + task.getName().split("__")[1] + "_" + task2.getName().split("__")[1];
                                		tcdp.addComponent(originPort, internalComp.getX(), internalComp.getY(), true, true);
                            		} else if (internalComp.getValue().equals(task2.getName().split("__")[1])) {
                                		destPort = new TMLCChannelOutPort(internalComp.getX(), internalComp.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, internalComp, tcdp);
                                		destPort.isOrigin = false;
                                		destPort.commName = "nonceCh" + task.getName().split("__")[1] + "_" + task2.getName().split("__")[1];
                                		tcdp.addComponent(destPort, internalComp.getX(), internalComp.getY(), true, true);
                            		}
                        		}
                        	}
                        }
                    }
                    tmlmodel.addChannel(new TMLChannel("nonceCh" + task.getName().split("__")[1] + "_" + task2.getName().split("__")[1], originPort));
                    //Add connection
                    TMLCPortConnector conn = new TMLCPortConnector(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp, originPort.getTGConnectingPointAtIndex(0), destPort.getTGConnectingPointAtIndex(0), new Vector<Point>());
                    tcdp.addComponent(conn, 0, 0, false, true);
                }
            }
        }
        //  }
        //Add encryption/nonces to activity diagram
        for (TMLTask task : toSecure.keySet()) {
            String title = task.getName().split("__")[0];
            TraceManager.addDev("Securing task " + task.getName());
            TMLActivityDiagramPanel tad = t.getTMLActivityDiagramPanel(task.getName());
            //Get start state position, shift everything down
            int xpos = 0;
            int ypos = 0;
            TGConnector fromStart = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
            TGConnectingPoint point = new TGConnectingPoint(null, 0, 0, false, false);
            //Find states immediately before the write channel operator

            //For each occurence of a write channel operator, add encryption/nonces before it

            for (String channel : secOutChannels.get(task)) {
                Set<TGComponent> channelInstances = new HashSet<TGComponent>();
                int yShift = 50;
                TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);
                //First, find the connector that points to it. We will add the encryption, nonce operators directly before the write channel operator
                for (TGComponent tg : tad.getComponentList()) {
                    if (tg instanceof TMLADWriteChannel) {
                        TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
                        if (writeChannel.getChannelName().equals(channel) && writeChannel.getSecurityContext().equals("")) {

                            if (fromStart != null) {
                                channelInstances.add(tg);
                            }
                        }
                    }
                }
                for (TGComponent comp : channelInstances) {
                    //TMLADWriteChannel writeChannel = (TMLADWriteChannel) comp;
                    xpos = comp.getX();
                    ypos = comp.getY();
                    fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();
                    //Add encryption operator
                    TMLADEncrypt enc = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    TMLADReadChannel rd = new TMLADReadChannel(0, 0, 0, 0, 0, 0, false, null, tad);
                    if (nonceOutChannels.get(task).contains(channel)) {
                        //Receive any nonces if ensuring authenticity
                        rd = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size() > 0) {
                            rd.setChannelName(matches.get(0).getName().replaceAll(title + "__", ""));
                        } else {
                            rd.setChannelName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        rd.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(rd, xpos, ypos + yShift, false, true);
                        fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
                        fromStart = new TGConnectorTMLAD(enc.getX(), enc.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        yShift += 60;
                        //Move encryption operator after receive nonce component
                        enc.setCd(xpos, ypos + yShift);
                        if (tmlc != null) {
                            enc.nonce = "nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1];
                        }
                    }

                    enc.securityContext = "autoEncrypt_" + channel;
                    enc.type = "Symmetric Encryption";
                    enc.message_overhead = overhead;
                    enc.encTime = encComp;
                    enc.decTime = decComp;
                    tad.addComponent(enc, xpos, ypos + yShift, false, true);
                    yShift += 60;
                    fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                    fromStart = new TGConnectorTMLAD(enc.getX(), enc.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(enc.getTGConnectingPointAtIndex(1));

                    //Direct the last TGConnector back to the start of the write channel operator

                    fromStart.setP2(point);
                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg : tad.getComponentList()) {
                        if (tg instanceof TMLADWriteChannel) {
                            TMLADWriteChannel wChannel = (TMLADWriteChannel) tg;
                            TraceManager.addDev("Inspecting write channel " + wChannel.getChannelName());
                            if (channel.equals(wChannel.getChannelName()) && wChannel.getSecurityContext().equals("")) {
                                TraceManager.addDev("Securing write channel " + wChannel.getChannelName());
                                wChannel.setSecurityContext("autoEncrypt_" + wChannel.getChannelName());

                            }
                        }
                        if (tg.getY() >= ypos && tg != enc && tg != rd) {
                            tg.setCd(tg.getX(), tg.getY() + yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);
                    tad.repaint();
                }
            }

            for (String channel : macOutChannels.get(task)) {
                //Add MAC before writechannel
                int yShift = 50;
                TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);
                //First, find the connector that points to it. We will add the encryption, nonce operators directly before the write channel operator
                Set<TGComponent> channelInstances = new HashSet<TGComponent>();
                for (TGComponent tg : tad.getComponentList()) {
                    if (tg instanceof TMLADWriteChannel) {
                        TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
                        if (writeChannel.getChannelName().equals(channel) && writeChannel.getSecurityContext().equals("")) {
                            xpos = tg.getX();
                            ypos = tg.getY();
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                channelInstances.add(tg);
                            }
                        }
                    }
                }
                for (TGComponent comp : channelInstances) {
                    //TMLADWriteChannel writeChannel = (TMLADWriteChannel) comp;
                    xpos = comp.getX();
                    ypos = comp.getY();
                    fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();

                    TMLADEncrypt enc = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

                    //If we need to receive a nonce
                    TMLADReadChannel rd = new TMLADReadChannel(0, 0, 0, 0, 0, 0, false, null, tad);
                    if (macNonceOutChannels.get(task).contains(channel)) {
                        //Receive any nonces if ensuring authenticity
                        rd = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size() > 0) {
                            rd.setChannelName(matches.get(0).getName().replaceAll(title + "__", ""));
                        } else {
                            rd.setChannelName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        rd.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(rd, xpos, ypos + yShift, false, true);
                        fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
                        fromStart = new TGConnectorTMLAD(enc.getX(), enc.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                        yShift += 60;
                        //Move encryption operator after receive nonce component
                        enc.setCd(xpos, ypos + yShift);
                        if (tmlc != null) {
                            enc.nonce = "nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1];
                        }
                    }

                    //Add encryption operator

                    enc.securityContext = "autoEncrypt_" + channel;
                    enc.type = "MAC";
                    enc.message_overhead = overhead;
                    enc.encTime = encComp;
                    enc.decTime = decComp;
                    enc.size = overhead;
                    tad.addComponent(enc, xpos, ypos + yShift, false, true);
                    yShift += 60;
                    fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                    fromStart = new TGConnectorTMLAD(enc.getX(), enc.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(enc.getTGConnectingPointAtIndex(1));

                    //Direct the last TGConnector back to the start of the write channel operator

                    fromStart.setP2(point);
                    //Shift components down to make room for the added ones, and add security contexts to write channels
                    for (TGComponent tg : tad.getComponentList()) {
                        if (tg instanceof TMLADWriteChannel) {
                            TMLADWriteChannel wChannel = (TMLADWriteChannel) tg;
                            TraceManager.addDev("Inspecting write channel " + wChannel.getChannelName());
                            if (channel.equals(wChannel.getChannelName()) && wChannel.getSecurityContext().equals("")) {
                                TraceManager.addDev("Securing write channel " + wChannel.getChannelName());
                                wChannel.setSecurityContext("autoEncrypt_" + wChannel.getChannelName());
                                tad.repaint();
                            }
                        }
                        if (tg.getY() >= ypos && tg != enc && tg != rd) {
                            tg.setCd(tg.getX(), tg.getY() + yShift);
                        }
                    }
                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);
                }
            }
            for (String channel : macInChannels.get(task)) {
                //Add decryptmac after readchannel
                int yShift = 50;
                Set<TGComponent> channelInstances = new HashSet<TGComponent>();
                TGConnector conn = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
                TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);
                //Find read channel operator

                for (TGComponent tg : tad.getComponentList()) {
                    if (tg instanceof TMLADReadChannel) {
                        TMLADReadChannel readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getChannelName().equals(channel) && readChannel.getSecurityContext().equals("")) {
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                channelInstances.add(tg);
                            }
                        }
                    }
                }


                for (TGComponent comp : channelInstances) {

                    fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();
                    conn = tad.findTGConnectorStartingAt(comp.getTGConnectingPointAtIndex(1));
                    next = conn.getTGConnectingPointP2();
                    xpos = fromStart.getX();
                    ypos = fromStart.getY();


                    TMLADReadChannel readChannel = (TMLADReadChannel) comp;
                    TraceManager.addDev("Securing read channel " + readChannel.getChannelName());
                    readChannel.setSecurityContext("autoEncrypt_" + readChannel.getChannelName());
                    tad.repaint();

                    TMLADWriteChannel wr = new TMLADWriteChannel(0, 0, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    //Create nonce and send it
                    TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);
                    if (macNonceInChannels.get(task).contains(channel)) {
                        //Create a nonce operator and a write channel operator
                        TMLADEncrypt nonce = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        nonce.securityContext = "nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1];
                        nonce.type = "Nonce";
                        nonce.message_overhead = overhead;
                        nonce.encTime = encComp;
                        nonce.decTime = decComp;
                        tad.addComponent(nonce, xpos, ypos + yShift, false, true);
                        fromStart.setP2(nonce.getTGConnectingPointAtIndex(0));
                        yShift += 50;
                        wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        //Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
                        //Find matching channels
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size() > 0) {
                            wr.setChannelName(matches.get(0).getName().replaceAll(title + "__", ""));
                        } else {
                            wr.setChannelName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        //send the nonce along the channel
                        wr.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(wr, xpos, ypos + yShift, false, true);
                        wr.makeValue();
                        TGConnector tmp = new TGConnectorTMLAD(wr.getX(), wr.getY() + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, nonce.getTGConnectingPointAtIndex(1), wr.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tad.addComponent(tmp, xpos, ypos, false, true);
                        fromStart = new TGConnectorTMLAD(wr.getX(), wr.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, wr.getTGConnectingPointAtIndex(1), null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        //Connect created write channel operator to start of read channel operator
                        fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(point);
                        //Shift everything from the read channel on down
                        for (TGComponent tg : tad.getComponentList()) {
                            if (tg.getY() >= ypos && tg != nonce && tg != wr) {
                                tg.setCd(tg.getX(), tg.getY() + yShift);
                            }
                        }
                    }

                    //Add decryption operator if it does not already exist
                    xpos = conn.getX();
                    ypos = conn.getY();

                    TMLADDecrypt dec = new TMLADDecrypt(xpos + 10, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    dec.securityContext = "autoEncrypt_" + readChannel.getChannelName();
                    tad.addComponent(dec, dec.getX(), dec.getY(), false, true);
                    conn.setP2(dec.getTGConnectingPointAtIndex(0));
                    yShift += 60;
                    conn = new TGConnectorTMLAD(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, dec.getTGConnectingPointAtIndex(1), next, new Vector<Point>());
                    conn.setP1(dec.getTGConnectingPointAtIndex(1));
                    conn.setP2(next);
                    tad.addComponent(conn, conn.getX(), conn.getY(), false, true);
                    //Shift everything down
                    for (TGComponent tg : tad.getComponentList()) {
                        if (tg instanceof TMLADReadChannel) {
                            readChannel = (TMLADReadChannel) tg;
                            TraceManager.addDev("Inspecting read channel " + readChannel.getChannelName());
                            if (channel.equals(readChannel.getChannelName()) && readChannel.getSecurityContext().equals("")) {
                                TraceManager.addDev("Securing read channel " + readChannel.getChannelName());
                                readChannel.setSecurityContext("autoEncrypt_" + readChannel.getChannelName());

                            }
                        }
                        if (tg.getY() > ypos && tg != dec && tg != comp) {

                            tg.setCd(tg.getX(), tg.getY() + yShift);
                        }
                    }


                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);
                    tad.repaint();
                }
            }
            for (String channel : secInChannels.get(task)) {
                TraceManager.addDev("securing channel " + channel);
                int yShift = 20;
                //        String title = task.getName().split("__")[0];
                TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);
                TGConnector conn = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
                TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);
                //Find read channel operator
                TMLADReadChannel readChannel = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                HashSet<TGComponent> channelInstances = new HashSet<TGComponent>();
                for (TGComponent tg : tad.getComponentList()) {
                    if (tg instanceof TMLADReadChannel) {
                        readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getChannelName().equals(channel) && readChannel.getSecurityContext().equals("")) {
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                channelInstances.add(tg);
                            }
                        }
                    }
                }

                for (TGComponent comp : channelInstances) {

                    fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();
                    conn = tad.findTGConnectorStartingAt(comp.getTGConnectingPointAtIndex(1));
                    next = conn.getTGConnectingPointP2();
                    xpos = fromStart.getX();
                    ypos = fromStart.getY();
                    TMLADWriteChannel wr = new TMLADWriteChannel(0, 0, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    if (nonceInChannels.get(task).contains(channel)) {
                        //Create a nonce operator and a write channel operator
                        TMLADEncrypt nonce = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        nonce.securityContext = "nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1];
                        nonce.type = "Nonce";
                        nonce.message_overhead = overhead;
                        nonce.encTime = encComp;
                        nonce.decTime = decComp;
                        tad.addComponent(nonce, xpos, ypos + yShift, false, true);
                        fromStart.setP2(nonce.getTGConnectingPointAtIndex(0));
                        yShift += 50;
                        wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        //Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
                        //Find matching channels
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size() > 0) {
                            wr.setChannelName(matches.get(0).getName().replaceAll(title + "__", ""));
                        } else {
                            wr.setChannelName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        //send the nonce along the channel
                        wr.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(wr, xpos, ypos + yShift, false, true);
                        wr.makeValue();
                        TGConnector tmp = new TGConnectorTMLAD(wr.getX(), wr.getY() + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, nonce.getTGConnectingPointAtIndex(1), wr.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tad.addComponent(tmp, xpos, ypos, false, true);
                        fromStart = new TGConnectorTMLAD(wr.getX(), wr.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, wr.getTGConnectingPointAtIndex(1), null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        //Connect created write channel operator to start of read channel operator
                        fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(point);
                        //Shift everything from the read channel on down
                        for (TGComponent tg : tad.getComponentList()) {
                            if (tg.getY() >= ypos && tg != nonce && tg != wr) {
                                tg.setCd(tg.getX(), tg.getY() + yShift);
                            }
                        }
                    }
                    //tad.repaint();

                    //Now add the decrypt operator
                    yShift = 40;
                    TraceManager.addDev("Securing read channel " + readChannel.getChannelName());
                    readChannel.setSecurityContext("autoEncrypt_" + readChannel.getChannelName());
                    tad.repaint();
                    //Add decryption operator if it does not already exist
                    xpos = readChannel.getX();
                    ypos = readChannel.getY();
                    TMLADDecrypt dec = new TMLADDecrypt(xpos + 10, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    dec.securityContext = "autoEncrypt_" + readChannel.getChannelName();
                    tad.addComponent(dec, dec.getX(), dec.getY(), false, true);
                    conn.setP2(dec.getTGConnectingPointAtIndex(0));
                    yShift += 100;
                    conn = new TGConnectorTMLAD(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, dec.getTGConnectingPointAtIndex(1), next, new Vector<Point>());
                    conn.setP1(dec.getTGConnectingPointAtIndex(1));

                    conn.setP2(next);
                    tad.addComponent(conn, conn.getX(), conn.getY(), false, true);
                    //Shift everything down
                    for (TGComponent tg : tad.getComponentList()) {
                        if (tg instanceof TMLADReadChannel) {
                            readChannel = (TMLADReadChannel) tg;
                            TraceManager.addDev("Inspecting read channel " + readChannel.getChannelName());
                            if (channel.equals(readChannel.getChannelName()) && readChannel.getSecurityContext().equals("")) {
                                TraceManager.addDev("Securing read channel " + readChannel.getChannelName());
                                readChannel.setSecurityContext("autoEncrypt_" + readChannel.getChannelName());

                            }
                        }
                        if (tg.getY() > ypos && tg != dec) {

                            tg.setCd(tg.getX(), tg.getY() + yShift);
                        }
                    }

                    tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);

                    tad.repaint();
                }
            }
        }
        GTMLModeling gtm = new GTMLModeling(t, false);
        TMLModeling<TGComponent> newmodel = gtm.translateToTMLModeling(false, false);
        for (TMLTask task : newmodel.getTasks()) {
            task.setName(tabName + "_" + name + "__" + task.getName());
        }
        for (TMLTask task : tmlmodel.getTasks()) {
            HwExecutionNode node = (HwExecutionNode) map.getHwNodeOf(task);
            if (newmodel.getTMLTaskByName(task.getName().replace(tabName, tabName + "_" + name)) != null) {
                map.addTaskToHwExecutionNode(newmodel.getTMLTaskByName(task.getName().replace(tabName, tabName + "_" + name)), node);
                map.removeTask(task);
            } else {
                System.out.println("Can't find " + task.getName());
            }
        }
        //map.setTMLModeling(newmodel);
        //System.out.println(map);
        //TMLMapping newMap = gtm.translateToTMLMapping();
        map.setTMLModeling(newmodel);
        return;
	}
}
