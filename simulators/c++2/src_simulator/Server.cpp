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
#include<Server.h>
#include<Simulator.h>

Server::Server():_socketClient(-1){
	pthread_mutex_init(&_replyMutex, NULL);
}

int Server::run(){
	int aSocketServer;	// listen on aSocketServer, client connection on _socketClient
	struct addrinfo aHints; 		//aHints for getaddrinfo
	struct addrinfo *aServerInfo;		//information about the server, created by getaddrinfo
	struct sockaddr_storage aClientAddrInfo;// connector's address information
	int aRetVal;				//return value for getaddrinfo

	memset(&aHints, 0, sizeof aHints);
	aHints.ai_family = AF_UNSPEC;
	aHints.ai_socktype = SOCK_STREAM;
	aHints.ai_flags = AI_PASSIVE; // use my IP

	//get address information about server: getaddrinfo(hostname, port, hints, result)
	if ((aRetVal = getaddrinfo(NULL, PORT, &aHints, &aServerInfo)) != 0) {
		std::cerr << "getaddrinfo: " << gai_strerror(aRetVal) << std::endl;
		return 1;
	}

	// loop through all the results and bind to the first we can
	bool bound=false;
	struct addrinfo *p;
	for(p = aServerInfo; p != NULL && !bound; p = p->ai_next) {
		//get the file descriptor: socket(IPdomain, typeOfConnection, protocol)
		if ((aSocketServer = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1) {
			perror("server: socket");
		}else{
			//set socket options: setsockopt(socket, level, optname, optval, optlen)
			int yes=1;
			if (setsockopt(aSocketServer, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1) {
				perror("setsockopt");
				exit(1);
			}
			//bind socket to a specific port of the server: bind(filedescriptor, address data structure, addresslen)
			if (bind(aSocketServer, p->ai_addr, p->ai_addrlen) == -1) {
				close(aSocketServer);
				perror("server: bind");
			}else
				bound=true;
        	}
	}

	if (p == NULL){
		std::cerr << "server: failed to bind\n";
		exit(1);
	}
	
	freeaddrinfo(aServerInfo);
	
	do{
		//causes the socket to listen to incoming connections: listen(filedescriptor, number of connections)
		if (listen(aSocketServer, BACKLOG) == -1) {
			perror("listen");
			exit(1);
		}
	
		std::cout << "server: waiting for connections...\n";
		socklen_t aAddrInfoSize= sizeof(struct sockaddr_storage);
		while((_socketClient = accept(aSocketServer, (struct sockaddr *)&aClientAddrInfo, &aAddrInfoSize))==-1) {  // main accept loop
			//accept a connection: accept(filedescriptor, pointer to address information of caller, size of address information)
			perror("accept");
		}
		char* aMsg="You are connected to the simulation server. Please enter a command:\0";
		if (send(_socketClient, aMsg, strlen(aMsg), 0) == -1) perror("send");
		int aNumberOfBytes=1;
		char s[INET6_ADDRSTRLEN];
		inet_ntop(aClientAddrInfo.ss_family, get_in_addr((struct sockaddr *)&aClientAddrInfo), s, sizeof s);
		std::cout << "server: got connection from " << s << std::endl;
		char aTmpBuffer[BUFFER_SIZE];
		while(!_syncInfo->_terminate && aNumberOfBytes>0){
			aNumberOfBytes=recv(_socketClient, aTmpBuffer, BUFFER_SIZE-1, 0);
			if (aNumberOfBytes < 1){
				std::cout << "Broken connection detected, error code: " << aNumberOfBytes << std::endl;
				_socketClient=-1;
				//perror("receive");
			}else{
				aTmpBuffer[aNumberOfBytes]='\0';
				if (strlen(aTmpBuffer)>0){
					std::cout << "Command received: " << aTmpBuffer << std::endl;
					if (!_syncInfo->_simulator->execAsyncCmd(aTmpBuffer)){
						if (pthread_mutex_trylock(&_syncInfo->_mutexProduce)==0){
							strcpy(_syncInfo->_command,aTmpBuffer);
							pthread_mutex_unlock(&_syncInfo->_mutexConsume);		
						}else{
							_syncInfo->_simulator->sendStatus();
						}
					}
				}
			}	
		}
	}while (!_syncInfo->_terminate);
	std::cout << "Server loop terminated" << std::endl;
	//sendReply("Terminate simulator\n");
	int aSocketClient=_socketClient;
	pthread_mutex_lock(&_replyMutex);
	_socketClient=-1;
	pthread_mutex_unlock(&_replyMutex);
	close(aSocketClient);
	std::cout << "Socket client closed" << std::endl;
	close(aSocketServer);
	std::cout << "Socket server closed" << std::endl;
}

void* Server::get_in_addr(struct sockaddr *sa) const{
	if (sa->sa_family == AF_INET) {
		return &(reinterpret_cast<struct sockaddr_in*>(sa)->sin_addr);
	}
	return &(reinterpret_cast<struct sockaddr_in6*>(sa)->sin6_addr);
}

void Server::sendReply(std::string iReplyStr){
	pthread_mutex_lock(&_replyMutex);
	if (_socketClient!=-1){
		if (send(_socketClient, iReplyStr.c_str(), iReplyStr.length(), 0) == -1) perror("send");
	}
	pthread_mutex_unlock(&_replyMutex);
}
