package project.alwaystry;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

public class AvatarBDBlockAndroid extends TGComponentAndroid{

	private String stereotype = "block";
	private String name = "Name";

	private Paint mPaint;
	private Paint cpPaint;
	private Paint ePaint;
	
	
	
	private TextPaint mTextPaint;
	
	protected Vector myAttributes, myMethods, mySignals;
	
	

	public AvatarBDBlockAndroid(int _x, int _y, int _minWidth, int _minHeight,int _maxWidth,int _maxHeight, View _panel){
		super(_x, _y, _minWidth, _minHeight, _maxWidth, _maxHeight, _panel);

		width = 250;
		height = 200;
		
		//showConnectingPoints = false;
		select(false);
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.rgb(193, 218, 241));
        
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
        
        nbConnectingPoints = 16;
        connectingPoints = new AvatarBDConnectingPointAndroid[16];
        
        connectingPoints[0] = new AvatarBDConnectingPointAndroid(this,0,0,true,true,0,0);
        connectingPoints[1] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.5, 0.0);
        connectingPoints[2] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 1.0, 0.0);
        connectingPoints[3] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.0, 0.5);
        connectingPoints[4] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 1.0, 0.5);
        connectingPoints[5] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.0, 1.0);
        connectingPoints[6] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.5, 1.0);
        connectingPoints[7] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 1.0, 1.0);
        
        connectingPoints[8] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoints[9] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.75, 0.0);
        connectingPoints[10] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.0, 0.25);
        connectingPoints[11] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 1.0, 0.25);
        connectingPoints[12] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.0, 0.75);
        connectingPoints[13] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 1.0, 0.75);
        connectingPoints[14] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.25, 1.0);
        connectingPoints[15] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.75, 1.0);
        
        addTGConnectingPointsComment();
	}
    
	public TGComponentAndroid isOnMe(int x1, int y1){
		if ((x1 >= x) && ((x + width) >= x1) && (y1 >= y) && ((y + height) >= y1)) {
            return this;
       }
        return null;
	}

	
	
	
	public void internalDrawing(Canvas canvas) {
		if(selected){
			ePaint.setColor(Color.RED);
			mTextPaint.setColor(Color.RED);
		}else{
			ePaint.setColor(Color.BLACK);
			mTextPaint.setColor(Color.BLACK);
		}
		Log.i("BDblock", "internal drawing!");
		int lp=getX();
		int tp=getY();
		int rp=getX()+getWidth();
		int bp=getY()+getHeight();
        
        mPaint.setColor(Color.rgb(193, 218, 241));
		canvas.drawRect(lp+3, tp+3, rp-3, bp-3, mPaint);
		
		canvas.drawLine(lp, tp, rp, tp, ePaint);
		canvas.drawLine(lp, tp, lp, bp, ePaint);
		canvas.drawLine(rp, tp, rp,bp, ePaint);
		canvas.drawLine(lp, bp, rp, bp, ePaint);
		
		String ster = "<<"+stereotype+">>";
		
		mTextPaint.setFakeBoldText(true);
		canvas.drawText(ster, lp+(getWidth()-ster.length())/2, tp+25, mTextPaint);
		
		mTextPaint.setFakeBoldText(false);
		if (name != null && name.length() > 0) {
            canvas.drawText(name,lp+(getWidth()-name.length())/2, tp+38,mTextPaint);
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
		
		canvas.drawText("attribute 1", x+50+7, y+60, mTextPaint);
		
		canvas.drawLine(x, y+70, rp, y+70, ePaint);
		
		//draw methods
		
		//draw signals
	
//		if(showConnectingPoints){
//			Log.i("block", ""+showConnectingPoints);
//			//canvas.drawRect(0, 0, 8, 8, cpPaint);
//			for(int i=0; i<nbConnectingPoints ; i++){
//			//	Log.i("block", "drawing points");
//				connectingPoints[i].internalDrawing(canvas);
//			}
//		}
		
		drawTGConnectingPoint(canvas, cptype);
		
	}
	
	public String getStereotype() {
		return stereotype;
	}

	public void setStereotype(String stereotype) {
		this.stereotype = stereotype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public View getPanel() {
		return panel;
	}

	public void setPanel(View panel) {
		this.panel = panel;
	}



	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}




//	public boolean isShowConnectingPoints() {
//		return showConnectingPoints;
//	}
//
//	public void setShowConnectingPoints(boolean showConnectingPoints) {
//		this.showConnectingPoints = showConnectingPoints;
//	}
    
	public int getNbConnectingPoints(){
		return nbConnectingPoints;
	}

	
	
}
