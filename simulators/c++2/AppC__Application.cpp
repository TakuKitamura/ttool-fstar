#include <AppC__Application.h>

AppC__Application::AppC__Application(ID iID, Priority iPriority, std::string iName, FPGA** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__AppC__fromAtoT
, TMLChannel* channel__AppC__fromTtoA
, TMLEventChannel* event__AppC__abort__AppC__abort
, TMLEventChannel* event__AppC__close__AppC__close
, TMLEventChannel* event__AppC__connectionOpened__AppC__connectionOpened
, TMLEventChannel* event__AppC__open__AppC__open
, TMLEventChannel* event__AppC__opened__AppC__opened
, TMLEventChannel* event__AppC__receive_Application__AppC__receive_Application
, TMLEventChannel* event__AppC__send_TCP__AppC__send_TCP
, TMLEventChannel* request__AppC__start_TCP_IP
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,_waitOnRequest(272,this,requestChannel,0,"\x7c\x3\x0\x0",false)
,_send277(277,this,event__AppC__open__AppC__open,0,"\x7c\x3\x0\x0",true)
,_wait274(274,this,event__AppC__opened__AppC__opened,0,"\x7c\x3\x0\x0",true)
,_send275(275,this,event__AppC__connectionOpened__AppC__connectionOpened,0,"\x7c\x3\x0\x0",false)
,_execi273(273,this,0,0,10,"\x7c\x3\x0\x0",false)
,_write278(278,this,0,channel__AppC__fromAtoT,"\x7c\x3\x0\x0",true,1)
,_send279(279,this,event__AppC__send_TCP__AppC__send_TCP,0,"\x7c\x3\x0\x0",true)
,_send282(282,this,event__AppC__close__AppC__close,0,"\x7c\x3\x0\x0",true)
,_send281(281,this,event__AppC__abort__AppC__abort,0,"\x7c\x3\x0\x0",true)
,_choice276(276,this,(RangeFuncPointer)&AppC__Application::_choice276_func,2,"\x7c\x3\x0\x0",false)

{
    //generate task variable look-up table
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__AppC__fromAtoT->setBlockedWriteTask(this);
    channel__AppC__fromTtoA->setBlockedReadTask(this);
    event__AppC__abort__AppC__abort->setBlockedWriteTask(this);
    event__AppC__close__AppC__close->setBlockedWriteTask(this);
    event__AppC__connectionOpened__AppC__connectionOpened->setBlockedWriteTask(this);
    event__AppC__open__AppC__open->setBlockedWriteTask(this);
    event__AppC__opened__AppC__opened->setBlockedReadTask(this);
    event__AppC__receive_Application__AppC__receive_Application->setBlockedReadTask(this);
    event__AppC__send_TCP__AppC__send_TCP->setBlockedWriteTask(this);
    requestChannel->setBlockedReadTask(this);
    request__AppC__start_TCP_IP->setBlockedWriteTask(this);
    
    //command chaining
    _send282.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _send281.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _choice276.setNextCommand(array(2,(TMLCommand*)&_send282,(TMLCommand*)&_send281));
    _send279.setNextCommand(array(1,(TMLCommand*)&_choice276));
    _write278.setNextCommand(array(1,(TMLCommand*)&_send279));
    _execi273.setNextCommand(array(1,(TMLCommand*)&_write278));
    _send275.setNextCommand(array(1,(TMLCommand*)&_execi273));
    _wait274.setNextCommand(array(1,(TMLCommand*)&_send275));
    _send277.setNextCommand(array(1,(TMLCommand*)&_wait274));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_send277));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__AppC__fromAtoT;
    _channels[1] = channel__AppC__fromTtoA;
    _channels[2] = event__AppC__abort__AppC__abort;
    _channels[3] = event__AppC__close__AppC__close;
    _channels[4] = event__AppC__connectionOpened__AppC__connectionOpened;
    _channels[5] = event__AppC__open__AppC__open;
    _channels[6] = event__AppC__opened__AppC__opened;
    _channels[7] = event__AppC__receive_Application__AppC__receive_Application;
    _channels[8] = event__AppC__send_TCP__AppC__send_TCP;
    _channels[9] = requestChannel;
    refreshStateHash("\x7c\x3\x0\x0");
}

unsigned int AppC__Application::_choice276_func(ParamType& oMin, ParamType& oMax){
    oMin=0;
    oMax=1;
    return myrand(0, 1);
    
}

std::istream& AppC__Application::readObject(std::istream& i_stream_var){
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& AppC__Application::writeObject(std::ostream& i_stream_var){
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void AppC__Application::reset(){
    TMLTask::reset();
}

HashValueType AppC__Application::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
            _channels[1]->setSignificance(this, ((_liveVarList[0] & 2)!=0));
             _channels[4]->setSignificance(this, ((_liveVarList[0] & 16)!=0));
             _channels[6]->setSignificance(this, ((_liveVarList[0] & 64)!=0));
             _channels[7]->setSignificance(this, ((_liveVarList[0] & 128)!=0));
             _channels[9]->setSignificance(this, ((_liveVarList[1] & 2)!=0));
        }
    }
    return _stateHash.getHash();
}

