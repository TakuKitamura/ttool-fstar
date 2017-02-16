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
   * Class AvatarBDBlock
   * Node. To be used in AVATAR Block Diagrams
   * Creation: 06/04/2010
   * @version 1.1 06/04/2010
   * @author Ludovic APVRILLE
   * @see
   */

package ui.avatarbd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;
import ui.avatarsmd.*;


public class AvatarBDBlock extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent, GenericTree, AvatarBDStateMachineOwner {

    private static String GLOBAL_CODE_INFO = "(global code)";

    private int textY1 = 3;
    private static String stereotype = "block";
    private static String stereotypeCrypto = "cryptoblock";

    private int maxFontSize = 12;
    private int minFontSize = 4;
    private int currentFontSize = -1;
    private int textX = 7;

    private int limitName = -1;
    private int limitAttr = -1;
    private int limitMethod = -1;

    // Icon
    private int iconSize = 15;
    private boolean iconIsDrawn = false;

    private boolean isCryptoBlock = false;


    // TAttribute, AvatarMethod, AvatarSignal
    protected LinkedList<TAttribute> myAttributes;
    protected LinkedList<AvatarMethod> myMethods;
    protected LinkedList<AvatarSignal> mySignals;
    protected String [] globalCode;


    public String oldValue;

    public AvatarBDBlock(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 250;
        height = 200;
        minWidth = 5;
        minHeight = 2;

        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];

