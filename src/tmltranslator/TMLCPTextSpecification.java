/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
*
* ludovic.apvrille AT telecom-paristech.fr
* andrea.enrici AT telecom-paristech.fr
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
* /**
* Class TMLTextSpecification
* Import and export of TML CP textual specifications
* Creation: 02/06/2014
* @version 1.0 02/06/2014
* @author Ludovic APVRILLE, Andrea ENRICI
* @see
*/


package tmltranslator;

import java.util.*;
import java.io.*;
import myutil.*;
import tmltranslator.tmlcp.*;
import ui.*;

public class TMLCPTextSpecification {
	public final static String CR = "\n";
	public final static String SP = " ";
	public final static String CR2 = "\n\n";
	public final static String SC = ";";
	public final static String C = ",";
	public final static String TAB = "\t";
	public final static String TAB2 = "\t\t";
	//Reserved strings for operations
	public final static String MAIN = "MAIN";
	public final static String END = "END";
	public final static String LOOP = "loop";
	public final static String ENDLOOP = "endloop";
	public final static String STOP = "stop state";
	public final static String START = "start state";
	public final static String SEQUENCE_OP = SC;
	public final static String PARALLELISM_OP = "*";
	public final static String SELECTION_OP = "+";

	private int loopCounter = 0;
	private int nbLoops = 10;
	private String loopDataStructure[] = new String[nbLoops];
    
  private String spec;
	private String title;
	
	private TMLModeling tmlm;
	private ArrayList<TMLTXTError> errors;
	private ArrayList<TMLTXTError> warnings;
	
	// For reading TMLTXT specifications
	private boolean inDec = true;
	private boolean inTask = false;
	private boolean inTaskDec = false;
	private boolean inTaskBehavior = false;
	private TMLTask task;
	private TMLActivityElement tmlae;
	private ArrayList<TMLParserSaveElt> parses;
	
	/*private static String keywords[] = {"BOOL", "INT", "NAT", "CHANNEL", "EVENT", "REQUEST", "LOSSYCHANNEL", "LOSSYEVENT", "LOSSYREQUEST", "BRBW", "NBRNBW", 
		"BRNBW", "INF", "NIB", "NINB", "TASK", "ENDTASK", "IF", "ELSE", "ORIF", "ENDIF", "FOR", "ENDFOR",
	"SELECTEVT", "CASE", "ENDSELECTEVT", "ENDCASE", "WRITE", "READ", "WAIT", "NOTIFY", "NOTIFIED", "RAND", "CASERAND", "ENDRAND", "ENDCASERAND", "EXECI", "EXECC", "DELAY", "RANDOM",
	"RANDOMSEQ", "ENDRANDOMSEQ", "SEQ", "ENDSEQ"};
	
	private String channeltypes[] = {"BRBW", "NBRNBW", "BRNBW"};
	private String eventtypes[] = {"INF", "NIB", "NINB"};
	
	private String beginArray[] = {"TASK", "FOR", "IF", "ELSE", "ORIF", "SELECTEVT", "CASE", "RAND", "CASERAND", "RANDOMSEQ", "SEQ"};
	private String endArray[] = {"ENDTASK", "ENDFOR", "ENDIF", "ELSE", "ORIF", "ENDSELECTEVT", "ENDCASE", "ENDRAND", "ENDCASERAND", "ENDRANDOMSEQ", "ENDSEQ"};	*/

	private String nextElem;	//used to produce the TML text
	private String currentElem;	//used to produce the TML text
	private String currentJunc = "junction";	//used to produce the TML text
	private ArrayList<String> junctionList = new ArrayList<String>();
	private ArrayList<Integer> indexOfConnToRemove;
	private ArrayList<TMLCPConnector> listTMLCPConnectors;
	//private TMLCommunicationPatternPanel tmlcpp;
	private Vector<TDiagramPanel> cpPanelList;

