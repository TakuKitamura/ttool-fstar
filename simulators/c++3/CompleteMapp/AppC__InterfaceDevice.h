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
    
    TMLRequestCommand _request73;
    TMLSendCommand _send74;
    TMLWaitCommand _wait75;
    TMLSendCommand _send76;
    TMLWaitCommand _wait77;
    TMLActionCommand _lpIncAc86;
    TMLWriteCommand _write78;
    TMLSendCommand _send79;
    TMLNotifiedCommand _notified81;
    TMLWaitCommand _wait84;
    TMLReadCommand _read83;
    TMLStopCommand _stop72;
    TMLRandomChoiceCommand _choice72;
    TMLRandomChoiceCommand _choice71;
    TMLStopCommand _stop87;
    TMLRandomChoiceCommand _lpChoice86;
    TMLActionCommand _action259;
    
    void _lpIncAc86_func();
    unsigned int _choice72_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice71_func(ParamType& oMin, ParamType& oMax);
    unsigned int _lpChoice86_func(ParamType& oMin, ParamType& oMax);
    void _action259_func();
    
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
