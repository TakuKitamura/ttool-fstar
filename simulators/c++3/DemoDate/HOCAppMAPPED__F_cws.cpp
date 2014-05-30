#include <HOCAppMAPPED__F_cws.h>

HOCAppMAPPED__F_cws::HOCAppMAPPED__F_cws(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in
, TMLEventChannel* event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in
, TMLEventChannel* request__HOCAppMAPPED__r_cws
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,_wait250(250,this,event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_cws::_wait250_func,"\x3\x0\x0\x0",true)
,_request247(247,this,request__HOCAppMAPPED__r_cws,(ParamFuncPointer)&HOCAppMAPPED__F_cws::_request247_func,"\x3\x0\x0\x0",true)
,_send249(249,this,event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_cws::_send249_func,"\x0\x0\x0\x0",true)
,_stop248(248,this)

{
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[51]=&size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in->setBlockedReadTask(this);
    request__HOCAppMAPPED__r_cws->setBlockedWriteTask(this);
    
    //command chaining
    _send249.setNextCommand(array(1,(TMLCommand*)&_stop248));
    _request247.setNextCommand(array(1,(TMLCommand*)&_send249));
    _wait250.setNextCommand(array(1,(TMLCommand*)&_request247));
    _currCommand=&_wait250;
    _firstCommand=&_wait250;
    
    _channels[0] = event__HOCAppMAPPED__cws_evt_out__HOCAppMAPPED__DMAcws_evt_in;
    _channels[1] = event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in;
    refreshStateHash("\x6\x0\x0\x0");
}

Parameter* HOCAppMAPPED__F_cws::_wait250_func(Parameter* ioParam){
    ioParam->getP(&size);
    return 0;
}

Parameter* HOCAppMAPPED__F_cws::_request247_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

Parameter* HOCAppMAPPED__F_cws::_send249_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

std::istream& HOCAppMAPPED__F_cws::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__F_cws::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__F_cws::reset(){
    TMLTask::reset();
    size=0;
}

HashValueType HOCAppMAPPED__F_cws::getStateHash(){
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

