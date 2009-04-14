/**Copyright or � or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
 * Class TMLArchiCPUNode
 * Node. To be used in TML architecture diagrams.
 * Creation: 02/05/2005
 * @version 1.1 21/05/2008
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

public class TMLArchiCPUNode extends TMLArchiNode implements SwallowTGComponent, WithAttributes {
    private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 2;
    private int derivationy = 3;
    private String stereotype = "CPU";
	
	private int byteDataSize = HwCPU.DEFAULT_BYTE_DATA_SIZE;
	private int pipelineSize = HwCPU.DEFAULT_PIPELINE_SIZE;
	private int goIdleTime = HwCPU.DEFAULT_GO_IDLE_TIME;
	private int maxConsecutiveIdleCycles = HwCPU.DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES;
	private int taskSwitchingTime = HwCPU.DEFAULT_TASK_SWITCHING_TIME;
	private int branchingPredictionPenalty = HwCPU.DEFAULT_BRANCHING_PREDICTION_PENALTY;
	private int schedulingPolicy = HwCPU.DEFAULT_SCHEDULING;
	private int execiTime = HwCPU.DEFAULT_EXECI_TIME;
	private int execcTime = HwCPU.DEFAULT_EXECC_TIME;
	private int cacheMiss = HwCPU.DEFAULT_CACHE_MISS;
    
    public TMLArchiCPUNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 250;
        height = 200;
        minWidth = 150;
        minHeight = 100;
        
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];
        
        connectingPoint[0] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.0);
        connectingPoint[1] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.5, 0.0);
        connectingPoint[2] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.0);
        connectingPoint[3] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.5);
        connectingPoint[4] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.5);
        connectingPoint[5] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 1.0);
        connectingPoint[6] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.5, 1.0);
        connectingPoint[7] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 1.0);
        
        connectingPoint[8] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.25, 0.0);
        connectingPoint[9] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.75, 0.0);
        connectingPoint[10] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.25);
        connectingPoint[11] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.25);
        connectingPoint[12] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.75);
        connectingPoint[13] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.75);
        connectingPoint[14] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.25, 1.0);
        connectingPoint[15] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.75, 1.0);
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
        name = tdp.findNodeName("CPU");
		value = "name";
        
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
		g.setColor(ColorManager.CPU_BOX_1);
		g.fill3DRect(x+1, y+1, width-1, height-1, true);
		g.setColor(c);
        
        // Strings
        String ster = "<<" + stereotype + ">>";
        int w  = g.getFontMetrics().stringWidth(ster);
		Font f = g.getFont();
		g.setFont(f.deriveFont(Font.BOLD));
        g.drawString(ster, x + (width - w)/2, y + textY1);
		g.setFont(f);
        w  = g.getFontMetrics().stringWidth(name);
        g.drawString(name, x + (width - w)/2, y + textY2);
		
		// Icon
		g.drawImage(IconManager.imgic1100.getImage(), x + 4, y + 4, null);
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
    
    public boolean editOndoubleClick(JFrame frame) {
		boolean error = false;
		String errors = "";
		int tmp;
		String tmpName;
        
		JDialogCPUNode dialog = new JDialogCPUNode(frame, "Setting CPU attributes", this);
		dialog.setSize(500, 450);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		if (dialog.getNodeName().length() != 0) {
			tmpName = dialog.getNodeName();
			tmpName = tmpName.trim();
			 if (!TAttribute.isAValidId(tmpName, false, false)) {
                error = true;
				errors += "Name of the node  ";
			 } else {
				 name = tmpName;
			 }
		}
		
		schedulingPolicy = dialog.getSchedulingPolicy();
		if (schedulingPolicy == HwCPU.BASIC_ROUND_ROBIN) {
			stereotype = "CPURR";
		}
		
		if (schedulingPolicy == HwCPU.ROUND_ROBIN_PRIORITY_BASED) {
			stereotype = "CPURRPB";
		}
		
		if (dialog.getByteDataSize().length() != 0) {	
			try {
				tmp = byteDataSize;
				byteDataSize = Integer.decode(dialog.getByteDataSize()).intValue();
				if (byteDataSize <= 0) {
					byteDataSize = tmp;
					error = true;
					errors += "Data size  ";
				}
			} catch (Exception e) {
				error = true;
				errors += "Data size  ";
			}
		}
		
		if (dialog.getPipelineSize().length() != 0) {	
			try {
				tmp = pipelineSize;
				pipelineSize = Integer.decode(dialog.getPipelineSize()).intValue();
				if (pipelineSize <= 0) {
					pipelineSize = tmp;
					error = true;
					errors += "Pipeline size  ";
				}
			} catch (Exception e) {
				error = true;
				errors += "Pipeline size  ";
			}
		}
		
		if (dialog.getGoIdleTime().length() != 0) {	
			try {
				tmp = goIdleTime;
				goIdleTime = Integer.decode(dialog.getGoIdleTime()).intValue();
				if (goIdleTime < 0) {
					goIdleTime = tmp;
					error = true;
					errors += "Go idle time  ";
				}
			} catch (Exception e) {
				error = true;
				errors += "Go idle time  ";
			}
		}
		
		if (dialog.getMaxConsecutiveIdleCycles().length() != 0) {	
			try {
				tmp = goIdleTime;
				maxConsecutiveIdleCycles = Integer.decode(dialog.getMaxConsecutiveIdleCycles()).intValue();
				if (maxConsecutiveIdleCycles < 0) {
					maxConsecutiveIdleCycles = tmp;
					error = true;
					errors += "Max consecutive idle cycles  ";
				}
			} catch (Exception e) {
				error = true;
				errors += "Max consecutive idle cycles  ";
			}
		}
		
		if (dialog.getExeciTime().length() != 0) {	
			try {
				tmp = execiTime;
				execiTime = Integer.decode(dialog.getExeciTime()).intValue();
				if (execiTime < 0) {
					execiTime = tmp;
					error = true;
					errors += "execi time  ";
				}
			} catch (Exception e) {
				error = true;
				errors += "execi time  ";
			}
		}
		
		if (dialog.getExeccTime().length() != 0) {	
			try {
				tmp = execcTime;
				execcTime = Integer.decode(dialog.getExeccTime()).intValue();
				if (execcTime < 0) {
					execcTime = tmp;
					error = true;
					errors += "execc time  ";
				}
			} catch (Exception e) {
				error = true;
				errors += "execc time  ";
			}
		}
		
		if (dialog.getTaskSwitchingTime().length() != 0) {	
			try {
				tmp = taskSwitchingTime;
				taskSwitchingTime = Integer.decode(dialog.getTaskSwitchingTime()).intValue();
				if (taskSwitchingTime <= 0) {
					taskSwitchingTime = tmp;
					error = true;
					errors += "Task switching time  ";
				}
			} catch (Exception e) {
				error = true;
				errors += "Task switching time  ";
			}
		}
		
		if (dialog.getBranchingPredictionPenalty().length() != 0) {	
			try {
				tmp = branchingPredictionPenalty;
				branchingPredictionPenalty = Integer.decode(dialog.getBranchingPredictionPenalty()).intValue();
				if ((branchingPredictionPenalty < 0) || (branchingPredictionPenalty > 100)) {
					branchingPredictionPenalty = tmp;
					error = true;
					errors += "Mis-branching prediction  ";
				}
			} catch (Exception e) {
				error = true;
				errors += "Mis-branching prediction  ";
			}
		}
		
		if (dialog.getCacheMiss().length() != 0) {	
			try {
				tmp = cacheMiss;
				cacheMiss = Integer.decode(dialog.getCacheMiss()).intValue();
				if ((cacheMiss < 0) || (cacheMiss > 100)) {
					cacheMiss = tmp;
					error = true;
					errors += "Cache-miss  ";
				}
			} catch (Exception e) {
				error = true;
				errors += "Cache-miss  ";
			}
		}
		
		if (dialog.getClockRatio().length() != 0) {	
			try {
				tmp = clockRatio;
				clockRatio = Integer.decode(dialog.getClockRatio()).intValue();
				if (clockRatio < 1) {
					clockRatio = tmp;
					error = true;
					errors += "Clock ratio  ";
				}
			} catch (Exception e) {
				error = true;
				errors += "Clock ratio  ";
			}
		}
		
		if (error) {
			JOptionPane.showMessageDialog(frame,
                "Invalid value for the following attributes: " + errors,
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
		}
		
        return true;
    }
    
    
    public int getType() {
        return TGComponentManager.TMLARCHI_CPUNODE;
    }
    
    public void addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        //System.out.println("Add swallow component");
        // Choose its position
        
        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);
        
        //Set its coordinates
        if (tgc instanceof TMLArchiArtifact) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((TMLArchiArtifact)tgc).resizeWithFather();
            //tgc.setCdRectangle(0, width - tgc.getWidth(), 0, height - tgc.getHeight());
            //tgc.setCd(x, y);
        }
        
        // else unknown*/
        
        //add it
        addInternalComponent(tgc, 0);
    }
    
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }
    
    
    public Vector getArtifactList() {
        Vector v = new Vector();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TMLArchiArtifact) {
                v.add(tgcomponent[i]);
            }
        }
        return v;
    }
    
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TMLArchiArtifact) {
                ((TMLArchiArtifact)tgcomponent[i]).resizeWithFather();
            }
        }
        
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info stereotype=\"" + stereotype + "\" nodeName=\"" + name);
        sb.append("\" />\n");
		sb.append("<attributes byteDataSize=\"" + byteDataSize + "\" ");
		sb.append(" schedulingPolicy=\"" + schedulingPolicy + "\" ");
		sb.append(" goIdleTime=\"" + goIdleTime + "\" ");
		sb.append(" maxConsecutiveIdleCycles=\"" + maxConsecutiveIdleCycles + "\" ");
		sb.append(" pipelineSize=\"" + pipelineSize + "\" ");
		sb.append(" taskSwitchingTime=\"" + taskSwitchingTime + "\" ");
		sb.append(" branchingPredictionPenalty=\"" + branchingPredictionPenalty + "\" ");
		sb.append(" cacheMiss=\"" + cacheMiss + "\"");
		sb.append(" execiTime=\"" + execiTime + "\"");
		sb.append(" execcTime=\"" + execcTime + "\"");     
		sb.append(" clockRatio=\"" + clockRatio + "\"");
        sb.append("/>\n");
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
                                byteDataSize = Integer.decode(elt.getAttribute("byteDataSize")).intValue();
                                schedulingPolicy =Integer.decode(elt.getAttribute("schedulingPolicy")).intValue();
								goIdleTime = Integer.decode(elt.getAttribute("goIdleTime")).intValue();
								pipelineSize = Integer.decode(elt.getAttribute("pipelineSize")).intValue();
								taskSwitchingTime = Integer.decode(elt.getAttribute("taskSwitchingTime")).intValue();
								branchingPredictionPenalty = Integer.decode(elt.getAttribute("branchingPredictionPenalty")).intValue();
								if ((elt.getAttribute("cacheMiss") != null) &&  (elt.getAttribute("cacheMiss").length() > 0)){
									cacheMiss = Integer.decode(elt.getAttribute("cacheMiss")).intValue();
								}
								if ((elt.getAttribute("execiTime") != null) &&  (elt.getAttribute("execiTime").length() > 0)){
									execiTime = Integer.decode(elt.getAttribute("execiTime")).intValue();
								}
								if ((elt.getAttribute("execcTime") != null) &&  (elt.getAttribute("execcTime").length() > 0)){
									execcTime = Integer.decode(elt.getAttribute("execcTime")).intValue();
								}
								if ((elt.getAttribute("maxConsecutiveIdleCycles") != null) &&  (elt.getAttribute("maxConsecutiveIdleCycles").length() > 0)){
									maxConsecutiveIdleCycles = Integer.decode(elt.getAttribute("maxConsecutiveIdleCycles")).intValue();
								}
								if ((elt.getAttribute("clockRatio") != null) &&  (elt.getAttribute("clockRatio").length() > 0)){
									clockRatio = Integer.decode(elt.getAttribute("clockRatio")).intValue();
								}
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
    
   	public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_NODE_TMLARCHI;
      }
	  
	  public int getByteDataSize(){
		  return byteDataSize;
	  }
	  
	  public int getPipelineSize(){
		  return pipelineSize;
	  }
	  
	  public int getGoIdleTime(){
		  return goIdleTime;
	  }
	  
	  public int getMaxConsecutiveIdleCycles(){
		  return maxConsecutiveIdleCycles;
	  }
	  
	  public int getExeciTime(){
		  return execiTime;
	  }
	  
	  public int getExeccTime(){
		  return execcTime;
	  }
	  
	  public int getTaskSwitchingTime(){
		  return taskSwitchingTime;
	  }
	  
	  public int getBranchingPredictionPenalty(){
		  return branchingPredictionPenalty;
	  }  
	  
	  public int getCacheMiss(){
		  return cacheMiss;
	  }  
	  
	  public int getSchedulingPolicy(){
		  return schedulingPolicy;
	  }
	  
	  public String getAttributes() {
		  String attr = "";
		  attr += "Data size (in byte) = " + byteDataSize + "\n";
		  attr += "Pipeline size = " + pipelineSize + "\n";
		  if (schedulingPolicy == HwCPU.DEFAULT_SCHEDULING) {
			  attr += "Scheduling policy = basic Round Robin\n";
		  }
		  attr += "Task switching time (in cycle) = " + taskSwitchingTime + "\n";
		  attr += "Go in idle mode (in cycle) = " + goIdleTime + "\n";
		  attr += "Max consecutive idle cycles before going in idle mode (in cycle) = " + maxConsecutiveIdleCycles + "\n";
		  attr += "EXECI execution time (in cycle) = " + execiTime + "\n";
		  attr += "EXECC execution time (in cycle) = " + execcTime + "\n";
		  attr += "Branching prediction misrate (in %) = " + branchingPredictionPenalty + "\n";
		  attr += "Cache miss (in %) = " + cacheMiss + "\n";
		   attr += "Clock ratio = " + clockRatio + "\n";
		  return attr;
		  
	  }
    
}
