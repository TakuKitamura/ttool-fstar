#include <HOCAppMAPPED__F_DMAvsum.h>

HOCAppMAPPED__F_DMAvsum::HOCAppMAPPED__F_DMAvsum(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1
, TMLEventChannel* event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in
, TMLEventChannel* request__HOCAppMAPPED__r_DMAvsum
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,_wait189(189,this,event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_DMAvsum::_wait189_func,"\x3\x0\x0\x0",true)
,_request186(186,this,request__HOCAppMAPPED__r_DMAvsum,(ParamFuncPointer)&HOCAppMAPPED__F_DMAvsum::_request186_func,"\x3\x0\x0\x0",true)
,_send188(188,this,event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1,(ParamFuncPointer)&HOCAppMAPPED__F_DMAvsum::_send188_func,"\x0\x0\x0\x0",true)
,_stop187(187,this)

{
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[25]=&size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1->setBlockedWriteTask(this);
    event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in->setBlockedReadTask(this);
    request__HOCAppMAPPED__r_DMAvsum->setBlockedWriteTask(this);
    
    //command chaining
    _send188.setNextCommand(array(1,(TMLCommand*)&_stop187));
    _request186.setNextCommand(array(1,(TMLCommand*)&_send188));
    _wait189.setNextCommand(array(1,(TMLCommand*)&_request186));
    _currCommand=&_wait189;
    _firstCommand=&_wait189;
    
    _channels[0] = event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1;
    _channels[1] = event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in;
    refreshStateHash("\x6\x0\x0\x0");
}

Parameter* HOCAppMAPPED__F_DMAvsum::_wait189_func(Parameter* ioParam){
    ioParam->getP(&size);
    return 0;
}

Parameter* HOCAppMAPPED__F_DMAvsum::_request186_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

Parameter* HOCAppMAPPED__F_DMAvsum::_send188_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

std::istream& HOCAppMAPPED__F_DMAvsum::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__F_DMAvsum::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__F_DMAvsum::reset(){
    TMLTask::reset();
    size=0;
}

HashValueType HOCAppMAPPED__F_DMAvsum::getStateHash(){
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

