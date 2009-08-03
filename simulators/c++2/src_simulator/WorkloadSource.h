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
//#include <TMLTask.h>

class TMLTransaction;
class Master;
class TMLTask;

class WorkloadSource{
public:
	WorkloadSource(unsigned int iPriority): _priority(iPriority), _srcArraySpecified(false) {}
	WorkloadSource(unsigned int iPriority, WorkloadSource** aSourceArray, unsigned int iNbOfSources): _priority(iPriority), _srcArraySpecified(true){
		for (unsigned int i=0;i<iNbOfSources;i++){
			addWorkloadSource(aSourceArray[i]);
			std::cout << "Workload source added " << aSourceArray[i]->toString() << "\n";
		}
		delete[] aSourceArray;
	}
	virtual ~WorkloadSource();
	virtual TMLTransaction* getNextTransaction() const=0;
	inline unsigned int getPriority() const{return _priority;}
	inline void addWorkloadSource(WorkloadSource* iSource){_workloadList.push_back(iSource);}
	virtual void registerTransaction(TMLTransaction* iTrans, Master* iSourceDevice){
		for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i) (*i)->registerTransaction(iTrans, iSourceDevice);
	}
	virtual void schedule(TMLTime iEndSchedule){};
	virtual void reset(){}
	virtual std::string toString() const =0;
protected:
	WorkloadList _workloadList;
	unsigned int _priority;
	bool _srcArraySpecified;
};
#endif
