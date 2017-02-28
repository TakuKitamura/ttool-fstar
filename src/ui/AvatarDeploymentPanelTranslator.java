// written by Julien Henon, M1 project 2015, amended by D. Genius

package ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import ddtranslatorSoclib.AvatarBridge;
import ddtranslatorSoclib.AvatarBus;
import ddtranslatorSoclib.AvatarCPU;
import ddtranslatorSoclib.AvatarChannel;
import ddtranslatorSoclib.AvatarComponent;
import ddtranslatorSoclib.AvatarConnectingPoint;
import ddtranslatorSoclib.AvatarConnector;
import ddtranslatorSoclib.AvatarCoproMWMR;
import ddtranslatorSoclib.AvatarCrossbar;
import ddtranslatorSoclib.AvatarICU;
import ddtranslatorSoclib.AvatarMappedObject;
import ddtranslatorSoclib.AvatarRAM;
import ddtranslatorSoclib.AvatarTTY;
import ddtranslatorSoclib.AvatarTask;
import ddtranslatorSoclib.AvatarTimer;
import ddtranslatorSoclib.AvatarVgmn;
import ddtranslatorSoclib.AvatarddSpecification;
import ui.avatardd.ADDBlockArtifact;
import ui.avatardd.ADDBridgeNode;
import ui.avatardd.ADDBusNode;
import ui.avatardd.ADDCPUNode;
import ui.avatardd.ADDChannelArtifact;
import ui.avatardd.ADDConnectingPoint;
import ui.avatardd.ADDConnector;
import ui.avatardd.ADDCoproMWMRNode;
import ui.avatardd.ADDCrossbarNode;
import ui.avatardd.ADDDiagramPanel;
import ui.avatardd.ADDICUNode;
import ui.avatardd.ADDMemoryNode;
import ui.avatardd.ADDRAMNode;
import ui.avatardd.ADDTTYNode;
import ui.avatardd.ADDTimerNode;
import ui.avatardd.ADDVgmnNode;

public class AvatarDeploymentPanelTranslator {

	private int nb_init = 0;

	/*
	 * there are seven targets which are fixed but which are invisible to the user of thr TTool deployment diagram) :
	 * 
	 * Targets on RAM0 :
	 * the text segment (target 0)
	 * the reset segment (target 1)
	 * the data segment (target 2)
	 * 
	 * Other targets :
	 * the simhelper segment (target 3)
	 * the icu segment (target 4)
	 * the timer segment (target 5)
	 * the fdt segment (target 6)
	 * 
	 * There always is a RAM0
	 */

	private int nb_target = 6;
	private int no_proc = 0;
	private int no_ram = 0;
	private int no_tty = 0;

	private int nb_clusters = 0;
	private LinkedList<TGComponent> tgcComponents;

	private LinkedList<AvatarComponent> avatarComponents;
	private LinkedList<AvatarConnector> avatarConnectors;
	private LinkedList<AvatarMappedObject> avatarMappedObject;

	public AvatarDeploymentPanelTranslator(ADDDiagramPanel _avatarddDiagramPanel) {
		tgcComponents = _avatarddDiagramPanel.getComponentList();
		
		avatarComponents = new LinkedList<AvatarComponent>();
		avatarConnectors = new LinkedList<AvatarConnector>();
		avatarMappedObject = new LinkedList<AvatarMappedObject>();

		MakeListOfComponentAndMappedObject(_avatarddDiagramPanel);
	}

