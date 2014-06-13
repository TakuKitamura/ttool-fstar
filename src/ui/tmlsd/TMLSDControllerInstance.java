/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

ludovic.apvrille AT telecom-paristech.fr
andrea.enrici AT telecom-paristech.fr

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
 * Class TMLSDControllerInstance
 * Instance of a Controller for a TML Sequence Diagram
 * Creation: 17/02/2004
 * @version 1.1 10/06/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @see
 */

package ui.tmlsd;

import java.awt.*;
import javax.swing.*;
import org.w3c.dom.*;
import java.awt.event.*;

import myutil.*;
import ui.*;
import ui.window.*;

import tmltranslator.tmlcp.*;

public class TMLSDControllerInstance extends TMLSDInstance implements SwallowTGComponent {

    public TMLSDControllerInstance(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
																	TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 10;
        height = 500;
        //textX = 0;
        //textY = 2;
        minWidth = 10;
        maxWidth = 10;
        minHeight = 250;
        maxHeight = 1500;
        
        
        makeTGConnectingPoints();
        //addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
        value = "Controller instance name";
        name = "Controller instance";
				isActor = false;
        
        myImageIcon = IconManager.imgic500;
	}
    
	public boolean editOndoubleClick(JFrame frame) {
			
		String oldValue = name;
		
		/*	JDialogSDInstance jdsdi = new JDialogSDInstance(frame, name, isActor, "Instance attributes");
      jdsdi.setSize(300, 250);
      GraphicLib.centerOnParent(jdsdi);
      jdsdi.show(); // blocked until dialog has been closed
		*/
		JDialogAttribute jda = new JDialogAttribute( myAttributes, null, frame, "Setting attributes of " + this.name, "Attribute" );
    setJDialogOptions( jda );
    jda.setSize( 650, 375 );
    GraphicLib.centerOnParent( jda );
    jda.setVisible( true ); // blocked until dialog has been closed
    //makeValue();
    //if (oldValue.equals(value)) {
	    //return false;
    //}
		/*rescaled = true;
		return true;
    }*/
		
     	String text = getName() + ": ";
      if(hasFather() ) {
        text = getTopLevelName() + " / " + text;
      }
		
			/*if( jdsdi.hasBeenUpdated() ) {
				isActor = jdsdi.isAnActor();
				String s = jdsdi.getInstanceName();
				if( s != null ) {
					s = s.trim();
				}*/
			
			String s = this.name;
			if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
				if (!TAttribute.isAValidId(s, false, false)) {
					JOptionPane.showMessageDialog( frame,
						"Could not change the name of the instance: the new name is not a valid name",
						"Error", JOptionPane.INFORMATION_MESSAGE );
					return false;
				}
				setName(s);
				TraceManager.addDev( Integer.toString( connectingPoint.length ) );
        for( int i = 0; i < connectingPoint.length; i++ ) {
				//for each connecting point connected to something
					if( connectingPoint[i].getReferenceToConnector() != null )	{
						TGConnectorMessageAsyncTMLSD connector = (TGConnectorMessageAsyncTMLSD) connectingPoint[i].getReferenceToConnector();
						if( connectingPoint[i].isSource() )	{
							connector.setStartName(s);
							TraceManager.addDev( connector.getConnectorName() );
						}
						else	{
							connector.setEndName(s);
							TraceManager.addDev( connector.getConnectorName() );
						}
					}
				}
				return true;
			}
			return false;
		}
/*        return false;
    }*/
	
	protected void setJDialogOptions( JDialogAttribute jda ) {
		
		jda.addAccess(TAttribute.getStringAccess(TAttribute.PUBLIC));
		jda.addAccess(TAttribute.getStringAccess(TAttribute.PRIVATE));
		jda.addType(TAttribute.getStringType(TAttribute.NATURAL), true);
		jda.addType(TAttribute.getStringType(TAttribute.BOOLEAN), true);
		
/*		Vector<String> records = ( (TMLComponentTaskDiagramPanel )(tdp)).getAllRecords(this);
		for( String s: records ) {
			jda.addType(s, false);
		}*/
		
		jda.enableInitialValue(true);
		jda.enableRTLOTOSKeyword(true);
		jda.enableJavaKeyword(false);
		jda.enableTMLKeyword(false);
	}

	@Override public int getType() {
		return TGComponentManager.TMLSD_CONTROLLER_INSTANCE;
	}

	public String getInstanceType()	{
		return "CONTROLLER";
	}
}	//End of class
