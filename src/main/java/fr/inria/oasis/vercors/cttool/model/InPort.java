package fr.inria.oasis.vercors.cttool.model;

public class InPort extends PortImpl{

	public InPort(String name)
	{
		super(name);
	}
	
	public InPort(String name, Component father)
	{
		super(name,father);
	}

  public String toString()
  {
	  return "InPort "+super.toString();
  }

}
