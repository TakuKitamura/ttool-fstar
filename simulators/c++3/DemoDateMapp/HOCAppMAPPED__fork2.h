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
    
    TMLWaitCommand _wait239;
    TMLReadCommand _read236;
    TMLActionCommand _action377;
    TMLActionCommand _action380;
    TMLActionCommand _lpIncAc373;
    TMLActionCommand _action378;
    TMLSendCommand _send238;
    TMLWriteCommand _write242;
    TMLActionCommand _action381;
    TMLSendCommand _send237;
    TMLWriteCommand _write241;
    TMLStopCommand _stop372;
    TMLRandomChoiceCommand _choice372;
    TMLStopCommand _stop375;
    TMLRandomChoiceCommand _lpChoice373;
    TMLActionCommand _action390;
    
    Parameter* _wait239_func(Parameter* ioParam);
    TMLLength _read236_func();
    void _lpIncAc373_func();
    Parameter* _send238_func(Parameter* ioParam);
    TMLLength _write242_func();
    void _action378_func();
    Parameter* _send237_func(Parameter* ioParam);
    TMLLength _write241_func();
    void _action381_func();
    unsigned int _choice372_func(ParamType& oMin, ParamType& oMax);
    unsigned int _lpChoice373_func(ParamType& oMin, ParamType& oMax);
    void _action390_func();
    void _action380_func();
    void _action377_func();
    
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
