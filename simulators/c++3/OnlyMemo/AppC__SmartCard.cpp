#include <AppC__SmartCard.h>

AppC__SmartCard::AppC__SmartCard(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__AppC__fromDtoSC__AppC__fromDtoSC0
, TMLChannel* channel__AppC__fromPtoT
, TMLChannel* channel__AppC__fromSCtoD
, TMLChannel* channel__AppC__fromTtoP
, TMLEventChannel* event__AppC__answerToReset__AppC__answerToReset
, TMLEventChannel* event__AppC__data_Ready_SC__AppC__data_Ready_SC
, TMLEventChannel* event__AppC__data_Ready__AppC__data_Ready
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
,_waitOnRequest(77,this,requestChannel,0,"\x0\xf9\x1f\x0",false)
,_wait78(78,this,event__AppC__reset__AppC__reset,0,"\x0\xf9\x1f\x0",false)
,_send79(79,this,event__AppC__answerToReset__AppC__answerToReset,0,"\x0\xf9\x1f\x0",false)
,_wait80(80,this,event__AppC__pTS__AppC__pTS,0,"\x0\xf9\x1f\x0",false)
,_send81(81,this,event__AppC__pTSConfirm__AppC__pTSConfirm,0,"\x0\xf9\x1f\x0",false)
,_request82(82,this,request__AppC__start_TCP_IP,0,"\x0\xf9\x1f\x0",false)
,_request83(83,this,request__AppC__start_Application,0,"\x0\xf9\x1f\x0",false)
,_lpIncAc94(94,this,(ActionFuncPointer)&AppC__SmartCard::_lpIncAc94_func, 0, false)
,_read87(87,this,0,channel__AppC__fromTtoP,"\x10\xf9\x1f\x0",true,1)
,_send85(85,this,event__AppC__data_Ready_SC__AppC__data_Ready_SC,0,"\x10\xf9\x1f\x0",true)
,_write89(89,this,0,channel__AppC__fromSCtoD,"\x10\xf9\x1f\x0",true,1)
,_read91(91,this,0,channel__AppC__fromDtoSC__AppC__fromDtoSC0,"\x10\xf9\x1f\x0",true,1)
,_send90(90,this,event__AppC__receive__AppC__receive,0,"\x10\xf9\x1f\x0",true)
,_write92(92,this,0,channel__AppC__fromPtoT,"\x10\xf9\x1f\x0",true,1)
,_select96(96,this,array(2,(TMLEventChannel*)event__AppC__send__AppC__send,(TMLEventChannel*)event__AppC__data_Ready__AppC__data_Ready),2,"\x10\xf9\x1f\x0",false,array(2,(ParamFuncPointer)0,(ParamFuncPointer)0))
,_lpChoice94(94,this,(RangeFuncPointer)&AppC__SmartCard::_lpChoice94_func,2,0, false)
,_action259(259,this,(ActionFuncPointer)&AppC__SmartCard::_action259_func, 0, false)

{
    _comment = new std::string[2];
    _comment[0]=std::string("Action j = j");
    _comment[1]=std::string("Action j=0");
    
    //generate task variable look-up table
    _varLookUpName["resetType"]=&resetType;
    _varLookUpID[10]=&resetType;
    _varLookUpName["a"]=&a;
    _varLookUpID[11]=&a;
    _varLookUpName["b"]=&b;
    _varLookUpID[12]=&b;
    _varLookUpName["i"]=&i;
    _varLookUpID[13]=&i;
    _varLookUpName["j"]=&j;
    _varLookUpID[14]=&j;
    _varLookUpName["x"]=&x;
    _varLookUpID[15]=&x;
    _varLookUpName["tcpctrl__a"]=&tcpctrl__a;
    _varLookUpID[16]=&tcpctrl__a;
    _varLookUpName["tcpctrl__state"]=&tcpctrl__state;
    _varLookUpID[17]=&tcpctrl__state;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__AppC__fromDtoSC__AppC__fromDtoSC0->setBlockedReadTask(this);
    channel__AppC__fromPtoT->setBlockedWriteTask(this);
    channel__AppC__fromSCtoD->setBlockedWriteTask(this);
    channel__AppC__fromTtoP->setBlockedReadTask(this);
    event__AppC__answerToReset__AppC__answerToReset->setBlockedWriteTask(this);
    event__AppC__data_Ready_SC__AppC__data_Ready_SC->setBlockedWriteTask(this);
    event__AppC__data_Ready__AppC__data_Ready->setBlockedReadTask(this);
    event__AppC__pTSConfirm__AppC__pTSConfirm->setBlockedWriteTask(this);
    event__AppC__pTS__AppC__pTS->setBlockedReadTask(this);
    event__AppC__receive__AppC__receive->setBlockedWriteTask(this);
    event__AppC__reset__AppC__reset->setBlockedReadTask(this);
    event__AppC__send__AppC__send->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    request__AppC__start_Application->setBlockedWriteTask(this);
    request__AppC__start_TCP_IP->setBlockedWriteTask(this);
    
    //command chaining
    _lpIncAc94.setNextCommand(array(1,(TMLCommand*)&_lpChoice94));
    _write89.setNextCommand(array(1,(TMLCommand*)&_lpIncAc94));
    _send85.setNextCommand(array(1,(TMLCommand*)&_write89));
    _read87.setNextCommand(array(1,(TMLCommand*)&_send85));
    _write92.setNextCommand(array(1,(TMLCommand*)&_lpIncAc94));
    _send90.setNextCommand(array(1,(TMLCommand*)&_write92));
    _read91.setNextCommand(array(1,(TMLCommand*)&_send90));
    _select96.setNextCommand(array(2,(TMLCommand*)&_read87,(TMLCommand*)&_read91));
    _lpChoice94.setNextCommand(array(2,(TMLCommand*)&_select96,(TMLCommand*)&_waitOnRequest));
    _action259.setNextCommand(array(1,(TMLCommand*)&_lpChoice94));
    _request83.setNextCommand(array(1,(TMLCommand*)&_action259));
    _request82.setNextCommand(array(1,(TMLCommand*)&_request83));
    _send81.setNextCommand(array(1,(TMLCommand*)&_request82));
    _wait80.setNextCommand(array(1,(TMLCommand*)&_send81));
    _send79.setNextCommand(array(1,(TMLCommand*)&_wait80));
    _wait78.setNextCommand(array(1,(TMLCommand*)&_send79));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_wait78));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__AppC__fromDtoSC__AppC__fromDtoSC0;
    _channels[1] = channel__AppC__fromPtoT;
    _channels[2] = channel__AppC__fromSCtoD;
    _channels[3] = channel__AppC__fromTtoP;
    _channels[4] = event__AppC__answerToReset__AppC__answerToReset;
    _channels[5] = event__AppC__data_Ready_SC__AppC__data_Ready_SC;
    _channels[6] = event__AppC__data_Ready__AppC__data_Ready;
    _channels[7] = event__AppC__pTSConfirm__AppC__pTSConfirm;
    _channels[8] = event__AppC__pTS__AppC__pTS;
    _channels[9] = event__AppC__receive__AppC__receive;
    _channels[10] = event__AppC__reset__AppC__reset;
    _channels[11] = event__AppC__send__AppC__send;
    _channels[12] = requestChannel;
    refreshStateHash("\x0\xf9\x1f\x0");
}

void AppC__SmartCard::_lpIncAc94_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    j = j;
}

unsigned int AppC__SmartCard::_lpChoice94_func(ParamType& oMin, ParamType& oMax){
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

void AppC__SmartCard::_action259_func(){
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
            _channels[0]->setSignificance(this, ((_liveVarList[1] & 1)!=0));
            _channels[3]->setSignificance(this, ((_liveVarList[1] & 8)!=0));
             _channels[6]->setSignificance(this, ((_liveVarList[1] & 64)!=0));
             _channels[8]->setSignificance(this, ((_liveVarList[2] & 1)!=0));
             _channels[10]->setSignificance(this, ((_liveVarList[2] & 4)!=0));
             _channels[11]->setSignificance(this, ((_liveVarList[2] & 8)!=0));
             _channels[12]->setSignificance(this, ((_liveVarList[2] & 16)!=0));
        }
    }
    return _stateHash.getHash();
}

