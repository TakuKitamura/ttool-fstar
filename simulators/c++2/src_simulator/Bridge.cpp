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

#include <Bridge.h>
#include <SchedulableCommDevice.h>
#include <TMLChannel.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>

Bridge::Bridge(std::string iName, TMLTime iTimePerCycle, unsigned int iBufferSize):_name(iName), _timePerCycle(iTimePerCycle), _bufferSize(iBufferSize){
}

void Bridge::CalcTransactionLength(TMLTransaction* iTrans){
}

//TMLTransaction* Bridge::getNextBusTransaction(SchedulableCommDevice* iBus) const{
//	return 0;
//}

Master* Bridge::getConnectedMaster(){
	return (Master*)this;
}

Bridge::~Bridge(){
	//BridgeTransactionListHash::iterator i;
	//for(i=_transListHash.begin(); i != _transListHash.end(); ++i){
	//	delete i->second;
	//}
}

//void ForwardTransactionToMaster(TMLTransaction* iTrans){
	//registerTransaction(aNewTrans);
//}

//Belongs to Master Interface 
//TMLTransaction* Bridge::getNextBusTransaction(SchedulableCommDevice* iBus) const{
	//use hash table to find all transactions for the bus which is polling
	//return transaction which is runnable first
	/*if (_schedulingNeeded) schedule();
	FutureTransactionQueue* aTransQueue = _transListHash[iBus];
	if (aTransQueue==0) return 0;
	return aTransQueue->top();*/
//}

//void Bridge::schedule(){
	//call getNextTransaction on all buses to which a transaction has been forwarded
	//look up in transaction list of the given bus if returned transaction == transaction which finishes first
	//select transaction which finishes first _nextTransaction
	/*BridgeTransactionListHash::iterator i;
	SchedulableCommDevice* aTempBus;
	TMLTransaction* aTempTransaction;
	TMLTime aRunnableTime=-1;
	_nextBus=0;
	for(i=_transListHash.begin(); i != _transListHash.end(); ++i){
		aTempBus = i->first;
		aTempTransaction = i->second->top();
		if (aTempTransaction == aTempBus->getNextTransaction() && aTempTransaction->getRunnableTime() < aRunnableTime){
			aRunnableTime = aTempTransaction->getRunnableTime();
			_nextBus = aTempBus;
		}
	}
	_schedulingNeeded=false;*/
//}

//void Bridge::addTransaction(){
	//delete transaction from hash table
	//FutureTransactionQueue* aTransQueue = _transListHash[_nextBus];
	//if (aTransQueue!=0) aTransQueue->pop();
//}

//TMLTransaction* Bridge::getNextTransaction(){
	//return next transaction
	//if (_schedulingNeeded) schedule();
	//FutureTransactionQueue* aTransQueue = _transListHash[_nextBus];
	//if (aTransQueue==0) return 0;
	//return aTransQueue->top();
//}

//void Bridge::registerTransaction(TMLTransaction* iTrans){
//void Bridge::registerTransaction(TMLTransaction* iTrans, Master* iSourceDevice){
	/*SchedulableCommDevice* aNextBus;
	TMLChannel* aChannel = iTrans->getChannel();
	if (iTrans->getCommand()->getTask() == aChannel->getBlockedWriteTask()){
		//write transaction
		aNextBus = aChannel->getBus(iTrans->getHop());

	}else{
		//read transaction
		aNextBus = aChannel->getBus(aChannel->getNumberOfHops()-iTrans->getHop()-1);
	}
	//  add Transaction to HashTable (based on destination bus)
	FutureTransactionQueue* aTransQueue = _transListHash[aNextBus];
	if (aTransQueue==0){
		aTransQueue = new FutureTransactionQueue();
		_transListHash[aNextBus] = aTransQueue;
	}
		
	aTransQueue->push(aNewTrans);
	aNextBus->registerTransaction(aNewTrans);
	_schedulingNeeded=true;
	}*/
//}

//void Bridge::schedule2HTML(std::ofstream& myfile){
//}

//void Bridge::schedule2TXT(std::ofstream& myfile){
//}
