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
* Class UPPAALTransition
* Creation: 03/11/2006
* @version 1.0 03/11/2006
* @author Ludovic APVRILLE
* @see
*/

package uppaaldesc;

import java.util.*;

import java.awt.Point;
import myutil.Conversion;


public class UPPAALTransition {
	public UPPAALLocation destinationLoc, sourceLoc;
	
	public List<Point> points; //nails -> intermediate graphical points on transitions
	
	public String guard = "";
	public Point guardPoint;
	public String synchronization = "";
	public Point synchronizationPoint;
	public String assignment = "";
	public Point assignmentPoint;
	
    public UPPAALTransition() {
		points = new LinkedList<Point>();
		guardPoint = new Point();
		synchronizationPoint = new Point();
		assignmentPoint = new Point();
    }
	
    public String getXML() {
		if (guard == null) {
			guard = "";
		}
		
		
		
		String ret = "<transition>\n";
		
		if(sourceLoc != null) {
			ret+= "<source ref=\"" +sourceLoc.id + "\" />\n";
		}
		if(destinationLoc != null) {
			ret+= "<target ref=\"" +destinationLoc.id + "\" />\n";
		}
		
		ret += "<label kind=\"guard\" x=\"" + guardPoint.x + "\" y=\"" +  guardPoint.y + "\">" + Conversion.transformToXMLString(guard) + "</label>\n";
		ret += "<label kind=\"synchronisation\" x=\"" + synchronizationPoint.x + "\" y=\"" +  synchronizationPoint.y + "\">" + Conversion.transformToXMLString(synchronization) + "</label>\n";
		ret += "<label kind=\"assignment\" x=\"" + assignmentPoint.x + "\" y=\"" +  assignmentPoint.y + "\">" + Conversion.transformToXMLString(assignment) + "</label>\n";
		
		Iterator<Point> iterator = points.listIterator();
		Point p;
		while(iterator.hasNext()) {
			p = iterator.next();
			ret += "<nail x=\"" + p.x + "\" y=\"" +  p.y + "\" />\n";
		}
		
		ret += "</transition>\n";
		return ret;
		
		
    }
	
	public void enhanceGraphics() {
		if ((points.size() == 0) && (destinationLoc == sourceLoc)) {
			points.add(new Point(sourceLoc.idPoint.x - 50, sourceLoc.idPoint.y - 50));
		}
		
		
		if ((points.size() == 1) && (destinationLoc == sourceLoc)) {
			Point p = (Point)(points.get(0));
			points.add(new Point(p.x+10, p.y+35));
		}
	}
	
	public boolean isAnEmptyTransition() {
		
		if (guard == null) {
			guard = "";
		}
		
		if (guard.length() > 0) {
			return false;
		}
		
		if (synchronization.length() > 0) {
			return false;
		}
		
		if (assignment.length() > 0) {
			return false;
		}
		
		return true;
	}
	
}