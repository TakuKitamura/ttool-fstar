package tmltranslator.dsez3engine;

import com.microsoft.z3.*;
import myutil.TraceManager;
import tmltranslator.*;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class OptimizationModel {

    private Map<String, Integer> optimizedSolutionX = new HashMap<String, Integer>();
    private Map<String, Integer> optimizedSolutionStart = new HashMap<String, Integer>();
    private InputInstance inputInstance;

    public OptimizationModel(InputInstance inputInstance) {

        this.inputInstance = inputInstance;
    }

    public Map<String, Integer> getOptimizedSolutionX() {
        return optimizedSolutionX;
    }

    public void setOptimizedSolutionX(Map<String, Integer> optimizedSolutionX) {
        this.optimizedSolutionX = optimizedSolutionX;
    }

    public Map<String, Integer> getOptimizedSolutionStart() {
        return optimizedSolutionStart;
    }

    public void setOptimizedSolutionStart(Map<String, Integer> optimizedSolutionStart) {
        this.optimizedSolutionStart = optimizedSolutionStart;
    }


    public void findFeasibleMapping(Context ctx) throws TestFailedException {

        TraceManager.addDev("\nFind feasible Mapping");

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

        //TraceManager.addDev("\nDefining the bounds of Xtp (1)");

        for (Object tmlTask : inputInstance.getModeling().getTasks()) {

            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);
            TMLTask taskCast = (TMLTask)tmlTask;

            for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {

                int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

                X[t][p] = ctx.mkIntConst("X_" + taskCast.getID() + "_" + hwNode.getID());

                // Constraints: 0 <= Xtp <= 1
                c_bound_x[t][p] = ctx.mkAnd(ctx.mkLe(ctx.mkInt(0), X[t][p]),
                        ctx.mkLe(X[t][p], ctx.mkInt(1)));

                // TraceManager.addDev(c_bound_x[t][p]);
            }
        }

        //Constraint on start >=0
        BoolExpr[] c_bound_start = new BoolExpr[inputInstance.getModeling().getTasks().size()];

        for (Object tmlTask :  inputInstance.getModeling().getTasks()) {
            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);
            TMLTask taskCast = (TMLTask)tmlTask;

            start[t] = ctx.mkIntConst("start_" + taskCast.getID());
            c_bound_start[t] = ctx.mkGe(start[t], ctx.mkInt(0));
            mapping_constraints = ctx.mkAnd(mapping_constraints, c_bound_start[t]);
        }

        // TODO add the definition interval for Ycw

        //CONSTRAINTS//
        //Each task must be mapped to exactly 1 processing unit in its eligibility set:

        // ∀t, SUM p (X tp) ≤ 1

        //TraceManager.addDev("\nUnique task-CPU mapping constraints (3)");
        BoolExpr[] c_unique_x = new BoolExpr[inputInstance.getModeling().getTasks().size()];
        for (Object tmlTask :  inputInstance.getModeling().getTasks()) {
            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);

            ArithExpr sum_X = ctx.mkAdd(X[t]);
            c_unique_x[t] = ctx.mkLe(sum_X, ctx.mkInt(1));
            // TraceManager.addDev(c_unique_x[t]);

        }


        //Feasible Task map: ∀t, SUM p in F(t) (X tp) = 1
        //TraceManager.addDev("\nFeasible task-CPU mapping constraints (4)");
        BoolExpr[] c_feasibleMapX = new BoolExpr[inputInstance.getModeling().getTasks().size()];
        for (Object tmlTask :  inputInstance.getModeling().getTasks()) {
            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);
            TMLTask taskCast = (TMLTask)tmlTask;
            ArithExpr sum_X = ctx.mkInt(0);

            for (HwNode hwNode : inputInstance.getFeasibleCPUs(taskCast)) {

                int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

                sum_X = ctx.mkAdd(sum_X, X[t][p]);
            }
            c_feasibleMapX[t] = ctx.mkEq(sum_X, ctx.mkInt(1));

            //TraceManager.addDev(c_feasibleMapX[t]);

        }

        //Memory size constraint: ∀p, ∀t, X tp × (bin + bout ) ≤ mem

        //TraceManager.addDev("\nMemory size constraints (5)");
        BoolExpr[][] c_mem = new BoolExpr[inputInstance.getArchitecture().getNbOfCPU()][inputInstance.getModeling().getTasks().size()];

        for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {
            int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

            IntExpr mem = ctx.mkInt(inputInstance.getLocalMemoryOfHwExecutionNode(hwNode).memorySize);


            for (Object tmlTask :  inputInstance.getModeling().getTasks()) {
                int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);
                TMLTask taskCast = (TMLTask)tmlTask;

                IntExpr bin_plus_bout = ctx.mkInt(inputInstance.getBufferIn(taskCast) + inputInstance.getBufferOut(taskCast));
                ArithExpr bin_plus_bout_times_X = ctx.mkMul(bin_plus_bout, X[t][p]);

                c_mem[p][t] = ctx.mkLe(bin_plus_bout_times_X, mem);

                // TraceManager.addDev(c_mem[p][t]);

            }
        }

