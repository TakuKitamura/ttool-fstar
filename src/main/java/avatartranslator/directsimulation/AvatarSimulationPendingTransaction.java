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

import avatartranslator.AvatarRandom;
import avatartranslator.AvatarStateMachineElement;
import avatartranslator.AvatarTransition;
import myutil.MyMath;
import myutil.TraceManager;

import java.awt.*;
import java.util.UUID;
import java.util.Vector;


/**
 * Class AvatarSimulationPendingTransaction
 * Avatar: notion of pending transaction in simulation
 * Creation: 11/01/2011
 *
 * @author Ludovic APVRILLE
 * @version 1.0 11/01/2011
 */
public class AvatarSimulationPendingTransaction {

    public AvatarSimulationBlock asb;
    public AvatarStateMachineElement previouslyExecutedElement;
    public AvatarStateMachineElement elementToExecute;
    public AvatarStateMachineElement involvedElement; //(transition)
    public AvatarSimulationPendingTransaction linkedTransaction;
    public Vector<AvatarSimulationPendingTransaction> linkedTransactions; // Used for broadcast
    public AvatarSimulationAsynchronousTransaction linkedAsynchronousMessage;
    public long clockValue;
    public boolean isSynchronous;
    public boolean isSending;
    public Vector<String> parameters;

    // To store a delay prior to execution
    public int myMinDelay;
    public int myMaxDelay;
    public boolean hasDelay;
    public int delayDistributionLaw;
    public double extraParam1;
    public double extraParam2;

    // For time already elapsed for that transition
    public boolean hasElapsedTime;
    public int elapsedTime;

    // To store a computed delay
    public int myMinDuration; // computed min delay by simulator
    public int myMaxDuration; // computed max delay by simulator
    public int selectedDuration; // duration selected by simulator
    public long maxDuration; // max duration selected by simulator
    public boolean hasClock; // Selected by simulator to indicate a delay on that transaction
    public boolean durationSelected;
    public long clockValueAtEnd;

    // if linked duration
    public boolean durationOnCurrent;
    public boolean durationOnOther;

    // silent
    public boolean isSilent;

    // broadcast
    public boolean isBroadcast;

    // Lost
    public boolean isLost;

    // Probability of this transaction
    public double probability = 0.5;


    public AvatarSimulationPendingTransaction() {
        hasClock = false;
        hasElapsedTime = false;
        hasDelay = false;
        isBroadcast = false;
    }

    public AvatarSimulationPendingTransaction cloneMe() {

        AvatarSimulationPendingTransaction aspt = new AvatarSimulationPendingTransaction();
        aspt.asb = this.asb;
        aspt.elementToExecute = this.elementToExecute;
        aspt.previouslyExecutedElement = this.previouslyExecutedElement;
        aspt.involvedElement = this.involvedElement;
        aspt.linkedTransaction = this.linkedTransaction;
        aspt.linkedAsynchronousMessage = this.linkedAsynchronousMessage;
        aspt.clockValue = this.clockValue;

        aspt.myMinDelay = this.myMinDelay;
        aspt.myMaxDelay = this.myMaxDelay;
        aspt.hasDelay = this.hasDelay;
        aspt.delayDistributionLaw = this.delayDistributionLaw;
        aspt.extraParam1 = this.extraParam1;
        aspt.hasElapsedTime = this.hasElapsedTime;
        aspt.elapsedTime = this.elapsedTime;
        aspt.myMinDuration = this.myMinDuration;
        aspt.myMaxDuration = this.myMaxDuration;
        aspt.selectedDuration = this.selectedDuration;
        aspt.maxDuration = this.maxDuration;
        aspt.hasClock = this.hasClock;
        aspt.isBroadcast = this.isBroadcast;

        return aspt;
    }

    public AvatarSimulationPendingTransaction fullCloneMe() {

        AvatarSimulationPendingTransaction aspt = cloneMe();
        if (linkedTransactions != null) {
            aspt.linkedTransactions = new Vector<AvatarSimulationPendingTransaction>();
            for (AvatarSimulationPendingTransaction aspt0 : linkedTransactions) {
                aspt.linkedTransactions.add(aspt0);
            }
        }

        return aspt;
    }

    public boolean hasConfiguredDurationMoreThan0() {
        if (linkedTransaction == null) {
            if (!hasDelay) {
                return false;
            }
            return myMinDuration > 0;
        }

        if ((!durationOnCurrent) && (!durationOnOther)) {
            return false;
        }

        return myMinDuration > 0;

    }


