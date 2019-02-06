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




package ui.tmlcompd;

import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogTMLCompositePort;
import ui.avatarrd.AvatarRDRequirement;
import ui.tmlad.TMLADReadChannel;
import ui.tmlad.TMLADWriteChannel;
import ui.tmlad.TMLADSendEvent;
import ui.tmlad.TMLADSendRequest;
import ui.tmlad.TMLADWaitEvent;
import ui.tmlad.TMLADNotifiedEvent;
import ui.tmldd.TMLArchiCPNode;
import ui.tmldd.TMLArchiPortArtifact;

import proverifspec.ProVerifResultTrace;
import proverifspec.ProVerifResultTraceStep;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import ui.interactivesimulation.JFrameSimulationSDPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * Class TMLCPrimitivePort
 * Primitive port. To be used in TML component task diagrams
 * Creation: 12/03/2008
 * @version 1.0 12/03/2008
 * @author Ludovic APVRILLE
 */
public abstract class TMLCPrimitivePort extends TGCScalableWithInternalComponent implements SwallowedTGComponent, LinkedReference, WithAttributes {
    protected Color myColor;
    protected int orientation;
    protected int oldx, oldy;
    protected int halfwidth = 13;
    protected int currentOrientation = GraphicLib.NORTH;

    protected int nbMaxAttribute = 5;
    protected TType list[];
    protected int maxSamples = 8;
    protected int widthSamples = 4;
    protected boolean isFinite = false;
    protected boolean isBlocking = true;
    public boolean isOrigin = true;
    public int typep = 0;
    protected int oldTypep = typep;
    public String commName;

	//Authenticity lock parameters
	protected int authlockwidth=(int) (16*tdp.getZoom());
    protected int authlockheight=(int) (16*tdp.getZoom());

    protected int xc=(int) (18*tdp.getZoom());
    protected int yc= (int) (12*tdp.getZoom());

    protected int authxoffset= (int) (20*tdp.getZoom());
    protected int authyoffset= (int) (18*tdp.getZoom());

    protected int authovalwidth=(int) (10*tdp.getZoom());
    protected int authovalheight=(int) (15*tdp.getZoom());


	//Confidentiality lock parameters
	protected int conflockwidth=(int) (9*tdp.getZoom());
    protected int conflockheight=(int) (7*tdp.getZoom());
    protected int confyoffset = 3*conflockheight;

    protected int confovalwidth=(int) (6*tdp.getZoom());
    protected int confovalheight=(int) (9*tdp.getZoom());

    protected boolean isLossy;
    protected boolean isPostex = false;
    protected boolean isPrex = false;
    protected int lossPercentage;
    protected int maxNbOfLoss; //-1 means no max

    //Security Verification
    public int checkConfStatus;

    public int checkSecConfStatus;
    public String secName="";

    public int checkWeakAuthStatus;
    public int checkStrongAuthStatus;
    public boolean checkConf;
    public boolean checkAuth;
    public static int NOCHECK= 0;
    public static int TOCHECK = 1;
    public static int CHECKED_CONF = 2;
    public static int CHECKED_UNCONF = 3;
        
    //ProVerifTrace
    String pragma;
    ProVerifResultTrace resTrace;
    

    public String mappingName="???";
    protected int decPoint = 3;


    protected boolean conflict = false;
    protected String conflictMessage;
    protected String dataFlowType = "VOID";
    protected String associatedEvent = "VOID";


    public int verification;
    
    public TMLCPrimitivePort(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(2*halfwidth, 2*halfwidth);

        minWidth = 1;
        minHeight = 1;

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = false;
        checkConf=false;

        //#issue 82
        commName = tdp.findTMLCPrimitivePortName("comm_");
        //commName = "comm";
        //value = "MyName";
        makeValue();
        setName("Primitive port");
        checkConfStatus= NOCHECK;
        list = new TType[nbMaxAttribute];
        for(int i=0; i<nbMaxAttribute; i++) {
            list[i] = new TType();
        }

        myImageIcon = IconManager.imgic1206;
    }

