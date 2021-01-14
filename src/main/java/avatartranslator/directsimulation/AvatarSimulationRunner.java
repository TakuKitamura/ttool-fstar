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


package avatartranslator.directsimulation;

import avatartranslator.AvatarAttribute;
import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachineElement;
import myutil.DataElement;
import myutil.TraceManager;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Class AvatarSimulationRunner
 * Executor for multiple simulations
 * Creation: 08/01/2021
 *
 * @author Ludovic APVRILLE
 * @version 1.0 08/01/2021
 */

public class AvatarSimulationRunner {

   private static int MAX_NB_OF_TRANSACTIONS = 10000;

   private AvatarSpecification as;
   private boolean stop;
   private AvatarSpecificationSimulation ass;

   public ArrayList<AvatarSpecificationSimulation> listOfSimulations;

    public AvatarSimulationRunner(AvatarSpecification _as) {
       as = _as;
    }

    public synchronized void runXSimulation(int nbOfSimulations, AvatarSimulationRunnerListener listener) {
        stop = false;

        if (nbOfSimulations < 1) {
            return;
        }

        listOfSimulations = new ArrayList<>(nbOfSimulations);

        for (int simulationIndex=0; simulationIndex<nbOfSimulations; simulationIndex++) {
            //TraceManager.addDev("Simulation #" + simulationIndex);
            listener.setSimulationDone(simulationIndex);
            ass = new AvatarSpecificationSimulation(as, null);
            listOfSimulations.add(ass);
            ass.runSimulationToCompletion(MAX_NB_OF_TRANSACTIONS);
            if (stop) {
                break;
            }
        }
    }

    public synchronized long getMinSimulationTime() {
        if (listOfSimulations == null) {
            return -1;
        }
        long minSimTime = Long.MAX_VALUE;
        for(AvatarSpecificationSimulation ass: listOfSimulations) {
            minSimTime = Math.min(ass.getClockValue(), minSimTime);
        }
        return minSimTime;
    }

    public synchronized long getMaxSimulationTime() {
        if (listOfSimulations == null) {
            return -1;
        }
        long maxSimTime = -1;
        for(AvatarSpecificationSimulation ass: listOfSimulations) {
            maxSimTime = Math.max(ass.getClockValue(), maxSimTime);
        }
        return maxSimTime;
    }

    public synchronized double getAverageSimulationTime() {
        if (listOfSimulations == null) {
            return -1;
        }
        double averageSimTime = 0;
        for(AvatarSpecificationSimulation ass: listOfSimulations) {
            averageSimTime += ass.getClockValue() / listOfSimulations.size();
        }
        return averageSimTime;
    }

    public synchronized double[] getSimulationTimes() {
        double[] values = new double[listOfSimulations.size()];
        for(int i=0; i< listOfSimulations.size(); i++) {
            values[i] = listOfSimulations.get(i).getClockValue();
        }
        return values;
    }

    public void stopSimulation() {
        stop = true;
        if (ass != null) {
            Thread t = new Thread(() -> ass.killSimulation());
            t.start();
        }
    }

    public List<AvatarBlock> getBlocksOfTransactions() {
        return as.getListOfBlocks();
    }

    public double[] getTimesOfLastTransactionOfBlock(AvatarBlock ab) {
        double [] timeLastTransaction = new double[listOfSimulations.size()];

        int i = 0;
        for(AvatarSpecificationSimulation ass: listOfSimulations) {
            timeLastTransaction[i] = (double)( ass.getTimeOfLastTransactionOfBlock(ab) );
            i++;
        }

        return timeLastTransaction;
    }

    public ArrayList<Double> getDataTimesOfAttributesOfBlock(AvatarBlock ab, AvatarAttribute aa, int indexOfAttribute) {
        ArrayList<Double> listOfDoubles = new ArrayList<>();

        for(AvatarSpecificationSimulation ass: listOfSimulations) {
            ass.fillValuesOfTimesOfBlockAttribute(ab, aa, indexOfAttribute, listOfDoubles);
        }

        return listOfDoubles;
    }

    public ArrayList<Double> getLastValueAndTimeOfAttributesOfBlock(AvatarBlock ab, AvatarAttribute aa, int indexOfAttribute) {
        ArrayList<Double> listOfDoubles = new ArrayList<>();

        for(AvatarSpecificationSimulation ass: listOfSimulations) {
            ass.fillLastValueAndTimeOfBlockAttribute(ab, aa, indexOfAttribute, listOfDoubles);
        }

        return listOfDoubles;
    }

    public ArrayList<Double> getValueAndTimeOfAttributesOfBlockBySimNumber(AvatarBlock ab, AvatarAttribute aa, int indexOfAttribute, int simuIndex) {
        ArrayList<Double> listOfDoubles = new ArrayList<>();

        if (simuIndex < listOfSimulations.size()) {
            AvatarSpecificationSimulation ass = listOfSimulations.get(simuIndex);
            ass.fillValuesOfTimesOfBlockAttribute(ab, aa, indexOfAttribute, listOfDoubles);
        }

        return listOfDoubles;
    }


}
