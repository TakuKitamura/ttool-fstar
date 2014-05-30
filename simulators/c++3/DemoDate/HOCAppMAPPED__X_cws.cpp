#include <HOCAppMAPPED__X_cws.h>

HOCAppMAPPED__X_cws::HOCAppMAPPED__X_cws(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in
, TMLChannel* channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,arg1__req(0)
,_waitOnRequest(240,this,requestChannel,(ParamFuncPointer)&HOCAppMAPPED__X_cws::waitOnRequest_func,"\x1e\x0\x0\x0",false)
,_action245(245,this,(ActionFuncPointer)&HOCAppMAPPED__X_cws::_action245_func, "\x1d\x0\x0\x0",false)
,_read244(244,this,(LengthFuncPointer)&HOCAppMAPPED__X_cws::_read244_func,channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in,"\x1d\x0\x0\x0",true)
,_execi241(241,this,(LengthFuncPointer)&HOCAppMAPPED__X_cws::_execi241_func,0,1,"\x1d\x0\x0\x0",false)
,_write243(243,this,(LengthFuncPointer)&HOCAppMAPPED__X_cws::_write243_func,channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in,"\x1c\x0\x0\x0",true)

{
    _comment = new std::string[1];
    _comment[0]=std::string("Action size = arg1__req");
    
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[48]=&size;
    _varLookUpName["arg1__req"]=&arg1__req;
    _varLookUpID[143]=&arg1__req;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _write243.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _execi241.setNextCommand(array(1,(TMLCommand*)&_write243));
    _read244.setNextCommand(array(1,(TMLCommand*)&_execi241));
    _action245.setNextCommand(array(1,(TMLCommand*)&_read244));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action245));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__HOCAppMAPPED__cws_ch_out__HOCAppMAPPED__DMAcws_ch_in;
    _channels[1] = channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in;
    _channels[2] = requestChannel;
    refreshStateHash("\x1e\x0\x0\x0");
}

Parameter* HOCAppMAPPED__X_cws::waitOnRequest_func(Parameter* ioParam){
    ioParam->getP(&arg1__req);
    return 0;
}

TMLLength HOCAppMAPPED__X_cws::_read244_func(){
    return (TMLLength)(size);
}

TMLLength HOCAppMAPPED__X_cws::_execi241_func(){
    return (TMLLength)(11 + size/2);
}

TMLLength HOCAppMAPPED__X_cws::_write243_func(){
    return (TMLLength)(size);
}

void HOCAppMAPPED__X_cws::_action245_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    size = arg1__req;
}

std::istream& HOCAppMAPPED__X_cws::readObject(std::istream& i_stream_var){
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

std::ostream& HOCAppMAPPED__X_cws::writeObject(std::ostream& i_stream_var){
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

void HOCAppMAPPED__X_cws::reset(){
    TMLTask::reset();
    size=0;
    arg1__req=0;
}

HashValueType HOCAppMAPPED__X_cws::getStateHash(){
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

