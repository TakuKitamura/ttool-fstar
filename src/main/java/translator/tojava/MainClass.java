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

package translator.tojava;

import myutil.FileException;
import myutil.FileUtils;

/**
 * Class MainClass Creation: 07/03/2005
 * 
 * @version 1.0 07/03/2005
 * @author Ludovic APVRILLE
 */
public class MainClass {

    private String javaName;
    private String header = "";
    private String importCode = "";
    private String classDeclationCode = "";
    private String attributesCode = "";
    private String operationCode = "";
    private String gateCode = "";
    private String synchroCode = "";
    private String startingCode = "";
    private String classEndCode = "}";
    private String path = "";

    private int jgateId;

    public final static String JAVA_EXTENSION = "java";

    public MainClass(String _name) {
        javaName = _name;
    }

    public String getName() {
        return javaName;
    }

    public int getUniqueGateId() {
        jgateId++;
        return jgateId - 1;
    }

    private int getGateId() {
        return jgateId;
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

    public void addGateCode(String _code) {
        gateCode += _code;
    }

    public void addSynchroCode(String _code) {
        synchroCode += _code;
    }

    public void addStartingCode(String _code) {
        startingCode += _code;
    }

    public void setHeader(String _header) {
        header = _header;
    }

    public String getfullCode() {
        return header + "\n\n" + importCode + "\n\n" + classDeclationCode + "\n\n" + attributesCode + "\n\n"
                + operationCode + "\n\n" + classEndCode;
    }

    public String toString() {
        return getfullCode();
    }

    public void saveAsFileIn(String _path) throws FileException {
        path = _path;
        FileUtils.saveFile(path + javaName + "." + JAVA_EXTENSION, getfullCode());
    }

    public void generateBasicCode() {
        addImportCode("import jttool.*;\nimport java.util.*;\n");
        setDeclarationCode(TURTLE2Java.DECL_CODE_01 + getName() + " " + translator.JKeyword.START_CODE);
        addAttributeCode("private static LinkedList internalThreads = new LinkedList();\n");
    }

    public void generateOperationCode() {
        addOperationCode(translator.JKeyword.INDENT + translator.JKeyword.PUBLIC + " " + translator.JKeyword.STATIC
                + " " + " void " + "main(String[] args)" + translator.JKeyword.START_CODE + "\n");
        addOperationCode("\n" + translator.JKeyword.INDENT + "/* JGate: creation */\n");
        addOperationCode(gateCode);
        addOperationCode("\n\n" + translator.JKeyword.INDENT + "/* JGate: synchronization */\n");
        addOperationCode(synchroCode);
        addOperationCode("\n\n" + translator.JKeyword.INDENT + "/* Starting instances */\n");
        addOperationCode(startingCode);
        addOperationCode("\n\n" + translator.JKeyword.INDENT + translator.JKeyword.STOP_CODE);
    }

}