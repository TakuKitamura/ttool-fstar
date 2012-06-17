package project.alwaystry;

import java.util.Vector;

import copyfromJAVAsource.AvatarMethod;
import copyfromJAVAsource.AvatarSignal;
import copyfromJAVAsource.TAttribute;

import myutilandroid.GraphicLibAndroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class AvatarBDBlockAndroid extends TGComponentAndroid{

	private String stereotype = "block";
	//private String name = "Name";

	private Paint mPaint;
	private Paint cpPaint;
	private Paint ePaint;
	
	
	
	private TextPaint mTextPaint;
	
	protected Vector myAttributes, myMethods, mySignals;
	
	

	public AvatarBDBlockAndroid(int _x, int _y, int _minWidth, int _minHeight,int _maxWidth,int _maxHeight, View _panel){
		super(_x, _y, _minWidth, _minHeight, _maxWidth, _maxHeight, _panel);

		name = "Name";
		
		width = 250;
		height = 200;
		
		//showConnectingPoints = false;
		select(false);
		
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
        
        nbConnectingPoints = 16;
        connectingPoints = new AvatarBDConnectingPointAndroid[16];
        
        connectingPoints[0] = new AvatarBDConnectingPointAndroid(this,0,0,true,true,0,0);
        connectingPoints[1] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.5, 0.0);
        connectingPoints[2] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 1.0, 0.0);
        connectingPoints[3] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.0, 0.5);
        connectingPoints[4] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 1.0, 0.5);
        connectingPoints[5] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.0, 1.0);
        connectingPoints[6] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.5, 1.0);
        connectingPoints[7] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 1.0, 1.0);
        
        connectingPoints[8] = new AvatarBDConnectingPointAndroid(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoints[9] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.75, 0.0);
        connectingPoints[10] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.0, 0.25);
        connectingPoints[11] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 1.0, 0.25);
        connectingPoints[12] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.0, 0.75);
        connectingPoints[13] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 1.0, 0.75);
        connectingPoints[14] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.25, 1.0);
        connectingPoints[15] = new AvatarBDConnectingPointAndroid(this,0, 0, true, true, 0.75, 1.0);
        
        addTGConnectingPointsComment();
        
        myAttributes = new Vector();
		myMethods = new Vector();
		mySignals = new Vector();
		
		myAttributes.add(new TAttribute(TAttribute.PRIVATE,"attribute1","2",TAttribute.INTEGER));
	}
    
	public TGComponentAndroid isOnMe(int x1, int y1){
		if ((x1 >= x) && ((x + width) >= x1) && (y1 >= y) && ((y + height) >= y1)) {
            return this;
       }
        return null;
	}

	public boolean inEditNameArea(int x1,int y1){
		return GraphicLibAndroid.isInRectangle(x1, y1, getX(), getY(), getWidth(), 30);
	}
	
	
	public void internalDrawing(Canvas canvas) {
		if(selected){
			ePaint.setColor(Color.RED);
			mTextPaint.setColor(Color.RED);
		}else{
			ePaint.setColor(Color.BLACK);
			mTextPaint.setColor(Color.BLACK);
		}
		Log.i("BDblock", "internal drawing!");
		int lp=getX();
		int tp=getY();
		int rp=getX()+getWidth();
		int bp=getY()+getHeight();
        
        mPaint.setColor(Color.rgb(193, 218, 241));
		canvas.drawRect(lp+3, tp+3, rp-3, bp-3, mPaint);
		
		canvas.drawLine(lp, tp, rp, tp, ePaint);
		canvas.drawLine(lp, tp, lp, bp, ePaint);
		canvas.drawLine(rp, tp, rp,bp, ePaint);
		canvas.drawLine(lp, bp, rp, bp, ePaint);
		
		String ster = "<<"+stereotype+">>";
		
		mTextPaint.setFakeBoldText(true);
		canvas.drawText(ster, lp+(getWidth()-ster.length())/2, tp+13, mTextPaint);
		
		mTextPaint.setFakeBoldText(false);
		if (name != null && name.length() > 0) {
            canvas.drawText(name,lp+(getWidth()-name.length())/2, tp+25,mTextPaint);
        }
		
//		mPaint.setColor(Color.BLACK);
//		mPaint.setStrokeWidth(2);
		canvas.drawLine(x, y+30, rp, y+30, ePaint);
		
		//Icon
		
		TextPaint tpaint = new TextPaint(mTextPaint);
		tpaint.setTextAlign(Paint.Align.LEFT);
		//draw Attributes
		int cpt = 33;
		int index = 0;
		String attr;
		
		TAttribute a;
		
		int w;
		Log.i("block drawing", ""+mTextPaint.getTextSize());
		int step = 10;
		
		while(index< myAttributes.size()){
			cpt+=step;
			if(cpt >= getHeight() - 5){
				break;
			}
			a = (TAttribute)(myAttributes.get(index));
			attr = a.toString();
			w =attr.length()*8;
			if(w +5 < width){
				canvas.drawText(attr, x+5, y+cpt, tpaint);
			}else{
				attr = "...";
				w = attr.length()*8;
				if(w +5 < width){
					canvas.drawText(attr, x+5, y+cpt, tpaint);
				}else{
					cpt -= step;
				}
			}
			index ++;
		}
		
		cpt +=10;
		//methods
		if(myMethods.size()>0){
			if(cpt < getHeight()){
				canvas.drawLine(x, y+cpt, rp,y+cpt, ePaint);
			}
		}
		cpt +=3;
		index =0;
		String method;
		AvatarMethod am;
		
		while(index < myMethods.size()){
			cpt += step;
			if(cpt >= getHeight() - 5){
				break;
			}
			am = (AvatarMethod)(myMethods.get(index));
			method = "~ " + am.toString();
			w = method.length()*8;
			if(w+5 < getWidth()){
				canvas.drawText(method, x+5, y+cpt, tpaint);
			}else{
				method = "...";
				w = method.length()*8;
				if(w+5 < getWidth()){
					canvas.drawText(method, x+5, y+cpt, tpaint);
				}else{
					cpt -=step;
				}
			}
			index++;
		}
		
		cpt +=10;
				
		//Signals
		if(mySignals.size()>0){
			if(cpt < getHeight()){
				canvas.drawLine(x, y+cpt, rp,y+cpt, ePaint);
			}
		}
		cpt+=3;
		index = 0;
		String signal;
		AvatarSignal as;
		
		while(index<mySignals.size()){
			cpt += step;
			if(cpt >= getHeight() - 5){
				break;
			}
			as = (AvatarSignal)(mySignals.get(index));
			signal = "~ " + as.toString();
			w = signal.length()*8;
			if(w+5 < getWidth()){
				canvas.drawText(signal, x+5, y+cpt, tpaint);
			}else{
				signal = "...";
				w = signal.length()*8;
				if(w+5 < getWidth()){
					canvas.drawText(signal, x+5, y+cpt, tpaint);
				}else{
					cpt-=step;
				}
			}
			
			index++;
		}
		//canvas.drawText("attribute 1", x+50+7, y+60, mTextPaint);
		
		//canvas.drawLine(x, y+70, rp, y+70, ePaint);
		
		//draw methods
		
		//draw signals
	
//		if(showConnectingPoints){
//			Log.i("block", ""+showConnectingPoints);
//			//canvas.drawRect(0, 0, 8, 8, cpPaint);
//			for(int i=0; i<nbConnectingPoints ; i++){
//			//	Log.i("block", "drawing points");
//				connectingPoints[i].internalDrawing(canvas);
//			}
//		}
		
		drawTGConnectingPoint(canvas, cptype);
		
	}
	
	public String getStereotype() {
		return stereotype;
	}

	public void setStereotype(String stereotype) {
		this.stereotype = stereotype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public View getPanel() {
		return panel;
	}

	public void setPanel(View panel) {
		this.panel = panel;
	}



	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}




