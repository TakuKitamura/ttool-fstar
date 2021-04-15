/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
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

package avatartranslator.modelchecker;

import avatartranslator.*;
import myutil.TraceManager;


/**
   * Class SafetyProperty
   * Coding of a safety or liveness property
   * Creation: 22/11/2017
   * @version 1.0 24/08/2020
   * @author Ludovic APVRILLE, Alessandro TEMPIA CALVINO
 */
public class SafetyProperty  {

    private String rawProperty;
    private String refProperty;
    private int errorOnProperty;
    private AvatarExpressionSolver safetySolver;
    private AvatarExpressionSolver safetySolverLead;
    private SpecificationPropertyPhase phase;
    private AvatarStateMachineElement state;
    
    // Error on property
    public static final int NO_ERROR = 0;
    public static final int BAD_SAFETY_TYPE = 1;
    public static final int BAD_PROPERTY_STRUCTURE = 1;

    // Type of safety
    public static final int ALLTRACES_ALLSTATES = 0;// A[] p
    public static final int ALLTRACES_ONESTATE = 1; // A<> p
    public static final int ONETRACE_ALLSTATES = 2; // E[] p
    public static final int ONETRACE_ONESTATE = 3;  // E<> p
    public static final int LEADS_TO = 4;           // p --> q

    // Type of property
    public static final int BLOCK_STATE = 0;
    public static final int BOOL_EXPR = 1;
    
    public int safetyType;
    public int propertyType;
    public int leadType;
    public boolean result;
    public boolean expectedResult;
    
    public SafetyProperty(String property, String _refProperty) {
        TraceManager.addDev("Adding safety pragma with prop=" + property + " ref=" + _refProperty);
        rawProperty = property;
        refProperty = _refProperty;
        expectedResult = true;
        phase = SpecificationPropertyPhase.NOTCOMPUTED;
        /* to manually analyze after the AvatarSpecification is ready
           for model-checking in order to build correct expression solvers */
    }

    public SafetyProperty(String property) {
        rawProperty = property;
        refProperty = property;
        expectedResult = true;
        phase = SpecificationPropertyPhase.NOTCOMPUTED;
        /* to manually analyze after the AvatarSpecification is ready
           for model-checking in order to build correct expression solvers */
    }
    
    public SafetyProperty(AvatarBlock block, AvatarStateMachineElement state, int _safetyType) {
        //create liveness safety
        AvatarExpressionAttribute attribute = new AvatarExpressionAttribute(block, state);
        safetySolver = new AvatarExpressionSolver();
        safetySolver.buildExpression(attribute);
        propertyType = BLOCK_STATE;
        safetyType = _safetyType;
        result = true;
        expectedResult = true;
        phase = SpecificationPropertyPhase.NOTCOMPUTED;
        rawProperty = "Element " + state.getExtendedName() + " of block " + block.getName();
        this.state = state;
    }

    public boolean analyzeProperty(AvatarSpecification _spec) {
    	String tmpP = rawProperty.trim();
    	String p;
    	
    	tmpP = checkExpectedResult(tmpP);
    	
    	if (!initType(tmpP)) {
    	    return false;
    	}
    	
    	if (safetyType != LEADS_TO) {
    	    p = tmpP.substring(3, tmpP.length()).trim();
    	    initSafetyTrace(_spec, p);
    	} else {
    	    p = tmpP;
    	    initSafetyLeads(_spec, p);
    	}
    	
    	
    	return (errorOnProperty == NO_ERROR);
    }
    
