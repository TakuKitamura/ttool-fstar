#include <AppC__TCPIP.h>

AppC__TCPIP::AppC__TCPIP(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
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
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,wind(64)
,i(0)
,j(0)
,a(0)
,b(0)
,tcpctrl__a(0)
,tcpctrl__state(0)
,seqi(0)
,_waitOnRequest(105,this,requestChannel,0,"\xe0\xe3\x7f\x0",false)
,_lpIncAc140(140,this,(ActionFuncPointer)&AppC__TCPIP::_lpIncAc140_func, 0, false)
,_notified143(143,this,event__AppC__abort__AppC__abort,&tcpctrl__a,"tcpctrl__a","\xe2\xe3\x7f\x0",false)
,_wait153(153,this,event__AppC__abort__AppC__abort,0,"\xe2\xe3\x7f\x0",true)
,_read144(144,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi121(121,this,(LengthFuncPointer)&AppC__TCPIP::_execi121_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write179(179,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send198(198,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_read139(139,this,0,channel__AppC__fromPtoT,"\xe2\xe3\x7f\x0",true,1)
,_execi129(129,this,(LengthFuncPointer)&AppC__TCPIP::_execi129_func,0,1,"\xe2\xe3\x7f\x0",false)
,_send213(213,this,event__AppC__stop__AppC__stop,0,"\xe2\xe3\x7f\x0",true)
,_read244(244,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi115(115,this,(LengthFuncPointer)&AppC__TCPIP::_execi115_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write214(214,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send217(217,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_write218(218,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_action220(220,this,(ActionFuncPointer)&AppC__TCPIP::_action220_func, "\xe2\xe3\x7f\x0",false)
,_action222(222,this,(ActionFuncPointer)&AppC__TCPIP::_action222_func, "\xe2\xe3\x7f\x0",false)
,_action228(228,this,(ActionFuncPointer)&AppC__TCPIP::_action228_func, "\xa2\xe3\x7f\x0",false)
,_action227(227,this,(ActionFuncPointer)&AppC__TCPIP::_action227_func, "\xe2\xe3\x7f\x0",false)
,_action225(225,this,(ActionFuncPointer)&AppC__TCPIP::_action225_func, "\xe2\xe3\x7f\x0",false)
,_choice106(106,this,(RangeFuncPointer)&AppC__TCPIP::_choice106_func,2,"\xe2\xe3\x7f\x0",false)
,_choice108(108,this,(RangeFuncPointer)&AppC__TCPIP::_choice108_func,3,"\xe2\xe3\x7f\x0",false)
,_choice109(109,this,(RangeFuncPointer)&AppC__TCPIP::_choice109_func,3,"\xe2\xe3\x7f\x0",false)
,_choice110(110,this,(RangeFuncPointer)&AppC__TCPIP::_choice110_func,2,"\xe2\xe3\x7f\x0",false)
,_execi112(112,this,(LengthFuncPointer)&AppC__TCPIP::_execi112_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write211(211,this,0,channel__AppC__fromTtoA,"\xe2\xe3\x7f\x0",true,1)
,_send243(243,this,event__AppC__receive_Application__AppC__receive_Application,0,"\xe2\xe3\x7f\x0",true)
,_execi111(111,this,(LengthFuncPointer)&AppC__TCPIP::_execi111_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write212(212,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send216(216,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_choice113(113,this,(RangeFuncPointer)&AppC__TCPIP::_choice113_func,2,"\xe2\xe3\x7f\x0",false)
,_execi116(116,this,(LengthFuncPointer)&AppC__TCPIP::_execi116_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action233(233,this,(ActionFuncPointer)&AppC__TCPIP::_action233_func, "\xa2\xe3\x7f\x0",false)
,_write229(229,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send234(234,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_request231(231,this,request__AppC__req_Timer,0,"\xa2\xe3\x7f\x0",true)
,_write232(232,this,0,channel__AppC__temp,"\xa2\xe3\x7f\x0",false,1)
,_action235(235,this,(ActionFuncPointer)&AppC__TCPIP::_action235_func, "\xe2\xe3\x7f\x0",false)
,_execi117(117,this,(LengthFuncPointer)&AppC__TCPIP::_execi117_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write236(236,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send239(239,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action237(237,this,(ActionFuncPointer)&AppC__TCPIP::_action237_func, "\xe2\xe3\x7f\x0",false)
,_choice107(107,this,(RangeFuncPointer)&AppC__TCPIP::_choice107_func,3,"\xe2\xe3\x7f\x0",false)
,_choice114(114,this,(RangeFuncPointer)&AppC__TCPIP::_choice114_func,3,"\xe2\xe3\x7f\x0",false)
,_action167(167,this,(ActionFuncPointer)&AppC__TCPIP::_action167_func, "\xe2\xe3\x7f\x0",false)
,_execi124(124,this,(LengthFuncPointer)&AppC__TCPIP::_execi124_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write169(169,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send199(199,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action170(170,this,(ActionFuncPointer)&AppC__TCPIP::_action170_func, "\xe2\xe3\x7f\x0",false)
,_choice125(125,this,(RangeFuncPointer)&AppC__TCPIP::_choice125_func,3,"\xe2\xe3\x7f\x0",false)
,_execi132(132,this,(LengthFuncPointer)&AppC__TCPIP::_execi132_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write176(176,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send201(201,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action177(177,this,(ActionFuncPointer)&AppC__TCPIP::_action177_func, "\xe2\xe3\x7f\x0",false)
,_execi122(122,this,(LengthFuncPointer)&AppC__TCPIP::_execi122_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write173(173,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send200(200,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action174(174,this,(ActionFuncPointer)&AppC__TCPIP::_action174_func, "\xe2\xe3\x7f\x0",false)
,_execi119(119,this,(LengthFuncPointer)&AppC__TCPIP::_execi119_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write204(204,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_action205(205,this,(ActionFuncPointer)&AppC__TCPIP::_action205_func, "\xa2\xe3\x7f\x0",false)
,_send208(208,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action207(207,this,(ActionFuncPointer)&AppC__TCPIP::_action207_func, "\xe2\xe3\x7f\x0",false)
,_choice118(118,this,(RangeFuncPointer)&AppC__TCPIP::_choice118_func,2,"\xe2\xe3\x7f\x0",false)
,_choice123(123,this,(RangeFuncPointer)&AppC__TCPIP::_choice123_func,3,"\xe2\xe3\x7f\x0",false)
,_choice128(128,this,(RangeFuncPointer)&AppC__TCPIP::_choice128_func,3,"\xe2\xe3\x7f\x0",false)
,_execi130(130,this,(LengthFuncPointer)&AppC__TCPIP::_execi130_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action192(192,this,(ActionFuncPointer)&AppC__TCPIP::_action192_func, "\xa2\xe3\x7f\x0",false)
,_write164(164,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send196(196,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action158(158,this,(ActionFuncPointer)&AppC__TCPIP::_action158_func, "\xe2\xe3\x7f\x0",false)
,_request188(188,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write189(189,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi126(126,this,(LengthFuncPointer)&AppC__TCPIP::_execi126_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action193(193,this,(ActionFuncPointer)&AppC__TCPIP::_action193_func, "\xa2\xe3\x7f\x0",false)
,_write165(165,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send195(195,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action162(162,this,(ActionFuncPointer)&AppC__TCPIP::_action162_func, "\xe2\xe3\x7f\x0",false)
,_request186(186,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write187(187,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_action171(171,this,(ActionFuncPointer)&AppC__TCPIP::_action171_func, "\xe2\xe3\x7f\x0",false)
,_choice120(120,this,(RangeFuncPointer)&AppC__TCPIP::_choice120_func,2,"\xe2\xe3\x7f\x0",false)
,_choice135(135,this,(RangeFuncPointer)&AppC__TCPIP::_choice135_func,3,"\xe2\xe3\x7f\x0",false)
,_read155(155,this,0,channel__AppC__fromAtoT,"\xe2\xe3\x7f\x0",true,1)
,_execi131(131,this,(LengthFuncPointer)&AppC__TCPIP::_execi131_func,0,1,"\xe2\xe3\x7f\x0",false)
,_action191(191,this,(ActionFuncPointer)&AppC__TCPIP::_action191_func, "\xe2\xe3\x7f\x0",false)
,_write156(156,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send194(194,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_request184(184,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write185(185,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi127(127,this,(LengthFuncPointer)&AppC__TCPIP::_execi127_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action190(190,this,(ActionFuncPointer)&AppC__TCPIP::_action190_func, "\xa2\xe3\x7f\x0",false)
,_write161(161,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send197(197,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action160(160,this,(ActionFuncPointer)&AppC__TCPIP::_action160_func, "\xe2\xe3\x7f\x0",false)
,_request181(181,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write182(182,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_choice136(136,this,(RangeFuncPointer)&AppC__TCPIP::_choice136_func,3,"\xe2\xe3\x7f\x0",false)
,_action137(137,this,(ActionFuncPointer)&AppC__TCPIP::_action137_func, "\xe2\xe3\x7f\x0",false)
,_choice133(133,this,(RangeFuncPointer)&AppC__TCPIP::_choice133_func,2,"\xe2\xe3\x7f\x0",false)
,_select150(150,this,array(5,(TMLEventChannel*)event__AppC__timeOut__AppC__timeOut,(TMLEventChannel*)event__AppC__receive__AppC__receive,(TMLEventChannel*)event__AppC__close__AppC__close,(TMLEventChannel*)event__AppC__send_TCP__AppC__send_TCP,(TMLEventChannel*)event__AppC__open__AppC__open),5,"\xe2\xe3\x7f\x0",false,array(5,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0))
, _stop134(134,this)
,_choice134(134,this,(RangeFuncPointer)&AppC__TCPIP::_choice134_func,3,"\xe2\xe3\x7f\x0",false)
,_action142(142,this,(ActionFuncPointer)&AppC__TCPIP::_action142_func, "\xe0\xe3\x7f\x0",false)
,_lpChoice140(140,this,(RangeFuncPointer)&AppC__TCPIP::_lpChoice140_func,2,0, false)
,_action262(262,this,(ActionFuncPointer)&AppC__TCPIP::_action262_func, 0, false)

{
    _comment = new std::string[26];
    _comment[0]=std::string("Action i = i");
    _comment[1]=std::string("Action tcpctrl__state =4");
    _comment[2]=std::string("Action tcpctrl__state =6");
    _comment[3]=std::string("Action tcpctrl__state =0");
    _comment[4]=std::string("Action tcpctrl__state =8");
    _comment[5]=std::string("Action tcpctrl__state =0");
    _comment[6]=std::string("Action tcpctrl__state =2");
    _comment[7]=std::string("Action seqi = seqi + wind");
    _comment[8]=std::string("Action tcpctrl__state =2");
    _comment[9]=std::string("Action tcpctrl__state =1");
    _comment[10]=std::string("Action tcpctrl__state =3");
    _comment[11]=std::string("Action tcpctrl__state =7");
    _comment[12]=std::string("Action tcpctrl__state =9");
    _comment[13]=std::string("Action tcpctrl__state =0");
    _comment[14]=std::string("Action tcpctrl__state =8");
    _comment[15]=std::string("Action tcpctrl__state =5");
    _comment[16]=std::string("Action seqi = seqi + wind");
    _comment[17]=std::string("Action tcpctrl__state =10");
    _comment[18]=std::string("Action seqi = seqi + wind");
    _comment[19]=std::string("Action tcpctrl__state =0");
    _comment[20]=std::string("Action seqi = seqi + wind");
    _comment[21]=std::string("Action tcpctrl__state=3");
    _comment[22]=std::string("Action seqi = seqi + wind");
    _comment[23]=std::string("Action tcpctrl__state =1");
    _comment[24]=std::string("Action tcpctrl__state=0");
    _comment[25]=std::string("Action i=0");
    
    //generate task variable look-up table
    _varLookUpName["wind"]=&wind;
    _varLookUpID[21]=&wind;
    _varLookUpName["i"]=&i;
    _varLookUpID[22]=&i;
    _varLookUpName["j"]=&j;
    _varLookUpID[23]=&j;
    _varLookUpName["a"]=&a;
    _varLookUpID[24]=&a;
    _varLookUpName["b"]=&b;
    _varLookUpID[25]=&b;
    _varLookUpName["tcpctrl__a"]=&tcpctrl__a;
    _varLookUpID[26]=&tcpctrl__a;
    _varLookUpName["tcpctrl__state"]=&tcpctrl__state;
    _varLookUpID[27]=&tcpctrl__state;
    _varLookUpName["seqi"]=&seqi;
    _varLookUpID[28]=&seqi;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__AppC__fromAtoT->setBlockedReadTask(this);
    channel__AppC__fromPtoT->setBlockedReadTask(this);
    channel__AppC__fromTtoA->setBlockedWriteTask(this);
    channel__AppC__fromTtoP->setBlockedWriteTask(this);
    channel__AppC__temp->setBlockedWriteTask(this);
    event__AppC__abort__AppC__abort->setBlockedReadTask(this);
    event__AppC__close__AppC__close->setBlockedReadTask(this);
    event__AppC__open__AppC__open->setBlockedReadTask(this);
    event__AppC__receive_Application__AppC__receive_Application->setBlockedWriteTask(this);
    event__AppC__receive__AppC__receive->setBlockedReadTask(this);
    event__AppC__send_TCP__AppC__send_TCP->setBlockedReadTask(this);
    event__AppC__send__AppC__send->setBlockedWriteTask(this);
    event__AppC__stop__AppC__stop->setBlockedWriteTask(this);
    event__AppC__timeOut__AppC__timeOut->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    request__AppC__req_Timer->setBlockedWriteTask(this);
    
    //command chaining
    _lpIncAc140.setNextCommand(array(1,(TMLCommand*)&_lpChoice140));
    _wait153.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _send198.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _write179.setNextCommand(array(1,(TMLCommand*)&_send198));
    _execi121.setNextCommand(array(1,(TMLCommand*)&_write179));
    _read144.setNextCommand(array(1,(TMLCommand*)&_execi121));
    _write218.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _send217.setNextCommand(array(1,(TMLCommand*)&_write218));
    _write214.setNextCommand(array(1,(TMLCommand*)&_send217));
    _execi115.setNextCommand(array(1,(TMLCommand*)&_write214));
    _action220.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _action222.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _action227.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _action228.setNextCommand(array(1,(TMLCommand*)&_action227));
    _action225.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _choice106.setNextCommand(array(2,(TMLCommand*)&_action225,(TMLCommand*)&_lpIncAc140));
    _choice108.setNextCommand(array(3,(TMLCommand*)&_action222,(TMLCommand*)&_action228,(TMLCommand*)&_choice106));
    _choice109.setNextCommand(array(3,(TMLCommand*)&_action220,(TMLCommand*)&_lpIncAc140,(TMLCommand*)&_choice108));
    _choice110.setNextCommand(array(2,(TMLCommand*)&_execi115,(TMLCommand*)&_choice109));
    _read244.setNextCommand(array(1,(TMLCommand*)&_choice110));
    _send213.setNextCommand(array(1,(TMLCommand*)&_read244));
    _send216.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _write212.setNextCommand(array(1,(TMLCommand*)&_send216));
    _execi111.setNextCommand(array(1,(TMLCommand*)&_write212));
    _send243.setNextCommand(array(1,(TMLCommand*)&_execi111));
    _write211.setNextCommand(array(1,(TMLCommand*)&_send243));
    _execi112.setNextCommand(array(1,(TMLCommand*)&_write211));
    _choice113.setNextCommand(array(2,(TMLCommand*)&_execi112,(TMLCommand*)&_lpIncAc140));
    _action235.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _write232.setNextCommand(array(1,(TMLCommand*)&_action235));
    _request231.setNextCommand(array(1,(TMLCommand*)&_write232));
    _send234.setNextCommand(array(1,(TMLCommand*)&_request231));
    _write229.setNextCommand(array(1,(TMLCommand*)&_send234));
    _action233.setNextCommand(array(1,(TMLCommand*)&_write229));
    _execi116.setNextCommand(array(1,(TMLCommand*)&_action233));
    _action237.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _send239.setNextCommand(array(1,(TMLCommand*)&_action237));
    _write236.setNextCommand(array(1,(TMLCommand*)&_send239));
    _execi117.setNextCommand(array(1,(TMLCommand*)&_write236));
    _choice107.setNextCommand(array(3,(TMLCommand*)&_execi116,(TMLCommand*)&_execi117,(TMLCommand*)&_lpIncAc140));
    _choice114.setNextCommand(array(3,(TMLCommand*)&_send213,(TMLCommand*)&_choice113,(TMLCommand*)&_choice107));
    _action167.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _action170.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _send199.setNextCommand(array(1,(TMLCommand*)&_action170));
    _write169.setNextCommand(array(1,(TMLCommand*)&_send199));
    _execi124.setNextCommand(array(1,(TMLCommand*)&_write169));
    _choice125.setNextCommand(array(3,(TMLCommand*)&_action167,(TMLCommand*)&_execi124,(TMLCommand*)&_lpIncAc140));
    _action177.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _send201.setNextCommand(array(1,(TMLCommand*)&_action177));
    _write176.setNextCommand(array(1,(TMLCommand*)&_send201));
    _execi132.setNextCommand(array(1,(TMLCommand*)&_write176));
    _action174.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _send200.setNextCommand(array(1,(TMLCommand*)&_action174));
    _write173.setNextCommand(array(1,(TMLCommand*)&_send200));
    _execi122.setNextCommand(array(1,(TMLCommand*)&_write173));
    _action207.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _send208.setNextCommand(array(1,(TMLCommand*)&_action207));
    _action205.setNextCommand(array(1,(TMLCommand*)&_send208));
    _write204.setNextCommand(array(1,(TMLCommand*)&_action205));
    _execi119.setNextCommand(array(1,(TMLCommand*)&_write204));
    _choice118.setNextCommand(array(2,(TMLCommand*)&_execi119,(TMLCommand*)&_lpIncAc140));
    _choice123.setNextCommand(array(3,(TMLCommand*)&_execi132,(TMLCommand*)&_execi122,(TMLCommand*)&_choice118));
    _choice128.setNextCommand(array(3,(TMLCommand*)&_choice114,(TMLCommand*)&_choice125,(TMLCommand*)&_choice123));
    _execi129.setNextCommand(array(1,(TMLCommand*)&_choice128));
    _read139.setNextCommand(array(1,(TMLCommand*)&_execi129));
    _write189.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _request188.setNextCommand(array(1,(TMLCommand*)&_write189));
    _action158.setNextCommand(array(1,(TMLCommand*)&_request188));
    _send196.setNextCommand(array(1,(TMLCommand*)&_action158));
    _write164.setNextCommand(array(1,(TMLCommand*)&_send196));
    _action192.setNextCommand(array(1,(TMLCommand*)&_write164));
    _execi130.setNextCommand(array(1,(TMLCommand*)&_action192));
    _write187.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _request186.setNextCommand(array(1,(TMLCommand*)&_write187));
    _action162.setNextCommand(array(1,(TMLCommand*)&_request186));
    _send195.setNextCommand(array(1,(TMLCommand*)&_action162));
    _write165.setNextCommand(array(1,(TMLCommand*)&_send195));
    _action193.setNextCommand(array(1,(TMLCommand*)&_write165));
    _execi126.setNextCommand(array(1,(TMLCommand*)&_action193));
    _action171.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _choice120.setNextCommand(array(2,(TMLCommand*)&_action171,(TMLCommand*)&_lpIncAc140));
    _choice135.setNextCommand(array(3,(TMLCommand*)&_execi130,(TMLCommand*)&_execi126,(TMLCommand*)&_choice120));
    _write185.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _request184.setNextCommand(array(1,(TMLCommand*)&_write185));
    _send194.setNextCommand(array(1,(TMLCommand*)&_request184));
    _write156.setNextCommand(array(1,(TMLCommand*)&_send194));
    _action191.setNextCommand(array(1,(TMLCommand*)&_write156));
    _execi131.setNextCommand(array(1,(TMLCommand*)&_action191));
    _read155.setNextCommand(array(1,(TMLCommand*)&_execi131));
    _write182.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _request181.setNextCommand(array(1,(TMLCommand*)&_write182));
    _action160.setNextCommand(array(1,(TMLCommand*)&_request181));
    _send197.setNextCommand(array(1,(TMLCommand*)&_action160));
    _write161.setNextCommand(array(1,(TMLCommand*)&_send197));
    _action190.setNextCommand(array(1,(TMLCommand*)&_write161));
    _execi127.setNextCommand(array(1,(TMLCommand*)&_action190));
    _choice136.setNextCommand(array(3,(TMLCommand*)&_read155,(TMLCommand*)&_execi127,(TMLCommand*)&_lpIncAc140));
    _action137.setNextCommand(array(1,(TMLCommand*)&_lpIncAc140));
    _choice133.setNextCommand(array(2,(TMLCommand*)&_action137,(TMLCommand*)&_lpIncAc140));
    _select150.setNextCommand(array(5,(TMLCommand*)&_read144,(TMLCommand*)&_read139,(TMLCommand*)&_choice135,(TMLCommand*)&_choice136,(TMLCommand*)&_choice133));
    _choice134.setNextCommand(array(3,(TMLCommand*)&_wait153,(TMLCommand*)&_select150,(TMLCommand*)&_stop134));
    _notified143.setNextCommand(array(1,(TMLCommand*)&_choice134));
    _action142.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _lpChoice140.setNextCommand(array(2,(TMLCommand*)&_notified143,(TMLCommand*)&_action142));
    _action262.setNextCommand(array(1,(TMLCommand*)&_lpChoice140));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action262));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__AppC__fromAtoT;
    _channels[1] = channel__AppC__fromPtoT;
    _channels[2] = channel__AppC__fromTtoA;
    _channels[3] = channel__AppC__fromTtoP;
    _channels[4] = channel__AppC__temp;
    _channels[5] = event__AppC__abort__AppC__abort;
    _channels[6] = event__AppC__close__AppC__close;
    _channels[7] = event__AppC__open__AppC__open;
    _channels[8] = event__AppC__receive_Application__AppC__receive_Application;
    _channels[9] = event__AppC__receive__AppC__receive;
    _channels[10] = event__AppC__send_TCP__AppC__send_TCP;
    _channels[11] = event__AppC__send__AppC__send;
    _channels[12] = event__AppC__stop__AppC__stop;
    _channels[13] = event__AppC__timeOut__AppC__timeOut;
    _channels[14] = requestChannel;
    refreshStateHash("\xe0\xe3\x7f\x0");
}

void AppC__TCPIP::_lpIncAc140_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    i = i;
}

TMLLength AppC__TCPIP::_execi121_func(){
    return (TMLLength)(b);
}

TMLLength AppC__TCPIP::_execi129_func(){
    return (TMLLength)(b);
}

TMLLength AppC__TCPIP::_execi115_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action220_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    tcpctrl__state =4;
}

void AppC__TCPIP::_action222_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    tcpctrl__state =6;
}

void AppC__TCPIP::_action227_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,3));
    #endif
    tcpctrl__state =0;
}

void AppC__TCPIP::_action228_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,4));
    #endif
    tcpctrl__state =8;
}

void AppC__TCPIP::_action225_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,5));
    #endif
    tcpctrl__state =0;
}

unsigned int AppC__TCPIP::_choice106_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state ==10 ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

unsigned int AppC__TCPIP::_choice108_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state ==5 ){
        oC++;
        oMax += 1;
        
    }
    if ( tcpctrl__state == 7 ){
        oC++;
        oMax += 2;
        
    }
    if (oMax==0){
         oMax=4;
        return 2;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

unsigned int AppC__TCPIP::_choice109_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state ==2 ){
        oC++;
        oMax += 1;
        
    }
    if ( tcpctrl__state ==4 ){
        oC++;
        oMax += 2;
        
    }
    if (oMax==0){
         oMax=4;
        return 2;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

unsigned int AppC__TCPIP::_choice110_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=1;
    return myrand(0, 1);
    
}

TMLLength AppC__TCPIP::_execi112_func(){
    return (TMLLength)(b);
}

TMLLength AppC__TCPIP::_execi111_func(){
    return (TMLLength)(b);
}

unsigned int AppC__TCPIP::_choice113_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state  == 4 ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

TMLLength AppC__TCPIP::_execi116_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action235_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,6));
    #endif
    tcpctrl__state =2;
}

void AppC__TCPIP::_action233_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,7));
    #endif
    seqi = seqi + wind;
}

TMLLength AppC__TCPIP::_execi117_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action237_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,8));
    #endif
    tcpctrl__state =2;
}

unsigned int AppC__TCPIP::_choice107_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state ==1 ){
        oC++;
        oMax += 1;
        
    }
    if ( tcpctrl__state ==3 ){
        oC++;
        oMax += 2;
        
    }
    if (oMax==0){
         oMax=4;
        return 2;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

unsigned int AppC__TCPIP::_choice114_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=2;
    return myrand(0, 2);
    
}

void AppC__TCPIP::_action167_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,9));
    #endif
    tcpctrl__state =1;
}

TMLLength AppC__TCPIP::_execi124_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action170_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,10));
    #endif
    tcpctrl__state =3;
}

unsigned int AppC__TCPIP::_choice125_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state ==0 ){
        oC++;
        oMax += 1;
        
    }
    if ( tcpctrl__state==0 ){
        oC++;
        oMax += 2;
        
    }
    if (oMax==0){
         oMax=4;
        return 2;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

TMLLength AppC__TCPIP::_execi132_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action177_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,11));
    #endif
    tcpctrl__state =7;
}

TMLLength AppC__TCPIP::_execi122_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action174_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,12));
    #endif
    tcpctrl__state =9;
}

TMLLength AppC__TCPIP::_execi119_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action207_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,13));
    #endif
    tcpctrl__state =0;
}

void AppC__TCPIP::_action205_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,14));
    #endif
    tcpctrl__state =8;
}

unsigned int AppC__TCPIP::_choice118_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state ==6 ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

unsigned int AppC__TCPIP::_choice123_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state == 5){
        oC++;
        oMax += 1;
        
    }
    if ( tcpctrl__state ==4 ){
        oC++;
        oMax += 2;
        
    }
    if (oMax==0){
         oMax=4;
        return 2;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

unsigned int AppC__TCPIP::_choice128_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=2;
    return myrand(0, 2);
    
}

TMLLength AppC__TCPIP::_execi130_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action158_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,15));
    #endif
    tcpctrl__state =5;
}

void AppC__TCPIP::_action192_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,16));
    #endif
    seqi = seqi + wind;
}

TMLLength AppC__TCPIP::_execi126_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action162_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,17));
    #endif
    tcpctrl__state =10;
}

