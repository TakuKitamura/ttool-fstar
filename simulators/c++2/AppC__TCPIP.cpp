#include <AppC__TCPIP.h>

AppC__TCPIP::AppC__TCPIP(ID iID, Priority iPriority, std::string iName, FPGA** iCPUs, unsigned int iNumOfCPUs
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
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,wind(64)
,seqNum(0)
,i(0)
,j(0)
,a(0)
,b(0)
,tcpctrl__a(0)
,tcpctrl__state(0)
,_waitOnRequest(108,this,requestChannel,0,"\xc2\xe3\xff\x0",false)
,_lpIncAc144(144,this,(ActionFuncPointer)&AppC__TCPIP::_lpIncAc144_func, 0, false)
,_notified147(147,this,event__AppC__abort__AppC__abort,&tcpctrl__a,"tcpctrl__a","\xc6\xe3\xff\x0",false)
,_wait157(157,this,event__AppC__abort__AppC__abort,0,"\xc6\xe3\xff\x0",true)
,_read148(148,this,0,channel__AppC__temp,"\xc6\xe3\xff\x0",false,1)
,_execi126(126,this,(LengthFuncPointer)&AppC__TCPIP::_execi126_func,0,1,"\xc6\xe3\xff\x0",false)
,_write183(183,this,0,channel__AppC__fromTtoP,"\xc6\xe3\xff\x0",true,1)
,_send202(202,this,event__AppC__send__AppC__send,0,"\xc6\xe3\xff\x0",true)
,_read143(143,this,0,channel__AppC__fromPtoT,"\xc6\xe3\xff\x0",true,1)
,_execi109(109,this,(RangeFuncPointer)&AppC__TCPIP::_execi109_func,0,"\xc6\xe3\xff\x0",false)
,_send217(217,this,event__AppC__stop__AppC__stop,0,"\xc6\xe3\xff\x0",true)
,_read248(248,this,0,channel__AppC__temp,"\xc6\xe3\xff\x0",false,1)
,_execi120(120,this,(LengthFuncPointer)&AppC__TCPIP::_execi120_func,0,1,"\xc6\xe3\xff\x0",false)
,_write218(218,this,0,channel__AppC__fromTtoP,"\xc6\xe3\xff\x0",true,1)
,_send221(221,this,event__AppC__send__AppC__send,0,"\xc6\xe3\xff\x0",true)
,_write222(222,this,0,channel__AppC__temp,"\xc6\xe3\xff\x0",false,1)
,_action224(224,this,(ActionFuncPointer)&AppC__TCPIP::_action224_func, "\xc6\xe3\xff\x0",false)
,_action226(226,this,(ActionFuncPointer)&AppC__TCPIP::_action226_func, "\xc6\xe3\xff\x0",false)
,_action232(232,this,(ActionFuncPointer)&AppC__TCPIP::_action232_func, "\x46\xe3\xff\x0",false)
,_action231(231,this,(ActionFuncPointer)&AppC__TCPIP::_action231_func, "\xc6\xe3\xff\x0",false)
,_action229(229,this,(ActionFuncPointer)&AppC__TCPIP::_action229_func, "\xc6\xe3\xff\x0",false)
,_choice111(111,this,(RangeFuncPointer)&AppC__TCPIP::_choice111_func,2,"\xc6\xe3\xff\x0",false)
,_choice113(113,this,(RangeFuncPointer)&AppC__TCPIP::_choice113_func,3,"\xc6\xe3\xff\x0",false)
,_choice114(114,this,(RangeFuncPointer)&AppC__TCPIP::_choice114_func,3,"\xc6\xe3\xff\x0",false)
,_choice115(115,this,(RangeFuncPointer)&AppC__TCPIP::_choice115_func,2,"\xc6\xe3\xff\x0",false)
,_execi117(117,this,(LengthFuncPointer)&AppC__TCPIP::_execi117_func,0,1,"\xc6\xe3\xff\x0",false)
,_write215(215,this,0,channel__AppC__fromTtoA,"\xc6\xe3\xff\x0",true,1)
,_send247(247,this,event__AppC__receive_Application__AppC__receive_Application,0,"\xc6\xe3\xff\x0",true)
,_execi116(116,this,(LengthFuncPointer)&AppC__TCPIP::_execi116_func,0,1,"\xc6\xe3\xff\x0",false)
,_write216(216,this,0,channel__AppC__fromTtoP,"\xc6\xe3\xff\x0",true,1)
,_send220(220,this,event__AppC__send__AppC__send,0,"\xc6\xe3\xff\x0",true)
,_choice118(118,this,(RangeFuncPointer)&AppC__TCPIP::_choice118_func,2,"\xc6\xe3\xff\x0",false)
,_execi121(121,this,(LengthFuncPointer)&AppC__TCPIP::_execi121_func,0,1,"\x46\xe3\xff\x0",false)
,_action237(237,this,(ActionFuncPointer)&AppC__TCPIP::_action237_func, "\x46\xe3\xff\x0",false)
,_write233(233,this,0,channel__AppC__fromTtoP,"\x46\xe3\xff\x0",true,1)
,_send238(238,this,event__AppC__send__AppC__send,0,"\x46\xe3\xff\x0",true)
,_request235(235,this,request__AppC__req_Timer,0,"\x46\xe3\xff\x0",true)
,_write236(236,this,0,channel__AppC__temp,"\x46\xe3\xff\x0",false,1)
,_action239(239,this,(ActionFuncPointer)&AppC__TCPIP::_action239_func, "\xc6\xe3\xff\x0",false)
,_execi122(122,this,(LengthFuncPointer)&AppC__TCPIP::_execi122_func,0,1,"\x46\xe3\xff\x0",false)
,_write240(240,this,0,channel__AppC__fromTtoP,"\x46\xe3\xff\x0",true,1)
,_send243(243,this,event__AppC__send__AppC__send,0,"\x46\xe3\xff\x0",true)
,_action241(241,this,(ActionFuncPointer)&AppC__TCPIP::_action241_func, "\xc6\xe3\xff\x0",false)
,_choice112(112,this,(RangeFuncPointer)&AppC__TCPIP::_choice112_func,3,"\xc6\xe3\xff\x0",false)
,_choice119(119,this,(RangeFuncPointer)&AppC__TCPIP::_choice119_func,3,"\xc6\xe3\xff\x0",false)
,_action171(171,this,(ActionFuncPointer)&AppC__TCPIP::_action171_func, "\xc6\xe3\xff\x0",false)
,_execi129(129,this,(LengthFuncPointer)&AppC__TCPIP::_execi129_func,0,1,"\x46\xe3\xff\x0",false)
,_write173(173,this,0,channel__AppC__fromTtoP,"\x46\xe3\xff\x0",true,1)
,_send203(203,this,event__AppC__send__AppC__send,0,"\x46\xe3\xff\x0",true)
,_action174(174,this,(ActionFuncPointer)&AppC__TCPIP::_action174_func, "\xc6\xe3\xff\x0",false)
,_choice130(130,this,(RangeFuncPointer)&AppC__TCPIP::_choice130_func,3,"\xc6\xe3\xff\x0",false)
,_execi136(136,this,(LengthFuncPointer)&AppC__TCPIP::_execi136_func,0,1,"\x46\xe3\xff\x0",false)
,_write180(180,this,0,channel__AppC__fromTtoP,"\x46\xe3\xff\x0",true,1)
,_send205(205,this,event__AppC__send__AppC__send,0,"\x46\xe3\xff\x0",true)
,_action181(181,this,(ActionFuncPointer)&AppC__TCPIP::_action181_func, "\xc6\xe3\xff\x0",false)
,_execi127(127,this,(LengthFuncPointer)&AppC__TCPIP::_execi127_func,0,1,"\x46\xe3\xff\x0",false)
,_write177(177,this,0,channel__AppC__fromTtoP,"\x46\xe3\xff\x0",true,1)
,_send204(204,this,event__AppC__send__AppC__send,0,"\x46\xe3\xff\x0",true)
,_action178(178,this,(ActionFuncPointer)&AppC__TCPIP::_action178_func, "\xc6\xe3\xff\x0",false)
,_execi124(124,this,(LengthFuncPointer)&AppC__TCPIP::_execi124_func,0,1,"\x46\xe3\xff\x0",false)
,_write208(208,this,0,channel__AppC__fromTtoP,"\x46\xe3\xff\x0",true,1)
,_action209(209,this,(ActionFuncPointer)&AppC__TCPIP::_action209_func, "\x46\xe3\xff\x0",false)
,_send212(212,this,event__AppC__send__AppC__send,0,"\x46\xe3\xff\x0",true)
,_action211(211,this,(ActionFuncPointer)&AppC__TCPIP::_action211_func, "\xc6\xe3\xff\x0",false)
,_choice123(123,this,(RangeFuncPointer)&AppC__TCPIP::_choice123_func,2,"\xc6\xe3\xff\x0",false)
,_choice128(128,this,(RangeFuncPointer)&AppC__TCPIP::_choice128_func,3,"\xc6\xe3\xff\x0",false)
,_choice133(133,this,(RangeFuncPointer)&AppC__TCPIP::_choice133_func,3,"\xc6\xe3\xff\x0",false)
,_execi134(134,this,(LengthFuncPointer)&AppC__TCPIP::_execi134_func,0,1,"\x46\xe3\xff\x0",false)
,_action196(196,this,(ActionFuncPointer)&AppC__TCPIP::_action196_func, "\x46\xe3\xff\x0",false)
,_write168(168,this,0,channel__AppC__fromTtoP,"\x46\xe3\xff\x0",true,1)
,_send200(200,this,event__AppC__send__AppC__send,0,"\x46\xe3\xff\x0",true)
,_action162(162,this,(ActionFuncPointer)&AppC__TCPIP::_action162_func, "\xc6\xe3\xff\x0",false)
,_request192(192,this,request__AppC__req_Timer,0,"\xc6\xe3\xff\x0",true)
,_write193(193,this,0,channel__AppC__temp,"\xc6\xe3\xff\x0",false,1)
,_execi131(131,this,(LengthFuncPointer)&AppC__TCPIP::_execi131_func,0,1,"\x46\xe3\xff\x0",false)
,_action197(197,this,(ActionFuncPointer)&AppC__TCPIP::_action197_func, "\x46\xe3\xff\x0",false)
,_write169(169,this,0,channel__AppC__fromTtoP,"\x46\xe3\xff\x0",true,1)
,_send199(199,this,event__AppC__send__AppC__send,0,"\x46\xe3\xff\x0",true)
,_action166(166,this,(ActionFuncPointer)&AppC__TCPIP::_action166_func, "\xc6\xe3\xff\x0",false)
,_request190(190,this,request__AppC__req_Timer,0,"\xc6\xe3\xff\x0",true)
,_write191(191,this,0,channel__AppC__temp,"\xc6\xe3\xff\x0",false,1)
,_action175(175,this,(ActionFuncPointer)&AppC__TCPIP::_action175_func, "\xc6\xe3\xff\x0",false)
,_choice125(125,this,(RangeFuncPointer)&AppC__TCPIP::_choice125_func,2,"\xc6\xe3\xff\x0",false)
,_choice139(139,this,(RangeFuncPointer)&AppC__TCPIP::_choice139_func,3,"\xc6\xe3\xff\x0",false)
,_read159(159,this,0,channel__AppC__fromAtoT,"\xc6\xe3\xff\x0",true,1)
,_execi135(135,this,(LengthFuncPointer)&AppC__TCPIP::_execi135_func,0,1,"\xc6\xe3\xff\x0",false)
,_action195(195,this,(ActionFuncPointer)&AppC__TCPIP::_action195_func, "\xc6\xe3\xff\x0",false)
,_write160(160,this,0,channel__AppC__fromTtoP,"\xc6\xe3\xff\x0",true,1)
,_send198(198,this,event__AppC__send__AppC__send,0,"\xc6\xe3\xff\x0",true)
,_request188(188,this,request__AppC__req_Timer,0,"\xc6\xe3\xff\x0",true)
,_write189(189,this,0,channel__AppC__temp,"\xc6\xe3\xff\x0",false,1)
,_execi132(132,this,(LengthFuncPointer)&AppC__TCPIP::_execi132_func,0,1,"\x46\xe3\xff\x0",false)
,_action194(194,this,(ActionFuncPointer)&AppC__TCPIP::_action194_func, "\x46\xe3\xff\x0",false)
,_write165(165,this,0,channel__AppC__fromTtoP,"\x46\xe3\xff\x0",true,1)
,_send201(201,this,event__AppC__send__AppC__send,0,"\x46\xe3\xff\x0",true)
,_action164(164,this,(ActionFuncPointer)&AppC__TCPIP::_action164_func, "\xc6\xe3\xff\x0",false)
,_request185(185,this,request__AppC__req_Timer,0,"\xc6\xe3\xff\x0",true)
,_write186(186,this,0,channel__AppC__temp,"\xc6\xe3\xff\x0",false,1)
,_choice140(140,this,(RangeFuncPointer)&AppC__TCPIP::_choice140_func,3,"\xc6\xe3\xff\x0",false)
,_send110(110,this,event__AppC__opened__AppC__opened,0,"\xc6\xe3\xff\x0",true)
,_action141(141,this,(ActionFuncPointer)&AppC__TCPIP::_action141_func, "\xc6\xe3\xff\x0",false)
,_choice137(137,this,(RangeFuncPointer)&AppC__TCPIP::_choice137_func,2,"\xc6\xe3\xff\x0",false)
,_select154(154,this,array(5,(TMLEventChannel*)event__AppC__timeOut__AppC__timeOut,(TMLEventChannel*)event__AppC__receive__AppC__receive,(TMLEventChannel*)event__AppC__close__AppC__close,(TMLEventChannel*)event__AppC__send_TCP__AppC__send_TCP,(TMLEventChannel*)event__AppC__open__AppC__open),5,"\xc6\xe3\xff\x0",false,array(5,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0,(ParamFuncPointer)0))
, _stop138(138,this)
,_choice138(138,this,(RangeFuncPointer)&AppC__TCPIP::_choice138_func,3,"\xc6\xe3\xff\x0",false)
,_action146(146,this,(ActionFuncPointer)&AppC__TCPIP::_action146_func, "\xc2\xe3\xff\x0",false)
,_lpChoice144(144,this,(RangeFuncPointer)&AppC__TCPIP::_lpChoice144_func,2,0, false)
,_action289(289,this,(ActionFuncPointer)&AppC__TCPIP::_action289_func, 0, false)

{
    _comment = new std::string[26];
    _comment[0]=std::string("Action i = i");
    _comment[1]=std::string("Action tcpctrl__state =4");
    _comment[2]=std::string("Action tcpctrl__state =6");
    _comment[3]=std::string("Action tcpctrl__state =0");
    _comment[4]=std::string("Action tcpctrl__state =8");
    _comment[5]=std::string("Action tcpctrl__state =0");
    _comment[6]=std::string("Action tcpctrl__state =2");
    _comment[7]=std::string("Action seqNum=seqNum+wind");
    _comment[8]=std::string("Action tcpctrl__state =2");
    _comment[9]=std::string("Action tcpctrl__state =1");
    _comment[10]=std::string("Action tcpctrl__state =3");
    _comment[11]=std::string("Action tcpctrl__state =7");
    _comment[12]=std::string("Action tcpctrl__state =9");
    _comment[13]=std::string("Action tcpctrl__state =0");
    _comment[14]=std::string("Action tcpctrl__state =8");
    _comment[15]=std::string("Action tcpctrl__state =5");
    _comment[16]=std::string("Action seqNum=seqNum+wind");
    _comment[17]=std::string("Action tcpctrl__state =10");
    _comment[18]=std::string("Action seqNum=seqNum+wind");
    _comment[19]=std::string("Action tcpctrl__state =0");
    _comment[20]=std::string("Action seqNum=seqNum+wind");
    _comment[21]=std::string("Action tcpctrl__state=3");
    _comment[22]=std::string("Action seqNum=seqNum+wind");
    _comment[23]=std::string("Action tcpctrl__state =1");
    _comment[24]=std::string("Action tcpctrl__state=0");
    _comment[25]=std::string("Action i=0");
    
    //generate task variable look-up table
    _varLookUpName["wind"]=&wind;
    _varLookUpID[18]=&wind;
    _varLookUpName["seqNum"]=&seqNum;
    _varLookUpID[19]=&seqNum;
    _varLookUpName["i"]=&i;
    _varLookUpID[20]=&i;
    _varLookUpName["j"]=&j;
    _varLookUpID[21]=&j;
    _varLookUpName["a"]=&a;
    _varLookUpID[22]=&a;
    _varLookUpName["b"]=&b;
    _varLookUpID[23]=&b;
    _varLookUpName["tcpctrl__a"]=&tcpctrl__a;
    _varLookUpID[24]=&tcpctrl__a;
    _varLookUpName["tcpctrl__state"]=&tcpctrl__state;
    _varLookUpID[25]=&tcpctrl__state;
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
    event__AppC__opened__AppC__opened->setBlockedWriteTask(this);
    event__AppC__receive_Application__AppC__receive_Application->setBlockedWriteTask(this);
    event__AppC__receive__AppC__receive->setBlockedReadTask(this);
    event__AppC__send_TCP__AppC__send_TCP->setBlockedReadTask(this);
    event__AppC__send__AppC__send->setBlockedWriteTask(this);
    event__AppC__stop__AppC__stop->setBlockedWriteTask(this);
    event__AppC__timeOut__AppC__timeOut->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    request__AppC__req_Timer->setBlockedWriteTask(this);
    
    //command chaining
    _lpIncAc144.setNextCommand(array(1,(TMLCommand*)&_lpChoice144));
    _wait157.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _send202.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _write183.setNextCommand(array(1,(TMLCommand*)&_send202));
    _execi126.setNextCommand(array(1,(TMLCommand*)&_write183));
    _read148.setNextCommand(array(1,(TMLCommand*)&_execi126));
    _write222.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _send221.setNextCommand(array(1,(TMLCommand*)&_write222));
    _write218.setNextCommand(array(1,(TMLCommand*)&_send221));
    _execi120.setNextCommand(array(1,(TMLCommand*)&_write218));
    _action224.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _action226.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _action231.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _action232.setNextCommand(array(1,(TMLCommand*)&_action231));
    _action229.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _choice111.setNextCommand(array(2,(TMLCommand*)&_action229,(TMLCommand*)&_lpIncAc144));
    _choice113.setNextCommand(array(3,(TMLCommand*)&_action226,(TMLCommand*)&_action232,(TMLCommand*)&_choice111));
    _choice114.setNextCommand(array(3,(TMLCommand*)&_action224,(TMLCommand*)&_lpIncAc144,(TMLCommand*)&_choice113));
    _choice115.setNextCommand(array(2,(TMLCommand*)&_execi120,(TMLCommand*)&_choice114));
    _read248.setNextCommand(array(1,(TMLCommand*)&_choice115));
    _send217.setNextCommand(array(1,(TMLCommand*)&_read248));
    _send220.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _write216.setNextCommand(array(1,(TMLCommand*)&_send220));
    _execi116.setNextCommand(array(1,(TMLCommand*)&_write216));
    _send247.setNextCommand(array(1,(TMLCommand*)&_execi116));
    _write215.setNextCommand(array(1,(TMLCommand*)&_send247));
    _execi117.setNextCommand(array(1,(TMLCommand*)&_write215));
    _choice118.setNextCommand(array(2,(TMLCommand*)&_execi117,(TMLCommand*)&_lpIncAc144));
    _action239.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _write236.setNextCommand(array(1,(TMLCommand*)&_action239));
    _request235.setNextCommand(array(1,(TMLCommand*)&_write236));
    _send238.setNextCommand(array(1,(TMLCommand*)&_request235));
    _write233.setNextCommand(array(1,(TMLCommand*)&_send238));
    _action237.setNextCommand(array(1,(TMLCommand*)&_write233));
    _execi121.setNextCommand(array(1,(TMLCommand*)&_action237));
    _action241.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _send243.setNextCommand(array(1,(TMLCommand*)&_action241));
    _write240.setNextCommand(array(1,(TMLCommand*)&_send243));
    _execi122.setNextCommand(array(1,(TMLCommand*)&_write240));
    _choice112.setNextCommand(array(3,(TMLCommand*)&_execi121,(TMLCommand*)&_execi122,(TMLCommand*)&_lpIncAc144));
    _choice119.setNextCommand(array(3,(TMLCommand*)&_send217,(TMLCommand*)&_choice118,(TMLCommand*)&_choice112));
    _action171.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _action174.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _send203.setNextCommand(array(1,(TMLCommand*)&_action174));
    _write173.setNextCommand(array(1,(TMLCommand*)&_send203));
    _execi129.setNextCommand(array(1,(TMLCommand*)&_write173));
    _choice130.setNextCommand(array(3,(TMLCommand*)&_action171,(TMLCommand*)&_execi129,(TMLCommand*)&_lpIncAc144));
    _action181.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _send205.setNextCommand(array(1,(TMLCommand*)&_action181));
    _write180.setNextCommand(array(1,(TMLCommand*)&_send205));
    _execi136.setNextCommand(array(1,(TMLCommand*)&_write180));
    _action178.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _send204.setNextCommand(array(1,(TMLCommand*)&_action178));
    _write177.setNextCommand(array(1,(TMLCommand*)&_send204));
    _execi127.setNextCommand(array(1,(TMLCommand*)&_write177));
    _action211.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _send212.setNextCommand(array(1,(TMLCommand*)&_action211));
    _action209.setNextCommand(array(1,(TMLCommand*)&_send212));
    _write208.setNextCommand(array(1,(TMLCommand*)&_action209));
    _execi124.setNextCommand(array(1,(TMLCommand*)&_write208));
    _choice123.setNextCommand(array(2,(TMLCommand*)&_execi124,(TMLCommand*)&_lpIncAc144));
    _choice128.setNextCommand(array(3,(TMLCommand*)&_execi136,(TMLCommand*)&_execi127,(TMLCommand*)&_choice123));
    _choice133.setNextCommand(array(3,(TMLCommand*)&_choice119,(TMLCommand*)&_choice130,(TMLCommand*)&_choice128));
    _execi109.setNextCommand(array(1,(TMLCommand*)&_choice133));
    _read143.setNextCommand(array(1,(TMLCommand*)&_execi109));
    _write193.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _request192.setNextCommand(array(1,(TMLCommand*)&_write193));
    _action162.setNextCommand(array(1,(TMLCommand*)&_request192));
    _send200.setNextCommand(array(1,(TMLCommand*)&_action162));
    _write168.setNextCommand(array(1,(TMLCommand*)&_send200));
    _action196.setNextCommand(array(1,(TMLCommand*)&_write168));
    _execi134.setNextCommand(array(1,(TMLCommand*)&_action196));
    _write191.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _request190.setNextCommand(array(1,(TMLCommand*)&_write191));
    _action166.setNextCommand(array(1,(TMLCommand*)&_request190));
    _send199.setNextCommand(array(1,(TMLCommand*)&_action166));
    _write169.setNextCommand(array(1,(TMLCommand*)&_send199));
    _action197.setNextCommand(array(1,(TMLCommand*)&_write169));
    _execi131.setNextCommand(array(1,(TMLCommand*)&_action197));
    _action175.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _choice125.setNextCommand(array(2,(TMLCommand*)&_action175,(TMLCommand*)&_lpIncAc144));
    _choice139.setNextCommand(array(3,(TMLCommand*)&_execi134,(TMLCommand*)&_execi131,(TMLCommand*)&_choice125));
    _write189.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _request188.setNextCommand(array(1,(TMLCommand*)&_write189));
    _send198.setNextCommand(array(1,(TMLCommand*)&_request188));
    _write160.setNextCommand(array(1,(TMLCommand*)&_send198));
    _action195.setNextCommand(array(1,(TMLCommand*)&_write160));
    _execi135.setNextCommand(array(1,(TMLCommand*)&_action195));
    _read159.setNextCommand(array(1,(TMLCommand*)&_execi135));
    _write186.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _request185.setNextCommand(array(1,(TMLCommand*)&_write186));
    _action164.setNextCommand(array(1,(TMLCommand*)&_request185));
    _send201.setNextCommand(array(1,(TMLCommand*)&_action164));
    _write165.setNextCommand(array(1,(TMLCommand*)&_send201));
    _action194.setNextCommand(array(1,(TMLCommand*)&_write165));
    _execi132.setNextCommand(array(1,(TMLCommand*)&_action194));
    _choice140.setNextCommand(array(3,(TMLCommand*)&_read159,(TMLCommand*)&_execi132,(TMLCommand*)&_lpIncAc144));
    _action141.setNextCommand(array(1,(TMLCommand*)&_lpIncAc144));
    _choice137.setNextCommand(array(2,(TMLCommand*)&_action141,(TMLCommand*)&_lpIncAc144));
    _send110.setNextCommand(array(1,(TMLCommand*)&_choice137));
    _select154.setNextCommand(array(5,(TMLCommand*)&_read148,(TMLCommand*)&_read143,(TMLCommand*)&_choice139,(TMLCommand*)&_choice140,(TMLCommand*)&_send110));
    _choice138.setNextCommand(array(3,(TMLCommand*)&_wait157,(TMLCommand*)&_select154,(TMLCommand*)&_stop138));
    _notified147.setNextCommand(array(1,(TMLCommand*)&_choice138));
    _action146.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _lpChoice144.setNextCommand(array(2,(TMLCommand*)&_notified147,(TMLCommand*)&_action146));
    _action289.setNextCommand(array(1,(TMLCommand*)&_lpChoice144));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action289));
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
    _channels[8] = event__AppC__opened__AppC__opened;
    _channels[9] = event__AppC__receive_Application__AppC__receive_Application;
    _channels[10] = event__AppC__receive__AppC__receive;
    _channels[11] = event__AppC__send_TCP__AppC__send_TCP;
    _channels[12] = event__AppC__send__AppC__send;
    _channels[13] = event__AppC__stop__AppC__stop;
    _channels[14] = event__AppC__timeOut__AppC__timeOut;
    _channels[15] = requestChannel;
    refreshStateHash("\xc2\xe3\xff\x0");
}

