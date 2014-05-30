#ifndef HOCAPPMAPPED__X_DMAFORK1__H
#define HOCAPPMAPPED__X_DMAFORK1__H

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
class HOCAppMAPPED__X_DMAfork1: public TMLTask {
    private:
    // Attributes
    ParamType size;
    ParamType i;
    ParamType arg1__req;
    ParamType rnd__0;
    TMLChannel* _channels[3];
    
    TMLWaitCommand _waitOnRequest;
    TMLActionCommand _action310;
    TMLActionCommand _lpIncAc304;
    TMLReadCommand _read309;
    TMLExeciCommand _execi306;
    TMLWriteCommand _write308;
    TMLRandomChoiceCommand _lpChoice304;
    TMLActionCommand _action376;
    
    Parameter* waitOnRequest_func(Parameter* ioParam);
    void _lpIncAc304_func();
    TMLLength _execi306_func();
    unsigned int _lpChoice304_func(ParamType& oMin, ParamType& oMax);
    void _action376_func();
    void _action310_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__X_DMAfork1(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in
    , TMLChannel* channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in
    , TMLEventChannel* requestChannel
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
