#ifndef HOCAPPMAPPED__FORK2__H
#define HOCAPPMAPPED__FORK2__H

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
class HOCAppMAPPED__fork2: public TMLTask {
    private:
    // Attributes
    ParamType PREX;
    ParamType r_size;
    ParamType looprd__0;
    ParamType rd__0__0;
    ParamType rd__0__1;
    ParamType rnd__0;
    TMLChannel* _channels[6];
    
    TMLWaitCommand _wait332;
    TMLReadCommand _read329;
    TMLActionCommand _action353;
    TMLActionCommand _action356;
    TMLActionCommand _lpIncAc349;
    TMLActionCommand _action354;
    TMLSendCommand _send331;
    TMLWriteCommand _write335;
    TMLActionCommand _action357;
    TMLSendCommand _send330;
    TMLWriteCommand _write334;
    TMLStopCommand _stop348;
    TMLRandomChoiceCommand _choice348;
    TMLStopCommand _stop351;
    TMLRandomChoiceCommand _lpChoice349;
    TMLActionCommand _action388;
    
    Parameter* _wait332_func(Parameter* ioParam);
    TMLLength _read329_func();
    void _lpIncAc349_func();
    Parameter* _send331_func(Parameter* ioParam);
    TMLLength _write335_func();
    void _action354_func();
    Parameter* _send330_func(Parameter* ioParam);
    TMLLength _write334_func();
    void _action357_func();
    unsigned int _choice348_func(ParamType& oMin, ParamType& oMax);
    unsigned int _lpChoice349_func(ParamType& oMin, ParamType& oMax);
    void _action388_func();
    void _action356_func();
    void _action353_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__fork2(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in
    , TMLChannel* channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in
    , TMLChannel* channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in
    , TMLEventChannel* event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in
    , TMLEventChannel* event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in
    , TMLEventChannel* event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
