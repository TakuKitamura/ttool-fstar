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
#include <HOCAppMAPPED__F_DMAcwm2.h>
#include <HOCAppMAPPED__F_DMAcws.h>
#include <HOCAppMAPPED__F_DMAfork1.h>
#include <HOCAppMAPPED__F_DMAsink.h>
#include <HOCAppMAPPED__F_DMAvsum.h>
#include <HOCAppMAPPED__F_acc.h>
#include <HOCAppMAPPED__F_cwm1.h>
#include <HOCAppMAPPED__F_cwm2.h>
#include <HOCAppMAPPED__F_cws.h>
#include <HOCAppMAPPED__F_src.h>
#include <HOCAppMAPPED__F_vsum.h>
#include <HOCAppMAPPED__SINK.h>
#include <HOCAppMAPPED__X_DMAcwm2.h>
#include <HOCAppMAPPED__X_DMAcws.h>
#include <HOCAppMAPPED__X_DMAfork1.h>
#include <HOCAppMAPPED__X_DMAsink.h>
#include <HOCAppMAPPED__X_DMAvsum.h>
#include <HOCAppMAPPED__X_acc.h>
#include <HOCAppMAPPED__X_cwm1.h>
#include <HOCAppMAPPED__X_cwm2.h>
#include <HOCAppMAPPED__X_cws.h>
#include <HOCAppMAPPED__X_src.h>
#include <HOCAppMAPPED__X_vsum.h>
#include <HOCAppMAPPED__fork1.h>
#include <HOCAppMAPPED__fork2.h>
#include <HOCAppMAPPED__fork3.h>
#include <HOCAppMAPPED__join1.h>
#include <HOCAppMAPPED__join2.h>


