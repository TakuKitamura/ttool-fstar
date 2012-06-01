package project.alwaystry;

import android.graphics.Canvas;

public abstract class TGConnectorAndroid extends TGComponentAndroid{
	
	protected TGConnectingPointAndroid p1, p2; // initial and destination connecting points.
	protected AvatarBDPanelAndroid panel;

	public TGConnectorAndroid(TGConnectingPointAndroid p1,TGConnectingPointAndroid p2,AvatarBDPanelAndroid panel){
		this.p1 = p1;
		this.p2 = p2;
		this.panel = panel;
		x =Math.min(p1.getX(), p2.getX())-p1.width/2;
		y = Math.min(p1.getY(), p2.getY())-p1.height/2;
		width = Math.abs(p2.getX()-p1.getX())+p1.width;
		height = Math.abs(p2.getY()-p1.getY())+p1.height;
		
	}
	@Override
	public abstract void internalDrawing(Canvas canvas) ;
		// TODO Auto-generated method stub

	@Override
	public TGComponentAndroid isOnMe(int _x, int _y) {
		// TODO Auto-generated method stub
		
		
		if( p1.isOnMe(_x, _y)!=null|| p2.isOnMe(_x, _y)!=null){
			return this;
		}
		
		float mx = (p1.getX()-p2.getX())*(_y-p1.getY())/(p1.getY()-p2.getY()) +p1.getX();
		float my = (p1.getY()-p2.getY())*(_x-p1.getX())/(p1.getX()-p2.getY()) +p1.getY();
		
		if(Math.abs(mx-_x)<=distanceSelected || Math.abs(my-_y)<= distanceSelected){
			return this;
		}
		return null;
	}
	 

}
