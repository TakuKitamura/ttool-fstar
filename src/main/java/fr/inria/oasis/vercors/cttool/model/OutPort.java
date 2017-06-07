package fr.inria.oasis.vercors.cttool.model;

public class OutPort extends PortImpl{

	public OutPort(String name)
	{
		super(name);
	}
	
	public OutPort(String name, Component father)
	{
		super(name,father);
	}


	public String toString()
	  {
		  return "OutPort "+super.toString();
	  }

}