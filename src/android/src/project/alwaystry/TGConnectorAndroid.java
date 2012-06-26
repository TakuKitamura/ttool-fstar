package project.alwaystry;

import myutilandroid.GraphicLibAndroid;
import android.graphics.Canvas;
import android.view.View;

public abstract class TGConnectorAndroid extends TGComponentAndroid{
	
	protected TGConnectingPointAndroid p1, p2; // initial and destination connecting points.
	protected boolean movingHead;
	protected int type;
	
	public TGConnectorAndroid(int _minWidth, int _minHeight,int _maxWidth,int _maxHeight,TGConnectingPointAndroid p1,TGConnectingPointAndroid p2,View panel){
		super(Math.min(p1.getX(), p2.getX())-p1.width/2, Math.min(p1.getY(), p2.getY())-p1.height/2, _minWidth, _minHeight, _maxWidth, _maxHeight, panel);
		this.p1 = p1;
		this.p2 = p2;
		
	}
	
	public abstract void internalDrawing(Canvas canvas) ;
		


	
	public TGComponentAndroid isOnMe(int _x, int _y) {
		
		if( p1.isOnMe(_x, _y)!=null|| p2.isOnMe(_x, _y)!=null){
			return this;
		}
		
		
		float mx = (p1.getX()-p2.getX())*(_y-p1.getY())/(p1.getY()-p2.getY()) +p1.getX();
		float my = (p1.getY()-p2.getY())*(_x-p1.getX())/(p1.getX()-p2.getY()) +p1.getY();
		
		if(Math.abs(mx-_x)<=20 || Math.abs(my-_y)<= 20){
			return this;
		}
		return null;
	}
	 
	public void setMovingHead(boolean m){
		movingHead = m;
	}
	public boolean getMoveingHead(){
		return movingHead;
	}
	
	public TGConnectingPointAndroid getTGConnectingPointP1() {
        return p1;
    }
    
    public TGConnectingPointAndroid getTGConnectingPointP2() {
        return p2;
    }
    
    public void setP1(TGConnectingPointAndroid p) {
        p1 = p;
    }
    
    public void setP2(TGConnectingPointAndroid p) {
        p2 = p;
    }
	
	public boolean isP1(CDElementAndroid cd) {
        return (p1 == cd);
    }
    
    public boolean isP2(CDElementAndroid cd) {
        return (p2 == cd);
    }
    
    public CDElementAndroid [] closerPToClickFirst(int x, int y) {
        CDElementAndroid [] cde = new CDElementAndroid[2];
   
        int distance1 = (int)GraphicLibAndroid.distanceBetweenTwoP(x, y, p1.getX(), p1.getY());
        int distance2 = (int)GraphicLibAndroid.distanceBetweenTwoP(x, y, p2.getX(), p2.getY());
        if (distance1 < distance2) {
            cde[0] = p1;
            cde[1] = p2;
        } else {
            cde[0] = p2;
            cde[1] = p1;
        }
        
        return cde;
    }
    
    public int getType(){
    	return type;
    }
    
    public int getWidth(){
    	return Math.abs(p1.getX()-p2.getX());
    }
    
    public int getHeight(){
    	return Math.abs(p1.getY()-p2.getY());
    }
    
    public int getX(){
    	return Math.min(p1.getX(), p2.getX());
    }
    
    public int getY(){
    	return Math.min(p1.getY(), p2.getY());
    }
	
}
