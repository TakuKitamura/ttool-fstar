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

import common.SpecConfigTTool;
import myutil.*;
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
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

/**
 * Class DocumentationGenerator
 * Generation of documentation from TTool diagrams
 * Creation: 19/09/2008
 *
 * @author Ludovic APVRILLE
 * @version 2.0 02/03/2016
 */
public class DocumentationGenerator implements SteppedAlgorithm, StoppableGUIElement {

    // type
    private Vector<TURTLEPanel> panels; // TURTLEPanels
    private JTabbedPane mainTabbedPane;
    private int firstHeadingNumber = 1;
    private static String title = "TTool project:";
    private String fileName = "doc.html";
    private String fileNameSvg = "docsvg.html";
    private String texFileName = "doc.tex";
    private String texIncludeFileName = "diag.tex";
    private String texFileNameSvg = "docsvg.tex";
    private String texIncludeFileNameSvg = "diagsvg.tex";
    private final String path;

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

    public DocumentationGenerator(Vector<TURTLEPanel> _panels, JTabbedPane _mainTabbedPane, String _path, String _projectName) {
        panels = _panels;
        mainTabbedPane = _mainTabbedPane;
        path = _path + "/";
        projectName = _projectName;

        //int i,j;
        for (int i = 0; i < panels.size(); i++) {
            TURTLEPanel tp = panels.elementAt(i);
            total += tp.panels.size();
        }
    }

    public void setFirstHeadingNumber(int _firstHeadingNumber) {
        firstHeadingNumber = _firstHeadingNumber;
    }

    public String getDocumentation() {
        return doc;
    }

