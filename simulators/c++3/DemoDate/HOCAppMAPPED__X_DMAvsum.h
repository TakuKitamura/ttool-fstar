#ifndef HOCAPPMAPPED__X_DMAVSUM__H
#define HOCAPPMAPPED__X_DMAVSUM__H

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
class HOCAppMAPPED__X_DMAvsum: public TMLTask {
    private:
    // Attributes
    ParamType size;
    ParamType i;
    ParamType arg1__req;
    ParamType rnd__0;
    TMLChannel* _channels[3];
    
    TMLWaitCommand _waitOnRequest;
    TMLActionCommand _action289;
    TMLActionCommand _lpIncAc284;
    TMLReadCommand _read288;
    TMLExeciCommand _execi285;
    TMLWriteCommand _write287;
    TMLRandomChoiceCommand _lpChoice284;
    TMLActionCommand _action382;
    
    Parameter* waitOnRequest_func(Parameter* ioParam);
    void _lpIncAc284_func();
    TMLLength _read288_func();
    TMLLength _execi285_func();
    TMLLength _write287_func();
    unsigned int _lpChoice284_func(ParamType& oMin, ParamType& oMax);
    void _action382_func();
    void _action289_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__X_DMAvsum(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1
    , TMLChannel* channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in
    , TMLEventChannel* requestChannel
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
