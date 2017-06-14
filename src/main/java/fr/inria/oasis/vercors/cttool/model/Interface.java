package fr.inria.oasis.vercors.cttool.model;

import java.util.Collection;

public interface Interface {

	Collection<Message> getMessages();
	
	String getName() ;

	void setName(String name);

	void addMessage(Message m);
	void removeMessage(Message m);
	void removeMessage(String messageName);
	Message getMessageByName(String messageName);

	boolean isMandatory();
	void setMandatory(boolean isMandatory);
}
