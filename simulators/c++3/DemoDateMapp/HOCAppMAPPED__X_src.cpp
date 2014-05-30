#include <HOCAppMAPPED__X_src.h>

HOCAppMAPPED__X_src::HOCAppMAPPED__X_src(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,r_size(0)
,arg1__req(0)
,_waitOnRequest(307,this,requestChannel,(ParamFuncPointer)&HOCAppMAPPED__X_src::waitOnRequest_func,"\xe\x0\x0\x0",false)
,_action311(311,this,(ActionFuncPointer)&HOCAppMAPPED__X_src::_action311_func, "\xd\x0\x0\x0",false)
,_execi308(308,this,(LengthFuncPointer)&HOCAppMAPPED__X_src::_execi308_func,0,1,"\xd\x0\x0\x0",false)
,_write310(310,this,(LengthFuncPointer)&HOCAppMAPPED__X_src::_write310_func,channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in,"\xc\x0\x0\x0",true)

{
    _comment = new std::string[1];
    _comment[0]=std::string("Action r_size = arg1__req");
    
    //generate task variable look-up table
    _varLookUpName["r_size"]=&r_size;
    _varLookUpID[89]=&r_size;
    _varLookUpName["arg1__req"]=&arg1__req;
    _varLookUpID[159]=&arg1__req;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in->setBlockedWriteTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _write310.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _execi308.setNextCommand(array(1,(TMLCommand*)&_write310));
    _action311.setNextCommand(array(1,(TMLCommand*)&_execi308));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action311));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in;
    _channels[1] = requestChannel;
    refreshStateHash("\xe\x0\x0\x0");
}

Parameter* HOCAppMAPPED__X_src::waitOnRequest_func(Parameter* ioParam){
    ioParam->getP(&arg1__req);
    return 0;
}

TMLLength HOCAppMAPPED__X_src::_execi308_func(){
    return (TMLLength)(r_size);
}

TMLLength HOCAppMAPPED__X_src::_write310_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__X_src::_action311_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    r_size = arg1__req;
}

std::istream& HOCAppMAPPED__X_src::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,r_size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable r_size " << r_size << std::endl;
    #endif
    READ_STREAM(i_stream_var,arg1__req);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable arg1__req " << arg1__req << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__X_src::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,r_size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable r_size " << r_size << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,arg1__req);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable arg1__req " << arg1__req << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__X_src::reset(){
    TMLTask::reset();
    r_size=0;
    arg1__req=0;
}

HashValueType HOCAppMAPPED__X_src::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(r_size);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(arg1__req);
            _channels[0]->setSignificance(this, ((_liveVarList[0] & 4)!=0));
             _channels[1]->setSignificance(this, ((_liveVarList[0] & 8)!=0));
        }
    }
    return _stateHash.getHash();
}

