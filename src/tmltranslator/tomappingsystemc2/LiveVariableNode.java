package tmltranslator.tomappingsystemc2;

import java.util.*;
import tmltranslator.*;
import javax.script.*;

public class LiveVariableNode{
	private LinkedList<LiveVariableNode> _succList = new LinkedList<LiveVariableNode>(), _predList = new LinkedList<LiveVariableNode>();
	private boolean _valid=false;
	StaticAnalysis _analysis;
	//** Live Variable Analysis
	private int[] _useVars;
	private int[] _defVars;
	private int[] _inVars;
	private int[] _outVars;
	//** Reachable Definition analysis
	private int[] _killDefs;
	private int[] _generateDefs;
	private int[] _killCandidates;
	private int[] _inDefs;
	private int[] _outDefs;
	//** others
	private int _myDefID = -2;
	private boolean _isConstant=false;
	private boolean _constantStuckToFalse=false;
	private boolean _infected=false;
	//private int _checkpoint=0;
	private CheckpointInfo _checkpoint=null;
	private boolean _guard=false;
	private TMLActivityElement _linkedElem = null;
	private LiveVariableNode _superiorBranchNode = null;
	private int _exprValue = 0;
	private String _lhs = "";
	private String _rhs = "";
	private String _unrolledExpr = "";
	private boolean _varDepSource = false;
	private String _nodeInfo="";
	

	LiveVariableNode(StaticAnalysis iAnalysis, int[] iUseVars, int[] iDefVars, TMLActivityElement iLinkedElem, LiveVariableNode iSuperiorNode, boolean iConstantStuckToFalse, String iLhs, String iRhs){
		this(iAnalysis, iUseVars, iDefVars, iLinkedElem, iSuperiorNode, iConstantStuckToFalse);
		_lhs=iLhs;
		_rhs= iRhs;
		//System.out.println("lhs in init:*" + _lhs + "* rhs in init:*" + _rhs);
	}
	
	LiveVariableNode(StaticAnalysis iAnalysis, int[] iUseVars, int[] iDefVars, TMLActivityElement iLinkedElem, LiveVariableNode iSuperiorNode, boolean iConstantStuckToFalse){
		this(iAnalysis, iUseVars, iDefVars, iLinkedElem, iSuperiorNode);
		_constantStuckToFalse=iConstantStuckToFalse;
		
	}

	LiveVariableNode(StaticAnalysis iAnalysis, int[] iUseVars, int[] iDefVars, TMLActivityElement iLinkedElem, LiveVariableNode iSuperiorNode){
		_useVars=iUseVars;
		_defVars=iDefVars;
		_superiorBranchNode = iSuperiorNode;
		_linkedElem = iLinkedElem;
		_analysis = iAnalysis;
		//_succList = ;
		//_predList = new LinkedList<LiveVariableNode>();
	}
	
	public void liveVariableInit(){
		boolean isADefinition=false;
		_inVars = new int[_defVars.length];
		_outVars = new int[_defVars.length];
		for (int i=0; i<_defVars.length;i++){
			//_defNegVars[i] =  ~_defVars[i];
			//_inVars[i] = _useVars[i] & _defNegVars[i];
			_inVars[i] = _useVars[i] & (~_defVars[i]);
			isADefinition |= (_defVars[i]!=0);
		}
		if (isADefinition)
			_myDefID = _analysis.getNextDefID();
		else
			_myDefID=-1;
	}
	
	public int varStatistics(int[] ioStatistics){
		if(_checkpoint!=null){	
			for (int bytes=0; bytes < _outVars.length; bytes++){
				for (int bits=0; bits<32;bits++){
					//System.out.println("bytes: " + bytes + " stat index: " + (bytes << 5 |bits));
					//if((_outVars[bytes] & (1 << bits))!=0)
					if((_outVars[bytes] & (1 << bits))!=0 || (_useVars[bytes] & (1 << bits))!=0)
					      ioStatistics[bytes << 5 |bits]++;
				}
			}
			return 3;
		}
		return (_linkedElem!=null && (_linkedElem instanceof TMLReadChannel || _linkedElem instanceof TMLWriteChannel|| _linkedElem instanceof TMLSendEvent || _linkedElem instanceof TMLWaitEvent || _linkedElem instanceof TMLSendRequest))? 1:0;
	}
				
