#include <HOCAppMAPPED__F_DMAcws.h>

HOCAppMAPPED__F_DMAcws::HOCAppMAPPED__F_DMAcws(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3
, TMLEventChannel* event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in
, TMLEventChannel* request__HOCAppMAPPED__r_DMAcws
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,_wait302(302,this,event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_DMAcws::_wait302_func,"\x3\x0\x0\x0",true)
,_request299(299,this,request__HOCAppMAPPED__r_DMAcws,(ParamFuncPointer)&HOCAppMAPPED__F_DMAcws::_request299_func,"\x3\x0\x0\x0",true)
,_send301(301,this,event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3,(ParamFuncPointer)&HOCAppMAPPED__F_DMAcws::_send301_func,"\x0\x0\x0\x0",true)
,_stop300(300,this)

{
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[79]=&size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3->setBlockedWriteTask(this);
    event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in->setBlockedReadTask(this);
    request__HOCAppMAPPED__r_DMAcws->setBlockedWriteTask(this);
    
    //command chaining
    _send301.setNextCommand(array(1,(TMLCommand*)&_stop300));
    _request299.setNextCommand(array(1,(TMLCommand*)&_send301));
    _wait302.setNextCommand(array(1,(TMLCommand*)&_request299));
    _currCommand=&_wait302;
    _firstCommand=&_wait302;
    
    _channels[0] = event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3;
    _channels[1] = event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in;
    refreshStateHash("\x6\x0\x0\x0");
}

Parameter* HOCAppMAPPED__F_DMAcws::_wait302_func(Parameter* ioParam){
    ioParam->getP(&size);
    return 0;
}

Parameter* HOCAppMAPPED__F_DMAcws::_request299_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

Parameter* HOCAppMAPPED__F_DMAcws::_send301_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

std::istream& HOCAppMAPPED__F_DMAcws::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__F_DMAcws::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__F_DMAcws::reset(){
    TMLTask::reset();
    size=0;
}

HashValueType HOCAppMAPPED__F_DMAcws::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(size);
             _channels[1]->setSignificance(this, ((_liveVarList[0] & 4)!=0));
        }
    }
    return _stateHash.getHash();
}

