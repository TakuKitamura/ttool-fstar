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
* Class VCDGenerator
* Creation : 13/07/2009
** @version 1.0 13/07/2009
* @author Ludovic APVRILLE
* @see
*/

package ui.graph;

import java.util.*;
import java.io.*;

import myutil.*;
import vcd.*;

public class VCDGenerator  {
    
	public final static int LOW = 0;
	public final static int LOW_TO_HIGH = 1;
	public final static int HIGH_TO_LOW = 2;
	public final static int HIGH = 3;
	public final static int NB_OF_MODES = 4;
	
    private AUTGraph graph;
	private long simulationTicks = 1000000; 
	private String tickInfo = "GOTS"; 
	private String coreInfo = "PRINTCORESTATES";
	private String taskInfo = "TASKINFO";
	private String info = "SYSTEMINFO";
	
	private int nbOfTasks;
	private int nbOfCores;
	private CorePowerConsumption pcs[];
	
	private long pcInMode[];
	
	
	private long currentTime;
	
	private VCDContent vcd;
	
	private boolean go;
	private String activity;
	

    
    public VCDGenerator(AUTGraph _graph) {
        graph = _graph;
		pcInMode = new long[NB_OF_MODES];
    }
    
    public int generateVCD() {
		go = true;
		vcd = new VCDContent();
		
		return simulate();
    }
	
	public void setPowerConsumptionInMode(int _mode, long _value) {
		if (_mode < NB_OF_MODES) {
			pcInMode[_mode] = _value;
		}
	}
	
	public void setSimulationTicks(long _st) {
		simulationTicks = _st;
	}
    
    public int getPercentage() {
    	return (int)(currentTime*100/simulationTicks);
    }
	
	public void setInfo(String _info) {
		String s = modify(_info);
		if (s.length() > 0) {
			info = s;
		}
	}
	
	public void setTickInfo(String _info) {
		String s = modify(_info);
		if (s.length() > 0) {
			tickInfo = s;
		}
	}   
	
	public void setCoreInfo(String _info) {
		String s = modify(_info);
		if (s.length() > 0) {
			coreInfo = s;
		}
	}
	
	public void setTaskInfo(String _info) {
		String s = modify(_info);
		if (s.length() > 0) {
			taskInfo = s;
		}
	}
	
	
	
	
	
	private String modify(String _s) {
		String s = _s.trim().toUpperCase();
		return s;
	}
	
	public String getVCDString() {
		if (vcd != null) {
			return vcd.toString();
		} else {
			return "No vcd";
		}
	}
	
	public void saveInFile(String path, String fileName) throws FileException {
		FileUtils.saveFile(path + fileName, getVCDString());
	}
	
