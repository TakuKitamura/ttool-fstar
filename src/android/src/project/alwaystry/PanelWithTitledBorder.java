package project.alwaystry;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PanelWithTitledBorder extends RelativeLayout{

	protected String name = "Adding Attributes";
	
	public PanelWithTitledBorder(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		
	}
	public PanelWithTitledBorder(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
	}
	public PanelWithTitledBorder(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	public void setName(String na){
		name = na;
	}
	
	protected void dispatchDraw(Canvas canvas){
		super.dispatchDraw(canvas);
		
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1);
		
		TextPaint tPaint = new TextPaint();
		tPaint.setAntiAlias(true);
	    tPaint.setTextAlign(Paint.Align.LEFT);
        tPaint.setColor(Color.BLUE);
        tPaint.setTextSize(20);
        
		canvas.drawLine(0, 5, 10, 5, paint);
		canvas.drawText(name,13, 15, tPaint);
		canvas.drawLine(10+name.length()*10+3, 5, this.getWidth(), 5, paint);
		canvas.drawLine(0, 5, 0, this.getHeight(), paint);
		canvas.drawLine(0, this.getHeight(), this.getWidth(), this.getHeight(), paint);
		canvas.drawLine(this.getWidth(), 5, this.getWidth(), this.getHeight(), paint);
		
	}
}
