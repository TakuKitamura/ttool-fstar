/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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


package tmltranslator;

import myutil.TraceManager;
import tmltranslator.tmlcp.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class TMLCP: data structure for the TMLCP
 * Creation: 18/02/2014
 *
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @version 1.1 10/06/2014
 */
public class TMLCP extends TMLElement {

    private TMLCPActivityDiagram mainCP;
    private ArrayList<TMLCPActivityDiagram> otherCPs; //Ref to other CPs
    private ArrayList<TMLCPSequenceDiagram> sds; //Ref to SDs

//    private int hashCode;
//    private boolean hashCodeComputed = false;

    public TMLCP(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        init();
    }

    public TMLCP(String _name) {
        super(_name, null);     //no reference to any object in the default constructor
        init();
    }

    private void init() {
        otherCPs = new ArrayList<TMLCPActivityDiagram>();
        sds = new ArrayList<TMLCPSequenceDiagram>();
    }
//
//    private void computeHashCode() {
//        /*TMLArchiTextSpecification architxt = new TMLArchiTextSpecification("spec.tarchi");
//          String s = architxt.toTextFormat(this);
//          hashCode = s.hashCode();*/
//        //TraceManager.addDev("TARCHI hashcode = " + hashCode);
//    }

//    private boolean checkDiagramDeclaration( TMLCPSequenceDiagram _tmlsdSection )       {
//
//        int i;
//        TMLCPActivityDiagram CPsection;
//
//        //first check the main CP
//        if( mainCP.containsSDDiagram( _tmlsdSection.getName() ) )       {
//            return true;
//        }
//        else    {
//            // then check on the other CPs
//            for( i = 0; i < otherCPs.size(); i++ )      {
//                CPsection = otherCPs.get(i);
//                if( CPsection.containsSDDiagram( _tmlsdSection.getName() ) )    {
//                    return true;
//                }
//            }
//            return false;
//        }
//    }

//    private boolean checkDiagramDeclaration( TMLCPActivityDiagram _tmlcpSection )       {
//
//        int i;
//        TMLCPActivityDiagram CPsection;
//
//        if( mainCP.containsADDiagram( _tmlcpSection.getName() ) )       { //first check in the main CP's list
//            return true;
//        }
//        else    {
//            for( i = 0; i < otherCPs.size(); i++ )      { // then check in the other CPs' lists
//                CPsection = otherCPs.get(i);
//                if( CPsection.containsADDiagram( _tmlcpSection.getName() ) )    {
//                    return true;
//                }
//            }
//            return false;
//        }
//    }

//    private boolean checkMultipleDiagramDeclaration( TMLCPSequenceDiagram _tmlsdSection )       {
//
//        int i, counterSD = 0;
//        String tempString;
//        TMLCPSequenceDiagram tempSD;
//
//        for( i = 0; i < sds.size(); i++ )       {
//            tempSD = sds.get(i);
//            tempString = tempSD.getName();
//            if( tempString.equals( _tmlsdSection.getName() ) )  {
//                counterSD++;
//            }
//        }
//        return ( counterSD > 0 );
//    }
//
//    private boolean checkMultipleDiagramDeclaration( TMLCPActivityDiagram _tmlcpSection )       {
//
//        int i, counterAD = 0;
//        String tempString;
//        TMLCPActivityDiagram tempAD;
//
//        for( i = 0; i < otherCPs.size(); i++ )  {
//            tempAD = otherCPs.get(i);
//            tempString = tempAD.getName();
//            if( tempString.equals( _tmlcpSection.getName() ) )  {
//                counterAD++;
//            }
//        }
//        return ( counterAD > 0 );
//    }

    public int getHashCode() {
        return 0;
        /*if (!hashCodeComputed) {
          computeHashCode();
          hashCodeComputed = true;
          }
          return hashCode;*/
    }

    public void setMainCP(TMLCPActivityDiagram _tmlcpSection) {
        mainCP = _tmlcpSection;
    }

    public void addCPActivityDiagram(TMLCPActivityDiagram _diag) {
        otherCPs.add(_diag);
    }

    public void addCPSequenceDiagram(TMLCPSequenceDiagram _sd) {
        sds.add(_sd);
    }

    /* Debugging methods */
    public int getNumSections() {
        return otherCPs.size();
    }

    public int getNumSequences() {
        return sds.size();
    }

    public List<TMLCPActivityDiagram> getCPActivityDiagrams() {
        return otherCPs;
    }

    public List<TMLCPSequenceDiagram> getCPSequenceDiagrams() {
        return sds;
    }

    public TMLCPActivityDiagram getMainCP() {
        return mainCP;
    }

