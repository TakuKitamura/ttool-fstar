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
 * Class AvatarTreePanelTranslator
 * Creation: 13/04/2015
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.util.*;

import myutil.*;
import ui.atd.*;

import attacktrees.*;
//import translator.*;
import ui.window.*;


public class AttackTreePanelTranslator {


    protected AttackTreePanel atp;
    protected Vector checkingErrors, warnings;
    protected CorrespondanceTGElement listE; // usual list
    //protected CorrespondanceTGElement listB; // list for particular element -> first element of group of blocks
    protected LinkedList <TDiagramPanel> panels;


    public AttackTreePanelTranslator(AttackTreePanel _atp) {
        atp = _atp;
        reinit();
    }

    public void reinit() {
        checkingErrors = new Vector();
        warnings = new Vector();
        listE = new CorrespondanceTGElement();
        panels = new LinkedList <TDiagramPanel>();
    }

    public Vector getCheckingErrors() {
        return checkingErrors;
    }

    public Vector getWarnings() {
        return warnings;
    }

    public CorrespondanceTGElement getCorrespondanceTGElement() {
        return listE;
    }

    public AttackTree translateToAttackTreeDataStructure() {
      
        AttackTree at = new AttackTree("AttackTree", atp);

        
	for(TDiagramPanel panel: atp.panels) {
	    if (panel instanceof AttackTreeDiagramPanel) {
		translate((AttackTreeDiagramPanel)panel);
	    }
	}

	return at;
        
    }

    public void translate(AttackTreeDiagramPanel atdp) {
    }


  

   

    /*public void createBlocks(AvatarSpecification _as, LinkedList<AvatarBDBlock> _blocks) {
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
            ab = new AvatarBlock(block.getBlockName(), block);
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
                    //TraceManager.addDev(" -> Other type found: " + a.getTypeOther());
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
	}*/

   
    /*}

        //TraceManager.addDev("Size of vector:" + v.size());
        for(i=0; i<v.size(); i++) {
            aa = _ab.getAvatarAttributeWithName((String)(v.get(i)));
            if (aa == null) {
                CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + _name + " in signal expression: " + _idOperator);
                ce.setAvatarBlock(_ab);
                ce.setTDiagramPanel(_tdp);
                ce.setTGComponent(_tgc);
                addCheckingError(ce);
                return ;
            } else {
                //TraceManager.addDev("-> Adding attr in action on signal in block " + _ab.getName() + ":" + _name + "__" + tatmp.getId());
                _aaos.addValue((String)(v.get(i)));
            }
        }


	}*/

   

          


}