    //for local variables inside a block
    public boolean analyzeProperty(AvatarBlock block, AvatarSpecification _spec) {
        String tmpP = rawProperty.trim();
        String p;
        
        tmpP = checkExpectedResult(tmpP);
        
        if (!initType(tmpP)) {
            return false;
        }
        
        if (safetyType != LEADS_TO) {
            p = tmpP.substring(3, tmpP.length()).trim();
            initSafetyTrace(block, _spec, p);
        } else {
            p = tmpP;
            initSafetyLeads(block, _spec, p);
        }
        
        
        return (errorOnProperty == NO_ERROR);
    }
 
    
    public void initLead() {
        AvatarExpressionSolver tmp;
        int type;
        tmp = safetySolver;
        safetySolver = safetySolverLead;
        safetySolverLead = tmp;
        safetyType = ALLTRACES_ONESTATE;
        type = propertyType;
        propertyType = leadType;
        leadType = type;
        result = true;
    }
    
    public void restoreLead() {
        //to be used only after initLead()
        AvatarExpressionSolver tmp;
        int type;
        tmp = safetySolver;
        safetySolver = safetySolverLead;
        safetySolverLead = tmp;
        safetyType = LEADS_TO;
        type = propertyType;
        propertyType = leadType;
        leadType = type;
        result = true;
    }

    
    public boolean hasError() {
        return errorOnProperty != NO_ERROR;
    }

    
    public void setErrorOnP() {
        errorOnProperty = BAD_PROPERTY_STRUCTURE;
    }
    
    
    public String getRawProperty() {
        return rawProperty;
    }

    public String getRefProperty() {
        return refProperty;
    }
    
    
    public boolean getSolverResult(SpecificationState _ss) {
        return safetySolver.getResult(_ss) != 0;
    }
    
    
    public boolean getSolverResult(SpecificationState _ss, AvatarStateMachineElement _asme) {
        return safetySolver.getResult(_ss, _asme) != 0;
    }
    
    
    public boolean getSolverLeadResult(SpecificationState _ss, AvatarStateMachineElement _asme) {
        return safetySolverLead.getResult(_ss, _asme) != 0;
    }
    
    
    public SpecificationPropertyPhase getPhase() {
        return phase;
    }
    
    
    public void setComputed() {
        if (result == expectedResult) {
            phase = SpecificationPropertyPhase.SATISFIED;
        } else {
            phase = SpecificationPropertyPhase.NONSATISFIED;
        }
    }
    
    
    public AvatarStateMachineElement getState() {
        return state;
    }
    
    
    public void linkSolverStates() {
        //linking to states so that normal bool elaborations are possible
        if (safetySolver != null) {
            safetySolver.linkStates();
        }
        if (safetySolverLead != null) {
            safetySolverLead.linkStates();
        }
    }
    
    
    public String toString() {
        String ret = rawProperty;
        switch(phase) {
        case NOTCOMPUTED:
            ret += " -> property not computed";
            break;
        case SATISFIED:
            ret += " -> property is satisfied";
            break;
        case NONSATISFIED:
            ret += " -> property is NOT satisfied"; 
            break;
        }
        return ret;
    }
    
    
    public String toLivenessString() {
        String name = rawProperty;
        switch(phase) {
        case NOTCOMPUTED:
            name += " -> liveness not computed";
            break;
        case SATISFIED:
            name += " -> liveness is satisfied";
            break;
        case NONSATISFIED:
            name += " -> liveness is NOT satisfied"; 
            break;
        }
        return name;
    }
    
    private String checkExpectedResult(String tmpP) {
        if (tmpP.startsWith("T ") || tmpP.startsWith("t ")) {
            expectedResult = true;
            return tmpP.substring(2).trim();
        } else if (tmpP.startsWith("F ") || tmpP.startsWith("f ")) {
            expectedResult = false;
            return tmpP.substring(2).trim();
        }
        
        return tmpP;
    }
    