void AppC__TCPIP::_lpIncAc144_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    i = i;
}

TMLLength AppC__TCPIP::_execi126_func(){
    return (TMLLength)(b);
}

unsigned int AppC__TCPIP::_execi109_func(ParamType& oMin, ParamType& oMax){
     oMin=b;
    oMax=b;
    return myrand(oMin, oMax);
}

TMLLength AppC__TCPIP::_execi120_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action224_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    tcpctrl__state =4;
}

void AppC__TCPIP::_action226_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    tcpctrl__state =6;
}

void AppC__TCPIP::_action231_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,3));
    #endif
    tcpctrl__state =0;
}

void AppC__TCPIP::_action232_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,4));
    #endif
    tcpctrl__state =8;
}

void AppC__TCPIP::_action229_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,5));
    #endif
    tcpctrl__state =0;
}

unsigned int AppC__TCPIP::_choice111_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice113_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice114_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice115_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=1;
    return myrand(0, 1);
    
}

TMLLength AppC__TCPIP::_execi117_func(){
    return (TMLLength)(b);
}

TMLLength AppC__TCPIP::_execi116_func(){
    return (TMLLength)(b);
}

unsigned int AppC__TCPIP::_choice118_func(ParamType& oMin, ParamType& oMax){
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

TMLLength AppC__TCPIP::_execi121_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action239_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,6));
    #endif
    tcpctrl__state =2;
}

