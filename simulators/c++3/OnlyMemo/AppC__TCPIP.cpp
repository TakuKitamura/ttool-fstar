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
,_waitOnRequest(97,this,requestChannel,0,"\xe0\xe3\x7f\x0",false)
,_lpIncAc132(132,this,(ActionFuncPointer)&AppC__TCPIP::_lpIncAc132_func, 0, false)
,_notified135(135,this,event__AppC__abort__AppC__abort,&tcpctrl__a,"tcpctrl__a","\xe2\xe3\x7f\x0",false)
,_wait145(145,this,event__AppC__abort__AppC__abort,0,"\xe2\xe3\x7f\x0",true)
,_read136(136,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi113(113,this,(LengthFuncPointer)&AppC__TCPIP::_execi113_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write171(171,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send190(190,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_read131(131,this,0,channel__AppC__fromPtoT,"\xe2\xe3\x7f\x0",true,1)
,_execi121(121,this,(LengthFuncPointer)&AppC__TCPIP::_execi121_func,0,1,"\xe2\xe3\x7f\x0",false)
,_send205(205,this,event__AppC__stop__AppC__stop,0,"\xe2\xe3\x7f\x0",true)
,_read236(236,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi107(107,this,(LengthFuncPointer)&AppC__TCPIP::_execi107_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write206(206,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send209(209,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_write210(210,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_action212(212,this,(ActionFuncPointer)&AppC__TCPIP::_action212_func, "\xe2\xe3\x7f\x0",false)
,_action214(214,this,(ActionFuncPointer)&AppC__TCPIP::_action214_func, "\xe2\xe3\x7f\x0",false)
,_action220(220,this,(ActionFuncPointer)&AppC__TCPIP::_action220_func, "\xa2\xe3\x7f\x0",false)
,_action219(219,this,(ActionFuncPointer)&AppC__TCPIP::_action219_func, "\xe2\xe3\x7f\x0",false)
,_action217(217,this,(ActionFuncPointer)&AppC__TCPIP::_action217_func, "\xe2\xe3\x7f\x0",false)
,_choice98(98,this,(RangeFuncPointer)&AppC__TCPIP::_choice98_func,2,"\xe2\xe3\x7f\x0",false)
,_choice100(100,this,(RangeFuncPointer)&AppC__TCPIP::_choice100_func,3,"\xe2\xe3\x7f\x0",false)
,_choice101(101,this,(RangeFuncPointer)&AppC__TCPIP::_choice101_func,3,"\xe2\xe3\x7f\x0",false)
,_choice102(102,this,(RangeFuncPointer)&AppC__TCPIP::_choice102_func,2,"\xe2\xe3\x7f\x0",false)
,_execi104(104,this,(LengthFuncPointer)&AppC__TCPIP::_execi104_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write203(203,this,0,channel__AppC__fromTtoA,"\xe2\xe3\x7f\x0",true,1)
,_send235(235,this,event__AppC__receive_Application__AppC__receive_Application,0,"\xe2\xe3\x7f\x0",true)
,_execi103(103,this,(LengthFuncPointer)&AppC__TCPIP::_execi103_func,0,1,"\xe2\xe3\x7f\x0",false)
,_write204(204,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send208(208,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_choice105(105,this,(RangeFuncPointer)&AppC__TCPIP::_choice105_func,2,"\xe2\xe3\x7f\x0",false)
,_execi108(108,this,(LengthFuncPointer)&AppC__TCPIP::_execi108_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action225(225,this,(ActionFuncPointer)&AppC__TCPIP::_action225_func, "\xa2\xe3\x7f\x0",false)
,_write221(221,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send226(226,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_request223(223,this,request__AppC__req_Timer,0,"\xa2\xe3\x7f\x0",true)
,_write224(224,this,0,channel__AppC__temp,"\xa2\xe3\x7f\x0",false,1)
,_action227(227,this,(ActionFuncPointer)&AppC__TCPIP::_action227_func, "\xe2\xe3\x7f\x0",false)
,_execi109(109,this,(LengthFuncPointer)&AppC__TCPIP::_execi109_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write228(228,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send231(231,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action229(229,this,(ActionFuncPointer)&AppC__TCPIP::_action229_func, "\xe2\xe3\x7f\x0",false)
,_choice99(99,this,(RangeFuncPointer)&AppC__TCPIP::_choice99_func,3,"\xe2\xe3\x7f\x0",false)
,_choice106(106,this,(RangeFuncPointer)&AppC__TCPIP::_choice106_func,3,"\xe2\xe3\x7f\x0",false)
,_action159(159,this,(ActionFuncPointer)&AppC__TCPIP::_action159_func, "\xe2\xe3\x7f\x0",false)
,_execi116(116,this,(LengthFuncPointer)&AppC__TCPIP::_execi116_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write161(161,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send191(191,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action162(162,this,(ActionFuncPointer)&AppC__TCPIP::_action162_func, "\xe2\xe3\x7f\x0",false)
,_choice117(117,this,(RangeFuncPointer)&AppC__TCPIP::_choice117_func,3,"\xe2\xe3\x7f\x0",false)
,_execi124(124,this,(LengthFuncPointer)&AppC__TCPIP::_execi124_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write168(168,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send193(193,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action169(169,this,(ActionFuncPointer)&AppC__TCPIP::_action169_func, "\xe2\xe3\x7f\x0",false)
,_execi114(114,this,(LengthFuncPointer)&AppC__TCPIP::_execi114_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write165(165,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send192(192,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action166(166,this,(ActionFuncPointer)&AppC__TCPIP::_action166_func, "\xe2\xe3\x7f\x0",false)
,_execi111(111,this,(LengthFuncPointer)&AppC__TCPIP::_execi111_func,0,1,"\xa2\xe3\x7f\x0",false)
,_write196(196,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_action197(197,this,(ActionFuncPointer)&AppC__TCPIP::_action197_func, "\xa2\xe3\x7f\x0",false)
,_send200(200,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action199(199,this,(ActionFuncPointer)&AppC__TCPIP::_action199_func, "\xe2\xe3\x7f\x0",false)
,_choice110(110,this,(RangeFuncPointer)&AppC__TCPIP::_choice110_func,2,"\xe2\xe3\x7f\x0",false)
,_choice115(115,this,(RangeFuncPointer)&AppC__TCPIP::_choice115_func,3,"\xe2\xe3\x7f\x0",false)
,_choice120(120,this,(RangeFuncPointer)&AppC__TCPIP::_choice120_func,3,"\xe2\xe3\x7f\x0",false)
,_execi122(122,this,(LengthFuncPointer)&AppC__TCPIP::_execi122_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action184(184,this,(ActionFuncPointer)&AppC__TCPIP::_action184_func, "\xa2\xe3\x7f\x0",false)
,_write156(156,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send188(188,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action150(150,this,(ActionFuncPointer)&AppC__TCPIP::_action150_func, "\xe2\xe3\x7f\x0",false)
,_request180(180,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write181(181,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi118(118,this,(LengthFuncPointer)&AppC__TCPIP::_execi118_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action185(185,this,(ActionFuncPointer)&AppC__TCPIP::_action185_func, "\xa2\xe3\x7f\x0",false)
,_write157(157,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send187(187,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action154(154,this,(ActionFuncPointer)&AppC__TCPIP::_action154_func, "\xe2\xe3\x7f\x0",false)
,_request178(178,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write179(179,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_action163(163,this,(ActionFuncPointer)&AppC__TCPIP::_action163_func, "\xe2\xe3\x7f\x0",false)
,_choice112(112,this,(RangeFuncPointer)&AppC__TCPIP::_choice112_func,2,"\xe2\xe3\x7f\x0",false)
,_choice127(127,this,(RangeFuncPointer)&AppC__TCPIP::_choice127_func,3,"\xe2\xe3\x7f\x0",false)
,_read147(147,this,0,channel__AppC__fromAtoT,"\xe2\xe3\x7f\x0",true,1)
,_execi123(123,this,(LengthFuncPointer)&AppC__TCPIP::_execi123_func,0,1,"\xe2\xe3\x7f\x0",false)
,_action183(183,this,(ActionFuncPointer)&AppC__TCPIP::_action183_func, "\xe2\xe3\x7f\x0",false)
,_write148(148,this,0,channel__AppC__fromTtoP,"\xe2\xe3\x7f\x0",true,1)
,_send186(186,this,event__AppC__send__AppC__send,0,"\xe2\xe3\x7f\x0",true)
,_request176(176,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write177(177,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_execi119(119,this,(LengthFuncPointer)&AppC__TCPIP::_execi119_func,0,1,"\xa2\xe3\x7f\x0",false)
,_action182(182,this,(ActionFuncPointer)&AppC__TCPIP::_action182_func, "\xa2\xe3\x7f\x0",false)
,_write153(153,this,0,channel__AppC__fromTtoP,"\xa2\xe3\x7f\x0",true,1)
,_send189(189,this,event__AppC__send__AppC__send,0,"\xa2\xe3\x7f\x0",true)
,_action152(152,this,(ActionFuncPointer)&AppC__TCPIP::_action152_func, "\xe2\xe3\x7f\x0",false)
,_request173(173,this,request__AppC__req_Timer,0,"\xe2\xe3\x7f\x0",true)
,_write174(174,this,0,channel__AppC__temp,"\xe2\xe3\x7f\x0",false,1)
,_choice128(128,this,(RangeFuncPointer)&AppC__TCPIP::_choice128_func,3,"\xe2\xe3\x7f\x0",false)
,_action129(129,this,(ActionFuncPointer)&AppC__TCPIP::_action129_func, "\xe2\xe3\x7f\x0",false)
,_choice125(125,this,(RangeFuncPointer)&AppC__TCPIP::_choice125_func,2,"\xe2\xe3\x7f\x0",false)
,_select142(142,this,array(5,(TMLEventChannel*)event__AppC__timeOut__AppC__timeOut,(TMLEventChannel*)event__AppC__receive__AppC__receive,(TMLEventChannel*)event__AppC__close__AppC__close,(TMLEventChannel*)event__AppC__send_TCP__AppC__send_TCP,(TMLEventChannel*)event__AppC__open__AppC__open),5,"\xe2\xe3\x7f\x0",false,array(5,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0))
, _stop126(126,this)
,_choice126(126,this,(RangeFuncPointer)&AppC__TCPIP::_choice126_func,3,"\xe2\xe3\x7f\x0",false)
,_action134(134,this,(ActionFuncPointer)&AppC__TCPIP::_action134_func, "\xe0\xe3\x7f\x0",false)
,_lpChoice132(132,this,(RangeFuncPointer)&AppC__TCPIP::_lpChoice132_func,2,0, false)
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
    _varLookUpID[20]=&wind;
    _varLookUpName["i"]=&i;
    _varLookUpID[21]=&i;
    _varLookUpName["j"]=&j;
    _varLookUpID[22]=&j;
    _varLookUpName["a"]=&a;
    _varLookUpID[23]=&a;
    _varLookUpName["b"]=&b;
    _varLookUpID[24]=&b;
    _varLookUpName["tcpctrl__a"]=&tcpctrl__a;
    _varLookUpID[25]=&tcpctrl__a;
    _varLookUpName["tcpctrl__state"]=&tcpctrl__state;
    _varLookUpID[26]=&tcpctrl__state;
    _varLookUpName["seqi"]=&seqi;
    _varLookUpID[27]=&seqi;
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
    _lpIncAc132.setNextCommand(array(1,(TMLCommand*)&_lpChoice132));
    _wait145.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _send190.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _write171.setNextCommand(array(1,(TMLCommand*)&_send190));
    _execi113.setNextCommand(array(1,(TMLCommand*)&_write171));
    _read136.setNextCommand(array(1,(TMLCommand*)&_execi113));
    _write210.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _send209.setNextCommand(array(1,(TMLCommand*)&_write210));
    _write206.setNextCommand(array(1,(TMLCommand*)&_send209));
    _execi107.setNextCommand(array(1,(TMLCommand*)&_write206));
    _action212.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _action214.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _action219.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _action220.setNextCommand(array(1,(TMLCommand*)&_action219));
    _action217.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _choice98.setNextCommand(array(2,(TMLCommand*)&_action217,(TMLCommand*)&_lpIncAc132));
    _choice100.setNextCommand(array(3,(TMLCommand*)&_action214,(TMLCommand*)&_action220,(TMLCommand*)&_choice98));
    _choice101.setNextCommand(array(3,(TMLCommand*)&_action212,(TMLCommand*)&_lpIncAc132,(TMLCommand*)&_choice100));
    _choice102.setNextCommand(array(2,(TMLCommand*)&_execi107,(TMLCommand*)&_choice101));
    _read236.setNextCommand(array(1,(TMLCommand*)&_choice102));
    _send205.setNextCommand(array(1,(TMLCommand*)&_read236));
    _send208.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _write204.setNextCommand(array(1,(TMLCommand*)&_send208));
    _execi103.setNextCommand(array(1,(TMLCommand*)&_write204));
    _send235.setNextCommand(array(1,(TMLCommand*)&_execi103));
    _write203.setNextCommand(array(1,(TMLCommand*)&_send235));
    _execi104.setNextCommand(array(1,(TMLCommand*)&_write203));
    _choice105.setNextCommand(array(2,(TMLCommand*)&_execi104,(TMLCommand*)&_lpIncAc132));
    _action227.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _write224.setNextCommand(array(1,(TMLCommand*)&_action227));
    _request223.setNextCommand(array(1,(TMLCommand*)&_write224));
    _send226.setNextCommand(array(1,(TMLCommand*)&_request223));
    _write221.setNextCommand(array(1,(TMLCommand*)&_send226));
    _action225.setNextCommand(array(1,(TMLCommand*)&_write221));
    _execi108.setNextCommand(array(1,(TMLCommand*)&_action225));
    _action229.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _send231.setNextCommand(array(1,(TMLCommand*)&_action229));
    _write228.setNextCommand(array(1,(TMLCommand*)&_send231));
    _execi109.setNextCommand(array(1,(TMLCommand*)&_write228));
    _choice99.setNextCommand(array(3,(TMLCommand*)&_execi108,(TMLCommand*)&_execi109,(TMLCommand*)&_lpIncAc132));
    _choice106.setNextCommand(array(3,(TMLCommand*)&_send205,(TMLCommand*)&_choice105,(TMLCommand*)&_choice99));
    _action159.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _action162.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _send191.setNextCommand(array(1,(TMLCommand*)&_action162));
    _write161.setNextCommand(array(1,(TMLCommand*)&_send191));
    _execi116.setNextCommand(array(1,(TMLCommand*)&_write161));
    _choice117.setNextCommand(array(3,(TMLCommand*)&_action159,(TMLCommand*)&_execi116,(TMLCommand*)&_lpIncAc132));
    _action169.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _send193.setNextCommand(array(1,(TMLCommand*)&_action169));
    _write168.setNextCommand(array(1,(TMLCommand*)&_send193));
    _execi124.setNextCommand(array(1,(TMLCommand*)&_write168));
    _action166.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _send192.setNextCommand(array(1,(TMLCommand*)&_action166));
    _write165.setNextCommand(array(1,(TMLCommand*)&_send192));
    _execi114.setNextCommand(array(1,(TMLCommand*)&_write165));
    _action199.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _send200.setNextCommand(array(1,(TMLCommand*)&_action199));
    _action197.setNextCommand(array(1,(TMLCommand*)&_send200));
    _write196.setNextCommand(array(1,(TMLCommand*)&_action197));
    _execi111.setNextCommand(array(1,(TMLCommand*)&_write196));
    _choice110.setNextCommand(array(2,(TMLCommand*)&_execi111,(TMLCommand*)&_lpIncAc132));
    _choice115.setNextCommand(array(3,(TMLCommand*)&_execi124,(TMLCommand*)&_execi114,(TMLCommand*)&_choice110));
    _choice120.setNextCommand(array(3,(TMLCommand*)&_choice106,(TMLCommand*)&_choice117,(TMLCommand*)&_choice115));
    _execi121.setNextCommand(array(1,(TMLCommand*)&_choice120));
    _read131.setNextCommand(array(1,(TMLCommand*)&_execi121));
    _write181.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _request180.setNextCommand(array(1,(TMLCommand*)&_write181));
    _action150.setNextCommand(array(1,(TMLCommand*)&_request180));
    _send188.setNextCommand(array(1,(TMLCommand*)&_action150));
    _write156.setNextCommand(array(1,(TMLCommand*)&_send188));
    _action184.setNextCommand(array(1,(TMLCommand*)&_write156));
    _execi122.setNextCommand(array(1,(TMLCommand*)&_action184));
    _write179.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _request178.setNextCommand(array(1,(TMLCommand*)&_write179));
    _action154.setNextCommand(array(1,(TMLCommand*)&_request178));
    _send187.setNextCommand(array(1,(TMLCommand*)&_action154));
    _write157.setNextCommand(array(1,(TMLCommand*)&_send187));
    _action185.setNextCommand(array(1,(TMLCommand*)&_write157));
    _execi118.setNextCommand(array(1,(TMLCommand*)&_action185));
    _action163.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _choice112.setNextCommand(array(2,(TMLCommand*)&_action163,(TMLCommand*)&_lpIncAc132));
    _choice127.setNextCommand(array(3,(TMLCommand*)&_execi122,(TMLCommand*)&_execi118,(TMLCommand*)&_choice112));
    _write177.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _request176.setNextCommand(array(1,(TMLCommand*)&_write177));
    _send186.setNextCommand(array(1,(TMLCommand*)&_request176));
    _write148.setNextCommand(array(1,(TMLCommand*)&_send186));
    _action183.setNextCommand(array(1,(TMLCommand*)&_write148));
    _execi123.setNextCommand(array(1,(TMLCommand*)&_action183));
    _read147.setNextCommand(array(1,(TMLCommand*)&_execi123));
    _write174.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _request173.setNextCommand(array(1,(TMLCommand*)&_write174));
    _action152.setNextCommand(array(1,(TMLCommand*)&_request173));
    _send189.setNextCommand(array(1,(TMLCommand*)&_action152));
    _write153.setNextCommand(array(1,(TMLCommand*)&_send189));
    _action182.setNextCommand(array(1,(TMLCommand*)&_write153));
    _execi119.setNextCommand(array(1,(TMLCommand*)&_action182));
    _choice128.setNextCommand(array(3,(TMLCommand*)&_read147,(TMLCommand*)&_execi119,(TMLCommand*)&_lpIncAc132));
    _action129.setNextCommand(array(1,(TMLCommand*)&_lpIncAc132));
    _choice125.setNextCommand(array(2,(TMLCommand*)&_action129,(TMLCommand*)&_lpIncAc132));
    _select142.setNextCommand(array(5,(TMLCommand*)&_read136,(TMLCommand*)&_read131,(TMLCommand*)&_choice127,(TMLCommand*)&_choice128,(TMLCommand*)&_choice125));
    _choice126.setNextCommand(array(3,(TMLCommand*)&_wait145,(TMLCommand*)&_select142,(TMLCommand*)&_stop126));
    _notified135.setNextCommand(array(1,(TMLCommand*)&_choice126));
    _action134.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _lpChoice132.setNextCommand(array(2,(TMLCommand*)&_notified135,(TMLCommand*)&_action134));
    _action262.setNextCommand(array(1,(TMLCommand*)&_lpChoice132));
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

void AppC__TCPIP::_lpIncAc132_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    i = i;
}

TMLLength AppC__TCPIP::_execi113_func(){
    return (TMLLength)(b);
}

TMLLength AppC__TCPIP::_execi121_func(){
    return (TMLLength)(b);
}

TMLLength AppC__TCPIP::_execi107_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action212_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    tcpctrl__state =4;
}

void AppC__TCPIP::_action214_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    tcpctrl__state =6;
}

void AppC__TCPIP::_action219_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,3));
    #endif
    tcpctrl__state =0;
}

void AppC__TCPIP::_action220_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,4));
    #endif
    tcpctrl__state =8;
}

void AppC__TCPIP::_action217_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,5));
    #endif
    tcpctrl__state =0;
}

unsigned int AppC__TCPIP::_choice98_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice100_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice101_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice102_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=1;
    return myrand(0, 1);
    
}

TMLLength AppC__TCPIP::_execi104_func(){
    return (TMLLength)(b);
}

TMLLength AppC__TCPIP::_execi103_func(){
    return (TMLLength)(b);
}

unsigned int AppC__TCPIP::_choice105_func(ParamType& oMin, ParamType& oMax){
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

TMLLength AppC__TCPIP::_execi108_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action227_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,6));
    #endif
    tcpctrl__state =2;
}

void AppC__TCPIP::_action225_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,7));
    #endif
    seqi = seqi + wind;
}

TMLLength AppC__TCPIP::_execi109_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action229_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,8));
    #endif
    tcpctrl__state =2;
}

unsigned int AppC__TCPIP::_choice99_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice106_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=2;
    return myrand(0, 2);
    
}

void AppC__TCPIP::_action159_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,9));
    #endif
    tcpctrl__state =1;
}

TMLLength AppC__TCPIP::_execi116_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action162_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,10));
    #endif
    tcpctrl__state =3;
}

unsigned int AppC__TCPIP::_choice117_func(ParamType& oMin, ParamType& oMax){
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

TMLLength AppC__TCPIP::_execi124_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action169_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,11));
    #endif
    tcpctrl__state =7;
}

TMLLength AppC__TCPIP::_execi114_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action166_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,12));
    #endif
    tcpctrl__state =9;
}

TMLLength AppC__TCPIP::_execi111_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action199_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,13));
    #endif
    tcpctrl__state =0;
}

void AppC__TCPIP::_action197_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,14));
    #endif
    tcpctrl__state =8;
}

unsigned int AppC__TCPIP::_choice110_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice115_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice120_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=2;
    return myrand(0, 2);
    
}

TMLLength AppC__TCPIP::_execi122_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action150_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,15));
    #endif
    tcpctrl__state =5;
}

