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
,_waitOnRequest(88,this,requestChannel,0,"\xe0\xe3\x7f\x0",false)
,_lpIncAc123(123,this,(ActionFuncPointer)&AppC__TCPIP::_lpIncAc123_func, 0, false)
,_notified126(126,this,event__AppC__abort__AppC__abort,&tcpctrl__a,"tcpctrl__a","\xe2\xe3\x7f\x0",false)
,_wait136(136,this,event__AppC__abort__AppC__abort,0,"\xe2\xe3\x7f\x0",true)
,_read127(127,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi104(104,this,(LengthFuncPointer)&AppC__TCPIP::_execi104_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write162(162,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send181(181,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_read122(122,this,0,channel__AppC__fromPtoT,"\xe2\xe3\x7f\x0",true,1)
,_execi112(112,this,(LengthFuncPointer)&AppC__TCPIP::_execi112_func,0,1,"\xe2\xe3\x7f\x0",false)
,_send196(196,this,event__AppC__stop__AppC__stop,0,"\xe2\xe3\x7f\x0",true)
,_read227(227,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi98(98,this,(LengthFuncPointer)&AppC__TCPIP::_execi98_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write197(197,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send200(200,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_write201(201,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_action203(203,this,(ActionFuncPointer)&AppC__TCPIP::_action203_func, "\xe2\xe3\x7f\x0",false)
,_action205(205,this,(ActionFuncPointer)&AppC__TCPIP::_action205_func, "\xe2\xe3\x7f\x0",false)
,_action211(211,this,(ActionFuncPointer)&AppC__TCPIP::_action211_func, "\xa2\xe3\x7f\x0",false)
,_action210(210,this,(ActionFuncPointer)&AppC__TCPIP::_action210_func, "\xe2\xe3\x7f\x0",false)
,_action208(208,this,(ActionFuncPointer)&AppC__TCPIP::_action208_func, "\xe2\xe3\x7f\x0",false)
,_choice89(89,this,(RangeFuncPointer)&AppC__TCPIP::_choice89_func,2,"\xe2\xe3\x7f\x0",false)
,_choice91(91,this,(RangeFuncPointer)&AppC__TCPIP::_choice91_func,3,"\xe2\xe3\x7f\x0",false)
,_choice92(92,this,(RangeFuncPointer)&AppC__TCPIP::_choice92_func,3,"\xe2\xe3\x7f\x0",false)
,_choice93(93,this,(RangeFuncPointer)&AppC__TCPIP::_choice93_func,2,"\xe2\xe3\x7f\x0",false)
,_execi95(95,this,(LengthFuncPointer)&AppC__TCPIP::_execi95_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write194(194,this,0,channel__AppC__fromTtoA,"\xe2\xe3\x7f\x0",true,1)
,_send226(226,this,event__AppC__receive_Application__AppC__receive_Application,0,"\xe2\xe3\x7f\x0",true)
,_execi94(94,this,(LengthFuncPointer)&AppC__TCPIP::_execi94_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write195(195,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send199(199,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_choice96(96,this,(RangeFuncPointer)&AppC__TCPIP::_choice96_func,2,"\xe2\xe3\x7f\x0",false)
,_execi99(99,this,(LengthFuncPointer)&AppC__TCPIP::_execi99_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action216(216,this,(ActionFuncPointer)&AppC__TCPIP::_action216_func, "\xa2\xe3\x7f\x0",false)
,_write212(212,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send217(217,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_request214(214,this,request__AppC__req_Timer,0,"\xa2\xe3\x7f\x0",true)
,_write215(215,this,0,channel__AppC__temp,"\xa2\xe3\x7f\x0",false,1)
,_action218(218,this,(ActionFuncPointer)&AppC__TCPIP::_action218_func, "\xe2\xe3\x7f\x0",false)
,_execi100(100,this,(LengthFuncPointer)&AppC__TCPIP::_execi100_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write219(219,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send222(222,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action220(220,this,(ActionFuncPointer)&AppC__TCPIP::_action220_func, "\xe2\xe3\x7f\x0",false)
,_choice90(90,this,(RangeFuncPointer)&AppC__TCPIP::_choice90_func,3,"\xe2\xe3\x7f\x0",false)
,_choice97(97,this,(RangeFuncPointer)&AppC__TCPIP::_choice97_func,3,"\xe2\xe3\x7f\x0",false)
,_action150(150,this,(ActionFuncPointer)&AppC__TCPIP::_action150_func, "\xe2\xe3\x7f\x0",false)
,_execi107(107,this,(LengthFuncPointer)&AppC__TCPIP::_execi107_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write152(152,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send182(182,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action153(153,this,(ActionFuncPointer)&AppC__TCPIP::_action153_func, "\xe2\xe3\x7f\x0",false)
,_choice108(108,this,(RangeFuncPointer)&AppC__TCPIP::_choice108_func,3,"\xe2\xe3\x7f\x0",false)
,_execi115(115,this,(LengthFuncPointer)&AppC__TCPIP::_execi115_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write159(159,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send184(184,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action160(160,this,(ActionFuncPointer)&AppC__TCPIP::_action160_func, "\xe2\xe3\x7f\x0",false)
,_execi105(105,this,(LengthFuncPointer)&AppC__TCPIP::_execi105_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write156(156,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send183(183,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action157(157,this,(ActionFuncPointer)&AppC__TCPIP::_action157_func, "\xe2\xe3\x7f\x0",false)
,_execi102(102,this,(LengthFuncPointer)&AppC__TCPIP::_execi102_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write187(187,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_action188(188,this,(ActionFuncPointer)&AppC__TCPIP::_action188_func, "\xa2\xe3\x7f\x0",false)
,_send191(191,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action190(190,this,(ActionFuncPointer)&AppC__TCPIP::_action190_func, "\xe2\xe3\x7f\x0",false)
,_choice101(101,this,(RangeFuncPointer)&AppC__TCPIP::_choice101_func,2,"\xe2\xe3\x7f\x0",false)
,_choice106(106,this,(RangeFuncPointer)&AppC__TCPIP::_choice106_func,3,"\xe2\xe3\x7f\x0",false)
,_choice111(111,this,(RangeFuncPointer)&AppC__TCPIP::_choice111_func,3,"\xe2\xe3\x7f\x0",false)
,_execi113(113,this,(LengthFuncPointer)&AppC__TCPIP::_execi113_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action175(175,this,(ActionFuncPointer)&AppC__TCPIP::_action175_func, "\xa2\xe3\x7f\x0",false)
,_write147(147,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send179(179,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action141(141,this,(ActionFuncPointer)&AppC__TCPIP::_action141_func, "\xe2\xe3\x7f\x0",false)
,_request171(171,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write172(172,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi109(109,this,(LengthFuncPointer)&AppC__TCPIP::_execi109_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action176(176,this,(ActionFuncPointer)&AppC__TCPIP::_action176_func, "\xa2\xe3\x7f\x0",false)
,_write148(148,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send178(178,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action145(145,this,(ActionFuncPointer)&AppC__TCPIP::_action145_func, "\xe2\xe3\x7f\x0",false)
,_request169(169,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write170(170,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_action154(154,this,(ActionFuncPointer)&AppC__TCPIP::_action154_func, "\xe2\xe3\x7f\x0",false)
,_choice103(103,this,(RangeFuncPointer)&AppC__TCPIP::_choice103_func,2,"\xe2\xe3\x7f\x0",false)
,_choice118(118,this,(RangeFuncPointer)&AppC__TCPIP::_choice118_func,3,"\xe2\xe3\x7f\x0",false)
,_read138(138,this,0,channel__AppC__fromAtoT,"\xe2\xe3\x7f\x0",true,1)
,_execi114(114,this,(LengthFuncPointer)&AppC__TCPIP::_execi114_func,0,1,"\xe2\xe3\x7f\x0",false)
,_action174(174,this,(ActionFuncPointer)&AppC__TCPIP::_action174_func, "\xe2\xe3\x7f\x0",false)
,_write139(139,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send177(177,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_request167(167,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write168(168,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi110(110,this,(LengthFuncPointer)&AppC__TCPIP::_execi110_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action173(173,this,(ActionFuncPointer)&AppC__TCPIP::_action173_func, "\xa2\xe3\x7f\x0",false)
,_write144(144,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send180(180,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action143(143,this,(ActionFuncPointer)&AppC__TCPIP::_action143_func, "\xe2\xe3\x7f\x0",false)
,_request164(164,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write165(165,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_choice119(119,this,(RangeFuncPointer)&AppC__TCPIP::_choice119_func,3,"\xe2\xe3\x7f\x0",false)
,_action120(120,this,(ActionFuncPointer)&AppC__TCPIP::_action120_func, "\xe2\xe3\x7f\x0",false)
,_choice116(116,this,(RangeFuncPointer)&AppC__TCPIP::_choice116_func,2,"\xe2\xe3\x7f\x0",false)
,_select133(133,this,array(5,(TMLEventChannel*)event__AppC__timeOut__AppC__timeOut,(TMLEventChannel*)event__AppC__receive__AppC__receive,(TMLEventChannel*)event__AppC__close__AppC__close,(TMLEventChannel*)event__AppC__send_TCP__AppC__send_TCP,(TMLEventChannel*)event__AppC__open__AppC__open),5,"\xe2\xe3\x7f\x0",false,array(5,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0))
, _stop117(117,this)
,_choice117(117,this,(RangeFuncPointer)&AppC__TCPIP::_choice117_func,3,"\xe2\xe3\x7f\x0",false)
,_action125(125,this,(ActionFuncPointer)&AppC__TCPIP::_action125_func, "\xe0\xe3\x7f\x0",false)
,_lpChoice123(123,this,(RangeFuncPointer)&AppC__TCPIP::_lpChoice123_func,2,0, false)
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
    _varLookUpID[17]=&wind;
    _varLookUpName["i"]=&i;
    _varLookUpID[18]=&i;
    _varLookUpName["j"]=&j;
    _varLookUpID[19]=&j;
    _varLookUpName["a"]=&a;
    _varLookUpID[20]=&a;
    _varLookUpName["b"]=&b;
    _varLookUpID[21]=&b;
    _varLookUpName["tcpctrl__a"]=&tcpctrl__a;
    _varLookUpID[22]=&tcpctrl__a;
    _varLookUpName["tcpctrl__state"]=&tcpctrl__state;
    _varLookUpID[23]=&tcpctrl__state;
    _varLookUpName["seqi"]=&seqi;
    _varLookUpID[24]=&seqi;
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
    _lpIncAc123.setNextCommand(array(1,(TMLCommand*)&_lpChoice123));
    _wait136.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _send181.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _write162.setNextCommand(array(1,(TMLCommand*)&_send181));
    _execi104.setNextCommand(array(1,(TMLCommand*)&_write162));
    _read127.setNextCommand(array(1,(TMLCommand*)&_execi104));
    _write201.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _send200.setNextCommand(array(1,(TMLCommand*)&_write201));
    _write197.setNextCommand(array(1,(TMLCommand*)&_send200));
    _execi98.setNextCommand(array(1,(TMLCommand*)&_write197));
    _action203.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _action205.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _action210.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _action211.setNextCommand(array(1,(TMLCommand*)&_action210));
    _action208.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _choice89.setNextCommand(array(2,(TMLCommand*)&_action208,(TMLCommand*)&_lpIncAc123));
    _choice91.setNextCommand(array(3,(TMLCommand*)&_action205,(TMLCommand*)&_action211,(TMLCommand*)&_choice89));
    _choice92.setNextCommand(array(3,(TMLCommand*)&_action203,(TMLCommand*)&_lpIncAc123,(TMLCommand*)&_choice91));
    _choice93.setNextCommand(array(2,(TMLCommand*)&_execi98,(TMLCommand*)&_choice92));
    _read227.setNextCommand(array(1,(TMLCommand*)&_choice93));
    _send196.setNextCommand(array(1,(TMLCommand*)&_read227));
    _send199.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _write195.setNextCommand(array(1,(TMLCommand*)&_send199));
    _execi94.setNextCommand(array(1,(TMLCommand*)&_write195));
    _send226.setNextCommand(array(1,(TMLCommand*)&_execi94));
    _write194.setNextCommand(array(1,(TMLCommand*)&_send226));
    _execi95.setNextCommand(array(1,(TMLCommand*)&_write194));
    _choice96.setNextCommand(array(2,(TMLCommand*)&_execi95,(TMLCommand*)&_lpIncAc123));
    _action218.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _write215.setNextCommand(array(1,(TMLCommand*)&_action218));
    _request214.setNextCommand(array(1,(TMLCommand*)&_write215));
    _send217.setNextCommand(array(1,(TMLCommand*)&_request214));
    _write212.setNextCommand(array(1,(TMLCommand*)&_send217));
    _action216.setNextCommand(array(1,(TMLCommand*)&_write212));
    _execi99.setNextCommand(array(1,(TMLCommand*)&_action216));
    _action220.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _send222.setNextCommand(array(1,(TMLCommand*)&_action220));
    _write219.setNextCommand(array(1,(TMLCommand*)&_send222));
    _execi100.setNextCommand(array(1,(TMLCommand*)&_write219));
    _choice90.setNextCommand(array(3,(TMLCommand*)&_execi99,(TMLCommand*)&_execi100,(TMLCommand*)&_lpIncAc123));
    _choice97.setNextCommand(array(3,(TMLCommand*)&_send196,(TMLCommand*)&_choice96,(TMLCommand*)&_choice90));
    _action150.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _action153.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _send182.setNextCommand(array(1,(TMLCommand*)&_action153));
    _write152.setNextCommand(array(1,(TMLCommand*)&_send182));
    _execi107.setNextCommand(array(1,(TMLCommand*)&_write152));
    _choice108.setNextCommand(array(3,(TMLCommand*)&_action150,(TMLCommand*)&_execi107,(TMLCommand*)&_lpIncAc123));
    _action160.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _send184.setNextCommand(array(1,(TMLCommand*)&_action160));
    _write159.setNextCommand(array(1,(TMLCommand*)&_send184));
    _execi115.setNextCommand(array(1,(TMLCommand*)&_write159));
    _action157.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _send183.setNextCommand(array(1,(TMLCommand*)&_action157));
    _write156.setNextCommand(array(1,(TMLCommand*)&_send183));
    _execi105.setNextCommand(array(1,(TMLCommand*)&_write156));
    _action190.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _send191.setNextCommand(array(1,(TMLCommand*)&_action190));
    _action188.setNextCommand(array(1,(TMLCommand*)&_send191));
    _write187.setNextCommand(array(1,(TMLCommand*)&_action188));
    _execi102.setNextCommand(array(1,(TMLCommand*)&_write187));
    _choice101.setNextCommand(array(2,(TMLCommand*)&_execi102,(TMLCommand*)&_lpIncAc123));
    _choice106.setNextCommand(array(3,(TMLCommand*)&_execi115,(TMLCommand*)&_execi105,(TMLCommand*)&_choice101));
    _choice111.setNextCommand(array(3,(TMLCommand*)&_choice97,(TMLCommand*)&_choice108,(TMLCommand*)&_choice106));
    _execi112.setNextCommand(array(1,(TMLCommand*)&_choice111));
    _read122.setNextCommand(array(1,(TMLCommand*)&_execi112));
    _write172.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _request171.setNextCommand(array(1,(TMLCommand*)&_write172));
    _action141.setNextCommand(array(1,(TMLCommand*)&_request171));
    _send179.setNextCommand(array(1,(TMLCommand*)&_action141));
    _write147.setNextCommand(array(1,(TMLCommand*)&_send179));
    _action175.setNextCommand(array(1,(TMLCommand*)&_write147));
    _execi113.setNextCommand(array(1,(TMLCommand*)&_action175));
    _write170.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _request169.setNextCommand(array(1,(TMLCommand*)&_write170));
    _action145.setNextCommand(array(1,(TMLCommand*)&_request169));
    _send178.setNextCommand(array(1,(TMLCommand*)&_action145));
    _write148.setNextCommand(array(1,(TMLCommand*)&_send178));
    _action176.setNextCommand(array(1,(TMLCommand*)&_write148));
    _execi109.setNextCommand(array(1,(TMLCommand*)&_action176));
    _action154.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _choice103.setNextCommand(array(2,(TMLCommand*)&_action154,(TMLCommand*)&_lpIncAc123));
    _choice118.setNextCommand(array(3,(TMLCommand*)&_execi113,(TMLCommand*)&_execi109,(TMLCommand*)&_choice103));
    _write168.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _request167.setNextCommand(array(1,(TMLCommand*)&_write168));
    _send177.setNextCommand(array(1,(TMLCommand*)&_request167));
    _write139.setNextCommand(array(1,(TMLCommand*)&_send177));
    _action174.setNextCommand(array(1,(TMLCommand*)&_write139));
    _execi114.setNextCommand(array(1,(TMLCommand*)&_action174));
    _read138.setNextCommand(array(1,(TMLCommand*)&_execi114));
    _write165.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _request164.setNextCommand(array(1,(TMLCommand*)&_write165));
    _action143.setNextCommand(array(1,(TMLCommand*)&_request164));
    _send180.setNextCommand(array(1,(TMLCommand*)&_action143));
    _write144.setNextCommand(array(1,(TMLCommand*)&_send180));
    _action173.setNextCommand(array(1,(TMLCommand*)&_write144));
    _execi110.setNextCommand(array(1,(TMLCommand*)&_action173));
    _choice119.setNextCommand(array(3,(TMLCommand*)&_read138,(TMLCommand*)&_execi110,(TMLCommand*)&_lpIncAc123));
    _action120.setNextCommand(array(1,(TMLCommand*)&_lpIncAc123));
    _choice116.setNextCommand(array(2,(TMLCommand*)&_action120,(TMLCommand*)&_lpIncAc123));
    _select133.setNextCommand(array(5,(TMLCommand*)&_read127,(TMLCommand*)&_read122,(TMLCommand*)&_choice118,(TMLCommand*)&_choice119,(TMLCommand*)&_choice116));
    _choice117.setNextCommand(array(3,(TMLCommand*)&_wait136,(TMLCommand*)&_select133,(TMLCommand*)&_stop117));
    _notified126.setNextCommand(array(1,(TMLCommand*)&_choice117));
    _action125.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _lpChoice123.setNextCommand(array(2,(TMLCommand*)&_notified126,(TMLCommand*)&_action125));
    _action262.setNextCommand(array(1,(TMLCommand*)&_lpChoice123));
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

void AppC__TCPIP::_lpIncAc123_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    i = i;
}

TMLLength AppC__TCPIP::_execi104_func(){
    return (TMLLength)(b);
}

TMLLength AppC__TCPIP::_execi112_func(){
    return (TMLLength)(b);
}

TMLLength AppC__TCPIP::_execi98_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action203_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    tcpctrl__state =4;
}

void AppC__TCPIP::_action205_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    tcpctrl__state =6;
}

void AppC__TCPIP::_action210_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,3));
    #endif
    tcpctrl__state =0;
}

void AppC__TCPIP::_action211_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,4));
    #endif
    tcpctrl__state =8;
}

void AppC__TCPIP::_action208_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,5));
    #endif
    tcpctrl__state =0;
}

unsigned int AppC__TCPIP::_choice89_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice91_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice92_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice93_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=1;
    return myrand(0, 1);
    
}

TMLLength AppC__TCPIP::_execi95_func(){
    return (TMLLength)(b);
}

TMLLength AppC__TCPIP::_execi94_func(){
    return (TMLLength)(b);
}

unsigned int AppC__TCPIP::_choice96_func(ParamType& oMin, ParamType& oMax){
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

TMLLength AppC__TCPIP::_execi99_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action218_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,6));
    #endif
    tcpctrl__state =2;
}

void AppC__TCPIP::_action216_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,7));
    #endif
    seqi = seqi + wind;
}

TMLLength AppC__TCPIP::_execi100_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action220_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,8));
    #endif
    tcpctrl__state =2;
}

unsigned int AppC__TCPIP::_choice90_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice97_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=2;
    return myrand(0, 2);
    
}

void AppC__TCPIP::_action150_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,9));
    #endif
    tcpctrl__state =1;
}

TMLLength AppC__TCPIP::_execi107_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action153_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,10));
    #endif
    tcpctrl__state =3;
}

unsigned int AppC__TCPIP::_choice108_func(ParamType& oMin, ParamType& oMax){
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

TMLLength AppC__TCPIP::_execi115_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action160_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,11));
    #endif
    tcpctrl__state =7;
}

TMLLength AppC__TCPIP::_execi105_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action157_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,12));
    #endif
    tcpctrl__state =9;
}

TMLLength AppC__TCPIP::_execi102_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action190_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,13));
    #endif
    tcpctrl__state =0;
}

void AppC__TCPIP::_action188_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,14));
    #endif
    tcpctrl__state =8;
}

unsigned int AppC__TCPIP::_choice101_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice106_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice111_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=2;
    return myrand(0, 2);
    
}

TMLLength AppC__TCPIP::_execi113_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action141_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,15));
    #endif
    tcpctrl__state =5;
}

void AppC__TCPIP::_action175_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,16));
    #endif
    seqi = seqi + wind;
}

TMLLength AppC__TCPIP::_execi109_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action145_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,17));
    #endif
    tcpctrl__state =10;
}

void AppC__TCPIP::_action176_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,18));
    #endif
    seqi = seqi + wind;
}

void AppC__TCPIP::_action154_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,19));
    #endif
    tcpctrl__state =0;
}

unsigned int AppC__TCPIP::_choice103_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice118_func(ParamType& oMin, ParamType& oMax){
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

TMLLength AppC__TCPIP::_execi114_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action174_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,20));
    #endif
    seqi = seqi + wind;
}

TMLLength AppC__TCPIP::_execi110_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action143_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,21));
    #endif
    tcpctrl__state=3;
}

void AppC__TCPIP::_action173_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,22));
    #endif
    seqi = seqi + wind;
}

unsigned int AppC__TCPIP::_choice119_func(ParamType& oMin, ParamType& oMax){
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

void AppC__TCPIP::_action120_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,23));
    #endif
    tcpctrl__state =1;
}

unsigned int AppC__TCPIP::_choice116_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice117_func(ParamType& oMin, ParamType& oMax){
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

void AppC__TCPIP::_action125_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,24));
    #endif
    tcpctrl__state=0;
}

unsigned int AppC__TCPIP::_lpChoice123_func(ParamType& oMin, ParamType& oMax){
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