    public void initConnectingPoint(boolean in, boolean out, int nb) {
        nbConnectingPoint = nb;
        connectingPoint = new TGConnectingPoint[nb];
        int i;
        for (i=0; i<nbConnectingPoint; i++) {
            connectingPoint[i] = new TMLCPortConnectingPoint(this, 0, 0, in, out, 0.5, 0.0);
        }
    }

    public Color getMyColor() {
        return myColor;
    }

    public void internalDrawing(Graphics g) {
        if ((x != oldx) | (oldy != y)) {
            // Component has moved!
            manageMove();
            oldx = x;
            oldy = y;
        }

        //
        calculatePortColor();

        if (rescaled) {
            rescaled = false;
        }

        // Zoom is assumed to be computed
        Color c = g.getColor();
        if ((width > 2) && (height > 2)) {
            g.setColor(myColor);
            g.fillRect(x, y, width, height);
            if (conflict) {
                if (typep == 0) {
                    g.setColor(ColorManager.TML_PORT_CHANNEL);
                } else if (typep == 1) {
                    g.setColor(ColorManager.TML_PORT_EVENT);
                } else {
                    g.setColor(ColorManager.TML_PORT_REQUEST);
                }
                g.fillRect(x, y, width, height/2);
            }

            g.setColor(c);
        }
        g.drawRect(x, y, width, height);


        int []px = new int[5];
        int []py = new int[5];

        int xtmp, xtmp1, xtmp2, ytmp, ytmp1, ytmp2;

        switch(currentOrientation) {
        case GraphicLib.NORTH:
            px[0] = x + decPoint;
            px[1] = x + width - decPoint;
            xtmp = x + width/2;
            ytmp1 = y + decPoint;
            ytmp2 = y + height - decPoint;
            if (isOrigin()) {
                py[0] = ytmp2;
                py[1] = ytmp2;
                ytmp = ytmp1;
            } else {
                py[0] = ytmp1;
                py[1] = ytmp1;
                ytmp = ytmp2;
            }
            break;
        case GraphicLib.SOUTH:
            px[0] = x + decPoint;
            px[1] = x + width - decPoint;
            xtmp = x + width/2;
            ytmp1 = y + decPoint;
            ytmp2 = y + height - decPoint;
            if (isOrigin()) {
                py[0] = ytmp1;
                py[1] = ytmp1;
                ytmp = ytmp2;
            } else {
                py[0] = ytmp2;
                py[1] = ytmp2;
                ytmp = ytmp1;
            }
            break;
        case GraphicLib.WEST:
            py[0] = y + decPoint;
            py[1] = y + height - decPoint;
            ytmp = y + height / 2;
            xtmp2 = x + decPoint;
            xtmp1 = x + width - decPoint;
            if (isOrigin()) {
                px[0] = xtmp1;
                px[1] = xtmp1;
                xtmp = xtmp2;
            } else {
                px[0] = xtmp2;
                px[1] = xtmp2;
                xtmp = xtmp1;
            }
            break;
        case GraphicLib.EAST:
        default:
            py[0] = y + decPoint;
            py[1] = y + height - decPoint;
            ytmp = y + height / 2;
            xtmp2 = x + decPoint;
            xtmp1 = x + width - decPoint;
            if (isOrigin()) {
                px[0] = xtmp2;
                px[1] = xtmp2;
                xtmp = xtmp1;
            } else {
                px[0] = xtmp1;
                px[1] = xtmp1;
                xtmp = xtmp2;
            }
        }

        px[2] = xtmp;
        py[2] = ytmp;

        if (isLossy) {
            g.setColor(ColorManager.LOSSY);
        }
        g.drawPolygon(px, py, 3);
        g.fillPolygon(px, py, 3);
        g.setColor(c);

        if (isBlocking) {
            switch(currentOrientation) {
            case GraphicLib.NORTH:
            case GraphicLib.SOUTH:
                px[3] = x + decPoint;
                px[4] = x + width - decPoint;
                py[3] = ytmp;
                py[4] = ytmp;
                break;
            case GraphicLib.WEST:
            case GraphicLib.EAST:
                py[3] = y + decPoint;
                py[4] = y + height - decPoint;
                px[3] = xtmp;
                px[4] = xtmp;
                break;
            }
            g.drawLine(px[4], py[4], px[3], py[3]);
        }

        TGComponent tgc = getFather();
        int ft = 10;
        if ((tgc != null) && (tgc instanceof TMLCPrimitiveComponent)) {
            ft = ((TMLCPrimitiveComponent)tgc).getCurrentFontSize();
            //
        }
        //
        int w;
        Font f = g.getFont();
        Font fold = f;

        int si = Math.min(8, (int)((float)ft - 2));
        f = f.deriveFont((float)si);
        g.setFont(f);
        w = g.getFontMetrics().stringWidth(commName);
        if (w < ((int)(width * 1.5))) {
            g.drawString(commName, x, y-1);
        }

        if (checkConf && isOrigin){
            drawConfVerification(g);
        }
        if (checkAuth && !isOrigin){
            drawAuthVerification(g);
        }
        g.setFont(fold);

        drawParticularity(g);
    }

