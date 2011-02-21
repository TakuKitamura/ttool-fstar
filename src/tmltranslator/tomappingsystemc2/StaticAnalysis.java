package tmltranslator.tomappingsystemc2;

import java.util.*;
import tmltranslator.*;
import java.util.regex.*;
//import java.util.HashSet;

public class StaticAnalysis{
	private ArrayList<LiveVariableNode> liveNodes;
	private final static Pattern _varPattern = Pattern.compile("[\\w&&\\D]+[\\w]*");
	private TMLTask _task;
	private ArrayList<TMLChannel> _channels;
	private ArrayList<TMLEvent> _events;
	private ArrayList<TMLRequest> _requests;
	
	private int _nextDefID=0;
	private int[][] _varToDefs=null;
	private int _bytesForDefs;
	private int _bytesForVars;
	private LiveVariableNode[] _defLookup=null;
	private Set<Integer> _depChannels;

	public StaticAnalysis(TMLTask iTask, ArrayList<TMLChannel>iChannels, ArrayList<TMLEvent> iEvents, ArrayList<TMLRequest> iRequests, Set<Integer> iDepChannels){
		_task = iTask;
		_channels=iChannels;
		_events=iEvents;
		_requests=iRequests;
		_depChannels = iDepChannels;
	}
	
	public int getNextDefID(){
		return _nextDefID++;
	}
	
	/*public int getNbOfDefs(){
		return _nextDefID;
	}*/
	
	public int getBytesForDefs(){
		return _bytesForDefs;
	}
	
	public int[][] getDefsForVar(){
		return _varToDefs;
	}
	
	public LiveVariableNode[] getDefLookUp(){
		return _defLookup;
	}

	public void addDepChannel(int iID){
		System.out.println("Add Dependent Channel: " + iID);
		_depChannels.add(iID);
	}

	public boolean isChannelDep(int iID){
		System.out.println("Check if Channel dep: " + iID + " answer: "  + _depChannels.contains(iID));
		return _depChannels.contains(iID);
	}

	private int getVarSeqNoByName(String attName){
		int aSeq=0;
		for(TMLAttribute att: _task.getAttributes()) {
			if (att.name.equals(attName)) return aSeq;
			aSeq++;
		}
		return -1;
	}

	private int[] parseExprToVariableMap(String iExpr, int[] iMap){
		//byte[] aResMap;
		int[] aResMap;
		if(iMap==null)
			aResMap=new int[_bytesForVars];
		else
			aResMap=iMap;
		if(!(iExpr==null || iExpr.isEmpty())){
			//System.out.println("Examine expression: " + iExpr);
			Matcher matcher = _varPattern.matcher(iExpr);
			//System.out.print("Found tokens: ");
			while (matcher.find()){
				String token = iExpr.substring(matcher.start(), matcher.end());
				//System.out.print(token + ", ");
				int aVarSeqNo = getVarSeqNoByName(token);
				if (aVarSeqNo>=0) aResMap[aVarSeqNo >>> 5] |= 1 << (aVarSeqNo & 0x1F);
			}
			//System.out.println();
			//System.out.println("Byte sequence: ");
		}
		/*for(int i=0; i<_bytesForVars; i++)
			System.out.print(((int)aResMap[i]) + ", ");
		System.out.println();*/
		return aResMap;		
	}

	private int[] parseChannelToVariableMap(TMLChannel iChannel, int[] iMap){
		int[] aResMap;
		if(iMap==null)
			aResMap=new int[_bytesForVars];
		else
			aResMap=iMap;
		int anIndex = _task.getAttributes().size() + _channels.indexOf(iChannel);
		aResMap[anIndex >>> 5] |= 1 << (anIndex & 0x1F);
		return aResMap;	
	}

