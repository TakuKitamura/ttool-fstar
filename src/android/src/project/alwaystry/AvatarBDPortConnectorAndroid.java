package project.alwaystry;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AvatarBDPortConnectorAndroid extends View{

	private Point start = null;
	private int tx =-1,ty=-1,bx =-1,by=-1;
	private Point end = null;
	private Paint mPaint = new Paint();
	public AvatarBDPortConnectorAndroid(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public AvatarBDPortConnectorAndroid(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public AvatarBDPortConnectorAndroid(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void initPortConnector(int _tx, int _ty,int _bx, int _by){
		tx = _tx;
		ty = _ty;
		bx = _bx;
		by = _by;
		//start = new Point(0,0);
		//end = new Point(bx-tx,by-ty);
		/*
		start.x=0;
		start.y = 0;
		end.x = bx-tx;
		end.y = by-ty;*/
	}
	
	protected void setStart(Point _start){
		start = _start;
	}
	
	protected void setEnd(Point _end){
		end = _end;
	}
	
	protected Point getStart(){
		return start;
	}
	
	protected Point getEnd(){
		return end;
	}
	protected void onDraw(Canvas canvas){
		Log.i("connector", "draw !!!!");
		Log.i("connector", "start:"+start+" end:"+end);
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(5);
		if(tx !=-1&&ty !=-1&&bx !=-1&&by !=-1){
			canvas.drawLine(tx, ty, bx, by, mPaint);
			Log.i("connector", "draw a line");
		}
	}

	
	
}