	private boolean atLeast1DepDefForVars(int[] iConcernedVars, boolean iVarGoOutOfScope, int iMinNbOfDefs, CheckpointInfo iCheckInfo){
		//following a command where a variable goes out of scope which is reached by at least two definitions, at least one of them being dependent
		//following a command which kills more than one definition of a variable and at least one of these definitions is dependent
		int[] aDefsForVar = new int[_analysis.getBytesForDefs()];
		for (int bytes=0; bytes<iConcernedVars.length; bytes++){
			for (int bits=0; bits<32;bits++){
				if ((iConcernedVars[bytes] & (1<< bits))!=0 && (!iVarGoOutOfScope || (_outVars[bytes] & (1<< bits))==0)){
					//for all variables which are used in this statement and go out of scope
					//reached by dependent definition?
					//find all definitions for var
					for (int i=0; i<_analysis.getBytesForDefs();i++)
						aDefsForVar[i] = _inDefs[i] & _analysis.getDefsForVar()[bytes << 5 |bits][i];
					//if (defsInfected(aDefsForVar)<-1) return true;
					//if (defsInfected(aDefsForVar)<0) return true; //NEW!!!!!!!!!
					if (defsInfected(aDefsForVar) < 1 - iMinNbOfDefs){
						  iCheckInfo.setDefinitions(aDefsForVar);
						  iCheckInfo.setVariableID(bytes << 5 |bits, iConcernedVars.length);
						  return true;
					}
				}
			}
		}
		return false;
	}
	
	private int isCheckpointCandidate(CheckpointInfo iCheckInfo){
		//being dependent or having at least one dependent inverse operator in the task the communication is established with
		int aReasonCode=0;
		//if (_linkedElem!=null) System.out.println("--- " + _linkedElem.getID() + " start isCheckpointCandidate"); else
		//System.out.println("--- start isCheckpointCandidate");
		if(_linkedElem instanceof TMLReadChannel || _linkedElem instanceof TMLWriteChannel){
			for (int i=0; i<((TMLActivityElementChannel)_linkedElem).getNbOfChannels(); i++){
				TMLChannel aChannel = ((TMLActivityElementChannel)_linkedElem).getChannel(i);
				//System.out.println("get Infected for channel " + aChannel.getName() + "=" + aChannel.getInfected());
				//if (aChannel.getInfected()) aReasonCode = CheckpointInfo.CHANNEL_DEP;
				if (_analysis.isChannelDep(aChannel.getID())) aReasonCode = CheckpointInfo.CHANNEL_DEP;
			}
		}else if(_linkedElem instanceof TMLSendEvent || _linkedElem instanceof TMLWaitEvent){
			//if (((TMLActivityElementEvent)_linkedElem).getEvent().getInfected()) aReasonCode = CheckpointInfo.CHANNEL_DEP;
			if (_analysis.isChannelDep(((TMLActivityElementEvent)_linkedElem).getEvent().getID())) aReasonCode = CheckpointInfo.CHANNEL_DEP;
		}else if (_linkedElem instanceof TMLSendRequest){
			//if (((TMLSendRequest)_linkedElem).getRequest().getInfected()) aReasonCode = CheckpointInfo.CHANNEL_DEP;
			if (_analysis.isChannelDep(((TMLSendRequest)_linkedElem).getRequest().getID())) aReasonCode = CheckpointInfo.CHANNEL_DEP;
		}
		//consider receive event
		if (_predList.size()>1){
			//System.out.println("more than one pred found");
			aReasonCode += CheckpointInfo.CTRL_FLOW_JOIN;
		}
		iCheckInfo.setOperationMode(CheckpointInfo.KILLED_DEF);
		if (atLeast1DepDefForVars(_defVars, false, 1, iCheckInfo)){
			//System.out.println(getStatementDescr() + "  kills dependent definitions!!!");
			aReasonCode += CheckpointInfo.KILLED_DEF;
		}
		iCheckInfo.setOperationMode(CheckpointInfo.VAR_OUT_OF_SCOPE);
		if (atLeast1DepDefForVars(_useVars, true, 1, iCheckInfo)){
			//System.out.println(getStatementDescr()+ " variable went out of scope!!! ");
			aReasonCode += CheckpointInfo.VAR_OUT_OF_SCOPE;
		}
		if(_varDepSource){
			if (canBeRemoved())
				_varDepSource=false;
			else{
				//_analysis.setLastVarDepSource(_generateDefs);
				iCheckInfo._depSrcDef = _generateDefs;
				//System.out.println("Stored defs: "); printDefList(_generateDefs);
				aReasonCode += CheckpointInfo.DEP_SRC;
			}
		}
		//System.out.println("--- end isCheckpointCandidate: " +  aReasonCode);
		return aReasonCode;

	}

