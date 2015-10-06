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

import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.io.*;
import javax.swing.*;


import ui.CheckingError;
import ui.AvatarDesignPanel;
import ui.TGComponent;
import proverifspec.*;
import myutil.*;
import avatartranslator.*;

public class AVATAR2ProVerif implements AvatarTranslator {

    private final static String ATTR_DELIM = "__";

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

    private HashMap<AvatarAttribute, AvatarAttribute> pubs;
    private HashMap<AvatarAttribute, AvatarAttribute> nameEquivalence;

    private Vector warnings;

    public AVATAR2ProVerif(AvatarSpecification _avspec) {
        this.avspec = _avspec;
        this.spec = null;
    }

    public boolean saveInFile(String path) throws FileException {
        // TODO check if pvspec exists etc.
        // TODO: Add a field for Hash and check it
	//Our hash file is always hash.txt
	String hashCode= Integer.toString(this.spec.getStringSpec().hashCode());
	File file = new File(path);
	BufferedReader br;
        if (file.exists()){
	    File hash = new File("hash.txt");
	    if (hash.exists()){
		try {
  	 	    br = new BufferedReader(new FileReader("hash.txt"));
		    String line = br.readLine();
		    br = new BufferedReader(new FileReader(path));
		    String s = br.readLine();
		    String tmp;
		    while ((tmp = br.readLine()) !=null){
			s = s+"\n" + tmp;
		    }
		    String fileHash = Integer.toString(s.hashCode());
		    if (!line.equals(fileHash)){
			if(JOptionPane.showConfirmDialog(null, "File " + path + " already exists. Do you want to overwrite?", "Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			    return false;
			}
		    }
		    br.close();
		} catch (Exception e) {
			
		}
	    }
	}
        FileUtils.saveFile(path, this.spec.getStringSpec());
	FileUtils.saveFile("hash.txt", hashCode);
	return true;
    }

    public Vector getWarnings() {
        return this.warnings;
    }

    public AvatarSpecification getAvatarSpecification () {
        return this.avspec;
    }

    public ProVerifSpec generateProVerif(boolean _debug, boolean _optimize, boolean _stateReachability, boolean _typed) {

        this.warnings = new Vector();
        if (_typed)
            this.spec = new ProVerifSpec (new ProVerifPitypeSyntaxer ());
        else
            this.spec = new ProVerifSpec (new ProVerifPiSyntaxer ());

        // TODO: What are composite states ?
        this.avspec.removeCompositeStates();
        this.avspec.removeTimers();
        // TODO: Why remove Else guards ?
        // this.avspec.removeElseGuards();

        LinkedList<AvatarAttribute> allKnowledge = this.makeStartingProcess();

        this.makeHeader(_stateReachability);

        this.makeBlocks(allKnowledge);

        return this.spec;
    }

    public ProVerifOutputAnalyzer getOutputAnalyzer () {
        return new ProVerifOutputAnalyzer (this);
    }

    public String getTrueName (AvatarAttribute attr) {
        AvatarAttribute trueAttr = this.nameEquivalence.get (attr);
        if (trueAttr == null)
            return null;
        return AVATAR2ProVerif.translateTerm (trueAttr, null);
    }

    private static String makeAttrName (String... _params) {
        String result = "";
        boolean first = true;
        for (String p: _params) {
            if (first)
                first = false;
            else
                result += ATTR_DELIM;
            result += p;
        }
        return result;
    }

    public void makeHeader(boolean _stateReachability) {
        TraceManager.addDev("\n\n=+=+=+ Making Headers +=+=+=");
        this.spec.addDeclaration (new ProVerifComment    ("Boolean return types"));
        this.spec.addDeclaration (new ProVerifConst      (TRUE, "bitstring"));
        this.spec.addDeclaration (new ProVerifConst      (FALSE, "bitstring"));
        this.spec.addDeclaration (new ProVerifComment    ("Functions data"));
        this.spec.addDeclaration (new ProVerifConst      (UNKNOWN, "bitstring"));

        this.spec.addDeclaration (new ProVerifComment    ("Public key cryptography"));
        this.spec.addDeclaration (new ProVerifFunc       (PK_PK, new String[] {"bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifFunc       (PK_ENCRYPT, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring"), new ProVerifVar ("y", "bitstring")}, PK_DECRYPT + " (" + PK_ENCRYPT + " (x, " + PK_PK + " (y)), y) = x"));
        this.spec.addDeclaration (new ProVerifFunc       (PK_SIGN, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("m", "bitstring"), new ProVerifVar ("sk", "bitstring")}, PK_VERIFYSIGN + " (m, " + PK_SIGN + " (m, sk), " + PK_PK + " (sk)) = " + TRUE));

