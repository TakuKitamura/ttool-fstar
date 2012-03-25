package project.alwaystry;

import project.alwaystry.drageventblock.MyShadowBuilder;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Blockrectangle extends View{
	static final String Tag = "blockrectangle";
	private Paint mPaint;
	private TextPaint mTextPaint;
	String mText;
	
	public Blockrectangle(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// TODO Auto-generated constructor stub
		setFocusable(true);
        setClickable(true);
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(6);
        mPaint.setColor(Color.LTGRAY);
        
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
        
        mText = "Name";
        
		// TODO Auto-generated constructor stub
	}
	
	String getmText(){
		return mText;
	}
	
	void setmText(String text){
		mText = text;
	}
	
	
	protected void onDraw(Canvas canvas) {
		float wf = getWidth();
        float hf = getHeight();
        
		canvas.drawRect(0, 0, wf, hf, mPaint);
		
		if (mText != null && mText.length() > 0) {
            canvas.drawText(mText, 0, mText.length(),
                   30, 10,
                    mTextPaint);
        }
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(100,100);
    }

}
