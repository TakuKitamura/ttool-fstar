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
 * @version 2.0 02/03/2016
 * @author Ludovic APVRILLE
 * @see
 */


package ui;

import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.SteppedAlgorithm;
import ui.ad.TActivityDiagramPanel;
import ui.avatarsmd.AvatarSMDPanel;
import ui.dd.TDeploymentDiagramPanel;
import ui.tmlad.TMLActivityDiagramPanel;
import ui.tmlcd.TMLTaskDiagramPanel;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.util.DefaultText;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

public class DocumentationGenerator implements SteppedAlgorithm, StoppableGUIElement {

    // type
    private Vector panels; // TURTLEPanels
    private JTabbedPane mainTabbedPane;
    private int firstHeadingNumber = 1;
    private static String title = "TTool project:";
    private String fileName = "doc.html";
    private String fileNameSvg = "docsvg.html";
    private String texFileName = "doc.tex";
    private String texIncludeFileName = "diag.tex";
    private String texFileNameSvg = "docsvg.tex";
    private String texIncludeFileNameSvg = "diagsvg.tex";
    private String path;
    private String projectName;

    private int cpt, total; // For loops -> to know at which point it is of its algorithm
    private boolean finished = false;
    private boolean go = true;
    private boolean stopped = false;
    private String panelName = "";


    private String doc;
    private String docSvg;
    private String mainLatexDoc;
    private String includeLatexDoc;
    private String mainLatexDocSvg;
    private String includeLatexDocSvg;
    


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

    public String getMainLatexDocumentation() {
        return mainLatexDoc;
    }

