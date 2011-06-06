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

#ifndef BusMasterH
#define BusMasterH

#include <definitions.h>
#include <WorkloadSource.h>
#include <TMLTransaction.h>
#include <SchedulableDevice.h>
#include <TMLChannel.h>
class TMLTransaction;
class SchedulableCommDevice;

///Class serving as interface between CPUs and buses (more precisely bus schedulers)
class BusMaster: public WorkloadSource{
public:
	///Constructor
    	/**
	\param iName Name of the bus master
      	\param iPriority Priority of the bus master
	\param iNbOfBuses Number of buses(bus channels) the master is connected to
	\param iBusArray Pointer to the buses(bus channels) the master is connected to
    	*/
	BusMaster(const std::string& iName, Priority iPriority, unsigned int iNbOfBuses, SchedulableCommDevice** iBusArray): WorkloadSource(iPriority), _name(iName), _nbOfBuses(iNbOfBuses), _busArray(iBusArray), _busSortArray(0), _nextTransaction(0), _nextBus(iBusArray[0]), /*_lastSimTime(-1),*/ _contentionDelay(0), _noTransactions(0), _channelBasedPrioEnabled(false), _channelBasedPrio(0){
		_busSortArray=new SchedulableCommDevice*[_nbOfBuses];
		for (unsigned int i=0; i <_nbOfBuses; i++) _busSortArray[i]=_busArray[i];
		_channelBasedPrioEnabled = _busArray[0]->ChannelBasedPrio();
	}
	
	///Destructor
	~BusMaster(){
		std::cout << _name << ": Bus Master deleted\n";
		delete[] _busArray;
		delete[] _busSortArray;
	}
	
	void reset(){
		_nextTransaction=0;
		//_lastSimTime=-1;
		_nextBus=_busArray[0];
		_contentionDelay=0;
		_noTransactions=0;
	}

	void registerTransaction(TMLTransaction* iTrans){
		if (iTrans!=_nextTransaction){
			//std::cout << _name << ": registerTransaction" << std::endl;
			for (unsigned int i=0; i <_nbOfBuses; i++) _busArray[i]->registerTransaction();
			_nextTransaction=iTrans;
			if (_channelBasedPrioEnabled && iTrans!=0){
				_channelBasedPrio=iTrans->getChannel()->getPriority();
			}
		}
	}

	TMLTransaction* getNextTransaction(TMLTime iEndSchedule) const{
		if (_nextTransaction==0) return 0;
		for (unsigned int i=0; i <_nbOfBuses; i++){
			//std::cout << "Raw version allowed\n";
			if ((*(_busArray[i])).SchedulableDevice::getNextTransaction()==_nextTransaction){
				//std::cout << _name << "trans already scheduled by: " << _busArray[i]->toString() << "\n";
				return 0;
			}
		}
		return _nextTransaction;
	}

	void addTransaction(TMLTransaction* iTransToBeAdded){
		//std::cout << _name << ": add Trans\n";
		//std::cout << _name << ": trans added on Bus: " << _nextBus->toString() << std::endl;
		_nextBus->addTransaction(iTransToBeAdded);
		_nextTransaction=0;
		//std::cout << _name << ": end add" << std::endl;
	}

	///Indicates whether bus access has been granted
	/**
	\return Returns true if access has been granted
	*/ 
	bool accessGranted(){
		//std::cout << _name << ":access granted " << "\n";
		if (_nextTransaction==0){
			//std::cout << _name << ":branch no trans" << "\n";
			return false;
		}
		if (_nbOfBuses==1) {
			//std::cout << _name << ": branch 1 bus\n";
			//bool test = _nextTransaction==_busArray[0]->getNextTransaction();
			return (_nextTransaction==_busArray[0]->getNextTransaction());
			//return test;
		}
		sortBusList();
		//_transWasScheduled=false;
		//std::cout << "Bus scheduling initiated by: " << _name << "\n";
		for (unsigned int i=0; i <_nbOfBuses; i++){
			if (_busSortArray[i]->getNextTransaction()==_nextTransaction){
				_nextBus=_busSortArray[i];
				//std::cout << _name << ":access granted end true, bus: "<< _nextBus->toString() << "\n";
				return true;
			}
		}
		//std::cout << _name << ":access granted end false" << "\n";
		return false;
	}

