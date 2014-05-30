#include <HOCAppMAPPED__F_DMAfork1.h>

HOCAppMAPPED__F_DMAfork1::HOCAppMAPPED__F_DMAfork1(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLEventChannel* event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in
, TMLEventChannel* event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in
, TMLEventChannel* request__HOCAppMAPPED__r_DMAfork1
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,_wait315(315,this,event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_DMAfork1::_wait315_func,"\x3\x0\x0\x0",true)
,_request312(312,this,request__HOCAppMAPPED__r_DMAfork1,(ParamFuncPointer)&HOCAppMAPPED__F_DMAfork1::_request312_func,"\x3\x0\x0\x0",true)
,_send314(314,this,event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in,(ParamFuncPointer)&HOCAppMAPPED__F_DMAfork1::_send314_func,"\x0\x0\x0\x0",true)
,_stop313(313,this)

{
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[86]=&size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in->setBlockedReadTask(this);
    request__HOCAppMAPPED__r_DMAfork1->setBlockedWriteTask(this);
    
    //command chaining
    _send314.setNextCommand(array(1,(TMLCommand*)&_stop313));
    _request312.setNextCommand(array(1,(TMLCommand*)&_send314));
    _wait315.setNextCommand(array(1,(TMLCommand*)&_request312));
    _currCommand=&_wait315;
    _firstCommand=&_wait315;
    
    _channels[0] = event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in;
    _channels[1] = event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in;
    refreshStateHash("\x6\x0\x0\x0");
}

Parameter* HOCAppMAPPED__F_DMAfork1::_wait315_func(Parameter* ioParam){
    ioParam->getP(&size);
    return 0;
}

Parameter* HOCAppMAPPED__F_DMAfork1::_request312_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

Parameter* HOCAppMAPPED__F_DMAfork1::_send314_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(size);
}

std::istream& HOCAppMAPPED__F_DMAfork1::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__F_DMAfork1::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__F_DMAfork1::reset(){
    TMLTask::reset();
    size=0;
}

HashValueType HOCAppMAPPED__F_DMAfork1::getStateHash(){
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

