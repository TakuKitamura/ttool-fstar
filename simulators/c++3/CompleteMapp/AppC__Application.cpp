#include <AppC__Application.h>

AppC__Application::AppC__Application(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__AppC__fromAtoT
, TMLChannel* channel__AppC__fromTtoA
, TMLEventChannel* event__AppC__abort__AppC__abort
, TMLEventChannel* event__AppC__close__AppC__close
, TMLEventChannel* event__AppC__open__AppC__open
, TMLEventChannel* event__AppC__receive_Application__AppC__receive_Application
, TMLEventChannel* event__AppC__send_TCP__AppC__send_TCP
, TMLEventChannel* request__AppC__start_TCP_IP
, TMLEventChannel* requestChannel
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,_waitOnRequest(228,this,requestChannel,0,"\xdc\x0\x0\x0",false)
,_send230(230,this,event__AppC__open__AppC__open,0,"\xdc\x0\x0\x0",true)
,_write231(231,this,0,channel__AppC__fromAtoT,"\xdc\x0\x0\x0",true,1)
,_send232(232,this,event__AppC__send_TCP__AppC__send_TCP,0,"\xdc\x0\x0\x0",true)
,_send235(235,this,event__AppC__close__AppC__close,0,"\xdc\x0\x0\x0",true)
,_send234(234,this,event__AppC__abort__AppC__abort,0,"\xdc\x0\x0\x0",true)
,_choice229(229,this,(RangeFuncPointer)&AppC__Application::_choice229_func,2,"\xdc\x0\x0\x0",false)

{
    //generate task variable look-up table
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__AppC__fromAtoT->setBlockedWriteTask(this);
    channel__AppC__fromTtoA->setBlockedReadTask(this);
    event__AppC__abort__AppC__abort->setBlockedWriteTask(this);
    event__AppC__close__AppC__close->setBlockedWriteTask(this);
    event__AppC__open__AppC__open->setBlockedWriteTask(this);
    event__AppC__receive_Application__AppC__receive_Application->setBlockedReadTask(this);
    event__AppC__send_TCP__AppC__send_TCP->setBlockedWriteTask(this);
    requestChannel->setBlockedReadTask(this);
    request__AppC__start_TCP_IP->setBlockedWriteTask(this);
    
    //command chaining
    _send235.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _send234.setNextCommand(array(1,(TMLCommand*)&_waitOnRequest));
    _choice229.setNextCommand(array(2,(TMLCommand*)&_send235,(TMLCommand*)&_send234));
    _send232.setNextCommand(array(1,(TMLCommand*)&_choice229));
    _write231.setNextCommand(array(1,(TMLCommand*)&_send232));
    _send230.setNextCommand(array(1,(TMLCommand*)&_write231));
    _waitOnRequest.setNextCommand(array(1,(TMLCommand*)&_send230));
    _currCommand=&_waitOnRequest;
    _firstCommand=&_waitOnRequest;
    
    _channels[0] = channel__AppC__fromAtoT;
    _channels[1] = channel__AppC__fromTtoA;
    _channels[2] = event__AppC__abort__AppC__abort;
    _channels[3] = event__AppC__close__AppC__close;
    _channels[4] = event__AppC__open__AppC__open;
    _channels[5] = event__AppC__receive_Application__AppC__receive_Application;
    _channels[6] = event__AppC__send_TCP__AppC__send_TCP;
    _channels[7] = requestChannel;
    refreshStateHash("\xdc\x0\x0\x0");
}

unsigned int AppC__Application::_choice229_func(ParamType& oMin, ParamType& oMax){
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
             _channels[5]->setSignificance(this, ((_liveVarList[0] & 32)!=0));
             _channels[7]->setSignificance(this, ((_liveVarList[0] & 128)!=0));
        }
    }
    return _stateHash.getHash();
}

