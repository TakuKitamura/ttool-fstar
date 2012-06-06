package project.alwaystry;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class TGConnectorCommentAndroid extends TGConnectorAndroid{

	Paint paint;
	public TGConnectorCommentAndroid(int _minWidth, int _minHeight,int _maxWidth,int _maxHeight,TGConnectingPointAndroid p1,TGConnectingPointAndroid p2,View panel) {
		super(_minWidth, _minHeight, _maxWidth, _maxHeight, p1, p2, panel);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));
	
		type = TGComponentAndroid.CONNECTOR_COMMENT;
		
	}

	@Override
	public void internalDrawing(Canvas canvas) {
		// TODO Auto-generated method stub
		
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
		
		canvas.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), paint);
	}

}
