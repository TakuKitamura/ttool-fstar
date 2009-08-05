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

class TMLTransaction;
class SchedulableCommDevice;

///Class serving as interface between CPUs and buses (more precisely bus schedulers)
class BusMaster: public WorkloadSource{
public:
	///Constructor
    	/**
	\param iName Name of the bus master
      	\param iPriority Priority of the bus master
	\param iBus Pointer to the bus the master is connected to
    	*/
	BusMaster(const std::string& iName, unsigned int iPriority, SchedulableCommDevice* iBus): WorkloadSource(iPriority), _name(iName), _bus(iBus), _nextTransaction(0){
	}
	
	///Destructor
	~BusMaster(){
		std::cout << _name << ": Bus Master deleted\n";
	}
	
	void reset(){
		_nextTransaction=0;
		_contentionDelay=0;
		_noTransactions=0;
	}

	void registerTransaction(TMLTransaction* iTrans){
		if (iTrans!=_nextTransaction){
			_bus->truncateToBurst(iTrans);
			_bus->registerTransaction();
			_nextTransaction=iTrans;
		}
	}

	TMLTransaction* getNextTransaction() const{
		return _nextTransaction;
	}

	void addTransaction(){
		_bus->addTransaction();
		_nextTransaction=0;
	}

	///Indicates whether bus access has been granted
	/**
	\return Returns true if access has been granted
	*/ 
	bool accessGranted(){
		return  (_nextTransaction!=0 && _bus->getNextTransaction()==_nextTransaction);
	}

	///Returns the pointer to the bus the master is connected to
	/**
	\return Pointer to bus
	*/
	SchedulableCommDevice* getBus(){
		return _bus;
	}

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
			s << TAG_CONTDELo << " busID=\"" << _bus->getID()<< "\" busName=\"" << _bus->toString() << "\">" << (static_cast<float>(_contentionDelay)/static_cast<float>(_noTransactions)) << TAG_CONTDELc << std::endl;
	}

	std::string toString() const{
		return _name;
	}

	std::istream& readObject(std::istream &is){
		WorkloadSource::readObject(is);
#ifdef SAVE_BENCHMARK_VARS
		READ_STREAM(is,_contentionDelay);
		std::cout << "Read: BusMaster " << _name << " contentionDelay: " << _contentionDelay << std::endl;
		READ_STREAM(is,_noTransactions);
		std::cout << "Read: BusMaster " << _name << " noTransactions: " << _noTransactions << std::endl;
#endif
		return is;
	}
	std::ostream& writeObject(std::ostream &os){
		WorkloadSource::writeObject(os);
#ifdef SAVE_BENCHMARK_VARS
		WRITE_STREAM(os,_contentionDelay);
		std::cout << "Write: BusMaster " << _name << " contentionDelay: " << _contentionDelay << std::endl;
		WRITE_STREAM(os,_noTransactions);
		std::cout << "Write: BusMaster " << _name << " noTransactions: " << _noTransactions << std::endl;
#endif
		return os;
	}

protected:
	///Name
	std::string _name;
	///Pointer to the bus the master is connected to
	SchedulableCommDevice* _bus;
	///Transaction
	TMLTransaction* _nextTransaction;
	///Sum of the contention delay of all registered transactions	
	unsigned long _contentionDelay;
	///Number of registered transactions
	unsigned long _noTransactions;
};

#endif
