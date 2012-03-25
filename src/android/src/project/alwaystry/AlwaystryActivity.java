package project.alwaystry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class AlwaystryActivity extends Activity implements OnLongClickListener,OnTouchListener{
    /** Called when the activity is first created. */
    private boolean dragActive = false;
    private int mx =0;
    private int my =0;
    private long click1time = 0;
    private Blockrectangle br;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block);
        br = (Blockrectangle)findViewById(R.id.blockrectangle1);
        br.setOnLongClickListener(this);
        br.setOnTouchListener(this);
        
    }

	
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		dragActive = true;
		/*
		RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)(v.getLayoutParams());
		Log.i("Alwaystry", "lparams : topmagin, leftmargin " +lParams.topMargin+"  "+lParams.leftMargin );
		mx = lParams.leftMargin;
		my = lParams.topMargin;*/
		return true;
	}

	@Override
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
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Modifying");
				final EditText input = new EditText(this);
				input.setText(br.getmText());
				alert.setView(input);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						br.setmText(input.getText().toString());
					    br.invalidate();
					  }
					});

					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int whichButton) {
					    // Canceled.
					  }
					});

					alert.show();

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