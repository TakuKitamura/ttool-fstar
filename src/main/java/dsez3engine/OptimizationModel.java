package dsez3engine;

import com.microsoft.z3.*;
import tmltranslator.*;
import tmltranslator.tmlcp.TMLCPSequenceDiagram;

import java.util.ArrayList;
import java.util.List;


public class OptimizationModel {

    private InputInstance inputInstance;
    //private List<TMLCPSequenceDiagram> tmlcpSequenceDiagrams = new ArrayList<TMLCPSequenceDiagram>();

    public OptimizationModel(InputInstance inputInstance) {
        this.inputInstance = inputInstance;
    }


    public void findFeasibleMapping(Context ctx) throws TestFailedException {

        System.out.println("\nFind a feasible Mapping");

        //Decision variables

        //Definition of Xtp as a matrix. Each cell corresponds to a combination of a task and a processor.
        IntExpr[][] X = new IntExpr[inputInstance.getModeling().getTasks().size()][inputInstance.getArchitecture().getCPUs().size()]; //getCPUs returns only HwCPU elements

        //TODO paths

        //Definition of Ycw as a matrix. Each cell corresponds to a combination of a channel and a path.
        //  IntExpr[][] Y = new IntExpr[inputInstance.getModeling().getChannels().size()][tmlcpSequenceDiagrams.size()];

        //Definition of start time variable for each task
        IntExpr[] start = new IntExpr[inputInstance.getModeling().getTasks().size()];


        //Mapping Constraints :
        BoolExpr mapping_constraints = ctx.mkTrue();

        //Matrix of constraints to define the interval of Xtp : 0 <= Xtp <= 1
        BoolExpr[][] c_bound_x = new BoolExpr[inputInstance.getModeling().getTasks().size()][inputInstance.getArchitecture().getCPUs().size()];

        //System.out.println("\nDefining the bounds of Xtp (1)");

        for (TMLTask tmlTask : (List<TMLTask>) inputInstance.getModeling().getTasks()) {

            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);

            for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {

                int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

                X[t][p] = ctx.mkIntConst("X_" + tmlTask.getID() + "_" + hwNode.getID());

                // Constraints: 0 <= Xtp <= 1
                c_bound_x[t][p] = ctx.mkAnd(ctx.mkLe(ctx.mkInt(0), X[t][p]),
                        ctx.mkLe(X[t][p], ctx.mkInt(1)));

               // System.out.println(c_bound_x[t][p]);
            }
        }

        // TODO add the definition interval for Ycw

        //CONSTRAINTS//
        //Each task must be mapped to exactly 1 processing unit in its eligibility set:

        // ∀t, SUM p (X tp) ≤ 1

        //System.out.println("\nUnique task-CPU mapping constraints (3)");
        BoolExpr[] c_unique_x = new BoolExpr[inputInstance.getModeling().getTasks().size()];
        for (TMLTask tmlTask : (List<TMLTask>) inputInstance.getModeling().getTasks()) {
            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask); // TODO or use the local instance modeling

            ArithExpr sum_X = ctx.mkAdd(X[t]);
            c_unique_x[t] = ctx.mkLe(sum_X, ctx.mkInt(1));
           // System.out.println(c_unique_x[t]);

        }


        //Feasible Task map: ∀t, SUM p in F(t) (X tp) = 1
        //System.out.println("\nFeasible task-CPU mapping constraints (4)");
        BoolExpr[] c_feasibleMapX = new BoolExpr[inputInstance.getModeling().getTasks().size()];
        for (TMLTask tmlTask : (List<TMLTask>) inputInstance.getModeling().getTasks()) {
            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);
            ArithExpr sum_X = ctx.mkInt(0);

            for (HwNode hwNode : inputInstance.getFeasibleCPUs(tmlTask)) {

                int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

                sum_X = ctx.mkAdd(sum_X, X[t][p]);
            }
            c_feasibleMapX[t] = ctx.mkEq(sum_X, ctx.mkInt(1));

            //System.out.println(c_feasibleMapX[t]);

        }

        //Memory size constraint: ∀p, ∀t, X tp × (bin + bout ) ≤ mem

        //System.out.println("\nMemory size constraints (5)");
        BoolExpr[][] c_mem = new BoolExpr[inputInstance.getArchitecture().getNbOfCPU()][inputInstance.getModeling().getTasks().size()];

        for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {
            int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

            IntExpr mem = ctx.mkInt(inputInstance.getLocalMemoryOfHwExecutionNode(hwNode).memorySize);


            for (TMLTask tmlTask : (List<TMLTask>) inputInstance.getModeling().getTasks()) {
                int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);

                IntExpr bin_plus_bout = ctx.mkInt(inputInstance.getBufferIn(tmlTask) + inputInstance.getBufferOut(tmlTask));
                ArithExpr bin_plus_bout_times_X = ctx.mkMul(bin_plus_bout, X[t][p]);

                c_mem[p][t] = ctx.mkLe(bin_plus_bout_times_X, mem);

                //TODO may be uncomment
               // System.out.println(c_mem[p][t]);

            }
        }

