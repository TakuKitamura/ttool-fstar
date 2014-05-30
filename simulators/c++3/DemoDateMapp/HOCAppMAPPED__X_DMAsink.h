#ifndef HOCAPPMAPPED__X_DMASINK__H
#define HOCAPPMAPPED__X_DMASINK__H

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
class HOCAppMAPPED__X_DMAsink: public TMLTask {
    private:
    // Attributes
    ParamType size;
    ParamType i;
    ParamType arg1__req;
    ParamType rnd__0;
    TMLChannel* _channels[3];
    
    TMLWaitCommand _waitOnRequest;
    TMLActionCommand _action351;
    TMLActionCommand _lpIncAc346;
    TMLReadCommand _read350;
    TMLExeciCommand _execi345;
    TMLWriteCommand _write349;
    TMLRandomChoiceCommand _lpChoice346;
    TMLActionCommand _action405;
    
    Parameter* waitOnRequest_func(Parameter* ioParam);
    void _lpIncAc346_func();
    TMLLength _execi345_func();
    unsigned int _lpChoice346_func(ParamType& oMin, ParamType& oMax);
    void _action405_func();
    void _action351_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__X_DMAsink(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2
    , TMLChannel* channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in
    , TMLEventChannel* requestChannel
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
