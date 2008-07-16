package fr.inria.oasis.vercors.cttool.model;

/**
* Represents a Port with its Interface
* and its bindings
* @author Emil Salageanu
*
*/
public interface Port {

	public String getName();
	public void setName(String name);
	
	public Port getToPort();
	public void setToPort(Port p);
	public Port getLastToPort();
	
	public Port getFromPort();
	public void setFromPort(Port p);
	public Port getLastFromPort();
	
	public Interface getInterface();
	public void setInterface(Interface i);
	
	public Component getFather();
	public void setFather(Component c);
		
}
