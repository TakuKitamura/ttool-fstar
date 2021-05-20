/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
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
 */

package tmltranslator;

import compiler.tmlparser.ParseException;
import compiler.tmlparser.SimpleNode;
import compiler.tmlparser.TMLExprParser;
import compiler.tmlparser.TokenMgrError;
import tmltranslator.tmlcp.*;

import java.io.StringReader;
import java.util.*;

/**
 * Class TMLSyntaxChecking Used verifying the syntax of the TML specification
 * for a Communication Pattern Creation: 05/09/2014
 *
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @version 1.0 05/09/2014
 */
public class TMLCPSyntaxChecking {

    // private final String WRONG_ORIGIN_CHANNEL = "is not declared as an origin
    // channel of the task";
    // private final String WRONG_DESTINATION_CHANNEL = "is not declared as a
    // destination channel of the task";
    // private final String WRONG_ORIGIN_EVENT = "is not declared as an origin event
    // of the task";
    // private final String WRONG_DESTINATION_EVENT = "is not declared as a
    // destination event of the task";
    // private final String WRONG_ORIGIN_REQUEST = "is not declared as an origin
    // request of the task";
    private final String SYNTAX_ERROR = "syntax error";
    // private final String WRONG_VARIABLE_IDENTIFIER = "forbidden variable's name";
    private final String VARIABLE_ERROR = "variable is not used according to its type";
    private final String UNDECLARED_VARIABLE = "unknown variable";
    private final String WRONG_VARIABLE_TYPE = "incorrect variable type";
    // private final String SYNTAX_ERROR_VARIABLE_EXPECTED = "syntax error (variable
    // expected)";
    // private final String TIME_UNIT_ERROR = "unknown time unit";

    private List<TMLCPError> errors;
    private List<TMLCPError> warnings;
    private TMLCP tmlcp;
    // private TMLMapping mapping;

    public TMLCPSyntaxChecking(TMLCP _tmlcp) {
        tmlcp = _tmlcp;
    }

    /*
     * public TMLCPSyntaxChecking(TMLMapping _mapping) { mapping = _mapping; tmlm =
     * mapping.getTMLModeling(); }
     */

    // Checking the syntax of Activity and Sequence Diagrams
    public void checkSyntax() {

        errors = new ArrayList<TMLCPError>();
        warnings = new ArrayList<TMLCPError>();

        /* checkMainCP(); */
        checkActivityDiagrams();
        checkSequenceDiagrams();
    }

    // Check that all diagrams, forks, joins and choices are connected by retrieving
    // the list
    // of elements and checking if they appear as start or end name in the list of
    // connectors.
    // First check the mainCP then check the other Activity Diagrams
    private void checkActivityDiagrams() {

        // List<TMLCPActivityDiagram> junctionsList = new
        // ArrayList<TMLCPActivityDiagram>();

        TMLCPActivityDiagram mainCP = tmlcp.getMainCP();

        // Checking mainCP
        List<TMLCPElement> currentListOfElements = mainCP.getElements();
        checkStartState(currentListOfElements, mainCP);
        checkDisconnectedSubParts(currentListOfElements, mainCP);
        checkDiagramsBetweenForkAndJoin(currentListOfElements, mainCP);

        // Checking the other ActivityDiagrams
        List<TMLCPActivityDiagram> listADs = tmlcp.getCPActivityDiagrams();
        for (TMLCPActivityDiagram diag : listADs) {
            currentListOfElements = diag.getElements();
            checkStartState(currentListOfElements, diag);
            checkDisconnectedSubParts(currentListOfElements, diag);
            checkDiagramsBetweenForkAndJoin(currentListOfElements, diag);
        }
        /*
         * for( TMLCPElement elem: listElements ) { if( elem instanceof
         * tmltranslator.tmlcp.TMLCPRefAD ) {
         * listElementsToCheck.add(((tmltranslator.tmlcp.TMLCPRefAD)elem).getName() ); }
         * if( elem instanceof tmltranslator.tmlcp.TMLCPRefSD ) {
         * listElementsToCheck.add(((tmltranslator.tmlcp.TMLCPRefSD)elem).getName() ); }
         * if( elem instanceof TMLCPConnector ) { listConnectorsStartEndNames.add(
         * ((TMLCPConnector)elem).getEndName() ); listConnectorsStartEndNames.add(
         * ((TMLCPConnector)elem).getStartName() ); } if( elem instanceof
         * tmltranslator.tmlcp.TMLCPChoice ) { ArrayList<String> guards = (
         * (tmltranslator.tmlcp.TMLCPChoice)elem ).getGuards(); listElementsToCheck.add(
         * ((tmltranslator.tmlcp.TMLCPChoice)elem).getName() ); } if( elem instanceof
         * tmltranslator.tmlcp.TMLCPFork ) { //ArrayList<String> guards = (
         * (tmltranslator.tmlcp.TMLCPFork)elem ).getGuards(); listElementsToCheck.add(
         * ((tmltranslator.tmlcp.TMLCPFork)elem).getName() ); } if( elem instanceof
         * tmltranslator.tmlcp.TMLCPJoin ) { //ArrayList<String> guards = (
         * (tmltranslator.tmlcp.TMLCPFork)elem ).getGuards(); listElementsToCheck.add(
         * ((tmltranslator.tmlcp.TMLCPJoin)elem).getName() ); } }
         * 
         * for( String s: listElementsToCheck ) { if(
         * !listConnectorsStartEndNames.contains(s) ) { addError( "Element <<" + s +
         * ">> in diagram <<" + mainCP.getName() + ">> is not connected",
         * TMLCPError.ERROR_STRUCTURE ); } }
         */
    }

