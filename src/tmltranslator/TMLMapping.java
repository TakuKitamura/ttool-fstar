/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class TMLMapping
 * Creation: 05/09/2007
 * @version 1.0 05/09/2007
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator;

import java.util.*;


public class TMLMapping {
    private TMLModeling tmlm;
    private TMLArchitecture tmla;
 
	private ArrayList<HwExecutionNode> onnodes;
	private ArrayList<TMLTask> mappedtasks;
	private ArrayList<HwCommunicationNode> oncommnodes;
	private ArrayList<TMLElement> mappedcommelts;
	
	private boolean optimized = false;
	
	private int hashCode;
	private boolean hashCodeComputed = false;
    
    public TMLMapping(TMLModeling _tmlm, TMLArchitecture _tmla, boolean reset) {
        tmlm = _tmlm;
		tmla = _tmla;
		init();
		
		if (reset) {
			DIPLOElement.resetID();
		}
    }
	
	public void makeMinimumMapping() {
		HwCPU cpu;
		HwMemory mem;
		HwBus bus;
		HwLink link0, link1;
		TMLTask t;
		TMLChannel ch;
		ListIterator iterator;
		int cpt;
		
		if (tmla == null) {
			tmla = new TMLArchitecture();
		}
		
		if (!tmla.hasCPU()) {
			cpu = new HwCPU("defaultCPU");
			cpu.byteDataSize = 4;
			cpu.pipelineSize = 1;
			cpu.goIdleTime = 0;
			cpu.taskSwitchingTime = 1;
			cpu.branchingPredictionPenalty = 0;
			cpu.execiTime = 1;
			tmla.addHwNode(cpu);
			
			// tasks
			iterator = tmlm.getTasks().listIterator();
			while(iterator.hasNext()) {
				t = (TMLTask)(iterator.next());
				addTaskToHwExecutionNode(t, cpu);
			}
		}
		
		// Is there a memory?
		if (!tmla.hasMemory()) {
			mem = new HwMemory("defaultMemory");
			tmla.addHwNode(mem);
			iterator = tmlm.getChannels().listIterator();
			
			while(iterator.hasNext()) {
				ch = (TMLChannel)(iterator.next());
				addCommToHwCommNode(ch, mem);
			}
		}
		
		if (!tmla.hasBus()) {
			bus = new HwBus("defaultBus");
			tmla.addHwNode(bus);
			// Connect all possible nodes to that bus
			cpt = 0;
			for(HwNode node: tmla.getHwNodes()) {
				link0 = new HwLink("to_bus_" + cpt);
				cpt ++;
				link0.bus = bus;
				link0.hwnode = node;
				tmla.addHwLink(link0);
			}
			
			// Add all channels on that bus
			iterator = tmlm.getChannels().listIterator();
			// All channels non-mapped on a memory are added to that memory
			while(iterator.hasNext()) {
				ch = (TMLChannel)(iterator.next());
				addCommToHwCommNode(ch, bus);
			}
		}
	}
	
	private void init() {
		mappedtasks = new ArrayList<TMLTask>();
		onnodes = new ArrayList<HwExecutionNode>();
		oncommnodes = new ArrayList<HwCommunicationNode>();
		mappedcommelts = new ArrayList<TMLElement>();
	}
	
	public void addTaskToHwExecutionNode(TMLTask _task, HwExecutionNode _hwnode) {
		onnodes.add(_hwnode);
		mappedtasks.add(_task);
	}
	
	public void addCommToHwCommNode(TMLElement _elt, HwCommunicationNode _hwcommnode) {
		oncommnodes.add(_hwcommnode);
		mappedcommelts.add(_elt);
	}
	
	public TMLModeling getTMLModeling() {
		return tmlm;
	}
	public TMLArchitecture getTMLArchitecture() {
		return tmla;
	}
	
	private void computeHashCode() {
		hashCode = tmlm.getHashCode() + tmla.getHashCode();
		TMLMappingTextSpecification tmaptxt = new TMLMappingTextSpecification("spec.tmap");
		hashCode += tmaptxt.toTextFormat(this).hashCode();
		System.out.println("TMAP hashcode = " + hashCode); 
	}
	