/*
        //At least one route Constraint: ∀c, (t1,t2) in c; X t1p1 + X t2p2 − r p1p2 ≤ 1
        TraceManager.addDev("\nAt least one route constraints (6)");
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

                    TraceManager.addDev("Channel(" + channel.toString() + ") " + c_route[c]);


                    // mapping_constraints = ctx.mkAnd(c_route[c], mapping_constraints);
                }
            }
        }
*/
        //Scheduling: Precedence constraints: ∀c, τs (t cons ) ≥ τe (t prod )
        // TraceManager.addDev("\nPrecedence constraints (11)");
        BoolExpr[] c_precedence = new BoolExpr[inputInstance.getModeling().getChannels().size()];

        for (Object channel : inputInstance.getModeling().getChannels()) {

            int c = inputInstance.getModeling().getChannels().indexOf(channel);
            TMLChannel channelCast = (TMLChannel) channel;

            //for each channel get producer and consumer
            TMLTask producer = channelCast.getOriginTask();
            int prodIndex = inputInstance.getModeling().getTasks().indexOf(producer);

            TMLTask consumer = channelCast.getDestinationTask();
            int consIndex = inputInstance.getModeling().getTasks().indexOf(consumer);

            ArithExpr wcetProducer = ctx.mkInt(0);

            for (HwExecutionNode hwNode : inputInstance.getFeasibleCPUs(producer)) {
                int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

                wcetProducer = ctx.mkAdd(wcetProducer, ctx.mkMul(X[prodIndex][p], ctx.mkInt(inputInstance.getWCET(producer, hwNode))));

            }

            ArithExpr endProducer = ctx.mkAdd(start[prodIndex], wcetProducer);
            ArithExpr startC_minus_endP = ctx.mkSub(start[consIndex], endProducer);

            c_precedence[c] = ctx.mkGe(startC_minus_endP, ctx.mkInt(0));

            //  TraceManager.addDev(c_precedence[c]);

            mapping_constraints = ctx.mkAnd(mapping_constraints, c_precedence[c]);

        }


        //∀p∀t i ∀t j 6 = t i , ¬((X t i p = 1) ∧ (X t j p = 1)) ∨ ((τ s (t j ) ≥ τ e (t i )) ∨ (τ s (t i ) ≥ τ e (t j )))
        //Scheduling: One single task running on CPU ∀t
        //TraceManager.addDev("\n Same resource/no time overlap Constraints (12)");
        BoolExpr c_monoTask[][][] = new BoolExpr[inputInstance.getArchitecture().getCPUs().size()][inputInstance.getModeling().getTasks().size()][inputInstance.getModeling().getTasks().size()];

        for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {
            int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

            for (Object taski :  inputInstance.getModeling().getTasks()) {

                int ti = inputInstance.getModeling().getTasks().indexOf(taski);
                TMLTask taskiCast = (TMLTask)taski;
                //Calculate End times of ti
                ArithExpr wceti = ctx.mkInt(0);

                for (HwExecutionNode ihwNode : inputInstance.getFeasibleCPUs(taskiCast)) {
                    int ip = inputInstance.getArchitecture().getCPUs().indexOf(ihwNode);

                    wceti = ctx.mkAdd(wceti, ctx.mkMul(X[ti][ip], ctx.mkInt(inputInstance.getWCET(taskiCast, ihwNode))));

                }

                ArithExpr endti = ctx.mkAdd(start[ti], wceti);

                for (Object taskj : inputInstance.getModeling().getTasks()) {

                    int tj = inputInstance.getModeling().getTasks().indexOf(taskj);
                    TMLTask taskjCast = (TMLTask)taskj;


                    if (tj != ti) {
                        //Calculate End times of tj

                        ArithExpr wcetj = ctx.mkInt(0);

                        for (HwExecutionNode jhwNode : inputInstance.getFeasibleCPUs(taskjCast)) {
                            int jp = inputInstance.getArchitecture().getCPUs().indexOf(jhwNode);

                            wcetj = ctx.mkAdd(wcetj, ctx.mkMul(X[tj][jp], ctx.mkInt(inputInstance.getWCET(taskjCast, jhwNode))));


                        }

                        ArithExpr endtj = ctx.mkAdd(start[tj], wcetj);

                        BoolExpr alpha = ctx.mkAnd(ctx.mkEq(X[ti][p], ctx.mkInt(1)), ctx.mkEq(X[tj][p], ctx.mkInt(1)));

                        BoolExpr not_alpha = ctx.mkNot(alpha);

                        BoolExpr beta = ctx.mkOr(ctx.mkGe(start[ti], endtj), ctx.mkGe(start[tj], endti));

                        //
                        c_monoTask[p][ti][tj] = ctx.mkOr(not_alpha, beta);
                        // TraceManager.addDev("\n" + c_monoTask[p][ti][tj]);

                        mapping_constraints = ctx.mkAnd(mapping_constraints, c_monoTask[p][ti][tj]);
                    }
                }

            }

        }


        //Grouping remaining constraints of the model

        // TraceManager.addDev("\nAll mapping constraints");
        for (Object task :  inputInstance.getModeling().getTasks()) {

            int t = inputInstance.getModeling().getTasks().indexOf(task);
            TMLTask taskCast = (TMLTask)task;

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
            Expr[] optimized_result_start = new Expr[inputInstance.getModeling().getTasks().size()];

            TraceManager.addDev("Proposed feasible Mapping solution:");


            for (Object task : inputInstance.getModeling().getTasks()) {
                int t = inputInstance.getModeling().getTasks().indexOf(task);
                TMLTask taskCast = (TMLTask)task;

                for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {
                    int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);


                    optimized_result_X[t][p] = m.evaluate(X[t][p], false);
                    TraceManager.addDev("X[" + taskCast.getName() + "][" + hwNode.getName() + "] = " + optimized_result_X[t][p]);

                }

                TraceManager.addDev("\n");
            }

            for (Object task : inputInstance.getModeling().getTasks()) {
                int t = inputInstance.getModeling().getTasks().indexOf(task);
                TMLTask taskCast = (TMLTask)task;
                optimized_result_start[t] = m.evaluate(start[t], false);
                TraceManager.addDev("start[" + taskCast.getName() + "] = " + optimized_result_start[t]);

            }


        } else {
            TraceManager.addDev("Failed to solve mapping problem");
            throw new TestFailedException();
        }


    }//findFeasibleMapping()


    public void findOptimizedMapping(Context ctx) throws TestFailedException {

        TraceManager.addDev("\nFind an optimized Mapping");

        //Decision variables

        //Definition of Xtp as a matrix. Each cell corresponds to a combination of a task and a processor.
        IntExpr[][] X = new IntExpr[inputInstance.getModeling().getTasks().size()][inputInstance.getArchitecture().getCPUs().size()]; //getCPUs returns only HwCPU elements


        //Definition of start time variable for each task
        IntExpr[] start = new IntExpr[inputInstance.getModeling().getTasks().size()];


        //Mapping Constraints :
        BoolExpr mapping_constraints = ctx.mkTrue();

        //Matrix of constraints to define the interval of Xtp : 0 <= Xtp <= 1
        BoolExpr[][] c_bound_x = new BoolExpr[inputInstance.getModeling().getTasks().size()][inputInstance.getArchitecture().getCPUs().size()];

        //TraceManager.addDev("\nDefining the bounds of Xtp (1)");

        for (Object tmlTask :  inputInstance.getModeling().getTasks()) {

            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);
            TMLTask taskCast = (TMLTask)tmlTask;

            for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {

                int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

                X[t][p] = ctx.mkIntConst("X_" + taskCast.getID() + "_" + hwNode.getID());

                // Constraints: 0 <= Xtp <= 1
                c_bound_x[t][p] = ctx.mkAnd(ctx.mkLe(ctx.mkInt(0), X[t][p]),
                        ctx.mkLe(X[t][p], ctx.mkInt(1)));

                // TraceManager.addDev(c_bound_x[t][p]);
            }
        }

        //Constraint on start >=0
        BoolExpr[] c_bound_start = new BoolExpr[inputInstance.getModeling().getTasks().size()];

        for (Object tmlTask :  inputInstance.getModeling().getTasks()) {
            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);

            TMLTask taskCast = (TMLTask)tmlTask;
            start[t] = ctx.mkIntConst("start_" + taskCast.getID());
            c_bound_start[t] = ctx.mkGe(start[t], ctx.mkInt(0));
            mapping_constraints = ctx.mkAnd(mapping_constraints, c_bound_start[t]);
        }


        //CONSTRAINTS//
        //Each task must be mapped to exactly 1 processing unit in its eligibility set:

        // ∀t, SUM p (X tp) ≤ 1

        //TraceManager.addDev("\nUnique task-CPU mapping constraints (3)");
        BoolExpr[] c_unique_x = new BoolExpr[inputInstance.getModeling().getTasks().size()];
        for (Object tmlTask :  inputInstance.getModeling().getTasks()) {
            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask); // TODO or use the local instance modeling

            ArithExpr sum_X = ctx.mkAdd(X[t]);
            c_unique_x[t] = ctx.mkLe(sum_X, ctx.mkInt(1));
            // TraceManager.addDev(c_unique_x[t]);

        }


        //Feasible Task map: ∀t, SUM p in F(t) (X tp) = 1
        //TraceManager.addDev("\nFeasible task-CPU mapping constraints (4)");
        BoolExpr[] c_feasibleMapX = new BoolExpr[inputInstance.getModeling().getTasks().size()];
        for (Object tmlTask :  inputInstance.getModeling().getTasks()) {
            int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);
            ArithExpr sum_X = ctx.mkInt(0);

            TMLTask taskCast = (TMLTask)tmlTask;

            for (HwNode hwNode : inputInstance.getFeasibleCPUs(taskCast)) {

                int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

                sum_X = ctx.mkAdd(sum_X, X[t][p]);
            }
            c_feasibleMapX[t] = ctx.mkEq(sum_X, ctx.mkInt(1));

            //TraceManager.addDev(c_feasibleMapX[t]);

        }

        //Memory size constraint: ∀p, ∀t, X tp × (bin + bout ) ≤ mem

        //TraceManager.addDev("\nMemory size constraints (5)");
        BoolExpr[][] c_mem = new BoolExpr[inputInstance.getArchitecture().getNbOfCPU()][inputInstance.getModeling().getTasks().size()];

        for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {
            int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

            IntExpr mem = ctx.mkInt(inputInstance.getLocalMemoryOfHwExecutionNode(hwNode).memorySize);


            for (Object tmlTask : inputInstance.getModeling().getTasks()) {
                int t = inputInstance.getModeling().getTasks().indexOf(tmlTask);

                TMLTask taskCast = (TMLTask)tmlTask;

                IntExpr bin_plus_bout = ctx.mkInt(inputInstance.getBufferIn(taskCast) + inputInstance.getBufferOut(taskCast));
                ArithExpr bin_plus_bout_times_X = ctx.mkMul(bin_plus_bout, X[t][p]);

                c_mem[p][t] = ctx.mkLe(bin_plus_bout_times_X, mem);

                // TraceManager.addDev(c_mem[p][t]);

            }
        }

