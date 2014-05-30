#include <HOCAppMAPPED__fork1.h>

HOCAppMAPPED__fork1::HOCAppMAPPED__fork1(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in
, TMLChannel* channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in
, TMLChannel* channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in
, TMLEventChannel* event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in
, TMLEventChannel* event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in
, TMLEventChannel* event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,r_size(0)
,looprd__0(0)
,rd__0__0(0)
,rd__0__1(0)
,_wait206(206,this,event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork1::_wait206_func,"\xf1\x1\x0\x0",true)
,_read205(205,this,(LengthFuncPointer)&HOCAppMAPPED__fork1::_read205_func,channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in,"\xb1\x1\x0\x0",true)
,_action343(343,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_action343_func, "\xb5\x1\x0\x0",false)
,_action346(346,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_action346_func, "\xbd\x1\x0\x0",false)
,_lpIncAc339(339,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_lpIncAc339_func, 0, false)
,_action344(344,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_action344_func, "\xbf\x1\x0\x0",false)
,_send208(208,this,event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork1::_send208_func,"\xbf\x1\x0\x0",true)
,_write209(209,this,(LengthFuncPointer)&HOCAppMAPPED__fork1::_write209_func,channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in,"\xbf\x1\x0\x0",true)
,_action347(347,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_action347_func, "\xbf\x1\x0\x0",false)
,_send210(210,this,event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork1::_send210_func,"\xbf\x1\x0\x0",true)
,_write211(211,this,(LengthFuncPointer)&HOCAppMAPPED__fork1::_write211_func,channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in,"\xbf\x1\x0\x0",true)
, _stop338(338,this)
,_choice338(338,this,(RangeFuncPointer)&HOCAppMAPPED__fork1::_choice338_func,3,"\xbf\x1\x0\x0",false)
,_stop341(341,this)
,_lpChoice339(339,this,(RangeFuncPointer)&HOCAppMAPPED__fork1::_lpChoice339_func,2,0, false)
,_action385(385,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_action385_func, 0, false)

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
    _varLookUpID[30]=&r_size;
    _varLookUpName["looprd__0"]=&looprd__0;
    _varLookUpID[340]=&looprd__0;
    _varLookUpName["rd__0__0"]=&rd__0__0;
    _varLookUpID[342]=&rd__0__0;
    _varLookUpName["rd__0__1"]=&rd__0__1;
    _varLookUpID[345]=&rd__0__1;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in->setBlockedReadTask(this);
    event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in->setBlockedReadTask(this);
    
    //command chaining
    _lpIncAc339.setNextCommand(array(1,(TMLCommand*)&_lpChoice339));
    _write209.setNextCommand(array(1,(TMLCommand*)&_lpIncAc339));
    _send208.setNextCommand(array(1,(TMLCommand*)&_write209));
    _action344.setNextCommand(array(1,(TMLCommand*)&_send208));
    _write211.setNextCommand(array(1,(TMLCommand*)&_lpIncAc339));
    _send210.setNextCommand(array(1,(TMLCommand*)&_write211));
    _action347.setNextCommand(array(1,(TMLCommand*)&_send210));
    _choice338.setNextCommand(array(3,(TMLCommand*)&_action344,(TMLCommand*)&_action347,(TMLCommand*)&_stop338));
    _lpChoice339.setNextCommand(array(2,(TMLCommand*)&_choice338,(TMLCommand*)&_stop341));
    _action385.setNextCommand(array(1,(TMLCommand*)&_lpChoice339));
    _action346.setNextCommand(array(1,(TMLCommand*)&_action385));
    _action343.setNextCommand(array(1,(TMLCommand*)&_action346));
    _read205.setNextCommand(array(1,(TMLCommand*)&_action343));
    _wait206.setNextCommand(array(1,(TMLCommand*)&_read205));
    _currCommand=&_wait206;
    _firstCommand=&_wait206;
    
    _channels[0] = channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in;
    _channels[1] = channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in;
    _channels[2] = channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in;
    _channels[3] = event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in;
    _channels[4] = event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in;
    _channels[5] = event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in;
    refreshStateHash("\xf0\x3\x0\x0");
}

Parameter* HOCAppMAPPED__fork1::_wait206_func(Parameter* ioParam){
    ioParam->getP(&r_size);
    return 0;
}

TMLLength HOCAppMAPPED__fork1::_read205_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork1::_lpIncAc339_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    looprd__0 = looprd__0 + 1;
}

Parameter* HOCAppMAPPED__fork1::_send208_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

TMLLength HOCAppMAPPED__fork1::_write209_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork1::_action344_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    rd__0__0 = true;
}

Parameter* HOCAppMAPPED__fork1::_send210_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

TMLLength HOCAppMAPPED__fork1::_write211_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork1::_action347_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    rd__0__1 = true;
}

unsigned int HOCAppMAPPED__fork1::_choice338_func(ParamType& oMin, ParamType& oMax){
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

unsigned int HOCAppMAPPED__fork1::_lpChoice339_func(ParamType& oMin, ParamType& oMax){
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

void HOCAppMAPPED__fork1::_action385_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,3));
    #endif
    looprd__0=0;
}

void HOCAppMAPPED__fork1::_action346_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,4));
    #endif
    rd__0__1 = false;
}

void HOCAppMAPPED__fork1::_action343_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,5));
    #endif
    rd__0__0 = false;
}

std::istream& HOCAppMAPPED__fork1::readObject(std::istream& i_stream_var){
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

std::ostream& HOCAppMAPPED__fork1::writeObject(std::ostream& i_stream_var){
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

void HOCAppMAPPED__fork1::reset(){
    TMLTask::reset();
    r_size=0;
    looprd__0=0;
    rd__0__0=0;
    rd__0__1=0;
}

HashValueType HOCAppMAPPED__fork1::getStateHash(){
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
             _channels[5]->setSignificance(this, ((_liveVarList[1] & 2)!=0));
        }
    }
    return _stateHash.getHash();
}