    public String getPath() {
        return path;
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

    public void stopElement() {
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
        int i, j;
        cpt = 0;
        BufferedImage image;
        String svgImg;
        TURTLEPanel tp;
        TDiagramPanel tdp;
        File file1;
        //String tmp, tmpForRef;

        TraceManager.addDev("Path=" + getPath());

        final File docFolder = new File(getPath());

        if (!docFolder.exists()) {
            docFolder.mkdir();

            final String makefileName = File.separator + "Makefile";
            final File makeFile = new File(SpecConfigTTool.IMGPath + makefileName);

            try {
                if (makeFile.exists()) {
                    Files.copy(makeFile.toPath(), new File(getPath() + makefileName).toPath());
                }

                final String mliFileName = File.separator + "mli.mk";
                final File mliFile = new File(SpecConfigTTool.IMGPath + mliFileName);

                if (mliFile.exists()) {
                    Files.copy(mliFile.toPath(), new File(getPath() + mliFileName).toPath());
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        mainLatexDoc = getLatexDocumentationHeader(projectName);
        includeLatexDoc = getIncludeLatexDocumentationHeader(projectName);
        mainLatexDocSvg = getLatexDocumentationHeaderSvg(projectName);
        includeLatexDocSvg = getIncludeLatexDocumentationHeaderSvg(projectName);

        doc = "";
        doc += "<html>\n";
        doc += getDocumentationHeader(projectName);
        doc += "<body>\n";

        doc += "<center><h1>" + title + "</h1></center>\n";
        doc += "<center><b><h1>" + projectName + "</h1></b></center>\n<br><br>\n";

        docSvg = doc;

        for (i = 0; i < panels.size(); i++) {
            tp = panels.elementAt(i);

            String mainTitle = mainTabbedPane.getTitleAt(i);
            panelName = mainTitle;

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
                mainTitle = "TURTLE Design";
            } else if (tp instanceof AnalysisPanel) {
                mainTitle = "TURTLE Analysis";
            } else if (tp instanceof DeploymentPanel) {
                mainTitle = "TURTLE Deployment";
            }

            // Issue #32: This should only be done for Latex
//            tmp = Conversion.replaceAllChar(tmp, '_', "\\_");

            // HTML
            doc += "<br>\n<h" + firstHeadingNumber + ">" + mainTitle + "</h" + firstHeadingNumber + ">\n";
            docSvg += "<br>\n<h" + firstHeadingNumber + ">" + mainTitle + "</h" + firstHeadingNumber + ">\n";

            // Latex
            // Issue #32: This should only be done for Latex
            final String latexMainTitle = Conversion.replaceAllChar(mainTitle, '_', "\\_");
            includeLatexDoc += "\\section{" + latexMainTitle + "}\n";
            includeLatexDocSvg += "\\section{" + latexMainTitle + "}\n";

            for (j = 0; j < tp.panels.size(); j++) {
                if (go == false) {
                    return false;
                }

                tdp = tp.panels.elementAt(j);

                String subTitle = tp.tabbedPane.getTitleAt(j);

                String tmpForRef = Conversion.replaceAllChar(subTitle, '_', "");
                tmpForRef += tmpForRef + i + j;
//				tmp = Conversion.replaceAllChar(tmp, '_', "\\_");

                if (tdp instanceof TMLActivityDiagramPanel) {
                    subTitle = "Behavior of Task: " + subTitle;
                } else if (tdp instanceof AvatarSMDPanel) {
                    subTitle = "Behavior of Block: " + subTitle;
                } else if (tdp instanceof TActivityDiagramPanel) {
                    subTitle = "Behavior of TClass: " + subTitle;
                } else if (tdp instanceof TMLTaskDiagramPanel) {
                    subTitle = "Task and communications between tasks";
                } else if (tdp instanceof TMLArchiDiagramPanel) {
                    subTitle = "Architecture or Mapping of " + panelName;
                } else if (tdp instanceof TDeploymentDiagramPanel) {
                    subTitle = "";
                }

                String imgName = "img_" + i + "_" + j + ".png";

                // Issue #32: Wrong name
                String imgNameSvg = "img_" + i + "_" + j;

                // HTML
                doc += "<h" + (firstHeadingNumber + 1) + ">" + subTitle + "</h" + (firstHeadingNumber + 1) + ">\n";
                docSvg += "<h" + (firstHeadingNumber + 1) + ">" + subTitle + "</h" + (firstHeadingNumber + 1) + ">\n";

                // Latex
                final String latexSubtitle = Conversion.replaceAllChar(subTitle, '_', "\\_");
                includeLatexDoc += "\\subsection{" + latexSubtitle + "}\n";
                includeLatexDoc += "Figures \\ref{fig:" + tmpForRef + "} presents ...\n";
                includeLatexDoc += "\\begin{figure*}[htb]\n\\centering\n";
                includeLatexDoc += "\\includegraphics[width=\\textwidth]{" + imgName + "}\n";
                includeLatexDoc += "\\caption{Diagram \"" + latexSubtitle + "\"}\n\\label{fig:" + tmpForRef + "}\n\\end{figure*}\n\n";

                includeLatexDocSvg += "\\subsection{" + latexSubtitle + "}\n";
                includeLatexDocSvg += "Figures \\ref{fig:" + tmpForRef + "} presents ...\n";
                includeLatexDocSvg += "\\begin{figure*}[htb]\n\\centering\n";
                includeLatexDocSvg += "\\includegraphics[width=\\textwidth]{" + imgNameSvg + "-svg.pdf}\n";
                includeLatexDocSvg += "\\caption{Diagram \"" + latexSubtitle + "\"}\n\\label{fig:" + tmpForRef + "}\n\\end{figure*}\n\n";

                // Capturing the diagram
                image = tdp.performMinimalCapture();
                svgImg = tdp.svgCapture();
                file1 = new File(path + imgName);

                try {
                    // save captured image to PNG file
                    ImageIO.write(image, "png", file1);
                    FileUtils.saveFile(path + imgNameSvg + ".svg", svgImg);
                    //doc += "<center><img src=\"img_" + i + "_" + j + ".png\" align=\"middle\" title=\"" + tmp + "\"></center>\n";
                    doc += "<center><img src=\"img_" + i + "_" + j + ".png\" align=\"middle\" title=\"" + subTitle + "\"></center>\n";
                    docSvg += "<center><img src=\"img_" + i + "_" + j + ".svg\" align=\"middle\" title=\"" + subTitle + "\"></center>\n";
                } catch (Exception e) {
                    System.out.println("Image (" + i + ", " + j + ") could not be captured");
                    e.printStackTrace();
                }

                cpt++;
            }
        }

        doc += "</body>\n</html>";
        docSvg += "</body>\n</html>";

        try {
            FileUtils.saveFile(path + fileName, doc);
        } catch (FileException fe) {
            fe.printStackTrace();
            System.out.println("HTML file could not be saved");

            return false;
        }

        try {
            FileUtils.saveFile(path + fileNameSvg, docSvg);
        } catch (FileException fe) {
            fe.printStackTrace();
            System.out.println("HTML file with svg img could not be saved");

            return false;
        }

        try {
            FileUtils.saveFile(path + texFileName, mainLatexDoc);
        } catch (FileException fe) {
            fe.printStackTrace();
            System.out.println("Main latex file could not be saved");

            return false;
        }

        try {
            FileUtils.saveFile(path + texIncludeFileName, includeLatexDoc);
        } catch (FileException fe) {
            fe.printStackTrace();
            System.out.println("Include latex file could not be saved");

            return false;
        }

        try {
            FileUtils.saveFile(path + texFileNameSvg, mainLatexDocSvg);
        } catch (FileException fe) {
            fe.printStackTrace();
            System.out.println("Main latex svg file could not be saved");

            return false;
        }

        try {
            FileUtils.saveFile(path + texIncludeFileNameSvg, includeLatexDocSvg);
        } catch (FileException fe) {
            fe.printStackTrace();
            System.out.println("included svg file could not be saved");

            return false;
        }


        finished = true;

        return true;
    }

    public int getPercentage() {
        return (cpt * 100) / total;
    }

    public String getDocumentationHeader(String _projectName) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        String tmpdoc = "";
        tmpdoc += "<!----- Automatically generated by TTool version ";
        tmpdoc += DefaultText.getVersion();
        tmpdoc += " generation date: " + formattedDate;
        tmpdoc += "---->\n";
        tmpdoc += "\n<head>\n<title>";
        tmpdoc += getTitle() + ": " + _projectName;
        tmpdoc += "</title>\n</head>\n";
        return tmpdoc;
    }


    public String getLatexDocumentationHeader(String _projectName) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        String tmpdoc = "";
        tmpdoc += "%----- Automatically generated by TTool version ";
        tmpdoc += DefaultText.getVersion();
        tmpdoc += " generation date: " + formattedDate;
        tmpdoc += "----\n";
        tmpdoc += "\\documentclass[11pt,a4paper]{article}\n\n\\usepackage{graphicx}\n\n\\begin{document}\n";
        //tmpdoc += "\\extrafloats{750}\n";
        tmpdoc += "\\title{" + Conversion.replaceAllString(projectName, "_", "\\_") + "}\n";
        tmpdoc += "\\date{\\today}\n";
        tmpdoc += "\\maketitle\n\n";
        tmpdoc += "\\input{" + texIncludeFileName + "}\n";
        tmpdoc += "\\end{document}\n\n";
        return tmpdoc;
    }

    public String getIncludeLatexDocumentationHeader(String _projectName) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        String tmpdoc = "";
        tmpdoc += "%----- Automatically generated by TTool version ";
        tmpdoc += DefaultText.getVersion();
        tmpdoc += " generation date: " + formattedDate;
        tmpdoc += "----\n\n";
        return tmpdoc;
    }

