package ui.graph;

import org.junit.Test;

public class AUTGraphTest  {
    @Test
    public void testAUTGraph() {
        AUTGraph graph = new AUTGraph();

        graph.setNbOfStates(6);

        graph.addTransition(new AUTTransition(0, "a", 1));
        graph.addTransition(new AUTTransition(0, "b", 3));

        graph.addTransition(new AUTTransition(1, "a", 2));
        graph.addTransition(new AUTTransition(1, "b", 3));
        graph.addTransition(new AUTTransition(1, "b", 4));

        graph.addTransition(new AUTTransition(2, "a", 1));
        graph.addTransition(new AUTTransition(2, "b", 4));

        graph.addTransition(new AUTTransition(3, "c", 5));
        graph.addTransition(new AUTTransition(4, "c", 5));

        graph.computeStates();

        System.out.println("Graph:" + graph.toFullString());

        graph.partitionGraph();
    }
}

