
/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
  Daniela Genius, Lip6, UMR 7606 

  ludovic.apvrille AT enst.fr
  daniela.genius@lip6.fr

  This software is a computer program whose purpose is to allow the 
  edition of TURTLE analysis, design and deployment diagrams, to 
  allow the generation of RT-LOTOS or Java code from this diagram, 
  and at last to allow the analysis of formal validation traces 
  obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
  from INRIA Rhone-Alpes.

  This software is governed by the CeCILL  license under French law and
  abiding by the rules of distribution of free software.  You can  use, 
  modify and/ or redistribute the software under the terms of the CeCILL
  license as circulated by CEA, CNRS and INRIA at the following URL
  "http://www.cecill.info". 

  As a counterpart to the access to the source code and  rights to copy,
  modify and redistribute granted by the license, users are provided only
  with a limited warranty  and the software's author,  the holder of the
  economic rights,  and the successive licensors  have only  limited
  liability. 

  In this respect, the user's attention is drawn to the risks associated
  with loading,  using,  modifying and/or developing or reproducing the
  software by the user in light of its specific status of free software,
  that may mean  that it is complicated to manipulate,  and  that  also
  therefore means  that it is reserved for developers  and  experienced
  professionals having in-depth computer knowledge. Users are therefore
  encouraged to load and test the software's suitability as regards their
  requirements in conditions enabling the security of their systems and/or 
  data to be ensured and,  more generally, to use and operate it in the 
  same conditions as regards security. 

  The fact that you are presently reading this means that you have had
  knowledge of the CeCILL license and that you accept its terms.
*/
/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 
	    v2.1 Daniela GENIUS, summer 2016*/

package ddtranslatorSoclib.toTopCell;
import java.util.*;
import ddtranslatorSoclib.*;

public class Declaration {
    
    private static String CR = "\n";
    private static String CR2 = "\n\n";   
       
    public static String generateName(AvatarChannel channel){
	String channelName="";
	String channelNameTest = channel.getChannelName();

	//extract first block name
	int pos1=channelNameTest.indexOf('/');
	int pos2;
	if ((channelNameTest.substring(pos1+1,pos1+3)).equals("in")){
	    pos2=pos1+3;	
	}
	else{ 	    
	    pos2=pos1+4;
	}
	channelName=channelName+channelNameTest.substring(0,pos1)+"_";

	//extract first signal name
	channelNameTest=channelNameTest.substring((pos2),channelNameTest.length());
	pos1=channelNameTest.indexOf('(');
	channelName=channelName+channelNameTest.substring(1,pos1)+"__";
	
	pos1=channelNameTest.indexOf('#');
	channelNameTest=channelNameTest.substring(pos1+1,channelNameTest.length());
	pos1=channelNameTest.indexOf('#');
	channelNameTest=channelNameTest.substring(pos1+2,channelNameTest.length());

	//extract second  block name
	pos1=channelNameTest.indexOf('/');

	if ((channelNameTest.substring(pos1+1,pos1+3)).equals("in")){
	    pos2=pos1+3;
	}
	else{ 
	    pos2=pos1+4;
	}
	channelName=channelName+channelNameTest.substring(0,pos1)+"_";

	//extract second signal name
	
        channelNameTest=channelNameTest.substring((pos2),channelNameTest.length());

	pos1=channelNameTest.indexOf('(');	
        channelName=channelName+channelNameTest.substring(1,pos1);	
 
    return channelName;
    }