class CurrentComponents: public SimComponents{
    public:
    CurrentComponents():SimComponents(-1456735367){
        //Declaration of CPUs
        RRScheduler* defaultCPU_scheduler = new RRScheduler("defaultCPU_RRSched", 0, 2000000, 1 ) ;
        CPU* defaultCPU0 = new SingleCoreCPU(336, "defaultCPU_0", defaultCPU_scheduler, 1, 1, 1, 1, 1, 0, 0, 10, 4);
        addCPU(defaultCPU0);
        
        //Declaration of Buses
        Bus* defaultBus_0 = new Bus(337,"defaultBus_0",0, 100, 4, 1,false);
        addBus(defaultBus_0);
        
        //Declaration of Bridges
        
        //Declaration of Memories
        
        //Declaration of Bus masters
        BusMaster* defaultCPU0_defaultBus_Master = new BusMaster("defaultCPU0_defaultBus_Master", 0, 1, array(1, (SchedulableCommDevice*)defaultBus_0));
        defaultCPU0->addBusMaster(defaultCPU0_defaultBus_Master);
        
        //Declaration of channels
        TMLbrbwChannel* channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2 = new TMLbrbwChannel(105,"HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2);
        TMLbrbwChannel* channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3 = new TMLbrbwChannel(107,"HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3);
        TMLbrbwChannel* channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in = new TMLbrbwChannel(108,"HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2 = new TMLbrbwChannel(104,"HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2);
        TMLbrbwChannel* channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1 = new TMLbrbwChannel(106,"HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1);
        TMLbrbwChannel* channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1 = new TMLbrbwChannel(97,"HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1);
        TMLbrbwChannel* channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in = new TMLbrbwChannel(95,"HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in = new TMLbrbwChannel(94,"HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in = new TMLbrbwChannel(103,"HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in = new TMLbrbwChannel(99,"HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in = new TMLbrbwChannel(98,"HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in = new TMLbrbwChannel(111,"HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in = new TMLbrbwChannel(112,"HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in = new TMLbrbwChannel(109,"HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in = new TMLbrbwChannel(110,"HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in = new TMLbrbwChannel(101,"HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),49152,0,0);
        addChannel(channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in = new TMLbrbwChannel(100,"HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in = new TMLbrbwChannel(102,"HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in);
        TMLbrbwChannel* channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in = new TMLbrbwChannel(96,"HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in",8,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),16348,0,0);
        addChannel(channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in);
        
        //Declaration of events
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2 = new TMLEventBChannel<ParamType,1>(124,"HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3 = new TMLEventBChannel<ParamType,1>(126,"HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in = new TMLEventBChannel<ParamType,1>(127,"HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2 = new TMLEventBChannel<ParamType,1>(123,"HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1 = new TMLEventBChannel<ParamType,1>(125,"HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1 = new TMLEventBChannel<ParamType,1>(116,"HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in = new TMLEventBChannel<ParamType,1>(114,"HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in = new TMLEventBChannel<ParamType,1>(113,"HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in = new TMLEventBChannel<ParamType,1>(122,"HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in = new TMLEventBChannel<ParamType,1>(117,"HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in = new TMLEventBChannel<ParamType,1>(118,"HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in = new TMLEventBChannel<ParamType,1>(130,"HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in = new TMLEventBChannel<ParamType,1>(131,"HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in = new TMLEventBChannel<ParamType,1>(129,"HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in = new TMLEventBChannel<ParamType,1>(128,"HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in);
        TMLEventFBChannel<ParamType,1>* event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in = new TMLEventFBChannel<ParamType,1>(120,"HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in",0,0,0,8,0);
        addEvent(event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in = new TMLEventBChannel<ParamType,1>(119,"HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in = new TMLEventBChannel<ParamType,1>(121,"HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in);
        TMLEventBChannel<ParamType,1>* event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in = new TMLEventBChannel<ParamType,1>(115,"HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in",0,0,0,0,false,false);
        addEvent(event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in);
        
        //Declaration of requests
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_DMAcwm2 = new TMLEventBChannel<ParamType,1>(146,"reqChannelHOCAppMAPPED__X_DMAcwm2",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_DMAcwm2);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_DMAcws = new TMLEventBChannel<ParamType,1>(150,"reqChannelHOCAppMAPPED__X_DMAcws",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_DMAcws);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_DMAfork1 = new TMLEventBChannel<ParamType,1>(152,"reqChannelHOCAppMAPPED__X_DMAfork1",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_DMAfork1);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_DMAsink = new TMLEventBChannel<ParamType,1>(144,"reqChannelHOCAppMAPPED__X_DMAsink",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_DMAsink);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_DMAvsum = new TMLEventBChannel<ParamType,1>(148,"reqChannelHOCAppMAPPED__X_DMAvsum",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_DMAvsum);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_acc = new TMLEventBChannel<ParamType,1>(138,"reqChannelHOCAppMAPPED__X_acc",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_acc);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_cwm1 = new TMLEventBChannel<ParamType,1>(134,"reqChannelHOCAppMAPPED__X_cwm1",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_cwm1);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_cwm2 = new TMLEventBChannel<ParamType,1>(132,"reqChannelHOCAppMAPPED__X_cwm2",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_cwm2);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_cws = new TMLEventBChannel<ParamType,1>(142,"reqChannelHOCAppMAPPED__X_cws",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_cws);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_src = new TMLEventBChannel<ParamType,1>(140,"reqChannelHOCAppMAPPED__X_src",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_src);
        TMLEventBChannel<ParamType,1>* reqChannel_HOCAppMAPPED__X_vsum = new TMLEventBChannel<ParamType,1>(136,"reqChannelHOCAppMAPPED__X_vsum",0,0,0,0,true,false);
        addRequest(reqChannel_HOCAppMAPPED__X_vsum);
        
        //Set bus schedulers
        defaultBus_0->setScheduler((WorkloadSource*) new RRScheduler("defaultBus_RRSched", 0, 5, 1, array(1, (WorkloadSource*)defaultCPU0_defaultBus_Master), 1));
        
        //Declaration of tasks
        HOCAppMAPPED__F_DMAcwm2* task__HOCAppMAPPED__F_DMAcwm2 = new HOCAppMAPPED__F_DMAcwm2(63,0,"HOCAppMAPPED__F_DMAcwm2", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2
        ,event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in
        ,reqChannel_HOCAppMAPPED__X_DMAcwm2
        );
        addTask(task__HOCAppMAPPED__F_DMAcwm2);
        HOCAppMAPPED__F_DMAcws* task__HOCAppMAPPED__F_DMAcws = new HOCAppMAPPED__F_DMAcws(77,0,"HOCAppMAPPED__F_DMAcws", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3
        ,event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in
        ,reqChannel_HOCAppMAPPED__X_DMAcws
        );
        addTask(task__HOCAppMAPPED__F_DMAcws);
        HOCAppMAPPED__F_DMAfork1* task__HOCAppMAPPED__F_DMAfork1 = new HOCAppMAPPED__F_DMAfork1(84,0,"HOCAppMAPPED__F_DMAfork1", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in
        ,event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in
        ,reqChannel_HOCAppMAPPED__X_DMAfork1
        );
        addTask(task__HOCAppMAPPED__F_DMAfork1);
        HOCAppMAPPED__F_DMAsink* task__HOCAppMAPPED__F_DMAsink = new HOCAppMAPPED__F_DMAsink(56,0,"HOCAppMAPPED__F_DMAsink", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2
        ,event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in
        ,reqChannel_HOCAppMAPPED__X_DMAsink
        );
        addTask(task__HOCAppMAPPED__F_DMAsink);
        HOCAppMAPPED__F_DMAvsum* task__HOCAppMAPPED__F_DMAvsum = new HOCAppMAPPED__F_DMAvsum(66,0,"HOCAppMAPPED__F_DMAvsum", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1
        ,event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in
        ,reqChannel_HOCAppMAPPED__X_DMAvsum
        );
        addTask(task__HOCAppMAPPED__F_DMAvsum);
        HOCAppMAPPED__F_acc* task__HOCAppMAPPED__F_acc = new HOCAppMAPPED__F_acc(25,0,"HOCAppMAPPED__F_acc", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1
        ,event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in
        ,reqChannel_HOCAppMAPPED__X_acc
        );
        addTask(task__HOCAppMAPPED__F_acc);
        HOCAppMAPPED__F_cwm1* task__HOCAppMAPPED__F_cwm1 = new HOCAppMAPPED__F_cwm1(13,0,"HOCAppMAPPED__F_cwm1", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in
        ,event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in
        ,reqChannel_HOCAppMAPPED__X_cwm1
        );
        addTask(task__HOCAppMAPPED__F_cwm1);
        HOCAppMAPPED__F_cwm2* task__HOCAppMAPPED__F_cwm2 = new HOCAppMAPPED__F_cwm2(7,0,"HOCAppMAPPED__F_cwm2", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in
        ,event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in
        ,reqChannel_HOCAppMAPPED__X_cwm2
        );
        addTask(task__HOCAppMAPPED__F_cwm2);
        HOCAppMAPPED__F_cws* task__HOCAppMAPPED__F_cws = new HOCAppMAPPED__F_cws(49,0,"HOCAppMAPPED__F_cws", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in
        ,event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in
        ,reqChannel_HOCAppMAPPED__X_cws
        );
        addTask(task__HOCAppMAPPED__F_cws);
        HOCAppMAPPED__F_src* task__HOCAppMAPPED__F_src = new HOCAppMAPPED__F_src(40,0,"HOCAppMAPPED__F_src", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in
        ,reqChannel_HOCAppMAPPED__X_src
        );
        addTask(task__HOCAppMAPPED__F_src);
        HOCAppMAPPED__F_vsum* task__HOCAppMAPPED__F_vsum = new HOCAppMAPPED__F_vsum(19,0,"HOCAppMAPPED__F_vsum", array(1,defaultCPU0),1
        ,event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in
        ,event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in
        ,reqChannel_HOCAppMAPPED__X_vsum
        );
        addTask(task__HOCAppMAPPED__F_vsum);
        HOCAppMAPPED__SINK* task__HOCAppMAPPED__SINK = new HOCAppMAPPED__SINK(1,0,"HOCAppMAPPED__SINK", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in
        ,event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in
        );
        addTask(task__HOCAppMAPPED__SINK);
        HOCAppMAPPED__X_DMAcwm2* task__HOCAppMAPPED__X_DMAcwm2 = new HOCAppMAPPED__X_DMAcwm2(59,0,"HOCAppMAPPED__X_DMAcwm2", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2
        ,channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in
        ,reqChannel_HOCAppMAPPED__X_DMAcwm2
        );
        addTask(task__HOCAppMAPPED__X_DMAcwm2);
        HOCAppMAPPED__X_DMAcws* task__HOCAppMAPPED__X_DMAcws = new HOCAppMAPPED__X_DMAcws(73,0,"HOCAppMAPPED__X_DMAcws", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3
        ,channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in
        ,reqChannel_HOCAppMAPPED__X_DMAcws
        );
        addTask(task__HOCAppMAPPED__X_DMAcws);
        HOCAppMAPPED__X_DMAfork1* task__HOCAppMAPPED__X_DMAfork1 = new HOCAppMAPPED__X_DMAfork1(80,0,"HOCAppMAPPED__X_DMAfork1", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in
        ,channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in
        ,reqChannel_HOCAppMAPPED__X_DMAfork1
        );
        addTask(task__HOCAppMAPPED__X_DMAfork1);
        HOCAppMAPPED__X_DMAsink* task__HOCAppMAPPED__X_DMAsink = new HOCAppMAPPED__X_DMAsink(52,0,"HOCAppMAPPED__X_DMAsink", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2
        ,channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in
        ,reqChannel_HOCAppMAPPED__X_DMAsink
        );
        addTask(task__HOCAppMAPPED__X_DMAsink);
        HOCAppMAPPED__X_DMAvsum* task__HOCAppMAPPED__X_DMAvsum = new HOCAppMAPPED__X_DMAvsum(69,0,"HOCAppMAPPED__X_DMAvsum", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1
        ,channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in
        ,reqChannel_HOCAppMAPPED__X_DMAvsum
        );
        addTask(task__HOCAppMAPPED__X_DMAvsum);
        HOCAppMAPPED__X_acc* task__HOCAppMAPPED__X_acc = new HOCAppMAPPED__X_acc(22,0,"HOCAppMAPPED__X_acc", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1
        ,channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in
        ,reqChannel_HOCAppMAPPED__X_acc
        );
        addTask(task__HOCAppMAPPED__X_acc);
        HOCAppMAPPED__X_cwm1* task__HOCAppMAPPED__X_cwm1 = new HOCAppMAPPED__X_cwm1(10,0,"HOCAppMAPPED__X_cwm1", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in
        ,channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in
        ,reqChannel_HOCAppMAPPED__X_cwm1
        );
        addTask(task__HOCAppMAPPED__X_cwm1);
        HOCAppMAPPED__X_cwm2* task__HOCAppMAPPED__X_cwm2 = new HOCAppMAPPED__X_cwm2(4,0,"HOCAppMAPPED__X_cwm2", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in
        ,channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in
        ,reqChannel_HOCAppMAPPED__X_cwm2
        );
        addTask(task__HOCAppMAPPED__X_cwm2);
        HOCAppMAPPED__X_cws* task__HOCAppMAPPED__X_cws = new HOCAppMAPPED__X_cws(46,0,"HOCAppMAPPED__X_cws", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in
        ,channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in
        ,reqChannel_HOCAppMAPPED__X_cws
        );
        addTask(task__HOCAppMAPPED__X_cws);
        HOCAppMAPPED__X_src* task__HOCAppMAPPED__X_src = new HOCAppMAPPED__X_src(43,0,"HOCAppMAPPED__X_src", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in
        ,reqChannel_HOCAppMAPPED__X_src
        );
        addTask(task__HOCAppMAPPED__X_src);
        HOCAppMAPPED__X_vsum* task__HOCAppMAPPED__X_vsum = new HOCAppMAPPED__X_vsum(16,0,"HOCAppMAPPED__X_vsum", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in
        ,channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in
        ,reqChannel_HOCAppMAPPED__X_vsum
        );
        addTask(task__HOCAppMAPPED__X_vsum);
        HOCAppMAPPED__fork1* task__HOCAppMAPPED__fork1 = new HOCAppMAPPED__fork1(28,0,"HOCAppMAPPED__fork1", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in
        ,channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in
        ,channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in
        ,event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in
        ,event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in
        ,event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in
        );
        addTask(task__HOCAppMAPPED__fork1);
        HOCAppMAPPED__fork2* task__HOCAppMAPPED__fork2 = new HOCAppMAPPED__fork2(90,0,"HOCAppMAPPED__fork2", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in
        ,channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in
        ,channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in
        ,event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in
        ,event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in
        ,event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in
        );
        addTask(task__HOCAppMAPPED__fork2);
        HOCAppMAPPED__fork3* task__HOCAppMAPPED__fork3 = new HOCAppMAPPED__fork3(87,0,"HOCAppMAPPED__fork3", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in
        ,channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in
        ,channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in
        ,event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in
        ,event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in
        ,event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in
        );
        addTask(task__HOCAppMAPPED__fork3);
        HOCAppMAPPED__join1* task__HOCAppMAPPED__join1 = new HOCAppMAPPED__join1(35,0,"HOCAppMAPPED__join1", array(1,defaultCPU0),1
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
        HOCAppMAPPED__join2* task__HOCAppMAPPED__join2 = new HOCAppMAPPED__join2(31,0,"HOCAppMAPPED__join2", array(1,defaultCPU0),1
        ,channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2
        ,channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1
        ,channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in
        ,event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2
        ,event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1
        ,event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in
        );
        addTask(task__HOCAppMAPPED__join2);
        
    }
    
    void generateTEPEs(){
        //Declaration of TEPEs
        
    }
};

#include <main.h>
