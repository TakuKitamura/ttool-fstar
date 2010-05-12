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
* Class ConfigurationTTool
* Creation: 21/12/2003
* Version 1.0
* @author Ludovic APVRILLE
* @see
*/

package ui;

//import java.awt.*;
import java.io.*;

import org.w3c.dom.*;
//import org.xml.sax.*;
import javax.xml.parsers.*;

import myutil.*;

/**
* Class
*
* @author Ludovic APVRILLE
* @see
*/
public class ConfigurationTTool {
    
    public static String RTLHost = "";
    public static String RTLPath = "";
    public static String DTA2DOTPath = "";
    public static String RGSTRAPPath = "";
    public static String RG2TLSAPath = "";
    public static String AldebaranHost = "";
    public static String AldebaranPath = "";
    public static String BcgioPath = ""; // Same host as aldebaran
	public static String BisimulatorPath = "";
    public static String BcgminPath = ""; // Same host as aldebaran
    public static String BcgmergePath = ""; // Same host as aldebaran
    public static String CaesarPath = ""; // Same host as aldebaran
    public static String CaesarOpenPath = ""; // Same host as aldebaran
    public static String DOTTYHost = "";
    public static String DOTTYPath = "";
    public static String FILEPath = "";
    public static String LOTOSPath = "";
    public static String LIBPath = "";
    public static String IMGPath = "";
    public static String GGraphPath = "";
    public static String TGraphPath = "";
    public static String TToolUpdateURL = "";
    public static String TToolUpdateProxy = "";
    public static String TToolUpdateProxyPort = "";
    public static String TToolUpdateProxyHost = "";
    public static String JavaCodeDirectory = "";
    public static String JavaCompilerPath = "";
    public static String TToolClassPath = "";
    public static String SimuJavaCodeDirectory = "";
    public static String TToolSimuClassPath = "";
    public static String JavaExecutePath = "";
    public static String JavaHeader = "";
    public static String NCDirectory = "";	
    public static String SystemCCodeDirectory = "";
	public static String TMLCodeDirectory = "";
    public static String SystemCCodeCompileCommand = "";
    public static String SystemCCodeExecuteCommand = "";
	public static String SystemCCodeInteractiveExecuteCommand = "";
    public static String SystemCHost = "";
	public static String VCDPath = "";
    public static String GTKWavePath = "";
    public static String UPPAALCodeDirectory = "";
	public static String UPPAALVerifierPath = "";
	public static String UPPAALVerifierHost = "";
	public static String ExternalCommand1Host = "";
	public static String ExternalCommand2Host = "";
	public static String ExternalCommand1 = "";
	public static String ExternalCommand2 = "";
    
    public static String LastOpenFile = "";
    public static boolean LastOpenFileDefined = false;
	
	public static String LastWindowAttributesX="",  LastWindowAttributesY= "";
	public static String LastWindowAttributesWidth="",  LastWindowAttributesHeight= "";
	public static String LastWindowAttributesMax="";
    
    public static String fileName = "";
    
    
    public static void loadConfiguration(String _fileName, boolean systemcOn) throws MalformedConfigurationException {
        fileName = _fileName;
        
        File f = new File(fileName);
        
        if (!FileUtils.checkFileForOpen(f)) {
            throw new MalformedConfigurationException("Filepb");
        }
        
        String data = FileUtils.loadFileData(f);
        
        if (data == null) {
            throw new MalformedConfigurationException("Filepb");
        }
        
        loadConfigurationFromXML(data, systemcOn);
    }
    
