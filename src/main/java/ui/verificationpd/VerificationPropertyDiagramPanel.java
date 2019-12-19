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




package ui.verificationpd;

import myutil.TraceManager;
import org.w3c.dom.Element;
import ui.*;

/**
 * Class VerificationPropertyDiagramPanel
 * Panel for tracking verifications
 * Creation: 10/04/2019
 * @version 1.0 10/04/2019
 * @author Ludovic APVRILLE
 */
public class VerificationPropertyDiagramPanel extends TDiagramPanel implements TDPWithAttributes {
    //public Vector validated, ignored;

    public  VerificationPropertyDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        
        //TraceManager.addDev("Sysmlsec metho diagram created");
        /*TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
          addMouseListener(tdmm);
          addMouseMotionListener(tdmm);*/
    }

    public boolean actionOnDoubleClick(TGComponent tgc) {
        return true;
    }

    public boolean actionOnAdd(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
          TCDTClass tgcc = (TCDTClass)(tgc);
          mgui.addTClass(tgcc.getClassName());
          return true;
          }*/
        return false;
    }

    public boolean actionOnRemove(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
          TCDTClass tgcc = (TCDTClass)(tgc);
          mgui.removeTClass(tgcc.getClassName());
          resetAllInstancesOf(tgcc);
          return true;
          }*/
        return false;
    }

    public boolean actionOnValueChanged(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
          return actionOnDoubleClick(tgc);
          }*/
        return false;
    }

    public String getXMLHead() {
        return "<VerificationPropertyDiagramPanel name=\"" + name + "\"" + sizeParam() + zoomParam() + " >";
    }

    public String getXMLTail() {
        return "</VerificationPropertyDiagramPanel>";
    }

    public String getXMLSelectedHead() {
        return "<VerificationPropertyDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel +
                "\" heightSel=\"" + heightSel + "\" >";
    }

    public String getXMLSelectedTail() {
        return "</VerificationPropertyDiagramPanelCopy>";
    }

    public String getXMLCloneHead() {
        return "<VerificationPropertyDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\""
                + 0 + "\" >";
    }

    public String getXMLCloneTail() {
        return "</VerificationPropertyDiagramPanelCopy>";
    }


    public void makePostLoadingProcessing() throws MalformedModelingException {

    }

    public void enhance() {
        autoAdjust();
    }

    public void loadExtraParameters(Element elt) {
    }
}
