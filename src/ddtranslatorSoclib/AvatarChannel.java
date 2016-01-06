/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;

public class AvatarChannel extends AvatarMappedObject{

    private AvatarRAM avatarRAMReference; 
    private String referenceDiagram ;
	private String channelName ;

    public AvatarChannel(String _referenceDiagram,  String _channelName, AvatarRAM _avatarRAMReference ){
      referenceDiagram =  _referenceDiagram;
      channelName = _channelName;
      avatarRAMReference = _avatarRAMReference;
    }

    public AvatarRAM getAvatarRAMReference(){
      return  avatarRAMReference;
    }

    public int getRAMNo(){
      return avatarRAMReference.getNo_ram();
    }

    public String getReferenceDiagram(){
      return referenceDiagram;
    }
    
    public String  getChannelName(){
      return channelName;
    } 
}