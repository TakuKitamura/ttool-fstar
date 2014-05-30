#ifndef HOCAPPMAPPED__FORK1__H
#define HOCAPPMAPPED__FORK1__H

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
class HOCAppMAPPED__fork1: public TMLTask {
    private:
    // Attributes
    ParamType r_size;
    ParamType looprd__0;
    ParamType rd__0__0;
    ParamType rd__0__1;
    ParamType rnd__0;
    TMLChannel* _channels[6];
    
    TMLWaitCommand _wait206;
    TMLReadCommand _read205;
    TMLActionCommand _action343;
    TMLActionCommand _action346;
    TMLActionCommand _lpIncAc339;
    TMLActionCommand _action344;
    TMLSendCommand _send208;
    TMLWriteCommand _write209;
    TMLActionCommand _action347;
    TMLSendCommand _send210;
    TMLWriteCommand _write211;
    TMLStopCommand _stop338;
    TMLRandomChoiceCommand _choice338;
    TMLStopCommand _stop341;
    TMLRandomChoiceCommand _lpChoice339;
    TMLActionCommand _action385;
    
    Parameter* _wait206_func(Parameter* ioParam);
    TMLLength _read205_func();
    void _lpIncAc339_func();
    Parameter* _send208_func(Parameter* ioParam);
    TMLLength _write209_func();
    void _action344_func();
    Parameter* _send210_func(Parameter* ioParam);
    TMLLength _write211_func();
    void _action347_func();
    unsigned int _choice338_func(ParamType& oMin, ParamType& oMax);
    unsigned int _lpChoice339_func(ParamType& oMin, ParamType& oMax);
    void _action385_func();
    void _action346_func();
    void _action343_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__fork1(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in
    , TMLChannel* channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in
    , TMLChannel* channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in
    , TMLEventChannel* event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in
    , TMLEventChannel* event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in
    , TMLEventChannel* event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
