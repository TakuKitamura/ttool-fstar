/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

   ludovic.apvrille AT enst.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.

   /**
   * Class AvatarTermFunction
   * Creation: 16/09/2015
   * @version 1.0 16/09/2015
   * @author Florian LUGOU
   * @see
   */

package avatartranslator;

import java.util.HashMap;
import java.util.LinkedList;

import myutil.TraceManager;

public class AvatarTermFunction extends AvatarTerm implements AvatarAction {
    AvatarTuple args;
    AvatarMethod method;

    public AvatarTermFunction (AvatarMethod _method, AvatarTuple _args, Object _referenceObject) {
        super (_method.getName () + " " + _args.toString (), _referenceObject);
        this.args = _args;
        this.method = _method;
    }

    public static AvatarTermFunction createFromString (AvatarBlock block, String toParse) {
        int indexLParen = toParse.indexOf ("(");
        String methodName;
        AvatarTuple argsTuple;

        if (indexLParen == -1) {
            // No left parenthesis: this must be a 0-arity function call
            methodName = toParse.trim ();
            argsTuple = new AvatarTuple (block);
        }
        else {
            // Left parenthesis present
            methodName = toParse.substring (0, indexLParen).trim ();
            argsTuple = AvatarTuple.createFromString (block, toParse.substring (indexLParen));
        }

        AvatarMethod meth = block.getAvatarMethodWithName (methodName);
        if (meth != null && argsTuple != null && meth.getListOfAttributes ().size () == argsTuple.getComponents ().size ()){
            // Method was found and the arguments provided are correct
        TraceManager.addDev ("Function call '" + toParse + "' added parsed");
            return new AvatarTermFunction (meth, argsTuple, block);
}
        TraceManager.addDev ("Function call '" + toParse + "' couldn't be parsed");

        return null;
    }

    public AvatarMethod getMethod () {
        return this.method;
    }

    public AvatarTuple getArgs () {
        return this.args;
    }

    public void addArgument (AvatarTerm term) {
        this.args.addComponent (term);
    }

    public boolean isAMethodCall () {
        return true;
    }

    public boolean isAVariableSetting () {
        return false;
    }

    public boolean isABasicVariableSetting () {
        return false;
    }

    public boolean isLeftHand () {
        return false;
    }

    public String toString () {
        return this.method.getName () + " " + this.args.toString ();
    }

    @Override
    public AvatarTermFunction clone () {
        return new AvatarTermFunction (this.method, this.args.clone (), this.referenceObject);
    }

    @Override
    public void replaceAttributes (HashMap<AvatarAttribute, AvatarAttribute> attributesMapping) {
        this.args.replaceAttributes (attributesMapping);
    }
}
