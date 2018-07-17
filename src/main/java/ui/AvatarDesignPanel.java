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




package ui;

import avatartranslator.AvatarAttribute;
import avatartranslator.AvatarPragmaAuthenticity;
import avatartranslator.AvatarPragmaReachability;
import avatartranslator.AvatarPragmaSecret;
import avatartranslator.AvatarPragma;
import avatartranslator.AvatarPragmaLatency;
import myutil.GraphicLib;
import proverifspec.ProVerifOutputAnalyzer;
import proverifspec.ProVerifQueryAuthResult;
import proverifspec.ProVerifQueryResult;
import proverifspec.ProVerifResultTrace;

import ui.avatarbd.*;
import ui.avatardd.ADDDiagramPanel;
import ui.avatarsmd.AvatarSMDPanel;
import ui.avatarsmd.AvatarSMDState;
import ui.avatarsmd.AvatarSMDToolBar;
import ui.interactivesimulation.SimulationLatency;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
   * Class AvatarDesignPanel
   * Management of Avatar block panels
   * Creation: 06/04/2010
   * @version 1.0 06/04/2010
   * @author Ludovic APVRILLE
   * @see MainGUI
 */
public class AvatarDesignPanel extends TURTLEPanel {
    public AvatarBDPanel abdp;


    public AvatarDesignPanel(MainGUI _mgui) {
        super(_mgui);
    	if (_mgui==null){
    		//for unit testing only
   			abdp = new AvatarBDPanel(null,null);
   			return;
    	}
        // Issue #41 Ordering of tabbed panes 
        tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();
        
        cl = new ChangeListener() {
        	
        	@Override
            public void stateChanged(ChangeEvent e){
                mgui.paneDesignAction(e);
            }
        };

        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));
    }


    public void setValidated(LinkedList<AvatarBDStateMachineOwner> _validated) {
        if (abdp != null) {
            abdp.setValidated(_validated);
        }
    }

    public void setIgnored(LinkedList<AvatarBDStateMachineOwner> _ignored) {
        if (abdp != null) {
            abdp.setIgnored(_ignored);
        }
    }

    public void setOptimized(boolean _optimized) {
        if (abdp != null) {
            abdp.setOptimized(_optimized);
        }
    }

    public LinkedList<AvatarBDStateMachineOwner> getValidated() {
        if (abdp != null) {
            return abdp.getValidated();
        }
        return null;
    }

    public LinkedList<AvatarBDStateMachineOwner> getIgnored() {
        if (abdp != null) {
            return abdp.getIgnored();
        }
        return null;
    }

    public boolean getOptimized() {
        if (abdp != null) {
            return abdp.getOptimized();
        }
        return true;
    }

    public AvatarBDPanel getAvatarBDPanel() {
        return abdp;
    }

    public AvatarSMDPanel getAvatarSMDPanel(String name) {
        AvatarSMDPanel asmdp;
        for(int i=1; i<panels.size(); i++) {
            asmdp = (AvatarSMDPanel)(panels.elementAt(i));
            if (asmdp.getName().compareTo(name) ==0) {
                return asmdp;
            }
        }
        return null;
    }

    public void addAvatarStateMachineDiagramPanel(String s) {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        AvatarSMDToolBar toolBarActivity = new AvatarSMDToolBar(mgui);
        toolbars.add(toolBarActivity);

        AvatarSMDPanel asmdp = new AvatarSMDPanel(mgui, toolBarActivity);
        asmdp.tp = this;
        asmdp.setName(s);
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(asmdp);
        asmdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(MainGUI.INCREMENT);
        toolBarPanel.add(toolBarActivity, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        panels.add(asmdp);
        tabbedPane.addTab(s, IconManager.imgic63, toolBarPanel, "Opens the state machine of " + s);
        //tabbedPane.setMnemonicAt(tabbedPane.getTabCount()-1, '^');
        return;
    }

    public void init() {

        //  Class Diagram toolbar
        AvatarBDToolBar toolBarAvatarBD = new AvatarBDToolBar(mgui);
        toolbars.add(toolBarAvatarBD);

        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        //Class diagram
        abdp = new AvatarBDPanel(mgui, toolBarAvatarBD);
        abdp.setName("Block Diagram");
        abdp.tp = this;
        tdp = abdp;
        panels.add(abdp); // Always first in list
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(abdp);
        abdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT);
        toolBarPanel.add(toolBarAvatarBD, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("Design", IconManager.imgic80, toolBarPanel, "Opens the Design");
        tabbedPane.setSelectedIndex(0);
        //tabbedPane.setMnemonicAt(tabbedPane.getTabCount()-1, '^');
        mgui.changeMade(abdp, TDiagramPanel.NEW_COMPONENT);

        //jsp.setVisible(true);

    }

    public LinkedList<AvatarBDLibraryFunction> getAllLibraryFunctions(String _name) {
        return abdp.getAllLibraryFunctionsForBlock (_name);
    }

    public LinkedList<TAttribute> getAllAttributes(String _name) {
        return abdp.getAllAttributesOfBlock(_name);
    }

    public LinkedList<AvatarMethod> getAllMethods(String _name) {
        return abdp.getAllMethodsOfBlock(_name);
    }

    public LinkedList<AvatarSignal> getAllSignals(String _name) {
        return abdp.getAllSignalsOfBlock(_name);
    }

    public LinkedList<String> getAllTimers(String _name) {
        return abdp.getAllTimersOfBlock(_name);
    }
    
    public String saveHeaderInXml(String extensionToName) {
	if (extensionToName == null) {
	    return "<Modeling type=\"AVATAR Design\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
	}
	return "<Modeling type=\"AVATAR Design\" nameTab=\"" + mgui.getTabName(this) + extensionToName + "\" >\n";
    }

    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }

    public String toString() {
        return mgui.getTitleAt(this) + " (Design)";
    }

    public void resetMetElements() {
        //TraceManager.addDev("Reset met elements");
        TGComponent tgc;

        for(int i=0; i<panels.size(); i++) {
            Iterator<TGComponent> iterator = panels.get(i).getComponentList().listIterator();
            while(iterator.hasNext()) {
                tgc = iterator.next();
                tgc.setAVATARMet(0);
                tgc.setInternalAvatarMet(0);

            }
        }

    }



    public LinkedList<TGComponent> getListOfComponentsInMutex() {
        TGComponent tgc;
        TDiagramPanel tdp;

        LinkedList<TGComponent> list = new LinkedList<TGComponent>();

        for(int i=0; i<panels.size(); i++) {
            tdp = panels.get(i);
            if (tdp instanceof AvatarSMDPanel) {
                Iterator<TGComponent> iterator = panels.get(i).getComponentList().listIterator();
                while(iterator.hasNext()) {
                    tgc = iterator.next();
                    tgc.getAllCheckableInvariant(list);
                }
            }
        }

        return list;

    }

    public TGComponent hasCheckableMasterMutex() {
        TGComponent tgc, tgctmp;
        for(int i=0; i<panels.size(); i++) {
            tdp = panels.get(i);
            if (tdp instanceof AvatarSMDPanel) {
                Iterator<TGComponent> iterator = panels.get(i).getComponentList().listIterator();
                while(iterator.hasNext()) {
                    tgc = iterator.next();
                    tgctmp = tgc.hasCheckableMasterMutex();
                    if (tgctmp != null) {
                        //TraceManager.addDev("Found element with master mutex: " + tgctmp);
                        return tgctmp;
                    }
                }
            }
        }

        return null;
    }

    public void removeAllMutualExclusionWithMasterMutex() {
        TGComponent tgc;
        for(int i=0; i<panels.size(); i++) {
            tdp = panels.get(i);
            if (tdp instanceof AvatarSMDPanel) {
                Iterator<TGComponent> iterator = panels.get(i).getComponentList().listIterator();
                while(iterator.hasNext()) {
                    tgc = iterator.next();
                    tgc.removeAllMutualExclusionWithMasterMutex();
                }
            }
        }
    }

    public void reinitMutualExclusionStates() {
        TGComponent tgc;
        for(int i=0; i<panels.size(); i++) {
            tdp = panels.get(i);
            if (tdp instanceof AvatarSMDPanel) {
                Iterator<TGComponent> iterator = panels.get(i).getComponentList().listIterator();
                while(iterator.hasNext()) {
                    tgc = iterator.next();
                    if (tgc instanceof AvatarSMDState) {
                        ((AvatarSMDState)tgc).reinitMutualExclusionStates();
                    }
                }
            }
        }
    }

    public void clearGraphicalInfoOnInvariants() {

    }

    public LinkedList<String> getPropertyPragmas() {
        LinkedList<String> result = new LinkedList<String> ();
        for (Object tgc: abdp.getComponentList()) {
            if (tgc instanceof AvatarBDPragma) {
                result.addAll(((AvatarBDPragma) tgc).getProperties());
            }
        }

        return result;
    }

    public LinkedList<String> getModelPragmas() {
        LinkedList<String> result = new LinkedList<String> ();
        for (Object tgc: abdp.getComponentList()) {
            if (tgc instanceof AvatarBDPragma) {
                result.addAll(((AvatarBDPragma) tgc).getModels());
            }
        }

        return result;
    }


    public void resetModelBacktracingProVerif() {
        if (abdp == null) {
            return;
        }

        // Reset confidential attributes
        for(AvatarBDBlock block1: abdp.getFullBlockList()) {
            block1.resetConfidentialityOfAttributes();
        }
        for (Object tgc: abdp.getComponentList()){
            if (tgc instanceof AvatarBDPragma){
                AvatarBDPragma pragma = (AvatarBDPragma) tgc;
                pragma.authStrongMap.clear();
                pragma.authWeakMap.clear();

            }
        }
        // Reset reachable states
        for(int i=0; i<panels.size(); i++) {
            tdp = panels.get(i);
            if (tdp instanceof AvatarSMDPanel) {
                ((AvatarSMDPanel)tdp).resetStateSecurityInfo();
            }
        }
    }


    public void modelBacktracingUppaal( Map<String, Integer> verifMap){
        for (Object ob: abdp.getComponentList()) {
            if (ob instanceof AvatarBDSafetyPragma) {
                AvatarBDSafetyPragma pragma = (AvatarBDSafetyPragma) ob;
                pragma.verifMap = verifMap;
            }
        }
    }

	public void modelBacktracingLatency(Vector<SimulationLatency> latencies){
		//Search for Safety Pragma
		for (Object ob: abdp.getComponentList()) {
			if (ob instanceof AvatarBDPerformancePragma) {
				AvatarBDPerformancePragma bdpragma = (AvatarBDPerformancePragma) ob;
				//Match each safety pragma to latency result
				for (String s: bdpragma.getProperties()){
					for (SimulationLatency latency: latencies){
						for (AvatarPragmaLatency pragma : latency.getPragmas()){
							if (pragma.getPragmaString().equals(s)){
								//Check if the latency statement is true
								int refTime = pragma.getTime();
								float time = 0;
								//
								try {
									time = Float.valueOf(latency.getAverageTime());
								} catch (Exception e){
									continue;
								}				
								if (pragma.getSymbolType() == AvatarPragmaLatency.lessThan){
									if (time<refTime){
										bdpragma.verifMap.put(s, "PROVED_TRUE");
										//mark as true
									}
									else {
										bdpragma.verifMap.put(s, "PROVED_FALSE");
									}
								}
								else if (pragma.getSymbolType() == AvatarPragmaLatency.greaterThan) {
									if (time>refTime){
										bdpragma.verifMap.put(s, "PROVED_TRUE");
										//mark as true
									}
									else {
										bdpragma.verifMap.put(s, "PROVED_FALSE");
									}
								}
								else if (pragma.getSymbolType() == AvatarPragmaLatency.query) {
									//Draw average time on verif map
									bdpragma.verifMap.put(s,Float.toString(time));
								}	
							}
						}
					}
				}
			}
		}
	}


    public void modelBacktracingProVerif(ProVerifOutputAnalyzer pvoa) {

        if (abdp == null) {
            return;
        }

        resetModelBacktracingProVerif();

        // Confidential attributes
        Map<AvatarPragmaSecret, ProVerifQueryResult> confResults = pvoa.getConfidentialityResults();
        HashMap<AvatarAttribute, AvatarPragma> secretAttributes = new HashMap<AvatarAttribute, AvatarPragma> ();
        HashMap<AvatarAttribute, AvatarPragma> nonSecretAttributes = new HashMap<AvatarAttribute, AvatarPragma> ();
        for (AvatarPragmaSecret pragma: confResults.keySet())
        {
            ProVerifQueryResult result = confResults.get(pragma);
            if (result.isProved())
            {
                if (result.isSatisfied())
                    secretAttributes.put(pragma.getArg(), pragma);
                else
                    nonSecretAttributes.put(pragma.getArg(),pragma);
            }
        }

        for (AvatarBDBlock bdBlock: abdp.getFullBlockList ())
            for (TAttribute tattr: bdBlock.getAttributeList ()) {
                if (tattr.getType () == TAttribute.OTHER) {
                    LinkedList<TAttribute> types = abdp.getAttributesOfDataType (tattr.getTypeOther ());
                    int toBeFound = types.size ();
                    boolean ko = false;
                    for (TAttribute type: types) {
                        for(AvatarAttribute attribute: secretAttributes.keySet())
                            if (attribute.getBlock ().getName ().equals (bdBlock.getBlockName ()) && attribute.getName ().equals (tattr.getId () + "__" + type.getId ())) {
                                toBeFound --;
                                ProVerifResultTrace trace = confResults.get(secretAttributes.get(attribute)).getTrace();
								if (trace!=null){
									bdBlock.addProVerifTrace(tattr, trace);
								}
                                break;
                            }

                        for(AvatarAttribute attribute: nonSecretAttributes.keySet())
                            if (attribute.getBlock ().getName ().equals (bdBlock.getBlockName ()) && attribute.getName ().equals (tattr.getId () + "__" + type.getId ())) {
                                ko = true;
                                ProVerifResultTrace trace = confResults.get(nonSecretAttributes.get(attribute)).getTrace();
								if (trace!=null){
									bdBlock.addProVerifTrace(tattr, trace);
								}
                                break;
                            }

                        if (ko)
                            break;
                    }

                    if (ko){
                        tattr.setConfidentialityVerification(TAttribute.CONFIDENTIALITY_KO);
                      
                    }
                    else if (toBeFound == 0) {
                        tattr.setConfidentialityVerification(TAttribute.CONFIDENTIALITY_OK);
                        
                    }
                } else {
                    for(AvatarAttribute attribute: secretAttributes.keySet())
                        if (attribute.getBlock ().getName ().equals (bdBlock.getBlockName ()) && attribute.getName ().equals (tattr.getId ())){
                            tattr.setConfidentialityVerification(TAttribute.CONFIDENTIALITY_OK);
                            ProVerifResultTrace trace = confResults.get(secretAttributes.get(attribute)).getTrace();
							if (trace!=null){
								bdBlock.addProVerifTrace(tattr, trace);
							}
                        }
                    for(AvatarAttribute attribute: nonSecretAttributes.keySet())
                        if (attribute.getBlock ().getName ().equals (bdBlock.getBlockName ()) && attribute.getName ().equals (tattr.getId ())){
                            tattr.setConfidentialityVerification(TAttribute.CONFIDENTIALITY_KO);
                            ProVerifResultTrace trace = confResults.get(nonSecretAttributes.get(attribute)).getTrace();
							if (trace!=null){
								bdBlock.addProVerifTrace(tattr, trace);
							}
                        }
                }
            }


        // Reachable states
        Map<AvatarPragmaReachability, ProVerifQueryResult> reachResults = pvoa.getReachabilityResults();
        for (AvatarPragmaReachability pragma: reachResults.keySet())
        {
            ProVerifQueryResult result = reachResults.get(pragma);
            if (result.isProved())
            {
                for(int i=0; i<panels.size(); i++) {
                    tdp = panels.get(i);
                    if ((tdp instanceof AvatarSMDPanel) && (tdp.getName().compareTo(pragma.getBlock().getName()) == 0)) {
                        Iterator<TGComponent> iterator = panels.get(i).getComponentList().listIterator();
                        while(iterator.hasNext()) {
                            TGComponent tgc = iterator.next();
                            if (tgc instanceof AvatarSMDState) {
                                ((AvatarSMDState)tgc).setSecurityInfo(
                                    result.isSatisfied() ? AvatarSMDState.REACHABLE : AvatarSMDState.NOT_REACHABLE,
                                    pragma.getState().getName());
                            }
                        }

                        break;
                    }
                }
            }
        }

        Map<AvatarPragmaAuthenticity, ProVerifQueryAuthResult> authResults = pvoa.getAuthenticityResults();
        for (Object ob: abdp.getComponentList())
            if (ob instanceof AvatarBDPragma) {
                AvatarBDPragma pragma = (AvatarBDPragma) ob;
                for (String prop: pragma.getProperties()) {
                    String[] split = prop.trim ().split ("\\s+");
                    if (split.length != 3)
                        continue;
                    if (split[0].equals ("#Authenticity")) {
                        String[] argA = split[1].split("\\.");
                        String[] argB = split[2].split("\\.");

                        if (argA.length != 3 || argB.length != 3)
                            continue;

                        TAttribute tattrA = abdp.getAttributeByBlockName (argA[0], argA[2]);
                        TAttribute tattrB = abdp.getAttributeByBlockName (argB[0], argB[2]);

                        if (tattrA == null || tattrB == null)
                            continue;

                        if (tattrA.getType () != tattrB.getType ())
                            continue;

                        if (tattrA.getType () == TAttribute.OTHER) {
                            if (! tattrA.getTypeOther ().equals (tattrB.getTypeOther ()))
                                continue;

                            LinkedList<TAttribute> types = abdp.getAttributesOfDataType (tattrA.getTypeOther ());
                            int toBeFound = types.size ();
                            boolean ko = false;
                            boolean weakKo = false;
                            boolean isNotProved = false;
                            boolean weakIsNotProved = false;
							ProVerifQueryAuthResult result= new ProVerifQueryAuthResult(false, false);
                            for (TAttribute type: types) {
                                for (AvatarPragmaAuthenticity pragmaAuth: authResults.keySet())
                                {
                                    if (!pragmaAuth.getAttrA().getAttribute().getBlock().getName().equals(argA[0].replaceAll("\\.", "__"))
                                            || !pragmaAuth.getAttrB().getAttribute().getBlock().getName().equals(argB[0].replaceAll("\\.", "__"))
                                            || !pragmaAuth.getAttrA().getAttribute().getName().equals(argA[2] + "__" + type.getId())
                                            || !pragmaAuth.getAttrB().getAttribute().getName().equals(argB[2] + "__" + type.getId())
                                            || !pragmaAuth.getAttrA().getState().getName().equals(argA[1].replaceAll("\\.", "__"))
                                            || !pragmaAuth.getAttrB().getState().getName().equals(argB[1].replaceAll("\\.", "__")))
                                        continue;

                                    result = authResults.get(pragmaAuth);
                                    toBeFound --;

                                    if (result.isProved())
                                    {
                                        if (!result.isSatisfied())
                                        {
                                            ko = true;
                                        }
                                    }
                                    else
                                    {
                                        isNotProved = true;
                                    }

                                    if (result.isWeakProved())
                                    {
                                        if (!result.isWeakSatisfied())
                                        {
                                            weakKo = true;
                                        }
                                    }
                                    else
                                    {
                                        weakIsNotProved = true;
                                    }

                                    break;
                                }
                            }

                            if (ko)
                                pragma.authStrongMap.put(prop, 2);
                            else if (toBeFound == 0) {
                                if (isNotProved)
                                    pragma.authStrongMap.put(prop, 3);
                                else
                                    pragma.authStrongMap.put(prop, 1);
                            }

                            if (weakKo)
                                pragma.authWeakMap.put(prop, 2);
                            else if (toBeFound == 0) {
                                if (weakIsNotProved)
                                    pragma.authWeakMap.put(prop, 3);
                                else
                                    pragma.authWeakMap.put(prop, 1);
                            }
							ProVerifResultTrace trace = result.getTrace();
							if (trace!=null){
								pragma.pragmaTraceMap.put(prop, trace);
							}
                        } else {
                            for (AvatarPragmaAuthenticity pragmaAuth: authResults.keySet())
                            {
                                if (!pragmaAuth.getAttrA().getAttribute().getBlock().getName().equals(argA[0].replaceAll("\\.", "__"))
                                        || !pragmaAuth.getAttrB().getAttribute().getBlock().getName().equals(argB[0].replaceAll("\\.", "__"))
                                        || !pragmaAuth.getAttrA().getAttribute().getName().equals(argA[2])
                                        || !pragmaAuth.getAttrB().getAttribute().getName().equals(argB[2])
                                        || !pragmaAuth.getAttrA().getState().getName().equals(argA[1].replaceAll("\\.", "__"))
                                        || !pragmaAuth.getAttrB().getState().getName().equals(argB[1].replaceAll("\\.", "__")))
                                    continue;

                                ProVerifQueryAuthResult result = authResults.get(pragmaAuth);

                                if (result.isProved())
                                {
                                    if (result.isSatisfied())
                                    {
                                        pragma.authStrongMap.put(prop, 1);
                                    }
                                    else
                                    {
                                        pragma.authStrongMap.put(prop, 2);
                                    }
                                }
                                else
                                {
                                    pragma.authStrongMap.put(prop, 3);
                                }

                                if (result.isWeakProved())
                                {
                                    if (result.isWeakSatisfied())
                                    {
                                        pragma.authWeakMap.put(prop, 1);
                                    }
                                    else
                                    {
                                        pragma.authWeakMap.put(prop, 2);
                                    }
                                }
                                else
                                {
                                    pragma.authWeakMap.put(prop, 3);
                                }
								//Add ProVerif Result Trace to pragma
								ProVerifResultTrace trace = pvoa.getResults().get(pragmaAuth).getTrace();
								if (trace!=null){
									pragma.pragmaTraceMap.put(prop, trace);
								}
                                break;
                            }
                        }
                    }
                }
            }
    }

    public ArrayList<String> getAllNonMappedAvatarBlockNames(String _name, ADDDiagramPanel _tadp, boolean ref, String name) {
        return abdp.getAllNonMappedAvatarBlockNames(_name, _tadp, ref, name);
    }

    public ArrayList<String> getAllNonMappedAvatarChannelNames(String _name, ADDDiagramPanel _tadp, boolean ref, String name) {
        return abdp.getAllNonMappedAvatarChannelNames(_name, _tadp);
    }


}
