#include <AppC__SmartCard.h>

AppC__SmartCard::AppC__SmartCard(ID iID, Priority iPriority, std::string iName, FPGA** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__AppC__fromDtoSC
, TMLChannel* channel__AppC__fromPtoT
, TMLChannel* channel__AppC__fromSCtoD
, TMLChannel* channel__AppC__fromTtoP
, TMLEventChannel* event__AppC__answerToReset__AppC__answerToReset
, TMLEventChannel* event__AppC__connectionOpened__AppC__connectionOpened
, TMLEventChannel* event__AppC__data_Ready_SC__AppC__data_Ready_SC
, TMLEventChannel* event__AppC__data_Ready__AppC__data_Ready
, TMLEventChannel* event__AppC__end__AppC__end
, TMLEventChannel* event__AppC__pTSConfirm__AppC__pTSConfirm
, TMLEventChannel* event__AppC__pTS__AppC__pTS
, TMLEventChannel* event__AppC__receive__AppC__receive
, TMLEventChannel* event__AppC__reset__AppC__reset
, TMLEventChannel* event__AppC__send__AppC__send
, TMLEventChannel* request__AppC__start_Application
, TMLEventChannel* request__AppC__start_TCP_IP
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,resetType(0)
,a(0)
,b(0)
,i(0)
,j(0)
,x(0)
,tcpctrl__a(0)
,tcpctrl__state(0)
,t(0)
,_waitOnRequest(249,this,requestChannel,0,"\x0\xf2\xff\x0",false)
,_wait253(253,this,event__AppC__reset__AppC__reset,0,"\x0\xf2\xff\x0",false)
,_send254(254,this,event__AppC__answerToReset__AppC__answerToReset,0,"\x0\xf2\xff\x0",false)
,_wait255(255,this,event__AppC__pTS__AppC__pTS,0,"\x0\xf2\xff\x0",false)
,_send256(256,this,event__AppC__pTSConfirm__AppC__pTSConfirm,0,"\x0\xf2\xff\x0",false)
,_request257(257,this,request__AppC__start_TCP_IP,0,"\x0\xf2\xff\x0",false)
,_request258(258,this,request__AppC__start_Application,0,"\x0\xf2\xff\x0",false)
,_wait252(252,this,event__AppC__connectionOpened__AppC__connectionOpened,0,"\x0\xf2\xff\x0",false)
,_lpIncAc269(269,this,(ActionFuncPointer)&AppC__SmartCard::_lpIncAc269_func, 0, false)
,_read262(262,this,0,channel__AppC__fromTtoP,"\x10\xf2\xff\x0",true,1)
,_send260(260,this,event__AppC__data_Ready_SC__AppC__data_Ready_SC,0,"\x10\xf2\xff\x0",true)
,_write264(264,this,0,channel__AppC__fromSCtoD,"\x10\xf2\xff\x0",true,1)
,_read266(266,this,0,channel__AppC__fromDtoSC,"\x10\xf2\xff\x0",true,1)
,_send265(265,this,event__AppC__receive__AppC__receive,0,"\x10\xf2\xff\x0",true)
,_write267(267,this,0,channel__AppC__fromPtoT,"\x10\xf2\xff\x0",true,1)
,_select271(271,this,array(3,(TMLEventChannel*)event__AppC__send__AppC__send,(TMLEventChannel*)event__AppC__data_Ready__AppC__data_Ready,(TMLEventChannel*)event__AppC__end__AppC__end),3,"\x10\xf2\xff\x0",false,array(3,(ParamFuncPointer)0,(ParamFuncPointer)&AppC__SmartCard::_select271_func_1
,(ParamFuncPointer)0))
,_lpChoice269(269,this,(RangeFuncPointer)&AppC__SmartCard::_lpChoice269_func,2,0, false)
,_action292(292,this,(ActionFuncPointer)&AppC__SmartCard::_action292_func, 0, false)

{
    _comment = new std::string[2];
    _comment[0]=std::string("Action j = j");
    _comment[1]=std::string("Action j=0");
    
    //generate task variable look-up table
    _varLookUpName["resetType"]=&resetType;
    _varLookUpID[28]=&resetType;
    _varLookUpName["a"]=&a;
    _varLookUpID[29]=&a;
    _varLookUpName["b"]=&b;
    _varLookUpID[30]=&b;
    _varLookUpName["i"]=&i;
    _varLookUpID[31]=&i;
    _varLookUpName["j"]=&j;
    _varLookUpID[32]=&j;
    _varLookUpName["x"]=&x;
    _varLookUpID[33]=&x;
    _varLookUpName["tcpctrl__a"]=&tcpctrl__a;
    _varLookUpID[34]=&tcpctrl__a;
    _varLookUpName["tcpctrl__state"]=&tcpctrl__state;
    _varLookUpID[35]=&tcpctrl__state;
    _varLookUpName["t"]=&t;
    _varLookUpID[36]=&t;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__AppC__fromDtoSC->setBlockedReadTask(this);
    channel__AppC__fromPtoT->setBlockedWriteTask(this);
    channel__AppC__fromSCtoD->setBlockedWriteTask(this);
    channel__AppC__fromTtoP->setBlockedReadTask(this);
    event__AppC__answerToReset__AppC__answerToReset->setBlockedWriteTask(this);
    event__AppC__connectionOpened__AppC__connectionOpened->setBlockedReadTask(this);
    event__AppC__data_Ready_SC__AppC__data_Ready_SC->setBlockedWriteTask(this);
    event__AppC__data_Ready__AppC__data_Ready->setBlockedReadTask(this);
    event__AppC__end__AppC__end->setBlockedReadTask(this);
    event__AppC__pTSConfirm__AppC__pTSConfirm->setBlockedWriteTask(this);
    event__AppC__pTS__AppC__pTS->setBlockedReadTask(this);
    event__AppC__receive__AppC__receive->setBlockedWriteTask(this);
    event__AppC__reset__AppC__reset->setBlockedReadTask(this);
    event__AppC__send__AppC__send->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    request__AppC__start_Application->setBlockedWriteTask(this);
    request__AppC__start_TCP_IP->setBlockedWriteTask(this);
    
    //command chaining
    _lpIncAc269.setNextCommand(array(1,(TMLCommand*)&_lpChoice269));
    _write264.setNextCommand(array(1,(TMLCommand*)&_lpIncAc269));
    _send260.setNextCommand(array(1,(TMLCommand*)&_write264));
    _read262.setNextCommand(array(1,(TMLCommand*)&_send260));
    _write267.setNextCommand(array(1,(TMLCommand*)&_lpIncAc269));
    _send265.setNextCommand(array(1,(TMLCommand*)&_write267));
    _read266.setNextCommand(array(1,(TMLCommand*)&_send265));
    _select271.setNextCommand(array(3,(TMLCommand*)&_read262,(TMLCommand*)&_read266,(TMLCommand*)&_lpIncAc269));
    _lpChoice269.setNextCommand(array(2,(TMLCommand*)&_select271,(TMLCommand*)&_waitOnRequest));
    _action292.setNextCommand(array(1,(TMLCommand*)&_lpChoice269));
    _wait252.setNextCommand(array(1,(TMLCommand*)&_action292));
    _request258.setNextCommand(array(1,(TMLCommand*)&_wait252));
    _request257.setNextCommand(array(1,(TMLCommand*)&_request258));
    _send256.setNextCommand(array(1,(TMLCommand*)&_request257));
    _wait255.setNextCommand(array(1,(TMLCommand*)&_send256));
    _send254.setNextCommand(array(1,(TMLCommand*)&_wait255));
    _wait253.setNextCommand(array(1,(TMLCommand*)&_send254));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_wait253));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__AppC__fromDtoSC;
    _channels[1] = channel__AppC__fromPtoT;
    _channels[2] = channel__AppC__fromSCtoD;
    _channels[3] = channel__AppC__fromTtoP;
    _channels[4] = event__AppC__answerToReset__AppC__answerToReset;
    _channels[5] = event__AppC__connectionOpened__AppC__connectionOpened;
    _channels[6] = event__AppC__data_Ready_SC__AppC__data_Ready_SC;
    _channels[7] = event__AppC__data_Ready__AppC__data_Ready;
    _channels[8] = event__AppC__end__AppC__end;
    _channels[9] = event__AppC__pTSConfirm__AppC__pTSConfirm;
    _channels[10] = event__AppC__pTS__AppC__pTS;
    _channels[11] = event__AppC__receive__AppC__receive;
    _channels[12] = event__AppC__reset__AppC__reset;
    _channels[13] = event__AppC__send__AppC__send;
    _channels[14] = requestChannel;
    refreshStateHash("\x0\xf2\xff\x0");
}

