/**Copyright or  or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
*
* ludovic.apvrille AT enst.fr
*
* This software is a computer program whose purpose is to allow the
* edition of TURTLE analysis, design and deployment diagrams, to
* allow the generation of RT-LOTOS or Java code from this diagram,
* and at last to allow the analysis of formal validation traces
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
* Class Mapping2TIF
* Creation: 14/01/2008
* @version 1.0 14/01/2008
* @author Ludovic APVRILLE
* @see
*/

package tmltranslator.toturtle;

import java.util.*;

import tmltranslator.*;
import myutil.*;
import translator.*;
import ui.*;


public class Mapping2TIF {
    
    //private static int gateId;
    
    private static String nameChannelNBRNBW = "ChannelNBRNBW__";
    private static String nameChannelBRNBW = "ChannelBRNBW__";
    private static String nameChannelBRBW = "ChannelBRBW__";
    
    private static String nameEvent = "Event__";
    
    private static String nameRequest = "Request__";
    
    
	private TMLMapping tmlmapping;
	private TMLModeling tmlmodeling;
	private TMLArchitecture tmlarchitecture;
	
    private TURTLEModeling tm;
    private Vector checkingErrors;
	
	private boolean showSampleChannels = false;
	private boolean showChannels = false;
	private boolean showEvents = false;
	private boolean showRequests = false;
	private boolean showExecs = false;
	private boolean showBusTransfers = false;
	private boolean showScheduling = false;
	private boolean showTaskState = false;
	private boolean showChannelState = false;
	private boolean showBlockedCPU = false;
	private boolean showTerminateCPUs = false;
	private boolean showBranching = false;
	private boolean isClocked = false;
	private String tickValue = "1";
	private String gateTick = "tick__1";
	private boolean isEndClocked = false;
	private boolean isCountTick = false;
	private boolean hasMaxCountTick = false;
	private String maxCountTickValue;
	private boolean randomTasks = false;
    
	private TClass clock;
	private TClass system;
	private ActivityDiagram systemad;
	private ArrayList<TMLCommunicationElement> allcommunications;
	private ArrayList<HwBus> allbuses;
	private HwBus fakeBus = new HwBus("fake__bus");
	private ArrayList<ADJunction> initJuncs;
	private ArrayList<ADJunction> behaviorJuncs;
	private ADJunction beforeInit;
	private ADJunction beforeBehavior;
	private ADJunction afterInit;
	private ADJunction afterBehavior;
	private ADJunction beforeCommManager;
	private ADJunction afterCommManager;
    
    public Mapping2TIF(TMLMapping _tmlmapping) {
		tmlmapping = _tmlmapping;
        tmlmodeling = tmlmapping.getTMLModeling();
		tmlarchitecture = tmlmapping.getTMLArchitecture();
    }
    
    public Vector getCheckingErrors() {
        return checkingErrors;
    }
	
	public void setShowSampleChannels(boolean _b) {
		showSampleChannels = _b;
	}
	
	public void setShowChannels(boolean _b) {
		showChannels = _b;
	}
	
	public void setShowEvents(boolean _b) {
		showEvents = _b;
	}
	
	public void setShowRequests(boolean _b) {
		showRequests = _b;
	}
	
	public void setShowExecs(boolean _b) {
		showExecs = _b;
	}
	
	public void setShowBusTransfers(boolean _b) {
		showBusTransfers = _b;
	}
	
	public void setShowBlockedCPU(boolean _b) {
		showBlockedCPU = _b;
		//System.out.println("CPU blocked is shown");
	}
	
	public void setShowTerminateCPUs(boolean _b) {
		showTerminateCPUs = _b;
	}
	
	public void setShowTaskState(boolean _b) {
		showTaskState = _b;
	}
	
	public void setShowChannelState(boolean _b) {
		showChannelState = _b;
	}
	
	public void setShowScheduling(boolean _b) {
		showScheduling = _b;
	}
	
	public void setRandomTasks(boolean _b) {
		randomTasks = _b;
	}
	
	public void setShowBranching(boolean _b) {
		showBranching = _b;
	}
	
	public void setIsClocked(boolean _b) {
		isClocked = _b;
	}
	
	public void setTickValue(String _s) {
		tickValue = _s;
	}
	
	public void setIsEndClocked(boolean _b) {
		isEndClocked = _b;
	}
	
	public void setIsCountTick(boolean _b) {
		isCountTick = _b;
	}
	
	public void hasMaxCountTick(boolean _b) {
		hasMaxCountTick = _b;
	}
	
	public void setMaxCountTickValue(String _s) {
		maxCountTickValue = _s;
	}
    
    
    public TURTLEModeling generateTURTLEModeling() {
		// Optimize TML modeling
		//tmlmodeling.optimize();
		tmlmapping.removeAllRandomSequences();
		
		//System.out.println("generate TM");
        tm = new TURTLEModeling();
        checkingErrors = new Vector();
		
		// Init structures
		prepareSystemTClass();
		prepareCommunications();
		
		ArrayList<TMLChannel> localChannels = new ArrayList<TMLChannel>();
		ArrayList<TMLEvent> localEvents = new ArrayList<TMLEvent>();
		ArrayList<TMLRequest> localRequests = new ArrayList<TMLRequest>();
		fillCommunicationArrays(system, localChannels, localEvents, localRequests);
		makeCommunicationAttributes(system, localChannels, localEvents, localRequests);
		
		// Translate CPUs
		makeCPUs(localChannels, localEvents, localRequests);
		
		// Communication manager
		makeCommunicationManager(system, systemad, localChannels, localEvents, localRequests);
		
		// End system activity diagram
		endActivityDiagram();
        
        return tm;
    }
	
	private void prepareSystemTClass() {
		system = new TClass("soc", true);
		tm.addTClass(system);
		systemad = new ActivityDiagram();
		system.setActivityDiagram(systemad);
		initJuncs = new ArrayList<ADJunction>();
		behaviorJuncs = new ArrayList<ADJunction>();
		
		beforeInit = new ADJunction();
		beforeBehavior = new ADJunction();
		afterInit = new ADJunction();
		afterBehavior = new ADJunction();
		systemad.add(beforeInit);
		systemad.add(beforeBehavior);
		systemad.add(afterInit);
		systemad.add(afterBehavior);
		
		if (showTerminateCPUs) {
			system.addNewGateIfApplicable("allCPUsTerminated");
		}
		
		if (isClocked) {
			gateTick = "tick__" + tickValue;
			system.addNewGateIfApplicable(gateTick);
			if (tickValue.compareTo("1") != 0) {
				system.addNewParamIfApplicable("count__modulotick", "nat", "0");
			}
		}
		
		if ((isCountTick) || (hasMaxCountTick)) {
			system.addNewParamIfApplicable("count__tick", "nat", "0");
		}
		
		if (hasMaxCountTick) {
			system.addNewGateIfApplicable("maxCountTickReached");
		}
		
		if (isEndClocked) {
			system.addNewGateIfApplicable("system__endTick");
		}
		
		system.addNewParamIfApplicable("written", "nat", "0");
		system.addNewParamIfApplicable("read", "nat", "0");
		
		// For transfer on buses
		system.addNewParamIfApplicable("transferDone", "bool", "false");
	}
	
	private void prepareCommunications() {
		allcommunications = new ArrayList<TMLCommunicationElement>();
		allbuses = new ArrayList<HwBus>();
		afterCommManager = new ADJunction();
		beforeCommManager = new ADJunction();
		systemad.add(afterCommManager);
		systemad.add(beforeCommManager);
	}
	