	public static int evaluate(String string) throws IllegalArgumentException{
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		try{
			//Object object = engine.eval("eval("+string+")");
			engine.eval(string);
			Object object = engine.get("xx_xx");
			if ((object != null) && (object instanceof Number)){
                		return ((Number)(object)).intValue();
			}else{
				throw new IllegalArgumentException("Invalid input: '"+string+"'");
			}
		}catch (ScriptException e){
			throw new IllegalArgumentException("Invalid input: '"+string+"'", e);
		}
	}
	
	public void prepareReachingDefinitions(){
		invalidate();
		_generateDefs = new int[_analysis.getBytesForDefs()];
		_killDefs= new int[_analysis.getBytesForDefs()];
		_killCandidates = new int[_analysis.getBytesForDefs()];
		_inDefs = new int[_analysis.getBytesForDefs()];
		_outDefs = new int[_analysis.getBytesForDefs()];
		//System.out.println("<> varToStatements asso: " + getStatementDescr());
		if (_myDefID!=-1){
		//if (_myDefID!=-1 && !canBeRemoved()){  //NEW!!!!!!!!!!!
			for (int bytes=0; bytes<_defVars.length;bytes++){
				for (int bits=0; bits<32;bits++){
					//if ((_defVars[bytes] & (1 << bits))!=0){
					//only consider live variables= defined + significant
					if ((_defVars[bytes] & (1 << bits))!=0){ //NEW!!!!!!!!!!
						  if((_outVars[bytes] & (1 << bits))==0){
							_defVars[bytes] ^= (1 << bits);
							//System.out.println("del");
						  }else
							_analysis.getDefsForVar()[(bytes << 5)|bits][_myDefID >>> 5] |= 1 << (_myDefID & 0x1F);
						//System.out.println("var found:" + ((bytes << 5)|bits));
					}
				}
			}
			if (!canBeRemoved()){
				_generateDefs[_myDefID >>> 5] =  1 << (_myDefID & 0x1F);
				_analysis.getDefLookUp()[_myDefID]=this;
			}
		}
		//System.out.println("<> END varToStatements asso: " + getStatementDescr());
	}

	public void printKillEntries(){
		//System.out.println("++++++++++ Kill definitions list ++++++++++");
		System.out.print(getStatementDescr() + "  kills definitions: " + printDefList(_killDefs));
		System.out.println();
	}

	public void printReachingEntries(){
		//System.out.println("++++++++++ Reaching definitions list ++++++++++");
		//if(_linkedElem!=null && (_linkedElem instanceof TMLReadChannel || _linkedElem instanceof TMLWriteChannel|| _linkedElem instanceof TMLSendEvent || _linkedElem instanceof TMLWaitEvent || _linkedElem instanceof TMLSendRequest)){
		System.out.print(getStatementDescr());
		/*printDefList(_inVars);
		System.out.print("  outvars: ");
		printDefList(_outVars);
		System.out.print("  defVars: ");
		printDefList(_defVars);
		System.out.print("  useVars: ");
		printDefList(_useVars);*/
		//+ "  reached by definitions: ");
		//printDefList(_inDefs);
		System.out.println();
		//}
	}

	private String printDefList(int[] iList){
		String aResult="";
		for (int bytes=0; bytes<iList.length; bytes++){
			for (int bits=0; bits<32;bits++){
				if ((iList[bytes]& (1 << bits))!=0) aResult += ((bytes << 5)|bits) + ", "; 
			}
		}
		return aResult;
	}
	
	private String printDefCmdIDs(int[] iList){
		String aResult="";
		if (iList!=null){
			for (int bytes=0; bytes<iList.length; bytes++){
				for (int bits=0; bits<32;bits++){
					if ((iList[bytes] & (1 << bits))!=0){
						TMLActivityElement anElem = _analysis.getDefLookUp()[(bytes << 5)|bits]._linkedElem;
						aResult += _analysis.getDefLookUp()[(bytes << 5)|bits].getNodeInfo();
						if (anElem!=null) aResult += "(" + anElem.getID() + "),"; else aResult += ",";
					}
				}
			}
		}
		return aResult;
	}

