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

#include <SchedulableDevice.h>

SchedulableDevice::SchedulableDevice(	ID iID,
										std::string iName,
										WorkloadSource* iScheduler ) :
						_ID(iID),
						_name(iName),
						_endSchedule(0),
						_scheduler(iScheduler),
						_nextTransaction(0),
						_deleteScheduler(true),
						_busyCycles(0),
						_static_consumPerCycle (15),
						_dynamic_consumPerCycle (35) {
	_transactList.reserve( BLOCK_SIZE_TRANS );
}

std::istream& SchedulableDevice::readObject(std::istream &is) {
	READ_STREAM(is,_endSchedule);
	//_simulatedTime=max(_simulatedTime,_endSchedule);   ????????????
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: Schedulable Device " << _name << ": " << _endSchedule << std::endl;
#endif
	return is;
}

std::ostream& SchedulableDevice::writeObject(std::ostream &os){
	WRITE_STREAM(os,_endSchedule);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: Schedulable Device " << _name << ": " << _endSchedule << std::endl;
#endif
	return os;
}

void SchedulableDevice::reset() {
	_endSchedule=0;
	_simulatedTime=0;
}

///Destructor
SchedulableDevice::~SchedulableDevice(){
	if (_scheduler!=0 && _deleteScheduler) delete _scheduler;
}

TMLTransaction* SchedulableDevice::getTransactions1By1(bool iInit){
	if (iInit) _posTrasactListGraph=_transactList.begin();
	if (_posTrasactListGraph == _transactList.end()) return 0;
	TMLTransaction* aTrans = *_posTrasactListGraph;
	_posTrasactListGraph++;
	return aTrans;
}

// Issue #4: Some browsers (like Firefox) do not support column spans of more than 1000 columns
void SchedulableDevice::writeHTMLColumn(	std::ofstream& myfile,
											const unsigned int colSpan,
											const std::string cellClass ) {
	writeHTMLColumn( myfile, colSpan, cellClass, "" );
}

void SchedulableDevice::writeHTMLColumn(	std::ofstream& myfile,
											const unsigned int colSpan,
											const std::string cellClass,
											const std::string title ) {
	writeHTMLColumn( myfile, colSpan, cellClass, title, "", true );
}

void SchedulableDevice::writeHTMLColumn(	std::ofstream& myfile,
											const unsigned int colSpan,
											const std::string cellClass,
											const std::string title,
											const std::string content,
											const bool endline ) {
	std::string begLine( START_TD );

	if ( !title.empty() ) {
		begLine.append( " title=\"" );
		begLine.append( title );
		begLine.append( "\"" );
	}

	begLine.append( " class=\"" );

	if ( colSpan == 1 ) {
		begLine.append( cellClass );
		begLine.append( "\"" );
		myfile << begLine << ">" << END_TD;

		if ( endline ) {
			myfile << std::endl;
		}
	}
	else {
		int actualLength = colSpan;
		bool first = true;
		bool last = false;

		do {
			last = actualLength <= MAX_COL_SPAN;
			std::string clasVal( cellClass );

			if ( first && !last ) {
				clasVal.append( "first" );
				first = false;
			}
			else if ( last && !first ) {
				clasVal.append( "last" );
			}
			else if ( !last && !first ) {
				clasVal.append( "mid" );
			}

			clasVal.append( "\"" );

			std::string colSpan( " colspan=\"" );
			std::ostringstream spanVal;
			spanVal << std::min( MAX_COL_SPAN, actualLength ) <<  "\"";
			colSpan.append( spanVal.str() );

			myfile << begLine << clasVal << colSpan << ">" << content << END_TD;

			if ( last && endline ) {
				myfile << std::endl;
			}

			actualLength -= MAX_COL_SPAN;
		} while ( !last );
	}
}

std::string SchedulableDevice::determineHTMLCellClass( 	std::map<TMLTask*, std::string> &taskColors,
														TMLTask* task,
														unsigned int &nextColor ) {
	std::map<TMLTask*, std::string>::const_iterator it = taskColors.find( task );

	if ( it == taskColors.end() ) {
		unsigned int aColor = nextColor % NB_HTML_COLORS;
		std::ostringstream cellClass;
		cellClass << "t" << aColor;
		taskColors[ task ] = cellClass.str();
		nextColor++;
	}

	return taskColors[ task ];
}

