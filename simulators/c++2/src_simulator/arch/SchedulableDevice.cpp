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
						_dynamic_consumPerCycle (35),
						_cycleTime(0) {
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
	maxScale = 0;
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
						const std::string title) {
	writeHTMLColumn( myfile, colSpan, cellClass, title, "", true );
}


void SchedulableDevice::writeHTMLColumn(	std::ofstream& myfile,
						const unsigned int colSpan,
						const std::string cellClass,
						const std::string title,
						const std::string content) {
	writeHTMLColumn( myfile, colSpan, cellClass, title, content, true );
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

	if ( colSpan == 1) {
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


double SchedulableDevice::averageLoad() const{
  double _averageLoad=0;
  TMLTime _maxEndTime=0;
  for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
      TMLTime _endTime= (*i)->getEndTime();
      _maxEndTime=max(_maxEndTime,_endTime);
  }
  std::cout<<"max end time is "<<_maxEndTime<<std::endl;
  for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
     _averageLoad += (*i)->getEndTime() - (*i)->getStartTime();  
  
  }
  if(_maxEndTime == 0)
    return 0;
  else {
    _averageLoad = (double)_averageLoad/_maxEndTime;
    return _averageLoad;
  }
  
}

void SchedulableDevice::drawPieChart(std::ofstream& myfile) const { 
  TMLTime _maxEndTime=0;

  for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
    TMLTime _endTime= (*i)->getEndTime();
    _maxEndTime=max(_maxEndTime,_endTime);
  }
   
  std::map <TMLTask*, double > transPercentage;
  for( TransactionList::const_iterator i = _transactList.begin(); i!= _transactList.end(); ++i){     
    transPercentage[(*i)-> getCommand()->getTask()]+=(double)((*i)->getEndTime()-(*i)->getStartTime())/_maxEndTime;           
  }
    
  std::map <TMLTask*, double>::iterator iter = transPercentage.begin();
  myfile << "   var ctx" << _ID << "= $(\"#pie-chartcanvas-" << _ID << "\");\n";
    
  double idle=1;
  myfile << "   var data" << _ID << " = new Array (";
  while( iter != transPercentage.end()){
    myfile << "\"" << iter->second << "\",";
    idle-=iter->second;
    ++iter;
  }
  myfile << "\"" << idle << "\");\n";
    
  myfile << "    var efficiency" << _ID << " = [];" << std::endl;
  myfile << "    var coloR" << _ID << " = [];" << std::endl;
  myfile << "    var dynamicColors" << _ID << SCHED_HTML_JS_FUNCTION;
    
  myfile << "    for (var i in data" << _ID << "){\n";
  myfile << "             efficiency" << _ID << ".push(data" << _ID << "[i]);\n";
  myfile << "             coloR" << _ID << ".push(dynamicColors" << _ID << "());\n";
  myfile << "}" << std::endl;
    
  myfile << "   var data" << _ID << " = { \n";
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
                                           data : efficiency" << _ID << ",\n";
  myfile << "                            backgroundColor : coloR" << _ID << std::endl;
  myfile << SCHED_HTML_JS_CONTENT1;
  myfile << "  var options" << _ID << SCHED_HTML_JS_CONTENT3;
  myfile << _name << ": Average load is " <<  std::setprecision(2) << averageLoad() << SCHED_HTML_JS_CONTENT2 << std::endl; 
 
}
  


void SchedulableDevice::showPieChart(std::ofstream& myfile) const{
  //myfile << SCHED_HTML_JS_BUTTON1 << _ID  << SCHED_HTML_JS_BUTTON2 << std::endl;
  myfile << SCHED_HTML_JS_DIV_BEGIN2 << std::endl;
  myfile << SCHED_HTML_JS_BEGIN_CANVAS << _ID << SCHED_HTML_JS_END_CANVAS <<std::endl;
  myfile << SCHED_HTML_JS_DIV_END << std::endl;
}

