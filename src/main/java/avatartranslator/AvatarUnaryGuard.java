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
   * Class AvatarUnaryGuard
   * Creation: 16/09/2015
   * @version 1.0 16/09/2015
   * @author Florian LUGOU
 */
public class AvatarUnaryGuard extends AvatarComposedGuard {
    AvatarComposedGuard guard;

    String unary;

    String before;
    String after;

    public AvatarUnaryGuard (String _unary, String _before, String _after, AvatarComposedGuard _guard) {
        this.unary = _unary;
        this.before = _before;
        this.after = _after;
        this.guard = _guard;
    }

    public String getUnaryOp () {
        return this.unary;
    }

    public String getBefore () {
        return this.before;
    }

    public String getAfter () {
        return this.after;
    }

    public AvatarComposedGuard getGuard () {
        return this.guard;
    }

    public AvatarComposedGuard getOpposite () {
        if (this.unary.equals ("not")) {
            return this.guard;
        }
        return new AvatarUnaryGuard ("not", "(", ")", this);
    }

    public String getAsString (AvatarSyntaxTranslator translator) {
        return translator.translateUnaryOp (this.unary) + this.before + this.guard.getAsString (translator) + this.after;
    }

    @Override
    public AvatarUnaryGuard clone () {
        return new AvatarUnaryGuard (this.unary, this.before, this.after, this.guard.clone ());
    }

    @Override
    public void replaceAttributes( Map<AvatarAttribute, AvatarAttribute> attributesMapping) {
        this.guard.replaceAttributes (attributesMapping);
    }
}
