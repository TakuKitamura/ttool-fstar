#include <HOCAppMAPPED__join1.h>

HOCAppMAPPED__join1::HOCAppMAPPED__join1(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2
, TMLChannel* channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3
, TMLChannel* channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1
, TMLChannel* channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in
, TMLEventChannel* event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2
, TMLEventChannel* event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3
, TMLEventChannel* event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1
, TMLEventChannel* event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,r_size1(0)
,r_size2(0)
,r_size3(0)
,_wait208(208,this,event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1,(ParamFuncPointer)&HOCAppMAPPED__join1::_wait208_func,"\xf9\x5\x0\x0",true)
,_read207(207,this,(LengthFuncPointer)&HOCAppMAPPED__join1::_read207_func,channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1,"\xd9\x5\x0\x0",true)
,_wait212(212,this,event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2,(ParamFuncPointer)&HOCAppMAPPED__join1::_wait212_func,"\x5b\x5\x0\x0",true)
,_read214(214,this,(LengthFuncPointer)&HOCAppMAPPED__join1::_read214_func,channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2,"\x51\x5\x0\x0",true)
,_wait211(211,this,event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3,(ParamFuncPointer)&HOCAppMAPPED__join1::_wait211_func,"\x53\x4\x0\x0",true)
,_read213(213,this,(LengthFuncPointer)&HOCAppMAPPED__join1::_read213_func,channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3,"\x43\x4\x0\x0",true)
,_send206(206,this,event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in,(ParamFuncPointer)&HOCAppMAPPED__join1::_send206_func,"\x43\x0\x0\x0",true)
,_write210(210,this,(LengthFuncPointer)&HOCAppMAPPED__join1::_write210_func,channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in,"\x0\x0\x0\x0",true)
,_stop209(209,this)

{
    //generate task variable look-up table
    _varLookUpName["r_size1"]=&r_size1;
    _varLookUpID[37]=&r_size1;
    _varLookUpName["r_size2"]=&r_size2;
    _varLookUpID[38]=&r_size2;
    _varLookUpName["r_size3"]=&r_size3;
    _varLookUpID[39]=&r_size3;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2->setBlockedReadTask(this);
    channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3->setBlockedReadTask(this);
    channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1->setBlockedReadTask(this);
    channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2->setBlockedReadTask(this);
    event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3->setBlockedReadTask(this);
    event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1->setBlockedReadTask(this);
    event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in->setBlockedWriteTask(this);
    
    //command chaining
    _write210.setNextCommand(array(1,(TMLCommand*)&_stop209));
    _send206.setNextCommand(array(1,(TMLCommand*)&_write210));
    _read213.setNextCommand(array(1,(TMLCommand*)&_send206));
    _wait211.setNextCommand(array(1,(TMLCommand*)&_read213));
    _read214.setNextCommand(array(1,(TMLCommand*)&_wait211));
    _wait212.setNextCommand(array(1,(TMLCommand*)&_read214));
    _read207.setNextCommand(array(1,(TMLCommand*)&_wait212));
    _wait208.setNextCommand(array(1,(TMLCommand*)&_read207));
    _currCommand=&_wait208;
    _firstCommand=&_wait208;
    
    _channels[0] = channel__HOCAppMAPPED__DMAcwm2_ch_out__HOCAppMAPPED__join1_ch_in2;
    _channels[1] = channel__HOCAppMAPPED__DMAcws_ch_out__HOCAppMAPPED__join1_ch_in3;
    _channels[2] = channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1;
    _channels[3] = channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in;
    _channels[4] = event__HOCAppMAPPED__DMAcwm2_evt_out__HOCAppMAPPED__join1_evt_in2;
    _channels[5] = event__HOCAppMAPPED__DMAcws_evt_out__HOCAppMAPPED__join1_evt_in3;
    _channels[6] = event__HOCAppMAPPED__DMAvsum_evt_out__HOCAppMAPPED__join1_evt_in1;
    _channels[7] = event__HOCAppMAPPED__join1_evt_out__HOCAppMAPPED__acc_evt_in;
    refreshStateHash("\xf8\x7\x0\x0");
}

Parameter* HOCAppMAPPED__join1::_wait208_func(Parameter* ioParam){
    ioParam->getP(&r_size1);
    return 0;
}

TMLLength HOCAppMAPPED__join1::_read207_func(){
    return (TMLLength)(r_size1);
}

Parameter* HOCAppMAPPED__join1::_wait212_func(Parameter* ioParam){
    ioParam->getP(&r_size2);
    return 0;
}

TMLLength HOCAppMAPPED__join1::_read214_func(){
    return (TMLLength)(r_size2);
}

Parameter* HOCAppMAPPED__join1::_wait211_func(Parameter* ioParam){
    ioParam->getP(&r_size2);
    return 0;
}

TMLLength HOCAppMAPPED__join1::_read213_func(){
    return (TMLLength)(r_size2);
}

Parameter* HOCAppMAPPED__join1::_send206_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size1 + r_size2 + r_size3);
}

TMLLength HOCAppMAPPED__join1::_write210_func(){
    return (TMLLength)(r_size1 + r_size2 + r_size3);
}

std::istream& HOCAppMAPPED__join1::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,r_size1);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable r_size1 " << r_size1 << std::endl;
    #endif
    READ_STREAM(i_stream_var,r_size2);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable r_size2 " << r_size2 << std::endl;
    #endif
    READ_STREAM(i_stream_var,r_size3);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable r_size3 " << r_size3 << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__join1::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,r_size1);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable r_size1 " << r_size1 << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,r_size2);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable r_size2 " << r_size2 << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,r_size3);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable r_size3 " << r_size3 << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__join1::reset(){
    TMLTask::reset();
    r_size1=0;
    r_size2=0;
    r_size3=0;
}

HashValueType HOCAppMAPPED__join1::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(r_size1);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(r_size2);
            if ((_liveVarList[0] & 4)!=0) _stateHash.addValue(r_size3);
            _channels[0]->setSignificance(this, ((_liveVarList[0] & 8)!=0));
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 16)!=0));
            _channels[2]->setSignificance(this, ((_liveVarList[0] & 32)!=0));
            _channels[3]->setSignificance(this, ((_liveVarList[0] & 64)!=0));
             _channels[4]->setSignificance(this, ((_liveVarList[0] & 128)!=0));
             _channels[5]->setSignificance(this, ((_liveVarList[1] & 1)!=0));
             _channels[6]->setSignificance(this, ((_liveVarList[1] & 2)!=0));
             _channels[7]->setSignificance(this, ((_liveVarList[1] & 4)!=0));
        }
    }
    return _stateHash.getHash();
}

