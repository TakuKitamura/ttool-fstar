/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;
import java.util.*;

public class AvatarConnectingPoint{
    
    AvatarConnector ownerConnector ;
    AvatarComponent ownerComponent;
    
    public AvatarConnectingPoint( AvatarComponent _ownerComponent)
    {
      ownerComponent = _ownerComponent;
    }

    AvatarConnector getConnector(){
      return ownerConnector;
    }

    public AvatarComponent getComponent(){//DG
      return ownerComponent;
    }

    void setConnector(AvatarConnector _connector){
      ownerConnector = _connector;
    }
    
    boolean ConnectingPointIsFree(){
      return  ownerConnector == null;
    }
    
}