        connectingPoint[0] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);

        connectingPoint[8] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        multieditable = true;
        removable = true;
        userResizable = true;

        name = tdp.findAvatarBDBlockName("Block");
        setValue(name);
        oldValue = value;

        oldScaleFactor = tdp.getZoom();
        currentFontSize = (int) (maxFontSize*oldScaleFactor);

        myImageIcon = IconManager.imgic700;

        this.myAttributes = new LinkedList<TAttribute> ();
        this.myMethods = new LinkedList<AvatarMethod> ();
        this.mySignals = new LinkedList<AvatarSignal> ();

        actionOnAdd();
    }

    @Override
    public void internalDrawing (Graphics graph) {
        Font font = graph.getFont ();
        this.internalDrawingAux (graph);
        graph.setFont (font);
    }

    public void setSignalsAsNonAttached() {
	for (AvatarSignal mySig: mySignals) mySig.attachedToARelation = false;
    }
    
    public void addSignal(AvatarSignal sig){
	this.mySignals.add(sig);
    }
    
    public void internalDrawingAux (Graphics graph) {
        // Draw outer rectangle (for border)
        Color c = graph.getColor ();
        graph.drawRect (this.x, this.y, this.width, this.height);

        // Draw inner rectangle
        graph.setColor (ColorManager.AVATAR_BLOCK);
        graph.fillRect (this.x+1, this.y+1, this.width-1, this.height-1);
        graph.setColor (c);

        // limits
        this.limitName = -1;
        this.limitAttr = -1;
        this.limitMethod = -1;

        // h retains the coordinate along X where an element was last drawn
        int h = 0;

        int textY1 = (int) (this.textY1 * this.tdp.getZoom ());
        int textX = (int) (this.textX * this.tdp.getZoom ());

        // Draw icon
        this.iconIsDrawn = this.width > IconManager.iconSize + 2*textX && height > IconManager.iconSize + 2*textX;
        if (this.iconIsDrawn)
            graph.drawImage (IconManager.img5100, this.x + this.width - IconManager.iconSize - textX, this.y + textX, null);


        Font font = graph.getFont ();

        String ster;
        if (!this.isCryptoBlock)
            ster = "<<" + stereotype + ">>";
        else
            ster = "<<" + stereotypeCrypto + ">>";

        if (this.rescaled && !this.tdp.isScaled ()) {
            this.rescaled = false;
            // Must set the font size...
            // Incrementally find the biggest font not greater than max_font size
            // If font is less than min_font, no text is displayed

            // This is the maximum font size possible
            int maxCurrentFontSize = Math.max (0, Math.min (this.height, (int) (this.maxFontSize*this.tdp.getZoom ())));
            font = font.deriveFont ((float) maxCurrentFontSize);

            // Try to decrease font size until we get below the minimum
            while (maxCurrentFontSize > (this.minFontSize*this.tdp.getZoom () - 1)) {
                // Compute width of name of the function
                int w0 = graph.getFontMetrics (font).stringWidth (this.value);
                // Compute width of string stereotype
                int w1 = graph.getFontMetrics (font).stringWidth (ster);

                // if one of the two width is small enough use this font size
                if (Math.min (w0, w1) < this.width - (2*this.textX))
                    break;

                // Decrease font size
                maxCurrentFontSize --;
                // Scale the font
                font = font.deriveFont ((float) maxCurrentFontSize);
            }

            // Box is too damn small
            if (this.currentFontSize < this.minFontSize*this.tdp.getZoom ()) {
                maxCurrentFontSize ++;
                // Scale the font
                font = font.deriveFont ((float) maxCurrentFontSize);
            }

            // Use this font
            graph.setFont (font);
            this.currentFontSize = maxCurrentFontSize;
        } else
            font = font.deriveFont (this.currentFontSize);

        graph.setFont (font.deriveFont (Font.BOLD));
        h = graph.getFontMetrics ().getAscent () + graph.getFontMetrics ().getLeading () + textY1;

        if (h + graph.getFontMetrics ().getDescent () + textY1 >= this.height)
            return;

        // Write stereotype if small enough
        int w = graph.getFontMetrics ().stringWidth (ster);
        if (w + 2*textX < this.width)
            graph.drawString (ster, this.x + (this.width - w)/2, this.y + h);
        else {
            // try to draw with "..." instead
            if (!this.isCryptoBlock)
                ster = this.stereotype;
            else
                ster = this.stereotypeCrypto;

            for (int stringLength = ster.length ()-1; stringLength >= 0; stringLength--) {
                String abbrev = "<<" + ster.substring (0, stringLength) + "...>>";
                w = graph.getFontMetrics ().stringWidth (abbrev);
                if (w + 2*textX < this.width) {
                    graph.drawString (abbrev, this.x + (this.width - w)/2, this.y + h);
                    break;
                }
            }
        }

        // Write value if small enough
        graph.setFont (font);
        h += graph.getFontMetrics ().getHeight () + textY1;
        if (h + graph.getFontMetrics ().getDescent () + textY1 >= this.height)
            return;

        w = graph.getFontMetrics ().stringWidth (this.value);
        if (w + 2*textX < this.width)
            graph.drawString (this.value, this.x + (this.width - w)/2, this.y + h);
        else {
            // try to draw with "..." instead
            for (int stringLength = this.value.length ()-1; stringLength >= 0; stringLength--) {
                String abbrev = this.value.substring (0, stringLength) + "...";
                w = graph.getFontMetrics ().stringWidth (abbrev);
                if (w + 2*textX < this.width) {
                    graph.drawString (abbrev, this.x + (this.width - w)/2, this.y + h);
                    break;
                }
            }
        }

        h += graph.getFontMetrics ().getDescent () + textY1;

        // Update lower bound of text
        this.limitName = this.y + h;

        if (h + textY1 >= this.height)
            return;

        // Draw separator
        graph.drawLine (this.x, this.y+h, this.x+this.width, this.y+h);

        if (! ((AvatarBDPanel) this.tdp).areAttributesVisible ())
            return;

        // Set font size
        // int attributeFontSize = Math.min (12, this.currentFontSize - 2);
        int attributeFontSize = this.currentFontSize*5/6;
        graph.setFont (font.deriveFont ((float) attributeFontSize));
        int step = graph.getFontMetrics ().getHeight ();

        h += textY1;

        // Attributes 
        for (TAttribute attr: this.myAttributes) {
            h += step;
            if (h >= this.height - textX) {
                this.limitAttr = this.y + this.height;
                return;
            }

            // Get the string for this parameter
            String attrString = attr.toAvatarString ();

            // Try to draw it
            w = graph.getFontMetrics ().stringWidth (attrString);
            if (w + 2*textX < this.width) {
                graph.drawString (attrString, this.x + textX, this.y + h);
                this.drawConfidentialityVerification (attr.getConfidentialityVerification (), graph, this.x, this.y + h);
            } else {
                // If we can't, try to draw with "..." instead
                int stringLength;
                for (stringLength = attrString.length ()-1; stringLength >= 0; stringLength--) {
                    String abbrev = attrString.substring (0, stringLength) + "...";
                    w = graph.getFontMetrics ().stringWidth (abbrev);
                    if (w + 2*textX < this.width) {
                        graph.drawString (abbrev, this.x + textX, this.y + h);
                        this.drawConfidentialityVerification (attr.getConfidentialityVerification (), graph, this.x, this.y + h);
                        break;
                    }
                }

                if (stringLength < 0)
                    // skip attribute
                    h -= step;
            }
        }

        h += graph.getFontMetrics ().getDescent () + textY1;

        // Remember the end of attributes
        this.limitAttr = this.y + h;

        if (h + textY1 >= this.height)
            return;

        graph.drawLine (this.x, this.y+h, this.x+this.width, this.y+h);
        h += textY1;

        // Methods
        for (AvatarMethod method: this.myMethods) {
            h += step;
            if (h >= this.height - textX) {
                this.limitMethod = this.y + this.height;
                return;
            }

            // Get the string for this method
            String methodString = "- " + method.toString ();

            w = graph.getFontMetrics ().stringWidth (methodString);
            if (w + 2*textX < this.width)
                graph.drawString (methodString, this.x + textX, this.y + h);
            else {
                // If we can't, try to draw with "..." instead
                int stringLength;
                for (stringLength = methodString.length ()-1; stringLength >= 0; stringLength--) {
                    String abbrev = methodString.substring (0, stringLength) + "...";
                    w = graph.getFontMetrics ().stringWidth (abbrev);
                    if (w + 2*textX < this.width) {
                        graph.drawString (abbrev, this.x + textX, this.y + h);
                        break;
                    }
                }

                if (stringLength < 0)
                    // skip method
                    h -= step;
            }
        }

        h += graph.getFontMetrics ().getDescent () + textY1;

        // Remember limit of methods
        this.limitMethod = this.y + h;

        if (h + textY1 >= this.height)
            return;

        graph.drawLine (this.x, this.y+h, this.x+this.width, this.y+h);
        h += textY1;

        // Signals
        for (AvatarSignal signal: this.mySignals) {
            h += step;
            if (h >= this.height - textX)
                return;

            String signalString = "~ " + signal.toString ();
            w = graph.getFontMetrics ().stringWidth (signalString);
            if (w + 2*textX < this.width) {
                graph.drawString (signalString, this.x + textX, this.y + h);
		drawInfoAttachement(signal, graph, x, y+h);
			
	    }
	    else {
                // If we can't, try to draw with "..." instead
                int stringLength;
                for (stringLength = signalString.length ()-1; stringLength >= 0; stringLength--) {
                    String abbrev = signalString.substring (0, stringLength) + "...";
                    w = graph.getFontMetrics ().stringWidth (abbrev);
                    if (w + 2*textX < this.width) {
                        graph.drawString (abbrev, this.x + textX, this.y + h);
			drawInfoAttachement(signal, graph, x, y+h);		       
			
			
                        break;
                    }
                }

                if (stringLength < 0)
                    // skip signal
                    h -= step;
            }
        }

        h += graph.getFontMetrics ().getDescent () + textY1;

        if (h + textY1 >= this.height)
            return;

        // Global code
        if (hasGlobalCode()) {
            if (h+textY1+step >= this.height - textX)
                return;

            graph.drawLine (this.x, this.y+h, this.x+this.width, this.y+h);
            h += textY1+step;

            w = graph.getFontMetrics ().stringWidth (GLOBAL_CODE_INFO);
            if (w + 2*textX < this.width)
                graph.drawString (GLOBAL_CODE_INFO, this.x + (this.width - w)/2, this.y + h);
        }
    }

    private void drawInfoAttachement(AvatarSignal _as,  Graphics g, int _x, int _y) {
	if (_as.attachedToARelation) {
	    return;
	}
	Color c = g.getColor();
	g.setColor(Color.RED);
	int []xA = new int[3];
	int []yA = new int[3];
	//top of triangle
	xA[0] = _x + 5;
	yA[0] = _y - 7;
	
	// Right bottom point
	xA[1] = _x + 2;
	yA[1] = _y;

	// Left bottom point
	xA[2] = _x + 8;
	yA[2] = _y;

	g.fillPolygon(xA, yA, 3);

	g.setColor(c);
    }


    private void drawConfidentialityVerification(int confidentialityVerification, Graphics g, int _x, int _y) {
        Color c = g.getColor();
        Color c1;
		int xc=(int)(6*tdp.getZoom());
		int yc=(int)(10*tdp.getZoom());
		int lockwidth=(int) (9*tdp.getZoom());
		int lockheight=(int) (7*tdp.getZoom());
		int ovalwidth=(int) (6*tdp.getZoom());
		int ovalheight=(int) (9*tdp.getZoom());
        switch(confidentialityVerification) {
        case TAttribute.CONFIDENTIALITY_OK:
            c1 = Color.green;
            break;
        case TAttribute.CONFIDENTIALITY_KO:
            c1 = Color.red;
            break;
        case TAttribute.COULD_NOT_VERIFY_CONFIDENTIALITY:
            c1 = Color.orange;
            break;
        default:
            return;
        }

        g.drawOval(_x+xc, _y-yc, ovalwidth, ovalheight);
        g.setColor(c1);
        g.fillRect(_x+xc*2/3, _y-yc*2/3, lockwidth, lockheight);
        g.setColor(c);
        g.drawRect(_x+xc*2/3, _y-yc*2/3, lockwidth, lockheight);

    }


    public TGComponent isOnOnlyMe(int x1, int y1) {

        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
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

    public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
        int textX = (int) (this.textX * this.tdp.getZoom ());
        if (iconIsDrawn) {
            if (GraphicLib.isInRectangle(_x, _y, x + width - iconSize - textX, y + textX, iconSize, iconSize)) {
                tdp.selectTab(getValue());
                return true;
            }
        }
        // On the name ?
        if ((limitName == -1 && _y <= y + 2*currentFontSize) || _y < limitName) {
            oldValue = value;

            //String text = getName() + ": ";
            String s = (String)JOptionPane.showInputDialog(frame, "Block name",
                                                           "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
                                                           null,
                                                           getValue());

            if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
                //boolean b;
                if (!TAttribute.isAValidId(s, false, false)) {
                    JOptionPane.showMessageDialog(frame,
                                                  "Could not change the name of the Block: the new name is not a valid name",
                                                  "Error",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }

                if (!tdp.isAvatarBlockNameUnique(s)) {
                    JOptionPane.showMessageDialog(frame,
                                                  "Could not change the name of the Block: the new name is already in use",
                                                  "Error",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }

                setValue(s);
                recalculateSize();

                if (tdp.actionOnDoubleClick(this)) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(frame,
                                                  "Could not change the name of the Block: frame error",
                                                  "Error",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    setValue(oldValue);
                }
            }
            return false;
        }

        // And so -> attributes!
        int tab = 0;
        if (limitAttr != -1) {
            if (_y > limitAttr) {
                if (limitMethod == -1) {
                    tab = 2;
                } else {
                    tab = 1;
                }
            }
        }
        if (limitMethod != -1) {
            if (_y > limitMethod) {
                tab = 2;
            }
        }

        if ((limitMethod == -1) && (limitAttr == -1)) {
            if (this.mySignals.size() > 1) {
                tab = 2;
            }
        }

        String mainCode = null;
        TDiagramPanel ttdp = getTDiagramPanel();
        if (ttdp instanceof AvatarBDPanel) {
            mainCode = ((AvatarBDPanel)(ttdp)).getMainCode();
        }
        JDialogAvatarBlock jdab = new JDialogAvatarBlock(this.myAttributes, this.myMethods, this.mySignals, null, frame, "Setting attributes of " + value, "Attribute", tab, globalCode, true, mainCode);
        setJDialogOptions(jdab);
        jdab.setSize(650, 575);
        GraphicLib.centerOnParent(jdab);
        jdab.setVisible(true); // blocked until dialog has been closed
        //makeValue();
        //if (oldValue.equals(value)) {
        //return false;
        //}

        if (!jdab.hasBeenCancelled()) {
            globalCode = jdab.getGlobalCode();
            String tmp = jdab.getMainCode();
            if (tmp != null) {
                ((AvatarBDPanel)(ttdp)).setMainCode(tmp);
            }
        }

        ((AvatarBDPanel)tdp).updateAllSignalsOnConnectors();
        rescaled = true;
        return true;
    }

    protected void setJDialogOptions(JDialogAvatarBlock _jdab) {
        //jda.addAccess(TAttribute.getStringAccess(TAttribute.PUBLIC));
        _jdab.addAccess(TAttribute.getStringAccess(TAttribute.PRIVATE));
        //_jdab.addType(TAttribute.getStringType(TAttribute.NATURAL), true);
        _jdab.addType(TAttribute.getStringAvatarType(TAttribute.BOOLEAN), true);
        _jdab.addType(TAttribute.getStringAvatarType(TAttribute.INTEGER), true);
        _jdab.addType(TAttribute.getStringType(TAttribute.TIMER), false);

        for(String s: tdp.getAllDataTypes()) {
            _jdab.addType(s, false);
        }


        _jdab.enableInitialValue(true);
        _jdab.enableRTLOTOSKeyword(false);
        _jdab.enableJavaKeyword(false);
    }


    public int getType() {
        return TGComponentManager.AVATARBD_BLOCK;
    }

    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        if (tgc instanceof AvatarBDBlock || tgc instanceof AvatarBDLibraryFunction) {
            return true;
        }

        return false;
    }

    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        boolean swallowed = false;

        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof SwallowTGComponent) {
                if (tgcomponent[i].isOnMe(x, y) != null) {
                    swallowed = true;
                    ((SwallowTGComponent)tgcomponent[i]).addSwallowedTGComponent(tgc, x, y);
                    break;
                }
            }
        }

        if (swallowed) {
            return true;
        }

        if (!acceptSwallowedTGComponent(tgc)) {
            return false;
        }

        //System.out.println("Add swallow component");
        // Choose its position

        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);

        //Set its coordinates
        if (tgc instanceof AvatarBDBlock) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((AvatarBDBlock)tgc).resizeWithFather();
            //tgc.setCdRectangle(0, width - tgc.getWidth(), 0, height - tgc.getHeight());
            //tgc.setCd(x, y);
        }

        else if (tgc instanceof AvatarBDLibraryFunction)
            ((AvatarBDLibraryFunction) tgc).resizeWithFather ();

        // else unknown*/

        //add it
        addInternalComponent(tgc, 0);

        return true;
    }

    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeMyInternalComponent(tgc, false);
    }

    public boolean removeMyInternalComponent(TGComponent tgc, boolean actionOnRemove) {
        //TGComponent tgc;
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] == tgc) {
                nbInternalTGComponent = nbInternalTGComponent - 1;
                if (nbInternalTGComponent == 0) {
                    tgcomponent = null;
                } else {
                    TGComponent [] tgcomponentbis = new TGComponent[nbInternalTGComponent];
                    for(int j=0; j<nbInternalTGComponent; j++) {
                        if (j<i) {
                            tgcomponentbis[j] = tgcomponent[j];
                        }
                        if (j>=i) {
                            tgcomponentbis[j] = tgcomponent[j+1];
                        }
                    }
                    tgcomponent = tgcomponentbis;
                }
                if (actionOnRemove) {
                    tgc.actionOnRemove();
                    tdp.actionOnRemove(tgc);
                }
                return true;
            } else {
                if (((AvatarBDBlock)tgcomponent[i]).removeMyInternalComponent(tgc, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<CryptoBlock value=\"" + isCryptoBlock + "\" />\n");
        for (TAttribute a: this.myAttributes) {
            sb.append("<Attribute access=\"");
            sb.append(a.getAccess());
            sb.append("\" id=\"");
            sb.append(a.getId());
            sb.append("\" value=\"");
            sb.append(a.getInitialValue());
            sb.append("\" type=\"");
            sb.append(a.getType());
            sb.append("\" typeOther=\"");
            sb.append(a.getTypeOther());
            sb.append("\" />\n");
        }
        for (AvatarMethod am: this.myMethods) {
            sb.append("<Method value=\"");
            sb.append(am.toSaveString());
            sb.append("\" />\n");
        }
        for (AvatarSignal as: this.mySignals) {
            sb.append("<Signal value=\"");
            sb.append(as.toString());
	    sb.append("\" attached=\"");
	    sb.append(as.attachedToARelation);
            sb.append("\" />\n");
        }
        if (hasGlobalCode()) {
            for(int i=0; i<globalCode.length; i++) {
                sb.append("<globalCode value=\"");
                sb.append(GTURTLEModeling.transformString(globalCode[i]));
                sb.append("\" />\n");
            }
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{

        String s;
        String tmpGlobalCode = "";

        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            int access, type;
            String typeOther;
            String id, valueAtt;
            String method;
            String signal;
            AvatarMethod am;
            AvatarSignal as;
            boolean implementation = false;
            String crypt;
	    String attached;


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
                            if (elt.getTagName().equals("CryptoBlock")) {
                                crypt = elt.getAttribute("value");
                                if (crypt.compareTo("true") == 0) {
                                    isCryptoBlock = true;
                                }
                            }
                            if (elt.getTagName().equals("Attribute")) {
                                //System.out.println("Analyzing attribute");
                                access = Integer.decode(elt.getAttribute("access")).intValue();
                                type = Integer.decode(elt.getAttribute("type")).intValue();
                                try {
                                    typeOther = elt.getAttribute("typeOther");
                                } catch (Exception e) {
                                    typeOther = "";
                                }
                                id = elt.getAttribute("id");
                                valueAtt = elt.getAttribute("value");

                                if (valueAtt.equals("null")) {
                                    valueAtt = "";
                                }
                                if ((TAttribute.isAValidId(id, false, false)) && (TAttribute.isAValidInitialValue(type, valueAtt))) {
                                    //TraceManager.addDev("Adding attribute " + id + " typeOther=" + typeOther);
                                    if (type == TAttribute.NATURAL) {
                                        type = TAttribute.INTEGER;
                                    }
                                    TAttribute ta = new TAttribute(access, id, valueAtt, type, typeOther);
                                    ta.isAvatar = true;
                                    this.myAttributes.add (ta);
                                }
                            }
                            if (elt.getTagName().equals("Method")) {
                                //System.out.println("Analyzing attribute");
                                method = elt.getAttribute("value");

                                if (method.equals("null")) {
                                    method = "";
                                }
                                if (method.startsWith("$")) {
                                    implementation = true;
                                    method = method.substring(1, method.length());
                                } else {
                                    implementation = false;
                                }

                                if (method.startsWith("aencrypt(")) {
                                    isCryptoBlock = true;
                                }

                                am = AvatarMethod.isAValidMethod(method);
                                if (am != null) {
                                    //TraceManager.addDev("Setting to " + implementation + " the implementation of " + am);
                                    am.setImplementationProvided(implementation);
                                    this.myMethods.add(am);
                                }
                            }
                            if (elt.getTagName().equals("Signal")) {
                                //System.out.println("Analyzing attribute");
                                signal = elt.getAttribute("value");
				attached = elt.getAttribute("attached");

                                if (signal.equals("null")) {
                                    signal = "";
                                }
                                as = AvatarSignal.isAValidSignal(signal);
                                if (as != null) {
                                    this.mySignals.add(as);
				    if (attached != null) {
					as.attachedToARelation = (attached.compareTo("true") == 0);
				    }
                                } else {
                                    TraceManager.addDev("Invalid signal:" + signal);
                                }
                            }
                            if (elt.getTagName().equals("globalCode")) {
                                //System.out.println("Analyzing line");
                                s = elt.getAttribute("value");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                tmpGlobalCode += GTURTLEModeling.decodeString(s) + "\n";
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }

        if (tmpGlobalCode.trim().length() == 0) {
            globalCode = null;
        } else {
            globalCode = Conversion.wrapText(tmpGlobalCode);
        }
    }

    public String getBlockName() {
        return value;
    }

    public boolean hasGlobalCode() {
        if (globalCode == null) {
            return false;
        }

        if (globalCode.length == 0) {
            return false;
        }

        String tmp;
        for(int i=0; i<globalCode.length; i++) {
            tmp = globalCode[i].trim();
            if (tmp.length()>0) {
                if (!(tmp.equals("\n"))) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getGlobalCode() {
        if (globalCode == null) {
            return null;
        }
        String ret = "";
        for(int i=0; i<globalCode.length; i++) {
            ret += globalCode[i] + "\n";
        }
        return ret;
    }



    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarBDBlock) {
                ((AvatarBDBlock)tgcomponent[i]).resizeWithFather();
            }
        }

        if (getFather() != null) {
            resizeWithFather();
        }

    }

    public void resizeWithFather() {
        if ((father != null) && (father instanceof AvatarBDBlock)) {
            // Too large to fit in the father? -> resize it!
            resizeToFatherSize();

            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }

    public LinkedList<AvatarBDBlock> getBlockList() {
        LinkedList<AvatarBDBlock> list = new LinkedList<AvatarBDBlock>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarBDBlock) {
                list.add((AvatarBDBlock)(tgcomponent[i]));
            }
        }
        return list;
    }

    public LinkedList<AvatarBDBlock> getFullBlockList() {
        LinkedList<AvatarBDBlock> list = new LinkedList<AvatarBDBlock>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarBDBlock) {
                list.add((AvatarBDBlock)(tgcomponent[i]));
                list.addAll(((AvatarBDBlock)tgcomponent[i]).getFullBlockList());
            }
        }
        return list;
    }

    public LinkedList<AvatarBDLibraryFunction> getFullLibraryFunctionList () {
        LinkedList<AvatarBDLibraryFunction> list = new LinkedList<AvatarBDLibraryFunction> ();
        for (int i=0; i<nbInternalTGComponent; i++)
            if (this.tgcomponent[i] instanceof AvatarBDLibraryFunction)
                list.add ((AvatarBDLibraryFunction) this.tgcomponent[i]);

        return list;
    }

    public boolean hasInternalBlockWithName(String name) {
        LinkedList<AvatarBDBlock> list  = getFullBlockList();
        for(AvatarBDBlock b: list) {
            if (b.getValue().compareTo(name) ==0) {
                return true;
            }
        }
        return false;
    }



    public int getDefaultConnector() {
        return TGComponentManager.AVATARBD_PORT_CONNECTOR;
    }

    public LinkedList<TAttribute> getAttributeList() {
        return this.myAttributes;
    }

    public TAttribute getAttributeByName(String _name) {
        for (TAttribute a: this.myAttributes)
            if (a.getId().compareTo(_name) == 0)
                return a;
        return null;
    }
    public void addAttribute(TAttribute ta){
	this.myAttributes.add(ta);
    }

    public LinkedList<AvatarMethod> getMethodList() {
        return this.myMethods;
    }

    public LinkedList<AvatarSignal> getSignalList() {
        return this.mySignals;
    }

    public LinkedList<AvatarSignal> getOutSignalList() {
        LinkedList<AvatarSignal> v = new LinkedList<AvatarSignal> ();
	for(AvatarSignal s: this.mySignals)
            if (s.getInOut() == AvatarSignal.OUT)
                v.add(s);
        return v;
    }

    public LinkedList<AvatarSignal> getInSignalList() {
        LinkedList<AvatarSignal> v = new LinkedList<AvatarSignal> ();
	for(AvatarSignal s: this.mySignals)
            if (s.getInOut() == AvatarSignal.IN)
                v.add(s);
        return v;
    }

    public LinkedList<AvatarMethod> getAllMethodList() {
        if (getFather() == null) {
            return this.myMethods;
        }

        LinkedList<AvatarMethod> v = new LinkedList<AvatarMethod> ();
        v.addAll(this.myMethods);
        v.addAll(((AvatarBDBlock) getFather()).getAllMethodList());
        return v;
    }

    public LinkedList<AvatarBDLibraryFunction> getAllLibraryFunctionList() {
        LinkedList<AvatarBDLibraryFunction> list = this.getFullLibraryFunctionList ();

        if (this.getFather() == null) {
            list.addAll (((AvatarBDPanel) this.tdp).getFullLibraryFunctionList ());
            return list;
        }

        list.addAll(((AvatarBDBlock) this.getFather()).getAllLibraryFunctionList());
        return list;
    }

    public LinkedList<AvatarSignal> getAllSignalList() {
        if (getFather() == null) {
            return this.mySignals;
        }

        LinkedList<AvatarSignal> v = new LinkedList<AvatarSignal> ();
        v.addAll(this.mySignals);
        v.addAll(((AvatarBDBlock)getFather()).getAllSignalList());
        return v;
    }

    public LinkedList<String> getAllTimerList() {
        LinkedList<String> v = new LinkedList<String> ();

        for (TAttribute a: this.myAttributes)
            if (a.getType() == TAttribute.TIMER)
                v.add(a.getId());
        return v;
    }

    public AvatarSignal getAvatarSignalFromName(String _name) {
        for (AvatarSignal as: this.mySignals)
            if (as.getId().compareTo(_name) == 0)
                return as;
        return null;
    }

    public LinkedList<AvatarSignal> getListOfAvailableSignals() {
        return ((AvatarBDPanel)(tdp)).getListOfAvailableSignals(this);
    }

    public LinkedList<AvatarSignal> getListOfAvailableOutSignals() {
        return ((AvatarBDPanel)(tdp)).getListOfAvailableOutSignals(this);
    }

    public LinkedList<AvatarSignal> getListOfAvailableInSignals() {
        return ((AvatarBDPanel)(tdp)).getListOfAvailableInSignals(this);
    }


    // _id may contain the full signal
    public AvatarSignal getSignalNameBySignalDef(String _id) {
        int index0 = _id.indexOf('(');
        if (index0 > -1) {
            _id = _id.substring(0, index0);
        }
        _id = _id.trim();
        //TraceManager.addDev("Searching for signal with id=" + _id);
        for (AvatarSignal as: this.mySignals){
		//	System.out.println(as.getId() + " " 
            if (as.getId().compareTo(_id) == 0)
                return as;
		}
        //TraceManager.addDev("Not found");
        return null;
    }

    public AvatarSignal getAvatarSignalFromFullName(String _id) {
        if(_id.startsWith("in ")) {
            return getSignalNameBySignalDef(_id.substring(3, _id.length()).trim());
        }

        if(_id.startsWith("out ")) {
            return getSignalNameBySignalDef(_id.substring(4, _id.length()).trim());
        }
        return null;
    }

    public AvatarSMDPanel getAvatarSMDPanel() {
        return ((AvatarDesignPanel)(tdp.tp)).getAvatarSMDPanel(getBlockName());
    }

    public boolean isCryptoBlock() {
        return isCryptoBlock;
    }

    public void removeCryptoElements() {
        isCryptoBlock = false;

        for (String method: AvatarMethod.cryptoMethods)
            this.removeMethodIfApplicable (method);
    }

    public void addCryptoElements() {
        isCryptoBlock = true;

        for (String method: AvatarMethod.cryptoMethods)
            this.addMethodIfApplicable (method);
    }

    private void removeMethodIfApplicable(String methodString) {
        Iterator<AvatarMethod> iterator = this.myMethods.iterator ();
        while (iterator.hasNext ()) {
            AvatarMethod am = iterator.next ();
            // TODO: replace by a more OO way...
            if (am.toString ().equals (methodString)) {
                iterator.remove ();
                break;
            }
        }
    }

    public void addMethodIfApplicable (String methodString) {
        for (AvatarMethod am: this.myMethods)
            // TODO: replace by a more OO way...
            if (am.toString ().equals (methodString))
                    return;

        AvatarMethod am = AvatarMethod.isAValidMethod (methodString);
        if (am != null)
            this.myMethods.add (am);
    }

    public boolean hasDefinitions() {
        return ((this.myAttributes.size() + this.myMethods.size() + this.mySignals.size() + nbInternalTGComponent)>0);
    }

    // Main Tree

    public int getChildCount() {
        //TraceManager.addDev("Counting childs!");
        return this.myAttributes.size() + this.myMethods.size() + this.mySignals.size() + nbInternalTGComponent;
    }

    public Object getChild(int index) {

        int sa = nbInternalTGComponent;

        if (sa > index) {
            return tgcomponent[index];
        }

        index = index - nbInternalTGComponent;
        sa = this.myAttributes.size();
        //      TraceManager.addDev("index = " + index + " sa=" + sa);
        if (sa <= index) {
            index = index - sa;
            sa = this.myMethods.size();
            if (sa <= index) {
                return this.mySignals.get(index - sa);
            } else {
                return this.myMethods.get(index);
            }
        }

        return this.myAttributes.get(index);
    }

    public int getIndexOfChild(Object child) {
        if (child instanceof AvatarBDBlock) {
            for(int i=0; i<nbInternalTGComponent; i++) {
                if (tgcomponent[i] == child) {
                    return i;
                }
            }
        }

        if (child instanceof TAttribute) {
            return this.myAttributes.indexOf(child) + nbInternalTGComponent;
        }

        if (child instanceof AvatarMethod) {
            return this.myMethods.indexOf(child) + this.myAttributes.size() + nbInternalTGComponent;
        }

        if (child instanceof AvatarSignal) {
            return this.mySignals.indexOf(child) + this.myAttributes.size() + this.myMethods.size() + nbInternalTGComponent;
        }

        return -1;
    }

    public ImageIcon getImageIcon() {
        return myImageIcon;
    }

    public void resetConfidentialityOfAttributes() {
        for (TAttribute a: this.myAttributes)
            a.setConfidentialityVerification(TAttribute.NOT_VERIFIED);
    }

    @Override
    public String getOwnerName () {
        return this.getBlockName ();
    }
    
    public String toString() {
	return "Block: " + getValue();
    }
    
}
