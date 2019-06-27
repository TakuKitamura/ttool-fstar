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
    myfile << "   var ctx" << _ID << "_"  << j << "= $(\"#pie-chartcanvas-" << _ID << "_" << j << "\");\n";
    
    double idle=1;
    myfile << "   var data" << _ID << "_" << j << " = new Array (";
    while( iter != transPercentage.end()){
      myfile << "\"" << iter->second << "\",";
      idle-=iter->second;
      ++iter;
    }
    myfile << "\"" << idle << "\");\n";
    
    myfile << "    var efficiency" << _ID << "_" << j << " = [];" << std::endl;
    myfile << "    var coloR" << _ID << "_" << j << " = [];" << std::endl;
    myfile << "    var dynamicColors" << _ID << "_" << j << SCHED_HTML_JS_FUNCTION;
    
    myfile << "    for (var i in data" << _ID << "_" << j << "){\n";
    myfile << "             efficiency" << _ID << "_" << j << ".push(data" << _ID << "_" << j << "[i]);\n";
    myfile << "             coloR" << _ID << "_" << j << ".push(dynamicColors" << _ID << "_" << j << "());\n";
    myfile << "}" << std::endl;
    
    myfile << "   var data" << _ID << "_" << j << " = { \n";
    myfile << "           labels : [";
    iter = transPercentage.begin();
    while( iter != transPercentage.end()){
      myfile << " \"" << iter->first->toString() << "\",";
      idle-=iter->second;
      ++iter;
    }        
    myfile << "\"idle time\"],\n";
    myfile << "          datasets : [\n \
                                     {\n \
                                           data : efficiency" << _ID << "_" << j << ",\n";
    myfile << "                            backgroundColor : coloR" << _ID << "_" << j << std::endl;
    myfile << SCHED_HTML_JS_CONTENT1;
    myfile << "  var options" << _ID << "_" << j << SCHED_HTML_JS_CONTENT3;
    myfile << _name << "_core_" << this->_cycleTime << ": Average load is " << std::setprecision(2) << averageLoad(j) << SCHED_HTML_JS_CONTENT2 << std::endl; 
  }
  
}


void CPU::buttonPieChart(std::ofstream& myfile) const{
  // myfile << "$(\"#" << _ID << "\").click(function() {\n";
 
    myfile << "    var chart" << _ID << "_" << this->_cycleTime << " = new Chart( "<<
      "ctx" << _ID << "_" << this->_cycleTime << ", {\n \
              type : \"pie\",\n";
    myfile << "               data : data" << _ID << "_" << this->_cycleTime <<",\n";
    myfile << "               options : options" << _ID << "_" << this->_cycleTime << std::endl << "                   });" << std::endl;
    myfile << "   chart" << _ID << "_" << this->_cycleTime << SCHED_HTML_JS_HIDE;
    myfile << "   chart" << _ID << "_" << this->_cycleTime << ".update();" << std::endl;
}


void CPU::showPieChart(std::ofstream& myfile) const{
  //myfile << SCHED_HTML_JS_DIV_ID << _ID << "_" << this->_cycleTime << SCHED_HTML_JS_DIV_ID_END << std::endl;
  /* myfile << "    var chart" << _ID << "_" << this->_cycleTime << " = new Chart( "<<
    "ctx" << _ID << "_" << this->_cycleTime << ", {\n \
    type : \"pie\",\n";
  myfile << "data : data " << _ID << this->_cycleTime <<",\n";
  myfile << SCHED_HTML_JS_CONTENT2 << std::endl;*/
  //myfile << SCHED_HTML_JS_BUTTON1 << _ID << "_" << this->_cycleTime << SCHED_HTML_JS_BUTTON2 << std::endl;
  myfile << SCHED_HTML_JS_DIV_BEGIN2 << std::endl;
  myfile << SCHED_HTML_JS_BEGIN_CANVAS << _ID << "_" << this->_cycleTime << SCHED_HTML_JS_END_CANVAS << std::endl;
  myfile << SCHED_HTML_JS_DIV_END << std::endl;
    
}
  
   

void CPU::schedule2HTML(std::ofstream& myfile) const {  
  // myfile << "<h2><span>Scheduling for device: "<< _name <<"_core_"<<this->_cycleTime<< "</span></h2>" << std::endl;
  myfile << SCHED_HTML_DIV << SCHED_HTML_BOARD;
  myfile << _name << "_core_" << this->_cycleTime << END_TD << "</tr>" << std::endl;
  myfile << SCHED_HTML_JS_TABLE_END << std::endl;
  myfile << SCHED_HTML_BOARD2 << std::endl;
  if ( _transactList.size() == 0 ) {
    myfile << "<h4>Device never activated</h4>" << std::endl;
    myfile << SCHED_HTML_JS_CLEAR << std::endl;
  }
  else {
    //myfile << "<table>" << std::endl << "<tr>";
    myfile  << "<tr>";
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
	std::string aCurrTransName=aCurrTrans->toShortString();
	unsigned int indexTrans=aCurrTransName.find_first_of(":");
	std::string aCurrContent=aCurrTransName.substr(indexTrans+1,2);
	writeHTMLColumn( myfile, aLength, cellClass, aCurrTrans->toShortString(), aCurrContent );

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

    myfile << "</tr>" << std::endl << "</table>" << std::endl << SCHED_HTML_JS_DIV_END << std::endl;
    myfile << SCHED_HTML_JS_CLEAR << std::endl;
  
    /* for( std::map<TMLTask*, std::string>::iterator taskColIt = taskCellClasses.begin(); taskColIt != taskCellClasses.end(); ++taskColIt ) {
      TMLTask* task = (*taskColIt).first;
      // Unset the default td max-width of 5px. For some reason setting the max-with on a specific t style does not work
      myfile << "<td class=\"" << taskCellClasses[ task ] << "\"></td><td style=\"max-width: unset;\">" << task->toString() << "</td><td class=\"space\"></td>";
      }*/

    //myfile << "</tr>" << std::endl;

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
    //  myfile << "</table>" << std::endl;
  }
}
