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
#include <TMLExeciRangeCommand.h>
#include <TMLRequestCommand.h>
#include <TMLSendCommand.h>
#include <TMLWaitCommand.h>
#include <TMLWriteCommand.h>
#include <TMLStopCommand.h>
#include <TMLWriteMultCommand.h>
#include <TMLRandomCommand.h>

extern "C" bool condFunc(TMLTask* _ioTask_);
class AppC__TCPIP: public TMLTask {
    private:
    // Attributes
    ParamType wind;
    ParamType seqNum;
    ParamType i;
    ParamType j;
    ParamType a;
    ParamType b;
    ParamType tcpctrl__a;
    ParamType tcpctrl__state;
    ParamType rnd__0;
    TMLChannel* _channels[16];
    
    TMLWaitCommand _waitOnRequest;
    TMLActionCommand _lpIncAc144;
    TMLNotifiedCommand _notified147;
    TMLWaitCommand _wait157;
    TMLReadCommand _read148;
    TMLExeciCommand _execi126;
    TMLWriteCommand _write183;
    TMLSendCommand _send202;
    TMLReadCommand _read143;
    TMLExeciRangeCommand _execi109;
    TMLSendCommand _send217;
    TMLReadCommand _read248;
    TMLExeciCommand _execi120;
    TMLWriteCommand _write218;
    TMLSendCommand _send221;
    TMLWriteCommand _write222;
    TMLActionCommand _action224;
    TMLActionCommand _action226;
    TMLActionCommand _action232;
    TMLActionCommand _action231;
    TMLActionCommand _action229;
    TMLRandomChoiceCommand _choice111;
    TMLRandomChoiceCommand _choice113;
    TMLRandomChoiceCommand _choice114;
    TMLRandomChoiceCommand _choice115;
    TMLExeciCommand _execi117;
    TMLWriteCommand _write215;
    TMLSendCommand _send247;
    TMLExeciCommand _execi116;
    TMLWriteCommand _write216;
    TMLSendCommand _send220;
    TMLRandomChoiceCommand _choice118;
    TMLExeciCommand _execi121;
    TMLActionCommand _action237;
    TMLWriteCommand _write233;
    TMLSendCommand _send238;
    TMLRequestCommand _request235;
    TMLWriteCommand _write236;
    TMLActionCommand _action239;
    TMLExeciCommand _execi122;
    TMLWriteCommand _write240;
    TMLSendCommand _send243;
    TMLActionCommand _action241;
    TMLRandomChoiceCommand _choice112;
    TMLRandomChoiceCommand _choice119;
    TMLActionCommand _action171;
    TMLExeciCommand _execi129;
    TMLWriteCommand _write173;
    TMLSendCommand _send203;
    TMLActionCommand _action174;
    TMLRandomChoiceCommand _choice130;
    TMLExeciCommand _execi136;
    TMLWriteCommand _write180;
    TMLSendCommand _send205;
    TMLActionCommand _action181;
    TMLExeciCommand _execi127;
    TMLWriteCommand _write177;
    TMLSendCommand _send204;
    TMLActionCommand _action178;
    TMLExeciCommand _execi124;
    TMLWriteCommand _write208;
    TMLActionCommand _action209;
    TMLSendCommand _send212;
    TMLActionCommand _action211;
    TMLRandomChoiceCommand _choice123;
    TMLRandomChoiceCommand _choice128;
    TMLRandomChoiceCommand _choice133;
    TMLExeciCommand _execi134;
    TMLActionCommand _action196;
    TMLWriteCommand _write168;
    TMLSendCommand _send200;
    TMLActionCommand _action162;
    TMLRequestCommand _request192;
    TMLWriteCommand _write193;
    TMLExeciCommand _execi131;
    TMLActionCommand _action197;
    TMLWriteCommand _write169;
    TMLSendCommand _send199;
    TMLActionCommand _action166;
    TMLRequestCommand _request190;
    TMLWriteCommand _write191;
    TMLActionCommand _action175;
    TMLRandomChoiceCommand _choice125;
    TMLRandomChoiceCommand _choice139;
    TMLReadCommand _read159;
    TMLExeciCommand _execi135;
    TMLActionCommand _action195;
    TMLWriteCommand _write160;
    TMLSendCommand _send198;
    TMLRequestCommand _request188;
    TMLWriteCommand _write189;
    TMLExeciCommand _execi132;
    TMLActionCommand _action194;
    TMLWriteCommand _write165;
    TMLSendCommand _send201;
    TMLActionCommand _action164;
    TMLRequestCommand _request185;
    TMLWriteCommand _write186;
    TMLRandomChoiceCommand _choice140;
    TMLSendCommand _send110;
    TMLActionCommand _action141;
    TMLRandomChoiceCommand _choice137;
    TMLSelectCommand _select154;
    TMLStopCommand _stop138;
    TMLRandomChoiceCommand _choice138;
    TMLActionCommand _action146;
    TMLRandomChoiceCommand _lpChoice144;
    TMLActionCommand _action289;
    
    void _lpIncAc144_func();
    TMLLength _execi126_func();
    unsigned int _execi109_func(ParamType & oMin, ParamType& oMax);
    TMLLength _execi120_func();
    void _action224_func();
    void _action226_func();
    void _action231_func();
    void _action232_func();
    void _action229_func();
    unsigned int _choice111_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice113_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice114_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice115_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi117_func();
    TMLLength _execi116_func();
    unsigned int _choice118_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi121_func();
    void _action239_func();
    void _action237_func();
    TMLLength _execi122_func();
    void _action241_func();
    unsigned int _choice112_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice119_func(ParamType& oMin, ParamType& oMax);
    void _action171_func();
    TMLLength _execi129_func();
    void _action174_func();
    unsigned int _choice130_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi136_func();
    void _action181_func();
    TMLLength _execi127_func();
    void _action178_func();
    TMLLength _execi124_func();
    void _action211_func();
    void _action209_func();
    unsigned int _choice123_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice128_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice133_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi134_func();
    void _action162_func();
    void _action196_func();
    TMLLength _execi131_func();
    void _action166_func();
    void _action197_func();
    void _action175_func();
    unsigned int _choice125_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice139_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi135_func();
    void _action195_func();
    TMLLength _execi132_func();
    void _action164_func();
    void _action194_func();
    unsigned int _choice140_func(ParamType& oMin, ParamType& oMax);
    void _action141_func();
    unsigned int _choice137_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice138_func(ParamType& oMin, ParamType& oMax);
    void _action146_func();
    unsigned int _lpChoice144_func(ParamType& oMin, ParamType& oMax);
    void _action289_func();
    
    public:
    friend bool condFunc(TMLTask* _ioTask_);
    friend class CurrentComponents;
    AppC__TCPIP(ID iID, Priority iPriority, std::string iName, FPGA** iCPUs, unsigned int iNumOfCPUs
    , TMLChannel* channel__AppC__fromAtoT
    , TMLChannel* channel__AppC__fromPtoT
    , TMLChannel* channel__AppC__fromTtoA
    , TMLChannel* channel__AppC__fromTtoP
    , TMLChannel* channel__AppC__temp
    , TMLEventChannel* event__AppC__abort__AppC__abort
    , TMLEventChannel* event__AppC__close__AppC__close
    , TMLEventChannel* event__AppC__open__AppC__open
    , TMLEventChannel* event__AppC__opened__AppC__opened
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
