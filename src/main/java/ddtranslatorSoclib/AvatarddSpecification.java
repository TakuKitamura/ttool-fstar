/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * Daniela Genius, Lip6, UMR 7606 
 * 
 * ludovic.apvrille AT enst.fr
 * daniela.genius@lip6.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */





/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;

import java.util.LinkedList;
import java.util.List;
import ui.tmldd.TMLArchiHWANode;//DG 23.08.
public class AvatarddSpecification{
    private List<AvatarComponent> components;
    private List<AvatarConnector> connectors;
    private List<AvatarMappedObject> mappedObjects;
		
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
    int nb_target;// = 6; 
    int nb_mwmr_segments = 0;
	
    public AvatarddSpecification( List<AvatarComponent> _components, List<AvatarConnector> _connectors, List<AvatarMappedObject> _mappedObjects, int _nb_target, int _nb_init){
		components = _components ;
		connectors = _connectors ;
		mappedObjects = _mappedObjects ;
        nb_target = _nb_target;
        nb_init = _nb_init;
	}
    
    public List<AvatarComponent> getComponents(){
      return components;
    }

    public List<AvatarConnector> getConnectors(){
      return connectors;
    }

    public List<AvatarMappedObject> getMappedObjects(){
      return mappedObjects;
    }

    public List<AvatarTask> getAllMappedTask(){
    	List<AvatarTask> tasks = new LinkedList<AvatarTask>();
      
    	for (AvatarMappedObject task : mappedObjects ) {
    		if (task instanceof AvatarTask) {
    			tasks.add((AvatarTask)task);
    		}
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
      
    public LinkedList<AvatarTTY> getAllTTY(){
	//int i=0;
      LinkedList<AvatarTTY> ttys = new LinkedList<AvatarTTY>();
      for (AvatarComponent tty : components )
        {
	    if (tty instanceof AvatarTTY){ 		
		ttys.add((AvatarTTY)tty);
		nb_target++;
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
   
    public List<AvatarRAM> getAllRAM(){
	//int i=0;
    	List<AvatarRAM> rams = new LinkedList<AvatarRAM>();
      
    	for (AvatarComponent ram : components ) {
    		if (ram instanceof AvatarRAM){  
		    rams.add((AvatarRAM)ram);
		    nb_target++;	
    		}
    	}    
      
    	return rams;
    }

    public List<AvatarBus> getAllBus(){
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
	    
	    if (crossbar instanceof AvatarCrossbar){
		
		crossbars.add((AvatarCrossbar)crossbar);
		nb_target++;
	    }

        }
      return crossbars;
    }

    /*     public LinkedList<AvatarBridge> getAllBridge(){
      LinkedList<AvatarBridge> bridges = new LinkedList<AvatarBridge>();   
      for (AvatarComponent bridge : components )
        {
	    if (bridges instanceof AvatarBridge){	
		bridges.add((AvatarBridge)bridge);	
	    }
        }
      return bridges;
      }*/

  
   
   public int getNbClusters(){      
       return getAllCrossbar().size();
    }


      public List<AvatarCoproMWMR> getAllCoproMWMR(){
      List<AvatarCoproMWMR> copros = new LinkedList<AvatarCoproMWMR>();
      for (AvatarComponent copro : components )
        {
	    if (copro instanceof AvatarCoproMWMR){
		
            copros.add((AvatarCoproMWMR)copro);
	    nb_target++;//DG 9.7. attention not all are target
	    }
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
