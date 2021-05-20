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

/**
 * Class AvatarError Creation: 20/09/2020
 *
 * @author Ludovic APVRILLE
 * @version 1.0 20/08/2020
 */
public class AvatarError {

  public final static String[] errorStrings = { "Invalid number of signals in the two sides of the relation",
      "Signals are connected but there are not compatible because they are both IN signals",
      "Signals are connected but there are not compatible because they are both OUT signals",
      "Signals are connected but there have non compatible parameters", "Missing block1 declaration in relation",
      "Missing block2 declaration in relation", // 5
      "No signal of that name in block", "State machine must terminate with a stop state" };

  public int error;
  public AvatarSpecification avspec;
  public AvatarBlock block;
  public AvatarRelation relation;
  public AvatarElement firstAvatarElement;
  public AvatarElement secondAvatarElement;

  public AvatarError(AvatarSpecification _avspec) {
    avspec = _avspec;
  }

  public String toString() {
    String ret = "";
    if (block != null) {
      ret += block.getName() + " | ";
    }
    if (relation != null) {
      ret += "relation " + relation.block1.getName() + " <> " + relation.block1.getName() + " | ";
    }

    if (firstAvatarElement != null) {
      ret += " for " + firstAvatarElement.getName() + " | ";
    }

    if (secondAvatarElement != null) {
      ret += " and for " + secondAvatarElement.getName() + " | ";
    }

    if (error < errorStrings.length) {
      ret += " : " + errorStrings[error];
    }

    return ret;
  }

}
