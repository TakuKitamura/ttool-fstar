#include <HOCAppMAPPED__F_cwm1.h>

HOCAppMAPPED__F_cwm1::HOCAppMAPPED__F_cwm1(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in
, TMLEventChannel* event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in
, TMLEventChannel* request__HOCAppMAPPED__r_cwm1
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,_wait267(267,this,event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_cwm1::_wait267_func,"\x3\x0\x0\x0",true)
,_request264(264,this,request__HOCAppMAPPED__r_cwm1,(ParamFuncPointer)&HOCAppMAPPED__F_cwm1::_request264_func,"\x3\x0\x0\x0",true)
,_send266(266,this,event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_cwm1::_send266_func,"\x0\x0\x0\x0",true)
,_stop265(265,this)

{
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[65]=&size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in->setBlockedReadTask(this);
    request__HOCAppMAPPED__r_cwm1->setBlockedWriteTask(this);
    
    //command chaining
    _send266.setNextCommand(array(1,(TMLCommand*)&_stop265));
    _request264.setNextCommand(array(1,(TMLCommand*)&_send266));
    _wait267.setNextCommand(array(1,(TMLCommand*)&_request264));
    _currCommand=&_wait267;
    _firstCommand=&_wait267;
    
    _channels[0] = event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in;
    _channels[1] = event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in;
    refreshStateHash("\x6\x0\x0\x0");
}

Parameter* HOCAppMAPPED__F_cwm1::_wait267_func(Parameter* ioParam){
    ioParam->getP(&size);
    return 0;
}

Parameter* HOCAppMAPPED__F_cwm1::_request264_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

Parameter* HOCAppMAPPED__F_cwm1::_send266_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

std::istream& HOCAppMAPPED__F_cwm1::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__F_cwm1::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__F_cwm1::reset(){
    TMLTask::reset();
    size=0;
}

HashValueType HOCAppMAPPED__F_cwm1::getStateHash(){
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

