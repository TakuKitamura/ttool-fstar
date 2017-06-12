package fr.inria.oasis.vercors.cttool.model;

import java.util.Collection;

/**
* Represents a component with the list of subcomponents,
* the ports and attributes
* @author Emil Salageanu
*
*/

public interface Component {

	String getName();
	void setName(String name);
	
	//Sub Components related methods:
    Collection<Component> getSubComponents();
	boolean addSubComponent(Component c);
	boolean removeSubComponent(Component c);
	boolean removeSubCompoenentByName(String compName);
	Component getSubComponentByName(String compName);
	Collection<Component> getAllPrimitives();
	boolean isDescendant(Component comp);
	
	//Ports related methods	
    Collection<Port> getPorts();
	boolean addPort(Port p);
	boolean removePort(Port p);
	boolean removePortByName(String portName);
	Port getPortByName(String portName);
	
	//Attributes related methods
    Collection<Attribute> getAttributes();
	boolean addAttribute(Attribute a);
	boolean removeAttribute(Attribute a);
	boolean removeAttributeByName(String attributeName);
	Attribute getAttributeByName(String attributeName);
	 
	boolean isPrimitive();
	

	void setBehaviour(Behaviour b);
	Behaviour getBehaviour();
	
	Component getFather();
	void setFather(Component f);

	String getPath();
	String prettyPrint();
}