void AppC__TCPIP::_action193_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,18));
    #endif
    seqi = seqi + wind;
}

void AppC__TCPIP::_action171_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,19));
    #endif
    tcpctrl__state =0;
}

unsigned int AppC__TCPIP::_choice120_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state == 1 ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

unsigned int AppC__TCPIP::_choice135_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( (tcpctrl__state ==2)||(tcpctrl__state ==4) ){
        oC++;
        oMax += 1;
        
    }
    if ( tcpctrl__state ==9 ){
        oC++;
        oMax += 2;
        
    }
    if (oMax==0){
         oMax=4;
        return 2;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

TMLLength AppC__TCPIP::_execi131_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action191_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,20));
    #endif
    seqi = seqi + wind;
}

TMLLength AppC__TCPIP::_execi127_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action160_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,21));
    #endif
    tcpctrl__state=3;
}

void AppC__TCPIP::_action190_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,22));
    #endif
    seqi = seqi + wind;
}

unsigned int AppC__TCPIP::_choice136_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state ==4 ){
        oC++;
        oMax += 1;
        
    }
    if ( tcpctrl__state == 1){
        oC++;
        oMax += 2;
        
    }
    if (oMax==0){
         oMax=4;
        return 2;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

void AppC__TCPIP::_action137_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,23));
    #endif
    tcpctrl__state =1;
}

