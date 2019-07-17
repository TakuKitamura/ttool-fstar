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
#include <MultiCoreCPU.h>
#include <FPGA.h>
#include <RRScheduler.h>
#include <RRPrioScheduler.h>
#include <OrderScheduler.h>
#include <PrioScheduler.h>
#include <Bus.h>
#include <ReconfigScheduler.h>
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
#include <AppC__Timer.h>
#include <AppC__TCPIP.h>
#include <AppC__SmartCard.h>
#include <AppC__Application.h>


class CurrentComponents: public SimComponents{
    public:
    CurrentComponents():SimComponents(-672758982){
        //Declaration of CPUs
        RRScheduler* CPU1_scheduler = new RRScheduler("CPU1_RRSched", 0, 2000000, 3 ) ;
        CPU* CPU1_1 = new SingleCoreCPU(3, "CPU1_1", CPU1_scheduler, 2, 1, 1, 5, 20, 2, 10, 10, 4);
        addCPU(CPU1_1);
        RRScheduler* HWA0_scheduler = new RRScheduler("HWA0_RRSched", 0, 2000000, 1 ) ;
        CPU* HWA0 = new SingleCoreCPU(4, "HWA0", HWA0_scheduler, 1, 1, 1, 1, 1, 0, 10, 10, 4);
        addCPU(HWA0);
        OrderScheduler* FPGA0_scheduler = new OrderScheduler("FPGA0_RRSched", 0) ;
        FPGA* FPGA0 = new FPGA(5, "FPGA0", FPGA0_scheduler, 50, 10, 10, 1, 1);
        addFPGA(FPGA0);
        
        //Declaration of Model Name
        std::string msg="/home/niusiyuan/test/TTool/modeling/DIPLODOCUS/SmartCardProtocol.xml / DIPLODOCUS architecture and mapping Diagram";
        addModelName("/home/niusiyuan/test/TTool/modeling/DIPLODOCUS/SmartCardProtocol.xml / DIPLODOCUS architecture and mapping Diagram");
        //Declaration of Buses
        Bus* Bus0_0 = new Bus(2,"Bus0_0",0, 100, 4, 5,false);
        addBus(Bus0_0);
        
        //Declaration of Bridges
        
        //Declaration of Memories
        Memory* Memory0 = new Memory(1,"Memory0", 1, 4);
        addMem(Memory0);
        
        //Declaration of Bus masters
        BusMaster* CPU1_0_Bus0_Master = new BusMaster("CPU1_0_Bus0_Master", 0, 1, array(1, (SchedulableCommDevice*) Bus0_0));
        CPU1_1->addBusMaster(CPU1_0_Bus0_Master);
        BusMaster* HWA0_0_Bus0_Master = new BusMaster("HWA0_0_Bus0_Master", 0, 1, array(1, (SchedulableCommDevice*) Bus0_0));
        HWA0->addBusMaster(HWA0_0_Bus0_Master);
        BusMaster* FPGA0_0_Bus0_Master = new BusMaster("FPGA0_0_Bus0_Master", 0, 1, array(1, (SchedulableCommDevice*) Bus0_0));
        FPGA0->addBusMaster(FPGA0_0_Bus0_Master);
        
        //Declaration of channels
        TMLbrnbwChannel* channel__AppC__fromAtoT = new TMLbrnbwChannel(57,"AppC__fromAtoT",4,2,array(2,FPGA0_0_Bus0_Master,FPGA0_0_Bus0_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromAtoT);
        TMLbrnbwChannel* channel__AppC__fromDtoSC = new TMLbrnbwChannel(39,"AppC__fromDtoSC",40,2,array(2,CPU1_0_Bus0_Master,FPGA0_0_Bus0_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromDtoSC);
        TMLbrnbwChannel* channel__AppC__fromPtoT = new TMLbrnbwChannel(54,"AppC__fromPtoT",4,2,array(2,FPGA0_0_Bus0_Master,FPGA0_0_Bus0_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromPtoT);
        TMLbrnbwChannel* channel__AppC__fromSCtoD = new TMLbrnbwChannel(51,"AppC__fromSCtoD",40,2,array(2,FPGA0_0_Bus0_Master,CPU1_0_Bus0_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromSCtoD);
        TMLbrnbwChannel* channel__AppC__fromTtoA = new TMLbrnbwChannel(45,"AppC__fromTtoA",4,2,array(2,FPGA0_0_Bus0_Master,FPGA0_0_Bus0_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromTtoA);
        TMLbrnbwChannel* channel__AppC__fromTtoP = new TMLbrnbwChannel(42,"AppC__fromTtoP",4,2,array(2,FPGA0_0_Bus0_Master,FPGA0_0_Bus0_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0,0);
        addChannel(channel__AppC__fromTtoP);
        TMLnbrnbwChannel* channel__AppC__temp = new TMLnbrnbwChannel(48,"AppC__temp",4,2,array(2,FPGA0_0_Bus0_Master,FPGA0_0_Bus0_Master),array(2,static_cast<Slave*>(0),static_cast<Slave*>(0)),0);
        addChannel(channel__AppC__temp);
        
        //Declaration of events
        TMLEventFChannel<ParamType,0>* event__AppC__abort__AppC__abort = new TMLEventFChannel<ParamType,0>(75,"AppC__abort__AppC__abort",0,0,0,1,0);
        addEvent(event__AppC__abort__AppC__abort);
        TMLEventFChannel<ParamType,0>* event__AppC__answerToReset__AppC__answerToReset = new TMLEventFChannel<ParamType,0>(69,"AppC__answerToReset__AppC__answerToReset",0,0,0,1,0);
        addEvent(event__AppC__answerToReset__AppC__answerToReset);
        TMLEventFChannel<ParamType,0>* event__AppC__close__AppC__close = new TMLEventFChannel<ParamType,0>(76,"AppC__close__AppC__close",0,0,0,1,0);
        addEvent(event__AppC__close__AppC__close);
        TMLEventFBChannel<ParamType,0>* event__AppC__connectionOpened__AppC__connectionOpened = new TMLEventFBChannel<ParamType,0>(77,"AppC__connectionOpened__AppC__connectionOpened",0,0,0,8,0);
        addEvent(event__AppC__connectionOpened__AppC__connectionOpened);
        TMLEventBChannel<ParamType,0>* event__AppC__data_Ready_SC__AppC__data_Ready_SC = new TMLEventBChannel<ParamType,0>(71,"AppC__data_Ready_SC__AppC__data_Ready_SC",0,0,0,0,false,false);
        addEvent(event__AppC__data_Ready_SC__AppC__data_Ready_SC);
        TMLEventBChannel<ParamType,2>* event__AppC__data_Ready__AppC__data_Ready = new TMLEventBChannel<ParamType,2>(63,"AppC__data_Ready__AppC__data_Ready",0,0,0,0,false,false);
        addEvent(event__AppC__data_Ready__AppC__data_Ready);
        TMLEventFChannel<ParamType,0>* event__AppC__end__AppC__end = new TMLEventFChannel<ParamType,0>(62,"AppC__end__AppC__end",0,0,0,1,0);
        addEvent(event__AppC__end__AppC__end);
        TMLEventFChannel<ParamType,0>* event__AppC__open__AppC__open = new TMLEventFChannel<ParamType,0>(74,"AppC__open__AppC__open",0,0,0,1,0);
        addEvent(event__AppC__open__AppC__open);
        TMLEventBChannel<ParamType,0>* event__AppC__opened__AppC__opened = new TMLEventBChannel<ParamType,0>(68,"AppC__opened__AppC__opened",0,0,0,0,false,false);
        addEvent(event__AppC__opened__AppC__opened);
        TMLEventFChannel<ParamType,0>* event__AppC__pTSConfirm__AppC__pTSConfirm = new TMLEventFChannel<ParamType,0>(70,"AppC__pTSConfirm__AppC__pTSConfirm",0,0,0,1,0);
        addEvent(event__AppC__pTSConfirm__AppC__pTSConfirm);
        TMLEventFChannel<ParamType,0>* event__AppC__pTS__AppC__pTS = new TMLEventFChannel<ParamType,0>(61,"AppC__pTS__AppC__pTS",0,0,0,1,0);
        addEvent(event__AppC__pTS__AppC__pTS);
        TMLEventBChannel<ParamType,0>* event__AppC__receive_Application__AppC__receive_Application = new TMLEventBChannel<ParamType,0>(66,"AppC__receive_Application__AppC__receive_Application",0,0,0,0,false,false);
        addEvent(event__AppC__receive_Application__AppC__receive_Application);
        TMLEventBChannel<ParamType,0>* event__AppC__receive__AppC__receive = new TMLEventBChannel<ParamType,0>(72,"AppC__receive__AppC__receive",0,0,0,0,false,false);
        addEvent(event__AppC__receive__AppC__receive);
        TMLEventFChannel<ParamType,0>* event__AppC__reset__AppC__reset = new TMLEventFChannel<ParamType,0>(60,"AppC__reset__AppC__reset",0,0,0,1,0);
        addEvent(event__AppC__reset__AppC__reset);
        TMLEventBChannel<ParamType,0>* event__AppC__send_TCP__AppC__send_TCP = new TMLEventBChannel<ParamType,0>(73,"AppC__send_TCP__AppC__send_TCP",0,0,0,0,false,false);
        addEvent(event__AppC__send_TCP__AppC__send_TCP);
        TMLEventBChannel<ParamType,0>* event__AppC__send__AppC__send = new TMLEventBChannel<ParamType,0>(65,"AppC__send__AppC__send",0,0,0,0,false,false);
        addEvent(event__AppC__send__AppC__send);
        TMLEventFChannel<ParamType,0>* event__AppC__stop__AppC__stop = new TMLEventFChannel<ParamType,0>(67,"AppC__stop__AppC__stop",0,0,0,1,0);
        addEvent(event__AppC__stop__AppC__stop);
        TMLEventFChannel<ParamType,0>* event__AppC__timeOut__AppC__timeOut = new TMLEventFChannel<ParamType,0>(64,"AppC__timeOut__AppC__timeOut",0,0,0,1,0);
        addEvent(event__AppC__timeOut__AppC__timeOut);
        
        //Declaration of requests
        TMLEventBChannel<ParamType,0>* reqChannel_AppC__Application = new TMLEventBChannel<ParamType,0>(81,"reqChannel_AppC__Application",0,0,0,0,true,false);
        addRequest( reqChannel_AppC__Application);
        TMLEventBChannel<ParamType,0>* reqChannel_AppC__SmartCard = new TMLEventBChannel<ParamType,0>(80,"reqChannel_AppC__SmartCard",0,0,0,0,true,false);
        addRequest( reqChannel_AppC__SmartCard);
        TMLEventBChannel<ParamType,0>* reqChannel_AppC__TCPIP = new TMLEventBChannel<ParamType,0>(79,"reqChannel_AppC__TCPIP",0,0,0,0,true,false);
        addRequest( reqChannel_AppC__TCPIP);
        TMLEventBChannel<ParamType,0>* reqChannel_AppC__Timer = new TMLEventBChannel<ParamType,0>(78,"reqChannel_AppC__Timer",0,0,0,0,true,false);
        addRequest( reqChannel_AppC__Timer);
        
        //Set bus schedulers
        Bus0_0->setScheduler( (WorkloadSource*) new RRScheduler("Bus0_RRSched", 0, 5, 2, array(3, (WorkloadSource*) HWA0_0_Bus0_Master, (WorkloadSource*) CPU1_0_Bus0_Master, (WorkloadSource*) FPGA0_0_Bus0_Master), 3));
        
        //Declaration of tasks
        AppC__InterfaceDevice* task__AppC__InterfaceDevice = new AppC__InterfaceDevice(6,0,"AppC__InterfaceDevice", array(1,CPU1_1), 1
        ,channel__AppC__fromDtoSC
        ,channel__AppC__fromSCtoD
        ,event__AppC__answerToReset__AppC__answerToReset
        ,event__AppC__data_Ready_SC__AppC__data_Ready_SC
        ,event__AppC__data_Ready__AppC__data_Ready
        ,event__AppC__end__AppC__end
        ,event__AppC__pTSConfirm__AppC__pTSConfirm
        ,event__AppC__pTS__AppC__pTS
        ,event__AppC__reset__AppC__reset
        , reqChannel_AppC__SmartCard
        );
        addTask(task__AppC__InterfaceDevice);
        AppC__Timer* task__AppC__Timer = new AppC__Timer(13,0,"AppC__Timer", array(1 ,HWA0), 1
        ,event__AppC__stop__AppC__stop
        ,event__AppC__timeOut__AppC__timeOut
        ,reqChannel_AppC__Timer
        );
        addTask(task__AppC__Timer);
        AppC__TCPIP* task__AppC__TCPIP = new AppC__TCPIP(16,0,"AppC__TCPIP", array(1 ,FPGA0), 1
        ,channel__AppC__fromAtoT
        ,channel__AppC__fromPtoT
        ,channel__AppC__fromTtoA
        ,channel__AppC__fromTtoP
        ,channel__AppC__temp
        ,event__AppC__abort__AppC__abort
        ,event__AppC__close__AppC__close
        ,event__AppC__open__AppC__open
        ,event__AppC__opened__AppC__opened
        ,event__AppC__receive_Application__AppC__receive_Application
        ,event__AppC__receive__AppC__receive
        ,event__AppC__send_TCP__AppC__send_TCP
        ,event__AppC__send__AppC__send
        ,event__AppC__stop__AppC__stop
        ,event__AppC__timeOut__AppC__timeOut
        , reqChannel_AppC__Timer
        ,reqChannel_AppC__TCPIP
        );
        addTask(task__AppC__TCPIP);
        AppC__SmartCard* task__AppC__SmartCard = new AppC__SmartCard(26,0,"AppC__SmartCard", array(1 ,FPGA0), 1
        ,channel__AppC__fromDtoSC
        ,channel__AppC__fromPtoT
        ,channel__AppC__fromSCtoD
        ,channel__AppC__fromTtoP
        ,event__AppC__answerToReset__AppC__answerToReset
        ,event__AppC__connectionOpened__AppC__connectionOpened
        ,event__AppC__data_Ready_SC__AppC__data_Ready_SC
        ,event__AppC__data_Ready__AppC__data_Ready
        ,event__AppC__end__AppC__end
        ,event__AppC__pTSConfirm__AppC__pTSConfirm
        ,event__AppC__pTS__AppC__pTS
        ,event__AppC__receive__AppC__receive
        ,event__AppC__reset__AppC__reset
        ,event__AppC__send__AppC__send
        , reqChannel_AppC__Application
        , reqChannel_AppC__TCPIP
        ,reqChannel_AppC__SmartCard
        );
        addTask(task__AppC__SmartCard);
        AppC__Application* task__AppC__Application = new AppC__Application(37,0,"AppC__Application", array(1 ,FPGA0), 1
        ,channel__AppC__fromAtoT
        ,channel__AppC__fromTtoA
        ,event__AppC__abort__AppC__abort
        ,event__AppC__close__AppC__close
        ,event__AppC__connectionOpened__AppC__connectionOpened
        ,event__AppC__open__AppC__open
        ,event__AppC__opened__AppC__opened
        ,event__AppC__receive_Application__AppC__receive_Application
        ,event__AppC__send_TCP__AppC__send_TCP
        , reqChannel_AppC__TCPIP
        ,reqChannel_AppC__Application
        );
        addTask(task__AppC__Application);
    }
    
    void generateTEPEs(){
        //Declaration of TEPEs
        
    }
};

#include <main.h>
