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
 * Class DocumentationGenerator
 * Generation of documentation from TTool diagrams
 * Creation: 19/09/2008
 * @version 1.0 19/09/2008
 * @author Ludovic APVRILLE
 * @see
 */


package ui;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.text.*;

import myutil.*;
import ui.ad.*;
import ui.dd.*;
import ui.tmlad.*;
import ui.tmlcd.*;
import ui.tmldd.*;

public class DocumentationGenerator implements SteppedAlgorithm, StoppableGUIElement {
    
    // type
    private Vector panels; // TURTLEPanels
	private JTabbedPane mainTabbedPane;
	private int firstHeadingNumber = 1;
	private static String title = "TTool project:";
	private String fileName = "doc.html";
	private String path;
	private String projectName;
	
	private int cpt, total; // For loops -> to know at which point it is of its algorithm
	private boolean finished = false;
	private boolean go = true;
	private boolean stopped = false;
	private String panelName = "";
	
	
	private String doc;
	
    
    public DocumentationGenerator(Vector _panels, JTabbedPane _mainTabbedPane, String _path, String _projectName) {
		panels = _panels;
		mainTabbedPane = _mainTabbedPane;
		path = _path + "/";
		projectName = _projectName;
		
		int i,j;
		for(i=0; i<panels.size(); i++) {
			TURTLEPanel tp = (TURTLEPanel)(panels.elementAt(i));
			total += tp.panels.size();
		}
    }
	
	public void setFirstHeadingNumber(int _firstHeadingNumber) {
		firstHeadingNumber = _firstHeadingNumber;
	}
	
	public String getDocumentation() {
		return doc;
	}
	
	public void setTitle(String _title) {
		title = _title;
	}
	
	public static String getTitle() {
		return title;
	}
	
	public void goElement() {
		finished = false;
		go = true;
	}
	
	public void stopElement(){
		go = false;
	}
	
	public boolean hasFinished() {
		return (finished == true);
	}
	
	public void setFinished() {
		finished = true;
		go = false;
	}
	
	public boolean hasBeenStopped() {
		return (stopped == true);
	}
	
	public String getCurrentActivity() {
		return "Generating documentation for " + panelName;
	}
	
	public boolean generateDocumentation() {
		int i,j;
		cpt = 0;
		BufferedImage image;
		TURTLEPanel tp;
		TDiagramPanel tdp;
		File file1;
		String tmp;
		
		doc = "";
		doc += "<html>\n";
		doc += getDocumentationHeader(projectName);
		doc += "<body>\n";
		
		doc +="<center><h1>" + title + "</h1></center>\n";
		doc +="<center><b><h1>" + projectName + "</h1></b></center>\n<br><br>\n";
		
		for(i=0; i<panels.size(); i++) {
			tp = (TURTLEPanel)(panels.elementAt(i));
			
			tmp = mainTabbedPane.getTitleAt(i);
			panelName = tmp;
			
			if (tp instanceof TMLDesignPanel) {
				tmp = "DIPLODOCUS Application Modeling: " + tmp; 
			}
			if (tp instanceof TMLComponentDesignPanel) {
				tmp = "DIPLODOCUS Component-based Application Modeling: " + tmp; 
			}
			if (tp instanceof TMLArchiPanel) {
				tmp = "DIPLODOCUS Architecture / Mapping Modeling: " + tmp; 
			}
			if (tp instanceof DesignPanel) {
				tmp = "TURTLE Design";
			}
			if (tp instanceof AnalysisPanel) {
				tmp = "TURTLE Analysis";
			}
			if (tp instanceof DeploymentPanel) {
				tmp = "TURTLE Deployment";
			}		
			
			doc += "<br>\n<h" + firstHeadingNumber + ">" + tmp + "</h" + firstHeadingNumber + ">\n";
			for(j=0; j<tp.panels.size(); j++) {
				if (go == false) {
					return false;
				}
				tdp = (TDiagramPanel)(tp.panels.elementAt(j));
				
				tmp = tp.tabbedPane.getTitleAt(j);
				
				if (tdp instanceof TMLActivityDiagramPanel) {
					tmp = "Behavior of Task: " + tmp; 
				}
				
				if (tdp instanceof TActivityDiagramPanel) {
					tmp = "Behavior of TClass: " + tmp; 
				}
				
				if (tdp instanceof TMLTaskDiagramPanel) {
					tmp = "Task and communications between tasks";
				}
				
				if (tdp instanceof TMLArchiDiagramPanel) {
					tmp = "";
				}
				
				if (tdp instanceof TDeploymentDiagramPanel) {
					tmp = "";
				}
				
				doc += "<h" + (firstHeadingNumber+1) + ">" + tmp + "</h" + (firstHeadingNumber+1) + ">\n";
				image = tdp.performMinimalCapture();
				file1 = new File(path + "img_" + i + "_" + j + ".png");
				//frame.paint(frame.getGraphics());
				try {
					// save captured image to PNG file
					ImageIO.write(image, "png", file1);
					doc += "<center><img src=\"img_" + i + "_" + j + ".png\" align=\"middle\" title=\"" + tmp + "\"></center>\n";
				} catch (Exception e) {
					System.out.println("Image (" + i + ", " + j + ") could not be captured");
				}
				cpt ++;
			}
		}
		
		doc+="</body>\n</html>";
		
		try {
			FileUtils.saveFile(path+fileName, doc);
		} catch (FileException fe) {
			System.out.println("HTML file could not be saved");
			return false;
		}
		
		finished = true;
		
		return true;
	}
	
	public int getPercentage() {
		return (int)((cpt*100) / total);
	}
	
	public  String getDocumentationHeader(String _projectName) {
		GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
		Date date = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		String formattedDate = formatter.format(date);

		String tmpdoc="";
		tmpdoc += "<!----- Automatically generated by TTool version ";
		tmpdoc += DefaultText.getVersion();
		tmpdoc += " generation date: " + formattedDate;
		tmpdoc += "---->\n";
		tmpdoc += "\n<head>\n<title>";
		tmpdoc += getTitle() + ": " +_projectName;
		tmpdoc += "</title>\n</head>\n";
		return tmpdoc;
	}
    
   
}