void AppC__TCPIP::_action184_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,16));
    #endif
    seqi = seqi + wind;
}

TMLLength AppC__TCPIP::_execi118_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action154_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,17));
    #endif
    tcpctrl__state =10;
}

void AppC__TCPIP::_action185_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,18));
    #endif
    seqi = seqi + wind;
}

void AppC__TCPIP::_action163_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,19));
    #endif
    tcpctrl__state =0;
}

unsigned int AppC__TCPIP::_choice112_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice127_func(ParamType& oMin, ParamType& oMax){
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

TMLLength AppC__TCPIP::_execi123_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action183_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,20));
    #endif
    seqi = seqi + wind;
}

TMLLength AppC__TCPIP::_execi119_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action152_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,21));
    #endif
    tcpctrl__state=3;
}

void AppC__TCPIP::_action182_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,22));
    #endif
    seqi = seqi + wind;
}

unsigned int AppC__TCPIP::_choice128_func(ParamType& oMin, ParamType& oMax){
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

void AppC__TCPIP::_action129_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,23));
    #endif
    tcpctrl__state =1;
}

unsigned int AppC__TCPIP::_choice125_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice126_func(ParamType& oMin, ParamType& oMax){
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

void AppC__TCPIP::_action134_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,24));
    #endif
    tcpctrl__state=0;
}

unsigned int AppC__TCPIP::_lpChoice132_func(ParamType& oMin, ParamType& oMax){
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

