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
