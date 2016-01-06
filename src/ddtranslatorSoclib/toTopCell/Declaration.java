
/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toTopCell;
import java.util.*;
import ddtranslatorSoclib.*;

public class Declaration {
    
    private static String CR = "\n";
    private static String CR2 = "\n\n";

	public static String getDeclarations() {
	   
		String declaration = "//----------------------------Instantiation-------------------------------" + CR2;	

		// the following are present in every platform and their adresses fixed once and for all	
		declaration += CR
				+ "caba::VciHeterogeneousRom<vci_param> vcihetrom(\"vcihetrom\",  IntTab(0), maptab);" + CR;

		declaration += "caba::VciRam<vci_param> vcirom(\"vcirom\", IntTab(1), maptab, data_ldr);" + CR;

		declaration += " caba::VciSimhelper<vci_param> vcisimhelper    (\"vcisimhelper\", IntTab(3), maptab);" + CR;
		//current hypothesis max. 1 (multi-) timer
		declaration = declaration + "caba::VciXicu<vci_param> vcixicu(\"vci_xicu\", maptab, IntTab(4), 1, xicu_n_irq, cpus.size(), cpus.size());" + CR;

		declaration = declaration + "caba::VciRtTimer<vci_param> vcirttimer    (\"vcirttimer\", IntTab(5), maptab, 1, true);" + CR2;
	
		declaration +=  "caba::VciFdtRom<vci_param> vcifdtrom(\"vci_fdt_rom\", IntTab(6), maptab);" + CR;

		declaration +=  "caba::VciLocks<vci_param> vci_locks(\"vci_locks\", IntTab("+(TopCellGenerator.avatardd.getNb_target()+3)+"), maptab);" + CR;//DG 04.12.	
		declaration += "caba::VciRam<vci_param>mwmr_ram(\"mwmr_ram\",IntTab("+(TopCellGenerator.avatardd.getNb_target()+3)+"),maptab);" + CR2; 
		declaration += "caba::VciRam<vci_param>mwmrd_ram(\"mwmrd_ram\", IntTab("+(TopCellGenerator.avatardd.getNb_target()+4)+"),maptab);" + CR2; 
      // There can be an arbitrary number of RAMS (at least one, RAM0 is distinguished) and TTY		

	 for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()){
		    declaration += "caba::VciMultiTty<vci_param> " + tty.getTTYName()+ "(\"" + tty.getTTYName()+ "\", IntTab(" + tty.getNo_target()+ "), maptab, \"vci_multi_tty"+"\", NULL);";}

        for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) 
          declaration += "soclib::caba::VciRam<vci_param>" + ram.getMemoryName()+ "(\"" + ram.getMemoryName()+ "\"" + ", IntTab("
            + ram.getNo_target() + "), maptab);" + CR2; 

	declaration +=  "caba::VciFdAccess<vci_param> vcifd(\"vcifd\", maptab, IntTab(cpus.size()+1), IntTab("+(TopCellGenerator.avatardd.getNb_target())+"));" + CR;
	declaration +=  "caba::VciEthernet<vci_param> vcieth(\"vcieth\", maptab, IntTab(cpus.size()+2), IntTab("+(TopCellGenerator.avatardd.getNb_target()+1)+"), \"soclib0\");" + CR;
	declaration +=  "caba::VciBlockDevice<vci_param> vcibd(\"vcibd\", maptab, IntTab(cpus.size()), IntTab("+(TopCellGenerator.avatardd.getNb_target()+2)+"),\"block0.iso\", 2048);" + CR;	

	  for  (AvatarBus bus : TopCellGenerator.avatardd.getAllBus()) {
          System.out.println("initiators: "+TopCellGenerator.avatardd.getNb_init());	
          System.out.println("targets: "+TopCellGenerator.avatardd.getNb_target());
	  
	  declaration += "soclib::caba::VciVgsb<vci_param> vgsb(\"" + bus.getBusName() + "\"" + " , maptab, cpus.size()+3," + (TopCellGenerator.avatardd.getNb_target()+6)+
	     ");" + CR2;

          //if BUS was not last in input file, update here
          bus.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          bus.setnbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target());
	  }	

         for  (AvatarVgmn vgmn : TopCellGenerator.avatardd.getAllVgmn()) {
          System.out.println("initiators: "+TopCellGenerator.avatardd.getNb_init());	
          System.out.println("targets: "+TopCellGenerator.avatardd.getNb_target());
      
	  declaration += "soclib::caba::VciVgmn<vci_param> vgmn(\"" + vgmn.getVgmnName() + "\"" + " , maptab, cpus.size()+3," + (TopCellGenerator.avatardd.getNb_target()+6)+
	     "," + vgmn.getMinLatency() + "," + vgmn.getFifoDepth() + ");" + CR2;

	  // if VGMN was not last in input file, update here 
          vgmn.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          vgmn.setnbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target()+4);//DG 04.12. two additionnal targets for channel mapping
	  }	

	int cluster_index;
	int cluster_address;
	for  (AvatarCrossbar crossbar : TopCellGenerator.avatardd.getAllCrossbar()) {
          System.out.println("initiators: "+TopCellGenerator.avatardd.getNb_init());	
          System.out.println("targets: "+TopCellGenerator.avatardd.getNb_target());
	  cluster_index=crossbar.getClusterIndex();
	  cluster_address=crossbar.getClusterAddress();//Adress doit etre calculee a terme
	  declaration += "soclib::caba::VciLocalCrossbar<vci_param> crossbar(\"" + crossbar.getCrossbarName() + "\"" + " , maptab, cluster_index, cluster_address, cpus.size()+3," + (TopCellGenerator.avatardd.getNb_target()+3)+
	     ");" + CR2;

          //if CROSSBAR was not last in input file, update here 
          crossbar.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          crossbar.setnbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target());
	  }	
	  return declaration;
    }
}
