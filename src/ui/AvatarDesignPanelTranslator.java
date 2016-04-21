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
 * Class AvatarDesignPanelTranslator
 * Creation: 18/05/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.util.*;



import myutil.*;
import ui.avatarbd.*;
import ui.avatarsmd.*;

import avatartranslator.*;
//import translator.*;
import ui.window.*;


public class AvatarDesignPanelTranslator {

    protected AvatarDesignPanel adp;
    protected Vector checkingErrors, warnings;
    protected CorrespondanceTGElement listE; // usual list
    //protected CorrespondanceTGElement listB; // list for particular element -> first element of group of blocks
    protected LinkedList <TDiagramPanel> panels;
    protected HashMap<String, Vector> typeAttributesMap;    
    protected HashMap<String, String> nameTypeMap;
    public AvatarDesignPanelTranslator(AvatarDesignPanel _adp) {
        adp = _adp;
        reinit();
    }

    public void reinit() {
        checkingErrors = new Vector();
        warnings = new Vector();
        listE = new CorrespondanceTGElement();
        panels = new LinkedList <TDiagramPanel>();
    }

    public Vector getErrors() {
        return checkingErrors;
    }

    public Vector getWarnings() {
        return warnings;
    }

    public CorrespondanceTGElement getCorrespondanceTGElement() {
        return listE;
    }

    public AvatarSpecification generateAvatarSpecification(Vector<AvatarBDStateMachineOwner> _blocks) {
        LinkedList<AvatarBDBlock> blocks = new LinkedList<AvatarBDBlock>();
        LinkedList<AvatarBDLibraryFunction> libraryFunctions = new LinkedList<AvatarBDLibraryFunction>();

        for (AvatarBDStateMachineOwner owner: _blocks)
            if (owner instanceof AvatarBDBlock)
                blocks.add ((AvatarBDBlock) owner);
            else
                libraryFunctions.add ((AvatarBDLibraryFunction) owner);

        AvatarSpecification as = new AvatarSpecification("avatarspecification", adp);

        if (adp != null) {
            AvatarBDPanel abdp = adp.getAvatarBDPanel();
            if (abdp != null) {
                as.addApplicationCode(abdp.getMainCode());
            }
        }
        typeAttributesMap = new HashMap<String, Vector>();
        nameTypeMap = new HashMap<String,String>();
        createLibraryFunctions (as, libraryFunctions);
        createBlocks(as, blocks);
        createRelationsBetweenBlocks(as, blocks);
        makeBlockStateMachines(as);
        /*for (String s: nameTypeMap.keySet()){
          System.out.println(s + " "+ nameTypeMap.get(s));
          System.out.println(typeAttributesMap.get(nameTypeMap.get(s)).size());
          } */
        createPragmas(as, blocks);

        TraceManager.addDev("Removing else guards");
        as.removeElseGuards();
        TraceManager.addDev("Removing else guards ... done");
        //System.out.println(as.toString());
        return as;
    }

    public class ErrorAccumulator {
        private TGComponent tgc;
        private TDiagramPanel tdp;
        private AvatarBlock ab;

        public ErrorAccumulator (TGComponent tgc, TDiagramPanel tdp, AvatarBlock ab) {
            this.tgc = tgc;
            this.tdp = tdp;
            this.ab = ab;
        }

        public ErrorAccumulator (TGComponent tgc, TDiagramPanel tdp) {
            this (tgc, tdp, null);
        }

        public CheckingError createError (String msg) {
            CheckingError ce = new CheckingError (CheckingError.BEHAVIOR_ERROR, msg);
            ce.setAvatarBlock (this.ab);
            ce.setTGComponent (this.tgc);
            ce.setTDiagramPanel (this.tdp);

            return ce;
        }

        public void addWarning (CheckingError ce) {
            AvatarDesignPanelTranslator.this.addWarning (ce);
        }

        public void addWarning (String msg) {
            this.addWarning (this.createError (msg));
        }

        public void addError (CheckingError ce) {
            AvatarDesignPanelTranslator.this.addCheckingError (ce);
        }

        public void addError (String msg) {
            this.addError (this.createError (msg));
        }
    }

