package project.alwaystry;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class AlwaystryActivity extends Activity implements OnGesturePerformedListener{//implements OnTouchListener{//implements OnLongClickListener,OnTouchListener{
	
	int clickaction = 0;
	int tx = -1,ty=-1,bx=-1,by=-1;
	private GestureLibrary gestureLib;
	AvatarBDPanelAndroid panel;
	GestureOverlayView gestures;
	
	public static final int EDIT_ATTRIBUTES = 1;
	
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block);   
        panel = (AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1);
		
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.actions);
        if(!gestureLib.load()){
        	finish();
        }
        gestures = (GestureOverlayView)findViewById(R.id.gestureOverlayView1);
		gestures.addOnGesturePerformedListener(this);
		
		
       
    }
    
    public int getclickaction(){
    	return clickaction;
    }
    
    public void resetClickaction(){
    	clickaction = 0;
    }
    public void clickbutton(View v){
    	Log.i("alwaystry", "buttonclicked");
    	switch(v.getId()){
    	case R.id.iod_edit:
    		panel.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
    		panel.setMode(AvatarBDPanelAndroid.NORMAL);
    	    panel.cleanSelection();
    		clickaction =1;
    		panel.setComponentSelected(null);
    		break;
    	case R.id.uml_note:
    		panel.setCreatedtype(TGComponentAndroid.UML_NOTE);
    		clickaction =2;
    		break;
    	case R.id.concomment:
    		((AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1)).showAllConnectingPoints(TGComponentAndroid.CONNECTOR_COMMENT);
        	///	((AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1)).cleanConnectorlist();
        		panel.setCreatedtype(TGComponentAndroid.CONNECTOR_COMMENT);
        		panel.setMode(AvatarBDPanelAndroid.ADDING_CONNECTOR);
    		clickaction =3;
    		break;
    	case R.id.block:
    		panel.setCreatedtype(TGComponentAndroid.AVATARBD_BLOCK);
    		clickaction =4;
    		break;
    	case R.id.cryptoblock:
    		panel.setCreatedtype(TGComponentAndroid.AVATARBD_CRYPTOBLOCK);
    		clickaction =5;
    		break;
    	case R.id.datatype:
    		panel.setCreatedtype(TGComponentAndroid.AVATARBD_DATATYPE);
    		clickaction =6;
    		break;
    	case R.id.comp:
    		((AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1)).showAllConnectingPoints(TGComponentAndroid.AVATARBD_COMPOSITION_CONNECTOR);
    	//	((AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1)).cleanCompoconnectorlist();
    		panel.setCreatedtype(TGComponentAndroid.AVATARBD_COMPOSITION_CONNECTOR);
    		panel.setMode(AvatarBDPanelAndroid.ADDING_CONNECTOR);
    		clickaction =7;
    		break;
    	case R.id.link:
    		((AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1)).showAllConnectingPoints(TGComponentAndroid.AVATARBD_PORT_CONNECTOR);
    	///	((AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1)).cleanConnectorlist();
    		panel.setCreatedtype(TGComponentAndroid.AVATARBD_PORT_CONNECTOR);
    		panel.setMode(AvatarBDPanelAndroid.ADDING_CONNECTOR);
    		clickaction =8;
    		break;
    	}
    		
    }

	
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		if(predictions.size()>0){
			Prediction prediction = predictions.get(0);
			if(prediction.score>1.0){
				panel.deleteComponent();
				Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
				
			}
		}
	}
 
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle objectBundle = intent.getExtras();
		switch(requestCode) {
		case EDIT_ATTRIBUTES:
			if(resultCode == Activity.RESULT_OK){
				if(objectBundle != null && objectBundle.containsKey("attributes") ){
					String[] attributes =objectBundle.getStringArray("attributes");
					
					if(objectBundle.containsKey("methods")&& objectBundle.containsKey("signals")){
						((AvatarBDBlockAndroid)panel.getComponentSelected()).setAttributes(attributes);
						//((AvatarBDBlockAndroid)panel.getComponentSelected()).setAttributes(attributes);
						String[] methods =objectBundle.getStringArray("methods");
						((AvatarBDBlockAndroid)panel.getComponentSelected()).setMethods(methods);
						String[] signals =objectBundle.getStringArray("signals");
						((AvatarBDBlockAndroid)panel.getComponentSelected()).setSignals(signals);
						panel.invalidate();
					}else{
						((AvatarBDDataTypeAndroid)panel.getComponentSelected()).setAttributes(attributes);
					}
					
					panel.invalidate();
				}
				
			}
			break;
		
		}		
	}

}