    // Check that there is one and only one TMLCPStartState, if no start state or
    // multiple start states, an error is raised
    private void checkStartState(List<TMLCPElement> listElements, TMLCPActivityDiagram diag) {

        int startCounter = 0;
        for (TMLCPElement elem : diag.getElements()) {
            if (elem instanceof TMLCPStart) {
                startCounter++;
            }
        }
        if (startCounter > 1) {
            addError("Multiple start states in diagram <<" + diag.getName() + ">>", TMLCPError.ERROR_STRUCTURE);
        }
        if (startCounter == 0) {
            addError("No start state has been detected in diagram <<" + diag.getName() + ">>",
                    TMLCPError.ERROR_STRUCTURE);
        }
    }

    // Look for disconnected sub-graphs by detecting elements which do not appear in
    // the field next of any other element in the list
    // of elements
    private void checkDisconnectedSubParts(List<TMLCPElement> currentListOfElements, TMLCPActivityDiagram diag) {

        List<TMLCPElement> listOfElementsToCheck = currentListOfElements;
        int counter = 0;

        for (TMLCPElement currentElement : currentListOfElements) {
            if (!(currentElement instanceof TMLCPStart)) {
                for (TMLCPElement element : listOfElementsToCheck) {
                    List<TMLCPElement> nextElements = element.getNextElements();
                    if (!nextElements.contains(currentElement)) { // counting how many times currentElement is NOT
                                                                  // present as a
                                                                  // next element
                        counter++;
                    }
                }
            }
            // If currentElement is NEVER present as a next element it means its head is not
            // connected
            if (counter == currentListOfElements.size()) {
                addError("Element <<" + currentElement.toString() + ">> in diagram <<" + diag.getName()
                        + ">> is not correctly connected", TMLCPError.ERROR_STRUCTURE);
            }
            counter = 0;
        }
    }

    private void checkDiagramsBetweenForkAndJoin(List<TMLCPElement> currentListOfElements, TMLCPActivityDiagram diag) {

        List<TMLCPFork> listOfForks = new ArrayList<TMLCPFork>();
        List<TMLCPJoin> listOfJoins = new ArrayList<TMLCPJoin>();

        for (TMLCPElement element : currentListOfElements) {
            if (element instanceof TMLCPFork) {
                listOfForks.add((TMLCPFork) element);
            }
        }
        for (TMLCPFork fork : listOfForks) {
            for (TMLCPElement element : fork.getNextElements()) {
                TMLCPJoin joinNode = explorePath(element);
                if (joinNode == null) {
                    addError("Error in fork node <<" + element.toString() + ">> in diagram <<" + diag.getName(),
                            TMLCPError.ERROR_STRUCTURE);
                } else {
                    listOfJoins.add(joinNode);
                    if (Collections.frequency(listOfJoins, listOfJoins.get(0)) != listOfJoins.size()) {
                        addError("Error element <<" + element.toString() + ">> in diagram <<" + diag.getName()
                                + ">> is not connected to the right join node", TMLCPError.ERROR_STRUCTURE);
                        break;
                    }
                }
            }
            listOfJoins.clear();
        }
    }

