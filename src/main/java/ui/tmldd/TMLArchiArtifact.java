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

package ui.tmldd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tmltranslator.modelcompiler.*;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogTMLTaskArtifact;

import javax.swing.*;
import java.awt.*;

/**
 * Class TMLArchiArtifact
 * Artifact of a deployment diagram
 * Creation: 02/05/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.0 02/05/2005
 */
public class TMLArchiArtifact extends TGCWithoutInternalComponent implements SwallowedTGComponent, WithAttributes, TMLArchiTaskInterface {

	// Issue #31
	private static final int SPACE = 5;
	private static final int CRAN = 5;
	private static final int FILE_X = 20;
	private static final int FILE_Y = 25;
//    protected int lineLength = 5;
//    protected int textX =  5;
//    protected int textY =  15;
//    protected int textY2 =  35;
//    protected int space = 5;
//    protected int fileX = 20;
//    protected int fileY = 25;
//    protected int cran = 5;

    protected String oldValue = "";
    protected String referenceTaskName = "referenceToTask";
    protected String taskName = "name";
    protected int priority = 0; // Between 0 and 10
    //protected String operation = "";
    protected String operationMEC = "VOID";

    private ArchUnitMEC fatherArchUnitMECType = new CpuMEC();

    public String status="";
    public String lastTransaction="";
    
    public TMLArchiArtifact(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        // Issue #31
		textX = 5;
        textY = 15;
//        width = 100;
//        height = 40;
        minWidth = 100;
        initScaling( 100, 40 );

        nbConnectingPoint = 0;
        addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        value = "TMLDesign::task    ";
        taskName = "name";
        referenceTaskName = "TMLTask";

        makeFullValue();

        myImageIcon = IconManager.imgic702;
    }

    @Override
    public boolean isHidden() {
		//TraceManager.addDev("Archi task artifact: Am I hidden?" + getValue());
		boolean ret = false;
		if (tdp != null) {
		    if (tdp instanceof TMLArchiDiagramPanel) {
			ret = !(((TMLArchiDiagramPanel)(tdp)).inCurrentView(this));
			
		    }
		}
		//TraceManager.addDev("Hidden? -> " + ret);
		return ret;
    }

    public int getPriority() {
        return priority;
    }

    /*public String getOperation() {
      return operation;
      }*/

    @Override
    protected void internalDrawing(Graphics g) {
        if (oldValue.compareTo(value) != 0) {
            setValue(value, g);
        }

        g.drawRect(x, y, width, height);
        Color c = g.getColor();

        if (runningStatus.compareTo("running") == 0) {
            g.setColor(ColorManager.CURRENT_COMMAND_RUNNING);
        } else if (runningStatus.compareTo("runnable") == 0) {
            g.setColor(ColorManager.CURRENT_COMMAND_RUNNABLE);
        } else if (runningStatus.compareTo("suspended") == 0) {
            g.setColor(ColorManager.CURRENT_COMMAND_SUSPENDED);
        } else if (runningStatus.compareTo("terminated") == 0) {
            g.setColor(ColorManager.CURRENT_COMMAND_TERMINATED);
        } else {
            g.setColor(ColorManager.CPU_BOX_2);
        }
        g.fillRect(x + 1, y + 1, width - 1, height - 1);
        g.setColor(c);

        // Issue #31
        final int space = scale( SPACE );
        final int marginFileX = scale( SPACE + FILE_X );
        final int marginFileY = scale( SPACE + FILE_Y );
        final int marginCran = scale( SPACE + CRAN );
        
        g.drawLine(x+width- marginFileX /*space-fileX*/, y + space, x+width- marginFileX /*space-fileX*/, y+ marginFileY/*space+fileY*/);
        g.drawLine(x+width- marginFileX/*space-fileX*/, y + space, x+width-marginCran/*space-cran*/, y+space);
        g.drawLine(x+width-marginCran/*space-cran*/, y+space, x+width-space, y+ marginCran/*space + cran*/);
        g.drawLine(x+width-space, y+ marginCran/*space + cran*/, x+width-space, y+ marginFileY/*space+fileY*/);
        g.drawLine(x+width-space, y+ marginFileY/*space+fileY*/, x+width-marginFileX/*space-fileX*/, y+ marginFileY/*space+fileY*/);
        g.drawLine(x+width- marginCran /*space-cran*/, y+space, x+width- marginCran/*space-cran*/, y+ marginCran/*space+cran*/);
        g.drawLine(x+width- marginCran/*space-cran*/, y+ marginCran/*space+cran*/, x + width-space, y+marginCran /*space+cran*/);
        drawSingleString(g,value, x + textX , y + textY);
    }

