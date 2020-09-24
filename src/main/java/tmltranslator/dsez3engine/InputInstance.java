/* Copyright or (C) 2017-2020 Nokia
 * Copyright or (C) GET / ENST, Telecom-Paris
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package tmltranslator.dsez3engine;

import myutil.TraceManager;
import tmltranslator.*;
import ui.TGComponent;

import java.util.ArrayList;
import java.util.List;


public class InputInstance {

    private TMLArchitecture architecture;
    private TMLModeling<TGComponent> modeling;

    public InputInstance(TMLArchitecture architecture, TMLModeling<TGComponent> modeling) {
        this.architecture = architecture;
        this.modeling = modeling;
    }

    //get the list of eligible CPUs for a given task
    public List<HwExecutionNode> getFeasibleCPUs(TMLTask tmlTask) {

        List<HwExecutionNode> feasibleCPUs = new ArrayList<>();

        for (HwNode hwNode : architecture.getHwNodes()) {
            if (hwNode instanceof HwExecutionNode) {

                if ((((HwExecutionNode) hwNode).supportOperation(tmlTask.getOperation()) ) || (((HwExecutionNode) hwNode).getOperation().equals(" ")))
                    feasibleCPUs.add((HwExecutionNode) hwNode);

            }

        }
        return feasibleCPUs;
    }

    public int getBufferIn(TMLTask tmlTask) {

        //TODO if tmlReadChannel.getNbOfSamples() does not return int msg: please enter an integer number of samples

        int bin = 0;
        for (TMLReadChannel tmlReadChannel : tmlTask.getReadChannels()) {
            bin = bin + Integer.valueOf(tmlReadChannel.getNbOfSamples());
        }

        return bin;
    }

    public int getBufferOut(TMLTask tmlTask) {
        int bout = 0;
        for (TMLWriteChannel tmlWriteChannel : tmlTask.getWriteChannels()) {
            bout = bout + Integer.valueOf(tmlWriteChannel.getNbOfSamples());
        }
        return bout;
    }

    public int getWCET(TMLTask tmlTask, HwExecutionNode hwExecutionNode) {
        return (tmlTask.getWorstCaseIComplexity() * hwExecutionNode.getExeciTime());
    }

    //should work for cases where each processing unit is equipped with a unique local memory connected directly through a bus
    public HwMemory getLocalMemoryOfHwExecutionNode(HwNode hwNode) {

        List<HwLink> firstLinks = new ArrayList<>();

        for (int i = 0; i < architecture.getLinkByHwNode(hwNode).size(); i++) {
            firstLinks.add(architecture.getLinkByHwNode(hwNode).get(i));
        }


        HwMemory tempMem = new HwMemory("");

        if ((!firstLinks.isEmpty())) {
            List<HwLink> secondLinks = new ArrayList<>();

            for (int i = 0; i < architecture.getLinkByBus(firstLinks.get(0).bus).size(); i++) {
                secondLinks.add(architecture.getLinkByBus(firstLinks.get(0).bus).get(i)); // link 1 and 1 cpu side
            }


            for (HwLink secondlink : secondLinks) {
                if (secondlink.hwnode instanceof HwMemory) {
                    tempMem = (HwMemory) secondlink.hwnode;

                }
            }
        }


        return tempMem;
    }

    //TODO this supposes that we have one single final task

    public TMLTask getFinalTask(TMLModeling tmlm) {
        TMLTask finalTask = null;

        for (Object tmlTask : tmlm.getTasks()) {
            TMLTask taskCast = (TMLTask) tmlTask;

            if ((taskCast.getWriteChannels().isEmpty()) && (!taskCast.getReadChannels().isEmpty()))
                finalTask = taskCast;
        }

        // TraceManager.addDev("final task is" + finalTask.getName());

        return finalTask;
    }


    public TMLArchitecture getArchitecture() {
        return architecture;
    }

    public TMLModeling<TGComponent> getModeling() {
        return modeling;
    }
}