    // Recursive function that explores the path from an element until a join node
    private TMLCPJoin explorePath(TMLCPElement element) {

        TMLCPJoin joinNode = new TMLCPJoin(element.toString(), element);
        if (element instanceof TMLCPJoin) { // stop condition
            joinNode = (TMLCPJoin) element;
        } else if ((element instanceof TMLCPRefSD) || (element instanceof TMLCPRefAD)) {
            joinNode = explorePath(element.getNextElements().get(0));
        } else {
            joinNode = null;
        }
        return joinNode;
    }

    // Check that all diagrams are connected by retrieving the list of diagrams and
    // checking if they appear as start or end name in
    // the list of connectors of the AD diagram under examination
    // At the same time, get the list of guards of choice elements
    // private void checkActivityDiagramsOLD() {
    //
    // List<TMLCPActivityDiagram> listADs = tmlcp.getCPActivityDiagrams();
    // List<TMLCPSequenceDiagram> listSDs = tmlcp.getCPSequenceDiagrams();
    // List<String> listConnectorsStartEndNames = new ArrayList<String>();
    // List<String> listDiagramNames = new ArrayList<String>();
    // List<String> localListOfSDDiagrams = new ArrayList<String>();
    // //check that all diagrams are connected
    //
    // for( TMLCPActivityDiagram diag: listADs ) {
    // List<TMLCPElement> listElements = diag.getElements();
    // for( TMLCPElement elem : listElements ) {
    // if( elem instanceof tmltranslator.tmlcp.TMLCPRefAD ) {
    // listDiagramNames.add(((tmltranslator.tmlcp.TMLCPRefAD)elem).getName() );
    // }
    // if( elem instanceof tmltranslator.tmlcp.TMLCPRefSD ) {
    // listDiagramNames.add(((tmltranslator.tmlcp.TMLCPRefSD)elem).getName() );
    // localListOfSDDiagrams.add( ( (tmltranslator.tmlcp.TMLCPRefSD)elem ).getName()
    // );
    // }
    // if( elem instanceof TMLCPConnector ) {
    // listConnectorsStartEndNames.add( ((TMLCPConnector)elem).getEndName() );
    // listConnectorsStartEndNames.add( ((TMLCPConnector)elem).getStartName() );
    // }
    // if( elem instanceof tmltranslator.tmlcp.TMLCPChoice ) {
    // List<String> guards = ( (tmltranslator.tmlcp.TMLCPChoice)elem ).getGuards();
    // Set<String> variableList = new HashSet<String>();
    // for( String guard: guards ) {
    // guard = guard.replaceAll("\\s+","");
    // if( guard.length() > 0 ) {
    // String[] token = guard.split( "\\[" );
    // if( token[1].equals("]") ) {
    // break;
    // }
    // String[] token1 = token[1].split("=");
    // if( token1.length > 1 ) {
    // variableList.add( token1[0] );
    // }
    // else {
    // String[] token2 = token1[0].split("<");
    // if( token2.length > 1 ) {
    // variableList.add( token2[0] );
    // }
    // else {
    // String[] token3 = token2[0].split(">");
    // variableList.add( token3[0] );
    // }
    // }
    // }
    // }
    // checkChoiceGuards( listSDs, variableList, localListOfSDDiagrams,
    // diag.getName() );
    // //check if they have been declared in the instances of a SD diagram
    // localListOfSDDiagrams = new ArrayList<String>();
    // variableList = new HashSet<String>();
    // /*for( TMLCPSequenceDiagram diagram: listSDDiagrams ) {
    // ArrayList<TMLAttribute> listAttributes = diagram.getAttributeList();
    // for( TMLAttribute attr: listAttributes ) {
    // if( !variableList.contains( attr.getName() ) ) {
    // addError( "Variable <<" + attr.getName() + ">> is not declared in any diagram
    // of <<" + diag.getName() + ">>",
    // TMLCPError.ERROR_STRUCTURE );
    // }
    // }
    // }*/
    // } //endOfLoop over elements
    //
    // }
    // for( String s: listDiagramNames ) {
    // if( !listConnectorsStartEndNames.contains(s) ) {
    // addError( "Diagram <<" + s + ">> in diagram <<" + diag.getName() + ">> is not
    // connected", TMLCPError.ERROR_STRUCTURE );
    // }
    // }
    // listConnectorsStartEndNames = new ArrayList<String>();
    // listDiagramNames = new ArrayList<String>();
    // }
    // }

