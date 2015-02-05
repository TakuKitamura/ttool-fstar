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
   * Class TMLArchiCPNode
   * Node. To be used in TML architecture diagrams.
   * Creation: 20/02/2013
   * @version 1.0 20/02/2013
   * @author Ludovic APVRILLE
   * @see
   */

package ui.tmldd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

import tmltranslator.*;

public class TMLArchiCPNode extends TMLArchiCommunicationNode implements SwallowTGComponent {
    private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 2;
    private int derivationy = 3;
    private String stereotype = "CP";
    private String reference="";
    private Vector<String> mappedUnits = new Vector<String>();

    public TMLArchiCPNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 250;
        height = 50;
        minWidth = 100;
        minHeight = 50;

        nbConnectingPoint = 0;
        connectingPoint = new TGConnectingPoint[0];

        /*connectingPoint[0] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.0, 0.0);
          connectingPoint[1] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
          connectingPoint[2] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 1.0, 0.0);
          connectingPoint[3] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
          connectingPoint[4] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 1.0, 0.5);
          connectingPoint[5] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.0, 1.0);
          connectingPoint[6] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.5, 1.0);
          connectingPoint[7] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 1.0, 1.0);

          connectingPoint[8] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.25, 0.0);
          connectingPoint[9] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.75, 0.0);
          connectingPoint[10] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.0, 0.25);
          connectingPoint[11] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 1.0, 0.25);
          connectingPoint[12] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.0, 0.75);
          connectingPoint[13] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 1.0, 0.75);
          connectingPoint[14] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.25, 1.0);
          connectingPoint[15] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.75, 1.0);*/

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        name = tdp.findNodeName("CP");
        value = name;

        myImageIcon = IconManager.imgic700;
    }

    public void internalDrawing(Graphics g) {
        Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);


        // Top lines
        g.drawLine(x, y, x + derivationx, y - derivationy);
        g.drawLine(x + width, y, x + width + derivationx, y - derivationy);
        g.drawLine(x + derivationx, y - derivationy, x + width + derivationx, y - derivationy);

        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationx, y - derivationy + height);
        g.drawLine(x + derivationx + width, y - derivationy, x + width + derivationx, y - derivationy + height);

        // Filling color
        g.setColor(ColorManager.BUS_BOX);
        g.fill3DRect(x+1, y+1, width-1, height-1, true);
        g.setColor(c);

        // Strings
        String ster = "<<" + stereotype + ">>";
        int w  = g.getFontMetrics().stringWidth(ster);
        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        g.drawString(ster, x + (width - w)/2, y + textY1);
        g.setFont(f);
        String val = name + "::" + reference;
        w  = g.getFontMetrics().stringWidth(val);
        g.drawString(val, x + (width - w)/2, y + textY2);

        // Icon
        //g.drawImage(IconManager.imgic1102.getImage(), x + width - 20, y + 4, null);
        g.drawImage(IconManager.imgic1102.getImage(), x + 4, y + 4, null);
        g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
    }

    public TGComponent isOnOnlyMe(int x1, int y1) {

        Polygon pol = new Polygon();
        pol.addPoint(x, y);
        pol.addPoint(x + derivationx, y - derivationy);
        pol.addPoint(x + derivationx + width, y - derivationy);
        pol.addPoint(x + derivationx + width, y + height - derivationy);
        pol.addPoint(x + width, y + height);
        pol.addPoint(x, y + height);
        if (pol.contains(x1, y1)) {
            return this;
        }

        return null;
    }

    public String getStereotype() {
        return stereotype;

    }

    public String getNodeName() {
        return name;
    }

    public String getReference() {
        return reference;
    }

    public boolean editOndoubleClick( JFrame frame ) {
        boolean error = false;
        String errors = "";
        String tmpName;

        JDialogReferenceCP dialog = new JDialogReferenceCP( frame, "Setting CP attributes", this, mappedUnits, name );
        dialog.setSize( 700, 550 );
        GraphicLib.centerOnParent( dialog );
        dialog.show(); // blocked until dialog has been closed
        //setJDialogOptions(jdab);
        name = dialog.getNodeName();
        mappedUnits = dialog.getMappedUnits();

        if( !dialog.isRegularClose() )  {
            return false;
        }

        if( dialog.getNodeName().length() != 0 )        {
            tmpName = dialog.getNodeName();
            tmpName = tmpName.trim();
            if( !TAttribute.isAValidId(tmpName, false, false) ) {
                error = true;
                errors += "Name of the node  ";
            }
            else        {
                name = tmpName;
            }
        }

        reference = dialog.getCPReference();

        if( error )     {
            JOptionPane.showMessageDialog( frame, "Invalid value for the following attributes: " + errors,
                                           "Error", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        return true;
    }   //End of method editOnDoubleClick




    public int getType() {
        return TGComponentManager.TMLARCHI_CPNODE;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info stereotype=\"" + stereotype + "\" nodeName=\"" + name);
        sb.append("\" />\n");
        sb.append("<attributes reference=\"" + reference + "\" ");
        sb.append("/>\n");
        for( String s: mappedUnits )    {
            String[] firstPart = s.split( " : " );
            String[] secondPart = firstPart[0].split("\\.");
            sb.append( "<mappingInfo " + "CPname=\"" + secondPart[0] + "\" instanceName=\"" + secondPart[1] +
                       "\" architectureUnit=\"" + firstPart[1] + "\" />\n" );
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            int t1id;
            String sstereotype = null, snodeName = null;

            mappedUnits.removeAllElements();
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; i<nli.getLength(); i++) {
                        n2 = nli.item(i);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                sstereotype = elt.getAttribute("stereotype");
                                snodeName = elt.getAttribute("nodeName");
                            }
                            if (sstereotype != null) {
                                stereotype = sstereotype;
                            }
                            if (snodeName != null){
                                name = snodeName;
                            }

                            if (elt.getTagName().equals("attributes")) {
                                reference = elt.getAttribute("reference");
                            }
                            if( elt.getTagName().equals("mappingInfo")) {
                                String instanceName = elt.getAttribute( "instanceName" ) ;
                                String architectureUnit = elt.getAttribute( "architectureUnit" ) ;
                                TraceManager.addDev( "architectureUnit: " + architectureUnit );
                                mappedUnits.add( reference + "." + instanceName + " : " + architectureUnit );
                                TraceManager.addDev( "added: " + reference + "." + instanceName + " : " + architectureUnit );
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public int getComponentType()       {
        return OTHER;
    }


}
