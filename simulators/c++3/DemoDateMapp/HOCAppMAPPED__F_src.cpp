#include <HOCAppMAPPED__F_src.h>

HOCAppMAPPED__F_src::HOCAppMAPPED__F_src(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in
, TMLEventChannel* request__HOCAppMAPPED__r_src
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,r_size(0)
,_action244(244,this,(ActionFuncPointer)&HOCAppMAPPED__F_src::_action244_func, "\x2\x0\x0\x0",false)
,_request245(245,this,request__HOCAppMAPPED__r_src,(ParamFuncPointer)&HOCAppMAPPED__F_src::_request245_func,"\x2\x0\x0\x0",false)
,_send246(246,this,event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_src::_send246_func,"\x0\x0\x0\x0",false)
,_stop247(247,this)

{
    _comment = new std::string[1];
    _comment[0]=std::string("Action r_size = 2");
    
    //generate task variable look-up table
    _varLookUpName["r_size"]=&r_size;
    _varLookUpID[53]=&r_size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in->setBlockedWriteTask(this);
    request__HOCAppMAPPED__r_src->setBlockedWriteTask(this);
    
    //command chaining
    _send246.setNextCommand(array(1,(TMLCommand*)&_stop247));
    _request245.setNextCommand(array(1,(TMLCommand*)&_send246));
    _action244.setNextCommand(array(1,(TMLCommand*)&_request245));
    _currCommand=&_action244;
    _firstCommand=&_action244;
    
    _channels[0] = event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in;
    refreshStateHash("\x2\x0\x0\x0");
}

Parameter* HOCAppMAPPED__F_src::_request245_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

Parameter* HOCAppMAPPED__F_src::_send246_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

void HOCAppMAPPED__F_src::_action244_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    r_size = 2;
}

std::istream& HOCAppMAPPED__F_src::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,r_size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable r_size " << r_size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__F_src::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,r_size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable r_size " << r_size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__F_src::reset(){
    TMLTask::reset();
    r_size=0;
}

HashValueType HOCAppMAPPED__F_src::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(r_size);
        }
    }
    return _stateHash.getHash();
}

