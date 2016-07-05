
/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toTopCell;
import java.util.*;
import ddtranslatorSoclib.*;

public class Declaration {
    
    private static String CR = "\n";
    private static String CR2 = "\n\n";

    //DG 28.04. parsing of channel name seems less complicated than identifying the referenced avatar blocks and channels 
       
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

    //fin ajoute DG

	public static String getDeclarations() {
	   
		String declaration = "//----------------------------Instantiation-------------------------------" + CR2;	

		// the following are present in every platform 

		//Is the present architecture clustered? at least one crossbar
	
		int nb_clusters=TopCellGenerator.avatardd.getAllCrossbar().size();	

		boolean trace_caba=true; //tracing is enabled in cycle accurate mode

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
		declaration +=  "caba::VciLocks<vci_param> vci_locks(\"vci_locks\", IntTab("+(TopCellGenerator.avatardd.getNb_target()+3)+"), maptab);" + CR;
}	
else{
	declaration +=  "caba::VciLocks<vci_param> vci_locks(\"vci_locks\", IntTab(0,3), maptab);" + CR;
	}
	
      // There can be an arbitrary number of RAMS (at least one, RAM0 is distinguished) and TTY	
	
		    	
		    if(nb_clusters==0){
			int i=0;
	 for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()){
		    declaration += "caba::VciMultiTty<vci_param> " + tty.getTTYName()+ "(\"" + tty.getTTYName()+ "\", IntTab(" + tty.getNo_target()+ "), maptab, \"vci_multi_tty"+i+"\", NULL);"+ CR;
		    i++;
}

	 //target address depends on number of TTYs
	if(nb_clusters==0){
		    declaration += "caba::VciRam<vci_param>mwmr_ram(\"mwmr_ram\",IntTab("+(TopCellGenerator.avatardd.getNb_target()+3+i)+"),maptab);" + CR2; 
		    declaration += "caba::VciRam<vci_param>mwmrd_ram(\"mwmrd_ram\", IntTab("+(TopCellGenerator.avatardd.getNb_target()+4+i)+"),maptab);" + CR2; 
}
	

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
	  
	  declaration += "soclib::caba::VciVgsb<vci_param> vgsb(\"" + bus.getBusName() + "\"" + " , maptab, cpus.size()+3," + (TopCellGenerator.avatardd.getNb_target()+6)+
	     ");" + CR2;
	  int i=0;

	  //monitoring either by logger(1) ou stats (2) 
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

          //if BUS was not last in input file, update here
          bus.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          bus.setnbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target());
	  }	

         for  (AvatarVgmn vgmn : TopCellGenerator.avatardd.getAllVgmn()) {
          System.out.println("initiators: "+TopCellGenerator.avatardd.getNb_init());	
          System.out.println("targets: "+TopCellGenerator.avatardd.getNb_target());
      
	  declaration += "soclib::caba::VciVgmn<vci_param> vgmn(\"" + vgmn.getVgmnName() + "\"" + " , maptab, cpus.size()+3," + (TopCellGenerator.avatardd.getNb_target()+6)+
	     "," + vgmn.getMinLatency() + "," + vgmn.getFifoDepth() + ");" + CR2;
	  int i=0;
	  //performance measurement infrastructure

	  for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) { 

	    if (ram.getMonitored()==1){
		int number = ram.getNo_target();
		declaration += "soclib::caba::VciLogger<vci_param> logger"+i+"(\"logger" + i+"\",maptab);" + CR2;
	      i++;	      
	    }	
	    else{
		if (ram.getMonitored()==2){
		int number = ram.getNo_target();

                //LinkedList<AvatarChannel> channels=ram.getChannels();	
	
		String strArray="";

                for(AvatarChannel channel: ram.getChannels()){
		    //   strArray=strArray+"\""+channel.getChannelName()+"\","; 
		    String chname = generateName(channel);
		     strArray=strArray+"\""+chname+"\",";
		}      
		declaration += "soclib::caba::VciMwmrStats<vci_param> mwmr_stats"+i+"(\"mwmr_stats" + i+"\",maptab, data_ldr, \"mwmr0.log\",stringArray("+strArray+"NULL));" + CR2;
	      i++;	      
	    }	
	    }
	  }	 

	  // if VGMN was not last in input file, update here 
          vgmn.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          vgmn.setnbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target()+4);
	 }
}else
    //clustered
    {
  for  (AvatarBus bus : TopCellGenerator.avatardd.getAllBus()) {
       	  
	  //for the moment we fix no init and no target
	  declaration += "soclib::caba::VciVgsb<vci_param> " + bus.getBusName() +"(\"" + bus.getBusName() + "\"" + " , maptab, "+ 1 +"," + 6+ ");" + CR2;

          //if BUS was not last in input file, update here

 int i=0;
	  if(trace_caba){
	      for(i=0;i<TopCellGenerator.avatardd.getNb_init();i++){
		  declaration += "soclib::caba::VciLogger<vci_param> logger(\"logger" + i+"\",maptab);" + CR2;
	      }
	      int j=i;
	      for(i=0;i<TopCellGenerator.avatardd.getAllRAM().size()+3;i++){
		  declaration += "soclib::caba::VciLogger<vci_param> logger(\"logger" + j+"\",maptab);" + CR2;
	      }
	  }

          bus.setNbOfAttachedInitiators(1); 
          bus.setnbOfAttachedTargets(6);	  
  }	

         for  (AvatarVgmn vgmn : TopCellGenerator.avatardd.getAllVgmn()) {
          System.out.println("initiators: "+TopCellGenerator.avatardd.getNb_init());	
          System.out.println("targets: "+TopCellGenerator.avatardd.getNb_target());
      
	  declaration += "soclib::caba::VciVgmn<vci_param> "+ vgmn.getVgmnName() +" (\"" + vgmn.getVgmnName() + "\"" + " , maptab, "+ 1 +"," + 6 +
	     "," + vgmn.getMinLatency() + "," + vgmn.getFifoDepth() + ");" + CR2;
	  int i=0;
	  if(trace_caba){
	      for(i=0;i<TopCellGenerator.avatardd.getNb_init();i++){
		  declaration += "soclib::caba::VciLogger<vci_param> logger(\"logger" + i+"\",maptab);" + CR2;
	      }
	      int j=i;
	      for(i=0;i<TopCellGenerator.avatardd.getAllRAM().size()+3;i++){
		  declaration += "soclib::caba::VciLogger<vci_param> logger(\"logger" + j+"\",maptab);" + CR2;
	      }
    }


	  // if VGMN was not last in input file, update here 
          vgmn.setNbOfAttachedInitiators(1); 
          vgmn.setnbOfAttachedTargets(6);	
	 }
	

	for  (AvatarCrossbar crossbar : TopCellGenerator.avatardd.getAllCrossbar()) {
          System.out.println("initiators: "+crossbar.getNbOfAttachedInitiators());	
          System.out.println("targets: "+crossbar.getNbOfAttachedTargets());
	
	  declaration += "soclib::caba::VciLocalCrossbar<vci_param> crossbar(\"" + crossbar.getCrossbarName() + "\"" + " , maptab, IntTab("+ crossbar.getClusterIndex()+"),IntTab("+crossbar.getClusterAddress()+"), "+crossbar.getNbOfAttachedInitiators()+", "+crossbar.getNbOfAttachedTargets()+");" + CR2;

          //if CROSSBAR was not last in input file, update here 
          crossbar.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          crossbar.setnbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target());
	  }	
    }
	  return declaration;
	}
}