	///Returns the pointer to the bus the master is connected to
	/**
	\return Pointer to bus
	*/
	SchedulableCommDevice* getBus(){
		return _busArray[0];
	}

	/*void transWasScheduled(){
		_transWasScheduled=true;
		_addTransCheck=_addTransFlag;
		_lastSimTime = SchedulableDevice::getSimulatedTime();
	}*/

	///Updates the bus contention statistics whenever a new bus transaction is executed
	/**
	\param iContentionDelay Contention delay of the transaction
	*/
	void addBusContention(unsigned long iContentionDelay){
		_contentionDelay+=iContentionDelay;
		_noTransactions++;
	}

	///Writes benchmarking data to a given stream
	/**
      	\param s Reference to an output stream
	*/
	void streamBenchmarks(std::ostream& s) const{
		if (_noTransactions!=0)
			s << TAG_CONTDELo << " busID=\"" << _busArray[0]->getID()<< "\" busName=\"" << _busArray[0]->toString() << "\">" << (static_cast<float>(_contentionDelay)/static_cast<float>(_noTransactions)) << TAG_CONTDELc << std::endl;
	}

	std::string toString() const{
		return _name;
	}

	Priority getPriority() const{
		return (_channelBasedPrioEnabled)?_channelBasedPrio: _priority;
	}

	std::istream& readObject(std::istream &is){
		WorkloadSource::readObject(is);
#ifdef SAVE_BENCHMARK_VARS
		READ_STREAM(is,_contentionDelay);
#ifdef DEBUG_SERIALIZE
		std::cout << "Read: BusMaster " << _name << " contentionDelay: " << _contentionDelay << std::endl;
#endif
		READ_STREAM(is,_noTransactions);
#ifdef DEBUG_SERIALIZE
		std::cout << "Read: BusMaster " << _name << " noTransactions: " << _noTransactions << std::endl;
#endif
#endif
		return is;
	}
	std::ostream& writeObject(std::ostream &os){
		WorkloadSource::writeObject(os);
#ifdef SAVE_BENCHMARK_VARS
		WRITE_STREAM(os,_contentionDelay);
#ifdef DEBUG_SERIALIZE
		std::cout << "Write: BusMaster " << _name << " contentionDelay: " << _contentionDelay << std::endl;
#endif
		WRITE_STREAM(os,_noTransactions);
#ifdef DEBUG_SERIALIZE
		std::cout << "Write: BusMaster " << _name << " noTransactions: " << _noTransactions << std::endl;
#endif
#endif
		return os;
	}

protected:
	///Sorts the bus list by ascending finish times of the last scheduled transaction
	void sortBusList(){
		//std::cout << _name << ": sort result: ";
		unsigned int aBound = _nbOfBuses;
		bool aSwapped;
		do{
			aSwapped=false;
			aBound--;
			for(unsigned int i=0; i<aBound; i++){
				if (_busSortArray[i]->getEndSchedule() > _busSortArray[i+1]->getEndSchedule()){
					SchedulableCommDevice* aTmp = _busSortArray[i];
					_busSortArray[i] = _busSortArray[i+1];
					_busSortArray[i+1] = aTmp;
					aSwapped= true;
				}
			}
		}while (aSwapped);
		/*for(unsigned int i=0; i<_nbOfBuses; i++){
			std::cout << ", " << _busSortArray[i]->toString();
		}
		std::cout << "     " << _name << ": end sort" << std::endl;*/
	}

	///Name
	std::string _name;	
	///Number of connected channels
	unsigned int _nbOfBuses;
	///Pointer to the bus the master is connected to
	SchedulableCommDevice** _busArray;
	///Array containing a sorted array of bus pointers (criterion: schedule end time)
	SchedulableCommDevice** _busSortArray;
	///Transaction
	TMLTransaction* _nextTransaction;
	///Next bus pointer
	SchedulableCommDevice* _nextBus;
	////Flag indicating at what simulation time _nextTransaction was scheduled
	//mutable TMLTime _lastSimTime;
	///Sum of the contention delay of all registered transactions
	unsigned long _contentionDelay;
	///Number of registered transactions
	unsigned long _noTransactions;
	///Flag indicating whether channel based priorities apply
	bool _channelBasedPrioEnabled;
	///Channel based priority if applicable
	Priority _channelBasedPrio;
};

#endif
