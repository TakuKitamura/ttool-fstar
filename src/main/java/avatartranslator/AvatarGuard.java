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

import myutil.Conversion;
import myutil.TraceManager;

import java.util.Map;

/**
 * Class AvatarGuard
 * Creation: 16/09/2015
 *
 * @author Florian LUGOU
 * @version 1.0 16/09/2015
 */
public abstract class AvatarGuard {

    public static int getMatchingRParen(String s, int indexLParen) {
        int index, n;
        n = 1;
        for (index = indexLParen + 1; index < s.length(); index++) {
            if (s.charAt(index) == '(')
                n++;
            else if (s.charAt(index) == ')')
                n--;
            if (n == 0)
                return index;
        }

        return -1;
    }

    public static AvatarGuard createFromString(AvatarStateMachineOwner block, String _guard) {
        if (_guard == null)
            return new AvatarGuardEmpty();

        String sane = AvatarGuard.sanitizeString(_guard);
        if (sane.isEmpty())
            return new AvatarGuardEmpty();

        if (sane.toLowerCase().equals("else"))
            return new AvatarGuardElse();

        int indexRParen = 0;
        AvatarTuple tuple = null;

        AvatarGuard first = null;
        if (sane.startsWith("not(")) {
            indexRParen = AvatarGuard.getMatchingRParen(sane, 3);
            if (indexRParen < 0) {
                TraceManager.addDev("1. Invalid guard expression with tuple " + sane);
                return new AvatarGuardEmpty();
            }
            first = AvatarGuard.createFromString(block, sane.substring(4, indexRParen));

            if (indexRParen >= sane.length() - 1) {
                if (first instanceof AvatarComposedGuard)
                    return new AvatarUnaryGuard("not", "(", ")", (AvatarComposedGuard) first);
                else {
                    TraceManager.addDev("Could not create unary guard " + sane);
                    return new AvatarGuardEmpty();
                }
            }
        }

        if (sane.startsWith("(")) {
            indexRParen = AvatarGuard.getMatchingRParen(sane, 0);
            if (indexRParen < 0) {
                TraceManager.addDev("2. Invalid guard expression with tuple " + sane);
                return new AvatarGuardEmpty();
            }
            tuple = AvatarTuple.createFromString(block, sane.substring(0, indexRParen));
            if (tuple == null) {
                first = AvatarGuard.createFromString(block, sane.substring(1, indexRParen));

                if (indexRParen == sane.length() - 1) {
                    if (first instanceof AvatarComposedGuard)
                        return new AvatarUnaryGuard("", "(", ")", (AvatarComposedGuard) first);
                    else {
                        TraceManager.addDev("Unary guard " + sane + " does not contain guard");
                        return new AvatarGuardEmpty();
                    }
                } else {
                    int indexLParen = sane.indexOf("(", indexRParen);
                    if (indexLParen == -1)
                        indexLParen = indexRParen;

                    for (String delim: new String[]{"and", "or", "&&", "||"}) {

                        int indexBinaryOp = sane.substring(0, indexLParen).indexOf(delim, indexRParen + 1);
                        if (indexBinaryOp != -1) {
                            first = AvatarGuard.createFromString(block, sane.substring(0, indexBinaryOp));
                            AvatarGuard second = AvatarGuard.createFromString(block, sane.substring(indexBinaryOp + delim.length()));
                            if (first instanceof AvatarComposedGuard && second instanceof AvatarComposedGuard)
                                return new AvatarBinaryGuard((AvatarComposedGuard) first, (AvatarComposedGuard) second, delim);
                            TraceManager.addDev("Binary guard " + sane + "does not contain 2 guards");
                            return new AvatarGuardEmpty();
                        }
                    }
                    TraceManager.addDev("3. Invalid guard " + sane);
                    return new AvatarGuardEmpty();
                }
            } else if (tuple.getComponents().size() == 1) {
                first = AvatarGuard.createFromString(block, sane.substring(1, indexRParen));
                if (indexRParen == sane.length() - 1) {
                    if (first instanceof AvatarComposedGuard)
                        return new AvatarUnaryGuard("", "(", ")", (AvatarComposedGuard) first);
                    else {
                        TraceManager.addDev("Unary guard " + sane + " does not contain guard");
                        return new AvatarGuardEmpty();
                    }
                }
                int indexLParen = sane.indexOf("(", indexRParen);
                if (indexLParen == -1)
                    indexLParen = indexRParen;

                for (String delim : new String[]{"and", "or", "&&", "||"}) {

                    int indexBinaryOp = sane.substring(0, indexLParen).indexOf(delim, indexRParen + 1);
                    if (indexBinaryOp != -1) {
                        first = AvatarGuard.createFromString(block, sane.substring(0, indexBinaryOp));
                        AvatarGuard second = AvatarGuard.createFromString(block, sane.substring(indexBinaryOp + delim.length()));
                        if (first instanceof AvatarComposedGuard && second instanceof AvatarComposedGuard)
                            return new AvatarBinaryGuard((AvatarComposedGuard) first, (AvatarComposedGuard) second, delim);
                        TraceManager.addDev("Binary guard " + sane + "does not contain 2 guards");
                        return new AvatarGuardEmpty();
                    }
                }
                TraceManager.addDev("4. Invalid guard " + sane);
                return new AvatarGuardEmpty();
            } else {
                int indexLParen = sane.indexOf("(", indexRParen);
                if (indexLParen == -1)
                    indexLParen = indexRParen;

                for (String delim : new String[]{"==", "!="}) {
                    int indexBinaryOp = sane.substring(0, indexLParen).indexOf(delim, indexRParen + 1);
                    if (indexBinaryOp != -1) {
                        AvatarTerm secondTerm = AvatarTerm.createFromString(block, sane.substring(indexBinaryOp + delim.length()));
                        if (secondTerm != null)
                            return new AvatarSimpleGuardDuo(tuple, secondTerm, delim);
                        TraceManager.addDev("Could not find term in guard " + sane);
                        return new AvatarGuardEmpty();
                    }
                }

                TraceManager.addDev("5. Invalid guard expression with tuple " + sane);
                return new AvatarGuardEmpty();
            }
        }

        for (String delim : new String[]{"==", "!=", "<=", ">=", "<", ">"}) {
            int indexBinaryOp = sane.indexOf(delim);
            if (indexBinaryOp != -1) {
                AvatarTerm firstTerm = AvatarTerm.createFromString(block, sane.substring(0, indexBinaryOp));
                AvatarTerm secondTerm = AvatarTerm.createFromString(block, sane.substring(indexBinaryOp + delim.length()));
                if (secondTerm != null && firstTerm != null)
                    return new AvatarSimpleGuardDuo(firstTerm, secondTerm, delim);
                // TraceManager.addDev("Term in guard does not exist " +sane.substring (0, indexBinaryOp) + " "+ sane.substring (indexBinaryOp + delim.length ()));
                return new AvatarGuardEmpty();
            }
        }

        AvatarTerm term = AvatarTerm.createFromString(block, sane);
        if (term != null)
            return new AvatarSimpleGuardMono(term);

        // TODO: add warning
        TraceManager.addDev("Could not parse guard '" + sane + "'. Replacing by an empty guard.");
        return new AvatarGuardEmpty();
    }

