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
	Map<String, List<String>> selectedCPUTasks;
	boolean autoConf;
	boolean autoWeakAuth;
	boolean autoStrongAuth;
	int channelIndex=0;

	TMLComponentDesignPanel tmlcdp;
	AVATAR2ProVerif avatar2proverif;
	AvatarSpecification avatarspec;
	ProVerifSpec proverif;
	Map<String, HSMChannel> hsmChannels = new HashMap<String, HSMChannel>();

	Map<String, HSMChannel> secChannels = new HashMap<String, HSMChannel>();

	Map<String, Integer> channelIndexMap = new HashMap<String, Integer>();

	Map<String, List<HSMChannel>> hsmChannelMap = new HashMap<String, List<HSMChannel>>();

	Map<String, String> taskHSMMap = new HashMap<String, String>();
	List<String> hsmTasks = new ArrayList<String>();

	Map<String, String> channelSecMap = new HashMap<String, String>();
	TMLMapping newMap;

	public SecurityGeneration(MainGUI gui, String name, TMLMapping<TGComponent> map, TMLArchiPanel newarch, String encComp, String overhead, String decComp, boolean autoConf, boolean autoWeakAuth, boolean autoStrongAuth,	Map<String, List<String>> selectedCPUTasks){

		this.gui = gui;
		this.name=name;
		this.map=map;
		this.newMap=map;
		this.newarch=newarch;
		this.encComp = encComp;
		this.overhead = overhead;
		this.decComp = decComp;
		this.autoConf=autoConf;
		this.autoWeakAuth = autoWeakAuth;
		this.autoStrongAuth = autoStrongAuth;
		this.selectedCPUTasks = selectedCPUTasks;
	}
	public void proverifAnalysis(TMLMapping<TGComponent> map, List<String> nonAuthChans, List<String> nonConfChans) {
		if (map == null) {
			TraceManager.addDev("No mapping");
			return;
		}

		//Perform ProVerif Analysis
		TML2Avatar t2a = new TML2Avatar(newMap, false, true);
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
				//    	System.out.println("Pragma " + pragma);
				if (confResults.get(pragma).isProved() && !confResults.get(pragma).isSatisfied()) {
					nonConfChans.add(pragma.getArg().getBlock().getName() + "__" + pragma.getArg().getName());
					TraceManager.addDev(pragma.getArg().getBlock().getName() + "." + pragma.getArg().getName() + " is not secret");

					TMLChannel chan = map.getTMLModeling().getChannelByShortName(pragma.getArg().getName().replaceAll("_chData", ""));

					if (chan==null){
						chan = map.getTMLModeling().getChannelByOriginPortName(pragma.getArg().getName().replaceAll("_chData", ""));
					}

					if (chan==null){
						continue;
					}

					if (chan.isBasicChannel()){
						nonConfChans.add(chan.getOriginTask().getName() + "__" + pragma.getArg().getName());
					}
					else {
						for (TMLTask originTask: chan.getOriginTasks()){
							nonConfChans.add(originTask.getName() + "__" + pragma.getArg().getName());
						}
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
			//       TraceManager.addDev("nonConfChans " + nonConfChans);
			//     TraceManager.addDev("nonauthchans " + nonAuthChans);
			//   TraceManager.addDev("all results displayed");

		} catch (Exception e) {
			System.out.println("EXCEPTION " + e);

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
			System.out.println("Error in Security Generation Thread");
		}
		return map;
	}

	public boolean portInTask(TMLTask task, String portName){
		TMLActivityDiagramPanel tad = tmlcdp.getTMLActivityDiagramPanel(task.getName());
		for (TGComponent tg : tad.getComponentList()) {
			if (tg instanceof TMLADWriteChannel) {
				TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
				if (writeChannel.getChannelName().equals(portName)) {
					return true;
				}
			}
			else if (tg instanceof TMLADReadChannel){
				TMLADReadChannel readChannel = (TMLADReadChannel) tg;
				if (readChannel.getChannelName().equals(portName)) {
					return true;
				}
			}
		}
		return false;
	}
	public void run(){
		GTMLModeling gtm = new GTMLModeling(newarch, true);
		map = gtm.translateToTMLMapping();
		// oldmodel = map.getTMLModeling();
		Map<TMLTask, HashSet<TMLTask>> toSecure = new HashMap<TMLTask, HashSet<TMLTask>>();
		Map<TMLTask, HashSet<TMLTask>> toSecureRev = new HashMap<TMLTask, HashSet<TMLTask>>();
		Map<TMLTask, HashSet<String>> secOutChannels = new HashMap<TMLTask, HashSet<String>>();
		Map<TMLTask, HashSet<String>> secInChannels = new HashMap<TMLTask, HashSet<String>>();
		Map<TMLTask, HashSet<String>> nonceOutChannels = new HashMap<TMLTask, HashSet<String>>();
		Map<TMLTask, HashSet<String>> nonceInChannels = new HashMap<TMLTask, HashSet<String>>();
		Map<TMLTask, HashSet<String>> macOutChannels = new HashMap<TMLTask, HashSet<String>>();
		Map<TMLTask, HashSet<String>> macInChannels = new HashMap<TMLTask, HashSet<String>>();

		Map<TMLTask, HashSet<String>> hsmSecInChannels = new HashMap<TMLTask, HashSet<String>>();
		Map<TMLTask, HashSet<String>> hsmSecOutChannels = new HashMap<TMLTask, HashSet<String>>();  

		//TraceManager.addDev("mapping " + map.getSummaryTaskMapping());

		//   Map<String, HSMChannel> secChannels = new HashMap<String, HSMChannel>();
		Map<String, HSMChannel> hsmChannels = new HashMap<String, HSMChannel>();


		for (String cpuName : selectedCPUTasks.keySet()) {
			for (String task: selectedCPUTasks.get(cpuName)){
				hsmTasks.add(task);
				taskHSMMap.put(task, cpuName);    
			}
			hsmChannelMap.put(cpuName, new ArrayList<HSMChannel>());

		}


		//Proverif Analysis channels
		List<String> nonAuthChans = new ArrayList<String>();
		List<String> nonConfChans = new ArrayList<String>();

		proverifAnalysis(map, nonAuthChans, nonConfChans);

		TMLModeling<TGComponent> tmlmodel = map.getTMLModeling();
		List<TMLChannel> channels = tmlmodel.getChannels();
		for (TMLChannel channel : channels) {
			for (TMLCPrimitivePort p : channel.ports) {
				channel.checkConf = channel.checkConf || p.checkConf;
				channel.checkAuth = channel.checkAuth || p.checkAuth;
			}
		}

		//System.out.println("Nonauth " + nonAuthChans);
		//System.out.println("NonConf " + nonConfChans);

		//Create clone of Component Diagram + Activity diagrams to secure
		TGComponent tgcomp = map.getTMLModeling().getTGComponent();
		tmlcdp = (TMLComponentDesignPanel) tgcomp.getTDiagramPanel().tp;
		//        TMLComponentDesignPanel tmlcdp = map.getTMLCDesignPanel();
		int ind = gui.tabs.indexOf(tmlcdp);
		if (ind == -1) {
			TraceManager.addDev("No Component Design Panel");
			return;
		}
		String tabName = gui.getTitleAt(tmlcdp);
		String title = tabName;
		gui.cloneRenameTab(ind, name);
		tmlcdp = (TMLComponentDesignPanel) gui.tabs.get(gui.tabs.size() - 1);

		TMLComponentTaskDiagramPanel tcdp = tmlcdp.tmlctdp;
		//Create clone of architecture panel and map tasks to it
		newarch.renameMapping(tabName, tabName + "_" + name);

		for (TMLTask task : map.getTMLModeling().getTasks()) {
			HashSet<String> tmp = new HashSet<String>();
			HashSet<String> tmp2 = new HashSet<String>();
			HashSet<TMLTask> tmp3 = new HashSet<TMLTask>();
			HashSet<TMLTask> tmp4 = new HashSet<TMLTask>();
			HashSet<String> tmp5 = new HashSet<String>();
			HashSet<String> tmp6 = new HashSet<String>();
			HashSet<String> tmp7 = new HashSet<String>();
			HashSet<String> tmp8 = new HashSet<String>();
			HashSet<String> tmp9 = new HashSet<String>();
			HashSet<String> tmp10 = new HashSet<String>();


			secInChannels.put(task, tmp);
			secOutChannels.put(task, tmp2);
			toSecure.put(task, tmp3);
			toSecureRev.put(task, tmp4);
			nonceInChannels.put(task, tmp5);
			nonceOutChannels.put(task, tmp6);
			macInChannels.put(task, tmp7);
			macOutChannels.put(task, tmp8);

			hsmSecInChannels.put(task, tmp9);
			hsmSecOutChannels.put(task, tmp10);

		}
		//ToSecure keeps a map of origin task: {dest task} for which security operators need to be added
		//ToSecureRev keeps a map of dest task: {origin task} for which security operators need to be added

		//SecOutChannels are channels which need symmetric encryption operators added
		//SecInChannels are channels which need sym decryption operators added
		//nonceInChannels are channels which need to send a nonce before receiving the channel data 
		//nonceOutChannels are channels which need to receive a nonce before sending the channel data
		//macInChannels are channels which need verifymac operators added
		//macOutChannels are channels which need mac operators added

		//hsmSecInChannels need to send data to the hsm to decrypt after receiving channel data
		//hsmSecOutChannels need to send data to the hsm to encrypt before sending channel data

		//With the proverif results, check which channels need to be secured
		for (TMLTask task : map.getTMLModeling().getTasks()) {
			//System.out.println("Task " + task.getName());
			//Check if all channel operators are secured
			TMLActivityDiagramPanel tad = tmlcdp.getTMLActivityDiagramPanel(task.getName());
			if (tad==null){
				continue;

			}

			for (TMLChannel chan: tmlmodel.getChannels(task)){ 	
				//Origin and Destination ports can have different names. Therefore, we need to put both names in the list of channels to secure.
				List<String> portNames = new ArrayList<String>();
				boolean nonConf = false;
				boolean nonAuth = false;
				if (chan.isBasicChannel()){

					portNames.add(chan.getOriginPort().getName());
					portNames.add(chan.getDestinationPort().getName());
					if (nonConfChans.contains(chan.getOriginTask().getName().split("__")[1] + "__" + chan.getOriginPort().getName() + "_chData")){
						nonConf=true;
					}
					if (nonAuthChans.contains(chan.getDestinationTask().getName().split("__")[1] + "__" + title + "__" + chan.getDestinationPort().getName())){
						nonAuth=true;
					}
					//When port names are different
					if (nonAuthChans.contains(chan.getDestinationTask().getName().split("__")[1] + "__" + chan.getName())){
						nonAuth=true;
					}
				}
				else {
					for (TMLPort port: chan.getOriginPorts()){
						for (TMLTask origTask: chan.getOriginTasks()){
							if (nonConfChans.contains(origTask.getName().split("__")[1] + "__" + port.getName() + "_chData")){
								nonConf=true;
							}
						}
						portNames.add(port.getName());
					}


					for (TMLTask destTask: chan.getDestinationTasks()){
						for (TMLPort port: chan.getDestinationPorts()){
							if (nonAuthChans.contains(destTask.getName().split("__")[1] + "__" + title + "__" + port.getName())){
								nonAuth=true;
							}
							if (!portNames.contains(port.getName())){
								portNames.add(port.getName());
							}
						}
						//System.out.println(destTask.getName().split("__")[1] + "__" + chan.getName());
						if  (nonAuthChans.contains(destTask.getName().split("__")[1] + "__" + chan.getName())){
							nonAuth=true;
						}

					}
					//When port names are different

				}



				//   System.out.println("channel " + chan.getName() + " ports " + portNames);

				String secName = chan.getName().split("__")[chan.getName().split("__").length-1];

				for (String chanName: portNames){
					//Classify channels based on the type of security requirements and unsatisfied properties
					if (chan.isBasicChannel()){
						if (chan.checkConf && autoConf && nonConf) {
							toSecure.get(chan.getOriginTask()).add(chan.getDestinationTask());
							if (chan.checkAuth && autoStrongAuth) {
								if (!toSecureRev.get(chan.getDestinationTask()).contains(chan.getOriginTask())){
									toSecureRev.get(chan.getDestinationTask()).add(chan.getOriginTask());
								}
							}
							if (hsmTasks.contains(chan.getOriginTask().getName().split("__")[1])){
								channelSecMap.put(chanName, "hsmSec_"+secName);
								if (!hsmSecOutChannels.get(chan.getOriginTask()).contains(chanName)  && portInTask(chan.getOriginTask(),chanName)){
									HSMChannel hsmchan = new HSMChannel(chanName,  chan.getOriginTask().getName().split("__")[1], HSMChannel.SENC);
									hsmChannelMap.get(taskHSMMap.get(chan.getOriginTask().getName().split("__")[1])).add(hsmchan);
									hsmSecOutChannels.get(chan.getOriginTask()).add(chanName);

									if (chan.checkAuth && autoStrongAuth) {
										nonceOutChannels.get(chan.getOriginTask()).add(chanName);
										hsmchan.nonceName="nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
									}
								}
							}
							else {
								if (!secOutChannels.get(chan.getOriginTask()).contains(chanName)) {
									secOutChannels.get(chan.getOriginTask()).add(chanName);
									channelSecMap.put(chanName, "autoEncrypt_"+secName);	
									if (chan.checkAuth && autoStrongAuth) {
										nonceOutChannels.get(chan.getOriginTask()).add(chanName);
									}
								}
							}

							if (hsmTasks.contains(chan.getDestinationTask().getName().split("__")[1])){
								if (!hsmSecInChannels.get(chan.getDestinationTask()).contains(chanName)  && portInTask(chan.getDestinationTask(),chanName) ) {
									HSMChannel hsmchan = new HSMChannel(chanName,  chan.getDestinationTask().getName().split("__")[1], HSMChannel.DEC);
									hsmChannelMap.get(taskHSMMap.get(chan.getDestinationTask().getName().split("__")[1])).add(hsmchan);
									hsmSecInChannels.get(chan.getDestinationTask()).add(chanName);
									if (chan.checkAuth && autoStrongAuth) {
										nonceInChannels.get(chan.getDestinationTask()).add(chanName);
										hsmchan.nonceName="nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
									}
								}
							}
							else {
								if (!secInChannels.get(chan.getDestinationTask()).contains(chanName)) {
									secInChannels.get(chan.getDestinationTask()).add(chanName);
									if (chan.checkAuth && autoStrongAuth) {
										nonceInChannels.get(chan.getDestinationTask()).add(chanName);
									}
								}
							}                            	

						} else if (chan.checkAuth && autoWeakAuth && nonAuth) {
							toSecure.get(chan.getOriginTask()).add(chan.getDestinationTask());
							if (autoStrongAuth) {
								if (!toSecureRev.get(chan.getDestinationTask()).contains(chan.getOriginTask())){
									toSecureRev.get(chan.getDestinationTask()).add(chan.getOriginTask());
								}
								/*}
								  else {
								  TMLChannel chantmp = oldmodel.getChannelByShortName("__"+chan.getName());
								  System.out.println("Channel found "+ chantmp);
								  }*/

						}
						if (hsmTasks.contains(chan.getOriginTask().getName().split("__")[1])){
							channelSecMap.put(chanName, "hsmSec_"+secName);
							if (!hsmSecOutChannels.get(chan.getOriginTask()).contains(chanName) && portInTask(chan.getOriginTask(),chanName)){
								HSMChannel hsmchan = new HSMChannel(chanName,  chan.getOriginTask().getName().split("__")[1], HSMChannel.MAC);
								hsmChannelMap.get(taskHSMMap.get(chan.getOriginTask().getName().split("__")[1])).add(hsmchan);
								hsmSecOutChannels.get(chan.getOriginTask()).add(chanName);

								if (autoStrongAuth) {
									nonceOutChannels.get(chan.getOriginTask()).add(chanName);
									hsmchan.nonceName="nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
								}
							}
						}
						else {
							if (!macInChannels.get(chan.getOriginTask()).contains(chanName)) {
								macOutChannels.get(chan.getOriginTask()).add(chanName);
								channelSecMap.put(chanName, "autoEncrypt_"+secName);	
								if (autoStrongAuth) {
									nonceOutChannels.get(chan.getOriginTask()).add(chanName);
								}
							}
						}

						if (hsmTasks.contains(chan.getDestinationTask().getName().split("__")[1])){
							if (!hsmSecInChannels.get(chan.getDestinationTask()).contains(chanName) && portInTask(chan.getDestinationTask(),chanName)) {
								HSMChannel hsmchan = new HSMChannel(chanName,  chan.getDestinationTask().getName().split("__")[1], HSMChannel.DEC);
								hsmChannelMap.get(taskHSMMap.get(chan.getDestinationTask().getName().split("__")[1])).add(hsmchan);
								hsmSecInChannels.get(chan.getDestinationTask()).add(chanName);
								if (chan.checkAuth && autoStrongAuth) {
									nonceInChannels.get(chan.getDestinationTask()).add(chanName);
									hsmchan.nonceName="nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
								}
							}
						}
						else {
							if (!secInChannels.get(chan.getDestinationTask()).contains(chanName)) {
								secInChannels.get(chan.getDestinationTask()).add(chanName);
								if (chan.checkAuth && autoStrongAuth) {
									nonceInChannels.get(chan.getDestinationTask()).add(chanName);
								}
							}
						}  
					}
				}
				else {
					//Ignore strong authenticity for fork and join channels
					//Only add hsm channel for own port
					for (TMLTask orig: chan.getOriginTasks()){
						for (TMLTask dest:chan.getDestinationTasks()){
							if (chan.checkConf && autoConf && nonConf) {
								toSecure.get(orig).add(dest);
								/*if (chan.checkAuth && autoStrongAuth) {
								  if (!toSecureRev.get(dest).contains(orig)){
								  toSecureRev.get(dest).add(orig);
								  }
								  }*/
								if (hsmTasks.contains(orig.getName().split("__")[1])){
									channelSecMap.put(chanName, "hsmSec_"+secName);
									if (!hsmSecOutChannels.get(orig).contains(chanName) && portInTask(orig,chanName)){
										HSMChannel hsmchan = new HSMChannel(chanName,  orig.getName().split("__")[1], HSMChannel.SENC);
										hsmChannelMap.get(taskHSMMap.get(orig.getName().split("__")[1])).add(hsmchan);
										hsmSecOutChannels.get(orig).add(chanName);

										/*	if (chan.checkAuth && autoStrongAuth) {
											nonceOutChannels.get(orig).add(chanName);
											hsmchan.nonceName="nonce_" + dest.getName().split("__")[1] + "_" + orig.getName().split("__")[1];
											}*/
									}
								}
								else {
									if (!secOutChannels.get(orig).contains(chanName)) {
										secOutChannels.get(orig).add(chanName);
										channelSecMap.put(chanName, "autoEncrypt_"+secName);	
										/* if (chan.checkAuth && autoStrongAuth) {
										   nonceOutChannels.get(orig).add(chanName);
										   }*/
									}
								}

								if (hsmTasks.contains(dest.getName().split("__")[1])){
									if (!hsmSecInChannels.get(dest).contains(chanName) && portInTask(dest,chanName)) {
										HSMChannel hsmchan = new HSMChannel(chanName,  dest.getName().split("__")[1], HSMChannel.DEC);
										hsmChannelMap.get(taskHSMMap.get(dest.getName().split("__")[1])).add(hsmchan);
										hsmSecInChannels.get(dest).add(chanName);
										/*	if (chan.checkAuth && autoStrongAuth) {
											nonceInChannels.get(dest).add(chanName);
											hsmchan.nonceName="nonce_" + dest.getName().split("__")[1] + "_" + orig.getName().split("__")[1];
											}*/
									}
								}
								else {
									if (!secInChannels.get(dest).contains(chanName)) {
										secInChannels.get(dest).add(chanName);
										/*if (chan.checkAuth && autoStrongAuth) {
										  nonceInChannels.get(dest).add(chanName);
										  }*/
									}
								}                            	

							} else if (chan.checkAuth && autoWeakAuth && nonAuth) {
								toSecure.get(orig).add(dest);
								/*	if (autoStrongAuth) {
								/*  		if (chan.getOriginTask().getReferenceObject() instanceof TMLCPrimitiveComponent && chan.getDestinationTask().getReferenceObject() instanceof TMLCPrimitiveComponent){*/
								/*if (!toSecureRev.get(dest).contains(orig)){
								  toSecureRev.get(dest).add(orig);
								  }*/
								/*}
								  else {
								  TMLChannel chantmp = oldmodel.getChannelByShortName("__"+chan.getName());
								  System.out.println("Channel found "+ chantmp);
								  }

								  }*/
								if (hsmTasks.contains(orig.getName().split("__")[1])){
									channelSecMap.put(chanName, "hsmSec_"+secName);
									if (!hsmSecOutChannels.get(orig).contains(chanName) && portInTask(orig,chanName)){
										HSMChannel hsmchan = new HSMChannel(chanName,  orig.getName().split("__")[1], HSMChannel.MAC);
										hsmChannelMap.get(taskHSMMap.get(orig.getName().split("__")[1])).add(hsmchan);
										hsmSecOutChannels.get(orig).add(chanName);

										/*if (autoStrongAuth) {
										  nonceOutChannels.get(orig).add(chanName);
										  hsmchan.nonceName="nonce_" + dest.getName().split("__")[1] + "_" + orig.getName().split("__")[1];
										  }*/
									}
								}
								else {
									if (!macInChannels.get(orig).contains(chanName)) {
										macOutChannels.get(orig).add(chanName);
										channelSecMap.put(chanName, "autoEncrypt_"+secName);	
										/*   if (autoStrongAuth) {
										     nonceOutChannels.get(orig).add(chanName);
										     }*/
									}
								}

								if (hsmTasks.contains(dest.getName().split("__")[1])){

									if (!hsmSecInChannels.get(dest).contains(chanName) && portInTask(dest,chanName)) {
										HSMChannel hsmchan = new HSMChannel(chanName,  dest.getName().split("__")[1], HSMChannel.DEC);
										hsmChannelMap.get(taskHSMMap.get(dest.getName().split("__")[1])).add(hsmchan);
										hsmSecInChannels.get(dest).add(chanName);
										/*if (chan.checkAuth && autoStrongAuth) {
										  nonceInChannels.get(dest).add(chanName);
										  hsmchan.nonceName="nonce_" + dest.getName().split("__")[1] + "_" + orig.getName().split("__")[1];
										  }*/
									}
								}
								else {
									if (!secInChannels.get(dest).contains(chanName)) {
										secInChannels.get(dest).add(chanName);
										/*if (chan.checkAuth && autoStrongAuth) {
										  nonceInChannels.get(dest).add(chanName);
										  }*/
									}
								}  
							}
						}
					}
				}

			}
		}
	}

	// System.out.println("hsmchannelmap" + hsmChannelMap);
	TraceManager.addDev("secChannelMap" + channelSecMap);

	TraceManager.addDev("macoutchans " + macOutChannels);
	TraceManager.addDev("macinchans " + macInChannels);
	TraceManager.addDev("nonsecin " + secInChannels);
	TraceManager.addDev("nonsecout " + secOutChannels);
	TraceManager.addDev("noncein " + nonceInChannels);
	TraceManager.addDev("nonceout " + nonceOutChannels);
	TraceManager.addDev("hsmsecin " + hsmSecInChannels);
	TraceManager.addDev("hsmsecout " + hsmSecOutChannels);        
	TraceManager.addDev("tosecrev " + toSecureRev);

	//Add a HSM Task for each selected CPU on the component diagram, add associated channels, etc
	for (String cpuName : selectedCPUTasks.keySet()) {
		TMLCPrimitiveComponent hsm = new TMLCPrimitiveComponent(0, tcdp.getMaxY()-200, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxY(), false, null, tcdp);
		TAttribute index = new TAttribute(2, "channelIndex", "0", 0);
		hsm.getAttributeList().add(index);
		tcdp.addComponent(hsm, 0, 500, false, true);
		hsm.setValueWithChange("HSM_" + cpuName);

		//Find all associated components
		List<TMLCPrimitiveComponent> comps = new ArrayList<TMLCPrimitiveComponent>();
		//Find the component to add a HSM to

		for (TGComponent tg : tcdp.getComponentList()) {
			if (tg instanceof TMLCPrimitiveComponent) {
				for (String compName : selectedCPUTasks.get(cpuName)) {
					if (tg.getValue().equals(compName)) {
						comps.add((TMLCPrimitiveComponent) tg);
						break;
					}
				}
			} else if (tg instanceof TMLCCompositeComponent) {
				TMLCCompositeComponent cc = (TMLCCompositeComponent) tg;
				List<TMLCPrimitiveComponent> pcomps = cc.getAllPrimitiveComponents();
				for (TMLCPrimitiveComponent pc : pcomps) {
					for (String compName : selectedCPUTasks.get(cpuName)) {
						if (pc.getValue().equals(compName)) {
							comps.add(pc);
							break;
						}
					}
				}
			}
		}
		if (comps.size() == 0) {
			//System.out.println("No Components found");
			continue;
		}

		for (TMLCPrimitiveComponent comp : comps) {

			Map<String, HSMChannel> compChannels = new HashMap<String, HSMChannel>();
			String compName = comp.getValue();

			List<ChannelData> hsmChans = new ArrayList<ChannelData>();
			ChannelData chd = new ChannelData("startHSM_" + cpuName, false, false);
			hsmChans.add(chd);
			for (HSMChannel hsmChan : hsmChannelMap.get(cpuName)) {
				if (!hsmChan.task.equals(comp.getValue())){
					continue;
				}
				if (!channelIndexMap.containsKey(hsmChan.name)){
					channelIndexMap.put(hsmChan.name,channelIndex);
					channelIndex++;
				}   
				chd = new ChannelData("data_" + hsmChan.name + "_" + hsmChan.task, false, true);
				hsmChans.add(chd);
				chd = new ChannelData("retData_" + hsmChan.name + "_" + hsmChan.task, true, true);
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
					destPort.setParam(0, new TType(1));
				}
				destPort.isOrigin = !hsmChan.isOrigin;

				tcdp.addComponent(destPort, comp.getX(), comp.getY(), true, true);

				TMLCPortConnector conn = new TMLCPortConnector(0, 0, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, null, tcdp, originPort.getTGConnectingPointAtIndex(0), destPort.getTGConnectingPointAtIndex(0), new Vector<Point>());
				tcdp.addComponent(conn, 0, 0, false, true);
			}
		}
	}


	for (String cpuName : selectedCPUTasks.keySet()) {
		buildHSMActivityDiagram(cpuName);
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
		TMLArchiMemoryNode mem = new TMLArchiMemoryNode(cpu.getX(), archPanel.getMaxY()-800, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel);
		archPanel.addComponent(mem, cpu.getX(), archPanel.getMaxY()-400, false, true);
		mem.setName("HSMMemory_" + cpuName);
		//Add Hardware Accelerator

		TMLArchiHWANode hwa = new TMLArchiHWANode(cpu.getX(), archPanel.getMaxY()-300, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel);
		archPanel.addComponent(hwa, cpu.getX() + 100, cpu.getY() + 100, false, true);
		hwa.setName("HSM_" + cpuName);
		//Add hsm task to hwa


		TMLArchiArtifact hsmArt = new TMLArchiArtifact(cpu.getX(), archPanel.getMaxY()- 280, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, hwa, archPanel);
		archPanel.addComponent(hsmArt, cpu.getX() + 100, archPanel.getMaxY()- 280, true, true);
		hsmArt.setFullName("HSM_" + cpuName, refTask);
		//Add bus connecting the cpu and HWA

		TMLArchiBUSNode bus = new TMLArchiBUSNode(cpu.getX() + 50, archPanel.getMaxY() -500, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel);
		bus.setPrivacy(1);
		bus.setName("HSMBus_" + cpuName);
		archPanel.addComponent(bus, cpu.getX() + 200, cpu.getY() + 200, false, true);

		//Connect Bus and CPU
		TMLArchiConnectorNode connect = new TMLArchiConnectorNode(cpu.getX() + 100, archPanel.getMaxY() -300, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel, null, null, new Vector<Point>());
		TGConnectingPoint p1 = bus.findFirstFreeTGConnectingPoint(true, true);
		p1.setFree(false);
		connect.setP2(p1);


		TGConnectingPoint p2 = cpu.findFirstFreeTGConnectingPoint(true, true);
		p2.setFree(false);
		connect.setP1(p2);
		archPanel.addComponent(connect, cpu.getX() + 100, cpu.getY() + 100, false, true);
		//Connect Bus and HWA

		connect = new TMLArchiConnectorNode(cpu.getX() + 100, cpu.getY() + 100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel, null, null, new Vector<Point>());
		p1 = bus.findFirstFreeTGConnectingPoint(true, true);
		p1.setFree(false);
		connect.setP2(p1);

		p2 = hwa.findFirstFreeTGConnectingPoint(true, true);
		p2.setFree(false);
		connect.setP1(p2);

		archPanel.addComponent(connect, cpu.getX() + 100, cpu.getY() + 100, false, true);
		//Connect Bus and Memory

		connect = new TMLArchiConnectorNode(cpu.getX() + 100, cpu.getY() + 100, archPanel.getMinX(), archPanel.getMaxX(), archPanel.getMinY(), archPanel.getMaxY(), true, null, archPanel, null, null, new Vector<Point>());
		p1 = bus.findFirstFreeTGConnectingPoint(true, true);
		p1.setFree(false);
		connect.setP2(p1);

		p2 = mem.findFirstFreeTGConnectingPoint(true, true);
		p2.setFree(false);
		connect.setP1(p2);
		archPanel.addComponent(connect, cpu.getX() + 100, cpu.getY() + 100, false, true);
	}

	//        
	//        int num=0;
	//int nonceNum=0;
	//Create reverse channels on component diagram to send nonces if they don't already exist


	for (TMLTask task : toSecureRev.keySet()) {
		// TraceManager.addDev("Adding nonces to " + task.getName());
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

				if (task.getReferenceObject() instanceof TMLCPrimitiveComponent){
					TGComponent tg = (TGComponent) task.getReferenceObject();
					originPort = new TMLCChannelOutPort(tg.getX(), tg.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, tg, tcdp);
					originPort.commName = "nonceCh" + task.getName().split("__")[1] + "_" + task2.getName().split("__")[1];
					tcdp.addComponent(originPort, tg.getX(), tg.getY(), true, true);
				}
				if (task2.getReferenceObject() instanceof TMLCPrimitiveComponent){
					TGComponent tg = (TGComponent) task2.getReferenceObject();
					destPort = new TMLCChannelOutPort(tg.getX(), tg.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, tg, tcdp);
					destPort.isOrigin = false;
					destPort.commName = "nonceCh" + task.getName().split("__")[1] + "_" + task2.getName().split("__")[1];
					tcdp.addComponent(destPort, tg.getX(), tg.getY(), true, true);
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
	TraceManager.addDev("Securing task " + task.getName());
	TMLActivityDiagramPanel tad = tmlcdp.getTMLActivityDiagramPanel(task.getName());
	if (tad==null){
		continue;
	}
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
		if (tmlc==null){
			//Find by origin port instead
			tmlc = tmlmodel.getChannelByOriginPortName(channel);
		}
		if (tmlc==null){
			continue;
		}
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
			yShift=50;
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
				//System.out.println("tmlc " + tmlc);
				//					System.out.println("Checking "+ tmlc.getDestinationTask() + " " + tmlc.getOriginTask());
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

			enc.securityContext = channelSecMap.get(channel);
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
					if (channel.equals(wChannel.getChannelName()) && wChannel.getSecurityContext().equals("")) {
						wChannel.setSecurityContext(channelSecMap.get(channel));
						wChannel.setEncForm(true);

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
		if (tmlc ==null){
			tmlc = tmlmodel.getChannelByOriginPortName(channel);
		}
		if (tmlc ==null){
			continue;
		}
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
			yShift=50;
			//TMLADWriteChannel writeChannel = (TMLADWriteChannel) comp;
			xpos = comp.getX();
			ypos = comp.getY();
			fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
			point = fromStart.getTGConnectingPointP2();

			TMLADEncrypt enc = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

			//If we need to receive a nonce
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

			//Add encryption operator

			enc.securityContext = channelSecMap.get(channel);
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
					if (channel.equals(wChannel.getChannelName()) && wChannel.getSecurityContext().equals("")) {
						wChannel.setSecurityContext(channelSecMap.get(channel));
						wChannel.setEncForm(true);
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
	for (String channel: hsmSecOutChannels.get(task)){
		Set<TGComponent> channelInstances = new HashSet<TGComponent>();
		TGConnector conn = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
		TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);     
		for (TGComponent tg : tad.getComponentList()) {
			if (tg instanceof TMLADWriteChannel) {
				TMLADWriteChannel writeChannel = (TMLADWriteChannel) tg;
				if (writeChannel.getChannelName().equals(channel) && writeChannel.getSecurityContext().equals("")) {
					fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
					if (fromStart != null) {
						channelInstances.add(tg);
					}
				}
			}
		}
		for (TGComponent chan : channelInstances) {

			TMLADWriteChannel writeChannel = (TMLADWriteChannel) chan;
			String chanName = writeChannel.getChannelName();
			TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + chanName);
			if (tmlc ==null){
				tmlc = tmlmodel.getChannelByOriginPortName(channel);
			}
			if (tmlc ==null){
				continue;
			}
			writeChannel.setSecurityContext(channelSecMap.get(chanName));
			writeChannel.setEncForm(true);
			xpos = chan.getX();
			ypos = chan.getY();
			fromStart = tad.findTGConnectorEndingAt(chan.getTGConnectingPointAtIndex(0));
			point = fromStart.getTGConnectingPointP2();

			int yShift = 50;




			TMLADSendRequest req = new TMLADSendRequest(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			req.setRequestName("startHSM_" + taskHSMMap.get(task.getName().split("__")[1]));


			req.setParam(0, Integer.toString(channelIndexMap.get(chanName)));
			req.makeValue();
			tad.addComponent(req, xpos, ypos + yShift, false, true);

			fromStart.setP2(req.getTGConnectingPointAtIndex(0));
			//tad.addComponent(fromStart, xpos, ypos, false, true);

			//Add connection
			fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
			fromStart.setP1(req.getTGConnectingPointAtIndex(1));





			TMLADWriteChannel wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);



			yShift += 50;
			//Add write channel operator
			wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			wr.setChannelName("data_" + chanName + "_" + task.getName().split("__")[1]);
			wr.setEncForm(false);
			wr.setSecurityContext(channelSecMap.get(chanName));
			tad.addComponent(wr, xpos, ypos + yShift, false, true);


			fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
			tad.addComponent(fromStart, xpos, ypos, false, true);

			fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
			tad.addComponent(fromStart, xpos, ypos, false, true);
			fromStart.setP1(wr.getTGConnectingPointAtIndex(1));


			TMLADReadChannel rd = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

			TMLADWriteChannel wr2 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

			//Receive any nonces if ensuring authenticity           
			if (nonceOutChannels.get(task).contains(channel)) {
				//Read nonce from rec task
				yShift+=50;

				List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());
				rd = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
				if (matches.size() > 0) {
					rd.setChannelName(matches.get(0).getName().replaceAll(title + "__", ""));
				} else {
					rd.setChannelName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
				}
				rd.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
				tad.addComponent(rd, xpos, ypos + yShift, false, true);
				fromStart.setP2(rd.getTGConnectingPointAtIndex(0));

				fromStart = new TGConnectorTMLAD(rd.getX(), rd.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
				tad.addComponent(fromStart, xpos, ypos, false, true);
				fromStart.setP1(rd.getTGConnectingPointAtIndex(1));


				//Also send nonce to hsm
				yShift+=50;
				wr2 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);       
				wr2.setChannelName("data_" + chanName + "_" + task.getName().split("__")[1]);
				wr2.setSecurityContext(channelSecMap.get(chanName));
				tad.addComponent(wr2, xpos, ypos + yShift, false, true);

				TGConnectorTMLAD tmp = new TGConnectorTMLAD(wr2.getX(), wr2.getY() + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, rd.getTGConnectingPointAtIndex(1), wr2.getTGConnectingPointAtIndex(0), new Vector<Point>());
				tad.addComponent(tmp, xpos, ypos, false, true);

				fromStart.setP2(wr2.getTGConnectingPointAtIndex(0));

				fromStart = new TGConnectorTMLAD(rd.getX(), rd.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
				tad.addComponent(fromStart, xpos, ypos, false, true);
				fromStart.setP1(wr2.getTGConnectingPointAtIndex(1));



				yShift += 60;
			}


			//Read channel operator to receive hsm data

			yShift += 60;
			TMLADReadChannel rd2 = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			rd2.setChannelName("retData_" + chanName + "_" + task.getName().split("__")[1]);
			rd2.setSecurityContext(channelSecMap.get(chanName));
			tad.addComponent(rd2, xpos, ypos + yShift, false, true);

			fromStart.setP2(rd2.getTGConnectingPointAtIndex(0));
			yShift += 50;

			//Add connector
			fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
			tad.addComponent(fromStart, xpos, ypos, false, true);
			fromStart.setP1(rd2.getTGConnectingPointAtIndex(1));
			yShift += 50;

			//Direct the last TGConnector back to the start of the write channel operator


			fromStart.setP2(point);
			//Shift components down to make room for the added ones, and add security contexts to write channels
			for (TGComponent tg : tad.getComponentList()) {
				if (tg.getY() >= ypos && tg != wr && tg != req && tg != rd && tg!=wr2 && tg!=rd2) {
					tg.setCd(tg.getX(), tg.getY() + yShift);
				}
			}
			tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);
			tad.repaint();        
		}   	
	}

	for (String channel: hsmSecInChannels.get(task)){
		//System.out.println("Checking hsmsecinchannel " + channel + " " + task.getName());
		Set<TGComponent> channelInstances = new HashSet<TGComponent>();
		TGConnector conn = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
		TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);     
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
		//System.out.println("matches " + channelInstances);
		for (TGComponent chan : channelInstances) {
			TMLADReadChannel readChannel = (TMLADReadChannel) chan;
			String chanName = readChannel.getChannelName();
			TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + chanName);
			if (tmlc ==null){
				tmlc = tmlmodel.getChannelByDestinationPortName(channel);
			}
			if (tmlc ==null){
				continue;
			}
			readChannel.setSecurityContext(channelSecMap.get(chanName));
			readChannel.setEncForm(true);
			xpos = chan.getX()+1;
			ypos = chan.getY()+1;
			fromStart = tad.findTGConnectorStartingAt(chan.getTGConnectingPointAtIndex(1));
			point = fromStart.getTGConnectingPointP2();

			int yShift = 50;





			TMLADSendRequest req = new TMLADSendRequest(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			req.setRequestName("startHSM_" + taskHSMMap.get(task.getName().split("__")[1]));




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
			wr.setChannelName("data_" + chanName + "_" + task.getName().split("__")[1]);
			wr.setSecurityContext(channelSecMap.get(chanName));
			wr.makeValue();
			tad.addComponent(wr, xpos, ypos + yShift, false, true);

			//Add connector between request and write
			fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
			tad.addComponent(fromStart, xpos, ypos, false, true);

			//Add connector between write and ???
			fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
			tad.addComponent(fromStart, xpos, ypos, false, true);
			fromStart.setP1(wr.getTGConnectingPointAtIndex(1));






			//If needed, forge nonce, send it to receiving task
			TMLADEncrypt nonce = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			TMLADWriteChannel wr3 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			TMLADWriteChannel wr2 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

			if (nonceInChannels.get(task).contains(channel)) {
				//Create a nonce operator and a write channel operator
				yShift+=60;
				nonce = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
				nonce.securityContext = "nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1];
				nonce.type = "Nonce";
				nonce.message_overhead = overhead;
				nonce.encTime = encComp;
				nonce.decTime = decComp;
				tad.addComponent(nonce, xpos, ypos + yShift, false, true);
				fromStart.setP2(nonce.getTGConnectingPointAtIndex(0));
				yShift += 50;

				wr3 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
				//Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
				//Find matching channels
				List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

				if (matches.size() > 0) {
					wr3.setChannelName(matches.get(0).getName().replaceAll(title + "__", ""));
				} else {
					wr3.setChannelName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
				}
				//send the nonce along the channel
				wr3.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
				tad.addComponent(wr3, xpos, ypos + yShift, false, true);

				wr3.makeValue();
				TGConnector tmp = new TGConnectorTMLAD(wr3.getX(), wr3.getY() + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, nonce.getTGConnectingPointAtIndex(1), wr3.getTGConnectingPointAtIndex(0), new Vector<Point>());
				tad.addComponent(tmp, xpos, ypos, false, true);


				//Also send nonce to hsm
				yShift+=50;
				wr2 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
				wr2.setChannelName("data_" + chanName + "_" + task.getName().split("__")[1]);
				wr2.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
				tad.addComponent(wr2, xpos, ypos + yShift, false, true);

				tmp = new TGConnectorTMLAD(wr2.getX(), wr2.getY() + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, wr3.getTGConnectingPointAtIndex(1), wr2.getTGConnectingPointAtIndex(0), new Vector<Point>());
				tad.addComponent(tmp, xpos, ypos, false, true);

				fromStart = new TGConnectorTMLAD(wr2.getX(), wr.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, wr2.getTGConnectingPointAtIndex(1), null, new Vector<Point>());
				tad.addComponent(fromStart, xpos, ypos, false, true);
				//Connect created write channel operator to start of read channel operator
				fromStart.setP1(wr2.getTGConnectingPointAtIndex(1));
				fromStart.setP2(point);






				/*  //Shift everything from the read channel on down
				    for (TGComponent tg : tad.getComponentList()) {
				    if (tg.getY() >= ypos && tg != nonce && tg != wr2 && tg!=wr3) {
				    tg.setCd(tg.getX(), tg.getY() + yShift);
				    }
				    }*/
			}




			//Add read channel operator

			yShift += 60;
			TMLADReadChannel rd = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			rd.setChannelName("retData_" + chanName + "_" + task.getName().split("__")[1]);
			rd.setSecurityContext(channelSecMap.get(chanName));
			rd.setEncForm(false);
			tad.addComponent(rd, xpos, ypos + yShift, false, true);

			fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
			yShift += 50;

			//Add connector
			fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
			tad.addComponent(fromStart, xpos, ypos, false, true);
			fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
			yShift += 50;

			//Direct the last TGConnector back to the start of the operator after the read channel


			fromStart.setP2(point);

			//Shift components down to make room for the added ones, and add security contexts to write channels
			for (TGComponent tg : tad.getComponentList()) {
				if (tg.getY() >= ypos && tg != wr && tg != req && tg != rd && tg!=wr2 && tg!=nonce && tg!=wr3) {
					tg.setCd(tg.getX(), tg.getY() + yShift);
				}
			}
			tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);
			tad.repaint();        
		}   	
	}
	for (String channel : macInChannels.get(task)) {
		//Add decryptmac after readchannel
		int yShift = 50;
		Set<TGComponent> channelInstances = new HashSet<TGComponent>();
		TGConnector conn = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
		TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);
		//Find read channel operator
		TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);                
		if (tmlc ==null){
			tmlc = tmlmodel.getChannelByDestinationPortName(channel);
		}
		if (tmlc ==null){
			continue;
		}
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
			yShift=50;
			fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
			point = fromStart.getTGConnectingPointP2();
			conn = tad.findTGConnectorStartingAt(comp.getTGConnectingPointAtIndex(1));
			next = conn.getTGConnectingPointP2();
			xpos = fromStart.getX();
			ypos = fromStart.getY();


			TMLADReadChannel readChannel = (TMLADReadChannel) comp;
			readChannel.setSecurityContext(channelSecMap.get(readChannel.getChannelName()));
			tad.repaint();

			TMLADWriteChannel wr = new TMLADWriteChannel(0, 0, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			//Create nonce and send it
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
				yShift+=40;
				//Shift everything from the read channel on down
				for (TGComponent tg : tad.getComponentList()) {
					if ((tg.getY() >= ypos && tg != nonce && tg != wr) || tg == readChannel) {
						tg.setCd(tg.getX(), tg.getY() + yShift);
					}
				}
			}

			//Add decryption operator if it does not already exist
			xpos = conn.getX();
			ypos = conn.getY();

			TMLADDecrypt dec = new TMLADDecrypt(xpos + 10, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			dec.securityContext = channelSecMap.get(readChannel.getChannelName());
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

					if (channel.equals(readChannel.getChannelName()) && readChannel.getSecurityContext().equals("")) {
						readChannel.setSecurityContext(channelSecMap.get(readChannel.getChannelName()));
						readChannel.setEncForm(true);

					}
				}
				if (tg.getY() >= ypos && tg != dec && tg != comp) {

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

		TGConnector conn = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
		TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);
		//Find read channel operator
		TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);                
		if (tmlc ==null){
			tmlc = tmlmodel.getChannelByDestinationPortName(channel);
		}
		if (tmlc ==null){
			continue;
		}
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
			yShift=30;
			readChannel = (TMLADReadChannel) comp;
			fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
			point = fromStart.getTGConnectingPointP2();
			conn = tad.findTGConnectorStartingAt(comp.getTGConnectingPointAtIndex(1));
			next = conn.getTGConnectingPointP2();
			xpos = comp.getX();
			ypos = comp.getY();
			TMLADWriteChannel wr = new TMLADWriteChannel(0, 0, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			TMLADEncrypt nonce = new TMLADEncrypt(0, 0, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			if (nonceInChannels.get(task).contains(channel)) {
				//Create a nonce operator and a write channel operator
				nonce = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
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
				yShift+=40;
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
					if ((tg.getY() >= ypos && tg != nonce && tg != wr)) {
						tg.setCd(tg.getX(), tg.getY() + yShift);
					}
				}
			}
			//tad.repaint();

			//Now add the decrypt operator
			yShift += 40;
			readChannel.setSecurityContext(channelSecMap.get(readChannel.getChannelName()));
			readChannel.setEncForm(true);
			tad.repaint();
			//Add decryption operator if it does not already exist
			xpos = readChannel.getX();
			ypos = readChannel.getY();
			TMLADDecrypt dec = new TMLADDecrypt(xpos + 10, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			dec.securityContext = channelSecMap.get(readChannel.getChannelName());
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
					if (channel.equals(readChannel.getChannelName()) && readChannel.getSecurityContext().equals("")) {
						readChannel.setSecurityContext( channelSecMap.get(readChannel.getChannelName()));
						readChannel.setEncForm(true);

					}
				}
				if (tg.getY() >= ypos && tg != dec && tg!= wr && tg != nonce && tg!= comp) {

					tg.setCd(tg.getX(), tg.getY() + yShift);
				}
			}

			tad.setMaxPanelSize(tad.getMaxX(), tad.getMaxY() + yShift);

			tad.repaint();
		}
	}
}



