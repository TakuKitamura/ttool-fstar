package fr.inria.oasis.vercors.cttool.model;

public class AttributeImpl implements Attribute{

	private String name;
	private int myType;
	private int access;
	private String initialValue;
	
	
	
	
	public AttributeImpl(String name, int type) {
		super();
		this.name = name;
		this.myType=type;
		this.access=Attribute.PUBLIC;
	}

	
	
	
	public AttributeImpl(String name, int myType, int access) {
		this.name = name;
		this.myType = myType;
		this.access = access;
	}

	public AttributeImpl(String name, int myType, int access,String initialValue) {
		this.name = name;
		this.myType = myType;
		this.access = access;
		this.initialValue=initialValue;
	}



	public int getAccess() {
		return access;
	}




	public void setAccess(int access) {
		this.access = access;
	}




	public String getInitialValue() {
		return initialValue;
	}




	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}




	public int getType() {
		return myType;
	}




	public void setType(int myType) {
		this.myType = myType;
	}




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}

	public String toString()
	{
		return this.name;
	}

}
