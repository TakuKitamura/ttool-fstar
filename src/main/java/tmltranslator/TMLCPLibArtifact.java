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

import java.util.List;

/**
 * Class TMLCPLibArtifact: Artifact mapped on a TMLCPLib Creation: 16/02/2015
 * 
 * @version 1.0 16/02/2015
 * @author Ludovic APVRILLE
 */
public class TMLCPLibArtifact extends TMLElement {

    public String taskName;
    public String portName;
    public String memoryName;
    public int priority;
    private List<String> bufferParameters;

    public TMLCPLibArtifact(String _name, Object _referenceObject, String _taskName, String _portName,
            String _memoryName, int _priority, List<String> _bufferParameters) {
        super(_name, _referenceObject);
        taskName = _taskName;
        portName = _portName;
        memoryName = _memoryName;
        priority = _priority;
        bufferParameters = _bufferParameters;
    }

    public TMLCPLibArtifact() {
        super("DefaultCP", null); // no reference to any object in the default constructor
    }

    public String getTaskName() {
        return taskName;
    }

    public String getPortName() {
        return portName;
    }

    public String getMemoryName() {
        return memoryName;
    }

    public int getPriority() {
        return priority;
    }

    public List<String> getBufferParameters() {
        return bufferParameters;
    }

    public String toXML() {
        String s = "<TMLCPLIBARTIFACT taskName=\"" + taskName + "\" portName=\"" + portName + "\" memoryName=\""
                + memoryName + "\" priority=\"" + priority + "\" >\n";
        for (String bp : bufferParameters) {
            s += "<BUFFERPARAMETERS param=\"" + bp + "\" />\n";
        }
        s += "</TMLCPLIBARTIFACT>\n";
        return s;
    }
} // End of the class
