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

package avatartranslator.modelcheckervalidator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import launcher.LauncherException;
import launcher.RshClient;
import ui.GTURTLEModeling;
import ui.MainGUI;
import ui.TGComponent;
import ui.TGComponentAndUPPAALQuery;
import ui.TURTLEPanel;
import uppaaldesc.UPPAALSpec;
import uppaaldesc.UPPAALTemplate;
import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachineElement;
import avatartranslator.modelchecker.AvatarModelChecker;
import avatartranslator.modelchecker.SafetyProperty;
import avatartranslator.modelchecker.SpecificationPropertyPhase;
import avatartranslator.modelchecker.SpecificationReachability;
import avatartranslator.touppaal.AVATAR2UPPAAL;


/**
 * Class ModelCheckerValidator
 * Model-checker validator
 * Creation: 05/05/2020
 *
 * @author Alessandro TEMPIA CALVINO
 * @version 1.0 05/05/2020
 */
public class ModelCheckerValidator {
    public final static int STUDY_SELECTED = 1;
    public final static int STUDY_ALL = 2;
    
    private static final java.util.Set<String> PROP_VERIFIED_LABELS = new HashSet<String>();
    private static final java.util.Set<String> PROP_NOT_VERIFIED_LABELS = new HashSet<String>();
    
    static {
//        for (final String label : ConfigurationTTool.UPPAALPropertyVerifMessage.split(",")) {
//            if (!label.trim().isEmpty()) {
//                PROP_VERIFIED_LABELS.add(label.trim());
//            }
//        }

        // Handle the case where nothing is defined in the configuration
        if (PROP_VERIFIED_LABELS.isEmpty()) {
            PROP_VERIFIED_LABELS.add("Property is satisfied");
            PROP_VERIFIED_LABELS.add("Formula is satisfied");
        }

//        for (final String label : ConfigurationTTool.UPPAALPropertyNotVerifMessage.split(",")) {
//            if (!label.trim().isEmpty()) {
//                PROP_NOT_VERIFIED_LABELS.add(label.trim());
//            }
//        }

        // Handle the case where nothing is defined in the configuration
        if (PROP_NOT_VERIFIED_LABELS.isEmpty()) {
            PROP_NOT_VERIFIED_LABELS.add("Property is NOT satisfied");
            PROP_NOT_VERIFIED_LABELS.add("Formula is NOT satisfied");
        }
    }
    
    
    
