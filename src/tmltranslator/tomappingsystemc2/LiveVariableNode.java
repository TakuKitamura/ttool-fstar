package tmltranslator.tomappingsystemc2;

import java.util.*;
import tmltranslator.*;
import javax.script.*;

public class LiveVariableNode{
	private LinkedList<LiveVariableNode> _succList, _predList;
	private boolean _valid=false;
	private static int _nextDefID=0;
	private static int[][] _varToStatements=null;
	private static int _bytesForStatements;
	private static LiveVariableNode[] _defLookup;
	private int[] _useVars;
	private int[] _defVars;
	private int[] _defNegVars;
	private int[] _inVars;
	private int[] _outVars;
	private int[] _killDefs;
	private int[] _generateDefs;
	private int[] _killCandidates;
	private int[] _inDefs;
	private int[] _outDefs;
	private int _myDefID;
	private boolean _isConstant=false;
	private boolean _constantStuckToFalse=false;
	private boolean _infected=false;
	private TMLActivityElement _linkedElem;
	private LiveVariableNode _superiorBranchNode=null;
	private int _exprValue=0;
	private String _lhs="";
	private String _rhs="";
	private String _unrolledExpr="";
	

	LiveVariableNode(int[] iUseVars, int[] iDefVars, TMLActivityElement iLinkedElem, LiveVariableNode iSuperiorNode, boolean iConstantStuckToFalse, String iLhs, String iRhs){
		this(iUseVars, iDefVars, iLinkedElem, iSuperiorNode, iConstantStuckToFalse);
		_lhs=iLhs;
		_rhs= iRhs;
		//System.out.println("lhs in init:*" + _lhs + "* rhs in init:*" + _rhs);
	}
	
	LiveVariableNode(int[] iUseVars, int[] iDefVars, TMLActivityElement iLinkedElem, LiveVariableNode iSuperiorNode, boolean iConstantStuckToFalse){
		this(iUseVars, iDefVars, iLinkedElem, iSuperiorNode);
		_constantStuckToFalse=iConstantStuckToFalse;
		
	}