    public void setValue(String val, Graphics g) {
        oldValue = value;
        int w  = g.getFontMetrics().stringWidth(value);
        
        final int marginFileX = scale( SPACE + FILE_X );
        int w1 = Math.max(minWidth, w + 2 * textX + marginFileX/*fileX + space*/);

        //
        if (w1 != width) {
            width = w1;
            resizeWithFather();
        }
        //
    }

    @Override
    public void resizeWithFather() {
        if ((father != null) && ((father instanceof TMLArchiCPUNode) || (father instanceof TMLArchiHWANode) || (father instanceof TMLArchiFPGANode))) {
            //
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String tmp;
        boolean error = false;
        if (father != null) {
            fatherArchUnitMECType = ((TMLArchiNode) father).getMECType();
        }
        JDialogTMLTaskArtifact dialog = new JDialogTMLTaskArtifact(frame, "Setting artifact attributes", this, operationMEC, fatherArchUnitMECType);
    //    dialog.setSize(400, 350);
        GraphicLib.centerOnParent(dialog, 400, 350);
        dialog.setVisible( true ); // blocked until dialog has been closed
        operationMEC = dialog.getOperation();

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getReferenceTaskName() == null) {
            return false;
        }

        if (dialog.getReferenceTaskName().length() != 0) {
            tmp = dialog.getReferenceTaskName();
            referenceTaskName = tmp;
        }

        if (dialog.getTaskName().length() != 0) {
            tmp = dialog.getTaskName();

            if (!TAttribute.isAValidId(tmp, false, false, false)) {
                error = true;
            } else {
                taskName = tmp;
            }
        }

        priority = dialog.getPriority();

        if (error) {
            JOptionPane.showMessageDialog(frame,
                    "Name is non-valid",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        makeFullValue();

        return !error;
    }

    public void makeFullValue() {
        value = referenceTaskName + "::" + taskName;
    }

    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }

    @Override
    public int getType() {
        return TGComponentManager.TMLARCHI_ARTIFACT;
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append( "<info value=\"" + value + "\" taskName=\"" + taskName + "\" referenceTaskName=\"" );
        sb.append( referenceTaskName );
        sb.append( "\" priority=\"" );
        sb.append( priority );
        sb.append( "\" operationMEC=\"" );
        sb.append( operationMEC );
        sb.append( "\" fatherComponentMECType=\"" + fatherArchUnitMECType.getIndex() );
        sb.append( "\" />\n" );
        sb.append( "</extraparam>\n" );
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
         //   int t1id;
            String svalue = null, sname = null, sreferenceTask = null;
            String prio;

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                svalue = elt.getAttribute("value");
                                sname = elt.getAttribute("taskName");
                                sreferenceTask = elt.getAttribute("referenceTaskName");
                                prio = elt.getAttribute("priority");
                                if (elt != null) {
                                    priority = Integer.decode(prio).intValue();
                                }
                                operationMEC = elt.getAttribute("operationMEC");
                                //TraceManager.addDev("operationMEC:" + operationMEC);
                                String tmp = elt.getAttribute("operation");
                                //TraceManager.addDev("operation:" + tmp);
                                if ((tmp != null) && (tmp.length() > 0)) {
                                    operationMEC = tmp;
                                }

                                if ((elt.getAttribute("fatherComponentMECType") != null) && (elt.getAttribute("fatherComponentMECType").length() > 0)) {
                                    if (elt.getAttribute("fatherComponentMECType").length() > 1) {
                                        fatherArchUnitMECType = ArchUnitMEC.Types.get(0);
                                    } else {
                                        fatherArchUnitMECType = ArchUnitMEC.Types.get(Integer.valueOf(elt.getAttribute("fatherComponentMECType")));
                                    }
                                }
                            }
                            if (svalue != null) {
                                value = svalue;
                            }
                            if (sname != null){
                                taskName = sname;
                            }
                            if (sreferenceTask != null) {
                                referenceTaskName = sreferenceTask;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException( e );
        }
        makeFullValue();
    }

