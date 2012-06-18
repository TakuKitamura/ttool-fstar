package project.alwaystry;

import myutilandroid.GraphicLibAndroid;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;



public class TGConnectingPointAndroid implements CDElementAndroid{

	protected int x, y;// relative cd and center of the point
	protected int state;
	protected CDElementAndroid container;
	private boolean free = true;
	
	private int id;
	
	protected boolean in;
	protected boolean out;
	
	protected int width = 8;
	protected int height = 8;
	
	protected Paint cpPaint;
	protected int myColor;
	
	public static final int NORMAL = 0;
    public static final int SELECTED = 1;
    
    protected static final int IN = Color.BLUE;
    protected static final int OUT = Color.CYAN;
    protected static final int INOUT = Color.rgb(255, 165, 0);//orange
    protected static final int NO = Color.WHITE;
	
	protected int orientation;
	
	public static final int NORTH=0;
	public static final int EAST=1;
	public static final int SOUTH=2;
	public static final int WEST=3;
	
	protected double w;
	protected double h;
	
	public TGConnectingPointAndroid(CDElementAndroid _container, int _x, int _y, boolean _in, boolean _out,double _w, double _h){
		container = _container;
        x = _x;
        y = _y;
        in = _in;
        out = _out;
        
        w = _w;
        h = _h;
        
        if (in) {
            if (out) {
                myColor = INOUT;
            } else {
                myColor = IN;
            }
        } else {
            if (out) {
                myColor = OUT;
            } else {
                myColor = NO;
            }
        }
        
        cpPaint = new Paint();
		cpPaint.setAntiAlias(true);
        
	}
	
	protected void internalDrawing(Canvas canvas){
		int mx;
        int my;
        int endx;
        int endy;
        if (state == SELECTED) {
            mx = getX() - width / 2;
            my = getY() - height / 2;
            endx = getX() + width/2;
            endy = getY() + height/2;
            
        } else {
        	mx = getX() - width ;
            my = getY() - height ;
            endx = getX() + width;
            endy = getY() + height;
            
        }
        Log.i("point", "mx: "+mx+" my: "+my+" endx: "+endx+" endy: "+endy);
        cpPaint.setColor(myColor);
        canvas.drawRect(mx, my, endx, endy,cpPaint);
        GraphicLibAndroid.doubleColorRect(canvas, mx, my, endx, endy, Color.LTGRAY, Color.BLACK);
		//canvas.drawRect(getX()-width/2, getY()-height/2, getX() + width/2, getY() + height/2, cpPaint);
	}
	
//	public void drawOutAndFreeAndCompatible(Canvas c, int connectorID) {
//      if (isOut()&& isFree()  && isCompatibleWith(connectorID)) {
//        int mx = getX();
//        int my = getY();
//        mx = mx - width / 2;
//        my = my - height / 2;
//        cpPaint.setColor(myColor);  
//        c.drawRect(mx, my, width, height,cpPaint);
//        GraphicLibAndroid.doubleColorRect(c, mx, my, width, height, Color.LTGRAY, Color.BLACK);
//      }
//    }
	
	public boolean isIn() {
        return in;
    }
    
    public boolean isOut() {
        return out;
    }
    
    public boolean isCloseTo(int _x, int _y) {
        int mx = getX();
        int my = getY();
        return GraphicLibAndroid.isInRectangle(_x, _y, mx - width, my - height, 2*width, 2*height);
    }
	
    public TGConnectingPointAndroid isOnMe(int x1,int y1){
    	if(isCloseTo(x1,y1))
    		return this;
    	else
    		return null;
    }
    
	public int getX() {
		// TODO Auto-generated method stub
		return x + container.getX() + (int)(container.getWidth() * w);
	}

	public int getY() {
		// TODO Auto-generated method stub
		return y + container.getY() + (int)(container.getHeight() * h);
	}

	public void setW(double _w) {
		w = _w;
	}
	
	public void setH(double _h) {
		h = _h;
	}
	
	 public int getId() {
        return id;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
	public void setCd(int x, int y) {
		// TODO Auto-generated method stub
		this.x = x;
		this.y = y;
	}
	
	// return true if state _s is different from the previous one
    public boolean setState(int _s){
        boolean b = false;
        if ((_s>-1) && (_s<2)) {
            if (state != _s)
                b = true;
            state = _s;
        }
        return b;
    }
    
    public int getState() {
        return state;
    }

    public CDElementAndroid getFather() {
        return container;
    }
    
    public void setFather(CDElementAndroid cd) {
        container = cd;
    }
    
    public boolean isFree() {
    	return free;
    }
    
    public void setFree(boolean b) {
    	free = b;
    }
    
    public String getName() {
        return container.getName();
    }

    public boolean isCompatibleWith(int type) {
        return true;
    }
}
