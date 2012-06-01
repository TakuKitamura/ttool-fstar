package project.alwaystry;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.View;


public class AvatarBDDataTypeAndroid extends TGComponentAndroid{
	
	private String stereotype = "datatype";
 
	protected Vector myAttributes;
	private View panel;
	//private int x,y;
	//private int width,height;
	private String name = "datatype0";
	private Paint mPaint;
	private Paint cpPaint;
	private Paint ePaint;
	
	private TextPaint mTextPaint;
	
	private int cptype =-1;
	
	public AvatarBDDataTypeAndroid(int _x, int _y,  View _panel)  {
		x=_x;
		y=_y;
		setPanel(_panel);
		width = 250;
		height = 200;
		
		nbConnectingPoints = 0;
        connectingPoints = new TGConnectingPointAndroid[0];
        
        addTGConnectingPointsComment();
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.rgb(156, 220, 162));
        
        cpPaint = new Paint();
        cpPaint.setAntiAlias(true);
        cpPaint.setStrokeWidth(6);
        cpPaint.setColor(Color.RED);
        
        ePaint = new Paint();
        ePaint.setAntiAlias(true);
        ePaint.setStrokeWidth(2);
        ePaint.setColor(Color.BLACK);
        
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
		
		myAttributes = new Vector();
	}
	
	public void internalDrawing(Canvas canvas) {
		int lp=x;
		int tp=y;
		int rp=x+width;
		int bp=y+height;
		
		if(selected){
			ePaint.setColor(Color.RED);
			mTextPaint.setColor(Color.RED);
		}else{
			ePaint.setColor(Color.BLACK);
			mTextPaint.setColor(Color.BLACK);
		}
		
		mPaint.setColor(Color.rgb(156, 220, 162));
		canvas.drawRect(lp+3, tp+3, rp-3, bp-3, mPaint);
		
		canvas.drawLine(lp, tp, rp, tp, ePaint);
		canvas.drawLine(lp, tp, lp, bp, ePaint);
		canvas.drawLine(rp, tp, rp,bp, ePaint);
		canvas.drawLine(lp, bp, rp, bp, ePaint);
		
		String ster = "<<"+stereotype+">>";
		mTextPaint.setFakeBoldText(true);
		canvas.drawText(ster, lp+(width-ster.length())/2, tp+25, mTextPaint);
		
		mTextPaint.setFakeBoldText(false);
		if (name != null && name.length() > 0) {
            canvas.drawText(name,lp+(width-name.length())/2, tp+38,mTextPaint);
        }
		
//		mPaint.setColor(Color.BLACK);
//		mPaint.setStrokeWidth(2);
		canvas.drawLine(x, y+42, rp, y+42, ePaint);
		
		int h=0;
		int w;
		
		h = 3;
		
		//Icon
		int cpt = h;
		int index = 0;
		String attr;
		//draw Attributes
		
	//	canvas.drawText("attribute 1", x+50+7, y+60, mTextPaint);
		
		canvas.drawLine(x, y+70, rp, y+70, ePaint);
		
		this.drawTGConnectingPoint(canvas, getCptype());
		
	}
	public View getPanel() {
		return panel;
	}
	public void setPanel(View panel) {
		this.panel = panel;
	}

	@Override
	public TGComponentAndroid isOnMe(int x1, int y1) {
		// TODO Auto-generated method stub
		if ((x1 >= x) && ((x + width) >= x1) && (y1 >= y) && ((y + height) >= y1)) {
            return this;
       }
		return null;
	}
	
	public int getCptype() {
		return cptype;
	}

	public void setCptype(int cptype) {
		this.cptype = cptype;
	}
}