    public String getLatexDocumentationHeaderSvg(String _projectName) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        String tmpdoc = "";
        tmpdoc += "%----- Automatically generated by TTool version ";
        tmpdoc += DefaultText.getVersion();
        tmpdoc += " generation date: " + formattedDate;
        tmpdoc += "----\n";
        tmpdoc += "% To be compiled as follows: make all. Relies on https://github.com/pacalet/mli.git\n";
        tmpdoc += "\\documentclass[11pt,a4paper]{article}\n\n\\usepackage{graphicx}\n\n\\begin{document}\n";
        //tmpdoc += "\\extrafloats{750}\n";
        tmpdoc += "\\title{" + Conversion.replaceAllString(projectName, "_", "\\_") + "}\n";
        tmpdoc += "\\date{\\today}\n";
        tmpdoc += "\\maketitle\n\n";
        tmpdoc += "\\input{" + texIncludeFileNameSvg + "}\n";
        tmpdoc += "\\end{document}\n\n";
        return tmpdoc;
    }

    public String getIncludeLatexDocumentationHeaderSvg(String _projectName) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        String tmpdoc = "";
        tmpdoc += "%----- Automatically generated by TTool version ";
        tmpdoc += DefaultText.getVersion();
        tmpdoc += " generation date: " + formattedDate;
        tmpdoc += "----\n\n";
        return tmpdoc;
    }

}
