package project.alwaystry;

import android.view.View;

public class AvatarBDConnectingPointAndroid {
	private boolean in;
	private boolean out;
	private int x;
	private int y;
	private int width = 10;
	private int height = 10;
	private double w ;
	private double h ;
	private View container;
	
	public AvatarBDConnectingPointAndroid(View _container, int _x, int _y, boolean _in, boolean _out, double _w, double _h){
		x = _x;
		y = _y;
		in = _in;
		out = _out;
		w = _w;
		h = _h;
		container = _container;
	}
	
	public int getX(){
		return x +(int)((container.getWidth()-14)*w);
	}
	
	public int getY(){
		return y +(int)((container.getHeight()-14)*h);
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
}