	private boolean allDefsConstantAndEqual(int[] iDefinitions){
		boolean aIsConst=true, aFirstTime=true, aDefFound=false;
		int aLastExprVal=0; 
		//System.out.println("******* allDefsConstantAndEqual");
		for (int bytes=0; bytes<iDefinitions.length && aIsConst; bytes++){
			for (int bits=0; bits<32 && aIsConst; bits++){
				int anIndex = (bytes << 5)|bits;
				if ((iDefinitions[bytes] & (1<< bits))!=0){ //what if there are no definitions????
					aDefFound=true;
					if (_analysis.getDefLookUp()[anIndex].isConstant()){
						//System.out.println(_analysis.getDefLookUp()[anIndex].getStatementDescr() + " said to be constant");
						if (aFirstTime || _analysis.getDefLookUp()[anIndex].getExpressionValue() == aLastExprVal){
							aLastExprVal = _analysis.getDefLookUp()[anIndex].getExpressionValue();
							_unrolledExpr+= _analysis.getDefLookUp()[anIndex].getExpressionString() + ";";
							aFirstTime=false;
						}else
							aIsConst=false;
					}else{
						//System.out.println(_analysis.getDefLookUp()[anIndex].getStatementDescr() + " said to be NOT constant");
						aIsConst=false;
					}
				}//else
					//if (_analysis.getDefLookUp()[anIndex]!=null) System.out.println(_analysis.getDefLookUp()[anIndex].getStatementDescr() + " not concerned"); 
			}
		}
		//if (aIsConst)
		//System.out.println(aIsConst + " lhs:" + _lhs + " rhs:" + _rhs);
		return (aIsConst && aDefFound);
	}

	public int determineIfConstant(){
		//not constant: random, notified event, receive event, select event
		//if (_linkedElem!=null && (_linkedElem instanceof TMLRandom || _linkedElem instanceof TMLWaitEvent || _linkedElem instanceof TMLNotifiedEvent || _linkedElem instanceof TMLSelectEvt)) return false;
		if (_isConstant) return 0;
		_valid=false;
		//boolean aIsConst=true;
		int aResult=0;
		if (!_constantStuckToFalse){
			int[] aReachingDefForVar= new int[_analysis.getBytesForDefs()];
			aResult=2;
		//System.out.println("!!!!Checking if  " + getStatementDescr() + " is constant");
			for (int bytes=0; bytes<_useVars.length; bytes++){
				for (int bits=0; bits<32;bits++){
					if ((_useVars[bytes] & (1<< bits))!=0){
						//System.out.println("var " + ((bytes << 5)|bits) + " is used  usevars:" + _useVars[bytes]);
						//System.out.println("Reached by definitions: ");
						//printDefList(_inDefs);
						for (int i=0; i<_analysis.getBytesForDefs();i++){
							aReachingDefForVar[i] = _analysis.getDefsForVar()[(bytes << 5)|bits][i] & _inDefs[i];
						}
						//aIsConst &= allDefsConstant(aReachingDefForVar);
						if(allDefsConstantAndEqual(aReachingDefForVar)){
							//toggle bit no [bits] = set it to zero as it is one
							_useVars[bytes]^= (1<< bits);
							//System.out.println("*** " + getStatementDescr() + "toggle bit " + ((bytes << 5)|bits) + "***");
							//_inVars[bytes] = _useVars[bytes] & _defNegVars[bytes];
							//_inVars[bytes] = _useVars[bytes] & (~_defVars[bytes]);  NEW!!!!!!!
							aResult |= 1;   //set bit 2^0
						}else{
							//System.out.println("not all defs constant for variable " + ((bytes << 5)|bits));
							//aIsConst=false;
							aResult &= 1;  //del bit 2^1
						}
					}
				}
			}
			_isConstant = ((aResult & 2)!=0);
			if (_isConstant && !(_lhs==null ||_lhs.isEmpty())){
				_unrolledExpr+= "xx_xx=" + _rhs + ";";
				//try{
					_exprValue = evaluate(_unrolledExpr);
					//System.out.println("Expr: *" + _unrolledExpr + "* evaluated to: " + _exprValue);
				//}catch(IllegalArgumentException e){
					//System.out.println("At lest one variable of the expression remains undefined: " + _rhs);
				//}
			}
		}
		//boolean aChangeInResult = (_constantStuckToFalse)? false : aIsConst!= _isConstant;
		//boolean aChangeInResult = (_constantStuckToFalse)? false : aIsConst;
		//_isConstant = aIsConst;
		//return aChangeInResult;
		return aResult;
		//}
	}

