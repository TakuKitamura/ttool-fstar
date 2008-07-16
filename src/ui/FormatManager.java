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
 * Class FormatManager
 * Management of the output data of external tools
 * Creation: 08/07/2004
 * @version 1.0 08/07/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.awt.*;
import java.io.*;

import myutil.*;

public class FormatManager {
    
    // Simulation (RTL)
    public static int nbActionSimulation(String data) {
        int nbAction = 0;
        int index;
        
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);
        String s;
        
        try {
            while((s = br.readLine()) != null) {
                index = s.indexOf('(');
                if (index != -1) {
                    nbAction ++;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception nbActionSimulation");
        }
        return nbAction;
    }
    
    // DTA (RTL)
    public static Point nbStateTransitionDTA(String data) {
        int nbState = 0;
        int nbTransition = 0;
        int index;
        String s1, s2;
        
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);
        String s;
        
        try {
            while((s = br.readLine()) != null) {
                index = s.indexOf("arcs");
                if (index != -1) {
                    //
                    s1 = s.substring(0, s.indexOf(" states,"));
                    //System.out.println("s1 = *" + s1 + "*");
                    nbState = new Integer(s1).intValue();
                    s2 = s.substring(s.indexOf("states,") + 8, index - 1);
                    //System.out.println("s2 = *" + s2 + "*");
                    nbTransition = new Integer(s2).intValue();
                }
            }
        } catch (Exception e) {
            System.out.println("Exception nbStateTransitionDTA");
        }
        return new Point(nbState, nbTransition);
    }
    
    // RG (RTL) -> default format
    public static Point nbStateTransitionRGDefault(String data) {
        int nbState = 0;
        int nbTransition = 0;
        int index;
        
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);
        String s;
        
        try {
            while((s = br.readLine()) != null) {
                index = s.indexOf("(");
                if (index == 0)
                    nbTransition ++;
                else
                    nbState ++;
            }
        } catch (Exception e) {
            System.out.println("Exception nbStateTransitionRGDefault");
        }
        return new Point(nbState, nbTransition);
    }
    
    // RG (RTL) -> default format
    public static Point nbStateTransitionRGAldebaran(String data) {
        int nbState = 0;
        int nbTransition = 0;
        int index;
        String s1, s2;
        
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);
        String s;
        
        try {
            while((s = br.readLine()) != null) {
                index = s.indexOf("des");
                //System.out.println("Searching for des");
                if (index == 0) {
                    //System.out.println("des found");
                    s1 = s.substring(s.indexOf(',') + 1, s.length());
                    s1 = s1.substring(0, s1.indexOf(','));
                    s1 = Conversion.removeFirstSpaces(s1);
                    nbTransition = new Integer(s1).intValue();
                    s2 = s.substring(s.indexOf(",") + 1, s.indexOf(')'));
                    s2 = s2.substring(s2.indexOf(",") + 1, s2.length());
                    s2 = Conversion.removeFirstSpaces(s2);
                    nbState = new Integer(s2).intValue();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception nbStateTransitionRGDefault");
        }
        return new Point(nbState, nbTransition);
    }
}