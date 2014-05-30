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
,_wait227(227,this,event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork1::_wait227_func,"\xf1\x1\x0\x0",true)
,_read226(226,this,(LengthFuncPointer)&HOCAppMAPPED__fork1::_read226_func,channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in,"\xb1\x1\x0\x0",true)
,_action367(367,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_action367_func, "\xb5\x1\x0\x0",false)
,_action370(370,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_action370_func, "\xbd\x1\x0\x0",false)
,_lpIncAc363(363,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_lpIncAc363_func, 0, false)
,_action368(368,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_action368_func, "\xbf\x1\x0\x0",false)
,_send229(229,this,event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork1::_send229_func,"\xbf\x1\x0\x0",true)
,_write230(230,this,(LengthFuncPointer)&HOCAppMAPPED__fork1::_write230_func,channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in,"\xbf\x1\x0\x0",true)
,_action371(371,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_action371_func, "\xbf\x1\x0\x0",false)
,_send231(231,this,event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in,(ParamFuncPointer)&HOCAppMAPPED__fork1::_send231_func,"\xbf\x1\x0\x0",true)
,_write232(232,this,(LengthFuncPointer)&HOCAppMAPPED__fork1::_write232_func,channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in,"\xbf\x1\x0\x0",true)
, _stop362(362,this)
,_choice362(362,this,(RangeFuncPointer)&HOCAppMAPPED__fork1::_choice362_func,3,"\xbf\x1\x0\x0",false)
,_stop365(365,this)
,_lpChoice363(363,this,(RangeFuncPointer)&HOCAppMAPPED__fork1::_lpChoice363_func,2,0, false)
,_action387(387,this,(ActionFuncPointer)&HOCAppMAPPED__fork1::_action387_func, 0, false)

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
    _varLookUpID[46]=&r_size;
    _varLookUpName["looprd__0"]=&looprd__0;
    _varLookUpID[364]=&looprd__0;
    _varLookUpName["rd__0__0"]=&rd__0__0;
    _varLookUpID[366]=&rd__0__0;
    _varLookUpName["rd__0__1"]=&rd__0__1;
    _varLookUpID[369]=&rd__0__1;
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in->setBlockedWriteTask(this);
    channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in->setBlockedReadTask(this);
    event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in->setBlockedWriteTask(this);
    event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in->setBlockedReadTask(this);
    
    //command chaining
    _lpIncAc363.setNextCommand(array(1,(TMLCommand*)&_lpChoice363));
    _write230.setNextCommand(array(1,(TMLCommand*)&_lpIncAc363));
    _send229.setNextCommand(array(1,(TMLCommand*)&_write230));
    _action368.setNextCommand(array(1,(TMLCommand*)&_send229));
    _write232.setNextCommand(array(1,(TMLCommand*)&_lpIncAc363));
    _send231.setNextCommand(array(1,(TMLCommand*)&_write232));
    _action371.setNextCommand(array(1,(TMLCommand*)&_send231));
    _choice362.setNextCommand(array(3,(TMLCommand*)&_action368,(TMLCommand*)&_action371,(TMLCommand*)&_stop362));
    _lpChoice363.setNextCommand(array(2,(TMLCommand*)&_choice362,(TMLCommand*)&_stop365));
    _action387.setNextCommand(array(1,(TMLCommand*)&_lpChoice363));
    _action370.setNextCommand(array(1,(TMLCommand*)&_action387));
    _action367.setNextCommand(array(1,(TMLCommand*)&_action370));
    _read226.setNextCommand(array(1,(TMLCommand*)&_action367));
    _wait227.setNextCommand(array(1,(TMLCommand*)&_read226));
    _currCommand=&_wait227;
    _firstCommand=&_wait227;
    
    _channels[0] = channel__HOCAppMAPPED__fork1_ch_out1__HOCAppMAPPED__DMAsink_ch_in;
    _channels[1] = channel__HOCAppMAPPED__fork1_ch_out2__HOCAppMAPPED__DMAfork1_ch_in;
    _channels[2] = channel__HOCAppMAPPED__src_ch_out__HOCAppMAPPED__fork1_ch_in;
    _channels[3] = event__HOCAppMAPPED__fork1_evt_out1__HOCAppMAPPED__DMAsink_evt_in;
    _channels[4] = event__HOCAppMAPPED__fork1_evt_out2__HOCAppMAPPED__DMAfork1_evt_in;
    _channels[5] = event__HOCAppMAPPED__src_evt_out__HOCAppMAPPED__fork1_evt_in;
    refreshStateHash("\xf0\x3\x0\x0");
}

Parameter* HOCAppMAPPED__fork1::_wait227_func(Parameter* ioParam){
    ioParam->getP(&r_size);
    return 0;
}

TMLLength HOCAppMAPPED__fork1::_read226_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork1::_lpIncAc363_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,0));
    #endif
    looprd__0 = looprd__0 + 1;
}

Parameter* HOCAppMAPPED__fork1::_send229_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

TMLLength HOCAppMAPPED__fork1::_write230_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork1::_action368_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,1));
    #endif
    rd__0__0 = true;
}

Parameter* HOCAppMAPPED__fork1::_send231_func(Parameter* ioParam){
    return new SizedParameter<ParamType,1>(r_size);
}

TMLLength HOCAppMAPPED__fork1::_write232_func(){
    return (TMLLength)(r_size);
}

void HOCAppMAPPED__fork1::_action371_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,2));
    #endif
    rd__0__1 = true;
}

unsigned int HOCAppMAPPED__fork1::_choice362_func(ParamType& oMin, ParamType& oMax){
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

unsigned int HOCAppMAPPED__fork1::_lpChoice363_func(ParamType& oMin, ParamType& oMax){
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

void HOCAppMAPPED__fork1::_action387_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,3));
    #endif
    looprd__0=0;
}

void HOCAppMAPPED__fork1::_action370_func(){
    #ifdef ADD_COMMENTS
    addComment(new Comment(_endLastTransaction,0,4));
    #endif
    rd__0__1 = false;
}

void HOCAppMAPPED__fork1::_action367_func(){
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

