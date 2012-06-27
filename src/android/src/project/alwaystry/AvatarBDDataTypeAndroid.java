package project.alwaystry;

import java.util.Vector;

import copyfromJAVAsource.TAttribute;

import myutilandroid.GraphicLibAndroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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


public class AvatarBDDataTypeAndroid extends TGComponentAndroid{
	
	private String stereotype = "datatype";
 
	protected Vector myAttributes;

	private Paint mPaint;
	private Paint cpPaint;
	private Paint ePaint;
	
	private TextPaint mTextPaint;
	
	private int cptype =-1;
	
	public AvatarBDDataTypeAndroid(int _x, int _y, int _minWidth, int _minHeight,int _maxWidth,int _maxHeight, View _panel)  {
		super(_x, _y, _minWidth, _minHeight, _maxWidth, _maxHeight, _panel);
		
		Log.i("datatype", "panel"+panel);
		name = "datatypeName";
		
		width = 250;
		height = 200;
		
		nbConnectingPoints = 0;
        connectingPoints = new TGConnectingPointAndroid[0];
        
        addTGConnectingPointsComment();
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.rgb(156, 220, 162));
        
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
		
		myAttributes = new Vector();
	}
	
	public void internalDrawing(Canvas canvas) {
		int lp=x;
		int tp=y;
		int rp=x+width;
		int bp=y+height;
		
		if(selected){
			ePaint.setColor(Color.RED);
			mTextPaint.setColor(Color.RED);
		}else{
			ePaint.setColor(Color.BLACK);
			mTextPaint.setColor(Color.BLACK);
		}
		
		mPaint.setColor(Color.rgb(156, 220, 162));
		canvas.drawRect(lp+3, tp+3, rp-3, bp-3, mPaint);
		
		canvas.drawLine(lp, tp, rp, tp, ePaint);
		canvas.drawLine(lp, tp, lp, bp, ePaint);
		canvas.drawLine(rp, tp, rp,bp, ePaint);
		canvas.drawLine(lp, bp, rp, bp, ePaint);
		
		String ster = "<<"+stereotype+">>";
		mTextPaint.setFakeBoldText(true);
		canvas.drawText(ster, lp+(width-ster.length())/2, tp+13, mTextPaint);
		
		mTextPaint.setFakeBoldText(false);
		if (name != null && name.length() > 0) {
            canvas.drawText(name,lp+(width-name.length())/2, tp+25,mTextPaint);
        }
		
		canvas.drawLine(x, y+30, rp, y+30, ePaint);
		
		//Icon
		if(width>30 && height > 30){
			Bitmap bitmap = BitmapFactory.decodeResource(panel.getResources(), R.drawable.avatarhead32);
			
			canvas.drawBitmap(bitmap, x+width - 30, y+5, ePaint);

		}

		//draw Attributes
		TextPaint tpaint = new TextPaint(mTextPaint);
		tpaint.setTextAlign(Paint.Align.LEFT);
		
		int cpt = 33;
		int index = 0;
		String attr;
		TAttribute a;
		int w;
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
		
	//	canvas.drawText("attribute 1", x+50+7, y+60, mTextPaint);
		
	//	canvas.drawLine(x, y+70, rp, y+70, ePaint);
		
		this.drawTGConnectingPoint(canvas, getCptype());
		
	}
	public View getPanel() {
		return panel;
	}
	public void setPanel(View panel) {
		this.panel = panel;
	}

	
	public TGComponentAndroid isOnMe(int x1, int y1) {
		// TODO Auto-generated method stub
		if ((x1 >= x) && ((x + width) >= x1) && (y1 >= y) && ((y + height) >= y1)) {
            return this;
       }
		return null;
	}
	
	public int getCptype() {
		return cptype;
	}

	public void setCptype(int cptype) {
		this.cptype = cptype;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean inEditNameArea(int x1,int y1){
		return GraphicLibAndroid.isInRectangle(x1, y1, getX(), getY(), getWidth(), 30);
	}
	
	protected boolean editOndoubleClick(int _x, int _y) {
		// TODO Auto-generated method stub
		if(inEditNameArea(_x, _y)){
			Log.i("datatype", "doubleclick"+panel);
			AlertDialog.Builder alert = new AlertDialog.Builder(panel.getContext());
//			AlertDialog alertDialog;				
//			panel.getContext();
			LayoutInflater inflater = (LayoutInflater) panel.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.blocknamealert,
			                               (ViewGroup) panel.findViewById(R.id.blockLayout));
			alert.setTitle("setting value");
			
			TextView text = (TextView)layout.findViewById(R.id.textView1);
			text.setText("Datatype name");
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
			
			String[] attributes = new String[myAttributes.size()];
			for(int i=0;i<myAttributes.size();i++){
				 attributes[i] = ((TAttribute)myAttributes.get(i)).toString();
			}
			
			Bundle attribundle = new Bundle();
			attribundle.putStringArray("attributes", attributes);
			
			Intent intent = new Intent().setClass(panel.getContext(),EditAttributesActivity.class);	
			intent.putExtras(attribundle);
			((Activity) panel.getContext()).startActivityForResult(intent,AlwaystryActivity.EDIT_ATTRIBUTES);
			
		}
		return true;
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
	
	public String getDataTypeName(){
		return name;
	}
}
