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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Class TMLCPLib: data structure for librairies of TMLCP Creation: 16/02/2015
 * 
 * @version 1.0 16/01/2015
 * @author Ludovic APVRILLE
 */
public class TMLCPLib extends TMLElement {

  private List<TMLCPLibArtifact> artifacts;
  private Vector<String> mappedUnits = new Vector<String>();
  private int cpMECType;
  private String typeName;
  private Vector<String> assignedAttributes;
  private List<Integer> transferTypes;

  public TMLCPLib(String _name, String _typeName, Object _referenceObject, int _cpMECType) {
    super(_name, _referenceObject);

    typeName = _typeName;
    cpMECType = _cpMECType;
    init();
  }

  public TMLCPLib() {
    super("DefaultCP", null); // no reference to any object in the default constructor

    init();
  }

  public void setMappedUnits(Vector<String> _mappedUnits) {
    mappedUnits = _mappedUnits;
  }

  private void init() {
    artifacts = new ArrayList<TMLCPLibArtifact>();
  }

  public void addArtifact(TMLCPLibArtifact _arti) {
    artifacts.add(_arti);
  }

  public List<TMLCPLibArtifact> getArtifacts() {
    return artifacts;
  }

  public Vector<String> getMappedUnits() {
    return mappedUnits;
  }

  public String getTypeName() {
    return typeName;
  }

  public boolean isDMATransfer() {
    return typeName.compareTo("DMA_transfer") == 0;
  }

  public boolean isDoubleDMATransfer() {
    return typeName.compareTo("Double_DMA_transfer") == 0;
  }

  public boolean isMemoryCopy() {
    return typeName.compareTo("CP_Memory_Copy") == 0;
  }

  public String getUnitByName(String id) {
    id = "." + id + " : ";
    for (String s : mappedUnits) {
      if (s.indexOf(id) > -1) {
        return s.substring(s.indexOf(":") + 1, s.length()).trim();
      }
    }
    return null;
  }

  public int getCPMECType() {
    return cpMECType;
  }

  public void setAssignedAttributes(Vector<String> _assignedAttributes) {
    assignedAttributes = _assignedAttributes;
  }

  public Vector<String> getAssignedAttributes() {
    return assignedAttributes;
  }

  public void setTransferTypes(List<Integer> _transferTypes) {
    transferTypes = _transferTypes;
  }

  public List<Integer> getTransferTypes() {
    return transferTypes;
  }

  public String toXML() {
    String s = "<TMLCPLIB name=\"" + name + "\" cpMECType=\"" + cpMECType + "\" typeName=\"" + typeName + "\">\n";
    if (artifacts != null) {
      for (TMLCPLibArtifact arti : artifacts) {
        s += arti.toXML();
      }
    }

    if (mappedUnits != null) {
      for (String mu : mappedUnits) {
        if (mu != null) {
          s += "<MAPPEUNIT unit=\"" + mu + "\" />\n";
        }
      }
    }

    if (assignedAttributes != null) {
      for (String aa : assignedAttributes) {
        if (aa != null) {
          s += "<ASSIGNEDATTRIBUTE attr=\"" + aa + "\" />\n";
        }
      }
    }

    if (transferTypes != null) {
      for (Integer i : transferTypes) {
        if (i != null) {
          s += "<TRANSFERTYPE type=\"" + i.toString() + "\" />\n";
        }
      }
    }
    s += "</TMLCPLIB>";
    return s;
  }
} // End of the class
