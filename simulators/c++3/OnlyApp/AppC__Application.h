#ifndef APPC__APPLICATION__H
#define APPC__APPLICATION__H

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
class AppC__Application: public TMLTask {
    private:
    // Attributes
    ParamType rnd__0;
    TMLChannel* _channels[8];
    
    TMLWaitCommand _waitOnRequest;
    TMLSendCommand _send98;
    TMLWriteCommand _write99;
    TMLSendCommand _send100;
    TMLSendCommand _send103;
    TMLSendCommand _send102;
    TMLRandomChoiceCommand _choice97;
    
    unsigned int _choice97_func(ParamType& oMin, ParamType& oMax);
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    AppC__Application(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__AppC__fromAtoT
    , TMLChannel* channel__AppC__fromTtoA
    , TMLEventChannel* event__AppC__abort__AppC__abort
    , TMLEventChannel* event__AppC__close__AppC__close
    , TMLEventChannel* event__AppC__open__AppC__open
    , TMLEventChannel* event__AppC__receive_Application__AppC__receive_Application
    , TMLEventChannel* event__AppC__send_TCP__AppC__send_TCP
    , TMLEventChannel* request__AppC__start_TCP_IP
    , TMLEventChannel* requestChannel
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
