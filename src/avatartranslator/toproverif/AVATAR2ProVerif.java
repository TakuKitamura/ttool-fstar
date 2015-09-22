/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class AVATAR2ProVerif
 * Creation: 03/09/2010
 * @version 1.1 03/09/2010
 * @author Ludovic APVRILLE
 * @see
 */

package avatartranslator.toproverif;

import java.awt.*;
import java.util.*;

import proverifspec.*;
import myutil.*;
import avatartranslator.*;
import ui.*;

public class AVATAR2ProVerif implements AvatarTranslator {

    private static int GENERAL_ID = 0;

    private final static String UNKNOWN = "UNKNOWN";

    private final static String TRUE = "TRUE";
    private final static String FALSE = "FALSE";

    private final static String PK_PK = "pk";
    private final static String PK_ENCRYPT = "aencrypt";
    private final static String PK_DECRYPT = "adecrypt";
    private final static String PK_SIGN = "sign";
    private final static String PK_VERIFYSIGN = "verifySign";

    private final static String CERT_CERT = "cert";
    private final static String CERT_VERIFYCERT = "verifyCert";
    private final static String CERT_GETPK = "getpk";

    private final static String SK_ENCRYPT = "sencrypt";
    private final static String SK_DECRYPT = "sdecrypt";

    private final static String MAC_MAC = "MAC";
    private final static String MAC_VERIFYMAC = "verifyMAC";

    private final static String HASH_HASH = "hash";

    private final static String CH_MAINCH = "ch";
    private final static String CH_ENCRYPT = "privChEnc";
    private final static String CH_DECRYPT = "privChDec";

    private final static String CHCTRL_CH = "chControl";
    private final static String CHCTRL_ENCRYPT = "chControlEnc";
    private final static String CHCTRL_DECRYPT = "chControlDec";

    private ProVerifSpec spec;
    private AvatarSpecification avspec;
    private Hashtable<String, Integer> macs;

    private LinkedList<ProVerifVar> sessionKnowledge;

    protected Hashtable<String, String> declarations;

    private Vector warnings;

    private boolean advancedTranslation;

    public AVATAR2ProVerif(AvatarSpecification _avspec) {
        avspec = _avspec;
    }

    public void saveInFile(String path) throws FileException {
        FileUtils.saveFile(path + "pvspec", spec.getStringSpec ());
    }

    public Vector getWarnings() {
        return warnings;
    }

    public ProVerifSpec generateProVerif(boolean _debug, boolean _optimize, boolean _stateReachability, boolean _advancedTranslation, boolean _typed) {
        advancedTranslation = _advancedTranslation;
        GENERAL_ID = 0;

        macs = new Hashtable<String, Integer>();
        declarations = new Hashtable<String, String>();

        warnings = new Vector();
        if (_typed)
            spec = new ProVerifSpec (new ProVerifPitypeSyntaxer ());
        else
            spec = new ProVerifSpec (new ProVerifPiSyntaxer ());

        avspec.removeCompositeStates();
        avspec.removeTimers();
        avspec.removeElseGuards();

        makeHeader(_stateReachability);

        makeStartingProcess();

        makeBlocks();

        return spec;
    }

    private static String makeAttrName(String _block, String _attribute) {
        return _block + "__" + _attribute;
    }

    private ProVerifProcInstr makeActionFromBlockParam(String _block, String _param) {
        String tmp = makeAttrName(_block, _param);
        String tmpH = declarations.get(tmp);
        if (tmpH == null) {
            declarations.put(tmp, tmp);
            return new ProVerifProcNew (tmp, "bitstring");
        }

        return null;
    }

