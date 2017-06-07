package fr.inria.oasis.vercors.cttool.model;

public class MessageImpl implements Message{

	private String name;
	
	public MessageImpl(String name)
	{
		this.name=name;
			}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name=name;
	}

	public boolean equals(Message m) {
		return (this.name.equals(m.getName()));
	}

	
}
