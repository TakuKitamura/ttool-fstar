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



package fr.inria.oasis.vercors.cttool.model;

public class PortImpl implements Port{

	private String name;
	private Port toPort;
	private Port fromPort;
	private Interface myInterface;
	private Component father;
	
	public PortImpl(String name)
	{
		this.name=name;
	}
	
	public PortImpl(String name, Component father)
	{
		this.name=name;
		this.father=father;
	}
	
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name=name;		
	}

	public Port getToPort() {
		return toPort;
	}

	public void setToPort(Port p) {
	toPort=p;	
	}

	public Port getFromPort() {
		return fromPort;
	}

	public void setFromPort(Port p) {
		this.fromPort=p;	
	}

	public Interface getInterface() {
		return this.myInterface;
	}

	public void setInterface(Interface i) {
		this.myInterface=i;	}

	public Component getFather() {
		return this.father;
	}

	public void setFather(Component c) {
		this.father=c;
	}

	public String toString()
	{
		String out="";
		String fp="";
		String tp="";
		String itf="";
		
		if (fromPort!=null)
			fp=" fromPort "+fromPort.getName();
		if (toPort!=null)
			tp=" toPort "+toPort.getName();
		if (this.myInterface!=null)
			itf="itf "+myInterface.getName()+" ";
		
		out+=" "+name+fp+tp+itf;
		
		return out;
	}

	public Port getLastToPort() {
		if (this.getToPort()==null) return null;
		Port nextToPort=this.getToPort();
		while (nextToPort.getToPort()!=null)
		{
			nextToPort=nextToPort.getToPort();
		}
		return nextToPort;
	}

	public Port getLastFromPort() {
		if (this.getFromPort()==null) return null;
		Port nextFromPort=this.getFromPort();
		while (nextFromPort.getFromPort()!=null)
		{
			nextFromPort=nextFromPort.getFromPort();
		}
		return nextFromPort;
	}
	
}