    public String toString() {
        String res = "in Block " + asb.getName() + ": ";
        if (linkedTransactions != null) {
            res = res + "broadcast ";
        }
        if (linkedTransaction == null) {
            if (elementToExecute == null) {
                TraceManager.addDev("Null element to execute");
            }

            res = res + elementToExecute.getNiceName() + "/ID=" + elementToExecute.getID() + "/UUID=" + elementToExecute.getUUID();
            if (hasClock) {
                if (myMinDuration == maxDuration) {
                    res += " [Delay: " + myMinDuration + "]";
                } else {
                    res += " [Delay: between " + myMinDuration + " and " + maxDuration + "]";
                }
            }

        } else {
            res += "[SYNCHRO]" + elementToExecute.getNiceName() + "/ID=" + elementToExecute.getID() + "/UUID=" + elementToExecute.getUUID();
            res += " | " + linkedTransaction.toString();
        }

        if (linkedTransactions != null) {
            res += " --to--> [";
            int cpt = 0;
            for (AvatarSimulationPendingTransaction aspt : linkedTransactions) {
                if (cpt == 0) {
                    cpt++;
                } else {
                    res += " ";
                }
                res += aspt.elementToExecute.getID() + "/" + aspt.elementToExecute.getUUID();
            }
            res += "]";
        }

        return res;
    }

    public Point hasDuplicatedBlockTransaction() {
        Vector<AvatarSimulationBlock> blocks = new Vector<AvatarSimulationBlock>();

        if (linkedTransactions == null) {
            return null;
        }

        for (AvatarSimulationPendingTransaction aspt : linkedTransactions) {
            if (blocks.contains(aspt.asb)) {
                return new Point(blocks.indexOf(aspt.asb), blocks.size());
            }
            blocks.add(aspt.asb);

        }
        return null;
    }

    public boolean hasBlock(AvatarSimulationBlock _b) {
        //TraceManager.addDev("HasBlock? _b=" + _b + " tr=" + this);
        if (asb == _b) {
            //TraceManager.addDev("True");
            return true;
        }

        if (linkedTransaction != null) {
            if (linkedTransaction.asb == _b) {
                //TraceManager.addDev("True");
                return true;
            }
        }

        if (linkedTransactions != null) {
            for (AvatarSimulationPendingTransaction aspt : linkedTransactions) {
                if (aspt.asb == _b) {
                    //TraceManager.addDev("True");
                    return true;
                }
            }
        }

        //TraceManager.addDev("False");
        return false;

    }

    public void makeRandomDelay() {
        switch (delayDistributionLaw) {
            case AvatarTransition.DELAY_UNIFORM_LAW:
                //TraceManager.addDev("\n\n\n******* UNIFORM LAW ********");
                selectedDuration = myMinDuration + (int) (Math.floor(Math.random() * (maxDuration - myMinDuration)));
                return;
            case AvatarTransition.DELAY_TRIANGULAR_LAW:
                //TraceManager.addDev("\n\n\n******* TRIANGULAR LAW ********");
                selectedDuration = (int) (MyMath.triangularDistribution((double) (myMinDuration), (double) (maxDuration), extraParam1));
                return;
            case AvatarTransition.DELAY_GAUSSIAN_LAW:
                //TraceManager.addDev("\n\n\n******* GAUSSIAN LAW ********");
                selectedDuration = (int)(Math.floor(MyMath.gaussianDistribution((double) (myMinDuration), (double) (maxDuration), extraParam1)));
                return;
            case AvatarTransition.RANDOM_LOG_NORMAL_LAW:
                try {
                    selectedDuration =  (int) (Math.floor(MyMath.logNormalDistribution((double) (myMinDuration), (double) (maxDuration),
                            extraParam1,
                            extraParam2)));
                } catch (Exception e) {
                    TraceManager.addDev("Exception on log normal: " + e.getMessage());
                    selectedDuration = myMinDuration;
                }
                return;
            case AvatarTransition.RANDOM_EXPONENTIAL_LAW:
                try {
                    selectedDuration =  (int) (Math.floor(MyMath.exponentialDistribution( (double) (myMinDuration), (double) (maxDuration), extraParam1) ));
                } catch (Exception e) {
                    TraceManager.addDev("Exception on exponential distribution: " + e.getMessage());
                    selectedDuration = myMinDuration;
                }
                return;
            case AvatarTransition.RANDOM_WEIBULL_LAW:
                try {
                    selectedDuration = (int) (Math.floor(MyMath.weibullDistribution( (double) (myMinDuration), (double) (maxDuration), extraParam1,
                            extraParam2) ));
                } catch (Exception e) {
                    TraceManager.addDev("Exception on weibull distribution: " + e.getMessage());
                    selectedDuration = myMinDuration;
                }
                return;

        }
    }

    public UUID getUUID() {
        if (elementToExecute == null) {
            return null;
        }

        return elementToExecute.getUUID();

    }


}
