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
 * Class IconManager
 * Creation: 15/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.awt.*;
import myutil.*;

/**
 * Class
 *
 * @author Ludovic APVRILLE
 * @see
 */
public class ColorManager {
    
    public static final Color DEFAULT = Color.black;
    
    public static final Color NORMAL_0 = Color.black;
    public static final Color POINTER_ON_ME_0 = Color.red;
	public static final Color ACCESSIBILITY = Color.red;
	public static final Color BREAKPOINT = new Color(13, 248, 18);
	public static final Color CURRENT_COMMAND = new Color(13, 248, 18, 200);
	public static final Color DIPLOID = new Color(163, 5, 253);
    public static Color SELECTED_0 = Color.blue;
    public static final Color MOVING_0 = Color.magenta;
    public static final Color ADDING_0 = Color.lightGray;
	
    public static final Color POINTED_0 = Color.orange;
	//public static final Color POINTED_0 = new Color(231, 132, 19);
    
	//public static final Color UML_NOTE_BG = new Color(189, 91, 13, 200);
	public static final Color UML_NOTE_BG = new Color(173, 190, 234, 200);
	
    public static final Color COMPOSITION_OPERATOR = Color.yellow;
    public static final Color ATTRIBUTE_BOX = new Color(199, 243, 105);
	public static final Color ATTRIBUTE_BOX_ACTION = new Color(199, 243, 105);
    public static final Color GATE_BOX_ACTION = new Color(215, 241, 247);
    public static final Color ARRAY_BOX_ACTION = new Color(227, 215, 31);
    public static final Color GATE_BOX = new Color(215, 241, 247);
    public static final Color OPERATION_BOX = new Color(243, 207, 158);
    public static final Color ACTIVITY_BOX = new Color(255, 208, 255);
    public static final Color RESIZE_POINTED = new Color(26, 114, 244);
	//public static final Color UNKNOWN_BOX_ACTION = new Color(239, 44, 12, 125);
	public static final Color UNKNOWN_BOX_ACTION = new Color(255, 12, 27);
	
    //public static final Color RANDOM = new Color(113, 170, 155);
    //public static final Color CHOICE = new Color(255, 255, 0);
    //public static final Color FOR = new Color(255, 167, 11);
    //public static final Color EXEC = new Color(143, 149, 255);
    
    public static final Color RANDOM = new Color(199, 243, 105);
    public static final Color CHOICE = new Color(199, 243, 105);
    public static final Color FOR = new Color(199, 243, 105);
    public static final Color EXEC = new Color(199, 243, 105);


    public static final Color REQ_ATTRIBUTE_BOX = new Color(179, 249, 179);
    //public static final Color REQ_ATTRIBUTE_BOX = new Color(190, 229, 158);
    public static final Color OBS_ATTRIBUTE_BOX = new Color(225, 247, 225);
    
	public static final Color CPU_BOX_1 = new Color(198, 235, 249);
	public static final Color CPU_BOX_2 = new Color(198, 227, 249);
	//public static final Color BUS_BOX = new Color(255, 207, 114);
	public static final Color BUS_BOX = new Color(215, 188, 128);
	public static final Color BRIDGE_BOX = new Color(215, 166, 72);
	public static final Color MEMORY_BOX = new Color(172, 234, 211);
	public static final Color HWA_BOX = new Color(144, 201, 211);
        
	public static final Color TML_COMPOSITE_COMPONENT = new Color(239, 212, 176, 125);
	
	public static final Color TML_PORT_CHANNEL = new Color(104, 229, 255);
	public static final Color TML_PORT_EVENT = new Color(216, 187, 249);
	public static final Color TML_PORT_REQUEST = new Color(196, 166, 122);	
    
    public static final Color UML_NOTE = Color.lightGray;
    
    public static final Color DIAGRAM_BACKGROUND = Color.white;
	
	public static final Color MainTabbedPane = new Color(231, 214, 176);
	//public static final Color MainTabbedPaneBack = new Color(136, 94, 4);
	public static final Color MainTabbedPaneBack = new Color(9, 7, 85);
	public static final Color MainTabbedPaneSelect = new Color(231, 178, 60);
	
	public static final Color InteractiveSimulationJTABackground = new Color(50, 40, 40);
	public static final Color InteractiveSimulationJTAForeground = new Color(255, 166, 38);
	public static final Color InteractiveSimulationBackground = new Color(5, 100, 7);
	public static final Color InteractiveSimulationText = new Color(5, 100, 7);
	
	public static final Color InteractiveSimulationText_READY = new Color(5, 100, 7);
	public static final Color InteractiveSimulationText_BUSY = new Color(255, 166, 38);
	public static final Color InteractiveSimulationText_TERM = new Color(241, 6, 6);
	public static final Color InteractiveSimulationText_UNKNOWN = new Color(6, 6, 241);
    
    public final static void setColor(Graphics g, int state, int type) {
        if (type == 0) {
            switch(state) {
                case 0:
                    g.setColor(NORMAL_0);
                    break;
                case 1:
                    g.setColor(POINTER_ON_ME_0);
                    GraphicLib.setMediumStroke(g);
                    break;
                case 2:
                    g.setColor(SELECTED_0);
                    break;
                case 3:
                    g.setColor(MOVING_0);
                    break;
                case 4:
                    g.setColor(ADDING_0);
                    break;
                case 5:
                    g.setColor(POINTED_0);
                    break;
                case 6:
                    g.setColor(RESIZE_POINTED);
                    break;
                case 7:
                    g.setColor(RESIZE_POINTED);
                    break;
                default:
                    g.setColor(DEFAULT);
            }
        }
    }
    
} // Class Color



