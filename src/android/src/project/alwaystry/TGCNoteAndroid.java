package project.alwaystry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class TGCNoteAndroid extends TGComponentAndroid{
	
	private Paint mPaint;
	private Paint ePaint;
	private Paint tPaint;
	private TextPaint mTextPaint;
	
	protected String[] value; //applies if editable
    protected String name ;
    
    protected int limit = 15;
	
	public TGCNoteAndroid(int _x, int _y, int _minWidth, int _minHeight,int _maxWidth,int _maxHeight, View _panel){
		super(_x, _y, _minWidth, _minHeight, _maxWidth, _maxHeight, _panel);
		
		width = 180;
        height = 80;
        //minWidth = 50;
        //minHeight = 20;
        
        nbConnectingPoints = 0;
        addTGConnectingPointsComment();
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
       // mPaint.setColor(Color.rgb(193, 218, 241));
        
        ePaint = new Paint();
        ePaint.setAntiAlias(true);
        ePaint.setStrokeWidth(2);
        ePaint.setColor(Color.BLACK);
        
        tPaint = new Paint();
        tPaint.setAntiAlias(true);
        tPaint.setStrokeWidth(2);
        tPaint.setColor(Color.LTGRAY);
        
        
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(Color.BLACK);
        
        name = "UML Note";
        value = new String[]{"UML note:","Double click to edit!!!!"};
	}

	public TGComponentAndroid isOnMe(int x1, int y1) {
		if ((x1 >= x) && ((x + width) >= x1) && (y1 >= y) && ((y + height) >= y1)) {
            return this;
       }
        return null;
		
	}

	public void internalDrawing(Canvas canvas) {
		if(selected){
			ePaint.setColor(Color.RED);
			tPaint.setColor(Color.RED);
		}else{
			ePaint.setColor(Color.BLACK);
			tPaint.setColor(Color.LTGRAY);
		}			
		for(int i=0;i<value.length;i++){
			if(value[i].length()*5 > width){
				width = value[i].length()*5;
			}
		}
		
		height = value.length *15;
		
		canvas.drawLine(x, y, x+width, y, ePaint);
		canvas.drawLine(x, y, x, y+height, ePaint);
		canvas.drawLine(x, y+height, x+width-limit, y+height, ePaint);
		canvas.drawLine(x+width, y, x+width, y+height-limit, ePaint);
		
		mPaint.setColor(Color.rgb(173, 190, 234));
		
		Path path = new Path();
		path.moveTo((float)x, (float)y);
		path.lineTo((float)(x+width), (float)y);
		path.lineTo((float)(x + width), (float)(y+height-limit));
		path.lineTo((float)(x + width-limit), (float)(y+height));
		path.lineTo((float)x, (float)(y+height));
		
		mPaint.setStyle(Paint.Style.FILL);
		
		canvas.drawPath(path, mPaint);
		
		Path path1 = new Path();
		path1.moveTo((float)(x + width), (float)(y+height-limit));
		path1.lineTo((float)(x+width-4), (float)(y+height-limit+3));
		path1.lineTo((float)(x + width-10), (float)(y+height-limit+2));
		path1.lineTo((float)(x + width-limit), (float)(y+height));
		
		ePaint.setStyle(Paint.Style.STROKE);
		canvas.drawPath(path1, ePaint);
		
		tPaint.setStyle(Paint.Style.FILL);
		canvas.drawPath(path1, tPaint);
		
		int step =12;
		int cpt=3;
		for(int i=0;i<value.length;i++){
			cpt+=step;
			canvas.drawText(value[i], x+5, y+cpt, mTextPaint);
		}
		
		this.drawTGConnectingPoint(canvas, getCptype());
	}

	public void setValue(String s){
		String[] newValues = s.split("\n");
		value = new String[newValues.length];
		for(int i=0;i<newValues.length;i++){
			value[i] = new String(newValues[i]);
		}
	}
	public String getValue(){
		String v = "";
		for(int i=0;i<value.length;i++){
			v+= (value[i]+"\n");
		}
			
		return v;
	}
	protected boolean editOndoubleClick(int _x, int _y) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(panel.getContext());
		LayoutInflater inflater = (LayoutInflater) panel.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.tgcnotealert,
                (ViewGroup) panel.findViewById(R.id.linearLayout1));
		alert.setTitle("setting the note");
		final EditText input = (EditText)layout.findViewById(R.id.noteEditText);
		input.setText(getValue());
		alert.setView(layout);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				setValue(input.getText().toString());
			    panel.invalidate();
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();
		
		// TODO Auto-generated method stub
		return false;
	}

}
