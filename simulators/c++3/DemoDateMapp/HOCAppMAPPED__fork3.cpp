#include <HOCAppMAPPED__fork3.h>

HOCAppMAPPED__fork3::HOCAppMAPPED__fork3(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in
, TMLChannel* channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in
, TMLChannel* channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in
, TMLEventChannel* event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in
, TMLEventChannel* event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in
, TMLEventChannel* event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,r_size(0)
,looprd__0(0)
,rd__0__0(0)
,rd__0__1(0)
,_wait177(177,this,event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork3::_wait177_func,"\x71\x3\x0\x0",true)
,_read175(175,this,(LengthFuncPointer)&HOCAppMAPPED__fork3::_read175_func,channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in,"\x61\x3\x0\x0",true)
,_action357(357,this,(ActionFuncPointer)&HOCAppMAPPED__fork3::_action357_func, "\x65\x3\x0\x0",false)
,_action360(360,this,(ActionFuncPointer)&HOCAppMAPPED__fork3::_action360_func, "\x6d\x3\x0\x0",false)
,_lpIncAc353(353,this,(ActionFuncPointer)&HOCAppMAPPED__fork3::_lpIncAc353_func, 0, false)
,_action358(358,this,(ActionFuncPointer)&HOCAppMAPPED__fork3::_action358_func, "\x6f\x3\x0\x0",false)
,_send176(176,this,event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork3::_send176_func,"\x6f\x3\x0\x0",true)
,_write179(179,this,(LengthFuncPointer)&HOCAppMAPPED__fork3::_write179_func,channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in,"\x6f\x3\x0\x0",true)
,_action361(361,this,(ActionFuncPointer)&HOCAppMAPPED__fork3::_action361_func, "\x6f\x3\x0\x0",false)
,_send174(174,this,event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork3::_send174_func,"\x6f\x3\x0\x0",true)
,_write173(173,this,(LengthFuncPointer)&HOCAppMAPPED__fork3::_write173_func,channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in,"\x6f\x3\x0\x0",true)
, _stop352(352,this)
,_choice352(352,this,(RangeFuncPointer)&HOCAppMAPPED__fork3::_choice352_func,3,"\x6f\x3\x0\x0",false)
,_stop355(355,this)
,_lpChoice353(353,this,(RangeFuncPointer)&HOCAppMAPPED__fork3::_lpChoice353_func,2,0, false)
,_action384(384,this,(ActionFuncPointer)&HOCAppMAPPED__fork3::_action384_func, 0, false)

{
    _comment = new std::string[6];
    _comment[0]=std::string("Action looprd__0 = looprd__0 + 1");
    _comment[1]=std::string("Action rd__0__0 = true");
    _comment[2]=std::string("Action rd__0__1 = true");
    _comment[3]=std::string("Action looprd__0=0");
    _comment[4]=std::string("Action rd__0__1 = false");
    _comment[5]=std::string("Action rd__0__0 = false");
    
    //generate task variable look-up table
    _varLookUpName["r_size"]=&r_size;
    _varLookUpID[19]=&r_size;
    _varLookUpName["looprd__0"]=&looprd__0;
    _varLookUpID[354]=&looprd__0;
    _varLookUpName["rd__0__0"]=&rd__0__0;
    _varLookUpID[356]=&rd__0__0;
    _varLookUpName["rd__0__1"]=&rd__0__1;
    _varLookUpID[359]=&rd__0__1;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in->setBlockedReadTask(this);
    channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in->setBlockedReadTask(this);
    event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in->setBlockedWriteTask(this);
    
    //command chaining
    _lpIncAc353.setNextCommand(array(1,(TMLCommand*)&_lpChoice353));
    _write179.setNextCommand(array(1,(TMLCommand*)&_lpIncAc353));
    _send176.setNextCommand(array(1,(TMLCommand*)&_write179));
    _action358.setNextCommand(array(1,(TMLCommand*)&_send176));
    _write173.setNextCommand(array(1,(TMLCommand*)&_lpIncAc353));
    _send174.setNextCommand(array(1,(TMLCommand*)&_write173));
    _action361.setNextCommand(array(1,(TMLCommand*)&_send174));
    _choice352.setNextCommand(array(3,(TMLCommand*)&_action358,(TMLCommand*)&_action361,(TMLCommand*)&_stop352));
    _lpChoice353.setNextCommand(array(2,(TMLCommand*)&_choice352,(TMLCommand*)&_stop355));
    _action384.setNextCommand(array(1,(TMLCommand*)&_lpChoice353));
    _action360.setNextCommand(array(1,(TMLCommand*)&_action384));
    _action357.setNextCommand(array(1,(TMLCommand*)&_action360));
    _read175.setNextCommand(array(1,(TMLCommand*)&_action357));
    _wait177.setNextCommand(array(1,(TMLCommand*)&_read175));
    _currCommand=&_wait177;
    _firstCommand=&_wait177;
    
    _channels[0] = channel__HOCAppMAPPED__DMAfork1_ch_out__HOCAppMAPPED__fork3_ch_in;
    _channels[1] = channel__HOCAppMAPPED__fork3_ch_out1__HOCAppMAPPED__cwm1_ch_in;
    _channels[2] = channel__HOCAppMAPPED__fork3_ch_out2__HOCAppMAPPED__cws_ch_in;
    _channels[3] = event__HOCAppMAPPED__DMAfork1_evt_out__HOCAppMAPPED__fork3_evt_in;
    _channels[4] = event__HOCAppMAPPED__fork3_evt_out1__HOCAppMAPPED__cwm1_evt_in;
    _channels[5] = event__HOCAppMAPPED__fork3_evt_out2__HOCAppMAPPED__cws_evt_in;
    refreshStateHash("\xf0\x3\x0\x0");
}

Parameter* HOCAppMAPPED__fork3::_wait177_func(Parameter* ioParam){
    ioParam->getP(&r_size);
    return 0;
}

TMLLength HOCAppMAPPED__fork3::_read175_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork3::_lpIncAc353_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    looprd__0 = looprd__0 + 1;
}

