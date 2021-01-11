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




package myutil;

import help.HelpEntry;
import translator.CheckingError;
import translator.GroupOfGates;
import ui.*;
import ui.ad.TActivityDiagramPanel;
import ui.atd.AttackTreeDiagramPanel;
import ui.ftd.FaultTreeDiagramPanel;
import ui.avatarad.AvatarADPanel;
import ui.avatarbd.AvatarBDPanel;
import ui.avatarcd.AvatarCDPanel;
import ui.avatardd.ADDDiagramPanel;
import ui.avatarmad.AvatarMADPanel;
import ui.avatarmethodology.AvatarMethodologyDiagramPanel;
import ui.avatarpd.AvatarPDPanel;
import ui.avatarrd.AvatarRDPanel;
import ui.avatarsmd.AvatarSMDPanel;
import ui.cd.TClassDiagramPanel;
import ui.dd.TDeploymentDiagramPanel;
import ui.diplodocusmethodology.DiplodocusMethodologyDiagramPanel;
import ui.ebrdd.EBRDDPanel;
import graph.RG;
import ui.iod.InteractionOverviewDiagramPanel;
import ui.ncdd.NCDiagramPanel;
import ui.osad.TURTLEOSActivityDiagramPanel;
import ui.oscd.TURTLEOSClassDiagramPanel;
import ui.req.RequirementDiagramPanel;
import ui.sd.SequenceDiagramPanel;
import ui.sysmlsecmethodology.SysmlsecMethodologyDiagramPanel;
import ui.tmlad.TMLActivityDiagramPanel;
import ui.tmlcd.TMLTaskDiagramPanel;
import ui.tmlcompd.TMLComponentTaskDiagramPanel;
import ui.tmlcp.TMLCPPanel;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmlsd.TMLSDPanel;
import ui.ucd.UseCaseDiagramPanel;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;



/**
   * Class TreeRendererStats
   * Icons and tooltiptext for the main tree
   * Creation: 11/01/2021
   * @version 1.0 11/01/2021
   * @author Ludovic APVRILLE
 */
public class TreeRendererStats extends DefaultTreeCellRenderer  {

    public TreeRendererStats() {

    }

    public Component getTreeCellRendererComponent(
                                                  JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {

        super.getTreeCellRendererComponent(
                                           tree, value, sel,
                                           expanded, leaf, row,
                                           hasFocus);

        if (value instanceof DataElement) {
            //setIcon(IconManager.imgic80);
            setToolTipText(value.toString());
        } else {
            setToolTipText(null);
        }

        return this;
    }
}
