package project.alwaystry;

import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class TDiagramTouchManagerAndroid implements OnTouchListener, OnClickListener{

	AvatarBDPanelAndroid panel;
	int clickedX,clickedY;
	float dist;
	TGComponentAndroid rescaleComponent;
	
	public TDiagramTouchManagerAndroid(AvatarBDPanelAndroid panel){
		this.panel = panel;
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		final int X = (int)event.getX();
		final int Y = (int)event.getY();
		dumpEvent(event);
		switch(event.getAction()& MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			
			clickedX = X;
			clickedY = Y;
			Log.i("touchmanager touch", "clickedX:"+clickedX+ "clickedY: "+clickedY);
			
			if(panel.getMode() == AvatarBDPanelAndroid.SELECTED_COMPONENTS && panel.isInSelectedRectangle(X, Y)){
				panel.setMode(AvatarBDPanelAndroid.MOVING_SELECTED_COMPONENTS);
			} else if(panel.getMode() == AvatarBDPanelAndroid.SELECTED_COMPONENTS && !panel.isInSelectedRectangle(X, Y)){
				panel.setMode(AvatarBDPanelAndroid.NORMAL);
				panel.setXsel(-1);
				panel.setYsel(-1);
				panel.setXendsel(-1);
				panel.setYendsel(-1);
				panel.invalidate();
			}
			
			if(panel.getCreatedtype() != TGComponentAndroid.NOCOMPONENT){
//				panel.setMode(AvatarBDPanelAndroid.NORMAL);
//			}else{
				panel.createComponent(X,Y,panel.getCreatedtype());
				panel.invalidate();
			}
			
			if(panel.getMode() == AvatarBDPanelAndroid.NORMAL){
				panel.setComponentSelected(panel.getSelectedComponent(X, Y));
				if(panel.getComponentSelected() != null){
					panel.setMode(AvatarBDPanelAndroid.MOVING_COMPONENT);
				}else{
					panel.setMode(AvatarBDPanelAndroid.SELECTING_COMPONENTS);
					panel.setXsel(X);
					panel.setYsel(Y);
				}
			}	
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			float dx = event.getX(0) - event.getX(1);
			float dy = event.getY(0) - event.getY(1);
			dist = FloatMath.sqrt(dx*dx+dy*dy);
			Log.i("touch manager", "Dist = "+ dist);
			if(dist >10f){
				TGComponentAndroid s1 = panel.getSelectedComponent((int)event.getX(0),(int)event.getY(0));
				TGComponentAndroid s2 = panel.getSelectedComponent((int)event.getX(1), (int)event.getY(1));
				if(s1 != null && s2 != null && s1.equals(s2)){
					rescaleComponent = s1;
					panel.setMode(AvatarBDPanelAndroid.RESIZING_COMPONENT);
				}
				System.out.println("current time :" + System.currentTimeMillis());
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.i("touchmanager", "turn to normal");
			if(panel.getMode() == AvatarBDPanelAndroid.SELECTING_COMPONENTS){
				panel.endSelectComponents();
				panel.invalidate();
			}
			else
				panel.setMode(AvatarBDPanelAndroid.NORMAL);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			panel.setMode(AvatarBDPanelAndroid.NORMAL);
			break;
		case MotionEvent.ACTION_MOVE:
			if(panel.getMode() == AvatarBDPanelAndroid.MOVING_COMPONENT){
				if(panel.getComponentSelected() instanceof TGConnectorAndroid){
				
				}else{
					int dcx = clickedX - X;
					int dcy = clickedY - Y;
					int ox = panel.getComponentSelected().getX();
					int oy = panel.getComponentSelected().getY();
					panel.getComponentSelected().setCd(ox-dcx, oy-dcy);
					clickedX = X;
					clickedY = Y;
					Log.i("block location", "X: "+X+" Y: "+X);
					panel.invalidate();
				}
				
			}
			
			if(panel.getMode() == AvatarBDPanelAndroid.MOVING_SELECTED_COMPONENTS){
				panel.moveSelected(X, Y);
				panel.invalidate();
			}
			
			if(panel.getMode() == AvatarBDPanelAndroid.SELECTING_COMPONENTS){
				panel.setXendsel(X);
				panel.setYendsel(Y);
				panel.invalidate();
			}
			
			if(panel.getMode() == AvatarBDPanelAndroid.RESIZING_COMPONENT){
				float x1 = event.getX(0)-event.getX(1);
				float y1 = event.getY(0)-event.getY(1);
				float newdist = FloatMath.sqrt(x1*x1+y1*y1);
				
				rescaleComponent.setRescale(newdist/dist);
//				int rcx = rescaleComponent.getX();
//				int rcy = rescaleComponent.getY();
//				int rcw = rescaleComponent.getWidth();
//				int rch = rescaleComponent.getHeight();
//				rescaleComponent.setCd(rcx+, _y)
				Log.i("touch manager", "rescale: "+rescaleComponent.getRescale());
				panel.invalidate();
			}
			
			break;
		}
		
		return true;
	}

	private void dumpEvent(MotionEvent event) {
		   String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
		      "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
		   StringBuilder sb = new StringBuilder();
		   int action = event.getAction();
		   int actionCode = action & MotionEvent.ACTION_MASK;
		   sb.append("event ACTION_" ).append(names[actionCode]);
		   if (actionCode == MotionEvent.ACTION_POINTER_DOWN
		         || actionCode == MotionEvent.ACTION_POINTER_UP) {
		      sb.append("(pid " ).append(
		      action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
		      sb.append(")" );
		   }
		   sb.append("[" );
		   for (int i = 0; i < event.getPointerCount(); i++) {
		      sb.append("#" ).append(i);
		      sb.append("(pid " ).append(event.getPointerId(i));
		      sb.append(")=" ).append((int) event.getX(i));
		      sb.append("," ).append((int) event.getY(i));
		      if (i + 1 < event.getPointerCount())
		         sb.append(";" );
		   }
		   sb.append("]" );
		   Log.d("Touch Manager", sb.toString());
		}

	
	public void onClick(View v) {
		Log.i("touchmanager", "clickedX:"+clickedX+ "clickedY: "+clickedY);
		if(panel.getCreatedtype() == TGComponentAndroid.NOCOMPONENT){
			panel.setMode(AvatarBDPanelAndroid.NORMAL);
		}else{
			panel.createComponent(clickedX,clickedY,panel.getCreatedtype());
			panel.invalidate();
		}
		
		
	}

	
}
