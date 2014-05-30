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
    TMLWaitCommand _wait238;
    TMLSendCommand _send239;
    TMLWaitCommand _wait240;
    TMLSendCommand _send241;
    TMLRequestCommand _request242;
    TMLRequestCommand _request243;
    TMLActionCommand _lpIncAc254;
    TMLReadCommand _read247;
    TMLSendCommand _send245;
    TMLWriteCommand _write249;
    TMLReadCommand _read251;
    TMLSendCommand _send250;
    TMLWriteCommand _write252;
    TMLSelectCommand _select256;
    TMLRandomChoiceCommand _lpChoice254;
    TMLActionCommand _action265;
    
    void _lpIncAc254_func();
    unsigned int _lpChoice254_func(ParamType& oMin, ParamType& oMax);
    void _action265_func();
    
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
