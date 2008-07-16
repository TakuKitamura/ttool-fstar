/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class JavaClass
 * Creation: 03/03/2005
 * @version 1.0 03/03/2005
 * @author Ludovic APVRILLE
 * @see
 */

package translator.tojava;

import java.util.*;

import myutil.*;

public class JavaClass {
    
    private String TURTLEName;
    private String javaName;
    private String header = "";
	private String userHeader = "";
    private String importCode = "";
    private String classDeclationCode = "";
    private String attributesCode = "";
    private String operationCode = "";
    private String startingSequenceCode = "";
    private String startingPreemptionCode = "";
    private String classEndCode = "}";
    private String path = "";
    private String packageName;
    
    private Vector attributes;
    private Vector gates;
    private Vector operations;
    
    private int operationId;
    
    private boolean active;
    
    public final static String JAVA_EXTENSION = "java";
    
    public JavaClass(String _TURTLEName, boolean _active) {
        attributes = new Vector();
        gates = new Vector();
        operations = new Vector();
        TURTLEName = _TURTLEName;
        active = _active;
        generateJavaName();
        addStateAndGoAttribute();
    }
	
	public void setUserHeader(String _header) {
		userHeader = "// User headers \n" + _header + "\n// End of user headers\n\n";
		//System.out.println("user header = " + userHeader);
	}
    
    public boolean isActive() {
        return active;
    }
    
    public void generateJavaName() {
        if ((TURTLEName != null) && (TURTLEName.length() > 1))
            javaName = TURTLEName.substring(0, 1).toUpperCase() + TURTLEName.substring(1, TURTLEName.length());
        else
            javaName = TURTLEName;
    }
    
    public void addStateAndGoAttribute() {
        /*JAttribute ja = new JAttribute(TURTLE2Java.T__STATE, "nat", "0");
        attributes.add(ja);
        ja = new JAttribute(TURTLE2Java.T__GO, "bool", "true");
        attributes.add(ja);*/
    }
    
    public String getJavaName() {
        return javaName;
    }
    
    public String getTURTLEName() {
        return TURTLEName;
    }
    
    private int getUniqueOperationId() {
        operationId ++;
        return operationId-1;
    }
    
    public void setDeclarationCode(String _code) {
        classDeclationCode = _code;
    }
    
    public void addImportCode(String _code) {
        importCode += _code;
    }
    
    public void addAttributeCode(String _code) {
        attributesCode += _code;
    }
    
    public void addOperationCode(String _code) {
        operationCode += _code;
    }
    
    public void addStartingSequenceCode(String _code) {
        operationCode += _code;
    }
    
    public void addStartingPreemptionCode(String _code) {
        operationCode += _code;
    }
    
    public void setHeader(String _header) {
        header = _header;
    }
    
    public String getfullCode() {
        return header + "\n\n" + userHeader + importCode + "\n\n" + classDeclationCode + "\n\n" + attributesCode + "\n\n" + operationCode + "\n\n" + startingPreemptionCode + "\n\n" + "\n\n" + startingSequenceCode + classEndCode;
    }
    
    public String toString() {
        return getfullCode();
    }
    
    public void saveAsFileIn(String _path) throws FileException {
        path = _path;
        FileUtils.saveFile(path + javaName + "." + JAVA_EXTENSION, getfullCode());
    }
    
    public void addAttribute(JAttribute ja) {
        attributes.add(ja);
    }
    
    public int getAttributeNb() {
        return attributes.size();
    }
    
    public JAttribute getAttributeAt(int index) {
        return (JAttribute)(attributes.elementAt(index));
    }
    
    public void addGate(JGate jg) {
        gates.add(jg);
    }
    
    public int getGateNb() {
        return gates.size();
    }
    
    public JGate getGateAt(int index) {
        return (JGate)(gates.elementAt(index));
    }
    
    public JGate foundJGate(String name) {
        
        for(int i=0; i<getGateNb(); i++) {
            if (getGateAt(i).getName().equals(name)) {
                return getGateAt(i);
            }
        }
        
        return null;
    }
    