void SchedulableDevice::buttonPieChart(std::ofstream& myfile) const{
  // myfile << "$(\"#" << _ID << "\").click(function() {\n";
    myfile << "    var chart" << _ID << " = new Chart( "<<
      "ctx" << _ID << ", {\n \
              type : \"pie\",\n";
    myfile << "               data : data" << _ID << ",\n";
    myfile << "               options : options" << _ID << std::endl << "                   });" << std::endl;
    // myfile << "   chart" << _ID << SCHED_HTML_JS_HIDE;
    myfile << "   if(!" << SHOW_PIE_CHART << "){\n \t"
        << "      document.getElementById(\"pie-chartcanvas-" << _ID << "\"" << ").style.display = \"none\";\n \t"
        << "   }\n \t"
        << "   else {\n \t"
        << "      document.getElementById(\"pie-chartcanvas-"<< _ID << "\"" << ").style.display = \"block\"; \n \t"
        << "   }\n \t";
    myfile << "   chart" << _ID << ".update();" << std::endl;
}

std::map<TMLTask*, std::string> SchedulableDevice::HWTIMELINE2HTML(std::ostringstream& myfile,std::map<TMLTask*, std::string> taskCellClasses,unsigned int nextCellClassIndex, std::string& iTracetaskList) {
    TransactionList _transactListClone;
    std::string taskList = iTracetaskList.c_str();
    for (int z = 0; z < _transactList.size(); z++) {
        std::string taskName = _transactList[z]->getCommand()->getTask()->toString();
        std::size_t pos = taskList.find(taskName); /*pos1 = position of "bin" if we working with open model*/
        if(pos != std::string::npos){
            _transactListClone.push_back(_transactList[z]);
        }
    }
    if ( _transactListClone.size() == 0 ) {
        std::cout << "Device never activated" << std::endl;
    } else {
        myfile << "<tr><td title = \"Average load: " << std::setprecision(2) << averageLoad() << "; Utilization: " << (static_cast<float>(_busyCycles)/static_cast<float>(_simulatedTime)) << "\" width=\"170px\" style=\"max-width: unset;min-width: 170px;background-color: aqua;\">" <<  _name << "</td><td class=\"notfirst\"></td><td class=\"notlast\"></td>";
//        std::map<TMLTask*, std::string> taskCellClasses;
//        unsigned int nextCellClassIndex = 0;
        TMLTime aCurrTime = 0;
        unsigned int tempReduce = 0;
        std::vector<unsigned int> listScale;
        std::vector<unsigned int> listScaleTime;
        listScale.push_back(0);
        listScaleTime.push_back(0);
        bool changeCssClass = false;
        TMLTransaction* checkLastTime = _transactListClone.back();
        for( TransactionList::const_iterator i = _transactListClone.begin(); i != _transactListClone.end(); ++i ) {
          std::cout<<"get transaction core number is: "<<(*i)->getTransactCoreNumber()<<std::endl;
          std::cout<<"time : "<<_cycleTime<<std::endl;
          std::cout << "CPU:calcSTL: html of CPU " << _name << ": " << (*i)->toString() << std::endl;
          //if( (*i)->getTransactCoreNumber() == this->_cycleTime ){
          TMLTransaction* aCurrTrans = *i;
          unsigned int aBlanks = aCurrTrans->getStartTime() - aCurrTime;
        bool isBlankTooBig = false;
        std::ostringstream tempString;
        int tempBlanks;
        if((checkLastTime)->getEndTime() >= MIN_RESIZE_THRESHOLD && aBlanks > MIN_RESIZE_TRANS) {
            int newBlanks = 0;
            if (aBlanks > 100000) {
                newBlanks = (int) aBlanks/100;
            } else if (aBlanks > 250) {
                newBlanks = (int) aBlanks/20;
            } else {
                newBlanks = 10;
            }
            tempBlanks = aBlanks;
            tempReduce += aBlanks - newBlanks;
            aBlanks = newBlanks;
            isBlankTooBig = true;
            changeCssClass = true;
        }
        if ( aBlanks >= 0 && (!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction()) ){
            listScale.push_back(aBlanks+1);
            tempString << tempBlanks+1;
            if(aCurrTrans->getStartTime()+1 > listScaleTime.back()){
                listScaleTime.push_back(aCurrTrans->getStartTime()+1);
            }
            if (isBlankTooBig){
                myfile << "<td colspan=\""<< aBlanks+1 <<"\" title=\"idle time\" class=\"not\">" << "<- idle " + tempString.str() + " ->" << "</td>";
//                writeHTMLColumn( myfile, aBlanks+1, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
            } else {
                myfile << "<td colspan=\""<< aBlanks+1 <<"\" title=\"idle time\" class=\"not\"></td>";
//                writeHTMLColumn( myfile, aBlanks+1, "not", "idle time" );
            }
        }
        else if ( aBlanks > 0 ){
            listScale.push_back(aBlanks);
            tempString << tempBlanks;
            if(aCurrTrans->getStartTime() > listScaleTime.back()){
                listScaleTime.push_back(aCurrTrans->getStartTime());
            }
            if (isBlankTooBig){
                myfile << "<td colspan=\""<< aBlanks <<"\" title=\"idle time\" class=\"not\">" << "<- idle " + tempString.str() + " ->" << "</td>";
//                writeHTMLColumn( myfile, aBlanks, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
            } else {
                myfile << "<td colspan=\""<< aBlanks <<"\" title=\"idle time\" class=\"not\"></td>";
//                writeHTMLColumn( myfile, aBlanks, "not", "idle time" );
            }
        }

        unsigned int aLength = aCurrTrans->getPenalties();

        if ( aLength != 0 ) {
            std::ostringstream title;
            listScaleTime.push_back(listScaleTime.back()+aLength);
            if(checkLastTime->getEndTime() >= MIN_RESIZE_THRESHOLD && aLength > MIN_RESIZE_TRANS){
                int tempLength = 0;
                if (aLength > 100000) {
                    tempLength = (int) aLength/100;
                } else if (aLength > 250) {
                    tempLength = (int) aLength/20;
                } else {
                    tempLength = 10;
                }
                tempReduce += aLength - tempLength;
                aLength = tempLength;
            }
            listScale.push_back(aLength);
            title << "idle:" << aCurrTrans->getIdlePenalty() << " switching penalty:" << aCurrTrans->getTaskSwitchingPenalty();
            myfile << "<td colspan=\"" << aLength << "\" title=\"" << title.str() << "\" class=\"not\"></td>";
//            writeHTMLColumn( myfile, aLength, "not", title.str() );
        }

          aLength = aCurrTrans->getOperationLength();

          // Issue #4
          TMLTask* task = aCurrTrans->getCommand()->getTask();
          const std::string cellClass = determineHTMLCellClass( taskCellClasses, task, nextCellClassIndex);
          std::string aCurrTransName=aCurrTrans->toShortString();
          unsigned int indexTrans=aCurrTransName.find_first_of(":");
          std::string aCurrContent=aCurrTransName.substr(indexTrans+1,2);
          if(!(!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction())){
              if(checkLastTime->getEndTime() >= MIN_RESIZE_THRESHOLD && aLength > MIN_RESIZE_TRANS){
                  int tempLength = 0;
                  if (aLength > 100000) {
                      tempLength = (int) aLength/100;
                  } else if (aLength > 250) {
                      tempLength = (int) aLength/20;
                  } else {
                      tempLength = 10;
                  }
                  tempReduce += aLength - tempLength;
                  aLength = tempLength;
              }
              std::string aCurrFullTransName = aCurrTrans->toString();
              unsigned int indexTrans1 = aCurrFullTransName.find("len:");
              unsigned int indexTrans2 = aCurrFullTransName.find("params:");
              std::string transName = aCurrTransName.substr(0, indexTrans + 1) + " " + aCurrFullTransName.substr(0, indexTrans1) + " --Attributes " + aCurrFullTransName.substr(indexTrans2, aCurrFullTransName.length());
              myfile << "<td colspan=\"" << aLength << "\" title=\"" << transName << "\" class=\""<< cellClass <<"\">"<< aCurrContent <<"</td>";
//              writeHTMLColumn( myfile, aLength, cellClass, aCurrTrans->toShortString(), aCurrContent);
              listScale.push_back(aLength);
              if(aCurrTrans->getStartTime() > listScaleTime.back()){
                 listScaleTime.push_back(aCurrTrans->getStartTime());
              }
              if(aCurrTrans->getEndTime() > listScaleTime.back()){
                 listScaleTime.push_back(aCurrTrans->getEndTime());
              }
          }
          aCurrTime = aCurrTrans->getEndTime();
          // }
        }

        myfile << "</tr>" << "<tr>";
        myfile << "<td width=\"170px\" style=\"max-width: unset;min-width: 170px;border-style: none none none none;\"></td><td class=\"notfirst\"></td><td class=\"notlast\"></td>";

        for ( unsigned int aLength = 0; aLength < listScale.size(); aLength += 1 ) {
          std::ostringstream spanVal;
          if(aLength < listScaleTime.size())
            spanVal << listScaleTime[aLength];
          else
            spanVal << "";
          if(aLength+1 >= listScale.size()){

            if(changeCssClass){
                myfile << "<td colspan=\"5\" class=\"sc1\">" << spanVal.str() << "</td>";
//                writeHTMLColumn( myfile, 5, "sc1",  spanVal.str(), spanVal.str(), false );
            } else
                myfile << "<td colspan=\"5\" class=\"sc\">" << spanVal.str() << "</td>";
//                writeHTMLColumn( myfile, 5, "sc", spanVal.str(), spanVal.str(), false );
          }else {
            if(changeCssClass){
                myfile << "<td colspan=\"" << listScale[aLength+1] << "\" class=\"sc1\">" << spanVal.str() << "</td>";
//                writeHTMLColumn( myfile, listScale[aLength+1], "sc1", spanVal.str(), spanVal.str(), false );
            } else
                myfile << "<td colspan=\"" << listScale[aLength+1] << "\" class=\"sc\">" << spanVal.str() << "</td>";
//                writeHTMLColumn( myfile, listScale[aLength+1], "sc", spanVal.str(), spanVal.str(), false );
            }
        }
        myfile << "</tr>" << "<tr>";
        for ( unsigned int aLength = 0; aLength < 3; aLength++ ) {
            if( aLength == 1) {
              myfile << "<th class=\"notfirst\">";
            } else {
              myfile << "<th></th>";
            }
        }
        if (aCurrTime - tempReduce + 2 > maxScale) {
            maxScale = aCurrTime - tempReduce + 2;
        }
        myfile << "</tr>";
        myfile << SCHED_HTML_JS_CLEAR << std::endl;
    }
    return taskCellClasses;
}

