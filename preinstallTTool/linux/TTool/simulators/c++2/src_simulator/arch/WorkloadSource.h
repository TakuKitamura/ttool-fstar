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
#ifndef WorkloadSourceH
#define WorkloadSourceH
#include <definitions.h>
#include <Serializable.h>

class TMLTransaction;
class Master;
class TMLTask;
class SchedulableDevice;

///Base class for components providing workload like tasks and schedulers
class WorkloadSource: public Serializable{
public:
	///Constructor
	/**
	\param iPriority Priority of the workload source
	*/
	WorkloadSource(Priority iPriority): _priority(iPriority), _srcArraySpecified(false) {}
	///Constructor
    	/**
      	\param iPriority Priority of the scheduler
	\param aSourceArray Array of pointers to workload ressources from which transactions may be received
	\param iNbOfSources Length of the array
    	*/
	WorkloadSource(Priority iPriority, WorkloadSource** aSourceArray, unsigned int iNbOfSources): _priority(iPriority), _srcArraySpecified(true){
		for (unsigned int i=0;i<iNbOfSources;i++){
			addWorkloadSource(aSourceArray[i]);
			std::cout << "Workload source added " << aSourceArray[i]->toString() << "\n";
		}
		delete[] aSourceArray;
	}
	///Destruktor
	virtual ~WorkloadSource();
	///Returns the next transaction to be executed by the ressource
	/**
	\return Pointer to the transaction to be executed
	*/
	virtual TMLTransaction* getNextTransaction(TMLTime iEndSchedule) const=0;
	///Returns the priority of the workload source
	/**
	\return Priority of the workload source
	*/
	virtual inline Priority getPriority() const{return _priority;}
	///Add a source which provides transactions to the scheduler
	/**
	\param iSource Pointer to workload source
	*/
	inline void addWorkloadSource(WorkloadSource* iSource){
		for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i)
			if (*i==iSource) return;
		_workloadList.push_back(iSource);
	}
	///Perform scheduling
	/**
	\param iEndSchedule Current time of the ressource
	\return Time slice granted by the scheduler
	*/
	virtual TMLTime schedule(TMLTime iEndSchedule){return 0;}
	virtual void reset(){for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i) (*i)->reset();}
	virtual std::istream& readObject(std::istream &is){
		for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i) (*i)->readObject(is);
		return is;
	}
	virtual std::ostream& writeObject(std::ostream &os){
		for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i) (*i)->writeObject(os);
		return os;
	}
	virtual std::string toString() const =0;
	///Signals that the last scheduled transaction has been selected by the given device
	/**
	\param iDevice Pointer to the device
	*/
	/*virtual void transWasScheduled(SchedulableDevice* iDevice) {}
	///Signals that the last scheduled transaction is not selected by the device any more
	virtual void resetScheduledFlag(){
		for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i) (*i)->resetScheduledFlag();
	}*/
protected:
	///List of sources which provide transactions to the scheduler
	WorkloadList _workloadList;
	///Priority of the workload source
	Priority _priority;
	///Indicates whether sources contained in workload list have to be deleted
	bool _srcArraySpecified;
};
#endif