    private ProVerifProcInstr makeLetActionFromBlockParam(String _block, String _param, String known) {
        String tmp = makeAttrName(_block, _param);
        String tmpH = declarations.get(tmp);
        if (tmpH == null) {
            declarations.put(tmp, tmp);
            return new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar (tmp, "bitstring")}, known);
        }

        return null;
    }

    private void addDeclarationsFromList(int startIndex, String[] list, String result) {
        String tmp, blockName, paramName;
        String tmp1;
        int index;

        for(int i=startIndex; i<list.length; i++) {
            tmp = list[i];
            index = tmp.indexOf('.');
            if (index != -1) {
                blockName = tmp.substring(0, index).trim();
                paramName = tmp.substring(index+1);
                tmp1 = makeAttrName(blockName, paramName);
                if (tmp1 != null)
                    declarations.put(tmp1, result);
            }
        }
    }

    public void makeHeader(boolean _stateReachability) {
        TraceManager.addDev("\n\n=+=+=+ Making Headers +=+=+=");
        spec.addDeclaration (new ProVerifComment    ("Boolean return types"));
        spec.addDeclaration (new ProVerifConst      (TRUE, "bitstring"));
        spec.addDeclaration (new ProVerifConst      (FALSE, "bitstring"));
        spec.addDeclaration (new ProVerifComment    ("Functions data"));
        spec.addDeclaration (new ProVerifConst      (UNKNOWN, "bitstring"));

        spec.addDeclaration (new ProVerifComment    ("Public key cryptography"));
        spec.addDeclaration (new ProVerifFunc       (PK_PK, new String[] {"bitstring"}, "bitstring"));
        spec.addDeclaration (new ProVerifFunc       (PK_ENCRYPT, new String[] {"bitstring", "bitstring"}, "bitstring"));
        spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring"), new ProVerifVar ("y", "bitstring")}, PK_DECRYPT + "(" + PK_ENCRYPT + "(x," + PK_PK + "(y)),y) = x"));
        spec.addDeclaration (new ProVerifFunc       (PK_SIGN, new String[] {"bitstring", "bitstring"}, "bitstring"));
        spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("m", "bitstring"), new ProVerifVar ("sk", "bitstring")}, PK_VERIFYSIGN + "(m," + PK_SIGN + "(m,sk)," + PK_PK + "(sk))=" + TRUE));

        spec.addDeclaration (new ProVerifComment    ("Certificates"));
        spec.addDeclaration (new ProVerifFunc       (CERT_CERT, new String[] {"bitstring", "bitstring"}, "bitstring"));
        spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("epk", "bitstring"), new ProVerifVar ("sk", "bitstring")}, CERT_VERIFYCERT + "(" + CERT_CERT + "(epk," + PK_SIGN + "(epk,sk))," + PK_PK + "(sk))=" + TRUE));
        spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("epk", "bitstring"), new ProVerifVar ("sk", "bitstring")}, CERT_GETPK + "(" + CERT_CERT + "(epk," + PK_SIGN + "(epk,sk)))=epk"));

        spec.addDeclaration (new ProVerifComment    ("Symmetric key cryptography"));
        spec.addDeclaration (new ProVerifFunc       (SK_ENCRYPT, new String[] {"bitstring", "bitstring"}, "bitstring"));
        spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring"), new ProVerifVar ("k", "bitstring")}, SK_DECRYPT + "(" + SK_ENCRYPT + "(x,k),k)=x"));

        spec.addDeclaration (new ProVerifComment    ("MAC"));
        spec.addDeclaration (new ProVerifFunc       (MAC_MAC, new String[] {"bitstring", "bitstring"}, "bitstring"));
        spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("m", "bitstring"), new ProVerifVar ("k", "bitstring")}, MAC_VERIFYMAC + "(m,k," + MAC_MAC + "(m,k))=" + TRUE));

        spec.addDeclaration (new ProVerifComment    ("HASH"));
        spec.addDeclaration (new ProVerifFunc       (HASH_HASH, new String[] {"bitstring"}, "bitstring"));

        spec.addDeclaration (new ProVerifComment    ("Channel"));
        spec.addDeclaration (new ProVerifVar        (CH_MAINCH, "channel"));
        spec.addDeclaration (new ProVerifFunc       (CH_ENCRYPT, new String[] {"bitstring"}, "bitstring", true));
        spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring")}, CH_DECRYPT + "(" + CH_ENCRYPT + "(x))=x", true));

        spec.addDeclaration (new ProVerifComment    ("Control Channel"));
        spec.addDeclaration (new ProVerifVar        (CHCTRL_CH, "channel"));
        spec.addDeclaration (new ProVerifFunc       (CHCTRL_ENCRYPT, new String[] {"bitstring"}, "bitstring", true));
        spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring")}, CHCTRL_DECRYPT + "(" + CHCTRL_ENCRYPT + "(x))=x", true));

        LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
        String action = "(";
        for(AvatarBlock block: blocks) {
            HashMap<AvatarStateMachineElement, Integer> simplifiedElements = block.getStateMachine ().getSimplifiedElements ();
            for (AvatarStateMachineElement asme: simplifiedElements.keySet ())
                spec.addDeclaration (new ProVerifVar        ("call__" + block.getName() + "__" + simplifiedElements.get (asme), "bitstring", true));
        }

        spec.addDeclaration (new ProVerifComment    ("Data"));

        /* Data */
        TraceManager.addDev("Constants");
        for(AvatarBlock block: blocks) {
            for(AvatarAttribute attribute: block.getAttributes()) {
                String pragma = hasConstantPragmaStartingWithAttribute(block.getName(), attribute.getName());
                if (pragma != null) {
                    String constName = makeAttrName(block.getName(), attribute.getName());
                    TraceManager.addDev("|    " + constName);
                    spec.addDeclaration (new ProVerifConst      (constName, "bitstring"));
                    declarations.put(constName, constName);
                    String[] list = getListOfBlockParams(pragma);
                    addDeclarationsFromList(1, list, constName);
                }
            }
        }

        /* Secrecy Assumptions */
        spec.addDeclaration (new ProVerifComment    ("Secrecy Assumptions"));
        TraceManager.addDev("Secrecy Assumptions");
        for(String pr: avspec.getPragmas())
            if (isSecrecyAssumptionPragma(pr)) {
                String[] list = getListOfBlockParams(pr);
                for(int i=0; i<list.length; i++) {
                    String tmp = list[i];
                    int index = tmp.indexOf('.');
                    if (index != -1) {
                        String blockName = tmp.substring(0, index).trim();
                        String paramName = tmp.substring(index+1);
                        String tmp1 = makeAttrName(blockName, paramName);
                        if (tmp1 != null) {
                            TraceManager.addDev("|    " + tmp1);
                            spec.addDeclaration (new ProVerifSecrecyAssum (tmp1));
                        }
                    }
                }
            }

        /* Queries */
        /* Parse all attributes starting with "secret" and declare them as non accesible to attacker" */
        spec.addDeclaration (new ProVerifComment    ("Queries"));
        TraceManager.addDev("Queries"); 
        for(AvatarBlock block: blocks) {
            for(AvatarAttribute attribute: block.getAttributes()) {
                // Attribute is preinitialized if it is in a secret pragma
                //TraceManager.addDev("Testing secret of " + block.getName() + "." + attribute.getName() + " ?");
                if (hasSecretPragmaWithAttribute(block.getName(), attribute.getName())) {
                    //TraceManager.addDev("Secret!");
                    String varName = makeAttrName(block.getName(), attribute.getName());
                    spec.addDeclaration (new ProVerifVar        (varName, "bitstring", true));
                    declarations.put(varName, varName);
                    spec.addDeclaration (new ProVerifQueryAtt   (varName));
                    TraceManager.addDev("|    attacker (" + varName + ")"); 
                }
            }

            // Queries for states
            if (_stateReachability)
                for(AvatarStateMachineElement asme: block.getStateMachine().getListOfElements())
                    if (asme instanceof AvatarState) {
                        spec.addDeclaration (new ProVerifQueryEv    (new ProVerifVar[] {}, "enteringState__" + block.getName() + "__" + asme.getName()));
                        spec.addDeclaration (new ProVerifEvDecl     ("enteringState__" + block.getName() + "__" + asme.getName(), new String[] {}));
                        TraceManager.addDev("|    event (enteringState__" + block.getName() + "__" + asme.getName() + ")"); 
                    }
        }

        /* Autenticity */
        makeAuthenticityPragmas();
    }

    public void makeAuthenticityPragmas() {
        spec.addDeclaration (new ProVerifComment    ("Authenticity"));
        TraceManager.addDev("Authenticity"); 

        LinkedList<String> pragmas = avspec.getPragmas();
        int cpt = -1;

        for(String pragma: pragmas) {
            cpt ++;
            if (isAuthenticityPragma(pragma)) {
                String tmp = pragma.substring(13).trim();

                if (tmp.length() != 0) {
                    String[] tmps = tmp.split (" +");
                    if (tmps.length > 1) {
                        String p0 = Conversion.replaceAllChar(tmps[0], '.', "__");
                        String p1 = Conversion.replaceAllChar(tmps[1], '.', "__");

                        TraceManager.addDev("|    authenticity__" + p1 + "__" + cpt + "(m__93482) ==> authenticity__" + p0 + "__" + cpt + "(m__93482)"); 
                        spec.addDeclaration (new ProVerifEvDecl ("authenticity__" + p1 + "__" + cpt, new String[] {"bitstring"}));
                        spec.addDeclaration (new ProVerifEvDecl ("authenticity__" + p0 + "__" + cpt, new String[] {"bitstring"}));
                        spec.addDeclaration (new ProVerifQueryEvinj (new ProVerifVar[] {new ProVerifVar ("m__93482", "bitstring")}, "authenticity__" + p1 + "__" + cpt + "(m__93482)", "authenticity__" + p0 + "__" + cpt + "(m__93482)"));
                    }
                }
            }
        }
    }

    public boolean hasAuthenticityPragma(boolean isOut, String _blockName, String attributeName) {
        LinkedList<String> pragmas = avspec.getPragmas();

        for(String pragma: pragmas)
            if (isAuthenticityPragma(pragma)) {
                String tmp = pragma.substring(13).trim();

                if (tmp.isEmpty ())
                    return false;

                String[] tmps = tmp.split(" ");

                if (tmps.length >1) {
                    if (isOut)
                        tmp = tmps[0];
                    else
                        tmp = tmps[1];

                    int index = tmp.indexOf('.');
                    if ( index != -1
                            && tmp.substring(0, index).compareTo(_blockName) == 0
                            && tmp.substring(index+1).compareTo(attributeName) == 0)
                        return true;
                }
            }

        return false;
    }

    public LinkedList<String> getAuthenticityPragmas(String _blockName, String _stateName) {
        LinkedList<String> pragmas = avspec.getPragmas();
        LinkedList<String> ret = new LinkedList<String>();

        int cpt = -1;
        for(String pragma: pragmas) {
            cpt ++;
            if (isAuthenticityPragma(pragma)) {
                String tmp = pragma.substring(13).trim();

                if (tmp.isEmpty())
                    return ret;

                String[] tmps = tmp.split(" ");

                if (tmps.length > 1)
                    for(int i=0; i<2; i++) {
                        tmp = tmps[i];

                        if (tmp.length() > 0) {

                            int index = tmp.indexOf('.');
                            if ( index != -1
                                    && tmp.substring(0, index).compareTo(_blockName) == 0) {

                                tmp = tmp.substring(index+1);
                                index = tmp.indexOf('.');
                                if ( index != -1
                                        && tmp.substring(0, index).compareTo(_stateName) == 0)
                                    ret.add ("authenticity__" + _blockName + "__" + _stateName + "__" + tmp.substring(index+1) + "__" + cpt + "(" + tmp.substring(index+1) + ")");
                                    }
                        }
                    }
            }
        }

        return ret;
    }

    public boolean isPublicPrivateKeyPragma(String _blockName, String attributeName) {
        LinkedList<String> pragmas = avspec.getPragmas();

        for(String pragma: pragmas)
            if (isPrivatePublicKeyPragma(pragma)) {
                String tmp = pragma.substring(18).trim();

                if (tmp.isEmpty ())
                    return false;

                String[] tmps = tmp.split(" ");
                for(int i=0; i<tmps.length; i++) {
                    tmp = tmps[i];

                    int index = tmp.indexOf('.');
                    if ( (index != -1
                                && tmp.substring(0, index).compareTo(_blockName) == 0
                                && tmp.substring(index+1).compareTo(attributeName) == 0)
                            || tmp.compareTo(attributeName) == 0)
                        return true;
                }
            }

        return false;
    }

    public boolean hasSecretPragmaWithAttribute(String _blockName, String attributeName) {
        LinkedList<String> pragmas = avspec.getPragmas();

        for(String pragma: pragmas)
            if (isSecretPragma(pragma)) {
                String tmp = pragma.substring(7).trim();

                if (tmp.isEmpty ())
                    return false;

                String[] tmps = tmp.split(" ");
                for(int i=0; i<tmps.length; i++) {
                    tmp = tmps[i];
                    int index = tmp.indexOf('.');
                    if ( index != -1
                            && tmp.substring(0, index).compareTo(_blockName) == 0
                            && tmp.substring(index+1).compareTo(attributeName) == 0)
                        return true;
                }
            }

        return false;
    }

    public String hasConstantPragmaStartingWithAttribute(String _blockName, String attributeName) {
        LinkedList<String> pragmas = avspec.getPragmas();

        for(String pragma: pragmas)
            if (isConstantPragma(pragma)) {
                String tmp = pragma.substring(8).trim();

                if (tmp.isEmpty ())
                    return null;

                String[] tmps = tmp.split(" ");
                tmp = tmps[0];

                int index = tmp.indexOf('.');
                if ( index != -1
                        && tmp.substring(0, index).compareTo(_blockName) == 0
                        && tmp.substring(index+1).compareTo(attributeName) == 0)
                    return pragma;
            }

        return null;
    }

    public boolean hasSecretPragmaWithAttribute(String attributeName) {
        LinkedList<String> pragmas = avspec.getPragmas();

        for(String pragma: pragmas)
            if (isSecretPragma(pragma)) {
                String tmp = pragma.substring(7).trim();

                if (tmp.isEmpty ())
                    return false;

                String[] tmps = tmp.split(" ");
                for(String attribute: tmps) {
                    int index = attribute.indexOf('.');
                    if ( index != -1
                            && attribute.substring(index+1).compareTo(attributeName) == 0)
                        return true;
                }
            }

        return false;
    }

    public boolean hasPrivatePublicKeysPragmaWithAttribute(String _blockName, String attributeName) {
        LinkedList<String> pragmas = avspec.getPragmas();

        for(String pragma: pragmas)
            if (isPrivatePublicKeyPragma(pragma)) {
                String tmp = pragma.substring(17).trim();

                if (tmp.isEmpty ())
                    return false;

                String[] tmps = tmp.split(" ");
                if (tmp.compareTo(_blockName) != 0)
                    continue;

                for(int i=1; i<tmps.length; i++) {
                    tmp = tmps[i];
                    if (tmp.compareTo(attributeName) == 0)
                        return true;
                }
            }

        return false;
    }

    public boolean hasInitialSystemKnowledgePragmaWithAttribute(String _blockName, String attributeName) {
        LinkedList<String> pragmas = avspec.getPragmas();

        for(String pragma: pragmas)
            if (isInitialSystemKnowledgePragma(pragma)) {
                String tmp = pragma.substring(23, pragma.length()).trim();

                if (tmp.isEmpty ())
                    return false;

                String[] tmps = tmp.split(" ");
                for(String attribute: tmps) {
                    int index = attribute.indexOf('.');
                    if ( index != -1
                            && attribute.substring(0, index).compareTo(_blockName) == 0
                            && attribute.substring(index+1).compareTo(attributeName) == 0)
                        return true;
                }
            }

        return false;
    }

    public boolean isSecretPragma(String _pragma) {
        return _pragma.startsWith("Secret ");
    }

    public boolean isAuthenticityPragma(String _pragma) {
        return _pragma.startsWith("Authenticity ");
    }

    public boolean isInitialSystemKnowledgePragma(String _pragma) {
        return _pragma.startsWith("InitialSystemKnowledge ");
    }

    public boolean isInitialSessionKnowledgePragma(String _pragma) {
        return _pragma.startsWith("InitialSessionKnowledge ");
    }

    public boolean isConstantPragma(String _pragma) {
        return _pragma.startsWith("Constant ");
    }

    public boolean isSecrecyAssumptionPragma(String _pragma) {
        return _pragma.startsWith("SecrecyAssumption ");
    }

    public boolean isPrivatePublicKeyPragma(String _pragma) {
        return _pragma.startsWith("PrivatePublicKeys ");
    }

    public String[] getListOfBlockParams(String _pragma) {
        String s = _pragma;

        if (isSecretPragma(s))
            s = s.substring(7);
        else if (isInitialSystemKnowledgePragma(s))
            s = s.substring(23);
        else if (isInitialSessionKnowledgePragma(s))
            s = s.substring(24);
        else if (isConstantPragma(s))
            s = s.substring(8);
        else if (isSecrecyAssumptionPragma(s))
            s = s.substring(17);
        else
            return null;

        return s.trim ().split (" ");
    }

    public void makeStartingProcess() {
        TraceManager.addDev("\n\n=+=+=+ Making Starting Process +=+=+=");

        ProVerifProcess p = new ProVerifProcess("starting__", new ProVerifVar[] {});
        ProVerifProcInstr lastInstr = p;
        LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();

        HashMap<String, String> pubs = new HashMap<String, String>();

        String[] list;
        String blockName, paramName;

        TraceManager.addDev("Exploring Pragmas of all Processes");
        for(String pragma: avspec.getPragmas())
            if (isInitialSystemKnowledgePragma(pragma)) {
                // Identify each blockName / paramName
                list = getListOfBlockParams(pragma);

                // Declare only the first one of the list
                if (list.length > 0) {
                    String tmp = list[0];
                    int index = tmp.indexOf('.');
                    if (index != -1) {
                        blockName = tmp.substring(0, index).trim();
                        paramName = tmp.substring(index+1);

                        // TODO: move that outside the process ?
                        String blockParamName = makeAttrName(blockName, paramName);
                        String known = pubs.get(blockParamName);
                        ProVerifProcInstr tmpInstr;
                        if (known != null)
                            tmpInstr = makeLetActionFromBlockParam(blockName, paramName, known);
                        else
                            tmpInstr = makeActionFromBlockParam(blockName, paramName);

                        addDeclarationsFromList(1, list, blockParamName);
                        TraceManager.addDev("|    Initial knowledge pragma: " + blockParamName);
                        lastInstr = lastInstr.setNextInstr (tmpInstr);
                    }
                }
            } else if (isPrivatePublicKeyPragma(pragma)) {
                String privK, pubK;
                int index = pragma.indexOf(" ");
                if (index != -1) {
                    String tmp = pragma.substring(index+1).trim();
                    index = tmp.indexOf(" ");
                    if (index != -1) {
                        blockName = tmp.substring(0, index);
                        String tmp2 = tmp.substring(index+1).trim();
                        index = tmp2.indexOf(" ");
                        if (index != -1) {
                            privK = tmp2.substring(0, index).trim();
                            pubK = tmp2.substring(index+1).trim();
                            TraceManager.addDev("|    Private Public key pragma: " + privK + " / " + pubK);

                            String pubAttrName = makeAttrName(blockName, pubK);
                            pubs.put(pubAttrName, pubK);

                            lastInstr = lastInstr.setNextInstr (makeActionFromBlockParam(blockName, privK));

                            lastInstr = lastInstr.setNextInstr (new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar (pubAttrName, "bitstring")}, PK_PK + "(" + makeAttrName(blockName, privK) + ")"));;
                            lastInstr = lastInstr.setNextInstr (new ProVerifProcRaw ("out (" + CH_MAINCH + ", " + pubAttrName + ");"));
                            //TraceManager.addDev("********************************* Putting :" + makeAttrName(blockName, pubK + " -> " + makeAttrName(blockName, pubK)));
                            declarations.put(pubAttrName, pubAttrName);
                        }
                    }
                }
            } else if (isSecrecyAssumptionPragma(pragma)) {
                // Identify each blockName / paramName
                list = getListOfBlockParams(pragma);

                // TODO: move that outside the process ?
                // Declare only the first one of the list
                if (list.length > 0) {
                    String tmp = list[0];
                    int index = tmp.indexOf('.');
                    if (index != -1) {
                        blockName = tmp.substring(0, index).trim();
                        paramName = tmp.substring(index+1);

                        TraceManager.addDev("|    Secrecy assumption pragma: " + blockName + "__" + paramName);
                        lastInstr = lastInstr.setNextInstr (makeActionFromBlockParam(blockName, paramName));
                    }
                }
            }

        ProVerifProcRawGlobing globing = new ProVerifProcRawGlobing ("(", ")");
        lastInstr.setNextInstr (globing);
        lastInstr = globing.getIntra ();
        String action = "";
        for(AvatarBlock block: blocks) {
            HashMap<AvatarStateMachineElement, Integer> simplifiedElements = block.getStateMachine ().getSimplifiedElements ();
            int index = 0;
            for (AvatarStateMachineElement asme: simplifiedElements.keySet ()) {
                if (index != 0)
                    action += " | ";
                index ++;
                action += "!" + block.getName() + "__" + simplifiedElements.get (asme);
            }
            action += " | ";
        }

        lastInstr = lastInstr.setNextInstr (new ProVerifProcRaw (action));

        globing = new ProVerifProcRawGlobing ("! (", ")");
        lastInstr.setNextInstr (globing);
        lastInstr = globing.getIntra ();

        TraceManager.addDev("Finding session knowledge");
        this.sessionKnowledge = new LinkedList<ProVerifVar> ();
        // Must add Session Knowledge
        for(String pragma: avspec.getPragmas())
            //TraceManager.addDev("Working on pragma: " + pragma);
            if (isInitialSessionKnowledgePragma(pragma)) {
                list = getListOfBlockParams(pragma);


                // Declare only the first one of the list
                if (list.length > 0) {
                    String tmp = list[0];
                    int index = tmp.indexOf('.');
                    if (index != -1) {
                        blockName = tmp.substring(0, index).trim();
                        paramName = tmp.substring(index+1);

                        TraceManager.addDev("|    Session knowledge pragma: " + blockName + "__" + paramName);
                        lastInstr = lastInstr.setNextInstr (makeActionFromBlockParam(blockName, paramName));
                        this.sessionKnowledge.add (new ProVerifVar (blockName + "__" + paramName, "bitstring"));
                        addDeclarationsFromList(1, list, makeAttrName(blockName, paramName));
                    }
                }
            }

        TraceManager.addDev("Finding processes");
        ProVerifProcParallel paral = new ProVerifProcParallel ();
        for(AvatarBlock block: blocks)
            paral.addInstr (new ProVerifProcCall (block.getName() + "__start", this.sessionKnowledge.toArray (new ProVerifVar[this.sessionKnowledge.size ()])));
        lastInstr = lastInstr.setNextInstr (paral);
        spec.setMainProcess(p);
    }

    private String translateTerm (AvatarTerm term) {
        return term.getName ();
    }

    /**
     * Generate ProVerif code for each process for each Avatar block
     */
    public void makeBlocks() {
        TraceManager.addDev("\n\n=+=+=+ Making Blocks +=+=+=");

        LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
        for(AvatarBlock block: blocks)
            makeBlock(block);
    }

    /**
     * Compute a list of ProVerifVar corresponding to the attributes of the block
     */
    private LinkedList<ProVerifVar> getAttributesFromBlock (AvatarBlock ab) {
        LinkedList<ProVerifVar> result = new LinkedList<ProVerifVar> ();
        for(AvatarAttribute aa: ab.getAttributes())
            result.add (new ProVerifVar (aa.getName (), "bitstring"));
        return result;
    }

    /**
     * Generate ProVerif code for one Avatar block
     */
    public void makeBlock(AvatarBlock ab) {
        TraceManager.addDev("\nAvatarBlock: " + ab.getName ());

        // Create first ProVerif process for this block and add it to the ProVerif specification
        ProVerifProcInstr lastInstr = new ProVerifProcess(ab.getName() + "__start", this.sessionKnowledge.toArray (new ProVerifVar[this.sessionKnowledge.size ()]));
        spec.addDeclaration (lastInstr);

        // Create a ProVerif Variable corresponding to each attribute block
        LinkedList<ProVerifVar> attributes = this.getAttributesFromBlock (ab);
        for(ProVerifVar aa: attributes) {
            String dec = declarations.get(makeAttrName(ab.getName(), aa.getName()));
            if (dec == null)
                lastInstr = lastInstr.setNextInstr (new ProVerifProcNew (aa.getName (), "bitstring"));
            else if (dec.compareTo(aa.getName()) != 0)
                lastInstr = lastInstr.setNextInstr (new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar (aa.getName (), "bitstring")}, dec));
            TraceManager.addDev("|    AvatarAttribute: " + aa.getName ());
        }

        // Call the first "real" process
        String tmp = "out (" + CHCTRL_CH + ", " + CHCTRL_ENCRYPT + " ((call__" + ab.getName () + "__0";
        for(ProVerifVar aa: attributes)
            tmp += ", " + aa.getName ();
        lastInstr = lastInstr.setNextInstr (new ProVerifProcRaw (tmp + ")))"));

        macs.clear();

        // Generate a new process for every simplified element of the block's state machine
        attributes.add (0, new ProVerifVar ("call__num", "bitstring"));
        HashMap<AvatarStateMachineElement, Integer> simplifiedElements = ab.getStateMachine ().getSimplifiedElements ();
        for (AvatarStateMachineElement asme: simplifiedElements.keySet ())
            if (asme != null) {
                // Create the ProVerif process and add it to the ProVerif specification
                ProVerifProcInstr p = new ProVerifProcess(makeAttrName(ab.getName(), simplifiedElements.get (asme).toString ()), new ProVerifVar[] {});
                this.spec.addDeclaration (p);

                // TODO: add parameters
                // Read and decrypt control data: variables sent to the process and the call__num variable
                p = p.setNextInstr (new ProVerifProcIn (CHCTRL_CH, new ProVerifVar[] {new ProVerifVar ("chControlData", "bitstring")}));
                p = p.setNextInstr (new ProVerifProcLet (attributes.toArray (new ProVerifVar[attributes.size()]), CHCTRL_DECRYPT + " (chControlData)"));

                // Check that call__num variable really corresponds to this process
                p = p.setNextInstr (new ProVerifProcITE ("call__num = call__" + ab.getName () + "__" + simplifiedElements.get (asme)));

                // Create an object that will serve as an argument passed to the translation functions
                ProVerifTranslatorParameter arg = new ProVerifTranslatorParameter ();
                arg.block = ab;
                arg.choiceInfo = null;
                arg.lastInstr = p;
                arg.simplifiedElements = simplifiedElements;

                // Translate this simplified element
                asme.translate (this, arg);
            }
    }

    class ProVerifTranslatorParameter {
        AvatarBlock block;
        String choiceInfo;
        ProVerifProcInstr lastInstr;
        HashMap<AvatarStateMachineElement, Integer> simplifiedElements;
    }

    /**
     * Commodity method that translates the transition to the next Avatar state machine element
     */
    private void translateNext (AvatarStateMachineElement current, AvatarStateMachineElement next, Object _arg) {
        ProVerifTranslatorParameter arg = (ProVerifTranslatorParameter) _arg;
        // Check if next is not null
        if (next != null) {

            // Check if next is the root of a process
            Integer n = arg.simplifiedElements.get (next);
            if (n != null) {
                // If next is the root of a process send the attributes and arguments on the control channel
                String tmp = "out (" + CHCTRL_CH + ", " + CHCTRL_ENCRYPT + " ((call__" + arg.block.getName () + "__" + n;
                for(ProVerifVar aa: this.getAttributesFromBlock (arg.block))
                    tmp += ", " + aa.getName ();

                // Generate the arguments to send to the next process from the previous Avatar transition
                if (current instanceof AvatarTransition) {
                    for(AvatarActionAssignment action: ((AvatarTransition) current).getAssignments ()) {
                        AvatarLeftHand leftHand = action.getLeftHand ();
                        if (leftHand instanceof AvatarTuple)
                            for (AvatarTerm term: ((AvatarTuple) leftHand).getComponents ())
                                tmp += ", " + ((AvatarLocalVar) term).getName ();
                        else
                            tmp += ", " + ((AvatarLocalVar) leftHand).getName ();
                    }

                    for(AvatarTermFunction action: ((AvatarTransition) current).getFunctionCalls ()) {
                        String name = action.getMethod ().getName ();

                        if (name.equals ("get2") || name.equals ("get3") || name.equals ("get4")) {
                            LinkedList<AvatarTerm> args = action.getArgs ();

                            if (args.get(0) instanceof AvatarLocalVar)
                                tmp += ", " + ((AvatarLocalVar) args.get(0)).getName ();
                        }
                    }
                }

                arg.lastInstr.setNextInstr (new ProVerifProcRaw (tmp + ")))"));
            }
            else
                // If the next state machine element is not the root of a process, simply translate it
                next.translate (this, arg);
        }
    }

    /**
     * Translation function handling ActionOnSignal states
     */
    public void translateActionOnSignal (AvatarActionOnSignal _asme, Object _arg) {
        TraceManager.addDev("|    Action on signal");

        ProVerifTranslatorParameter arg = (ProVerifTranslatorParameter) _arg;
        avatartranslator.AvatarSignal as = _asme.getSignal();
        ProVerifProcInstr _lastInstr = arg.lastInstr;

        // Check if the channel is private
        boolean isPrivate = false;
        AvatarRelation ar = this.avspec.getAvatarRelationWithSignal(as);
        if (ar != null)
            isPrivate = ar.isPrivate();

        if (as.isOut()) {
            // If this is an out operation

            // Use a dummy name if no value is sent
            if (_asme.getNbOfValues() == 0)
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcNew ("data__", "bistring"));

            String tmp = "out (" + CH_MAINCH + ", ";
            if (isPrivate)
                tmp += CH_ENCRYPT + " (";

            if (_asme.getNbOfValues() == 0)
                tmp += "data__";
            else
                for(int i=0; i<_asme.getNbOfValues(); i++) {
                    if (i>0)
                        tmp += ", ";
                    tmp += _asme.getValue(i);
                }

            if (isPrivate)
                tmp += ")";

            tmp += ")";
            TraceManager.addDev("|    |    " + tmp);

            _lastInstr = _lastInstr.setNextInstr (new ProVerifProcRaw (tmp, true));

        } else {
            // If it's an In operation
            LinkedList<ProVerifVar> vars = new LinkedList<ProVerifVar> ();
            if (_asme.getNbOfValues() == 0)
                vars.add (new ProVerifVar ("data__", "bitstring"));
            else
                for(int i=0; i<_asme.getNbOfValues(); i++)
                    vars.add (new ProVerifVar (_asme.getValue(i), "bitstring"));

            // If the channel is private use the CH_DECRYPT function
            if (isPrivate) {
                TraceManager.addDev("|    |    in (chPriv, ...)");
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcIn (CH_MAINCH, new ProVerifVar[] {new ProVerifVar ("privChData", "bitstring")}));
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcLet (vars.toArray (new ProVerifVar[vars.size()]), CH_DECRYPT + " (privChData)"));
            } else {
                TraceManager.addDev("|    |    in (ch, ...)");
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcIn (CH_MAINCH, vars.toArray (new ProVerifVar[vars.size()])));
            }
        }

        arg.lastInstr = _lastInstr;
        this.translateNext (_asme, _asme.getNext(0), arg);
    }

    /**
     * Translation function handling Avatar Transitions
     */
    public void translateTransition (AvatarTransition _asme, Object _arg) {
        TraceManager.addDev("|    Transition");
        ProVerifTranslatorParameter arg = (ProVerifTranslatorParameter) _arg;
        ProVerifProcInstr _lastInstr = arg.lastInstr;

        // Check if the transition is guarded
        if (_asme.isGuarded()) {
            String tmp = modifyGuard(arg.block, _asme.getGuard().toString ());
            if (tmp != null) {
                TraceManager.addDev("|    |    transition is guarded by " + tmp);
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcITE (tmp));
            } else {
                CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Guard: " + _asme.getGuard() + " in block " + arg.block.getName() + " is not supported. Replacing by an empty guard");
                ce.setAvatarBlock(arg.block);
                ce.setTDiagramPanel(((AvatarDesignPanel)(avspec.getReferenceObject())).getAvatarSMDPanel(arg.block.getName()));
                ce.setTGComponent((TGComponent)(_asme.getReferenceObject()));
                warnings.add(ce);
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcRaw ("(*  Unsuported guard:" + _asme.getGuard() + " *)"));
            }
        }

        TraceManager.addDev("|    |    Actions");
        // Loop over all assigment functions
        for(AvatarActionAssignment action: _asme.getAssignments ()) {
            TraceManager.addDev("|    |    |    assignment found: " + action);

            // Compute left hand part of the assignment
            AvatarLeftHand leftHand = action.getLeftHand ();
            LinkedList<ProVerifVar> proVerifLeftHand = new LinkedList<ProVerifVar> ();
            if (leftHand instanceof AvatarTuple)
                for (AvatarTerm term: ((AvatarTuple) leftHand).getComponents ())
                    proVerifLeftHand.add (new ProVerifVar (((AvatarLocalVar) term).getName (), "bitstring"));
            else if (leftHand instanceof AvatarLocalVar)
                proVerifLeftHand.add (new ProVerifVar (((AvatarLocalVar) leftHand).getName (), "bitstring"));
            else
                proVerifLeftHand.add (new ProVerifVar (((AvatarAttribute) leftHand).getName (), "bitstring"));

            // Compute right part of assignment
            AvatarTerm rightHand = action.getRightHand ();
            String proVerifRightHand = null;
            if (rightHand instanceof AvatarTermFunction) {
                // If it's a function call
                String name = ((AvatarTermFunction) rightHand).getMethod ().getName ();
                LinkedList<AvatarTerm> args = ((AvatarTermFunction) rightHand).getArgs ();

                if (name.equals ("verifyMAC") && advancedTranslation) {
                    // If the function called is verifyMAC and advanced translation is enabled, perform translation
                    _lastInstr = _lastInstr.setNextInstr (new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar ("MAC__tmp0__" + GENERAL_ID, "bitstring")}, "MAC(" + args.get (0).getName () + " , " + args.get (1).getName () + ")"));
                    _lastInstr = _lastInstr.setNextInstr (new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar ("MAC__tmp1__" + GENERAL_ID, "bitstring")}, args.get (2).getName ()));
                    macs.remove(leftHand.getName ());
                    macs.put(leftHand.getName (), new Integer(GENERAL_ID));
                    GENERAL_ID++;
                } else if (name.equals ("concat2") || name.equals ("concat3") || name.equals ("concat4")) {
                    // If it's a concat function, just use tuples
                    String tmp = "(";
                    boolean first = true;
                    for (AvatarTerm term: args) {
                        if (first)
                            first = false;
                        else
                            tmp += ", ";
                        tmp += term.getName ();
                    }
                    tmp += ")";
                    _lastInstr = _lastInstr.setNextInstr (new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar (leftHand.getName (), "bitstring")}, tmp));
                } else
                    // Else use the function as is
                    proVerifRightHand = this.translateTerm (rightHand);
            } else
                // If it's not a function, use it as is
                proVerifRightHand = this.translateTerm (rightHand);

            if (proVerifRightHand != null)
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcLet (proVerifLeftHand.toArray (new ProVerifVar[proVerifLeftHand.size ()]), proVerifRightHand));
        }

        // Loop over all function calls
        for(AvatarTermFunction action: _asme.getFunctionCalls ()) {
            String name = action.getMethod ().getName ();

            if (name.equals ("get2") || name.equals ("get3") || name.equals ("get4")) {
                // If the function called is get[234]
                LinkedList<AvatarTerm> args = action.getArgs ();
                int index = (int) name.charAt (3) - 49;

                if (args.get(0) instanceof AvatarLocalVar)
                    // Create the corresponding assignment
                    _lastInstr = _lastInstr.setNextInstr (new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar (((AvatarLocalVar) args.get(0)).getName (), "bitstring")}, this.translateTerm (args.get (index))));
            }
        }

        arg.lastInstr = _lastInstr;
        this.translateNext (_asme, _asme.getNext(0), arg);
    }

    public void translateState (AvatarState _asme, Object _arg) {
        TraceManager.addDev("|    State");
        ProVerifTranslatorParameter arg = (ProVerifTranslatorParameter) _arg;
        ProVerifProcInstr _lastInstr = arg.lastInstr;

        // Adding an event for reachability of the state
        _lastInstr = _lastInstr.setNextInstr (new ProVerifProcRaw ("event enteringState__" + arg.block.getName() + "__" + _asme.getName() + "()", true));

        // Adding an event if authenticity is concerned with that state
        LinkedList<String> pos = getAuthenticityPragmas (arg.block.getName(), _asme.getName());
        for(String sp: pos) {
            TraceManager.addDev("|    |    authenticity event " + sp + "added");
            _lastInstr = _lastInstr.setNextInstr (new ProVerifProcRaw ("event " + sp, true));
        }

        int nbOfNexts = _asme.nbOfNexts ();
        if (nbOfNexts == 0)
            return;
        else if (nbOfNexts == 1) {
            arg.lastInstr = _lastInstr;
            this.translateNext (_asme, _asme.getNext(0), arg);

        } else if (_asme.hasElseChoiceType1()) {
            TraceManager.addDev("|    |    calling next ITE");
            ProVerifProcITE ite = new ProVerifProcITE (this.modifyGuard (arg.block, ((AvatarTransition) _asme.getNext (0)).getGuard ().toString ()));

            arg.lastInstr = _lastInstr.setNextInstr (ite);
            this.translateNext (_asme.getNext (0), _asme.getNext (0).getNext (0), arg);

            arg.lastInstr = ite.getElse ();
            this.translateNext (_asme.getNext (1), _asme.getNext (1).getNext (0), arg);

        } else {
            TraceManager.addDev("|    |    non deterministic next state");
            for (int i=0; i<nbOfNexts-1; i++) {
                String choice = "choice__" + _asme.getName () + "__" + i;
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcNew (choice, "bistring"));
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcRaw ("out (" + CH_MAINCH + ", " + choice + ");"));
            }
            _lastInstr = _lastInstr.setNextInstr (new ProVerifProcIn (CH_MAINCH, new ProVerifVar[] {new ProVerifVar ("choice__" + _asme.getName (), "bitstring")}));
            for (int i=0; i<nbOfNexts-1; i++) {
                String choice = "choice__" + _asme.getName () + "__" + i;
                ProVerifProcITE ite = new ProVerifProcITE ("choice__" + _asme.getName () + " = " + choice);

                arg.lastInstr = _lastInstr.setNextInstr (ite);
                this.translateNext (_asme, _asme.getNext (i), arg);

                _lastInstr = ite.getElse ();
            }

            arg.lastInstr = _lastInstr;
            this.translateNext (_asme, _asme.getNext (nbOfNexts-1), arg);
        }
    }

    public void translateRandom (AvatarRandom _asme, Object _arg) {
        this.translateNext (_asme, _asme.getNext(0), _arg);
    }

    public void translateStartState (AvatarStartState _asme, Object _arg) {
        this.translateNext (_asme, _asme.getNext(0), _arg);
    }

    public void translateTimerOperator (AvatarTimerOperator _asme, Object _arg) {
        this.translateNext (_asme, _asme.getNext(0), _arg);
    }

    public void translateStopState (AvatarStopState _asme, Object _arg) {
    }

    // Supported guards: a == b, not(a == b)
    // -> transformed into a = b, a <> b
    // Returns nulls otherwise
    public String modifyGuard(AvatarBlock _block, String _guard) {
        String[] ab;

        String s = Conversion.replaceAllString(_guard, "[", "");
        s = Conversion.replaceAllString(s, "]", "").trim();
        s = Conversion.replaceAllString(s, " ", "");

        if (s.startsWith("not(")) {
            if (s.endsWith(")")) {
                s = s.substring(4, s.length()-1);


                // Should have a "a == b";
                ab = getEqualGuard(_block, s);
                if (ab == null)
                    return null;

                return ab[0] + " <> " + ab[1];
            }
            return null;
        } else {
            ab = getEqualGuard(_block, s);
            if (ab == null)
                return null;

            return ab[0] + " = " + ab[1];
        }
    }

    // Input string must be of the form "a==b" or "b"
    // With a and b ids.
    // Returns a and b
    // Otherwise, returns null;
    public String[] getEqualGuard(AvatarBlock _block, String _guard) {
        Integer myInt;
        int index = _guard.indexOf("==");
        if (index == -1) {
            _guard = myTrim(_guard);
            AvatarTerm term = AvatarTerm.createFromString (_block, _guard);
            if (term instanceof AvatarLocalVar || term instanceof AvatarAttribute) {
                _guard = term.getName ();
                myInt = macs.get(_guard.trim());
                String[] ab = new String[2];
                if (myInt != null) {
                    ab[0] = "MAC__tmp0__" + myInt.intValue();
                    ab[1] = "MAC__tmp1__" + myInt.intValue();
                } else {
                    ab[0] = _guard;
                    ab[1] = TRUE;
                }
                return ab;
            }

            return null;
        }

        AvatarTerm a = AvatarTerm.createFromString (_block, _guard.substring(0, index).trim());
        AvatarTerm b = AvatarTerm.createFromString (_block, _guard.substring(index+2).trim());

        if ((a instanceof AvatarLocalVar || a instanceof AvatarAttribute) && (b instanceof AvatarLocalVar || b instanceof AvatarAttribute)) {
            String[] ab = new String[2];
            ab[0] = a.getName ();
            ab[1] = b.getName ();
            return ab;
        }

        return null;
    }

    // Remove all begining and trailing parenthesis and spaces
    private String myTrim(String toBeTrimmed) {
        int length = toBeTrimmed.length();
        String tmp = toBeTrimmed.trim();

        while (tmp.startsWith("("))
            tmp = tmp.substring(1).trim ();

        while (tmp.endsWith(")"))
            tmp = tmp.substring(0, tmp.length()-1).trim ();

        return tmp;
    }
}