    private static String sanitizeString(String s) {
        String result = Conversion.replaceAllChar(s, ' ', "").trim();
        result = Conversion.replaceAllChar(result, '[', "");
        result = Conversion.replaceAllChar(result, ']', "");

        return result;
    }

    public static AvatarGuard addGuard(AvatarGuard _guard, AvatarGuard _g, String _binaryOp) {
        if (_g == null || !(_g instanceof AvatarComposedGuard) || !(_guard instanceof AvatarComposedGuard))
            return _guard;

        return new AvatarBinaryGuard(new AvatarUnaryGuard("", "(", ")", (AvatarComposedGuard) _guard),
                new AvatarUnaryGuard("", "(", ")", (AvatarComposedGuard) _g),
                _binaryOp);
    }

    public AvatarGuard getRealGuard(AvatarStateMachineElement precedent) {
        return this;
    }

    public boolean isElseGuard() {
        return false;
    }

    public boolean isGuarded() {
        return true;
    }

    public abstract String getAsString(AvatarSyntaxTranslator translator);

    @Override
    public String toString() {
        return this.getAsString(new AvatarSyntaxTranslator());
    }

    /**
     * Returns a full clone of the guard.
     *
     * @return A clone of the guard.
     */
    public abstract AvatarGuard clone();

    /**
     * Replaces attributes in this guard according to the provided mapping.
     *
     * @param attributesMapping The mapping used to replace the attributes of the guard. All the attributes of the block should be present as keys.
     */
    public abstract void replaceAttributes(Map<AvatarAttribute, AvatarAttribute> attributesMapping);
}