	private void makeCommunicationManager(TClass tcpu, ActivityDiagram ad, ArrayList<TMLChannel> localChannels, ArrayList<TMLEvent> localEvents, ArrayList<TMLRequest> localRequests) {
		// Nothing at first -> have to be modified (update comm elt parameters)
		ADActionStateWithParam adap, adap1, adap2, adap3;
		int index;
		ADChoice choice, choice0;
		ADActionStateWithParam actionp1, actionp2, actionp3, actionp4;
		ADActionStateWithGate actiong0;
		ADJunction adj0;
		String guard0, guard1, guard00, guard01;
		String chvar;
		
		ADComponent previous = beforeCommManager;
		
		LinkedList met = new LinkedList();
		HwBus bus0;
		ADChoice adch;
		ADJunction adj;
		Param p;
		
		// Init all bus sizes
		for(HwBus bus: allbuses) {
			if (bus != fakeBus) {
				if (!met.contains(bus)) {
					met.add(bus);
					if (showBusTransfers) {
						system.addNewGateIfApplicable("transferOn__" + bus.getName());
					}
					system.addNewParamIfApplicable("n__tmpbus", "nat", "0");
					system.addNewParamIfApplicable("n__" + bus.getName(), "nat", "0");
					
					if (bus.clockRatio != 1) {
						system.addNewParamIfApplicable(bus.getName() + "__tickRatio", "nat", bus.clockRatio + " - 1");
						adap = getActionStateWithParam(tcpu, ad, bus.getName() + "__tickRatio", "(" + bus.getName() + "__tickRatio + 1) % " + bus.clockRatio);
						previous.addNext(adap);
						previous = adap;
					}
					
					adap = getActionStateWithParam(tcpu, ad, "n__" + bus.getName(), "" + bus.byteDataSize);
					previous.addNext(adap);
					previous = adap;
				}
			}
		}
		
		adap = getActionStateWithParam(tcpu, ad, "transferDone", "false");
		previous.addNext(adap);
		previous = adap;
		
		if (showChannelState) {
			for(TMLChannel channel: localChannels) {
				system.addNewGateIfApplicable("channelState__" + channel.getName());
				chvar = "n__" + channel.getName() + "__";
				guard0 = "";
				p = system.getParamByName(chvar+"tmpor");
				if (p!= null) {
					guard0 += "!"+chvar+"tmpor";
				}
				p = system.getParamByName(chvar+"or");
				if (p!= null) {
					guard0 += "!"+chvar+"or";
				}
				p = system.getParamByName(chvar+"dest");
				if (p!= null) {
					guard0 += "!"+chvar+"dest";
				}
				p = system.getParamByName(chvar+"tmpdest");
				if (p!= null) {
					guard0 += "!"+chvar+"tmpdest";
				}
				p = system.getParamByName(chvar+"total");
				if (p!= null) {
					guard0 += "!"+chvar+"total";
				}
				System.out.println("Info for channel " + channel.getName() + " = " + guard0);
				actiong0 = getActionGate(tcpu, ad, "channelState__" + channel.getName(), guard0);
				previous.addNext(actiong0);
				previous = actiong0;
			}
		}
		
		// Managing bus tranfers
		index = 0;
		TMLChannel ch;
		for(TMLCommunicationElement tmle: allcommunications) {
			if (tmle instanceof TMLChannel) {
				//index = allcommunications.indexOf(channel);
				ch = (TMLChannel)tmle;
				chvar = "n__" + ch.getName() + "__";
				bus0 =  getBusOf(ch);
				if ((bus0 != null) && (bus0 != fakeBus)) {
					if ((ch.getType() == TMLChannel.BRBW) || (ch.getType() == TMLChannel.BRNBW)) {
						adap = getActionStateWithParam(tcpu, ad, chvar + "tmpdest", chvar + "tmpdest + " + chvar + "dest");
						previous.addNext(adap);
						previous = adap;
						
						adap = getActionStateWithParam(tcpu, ad, chvar + "dest", "0");
						previous.addNext(adap);
						previous = adap;
						
						adap2 = getActionStateWithParam(tcpu, ad, chvar + "tmpor", "false");
						previous.addNext(adap2);
						previous = adap2;
						
						choice = new ADChoice();
						adj = new ADJunction();
						ad.add(adj);
						ad.add(choice);
						previous.addNext(choice);
						guard0 = chvar + "tmpdest == 0";
						guard1 = "[not(" + guard0 + ")]";
						guard0 = "[" + guard0 + "]";
							
						choice.addNext(adj);
						choice.addGuard(guard0);
							
						choice.addGuard(guard1);
						previous = choice;
							
						adap1 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1) + "__tmp", "true");
						previous.addNext(adap1);
						adap1.addNext(adj);
						previous = adj;
					} 
					
					guard0 = "(n__" + bus0.getName() + " >0) and (" + chvar + "or > 0)";
					if (bus0.clockRatio != 1) {
						guard0 = guard0 + " and (" + bus0.getName() + "__tickRatio == 0)";
					}
					guard1 = "[not(" + guard0 + ")]";
					guard0 = "[" + guard0 + "]";
					
					adj = new ADJunction();
					ad.add(adj);
					adch = new ADChoice();
					ad.add(adch);
					previous.addNext(adch);
					previous = adj;
					adch.addGuard(guard1);
					adch.addNext(adj);
					
					adch.addGuard(guard0);
					adap = getActionStateWithParam(tcpu, ad, "n__tmpbus", "min(n__" + bus0.getName() + "," + chvar + "or)");
					adch.addNext(adap);
					adap1 = getActionStateWithParam(tcpu, ad, chvar + "or", chvar + "or - n__tmpbus");
					adap.addNext(adap1);
					adap2 = getActionStateWithParam(tcpu, ad, "n__" + bus0.getName(), "n__" + bus0.getName() + " - n__tmpbus");
					adap1.addNext(adap2);
					previous = adap2;
					
					if ((ch.getType() == TMLChannel.BRBW) || (ch.getType() == TMLChannel.BRNBW)) {
						adap3 = getActionStateWithParam(tcpu, ad, chvar + "dest", chvar + "dest + n__tmpbus");
						previous.addNext(adap3);
						previous = adap3;
					}
					
					adap = getActionStateWithParam(tcpu, ad, "transferDone", "true");
					previous.addNext(adap);
					previous = adap;
					
					if (showBusTransfers) {
						actiong0 = getActionGate(tcpu, ad, "transferOn__" + bus0.getName(), "!" + index + "!n__tmpbus!" +"n__" + bus0.getName());
						previous.addNext(actiong0);
						actiong0.addNext(adj);
					} else {
						previous.addNext(adj);
					}
					previous = adj;
					
					if (ch.getType() == TMLChannel.BRBW) {
						adap1 = getActionStateWithParam(tcpu, ad, chvar + "total", chvar + "dest + " + chvar + "tmpdest");
						previous.addNext(adap1);
						previous = adap1;
					}
					
					
				} else {
					if (ch.getType() != TMLChannel.NBRNBW) {
						choice = new ADChoice();
						adj = new ADJunction();
						ad.add(adj);
						ad.add(choice);
						previous.addNext(choice);
						//guard0 = chvar + "tmpor == 0";
						//guard1 = "[not(" + guard0 + ")]";
						//guard0 = "[" + guard0 + "]";
						guard0 = chvar + "tmpor";
						guard1 = "[" + guard0 + "]";
						guard0 = "[not(" + guard0 + ")]";
						
						choice.addNext(adj);
						choice.addGuard(guard0);
						
						choice.addGuard(guard1);
						previous = choice;
						
						if ((ch.getType() == TMLChannel.BRBW) || (ch.getType() == TMLChannel.BRNBW)) {
							adap = getActionStateWithParam(tcpu, ad, chvar + "tmpdest", chvar + "tmpdest + " + chvar + "or");
							previous.addNext(adap);
							previous = adap;
						}
						
						if ((ch.getType() == TMLChannel.BRBW) || (ch.getType() == TMLChannel.BRNBW)) {
							adap1 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1) + "__tmp", "true");
							previous.addNext(adap1);
							previous = adap1;
						}
						
						adap1 = getActionStateWithParam(tcpu, ad, chvar + "or", "0");
						previous.addNext(adap1);
						previous = adap1;
						
						adap1 = getActionStateWithParam(tcpu, ad, chvar + "tmpor", "false");
						previous.addNext(adap1);
						adap1.addNext(adj);
						previous = adj;
						
						if (ch.getType() == TMLChannel.BRBW) {
							adap1 = getActionStateWithParam(tcpu, ad, chvar + "total", chvar + "tmpdest");
							previous.addNext(adap1);
							previous = adap1;
							
							/*adap = getActionStateWithParam(tcpu, ad, chvar + "dest", chvar + "tmpdest");
							previous.addNext(adap);
							previous = adap;*/
						}
					}
				}
			}
			index ++;
		}
		
		for(TMLChannel channel: localChannels) {
			if (channel.isBlockingAtOrigin()) {
				index = allcommunications.indexOf(channel);
				//adap = getActionStateWithParam(tcpu, ad, "n__" + channel.getName() + "__tmp", "n__" + channel.getName());
				//previous.addNext(adap);
				
				choice = new ADChoice();
				ad.add(choice);
				previous.addNext(choice);
				guard00 = "blockedOn__" + (index * 2) + "__tmp";
				guard01 = "[not(" + guard00 + ")]";
				guard00 = "[" + guard00 + "]";
				
				adj0 = new ADJunction();
				ad.add(adj0);
				
				choice.addGuard(guard01);
				choice.addNext(adj0);
				
				actionp1 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2) + "__tmp", "false");
				choice.addGuard(guard00);
				choice.addNext(actionp1);
				
				choice0 = new ADChoice();
				ad.add(choice0);
				actionp1.addNext(choice0);
				guard0 = "blockedOn__" + (index * 2);
				guard1 = "[not(" + guard0 + ")]";
				guard0 = "[" + guard0 + "]";
				
				choice0.addNext(adj0);
				choice0.addGuard(guard1);
				
				actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2), "false");
				actionp3 = getActionStateWithParam(tcpu, ad, channel.getOriginTask().getName() + "__state", "0");
				actionp4 = getActionStateWithParam(tcpu, ad, cpuof(channel.getOriginTask()).getName() + "__selected", "0");
				
				choice0.addNext(actionp2);
				choice0.addGuard(guard0);
				actionp2.addNext(actionp3);
				actionp3.addNext(actionp4);
				actionp4.addNext(adj0);
				
				previous = adj0;
			}
			
			if (channel.isBlockingAtDestination()) {
				index = allcommunications.indexOf(channel);
				//adap = getActionStateWithParam(tcpu, ad, "n__" + channel.getName() + "__tmp", "n__" + channel.getName());
				//previous.addNext(adap);
				
				choice = new ADChoice();
				ad.add(choice);
				previous.addNext(choice);
				guard00 = "blockedOn__" + (index * 2 + 1) + "__tmp";
				guard01 = "[not(" + guard00 + ")]";
				guard00 = "[" + guard00 + "]";
				
				adj0 = new ADJunction();
				ad.add(adj0);
				
				choice.addGuard(guard01);
				choice.addNext(adj0);
				
				actionp1 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1) + "__tmp", "false");
				choice.addGuard(guard00);
				choice.addNext(actionp1);
				
				choice0 = new ADChoice();
				ad.add(choice0);
				actionp1.addNext(choice0);
				guard0 = "blockedOn__" + (index * 2 + 1);
				guard1 = "[not(" + guard0 + ")]";
				guard0 = "[" + guard0 + "]";
				
				choice0.addNext(adj0);
				choice0.addGuard(guard1);
				
				actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1), "false");
				actionp3 = getActionStateWithParam(tcpu, ad, channel.getDestinationTask().getName() + "__state", "0");
				actionp4 = getActionStateWithParam(tcpu, ad, cpuof(channel.getDestinationTask()).getName() + "__selected", "0");
				
				choice0.addNext(actionp2);
				choice0.addGuard(guard0);
				actionp2.addNext(actionp3);
				actionp3.addNext(actionp4);
				actionp4.addNext(adj0);
				
				previous = adj0;
			}
		}
		
		if (showChannelState) {
			for(TMLChannel channel: localChannels) {
				system.addNewGateIfApplicable("channelState__" + channel.getName());
				chvar = "n__" + channel.getName() + "__";
				guard0 = "";
				p = system.getParamByName(chvar+"tmpor");
				if (p!= null) {
					guard0 += "!"+chvar+"tmpor";
				}
				p = system.getParamByName(chvar+"or");
				if (p!= null) {
					guard0 += "!"+chvar+"or";
				}
				p = system.getParamByName(chvar+"dest");
				if (p!= null) {
					guard0 += "!"+chvar+"dest";
				}
				p = system.getParamByName(chvar+"tmpdest");
				if (p!= null) {
					guard0 += "!"+chvar+"tmpdest";
				}
				p = system.getParamByName(chvar+"total");
				if (p!= null) {
					guard0 += "!"+chvar+"total";
				}
				System.out.println("Info for channel " + channel.getName() + " = " + guard0);
				actiong0 = getActionGate(tcpu, ad, "channelState__" + channel.getName(), guard0);
				previous.addNext(actiong0);
				previous = actiong0;
			}
		}
		
		// Events
		for(TMLEvent event: localEvents) {
			//adap = getActionStateWithParam(tcpu, ad, "n__" + event.getName() + "__tmp", "n__" + event.getName());
			//previous.addNext(adap);
			//previous = adap;
			
			if (event.isBlockingAtOrigin()) {
				index = allcommunications.indexOf(event);
				adap = getActionStateWithParam(tcpu, ad, "n__" + event.getName() + "__tmp", "n__" + event.getName());
				previous.addNext(adap);
				
				choice = new ADChoice();
				ad.add(choice);
				adap.addNext(choice);
				guard00 = "blockedOn__" + (index * 2) + "__tmp";
				guard01 = "[not(" + guard00 + ")]";
				guard00 = "[" + guard00 + "]";
				
				adj0 = new ADJunction();
				ad.add(adj0);
				
				choice.addGuard(guard01);
				choice.addNext(adj0);
				
				actionp1 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2) + "__tmp", "false");
				choice.addGuard(guard00);
				choice.addNext(actionp1);
				
				choice0 = new ADChoice();
				ad.add(choice0);
				actionp1.addNext(choice0);
				guard0 = "blockedOn__" + (index * 2);
				guard1 = "[not(" + guard0 + ")]";
				guard0 = "[" + guard0 + "]";
				
				choice0.addNext(adj0);
				choice0.addGuard(guard1);
				
				actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2), "false");
				actionp3 = getActionStateWithParam(tcpu, ad, event.getOriginTask().getName() + "__state", "0");
				actionp4 = getActionStateWithParam(tcpu, ad, cpuof(event.getOriginTask()).getName() + "__selected", "0");
				
				choice0.addNext(actionp2);
				choice0.addGuard(guard0);
				actionp2.addNext(actionp3);
				actionp3.addNext(actionp4);
				actionp4.addNext(adj0);
				
				previous = adj0;
			}
			
			if (event.isBlockingAtDestination()) {
				index = allcommunications.indexOf(event);
				adap = getActionStateWithParam(tcpu, ad, "n__" + event.getName() + "__tmp", "n__" + event.getName());
				previous.addNext(adap);
				
				choice = new ADChoice();
				ad.add(choice);
				adap.addNext(choice);
				guard00 = "blockedOn__" + (index * 2 + 1) + "__tmp";
				guard01 = "[not(" + guard00 + ")]";
				guard00 = "[" + guard00 + "]";
				
				adj0 = new ADJunction();
				ad.add(adj0);
				
				choice.addGuard(guard01);
				choice.addNext(adj0);
				
				actionp1 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1) + "__tmp", "false");
				choice.addGuard(guard00);
				choice.addNext(actionp1);
				
				choice0 = new ADChoice();
				ad.add(choice0);
				actionp1.addNext(choice0);
				guard0 = "blockedOn__" + (index * 2 + 1);
				guard1 = "[not(" + guard0 + ")]";
				guard0 = "[" + guard0 + "]";
				
				choice0.addNext(adj0);
				choice0.addGuard(guard1);
				
				actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1), "false");
				actionp3 = getActionStateWithParam(tcpu, ad, event.getDestinationTask().getName() + "__state", "0");
				actionp4 = getActionStateWithParam(tcpu, ad, cpuof(event.getDestinationTask()).getName() + "__selected", "0");
				
				choice0.addNext(actionp2);
				choice0.addGuard(guard0);
				actionp2.addNext(actionp3);
				actionp3.addNext(actionp4);
				actionp4.addNext(adj0);
				
				previous = adj0;
			}
		}
		
		// Requests
		for(TMLRequest request: localRequests) {
			index = allcommunications.indexOf(request);
			adap = getActionStateWithParam(tcpu, ad, "n__" + request.getName() + "__tmp", "n__" + request.getName());
			previous.addNext(adap);
			previous = adap;
			
			choice = new ADChoice();
			ad.add(choice);
			previous.addNext(choice);
			guard00 = "blockedOn__" + (index * 2 + 1) + "__tmp";
			guard01 = "[not(" + guard00 + ")]";
			guard00 = "[" + guard00 + "]";
			
			adj0 = new ADJunction();
			ad.add(adj0);
			
			choice.addGuard(guard01);
			choice.addNext(adj0);
			
			actionp1 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1) + "__tmp", "false");
			choice.addGuard(guard00);
			choice.addNext(actionp1);
			
			choice0 = new ADChoice();
			ad.add(choice0);
			actionp1.addNext(choice0);
			guard0 = "blockedOn__" + (index * 2 + 1);
			guard1 = "[not(" + guard0 + ")]";
			guard0 = "[" + guard0 + "]";
			
			choice0.addNext(adj0);
			choice0.addGuard(guard1);
			
			actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1), "false");
			actionp3 = getActionStateWithParam(tcpu, ad, request.getDestinationTask().getName() + "__state", "0");
			actionp4 = getActionStateWithParam(tcpu, ad, cpuof(request.getDestinationTask()).getName() + "__selected", "0");
			
			choice0.addNext(actionp2);
			choice0.addGuard(guard0);
			actionp2.addNext(actionp3);
			actionp3.addNext(actionp4);
			actionp4.addNext(adj0);
			
			previous = adj0;
		}
		
		previous.addNext(afterCommManager);
	}
	
	/*private int getMyBlockingIndex(TMLCommunicationElement _tmle) {
		int cpt = 0;
		for(TMLCommunicationElement tmle: allcommunications) {
			if (tmle == _tmle) {
				return cpt;
			}
			cpt ++;
		}
			
	}*/
	
	private void endActivityDiagram() {
		ADActionStateWithGate adag;
		ADStop stop;
		
		int maxClockRatio = tmlmapping.getMaxClockRatio();
		System.out.println("Max clock ratio = " + maxClockRatio);
		
		// Loop from the end of comm manager to right before behavior
		afterCommManager.addNext(beforeBehavior);
		
		// Go from the end of init to the beginning of behavior
		ADJunction junc0 = initJuncs.get(initJuncs.size()-1);
		junc0.addNext(afterInit);
		
		// Go from the end of behavior to the beginning of comm manager
		junc0 = behaviorJuncs.get(behaviorJuncs.size()-1);
		junc0.addNext(afterBehavior);
		if (isEndClocked) {
			adag = getActionGate(system, systemad, "system__endTick", "");
			afterBehavior.addNext(adag);
			adag.addNext(afterInit);
		} else {
			afterBehavior.addNext(afterInit);
		}
		
		// General choice? Is the system finished / blocked??? If no: tick. If  yes: terminated
		ADChoice choice0 = new ADChoice();
		String guard = "";
		systemad.add(choice0);
		
		if (hasMaxCountTick) {
			ADChoice choice1 = new ADChoice();
			systemad.add(choice0);
			afterInit.addNext(choice1);
			guard = "count__tick == " + maxCountTickValue;
			choice1.addGuard("[not(" + guard + ")]");
			choice1.addNext(choice0);
			choice1.addGuard("[" + guard + "]");
			adag = getActionGate(system, systemad, "maxCountTickReached", "");
			choice1.addNext(adag);
			stop = new ADStop();
			systemad.add(stop);
			adag.addNext(stop);
		} else {
			afterInit.addNext(choice0);
		}
		
		guard = "";
		int cpt = 0;
		ArrayList<HwNode> executions = tmlarchitecture.getHwNodes();
		for(HwNode node : executions) {
			if (node instanceof HwCPU) {
				if (cpt == 0) {
					guard += "(" + node.getName();
				} else {
					guard += " and (" + node.getName();
				}
				guard += "__selected == 2)";
				cpt ++;
			}
		}
		
		guard += " and (not(transferDone))";
		
		ADJunction adj = new ADJunction();
		systemad.add(adj);
		choice0.addGuard("[not(" + guard + ")]");
		ADComponent previous = choice0;
		
		ADJunction adjcl = null;
		
		if (maxClockRatio != 1) {
			
			Param p1 = system.addNewParamIfApplicable("current__clock", Param.NAT, "0");
			ADActionStateWithParam actions001 = new ADActionStateWithParam(p1);
			systemad.add(actions001);
			actions001.setActionValue("0");
			previous.addNext(actions001);
			
			adjcl = new ADJunction();
			systemad.add(adjcl);
			actions001.addNext(adjcl);
			previous = adjcl;
		}
		
		if (isClocked) {
			if (tickValue.compareTo("1") == 0) {
				adag = getActionGate(system, systemad, gateTick, "");
				previous.addNext(adag);
				adag.addNext(adj);
			} else {
				Param p1 = system.getParamByName("count__modulotick");
				ADActionStateWithParam actions01 = new ADActionStateWithParam(p1);
				systemad.add(actions01);
				actions01.setActionValue("(count__modulotick + 1) % " + tickValue);
				ADChoice choice01 = new ADChoice();
				previous.addNext(actions01);
				actions01.addNext(choice01);
				String guard01, guard02;
				systemad.add(choice01);
				guard01 = "count__modulotick  == 0";
				guard02 = "[not(" + guard01 + ")]";
				guard01 = "[" + guard01 + ")";
				choice01.addGuard(guard02);
				choice01.addNext(adj);
				choice01.addGuard(guard01);
				adag = getActionGate(system, systemad, gateTick, "");
				choice01.addNext(adag);
				adag.addNext(adj);
			}
		} else {
			previous.addNext(adj);
		}
		
		if((isCountTick) || (hasMaxCountTick)) {
			Param p = system.getParamByName("count__tick");
			ADActionStateWithParam actions = new ADActionStateWithParam(p);
			systemad.add(actions);
			actions.setActionValue("count__tick + 1");
			adj.addNext(actions);
			actions.addNext(beforeCommManager);
		} else {
			adj.addNext(beforeCommManager);
		}
		
		choice0.addGuard("[" + guard + "]");
		previous = choice0;
		
		if (maxClockRatio != 1) {
			ADChoice choice001 = new ADChoice();
			systemad.add(choice001);
			previous.addNext(choice001);
			Param p2 = system.addNewParamIfApplicable("current__clock", Param.NAT, "0");
			String guard01 = "[current__clock == " + maxClockRatio + "]";
			String guard02 = "[not(current__clock == " + maxClockRatio + ")]";
			choice001.addGuard(guard02);
			ADActionStateWithParam actions002 = new ADActionStateWithParam(p2);
			systemad.add(actions002);
			actions002.setActionValue("current__clock + 1");
			choice001.addNext(actions002);
			actions002.addNext(adjcl);
			
			choice001.addGuard(guard01);
			previous = choice001;
		}
		
		
		if (showTerminateCPUs) {
			if (isCountTick) {
				adag = getActionGate(system, systemad, "allCPUsTerminated", "!count__tick");
			} else {
				adag = getActionGate(system, systemad, "allCPUsTerminated", "");
			}
			previous.addNext(adag);
			stop = new ADStop();
			systemad.add(stop);
			adag.addNext(stop);
		} else {
			stop = new ADStop();
			systemad.add(stop);
			previous.addNext(stop);
		}
		
		// From start state ...
		ADStart start = systemad.getStartState();
		start.addNext(beforeInit);
	}
	
	private void makeCPUs(ArrayList<TMLChannel> localChannels, ArrayList<TMLEvent> localEvents, ArrayList<TMLRequest> localRequests) {
		ArrayList<HwNode> executions = tmlarchitecture.getHwNodes();
		
		int cpt =0;
		
		for(HwNode node : executions) {
			if (node instanceof HwCPU) {
				System.out.println("CPU=" + node.getName() + " cpt=" + cpt);
				prepareCPU((HwCPU)node, cpt);
			}
			cpt ++;
		}
		
		cpt = 0;
		for(HwNode node : executions) {
			System.out.println("Node=" + node.getName() + " cpt=" + cpt);
			if (node instanceof HwCPU) {
				makeCPU((HwCPU)node, cpt, localChannels, localEvents, localRequests);
				cpt ++;
			}
		}
	}
	
	private void prepareCPU(HwCPU cpu, int index) {
		// Create all ending junctions
		ADJunction junc = new ADJunction();
		initJuncs.add(junc);
		systemad.add(junc);
		
		junc =  new ADJunction();
		behaviorJuncs.add(junc);
		systemad.add(junc);
		
		ArrayList<TMLTask> tasks = new ArrayList<TMLTask>();
		
		ArrayList<HwExecutionNode> executions = tmlmapping.getNodes();
		ArrayList<TMLTask> mappedtasks  = tmlmapping.getMappedTasks();
		
		for(int i=0; i<executions.size(); i++) {
			if (executions.get(i) == cpu) {
				tasks.add(mappedtasks.get(i));
			}
		}
		if (tasks.size() > 0) {
			makeTaskAttributes(system, tasks);
			makeCPUDefaultAttributes(system, cpu);
		}
	}
	
	private void makeCPU(HwCPU cpu, int index, ArrayList<TMLChannel> localChannels, ArrayList<TMLEvent> localEvents, ArrayList<TMLRequest> localRequests) {
		System.out.println("Making cpu: " + cpu.getName());
		ArrayList<TMLTask> tasks = new ArrayList<TMLTask>();
		
		ArrayList<HwExecutionNode> executions = tmlmapping.getNodes();
		ArrayList<TMLTask> mappedtasks  = tmlmapping.getMappedTasks();
		
		for(int i=0; i<executions.size(); i++) {
			if (executions.get(i) == cpu) {
				tasks.add(mappedtasks.get(i));
			}
		}
		
		// The CPU is not empty
		if (tasks.size() > 0) {
			TClass t = system;
			makeCPUDefaultGates(t, cpu, tasks, localChannels, localEvents, localRequests);
			makeCPUActivityDiagram(t, cpu, index, tasks, localChannels, localEvents, localRequests);
		}
	}
	
	private HwCPU cpuof(TMLTask task) {
		int index = tmlmapping.getMappedTasks().indexOf(task);
		if (index == -1) {
			System.out.println("****** -1 index");
			return null;
		}
		HwNode node = tmlmapping.getNodes().get(index);
		if (node instanceof HwCPU) {
			return (HwCPU)node;
		}
		System.out.println("****** Unknown node");
		return null;
		
	}
	
	private void fillCommunicationArrays(TClass tcpu, ArrayList<TMLChannel> localChannels, ArrayList<TMLEvent> localEvents, ArrayList<TMLRequest> localRequests) {
		HwBus bus;
		ArrayList<TMLTask> tasks  = tmlmapping.getMappedTasks();
		
		for(TMLChannel ch: tmlmodeling.getChannels()) {
			if (tasks.contains(ch.getOriginTask()) && tasks.contains(ch.getDestinationTask())) {
				localChannels.add(ch);
				allcommunications.add(ch);
				bus = tmlmapping.getFirstHwBusOf(ch);
				if (bus == null) {
					bus = fakeBus;
					System.out.println("channel " + ch.getName() + " is not mapped");
				} else {
					System.out.println("channel " + ch.getName() + " is mapped on " + bus.getName());
				}
				allbuses.add(bus);
			}
		}
		
		// Sort list of channels -> first channel has the highest priority, etc.
		sortChannels();
		
		for(TMLEvent evt: tmlmodeling.getEvents()) {
			if (tasks.contains(evt.getOriginTask()) && tasks.contains(evt.getDestinationTask())) {
				localEvents.add(evt);
				allcommunications.add(evt);
				allbuses.add(fakeBus);
			}
		}
		
		for(TMLRequest req: tmlmodeling.getRequests()) {
			if (tasks.contains(req.getDestinationTask())) {
				boolean ok = true;
				for(TMLTask task: req.getOriginTasks()) {
					if (!tasks.contains(task)) {
						ok = false;
						break;
					}
				}
				if (ok) {
					localRequests.add(req);
					allcommunications.add(req);
					allbuses.add(fakeBus);
				}
			}
		}
		
		int cpt = 0;
		for(TMLCommunicationElement tmle: allcommunications) {
			if (tmle.isBlockingAtOrigin()) {
				tcpu.addNewParamIfApplicable("blockedOn__" + (2*cpt), "bool", "false");
				tcpu.addNewParamIfApplicable("blockedOn__" + (2*cpt) + "__tmp", "bool", "false");
			}
			if (tmle.isBlockingAtDestination()) {
				tcpu.addNewParamIfApplicable("blockedOn__" + (2*cpt+1), "bool", "false");
				tcpu.addNewParamIfApplicable("blockedOn__" + (2*cpt+1) + "__tmp", "bool", "false");
			}
			cpt++;
		}
	}
	
	private void sortChannels() {
		ArrayList<TMLCommunicationElement> tmpcommunications = new ArrayList<TMLCommunicationElement>();
		TMLChannel ch;
		TMLCommunicationElement tmle;
		int prio, myprio, index, currentIndex = 0;
		HwCPU cpu;
		HwLink link;
		
		for(int i=0; i<allcommunications.size(); i++) {
			tmle = allcommunications.get(i);
			if (!(tmle instanceof TMLChannel)) {
				tmpcommunications.add(tmle);
				allcommunications.remove(i);
				i--;
			}
		}
		
		while(allcommunications.size() > 0) {
			currentIndex = 0;
			index = -1;
			prio = -1;
			for(TMLCommunicationElement tmlee: allcommunications) {
				ch = (TMLChannel)tmlee;
				cpu = cpuof(ch.getOriginTask());
				if (cpu == null) {
					myprio = 0;
				} else {
					link = tmlarchitecture.getHwLinkByHwNode(cpu);
					if (link == null) {
						myprio = 0;
					} else {
						myprio = link.getPriority();
					}
				}
				
				if (myprio > prio) {
					prio = myprio;
					index = currentIndex;
				}
				
				currentIndex ++;
			}
			tmpcommunications.add(allcommunications.get(index));
			allcommunications.remove(index);
		}
		allcommunications = tmpcommunications;
		
		for(int i=0; i<allcommunications.size(); i++) {
			tmle = allcommunications.get(i);
			if (tmle instanceof TMLChannel) {
				ch = (TMLChannel)tmle;
				System.out.println("Channel #" + i + " = " + ch.getName()); 
			}
			
		}
		
	}
	
	private void makeCPUDefaultAttributes(TClass tcpu, HwCPU cpu) {
		// Per CPU
		tcpu.addNewParamIfApplicable(cpu.getName() + "__selected", "nat", "0");
		tcpu.addNewParamIfApplicable(cpu.getName() + "__turn", "nat", "0");
		tcpu.addNewParamIfApplicable(cpu.getName() + "__sizeCPU", "nat", ""+cpu.byteDataSize);
		tcpu.addNewParamIfApplicable(cpu.getName() + "__nTick", "nat", "0");
		tcpu.addNewParamIfApplicable(cpu.getName() + "__byteDataSize", "nat", ""+cpu.byteDataSize);
		
		if ((cpu.branchingPredictionPenalty > 0) && (cpu.pipelineSize>0)) {
			if (showBranching) {
				system.addNewGateIfApplicable(cpu.getName() + "__branching");
			}
			tcpu.addNewParamIfApplicable(cpu.getName() + "__branchingCycle", "nat", "0");
			tcpu.addNewParamIfApplicable(cpu.getName() + "__branchingMiss", "nat", "" + (99/cpu.branchingPredictionPenalty));
			tcpu.addNewParamIfApplicable(cpu.getName() + "__branchingPenalty", "nat", "" + cpu.pipelineSize);
		}
		
		if (cpu.clockRatio != 1) {
			tcpu.addNewParamIfApplicable(cpu.getName() + "__tickRatio", "nat", cpu.clockRatio + " - 1");
		}
		// Multicpu
		//tcpu.addNewParamIfApplicable("system__turn", "nat", "0");
		
	}
	
	private void makeTaskAttributes(TClass tcpu, ArrayList<TMLTask> tasks) {
		int i;
		String init;
		
		for(TMLTask task: tasks) {
			tcpu.addNewParamIfApplicable(task.getName() + "__state", "nat", "0");
			tcpu.addNewParamIfApplicable(task.getName() + "__istate", "nat", "0");
			//tcpu.addNewParamIfApplicable(task.getName() + "__blockedOn", "nat", "0");
			
			for(TMLAttribute attribute:task.getAttributes()) {
				init = attribute.initialValue;
				if ((init == null) || (init.length() == 0)) {
					init = attribute.getDefaultInitialValue();
				}
				switch(attribute.type.getType()) {
				case TMLType.NATURAL:
					//System.out.println("Adding nat attribute:" + attribute.name+ " init=" + attribute.initialValue);
					if (attribute.name.equals("i")) {
						tcpu.addNewParamIfApplicable(task.getName() + "__" + attribute.name + "_0", "nat", modifyString(init, task));
					} else {
						tcpu.addNewParamIfApplicable(task.getName() + "__" + attribute.name, "nat", modifyString(init, task));
					}
					
					break;
				default:
					//System.out.println("Adding other attribute:" + attribute.name + " init=" + init);
					tcpu.addNewParamIfApplicable(task.getName() + "__" + attribute.name, "bool", modifyString(init, task));
				}
			}
		}
	}
	
	private void makeCommunicationAttributes(TClass tcpu,  ArrayList<TMLChannel> localChannels, ArrayList<TMLEvent> localEvents, ArrayList<TMLRequest> localRequests) {	
		// Channels
		for(TMLChannel channel: localChannels) {
			tcpu.addNewParamIfApplicable("toWrite__" + channel.getName(), "nat", "0");
			tcpu.addNewParamIfApplicable("toRead__" + channel.getName(), "nat", "0");
			if (showSampleChannels) {
				tcpu.addNewParamIfApplicable("totalWritten__" + channel.getName(), "nat", "0");
				tcpu.addNewParamIfApplicable("totalRead__" + channel.getName(), "nat", "0");
			}
			switch(channel.getType()) {
			case TMLChannel.BRBW:
				//System.out.println("BRBW");
				//System.out.println("Adding max parameters");
				tcpu.addNewParamIfApplicable("max__" + channel.getName(), "nat", "" + (channel.getSize() * channel.getMax()));
				tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__tmpor", "bool", "false");
				tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__tmpdest", "nat", "0");
				tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__total", "nat", "0");
				tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__or", "nat", "0");
				if (isMapped(channel)) {
					tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__dest", "nat", "0");
				} 
				break;
			case TMLChannel.BRNBW:
				//System.out.println("BRNBW");
				//tcpu.addNewParamIfApplicable("max__" + channel.getName(), "nat", "" + channel.getSize() + "*" + channel.getMax());
				tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__tmpor", "bool", "false");
				tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__tmpdest", "nat", "0");
				/*if (isMapped(channel)) {*/
					tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__or", "nat", "0");
					tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__dest", "nat", "0");
				//} 
				break;
			case TMLChannel.NBRNBW:
				//tcpu.addNewParamIfApplicable("max__" + channel.getName(), "nat", "" + channel.getSize() + "*" + channel.getMax());
				
				if (isMapped(channel)) {
					tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__or", "nat", "0");
					//tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__tmpor", "bool", "false");
					//tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__tmpdest", "nat", "0");
					//tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__or", "nat", "0");
					//tcpu.addNewParamIfApplicable("n__" + channel.getName() + "__dest", "nat", "0");
				} 
				break;
			}
		}
		
		// Events
		for(TMLEvent event: localEvents) {
			tcpu.addNewParamIfApplicable("n__" + event.getName(), "nat", "0");
			tcpu.addNewParamIfApplicable("n__" + event.getName() + "__tmp", "nat", "0");
			
			if (!event.isInfinite()) {
				tcpu.addNewParamIfApplicable("max__" + event.getName(), "nat", "" + event.getMaxSize());
			}
			
			for(int i=0; i<event.getNbOfParams(); i++) {
				tcpu.addNewParamIfApplicable("fifo" + i + "__" + event.getName(), "Queue_nat", "nil");
			}
		}
		
		// Requests
		for(TMLRequest request: localRequests) {
			tcpu.addNewParamIfApplicable("n__" + request.getName(), "nat", "0");
			tcpu.addNewParamIfApplicable("n__" + request.getName() + "__tmp", "nat", "0");
			for(int i=0; i<request.getNbOfParams(); i++) {
				tcpu.addNewParamIfApplicable("fifo" + (i+1) + "__" + request.getName(), "Queue_nat", "nil");
				tcpu.addNewParamIfApplicable(modifyString("arg" + (i+1) + "__req", request.getDestinationTask()), "nat", "0");
			}
		}
	}
	
	private void makeCPUDefaultGates(TClass tcpu, HwCPU cpu, ArrayList<TMLTask> tasks, ArrayList<TMLChannel> localChannels, ArrayList<TMLEvent> localEvents, ArrayList<TMLRequest> localRequests) {
		if (showBlockedCPU) {
			tcpu.addNewGateIfApplicable(cpu.getName() + "__allBlocked");
		}
		
		if (cpu.taskSwitchingTime > 0) {
			tcpu.addNewParamIfApplicable(cpu.getName() + "__switchingTime", "nat", "0");
		}
		
		if (showScheduling) {
			tcpu.addNewGateIfApplicable(cpu.getName() + "__selection");
			if (cpu.taskSwitchingTime > 0) {
				tcpu.addNewGateIfApplicable(cpu.getName() + "__switching");
			}
			for(TMLTask task: tasks) {
				tcpu.addNewGateIfApplicable(task.getName() + "__selected");
			}
		}
		
		if (showTaskState) {
			for(TMLTask task: tasks) {
				tcpu.addNewGateIfApplicable(task.getName() + "__startState");
				tcpu.addNewGateIfApplicable(task.getName() + "__endState");
				tcpu.addNewGateIfApplicable(task.getName() + "__terminated");
			}	
		}
		
		//if (isClocked) {
			//tcpu.addNewGateIfApplicable(cpu.getName() + "__tick");
			//tcpu.addNewGateIfApplicable(cpu.getName() + "__endTick");
		//}
		
		// Channels
		if (showChannels) {
			for(TMLChannel channel: localChannels) {
				if (tasks.contains(channel.getOriginTask())) {
					tcpu.addNewGateIfApplicable(cpu.getName() + "__" + channel.getOriginTask().getName() + "__wr__" + channel.getName());
				}
				if (tasks.contains(channel.getDestinationTask())) {
					tcpu.addNewGateIfApplicable(cpu.getName() + "__" + channel.getDestinationTask().getName() + "__rd__" + channel.getName());
				}
			}
		}
		
		// Events
		if (showEvents) {
			for(TMLEvent event: localEvents) {
				if (tasks.contains(event.getOriginTask())) {
					tcpu.addNewGateIfApplicable(cpu.getName() + "__" + event.getOriginTask().getName() + "__notify__" + event.getName());
				}
				if (tasks.contains(event.getDestinationTask())) {
					tcpu.addNewGateIfApplicable(cpu.getName() + "__" + event.getDestinationTask().getName() + "__wait__" + event.getName());
					tcpu.addNewGateIfApplicable(cpu.getName() + "__"  + event.getDestinationTask().getName() + "__notified__" + event.getName());
				}
			}
		}
		
		// Requests
		if (showRequests) {
			for(TMLRequest request: localRequests) {
				if (tasks.contains(request.getDestinationTask())) {
					tcpu.addNewGateIfApplicable(cpu.getName() + "__" + request.getDestinationTask().getName() + "__waitReq__" + request.getName());
				}
				for(TMLTask task: request.getOriginTasks()) {
					if (tasks.contains(task)) {
						tcpu.addNewGateIfApplicable(cpu.getName() + "__" + task.getName() + "__sendReq__" + request.getName());
					}
				}
			}
		}
	}
	
	private void makeCPUActivityDiagram(TClass tcpu, HwCPU cpu, int index, ArrayList<TMLTask> tasks, ArrayList<TMLChannel> localChannels, ArrayList<TMLEvent> localEvents, ArrayList<TMLRequest> localRequests) {
		ActivityDiagram ad = tcpu.getActivityDiagram();
		
		ADJunction mainInitJunction = initJuncs.get(index);
		
		ADJunction maincpujunc;
		if (index == 0) {
			maincpujunc = beforeBehavior;
		} else {
			maincpujunc = behaviorJuncs.get(index-1);
		}
		
		ADJunction endjunc = behaviorJuncs.get(index);
		
		ADChoice selectedChoice = new ADChoice();
		ad.add(selectedChoice);
		
		makeCPUADInit(tcpu, cpu, index, ad, mainInitJunction, tasks);
		
		makeCPUADSelection(tcpu, cpu, ad, maincpujunc, endjunc, selectedChoice, tasks);
		
		makeCPUADScheduling(tcpu, cpu, ad, selectedChoice, maincpujunc, tasks);
		
		makeCPUADTermination(tcpu, cpu, ad, selectedChoice, endjunc, tasks);
		
		makeCPUADTasks(tcpu, cpu, ad, selectedChoice, maincpujunc, endjunc, tasks);
	}
	
	private void makeCPUADInit(TClass tcpu, HwCPU cpu, int index, ActivityDiagram ad, ADJunction mainJunction, ArrayList<TMLTask> tasks) {
		
		ADComponent start;
		if (index == 0) {
			start = beforeInit;
		} else {
			start = initJuncs.get(index-1);
		}
		ADComponent follow = start;
		
		Param p = tcpu.getParamByName(cpu.getName() + "__turn");
		
		if (randomTasks && (p!= null) && (cpu.schedulingPolicy == HwCPU.BASIC_ROUND_ROBIN)) {
			ADChoice choice = new ADChoice();
			ad.add(choice);
			follow.addNext(choice);
			
			ADJunction junction = new ADJunction();
			ad.add(junction);
			junction.addNext(mainJunction);
			
			ADActionStateWithParam actions;
			int cpt = 0;
			
			for(TMLTask task: tasks) {
				actions = new ADActionStateWithParam(p);
				ad.add(actions);
				actions.setActionValue("" + cpt);
				cpt ++;
				choice.addNext(actions);
				choice.addGuard("[ ]");
				actions.addNext(junction);
			}
		} else {
			follow.addNext(mainJunction);
		}
	}
	
	private void makeCPUADSelection(TClass tcpu, HwCPU cpu, ActivityDiagram ad, ADJunction mainJunction, ADJunction endjunc, ADChoice selectedChoice, ArrayList<TMLTask> tasks) {
		int cpt = 0;
		ADComponent previous = mainJunction;
		String guard0, guard1;
		ADActionStateWithParam adap1, adap2;
		ADActionStateWithGate adag;
		ADComponent previoustmp;
		ADChoice choice0;
		String name;
		
		// Clock ratio management
		if (cpu.clockRatio != 1) {
			name = cpu.getName() + "__tickRatio";
			adap1 = new ADActionStateWithParam(tcpu.getParamByName(name));
			adap1.setActionValue("(" + name + " + 1) % " + cpu.clockRatio);
			ad.add(adap1);
			previous.addNext(adap1);
			
			choice0 = new ADChoice();
			ad.add(choice0);
			adap1.addNext(choice0);
			guard0 = name + " == 0";
			guard1 = "[not(" + guard0 + ")]";
			guard0 = "[" + guard0 + "]";
			
			choice0.addNext(endjunc);
			choice0.addGuard(guard1);
			
			choice0.addGuard(guard0);
			previous = choice0;
		}
		
		// Branching penalty
		if ((cpu.branchingPredictionPenalty > 0) && (cpu.pipelineSize>0)) {
			name = cpu.getName() + "__";
			choice0 = new ADChoice();
			ad.add(choice0);
			previous.addNext(choice0);
			guard0 = name + "branchingCycle == " + name + "branchingMiss";
			guard1 = "[not(" + guard0 + ")]";
			guard0 = "[" + guard0 + "]";
			
			adap1 = new ADActionStateWithParam(tcpu.getParamByName(name + "branchingCycle"));
			adap1.setActionValue(name + "branchingCycle + 1");
			ad.add(adap1);
			choice0.addGuard(guard1);
			choice0.addNext(adap1);
			previous = adap1;
			
			
			choice0.addGuard(guard0);
			if (showBranching) {
				adag = new  ADActionStateWithGate(tcpu.getGateByName(name + "branching"));
				adag.setActionValue("!" + name + "branchingPenalty");
				ad.add(adag);
				choice0.addNext(adag);
				previoustmp = adag;
			} else {
				previoustmp = choice0;
			}
			
			adap2 = new ADActionStateWithParam(tcpu.getParamByName(name + "branchingPenalty"));
			adap2.setActionValue(name + "branchingPenalty - 1");
			ad.add(adap2);
			previoustmp.addNext(adap2);
			
			ADChoice choice1 = new ADChoice();
			ad.add(choice1);
			adap2.addNext(choice1);
			ADJunction junc = new ADJunction();
			ad.add(junc);
			guard0 = name + "branchingPenalty == 0";
			guard1 = "[not(" + guard0 + ")]";
			guard0 = "[" + guard0 + "]";
			choice1.addGuard(guard1);
			choice1.addNext(junc);
			
			ADActionStateWithParam adap3 = new ADActionStateWithParam(tcpu.getParamByName(name + "branchingCycle"));
			adap3.setActionValue("0");
			ad.add(adap3);
			choice1.addGuard(guard0);
			choice1.addNext(adap3);
			
			ADActionStateWithParam adap4 = new ADActionStateWithParam(tcpu.getParamByName(name + "branchingPenalty"));
			adap4.setActionValue("" + cpu.pipelineSize);
			ad.add(adap4);
			adap3.addNext(adap4);
			
			adap4.addNext(junc);
			junc.addNext(endjunc);
		}
		
		// Task switching time
		if (cpu.taskSwitchingTime > 0) {
			choice0 = new ADChoice();
			ad.add(choice0);
			previous.addNext(choice0);
			guard0 = cpu.getName() + "__switchingTime > 0";
			guard1 = "[not(" + guard0 + ")]";
			guard0 = "[" + guard0 + "]";
			
			// First option: CPU is performing shceduling operations
			adap1 = new ADActionStateWithParam(tcpu.getParamByName(cpu.getName() + "__switchingTime"));
			adap1.setActionValue(cpu.getName() + "__switchingTime - 1");
			ad.add(adap1);
			choice0.addGuard(guard0);
			choice0.addNext(adap1);
			previoustmp = adap1;
			if (showScheduling) {
				adag = new  ADActionStateWithGate(tcpu.getGateByName(cpu.getName() + "__switching"));
				adag.setActionValue("!" + cpu.getName() + "__switchingTime");
				ad.add(adag);
				previoustmp.addNext(adag);
				previoustmp = adag;
			}
			previoustmp.addNext(endjunc);
			
			//Second option:no scheduling
			choice0.addGuard(guard1);
			previous = choice0;
		}
		
		
		// Selected information
		if (showScheduling) {
			ADActionStateWithGate selection = new ADActionStateWithGate(tcpu.getGateByName(cpu.getName() + "__selection"));
			String selected = "!" + cpu.getName() + "__selected";
			System.out.println("First value= cpu selected or not");
			System.out.println("Runnable=0, running=1, blocked=2, terminated=3");
			cpt ++;
			for(TMLTask task: tasks) {
				System.out.println("task #i = " + task.getName()); 
				selected += "!" + task.getName() + "__state";
			}
			selection.setActionValue(selected);
			ad.add(selection);
			previous.addNext(selection);
			selection.addNext(selectedChoice);
		} else {
			previous.addNext(selectedChoice);
		}
	}
	
	private void makeCPUADTermination(TClass tcpu, HwCPU cpu, ActivityDiagram ad, ADChoice selectedChoice, ADJunction endjunc, ArrayList<TMLTask> tasks) {
		ADStop stop = new ADStop();
		ad.add(stop);
		selectedChoice.addGuard("[" + cpu.getName() + "__selected == 2]");
		
		if (showBlockedCPU) {
			ADActionStateWithGate terminate = new ADActionStateWithGate(tcpu.getGateByName(cpu.getName() + "__allBlocked"));
			String states = "";
			for(TMLTask task: tasks) {
				states += "!" + task.getName() + "__state";
			}
			terminate.setActionValue(states);
			ad.add(terminate);
			selectedChoice.addNext(terminate);
			terminate.addNext(endjunc);
		} else {
			selectedChoice.addNext(endjunc);
		}
	}
	
	private void makeCPUADScheduling(TClass tcpu, HwCPU cpu, ActivityDiagram ad, ADChoice selectedChoice, ADJunction mainJunction, ArrayList<TMLTask> tasks) {
		// If state is equal to 1, state is set to zero
		String guard0, guard1, action;
		ADChoice test;
		ADJunction junction, junction1;
		ADComponent previous = selectedChoice;
		ADComponent previoustmp;
		ADActionStateWithParam actions, actions0;
		ADActionStateWithGate actiong;
		Param p;
		Gate g;
		int cpt;
		
		
		for(TMLTask task: tasks) {
			guard0 = "[" + task.getName() + "__state == 1]";
			guard1 = "[not(" + task.getName() + "__state == 1)]";
			action = "0";
			
			p = tcpu.getParamByName(task.getName() + "__state");
			if (p != null) {
				actions = new ADActionStateWithParam(p);
				ad.add(actions);
				actions.setActionValue(action);
				
				test = new ADChoice();
				ad.add(test);
				
				junction = new ADJunction();
				ad.add(junction);
				
				test.addNext(actions);
				test.addGuard(guard0);
				test.addNext(junction);
				test.addGuard(guard1);
				
				actions.addNext(junction);
				
				if (previous instanceof ADChoice) {
					selectedChoice.addNext(test);
					selectedChoice.addGuard("[" + cpu.getName() + "__selected == 0]");
				} else {
					previous.addNext(test);
				}
				previous = junction;
			}
			
		}
		
		// If all tasks are blocked or terminated -> selected is put to 2
		ADChoice finished = new ADChoice();
		ad.add(finished);
		previous.addNext(finished);
		
		guard0 = "";
		cpt = 0;
		
		for(TMLTask task: tasks) {
			if (cpt > 0) {
				guard0 += " and ";
			}
			guard0 += "(" + task.getName() + "__state > 1)";
			cpt ++;
		}
		
		guard1 = "[not(" + guard0 + ")]";
		guard0 = "[" + guard0 + "]";
		
		action = "2";
		p = tcpu.getParamByName(cpu.getName() + "__selected");
		
		actions = new ADActionStateWithParam(p);
		ad.add(actions);
		actions.setActionValue(action);
		
		junction = new ADJunction();
		ad.add(junction);
		
		finished.addNext(actions);
		finished.addGuard(guard0);
		actions.addNext(mainJunction);
		
		finished.addNext(junction);
		finished.addGuard(guard1);
		
		// Scheduling RR
		if (cpu.schedulingPolicy == HwCPU.BASIC_ROUND_ROBIN) {
			test = new ADChoice();
			ad.add(test);
			junction.addNext(test);
			
			cpt = 0;
			
			for(TMLTask task: tasks) {
				finished = new ADChoice();
				ad.add(finished);
				guard0 = "[" + cpu.getName() + "__turn == " + cpt + "]";
				
				test.addNext(finished);
				test.addGuard(guard0);
				
				guard0 = task.getName() + "__state < 2";
				guard1 = "[not(" + guard0 + ")]";
				guard0 = "[" + guard0 + "]";
				
				// Changing turn
				if (cpt < (tasks.size() - 1)) {
					action = "" + (cpt+1);
				} else {
					action = "0";
				}
				p = tcpu.getParamByName(cpu.getName() + "__turn");
				actions = new ADActionStateWithParam(p);
				ad.add(actions);
				actions.setActionValue(action);
				
				finished.addNext(actions);
				finished.addGuard(guard1);
				actions.addNext(junction);
				
				// Turn is ok!
				action = "1";
				p = tcpu.getParamByName(cpu.getName() + "__selected");
				actions = new ADActionStateWithParam(p);
				ad.add(actions);
				actions.setActionValue(action);
				
				action = "1";
				p = tcpu.getParamByName(task.getName() + "__state");
				actions0 = new ADActionStateWithParam(p);
				ad.add(actions0);
				actions0.setActionValue(action);
				
				finished.addNext(actions);
				finished.addGuard(guard0);
				actions.addNext(actions0);
				
				previoustmp = actions0;
				if (cpu.taskSwitchingTime > 0) {
					actions = new ADActionStateWithParam(tcpu.getParamByName(cpu.getName() + "__switchingTime"));
					actions.setActionValue("" + cpu.taskSwitchingTime);
					ad.add(actions);
					previoustmp.addNext(actions);
					previoustmp = actions;
				}
				
				// Show scheduling
				if (showScheduling) {
					g = tcpu.getGateByName(task.getName() + "__selected");
					actiong = new ADActionStateWithGate(g);
					actiong.setActionValue("");
					ad.add(actiong);
					previoustmp.addNext(actiong);
					actiong.addNext(mainJunction);
				} else {
					previoustmp.addNext(mainJunction);
				}
				cpt ++;
			}
			
			// Priority-based scheduling
		} else {
			// Tasks are classified according to their priority
			int index = 0;
			int priority = -1;
			ArrayList<TMLTask> prios = new  ArrayList<TMLTask>();
			while(tasks.size() > 0) {
				cpt = 0;
				index = 0;
				for(TMLTask task: tasks) {
					if (task.getPriority() > priority) {
						index = cpt;
						priority = task.getPriority();
					}
					cpt ++;
				}
				prios.add(tasks.get(index));
				tasks.remove(index);
			}
			
			previous = junction;
			//selectedChoice.addGuard("[" + cpu.getName() + "__selected == 0]");
			//junction = new ADJunction();
			//ad.add(junction);
			
			for(TMLTask task: prios) {
				tasks.add(task);
				test = new ADChoice();
				ad.add(test);
				previous.addNext(test);
				
				junction1 = new ADJunction();
				ad.add(junction1);
				previous = junction1;
				
				guard0 = task.getName() + "__state < 2";
				guard1 = "[not(" + guard0 + ")]";
				guard0 = "[" + guard0 + "]";
				
				// Current task is blocked
				test.addNext(junction1);
				test.addGuard(guard1);
				
				// Current task is not blocked
				action = "1";
				p = tcpu.getParamByName(cpu.getName() + "__selected");
				actions = new ADActionStateWithParam(p);
				ad.add(actions);
				actions.setActionValue(action);
				
				action = "1";
				p = tcpu.getParamByName(task.getName() + "__state");
				actions0 = new ADActionStateWithParam(p);
				ad.add(actions0);
				actions0.setActionValue(action);
				
				test.addNext(actions);
				test.addGuard(guard0);
				actions.addNext(actions0);
				
				previoustmp = actions0;
				if (cpu.taskSwitchingTime > 0) {
					actions = new ADActionStateWithParam(tcpu.getParamByName(cpu.getName() + "__switchingTime"));
					actions.setActionValue("" + cpu.taskSwitchingTime);
					ad.add(actions);
					previoustmp.addNext(actions);
					previoustmp = actions;
				}
				
				if (showScheduling) {
					g = tcpu.getGateByName(task.getName() + "__selected");
					actiong = new ADActionStateWithGate(g);
					actiong.setActionValue("");
					ad.add(actiong);
					previoustmp.addNext(actiong);
					actiong.addNext(mainJunction);
				} else {
					previoustmp.addNext(mainJunction);
				}
			}
			previous.addNext(mainJunction);
		}
	}
	
	private void makeCPUADTasks(TClass tcpu, HwCPU cpu, ActivityDiagram ad, ADChoice selectedChoice, ADJunction mainJunction, ADJunction endjunc, ArrayList<TMLTask> tasks) {
		ADChoice taskChoice = new ADChoice();
		ad.add(taskChoice);
		selectedChoice.addNext(taskChoice);
		selectedChoice.addGuard("[" + cpu.getName() + "__selected == 1]");
		int cpt = 0;
		
		ADJunction nextJunction;
		
		nextJunction = new ADJunction();
		ad.add(nextJunction);
		
		ADChoice tick = new ADChoice();
		ad.add(tick);
		
		ADJunction adj = new ADJunction();
		ad.add(adj);
		adj.addNext(mainJunction);
		
		nextJunction.addNext(tick);
		tick.addNext(adj);
		tick.addGuard("[" + cpu.getName() + "__nTick == 0]");
		
		Gate g0 = tcpu.getGateByName(cpu.getName() + "__endTick");
		Gate g1 = tcpu.getGateByName(cpu.getName() + "__tick");
		Param p = tcpu.getParamByName(cpu.getName() + "__nTick");
		/*ADActionStateWithGate action0 = new ADActionStateWithGate(g0);
		action0.setActionValue("");
		ad.add(action0);
		ADActionStateWithGate action1 = new ADActionStateWithGate(g1);
		action1.setActionValue("");
		ad.add(action1);*/
		ADActionStateWithParam action2 = new ADActionStateWithParam(p);
		action2.setActionValue("0");
		ad.add(action2);
		
		tick.addNext(action2);
		tick.addGuard("[" + cpu.getName() + "__nTick == 1]");
		action2.addNext(endjunc);
		
		/*if (isClocked) {
			nextJunction = new ADJunction();
			ad.add(nextJunction);
			
			ADChoice tick = new ADChoice();
			ad.add(tick);
			
			ADJunction adj = new ADJunction();
			ad.add(adj);
			adj.addNext(mainJunction);
			
			nextJunction.addNext(tick);
			tick.addNext(adj);
			tick.addGuard("[" + cpu.getName() + "__nTick == 0]");
			
			Gate g0 = tcpu.getGateByName(cpu.getName() + "__endTick");
			Gate g1 = tcpu.getGateByName(cpu.getName() + "__tick");
			Param p = tcpu.getParamByName(cpu.getName() + "__nTick");
			ADActionStateWithGate action0 = new ADActionStateWithGate(g0);
			action0.setActionValue("");
			ad.add(action0);
			ADActionStateWithGate action1 = new ADActionStateWithGate(g1);
			action1.setActionValue("");
			ad.add(action1);
			ADActionStateWithParam action2 = new ADActionStateWithParam(p);
			action2.setActionValue("0");
			ad.add(action2);
			
			tick.addNext(action0);
			tick.addGuard("[" + cpu.getName() + "__nTick == 1]");
			action0.addNext(action2);
			action2.addNext(action1);
			action1.addNext(adj);
		} else {
			nextJunction = mainJunction;
		}*/
		
		for(TMLTask task: tasks) {
			makeCPUADTask(tcpu, cpu, ad, taskChoice, nextJunction, task, cpt, tasks);
			cpt ++;
		}
	}
	
	private void makeCPUADTask(TClass tcpu, HwCPU cpu, ActivityDiagram ad, ADChoice taskChoice, ADJunction nextJunction, TMLTask task, int cpt, ArrayList<TMLTask> tasks) {
		ADChoice stateChoice = new ADChoice();
		ad.add(stateChoice);
		taskChoice.addGuard("[" + task.getName() + "__state == 1]");
		
		
		// Common beginning
		if (showTaskState) {
			Gate g = tcpu.getGateByName(task.getName() + "__startState");
			ADActionStateWithGate actiong = new ADActionStateWithGate(g);
			ad.add(actiong);
			actiong.setActionValue("!" + task.getName() + "__istate");
			taskChoice.addNext(actiong);
			actiong.addNext(stateChoice);
		} else {
			taskChoice.addNext(stateChoice);
		}
		
		// Common end
		ADJunction endJunction = new ADJunction();
		ad.add(endJunction);
		
		ADJunction adj = new ADJunction();
		ad.add(adj);
		adj.addNext(nextJunction);
		
		ADComponent previous;
		// if show state ...
		if (showTaskState) {
			Gate g = tcpu.getGateByName(task.getName() + "__endState");
			ADActionStateWithGate actiong = new ADActionStateWithGate(g);
			actiong.setActionValue("!" + task.getName() + "__istate");
			ad.add(actiong);
			endJunction.addNext(actiong);
			previous = actiong;
		} else {
			previous = endJunction;
		}
		
		String guard = task.getName() + "__state == 1";
		ADChoice endChoice = new ADChoice();
		ad.add(endChoice);
		previous.addNext(endChoice);
		Param p = tcpu.getParamByName(cpu.getName() + "__selected");
		endChoice.addNext(adj);
		endChoice.addGuard("[" + guard + "]");
		
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		ad.add(actionp);
		actionp.setActionValue("0");
		endChoice.addNext(actionp);
		endChoice.addGuard("[not(" + guard + ")]");
		actionp.addNext(adj);
		
		
		//stateChoice.addNext(endJunction);
		//stateChoice.addGuard("[ ]");
		
		
		makeCPUADTaskBehavior(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks);
	}
	
	private void makeCPUADTaskBehavior(TClass tcpu, HwCPU cpu, ActivityDiagram ad, ADChoice stateChoice, ADJunction endJunction, TMLTask task, int cpt, ArrayList<TMLTask> tasks) {
		ADStart start = ad.getStartState();
		
		int stateId = 0;
		String gateName;
		ADActionStateWithGate actiong1;
		
		
		// Manage requested
		if (task.isRequested()) {
			TMLRequest req = task.getRequest();
			ADChoice choice = new ADChoice();
			ad.add(choice);
			stateIdGuard(stateChoice, choice, stateId, task);
			
			String guard = "n__" + req.getName() + "__tmp == 0";
			String guard0 = "[" + guard + "]";
			String guard1 = "[not(" + guard + ")]";
			
			// Blocked branch
			ADActionStateWithParam actionp = getStateActionStateWithParam(tcpu, ad, task, "2");
			
			choice.addNext(actionp);
			choice.addGuard(guard0);
			
			int index = allcommunications.indexOf(req);
			//System.out.println("index of block = " + index);
			ADActionStateWithParam actionp0 = getBlockedOnActionStateWithParam(tcpu, ad, (2 * index) + 1);
			actionp.addNext(actionp0);
			actionp0.addNext(endJunction);
			
			// Get request branch
			ADActionStateWithParam actionp1 = getActionStateWithParam(tcpu, ad, "n__" + req.getName(), "n__" + req.getName() + " - 1");
			choice.addNext(actionp1);
			choice.addGuard(guard1);
			
			//gateName = task.getName() + "__WAITREQ__" + req.getName();
			//tcpu.addNewGateIfApplicable(gateName);
			//actiong1 = getActionGate(tcpu, ad, gateName, "");
			//actionp1.addNext(actiong1);
			
			ADComponent previous = actionp1;
			ADActionStateWithParam actionp2, actionp3;
			
			for(int i=0; i<req.getNbOfParams(); i++) {
				actionp2 = getReqFirstActionStateWithParam(tcpu, ad, task, req, i+1);
				actionp3 = getReqDequeueActionStateWithParam(tcpu, ad, task, req, i+1);
				previous.addNext(actionp2);
				actionp2.addNext(actionp3);
				previous = actionp3;
			}
			
			ADActionStateWithParam actionp5 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			if (showRequests) {
				ADActionStateWithGate actiong = getWaitReqActionStateWithGate(tcpu, cpu, ad, task, req);
				previous.addNext(actiong);
				actiong.addNext(actionp5);
			} else {
				previous.addNext(actionp5);
			}
			
			ADActionStateWithParam actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
			actionp6.addNext(endJunction);
			actionp5.addNext(actionp6);
			
			
		}
		
		TMLActivityElement element = task.getActivityDiagram().getFirst().getNextElement(0);
		makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, -1, element);
		
		//stateChoice.addNext(endJunction);
		//stateChoice.addGuard("[nTick > 2]");
	}
	
	private int makeCPUADTaskBehaviorComponent(TClass tcpu, HwCPU cpu, ActivityDiagram ad, ADChoice stateChoice, ADJunction endJunction, TMLTask task, int cpt, ArrayList<TMLTask> tasks, int stateId, int branchStateId, TMLActivityElement element) {
		
		ADActionStateWithParam actionp0, actionp1, actionp2, actionp3, actionp4, actionp5, actionp6, actionp7, actionp8, actionp9;
		ADActionStateWithParam actionpspe = null;
		ADActionStateWithGate actiong0, actiong1, actiong2, actiongd;
		Gate g0, g1, g2;
		TMLSendRequest sendreq;
		ADComponent previous;
		TMLRequest req;
		TMLForLoop tmlforloop;
		ADChoice choice0, choice1 = null, choice2;
		ADJunction adj0, adj1 = null;
		Param param0, param1, param2, param3;
		TMLWriteChannel tmlwc;
		TMLReadChannel tmlrc;
		TMLChannel ch;
		TMLChoice tmlchoice;
		TMLSendEvent sendevt;
		TMLEvent evt;
		TMLSelectEvt tmlse;
		TMLRandom tmlrandom;
		
		String guard0, guard1, guard2;
		int index, i;
		String gateName, gateName0, paramName, cpts, action0, action1, action2, action3;
		String name, nameTot;
		
		//System.out.println("task=" + task.getName() + " stateid=" + stateId + " elt=" + element);
		
		// STOP
		//System.out.println("BranchStateId=" + branchStateId);
		if (element instanceof TMLStopState) {
			//System.out.println("BranchStateId=" + branchStateId);
			if (branchStateId != -1) {
				// Intermediate stop state -> must branch to corresponding end state
				// Then, the translation ends
				//System.out.println("Branching to: " + branchStateId);
				actionp0 = getStateIdActionState(tcpu, ad, task, branchStateId);
				stateIdGuard(stateChoice, actionp0, stateId, task);
				actionp0.addNext(endJunction);
				stateId ++;
				return stateId;
			} else {
				if (task.isRequested()) {
					//System.out.println("Task requested: stateId=" + stateId);
					actionp0 = getInitialStateIdActionState(tcpu, ad, task);
					stateIdGuard(stateChoice, actionp0, stateId, task);
					actionp0.addNext(endJunction);
					stateId ++;
					//stateId = 0;
				} else {
					//System.out.println("Task not requested");
					// End of the activity
					actionp0 = getStateActionStateWithParam(tcpu, ad, task, "3");
					stateIdGuard(stateChoice, actionp0, stateId, task);
					stateId ++;
					
					if (showTaskState) {
						actiong0 = getActionGate(tcpu, ad, task.getName() + "__terminated", "");
						actionp0.addNext(actiong0);
						actiong0.addNext(endJunction);
					} else {
						actionp0.addNext(endJunction);
					}
				}
			}
			
			// Write Channel
		}  else if (element instanceof TMLWriteChannel) {
			tmlwc = (TMLWriteChannel)(element);
			// Multiwrite channels are not yet supported
			ch = tmlwc.getChannel(0);
			name = ch.getName();
			
			param1 = tcpu.getParamByName("toWrite__" + name);
			paramName = param1.getName();
			gateName = cpu.getName() + "__" + task.getName() + "__wro__" + name;
			gateName0 = cpu.getName() + "__" + task.getName() + "__wr__" + name;
			if (showChannels) {
				tcpu.addNewGateIfApplicable(gateName);
			}
			if (showSampleChannels) {
				//System.out.println("Show sample channels");
				tcpu.addNewGateIfApplicable(gateName0);
				nameTot = "totalWritten__" + ch.getName();
				
				actionpspe = getActionStateWithParam(tcpu, ad, nameTot, nameTot + " + written");
				adj1 = new ADJunction();
				ad.add(adj1);
				actionpspe.addNext(adj1);
				choice1 = new ADChoice();
				ad.add(choice1);
				adj1.addNext(choice1);
				
				guard0 = "[ " + nameTot  + " < " + ch.getSize() + "]";
				guard1 = "[ not(" + nameTot + " < " + ch.getSize() + ")]";
				
				choice1.addGuard(guard1);
				actiong0 = getActionGate(tcpu, ad, gateName0, "!" + ch.getSize() + "!" + ch.getSize());
				actionp0 = getActionStateWithParam(tcpu, ad, nameTot, nameTot + " - " + ch.getSize());
				choice1.addNext(actiong0);
				actiong0.addNext(actionp0);
				actionp0.addNext(adj1);
				
				choice1.addGuard(guard0);
			}
			
			// Init: nb to write in channel
			actionp1 = getActionStateWithParam(tcpu, ad, param1.getName(), "(" + modifyString(tmlwc.getNbOfSamples(), task) + ") * (" + ch.getSize() + ")");
			stateIdGuard(stateChoice, actionp1, stateId, task);
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			actionp1.addNext(actionp2);
			actionp2.addNext(endJunction);
			stateId ++;
			
			// debug?
			if (showTaskState) {
				tcpu.addNewGateIfApplicable("writeInfo");
				if (ch.getType() == TMLChannel.NBRNBW) {
					actiongd = getActionGate(tcpu, ad, "writeInfo", "!toWrite__" + name);
				} else if (ch.getType() == TMLChannel.BRNBW){
					actiongd = getActionGate(tcpu, ad, "writeInfo", "!toWrite__" + name + "!n__" + name + "__or");
				} else {
					actiongd = getActionGate(tcpu, ad, "writeInfo", "!toWrite__" + name + "!n__" + name + "__total!max__" + name);
				}
				choice0 = new ADChoice();
				ad.add(choice0);
				stateIdGuard(stateChoice, actiongd, stateId, task);
				actiongd.addNext(choice0);
				guard0 = param1.getName() + " < 1";
				// -> no data towrite
				choice0.addGuard("[" + guard0 + "]");
				actionp3 = getNextStateIdActionState(tcpu, ad, task);
				choice0.addNext(actionp3);
				actionp3.addNext(endJunction);
				stateId ++;
			} else {
				// Test: data to write?
				choice0 = new ADChoice();
				ad.add(choice0);
				stateIdGuard(stateChoice, choice0, stateId, task);
				guard0 = param1.getName() + " < 1";
				// -> no data towrite
				choice0.addGuard("[" + guard0 + "]");
				actionp3 = getNextStateIdActionState(tcpu, ad, task);
				choice0.addNext(actionp3);
				actionp3.addNext(endJunction);
				stateId ++;
			}
			
			// ->data to write
			// First compute the nb of data to write
			// Make on action on write, deduce the number of samples written and make a tick
			// Block if cannot write
			choice0.addGuard("[not(" + guard0 + ")]");
			switch(ch.getType()) {
			case TMLChannel.BRBW:
				//System.out.println("BRBW");
				//tcpu.addNewParamIfApplicable("max__" + channel.getName(), "nat", "" + channel.getSize() + "*" + channel.getMax());
				//tcpu.addNewParamIfApplicable("n__" + channel.getName(), "nat", "0");
				//Channel is full?
				choice2 = new ADChoice();
				ad.add(choice2);
				choice0.addNext(choice2);
				
				if(isMapped(ch)) {
					guard0 = "(n__" + name + "__or + n__" + name + "__total) == (max__" + name + ")";
				} else {
					guard0 = "(n__" + name + "__total) == (max__" + name + ")";
				}
				guard1 = "[ " + guard0 + " ]";
				guard2 = "[not(" + guard0 + ")]";
				
				// -> channel is full
				// must block on write
				actionp1 = getStateActionStateWithParam(tcpu, ad, task, "2");
				choice2.addNext(actionp1);
				choice2.addGuard(guard1);
				index = allcommunications.indexOf(ch);
				actionp0 = getBlockedOnActionStateWithParam(tcpu, ad, 2 * index);
				actionp1.addNext(actionp0);
				actionp0.addNext(endJunction);
				
				
				// channel is not full
				actionp5 = getActionStateWithParam(tcpu, ad, "written", "min(min(" + paramName + ", " +cpu.getName() + "__byteDataSize), max__" + name + " - (n__" + name + "__or + n__" + name + "__total))");
				choice2.addNext(actionp5);
				choice2.addGuard(guard2);
				actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
				if (showSampleChannels) {
					choice1.addNext(actionp6);
					previous = actionpspe;
				} else {
					previous = actionp6;
				}
				if (showChannels) {
					actiong0 = getActionGate(tcpu, ad, gateName, "!written!" + ch.getSize());
					actionp5.addNext(actiong0);
					actiong0.addNext(previous);
				} else {
					actionp5.addNext(previous);
				}
				actionp7 = getActionStateWithParam(tcpu, ad, paramName, paramName + " - written");
				actionp6.addNext(actionp7);
				actionp8 = getActionStateWithParam(tcpu, ad, "n__" + name + "__or", "n__" + name + "__or + written");
				actionp7.addNext(actionp8);
				actionp9 = getActionStateWithParam(tcpu, ad, "n__" + name + "__tmpor", "true");
				actionp8.addNext(actionp9);
				actionp9.addNext(endJunction);
				
				break;
			case TMLChannel.BRNBW:
				//System.out.println("BRNBW");
				actionp5 = getActionStateWithParam(tcpu, ad, "written", "min(" + paramName + ", " + cpu.getName() + "__byteDataSize)");
				choice0.addNext(actionp5);
				actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
				if (showSampleChannels) {
					choice1.addNext(actionp6);
					previous = actionpspe;
				} else {
					previous = actionp6;
				}
				if (showChannels) {
					actiong0 = getActionGate(tcpu, ad, gateName, "!written!" + ch.getSize());
					actionp5.addNext(actiong0);
					actiong0.addNext(previous);
				} else {
					actionp5.addNext(previous);
				}
				actionp7 = getActionStateWithParam(tcpu, ad, paramName, paramName + " - written");
				actionp6.addNext(actionp7);
				actionp8 = getActionStateWithParam(tcpu, ad, "n__" + name + "__or", "n__" + name+ "__or + written");
				actionp7.addNext(actionp8);
				actionp9 = getActionStateWithParam(tcpu, ad, "n__" + name + "__tmpor", "true");
				actionp8.addNext(actionp9);
				actionp9.addNext(endJunction);
				break;
				
			case TMLChannel.NBRNBW:
				//System.out.println("NBRNBW");
				actionp5 = getActionStateWithParam(tcpu, ad, "written", "min(" + paramName + ", " + cpu.getName() + "__byteDataSize)");
				choice0.addNext(actionp5);
				actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
				if (showSampleChannels) {
					choice1.addNext(actionp6);
					previous = actionpspe;
				} else {
					previous = actionp6;
				}
				if (showChannels) {
					actiong0 = getActionGate(tcpu, ad, gateName, "!written!" + ch.getSize());
					actionp5.addNext(actiong0);
					actiong0.addNext(previous);
				} else {
					actionp5.addNext(previous);
				}
				actionp7 = getActionStateWithParam(tcpu, ad, paramName, paramName + " - written");
				actionp6.addNext(actionp7);
				if (isMapped(ch)) {
					actionp8 = getActionStateWithParam(tcpu, ad, "n__" + name + "__or", "n__" + name+ "__or + written");
					actionp7.addNext(actionp8);
					actionp8.addNext(endJunction);
				} else {
					actionp7.addNext(endJunction);
				}
				break;
			}
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
			// Read Channel
		}  else if (element instanceof TMLReadChannel) {
			tmlrc = (TMLReadChannel)(element);
			ch = tmlrc.getChannel(0);
			name = ch.getName();
			
			param1 = tcpu.getParamByName("toRead__" + name);
			paramName = param1.getName();
			gateName = cpu.getName() + "__" + task.getName() + "__rdo__" + name;
			gateName0 = cpu.getName() + "__" + task.getName() + "__rd__" + name;
			
			if (showChannels) {
				tcpu.addNewGateIfApplicable(gateName);
			}
			
			if (showSampleChannels) {
				//System.out.println("Show sample channels");
				tcpu.addNewGateIfApplicable(gateName0);
				nameTot = "totalRead__" + ch.getName();
				
				actionpspe = getActionStateWithParam(tcpu, ad, nameTot, nameTot + " + read");
				adj1 = new ADJunction();
				ad.add(adj1);
				actionpspe.addNext(adj1);
				choice1 = new ADChoice();
				ad.add(choice1);
				adj1.addNext(choice1);
				
				guard0 = "[ " + nameTot  + " < " + ch.getSize() + "]";
				guard1 = "[ not(" + nameTot + " < " + ch.getSize() + ")]";
				
				choice1.addGuard(guard1);
				actiong0 = getActionGate(tcpu, ad, gateName0, "!" + ch.getSize() + "!" + ch.getSize());
				actionp0 = getActionStateWithParam(tcpu, ad, nameTot, nameTot + " - " + ch.getSize());
				choice1.addNext(actiong0);
				actiong0.addNext(actionp0);
				actionp0.addNext(adj1);
				
				choice1.addGuard(guard0);
			}
			
			// Init: nb to read from channel
			actionp1 = getActionStateWithParam(tcpu, ad, param1.getName(), "(" + modifyString(tmlrc.getNbOfSamples(), task) + ") * (" + ch.getSize() + ")");
			stateIdGuard(stateChoice, actionp1, stateId, task);
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			actionp1.addNext(actionp2);
			actionp2.addNext(endJunction);
			stateId ++;
			
			// Test: data to read?
			choice0 = new ADChoice();
			ad.add(choice0);
			stateIdGuard(stateChoice, choice0, stateId, task);
			guard0 = param1.getName() + " < 1";
			// -> no data to read
			choice0.addGuard("[" + guard0 + "]");
			actionp3 = getNextStateIdActionState(tcpu, ad, task);
			choice0.addNext(actionp3);
			actionp3.addNext(endJunction);
			stateId ++;
			
			// ->data to read
			switch(ch.getType()) {
			case TMLChannel.BRBW:
			case TMLChannel.BRNBW:
				choice0.addGuard("[not(" + guard0 + ")]");	
				choice2 = new ADChoice();
				ad.add(choice2);
				choice0.addNext(choice2);
				
				guard2 = "n__" + name + "__tmpdest == 0";
				guard0 = "[" + guard2 + "]";
				guard1 = "[not(" + guard2 + ")]";
				
				// If nothing in channel -> must block
				// Blocked branch
				actionp1 = getStateActionStateWithParam(tcpu, ad, task, "2");
				choice2.addNext(actionp1);
				choice2.addGuard(guard0);
				index = allcommunications.indexOf(ch);
				actionp0 = getBlockedOnActionStateWithParam(tcpu, ad, (2 * index) + 1);
				actionp1.addNext(actionp0);
				actionp0.addNext(endJunction);
				
				// Not blocked branch
				actionp5 = getActionStateWithParam(tcpu, ad, "read", "min(min(" + paramName + ", " + cpu.getName() + "__byteDataSize), n__" + name + "__tmpdest)");
				choice2.addNext(actionp5);
				choice2.addGuard(guard1);
				actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
				if (showSampleChannels) {
					choice1.addNext(actionp6);
					previous = actionpspe;
				} else {
					previous = actionp6;
				}
				if (showChannels) {
					actiong0 = getActionGate(tcpu, ad, gateName, "!read!" + ch.getSize());
					actionp5.addNext(actiong0);
					actiong0.addNext(previous);
				} else {
					actionp5.addNext(previous);
				}
				actionp7 = getActionStateWithParam(tcpu, ad, paramName, paramName + " - read");
				actionp6.addNext(actionp7);
				actionp8 = getActionStateWithParam(tcpu, ad, "n__" + name + "__tmpdest", "n__" + name + "__tmpdest - read");
				actionp7.addNext(actionp8);
				
				if (!ch.isBlockingAtOrigin()) {
					actionp8.addNext(endJunction);
				} else {
					// Must signal the origin task if it was blocked...
					
					index = allcommunications.indexOf(ch);
					actionp9 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2) + "__tmp", "true");
					actionp8.addNext(actionp9);
					actionp9.addNext(endJunction);
					
					/*choice2 = new ADChoice();
					ad.add(choice2);
					actionp8.addNext(choice2);
					index = allcommunications.indexOf(ch);
					guard0 = "blockedOn__" + (index * 2) + " == 1";
					guard1 = "[not(" + guard0 + ")]";
					guard0 = "[" + guard0 + "]";
					
					adj0 = new ADJunction();
					ad.add(adj0);
					adj0.addNext(endJunction);
					
					choice2.addNext(adj0);
					choice2.addGuard(guard1);
					
					actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2), "0");
					actionp3 = getActionStateWithParam(tcpu, ad, ch.getOriginTask().getName() + "__state", "0");
					actionp4 = getActionStateWithParam(tcpu, ad, cpuof(ch.getOriginTask()).getName() + "__selected", "0");
					
					choice2.addNext(actionp2);
					choice2.addGuard(guard0);
					actionp2.addNext(actionp3);
					actionp3.addNext(actionp4);
					actionp4.addNext(endJunction);*/
				}
				break;
			case TMLChannel.NBRNBW:
				//System.out.println("NBRNBW");
				actionp5 = getActionStateWithParam(tcpu, ad, "read", "min(" + paramName + ", " + cpu.getName() + "__byteDataSize)");
				choice0.addNext(actionp5);
				choice0.addGuard("[not(" + guard0 + ")]");	
				actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
				if (showSampleChannels) {
					choice1.addNext(actionp6);
					previous = actionpspe;
				} else {
					previous = actionp6;
				}
				if (showChannels) {
					actiong0 = getActionGate(tcpu, ad, gateName, "!read!" + ch.getSize());
					actionp5.addNext(actiong0);
					actiong0.addNext(previous);
				} else {
					actionp5.addNext(previous);
				}
				actionp7 = getActionStateWithParam(tcpu, ad, paramName, paramName + " - read");
				actionp6.addNext(actionp7);
				actionp7.addNext(endJunction);
				break;
			}
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
			// SEND REQUEST
		}  else if (element instanceof TMLSendRequest) {
			sendreq = (TMLSendRequest)element;
			req = sendreq.getRequest();
			
			actionp1 = getActionStateWithParam(tcpu, ad, "n__" + req.getName(), "n__" + req.getName() + " + 1");
			stateIdGuard(stateChoice, actionp1, stateId, task);
			previous = actionp1;
			
			for(i=0; i<req.getNbOfParams(); i++) {
				actionp2 = getReqEnqueueActionStateWithParam(tcpu, ad, task, sendreq, i+1);
				previous.addNext(actionp2);
				previous = actionp2;
			}
			
			actionp5 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			if (showRequests) {
				actiong0 = getSendReqActionStateWithGate(tcpu, cpu, ad, task, sendreq);
				previous.addNext(actiong0);
				actiong0.addNext(actionp5);
			} else {
				previous.addNext(actionp5);
			}
			
			actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
			//actionp6.addNext(endJunction);
			actionp5.addNext(actionp6);
			
			// New approach
			index = allcommunications.indexOf(req);
			actionp7 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1) + "__tmp", "true");
			actionp6.addNext(actionp7);
			actionp7.addNext(endJunction);
			
			/*choice0 = new ADChoice();
			ad.add(choice0);
			actionp6.addNext(choice0);
			index = allcommunications.indexOf(req);
			guard0 = "blockedOn__" + (index * 2 + 1) + " == 1";
			guard1 = "[not(" + guard0 + ")]";
			guard0 = "[" + guard0 + "]";
			
			adj0 = new ADJunction();
			ad.add(adj0);
			adj0.addNext(endJunction);
			
			choice0.addNext(adj0);
			choice0.addGuard(guard1);
			
			actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1), "0");
			actionp3 = getActionStateWithParam(tcpu, ad, req.getDestinationTask().getName() + "__state", "0");
			actionp4 = getActionStateWithParam(tcpu, ad, cpuof(req.getDestinationTask()).getName() + "__selected", "0");
			
			choice0.addNext(actionp2);
			choice0.addGuard(guard0);
			actionp2.addNext(actionp3);
			actionp3.addNext(actionp4);
			actionp4.addNext(adj0);*/
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
			
			// SEND EVENT
		}  else if (element instanceof TMLSendEvent) {
			//System.out.println("*********** TML send Evt");
			sendevt = (TMLSendEvent)element;
			evt = sendevt.getEvent();
			
			if (evt.isInfinite()) {
				actionp1 = getActionStateWithParam(tcpu, ad, "n__" + evt.getName(), "n__" + evt.getName() + " + 1");
				stateIdGuard(stateChoice, actionp1, stateId, task);
				previous = actionp1;
				
				for(i=0; i<evt.getNbOfParams(); i++) {
					actionp2 = getEvtEnqueueActionStateWithParam(tcpu, ad, task, sendevt, i);
					previous.addNext(actionp2);
					previous = actionp2;
				}
				
				actionp5 = getNextStateIdActionState(tcpu, ad, task);
				stateId ++;
				if (showEvents) {
					actiong0 = getSendEvtActionStateWithGate(tcpu, cpu, ad, task, sendevt);
					previous.addNext(actiong0);
					actiong0.addNext(actionp5);
				} else {
					previous.addNext(actionp5);
				}
				
				//gateName = task.getName() + "__SENDREQ__" + req.getName();
				//tcpu.addNewGateIfApplicable(gateName);
				//actiong1 = getActionGate(tcpu, ad, gateName, "");
				//actiong0.addNext(actiong1);
				
				
				actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
				//actionp6.addNext(endJunction);
				actionp5.addNext(actionp6);
				
				// New approach
				index = allcommunications.indexOf(evt);
				actionp7 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1) + "__tmp", "true");
				actionp6.addNext(actionp7);
				actionp7.addNext(endJunction);
				
				/*choice0 = new ADChoice();
				ad.add(choice0);
				actionp6.addNext(choice0);
				index = allcommunications.indexOf(evt);
				guard0 = "blockedOn__" + (index * 2 + 1) + " == 1";
				guard1 = "[not(" + guard0 + ")]";
				guard0 = "[" + guard0 + "]";
				
				adj0 = new ADJunction();
				ad.add(adj0);
				adj0.addNext(endJunction);
				
				choice0.addNext(adj0);
				choice0.addGuard(guard1);
				
				actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1), "0");
				actionp3 = getActionStateWithParam(tcpu, ad, evt.getDestinationTask().getName() + "__state", "0");
				actionp4 = getActionStateWithParam(tcpu, ad, cpu.getName() + cpu.getName() + "__selected", "0");
				
				choice0.addNext(actionp2);
				choice0.addGuard(guard0);
				actionp2.addNext(actionp3);
				actionp3.addNext(actionp4);
				
				actionp4.addNext(endJunction);*/
				
			} else {
				if (evt.isBlocking()) {
					choice1 = new ADChoice();
					ad.add(choice1);
					guard0 = "n__" + evt.getName() + "__tmp == max__" + evt.getName();
					guard1 = "[not(" + guard0 + ")]";
					guard2 = "[" + guard0 + "]";
					
					stateIdGuard(stateChoice, choice1, stateId, task);
					
					// FIFO is not full
					actionp1 = getActionStateWithParam(tcpu, ad, "n__" + evt.getName(), "n__" + evt.getName() + " + 1");
					choice1.addNext(actionp1);
					choice1.addGuard(guard1);
					previous = actionp1;
					for(i=0; i<evt.getNbOfParams(); i++) {
						actionp2 = getEvtEnqueueActionStateWithParam(tcpu, ad, task, sendevt, i);
						previous.addNext(actionp2);
						previous = actionp2;
					}
					
					actionp5 = getNextStateIdActionState(tcpu, ad, task);
					stateId ++;
					if (showEvents) {
						actiong0 = getSendEvtActionStateWithGate(tcpu, cpu, ad, task, sendevt);
						previous.addNext(actiong0);
						actiong0.addNext(actionp5);
					} else {
						previous.addNext(actionp5);
					}
					
					actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
					//actionp6.addNext(endJunction);
					actionp5.addNext(actionp6);
					
					// New approach
					index = allcommunications.indexOf(evt);
					actionp7 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1) + "__tmp", "true");
					actionp6.addNext(actionp7);
					actionp7.addNext(endJunction);
					
					/*choice0 = new ADChoice();
					ad.add(choice0);
					actionp6.addNext(choice0);
					index = allcommunications.indexOf(evt);
					guard0 = "blockedOn__" + (index * 2 + 1) + " == 1";
					guard1 = "[not(" + guard0 + ")]";
					guard0 = "[" + guard0 + "]";
					
					adj0 = new ADJunction();
					ad.add(adj0);
					adj0.addNext(endJunction);
					
					choice0.addNext(adj0);
					choice0.addGuard(guard1);
					
					actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1), "0");
					actionp3 = getActionStateWithParam(tcpu, ad, evt.getDestinationTask().getName() + "__state", "0");
					actionp4 = getActionStateWithParam(tcpu, ad, cpu.getName() + "__selected", "0");
					
					choice0.addNext(actionp2);
					choice0.addGuard(guard0);
					actionp2.addNext(actionp3);
					actionp3.addNext(actionp4);
					
					actionp4.addNext(endJunction);*/
					
					// FIFO is full -> must block
					actionp1 = getStateActionStateWithParam(tcpu, ad, task, "2");
					choice1.addNext(actionp1);
					choice1.addGuard(guard2);
					index = allcommunications.indexOf(evt);
					actionp0 = getBlockedOnActionStateWithParam(tcpu, ad, 2 * index);
					actionp1.addNext(actionp0);
					actionp0.addNext(endJunction);
					
				} else {
					// Crushing last event if necessary
					choice1 = new ADChoice();
					ad.add(choice1);
					guard0 = "n__" + evt.getName() + "__tmp == max__" + evt.getName();
					guard1 = "[not(" + guard0 + ")]";
					guard0 = "[" + guard0 + "]";
					
					adj0 = new ADJunction();
					ad.add(adj0);
					
					stateIdGuard(stateChoice, choice1, stateId, task);
					
					// FIFO is not full
					actionp1 = getActionStateWithParam(tcpu, ad, "n__" + evt.getName(), "n__" + evt.getName() + " + 1");
					choice1.addNext(actionp1);
					choice1.addGuard(guard1);
					previous = actionp1;
					for(i=0; i<evt.getNbOfParams(); i++) {
						actionp2 = getEvtEnqueueActionStateWithParam(tcpu, ad, task, sendevt, i);
						previous.addNext(actionp2);
						previous = actionp2;
					}
					
					if (showEvents) {
						actiong0 = getSendEvtActionStateWithGate(tcpu, cpu, ad, task, sendevt);
						previous.addNext(actiong0);
						actiong0.addNext(adj0);
					} else {
						previous.addNext(adj0);
					}
					
					// FIFO is full
					previous = choice1;
					choice1.addGuard(guard0);
					for(i=0; i<evt.getNbOfParams(); i++) {
						actionp2 = getEvtEnqueueDequeueActionStateWithParam(tcpu, ad, task, sendevt, i);
						previous.addNext(actionp2);
						previous = actionp2;
					}
					
					if (showEvents) {
						actiong0 = getSendEvtActionStateWithGate(tcpu, cpu, ad, task, sendevt);
						previous.addNext(actiong0);
						actiong0.addNext(adj0);
					} else {
						previous.addNext(adj0);
					}
					
					// Both cases
					actionp5 = getNextStateIdActionState(tcpu, ad, task);
					stateId ++;
					adj0.addNext(actionp5);
					
					actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
					//actionp6.addNext(endJunction);
					actionp5.addNext(actionp6);
					
					// New approach
					index = allcommunications.indexOf(evt);
					actionp7 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1) + "__tmp", "true");
					actionp6.addNext(actionp7);
					actionp7.addNext(endJunction);
					/*choice0 = new ADChoice();
					ad.add(choice0);
					actionp6.addNext(choice0);
					index = allcommunications.indexOf(evt);
					guard0 = "blockedOn__" + (index * 2 + 1) + " == 1";
					guard1 = "[not(" + guard0 + ")]";
					guard0 = "[" + guard0 + "]";
					
					adj0 = new ADJunction();
					ad.add(adj0);
					adj0.addNext(endJunction);
					
					choice0.addNext(adj0);
					choice0.addGuard(guard1);
					
					actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1), "0");
					actionp3 = getActionStateWithParam(tcpu, ad, evt.getDestinationTask().getName() + "__state", "0");
					actionp4 = getActionStateWithParam(tcpu, ad, cpu.getName() + "__selected", "0");
					
					choice0.addNext(actionp2);
					choice0.addGuard(guard0);
					actionp2.addNext(actionp3);
					actionp3.addNext(actionp4);
					
					actionp4.addNext(endJunction);*/
				}
			}
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
			// WAIT EVENT
		} else if (element instanceof TMLWaitEvent) {	
			evt = ((TMLWaitEvent)(element)).getEvent();
			
			choice0 = new ADChoice();
			ad.add(choice0);
			stateIdGuard(stateChoice, choice0, stateId, task);
			
			guard2 = "n__" + evt.getName() + "__tmp == 0";
			guard0 = "[" + guard2 + "]";
			guard1 = "[not(" + guard2 + ")]";
			
			// Blocked branch
			actionp0 = getStateActionStateWithParam(tcpu, ad, task, "2");
			choice0.addNext(actionp0);
			choice0.addGuard(guard0);
			index = allcommunications.indexOf(evt);
			actionp1 = getBlockedOnActionStateWithParam(tcpu, ad, (2 * index) + 1);
			actionp0.addNext(actionp1);
			actionp1.addNext(endJunction);
			
			// Get event branch
			actionp1 = getActionStateWithParam(tcpu, ad, "n__" + evt.getName(), "n__" + evt.getName() + " - 1");
			choice0.addNext(actionp1);
			choice0.addGuard(guard1);
			
			previous = actionp1;
			for(i=0; i<evt.getNbOfParams(); i++) {
				actionp2 = getEvtFirstActionStateWithParam(tcpu, ad, task, (TMLWaitEvent)(element), evt, i);
				actionp3 = getEvtDequeueActionStateWithParam(tcpu, ad, task, evt, i);
				previous.addNext(actionp2);
				actionp2.addNext(actionp3);
				previous = actionp3;
			}
			
			actionp5 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			//System.out.println("StateId=" + stateId +  "comp=" + element);
			if (showEvents) {
				actiong0 = getWaitEvtActionStateWithGate(tcpu, cpu, ad, task, (TMLWaitEvent)(element), evt);
				previous.addNext(actiong0);
				actiong0.addNext(actionp5);
			} else {
				previous.addNext(actionp5);
			}
			
			actionp6 = getNTickActionStateWithParam(tcpu, cpu, ad);
			//actionp6.addNext(endJunction);
			actionp5.addNext(actionp6);
			
			// Unblock the sender if necessary
			if (evt.isInfinite() || !evt.isBlocking()) {
				//System.out.println("Non blocking");
				actionp6.addNext(endJunction);
			} else {
				//System.out.println("Is blocking");
				// New approach
				index = allcommunications.indexOf(evt);
				actionp7 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2) + "__tmp", "true");
				actionp6.addNext(actionp7);
				actionp7.addNext(endJunction);
				/*choice0 = new ADChoice();
				ad.add(choice0);
				actionp6.addNext(choice0);
				
				index = allcommunications.indexOf(evt);
				guard0 = "blockedOn__" + (index * 2) + " == 1";
				guard1 = "[not(" + guard0 + ")]";
				guard0 = "[" + guard0 + "]";
				
				adj0 = new ADJunction();
				ad.add(adj0);
				adj0.addNext(endJunction);
				
				choice0.addNext(adj0);
				choice0.addGuard(guard1);
				
				actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2), "0");
				actionp3 = getActionStateWithParam(tcpu, ad, evt.getOriginTask().getName() + "__state", "0");
				actionp4 = getActionStateWithParam(tcpu, ad, cpu.getName() + "__selected", "0");
				
				choice0.addNext(actionp2);
				choice0.addGuard(guard0);
				actionp2.addNext(actionp3);
				actionp3.addNext(actionp4);
				
				actionp4.addNext(endJunction);*/
			}
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
			// NOTIFIED EVENT
		} else if (element instanceof TMLNotifiedEvent) {
			evt = ((TMLNotifiedEvent)element).getEvent();
			paramName = modifyString(((TMLNotifiedEvent)element).getVariable(), task);
			actionp1 = getActionStateWithParam(tcpu, ad, paramName, "n__" + evt.getName() + "__tmp");
			stateIdGuard(stateChoice, actionp1, stateId, task);
			
			actionp3 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			if (showEvents) {
				actiong0 = getNotifiedEvtActionStateWithGate(tcpu, cpu, ad, task, paramName, evt);
				actionp1.addNext(actiong0);
				actiong0.addNext(actionp3);
			} else {
				actionp1.addNext(actionp3);
			}
			
			actionp4 = getNTickActionStateWithParam(tcpu, cpu, ad);
			actionp3.addNext(actionp4);
			actionp4.addNext(endJunction);
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
			// SELECT EVENT
		} else if (element instanceof TMLSelectEvt) {
			tmlse = (TMLSelectEvt)element;
			choice0 = new ADChoice();
			ad.add(choice0);
			stateIdGuard(stateChoice, choice0, stateId, task);
			stateId ++;
			
			// Making main guard -> is one of the event available?
			guard0 = "";
			for(i=0; i<element.getNbNext(); i++) {
				if (i!=0) {
					guard0 += " and ";
				}
				guard0 += "(n__" + tmlse.getEvent(i).getName() + "__tmp == 0)";
			}
			guard1 = "[not(" + guard0 + ")]";
			guard0 = "[" + guard0 + "]";
			
			// No event
			choice0.addGuard(guard0);
			previous = choice0;
			for(i=0; i<element.getNbNext(); i++) {
				index = allcommunications.indexOf(tmlse.getEvent(i));
				actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1), "true");
				previous.addNext(actionp2);
				previous = actionp2;
			}
			actionp3 = getActionStateWithParam(tcpu, ad, task.getName() + "__state", "2");
			previous.addNext(actionp3);
			actionp4 = getActionStateWithParam(tcpu, ad, cpu.getName() + "__selected", "0");
			actionp3.addNext(actionp4);
			actionp4.addNext(endJunction);
			
			// At least one event
			//choice0.addGuard(guard1);
			/*previous = choice0;
			for(i=0; i<element.getNbNext(); i++) {
				index = allcommunications.indexOf(tmlse.getEvent(i));
				actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1), "0");
				previous.addNext(actionp2);
				previous = actionp2;
			}*/
			choice1 = new ADChoice();
			ad.add(choice1);
			//previous.addNext(choice1);
			choice0.addNext(choice1);
			choice0.addGuard(guard1);
			for(i=0; i<tmlse.getNbNext(); i++) {
				index = allcommunications.indexOf(tmlse.getEvent(i));
				choice1.addGuard("[ n__" + tmlse.getEvent(i).getName() + "__tmp > 0]");
				actionp1 = getStateIdActionState(tcpu, ad, task, stateId);
				actionp2 = getActionStateWithParam(tcpu, ad, "blockedOn__" + (index * 2 + 1), "false");
				//actionp1.addNext(endJunction);
				actionp1.addNext(actionp2);
				actionp2.addNext(endJunction);
				choice1.addNext(actionp1);
				stateId = makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(i));
			}
			
			// RANDOM
		} else if (element instanceof TMLRandom) {	
			
			tmlrandom = (TMLRandom)element;
				
			param0 = tcpu.addNewParamIfApplicable("min__random", Param.NAT, "0");
			param1 = tcpu.addNewParamIfApplicable("max__random", Param.NAT, "0");
			param2 = tcpu.addNewParamIfApplicable(task.getName() + "__" + tmlrandom.getVariable(), Param.NAT, "0");
			
			actionp0 = getActionStateWithParam(tcpu, ad, param0.getName(), modifyString("min(" + tmlrandom.getMinValue() + ", " + tmlrandom.getMaxValue() + ")", task));
			stateIdGuard(stateChoice, actionp0, stateId, task);
			
			actionp1 = getActionStateWithParam(tcpu, ad, param1.getName(), modifyString("max(" + tmlrandom.getMinValue() + ", " + tmlrandom.getMaxValue() + ")", task));
			actionp0.addNext(actionp1);
			
			actionp3 = getActionStateWithParam(tcpu, ad, param2.getName(), modifyString("min__random", null));
			actionp4 = getActionStateWithParam(tcpu, ad, param0.getName(), modifyString("min__random + 1", null));
			
			actionp5 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			
			adj0 = new ADJunction();
			ad.add(adj0);
			actionp1.addNext(adj0);
			
			choice0 = new ADChoice();
			ad.add(choice0);
			adj0.addNext(choice0);
			
			choice0.addGuard("[min__random < (max__random + 1)]");
			choice0.addNext(actionp3);
			actionp3.addNext(actionp5);
			actionp5.addNext(endJunction);
			
			choice0.addGuard("[min__random < max__random]");
			choice0.addNext(actionp4);
			actionp4.addNext(adj0);
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
			//EXECI
		} else if (element instanceof TMLExecI) {
			paramName = task.getName() + "__execinb";
			gateName = cpu.getName() + "__" + task.getName() + "__EXECI";
			
			tcpu.addNewParamIfApplicable(paramName, Param.NAT, "0");
			if (showExecs) {
				tcpu.addNewGateIfApplicable(gateName);
			}
			
			// Init
			actionp1 = getActionStateWithParam(tcpu, ad, paramName, modifyString(((TMLExecI)(element)).getAction() + " * (" + cpu.getExeciTime() + ")", task));
			stateIdGuard(stateChoice, actionp1, stateId, task);
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			actionp1.addNext(actionp2);
			actionp2.addNext(endJunction);
			
			// ExecI
			choice0 = new ADChoice();
			ad.add(choice0);
			stateIdGuard(stateChoice, choice0, stateId, task);
			
			choice0.addGuard("[ " + paramName + " == 0]");
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			choice0.addNext(actionp2);
			actionp2.addNext(endJunction);
			
			choice0.addGuard("[ not(" + paramName + " == 0)]");
			actionp1 = getActionStateWithParam(tcpu, ad, paramName, paramName + " - 1");
			actionp2 = getNTickActionStateWithParam(tcpu, cpu, ad);
			
			if (showExecs) {
				actiong0 = getActionGate(tcpu, ad, gateName, "");
				actiong0.addNext(actionp2);
				actionp1.addNext(actiong0);
			} else {
				actionp1.addNext(actionp2);
			}
			
			choice0.addNext(actionp1);
			actionp2.addNext(endJunction);
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
		
		} else if (element instanceof TMLExecC) {
			System.out.println("Execc" + cpu.getExeccTime());
			paramName = task.getName() + "__execcnb";
			gateName = cpu.getName() + "__" + task.getName() + "__EXECC";
			
			tcpu.addNewParamIfApplicable(paramName, Param.NAT, "0");
			if (showExecs) {
				tcpu.addNewGateIfApplicable(gateName);
			}
			
			// Init
			actionp1 = getActionStateWithParam(tcpu, ad, paramName, modifyString(((TMLExecC)(element)).getAction()+ " * (" + cpu.getExeccTime() + ")", task));
			stateIdGuard(stateChoice, actionp1, stateId, task);
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			actionp1.addNext(actionp2);
			actionp2.addNext(endJunction);
			
			// ExecI
			choice0 = new ADChoice();
			ad.add(choice0);
			stateIdGuard(stateChoice, choice0, stateId, task);
			
			choice0.addGuard("[ " + paramName + " == 0]");
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			choice0.addNext(actionp2);
			actionp2.addNext(endJunction);
			
			choice0.addGuard("[ not(" + paramName + " == 0)]");
			actionp1 = getActionStateWithParam(tcpu, ad, paramName, paramName + " - 1");
			actionp2 = getNTickActionStateWithParam(tcpu, cpu, ad);
			
			if (showExecs) {
				actiong0 = getActionGate(tcpu, ad, gateName, "");
				actiong0.addNext(actionp2);
				actionp1.addNext(actiong0);
			} else {
				actionp1.addNext(actionp2);
			}
			
			choice0.addNext(actionp1);
			actionp2.addNext(endJunction);
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
		// EXECI Interval
		} else if (element instanceof TMLExecIInterval) {
			//System.out.println("Execi interval");
			paramName = task.getName() + "__execinb";
			gateName = cpu.getName() + "__" + task.getName() + "__EXECI";
			
			tcpu.addNewParamIfApplicable(paramName, Param.NAT, "0");
			if (showExecs) {
				tcpu.addNewGateIfApplicable(gateName);
			}
			
			String smin = modifyString(((TMLExecIInterval)(element)).getMinDelay(), task);
			String smax = modifyString(((TMLExecIInterval)(element)).getMaxDelay(), task);
			
			// Init
			actionp1 = getActionStateWithParam(tcpu, ad, paramName, "(" + smax + ") * (" + cpu.getExeciTime() + ")");
			stateIdGuard(stateChoice, actionp1, stateId, task);
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			actionp1.addNext(actionp2);
			actionp2.addNext(endJunction);
			
			// ExecI
			choice0 = new ADChoice();
			ad.add(choice0);
			stateIdGuard(stateChoice, choice0, stateId, task);
			
			
			cpts = "(((" + cpu.getExeciTime() + ")*((" + smax  + ")-("  + smin + "))) + 1)";
			choice0.addGuard("[(" + paramName + " < " + cpts + ") and ( (" + paramName + " % " + cpu.getExeciTime() + ") == 0)]");
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			choice0.addNext(actionp2);
			actionp2.addNext(endJunction);
			
			choice0.addGuard("[" + paramName + " > 0]");
			actionp1 = getActionStateWithParam(tcpu, ad, paramName, paramName + " - 1");
			//actiong0 = getActionGate(tcpu, ad, gateName, "");
			actionp2 = getNTickActionStateWithParam(tcpu, cpu, ad);
			
			if (showExecs) {
				actiong0 = getActionGate(tcpu, ad, gateName, "");
				actiong0.addNext(actionp2);
				actionp1.addNext(actiong0);
			} else {
				actionp1.addNext(actionp2);
			}
			
			choice0.addNext(actionp1);
			actionp2.addNext(endJunction);
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
		} else if (element instanceof TMLExecCInterval) {
			paramName = task.getName() + "__execinb";
			gateName = cpu.getName() + "__" + task.getName() + "__EXECC";
			
			tcpu.addNewParamIfApplicable(paramName, Param.NAT, "0");
			if (showExecs) {
				tcpu.addNewGateIfApplicable(gateName);
			}
			
			String smin = modifyString(((TMLExecCInterval)(element)).getMinDelay(), task);
			String smax = modifyString(((TMLExecCInterval)(element)).getMaxDelay(), task);
			
			// Init
			actionp1 = getActionStateWithParam(tcpu, ad, paramName, "(" + smax + ") * (" + cpu.getExeccTime() + ")");
			stateIdGuard(stateChoice, actionp1, stateId, task);
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			actionp1.addNext(actionp2);
			actionp2.addNext(endJunction);
			
			// ExecI
			choice0 = new ADChoice();
			ad.add(choice0);
			stateIdGuard(stateChoice, choice0, stateId, task);
			
			
			cpts = "(((" + cpu.getExeccTime() + ")*((" + smax  + ")-("  + smin + "))) + 1)";
			choice0.addGuard("[(" + paramName + " < " + cpts + ") and ( (" + paramName + " % " + cpu.getExeccTime() + ") == 0)]");
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			choice0.addNext(actionp2);
			actionp2.addNext(endJunction);
			
			choice0.addGuard("[" + paramName + " > 0]");
			actionp1 = getActionStateWithParam(tcpu, ad, paramName, paramName + " - 1");
			//actiong0 = getActionGate(tcpu, ad, gateName, "");
			actionp2 = getNTickActionStateWithParam(tcpu, cpu, ad);
			
			if (showExecs) {
				actiong0 = getActionGate(tcpu, ad, gateName, "");
				actiong0.addNext(actionp2);
				actionp1.addNext(actiong0);
			} else {
				actionp1.addNext(actionp2);
			}
			
			choice0.addNext(actionp1);
			actionp2.addNext(endJunction);
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
		} else if (element instanceof TMLForLoop) {
			
			tmlforloop = (TMLForLoop)element;
			
			action1 = modifyString(tmlforloop.getInit(), task);
			action2 = modifyString(tmlforloop.getCondition(), task);
			action3 = modifyString(tmlforloop.getIncrement(), task);
			
			if (action2.length() == 0) {
				action2 = "true";
			}
			
			if ((action1.length() == 0) || (action3.length() == 0)) {
				system.addNewParamIfApplicable("loop__", "nat", "0");
			}
			
			if (action1.length() == 0) {
				action1 = "loop__ = 0";
			}
			
			if (action3.length() == 0) {
				action3 = "loop__ = 0";
			}
			
			
			if (((param1 = paramAnalyzer(action1, tcpu)) == null)) {
				TraceManager.addDev("Param Error");
				CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Parameter undeclared in loop " + action1);
				error.setTClass(tcpu);
				error.setTGComponent((TGComponent)(element.getReferenceObject()));
				checkingErrors.add(error);
				return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			}
			
			if (((param3 = paramAnalyzer(action3, tcpu)) == null)) {
				CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Parameter undeclared in loop" + action3);
				error.setTClass(tcpu);
				error.setTGComponent((TGComponent)(element.getReferenceObject()));
				checkingErrors.add(error);
				return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			}
			
			
			// Init
				actionp1 = getActionStateWithParam(tcpu, ad, param1.getName(), getActionValueParam(action1, tcpu));
				stateIdGuard(stateChoice, actionp1, stateId, task);
				actionp2 = getStateIdActionState(tcpu, ad, task, stateId+3);
				actionp1.addNext(actionp2);
				actionp2.addNext(endJunction);
				stateId ++;
			
			// We keep a state for the end of the loop
			index = stateId;
			stateId ++;
			
			actionp5 = getNextStateIdActionState(tcpu, ad, task);
			stateIdGuard(stateChoice, actionp5, stateId, task);
			actionp6 = getActionStateWithParam(tcpu, ad, param3.getName(), getActionValueParam(action3, tcpu));
			actionp5.addNext(actionp6);
			actionp6.addNext(endJunction);
			stateId ++;
		
			
			// Test condition of the loop
			choice0 = new ADChoice();
			ad.add(choice0);
			stateIdGuard(stateChoice, choice0, stateId, task);
			choice0.addGuard("[ " + action2 + "]");
			actionp3 = getNextStateIdActionState(tcpu, ad, task);
			stateId ++;
			choice0.addNext(actionp3);
			actionp3.addNext(endJunction);
			choice0.addGuard("[ not(" + action2 + ")]");
			actionp4 = getStateIdActionState(tcpu, ad, task, index);
			choice0.addNext(actionp4);
			actionp4.addNext(endJunction);
			
			// Translate the inner of the loop
			//System.out.println("Loop branch to " + index);
			stateId = makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, index+1, element.getNextElement(0));
			//System.out.println("StateId = " + stateId);
			
			actionp7 = getStateIdActionState(tcpu, ad, task, stateId);
			stateIdGuard(stateChoice, actionp7, index, task);
			actionp7.addNext(endJunction);
			
			// Translate the following of the loop
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(1));
			
			// SEQUENCE
		} else if (element instanceof TMLSequence) {
			
			if (element.getNbNext() < 2) {
				return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			} else {
				// At least two elements
				// Reserved as many states are required to manage next elements
				
				for(i=1; i<element.getNbNext(); i++) {
					actionp1 = getStateIdActionState(tcpu, ad, task, stateId+2);
					stateIdGuard(stateChoice, actionp1, stateId, task);
					actionp1.addNext(endJunction);
					stateId ++;
					
					index = stateId;
					stateId ++;
					
					stateId = makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, index, element.getNextElement(i-1));
					
					//System.out.println("Ending sequence with index=" + index +  " stateId=" + stateId);
					actionp2 = getStateIdActionState(tcpu, ad, task, stateId);
					stateIdGuard(stateChoice, actionp2, index, task);
					actionp2.addNext(endJunction);
				}
				//System.out.println("Ending loop");
				return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(element.getNbNext()-1));
				
			}
			
			// CHOICE
		} else if (element instanceof TMLChoice) {
			tmlchoice = (TMLChoice)element;
			if (tmlchoice.getNbGuard() !=0 ) {
				int index1 = tmlchoice.getElseGuard(), index2 = tmlchoice.getAfterGuard();
				if (index2 != -1) {
					index = stateId;
					
					stateId ++;
					actionp1 = getStateIdActionState(tcpu, ad, task, stateId+2);
					stateIdGuard(stateChoice, actionp1, stateId, task);
					actionp1.addNext(endJunction);
					
					stateId ++;
					// Translating the after guard
					stateId = makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(index2));
					
					branchStateId = index + 1;
					
					actionp1 = getStateIdActionState(tcpu, ad, task, stateId);
					stateIdGuard(stateChoice, actionp1, index, task);
					actionp1.addNext(endJunction);
				}
				
				//System.out.println("Branch state Id = " + stateId);
				
				// Regular translation with no care with after guard
				choice0 = new ADChoice();
				ad.add(choice0);
				stateIdGuard(stateChoice, choice0, stateId, task);
				stateId ++;
				for(i=0; i<tmlchoice.getNbGuard(); i++) {
					if (i != index2) {
						if (i==index1) {
							/* else guard */
							guard0 = modifyString(tmlchoice.getValueOfElse(), task);
							//System.out.println("modified else guard=" + guard0);
						} else {
							if (tmlchoice.isStochasticGuard(i)) {
								guard0 = "[ ]";
							} else {
								//System.out.println("guard=" + tmlchoice.getGuard(i));
								guard0 = modifyString(tmlchoice.getGuard(i), task);
								//System.out.println("modified guard=" + guard0);
							}
						}
						actionp1 = getStateIdActionState(tcpu, ad, task, stateId);
						actionp1.addNext(endJunction);
						choice0.addGuard(guard0);
						choice0.addNext(actionp1);
						//System.out.println("StateId before call = " + stateId);
						stateId = makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(i));
						//System.out.println("StateId after call = " + stateId);
					}
				}
			}
			
			// ACTION STATE
		} else if (element instanceof TMLActionState) {
			action1 = ((TMLActionState)(element)).getAction();
			action1 = modifyString(action1, task);			
			
			if ((param1 = paramAnalyzer(action1, tcpu)) == null) {
				CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Parameter undeclared in " + action1);
				error.setTClass(tcpu);
				checkingErrors.add(error);
				return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			}
			
			actionp1 = getActionStateWithParam(tcpu, ad, param1.getName(), getActionValueParam(action1, tcpu));
			stateIdGuard(stateChoice, actionp1, stateId, task);
			actionp2 = getNextStateIdActionState(tcpu, ad, task);
			actionp1.addNext(actionp2);
			actionp2.addNext(endJunction);
			stateId ++;
			
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
			
			// UNKNOWN
		} else {
			// Unknown element: skipped
			System.out.println("UNKNOWN ELEMENT: " + element);
			return makeCPUADTaskBehaviorComponent(tcpu, cpu, ad, stateChoice, endJunction, task, cpt, tasks, stateId, branchStateId, element.getNextElement(0));
		}
		
		//System.out.println("element = " + element + " / returned StateId = " + stateId);
		return stateId;
	}
	
	/*private int stateIdPlusPlus(int stateId, TMLActivityElement element, TMLTask task) {
		System.out.println("task=" + task.getName() + " stateid=" + stateId + " elt=" + element);
		return stateId++;
	}*/
	
	private ADActionStateWithGate getActionGate(TClass tcpu, ActivityDiagram ad, String name, String action) {
		Gate g = tcpu.getGateByName(name);
		ADActionStateWithGate adag = new ADActionStateWithGate(g);
		ad.add(adag);
		adag.setActionValue(action);
		return adag;
	}
	
	
	private ADActionStateWithGate getWaitReqActionStateWithGate(TClass tcpu, HwCPU cpu, ActivityDiagram ad, TMLTask task, TMLRequest req) {
		Gate g = tcpu.getGateByName(cpu.getName() + "__" + task.getName() + "__waitReq__" + req.getName());
		ADActionStateWithGate adag = new ADActionStateWithGate(g);
		String actionValue = "";
		for(int i=0; i<req.getNbOfParams(); i++) {
			actionValue += "!" + modifyString("arg" + (i+1) + "__" + req.getName(), task);
		}
		adag.setActionValue(actionValue);
		ad.add(adag);
		return adag;
	}
	
	private ADActionStateWithGate getWaitEvtActionStateWithGate(TClass tcpu, HwCPU cpu, ActivityDiagram ad, TMLTask task, TMLWaitEvent waitEvt, TMLEvent evt) {
		Gate g = tcpu.getGateByName(cpu.getName() + "__" + task.getName() + "__wait__" + evt.getName());
		ADActionStateWithGate adag = new ADActionStateWithGate(g);
		String actionValue = "";
		for(int i=0; i<evt.getNbOfParams(); i++) {
			actionValue += "!" + modifyString(waitEvt.getParam(i), task);
		}
		//actionValue += "!n__" + evt.getName();
		adag.setActionValue(actionValue);
		ad.add(adag);
		return adag;
	}
	
	private ADActionStateWithGate getNotifiedEvtActionStateWithGate(TClass tcpu, HwCPU cpu, ActivityDiagram ad, TMLTask task, String variable, TMLEvent evt) {
		Gate g = tcpu.getGateByName(cpu.getName() + "__" + task.getName() + "__notified__" + evt.getName());
		ADActionStateWithGate adag = new ADActionStateWithGate(g);
		adag.setActionValue("!" + variable);
		ad.add(adag);
		return adag;
	}
	
	
	private ADActionStateWithGate getSendEvtActionStateWithGate(TClass tcpu, HwCPU cpu, ActivityDiagram ad, TMLTask task, TMLSendEvent sendevt) {
		Gate g = tcpu.getGateByName(cpu.getName() + "__" + task.getName() + "__notify__" + sendevt.getEvent().getName());
		ADActionStateWithGate adag = new ADActionStateWithGate(g);
		String actionValue = "";
		String tmp = "";
		for(int i=0; i<sendevt.getEvent().getNbOfParams(); i++) {
			tmp = sendevt.getParam(i);
			tmp = modifyString(tmp, task);
			actionValue += "!" + tmp;
		}
		adag.setActionValue(actionValue);
		ad.add(adag);
		return adag;
	}
	
	private ADActionStateWithGate getSendReqActionStateWithGate(TClass tcpu, HwCPU cpu, ActivityDiagram ad, TMLTask task, TMLSendRequest sendreq) {
		Gate g = tcpu.getGateByName(cpu.getName() + "__" + task.getName() + "__sendReq__" + sendreq.getRequest().getName());
		ADActionStateWithGate adag = new ADActionStateWithGate(g);
		String actionValue = "";
		String tmp = "";
		for(int i=0; i<sendreq.getRequest().getNbOfParams(); i++) {
			tmp = sendreq.getParam(i);
			tmp = modifyString(tmp, task);
			actionValue += "!" + tmp;
		}
		adag.setActionValue(actionValue);
		ad.add(adag);
		return adag;
	}
	
	private ADActionStateWithParam getNTickActionStateWithParam(TClass tcpu, HwCPU cpu, ActivityDiagram ad) {
		Param p = tcpu.getParamByName(cpu.getName() + "__nTick");
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue("1");
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getNextStateIdActionState(TClass tcpu, ActivityDiagram ad, TMLTask task) {
		Param p = tcpu.getParamByName(task.getName() + "__istate");
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue(task.getName() + "__istate + 1");
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getInitialStateIdActionState(TClass tcpu, ActivityDiagram ad, TMLTask task) {
		Param p = tcpu.getParamByName(task.getName() + "__istate");
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue("0");
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getStateIdActionState(TClass tcpu, ActivityDiagram ad, TMLTask task, int id) {
		Param p = tcpu.getParamByName(task.getName() + "__istate");
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue(""+id);
		ad.add(actionp);
		return actionp;
	}
	
	
	private ADActionStateWithParam getActionStateWithParam(TClass tcpu, ActivityDiagram ad, String param, String action) {
		Param p = tcpu.getParamByName(param);
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue(action);
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getReqFirstActionStateWithParam(TClass tcpu, ActivityDiagram ad, TMLTask task, TMLRequest req, int index) {
		Param p = tcpu.getParamByName(modifyString("arg" + index + "__req", task));
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue("First(fifo" + index + "__" + req.getName() + ")");
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getReqDequeueActionStateWithParam(TClass tcpu, ActivityDiagram ad, TMLTask task, TMLRequest req, int index) {
		Param p = tcpu.getParamByName("fifo" + index + "__" + req.getName());
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue("Dequeue(fifo" + index + "__" + req.getName() + ")");
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getEvtFirstActionStateWithParam(TClass tcpu, ActivityDiagram ad, TMLTask task, TMLWaitEvent waitEvt, TMLEvent evt, int index) {
		//System.out.println("Searched param = " + modifyString(waitEvt.getParam(index), task)+ " index = " + index + " nbOfParams=" + waitEvt.getNbOfParams() + " allparams=" + waitEvt.getAllParams());
		Param p = tcpu.getParamByName(modifyString(waitEvt.getParam(index), task));
		if (p == null) {
			System.out.println("************ NULL Param *******");
		}
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue("First(fifo" + index + "__" + evt.getName() + ")");
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getEvtDequeueActionStateWithParam(TClass tcpu, ActivityDiagram ad, TMLTask task, TMLEvent evt, int index) {
		Param p = tcpu.getParamByName("fifo" + index + "__" + evt.getName());
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue("Dequeue(fifo" + index + "__" + evt.getName() + ")");
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getReqEnqueueActionStateWithParam(TClass tcpu, ActivityDiagram ad, TMLTask task, TMLSendRequest sendreq, int index) {
		Param p = tcpu.getParamByName("fifo" + index + "__" + sendreq.getRequest().getName());
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue("Enqueue(" + modifyString(sendreq.getParam(index-1), task) + ", fifo" + index + "__" + sendreq.getRequest().getName() + ")");
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getEvtEnqueueActionStateWithParam(TClass tcpu, ActivityDiagram ad, TMLTask task, TMLSendEvent sendevt, int index) {
		Param p = tcpu.getParamByName("fifo" + index + "__" + sendevt.getEvent().getName());
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue("Enqueue(" + modifyString(sendevt.getParam(index), task) + ", fifo" + index + "__" + sendevt.getEvent().getName() + ")");
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getEvtEnqueueDequeueActionStateWithParam(TClass tcpu, ActivityDiagram ad, TMLTask task, TMLSendEvent sendevt, int index) {
		Param p = tcpu.getParamByName("fifo" + index + "__" + sendevt.getEvent().getName());
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue("Enqueue(" + modifyString(sendevt.getParam(index), task) + ", Dequeue(fifo" + index + "__" + sendevt.getEvent().getName() + "))");
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getStateActionStateWithParam(TClass tcpu, ActivityDiagram ad, TMLTask task, String action) {
		Param p = tcpu.getParamByName(task.getName() + "__state");
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue(action);
		ad.add(actionp);
		return actionp;
	}
	
	private ADActionStateWithParam getBlockedOnActionStateWithParam(TClass tcpu, ActivityDiagram ad, int index) {
		Param p = tcpu.getParamByName("blockedOn__" + index);
		ADActionStateWithParam actionp = new ADActionStateWithParam(p);
		actionp.setActionValue("true");
		ad.add(actionp);
		return actionp;
	}
	
	
	private void stateIdGuard(ADChoice previous, ADComponent next, int stateId, TMLTask task) {
		previous.addNext(next);
		previous.addGuard("[" + task.getName() + "__istate == " + stateId + "]");
	}
	
	private Param paramAnalyzer(String action, TClass tcl) {
		int index = action.indexOf("=");
		if (index < 0) {
			// ++ expression ?
			index = action.indexOf("++");
			if (index < 0) {
				// -- expression
				index = action.indexOf("--");
				if (index < 0) {
					return null;
				}
			}
		}
		
		action = action.substring(0, index);
		action = action.trim();
		
		return tcl.getParamByName(action);
	}
	
	private String getActionValueParam(String action, TClass tcl) {
		int index = action.indexOf("=");
		if (index < 0) {
			// ++ expression ?
			index = action.indexOf("++");
			if (index < 0) {
				// -- expression
				index = action.indexOf("--");
				if (index < 0) {
					return null;
				} else {
					action = action.substring(0, index);
					action = action.trim();
					return action + "-1";
				}
			} else {
				action = action.substring(0, index);
				action = action.trim();
				return action + "+1";
			}
		}
		
		return action = action.substring(index+1, action.length()).trim();
	}
	
	private boolean isMapped(TMLChannel _ch) {
		int index = allcommunications.indexOf(_ch);
		if (index == -1) {
			return false;
		}
		
		return (allbuses.get(index) != fakeBus);
	}
	
	private HwBus getBusOf(TMLChannel _ch) {
		int index = allcommunications.indexOf(_ch);
		if (index == -1) {
			return null;
		}
		return allbuses.get(index);
	}
	
	private String modifyString(String _input, TMLTask task) {
		_input = Conversion.replaceAllString(_input, "<<", "*");
		_input = Conversion.replaceAllString(_input, ">>", "/");
		
		// Replaces &&, || and !
		_input = Conversion.replaceAllString(_input,"&&", "and");
		_input = Conversion.replaceAllString(_input, "||", "or");
		_input = Conversion.replaceAllString(_input, "!", "not");
		_input = Conversion.replaceAllStringNonAlphanumerical(_input, "i", "i_0");
		
		if (task != null) {
			_input = TURTLEModeling.addPrefixInExpression(task.getName() + "__", _input);
		}
		
		return _input;
	}
}
