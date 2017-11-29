/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 *
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paritech.fr
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




package ui.sd2;

import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogSDInstance;

import javax.swing.*;
import java.awt.*;



/**
 * Class SDInstance
 * Fixed duration operator. To be used in sequence diagrams
 * Creation: 04/10/2004
 * @version 1.1 10/06/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class SDInstance extends TGCScalableWithInternalComponent implements SwallowTGComponent, SpecificActionAfterAdd {
    //private int lineLength = 5;
    //private int textX, textY;
    private int spacePt = 10;
    private int wText = 10, hText = 15;
    //private int increaseSlice = 250;
    private boolean isActor;
    private static int heightActor = 30;
    private static int widthActor = 16;


    public SDInstance(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = (int)(10 * tdp.getZoom());
        height = (int)(500 * tdp.getZoom());
        minWidth = (int)(10 * tdp.getZoom());
        maxWidth = (int)(10 * tdp.getZoom());
        minHeight = (int)(250 * tdp.getZoom());
        maxHeight = (int)(1500 * tdp.getZoom());
        //TraceManager.addDev("Init tgc= " + this + " minHeight=" + minHeight);
        //TraceManager.addDev("Init tgc= " + this + " maxHeight=" + maxHeight);
        oldScaleFactor = tdp.getZoom();


        //makeTGConnectingPoints();
        //addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        //makePortMessage();

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        value = "Instance name";
        name = "instance";
        isActor = false;

        myImageIcon = IconManager.imgic500;
    }

    public void internalDrawing(Graphics g) {

        if( !tdp.isScaled() ) {
            wText  = g.getFontMetrics().stringWidth(name);
            hText = g.getFontMetrics().getHeight();
        }
        g.drawString( name, x - (wText / 2) + width/2, y - 3 );
        g.drawLine(x - (wText / 2) + width/2, y-2, x + (wText / 2) + width/2, y-2);
        g.drawLine(x+(width/2), y, x+(width/2), y +height);

        if( isActor ) {
            int xtmp = x + (width-widthActor) / 2;
            int ytmp = y-hText;
            // Head
            g.drawOval(xtmp+(widthActor/4)-1, ytmp-heightActor, 2+widthActor/2, 2+widthActor/2);
            //Body
            g.drawLine(xtmp+widthActor/2, ytmp-heightActor/3, xtmp+widthActor/2, ytmp-(2*heightActor)/3);
            //Arms
            g.drawLine(xtmp, ytmp-(heightActor/2) - 2, xtmp+widthActor, ytmp-(heightActor/2) - 2);
            //Left leg
            g.drawLine(xtmp+widthActor, ytmp, xtmp+widthActor/2, ytmp-heightActor/3);
            //right leg
            g.drawLine(xtmp, ytmp, xtmp+widthActor/2, ytmp-heightActor/3);
        }
    }


    public TGComponent isOnOnlyMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }

        if (GraphicLib.isInRectangle(_x, _y, x + (width/2) - (wText/2) , y-hText, wText, hText)) {
            return this;
        }

        if (isActor) {
            if (GraphicLib.isInRectangle(_x, _y, x + (width-widthActor) / 2, y-heightActor-hText, widthActor, heightActor)) {
                return this;
            }
        }
        return null;
    }

    public int getMyCurrentMinX() {
        return Math.min(x + (width/2) - (wText/2), x);

    }

    public int getMyCurrentMaxX() {
        return Math.max(x + (width/2) + (wText/2), x + width);
    }

    public int getMyCurrentMinY() {
        return Math.min(y-hText-heightActor, y);
    }

    public String getInstanceName() {
        return getValue();
    }

    public int getType() {
        return TGComponentManager.SDZV_INSTANCE;
    }

    public int spacePt() {
        return (int)(Math.floor(spacePt*tdp.getZoom()));
    }

    public double spacePtDouble() {
        return spacePt*tdp.getZoom();
    }

    public void rescale(double scaleFactor){
        //TraceManager.addDev("my rescale");

        super.rescale(scaleFactor);

        // update TG Connecting Points
        //int yh = spacePt();
        /*for(int i=0; i<nbConnectingPoint; i++, yh+=spacePt()) {
          connectingPoint[i].setCdX(width/2);
          connectingPoint[i].setCdY(yh);
          }*/

        //height = Math.max(getMinHeightSize(), height);
        hasBeenResized();
    }

    public void computeMinHeight() {
        height = Math.max(getMinHeightSize(), height);
    }

    public int getNbOfConnectingPoints() {
        return 100;
        //return (int)(((height - (2 * spacePt())) / spacePt()));
    }


    public void makePortMessage() {
        int nbOfInternal = 30;

        for(int i=0; i<nbOfInternal; i ++) {
            double ratio = ((i)/(double)(nbOfInternal));//+(spacePt*tdp.getZoom()/height);
            SDPortForMessage port = new SDPortForMessage(100, 200+ (int)(y + ratio*height), tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);

            //tdp.addComponent(port, x+width/2, y+100, true, true);

            //TraceManager.addDev("Adding internal components");
            if (!addSwallowedTGComponent(port, x+width/2, (int)(5*spacePt*tdp.getZoom()) + (int)(y + ratio*height))) {
                TraceManager.addDev("Adding PortForMessage failed");
            } else {
                port.wasSwallowed();
            }
            port.setMoveCd(0, (int)(10*spacePt*tdp.getZoom()) + (int)(ratio*height), false);
            //TraceManager.addDev("Nb of internal components:" + nbInternalTGComponent);
        }
    }

    /*private void makeTGConnectingPoints() {
    //TraceManager.addDev("Making TG connecting points of " + name);
    nbConnectingPoint = getNbOfConnectingPoints();
    connectingPoint = new TGConnectingPoint[nbConnectingPoint];

    //int yh = spacePt();
    double div = 1.0/height - (nbConnectingPoint);
    //TraceManager.addDev("Div=" + div);

    for(int i=0; i<nbConnectingPoint; i ++) {
    double ratio = ((i)/(double)(nbConnectingPoint));//+(spacePt*tdp.getZoom()/height);
    //TraceManager.addDev("Ratio=" + ratio);
    connectingPoint[i] = new TGConnectingPointMessageSD(this, 0, 0, true, true, 0.5, ratio);
    }

    }*/

    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = name;

        JDialogSDInstance jdsdi = new JDialogSDInstance(frame, name, isActor, "Instance attributes");
        //   jdsdi.setSize(300, 250);
        GraphicLib.centerOnParent(jdsdi, 300, 250);
        jdsdi.setVisible( true ); // blocked until dialog has been closed


        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }

        if (jdsdi.hasBeenUpdated()) {
            isActor = jdsdi.isAnActor();
            String s = jdsdi.getInstanceName();

            if (s != null) {
                s = s.trim();
            }

            if((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
                if(!TAttribute.isAValidId(s, false, false)) {
                    JOptionPane.showMessageDialog( frame,
                                                   "Could not change the name of the instance: the new name is not a valid name",
                                                   "Error", JOptionPane.INFORMATION_MESSAGE );
                    return false;
                }
                setName(s);
                return true;
            }
        }
        return false;
    }

    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        if ((tgc instanceof ui.sd2.SDAbsoluteTimeConstraint) || (tgc instanceof ui.sd2.SDRelativeTimeConstraint) || (tgc instanceof ui.sd2.SDTimeInterval)){
            return true;
        }

        if ((tgc instanceof ui.sd2.SDActionState) || (tgc instanceof ui.sd2.SDCoregion)|| (tgc instanceof ui.sd2.SDGuard)) {
            return true;
        }

        if (tgc instanceof ui.sd2.SDTimerSetting) {
            return true;
        }

        if (tgc instanceof ui.sd2.SDTimerExpiration) {
            return true;
        }

        if (tgc instanceof ui.sd2.SDTimerCancellation) {
            return true;
        }

        return tgc instanceof SDPortForMessage;


    }

    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        //TraceManager.addDev("Element 0" + tgc + " added to SDInstance");
        if (!acceptSwallowedTGComponent(tgc)) {
            return false;
        }

        //TraceManager.addDev("Element 1" + tgc + " added to SDInstance");

        //System.out.println("Add swallow component");
        // Choose its position
        int realY = Math.max(y, getY() + spacePt());
        realY = Math.min(realY, getY() + height + spacePt());
        int realX = tgc.getX();


        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);

        // Set its coordinates
        if ((tgc instanceof SDAbsoluteTimeConstraint) || (tgc instanceof SDRelativeTimeConstraint) || (tgc instanceof SDTimeInterval)){
            realX = getX() + (width/2) - tgc.getWidth();
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //tgc.setCdRectangle(0, -50, 0, 50);
            tgc.setCd(realX, realY);
        }

        if ((tgc instanceof SDActionState) || (tgc instanceof SDCoregion)|| (tgc instanceof SDGuard) || (tgc instanceof SDPortForMessage)) {
            realX = getX()+(width/2);
            //tgc.setCdRectangle((width/2), (width/2), spacePt, height-spacePt-tgc.getHeight());
            tgc.setCd(realX, realY);
        }

        if (tgc instanceof SDTimerSetting) {
            realX = getX()+(width/2) + ((SDTimerSetting)tgc).getLineLength() - tgc.getWidth()/2;
            //tgc.setCdRectangle((width/2) + ((SDTimerSetting)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerSetting)tgc).getLineLength() - tgc.getWidth()/2, spacePt - tgc.getHeight()/2, height-spacePt-tgc.getHeight() / 2);
            tgc.setCd(realX, realY);
        }

        if (tgc instanceof SDTimerExpiration) {
            realX = getX()+(width/2) + ((SDTimerExpiration)tgc).getLineLength() - tgc.getWidth()/2;
            //tgc.setCdRectangle((width/2) + ((SDTimerExpiration)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerExpiration)tgc).getLineLength() - tgc.getWidth()/2, spacePt - tgc.getHeight()/2, height-spacePt-tgc.getHeight() / 2);
            tgc.setCd(realX, realY);
        }

        if (tgc instanceof SDTimerCancellation) {
            realX = getX()+(width/2) + ((SDTimerCancellation)tgc).getLineLength() - tgc.getWidth()/2;
            //tgc.setCdRectangle((width/2) + ((SDTimerCancellation)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerCancellation)tgc).getLineLength() - tgc.getWidth()/2, spacePt - tgc.getHeight()/2, height-spacePt-tgc.getHeight() / 2);
            tgc.setCd(realX, realY);
        }

        setCDRectangleOfSwallowed(tgc);

        // coregions -> in the middle !

        // else unknown

        //add it
        addInternalComponent(tgc, 0);
        TraceManager.addDev("Element " + tgc + " added to SDInstance");

        return true;
    }

    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }

    public boolean isInCoregion(int yy) {
        TGComponent tgc;
        for(int i=0; i<nbInternalTGComponent; i++) {
            tgc = tgcomponent[i];
            if (tgc instanceof SDCoregion) {
                //System.out.println("Coregion found from " + tgc.getY() + " to " + (tgc.getY() + tgc.getHeight()));
                if (tgc.isOnMe(tgc.getX(), yy) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInSameCoregion(int y1, int y2) {
        //System.out.println("Is in same coregion y1=" + y1 + " y2=" + y2);
        int y11 = Math.min(y1, y2);
        int y22 = Math.max(y1, y2) +1;
        for(int i=y11; i<y22; i++) {
            if (!isInCoregion(i)) {
                //System.out.println("No!");
                return false;
            }
        }
        //System.out.println("YES !");
        return true;
    }

    public boolean inSameCoregion(TGComponent tgc1, TGComponent tgc2) {
        TGComponent tgctmp;
        if (tgc2.getY() < tgc1.getY()) {
            tgctmp = tgc1;
            tgc1 = tgc2;
            tgc2 = tgctmp;
        }

        // each y between the two components should be in a coregion
        for(int i=tgc1.getY(); i<tgc2.getY()+1; i++) {
            if (!isInCoregion(i)) {
                return false;
            }
        }

        return true;

    }

    // previous in the sense of with y the closer and before
    public TGComponent getPreviousTGComponent(TGComponent tgcToAnalyse) {
        int close = Integer.MAX_VALUE;
        TGComponent tgc;
        TGComponent tgcfound = null;
        int diff;

        for(int i=0; i<nbInternalTGComponent; i++) {
            tgc = tgcomponent[i];
            if (tgc != tgcToAnalyse) {
                diff = tgcToAnalyse.getY() - tgc.getY();
                if ((diff > 0) && (diff < close)) {
                    if (!inSameCoregion(tgcToAnalyse, tgc)) {
                        close = diff;
                        tgcfound = tgc;
                    }

                }
            }
        }

        return tgcfound;
    }

    public TGComponent getTGComponentActionCloserTo(TGComponent tgc) {
        /* action : message send, message receive, other ? */
        /* timers ? */
        /* now: only message! */

        return ((SequenceDiagramPanel)tdp).messageActionCloserTo(tgc, this);

    }



    public Point modifyInHeight(int newy) {
        ((SequenceDiagramPanel)(tdp)).updateAllInstanceMinMaxSize();
        return super.modifyInHeight(newy);
    }


    // Used when there is a rescale or a component added
    public int getMinHeightSize() {
        int msize = (int)(250*tdp.getZoom());
        int i;

        /*for(i=0; i<connectingPoint.length ; i++) {
          if (!(connectingPoint[i].isFree())) {
          TraceManager.addDev("Found a non free connecting point y=" + connectingPoint[i].getY() + " heightOfPt=" + (connectingPoint[i].getY() - y + spacePt()));
          msize = Math.max(msize, connectingPoint[i].getY() - y + spacePt() );
          }
          }*/

        for(i=0; i<nbInternalTGComponent ; i++) {
            msize = Math.max(msize, tgcomponent[i].getY() + tgcomponent[i].getHeight()- y + spacePt());
        }

        TraceManager.addDev("Min height size of " + name + " =" + msize + " height=" + height);

        return msize;
    }

    // Method called when there is a resize order
    public void setUserResize(int desired_x, int desired_y, int desired_width, int desired_height) {
        //System.out.println("newx = " + desired_x + " newy = " + desired_y + " minWidth = " + minWidth);

        setCd(desired_x, desired_y);
        actionOnUserResize(desired_width, desired_height);
        ((SequenceDiagramPanel)tdp).instanceHasBeenResized(this,  desired_width, desired_height);
    }


    public void hasBeenResized(){
        /*TraceManager.addDev("Has been resized: " + name + " height=" + height);
          int i;

          for (int k=0; k<nbConnectingPoint; k++) {
          if (!connectingPoint[k].isFree()) {
          TraceManager.addDev("Non free TG point in " + name);
          }
          }

          TGConnectingPoint [] connectingPointTmp = connectingPoint;
          makeTGConnectingPoints();
          //nbConnectingPoint = getNbOfConnectingPoints();
          //connectingPoint = new TGConnectingPoint[nbConnectingPoint];
          for(i=0; i<Math.min(connectingPointTmp.length, connectingPoint.length); i++) {
          connectingPoint[i] = connectingPointTmp[i];

          if (!connectingPoint[i].isFree()) {
          TraceManager.addDev("Non free point in " + name);
          }
          }

          for (int j=nbConnectingPoint; j<connectingPointTmp.length; j++) {
          if (!connectingPointTmp[j].isFree()) {
          TraceManager.addDev("Non free TG point");
          }
          }*/

        // Increase tdp if necessary?

        // Reposition each swallowed component
        for(int i=0; i<nbInternalTGComponent ; i++) {
            setCDRectangleOfSwallowed(tgcomponent[i]);
        }
    }

    private void setCDRectangleOfSwallowed(TGComponent tgc) {
        if ((tgc instanceof SDAbsoluteTimeConstraint) || (tgc instanceof SDRelativeTimeConstraint)){
            tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt(), height- spacePt());
        }

        if ((tgc instanceof SDActionState) || (tgc instanceof SDGuard) || (tgc instanceof SDCoregion) || (tgc instanceof SDTimeInterval) || (tgc instanceof SDPortForMessage)) {
            tgc.setCdRectangle((width/2), (width/2), spacePt(), height- spacePt() -tgc.getHeight());
        }

        if (tgc instanceof SDTimerSetting) {
            tgc.setCdRectangle((width/2) + ((SDTimerSetting)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerSetting)tgc).getLineLength() - tgc.getWidth()/2, spacePt() - tgc.getHeight()/2, height- spacePt() -tgc.getHeight() / 2);
        }

        if (tgc instanceof SDTimerExpiration) {
            tgc.setCdRectangle((width/2) + ((SDTimerExpiration)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerExpiration)tgc).getLineLength() - tgc.getWidth()/2, spacePt() - tgc.getHeight()/2, height- spacePt() -tgc.getHeight() / 2);
        }

        if (tgc instanceof SDTimerCancellation) {
            tgc.setCdRectangle((width/2) + ((SDTimerCancellation)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerCancellation)tgc).getLineLength() - tgc.getWidth()/2, spacePt() - tgc.getHeight()/2, height- spacePt() -tgc.getHeight() / 2);
        }
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Actor data=\"");
        sb.append(""+isActor);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;

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
                            if (elt.getTagName().equals("Actor")) {
                                if (elt.getAttribute("data").compareTo("true") == 0) {
                                    isActor = true;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public void setActor(boolean b) {
        isActor = b;
    }
    

    public void specificActionAfterAdd() {
        makePortMessage();
    }

}
