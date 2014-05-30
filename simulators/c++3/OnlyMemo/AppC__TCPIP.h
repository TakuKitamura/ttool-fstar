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
    TMLActionCommand _lpIncAc132;
    TMLNotifiedCommand _notified135;
    TMLWaitCommand _wait145;
    TMLReadCommand _read136;
    TMLExeciCommand _execi113;
    TMLWriteCommand _write171;
    TMLSendCommand _send190;
    TMLReadCommand _read131;
    TMLExeciCommand _execi121;
    TMLSendCommand _send205;
    TMLReadCommand _read236;
    TMLExeciCommand _execi107;
    TMLWriteCommand _write206;
    TMLSendCommand _send209;
    TMLWriteCommand _write210;
    TMLActionCommand _action212;
    TMLActionCommand _action214;
    TMLActionCommand _action220;
    TMLActionCommand _action219;
    TMLActionCommand _action217;
    TMLRandomChoiceCommand _choice98;
    TMLRandomChoiceCommand _choice100;
    TMLRandomChoiceCommand _choice101;
    TMLRandomChoiceCommand _choice102;
    TMLExeciCommand _execi104;
    TMLWriteCommand _write203;
    TMLSendCommand _send235;
    TMLExeciCommand _execi103;
    TMLWriteCommand _write204;
    TMLSendCommand _send208;
    TMLRandomChoiceCommand _choice105;
    TMLExeciCommand _execi108;
    TMLActionCommand _action225;
    TMLWriteCommand _write221;
    TMLSendCommand _send226;
    TMLRequestCommand _request223;
    TMLWriteCommand _write224;
    TMLActionCommand _action227;
    TMLExeciCommand _execi109;
    TMLWriteCommand _write228;
    TMLSendCommand _send231;
    TMLActionCommand _action229;
    TMLRandomChoiceCommand _choice99;
    TMLRandomChoiceCommand _choice106;
    TMLActionCommand _action159;
    TMLExeciCommand _execi116;
    TMLWriteCommand _write161;
    TMLSendCommand _send191;
    TMLActionCommand _action162;
    TMLRandomChoiceCommand _choice117;
    TMLExeciCommand _execi124;
    TMLWriteCommand _write168;
    TMLSendCommand _send193;
    TMLActionCommand _action169;
    TMLExeciCommand _execi114;
    TMLWriteCommand _write165;
    TMLSendCommand _send192;
    TMLActionCommand _action166;
    TMLExeciCommand _execi111;
    TMLWriteCommand _write196;
    TMLActionCommand _action197;
    TMLSendCommand _send200;
    TMLActionCommand _action199;
    TMLRandomChoiceCommand _choice110;
    TMLRandomChoiceCommand _choice115;
    TMLRandomChoiceCommand _choice120;
    TMLExeciCommand _execi122;
    TMLActionCommand _action184;
    TMLWriteCommand _write156;
    TMLSendCommand _send188;
    TMLActionCommand _action150;
    TMLRequestCommand _request180;
    TMLWriteCommand _write181;
    TMLExeciCommand _execi118;
    TMLActionCommand _action185;
    TMLWriteCommand _write157;
    TMLSendCommand _send187;
    TMLActionCommand _action154;
    TMLRequestCommand _request178;
    TMLWriteCommand _write179;
    TMLActionCommand _action163;
    TMLRandomChoiceCommand _choice112;
    TMLRandomChoiceCommand _choice127;
    TMLReadCommand _read147;
    TMLExeciCommand _execi123;
    TMLActionCommand _action183;
    TMLWriteCommand _write148;
    TMLSendCommand _send186;
    TMLRequestCommand _request176;
    TMLWriteCommand _write177;
    TMLExeciCommand _execi119;
    TMLActionCommand _action182;
    TMLWriteCommand _write153;
    TMLSendCommand _send189;
    TMLActionCommand _action152;
    TMLRequestCommand _request173;
    TMLWriteCommand _write174;
    TMLRandomChoiceCommand _choice128;
    TMLActionCommand _action129;
    TMLRandomChoiceCommand _choice125;
    TMLSelectCommand _select142;
    TMLStopCommand _stop126;
    TMLRandomChoiceCommand _choice126;
    TMLActionCommand _action134;
    TMLRandomChoiceCommand _lpChoice132;
    TMLActionCommand _action262;
    
    void _lpIncAc132_func();
    TMLLength _execi113_func();
    TMLLength _execi121_func();
    TMLLength _execi107_func();
    void _action212_func();
    void _action214_func();
    void _action219_func();
    void _action220_func();
    void _action217_func();
    unsigned int _choice98_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice100_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice101_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice102_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi104_func();
    TMLLength _execi103_func();
    unsigned int _choice105_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi108_func();
    void _action227_func();
    void _action225_func();
    TMLLength _execi109_func();
    void _action229_func();
    unsigned int _choice99_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice106_func(ParamType& oMin, ParamType& oMax);
    void _action159_func();
    TMLLength _execi116_func();
    void _action162_func();
    unsigned int _choice117_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi124_func();
    void _action169_func();
    TMLLength _execi114_func();
    void _action166_func();
    TMLLength _execi111_func();
    void _action199_func();
    void _action197_func();
    unsigned int _choice110_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice115_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice120_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi122_func();
    void _action150_func();
    void _action184_func();
    TMLLength _execi118_func();
    void _action154_func();
    void _action185_func();
    void _action163_func();
    unsigned int _choice112_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice127_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi123_func();
    void _action183_func();
    TMLLength _execi119_func();
    void _action152_func();
    void _action182_func();
    unsigned int _choice128_func(ParamType& oMin, ParamType& oMax);
    void _action129_func();
    unsigned int _choice125_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice126_func(ParamType& oMin, ParamType& oMax);
    void _action134_func();
    unsigned int _lpChoice132_func(ParamType& oMin, ParamType& oMax);
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
