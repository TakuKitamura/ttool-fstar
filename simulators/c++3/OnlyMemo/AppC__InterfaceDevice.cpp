#include <AppC__InterfaceDevice.h>

AppC__InterfaceDevice::AppC__InterfaceDevice(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__AppC__fromDtoSC__AppC__fromDtoSC0
, TMLChannel* channel__AppC__fromSCtoD
, TMLEventChannel* event__AppC__answerToReset__AppC__answerToReset
, TMLEventChannel* event__AppC__data_Ready_SC__AppC__data_Ready_SC
, TMLEventChannel* event__AppC__data_Ready__AppC__data_Ready
, TMLEventChannel* event__AppC__pTSConfirm__AppC__pTSConfirm
, TMLEventChannel* event__AppC__pTS__AppC__pTS
, TMLEventChannel* event__AppC__reset__AppC__reset
, TMLEventChannel* request__AppC__activation
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,resetType(0)
,x(0)
,i(0)
,nbOfComputedPackets(1)
,_request62(62,this,request__AppC__activation,0,"\xe0\xf\x0\x0",false)
,_send63(63,this,event__AppC__reset__AppC__reset,0,"\xe0\x7\x0\x0",false)
,_wait64(64,this,event__AppC__answerToReset__AppC__answerToReset,0,"\xa0\x7\x0\x0",false)
,_send65(65,this,event__AppC__pTS__AppC__pTS,0,"\xa0\x3\x0\x0",false)
,_wait66(66,this,event__AppC__pTSConfirm__AppC__pTSConfirm,0,"\xa0\x1\x0\x0",false)
,_lpIncAc75(75,this,(ActionFuncPointer)&AppC__InterfaceDevice::_lpIncAc75_func, 0, false)
,_write67(67,this,0,channel__AppC__fromDtoSC__AppC__fromDtoSC0,"\xa4\x1\x0\x0",true,1)
,_send68(68,this,event__AppC__data_Ready__AppC__data_Ready,0,"\xa4\x1\x0\x0",true)
,_notified70(70,this,event__AppC__data_Ready_SC__AppC__data_Ready_SC,&x,"x","\xa6\x1\x0\x0",false)
,_wait73(73,this,event__AppC__data_Ready_SC__AppC__data_Ready_SC,0,"\xa4\x1\x0\x0",true)
,_read72(72,this,0,channel__AppC__fromSCtoD,"\xa4\x1\x0\x0",true,1)
, _stop61(61,this)
,_choice61(61,this,(RangeFuncPointer)&AppC__InterfaceDevice::_choice61_func,3,"\xa4\x1\x0\x0",false)
,_choice60(60,this,(RangeFuncPointer)&AppC__InterfaceDevice::_choice60_func,2,"\xa4\x1\x0\x0",false)
,_stop76(76,this)
,_lpChoice75(75,this,(RangeFuncPointer)&AppC__InterfaceDevice::_lpChoice75_func,2,0, false)
,_action256(256,this,(ActionFuncPointer)&AppC__InterfaceDevice::_action256_func, 0, false)

{
    _comment = new std::string[2];
    _comment[0]=std::string("Action i = i +1");
    _comment[1]=std::string("Action i=0");
    
    //generate task variable look-up table
    _varLookUpName["resetType"]=&resetType;
    _varLookUpID[4]=&resetType;
    _varLookUpName["x"]=&x;
    _varLookUpID[5]=&x;
    _varLookUpName["i"]=&i;
    _varLookUpID[6]=&i;
    _varLookUpName["nbOfComputedPackets"]=&nbOfComputedPackets;
    _varLookUpID[7]=&nbOfComputedPackets;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__AppC__fromDtoSC__AppC__fromDtoSC0->setBlockedWriteTask(this);
    channel__AppC__fromSCtoD->setBlockedReadTask(this);
    event__AppC__answerToReset__AppC__answerToReset->setBlockedReadTask(this);
    event__AppC__data_Ready_SC__AppC__data_Ready_SC->setBlockedReadTask(this);
    event__AppC__data_Ready__AppC__data_Ready->setBlockedWriteTask(this);
    event__AppC__pTSConfirm__AppC__pTSConfirm->setBlockedReadTask(this);
    event__AppC__pTS__AppC__pTS->setBlockedWriteTask(this);
    event__AppC__reset__AppC__reset->setBlockedWriteTask(this);
    request__AppC__activation->setBlockedWriteTask(this);
    
    //command chaining
    _lpIncAc75.setNextCommand(array(1,(TMLCommand*)&_lpChoice75));
    _send68.setNextCommand(array(1,(TMLCommand*)&_lpIncAc75));
    _write67.setNextCommand(array(1,(TMLCommand*)&_send68));
    _read72.setNextCommand(array(1,(TMLCommand*)&_lpIncAc75));
    _wait73.setNextCommand(array(1,(TMLCommand*)&_read72));
    _choice61.setNextCommand(array(3,(TMLCommand*)&_lpIncAc75,(TMLCommand*)&_wait73,(TMLCommand*)&_stop61));
    _notified70.setNextCommand(array(1,(TMLCommand*)&_choice61));
    _choice60.setNextCommand(array(2,(TMLCommand*)&_write67,(TMLCommand*)&_notified70));
    _lpChoice75.setNextCommand(array(2,(TMLCommand*)&_choice60,(TMLCommand*)&_stop76));
    _action256.setNextCommand(array(1,(TMLCommand*)&_lpChoice75));
    _wait66.setNextCommand(array(1,(TMLCommand*)&_action256));
    _send65.setNextCommand(array(1,(TMLCommand*)&_wait66));
    _wait64.setNextCommand(array(1,(TMLCommand*)&_send65));
    _send63.setNextCommand(array(1,(TMLCommand*)&_wait64));
    _request62.setNextCommand(array(1,(TMLCommand*)&_send63));
    _currCommand=&_request62;
    _firstCommand=&_request62;
    
    _channels[0] = channel__AppC__fromDtoSC__AppC__fromDtoSC0;
    _channels[1] = channel__AppC__fromSCtoD;
    _channels[2] = event__AppC__answerToReset__AppC__answerToReset;
    _channels[3] = event__AppC__data_Ready_SC__AppC__data_Ready_SC;
    _channels[4] = event__AppC__data_Ready__AppC__data_Ready;
    _channels[5] = event__AppC__pTSConfirm__AppC__pTSConfirm;
    _channels[6] = event__AppC__pTS__AppC__pTS;
    _channels[7] = event__AppC__reset__AppC__reset;
    refreshStateHash("\xe0\xf\x0\x0");
}

void AppC__InterfaceDevice::_lpIncAc75_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    i = i +1;
}

