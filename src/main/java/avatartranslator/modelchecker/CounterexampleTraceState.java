package avatartranslator.modelchecker;

public class CounterexampleTraceState {
    public CounterexampleTraceState father;
    public int[] blocksStates;
    public int hash;

    public CounterexampleTraceState() {
        this.father = null;
        this.blocksStates = null;
    }

    public void initState(SpecificationState ss, CounterexampleTraceState father) {
        this.father = father;
        this.hash = ss.hashValue;
        if (ss.blocks != null) {
            this.blocksStates = new int[ss.blocks.length];
            for (int i = 0; i < ss.blocks.length; i++) {
                this.blocksStates[i] = ss.blocks[i].values[SpecificationBlock.STATE_INDEX];
            }
        }
    }

    public String toString() {
        return "" + hash;
    }

}