Parameter* HOCAppMAPPED__fork3::_send176_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

TMLLength HOCAppMAPPED__fork3::_write179_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork3::_action358_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    rd__0__0 = true;
}

Parameter* HOCAppMAPPED__fork3::_send174_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

TMLLength HOCAppMAPPED__fork3::_write173_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork3::_action361_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    rd__0__1 = true;
}

unsigned int HOCAppMAPPED__fork3::_choice352_func(ParamType& oMin, ParamType& oMax){
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

unsigned int HOCAppMAPPED__fork3::_lpChoice353_func(ParamType& oMin, ParamType& oMax){
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

void HOCAppMAPPED__fork3::_action384_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,3));
    #endif
    looprd__0=0;
}

void HOCAppMAPPED__fork3::_action360_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,4));
    #endif
    rd__0__1 = false;
}

void HOCAppMAPPED__fork3::_action357_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,5));
    #endif
    rd__0__0 = false;
}

std::istream& HOCAppMAPPED__fork3::readObject(std::istream& i_stream_var){
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

std::ostream& HOCAppMAPPED__fork3::writeObject(std::ostream& i_stream_var){
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

void HOCAppMAPPED__fork3::reset(){
    TMLTask::reset();
    r_size=0;
    looprd__0=0;
    rd__0__0=0;
    rd__0__1=0;
}

HashValueType HOCAppMAPPED__fork3::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            if ((_liveVarList[0] & 1)!=0) _stateHash.addValue(r_size);
            if ((_liveVarList[0] & 2)!=0) _stateHash.addValue(looprd__0);
            if ((_liveVarList[0] & 4)!=0) _stateHash.addValue(rd__0__0);
            if ((_liveVarList[0] & 8)!=0) _stateHash.addValue(rd__0__1);
            _channels[0]->setSignificance(this, ((_liveVarList[0] & 16)!=0));
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 32)!=0));
            _channels[2]->setSignificance(this, ((_liveVarList[0] & 64)!=0));
             _channels[3]->setSignificance(this, ((_liveVarList[0] & 128)!=0));
        }
    }
    return _stateHash.getHash();
}

