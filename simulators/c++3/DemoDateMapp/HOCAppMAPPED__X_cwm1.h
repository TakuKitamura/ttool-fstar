#ifndef HOCAPPMAPPED__X_CWM1__H
#define HOCAPPMAPPED__X_CWM1__H

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
class HOCAppMAPPED__X_cwm1: public TMLTask {
    private:
    // Attributes
    ParamType size;
    ParamType arg1__req;
    ParamType rnd__0;
    TMLChannel* _channels[3];
    
    TMLWaitCommand _waitOnRequest;
    TMLActionCommand _action294;
    TMLReadCommand _read293;
    TMLExeciCommand _execi290;
    TMLWriteCommand _write292;
    
    Parameter* waitOnRequest_func(Parameter* ioParam);
    TMLLength _read293_func();
    TMLLength _execi290_func();
    TMLLength _write292_func();
    void _action294_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__X_cwm1(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in
    , TMLChannel* channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in
    , TMLEventChannel* requestChannel
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
