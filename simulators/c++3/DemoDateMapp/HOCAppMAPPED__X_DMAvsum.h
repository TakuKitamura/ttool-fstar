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
    TMLActionCommand _action343;
    TMLActionCommand _lpIncAc338;
    TMLReadCommand _read342;
    TMLExeciCommand _execi339;
    TMLWriteCommand _write341;
    TMLRandomChoiceCommand _lpChoice338;
    TMLActionCommand _action402;
    
    Parameter* waitOnRequest_func(Parameter* ioParam);
    void _lpIncAc338_func();
    TMLLength _read342_func();
    TMLLength _execi339_func();
    TMLLength _write341_func();
    unsigned int _lpChoice338_func(ParamType& oMin, ParamType& oMax);
    void _action402_func();
    void _action343_func();
    
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
