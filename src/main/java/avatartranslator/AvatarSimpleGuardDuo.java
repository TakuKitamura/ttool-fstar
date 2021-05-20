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

import java.util.Map;

/**
 * An AvatarSimpleGuardDuo is an {@link AvatarSimpleGuard} that is of the form:
 * {@link AvatarTerm} binaryOp {@link AvatarTerm} Creation: 25/09/2015
 * 
 * @version 1.0 25/09/2015
 * @author Florian LUGOU
 */
public class AvatarSimpleGuardDuo extends AvatarSimpleGuard {
  AvatarTerm termA;
  AvatarTerm termB;
  String binaryOp;

  public AvatarSimpleGuardDuo(AvatarTerm _termA, AvatarTerm _termB, String _binaryOp) {
    this.termA = _termA;
    this.termB = _termB;
    this.binaryOp = _binaryOp;
  }

  public AvatarTerm getTermA() {
    return this.termA;
  }

  public AvatarTerm getTermB() {
    return this.termB;
  }

  public String getBinaryOp() {
    return this.binaryOp;
  }

  public String getAsString(AvatarSyntaxTranslator translator) {
    return this.termA.getName() + " " + translator.translateBinaryOp(this.binaryOp) + " " + this.termB.getName();
  }

  @Override
  public AvatarSimpleGuardDuo clone() {
    return new AvatarSimpleGuardDuo(this.termA.clone(), this.termB.clone(), this.binaryOp);
  }

  @Override
  public void replaceAttributes(Map<AvatarAttribute, AvatarAttribute> attributesMapping) {
    if (this.termA instanceof AvatarAttribute) {
      this.termA = attributesMapping.get(this.termA);
    } else {
      this.termA.replaceAttributes(attributesMapping);
    }
    if (this.termB instanceof AvatarAttribute) {
      this.termB = attributesMapping.get(this.termB);
    } else {
      this.termB.replaceAttributes(attributesMapping);
    }
  }
}