unsigned int AppC__TCPIP::_choice133_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__state==0 ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

unsigned int AppC__TCPIP::_choice134_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( tcpctrl__a>0 ){
        oC++;
        oMax += 1;
        
    }
    if ( tcpctrl__a==0 ){
        oC++;
        oMax += 2;
        
    }
    if (oMax==0){
         oMax=4;
        return 2;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

void AppC__TCPIP::_action142_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,24));
    #endif
    tcpctrl__state=0;
}

unsigned int AppC__TCPIP::_lpChoice140_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( (tcpctrl__a==0) ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

void AppC__TCPIP::_action262_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,25));
    #endif
    i=0;
}

std::istream& AppC__TCPIP::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,wind);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable wind " << wind << std::endl;
    #endif
    READ_STREAM(i_stream_var,i);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable i " << i << std::endl;
    #endif
    READ_STREAM(i_stream_var,j);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable j " << j << std::endl;
    #endif
    READ_STREAM(i_stream_var,a);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable a " << a << std::endl;
    #endif
    READ_STREAM(i_stream_var,b);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable b " << b << std::endl;
    #endif
    READ_STREAM(i_stream_var,tcpctrl__a);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable tcpctrl__a " << tcpctrl__a << std::endl;
    #endif
    READ_STREAM(i_stream_var,tcpctrl__state);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable tcpctrl__state " << tcpctrl__state << std::endl;
    #endif
    READ_STREAM(i_stream_var,seqi);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable seqi " << seqi << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& AppC__TCPIP::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,wind);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable wind " << wind << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,i);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable i " << i << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,j);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable j " << j << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,a);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable a " << a << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,b);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable b " << b << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,tcpctrl__a);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable tcpctrl__a " << tcpctrl__a << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,tcpctrl__state);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable tcpctrl__state " << tcpctrl__state << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,seqi);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable seqi " << seqi << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void AppC__TCPIP::reset(){
    TMLTask::reset();
    wind=64;
    i=0;
    j=0;
    a=0;
    b=0;
    tcpctrl__a=0;
    tcpctrl__state=0;
    seqi=0;
}

