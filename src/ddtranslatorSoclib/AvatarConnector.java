/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */


package ddtranslatorSoclib;
import java.util.*;

public class AvatarConnector{
    
    private AvatarConnectingPoint connectingPoint1;
    private AvatarConnectingPoint connectingPoint2;
    private int  monitored;

    public AvatarConnector(AvatarConnectingPoint _connectingPoint1,AvatarConnectingPoint _connectingPoint2, int _monitored){
      
      connectingPoint1 = _connectingPoint1;
      connectingPoint2 = _connectingPoint2; 
      monitored = _monitored;
    }

    AvatarConnectingPoint getconectingPoint1(){
      return connectingPoint1;
    }

    AvatarConnectingPoint getconectingPoint2(){
      return connectingPoint2;
    }

    public int getMonitored(){
      return monitored;
    } 

}