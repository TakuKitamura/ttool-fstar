package avatartranslator.modelchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;


public class CounterexampleTrace {
    private LinkedList<CounterexampleTraceState> trace;
    private CounterexampleTraceState counterexampleState;
    private AvatarSpecification spec;
    private List<CounterexampleQueryReport> autTraces;

    
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
    
    
    public List<CounterexampleQueryReport> getAUTTrace() {
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
    
    public void generateTraceAUT(String query, Map<Integer, SpecificationState> states) {
        if (trace == null) {
            return;
        }
        
        if (autTraces == null) {
            autTraces = new ArrayList<CounterexampleQueryReport>();
        }
        
        List<AvatarBlock> blocks = spec.getListOfBlocks();
        Map<Long, Integer> statesID = new HashMap<>();
        
        for (AvatarBlock block : blocks) {
            if (block.getStateMachine().allStates == null) {
                return;
            }
        }

        SpecificationState state = null;
        
        StringBuilder s = new StringBuilder();

        int id = 0;

        for (CounterexampleTraceState cs : trace) {
            if (!statesID.containsKey(states.get(cs.hash).id)) {
                statesID.put(states.get(cs.hash).id, id++);
            }
            if (state != null) {
                for (SpecificationLink sl : state.nexts) {
                    if (sl.destinationState.hashValue == cs.hash) {
                        s.append("(" + statesID.get(sl.originState.id) + ",\"" + sl.action + "\"," + statesID.get(sl.destinationState.id) + ")\n");
                        break;
                    }
                }
            }
            state = states.get(cs.hash);
        }
              
        
        s.insert(0 ,"des(0," + (trace.size() - 1) + "," + statesID.size() + ")\n");
        
        CounterexampleQueryReport cr = new CounterexampleQueryReport(null, query, s.toString());
        autTraces.add(cr);
    }

}