	private int[] parseEventToVariableMap(TMLEvent iEvent){
		int[] aResMap = new int[_bytesForVars];
		int anIndex = _task.getAttributes().size() + _channels.size() + _events.indexOf(iEvent);
		aResMap[anIndex >>> 5] |= 1 << (anIndex & 0x1F);
		return aResMap;
	}
	
	/*public String makeLiveVarEvalFunc(){
		String anEvalFunc="";
		//= "unsigned long getStateHash(const char* iLiveVarList) const{\n";
		int aSeq=0;
		for(TMLAttribute att: _task.getAttributes()) {
			anEvalFunc += "if (iLiveVarList[" + (aSeq >>> 3) + "] && " + (1 << (aSeq & 0x7)) + "!=0) iHash->addValue(" + att.getName() + ");\n";
			aSeq++;
		}
		for(TMLChannel ch: _channels) {
			anEvalFunc += "if (iLiveVarList[" + (aSeq >>> 3) + "] && " + (1 << (aSeq & 0x7)) + "!=0) " + ch.getExtendedName() + "->getStateHash(iHash);\n";
			aSeq++;
		}
		for(TMLEvent evt: _events) {
			anEvalFunc += "if (iLiveVarList[" + (aSeq >>> 3) + "] && " + (1 << (aSeq & 0x7)) + "!=0) " + evt.getExtendedName() + "->getStateHash(iHash);\n";
			aSeq++;
		}
		if (_task.isRequested())
			anEvalFunc += "if (iLiveVarList[" + (aSeq >>> 3) + "] && " + (1 << (aSeq & 0x7)) + "!=0) requestChannel->getStateHash(iHash);\n"; 
		//anEvalFunc += "}\n\n";
		return anEvalFunc;
	}*/

