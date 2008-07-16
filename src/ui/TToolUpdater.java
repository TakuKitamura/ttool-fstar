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
 * Class TToolUpdater
 * Possible states of TGComponent
 * Creation: 16/02/2005
 * @version 1.0 16/02/2005
 * @author Ludovic APVRILLE
 * @see MainGUI
 */

package ui;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;


public class TToolUpdater extends Thread {
    
    private Frame f;
    
    public TToolUpdater(Frame _f) {
        f = _f;
    }
    
    public void run() {
        if (ConfigurationTTool.TToolUpdateURL.length()<1) {
            return;
        }
        
        // Proxy configuration
        if (ConfigurationTTool.TToolUpdateProxy.compareTo("true") == 0) {
            System.getProperties().put("proxySet", "true");
            System.getProperties().put("proxyPort", ConfigurationTTool.TToolUpdateProxyPort);
            System.getProperties().put("proxyHost", ConfigurationTTool.TToolUpdateProxyHost);
        }
        
        try {
            // Getting document
            URL ttoolurl = new URL(ConfigurationTTool.TToolUpdateURL);
            URLConnection tc = ttoolurl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
            String inputLine;
            StringBuffer doc = new StringBuffer("");
            
            // Reading document
            while ((inputLine = in.readLine()) != null) {
                //System.out.println(inputLine);
                doc.append(inputLine);
            }
            in.close();
            
            // Extracting version number
            String s = doc.toString();
            int index1 = s.indexOf("*Version");
            if (index1 < 0) {
                return;
            }
            
            s = s.substring(index1+8, s.length());
            int index2 = s.indexOf("*");
            if (index2 < 0) {
                return;
            }
            
            s = s.substring(0, index2);
            s = s.trim();
            //System.out.println("Updated version number: " + s + " current version number: " + DefaultText.getVersion());
            
            int compare = s.compareTo(DefaultText.getVersion());
            if (compare == 0) {
                System.out.println("Your version of TTool is up to date");
            } else if (compare > 0) {
                System.out.println("A new version of TTool has been released. Please, update as soon as possible");
                JOptionPane.showMessageDialog(f,
                "A new version of TTool has been released. Please, update as soon as possible",
                "New version!",
                JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("You're currently working with a non-official version of TTool");
            }
            
            // Dealing with counter / statistics
            URL staturl = new URL("http://m1.nedstatbasic.net/n?id=ADQQ3gWbvyUrshjP9W3M7+vaqNIw");
            tc = staturl.openConnection();
            in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
            while ((inputLine = in.readLine()) != null) {}
            in.close();
            
        } catch (Exception e) {
            System.out.println("Couldn't check for a new version of TTool: " + e.getMessage());
        }
        
    }
}