void AppC__TCPIP::_action237_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,7));
    #endif
    seqNum=seqNum+wind;
}

TMLLength AppC__TCPIP::_execi122_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action241_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,8));
    #endif
    tcpctrl__state =2;
}

unsigned int AppC__TCPIP::_choice112_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice119_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=2;
    return myrand(0, 2);
    
}

void AppC__TCPIP::_action171_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,9));
    #endif
    tcpctrl__state =1;
}

TMLLength AppC__TCPIP::_execi129_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action174_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,10));
    #endif
    tcpctrl__state =3;
}

unsigned int AppC__TCPIP::_choice130_func(ParamType& oMin, ParamType& oMax){
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

TMLLength AppC__TCPIP::_execi136_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action181_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,11));
    #endif
    tcpctrl__state =7;
}

TMLLength AppC__TCPIP::_execi127_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action178_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,12));
    #endif
    tcpctrl__state =9;
}

TMLLength AppC__TCPIP::_execi124_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action211_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,13));
    #endif
    tcpctrl__state =0;
}

void AppC__TCPIP::_action209_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,14));
    #endif
    tcpctrl__state =8;
}

unsigned int AppC__TCPIP::_choice123_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice128_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice133_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=2;
    return myrand(0, 2);
    
}

TMLLength AppC__TCPIP::_execi134_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action162_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,15));
    #endif
    tcpctrl__state =5;
}

