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
#include <AppC__InterfaceDevice.h>
#include <AppC__SmartCard.h>
#include <AppC__Application.h>
#include <AppC__TCPIP.h>
#include <AppC__Timer.h>


class CurrentComponents: public SimComponents{
    public:
    CurrentComponents():SimComponents(-1754902028){
        //Declaration of CPUs
        RRScheduler* defaultCPU_scheduler = new RRScheduler("defaultCPU_RRSched", 0, 2000000, 1 ) ;
        CPU* defaultCPU0 = new SingleCoreCPU(252, "defaultCPU_0", defaultCPU_scheduler, 1, 1, 1, 1, 1, 0, 0, 10, 4);
        addCPU(defaultCPU0);
        
        //Declaration of Buses
        Bus* defaultBus_0 = new Bus(253,"defaultBus_0",0, 100, 4, 1,false);
        addBus(defaultBus_0);
        
        //Declaration of Bridges
        
        //Declaration of Memories
        
        //Declaration of Bus masters
        BusMaster* defaultCPU0_defaultBus_Master = new BusMaster("defaultCPU0_defaultBus_Master", 0, 1, array(1, (SchedulableCommDevice*)defaultBus_0));
        defaultCPU0->addBusMaster(defaultCPU0_defaultBus_Master);
        
        //Declaration of channels
        TMLbrnbwChannel* channel__AppC__fromAtoT = new TMLbrnbwChannel(35,"AppC__fromAtoT",4,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromAtoT);
        TMLbrnbwChannel* channel__AppC__fromDtoSC__AppC__fromDtoSC0 = new TMLbrnbwChannel(32,"AppC__fromDtoSC__AppC__fromDtoSC0",4,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromDtoSC__AppC__fromDtoSC0);
        TMLbrnbwChannel* channel__AppC__fromPtoT = new TMLbrnbwChannel(33,"AppC__fromPtoT",4,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromPtoT);
        TMLbrnbwChannel* channel__AppC__fromSCtoD = new TMLbrnbwChannel(34,"AppC__fromSCtoD",4,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromSCtoD);
        TMLbrnbwChannel* channel__AppC__fromTtoA = new TMLbrnbwChannel(37,"AppC__fromTtoA",4,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromTtoA);
        TMLbrnbwChannel* channel__AppC__fromTtoP = new TMLbrnbwChannel(38,"AppC__fromTtoP",4,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromTtoP);
        TMLnbrnbwChannel* channel__AppC__temp = new TMLnbrnbwChannel(36,"AppC__temp",4,2,array(2,defaultCPU0_defaultBus_Master,defaultCPU0_defaultBus_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0);
        addChannel(channel__AppC__temp);
        
        //Declaration of events
        TMLEventFChannel<ParamType,0>* event__AppC__abort__AppC__abort = new TMLEventFChannel<ParamType,0>(47,"AppC__abort__AppC__abort",0,0,0,1,0);
        addEvent(event__AppC__abort__AppC__abort);
        TMLEventFChannel<ParamType,0>* event__AppC__answerToReset__AppC__answerToReset = new TMLEventFChannel<ParamType,0>(44,"AppC__answerToReset__AppC__answerToReset",0,0,0,1,0);
        addEvent(event__AppC__answerToReset__AppC__answerToReset);
        TMLEventFChannel<ParamType,0>* event__AppC__close__AppC__close = new TMLEventFChannel<ParamType,0>(48,"AppC__close__AppC__close",0,0,0,1,0);
        addEvent(event__AppC__close__AppC__close);
        TMLEventBChannel<ParamType,0>* event__AppC__data_Ready_SC__AppC__data_Ready_SC = new TMLEventBChannel<ParamType,0>(43,"AppC__data_Ready_SC__AppC__data_Ready_SC",0,0,0,0,false,false);
        addEvent(event__AppC__data_Ready_SC__AppC__data_Ready_SC);
        TMLEventBChannel<ParamType,0>* event__AppC__data_Ready__AppC__data_Ready = new TMLEventBChannel<ParamType,0>(39,"AppC__data_Ready__AppC__data_Ready",0,0,0,0,false,false);
        addEvent(event__AppC__data_Ready__AppC__data_Ready);
        TMLEventFChannel<ParamType,0>* event__AppC__open__AppC__open = new TMLEventFChannel<ParamType,0>(46,"AppC__open__AppC__open",0,0,0,1,0);
        addEvent(event__AppC__open__AppC__open);
        TMLEventFChannel<ParamType,0>* event__AppC__pTSConfirm__AppC__pTSConfirm = new TMLEventFChannel<ParamType,0>(45,"AppC__pTSConfirm__AppC__pTSConfirm",0,0,0,1,0);
        addEvent(event__AppC__pTSConfirm__AppC__pTSConfirm);
        TMLEventFChannel<ParamType,0>* event__AppC__pTS__AppC__pTS = new TMLEventFChannel<ParamType,0>(41,"AppC__pTS__AppC__pTS",0,0,0,1,0);
        addEvent(event__AppC__pTS__AppC__pTS);
        TMLEventBChannel<ParamType,0>* event__AppC__receive_Application__AppC__receive_Application = new TMLEventBChannel<ParamType,0>(51,"AppC__receive_Application__AppC__receive_Application",0,0,0,0,false,false);
        addEvent(event__AppC__receive_Application__AppC__receive_Application);
        TMLEventBChannel<ParamType,0>* event__AppC__receive__AppC__receive = new TMLEventBChannel<ParamType,0>(42,"AppC__receive__AppC__receive",0,0,0,0,false,false);
        addEvent(event__AppC__receive__AppC__receive);
        TMLEventFChannel<ParamType,0>* event__AppC__reset__AppC__reset = new TMLEventFChannel<ParamType,0>(40,"AppC__reset__AppC__reset",0,0,0,1,0);
        addEvent(event__AppC__reset__AppC__reset);
        TMLEventBChannel<ParamType,0>* event__AppC__send_TCP__AppC__send_TCP = new TMLEventBChannel<ParamType,0>(49,"AppC__send_TCP__AppC__send_TCP",0,0,0,0,false,false);
        addEvent(event__AppC__send_TCP__AppC__send_TCP);
        TMLEventBChannel<ParamType,0>* event__AppC__send__AppC__send = new TMLEventBChannel<ParamType,0>(52,"AppC__send__AppC__send",0,0,0,0,false,false);
        addEvent(event__AppC__send__AppC__send);
        TMLEventFChannel<ParamType,0>* event__AppC__stop__AppC__stop = new TMLEventFChannel<ParamType,0>(50,"AppC__stop__AppC__stop",0,0,0,1,0);
        addEvent(event__AppC__stop__AppC__stop);
        TMLEventFChannel<ParamType,0>* event__AppC__timeOut__AppC__timeOut = new TMLEventFChannel<ParamType,0>(53,"AppC__timeOut__AppC__timeOut",0,0,0,1,0);
        addEvent(event__AppC__timeOut__AppC__timeOut);
        
        //Declaration of requests
        TMLEventBChannel<ParamType,0>* reqChannel_AppC__Application = new TMLEventBChannel<ParamType,0>(55,"reqChannelAppC__Application",0,0,0,0,true,false);
        addRequest(reqChannel_AppC__Application);
        TMLEventBChannel<ParamType,0>* reqChannel_AppC__SmartCard = new TMLEventBChannel<ParamType,0>(54,"reqChannelAppC__SmartCard",0,0,0,0,true,false);
        addRequest(reqChannel_AppC__SmartCard);
        TMLEventBChannel<ParamType,0>* reqChannel_AppC__TCPIP = new TMLEventBChannel<ParamType,0>(56,"reqChannelAppC__TCPIP",0,0,0,0,true,false);
        addRequest(reqChannel_AppC__TCPIP);
        TMLEventBChannel<ParamType,0>* reqChannel_AppC__Timer = new TMLEventBChannel<ParamType,0>(57,"reqChannelAppC__Timer",0,0,0,0,true,false);
        addRequest(reqChannel_AppC__Timer);
        
        //Set bus schedulers
        defaultBus_0->setScheduler((WorkloadSource*) new RRScheduler("defaultBus_RRSched", 0, 5, 1, array(1, (WorkloadSource*)defaultCPU0_defaultBus_Master), 1));
        
        //Declaration of tasks
        AppC__InterfaceDevice* task__AppC__InterfaceDevice = new AppC__InterfaceDevice(1,0,"AppC__InterfaceDevice", array(1,defaultCPU0),1
        ,channel__AppC__fromDtoSC__AppC__fromDtoSC0
        ,channel__AppC__fromSCtoD
        ,event__AppC__answerToReset__AppC__answerToReset
        ,event__AppC__data_Ready_SC__AppC__data_Ready_SC
        ,event__AppC__data_Ready__AppC__data_Ready
        ,event__AppC__pTSConfirm__AppC__pTSConfirm
        ,event__AppC__pTS__AppC__pTS
        ,event__AppC__reset__AppC__reset
        ,reqChannel_AppC__SmartCard
        );
        addTask(task__AppC__InterfaceDevice);
        AppC__SmartCard* task__AppC__SmartCard = new AppC__SmartCard(7,0,"AppC__SmartCard", array(1,defaultCPU0),1
        ,channel__AppC__fromDtoSC__AppC__fromDtoSC0
        ,channel__AppC__fromPtoT
        ,channel__AppC__fromSCtoD
        ,channel__AppC__fromTtoP
        ,event__AppC__answerToReset__AppC__answerToReset
        ,event__AppC__data_Ready_SC__AppC__data_Ready_SC
        ,event__AppC__data_Ready__AppC__data_Ready
        ,event__AppC__pTSConfirm__AppC__pTSConfirm
        ,event__AppC__pTS__AppC__pTS
        ,event__AppC__receive__AppC__receive
        ,event__AppC__reset__AppC__reset
        ,event__AppC__send__AppC__send
        ,reqChannel_AppC__Application
        ,reqChannel_AppC__TCPIP
        ,reqChannel_AppC__SmartCard
        );
        addTask(task__AppC__SmartCard);
        AppC__Application* task__AppC__Application = new AppC__Application(17,0,"AppC__Application", array(1,defaultCPU0),1
        ,channel__AppC__fromAtoT
        ,channel__AppC__fromTtoA
        ,event__AppC__abort__AppC__abort
        ,event__AppC__close__AppC__close
        ,event__AppC__open__AppC__open
        ,event__AppC__receive_Application__AppC__receive_Application
        ,event__AppC__send_TCP__AppC__send_TCP
        ,reqChannel_AppC__TCPIP
        ,reqChannel_AppC__Application
        );
        addTask(task__AppC__Application);
        AppC__TCPIP* task__AppC__TCPIP = new AppC__TCPIP(19,0,"AppC__TCPIP", array(1,defaultCPU0),1
        ,channel__AppC__fromAtoT
        ,channel__AppC__fromPtoT
        ,channel__AppC__fromTtoA
        ,channel__AppC__fromTtoP
        ,channel__AppC__temp
        ,event__AppC__abort__AppC__abort
        ,event__AppC__close__AppC__close
        ,event__AppC__open__AppC__open
        ,event__AppC__receive_Application__AppC__receive_Application
        ,event__AppC__receive__AppC__receive
        ,event__AppC__send_TCP__AppC__send_TCP
        ,event__AppC__send__AppC__send
        ,event__AppC__stop__AppC__stop
        ,event__AppC__timeOut__AppC__timeOut
        ,reqChannel_AppC__Timer
        ,reqChannel_AppC__TCPIP
        );
        addTask(task__AppC__TCPIP);
        AppC__Timer* task__AppC__Timer = new AppC__Timer(29,0,"AppC__Timer", array(1,defaultCPU0),1
        ,event__AppC__stop__AppC__stop
        ,event__AppC__timeOut__AppC__timeOut
        ,reqChannel_AppC__Timer
        );
        addTask(task__AppC__Timer);
        
    }
    
    void generateTEPEs(){
        //Declaration of TEPEs
        
    }
};

#include <main.h>
