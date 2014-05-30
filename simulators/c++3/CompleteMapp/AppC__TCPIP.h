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
    TMLActionCommand _lpIncAc123;
    TMLNotifiedCommand _notified126;
    TMLWaitCommand _wait136;
    TMLReadCommand _read127;
    TMLExeciCommand _execi104;
    TMLWriteCommand _write162;
    TMLSendCommand _send181;
    TMLReadCommand _read122;
    TMLExeciCommand _execi112;
    TMLSendCommand _send196;
    TMLReadCommand _read227;
    TMLExeciCommand _execi98;
    TMLWriteCommand _write197;
    TMLSendCommand _send200;
    TMLWriteCommand _write201;
    TMLActionCommand _action203;
    TMLActionCommand _action205;
    TMLActionCommand _action211;
    TMLActionCommand _action210;
    TMLActionCommand _action208;
    TMLRandomChoiceCommand _choice89;
    TMLRandomChoiceCommand _choice91;
    TMLRandomChoiceCommand _choice92;
    TMLRandomChoiceCommand _choice93;
    TMLExeciCommand _execi95;
    TMLWriteCommand _write194;
    TMLSendCommand _send226;
    TMLExeciCommand _execi94;
    TMLWriteCommand _write195;
    TMLSendCommand _send199;
    TMLRandomChoiceCommand _choice96;
    TMLExeciCommand _execi99;
    TMLActionCommand _action216;
    TMLWriteCommand _write212;
    TMLSendCommand _send217;
    TMLRequestCommand _request214;
    TMLWriteCommand _write215;
    TMLActionCommand _action218;
    TMLExeciCommand _execi100;
    TMLWriteCommand _write219;
    TMLSendCommand _send222;
    TMLActionCommand _action220;
    TMLRandomChoiceCommand _choice90;
    TMLRandomChoiceCommand _choice97;
    TMLActionCommand _action150;
    TMLExeciCommand _execi107;
    TMLWriteCommand _write152;
    TMLSendCommand _send182;
    TMLActionCommand _action153;
    TMLRandomChoiceCommand _choice108;
    TMLExeciCommand _execi115;
    TMLWriteCommand _write159;
    TMLSendCommand _send184;
    TMLActionCommand _action160;
    TMLExeciCommand _execi105;
    TMLWriteCommand _write156;
    TMLSendCommand _send183;
    TMLActionCommand _action157;
    TMLExeciCommand _execi102;
    TMLWriteCommand _write187;
    TMLActionCommand _action188;
    TMLSendCommand _send191;
    TMLActionCommand _action190;
    TMLRandomChoiceCommand _choice101;
    TMLRandomChoiceCommand _choice106;
    TMLRandomChoiceCommand _choice111;
    TMLExeciCommand _execi113;
    TMLActionCommand _action175;
    TMLWriteCommand _write147;
    TMLSendCommand _send179;
    TMLActionCommand _action141;
    TMLRequestCommand _request171;
    TMLWriteCommand _write172;
    TMLExeciCommand _execi109;
    TMLActionCommand _action176;
    TMLWriteCommand _write148;
    TMLSendCommand _send178;
    TMLActionCommand _action145;
    TMLRequestCommand _request169;
    TMLWriteCommand _write170;
    TMLActionCommand _action154;
    TMLRandomChoiceCommand _choice103;
    TMLRandomChoiceCommand _choice118;
    TMLReadCommand _read138;
    TMLExeciCommand _execi114;
    TMLActionCommand _action174;
    TMLWriteCommand _write139;
    TMLSendCommand _send177;
    TMLRequestCommand _request167;
    TMLWriteCommand _write168;
    TMLExeciCommand _execi110;
    TMLActionCommand _action173;
    TMLWriteCommand _write144;
    TMLSendCommand _send180;
    TMLActionCommand _action143;
    TMLRequestCommand _request164;
    TMLWriteCommand _write165;
    TMLRandomChoiceCommand _choice119;
    TMLActionCommand _action120;
    TMLRandomChoiceCommand _choice116;
    TMLSelectCommand _select133;
    TMLStopCommand _stop117;
    TMLRandomChoiceCommand _choice117;
    TMLActionCommand _action125;
    TMLRandomChoiceCommand _lpChoice123;
    TMLActionCommand _action262;
    
    void _lpIncAc123_func();
    TMLLength _execi104_func();
    TMLLength _execi112_func();
    TMLLength _execi98_func();
    void _action203_func();
    void _action205_func();
    void _action210_func();
    void _action211_func();
    void _action208_func();
    unsigned int _choice89_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice91_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice92_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice93_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi95_func();
    TMLLength _execi94_func();
    unsigned int _choice96_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi99_func();
    void _action218_func();
    void _action216_func();
    TMLLength _execi100_func();
    void _action220_func();
    unsigned int _choice90_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice97_func(ParamType& oMin, ParamType& oMax);
    void _action150_func();
    TMLLength _execi107_func();
    void _action153_func();
    unsigned int _choice108_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi115_func();
    void _action160_func();
    TMLLength _execi105_func();
    void _action157_func();
    TMLLength _execi102_func();
    void _action190_func();
    void _action188_func();
    unsigned int _choice101_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice106_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice111_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi113_func();
    void _action141_func();
    void _action175_func();
    TMLLength _execi109_func();
    void _action145_func();
    void _action176_func();
    void _action154_func();
    unsigned int _choice103_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice118_func(ParamType& oMin, ParamType& oMax);
    TMLLength _execi114_func();
    void _action174_func();
    TMLLength _execi110_func();
    void _action143_func();
    void _action173_func();
    unsigned int _choice119_func(ParamType& oMin, ParamType& oMax);
    void _action120_func();
    unsigned int _choice116_func(ParamType& oMin, ParamType& oMax);
    unsigned int _choice117_func(ParamType& oMin, ParamType& oMax);
    void _action125_func();
    unsigned int _lpChoice123_func(ParamType& oMin, ParamType& oMax);
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
