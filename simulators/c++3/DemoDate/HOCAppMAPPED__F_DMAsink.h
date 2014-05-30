#ifndef HOCAPPMAPPED__F_DMASINK__H
#define HOCAPPMAPPED__F_DMASINK__H

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
class HOCAppMAPPED__F_DMAsink: public TMLTask {
    private:
    // Attributes
    ParamType size;
    ParamType rnd__0;
    TMLChannel* _channels[2];
    
    TMLWaitCommand _wait263;
    TMLRequestCommand _request260;
    TMLSendCommand _send262;
    TMLStopCommand _stop261;
    
    Parameter* _wait263_func(Parameter* ioParam);
    Parameter* _request260_func(Parameter* ioParam);
    Parameter* _send262_func(Parameter* ioParam);
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    HOCAppMAPPED__F_DMAsink(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLEventChannel* event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2
    , TMLEventChannel* event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in
    , TMLEventChannel* request__HOCAppMAPPED__r_DMAsink
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
