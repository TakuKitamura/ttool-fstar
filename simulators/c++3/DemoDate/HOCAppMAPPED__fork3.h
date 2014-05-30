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
    
    TMLWaitCommand _wait323;
    TMLReadCommand _read321;
    TMLActionCommand _action363;
    TMLActionCommand _action366;
    TMLActionCommand _lpIncAc359;
    TMLActionCommand _action364;
    TMLSendCommand _send322;
    TMLWriteCommand _write325;
    TMLActionCommand _action367;
    TMLSendCommand _send320;
    TMLWriteCommand _write319;
    TMLStopCommand _stop358;
    TMLRandomChoiceCommand _choice358;
    TMLStopCommand _stop361;
    TMLRandomChoiceCommand _lpChoice359;
    TMLActionCommand _action391;
    
    Parameter* _wait323_func(Parameter* ioParam);
    TMLLength _read321_func();
    void _lpIncAc359_func();
    Parameter* _send322_func(Parameter* ioParam);
    TMLLength _write325_func();
    void _action364_func();
    Parameter* _send320_func(Parameter* ioParam);
    TMLLength _write319_func();
    void _action367_func();
    unsigned int _choice358_func(ParamType& oMin, ParamType& oMax);
    unsigned int _lpChoice359_func(ParamType& oMin, ParamType& oMax);
    void _action391_func();
    void _action366_func();
    void _action363_func();
    
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
