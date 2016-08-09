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
    public int no_target_local;
    public int no_cluster;
    public int monitored;

    LinkedList<AvatarChannel> channelMapped ;
    //DG 4.4. we add a field cluster_index, for the time when a cluster will have more than one RAM
    public AvatarRAM(String _memoryName, int _index, int _dataSize, int _no_ram, int _no_cluster, int _monitored)  {
      memoryName = _memoryName;
      index = _index;
      dataSize = _dataSize;
      no_ram = _no_ram;
      no_cluster=_no_cluster;
      monitored=_monitored;
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

    public int getNo_cluster(){
      return no_cluster;
    } 

    public void setNo_ram(int _no_ram){
      no_ram = _no_ram;
    }
    /* the target number is set by the topcell generator*/
    public void setNo_target(int _no_target){
      no_target = _no_target;
    }

    public void setNo_cluster(int _no_cluster){
      no_cluster = _no_cluster;
    }

    public int getIndex(){
	return index;
    }

    public int getDataSize(){
	return dataSize;
    }

    public int getMonitored(){
	return monitored;
    }
    public void setMonitored(int _monitored){
	monitored = _monitored;
    }
    
    public LinkedList<AvatarChannel> getChannels(){
      return  channelMapped;
    }

    public void addChannel(AvatarChannel avcl){
      channelMapped.add(avcl);
    }
}