	public TMLCPTextSpecification( String _title, Vector<TDiagramPanel> _cpPanelList ) {
		title = _title;
		cpPanelList = _cpPanelList;
		init();
    }   
	
	/*public TMLCPTextSpecification(String _title, boolean reset) {
		title = _title;
		if (reset) {
			DIPLOElement.resetID();
		}
		init();
	}*/

	private void init()	{
		indexOfConnToRemove = new ArrayList<Integer>();
		listTMLCPConnectors = new ArrayList<TMLCPConnector>();
	}
	
	public void saveFile(String path, String filename) throws FileException {
		TraceManager.addUser("Saving TMLCP spec file in " + path + filename);
        FileUtils.saveFile(path + filename, spec);
    }
	
/*	public TMLModeling getTMLModeling() {
		return tmlm;
	}
	
	public ArrayList<TMLTXTError> getErrors() {
		return errors;
	}  
	
	public ArrayList<TMLTXTError> getWarnings() {
		return warnings;
	}
	
	public void indent() {
		indent(4);
	}
*/
	
/*	public void indent(int _nbDec) {
		int dec = 0;
        int indexEnd;
        String output = "";
        String tmp;
        int nbOpen = 0;
        int nbClose = 0;
		
		while ( (indexEnd = spec.indexOf('\n')) > -1) {
			tmp = spec.substring(0, indexEnd+1);
			try {
                spec = spec.substring(indexEnd+1, spec.length());
            } catch (Exception e) {
                spec = "";
            }
			nbOpen = nbOfOpen(tmp);
            nbClose = nbOfClose(tmp);
			dec -= nbClose * _nbDec;
            tmp = Conversion.addHead(tmp.trim(), ' ', dec);
            dec += nbOpen * _nbDec;
			//TraceManager.addDev("dec=" + dec);
            output += tmp + "\n";
		}
		spec = output;
	}
*/
	
/*	private int nbOfOpen(String tmp) {
		return nbOf(tmp, beginArray);
	}
	
	private int nbOfClose(String tmp) {
		return nbOf(tmp, endArray);
	}
*/	
/*	private int nbOf(String _tmp, String[] array) {
		String tmp;
		int size;
		
		for(int i=0; i<array.length; i++) {
			if (_tmp.startsWith(array[i])) {
				tmp = _tmp.substring(array[i].length(), _tmp.length());
				//TraceManager.addDev("tmp=" + tmp + " _tmp" + _tmp + " array=" + array[i]);
				if ((tmp.length() == 0) || (tmp.charAt(0) == ' ') || (tmp.charAt(0) == '(') || (tmp.charAt(0) == '\n')) {
						//TraceManager.addDev("Returning 1!!");
						return 1;
				}
			}
		}
		return 0;
	}
*/
	
	public String toString() {
		return spec;
	}
	
	public StringBuffer toTextFormat() {

		StringBuffer ADstring = new StringBuffer("");;
		StringBuffer SDstring = new StringBuffer("");
		//tmlcp.sortByName();
		for( TDiagramPanel panel: cpPanelList )	{
			TraceManager.addDev( "I AM AVOIDING THE DS:" + panel.getName() );
		}

		ADstring.append( makeActivityDiagram( cpPanelList.get(0) ) );	//the main CP is generated first

		for( int panelCounter = 1; panelCounter < cpPanelList.size(); panelCounter++ )	{
			if( cpPanelList.get( panelCounter) instanceof ui.tmlsd.TMLSDPanel )	{
				SDstring.append( makeSequenceDiagram( cpPanelList.get( panelCounter ) ) );
				}
			if( cpPanelList.get( panelCounter ) instanceof ui.tmlcp.TMLCPPanel )	{
				ADstring.append( makeActivityDiagram( cpPanelList.get( panelCounter ) ) );
			}
			TraceManager.addDev( "PANEL number: " + panelCounter + " " + cpPanelList.get( panelCounter ) );
		}

		return ADstring.append( SDstring );
	}