unsigned int AppC__InterfaceDevice::_choice61_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( x==0 ){
        oC++;
        oMax += 1;
        
    }
    if ( x>0 ){
        oC++;
        oMax += 2;
        
    }
    if (oMax==0){
         oMax=4;
        return 2;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

unsigned int AppC__InterfaceDevice::_choice60_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=1;
    return myrand(0, 1);
    
}

unsigned int AppC__InterfaceDevice::_lpChoice75_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( i<nbOfComputedPackets ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

void AppC__InterfaceDevice::_action256_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    i=0;
}

std::istream& AppC__InterfaceDevice::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,resetType);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable resetType " << resetType << std::endl;
    #endif
    READ_STREAM(i_stream_var,x);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable x " << x << std::endl;
    #endif
    READ_STREAM(i_stream_var,i);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable i " << i << std::endl;
    #endif
    READ_STREAM(i_stream_var,nbOfComputedPackets);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable nbOfComputedPackets " << nbOfComputedPackets << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& AppC__InterfaceDevice::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,resetType);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable resetType " << resetType << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,x);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable x " << x << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,i);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable i " << i << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,nbOfComputedPackets);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable nbOfComputedPackets " << nbOfComputedPackets << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void AppC__InterfaceDevice::reset(){
    TMLTask::reset();
    resetType=0;
    x=0;
    i=0;
    nbOfComputedPackets=1;
}

HashValueType AppC__InterfaceDevice::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(resetType);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(x);
            if ((_liveVarList[0] & 4)!=0) _stateHash.addValue(i);
            if ((_liveVarList[0] & 8)!=0) _stateHash.addValue(nbOfComputedPackets);
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 32)!=0));
             _channels[2]->setSignificance(this, ((_liveVarList[0] & 64)!=0));
             _channels[3]->setSignificance(this, ((_liveVarList[0] & 128)!=0));
             _channels[5]->setSignificance(this, ((_liveVarList[1] & 2)!=0));
        }
    }
    return _stateHash.getHash();
}

