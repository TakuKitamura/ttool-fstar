#ifndef DIPLODOCUS_C_DESIGN__ALICE__H
#define DIPLODOCUS_C_DESIGN__ALICE__H

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
class DIPLODOCUS_C_Design__Alice: public TMLTask {
    private:
    // Attributes
    ParamType rnd__0;
    TMLChannel* _channels[3];
    
    TMLWaitCommand _wait15;
    TMLReadCommand _read14;
    TMLStopCommand _stop16;
    
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    DIPLODOCUS_C_Design__Alice(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__DIPLODOCUS_C_Design__Phone
    , TMLEventChannel* event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call
    , TMLEventChannel* event__DIPLODOCUS_C_Design__comm__DIPLODOCUS_C_Design__comm
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
