package project.alwaystry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class AlwaystryActivity extends Activity {//implements OnTouchListener{//implements OnLongClickListener,OnTouchListener{
	
	int clickaction = 0;
	int tx = -1,ty=-1,bx=-1,by=-1;
	
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block);   
        AvatarBDPanelAndroid panel = (AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1);
		//RelativeLayout layout = (RelativeLayout)findViewById(R.id.relativeLayout1);
		//layout.setOnTouchListener(this);
    }
    
    public int getclickaction(){
    	return clickaction;
    }
    
    public void clickbutton(View v){
    	Log.i("alwaystry", "buttonclicked");
    	switch(v.getId()){
    	case R.id.property:
    		clickaction =1;
    		break;
    	case R.id.edit:
    		clickaction =2;
    		break;
    	case R.id.concomment:
    		clickaction =3;
    		break;
    	case R.id.block:
    		clickaction =4;
    		break;
    	case R.id.cryptoblock:
    		clickaction =5;
    		break;
    	case R.id.datatype:
    		clickaction =6;
    		break;
    	case R.id.comp:
    		((AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1)).showAllConnectingPoints();
    		AvatarBDCompositionConnectorAndroid cconnector = new AvatarBDCompositionConnectorAndroid();
    		((AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1)).getCompoconnectorlist().add(cconnector);
    		clickaction =7;
    		break;
    	case R.id.link:
    		((AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1)).showAllConnectingPoints();
    		AvatarBDPortConnectorAndroid pconnector = new AvatarBDPortConnectorAndroid();
    		((AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1)).getConnectorlist().add(pconnector);
    		clickaction =8;
    		break;
    	}
    		
    }
/*
    public void addAPortConnector(int startx, int starty, int endx, int endy){
    	//int tx,ty,bx,by;
    	if(startx != -1){
    		tx = startx;
    	}
    	if(starty != -1){
    		ty = starty;
    	}
    	if(endx != -1){
    		bx = endx;
    	}
    	if(endy != -1){
    		by = endy;
    	}
    	Log.i("try", "tx:"+tx+"ty:"+ty+"bx:"+bx+"by:"+by);
    	if(tx != -1 && ty !=-1 && bx !=-1 && by!=-1){
    	AvatarBDPortConnectorAndroid portconnector = new AvatarBDPortConnectorAndroid(this);
    	portconnector.initPortConnector(tx, ty, bx, by);
    	RelativeLayout layout = (RelativeLayout)findViewById(R.id.relativeLayout1);
    	RelativeLayout.LayoutParams RlParams = new RelativeLayout.LayoutParams(
	            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	RlParams.leftMargin = tx;
    	RlParams.topMargin = ty;
    	layout.addView(portconnector, RlParams);
    	portconnector.invalidate();
    	Log.i("try", "add a port connector");
    	}
    }
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Log.i("alwaystry ontouch", "panel clicked");
		if(v.getId() == R.id.relativeLayout1){
			Log.i("alwaystry ontouch", "panel clicked!!!!!");
			final int X = (int)event.getX();
			final int Y = (int)event.getY();
			
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				switch(clickaction){
				case 1 :
					break;
				case 2 :
					break;
				case 3 :
					break;
				case 4 :
					Blockrectangle newblock = new Blockrectangle(this);
					RelativeLayout.LayoutParams RlParams = new RelativeLayout.LayoutParams(
				            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					RlParams.leftMargin = X;
					RlParams.topMargin = Y;
					RelativeLayout layout = (RelativeLayout)findViewById(R.id.relativeLayout1);
					layout.addView(newblock, RlParams);
					break;
				case 5 :
					break;
				case 6 :
					break;
				case 7 :
					break;
				case 8 :
					break;
				}
			}
		}
		return false;
	}
  */  

}