	public static String getDeclarations() {
	   
		String declaration = "//----------------------------Instantiation-------------------------------" + CR2;	
	
		//Is the platform clustered (currently only 1 central ICN permitted)?
	
		int nb_clusters = TopCellGenerator.avatardd.getAllCrossbar().size();	
		
		boolean trace_caba=true; 

	if(nb_clusters==0){
		declaration += CR
		    + "caba::VciHeterogeneousRom<vci_param> vcihetrom(\"vcihetrom\",  IntTab(0), maptab);" + CR;
	}
	else{
	    declaration += CR
		+ "caba::VciHeterogeneousRom<vci_param> vcihetrom(\"vcihetrom\",  IntTab(0,0), maptab);" + CR;
	}
	if(nb_clusters==0){
	    declaration += "caba::VciRam<vci_param> vcirom(\"vcirom\", IntTab(1), maptab, data_ldr);" + CR;
	}
	else{
	    declaration += "caba::VciRam<vci_param> vcirom(\"vcirom\", IntTab(0,1), maptab, data_ldr);" + CR;
	}

	if(nb_clusters==0){
	    declaration += " caba::VciSimhelper<vci_param> vcisimhelper    (\"vcisimhelper\", IntTab(3), maptab);" + CR;	
}
	else{
	    declaration += " caba::VciSimhelper<vci_param> vcisimhelper    (\"vcisimhelper\", IntTab(0,3), maptab);" + CR;	
	}

	if(nb_clusters==0){
	    declaration = declaration + "caba::VciXicu<vci_param> vcixicu(\"vci_xicu\", maptab, IntTab(4), 1, xicu_n_irq, cpus.size(), cpus.size());" + CR;
	}
	else{	
	    declaration = declaration + "caba::VciXicu<vci_param> vcixicu(\"vci_xicu\", maptab, IntTab(0,4), 1, xicu_n_irq, cpus.size(), cpus.size());" + CR;
	}

	if(nb_clusters==0){
	    declaration = declaration + "caba::VciRtTimer<vci_param> vcirttimer    (\"vcirttimer\", IntTab(5), maptab, 1, true);" + CR2;
	}
	else{
	    declaration = declaration + "caba::VciRtTimer<vci_param> vcirttimer    (\"vcirttimer\", IntTab(0,5), maptab, 1, true);" + CR2;
	}

	if(nb_clusters==0){
	    declaration +=  "caba::VciFdtRom<vci_param> vcifdtrom(\"vci_fdt_rom\", IntTab(6), maptab);" + CR;
	}
	else{
	    declaration +=  "caba::VciFdtRom<vci_param> vcifdtrom(\"vci_fdt_rom\", IntTab(0,6), maptab);" + CR;
	}

	if(nb_clusters==0){
	    declaration +=  "caba::VciLocks<vci_param> vcilocks(\"vcilocks\", IntTab("+(TopCellGenerator.avatardd.getNb_target()+3)+"), maptab);" + CR;
	}	
	else{
	    declaration +=  "caba::VciLocks<vci_param> vcilocks(\"vcilocks\", IntTab(0,8), maptab);" + CR;
	}
	   			    	
	if(nb_clusters==0){
	    int i=0;
	    for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()){
		declaration += "caba::VciMultiTty<vci_param> " + tty.getTTYName()+ "(\"" + tty.getTTYName()+ "\", IntTab(" + tty.getNo_target()+ "), maptab, \"vci_multi_tty"+i+"\", NULL);"+ CR;
		i++;
	    }

	 //target address depends on number of TTYs and RAMs
	
	for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) 
	     if(ram.getNo_ram()==0){
		 declaration += "soclib::caba::VciRam<vci_param>" + ram.getMemoryName()+ "(\"" + ram.getMemoryName()+ "\"" + ", IntTab(2), maptab);" + CR; 
	     }
	     else{
	     declaration += "soclib::caba::VciRam<vci_param>" + ram.getMemoryName()+ "(\"" + ram.getMemoryName()+ "\"" + ", IntTab("
		 + ram.getNo_target() + "), maptab);" + CR; 
	     }
	}
	else{
	    int i=0;
	    for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()){
		declaration += "caba::VciMultiTty<vci_param> " + tty.getTTYName()+ "(\"" + tty.getTTYName()+ "\", IntTab("+ tty.getNo_cluster()+"," + tty.getNo_target()+ "), maptab, \"vci_multi_tty"+i+"\", NULL);"+ CR;
		i++;}

	    for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) 
		declaration += "soclib::caba::VciRam<vci_param>" + ram.getMemoryName()+ "(\"" + ram.getMemoryName()+ "\"" + ", IntTab("+ram.getNo_cluster()+","
		    + ram.getNo_target() + "), maptab);" + CR2; 
	}
	if(nb_clusters==0){
	declaration +=  "caba::VciFdAccess<vci_param> vcifd(\"vcifd\", maptab, IntTab(cpus.size()+1), IntTab("+(TopCellGenerator.avatardd.getNb_target())+"));" + CR;
	declaration +=  "caba::VciEthernet<vci_param> vcieth(\"vcieth\", maptab, IntTab(cpus.size()+2), IntTab("+(TopCellGenerator.avatardd.getNb_target()+1)+"), \"soclib0\");" + CR;
	declaration +=  "caba::VciBlockDevice<vci_param> vcibd(\"vcibd\", maptab, IntTab(cpus.size()), IntTab("+(TopCellGenerator.avatardd.getNb_target()+2)+"),\"block0.iso\", 2048);" + CR;	
  }else{
	declaration +=  "caba::VciFdAccess<vci_param> vcifd(\"vcifd\", maptab, IntTab(0,cpus.size()+1), IntTab(0,7));" + CR;
	declaration +=  "caba::VciEthernet<vci_param> vcieth(\"vcieth\", maptab, IntTab(0,cpus.size()+2), IntTab(0,8), \"soclib0\");" + CR;
	declaration +=  "caba::VciBlockDevice<vci_param> vcibd(\"vcibd\", maptab, IntTab(0,cpus.size()), IntTab(0,9),\"block0.iso\", 2048);" + CR;	
  }

