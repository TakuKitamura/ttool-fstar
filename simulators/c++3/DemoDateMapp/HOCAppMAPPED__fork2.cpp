#include <HOCAppMAPPED__fork2.h>

HOCAppMAPPED__fork2::HOCAppMAPPED__fork2(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in
, TMLChannel* channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in
, TMLChannel* channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in
, TMLEventChannel* event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in
, TMLEventChannel* event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in
, TMLEventChannel* event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,PREX(1)
,r_size(0)
,looprd__0(0)
,rd__0__0(0)
,rd__0__1(0)
,_wait239(239,this,event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork2::_wait239_func,"\xe2\x6\x0\x0",true)
,_read236(236,this,(LengthFuncPointer)&HOCAppMAPPED__fork2::_read236_func,channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in,"\xc2\x6\x0\x0",true)
,_action377(377,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_action377_func, "\xca\x6\x0\x0",false)
,_action380(380,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_action380_func, "\xda\x6\x0\x0",false)
,_lpIncAc373(373,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_lpIncAc373_func, 0, false)
,_action378(378,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_action378_func, "\xde\x6\x0\x0",false)
,_send238(238,this,event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork2::_send238_func,"\xde\x6\x0\x0",true)
,_write242(242,this,(LengthFuncPointer)&HOCAppMAPPED__fork2::_write242_func,channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in,"\xde\x6\x0\x0",true)
,_action381(381,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_action381_func, "\xde\x6\x0\x0",false)
,_send237(237,this,event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork2::_send237_func,"\xde\x6\x0\x0",true)
,_write241(241,this,(LengthFuncPointer)&HOCAppMAPPED__fork2::_write241_func,channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in,"\xde\x6\x0\x0",true)
, _stop372(372,this)
,_choice372(372,this,(RangeFuncPointer)&HOCAppMAPPED__fork2::_choice372_func,3,"\xde\x6\x0\x0",false)
,_stop375(375,this)
,_lpChoice373(373,this,(RangeFuncPointer)&HOCAppMAPPED__fork2::_lpChoice373_func,2,0, false)
,_action390(390,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_action390_func, 0, false)

{
    _comment = new std::string[6];
    _comment[0]=std::string("Action looprd__0 = looprd__0 + 1");
    _comment[1]=std::string("Action rd__0__0 = true");
    _comment[2]=std::string("Action rd__0__1 = true");
    _comment[3]=std::string("Action looprd__0=0");
    _comment[4]=std::string("Action rd__0__1 = false");
    _comment[5]=std::string("Action rd__0__0 = false");
    
    //generate task variable look-up table
    _varLookUpName["PREX"]=&PREX;
    _varLookUpID[49]=&PREX;
    _varLookUpName["r_size"]=&r_size;
    _varLookUpID[50]=&r_size;
    _varLookUpName["looprd__0"]=&looprd__0;
    _varLookUpID[374]=&looprd__0;
    _varLookUpName["rd__0__0"]=&rd__0__0;
    _varLookUpID[376]=&rd__0__0;
    _varLookUpName["rd__0__1"]=&rd__0__1;
    _varLookUpID[379]=&rd__0__1;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in->setBlockedReadTask(this);
    channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in->setBlockedReadTask(this);
    event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in->setBlockedWriteTask(this);
    
    //command chaining
    _lpIncAc373.setNextCommand(array(1,(TMLCommand*)&_lpChoice373));
    _write242.setNextCommand(array(1,(TMLCommand*)&_lpIncAc373));
    _send238.setNextCommand(array(1,(TMLCommand*)&_write242));
    _action378.setNextCommand(array(1,(TMLCommand*)&_send238));
    _write241.setNextCommand(array(1,(TMLCommand*)&_lpIncAc373));
    _send237.setNextCommand(array(1,(TMLCommand*)&_write241));
    _action381.setNextCommand(array(1,(TMLCommand*)&_send237));
    _choice372.setNextCommand(array(3,(TMLCommand*)&_action378,(TMLCommand*)&_action381,(TMLCommand*)&_stop372));
    _lpChoice373.setNextCommand(array(2,(TMLCommand*)&_choice372,(TMLCommand*)&_stop375));
    _action390.setNextCommand(array(1,(TMLCommand*)&_lpChoice373));
    _action380.setNextCommand(array(1,(TMLCommand*)&_action390));
    _action377.setNextCommand(array(1,(TMLCommand*)&_action380));
    _read236.setNextCommand(array(1,(TMLCommand*)&_action377));
    _wait239.setNextCommand(array(1,(TMLCommand*)&_read236));
    _currCommand=&_wait239;
    _firstCommand=&_wait239;
    
    _channels[0] = channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in;
    _channels[1] = channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in;
    _channels[2] = channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in;
    _channels[3] = event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in;
    _channels[4] = event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in;
    _channels[5] = event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in;
    refreshStateHash("\xe0\x7\x0\x0");
}

Parameter* HOCAppMAPPED__fork2::_wait239_func(Parameter* ioParam){
    ioParam->getP(&r_size);
    return 0;
}

TMLLength HOCAppMAPPED__fork2::_read236_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork2::_lpIncAc373_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    looprd__0 = looprd__0 + 1;
}

