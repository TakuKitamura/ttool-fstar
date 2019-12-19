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
import tmltranslator.modelcompiler.CPMEC;
import tmltranslator.modelcompiler.CpuMemoryCopyMEC;
import tmltranslator.modelcompiler.DoubleDmaMEC;
import tmltranslator.modelcompiler.SingleDmaMEC;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogCommPatternMapping;

import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Class TMLArchiCPNode
 * Node. To be used in TML architecture diagrams.
 * Creation: 20/02/2013
 * @version 1.0 20/02/2013
 * @author Ludovic APVRILLE
 */
public class TMLArchiCPNode extends TMLArchiCommunicationNode implements SwallowTGComponent, WithAttributes, TMLArchiCPInterface {
    // Graphical attributes
	// Issue #31
//    private int textY1 = 15;
//    private int textY2 = 30;
//    private int derivationx = 2;
//    private int derivationy = 3;
    private String stereotype = "CP";
    private String reference="";

    // Mapped data
    private Vector<String> mappedUnits = new Vector<String>();
    private Vector<String> assignedAttributes = new Vector<String>();
    private String cpMEC = "VOID";
    private int transferType1 = -1;
    private int transferType2 = -1;
    
    // Derived attribute
    //private String completeName;

    public TMLArchiCPNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        // Issue #31
//        width = 250;
//        height = 50;
        textY = 15;
        minWidth = 100;
        minHeight = 50;
        initScaling( 250, 50 );

        nbConnectingPoint = 0;
        connectingPoint = new TGConnectingPoint[0];

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

    @Override
    protected void internalDrawing(Graphics g) {
        Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);

        // Issue #31
        final int derivationX = scale( DERIVATION_X );
        final int derivationY = scale( DERIVATION_Y );

        // Top lines
        g.drawLine(x, y, x + derivationX, y - derivationY);
        g.drawLine(x + width, y, x + width + derivationX, y - derivationY);
        g.drawLine(x + derivationX, y - derivationY, x + width + derivationX, y - derivationY);

        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationX, y - derivationY + height);
        g.drawLine(x + derivationX + width, y - derivationY, x + width + derivationX, y - derivationY + height);

        // Filling color
        g.setColor(ColorManager.BUS_BOX);
        g.fill3DRect(x+1, y+1, width-1, height-1, true);
        g.setColor(c);

        // Strings
        String ster = "<<" + stereotype + ">>";
        int w  = g.getFontMetrics().stringWidth(ster);
        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        drawSingleString(g,ster, x + (width - w)/2, y + textY); // Issue #31
        g.setFont(f);

        // Complete name is derived
        final String val = getCompleteName();

//        String val = name + "::" + reference;
//        completeName = val;
        w  = g.getFontMetrics().stringWidth(val);
        drawSingleString(g,val, x + (width - w)/2, y + 2 * textY ); // Issue #31

        // Icon

        // Issue #31
        final int iconMargin = scale( 4 );
        g.drawImage( scale( IconManager.imgic1102.getImage() ), x + iconMargin/*4*/, y + iconMargin/*4*/, null);
        g.drawImage( scale( IconManager.img9 ), x + width - scale( 20 ), y + iconMargin/*4*/, null);

        //g.drawImage(IconManager.imgic1102.getImage(), x + width - 20, y + 4, null);
