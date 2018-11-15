/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT telecom-paristech.fr
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

public class SimulationTrace {
	private String name;
	private String fullPathToFile = null;
	private int type;

	public static final int VCD_DIPLO = 0;
    public static final int TXT_DIPLO = 1;
    public static final int HTML_DIPLO = 2;

    public static final int PNG_AVATAR = 3;
    public static final int TXT_AVATAR = 4;
    public static final int SVG_AVATAR = 5;

	private static String[] TYPES = {"VCD DIPLO", "TXT DIPLO", "HTML DIPLO", "PNG AVATAR", "TXT AVATAR", "SVG AVATAR"};

    public SimulationTrace(String name, int type) {
        this(name, type, null);
    }

	public SimulationTrace(String name, int type, String fullPathToFile) {
	    this.name = name;
	    this.type = type;
	    this.fullPathToFile = fullPathToFile;
    }

    public int getType() {
	    return type;
    }

    public String getTypeString() {
	    return TYPES[type];
    }

    public String getFullPath() {
        return fullPathToFile;
    }

    public String getToolTip() {
	    return "Simulation trace. Name:" + name;
    }

    public boolean hasFile() {
	    return fullPathToFile != null;
    }

    public String getName() {
	    return name;
    }

    public String toString() {
        String ret="";
        ret += name + " of type " +  TYPES[type];
        if (fullPathToFile != null) {
            ret += " " + fullPathToFile;
        }
        return ret;

    }
}