HashValueType AppC__TCPIP::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(wind);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(i);
            if ((_liveVarList[0] & 4)!=0) _stateHash.addValue(j);
            if ((_liveVarList[0] & 8)!=0) _stateHash.addValue(a);
            if ((_liveVarList[0] & 16)!=0) _stateHash.addValue(b);
            if ((_liveVarList[0] & 32)!=0) _stateHash.addValue(tcpctrl__a);
            if ((_liveVarList[0] & 64)!=0) _stateHash.addValue(tcpctrl__state);
            if ((_liveVarList[0] & 128)!=0) _stateHash.addValue(seqi);
            _channels[0]->setSignificance(this, ((_liveVarList[1] & 1)!=0));
            _channels[1]->setSignificance(this, ((_liveVarList[1] & 2)!=0));
             _channels[5]->setSignificance(this, ((_liveVarList[1] & 32)!=0));
             _channels[6]->setSignificance(this, ((_liveVarList[1] & 64)!=0));
             _channels[7]->setSignificance(this, ((_liveVarList[1] & 128)!=0));
             _channels[9]->setSignificance(this, ((_liveVarList[2] & 2)!=0));
             _channels[10]->setSignificance(this, ((_liveVarList[2] & 4)!=0));
             _channels[13]->setSignificance(this, ((_liveVarList[2] & 32)!=0));
             _channels[14]->setSignificance(this, ((_liveVarList[2] & 64)!=0));
        }
    }
    return _stateHash.getHash();
}

