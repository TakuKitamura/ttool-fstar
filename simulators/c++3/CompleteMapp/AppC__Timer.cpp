#include <AppC__Timer.h>

AppC__Timer::AppC__Timer(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__AppC__stop__AppC__stop
, TMLEventChannel* event__AppC__timeOut__AppC__timeOut
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,x(0)
,_waitOnRequest(63,this,requestChannel,0,"\xe\x0\x0\x0",false)
,_notified69(69,this,event__AppC__stop__AppC__stop,&x,"x","\xf\x0\x0\x0",false)
,_send66(66,this,event__AppC__timeOut__AppC__timeOut,0,"\xe\x0\x0\x0",true)
,_wait67(67,this,event__AppC__stop__AppC__stop,0,"\xe\x0\x0\x0",true)
, _stop64(64,this)
,_choice64(64,this,(RangeFuncPointer)&AppC__Timer::_choice64_func,3,"\xe\x0\x0\x0",false)

{
    //generate task variable look-up table
    _varLookUpName["x"]=&x;
    _varLookUpID[8]=&x;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__AppC__stop__AppC__stop->setBlockedReadTask(this);
    event__AppC__timeOut__AppC__timeOut->setBlockedWriteTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _send66.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _wait67.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _choice64.setNextCommand(array(3,(TMLCommand*)&_send66,(TMLCommand*)&_wait67,(TMLCommand*)&_stop64));
    _notified69.setNextCommand(array(1,(TMLCommand*)&_choice64));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_notified69));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = event__AppC__stop__AppC__stop;
    _channels[1] = event__AppC__timeOut__AppC__timeOut;
    _channels[2] = requestChannel;
    refreshStateHash("\xe\x0\x0\x0");
}

unsigned int AppC__Timer::_choice64_func(ParamType& oMin, ParamType& oMax){
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

std::istream& AppC__Timer::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,x);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable x " << x << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& AppC__Timer::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,x);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable x " << x << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void AppC__Timer::reset(){
    TMLTask::reset();
    x=0;
}

HashValueType AppC__Timer::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(x);
             _channels[0]->setSignificance(this, ((_liveVarList[0] & 2)!=0));
             _channels[2]->setSignificance(this, ((_liveVarList[0] & 8)!=0));
        }
    }
    return _stateHash.getHash();
}

