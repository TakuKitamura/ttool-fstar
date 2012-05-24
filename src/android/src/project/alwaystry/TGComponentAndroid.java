package project.alwaystry;

import android.graphics.Canvas;

public abstract class TGComponentAndroid implements CDElementAndroid{

	public static final int UML_NOTE = 301;
	public static final int NOCOMPONENT = -1;
	public static final int AVATARBD_BLOCK = 5000;
	public static final int AVATARBD_CRYPTOBLOCK = 5004;
	public static final int AVATARBD_COMPOSITION_CONNECTOR = 5001;
	public static final int AVATARBD_PORT_CONNECTOR = 5002;
	public static final int AVATARBD_DATATYPE = 5003;
	
	
	// Attributes
    protected int x, y; // absolute cd
    protected int width, height;
    
    protected String name = "TGComponentAndroid";
	
    protected boolean selected;
    protected int distanceSelected = 5;
    
    public abstract TGComponentAndroid isOnMe(int _x, int _y);
    
    public final void select(boolean b) {
        selected = b;
    }
	
    public final boolean isSelected() {
        return selected;
    }
    
	public  int getX() {
        return x;
    }
    
    public  int getY() {
        return y;
    }
    
    public  int getWidth() {
        return width;
    }
    
    public  int getHeight() {
        return height;
    }
	@Override
	public void setCd(int _x, int _y) {
		// TODO Auto-generated method stub
	    x = _x;
	    y = _y;
	}	

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public boolean isOnOnlyMe(int x1, int y1){
		if ((x1 >= x) && ((x + width) >= x1) && (y1 >= y) && ((y + height) >= y1)) {
            return true;
       }
        return false;
	}

	public abstract void internalDrawing(Canvas canvas);


}