void AppC__SmartCard::_lpIncAc269_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    j = j;
}

Parameter* AppC__SmartCard::_select271_func_1(Parameter* ioParam){
    std::ostringstream ss;
    
    ioParam->getP(&t, &b);
    ss << "(" << t << "(t)" << "," << b << "(b)" << ")";
    if(_select271.getCurrTransaction() != NULL) _select271.getCurrTransaction()->lastParams = ss.str();
    
    return 0;
    
    
}unsigned int AppC__SmartCard::_lpChoice269_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( x==0 ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

void AppC__SmartCard::_action292_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    j=0;
}

std::istream& AppC__SmartCard::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,resetType);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable resetType " << resetType << std::endl;
    #endif
    READ_STREAM(i_stream_var,a);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable a " << a << std::endl;
    #endif
    READ_STREAM(i_stream_var,b);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable b " << b << std::endl;
    #endif
    READ_STREAM(i_stream_var,i);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable i " << i << std::endl;
    #endif
    READ_STREAM(i_stream_var,j);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable j " << j << std::endl;
    #endif
    READ_STREAM(i_stream_var,x);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable x " << x << std::endl;
    #endif
    READ_STREAM(i_stream_var,tcpctrl__a);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable tcpctrl__a " << tcpctrl__a << std::endl;
    #endif
    READ_STREAM(i_stream_var,tcpctrl__state);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable tcpctrl__state " << tcpctrl__state << std::endl;
    #endif
    READ_STREAM(i_stream_var,t);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable t " << t << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& AppC__SmartCard::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,resetType);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable resetType " << resetType << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,a);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable a " << a << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,b);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable b " << b << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,i);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable i " << i << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,j);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable j " << j << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,x);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable x " << x << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,tcpctrl__a);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable tcpctrl__a " << tcpctrl__a << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,tcpctrl__state);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable tcpctrl__state " << tcpctrl__state << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,t);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable t " << t << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void AppC__SmartCard::reset(){
    TMLTask::reset();
    resetType=0;
    a=0;
    b=0;
    i=0;
    j=0;
    x=0;
    tcpctrl__a=0;
    tcpctrl__state=0;
    t=0;
}

