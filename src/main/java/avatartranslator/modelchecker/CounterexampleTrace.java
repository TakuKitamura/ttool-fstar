package avatartranslator.modelchecker;

import java.util.LinkedList;
import java.util.List;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachine;

public class CounterexampleTrace {
    private LinkedList<CounterexampleTraceState> trace;
    private CounterexampleTraceState counterexampleState;
    private AvatarSpecification spec;

    
    public CounterexampleTrace(AvatarSpecification spec) {
        this.trace = null;
        this.counterexampleState = null;
        this.spec = spec;
    }
    
    
    public void setCounterexample(CounterexampleTraceState cs) {
        this.counterexampleState = cs;
    }

    
    public boolean hasCounterexample() {
        return counterexampleState != null;
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
                s.append("\t" + block.getName() + "." + block.getStateMachine().allStates[cs.blocksStates[j]].getName());
            }
            s.append("\n");
            id++;
        }
        return s.toString();
    }

}
