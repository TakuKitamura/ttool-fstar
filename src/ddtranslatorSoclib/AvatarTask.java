/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */


package ddtranslatorSoclib;
import java.util.*;

public class AvatarTask extends AvatarMappedObject{

    private AvatarCPU avatarCPUReference;
     private String taskName ;
	 private String referenceTaskName;

    public AvatarTask(String _taskName , String _referenceTaskName, AvatarCPU _avatarCPUReference ){

      taskName = _taskName;
      referenceTaskName = _referenceTaskName;
      avatarCPUReference = _avatarCPUReference;   
    }

    public String getTaskName(){
      return taskName;
    }

    public String getReferenceTaskName(){
      return referenceTaskName;
    }

    public AvatarCPU getAvatarCPUReference(){
      return avatarCPUReference;
    } 

    public int getCPUNo(){
      return avatarCPUReference.getNo_proc();
    }
}