    public abstract void drawParticularity(Graphics g);


    public void drawAuthVerification(Graphics g){
        
        g.drawString(secName, x-xc*2/3, y+yc*2/3);
        Color c = g.getColor();
        Color c1;
        Color c2;
        switch(checkStrongAuthStatus) {
        case 2:
            c1 = Color.green;
            break;
        case 3:
            c1 = Color.red;
            break;
        default:
            c1 = Color.gray;
        }
        switch(checkWeakAuthStatus) {
        case 2:
            c2 = Color.green;
            break;
        case 3:
            c2 = Color.red;
            break;
        default:
            c2= c1;
        }

        g.drawOval(x-xc, y+yc, authovalwidth, authovalheight);
        g.setColor(c1);
        int[] xps = new int[]{x-authxoffset, x-authxoffset, x-authxoffset+authlockwidth};
        int[] yps = new int[]{y+authyoffset, y+authyoffset+authlockheight, y+authyoffset+authlockheight};
        int[] xpw = new int[]{x-authxoffset+authlockwidth, x-authxoffset+authlockwidth, x-authxoffset};
        int[] ypw = new int[]{y+authyoffset+authlockheight, y+authyoffset, y+authyoffset};
        g.fillPolygon(xps, yps,3);

        g.setColor(c2);
        g.fillPolygon(xpw, ypw, 3);
        g.setColor(c);
        g.drawPolygon(xps, yps,3);
        g.drawPolygon(xpw, ypw, 3);
        g.drawString("S", x-authxoffset+1, y+yc+authyoffset);
        g.drawString("W", x-authxoffset+authlockwidth/2, y+yc+authovalheight);
        if (checkStrongAuthStatus ==3){
            g.drawLine(x-authxoffset, y+authyoffset*3/2, x-authxoffset/2, y+authyoffset+yc);
            g.drawLine(x-authxoffset, y+authyoffset+yc, x-authxoffset/2, y+authyoffset*3/2);
        }
        if (checkWeakAuthStatus==3 || checkStrongAuthStatus==3 && checkWeakAuthStatus <2){
            g.drawLine(x-xc*2/3, y+authyoffset, x-xc/3, y+yc+authlockheight);
            g.drawLine(x-xc*2/3, y+yc+authlockheight, x-xc/3, y+authyoffset);
        }
    }


    public void drawConfVerification(Graphics g){


        Color c = g.getColor();
        Color c1;
        switch(checkConfStatus) {
        case 1:
            c1 = Color.gray;
            break;
        case 2:
            c1 = Color.green;
            break;
        case 3:
            c1 = Color.red;
            break;
        default:
            return;
        }
        g.drawString(mappingName, x-conflockwidth*2, y-conflockheight);
        g.drawOval(x-confovalwidth*2, y, confovalwidth, confovalheight);
        g.setColor(c1);
        g.fillRect(x-conflockwidth*3/2, y+conflockheight/2, conflockwidth, conflockheight);
        g.setColor(c);
        g.drawRect(x-conflockwidth*3/2, y+conflockheight/2, conflockwidth, conflockheight);
        if (checkConfStatus==3){
            g.drawLine(x-conflockwidth*2, y, x, y+conflockheight*2);
            g.drawLine(x-conflockwidth*2, y+conflockheight*2, x, y);
        }


        if (!secName.equals("")){
            switch(checkSecConfStatus) {
            case 1:
                c1 = Color.gray;
                break;
            case 2:
                c1 = Color.green;
                break;
            case 3:
                c1 = Color.red;
                break;
            default:
                return;
            }
            g.drawString(secName, x-conflockwidth*2, y+conflockheight*3);
            g.drawOval(x-confovalwidth*2, y+confyoffset, confovalwidth, confovalheight);
            g.setColor(c1);
            g.fillRect(x-conflockwidth*3/2, y+conflockheight/2+confyoffset, conflockwidth, conflockheight);
            g.setColor(c);
            g.drawRect(x-conflockwidth*3/2, y+conflockheight/2+confyoffset, conflockwidth, conflockheight);
        }
    }
    
    