	private void MakeListOfComponentAndMappedObject(ADDDiagramPanel avatarddDiagramPanel) {

		Map<TGComponent, AvatarComponent> avatarMap = new HashMap<TGComponent, AvatarComponent>();

		for (TGComponent dp : tgcComponents) {
			if (dp instanceof ADDCPUNode) {
				ADDCPUNode addCPUNode = (ADDCPUNode) dp;
				String cpuName = addCPUNode.getNodeName();
				int nbOfIRQs = addCPUNode.getNbOfIRQs();
				int ICacheWays = addCPUNode.getICacheWays();
				int ICacheSets = addCPUNode.getICacheSets();
				int ICacheWords = addCPUNode.getICacheWords();
				int dCacheWays = addCPUNode.getDCacheWays();
				int dCacheSets = addCPUNode.getDCacheSets();
				int dCacheWords = addCPUNode.getDCacheWords();
				AvatarCPU avcpu;
				int monitored = addCPUNode.getMonitored();
				avcpu = new AvatarCPU(cpuName, nbOfIRQs, ICacheWays, ICacheSets, ICacheWords, dCacheWays, dCacheSets, dCacheWords, nb_init, no_proc, monitored);
				nb_init++;
				no_proc++;

				Vector tasks = addCPUNode.getArtifactList();
				for (int i = 0; i < tasks.size(); i++) {
					ADDBlockArtifact task = (ADDBlockArtifact) tasks.get(i);

					String taskName = task.getTaskName();
					String referenceTaskName = task.getReferenceTaskName();

					AvatarTask avtask = new AvatarTask(taskName, referenceTaskName, avcpu);
					avcpu.addTask(avtask);
					avatarMappedObject.add(avtask);
				}
				avatarMap.put(dp, avcpu);
				avatarComponents.add(avcpu);

			} else if (dp instanceof ADDTTYNode) {
				ADDTTYNode tty = (ADDTTYNode) dp;

				int index = tty.getIndex();
				String ttyName = tty.getNodeName();

				AvatarTTY avtty = new AvatarTTY(ttyName, index, no_tty, index);
				nb_target++;

				avatarMap.put(dp, avtty);
				avatarComponents.add(avtty);

			} else if (dp instanceof ADDBridgeNode) {
				ADDBridgeNode bridge = (ADDBridgeNode) dp;

				String bridgeName = bridge.getNodeName();
				AvatarBridge avbridge = new AvatarBridge(bridgeName);

				avatarMap.put(dp, avbridge);
				avatarComponents.add(avbridge);

			} else if (dp instanceof ADDBusNode) {

				ADDBusNode bus = (ADDBusNode) dp;

				String busName = bus.getNodeName();
				int nbOfAttachedInitiators = bus.getNbOfAttachedInitiators();
				int nbOfAttachedTargets = bus.getNbOfAttachedTargets();
				int fifoDepth = bus.getFifoDepth();
				int minLatency = bus.getMinLatency();
				System.out.println("vgsb read in");
				AvatarBus avbus = new AvatarBus(busName, nbOfAttachedInitiators, nbOfAttachedTargets, fifoDepth, minLatency);
				avatarMap.put(dp, avbus);
				avatarComponents.add(avbus);

			} else if (dp instanceof ADDVgmnNode) {

				ADDVgmnNode vgmn = (ADDVgmnNode) dp;

				String vgmnName = vgmn.getNodeName();
				int nbOfAttachedInitiators = vgmn.getNbOfAttachedInitiators();
				int nbOfAttachedTargets = vgmn.getNbOfAttachedTargets();
				int fifoDepth = vgmn.getFifoDepth();
				int minLatency = vgmn.getMinLatency();
				System.out.println("vgmn read in");
				AvatarVgmn avvgmn = new AvatarVgmn(vgmnName, nbOfAttachedInitiators, nbOfAttachedTargets, fifoDepth, minLatency);
				avatarMap.put(dp, avvgmn);
				avatarComponents.add(avvgmn);

			} else if (dp instanceof ADDCrossbarNode) {

				ADDCrossbarNode crossbar = (ADDCrossbarNode) dp;

				String crossbarName = crossbar.getNodeName();
				// int nbOfAttachedInitiators = crossbar.getNbOfAttachedInitiators();
				int nbOfAttachedInitiators = 0;

				// int nbOfAttachedTargets = crossbar.getNbOfAttachedTargets();

				int nbOfAttachedTargets = 0;

				int cluster_index = crossbar.getClusterIndex();
				int cluster_address = crossbar.getClusterAddress();

				AvatarCrossbar avcrossbar = new AvatarCrossbar(crossbarName, nbOfAttachedInitiators, nbOfAttachedTargets, cluster_index, cluster_address);
				nb_clusters++;
				System.out.println("nb crossbars read in" + nb_clusters);
				avatarMap.put(dp, avcrossbar);
				avatarComponents.add(avcrossbar);
			} else if (dp instanceof ADDICUNode) {

				ADDICUNode icu = (ADDICUNode) dp;

				String ICUName = icu.getNodeName();
				int index = icu.getIndex();
				int nbIRQ = icu.getNIrq();

				AvatarICU avicu = new AvatarICU(ICUName, index, nbIRQ);
				avatarMap.put(dp, avicu);
				avatarComponents.add(avicu);

			} else if (dp instanceof ADDTimerNode) {
				ADDTimerNode timer = (ADDTimerNode) dp;

				String timerName = timer.getNodeName();
				int nIrq = timer.getNIrq();
				int index = timer.getIndex();

				AvatarTimer avtimer = new AvatarTimer(timerName, index, nIrq);
				avatarMap.put(dp, avtimer);
				avatarComponents.add(avtimer);

			} else if (dp instanceof ADDCoproMWMRNode) {

				ADDCoproMWMRNode addCoproMWMRNode = (ADDCoproMWMRNode) dp;

				String timerName = addCoproMWMRNode.getNodeName();
				int srcid = addCoproMWMRNode.getSrcid(); // initiator id
				int tgtid = addCoproMWMRNode.getTgtid(); // target id
				int plaps = addCoproMWMRNode.getPlaps(); // configuration of integrated timer
				int fifoToCoprocDepth = addCoproMWMRNode.getFifoToCoprocDepth();
				int fifoFromCoprocDepth = addCoproMWMRNode.getFifoFromCoprocDepth();
				int nToCopro = addCoproMWMRNode.getNToCopro(); // Nb of channels going to copro
				int nFromCopro = addCoproMWMRNode.getNFromCopro(); // Nb of channels coming from copro
				int nConfig = addCoproMWMRNode.getNConfig(); // Nb of configuration registers
				int nStatus = addCoproMWMRNode.getNStatus(); // nb of status registers
				boolean useLLSC = addCoproMWMRNode.getUseLLSC(); // more efficient protocol. 0: not used. 1 or more -> used

				AvatarCoproMWMR acpMWMR;
				acpMWMR = new AvatarCoproMWMR(timerName, srcid, srcid, tgtid, plaps, fifoToCoprocDepth, fifoFromCoprocDepth, nToCopro, nFromCopro, nConfig, nStatus, useLLSC);
				avatarMap.put(dp, acpMWMR);

			} else if (dp instanceof ADDMemoryNode) {

				if (dp instanceof ADDRAMNode) {

					ADDRAMNode addRamNode = (ADDRAMNode) dp;
					String name = addRamNode.getNodeName();
					int index = addRamNode.getIndex();
					int byteDataSize = addRamNode.getDataSize();

					int monitored = addRamNode.getMonitored();

					AvatarRAM avram = new AvatarRAM(name, index, byteDataSize, no_ram, index, monitored);
					int cluster_index = avram.getIndex();

					no_ram++;
					nb_target++;

					Vector channels = addRamNode.getArtifactList();
					for (int i = 0; i < channels.size(); i++) {
						ADDChannelArtifact c = (ADDChannelArtifact) channels.get(i);

						String referenceDiagram = c.getReferenceDiagram();
						String channelName = c.getChannelName();
						//channel is inevitably on same cluster as RAM it is mapped on :)
						AvatarChannel avcl = new AvatarChannel(referenceDiagram, channelName, avram, cluster_index, monitored);
						avram.addChannel(avcl);
						avatarMappedObject.add(avcl);
					}
					avatarMap.put(dp, avram);
					avatarComponents.add(avram);
				}
			}
		}
		
		
		for (TGComponent dp : tgcComponents) {
		  
			if (dp instanceof ADDConnector) {

				ADDConnector connector = (ADDConnector) dp;
			
				TGConnectingPoint connectingPoint1 =  connector.get_p1();
				TGConnectingPoint connectingPoint2 =  connector.get_p2();	
	
				TGComponent owner_p1 = avatarddDiagramPanel.getComponentToWhichBelongs(connectingPoint1);
				TGComponent owner_p2 = avatarddDiagramPanel.getComponentToWhichBelongs(connectingPoint2);

				System.out.println(owner_p1.getName()+" connected to "+owner_p2.getName());	

				AvatarComponent avowner_p1 = avatarMap.get(owner_p1);	
				AvatarComponent avowner_p2 = avatarMap.get(owner_p2);
			       
				//create Avatar connecting points

				AvatarConnectingPoint avconnectingPoint1 = new AvatarConnectingPoint(avowner_p1);
				AvatarConnectingPoint avconnectingPoint2 = new AvatarConnectingPoint(avowner_p2);
				boolean spy = connector.hasASpy();
				int monitored = 0;
				if (spy == true){
					monitored = 1; 
				
				}
				AvatarConnector avconnector = new AvatarConnector(avconnectingPoint1, avconnectingPoint2, monitored);

				avatarConnectors.add(avconnector);

			}
		}
	}

	public AvatarddSpecification getAvatarddSpecification() {
		return new AvatarddSpecification(avatarComponents, avatarConnectors, avatarMappedObject, nb_target, nb_init);
	}

}
