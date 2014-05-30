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
,_wait332(332,this,event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork2::_wait332_func,"\xe2\x6\x0\x0",true)
,_read329(329,this,(LengthFuncPointer)&HOCAppMAPPED__fork2::_read329_func,channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in,"\xc2\x6\x0\x0",true)
,_action353(353,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_action353_func, "\xca\x6\x0\x0",false)
,_action356(356,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_action356_func, "\xda\x6\x0\x0",false)
,_lpIncAc349(349,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_lpIncAc349_func, 0, false)
,_action354(354,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_action354_func, "\xde\x6\x0\x0",false)
,_send331(331,this,event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork2::_send331_func,"\xde\x6\x0\x0",true)
,_write335(335,this,(LengthFuncPointer)&HOCAppMAPPED__fork2::_write335_func,channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in,"\xde\x6\x0\x0",true)
,_action357(357,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_action357_func, "\xde\x6\x0\x0",false)
,_send330(330,this,event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork2::_send330_func,"\xde\x6\x0\x0",true)
,_write334(334,this,(LengthFuncPointer)&HOCAppMAPPED__fork2::_write334_func,channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in,"\xde\x6\x0\x0",true)
, _stop348(348,this)
,_choice348(348,this,(RangeFuncPointer)&HOCAppMAPPED__fork2::_choice348_func,3,"\xde\x6\x0\x0",false)
,_stop351(351,this)
,_lpChoice349(349,this,(RangeFuncPointer)&HOCAppMAPPED__fork2::_lpChoice349_func,2,0, false)
,_action388(388,this,(ActionFuncPointer)&HOCAppMAPPED__fork2::_action388_func, 0, false)

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
    _varLookUpID[92]=&PREX;
    _varLookUpName["r_size"]=&r_size;
    _varLookUpID[93]=&r_size;
    _varLookUpName["looprd__0"]=&looprd__0;
    _varLookUpID[350]=&looprd__0;
    _varLookUpName["rd__0__0"]=&rd__0__0;
    _varLookUpID[352]=&rd__0__0;
    _varLookUpName["rd__0__1"]=&rd__0__1;
    _varLookUpID[355]=&rd__0__1;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in->setBlockedReadTask(this);
    channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in->setBlockedReadTask(this);
    event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in->setBlockedWriteTask(this);
    
    //command chaining
    _lpIncAc349.setNextCommand(array(1,(TMLCommand*)&_lpChoice349));
    _write335.setNextCommand(array(1,(TMLCommand*)&_lpIncAc349));
    _send331.setNextCommand(array(1,(TMLCommand*)&_write335));
    _action354.setNextCommand(array(1,(TMLCommand*)&_send331));
    _write334.setNextCommand(array(1,(TMLCommand*)&_lpIncAc349));
    _send330.setNextCommand(array(1,(TMLCommand*)&_write334));
    _action357.setNextCommand(array(1,(TMLCommand*)&_send330));
    _choice348.setNextCommand(array(3,(TMLCommand*)&_action354,(TMLCommand*)&_action357,(TMLCommand*)&_stop348));
    _lpChoice349.setNextCommand(array(2,(TMLCommand*)&_choice348,(TMLCommand*)&_stop351));
    _action388.setNextCommand(array(1,(TMLCommand*)&_lpChoice349));
    _action356.setNextCommand(array(1,(TMLCommand*)&_action388));
    _action353.setNextCommand(array(1,(TMLCommand*)&_action356));
    _read329.setNextCommand(array(1,(TMLCommand*)&_action353));
    _wait332.setNextCommand(array(1,(TMLCommand*)&_read329));
    _currCommand=&_wait332;
    _firstCommand=&_wait332;
    
    _channels[0] = channel__HOCAppMAPPED__cwm1_ch_out__HOCAppMAPPED__fork2_ch_in;
    _channels[1] = channel__HOCAppMAPPED__fork2_ch_out1__HOCAppMAPPED__vsum_ch_in;
    _channels[2] = channel__HOCAppMAPPED__fork2_ch_out2__HOCAppMAPPED__cwm2_ch_in;
    _channels[3] = event__HOCAppMAPPED__cwm1_evt_out__HOCAppMAPPED__fork2_evt_in;
    _channels[4] = event__HOCAppMAPPED__fork2_evt_out1__HOCAppMAPPED__vsum_evt_in;
    _channels[5] = event__HOCAppMAPPED__fork2_evt_out2__HOCAppMAPPED__cwm2_evt_in;
    refreshStateHash("\xe0\x7\x0\x0");
}

Parameter* HOCAppMAPPED__fork2::_wait332_func(Parameter* ioParam){
    ioParam->getP(&r_size);
    return 0;
}

TMLLength HOCAppMAPPED__fork2::_read329_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork2::_lpIncAc349_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    looprd__0 = looprd__0 + 1;
}

Parameter* HOCAppMAPPED__fork2::_send331_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

TMLLength HOCAppMAPPED__fork2::_write335_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork2::_action354_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    rd__0__0 = true;
}

Parameter* HOCAppMAPPED__fork2::_send330_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

TMLLength HOCAppMAPPED__fork2::_write334_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork2::_action357_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    rd__0__1 = true;
}

unsigned int HOCAppMAPPED__fork2::_choice348_func(ParamType& oMin, ParamType& oMax){
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

unsigned int HOCAppMAPPED__fork2::_lpChoice349_func(ParamType& oMin, ParamType& oMax){
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

void HOCAppMAPPED__fork2::_action388_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,3));
    #endif
    looprd__0=0;
}

void HOCAppMAPPED__fork2::_action356_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,4));
    #endif
    rd__0__1 = false;
}

void HOCAppMAPPED__fork2::_action353_func(){
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