    public void manageMove() {
        //
        if (father != null) {
            //
            Point p = GraphicLib.putPointOnRectangle(x+(width/2), y+(height/2), father.getX(), father.getY(), father.getWidth(), father.getHeight());

            x = p.x - width/2;
            y = p.y - height/2;

            setMoveCd(x, y);

            int orientation = GraphicLib.getCloserOrientation(x+(width/2), y+(height/2), father.getX(), father.getY(), father.getWidth(), father.getHeight());
            if (orientation != currentOrientation) {
                setOrientation(orientation);
            }
        }
    }

    // TGConnecting points ..
    public void setOrientation(int orientation) {
        currentOrientation = orientation;
        double w0, h0;//,w1, h1;

        switch(orientation) {
        case GraphicLib.NORTH:
            w0 = 0.5;
            h0 = 0.0;
            break;
        case GraphicLib.WEST:
            w0 = 0.0;
            h0 = 0.5;
            break;
        case GraphicLib.SOUTH:
            w0 = 0.5;
            h0 = 1.0;
            break;
        case GraphicLib.EAST:
        default:
            w0 = 1.0;
            h0 = 0.5;
        }

        for (int i=0; i<nbConnectingPoint; i++) {
            ((TMLCPortConnectingPoint)(connectingPoint[i])).setW(w0);
            ((TMLCPortConnectingPoint)(connectingPoint[i])).setH(h0);
        }
    }

