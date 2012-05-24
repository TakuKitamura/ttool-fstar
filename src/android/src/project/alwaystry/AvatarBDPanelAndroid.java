package project.alwaystry;

import java.util.LinkedList;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class AvatarBDPanelAndroid extends View {
	
	public int mode;
	
	public static final int NORMAL = 0;
    public static final int MOVING_COMPONENT = 1;
    public static final int ADDING_CONNECTOR = 2;
    public static final int MOVE_CONNECTOR_SEGMENT = 3;
    public static final int MOVE_CONNECTOR_HEAD = 4;
    public static final int SELECTING_COMPONENTS = 5;
    public static final int SELECTED_COMPONENTS = 6;
    public static final int MOVING_SELECTED_COMPONENTS = 7;
    public static final int RESIZING_COMPONENT = 8;
	
	private Paint panelBGPaint; 
	
	private LinkedList compolist;
	
	private int mx, my;
	private int xsel=-1,ysel=-1;
	private int xendsel =-1, yendsel=-1;
	private int maxX,minX;
	private AlwaystryActivity activity;
	int clickedX,clickedY;
	
	protected AvatarBDConnectingPointAndroid selectedConnectingPoint;
	
	//for adding connectors
	protected AvatarBDConnectingPointAndroid p1 = null;
	
	protected TGComponentAndroid componentSelected;
	protected int createdtype;

	public AvatarBDPanelAndroid(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initPanel();
	}
	
	public AvatarBDPanelAndroid(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPanel();
	}
	
	public AvatarBDPanelAndroid(Context context) {
		super(context);
		initPanel();
		// TODO Auto-generated constructor stub
	}
	
	private void initPanel(){
		mode = NORMAL;
		compolist = new LinkedList();
		
		panelBGPaint = new Paint();
		panelBGPaint.setColor(Color.WHITE);
		
		activity =(AlwaystryActivity)this.getContext();

		AvatarBDBlockAndroid block1 = new AvatarBDBlockAndroid(100,100,this);
		compolist.add(block1);
		setClickable(true);
		
		TDiagramTouchManagerAndroid tManager = new TDiagramTouchManagerAndroid(this);
		setOnClickListener(tManager);
		setOnTouchListener(tManager);
		
		
		setCreatedtype(TGComponentAndroid.NOCOMPONENT);
	}
	
	
	
	protected TGComponentAndroid getSelectedComponent(int x1, int y1){
		TGComponentAndroid comp;
		// clearn selected..
		for(int i=0; i< compolist.size();i++){
			 ((TGComponentAndroid)compolist.get(i)).select(false);
		}
		invalidate();
		for(int i=0; i< compolist.size();i++){
			
			comp = ((TGComponentAndroid)compolist.get(i)).isOnMe(x1, y1);
			if(comp != null){
				return comp;
			}
			
		}
		
		return null;
	}
	
	protected void onDraw(Canvas canvas) {
		Log.i("panel", "drawing");
		canvas.drawRect(0, 0, getWidth(), getHeight(), panelBGPaint);
		
		for(int i=0; i<compolist.size();i++){
			((TGComponentAndroid)compolist.get(i)).internalDrawing(canvas);
		}
		
		if(this.getXsel()!=-1 && xendsel != -1){
			Paint fgPaintSel = new Paint();
			fgPaintSel.setColor(Color.BLACK);
			fgPaintSel.setStyle(Style.STROKE);
			fgPaintSel.setStrokeWidth(2);
			fgPaintSel.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));

			canvas.drawRect(xsel, ysel, xendsel, yendsel, fgPaintSel);
		}
		
	}
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //setMeasuredDimension(800,600);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
	
	public void showAllConnectingPoints(){
		for(int i=0; i< compolist.size();i++){
			if(compolist.get(i) instanceof AvatarBDBlockAndroid){
			((AvatarBDBlockAndroid)compolist.get(i)).setShowConnectingPoints(true);
			}
		}
		invalidate();
	}
	
	public void hideAllConnectingPoints(){
		for(int i=0; i< compolist.size();i++){
			if(compolist.get(i) instanceof AvatarBDBlockAndroid){
			((AvatarBDBlockAndroid)compolist.get(i)).setShowConnectingPoints(false);
			}
		}
		invalidate();
	}
	
	public AvatarBDConnectingPointAndroid getPointSelected(int x1, int y1){
		Log.i("panel", "point selected:"+x1+" / "+y1);
		AvatarBDConnectingPointAndroid p;
		for(int i=0; i< compolist.size();i++){
			if(compolist.get(i) instanceof AvatarBDBlockAndroid){
				AvatarBDBlockAndroid block = (AvatarBDBlockAndroid)compolist.get(i);
				for(int j=0; j< block.getNbConnectingPoints();j++){
					p = (AvatarBDConnectingPointAndroid)block.connectingPoints[j].isOnMe(x1, y1);
					
					
					if(p != null){
						Log.i("panel", "ppppp point selected:"+p.getX()+" / "+p.getY());
						return p;
					}
				}
			}
		}
		return null;
	}
	
	public void createComponent(int x, int y,int type){
		switch(type){
		case TGComponentAndroid.UML_NOTE:
			TGCNoteAndroid note = new TGCNoteAndroid(x,y,this);
			compolist.add(note);
			this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
			break;
		case TGComponentAndroid.AVATARBD_BLOCK:
			AvatarBDBlockAndroid block = new AvatarBDBlockAndroid(x, y, this);
			compolist.add(block);
			this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
			break;
		case TGComponentAndroid.AVATARBD_CRYPTOBLOCK:
			break;
		case TGComponentAndroid.AVATARBD_DATATYPE:
			AvatarBDDataTypeAndroid datatype = new AvatarBDDataTypeAndroid(x, y, this);
			compolist.add(datatype);
			this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
			break;
		case TGComponentAndroid.AVATARBD_COMPOSITION_CONNECTOR:
			if(p1 == null){
				p1 = getPointSelected(x, y);
			}else{
				AvatarBDConnectingPointAndroid p2 = getPointSelected(x,y);
				if(!p1.equals(getPointSelected(x,y)) && p2 != null){
					AvatarBDCompositionConnectorAndroid comConnector = new AvatarBDCompositionConnectorAndroid(p1, p2);
					compolist.add(comConnector);
					p1 = null;
					hideAllConnectingPoints();
					this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
				}
			}
			break;
		case TGComponentAndroid.AVATARBD_PORT_CONNECTOR:
			Log.i("AvatarBDPanel", "creating port connector");
			if(p1 == null){
				p1 = getPointSelected(x, y);
				Log.i("panel", "p1: "+p1.getX()+" / "+p1.getY());
			}else{
				
				AvatarBDConnectingPointAndroid p2 = getPointSelected(x,y);
				Log.i("panel", "p2: "+p2.getX()+" / "+p2.getY());
				if(!p1.equals(p2) && p2 != null){
					AvatarBDPortConnectorAndroid portConnector = new AvatarBDPortConnectorAndroid(p1, p2);
					compolist.add(portConnector);
					p1 = null;
					hideAllConnectingPoints();
					this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
				}
			}
			break;
		}
	}

	public int getYendsel() {
		return yendsel;
	}

	public void setYendsel(int yendsel) {
		this.yendsel = yendsel;
	}

	public int getXendsel() {
		return xendsel;
	}

	public void setXendsel(int xendsel) {
		this.xendsel = xendsel;
	}
	
	public int getYsel() {
		return ysel;
	}

	public void setYsel(int ysel) {
		this.ysel = ysel;
	}

	public int getXsel() {
		return xsel;
	}

	public void setXsel(int xsel) {
		this.xsel = xsel;
	}
	
