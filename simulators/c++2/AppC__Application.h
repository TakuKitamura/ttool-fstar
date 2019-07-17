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
#include <TMLExeciRangeCommand.h>
#include <TMLRequestCommand.h>
#include <TMLSendCommand.h>
#include <TMLWaitCommand.h>
#include <TMLWriteCommand.h>
#include <TMLStopCommand.h>
#include <TMLWriteMultCommand.h>
#include <TMLRandomCommand.h>

extern "C" bool condFunc(TMLTask* _ioTask_);
class AppC__Application: public TMLTask {
    private:
    // Attributes
    ParamType rnd__0;
    TMLChannel* _channels[10];
    
    TMLWaitCommand _waitOnRequest;
    TMLSendCommand _send277;
    TMLWaitCommand _wait274;
    TMLSendCommand _send275;
    TMLExeciCommand _execi273;
    TMLWriteCommand _write278;
    TMLSendCommand _send279;
    TMLSendCommand _send282;
    TMLSendCommand _send281;
    TMLRandomChoiceCommand _choice276;
    
    unsigned int _choice276_func(ParamType& oMin, ParamType& oMax);
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    AppC__Application(ID iID, Priority iPriority, std::string iName, FPGA** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__AppC__fromAtoT
    , TMLChannel* channel__AppC__fromTtoA
    , TMLEventChannel* event__AppC__abort__AppC__abort
    , TMLEventChannel* event__AppC__close__AppC__close
    , TMLEventChannel* event__AppC__connectionOpened__AppC__connectionOpened
    , TMLEventChannel* event__AppC__open__AppC__open
    , TMLEventChannel* event__AppC__opened__AppC__opened
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
