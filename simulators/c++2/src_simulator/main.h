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

#ifndef MainH
#define MainH

void *SimThreadFunc( void *ptr ){
	Simulator* mySim = static_cast<Simulator*>(ptr);
	mySim->run();
	pthread_exit(NULL);
}

void *ServThreadFunc( void *ptr ){
	ServerIF* myServer = static_cast<ServerIF*>(ptr);
	myServer->run();
	pthread_exit(NULL);
}

//********** MAIN NEW SIMULATOR **********

int main(int len, char ** args) {
	struct timeval begin, end;
	SimServSyncInfo mySync;
	gettimeofday(&begin,NULL);
	mySync._simComponents = new CurrentComponents();
	Simulator mySim(&mySync);
	//mySync._simComponents = new CurrentComponents(&mySim);
	mySync._simComponents->setSimulator(&mySim);
	//mySim.init();
	mySync._simComponents->generateTEPEs();
	TMLCommand::setSimComponents(mySync._simComponents);
#ifdef EBRDD_ENABLED
	ERB::setSimComponents(mySync._simComponents);
	ERC::setSimComponents(mySync._simComponents);
#endif
	//ESO::setSimComponents(mySync._simComponents);
	gettimeofday(&end,NULL);
	std::cout << "The preparation took " << getTimeDiff(begin,end) << "usec.\n";
	ServerIF* myServer = mySim.run(len, args);
	if (myServer!=0){
		//Server myServer(&mySync);
		myServer->setSimSyncInfo(&mySync);
		mySync._simulator=&mySim;
		mySync._server=myServer;
		pthread_t aThreadSim, aThreadServ;
		int  aRetVal;
		aRetVal = pthread_create(&aThreadSim, NULL, SimThreadFunc, static_cast<void*>(&mySim));
		aRetVal = pthread_create(&aThreadServ, NULL, ServThreadFunc, static_cast<void*>(myServer));
		pthread_join(aThreadSim, NULL);
		pthread_join(aThreadServ, NULL);
		pthread_exit(NULL);
		delete myServer;
	}
	delete mySync._simComponents;
	std::cout << "Terminate\n";
	return 0;
}
#endif