	public boolean isConstant(){
		return (_constantStuckToFalse)? false: _isConstant;
	}
	
	public boolean isInfected(){
		return _infected;
	}
	
	public String getExpressionString(){
		return _lhs + "=" + _exprValue;
	}
	
	public int getExpressionValue(){
		return _exprValue;
	}

	public int[] getKillCandidates(){
		return _killCandidates;
	}
	
	public void setInfected(boolean iInfected){
		_infected = iInfected;
	}

	public void setVarDepSource(boolean iVarDepSource){
		_infected = iVarDepSource;
		_varDepSource = iVarDepSource;
	}
	
	public String getNodeInfo(){
		return _nodeInfo;
	}
	
	public void setNodeInfo(String iInfo){
		_nodeInfo=iInfo;
	}

	public boolean determineKilledSets(){
		boolean aChange=false;
		int [] aKillOut = new int[_analysis.getBytesForDefs()];
		int [] aKillIn = new int[_analysis.getBytesForDefs()];
		if (!_valid){
			_valid=true;
			//System.out.println("determineKilledSets for " + getStatementDescr() ); 
			for(LiveVariableNode aPred: _predList) {
				int[] aKillCandidatesPred = aPred.getKillCandidates();
				for (int i=0; i<_analysis.getBytesForDefs();i++){
					aKillOut[i] |= aKillCandidatesPred[i];
					aKillIn[i] |= aKillCandidatesPred[i];
				}
			}
			//System.out.println( "aLocalKillCandidates[0] " + aLocalKillCandidates[0]); 
			//System.out.println( "killDefs[0] before" + _killDefs[0]); 
			if (_myDefID!=-1){
				//System.out.println("Statement " + _myDefID + " (" + getStatementDescr() + ") defines: " + _generateDefs[0] + " could kill input: " + aLocalKillCandidates[0]);
				for (int bytes=0; bytes<_defVars.length; bytes++){
					for (int bits=0; bits<32;bits++){
						if ((_defVars[bytes] & (1<< bits))!=0){   //for all variables which are defined in this statement
							for (int i=0; i<_analysis.getBytesForDefs();i++){
								//killDefs = definitions representing kill candidates in program flow & all statements affecting the current variable targeted in this definition
								//all kill candidates -> for a specific variable -> variable determined by defVars  
								//_killDefs[i] |= (aLocalKillCandidates[i] & _varToStatements[(bytes << 5)|bits][i]);
								_killDefs[i] |= (aKillIn[i] & _analysis.getDefsForVar()[(bytes << 5)|bits][i]);
								//System.out.println( "killDefs[i] " + _killDefs[i] + " i" + i); 
								//int oldCandidates = aLocalKillCandidates[i];
								//delete all kill candidates for the variable yielded by that definition
								//aLocalKillCandidates[i] &= ~ _varToStatements[(bytes << 5)|bits][i];
								aKillOut[i] &= ~ _analysis.getDefsForVar()[(bytes << 5)|bits][i];
								//add this definition as killer for the variable
								//aLocalKillCandidates[i] |= _generateDefs[i];
								aKillOut[i] |= _generateDefs[i];
								//aChange |= (oldCandidates!=aLocalKillCandidates[i]);
							}
						}
					}
				}
			}
			//System.out.println( "killDefs[0] " + _killDefs[0]); 
			for (int i=0; i<_analysis.getBytesForDefs();i++){
				//aChange |= (_killCandidates[i]!=aLocalKillCandidates[i]);
				aChange |= (_killCandidates[i]!=aKillOut[i]);
				//_killCandidates[i]=aLocalKillCandidates[i];
				_killCandidates[i]=aKillOut[i];
			}
			if (aChange){
				for(LiveVariableNode aSucc: _succList) aSucc.invalidate();
			}
		}
		return aChange;
	}
	
