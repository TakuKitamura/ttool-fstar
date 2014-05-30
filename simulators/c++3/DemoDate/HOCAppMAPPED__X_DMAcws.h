#ifndef HOCAPPMAPPED__X_DMACWS__H
#define HOCAPPMAPPED__X_DMACWS__H

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
class HOCAppMAPPED__X_DMAcws: public TMLTask {
    private:
    // Attributes
    ParamType size;
    ParamType i;
    ParamType arg1__req;
    ParamType rnd__0;
    TMLChannel* _channels[3];
    
    TMLWaitCommand _waitOnRequest;
    TMLActionCommand _action297;
    TMLActionCommand _lpIncAc292;
    TMLReadCommand _read296;
    TMLExeciCommand _execi293;
    TMLWriteCommand _write295;
    TMLRandomChoiceCommand _lpChoice292;
    TMLActionCommand _action373;
    
    Parameter* waitOnRequest_func(Parameter* ioParam);
    void _lpIncAc292_func();
    TMLLength _execi293_func();
    unsigned int _lpChoice292_func(ParamType& oMin, ParamType& oMax);
    void _action373_func();
    void _action297_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__X_DMAcws(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3
    , TMLChannel* channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in
    , TMLEventChannel* requestChannel
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