    public static void saveConfiguration() throws MalformedConfigurationException {
        int index0, index1, index2, index3;
		String tmp, tmp1, tmp2, location;
        File f = new File(fileName);
		boolean write = false;
        
        if (!FileUtils.checkFileForOpen(f)) {
            throw new MalformedConfigurationException("Filepb");
        }
        
        String data = FileUtils.loadFileData(f);
        
        if (data == null) {
            throw new MalformedConfigurationException("Filepb");
        }
        
        index0 = data.indexOf("LastOpenFile");
        
        //sb.append("data = " + data + " ConfigurationTTool.LastOpenFile=" + ConfigurationTTool.LastOpenFile);
        
        if (index0 > -1) {
            index1 = data.indexOf('"', index0);
            if (index1 > -1) {
                index2 = data.indexOf('"', index1 + 1);
                if (index2 > -1) {
					tmp = data.substring(index2, data.length());
					data = data.substring(0, index1+1) + ConfigurationTTool.LastOpenFile+ tmp;	
					//sb.append("data = " + data);
					write = true;
					/*try {
						FileOutputStream fos = new FileOutputStream(f);
						fos.write(data.getBytes());
						fos.close();
					} catch (Exception e) {
						throw new  MalformedConfigurationException("Saving file failed");
					}*/
                }
            }
        }
		
		index0 = data.indexOf("LastWindowAttributes");
		if (index0 > -1) {
			tmp1 = data.substring(0, index0+20);
			tmp2 = data.substring(index0+20, data.length());
			index1 = tmp2.indexOf("/>");
			if (index1 > -1) {
				tmp2 = tmp2.substring(index1, tmp2.length());
				location = " x=\"" + LastWindowAttributesX;
				location += "\" y=\"" + LastWindowAttributesY;
				location += "\" width=\"" + LastWindowAttributesWidth;
				location += "\" height=\"" + LastWindowAttributesHeight;
				location += "\" max=\"" + LastWindowAttributesMax + "\" ";
				data = tmp1 + location + tmp2;
				write = true;
			}
		} else {
			index1= data.indexOf("</TURTLECONFIGURATION>");
			if (index1 > -1) {
				location = "<LastWindowAttributes x=\"" + LastWindowAttributesX;
				location += "\" y=\"" + LastWindowAttributesY;
				location += "\" width=\"" + LastWindowAttributesWidth;
				location += "\" height=\"" + LastWindowAttributesHeight;
				location += "\" max=\"" + LastWindowAttributesMax + "\"/>\n\n";
				data = data.substring(0, index1) + location + data.substring(index1, data.length());
				write = true;
			}
		}
		
		if (write) {
			//sb.append("Writing data=" + data); 
			try {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(data.getBytes());
				fos.close();
			} catch (Exception e) {
				throw new  MalformedConfigurationException("Saving file failed");
			}
		} else {
			TraceManager.addError("Configuration could not be saved");
		}
		
    }
	
	public static boolean configSizeAvailable() {
		if (LastWindowAttributesX.length() == 0) {
			return false;
		}
		
		if (LastWindowAttributesY.length() == 0) {
			return false;
		}
		
		if (LastWindowAttributesWidth.length() == 0) {
			return false;
		}
		
		if (LastWindowAttributesHeight.length() == 0) {
			return false;
		}
		
		return true;
	}
	
	public static void printConfiguration(boolean systemcOn) {
		System.out.println(getConfiguration(systemcOn));
	}
    