//	public void deleteBlock(int i){
//		compolist.remove(i);
//		invalidate();
//	}

	public void deleteComponent(){
		if(componentSelected != null){
			compolist.remove(componentSelected);
			componentSelected = null;
		}
		invalidate();
	}
	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public int getMinX() {
		return minX;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}
	
	public AlwaystryActivity getActivity(){
		return activity;
	}
	
	public void setSelectedTGConnectingPoint(AvatarBDConnectingPointAndroid p) {
		selectedConnectingPoint = p;
    }
	public AvatarBDConnectingPointAndroid getSelectedTGConnectingPoint() {
        return selectedConnectingPoint;
    }

	public TGComponentAndroid getComponentSelected() {
		return componentSelected;
	}

	public void setComponentSelected(TGComponentAndroid componentSelected) {
		this.componentSelected = componentSelected;
		if(componentSelected != null){
			componentSelected.select(true);
		}
			invalidate();
	}

	public int getCreatedtype() {
		return createdtype;
	}

	public void setCreatedtype(int createdtype) {
		this.createdtype = createdtype;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	
	
//	public boolean onTouch(View v, MotionEvent event) {
//		// TODO Auto-generated method stub
//		final int X = (int)event.getX();
//		final int Y = (int)event.getY();
//		
//		switch(event.getAction()){
//		case MotionEvent.ACTION_DOWN:
//			boolean blockClicked = false;
//			Log.i("Panel", "ACTION DOWN!");
//			if(((AlwaystryActivity)this.getContext()).getclickaction() == 1){
//				boolean clickonblock = false;
//				for(int i=0;i<compolist.size();i++){
//					if(((TGComponentAndroid)compolist.get(i)).isOnOnlyMe(X, Y)){
//						clickonblock = true;
//					}
//				}
//				if(!clickonblock){
//				this.setXsel(X);
//				this.setYsel(Y);	
//				}
//			}
//			if(((AlwaystryActivity)this.getContext()).getclickaction() == 8){
//				for(int i=0; i<compolist.size();i++){
//					if(compolist.get(i) instanceof AvatarBDBlockAndroid){
//						for(int j=0; j<((AvatarBDBlockAndroid)compolist.get(i)).getNbConnectingPoints();j++){
//							if(((AvatarBDBlockAndroid)compolist.get(i)).connectingPoints[j].isOnMe(X, Y)){
//								for(int n=0; n<connectorlist.size();n++){
//									if(!((AvatarBDPortConnectorAndroid)connectorlist.get(n)).hasStart()){
//										((AvatarBDPortConnectorAndroid)connectorlist.get(n)).setOutPoint(((AvatarBDBlockAndroid)compolist.get(i)).connectingPoints[j]);
//									}else if(!((AvatarBDPortConnectorAndroid)connectorlist.get(n)).hasEnd()){
//										((AvatarBDPortConnectorAndroid)connectorlist.get(n)).setInPoint(((AvatarBDBlockAndroid)compolist.get(i)).connectingPoints[j]);
//										hideAllConnectingPoints();
//										((AlwaystryActivity)this.getContext()).resetClickaction();
//									}
//										
//								}
//							}
//						}
//					}
//				}
//				invalidate();
//			}
//			
//			if(((AlwaystryActivity)this.getContext()).getclickaction() == 7){
//				for(int i=0; i<compolist.size();i++){
//					if(compolist.get(i) instanceof AvatarBDBlockAndroid){
//						for(int j=0; j<((AvatarBDBlockAndroid)compolist.get(i)).getNbConnectingPoints();j++){
//							if(((AvatarBDBlockAndroid)compolist.get(i)).connectingPoints[j].isOnMe(X, Y)){
//								for(int n=0; n<compoconnectorlist.size();n++){
//									if(!((AvatarBDCompositionConnectorAndroid)compoconnectorlist.get(n)).hasStart()){
//										((AvatarBDCompositionConnectorAndroid)compoconnectorlist.get(n)).setOutPoint(((AvatarBDBlockAndroid)compolist.get(i)).connectingPoints[j]);
//									}else if(!((AvatarBDCompositionConnectorAndroid)compoconnectorlist.get(n)).hasEnd()){
//										((AvatarBDCompositionConnectorAndroid)compoconnectorlist.get(n)).setInPoint(((AvatarBDBlockAndroid)compolist.get(i)).connectingPoints[j]);
//										hideAllConnectingPoints();
//										((AlwaystryActivity)this.getContext()).resetClickaction();
//									}
//										
//								}
//							}
//						}
//					}
//				}
//				invalidate();
//				
//			}
//			for(int i=0;i<compolist.size();i++){
//				if(((TGComponentAndroid)compolist.get(i)).isOnOnlyMe(X, Y)){
//					Log.i("Panel", "one block is clicked!");
//					((TGComponentAndroid)compolist.get(i)).select(true);
//					blockClicked = true;
//				}
//			}
//			
//			if(((AlwaystryActivity)this.getContext()).getclickaction() == 4){
//				AvatarBDBlockAndroid block2 = new AvatarBDBlockAndroid(X,Y,this);
//				compolist.add(block2);
//				invalidate();
//			}
//			if(((AlwaystryActivity)this.getContext()).getclickaction() == 6){
//				AvatarBDDataTypeAndroid datatype2 = new AvatarBDDataTypeAndroid(X,Y,this);
//				compolist.add(datatype2);
//				invalidate();
//			}
//			
//			
//			break;
//		case MotionEvent.ACTION_UP:
//			if(((AlwaystryActivity)this.getContext()).getclickaction()!=1 && ((AlwaystryActivity)this.getContext()).getclickaction()!=8 && ((AlwaystryActivity)this.getContext()).getclickaction()!=7){
//				((AlwaystryActivity)this.getContext()).resetClickaction();
//			}
//			for(int i=0;i<compolist.size();i++){
//				((TGComponentAndroid)compolist.get(i)).select(false);
//			}
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if(this.getXsel()==-1){
//			for(int i=0;i<compolist.size();i++){
//				if(((TGComponentAndroid)compolist.get(i)).isSelected()){
//					Log.i("Panel", "one block is moving!");
//					int nx = ((TGComponentAndroid)compolist.get(i)).getX()+(X-(int)event.getX());
//					int ny = ((TGComponentAndroid)compolist.get(i)).getY()+(Y-(int)event.getY());
//					((TGComponentAndroid)compolist.get(i)).setCd(X, Y);
//					Log.i("block location", "X: "+X+" Y: "+X);
//					invalidate();
//					break;
//				}
//			}
//			}else{
//				xendsel = X;
//				yendsel = Y;
//				invalidate();
//			}
//			
//			break;
//		}
//		
//		return true;
//	}
	
}
