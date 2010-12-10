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

#ifndef AvTransitionH
#define AvTransitionH
#include <definitions.h>
#include <AvNode.h>
#include <AvCheckpoint.h>
#include <EventQueueCallback.h>

enum state_enum{DISABLED, WAIT4AFTER, WAIT4COMP, WAIT4CMD};
typedef enum state_enum TransitionState;

class AvCmd;

class AvTransition: public AvNode, public AvCheckpoint, public EventQueueCallback{
public:
	AvTransition(ID iID, AvBlock* iBlock, CondFuncPointer iCondFunc, AVTTime iAfterMin, AVTTime iAfterMax, AVTTime iComputeMin, AVTTime iComputeMax, ActionFuncPointer iActionFunc);
	~AvTransition();
	AvNode* prepare(bool iControlTransfer);
	AvNode* execute(const SystemTransition& iSyncCmd);
	bool isEnabled(EnabledTransList& iEnaTransList, AvTransition* iIncomingTrans);
	AvNode* cancel();
	void eventQCallback();
	void setOutgoingCmd(AvCmd* iCmd);
	AvCmd* getOutgoingCmd();
protected:
	AvCmd* _outgoingCmd;
	CondFuncPointer _condFunc;
	AVTTime _afterMin, _afterMax, _computeMin, _computeMax;
	ActionFuncPointer _actionFunc;
	TransitionState _state;
	bool _lastControlTransfer;
};

#endif
