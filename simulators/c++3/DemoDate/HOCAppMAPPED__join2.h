#ifndef HOCAPPMAPPED__JOIN2__H
#define HOCAPPMAPPED__JOIN2__H

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
class HOCAppMAPPED__join2: public TMLTask {
    private:
    // Attributes
    ParamType r_size1;
    ParamType r_size2;
    ParamType rnd__0;
    TMLChannel* _channels[6];
    
    TMLWaitCommand _wait215;
    TMLReadCommand _read214;
    TMLWaitCommand _wait219;
    TMLReadCommand _read218;
    TMLSendCommand _send213;
    TMLWriteCommand _write217;
    TMLStopCommand _stop216;
    
    Parameter* _wait215_func(Parameter* ioParam);
    TMLLength _read214_func();
    Parameter* _wait219_func(Parameter* ioParam);
    TMLLength _read218_func();
    Parameter* _send213_func(Parameter* ioParam);
    TMLLength _write217_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__join2(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2
    , TMLChannel* channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1
    , TMLChannel* channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in
    , TMLEventChannel* event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2
    , TMLEventChannel* event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1
    , TMLEventChannel* event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
