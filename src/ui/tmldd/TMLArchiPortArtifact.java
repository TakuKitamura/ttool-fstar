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
   * Class TMLArchiPortArtifact
   * Communication Artifact of a deployment diagram
   * Creation: 22/11/2007
   * @version 1.0 22/11/2007
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
import tmltranslator.ctranslator.*;

public class TMLArchiPortArtifact extends TGCWithoutInternalComponent implements SwallowedTGComponent, WithAttributes {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int textY2 =  35;
    protected int space = 5;
    protected int fileX = 20;
    protected int fileY = 25;
    protected int cran = 5;
    protected String mappedMemory = "VOID";
    protected String oldValue = "";
    protected String referenceCommunicationName = "TMLCommunication";
    protected String portName = "name";
    protected String typeName = "port";
    protected String startAddress = "";
    protected String endAddress = "";
		protected ArrayList<String> bufferParameters = new ArrayList<String>();
		protected String bufferType = "noBuffer";
    protected int priority = 5; // Between 0 and 10

    public TMLArchiPortArtifact(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 75;
        height = 40;
        minWidth = 75;

        nbConnectingPoint = 0;
        addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;

        value = "";
        portName = "name";
        referenceCommunicationName = "TMLCommunication";

        makeFullValue();

        //setPriority(((TMLArchiDiagramPanel)tdp).getPriority(getFullValue(), priority);

        myImageIcon = IconManager.imgic702;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int _priority) {
        priority = _priority;
    }


