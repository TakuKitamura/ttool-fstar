#ifndef APPC__INTERFACEDEVICE__H
#define APPC__INTERFACEDEVICE__H

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
class AppC__InterfaceDevice: public TMLTask {
    private:
    // Attributes
    ParamType resetType;
    ParamType x;
    ParamType i;
    ParamType nbOfComputedPackets;
    ParamType rnd__0;
    TMLChannel* _channels[8];
    
    TMLRequestCommand _request61;
    TMLSendCommand _send62;
    TMLWaitCommand _wait63;
    TMLSendCommand _send64;
    TMLWaitCommand _wait65;
    TMLActionCommand _lpIncAc74;
    TMLWriteCommand _write66;
    TMLSendCommand _send67;
    TMLNotifiedCommand _notified69;
    TMLWaitCommand _wait72;
    TMLReadCommand _read71;
    TMLStopCommand _stop60;
    TMLRandomChoiceCommand _choice60;
    TMLRandomChoiceCommand _choice59;
    TMLStopCommand _stop75;
    TMLRandomChoiceCommand _lpChoice74;
    TMLActionCommand _action256;
    
    void _lpIncAc74_func();
    unsigned int _choice60_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice59_func(ParamType& oMin, ParamType& oMax);
    unsigned int _lpChoice74_func(ParamType& oMin, ParamType& oMax);
    void _action256_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    AppC__InterfaceDevice(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__AppC__fromDtoSC__AppC__fromDtoSC0
    , TMLChannel* channel__AppC__fromSCtoD
    , TMLEventChannel* event__AppC__answerToReset__AppC__answerToReset
    , TMLEventChannel* event__AppC__data_Ready_SC__AppC__data_Ready_SC
    , TMLEventChannel* event__AppC__data_Ready__AppC__data_Ready
    , TMLEventChannel* event__AppC__pTSConfirm__AppC__pTSConfirm
    , TMLEventChannel* event__AppC__pTS__AppC__pTS
    , TMLEventChannel* event__AppC__reset__AppC__reset
    , TMLEventChannel* request__AppC__activation
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