/*
        //At least one route Constraint: ∀c, (t1,t2) in c; X t1p1 + X t2p2 − r p1p2 ≤ 1
        System.out.println("\nAt least one route constraints (6)");
        BoolExpr[] c_route = new BoolExpr[inputInstance.getModeling().getChannels().size()];

        IntExpr[][] r = new IntExpr[inputInstance.getArchitecture().getCPUs().size()][inputInstance.getArchitecture().getCPUs().size()];

        for (TMLChannel channel : (List<TMLChannel>) inputInstance.getModeling().getChannels()) {

            int c = inputInstance.getModeling().getChannels().indexOf(channel);
            //for each channel get producer and consumer
            TMLTask producer = channel.getOriginTask();
            int prodIndex = inputInstance.getModeling().getTasks().indexOf(producer);

            TMLTask consumer = channel.getDestinationTask();
            int consIndex = inputInstance.getModeling().getTasks().indexOf(consumer);

            for (HwNode source : inputInstance.getArchitecture().getCPUs()) {
                int src = inputInstance.getArchitecture().getCPUs().indexOf(source);

                for (HwNode destination : inputInstance.getArchitecture().getCPUs()) {
                    int dst = inputInstance.getArchitecture().getCPUs().indexOf(destination);
                    //for each couple of processors TODO calculate rp1p2
                    r[src][dst] = ctx.mkInt(1);
                    ArithExpr Xp_plus_Xc = ctx.mkAdd(X[prodIndex][src], X[consIndex][dst]);
                    ArithExpr Xp_plus_Xc_minus_r = ctx.mkSub(Xp_plus_Xc, r[src][dst]);

                    c_route[c] = ctx.mkLe(Xp_plus_Xc_minus_r, ctx.mkInt(1));

                    System.out.println("Channel(" + channel.toString() + ") " + c_route[c]);

                    //TODO uncomment
                    // mapping_constraints = ctx.mkAnd(c_route[c], mapping_constraints);
                }
            }
        }

        //Scheduling: Precedence constraints: ∀c, τs (t cons ) ≥ τe (t prod )
        System.out.println("\nPrecedence constraints (11)");
        BoolExpr[] c_precedence = new BoolExpr[inputInstance.getModeling().getChannels().size()];

        for (TMLChannel channel : (List<TMLChannel>) inputInstance.getModeling().getChannels()) {

            int c = inputInstance.getModeling().getChannels().indexOf(channel);
            //for each channel get producer and consumer
            TMLTask producer = channel.getOriginTask();
            int prodIndex = inputInstance.getModeling().getTasks().indexOf(producer);

            TMLTask consumer = channel.getDestinationTask();
            int consIndex = inputInstance.getModeling().getTasks().indexOf(consumer);

            //System.out.println("\nChannel(" + producer.getId() + "," + consumer.getId() + ")");

            ArithExpr wcetProducer = ctx.mkInt(0);

            for (HwExecutionNode hwNode : inputInstance.getFeasibleCPUs(producer)) {
                int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

                wcetProducer = ctx.mkAdd(wcetProducer, ctx.mkMul(X[prodIndex][p], ctx.mkInt(inputInstance.getWCET(producer, hwNode))));

            }

            ArithExpr endProducer = ctx.mkAdd(start[prodIndex], wcetProducer);
            ArithExpr startC_minus_endP = ctx.mkSub(start[consIndex], endProducer);

            c_precedence[c] = ctx.mkGe(startC_minus_endP, ctx.mkInt(0));

            System.out.println(c_precedence[c]);
            //TODO uncomment
            // mapping_constraints = ctx.mkAnd(mapping_constraints, c_precedence[c]);

        }


        //∀p∀t i ∀t j 6 = t i , ¬((X t i p = 1) ∧ (X t j p = 1)) ∨ ((τ s (t j ) ≥ τ e (t i )) ∨ (τ s (t i ) ≥ τ e (t j )))
        //Scheduling: One single task running on CPU ∀t
        System.out.println("\n Same resource/no time overlap Constraints (12)");
        BoolExpr c_monoTask[][][] = new BoolExpr[inputInstance.getArchitecture().getCPUs().size()][inputInstance.getModeling().getTasks().size()][inputInstance.getModeling().getTasks().size()];

        for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {
            int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

            for (TMLTask taski : (List<TMLTask>) inputInstance.getModeling().getTasks()) {

                int ti = inputInstance.getModeling().getTasks().indexOf(taski);
                //Calculate End times of ti
                ArithExpr wceti = ctx.mkInt(0);

                for (HwExecutionNode ihwNode : inputInstance.getFeasibleCPUs(taski)) {
                    int ip = inputInstance.getArchitecture().getCPUs().indexOf(ihwNode);

                    wceti = ctx.mkAdd(wceti, ctx.mkMul(X[ti][ip], ctx.mkInt(inputInstance.getWCET(taski, ihwNode))));

                }

                ArithExpr endti = ctx.mkAdd(start[ti], wceti);

                for (TMLTask taskj : (List<TMLTask>) inputInstance.getModeling().getTasks()) {

                    int tj = inputInstance.getModeling().getTasks().indexOf(taskj);


                    if (tj != ti) {
                        //Calculate End times of tj

                        ArithExpr wcetj = ctx.mkInt(0);

                        for (HwExecutionNode jhwNode : inputInstance.getFeasibleCPUs(taskj)) {
                            int jp = inputInstance.getArchitecture().getCPUs().indexOf(jhwNode);

                            wcetj = ctx.mkAdd(wcetj, ctx.mkMul(X[tj][jp], ctx.mkInt(inputInstance.getWCET(taskj, jhwNode))));


                        }

                        ArithExpr endtj = ctx.mkAdd(start[tj], wcetj);

                        BoolExpr alpha = ctx.mkAnd(ctx.mkEq(X[ti][p], ctx.mkInt(1)), ctx.mkEq(X[tj][p], ctx.mkInt(1)));

                        BoolExpr not_alpha = ctx.mkNot(alpha);

                        BoolExpr beta = ctx.mkOr(ctx.mkGe(start[ti], endtj), ctx.mkGe(start[tj], endti));

                        //
                        c_monoTask[p][ti][tj] = ctx.mkOr(not_alpha, beta);
                        System.out.println("\n" + c_monoTask[p][ti][tj]);

                        //TODO uncomment
                        //mapping_constraints = ctx.mkAnd(mapping_constraints, c_monoTask[p][ti][tj]);
                    }
                }

            }

        }


*/

        //Grouping remaining constraints of the model

        // System.out.println("\nAll mapping constraints");
        for (TMLTask task : (List<TMLTask>) inputInstance.getModeling().getTasks()) {

            int t = inputInstance.getModeling().getTasks().indexOf(task);

            for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {
                int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

                mapping_constraints = ctx.mkAnd(c_bound_x[t][p], mapping_constraints);
                mapping_constraints = ctx.mkAnd(c_mem[p][t], mapping_constraints);

            }

            // Constraint: feasible unique mapping of tasks
            mapping_constraints = ctx.mkAnd(c_unique_x[t], mapping_constraints);
            mapping_constraints = ctx.mkAnd(c_feasibleMapX[t], mapping_constraints);

        }

        //Creating a solver instance
        Solver s = ctx.mkSolver();
        s.add(mapping_constraints);


        if (s.check() == Status.SATISFIABLE) {
            Model m = s.getModel();
            Expr[][] optimized_result_X = new Expr[inputInstance.getModeling().getTasks().size()][inputInstance.getArchitecture().getCPUs().size()];

            System.out.println("\nProposed Mapping solution:\n");
            for (TMLTask task : (List<TMLTask>) inputInstance.getModeling().getTasks()) {
                int t = inputInstance.getModeling().getTasks().indexOf(task);

                for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {
                    int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);


                    optimized_result_X[t][p] = m.evaluate(X[t][p], false);
                    System.out.println("X[" + task.getName() + "][" + hwNode.getName() + "] = " + optimized_result_X[t][p]);
                }


                System.out.println("\n");
            }

        } else {
            System.out.println("Failed to solve mapping problem");
            throw new TestFailedException();
        }


    }//findFeasibleMapping()


    class TestFailedException extends Exception {
        public TestFailedException() {
            super("Check FAILED");
        }
    }

}
