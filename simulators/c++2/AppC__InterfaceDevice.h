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
#include <TMLExeciRangeCommand.h>
#include <TMLRequestCommand.h>
#include <TMLSendCommand.h>
#include <TMLWaitCommand.h>
#include <TMLWriteCommand.h>
#include <TMLStopCommand.h>
#include <TMLWriteMultCommand.h>
#include <TMLRandomCommand.h>

extern "C" bool condFunc(TMLTask* _ioTask_);
class AppC__InterfaceDevice: public TMLTask {
    private:
    // Attributes
    ParamType resetType;
    ParamType x;
    ParamType i;
    ParamType nbOfComputedPackets;
    ParamType b;
    ParamType rnd__0;
    TMLChannel* _channels[9];
    
    TMLRequestCommand _request86;
    TMLSendCommand _send87;
    TMLWaitCommand _wait88;
    TMLSendCommand _send89;
    TMLWaitCommand _wait90;
    TMLActionCommand _lpIncAc99;
    TMLWriteCommand _write91;
    TMLSendCommand _send92;
    TMLNotifiedCommand _notified94;
    TMLWaitCommand _wait97;
    TMLReadCommand _read96;
    TMLStopCommand _stop85;
    TMLRandomChoiceCommand _choice85;
    TMLRandomChoiceCommand _choice84;
    TMLSendCommand _send83;
    TMLStopCommand _stop100;
    TMLRandomChoiceCommand _lpChoice99;
    TMLActionCommand _action286;
    
    void _lpIncAc99_func();
    Parameter* _send92_func(Parameter* ioParam);
    unsigned int _choice85_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice84_func(ParamType& oMin, ParamType& oMax);
    unsigned int _lpChoice99_func(ParamType& oMin, ParamType& oMax);
    void _action286_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    AppC__InterfaceDevice(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__AppC__fromDtoSC
    , TMLChannel* channel__AppC__fromSCtoD
    , TMLEventChannel* event__AppC__answerToReset__AppC__answerToReset
    , TMLEventChannel* event__AppC__data_Ready_SC__AppC__data_Ready_SC
    , TMLEventChannel* event__AppC__data_Ready__AppC__data_Ready
    , TMLEventChannel* event__AppC__end__AppC__end
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
