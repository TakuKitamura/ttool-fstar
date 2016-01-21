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
      private String endArray[] = {"ENDTASK", "ENDFOR", "ENDIF", "ELSE", "ORIF", "ENDSELECTEVT", "ENDCASE", "ENDRAND", "ENDCASERAND", "ENDRANDOMSEQ", "ENDSEQ"};        */

    private static String SEQUENCE_DIAGRAM = "SEQUENCE";

    private String nextElem;    //used to produce the TML text
    private String currentElem; //used to produce the TML text
    private String currentJunc = "junction";    //used to produce the TML text
    private ArrayList<String> junctionList = new ArrayList<String>();
    private ArrayList<Integer> indexOfConnToRemove;
    private ArrayList<TMLCPConnector> listTMLCPConnectors;
    private StringBuffer sbFork;
    private StringBuffer sbJunction;

    public TMLCPTextSpecification(String _title) {
        title = _title;
        init();
    }

    public TMLCPTextSpecification(String _title, boolean reset) {
        title = _title;
        if (reset) {
            DIPLOElement.resetID();
        }
        init();
    }

    private void init() {
        indexOfConnToRemove = new ArrayList<Integer>();
        listTMLCPConnectors = new ArrayList<TMLCPConnector>();
    }

    public void saveFile(String path, String filename) throws FileException {
        TraceManager.addUser("Saving TMLCP spec file in " + path + filename);
        FileUtils.saveFile(path + filename, spec);
    }

    /*  public TMLModeling getTMLModeling() {
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

    /*  public void indent(int _nbDec) {
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

    /*  private int nbOfOpen(String tmp) {
        return nbOf(tmp, beginArray);
        }

        private int nbOfClose(String tmp) {
        return nbOf(tmp, endArray);
        }
    */
    /*  private int nbOf(String _tmp, String[] array) {
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

    public String toTextFormat( TMLCP tmlcp ) {

        spec = makeHeader();
        spec += makeTMLTextSequenceDiagrams( tmlcp );
        spec += makeTMLTextActivityDiagrams( tmlcp );
        return spec;
    }

    /*public String toTextFormat( TMLMapping tmap ) {

        tmap.sortByName();
          spec = makeDeclarations( tmap );
          spec += makeTasks( tmap );
          indent();
          return spec;
        return "FAKE";
    }*/

    private String makeHeader() {

        StringBuffer sb = new StringBuffer( "// TML Communication Pattern - FORMAT 0.1" + CR );
        sb.append( "// Communication Pattern: " + title + CR );
        sb.append( "// Generated: " + new Date().toString() + CR2 );
        return sb.toString();
    }

    private String makeTMLTextSequenceDiagrams( TMLCP tmlcp )   {

        StringBuffer sb = new StringBuffer("");
        StringBuffer sbAttributes = new StringBuffer("");

        //Generating code for Sequence Diagrams
        ArrayList<TMLCPSequenceDiagram> listSDs = tmlcp.getCPSequenceDiagrams();
        for( TMLCPSequenceDiagram seqDiag: listSDs )    {
            sb.append( SEQUENCE_DIAGRAM + SP + seqDiag.getName() + CR2 );
            ArrayList<tmltranslator.tmlcp.TMLSDInstance> listInstances = seqDiag.getInstances();
            if( seqDiag.getAttributes().size() > 0 )    {
                for( TMLAttribute attribute: seqDiag.getAttributes() )  {
                    if( !sbAttributes.toString().contains( attribute.toString() ) )     { //if attribute not already contained, then add it
											if( attribute.toString().charAt( attribute.toString().length() - 1) == '=' )	{
												//delete trailing =
                        sbAttributes.append( seqDiag.getName() + "." + attribute.toString().substring( 0, attribute.toString().length()-1 ) + CR );
											}
											else	{
                        sbAttributes.append( seqDiag.getName() + "." + attribute.toString() + CR );
											}
                    }
                }
            }
            for( tmltranslator.tmlcp.TMLSDInstance inst: listInstances )        {
                sb.append( TAB + inst.getType() + SP + inst.getName() + CR );
                if( inst.getEvents().size() > 0 )       {
                    ArrayList<TMLSDEvent> listEvents = inst.getEvents();
                    Collections.sort( listEvents );
                    for( TMLSDEvent event: listEvents ) {
                        sb.append( TAB2 + event.toString() + CR );
                    }
                    sb.append( CR );
                }
            }
            sb.append( END + CR2 );
        }
        if( sbAttributes.length() > 0 ) {
					return sbAttributes.toString() + CR2 + sb.toString();
        }
        return sb.toString();
    }

    private String makeTMLTextActivityDiagrams( TMLCP tmlcp )   {

        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        ArrayList<TMLCPActivityDiagram> activityDiagList = tmlcp.getCPActivityDiagrams();
        ArrayList<TMLCPActivityDiagram> junctionDiagList = new ArrayList<TMLCPActivityDiagram>();
        ArrayList<String> diagsToRemove = new ArrayList<String>();

        for( TMLCPActivityDiagram ad: activityDiagList )        {
            String s = ad.getName();
            if( s.length() >= 9 )       {
                if( s.substring( 0,8 ).equals( "junction" ) )   {
                    junctionDiagList.add( ad );
                    diagsToRemove.add( ad.getName() );
                }
            }
        }

        // Remove junction diagrams from the main list
        Iterator<TMLCPActivityDiagram> it = activityDiagList.iterator();
        while( it.hasNext() )   {
            TMLCPActivityDiagram temp = it.next();
            if( diagsToRemove.contains( temp.getName() ) )      {
                it.remove();
            }
        }

        TMLCPActivityDiagram mainCP = tmlcp.getMainCP();
        sb.append( CR + "ACTIVITY " + mainCP.getName() + CR2 + TAB + "MAIN" );
        sb.append( makeSingleActivityDiagram2( mainCP ) + CR + TAB + "END" + CR );
        sb.append( CR + "END " + mainCP.getName() + CR );
        for( TMLCPActivityDiagram ad: activityDiagList )        {
            sb.append( CR + "ACTIVITY " + ad.getName() + CR2 + TAB + "MAIN" );
            sb.append( makeSingleActivityDiagram2( ad ) + CR + TAB + "END" + CR );
            sb.append( CR + "END " + ad.getName() + CR );
        }

        //return sb.toString();
        ArrayList<String> junctionTMLCodeList = new ArrayList<String>();
        ArrayList<String> junctionNamesList = new ArrayList<String>();
        for( TMLCPActivityDiagram ad: junctionDiagList )        {
            String temp = new String( CR + "ACTIVITY " + ad.getName() + CR2 + TAB + "MAIN" + CR + TAB );
            temp += makeSingleJunctionDiagram( ad );
            temp += ( TAB + "><" + CR + TAB + "END" + CR2 + "END " + ad.getName() + CR );
            sb.append( temp );          //the total text
            junctionTMLCodeList.add( temp );    //each entry contains the text for one single junctionAD
            junctionNamesList.add( getJunctionName( temp ) );
        }

        //Merge nested junction-choice
        /*ArrayList<Integer> indexToRemove = new ArrayList<Integer>();
          for( String s: junctionTMLCodeList )  {
          if( !s.equals("") )   {
          if( s.contains( "JUNCTION" ) )        {
          String[] v = s.split( "JUNCTION" );
          String[] v1 = v[1].split( CR );
          String junctionName = v1[0];
          int index = junctionNamesList.indexOf( junctionName );
          if( index != -1 )     {       //junctionName not found
          v = s.split( "JUNCTION(.)*\\n" );     //re-split the string to eliminate JUNCTION + junctionName
          String completeText = v[0] + junctionName + CR + v[1] + CR + TAB + TAB + junctionTMLCodeList.get( index );
          // merge the TML text code in the data structure
          junctionTMLCodeList.set( junctionTMLCodeList.indexOf(s), completeText );
          junctionTMLCodeList.set( index, "" );
          junctionNamesList.set( index, "" );
          indexToRemove.add( index );
          }
          else  {
          String completeText = "";
          }
          }
          }
          }

          for( Integer i: indexToRemove )       {
          junctionNamesList.remove( (int) i);
          junctionTMLCodeList.remove( (int) i);
          }

          for( String s: junctionNamesList )    {
          if( sb.toString().contains( "JUNCTION " + s ) )       {
          String[] v = sb.toString().split( "JUNCTION " + s );
          sb = new StringBuffer( v[0] + junctionTMLCodeList.get( junctionNamesList.indexOf( s ) ) + v[1] );
          }
          }*/

        return sb.toString();
    }

    private String getJunctionName( String temp )       {

        String v[] = temp.split("\\n");
        for( int i = 0; i < v.length; i++ )     {
            if( v[i].contains( "LOOP" ) )       {
                String v1[] = v[i].split("LOOP ");
                String junctionName = v1[1];
                return junctionName;
            }
        }
        return "ERROR";
    }

    private String makeSingleActivityDiagram2(TMLCPActivityDiagram ad) {

        StringBuffer sb = new StringBuffer( TAB + CR + TAB + "<>; " );
        TMLCPElement currentElement, nextElement;
        ArrayList<TMLCPElement> nextElements;

        currentElement = getStartState( ad ).getNextElements().get(0);  //get the first element after the start state

	ArrayList<TMLCPElement> met = new ArrayList<TMLCPElement>();

        sb.append(parseElement2(currentElement, met));

        return sb.toString() + CR + TAB + "><";
    }

    private String parseElement2(TMLCPElement currentElement, ArrayList<TMLCPElement> met) {

	if (currentElement == null) {
	    return "";
	}

	if (met.contains(currentElement)) {
	    return "";
	}

	if (currentElement instanceof TMLCPJoin) {
	    return "";
	}
	
	met.add(currentElement);

	if (currentElement instanceof TMLCPStop) {
	    //return "";
	    return( TAB + "><" );
	}

	if (currentElement instanceof TMLCPFork) {
	    return parseFork2((TMLCPFork)(currentElement), met);
	}
	
	if (currentElement instanceof TMLCPForLoop) {
	    //TraceManager.addDev("Handling ForLoop: " + currentElement);
	    return parseForLoop((TMLCPForLoop)(currentElement), met);
	}

	if (currentElement instanceof TMLCPChoice) {
	    return parseChoice2((TMLCPChoice)(currentElement), met);
	}

		
	String ret = parseSequence(currentElement);
	//ret += SEQUENCE_OP;
	ret += parseElement2(currentElement.getNextElements().get(0), met);
	return ret;
	
    }

    private String parseChoice2(TMLCPChoice choice, ArrayList<TMLCPElement> met)  {
	StringBuffer sb = new StringBuffer();
	ArrayList<TMLCPElement> branches = choice.getNextElements();
        ArrayList<String> guards = choice.getGuards();

	int index = 0;

	for( TMLCPElement element: branches )   {
	    sb.append( CR + TAB2 + guards.get(index) + SP);
	    sb.append(parseElement2(element, met));
	    index ++;
	}

	return sb.toString();
		  
    }


    private String parseForLoop(TMLCPForLoop loop, ArrayList<TMLCPElement> met)  {
	StringBuffer sb = new StringBuffer();
	ArrayList<TMLCPElement> nextElements = loop.getNextElements();
	sb.append( SP + "{" );
	
	TMLCPElement inLoop = nextElements.get(0);
	sb.append("(" + loop.getInit().replaceAll("\\s+","") + ";" + loop.getCondition().replaceAll("\\s+","") + ";" + loop.getIncrement().replaceAll("\\s+","") + ") ");
	sb.append(parseElement2(nextElements.get(0), met));	//first the for-loop body
	sb.append( "}" + SEQUENCE_OP );
	sb.append(parseElement2(nextElements.get(1), met));	//then the diagrams that follow the for-loop termination

	return sb.toString();
		  
    }

    private String parseFork2(TMLCPFork fork,  ArrayList<TMLCPElement> met)  {
	StringBuffer sb = new StringBuffer();
	ArrayList<TMLCPElement> nextElements = fork.getNextElements();
	

        TMLCPElement nextElement = null;

        sb.append( SP + "{" );
        for(TMLCPElement elt: nextElements)    {
	    sb.append( SP + "{" );
	    sb.append(parseElement2(elt, met));
	    sb = removeTrailingSymbols(sb, 2);
	    sb.append("}" + SP + PARALLELISM_OP);
	    
	}
        sb = removeTrailingSymbol( sb );
        sb.append( "}" + SEQUENCE_OP );

	// Search for the join
	TMLCPElement myElt = nextElements.get(0);
	while (!(myElt instanceof TMLCPJoin)) {
	    myElt = myElt.getNextElements().get(0);
	}
	// Start parsing after the join
	myElt = myElt.getNextElements().get(0);
        sb.append(parseElement2(myElt, met));
	
	return sb.toString();
    }




    private String makeSingleActivityDiagram( TMLCPActivityDiagram ad ) {

        StringBuffer sb = new StringBuffer( TAB + CR + TAB + "<>; " );
        TMLCPElement currentElement, nextElement;
        ArrayList<TMLCPElement> nextElements;

        currentElement = getStartState( ad ).getNextElements().get(0);  //get the first element after the start state
        while( !(currentElement instanceof TMLCPStop) ) {
            nextElements = currentElement.getNextElements();
            if( nextElements.size() > 1 )       {       // currentElement is a fork node
                sbFork = new StringBuffer();
                currentElement = parseFork( nextElements );     // currentElement is the closing join, use attribute sbFork
                sb.append( sbFork.toString() );
                sbFork.setLength(0);
            }
            else        {       // currentElement is either a refToDiag or a junction
                if( isAJunction( currentElement ) )     {
                    String s = ( (TMLCPRefAD) currentElement ).getName();
                    sb.append( s + ";" + CR );
                }
                else    {
                    sb.append( parseSequence( currentElement ) );
                }
            }
            currentElement = currentElement.getNextElements().get(0);
        }

        return sb.toString() + TAB + "><";
    }

    private String parseSequence( TMLCPElement element )        {

        if( element instanceof TMLCPRefSD )     {
            String sb = ( removeHashKey( ((TMLCPRefSD) element).getName() ) + SEQUENCE_OP + SP );
            return sb;
        }
        if( element instanceof TMLCPRefAD )     {
            String sb = ( removeHashKey( ((TMLCPRefAD) element).getName() ) + SEQUENCE_OP + SP );
            return sb;
        }
        return "";
    }

    private TMLCPElement parseFork( ArrayList<TMLCPElement> elements )  {

        TMLCPElement nextElement = null;

        sbFork.append( SP + "{" );
        for( TMLCPElement currentElement: elements )    {
            nextElement = currentElement;
            sbFork.append( SP + "{" );
            while( !(nextElement instanceof TMLCPJoin) )        {
                if( isAJunction( nextElement ) )        {
                    sbFork.append( "" );        // should raise and error, no junction-choice in fork-join
                }
                else    {
                    sbFork.append( parseSequence( nextElement ) );
                }
                nextElement = nextElement.getNextElements().get(0);     //no nested fork and join
            }
            sbFork = removeTrailingSymbols( sbFork, 2 );
            sbFork.append( "}" + SP + PARALLELISM_OP );
        }
        sbFork = removeTrailingSymbol( sbFork );
        sbFork.append( "}" + SEQUENCE_OP );
        return nextElement;
    }

    private String makeSingleJunctionDiagram( TMLCPActivityDiagram ad ) {

        StringBuffer sb = new StringBuffer( "<>;" + SP );
        TMLCPElement currentElement, nextElement;
        ArrayList<TMLCPElement> nextElements;

        //First parse the part from the junctionto the choice: either a fork or a sequence
        currentElement = getStartState( ad );
        while( !(currentElement instanceof TMLCPChoice) )       {
            nextElements = currentElement.getNextElements();
            if( nextElements.size() > 1 )       {       // currentElement is a fork node
                sbFork = new StringBuffer();
                nextElement = parseFork( nextElements );        // use attribute sbFork
                sb.append( sbFork.toString() );
                sbFork.setLength(0);
            }
            else        {       //it is a simple sequence with no nested junctions
                nextElement = nextElements.get(0);
                sb.append( parseSequence( nextElement ) );
            }
            currentElement = nextElement;
        }
        sb = removeTrailingSymbol( sb );

        sb.append( parseChoice( currentElement, ad ) );
        return sb.toString();
    }

    private String parseChoice( TMLCPElement currentElement, TMLCPActivityDiagram ad )  {

        StringBuffer sb = new StringBuffer( /*CR + TAB + "LOOP" + SP + ad.getName()*/ );
        //this LOOP is the keywork that is used to look for the junction diagram name, removing it, causing the generation not to work
        ArrayList<TMLCPElement> nextElements;
        int index = 0;
        ArrayList<TMLCPElement> branches = currentElement.getNextElements();
        ArrayList<String> guards = ( (TMLCPChoice)currentElement ).getGuards();

        for( TMLCPElement element: branches )   {       //for each of the branches go until a stop or a junction, only possible to find seq/fork
            sb.append( CR + TAB2 + guards.get(index) + SP );
            while( !(element instanceof TMLCPStop) )    {
                nextElements = element.getNextElements();
                if( nextElements.size() > 1 )   {       // currentElement is a fork node
                    sbFork = new StringBuffer();
                    element = parseFork( nextElements ).getNextElements().get(0);       // use attribute sbFork - element is the element after the join node
                    sb.append( sbFork.toString() + ";" );
                    sbFork.setLength(0);
                }
                else    {       //it is a simple sequence with no nested junctions, use element
                    if( isAJunction( element ) )        {
                        String s = ( (TMLCPRefAD) element ).getName();
                        sb = removeTrailingSymbol( sb );
                        if( s.equals( ad.getName() ) )  {
                            sb.append( SP + s + ";" + SP + "><" );      // it is a reference to the same junction-choice block
                        }
                        else    {
                            sb.append( SP + s + ";" + SP + "><" );      //it is a reference to another junction-choice block
                        }
                        break;
                    }
                    else        {
                        sb.append( parseSequence( element ) );
                    }
                    element = element.getNextElements().get(0);
                    if( element instanceof TMLCPStop )  {
                        sb = removeTrailingSymbol( sb );
                        sb.append( TAB + "><" );
                    }
                }
            }   // end of while
            index++;
        }       // end of for
        sb.append( CR );
        return sb.toString();
    }

    private boolean isAJunction( TMLCPElement element ) {

        if( element instanceof TMLCPRefAD )     {
            String s = ( (TMLCPRefAD) element ).getName();
            if( s.length() >= 9 )       {
                if( s.substring( 0,8 ).equals( "junction" ) )   {       //it is a reference to a junction diagram
                    return true;
                }
            }
        }
        return false;
    }

    // Retrieves the AD corresponding to a junction from the list of junction diagrams
    private TMLCPActivityDiagram getJunctionDiagram( String name, ArrayList<TMLCPActivityDiagram> junctionDiagList )    {

        for( TMLCPActivityDiagram diag: junctionDiagList )      {
            if( diag.getName().equals( name ) ) {
                return diag;
            }
        }
        return junctionDiagList.get(0);
    }

    private StringBuffer removeTrailingSymbol( StringBuffer sb )        {

        if( sb.length() > 0 )   {
            sb.setLength( sb.length() - 1 );
            return sb;
        }
        return sb;
    }

    private StringBuffer removeTrailingSymbols( StringBuffer sb, int n )        {

        if( sb.length() > 0 )   {
            sb.setLength( sb.length() - n );
            return sb;
        }
        return sb;
    }

    private TMLCPElement getStartState( TMLCPActivityDiagram ad )       {

        ArrayList<TMLCPElement> elementsList = ad.getElements();
        for( TMLCPElement elem: elementsList )  {
            if( elem instanceof TMLCPStart )    {
                return elem;
            }
        }
        return null;
    }

    private String removeHashKey( String s )    {

        String[] vector = s.split( "_#" );
        return vector[0];
    }
    //  public String makeDeclarations( TMLCP tmlcp ) {
    //
    //          ArrayList<TMLCPElement> listElements;
    //          currentElem = "";
    //          nextElem = "";
    //          String newSb = "";
    //          String sb = "";
    //          sb += "// TML Communication Pattern - FORMAT 0.1" + CR;
    //          sb += "// Communication Pattern: " + title + CR;
    //          sb += "// Generated: " + new Date().toString() + CR2;
    //
    //          //Generating code for the main CP
    //          TMLCPActivityDiagram mainCP = tmlcp.getMainCP();
    //          sb += "COMMUNICATION_PATTERN" + SP + mainCP.getName() + CR + TAB;
    //          listElements = mainCP.getElements();
    //          for( TMLCPElement elem : listElements ) {
    //                  if( elem instanceof tmltranslator.tmlcp.TMLCPRefAD )    {
    //                          tmltranslator.tmlcp.TMLCPRefAD refAD = (tmltranslator.tmlcp.TMLCPRefAD) elem;
    //                          sb += "ACTIVITY" + SP + refAD.getName() + CR + TAB;
    //                  }
    //                  if( elem instanceof tmltranslator.tmlcp.TMLCPRefSD )    {
    //                          tmltranslator.tmlcp.TMLCPRefSD refSD = (tmltranslator.tmlcp.TMLCPRefSD) elem;
    //                          sb += "SEQUENCE" + SP + refSD.getName() + CR + TAB;
    //                  }
    //                  if( elem instanceof TMLCPConnector )    {
    //                          TMLCPConnector connector = (TMLCPConnector) elem;
    //                          listTMLCPConnectors.add( connector );
    //                  }
    //          }
    //          //global variables should go here, but there are none up to now
    //          sb += CR + TAB + MAIN + CR + TAB2 + "<>" + SC + SP;
    //          //Yet to add: nested forks, choices and junctions
    //          currentElem = START;
    //          boolean encounteredSequence = false;
    //          boolean endOfGraph = false;
    //          boolean comingFromMain = true;  //flags to trace the history of the sub-routine calls
    //          boolean comingFromChoice = false;
    //          boolean comingFromFJ = false;
    //          while( !endOfGraph )    {
    //                  nextElem = getNextElem();       //get the name of the next element
    //                  String token = nextElem;
    //                  if( token.length() > 3 )        {
    //                          token = token.substring(0,4);
    //                  }
    //                  switch( token ) {
    //                          case "fork":    {       //The firts encountered object is a fork: go until join. Return the connector that follows the join
    //                                  sb += "{" + exploreForkJoin( comingFromMain, comingFromChoice, comingFromFJ );
    //                                  if( !nextElem.equals(STOP) )    {
    //                                          String temp = sb.substring(0,sb.length()-3) + "}" + SEQUENCE_OP + SP + nextElem + SEQUENCE_OP;
    //                                          sb = temp;
    //                                  }
    //                                  else    {
    //                                          endOfGraph = true;
    //                                          String temp = sb.substring(0,sb.length()-3) + "}" + SP + "><";
    //                                          sb = temp;
    //                                  }
    //                                  encounteredSequence = false;
    //                                  break;
    //                          }
    //                          case "junc":    {       //The first encountered object is a junction: count the loop
    //                                  if( nextElem.length() >=8 )     {
    //                                          if( nextElem.substring(0,8).equals("junction") )        {       //ensure the name starts with junction
    //                                                  currentJunc = nextElem;
    //                                                  sb += CR + TAB2 + LOOP + loopCounter + CR + TAB2;
    //                                                  }
    //                                          }       //else I should trigger an error
    //                                  encounteredSequence = false;
    //                          break;
    //                          }
    //                          case "choi":    { //The first encountered object is a choice: exploration of the graph will be over upon return
    //                                  if( nextElem.length() >= 6 )    {
    //                                          if( nextElem.substring(0,6).equals("choice") )  {       //ensure the name starts with choice
    //                                                  comingFromMain = true;
    //                                                  comingFromChoice = false;
    //                                                  comingFromFJ = false;
    //                                                  if( encounteredSequence )       {
    //                                                          sb += CR + TAB2;        //When there is a sequence of diags before the choice node
    //                                                  }
    //                                                  sb += CR + TAB2 + exploreChoiceBranches( comingFromMain, comingFromChoice, comingFromFJ );
    //                                                  String temp = sb.substring(0, sb.length()-1) + SP + "><";       //drop semicolon
    //                                                  sb = temp;
    //                                                  endOfGraph = true;
    //                                          }
    //                                  }       //else I should trigger an error
    //                                  encounteredSequence = false;
    //                          break;
    //                          }
    //                          default:        {       //The first encountered object is a diagram reference name: a sequence
    //                                  sb += nextElem + SEQUENCE_OP;
    //                                  encounteredSequence = true;
    //                          break;
    //                          }
    //                  }       //End of switch
    //                  if( nextElem.equals( "ERROR" ) )        {
    //                          TraceManager.addDev( "ERROR WHEN GENERATING TML CODE in mainCP: " + nextElem);
    //                          endOfGraph = true;
    //                          break;
    //                  }
    //                  if( nextElem.equals(STOP) )     {
    //                          String temp = sb.substring(0,sb.length()-12) + SP + "><";
    //                          sb = temp;
    //                          break;
    //                  }
    //                  currentElem = nextElem; //advance to next connector
    //          }       //End of while loop
    //          sb += CR + TAB + END + CR + END + CR2;
    //
    //          /***************************************************************************/
    //          //Generating code for the other ADs
    //          ArrayList<TMLCPActivityDiagram> listADs = tmlcp.getCPActivityDiagrams();
    //          for( TMLCPActivityDiagram AD: listADs ) {
    //                  TraceManager.addDev( "GENERATING THE CODE FOR THE AD " + AD.getName() );
    //                  sb += "START ACTIVITY" + SP + AD.getName() + CR + TAB;
    //                  listTMLCPConnectors.clear();
    //                  listElements = AD.getElements();
    //                  for( TMLCPElement elem : listElements ) {
    //                          if( elem instanceof tmltranslator.tmlcp.TMLCPRefAD )    {
    //                                  tmltranslator.tmlcp.TMLCPRefAD refAD = (tmltranslator.tmlcp.TMLCPRefAD) elem;
    //                                  sb += "ACTIVITY" + SP + refAD.getName() + CR + TAB;
    //                          }
    //                          if( elem instanceof tmltranslator.tmlcp.TMLCPRefSD )    {
    //                                  tmltranslator.tmlcp.TMLCPRefSD refSD = (tmltranslator.tmlcp.TMLCPRefSD) elem;
    //                                  sb += "SEQUENCE" + SP + refSD.getName() + CR + TAB;
    //                          }
    //                          if( elem instanceof TMLCPConnector )    {
    //                                  TMLCPConnector connector = (TMLCPConnector) elem;
    //                                  listTMLCPConnectors.add( connector );
    //                                  //TraceManager.addDev( connector.getStartName() + SP + connector.getEndName() + SP + connector.getGuard() );
    //                          }
    //                  }
    //                  //global variables should go here, but there are none up to now
    //                  sb += CR2 + TAB + MAIN + CR + TAB2 + "<>" + SC + " ";   //should I start with an open parenthesis?
    //                  //up to know I just consider sequence, activities, fork, join, choice but no nested structures to keep things simple!
    //                  currentElem = START;
    //                  endOfGraph = false;
    //                  comingFromMain = true;  //flags to trace the history of the sub-routine calls
    //                  comingFromChoice = false;
    //                  comingFromFJ = false;
    //                  loopCounter = 0;
    //                  encounteredSequence = false;
    //                  while( !endOfGraph )    {
    //                          nextElem = getNextElem();       //get the name of the next element
    //                          String token = nextElem;
    //                          if( token.length() > 3 )        {
    //                                  token = token.substring(0,4);
    //                          }
    //                          switch( token ) {
    //                                  case "fork":    {       //The firts encountered object is a fork: go until join. Return the connector that follows the join
    //                                          sb += "{" + exploreForkJoin( comingFromMain, comingFromChoice, comingFromFJ );
    //                                          if( !nextElem.equals(STOP) )    {
    //                                                  String temp = sb.substring(0,sb.length()-3) + "}" + SEQUENCE_OP + SP + nextElem + SEQUENCE_OP;
    //                                                  sb = temp;
    //                                          }
    //                                          else    {
    //                                                  endOfGraph = true;
    //                                                  String temp = sb.substring(0,sb.length()-3) + "}" + SP + "><";
    //                                                  sb = temp;
    //                                          }
    //                                          encounteredSequence = false;
    //                                  break;
    //                                  }
    //                                  case "junc":    {       //The first encountered object is a junction: count the loop
    //                                          if( nextElem.length() >=8 )     {
    //                                                  if( nextElem.substring(0,8).equals("junction") )        {       //ensure the name starts with junction
    //                                                          currentJunc = nextElem;
    //                                                          junctionList.add( nextElem );
    //                                                          sb += CR + TAB2 + LOOP + loopCounter + CR + TAB2;
    //                                                          //loopCounter += 1;
    //                                                          }
    //                                                  }       //else I should trigger an error
    //                                          encounteredSequence = false;
    //                                  break;
    //                                  }
    //                                  case "choi":    { //The first encountered object is a choice: exploration of the graph will be over upon return
    //                                          if( nextElem.length() >= 6 )    {
    //                                                  if( nextElem.substring(0,6).equals("choice") )  {       //ensure the name starts with choice
    //                                                          comingFromMain = true;
    //                                                          comingFromChoice = false;
    //                                                          comingFromFJ = false;
    //                                                          if( encounteredSequence )       {
    //                                                                  sb += CR + TAB2;
    //                                                          }
    //                                                          sb += exploreChoiceBranches( comingFromMain, comingFromChoice, comingFromFJ );
    //                                                          String temp = sb.substring(0, sb.length()-1) + SP + "><";       //drop semicolon
    //                                                          sb = temp;
    //                                                          currentJunc = "";
    //                                                          endOfGraph = true;
    //                                                  }
    //                                          }       //else I should trigger an error
    //                                          encounteredSequence = false;
    //                                  break;
    //                                  }
    //                                  default:        {       //The first encountered object is a diagram reference name: a sequence
    //                                          sb += nextElem + SEQUENCE_OP;
    //                                          encounteredSequence = true;
    //                                  break;
    //                                  }
    //                          }       //End of switch
    //                          if( nextElem.equals( "ERROR"  ) )       {
    //                                  TraceManager.addDev( "ERROR WHEN GENERATING TML CODE in otherADs: " + nextElem );
    //                          break;
    //                          }
    //                          currentElem = nextElem; //advance to next connector
    //                  }       //End of while loop
    //                  if( comingFromFJ )      {
    //                          newSb = sb.substring( 0, sb.length()-2 );       // drop last PARALLELISM_OP
    //                  }
    //                  sb += CR + TAB + END + CR + END + CR2;
    //
    //          } //End of Activity Diagram analysis
    //
    //          /***************************************************************************/
    //          //Generating code for Sequence Diagrams
    //          ArrayList<TMLCPSequenceDiagram> listSDs = tmlcp.getCPSequenceDiagrams();
    //          for( int i = 0; i < listSDs.size(); i++ )       {
    //                  TMLCPSequenceDiagram SD = listSDs.get(i);
    //                  sb += "SEQUENCE " + SD.getName() + CR + TAB;
    //                  ArrayList<tmltranslator.tmlcp.TMLSDInstance> listInstances = SD.getInstances();
    //                  ArrayList<TMLSDMessage> listMessages = SD.getMessages();
    //                  ArrayList<TMLSDAction> listActions = SD.getActions();
    //                  ArrayList<TMLAttribute> listAttributes = SD.getAttributes();
    //                  for( tmltranslator.tmlcp.TMLSDInstance inst: listInstances )    {
    //                          sb += inst.getType() + " " + inst.getName() + CR + TAB;
    //                  }
    //                  for( TMLAttribute attr: listAttributes )        {
    //                          sb += attr.getType().toString().toUpperCase() + " " + attr.getInstanceName() + "." + attr.getName() + CR + TAB;
    //                  }
    //                  for( TMLAttribute attr: listAttributes )        {
    //                          if( attr.isBool() )     {
    //                                  sb += attr.getInstanceName() + "." + attr.getName() + " = " + attr.getInitialValue().toUpperCase() + CR + TAB;
    //                          }
    //                          else    {
    //                                  sb += attr.getInstanceName() + "." + attr.getName() + " = " + attr.getInitialValue() + CR + TAB;
    //                          }
    //                  }
    //                  String temp = "";//sb.substring( 0, sb.length()-3 );    //Remove trailing CR + TAB + TAB
    //                  sb += CR + TAB + MAIN + CR + TAB + TAB;
    //                  ArrayList<TMLSDEvent> listEvents = SD.getEvents();
    //                  Collections.sort( listEvents );                         //actions and messages must be ordered and printed according to Y before being written!
    //                  /*TraceManager.addDev( "PRINTING SORTED ITEMS" );
    //                  for( TMLSDEvent item: listEvents )      {       //print the items
    //                          TraceManager.addDev( item.toString() );
    //                  }*/
    //                  for( TMLSDEvent item: listEvents )      {
    //                          if( item.getInstanceName().length() > 0 )       {       //the item is an action (attribute)
    //                                  sb += item.getInstanceName() + "." + item.getName();
    //                                  sb += CR + TAB + TAB;
    //                          }
    //                          else    {       //The item is a message
    //                                  ArrayList<TMLSDAttribute> listAttr = item.getAttributes();
    //                                  if( listAttr.size() == 0 )      {       //message with no parameters
    //                                          sb += item.getSenderName() + "." + item.getName() + "(" + item.getReceiverName() + ")";
    //                                  }
    //                                  else    {       //message with at least one parameter
    //                                          sb += item.getSenderName() + "." + item.getName() + "(" + item.getReceiverName() + ",";
    //                                          for( int p = 0; p < listAttr.size(); p++ )      {
    //                                                  if( p == (listAttr.size() - 1) )        {
    //                                                          sb += listAttr.get(p).getName() + ")";
    //                                                  }
    //                                                  else    {
    //                                                          sb += listAttr.get(p).getName() + ",";
    //                                                  }
    //                                          }
    //                                  }
    //                                  sb += CR + TAB + TAB;
    //                          }
    //                  }
    //                  temp = sb.substring( 0, sb.length()-1 );        //Remove trailing TAB
    //                  sb = temp + END + CR + END + CR2;
    //          }
    //          return sb;
    //  }
    //
    //  //Does not work if there are nested fork-joins
    //  private String exploreForkJoin( boolean comingFromMain, boolean comingFromChoice, boolean comingFromFJ )        {
    //
    //          String token, branchSb, globalSb = "";
    //          boolean exit;
    //          boolean encounteredSequence = false;
    //          boolean fromMain = comingFromMain;
    //          boolean fromChoice = comingFromChoice;
    //          boolean fromFJ = comingFromFJ;
    //          ArrayList<TMLCPConnector> connToFork = new ArrayList<TMLCPConnector>();
    //          TMLCPConnector conn;
    //          int i;
    //          ArrayList<String> diagsToJoin = new ArrayList<String>();
    //
    //          //Retrieve all connectors starting from the fork node of nextElem
    //          for( i = 0; i < listTMLCPConnectors.size(); i++ )       {
    //                  conn = listTMLCPConnectors.get(i);
    //                  if( conn.getStartName().equals( nextElem ) )    {
    //                          connToFork.add( conn );
    //                  }
    //          }
    //          for( int j = 0; j < connToFork.size(); j++ )    {       //For each output connector of the start fork node
    //                  currentElem = connToFork.get(j).getEndName();
    //                  nextElem = currentElem;
    //                  branchSb = "{";
    //                  exit = false;
    //                  encounteredSequence = false;
    //                  while( !exit )  {
    //                          token = nextElem;
    //                          if( token.length() > 3 )        {
    //                                  token = token.substring(0,4);
    //                          }
    //                          switch( token ) {
    //                                  case "fork":    {       //ERROR: no nested forks
    //                                          TraceManager.addDev( "ERROR: ENCOUNTERED NESTED FORK!" );
    //                                  break;
    //                                  }
    //                                  case "join":    {       //End of branch, regardless if coming from main or choice exploration
    //                                          if( encounteredSequence )       {
    //                                                  String temp = branchSb.substring(0,branchSb.length()-2);
    //                                                  branchSb = temp;
    //                                          }
    //                                          encounteredSequence = false;
    //                                          exit = true;
    //                                  break;
    //                                  }
    //                                  case "junc":    {       //register the junction and go on
    //                                          if( nextElem.length() >=8 )     {
    //                                                  if( nextElem.substring(0,8).equals("junction") )        {       //ensure the name starts with junction
    //                                                          if( fromChoice )        {       //ERROR: no nested choices
    //                                                                  TraceManager.addDev( "ERROR: ENCOUNTERED JUNCTION IN FORK WHILE COMING FROM CHOICE!" );
    //                                                                  exit = true;
    //                                                          }
    //                                                          else    {
    //                                                                  branchSb += CR + TAB2 + LOOP + loopCounter + CR + TAB2;
    //                                                          }
    //                                                  }
    //                                          }       //else I should trigger an error
    //                                          encounteredSequence = false;
    //                                  break;
    //                                  }
    //                                  case "choi":    {       //Encountered a choice within a fork-join
    //                                          if( nextElem.length() >= 6 )    {
    //                                                  if( nextElem.substring(0,6).equals("choice") )  {       //ensure the name starts with choice
    //                                                          if( fromChoice )        {
    //                                                                  TraceManager.addDev( "ERROR: ENCOUNTERED NESTED FORK!" );
    //                                                                  exit = true;
    //                                                          }
    //                                                          else    {
    //                                                                  fromMain = false;
    //                                                                  fromChoice = false;
    //                                                                  fromFJ = true;
    //                                                                  branchSb += exploreChoiceBranches( fromMain, fromChoice, fromFJ ); //The exploration will return when encountering a join node
    //                                                                  loopCounter += 1;
    //                                                                  exit = true;
    //                                                          }
    //                                                  }
    //                                          }
    //                                          encounteredSequence = false;
    //                                  break;
    //                                  }
    //                                  case "stop":    {       //ERROR: fork must always terminate with a join
    //                                          TraceManager.addDev( "ERROR: ENCOUNTERED STOP BEFORE JOIN!" );
    //                                          exit = true;
    //                                  break;
    //                                  }
    //                                  default:        {       //Found a sequence/activity diagram, continue to explore
    //                                          //TraceManager.addDev("IN THE DEFAULT BRANCH WITH " + currentElem);
    //                                          branchSb += nextElem + SEQUENCE_OP + SP;
    //                                          encounteredSequence = true;
    //                                  break;
    //                                  }
    //                          }       //End of switch
    //                          if( nextElem.equals( "ERROR" ) )        {
    //                                  TraceManager.addDev( "ERROR WHEN GENERATING TML CODE in exploreForkJoin: " + nextElem );
    //                                  exit = true;
    //                          break;
    //                          }
    //                          currentElem = nextElem; // advance to next connector
    //                          nextElem = getNextElem();
    //                  }       //End of while
    //                  if( branchSb.charAt(branchSb.length()-1) == ';' )       {       //Remove trailing semi-colons
    //                          String temp = branchSb.substring( 0, branchSb.length()-1);
    //                          branchSb = temp;
    //                  }
    //                  globalSb += branchSb + "}" + SP + PARALLELISM_OP + SP;
    //          }       //End of for, end of exploration of all branches
    //          if( fromChoice )        {       //Remove trailing semi-colons
    //                  String temp = globalSb.substring( 0, globalSb.length() - 3 );
    //                  globalSb = temp;
    //          }
    //          return globalSb;
    //  }
    //
    //  private String exploreChoiceBranches( boolean comingFromMain, boolean comingFromChoice, boolean comingFromFJ )  {
    //
    //          boolean fromMain = comingFromMain;
    //          boolean fromChoice = comingFromChoice;
    //          boolean fromFJ = comingFromFJ;
    //          boolean exit = false;
    //          int i;
    //          String token, branchSb, currentElemToPass = "", nextElemToPass = "", globalSb = "";
    //          ArrayList<TMLCPConnector> connToChoice = new ArrayList<TMLCPConnector>();
    //          ArrayList<String> diagsToJoin = new ArrayList<String>();
    //          TMLCPConnector conn;
    //
    //          //Retrieve all connectors starting from the choice node of nextElem
    //          for( i = 0; i < listTMLCPConnectors.size(); i++ )       {
    //                  conn = listTMLCPConnectors.get(i);
    //                  if( conn.getStartName().equals( nextElem ) )    {
    //                          connToChoice.add( conn );
    //                  }
    //          }
    //          for( int j = 0; j < connToChoice.size(); j++ )  {
    //                  currentElem = connToChoice.get(j).getEndName();
    //                  nextElem = currentElem;
    //                  branchSb = TAB + connToChoice.get(j).getGuard() + "{";
    //                  exit = false;
    //                  while( !exit )  {
    //                          token = nextElem;
    //                          if( token.length() > 3 )        {
    //                                  token = token.substring(0,4);
    //                          }
    //                          switch( token ) {
    //                                  case "fork":    {       //Encountered a fork on a choice branch
    //                                          fromMain = false;
    //                                          fromFJ = false;
    //                                          fromChoice = true;
    //                                          branchSb += exploreForkJoin( fromMain, fromChoice, fromFJ );    //Explore until the closing join node
    //                                  break;
    //                                  }
    //                                  case "join":    {       //Condition for exiting the branch
    //                                          if( fromMain )  {
    //                                                  TraceManager.addDev( "ERROR: ENCOUNTERED JOIN IN CHOICE WHILE COMING FROM MAIN!" );
    //                                                  exit = true;
    //                                          }
    //                                          else if( fromFJ )       {
    //                                                  currentElemToPass = currentElem;
    //                                                  nextElemToPass = nextElem;
    //                                                  String temp = branchSb.substring( 0, branchSb.length() - 2 );   //Remove trailing semicolon
    //                                                  branchSb = temp + "}" + SC + SP + ENDLOOP + loopCounter + CR + TAB;
    //                                                  exit = true;
    //                                          }
    //                                  break;
    //                                  }
    //                                  case "choi":    {       //not possible up to now to have nested choices
    //                                          if( nextElem.length() >= 6 )    {
    //                                                  if( nextElem.substring(0,6).equals("choice") )  {       //ensure the name starts with choice
    //                                                          TraceManager.addDev( "ENCOUNTERED NESTED CHOICE WHILE COMING FROM CHOICE!" );
    //                                                          exit = true;
    //                                                  }
    //                                          }
    //                                  break;
    //                                  }
    //                                  case "junc":    {       //Terminate branch exploration
    //                                          if( nextElem.length() >=8 )     {
    //                                                  if( nextElem.substring(0,8).equals("junction") )        {       //ensure the name starts with junction
    //                                                          String temp = branchSb.substring( 0, branchSb.length() - 2 );   //Remove trailing semicolon
    //                                                          branchSb = temp + "}" + SC + SP + LOOP + loopCounter + CR + TAB2;
    //                                                          exit = true;
    //                                                  }
    //                                          }
    //                                  break;
    //                                  }
    //                                  case "stop":    {       //Condition for exiting the branch
    //                                          if( fromFJ )    {
    //                                                  TraceManager.addDev( "ERROR: ENCOUNTERED NESTED CHOICE WHILE COMING FROM FJ!" );
    //                                                  exit = true;
    //                                          }
    //                                          else if( fromMain )     {
    //                                                  String temp = branchSb.substring( 0, branchSb.length() - 2 );   //Remove trailing semicolon
    //                                                  branchSb = temp + "}" + SC + SP + ENDLOOP + loopCounter + CR + TAB2;
    //                                                  exit = true;
    //                                          }
    //                                  break;
    //                                  }
    //                                  default:        {       //Found a reference to a sequence/activity diagram: sequence operator
    //                                          branchSb += nextElem + SEQUENCE_OP + SP;
    //                                  break;
    //                                  }
    //                          }       //End of switch
    //                          if( nextElem.equals( "ERROR" ) )        {
    //                                  TraceManager.addDev( "ERROR WHEN GENERATING TML CODE in exploreChoiceBranches: " + nextElem );
    //                                  exit = true;
    //                          }
    //                          currentElem = nextElem;         //advance to next connector
    //                          nextElem = getNextElem();
    //                  }       //End of while
    //                  globalSb += branchSb;
    //          }       //End of for, end of exploration of all branches
    //          if( fromFJ )    {       //Restore correct elements when called from a fork-join
    //                  currentElem = currentElemToPass;
    //                  nextElem = nextElemToPass;
    //          }
    //          return globalSb + ENDLOOP + loopCounter + SP;
    //  }

    //Look for a connector that starts from currentElem and get the endName
    private String getNextElem()        {

        TMLCPConnector conn;
        String endName = "";
        int i;
        for( i = 0; i < listTMLCPConnectors.size(); i++ )       {
            conn = listTMLCPConnectors.get(i);
            if( conn.getStartName().equals( currentElem ) )     {
                endName = conn.getEndName();
                break;
            }
        }
        return endName;
    }

    /*  public String makeTasks(TMLModeling tmlm) {
        String sb = "";
        for(TMLTask task: tmlm.getTasks()) {
        sb += "TASK" + SP + task.getName() + CR;
        sb += makeActivity(task);
        sb += "ENDTASK" + CR2;
        }
        return sb;
        }

        public String makeActivity(TMLTask task) {
        String sb = "";
        sb += "//Local variables" + CR;

        for(TMLAttribute attr: task.getAttributes()) {
        sb += TMLType.getStringType(attr.getType().getType()) + SP + attr.getName();
        if ((attr.getInitialValue() != null) && (attr.getInitialValue().length() > 0)){
        sb += " = " + attr.getInitialValue();
        }
        sb += CR;
        }

        sb += CR;
        sb += "//Behavior" + CR;
        sb += makeBehavior(task, task.getActivityDiagram().getFirst());

        return sb;
        }

        public String makeBehavior(TMLTask task, TMLActivityElement elt) {
        String code, code1, code2;
        TMLForLoop tmlfl;
        TMLActivityElementChannel tmlch;
        TMLActivityElementEvent tmlevt;
        TMLSendRequest tmlreq;
        TMLEvent evt;
        TMLRandom random;
        int i;
        String tmp1, tmp2;

        if (elt instanceof TMLStartState) {
        return makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLStopState) {
        return "";

        } else if (elt instanceof TMLExecI) {
        code = "EXECI" + SP + modifyString(((TMLExecI)elt).getAction()) + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLExecIInterval) {
        code = "EXECI" + SP + modifyString(((TMLExecIInterval)elt).getMinDelay()) + SP + modifyString(((TMLExecIInterval)elt).getMaxDelay()) + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLExecC) {
        code = "EXECC" + SP + modifyString(((TMLExecC)elt).getAction()) + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLExecCInterval) {
        code = "EXECC" + SP + modifyString(((TMLExecCInterval)elt).getMinDelay()) + SP + modifyString(((TMLExecCInterval)elt).getMaxDelay()) + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLDelay) {
        tmp1 = ((TMLDelay)elt).getMinDelay();
        tmp2 = ((TMLDelay)elt).getMaxDelay();
        if (tmp1.compareTo(tmp2) == 0) {
        code = "DELAY" + SP + modifyString(((TMLDelay)elt).getMinDelay()) + SP + modifyString(((TMLDelay)elt).getUnit()) + CR;
        } else {
        code = "DELAY" + SP + modifyString(((TMLDelay)elt).getMinDelay()) + SP + modifyString(((TMLDelay)elt).getMaxDelay()) + SP + modifyString(((TMLDelay)elt).getUnit()) + CR;
        }
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLForLoop) {
        tmlfl = (TMLForLoop)elt;
        code = "FOR(" + tmlfl.getInit() + SC + SP;
        code += tmlfl.getCondition() + SC + SP;
        code += tmlfl.getIncrement() + ")" + CR;
        code += makeBehavior(task, elt.getNextElement(0));
        return code + "ENDFOR" + CR + makeBehavior(task, elt.getNextElement(1));

        } else if (elt instanceof TMLRandom) {
        random = (TMLRandom)elt;
        code = "RANDOM" + SP + modifyString(""+random.getFunctionId()) + SP;
        code += modifyString(random.getVariable()) + SP;
        code += modifyString(random.getMinValue()) + SP;
        code += modifyString(random.getMaxValue()) + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLActionState) {
        code = modifyString(((TMLActivityElementWithAction)elt).getAction()) + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLWriteChannel) {
        tmlch = (TMLActivityElementChannel)elt;
        code = "WRITE ";
        for(int k=0; k<tmlch.getNbOfChannels(); k++) {
        code = code + tmlch.getChannel(k).getName() + SP;
        }
        code = code + modifyString(tmlch.getNbOfSamples()) + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLReadChannel) {
        tmlch = (TMLActivityElementChannel)elt;
        code = "READ " + tmlch.getChannel(0).getName() + SP + modifyString(tmlch.getNbOfSamples()) + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLSendEvent) {
        tmlevt = (TMLActivityElementEvent)elt;
        code = "NOTIFY " + tmlevt.getEvent().getName() + " " + tmlevt.getAllParams(" ") + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLWaitEvent) {
        tmlevt = (TMLActivityElementEvent)elt;
        code = "WAIT " + tmlevt.getEvent().getName() + " " + tmlevt.getAllParams(" ") + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLNotifiedEvent) {
        tmlevt = (TMLActivityElementEvent)elt;
        code = "NOTIFIED " + tmlevt.getEvent().getName() + " " + tmlevt.getVariable() + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLSendRequest) {
        tmlreq = (TMLSendRequest)elt;
        code = "REQUEST " + tmlreq.getRequest().getName() + " " + tmlreq.getAllParams(" ") + CR;
        return code + makeBehavior(task, elt.getNextElement(0));

        }  else if (elt instanceof TMLSequence) {
        code = "";
        for(i=0; i<elt.getNbNext(); i++) {
        code += makeBehavior(task, elt.getNextElement(i));
        }

        return code;

        } else if (elt instanceof TMLChoice) {
        TMLChoice choice = (TMLChoice)elt;
        code = "";
        if (choice.getNbGuard() !=0 ) {
        code1 = "";
        int index1 = choice.getElseGuard(), index2 = choice.getAfterGuard();
        int nb = Math.max(choice.nbOfNonDeterministicGuard(), choice.nbOfStochasticGuard());
        if (nb > 0) {
        // Assumed to be a non deterministic choice
        code += "RAND" + CR;
        }
        nb = 0;
        for(i=0; i<choice.getNbGuard(); i++) {
        if (i != index2) {
        if (choice.isNonDeterministicGuard(i)) {
        code2 = "" + (int)(Math.floor(100/choice.getNbGuard()));
        nb ++;
        } else if (choice.isStochasticGuard(i)){
        code2 = prepareString(choice.getStochasticGuard(i));
        nb ++;
        } else {
        code2 = modifyString(choice.getGuard(i));
        code2 = Conversion.replaceAllChar(code2, '[', "(");
        code2 = Conversion.replaceAllChar(code2, ']', ")");
        }
        //TraceManager.addDev("guard = " + code1 + " i=" + i);
        if (nb != 0) {
        //if (choice.isNonDeterministicGuard(i)) {
        //      code = "CASERAND 50";
        //} else {
        //      code = "CASERAND " + prepareString(choice.getStochasticGuard(i));
        //
        //}
        //nb ++;
        if (i != index1) {
        code += "CASERAND " + code2 + CR;
        code += makeBehavior(task, elt.getNextElement(i));
        code += "ENDCASERAND" + CR;
        }
        } else {
        if (i==0) {
        code += "IF " + code2;
        } else {
        if (i != index1) {
        code += "ORIF " + code2;
        } else {
        code += "ELSE";
        }
        }
        code += CR + makeBehavior(task, elt.getNextElement(i));
        }
        }
        }
        if (nb > 0) {
        // Assumed to be a non deterministic choice
        code += "ENDRAND" + CR;
        } else {
        code += "ENDIF" + CR;
        }
        if (index2 != -1) {
        code += makeBehavior(task, elt.getNextElement(index2));
        }
        }
        return code;

        } else if (elt instanceof TMLSelectEvt) {
        code = "SELECTEVT" + CR;
        for(i=0; i<elt.getNbNext(); i++) {
        try {
        tmlevt = (TMLActivityElementEvent)(elt.getNextElement(i));
        code += "CASE ";
        code += tmlevt.getEvent().getName() + " " + tmlevt.getAllParams(" ") + CR;
        code += makeBehavior(task, elt.getNextElement(i).getNextElement(0));
        code += "ENDCASE" + CR;
        } catch (Exception e) {
        TraceManager.addError("Non-event receiving following a select event operator");
        }
        }
        code += "ENDSELECTEVT" + CR;
        return code;

        } else if (elt instanceof TMLRandomSequence) {
        code = "RANDOMSEQ" + CR;
        for(i=0; i<elt.getNbNext(); i++) {
        code += "SEQ" + CR;
        code += makeBehavior(task, elt.getNextElement(i));
        code += "ENDSEQ" + CR;
        }
        code += "ENDRANDOMSEQ" + CR;
        return code;

        } else {
        if (elt == null) {
        return "";
        }
        TraceManager.addDev("Unrecognized element: " + elt);
        return makeBehavior(task, elt.getNextElement(0));
        }
        }

        public boolean makeTMLModeling(String _spec) {
        spec = _spec;
        tmlm = new TMLModeling();
        errors = new ArrayList<TMLTXTError>();
        warnings = new ArrayList<TMLTXTError>();

        spec = Conversion.removeComments(spec);
        //TraceManager.addDev(spec);
        browseCode();

        return (errors.size() == 0);
        }
    */
    public String printErrors() {
        String ret = "";
        for(TMLTXTError error: errors) {
            ret += "ERROR at line " + error.lineNb + ": " + error.message + CR;
            try {
                ret += "->" + spec.split("\n")[error.lineNb] + CR2;
            } catch (Exception e) {
                ret += "(Code line not accessible)" + CR;
            }
        }
        return ret;
    }

    public String printWarnings() {
        String ret = "";
        for(TMLTXTError error: warnings) {
            ret += "WARNING at line " + error.lineNb + CR;
            ret += error.message + CR;
        }
        return ret;
    }

    public String printSummary() {
        String ret = "";
        if (errors.size() == 0) {
            ret += printWarnings();
            ret += "Compilation successful" + CR;
            ret += "No error, " + warnings.size() + " warning(s)" + CR;
        } else {
            ret += printErrors() + CR + printWarnings() + CR;
            ret += "Compilation failed" + CR;
            ret += errors.size() + " error(s), "+ warnings.size() + " warning(s)" + CR;
        }

        return ret;
    }

    //  public void browseCode() {
    //          // Browse lines of code one after the other
    //          // Build accordinlgy the TMLModeling and updates errors and warnings
    //          // In case of fatal error, immedialty quit code bowsing
    //
    //          StringReader sr = new StringReader(spec);
    //        BufferedReader br = new BufferedReader(sr);
    //        String s;
    //          String s1;
    //          String [] split;
    //          int lineNb = 0;
    //
    //          inDec = true;
    //          inTask = false;
    //          inTaskDec = false;
    //          inTaskBehavior = false;
    //
    //
    //          String instruction;
    //
    //          parses = new ArrayList<TMLParserSaveElt>();
    //
    //        try {
    //            while((s = br.readLine()) != null) {
    //                          if (s != null) {
    //                                  s = s.trim();
    //                                  //TraceManager.addDev("s=" + s);
    //                                  s = removeUndesiredWhiteSpaces(s, lineNb);
    //                                  s1 = Conversion.replaceAllString(s, "\t", " ");
    //                                  s1 = Conversion.replaceRecursiveAllString(s1, "  ", " ");
    //                                  //TraceManager.addDev("s1=" + s1);
    //                                  if (s1 != null) {
    //                                          split = s1.split("\\s");
    //                                          if (split.length > 0) {
    //                                                  //TraceManager.addDev("analyse");
    //                                                  analyseInstruction(s, lineNb, split);
    //                                                  //TraceManager.addDev("end analyse");
    //                                          }
    //                                  }
    //
    //                                  lineNb++;
    //                          }
    //            }
    //        } catch (Exception e) {
    //            TraceManager.addError("Exception when reading specification: " + e.getMessage());
    //                  addError(0, lineNb, 0, "Exception when reading specification");
    //        }
    //  }
    //
    //  public void addError(int _type, int _lineNb, int _charNb, String _msg) {
    //          TMLTXTError error = new TMLTXTError(_type);
    //          error.lineNb = _lineNb;
    //          error.charNb = _charNb;
    //          error.message = _msg;
    //          errors.add(error);
    //  }
    //
    //  public int analyseInstruction(String _line, int _lineNb, String[] _split) {
    //          String error;
    //          String params;
    //          String id;
    //          TMLChannel ch;
    //          TMLEvent evt;
    //          TMLRequest request;
    //          TMLTask t1, t2;
    //          TMLAttribute attribute;
    //          TMLType type;
    //          TMLStopState stop;
    //          TMLRandom random;
    //          int tmp, tmp0, tmp1, i;
    //          int dec = 0;
    //          boolean blocking;
    //          TMLParserSaveElt parseElt;
    //
    //          //TraceManager.addDev("Analyzing instruction:" + _line);
    //
    //          if (parses.size() > 0) {
    //                  parseElt = parses.get(0);
    //                  if ((parseElt.type == TMLParserSaveElt.SELECTEVT) && ((!isInstruction("CASE", _split[0]) && (!isInstruction("ENDSELECTEVT", _split[0]))))) {
    //                          error = "CASE or ENDSELECTEVT instruction expected";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  if ((parseElt.type == TMLParserSaveElt.RAND) && ((!isInstruction("CASERAND", _split[0]) && (!isInstruction("ENDRAND", _split[0]))))) {
    //                          error = "CASERAND or ENDRAND instruction expected";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //          }
    //
    //          // CHANNEL
    //          if(isInstruction("CHANNEL", _split[0])) {
    //                  if (!inDec) {
    //                          error = "A channel may not be declared in a non-declaration part of a TML specification";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!((_split.length > 5) && (_split.length < 8))) {
    //                          error = "A channel must be declared with only 5 or 6 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (_split.length == 7) {
    //                          dec = 1;
    //                  } else {
    //                          dec = 0;
    //                  }
    //
    //                  if (!checkParameter("CHANNEL", _split, 1, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("CHANNEL", _split, 2, 2, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("CHANNEL", _split, 3, 1, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (_split.length == 7) {
    //                          if (!checkParameter("CHANNEL", _split, 4, 1, _lineNb)) {
    //                                  return -1;
    //                          }
    //                  }
    //
    //
    //                  if (!checkParameter("CHANNEL", _split, 4 + dec, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("CHANNEL", _split, 5 + dec, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (tmlm.getChannelByName(_split[1]) != null) {
    //                          error = "Duplicate definition of channel " + _split[1];
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (_split[2].toUpperCase().compareTo("NIB") == 0) {
    //
    //                  }
    //
    //                  ch = new TMLChannel(_split[1], null);
    //                  ch.setTypeByName(_split[2]);
    //                  try {
    //                          tmp = Integer.decode(_split[3]).intValue();
    //                          } catch (Exception e) {tmp = 4;}
    //                          ch.setSize(tmp);
    //
    //                          if (_split.length == 7) {
    //                                  try {
    //                                          tmp = Integer.decode(_split[4]).intValue();
    //                                  } catch (Exception e) {tmp = 8;}
    //                                  //TraceManager.addDev("Setting max to" + tmp);
    //                                  ch.setMax(tmp);
    //                          }
    //
    //                          t1 = tmlm.getTMLTaskByName(_split[4+dec]);
    //                          if (t1 == null) {
    //                                  t1 = new TMLTask(_split[4+dec], null, null);
    //                                  //TraceManager.addDev("New task:" + _split[4+dec]);
    //                                  tmlm.addTask(t1);
    //                          }
    //                          t2 = tmlm.getTMLTaskByName(_split[5+dec]);
    //                          if (t2 == null) {
    //                                  t2 = new TMLTask(_split[5+dec], null, null);
    //                                  //TraceManager.addDev("New task:" + _split[5+dec]);
    //                                  tmlm.addTask(t2);
    //                          }
    //                          ch.setTasks(t1, t2);
    //                          tmlm.addChannel(ch);
    //          } // CHANNEL
    //
    //
    //          // LOSSYCHANNEL
    //          if(isInstruction("LOSSYCHANNEL", _split[0])) {
    //                  if (!inDec) {
    //                          error = "A lossychannel may not be declared in a non-declaration part of a TML specification";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!((_split.length > 3) && (_split.length < 5))) {
    //                          error = "A lossychannel must be declared with exactly 3 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //
    //                  if (!checkParameter("LOSSYCHANNEL", _split, 1, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("LOSSYCHANNEL", _split, 2, 1, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("LOSSYCHANNEL", _split, 3, 9, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //
    //                  ch = tmlm.getChannelByName(_split[1]);
    //                  if (ch == null) {
    //                          error = "lossy channel not previsouly declared as a regular channel " + _split[1];
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  try {
    //                          tmp0 = Integer.decode(_split[2]).intValue();
    //                          } catch (Exception e) {tmp0 = 5;}
    //                  try {
    //                          tmp1 = Integer.decode(_split[3]).intValue();
    //                          } catch (Exception e) {tmp1 = -1;}
    //
    //                          ch.setLossy(true, tmp0, tmp1);
    //          } // LOSSYCHANNEL
    //
    //          // EVENT
    //          if(isInstruction("EVENT", _split[0])) {
    //                  if (!inDec) {
    //                          error = "An event may not be declared in a non-declaration part of a TML specification";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!((_split.length > 4) && (_split.length < 7))) {
    //                          error = "An event must be declared with only 4 or 5 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (_split.length == 6) {
    //                          dec = 1;
    //                  } else {
    //                          dec = 0;
    //                  }
    //
    //                  id = getEvtId(_split[1]);
    //                  params = getParams(_split[1]);
    //
    //                  //TraceManager.addDev("Evt id=" + id +  "params=" + params);
    //
    //                  if (!checkParameter("EVENT", _split, 1, 4, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("EVENT", _split, 2, 3, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (_split.length == 6) {
    //                          if (!checkParameter("EVENT", _split, 3, 1, _lineNb)) {
    //                                  return -1;
    //                          }
    //                  }
    //
    //                  if (!checkParameter("EVENT", _split, 3 + dec, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("EVENT", _split, 4 + dec, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (tmlm.getEventByName(id) != null) {
    //                          error = "Duplicate definition of event " + id;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (_split[2].toUpperCase().compareTo("NIB") == 0) {
    //                          blocking = true;
    //                  } else {
    //                          blocking = false;
    //                  }
    //
    //                  if (_split[2].toUpperCase().compareTo("INF") == 0) {
    //                          tmp = -1;
    //                  } else {
    //                          try {
    //                                  tmp = Integer.decode(_split[3]).intValue();
    //                          } catch (Exception e) {
    //                                  error = "Unvalid parameter #3: should be a numeric value";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                  }
    //
    //                  evt = new TMLEvent(id, null, tmp, blocking);
    //                  evt.addParam(params);
    //
    //                  t1 = tmlm.getTMLTaskByName(_split[3+dec]);
    //                  if (t1 == null) {
    //                          t1 = new TMLTask(_split[3+dec], null, null);
    //                          //TraceManager.addDev("New task:" + _split[3+dec]);
    //                          tmlm.addTask(t1);
    //                  }
    //                  t2 = tmlm.getTMLTaskByName(_split[4+dec]);
    //                  if (t2 == null) {
    //                          t2 = new TMLTask(_split[4+dec], null, null);
    //                          //TraceManager.addDev("New task:" + _split[4+dec]);
    //                          tmlm.addTask(t2);
    //                  }
    //                  evt.setTasks(t1, t2);
    //                  tmlm.addEvent(evt);
    //
    //
    //          } // EVENT
    //
    //          // LOSSYEVENT
    //          if(isInstruction("LOSSYEVENT", _split[0])) {
    //                  if (!inDec) {
    //                          error = "A lossyevent may not be declared in a non-declaration part of a TML specification";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!((_split.length > 3) && (_split.length < 5))) {
    //                          error = "A lossyevent must be declared with exactly 3 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //
    //                  if (!checkParameter("LOSSYEVENT", _split, 1, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("LOSSYEVENT", _split, 2, 1, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("LOSSYEVENT", _split, 3, 9, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //
    //                  evt = tmlm.getEventByName(_split[1]);
    //                  if (evt == null) {
    //                          error = "lossyevent not previsouly declared as a regular event " + _split[1];
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  try {
    //                          tmp0 = Integer.decode(_split[2]).intValue();
    //                          } catch (Exception e) {tmp0 = 5;}
    //                  try {
    //                          tmp1 = Integer.decode(_split[3]).intValue();
    //                          } catch (Exception e) {tmp1 = -1;}
    //
    //                          evt.setLossy(true, tmp0, tmp1);
    //          } // LOSSYEVENT
    //
    //          // REQUEST
    //          if((isInstruction("REQUEST", _split[0])) && (inDec)) {
    //                  if (!inDec) {
    //                          error = "A request may not be declared in a non-declaration part of a TML specification";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (_split.length < 4) {
    //                          error = "A request must be declared with at least 4 paremeters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //
    //                  id = getEvtId(_split[1]);
    //                  params = getParams(_split[1]);
    //
    //                  //TraceManager.addDev("Evt id=" + id +  "params=" + params);
    //
    //                  if (!checkParameter("REQUEST", _split, 1, 4, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  for(i=2; i<_split.length; i++) {
    //                          if (!checkParameter("REQUEST", _split, i, 0, _lineNb)) {
    //                                  return -1;
    //                          }
    //                  }
    //
    //                  if (tmlm.getRequestByName(id) != null) {
    //                          error = "Duplicate definition of request " + id;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  request = new TMLRequest(id, null);
    //                  request.addParam(params);
    //
    //                  for(i=2; i<_split.length; i++) {
    //                          t1 = tmlm.getTMLTaskByName(_split[i]);
    //                          if (t1 == null) {
    //                                  t1 = new TMLTask(_split[i], null, null);
    //                                  //TraceManager.addDev("New task:" + _split[i]);
    //                                  tmlm.addTask(t1);
    //                          }
    //                          if ((i+1) == _split.length) {
    //                                  request.setDestinationTask(t1);
    //                                  t1.setRequested(true);
    //                                  t1.setRequest(request);
    //                          } else {
    //                                  request.addOriginTask(t1);
    //                          }
    //                  }
    //
    //                  tmlm.addRequest(request);
    //          } // REQUEST
    //
    //          // LOSSYREQUEST
    //          if(isInstruction("LOSSYREQUEST", _split[0])) {
    //                  if (!inDec) {
    //                          error = "A lossyrequest may not be declared in a non-declaration part of a TML specification";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!((_split.length > 3) && (_split.length < 5))) {
    //                          error = "A lossyrequest must be declared with exactly 3 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //
    //                  if (!checkParameter("LOSSYREQUEST", _split, 1, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("LOSSYREQUEST", _split, 2, 1, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("LOSSYREQUEST", _split, 3, 9, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //
    //                  request = tmlm.getRequestByName(_split[1]);
    //                  if (request == null) {
    //                          error = "lossyrequest not previsouly declared as a regular event " + _split[1];
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  try {
    //                          tmp0 = Integer.decode(_split[2]).intValue();
    //                          } catch (Exception e) {tmp0 = 5;}
    //                  try {
    //                          tmp1 = Integer.decode(_split[3]).intValue();
    //                          } catch (Exception e) {tmp1 = -1;}
    //
    //                          request.setLossy(true, tmp0, tmp1);
    //          } // LOSSYREQUEST
    //
    //          // TASK
    //          if((isInstruction("TASK", _split[0]))) {
    //
    //                  //TraceManager.addDev("In task");
    //                  if (inTask) {
    //                          error = "A task may not be declared in the body of another task";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = true;
    //                  inTaskBehavior = false;
    //
    //                  if (_split.length != 2) {
    //                          error = "A request must be declared with exactly 2 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("TASK", _split, 1, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  //TraceManager.addDev("In task: 12");
    //                  task = tmlm.getTMLTaskByName(_split[1]);
    //                  if ((task != null)  && (task.getActivityDiagram() != null)) {
    //                          if (task.getActivityDiagram().getFirst() != null) {
    //                                  error = "Duplicate definition for task "+ (_split[1]);
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                  }
    //                  //TraceManager.addDev("In task: 13");
    //                  if (task == null) {
    //                          task = new TMLTask(_split[1], null, null);
    //                          tmlm.addTask(task);
    //                          //TraceManager.addDev("New task:" + _split[1]);
    //                  }
    //
    //                  TMLStartState start = new TMLStartState("start", null);
    //                  task.getActivityDiagram().setFirst(start);
    //                  tmlae = start;
    //
    //
    //          } // TASK
    //
    //
    //          // ENDTASK
    //          if((isInstruction("ENDTASK", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "A endtask may not be used outside the body of a task";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  inDec = true;
    //                  inTask = false;
    //                  inTaskDec = false;
    //                  inTaskBehavior = false;
    //
    //                  stop = new TMLStopState("stop", null);
    //                  task.getActivityDiagram().addElement(stop);
    //                  tmlae.addNext(stop);
    //
    //                  task = null;
    //          } // ENDTASK
    //
    //
    //          // Attribute declaration
    //          if ((isInstruction("INT", _split[0])) || (isInstruction("NAT", _split[0])) || (isInstruction("BOOL", _split[0]))){
    //                  if (!inTaskDec) {
    //                          error = "An attribute declaration must be done in a task right after its declaration, and before its bahavior";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  String inst = _split[0].toUpperCase();
    //
    //                  if (!((_split.length == 2) || (_split.length == 4))) {
    //                          error = "An attribute declaration must be done with either 1 or 3 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter(inst, _split, 1, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (_split.length > 2) {
    //                          if (!checkParameter(inst, _split, 2, 5, _lineNb)) {
    //                                  return -1;
    //                          }
    //                          if (!checkParameter(inst, _split, 3, 6, _lineNb)) {
    //                                  return -1;
    //                          }
    //                  }
    //
    //                  //TraceManager.addDev("Adding attribute " + _split[0] + " " + _split[1]);
    //
    //                  TMLAttribute ta = new TMLAttribute(_split[1], new TMLType(TMLType.getType(_split[0])));
    //                  if (_split.length > 2) {
    //                          ta.initialValue = _split[3];
    //                  } else {
    //                          ta.initialValue = ta.getDefaultInitialValue();
    //                  }
    //                  task.addAttribute(ta);
    //          } // Attribute declaration
    //
    //          // RANDOM
    //          if((isInstruction("RANDOM", _split[0]))) {
    //
    //                  if (!inTask) {
    //                          error = "A RANDOM operation may only be performed in a task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if (_split.length != 5) {
    //                          error = "A RANDOM operation must be declared with exactly 4 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("RANDOM", _split, 1, 1, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("RANDOM", _split, 2, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  random = new TMLRandom("random", null);
    //                  try {
    //                   random.setFunctionId(Integer.decode(_split[1]).intValue());
    //                  } catch (Exception e) {
    //                  }
    //
    //                  random.setVariable(_split[2]);
    //                  random.setMinValue(_split[3]);
    //                  random.setMaxValue(_split[4]);
    //
    //                  TraceManager.addDev("RANDOM min=" + random.getMinValue() + " max=" + random.getMaxValue());
    //
    //                  task.getActivityDiagram().addElement(random);
    //                  tmlae.addNext(random);
    //                  tmlae = random;
    //
    //          } // RANDOM
    //
    //          // READ
    //          if((isInstruction("READ", _split[0]))) {
    //
    //                  if (!inTask) {
    //                          error = "A READ operation may only be performed in a task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if (_split.length != 3) {
    //                          error = "A READ operation must be declared with exactly 3 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("READ", _split, 1, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  /*if (!checkParameter("READ", _split, 2, 7, _lineNb)) {
    //                          return -1;
    //                  }*/
    //
    //                  ch = tmlm.getChannelByName(_split[1]);
    //                  if (ch == null ){
    //                          error = "Undeclared channel: " +  _split[1];
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  if (ch.getDestinationTask() != task ){
    //                          error = "READ operations must be done only in destination task. Should be in task: " + ch.getDestinationTask().getName();
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //
    //                  TMLReadChannel tmlrch = new TMLReadChannel(_split[1], null);
    //                  tmlrch.addChannel(ch);
    //                  tmlrch.setNbOfSamples(_split[2]);
    //                  task.getActivityDiagram().addElement(tmlrch);
    //                  tmlae.addNext(tmlrch);
    //                  tmlae = tmlrch;
    //
    //          } // READ
    //
    //          // WRITE
    //          if((isInstruction("WRITE", _split[0]))) {
    //
    //                  if (!inTask) {
    //                          error = "A WRITE operation may only be performed in a task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if (_split.length < 3) {
    //                          error = "A WRITE operation must be declared with at most 3 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!checkParameter("WRITE", _split, 1, 0, _lineNb)) {
    //                          return -1;
    //                  }
    //
    //                  TMLWriteChannel tmlwch = new TMLWriteChannel(_split[1], null);
    //                  for(int k=0; k<_split.length-2; k++) {
    //                          ch = tmlm.getChannelByName(_split[1+k]);
    //                          if (ch == null ){
    //                                  error = "Undeclared channel: " +  _split[1+k];
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                          if (ch.getOriginTask() != task ){
    //                                  error = "WRITE operations must be done only in origin task. Should be in task: " + ch.getOriginTask().getName();
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //
    //                          tmlwch.addChannel(ch);
    //                  }
    //
    //                  tmlwch.setNbOfSamples(_split[2]);
    //                  task.getActivityDiagram().addElement(tmlwch);
    //                  tmlae.addNext(tmlwch);
    //                  tmlae = tmlwch;
    //
    //          } // WRITE
    //
    //          // NOTIFY
    //          if((isInstruction("NOTIFY", _split[0]))) {
    //
    //                  if (!inTask) {
    //                          error = "A NOTIFY operation may only be performed in a task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if (_split.length < 2) {
    //                          error = "A NOTIFY operation must be declared with at least 2 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  evt = tmlm.getEventByName(_split[1]);
    //                  if (evt == null) {
    //                          error = "Unknown event: " + _split[1] ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  dec = evt.getNbOfParams();
    //
    //                  if (_split.length != 2 + dec) {
    //                          error = "A NOTIFY operation on evt " + evt.getName() + " must be declared with exactly " + (1 + dec) + " parameters and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  TMLSendEvent sevt = new TMLSendEvent(evt.getName(), null);
    //                  sevt.setEvent(evt);
    //                  for(i=2; i<2 + dec; i++) {
    //                          sevt.addParam(_split[i]);
    //                  }
    //
    //                  task.getActivityDiagram().addElement(sevt);
    //                  tmlae.addNext(sevt);
    //                  tmlae = sevt;
    //          } // NOTIFY
    //
    //          // WAIT
    //          if((isInstruction("WAIT", _split[0]))) {
    //
    //                  if (!inTask) {
    //                          error = "A WAIT operation may only be performed in a task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if (_split.length < 2) {
    //                          error = "A WAIT operation must be declared with at least 2 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  evt = tmlm.getEventByName(_split[1]);
    //                  if (evt == null) {
    //                          error = "Unknown event: " + _split[1] ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  dec = evt.getNbOfParams();
    //
    //                  if (_split.length != 2 + dec) {
    //                          error = "A WAIT operation on evt " + evt.getName() + " must be declared with exactly " + (1 + dec) + " parameters and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  // Each param must be a declared attribute of the right type
    //                  for(i=2; i<2 + dec; i++) {
    //                          attribute = task.getAttributeByName(_split[i]);
    //                          if (attribute == null) {
    //                                  error = "Attribute: " + _split[i] + " is undeclared";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                          if (attribute.type.getType() != evt.getType(i-2).getType()) {
    //                                  error = "Attribute: " + _split[i] + " is not of the right type";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                  }
    //
    //
    //                  TMLWaitEvent wevt = new TMLWaitEvent(evt.getName(), null);
    //                  wevt.setEvent(evt);
    //                  for(i=2; i<2 + dec; i++) {
    //                          wevt.addParam(_split[i]);
    //                  }
    //
    //                  task.getActivityDiagram().addElement(wevt);
    //                  tmlae.addNext(wevt);
    //                  tmlae = wevt;
    //          } // WAIT
    //
    //          // NOTIFIED
    //          if((isInstruction("NOTIFIED", _split[0]))) {
    //
    //                  if (!inTask) {
    //                          error = "A NOTIFIED operation may only be performed in a task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if (_split.length != 3) {
    //                          error = "A NOTIFIED operation must be declared with exactly 2 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  evt = tmlm.getEventByName(_split[1]);
    //                  if (evt == null) {
    //                          error = "Unknown event: " + _split[1] ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  attribute = task.getAttributeByName(_split[2]);
    //                  if (attribute == null) {
    //                          error = "Attribute: " + _split[2] + " is undeclared";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  if (attribute.type.getType() != TMLType.NATURAL) {
    //                          error = "Attribute: " + _split[2] + " should be of natural type";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  TMLNotifiedEvent nevt = new TMLNotifiedEvent(evt.getName(), null);
    //                  nevt.setEvent(evt);
    //                  nevt.setVariable(_split[2]);
    //
    //                  task.getActivityDiagram().addElement(nevt);
    //                  tmlae.addNext(nevt);
    //                  tmlae = nevt;
    //          } // NOTIFIED
    //
    //          // Send REQUEST
    //          if((isInstruction("REQUEST", _split[0])) && (inTask)) {
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if (_split.length < 2) {
    //                          error = "A REQUEST operation must be declared with at least 1 parameter (request name), and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  request = tmlm.getRequestByName(_split[1]);
    //                  if (request == null) {
    //                          error = "Unknown request: " + _split[1] ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  dec = request.getNbOfParams();
    //
    //                  if (_split.length != 2 + dec) {
    //                          error = "A REQUEST operation on request " + request.getName() + " must be declared with exactly " + (1 + dec) + " parameters and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  TMLSendRequest sreq = new TMLSendRequest(request.getName(), null);
    //                  sreq.setRequest(request);
    //                  for(i=2; i<2 + dec; i++) {
    //                          sreq.addParam(_split[i]);
    //                  }
    //
    //                  task.getActivityDiagram().addElement(sreq);
    //                  tmlae.addNext(sreq);
    //                  tmlae = sreq;
    //          } // Send REQUEST
    //
    //          // FOR
    //          if((isInstruction("FOR", _split[0])) && (inTask)) {
    //                  //TraceManager.addDev("FOR encountered");
    //                  if (_split.length < 2) {
    //                          error = "FOR operation: missing parameters";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  // Extract the three elements of FOR
    //                  String forp = _split[1];
    //                  String forps[];
    //                  tmp0 = forp.indexOf('(');
    //                          tmp1 = forp.lastIndexOf(')');
    //                          if ((tmp0 == -1) || (tmp1 == -1)) {
    //                                  error = "FOR operation: badly formed parameters";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                          forp = forp.substring(tmp0+1, tmp1);
    //                          forps = forp.split(";");
    //                          if (forps.length != 3) {
    //                                  error = "FOR operation: badly formed parameters";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //
    //                          // All is ok: constructing the FOR
    //                          parseElt = new TMLParserSaveElt();
    //                          parseElt.type = TMLParserSaveElt.FOR;
    //                          parses.add(0, parseElt);
    //                          TMLForLoop loop = new TMLForLoop("loop", null);
    //                          loop.setInit(forps[0].trim());
    //                          loop.setCondition(forps[1].trim());
    //                          loop.setIncrement(forps[2].trim());
    //                          task.getActivityDiagram().addElement(loop);
    //                          parseElt.tmlae = loop;
    //                          tmlae.addNext(loop);
    //                          tmlae = loop;
    //
    //          } // FOR
    //
    //          // ENDFOR
    //          if(isInstruction("ENDFOR", _split[0])) {
    //                  if (!inTask) {
    //                          error = "ENDFOR: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  // Extract the first element of the stack
    //                  if (parses.size() == 0) {
    //                          error = "ENDFOR: badly placed instruction.";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parseElt = parses.get(0);
    //                  if (parseElt.type != TMLParserSaveElt.FOR) {
    //                          error = "ENDFOR: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  // All is ok: constructing the FOR
    //                  parses.remove(0);
    //                  stop = new TMLStopState("stop loop", null);
    //                  task.getActivityDiagram().addElement(stop);
    //                  tmlae.addNext(stop);
    //                  tmlae = parseElt.tmlae;
    //          } // ENDFOR
    //
    //          // SELECTEVT
    //          if((isInstruction("SELECTEVT", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "SELECTEVT: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if(_split.length > 1) {
    //                          error = "A SELECTEVT cannot have any parameters";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  parseElt = new TMLParserSaveElt();
    //                  parseElt.type = TMLParserSaveElt.SELECTEVT;
    //                  parses.add(0, parseElt);
    //                  TMLSequence seq = new TMLSequence("sequence", null);
    //                  parseElt.top = seq;
    //                  tmlae.addNext(seq);
    //                  TMLSelectEvt sel = new TMLSelectEvt("select evt", null);
    //                  parseElt.tmlae = sel;
    //                  seq.addNext(sel);
    //                  task.getActivityDiagram().addElement(seq);
    //                  task.getActivityDiagram().addElement(sel);
    //                  tmlae = sel;
    //          } // SELECTEVT
    //
    //          // ENDSELECTEVT
    //          if((isInstruction("ENDSELECTEVT", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "ENDSELECTEVT: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  // Extract the first element of the stack
    //                  if (parses.size() == 0) {
    //                          error = "ENDSELECTEVT: badly placed instruction.";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parseElt = parses.get(0);
    //                  if (parseElt.type != TMLParserSaveElt.SELECTEVT) {
    //                          error = "ENDSELECTEVT: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  parses.remove(0);
    //                  tmlae = parseElt.top;
    //          } // ENDSELECTEVT
    //
    //          // CASE
    //          if((isInstruction("CASE", _split[0]))) {
    //                  if (!inTask) {
    //                  error = "CASE: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (parses.size() == 0) {
    //                  error = "CASE: corresponding SELECTEVT not found";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  } else {
    //                          parseElt = parses.get(0);
    //                          if (parseElt.type != TMLParserSaveElt.SELECTEVT) {
    //                          error = "CASE: corresponding SELECTEVT not found";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if(_split.length < 2) {
    //                          error = "A CASE must have at least two parameters";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  evt = tmlm.getEventByName(_split[1]);
    //                  if (evt == null) {
    //                          error = "Unknown event: " + _split[1] ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  dec = evt.getNbOfParams();
    //
    //                  if (_split.length != 2 + dec) {
    //                          error = "A CASE operation on evt " + evt.getName() + " must be declared with exactly " + (1 + dec) + " parameters and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  TMLWaitEvent wevt = new TMLWaitEvent(evt.getName(), null);
    //                  wevt.setEvent(evt);
    //                  for(i=2; i<2 + dec; i++) {
    //                          wevt.addParam(_split[i]);
    //                  }
    //
    //                  task.getActivityDiagram().addElement(wevt);
    //                  tmlae.addNext(wevt);
    //
    //                  parseElt = new TMLParserSaveElt();
    //                  parseElt.type = TMLParserSaveElt.CASE;
    //                  parseElt.tmlae = wevt;
    //                  parseElt.top = tmlae;
    //                  parses.add(0, parseElt);
    //
    //                  tmlae = wevt;
    //          } // CASE
    //
    //
    //          // ENDCASE
    //          if((isInstruction("ENDCASE", _split[0]))) {
    //                  if (!inTask) {
    //                  error = "ENDCASE: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  // Extract the first element of the stack
    //                  if (parses.size() == 0) {
    //                  error = "ENDCASE: badly placed instruction.";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parseElt = parses.get(0);
    //                  if (parseElt.type != TMLParserSaveElt.CASE) {
    //                  error = "ENDCASE: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parses.remove(0);
    //                  stop = new TMLStopState("stop case", null);
    //                  task.getActivityDiagram().addElement(stop);
    //                  tmlae.addNext(stop);
    //                  tmlae = parseElt.top;
    //          } // ENDCASE
    //
    //          // RANDOMSEQ
    //          if((isInstruction("RANDOMSEQ", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "RANDOMSEQ: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if(_split.length > 1) {
    //                          error = "A RANDOMSEQ cannot have any parameters";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  parseElt = new TMLParserSaveElt();
    //                  parseElt.type = TMLParserSaveElt.RANDOMSEQ;
    //                  parses.add(0, parseElt);
    //                  TMLSequence seq = new TMLSequence("sequence", null);
    //                  parseElt.top = seq;
    //                  tmlae.addNext(seq);
    //                  TMLRandomSequence rseq = new TMLRandomSequence("random sequence", null);
    //                  parseElt.tmlae = rseq;
    //                  seq.addNext(rseq);
    //                  task.getActivityDiagram().addElement(seq);
    //                  task.getActivityDiagram().addElement(rseq);
    //                  tmlae = rseq;
    //          } // RANDOMSEQ
    //
    //          // ENDRANDOMSEQ
    //          if((isInstruction("ENDRANDOMSEQ", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "ENDRANDOMSEQ: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  // Extract the first element of the stack
    //                  if (parses.size() == 0) {
    //                          error = "ENDRANDOMSEQ: badly placed instruction.";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parseElt = parses.get(0);
    //                  if (parseElt.type != TMLParserSaveElt.RANDOMSEQ) {
    //                          error = "ENDRANDOMSEQ: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  parses.remove(0);
    //                  tmlae = parseElt.top;
    //          } // ENDRANDOMSEQ
    //
    //
    //          // SEQ
    //          if((isInstruction("SEQ", _split[0]))) {
    //                  if (!inTask) {
    //                  error = "SEQ: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (parses.size() == 0) {
    //                          error = "SEQ: corresponding RANDOMSEQ not found";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  } else {
    //                          parseElt = parses.get(0);
    //                          if (parseElt.type != TMLParserSaveElt.RANDOMSEQ) {
    //                          error = "SEQ: corresponding RANDOMSEQ not found";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if(_split.length >1 ) {
    //                          error = "A SEQ has no parameter";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!(parseElt.tmlae instanceof TMLRandomSequence)) {
    //                          error = "Malformed specification: unexpected SEQ";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  TMLRandomSequence rseq = (TMLRandomSequence)parseElt.tmlae;
    //                  TMLSequence seq = new TMLSequence("sequence", null);
    //                  rseq.addNext(seq);
    //
    //                  task.getActivityDiagram().addElement(seq);
    //
    //                  parseElt = new TMLParserSaveElt();
    //                  parseElt.type = TMLParserSaveElt.SEQ;
    //                  parseElt.tmlae = seq;
    //                  parseElt.top = rseq;
    //                  parses.add(0, parseElt);
    //
    //                  tmlae = seq;
    //          } // SEQ
    //
    //          // ENDSEQ
    //          if((isInstruction("ENDSEQ", _split[0]))) {
    //                  if (!inTask) {
    //                  error = "ENDSEQ: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  // Extract the first element of the stack
    //                  if (parses.size() == 0) {
    //                  error = "ENDSEQ: badly placed instruction.";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parseElt = parses.get(0);
    //                  if (parseElt.type != TMLParserSaveElt.SEQ) {
    //                  error = "ENDSEQ: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parses.remove(0);
    //                  stop = new TMLStopState("stop case", null);
    //                  task.getActivityDiagram().addElement(stop);
    //                  tmlae.addNext(stop);
    //                  tmlae = parseElt.top;
    //          } // ENDSEQ
    //
    //          // RAND
    //          if((isInstruction("RAND", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "RAND: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if(_split.length > 1) {
    //                          error = "A RAND cannot have any parameters";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  parseElt = new TMLParserSaveElt();
    //                  parseElt.type = TMLParserSaveElt.RAND;
    //                  parses.add(0, parseElt);
    //                  TMLSequence seq = new TMLSequence("sequence", null);
    //                  parseElt.top = seq;
    //                  tmlae.addNext(seq);
    //                  TMLChoice choice = new TMLChoice("choice evt", null);
    //                  parseElt.tmlae = choice;
    //                  seq.addNext(choice);
    //                  task.getActivityDiagram().addElement(seq);
    //                  task.getActivityDiagram().addElement(choice);
    //                  tmlae = choice;
    //          } // RAND
    //
    //          // ENDRAND
    //          if((isInstruction("ENDRAND", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "ENDRAND: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  // Extract the first element of the stack
    //                  if (parses.size() == 0) {
    //                          error = "ENDRAND: badly placed instruction.";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parseElt = parses.get(0);
    //                  if (parseElt.type != TMLParserSaveElt.RAND) {
    //                          error = "ENDRAND: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  parses.remove(0);
    //                  tmlae = parseElt.top;
    //          } // ENDRAND
    //
    //          // CASERAND
    //          if((isInstruction("CASERAND", _split[0]))) {
    //                  if (!inTask) {
    //                  error = "CASERAND: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (parses.size() == 0) {
    //                  error = "CASERAND: corresponding RAND not found";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  } else {
    //                          parseElt = parses.get(0);
    //                          if (parseElt.type != TMLParserSaveElt.RAND) {
    //                          error = "CASERAND: corresponding RAND not found";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if(_split.length != 2) {
    //                          error = "A CASERAND should have one parameter";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (!(parseElt.tmlae instanceof TMLChoice)) {
    //                          error = "Malformed specification";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  TMLChoice choice = (TMLChoice)parseElt.tmlae;
    //                  TMLSequence seq = new TMLSequence("sequence", null);
    //                  choice.addGuard("[" + _split[1] + "%]");
    //                  choice.addNext(seq);
    //
    //                  task.getActivityDiagram().addElement(seq);
    //
    //                  parseElt = new TMLParserSaveElt();
    //                  parseElt.type = TMLParserSaveElt.CASERAND;
    //                  parseElt.tmlae = seq;
    //                  parseElt.top = choice;
    //                  parses.add(0, parseElt);
    //
    //                  tmlae = seq;
    //
    //          } // CASERAND
    //
    //
    //          // ENDCASERAND
    //          if((isInstruction("ENDCASERAND", _split[0]))) {
    //                  if (!inTask) {
    //                  error = "ENDCASERAND: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  // Extract the first element of the stack
    //                  if (parses.size() == 0) {
    //                  error = "ENDCASERAND: badly placed instruction.";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parseElt = parses.get(0);
    //                  if (parseElt.type != TMLParserSaveElt.CASERAND) {
    //                  error = "ENDCASERAND: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parses.remove(0);
    //                  stop = new TMLStopState("stop case", null);
    //                  task.getActivityDiagram().addElement(stop);
    //                  tmlae.addNext(stop);
    //                  tmlae = parseElt.top;
    //          } // ENDCASERAND
    //
    //          // IF
    //          if((isInstruction("IF", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "IF: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if(_split.length != 2) {
    //                          error = "IF should be followed by one condition";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  String cond = _split[1].trim();
    //                  tmp0 = cond.indexOf('(');
    //                          tmp1 = cond.lastIndexOf(')');
    //                          if ((tmp0 == -1) || (tmp1 == -1)) {
    //                                  error = "IF operation: badly formed condition";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                          cond = cond.substring(tmp0+1, tmp1);
    //
    //                          parseElt = new TMLParserSaveElt();
    //                          parseElt.type = TMLParserSaveElt.IF;
    //                          parses.add(0, parseElt);
    //                          TMLSequence seq = new TMLSequence("sequence", null);
    //                          parseElt.top = seq;
    //                          tmlae.addNext(seq);
    //                          TMLChoice choice = new TMLChoice("if", null);
    //                          parseElt.tmlae = choice;
    //                          seq.addNext(choice);
    //                          task.getActivityDiagram().addElement(seq);
    //                          task.getActivityDiagram().addElement(choice);
    //
    //                          seq = new TMLSequence("sequence", null);
    //                          task.getActivityDiagram().addElement(seq);
    //                          choice.addNext(seq);
    //                          choice.addGuard("[" + cond + "]");
    //
    //                          tmlae = seq;
    //          } // IF
    //
    //          // ORIF
    //          if((isInstruction("ORIF", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "ORIF: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if(_split.length != 2) {
    //                          error = "ORIF should be followed by one condition";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //
    //                  String cond = _split[1].trim();
    //                  //TraceManager.addDev("cond1=" + cond);
    //                  tmp0 = cond.indexOf('(');
    //                          tmp1 = cond.lastIndexOf(')');
    //                          if ((tmp0 == -1) || (tmp1 == -1)) {
    //                                  error = "ORIF operation: badly formed condition";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                          cond = cond.substring(tmp0+1, tmp1);
    //                          //TraceManager.addDev("cond2=" + cond);
    //
    //                          if (parses.size() == 0) {
    //                                  error = "ORIF: badly placed instruction.";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                          parseElt = parses.get(0);
    //                          if (parseElt.type != TMLParserSaveElt.IF) {
    //                                  error = "ORIF: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //
    //                          if (parseElt.nbElse > 0) {
    //                                  error = "ORIF: should not followed a else instruction";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //
    //                          stop = new TMLStopState("stop", null);
    //                          task.getActivityDiagram().addElement(stop);
    //                          tmlae.addNext(stop);
    //
    //                          TMLSequence seq = new TMLSequence("sequence", null);
    //                          TMLChoice choice = (TMLChoice)parseElt.tmlae;
    //                          task.getActivityDiagram().addElement(seq);
    //
    //                          choice.addNext(seq);
    //                          choice.addGuard("[" + cond + "]");
    //
    //                          tmlae = seq;
    //          } // ORIF
    //
    //          // ELSE
    //          if((isInstruction("ELSE", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "ELSE: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if(_split.length != 1) {
    //                          error = "ELSE should have no parameter";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if (parses.size() == 0) {
    //                          error = "ELSE: badly placed instruction.";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parseElt = parses.get(0);
    //                  if (parseElt.type != TMLParserSaveElt.IF) {
    //                          error = "ELSE: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  stop = new TMLStopState("stop", null);
    //                  task.getActivityDiagram().addElement(stop);
    //                  tmlae.addNext(stop);
    //
    //                  parseElt.nbElse ++;
    //
    //                  TMLSequence seq = new TMLSequence("sequence", null);
    //                  TMLChoice choice = (TMLChoice)parseElt.tmlae;
    //                  task.getActivityDiagram().addElement(seq);
    //
    //                  choice.addNext(seq);
    //                  choice.addGuard("[else]");
    //
    //                  tmlae = seq;
    //          } // ELSE
    //
    //          // ENDIF
    //          if((isInstruction("ENDIF", _split[0]))) {
    //                  if (!inTask) {
    //                          error = "ENDIF: must be used in a Task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  // Extract the first element of the stack
    //                  if (parses.size() == 0) {
    //                          error = "ENDIF: badly placed instruction.";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //                  parseElt = parses.get(0);
    //                  if (parseElt.type != TMLParserSaveElt.IF) {
    //                          error = "ENDIF: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  stop = new TMLStopState("stop", null);
    //                  task.getActivityDiagram().addElement(stop);
    //                  tmlae.addNext(stop);
    //
    //                  parses.remove(0);
    //                  tmlae = parseElt.top;
    //          } // ENDIF
    //
    //          // EXECI
    //          if((isInstruction("EXECI", _split[0]))) {
    //
    //                  if (!inTask) {
    //                          error = "An EXECI operation may only be performed in a task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if ((_split.length < 2) ||(_split.length > 4)) {
    //                          error = "An EXECI operation must be declared with 1 or 2 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (_split.length == 2) {
    //                          TMLExecI execi = new TMLExecI("execi", null);
    //                          execi.setAction(_split[1]);
    //                          tmlae.addNext(execi);
    //                          task.getActivityDiagram().addElement(execi);
    //                          tmlae = execi;
    //                  } else {
    //                          TMLExecIInterval execi = new TMLExecIInterval("execi", null);
    //                          execi.setMinDelay(_split[1]);
    //                          execi.setMaxDelay(_split[2]);
    //                          tmlae.addNext(execi);
    //                          task.getActivityDiagram().addElement(execi);
    //                          tmlae = execi;
    //                  }
    //          } // EXECI
    //
    //          // EXECC
    //          if((isInstruction("EXECC", _split[0]))) {
    //
    //                  if (!inTask) {
    //                          error = "An EXECC operation may only be performed in a task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if ((_split.length < 2) ||(_split.length > 4)) {
    //                          error = "An EXECC operation must be declared with 1 or 2 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (_split.length == 2) {
    //                          TMLExecC execc = new TMLExecC("execc", null);
    //                          execc.setAction(_split[1]);
    //                          tmlae.addNext(execc);
    //                          task.getActivityDiagram().addElement(execc);
    //                          tmlae = execc;
    //                  } else {
    //                          TMLExecCInterval execci = new TMLExecCInterval("execci", null);
    //                          execci.setMinDelay(_split[1]);
    //                          execci.setMaxDelay(_split[2]);
    //                          tmlae.addNext(execci);
    //                          task.getActivityDiagram().addElement(execci);
    //                          tmlae = execci;
    //                  }
    //          } // EXECC
    //
    //          // DELAY
    //          if((isInstruction("DELAY", _split[0]))) {
    //
    //                  if (!inTask) {
    //                          error = "A DELAY operation may only be performed in a task body";
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  if ((_split.length < 3) ||(_split.length > 5)) {
    //                          error = "A DELAY operation must be declared with 2 or 3 parameters, and not " + (_split.length - 1) ;
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  if (_split.length == 3) {
    //                          if (!checkParameter("DELAY", _split, 2, 0, _lineNb)) {
    //                                  error = "A DELAY operation must be specified with a valid time unit (ns, us, ms, s))" ;
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                  }
    //
    //                  if (_split.length == 4) {
    //                          if (!checkParameter("DELAY", _split, 3, 0, _lineNb)) {
    //                                  error = "A DELAY operation must be specified with a valid time unit (ns, us, ms, s))" ;
    //                                  addError(0, _lineNb, 0, error);
    //                                  return -1;
    //                          }
    //                  }
    //
    //                  TMLDelay delay = new TMLDelay("delay", null);
    //                  delay.setMinDelay(_split[1]);
    //                  if (_split.length == 3) {
    //                          delay.setMaxDelay(_split[1]);
    //                          delay.setUnit(_split[2]);
    //                  } else {
    //                          delay.setMaxDelay(_split[2]);
    //                          delay.setUnit(_split[3]);
    //                  }
    //
    //
    //                  tmlae.addNext(delay);
    //                  task.getActivityDiagram().addElement(delay);
    //                  tmlae = delay;
    //
    //          } // EXECC
    //
    //          // Other command
    //          if((_split[0].length() > 0) && (!(isInstruction(_split[0])))) {
    //                  if (!inTask) {
    //                          error = "Syntax error in TML modeling: unrecognized instruction:" + _split[0];
    //                          addError(0, _lineNb, 0, error);
    //                          return -1;
    //                  }
    //
    //                  inDec = false;
    //                  inTask = true;
    //                  inTaskDec = false;
    //                  inTaskBehavior = true;
    //
    //                  TMLActionState action = new TMLActionState(_split[0], null);
    //                  action.setAction(_line);
    //                  tmlae.addNext(action);
    //                  task.getActivityDiagram().addElement(action);
    //                  tmlae = action;
    //
    //          } // Other command
    //
    //          return 0;
    //  }
    //
    //  // Type 0: id
    //  // Type 1: numeral
    //  // Type 2: channel type
    //  // Type 3: event type
    //  // Type 4: event name
    //  // Type 5: '='
    //  // Type 6: attribute value
    //  // Type 7: id or numeral
    //  // Type 8:unit
    //
    ///*        public boolean checkParameter(String _inst, String[] _split, int _parameter, int _type, int _lineNb) {
    //          boolean err = false;
    //          String error;
    //
    //          if(_parameter < _split.length) {
    //                  switch(_type) {
    //                  case 0:
    //                          if (!isAValidId(_split[_parameter])) {
    //                                  err = true;
    //                          }
    //                          break;
    //                  case 1:
    //                          if (!isANumeral(_split[_parameter])) {
    //                                  err = true;
    //                          }
    //                          break;
    //                  case 2:
    //                          if (!isIncluded(_split[_parameter], channeltypes)) {
    //                                  err = true;
    //                          }
    //                          break;
    //                  case 3:
    //                          if (!isIncluded(_split[_parameter], eventtypes)) {
    //                                  err = true;
    //                          }
    //                          break;
    //                  case 4:
    //                          if (!isAValidId(getEvtId(_split[_parameter]))) {
    //                                  err = true;
    //                                  //TraceManager.addDev("Unvalid id");
    //                          } else if (!TMLEvent.isAValidListOfParams(getParams(_split[_parameter]))) {
    //                                  //TraceManager.addDev("Unvalid param");
    //                                  err = true;
    //                          }
    //                          break;
    //                  case 5:
    //                          if (!(_split[_parameter].equals("="))) {
    //                                  TraceManager.addDev("Error of =");
    //                                  err = true;
    //                          }
    //                          break;
    //                  case 6:
    //                          if (_inst.equals("BOOL")) {
    //                                  String tmp = _split[_parameter].toUpperCase();
    //                                  if (!(tmp.equals("TRUE") || tmp.equals("FALSE"))) {
    //                                          err = true;
    //                                  }
    //                          } else {
    //                                  if (!isANumeral(_split[_parameter])) {
    //                                          err = true;
    //                                  }
    //                          }
    //                          break;
    //                  case 7:
    //                          if (!isAValidId(_split[_parameter]) && !isANumeral(_split[_parameter])) {
    //                                  err = true;
    //                          }
    //                          break;
    //                  case 8:
    //                          if (!isAValidUnit(_split[_parameter])) {
    //                                  err = true;
    //                          }
    //                          break;
    //                  case 9:
    //                          if (!isANegativeOrPositiveNumeral(_split[_parameter])) {
    //                                  err = true;
    //                          }
    //                          break;
    //                  }
    //          } else {
    //                  err = true;
    //          }
    //          if (err) {
    //                  error = "Unvalid parameter #" + _parameter + "-> $" + _split[_parameter] + "$ <- in " + _inst + " instruction";
    //                  addError(0, _lineNb, 0, error);
    //                  return false;
    //          }
    //          return true;
    //  }
    //*/
    //
    //  public boolean isInstruction(String instcode, String inst) {
    //          return (inst.toUpperCase().compareTo(instcode) == 0);
    //  }
    //
    //  public boolean isInstruction(String instcode) {
    //          return (!checkKeywords(instcode));
    //  }
    //
    //  public static boolean isAValidId(String _id) {
    //          if ((_id == null) || (_id.length() == 0)) {
    //                  return false;
    //          }
    //
    //          boolean b1 = (_id.substring(0,1)).matches("[a-zA-Z]");
    //        boolean b2 = _id.matches("\\w*");
    //          boolean b3 = checkKeywords(_id);
    //
    //          return (b1 && b2 && b3);
    //  }
    //
    //  public boolean isANumeral(String _num) {
    //          return _num.matches("\\d*");
    //  }
    //
    //  public boolean isANegativeOrPositiveNumeral(String _num) {
    //          if (_num.startsWith("-")) {
    //                  return isANumeral(_num.substring(1, _num.length()));
    //          }
    //          return isANumeral(_num);
    //  }
    //
    //  public boolean isAValidUnit(String s) {
    //          if (s.compareTo("ns") == 0) {
    //                  return true;
    //          } else if (s.compareTo("us") == 0) {
    //                  return true;
    //          } else if (s.compareTo("ms") == 0) {
    //                  return true;
    //          } else if (s.compareTo("s") == 0) {
    //                  return true;
    //          }
    //
    //          return false;
    //  }
    //
    //  public static boolean checkKeywords(String _id) {
    //          String id = _id.toUpperCase();
    //          for(int i=0; i<keywords.length; i++) {
    //                  if (id.compareTo(keywords[i]) == 0) {
    //                          return false;
    //                  }
    //          }
    //          return true;
    //  }
    //
    //  public boolean isIncluded(String _id, String[] _list) {
    //          String id = _id.toUpperCase();
    //          for(int i=0; i<_list.length; i++) {
    //                  if (id.compareTo(_list[i]) == 0) {
    //                          return true;
    //                  }
    //          }
    //          return false;
    //  }
    //
    //  public String removeUndesiredWhiteSpaces(String _input, int _lineNb) {
    //          String error, tmp;
    //          int index0, index1, index2;
    //
    //          if (_input.startsWith("EVENT ")) {
    //                  index0 = _input.indexOf('(');
    //                          index1 = _input.indexOf(')');
    //                          if ((index0 == -1) || (index1 == -1)) {
    //                                  error = "Syntax Error: should be of the form EVENT evtname(<list of max three types>) + other parameters";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return null;
    //                          }
    //                          return Conversion.replaceBetweenIndex(_input, index0, index1, " ", "");
    //          }
    //
    //          if (_input.startsWith("REQUEST ") && (inDec)) {
    //                  index0 = _input.indexOf('(');
    //                          index1 = _input.indexOf(')');
    //                          if ((index0 == -1) || (index1 == -1)) {
    //                                  error = "Syntax Error: should be of the form REQUEST requestname(<list of max three types>) + other parameters";
    //                                  addError(0, _lineNb, 0, error);
    //                                  return null;
    //                          }
    //                          return Conversion.replaceBetweenIndex(_input, index0, index1, " ", "");
    //          }
    //
    //          if (_input.startsWith("FOR(")) {
    //                          _input = "FOR (" + _input.substring(4, _input.length());
    //          }
    //
    //          if (_input.startsWith("FOR (")) {
    //                          tmp = _input.substring(5, _input.length());
    //                          tmp = Conversion.replaceAllString(tmp, " ", "");
    //                          return "FOR (" + tmp;
    //          }
    //
    //          if (_input.startsWith("IF(")) {
    //                          _input = "IF (" + _input.substring(3, _input.length());
    //          }
    //
    //          if (_input.startsWith("IF (")) {
    //                          tmp = _input.substring(4, _input.length());
    //                          tmp = Conversion.replaceAllString(tmp, " ", "");
    //                          return "IF (" + tmp;
    //          }
    //
    //          if (_input.startsWith("ORIF(")) {
    //                          _input = "ORIF (" + _input.substring(5, _input.length());
    //          }
    //
    //          if (_input.startsWith("ORIF (")) {
    //                          tmp = _input.substring(6, _input.length());
    //                          tmp = Conversion.replaceAllString(tmp, " ", "");
    //                          return "ORIF (" + tmp;
    //          }
    //
    //          return _input;
    //  }
    //
    //  private String getEvtId(String _input) {
    //          int index = _input.indexOf('(');
    //                  if (index == -1) {
    //                          return _input;
    //                  }
    //                  return _input.substring(0, index);
    //  }
    //
    //  private String getParams(String _input) {
    //          //TraceManager.addDev("input=" + _input);
    //          int index0 = _input.indexOf('(');
    //                  int index1 = _input.indexOf(')');
    //                  if ((index0 == -1) || (index1 == -1)) {
    //                          return _input;
    //                  }
    //                  return _input.substring(index0 + 1, index1);
    //  }
    //
    //  private static String prepareString(String s) {
    //          return s.replaceAll("\\s", "");
    //  }
    //
    //  public static String modifyString(String s) {
    //          return prepareString(s);
    //  }
}
