#ifndef APPC__SMARTCARD__H
#define APPC__SMARTCARD__H

#include <TMLTask.h>
#include <definitions.h>

#include <TMLbrbwChannel.h>
#include <TMLbrnbwChannel.h>
#include <TMLnbrnbwChannel.h>

#include <TMLEventBChannel.h>
#include <TMLEventFChannel.h>
#include <TMLEventFBChannel.h>

#include <TMLActionCommand.h>
#include <TMLChoiceCommand.h>
#include <TMLRandomChoiceCommand.h>
#include <TMLExeciCommand.h>
#include <TMLSelectCommand.h>
#include <TMLReadCommand.h>
#include <TMLNotifiedCommand.h>
#include<TMLExeciRangeCommand.h>
#include <TMLRequestCommand.h>
#include <TMLSendCommand.h>
#include <TMLWaitCommand.h>
#include <TMLWriteCommand.h>
#include <TMLStopCommand.h>
#include<TMLWriteMultCommand.h>
#include <TMLRandomCommand.h>

extern "C" bool condFunc(TMLTask* _ioTask_);
class AppC__SmartCard: public TMLTask {
    private:
    // Attributes
    ParamType resetType;
    ParamType a;
    ParamType b;
    ParamType i;
    ParamType j;
    ParamType x;
    ParamType tcpctrl__a;
    ParamType tcpctrl__state;
    ParamType rnd__0;
    TMLChannel* _channels[13];
    
    TMLWaitCommand _waitOnRequest;
    TMLWaitCommand _wait77;
    TMLSendCommand _send78;
    TMLWaitCommand _wait79;
    TMLSendCommand _send80;
    TMLRequestCommand _request81;
    TMLRequestCommand _request82;
    TMLActionCommand _lpIncAc93;
    TMLReadCommand _read86;
    TMLSendCommand _send84;
    TMLWriteCommand _write88;
    TMLReadCommand _read90;
    TMLSendCommand _send89;
    TMLWriteCommand _write91;
    TMLSelectCommand _select95;
    TMLRandomChoiceCommand _lpChoice93;
    TMLActionCommand _action259;
    
    void _lpIncAc93_func();
    unsigned int _lpChoice93_func(ParamType& oMin, ParamType& oMax);
    void _action259_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    AppC__SmartCard(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__AppC__fromDtoSC__AppC__fromDtoSC0
    , TMLChannel* channel__AppC__fromPtoT
    , TMLChannel* channel__AppC__fromSCtoD
    , TMLChannel* channel__AppC__fromTtoP
    , TMLEventChannel* event__AppC__answerToReset__AppC__answerToReset
    , TMLEventChannel* event__AppC__data_Ready_SC__AppC__data_Ready_SC
    , TMLEventChannel* event__AppC__data_Ready__AppC__data_Ready
    , TMLEventChannel* event__AppC__pTSConfirm__AppC__pTSConfirm
    , TMLEventChannel* event__AppC__pTS__AppC__pTS
    , TMLEventChannel* event__AppC__receive__AppC__receive
    , TMLEventChannel* event__AppC__reset__AppC__reset
    , TMLEventChannel* event__AppC__send__AppC__send
    , TMLEventChannel* request__AppC__start_Application
    , TMLEventChannel* request__AppC__start_TCP_IP
    , TMLEventChannel* requestChannel
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
