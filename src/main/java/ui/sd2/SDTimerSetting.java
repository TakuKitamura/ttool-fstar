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

package ui.sd2;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogTimeInterval;

import javax.swing.*;
import java.awt.*;

/**
 * Class SDTimerSetting Setting of a timer for a given duration. To be used in
 * Sequence Diagrams. Creation: 06/10/2004
 * 
 * @version 1.0 06/10/2004
 * @author Ludovic APVRILLE
 */
public class SDTimerSetting extends TGCScalableWithoutInternalComponent implements SwallowedTGComponent {
    private String timer = "myTimer";
    private String duration = "10";
    private int widthValue, heightValue;

    public SDTimerSetting(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father,
            TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = (int) (30 * tdp.getZoom());
        height = (int) (25 * tdp.getZoom());
        oldScaleFactor = tdp.getZoom();

        nbConnectingPoint = 0;
        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;

        name = "setting timer";
        makeValue();
        widthValue = 0;
        heightValue = 0;

        myImageIcon = IconManager.imgic514;
    }

    public void internalDrawing(Graphics g) {
        if (!tdp.isScaled()) {
            widthValue = g.getFontMetrics().stringWidth(value);
            heightValue = g.getFontMetrics().getHeight();
        }

        g.drawString(value, x + width, y + height / 2 + 3);

        g.drawLine(x + width / 2, y, x + width, y + height);
        g.drawLine(x + width / 2, y, x + width, y);
        g.drawLine(x + width / 2, y + height, x + width, y + height);
        g.drawLine(x + width, y, x + width / 2, y + height);

        g.drawLine(x, y + height / 2, x + width / 2, y + height / 2);
    }

    public int getYOrder() {
        return y + height / 2;
    }

    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }

        /* text */
        if (GraphicLib.isInRectangle(_x, _y, x + width, y + height / 2 - heightValue + 3, widthValue, heightValue)) {
            return this;
        }

        /* line */
        if (GraphicLib.isInRectangle(_x, _y, x, y + height / 2 - 2, width / 2, 4)) {
            return this;
        }
        return null;
    }

    public int getMyCurrentMaxX() {
        return x + width + widthValue;
    }

    public int getType() {
        return TGComponentManager.SDZV_TIMER_SETTING;
    }

    public String getTimer() {
        return timer;
    }

    public String getDuration() {
        return duration;
    }

    public void makeValue() {
        value = "{timer=" + timer + ", duration=" + duration + "}";
    }

    public boolean editOnDoubleClick(JFrame frame) {
        String oldMin = getTimer();
        String oldMax = getDuration();
        String[] array = new String[2];
        array[0] = getTimer();
        array[1] = getDuration();

        JDialogTimeInterval jdti = new JDialogTimeInterval(frame, array, "Setting absolute time constraints", "timer",
                "duration");
        // jdti.setSize(350, 250);
        GraphicLib.centerOnParent(jdti, 350, 250);
        jdti.setVisible(true); // blocked until dialog has been closed

        timer = array[0];
        duration = array[1];

        if ((timer != null) && (duration != null) && ((!timer.equals(oldMin)) || (!duration.equals(oldMax)))) {
            timer = timer.trim();
            duration = duration.trim();
            if (!TAttribute.isAValidId(timer, false, false, false)) {
                JOptionPane.showMessageDialog(frame, "Could not perform any change: the new name is not a valid name",
                        "Error", JOptionPane.INFORMATION_MESSAGE);
                timer = oldMin;
                duration = oldMax;
                return false;
            }

            boolean isInteger = true;
            try {
                /* int tg = */ Integer.parseInt(duration);
            } catch (NumberFormatException nfe) {
                isInteger = false;
            }

            if ((!TAttribute.isAValidId(duration, false, false, false)) && (isInteger == false)) {
                JOptionPane.showMessageDialog(frame,
                        "Could not perform any change: the new duration is not a valid duration", "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                timer = oldMin;
                duration = oldMax;
                return false;
            }
            makeValue();
            return true;
        }

        timer = oldMin;
        duration = oldMax;
        return false;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Interval timer=\"");
        sb.append(getTimer());
        sb.append("\" duration=\"");
        sb.append(getDuration());
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        //
        boolean timerSet = false;
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;

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
                            if (elt.getTagName().equals("Interval")) {
                                timer = elt.getAttribute("timer");
                                duration = elt.getAttribute("duration");
                                timerSet = true;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            if (!timerSet) {
                throw new MalformedModelingException();
            }
        }
        makeValue();
    }
}