//	public boolean isShowConnectingPoints() {
//		return showConnectingPoints;
//	}
//
//	public void setShowConnectingPoints(boolean showConnectingPoints) {
//		this.showConnectingPoints = showConnectingPoints;
//	}
    
	public int getNbConnectingPoints(){
		return nbConnectingPoints;
	}


	protected boolean editOndoubleClick(int _x, int _y) {
		// TODO Auto-generated method stub
		
		if(inEditNameArea(_x, _y)){
		AlertDialog.Builder alert = new AlertDialog.Builder(panel.getContext());
		//AlertDialog alertDialog;				
		//panel.getContext();
		LayoutInflater inflater = (LayoutInflater) panel.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.blocknamealert,
		                               (ViewGroup) panel.findViewById(R.id.linearLayout1));

		alert.setTitle("setting value");
		
		TextView text = (TextView)layout.findViewById(R.id.textView1);
		text.setText("Block name");
		final EditText input = (EditText)layout.findViewById(R.id.editText1);
		input.setText(getName());
		alert.setView(layout);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				setName(input.getText().toString());
			    panel.invalidate();
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();
	}else{
		
		String s = ((TAttribute)myAttributes.get(0)).toString();
		TAttribute a = TAttribute.getTAttributeFromString(s);
		if(a!=null){
		String s1 = a.toString();
		Log.i("block", "s1:"+s1);
		}
		String[] attributes = new String[myAttributes.size()];
		for(int i=0;i<myAttributes.size();i++){
			 attributes[i] = ((TAttribute)myAttributes.get(i)).toString();
		}
		 
		String[] methods = new String[myMethods.size()];
		for(int i=0;i<myMethods.size();i++){
			methods[i] = ((AvatarMethod)myMethods.get(i)).toString();
		}
		 
		String[] signals = new String[mySignals.size()];
		for(int i=0;i<mySignals.size();i++){
			methods[i] = ((AvatarSignal)mySignals.get(i)).toString();
		}
		
		String[] datatypes = new String[((AvatarBDPanelAndroid)panel).getAllDataTypes().size()];
		for(int i=0;i<((AvatarBDPanelAndroid)panel).getAllDataTypes().size();i++){
			datatypes[i] =((AvatarBDPanelAndroid)panel).getAllDataTypes().get(i);
		}
		Bundle attribundle = new Bundle();
		attribundle.putStringArray("attributes", attributes);
		attribundle.putStringArray("methods", methods);
		attribundle.putStringArray("signals", signals);
		attribundle.putStringArray("datatypes", datatypes);
		Intent intent = new Intent().setClass(panel.getContext(),EditAttributesActivity.class);	
		intent.putExtras(attribundle);
		((Activity) panel.getContext()).startActivityForResult(intent,AlwaystryActivity.EDIT_ATTRIBUTES);
	}
		
		
		
		return true;
	}
	public Vector getAttributes(){
		return myAttributes;
	}

	public Vector getMethods(){
		return myMethods;
	}
	
	public Vector getSignals(){
		return mySignals;
	}
	public void setAttributes(String[] attributes){
		Vector attributesPar = new Vector();
		for(int i=0; i<attributes.length; i++) {
            attributesPar.addElement(TAttribute.getTAttributeFromString(attributes[i]));
        }
		
		myAttributes.removeAllElements();
		for(int i=0; i<attributesPar.size(); i++) {
			myAttributes.addElement(attributesPar.elementAt(i));
        }
	}
	public void setMethods(String[] methods){
		
		Vector methodsPar = new Vector();
		for(int i=0; i<methods.length; i++) {
			methodsPar.addElement(AvatarMethod.isAValidMethod(methods[i]));
			Log.i("set methods", methods[i]);
			Log.i("set methods", "method "+AvatarMethod.isAValidMethod(methods[i]));
        }
		
		myMethods.removeAllElements();
		for(int i=0; i<methodsPar.size(); i++) {
			myMethods.addElement(methodsPar.elementAt(i));
        }
		
	}
	
	public void setSignals(String[] signals){
		Vector signalsPar = new Vector();
		for(int i=0; i<signals.length; i++) {
			signalsPar.addElement(AvatarSignal.isAValidSignal(signals[i]));
        }
		
		mySignals.removeAllElements();
		for(int i=0; i<signalsPar.size(); i++) {
			mySignals.addElement(signalsPar.elementAt(i));
        }
	}
	
	public void addCryptoElements() {
		// Adding function
		String method = "Message aencrypt(Message msg, Key k)";
		addMethodIfApplicable(myMethods, method);
		method = "Message adecrypt(Message msg, Key k)";
		addMethodIfApplicable(myMethods, method);
		method = "Key pk(Key k)";
		addMethodIfApplicable(myMethods, method);
		method = "Message sign(Message msg, Key k)";
		addMethodIfApplicable(myMethods, method);
		method = "bool verifySign(Message msg1, Message sig, Key k)";
		addMethodIfApplicable(myMethods, method);
		
		/* Certifying */
		method = "Message cert(Key k, Message msg)";
		addMethodIfApplicable(myMethods, method);
		method = "bool verifyCert(Message cert, Key k)";
		addMethodIfApplicable(myMethods, method);
		method = "Key getpk(Message cert)";
		addMethodIfApplicable(myMethods, method);
		
	
		method = "Message sencrypt(Message msg, Key k)";
		addMethodIfApplicable(myMethods, method);
		method = "Message sdecrypt(Message msg, Key k)";
		addMethodIfApplicable(myMethods, method);
		
		method = "Message hash(Message msg)";
		addMethodIfApplicable(myMethods, method);
		
		method = "Message MAC(Message msg, Key k)";
		addMethodIfApplicable(myMethods, method);
		method = "bool verifyMAC(Message msg, Key k, Message macmsg)";
		addMethodIfApplicable(myMethods, method);
		
		method = "Message concat2(Message msg1, Message msg2)";
		addMethodIfApplicable(myMethods, method);
		
		method = "Message concat3(Message msg1, Message msg2, Message msg3)";
		addMethodIfApplicable(myMethods, method);
		
		method = "Message concat4(Message msg1, Message msg2, Message msg3, Message msg4)";
		addMethodIfApplicable(myMethods, method);
		
		
		method = "get2(Message msg, Message msg1, Message msg2)";
		addMethodIfApplicable(myMethods, method);
		
		method = "get3(Message msg, Message msg1, Message msg2, Message msg3)";
		addMethodIfApplicable(myMethods, method);
		
		method = "get4(Message msg, Message msg1, Message msg2, Message msg3, Message msg4)";
		addMethodIfApplicable(myMethods, method);
		
		// Adding channels chin chout
		String signal = "in chin(Message msg)";
		addSignalIfApplicable(mySignals, signal);
		signal = "out chout(Message msg)";
		addSignalIfApplicable(mySignals, signal);
		
	}
	private void addSignalIfApplicable(Vector _v, String _s) {
		AvatarSignal as;
		for(Object o: _v) {
			if (o instanceof AvatarSignal) {
				as = (AvatarSignal)o;
				if (as.toString().compareTo(_s) == 0) {
					return;
				}
			}
		}
		
		as = AvatarSignal.isAValidSignal(_s);
		if (as != null) {
			_v.add(as);
		}
	}
	
	private void addMethodIfApplicable(Vector _v, String _s) {
		AvatarMethod am;
		for(Object o: _v) {
			if (o instanceof AvatarMethod) {
				am = (AvatarMethod)o;
				if (am.toString().compareTo(_s) == 0) {
					return;
				}
			}
		}
		
		am = AvatarMethod.isAValidMethod(_s);
		if (am != null) {
			_v.add(am);
		}
	}
	
}
