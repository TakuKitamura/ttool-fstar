package project.alwaystry;

import android.graphics.Canvas;
import android.view.View;

public abstract class TGComponentAndroid implements CDElementAndroid{

	public static final int CONNECTOR_COMMENT = 118;
	public static final int UML_NOTE = 301;
	public static final int NOCOMPONENT = -1;
	public static final int AVATARBD_BLOCK = 5000;
	public static final int AVATARBD_CRYPTOBLOCK = 5004;
	public static final int AVATARBD_COMPOSITION_CONNECTOR = 5001;
	public static final int AVATARBD_PORT_CONNECTOR = 5002;
	public static final int AVATARBD_DATATYPE = 5003;
	
	
	// Attributes
    protected int x, y; // absolute cd
    protected int width, height,minWidth,maxWidth,minHeight,maxHeight;
    protected View panel;
    
    protected String name = "TGComponentAndroid";
	
    protected boolean selected;
    protected int distanceSelected = 20;
    
    protected int nbConnectingPoints;
	protected TGConnectingPointAndroid[] connectingPoints;
	
	protected int cptype;
    protected float rescale;
	
    public abstract TGComponentAndroid isOnMe(int _x, int _y);
    
    public TGComponentAndroid(int _x, int _y, int _minWidth, int _minHeight,int _maxWidth,int _maxHeight, View _panel){
    	x = _x;
    	y =_y;
    	
    	minWidth =_minWidth;
    	minHeight = _minHeight;
    	
    	maxWidth = _maxWidth;
    	maxHeight = _maxWidth;
    	
    	panel = _panel;
    }
    
    
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
    
    public void setWidth(int w){
    	width = w;
    }
    
    public void setHeight(int h){
    	height = h;
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

	public int getCptype() {
		return cptype;
	}

	public void setCptype(int cptype) {
		this.cptype = cptype;
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

	public boolean isInRectangle(int x1, int y1, int width, int height) {
        if ((getX() < x1) || (getY() < y1) || ((getX() + this.width) > (x1 + width)) || ((getY() + this.height) > (y1 + height))) {
            //System.out.println("Not in my rectangle " + this);
            return false;
        } else {
            return true;
        }
    }
	
	public boolean areAllInRectangle(int x1, int y1, int width, int height) {
	     //   TGComponent tgc;
	        
        if (!isInRectangle(x1, y1, width, height)) {
            return false;
        }
//	        
//	        for(int i=0; i<nbInternalTGComponent; i++) {
//	            if (!tgcomponent[i].isInRectangle(x1, y1, width, height)) {
//	                return false;
//	            }
//	        }
        return true;
    }
	
	public int makeTGConnectingPointsComment(int nb) {
        int i, len;
        
        //System.out.println("Adding comment points to " + this.getName());
        if (connectingPoints != null) {
            TGConnectingPointAndroid[] tmp = connectingPoints;
            len = tmp.length;
            nbConnectingPoints = nbConnectingPoints + nb;
            connectingPoints = new TGConnectingPointAndroid[nbConnectingPoints];
            for(i=0; i<len; i++) {
                connectingPoints[i] = tmp[i];
            }
        }
        else {
            nbConnectingPoints =  nb;
            connectingPoints = new TGConnectingPointAndroid[nbConnectingPoints];
            len = 0;
        }
        return len;
    }
	
	public void cleanAllPoints(){
		for(int i=0; i<nbConnectingPoints;i++){
			connectingPoints[i].setState(TGConnectingPointAndroid.NORMAL);
			connectingPoints[i].setFree(true);
		}
	}
    
	public void addTGConnectingPointsCommentMiddle() {
        int len = makeTGConnectingPointsComment(8);
        generateTGConnectingPointsComment(len, -0.5, 0);
    }
    
    public void addTGConnectingPointsCommentCorner() {
        int len = makeTGConnectingPointsComment(4);
        generateTGConnectingPointsCommentCorner(len, 0, 0);
    }
    
    public void addTGConnectingPointsCommentTop() {
        int len = makeTGConnectingPointsComment(3);
        generateTGConnectingPointsCommentLine(len, 0, 0);
    }
    
    public void addTGConnectingPointsCommentDown() {
        int len = makeTGConnectingPointsComment(3);
        generateTGConnectingPointsCommentLine(len, 0, 1.0);
    }
    
    public void addTGConnectingPointsComment() {
        int len = makeTGConnectingPointsComment(8);
        generateTGConnectingPointsComment(len, 0, 0);
    }
    
    public void generateTGConnectingPointsComment(int len, double decw, double dech) {
        connectingPoints[len] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 0.0 + decw, 0.0 + dech);
        connectingPoints[len + 1] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 0.5 + decw, 0.0 + dech);
        connectingPoints[len + 2] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 1.0 + decw, 0.0 + dech);
        connectingPoints[len + 3] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 0.0 + decw, 0.5 + dech);
        connectingPoints[len + 4] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 1.0 + decw, 0.5 + dech);
        connectingPoints[len + 5] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 0.0 + decw, 1.0 + dech);
        connectingPoints[len + 6] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 0.5 + decw, 1.0 + dech);
        connectingPoints[len + 7] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 1.0 + decw, 1.0 + dech);
    }
    
    public void generateTGConnectingPointsCommentCorner(int len, double decw, double dech) {
        connectingPoints[len] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 0.0 + decw, 0.0 + dech);
        connectingPoints[len + 1] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 1.0 + decw, 0.0 + dech);
        connectingPoints[len + 2] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 0.0 + decw, 1.0 + dech);
        connectingPoints[len + 3] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 1.0 + decw, 1.0 + dech);
    }
    
    public void generateTGConnectingPointsCommentLine(int len, double decw, double dech) {
        connectingPoints[len] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 0.0 + decw, 0.0 + dech);
        connectingPoints[len + 1] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 1.0 + decw, 0.0 + dech);
        connectingPoints[len + 2] = new TGConnectingPointCommentAndroid(this, 0, 0, true, true, 0.5 + decw, 0.0 + dech);
    }
    
    public void drawTGConnectingPoint(Canvas canvas, int type) {
        //System.out.println("I am " + getName());
        for (int i=0; i<nbConnectingPoints; i++) {
            if (connectingPoints[i].isCompatibleWith(type)) {
                connectingPoints[i].internalDrawing(canvas);
            }
        }
		
//		if (this instanceof HiddenInternalComponents) {
//			if (((HiddenInternalComponents)(this)).areInternalsHidden()) {
//				return;
//			}
//		}
//		
//        for(int i=0; i<nbInternalTGComponent; i++) {
//            tgcomponent[i].drawTGConnectingPoint(g, type);
//        }
    }
    
    public float getRescale(){
    	return rescale;
    }
    public void setRescale(float f){
    	rescale = f;
    	
    	int newx,newy,neww,newh;
    	neww = (int)(this.getWidth()*f);
		newh = (int)(this.getHeight()*f);
		
		if((neww>maxWidth && newh >maxHeight) || (neww<minWidth || newh <minHeight)){
			return;
		}
		
    	
    	if(f > 1){
    		newx = (int)(this.getX()-50*(rescale-1)/2);
    		newy = (int)(this.getY()-50*(rescale-1)/2);
    		
    	}else{
    		newx = (int)(this.getX()+50*(1-rescale)/2);
    		newy = (int)(this.getY()+50*(1-rescale)/2);
    		
    	}
    	
    	
		
    	this.setCd(newx, newy);
    	this.setHeight(newh);
    	this.setWidth(neww);
    	
    }
    
    protected abstract boolean editOndoubleClick(int _x,int _y);
}