    public void checkChoiceGuards(List<TMLCPSequenceDiagram> listSDs, Set<String> variableList,
            List<String> localListOfSDDiagrams, String diagName) {

        List<TMLAttribute> attributeList = new ArrayList<TMLAttribute>();
        for (String s : localListOfSDDiagrams) {
            for (TMLCPSequenceDiagram sdDiagram : listSDs) {
                if (sdDiagram.getName().equals(s)) {
                    attributeList = sdDiagram.getAttributes();
                }
            }
        }
        for (TMLAttribute attr : attributeList) {
            if (!variableList.contains(attr.getName())) {
                addError("Attribute <<" + attr.getName() + ">> is not declared in diagram <<" + diagName + ">>",
                        TMLCPError.ERROR_STRUCTURE);
            }
        }
    }

    // For each sequence diagram, check:
    // - that the parameters of messages have been declared as attributes of the
    // instances
    // - that actions are syntactically correct (no arit operations on boolean,
    // etc.)
    // - that no 2 or more instances have the same name
    private void checkSequenceDiagrams() {
        List<TMLCPSequenceDiagram> listSDs = tmlcp.getCPSequenceDiagrams();

        for (TMLCPSequenceDiagram diag : listSDs) {
            List<TMLAttribute> attributes = diag.getAttributes();
            // checkUniquenessOfAttributesNames( diag ); // already done in the GUI when
            // declaring attributes
            checkActions(diag, attributes); // actions must be done on variables that have
            checkMessages(diag); // check that attributes have been declared been declared and coherently boolean
                                 // = boolean + 6 is not allowed
            checkInstances(diag); // instances within the same SD must all have different names
        }
    }

    private void checkActions(TMLCPSequenceDiagram diag, List<TMLAttribute> attributes) {
        List<TMLSDAction> actions = diag.getActions();
        // boolean exists = false;
        for (TMLSDAction action : actions) {
            String[] array = action.toString().split("=");
            String temp = array[0].replaceAll("\\s+", "");
            for (TMLAttribute attribute : attributes) {
                if (attribute.getName().equals(temp)) {
                    if (attribute.isBool()) {
                        parsing("assbool", action.toString(), attributes);
                        // exists = true;
                        break;
                    }
                    if (attribute.isNat()) {
                        parsing("assnat", action.toString(), attributes);
                        // exists = true;
                        break;
                    }
                }
            }
        }
    }

    // Check that for each message parameter, the corresponding attribute has been
    // declared in both the sender
    // and the receiver instances with the same name
    private void checkMessages(TMLCPSequenceDiagram diag) {

        List<TMLSDMessage> messagesList = diag.getMessages();

        for (TMLSDMessage message : messagesList) {
            String senderInstance = message.getSenderName();
            String receiverInstance = message.getReceiverName();
            List<TMLAttribute> parametersList = message.getAttributes();
            for (TMLAttribute parameter : parametersList) {
                // skip numerical parameters
                if (!parameter.getName().matches("-?\\d+(\\.\\d+)?")) {
                    if (!isParameterDeclared(parameter, senderInstance, diag)) {
                        addError(
                                "Parameter <<" + parameter.getName() + ">> has not been declared in instance <<"
                                        + senderInstance + ">> in diagram <<" + diag.getName() + ">>",
                                TMLCPError.ERROR_STRUCTURE);
                    }
                    if (!isParameterDeclared(parameter, receiverInstance, diag)) {
                        addError(
                                "Parameter <<" + parameter.getName() + ">> has not been declared in instance <<"
                                        + receiverInstance + ">> in diagram <<" + diag.getName() + ">>",
                                TMLCPError.ERROR_STRUCTURE);
                    }
                    if (!checkTypeCoherency(parameter, senderInstance, receiverInstance, diag)) {
                        addError("Parameter <<" + parameter.getName()
                                + ">> is declared with different types in instance <<" + senderInstance
                                + ">> and in instance <<" + receiverInstance + ">> in diagram <<" + diag.getName()
                                + ">>", TMLCPError.ERROR_STRUCTURE);
                    }
                }
            }
        }
    }