Parameter* HOCAppMAPPED__fork2::_send238_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

TMLLength HOCAppMAPPED__fork2::_write242_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork2::_action378_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    rd__0__0 = true;
}

Parameter* HOCAppMAPPED__fork2::_send237_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

TMLLength HOCAppMAPPED__fork2::_write241_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork2::_action381_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    rd__0__1 = true;
}

unsigned int HOCAppMAPPED__fork2::_choice372_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if (!(rd__0__0)){
        oC++;
        oMax += 1;
        
    }
    if (!(rd__0__1)){
        oC++;
        oMax += 2;
        
    }
    if (oMax==0){
         oMax=4;
        return 2;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

unsigned int HOCAppMAPPED__fork2::_lpChoice373_func(ParamType& oMin, ParamType& oMax){
    unsigned int oC=0;
    oMin=-1;
    oMax=0;
    if ( looprd__0 < 2 ){
        oC++;
        oMax += 1;
        
    }
    if (oMax==0){
         oMax=2;
        return 1;
    }
    return getEnabledBranchNo(myrand(1,oC), oMax);
    
}

void HOCAppMAPPED__fork2::_action390_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,3));
    #endif
    looprd__0=0;
}

void HOCAppMAPPED__fork2::_action380_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,4));
    #endif
    rd__0__1 = false;
}

void HOCAppMAPPED__fork2::_action377_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,5));
    #endif
    rd__0__0 = false;
}

std::istream& HOCAppMAPPED__fork2::readObject(std::istream& i_stream_var){
    READ_STREAM(i_stream_var,PREX);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable PREX " << PREX << std::endl;
    #endif
    READ_STREAM(i_stream_var,r_size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable r_size " << r_size << std::endl;
    #endif
    READ_STREAM(i_stream_var,looprd__0);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable looprd__0 " << looprd__0 << std::endl;
    #endif
    READ_STREAM(i_stream_var,rd__0__0);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable rd__0__0 " << rd__0__0 << std::endl;
    #endif
    READ_STREAM(i_stream_var,rd__0__1);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Read: Variable rd__0__1 " << rd__0__1 << std::endl;
    #endif
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& HOCAppMAPPED__fork2::writeObject(std::ostream& i_stream_var){
    WRITE_STREAM(i_stream_var,PREX);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable PREX " << PREX << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,r_size);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable r_size " << r_size << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,looprd__0);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable looprd__0 " << looprd__0 << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,rd__0__0);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable rd__0__0 " << rd__0__0 << std::endl;
    #endif
    WRITE_STREAM(i_stream_var,rd__0__1);
    #ifdef DEBUG_SERIALIZE
    std::cout << "Write: Variable rd__0__1 " << rd__0__1 << std::endl;
    #endif
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void HOCAppMAPPED__fork2::reset(){
    TMLTask::reset();
    PREX=1;
    r_size=0;
    looprd__0=0;
    rd__0__0=0;
    rd__0__1=0;
}

HashValueType HOCAppMAPPED__fork2::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(PREX);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(r_size);
            if ((_liveVarList[0] & 4)!=0) _stateHash.addValue(looprd__0);
            if ((_liveVarList[0] & 8)!=0) _stateHash.addValue(rd__0__0);
            if ((_liveVarList[0] & 16)!=0) _stateHash.addValue(rd__0__1);
            _channels[0]->setSignificance(this, ((_liveVarList[0] & 32)!=0));
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 64)!=0));
            _channels[2]->setSignificance(this, ((_liveVarList[0] & 128)!=0));
             _channels[3]->setSignificance(this, ((_liveVarList[1] & 1)!=0));
        }
    }
    return _stateHash.getHash();
}