if(nb_clusters==0){
	  for  (AvatarBus bus : TopCellGenerator.avatardd.getAllBus()) {
          System.out.println("initiators: "+TopCellGenerator.avatardd.getNb_init());	
          System.out.println("targets: "+TopCellGenerator.avatardd.getNb_target());
	  
	  declaration += "soclib::caba::VciVgsb<vci_param> vgsb(\"" + bus.getBusName() + "\"" + " , maptab, cpus.size()+3," + (TopCellGenerator.avatardd.getNb_target()+4)+");" + CR2;
	  int i=0;


	  //monitoring connectors marked by spy with the vci_ logger
	  /*  for (AvatarConnector connector : TopCellGenerator.avatardd.getAllConnectors()) { 
		    if (connector.getMonitored()==1){
		      
			
			AvatarConnectingPoint point = connector.getconectingPoint1();
			AvatarComponent component = point.getComponent();
			declaration += "soclib::caba::VciLogger<vci_param> logger"+i+"(\"logger" + i+"\",maptab);" + CR2;
			i++;
			if(component instanceof AvatarRAM){ 		
			   
			}
	
			//cache monitoring not yet implemented
			//	if(component instanceof AvatarCPU){ 
			//	component.setMonitored(1);
			//	}		
			//}
	       }*/


	  //monitoring RAM either by logger(1) ou stats (2) 
	  for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) { 
	     
	      if (ram.getMonitored()==1){
		 
		  declaration += "soclib::caba::VciLogger<vci_param> logger"+i+"(\"logger" + i+"\",maptab);" + CR2;
		  i++;	      
	      }	
	      else{
		  if (ram.getMonitored()==2){		      
             
		      String strArray="";

		      for(AvatarChannel channel: ram.getChannels()){ 
		   
			  String chname = generateName(channel);
		     
			  strArray=strArray+"\""+chname+"\",";
		      }   
		
		      declaration += "soclib::caba::VciMwmrStats<vci_param> mwmr_stats"+i+"(\"mwmr_stats" + i+"\",maptab, data_ldr, \"mwmr"+i+".log\",stringArray("+strArray+"NULL));" + CR2;
		      i++;	      
		  }	
	     }
	  }	 
 
          //if BUS was not last in input file, update here

          bus.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          bus.setnbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target());
	  }	

         for  (AvatarVgmn vgmn : TopCellGenerator.avatardd.getAllVgmn()) {
          System.out.println("initiators: "+TopCellGenerator.avatardd.getNb_init());	
          System.out.println("targets: "+TopCellGenerator.avatardd.getNb_target());
	  /* The user might have forgotten to specify the following, thus set default values */

	  if(vgmn.getMinLatency()<2)
	  vgmn.setMinLatency(10); //default value; must be > 2
	  if(vgmn.getFifoDepth()<2)
          vgmn.setFifoDepth(8); //default value; must be > 2


	  declaration += "soclib::caba::VciVgmn<vci_param> vgmn(\"" + vgmn.getVgmnName() + "\"" + " , maptab, cpus.size()+3," + (TopCellGenerator.avatardd.getNb_target()+4)+
	     "," + vgmn.getMinLatency() + "," + vgmn.getFifoDepth() + ");" + CR2;
	  int i=0;
	
	  for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) { 

	    if (ram.getMonitored()==1){
		int number = ram.getNo_target();
		declaration += "soclib::caba::VciLogger<vci_param> logger"+i+"(\"logger" + i+"\",maptab);" + CR2;
	      i++;	      
	    }	
	    else{

		if (ram.getMonitored()==2){
		int number = ram.getNo_target();
               	
		String strArray="";

                for(AvatarChannel channel: ram.getChannels()){
		    
		    String chname = generateName(channel);

		    strArray=strArray+"\""+chname+"\",";
		}      
		declaration += "soclib::caba::VciMwmrStats<vci_param> mwmr_stats"+i+"(\"mwmr_stats" + i+"\",maptab, data_ldr, \"mwmr0.log\",stringArray("+strArray+"NULL));" + CR2;
	      i++;	      
		}	
	    }
	  }	

	  /*	VciMwmrController(
		sc_module_name name,
		const MappingTable &mt,
		const IntTab &srcid,
		const IntTab &tgtid,
		const size_t plaps,
		const size_t fifo_to_coproc_depth,
		const size_t fifo_from_coproc_depth,
		const size_t n_to_coproc,
		const size_t n_from_coproc,
		const size_t n_config,
		const size_t n_status,
        const bool use_llsc );
	  */

	  //only non-clustered version
	  for (AvatarCoproMWMR copro : TopCellGenerator.avatardd.getAllCoproMWMR()){
		      declaration += "caba::VciMwmrController<vci_param> " + copro.getCoprocName()+ "(\"" + copro.getCoprocName()+ "\", maptab, IntTab("+copro.getSrcid() + "), IntTab("+copro.getTgtid() + "),copro.getPlaps(),copro.getFifoToCoProcDepth(),copro.getNToCopro(),copro.getNFromCopro(),copro.getNConfig(),copro.getNStatus(), copro.getUseLLSC());"+ CR;
		i++;}

	  // if VGMN was not last in input file, update here 
          vgmn.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          vgmn.setnbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target()+4);
	 }
}
else {

    /***************************************/
    /* clustered interconnect architecture */
    /***************************************/

    //monitor connectors marked by spy with the vci_ logger
    /*	  for (AvatarConnector connector : TopCellGenerator.avatardd.getAllConnectors()) { 
		    if (connector.getMonitored()==1){		      		

			AvatarConnectingPoint point = connector.getconectingPoint1();
			AvatarComponent component = point.getComponent();
			declaration += "soclib::caba::VciLogger<vci_param> logger"+i+"(\"logger" + i+"\",maptab);" + CR2;
			i++;
			if(component instanceof AvatarRAM){ 		
			   
			}
	
			//cache monitoring not yet implemented
			//	if(component instanceof AvatarCPU){ 
			//	component.setMonitored(1);
			//	}		
		    }
	   }*/
    
    for  (AvatarBus bus : TopCellGenerator.avatardd.getAllBus()) {
	
	declaration += "soclib::caba::VciVgsb<vci_param>  vgsb(\"" + bus.getBusName() + "\"" + " , maptab, "+ +nb_clusters+"," + nb_clusters + ");" + CR2;
	  
          //if BUS was not last in input file, update here	 
	  int i=0;
	  for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) { 

	      if (ram.getMonitored()==1){
		
		  declaration += "soclib::caba::VciLogger<vci_param> logger"+i+"(\"logger" + i+"\",maptab);" + CR2;
		  i++;	      
	      }	
	      else{
		  if (ram.getMonitored()==2){
		    
             
		      String strArray="";

		      for(AvatarChannel channel: ram.getChannels()){ 
		   
			  String chname = generateName(channel);
		     
			  strArray=strArray+"\""+chname+"\",";
		      }   
		
		      declaration += "soclib::caba::VciMwmrStats<vci_param> mwmr_stats"+i+"(\"mwmr_stats" + i+"\",maptab, data_ldr, \"mwmr"+i+".log\",stringArray("+strArray+"NULL));" + CR2;
		      i++;	      
		  }	
	     }
	  }	           
    }	

         // currently clustered around one vgmn
         for  (AvatarVgmn vgmn : TopCellGenerator.avatardd.getAllVgmn()) {
          System.out.println("initiators: "+TopCellGenerator.avatardd.getNb_init());	
          System.out.println("targets: "+TopCellGenerator.avatardd.getNb_target());
      	 
	  declaration += "soclib::caba::VciVgmn<vci_param> vgmn (\"" + vgmn.getVgmnName() + "\"" + " , maptab, "+ nb_clusters +"," + nb_clusters +
	      "," + vgmn.getMinLatency() + "," + vgmn.getFifoDepth() + ");" + CR2;

	  int i=0;	

	  //monitoring either by logger(1) ou stats (2) 
	  for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) { 

	      if (ram.getMonitored()==1){
		
		  declaration += "soclib::caba::VciLogger<vci_param> logger"+i+"(\"logger" + i+"\",maptab);" + CR2;
		  i++;	      
	      }	
	      else{
		  if (ram.getMonitored()==2){
		    
             
		      String strArray="";

		      for(AvatarChannel channel: ram.getChannels()){ 
		   
			  String chname = generateName(channel);
		     
			  strArray=strArray+"\""+chname+"\",";
		      }   
		
		      declaration += "soclib::caba::VciMwmrStats<vci_param> mwmr_stats"+i+"(\"mwmr_stats" + i+"\",maptab, data_ldr, \"mwmr"+i+".log\",stringArray("+strArray+"NULL));" + CR2;
		      i++;	      
		  }	
	     }
	  }	 		
	 }
	
	 int i=0;
	for  (AvatarCrossbar crossbar : TopCellGenerator.avatardd.getAllCrossbar()) {
	    
	    /* attribution d'un index de cluster par ordre d'arrivee */
	    //currently number on initiators and targets is fixed

	  crossbar.setClusterIndex(i);

	  if (crossbar.getClusterIndex()==0){
		  crossbar.setNbOfAttachedInitiators(nb_clusters);		  
		  crossbar.setNbOfAttachedTargets(13);
	      }
	  else{ 
	      //processor(s) and link to central interconnect are initiators
	      //crossbar.setNbOfAttachedInitiators(2);	 
	      //crossbar.setNbOfAttachedTargets(2);
	      crossbar.setNbOfAttachedInitiators(1);//DG 27.09.	 
	      crossbar.setNbOfAttachedTargets(1);//DG 27.09.
	  }

          System.out.println("initiators: "+crossbar.getNbOfAttachedInitiators());	
          System.out.println("targets: "+crossbar.getNbOfAttachedTargets());
	
	  //declaration += "soclib::caba::VciLocalCrossbar<vci_param> crossbar"+crossbar.getClusterAddress()+"(\"" + crossbar.getCrossbarName() + "\"" + " , maptab, IntTab("+ crossbar.getClusterIndex()+"),IntTab("+crossbar.getClusterAddress()+"), "+crossbar.getNbOfAttachedInitiators()+", "+crossbar.getNbOfAttachedTargets()+");" + CR2;

	  declaration += "soclib::caba::VciLocalCrossbar<vci_param> crossbar"+crossbar.getClusterIndex()+"(\"" + crossbar.getCrossbarName() + "\"" + " , maptab, IntTab("+ crossbar.getClusterIndex()+"),IntTab("+crossbar.getClusterIndex()+"), "+crossbar.getNbOfAttachedInitiators()+", "+crossbar.getNbOfAttachedTargets()+");" + CR2;


          //if CROSSBAR was not last in input file, update here 
          crossbar.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          crossbar.setNbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target());
	 
	  i++;
	}
    }
return declaration;
	}
}
