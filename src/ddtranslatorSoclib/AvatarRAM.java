/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */


package ddtranslatorSoclib;
import java.util.*;

public class AvatarRAM extends AvatarComponent{
    
    private String memoryName ;
    private int index;
    private int dataSize; 

    public int no_ram;
    public int no_target;

    LinkedList<AvatarChannel> channelMapped ;

    public AvatarRAM(String _memoryName,int _index,int _dataSize,int _no_ram)  {
      memoryName = _memoryName;
      index = _index;
      dataSize = _dataSize;
      no_ram = _no_ram;
     
      channelMapped =  new LinkedList<AvatarChannel>();
    }

    public String getMemoryName(){
	return memoryName;
    }

    public int getNo_ram(){
      return no_ram;
    }

    public int getNo_target(){
      return no_target;
    }

    public void setNo_ram(int _no_ram){
      no_ram = _no_ram;
    }
    /* the target number is set by the topcell generator*/
    public void setNo_target(int _no_target){
      no_target = _no_target;
    }
    public int getIndex(){
	return index;
    }

    public int getDataSize(){
	return dataSize;
    }
    
    public LinkedList<AvatarChannel> getChannels(){
      return  channelMapped;
    }

    public void addChannel(AvatarChannel avcl){
      channelMapped.add(avcl);
    }
}
