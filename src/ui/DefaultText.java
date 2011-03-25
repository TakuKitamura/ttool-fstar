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
 * Class DefaultText
 * Text of some windows
 * Creation: 01/12/2003
 * @version 1.1 29/01/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui;


public class DefaultText  {

    public static String BUILD = "7838";
	public static String DATE = "2011/03/25 16:33:47 CET";

    public static StringBuffer sbAbout = makeAbout();
    
    public static String getAboutText() {
        return new String(sbAbout);
    }
    
    public static String getVersion() {
        return "0.93-beta1"; /* Set new release December, 3th, 2010 */
    }
	
	public static String getFullVersion() {
		return getVersion() + " -- build: " + DefaultText.BUILD + " date: " + DefaultText.DATE;
	}
    
    private static StringBuffer makeAbout() {
        StringBuffer sb = new StringBuffer();
        sb.append("TTool version " + getFullVersion() + "\n\n");
        sb.append("Programmers\n\tLudovic Apvrille, Daniel Knorreck - Telecom ParisTech - contact email: ludovic.apvrille@telecom-paristech.fr\n");
        sb.append("\tCopyright Institut Telecom - Telecom ParisTech / Ludovic Apvrille / Daniel Knorreck \n\n");
        sb.append("Online documentation\n\tLudovic Apvrille - Telecom ParisTech - ludovic.apvrille@telecom-paristech.fr\n\n\n");
        sb.append("For more information regarding TTool, and the UML/SysML profiles supportd by TTool:\n");
        sb.append("TTool's website: http://labsoc.comelec.enst.fr/ttool/\n\n");
        return sb;
    }
	
	
}
