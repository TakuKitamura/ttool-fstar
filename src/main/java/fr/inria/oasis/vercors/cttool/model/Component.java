package fr.inria.oasis.vercors.cttool.model;

import java.util.Collection;

/**
* Represents a component with the list of subcomponents,
* the ports and attributes
* @author Emil Salageanu
*
*/

public interface Component {

	public String getName();
	public void setName(String name);
	
	//Sub Components related methods:
	public Collection<Component> getSubComponents();
	public boolean addSubComponent(Component c);
	public boolean removeSubComponent(Component c);
	public boolean removeSubCompoenentByName(String compName);
	public Component getSubComponentByName(String compName);
	public Collection<Component> getAllPrimitives();
	public boolean isDescendant(Component comp);
	
	//Ports related methods	
	public Collection<Port> getPorts();
	public boolean addPort(Port p);
	public boolean removePort(Port p);
	public boolean removePortByName(String portName);
	public Port getPortByName(String portName);
	
	//Attributes related methods
	public Collection<Attribute> getAttributes();
	public boolean addAttribute(Attribute a);
	public boolean removeAttribute(Attribute a);
	public boolean removeAttributeByName(String attributeName);
	public Attribute getAttributeByName(String attributeName);
	 
	public boolean isPrimitive();
	

	public void setBehaviour(Behaviour b);
	public Behaviour getBehaviour();
	
	public Component getFather();
	public void setFather(Component f);

	public String getPath();
	public String prettyPrint();
}
