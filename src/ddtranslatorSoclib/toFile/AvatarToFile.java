/* author: v1.0 Julien HENON 2015 */

package ddtranslatorSoclib.toFile;

import ui.*;
import ui.avatardd.*;
import ui.avatarbd.*;
import java.util.*;
import java.io.*;
import java.io.Writer;
import java.io.PrintWriter;
import avatartranslator.*;

public class AvatarToFile{
   
    final String path = "/users/enseig/genius/TURTLE/src/ddtranslatorSoclib/toFile";
    LinkedList componentList;
    AvatarSpecification avp;
    PrintWriter writerDeploy;
    PrintWriter writerMapping;
    PrintWriter writerAvatarSpec;

	public AvatarToFile(LinkedList _componentList, AvatarSpecification _avp){
      componentList = _componentList ;
	avp = _avp;
    //file = new File("file-deployment-Panel.txt");
    }

    public void extracParamToFile(){
      try{
        writerDeploy = new PrintWriter("file-deployment-Panel");
      }catch(IOException e){
        System.err.println("error opening file");
      }
       try{
        writerMapping = new PrintWriter("file-mapping-deployment-Panel");
      }catch(IOException e){
        System.err.println("error opening file");
      }
       try{
        writerAvatarSpec = new PrintWriter("file-AvatarSpec");
      }catch(IOException e){
        System.err.println("error opening file");
      }



      try{
        TGComponent dp = null;

        ListIterator iterator = componentList.listIterator();
        while(iterator.hasNext()) {
          dp = (TGComponent)iterator.next();
          if (dp instanceof ADDCPUNode){
            writerDeploy.println(((ADDCPUNode)dp).getName() );
            writerDeploy.println(((ADDCPUNode)dp).getAttributesToFile() );
            Vector tasks = ((ADDCPUNode)dp).getArtifactList();
            for (int i = 0 ; i < tasks.size() ; i ++){
              ADDBlockArtifact task = (ADDBlockArtifact)tasks.get(i);
              writerMapping.println(task.getTaskName() + " " + ((ADDCPUNode)dp).getNodeName());
            } 

          }else if(dp instanceof ADDTTYNode){
            writerDeploy.println(((ADDTTYNode)dp).getName() );
            writerDeploy.println(((ADDTTYNode)dp).getAttributesToFile() );
          }else if(dp instanceof ADDBridgeNode){
            writerDeploy.println(((ADDBridgeNode)dp).getName() );
            writerDeploy.println(((ADDBridgeNode)dp).getAttributesToFile() );
          }else if(dp instanceof ADDBusNode){
            writerDeploy.println(((ADDBusNode)dp).getName() );
            writerDeploy.println(((ADDBusNode)dp).getAttributesToFile() );
	  } else if(dp instanceof ADDVgmnNode){
            writerDeploy.println(((ADDVgmnNode)dp).getName() );
            writerDeploy.println(((ADDVgmnNode)dp).getAttributesToFile() );
	  } else if(dp instanceof ADDCrossbarNode){
            writerDeploy.println(((ADDCrossbarNode)dp).getName() );
            writerDeploy.println(((ADDCrossbarNode)dp).getAttributesToFile() );
          }else if(dp instanceof ADDDMANode){
            writerDeploy.println(((ADDDMANode)dp).getName() );
            writerDeploy.println(((ADDDMANode)dp).getAttributesToFile() );
          }else if(dp instanceof ADDICUNode){
            writerDeploy.println(((ADDICUNode)dp).getName() );
            writerDeploy.println(((ADDICUNode)dp).getAttributesToFile() );
          }else if(dp instanceof ADDTimerNode){
            writerDeploy.println(((ADDTimerNode)dp).getName() );
            writerDeploy.println(((ADDTimerNode)dp).getAttributesToFile() );
          }else if(dp instanceof ADDCoproMWMRNode){
            writerDeploy.println(((ADDCoproMWMRNode)dp).getName() );
            writerDeploy.println(((ADDCoproMWMRNode)dp).getAttributesToFile() );
          }else if(dp instanceof ADDMemoryNode){
            writerDeploy.println(((ADDMemoryNode)dp).getName() );
            writerDeploy.println(((ADDMemoryNode)dp).getAttributesToFile() );
            if (dp instanceof ADDRAMNode){ 
              Vector channels = (( ADDRAMNode)dp).getArtifactList();
              for(int i=0 ; i < channels.size() ; i++ ) {
                ADDChannelArtifact c = (ADDChannelArtifact)channels.get(i);
                writerMapping.println(c.getChannelName() + " " + ((ADDRAMNode)dp).getNodeName());
              } 
            }
          }else if(dp instanceof ADDArtifact){
            writerDeploy.println(((ADDArtifact)dp).getName() );
            writerDeploy.println(((ADDArtifact)dp).getAttributesToFile() );
          }
        }
	writerAvatarSpec.println(avp.toString());
      }catch(Exception e){
        System.err.println("Extrac error");
      }
      System.err.println("Extrac done !");
      writerDeploy.close();
      writerMapping.close();
      writerAvatarSpec.close();
      
    }
}
