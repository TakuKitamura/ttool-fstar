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
#include <HOCAppMAPPED__fork3.h>
#include <HOCAppMAPPED__F_DMAcws.h>
#include <HOCAppMAPPED__F_DMAvsum.h>
#include <HOCAppMAPPED__F_DMAcwm2.h>
#include <HOCAppMAPPED__F_DMAfork1.h>
#include <HOCAppMAPPED__F_DMAsink.h>
#include <HOCAppMAPPED__join1.h>
#include <HOCAppMAPPED__join2.h>
#include <HOCAppMAPPED__fork1.h>
#include <HOCAppMAPPED__fork2.h>
#include <HOCAppMAPPED__F_src.h>
#include <HOCAppMAPPED__F_acc.h>
#include <HOCAppMAPPED__F_cws.h>
#include <HOCAppMAPPED__F_vsum.h>
#include <HOCAppMAPPED__F_cwm1.h>
#include <HOCAppMAPPED__F_cwm2.h>
#include <HOCAppMAPPED__X_acc.h>
#include <HOCAppMAPPED__SINK.h>
#include <HOCAppMAPPED__X_vsum.h>
#include <HOCAppMAPPED__X_cwm1.h>
#include <HOCAppMAPPED__X_cws.h>
#include <HOCAppMAPPED__X_cwm2.h>
#include <HOCAppMAPPED__X_src.h>
#include <HOCAppMAPPED__X_DMAcwm2.h>
#include <HOCAppMAPPED__X_DMAfork1.h>
#include <HOCAppMAPPED__X_DMAcws.h>
#include <HOCAppMAPPED__X_DMAvsum.h>
#include <HOCAppMAPPED__X_DMAsink.h>


