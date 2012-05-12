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

public class AvatarBDPortConnectorAndroid {

	private int tx=-1,ty=-1,bx=-1,by=-1;
	private AvatarBDConnectingPointAndroid outPoint,inPoint;
	private Paint paint;
	private LinkedList internalpoints;
	
	public void setStart(int _tx,int _ty){
		tx = _tx;
		ty = _ty;
	}
	public void setEnd(int _bx, int _by){
		bx = _bx;
		by = _by;
	}
	public boolean hasStart(){/*
		if(tx !=-1 && ty !=-1){
			return true;
		}else{
			return false;
		}	*/
		if(outPoint != null){
			return true;
		}
		return false;
	}
	
	public boolean hasEnd(){
//		if(bx !=-1 && by !=-1){
//			return true;
//		}else{
//			return false;
//		}
		if(inPoint != null){
			return true;
		}
		return false;
	}
	
	public AvatarBDPortConnectorAndroid(){
		outPoint = null;
		inPoint = null;
		
		paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
	}
	
	protected void internalDrawing(Canvas canvas){
//		if(tx !=-1 && ty!=-1 && bx!=-1 && by!=-1)
//			canvas.drawLine(tx, ty, bx, by, paint);
		if(hasStart() && hasEnd()){
			canvas.drawRect(outPoint.getX()-outPoint.getWidth()/2, outPoint.getY()-outPoint.getHeight()/2, outPoint.getX()+outPoint.getWidth()/2, outPoint.getY()+outPoint.getHeight()/2, paint);
			canvas.drawLine(outPoint.getX(), outPoint.getY(), inPoint.getX(), inPoint.getY(), paint);
			canvas.drawRect(inPoint.getX()-inPoint.getWidth()/2, inPoint.getY()-inPoint.getHeight()/2, inPoint.getX()+inPoint.getWidth()/2, inPoint.getY()+inPoint.getHeight()/2, paint);
		}
	}
	public AvatarBDConnectingPointAndroid getOutPoint() {
		return outPoint;
	}
	public void setOutPoint(AvatarBDConnectingPointAndroid outPoint) {
		this.outPoint = outPoint;
	}
	public AvatarBDConnectingPointAndroid getInPoint() {
		return inPoint;
	}
	public void setInPoint(AvatarBDConnectingPointAndroid inPoint) {
		this.inPoint = inPoint;
	}
	public LinkedList getInternalpoints() {
		return internalpoints;
	}
	public void setInternalpoints(LinkedList internalpoints) {
		this.internalpoints = internalpoints;
	}

	
}
