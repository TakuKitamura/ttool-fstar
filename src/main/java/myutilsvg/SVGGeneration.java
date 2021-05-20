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

package myutilsvg;

import myutil.*;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.*;
import java.io.*;

/**
 * Class SVGGeneration
 *
 * Creation: 20/06/2018
 * 
 * @version 1.0 20/06/2018
 * @author Ludovic APVRILLE
 */
public class SVGGeneration {

  public SVGGeneration() {

  }

  public SVGGraphics2D getSVGGenerator(JPanel panel) {
    // Get a DOMImplementation.
    DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document.
    String svgNS = "http://www.w3.org/2000/svg";
    Document document = domImpl.createDocument(svgNS, "svg", null);

    // Create an instance of the SVG Generator.
    SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

    // Ask the test to render into the SVG Graphics2D implementation.
    panel.paint(svgGenerator);

    return svgGenerator;
  }

  public void saveInSVG(JPanel panel, String fileName) {
    SVGGraphics2D svgGenerator = getSVGGenerator(panel);
    boolean useCSS = true; // we want to use CSS style attributes
    try {
      File fileSave = new File(fileName);
      FileOutputStream fos = new FileOutputStream(fileSave);
      Writer out = new OutputStreamWriter(fos, "UTF-8");
      svgGenerator.stream(out, useCSS);
    } catch (Exception e) {
      TraceManager.addDev("SVG generation failed: " + e.getMessage());
    }
  }

  public String getSVGString(JPanel panel) {
    SVGGraphics2D svgGenerator = getSVGGenerator(panel);

    boolean useCSS = true; // we want to use CSS style attributes
    try {
      StringWriter ouS = new StringWriter();
      svgGenerator.stream(ouS, useCSS);
      String tmp = ouS.toString();
      return tmp;
    } catch (Exception e) {
      TraceManager.addDev("SVG generation failed: " + e.getMessage());
      return null;
    }
  }

}