class CurrentComponents: public SimComponents{
    public:
    CurrentComponents():SimComponents(-959273399){
        //Declaration of CPUs
        RRScheduler* MAINcpu_scheduler = new RRScheduler("MAINcpu_RRSched", 0, 2000000, 2 ) ;
        CPU* MAINcpu0 = new SingleCoreCPU(3, "MAINcpu_0", MAINcpu_scheduler, 1, 1, 1, 5, 20, 2, 10, 10, 4);
        addCPU(MAINcpu0);
        RRScheduler* FEP_scheduler = new RRScheduler("FEP_RRSched", 0, 2000000, 2 ) ;
        CPU* FEP0 = new SingleCoreCPU(9, "FEP_0", FEP_scheduler, 1, 1, 1, 5, 20, 2, 10, 10, 4);
        addCPU(FEP0);
        RRScheduler* ADAIFEMdma_scheduler = new RRScheduler("ADAIFEMdma_RRSched", 0, 2000000, 2 ) ;
        CPU* ADAIFEMdma0 = new SingleCoreCPU(11, "ADAIFEMdma_0", ADAIFEMdma_scheduler, 1, 1, 1, 5, 20, 2, 10, 10, 4);
        addCPU(ADAIFEMdma0);
        RRScheduler* ADAIFEM_scheduler = new RRScheduler("ADAIFEM_RRSched", 0, 2000000, 2 ) ;
        CPU* ADAIFEM0 = new SingleCoreCPU(14, "ADAIFEM_0", ADAIFEM_scheduler, 1, 1, 1, 5, 20, 2, 10, 10, 4);
        addCPU(ADAIFEM0);
        RRScheduler* BRIDGEdma_scheduler = new RRScheduler("BRIDGEdma_RRSched", 0, 2000000, 2 ) ;
        CPU* BRIDGEdma0 = new SingleCoreCPU(15, "BRIDGEdma_0", BRIDGEdma_scheduler, 1, 1, 1, 5, 20, 2, 10, 10, 4);
        addCPU(BRIDGEdma0);
        RRScheduler* FEPdma_scheduler = new RRScheduler("FEPdma_RRSched", 0, 2000000, 2 ) ;
        CPU* FEPdma0 = new SingleCoreCPU(16, "FEPdma_0", FEPdma_scheduler, 1, 1, 1, 5, 20, 2, 10, 10, 4);
        addCPU(FEPdma0);
        
        //Declaration of Buses
        Bus* MAINbus_0 = new Bus(4,"MAINbus_0",0, 100, 4, 1,false);
        addBus(MAINbus_0);
        Bus* Crossbar_0 = new Bus(6,"Crossbar_0",0, 100, 4, 1,false);
        addBus(Crossbar_0);
        Bus* FEPbus_0 = new Bus(8,"FEPbus_0",0, 100, 4, 1,false);
        addBus(FEPbus_0);
        Bus* ADAIFEMbus_0 = new Bus(13,"ADAIFEMbus_0",0, 100, 4, 1,false);
        addBus(ADAIFEMbus_0);
        
        //Declaration of Bridges
        Bridge* FEPbridge = new Bridge(1,"FEPbridge", 1, 4);
        addBridge(FEPbridge);
        Bridge* MAINbridge = new Bridge(5,"MAINbridge", 1, 4);
        addBridge(MAINbridge);
        Bridge* ADAIFEMbridge = new Bridge(10,"ADAIFEMbridge", 1, 4);
        addBridge(ADAIFEMbridge);
        
        //Declaration of Memories
        Memory* MAINmemory = new Memory(2,"MAINmemory", 1, 4);
        addMem(MAINmemory);
        Memory* FEPmemory = new Memory(7,"FEPmemory", 1, 4);
        addMem(FEPmemory);
        Memory* ADAIFEMmemory = new Memory(12,"ADAIFEMmemory", 1, 4);
        addMem(ADAIFEMmemory);
        
        //Declaration of Bus masters
        BusMaster* FEPbridge_Crossbar_Master = new BusMaster("FEPbridge_Crossbar_Master", 0, 1, array(1, (SchedulableCommDevice*)Crossbar_0));
        FEPbridge->addBusMaster(FEPbridge_Crossbar_Master);
        BusMaster* FEPbridge_FEPbus_Master = new BusMaster("FEPbridge_FEPbus_Master", 0, 1, array(1, (SchedulableCommDevice*)FEPbus_0));
        FEPbridge->addBusMaster(FEPbridge_FEPbus_Master);
        BusMaster* MAINcpu0_MAINbus_Master = new BusMaster("MAINcpu0_MAINbus_Master", 0, 1, array(1, (SchedulableCommDevice*)MAINbus_0));
        MAINcpu0->addBusMaster(MAINcpu0_MAINbus_Master);
        BusMaster* MAINbridge_MAINbus_Master = new BusMaster("MAINbridge_MAINbus_Master", 0, 1, array(1, (SchedulableCommDevice*)MAINbus_0));
        MAINbridge->addBusMaster(MAINbridge_MAINbus_Master);
        BusMaster* MAINbridge_Crossbar_Master = new BusMaster("MAINbridge_Crossbar_Master", 0, 1, array(1, (SchedulableCommDevice*)Crossbar_0));
        MAINbridge->addBusMaster(MAINbridge_Crossbar_Master);
        BusMaster* FEP0_FEPbus_Master = new BusMaster("FEP0_FEPbus_Master", 0, 1, array(1, (SchedulableCommDevice*)FEPbus_0));
        FEP0->addBusMaster(FEP0_FEPbus_Master);
        BusMaster* ADAIFEMbridge_ADAIFEMbus_Master = new BusMaster("ADAIFEMbridge_ADAIFEMbus_Master", 0, 1, array(1, (SchedulableCommDevice*)ADAIFEMbus_0));
        ADAIFEMbridge->addBusMaster(ADAIFEMbridge_ADAIFEMbus_Master);
        BusMaster* ADAIFEMbridge_Crossbar_Master = new BusMaster("ADAIFEMbridge_Crossbar_Master", 0, 1, array(1, (SchedulableCommDevice*)Crossbar_0));
        ADAIFEMbridge->addBusMaster(ADAIFEMbridge_Crossbar_Master);
        BusMaster* ADAIFEMdma0_Crossbar_Master = new BusMaster("ADAIFEMdma0_Crossbar_Master", 0, 1, array(1, (SchedulableCommDevice*)Crossbar_0));
        ADAIFEMdma0->addBusMaster(ADAIFEMdma0_Crossbar_Master);
        BusMaster* ADAIFEMdma0_ADAIFEMbus_Master = new BusMaster("ADAIFEMdma0_ADAIFEMbus_Master", 0, 1, array(1, (SchedulableCommDevice*)ADAIFEMbus_0));
        ADAIFEMdma0->addBusMaster(ADAIFEMdma0_ADAIFEMbus_Master);
        BusMaster* ADAIFEM0_ADAIFEMbus_Master = new BusMaster("ADAIFEM0_ADAIFEMbus_Master", 0, 1, array(1, (SchedulableCommDevice*)ADAIFEMbus_0));
        ADAIFEM0->addBusMaster(ADAIFEM0_ADAIFEMbus_Master);
        BusMaster* BRIDGEdma0_MAINbus_Master = new BusMaster("BRIDGEdma0_MAINbus_Master", 0, 1, array(1, (SchedulableCommDevice*)MAINbus_0));
        BRIDGEdma0->addBusMaster(BRIDGEdma0_MAINbus_Master);
        BusMaster* BRIDGEdma0_Crossbar_Master = new BusMaster("BRIDGEdma0_Crossbar_Master", 0, 1, array(1, (SchedulableCommDevice*)Crossbar_0));
        BRIDGEdma0->addBusMaster(BRIDGEdma0_Crossbar_Master);
        BusMaster* FEPdma0_FEPbus_Master = new BusMaster("FEPdma0_FEPbus_Master", 0, 1, array(1, (SchedulableCommDevice*)FEPbus_0));
        FEPdma0->addBusMaster(FEPdma0_FEPbus_Master);
        BusMaster* FEPdma0_Crossbar_Master = new BusMaster("FEPdma0_Crossbar_Master", 0, 1, array(1, (SchedulableCommDevice*)Crossbar_0));
        FEPdma0->addBusMaster(FEPdma0_Crossbar_Master);
        
        //Declaration of channels
        TMLbrbwChannel* channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2 = new TMLbrbwChannel(124,"HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2",8,2,array(2,BRIDGEdma0_MAINbus_Master,MAINcpu0_MAINbus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2);
        TMLbrbwChannel* channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3 = new TMLbrbwChannel(126,"HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3",8,2,array(2,BRIDGEdma0_MAINbus_Master,MAINcpu0_MAINbus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3);
        TMLbrbwChannel* channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in = new TMLbrbwChannel(125,"HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in",8,2,array(2,BRIDGEdma0_MAINbus_Master,MAINcpu0_MAINbus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2 = new TMLbrbwChannel(128,"HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2",8,2,array(2,BRIDGEdma0_MAINbus_Master,MAINcpu0_MAINbus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2);
        TMLbrbwChannel* channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1 = new TMLbrbwChannel(127,"HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1",8,2,array(2,BRIDGEdma0_MAINbus_Master,MAINcpu0_MAINbus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1);
        TMLbrbwChannel* channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1 = new TMLbrbwChannel(118,"HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1",8,2,array(2,MAINcpu0_MAINbus_Master,MAINcpu0_MAINbus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1);
        TMLbrbwChannel* channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in = new TMLbrbwChannel(120,"HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in",8,6,array(6,FEP0_FEPbus_Master,FEPbridge_Crossbar_Master,MAINbridge_MAINbus_Master,FEPbridge_FEPbus_Master,MAINbridge_Crossbar_Master,MAINcpu0_MAINbus_Master),array(6,static_cast<Slave*>(FEPbridge),static_cast<Slave*>(MAINbridge),static_cast<Slave*>(0),static_cast<Slave*>(0),static_cast<Slave*>(FEPbridge),static_cast<Slave*>(MAINbridge)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in = new TMLbrbwChannel(122,"HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in",8,4,array(4,FEP0_FEPbus_Master,FEPbridge_Crossbar_Master,FEPbridge_FEPbus_Master,BRIDGEdma0_Crossbar_Master),array(4,static_cast<Slave*>(FEPbridge),static_cast<Slave*>(0),static_cast<Slave*>(0),static_cast<Slave*>(FEPbridge)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in = new TMLbrbwChannel(121,"HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in",8,4,array(4,FEP0_FEPbus_Master,FEPbridge_Crossbar_Master,FEPbridge_FEPbus_Master,BRIDGEdma0_Crossbar_Master),array(4,static_cast<Slave*>(FEPbridge),static_cast<Slave*>(0),static_cast<Slave*>(0),static_cast<Slave*>(FEPbridge)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in = new TMLbrbwChannel(115,"HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in",8,2,array(2,MAINcpu0_MAINbus_Master,BRIDGEdma0_MAINbus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in = new TMLbrbwChannel(114,"HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in",8,2,array(2,MAINcpu0_MAINbus_Master,BRIDGEdma0_MAINbus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in = new TMLbrbwChannel(116,"HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in",8,6,array(6,MAINcpu0_MAINbus_Master,MAINbridge_Crossbar_Master,FEPbridge_FEPbus_Master,MAINbridge_MAINbus_Master,FEPbridge_Crossbar_Master,FEP0_FEPbus_Master),array(6,static_cast<Slave*>(MAINbridge),static_cast<Slave*>(FEPbridge),static_cast<Slave*>(0),static_cast<Slave*>(0),static_cast<Slave*>(MAINbridge),static_cast<Slave*>(FEPbridge)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in = new TMLbrbwChannel(117,"HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in",8,6,array(6,MAINcpu0_MAINbus_Master,MAINbridge_Crossbar_Master,FEPbridge_FEPbus_Master,MAINbridge_MAINbus_Master,FEPbridge_Crossbar_Master,FEP0_FEPbus_Master),array(6,static_cast<Slave*>(MAINbridge),static_cast<Slave*>(FEPbridge),static_cast<Slave*>(0),static_cast<Slave*>(0),static_cast<Slave*>(MAINbridge),static_cast<Slave*>(FEPbridge)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in = new TMLbrbwChannel(110,"HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in",8,6,array(6,MAINcpu0_MAINbus_Master,MAINbridge_Crossbar_Master,FEPbridge_FEPbus_Master,MAINbridge_MAINbus_Master,FEPbridge_Crossbar_Master,FEP0_FEPbus_Master),array(6,static_cast<Slave*>(MAINbridge),static_cast<Slave*>(FEPbridge),static_cast<Slave*>(0),static_cast<Slave*>(0),static_cast<Slave*>(MAINbridge),static_cast<Slave*>(FEPbridge)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in = new TMLbrbwChannel(111,"HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in",8,6,array(6,MAINcpu0_MAINbus_Master,MAINbridge_Crossbar_Master,FEPbridge_FEPbus_Master,MAINbridge_MAINbus_Master,FEPbridge_Crossbar_Master,FEP0_FEPbus_Master),array(6,static_cast<Slave*>(MAINbridge),static_cast<Slave*>(FEPbridge),static_cast<Slave*>(0),static_cast<Slave*>(0),static_cast<Slave*>(MAINbridge),static_cast<Slave*>(FEPbridge)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in = new TMLbrbwChannel(112,"HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in",8,2,array(2,MAINcpu0_MAINbus_Master,MAINcpu0_MAINbus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),49152,0,0);
        addChannel(channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in = new TMLbrbwChannel(113,"HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in",8,2,array(2,MAINcpu0_MAINbus_Master,MAINcpu0_MAINbus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in = new TMLbrbwChannel(123,"HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in",8,6,array(6,ADAIFEM0_ADAIFEMbus_Master,ADAIFEMbridge_Crossbar_Master,MAINbridge_MAINbus_Master,ADAIFEMbridge_ADAIFEMbus_Master,MAINbridge_Crossbar_Master,MAINcpu0_MAINbus_Master),array(6,static_cast<Slave*>(ADAIFEMbridge),static_cast<Slave*>(MAINbridge),static_cast<Slave*>(0),static_cast<Slave*>(0),static_cast<Slave*>(ADAIFEMbridge),static_cast<Slave*>(MAINbridge)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in = new TMLbrbwChannel(119,"HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in",8,4,array(4,FEP0_FEPbus_Master,FEPbridge_Crossbar_Master,FEPbridge_FEPbus_Master,BRIDGEdma0_Crossbar_Master),array(4,static_cast<Slave*>(FEPbridge),static_cast<Slave*>(0),static_cast<Slave*>(0),static_cast<Slave*>(FEPbridge)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in);
        
        //Declaration of events
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2 = new TMLEventBChannel<ParamType,1>(133,"HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3 = new TMLEventBChannel<ParamType,1>(131,"HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in = new TMLEventBChannel<ParamType,1>(134,"HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2 = new TMLEventBChannel<ParamType,1>(135,"HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1 = new TMLEventBChannel<ParamType,1>(132,"HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1 = new TMLEventBChannel<ParamType,1>(143,"HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in = new TMLEventBChannel<ParamType,1>(146,"HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in = new TMLEventBChannel<ParamType,1>(147,"HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in = new TMLEventBChannel<ParamType,1>(144,"HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in = new TMLEventBChannel<ParamType,1>(138,"HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in = new TMLEventBChannel<ParamType,1>(139,"HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in = new TMLEventBChannel<ParamType,1>(140,"HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in = new TMLEventBChannel<ParamType,1>(141,"HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in = new TMLEventBChannel<ParamType,1>(130,"HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in = new TMLEventBChannel<ParamType,1>(129,"HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in);
        TMLEventFBChannel<ParamType,1>* event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in = new TMLEventFBChannel<ParamType,1>(136,"HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in",0,0,0,8,0);
        addEvent(event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in = new TMLEventBChannel<ParamType,1>(137,"HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in = new TMLEventBChannel<ParamType,1>(142,"HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in = new TMLEventBChannel<ParamType,1>(145,"HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in);
        
        //Declaration of requests
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_DMAcwm2 = new TMLEventBChannel<ParamType,1>(160,"reqChannelHOCAppMAPPED__X_DMAcwm2",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_DMAcwm2);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_DMAcws = new TMLEventBChannel<ParamType,1>(164,"reqChannelHOCAppMAPPED__X_DMAcws",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_DMAcws);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_DMAfork1 = new TMLEventBChannel<ParamType,1>(162,"reqChannelHOCAppMAPPED__X_DMAfork1",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_DMAfork1);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_DMAsink = new TMLEventBChannel<ParamType,1>(168,"reqChannelHOCAppMAPPED__X_DMAsink",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_DMAsink);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_DMAvsum = new TMLEventBChannel<ParamType,1>(166,"reqChannelHOCAppMAPPED__X_DMAvsum",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_DMAvsum);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_acc = new TMLEventBChannel<ParamType,1>(148,"reqChannelHOCAppMAPPED__X_acc",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_acc);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_cwm1 = new TMLEventBChannel<ParamType,1>(152,"reqChannelHOCAppMAPPED__X_cwm1",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_cwm1);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_cwm2 = new TMLEventBChannel<ParamType,1>(156,"reqChannelHOCAppMAPPED__X_cwm2",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_cwm2);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_cws = new TMLEventBChannel<ParamType,1>(154,"reqChannelHOCAppMAPPED__X_cws",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_cws);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_src = new TMLEventBChannel<ParamType,1>(158,"reqChannelHOCAppMAPPED__X_src",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_src);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_vsum = new TMLEventBChannel<ParamType,1>(150,"reqChannelHOCAppMAPPED__X_vsum",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_vsum);
        
        //Set bus schedulers
        MAINbus_0->setScheduler((WorkloadSource*) new RRScheduler("MAINbus_RRSched", 0, 5, 1, array(3, (WorkloadSource*)BRIDGEdma0_MAINbus_Master, (WorkloadSource*)MAINcpu0_MAINbus_Master, (WorkloadSource*)MAINbridge_MAINbus_Master), 3));
        Crossbar_0->setScheduler((WorkloadSource*) new RRScheduler("Crossbar_RRSched", 0, 5, 1, array(6, (WorkloadSource*)BRIDGEdma0_Crossbar_Master, (WorkloadSource*)ADAIFEMdma0_Crossbar_Master, (WorkloadSource*)ADAIFEMbridge_Crossbar_Master, (WorkloadSource*)FEPbridge_Crossbar_Master, (WorkloadSource*)FEPdma0_Crossbar_Master, (WorkloadSource*)MAINbridge_Crossbar_Master), 6));
        FEPbus_0->setScheduler((WorkloadSource*) new RRScheduler("FEPbus_RRSched", 0, 5, 1, array(3, (WorkloadSource*)FEPbridge_FEPbus_Master, (WorkloadSource*)FEPdma0_FEPbus_Master, (WorkloadSource*)FEP0_FEPbus_Master), 3));
        ADAIFEMbus_0->setScheduler((WorkloadSource*) new RRScheduler("ADAIFEMbus_RRSched", 0, 5, 1, array(3, (WorkloadSource*)ADAIFEM0_ADAIFEMbus_Master, (WorkloadSource*)ADAIFEMbridge_ADAIFEMbus_Master, (WorkloadSource*)ADAIFEMdma0_ADAIFEMbus_Master), 3));
        
        //Declaration of tasks
        HOCAppMAPPED__fork3* task__HOCAppMAPPED__fork3 = new HOCAppMAPPED__fork3(17,0,"HOCAppMAPPED__fork3", array(1,MAINcpu0),1
        ,channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in
        ,channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in
        ,channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in
        ,event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in
        ,event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in
        ,event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in
        );
        addTask(task__HOCAppMAPPED__fork3);
        HOCAppMAPPED__F_DMAcws* task__HOCAppMAPPED__F_DMAcws = new HOCAppMAPPED__F_DMAcws(20,0,"HOCAppMAPPED__F_DMAcws", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3
        ,event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in
        ,reqChannel_HOCAppMAPPED__X_DMAcws
        );
        addTask(task__HOCAppMAPPED__F_DMAcws);
        HOCAppMAPPED__F_DMAvsum* task__HOCAppMAPPED__F_DMAvsum = new HOCAppMAPPED__F_DMAvsum(23,0,"HOCAppMAPPED__F_DMAvsum", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1
        ,event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in
        ,reqChannel_HOCAppMAPPED__X_DMAvsum
        );
        addTask(task__HOCAppMAPPED__F_DMAvsum);
        HOCAppMAPPED__F_DMAcwm2* task__HOCAppMAPPED__F_DMAcwm2 = new HOCAppMAPPED__F_DMAcwm2(26,0,"HOCAppMAPPED__F_DMAcwm2", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2
        ,event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in
        ,reqChannel_HOCAppMAPPED__X_DMAcwm2
        );
        addTask(task__HOCAppMAPPED__F_DMAcwm2);
        HOCAppMAPPED__F_DMAfork1* task__HOCAppMAPPED__F_DMAfork1 = new HOCAppMAPPED__F_DMAfork1(29,0,"HOCAppMAPPED__F_DMAfork1", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in
        ,event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in
        ,reqChannel_HOCAppMAPPED__X_DMAfork1
        );
        addTask(task__HOCAppMAPPED__F_DMAfork1);
        HOCAppMAPPED__F_DMAsink* task__HOCAppMAPPED__F_DMAsink = new HOCAppMAPPED__F_DMAsink(32,0,"HOCAppMAPPED__F_DMAsink", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2
        ,event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in
        ,reqChannel_HOCAppMAPPED__X_DMAsink
        );
        addTask(task__HOCAppMAPPED__F_DMAsink);
        HOCAppMAPPED__join1* task__HOCAppMAPPED__join1 = new HOCAppMAPPED__join1(35,0,"HOCAppMAPPED__join1", array(1,MAINcpu0),1
        ,channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2
        ,channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3
        ,channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1
        ,channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in
        ,event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2
        ,event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3
        ,event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1
        ,event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in
        );
        addTask(task__HOCAppMAPPED__join1);
        HOCAppMAPPED__join2* task__HOCAppMAPPED__join2 = new HOCAppMAPPED__join2(40,0,"HOCAppMAPPED__join2", array(1,MAINcpu0),1
        ,channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2
        ,channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1
        ,channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in
        ,event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2
        ,event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1
        ,event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in
        );
        addTask(task__HOCAppMAPPED__join2);
        HOCAppMAPPED__fork1* task__HOCAppMAPPED__fork1 = new HOCAppMAPPED__fork1(44,0,"HOCAppMAPPED__fork1", array(1,MAINcpu0),1
        ,channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in
        ,channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in
        ,channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in
        ,event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in
        ,event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in
        ,event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in
        );
        addTask(task__HOCAppMAPPED__fork1);
        HOCAppMAPPED__fork2* task__HOCAppMAPPED__fork2 = new HOCAppMAPPED__fork2(47,0,"HOCAppMAPPED__fork2", array(1,MAINcpu0),1
        ,channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in
        ,channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in
        ,channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in
        ,event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in
        ,event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in
        ,event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in
        );
        addTask(task__HOCAppMAPPED__fork2);
        HOCAppMAPPED__F_src* task__HOCAppMAPPED__F_src = new HOCAppMAPPED__F_src(51,0,"HOCAppMAPPED__F_src", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in
        ,reqChannel_HOCAppMAPPED__X_src
        );
        addTask(task__HOCAppMAPPED__F_src);
        HOCAppMAPPED__F_acc* task__HOCAppMAPPED__F_acc = new HOCAppMAPPED__F_acc(54,0,"HOCAppMAPPED__F_acc", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1
        ,event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in
        ,reqChannel_HOCAppMAPPED__X_acc
        );
        addTask(task__HOCAppMAPPED__F_acc);
        HOCAppMAPPED__F_cws* task__HOCAppMAPPED__F_cws = new HOCAppMAPPED__F_cws(57,0,"HOCAppMAPPED__F_cws", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in
        ,event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in
        ,reqChannel_HOCAppMAPPED__X_cws
        );
        addTask(task__HOCAppMAPPED__F_cws);
        HOCAppMAPPED__F_vsum* task__HOCAppMAPPED__F_vsum = new HOCAppMAPPED__F_vsum(60,0,"HOCAppMAPPED__F_vsum", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in
        ,event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in
        ,reqChannel_HOCAppMAPPED__X_vsum
        );
        addTask(task__HOCAppMAPPED__F_vsum);
        HOCAppMAPPED__F_cwm1* task__HOCAppMAPPED__F_cwm1 = new HOCAppMAPPED__F_cwm1(63,0,"HOCAppMAPPED__F_cwm1", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in
        ,event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in
        ,reqChannel_HOCAppMAPPED__X_cwm1
        );
        addTask(task__HOCAppMAPPED__F_cwm1);
        HOCAppMAPPED__F_cwm2* task__HOCAppMAPPED__F_cwm2 = new HOCAppMAPPED__F_cwm2(66,0,"HOCAppMAPPED__F_cwm2", array(1,MAINcpu0),1
        ,event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in
        ,event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in
        ,reqChannel_HOCAppMAPPED__X_cwm2
        );
        addTask(task__HOCAppMAPPED__F_cwm2);
        HOCAppMAPPED__X_acc* task__HOCAppMAPPED__X_acc = new HOCAppMAPPED__X_acc(69,0,"HOCAppMAPPED__X_acc", array(1,MAINcpu0),1
        ,channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1
        ,channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in
        ,reqChannel_HOCAppMAPPED__X_acc
        );
        addTask(task__HOCAppMAPPED__X_acc);
        HOCAppMAPPED__SINK* task__HOCAppMAPPED__SINK = new HOCAppMAPPED__SINK(72,0,"HOCAppMAPPED__SINK", array(1,MAINcpu0),1
        ,channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in
        ,event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in
        );
        addTask(task__HOCAppMAPPED__SINK);
        HOCAppMAPPED__X_vsum* task__HOCAppMAPPED__X_vsum = new HOCAppMAPPED__X_vsum(75,0,"HOCAppMAPPED__X_vsum", array(1,FEP0),1
        ,channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in
        ,channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in
        ,reqChannel_HOCAppMAPPED__X_vsum
        );
        addTask(task__HOCAppMAPPED__X_vsum);
        HOCAppMAPPED__X_cwm1* task__HOCAppMAPPED__X_cwm1 = new HOCAppMAPPED__X_cwm1(78,0,"HOCAppMAPPED__X_cwm1", array(1,FEP0),1
        ,channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in
        ,channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in
        ,reqChannel_HOCAppMAPPED__X_cwm1
        );
        addTask(task__HOCAppMAPPED__X_cwm1);
        HOCAppMAPPED__X_cws* task__HOCAppMAPPED__X_cws = new HOCAppMAPPED__X_cws(81,0,"HOCAppMAPPED__X_cws", array(1,FEP0),1
        ,channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in
        ,channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in
        ,reqChannel_HOCAppMAPPED__X_cws
        );
        addTask(task__HOCAppMAPPED__X_cws);
        HOCAppMAPPED__X_cwm2* task__HOCAppMAPPED__X_cwm2 = new HOCAppMAPPED__X_cwm2(84,0,"HOCAppMAPPED__X_cwm2", array(1,FEP0),1
        ,channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in
        ,channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in
        ,reqChannel_HOCAppMAPPED__X_cwm2
        );
        addTask(task__HOCAppMAPPED__X_cwm2);
        HOCAppMAPPED__X_src* task__HOCAppMAPPED__X_src = new HOCAppMAPPED__X_src(87,0,"HOCAppMAPPED__X_src", array(1,ADAIFEM0),1
        ,channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in
        ,reqChannel_HOCAppMAPPED__X_src
        );
        addTask(task__HOCAppMAPPED__X_src);
        HOCAppMAPPED__X_DMAcwm2* task__HOCAppMAPPED__X_DMAcwm2 = new HOCAppMAPPED__X_DMAcwm2(90,0,"HOCAppMAPPED__X_DMAcwm2", array(1,BRIDGEdma0),1
        ,channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2
        ,channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in
        ,reqChannel_HOCAppMAPPED__X_DMAcwm2
        );
        addTask(task__HOCAppMAPPED__X_DMAcwm2);
        HOCAppMAPPED__X_DMAfork1* task__HOCAppMAPPED__X_DMAfork1 = new HOCAppMAPPED__X_DMAfork1(94,0,"HOCAppMAPPED__X_DMAfork1", array(1,BRIDGEdma0),1
        ,channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in
        ,channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in
        ,reqChannel_HOCAppMAPPED__X_DMAfork1
        );
        addTask(task__HOCAppMAPPED__X_DMAfork1);
        HOCAppMAPPED__X_DMAcws* task__HOCAppMAPPED__X_DMAcws = new HOCAppMAPPED__X_DMAcws(98,0,"HOCAppMAPPED__X_DMAcws", array(1,BRIDGEdma0),1
        ,channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3
        ,channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in
        ,reqChannel_HOCAppMAPPED__X_DMAcws
        );
        addTask(task__HOCAppMAPPED__X_DMAcws);
        HOCAppMAPPED__X_DMAvsum* task__HOCAppMAPPED__X_DMAvsum = new HOCAppMAPPED__X_DMAvsum(102,0,"HOCAppMAPPED__X_DMAvsum", array(1,BRIDGEdma0),1
        ,channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1
        ,channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in
        ,reqChannel_HOCAppMAPPED__X_DMAvsum
        );
        addTask(task__HOCAppMAPPED__X_DMAvsum);
        HOCAppMAPPED__X_DMAsink* task__HOCAppMAPPED__X_DMAsink = new HOCAppMAPPED__X_DMAsink(106,0,"HOCAppMAPPED__X_DMAsink", array(1,BRIDGEdma0),1
        ,channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2
        ,channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in
        ,reqChannel_HOCAppMAPPED__X_DMAsink
        );
        addTask(task__HOCAppMAPPED__X_DMAsink);
        
    }
    
    void generateTEPEs(){
        //Declaration of TEPEs
        
    }
};

#include <main.h>
