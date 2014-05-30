#include <HOCAppMAPPED__F_vsum.h>

HOCAppMAPPED__F_vsum::HOCAppMAPPED__F_vsum(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in
, TMLEventChannel* event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in
, TMLEventChannel* request__HOCAppMAPPED__r_vsum
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,_wait190(190,this,event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_vsum::_wait190_func,"\x5\x0\x0\x0",true)
,_request187(187,this,request__HOCAppMAPPED__r_vsum,(ParamFuncPointer)&HOCAppMAPPED__F_vsum::_request187_func,"\x5\x0\x0\x0",true)
,_send189(189,this,event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_vsum::_send189_func,"\x0\x0\x0\x0",true)
,_stop188(188,this)

{
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[21]=&size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in->setBlockedReadTask(this);
    event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in->setBlockedWriteTask(this);
    request__HOCAppMAPPED__r_vsum->setBlockedWriteTask(this);
    
    //command chaining
    _send189.setNextCommand(array(1,(TMLCommand*)&_stop188));
    _request187.setNextCommand(array(1,(TMLCommand*)&_send189));
    _wait190.setNextCommand(array(1,(TMLCommand*)&_request187));
    _currCommand=&_wait190;
    _firstCommand=&_wait190;
    
    _channels[0] = event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in;
    _channels[1] = event__HOCAppMAPPED__vsum_evt_out__HOCAppMAPPED__DMAvsum_evt_in;
    refreshStateHash("\x6\x0\x0\x0");
}

Parameter* HOCAppMAPPED__F_vsum::_wait190_func(Parameter* ioParam){
    ioParam->getP(&size);
    return 0;
}

Parameter* HOCAppMAPPED__F_vsum::_request187_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

Parameter* HOCAppMAPPED__F_vsum::_send189_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

std::istream& HOCAppMAPPED__F_vsum::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__F_vsum::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__F_vsum::reset(){
    TMLTask::reset();
    size=0;
}

HashValueType HOCAppMAPPED__F_vsum::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(size);
             _channels[0]->setSignificance(this, ((_liveVarList[0] & 2)!=0));
        }
    }
    return _stateHash.getHash();
}

