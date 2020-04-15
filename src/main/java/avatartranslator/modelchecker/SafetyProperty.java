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

import java.time.Clock;

import avatartranslator.*;


/**
   * Class SafetyProperty
   * Coding of a safety property
   * Creation: 22/11/2017
   * @version 1.0 22/11/2017
   * @author Ludovic APVRILLE
 */
public class SafetyProperty  {
    // Error on property
    public static final int NO_ERROR = 0;
    public static final int BAD_SAFETY_TYPE = 1;
    public static final int BAD_PROPERTY_STRUCTURE = 1;

    // Type of safety
    public static final int ALLTRACES_ALLSTATES = 0;// A[]
    public static final int ALLTRACES_ONESTATE = 1; // A<>
    public static final int ONETRACE_ALLSTATES = 2; // E[]
    public static final int ONETRACE_ONESTATE = 3;  // E<>

    // Type of property
    public static final int BLOCK_STATE = 0;
    public static final int BOOL_EXPR = 1;
    
    
    private String rawProperty;

    private int safetyType;
    private int propertyType;
    private String p;
    public boolean result;

    private boolean isBlockStateProperty;
    private AvatarBlock block;
    private AvatarAttribute attribute;

    private int blockIndex;
    private int stateIndex;

    private int errorOnProperty;


    public SafetyProperty(String property, AvatarSpecification _spec) {
        rawProperty = property.trim();
        analyzeProperty(_spec);
        result = false;
    }

    public boolean analyzeProperty(AvatarSpecification _spec) {
    	String tmpP = rawProperty;
    	String[] pFields;
    	String blockString, fieldString;
    	
    	errorOnProperty = NO_ERROR;
    
    	if (tmpP.startsWith("A[]")) {
    	    safetyType = ALLTRACES_ALLSTATES;
    	} else if (tmpP.startsWith("A<>")) {
    	    safetyType = ALLTRACES_ONESTATE;
    	} else if (tmpP.startsWith("E[]")) {
    	    safetyType = ONETRACE_ALLSTATES;
    	} else if (tmpP.startsWith("E<>")) {
    	    safetyType = ONETRACE_ONESTATE;
    	} else {
    	    errorOnProperty = BAD_SAFETY_TYPE;
    	    return false;
    	}
    
    	p = tmpP.substring(3, tmpP.length()).trim();
    
    	if (p.length() == 0) {
    	    errorOnProperty = BAD_PROPERTY_STRUCTURE;
    	    return false;
    	}
    	
    	pFields = p.split(" "); //[0] item, [1] operator, [3] value
    	
    	if (pFields.length != 3 || pFields[0].split("\\.").length != 2) {
    	    errorOnProperty = BAD_PROPERTY_STRUCTURE;
            return false;
    	}
    	
    	blockString = pFields[0].split("\\.")[0];
    	fieldString = pFields[0].split("\\.")[1];
    	
    	block = _spec.getBlockWithName(blockString);
    	
    	if (block == null) {
    	    errorOnProperty = BAD_PROPERTY_STRUCTURE;
            return false;
    	}
    	
    	attribute = block.getAvatarAttributeWithName(fieldString);
    	
    	if (attribute == null) {
            errorOnProperty = BAD_PROPERTY_STRUCTURE;
            return false;
        }
    	
    	return (errorOnProperty == NO_ERROR);
    }

    
    public boolean hasError() {
        return errorOnProperty != NO_ERROR;
    }

    public void setErrorOnP() {
        errorOnProperty = BAD_PROPERTY_STRUCTURE;
    }

    public String getP() {
        return p;
    }
    

    public String toString() {
        if (result) {
            return rawProperty + " -> property is satisfied";
        } else {
            return rawProperty + " -> property is NOT satisfied";
        }
    }

}