    public List<String> checkSDsDefinition() {

        List<String> undefinedSDs = new ArrayList<String>();
        List<String> SDlist;
        String declaredSD;
        TMLCPSequenceDiagram definedSD;
        boolean isSDdefined = false;
        int i, j, k;

        //check in mainCP first
        SDlist = mainCP.getSDlist();
        for (j = 0; j < SDlist.size(); j++) {
            declaredSD = SDlist.get(j);
            for (i = 0; i < sds.size(); i++) {
                definedSD = sds.get(i);
                if (declaredSD.equals(definedSD.getName())) {
                    isSDdefined = true;
                    break;
                }
            }
            if (!isSDdefined) {
                undefinedSDs.add(declaredSD);
            }
            isSDdefined = false;        //reset to false for next iteration of the list of declared SDs
        }

        //then check in all other CPs
        isSDdefined = false;
        TMLCPActivityDiagram AD;

        for (k = 0; k < otherCPs.size(); k++) {
            AD = otherCPs.get(k);
            SDlist = AD.getSDlist();
            for (j = 0; j < SDlist.size(); j++) {
                declaredSD = SDlist.get(j);     //the diagram to check
                for (i = 0; i < sds.size(); i++) {
                    definedSD = sds.get(i);
                    if (declaredSD.equals(definedSD.getName())) {
                        isSDdefined = true;
                        break;
                    }
                }
                if (!isSDdefined) {
                    undefinedSDs.add(declaredSD);
                }
                isSDdefined = false;    //reset to false for next iteration of the list of declared SDs
            }
        }

        return undefinedSDs;
    }

    public List<String> checkADsDefinition() {

        List<String> undefinedADs = new ArrayList<String>();
        List<String> ADlist;
        String declaredAD;
        TMLCPActivityDiagram definedAD;
        boolean isADdefined = false;
        int i, j, k;

        //check in mainCP first
        ADlist = mainCP.getADlist();
        for (j = 0; j < ADlist.size(); j++) {
            declaredAD = ADlist.get(j);
            for (i = 0; i < otherCPs.size(); i++) {
                definedAD = otherCPs.get(i);
                if (declaredAD.equals(definedAD.getName())) {
                    isADdefined = true;
                    break;
                }
            }
            if (!isADdefined) {
                undefinedADs.add(declaredAD);
            }
            isADdefined = false;        //reset to false for next iteration of the list of declared SDs
        }

        //then check in all other CPs
        isADdefined = false;
        TMLCPActivityDiagram AD;

        for (k = 0; k < otherCPs.size(); k++) {
            AD = otherCPs.get(k);
            ADlist = AD.getADlist();
            for (j = 0; j < ADlist.size(); j++) {
                declaredAD = ADlist.get(j);     //the diagram to check
                for (i = 0; i < otherCPs.size(); i++) {
                    definedAD = otherCPs.get(i);
                    if (declaredAD.equals(definedAD.getName())) {
                        isADdefined = true;
                        break;
                    }
                }
                if (!isADdefined) {
                    undefinedADs.add(declaredAD);
                }
                isADdefined = false;    //reset to false for next iteration of the list of declared SDs
            }
        }
        return undefinedADs;
    }

    public void correctReferences() {

        TMLCPActivityDiagram CPsection;
        int i;

        mainCP.correctReferences(this);
        for (i = 0; i < otherCPs.size(); i++) {
            CPsection = otherCPs.get(i);
            CPsection.correctReferences(this);
        }
    }

    public void generateNexts() {
        mainCP.generateNexts();
        for (TMLCPActivityDiagram diag : otherCPs) {
            diag.generateNexts();
        }

    }

    public void removeADConnectors() {
        mainCP.removeADConnectors();
        for (TMLCPActivityDiagram diag : otherCPs) {
            diag.removeADConnectors();
        }
    }

    public void splitADs() {

        ArrayList<TMLCPActivityDiagram> all = new ArrayList<TMLCPActivityDiagram>();
        all.addAll(mainCP.splitADs());
        for (TMLCPActivityDiagram diag : otherCPs) {
            all.addAll(diag.splitADs());
        }
        otherCPs.addAll(all);
    }

    public TMLCPElement getNonConnectedElement() {
        TMLCPElement elt;
        elt = mainCP.getNonConnectedElement();
        if (elt != null) {
            return elt;
        }
        for (TMLCPActivityDiagram diag : otherCPs) {
            elt = diag.getNonConnectedElement();
            if (elt != null) {
                return elt;
            }
        }
        return null;
    }

    public List<TMLCPElement> getAllNonConnectedElements() {
        List<TMLCPElement> list = new LinkedList<TMLCPElement>();
        //TMLCPElement elt;
        list.addAll(mainCP.getAllNonConnectedElements());
        for (TMLCPActivityDiagram diag : otherCPs) {
            list.addAll(diag.getAllNonConnectedElements());
        }
        return list;
    }

    public List<TMLCPElement> removeAllNonConnectedElements() {
        List<TMLCPElement> list = new LinkedList<TMLCPElement>();
        //TMLCPElement elt;
        list.addAll(mainCP.removeAllNonConnectedElements());
        for (TMLCPActivityDiagram diag : otherCPs) {
            list.addAll(diag.removeAllNonConnectedElements());
        }
        return list;
    }

