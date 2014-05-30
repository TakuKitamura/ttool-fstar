#include <DIPLODOCUS_C_Design__Alice.h>

DIPLODOCUS_C_Design__Alice::DIPLODOCUS_C_Design__Alice(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs
, TMLChannel* channel__DIPLODOCUS_C_Design__Phone
, TMLEventChannel* event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call
):TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)
,_wait13(13,this,event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call,0,"\x0\x0\x0\x0",false)
,_read12(12,this,0,channel__DIPLODOCUS_C_Design__Phone,"\x0\x0\x0\x0",false,1)
,_stop14(14,this)

{
    //generate task variable look-up table
    _varLookUpName["rnd__0"]=&rnd__0;
    
    //set blocked read task/set blocked write task
    channel__DIPLODOCUS_C_Design__Phone->setBlockedReadTask(this);
    event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call->setBlockedReadTask(this);
    
    //command chaining
    _read12.setNextCommand(array(1,(TMLCommand*)&_stop14));
    _wait13.setNextCommand(array(1,(TMLCommand*)&_read12));
    _currCommand=&_wait13;
    _firstCommand=&_wait13;
    
    _channels[0] = channel__DIPLODOCUS_C_Design__Phone;
    _channels[1] = event__DIPLODOCUS_C_Design__Call__DIPLODOCUS_C_Design__Call;
    refreshStateHash("\x2\x0\x0\x0");
}

std::istream& DIPLODOCUS_C_Design__Alice::readObject(std::istream& i_stream_var){
    TMLTask::readObject(i_stream_var);
    return i_stream_var;
}

std::ostream& DIPLODOCUS_C_Design__Alice::writeObject(std::ostream& i_stream_var){
    TMLTask::writeObject(i_stream_var);
    return i_stream_var;
}

void DIPLODOCUS_C_Design__Alice::reset(){
    TMLTask::reset();
}

HashValueType DIPLODOCUS_C_Design__Alice::getStateHash(){
    if(_hashInvalidated){
        _hashInvalidated=false;
        _stateHash.init((HashValueType)_ID,30);
        if(_liveVarList!=0){
             _channels[1]->setSignificance(this, ((_liveVarList[0] & 2)!=0));
        }
    }
    return _stateHash.getHash();
}