    public void createPragmas(AvatarSpecification _as, LinkedList<AvatarBDBlock> _blocks) {
        Iterator iterator = adp.getAvatarBDPanel().getComponentList().listIterator();
        TGComponent tgc;
        AvatarBDPragma tgcn;
        AvatarBDSafetyPragma tgsp;
        String values [];
        String tmp;
        LinkedList<AvatarPragma> pragmaList;
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof AvatarBDPragma) {
                ErrorAccumulator errorAcc = new ErrorAccumulator (tgc, adp.getAvatarBDPanel());
                tgcn = (AvatarBDPragma)tgc;
                values = tgcn.getValues();
                for(int i=0; i<values.length; i++) {
                    tmp = values[i].trim();
                    if ((tmp.startsWith("#") && (tmp.length() > 1))) {
                        tmp = tmp.substring(1, tmp.length()).trim();

                        //TraceManager.addDev("Reworking pragma =" + tmp);
                        pragmaList = AvatarPragma.createFromString(tmp, tgc, _as.getListOfBlocks(), typeAttributesMap, nameTypeMap, errorAcc);

                        //TraceManager.addDev("Reworked pragma =" + tmp);

                        for (AvatarPragma tmpPragma: pragmaList){
                            if (tmpPragma instanceof AvatarPragmaConstant){
                                AvatarPragmaConstant apg = (AvatarPragmaConstant) tmpPragma;
                                for (AvatarConstant ac: apg.getConstants()){
                                    _as.addConstant(ac);
                                }
                            }
                            _as.addPragma(tmpPragma);
                            //TraceManager.addDev("Adding pragma:" + tmp);
                        }
                    }
                }
            }
            if (tgc instanceof AvatarBDSafetyPragma) {
                tgsp = (AvatarBDSafetyPragma)tgc;
                values = tgsp.getValues();
                for (String s: values){
                    _as.addSafetyPragma(s);
                }
            }
        }
    }

    public String reworkPragma(String _pragma, LinkedList<AvatarBDBlock> _blocks) {
        String ret = "";
        int i;

        // Identify first keyword
        _pragma = _pragma.trim();

        int index = _pragma.indexOf(" ");

        if (index == -1) {
            return null;
        }

        String header = _pragma.substring(0, index).trim();

        for(i=0; i<AvatarPragma.PRAGMAS.length; i++) {
            if (header.compareTo(AvatarPragma.PRAGMAS[i]) == 0) {
                break;
            }
        }

        // Invalid header?
        if (i == AvatarPragma.PRAGMAS.length) {
            TraceManager.addDev("Invalid Pragma " + 0);
            return null;
        }



        ret = AvatarPragma.PRAGMAS_TRANSLATION[i] + " ";

        // Checking for arguments


        boolean b = ret.startsWith("Authenticity ");
        boolean b1 = ret.startsWith("PrivatePublicKeys ");
        String arguments [] = _pragma.substring(index+1, _pragma.length()).trim().split(" ");
        String tmp;
        String blockName, stateName, paramName = "";
        boolean found = false;
        Vector types;
        AvatarBDBlock block;
        TAttribute ta;
        AvatarBlock ab;
        String myBlockName = "";




        for(i=0; i<arguments.length; i++) {
            tmp = arguments[i];
            TraceManager.addDev("arguments #=" + arguments.length + " pragma=" + _pragma + " tmp=" + tmp);

            if (b1) {

                // Private Public keys?
                if (i == 0) {
                    // Must be a block name
                    // Look for at least a block
                    found = false;
                    for(Object o: _blocks) {
                        block = (AvatarBDBlock)o;
                        //TraceManager.addDev("Comparing " + block.getBlockName() + " with " + tmp);
                        if (block.getBlockName().compareTo(tmp) ==0) {
                            myBlockName = block.getBlockName();
                            found = true;
                            ret = ret + tmp;
                            break;
                        }
                        /*for(Object oo: block.getAttributeList()) {
                          ta = (TAttribute)oo;
                          if (ta.getId().compareTo(tmp) == 0) {
                          found = true;

                          if ((ta.getType() == TAttribute.NATURAL) || (ta.getType() == TAttribute.INTEGER) || (ta.getType() == TAttribute.BOOLEAN)) {
                          ret = ret  + tmp + " ";
                          } else if (ta.getType() == TAttribute.OTHER) {
                        // Must find all subsequent types
                        types = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
                        if (types == null) {
                        TraceManager.addDev("Invalid Pragma " + 1);
                        return null;

                        } else {
                        for(int j=0; j<types.size(); j++) {
                        ret = ret + tmp + "__" + ((TAttribute)(types.elementAt(j))).getId() + " ";
                        }
                        }
                          }

                          }
                          }*/
                    }
                    if (found == false) {
                        TraceManager.addDev("Invalid Pragma " + 2);
                        return null;
                    }

                } else if ((i == 1) || (i == 2)) {
                    // Shall be an attribute
                    TraceManager.addDev("i= " + i);
                    for(Object o: _blocks) {
                        block = (AvatarBDBlock)o;
                        TraceManager.addDev("block= " + block.getBlockName() + " my block name=" + myBlockName);
                        if (block.getBlockName().compareTo(myBlockName) == 0) {
                            TraceManager.addDev("Found the block " + ret);
                            for(Object oo: block.getAttributeList()) {
                                ta = (TAttribute)oo;
                                TraceManager.addDev("Attribute: " + ta.getId());
                                if (ta.getId().compareTo(tmp) == 0) {
                                    paramName = ta.getId();
                                    found = true;
                                    TraceManager.addDev("Pragma " + ret + " found=" + found);
                                    if ((ta.getType() == TAttribute.NATURAL) || (ta.getType() == TAttribute.INTEGER) || (ta.getType() == TAttribute.BOOLEAN)) {
                                        ret = ret  + " " + paramName + " ";
                                    } else if (ta.getType() == TAttribute.OTHER) {
                                        // Must find all subsequent types
                                        types = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
                                        if (types == null) {
                                            TraceManager.addDev("Invalid Pragma " + 3);
                                            return null;
                                        } else {
                                            TraceManager.addDev("Pragma " + ret + " types size=" + types.size());
                                            for(int j=0; j<types.size(); j++) {
                                                ret = ret  + " " + paramName + "__" + ((TAttribute)(types.elementAt(j))).getId() + " ";
                                            }
                                        }

                                    } else {
                                        TraceManager.addDev("Invalid Pragma " + 4);
                                        return null;
                                    }

                                    break;
                                }
                            }
                        }
                    }
                } else {
                    // Badly formatted
                    TraceManager.addDev("Invalid Pragma " + 5);
                    return null;
                }

                // Other than PrivatePublicKeys
            } else {
                index = tmp.indexOf(".");
                if (index == -1) {
                    return null;
                }
                blockName = tmp.substring(0, index);

                //TraceManager.addDev("blockName=" + blockName);
                // Search for the block
                for(Object o: _blocks) {
                    block = (AvatarBDBlock)o;
                    if (block.getBlockName().compareTo(blockName) == 0) {

                        if (b) {
                            // Authenticity
                            stateName = tmp.substring(index+1, tmp.length());
                            //TraceManager.addDev("stateName=" + stateName);
                            index = stateName.indexOf(".");
                            if (index == -1) {
                                return null;
                            }
                            paramName = stateName.substring(index+1, stateName.length());
                            stateName = stateName.substring(0, index);

                            for(Object oo: block.getAttributeList()) {
                                ta = (TAttribute)oo;
                                if (ta.getId().compareTo(paramName) == 0) {
                                    found = true;

                                    if ((ta.getType() == TAttribute.NATURAL) || (ta.getType() == TAttribute.INTEGER) || (ta.getType() == TAttribute.BOOLEAN)) {
                                        ret = ret + blockName + "." + stateName + "." + paramName + " ";
                                    } else if (ta.getType() == TAttribute.OTHER) {
                                        // Must find all subsequent types
                                        types = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
                                        if (types == null) {
                                            return null;
                                        } else {
                                            for(int j=0; j<types.size(); j++) {
                                                ret = ret + blockName + "." + stateName + "." + paramName + "__" + ((TAttribute)(types.elementAt(j))).getId() + " ";
                                            }
                                        }

                                    } else {
                                        return null;
                                    }

                                    break;
                                }
                            }

                        } else {
                            // Other: confidentiality, initial system knowledge, initial session knowledge, constant

                            paramName = tmp.substring(index+1, tmp.length());
                            for(Object oo: block.getAttributeList()) {
                                ta = (TAttribute)oo;
                                if (ta.getId().compareTo(paramName) == 0) {
                                    found = true;

                                    if ((ta.getType() == TAttribute.NATURAL) || (ta.getType() == TAttribute.INTEGER) || (ta.getType() == TAttribute.BOOLEAN)) {
                                        ret = ret + blockName + "." + paramName + " ";
                                    } else if (ta.getType() == TAttribute.OTHER) {
                                        // Must find all subsequent types
                                        types = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
                                        if (types == null) {
                                            return null;
                                        } else {
                                            for(int j=0; j<types.size(); j++) {
                                                ret = ret + blockName + "." + paramName + "__" + ((TAttribute)(types.elementAt(j))).getId() + " ";
                                            }
                                        }

                                    } else {
                                        return null;
                                    }

                                    break;
                                }
                            }

                        }
                    }
                }
            }

            if (!found) {
                return null;
            }

        }

        TraceManager.addDev("Reworked pragma: " + ret);

        return ret.trim();
    }

    private AvatarAttribute createRegularAttribute (AvatarStateMachineOwner _ab, TAttribute _a, String _preName) {
        AvatarType type = AvatarType.UNDEFINED;
        if (_a.getType() == TAttribute.INTEGER){
            type = AvatarType.INTEGER;
        } else if (_a.getType() == TAttribute.NATURAL){
            type = AvatarType.INTEGER;
        } else if (_a.getType() == TAttribute.BOOLEAN) {
            type = AvatarType.BOOLEAN;
        } else if (_a.getType() == TAttribute.TIMER) {
            type = AvatarType.TIMER;
        }
        AvatarAttribute aa = new AvatarAttribute(_preName + _a.getId(), type, _ab, _a);
        aa.setInitialValue(_a.getInitialValue());

        return aa;
    }

    public void addRegularAttribute(AvatarBlock _ab, TAttribute _a, String _preName) {
        _ab.addAttribute(this.createRegularAttribute (_ab, _a, _preName));
    }

    public void createLibraryFunctions (AvatarSpecification _as, LinkedList<AvatarBDLibraryFunction> _libraryFunctions) {
        for (AvatarBDLibraryFunction libraryFunction: _libraryFunctions) {
            AvatarLibraryFunction alf = new AvatarLibraryFunction (libraryFunction.getFunctionName (), _as, libraryFunction);
            _as.addLibraryFunction (alf);
            listE.addCor(alf, libraryFunction);
            libraryFunction.setAVATARID(alf.getID());

            // Create parameters
            for (TAttribute attr: libraryFunction.getParameters ())
                if (attr.getType() == TAttribute.INTEGER
                        || attr.getType() == TAttribute.NATURAL
                        || attr.getType() == TAttribute.BOOLEAN
                        || attr.getType() == TAttribute.TIMER)
                    alf.addParameter (this.createRegularAttribute (alf, attr, ""));
                else {
                    // other
                    Vector<TAttribute> types = adp.getAvatarBDPanel ().getAttributesOfDataType (attr.getTypeOther ());
                    if (types == null) {
                        CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + attr.getTypeOther() + " used in " + alf.getName());
                        // TODO: adapt
                        // ce.setAvatarBlock(ab);
                        ce.setTDiagramPanel(adp.getAvatarBDPanel());
                        addCheckingError(ce);
                        return;
                    } else {
                        if (types.isEmpty ()) {
                            CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Data type definition must contain at least one attribute:  " + alf.getName());
                            ce.setTDiagramPanel(adp.getAvatarBDPanel());
                            addCheckingError(ce);
                        } else {
                            nameTypeMap.put (libraryFunction.getFunctionName()+"."+attr.getId(), attr.getTypeOther());
                            typeAttributesMap.put (attr.getTypeOther(), types);
                            for (TAttribute type: types)
                                alf.addParameter (this.createRegularAttribute (alf, type, attr.getId() + "__"));
                        }
                    }
                }

            // Create return values
            for (TAttribute attr: libraryFunction.getReturnAttributes ())
                if (attr.getType() == TAttribute.INTEGER
                        || attr.getType() == TAttribute.NATURAL
                        || attr.getType() == TAttribute.BOOLEAN
                        || attr.getType() == TAttribute.TIMER)
                    alf.addReturnAttribute (this.createRegularAttribute (alf, attr, ""));
                else {
                    // other
                    Vector<TAttribute> types = adp.getAvatarBDPanel ().getAttributesOfDataType (attr.getTypeOther ());
                    if (types == null) {
                        CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + attr.getTypeOther() + " used in " + alf.getName());
                        ce.setTDiagramPanel(adp.getAvatarBDPanel());
                        addCheckingError(ce);
                        return;
                    } else {
                        if (types.isEmpty ()) {
                            CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Data type definition must contain at least one attribute:  " + alf.getName());
                            ce.setTDiagramPanel(adp.getAvatarBDPanel());
                            addCheckingError(ce);
                        } else {
                            nameTypeMap.put (libraryFunction.getFunctionName()+"."+attr.getId(), attr.getTypeOther());
                            typeAttributesMap.put (attr.getTypeOther(), types);
                            for (TAttribute type: types)
                                alf.addReturnAttribute (this.createRegularAttribute (alf, type, attr.getId() + "__"));
                        }
                    }
                }

            // Create attributes
            for (TAttribute attr: libraryFunction.getAttributes ())
                if (attr.getType() == TAttribute.INTEGER
                        || attr.getType() == TAttribute.NATURAL
                        || attr.getType() == TAttribute.BOOLEAN
                        || attr.getType() == TAttribute.TIMER)
                    alf.addReturnAttribute (this.createRegularAttribute (alf, attr, ""));
                else {
                    // other
                    Vector<TAttribute> types = adp.getAvatarBDPanel ().getAttributesOfDataType (attr.getTypeOther ());
                    if (types == null) {
                        CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + attr.getTypeOther() + " used in " + alf.getName());
                        ce.setTDiagramPanel(adp.getAvatarBDPanel());
                        addCheckingError(ce);
                        return;
                    } else {
                        if (types.isEmpty ()) {
                            CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Data type definition must contain at least one attribute:  " + alf.getName());
                            ce.setTDiagramPanel(adp.getAvatarBDPanel());
                            addCheckingError(ce);
                        } else {
                            nameTypeMap.put (libraryFunction.getFunctionName()+"."+attr.getId(), attr.getTypeOther());
                            typeAttributesMap.put (attr.getTypeOther(), types);
                            for (TAttribute type: types)
                                alf.addAttribute (this.createRegularAttribute (alf, type, attr.getId() + "__"));
                        }
                    }
                }

                // Create methods
                for (ui.AvatarMethod uiam: libraryFunction.getMethods ()) {
                    avatartranslator.AvatarMethod atam = new avatartranslator.AvatarMethod (uiam.getId (), uiam);
                    atam.setImplementationProvided (uiam.isImplementationProvided());
                    alf.addMethod (atam);
                    this.makeParameters (alf, atam, uiam);
                    this.makeReturnParameters (alf, libraryFunction, atam, uiam);
                }

                // Create signals
                for (ui.AvatarSignal uias: libraryFunction.getSignals ()) {
                    avatartranslator.AvatarSignal atas;
                    if (uias.getInOut() == uias.IN)
                        atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.IN, uias);
                    else
                        atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.OUT, uias);

                    alf.addSignal (atas);
                    this.makeParameters (alf, atas, uias);
                }
        }
    }

    public void createBlocks(AvatarSpecification _as, LinkedList<AvatarBDBlock> _blocks) {
        AvatarBlock ab;
        Vector v;
        TAttribute a;
        int i;
        AvatarAttribute aa;
        ui.AvatarMethod uiam;
        ui.AvatarSignal uias;
        avatartranslator.AvatarMethod atam;
        avatartranslator.AvatarSignal atas;
        TGComponent tgc1, tgc2;
        Vector types;

        for(AvatarBDBlock block: _blocks) {
            ab = new AvatarBlock(block.getBlockName(), _as, block);
            _as.addBlock(ab);
            listE.addCor(ab, block);
            block.setAVATARID(ab.getID());

            // Create attributes
            v = block.getAttributeList();
            for(i=0; i<v.size(); i++) {
                a = (TAttribute)(v.elementAt(i));
                if (a.getType() == TAttribute.INTEGER){
                    addRegularAttribute(ab, a, "");
                } else if (a.getType() == TAttribute.NATURAL){
                    addRegularAttribute(ab, a, "");
                } else if (a.getType() == TAttribute.BOOLEAN) {
                    addRegularAttribute(ab, a, "");
                } else if (a.getType() == TAttribute.TIMER) {
                    addRegularAttribute(ab, a, "");
                } else {
                    // other
                    // TraceManager.addDev(" -> Other type found: " + a.getTypeOther());
                    types = adp.getAvatarBDPanel().getAttributesOfDataType(a.getTypeOther());
                    if (types == null) {
                        CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + a.getTypeOther() + " used in " + ab.getName());
                        ce.setAvatarBlock(ab);
                        ce.setTDiagramPanel(adp.getAvatarBDPanel());
                        addCheckingError(ce);
                        return;
                    } else {
                        if (types.size() ==0) {
                            CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Data type definition must contain at least one attribute:  " + ab.getName());
                            ce.setAvatarBlock(ab);
                            ce.setTDiagramPanel(adp.getAvatarBDPanel());
                            addCheckingError(ce);
                        } else {
                            nameTypeMap.put(block.getBlockName()+"."+a.getId(), a.getTypeOther());
                            typeAttributesMap.put(a.getTypeOther(), types);
                            for(int j=0; j<types.size(); j++) {
                                addRegularAttribute(ab, (TAttribute)(types.elementAt(j)), a.getId() + "__");
                            }
                        }
                    }

                }
            }

            // Create methods
            v = block.getMethodList();
            for(i=0; i<v.size(); i++) {
                uiam = (AvatarMethod)(v.get(i));
                atam = new avatartranslator.AvatarMethod(uiam.getId(), uiam);
                atam.setImplementationProvided(uiam.isImplementationProvided());
                ab.addMethod(atam);
                makeParameters(ab, atam, uiam);
                makeReturnParameters(ab, block, atam, uiam);
            }
            // Create signals
            v = block.getSignalList();
            for(i=0; i<v.size(); i++) {
                uias = (AvatarSignal)(v.get(i));

                if (uias.getInOut() == uias.IN) {
                    atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.IN, uias);
                } else {
                    atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.OUT, uias);
                }
                ab.addSignal(atas);
                makeParameters(ab, atas, uias);
            }

            // Put global code
            ab.addGlobalCode(block.getGlobalCode());

        }

        // Make block hierarchy
        for(AvatarBlock block: _as.getListOfBlocks()) {
            tgc1 = listE.getTG(block);
            if ((tgc1 != null) && (tgc1.getFather() != null)) {
                tgc2 = tgc1.getFather();
                ab = listE.getAvatarBlock(tgc2);
                if (ab != null) {
                    block.setFather(ab);
                }
            }
        }
    }

    public void makeBlockStateMachines(AvatarSpecification _as) {
        // Make state machine of blocks
        for(AvatarBlock block: _as.getListOfBlocks())
            this.makeStateMachine(_as, block);

        // Make state machine of library functions
        for (AvatarLibraryFunction libraryFunction: _as.getListOfLibraryFunctions ())
            this.makeStateMachine (_as, libraryFunction);
    }

    public void makeReturnParameters(AvatarStateMachineOwner _ab, AvatarBDStateMachineOwner _block, avatartranslator.AvatarMethod _atam, ui.AvatarMethod _uiam) {
        String rt = _uiam.getReturnType().trim();
        AvatarAttribute aa;
        Vector types;
        TAttribute ta;
        AvatarType type = AvatarType.UNDEFINED;

        if (rt.length() == 0) {
            return;
        }

        if ((rt.compareTo("int") == 0) || (rt.compareTo("bool") == 0)) {
            aa = new AvatarAttribute("return__0", AvatarType.getType(rt), _ab, _block);
            _atam.addReturnParameter(aa);
        } else {
            types = adp.getAvatarBDPanel().getAttributesOfDataType(rt);
            if (types == null) {
                CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + rt + " declared as a return parameter of a method of " + _block.getOwnerName());
                // TODO: adapt
                // ce.setAvatarBlock(_ab);
                ce.setTDiagramPanel(adp.getAvatarBDPanel());
                addCheckingError(ce);
                return;
            } else {
                for(int j=0; j<types.size(); j++) {
                    ta = (TAttribute)(types.elementAt(j));
                    if (ta.getType() == TAttribute.INTEGER)
                        type = AvatarType.INTEGER;
                    else if (ta.getType() == TAttribute.NATURAL)
                        type = AvatarType.INTEGER;
                    else if (ta.getType() == TAttribute.BOOLEAN)
                        type = AvatarType.BOOLEAN;
                    else
                        type = AvatarType.INTEGER;
                    aa = new AvatarAttribute("return__" + j, type, _ab, _block);
                    _atam.addReturnParameter(aa);
                }
            }
        }

    }

    public void makeParameters(AvatarStateMachineOwner _block, avatartranslator.AvatarMethod _atam, ui.AvatarMethod _uiam) {
        String typeIds[] = _uiam.getTypeIds();
        String types[] = _uiam.getTypes();
        AvatarAttribute aa;
        TAttribute ta;
        Vector v;
        AvatarType type = AvatarType.UNDEFINED;

        for(int i=0; i<types.length; i++) {
            v = adp.getAvatarBDPanel().getAttributesOfDataType(types[i]);
            if (v == null) {
                if (AvatarType.getType(types[i]) == AvatarType.UNDEFINED) {
                    CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  \"" + types[i] + "\" declared in method " + _atam + " of block " + _block.getName());
                    // TODO: adapt
                    // ce.setAvatarBlock(_block);
                    ce.setTDiagramPanel(adp.getAvatarBDPanel());
                    addCheckingError(ce);
                }
                aa = new AvatarAttribute(typeIds[i], AvatarType.getType(types[i]), _block, _uiam);
                _atam.addParameter(aa);
            } else {
                for(int j=0; j<v.size(); j++) {
                    ta = (TAttribute)(v.get(j));
                    if (ta.getType() == TAttribute.INTEGER){
                        type = AvatarType.INTEGER;
                    } else if (ta.getType() == TAttribute.NATURAL){
                        type = AvatarType.INTEGER;
                    } else if (ta.getType() == TAttribute.BOOLEAN) {
                        type = AvatarType.BOOLEAN;
                    } else if (ta.getType() == TAttribute.TIMER) {
                        type = AvatarType.TIMER;
                    }
                    aa = new AvatarAttribute(typeIds[i] + "__" + ta.getId(), type, _block, _uiam);
                    _atam.addParameter(aa);
                }
            }
        }
    }

    public void manageAttribute (String _name, AvatarStateMachineOwner _ab, AvatarActionOnSignal _aaos, TDiagramPanel _tdp, TGComponent _tgc, String _idOperator) {
        TAttribute ta =  adp.getAvatarBDPanel().getAttribute(_name, _ab.getName());
        if (ta == null) {
            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + _name + " in signal expression: " + _idOperator);
            // TODO: adapt
            // ce.setAvatarBlock(_ab);
            ce.setTDiagramPanel(_tdp);
            ce.setTGComponent(_tgc);
            addCheckingError(ce);
            TraceManager.addDev("not found");
            return ;
        }

        //TraceManager.addDev("Found: " + ta.getId());

        AvatarAttribute aa;
        Vector v = new Vector();
        int i;
        TAttribute tatmp;

        if (ta.getType() == TAttribute.OTHER) {
            Vector v0 = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
            for(i=0; i<v0.size(); i++) {
                tatmp = (TAttribute)(v0.get(i));
                v.add(_name + "__" + tatmp.getId());
            }
        } else {
            v.add(_name);
        }

        //TraceManager.addDev("Size of vector:" + v.size());
        for(i=0; i<v.size(); i++) {
            aa = _ab.getAvatarAttributeWithName((String)(v.get(i)));
            if (aa == null) {
                CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + _name + " in signal expression: " + _idOperator);
                // TODO: adapt
                // ce.setAvatarBlock(_ab);
                ce.setTDiagramPanel(_tdp);
                ce.setTGComponent(_tgc);
                addCheckingError(ce);
                return ;
            } else {
                //TraceManager.addDev("-> Adding attr in action on signal in block " + _ab.getName() + ":" + _name + "__" + tatmp.getId());
                _aaos.addValue((String)(v.get(i)));
            }
        }
    }

    private void translateAvatarSMDSendSignal (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDSendSignal asmdss) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        avatartranslator.AvatarSignal atas = _ab.getAvatarSignalWithName (asmdss.getSignalName ());
        if (atas == null)
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Unknown signal: " + asmdss.getSignalName());

        // Get relation of that signal
        if (_ab instanceof AvatarBlock) {
            // Note that for library functions, signals are just placeholders so they don't need to be connected to anything
            AvatarRelation ar = _as.getAvatarRelationWithSignal (atas);
            if (ar == null)
                throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Signal used for sending in " + asmdss.getValue() + " is not connected to a channel");
        }

        AvatarActionOnSignal aaos = new AvatarActionOnSignal ("action_on_signal", atas, asmdss);
        if (asmdss.hasCheckableAccessibility())
            aaos.setCheckable();

        if (aaos.isReceiving ())
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "A receiving signal is used for sending: " + asmdss.getValue());

        if (asmdss.getNbOfValues() == -1)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal: " + asmdss.getValue());

        for(int i=0; i<asmdss.getNbOfValues(); i++) {
            String tmp = asmdss.getValue(i);
            if (tmp.isEmpty ())
                throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Empty parameter in signal expression: " + asmdss.getValue());

            this.manageAttribute (tmp, _ab, aaos, tdp, asmdss, asmdss.getValue());
        }

        if (aaos.getNbOfValues () != atas.getListOfAttributes ().size ())
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Badly formed signal sending: " + asmdss.getValue() + " -> nb of parameters does not match definition");

        // Checking expressions passed as parameter
        for (int i=0; i<aaos.getNbOfValues(); i++) {
            String theVal = aaos.getValue(i);
            if (atas.getListOfAttributes ().get (i).isInt ()) {
                if (AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, theVal) < 0)
                    throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdss.getValue() + " -> value at index #" + i + " does not match definition");
            } else {
                // We assume it is a bool attribute
                if (AvatarSyntaxChecker.isAValidBoolExpr(_as, _ab, theVal) < 0)
                    throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdss.getValue() + " -> value at index #" + i + " does not match definition");
            }
        }

        this.listE.addCor (aaos, asmdss);
        asmdss.setAVATARID (aaos.getID());
        asm.addElement (aaos);
    }


    private void translateAvatarSMDReceiveSignal (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDReceiveSignal asmdrs) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        avatartranslator.AvatarSignal atas = _ab.getAvatarSignalWithName (asmdrs.getSignalName ());
        if (atas == null)
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Unknown signal: " + asmdrs.getSignalName());

        // Get relation of that signal
        if (_ab instanceof AvatarBlock) {
            // Note that for library functions, signals are just placeholders so they don't need to be connected to anything
            AvatarRelation ar = _as.getAvatarRelationWithSignal (atas);
            if (ar == null)
                throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Signal used for receiving in " + asmdrs.getValue() + " is not connected to a channel");
        }

        AvatarActionOnSignal aaos = new AvatarActionOnSignal ("action_on_signal", atas, asmdrs);
        if (asmdrs.hasCheckableAccessibility())
            aaos.setCheckable();

        if (aaos.isSending())
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "A sending signal is used for receiving: " + asmdrs.getValue());

        if (asmdrs.getNbOfValues() == -1)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal: " + asmdrs.getValue());

        for(int i=0; i<asmdrs.getNbOfValues(); i++) {
            String tmp = asmdrs.getValue(i);
            if (tmp.isEmpty ())
                throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Empty parameter in signal expression: " + asmdrs.getValue());

            this.manageAttribute (tmp, _ab, aaos, tdp, asmdrs, asmdrs.getValue());
        }

        if (aaos.getNbOfValues () != atas.getListOfAttributes ().size ())
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdrs.getValue() + " -> nb of parameters does not match definition");

        // Checking expressions passed as parameter
        for (int i=0; i<aaos.getNbOfValues(); i++) {
            String theVal = aaos.getValue(i);
            if (atas.getListOfAttributes ().get (i).isInt ()) {
                if (AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, theVal) < 0)
                    throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdrs.getValue() + " -> value at index #" + i + " does not match definition");
            } else {
                // We assume it is a bool attribute
                if (AvatarSyntaxChecker.isAValidBoolExpr(_as, _ab, theVal) < 0)
                    throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdrs.getValue() + " -> value at index #" + i + " does not match definition");
            }
        }

        this.listE.addCor (aaos, asmdrs);
        asmdrs.setAVATARID (aaos.getID());
        asm.addElement (aaos);
    }

    private void translateAvatarSMDState (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDState tgc) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        AvatarState astate = asm.getStateWithName(tgc.getValue());
        if (astate == null) {
            astate = new AvatarState (tgc.getValue(), tgc);
            asm.addElement (astate);
        }

        if (tgc.hasCheckableAccessibility ())
            astate.setCheckable ();

        // Executable code
        astate.addEntryCode(((AvatarSMDState)(tgc)).getEntryCode());

        this.listE.addCor (astate, tgc);
        astate.addReferenceObject (tgc);
        tgc.setAVATARID (astate.getID());
    }

    private void translateAvatarSMDRandom (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDRandom asmdrand) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        AvatarRandom arandom = new AvatarRandom ("random", asmdrand);
        String tmp1 = modifyString (asmdrand.getMinValue());
        int error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp1);
        if (error < 0)
            this.makeError (error, tdp, _ab, asmdrand, "min value of random", tmp1);

        String tmp2 = modifyString(asmdrand.getMaxValue());
        error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp2);
        if (error < 0)
            this.makeError (error, tdp, _ab, asmdrand, "max value of random", tmp2);

        arandom.setValues (tmp1, tmp2);
        arandom.setFunctionId (asmdrand.getFunctionId());

        tmp1 = modifyString(asmdrand.getVariable());
        AvatarAttribute aa = _ab.getAvatarAttributeWithName (tmp1);

        if (aa == null)
            this.makeError (-3, tdp, _ab, asmdrand, "random", tmp1);
        // Checking type of variable -> must be an int
        else if (!(aa.isInt()))
            this.makeError (error, tdp, _ab, asmdrand, ": variable of random must be of type \"int\"", tmp2);

        arandom.setVariable (tmp1);

        asm.addElement (arandom);
        listE.addCor (arandom, asmdrand);
        asmdrand.setAVATARID (arandom.getID());
    }

    private void translateAvatarSMDSetTimer (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDSetTimer asmdst) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        String tmp = asmdst.getTimerName();
        AvatarAttribute aa = _ab.getAvatarAttributeWithName(tmp);
        if (aa == null)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed timer parameter: " + tmp + " in timer setting");

        if (aa.getType() != AvatarType.TIMER)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in timer setting: shall be a parameter of type \"Timer\"");

        tmp = this.modifyString (asmdst.getTimerValue ());
        int error = AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, tmp);
        if (error < 0)
            this.makeError(error, tdp, _ab, asmdst, "value of the timer setting", tmp);

        AvatarSetTimer asettimer = new AvatarSetTimer("settimer__" + aa.getName(), asmdst);
        asettimer.setTimer (aa);
        asettimer.setTimerValue (tmp);
        asm.addElement (asettimer);
        this.listE.addCor (asettimer, asmdst);
        asmdst.setAVATARID (asettimer.getID());
    }

    private void translateAvatarSMDResetTimer (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDResetTimer asmdrt) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        String tmp = asmdrt.getTimerName();
        AvatarAttribute aa = _ab.getAvatarAttributeWithName (tmp);
        if (aa == null)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed timer parameter: " + tmp + " in timer reset");

        if (aa.getType() != AvatarType.TIMER)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in timer reset: shall be a parameter of type \"Timer\"");

        AvatarResetTimer aresettimer = new AvatarResetTimer("resettimer__" + aa.getName(), asmdrt);
        aresettimer.setTimer (aa);
        asm.addElement(aresettimer);
        this.listE.addCor (aresettimer, asmdrt);
        asmdrt.setAVATARID (aresettimer.getID());
    }

    private void translateAvatarSMDExpireTimer (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDExpireTimer asmdet) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        String tmp = asmdet.getTimerName();
        AvatarAttribute aa = _ab.getAvatarAttributeWithName (tmp);
        if (aa == null)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed timer parameter: " + tmp + " in timer expiration");

        if (aa.getType() != AvatarType.TIMER)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in timer expiration: shall be a parameter of type \"Timer\"");

        AvatarExpireTimer aexpiretimer = new AvatarExpireTimer("expiretimer__" + aa.getName(), asmdet);
        aexpiretimer.setTimer(aa);
        asm.addElement(aexpiretimer);
        this.listE.addCor(aexpiretimer, asmdet);
        asmdet.setAVATARID(aexpiretimer.getID());
    }

    public void makeStateMachine (AvatarSpecification _as, AvatarStateMachineOwner _ab) {
        AvatarBDStateMachineOwner block = (AvatarBDStateMachineOwner) listE.getTG (_ab);
        AvatarStateMachine asm = _ab.getStateMachine ();

        if (block == null) {
            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "No corresponding graphical block for " + _ab.getName());
            // TODO: adapt
            // ce.setAvatarBlock(_ab);
            ce.setTDiagramPanel(adp.getAvatarBDPanel());
            addCheckingError(ce);
            return;
        }

        AvatarSMDPanel asmdp = block.getAvatarSMDPanel();
        if (asmdp == null)
            return;

        String name = block.getOwnerName();

        int size = checkingErrors.size();

        TDiagramPanel tdp = (TDiagramPanel) asmdp;

        // search for start state
        AvatarSMDStartState tss = null;
        for (TGComponent tgc: asmdp.getComponentList ())
            if (tgc instanceof AvatarSMDStartState) {
                if (tss == null)
                    tss = (AvatarSMDStartState) tgc;
                else {
                    CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in the state machine diagram of " + name);
                    ce.setTDiagramPanel(tdp);
                    addCheckingError(ce);
                    return;
                }
            }

        if (tss == null) {
            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "No start state in the state machine diagram of " + name);
            ce.setTDiagramPanel(tdp);
            addCheckingError(ce);
            return;
        }

        // This shall also be true for all composite state: at most one start state!
        if (checkForStartStateOfCompositeStates (asmdp) != null) {
            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in composite state");
            ce.setTDiagramPanel(tdp);
            addCheckingError(ce);
            return;
        }

        int choiceID = 0;
        // First pass: creating AVATAR components, but no interconnection between them
        for (TGComponent tgc: asmdp.getAllComponentList ())
            try {
                // Receive signal
                if (tgc instanceof AvatarSMDReceiveSignal)
                    this.translateAvatarSMDReceiveSignal (tdp, _as, _ab, (AvatarSMDReceiveSignal) tgc);
                // Send signals
                else if (tgc instanceof AvatarSMDSendSignal)
                    this.translateAvatarSMDSendSignal (tdp, _as, _ab, (AvatarSMDSendSignal) tgc);
                // State
                else if (tgc instanceof AvatarSMDState)
                    this.translateAvatarSMDState (tdp, _as, _ab, (AvatarSMDState) tgc);
                // Choice
                else if (tgc instanceof AvatarSMDChoice) {
                    AvatarState astate = new AvatarState ("choice__" + choiceID, tgc);
                    choiceID ++;
                    asm.addElement (astate);
                    listE.addCor (astate, tgc);
                    tgc.setAVATARID (astate.getID());
                }
                // Random
                else if (tgc instanceof AvatarSMDRandom)
                    this.translateAvatarSMDRandom (tdp, _as, _ab, (AvatarSMDRandom) tgc);
                // Set timer
                else if (tgc instanceof AvatarSMDSetTimer)
                    this.translateAvatarSMDSetTimer (tdp, _as, _ab, (AvatarSMDSetTimer) tgc);
                // Reset timer
                else if (tgc instanceof AvatarSMDResetTimer)
                    this.translateAvatarSMDResetTimer (tdp, _as, _ab, (AvatarSMDResetTimer) tgc);
                // Expire timer
                else if (tgc instanceof AvatarSMDExpireTimer)
                    this.translateAvatarSMDExpireTimer (tdp, _as, _ab, (AvatarSMDExpireTimer) tgc);
                // Start state
                else if (tgc instanceof AvatarSMDStartState) {
                    AvatarStartState astart = new AvatarStartState("start", tgc);
                    this.listE.addCor (astart, tgc);
                    tgc.setAVATARID (astart.getID());
                    asm.addElement(astart);
                    if (tgc.getFather() == null)
                        asm.setStartState(astart);
                // Stop state
                } else if (tgc instanceof AvatarSMDStopState) {
                    AvatarStopState astop = new AvatarStopState ("stop", tgc);
                    this.listE.addCor(astop, tgc);
                    tgc.setAVATARID(astop.getID());
                    asm.addElement(astop);
                }
            } catch (CheckingError ce) {
                // TODO: adapt
                // ce.setAvatarBlock (_ab);
                ce.setTDiagramPanel (tdp);
                ce.setTGComponent (tgc);
                ce.addMessagePrefix ("State Machine of " + name + ": ");
                this.addCheckingError (ce);
            }

        if (checkingErrors.size() != size)
            return;

        // Remove all internal start states
        asm.removeAllInternalStartStates();

        // Make hierachy between states and elements
        for (TGComponent tgc: asmdp.getAllComponentList ())
            if (tgc != null && tgc.getFather() != null) {
                AvatarStateMachineElement element1 = (AvatarStateMachineElement)(listE.getObject(tgc));
                AvatarStateMachineElement element2 = (AvatarStateMachineElement)(listE.getObject(tgc.getFather()));
                if (element1 != null && element2 != null && element2 instanceof AvatarState)
                    element1.setState ((AvatarState) element2);
            }

        // Make next: handle transitions
        for (TGComponent tgc: asmdp.getAllComponentList ())
            if (tgc instanceof AvatarSMDConnector) {
                AvatarSMDConnector asmdco = (AvatarSMDConnector) tgc;
                TGComponent tgc1 = tdp.getComponentToWhichBelongs (asmdco.getTGConnectingPointP1());
                TGComponent tgc2 = tdp.getComponentToWhichBelongs (asmdco.getTGConnectingPointP2());
                if (tgc1 == null || tgc2 == null)
                    TraceManager.addDev("Tgcs null in Avatar translation");
                else {
                    AvatarStateMachineElement element1 = (AvatarStateMachineElement)(listE.getObject(tgc1));
                    AvatarStateMachineElement element2 = (AvatarStateMachineElement)(listE.getObject(tgc2));
                    if (element1 != null && element2 != null) {
                        AvatarTransition at = new AvatarTransition (_ab, "avatar transition", tgc);

                        // Guard
                        String tmp = modifyString (asmdco.getGuard());
                        AvatarGuard guard = AvatarGuard.createFromString (_ab, tmp);
                        if (guard.isElseGuard())
                            at.setGuard(guard);
                        else {
                            int error = AvatarSyntaxChecker.isAValidGuard (_as, _ab, tmp);
                            if (error < 0)
                                this.makeError (error, tdp, _ab, tgc, "transition guard", tmp);
                            else
                                at.setGuard (guard);
                        }

                        // Delays
                        String tmp1 = modifyString (asmdco.getAfterMinDelay ());
                        int error = AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, tmp1);
                        if (error < 0) {
                            this.makeError (error, tdp, _ab, tgc, "after min delay", tmp1);
                            tmp1 = null;
                        }
                        String tmp2 = modifyString (asmdco.getAfterMaxDelay ());
                        error = AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, tmp2);
                        if (error < 0) {
                            this.makeError(error, tdp, _ab, tgc, "after max delay", tmp2);
                            tmp2 = null;
                        }

                        if (tmp1 != null && tmp2 != null)
                            at.setDelays(tmp1, tmp2);

                        // Compute min and max
                        tmp1 = modifyString (asmdco.getComputeMinDelay ());
                        error = AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, tmp1);
                        if (error < 0) {
                            this.makeError (error, tdp, _ab, tgc, "compute min ", tmp1);
                            tmp1 = null;
                        }
                        tmp2 = modifyString(asmdco.getComputeMaxDelay());
                        error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp2);
                        if (error < 0) {
                            this.makeError (error, tdp, _ab, tgc, "compute max ", tmp2);
                            tmp2 = null;
                        }

                        if (tmp1 != null && tmp2 != null)
                            at.setComputes(tmp1, tmp2);

                        // Actions
                        for(String s: asmdco.getActions())
                            if (s.trim().length() > 0) {
                                s = modifyString(s.trim());

                                // Variable assignation or method call?
                                if (!isAVariableAssignation(s)) {
                                    // Method call
                                    int index2 = s.indexOf(";");
                                    if (index2 != -1)
                                        this.makeError(error, tdp, _ab, tgc, "transition action", s);

                                    s = modifyStringMethodCall(s, _ab.getName());
                                    if (!AvatarBlock.isAValidMethodCall (_ab, s)) {
                                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed transition method call: " + s);
                                        // TODO: adapt
                                        // ce.setAvatarBlock(_ab);
                                        ce.setTDiagramPanel(tdp);
                                        ce.setTGComponent(tgc);
                                        addCheckingError(ce);
                                    } else
                                        at.addAction(s);
                                } else {
                                    // Variable assignation
                                    error = AvatarSyntaxChecker.isAValidVariableExpr (_as, _ab, s);
                                    if (error < 0)
                                        this.makeError (error, tdp, _ab, tgc, "transition action", s);
                                    else
                                        at.addAction (s);
                                }
                            }

                        element1.addNext (at);
                        at.addNext (element2);
                        this.listE.addCor (at, tgc);
                        tgc.setAVATARID (at.getID());
                        asm.addElement (at);

                        // Check for after on composite transitions
                        if (at.hasDelay() && element1 instanceof AvatarState && asm.isACompositeTransition(at)) {
                            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "After clause cannot be used on composite transitions. Use timers instead.");
                            // TODO: adapt
                            // ce.setAvatarBlock(_ab);
                            ce.setTDiagramPanel(tdp);
                            ce.setTGComponent(tgc);
                            addCheckingError(ce);
                        }
                    }
                }
            }

        asm.handleUnfollowedStartState(_ab);

        // Investigate all states -> put warnings for all empty transitions from a state to the same one (infinite loop)
        int nb;
        for (AvatarStateMachineElement asmee: asm.getListOfElements())
            if (asmee instanceof AvatarState && ((AvatarState)asmee).hasEmptyTransitionsOnItself(asm) > 0) {
                CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "State(s) " + asmee.getName() + " has empty transitions on itself");
                // TODO: adapt
                // ce.setAvatarBlock(_ab);
                ce.setTDiagramPanel(tdp);
                ce.setTGComponent((TGComponent)(asmee.getReferenceObject()));
                addWarning(ce);
            }
    }

    private void makeError(int _error, TDiagramPanel _tdp, AvatarStateMachineOwner _ab, TGComponent _tgc, String _info, String _element) {
        if (_error == -3) {
            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Undeclared variable in " + _info + ": " + _element);
            // TODO: adapt
            // ce.setAvatarBlock(_ab);
            ce.setTDiagramPanel(_tdp);
            ce.setTGComponent(_tgc);
            addCheckingError(ce);
        } else {
            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formatted " + _info + ": " + _element);
            // TODO: adapt
            // ce.setAvatarBlock(_ab);
            ce.setTDiagramPanel(_tdp);
            ce.setTGComponent(_tgc);
            addCheckingError(ce);
        }
    }

    // Checks whether all states with internal state machines have at most one start state
    private TGComponent checkForStartStateOfCompositeStates(AvatarSMDPanel _panel) {
        TGComponent tgc;
        ListIterator iterator = _panel.getComponentList().listIterator();
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof AvatarSMDState) {
                tgc = (((AvatarSMDState)(tgc)).checkForStartStateOfCompositeStates());
                if (tgc != null) {
                    return tgc;
                }
            }
        }
        return null;
    }


    public void createRelationsBetweenBlocks(AvatarSpecification _as, LinkedList<AvatarBDBlock> _blocks) {
        adp.getAvatarBDPanel().updateAllSignalsOnConnectors();
        Iterator iterator = adp.getAvatarBDPanel().getComponentList().listIterator();

        TGComponent tgc;
        AvatarBDPortConnector port;
        AvatarBDBlock block1, block2;
        LinkedList<String> l1, l2;
        int i;
        String name1, name2;
        AvatarRelation r;
        AvatarBlock b1, b2;
        avatartranslator.AvatarSignal atas1, atas2;

        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof AvatarBDPortConnector) {
                port = (AvatarBDPortConnector)tgc;
                block1 = port.getAvatarBDBlock1();
                block2 = port.getAvatarBDBlock2();

                //TraceManager.addDev("Searching block with name " + block1.getBlockName());
                b1 = _as.getBlockWithName(block1.getBlockName());
                b2 = _as.getBlockWithName(block2.getBlockName());

                if ((b1 != null) && (b2 != null)) {

                    r = new AvatarRelation("relation", b1, b2, tgc);
                    // Signals of l1
                    l1 = port.getListOfSignalsOrigin();
                    l2 = port.getListOfSignalsDestination();

                    for(i=0; i<l1.size(); i++) {
                        name1 = AvatarSignal.getSignalNameFromFullSignalString(l1.get(i));
                        name2 = AvatarSignal.getSignalNameFromFullSignalString(l2.get(i));
                        //TraceManager.addDev("Searching signal with name " + name1 +  " in block " + b1.getName());
                        atas1 = b1.getAvatarSignalWithName(name1);
                        atas2 = b2.getAvatarSignalWithName(name2);
                        if ((atas1 != null) && (atas2 != null)) {
                            r.addSignals(atas1, atas2);
                        } else {
                            TraceManager.addDev("null gates in AVATAR relation: " + name1 + " " + name2);
                        }
                    }

                    // Attribute of the relation
                    r.setBlocking(port.isBlocking());
                    r.setAsynchronous(port.isAsynchronous());
                    r.setSizeOfFIFO(port.getSizeOfFIFO());
                    r.setPrivate(port.isPrivate());
                    r.setBroadcast(port.isBroadcast());
                    r.setLossy(port.isLossy());

                    _as.addRelation(r);
                } else {
                    TraceManager.addDev("Null block b1=" + b1 + " b2=" + b2);
                }
            }
        }
    }

    private void addCheckingError(CheckingError ce) {
        if (checkingErrors == null) {
            checkingErrors = new Vector();
        }
        checkingErrors.addElement(ce);
    }

    private void addWarning(CheckingError ce) {
        if (warnings == null) {
            warnings = new Vector();
        }
        warnings.addElement(ce);
    }

    private String modifyString(String _input) {
        return Conversion.replaceAllChar(_input, '.', "__");
    }

    private String modifyStringMethodCall(String _input, String _blockName) {

        int index0 = _input.indexOf('(');
        int index1 = _input.indexOf(')');

        if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
            return _input;
        }


        String s = _input.substring(index0+1, index1).trim();
        String output = "";

        if (s.length() == 0) {
            return _input;
        }

        //TraceManager.addDev("-> -> Analyzing method call " + s);
        TAttribute ta, tatmp;

        String [] actions = s.split(",");
        s = "";
        for(int i=0; i<actions.length; i++) {
            ta = adp.getAvatarBDPanel().getAttribute(actions[i].trim(), _blockName);
            if (ta == null) {
                s = s + actions[i].trim();
            } else {
                if (ta.getType() == TAttribute.OTHER) {
                    Vector v0 = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
                    for(int j=0; j<v0.size(); j++) {
                        tatmp = (TAttribute)(v0.get(j));
                        s += actions[i].trim() + "__" + tatmp.getId();
                        if (j != v0.size()-1) {
                            s = s + ", ";
                        }
                    }
                } else {
                    s = s + actions[i].trim();
                }
            }
            if (i != actions.length-1) {
                s = s + ", ";
            }
        }

        s  = _input.substring(0, index0) + "(" + s + ")";

        // Managing output parameters
        index0 = s.indexOf("=");
        if (index0 != -1) {
            String param = s.substring(0, index0).trim();
            ta = adp.getAvatarBDPanel().getAttribute(param, _blockName);
            if (ta == null) {
                TraceManager.addDev("-> -> NULL Param " + param + " in block " + _blockName);
                s = param + s.substring(index0, s.length());
            } else {
                if (ta.getType() == TAttribute.OTHER) {
                    String newparams = "";
                    Vector v0 = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
                    for(int j=0; j<v0.size(); j++) {
                        tatmp = (TAttribute)(v0.get(j));
                        newparams += param + "__" + tatmp.getId();
                        if (j != v0.size()-1) {
                            newparams = newparams + ", ";
                        }
                    }
                    if (v0.size() > 1) {
                        newparams = "(" + newparams + ")";
                    }
                    s = newparams + s.substring(index0, s.length());
                } else {
                    s = param + s.substring(index0, s.length());
                }
            }
        }

        //TraceManager.addDev("-> -> Returning method call " + s);

        return s;
    }

    public boolean isAVariableAssignation (String _input) {
        int index = _input.indexOf('=');
        if (index == -1) {
            return false;
        }

        // Must check whether what follows the '=' is a function or not.
        String tmp = _input.substring(index+1, _input.length()).trim();

        index = tmp.indexOf('(');
        if (index == -1) {
            return true;
        }

        tmp = tmp.substring(0, index);

        //TraceManager.addDev("rest= >" + tmp + "<");
        int length = tmp.length();
        tmp = tmp.trim();
        if (tmp.length() != length) {
            TraceManager.addDev("pb of length");
            return true;
        }

        return !(TAttribute.isAValidId(tmp, false, false));
    }

    public void checkForAfterOnCompositeTransition() {

    }



}
