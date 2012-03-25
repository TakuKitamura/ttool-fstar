package project.alwaystry;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.ViewGroup;
import android.widget.Toast;

public class drageventblock extends View{
	static final String Tag = "blockrectangle";
	private Paint mPaint;

	public drageventblock(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// TODO Auto-generated constructor stub
		setFocusable(true);
        setClickable(true);
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(6);
        mPaint.setColor(Color.LTGRAY);
        
        setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("BlockRectangle", "BRectangle : " + v.toString());
                Log.i("Blockrectangle0",""+data);
                v.startDrag(data, new MyShadowBuilder(v),
                        (Object)v, 0);
                
                return true;
            }
        });
        
        setOnDragListener(new View.OnDragListener() {
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                View dragView = (View) event.getLocalState();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED: {
                    	Log.i("Blockrectangle1", "left : "+dragView.getLeft());
                        // Bring up a fourth draggable dot on the fly. Note that it
                        // is properly notified about the ongoing drag, and lights up
                        // to indicate that it can handle the current content.
                    	v.setVisibility(View.INVISIBLE);
                    } break;

                    case DragEvent.ACTION_DRAG_ENDED: {
                    	Log.i("Blockrectangle2", "left : "+event.toString());
                        // Hide the surprise again
                    	ViewGroup owner = (ViewGroup) dragView.getParent();
                        owner.removeView(dragView);
                        ClipData clipdata = event.getClipData();
                        Log.i("Blockrectangle3",""+clipdata);
                        //v.addView(dragView);
                        //v.setGravity(Gravity.CENTER);
                        //v.showAsLanded();

                        // Report the drop/no-drop result to the user
                        
                    } break;
                }
                return false;
            }
        });

	}
	
	class MyShadowBuilder extends DragShadowBuilder {
        public MyShadowBuilder(View view) {
            super(view);
           
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            super.onDrawShadow(canvas);
        }
    }
	
	protected void onDraw(Canvas canvas) {
		float wf = getWidth();
        float hf = getHeight();
        
		canvas.drawRect(0, 0, wf, hf, mPaint);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(100,100);
    }

	
	
	

}
