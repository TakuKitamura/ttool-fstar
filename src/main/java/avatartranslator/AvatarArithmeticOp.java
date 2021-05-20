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

package avatartranslator;

import myutil.TraceManager;

import java.util.Map;

/**
 * Class AvatarArithmeticOp Creation: 16/09/2015
 * 
 * @version 1.0 16/09/2015
 * @author Florian LUGOU
 */
public class AvatarArithmeticOp extends AvatarTerm {
  AvatarTerm term1;
  AvatarTerm term2;
  String operator;

  private static final String[] knownOp = { "+", "-", "*" };

  public AvatarArithmeticOp(AvatarTerm _term1, AvatarTerm _term2, String _operator, Object _referenceObject) {
    super(_term1.getName() + _operator + _term2.getName(), _referenceObject);
    this.operator = _operator;
    this.term1 = _term1;
    this.term2 = _term2;
  }

  public static AvatarArithmeticOp createFromString(AvatarStateMachineOwner block, String toParse) {
    for (String op : AvatarArithmeticOp.knownOp) {
      int indexOp = toParse.indexOf(op);
      if (indexOp != -1) {
        AvatarTerm t1, t2;
        t1 = AvatarTerm.createFromString(block, toParse.substring(0, indexOp).trim());
        if (t1 == null)
          continue;

        t2 = AvatarTerm.createFromString(block, toParse.substring(indexOp + op.length()).trim());
        if (t2 == null)
          continue;

        return new AvatarArithmeticOp(t1, t2, op, block);
      }
    }

    return null;
  }

  public String getOperator() {
    return this.operator;
  }

  public AvatarTerm getTerm1() {
    return this.term1;
  }

  public AvatarTerm getTerm2() {
    return this.term2;
  }

  public String toString() {
    return this.term1.getName() + operator + this.term2.getName();
  }

  public boolean isLeftHand() {
    return false;
  }

  @Override
  public boolean containsAMethodCall() {
    return this.term1.containsAMethodCall() || this.term2.containsAMethodCall();
  }

  @Override
  public AvatarArithmeticOp clone() {
    return new AvatarArithmeticOp(this.term1.clone(), this.term2.clone(), this.operator, this.referenceObject);
  }

  @Override
  public void replaceAttributes(Map<AvatarAttribute, AvatarAttribute> attributesMapping) {

    // TraceManager.addDev("Replace Attribute term1=" +
    // term1.getClass().getCanonicalName() + " / " + term1.getName());

    if (this.term1 instanceof AvatarAttribute) {
      // TraceManager.addDev("Found an attribute: " + this.term1.getName());

      AvatarAttribute at = attributesMapping.get(this.term1);
      if (at == null) {
        // Search by name
        for (AvatarAttribute atbis : attributesMapping.keySet()) {
          if (atbis.getName().equals(this.term1.getName())) {
            at = attributesMapping.get(atbis);
            break;
          }
        }

      }

      if (at == null) {
        // TraceManager.addDev("No correspondance for " + this.term1.getName());
      } else {
        // TraceManager.addDev("Replaced with: " + at.getClass().getCanonicalName() +
        // " / " + at.getName());
        this.term1 = at;
        name = this.toString();
        // TraceManager.addDev("Next expr " + this.toString() + " name of var=" +
        // this.term1.getName());
      }

    } else {
      this.term1.replaceAttributes(attributesMapping);
    }

    // TraceManager.addDev("Replace Attribute term2=" +
    // term2.getClass().getCanonicalName() + " / " + term2.getName());

    if (this.term2 instanceof AvatarAttribute) {
      AvatarAttribute at = attributesMapping.get(this.term2);
      if (at == null) {
        // Search by name
        for (AvatarAttribute atbis : attributesMapping.keySet()) {
          if (atbis.getName().equals(this.term2.getName())) {
            at = attributesMapping.get(atbis);
            break;
          }
        }
      }

      if (at == null) {
        // TraceManager.addDev("No correspondance for " + this.term2.getName());
      } else {
        // TraceManager.addDev("Replaced with: " + at.getClass().getCanonicalName() +
        // " / " + at.getName());
        this.term2 = at;
        name = this.toString();
        // TraceManager.addDev("Next expr " + this.toString() + " name of var=" +
        // this.term2.getName());
      }

    } else {
      this.term2.replaceAttributes(attributesMapping);
    }

    // TraceManager.addDev("Expr: " + this.toString());
  }
}
