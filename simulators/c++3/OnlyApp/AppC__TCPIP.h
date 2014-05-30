#ifndef APPC__TCPIP__H
#define APPC__TCPIP__H

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
class AppC__TCPIP: public TMLTask {
    private:
    // Attributes
    ParamType wind;
    ParamType i;
    ParamType j;
    ParamType a;
    ParamType b;
    ParamType tcpctrl__a;
    ParamType tcpctrl__state;
    ParamType seqi;
    ParamType rnd__0;
    TMLChannel* _channels[15];
    
    TMLWaitCommand _waitOnRequest;
    TMLActionCommand _lpIncAc140;
    TMLNotifiedCommand _notified143;
    TMLWaitCommand _wait153;
    TMLReadCommand _read144;
    TMLExeciCommand _execi121;
    TMLWriteCommand _write179;
    TMLSendCommand _send198;
    TMLReadCommand _read139;
    TMLExeciCommand _execi129;
    TMLSendCommand _send213;
    TMLReadCommand _read244;
    TMLExeciCommand _execi115;
    TMLWriteCommand _write214;
    TMLSendCommand _send217;
    TMLWriteCommand _write218;
    TMLActionCommand _action220;
    TMLActionCommand _action222;
    TMLActionCommand _action228;
    TMLActionCommand _action227;
    TMLActionCommand _action225;
    TMLRandomChoiceCommand _choice106;
    TMLRandomChoiceCommand _choice108;
    TMLRandomChoiceCommand _choice109;
    TMLRandomChoiceCommand _choice110;
    TMLExeciCommand _execi112;
    TMLWriteCommand _write211;
    TMLSendCommand _send243;
    TMLExeciCommand _execi111;
    TMLWriteCommand _write212;
    TMLSendCommand _send216;
    TMLRandomChoiceCommand _choice113;
    TMLExeciCommand _execi116;
    TMLActionCommand _action233;
    TMLWriteCommand _write229;
    TMLSendCommand _send234;
    TMLRequestCommand _request231;
    TMLWriteCommand _write232;
    TMLActionCommand _action235;
    TMLExeciCommand _execi117;
    TMLWriteCommand _write236;
    TMLSendCommand _send239;
    TMLActionCommand _action237;
    TMLRandomChoiceCommand _choice107;
    TMLRandomChoiceCommand _choice114;
    TMLActionCommand _action167;
    TMLExeciCommand _execi124;
    TMLWriteCommand _write169;
    TMLSendCommand _send199;
    TMLActionCommand _action170;
    TMLRandomChoiceCommand _choice125;
    TMLExeciCommand _execi132;
    TMLWriteCommand _write176;
    TMLSendCommand _send201;
    TMLActionCommand _action177;
    TMLExeciCommand _execi122;
    TMLWriteCommand _write173;
    TMLSendCommand _send200;
    TMLActionCommand _action174;
    TMLExeciCommand _execi119;
    TMLWriteCommand _write204;
    TMLActionCommand _action205;
    TMLSendCommand _send208;
    TMLActionCommand _action207;
    TMLRandomChoiceCommand _choice118;
    TMLRandomChoiceCommand _choice123;
    TMLRandomChoiceCommand _choice128;
    TMLExeciCommand _execi130;
    TMLActionCommand _action192;
    TMLWriteCommand _write164;
    TMLSendCommand _send196;
    TMLActionCommand _action158;
    TMLRequestCommand _request188;
    TMLWriteCommand _write189;
    TMLExeciCommand _execi126;
    TMLActionCommand _action193;
    TMLWriteCommand _write165;
    TMLSendCommand _send195;
    TMLActionCommand _action162;
    TMLRequestCommand _request186;
    TMLWriteCommand _write187;
    TMLActionCommand _action171;
    TMLRandomChoiceCommand _choice120;
    TMLRandomChoiceCommand _choice135;
    TMLReadCommand _read155;
    TMLExeciCommand _execi131;
    TMLActionCommand _action191;
    TMLWriteCommand _write156;
    TMLSendCommand _send194;
    TMLRequestCommand _request184;
    TMLWriteCommand _write185;
    TMLExeciCommand _execi127;
    TMLActionCommand _action190;
    TMLWriteCommand _write161;
    TMLSendCommand _send197;
    TMLActionCommand _action160;
    TMLRequestCommand _request181;
    TMLWriteCommand _write182;
    TMLRandomChoiceCommand _choice136;
    TMLActionCommand _action137;
    TMLRandomChoiceCommand _choice133;
    TMLSelectCommand _select150;
    TMLStopCommand _stop134;
    TMLRandomChoiceCommand _choice134;
    TMLActionCommand _action142;
    TMLRandomChoiceCommand _lpChoice140;
    TMLActionCommand _action262;
    
    void _lpIncAc140_func();
    TMLLength _execi121_func();
    TMLLength _execi129_func();
    TMLLength _execi115_func();
    void _action220_func();
    void _action222_func();
    void _action227_func();
    void _action228_func();
    void _action225_func();
    unsigned int _choice106_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice108_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice109_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice110_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi112_func();
    TMLLength _execi111_func();
    unsigned int _choice113_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi116_func();
    void _action235_func();
    void _action233_func();
    TMLLength _execi117_func();
    void _action237_func();
    unsigned int _choice107_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice114_func(ParamType& oMin, ParamType& oMax);
    void _action167_func();
    TMLLength _execi124_func();
    void _action170_func();
    unsigned int _choice125_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi132_func();
    void _action177_func();
    TMLLength _execi122_func();
    void _action174_func();
    TMLLength _execi119_func();
    void _action207_func();
    void _action205_func();
    unsigned int _choice118_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice123_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice128_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi130_func();
    void _action158_func();
    void _action192_func();
    TMLLength _execi126_func();
    void _action162_func();
    void _action193_func();
    void _action171_func();
    unsigned int _choice120_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice135_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi131_func();
    void _action191_func();
    TMLLength _execi127_func();
    void _action160_func();
    void _action190_func();
    unsigned int _choice136_func(ParamType& oMin, ParamType& oMax);
    void _action137_func();
    unsigned int _choice133_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice134_func(ParamType& oMin, ParamType& oMax);
    void _action142_func();
    unsigned int _lpChoice140_func(ParamType& oMin, ParamType& oMax);
    void _action262_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    AppC__TCPIP(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__AppC__fromAtoT
    , TMLChannel* channel__AppC__fromPtoT
    , TMLChannel* channel__AppC__fromTtoA
    , TMLChannel* channel__AppC__fromTtoP
    , TMLChannel* channel__AppC__temp
    , TMLEventChannel* event__AppC__abort__AppC__abort
    , TMLEventChannel* event__AppC__close__AppC__close
    , TMLEventChannel* event__AppC__open__AppC__open
    , TMLEventChannel* event__AppC__receive_Application__AppC__receive_Application
    , TMLEventChannel* event__AppC__receive__AppC__receive
    , TMLEventChannel* event__AppC__send_TCP__AppC__send_TCP
    , TMLEventChannel* event__AppC__send__AppC__send
    , TMLEventChannel* event__AppC__stop__AppC__stop
    , TMLEventChannel* event__AppC__timeOut__AppC__timeOut
    , TMLEventChannel* request__AppC__req_Timer
    , TMLEventChannel* requestChannel
    );
    std::istream& readObject(std::istream& i_stream_var);
    std::ostream& writeObject(std::ostream& i_stream_var);
    void reset();
    HashValueType getStateHash();
};
#endif
