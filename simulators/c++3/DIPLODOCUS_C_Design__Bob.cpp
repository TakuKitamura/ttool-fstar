#include <DIPLODOCUS_C_Design__Bob.h>

DIPLODOCUS_C_Design__Bob::DIPLODOCUS_C_Design__Bob(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__DIPLODOCUS_C_Design__Phone
, TMLEventChannel* event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call
, TMLEventChannel* event__DIPLODOCUS_C_Design__comm__DIPLODOCUS_C_Design__comm
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,_send19(19,this,event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call,0,"\x0\x0\x0\x0",false)
,_write18(18,this,0,channel__DIPLODOCUS_C_Design__Phone,"\x0\x0\x0\x0",false,1)
,_stop20(20,this)

{
    //generate task variable look-up table
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__DIPLODOCUS_C_Design__Phone->setBlockedWriteTask(this);
    event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call->setBlockedWriteTask(this);
    event__DIPLODOCUS_C_Design__comm__DIPLODOCUS_C_Design__comm->setBlockedWriteTask(this);
    
    //command chaining
    _write18.setNextCommand(array(1,(TMLCommand*)&_stop20));
    _send19.setNextCommand(array(1,(TMLCommand*)&_write18));
    _currCommand=&_send19;
    _firstCommand=&_send19;
    
    _channels[0] = channel__DIPLODOCUS_C_Design__Phone;
    _channels[1] = event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call;
    _channels[2] = event__DIPLODOCUS_C_Design__comm__DIPLODOCUS_C_Design__comm;
    refreshStateHash("\x2\x0\x0\x0");
}

std::istream& DIPLODOCUS_C_Design__Bob::readObject(std::istream& i_stream_var){
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& DIPLODOCUS_C_Design__Bob::writeObject(std::ostream& i_stream_var){
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void DIPLODOCUS_C_Design__Bob::reset(){
    TMLTask::reset();
}

HashValueType DIPLODOCUS_C_Design__Bob::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
        }
    }
    return _stateHash.getHash();
}

