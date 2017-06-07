package fr.inria.oasis.vercors.cttool.model;

public interface Message {

	public String getName();
	public void setName(String name);
	
	
	public boolean equals(Message m);
}