	public int getHashCode() {
		if (!hashCodeComputed) {
			computeHashCode();
			hashCodeComputed = true;
		}
		return hashCode;
	}
	
	public ArrayList<HwExecutionNode> getNodes(){
		return onnodes;
	}
	
	public ArrayList<TMLTask> getMappedTasks(){
		return mappedtasks;
	}
	
	public ArrayList<HwCommunicationNode> getCommunicationNodes(){
		return oncommnodes;
	}
	
	public ArrayList<TMLElement> getMappedCommunicationElement(){
		return mappedcommelts;
	}
	
	
	public TMLTask getTaskByName(String _name) {
		return tmlm.getTMLTaskByName(_name);
	}
	
	public HwExecutionNode getHwExecutionNodeByName(String _name) {
		HwNode node = tmla.getHwNodeByName(_name);
		if (node instanceof HwExecutionNode) {
			return (HwExecutionNode)(node);
		}
		return null;
	}
	
	public HwCommunicationNode getHwCommunicationNodeByName(String _name) {
		HwNode node = tmla.getHwNodeByName(_name);
		if (node instanceof HwCommunicationNode) {
			return (HwCommunicationNode)(node);
		}
		return null;
	}
	
	public TMLElement getCommunicationElementByName(String _name) {
		return tmlm.getCommunicationElementByName(_name);
	}
	
	public boolean isTaskMapped(TMLTask _task) {
		return (mappedtasks.contains(_task));
	}
	
	public HwNode getHwNodeOf(TMLTask _task) {
		int index = mappedtasks.indexOf(_task);
		if (index == -1) {
			return null;
		}
		
		return onnodes.get(index);
	}
	
	public boolean isAUsedHwNode(HwNode _node) {
		return (onnodes.contains(_node));
	}
	
	public boolean isTaskMappedOn(TMLTask _task, HwNode _node) {
		 for(int i=0; i<onnodes.size(); i++) {
			 if (onnodes.get(i) == _node) {
				 if (mappedtasks.get(i) == _task) {
					 return true;
				 }
			 }
		 }
		 return false;
	}

	/*public boolean isChannelMappedOn(TMLElement _channel, HwCommunicationNode _node) {
		 for(int i=0; i<oncommnodes.size(); i++) {
			 if (oncommnodes.get(i) == _node && mappedcommelts.get(i) == _channel) return true;
		 }
		 return false;
	}*/
	
	public boolean oneTaskMappedOn(TMLRequest _request, HwNode _node) {
		TMLTask task;
		ListIterator iterator = _request.getOriginTasks().listIterator();
		while(iterator.hasNext()) {
			task = (TMLTask)(iterator.next());
			if (isTaskMappedOn(task, _node)) {
				return true;
			}
		}
		return false;
	}
	
	public HwBus getFirstHwBusOf(TMLElement _tmle) {
		int index = mappedcommelts.indexOf(_tmle);
		if (index == -1) {
			return null;
		}
		
		index = 0;
		for(HwCommunicationNode node: oncommnodes) {
			if (node instanceof HwBus) {
				if (mappedcommelts.get(index) == _tmle) {
					return (HwBus)node;
				}
			}
			index ++;
		}
		
		return null;
		
	}
	
	public int getMaxClockRatio() {
		int ret = 1;
		for(HwCommunicationNode node: oncommnodes) {
			ret = Math.max(node.clockRatio, ret);
		}
		
		for(HwExecutionNode node1: onnodes) {
			ret = Math.max(node1.clockRatio, ret);
		}
		
		return ret;
	}
	
	public ArrayList<TMLError> optimize() {
		ArrayList<TMLError> list = new ArrayList<TMLError>();
		if (!optimized) {
			optimized = true;
			list.addAll(tmlm.optimize());
		}
		return list;
	}

	public LinkedList<HwCommunicationNode> findNodesForElement(TMLElement _elementToFind){
		LinkedList<HwCommunicationNode> list = new LinkedList<HwCommunicationNode>();
		int index=0;
		for(TMLElement tmlelem: mappedcommelts) {
			if (tmlelem == _elementToFind) list.add(oncommnodes.get(index));
			index++;
		}
		return list;
	}

  
  
}