package project.alwaystry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import myutilandroid.GraphicLibAndroid;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import copyfromJAVAsource.AvatarSignal;

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
	
	//for moving connector head.
	protected int x1;
    protected int y1;
    protected int x2;
    protected int y2;
	
	private int mx, my;

	private int xsel=-1,ysel=-1;
	private int xendsel =-1, yendsel=-1;
	protected int xSEL,ySEL,widthSel,heightSel;
	protected int sel = 5;
	protected boolean showSelectionZone = false;
	private int maxX,minX;
	private AlwaystryActivity activity;
	
	protected int minWidth = 100,minHeight = 50,maxWidth = 600,maxHeight = 500;
	
	
	protected AvatarBDConnectingPointAndroid selectedConnectingPoint;
	
	//for adding connectors
	protected TGConnectingPointAndroid p1 = null;
	
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
	
		initPanel();
	}
	
	public AvatarBDPanelAndroid(Context context) {
		super(context);
		initPanel();
		
	}
	
	private void initPanel(){
		mode = NORMAL;
		compolist = new LinkedList();
		
		panelBGPaint = new Paint();
		panelBGPaint.setColor(Color.WHITE);
		
		activity =(AlwaystryActivity)this.getContext();

		AvatarBDBlockAndroid block1 = new AvatarBDBlockAndroid(100,100,minWidth,minHeight,maxWidth,maxHeight,this);
		compolist.add(block1);
		setClickable(true);
		
		TDiagramTouchManagerAndroid tManager = new TDiagramTouchManagerAndroid(this);
	
		setOnTouchListener(tManager);
		
		setCreatedtype(TGComponentAndroid.NOCOMPONENT);
	}
	
	
	
	protected TGComponentAndroid getSelectedComponent(int _x1, int _y1){
		TGComponentAndroid comp;
		// clearn selected..
		for(int i=0; i< compolist.size();i++){
			 ((TGComponentAndroid)compolist.get(i)).select(false);
		}
		invalidate();
		for(int i=0; i< compolist.size();i++){
			
			comp = ((TGComponentAndroid)compolist.get(i)).isOnMe(_x1, _y1);
			if(comp != null){
				return comp;
			}
			
		}
		
		return null;
	}
	
	public void setMovingHead(int _x1, int _y1, int _x2, int _y2) {
        x1 = _x1; y1 = _y1;
        x2 = _x2; y2 = _y2;
    }
	
	protected void onDraw(Canvas canvas) {
		Log.i("panel", "drawing");
		canvas.drawRect(0, 0, getWidth(), getHeight(), panelBGPaint);
		
		for(int i=0; i<compolist.size();i++){
			((TGComponentAndroid)compolist.get(i)).internalDrawing(canvas);
		}
		
		if(mode == MOVE_CONNECTOR_HEAD){
			Paint fgPaintSel = new Paint();
			fgPaintSel.setColor(Color.MAGENTA);
			fgPaintSel.setStyle(Style.STROKE);
			fgPaintSel.setStrokeWidth(2);
			fgPaintSel.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));
			
			canvas.drawLine(x1, y1, x2, y2, fgPaintSel);
		}
		
		if(mode == SELECTING_COMPONENTS){
			Log.i("panel", "drawing selecting+ xsel: "+getXsel());
			
			if(this.getXsel()!=-1 && xendsel != -1){
				Paint fgPaintSel = new Paint();
				fgPaintSel.setColor(Color.BLACK);
				fgPaintSel.setStyle(Style.STROKE);
				fgPaintSel.setStrokeWidth(2);
				fgPaintSel.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
	
				canvas.drawRect(xsel, ysel, xendsel, yendsel, fgPaintSel);
			}
		}
		
		if (((mode == SELECTED_COMPONENTS) || (mode == MOVING_SELECTED_COMPONENTS))) {
			Log.i("panel", "drawing selected "+getMode());
			Paint fgPaintSel = new Paint();
			if (showSelectionZone) {
				Log.i("panel", "drawing selected "+xsel+" "+ysel+" "+xendsel+" "+yendsel);
				if (mode == MOVING_SELECTED_COMPONENTS) {
					
					fgPaintSel.setColor(Color.MAGENTA);
				} else {
					
					fgPaintSel.setColor(Color.RED);
				}
				
			} else {
				
			}
			
			
			fgPaintSel.setStyle(Style.STROKE);
			fgPaintSel.setStrokeWidth(2);
			fgPaintSel.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));

			canvas.drawRect(xSEL, ySEL, xSEL+widthSel, ySEL+heightSel, fgPaintSel);
			fgPaintSel.setStyle(Style.FILL);
			canvas.drawRect(xSEL - sel, ySEL - sel, xSEL + sel, ySEL + sel,fgPaintSel);
			canvas.drawRect(xSEL - sel + widthSel, ySEL - sel, xSEL + sel + widthSel,ySEL + sel,fgPaintSel);
			canvas.drawRect(xSEL - sel, ySEL - sel + heightSel, xSEL + sel, ySEL + sel + heightSel,fgPaintSel);
			canvas.drawRect(xSEL - sel + widthSel, ySEL - sel + heightSel,xSEL + sel + widthSel, ySEL + sel + heightSel,fgPaintSel);
			if (showSelectionZone) {
				//GraphicLib.setNormalStroke(g);
			}
		}
		
	}
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //setMeasuredDimension(800,600);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
	
	public void showAllConnectingPoints(int type){
		for(int i=0; i< compolist.size();i++){
			if(!(compolist.get(i) instanceof TGConnectorAndroid)){
			((TGComponentAndroid)compolist.get(i)).setCptype(type);
			}
		}
		invalidate();
	}
	
	public void hideAllConnectingPoints(){
		for(int i=0; i< compolist.size();i++){
			if(!(compolist.get(i) instanceof TGConnectorAndroid)){
			((TGComponentAndroid)compolist.get(i)).setCptype(-1);
			}
		}
		invalidate();
	}

	public TGConnectingPointAndroid getPointSelected(int x1, int y1,int type){
		Log.i("panel", "point selected:"+x1+" / "+y1);
		TGConnectingPointAndroid p;
		TGComponentAndroid com;
		
		for(int i=0; i< compolist.size();i++){
			com= (TGComponentAndroid)(compolist.get(i));
			for(int j=0; j<com.nbConnectingPoints;j++){
				p = (TGConnectingPointAndroid)(com.connectingPoints[j]).isOnMe(x1, y1);
				if(p != null){
					if(p.isOut() && p.isFree() && p.isCompatibleWith(type)){
						p.setState(TGConnectingPointAndroid.SELECTED);
						invalidate();
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
			TGCNoteAndroid note = new TGCNoteAndroid(x,y,minWidth,minHeight,maxWidth,maxHeight,this);
			compolist.add(note);
			this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
			break;
		case TGComponentAndroid.CONNECTOR_COMMENT:
			if(p1 == null){
			TGConnectingPointAndroid point = getPointSelected(x,y,TGComponentAndroid.CONNECTOR_COMMENT);
				if(point !=null){
					
					point.setFree(false);
					p1 = point;
					
				}
			}else{
				TGConnectingPointAndroid p2 = getPointSelected(x,y,TGComponentAndroid.CONNECTOR_COMMENT);
				if(!p1.equals(p2) && p2 != null){
						p2.setFree(false);
						TGConnectorCommentAndroid comConnector = new TGConnectorCommentAndroid(minWidth,minHeight,maxWidth,maxHeight,p1, p2,this);
						compolist.add(comConnector);
						p1 = null;
						hideAllConnectingPoints();
						this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
						this.setMode(NORMAL);
					
				}
			}
			break;
		case TGComponentAndroid.AVATARBD_BLOCK:
			AvatarBDBlockAndroid block = new AvatarBDBlockAndroid(x, y,minWidth,minHeight,maxWidth,maxHeight, this);
			compolist.add(block);
			this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
			break;
		case TGComponentAndroid.AVATARBD_CRYPTOBLOCK:
			AvatarBDBlockAndroid cryptoblock = new AvatarBDBlockAndroid(x, y,minWidth,minHeight,maxWidth,maxHeight, this);
			cryptoblock.addCryptoElements();
			compolist.add(cryptoblock);
			this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
			break;
		case TGComponentAndroid.AVATARBD_DATATYPE:
			AvatarBDDataTypeAndroid datatype = new AvatarBDDataTypeAndroid(x, y,minWidth,minHeight,maxWidth,maxHeight, this);
			compolist.add(datatype);
			this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
			break;
		case TGComponentAndroid.AVATARBD_COMPOSITION_CONNECTOR:
			if(p1 == null){
				TGConnectingPointAndroid point = getPointSelected(x,y,TGComponentAndroid.AVATARBD_COMPOSITION_CONNECTOR);
				if(point !=null){
					if(point.isFree()){
						point.setFree(false);
						p1 = point;
					}
				}
			}else{
				TGConnectingPointAndroid p2 = getPointSelected(x,y,TGComponentAndroid.AVATARBD_COMPOSITION_CONNECTOR);
				if(!p1.equals(p2) && p2 != null){
					if(p2.isFree()){
						p2.setFree(false);
						AvatarBDCompositionConnectorAndroid comConnector = new AvatarBDCompositionConnectorAndroid(minWidth,minHeight,maxWidth,maxHeight,p1, p2,this);
						compolist.add(comConnector);
						p1 = null;
						hideAllConnectingPoints();
						this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
						this.setMode(NORMAL);
						
					}
				}
			}
			break;
		case TGComponentAndroid.AVATARBD_PORT_CONNECTOR:
			Log.i("AvatarBDPanel", "creating port connector");
			if(p1 == null){
				TGConnectingPointAndroid point = getPointSelected(x,y,TGComponentAndroid.AVATARBD_PORT_CONNECTOR);
				if(point !=null){
					if(point.isFree()){
						point.setFree(false);
						p1 = point;
					}
				}
//				Log.i("panel", "p1: "+p1.getX()+" / "+p1.getY());
			}else{
				
				TGConnectingPointAndroid p2 = getPointSelected(x,y,TGComponentAndroid.AVATARBD_PORT_CONNECTOR);
			//	Log.i("panel", "p2: "+p2.getX()+" / "+p2.getY());
				if(!p1.equals(p2) && p2 != null){
					if(p2.isFree()){
						p2.setFree(false);
						AvatarBDPortConnectorAndroid portConnector = new AvatarBDPortConnectorAndroid(minWidth,minHeight,maxWidth,maxHeight,p1, p2,this);
						compolist.add(portConnector);
						p1 = null;
						hideAllConnectingPoints();
						this.setCreatedtype(TGComponentAndroid.NOCOMPONENT);
						this.setMode(NORMAL);
					}
				}
			}
			break;
		}
	}
	
	public int selectComponentInRectangle(int x, int y, int width, int height) {
        
        TGComponentAndroid tgc;
        int cpt = 0;
        Iterator iterator = compolist.listIterator();

        while(iterator.hasNext()) {
            tgc = (TGComponentAndroid)(iterator.next());
            if(tgc instanceof TGConnectorAndroid){
            	Log.i("panel", "x: "+tgc.getX()+"y: "+tgc.getY()+"width: "+ tgc.getWidth()+"height: "+ tgc.getHeight());
            	Log.i("panel11", "x: "+x+"y: "+y+"width: "+ width+"height: "+ height);
            }
            if (tgc.areAllInRectangle(x, y, width, height)) {
            	Log.i("panel","tgc ");
                tgc.select(true);
               // tgc.setState(TGState.SELECTED);
                cpt ++;
            } else {
                tgc.select(false);
            //    tgc.setState(TGState.NORMAL);
            }
            
        }
        
        return cpt;
    }
	public boolean isInSelectedRectangle(int x, int y) {
        return GraphicLibAndroid.isInRectangle(x, y, xSEL, ySEL, widthSel, heightSel);
    }
	
	public void moveSelected(int x, int y) {
       // x = Math.min(Math.max(minLimit, x), maxX - widthSel);
       // y = Math.min(Math.max(minLimit, y), maxY - heightSel);
        
        int oldX = xSEL;
        int oldY = ySEL;
        xSEL = x;
        ySEL = y;
        TGComponentAndroid tgc;
        Iterator iterator = compolist.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponentAndroid)(iterator.next());
            if (tgc.isSelected()) {
            	tgc.setCd(tgc.getX()+xSEL-oldX, tgc.getY()+ySEL-oldY);
                //tgc.forceMove(xSel - oldX, ySel - oldY);
            }
        }
        invalidate();
    }
	
	public void endSelectComponents() {
		int nb =0;
		if(xsel !=-1 && xendsel !=-1)
			nb = selectComponentInRectangle(Math.min(xsel, xendsel), Math.min(ysel, yendsel), Math.abs(xsel - xendsel), Math.abs(ysel - yendsel));
        Log.i("panel", "select number: "+nb);
        if(nb !=0){
        	
            mode = SELECTED_COMPONENTS;
            Log.i("panel", "select mode: "+mode);
            showSelectionZone = true;
            xSEL = Math.min(xsel, xendsel);
            ySEL = Math.min(ysel, yendsel);
            widthSel = Math.abs(xsel - xendsel);
            heightSel = Math.abs(ysel - yendsel);
        }else{        	 
                 mode = NORMAL;
                 xsel= -1;
                 ysel = -1;
                 xendsel =-1;
                 yendsel =-1;        
        }
        invalidate();
		
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
	
	public void deleteComponent(){
		if(componentSelected != null){
			
				((TGComponentAndroid) componentSelected).cleanAllPoints();
				if(componentSelected instanceof TGConnectorAndroid){
					((TGConnectorAndroid)componentSelected).p1.setState(TGConnectingPointAndroid.NORMAL);
					((TGConnectorAndroid)componentSelected).p1.setFree(true);
					((TGConnectorAndroid)componentSelected).p2.setState(TGConnectingPointAndroid.NORMAL);
					((TGConnectorAndroid)componentSelected).p2.setFree(true);
				}
			compolist.remove(componentSelected);
			componentSelected = null;
		}
		if(compolist.size()>0){
			for(int i = compolist.size()-1; i>=0;i--){
				if(((TGComponentAndroid)compolist.get(i)).isSelected()){
					((TGComponentAndroid)compolist.get(i)).cleanAllPoints();
					compolist.remove(i);
				}
			}
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
		if(createdtype == TGComponentAndroid.NOCOMPONENT){
			if(p1 != null){
				p1.setState(TGConnectingPointAndroid.NORMAL);
				p1.setFree(true);
			}
			this.hideAllConnectingPoints();
			p1 = null;
		}
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	public LinkedList getCompolist(){
		return compolist;
	}
	
	public void cleanSelection(){
		xsel=-1;
		ysel=-1;
		xendsel =-1; 
		yendsel=-1;
	}
	
	public Vector<String> getAllDataTypes() {
		Vector<String> v = new Vector<String>();
		TGComponentAndroid tgc;
		Iterator iterator = compolist.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponentAndroid)(iterator.next());
			
			if (tgc instanceof AvatarBDDataTypeAndroid) {
				v.add(((AvatarBDDataTypeAndroid)(tgc)).getDataTypeName());
			}
		}
		
		return v;
	}
	
	public TGComponentAndroid getComponentToWhichBelongs(TGConnectingPointAndroid p) {
        TGComponentAndroid tgc1, tgc2;
        Iterator iterator = compolist.listIterator();
        
        while(iterator.hasNext()) {
            tgc1 = (TGComponentAndroid)(iterator.next());
            if(tgc1.belongsToMe(p)){
            	return tgc1;
            }

        }
        return null;
    }
	
	public Vector getListOfAvailableSignals(AvatarBDBlockAndroid _block) {
		int i;
		TGComponentAndroid tgc;
		LinkedList<String> ll;
		AvatarBDPortConnectorAndroid port;
        Iterator iterator = compolist.listIterator();
		ArrayList<String> list = new ArrayList<String>();
		Vector v = new Vector();
		Vector listOfBlock = _block.getSignalList();
		
		if (listOfBlock.size() == 0) {
			return v;
		}
		
		v.addAll(listOfBlock);
        
        while(iterator.hasNext()) {
            tgc = (TGComponentAndroid)(iterator.next());
            if (tgc instanceof AvatarBDPortConnectorAndroid) {
				port = (AvatarBDPortConnectorAndroid)tgc;
				if (port.getAvatarBDBlock1() == _block) {
					ll = port.getListOfSignalsOrigin();
					removeSignals(v, ll);
				}
				if (port.getAvatarBDBlock2() == _block) {
					ll = port.getListOfSignalsDestination();
					removeSignals(v, ll);
				}
			}
		}
		
		return v;
	}
	
	private void removeSignals(Vector v, LinkedList<String> list) {
		int i;
		AvatarSignal as;
		for(String s: list) {
			for(i=0; i<v.size(); i++) {
				as = (AvatarSignal)(v.get(i));
				if (as.toString().compareTo(s) == 0) {
					v.removeElementAt(i);
					break;
				}
			}
		}
	}
}
