#include <Simulator.h>
#include <AliasConstraint.h>
#include <EqConstraint.h>
#include <LogConstraint.h>
#include <PropLabConstraint.h>
#include <PropRelConstraint.h>
#include <SeqConstraint.h>
#include <SignalConstraint.h>
#include <TimeMMConstraint.h>
#include <TimeTConstraint.h>
#include <CPU.h>
#include <SingleCoreCPU.h>
#include <RRScheduler.h>
#include <RRPrioScheduler.h>
#include <PrioScheduler.h>
#include <Bus.h>
#include <Bridge.h>
#include <Memory.h>
#include <TMLbrbwChannel.h>
#include <TMLnbrnbwChannel.h>
#include <TMLbrnbwChannel.h>
#include <TMLEventBChannel.h>
#include <TMLEventFChannel.h>
#include <TMLEventFBChannel.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>
#include <TMLTask.h>
#include <SimComponents.h>
#include <Server.h>
#include <SimServSyncInfo.h>
#include <ListenersSimCmd.h>
#include <DIPLODOCUS_C_Design__Alice.h>
#include <DIPLODOCUS_C_Design__Bob.h>


class CurrentComponents: public SimComponents{
    public:
    CurrentComponents():SimComponents(1379091812){
        //Declaration of CPUs
        RRScheduler* CPU1_scheduler = new RRScheduler("CPU1_RRSched", 0, 2000000, 2 ) ;
        CPU* CPU10 = new SingleCoreCPU(1, "CPU1_0", CPU1_scheduler, 1, 1, 1, 5, 20, 2, 10, 10, 4);
        RRScheduler* CPU0_scheduler = new RRScheduler("CPU0_RRSched", 0, 2000000, 2 ) ;
        CPU* CPU00 = new SingleCoreCPU(2, "CPU0_0", CPU0_scheduler, 1, 1, 1, 5, 20, 2, 10, 10, 4);
        
        //Declaration of Buses
        Bus* BusNORD_0 = new Bus(3,"BusNORD_0",0, 100, 4, 1,false);
        addBus(BusNORD_0);
        Bus* BusSUD_0 = new Bus(4,"BusSUD_0",0, 100, 4, 1,false);
        addBus(BusSUD_0);
        
        //Declaration of Bridges
        
        //Declaration of Memories
        Memory* Memory0 = new Memory(5,"Memory0", 1, 4);
        addMem(Memory0);
        
        //Declaration of Bus masters
        BusMaster* CPU10_BusNORD_Master = new BusMaster("CPU10_BusNORD_Master", 0, 1, array(1, (SchedulableCommDevice*)BusNORD_0));
        CPU10->addBusMaster(CPU10_BusNORD_Master);
        BusMaster* CPU10_BusSUD_Master = new BusMaster("CPU10_BusSUD_Master", 0, 1, array(1, (SchedulableCommDevice*)BusSUD_0));
        CPU10->addBusMaster(CPU10_BusSUD_Master);
        BusMaster* CPU00_BusNORD_Master = new BusMaster("CPU00_BusNORD_Master", 0, 1, array(1, (SchedulableCommDevice*)BusNORD_0));
        CPU00->addBusMaster(CPU00_BusNORD_Master);
        BusMaster* CPU00_BusSUD_Master = new BusMaster("CPU00_BusSUD_Master", 0, 1, array(1, (SchedulableCommDevice*)BusSUD_0));
        CPU00->addBusMaster(CPU00_BusSUD_Master);
        
        //Declaration of channels
        TMLnbrnbwChannel* channel__DIPLODOCUS_C_Design__Phone = new TMLnbrnbwChannel(10,"DIPLODOCUS_C_Design__Phone",4,2, array(2,CPU00_BusSUD_Master,CPU10_BusSUD_Master), array(2,static_cast<Slave*>(Memory0),static_cast<Slave*>(Memory0)),0);
        addChannel(channel__DIPLODOCUS_C_Design__Phone);
        
        //Declaration of events
        TMLEventBChannel<ParamType,0>* event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call = new TMLEventBChannel<ParamType,0>(11,"DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call",0,0,0,0,false,false);
        addEvent(event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call);
        TMLEventBChannel<ParamType,0>* event__DIPLODOCUS_C_Design__comm__DIPLODOCUS_C_Design__comm = new TMLEventBChannel<ParamType,0>(12,"DIPLODOCUS_C_Design__comm__DIPLODOCUS_C_Design__comm",0,0,0,0,false,false);
        addEvent(event__DIPLODOCUS_C_Design__comm__DIPLODOCUS_C_Design__comm);
        
        //Declaration of requests
        
        //Set bus schedulers
        BusNORD_0->setScheduler((WorkloadSource*) new RRScheduler("BusNORD_RRSched", 0, 5, 1, array(2, (WorkloadSource*)CPU10_BusNORD_Master, (WorkloadSource*)CPU00_BusNORD_Master), 2));
        BusSUD_0->setScheduler((WorkloadSource*) new RRScheduler("BusSUD_RRSched", 0, 5, 1, array(2, (WorkloadSource*)CPU10_BusSUD_Master, (WorkloadSource*)CPU00_BusSUD_Master), 2));
        
        //Declaration of tasks
        DIPLODOCUS_C_Design__Alice* task__DIPLODOCUS_C_Design__Alice = new DIPLODOCUS_C_Design__Alice(6,0,"DIPLODOCUS_C_Design__Alice", array(1,CPU10),1
        ,channel__DIPLODOCUS_C_Design__Phone
        ,event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call
        ,event__DIPLODOCUS_C_Design__comm__DIPLODOCUS_C_Design__comm
        );
        addTask(task__DIPLODOCUS_C_Design__Alice);
        DIPLODOCUS_C_Design__Bob* task__DIPLODOCUS_C_Design__Bob = new DIPLODOCUS_C_Design__Bob(8,0,"DIPLODOCUS_C_Design__Bob", array(1,CPU00),1
        ,channel__DIPLODOCUS_C_Design__Phone
        ,event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call
        ,event__DIPLODOCUS_C_Design__comm__DIPLODOCUS_C_Design__comm
        );
        addTask(task__DIPLODOCUS_C_Design__Bob);
        
    }
    
    void generateTEPEs(){
        //Declaration of TEPEs
        
    }
};

#include <main.h>