    public static String getConfiguration(boolean systemcOn) {
		StringBuffer sb = new StringBuffer("");
		// Formal verification
		sb.append("RTL:\n");
        sb.append("RTLHost: " + RTLHost + "\n");
        sb.append("RTLPath: " + RTLPath + "\n");
        sb.append("DTA2DOTPath: " + DTA2DOTPath + "\n");
        sb.append("RG2TLSAPath: " + RG2TLSAPath + "\n");
        sb.append("RGSTRAPPath: " + RGSTRAPPath + "\n");
		sb.append("\nCADP:\n");
        sb.append("AldebaranHost: " + AldebaranHost + "\n");
        sb.append("AldebaranPath: " + AldebaranPath + "\n");
        sb.append("BcgioPath: " + BcgioPath + "\n" );
        sb.append("BcgminPath: " + BcgminPath + "\n" );
		sb.append("BisimulatorPath: " + BisimulatorPath + "\n" );
        sb.append("BcgmergePath: " + BcgmergePath + "\n");
        sb.append("CaesarPath: " + CaesarPath + "\n");
        sb.append("CaesarOpenPath: " + CaesarOpenPath + "\n");
		sb.append("\nDotty:\n");
        sb.append("DOTTYHost: " + DOTTYHost + "\n");
        sb.append("DOTTYPath: " + DOTTYPath + "\n");
		// UPPAAL
		sb.append("\nUPPAAL:\n");
        sb.append("UPPAALCodeDirectory: " + UPPAALCodeDirectory + "\n");
		sb.append("UPPAALVerifierPATH: " + UPPAALVerifierPath + "\n");
		sb.append("UPPAALVerifierHOST: " + UPPAALVerifierHost + "\n");
		
		sb.append("\nYour files (modeling, librairies, etc.):\n");
        sb.append("FILEPath: " + FILEPath + "\n");
        sb.append("LOTOSPath: " + LOTOSPath + "\n");
        sb.append("LIBPath: " + LIBPath + "\n");
        sb.append("IMGPath: " + IMGPath + "\n");
        sb.append("GGraphPath: " + GGraphPath + "\n");
        sb.append("TGraphPath: " + TGraphPath + "\n");
		sb.append("\nTTool update:\n");
        sb.append("TToolUpdateURL: " + TToolUpdateURL + "\n");
        sb.append("TToolUpdateProxy: " + TToolUpdateProxy + "\n");
        sb.append("TToolUpdateProxyPort: " + TToolUpdateProxyPort + "\n");
        sb.append("TToolUpdateProxyHost: " + TToolUpdateProxyHost + "\n");
		sb.append("\nJava prototyping:\n");
        sb.append("JavaCodeDirectory: " + JavaCodeDirectory + "\n");
		sb.append("JavaHeader: " + JavaHeader + "\n");
        sb.append("JavaCompilerPath: " + JavaCompilerPath + "\n");
        sb.append("TToolClassPath: " + TToolClassPath + "\n");
        sb.append("JavaExecutePath: " + JavaExecutePath + "\n");
        sb.append("SimuJavaCodeDirectory: " + SimuJavaCodeDirectory + "\n");
        sb.append("TToolSimuClassPath: " + TToolSimuClassPath + "\n");
        
		sb.append("\nDIPLODOCUS:\n");
        if (systemcOn) {
            sb.append("SystemCCodeDirectory: " + SystemCCodeDirectory + "\n");
            sb.append("SystemCHost: " + SystemCHost + "\n");
            sb.append("SystemCCodeCompileCommand: " + SystemCCodeCompileCommand + "\n");
            sb.append("SystemCCodeExecuteCommand: " + SystemCCodeExecuteCommand + "\n");
			sb.append("SystemCCodeInteractiveExecuteCommand: " + SystemCCodeInteractiveExecuteCommand + "\n");
            sb.append("GTKWavePath: " + GTKWavePath + "\n");
        }
		
		// TML
		
		sb.append("TMLCodeDirectory" + TMLCodeDirectory + "\n");
		
		// VCD
		sb.append("VCDPath: " + VCDPath + "\n");
		
		// NC
		sb.append("\nNetwork calculus:\n");
		sb.append("NCDirectory: " + NCDirectory + "\n");
		
		
		
		sb.append("\nCustom external commands:\n");
		sb.append("ExternalCommand1Host: " + ExternalCommand1Host + "\n");
		sb.append("ExternalCommand1: " + ExternalCommand1 + "\n");
		sb.append("ExternalCommand2Host: " + ExternalCommand2Host + "\n");
		sb.append("ExternalCommand2: " + ExternalCommand2 + "\n");
		
        sb.append("\nInformation saved by TTool:\n");
        sb.append("LastOpenFile: " + LastOpenFile + "\n");
		sb.append("LastWindowAttributesX: " + LastWindowAttributesX + "\n");
		sb.append("LastWindowAttributesY: " + LastWindowAttributesY + "\n");
		sb.append("LastWindowAttributesWidth: " + LastWindowAttributesWidth + "\n");
		sb.append("LastWindowAttributesHeight: " + LastWindowAttributesHeight + "\n");
		sb.append("LastWindowAttributesMax: " + LastWindowAttributesMax + "\n");
		
		return sb.toString();
        
    }
    