/*
        //At least one route Constraint: ∀c, (t1,t2) in c; X t1p1 + X t2p2 − r p1p2 ≤ 1
        TraceManager.addDev("\nAt least one route constraints (6)");
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

                    TraceManager.addDev("Channel(" + channel.toString() + ") " + c_route[c]);


                    // mapping_constraints = ctx.mkAnd(c_route[c], mapping_constraints);
                }
            }
        }
*/
        //Scheduling: Precedence constraints: ∀c, τs (t cons ) ≥ τe (t prod )
        // TraceManager.addDev("\nPrecedence constraints (11)");
        BoolExpr[] c_precedence = new BoolExpr[inputInstance.getModeling().getChannels().size()];

        for (Object channel :  inputInstance.getModeling().getChannels()) {

            int c = inputInstance.getModeling().getChannels().indexOf(channel);
            //for each channel get producer and consumer
            TMLChannel channelCast = (TMLChannel)channel;
            TMLTask producer = channelCast.getOriginTask();
            int prodIndex = inputInstance.getModeling().getTasks().indexOf(producer);

            TMLTask consumer = channelCast.getDestinationTask();
            int consIndex = inputInstance.getModeling().getTasks().indexOf(consumer);

            //TraceManager.addDev("\nChannel(" + producer.getId() + "," + consumer.getId() + ")");

            ArithExpr wcetProducer = ctx.mkInt(0);

            for (HwExecutionNode hwNode : inputInstance.getFeasibleCPUs(producer)) {
                int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

                wcetProducer = ctx.mkAdd(wcetProducer, ctx.mkMul(X[prodIndex][p], ctx.mkInt(inputInstance.getWCET(producer, hwNode))));

            }

            ArithExpr endProducer = ctx.mkAdd(start[prodIndex], wcetProducer);
            ArithExpr startC_minus_endP = ctx.mkSub(start[consIndex], endProducer);

            c_precedence[c] = ctx.mkGe(startC_minus_endP, ctx.mkInt(0));

            //  TraceManager.addDev(c_precedence[c]);

            mapping_constraints = ctx.mkAnd(mapping_constraints, c_precedence[c]);

        }


        //∀p∀t i ∀t j 6 = t i , ¬((X t i p = 1) ∧ (X t j p = 1)) ∨ ((τ s (t j ) ≥ τ e (t i )) ∨ (τ s (t i ) ≥ τ e (t j )))
        //Scheduling: One single task running on CPU ∀t
        //TraceManager.addDev("\n Same resource/no time overlap Constraints (12)");
        BoolExpr c_monoTask[][][] = new BoolExpr[inputInstance.getArchitecture().getCPUs().size()][inputInstance.getModeling().getTasks().size()][inputInstance.getModeling().getTasks().size()];

        for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {
            int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);

            for (Object taski :  inputInstance.getModeling().getTasks()) {

                int ti = inputInstance.getModeling().getTasks().indexOf(taski);
                //Calculate End times of ti
                ArithExpr wceti = ctx.mkInt(0);

                TMLTask taskiCast = (TMLTask)taski;

                for (HwExecutionNode ihwNode : inputInstance.getFeasibleCPUs(taskiCast)) {
                    int ip = inputInstance.getArchitecture().getCPUs().indexOf(ihwNode);

                    wceti = ctx.mkAdd(wceti, ctx.mkMul(X[ti][ip], ctx.mkInt(inputInstance.getWCET(taskiCast, ihwNode))));

                }

                ArithExpr endti = ctx.mkAdd(start[ti], wceti);

                for (Object taskj :  inputInstance.getModeling().getTasks()) {

                    int tj = inputInstance.getModeling().getTasks().indexOf(taskj);


                    if (tj != ti) {
                        //Calculate End times of tj

                        ArithExpr wcetj = ctx.mkInt(0);

                        TMLTask taskjCast = (TMLTask)taskj;


                        for (HwExecutionNode jhwNode : inputInstance.getFeasibleCPUs(taskjCast)) {
                            int jp = inputInstance.getArchitecture().getCPUs().indexOf(jhwNode);

                            wcetj = ctx.mkAdd(wcetj, ctx.mkMul(X[tj][jp], ctx.mkInt(inputInstance.getWCET(taskjCast, jhwNode))));


                        }

                        ArithExpr endtj = ctx.mkAdd(start[tj], wcetj);

                        BoolExpr alpha = ctx.mkAnd(ctx.mkEq(X[ti][p], ctx.mkInt(1)), ctx.mkEq(X[tj][p], ctx.mkInt(1)));

                        BoolExpr not_alpha = ctx.mkNot(alpha);

                        BoolExpr beta = ctx.mkOr(ctx.mkGe(start[ti], endtj), ctx.mkGe(start[tj], endti));

                        //
                        c_monoTask[p][ti][tj] = ctx.mkOr(not_alpha, beta);
                        // TraceManager.addDev("\n" + c_monoTask[p][ti][tj]);

                        mapping_constraints = ctx.mkAnd(mapping_constraints, c_monoTask[p][ti][tj]);
                    }
                }

            }

        }


        //Grouping remaining constraints of the model

        // TraceManager.addDev("\nAll mapping constraints");
        for (Object task : inputInstance.getModeling().getTasks()) {

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

        //OPTIMIZE CONTEXT

        //Create an Optimize context
        Optimize opt = ctx.mkOptimize();

        //Assert a constraints into the optimize solver
        opt.Add(mapping_constraints);


        // Objective: minimize latency

        //Creating an expression for latency

        //Calculating end time of last task
        TMLTask finalTask = inputInstance.getFinalTask(inputInstance.getModeling());
        int finalTaskIndex = inputInstance.getModeling().getTasks().indexOf(inputInstance.getFinalTask(inputInstance.getModeling()));
        ArithExpr wcet_last = ctx.mkInt(0);
        int l = 0;
        for (HwExecutionNode hwNode : inputInstance.getFeasibleCPUs(inputInstance.getFinalTask(inputInstance.getModeling()))) {
            int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);
            wcet_last = ctx.mkAdd(wcet_last, ctx.mkMul(X[finalTaskIndex][p], ctx.mkInt(inputInstance.getWCET(finalTask, hwNode))));
            l += 1;

        }

        ArithExpr end_last = ctx.mkAdd(start[finalTaskIndex], wcet_last);

        ArithExpr latency = ctx.mkSub(end_last, ctx.mkInt(0));


        // Add minimization objective.
        Optimize.Handle objective_latency = opt.MkMinimize(latency);

        String outputToDisplay;

        if (opt.Check() == Status.SATISFIABLE) {
            Model m = opt.getModel();
            Expr[][] optimized_result_X = new Expr[inputInstance.getModeling().getTasks().size()][inputInstance.getArchitecture().getCPUs().size()];
            Expr[] optimized_result_start = new Expr[inputInstance.getModeling().getTasks().size()];

            outputToDisplay ="The optimal mapping solution is:\n\n";

            for ( Object task : inputInstance.getModeling().getTasks()) {
                int t = inputInstance.getModeling().getTasks().indexOf(task);

                for (HwNode hwNode : inputInstance.getArchitecture().getCPUs()) {
                    int p = inputInstance.getArchitecture().getCPUs().indexOf(hwNode);
                    //evaluate optimal solution
                    optimized_result_X[t][p] = m.evaluate(X[t][p], true);
                    TMLTask taskCast = (TMLTask)task;
                    optimizedSolutionX.put("X[" + taskCast.getName() + "][" + hwNode.getName() + "] = ", Integer.parseInt
                            (optimized_result_X[t][p]
                            .toString()));

                   // TraceManager.addDev("X[" + task.getName() + "][" + hwNode.getName() + "] = " + optimized_result_X[t][p]);
                }

                TraceManager.addDev("\n");
            }

            for (Object task :  inputInstance.getModeling().getTasks()) {
                int t = inputInstance.getModeling().getTasks().indexOf(task);

                optimized_result_start[t] = m.evaluate(start[t], false);
                TMLTask taskCast = (TMLTask)task;

                optimizedSolutionStart.put("start[" + taskCast.getName() + "] = ", Integer.parseInt(optimized_result_start[t].toString()));

               // TraceManager.addDev("start[" + task.getName() + "] = " + optimized_result_start[t]);

            }

            for(Map.Entry<String, Integer> entry : optimizedSolutionX.entrySet()) {
                outputToDisplay = outputToDisplay + entry.getKey() + " = " + entry.getValue() + "\n";
            }

            outputToDisplay += "\n\n";

            for(Map.Entry<String, Integer> entry : optimizedSolutionStart.entrySet()) {
                outputToDisplay = outputToDisplay + entry.getKey() + " = " + entry.getValue() + "\n";
            }

            TraceManager.addDev(outputToDisplay);

        } else {
            outputToDisplay ="Failed to solve mapping problem";
            TraceManager.addDev(outputToDisplay);
            throw new TestFailedException();
        }

    }


    class TestFailedException extends Exception {
        public TestFailedException() {
            super("Check FAILED");
        }
    }


}
