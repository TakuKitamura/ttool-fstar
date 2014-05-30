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
,_waitOnRequest(237,this,requestChannel,0,"\x0\xf9\x1f\x0",false)
,_wait238(238,this,event__AppC__reset__AppC__reset,0,"\x0\xf9\x1f\x0",false)
,_send239(239,this,event__AppC__answerToReset__AppC__answerToReset,0,"\x0\xf9\x1f\x0",false)
,_wait240(240,this,event__AppC__pTS__AppC__pTS,0,"\x0\xf9\x1f\x0",false)
,_send241(241,this,event__AppC__pTSConfirm__AppC__pTSConfirm,0,"\x0\xf9\x1f\x0",false)
,_request242(242,this,request__AppC__start_TCP_IP,0,"\x0\xf9\x1f\x0",false)
,_request243(243,this,request__AppC__start_Application,0,"\x0\xf9\x1f\x0",false)
,_lpIncAc254(254,this,(ActionFuncPointer)&AppC__SmartCard::_lpIncAc254_func, 0, false)
,_read247(247,this,0,channel__AppC__fromTtoP,"\x10\xf9\x1f\x0",true,1)
,_send245(245,this,event__AppC__data_Ready_SC__AppC__data_Ready_SC,0,"\x10\xf9\x1f\x0",true)
,_write249(249,this,0,channel__AppC__fromSCtoD,"\x10\xf9\x1f\x0",true,1)
,_read251(251,this,0,channel__AppC__fromDtoSC__AppC__fromDtoSC0,"\x10\xf9\x1f\x0",true,1)
,_send250(250,this,event__AppC__receive__AppC__receive,0,"\x10\xf9\x1f\x0",true)
,_write252(252,this,0,channel__AppC__fromPtoT,"\x10\xf9\x1f\x0",true,1)
,_select256(256,this,array(2,(TMLEventChannel*)event__AppC__send__AppC__send,(TMLEventChannel*)event__AppC__data_Ready__AppC__data_Ready),2,"\x10\xf9\x1f\x0",false,array(2,(ParamFuncPointer)0,(ParamFuncPointer)0))
,_lpChoice254(254,this,(RangeFuncPointer)&AppC__SmartCard::_lpChoice254_func,2,0, false)
,_action265(265,this,(ActionFuncPointer)&AppC__SmartCard::_action265_func, 0, false)

{
    _comment = new std::string[2];
    _comment[0]=std::string("Action j = j");
    _comment[1]=std::string("Action j=0");
    
    //generate task variable look-up table
    _varLookUpName["resetType"]=&resetType;
    _varLookUpID[29]=&resetType;
    _varLookUpName["a"]=&a;
    _varLookUpID[30]=&a;
    _varLookUpName["b"]=&b;
    _varLookUpID[31]=&b;
    _varLookUpName["i"]=&i;
    _varLookUpID[32]=&i;
    _varLookUpName["j"]=&j;
    _varLookUpID[33]=&j;
    _varLookUpName["x"]=&x;
    _varLookUpID[34]=&x;
    _varLookUpName["tcpctrl__a"]=&tcpctrl__a;
    _varLookUpID[35]=&tcpctrl__a;
    _varLookUpName["tcpctrl__state"]=&tcpctrl__state;
    _varLookUpID[36]=&tcpctrl__state;
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
    _lpIncAc254.setNextCommand(array(1,(TMLCommand*)&_lpChoice254));
    _write249.setNextCommand(array(1,(TMLCommand*)&_lpIncAc254));
    _send245.setNextCommand(array(1,(TMLCommand*)&_write249));
    _read247.setNextCommand(array(1,(TMLCommand*)&_send245));
    _write252.setNextCommand(array(1,(TMLCommand*)&_lpIncAc254));
    _send250.setNextCommand(array(1,(TMLCommand*)&_write252));
    _read251.setNextCommand(array(1,(TMLCommand*)&_send250));
    _select256.setNextCommand(array(2,(TMLCommand*)&_read247,(TMLCommand*)&_read251));
    _lpChoice254.setNextCommand(array(2,(TMLCommand*)&_select256,(TMLCommand*)&_waitOnRequest));
    _action265.setNextCommand(array(1,(TMLCommand*)&_lpChoice254));
    _request243.setNextCommand(array(1,(TMLCommand*)&_action265));
    _request242.setNextCommand(array(1,(TMLCommand*)&_request243));
    _send241.setNextCommand(array(1,(TMLCommand*)&_request242));
    _wait240.setNextCommand(array(1,(TMLCommand*)&_send241));
    _send239.setNextCommand(array(1,(TMLCommand*)&_wait240));
    _wait238.setNextCommand(array(1,(TMLCommand*)&_send239));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_wait238));
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

void AppC__SmartCard::_lpIncAc254_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    j = j;
}

unsigned int AppC__SmartCard::_lpChoice254_func(ParamType& oMin, ParamType& oMax){
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

void AppC__SmartCard::_action265_func(){
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

