package project.alwaystry;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class TDiagramTouchManagerAndroid implements OnTouchListener, OnClickListener{

	AvatarBDPanelAndroid panel;
	int clickedX,clickedY;
	
	public TDiagramTouchManagerAndroid(AvatarBDPanelAndroid panel){
		this.panel = panel;
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		final int X = (int)event.getX();
		final int Y = (int)event.getY();
		
		switch(event.getAction()){
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
		case MotionEvent.ACTION_UP:
			Log.i("touchmanager", "turn to normal");
			if(panel.getMode() == AvatarBDPanelAndroid.SELECTING_COMPONENTS){
				panel.endSelectComponents();
				panel.invalidate();
			}
			else
				panel.setMode(AvatarBDPanelAndroid.NORMAL);
			break;
		case MotionEvent.ACTION_MOVE:
			if(panel.getMode() == AvatarBDPanelAndroid.MOVING_COMPONENT){
				if(panel.getComponentSelected() instanceof TGConnectorAndroid){
				
				}else{
					panel.getComponentSelected().setCd(X, Y);
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
			
			break;
		}
		
		return true;
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