	public String getStatementDescr(){
		//String aName = "| Live ID: " + _myDefID + " | ";
		String aName = "| ";
		if (_linkedElem==null)
				aName += "cmd type: unknown | cmd ID: unknown";
		else
			aName += "cmd type: " + _linkedElem.getName() + " | cmd ID: " + _linkedElem.getID();
		aName+= " | infected: " + _infected;
		if (!(_lhs==null || _rhs==null || _lhs.isEmpty() || _rhs.isEmpty()))
			aName += " | action: " + _lhs + "=" + _rhs;
		aName += " | const: ";
		if (_isConstant){
			if (_lhs==null || _lhs.isEmpty())
				aName+= "true";
			else
				aName+= _exprValue;
		}else 
			aName += "false"; 
		aName += " | to be removed: " + canBeRemoved() + " | checkp:";
		if (_checkpoint==null)
			aName += "0";
		else{
			if ((_checkpoint._reasonCode & CheckpointInfo.CHANNEL_DEP)!=0) aName += " CH_DEP";
			if ((_checkpoint._reasonCode & CheckpointInfo.CTRL_FLOW_JOIN)!=0) aName += " CTRL(" + printDefCmdIDs(_checkpoint._ctrlFlowJoinDefs) + ")";
			if ((_checkpoint._reasonCode & CheckpointInfo.KILLED_DEF)!=0) aName += " KILL(" + printDefCmdIDs(_checkpoint._killedDefs) + ")";
			if ((_checkpoint._reasonCode & CheckpointInfo.VAR_OUT_OF_SCOPE)!=0) aName += " VAR_SCP(" + _analysis.printVariables(_checkpoint._varsOutOfScope) + ")";
			if ((_checkpoint._reasonCode & CheckpointInfo.DEP_SRC)!=0) aName += " SRC(" + printDefCmdIDs(_checkpoint._depSrcDef) + ")";
		}
		aName += " | ";
		return aName;
	}

	public void invalidate(){
		_valid=false;
	}
	
	private boolean chOfCmdAllNBRNBW(TMLActivityElementChannel iElem){
		for (int i=0; i<iElem.getNbOfChannels(); i++)
			if (iElem.getChannel(i).getType()!=TMLChannel.NBRNBW) return false;
		return true;
	}
	
	//public void determineCheckpoints(int iPropagate, CheckpointInfo iCheckInfo){
	public void determineCheckpoints(CheckpointInfo iCheckInfo){
		if (!_guard){
			//if (_checkpoint==0){
			if (_checkpoint==null){
				//int aCandidate = iCheckInfo.__reasonCode | isCheckpointCandidate(iCheckInfo);
				iCheckInfo._reasonCode |= isCheckpointCandidate(iCheckInfo);
				//if (_linkedElem!=null) System.out.println(_linkedElem.getID() + "  aCandidate: " + aCandidate);
				iCheckInfo.setOperationMode(CheckpointInfo.CTRL_FLOW_JOIN);
				//if ((aCandidate & CheckpointInfo.CTRL_FLOW_JOIN)!=0 && !atLeast1DepDefForVars(_inVars, false, 2, iCheckInfo)){
				if ((iCheckInfo._reasonCode & CheckpointInfo.CTRL_FLOW_JOIN)!=0 && !atLeast1DepDefForVars(_inVars, false, 2, iCheckInfo)){
					//aCandidate ^= CheckpointInfo.CTRL_FLOW_JOIN;
					iCheckInfo._reasonCode ^= CheckpointInfo.CTRL_FLOW_JOIN;
					//System.out.println("aCandidate reset control flow join");
				}
				//if ((aCandidate & CheckpointInfo.DEP_SRC)!=0){
				if ((iCheckInfo._reasonCode & CheckpointInfo.DEP_SRC)!=0){
					boolean aDefReachable=false;
					for (int i=0; i<_analysis.getBytesForDefs() && !aDefReachable; i++)
						//aDefReachable |= ((_analysis.getLastVarDepSource()[i] & _outDefs[i]) !=0);
						aDefReachable |= ((iCheckInfo._depSrcDef[i] & _outDefs[i]) !=0);
					if (!aDefReachable){
						//aCandidate ^= CheckpointInfo.DEP_SRC;
						iCheckInfo._reasonCode ^= CheckpointInfo.DEP_SRC;
						/*System.out.println("aCandidate reset dep src");
						System.out.println("comp1 in defs: "); printDefList(_outDefs);
						System.out.println("comp2 stored defs: "); printDefList(_analysis.getLastVarDepSource());*/
					}
				}
				//if(aCandidate>0){
				if(iCheckInfo._reasonCode > 0){
					if(((_linkedElem instanceof TMLReadChannel || _linkedElem instanceof TMLWriteChannel) && !chOfCmdAllNBRNBW((TMLActivityElementChannel)_linkedElem)) || _linkedElem instanceof TMLSendEvent || _linkedElem instanceof TMLWaitEvent || _linkedElem instanceof TMLSendRequest){
						//_checkpoint=aCandidate;
						_checkpoint = iCheckInfo;
					}else{
						_guard=true;
						for(LiveVariableNode aSucc: _succList)
							aSucc.determineCheckpoints(iCheckInfo);
						_guard = false;
					}
				}
			}else
				//_checkpoint |= iPropagate;
				_checkpoint.incorporate(iCheckInfo);
		}
	}