    public TGComponent isOnOnlyMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        else if(checkAuth && !isOrigin && GraphicLib.isInRectangle(_x, _y, x-authxoffset, y, authxoffset, authlockheight)){
        	return this;
        }
        else if (checkConf && isOrigin && GraphicLib.isInRectangle(_x, _y, x-conflockwidth*3/2, y, conflockwidth*3/2, height)){
        	return this;
        }
        return null;
    }


    //public abstract int getType();

    public void wasSwallowed() {
        myColor = null;
    }

    public void wasUnswallowed() {
        myColor = null;
        setFather(null);
        TDiagramPanel tdp = getTDiagramPanel();
        setCdRectangle(tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY());

    }

    public void resizeWithFather() {
        //
        if ((father != null) && (father instanceof TMLCPrimitiveComponent)) {
            // Too large to fit in the father? -> resize it!
            //resizeToFatherSize();
            //
            setCdRectangle(0-getWidth()/2, father.getWidth() - (getWidth()/2), 0-getHeight()/2, father.getHeight() - (getHeight()/2));
            setMoveCd(x, y);
            oldx = -1;
            oldy = -1;
        }
    }

    public boolean editOndoubleClick(JFrame frame) {
        //
        //String oldValue = valueOCL;
        int oldSample = maxSamples;
        //   int oldWidthSample = widthSamples;


        Vector<String> otherTypes;

        if (getFather() == null) {
            otherTypes = new Vector<String>();
        } else {
            TMLCPrimitiveComponent tgc = (TMLCPrimitiveComponent)(getFather());
            otherTypes = tgc.getAllRecords();
        }
        Vector<TGComponent> refs = new Vector<TGComponent>();
        for (TGComponent req: tdp.getMGUI().getAllRequirements()){
            //
            if (req instanceof AvatarRDRequirement){
                refs.add(req);
            }
        }

        JDialogTMLCompositePort jda = new JDialogTMLCompositePort(commName, typep, list[0], list[1], list[2], list[3], list[4], isOrigin, isFinite, isBlocking, ""+maxSamples, ""+widthSamples, isLossy, lossPercentage, maxNbOfLoss, frame, "Port properties", otherTypes, dataFlowType, associatedEvent, isPrex, isPostex, checkConf, checkAuth, reference, refs);
        // jda.setSize(350, 700);
        GraphicLib.centerOnParent(jda, 350, 700 );
        // jda.show(); // blocked until dialog has been closed
        jda.setVisible( true );
        dataFlowType = jda.getDataFlowType();
        associatedEvent = jda.getAssociatedEvent();
        isPrex = jda.isChannelPrex();
        isPostex = jda.isChannelPostex();

        TraceManager.addDev( "The Data flow type is: " + dataFlowType );
        TraceManager.addDev( "The Associated event is: " + associatedEvent );

        String oldName = getPortName();
        //TraceManager.addDev("old port name : " + oldName);

        if (jda.hasNewData()) {
            try {
                maxSamples = Integer.decode(jda.getMaxSamples()).intValue();
                widthSamples = Integer.decode(jda.getWidthSamples()).intValue();
                if (maxSamples < 1) {
                    maxSamples = oldSample;
                    JOptionPane.showMessageDialog(frame, "Non valid value: " + maxSamples + ": Should be at least 1", "Error", JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
                isOrigin = jda.isOrigin();
                isFinite = jda.isFinite();
                isBlocking = jda.isBlocking();

                /* is port name valid ?
                 * author : minh hiep
                 */
                String s = jda.getParamName();
                //TraceManager.addDev("port name : " + s);

                if ((s != null) && (s.length() > 0)) {
                    // Check whether this name is already in use, or not

                    if (!TAttribute.isAValidId(s, false, true, false)) {
                        JOptionPane.showMessageDialog(frame,
                                "Could not change the name of the port: the new name is not a valid name",
                                "Error",
                                JOptionPane.INFORMATION_MESSAGE);
                        return false;
                    }

                    if (oldName.compareTo(s) != 0) {
                        if (((TMLComponentTaskDiagramPanel) (tdp)).namePrimitivePortInUse(this, s)) {
                            JOptionPane.showMessageDialog(frame,
                                    "Error: the name is already in use",
                                    "Name modification",
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                    setPortName(s);
                    commName = s;
                }

                //setPortName(jda.getParamName());
                //commName = jda.getParamName();
                isLossy = jda.isLossy();
                lossPercentage = jda.getLossPercentage();
                maxNbOfLoss = jda.getMaxNbOfLoss();
                oldTypep = typep;
                typep = jda.getPortType();
                checkConf = jda.checkConf;
                reference = jda.getReference();
                if (checkConf){
                    if (checkConfStatus == NOCHECK){
                        checkConfStatus = TOCHECK;
                    }
                }
                else {
                    if (checkConfStatus != NOCHECK){
                        checkConfStatus = NOCHECK;
                    }
                }
                checkAuth = jda.checkAuth;
                if (checkStrongAuthStatus < 2){
                	checkStrongAuthStatus = 1;
                	checkWeakAuthStatus = 1;
                }
                for(int i=0; i<nbMaxAttribute; i++) {
                    //TraceManager.addDev("Getting string type: " + jda.getStringType(i));
                    list[i].setType(jda.getStringType(i));
                    //TraceManager.addDev("Recorded type: " + list[i].getTypeOther());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Non valid value: " + e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }


        ((TMLComponentTaskDiagramPanel)tdp).updatePorts();


        return true;
    }
    
    public void showTrace(){
    	//Show Result trace
    	if (resTrace==null){
    		return;
    	}
		PipedOutputStream pos = new PipedOutputStream();
        try {
        	PipedInputStream pis = new PipedInputStream(pos, 4096);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(pos));
			String title = "";
			if (isOrigin){	
				title = "Trace for confidentiality property of ";
			}
			else {
				title = "Trace for authenticity property of ";
			}
            JFrameSimulationSDPanel jfssdp = new JFrameSimulationSDPanel(null, tdp.getMGUI(), title + pragma);
            jfssdp.setIconImage(IconManager.img8);
            GraphicLib.centerOnParent(jfssdp, 600, 600);
            jfssdp.setFileReference(new BufferedReader(new InputStreamReader(pis)));
            jfssdp.setVisible(true);
			jfssdp.setLimitEntity(false);
                        //jfssdp.setModalExclusionType(ModalExclusionType
                          //      .APPLICATION_EXCLUDE);
            jfssdp.toFront();

                        TraceManager.addDev("\n--- Trace ---");
            int i = 0;
            for (ProVerifResultTraceStep step : resTrace.getTrace()) {
                TraceManager.addDev("\n--- Trace #" + i + ": " + step.toString());
            	step.describeAsTMLSDTransaction(bw, i);
                i++;
            }
            bw.close();
        } catch (IOException e) {
        	TraceManager.addDev("Error when writing trace step SD transaction");
        } finally {
        	try {
        		pos.close();
            } catch (IOException ignored) {
        	}
        }

    }

    protected String translateExtraParam() {
        TType a;
        //String val = "";
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Prop commName=\"");
        sb.append(commName);
        sb.append("\" commType=\"" + typep);
        sb.append("\" origin=\"");
        if (isOrigin) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("\" finite=\"");
        if (isFinite) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("\" blocking=\"");
        if (isBlocking) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("\" maxSamples=\"" + maxSamples);
        sb.append("\" widthSamples=\"" + widthSamples);
        sb.append("\" isLossy=\"" + isLossy);
        sb.append("\" isPrex=\"" + isPrex);
        sb.append("\" isPostex=\"" + isPostex);
        sb.append("\" lossPercentage=\"" + lossPercentage);
        sb.append("\" maxNbOfLoss=\"" + maxNbOfLoss);
        sb.append("\" dataFlowType=\"" + dataFlowType);
        sb.append("\" associatedEvent=\"" + associatedEvent);
        sb.append("\" checkConf=\"" + checkConf);
        sb.append("\" checkConfStatus=\"" + checkConfStatus);
        sb.append("\" checkAuth=\"" + checkAuth);
        sb.append("\" checkWeakAuthStatus=\"" + checkWeakAuthStatus);
        sb.append("\" checkStrongAuthStatus=\"" + checkStrongAuthStatus);
        sb.append("\" />\n");
        for(int i=0; i<nbMaxAttribute; i++) {
            //
            a = list[i];
            //
            //val = val + a + "\n";
            sb.append("<Type");
            sb.append(" type=\"");
            sb.append(a.getType());
            sb.append("\" typeOther=\"");
            sb.append(a.getTypeOther());
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            //int access;
            int typeAtt;
            String typeOther;
            //String id, valueAtt;

            int nbAttribute = 0;

            //
            //

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if ((elt.getTagName().equals("Type")) && (nbAttribute < nbMaxAttribute)) {
                                //
                                typeAtt = Integer.decode(elt.getAttribute("type")).intValue();
                                try {
                                    typeOther = elt.getAttribute("typeOther");
                                } catch (Exception e) {
                                    typeOther = "";
                                }

                                TType ta = new TType(typeAtt, typeOther);
                                list[nbAttribute] = ta;
                                nbAttribute ++;

                            }

                            if (elt.getTagName().equals("Prop")) {
                                commName = elt.getAttribute("commName");
                                try {
                                    //
                                    typep = Integer.decode(elt.getAttribute("commType")).intValue();
                                    //
                                    //
                                    maxSamples = Integer.decode(elt.getAttribute("maxSamples")).intValue();
                                    //
                                    widthSamples = Integer.decode(elt.getAttribute("widthSamples")).intValue();

                                } catch (Exception e) {

                                }

                                try {
                                    lossPercentage = Integer.decode(elt.getAttribute("lossPercentage")).intValue();
                                    maxNbOfLoss = Integer.decode(elt.getAttribute("maxNbOfLoss")).intValue();
                                    dataFlowType = elt.getAttribute("dataFlowType");
                                    associatedEvent = elt.getAttribute("associatedEvent");
                                    checkConf = (elt.getAttribute("checkConf").compareTo("true")==0);
                                    if (checkConf){
                                        checkConfStatus=TOCHECK;
                                    }
                                    checkAuth = (elt.getAttribute("checkAuth").compareTo("true")==0);
                                    isLossy = (elt.getAttribute("isLossy").compareTo("true") ==0);
                                    isPrex = (elt.getAttribute("isPrex").compareTo("true") ==0);
                                    isPostex = (elt.getAttribute("isPostex").compareTo("true") ==0);
                                } catch (Exception e) {
                                    lossPercentage = 0;
                                    maxNbOfLoss = -1;
                                    isLossy = false;
                                }

                                try {
                                    isBlocking = (elt.getAttribute("blocking").compareTo("true") ==0);
                                    isOrigin = (elt.getAttribute("origin").compareTo("true") ==0);
                                    isFinite = (elt.getAttribute("finite").compareTo("true") ==0);

                                } catch (Exception e) {}

                            }

                            makeValue();
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public void makeValue() {
        value = getPortTypeName() + " " + getPortName();
    }

    public String getPortName() {
        return commName;
    }

    public int getPortType() {
        return typep;
    }

    public String getPortTypeName() {
        switch(typep) {
        case 0:
            return "Channel";
        case 1:
            return "Event";
        case 2:
        default:
            return "Request";
        }
    }

    public boolean isBlocking() {
        return isBlocking;
    }

    public boolean isFinite() {
        return isFinite;
    }

    public int getMax() {
        return maxSamples;
    }

    public int getSize() {
        return widthSamples;
    }

    public boolean isOrigin() {
        return isOrigin;
    }

    public int getNbMaxAttribute() {
        return nbMaxAttribute;
    }

    public TType getParamAt(int index) {
        return list[index];
    }
    public void setParam(int index, TType t){
        list[index] = t;
    }

    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_PORT_TMLC;
    }


    public String getAttributes() {
        String attr = "";
        if (isOrigin()) {
            attr += "out ";
        } else {
            attr += "in ";
        }
        attr += getPortTypeName() + ": ";
        attr += getPortName() + "\n";
        /*if (isOrigin()) {
          attr += "Origin\n"
          } else {
          attr = += "Destination\n";
          }*/

        // Channel
        if (typep == 0) {
            if (!isBlocking()) {
                attr += "N";
            }
            attr += "B";
            if (isOrigin()) {
                attr += "W\n";
                attr += "Width (in B): " + getSize() + "\n";
                if (isFinite()) {
                    attr += "Max samples: " + getNbMaxAttribute() + "\n";
                } else {
                    attr += "Infinite\n";
                }
            } else {
                attr += "R\n";
            }

            // Event and Request
        } else {
            attr += "(";
            //  TType type1;
            for(int i=0; i<nbMaxAttribute; i++) {
                if (i!=0) {
                    attr += ",";
                }
                attr += TType.getStringType(list[i].getType());
            }
            attr += ")\n";
            if (typep == 1) {
                if (isOrigin()) {
                    if (!isFinite()) {
                        attr += "Infinite FIFO\n";
                    } else {
                        if (isBlocking()) {
                            attr += "Blocking ";
                        } else {
                            attr += "Non-blocking ";
                        }
                        attr += "finite FIFO: " + getMax() + "\n";
                    }
                }
            }
        }

        if (conflict) {
            attr += "Error in path=" + conflictMessage;
        }

        return attr;
    }

    public boolean isLossy() {
        return isLossy && isOrigin;
    }

    public int getLossPercentage() {
        return lossPercentage;
    }

    public int getMaxNbOfLoss() {
        return maxNbOfLoss;
    }

    public boolean getConflict() {
        return conflict;
    }

    public void setConflict(boolean _conflict, String _msg) {
        conflict = _conflict;
        myColor = null;
        conflictMessage = _msg;
        calculatePortColor();
    }

    public void calculatePortColor() {
        if (conflict) {
            myColor = Color.red;
        } else {
            if (typep == 0) {
                myColor = ColorManager.TML_PORT_CHANNEL;
            } else if (typep == 1) {
                myColor = ColorManager.TML_PORT_EVENT;
            } else {
                myColor = ColorManager.TML_PORT_REQUEST;
            }
        }
    }

    public String getDataFlowType()     {
        return dataFlowType;
    }

    public boolean isPrex()     {
        return isPrex;
    }

    public boolean isPostex()   {
        return isPostex;
    }

    public String getAssociatedEvent()  {
        return associatedEvent;
    }

    public boolean hasSameParametersThan(TMLCPrimitivePort _p) {
        for(int i=0; i<5; i++) {
            if (!(getParamAt(i).equals(_p.getParamAt(i)))) {
                return false;
            }
        }
        return true;
    }
    
    public void setResultTrace(ProVerifResultTrace trace){
    	resTrace = trace;
    }

	public void setPragmaString(String str){
		pragma=str;
	}

    public void setPortName(String s) {
        for (TURTLEPanel tp : tdp.getMainGUI().tabs) {
            for (TDiagramPanel t : tp.getPanels()) {
                for (TGComponent t2 : t.getComponentList()) {
                    if (t2 instanceof TMLArchiCPNode) {
                        TMLArchiCPNode tacn = (TMLArchiCPNode) t2;
                        for (TGComponent tgc : tacn.getRecursiveAllInternalComponent()) {
                            if (tgc instanceof TMLArchiPortArtifact) {
                                TMLArchiPortArtifact tapi = (TMLArchiPortArtifact) tgc;
                                String tmp = tapi.getValue().replaceAll("(?i)" + commName + "$", s);
                                tapi.setValue(tmp);
                            }
                        }
                    }
                }
            }
        }

        if ( (father != null) && (father instanceof TMLCPrimitiveComponent)) {
            String name = father.getValue();
            //TraceManager.addDev("Looking for diagram with AD name=" + name + " of class=" + father.getClass());
            TURTLEPanel tp = tdp.getMainGUI().getCurrentTURTLEPanel();
            for (TDiagramPanel t : tp.getPanels()) {
                if (t.getName().compareTo(name) == 0) {
                    //TraceManager.addDev("Renaming operators in AD=" + name);
                    for (TGComponent t2 : t.getComponentList()) {
                        if (t2 instanceof TMLADWriteChannel) {
                            TMLADWriteChannel twc = (TMLADWriteChannel) t2;
                            if (twc.getChannelName().equals(commName))
                                twc.setChannelName(s);
                        }

                        if (t2 instanceof TMLADReadChannel) {
                            TMLADReadChannel trc = (TMLADReadChannel) t2;
                            if (trc.getChannelName().equals(commName))
                                trc.setChannelName(s);
                        }


                        if (t2 instanceof TMLADSendEvent) {
                            TMLADSendEvent tse = (TMLADSendEvent) t2;
                            //TraceManager.addDev("Send event with event=" + tse.getEventName() + " vs " + commName);
                            if (tse.getEventName().equals(commName))
                                tse.setEventName(s);
                        }

                        if (t2 instanceof TMLADSendRequest) {
                            TMLADSendRequest tsr = (TMLADSendRequest) t2;
                            if (tsr.getRequestName().equals(commName))
                                tsr.setRequestName(s);
                        }

                        if (t2 instanceof TMLADWaitEvent) {
                            TMLADWaitEvent twe = (TMLADWaitEvent) t2;
                            if (twe.getEventName().equals(commName))
                                twe.setEventName(s);
                        }

                        if (t2 instanceof TMLADNotifiedEvent) {
                            TMLADNotifiedEvent tne = (TMLADNotifiedEvent) t2;
                            if (tne.getEventName().equals(commName))
                                tne.setEventName(s);
                        }
                    }
                    t.repaint();
                }
            }
        }
    }

    //#issue 82
    public String getPortNameFromValue(String myValue) {
        String s = "";
        String string[] = myValue.split("\\s");
        for (int i = 1; i < string.length; i++) {
            s = s + string[i];
        }
        return s;
    }

    //#issue 82
    public int getPortTypeFromValue(String myValue) {
        String typePortName = myValue.split("\\s")[0];
        int typePort = 0;
        if (typePortName.equals("Channel"))
            typePort = 0;
        if (typePortName.equals("Event"))
            typePort = 1;
        if (typePortName.equals("Request"))
            typePort = 2;
        return typePort;
    }



}
