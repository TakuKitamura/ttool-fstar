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

package translator;

import translator.Relation;
import translator.TClass;

/**
 * Class CheckingError Creation: 11/11/2003
 * 
 * @version 1.0 11/11/2003
 * @author Ludovic APVRILLE
 */
public class CheckingError extends Exception {

  public final static int STRUCTURE_ERROR = 0;
  public final static int BEHAVIOR_ERROR = 1;
  public final static int INFO = 2;

  protected int type;
  protected String message;
  protected TClass t; // only one if BEHAVIOR_ERROR,
  protected Relation r; // non-null if BEHAVIOR_ERROR

  public CheckingError(int _type, String _message) {
    type = _type;
    message = _message;
  }

  public CheckingError(CheckingError ce) {
    this.type = ce.getType();
    this.message = ce.getMessage();
    this.t = ce.getTClass();
    this.r = ce.getRelation();
  }

  public void setTClass(TClass _t) {
    t = _t;
  }

  public void addMessagePrefix(String prefix) {
    this.message = prefix + this.message;
  }

  public void setRelation(Relation _r) {
    r = _r;
  }

  public int getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

  public Relation getRelation() {
    return r;
  }

  public TClass getTClass() {
    return t;
  }

  public String toString() {
    if (t != null) {
      return t.getName() + ": " + message;
    }

    if (r != null) {
      return r.getName() + ": " + message;
    }

    return message;
  }
}
