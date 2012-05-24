package project.alwaystry;

import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

public class Blockrectangle extends View implements OnLongClickListener,OnTouchListener{
	static final String Tag = "blockrectangle";
	private String stereotype = "block";
	private Paint mPaint;
	private Paint cpPaint;
	private Paint ePaint;
	
	private TextPaint mTextPaint;
	private Point currentClickedPoint= null;
	//String mText;
	
	private boolean dragActive = false;
	private boolean showConnectingPoints = true;
	
	private int mx =0;
    private int my =0;
    private long click1time = 0;
	
    protected Vector myAttributes, myMethods, mySignals;
    
    String name;
    
    int nbConnectingPoint = 16;
    AvatarBDConnectingPointAndroid[] connectingpoints;
   
    
    
	public Blockrectangle(Context context, AttributeSet attrs) {
		super(context, attrs);
		initRectangle();
		// TODO Auto-generated constructor stub
		
	}
	
	public Blockrectangle(Context context) {
		super(context);
		initRectangle();
		// TODO Auto-generated constructor stub
		
	}
	
	public void initRectangle(){
		setFocusable(true);
        setClickable(true);
        
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
        
        name = "Name";
        
        setOnLongClickListener(this);
        setOnTouchListener(this);
       
        connectingpoints = new AvatarBDConnectingPointAndroid[16];
        /*
        connectingpoints[0] = new AvatarBDConnectingPointAndroid(this,0,0,true,true,0,0);
        connectingpoints[1] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.5, 0.0);
        connectingpoints[2] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 1.0, 0.0);
        connectingpoints[3] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.0, 0.5);
        connectingpoints[4] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 1.0, 0.5);
        connectingpoints[5] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.0, 1.0);
        connectingpoints[6] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.5, 1.0);
        connectingpoints[7] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 1.0, 1.0);
        
        connectingpoints[8] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.25, 0.0);
        connectingpoints[9] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.75, 0.0);
        connectingpoints[10] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.0, 0.25);
        connectingpoints[11] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 1.0, 0.25);
        connectingpoints[12] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.0, 0.75);
        connectingpoints[13] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 1.0, 0.75);
        connectingpoints[14] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.25, 1.0);
        connectingpoints[15] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.75, 1.0);*/
	}
	
	String getName(){
		return name;
	}
	
