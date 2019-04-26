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

#include <CPU.h>
class TMLTask;
class TMLTransaction;

double CPU::averageLoad (unsigned int n) const{
  double _averageLoad=0;
  TMLTime _maxEndTime=0;
  for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
    if( (*i)->getTransactCoreNumber() == n ){
      TMLTime _endTime= (*i)->getEndTime();
      _maxEndTime=max(_maxEndTime,_endTime);
    }
  }
  std::cout<<"max end time is "<<_maxEndTime<<std::endl;
  for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
    if( (*i)->getTransactCoreNumber() == n ){
      _averageLoad += (*i)->getEndTime() - (*i)->getStartTime();
    }
  }
  if(_maxEndTime == 0)
    return 0;
  else {
    _averageLoad = (double)_averageLoad/_maxEndTime;
    return _averageLoad;
  }
  /*if( _maxEndTime == 0 ) 
    myfile << "average load is 0" << "<br>";
  else
  myfile<<" average load is "<<(double)_averageLoad/_maxEndTime<<"<br>";*/
 
}


void CPU::drawPieChart(std::ofstream& myfile) const {
 
   TMLTime _maxEndTime=0;
   for(unsigned int j = 0; j < amountOfCore ; ++j){
     for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
       if( (*i)->getTransactCoreNumber() == j ){
	 TMLTime _endTime= (*i)->getEndTime();
	 _maxEndTime=max(_maxEndTime,_endTime);
       }
     }
     std::map <TMLTask*, double > transPercentage;
     for( TransactionList::const_iterator i = _transactList.begin(); i!= _transactList.end(); ++i){
       if( (*i)->getTransactCoreNumber() == j ){
	 transPercentage[(*i)-> getCommand()->getTask()]+=(double)((*i)->getEndTime()-(*i)->getStartTime())/_maxEndTime;      
       }
     }
     std::map <TMLTask*, double>::iterator iter = transPercentage.begin();
     myfile << "     var chart" << _ID << "_" << j << "= new CanvasJS.Chart(\"chartContainer" << _ID << "_" << j <<"\"," << std::endl;
     myfile <<  SCHED_HTML_JS_CONTENT2 << "Average load is " << averageLoad(j) <<  SCHED_HTML_JS_CONTENT3 << std::endl;
     double idle=1;
     while( iter != transPercentage.end()){
       myfile << "                { y:" << (iter->second)*100 << ", indexLabel: \"" << iter->first->toString() << "\" }," << std::endl;
       idle-=iter->second;
       ++iter;  
     }
     myfile << "                { y:" << idle*100 << ", indexLabel: \"idle time\"" << " }" << std::endl;
     myfile << std::endl;
     myfile << SCHED_HTML_PIE_END;
     myfile << "chart" << _ID << "_" << j << ".render();" << std::endl; 
   }
  
}

void CPU::showPieChart(std::ofstream& myfile) const{
  myfile << SCHED_HTML_JS_DIV_ID << _ID << "_" << this->_cycleTime << SCHED_HTML_JS_DIV_END << "<br>";
}
  
   

void CPU::schedule2HTML(std::ofstream& myfile) const {  
  myfile << "<h2><span>Scheduling for device: "<< _name <<"_core_"<<this->_cycleTime<< "</span></h2>" << std::endl;

  if ( _transactList.size() == 0 ) {
    myfile << "<h4>Device never activated</h4>" << std::endl;
  }
  else {
    myfile << "<table>" << std::endl << "<tr>";

    std::map<TMLTask*, std::string> taskCellClasses;
    unsigned int nextCellClassIndex = 0;
    TMLTime aCurrTime = 0;

    for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
      std::cout<<"get transaction core number is: "<<(*i)->getTransactCoreNumber()<<std::endl;
      std::cout<<"time : "<<_cycleTime<<std::endl;
      //std::cout << "CPU:calcSTL: html of CPU " << _name << ": " << (*i)->toString() << std::endl;
      if( (*i)->getTransactCoreNumber() == this->_cycleTime ){
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
