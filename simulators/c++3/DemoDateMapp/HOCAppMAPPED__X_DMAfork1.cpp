#include <HOCAppMAPPED__X_DMAfork1.h>

HOCAppMAPPED__X_DMAfork1::HOCAppMAPPED__X_DMAfork1(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in
, TMLChannel* channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,i(0)
,arg1__req(0)
,_waitOnRequest(320,this,requestChannel,(ParamFuncPointer)&HOCAppMAPPED__X_DMAfork1::waitOnRequest_func,"\x3c\x0\x0\x0",false)
,_action327(327,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAfork1::_action327_func, "\x39\x0\x0\x0",false)
,_lpIncAc321(321,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAfork1::_lpIncAc321_func, 0, false)
,_read326(326,this,0,channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in,"\x3b\x0\x0\x0",true,1)
,_execi323(323,this,(LengthFuncPointer)&HOCAppMAPPED__X_DMAfork1::_execi323_func,0,1,"\x3b\x0\x0\x0",false)
,_write325(325,this,0,channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in,"\x3b\x0\x0\x0",true,1)
,_lpChoice321(321,this,(RangeFuncPointer)&HOCAppMAPPED__X_DMAfork1::_lpChoice321_func,2,0, false)
,_action396(396,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAfork1::_action396_func, 0, false)

{
    _comment = new std::string[3];
    _comment[0]=std::string("Action i = i-1");
    _comment[1]=std::string("Action i=size");
    _comment[2]=std::string("Action size = arg1__req");
    
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[96]=&size;
    _varLookUpName["i"]=&i;
    _varLookUpID[97]=&i;
    _varLookUpName["arg1__req"]=&arg1__req;
    _varLookUpID[163]=&arg1__req;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _lpIncAc321.setNextCommand(array(1,(TMLCommand*)&_lpChoice321));
    _write325.setNextCommand(array(1,(TMLCommand*)&_lpIncAc321));
    _execi323.setNextCommand(array(1,(TMLCommand*)&_write325));
    _read326.setNextCommand(array(1,(TMLCommand*)&_execi323));
    _lpChoice321.setNextCommand(array(2,(TMLCommand*)&_read326,(TMLCommand*)&_waitOnRequest));
    _action396.setNextCommand(array(1,(TMLCommand*)&_lpChoice321));
    _action327.setNextCommand(array(1,(TMLCommand*)&_action396));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action327));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in;
    _channels[1] = channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in;
    _channels[2] = requestChannel;
    refreshStateHash("\x3c\x0\x0\x0");
}

Parameter* HOCAppMAPPED__X_DMAfork1::waitOnRequest_func(Parameter* ioParam){
    ioParam->getP(&arg1__req);
    return 0;
}

void HOCAppMAPPED__X_DMAfork1::_lpIncAc321_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    i = i-1;
}

TMLLength HOCAppMAPPED__X_DMAfork1::_execi323_func(){
    return (TMLLength)(size);
}

unsigned int HOCAppMAPPED__X_DMAfork1::_lpChoice321_func(ParamType& oMin, ParamType& oMax){
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

void HOCAppMAPPED__X_DMAfork1::_action396_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    i=size;
}

void HOCAppMAPPED__X_DMAfork1::_action327_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    size = arg1__req;
}

std::istream& HOCAppMAPPED__X_DMAfork1::readObject(std::istream& i_stream_var){
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

std::ostream& HOCAppMAPPED__X_DMAfork1::writeObject(std::ostream& i_stream_var){
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

void HOCAppMAPPED__X_DMAfork1::reset(){
    TMLTask::reset();
    size=0;
    i=0;
    arg1__req=0;
}

HashValueType HOCAppMAPPED__X_DMAfork1::getStateHash(){
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

