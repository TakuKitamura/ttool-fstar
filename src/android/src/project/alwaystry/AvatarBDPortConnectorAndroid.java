package project.alwaystry;

import java.util.LinkedList;
import java.util.Vector;

import copyfromJAVAsource.AvatarSignal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AvatarBDPortConnectorAndroid extends TGConnectorAndroid{

	private Paint paint;
	private LinkedList internalpoints;
	protected LinkedList<String> inSignalsAtOrigin;
	protected LinkedList<String> outSignalsAtDestination;
	
	protected LinkedList<String> inSignalsAtDestination;
	protected LinkedList<String> outSignalsAtOrigin;
	
	protected boolean asynchronous;
	protected int sizeOfFIFO;
	protected boolean blockingFIFO;
	
	public AvatarBDPortConnectorAndroid(int _minWidth, int _minHeight,int _maxWidth,int _maxHeight,TGConnectingPointAndroid p1,TGConnectingPointAndroid p2,View panel){
		super(_minWidth, _minHeight, _maxWidth, _maxHeight, p1, p2, panel);
		
		paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		
		type = TGComponentAndroid.AVATARBD_PORT_CONNECTOR;
		
		inSignalsAtOrigin = new LinkedList<String>();
		inSignalsAtDestination = new LinkedList<String>();
		outSignalsAtOrigin = new LinkedList<String>();
		outSignalsAtDestination = new LinkedList<String>();
	}
	
	public void internalDrawing(Canvas canvas){
		
		
		if(p1.isFree() || p2.isFree()){
			p1.setState(TGConnectingPointAndroid.NORMAL);
			p2.setState(TGConnectingPointAndroid.NORMAL);
			((AvatarBDPanelAndroid)panel).getCompolist().remove(this);
			return;
		}
		
		TextPaint tPaint = new TextPaint();
		tPaint.setAntiAlias(true);
		tPaint.setTextAlign(Paint.Align.LEFT);
		
		if(selected){
			paint.setColor(Color.RED);
			tPaint.setColor(Color.RED);
		}else{
			paint.setColor(Color.BLACK);
			tPaint.setColor(Color.BLACK);
		}
		
		if(movingHead){
			paint.setColor(Color.MAGENTA);
		}
		Log.i("portconnector", "internaldrawing");
		Log.i("portconnector", "p1: "+p1.isFree()+"p2 :" +p2.isFree());
		canvas.drawRect(p1.getX()-p1.getWidth()/2, p1.getY()-p1.getHeight()/2, p1.getX()+p1.getWidth()/2, p1.getY()+p1.getHeight()/2, paint);
		canvas.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), paint);
		canvas.drawRect(p2.getX()-p2.getWidth()/2, p2.getY()-p2.getHeight()/2, p2.getX()+p2.getWidth()/2, p2.getY()+p2.getHeight()/2, paint);
	
		int decX = 6;
		int decY = 20;

		int h = - decY;
		int step = 10;
		int w;
		String s;

		// Signals at origin
		if (inSignalsAtOrigin.size() > 0) {
			
			for(String iso: inSignalsAtOrigin) {
				h += step;
				s = getShortName(iso);
				if (p1.getX() <= p2.getX()) {
					canvas.drawText(s, p1.getX() + decX, p1.getY() + h, tPaint);
				} else {
					w = s.length()*8;
					canvas.drawText(s, p1.getX() - decX - w, p1.getY() + h, tPaint);
				}
			}
		}
		if (outSignalsAtOrigin.size() > 0) {
			
			for(String oso: outSignalsAtOrigin) {
				h += step;
				s = getShortName(oso);
				if (p1.getX() <= p2.getX()) {
					canvas.drawText(s, p1.getX() + decX, p1.getY() + h,tPaint);
				} else {
					w = s.length()*8;
					canvas.drawText(s, p1.getX() - decX - w, p1.getY() + h,tPaint);
				}
			}
		}
		// Signals at destination
		h = - decY;
		if (outSignalsAtDestination.size() > 0) {
			
			for(String osd: outSignalsAtDestination) {
				h += step;
				s = getShortName(osd);
				if (p1.getX() > p2.getX()) {
					canvas.drawText(s, p2.getX() + decX, p2.getY() + h,tPaint);
				} else {
					w = s.length()*8;
					canvas.drawText(s, p2.getX() - decX - w, p2.getY() + h,tPaint);
				}
			}
		}
		if (inSignalsAtDestination.size() > 0) {
			
			for(String isd: inSignalsAtDestination) {
				h += step;
				s = getShortName(isd);
				if (p1.getX() > p2.getX()) {
					canvas.drawText(s, p2.getX() + decX, p2.getY() + h,tPaint);
				} else {
					w = s.length()*8;
					canvas.drawText(s, p2.getX() - decX - w, p2.getY() + h,tPaint);
				}
			}
		}
        
	}

	// remove the parameters in the name of a signal
	public String getShortName(String _s) {
		int index = _s.indexOf('(');
		if (index == -1) {
			return _s;
		}
		
		return _s.substring(0, index).trim();
	}
	public LinkedList getInternalpoints() {
		return internalpoints;
	}
	public void setInternalpoints(LinkedList internalpoints) {
		this.internalpoints = internalpoints;
	}

	public AvatarBDBlockAndroid getAvatarBDBlock1() {
		return (AvatarBDBlockAndroid)(((AvatarBDPanelAndroid)panel).getComponentToWhichBelongs(p1));
	}
	
	public AvatarBDBlockAndroid getAvatarBDBlock2() {
		return (AvatarBDBlockAndroid)(((AvatarBDPanelAndroid)panel).getComponentToWhichBelongs(p2));
	}
	
	public static String makeSignalAssociation(AvatarBDBlockAndroid _block1, AvatarSignal _as1, AvatarBDBlockAndroid _block2, AvatarSignal _as2) {
		String s = _block1.getName() + "." + _as1.toBasicString();
		if (_as1.getInOut() == AvatarSignal.OUT) {
			s += " -> ";
		} else {
			s += " <- ";
		}
		s += _block2.getName() + "." + _as2.toBasicString();
		return s;
	}
	
	public Vector getAssociationSignals() {
		AvatarBDBlockAndroid block1 = getAvatarBDBlock1();
		AvatarBDBlockAndroid block2 = getAvatarBDBlock2();
		
		int i;
		Vector v = new Vector();
		String s;
		
		for(i=0; i<outSignalsAtOrigin.size(); i++) {
			try {
				s = makeSignalAssociation(block1, block1.getAvatarSignalFromFullName(outSignalsAtOrigin.get(i)), block2, block2.getAvatarSignalFromFullName(inSignalsAtDestination.get(i)));
				v.add(s);
			} catch (Exception e) {
				// Probably a signal has been removed
			}
		}
		
		for(i=0; i<inSignalsAtOrigin.size(); i++) {
			try {
				s = makeSignalAssociation(block1, block1.getAvatarSignalFromFullName(inSignalsAtOrigin.get(i)), block2, block2.getAvatarSignalFromFullName(outSignalsAtDestination.get(i)));
				v.add(s);
			} catch (Exception e) {
				// Probably a signal has been removed
			}
		}
		
		return v;
	}
	
	protected boolean editOndoubleClick(int _x, int _y) {
		// TODO Auto-generated method stub
		AvatarBDBlockAndroid block1 = getAvatarBDBlock1();
		AvatarBDBlockAndroid block2 = getAvatarBDBlock2();
		
		Vector available1 = block1.getListOfAvailableSignals();
		Vector available2 = block2.getListOfAvailableSignals();
		
		String[] availableSignals1 = new String[available1.size()];
		String[] availableSignals2 = new String[available2.size()];
		
		for(int i=0;i<available1.size();i++){
			availableSignals1[i] = ((AvatarSignal)available1.elementAt(i)).toString();
		}
		
		for(int i=0;i<available2.size();i++){
			availableSignals2[i] = ((AvatarSignal)available2.elementAt(i)).toString();
		}
		
		String[] block1Signals = new String[block1.mySignals.size()];
		String[] block2Signals = new String[block2.mySignals.size()];
		
		for(int i=0;i<block1.mySignals.size();i++){
			block1Signals[i] = ((AvatarSignal)block1.mySignals.elementAt(i)).toString();
		}
		
		for(int i=0;i<block2.mySignals.size();i++){
			block2Signals[i] = ((AvatarSignal)block2.mySignals.elementAt(i)).toString();
		}
		
		Vector v = getAssociationSignals();
		String[] asarray = new String[v.size()];
		for(int i= 0;i<v.size();i++){
			asarray[i]=(String)v.elementAt(i);
		}

		Bundle associabundle = new Bundle();
		associabundle.putStringArray("associationSignals", asarray);
		associabundle.putStringArray("availableSignals1", availableSignals1);
		associabundle.putStringArray("availableSignals2", availableSignals2);
		associabundle.putString("block1", block1.getName());
		associabundle.putString("block2", block2.getName());
		associabundle.putStringArray("block1signals",block1Signals);
		associabundle.putStringArray("block2signals",block2Signals);
		Intent intent = new Intent().setClass(panel.getContext(),SignalAssociationActivity.class);	
		intent.putExtras(associabundle);
		((Activity) panel.getContext()).startActivityForResult(intent, AlwaystryActivity.SIGNALASSOCIATION);
		
		return false;
	}

	public LinkedList<String> getListOfSignalsOrigin() {
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(inSignalsAtOrigin);
		list.addAll(outSignalsAtOrigin);
		return list;
	}
	
	public LinkedList<String> getListOfSignalsDestination() {
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(outSignalsAtDestination);
		list.addAll(inSignalsAtDestination);
		return list;
	}
	
	public String getFirstSignalOfSignalAssociation(String _assoc) {
		int index0 = _assoc.indexOf(".");
		
		
		if (index0 == -1) {
			return null;
		}
		
		int index1 = _assoc.indexOf("->");
		int index2 = _assoc.indexOf("<-");
		
		index1 = Math.max(index1, index2);
		if (index1 == -1) {
			return null;
		}
		
		return _assoc.substring(index0+1, index1).trim();
	}
	
	public String getSecondSignalOfSignalAssociation(String _assoc) {
		int index0 = _assoc.indexOf("->");
		int index1 = _assoc.indexOf("<-");
		
		if ((index0 == -1) && (index1 == -1)) {
			return null;
		}
		
		index0 = Math.max(index0, index1);
		_assoc = _assoc.substring(index0+2, _assoc.length());
		
		index0 = _assoc.indexOf(".");
		
		if (index0 == -1) {
			return null;
		}
		
		return _assoc.substring(index0+1, _assoc.length()).trim();
	}
	
	public void setSignalAssociation(boolean asychro,boolean blocking,int FIFOsize,String[] assocs){
		AvatarBDBlockAndroid block1 = getAvatarBDBlock1();
		AvatarBDBlockAndroid block2 = getAvatarBDBlock2();
		
		inSignalsAtOrigin.clear();
		inSignalsAtDestination.clear();
		outSignalsAtOrigin.clear();
		outSignalsAtDestination.clear();
		
		String assoc;
		AvatarSignal as1, as2;
		int index;
		for(int i=0;i<assocs.length;i++){
			assoc = assocs[i];
			as1 = block1.getSignalNameBySignalDef(getFirstSignalOfSignalAssociation(assoc));
			as2 = block2.getSignalNameBySignalDef(getSecondSignalOfSignalAssociation(assoc));
			
			if((as1 != null) && (as2 != null)){
				index = assoc.indexOf("->");
				if (index > -1) {
					outSignalsAtOrigin.add(as1.toString());
					inSignalsAtDestination.add(as2.toString());
				} else {
					inSignalsAtOrigin.add(as1.toString());
					outSignalsAtDestination.add(as2.toString());
				}
			}
		}
		
		asynchronous = asychro;
		blockingFIFO = blocking;
		sizeOfFIFO = FIFOsize;
		
		
	}
	
}
