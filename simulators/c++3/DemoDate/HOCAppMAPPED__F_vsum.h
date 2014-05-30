#ifndef HOCAPPMAPPED__F_VSUM__H
#define HOCAPPMAPPED__F_VSUM__H

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
class HOCAppMAPPED__F_vsum: public TMLTask {
    private:
    // Attributes
    ParamType size;
    ParamType rnd__0;
    TMLChannel* _channels[2];
    
    TMLWaitCommand _wait190;
    TMLRequestCommand _request187;
    TMLSendCommand _send189;
    TMLStopCommand _stop188;
    
    Parameter* _wait190_func(Parameter* ioParam);
    Parameter* _request187_func(Parameter* ioParam);
    Parameter* _send189_func(Parameter* ioParam);
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__F_vsum(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLEventChannel* event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in
    , TMLEventChannel* event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in
    , TMLEventChannel* request__HOCAppMAPPED__r_vsum
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