    public static void loadConfigurationFromXML(String data, boolean systemcOn) throws MalformedConfigurationException {
        
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();;
            
            // building nodes from xml String
            Document doc = db.parse(bais);
            NodeList nl;
            
            nl = doc.getElementsByTagName("RTLHost");
            if (nl.getLength() > 0)
                RTLHOST(nl);
            nl = doc.getElementsByTagName("RTLPath");
            if (nl.getLength() > 0)
                RTLPath(nl);
            nl = doc.getElementsByTagName("DTA2DOTPath");
            if (nl.getLength() > 0)
                DTA2DOTPath(nl);
            nl = doc.getElementsByTagName("RG2TLSAPath");
            if (nl.getLength() > 0)
                RG2TLSAPath(nl);
            nl = doc.getElementsByTagName("RGSTRAPPath");
            if (nl.getLength() > 0)
                RGSTRAPPath(nl);
            nl = doc.getElementsByTagName("AldebaranPath");
            if (nl.getLength() > 0)
                AldebaranPath(nl);
            nl = doc.getElementsByTagName("AldebaranHost");
            if (nl.getLength() > 0)
                AldebaranHost(nl);
            nl = doc.getElementsByTagName("BcgioPath");
            if (nl.getLength() > 0)
                BcgioPath(nl);
            nl = doc.getElementsByTagName("BcgmergePath");
            if (nl.getLength() > 0)
                BcgmergePath(nl);
            nl = doc.getElementsByTagName("BcgminPath");
            if (nl.getLength() > 0)
                BcgminPath(nl);
			nl = doc.getElementsByTagName("BisimulatorPath");
            if (nl.getLength() > 0)
                BisimulatorPath(nl);
            nl = doc.getElementsByTagName("CaesarPath");
            if (nl.getLength() > 0)
                CaesarPath(nl);
            nl = doc.getElementsByTagName("CaesarOpenPath");
            if (nl.getLength() > 0)
                CaesarOpenPath(nl);
            nl = doc.getElementsByTagName("DOTTYHost");
            if (nl.getLength() > 0)
                DOTTYHost(nl);
            nl = doc.getElementsByTagName("DOTTYPath");
            if (nl.getLength() > 0)
                DOTTYPath(nl);
            nl = doc.getElementsByTagName("FILEPath");
            if (nl.getLength() > 0)
                FILEPath(nl);
            nl = doc.getElementsByTagName("LOTOSPath");
            if (nl.getLength() > 0)
                LOTOSPath(nl);
            nl = doc.getElementsByTagName("LIBPath");
            if (nl.getLength() > 0)
                LIBPath(nl);
            nl = doc.getElementsByTagName("IMGPath");
            if (nl.getLength() > 0)
                IMGPath(nl);
            nl = doc.getElementsByTagName("GGraphPath");
            if (nl.getLength() > 0)
                GGraphPath(nl);
            nl = doc.getElementsByTagName("TGraphPath");
            if (nl.getLength() > 0)
                TGraphPath(nl);
            nl = doc.getElementsByTagName("TToolUpdateURL");
            if (nl.getLength() > 0)
                TToolUpdateURL(nl);
            nl = doc.getElementsByTagName("TToolUpdateProxy");
            if (nl.getLength() > 0)
                TToolUpdateProxy(nl);
            nl = doc.getElementsByTagName("TToolUpdateProxyPort");
            if (nl.getLength() > 0)
                TToolUpdateProxyPort(nl);
            nl = doc.getElementsByTagName("TToolUpdateProxyHost");
            if (nl.getLength() > 0)
                TToolUpdateProxyHost(nl);
            nl = doc.getElementsByTagName("JavaCodeDirectory");
            if (nl.getLength() > 0)
                JavaCodeDirectory(nl);
			nl = doc.getElementsByTagName("JavaHeader");
            if (nl.getLength() > 0)
                JavaHeader(nl);
            nl = doc.getElementsByTagName("JavaCompilerPath");
            if (nl.getLength() > 0)
                JavaCompilerPath(nl);
            nl = doc.getElementsByTagName("TToolClassPath");
            if (nl.getLength() > 0)
                TToolClassPath(nl);
            nl = doc.getElementsByTagName("SimuJavaCodeDirectory");
            if (nl.getLength() > 0)
                SimuJavaCodeDirectory(nl);
            nl = doc.getElementsByTagName("TToolSimuClassPath");
            if (nl.getLength() > 0)
                TToolSimuClassPath(nl);
            nl = doc.getElementsByTagName("JavaExecutePath");
            if (nl.getLength() > 0)
                JavaExecutePath(nl);
			nl = doc.getElementsByTagName("NCDirectory");
            if (nl.getLength() > 0)
                NCDirectory(nl);
            nl = doc.getElementsByTagName("SystemCCodeDirectory");
            if (nl.getLength() > 0)
                SystemCCodeDirectory(nl);
			
            
			if (systemcOn) {
					nl = doc.getElementsByTagName("SystemCHost");
					if (nl.getLength() > 0)
						SystemCHost(nl);
					nl = doc.getElementsByTagName("SystemCCodeCompileCommand");
					if (nl.getLength() > 0)
						SystemCCodeCompileCommand(nl);
					nl = doc.getElementsByTagName("SystemCCodeExecuteCommand");
					if (nl.getLength() > 0)
						SystemCCodeExecuteCommand(nl);
					nl = doc.getElementsByTagName("SystemCCodeInteractiveExecuteCommand");
					if (nl.getLength() > 0)
						SystemCCodeInteractiveExecuteCommand(nl);
					nl = doc.getElementsByTagName("GTKWavePath");
					if (nl.getLength() > 0)
						GTKWavePath(nl);
				}
				
				nl = doc.getElementsByTagName("TMLCodeDirectory");
				if (nl.getLength() > 0)
					TMLCodeDirectory(nl);
				
				nl = doc.getElementsByTagName("VCDPath");
				if (nl.getLength() > 0)
					VCDPath(nl);
				
				nl = doc.getElementsByTagName("UPPAALCodeDirectory");
				if (nl.getLength() > 0)
					UPPAALCodeDirectory(nl);
				
				nl = doc.getElementsByTagName("UPPAALVerifierPath");
				if (nl.getLength() > 0)
					UPPAALVerifierPath(nl);
				
				nl = doc.getElementsByTagName("UPPAALVerifierHost");
				if (nl.getLength() > 0)
					UPPAALVerifierHost(nl);
				
				nl = doc.getElementsByTagName("ExternalCommand1Host");
				if (nl.getLength() > 0)
					ExternalCommand1Host(nl);
				
				nl = doc.getElementsByTagName("ExternalCommand1");
				if (nl.getLength() > 0)
					ExternalCommand1(nl);
				
				nl = doc.getElementsByTagName("ExternalCommand2Host");
				if (nl.getLength() > 0)
					ExternalCommand2Host(nl);
				
				nl = doc.getElementsByTagName("ExternalCommand2");
				if (nl.getLength() > 0)
					ExternalCommand2(nl);
				
				nl = doc.getElementsByTagName("LastOpenFile");
				if (nl.getLength() > 0)
					LastOpenFile(nl);
				
				nl = doc.getElementsByTagName("LastWindowAttributes");
				if (nl.getLength() > 0)
					LastWindowAttributes(nl);
				
				
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void RTLHOST(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            RTLHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void RTLPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            RTLPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void DTA2DOTPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            DTA2DOTPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void RG2TLSAPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            RG2TLSAPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void RGSTRAPPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            RGSTRAPPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void AldebaranPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            AldebaranPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void AldebaranHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            AldebaranHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void BcgioPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            BcgioPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void BcgmergePath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            BcgmergePath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void BcgminPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            BcgminPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	private static void BisimulatorPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            BisimulatorPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void CaesarPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            CaesarPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void CaesarOpenPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            CaesarOpenPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void DOTTYHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            DOTTYHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void DOTTYPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            DOTTYPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void FILEPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            FILEPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void LOTOSPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            LOTOSPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void LIBPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            LIBPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void IMGPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            IMGPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void GGraphPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            GGraphPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void TGraphPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            TGraphPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void TToolUpdateURL(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            TToolUpdateURL = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void TToolUpdateProxy(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            TToolUpdateProxy = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void TToolUpdateProxyPort(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            TToolUpdateProxyPort = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void TToolUpdateProxyHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            TToolUpdateProxyHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void JavaCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            JavaCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	 private static void JavaHeader(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            JavaHeader = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void JavaCompilerPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            JavaCompilerPath = elt.getAttribute("data");
            if (JavaCompilerPath.startsWith("[")) {
                JavaCompilerPath = "\"" + JavaCompilerPath.substring(1, JavaCompilerPath.length()) + "\"";
            }
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void TToolClassPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            TToolClassPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void SimuJavaCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            SimuJavaCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void TToolSimuClassPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            TToolSimuClassPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void JavaExecutePath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            JavaExecutePath = elt.getAttribute("data");
            if (JavaExecutePath.startsWith("[")) {
                JavaExecutePath = "\"" + JavaExecutePath.substring(1, JavaExecutePath.length()) + "\"";
            }
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	 private static void NCDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            NCDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void SystemCCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            SystemCCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void SystemCHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            SystemCHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void SystemCCodeCompileCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            SystemCCodeCompileCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void SystemCCodeExecuteCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            SystemCCodeExecuteCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	private static void SystemCCodeInteractiveExecuteCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            SystemCCodeInteractiveExecuteCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void GTKWavePath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            GTKWavePath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	 private static void TMLCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            TMLCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	private static void VCDPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            VCDPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void UPPAALCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            UPPAALCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	private static void UPPAALVerifierPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            UPPAALVerifierPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	private static void UPPAALVerifierHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            UPPAALVerifierHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	private static void ExternalCommand1Host(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            ExternalCommand1Host = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	private static void ExternalCommand1(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            ExternalCommand1 = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	private static void ExternalCommand2Host(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            ExternalCommand2Host = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	private static void ExternalCommand2(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            ExternalCommand2 = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
    private static void LastOpenFile(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            LastOpenFile = elt.getAttribute("data");
            LastOpenFileDefined = true;
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
	
	private static void LastWindowAttributes(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element)(nl.item(0));
            LastWindowAttributesX = elt.getAttribute("x");
			LastWindowAttributesY = elt.getAttribute("y"); 
			LastWindowAttributesWidth = elt.getAttribute("width");
			LastWindowAttributesHeight = elt.getAttribute("height");
			LastWindowAttributesMax = elt.getAttribute("max");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }
    
} //