void SchedulableDevice::HW2HTML(std::ofstream& myfile) const {    
  //	myfile << "<h2><span>Scheduling for device: "<< _name << "</span></h2>" << std::endl;
  myfile << SCHED_HTML_BOARD;
  myfile << _name  << END_TD << "</tr>" << std::endl;
  myfile << SCHED_HTML_JS_TABLE_END << std::endl;
  myfile << SCHED_HTML_BOARD2 << std::endl;
  if ( _transactList.size() == 0 ) {
    myfile << "<h4>Device never activated</h4>" << std::endl;
    myfile << SCHED_HTML_JS_TABLE_END << std::endl << SCHED_HTML_JS_CLEAR << std::endl;
  }
  else {
    //myfile << "<table>" << std::endl << "<tr>";
    myfile << "<tr>";
    std::map<TMLTask*, std::string> taskCellClasses;
    unsigned int nextCellClassIndex = 0;
    TMLTime aCurrTime = 0;
    unsigned int tempReduce = 0;
    std::vector<unsigned int> listScale;
    std::vector<unsigned int> listScaleTime;
    listScale.push_back(0);
    listScaleTime.push_back(0);
    bool changeCssClass = false;
    TMLTransaction* checkLastTime = _transactList.back();
    for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
      std::cout<<"get transaction core number is: "<<(*i)->getTransactCoreNumber()<<std::endl;
      std::cout<<"time : "<<_cycleTime<<std::endl;
      std::cout << "CPU:calcSTL: html of CPU " << _name << ": " << (*i)->toString() << std::endl;
      //if( (*i)->getTransactCoreNumber() == this->_cycleTime ){
      TMLTransaction* aCurrTrans = *i;
      unsigned int aBlanks = aCurrTrans->getStartTime() - aCurrTime;
	bool isBlankTooBig = false;
	std::ostringstream tempString;
	int tempBlanks;
	if((checkLastTime)->getEndTime() >= 250 && aBlanks > 10) {
	    int newBlanks = 10;
	    tempBlanks = aBlanks;
	    tempReduce += aBlanks - newBlanks;
	    aBlanks = newBlanks;
	    isBlankTooBig = true;
        changeCssClass = true;
	}
	if ( aBlanks >= 0 && (!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction()) ){
	    listScale.push_back(aBlanks+1);
	    tempString << tempBlanks+1;
	    if(aCurrTrans->getStartTime()+1 > listScaleTime.back()){
            listScaleTime.push_back(aCurrTrans->getStartTime()+1);
        }
	    if (isBlankTooBig){
	        writeHTMLColumn( myfile, aBlanks+1, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
	    } else {
	        writeHTMLColumn( myfile, aBlanks+1, "not", "idle time" );
	    }
	}
	else if ( aBlanks > 0 ){
	    listScale.push_back(aBlanks);
	    tempString << tempBlanks;
	    if(aCurrTrans->getStartTime() > listScaleTime.back()){
            listScaleTime.push_back(aCurrTrans->getStartTime());
        }
	    if (isBlankTooBig){
            writeHTMLColumn( myfile, aBlanks, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
        } else {
            writeHTMLColumn( myfile, aBlanks, "not", "idle time" );
        }
	}

      unsigned int aLength = aCurrTrans->getPenalties();

      if ( aLength != 0 ) {
	std::ostringstream title;
    listScaleTime.push_back(listScaleTime.back()+aLength);
    if(checkLastTime->getEndTime() >= 250 && aLength >10){
        tempReduce += aLength - 10;
        aLength = 10;
    }
    listScale.push_back(aLength);
	title << "idle:" << aCurrTrans->getIdlePenalty() << " switching penalty:" << aCurrTrans->getTaskSwitchingPenalty();
	writeHTMLColumn( myfile, aLength, "not", title.str() );
      }

      aLength = aCurrTrans->getOperationLength();

      // Issue #4
      TMLTask* task = aCurrTrans->getCommand()->getTask();
      const std::string cellClass = determineHTMLCellClass( taskCellClasses, task, nextCellClassIndex );
      std::string aCurrTransName=aCurrTrans->toShortString();
      unsigned int indexTrans=aCurrTransName.find_first_of(":");
      std::string aCurrContent=aCurrTransName.substr(indexTrans+1,2);
      if(!(!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction())){
          if(checkLastTime->getEndTime() >= 250 && aLength >10){
              tempReduce += aLength - 10;
              aLength = 10;
          }
          writeHTMLColumn( myfile, aLength, cellClass, aCurrTrans->toShortString(), aCurrContent);
          listScale.push_back(aLength);
          if(aCurrTrans->getStartTime() > listScaleTime.back()){
             listScaleTime.push_back(aCurrTrans->getStartTime());
          }
          if(aCurrTrans->getEndTime() > listScaleTime.back()){
             listScaleTime.push_back(aCurrTrans->getEndTime());
          }
      }
      aCurrTime = aCurrTrans->getEndTime();
      // }
    }
		

    myfile << "</tr>" << std::endl << "<tr>";

    for ( unsigned int aLength = 0; aLength < aCurrTime - tempReduce; aLength++ ) {
      myfile << "<th></th>";
    }

    myfile << "</tr>" << std::endl << "<tr>";

    for ( unsigned int aLength = 0; aLength < listScale.size(); aLength += 1 ) {
      std::ostringstream spanVal;
      if(aLength < listScaleTime.size())
        spanVal << listScaleTime[aLength];
      else
        spanVal << "";
      if(aLength+1 >= listScale.size()){

        if(changeCssClass){
            writeHTMLColumn( myfile, 5, "sc1",  spanVal.str(), spanVal.str(), false );
        } else
            writeHTMLColumn( myfile, 5, "sc", spanVal.str(), spanVal.str(), false );
      }else {
        if(changeCssClass){
            writeHTMLColumn( myfile, listScale[aLength+1], "sc1", spanVal.str(), spanVal.str(), false );
        } else
            writeHTMLColumn( myfile, listScale[aLength+1], "sc", spanVal.str(), spanVal.str(), false );
        }
      //myfile << "<td colspan=\"5\" class=\"sc\">" << aLength << "</td>";
    }

    myfile << "</tr>" << std::endl << "</table>" << std::endl << SCHED_HTML_JS_DIV_END << std::endl;
    myfile << SCHED_HTML_JS_CLEAR << std::endl;
  }
}

  
void SchedulableDevice::schedule2XML(std::ostringstream& glob,std::ofstream& myfile) const{
  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
      (*i)->toXML(glob, 0, _name, _ID);
     // myfile << glob.str() << std::endl;

  }
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
        unsigned int tempReduce = 0;
        std::vector<unsigned int> listScale;
        std::vector<unsigned int> listScaleTime;
        listScale.push_back(0);
        listScaleTime.push_back(0);
        bool changeCssClass = false;
        TMLTransaction* checkLastTime = _transactList.back();
		for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
		  std::cout<<"get transaction core number is: "<<(*i)->getTransactCoreNumber()<<std::endl;
		  std::cout<<"time : "<<_cycleTime<<std::endl;
		  std::cout << "CPU:calcSTL: html of CPU " << _name << ": " << (*i)->toString() << std::endl;
		  //if( (*i)->getTransactCoreNumber() == this->_cycleTime ){
			TMLTransaction* aCurrTrans = *i;
			unsigned int aBlanks = aCurrTrans->getStartTime() - aCurrTime;
            bool isBlankTooBig = false;
            std::ostringstream tempString;
            int tempBlanks;
            if(checkLastTime->getEndTime() >= 250 && aBlanks > 10) {
                int newBlanks = 10;
                tempBlanks = aBlanks;
                tempReduce += aBlanks - newBlanks;
                aBlanks = newBlanks;
                isBlankTooBig = true;
                changeCssClass = true;
            }
            if ( aBlanks >= 0 && (!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction()) ){
                listScale.push_back(aBlanks+1);
                tempString << tempBlanks+1;
     	        if(aCurrTrans->getStartTime()+1 > listScaleTime.back()){
                    listScaleTime.push_back(aCurrTrans->getStartTime()+1);
                }
                if (isBlankTooBig){
                    writeHTMLColumn( myfile, aBlanks+1, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
                } else {
                    writeHTMLColumn( myfile, aBlanks+1, "not", "idle time" );
                }
            }
            else if ( aBlanks > 0 ){
                listScale.push_back(aBlanks);
                tempString << tempBlanks;
                if(aCurrTrans->getStartTime() > listScaleTime.back()){
                    listScaleTime.push_back(aCurrTrans->getStartTime());
                }
                if (isBlankTooBig){
                    writeHTMLColumn( myfile, aBlanks, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
                } else {
                    writeHTMLColumn( myfile, aBlanks, "not", "idle time" );
                }
            }

			unsigned int aLength = aCurrTrans->getPenalties();

			if ( aLength != 0 ) {
				std::ostringstream title;
                listScaleTime.push_back(listScaleTime.back()+aLength);
                if (checkLastTime->getEndTime() >= 250 && aLength > 10){
                    tempReduce += aLength - 10;
                    aLength = 10;
                }
                listScale.push_back(aLength);
				title << "idle:" << aCurrTrans->getIdlePenalty() << " switching penalty:" << aCurrTrans->getTaskSwitchingPenalty();
				writeHTMLColumn( myfile, aLength, "not", title.str() );
			}

			aLength = aCurrTrans->getOperationLength();

			// Issue #4
			TMLTask* task = aCurrTrans->getCommand()->getTask();
			const std::string cellClass = determineHTMLCellClass( taskCellClasses, task, nextCellClassIndex );
            if(!(!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction())){
              if(checkLastTime->getEndTime() >= 250 && aLength >10){
                  tempReduce += aLength - 10;
                  aLength = 10;
              }
              writeHTMLColumn( myfile, aLength, cellClass, aCurrTrans->toShortString() );
              listScale.push_back(aLength);
              if(aCurrTrans->getStartTime() > listScaleTime.back()){
                 listScaleTime.push_back(aCurrTrans->getStartTime());
              }
              if(aCurrTrans->getEndTime() > listScaleTime.back()){
                listScaleTime.push_back(aCurrTrans->getEndTime());
              }
            }

			aCurrTime = aCurrTrans->getEndTime();
		 // }
		}
		

		myfile << "</tr>" << std::endl << "<tr>";

		for ( unsigned int aLength = 0; aLength < aCurrTime - tempReduce; aLength++ ) {
			myfile << "<th></th>";
		}

		myfile << "</tr>" << std::endl << "<tr>";

        for ( unsigned int aLength = 0; aLength < listScale.size(); aLength += 1 ) {
          std::ostringstream spanVal;
          if(aLength < listScaleTime.size())
            spanVal << listScaleTime[aLength];
          else
            spanVal << "";
          if(aLength+1 >= listScale.size()){

            if(changeCssClass){
                writeHTMLColumn( myfile, 5, "sc1",  spanVal.str(), spanVal.str(), false );
            } else
                writeHTMLColumn( myfile, 5, "sc", spanVal.str(), spanVal.str(), false );
          }else {
            if(changeCssClass){
                writeHTMLColumn( myfile, listScale[aLength+1], "sc1", spanVal.str(), spanVal.str(), false );
            } else
                writeHTMLColumn( myfile, listScale[aLength+1], "sc", spanVal.str(), spanVal.str(), false );
            }
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