	void setName(String text){
		name = text;
	}
	
	
	protected void onDraw(Canvas canvas) {
		float wf = getWidth();
        float hf = getHeight();
        
        mPaint.setColor(Color.rgb(193, 218, 241));
		canvas.drawRect(10, 10, wf-10, hf-10, mPaint);
		
		canvas.drawLine(7, 7, wf-7, 7, ePaint);
		canvas.drawLine(7, 7, 7, hf-7, ePaint);
		canvas.drawLine(wf-7, 7, wf-7,hf-7, ePaint);
		canvas.drawLine(7, hf-7, wf-7, hf-7, ePaint);
		
		String ster = "<<"+stereotype+">>";
		mTextPaint.setFakeBoldText(true);
		canvas.drawText(ster, (wf-14-ster.length())/2, 25, mTextPaint);
		
		mTextPaint.setFakeBoldText(false);
		if (name != null && name.length() > 0) {
            canvas.drawText(name,(wf-14-name.length())/2, 38,mTextPaint);
        }
		
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(2);
		canvas.drawLine(7, 42, wf-7, 42, mPaint);
		
		int h=0;
		int w;
		
		h = 3;
		
		//Icon
		int cpt = h;
		int index = 0;
		String attr;
		//draw Attributes
		
		canvas.drawText("attribute 1", 50+7, 60, mTextPaint);
		
		
		canvas.drawLine(7, 70, wf-7, 70, mPaint);
		
		//draw methods
		
		//draw signals
	
		if(showConnectingPoints){
			Log.i("block", ""+showConnectingPoints);
			//canvas.drawRect(0, 0, 8, 8, cpPaint);
			for(int i=0; i<nbConnectingPoint ; i++){
				Log.i("block", "x: "+connectingpoints[i].getX()+"y: "+connectingpoints[i].getY());
				int tx = 7+connectingpoints[i].getX()-connectingpoints[i].getWidth()/2;
				int ty = 7+connectingpoints[i].getY()-connectingpoints[i].getHeight()/2;
				int bx = 7+connectingpoints[i].getX()+connectingpoints[i].getWidth()/2;
				int by = 7+connectingpoints[i].getY()+connectingpoints[i].getHeight()/2;
				canvas.drawRect(tx, ty, bx, by, cpPaint);
			}
		}
		
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(250,200);
    }


	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		Log.i("Alwaystry","long click!!!");
		dragActive = true;
		return true;
	}

	
	public boolean clickOnAConnectingPoint(float x, float y){
		boolean clicked = false;
		for(int i = 0; i<nbConnectingPoint;i++){
			int tx = 7+connectingpoints[i].getX()-connectingpoints[i].getWidth()/2;
			int ty = 7+connectingpoints[i].getY()-connectingpoints[i].getHeight()/2;
			int bx = 7+connectingpoints[i].getX()+connectingpoints[i].getWidth()/2;
			int by = 7+connectingpoints[i].getY()+connectingpoints[i].getHeight()/2;
			
			if(x>=tx && x<=bx && y>=ty && y<=by){
				clicked = true;
				return clicked;
			}
		}
		return clicked;
	}
	
	public Point getCurrentClickedPoint(){
		return currentClickedPoint;
	}
	
	public void resetCurrentClickedPoint(){
		currentClickedPoint = null;
	}
	public boolean onTouch(View v, MotionEvent event) {
		
		// TODO Auto-generated method stub
		final int X = (int)event.getRawX();
		final int Y = (int)event.getRawY();
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			float cx = event.getX();
			float cy = event.getY();
			
			if(((AlwaystryActivity)(this.getContext())).getclickaction() == 8){
				((AlwaystryActivity)(this.getContext())).clickaction =9;
				int x = (int)event.getX();
				int y = (int)event.getY();
				Log.i("blockrectangle!!!", "x: "+x+"y: "+y);
				if(clickOnAConnectingPoint(cx,cy)){
					
					Log.i("block", "clickonAconnectingPoint");
					int px = x;
					int py = (int)this.getTop()+y;
							
					currentClickedPoint = new Point(px,py);
					/*
					AvatarBDPortConnectorAndroid avatarpconnector;
					RelativeLayout layout = (RelativeLayout)findViewById(R.id.relativeLayout1);
					if(layout.getChildAt(0) instanceof AvatarBDPortConnectorAndroid){
						avatarpconnector = (AvatarBDPortConnectorAndroid)layout.getChildAt(0);
					}else{
						avatarpconnector = new AvatarBDPortConnectorAndroid(this.getContext());
						layout.addView(avatarpconnector, 0);
					}
					
					if(avatarpconnector.getStart()== null){
						avatarpconnector.setStart(currentClickedPoint);
					}else if(avatarpconnector.getEnd() == null){
						avatarpconnector.setEnd(currentClickedPoint);
						
					}*/
					
					//((AlwaystryActivity)(this.getContext())).addAPortConnector(px, py, -1, -1);
					Log.i(name, "first: "+px+" y: "+py);
			}
			
			}else if(((AlwaystryActivity)(this.getContext())).getclickaction() == 9){
				int x = (int)event.getX();
				int y = (int)event.getY();
				Log.i("blockrectangle!!!", "x: "+x+"y: "+y);
				if(clickOnAConnectingPoint(cx,cy)){
					
					Log.i("block", "clickonAconnectingPoint");
					int px = (int)x;
					int py = (int)this.getTop()+y;
							
					currentClickedPoint = new Point(px,py);
				//	((AlwaystryActivity)(this.getContext())).addAPortConnector(-1, -1, px, py);
					Log.i(name, "second: "+px+" y: "+py);
				}
			}else{
			
			RelativeLayout.LayoutParams RlParams = (RelativeLayout.LayoutParams)(v.getLayoutParams());
			Log.i("Alwaystry", "lparams : topmagin, leftmargin " +RlParams.topMargin+"  "+RlParams.leftMargin );
			mx = X - RlParams.leftMargin;
			my = Y - RlParams.topMargin;
			if(click1time == 0){
				click1time = SystemClock.uptimeMillis();
				Log.i("Alwaystry","time is "+click1time);
			}else if(SystemClock.uptimeMillis()-click1time < 1000){
				Log.i("Alwaystry","double click!!");
				click1time = 0;
				
				boolean changename = true;
				
				if(event.getY() < 35)
					changename = true;
				else
					changename = false;
				
				if(changename){
					AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
					AlertDialog alertDialog;
					
					this.getContext();
					LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.blocknamealert,
					                               (ViewGroup) findViewById(R.id.relativeLayout1));
	
					alert.setTitle("setting value");
					
					TextView text = (TextView)layout.findViewById(R.id.textView1);
					text.setText("Block name");
					final EditText input = (EditText)layout.findViewById(R.id.editText1);
					input.setText(getName());
					alert.setView(layout);
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							setName(input.getText().toString());
						    invalidate();
						  }
						});
	
						alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						  public void onClick(DialogInterface dialog, int whichButton) {
						    // Canceled.
						  }
						});
	
						alert.show();
				}else{
					EditAttributesDialog editAttributesDialog = new EditAttributesDialog(this.getContext());
					editAttributesDialog.setName(getName());
					editAttributesDialog.organizeContent();
					editAttributesDialog.show();
				}
			}else{
				click1time = SystemClock.uptimeMillis();
				Log.i("Alwaystry","just click!!");
				
			}
		}
		}
		
		if(!dragActive){
			return false;
		}else{
			switch(event.getAction()){
			case MotionEvent.ACTION_UP:
				Log.i("Alwaystry","stop dragging");
				dragActive = false;
				break;
			case MotionEvent.ACTION_MOVE:
				Log.i("Alwaystry","it is dragging");
			
				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)(v.getLayoutParams());
				lParams.leftMargin = X - mx;
				lParams.topMargin = Y - my;
				Log.i("Alwaystry","leftMargin:  "+lParams.leftMargin);
				v.setLayoutParams(lParams);
				
				break;
				
			}
			return true;
		}
		
	}

}