//        g.drawImage(IconManager.imgic1102.getImage(), x + 4, y + 4, null);
        //g.drawImage(IconManager.img9, x + width - 20, y + 4, null);


        // Link to mapped units
        if (c == ColorManager.POINTER_ON_ME_0) {
            TDiagramPanel tdp = getTDiagramPanel();
            TGComponent tgc;
            if (tdp != null) {
                for(String ss: mappedUnits) {
                    int index = ss.indexOf(":");
                    if (index > -1) {
                        String[] tabOfNames = ss.substring(index+1, ss.length()).trim().split(",");
                        for (int i=0; i<tabOfNames.length; i++) {
                            String s = tabOfNames[i].trim();
                            if (s.length() > 0) {
                                ListIterator<TGComponent> iterator = tdp.getComponentList().listIterator();

                                while(iterator.hasNext()) {
                                    tgc = iterator.next();

                                    if (tgc instanceof TMLArchiNode) {
                                        //TraceManager.addDev("Testing |" + tgc.getName() + "|  vs | " + s + "|");
                                        if (tgc.getName().compareTo(s) == 0) {
                                            //TraceManager.addDev("Ok");
                                            GraphicLib.dashedLine(g, getX() + getWidth()/2, getY() + getHeight()/2, tgc.getX() + tgc.getWidth()/2, tgc.getY() + tgc.getHeight()/2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {
        Polygon pol = new Polygon();
        pol.addPoint(x, y);

        // Issue #31
        final int derivationX =  scale( DERIVATION_X );
        final int derivationY =  scale( DERIVATION_Y );
        pol.addPoint(x + derivationX, y - derivationY);
        pol.addPoint(x + derivationX + width, y - derivationY);
        pol.addPoint(x + derivationX + width, y + height - derivationY);
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

    @Override
    public boolean editOndoubleClick( JFrame frame ) {
        boolean error = false;
        String errors = "";
        String tmpName;

        JDialogCommPatternMapping dialog = new JDialogCommPatternMapping( frame, "Communication Pattern Mapping", this, mappedUnits, name, cpMEC, assignedAttributes, transferType1, transferType2 );
        //dialog.setSize( 700, 550 );
        GraphicLib.centerOnParent( dialog, 750, 500 );
        dialog.setVisible( true ); // blocked until dialog has been closed
        //setJDialogOptions(jdab);

        // Issue #36
        if( dialog.hasBeenCancelled() )  {
            return false;
        }

        name = dialog.getNodeName();
        mappedUnits = dialog.getMappedUnits();
        cpMEC = dialog.getCPMEC();
        transferType1 = dialog.getTransferTypes().get(0);
        transferType2 = dialog.getTransferTypes().get(1);
        assignedAttributes = dialog.getAssignedAttributes();
        //        TraceManager.addDev( "name " + name );
        //        TraceManager.addDev( "mappedUnits " + mappedUnits );
        //        TraceManager.addDev( "cpMEC " + cpMEC );
        //        TraceManager.addDev( "transferType1 " + transferType1 );
        //        TraceManager.addDev( "transferType2 " + transferType2 );
        //        TraceManager.addDev( "assignedAttributes " + assignedAttributes.toString() );

        // Issue #36
        //        if( !dialog.isRegularClose() )  {
        //            return false;
        //        }

        if( dialog.getNodeName().length() != 0 )        {
            tmpName = dialog.getNodeName();
            tmpName = tmpName.trim();

            if( !TAttribute.isAValidId(tmpName, false, false, false) ) {
                error = true;
                errors += "Name of the node  ";
            }
            else        {
                name = tmpName;
            }
        }

        reference = dialog.getCPReference();

        if( error ) {
            JOptionPane.showMessageDialog( frame, "Invalid value for the following attributes: " + errors,
                                           "Error", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        return true;
    }   //End of method editOnDoubleClick

    @Override
    public int getType() {
        return TGComponentManager.TMLARCHI_CPNODE;
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info stereotype=\"" + stereotype + "\" nodeName=\"" + name + "\" cpMEC=\"" + cpMEC + "\" transferType1=\"" + String.valueOf(transferType1) + "\" transferType2=\"" + String.valueOf(transferType2) );
        sb.append("\" />\n");
        sb.append("<attributes reference=\"" + reference + "\" ");
        sb.append("/>\n");

        for( String s: mappedUnits )    {
            String[] firstPart = s.split( " : " );
            String[] secondPart = firstPart[0].split("\\.");
            sb.append( "<mappingInfo " + "CPname=\"" + secondPart[0] + "\" instanceName=\"" + secondPart[1] +
                       "\" architectureUnit=\"" + firstPart[1] + "\" />\n" );
        }

        for( String s: assignedAttributes )    {
            String[] tokens = s.split( " " );
            sb.append( "<mappedAttributes " + "type=\"" + tokens[0] + "\" name=\"" + tokens[1] + "\" value=\"" + tokens[3].substring(0,tokens[3].length()-1) + "\" />\n" );
        }

        sb.append("</extraparam>\n");

        return sb.toString();
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId)
        throws MalformedModelingException {
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            //       int t1id;
            String sstereotype = null, snodeName = null;

            mappedUnits.removeAllElements();
            assignedAttributes.removeAllElements();
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();

                    // Issue #17 copy-paste error on j index
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //n2 = nli.item(i);

                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                sstereotype = elt.getAttribute("stereotype");
                                snodeName = elt.getAttribute("nodeName");
                                if( ( elt.getAttribute("cpMEC") != null ) && ( elt.getAttribute("cpMEC").length() > 0 ) )       {
                                    cpMEC = elt.getAttribute( "cpMEC" );
                                    transferType1 = Integer.parseInt(elt.getAttribute( "transferType1" ) );
                                    transferType2 = Integer.parseInt(elt.getAttribute( "transferType2" ) );
                                }
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
                                mappedUnits.add( reference + "." + instanceName + " : " + architectureUnit );
                            }
                            if( elt.getTagName().equals("mappedAttributes")) {
                                String attributeType = elt.getAttribute( "type" );
                                String attributeName = elt.getAttribute( "name" );
                                String attributeValue = elt.getAttribute( "value" );
                                assignedAttributes.add( attributeType + " " + attributeName + " = " + attributeValue + ";" );
                            }
                        }
                    }
                }
            }

        }
        catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    @Override
    public int getComponentType()       {
        return OTHER;
    }

    @Override
    public boolean addSwallowedTGComponent( TGComponent tgc, int x, int y )     {

        if( tgc instanceof TMLArchiCommunicationArtifact )      {
            // Make it an internal component
            // It's one of my son
            //Set its coordinates
            tgc.setFather(this);
            tgc.setDrawingZone(true);
            //
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //
            tgc.resizeWithFather();
            //tgc.setCdRectangle(0, width - tgc.getWidth(), 0, height - tgc.getHeight());
            //tgc.setCd(x, y);
            //add it
            addInternalComponent( tgc, 0 );
            return true;
        }
        else    {
            if( tgc instanceof TMLArchiPortArtifact )   {
                tgc.setFather( this );
                tgc.setDrawingZone( true );
                tgc.resizeWithFather();
                addInternalComponent( tgc, 0 );
                return true;
            }
            return false;
        }
    }

    @Override
    public void hasBeenResized() {
        for( int i = 0; i < nbInternalTGComponent; i++ )        {
            if( tgcomponent[i] instanceof TMLArchiCommunicationArtifact ) {
                tgcomponent[i].resizeWithFather();
            }
            else        {
                if( tgcomponent[i] instanceof TMLArchiPortArtifact )    {
                    tgcomponent[i].resizeWithFather();
                }
            }
        }
    }

    public List<TMLArchiPortArtifact> getPortArtifactList() {
        List<TMLArchiPortArtifact> v = new ArrayList<TMLArchiPortArtifact>();

        for( int i = 0; i < nbInternalTGComponent; i++ )        {
            if( tgcomponent[i] instanceof TMLArchiPortArtifact )        {
                v.add( (TMLArchiPortArtifact)(tgcomponent[i]) );
            }
        }

        return v;
    }

    public Vector<String> getMappedUnits()      {
        return mappedUnits;
    }

    public int getCPMEC()       {
        if( cpMEC.equals( "Memory Copy" ) )     {
            return CPMEC.CPU_MEMORY_COPY_MEC;
        }
        if( cpMEC.equals( "Single DMA" ) )      {
            return CPMEC.SINGLE_DMA_MEC;
        }
        if( cpMEC.equals( "Double DMA" ) )      {
            return CPMEC.DOUBLE_DMA_MEC;
        }
        return -1;
    }

    public String getCompleteName()     {
      return name + "::" + String.valueOf( reference );
        //return completeName;
    }

    public Vector<String> getAssignedAttributes() throws java.lang.IllegalArgumentException {
        Vector<String> vectorToReturn;
        switch( cpMEC ) {
        case CPMEC.MEMORY_COPY:
            //TraceManager.addDev( "**** ASSIGNED ATTRIBUTES ****\\" + assignedAttributes );
            vectorToReturn = CPMEC.getSortedAttributeValues( assignedAttributes, CpuMemoryCopyMEC.ORDERED_ATTRIBUTE_NAMES );

            break;
        case CPMEC.SINGLE_DMA:
            vectorToReturn = CPMEC.getSortedAttributeValues( assignedAttributes, SingleDmaMEC.ORDERED_ATTRIBUTE_NAMES );
            //vectorToReturn = SingleDmaMEC.sortAttributes( assignedAttributes );

            break;
        case CPMEC.DOUBLE_DMA:
            vectorToReturn = CPMEC.getSortedAttributeValues( assignedAttributes, DoubleDmaMEC.ORDERED_ATTRIBUTE_NAMES );
            //vectorToReturn = DoubleDmaMEC.sortAttributes( assignedAttributes );

            break;
        default:
	    if (cpMEC == null) {
		throw new IllegalArgumentException( "Unknown communication pattern ");
	    }
            throw new IllegalArgumentException( "Unknown communication pattern " + cpMEC + "." );
            //TraceManager.addDev( "ERROR in returning assignedAttributes" );
            //vectorToReturn = assignedAttributes;

            //break;
        }

        return vectorToReturn;
    }

    public List<Integer> getTransferTypes()        {
        List<Integer> transferTypes = new ArrayList<Integer>();
        transferTypes.add( transferType1 );
        transferTypes.add( transferType2 );

        return transferTypes;
    }

    // Display the mapping of instances onto platform units
    @Override
    public String getAttributes()   {
        String attr = "";

        for( String s: mappedUnits )    {
            if( s.split("\\.").length > 0 ) {   // Remove the trailing name of the CP
                attr += s.split("\\.")[1] + "\n";
            }
            else    {
                attr += s + "\n";
            }
        }

        return attr;
    }
}