    public void printDataStructure() {

        List<TMLCPActivityDiagram> CPlist = new ArrayList<TMLCPActivityDiagram>();
        List<TMLCPSequenceDiagram> SDlist = new ArrayList<TMLCPSequenceDiagram>();
        List<TMLAttribute> listAttributes = new ArrayList<TMLAttribute>();
        TMLCPActivityDiagram tempCP;
        TMLCPSequenceDiagram tempSD;
        TMLAttribute attr;
        int i, j, k;

        TraceManager.addDev("The data structure contains " + getNumSections() + " CP sections (AD) and " +
                getNumSequences() + " CP sequences (SD):");

        //Print the data structure for the main CP section

        //Print the data structure for the CP sections
        CPlist = getCPActivityDiagrams();
        for (i = 0; i < CPlist.size(); i++) {
            tempCP = CPlist.get(i);
            //TraceManager.addDevf("Activity Diagram n. %d: %s\n", i + 1, tempCP.getName());
            /*listAttributes = tempCP.getAttributeList();

            //Print attributes
            TraceManager.addDevf( "\tAttributes:\n");
            for( j = 0; j < listAttributes.size(); j++ )        {
            attr = listAttributes.get( j );
            TraceManager.addDevf( "\t\t%s\t%s\t%s\n",  attr.getName(), attr.getType(), attr.getInitialValue() );
            }*/

            //Print list of AD sections
            List<String> ADList;
            ADList = tempCP.getADlist();
            TraceManager.addDev("\tDeclared AD: ");
            for (j = 0; j < ADList.size(); j++) {
                TraceManager.addDev("\t\t\t" + ADList.get(j));
            }

            //Print list of SD sections
            List<String> SDList;
            SDList = tempCP.getSDlist();
            TraceManager.addDev("\tDeclared SD: ");
            for (j = 0; j < SDList.size(); j++) {
                TraceManager.addDev("\t\t\t" + SDList.get(j));
            }

            //Print list of Elements
            List<TMLCPElement> ElementsList;
            //TMLCPElement tempElem;
            ElementsList = tempCP.getElements();
            TraceManager.addDev("\tDeclared elements:");
            for (j = 0; j < ElementsList.size(); j++) {
                if (ElementsList.get(j) instanceof TMLCPRefAD) {
                    TMLCPRefAD refCP = (TMLCPRefAD) ElementsList.get(j);
                    tempCP = refCP.getReference();
                    ADList = tempCP.getADlist();
                    TraceManager.addDev("\tPrinting from reference to " + tempCP.getName());
                    for (k = 0; k < ADList.size(); k++) {
                        TraceManager.addDev("\t\t\t\t" + ADList.get(k));
                    }
                    TraceManager.addDev("\tStop printing from reference");
                }
                /*else  {
                  TMLCPSequenceDiagram tempSD = tempElem.getReference();
                  }*/
                TraceManager.addDev("\t\t\t" + ElementsList.get(j));
            }

            TraceManager.addDev("\n");
        }

        SDlist = getCPSequenceDiagrams();
        for (i = 0; i < SDlist.size(); i++) {
            tempSD = SDlist.get(i);
            TraceManager.addDev("Sequence Diagram n. " + (i+1) + ": " + tempSD.getName() + "\n");

            //Print Variables
            listAttributes = tempSD.getAttributes();
            //TraceManager.addDevf("\tAttributes:\n");
            for (j = 0; j < listAttributes.size(); j++) {
                attr = listAttributes.get(j);
                //TraceManager.addDev("\t\t %s\t%s\t%s\n", attr.getName(), attr.getType(), attr.getInitialValue());
            }

            //Print Instances
            List<TMLSDInstance> listInstances;
            TMLSDInstance inst;
            listInstances = tempSD.getInstances();
            TraceManager.addDev("\tInstances:");
            for (j = 0; j < listInstances.size(); j++) {
                inst = listInstances.get(j);
                //TraceManager.addDev("\t\t" + inst.getName() + "\n");
            }

            //Print Messages
            List<TMLSDMessage> listMessages;
            List<TMLAttribute> msgAttributes;
            TMLSDMessage msg;
            listMessages = tempSD.getMessages();
            TraceManager.addDev("\tMessages:");
            for (j = 0; j < listMessages.size(); j++) {
                msg = listMessages.get(j);
                //TraceManager.addDevf("\t\t%s\n", msg.getName());
                msgAttributes = msg.getAttributes();
                for (k = 0; k < msgAttributes.size(); k++) {
                    //TraceManager.addDevf("\t\t\t%s\n", msgAttributes.get(k));
                }
            }
        }
    }

    @Override
    public String toString() {

        String s = "\n*** Communication Pattern: " + getName() + "***\n";
        s += mainCP.toString();

        for (tmltranslator.tmlcp.TMLCPActivityDiagram diag : otherCPs) {
            s += diag.toString();
        }

        for (tmltranslator.tmlcp.TMLCPSequenceDiagram diag : sds) {
            s += diag.toString();
//            s += sds.toString();
        }

        return s;
    }
}       //End of the class
