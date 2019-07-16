#include <AppC__InterfaceDevice.h>

AppC__InterfaceDevice::AppC__InterfaceDevice(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__AppC__fromDtoSC
, TMLChannel* channel__AppC__fromSCtoD
, TMLEventChannel* event__AppC__answerToReset__AppC__answerToReset
, TMLEventChannel* event__AppC__data_Ready_SC__AppC__data_Ready_SC
, TMLEventChannel* event__AppC__data_Ready__AppC__data_Ready
, TMLEventChannel* event__AppC__end__AppC__end
, TMLEventChannel* event__AppC__pTSConfirm__AppC__pTSConfirm
, TMLEventChannel* event__AppC__pTS__AppC__pTS
, TMLEventChannel* event__AppC__reset__AppC__reset
, TMLEventChannel* request__AppC__activation
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,resetType(0)
,x(0)
,i(0)
,nbOfComputedPackets(1)
,b(0)
,_request86(86,this,request__AppC__activation,0,"\xc2\x3f\x0\x0",false)
,_send87(87,this,event__AppC__reset__AppC__reset,0,"\xc2\x1f\x0\x0",false)
,_wait88(88,this,event__AppC__answerToReset__AppC__answerToReset,0,"\x42\x1f\x0\x0",false)
,_send89(89,this,event__AppC__pTS__AppC__pTS,0,"\x42\xf\x0\x0",false)
,_wait90(90,this,event__AppC__pTSConfirm__AppC__pTSConfirm,0,"\x42\x7\x0\x0",false)
,_lpIncAc99(99,this,(ActionFuncPointer)&AppC__InterfaceDevice::_lpIncAc99_func, 0, false)
,_write91(91,this,0,channel__AppC__fromDtoSC,"\x46\x7\x0\x0",true,1)
,_send92(92,this,event__AppC__data_Ready__AppC__data_Ready,(ParamFuncPointer)&AppC__InterfaceDevice::_send92_func,"\x46\x7\x0\x0",true)
,_notified94(94,this,event__AppC__data_Ready_SC__AppC__data_Ready_SC,&x,"x","\x46\x7\x0\x0",false)
,_wait97(97,this,event__AppC__data_Ready_SC__AppC__data_Ready_SC,0,"\x46\x7\x0\x0",true)
,_read96(96,this,0,channel__AppC__fromSCtoD,"\x46\x7\x0\x0",true,1)
, _stop85(85,this)
,_choice85(85,this,(RangeFuncPointer)&AppC__InterfaceDevice::_choice85_func,3,"\x46\x7\x0\x0",false)
,_choice84(84,this,(RangeFuncPointer)&AppC__InterfaceDevice::_choice84_func,2,"\x46\x7\x0\x0",false)
,_send83(83,this,event__AppC__end__AppC__end,0,"\x0\x0\x0\x0",true)
,_stop100(100,this)
,_lpChoice99(99,this,(RangeFuncPointer)&AppC__InterfaceDevice::_lpChoice99_func,2,0, false)
,_action286(286,this,(ActionFuncPointer)&AppC__InterfaceDevice::_action286_func, 0, false)

{
    _comment = new std::string[2];
    _comment[0]=std::string("Action i = i +1");
    _comment[1]=std::string("Action i=0");
    
    //generate task variable look-up table
    _varLookUpName["resetType"]=&resetType;
    _varLookUpID[8]=&resetType;
    _varLookUpName["x"]=&x;
    _varLookUpID[9]=&x;
    _varLookUpName["i"]=&i;
    _varLookUpID[10]=&i;
    _varLookUpName["nbOfComputedPackets"]=&nbOfComputedPackets;
    _varLookUpID[11]=&nbOfComputedPackets;
    _varLookUpName["b"]=&b;
    _varLookUpID[12]=&b;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__AppC__fromDtoSC->setBlockedWriteTask(this);
    channel__AppC__fromSCtoD->setBlockedReadTask(this);
    event__AppC__answerToReset__AppC__answerToReset->setBlockedReadTask(this);
    event__AppC__data_Ready_SC__AppC__data_Ready_SC->setBlockedReadTask(this);
    event__AppC__data_Ready__AppC__data_Ready->setBlockedWriteTask(this);
    event__AppC__end__AppC__end->setBlockedWriteTask(this);
    event__AppC__pTSConfirm__AppC__pTSConfirm->setBlockedReadTask(this);
    event__AppC__pTS__AppC__pTS->setBlockedWriteTask(this);
    event__AppC__reset__AppC__reset->setBlockedWriteTask(this);
    request__AppC__activation->setBlockedWriteTask(this);
    
    //command chaining
    _lpIncAc99.setNextCommand(array(1,(TMLCommand*)&_lpChoice99));
    _send92.setNextCommand(array(1,(TMLCommand*)&_lpIncAc99));
    _write91.setNextCommand(array(1,(TMLCommand*)&_send92));
    _read96.setNextCommand(array(1,(TMLCommand*)&_lpIncAc99));
    _wait97.setNextCommand(array(1,(TMLCommand*)&_read96));
    _choice85.setNextCommand(array(3,(TMLCommand*)&_lpIncAc99,(TMLCommand*)&_wait97,(TMLCommand*)&_stop85));
    _notified94.setNextCommand(array(1,(TMLCommand*)&_choice85));
    _choice84.setNextCommand(array(2,(TMLCommand*)&_write91,(TMLCommand*)&_notified94));
    _send83.setNextCommand(array(1,(TMLCommand*)&_stop100));
    _lpChoice99.setNextCommand(array(2,(TMLCommand*)&_choice84,(TMLCommand*)&_send83));
    _action286.setNextCommand(array(1,(TMLCommand*)&_lpChoice99));
    _wait90.setNextCommand(array(1,(TMLCommand*)&_action286));
    _send89.setNextCommand(array(1,(TMLCommand*)&_wait90));
    _wait88.setNextCommand(array(1,(TMLCommand*)&_send89));
    _send87.setNextCommand(array(1,(TMLCommand*)&_wait88));
    _request86.setNextCommand(array(1,(TMLCommand*)&_send87));
    _currCommand=&_request86;
    _firstCommand=&_request86;
    
    _channels[0] = channel__AppC__fromDtoSC;
    _channels[1] = channel__AppC__fromSCtoD;
    _channels[2] = event__AppC__answerToReset__AppC__answerToReset;
    _channels[3] = event__AppC__data_Ready_SC__AppC__data_Ready_SC;
    _channels[4] = event__AppC__data_Ready__AppC__data_Ready;
    _channels[5] = event__AppC__end__AppC__end;
    _channels[6] = event__AppC__pTSConfirm__AppC__pTSConfirm;
    _channels[7] = event__AppC__pTS__AppC__pTS;
    _channels[8] = event__AppC__reset__AppC__reset;
    refreshStateHash("\xc2\x3f\x0\x0");
}

void AppC__InterfaceDevice::_lpIncAc99_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    i = i +1;
}