void AppC__TCPIP::_action196_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,16));
    #endif
    seqNum=seqNum+wind;
}

TMLLength AppC__TCPIP::_execi131_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action166_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,17));
    #endif
    tcpctrl__state =10;
}

void AppC__TCPIP::_action197_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,18));
    #endif
    seqNum=seqNum+wind;
}

void AppC__TCPIP::_action175_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,19));
    #endif
    tcpctrl__state =0;
}

unsigned int AppC__TCPIP::_choice125_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice139_func(ParamType& oMin, ParamType& oMax){
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

TMLLength AppC__TCPIP::_execi135_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action195_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,20));
    #endif
    seqNum=seqNum+wind;
}

TMLLength AppC__TCPIP::_execi132_func(){
    return (TMLLength)(b);
}

void AppC__TCPIP::_action164_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,21));
    #endif
    tcpctrl__state=3;
}

void AppC__TCPIP::_action194_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,22));
    #endif
    seqNum=seqNum+wind;
}

unsigned int AppC__TCPIP::_choice140_func(ParamType& oMin, ParamType& oMax){
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

void AppC__TCPIP::_action141_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,23));
    #endif
    tcpctrl__state =1;
}

unsigned int AppC__TCPIP::_choice137_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__TCPIP::_choice138_func(ParamType& oMin, ParamType& oMax){
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

void AppC__TCPIP::_action146_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,24));
    #endif
    tcpctrl__state=0;
}