HashValueType AppC__SmartCard::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(resetType);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(a);
            if ((_liveVarList[0] & 4)!=0) _stateHash.addValue(b);
            if ((_liveVarList[0] & 8)!=0) _stateHash.addValue(i);
            if ((_liveVarList[0] & 16)!=0) _stateHash.addValue(j);
            if ((_liveVarList[0] & 32)!=0) _stateHash.addValue(x);
            if ((_liveVarList[0] & 64)!=0) _stateHash.addValue(tcpctrl__a);
            if ((_liveVarList[0] & 128)!=0) _stateHash.addValue(tcpctrl__state);
            if ((_liveVarList[1] & 1)!=0) _stateHash.addValue(t);
            _channels[0]->setSignificance(this, ((_liveVarList[1] & 2)!=0));
            _channels[3]->setSignificance(this, ((_liveVarList[1] & 16)!=0));
             _channels[5]->setSignificance(this, ((_liveVarList[1] & 64)!=0));
             _channels[7]->setSignificance(this, ((_liveVarList[2] & 1)!=0));
             _channels[8]->setSignificance(this, ((_liveVarList[2] & 2)!=0));
             _channels[10]->setSignificance(this, ((_liveVarList[2] & 8)!=0));
             _channels[12]->setSignificance(this, ((_liveVarList[2] & 32)!=0));
             _channels[13]->setSignificance(this, ((_liveVarList[2] & 64)!=0));
             _channels[14]->setSignificance(this, ((_liveVarList[2] & 128)!=0));
        }
    }
    return _stateHash.getHash();
}

