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

#ifndef MasterH
#define MasterH

#include <definitions.h>
#include <SchedulableCommDevice.h>
#include <BusMasterInfo.h>

class TMLTransaction;
class SchedulableCommDevice;

///Base class for Bus masters
class Master{
public:
	///Constructor
	Master(){}
	///Retrieves the priority of the master for the given bus
	/**
	\param iDevice
	\return Bus priority
	*/
	unsigned int getBusPriority(SchedulableCommDevice* iDevice) const{
		//return _masterPrioHashTab[iDevice];
		//return _masterPrioHashTab[iDevice]->getPriority();
		return _masterPrioHashTab.find(iDevice)->second->getPriority();
	}
	///Sets the priority of the master for a given bus
	/**
	\param iDevice Pointer to the bus
	\param iPrio Priority
	*/
	void addBusPriority(SchedulableCommDevice* iDevice, unsigned int iPrio){
		//_masterPrioHashTab[iDevice]=iPrio;
		_masterPrioHashTab[iDevice]= new BusMasterInfo(iPrio);
	}
	///Destructor
	virtual ~Master(){
		for(MasterPriorityHashTab::iterator i=_masterPrioHashTab.begin(); i != _masterPrioHashTab.end(); ++i){
			delete i->second;
		}
	}
	void reset(){
		for(MasterPriorityHashTab::iterator i=_masterPrioHashTab.begin(); i != _masterPrioHashTab.end(); ++i){
			i->second->reset();
		}
	}
protected:
	///Updates the bus contention statistics when a new bus transaction has been executed
	/**
	\param iDevice Pointer to the bus
	\param iContentionDelay Contention delay of the transaction
	*/
	void addBusContention(SchedulableCommDevice* iDevice, unsigned long iContentionDelay) const{
		//_masterPrioHashTab[iDevice]->addContention(iContentionDelay);
		_masterPrioHashTab.find(iDevice)->second->addContention(iContentionDelay);
	}
	///Writes benchmarking data to a given stream
	/**
      	\param s Reference to an output stream
	*/
	void streamBenchmarks(std::ostream& s) const{
		for(MasterPriorityHashTab::const_iterator i=_masterPrioHashTab.begin(); i != _masterPrioHashTab.end(); ++i){
			s << "Average contention delay for bus " << i->first->toString() << ": " << i->second->getContentionDelay() << std::endl;
		}
	}
	///Map which associates the bus and the priority
	MasterPriorityHashTab _masterPrioHashTab;
};

#endif