	LiveVariableNode(int[] iUseVars, int[] iDefVars, TMLActivityElement iLinkedElem, LiveVariableNode iSuperiorNode){
		boolean isADefinition=false;
		_useVars=iUseVars;
		_defVars=iDefVars;
		_superiorBranchNode = iSuperiorNode;
		_defNegVars = new int[_defVars.length];
		_inVars = new int[_defVars.length];
		_outVars = new int[_defVars.length];
		if (_varToStatements==null) _varToStatements = new int [_defVars.length << 5][];
		_linkedElem = iLinkedElem;
		for (int i=0; i<_defVars.length;i++){
			_defNegVars[i] =  ~_defVars[i];
			_inVars[i] = _useVars[i] & _defNegVars[i];
			isADefinition |= (_defVars[i]!=0);
		}
		if (isADefinition)
			_myDefID=_nextDefID++;
		else
			_myDefID=-1;
		 _succList = new LinkedList<LiveVariableNode>();
		 _predList = new LinkedList<LiveVariableNode>();
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

	public static void reset(){
		_varToStatements=null;
		_nextDefID=0;
	}
	
	public void prepareReachingDefinitions(){
		invalidate();
		if(_varToStatements[0]==null){
			//System.out.println("Initialization done");
			_bytesForStatements = _nextDefID >>> 5;
			if ((_nextDefID & 0x1F)!=0 || _nextDefID==0) _bytesForStatements++;
			//System.out.println("Initialization Set _bytesForStatements = " + _bytesForStatements + "  _nextDefID:" + _nextDefID);
			for (int i=0; i<(_defVars.length << 5);i++){
				_varToStatements[i]=new int[_bytesForStatements];
			}
			_defLookup = new LiveVariableNode[_bytesForStatements << 5];
		}//else
			//System.out.println("Bytes for statements without init: " + _bytesForStatements);
		_generateDefs = new int[_bytesForStatements];
		_killDefs= new int[_bytesForStatements];
		_killCandidates = new int[_bytesForStatements];
		_inDefs = new int[_bytesForStatements];
		_outDefs = new int[_bytesForStatements];
		//System.out.println("<> varToStatements asso: " + getStatementDescr());
		if (_myDefID!=-1){
			for (int bytes=0; bytes<_defVars.length;bytes++){
				for (int bits=0; bits<32;bits++){
					if ((_defVars[bytes] & (1 << bits))!=0){
						 _varToStatements[(bytes << 5)|bits][_myDefID >>> 5] |= 1 << (_myDefID & 0x1F);
						//System.out.println("var found:" + ((bytes << 5)|bits));
					}
				}
			}
		 	_generateDefs[_myDefID >>> 5] =  1 << (_myDefID & 0x1F);
			_defLookup[_myDefID]=this;
		}
		//System.out.println("<> END varToStatements asso: " + getStatementDescr());
	}

	public void printKillEntries(){
		//System.out.println("++++++++++ Kill definitions list ++++++++++");
		System.out.print(getStatementDescr() + "  kills definitions: ");
		printDefList(_killDefs);
		System.out.println();
	}

	public void printReachingEntries(){
		//System.out.println("++++++++++ Reaching definitions list ++++++++++");
		System.out.print(getStatementDescr() + "  reached by definitions: ");
		printDefList(_inDefs);
		System.out.println();
	}

	private void printDefList(int[] iList){
		for (int bytes=0; bytes<iList.length; bytes++){
			for (int bits=0; bits<32;bits++){
				if ((iList[bytes]& (1 << bits))!=0) System.out.print(((bytes << 5)|bits) + ", "); 
			}
		}
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
					//aIsConst &= _defLookup[(bytes << 5)|bits].isConstant();	
					if (_defLookup[anIndex].isConstant()){
						//System.out.println(_defLookup[anIndex].getStatementDescr() + " said to be constant");
						if (aFirstTime || _defLookup[anIndex].getExpressionValue() == aLastExprVal){
							aLastExprVal = _defLookup[anIndex].getExpressionValue();
							_unrolledExpr+= _defLookup[anIndex].getExpressionString() + ";";
							aFirstTime=false;
						}else
							aIsConst=false;
					}else{
						//System.out.println(_defLookup[anIndex].getStatementDescr() + " said to be NOT constant");
						aIsConst=false;
					}
				}//else
					//if (_defLookup[anIndex]!=null) System.out.println(_defLookup[anIndex].getStatementDescr() + " not concerned"); 
			}
		}
		//if (aIsConst)
		//System.out.println(aIsConst + " lhs:" + _lhs + " rhs:" + _rhs);
		return (aIsConst && aDefFound);
	}

	public boolean determineIfConstant(){
		//not constant: random, notified event, receive event, select event
		//if (_linkedElem!=null && (_linkedElem instanceof TMLRandom || _linkedElem instanceof TMLWaitEvent || _linkedElem instanceof TMLNotifiedEvent || _linkedElem instanceof TMLSelectEvt)) return false;
		if (_isConstant) return false;
		_valid=false;
		int[] aReachingDefForVar= new int[_bytesForStatements];
		boolean aIsConst=true;
		//System.out.println("!!!!Checking if  " + getStatementDescr() + " is constant");
		for (int bytes=0; bytes<_useVars.length; bytes++){
			for (int bits=0; bits<32;bits++){
				if ((_useVars[bytes] & (1<< bits))!=0){
					//System.out.println("var " + ((bytes << 5)|bits) + " is used  usevars:" + _useVars[bytes]);
					//System.out.println("Reached by definitions: ");
					printDefList(_inDefs);
					for (int i=0; i<_bytesForStatements;i++){
						aReachingDefForVar[i] = _varToStatements[(bytes << 5)|bits][i] & _inDefs[i];
					}
					//aIsConst &= allDefsConstant(aReachingDefForVar);
					if(allDefsConstantAndEqual(aReachingDefForVar)){
						//toggle bit no [bits] = set it to zero as it is one
						_useVars[bytes]^= (1<< bits);
						//System.out.println("*** " + getStatementDescr() + "toggle bit " + ((bytes << 5)|bits) + "***");
						_inVars[bytes] = _useVars[bytes] & _defNegVars[bytes];
					}else{
						//System.out.println("not all defs constant for variable " + ((bytes << 5)|bits));
						aIsConst=false;
					}
				}
			}
		}
		if (aIsConst && !(_lhs==null ||_lhs.isEmpty())){
			_unrolledExpr+= "xx_xx=" + _rhs + ";";
			//try{
				_exprValue = evaluate(_unrolledExpr);
				//System.out.println("Expr: *" + _unrolledExpr + "* evaluated to: " + _exprValue);
			//}catch(IllegalArgumentException e){
				//System.out.println("At lest one variable of the expression remains undefined: " + _rhs);
			//}
		}
		//boolean aChangeInResult = (aIsConst!= _isConstant);
		boolean aChangeInResult = (_constantStuckToFalse)? false : aIsConst!= _isConstant;
		_isConstant = aIsConst;
		return aChangeInResult;
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

	public boolean determineKilledSets(){
		boolean aChange=false;
		//if (iYouCouldKill==null) iYouCouldKill=new int[_bytesForStatements];
		int [] aKillOut = new int[_bytesForStatements];
		int [] aKillIn = new int[_bytesForStatements];
		if (!_valid){
			_valid=true;
			//System.out.println("determineKilledSets for " + getStatementDescr() ); 
			for(LiveVariableNode aPred: _predList) {
				int[] aKillCandidatesPred = aPred.getKillCandidates();
				for (int i=0; i<_bytesForStatements;i++){
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
							for (int i=0; i<_bytesForStatements;i++){
								//killDefs = definitions representing kill candidates in program flow & all statements affecting the current variable targeted in this definition
								//all kill candidates -> for a specific variable -> variable determined by defVars  
								//_killDefs[i] |= (aLocalKillCandidates[i] & _varToStatements[(bytes << 5)|bits][i]);
								_killDefs[i] |= (aKillIn[i] & _varToStatements[(bytes << 5)|bits][i]);
								//System.out.println( "killDefs[i] " + _killDefs[i] + " i" + i); 
								//int oldCandidates = aLocalKillCandidates[i];
								//delete all kill candidates for the variable yielded by that definition
								//aLocalKillCandidates[i] &= ~ _varToStatements[(bytes << 5)|bits][i];
								aKillOut[i] &= ~ _varToStatements[(bytes << 5)|bits][i];
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
			for (int i=0; i<_bytesForStatements;i++){
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
		String aName = "| Live ID: " + _myDefID + " | ";
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
		aName += " | to be removed: " + canBeRemoved() + " |";
		return aName; 
	}

	public void invalidate(){
		_valid=false;
	}

	public void reachingDefinitionsInit(){
		invalidate();
		for (int i=0; i<_bytesForStatements;i++)
			_outDefs[i] = _generateDefs[i] & (~_killDefs[i]);
		//System.out.println("out Defs " + getStatementDescr() + " " + _outDefs[0] + " generate Defs: " + _generateDefs[0] + " kill Defs: " + _killDefs[0]);
	}

	public boolean reachingDefinitionAnalysis(){
		boolean aSuccInvalidated=false;
		if (!_valid){
			_valid=true;
			for(LiveVariableNode aPred: _predList) {
				int[] aPredOutDefs = aPred.getOutDefs();
				for (int i=0; i<_bytesForStatements;i++){
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
					_inVars[i] = _useVars[i] | _outVars[i] & _defNegVars[i];
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
		if (_infected)
			return false;
		else{
			if (_superiorBranchNode!=null && _superiorBranchNode.isInfected()){
				_infected=true;
			}else{
				int[] aRelevantDefs = new int[_bytesForStatements];
				for (int bytes=0; bytes<_useVars.length; bytes++){
					for (int bits=0; bits<32;bits++){
						if ((_useVars[bytes] & (1<< bits))!=0){   //for all variables which are defined in this statement
							for (int i=0; i<_bytesForStatements;i++){
								 aRelevantDefs[i] |=_inDefs[i] & _varToStatements[(bytes << 5)|bits][i];
							}						
						}
					}
				}
				_infected |= defsInfected(aRelevantDefs);
			}
		}
		return _infected;
	}
	
	private boolean defsInfected(int[] iDefs){
		boolean aInfected=false;
		for (int bytes=0; bytes<iDefs.length && !aInfected; bytes++){
			for (int bits=0; bits<32 && !aInfected;bits++){
				if ((iDefs[bytes] & (1<< bits))!=0){ 
					aInfected |= _defLookup[(bytes << 5)|bits].isInfected();
				}
			}
		}
		return aInfected;
	}

	public TMLActivityElement getLinkedElement(){
		return _linkedElem;
	}

	public boolean isEmptyNode(){
		if (_succList.size()>1) return false;
		for (int i=0; i<_defVars.length;i++)
			if (_defVars[i]!=0) return false;
		for (int i=0; i<_useVars.length;i++)
			if (_useVars[i]!=0) return false;
		return true;
	}

	public void setSuccessor(LiveVariableNode iSucc){
		if(iSucc!=null){
			_succList.add(iSucc);
			iSucc.setPredecessor(this);
		}
	}

	private void setPredecessor(LiveVariableNode iPred){
		_predList.add(iPred);
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

	public boolean canBeRemoved(){
		if ((_lhs==null || _lhs.isEmpty()) && (_rhs==null || _rhs.isEmpty()) && !(_linkedElem==null || _linkedElem instanceof TMLNotifiedEvent || _linkedElem instanceof TMLRandom)) return false;
		for (int i=0; i<_defVars.length;i++){
			if ((_defVars[i] & _outVars[i]) !=0) return false;
		}
		return true;
	}

	public String getLiveVariableString(){
		return intArrayToHexString(_outVars);
	}

	public String getStateModificationString(){
		return intArrayToHexString(_defVars);
	}

	private String intArrayToHexString(int[] iArray){
		String aResult = "array(" + iArray.length; 	
		for (int bytes=0; bytes<iArray.length; bytes++){
			//for (int bits=0; bits<32; bits+=8)
				aResult+= ", 0x" + Integer.toHexString(iArray[bytes]);
		}
		aResult+=")";
		return aResult;
	}
}
