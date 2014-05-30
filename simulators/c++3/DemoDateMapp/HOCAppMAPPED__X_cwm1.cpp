#include <HOCAppMAPPED__X_cwm1.h>

HOCAppMAPPED__X_cwm1::HOCAppMAPPED__X_cwm1(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in
, TMLChannel* channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,arg1__req(0)
,_waitOnRequest(289,this,requestChannel,(ParamFuncPointer)&HOCAppMAPPED__X_cwm1::waitOnRequest_func,"\x1e\x0\x0\x0",false)
,_action294(294,this,(ActionFuncPointer)&HOCAppMAPPED__X_cwm1::_action294_func, "\x1d\x0\x0\x0",false)
,_read293(293,this,(LengthFuncPointer)&HOCAppMAPPED__X_cwm1::_read293_func,channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in,"\x1d\x0\x0\x0",true)
,_execi290(290,this,(LengthFuncPointer)&HOCAppMAPPED__X_cwm1::_execi290_func,0,1,"\x1d\x0\x0\x0",false)
,_write292(292,this,(LengthFuncPointer)&HOCAppMAPPED__X_cwm1::_write292_func,channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in,"\x1c\x0\x0\x0",true)

{
    _comment = new std::string[1];
    _comment[0]=std::string("Action size = arg1__req");
    
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[80]=&size;
    _varLookUpName["arg1__req"]=&arg1__req;
    _varLookUpID[153]=&arg1__req;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _write292.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _execi290.setNextCommand(array(1,(TMLCommand*)&_write292));
    _read293.setNextCommand(array(1,(TMLCommand*)&_execi290));
    _action294.setNextCommand(array(1,(TMLCommand*)&_read293));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action294));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in;
    _channels[1] = channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in;
    _channels[2] = requestChannel;
    refreshStateHash("\x1e\x0\x0\x0");
}

Parameter* HOCAppMAPPED__X_cwm1::waitOnRequest_func(Parameter* ioParam){
    ioParam->getP(&arg1__req);
    return 0;
}

TMLLength HOCAppMAPPED__X_cwm1::_read293_func(){
    return (TMLLength)(size);
}

TMLLength HOCAppMAPPED__X_cwm1::_execi290_func(){
    return (TMLLength)(11 + size/2);
}

TMLLength HOCAppMAPPED__X_cwm1::_write292_func(){
    return (TMLLength)(size);
}

void HOCAppMAPPED__X_cwm1::_action294_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    size = arg1__req;
}

std::istream& HOCAppMAPPED__X_cwm1::readObject(std::istream& i_stream_var){
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

std::ostream& HOCAppMAPPED__X_cwm1::writeObject(std::ostream& i_stream_var){
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

void HOCAppMAPPED__X_cwm1::reset(){
    TMLTask::reset();
    size=0;
    arg1__req=0;
}

HashValueType HOCAppMAPPED__X_cwm1::getStateHash(){
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

