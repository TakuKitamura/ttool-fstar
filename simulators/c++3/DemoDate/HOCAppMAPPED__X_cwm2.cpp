#include <HOCAppMAPPED__X_cwm2.h>

HOCAppMAPPED__X_cwm2::HOCAppMAPPED__X_cwm2(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in
, TMLChannel* channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,arg1__req(0)
,_waitOnRequest(158,this,requestChannel,(ParamFuncPointer)&HOCAppMAPPED__X_cwm2::waitOnRequest_func,"\x1e\x0\x0\x0",false)
,_action163(163,this,(ActionFuncPointer)&HOCAppMAPPED__X_cwm2::_action163_func, "\x1d\x0\x0\x0",false)
,_read162(162,this,(LengthFuncPointer)&HOCAppMAPPED__X_cwm2::_read162_func,channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in,"\x1d\x0\x0\x0",true)
,_execi159(159,this,(LengthFuncPointer)&HOCAppMAPPED__X_cwm2::_execi159_func,0,1,"\x1d\x0\x0\x0",false)
,_write161(161,this,(LengthFuncPointer)&HOCAppMAPPED__X_cwm2::_write161_func,channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in,"\x1c\x0\x0\x0",true)

{
    _comment = new std::string[1];
    _comment[0]=std::string("Action size = arg1__req");
    
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[6]=&size;
    _varLookUpName["arg1__req"]=&arg1__req;
    _varLookUpID[133]=&arg1__req;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _write161.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _execi159.setNextCommand(array(1,(TMLCommand*)&_write161));
    _read162.setNextCommand(array(1,(TMLCommand*)&_execi159));
    _action163.setNextCommand(array(1,(TMLCommand*)&_read162));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action163));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in;
    _channels[1] = channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in;
    _channels[2] = requestChannel;
    refreshStateHash("\x1e\x0\x0\x0");
}

Parameter* HOCAppMAPPED__X_cwm2::waitOnRequest_func(Parameter* ioParam){
    ioParam->getP(&arg1__req);
    return 0;
}

TMLLength HOCAppMAPPED__X_cwm2::_read162_func(){
    return (TMLLength)(size);
}

TMLLength HOCAppMAPPED__X_cwm2::_execi159_func(){
    return (TMLLength)(11 + size/2);
}

TMLLength HOCAppMAPPED__X_cwm2::_write161_func(){
    return (TMLLength)(size);
}

void HOCAppMAPPED__X_cwm2::_action163_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    size = arg1__req;
}

std::istream& HOCAppMAPPED__X_cwm2::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    READ_STREAM(i_stream_var,arg1__req);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable arg1__req " << arg1__req << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__X_cwm2::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,arg1__req);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable arg1__req " << arg1__req << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__X_cwm2::reset(){
    TMLTask::reset();
    size=0;
    arg1__req=0;
}

HashValueType HOCAppMAPPED__X_cwm2::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(size);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(arg1__req);
            _channels[0]->setSignificance(this, ((_liveVarList[0] & 4)!=0));
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 8)!=0));
             _channels[2]->setSignificance(this, ((_liveVarList[0] & 16)!=0));
        }
    }
    return _stateHash.getHash();
}

