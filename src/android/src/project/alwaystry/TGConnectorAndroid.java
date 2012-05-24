package project.alwaystry;

import android.graphics.Canvas;

public abstract class TGConnectorAndroid extends TGComponentAndroid{
	
	protected AvatarBDConnectingPointAndroid p1, p2; // initial and destination connecting points.
	

	public TGConnectorAndroid(AvatarBDConnectingPointAndroid p1,AvatarBDConnectingPointAndroid p2){
		this.p1 = p1;
		this.p2 = p2;
	}
	@Override
	public abstract void internalDrawing(Canvas canvas) ;
		// TODO Auto-generated method stub

	@Override
	public TGComponentAndroid isOnMe(int _x, int _y) {
		// TODO Auto-generated method stub
		
		float mx = (p1.getX()-p2.getX())*(_y-p1.getY())/(p1.getY()-p2.getY()) +p1.getX();
		float my = (p1.getY()-p2.getY())*(_x-p1.getX())/(p1.getX()-p2.getY()) +p1.getY();
		
		if(Math.abs(mx-_x)<=distanceSelected || Math.abs(my-_y)<= distanceSelected){
			return this;
		}
		return null;
	}
	 

}
