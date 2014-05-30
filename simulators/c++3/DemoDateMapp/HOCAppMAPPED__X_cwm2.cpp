#include <HOCAppMAPPED__X_cwm2.h>

HOCAppMAPPED__X_cwm2::HOCAppMAPPED__X_cwm2(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in
, TMLChannel* channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,arg1__req(0)
,_waitOnRequest(301,this,requestChannel,(ParamFuncPointer)&HOCAppMAPPED__X_cwm2::waitOnRequest_func,"\x1e\x0\x0\x0",false)
,_action306(306,this,(ActionFuncPointer)&HOCAppMAPPED__X_cwm2::_action306_func, "\x1d\x0\x0\x0",false)
,_read305(305,this,(LengthFuncPointer)&HOCAppMAPPED__X_cwm2::_read305_func,channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in,"\x1d\x0\x0\x0",true)
,_execi302(302,this,(LengthFuncPointer)&HOCAppMAPPED__X_cwm2::_execi302_func,0,1,"\x1d\x0\x0\x0",false)
,_write304(304,this,(LengthFuncPointer)&HOCAppMAPPED__X_cwm2::_write304_func,channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in,"\x1c\x0\x0\x0",true)

{
    _comment = new std::string[1];
    _comment[0]=std::string("Action size = arg1__req");
    
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[86]=&size;
    _varLookUpName["arg1__req"]=&arg1__req;
    _varLookUpID[157]=&arg1__req;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__cwm2_ch_out__HOCAppMAPPED__DMAcwm2_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _write304.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _execi302.setNextCommand(array(1,(TMLCommand*)&_write304));
    _read305.setNextCommand(array(1,(TMLCommand*)&_execi302));
    _action306.setNextCommand(array(1,(TMLCommand*)&_read305));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action306));
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

TMLLength HOCAppMAPPED__X_cwm2::_read305_func(){
    return (TMLLength)(size);
}

TMLLength HOCAppMAPPED__X_cwm2::_execi302_func(){
    return (TMLLength)(11 + size/2);
}

TMLLength HOCAppMAPPED__X_cwm2::_write304_func(){
    return (TMLLength)(size);
}

void HOCAppMAPPED__X_cwm2::_action306_func(){
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

