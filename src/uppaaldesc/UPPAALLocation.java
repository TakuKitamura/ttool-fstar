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
 * Class UPPAALLocation
 * Creation: 03/11/2006
 * @version 1.0 03/11/2006
 * @author Ludovic APVRILLE
 * @see
 */

package uppaaldesc;

import java.awt.Point;
import myutil.*;


public class UPPAALLocation {
  public int int_id;
  public String id; // Internal id to identify locations -> not shown on diagram
  public Point idPoint;
  public String name; // Name shown on graph
  public Point namePoint;
  public String invariant;
  public boolean urgent = false;
  public boolean committed = false;
  
  private static int currentId = 0;
  private static int invariantX = 5;
  private static int invariantY = 15;
  
  public static void reinitID() {
	  currentId = 0;
  }

    public UPPAALLocation() {
      int_id = currentId;
      id = "id" + currentId;
      idPoint = new Point();
      name = id;
      namePoint = new Point();
      currentId ++;
    }
    
    public void setUrgent() {
	    urgent = true;
    }
	
	public void unsetUrgent() {
	    urgent = false;
    }
	
	public void setCommitted() {
	    committed = true;
    }
	
	public void unsetCommitted() {
	    committed = false;
    }
    
    public void setInvariant(String _invariant) {
	    invariant = _invariant;
    }
    
    public String getXML() {
           String ret = "<location id=\"" + id + "\" x=\"" + idPoint.x + "\" y=\"" +  idPoint.y + "\">\n";
           ret += "<name x=\"" + namePoint.x + "\" y=\"" +  namePoint.y + "\">" + name + "</name>\n";
           if ((invariant != null) && (invariant.length() > 0)) {
	           ret += "<label kind=\"invariant\"  x=\"" + (idPoint.x + invariantX) + "\" y=\"" +  (idPoint.y + invariantY) + "\">";
	           ret += Conversion.transformToXMLString(invariant);
	           ret +="</label>\n";
           }
           if (urgent) {
	           ret +="<urgent />\n";
       		}
			if (committed) {
				ret += "<committed />\n";
			}
           ret += "</location>\n";
           return ret;
    }

}