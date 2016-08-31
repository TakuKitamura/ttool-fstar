/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;
import java.util.*;

public class AvatarddSpecification{
	
    private LinkedList<AvatarComponent> components;
    private LinkedList<AvatarMappedObject> mappedObjects;
		
    private int nb_init = 0;

/*there are seven targets which are fixed but invisible to the user of the TTool deployment diagram) :

Targets on RAM0 :
the text segment (target 0)
the reset segment (target 1)
the data segment (target 2)

Other targets :
the simhelper segment (target 3)
the icu segment (target 4)
the timer segment (target 5)
the fdt segment (target 6)

There always is a RAM0, a TTY and an interconnect (Bus or VGMN or crossbar) otherwise an error message is printed
*/

/* initialization of counters, there are at least 6 targets */
    int nb_target = 6; 
    int nb_mwmr_segments = 0;
	
    public AvatarddSpecification(LinkedList<AvatarComponent> _components,  LinkedList<AvatarMappedObject> _mappedObjects, int _nb_target, int _nb_init){
	components = _components ;
	mappedObjects = _mappedObjects ;
        nb_target = _nb_target;
        nb_init = _nb_init;
	}
    
    public LinkedList<AvatarComponent> getComponents(){
      return components;
    }

    public LinkedList<AvatarMappedObject> getMappedObjects(){
      return mappedObjects;
    }

    public LinkedList<AvatarTask> getAllMappedTask(){
      LinkedList<AvatarTask> tasks = new LinkedList<AvatarTask>();
      for (AvatarMappedObject task : mappedObjects )
        {
          if (task instanceof AvatarTask)
            tasks.add((AvatarTask)task);
        }
      return tasks;
    }
    
   public LinkedList<AvatarChannel> getAllMappedChannels(){
      LinkedList<AvatarChannel> channels = new LinkedList<AvatarChannel>();
      for (AvatarMappedObject channel : mappedObjects )
        {
          if (channel instanceof AvatarChannel)
	      channels.add((AvatarChannel)channel);
        }
      return channels;
    }
    
    /*   public LinkedList<AvatarConnector> getAllConnectors(){
      LinkedList<AvatarConnector> connectors = new LinkedList<AvatarConnector>();
      for (AvatarComponent connector: components )
        {
          if (connector instanceof AvatarConnector)
	      connectors.add((AvatarConnector)connector);
        }
      return connectors;
      }*/

    public LinkedList<AvatarTTY> getAllTTY(){
	int i=0;
      LinkedList<AvatarTTY> ttys = new LinkedList<AvatarTTY>();
      for (AvatarComponent tty : components )
        {
	    if (tty instanceof AvatarTTY){ 
		//tty.setNo_tty(i);i++;
		ttys.add((AvatarTTY)tty);
	    }
        }
      return ttys;
    }

    public LinkedList<AvatarCPU> getAllCPU(){
      LinkedList<AvatarCPU> cpus = new LinkedList<AvatarCPU>();  
      for (AvatarComponent cpu : components )
        {
	    if (cpu instanceof AvatarCPU){
		cpus.add((AvatarCPU)cpu);		
	    }
        }     
      return cpus;
    }
   
    public LinkedList<AvatarRAM> getAllRAM(){
	int i=0;
      LinkedList<AvatarRAM> rams = new LinkedList<AvatarRAM>();
      for (AvatarComponent ram : components )
        {
	    if (ram instanceof AvatarRAM){  
	
		rams.add((AvatarRAM)ram);	
		
	    }
        }    
      return rams;
    }

    public LinkedList<AvatarBus> getAllBus(){
      LinkedList<AvatarBus> buss = new LinkedList<AvatarBus>();
      for (AvatarComponent bus : components )
        {
          if (bus instanceof AvatarBus)
            buss.add((AvatarBus)bus);
        }
      return buss;
    }

    public LinkedList<AvatarVgmn> getAllVgmn(){
      LinkedList<AvatarVgmn> vgmns = new LinkedList<AvatarVgmn>();
      for (AvatarComponent vgmn : components )
        {
	    if (vgmn instanceof AvatarVgmn){		
            vgmns.add((AvatarVgmn)vgmn);
	   
	    }
        }
      return vgmns;
    }

    public LinkedList<AvatarCrossbar> getAllCrossbar(){
      LinkedList<AvatarCrossbar> crossbars = new LinkedList<AvatarCrossbar>();
      for (AvatarComponent crossbar : components )
        {
	    //Currently, at least one crossbar -> clustered
	    if (crossbar instanceof AvatarCrossbar){
		System.out.println("Clustered Interconnect found");
		crossbars.add((AvatarCrossbar)crossbar);	    
	    }

        }
      return crossbars;
    }
  
   //Currently, we define 1 crossbar = 1 cluster
   public int getNbClusters(){      
       return getAllCrossbar().size();
    }

    public LinkedList<AvatarCoproMWMR> getAllCoproMWMR(){
      LinkedList<AvatarCoproMWMR> copros = new LinkedList<AvatarCoproMWMR>();
      for (AvatarComponent copro : components )
        {
          if (copro instanceof AvatarCoproMWMR)
            copros.add((AvatarCoproMWMR)copro);

        }
      return copros;
    }

    public int getNbCPU(){
      return (getAllCPU()).size();
    }

    public int getNbTTY(){
      return (getAllTTY()).size();
    }

    public int getNbRAM(){
      return (getAllRAM()).size();
    }

    public int getNbBus(){
      return (getAllBus()).size();
    } 
    
    public int getNbVgmn(){
      return (getAllVgmn()).size();
    }

    public int getNbCrossbar(){
      return (getAllCrossbar()).size();
    }

    public int getNbCoproMWMR(){
      return (getAllCoproMWMR()).size();
    }

    // for construction of the central interconnect

    public int getNb_init(){
      return nb_init ;
    }

    public int getNb_target(){
    return nb_target;
    }

  // etc .....

}
