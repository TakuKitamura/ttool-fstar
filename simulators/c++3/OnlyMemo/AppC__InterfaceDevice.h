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
    
    TMLRequestCommand _request62;
    TMLSendCommand _send63;
    TMLWaitCommand _wait64;
    TMLSendCommand _send65;
    TMLWaitCommand _wait66;
    TMLActionCommand _lpIncAc75;
    TMLWriteCommand _write67;
    TMLSendCommand _send68;
    TMLNotifiedCommand _notified70;
    TMLWaitCommand _wait73;
    TMLReadCommand _read72;
    TMLStopCommand _stop61;
    TMLRandomChoiceCommand _choice61;
    TMLRandomChoiceCommand _choice60;
    TMLStopCommand _stop76;
    TMLRandomChoiceCommand _lpChoice75;
    TMLActionCommand _action256;
    
    void _lpIncAc75_func();
    unsigned int _choice61_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice60_func(ParamType& oMin, ParamType& oMax);
    unsigned int _lpChoice75_func(ParamType& oMin, ParamType& oMax);
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
