#ifndef HOCAPPMAPPED__FORK3__H
#define HOCAPPMAPPED__FORK3__H

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
class HOCAppMAPPED__fork3: public TMLTask {
    private:
    // Attributes
    ParamType r_size;
    ParamType looprd__0;
    ParamType rd__0__0;
    ParamType rd__0__1;
    ParamType rnd__0;
    TMLChannel* _channels[6];
    
    TMLWaitCommand _wait177;
    TMLReadCommand _read175;
    TMLActionCommand _action357;
    TMLActionCommand _action360;
    TMLActionCommand _lpIncAc353;
    TMLActionCommand _action358;
    TMLSendCommand _send176;
    TMLWriteCommand _write179;
    TMLActionCommand _action361;
    TMLSendCommand _send174;
    TMLWriteCommand _write173;
    TMLStopCommand _stop352;
    TMLRandomChoiceCommand _choice352;
    TMLStopCommand _stop355;
    TMLRandomChoiceCommand _lpChoice353;
    TMLActionCommand _action384;
    
    Parameter* _wait177_func(Parameter* ioParam);
    TMLLength _read175_func();
    void _lpIncAc353_func();
    Parameter* _send176_func(Parameter* ioParam);
    TMLLength _write179_func();
    void _action358_func();
    Parameter* _send174_func(Parameter* ioParam);
    TMLLength _write173_func();
    void _action361_func();
    unsigned int _choice352_func(ParamType& oMin, ParamType& oMax);
    unsigned int _lpChoice353_func(ParamType& oMin, ParamType& oMax);
    void _action384_func();
    void _action360_func();
    void _action357_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__fork3(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in
    , TMLChannel* channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in
    , TMLChannel* channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in
    , TMLEventChannel* event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in
    , TMLEventChannel* event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in
    , TMLEventChannel* event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
