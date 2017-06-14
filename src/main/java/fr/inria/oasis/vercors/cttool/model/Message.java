package fr.inria.oasis.vercors.cttool.model;

public interface Message {

	String getName();
	void setName(String name);
	
	
	boolean equals(Message m);
}
