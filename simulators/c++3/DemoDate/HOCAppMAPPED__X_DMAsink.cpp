#include <HOCAppMAPPED__X_DMAsink.h>

HOCAppMAPPED__X_DMAsink::HOCAppMAPPED__X_DMAsink(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2
, TMLChannel* channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,i(0)
,arg1__req(0)
,_waitOnRequest(251,this,requestChannel,(ParamFuncPointer)&HOCAppMAPPED__X_DMAsink::waitOnRequest_func,"\x3c\x0\x0\x0",false)
,_action258(258,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAsink::_action258_func, "\x39\x0\x0\x0",false)
,_lpIncAc253(253,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAsink::_lpIncAc253_func, 0, false)
,_read257(257,this,0,channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in,"\x3b\x0\x0\x0",true,1)
,_execi252(252,this,(LengthFuncPointer)&HOCAppMAPPED__X_DMAsink::_execi252_func,0,1,"\x3b\x0\x0\x0",false)
,_write256(256,this,0,channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2,"\x3b\x0\x0\x0",true,1)
,_lpChoice253(253,this,(RangeFuncPointer)&HOCAppMAPPED__X_DMAsink::_lpChoice253_func,2,0, false)
,_action379(379,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAsink::_action379_func, 0, false)

{
    _comment = new std::string[3];
    _comment[0]=std::string("Action i = i-1");
    _comment[1]=std::string("Action i=size");
    _comment[2]=std::string("Action size = arg1__req");
    
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[54]=&size;
    _varLookUpName["i"]=&i;
    _varLookUpID[55]=&i;
    _varLookUpName["arg1__req"]=&arg1__req;
    _varLookUpID[145]=&arg1__req;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _lpIncAc253.setNextCommand(array(1,(TMLCommand*)&_lpChoice253));
    _write256.setNextCommand(array(1,(TMLCommand*)&_lpIncAc253));
    _execi252.setNextCommand(array(1,(TMLCommand*)&_write256));
    _read257.setNextCommand(array(1,(TMLCommand*)&_execi252));
    _lpChoice253.setNextCommand(array(2,(TMLCommand*)&_read257,(TMLCommand*)&_waitOnRequest));
    _action379.setNextCommand(array(1,(TMLCommand*)&_lpChoice253));
    _action258.setNextCommand(array(1,(TMLCommand*)&_action379));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action258));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__HOCAppMAPPED__DMAsink_ch_out__HOCAppMAPPED__join2_ch_in2;
    _channels[1] = channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in;
    _channels[2] = requestChannel;
    refreshStateHash("\x3c\x0\x0\x0");
}

Parameter* HOCAppMAPPED__X_DMAsink::waitOnRequest_func(Parameter* ioParam){
    ioParam->getP(&arg1__req);
    return 0;
}

void HOCAppMAPPED__X_DMAsink::_lpIncAc253_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    i = i-1;
}

TMLLength HOCAppMAPPED__X_DMAsink::_execi252_func(){
    return (TMLLength)(size*2);
}

unsigned int HOCAppMAPPED__X_DMAsink::_lpChoice253_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( i == 0 ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

void HOCAppMAPPED__X_DMAsink::_action379_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    i=size;
}

void HOCAppMAPPED__X_DMAsink::_action258_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    size = arg1__req;
}

std::istream& HOCAppMAPPED__X_DMAsink::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable size " << size << std::endl;
    #endif
    READ_STREAM(i_stream_var,i);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable i " << i << std::endl;
    #endif
    READ_STREAM(i_stream_var,arg1__req);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable arg1__req " << arg1__req << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__X_DMAsink::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable size " << size << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,i);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable i " << i << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,arg1__req);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable arg1__req " << arg1__req << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__X_DMAsink::reset(){
    TMLTask::reset();
    size=0;
    i=0;
    arg1__req=0;
}

HashValueType HOCAppMAPPED__X_DMAsink::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(size);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(i);
            if ((_liveVarList[0] & 4)!=0) _stateHash.addValue(arg1__req);
            _channels[0]->setSignificance(this, ((_liveVarList[0] & 8)!=0));
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 16)!=0));
             _channels[2]->setSignificance(this, ((_liveVarList[0] & 32)!=0));
        }
    }
    return _stateHash.getHash();
}

