#include <HOCAppMAPPED__F_acc.h>

HOCAppMAPPED__F_acc::HOCAppMAPPED__F_acc(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1
, TMLEventChannel* event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in
, TMLEventChannel* request__HOCAppMAPPED__r_acc
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,_wait201(201,this,event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_acc::_wait201_func,"\x3\x0\x0\x0",true)
,_request198(198,this,request__HOCAppMAPPED__r_acc,(ParamFuncPointer)&HOCAppMAPPED__F_acc::_request198_func,"\x3\x0\x0\x0",true)
,_send200(200,this,event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1,(ParamFuncPointer)&HOCAppMAPPED__F_acc::_send200_func,"\x0\x0\x0\x0",true)
,_stop199(199,this)

{
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[27]=&size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1->setBlockedWriteTask(this);
    event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in->setBlockedReadTask(this);
    request__HOCAppMAPPED__r_acc->setBlockedWriteTask(this);
    
    //command chaining
    _send200.setNextCommand(array(1,(TMLCommand*)&_stop199));
    _request198.setNextCommand(array(1,(TMLCommand*)&_send200));
    _wait201.setNextCommand(array(1,(TMLCommand*)&_request198));
    _currCommand=&_wait201;
    _firstCommand=&_wait201;
    
    _channels[0] = event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1;
    _channels[1] = event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in;
    refreshStateHash("\x6\x0\x0\x0");
}

Parameter* HOCAppMAPPED__F_acc::_wait201_func(Parameter* ioParam){
    ioParam->getP(&size);
    return 0;
}

Parameter* HOCAppMAPPED__F_acc::_request198_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

Parameter* HOCAppMAPPED__F_acc::_send200_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

std::istream& HOCAppMAPPED__F_acc::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__F_acc::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__F_acc::reset(){
    TMLTask::reset();
    size=0;
}

HashValueType HOCAppMAPPED__F_acc::getStateHash(){
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

