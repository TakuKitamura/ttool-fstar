#include <HOCAppMAPPED__F_DMAcwm2.h>

HOCAppMAPPED__F_DMAcwm2::HOCAppMAPPED__F_DMAcwm2(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2
, TMLEventChannel* event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in
, TMLEventChannel* request__HOCAppMAPPED__r_DMAcwm2
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,_wait194(194,this,event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_DMAcwm2::_wait194_func,"\x3\x0\x0\x0",true)
,_request191(191,this,request__HOCAppMAPPED__r_DMAcwm2,(ParamFuncPointer)&HOCAppMAPPED__F_DMAcwm2::_request191_func,"\x3\x0\x0\x0",true)
,_send193(193,this,event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2,(ParamFuncPointer)&HOCAppMAPPED__F_DMAcwm2::_send193_func,"\x0\x0\x0\x0",true)
,_stop192(192,this)

{
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[28]=&size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2->setBlockedWriteTask(this);
    event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in->setBlockedReadTask(this);
    request__HOCAppMAPPED__r_DMAcwm2->setBlockedWriteTask(this);
    
    //command chaining
    _send193.setNextCommand(array(1,(TMLCommand*)&_stop192));
    _request191.setNextCommand(array(1,(TMLCommand*)&_send193));
    _wait194.setNextCommand(array(1,(TMLCommand*)&_request191));
    _currCommand=&_wait194;
    _firstCommand=&_wait194;
    
    _channels[0] = event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2;
    _channels[1] = event__HOCAppMAPPED__cwm2_evt_out__HOCAppMAPPED__DMAcwm2_evt_in;
    refreshStateHash("\x6\x0\x0\x0");
}

Parameter* HOCAppMAPPED__F_DMAcwm2::_wait194_func(Parameter* ioParam){
    ioParam->getP(&size);
    return 0;
}

Parameter* HOCAppMAPPED__F_DMAcwm2::_request191_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

Parameter* HOCAppMAPPED__F_DMAcwm2::_send193_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

std::istream& HOCAppMAPPED__F_DMAcwm2::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__F_DMAcwm2::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__F_DMAcwm2::reset(){
    TMLTask::reset();
    size=0;
}

HashValueType HOCAppMAPPED__F_DMAcwm2::getStateHash(){
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

