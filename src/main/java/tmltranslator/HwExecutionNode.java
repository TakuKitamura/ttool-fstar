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

package tmltranslator;

import java.util.Objects;

/**
 * Class HwExecutionNode Creation: 23/11/2007
 * 
 * @version 1.0 05/03/2019
 * @author Ludovic APVRILLE
 */
public abstract class HwExecutionNode extends HwNode {
  protected int maximumNbOfTasks;

  public static final int DEFAULT_EXECI_TIME = 1;
  public int execiTime = DEFAULT_EXECI_TIME;

  public static final int DEFAULT_EXECC_TIME = 1;
  public int execcTime = DEFAULT_EXECC_TIME;

  protected String operation = ""; // empty means all

  public HwExecutionNode(String _name) {
    super(_name);
  }

  public abstract String getType();

  public int getExeciTime() {
    return execiTime;
  }

  public int getExeccTime() {
    return execcTime;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getOperation() {
    return operation;
  }

  public boolean supportOperation(String opType) {
    if (operation.length() == 0) {
      return true;
    }

    String[] ops = operation.split(" ");
    for (int i = 0; i < ops.length; i++) {
      if (ops[i].compareTo(opType) == 0) {
        return true;
      }
    }

    return false;
  }

  public boolean equalSpec(Object o) {
    if (!(o instanceof HwExecutionNode))
      return false;
    if (!super.equalSpec(o))
      return false;
    HwExecutionNode that = (HwExecutionNode) o;
    return maximumNbOfTasks == that.maximumNbOfTasks && execiTime == that.execiTime && execcTime == that.execcTime
        && operation.equals(that.operation);
  }

}
