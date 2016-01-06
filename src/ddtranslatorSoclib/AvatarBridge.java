/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;
import java.util.*;

public class AvatarBridge extends AvatarComponent{

    private String bridgeName;
    private int nbConnectingPoint = 16;
    
    AvatarConnectingPoint[] connectingPoints = new AvatarConnectingPoint[16];

    public AvatarBridge(String _bridgeName)  {        
      bridgeName = _bridgeName; 
    }
    
    AvatarConnectingPoint[] getAvatarConnectingPoints(){
	return connectingPoints;
    }
    int getnbConnectingPoint(){
	return nbConnectingPoint;
    }

    void setConnectingPoint(int indexConnectingPoint, AvatarConnector connector){
	return;
    }
} 
