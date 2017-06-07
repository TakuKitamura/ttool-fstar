package fr.inria.oasis.vercors.cttool.model;

import java.util.Collection;

public interface Interface {

	public Collection<Message> getMessages();
	
	public String getName() ;

	public void setName(String name);

	public void addMessage(Message m);
	public void removeMessage(Message m);
	public void removeMessage(String messageName);
	public Message getMessageByName(String messageName);

	public boolean isMandatory();
	public void setMandatory(boolean isMandatory);
}
