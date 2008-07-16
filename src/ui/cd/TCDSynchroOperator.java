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
 * Class TCDSynchroOperator
 * Sequence composition operator
 * To be used in class diagrams
 * Creation: 12/12/2003
 * @version 1.0 12/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.cd;

import java.awt.*;


import ui.*;

public class TCDSynchroOperator extends TCDCompositionOperatorWithSynchro {
    protected TClassSynchroInterface oldt1;
    protected TClassSynchroInterface oldt2;
    
    public TCDSynchroOperator(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        nbInternalTGComponent = 1;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        TCDSynchroGateList tgc = new TCDSynchroGateList(x, y+40, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, this, _tdp);
        tgc.setValue("{synchronization gates}");
        tgc.setDefaultValue("{synchronization gates}");
        tgc.setName("OCL formula listing all synchronization gates");
        tgc.setMoveWithFather(false);
        tgcomponent[0] = tgc;
        
        name = "synchro composition operator";
        value = "Synchro";
        
        myImageIcon = IconManager.imgic110;
    }
    
    public void internalDrawing(Graphics g) {
        g.drawRect(x, y, width, height);
        g.setColor(ColorManager.COMPOSITION_OPERATOR);
        g.fillRect(x+1, y+1, width-1, height-1);
        g.drawImage(IconManager.img8, x + width - 20, y + 3, ColorManager.COMPOSITION_OPERATOR, null);
        ColorManager.setColor(g, getState(), 0);
        g.setFont((g.getFont()).deriveFont(Font.BOLD));
        g.drawString(value, x + textX, y + textY);
        g.setFont((g.getFont()).deriveFont(Font.PLAIN));
    }
    
    public int getType() {
        return TGComponentManager.TCD_SYNCHRO_OPERATOR;
    }
    
    public void structureChanged() {
        if (tdp instanceof TClassDiagramPanel) {
            t1 = ((TClassDiagramPanel)tdp).getTClass1ToWhichIamConnected(this);
            t2 = ((TClassDiagramPanel)tdp).getTClass2ToWhichIamConnected(this);
            if ((t1 != oldt1) || (t2 != oldt2)) {
                oldt1 = t1;
                oldt2 = t2;
                ((TCDSynchroGateList)tgcomponent[0]).setTClass(t1, t2);
                if ((t1 != null) && (t2 != null)) {
                    setName("synchro composition operator between " + t1.getValue() + " and " + t2.getValue());
                }	 else {
                    setName("synchro composition operator");
                }
            }
        }
    }
    
}
