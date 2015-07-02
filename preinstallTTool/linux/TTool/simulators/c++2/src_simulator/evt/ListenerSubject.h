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
#ifndef ListenerSubjectH
#define ListenerSubjectH

///Base class for listener subjects 
template <typename T>
class ListenerSubject{
public:
	///Constructor
	ListenerSubject():_locked(false){}
	///Registers a new listener
	/**
	\param  iListener Pointer to the listener
	*/
	void registerListener(T* iListener){
		if (_locked)
			_adding.push_back(iListener);
		else
			_listeners.push_back(iListener);
	}
	///Removes a listener from the internal list
	/**
	\param  iListener Pointer to the listener
	*/
	void removeListener(T* iListener){
		if (_locked)
			_deletion.push_back(iListener);
		else	
			_listeners.remove(iListener);
	}

	///Lock list of listeners
	void listenersLock(){
		_locked=true;
	}
	
	///Unlock and update list of listeners
	void listenersUnLock(){
		_locked=false;
		for(typename std::list<T*>::iterator i=_deletion.begin(); i != _deletion.end(); ++i){
			_listeners.remove(*i);
		}
		for(typename std::list<T*>::iterator i=_adding.begin(); i != _adding.end(); ++i) {
			_listeners.push_back(*i);
		}
		_deletion.clear();
		_adding.clear();
	}

	///Destructor
	virtual ~ListenerSubject(){
		//listenersUnLock();
		//for(typename std::list<T*>::iterator i=_listeners.begin(); i != _listeners.end(); ++i) {
		//	delete (*i);
		//}
	}
protected:
	///List of listeners
	std::list<T*> _listeners;
private:
	///Listeners locked flag
	bool _locked;
	///List of listeners scheduled for deletion
	std::list<T*> _deletion;
	///List of listeners scheduled for adding
	std::list<T*> _adding;
};
#endif