//    GTMLModeling gtm = new GTMLModeling(newarch, false);
//   map = gtm.translateToTMLMapping();
//if (gtm.getCheckingErrors().size() > 0){
//System.out.println("@$()(#");
//map= null;
// }
/*for (TMLTask task : newmodel.getTasks()) {
  task.setName(tabName + "_" + name + "__" + task.getName());
  }
  for (TMLTask task : tmlmodel.getTasks()) {
  HwExecutionNode node = (HwExecutionNode) map.getHwNodeOf(task);
  if (newmodel.getTMLTaskByName(task.getName().replace(tabName, tabName + "_" + name)) != null) {
  map.addTaskToHwExecutionNode(newmodel.getTMLTaskByName(task.getName().replace(tabName, tabName + "_" + name)), node);
  map.removeTask(task);
  } else {

  }
  }*/
//map.setTMLModeling(newmodel);

//        map.setTMLModeling(newmodel);

return;
}
public void buildHSMActivityDiagram(String cpuName){
	int xpos = 0;
	int ypos = 0;
	TGConnector fromStart;
	//Build HSM Activity diagram

	TMLActivityDiagramPanel tad = tmlcdp.getTMLActivityDiagramPanel("HSM_" + cpuName);
	if (tad ==null){
		System.out.println("Missing task ");
		return;
	}

	TMLADStartState start = (TMLADStartState) tad.getComponentList().get(0);
	fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());

	if (hsmChannelMap.get(cpuName).size() ==0){
		TMLADStopState stop = new TMLADStopState(100, 100, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
		tad.addComponent(stop, 100, 100, false, true);


		//Connect stop and start
		fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
		fromStart.setP1(start.getTGConnectingPointAtIndex(0));
		fromStart.setP2(stop.getTGConnectingPointAtIndex(0));
		tad.addComponent(fromStart, 100, 100, false, true);
		return;
	}


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

	if (hsmChannelMap.get(cpuName).size() > 3) {
		TMLADChoice choice2 = new TMLADChoice(xc, 400, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
		int i = 0;
		for (HSMChannel ch : hsmChannelMap.get(cpuName)) {
			if (i>8){
				break;
			}
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
			rd.setChannelName("data_" + ch.name + "_" + ch.task);
			rd.setSecurityContext(channelSecMap.get(ch.name));
			tad.addComponent(rd, xc, 300, false, true);
			//Connect choice and readchannel
			fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
			fromStart.setP1(choice2.getTGConnectingPointAtIndex(i % 3 + 1));
			fromStart.setP2(rd.getTGConnectingPointAtIndex(0));

			tad.addComponent(fromStart, 300, 200, false, true);
			TMLADWriteChannel wr = new TMLADWriteChannel(xc, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			wr.setChannelName("retData_" + ch.name + "_" + ch.task);
			tad.addComponent(wr, xc, 600, false, true);
			wr.setSecurityContext(channelSecMap.get(ch.name));


			if (ch.secType == HSMChannel.DEC) {
				TMLADDecrypt dec = new TMLADDecrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
				dec.securityContext = channelSecMap.get(ch.name);
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
				enc.securityContext = channelSecMap.get(ch.name);
				if (ch.secType == HSMChannel.SENC) {
					enc.type = "Symmetric Encryption";
				} else if (ch.secType == HSMChannel.AENC) {
					enc.type = "Asymmetric Encryption";
				} else if (ch.secType == HSMChannel.MAC) {
					enc.type = "MAC";
				} else if (ch.secType == HSMChannel.NONCE) {
					enc.type = "Nonce";
				}

				enc.message_overhead = overhead;
				enc.encTime = encComp;
				enc.decTime = decComp;
				enc.nonce = ch.nonceName;
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

		for (HSMChannel ch : hsmChannelMap.get(cpuName)) {

			//Add guard as channelindex
			choice.setGuard("[channelIndex=="+channelIndexMap.get(ch.name)+"]",i-1);

			TMLADReadChannel rd = new TMLADReadChannel(xc, 300, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			rd.setChannelName("data_" + ch.name + "_" + ch.task);
			rd.setSecurityContext(channelSecMap.get(ch.name));
			tad.addComponent(rd, xc, 300, false, true);

			//Recieve plaintext data if encrypting
			if (ch.secType != HSMChannel.DEC) {
				rd.setEncForm(false);
			}

			//Connect choice and readchannel

			fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
			fromStart.setP1(choice.getTGConnectingPointAtIndex(i));
			fromStart.setP2(rd.getTGConnectingPointAtIndex(0));              
			tad.addComponent(fromStart, xc, 300, false, true);


			fromStart = new TGConnectorTMLAD(xc, 350, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
			fromStart.setP1(rd.getTGConnectingPointAtIndex(1));


			//If needed, receive nonce from task
			if (!ch.nonceName.equals("")){

				tad.addComponent(fromStart, 300, 200, false, true);

				rd = new TMLADReadChannel(xc, 350, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
				rd.setChannelName("data_" + ch.name + "_" + ch.task);
				rd.setSecurityContext(ch.nonceName);
				tad.addComponent(rd, xc, 350, false, true);


				fromStart.setP2(rd.getTGConnectingPointAtIndex(0));

				fromStart = new TGConnectorTMLAD(xc, 350, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
				fromStart.setP1(rd.getTGConnectingPointAtIndex(1));

			}


			//Send data back to task
			TMLADWriteChannel wr = new TMLADWriteChannel(xc, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
			wr.setChannelName("retData_" + ch.name + "_" + ch.task);

			//Return plaintext data if decrypting
			if (ch.secType == HSMChannel.DEC) {
				wr.setEncForm(false);
			}


			tad.addComponent(wr, xc, 600, false, true);
			wr.setSecurityContext(channelSecMap.get(ch.name));


			if (ch.secType == HSMChannel.DEC) {
				//Add Decrypt operator
				TMLADDecrypt dec = new TMLADDecrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
				dec.securityContext = channelSecMap.get(ch.name);
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
				enc.securityContext = channelSecMap.get(ch.name);
				if (ch.secType == HSMChannel.SENC) {
					enc.type = "Symmetric Encryption";
				} else if (ch.secType == HSMChannel.AENC) {
					enc.type = "Asymmetric Encryption";
				} else if (ch.secType == HSMChannel.MAC) {
					enc.type = "MAC";
				} else if (ch.secType == HSMChannel.NONCE) {
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
