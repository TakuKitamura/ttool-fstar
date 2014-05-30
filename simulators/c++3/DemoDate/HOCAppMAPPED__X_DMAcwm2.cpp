#include <HOCAppMAPPED__X_DMAcwm2.h>

HOCAppMAPPED__X_DMAcwm2::HOCAppMAPPED__X_DMAcwm2(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2
, TMLChannel* channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,i(0)
,arg1__req(0)
,_waitOnRequest(264,this,requestChannel,(ParamFuncPointer)&HOCAppMAPPED__X_DMAcwm2::waitOnRequest_func,"\x3c\x0\x0\x0",false)
,_action271(271,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAcwm2::_action271_func, "\x39\x0\x0\x0",false)
,_lpIncAc266(266,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAcwm2::_lpIncAc266_func, 0, false)
,_read270(270,this,(LengthFuncPointer)&HOCAppMAPPED__X_DMAcwm2::_read270_func,channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in,"\x3b\x0\x0\x0",true)
,_execi267(267,this,(LengthFuncPointer)&HOCAppMAPPED__X_DMAcwm2::_execi267_func,0,1,"\x3b\x0\x0\x0",false)
,_write269(269,this,(LengthFuncPointer)&HOCAppMAPPED__X_DMAcwm2::_write269_func,channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2,"\x3b\x0\x0\x0",true)
,_lpChoice266(266,this,(RangeFuncPointer)&HOCAppMAPPED__X_DMAcwm2::_lpChoice266_func,2,0, false)
,_action370(370,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAcwm2::_action370_func, 0, false)

{
    _comment = new std::string[3];
    _comment[0]=std::string("Action i = i-1");
    _comment[1]=std::string("Action i=size");
    _comment[2]=std::string("Action size = arg1__req");
    
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[61]=&size;
    _varLookUpName["i"]=&i;
    _varLookUpID[62]=&i;
    _varLookUpName["arg1__req"]=&arg1__req;
    _varLookUpID[147]=&arg1__req;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _lpIncAc266.setNextCommand(array(1,(TMLCommand*)&_lpChoice266));
    _write269.setNextCommand(array(1,(TMLCommand*)&_lpIncAc266));
    _execi267.setNextCommand(array(1,(TMLCommand*)&_write269));
    _read270.setNextCommand(array(1,(TMLCommand*)&_execi267));
    _lpChoice266.setNextCommand(array(2,(TMLCommand*)&_read270,(TMLCommand*)&_waitOnRequest));
    _action370.setNextCommand(array(1,(TMLCommand*)&_lpChoice266));
    _action271.setNextCommand(array(1,(TMLCommand*)&_action370));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action271));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2;
    _channels[1] = channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in;
    _channels[2] = requestChannel;
    refreshStateHash("\x3c\x0\x0\x0");
}

Parameter* HOCAppMAPPED__X_DMAcwm2::waitOnRequest_func(Parameter* ioParam){
    ioParam->getP(&arg1__req);
    return 0;
}

void HOCAppMAPPED__X_DMAcwm2::_lpIncAc266_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    i = i-1;
}

TMLLength HOCAppMAPPED__X_DMAcwm2::_read270_func(){
    return (TMLLength)(size);
}

TMLLength HOCAppMAPPED__X_DMAcwm2::_execi267_func(){
    return (TMLLength)(size*2);
}

TMLLength HOCAppMAPPED__X_DMAcwm2::_write269_func(){
    return (TMLLength)(size);
}

unsigned int HOCAppMAPPED__X_DMAcwm2::_lpChoice266_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( i==0 ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

void HOCAppMAPPED__X_DMAcwm2::_action370_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    i=size;
}

void HOCAppMAPPED__X_DMAcwm2::_action271_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    size = arg1__req;
}

std::istream& HOCAppMAPPED__X_DMAcwm2::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    READ_STREAM(i_stream_var,i);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable i " << i << std::endl;
    #endif
    READ_STREAM(i_stream_var,arg1__req);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable arg1__req " << arg1__req << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__X_DMAcwm2::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,i);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable i " << i << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,arg1__req);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable arg1__req " << arg1__req << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__X_DMAcwm2::reset(){
    TMLTask::reset();
    size=0;
    i=0;
    arg1__req=0;
}

HashValueType HOCAppMAPPED__X_DMAcwm2::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(size);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(i);
            if ((_liveVarList[0] & 4)!=0) _stateHash.addValue(arg1__req);
            _channels[0]->setSignificance(this, ((_liveVarList[0] & 8)!=0));
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 16)!=0));
             _channels[2]->setSignificance(this, ((_liveVarList[0] & 32)!=0));
        }
    }
    return _stateHash.getHash();
}