    public DesignPanel getDesignPanel() {
        return tdp.getGUI().getDesignPanel(value);
    }

    public String getReferenceTaskName() {
        return referenceTaskName;
    }

    public void setReferenceTaskName(String _referenceTaskName) {
        referenceTaskName = _referenceTaskName;
        makeFullValue();
    }
    public void setFullName(String _taskName, String _referenceTaskName){
        taskName= _taskName;
        referenceTaskName=_referenceTaskName;
        makeFullValue();
    }
    public String getTaskName() {
        return taskName;
    }
    
    public void setTaskName(String s) {
        taskName = s;
    }

    public String getAttributes() {
        return "Priority = " + priority;
    }

    public String getOperationMEC() {
        return operationMEC;
    }

    public int getOperationType()       {
        if( fatherArchUnitMECType instanceof FepMEC )   {
            if( operationMEC.equals( "CWM" ) )     {
                return FepOperationMEC.CWM_MEC;
            }
            else if( operationMEC.equals( "CWL" ) )        {
                //TraceManager.addDev( "Operation: " + operation + " returns " + FepOperationMEC.CwlMEC );
                return FepOperationMEC.CWL_MEC;
            }
            else if( operationMEC.equals( "CWA" ) )        {
                //TraceManager.addDev( "Operation: " + operation + " returns " + FepOperationMEC.CwaMEC );
                return FepOperationMEC.CWA_MEC;
            }
            else if( operationMEC.equals( "CWP" ) )        {
                //TraceManager.addDev( "Operation: " + operation + " returns " + FepOperationMEC.CwpMEC );
                return FepOperationMEC.CWP_MEC;
            }
            else if( operationMEC.equals( "FFT" ) )        {
                //TraceManager.addDev( "Operation: " + operation + " returns " + FepOperationMEC.FftMEC );
                return FepOperationMEC.FFT_MEC;
            }
            else if( operationMEC.equals( "SUM" ) )        {
                //TraceManager.addDev( "Operation: " + operation + " returns " + FepOperationMEC.SumMEC );
                return FepOperationMEC.SUM_MEC;
            }
        }
        else if( fatherArchUnitMECType instanceof MapperMEC )   {
            //TraceManager.addDev( "Operation: " + operation + " returns " + OperationMEC.MappOperationMEC );
            return OperationMEC.MAPP_OPERATION_MEC;
        }
        else if( fatherArchUnitMECType instanceof InterleaverMEC        )       {
            //TraceManager.addDev( "Operation: " + operation + " returns " + OperationMEC.IntlOperationMEC );
            return OperationMEC.INTL_OPERATION_MEC;
        }
        else if( fatherArchUnitMECType instanceof AdaifMEC )    {
            //TraceManager.addDev( "Operation: " + operation + " returns " + OperationMEC.AdaifOperationMEC );
            return OperationMEC.ADAIF_OPERATION_MEC;
        }
        else if( fatherArchUnitMECType instanceof CpuMEC )      {
            //TraceManager.addDev( "Operation: " + operation + " returns " + OperationMEC.CpuOperationMEC );
            return OperationMEC.CPU_OPERATION_MEC;
        }
        return -1;
    }

    public ArchUnitMEC getArchUnitMEC() {
        return fatherArchUnitMECType;
    }
}
