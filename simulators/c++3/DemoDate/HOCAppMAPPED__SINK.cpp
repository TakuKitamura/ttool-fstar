#include <HOCAppMAPPED__SINK.h>

HOCAppMAPPED__SINK::HOCAppMAPPED__SINK(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in
, TMLEventChannel* event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,_wait157(157,this,event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in,(ParamFuncPointer)&HOCAppMAPPED__SINK::_wait157_func,"\x3\x0\x0\x0",true)
,_read155(155,this,(LengthFuncPointer)&HOCAppMAPPED__SINK::_read155_func,channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in,"\x0\x0\x0\x0",true)
,_stop156(156,this)

{
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[3]=&size;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in->setBlockedReadTask(this);
    event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in->setBlockedReadTask(this);
    
    //command chaining
    _read155.setNextCommand(array(1,(TMLCommand*)&_stop156));
    _wait157.setNextCommand(array(1,(TMLCommand*)&_read155));
    _currCommand=&_wait157;
    _firstCommand=&_wait157;
    
    _channels[0] = channel__HOCAppMAPPED__join2_ch_out__HOCAppMAPPED__sink_ch_in;
    _channels[1] = event__HOCAppMAPPED__join2_evt_out__HOCAppMAPPED__sink_evt_in;
    refreshStateHash("\x6\x0\x0\x0");
}

Parameter* HOCAppMAPPED__SINK::_wait157_func(Parameter* ioParam){
    ioParam->getP(&size);
    return 0;
}

TMLLength HOCAppMAPPED__SINK::_read155_func(){
    return (TMLLength)(size);
}

std::istream& HOCAppMAPPED__SINK::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__SINK::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__SINK::reset(){
    TMLTask::reset();
    size=0;
}

HashValueType HOCAppMAPPED__SINK::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(size);
            _channels[0]->setSignificance(this, ((_liveVarList[0] & 2)!=0));
             _channels[1]->setSignificance(this, ((_liveVarList[0] & 4)!=0));
        }
    }
    return _stateHash.getHash();
}