unsigned int AppC__TCPIP::_lpChoice144_func(ParamType& oMin, ParamType& oMax){
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

void AppC__TCPIP::_action289_func(){
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
    READ_STREAM(i_stream_var,seqNum);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable seqNum " << seqNum << std::endl;
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
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& AppC__TCPIP::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,wind);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable wind " << wind << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,seqNum);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable seqNum " << seqNum << std::endl;
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
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void AppC__TCPIP::reset(){
    TMLTask::reset();
    wind=64;
    seqNum=0;
    i=0;
    j=0;
    a=0;
    b=0;
    tcpctrl__a=0;
    tcpctrl__state=0;
}

HashValueType AppC__TCPIP::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(wind);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(seqNum);
            if ((_liveVarList[0] & 4)!=0) _stateHash.addValue(i);
            if ((_liveVarList[0] & 8)!=0) _stateHash.addValue(j);
            if ((_liveVarList[0] & 16)!=0) _stateHash.addValue(a);
            if ((_liveVarList[0] & 32)!=0) _stateHash.addValue(b);
            if ((_liveVarList[0] & 64)!=0) _stateHash.addValue(tcpctrl__a);
            if ((_liveVarList[0] & 128)!=0) _stateHash.addValue(tcpctrl__state);
            _channels[0]->setSignificance(this, ((_liveVarList[1] & 1)!=0));
            _channels[1]->setSignificance(this, ((_liveVarList[1] & 2)!=0));
             _channels[5]->setSignificance(this, ((_liveVarList[1] & 32)!=0));
             _channels[6]->setSignificance(this, ((_liveVarList[1] & 64)!=0));
             _channels[7]->setSignificance(this, ((_liveVarList[1] & 128)!=0));
             _channels[10]->setSignificance(this, ((_liveVarList[2] & 4)!=0));
             _channels[11]->setSignificance(this, ((_liveVarList[2] & 8)!=0));
             _channels[14]->setSignificance(this, ((_liveVarList[2] & 64)!=0));
             _channels[15]->setSignificance(this, ((_liveVarList[2] & 128)!=0));
        }
    }
    return _stateHash.getHash();
}

