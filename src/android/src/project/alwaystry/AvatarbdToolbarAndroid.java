package project.alwaystry;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class AvatarbdToolbarAndroid extends LinearLayout {
	
	public AvatarbdToolbarAndroid(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	public AvatarbdToolbarAndroid(Context context, AttributeSet attrs){
		super(context, attrs);
		initView(context);
	}
	
	public AvatarbdToolbarAndroid(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		initView(context);
	}
	
	private void initView(Context context){
		View.inflate(context, R.layout.avatarbdtoolbar, this);
		Log.v("AvatarbdToolbar", "Number of Child: " + this.getChildCount());
	}
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);

	}
	

}
