/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class DiagramTreeRenderer
 * Icons and tooltiptext for the main tree
 * Creation: 15/12/2003
 * @version 1.0 15/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tree;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import translator.*;
import ui.*;
import ui.ad.*;
import ui.atd.*;
import ui.cd.*;
import ui.iod.*;
import ui.ebrdd.*;
import ui.req.*;
import ui.sd.*;
import ui.dd.*;
import ui.ucd.*;
import ui.tmlad.*;
import ui.tmlcd.*;
import ui.tmldd.*;
import ui.tmlcompd.*;

import ui.ncdd.*;

import ui.oscd.*;
import ui.osad.*;

public class DiagramTreeRenderer extends DefaultTreeCellRenderer  {
    
    public DiagramTreeRenderer() {
        
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
        
        if (value instanceof GTURTLEModeling) {
            setIcon(IconManager.imgic8);
            setToolTipText("TURTLE modeling");
        } else if (value instanceof DesignPanel) {
            setIcon(IconManager.imgic14);
            setToolTipText("TURTLE Design");
        } else if (value instanceof TURTLEOSDesignPanel) {
            setIcon(IconManager.imgic14);
            setToolTipText("TURTLE-OS Design");
        } else if (value instanceof AnalysisPanel) {
            setIcon(IconManager.imgic18);
            setToolTipText("TURTLE Analysis");
        } else if (value instanceof DeploymentPanel) {
            setIcon(IconManager.imgic60);
            setToolTipText("TURTLE Deployment");
        } else if (value instanceof NCPanel) {
            setIcon(IconManager.imgic60);
            setToolTipText("Network Calculus");
        } else if (value instanceof RequirementPanel) {
            setIcon(IconManager.imgic1000);
            setToolTipText("TURTLE Requirement");
        } else if (value instanceof AttackTreePanel) {
            setIcon(IconManager.imgic1074);
            setToolTipText("TURTLE Requirement");
        } else if (value instanceof TClassDiagramPanel) {
            setIcon(IconManager.imgic14);
            setToolTipText("TURTLE Class diagram");
        } else if (value instanceof TURTLEOSClassDiagramPanel) {
            setIcon(IconManager.imgic14);
            setToolTipText("TURTLE-OS Class diagram");
        } else if (value instanceof InteractionOverviewDiagramPanel) {
            setIcon(IconManager.imgic17);
            setToolTipText("TURTLE Interaction Overview Diagram");
        } else if (value instanceof SequenceDiagramPanel) {
            setIcon(IconManager.imgic18);
            setToolTipText("TURTLE Sequence Diagram");
        } else if (value instanceof UseCaseDiagramPanel) {
            setIcon(IconManager.imgic19);
            setToolTipText("TURTLE Use Case Diagram");
        } else if (value instanceof TDeploymentDiagramPanel) {
            setIcon(IconManager.imgic60);
            setToolTipText("TURTLE Deployment Diagram");          
        } else if (value instanceof NCDiagramPanel) {
            setIcon(IconManager.imgic60);
            setToolTipText("NC Diagram");          
        } else if (value instanceof RequirementDiagramPanel) {
            setIcon(IconManager.imgic1000);
            setToolTipText("TURTLE Requirement Diagram");          
        } else if (value instanceof AttackTreeDiagramPanel) {
            setIcon(IconManager.imgic1074);
            setToolTipText("Attack Tree Diagram (SysML Parametric diagram)");          
        } else if (value instanceof EBRDDPanel) {
            setIcon(IconManager.imgic1058);
            setToolTipText("Event-Based Requirement Description Diagram");          
        } else if (value instanceof TActivityDiagramPanel) {
            setIcon(IconManager.imgic15);
            setToolTipText("TURTLE Activity Diagram of " + value.toString());
        } else if (value instanceof TURTLEOSActivityDiagramPanel) {
            setIcon(IconManager.imgic15);
            setToolTipText("TURTLE-OS Activity Diagram of " + value.toString());
        } else if (value instanceof TMLActivityDiagramPanel) {
            setIcon(IconManager.imgic63);
            setToolTipText("TML Activity Diagram of " + value.toString());
        } else if (value instanceof TMLTaskDiagramPanel) {
            setIcon(IconManager.imgic62);
            setToolTipText("TML Task Diagram of " + value.toString());
        } else if (value instanceof TMLDesignPanel) {
            setIcon(IconManager.imgic62);
            setToolTipText("TML Design");
        } else if (value instanceof TMLComponentDesignPanel) {
            setIcon(IconManager.imgic1208);
            setToolTipText("TML Component-based Design");
        } else if (value instanceof TMLComponentTaskDiagramPanel) {
            setIcon(IconManager.imgic1208);
            setToolTipText("TML Component Design");
        } else if (value instanceof TMLArchiPanel) {
            setIcon(IconManager.imgic60);
            setToolTipText("TML Architecture");
        } else if (value instanceof TMLArchiDiagramPanel) {
            setIcon(IconManager.imgic60);
            setToolTipText("TML Architecture Diagram: " + value.toString());
        } else if (value instanceof TGComponent) {
            TGComponent t = (TGComponent)value;
            setIcon(t.getImageIcon());
            setToolTipText(t.toString());
        } else if (value instanceof ValidationDataTree) {
            setIcon(IconManager.imgic34);
            setToolTipText("Validation data");
        } else if (value instanceof TClassesValidationDataTree) {
            setIcon(IconManager.imgic104);
            setToolTipText("TClasses taken into account at validation");
        } else if (value instanceof SyntaxAnalysisTree) {
            setIcon(IconManager.imgic36);
            setToolTipText("Results of the syntax analysis or of the formal code generation");
        } else if (value instanceof SyntaxAnalysisErrorTree) {
            setIcon(IconManager.imgic36);
            setToolTipText("Error(s) of the syntax analysis or of the formal code generation");
        } else if (value instanceof SyntaxAnalysisWarningTree) {
            setIcon(IconManager.imgic36);
            setToolTipText("Warning(s) of the syntax analysis or of the formal code generation");
        } else if (value instanceof CorrespondanceValidationDataTree) {
            setIcon(null);
            setToolTipText("Correspondance between actions on simulation traces / DTA / RG, and on TURTLE gates ");
        } else if (value instanceof GroupOfGates) {
            setIcon(null);
            setToolTipText("TURTLE Gates ");
        }  else if (value instanceof CheckingError) {
            setIcon(IconManager.imgic322);
            setToolTipText(value.toString());
        } else {
            setToolTipText(null);
        }
        
        return this;
    }
}
