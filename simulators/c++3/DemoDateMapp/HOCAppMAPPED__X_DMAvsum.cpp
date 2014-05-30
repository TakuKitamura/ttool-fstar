#include <HOCAppMAPPED__X_DMAvsum.h>

HOCAppMAPPED__X_DMAvsum::HOCAppMAPPED__X_DMAvsum(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1
, TMLChannel* channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,size(0)
,i(0)
,arg1__req(0)
,_waitOnRequest(336,this,requestChannel,(ParamFuncPointer)&HOCAppMAPPED__X_DMAvsum::waitOnRequest_func,"\x3c\x0\x0\x0",false)
,_action343(343,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAvsum::_action343_func, "\x39\x0\x0\x0",false)
,_lpIncAc338(338,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAvsum::_lpIncAc338_func, 0, false)
,_read342(342,this,(LengthFuncPointer)&HOCAppMAPPED__X_DMAvsum::_read342_func,channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in,"\x3b\x0\x0\x0",true)
,_execi339(339,this,(LengthFuncPointer)&HOCAppMAPPED__X_DMAvsum::_execi339_func,0,1,"\x3b\x0\x0\x0",false)
,_write341(341,this,(LengthFuncPointer)&HOCAppMAPPED__X_DMAvsum::_write341_func,channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1,"\x3b\x0\x0\x0",true)
,_lpChoice338(338,this,(RangeFuncPointer)&HOCAppMAPPED__X_DMAvsum::_lpChoice338_func,2,0, false)
,_action402(402,this,(ActionFuncPointer)&HOCAppMAPPED__X_DMAvsum::_action402_func, 0, false)

{
    _comment = new std::string[3];
    _comment[0]=std::string("Action i = i-1");
    _comment[1]=std::string("Action i=size");
    _comment[2]=std::string("Action size = arg1__req");
    
    //generate task variable look-up table
    _varLookUpName["size"]=&size;
    _varLookUpID[104]=&size;
    _varLookUpName["i"]=&i;
    _varLookUpID[105]=&i;
    _varLookUpName["arg1__req"]=&arg1__req;
    _varLookUpID[167]=&arg1__req;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in->setBlockedReadTask(this);
    requestChannel->setBlockedReadTask(this);
    
    //command chaining
    _lpIncAc338.setNextCommand(array(1,(TMLCommand*)&_lpChoice338));
    _write341.setNextCommand(array(1,(TMLCommand*)&_lpIncAc338));
    _execi339.setNextCommand(array(1,(TMLCommand*)&_write341));
    _read342.setNextCommand(array(1,(TMLCommand*)&_execi339));
    _lpChoice338.setNextCommand(array(2,(TMLCommand*)&_read342,(TMLCommand*)&_waitOnRequest));
    _action402.setNextCommand(array(1,(TMLCommand*)&_lpChoice338));
    _action343.setNextCommand(array(1,(TMLCommand*)&_action402));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_action343));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__HOCAppMAPPED__DMAvsum_ch_out__HOCAppMAPPED__join1_ch_in1;
    _channels[1] = channel__HOCAppMAPPED__vsum_ch_out__HOCAppMAPPED__DMAvsum_ch_in;
    _channels[2] = requestChannel;
    refreshStateHash("\x3c\x0\x0\x0");
}

Parameter* HOCAppMAPPED__X_DMAvsum::waitOnRequest_func(Parameter* ioParam){
    ioParam->getP(&arg1__req);
    return 0;
}

void HOCAppMAPPED__X_DMAvsum::_lpIncAc338_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    i = i-1;
}

TMLLength HOCAppMAPPED__X_DMAvsum::_read342_func(){
    return (TMLLength)(size);
}

TMLLength HOCAppMAPPED__X_DMAvsum::_execi339_func(){
    return (TMLLength)(size*2);
}

TMLLength HOCAppMAPPED__X_DMAvsum::_write341_func(){
    return (TMLLength)(size);
}

unsigned int HOCAppMAPPED__X_DMAvsum::_lpChoice338_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( i==0 ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

void HOCAppMAPPED__X_DMAvsum::_action402_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    i=size;
}

void HOCAppMAPPED__X_DMAvsum::_action343_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    size = arg1__req;
}

std::istream& HOCAppMAPPED__X_DMAvsum::readObject(std::istream& i_stream_var){
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

std::ostream& HOCAppMAPPED__X_DMAvsum::writeObject(std::ostream& i_stream_var){
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

void HOCAppMAPPED__X_DMAvsum::reset(){
    TMLTask::reset();
    size=0;
    i=0;
    arg1__req=0;
}

HashValueType HOCAppMAPPED__X_DMAvsum::getStateHash(){
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

