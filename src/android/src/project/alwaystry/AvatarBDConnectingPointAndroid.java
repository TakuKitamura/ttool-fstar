package project.alwaystry;


public class AvatarBDConnectingPointAndroid extends TGConnectingPointAndroid{
	
	public AvatarBDConnectingPointAndroid(CDElementAndroid _container, int _x, int _y, boolean _in, boolean _out,double _w, double _h){
		super(_container, _x, _y, _in, _out, _w, _h);
	}
	
	public boolean isCompatibleWith(int type) {
        if (type == TGComponentAndroid.AVATARBD_COMPOSITION_CONNECTOR) {
            return true;
        }
		if (type == TGComponentAndroid.AVATARBD_PORT_CONNECTOR) {
            return true;
        }
        return false;
    }
	
}