	public void reachingDefinitionsInit(){
		invalidate();
		for (int i=0; i<_analysis.getBytesForDefs();i++){
			_outDefs[i] = _generateDefs[i] & (~_killDefs[i]);
		}
		//System.out.println("out Defs " + getStatementDescr() + " " + _outDefs[0] + " generate Defs: " + _generateDefs[0] + " kill Defs: " + _killDefs[0]);
	}

	public boolean reachingDefinitionAnalysis(){
		boolean aSuccInvalidated=false;
		if (!_valid){
			_valid=true;
			for(LiveVariableNode aPred: _predList) {
				int[] aPredOutDefs = aPred.getOutDefs();
				for (int i=0; i<_analysis.getBytesForDefs();i++){
					_inDefs[i] |= aPredOutDefs[i];
					int aOutDefsOld=_outDefs[i];
					_outDefs[i] = _generateDefs[i] | _inDefs[i] & (~_killDefs[i]);
					if (aOutDefsOld!=_outDefs[i]) aSuccInvalidated=true; 
				} 
			}
			if(aSuccInvalidated){
				for(LiveVariableNode aSucc: _succList)
					aSucc.invalidate();
			}
		}
		return aSuccInvalidated;
	}

	public boolean liveVariableAnalysis(){
		boolean aPredInvalidated=false;
		if (!_valid){
			_valid=true;
			/*if (_linkedElem==null)
				System.out.print("recalculate waitRequest invalidate: ");
			else 
				System.out.print("recalculate " + _linkedElem.getName() + " " + _linkedElem.getID() + "invalidate: ");*/
			for(LiveVariableNode aSucc: _succList) {
				int[] aSuccInVars = aSucc.getInVars();
				for (int i=0; i<aSuccInVars.length;i++){
					_outVars[i] |= aSuccInVars[i];
					int aInVarsOld=_inVars[i];
					//_inVars[i] = _useVars[i] | _outVars[i] & _defNegVars[i];
					_inVars[i] = _useVars[i] | _outVars[i] & (~_defVars[i]);
					if (aInVarsOld!=_inVars[i]) aPredInvalidated=true; 
				} 
			}
			if(aPredInvalidated){
				for(LiveVariableNode aPred: _predList){
					aPred.invalidate();
					/*if (aPred.getLinkedElement()==null)
						System.out.print("waitRequest, ");
					else
						System.out.print(aPred.getLinkedElement().getID() + ", ");*/
				}
			}
			//System.out.println();
		}
		return aPredInvalidated;
	}
	
