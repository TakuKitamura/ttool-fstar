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
* Class NCLink
* Creation: 14/11/2008
* @version 1.0 14/11/2008
* @author Ludovic APVRILLE
* @see
*/

package nc;



public class NCLink extends NCElement  {
	protected int capacity;
	protected NCCapacityUnit capacityUnit = new NCCapacityUnit(); // Default value: Mbs 
	protected NCLinkedElement le1, le2;
	
	public NCLink() {}
	
	public void setCapacity(int _capacity) {
		capacity = _capacity;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public NCLinkedElement getLinkedElement1() {
		return le1;
	}
	
	public NCLinkedElement getLinkedElement2() {
		return le2;
	}
	
	public void setLinkedElement1(NCLinkedElement ncle) {
		le1 = ncle;
	}
	
	public void setLinkedElement2(NCLinkedElement ncle) {
		le2 = ncle;
	}
	
	public void setCapacityUnit(NCCapacityUnit _unit) {
		capacityUnit = _unit;
	}
	
	public NCCapacityUnit getCapacityUnit() {
		return capacityUnit;
	}
	
	public Object clone() {
		NCLink link = new NCLink();
		link.setName(getName());
		NCCapacityUnit unit = new NCCapacityUnit();
		unit.setUnit(getCapacityUnit().getStringUnit());
		link.setCapacity(capacity);
		link.setCapacityUnit(unit);
		link.setLinkedElement1(le1);
		link.setLinkedElement2(le2);
		return link;
	}
	
	public NCLink cloneReversed() {
		NCLink link = new NCLink();
		link.setName(getName() + "_r");
		NCCapacityUnit unit = new NCCapacityUnit();
		unit.setUnit(getCapacityUnit().getStringUnit());
		link.setCapacity(capacity);
		link.setCapacityUnit(unit);
		link.setLinkedElement2(le1);
		link.setLinkedElement1(le2);
		return link;
	}
	
	
}