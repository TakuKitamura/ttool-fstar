/* * @version 1.0 07/07/2015
 * @author  Julien Henon, Daniela Genius */


package ddtranslatorSoclib;
import java.util.*;

public class AvatarROM extends AvatarComponent{

    private String memoryName;
    private int index;
    private int dataSize;




    public AvatarROM(String _memoryName,int _index, int _dataSize)  {

        memoryName = _memoryName;
        index =  _index;
        dataSize = _dataSize;

        nbConnectingPoint = 16;
        connectingPoint = new ConnectingPoints[16];


    }

    String getMemoryName(){
        return memoryName;
    }

    int getIndex(){
        return index;
    }

    int getDataSize(){
        return dataSize;
    }


}
