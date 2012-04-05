package project.alwaystry;

import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
	
	private TextPaint mTextPaint;
	//String mText;
	
	private boolean dragActive = false;
	
	private int mx =0;
    private int my =0;
    private long click1time = 0;
	
    protected Vector myAttributes, myMethods, mySignals;
    
    String name;
	
	public Blockrectangle(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// TODO Auto-generated constructor stub
		setFocusable(true);
        setClickable(true);
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(6);
        //mPaint.setColor(Color.LTGRAY);
        
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
        
        name = "Name";
        
        setOnLongClickListener(this);
        setOnTouchListener(this);
        

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
        
        mPaint.setColor(Color.BLACK);
		canvas.drawRect(0, 0, wf, hf, mPaint);
		mPaint.setColor(Color.rgb(193, 218, 241));
		canvas.drawRect(3, 3, wf-3, hf-3, mPaint);
		
		String ster = "<<"+stereotype+">>";
		mTextPaint.setFakeBoldText(true);
		canvas.drawText(ster, (wf-ster.length())/2, 15, mTextPaint);
		
		mTextPaint.setFakeBoldText(false);
		if (name != null && name.length() > 0) {
            canvas.drawText(name,(wf-name.length())/2, 28,mTextPaint);
        }
		
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(3);
		canvas.drawLine(0, 35, wf, 35, mPaint);
		
		int h=0;
		int w;
		
		h = 3;
		
		//Icon
		int cpt = h;
		int index = 0;
		String attr;
		//draw Attributes
		
		canvas.drawText("attribute 1", 50, 50, mTextPaint);
		
		
		canvas.drawLine(0, 70, wf, 70, mPaint);
		
		//draw methods
		
		//draw signals
	
		
		
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

	
	public boolean onTouch(View v, MotionEvent event) {
		
		// TODO Auto-generated method stub
		final int X = (int)event.getRawX();
		final int Y = (int)event.getRawY();
		if(event.getAction() == MotionEvent.ACTION_DOWN){
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
				v.setLayoutParams(lParams);
				break;
				
			}
			return true;
		}
		
	}

}