	public int simulate() {
		// Take a random path
		// Must locate system info first -> nb of tasks, nb of cores
		boolean deadlock = false;
		boolean infoFound = false;
		boolean cycle = false;
		AUTState currentState;
		AUTTransition tr;
		String label;
		int i;
		
		currentTime = 0;
		
		
		
		ArrayList<AUTState> met = new ArrayList<AUTState>();
		
		System.out.println("Computing states");
		activity = "Computing states";
		graph.computeStates();
		
		currentState = graph.findFirstOriginState();
		
		System.out.println("Searches for info");
		activity = "Searches for system info on graph";
		while((!cycle) && (!deadlock) && (!infoFound) && (go)) {
			met.add(currentState);
			tr = currentState.returnRandomTransition();
			if (tr == null) {
				deadlock = true;
			} else {
				label = tr.getLabel();
				if (label.toUpperCase().equals(info)) {
					//System.out.println("[info search] [state = " + currentState.id + "] currentStateFound label = " + label + " int param=" + tr.getNbOfIntParameters());
					if (tr.getNbOfIntParameters() == 2) {
						nbOfTasks = tr.getIntParameter(0);
						nbOfCores = tr.getIntParameter(1);
						initCorePowerConsumption();
						infoFound = true;
					}
				}
				currentState = graph.getState(tr.destination);
				if (met.contains(currentState)) {
					cycle = true;
				}
			}
		}
		
		if (!go) {
			return -3;
		}
		
		if (deadlock) {
			System.out.println("Deadlock");
			return -1;
		}
		
		if (cycle) {
			System.out.println("Cycle");
			return -2;
		}
		
		// Add variables
		activity = "Creating VCD variables";
		VCDVariable var;
		for(i=0; i<nbOfTasks; i++) {
			var = new VCDVariable("Task" + i);
			var.setBitwidth(2);
			vcd.addVariable(var);
			var = new VCDVariable("Task" + i + "Running");
			var.setBitwidth(1);
			vcd.addVariable(var);
		}
		for(i=0; i<nbOfCores; i++) {
			var = new VCDVariable("Core" + i);
			var.setBitwidth(2);
			vcd.addVariable(var);
			var = new VCDVariable("Core" + i+ "High");
			var.setBitwidth(1);
			vcd.addVariable(var);
		}
		
		// Now simulate the graph ...
		System.out.println("Simulate the graph tasks:" + nbOfTasks + " cores:" + nbOfCores);
		currentTime = 0;
		VCDTimeChange currentTC;
		int time;
		currentTC = new VCDTimeChange("" + currentTime);
		vcd.addTimeChange(currentTC);
		String s;
		int par, par0;
		long nbOfStates = 0;
		long oldCurrentTime;
		
		while((!cycle) && (currentTime<simulationTicks) && (go)) {
			tr = currentState.returnRandomTransition();
			if (tr == null) {
				deadlock = true;
			} else {
				// new tick?
				label = tr.getLabel();
				
				// New tick
				if (label.toUpperCase().equals(tickInfo)) {
					time = tr.getIntParameter(0);
					if (time != 0) {
						oldCurrentTime = currentTime;
						currentTime += time;
						// Verify if all tasks info have been put on previous time
						/*for(i=0; i<nbOfTasks; i++) {
							var = vcd.getVariableByName("Task" + i);
							if (var != null) {
								if (!currentTC.hasValueChangeOnVariable(var)) {
									currentTC.addVariable(var, "0");
								}
							}
						}*/
						computePowerConsumption(currentTC, oldCurrentTime, currentTime);
						currentTC = new VCDTimeChange("" + currentTime);
						vcd.addTimeChange(currentTC);
						activity = "Simulation: Current time=" + currentTime + "   Nb of analyzed transitions=" + nbOfStates;
						//System.out.println("CurrentTime " + currentTime + " nbOfStates: " + nbOfStates);
					}
				
				// Info on cores
				} else if (label.toUpperCase().equals(coreInfo)) {
					for(i=0; i<nbOfCores; i++) {
						par = tr.getIntParameter(i);
						var = vcd.getVariableByName("Core" + i);
						if (var != null) {
							s = "" + par;
							if (par == 2) {
								s = "10";
							} else if (par == 3) {
								s = "11";
							}
							currentTC.addVariable(var, s);
							var = vcd.getVariableByName("Core" + i + "High");
							if (var != null) {
								if (par == 3) {
									currentTC.addVariable(var, "1");
								} else {
									currentTC.addVariable(var, "0");
								}
							}
						}
					}
					
				// Info on tasks	
				} else if (label.toUpperCase().equals(taskInfo)) {
					par0 = tr.getIntParameter(0);
					var = vcd.getVariableByName("Task" + par0);
					if (var != null) {
						par = tr.getIntParameter(1);
						s = "" + par;
						if (par == 2) {
							s = "10";
						}
						currentTC.addVariable(var, s);
						var = vcd.getVariableByName("Task" + par0 + "Running");
						if (var != null) {
							if (par == 2) {
								currentTC.addVariable(var, "1");
							} else {
								currentTC.addVariable(var, "0");
							}
						}
					}
				}
				
				currentState = graph.getState(tr.destination);
				nbOfStates ++;
			}
		}
		
		if (!go) {
			return -3;
		}
		
		activity = "All done";
		
		return 0;
	}
	
	public boolean hasBeenStopped() {
		return (go == false);
	}
	
	public String getCurrentActivity() {
		return activity;
	}
	
	public void stop() {
		go = false;
	}
	
	public int getNbOfCores() {
		return nbOfCores;
	}
	
	public long getPowerConsumptionOfCore(int _index) {
		if ((_index < nbOfCores) && (pcs != null)) {
			return pcs[_index].computePowerConsumption();
		}
		return 0;
	}
	
	public void initCorePowerConsumption() {
		pcs = new CorePowerConsumption[nbOfCores];
		for (int i=0; i<nbOfCores; i++) {
			pcs[i] = new CorePowerConsumption(NB_OF_MODES);
			pcs[i].setPowerConsumptionInMode(pcInMode[LOW], LOW);
			pcs[i].setPowerConsumptionInMode(pcInMode[LOW_TO_HIGH], LOW_TO_HIGH);
			pcs[i].setPowerConsumptionInMode(pcInMode[HIGH_TO_LOW], HIGH_TO_LOW);
			pcs[i].setPowerConsumptionInMode(pcInMode[HIGH], HIGH);
		}
	}
	
	public void computePowerConsumption(VCDTimeChange tc, long oldTime, long newTime) {
		VCDVariable var;
		String value;
		int val, index;
		String varName;
		
		for(int i=0; i<tc.getNbOfVariables(); i++) {
			var = tc.getVariable(i);
			varName = var.getName();
			if (varName.startsWith("Core")) {
				if (!varName.endsWith("High")) {
					value = tc.getValue(i);
					try {
						if (value.equals("0")) {
							val = 0;
						} else if (value.equals("1")) {
							val = 1;
						} else if (value.equals("10")) {
							val = 2;
						} else {
							val = 3;
						}
						//val = Integer.parseInt(value);
						index = Integer.parseInt(varName.substring(4, varName.length()));
						// Must be from LOW to HIGH
						if (val < NB_OF_MODES) {
							pcs[index].addPowerConsumption(val, newTime - oldTime);
							//System.out.println("Adding power consumption to core #" + index + " mode = " + val + " value = " + (newTime - oldTime)); 
						}
					} catch (NumberFormatException nfe) {
					}
				}
			}
		}
		
	}
    
 
}