Parameter* AppC__InterfaceDevice::_send92_func(Parameter* ioParam){
    std::ostringstream ss;
    
    ss << "(" << x << "(x)" << "," << b << "(b)" << ")";
    if(_send92.getCurrTransaction() != NULL) _send92.getCurrTransaction()->lastParams = ss.str();
    
    return new SizedParameter<ParamType,2>(x,b);
}

unsigned int AppC__InterfaceDevice::_choice85_func(ParamType& oMin, ParamType& oMax){
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

unsigned int AppC__InterfaceDevice::_choice84_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=1;
    return myrand(0, 1);
    
}

unsigned int AppC__InterfaceDevice::_lpChoice99_func(ParamType& oMin, ParamType& oMax){
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

void AppC__InterfaceDevice::_action286_func(){
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
    READ_STREAM(i_stream_var,b);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable b " << b << std::endl;
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
    WRITE_STREAM(i_stream_var,b);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable b " << b << std::endl;
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
    b=0;
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
            if ((_liveVarList[0] & 16)!=0) _stateHash.addValue(b);
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 64)!=0));
             _channels[2]->setSignificance(this, ((_liveVarList[0] & 128)!=0));
             _channels[3]->setSignificance(this, ((_liveVarList[1] & 1)!=0));
             _channels[6]->setSignificance(this, ((_liveVarList[1] & 8)!=0));
        }
    }
    return _stateHash.getHash();
}

