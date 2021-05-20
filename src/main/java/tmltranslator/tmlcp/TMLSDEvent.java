/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enst.fr
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

package tmltranslator.tmlcp;

/**
 * Class TMLSDEvent. An event is either a message or an action. This class is
 * used to produce the TML code corresponding to messages and actions that are
 * sorted according to the graphical version of a SD diagram. Creation:
 * 18/02/2014
 * 
 * @version 1.0 26/06/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLSDEvent implements Comparable<TMLSDEvent> {

  public final static int SEND_MESSAGE_EVENT = 0;
  public final static int RECEIVE_MESSAGE_EVENT = 1;
  public final static int ACTION_EVENT = 2;

  private final static String SEND_MESSAGE_LABEL = "SND:";
  private final static String RECEIVE_MESSAGE_LABEL = "RCV:";
  private final static String ACTION_LABEL = "ACT:";
  private final static String ERROR = "ERROR_IN_EVENT";
  private int type;
  private int yCoord;
  private Object referenceObject;

  public TMLSDEvent(Object _referenceObject, int _type, int _yCoord) {
    this.referenceObject = _referenceObject;
    this.yCoord = _yCoord;
    this.type = _type;
  }

  public TMLSDEvent(Object _referenceObject, int _type) {
    this.referenceObject = _referenceObject;
    this.yCoord = -1;
    this.type = _type;
  }

  public int getYCoord() {
    return yCoord;
  }

  public int getType() {
    return this.type;
  }

  public Object getReferenceObject() {
    return this.referenceObject;
  }

  @Override
  public int compareTo(TMLSDEvent _event) {
    // TraceManager.addDev("Comparing events");
    int compareValue = _event.getYCoord();
    return this.yCoord - compareValue; // sort in ascending order
  }

  /*
   * public static Comparator<TMLSDEvent> yCoordComparator = new
   * Comparator<TMLSDEvent>() { public int compare( TMLSDEvent _item1, TMLSDEvent
   * _item2 ) { int yCoord1 = _item1.getYCoord(); int yCoord2 =
   * _item2.getYCoord();
   * 
   * //ascending order return yCoord1.compareTo( yCoord2 ); } };
   */

  @Override
  public String toString() {

    TMLSDMessage msg;

    switch (type) {
      case 0: // send message
        msg = ((TMLSDMessage) referenceObject);
        return SEND_MESSAGE_LABEL + msg.getReceiverName() + ":" + msg.toString();
      case 1: // receive message
        msg = ((TMLSDMessage) referenceObject);
        return RECEIVE_MESSAGE_LABEL + msg.getSenderName() + ":" + msg.toString();
      case 2: // action
        TMLSDAction action = ((TMLSDAction) referenceObject);
        return ACTION_LABEL + action.toString();
      default:
        return ERROR;
    }
  }
} // End of class
