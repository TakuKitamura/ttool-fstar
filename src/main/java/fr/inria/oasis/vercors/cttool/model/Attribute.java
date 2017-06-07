package fr.inria.oasis.vercors.cttool.model;
/**
* Represents an attribute of a component
* 
* @author Emil Salageanu
*
*/
public interface Attribute {

	
	
//	access
    public final static int PRIVATE = 0;
    public final static int PROTECTED = 1;
    public final static int PUBLIC = 2;
	
    
//  type
    public final static int NATURAL = 0;
    public final static int BOOLEAN = 4;
    public final static int OTHER = 5;
    
    
    
	public String getName();
	public void setName(String name);
	
	public int getAccess();
	public void setAccess(int access);
	
	public int getType();
	public void setType(int type);
	
	public String getInitialValue();
	public void setInitialValue(String o);
	
}
