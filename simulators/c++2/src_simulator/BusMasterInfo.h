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

#ifndef BusMasterInfoH
#define BusMasterInfoH

///Structure encapsulating information about a bus master and bus accesses it has performed 
class BusMasterInfo{
	public:
		///Constructor
		/**
		\param iPrio Priority of the bus master
		*/
		BusMasterInfo(unsigned int iPrio):_priority(iPrio), _contentionDelay(0), _noTransactions(0){}
		///Calculates the average contention delay of all registered bus transactions
		/**
		\return Average contention delay 
		*/
		float getContentionDelay() const{
			return (_noTransactions==0)?0:(static_cast<float>(_contentionDelay)/static_cast<float>(_noTransactions));
		}
		///Updates the internal variables used to calculate the statistics for the average contention delay of a bus master
		/**
		\param  iContentionDelay Contention delay of a recently executed transaction 
		*/
		void addContention(unsigned long iContentionDelay){
			_contentionDelay+=iContentionDelay;
			_noTransactions++;
		}
		///Returns the priority of the bus master
		/**
		\return Priority of the bus master
		*/
		unsigned int getPriority() const{
			return _priority;
		}
		void reset(){
 			_contentionDelay=0;
			_noTransactions=0;
		}
	protected:
		///Priority of the bus master
		unsigned int _priority;
		///Sum of the contention delay of all registered transactions	
		unsigned long _contentionDelay;
		///Number of registered transactions
		unsigned long _noTransactions;
};
#endif
