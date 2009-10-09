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
#ifndef ServerH
#define ServerH

#include <definitions.h>
#include <ServerIF.h>

class Simulator;
class SimServSyncInfo;

///Class encapsulating TCP server capabilities
class Server: public ServerIF{
public:
	///Constructor
	Server();
	///Run the server
	int run();
	void sendReply(std::string iReplyStr);
protected:
	///Returns position of character in string and and replaces it with 0
	/**
	\param iBuffer Pointer to buffer
	\param searchCh Character to search for
	\param iStart Start Index
	\param iLength String length
	\return Index of character
	*/
	int getPositionOf(char* iBuffer, char searchCh, int iStart, int iLength);
	///Determines the IP adress of the client
	void* get_in_addr(struct sockaddr *sa) const;
	///pointer to synchronization structure
	int _socketClient;
	///Mutex protecting the reply function of the Server
	pthread_mutex_t _replyMutex;
};
#endif
