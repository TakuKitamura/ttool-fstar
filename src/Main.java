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
 * Class Main
 * starts the main Windows and a project manager
 * Creation: 01/12/2003
 * @version 1.0 21/08/2004
 * @author Ludovic APVRILLE
 * @see
 */

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.net.URL;
//import java.io.*;
import javax.swing.ImageIcon;
import javax.swing.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class Main implements ActionListener {
    public boolean finish = false;
    public static Main main = new Main();
    public static boolean systemc = true;
    public static boolean lotos = true;
    public static boolean proactive = false;
    public static boolean tpn = false;
    public static boolean os = false;
    public static boolean uppaal = true;
    public static boolean nc = true ; // Network calculus
	public static boolean avatar = false ; // avatar profile
	
    public static void main(String[] args) {
		
		TraceManager.devPolicy = TraceManager.TO_DEVNULL;
      
        System.out.println("\n*** TTool version: " + DefaultText.getFullVersion() + " ***\n");
		
        // Read the image data and display the splash screen
        // -------------------------------------------------
        
        JStartingWindow splashFrame = null;
        //System.out.println("hello");
        URL imageURL = Main.class.getResource("ui/images/starting_logo.gif");
        Image img;
        //System.out.println("helli");
        if (imageURL != null) {
            //System.out.println("hellu");
            img = (new ImageIcon(imageURL)).getImage();
            splashFrame = JStartingWindow.splash(img, "Loading TTool's elements");
            //splashFrame.setLicenceMessage("An open-source toolkit from:");
            splashFrame.setMiddleMessage("version " + DefaultText.getVersion());
            //System.out.println("helly");
        } else {
            System.err.println("Starting image not found");
            System.exit(0);
        }
        
        
        new Timer(3000, main).start();
        
        
        
        // Starting window
        // setting default language
        splashFrame.setMessage("Setting language");
        Locale.setDefault(new Locale("en"));
        
        // Analyzing arguments
        String config = "config.xml";
        for(int i=0; i<args.length; i++) {
            if (args[i].compareTo("-systemc") == 0) {
                systemc = true;
                System.out.println("SystemC features activated - these are beta features that are meant to be used only for research purpose");
            }
            if (args[i].compareTo("-lotos") == 0) {
                lotos = true;
                System.out.println("LOTOS features activated");
            }
			if (args[i].compareTo("-nolotos") == 0) {
                lotos = false;
                System.out.println("LOTOS features activated");
            }
             if (args[i].compareTo("-launcher") == 0) {
                Thread t = new Thread(new RTLLauncher());
                t.start();
            }
            if (args[i].compareTo("-diplodocus") == 0) {
                systemc = true;
                lotos = true;
                System.out.println("Diplodocus features activated");
            }
			if (args[i].compareTo("-nodiplodocus") == 0) {
                systemc = false;
                System.out.println("Diplodocus features deactivated");
            }
            if (args[i].compareTo("-proactive") == 0) {
                proactive = true;
                lotos = true;
                System.out.println("Proactive features activated - these are beta features that are meant to be used only for research purpose");
            }
            if (args[i].compareTo("-tpn") == 0) {
                tpn = true;
                System.out.println("TPN features activated - these are beta features that are meant to be used only for research purpose");
            }

            if (args[i].compareTo("-nonc") ==0 )  {
				nc = false;
                System.out.println("Network calculus features unactivated - these are beta features that are meant to be used only for research purpose");
            }
			
			if (args[i].compareTo("-avatar") ==0 )  {
				avatar = true;
                System.out.println("AVATAR activated - these are beta features that are meant to be used only for research purpose");
            }
			
			if (args[i].compareTo("-noavatar") ==0 )  {
				avatar = false;
                System.out.println("AVATAR unactivated - these are beta features that are meant to be used only for research purpose");
            }
	    
            if (args[i].compareTo("-os") == 0) {
                os = true;
                System.out.println("TURTLE-OS features activated - these are beta features that are meant to be used only for research purpose");
            }
            
            if (args[i].compareTo("-uppaal") == 0) {
                uppaal = true;
                System.out.println("UPPAAL features activated - these are beta features that are meant to be used only for research purpose");
            }
			 if (args[i].compareTo("-nouppaal") == 0) {
                uppaal = false;
                System.out.println("UPPAAL features activated - these are beta features that are meant to be used only for research purpose");
            }
            
            if (args[i].compareTo("-config") == 0) {
                config = args[Math.min(args.length-1, i+1)];
            }
			
			if (args[i].compareTo("-debug") == 0) {
                TraceManager.devPolicy = TraceManager.TO_CONSOLE;
            }
			
        }
        
        // Icons
        splashFrame.setMessage("Loading images");
        IconManager icma = new IconManager();
        icma.loadImg();
        
        // Loading configuration
        splashFrame.setMessage("Loading configuration file: " + config);
        
        
        try {
            ConfigurationTTool.loadConfiguration(config, systemc);
        } catch (Exception e) {
            System.out.println("Couldn't load configuration properly : " + e.toString());
        }
        System.out.println("\nConfiguration:\n--------------");
        ConfigurationTTool.printConfiguration(systemc);
        System.out.println("\nDebugging trace:\n----------------");
        
        
        // making main window
        splashFrame.setMessage("Creating main window");
        MainGUI mainGUI = new MainGUI(systemc, lotos, proactive, tpn, os, uppaal, nc, avatar);
        splashFrame.setMessage("Building graphical components");
        mainGUI.build();
        
        // loading configuration
        
        // starting application
        if (mainGUI == null) {
            ErrorGUI.exit(ErrorGUI.GUI);
        }
		
        
        splashFrame.setMessage("Starting TTool ...");
        main.waitFinish();
        mainGUI.start();
        
        // Dispose the splash screen
        // -------------------------
        if (splashFrame != null) {
            splashFrame.dispose();
        }
        
        // Checking for update
        TToolUpdater tu = new TToolUpdater(mainGUI.frame);
        tu.start();
    }
    
    public synchronized void waitFinish() {
        while(finish == false) {
            try {
                wait();
            } catch (InterruptedException ie) {
            }
        }
    }
    
    public synchronized void okFinish() {
        finish = true;
        notifyAll();
    }
    
    public void actionPerformed(ActionEvent evt) {
        main.okFinish();
    }
    
} // Class Main