    public void addOperation(JOperation jo) {
        operations.add(jo);
    }
    
    public int getOperationNb() {
        return operations.size();
    }
    
    public JOperation getOperationAt(int index) {
        return (JOperation)(operations.elementAt(index));
    }
    
    public void generateAttributeDeclaration() {
        JAttribute ja;
        
        for(int i=0; i<attributes.size(); i++) {
            ja = getAttributeAt(i);
            addAttributeCode(JKeyword.INDENT + ja.getJavaDeclaration() + "\n");
        }
        addAttributeCode("\n");
    }
    
    public void generateGateDeclaration() {
        JGate jg;
        
        addImportCode("import jttool.*;\n");
        
        addAttributeCode(JKeyword.INDENT + "/* Internal and external Gates */\n");
        for(int i=0; i<gates.size(); i++) {
            jg = getGateAt(i);
            addAttributeCode(JKeyword.INDENT + jg.getJavaDeclaration() + "\n");
        }
        addAttributeCode("\n");
    }
    
    public void generateJTToolAttributes() {
        addAttributeCode(JKeyword.INDENT + "/* Attributes for TURTLE operator management */\n");
        addAttributeCode(JKeyword.INDENT + "private SynchroSchemes __sss = new SynchroSchemes();\n");
        addAttributeCode(JKeyword.INDENT + "private SynchroScheme __ss;\n");
        addAttributeCode(JKeyword.INDENT + "private int __nchoice;\n");
        addAttributeCode(JKeyword.INDENT + "private boolean __bchoice[];\n");
        addAttributeCode(JKeyword.INDENT + "private JGate __bchoice__name[];\n");
        addAttributeCode(JKeyword.INDENT + "private long __bchoice__mindelay[];\n");
        addAttributeCode(JKeyword.INDENT + "private long __bchoice__maxdelay[];\n");
        addAttributeCode(JKeyword.INDENT + "private SynchroSchemes __bchoice__synchro[];\n");
        addAttributeCode(JKeyword.INDENT + "private SynchroSchemes [] __ssss;\n");
        addAttributeCode(JKeyword.INDENT + "private int __cpt1, __cpt2;\n");
    }
    
    public void generateConstructor() {
        int i;
        JAttribute ja;
        JGate jg;
        String intermediateCode = "";
        
        addOperationCode("\n" + JKeyword.INDENT + "/* Constructor */\n");
        addOperationCode(JKeyword.INDENT + JKeyword.PUBLIC + " " + javaName + "(");
        
        for(i=0; i<getAttributeNb(); i++) {
            ja = getAttributeAt(i);
            addOperationCode(ja.getType() + " " + TURTLE2Java.ATTR_DEC + ja.getJavaName());
            if ( i == (getAttributeNb() -1)){
                if (getGateNb() > 0) {
                    addOperationCode(JKeyword.ATTRIBUTE_SEP + " ");
                }
            } else {
                addOperationCode(JKeyword.ATTRIBUTE_SEP + " ");
            }
            intermediateCode += JKeyword.INDENT + JKeyword.INDENT + ja.getJavaName() + " " + JKeyword.ATTRIBUTE_AFFECT + " " + TURTLE2Java.ATTR_DEC + ja.getJavaName() + JKeyword.END_OP + "\n";
        }
        
        for(i=0; i<getGateNb(); i++) {
            jg = getGateAt(i);
            addOperationCode(TURTLE2Java.JGATE + " " + TURTLE2Java.ATTR_DEC + jg.getJName());
            if (i != (getGateNb() -1)) {
                addOperationCode(JKeyword.ATTRIBUTE_SEP + " ");
            }
            intermediateCode += JKeyword.INDENT + JKeyword.INDENT + jg.getJName() + " " + JKeyword.ATTRIBUTE_AFFECT + " " + TURTLE2Java.ATTR_DEC + jg.getJName() + JKeyword.END_OP + "\n";
        }
        
        addOperationCode(") " + JKeyword.START_CODE + "\n");
        addOperationCode(intermediateCode);
        addOperationCode("\n" + JKeyword.INDENT + JKeyword.STOP_CODE);
    }
    
