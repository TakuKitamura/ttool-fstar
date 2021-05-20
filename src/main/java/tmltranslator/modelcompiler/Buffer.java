/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enstr.fr
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

package tmltranslator.modelcompiler;

import tmltranslator.TMLCPLibArtifact;
import tmltranslator.TMLPort;
import tmltranslator.TMLTask;

import java.util.List;

/**
 * Class Buffer Creation: 11/02/2014
 * 
 * @version 1.0 11/02/2014
 * @author Andrea ENRICI
 */
public class Buffer implements CCodeGenConstants {

    protected static final int NUM_SAMPLES_INDEX = 1;
    protected static final int BASE_ADDRESS_INDEX = 2;

    public static final String[] MEMORY_TYPES = { "FEP memory", "MAPPER memory", "ADAIF memory", "INTERLEAVER memory",
            "MAIN MEMORY memory" };
    public static final String[] ON_OFF_VALUES = { "ON", "OFF" };
    public static final int ANOMALY = -1;
    public static final int FEP_BUFFER = 0;
    public static final int MAPPER_BUFFER = 1;
    public static final int ADAIF_BUFFER = 2;
    public static final int INTERLEAVER_BUFFER = 3;
    public static final int MAIN_MEMORY_BUFFER = 4;
    public static final int BASE_BUFFER = 5;

    public static final int BUFFER_TYPE_INDEX = 0; // the index of the buffer type in bufferParameters. The latter is
                                                   // retrieved from the xml description of a design

    // public static String CR = "\n";
    // public static String CR2 = "\n\n";
    // public static String TAB = "\t";
    // public static String TAB2 = "\t\t";
    // public static String SP = " ";
    // public static String SC = ";";

    // protected static String USER_TO_DO = " 0 /* USER TODO: VALUE */";
    protected String code = "VOID";
    protected String name = "";
    protected String type = "";
    protected TMLTask task;
    protected TMLPort port;
    protected TMLCPLibArtifact artifact;
    protected String baseAddress = DEFAULT_NUM_VAL + USER_TO_DO;// " 0 /* USER TODO: VALUE */";
    protected String endAddress = baseAddress;// " 0 /* USER TODO: VALUE */";
    protected List<String> bufferParameters;

    // private String Context = "";

    public Buffer() {
        code = "struct" + SP + name + TAB + "{" + CR + "}" + SC;
    }

    @Override
    public String toString() {
        if (port != null) {
            if (artifact != null) {
                return "buff__" + port.getName() + " mapped onto " + artifact.getMemoryName();
            } else {
                return "buff__" + port.getName();
            }
        } else {
            String s = type + SP + "BUFFER" + SP + name + CR;
            return s;
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public TMLTask getTask() {
        return task;
    }

    public void addMappingArtifact(TMLCPLibArtifact _artifact) {
        artifact = _artifact;
    }

    public TMLCPLibArtifact getMappingArtifact() {
        return artifact;
    }

    public void setStartAddress(String _baseAddress) {
        baseAddress = _baseAddress;
    }

    public void setEndAddress(String _endAddress) {
        endAddress = _endAddress;
    }

    public String getInitCode() {
        StringBuffer s = new StringBuffer();
        s.append(TAB + name + ".baseAddress = " + baseAddress + SC + CR);
        return s.toString();
    }
    //
    // public String getContext() {
    // return Context;
    // }

    public void setMappingParameters(List<String> params) {
        bufferParameters = params;
    }
} // End of class
