package fr.inria.oasis.vercors.cttool.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

	

public class InterfaceImpl implements Interface{
	
	private String name;
	private Collection<Message> messages=new LinkedList<Message>();
	
	private boolean isMandatory;
	
	
	public InterfaceImpl(String name, boolean isMandatory)
	{
		this.name=name;
		this.isMandatory=isMandatory;
	}
	
	public InterfaceImpl(String name,Collection<Message> messages, boolean isMandatory )
	{
		this.name=name;
		this.messages=messages;
		this.isMandatory=isMandatory;
	}
	
	
	
	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
	public Collection<Message> getMessages() {
		return messages;
	}

	public void addMessage(Message m) {
		this.messages.add(m);
		
	}

	public void removeMessage(Message m) {
		this.messages.remove(m);
	}

	public void removeMessage(String messageName) {
	
		Message m=getMessageByName(messageName);
		messages.remove(m);
	}

	public Message getMessageByName(String messageName) {
		Iterator<Message> it = this.messages.iterator();
		while(it.hasNext())
		{
			Message m=it.next();
			if (m.getName().equals(messageName))
				return m;
		}
	 return null;
	}

	/*
	 * First Implemenation of is compatible
	 * Two interfaces are compatible if the have exactly the same messages
	 * in the same order
	 */
	/*public boolean isCompatibleWith(Interface otherInterface)
	{
		Iterator <Message> it=messages.iterator();
		while (it.hasNext())
		{
			
		}
		
		
		
		return true;		
	}
	*/
	
	
}
