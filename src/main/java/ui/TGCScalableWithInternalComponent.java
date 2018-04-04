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

package ui;

import myutil.TraceManager;

/**
   * Class TGCScalableWithInternalComponent
   * Graphical component that contains one or more internal components, and which is scalable
   * Creation: 10/03/2008
   * @version 1.0 10/03/2008
   * @author Ludovic APVRILLE
 */
public abstract class TGCScalableWithInternalComponent extends TGCWithInternalComponent implements ScalableTGComponent {
    protected boolean rescaled = false;
    protected double oldScaleFactor;

    public TGCScalableWithInternalComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
    }

    @Override
    public void rescale(double scaleFactor){
        /*TraceManager.addDev("rescaling for " + this.getClass() + " with oldScaleFactor=" + oldScaleFactor +
                " new scale factor = " + scaleFactor + " dx=" + dx + " dy=" + dy +
                " x=" + x + " y=" + y + " width=" + width + " height=" + height);*/
        rescaled = true;

        dwidth = (width + dwidth) / oldScaleFactor * scaleFactor;
        dheight = (height + dheight) / oldScaleFactor * scaleFactor;
        dx = (dx + x) / oldScaleFactor * scaleFactor;
        dy = (dy + y) / oldScaleFactor * scaleFactor;
        dMinWidth = (minWidth + dMinWidth) / oldScaleFactor * scaleFactor;
        dMinHeight = (minHeight + dMinHeight) / oldScaleFactor * scaleFactor;
        dMaxWidth = (maxWidth + dMaxWidth) / oldScaleFactor * scaleFactor;
        dMaxHeight = (maxHeight + dMaxHeight) / oldScaleFactor * scaleFactor;

        width = (int)(dwidth);
        dwidth = dwidth - width;
        height = (int)(dheight);
        dheight = dheight - height;
        minWidth = (int)(dMinWidth);
        minHeight = (int)(dMinHeight);
        maxWidth = (int)(dMaxWidth);
        maxHeight = (int)(dMaxHeight);

	//TraceManager.addDev("tgc= " + this + " minHeight=" + minHeight);
	//TraceManager.addDev("tgc= " + this + " maxHeight=" + maxHeight + " height=  " + height);

        dMinWidth = dMinWidth - minWidth;
        dMinHeight = dMinHeight - minHeight;
        dMaxWidth = dMaxWidth - maxWidth;
        dMaxHeight = dMaxHeight - maxHeight;
        x = (int)(dx);
        dx = dx - x;
        y = (int)(dy);
        dy = dy - y;

        //TraceManager.addDev("x=" + x + " y=" + y + " width=" + width + " height=" + height);

        oldScaleFactor = scaleFactor;

        if (father != null) {
            // Must rescale my zone...
            resizeWithFather();
        }

        setMoveCd(x, y, true);

        //TraceManager.addDev("x=" + x + " y=" + y + " width=" + width + " height=" + height);

        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof ScalableTGComponent) {
                ((ScalableTGComponent)tgcomponent[i]).rescale(scaleFactor);
            }
        }

        hasBeenResized();
    }

    public void initScaling(int w, int h) {
        oldScaleFactor = tdp.getZoom();

        dx = 0;
        dy = 0;

        dwidth = w * oldScaleFactor;
        width = (int)dwidth;
        dwidth = dwidth - width;

        dheight = h * oldScaleFactor;
        height = (int)(dheight);
        dheight = dheight - height;

        dMaxWidth = defMaxWidth * oldScaleFactor;
        dMaxHeight = defMaxHeight * oldScaleFactor;

        maxWidth = defMaxWidth;
        maxHeight = defMaxHeight;

        dMaxWidth = dMaxWidth - maxWidth;
        dMaxHeight = dMaxHeight - maxHeight;

        rescaled = true;
    }

}
