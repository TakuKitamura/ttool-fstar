package project.alwaystry;

import myutilandroid.GraphicLibAndroid;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class TDiagramTouchManagerAndroid implements OnTouchListener{

	AvatarBDPanelAndroid panel;
	int clickedX,clickedY;
	float dist;
	private TGComponentAndroid tgComponent;
	private TGConnectorAndroid tgConnector;
	private boolean isOut;
	private CDElementAndroid [] cde;
	private long click1time = 0;
	private TGComponentAndroid tgComponent1clicked;
	
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
				tgComponent = panel.getSelectedComponent(X, Y);
				panel.setComponentSelected(tgComponent);
				if(panel.getComponentSelected() != null){
					if(tgComponent instanceof TGConnectorAndroid){
						tgConnector = (TGConnectorAndroid)tgComponent;
						cde = tgConnector.closerPToClickFirst(X, Y);
						panel.setMovingHead(X, Y, cde[1].getX(), cde[1].getY());
						panel.showAllConnectingPoints(tgConnector.getType());
						if (cde[0] == tgConnector.getTGConnectingPointP2()) {
							isOut = false;
						} else {
							isOut = true;
						}
						panel.setMode(AvatarBDPanelAndroid.MOVE_CONNECTOR_HEAD);
					}else{
						panel.setMode(AvatarBDPanelAndroid.MOVING_COMPONENT);
						
					}
				}else{
					panel.setMode(AvatarBDPanelAndroid.SELECTING_COMPONENTS);
					panel.setXsel(X);
					panel.setYsel(Y);
				}
			}	
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
//			float dx = event.getX(0) - event.getX(1);
//			float dy = event.getY(0) - event.getY(1);
//			dist = FloatMath.sqrt(dx*dx+dy*dy);
			dist = GraphicLibAndroid.distanceBetweenTwoP(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
			Log.i("touch manager", "Dist = "+ dist);
			if(dist >10f){
				TGComponentAndroid s1 = panel.getSelectedComponent((int)event.getX(0),(int)event.getY(0));
				TGComponentAndroid s2 = panel.getSelectedComponent((int)event.getX(1), (int)event.getY(1));
				if(s1 != null && s2 != null && s1.equals(s2)){
					tgComponent = s1;
					panel.setMode(AvatarBDPanelAndroid.RESIZING_COMPONENT);
				}
				System.out.println("current time :" + System.currentTimeMillis());
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.i("touchmanager", "turn to normal");
			if(click1time ==0){
				click1time = SystemClock.uptimeMillis();
				Log.i("Alwaystry","time is "+click1time);
				if(tgComponent !=null){
					tgComponent1clicked = tgComponent;
				}
			}else if(SystemClock.uptimeMillis()-click1time < 1000){
				if(tgComponent1clicked != null &&tgComponent1clicked == tgComponent){
					Log.i("Alwaystry","double click!!");
					tgComponent.editOndoubleClick(X, Y);
				}
                click1time = 0;    
			}else{
				click1time = SystemClock.uptimeMillis();
			}
			
			if(panel.getMode() == AvatarBDPanelAndroid.SELECTING_COMPONENTS){
				panel.endSelectComponents();
				panel.invalidate();
			}else if(panel.getMode() == AvatarBDPanelAndroid.MOVE_CONNECTOR_HEAD){
				panel.setMode(AvatarBDPanelAndroid.NORMAL);
				tgConnector.setMovingHead(false);
				panel.hideAllConnectingPoints();
				TGConnectingPointAndroid p = panel.getPointSelected(X, Y, tgConnector.getType());
				if(p != null){
					((TGConnectingPointAndroid)cde[0]).setFree(true);
					((TGConnectingPointAndroid)cde[0]).setState(TGConnectingPointAndroid.NORMAL);
					p.setFree(false);
					if (isOut) {
						tgConnector.setP1(p);
					} else {
						tgConnector.setP2(p);
					}
					
				}
				panel.invalidate();
			}
			else
				panel.setMode(AvatarBDPanelAndroid.NORMAL);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			panel.setMode(AvatarBDPanelAndroid.NORMAL);
			break;
		case MotionEvent.ACTION_MOVE:
			if(panel.getMode() == AvatarBDPanelAndroid.MOVE_CONNECTOR_HEAD){
				panel.setMovingHead(X, Y, cde[1].getX(), cde[1].getY());
				panel.invalidate();
			}
			
			if(panel.getMode() == AvatarBDPanelAndroid.MOVING_COMPONENT){
//				if(panel.getComponentSelected() instanceof TGConnectorAndroid){
//				
//				}else{
					int dcx = clickedX - X;
					int dcy = clickedY - Y;
					int ox = tgComponent.getX();
					int oy = tgComponent.getY();
					tgComponent.setCd(ox-dcx, oy-dcy);
					clickedX = X;
					clickedY = Y;
					Log.i("block location", "X: "+X+" Y: "+X);
					panel.invalidate();
//				}
				
			}
			
			if(panel.getMode() == AvatarBDPanelAndroid.MOVING_SELECTED_COMPONENTS){
				int dcx = clickedX - X;
				int dcy = clickedY - Y;
				int ox = panel.xSEL;
				int oy = panel.ySEL;
				panel.moveSelected(ox-dcx, oy-dcy);
				clickedX = X;
				clickedY = Y;
				
				panel.invalidate();
			}
			
			if(panel.getMode() == AvatarBDPanelAndroid.SELECTING_COMPONENTS){
				panel.setXendsel(X);
				panel.setYendsel(Y);
				panel.invalidate();
			}
			
			if(panel.getMode() == AvatarBDPanelAndroid.RESIZING_COMPONENT){
//				float x1 = event.getX(0)-event.getX(1);
//				float y1 = event.getY(0)-event.getY(1);
//				float newdist = FloatMath.sqrt(x1*x1+y1*y1);
				
				float newdist = GraphicLibAndroid.distanceBetweenTwoP(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
				
				tgComponent.setRescale(newdist/dist);
//				int rcx = rescaleComponent.getX();
//				int rcy = rescaleComponent.getY();
//				int rcw = rescaleComponent.getWidth();
//				int rch = rescaleComponent.getHeight();
//				rescaleComponent.setCd(rcx+, _y)
				Log.i("touch manager", "rescale: "+tgComponent.getRescale());
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

//	
//	public void onClick(View v) {
//		Log.i("touchmanager on click", "clickedX:"+clickedX+ "clickedY: "+clickedY);
//		
////		if(panel.getMode() == AvatarBDPanelAndroid.SELECTED_COMPONENTS){
////			panel.setMode(AvatarBDPanelAndroid.NORMAL);
////			panel.setXsel(-1);
////			panel.setYsel(-1);
////			panel.setXendsel(-1);
////			panel.setYendsel(-1);
////			panel.invalidate();
////		}
//		
//		if(panel.getCreatedtype() != TGComponentAndroid.NOCOMPONENT){
//			panel.createComponent(clickedX,clickedY,panel.getCreatedtype());
//			panel.invalidate();
//		}
//		
//		
//	}

//	
//	public boolean onLongClick(View v) {
//		// TODO Auto-generated method stub
//		if(panel.getComponentSelected() instanceof AvatarBDBlockAndroid){
//			final AvatarBDBlockAndroid block = (AvatarBDBlockAndroid)panel.getComponentSelected();
//			if(block.inEditNameArea(clickedX, clickedY)){
//				AlertDialog.Builder alert = new AlertDialog.Builder(panel.getContext());
//				AlertDialog alertDialog;				
//				panel.getContext();
//				LayoutInflater inflater = (LayoutInflater) panel.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				View layout = inflater.inflate(R.layout.blocknamealert,
//				                               (ViewGroup) panel.findViewById(R.id.linearLayout1));
//
//				alert.setTitle("setting value");
//				
//				TextView text = (TextView)layout.findViewById(R.id.textView1);
//				text.setText("Block name");
//				final EditText input = (EditText)layout.findViewById(R.id.editText1);
//				input.setText(block.getName());
//				alert.setView(layout);
//				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						block.setName(input.getText().toString());
//					    panel.invalidate();
//					  }
//					});
//
//					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//					  public void onClick(DialogInterface dialog, int whichButton) {
//					    // Canceled.
//					  }
//					});
//
//					alert.show();
//			}
//		}
//		return true;
//	}

	
	
}
