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

	//private int tx=-1,ty=-1,bx=-1,by=-1;
	//private AvatarBDConnectingPointAndroid outPoint,inPoint;
	private Paint paint;
	private LinkedList internalpoints;
	
//	public void setStart(int _tx,int _ty){
//		tx = _tx;
//		ty = _ty;
//	}
//	public void setEnd(int _bx, int _by){
//		bx = _bx;
//		by = _by;
//	}
//	public boolean hasStart(){/*
//		if(tx !=-1 && ty !=-1){
//			return true;
//		}else{
//			return false;
//		}	*/
//		if(outPoint != null){
//			return true;
//		}
//		return false;
//	}
//	
//	public boolean hasEnd(){
////		if(bx !=-1 && by !=-1){
////			return true;
////		}else{
////			return false;
////		}
//		if(inPoint != null){
//			return true;
//		}
//		return false;
//	}
	
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
//	public AvatarBDConnectingPointAndroid getOutPoint() {
//		return outPoint;
//	}
//	public void setOutPoint(AvatarBDConnectingPointAndroid outPoint) {
//		this.outPoint = outPoint;
//	}
//	public AvatarBDConnectingPointAndroid getInPoint() {
//		return inPoint;
//	}
//	public void setInPoint(AvatarBDConnectingPointAndroid inPoint) {
//		this.inPoint = inPoint;
//	}
	public LinkedList getInternalpoints() {
		return internalpoints;
	}
	public void setInternalpoints(LinkedList internalpoints) {
		this.internalpoints = internalpoints;
	}

	
}
