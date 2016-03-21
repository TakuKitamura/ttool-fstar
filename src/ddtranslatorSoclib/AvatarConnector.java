/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */


package ddtranslatorSoclib;
import java.util.*;

public class AvatarConnector{
    
    private AvatarConnectingPoint connectingPoint1;
    private AvatarConnectingPoint connectingPoint2;


    public AvatarConnector(AvatarConnectingPoint _connectingPoint1,AvatarConnectingPoint _connectingPoint2){
      
      connectingPoint1 = _connectingPoint1;
      connectingPoint2 = _connectingPoint2;
    }

    AvatarConnectingPoint getconectingPoint1(){
      return connectingPoint1;
    }

    AvatarConnectingPoint getconectingPoint2(){
      return connectingPoint2;
    }

}