#include <HOCAppMAPPED__X_acc.h>

HOCAppMAPPED__X_acc::HOCAppMAPPED__X_acc(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1
, TMLChannel* channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,arg1__req(0)
,_waitOnRequest(273,this,requestChannel,(ParamFuncPointer)&HOCAppMAPPED__X_acc::waitOnRequest_func,"\x1e\x0\x0\x0",false)
,_action278(278,this,(ActionFuncPointer)&HOCAppMAPPED__X_acc::_action278_func, "\x1d\x0\x0\x0",false)
,_read277(277,this,(LengthFuncPointer)&HOCAppMAPPED__X_acc::_read277_func,channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in,"\x1d\x0\x0\x0",true)
,_execi274(274,this,(LengthFuncPointer)&HOCAppMAPPED__X_acc::_execi274_func,0,1,"\x1d\x0\x0\x0",false)
,_write276(276,this,(LengthFuncPointer)&HOCAppMAPPED__X_acc::_write276_func,channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1,"\x1c\x0\x0\x0",true)

{
    _comment = new std::string[1];
    _comment[0]=std::string("Action size = arg1__req");
    
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[71]=&size;
    _varLookUpName["arg1__req"]=&arg1__req;
    _varLookUpID[149]=&arg1__req;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _write276.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _execi274.setNextCommand(array(1,(TMLCommand*)&_write276));
    _read277.setNextCommand(array(1,(TMLCommand*)&_execi274));
    _action278.setNextCommand(array(1,(TMLCommand*)&_read277));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action278));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__HOCAppMAPPED__acc_ch_out__HOCAppMAPPED__join2_ch_in1;
    _channels[1] = channel__HOCAppMAPPED__join1_ch_out__HOCAppMAPPED__acc_ch_in;
    _channels[2] = requestChannel;
    refreshStateHash("\x1e\x0\x0\x0");
}

Parameter* HOCAppMAPPED__X_acc::waitOnRequest_func(Parameter* ioParam){
    ioParam->getP(&arg1__req);
    return 0;
}

TMLLength HOCAppMAPPED__X_acc::_read277_func(){
    return (TMLLength)(size);
}

TMLLength HOCAppMAPPED__X_acc::_execi274_func(){
    return (TMLLength)(size);
}

TMLLength HOCAppMAPPED__X_acc::_write276_func(){
    return (TMLLength)(size);
}

void HOCAppMAPPED__X_acc::_action278_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    size = arg1__req;
}

std::istream& HOCAppMAPPED__X_acc::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    READ_STREAM(i_stream_var,arg1__req);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable arg1__req " << arg1__req << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__X_acc::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,arg1__req);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable arg1__req " << arg1__req << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__X_acc::reset(){
    TMLTask::reset();
    size=0;
    arg1__req=0;
}

HashValueType HOCAppMAPPED__X_acc::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(size);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(arg1__req);
            _channels[0]->setSignificance(this, ((_liveVarList[0] & 4)!=0));
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 8)!=0));
             _channels[2]->setSignificance(this, ((_liveVarList[0] & 16)!=0));
        }
    }
    return _stateHash.getHash();
}

