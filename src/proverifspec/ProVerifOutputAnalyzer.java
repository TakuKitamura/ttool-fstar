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
 * Class ProVerifOutputAnalyzer
 * Creation: 16/09/2010
 * @version 1.0 16/09/2010
 * @author Ludovic APVRILLE
 * @see
 */

package proverifspec;

import java.util.*;
import java.util.regex.*;
import myutil.*;
import java.io.*;

import avatartranslator.toproverif.AVATAR2ProVerif;
import avatartranslator.AvatarAttribute;
import avatartranslator.AvatarBlock;


public class ProVerifOutputAnalyzer {


    private LinkedList<String> reachableEvents;
    private LinkedList<String> nonReachableEvents;
    private LinkedList<String> secretTerms;
    private LinkedList<String> nonSecretTerms;
    private LinkedList<String> satisfiedAuthenticity;
    private LinkedList<String> satisfiedWeakAuthenticity;
    private LinkedList<String> nonSatisfiedAuthenticity;
    private LinkedList<String> errors;
    private LinkedList<String> notproved;
    private final static String typedEvent = "not event(";
    private final static String untypedEvent = "not ev:";
    private final static String typedFalse = ") is false";
    private final static String typedTrue = ") is true";
    private final static String untypedFalse = " is false";
    private final static String untypedTrue = " is true";
    private final static String typedSecret = "not attacker(";
    private final static String untypedSecret = "not attacker:";
    private final static String typedStrongAuth = "inj-event(authenticity__";
    private final static String untypedStrongAuth = "evinj:authenticity__";
    private final static String typedWeakAuth = "(but event(authenticity__";
    private final static String untypedWeakAuth = "(but ev:authenticity__";
    private final static String typedAuthSplit = "==> inj-event(authenticity__";
    private final static String typedWeakAuthSplit = "==> event(authenticity__";
    private final static String untypedAuthSplit = "==> evinj:authenticity__";
    private final static String untypedWeakAuthSplit = "==> ev:authenticity__";

    private AVATAR2ProVerif avatar2proverif;

    public ProVerifOutputAnalyzer(AVATAR2ProVerif avatar2proverif) {
        this.avatar2proverif = avatar2proverif;

        reachableEvents = new LinkedList<String>();
        nonReachableEvents = new LinkedList<String>();
        secretTerms = new LinkedList<String>();
        nonSecretTerms = new LinkedList<String>();
        satisfiedAuthenticity = new LinkedList<String>();
        satisfiedWeakAuthenticity = new LinkedList<String>();
        nonSatisfiedAuthenticity = new LinkedList<String>();

        errors = new LinkedList<String>();
        notproved = new LinkedList<String>();
    }

