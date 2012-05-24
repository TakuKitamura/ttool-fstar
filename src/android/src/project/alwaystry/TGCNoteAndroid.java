package project.alwaystry;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.view.View;

public class TGCNoteAndroid extends TGComponentAndroid{
	
	
	private View panel;
	
	private Paint mPaint;
	private Paint ePaint;
	private Paint tPaint;
	private TextPaint mTextPaint;
	
	protected String value; //applies if editable
    protected String name ;
    
    protected int limit = 15;
	
	public TGCNoteAndroid(int _x, int _y,View _panel){
		x=_x;
		y=_y;
		panel = _panel;
		width = 150;
        height = 30;
        //minWidth = 50;
        //minHeight = 20;
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
       // mPaint.setColor(Color.rgb(193, 218, 241));
        
        ePaint = new Paint();
        ePaint.setAntiAlias(true);
        ePaint.setStrokeWidth(2);
        ePaint.setColor(Color.BLACK);
        
        tPaint = new Paint();
        tPaint.setAntiAlias(true);
        tPaint.setStrokeWidth(2);
        tPaint.setColor(Color.LTGRAY);
        
        
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
        
        name = "UML Note";
        value = "UML note:Long press to edit";
	}

	@Override
	public TGComponentAndroid isOnMe(int x1, int y1) {
		if ((x1 >= x) && ((x + width) >= x1) && (y1 >= y) && ((y + height) >= y1)) {
            return this;
       }
        return null;
		
	}

	@Override
	public void internalDrawing(Canvas canvas) {
		if(selected){
			ePaint.setColor(Color.RED);
			tPaint.setColor(Color.RED);
		}else{
			ePaint.setColor(Color.BLACK);
			tPaint.setColor(Color.LTGRAY);
		}			
		
		canvas.drawLine(x, y, x+width, y, ePaint);
		canvas.drawLine(x, y, x, y+height, ePaint);
		canvas.drawLine(x, y+height, x+width-limit, y+height, ePaint);
		canvas.drawLine(x+width, y, x+width, y+height-limit, ePaint);
		
		mPaint.setColor(Color.rgb(173, 190, 234));
		
		Path path = new Path();
		path.moveTo((float)x, (float)y);
		path.lineTo((float)(x+width), (float)y);
		path.lineTo((float)(x + width), (float)(y+height-limit));
		path.lineTo((float)(x + width-limit), (float)(y+height));
		path.lineTo((float)x, (float)(y+height));
		
		mPaint.setStyle(Paint.Style.FILL);
		
		canvas.drawPath(path, mPaint);
		
		Path path1 = new Path();
		path1.moveTo((float)(x + width), (float)(y+height-limit));
		path1.lineTo((float)(x+width-4), (float)(y+height-limit+3));
		path1.lineTo((float)(x + width-10), (float)(y+height-limit+2));
		path1.lineTo((float)(x + width-limit), (float)(y+height));
		
		ePaint.setStyle(Paint.Style.STROKE);
		canvas.drawPath(path1, ePaint);
		
		tPaint.setStyle(Paint.Style.FILL);
		canvas.drawPath(path1, tPaint);
		
		
		canvas.drawText(value, x+80, y+15, mTextPaint);
	}

}