    // Check that the parameter has been declared in the instance corresponding to
    // instanceName
    private boolean isParameterDeclared(TMLAttribute parameter, String instanceName, TMLCPSequenceDiagram diag) {

        for (TMLSDInstance instance : diag.getInstances()) {
            if (instance.getName().equals(instanceName)) {
                for (TMLAttribute attribute : instance.getAttributes()) { // don't use contains() as parameter is
                                                                          // created with
                                                                          // some partial attributes
                    if (attribute.getName().equals(parameter.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Check that the type of parameter is the samein both sender and receiver
    // instances
    private boolean checkTypeCoherency(TMLAttribute parameter, String senderInstance, String receiverInstance,
            TMLCPSequenceDiagram diag) {

        int typeOfAttributeInSenderInstance = 0;
        int typeOfAttributeInReceiverInstance = 0;

        for (TMLSDInstance instance : diag.getInstances()) {
            if (instance.getName().equals(senderInstance)) {
                for (TMLAttribute attribute : instance.getAttributes()) { // don't use contains() as parameter is
                                                                          // created with
                                                                          // some partial attributes
                    if (attribute.getName().equals(parameter.getName())) {
                        typeOfAttributeInSenderInstance = attribute.getType().getType();
                        break;
                    }
                }
            }
            if (instance.getName().equals(receiverInstance)) {
                for (TMLAttribute attribute : instance.getAttributes()) { // don't use contains() as parameter is
                                                                          // created with
                                                                          // some partial attributes
                    if (attribute.getName().equals(parameter.getName())) {
                        typeOfAttributeInReceiverInstance = attribute.getType().getType();
                        break;
                    }
                }
            }
        }
        return (typeOfAttributeInSenderInstance == typeOfAttributeInReceiverInstance);
    }

    private void checkInstances(TMLCPSequenceDiagram diag) {
        List<TMLSDInstance> instances = diag.getInstances();
        Set<String> hash = new HashSet<String>();
        for (TMLSDInstance instance : instances) {
            if (!hash.contains(instance.getName())) {
                hash.add(instance.getName());
            } else {
                addError("Instance <<" + instance.getName() + ">> is declared multiple times in diagram <<"
                        + diag.getName() + ">>", TMLCPError.ERROR_STRUCTURE);
            }
        }
    }

    public int hasErrors() {
        if (errors == null) {
            return 0;
        }
        return errors.size();
    }

    public int hasWarnings() {
        if (warnings == null) {
            return 0;
        }
        return warnings.size();
    }

    public List<TMLCPError> getErrors() {
        return errors;
    }

    public List<TMLCPError> getWarnings() {
        return warnings;
    }

    /*
     * public void addError( TMLTask t, TMLActivityElement elt, String message, int
     * type ) { TMLError error = new TMLError( type ); error.message = message;
     * error.task = t; error.element = elt; errors.add( error ); }
     */

    public void addError(String message, int type) {
        TMLCPError error = new TMLCPError(type);
        error.message = message;
        errors.add(error);
    }

    public String printSummary() {
        String ret = "";
        if (errors.size() == 0) {
            ret += printWarnings();
            ret += "Syntax checking: successful\n";
            ret += "No error, " + warnings.size() + " warning(s)\n";
        } else {
            ret += printErrors() + printWarnings();
            ret += "Syntax checking: failed\n";
            ret += errors.size() + " error(s), " + warnings.size() + " warning(s)\n";
        }
        return ret;
    }

    public String printErrors() {
        String ret = "*** ERRORS:";
        for (TMLCPError error : errors) {
            ret += "ERROR: " + error.message;
        }
        return ret;
    }

    public String printWarnings() {
        String ret = "";
        for (TMLCPError error : warnings) {
            ret += "ERROR / task " + error.task.getName() + " / element: " + error.element.getName() + ": "
                    + error.message + "\n";
        }
        return ret;
    }

    public void parsing(String parseCmd, String action, List<TMLAttribute> attributes) {
        TMLExprParser parser;
        SimpleNode root;

        // First parsing
        parser = new TMLExprParser(new StringReader(parseCmd + " " + action));
        try {
            // TraceManager.addDev("\nParsing :" + parseCmd + " " + action);
            root = parser.CompilationUnit();
            // root.dump("pref=");
            // TraceManager.addDev("Parse ok");
        } catch (ParseException e) {
            // TraceManager.addDev("ParseException --------> Parse error in :" + parseCmd +
            // " " + action);
            addError(SYNTAX_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
            return;
        } catch (TokenMgrError tke) {
            // TraceManager.addDev("TokenMgrError --------> Parse error in :" + parseCmd + "
            // " + action);
            addError(SYNTAX_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
            return;
        }

        // Second parsing
        // We only replace variables values after the "=" sign
        if (parseCmd.compareTo("natnumeral") == 0) {
            return;
        }

        int index = action.indexOf('=');
        String modif = action;

        if ((parseCmd.compareTo("assnat") == 0) || (parseCmd.compareTo("assbool") == 0)) {
            if (index != -1) {
                modif = action.substring(index + 1, action.length());
            }

            if (parseCmd.compareTo("assnat") == 0) {
                parseCmd = "actionnat";
            } else {
                parseCmd = "actionbool";
            }
        }

        if (parseCmd.compareTo("natid") == 0) {
            parseCmd = "natnumeral";
        }

        /*
         * for(TMLAttribute attr: t.getAttributeList()) { modif =
         * tmlm.putAttributeValueInString(modif, attr); }
         */
        parser = new TMLExprParser(new StringReader(parseCmd + " " + modif));
        try {
            // TraceManager.addDev("\nParsing :" + parseCmd + " " + modif);
            root = parser.CompilationUnit();
            // root.dump("pref=");
            // TraceManager.addDev("Parse ok");
        } catch (ParseException e) {
            // TraceManager.addDev("ParseException --------> Parse error in :" + parseCmd +
            // " " + action);
            addError(VARIABLE_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
            return;
        } catch (TokenMgrError tke) {
            // TraceManager.addDev("TokenMgrError --------> Parse error in :" + parseCmd + "
            // " + action);
            addError(VARIABLE_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
            return;
        }

        // Tree analysis: if the tree contains a variable, then, this variable has not
        // been declared
        List<String> vars = root.getVariables();

        // Do not raise a syntax error when variables appear in actions
        List<String> boolAttrNamesList = new ArrayList<String>(); // a list of the boolean attribute names
        List<String> natAttrNamesList = new ArrayList<String>(); // a list of the natural attribute names
        for (TMLAttribute attr : attributes) {
            if (attr.isNat()) {
                natAttrNamesList.add(attr.getName());
            }
            if (attr.isBool()) {
                boolAttrNamesList.add(attr.getName());
            }
        }
        if (parseCmd.equals("assnat")) {
            for (String s : vars) {
                if (!natAttrNamesList.contains(s)) {
                    if (boolAttrNamesList.contains(s)) {
                        addError(WRONG_VARIABLE_TYPE + " :" + s + " in expression " + action, TMLError.ERROR_BEHAVIOR);
                    } else {
                        addError(UNDECLARED_VARIABLE + " :" + s + " in expression " + action, TMLError.ERROR_BEHAVIOR);
                    }
                }
            }
        } else if (parseCmd.equals("assbool")) {
            for (String s : vars) {
                if (!boolAttrNamesList.contains(s)) {
                    if (natAttrNamesList.contains(s)) {
                        addError(WRONG_VARIABLE_TYPE + " :" + s + " in expression " + action, TMLError.ERROR_BEHAVIOR);
                    } else {
                        addError(UNDECLARED_VARIABLE + " :" + s + " in expression " + action, TMLError.ERROR_BEHAVIOR);
                    }
                }
            }
        }
    }
} // End of class
