package project.alwaystry;

public class TGConnectingPointCommentAndroid extends TGConnectingPointAndroid{

	public TGConnectingPointCommentAndroid(CDElementAndroid _container, int _x, int _y, boolean _in, boolean _out,double _w, double _h){
		super(_container, _x, _y, _in, _out, _w, _h);
	}
	public boolean isCompatibleWith(int type) {
        if (type == TGComponentAndroid.CONNECTOR_COMMENT) {
            return true;
        }
        return false;
    }
}
