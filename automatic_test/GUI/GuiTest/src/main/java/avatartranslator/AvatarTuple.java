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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
   * Class AvatarTuple
   * Creation: 16/09/2015
   * @version 1.0 16/09/2015
   * @author Florian LUGOU
 */
public class AvatarTuple extends AvatarLeftHand {
    List<AvatarTerm> components;

    public AvatarTuple (Object _referenceObject) {
        super (null, _referenceObject);
        this.components = new LinkedList<AvatarTerm> ();
    }

    public static AvatarTuple createFromString (AvatarStateMachineOwner block, String toParse) {
        AvatarTuple result = null;

        if (toParse.trim().startsWith("(")) {
            int indexLParen = toParse.indexOf ("(");
            int indexRParen = toParse.indexOf (")", indexLParen);
            if (indexRParen == -1)
                indexRParen = toParse.length ();
            String[] components = toParse.substring (indexLParen+1, indexRParen).trim().split (",");
            boolean illFormed = false;
            AvatarTuple argsTuple = new AvatarTuple (block);
            for (String arg: components) {
                if (!arg.isEmpty()) {
                    // TraceManager.addDev("In for with arg=" + arg+"|");
                    AvatarTerm t = AvatarTerm.createFromString (block, arg);
                    if (t == null) {
                        // Term couldn't be parsed
                        illFormed = true;
                        break;
                    }

                    argsTuple.addComponent (t);
                }
            }

            if (!illFormed)
                // Every argument was correctly parsed
                result = argsTuple;
        }

        return result;
    }

    public void addComponent (AvatarTerm term) {
        this.components.add (term);
    }

    public List<AvatarTerm> getComponents () {
        return this.components;
    }

    public String getName () {
        return this.toString ();
    }

    @Override
    public String toString () {
        String result = "(";
        boolean first = true;
        for (AvatarTerm term: components) {
            if (first)
                first = false;
            else
                result += ", ";
            result += term.getName ();
        }

        return result + ")";
    }

    public boolean isLeftHand () {
        for (AvatarTerm term: this.components)
            if (!(term instanceof AvatarAttribute))
                return false;
        return true;
    }

    @Override
    public boolean containsAMethodCall () {
        for (AvatarTerm term: this.components)
            if (term.containsAMethodCall ())
                return true;
        return false;
    }

    @Override
    public AvatarTuple clone () {
        AvatarTuple clone = new AvatarTuple (this.referenceObject);
        for (AvatarTerm term: this.components)
            clone.addComponent (term.clone ());
        return clone;
    }

    @Override
    public void replaceAttributes( Map<AvatarAttribute, AvatarAttribute> attributesMapping) {
        List<AvatarTerm> components = new LinkedList<AvatarTerm> ();
        for (AvatarTerm term: this.components)
            if (term instanceof AvatarAttribute)
                components.add (attributesMapping.get (term));
            else {
                components.add (term);
                term.replaceAttributes (attributesMapping);
            }
        this.components = components;
    }
}
