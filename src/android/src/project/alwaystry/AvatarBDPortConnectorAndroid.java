package project.alwaystry;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AvatarBDPortConnectorAndroid extends TGConnectorAndroid{

	private Paint paint;
	private LinkedList internalpoints;
	
	public AvatarBDPortConnectorAndroid(int _minWidth, int _minHeight,int _maxWidth,int _maxHeight,TGConnectingPointAndroid p1,TGConnectingPointAndroid p2,View panel){
		super(_minWidth, _minHeight, _maxWidth, _maxHeight, p1, p2, panel);
		
		paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		
		type = TGComponentAndroid.AVATARBD_PORT_CONNECTOR;
	}
	
	public void internalDrawing(Canvas canvas){
		
		if(p1.isFree() || p2.isFree()){
			((AvatarBDPanelAndroid)panel).getCompolist().remove(this);
			return;
		}
		
		if(selected){
			paint.setColor(Color.RED);
		}else{
			paint.setColor(Color.BLACK);
		}
		
		if(movingHead){
			paint.setColor(Color.MAGENTA);
		}
//		if(tx !=-1 && ty!=-1 && bx!=-1 && by!=-1)
//			canvas.drawLine(tx, ty, bx, by, paint);
		//if(hasStart() && hasEnd()){
		Log.i("portconnector", "internaldrawing");
		Log.i("portconnector", "p1: "+p1.isFree()+"p2 :" +p2.isFree());
			canvas.drawRect(p1.getX()-p1.getWidth()/2, p1.getY()-p1.getHeight()/2, p1.getX()+p1.getWidth()/2, p1.getY()+p1.getHeight()/2, paint);
			canvas.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), paint);
			canvas.drawRect(p2.getX()-p2.getWidth()/2, p2.getY()-p2.getHeight()/2, p2.getX()+p2.getWidth()/2, p2.getY()+p2.getHeight()/2, paint);
	//	}
	}

	public LinkedList getInternalpoints() {
		return internalpoints;
	}
	public void setInternalpoints(LinkedList internalpoints) {
		this.internalpoints = internalpoints;
	}

	@Override
	protected boolean editOndoubleClick(int _x, int _y) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
