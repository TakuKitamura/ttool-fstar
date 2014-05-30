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
    
    TMLWaitCommand _wait227;
    TMLReadCommand _read226;
    TMLActionCommand _action367;
    TMLActionCommand _action370;
    TMLActionCommand _lpIncAc363;
    TMLActionCommand _action368;
    TMLSendCommand _send229;
    TMLWriteCommand _write230;
    TMLActionCommand _action371;
    TMLSendCommand _send231;
    TMLWriteCommand _write232;
    TMLStopCommand _stop362;
    TMLRandomChoiceCommand _choice362;
    TMLStopCommand _stop365;
    TMLRandomChoiceCommand _lpChoice363;
    TMLActionCommand _action387;
    
    Parameter* _wait227_func(Parameter* ioParam);
    TMLLength _read226_func();
    void _lpIncAc363_func();
    Parameter* _send229_func(Parameter* ioParam);
    TMLLength _write230_func();
    void _action368_func();
    Parameter* _send231_func(Parameter* ioParam);
    TMLLength _write232_func();
    void _action371_func();
    unsigned int _choice362_func(ParamType& oMin, ParamType& oMax);
    unsigned int _lpChoice363_func(ParamType& oMin, ParamType& oMax);
    void _action387_func();
    void _action370_func();
    void _action367_func();
    
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
