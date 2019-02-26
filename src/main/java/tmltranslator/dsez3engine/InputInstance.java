package tmltranslator.dsez3engine;

import tmltranslator.*;

import java.util.ArrayList;
import java.util.List;


public class InputInstance {

    private TMLArchitecture architecture;
    private TMLModeling modeling;

    public InputInstance(TMLArchitecture architecture, TMLModeling modeling) {
        this.architecture = architecture;
        this.modeling = modeling;
    }

    //get the list of eligible CPUs for a given task
    public List<HwExecutionNode> getFeasibleCPUs(TMLTask tmlTask) {

        List<HwExecutionNode> feasibleCPUs = new ArrayList<>();

        for (HwNode hwNode : architecture.getHwNodes()) {
            if (hwNode instanceof HwExecutionNode) {

                for (String operationType : ((HwExecutionNode) hwNode).getOperationTypes()) {
                    if (operationType.equals(tmlTask.getOperation())) {
                        feasibleCPUs.add((HwExecutionNode) hwNode);
                        break;
                    }
                }
            }

        }
        return feasibleCPUs;
    }

    // TODO the location of the implemented methods (better in TMLTask).
    public int getBufferIn(TMLTask tmlTask) {

        int bin = 0;
        for (TMLChannel tmlReadChannel : tmlTask.getReadTMLChannels()) { // TODO replace with getReadChannels() once finished integration
            bin = bin + tmlReadChannel.getNumberOfSamples();
        }

        return bin;
    }

    public int getBufferOut(TMLTask tmlTask) {
        int bout = 0;
        for (TMLChannel tmlWriteChannel : tmlTask.getWriteTMLChannels()) {
            bout = bout + tmlWriteChannel.getNumberOfSamples();
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


//TODO
      /*  if(firstLinks.size() >= 1){
            for(HwLink recursiveHwLink : firstLinks){

            }

        }*/

        return tempMem;
    }

    //TODO this supposes that we have one single final task

    public TMLTask getFinalTask(TMLModeling tmlm){
        TMLTask finalTask = null;
        for (Object tmlTask :  tmlm.getTasks()){
            TMLTask taskCast = (TMLTask)tmlTask;
            if (taskCast.getWriteTMLChannels().isEmpty())
                finalTask = taskCast;
        }

        return finalTask;
    }


    public TMLArchitecture getArchitecture() {
        return architecture;
    }

    public TMLModeling getModeling() {
        return modeling;
    }
}