    public void analyzeTypedOutput(String _s) {
        String str, previous="";
        int index0, index1;

        BufferedReader reader = new BufferedReader(new StringReader(_s));
        try {
            while ((str = reader.readLine()) != null) {
                if (str.contains("RESULT")){
                    if (str.contains(typedEvent)){
                        if (str.contains(typedTrue)){
                            //Add string between tags
                            //Pattern.quote converts string into regex adding escape characters
                            nonReachableEvents.add(str.split(Pattern.quote(typedEvent))[1].split(Pattern.quote(typedTrue))[0]);
                        }
                        else if (str.contains(typedFalse)) {
                            reachableEvents.add(str.split(Pattern.quote(typedEvent))[1].split(Pattern.quote(typedFalse))[0]);
                        }
                    }
                    else if (str.contains(typedSecret)){
                        if (str.contains(typedTrue)){
                            //Add string between tags
                            secretTerms.add(str.split(Pattern.quote(typedSecret))[1].split("\\[")[0]);
                        }
                        else if (str.contains(typedFalse)) {
                            nonSecretTerms.add(str.split(Pattern.quote(typedSecret))[1].split("\\[")[0]);
                        }
                    }
                    else if (str.contains(typedStrongAuth)){
                        if (str.contains(typedTrue)){
                            //Add string between tags
                            satisfiedAuthenticity.add(str.split(typedStrongAuth)[1].split(typedAuthSplit)[0].split("\\(")[0] + " ==> " + str.split(typedAuthSplit)[1].split("\\(")[0]);
                        }
                        else if (str.contains(typedFalse)) {
                            nonSatisfiedAuthenticity.add(str.split(Pattern.quote(typedStrongAuth))[1].split("\\(")[0] + " ==> " + str.split(Pattern.quote(typedAuthSplit))[1].split("\\(")[0]);
                        }
                    }
                    else if (str.contains(typedWeakAuth)) {
                        if (str.contains(typedTrue)){
                            //Add string between tags
                            satisfiedWeakAuthenticity.add(str.split(Pattern.quote(typedWeakAuth))[1].split("\\(")[0] + " ==> " + str.split(Pattern.quote(typedWeakAuthSplit))[1].split("\\(")[0]);
                        }    	
                    }
                }
                if (str.contains("Error:")){
                    errors.add(str + ": " + previous);
                }
                else if (str.contains("cannot be proved")){
                    notproved.add(str);
                }
            }    
            previous = str;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void analyzeOutput(String _s, boolean isTyped) {
        String str, previous="";
        int index0, index1;
        if (isTyped) {
            analyzeTypedOutput(_s);
            return;
        }
        BufferedReader reader = new BufferedReader(new StringReader(_s));
        try {
            while ((str = reader.readLine()) != null) {
                if (str.contains("RESULT")){
                    if (str.contains(untypedEvent)){
                        if (str.contains(untypedTrue)){
                            //Add string between tags
                            //Pattern.quote converts string into regex adding escape characters
                            nonReachableEvents.add(str.split(Pattern.quote(untypedEvent))[1].split("\\(")[0]);
                        }
                        else if (str.contains(untypedFalse)) {
                            reachableEvents.add(str.split(Pattern.quote(untypedEvent))[1].split("\\(")[0]);
                        }
                    }
                    else if (str.contains(untypedSecret)){
                        if (str.contains(untypedTrue)){
                            //Add string between tags
                            secretTerms.add(str.split(Pattern.quote(untypedSecret))[1].split("\\[")[0]);
                        }
                        else if (str.contains(untypedFalse)) {
                            nonSecretTerms.add(str.split(Pattern.quote(untypedSecret))[1].split("\\[")[0]);
                        }
                    }
                    else if (str.contains(untypedStrongAuth)){
                        if (str.contains(untypedTrue)){
                            //Add string between tags
                            satisfiedAuthenticity.add(str.split(untypedStrongAuth)[1].split(untypedAuthSplit)[0].split("\\(")[0] + " ==> " + str.split(untypedAuthSplit)[1].split("\\(")[0]);
                        }
                        else if (str.contains(untypedFalse)) {
                            nonSatisfiedAuthenticity.add(str.split(Pattern.quote(untypedStrongAuth))[1].split("\\(")[0] + " ==> " + str.split(Pattern.quote(untypedAuthSplit))[1].split("\\(")[0]);
                        }
                    }
                    else if (str.contains(untypedWeakAuth)) {
                        if (str.contains(untypedTrue)){
                            //Add string between tags
                            satisfiedWeakAuthenticity.add(str.split(Pattern.quote(untypedWeakAuth))[1].split("\\(")[0] + " ==> " + str.split(Pattern.quote(untypedWeakAuthSplit))[1].split("\\(")[0]);
                        }    	
                    }
                }
                if (str.contains("Error:")){
                    errors.add(str + ": " + previous);
                }
                else if (str.contains("cannot be proved")){
                    notproved.add(str);
                }
            }    
            previous = str;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<String> getReachableEvents() {
        return reachableEvents;
    }

    public LinkedList<String> getNonReachableEvents() {
        return nonReachableEvents;
    }

    public LinkedList<AvatarAttribute> getSecretTerms() {
        // FIXME composed Types ?
        LinkedList<AvatarAttribute> result = new LinkedList<AvatarAttribute> ();
        for (AvatarBlock block: this.avatar2proverif.getAvatarSpecification ().getListOfBlocks ())
            for (AvatarAttribute attr: block.getAttributes ()) {
                String trueName = this.avatar2proverif.getTrueName (attr);
                if (this.secretTerms.contains (trueName))
                    result.add (attr);
            }
        return result;
    }

    // FIXME what about cannot be proved ?

    public LinkedList<AvatarAttribute> getNonSecretTerms() {
        // FIXME composed Types ?
        LinkedList<AvatarAttribute> result = new LinkedList<AvatarAttribute> ();
        for (AvatarBlock block: this.avatar2proverif.getAvatarSpecification ().getListOfBlocks ())
            for (AvatarAttribute attr: block.getAttributes ()) {
                String trueName = this.avatar2proverif.getTrueName (attr);
                if (this.nonSecretTerms.contains (trueName))
                    result.add (attr);
            }
        return result;
    }

    public LinkedList<String> getSatisfiedAuthenticity() {
        return satisfiedAuthenticity;
    }

    public LinkedList<String> getSatisfiedWeakAuthenticity() {
        return satisfiedWeakAuthenticity;
    }

    public LinkedList<String> getNonSatisfiedAuthenticity() {
        return nonSatisfiedAuthenticity;
    }

    public LinkedList<String> getErrors() {
        return errors;
    }

    public LinkedList<String> getNotProved() {
        return notproved;
    }

}
