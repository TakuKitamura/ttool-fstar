package project.alwaystry;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class AvatarBDPanelAndroid extends View implements OnTouchListener{
	
	private Paint panelBGPaint; 
	//private TextPaint mTextPaint;
	//private Paint borderPaint;
	//private Paint blockBGPaint;
	private LinkedList blocklist;
	private LinkedList connectorlist;
	private LinkedList compoconnectorlist;
	private int mx, my;
	

	public AvatarBDPanelAndroid(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initPanel();
	}
	
	public AvatarBDPanelAndroid(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPanel();
	}
	
	public AvatarBDPanelAndroid(Context context) {
		super(context);
		initPanel();
		// TODO Auto-generated constructor stub
	}
	
	private void initPanel(){
		blocklist = new LinkedList();
		connectorlist = new LinkedList();
		compoconnectorlist = new LinkedList();
		panelBGPaint = new Paint();
		panelBGPaint.setColor(Color.WHITE);
		/*
		blockBGPaint = new Paint();
		blockBGPaint.setColor(Color.rgb(193, 218, 241));
		
		mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
        
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(2);
        borderPaint.setColor(Color.BLACK);
        */
		AvatarBDBlockAndroid block1 = new AvatarBDBlockAndroid(100,100,this);
		blocklist.add(block1);
		setClickable(true);
		
		setOnTouchListener(this);
	}
	
	protected void onDraw(Canvas canvas) {
		Log.i("panel", "drawing");
		canvas.drawRect(0, 0, getWidth(), getHeight(), panelBGPaint);
		for(int i=0; i<blocklist.size();i++){
			((AvatarBDBlockAndroid)blocklist.get(i)).internalDrawing(canvas);
		}
		
		for(int j=0; j<connectorlist.size();j++){
			((AvatarBDPortConnectorAndroid)connectorlist.get(j)).internalDrawing(canvas);
		}
		for(int j=0; j<compoconnectorlist.size();j++){
			((AvatarBDCompositionConnectorAndroid)compoconnectorlist.get(j)).internalDrawing(canvas);
		}
		
	}
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //setMeasuredDimension(800,600);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
	
	public void showAllConnectingPoints(){
		for(int i=0; i< blocklist.size();i++){
			((AvatarBDBlockAndroid)blocklist.get(i)).setShowConnectingPoints(true);
		}
		invalidate();
	}
	
	public void hideAllConnectingPoints(){
		for(int i=0; i< blocklist.size();i++){
			((AvatarBDBlockAndroid)blocklist.get(i)).setShowConnectingPoints(false);
		}
		invalidate();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		final int X = (int)event.getX();
		final int Y = (int)event.getY();
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			boolean blockClicked = false;
			Log.i("Panel", "ACTION DOWN!");
			if(((AlwaystryActivity)this.getContext()).getclickaction() == 8){
				for(int i=0; i<blocklist.size();i++){
					for(int j=0; j<((AvatarBDBlockAndroid)blocklist.get(i)).getNbConnectingPoints();j++){
						if(((AvatarBDBlockAndroid)blocklist.get(i)).connectingPoints[j].isOnMe(X, Y)){
							for(int n=0; n<connectorlist.size();n++){
								if(!((AvatarBDPortConnectorAndroid)connectorlist.get(n)).hasStart()){
									((AvatarBDPortConnectorAndroid)connectorlist.get(n)).setOutPoint(((AvatarBDBlockAndroid)blocklist.get(i)).connectingPoints[j]);
								}else if(!((AvatarBDPortConnectorAndroid)connectorlist.get(n)).hasEnd()){
									((AvatarBDPortConnectorAndroid)connectorlist.get(n)).setInPoint(((AvatarBDBlockAndroid)blocklist.get(i)).connectingPoints[j]);
									hideAllConnectingPoints();
								}
									
							}
						}
					}
				}
				invalidate();
			}
			
			if(((AlwaystryActivity)this.getContext()).getclickaction() == 7){
				for(int i=0; i<blocklist.size();i++){
					for(int j=0; j<((AvatarBDBlockAndroid)blocklist.get(i)).getNbConnectingPoints();j++){
						if(((AvatarBDBlockAndroid)blocklist.get(i)).connectingPoints[j].isOnMe(X, Y)){
							for(int n=0; n<compoconnectorlist.size();n++){
								if(!((AvatarBDCompositionConnectorAndroid)compoconnectorlist.get(n)).hasStart()){
									((AvatarBDCompositionConnectorAndroid)compoconnectorlist.get(n)).setOutPoint(((AvatarBDBlockAndroid)blocklist.get(i)).connectingPoints[j]);
								}else if(!((AvatarBDCompositionConnectorAndroid)compoconnectorlist.get(n)).hasEnd()){
									((AvatarBDCompositionConnectorAndroid)compoconnectorlist.get(n)).setInPoint(((AvatarBDBlockAndroid)blocklist.get(i)).connectingPoints[j]);
									hideAllConnectingPoints();
								}
									
							}
						}
					}
				}
				invalidate();
			}
			for(int i=0;i<blocklist.size();i++){
				if(((AvatarBDBlockAndroid)blocklist.get(i)).isOnOnlyMe(X, Y)){
					Log.i("Panel", "one block is clicked!");
					((AvatarBDBlockAndroid)blocklist.get(i)).setClicked(true);
					blockClicked = true;
				}
			}
			
			if(((AlwaystryActivity)this.getContext()).getclickaction() == 4){
				AvatarBDBlockAndroid block2 = new AvatarBDBlockAndroid(X,Y,this);
				blocklist.add(block2);
				invalidate();
			}
			
			
			
			break;
		case MotionEvent.ACTION_UP:
			for(int i=0;i<blocklist.size();i++){
				((AvatarBDBlockAndroid)blocklist.get(i)).setClicked(false);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			for(int i=0;i<blocklist.size();i++){
				if(((AvatarBDBlockAndroid)blocklist.get(i)).isClicked()){
					Log.i("Panel", "one block is moving!");
					int nx = ((AvatarBDBlockAndroid)blocklist.get(i)).getX()+(X-(int)event.getX());
					int ny = ((AvatarBDBlockAndroid)blocklist.get(i)).getY()+(Y-(int)event.getY());
					((AvatarBDBlockAndroid)blocklist.get(i)).setX(X);
					((AvatarBDBlockAndroid)blocklist.get(i)).setY(Y);
					Log.i("block location", "X: "+X+" Y: "+X);
					invalidate();
					break;
				}
			}
			
			break;
		}
		
		return true;
	}

	public LinkedList getConnectorlist() {
		return connectorlist;
	}

	public void setConnectorlist(LinkedList connectorlist) {
		this.connectorlist = connectorlist;
	}

	public LinkedList getCompoconnectorlist(){
		return compoconnectorlist;
	}
}