    private boolean initType(String tmpP) {
        errorOnProperty = NO_ERROR;
        
        if (tmpP.startsWith("A[]")) {
            safetyType = ALLTRACES_ALLSTATES;
            result = true;
        } else if (tmpP.startsWith("A<>")) {
            safetyType = ALLTRACES_ONESTATE;
            result = true;
        } else if (tmpP.startsWith("E[]")) {
            safetyType = ONETRACE_ALLSTATES;
            result = false;
        } else if (tmpP.startsWith("E<>")) {
            safetyType = ONETRACE_ONESTATE;
            result = false;
        } else if (tmpP.contains("-->")){
            safetyType = LEADS_TO;
            result = true;
        } else {
            errorOnProperty = BAD_SAFETY_TYPE;
            result = false;
            return false;
        }
        return true;
    }
    
    
    private boolean initSafetyTrace(AvatarSpecification _spec, String p) {      
        safetySolver = new AvatarExpressionSolver(p);
        boolean exprRet = safetySolver.buildExpression(_spec);
        
        if (exprRet == false) {
            errorOnProperty = BAD_PROPERTY_STRUCTURE;
        }
        
        if (safetySolver.hasState()) {
            propertyType = BLOCK_STATE;
        } else {
            propertyType = BOOL_EXPR;
        }
        
        return exprRet;
    }
    
    private boolean initSafetyTrace(AvatarBlock block, AvatarSpecification _spec, String p) {      
        safetySolver = new AvatarExpressionSolver(p);
        boolean exprRet = safetySolver.buildExpression(block);
        
        if (exprRet == false) {
            errorOnProperty = BAD_PROPERTY_STRUCTURE;
        }
        
        safetySolver.setBlockIndex(_spec);
        
        if (safetySolver.hasState()) {
            propertyType = BLOCK_STATE;
        } else {
            propertyType = BOOL_EXPR;
        }
        
        return exprRet;
    }
    
    
    private boolean initSafetyLeads(AvatarSpecification _spec, String p) {
        String[] pFields;
        String pp, pq;
        boolean exprRet;
        
        pFields = p.split("-->");
        if (pFields.length != 2) {
            errorOnProperty = BAD_PROPERTY_STRUCTURE;
            return false;
        }

        pp = pFields[0].trim();
        pq = pFields[1].trim();
        
        safetySolver = new AvatarExpressionSolver(pp);
        exprRet = safetySolver.buildExpression(_spec);
    
        if (exprRet == false) {
            errorOnProperty = BAD_PROPERTY_STRUCTURE;
            return false;
        }
        
        safetySolverLead = new AvatarExpressionSolver(pq);
        exprRet = safetySolverLead.buildExpression(_spec);

        if (exprRet == false) {
            errorOnProperty = BAD_PROPERTY_STRUCTURE;
            return false;
        }
        
        if (safetySolver.hasState()) {
            propertyType = BLOCK_STATE;
        } else {
            propertyType = BOOL_EXPR;
        }
        
        if (safetySolverLead.hasState()) {
            leadType = BLOCK_STATE;
        } else {
            leadType = BOOL_EXPR;
        }
        
        return true;
    }
    
    private boolean initSafetyLeads(AvatarBlock block, AvatarSpecification _spec, String p) {
        String[] pFields;
        String pp, pq;
        boolean exprRet;
        
        pFields = p.split("-->");
        if (pFields.length != 2) {
            errorOnProperty = BAD_PROPERTY_STRUCTURE;
            return false;
        }

        pp = pFields[0].trim();
        pq = pFields[1].trim();
        
        safetySolver = new AvatarExpressionSolver(pp);
        exprRet = safetySolver.buildExpression(block);
    
        if (exprRet == false) {
            errorOnProperty = BAD_PROPERTY_STRUCTURE;
            return false;
        }
        
        safetySolver.setBlockIndex(_spec);
        
        safetySolverLead = new AvatarExpressionSolver(pq);
        exprRet = safetySolverLead.buildExpression(block);

        if (exprRet == false) {
            errorOnProperty = BAD_PROPERTY_STRUCTURE;
            return false;
        }
        
        safetySolverLead.setBlockIndex(_spec);
        
        if (safetySolver.hasState()) {
            propertyType = BLOCK_STATE;
        } else {
            propertyType = BOOL_EXPR;
        }
        
        if (safetySolverLead.hasState()) {
            leadType = BLOCK_STATE;
        } else {
            leadType = BOOL_EXPR;
        }
        
        return true;
    }

}
