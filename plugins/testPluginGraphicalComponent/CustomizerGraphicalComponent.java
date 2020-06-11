/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class CustomizerAvatarJavaCodeGeneration
 * Creation: 27/06/2017
 * @version 1.0 27/06/2017
 * @author Ludovic APVRILLE
 * @see
 */

import java.awt.*;
import java.net.URL;
import javax.swing.*;
     
public class CustomizerGraphicalComponent {
    public static ImageIcon myIcon;
    
    public CustomizerGraphicalComponent() {
    }

    
    public static String hasGraphicalComponent() {
	return "CustomizerGraphicalComponent";
    }

    public static String getPanelClassName() {
	return "TMLArchiDiagramPanel";
    }

    public static ImageIcon getImageIcon() {
	    URL url = CustomizerGraphicalComponent.class.getResource("myicon.gif");

	    System.out.println("URL=" + url);

        if (url != null)  {
	        myIcon = new ImageIcon(url);
            return myIcon;
        } 
	
        return null;
    }

    public static String getLongText() {
	return "Example of plugin component, and how to insert it in a toolbar";
    }

    public static String getShortText() {
	return "Example of plugin component";
    }

    
    public static String getVeryShortText() {
	return "Plugin component";
    }

    public static int getWidth() {
	return 100;
    }
    
    public static int getHeight() {
	return 50;
    }


    public boolean isMoveable() {
	return true;
    }

    public boolean isRemovable() {
	return true;
    }

    public boolean isUserResizable() {
	return true;
    }

    public boolean isEditable() {
	return true;
    }

    public String getCustomValue(String value, String diagramName) {
	return "My custom component: " + value + " in diagram:" + diagramName;
    }

    
    public int getWidth(Graphics g, String _value) {
	int w = g.getFontMetrics ().stringWidth (_value);
	return w + 20;	
    }

    public int getHeight(Graphics g, String _value) {
	return 40;
    }
    
    public void internalDrawing(Graphics g, int _x, int _y, int _width, int _height, String _value, String _diagramName) {
	g.drawRect(_x, _y, _width, _height);
	g.drawString(_value, _x+5, _y+20);
    }

    public boolean isOnMe(int _x, int _y, int _width, int _height, int _xP, int _yP) {
	if ((_xP>_x) && (_xP<_x+_width) && (_yP>_y) && (_yP<_y+_height)) {
	    return true;
	}
	return false;
    }

    public String editOnDoubleClick(JFrame _frame, String _value) {
	String s = (String)JOptionPane.showInputDialog(_frame, "My plugin component name",
						       "setting value", JOptionPane.PLAIN_MESSAGE, myIcon,
                                                           null,
                                                           _value);
	return s;
    }
    
    public static void main(String[] args) {
    }
}
