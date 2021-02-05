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


package ui.atd;

import attacktrees.Attacker;
import attacktrees.AttackerGroup;
import attacktrees.AttackerPopulation;
import myutil.Conversion;
import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAttack;
import ui.window.JDialogAttackerPopulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class ATDAttack
 * Attack to SysML value type
 * Creation: 09/12/2009
 *
 * @author Ludovic APVRILLE
 * @version 1.0 09/12/2009
 */
public class ATDAttackerPopulation extends TGCScalableWithInternalComponent implements WithAttributes
        /*, CanBeDisabled*/ {
    private int textY1 = 3;
    //   private int textY2 = 3;

    // private static int arc = 7;
    //private int textX = 10;

    private AttackerPopulation population;


    public ATDAttackerPopulation(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 250;
        height = (int) (200 * tdp.getZoom());
        minWidth = 100;
        minHeight = 50;

        nbConnectingPoint = 0;
        connectingPoint = new TGConnectingPoint[0];

        addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        population = new AttackerPopulation("MyPopulation", this);

        oldScaleFactor = tdp.getZoom();

        myImageIcon = IconManager.imgic702;
    }

    @Override
    public void internalDrawing(Graphics g) {

        Color c = g.getColor();
        g.setColor(ColorManager.ATD_ATTACKER_POPULATION);
        g.fillRect(x, y, width, height);
        g.setColor(c);

        int fontHeight = g.getFontMetrics().getHeight();
        int initY = y + fontHeight + textY1;

        drawSingleLimitedString(g, "Pop. name: " +  population.getName(), x+3, initY, width-4, 1);
        //g.drawString("Pop. name: " +  population.getName(), x+3, initY);
        initY += fontHeight + textY1;

        ArrayList<AttackerGroup> groups = population.getAttackerGroups();
        for(AttackerGroup ag: groups) {
            String s = ag.toString();
            //TraceManager.addDev("ag=" + s);
            if (s != null) {
                if (initY >= y + height) {
                    break;
                }
                drawSingleLimitedString(g, s, x+3, initY, width-4, 0);
            }
            initY += fontHeight + textY1;
        }
    }



    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String tmp;
        boolean error = false;

        JDialogAttackerPopulation dialog = new JDialogAttackerPopulation(frame, "Setting attackers", population);
        //     dialog.setSize(450, 350);
        GraphicLib.centerOnParent(dialog, 850, 350);
        dialog.setVisible(true); // blocked until dialog has been closed

        return dialog.hasBeenCancelled();
    }

    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {

        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    @Override
    public int getType() {
        return TGComponentManager.ATD_ATTACKER_POPULATION;
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        for(AttackerGroup ag: population.getAttackerGroups()) {
            sb.append("<attackergroup name=\"" + ag.attacker.getName());
            sb.append("\" money=\"" + ag.getMoney());
            sb.append("\" expertise=\"" + ag.getExpertise());
            sb.append("\" occurrence=\"" + ag.getOccurrence());
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            //      int t1id;
            String id = null;
            String money = null;
            String expertise = null, nb = null;

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
                            if (elt.getTagName().equals("attackergroup")) {
                                id = elt.getAttribute("name");
                                money = elt.getAttribute("money");
                                expertise = elt.getAttribute("expertise");
                                nb = elt.getAttribute("occurrence");
                            }
                            population.addAttackerGroup(id, money, expertise, nb);

                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

   public AttackerPopulation getAttackerPopulation() {
        return population;
   }

   public String getAttributes() {
        return "";
   }


    
    /**
     * Issue #69
     * @return : always true
     */
    @Override
    public boolean canBeDisabled() {
    	return true;
    }
}