void SchedulableDevice::schedule2HTML(std::ofstream& myfile) const {
	myfile << "<h2><span>Scheduling for device: "<< _name << "</span></h2>" << std::endl;

	if ( _transactList.size() == 0 ) {
		myfile << "<h4>Device never activated</h4>" << std::endl;
	}
	else {
		myfile << "<table>" << std::endl << "<tr>";

		std::map<TMLTask*, std::string> taskCellClasses;
		unsigned int nextCellClassIndex = 0;
		TMLTime aCurrTime = 0;

		for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
			TMLTransaction* aCurrTrans = *i;
			unsigned int aBlanks = aCurrTrans->getStartTime() - aCurrTime;

			if ( aBlanks > 0 ) {
				writeHTMLColumn( myfile, aBlanks, "not", "idle time" );
			}

			unsigned int aLength = aCurrTrans->getPenalties();

			if ( aLength != 0 ) {
				std::ostringstream title;
				title << "idle:" << aCurrTrans->getIdlePenalty() << " switch:" << aCurrTrans->getTaskSwitchingPenalty();
				writeHTMLColumn( myfile, aLength, "not", title.str() );
			}

			aLength = aCurrTrans->getOperationLength();

			// Issue #4
			TMLTask* task = aCurrTrans->getCommand()->getTask();
			const std::string cellClass = determineHTMLCellClass( taskCellClasses, task, nextCellClassIndex );

			writeHTMLColumn( myfile, aLength, cellClass, aCurrTrans->toShortString() );

			aCurrTime = aCurrTrans->getEndTime();
		}

		myfile << "</tr>" << std::endl << "<tr>";

		for ( unsigned int aLength = 0; aLength < aCurrTime; aLength++ ) {
			myfile << "<th></th>";
		}

		myfile << "</tr>" << std::endl << "<tr>";

		for ( unsigned int aLength = 0; aLength <= aCurrTime; aLength += 5 ) {
			std::ostringstream spanVal;
			spanVal << aLength;
			writeHTMLColumn( myfile, 5, "sc", "", spanVal.str(), false );
			//myfile << "<td colspan=\"5\" class=\"sc\">" << aLength << "</td>";
		}

		myfile << "</tr>" << std::endl << "</table>" << std::endl << "<table>" << std::endl << "<tr>";

		for( std::map<TMLTask*, std::string>::iterator taskColIt = taskCellClasses.begin(); taskColIt != taskCellClasses.end(); ++taskColIt ) {
			TMLTask* task = (*taskColIt).first;
			// Unset the default td max-width of 5px. For some reason setting the max-with on a specific t style does not work
			myfile << "<td class=\"" << taskCellClasses[ task ] << "\"></td><td style=\"max-width: unset;\">" << task->toString() << "</td><td class=\"space\"></td>";
		}

		myfile << "</tr>" << std::endl;

#ifdef ADD_COMMENTS
		bool aMoreComments = true, aInit = true;
		Comment* aComment;

		while ( aMoreComments ) {
			aMoreComments = false;
			myfile << "<tr>";

			for( std::map<TMLTask*, std::string>::iterator taskColIt = taskCellClasses.begin(); taskColIt != taskCellClasses.end(); ++taskColIt ) {
			//for(TaskList::const_iterator j=_taskList.begin(); j != _taskList.end(); ++j){
				TMLTask* task = (*taskColIt).first;
			    std::string aCommentString = task->getNextComment( aInit, aComment );

				if ( aComment == 0 ) {
					myfile << "<td></td><td></td><td class=\"space\"></td>";
				}
				else {
					replaceAll(aCommentString,"<","&lt;");
					replaceAll(aCommentString,">","&gt;");
					aMoreComments = true;
					myfile << "<td style=\"max-width: unset;\">" << aComment->_time << "</td><td><pre>" << aCommentString << "</pre></td><td class=\"space\"></td>";
				}
			}

			aInit = false;
			myfile << "</tr>" << std::endl;
		}
#endif
		myfile << "</table>" << std::endl;
	}
}

