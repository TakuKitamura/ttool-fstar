/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
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




package tmltranslator;

import tmltranslator.modelcompiler.ArchUnitMEC;
import tmltranslator.modelcompiler.CpuMEC;


/**
 * Class HwCPU
 * Creation: 05/09/2007
 * @version 1.0 05/09/2007
 * @author Ludovic APVRILLE
 */
public class HwCPU extends HwExecutionNode  {
	
	public static final int BASIC_ROUND_ROBIN = 0;
	public static final int ROUND_ROBIN_PRIORITY_BASED = 1;
	public static final int ENCRYPTION_NONE= 0;
	public static final int ENCRYPTION_SW= 1;
	public static final int ENCRYPTION_HW= 2;
	public static final int DEFAULT_NB_OF_CORES = 1;
	public static final int DEFAULT_BYTE_DATA_SIZE = 4;
	public static final int DEFAULT_PIPELINE_SIZE = 5;
	public static final int DEFAULT_GO_IDLE_TIME = 10;
	public static final int DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES = 10;
	public static final int DEFAULT_TASK_SWITCHING_TIME = 20;
	public static final int DEFAULT_BRANCHING_PREDICTION_PENALTY = 2;
	public static final int DEFAULT_CACHE_MISS = 5;
	public static final int DEFAULT_SCHEDULING = BASIC_ROUND_ROBIN;
	public static final int DEFAULT_SLICE_TIME = 10000; // in microseconds
	public static final ArchUnitMEC DEFAULT_MODEL_EXTENSION_CONSTRUCT = new CpuMEC();
	public int encryption  = 0;
	public int nbOfCores = DEFAULT_NB_OF_CORES; // Should be equal or greater than 1
	public int byteDataSize = DEFAULT_BYTE_DATA_SIZE; // Should be greater than 0
	public int pipelineSize = DEFAULT_PIPELINE_SIZE; // Should be greater than 0
	public int goIdleTime = DEFAULT_GO_IDLE_TIME; // Should be greater or equal to 0
	public int maxConsecutiveIdleCycles = DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES; // Should be greater or equal to 0
	public int taskSwitchingTime = DEFAULT_TASK_SWITCHING_TIME; // Should be greater or equal to 0
	public int branchingPredictionPenalty = DEFAULT_BRANCHING_PREDICTION_PENALTY; // Percentage: between 0 and 100
	public int cacheMiss = DEFAULT_CACHE_MISS; // Percentage: between 0 and 100
	public int schedulingPolicy = DEFAULT_SCHEDULING;
	public int sliceTime = DEFAULT_SLICE_TIME;
	public ArchUnitMEC MEC = DEFAULT_MODEL_EXTENSION_CONSTRUCT;
	
    public HwCPU(String _name) {
		super(_name);
		maximumNbOfTasks = 100;
    }
	
	public String getType() {
		switch(schedulingPolicy) {
		case ROUND_ROBIN_PRIORITY_BASED:
			return "CPURRPB";
		case BASIC_ROUND_ROBIN:
		default:
			return "CPURR";
		}
	}
 
}
