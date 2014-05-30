#ifndef HOCAPPMAPPED__JOIN1__H
#define HOCAPPMAPPED__JOIN1__H

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
class HOCAppMAPPED__join1: public TMLTask {
    private:
    // Attributes
    ParamType r_size1;
    ParamType r_size2;
    ParamType r_size3;
    ParamType rnd__0;
    TMLChannel* _channels[8];
    
    TMLWaitCommand _wait208;
    TMLReadCommand _read207;
    TMLWaitCommand _wait212;
    TMLReadCommand _read214;
    TMLWaitCommand _wait211;
    TMLReadCommand _read213;
    TMLSendCommand _send206;
    TMLWriteCommand _write210;
    TMLStopCommand _stop209;
    
    Parameter* _wait208_func(Parameter* ioParam);
    TMLLength _read207_func();
    Parameter* _wait212_func(Parameter* ioParam);
    TMLLength _read214_func();
    Parameter* _wait211_func(Parameter* ioParam);
    TMLLength _read213_func();
    Parameter* _send206_func(Parameter* ioParam);
    TMLLength _write210_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__join1(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2
    , TMLChannel* channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3
    , TMLChannel* channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1
    , TMLChannel* channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in
    , TMLEventChannel* event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2
    , TMLEventChannel* event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3
    , TMLEventChannel* event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1
    , TMLEventChannel* event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