	public StringBuffer makeActivityDiagram( TDiagramPanel panel )	{
		return new StringBuffer("Toto");
	}

	public StringBuffer makeSequenceDiagram( TDiagramPanel panel )	{
		return new StringBuffer("Toto");
	}
	
//	public String makeDeclarations() {
//
//		ArrayList<TMLCPElement> listElements;
//		currentElem = "";
//		nextElem = "";
//		String newSb = "";
//		String sb = "";
//		sb += "// TML Communication Pattern - FORMAT 0.1" + CR;
//		sb += "// Communication Pattern: " + title + CR;
//		sb += "// Generated: " + new Date().toString() + CR2; 
//
//		//Generating code for the main CP
//		TMLCPActivityDiagram mainCP = tmlcp.getMainCP();
//		sb += "COMMUNICATION_PATTERN" + SP + mainCP.getName() + CR + TAB;
//		listElements = mainCP.getElements();
//		for( TMLCPElement elem : listElements )	{
//			if( elem instanceof tmltranslator.tmlcp.TMLCPRefAD )	{
//				tmltranslator.tmlcp.TMLCPRefAD refAD = (tmltranslator.tmlcp.TMLCPRefAD) elem;
//				sb += "ACTIVITY" + SP + refAD.getName() + CR + TAB;
//			}
//			if( elem instanceof tmltranslator.tmlcp.TMLCPRefSD )	{
//				tmltranslator.tmlcp.TMLCPRefSD refSD = (tmltranslator.tmlcp.TMLCPRefSD) elem;
//				sb += "SEQUENCE" + SP + refSD.getName() + CR + TAB;
//			}
//			if( elem instanceof TMLCPConnector )	{
//				TMLCPConnector connector = (TMLCPConnector) elem;
//				listTMLCPConnectors.add( connector );
//			}
//		}
//		//global variables should go here, but there are none up to now
//		sb += CR + TAB + MAIN + CR + TAB2 + "<>" + SC + SP;
//		//Yet to add: nested forks, choices and junctions
//		currentElem = START;
//		boolean encounteredSequence = false;
//		boolean endOfGraph = false;
//		boolean comingFromMain = true;	//flags to trace the history of the sub-routine calls
//		boolean comingFromChoice = false;
//		boolean comingFromFJ = false;
//		while( !endOfGraph )	{
//			nextElem = getNextElem();	//get the name of the next element
//			String token = nextElem;
//			if( token.length() > 3 )	{
//				token = token.substring(0,4);
//			}
//			switch( token )	{
//				case "fork":	{	//The firts encountered object is a fork: go until join. Return the connector that follows the join
//					sb += "{" + exploreForkJoin( comingFromMain, comingFromChoice, comingFromFJ );
//					if( !nextElem.equals(STOP) )	{
//						String temp = sb.substring(0,sb.length()-3) + "}" + SEQUENCE_OP + SP + nextElem + SEQUENCE_OP;
//						sb = temp;
//					}
//					else	{
//						endOfGraph = true;
//						String temp = sb.substring(0,sb.length()-3) + "}" + SP + "><";
//						sb = temp;
//					}
//					encounteredSequence = false;
//					break;
//				}
//				case "junc":	{	//The first encountered object is a junction: count the loop
//					if( nextElem.length() >=8 )	{
//						if( nextElem.substring(0,8).equals("junction") )	{	//ensure the name starts with junction
//							currentJunc = nextElem;
//							sb += CR + TAB2 + LOOP + loopCounter + CR + TAB2;
//							}
//						}	//else I should trigger an error
//					encounteredSequence = false;
//				break;
//				}
//				case "choi":	{ //The first encountered object is a choice: exploration of the graph will be over upon return
//					if( nextElem.length() >= 6 )	{
//						if( nextElem.substring(0,6).equals("choice") )	{	//ensure the name starts with choice
//							comingFromMain = true;
//							comingFromChoice = false;
//							comingFromFJ = false;
//							if( encounteredSequence )	{
//								sb += CR + TAB2;	//When there is a sequence of diags before the choice node
//							}
//							sb += CR + TAB2 + exploreChoiceBranches( comingFromMain, comingFromChoice, comingFromFJ );
//							String temp = sb.substring(0, sb.length()-1) + SP + "><";	//drop semicolon
//							sb = temp;
//							endOfGraph = true;
//						}
//					}	//else I should trigger an error
//					encounteredSequence = false;
//				break;
//				}
//				default:	{	//The first encountered object is a diagram reference name: a sequence
//					sb += nextElem + SEQUENCE_OP;
//					encounteredSequence = true;
//				break;
//				}
//			}	//End of switch
//			if( nextElem.equals( "ERROR" ) )	{
//				TraceManager.addDev( "ERROR WHEN GENERATING TML CODE in mainCP: " + nextElem);
//				endOfGraph = true;
//				break;
//			}
//			if( nextElem.equals(STOP) )	{
//				String temp = sb.substring(0,sb.length()-12) + SP + "><";
//				sb = temp;
//				break;
//			}
//			currentElem = nextElem; //advance to next connector
//		}	//End of while loop
//		sb += CR + TAB + END + CR + END + CR2;
//
//		/***************************************************************************/
//		//Generating code for the other ADs
//		ArrayList<TMLCPActivityDiagram> listADs = tmlcp.getCPActivityDiagrams();
//		for( TMLCPActivityDiagram AD: listADs )	{
//			TraceManager.addDev( "GENERATING THE CODE FOR THE AD " + AD.getName() );
//			sb += "START ACTIVITY" + SP + AD.getName() + CR + TAB;
//			listTMLCPConnectors.clear();
//			listElements = AD.getElements();
//			for( TMLCPElement elem : listElements )	{
//				if( elem instanceof tmltranslator.tmlcp.TMLCPRefAD )	{
//					tmltranslator.tmlcp.TMLCPRefAD refAD = (tmltranslator.tmlcp.TMLCPRefAD) elem;
//					sb += "ACTIVITY" + SP + refAD.getName() + CR + TAB;
//				}
//				if( elem instanceof tmltranslator.tmlcp.TMLCPRefSD )	{
//					tmltranslator.tmlcp.TMLCPRefSD refSD = (tmltranslator.tmlcp.TMLCPRefSD) elem;
//					sb += "SEQUENCE" + SP + refSD.getName() + CR + TAB;
//				}
//				if( elem instanceof TMLCPConnector )	{
//					TMLCPConnector connector = (TMLCPConnector) elem;
//					listTMLCPConnectors.add( connector );
//					//TraceManager.addDev( connector.getStartName() + SP + connector.getEndName() + SP + connector.getGuard() );
//				}
//			}
//			//global variables should go here, but there are none up to now
//			sb += CR2 + TAB + MAIN + CR + TAB2 + "<>" + SC + " ";	//should I start with an open parenthesis?
//			//up to know I just consider sequence, activities, fork, join, choice but no nested structures to keep things simple!
//			currentElem = START;
//			endOfGraph = false;
//			comingFromMain = true;	//flags to trace the history of the sub-routine calls
//			comingFromChoice = false;
//			comingFromFJ = false;
//			loopCounter = 0;
//			encounteredSequence = false;
//			while( !endOfGraph )	{
//				nextElem = getNextElem();	//get the name of the next element
//				String token = nextElem;
//				if( token.length() > 3 )	{
//					token = token.substring(0,4);
//				}
//				switch( token )	{
//					case "fork":	{	//The firts encountered object is a fork: go until join. Return the connector that follows the join
//						sb += "{" + exploreForkJoin( comingFromMain, comingFromChoice, comingFromFJ );
//						if( !nextElem.equals(STOP) )	{
//							String temp = sb.substring(0,sb.length()-3) + "}" + SEQUENCE_OP + SP + nextElem + SEQUENCE_OP;
//							sb = temp;
//						}
//						else	{
//							endOfGraph = true;
//							String temp = sb.substring(0,sb.length()-3) + "}" + SP + "><";
//							sb = temp;
//						}
//						encounteredSequence = false;
//					break;
//					}
//					case "junc":	{	//The first encountered object is a junction: count the loop
//						if( nextElem.length() >=8 )	{
//							if( nextElem.substring(0,8).equals("junction") )	{	//ensure the name starts with junction
//								currentJunc = nextElem;
//								junctionList.add( nextElem );
//								sb += CR + TAB2 + LOOP + loopCounter + CR + TAB2;
//								//loopCounter += 1;
//								}
//							}	//else I should trigger an error
//						encounteredSequence = false;
//					break;
//					}
//					case "choi":	{ //The first encountered object is a choice: exploration of the graph will be over upon return
//						if( nextElem.length() >= 6 )	{
//							if( nextElem.substring(0,6).equals("choice") )	{	//ensure the name starts with choice
//								comingFromMain = true;
//								comingFromChoice = false;
//								comingFromFJ = false;
//								if( encounteredSequence )	{
//									sb += CR + TAB2;
//								}
//								sb += exploreChoiceBranches( comingFromMain, comingFromChoice, comingFromFJ );
//								String temp = sb.substring(0, sb.length()-1) + SP + "><";	//drop semicolon
//								sb = temp;
//								currentJunc = "";
//								endOfGraph = true;
//							}
//						}	//else I should trigger an error
//						encounteredSequence = false;
//					break;
//					}
//					default:	{	//The first encountered object is a diagram reference name: a sequence
//						sb += nextElem + SEQUENCE_OP;
//						encounteredSequence = true;
//					break;
//					}
//				}	//End of switch
//				if( nextElem.equals( "ERROR"  ) )	{
//					TraceManager.addDev( "ERROR WHEN GENERATING TML CODE in otherADs: " + nextElem );
//				break;
//				}
//				currentElem = nextElem; //advance to next connector
//			}	//End of while loop
//			if( comingFromFJ )	{
//				newSb = sb.substring( 0, sb.length()-2 );	// drop last PARALLELISM_OP
//			}
//			sb += CR + TAB + END + CR + END + CR2;
//
//		} //End of Activity Diagram analysis
//
//		/***************************************************************************/
//		//Generating code for Sequence Diagrams
//		ArrayList<TMLCPSequenceDiagram> listSDs = tmlcp.getCPSequenceDiagrams();
//		for( int i = 0; i < listSDs.size(); i++ )	{
//			TMLCPSequenceDiagram SD = listSDs.get(i);
//			sb += "SEQUENCE " + SD.getName() + CR + TAB;
//			ArrayList<tmltranslator.tmlcp.TMLSDInstance> listInstances = SD.getInstances();
//			ArrayList<TMLSDMessage> listMessages = SD.getMessages();
//			ArrayList<TMLSDAction> listActions = SD.getActions();
//			ArrayList<TMLAttribute> listAttributes = SD.getAttributes();
//			for( tmltranslator.tmlcp.TMLSDInstance inst: listInstances )	{
//				sb += inst.getType() + " " + inst.getName() + CR + TAB;
//			}
//			for( TMLAttribute attr: listAttributes )	{
//				sb += attr.getType().toString().toUpperCase() + " " + attr.getInstanceName() + "." + attr.getName() + CR + TAB;
//			}
//			for( TMLAttribute attr: listAttributes )	{
//				if( attr.isBool() )	{
//					sb += attr.getInstanceName() + "." + attr.getName() + " = " + attr.getInitialValue().toUpperCase() + CR + TAB;
//				}
//				else	{
//					sb += attr.getInstanceName() + "." + attr.getName() + " = " + attr.getInitialValue() + CR + TAB;
//				}
//			}
//			String temp = "";//sb.substring( 0, sb.length()-3 );	//Remove trailing CR + TAB + TAB
//			sb += CR + TAB + MAIN + CR + TAB + TAB;
//			ArrayList<TMLSDItem> listItems = SD.getItems();
//			Collections.sort( listItems ); 			//actions and messages must be ordered and printed according to Y before being written!
//			/*TraceManager.addDev( "PRINTING SORTED ITEMS" );
//			for( TMLSDItem item: listItems )	{	//print the items
//				TraceManager.addDev( item.toString() );
//			}*/
//			for( TMLSDItem item: listItems )	{	
//				if( item.getInstanceName().length() > 0 )	{	//the item is an action (attribute)
//					sb += item.getInstanceName() + "." + item.getName();
//					sb += CR + TAB + TAB;
//				}
//				else	{	//The item is a message
//					ArrayList<TMLSDAttribute> listAttr = item.getAttributes();
//					if( listAttr.size() == 0 )	{	//message with no parameters
//						sb += item.getSenderName() + "." + item.getName() + "(" + item.getReceiverName() + ")";						
//					}
//					else	{	//message with at least one parameter
//						sb += item.getSenderName() + "." + item.getName() + "(" + item.getReceiverName() + ",";
//						for( int p = 0; p < listAttr.size(); p++ )	{
//							if( p == (listAttr.size() - 1) )	{
//								sb += listAttr.get(p).getName() + ")";
//							}
//							else	{
//								sb += listAttr.get(p).getName() + ",";
//							}
//						}
//					}
//					sb += CR + TAB + TAB;
//				}
//			}
//			temp = sb.substring( 0, sb.length()-1 );	//Remove trailing TAB
//			sb = temp + END + CR + END + CR2;
//		}
//		return sb;
//	}
//
//	//Does not work if there are nested fork-joins
//	private String exploreForkJoin( boolean comingFromMain, boolean comingFromChoice, boolean comingFromFJ )	{
//
//		String token, branchSb, globalSb = "";
//		boolean exit;
//		boolean encounteredSequence = false;
//		boolean fromMain = comingFromMain;
//		boolean fromChoice = comingFromChoice;
//		boolean fromFJ = comingFromFJ;
//		ArrayList<TMLCPConnector> connToFork = new ArrayList<TMLCPConnector>();
//		TMLCPConnector conn;
//		int i;
//		ArrayList<String> diagsToJoin = new ArrayList<String>();
//
//		//Retrieve all connectors starting from the fork node of nextElem
//		for( i = 0; i < listTMLCPConnectors.size(); i++ )	{
//			conn = listTMLCPConnectors.get(i);
//			if( conn.getStartName().equals( nextElem ) )	{
//				connToFork.add( conn );
//			}
//		}
//		for( int j = 0; j < connToFork.size(); j++ )	{	//For each output connector of the start fork node
//			currentElem = connToFork.get(j).getEndName();
//			nextElem = currentElem;
//			branchSb = "{";
//			exit = false;
//			encounteredSequence = false;
//			while( !exit )	{
//				token = nextElem;
//				if( token.length() > 3 )	{
//					token = token.substring(0,4);
//				}
//				switch( token )	{
//					case "fork":	{	//ERROR: no nested forks
//						TraceManager.addDev( "ERROR: ENCOUNTERED NESTED FORK!" );
//						System.exit(0);
//					break;
//					}
//					case "join":	{	//End of branch, regardless if coming from main or choice exploration
//						if( encounteredSequence )	{
//							String temp = branchSb.substring(0,branchSb.length()-2);
//							branchSb = temp;
//						}
//						encounteredSequence = false;
//						exit = true;
//					break;
//					}
//					case "junc":	{	//register the junction and go on
//						if( nextElem.length() >=8 )	{
//							if( nextElem.substring(0,8).equals("junction") )	{	//ensure the name starts with junction
//								if( fromChoice )	{	//ERROR: no nested choices
//									TraceManager.addDev( "ERROR: ENCOUNTERED JUNCTION IN FORK WHILE COMING FROM CHOICE!" );
//									exit = true;
//									//System.exit(0);
//								}
//								else	{
//									branchSb += CR + TAB2 + LOOP + loopCounter + CR + TAB2;
//								}
//							}
//						}	//else I should trigger an error
//						encounteredSequence = false;
//					break;
//					}
//					case "choi":	{	//Encountered a choice within a fork-join
//						if( nextElem.length() >= 6 )	{
//							if( nextElem.substring(0,6).equals("choice") )	{	//ensure the name starts with choice
//								if( fromChoice )	{
//									TraceManager.addDev( "ERROR: ENCOUNTERED NESTED FORK!" );
//									exit = true;
//									//System.exit(0);
//								}
//								else	{
//									fromMain = false;
//									fromChoice = false;
//									fromFJ = true;
//									branchSb += exploreChoiceBranches( fromMain, fromChoice, fromFJ ); //The exploration will return when encountering a join node
//									loopCounter += 1;
//									exit = true;	
//								}
//							}
//						}
//						encounteredSequence = false;
//					break;
//					}
//					case "stop":	{	//ERROR: fork must always terminate with a join
//						TraceManager.addDev( "ERROR: ENCOUNTERED STOP BEFORE JOIN!" );
//						exit = true;
//						//System.exit(0);
//					break;
//					}
//					default:	{	//Found a sequence/activity diagram, continue to explore
//						//TraceManager.addDev("IN THE DEFAULT BRANCH WITH " + currentElem);
//						branchSb += nextElem + SEQUENCE_OP + SP;
//						encounteredSequence = true;
//					break;
//					}
//				}	//End of switch
//				if( nextElem.equals( "ERROR" ) )	{
//					TraceManager.addDev( "ERROR WHEN GENERATING TML CODE in exploreForkJoin: " + nextElem );
//					exit = true;
//				break;
//				}
//				currentElem = nextElem; // advance to next connector
//				nextElem = getNextElem();
//			}	//End of while
//			if( branchSb.charAt(branchSb.length()-1) == ';' )	{	//Remove trailing semi-colons
//				String temp = branchSb.substring( 0, branchSb.length()-1);
//				branchSb = temp;
//			}
//			globalSb += branchSb + "}" + SP + PARALLELISM_OP + SP;
//		}	//End of for, end of exploration of all branches
//		if( fromChoice )	{	//Remove trailing semi-colons
//			String temp = globalSb.substring( 0, globalSb.length() - 3 );
//			globalSb = temp;
//		}
//		return globalSb;
//	}
//
//	private String exploreChoiceBranches( boolean comingFromMain, boolean comingFromChoice, boolean comingFromFJ )	{
//	
//		boolean fromMain = comingFromMain;
//		boolean fromChoice = comingFromChoice;
//		boolean fromFJ = comingFromFJ;
//		boolean exit = false;
//		int i;
//		String token, branchSb, currentElemToPass = "", nextElemToPass = "", globalSb = "";
//		ArrayList<TMLCPConnector> connToChoice = new ArrayList<TMLCPConnector>();
//		ArrayList<String> diagsToJoin = new ArrayList<String>();
//		TMLCPConnector conn;
//
//		//Retrieve all connectors starting from the choice node of nextElem
//		for( i = 0; i < listTMLCPConnectors.size(); i++ )	{
//			conn = listTMLCPConnectors.get(i);
//			if( conn.getStartName().equals( nextElem ) )	{
//				connToChoice.add( conn );
//			}
//		}
//		for( int j = 0; j < connToChoice.size(); j++ )	{
//			currentElem = connToChoice.get(j).getEndName();
//			nextElem = currentElem;
//			branchSb = TAB + connToChoice.get(j).getGuard() + "{";
//			exit = false;
//			while( !exit )	{
//				token = nextElem;
//				if( token.length() > 3 )	{
//					token = token.substring(0,4);
//				}
//				switch( token )	{
//					case "fork":	{	//Encountered a fork on a choice branch
//						fromMain = false;
//						fromFJ = false;
//						fromChoice = true;
//						branchSb += exploreForkJoin( fromMain, fromChoice, fromFJ );	//Explore until the closing join node
//					break;
//					}
//					case "join":	{	//Condition for exiting the branch
//						if( fromMain )	{
//							TraceManager.addDev( "ERROR: ENCOUNTERED JOIN IN CHOICE WHILE COMING FROM MAIN!" );
//							exit = true;
//							//System.exit(0);
//						}
//						else if( fromFJ )	{
//							currentElemToPass = currentElem;
//							nextElemToPass = nextElem;
//							String temp = branchSb.substring( 0, branchSb.length() - 2 );	//Remove trailing semicolon
//							branchSb = temp + "}" + SC + SP + ENDLOOP + loopCounter + CR + TAB;
//							exit = true;
//						}
//					break;
//					}
//					case "choi":	{	//not possible up to now to have nested choices
//						if( nextElem.length() >= 6 )	{
//							if( nextElem.substring(0,6).equals("choice") )	{	//ensure the name starts with choice
//								TraceManager.addDev( "ENCOUNTERED NESTED CHOICE WHILE COMING FROM CHOICE!" );
//								exit = true;
//								//System.exit(0);
//							}
//						}
//					break;
//					}
//					case "junc":	{	//Terminate branch exploration
//						if( nextElem.length() >=8 )	{
//							if( nextElem.substring(0,8).equals("junction") )	{	//ensure the name starts with junction
//								String temp = branchSb.substring( 0, branchSb.length() - 2 );	//Remove trailing semicolon
//								branchSb = temp + "}" + SC + SP + LOOP + loopCounter + CR + TAB2;
//								exit = true;
//							}
//						}
//					break;
//					}
//					case "stop":	{	//Condition for exiting the branch
//						if( fromFJ )	{
//							TraceManager.addDev( "ERROR: ENCOUNTERED NESTED CHOICE WHILE COMING FROM FJ!" );
//							exit = true;
//							//System.exit(0);
//						}
//						else if( fromMain )	{
//							String temp = branchSb.substring( 0, branchSb.length() - 2 );	//Remove trailing semicolon
//							branchSb = temp + "}" + SC + SP + ENDLOOP + loopCounter + CR + TAB2;
//							exit = true;
//						}
//					break;
//					}
//					default:	{	//Found a reference to a sequence/activity diagram: sequence operator
//						branchSb += nextElem + SEQUENCE_OP + SP;
//					break;
//					}
//				}	//End of switch
//				if( nextElem.equals( "ERROR" ) )	{
//					TraceManager.addDev( "ERROR WHEN GENERATING TML CODE in exploreChoiceBranches: " + nextElem );
//					exit = true;
//					//System.exit(0);
//				}
//				currentElem = nextElem; 	//advance to next connector
//				nextElem = getNextElem();
//			}	//End of while
//			globalSb += branchSb;
//		}	//End of for, end of exploration of all branches
//		if( fromFJ )	{	//Restore correct elements when called from a fork-join
//			currentElem = currentElemToPass;
//			nextElem = nextElemToPass;
//		}
//		return globalSb + ENDLOOP + loopCounter + SP;
//	}
//
//	//Look for a connector that starts from currentElem and get the endName
//	private String getNextElem()	{
//
//		TMLCPConnector conn;
//		String endName = "";
//		int i;
//		for( i = 0; i < listTMLCPConnectors.size(); i++ )	{
//			conn = listTMLCPConnectors.get(i);
//			if( conn.getStartName().equals( currentElem ) )	{
//				endName = conn.getEndName();
//				break;
//			}
//		}
//		return endName;
//	}

}	//End of class
