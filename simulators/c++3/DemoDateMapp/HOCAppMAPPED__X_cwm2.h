#ifndef HOCAPPMAPPED__X_CWM2__H
#define HOCAPPMAPPED__X_CWM2__H

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
class HOCAppMAPPED__X_cwm2: public TMLTask {
    private:
    // Attributes
    ParamType size;
    ParamType arg1__req;
    ParamType rnd__0;
    TMLChannel* _channels[3];
    
    TMLWaitCommand _waitOnRequest;
    TMLActionCommand _action306;
    TMLReadCommand _read305;
    TMLExeciCommand _execi302;
    TMLWriteCommand _write304;
    
    Parameter* waitOnRequest_func(Parameter* ioParam);
    TMLLength _read305_func();
    TMLLength _execi302_func();
    TMLLength _write304_func();
    void _action306_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__X_cwm2(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in
    , TMLChannel* channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in
    , TMLEventChannel* requestChannel
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