    public String getIncludeLatexDocumentation() {
        return includeLatexDoc;
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
	String svgImg;
        TURTLEPanel tp;
        TDiagramPanel tdp;
        File file1;
        String tmp, tmpForRef;


	mainLatexDoc = getLatexDocumentationHeader(projectName);
	includeLatexDoc = getIncludeLatexDocumentationHeader(projectName);
	mainLatexDocSvg = getLatexDocumentationHeaderSvg(projectName);
	includeLatexDocSvg = getIncludeLatexDocumentationHeaderSvg(projectName);
	
        doc = "";
        doc += "<html>\n";
        doc += getDocumentationHeader(projectName);
        doc += "<body>\n";

        doc +="<center><h1>" + title + "</h1></center>\n";
        doc +="<center><b><h1>" + projectName + "</h1></b></center>\n<br><br>\n";

	docSvg = doc;
	

        for(i=0; i<panels.size(); i++) {
            tp = (TURTLEPanel)(panels.elementAt(i));

            tmp = mainTabbedPane.getTitleAt(i);
            panelName = tmp;

            /*if (tp instanceof TMLDesignPanel) {
                tmp = "DIPLODOCUS Application Modeling: " + tmp;
            }
            if (tp instanceof TMLComponentDesignPanel) {
                tmp = "DIPLODOCUS Component-based Application Modeling: " + tmp;
            }
            if (tp instanceof TMLArchiPanel) {
                tmp = "DIPLODOCUS Architecture / Mapping Modeling: " + tmp;
		}*/
            if (tp instanceof DesignPanel) {
                tmp = "TURTLE Design";
            }
            if (tp instanceof AnalysisPanel) {
                tmp = "TURTLE Analysis";
            }
            if (tp instanceof DeploymentPanel) {
                tmp = "TURTLE Deployment";
            }

	    tmp = Conversion.replaceAllChar(tmp, '_', "\\_");

	    // HTML
            doc += "<br>\n<h" + firstHeadingNumber + ">" + tmp + "</h" + firstHeadingNumber + ">\n";
	    docSvg += "<br>\n<h" + firstHeadingNumber + ">" + tmp + "</h" + firstHeadingNumber + ">\n";

	    // Latex
	    includeLatexDoc += "\\section{" + tmp + "}\n";
	    includeLatexDocSvg += "\\section{" + tmp + "}\n";
	    
            for(j=0; j<tp.panels.size(); j++) {
                if (go == false) {
                    return false;
                }
                tdp = tp.panels.elementAt(j);

                tmp = tp.tabbedPane.getTitleAt(j);

		tmpForRef = Conversion.replaceAllChar(tmp, '_', "");
		tmpForRef += tmpForRef + i + j;
		tmp = Conversion.replaceAllChar(tmp, '_', "\\_");
		

                if (tdp instanceof TMLActivityDiagramPanel) {
                    tmp = "Behavior of Task: " + tmp;
                }

		if (tdp instanceof AvatarSMDPanel) {
                    tmp = "Behavior of Block: " + tmp;
                }

                if (tdp instanceof TActivityDiagramPanel) {
                    tmp = "Behavior of TClass: " + tmp;
                }

                if (tdp instanceof TMLTaskDiagramPanel) {
                    tmp = "Task and communications between tasks";
                }

                if (tdp instanceof TMLArchiDiagramPanel) {
                    tmp = "Architecture or Mapping of " + panelName;
		}

                if (tdp instanceof TDeploymentDiagramPanel) {
                    tmp = "";
                }

		String imgName = "img_" + i + "_" + j + ".png";
		String imgNameSvg = "Vimg_" + i + "_" + j;
		
		// HTML
                doc += "<h" + (firstHeadingNumber+1) + ">" + tmp + "</h" + (firstHeadingNumber+1) + ">\n";
		docSvg += "<h" + (firstHeadingNumber+1) + ">" + tmp + "</h" + (firstHeadingNumber+1) + ">\n";

		// Latex
		includeLatexDoc += "\\subsection{" + tmp + "}\n";
		includeLatexDoc += "Figures \\ref{fig:" + tmpForRef  + "} presents ...\n";
		includeLatexDoc += "\\begin{figure*}[htb]\n\\centering\n";
		includeLatexDoc += "\\includegraphics[width=\\textwidth]{" + imgName + "}\n";
		includeLatexDoc += "\\caption{Diagram \"" + tmp + "\"}\n\\label{fig:" + tmpForRef + "}\n\\end{figure*}\n\n"; 

		includeLatexDocSvg += "\\subsection{" + tmp + "}\n";
		includeLatexDocSvg += "Figures \\ref{fig:" + tmpForRef  + "} presents ...\n";
		includeLatexDocSvg += "\\begin{figure*}[htb]\n\\centering\n";
		includeLatexDocSvg += "\\includegraphics[width=\\textwidth]{" + imgNameSvg + "-svg.pdf}\n";
		includeLatexDocSvg += "\\caption{Diagram \"" + tmp + "\"}\n\\label{fig:" + tmpForRef + "}\n\\end{figure*}\n\n"; 
		
		// Capturing the diagram		
                image = tdp.performMinimalCapture();
		svgImg = tdp.svgCapture();
                file1 = new File(path+imgName);
		//file2 = new File(imgNameSvg);
                //frame.paint(frame.getGraphics());
                try {
                    // save captured image to PNG file
                    ImageIO.write(image, "png", file1);
		    FileUtils.saveFile(path+imgNameSvg+".svg", svgImg);
                    //doc += "<center><img src=\"img_" + i + "_" + j + ".png\" align=\"middle\" title=\"" + tmp + "\"></center>\n";
		    doc += "<center><img src=\"img_" + i + "_" + j + ".png\" align=\"middle\" title=\"" + tmp + "\"></center>\n";
		    docSvg += "<center><img src=\"img_" + i + "_" + j + ".svg\" align=\"middle\" title=\"" + tmp + "\"></center>\n";
                } catch (Exception e) {
                    System.out.println("Image (" + i + ", " + j + ") could not be captured");
                }
                cpt ++;
            }
        }

        doc+="</body>\n</html>";
	docSvg+="</body>\n</html>";

        try {
            FileUtils.saveFile(path+fileName, doc);	    
        } catch (FileException fe) {
            System.out.println("HTML file could not be saved");
            return false;
        }

	try {
            FileUtils.saveFile(path+fileNameSvg, docSvg);	    
        } catch (FileException fe) {
            System.out.println("HTML file with svg img could not be saved");
            return false;
        }

	try {
            FileUtils.saveFile(path+texFileName, mainLatexDoc);	    
        } catch (FileException fe) {
            System.out.println("Main latex file could not be saved");
            return false;
        }

	try {
            FileUtils.saveFile(path+texIncludeFileName, includeLatexDoc);	    
        } catch (FileException fe) {
            System.out.println("Include latex file could not be saved");
            return false;
        }

	
	try {
            FileUtils.saveFile(path+texFileNameSvg, mainLatexDocSvg);	    
        } catch (FileException fe) {
            System.out.println("Main latex svg file could not be saved");
            return false;
        }

	try {
            FileUtils.saveFile(path+texIncludeFileNameSvg, includeLatexDocSvg);	    
        } catch (FileException fe) {
            System.out.println("include latex svg file could not be saved");
            return false;
        }
	

        finished = true;

        return true;
    }