	public boolean infectionAnalysis(){
		//if (_linkedElem!=null) System.out.println("Infection Analysis: "+  _linkedElem.getID());
		if (_infected){
			//System.out.println("cancelled");
			return false;
		}else{
			if (_superiorBranchNode!=null && _superiorBranchNode.isInfected()){
				_infected=true;
			}else{
				int[] aRelevantDefs = new int[_analysis.getBytesForDefs()];
				for (int bytes=0; bytes<_useVars.length; bytes++){
					for (int bits=0; bits<32;bits++){
						if ((_useVars[bytes] & (1<< bits))!=0){   //for all variables which are used in this statement
							for (int i=0; i<_analysis.getBytesForDefs();i++){
								 aRelevantDefs[i] |=_inDefs[i] & _analysis.getDefsForVar()[(bytes << 5)|bits][i];
							}						
						}
					}
				}
				_infected |= (defsInfected(aRelevantDefs)<0);
			}
		}
		
		if (_infected && _linkedElem!=null){
			//System.out.println("in if 1");
			if(_linkedElem instanceof TMLReadChannel || _linkedElem instanceof TMLWriteChannel){
				//System.out.println("in if 2");
				for (int i=0; i<((TMLActivityElementChannel)_linkedElem).getNbOfChannels(); i++){
					//System.out.println("in loop");
					TMLChannel aChannel = ((TMLActivityElementChannel)_linkedElem).getChannel(i);
					//System.out.println("Set Infected for channel " + aChannel.getName());
					//if (aChannel.getType()!=TMLChannel.NBRNBW) aChannel.setInfected(true);
					if (aChannel.getType()!=TMLChannel.NBRNBW) _analysis.addDepChannel(aChannel.getID());
				}
			}else if(_linkedElem instanceof TMLSendEvent || _linkedElem instanceof TMLWaitEvent){
				//((TMLActivityElementEvent)_linkedElem).getEvent().setInfected(true);
				_analysis.addDepChannel(((TMLActivityElementEvent)_linkedElem).getEvent().getID());
			}else if (_linkedElem instanceof TMLSendRequest){
				//((TMLSendRequest)_linkedElem).getRequest().setInfected(true);
				_analysis.addDepChannel(((TMLSendRequest)_linkedElem).getRequest().getID());
			}
		}
		return _infected;
	}
	
	private int defsInfected(int[] iDefs){
		boolean aInfected=false;
		int aNbOfDefs=0;
		for (int bytes=0; bytes<iDefs.length; bytes++){
			for (int bits=0; bits<32;bits++){
				if ((iDefs[bytes] & (1<< bits))!=0){ 
					aNbOfDefs++;
					aInfected |= _analysis.getDefLookUp()[(bytes << 5)|bits].isInfected();
				}
			}
		}
		return (aInfected)? -aNbOfDefs : aNbOfDefs;
	}
	

	public TMLActivityElement getLinkedElement(){
		return _linkedElem;
	}

	public boolean isEmptyNode(){
		return false;
		/*if (_succList.size()>1) return false;
		for (int i=0; i<_defVars.length;i++)
			if (_defVars[i]!=0) return false;
		for (int i=0; i<_useVars.length;i++)
			if (_useVars[i]!=0) return false;
		return true;*/
	}

	public void setSuccessor(LiveVariableNode iSucc){
		if(iSucc!=null){
			_succList.add(iSucc);
			iSucc.setPredecessor(this);
		}
	}

	private void setPredecessor(LiveVariableNode iPred){
		_predList.add(iPred);
		//if (_linkedElem!=null) System.out.println(_linkedElem.getID() + " adds pred " + iPred._myDefID);
	}

	public int[] getInVars(){
		return _inVars;
	}

	public int[] getOutVars(){
		return _outVars;
	}
	
	public int[] getInDefs(){
		return _inDefs;
	}

	public int[] getOutDefs(){
		return _outDefs;
	}

	private boolean canBeRemoved(){
		//if ((_lhs==null || _lhs.isEmpty()) && (_rhs==null || _rhs.isEmpty()) && !(_linkedElem==null || _linkedElem instanceof TMLNotifiedEvent || _linkedElem instanceof TMLRandom)) return false;
		for (int i=0; i<_defVars.length;i++){
			if ((_defVars[i] & _outVars[i]) !=0) return false;
		}
		return true;
	}

	public String getLiveVariableString(){
		/*System.out.print("Numbers: ");
		for (int aPos=0; aPos<_outVars.length; aPos++){
			System.out.print(Integer.toHexString(_outVars[aPos]) + ", ");
		}
		System.out.println();*/
		return intArrayToHexString(_outVars);
	}
	
	public String getStartLiveVariableString(){
		return intArrayToHexString(_inVars);
	}

	public String getStateModificationString(){
		return intArrayToHexString(_defVars);
	}

	private String intArrayToHexString(int[] iArray){
		String aResult = "\"";
		//\\x" + Integer.toHexString(iArray.length); 	
		for (int aPos=0; aPos<iArray.length; aPos++){
			int anItem = iArray[aPos];
			for (int bytes=0; bytes<4; bytes++){
			//for (int bits=0; bits<32; bits+=8)
				aResult+= "\\x" + Integer.toHexString(anItem & 0xFF);
				anItem >>>= 8;
			}
		}
		aResult+="\"";
		//System.out.println("String: " + aResult);
		return aResult;
	}
	
	public boolean isCheckpoint(){
		return (_checkpoint!=null);
	}
}
