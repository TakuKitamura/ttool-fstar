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
   * Class TMLCPrimitivePort
   * Primitive port. To be used in TML component task diagrams
   * Creation: 12/03/2008
   * @version 1.0 12/03/2008
   * @author Ludovic APVRILLE
   * @see
   */

package ui.tmlcompd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

import tmltranslator.*;

public abstract class TMLCPrimitivePort extends TGCScalableWithInternalComponent implements SwallowedTGComponent, WithAttributes {
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
    protected boolean isBlocking = false;
    protected boolean isOrigin = true;
    protected int typep = 0;
    protected int oldTypep = typep;
    protected String commName;

    protected boolean isLossy;
    protected boolean isPostex = false;
    protected boolean isPrex = false;
    protected int lossPercentage;
    protected int maxNbOfLoss; //-1 means no max
    
    public int checkStatus;
    public boolean checkConf;
    public static int NOCHECK= 0;
    public static int TOCHECK = 1;
    public static int CHECKED_CONF = 2;
    public static int CHECKED_UNCONF = 3;
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
        commName = "comm";
        //value = "MyName";
        makeValue();
        setName("Primitive port");
	checkStatus= NOCHECK;
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

        //System.out.println("NbTGConnectingPoint=" + nbConnectingPoint);
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
            //System.out.println("Got ft");
        }
        //System.out.println("ft=" + ft);
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

        // Name


        // Type
        /*String lname;
          if ((si + 2) < height) {
          switch(typep) {
          case 0:
          lname = "c";
          break;
          case 1:
          lname = "e";
          break;
          case 2:
          default:
          lname = "r";
          }
          w = g.getFontMetrics().stringWidth(lname);
          if (w < (width / 2)) {
          g.drawString(lname, x+width - w - 1, y+(int)(si)-2);
          }
          }*/
	drawVerification(g);
        g.setFont(fold);
	
        drawParticularity(g);
    }

    public abstract void drawParticularity(Graphics g);

    public void drawVerification(Graphics g){
        Color c = g.getColor();
        Color c1;
        switch(checkStatus) {
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
	g.drawString(mappingName, x, y);
        g.drawOval(x+6, y-10, 6, 9);
        g.setColor(c1);
        g.fillRect(x+4, y-7, 9, 7);
        g.setColor(c);
        g.drawRect(x+4, y-7, 9, 7);

    }

    public void manageMove() {
        //System.out.println("Manage move!");
        if (father != null) {
            //System.out.println("Has a father!");
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
        double w0, h0,w1, h1;

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
        //System.out.println("Resize port with father");
        if ((father != null) && (father instanceof TMLCPrimitiveComponent)) {
            // Too large to fit in the father? -> resize it!
            //resizeToFatherSize();
            //System.out.println("Setcd rectangle");
            setCdRectangle(0-getWidth()/2, father.getWidth() - (getWidth()/2), 0-getHeight()/2, father.getHeight() - (getHeight()/2));
            setMoveCd(x, y);
            oldx = -1;
            oldy = -1;
        }
    }

    public boolean editOndoubleClick(JFrame frame) {
        //System.out.println("Double click!");
        //String oldValue = valueOCL;
        int oldSample = maxSamples;
        int oldWidthSample = widthSamples;


        Vector<String> otherTypes;

        if (getFather() == null) {
            otherTypes = new Vector<String>();
        } else {
            TMLCPrimitiveComponent tgc = (TMLCPrimitiveComponent)(getFather());
            otherTypes = tgc.getAllRecords();
        }

        JDialogTMLCompositePort jda = new JDialogTMLCompositePort(commName, typep, list[0], list[1], list[2], list[3], list[4], isOrigin, isFinite, isBlocking, ""+maxSamples, ""+widthSamples, isLossy, lossPercentage, maxNbOfLoss, frame, "Port properties", otherTypes, dataFlowType, associatedEvent, isPrex, isPostex, checkConf);
        jda.setSize(350, 700);
        GraphicLib.centerOnParent(jda);
        jda.show(); // blocked until dialog has been closed
        dataFlowType = jda.getDataFlowType();
        associatedEvent = jda.getAssociatedEvent();
        isPrex = jda.isChannelPrex();
        isPostex = jda.isChannelPostex();
        TraceManager.addDev( "The Data flow type is: " + dataFlowType );
        TraceManager.addDev( "The Associated event is: " + associatedEvent );

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
                commName = jda.getParamName();
                isLossy = jda.isLossy();
                lossPercentage = jda.getLossPercentage();
                maxNbOfLoss = jda.getMaxNbOfLoss();
                oldTypep = typep;
                typep = jda.getPortType();
		checkConf = jda.checkConf;
		if (checkConf){
		    if (checkStatus==NOCHECK){
			checkStatus=TOCHECK;
		    }
		}
		else {
		    if (checkStatus!=NOCHECK){
			checkStatus=NOCHECK;
		    }
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
	sb.append("\" checkStatus=\"" + checkStatus);
        sb.append("\" />\n");
        for(int i=0; i<nbMaxAttribute; i++) {
            //System.out.println("Attribute:" + i);
            a = list[i];
            //System.out.println("Attribute:" + i + " = " + a.getId());
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

            //System.out.println("Loading attributes");
            //System.out.println(nl.toString());

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if ((elt.getTagName().equals("Type")) && (nbAttribute < nbMaxAttribute)) {
                                //System.out.println("Analyzing attribute");
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
                                    //System.out.println("Setting type");
                                    typep = Integer.decode(elt.getAttribute("commType")).intValue();
                                    //System.out.println("Setting type type=" + type);
                                    //System.out.println("Setting max");
                                    maxSamples = Integer.decode(elt.getAttribute("maxSamples")).intValue();
                                    //System.out.println("Setting width");
                                    widthSamples = Integer.decode(elt.getAttribute("widthSamples")).intValue();
                                } catch (Exception e) {

                                }

                                try {
                                    lossPercentage = Integer.decode(elt.getAttribute("lossPercentage")).intValue();
                                    maxNbOfLoss = Integer.decode(elt.getAttribute("maxNbOfLoss")).intValue();
                                    dataFlowType = elt.getAttribute("dataFlowType");
                                    associatedEvent = elt.getAttribute("associatedEvent");
				    checkConf = (elt.getAttribute("checkConf").compareTo("true")==0);
				    checkStatus = Integer.valueOf(elt.getAttribute("checkStatus"));
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
            TType type1;
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
	
	public String getDataFlowType()	{
		return dataFlowType;
	}

	public boolean isPrex()	{
		return isPrex;
	}

	public boolean isPostex()	{
		return isPostex;
	}

	public String getAssociatedEvent()	{
		return associatedEvent;
	}

}
