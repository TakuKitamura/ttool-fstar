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

#ifndef SimServSyncInfoH
#define SimServSyncInfoH

#include <definitions.h>

#define BUFFER_SIZE 1000
class CurrentComponents;
class Simulator;
class ServerIF;
class SimComponents;

///Synchronizes server and simulator
class SimServSyncInfo{
public:
	///Pointer to simulator
	Simulator* _simulator;
	///Pointer to server
	ServerIF* _server;
	///Pointe simulation components
	SimComponents* _simComponents;
	///Simulation terminate flag
	bool _terminate;

	///Constructor
	SimServSyncInfo():_simulator(0), _server(0), _simComponents(0), _terminate(false), _popGotStuck(false){
		pthread_mutex_init(&_mutexCmdAvailable, NULL);
		pthread_mutex_init(&_mutexListProtect, NULL);
		pthread_mutex_lock(&_mutexCmdAvailable);
	}
	
	///Destructor
	~SimServSyncInfo(){
		for(CommandQueue::iterator i=_cmdQueue.begin(); i != _cmdQueue.end(); ++i){
			delete (*i);
		}
	}

	///Push command on command queue
	/**
	\param iCmd Command
	*/
	void pushCommand(std::string* iCmd){
		//std::cout << "Value to write: " << *iCmd << std::endl;
		pthread_mutex_lock(&_mutexListProtect);
		//std::cout << "Before push_back" << *iCmd << std::endl;
		_cmdQueue.push_back(iCmd);
		if (_popGotStuck){
			_popGotStuck=false;
			pthread_mutex_unlock(&_mutexCmdAvailable);
		}
		pthread_mutex_unlock(&_mutexListProtect);
		//std::cout << "End write" << std::endl;
	}

	///Pop command from command queue
	/**
	\return Command
	*/
	std::string* popCommand(){
		pthread_mutex_lock(&_mutexListProtect);
		if (_cmdQueue.empty()){
			_popGotStuck=true;
			pthread_mutex_unlock(&_mutexListProtect);
			pthread_mutex_lock(&_mutexCmdAvailable);
			pthread_mutex_lock(&_mutexListProtect);
		}
		std::string* aCmdTmp=_cmdQueue.front();
		//std::cout << "Read value: " << *aCmdTmp << std::endl;
		_cmdQueue.pop_front();
		//std::cout << "After pop" << std::endl;
		pthread_mutex_unlock(&_mutexListProtect);
		//std::cout << "End read" << std::endl;
		return aCmdTmp;
	}

protected:
	///Mutex for mutual exclusion of list access
	pthread_mutex_t _mutexListProtect;
	///Mutex preventing read access to empty command queue
	pthread_mutex_t _mutexCmdAvailable;
	///Command queue
	CommandQueue _cmdQueue;
	///Flag indicating that the server is waiting for new commands
	bool _popGotStuck;	
};
#endif
