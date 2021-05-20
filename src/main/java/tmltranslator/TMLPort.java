/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * Andrea ENRICI, Nokia Bell Labs France
 *
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT nokia-bell-labs.com
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

/**
   * Class TMLPort
   * Creation: 16/02/2015
   * @version 1.0 16/02/2015
   * @author Ludovic APVRILLE, Andrea ENRICI
 */

package tmltranslator;

import ui.tmlcompd.TMLCPrimitivePort;

import java.util.Objects;

public class TMLPort extends TMLElement {

  private boolean prex;
  private boolean postex;
  private String associatedEvent;
  private String dataFlowType;

  public TMLPort(String _name, Object _referenceObject) {
    super(_name, _referenceObject);
    if (referenceObject instanceof TMLCPrimitivePort)
      dataFlowType = ((TMLCPrimitivePort) referenceObject).getDataFlowType();

  }

  public void setPrex(boolean _prex) {
    prex = _prex;

  }

  public boolean isPrex() {
    return prex;
  }

  public void setPostex(boolean _postex) {
    postex = _postex;

  }

  public boolean isPostex() {
    return postex;

  }

  public void setAssociatedEvent(String _eventName) {
    associatedEvent = _eventName;

  }

  public String getAssociatedEvent() {
    return associatedEvent;

  }

  public String getDataFlowType() {
    return dataFlowType;
  }

  public void setDataFlowType(String _dataFlowType) {
    dataFlowType = _dataFlowType;
  }

  public boolean equalSpec(Object o) {
    if (!(o instanceof TMLPort))
      return false;
    if (!super.equalSpec(o))
      return false;

    TMLPort tmlPort = (TMLPort) o;
    return prex == tmlPort.isPrex() && postex == tmlPort.isPostex()
        && Objects.equals(associatedEvent, tmlPort.getAssociatedEvent())
        && Objects.equals(dataFlowType, tmlPort.getDataFlowType());
  }
}