    public int getPercentage() {
        return (cpt*100) / total;
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

    
    public  String getLatexDocumentationHeader(String _projectName) {
        GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        String tmpdoc="";
        tmpdoc += "%----- Automatically generated by TTool version ";
        tmpdoc += DefaultText.getVersion();
        tmpdoc += " generation date: " + formattedDate;
        tmpdoc += "----\n";
	tmpdoc += "\\documentclass[11pt,a4paper]{article}\n\n\\usepackage{graphicx}\n\n\\begin{document}\n";
	tmpdoc += "\\extrafloats{750}\n";
	tmpdoc += "\\title{" + Conversion.replaceAllString(projectName, "_", "\\_") + "}\n";
	tmpdoc += "\\date{\\today}\n";
	tmpdoc += "\\maketitle\n\n";
	tmpdoc += "\\input{"+texIncludeFileName+"}\n";
	tmpdoc += "\\end{document}\n\n";
        return tmpdoc;
    }

    public  String getIncludeLatexDocumentationHeader(String _projectName) {
        GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        String tmpdoc="";
        tmpdoc += "%----- Automatically generated by TTool version ";
        tmpdoc += DefaultText.getVersion();
        tmpdoc += " generation date: " + formattedDate;
        tmpdoc += "----\n\n";
        return tmpdoc;
    }

    public  String getLatexDocumentationHeaderSvg(String _projectName) {
        GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        String tmpdoc="";
        tmpdoc += "%----- Automatically generated by TTool version ";
        tmpdoc += DefaultText.getVersion();
        tmpdoc += " generation date: " + formattedDate;
        tmpdoc += "----\n";
	tmpdoc += "% To be compiled as follows: make all. Relies on https://github.com/pacalet/mli.git\n";
	tmpdoc += "\\documentclass[11pt,a4paper]{article}\n\n\\usepackage{graphicx}\n\n\\begin{document}\n";
	tmpdoc += "\\extrafloats{750}\n";
	tmpdoc += "\\title{" + Conversion.replaceAllString(projectName, "_", "\\_") + "}\n";
	tmpdoc += "\\date{\\today}\n";
	tmpdoc += "\\maketitle\n\n";
	tmpdoc += "\\input{"+texIncludeFileNameSvg+"}\n";
	tmpdoc += "\\end{document}\n\n";
        return tmpdoc;
    }

    public  String getIncludeLatexDocumentationHeaderSvg(String _projectName) {
        GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        String tmpdoc="";
        tmpdoc += "%----- Automatically generated by TTool version ";
        tmpdoc += DefaultText.getVersion();
        tmpdoc += " generation date: " + formattedDate;
        tmpdoc += "----\n\n";
        return tmpdoc;
    }

}