    public void internalDrawing(Graphics g) {

        if (oldValue.compareTo(value) != 0) {
            setValue(value, g);
        }

        g.drawRect(x, y, width, height);

        //g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawLine(x+width-space-fileX, y + space, x+width-space-fileX, y+space+fileY);
        g.drawLine(x+width-space-fileX, y + space, x+width-space-cran, y+space);
        g.drawLine(x+width-space-cran, y+space, x+width-space, y+space + cran);
        g.drawLine(x+width-space, y+space + cran, x+width-space, y+space+fileY);
        g.drawLine(x+width-space, y+space+fileY, x+width-space-fileX, y+space+fileY);
        g.drawLine(x+width-space-cran, y+space, x+width-space-cran, y+space+cran);
        g.drawLine(x+width-space-cran, y+space+cran, x + width-space, y+space+cran);

        g.drawImage(IconManager.img9, x+width-space-fileX + 3, y + space + 7, null);

        g.drawString(value, x + textX , y + textY);

        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.ITALIC));
        g.drawString(typeName, x + textX , y + textY + 20);
        g.setFont(f);

        // Link to selected memory
	Color c = g.getColor();
        if (c == ColorManager.POINTER_ON_ME_0) {
            TDiagramPanel tdp = getTDiagramPanel();
            TGComponent tgc;
            if (tdp != null) {
                if (mappedMemory.length() > 0) {
                    ListIterator iterator = tdp.getComponentList().listIterator();
                    while(iterator.hasNext()) {
                        tgc = (TGComponent)(iterator.next());
                        if (tgc instanceof TMLArchiMemoryNode) {
                            //TraceManager.addDev("Testing |" + tgc.getName() + "|  vs | " + s + "|");
                            if (tgc.getName().compareTo(mappedMemory) == 0) {
                                //TraceManager.addDev("Ok");
                                GraphicLib.dashedLine(g, getX() + getWidth()/2, getY() + getHeight()/2, tgc.getX() + tgc.getWidth()/2, tgc.getY() + tgc.getHeight()/2);
                            }
                        }
                    }
                }
            }
        }


    }

    public void setValue(String val, Graphics g) {
        oldValue = value;
        int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX + fileX + space);

        //System.out.println("width=" + width + " w1=" + w1 + " w2=" + w2 + " value=" + value);
        if (w1 != width) {
            width = w1;
            resizeWithFather();
        }
        //System.out.println("width=" + width + " w1=" + w1 + " value=" + value);
    }

    public void resizeWithFather() {
        if ((father != null) && (father instanceof TMLArchiCommunicationNode)) {
            //System.out.println("cdRect comp");
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }


    public boolean editOndoubleClick(JFrame frame) {
        String tmp;
        boolean error = false;

				TraceManager.addDev( "bufferParameters before: " + bufferParameters.toString() );
        JDialogPortArtifact dialog = new JDialogPortArtifact(frame, "Setting port artifact attributes", this, mappedMemory, bufferParameters, value );
        dialog.setSize(700, 600);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        mappedMemory = dialog.getMappedMemory();
        bufferParameters = dialog.getBufferParameters();
				TraceManager.addDev( "bufferParameters after: " + bufferParameters.toString() );
				bufferType = bufferParameters.get( Buffer.bufferTypeIndex );

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getReferenceCommunicationName() == null) {
            return false;
        }

        if (dialog.getReferenceCommunicationName().length() != 0) {
            tmp = dialog.getReferenceCommunicationName();
            referenceCommunicationName = tmp;

        }

        if (dialog.getCommunicationName().length() != 0) {
            tmp = dialog.getCommunicationName();

            if (!TAttribute.isAValidId(tmp, false, false)) {
                error = true;
            } else {
                portName = tmp;
            }
        }

        if (dialog.getTypeName().length() != 0) {
            typeName = dialog.getTypeName();
        }

        priority = dialog.getPriority();

        ((TMLArchiDiagramPanel)tdp).setPriority(getFullValue(), priority);


        if (error) {
            JOptionPane.showMessageDialog(frame,
                                          "Name is non-valid",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }

        makeFullValue();

        return !error;

    }

    private void makeFullValue() {
        value = referenceCommunicationName + "::" + portName;
    }

    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }

    public int getType() {
        return TGComponentManager.TMLARCHI_PORT_ARTIFACT;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info value=\"" + value + "\" portName=\"" + portName + "\" referenceCommunicationName=\"");
        sb.append(referenceCommunicationName);
        sb.append("\" typeName=\"" + typeName);
        sb.append("\" mappedMemory=\"" + mappedMemory );
				if( !bufferType.equals( "" ) && !bufferType.equals( "noBuffer" ) )	{
					switch( Integer.parseInt( bufferType ) )	{
						case Buffer.FepBuffer:
							sb.append( FepBuffer.appendBufferParameters( bufferParameters ) );
							break;
						case Buffer.InterleaverBuffer:	
        			sb.append("\" bufferType=\"" + bufferType );
							//data in
	        		sb.append( "\" packedBinaryInIntl=\"" + bufferParameters.get( InterleaverBuffer.packedBinaryInIntlIndex ) );
	        		sb.append( "\" widthIntl=\"" + bufferParameters.get( InterleaverBuffer.widthIntlIndex ) );
	        		sb.append( "\" bitInOffsetIntl=\"" + bufferParameters.get( InterleaverBuffer.bitInOffsetIntlIndex ) );
	        		sb.append( "\" inputOffsetIntl=\"" + bufferParameters.get( InterleaverBuffer.inputOffsetIntlIndex ) );
							//data out
	        		sb.append( "\" packedBinaryOutIntl=\"" + bufferParameters.get( InterleaverBuffer.packedBinaryOutIntlIndex ) );
	        		sb.append( "\" bitOutOffsetIntl=\"" + bufferParameters.get( InterleaverBuffer.bitOutOffsetIntlIndex ) );
	        		sb.append( "\" outputOffsetIntl=\"" + bufferParameters.get( InterleaverBuffer.outputOffsetIntlIndex ) );
							//permutation table
	        		sb.append( "\" offsetPermIntl=\"" + bufferParameters.get( InterleaverBuffer.offsetPermIntlIndex) );
	        		sb.append( "\" lengthPermIntl=\"" + bufferParameters.get( InterleaverBuffer.lengthPermIntlIndex) );
						break;
						case Buffer.AdaifBuffer:	
							sb.append( AdaifBuffer.appendBufferParameters( bufferParameters ) );
        			/*sb.append("\" bufferType=\"" + bufferType );
        			sb.append("\" numSamples=\"" + bufferParameters.get( MMBuffer.numSamplesIndex ) );
        			sb.append("\" baseAddress=\"" + bufferParameters.get( MMBuffer.baseAddressIndex ) );*/
							break;
						case Buffer.MapperBuffer:	
        			sb.append("\" bufferType=\"" + bufferType );
							//data in
      	  		sb.append("\" numSamplesDataInMapp=\"" + bufferParameters.get( MapperBuffer.numSamplesDataInMappIndex ) );
    	    		sb.append("\" baseAddressDataInMapp=\"" + bufferParameters.get( MapperBuffer.baseAddressDataInMappIndex ) );
        			sb.append("\" bitsPerSymbolDataInMapp=\"" + bufferParameters.get( MapperBuffer.bitsPerSymbolDataInMappIndex ) );
        			sb.append("\" symmetricalValueDataInMapp=\"" + bufferParameters.get( MapperBuffer.symmetricalValueDataInMappIndex ) );
							//data out
        			sb.append("\" baseAddressDataOutMapp=\"" + bufferParameters.get( MapperBuffer.baseAddressDataOutMappIndex ) );
							//Look-up Table
        			sb.append("\" baseAddressLUTMapp=\"" + bufferParameters.get( MapperBuffer.baseAddressLUTMappIndex ) );
							break;
						case Buffer.MainMemoryBuffer:	
        			sb.append("\" bufferType=\"" + bufferType );
  	      		sb.append("\" numSamples=\"" + bufferParameters.get( MMBuffer.numSamplesIndex ) );
  	      		sb.append("\" baseAddress=\"" + bufferParameters.get( MMBuffer.baseAddressIndex ) );
							break;
						default:	//the fep buffer 
							sb.append( FepBuffer.appendBufferParameters( bufferParameters ) );
							break;
					}
				}
        sb.append("\" />\n");
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
            String svalue = null, sname = null, sreferenceCommunication = null, stype = null;
            //String prio = null;

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
                                svalue = elt.getAttribute("value");
                                sname = elt.getAttribute("portName");
                                sreferenceCommunication = elt.getAttribute("referenceCommunicationName");
                                stype = elt.getAttribute("typeName");
                                mappedMemory = elt.getAttribute("mappedMemory");
																if( (elt.getAttribute("bufferType") != null) &&  (elt.getAttribute("bufferType").length() > 0) )	{
                                bufferType = elt.getAttribute("bufferType");
																//bufferParameters.add( bufferType );
																switch( Integer.parseInt( bufferType ) )	{
																	case Buffer.FepBuffer:
																		bufferParameters = FepBuffer.buildBufferParameters( elt );
												        		/*bufferParameters.add( FepBuffer.baseAddressIndex, elt.getAttribute( "baseAddress" ) );
												        		bufferParameters.add( elt.getAttribute( "numSamples" ) );
												        		bufferParameters.add( elt.getAttribute( "bank" ) );
												        		bufferParameters.add( elt.getAttribute( "dataType" ) );*/
																		break;
																	case Buffer.InterleaverBuffer:	
																		bufferParameters.add( bufferType );
																		//data in
	        													bufferParameters.add( elt.getAttribute( "packedBinaryInIntl" ) );
	        													bufferParameters.add( elt.getAttribute( "widthIntl" ) );
	        													bufferParameters.add( elt.getAttribute( "bitInOffsetIntl" ) );
	        													bufferParameters.add( elt.getAttribute( "inputOffsetIntl" ) );
																		//data out
																		bufferParameters.add( elt.getAttribute( "packedBinaryOutIntl" ) );
																		bufferParameters.add( elt.getAttribute( "bitOutOffsetIntl" ) );
																		bufferParameters.add( elt.getAttribute( "outputOffsetIntl" ) );
																		//permutation table
												        		bufferParameters.add( elt.getAttribute( "offsetPermIntl" ) );
												        		bufferParameters.add( elt.getAttribute( "lengthPermIntl" ) );
																		break;
																	case Buffer.AdaifBuffer:	
																		AdaifBuffer.buildBufferParameters( elt );
																		/*bufferParameters.add( bufferType );
												        		bufferParameters.add( elt.getAttribute( "numSamples" ) );
												        		bufferParameters.add( elt.getAttribute( "baseAddress" ) );*/
																		break;
																	case Buffer.MapperBuffer:	
																		bufferParameters.add( bufferType );
																		//data in
												        		bufferParameters.add( elt.getAttribute( "numSamplesDataInMapp" ) );
												        		bufferParameters.add( elt.getAttribute( "baseAddressDataInMapp" ) );
												        		bufferParameters.add( elt.getAttribute( "bitsPerSymbolDataInMapp" ) );
												        		bufferParameters.add( elt.getAttribute( "symmetricalValueDataInMapp" ) );
																		//data out
											        			bufferParameters.add( elt.getAttribute( "baseAddressDataOutMapp" ) );
																		//Look-up Table
											        			bufferParameters.add( elt.getAttribute( "baseAddressLUTMapp" ) );
																		break;
																	case Buffer.MainMemoryBuffer:	
																		bufferParameters.add( bufferType );
												        		bufferParameters.add( elt.getAttribute( "numSamples" ) );
												        		bufferParameters.add( elt.getAttribute( "baseAddress" ) );
																		break;
																	default:	//the main memory buffer 
																		bufferParameters.add( bufferType );
												        		bufferParameters.add( elt.getAttribute( "baseAddress" ) );
												        		bufferParameters.add( elt.getAttribute( "numSamples" ) );
												        		bufferParameters.add( elt.getAttribute( "bank" ) );
												        		bufferParameters.add( elt.getAttribute( "dataType" ) );
																		break;
																}
																}
                                //prio = elt.getAttribute("priority");
                            }
                            if (svalue != null) {
                                value = svalue;
                            }
                            if (sname != null){
                                portName = sname;
                            }
                            if (sreferenceCommunication != null) {
                                referenceCommunicationName = sreferenceCommunication;
                            }
                            if (stype != null){
                                typeName = stype;
                            }

                            /*if ((prio != null) && (prio.trim().length() > 0)) {
                              priority = Integer.decode(prio).intValue();
                              }*/
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Channel artifact");
            throw new MalformedModelingException();
        }
        makeFullValue();
    }

    public DesignPanel getDesignPanel() {
        return tdp.getGUI().getDesignPanel(value);
    }

    public String getReferenceCommunicationName() {
        return referenceCommunicationName;
    }

    public void setReferenceCommunicationName(String _referenceCommunicationName) {
        referenceCommunicationName = _referenceCommunicationName;
        makeFullValue();
    }

    public String getPortName() {
        return portName;
    }


    public String getFullValue() {
        String tmp = getValue();
        tmp += " (" + getTypeName() + ")";
        return tmp;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getAttributes() {
        return "Priority = " + priority;
    }

    public String getMappedMemory() {
        return mappedMemory;
    }

		public String getEndAddress()	{
			return endAddress;
		}

		public String getStartAddress()	{
			return startAddress;
		}

		public ArrayList<String> getBufferParameters()	{
			return bufferParameters;
		}

}