	private void printLiveVarNode(LiveVariableNode iNode){
		int aSeq=0;
		for(TMLAttribute att: _task.getAttributes()) {
			if ((iNode.getOutVars()[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
				System.out.println(att.getName() + ": significant");
			aSeq++;
		}
		for(TMLChannel ch: _channels) {
			if ((iNode.getOutVars()[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
				System.out.println(ch.getName() + ": significant");
			aSeq++;
		}
		for(TMLEvent evt: _events) {
			if ((iNode.getOutVars()[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
				System.out.println(evt.getName() + ": significant");
			aSeq++;
		}
		if (_task.isRequested() && (iNode.getOutVars()[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
			System.out.println("reqChannel: significant");
	}

	public String printVariables(int[] iVariables){
		String aResult="";
		int aSeq=0;
		for(TMLAttribute att: _task.getAttributes()) {
			if ((iVariables[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
				aResult+= att.getName() + ",";
			aSeq++;
		}
		for(TMLChannel ch: _channels) {
			if ((iVariables[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
				aResult+= ch.getName() + ",";
			aSeq++;
		}
		for(TMLEvent evt: _events) {
			if ((iVariables[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
				aResult+= evt.getName() + ",";
			aSeq++;
		}
		if (_task.isRequested() && (iVariables[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
			aResult += "reqCh";
		return aResult;
	}

	public LiveVariableNode startAnalysis(){
		int aNbOfLiveElements = _task.getAttributes().size() + _channels.size() + _events.size();
		//int aNumberOfBytes=0;
		//LiveVariableNode.reset();
		liveNodes = new ArrayList<LiveVariableNode>();
		if (_task.isRequested()) aNbOfLiveElements++;		
		_bytesForVars = aNbOfLiveElements >>> 5;
		if ((aNbOfLiveElements & 0x1F)!=0) _bytesForVars++;
		LiveVariableNode aStartNode=null, aLastNode=null;
		//_depChannels.clear();
		for(TMLAttribute att: _task.getAttributes()) {
			if (att.hasInitialValue())
				aStartNode = new LiveVariableNode(this, new int[_bytesForVars], parseExprToVariableMap(att.name, null), null, null,
			false, att.name, att.initialValue);			
			else
				aStartNode = new LiveVariableNode(this, new int[_bytesForVars], parseExprToVariableMap(att.name, null), null, null,
			false, att.name, "0");
			liveNodes.add(aStartNode);
			aStartNode.setNodeInfo("init " + att.getName());
			if (aLastNode!=null) aLastNode.setSuccessor(aStartNode);
			aLastNode=aStartNode;
		}
		if (_task.isRequested()){
			int[] aReqVars = parseExprToVariableMap("arg1__req", null);
			for (int i=2; i<=_task.getRequest().getNbOfParams(); i++){
				parseExprToVariableMap("arg" + i + "__req", aReqVars);
			}
			//parseExprToVariableMap("arg2__req", aReqVars);
			//parseExprToVariableMap("arg3__req", aReqVars);
			int[] aReqChannelVar = new int[_bytesForVars];
			aReqChannelVar[_bytesForVars-1] = 1 << ((aNbOfLiveElements-1) & 0x1F);
			aStartNode = new LiveVariableNode(this, aReqChannelVar, aReqVars, null, null, true);
			for(TMLRequest aReq: _requests)
				if (aReq.getNbOfParams()>0){ aStartNode.setVarDepSource(true); break;}
			liveNodes.add(aStartNode);
			aStartNode.setNodeInfo("rec req");
			aStartNode.setSuccessor(buildLiveAnalysisTree(_task.getActivityDiagram().getFirst(), aStartNode, null));
		}else{
			aStartNode = buildLiveAnalysisTree(_task.getActivityDiagram().getFirst(), null, null);
		}
		if (aLastNode!=null) aLastNode.setSuccessor(aStartNode);
		boolean aChange;
		int aConstChange;
		do{
			//System.out.println("******************* one analysis round");
			_nextDefID=0;
			for(LiveVariableNode aLiveNode: liveNodes)
				aLiveNode.liveVariableInit();
			do{
				aChange=false;
				for(LiveVariableNode aLiveNode: liveNodes)
					aChange |= aLiveNode.liveVariableAnalysis();
			}while(aChange);
			_bytesForDefs = _nextDefID >>> 5;
			if ((_nextDefID & 0x1F)!=0 || _nextDefID==0) _bytesForDefs++;
			_varToDefs = new int[aNbOfLiveElements][_bytesForDefs];
			_defLookup = new LiveVariableNode[_nextDefID];
			for(LiveVariableNode aLiveNode: liveNodes){
				aLiveNode.prepareReachingDefinitions();
			}
			do{
				aChange=false;
				for(LiveVariableNode aLiveNode: liveNodes)
					aChange |= aLiveNode.determineKilledSets();
			}while(aChange);
			for(LiveVariableNode aLiveNode: liveNodes){
				//aLiveNode.printKillEntries();
				aLiveNode.reachingDefinitionsInit();
			}
			do{
				aChange=false;
				for(LiveVariableNode aLiveNode: liveNodes)
					aChange |= aLiveNode.reachingDefinitionAnalysis();
			}while(aChange);
			aConstChange=0;
			do{
				aConstChange &= 1;
				for(LiveVariableNode aLiveNode: liveNodes)
					aConstChange |= aLiveNode.determineIfConstant();
			}while(aConstChange>1);

			/*for(LiveVariableNode aLiveNode: liveNodes){
				aLiveNode.printReachingEntries();
				printLiveVarNode(aLiveNode);
			}*/

			 
		}while(aConstChange>0);
		
		do{
			aChange=false;
			for(LiveVariableNode aLiveNode: liveNodes)
				aChange |= aLiveNode.infectionAnalysis();
		}while(aChange);
		return aStartNode;
	}

	public void determineCheckpoints(int[] iStatistics){
		System.out.println("*** Static Analysis for task " + _task.getName());
		for(LiveVariableNode aLiveNode: liveNodes)
			aLiveNode.determineCheckpoints(new CheckpointInfo());
		//System.out.println("Create array size " + (_bytesForVars << 5));
		int[] aStatistics = new int[_bytesForVars << 5];
		int aNbOfCheckPoints=0, aNbOfCandidates=0;
		for(LiveVariableNode aLiveNode: liveNodes){
			int aStatResult = aLiveNode.varStatistics(aStatistics);
			//aLiveNode.printReachingEntries();
			//printLiveVarNode(aLiveNode);
			if((aStatResult & 1)!=0){
				aLiveNode.printReachingEntries();
				printLiveVarNode(aLiveNode);
				aNbOfCandidates++;
				if((aStatResult & 2)!=0) aNbOfCheckPoints++;
			}
		}
		if (aNbOfCandidates==0)
			System.out.println("No checkpoint candidates");
		else{
			int aNbOfLiveElements = _task.getAttributes().size() + _channels.size() + _events.size();
			int nbOfVars=0, nbOfChannels=0, nbOfEvents=0;
			for(int i=0; i<_task.getAttributes().size(); i++)
				nbOfVars += aStatistics[i];
			System.out.println("Variables Checks: " + nbOfVars + "  Candidates: " + (_task.getAttributes().size() * aNbOfCandidates));
			if (!_task.getAttributes().isEmpty()){
				iStatistics[0] += _task.getAttributes().size() * aNbOfCandidates;
				int aVarGain = (100 * (_task.getAttributes().size() * aNbOfCandidates - nbOfVars) / (_task.getAttributes().size() * aNbOfCandidates));
				iStatistics[1] += _task.getAttributes().size() * aNbOfCandidates - nbOfVars;
				System.out.println("Variables Gain: " + aVarGain);
			}
			for(int i=0; i<_channels.size(); i++)
				nbOfChannels += aStatistics[i + _task.getAttributes().size()];
			System.out.println("Channel Checks: " + nbOfChannels + "  Candidates: " + (_channels.size() * aNbOfCandidates));
			if (!_channels.isEmpty()){
				iStatistics[2] += _channels.size() * aNbOfCandidates;
				int aChGain = (100 * (_channels.size() * aNbOfCandidates - nbOfChannels) / (_channels.size() * aNbOfCandidates));
				iStatistics[3] += _channels.size() * aNbOfCandidates - nbOfChannels;
				System.out.println("Channels Gain: " + aChGain);
			}
			for(int i=0; i<_events.size(); i++)
				nbOfEvents += aStatistics[i + _task.getAttributes().size() + _channels.size()];
			System.out.println("Event Checks: " + nbOfEvents + "  Candidates: " + (_events.size() * aNbOfCandidates));
			if (!_events.isEmpty()){
				iStatistics[4] += _events.size() * aNbOfCandidates;
				int aEvtGain = (100 * (_events.size() * aNbOfCandidates - nbOfEvents) / (_events.size() * aNbOfCandidates));
				iStatistics[5] += _events.size() * aNbOfCandidates - nbOfEvents;
				System.out.println("Events Gain: " + aEvtGain);
			}
			System.out.println("Request Checks: " + aStatistics[_task.getAttributes().size() + _channels.size() + _events.size()] + "  Candidates: " + aNbOfCandidates);
			if (_task.isRequested()) System.out.println("Saved Requests: " + (100* (aNbOfCandidates - aStatistics[_task.getAttributes().size() + _channels.size() + _events.size()]) / aNbOfCandidates));
			System.out.println("Checkpoints: " + aNbOfCheckPoints + "  Candidates: " + aNbOfCandidates);
			iStatistics[6] += aNbOfCandidates;
			iStatistics[7] += aNbOfCandidates - aNbOfCheckPoints;
			System.out.println("Checkpoint Gain: " + (100 * (aNbOfCandidates - aNbOfCheckPoints) / aNbOfCandidates));
		}	
		System.out.println("*** End of Static Analysis for task " + _task.getName());
	}

	private LiveVariableNode buildLiveAnalysisTree(TMLActivityElement iCurrElem, LiveVariableNode iReturnNode, LiveVariableNode iSuperiorNode){
		LiveVariableNode aResNode=null;
		
		if (iCurrElem instanceof TMLStartState) {
			iCurrElem.getNextElement(0);
		}

		if (iCurrElem==null || iCurrElem instanceof TMLStopState){
			return iReturnNode;

		} else if (iCurrElem instanceof TMLActionState){
			String[] aTokens=((TMLActionState)iCurrElem).getAction().split("=",2);
			if (aTokens.length<2) aTokens=new String[2];
			aResNode = new LiveVariableNode(this, parseExprToVariableMap(aTokens[1], null), parseExprToVariableMap(aTokens[0], null), iCurrElem, iSuperiorNode, 
			false, aTokens[0], aTokens[1]);	

 		} else if (iCurrElem instanceof TMLRandom){
			TMLRandom aRndCmd=(TMLRandom)iCurrElem;
			int[] aRandomVars = parseExprToVariableMap(aRndCmd.getMinValue(), null);
			parseExprToVariableMap(aRndCmd.getMaxValue(), aRandomVars);
			aResNode = new LiveVariableNode(this, aRandomVars, parseExprToVariableMap(aRndCmd.getVariable(), null), iCurrElem, iSuperiorNode, true);
			aResNode.setVarDepSource(true);

		} else if (iCurrElem instanceof TMLDelay){
			int[] aDelayVars = parseExprToVariableMap(((TMLDelay)iCurrElem).getMinDelay(), null);
			parseExprToVariableMap(((TMLDelay)iCurrElem).getMaxDelay(), aDelayVars);
			aResNode = new LiveVariableNode(this, aDelayVars, new int[_bytesForVars], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLExecI){
			aResNode = new LiveVariableNode(this, parseExprToVariableMap(((TMLExecI)iCurrElem).getAction(), null), new int[_bytesForVars], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLExecC){
			aResNode = new LiveVariableNode(this, parseExprToVariableMap(((TMLExecC)iCurrElem).getAction(), null), new int[_bytesForVars], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLExecIInterval){
			int[] aExecVars = parseExprToVariableMap(((TMLExecIInterval)iCurrElem).getMinDelay(), null);
			parseExprToVariableMap(((TMLExecIInterval)iCurrElem).getMaxDelay(), aExecVars);
			aResNode = new LiveVariableNode(this, aExecVars, new int[_bytesForVars], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLExecCInterval){
			int[] aExecVars = parseExprToVariableMap(((TMLExecCInterval)iCurrElem).getMinDelay(), null);
			parseExprToVariableMap(((TMLExecCInterval)iCurrElem).getMaxDelay(), aExecVars);
			aResNode = new LiveVariableNode(this, aExecVars, new int[_bytesForVars],iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLForLoop){
			TMLForLoop aLoop = (TMLForLoop) iCurrElem;
			String[] aTokens=aLoop.getInit().split("=",2);
			if (aTokens.length<2) aTokens=new String[2];
			LiveVariableNode aInit = new LiveVariableNode(this, parseExprToVariableMap(aTokens[1], null), parseExprToVariableMap(aTokens[0], null), iCurrElem, iSuperiorNode, false, aTokens[0], aTokens[1]);
			aInit.setNodeInfo("init");
			LiveVariableNode aCondition = new LiveVariableNode(this, parseExprToVariableMap(aLoop.getCondition(), null), new int[_bytesForVars], iCurrElem, iSuperiorNode);
			aCondition.setNodeInfo("cond");
			aTokens=aLoop.getIncrement().split("=",2);
			if (aTokens.length<2) aTokens=new String[2];
			LiveVariableNode anIncrement = new LiveVariableNode(this, parseExprToVariableMap(aTokens[1], null), parseExprToVariableMap(aTokens[0],null), iCurrElem, aCondition, false, aTokens[0], aTokens[1]);
			anIncrement.setNodeInfo("inc");
			aInit.setSuccessor(aCondition);
			anIncrement.setSuccessor(aCondition);
			aCondition.setSuccessor(buildLiveAnalysisTree(iCurrElem.getNextElement(0), anIncrement, aCondition));   //in loop
			aCondition.setSuccessor(buildLiveAnalysisTree(iCurrElem.getNextElement(1), iReturnNode, aCondition));   //outside loop
			liveNodes.add(aInit);
			liveNodes.add(aCondition);
			liveNodes.add(anIncrement);
			return aInit;

		} else if (iCurrElem instanceof TMLReadChannel){
			TMLReadChannel aReadCmd = (TMLReadChannel)iCurrElem;
			int[] aReadVars=null;
			if (aReadCmd.getChannel(0).getType()==TMLChannel.BRBW || aReadCmd.getChannel(0).getType()==TMLChannel.BRNBW)
				aReadVars = parseChannelToVariableMap(aReadCmd.getChannel(0), null);
			else
				aReadVars = new int[_bytesForVars];
			parseExprToVariableMap(aReadCmd.getNbOfSamples(), aReadVars);
			aResNode = new LiveVariableNode(this, aReadVars, new int[_bytesForVars], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLWriteChannel){
			TMLWriteChannel aWriteCmd=(TMLWriteChannel)iCurrElem;
			int[] aWriteVars = new int[_bytesForVars];
			for(int i=0; i< aWriteCmd.getNbOfChannels(); i++){
				if (aWriteCmd.getChannel(i).getType()==TMLChannel.BRBW) parseChannelToVariableMap(aWriteCmd.getChannel(i), aWriteVars);
			}
			parseExprToVariableMap(aWriteCmd.getNbOfSamples(), aWriteVars);
			aResNode = new LiveVariableNode(this, aWriteVars, new int[_bytesForVars], iCurrElem, iSuperiorNode);
			
		} else if (iCurrElem instanceof TMLSendEvent){
			TMLSendEvent aSendCmd=(TMLSendEvent)iCurrElem;
			int[] aSendVars=null;
			//if (aSendCmd.getEvent().isBlocking())
				aSendVars = parseEventToVariableMap(aSendCmd.getEvent());
			//else
				//aSendVars = new int[_bytesForVars];
			//parseExprToVariableMap(aSendCmd.getParam(0), aSendVars);
			//parseExprToVariableMap(aSendCmd.getParam(1), aSendVars);
			//parseExprToVariableMap(aSendCmd.getParam(2), aSendVars);
			for (int i=0; i<aSendCmd.getNbOfParams(); i++){	
				parseExprToVariableMap(aSendCmd.getParam(i), aSendVars);
			}
			aResNode = new LiveVariableNode(this, aSendVars, new int[_bytesForVars], iCurrElem, iSuperiorNode);
		
		} else if (iCurrElem instanceof TMLSendRequest){
			TMLSendRequest aSendReqCmd=(TMLSendRequest)iCurrElem;
			int[] aSendReqVars = parseExprToVariableMap(aSendReqCmd.getParam(0), null);
			//parseExprToVariableMap(aSendReqCmd.getParam(1), aSendReqVars);
			//parseExprToVariableMap(aSendReqCmd.getParam(2), aSendReqVars);
			for (int i=1; i<aSendReqCmd.getNbOfParams(); i++){
				parseExprToVariableMap(aSendReqCmd.getParam(i), aSendReqVars);
			}
			aResNode = new LiveVariableNode(this, aSendReqVars, new int[_bytesForVars], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLWaitEvent){
			TMLWaitEvent aWaitCmd=(TMLWaitEvent)iCurrElem;
			int[] aWaitVars = parseExprToVariableMap(aWaitCmd.getParam(0), null);
			//parseExprToVariableMap(aWaitCmd.getParam(1), aWaitVars);
			//parseExprToVariableMap(aWaitCmd.getParam(2), aWaitVars);
			for (int i=1; i<aWaitCmd.getNbOfParams(); i++){
				parseExprToVariableMap(aWaitCmd.getParam(i), aWaitVars);
			}
			aResNode = new LiveVariableNode(this, parseEventToVariableMap(aWaitCmd.getEvent()), aWaitVars, iCurrElem, iSuperiorNode, true);
			if (aWaitCmd.getEvent().getNbOfParams()>0) aResNode.setVarDepSource(true);
			
		} else if (iCurrElem instanceof TMLNotifiedEvent){
			aResNode = new LiveVariableNode(this, parseEventToVariableMap(((TMLNotifiedEvent)iCurrElem).getEvent()), parseExprToVariableMap(((TMLNotifiedEvent)iCurrElem).getVariable(), null), iCurrElem, iSuperiorNode, true);
			aResNode.setVarDepSource(true);

		} else if (iCurrElem instanceof TMLSequence){
			((TMLSequence)iCurrElem).sortNexts();
			for(int i=((TMLSequence)iCurrElem).getNbNext()-1; i>=0; i--){
				iReturnNode = buildLiveAnalysisTree(iCurrElem.getNextElement(i), iReturnNode, iSuperiorNode);
			}
			return iReturnNode;
			 
		} else if (iCurrElem instanceof TMLChoice){
			int[] aChoiceVars = new int[_bytesForVars];
			TMLChoice aChoiceCmd = (TMLChoice)iCurrElem;
			for(int i=0; i<aChoiceCmd.getNbGuard(); i++)
				if (!(aChoiceCmd.isNonDeterministicGuard(i) || aChoiceCmd.isStochasticGuard(i) || aChoiceCmd.getElseGuard()==i)) parseExprToVariableMap(aChoiceCmd.getGuard(i), aChoiceVars);
			aResNode = new LiveVariableNode(this, aChoiceVars, new int[_bytesForVars], iCurrElem, iSuperiorNode);
			if (aChoiceCmd.nbOfNonDeterministicGuard()>0 || aChoiceCmd.nbOfStochasticGuard()>0) aResNode.setInfected(true);
			//System.out.println("checl:  " + aChoiceCmd.nbOfNonDeterministicGuard() + " ** "+ aChoiceCmd.nbOfStochasticGuard());
			for(int i=0; i<aChoiceCmd.getNbNext(); i++)
				aResNode.setSuccessor(buildLiveAnalysisTree(iCurrElem.getNextElement(i), iReturnNode, aResNode));
			liveNodes.add(aResNode);
			return aResNode;
		
		} else if (iCurrElem instanceof TMLSelectEvt){
			aResNode = new LiveVariableNode(this, new int[_bytesForVars], new int[_bytesForVars], iCurrElem, iSuperiorNode);
			if (iCurrElem.getNbNext()>1) aResNode.setInfected(true);
			for(int i=0; i<iCurrElem.getNbNext(); i++){
				aResNode.setSuccessor(buildLiveAnalysisTree(iCurrElem.getNextElement(i), iReturnNode, aResNode));
			}
			liveNodes.add(aResNode);
			return aResNode;
		}

		LiveVariableNode aSucc = buildLiveAnalysisTree(iCurrElem.getNextElement(0), iReturnNode, iSuperiorNode);
		if (aResNode==null || aResNode.isEmptyNode()){
			aResNode=aSucc;
		}else{
			liveNodes.add(aResNode);
			aResNode.setSuccessor(aSucc);
		}
		return aResNode;
	}

	public LiveVariableNode getLiveVarNodeByCommand(TMLActivityElement iCmd){
		for(LiveVariableNode aLiveNode: liveNodes)
			if (aLiveNode.getLinkedElement()==iCmd) return aLiveNode;
		return null;
	}
}