    /*
     * Starts the internal Avatar model-checker and UPPAAL. It verifies that the results are the same.
     * Returns true if the results are equal or UPPAAL is not installed. Returns false if the results are different.
     */
    public static boolean validate(MainGUI mgui, int rStudy, int lStudy, boolean sStudy, boolean dStudy) {
        GTURTLEModeling gtm = mgui.gtm;
        TURTLEPanel tp = mgui.getCurrentTURTLEPanel();
        
        AvatarSpecification spec = gtm.getAvatarSpecification();
        String fileName = MainGUI.REMOTE_UPPAAL_FILE;
        String fn;
        int id;
        
        if (spec == null) {
            System.out.println("Spec not initialized\n");
            return false;
        }
        
        RshClient rshc = new RshClient(gtm.getUPPAALVerifierHost());
        
        // checking UPPAAL installation
        File uppaalVerifier= new File(gtm.getPathUPPAALVerifier());
        
        if (!uppaalVerifier.exists()) {
            System.out.println("UPPAAL not installed in path: " + gtm.getPathUPPAALVerifier() +  "\n");
            return true;
        }
        
        
        AvatarModelChecker amc = new AvatarModelChecker(spec);
        amc.setIgnoreEmptyTransitions(true);
        amc.setIgnoreConcurrenceBetweenInternalActions(true);
        amc.setIgnoreInternalStates(true);
        amc.setComputeRG(false);
        
        if (rStudy == STUDY_SELECTED) {
            amc.setReachabilityOfSelected();
        } else if (rStudy == STUDY_ALL) {
            amc.setReachabilityOfAllStates();
        }
        
        if (lStudy == STUDY_SELECTED) {
            amc.setLivenessOfSelected();
        } else if (lStudy == STUDY_ALL) {
            amc.setLivenessOfAllStates();
        }
        
        if (sStudy) {
            amc.setSafetyAnalysis();
        }
        
        if (dStudy) {
            amc.setCheckNoDeadlocks(true);
        }
        
        //run first internal modelchecking
        amc.startModelCheckingProperties();
        
        //run UPPAAL
        StringBuilder diff = new StringBuilder();
        boolean equal = true;
        try {
            id = rshc.getId();
            fn = fileName.substring(0, fileName.length() - 4) + "_" + id;
            rshc.sendFileData(fn + ".xml", gtm.getLastUPPAALSpecification().getStringSpec());

            int uResult, index;
            String query;
            
            if (dStudy) {
                System.out.println("Deadlock Study");
                uResult = workQuery(gtm, rshc, "A[] not deadlock", fn, true);
                if (!((uResult == 0 && amc.getNbOfDeadlocks() > 0) || (uResult == 1 && amc.getNbOfDeadlocks() == 0))) {
                    diff.append("No Deadlock: amc = " + (amc.getNbOfDeadlocks() == 0) + "; uppaal = " + (uResult == 1) + "\n");
                    equal = false;
                }
            }
            
            if (rStudy == STUDY_SELECTED || rStudy == STUDY_ALL) {
                boolean match;
                ArrayList<SpecificationReachability> reachabilities = amc.getReachabilities();
                java.util.List<TGComponentAndUPPAALQuery> list = mgui.gtm.getUPPAALQueries(tp, rStudy == STUDY_ALL);
                for (TGComponentAndUPPAALQuery cq : list) {
                    String s = cq.uppaalQuery;
                    index = s.indexOf('$');
                    if ((index != -1)) {
                        query = s.substring(0, index);
                        uResult = workQuery(gtm, rshc, "E<> " + query, fn, true);
                        match = false;
                        for (SpecificationReachability sr : reachabilities) {
                            if (sr.ref1 instanceof AvatarStateMachineElement) {
                                Object o = ((AvatarStateMachineElement)sr.ref1).getReferenceObject();
                                if (o instanceof TGComponent) {
                                    TGComponent tgc = (TGComponent) (o);
                                    if (cq.tgc == tgc) {
                                        match = true;
                                        if (!(uResult == 1 && sr.result == SpecificationPropertyPhase.SATISFIED ||
                                                uResult == 0 && sr.result == SpecificationPropertyPhase.NONSATISFIED)) {
                                            diff.append("Reachability " + ((AvatarStateMachineElement)sr.ref1).getExtendedName() + ": amc = " + (sr.result == SpecificationPropertyPhase.SATISFIED) + "; uppaal = " + (uResult == 1) + "\n");
                                            equal = false;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        if (!match) {
                            diff.append("Reachability query " + s + " not matched\n");
                        }
                    }
                }
            }
            
            if (lStudy == STUDY_SELECTED || lStudy == STUDY_ALL) {
                boolean match;
                ArrayList<SafetyProperty> livenesses = amc.getLivenesses();
                java.util.List<TGComponentAndUPPAALQuery> list = mgui.gtm.getUPPAALQueries(tp, lStudy == STUDY_ALL);
                for (TGComponentAndUPPAALQuery cq : list) {
                    String s = cq.uppaalQuery;
                    index = s.indexOf('$');
                    if ((index != -1)) {
                        query = s.substring(0, index);
                        uResult = workQuery(gtm, rshc, "A<> " + query, fn, true);
                        match = false;
                        for (SafetyProperty sp : livenesses) {
                            Object o = sp.getState().getReferenceObject();
                            if (o instanceof TGComponent) {
                                TGComponent tgc = (TGComponent) (o);
                                if (cq.tgc == tgc) {
                                    match = true;
                                    if (!(uResult == 1 && sp.getPhase() == SpecificationPropertyPhase.SATISFIED ||
                                            uResult == 0 && sp.getPhase() == SpecificationPropertyPhase.NONSATISFIED)) {
                                        diff.append("Liveness " + sp.getState().getExtendedName() + ": amc = " + (sp.getPhase() == SpecificationPropertyPhase.SATISFIED) + "; uppaal = " + (uResult == 1) + "\n");
                                        equal = false;
                                    }
                                    break;
                                }
                            }
                        }
                        if (!match) {
                            diff.append("Liveness query " + s + " not matched\n");
                        }
                    }
                }
            }
            
            if (sStudy) {
                ArrayList<SafetyProperty> safeties = amc.getSafeties();
                for (SafetyProperty sp : safeties) {
                    query = translateCustomQuery(gtm, spec, removeExpectedResult(sp.getRawProperty()));
                    uResult = workQuery(gtm, rshc, query, fn, sp.expectedResult);
                    if (!(uResult == 1 && sp.getPhase() == SpecificationPropertyPhase.SATISFIED ||
                            uResult == 0 && sp.getPhase() == SpecificationPropertyPhase.NONSATISFIED)) {
                        diff.append("Safety " + sp.getRawProperty() + ": amc = " + (sp.getPhase() == SpecificationPropertyPhase.SATISFIED) + "; uppaal = " + (uResult == 1) + "\n");
                        equal = false;
                    }
                }
                
            }
            
            rshc.deleteFile(fn + ".xml");
            rshc.deleteFile(fn + ".q");
            rshc.deleteFile(fn + ".res");
            rshc.deleteFile(fn + ".xtr");

            rshc.freeId(id);
        } catch (Exception e) {
            System.out.println("Shit: " + e + "\n");
            return false;
        }
        
        if (diff.length() != 0) {
            System.out.println("Avatar-UPPAAL Compare diff:\n" + diff);
        }
        
        return equal;
    }
    
    
    private static String removeExpectedResult(String query) {
        if (query.matches("^[TtFf]\\s.*")) {
            return query.substring(2).trim();
        } else {
            return query;
        }
    }
    
    
    // return: -1: error
    // return: 0: property is NOt satisfied
    // return: 1: property is satisfied
    private static int workQuery(GTURTLEModeling gtm, RshClient rshc, String query, String fn, boolean expectedResult) throws LauncherException {

        int ret;
        //TraceManager.addDev("Working on query: " + query);

        String cmd, data;

        rshc.sendFileData(fn + ".q", query);
        
        cmd = gtm.getPathUPPAALVerifier() + " -u " + fn + ".xml " + fn + ".q";
        data = processCmd(rshc, cmd);

        //NOTE: [error] is only visible if Error Stream is parsed
        if (data.trim().length() == 0) {
            //jta.append("The verifier of UPPAAL could not be started: error\n");
            throw new LauncherException("");
        }
        // Issue #35: Different labels for UPPAAL 4.1.19
        else if (checkAnalysisResult(data, PROP_VERIFIED_LABELS)) {
            if (expectedResult) {
                ret = 1;
            } else {
                ret = 0;
            }
        }
        // Issue #35: Different labels for UPPAAL 4.1.19
        else if (checkAnalysisResult(data, PROP_NOT_VERIFIED_LABELS)) {
            if (!expectedResult) {
                ret = 1;
            } else {
                ret = 0;
            }
        } else {
           ret = -1;
        }

        return ret;
    }
    
    
    private static String processCmd(RshClient rshc, String cmd) throws LauncherException {
        try {
            rshc.setCmd(cmd);

            rshc.sendExecuteCommandRequest(true);

            final String data = rshc.getDataFromProcess();

            final Integer retCode = rshc.getProcessReturnCode();

            if (retCode == null || retCode != 0) {
                final String message;

                if (data == null || data.isEmpty()) {
                    message = "Error executing command '" + cmd + "' with return code " + retCode;
                } else {
                    message = data;
                }

                throw new LauncherException(System.lineSeparator() + message);
            }

            return data;
        } catch (Exception e) {
            return "";
        }
    }
    
    
    private static boolean checkAnalysisResult(final String resultData, final Collection<String> labels) {
        for (final String verifiedLabel : labels) {
            if (resultData.contains(verifiedLabel)) {
                return true;
            }
        }
        return false;
    }


    private static String translateCustomQuery(GTURTLEModeling gtm, AvatarSpecification avspec, String query) {
        UPPAALSpec spec = gtm.getLastUPPAALSpecification();
        AVATAR2UPPAAL avatar2uppaal = gtm.getAvatar2Uppaal();
        Map<String, String> hash = avatar2uppaal.getHash();
        String finQuery = query + " ";

        for (String str : hash.keySet()) {
            finQuery = finQuery.replaceAll(str + "\\s", hash.get(str));
            finQuery = finQuery.replaceAll(str + "\\)", hash.get(str) + "\\)");
            finQuery = finQuery.replaceAll(str + "\\-", hash.get(str) + "\\-");
        }
        
        if (avspec == null) {
            return "";
        }

        java.util.List<AvatarBlock> blocks = avspec.getListOfBlocks();
        java.util.List<String> matches = new java.util.ArrayList<String>();
        for (AvatarBlock block : blocks) {
            UPPAALTemplate temp = spec.getTemplateByName(block.getName());
            if (temp != null) {
                if (finQuery.contains(block.getName() + ".")) {
                    matches.add(block.getName());
                }
            }
        }


        for (String match : matches) {
            boolean ignore = false;
            for (String posStrings : matches) {
                if (!posStrings.equals(match) && posStrings.contains(match)) {
                    ignore = true;
                }
            }
            if (!ignore) {
                UPPAALTemplate temp = spec.getTemplateByName(match);
                int index = avatar2uppaal.getIndexOfTranslatedTemplate(temp);
                finQuery = finQuery.replaceAll(match, match + "__" + index);
            }
        }

        return finQuery;
    }

}
