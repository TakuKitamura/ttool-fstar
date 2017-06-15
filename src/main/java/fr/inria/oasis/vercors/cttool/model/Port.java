package fr.inria.oasis.vercors.cttool.model;

/**
* Represents a Port with its Interface
* and its bindings
* @author Emil Salageanu
*
*/
public interface Port {

	String getName();
	void setName(String name);
	
	Port getToPort();
	void setToPort(Port p);
	Port getLastToPort();
	
	Port getFromPort();
	void setFromPort(Port p);
	Port getLastFromPort();
	
	Interface getInterface();
	void setInterface(Interface i);
	
	Component getFather();
	void setFather(Component c);
		
}
