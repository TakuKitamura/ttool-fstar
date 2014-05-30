#include <HOCAppMAPPED__join2.h>

HOCAppMAPPED__join2::HOCAppMAPPED__join2(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2
, TMLChannel* channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1
, TMLChannel* channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in
, TMLEventChannel* event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2
, TMLEventChannel* event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1
, TMLEventChannel* event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,r_size1(0)
,r_size2(0)
,_wait215(215,this,event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1,(ParamFuncPointer)&HOCAppMAPPED__join2::_wait215_func,"\xbd\x0\x0\x0",true)
,_read214(214,this,(LengthFuncPointer)&HOCAppMAPPED__join2::_read214_func,channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1,"\xb5\x0\x0\x0",true)
,_wait219(219,this,event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2,(ParamFuncPointer)&HOCAppMAPPED__join2::_wait219_func,"\x97\x0\x0\x0",true)
,_read218(218,this,(LengthFuncPointer)&HOCAppMAPPED__join2::_read218_func,channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2,"\x93\x0\x0\x0",true)
,_send213(213,this,event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in,(ParamFuncPointer)&HOCAppMAPPED__join2::_send213_func,"\x13\x0\x0\x0",true)
,_write217(217,this,(LengthFuncPointer)&HOCAppMAPPED__join2::_write217_func,channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in,"\x0\x0\x0\x0",true)
,_stop216(216,this)

{
    //generate task variable look-up table
    _varLookUpName["r_size1"]=&r_size1;
    _varLookUpID[33]=&r_size1;
    _varLookUpName["r_size2"]=&r_size2;
    _varLookUpID[34]=&r_size2;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2->setBlockedReadTask(this);
    channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1->setBlockedReadTask(this);
    channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2->setBlockedReadTask(this);
    event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1->setBlockedReadTask(this);
    event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in->setBlockedWriteTask(this);
    
    //command chaining
    _write217.setNextCommand(array(1,(TMLCommand*)&_stop216));
    _send213.setNextCommand(array(1,(TMLCommand*)&_write217));
    _read218.setNextCommand(array(1,(TMLCommand*)&_send213));
    _wait219.setNextCommand(array(1,(TMLCommand*)&_read218));
    _read214.setNextCommand(array(1,(TMLCommand*)&_wait219));
    _wait215.setNextCommand(array(1,(TMLCommand*)&_read214));
    _currCommand=&_wait215;
    _firstCommand=&_wait215;
    
    _channels[0] = channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2;
    _channels[1] = channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1;
    _channels[2] = channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in;
    _channels[3] = event__HOCAppMAPPED__DMAsink_evt_out__HOCAppMAPPED__join2_evt_in2;
    _channels[4] = event__HOCAppMAPPED__acc_evt_out__HOCAppMAPPED__join2_evt_in1;
    _channels[5] = event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in;
    refreshStateHash("\xfc\x0\x0\x0");
}

Parameter* HOCAppMAPPED__join2::_wait215_func(Parameter* ioParam){
    ioParam->getP(&r_size1);
    return 0;
}

TMLLength HOCAppMAPPED__join2::_read214_func(){
    return (TMLLength)(r_size1);
}

Parameter* HOCAppMAPPED__join2::_wait219_func(Parameter* ioParam){
    ioParam->getP(&r_size2);
    return 0;
}

TMLLength HOCAppMAPPED__join2::_read218_func(){
    return (TMLLength)(r_size2);
}

Parameter* HOCAppMAPPED__join2::_send213_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size1 + r_size2);
}

TMLLength HOCAppMAPPED__join2::_write217_func(){
    return (TMLLength)(r_size1 + r_size2);
}

std::istream& HOCAppMAPPED__join2::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,r_size1);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable r_size1 " << r_size1 << std::endl;
    #endif
    READ_STREAM(i_stream_var,r_size2);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable r_size2 " << r_size2 << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__join2::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,r_size1);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable r_size1 " << r_size1 << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,r_size2);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable r_size2 " << r_size2 << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__join2::reset(){
    TMLTask::reset();
    r_size1=0;
    r_size2=0;
}

HashValueType HOCAppMAPPED__join2::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(r_size1);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(r_size2);
            _channels[0]->setSignificance(this, ((_liveVarList[0] & 4)!=0));
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 8)!=0));
            _channels[2]->setSignificance(this, ((_liveVarList[0] & 16)!=0));
             _channels[3]->setSignificance(this, ((_liveVarList[0] & 32)!=0));
             _channels[4]->setSignificance(this, ((_liveVarList[0] & 64)!=0));
        }
    }
    return _stateHash.getHash();
}

