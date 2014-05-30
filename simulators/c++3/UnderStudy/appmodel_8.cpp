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

/* As in 7 but now Phone is mapped to BusOVEST. This info is taken into account by the simulator src code, despite there's no
 * mapping on the bridges. So apparently this time the simulator src code takes mapping of channels into account. */

class CurrentComponents: public SimComponents{
    public:
    CurrentComponents():SimComponents(1229353063){
        //Declaration of CPUs
        RRScheduler* CPU1_scheduler = new RRScheduler("CPU1_RRSched", 0, 2000000, 2 ) ;
        CPU* CPU10 = new SingleCoreCPU(2, "CPU1_0", CPU1_scheduler, 1, 1, 1, 5, 20, 2, 10, 10, 4);
        addCPU(CPU10);
        RRScheduler* CPU0_scheduler = new RRScheduler("CPU0_RRSched", 0, 2000000, 2 ) ;
        CPU* CPU00 = new SingleCoreCPU(3, "CPU0_0", CPU0_scheduler, 1, 1, 1, 5, 20, 2, 10, 10, 4);
        addCPU(CPU00);
        
        //Declaration of Buses
        Bus* BusNORD_0 = new Bus(1,"BusNORD_0",0, 100, 4, 1,false);
        addBus(BusNORD_0);
        Bus* BusEST_0 = new Bus(4,"BusEST_0",0, 100, 4, 1,false);
        addBus(BusEST_0);
        Bus* BusOVEST_0 = new Bus(5,"BusOVEST_0",0, 100, 4, 1,false);
        addBus(BusOVEST_0);
        Bus* BusSUD_0 = new Bus(8,"BusSUD_0",0, 100, 4, 1,false);
        addBus(BusSUD_0);
        
        //Declaration of Bridges
        Bridge* BridgeSUD = new Bridge(6,"BridgeSUD", 1, 4);
        addBridge(BridgeSUD);
        Bridge* BridgeNORD = new Bridge(7,"BridgeNORD", 1, 4);
        addBridge(BridgeNORD);
        
        //Declaration of Memories
        
        //Declaration of Bus masters
        BusMaster* CPU10_BusSUD_Master = new BusMaster("CPU10_BusSUD_Master", 0, 1, array(1, (SchedulableCommDevice*)BusSUD_0));
        CPU10->addBusMaster(CPU10_BusSUD_Master);
        BusMaster* CPU00_BusNORD_Master = new BusMaster("CPU00_BusNORD_Master", 0, 1, array(1, (SchedulableCommDevice*)BusNORD_0));
        CPU00->addBusMaster(CPU00_BusNORD_Master);
        BusMaster* BridgeSUD_BusSUD_Master = new BusMaster("BridgeSUD_BusSUD_Master", 0, 1, array(1, (SchedulableCommDevice*)BusSUD_0));
        BridgeSUD->addBusMaster(BridgeSUD_BusSUD_Master);
        BusMaster* BridgeSUD_BusEST_Master = new BusMaster("BridgeSUD_BusEST_Master", 0, 1, array(1, (SchedulableCommDevice*)BusEST_0));
        BridgeSUD->addBusMaster(BridgeSUD_BusEST_Master);
        BusMaster* BridgeSUD_BusOVEST_Master = new BusMaster("BridgeSUD_BusOVEST_Master", 0, 1, array(1, (SchedulableCommDevice*)BusOVEST_0));
        BridgeSUD->addBusMaster(BridgeSUD_BusOVEST_Master);
        BusMaster* BridgeNORD_BusEST_Master = new BusMaster("BridgeNORD_BusEST_Master", 0, 1, array(1, (SchedulableCommDevice*)BusEST_0));
        BridgeNORD->addBusMaster(BridgeNORD_BusEST_Master);
        BusMaster* BridgeNORD_BusOVEST_Master = new BusMaster("BridgeNORD_BusOVEST_Master", 0, 1, array(1, (SchedulableCommDevice*)BusOVEST_0));
        BridgeNORD->addBusMaster(BridgeNORD_BusOVEST_Master);
        BusMaster* BridgeNORD_BusNORD_Master = new BusMaster("BridgeNORD_BusNORD_Master", 0, 1, array(1, (SchedulableCommDevice*)BusNORD_0));
        BridgeNORD->addBusMaster(BridgeNORD_BusNORD_Master);
        
        //Declaration of channels
        TMLnbrnbwChannel* channel__DIPLODOCUS_C_Design__Phone = new TMLnbrnbwChannel(13,"DIPLODOCUS_C_Design__Phone",4,6,array(6,CPU00_BusNORD_Master,BridgeNORD_BusOVEST_Master,BridgeSUD_BusSUD_Master,BridgeNORD_BusNORD_Master,BridgeSUD_BusOVEST_Master,CPU10_BusSUD_Master),array(6,static_cast<Slave*>(BridgeNORD),static_cast<Slave*>(BridgeSUD),static_cast<Slave*>(0),static_cast<Slave*>(0),static_cast<Slave*>(BridgeNORD),static_cast<Slave*>(BridgeSUD)),0);
        addChannel(channel__DIPLODOCUS_C_Design__Phone);
        
        //Declaration of events
        
        //Declaration of requests
        
        //Set bus schedulers
        BusNORD_0->setScheduler((WorkloadSource*) new RRScheduler("BusNORD_RRSched", 0, 5, 1, array(2, (WorkloadSource*)BridgeNORD_BusNORD_Master, (WorkloadSource*)CPU00_BusNORD_Master), 2));
        BusEST_0->setScheduler((WorkloadSource*) new RRScheduler("BusEST_RRSched", 0, 5, 1, array(2, (WorkloadSource*)BridgeSUD_BusEST_Master, (WorkloadSource*)BridgeNORD_BusEST_Master), 2));
        BusOVEST_0->setScheduler((WorkloadSource*) new RRScheduler("BusOVEST_RRSched", 0, 5, 1, array(2, (WorkloadSource*)BridgeSUD_BusOVEST_Master, (WorkloadSource*)BridgeNORD_BusOVEST_Master), 2));
        BusSUD_0->setScheduler((WorkloadSource*) new RRScheduler("BusSUD_RRSched", 0, 5, 1, array(2, (WorkloadSource*)CPU10_BusSUD_Master, (WorkloadSource*)BridgeSUD_BusSUD_Master), 2));
        
        //Declaration of tasks
        DIPLODOCUS_C_Design__Alice* task__DIPLODOCUS_C_Design__Alice = new DIPLODOCUS_C_Design__Alice(9,0,"DIPLODOCUS_C_Design__Alice", array(1,CPU10),1
        ,channel__DIPLODOCUS_C_Design__Phone
        );
        addTask(task__DIPLODOCUS_C_Design__Alice);
        DIPLODOCUS_C_Design__Bob* task__DIPLODOCUS_C_Design__Bob = new DIPLODOCUS_C_Design__Bob(11,0,"DIPLODOCUS_C_Design__Bob", array(1,CPU00),1
        ,channel__DIPLODOCUS_C_Design__Phone
        );
        addTask(task__DIPLODOCUS_C_Design__Bob);
        
    }
    
    void generateTEPEs(){
        //Declaration of TEPEs
        
    }
};

#include <main.h>
