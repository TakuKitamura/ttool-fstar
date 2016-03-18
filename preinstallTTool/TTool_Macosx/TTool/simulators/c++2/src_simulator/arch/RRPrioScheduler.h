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
#ifndef RRPrioSchedulerH
#define RRPrioSchedulerH
#include <WorkloadSource.h>

class TMLTransaction;

///Round Robin Priotity based scheduler
class RRPrioScheduler: public WorkloadSource{
public:
	///Constructor
    	/**
	\param iName Name of the scheduler
      	\param iPrio Priority of the scheduler
	\param iTimeSlice Time slice which is granted to clients
	\param iMinSliceSize Minimum size of a time slice
    	*/
	RRPrioScheduler(const std::string& iName, Priority iPrio, TMLTime iTimeSlice, TMLTime iMinSliceSize);
	//RRScheduler(const std::string& iName, Priority iPrio, TMLTime iTimeSlice);
	///Constructor
    	/**
	\param iName Name of the scheduler
      	\param iPrio Priority of the scheduler
	\param iTimeSlice Time slice which is granted to clients
	\param iMinSliceSize Minimum size of a time slice
	\param aSourceArray Array of pointers to workload ressources from which transactions may be received
	\param iNbOfSources Length of the array
    	*/
	RRPrioScheduler(const std::string& iName, Priority iPrio, TMLTime iTimeSlice, TMLTime iMinSliceSize, WorkloadSource** aSourceArray, unsigned int iNbOfSources);
	//RRScheduler(const std::string& iName, Priority iPrio, TMLTime iTimeSlice, WorkloadSource** aSourceArray, unsigned int iNbOfSources);
	///Destructor
	~RRPrioScheduler();
	TMLTime schedule(TMLTime iEndSchedule);
	inline TMLTransaction* getNextTransaction(TMLTime iEndSchedule) const {return _nextTransaction;}
	void reset();
	std::istream& readObject(std::istream &is);
	std::ostream& writeObject(std::ostream &os);
	inline std::string toString() const {return _name;}
	//void transWasScheduled(SchedulableDevice* iDevice);
protected:
	///Name of the scheduler
	std::string _name;
	///Next transaction to be executed
	TMLTransaction* _nextTransaction;
	///Time slice which is granted to ressources
	TMLTime _timeSlice;
	///Minimum size of a time slice
	TMLTime _minSliceSize;
	///Consumed portion of a time slice
	TMLTime _elapsedTime;
	///Last workload source to which ressource access was granted
	WorkloadSource* _lastSource;
};
#endif
