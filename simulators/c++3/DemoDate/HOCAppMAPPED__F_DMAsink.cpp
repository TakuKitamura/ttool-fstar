#include <HOCAppMAPPED__F_DMAsink.h>

HOCAppMAPPED__F_DMAsink::HOCAppMAPPED__F_DMAsink(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2
, TMLEventChannel* event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in
, TMLEventChannel* request__HOCAppMAPPED__r_DMAsink
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,_wait263(263,this,event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_DMAsink::_wait263_func,"\x3\x0\x0\x0",true)
,_request260(260,this,request__HOCAppMAPPED__r_DMAsink,(ParamFuncPointer)&HOCAppMAPPED__F_DMAsink::_request260_func,"\x3\x0\x0\x0",true)
,_send262(262,this,event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2,(ParamFuncPointer)&HOCAppMAPPED__F_DMAsink::_send262_func,"\x0\x0\x0\x0",true)
,_stop261(261,this)

{
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[58]=&size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2->setBlockedWriteTask(this);
    event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in->setBlockedReadTask(this);
    request__HOCAppMAPPED__r_DMAsink->setBlockedWriteTask(this);
    
    //command chaining
    _send262.setNextCommand(array(1,(TMLCommand*)&_stop261));
    _request260.setNextCommand(array(1,(TMLCommand*)&_send262));
    _wait263.setNextCommand(array(1,(TMLCommand*)&_request260));
    _currCommand=&_wait263;
    _firstCommand=&_wait263;
    
    _channels[0] = event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2;
    _channels[1] = event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in;
    refreshStateHash("\x6\x0\x0\x0");
}

Parameter* HOCAppMAPPED__F_DMAsink::_wait263_func(Parameter* ioParam){
    ioParam->getP(&size);
    return 0;
}

Parameter* HOCAppMAPPED__F_DMAsink::_request260_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

Parameter* HOCAppMAPPED__F_DMAsink::_send262_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

std::istream& HOCAppMAPPED__F_DMAsink::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__F_DMAsink::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__F_DMAsink::reset(){
    TMLTask::reset();
    size=0;
}

HashValueType HOCAppMAPPED__F_DMAsink::getStateHash(){
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

