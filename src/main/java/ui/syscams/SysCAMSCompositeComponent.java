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

package ui.syscams;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Class SysCAMSCompositeComponent
 * Composite Component. To be used in SystemC-AMS diagrams
 * Creation: 27/04/2018
 * @version 1.0 27/04/2018
 * @author Irina Kit Yan LEE
 */

public class SysCAMSCompositeComponent extends TGCScalableWithInternalComponent implements SwallowTGComponent {
	private int maxFontSize = 20;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private Color myColor;
	private int iconSize = 17;

	private int textX = 15;
	private double dtextX = 0.0;	
	
    public SysCAMSCompositeComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		initScaling(250, 200);
		
		oldScaleFactor = tdp.getZoom();
		dtextX = textX * oldScaleFactor;
		textX = (int)dtextX;
		dtextX = dtextX - textX;
		
        minWidth = 1;
        minHeight = 1;
        
        nbConnectingPoint = 0;
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
		
		maxWidth = 2000;
		maxHeight = 2000;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
		value = "cluster";
		name = "Composite component - Cluster";
		
        myImageIcon = IconManager.imgic1200;
    }
    
    public void internalDrawing(Graphics g) {
		int w;
		Font f = g.getFont();
		Font fold = f;
		
		if (myColor == null) {
			myColor = Color.white;
		}
		
		if (this.rescaled && !this.tdp.isScaled()) {
            this.rescaled = false;
            int maxCurrentFontSize = Math.max(0, Math.min(this.height, (int) (this.maxFontSize * this.tdp.getZoom())));
            f = f.deriveFont((float) maxCurrentFontSize);

            while (maxCurrentFontSize > (this.minFontSize * this.tdp.getZoom() - 1)) {
            	if (g.getFontMetrics().stringWidth(value) < (width - (2 * textX))) {
            		break;
            	}
                maxCurrentFontSize--;
                f = f.deriveFont((float) maxCurrentFontSize);
            }

            if (this.currentFontSize < this.minFontSize * this.tdp.getZoom()) {
                maxCurrentFontSize++;
                f = f.deriveFont((float) maxCurrentFontSize);
            }
            g.setFont(f);
            this.currentFontSize = maxCurrentFontSize;
        } else {
            f = f.deriveFont(this.currentFontSize);
    	}
		
		Color col = g.getColor();
		g.drawRect(x, y, width, height);
		if ((width > 2) && (height > 2)) {
			g.setColor(myColor);
			g.fillRect(x+1, y+1, width-1, height-1);
			g.setColor(col);
		}
       
        int attributeFontSize = this.currentFontSize * 5 / 6;
        g.setFont(f.deriveFont((float) attributeFontSize));
        g.setFont(f);
		w = g.getFontMetrics().stringWidth(value);
		if (!(w < (width - 2 * (iconSize + textX)))) {
			g.drawString(value, x + textX + 1, y + currentFontSize + textX);
		} else {
			g.drawString(value, x + (width - w)/2, y + currentFontSize + textX);
		}
		
		g.setFont(fold);
    }
	
	public void rescale(double scaleFactor){
		dtextX = (textX + dtextX) / oldScaleFactor * scaleFactor;
		textX = (int)(dtextX);
		dtextX = dtextX - textX; 
		super.rescale(scaleFactor);
	}
    
    public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public boolean editOnDoubleClick(JFrame frame) {
        String s = (String)JOptionPane.showInputDialog(frame, "Name:", "Setting component name",
		JOptionPane.PLAIN_MESSAGE, IconManager.imgic100,
        null,
        getValue());
        if ((s != null) && (s.length() > 0)) {
			if (!TAttribute.isAValidId(s, false, false, false)) {
				JOptionPane.showMessageDialog(frame,
					"Could not change the name of the component: the new name is not a valid name",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			if (((SysCAMSComponentTaskDiagramPanel)(tdp)).isCompositeNameUsed(s)) {
                JOptionPane.showMessageDialog(frame,
                                              "Error: the name is already in use",
                                              "Name modification",
                                              JOptionPane.ERROR_MESSAGE);
                return false;
            }
            setValueWithChange(s);
            return true;
        }
        return false;
    }
    
    public int getType() {
        return TGComponentManager.CAMS_CLUSTER;
    }
	
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
		boolean swallowed = false;
		
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SwallowTGComponent) {
				if (((SwallowTGComponent)tgcomponent[i]).acceptSwallowedTGComponent(tgc)) {
					if (tgcomponent[i].isOnMe(x, y) != null) {
						swallowed = true;
						((SwallowTGComponent)tgcomponent[i]).addSwallowedTGComponent(tgc, x, y);
						break;
					}
				}
			}
        }
		if (swallowed) {
			return true;
		}
		if (!acceptSwallowedTGComponent(tgc)) {
			return false;
		}
        tgc.setFather(this);
        tgc.setDrawingZone(true);
        
		if (tgc instanceof SysCAMSBlockTDF) {
            tgc.resizeWithFather();
        }
		if (tgc instanceof SysCAMSBlockDE) {
			tgc.resizeWithFather();
		}

		if (tgc instanceof SysCAMSClock) {
			tgc.resizeWithFather();
		}	
        addInternalComponent(tgc, 0);
		return true;
    }
    
    public void removeSwallowedTGComponent(TGComponent tgc) {
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
				break;
			}
		 }
    }
    
    public void hasBeenResized() {
		rescaled = true;
        for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SysCAMSBlockTDF) {
				tgcomponent[i].resizeWithFather();
			}
			if (tgcomponent[i] instanceof SysCAMSBlockDE) {
				tgcomponent[i].resizeWithFather();
			}
				if (tgcomponent[i] instanceof SysCAMSClock) {
				tgcomponent[i].resizeWithFather();
			}
        }
    }
	
	public java.util.List<SysCAMSBlockTDF> getAllBlockTDFComponents() {
		ArrayList<SysCAMSBlockTDF> ll = new ArrayList<SysCAMSBlockTDF>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SysCAMSCompositeComponent) {
				ll.addAll(((SysCAMSCompositeComponent)tgcomponent[i]).getAllBlockTDFComponents());
			}
			if (tgcomponent[i] instanceof SysCAMSBlockTDF) {
				ll.add(((SysCAMSBlockTDF)(tgcomponent[i])));
			}
		}
		return ll;
	}
	
	public java.util.List<SysCAMSBlockDE> getAllBlockDEComponents() {
		ArrayList<SysCAMSBlockDE> ll = new ArrayList<SysCAMSBlockDE>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SysCAMSCompositeComponent) {
				ll.addAll(((SysCAMSCompositeComponent)tgcomponent[i]).getAllBlockDEComponents());
			}
			if (tgcomponent[i] instanceof SysCAMSBlockDE) {
				ll.add(((SysCAMSBlockDE)(tgcomponent[i])));
			}
		}
		return ll;
	}

    	public java.util.List<SysCAMSClock> getAllClockComponents() {
		ArrayList<SysCAMSClock> ll = new ArrayList<SysCAMSClock>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SysCAMSCompositeComponent) {
				ll.addAll(((SysCAMSCompositeComponent)tgcomponent[i]).getAllClockComponents());
			}
			if (tgcomponent[i] instanceof SysCAMSClock) {
				ll.add(((SysCAMSClock)(tgcomponent[i])));
			}
		}
		return ll;
	}
	
	public void getAllCompositeComponents(ArrayList<String> list, String _name) {
		String s;
		SysCAMSCompositeComponent syscamscc;
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SysCAMSCompositeComponent) {
				syscamscc = (SysCAMSCompositeComponent)tgcomponent[i];
				s = _name + "::" + syscamscc.getValue();
				list.add(s);
				syscamscc.getAllCompositeComponents(list, _name);
			}
		}
	}
	
	public ArrayList<SysCAMSPortTDF> getAllInternalPortsTDF() {
		ArrayList<SysCAMSPortTDF> list = new ArrayList<SysCAMSPortTDF>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SysCAMSCompositeComponent) {
				list.addAll(((SysCAMSCompositeComponent)tgcomponent[i]).getAllInternalPortsTDF());
			}
			if (tgcomponent[i] instanceof SysCAMSBlockTDF) {
				list.addAll(((SysCAMSBlockTDF)tgcomponent[i]).getAllInternalPortsTDF());
			}
		}
		return list;
	}
	
	public SysCAMSBlockTDF getBlockTDFComponentByName(String _name) {
		SysCAMSBlockTDF tgc;
		ListIterator<SysCAMSBlockTDF> li = getAllBlockTDFComponents().listIterator();
		
		while(li.hasNext()) {
			tgc = li.next();
			if (tgc.getValue().equals(_name)) {
				return tgc;
			}
		}
		return null;
	}
	
	public SysCAMSCompositeComponent getCompositeComponentByName(String _name) {
		TGComponent tgc;
		SysCAMSCompositeComponent tmp;
        
       for(int i=0; i<nbInternalTGComponent; i++) {
            tgc = tgcomponent[i];
            if (tgc instanceof SysCAMSCompositeComponent) {
				tmp = (SysCAMSCompositeComponent)tgc;
				if (tmp.getValue().equals(_name)) {
					return tmp;
				}
				
				if ((tmp = tmp.getCompositeComponentByName(name)) != null) {
					return tmp;
				}
            }
        }
		return null;
	}
	
	public void drawTGConnectingPoint(Graphics g, int type) {
        for (int i=0; i<nbConnectingPoint; i++) {
            if (connectingPoint[i].isCompatibleWith(type)) {
                connectingPoint[i].draw(g);
            }
        }
		
        for(int i=0; i<nbInternalTGComponent; i++) {
			tgcomponent[i].drawTGConnectingPoint(g, type);
        }
    }
	
	public String getExtendedValue() {
		return getValuePanel() + "::" + getValue();
	}
	
	public void myActionWhenRemoved() {
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SysCAMSCompositeComponent) {
				tgcomponent[i].myActionWhenRemoved();
			}
		}
		tdp = null;
	}
	
	public void updateReferenceToSysCAMSCompositeComponent(SysCAMSCompositeComponent syscamscc) {
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SysCAMSCompositeComponent) {
				((SysCAMSCompositeComponent)tgcomponent[i]).updateReferenceToSysCAMSCompositeComponent(syscamscc);
			}
		}
	}
	
	public void delayedLoad() {
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SysCAMSCompositeComponent) {
				((SysCAMSCompositeComponent)tgcomponent[i]).delayedLoad();
			}
		}
	}
	
	public boolean hasRefencesTo(SysCAMSCompositeComponent syscamscc) {
		boolean b;
		
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SysCAMSCompositeComponent) {
				b = ((SysCAMSCompositeComponent)tgcomponent[i]).hasRefencesTo(syscamscc);
				if (b) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean acceptSwallowedTGComponent(TGComponent tgc) {
    	if (tgc instanceof SysCAMSBlockTDF) {
    		return true;
    	} else if (tgc instanceof SysCAMSBlockDE) {
    		return true;
    	}
	 else if (tgc instanceof SysCAMSClock) {
    		return true;
    	}
    	return false;
	}
}
