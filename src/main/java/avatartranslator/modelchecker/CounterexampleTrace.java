package avatartranslator.modelchecker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachine;

public class CounterexampleTrace {
    private LinkedList<CounterexampleTraceState> trace;
    private CounterexampleTraceState counterexampleState;
    private AvatarSpecification spec;
    private List<String> autTraces;

    
    public CounterexampleTrace(AvatarSpecification spec) {
        this.trace = null;
        this.counterexampleState = null;
        this.spec = spec;
        this.autTraces = null;
    }
    
    
    public void setCounterexample(CounterexampleTraceState cs) {
        this.counterexampleState = cs;
    }

    
    public boolean hasCounterexample() {
        return counterexampleState != null;
    }
    
    
    public void reset() {
        this.trace = null;
        this.counterexampleState = null;
    }
    
    
    public List<String> getAUTTrace() {
        return autTraces;
    }
    
    
    public void resetAUTTrace() {
        if (autTraces != null) {
            autTraces.clear();
        }
    }
    
    
    public boolean buildTrace(CounterexampleTraceState cs) {
        counterexampleState = cs;
        return buildTrace();
    }
    
    
    public boolean buildTrace() {
        if (counterexampleState == null) {
            return false;
        }

        trace = new LinkedList<CounterexampleTraceState>();

        CounterexampleTraceState cs = counterexampleState;
        trace.add(cs);

        while (cs.father != null) {
            cs = cs.father;
            trace.add(0, cs);
        }

        return true;
    }
    
    
    public String toString() {
        if (trace == null) {
            return "";
        }
        
        StringBuilder s = new StringBuilder();
        
        for (CounterexampleTraceState cs : trace) {
            s.append(cs.toString() + " -> ");
        }
        s.append(" []");
        return s.toString();
    }
    
    
    public String generateSimpleTrace() {
        if (trace == null) {
            return "";
        }
        
        StringBuilder s = new StringBuilder();
        List<AvatarBlock> blocks = spec.getListOfBlocks();
        
        for (AvatarBlock block : blocks) {
            if (block.getStateMachine().allStates == null) {
                return "";
            }
        }

        int id = 0;
        for (CounterexampleTraceState cs : trace) {
            s.append(id);
            int j = 0;
            for (AvatarBlock block : blocks) {
                s.append("\t" + block.getName() + "." + block.getStateMachine().allStates[cs.blocksStates[j++]].getName());
            }
            s.append("\n");
            id++;
        }
        return s.toString();
    }
    
    
    public String generateSimpleTrace(Map<Integer, SpecificationState> states) {
        if (trace == null) {
            return "";
        }
        
        StringBuilder s = new StringBuilder();
        List<AvatarBlock> blocks = spec.getListOfBlocks();
        
        for (AvatarBlock block : blocks) {
            if (block.getStateMachine().allStates == null) {
                return "";
            }
        }

        int id = 0;
        SpecificationState state = null;
        for (CounterexampleTraceState cs : trace) {
            if (state != null) {
                for (SpecificationLink sl : state.nexts) {
                    if (sl.destinationState.hashValue == cs.hash) {
                        s.append("Transition " + sl.action + "\n");
                        break;
                    }
                }
            }
            s.append("State " + id + ":\t");
            int j = 0;
            for (AvatarBlock block : blocks) {
                s.append("\t" + block.getName() + "." + block.getStateMachine().allStates[cs.blocksStates[j++]].getName());
            }
            s.append("\n");
            state = states.get(cs.hash);       
            id++;
        }
        return s.toString();
    }
    
    public void generateTraceAUT(Map<Integer, SpecificationState> states) {
        if (trace == null) {
            return;
        }
        
        if (autTraces == null) {
            autTraces = new ArrayList<String>();
        }
        
        List<AvatarBlock> blocks = spec.getListOfBlocks();
        
        for (AvatarBlock block : blocks) {
            if (block.getStateMachine().allStates == null) {
                return;
            }
        }

        SpecificationState state = null;
        
        StringBuilder s = new StringBuilder();

        int nstates = 0;
        int counterex = counterexampleState.hash;

        for (CounterexampleTraceState cs : trace) {
            if (state != null) {
                for (SpecificationLink sl : state.nexts) {
                    if (sl.destinationState.hashValue == cs.hash) {
                        s.append("(" + sl.originState.id + ",\"" + sl.action + "\"," + sl.destinationState.id + ")\n");
                        break;
                    }
                }
            }
            state = states.get(cs.hash);
            if (cs.hash == counterex) {
                nstates++;
            }
        }
               
        nstates = trace.size() - nstates + 1;
        
        s.insert(0 ,"des(0," + (trace.size() - 1) + "," + nstates + ")\n");
        
        
        autTraces.add(s.toString());
    }

}
