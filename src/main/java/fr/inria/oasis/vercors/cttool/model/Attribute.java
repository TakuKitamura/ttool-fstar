package fr.inria.oasis.vercors.cttool.model;
/**
* Represents an attribute of a component
* 
* @author Emil Salageanu
*
*/
public interface Attribute {

	
	
//	access
int PRIVATE = 0;
    int PROTECTED = 1;
    int PUBLIC = 2;
	
    
//  type
int NATURAL = 0;
    int BOOLEAN = 4;
    int OTHER = 5;
    
    
    
	String getName();
	void setName(String name);
	
	int getAccess();
	void setAccess(int access);
	
	int getType();
	void setType(int type);
	
	String getInitialValue();
	void setInitialValue(String o);
	
}