        this.spec.addDeclaration (new ProVerifComment    ("Certificates"));
        this.spec.addDeclaration (new ProVerifFunc       (CERT_CERT, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("epk", "bitstring"), new ProVerifVar ("sk", "bitstring")}, CERT_VERIFYCERT + " (" + CERT_CERT + " (epk, " + PK_SIGN + " (epk, sk)), " + PK_PK + " (sk)) = " + TRUE));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("epk", "bitstring"), new ProVerifVar ("sk", "bitstring")}, CERT_GETPK + " (" + CERT_CERT + " (epk, " + PK_SIGN + " (epk,sk))) = epk"));

        this.spec.addDeclaration (new ProVerifComment    ("Symmetric key cryptography"));
        this.spec.addDeclaration (new ProVerifFunc       (SK_ENCRYPT, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring"), new ProVerifVar ("k", "bitstring")}, SK_DECRYPT + " (" + SK_ENCRYPT + " (x, k), k) = x"));

        this.spec.addDeclaration (new ProVerifComment    ("MAC"));
        this.spec.addDeclaration (new ProVerifFunc       (MAC_MAC, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifFunc       (MAC_VERIFYMAC, new String[] {"bitstring", "bitstring", "bitstring"}, "bitstring",
                                  new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("m", "bitstring"), new ProVerifVar ("k", "bitstring")}, MAC_VERIFYMAC + " (m, k, " + MAC_MAC + " (m, k)) = " + TRUE,
                                  new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("m", "bitstring"), new ProVerifVar ("m2", "bitstring"), new ProVerifVar ("k", "bitstring")}, MAC_VERIFYMAC + " (m, k, m2) = " + FALSE))));

        this.spec.addDeclaration (new ProVerifComment    ("HASH"));
        this.spec.addDeclaration (new ProVerifFunc       (HASH_HASH, new String[] {"bitstring"}, "bitstring"));

        this.spec.addDeclaration (new ProVerifComment    ("Channel"));
        this.spec.addDeclaration (new ProVerifVar        (CH_MAINCH, "channel"));
        this.spec.addDeclaration (new ProVerifFunc       (CH_ENCRYPT, new String[] {"bitstring"}, "bitstring", true));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring")}, CH_DECRYPT + " (" + CH_ENCRYPT + " (x)) = x", true));

        this.spec.addDeclaration (new ProVerifComment    ("Control Channel"));
        this.spec.addDeclaration (new ProVerifVar        (CHCTRL_CH, "channel"));
        this.spec.addDeclaration (new ProVerifFunc       (CHCTRL_ENCRYPT, new String[] {"bitstring"}, "bitstring", true));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring")}, CHCTRL_DECRYPT + " (" + CHCTRL_ENCRYPT + " (x)) = x", true));

        /* Declare all the call__*** variables */
        LinkedList<AvatarBlock> blocks = this.avspec.getListOfBlocks();
        String action = "(";
        for(AvatarBlock block: blocks) {
            HashMap<AvatarStateMachineElement, Integer> simplifiedElements = block.getStateMachine ().getSimplifiedElements ();
            for (AvatarStateMachineElement asme: simplifiedElements.keySet ())
                this.spec.addDeclaration (new ProVerifVar        ("call__" + block.getName() + "__" + simplifiedElements.get (asme), "bitstring", true));
        }

        this.spec.addDeclaration (new ProVerifComment    ("Data"));
        TraceManager.addDev("Constants");
        for (AvatarPragma pragma: this.avspec.getPragmas ()) {
            TraceManager.addDev("Pragma    " + pragma.toString ());

            if (pragma instanceof AvatarPragmaConstant)
                for (AvatarConstant constant: ((AvatarPragmaConstant) pragma).getConstants ()) {
                    String constName = constant.getName ();
                    TraceManager.addDev("|    " + constName);
                    this.spec.addDeclaration (new ProVerifConst      (constName, "bitstring"));
                }
        }

        /* Secrecy Assumptions */
        HashSet<AvatarAttribute> secrecyChecked = new HashSet<AvatarAttribute> ();

        this.spec.addDeclaration (new ProVerifComment    ("Secrecy Assumptions"));
        TraceManager.addDev("Secrecy Assumptions");
        for (AvatarPragma pragma: this.avspec.getPragmas ())
            if (pragma instanceof AvatarPragmaSecrecyAssumption)
                for (AvatarAttribute attribute: pragma.getArgs ()) {
                    AvatarAttribute trueAttr = this.nameEquivalence.get (attribute);
                    if (trueAttr == null)
                        // TODO Warning
                        continue;
                    if (secrecyChecked.contains (trueAttr))
                        continue;

                    String name = AVATAR2ProVerif.translateTerm (trueAttr, null);
                    TraceManager.addDev("|    " + name);
                    this.spec.addDeclaration (new ProVerifSecrecyAssum (name));

                    secrecyChecked.add (trueAttr);
                }

        /* Queries */
        this.spec.addDeclaration (new ProVerifComment    ("Queries Secret"));
        TraceManager.addDev("Queries Secret"); 
        for (AvatarPragma pragma: this.avspec.getPragmas ())
            if (pragma instanceof AvatarPragmaSecret)
                for (AvatarAttribute attribute: pragma.getArgs ()) {
                    AvatarAttribute trueAttr = this.nameEquivalence.get (attribute);
                    if (trueAttr == null)
                        // TODO Warning
                        continue;
                    if (secrecyChecked.contains (trueAttr))
                        continue;

                    String varName = AVATAR2ProVerif.translateTerm (trueAttr, null);
                    this.spec.addDeclaration (new ProVerifQueryAtt   (varName, true));
                    TraceManager.addDev("|    attacker (" + varName + ")"); 

                    secrecyChecked.add (trueAttr);
                }

        // Queries for states
        if (_stateReachability) {
            this.spec.addDeclaration (new ProVerifComment    ("Queries Event"));
            TraceManager.addDev ("Queries Event"); 
            for (AvatarBlock block: this.avspec.getListOfBlocks ())
                for(AvatarStateMachineElement asme: block.getStateMachine ().getListOfElements ())
                    if (asme instanceof AvatarState) {
                        this.spec.addDeclaration (new ProVerifQueryEv    (new ProVerifVar[] {}, "enteringState__" + block.getName() + "__" + asme.getName()));
                        this.spec.addDeclaration (new ProVerifEvDecl     ("enteringState__" + block.getName() + "__" + asme.getName(), new String[] {}));
                        TraceManager.addDev("|    event (enteringState__" + block.getName() + "__" + asme.getName() + ")"); 
                    }
        }

        /* Autenticity */
        this.spec.addDeclaration (new ProVerifComment    ("Authenticity"));
        TraceManager.addDev ("Authenticity"); 
        for (AvatarPragma pragma: this.avspec.getPragmas ())
            if (pragma instanceof AvatarPragmaAuthenticity) {
                AvatarAttributeState attrA = ((AvatarPragmaAuthenticity) pragma).getAttrA ();
                AvatarAttributeState attrB = ((AvatarPragmaAuthenticity) pragma).getAttrB ();
                if (attrA != null && attrB != null) {
                    String sA = AVATAR2ProVerif.makeAttrName (attrA.getAttribute ().getBlock ().getName (), attrA.getAttribute ().getName (), attrA.getState ().getName ());
                    String sB = AVATAR2ProVerif.makeAttrName (attrB.getAttribute ().getBlock ().getName (), attrB.getAttribute ().getName (), attrB.getState ().getName ());
                    TraceManager.addDev("|    authenticity__" + sB + " (dummyM) ==> authenticity__" + sA + " (dummyM)"); 
                    spec.addDeclaration (new ProVerifEvDecl ("authenticity__" + sA, new String[] {"bitstring"}));
                    spec.addDeclaration (new ProVerifEvDecl ("authenticity__" + sB, new String[] {"bitstring"}));
                    spec.addDeclaration (new ProVerifQueryEvinj (new ProVerifVar[] {new ProVerifVar ("dummyM", "bitstring")}, "authenticity__" + sB + " (dummyM)", "authenticity__" + sA + " (dummyM)"));
                }
            }
    }

    private LinkedList<AvatarAttribute> makeStartingProcess() {
        TraceManager.addDev("\n\n=+=+=+ Making Starting Process +=+=+=");

        // Create starting process
        ProVerifProcess p = new ProVerifProcess("starting__", new ProVerifVar[] {});
        ProVerifProcInstr lastInstr = p;

        // Get all the blocks
        LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();

        // Used to store the names that are public keys
        this.pubs = new HashMap<AvatarAttribute, AvatarAttribute> ();
        for (AvatarPragma pragma: this.avspec.getPragmas ())
            if (pragma instanceof AvatarPragmaPrivatePublicKey)
                this.pubs.put (((AvatarPragmaPrivatePublicKey) pragma).getPublicKey (), ((AvatarPragmaPrivatePublicKey) pragma).getPrivateKey ());

        String blockName, paramName;

        // Store all the names that are system knowledge
        // Enable to raise warning when an attribute is both system and session knowledge
        LinkedList<AvatarAttribute> systemKnowledge = new LinkedList<AvatarAttribute> ();

        this.nameEquivalence = new HashMap<AvatarAttribute, AvatarAttribute> ();

        TraceManager.addDev("Finding system knowledge");
        for (AvatarPragma pragma: this.avspec.getPragmas ())
            // Check if pragma is system initial knowledge
            if (pragma instanceof AvatarPragmaInitialKnowledge && ((AvatarPragmaInitialKnowledge) pragma).isSystem ()) {
                AvatarAttribute first = null;
                boolean containsPublicKey = false;
                for (AvatarAttribute arg: pragma.getArgs ()) {
                    // ignore if the attribute was already declared
                    if (systemKnowledge.contains (arg))
                        continue;

                    ProVerifProcInstr tmpInstr;
                    // Check if it is the first from the list
                    if (first == null) {
                        first = arg;
                        this.nameEquivalence.put (first, first);

                        AvatarAttribute privateK = this.pubs.get (arg);
                        // Check if it is a public key
                        if (privateK != null) {
                            containsPublicKey = true;
                            String privateKStr = AVATAR2ProVerif.translateTerm (privateK, null);
                            // Check if the corresponding private key has already been declared
                            if (!systemKnowledge.contains (privateK)) {
                                lastInstr = lastInstr.setNextInstr (new ProVerifProcNew (privateKStr, "bitstring"));
                                systemKnowledge.add (privateK);
                            }

                            // Let the public key
                            lastInstr = lastInstr.setNextInstr (new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar (AVATAR2ProVerif.translateTerm (first, null), "bitstring")}, PK_PK + "(" + privateKStr + ")"));;
                            // Make the public key public
                            tmpInstr = new ProVerifProcRaw ("out (" + CH_MAINCH + ", " + AVATAR2ProVerif.translateTerm (first, null) + ");");
                        } else
                            tmpInstr = new ProVerifProcNew (AVATAR2ProVerif.translateTerm (first, null), "bitstring");
                    } else {
                        // If there are more arguments after a public key ignore what follows
                        if (containsPublicKey) {
                            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "You can't define equality between public keys and other attributes.");
                            //ce.setAvatarBlock(arg.block);
                            //FIXME: quel TDiagramPanel ?
                            //ce.setTDiagramPanel(((AvatarDesignPanel)(avspec.getReferenceObject())).getAvatarSMDPanel(arg.block.getName()));
                            //ce.setTGComponent((TGComponent)(_asme.getReferenceObject()));
                            //warnings.add(ce);
                            break;
                        }
                        // If there is a public key in the middle, ignore it
                        if (this.pubs.get (arg) != null) {
                            // TODO: raise warning
                            continue;
                        }

                        String str = AVATAR2ProVerif.translateTerm (arg, null);
                        tmpInstr = new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar (str, "bitstring")}, AVATAR2ProVerif.translateTerm (first, null));
                        this.nameEquivalence.put (arg, first);
                    }

                    TraceManager.addDev("|    Initial system knowledge pragma: " + AVATAR2ProVerif.translateTerm (first, null));
                    lastInstr = lastInstr.setNextInstr (tmpInstr);
                    systemKnowledge.add (arg);
                }
            }

        // Call all the processes corresponding to crossroads in the state machine
        ProVerifProcRawGlobing globing = new ProVerifProcRawGlobing ("(", ")");
        lastInstr.setNextInstr (globing);
        lastInstr = globing.getIntra ();
        String action = "";
        for(AvatarBlock block: blocks) {
            HashMap<AvatarStateMachineElement, Integer> simplifiedElements = block.getStateMachine ().getSimplifiedElements ();
            for (AvatarStateMachineElement asme: simplifiedElements.keySet ())
                action += "(!" + block.getName () + "__" + simplifiedElements.get (asme) + ") | ";
        }
        lastInstr = lastInstr.setNextInstr (new ProVerifProcRaw (action));

        globing = new ProVerifProcRawGlobing ("! (", ")");
        lastInstr.setNextInstr (globing);
        lastInstr = globing.getIntra ();

        TraceManager.addDev("Finding session knowledge");
        LinkedList<AvatarAttribute> sessionKnowledge = new LinkedList<AvatarAttribute> ();
        for (AvatarPragma pragma: this.avspec.getPragmas ())
            // Check if pragma is session initial knowledge
            if (pragma instanceof AvatarPragmaInitialKnowledge && !((AvatarPragmaInitialKnowledge) pragma).isSystem ()) {
                AvatarAttribute first = null;
                boolean containsPublicKey = false;
                for (AvatarAttribute arg: pragma.getArgs ()) {
                    // ignore if the attribute was already declared
                    if (sessionKnowledge.contains (arg))
                        continue;

                    // ignore if the attribute was sytem knowledge
                    if (systemKnowledge.contains (arg)) {
                        // TODO: raise warning
                        continue;
                    }

                    ProVerifProcInstr tmpInstr;
                    // Check if it is the first from the list
                    if (first == null) {
                        first = arg;
                        this.nameEquivalence.put (first, first);

                        AvatarAttribute privateK = this.pubs.get (arg);
                        // Check if it is a public key
                        if (privateK != null) {
                            containsPublicKey = true;
                            String privateKStr = AVATAR2ProVerif.translateTerm (privateK, null);
                            // Check if the corresponding private key has already been declared
                            if (!systemKnowledge.contains (privateK) && !sessionKnowledge.contains (privateK)) {
                                lastInstr = lastInstr.setNextInstr (new ProVerifProcNew (privateKStr, "bitstring"));
                                sessionKnowledge.add (privateK);
                            }

                            // Let the public key
                            lastInstr = lastInstr.setNextInstr (new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar (AVATAR2ProVerif.translateTerm (first, null), "bitstring")}, PK_PK + "(" + privateKStr + ")"));;
                            // Make the public key public
                            tmpInstr = new ProVerifProcRaw ("out (" + CH_MAINCH + ", " + AVATAR2ProVerif.translateTerm (first, null) + ");");
                        } else
                            tmpInstr = new ProVerifProcNew (AVATAR2ProVerif.translateTerm (first, null), "bitstring");
                    } else {
                        // If there are more arguments after a public key ignore what follows
                        if (containsPublicKey) {
                            // TODO: raise warning
                            break;
                        }
                        // If there is a public key in the middle, ignore it
                        if (this.pubs.get (arg) != null) {
                            // TODO: raise warning
                            continue;
                        }

                        this.nameEquivalence.put (arg, first);
                        String str = AVATAR2ProVerif.translateTerm (arg, null);
                        tmpInstr = new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar (str, "bitstring")}, AVATAR2ProVerif.translateTerm (first, null));
                    }

                    TraceManager.addDev("|    Initial session knowledge pragma: " + AVATAR2ProVerif.translateTerm (first, null));
                    lastInstr = lastInstr.setNextInstr (tmpInstr);
                    sessionKnowledge.add (arg);
                }
            }

        // Concatenate system and session knowledge
        systemKnowledge.addAll (sessionKnowledge);

        ProVerifVar[] knowledgeArray = this.getProVerifVarFromAttr (systemKnowledge);

        // Call every start process
        TraceManager.addDev("Finding processes");
        ProVerifProcParallel paral = new ProVerifProcParallel ();
        for(AvatarBlock block: blocks)
            paral.addInstr (new ProVerifProcCall (block.getName() + "__start", knowledgeArray));
        lastInstr = lastInstr.setNextInstr (paral);

        // Set main process
        spec.setMainProcess(p);

        return systemKnowledge;
    }

    /**
     * Generate ProVerif code for each process for each Avatar block
     */
    private void makeBlocks(LinkedList<AvatarAttribute> allKnowledge) {
        TraceManager.addDev("\n\n=+=+=+ Making Blocks +=+=+=");

        LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
        for(AvatarBlock block: blocks)
            makeBlock(block, allKnowledge);
    }

    private ProVerifVar[] getProVerifVarFromAttr (LinkedList<AvatarAttribute> attrs) {
        LinkedList<ProVerifVar> result = new LinkedList<ProVerifVar> ();
        for(AvatarAttribute aa: attrs)
            result.add (new ProVerifVar (AVATAR2ProVerif.translateTerm (aa, null), "bitstring"));
        return result.toArray (new ProVerifVar[result.size ()]);
    }

    /**
     * Compute a list of ProVerifVar corresponding to the attributes of the block
     */
    private ProVerifVar[] getAttributesFromBlock (AvatarBlock ab) {
        return this.getProVerifVarFromAttr (ab.getAttributes ());
    }

    /**
     * Generate ProVerif code for one Avatar block
     */
    private void makeBlock(AvatarBlock ab, LinkedList<AvatarAttribute> _allKnowledge) {
        TraceManager.addDev("\nAvatarBlock: " + ab.getName ());
        LinkedList<AvatarAttribute> allKnowledge = (LinkedList<AvatarAttribute>) _allKnowledge.clone ();

        // Create first ProVerif process for this block and add it to the ProVerif specification
        ProVerifVar[] knowledgeArray = this.getProVerifVarFromAttr (allKnowledge);
        ProVerifProcInstr lastInstr = new ProVerifProcess(ab.getName() + "__start", knowledgeArray);
        spec.addDeclaration (lastInstr);

        // Create a ProVerif Variable corresponding to each attribute block
        for (AvatarAttribute arg: ab.getAttributes ()) {
            // ignore if the attribute was already declared
            if (allKnowledge.contains (arg))
                continue;

            ProVerifProcInstr tmpInstr;
            String str = AVATAR2ProVerif.translateTerm (arg, null);

            AvatarAttribute privateK = this.pubs.get (arg);
            // Check if it is a public key
            if (privateK != null) {
                String privateKStr = AVATAR2ProVerif.translateTerm (privateK, null);
                // Check if the corresponding private key has already been declared
                if (!allKnowledge.contains (privateK)) {
                    lastInstr = lastInstr.setNextInstr (new ProVerifProcNew (privateKStr, "bitstring"));
                    this.nameEquivalence.put (privateK, privateK);
                    allKnowledge.add (privateK);
                }

                // Let the public key
                lastInstr = lastInstr.setNextInstr (new ProVerifProcLet (new ProVerifVar[] {new ProVerifVar (str, "bitstring")}, PK_PK + "(" + privateKStr + ")"));;
                // Make the public key public
                tmpInstr = new ProVerifProcRaw ("out (" + CH_MAINCH + ", " + str + ");");
            } else {
                tmpInstr = new ProVerifProcNew (str, "bitstring");
                this.nameEquivalence.put (arg, arg);
            }

            TraceManager.addDev("|    AvatarAttribute: " + str);
            lastInstr = lastInstr.setNextInstr (tmpInstr);
            allKnowledge.add (arg);
        }

        for (AvatarPragma pragma: this.avspec.getPragmas ())
            if (pragma instanceof AvatarPragmaPublic)
                for (AvatarAttribute attr: pragma.getArgs ())
                    if (attr.getBlock () == ab)
                        lastInstr = lastInstr.setNextInstr ( new ProVerifProcRaw ("out (" + CH_MAINCH + ", " + AVATAR2ProVerif.translateTerm (attr, null) + ");"));

        // Call the first "real" process
        String tmp = "out (" + CHCTRL_CH + ", " + CHCTRL_ENCRYPT + " ((call__" + ab.getName () + "__0";
        for(ProVerifVar aa: this.getAttributesFromBlock (ab))
            tmp += ", " + aa.getName ();
        lastInstr = lastInstr.setNextInstr (new ProVerifProcRaw (tmp + ")))"));

        // Generate a new process for every simplified element of the block's state machine
        HashMap<AvatarStateMachineElement, Integer> simplifiedElements = ab.getStateMachine ().getSimplifiedElements ();

        for (AvatarStateMachineElement asme: simplifiedElements.keySet ())
            if (asme != null) {
                HashMap<AvatarAttribute, Integer> attributeCmp = new HashMap<AvatarAttribute, Integer> ();
                for (AvatarAttribute attr: ab.getAttributes ())
                    attributeCmp.put (attr, 0);

                // Create the ProVerif process and add it to the ProVerif specification
                ProVerifProcInstr p = new ProVerifProcess(AVATAR2ProVerif.makeAttrName(ab.getName(), simplifiedElements.get (asme).toString ()), new ProVerifVar[] {});
                this.spec.addDeclaration (p);

                // Read and decrypt control data: variables sent to the process and the call__num variable
                p = p.setNextInstr (new ProVerifProcIn (CHCTRL_CH, new ProVerifVar[] {new ProVerifVar ("chControlData", "bitstring")}));
                LinkedList<ProVerifVar> attributes = new LinkedList<ProVerifVar> ();
                attributes.add (new ProVerifVar ("call__num", "bitstring"));
                for (AvatarAttribute attr: ab.getAttributes ()) {
                    Integer c = attributeCmp.get (attr) + 1;
                    attributeCmp.put (attr, c);
                    attributes.add (new ProVerifVar (AVATAR2ProVerif.translateTerm (attr, attributeCmp), "bitstring"));
                }
                p = p.setNextInstr (new ProVerifProcLet (attributes.toArray (new ProVerifVar[attributes.size()]), CHCTRL_DECRYPT + " (chControlData)"));

                // Check that call__num variable really corresponds to this process
                p = p.setNextInstr (new ProVerifProcITE ("call__num = call__" + ab.getName () + "__" + simplifiedElements.get (asme)));

                // Create an object that will serve as an argument passed to the translation functions
                ProVerifTranslatorParameter arg = new ProVerifTranslatorParameter ();
                arg.block = ab;
                arg.lastInstr = p;
                arg.simplifiedElements = simplifiedElements;
                arg.attributeCmp = attributeCmp;
                arg.lastASME = null;

                // Translate this simplified element
                asme.translate (this, arg);
            }
    }

    class ProVerifTranslatorParameter {
        AvatarBlock block;
        ProVerifProcInstr lastInstr;
        HashMap<AvatarStateMachineElement, Integer> simplifiedElements;
        HashMap<AvatarAttribute, Integer> attributeCmp;
        AvatarStateMachineElement lastASME;
    }

    /**
     * Commodity method that translates the transition to the next Avatar state machine element
     */
    private void translateNext (AvatarStateMachineElement next, Object _arg) {
        ProVerifTranslatorParameter arg = (ProVerifTranslatorParameter) _arg;
        // Check if next is not null
        if (next != null) {

            // Check if next is the root of a process
            Integer n = arg.simplifiedElements.get (next);
            if (n != null) {
                // If next is the root of a process send the attributes on the control channel
                String tmp = "out (" + CHCTRL_CH + ", " + CHCTRL_ENCRYPT + " ((call__" + arg.block.getName () + "__" + n;
                for(AvatarAttribute aa: arg.block.getAttributes ())
                    tmp += ", " + AVATAR2ProVerif.translateTerm (aa, arg.attributeCmp);

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
            else {
                boolean first = true;
                for(String value: _asme.getValues ()) {
                    AvatarTerm term = AvatarTerm.createFromString (arg.block, value);
                    if (term == null)
                        // TODO: warning
                        continue;

                    if (first)
                        first = false;
                    else
                        tmp += ", ";
                    tmp += AVATAR2ProVerif.translateTerm (term, arg.attributeCmp);
                }
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
                for(String value: _asme.getValues ()) {
                    AvatarTerm term = AvatarTerm.createFromString (arg.block, value);
                    if (term == null || ! (term instanceof AvatarAttribute))
                        // TODO: error
                        continue;

                    AvatarAttribute attr = (AvatarAttribute) term;
                    Integer c = arg.attributeCmp.get (attr) + 1;
                    arg.attributeCmp.put (attr, c);
                    vars.add (new ProVerifVar (AVATAR2ProVerif.translateTerm (attr, arg.attributeCmp), "bitstring"));
                }

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
        arg.lastASME = _asme;
        // FIXME: can't be followed by multiple states ?
        this.translateNext (_asme.getNext(0), arg);
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
            String tmp = AVATAR2ProVerif.translateGuard(_asme.getGuard().getRealGuard (arg.lastASME), arg.attributeCmp);
            if (tmp != null) {
                TraceManager.addDev("|    |    transition is guarded by " + tmp);
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcITE (tmp));
            } else {
                // TODO: warning
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
            AvatarLeftHand leftHand = action.getLeftHand ();

            // Compute right part of assignment
            AvatarTerm rightHand = action.getRightHand ();
            String proVerifRightHand = null;
            if (rightHand instanceof AvatarTermFunction) {
                // If it's a function call
                String name = ((AvatarTermFunction) rightHand).getMethod ().getName ();
                LinkedList<AvatarTerm> args = ((AvatarTermFunction) rightHand).getArgs ().getComponents ();

                if (name.equals ("concat2") || name.equals ("concat3") || name.equals ("concat4")) {
                    // If it's a concat function, just use tuples
                    boolean first = true;
                    proVerifRightHand = "(";
                    for (AvatarTerm argTerm: args) {
                        if (first)
                            first = false;
                        else
                            proVerifRightHand += ", ";
                        proVerifRightHand += AVATAR2ProVerif.translateTerm (argTerm, arg.attributeCmp);
                    }
                    proVerifRightHand += ")";
                } else
                    // Else use the function as is
                    proVerifRightHand = AVATAR2ProVerif.translateTerm (rightHand, arg.attributeCmp);
            } else
                // If it's not a function, use it as is
                proVerifRightHand = AVATAR2ProVerif.translateTerm (rightHand, arg.attributeCmp);

            // Compute left hand part of the assignment
            LinkedList<ProVerifVar> proVerifLeftHand = new LinkedList<ProVerifVar> ();
            if (leftHand instanceof AvatarTuple)
                for (AvatarTerm term: ((AvatarTuple) leftHand).getComponents ()) {
                    if (! (term instanceof AvatarAttribute))
                        // TODO: warning
                        continue;

                    AvatarAttribute attr = (AvatarAttribute) term;
                    Integer c = arg.attributeCmp.get (attr) + 1;
                    arg.attributeCmp.put (attr, c);
                    proVerifLeftHand.add (new ProVerifVar (AVATAR2ProVerif.translateTerm (attr, arg.attributeCmp), "bitstring"));
                }
            else if (leftHand instanceof AvatarAttribute) {
                AvatarAttribute attr = (AvatarAttribute) leftHand;
                Integer c = arg.attributeCmp.get (attr) + 1;
                arg.attributeCmp.put (attr, c);
                proVerifLeftHand.add (new ProVerifVar (AVATAR2ProVerif.translateTerm (attr, arg.attributeCmp), "bitstring"));
            }

            if (proVerifRightHand != null && proVerifLeftHand.size () > 0)
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcLet (proVerifLeftHand.toArray (new ProVerifVar[proVerifLeftHand.size ()]), proVerifRightHand));
        }

        // Loop over all function calls
        for(AvatarTermFunction action: _asme.getFunctionCalls ()) {
            String name = action.getMethod ().getName ();

            if (name.equals ("get2") || name.equals ("get3") || name.equals ("get4")) {
                // If the function called is get[234]
                LinkedList<AvatarTerm> args = action.getArgs ().getComponents ();
                int index = (int) name.charAt (3) - 48;

                boolean ok = true;
                for (int i = 1; i <= index; i++)
                    if (! (args.get(i) instanceof AvatarAttribute)) {
                        // TODO: raise warning
                        ok = false;
                    }

                if (ok) {
                    // Create the corresponding assignment
                    String rightHand = AVATAR2ProVerif.translateTerm (args.get (0), arg.attributeCmp);

                    LinkedList<ProVerifVar> tup = new LinkedList<ProVerifVar> ();
                    for (int i = 1; i <= index; i++) {
                        AvatarAttribute attr = (AvatarAttribute) args.get (i);
                        Integer c = arg.attributeCmp.get (attr) + 1;
                        arg.attributeCmp.put (attr, c);
                        tup.add (new ProVerifVar (AVATAR2ProVerif.translateTerm (attr, arg.attributeCmp), "bitstring"));
                    }

                    _lastInstr = _lastInstr.setNextInstr (new ProVerifProcLet (tup.toArray (new ProVerifVar[tup.size ()]), rightHand));
                }
            }
        }

        arg.lastInstr = _lastInstr;
        arg.lastASME = _asme;
        this.translateNext (_asme.getNext(0), arg);
    }

    public void translateState (AvatarState _asme, Object _arg) {
        TraceManager.addDev("|    State");
        ProVerifTranslatorParameter arg = (ProVerifTranslatorParameter) _arg;
        ProVerifProcInstr _lastInstr = arg.lastInstr;

        // Adding an event for reachability of the state
        _lastInstr = _lastInstr.setNextInstr (new ProVerifProcRaw ("event enteringState__" + arg.block.getName() + "__" + _asme.getName() + "()", true));

        // Adding an event if authenticity is concerned with that state
        for (AvatarPragma pragma: this.avspec.getPragmas ())
            if (pragma instanceof AvatarPragmaAuthenticity) {
                AvatarAttributeState attrA = ((AvatarPragmaAuthenticity) pragma).getAttrA ();
                AvatarAttributeState attrB = ((AvatarPragmaAuthenticity) pragma).getAttrB ();
                if (attrA.getState ().getName ().equals (_asme.getName ())) {
                    String sp = "authenticity__" + AVATAR2ProVerif.makeAttrName (attrA.getAttribute ().getBlock ().getName (), attrA.getAttribute ().getName (), _asme.getName ()) + " (" + AVATAR2ProVerif.makeAttrName (attrA.getAttribute ().getBlock ().getName (), attrA.getAttribute ().getName (), arg.attributeCmp.get (attrA.getAttribute ()).toString ()) + ")";
                    TraceManager.addDev("|    |    authenticity event " + sp + "added");
                    _lastInstr = _lastInstr.setNextInstr (new ProVerifProcRaw ("event " + sp, true));
                }
                if (attrB.getState ().getName ().equals (_asme.getName ())) {
                    String sp = "authenticity__" + AVATAR2ProVerif.makeAttrName (attrB.getAttribute ().getBlock ().getName (), attrB.getAttribute ().getName (), _asme.getName ()) + " (" + AVATAR2ProVerif.makeAttrName (attrB.getAttribute ().getBlock ().getName (), attrB.getAttribute ().getName (), arg.attributeCmp.get (attrB.getAttribute ()).toString ()) + ")";
                    TraceManager.addDev("|    |    authenticity event " + sp + "added");
                    _lastInstr = _lastInstr.setNextInstr (new ProVerifProcRaw ("event " + sp, true));
                }
            }

        int nbOfNexts = _asme.nbOfNexts ();
        if (nbOfNexts == 0)
            return;

        if (nbOfNexts == 1) {
            arg.lastInstr = _lastInstr;
            this.translateNext (_asme.getNext(0), arg);

        } else if (_asme.hasElseChoiceType1()) {
            TraceManager.addDev("|    |    calling next ITE");
            ProVerifProcITE ite = new ProVerifProcITE (AVATAR2ProVerif.translateGuard (((AvatarTransition) _asme.getNext (0)).getGuard ().getRealGuard (_asme), arg.attributeCmp));

            HashMap<AvatarAttribute, Integer> attributeCmp = new HashMap<AvatarAttribute, Integer> (arg.attributeCmp);

            arg.lastInstr = _lastInstr.setNextInstr (ite);
            this.translateNext (_asme.getNext (0).getNext (0), arg);

            arg.attributeCmp = attributeCmp;
            arg.lastInstr = ite.getElse ();
            this.translateNext (_asme.getNext (1).getNext (0), arg);

        } else {
            TraceManager.addDev("|    |    non deterministic next state");
            for (int i=0; i<nbOfNexts-1; i++) {
                String choice = "choice__" + _asme.getName () + "__" + i;
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcNew (choice, "bitstring"));
                _lastInstr = _lastInstr.setNextInstr (new ProVerifProcRaw ("out (" + CH_MAINCH + ", " + choice + ");"));
            }
            _lastInstr = _lastInstr.setNextInstr (new ProVerifProcIn (CH_MAINCH, new ProVerifVar[] {new ProVerifVar ("choice__" + _asme.getName (), "bitstring")}));

            HashMap<AvatarAttribute, Integer> attributeCmp = arg.attributeCmp;
            for (int i=0; i<nbOfNexts-1; i++) {
                String choice = "choice__" + _asme.getName () + "__" + i;
                ProVerifProcITE ite = new ProVerifProcITE ("choice__" + _asme.getName () + " = " + choice);

                arg.attributeCmp = new HashMap<AvatarAttribute, Integer> (arg.attributeCmp);
                arg.lastASME = _asme;
                arg.lastInstr = _lastInstr.setNextInstr (ite);
                this.translateNext (_asme.getNext (i), arg);

                _lastInstr = ite.getElse ();
            }

            arg.attributeCmp = attributeCmp;
            arg.lastInstr = _lastInstr;
            this.translateNext (_asme.getNext (nbOfNexts-1), arg);
        }
    }

    public void translateRandom (AvatarRandom _asme, Object _arg) {
        TraceManager.addDev("|    Random");
        ProVerifTranslatorParameter arg = (ProVerifTranslatorParameter) _arg;
        ProVerifProcInstr _lastInstr = arg.lastInstr;

        AvatarTerm term = AvatarTerm.createFromString (arg.block, _asme.getVariable ());
        LinkedList<AvatarAttribute> names = new LinkedList<AvatarAttribute> ();

        if (term instanceof AvatarAttribute)
            names.add ((AvatarAttribute) term);
        else if (term instanceof AvatarTuple)
            for (AvatarTerm t: ((AvatarTuple) term).getComponents ())
                if (t instanceof AvatarAttribute)
                    names.add ((AvatarAttribute) t);

        for (AvatarAttribute attr: names) {
            Integer c = arg.attributeCmp.get (attr) + 1;
            arg.attributeCmp.put (attr, c);
            _lastInstr = _lastInstr.setNextInstr (new ProVerifProcNew (AVATAR2ProVerif.translateTerm (attr, arg.attributeCmp), "bitstring"));
        }

        arg.lastInstr = _lastInstr;
        this.translateNext (_asme.getNext(0), _arg);
    }

    public void translateStartState (AvatarStartState _asme, Object _arg) {
        this.translateNext (_asme.getNext(0), _arg);
    }

    public void translateTimerOperator (AvatarTimerOperator _asme, Object _arg) {
        this.translateNext (_asme.getNext(0), _arg);
    }

    public void translateStopState (AvatarStopState _asme, Object _arg) {
    }

    protected static String translateTerm (AvatarTerm term, HashMap<AvatarAttribute, Integer> attributeCmp) {
        if (term instanceof AvatarAttribute) {
            AvatarAttribute attr = (AvatarAttribute) term;
            if (attributeCmp != null)
                return AVATAR2ProVerif.makeAttrName (attr.getBlock ().getName (), attr.getName (), attributeCmp.get (attr).toString ());
            else
                return AVATAR2ProVerif.makeAttrName (attr.getBlock ().getName (), attr.getName ());
        }

        if (term instanceof AvatarConstant) {
            AvatarConstant constant = (AvatarConstant) term;
            return constant.getName ();
        }

        if (term instanceof AvatarTermFunction) {
            AvatarTuple args = ((AvatarTermFunction) term).getArgs ();
            AvatarMethod method = ((AvatarTermFunction) term).getMethod ();

            return method.getName () + " " + AVATAR2ProVerif.translateTerm (args, attributeCmp);
        }

        if (term instanceof AvatarTuple) {
            String result = "(";
            boolean first = true;
            for (AvatarTerm arg: ((AvatarTuple) term).getComponents ()) {
                if (first)
                    first = false;
                else
                    result += ", ";
                result += AVATAR2ProVerif.translateTerm (arg, attributeCmp);
            }
            result += ")";

            return result;
        }

        return null;
    }

    // Supported guards: a == b, not(a == b), g1 and g2, g1 or g2
    // -> transformed into a = b, a <> b, g1 && g2, g1 || g2
    // Returns nulls otherwise
    private static String translateGuard (AvatarGuard _guard, HashMap<AvatarAttribute, Integer> attributeCmp) {
        if (_guard == null || _guard instanceof AvatarGuardEmpty)
            return null;

        if (_guard instanceof AvatarGuardElse)
            // TODO: warning
            return null;

        if (_guard instanceof AvatarSimpleGuardMono) {
            String term = AVATAR2ProVerif.translateTerm (((AvatarSimpleGuardMono) _guard).getTerm (), attributeCmp);
            if (term != null)
                return term + " = " + TRUE;

            return null;
        }

        if (_guard instanceof AvatarSimpleGuardDuo) {
            String delim = null;
            String termA = AVATAR2ProVerif.translateTerm (((AvatarSimpleGuardDuo) _guard).getTermA (), attributeCmp);
            String termB = AVATAR2ProVerif.translateTerm (((AvatarSimpleGuardDuo) _guard).getTermB (), attributeCmp);
            if (((AvatarSimpleGuardDuo) _guard).getBinaryOp ().equals ("=="))
                delim = "=";
            else if (((AvatarSimpleGuardDuo) _guard).getBinaryOp ().equals ("!="))
                delim = "<>";

            if (termA != null && termB != null && delim != null)
                return termA + " " + delim + " " + termB;

            return null;
        }

        if (_guard instanceof AvatarUnaryGuard) {
            String before = ((AvatarUnaryGuard) _guard).getBefore ();
            String after = ((AvatarUnaryGuard) _guard).getAfter ();
            AvatarGuard guard = ((AvatarUnaryGuard) _guard).getGuard ();

            String beforeProV = null;
            String afterProV = ")";

            if (before.equals ("not("))
                beforeProV = "not (";
            else if (before.equals ("("))
                beforeProV = "(";

            String guardProV = AVATAR2ProVerif.translateGuard (guard, attributeCmp);

            if (beforeProV != null && guardProV != null)
                return beforeProV + guardProV + afterProV;

            return null;
        }

        if (_guard instanceof AvatarBinaryGuard) {
            String delim = ((AvatarBinaryGuard) _guard).getBinaryOp ();
            AvatarGuard guardA = ((AvatarBinaryGuard) _guard).getGuardA ();
            AvatarGuard guardB = ((AvatarBinaryGuard) _guard).getGuardB ();

            String delimProV = null;

            if (delim.equals ("and"))
                delimProV = "&&";
            else if (delim.equals ("or"))
                delimProV = "||";

            String guardAProV = AVATAR2ProVerif.translateGuard (guardA, attributeCmp);
            String guardBProV = AVATAR2ProVerif.translateGuard (guardB, attributeCmp);

            if (delimProV != null && guardAProV != null && guardBProV != null)
                return guardAProV + " " + delimProV + " " + guardBProV;
        }

        return null;
    }
}
