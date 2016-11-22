/*Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Daniel Knorreck,
  Ludovic Apvrille, Renaud Pacalet
  *
  * ludovic.apvrille AT telecom-paristech.fr
  *
  * This software is a computer program whose purpose is to allow the
  * edition of TURTLE analysis, design and deployment diagrams, to
  * allow the generation of RT-LOTOS or Java code from this diagram,
  * and at last to allow the analysis of formal validation traces
  * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
  * from INRIA Rhone-Alpes.
  *
  * This software is governed by the CeCILL  license under French law and
  * abiding by the rules of distribution of free software.  You can  use,
  * modify and/ or redistribute the software under the terms of the CeCILL
  * license as circulated by CEA, CNRS and INRIA at the following URL
  * "http://www.cecill.info".
  *
  * As a counterpart to the access to the source code and  rights to copy,
  * modify and redistribute granted by the license, users are provided only
  * with a limited warranty  and the software's author,  the holder of the
  * economic rights,  and the successive licensors  have only  limited
  * liability.
  *
  * In this respect, the user's attention is drawn to the risks associated
  * with loading,  using,  modifying and/or developing or reproducing the
  * software by the user in light of its specific status of free software,
  * that may mean  that it is complicated to manipulate,  and  that  also
  * therefore means  that it is reserved for developers  and  experienced
  * professionals having in-depth computer knowledge. Users are therefore
  * encouraged to load and test the software's suitability as regards their
  * requirements in conditions enabling the security of their systems and/or
  * data to be ensured and,  more generally, to use and operate it in the
  * same conditions as regards security.
  *
  * The fact that you are presently reading this means that you have had
  * knowledge of the CeCILL license and that you accept its terms.
  *
  */

#include <TMLTransaction.h>
#include <TMLTask.h>
#include <CPU.h>
#include <TMLCommand.h>

MemPoolNoDel<TMLTransaction> TMLTransaction::memPool(BLOCK_SIZE_TRANS);


TMLTransaction::TMLTransaction():_runnableTime(0), _startTime(0), _length(0), _virtualLength(0), _command(0),
#ifdef PENALTIES_ENABLED
                                 _idlePenalty(0), _taskSwitchingPenalty(0), //, _branchingPenalty(0),
#endif
                                 _channel(0),_stateID(0) {
  //std::cout << "**** new TMLTransaction : set starttime=" << _startTime << "\n"; 
  
}

TMLTransaction::TMLTransaction(TMLCommand* iCommand, TMLLength iVirtualLength, TMLTime iRunnableTime, TMLChannel* iChannel):_runnableTime(iRunnableTime), _startTime(0), _length(0), _virtualLength(iVirtualLength), _command(iCommand),
#ifdef PENALTIES_ENABLED
                                                                                                                            _idlePenalty(0), _taskSwitchingPenalty(0), //, _branchingPenalty(0),
#endif
                                                                                                                            _channel(iChannel),_stateID(0) {
  //std::cout << "**** new TMLTransaction : set starttime=" << _startTime << "\n"; 
}

std::string TMLTransaction::printEnd() const{
  std::ostringstream outp;
  outp << getEndTime();
  return outp.str();
}

std::string TMLTransaction::toString() const{
  std::ostringstream outp;
  outp << _command->toString() << std::endl << "Transaction runnable:" << _runnableTime << " len:" << _length << " start:" << _startTime << " vLength:" << _virtualLength;
  if (_channel!=0) outp << " Ch: " << _channel->toShortString();
  return outp.str();
}

std::string TMLTransaction::toShortString() const{
  std::ostringstream outp;
  if (_command==0)
    outp << "System State ID: " <<  _virtualLength;
  else{
    outp << _command->toShortString() << " t:" << _startTime << " l:" << _length << " (vl:"<<  _virtualLength << ")";
    if (_channel!=0) outp << " Ch: " << _channel->toShortString();
  }
  return outp.str();
}


void TMLTransaction::toXML(std::ostringstream& glob, int deviceID, std::string deviceName) const {
  if (_command==0) {
    glob << TAG_TRANSo << " deviceid=\"" << deviceID << "\" devicename=\"" << deviceName << "\" id=\"" << _command->getID() << "\" command=\"0\"";
  } else {
    glob << TAG_TRANSo << " deviceid=\"" << deviceID << "\" devicename=\"" << deviceName << "\" command=\"" << _command->toShortString() << "\"";
    std::cout << "Info transaction:" <<  " starttime=\"" << _startTime << "\" endtime=\"" << getEndTime() << " length" << _length << "\" virtuallength=" <<  _virtualLength << " getStartTime:" << getStartTime() << "\n"; 
    glob << " starttime=\"" << _startTime << "\" endtime=\"" << getEndTime() << "\" length=\"" << _length << "\" virtuallength=\"" <<  _virtualLength << "\" id=\"" << _command->getID() << "\""; 
    if (_channel!=0) glob << " ch=\"" << _channel->toShortString() << "\"";
  }


  
  glob <<  TAG_TRANSc << "\n";

}