    public String getCallToOp(int index) {
        String s = "";
        int i;
        JAttribute ja;
        JGate jg;
        
        JOperation jo = getOperationAt(index);
        s += jo.getName() + "()" + JKeyword.END_OP_N;
        return s;
    }
    public String getDeclarationOfOp(int index) {
        String s = "";
        int i;
        JAttribute ja;
        JGate jg;
        
        JOperation jo = getOperationAt(index);
        s += JKeyword.INDENT + JKeyword.PRIVATE + " " + JKeyword.VOID + " " + jo.getName() + "(";
        s += ")" + TURTLE2Java.TH_EXCEPTION + JKeyword.START_CODE_N;
        
        return s;
    }
    
    public void generateAllOperations() {
        JOperation jo;
        
        addOperationCode("\n");
        
        for(int i=0; i<getOperationNb(); i++) {
            jo = getOperationAt(i);
            addOperationCode("\n" + jo.code + "\n");
        }
    }
    
    public String getCreationCode(String nameInstance) {
        return getCreationCodeWithSpecialState(nameInstance, 0, 2);
    }
    
    public String getCreationCodeWithSpecialState(String nameInstance, int valueState, int dec) {
        String s = "";
        JAttribute ja;
        JGate jg;
        int i;
        
        while (dec > 0) {
            s += JKeyword.INDENT;
            dec --;
        }
        s +=  getJavaName() + " " + nameInstance + " = new " + getJavaName() + "(";
        for(i=0; i<getAttributeNb(); i++) {
            ja = getAttributeAt(i);
            if (ja.getName().equals(TURTLE2Java.T__STATE)) {
                s += valueState;
            } else {
                s += ja.getValue();
            }
            if ( i == (getAttributeNb() -1)){
                if (getGateNb() > 0) {
                    s += JKeyword.ATTRIBUTE_SEP + " ";
                }
            } else {
                s += JKeyword.ATTRIBUTE_SEP + " ";
            }
        }
        
        for(i=0; i<getGateNb(); i++) {
            jg = getGateAt(i);
            s += jg.getJName();
            if (i != (getGateNb() -1)) {
                s += JKeyword.ATTRIBUTE_SEP + " ";
            } else {
                s += " ";
            }
        }
        
        s += ")" + JKeyword.END_OP;
        
        return s;
    }
    
    public String getCreationCodeWithSpecialStateCurrentValues(String nameInstance, int valueState, int dec) {
        String s = "";
        JAttribute ja;
        JGate jg;
        int i;
        
        while (dec > 0) {
            s += JKeyword.INDENT;
            dec --;
        }
        s +=  getJavaName() + " " + nameInstance + " = new " + getJavaName() + "(";
        for(i=0; i<getAttributeNb(); i++) {
            ja = getAttributeAt(i);
            if (ja.getName().equals(TURTLE2Java.T__STATE)) {
                s += valueState;
            } else {
                s += ja.getName();
            }
            if ( i == (getAttributeNb() -1)){
                if (getGateNb() > 0) {
                    s += JKeyword.ATTRIBUTE_SEP + " ";
                }
            } else {
                s += JKeyword.ATTRIBUTE_SEP + " ";
            }
        }
        
        for(i=0; i<getGateNb(); i++) {
            jg = getGateAt(i);
            s += jg.getJName();
            if (i != (getGateNb() -1)) {
                s += JKeyword.ATTRIBUTE_SEP + " ";
            } else {
                s += " ";
            }
        }
        
        s += ")" + JKeyword.END_OP;
        
        return s;
    }
    
    public void setPackageName(String _name) {
        packageName = _name;
    }
    
    public String getPackageName() {
        return packageName;
    }
}