/* this class produces the lines pertaining to the segment table. Except the segments containing CHANNEL channels and those corresponding to targets in shared memory, they need not be sepcified by the user of the deployment diagram */

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 - 2016 */


package ddtranslatorSoclib.toTopCell;
import ddtranslatorSoclib.*;
import java.util.*;

public class MappingTable {
	
    private final static String CR = "\n";
    private final static String CR2 = "\n\n"; 
    private static String mapping;
      
    public static String getMappingTable() {
    int l=0;
    int nb_clusters=TopCellGenerator.avatardd.getAllCrossbar().size();
    System.out.println("Number of clusters : "+ nb_clusters);
    if(nb_clusters == 0){
	mapping = CR2 + "//-----------------------mapping table------------------------" + CR2;
	mapping = mapping + "// ppc segments" + CR2;

	mapping = mapping + "maptab.add(Segment(\"resetppc\",  0xffffff80, 0x0080, IntTab(1), true));" + CR;
	mapping = mapping + "maptab.add(Segment(\"resetnios\", 0x00802000, 0x1000, IntTab(1), true));" + CR;
	mapping = mapping + "maptab.add(Segment(\"resetzero\", 0x00000000, 0x1000, IntTab(1), true));" + CR;
	mapping = mapping + "maptab.add(Segment(\"resetmips\", 0xbfc00000, 0x1000, IntTab(1), true));" + CR;
	
    /*there are seven targets which are fixed; target 3 to 6 are transparent and do not appear in the TTool deployment diagram:

      Targets on RAM0 :
      the text segment (target 0)
      the reset segment (target 1)
      the data segment (target 2)

      Other targets :
      the simhelper segment (target 3)
      the icu segment (target 4)
      the timer segment (target 5)
      the fdt segment (target 6)
 
      additional RAM segments (target 6+i)
      tty segments (target 6+i+j)
      fd access segment (target 6+i+j+1)
      ethernet segment (target 6+i+j+2)
      block device segment (target 6+i+j+3)
    */

	mapping += CR2 + "// RAM segments" + CR2;
	mapping += "maptab.add(Segment(\"text\", 0x60000000, 0x00100000, IntTab(0), true));" + CR;
	mapping += "maptab.add(Segment(\"rodata\", 0x80000000, 0x01000000, IntTab(1), true));" + CR;
	mapping += "maptab.add(Segment(\"data\", 0x7f000000, 0x01000000, IntTab(2), false)); " + CR2;

	mapping = mapping + "maptab.add(Segment(\"simhelper\", 0xd3200000, 0x00000100, IntTab(3), false));" + CR;	
	mapping = mapping + " maptab.add(Segment(\"vci_xicu\", 0xd2200000, 0x00001000, IntTab(4), false));" + CR;	
	mapping = mapping + "maptab.add(Segment(\"vci_rttimer\", 0xd6000000, 0x00000100, IntTab(5), false));" + CR2;
	mapping = mapping + "maptab.add(Segment(\"vci_fdt_rom\", 0xe0000000, 0x00001000, IntTab(6), false));" + CR2;

    
	int address_start = 268435456;
	int j=0; int i=0;
	int size;
	//if(TopCellGenerator.avatardd.getAllCrossbar().size()==0){	     
	    for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {
	
			 if(ram.getDataSize()==0){
	
		if((nb_clusters<16)||(TopCellGenerator.avatardd.getAllRAM().size()<16)){
		    size = 1073741824;
		}
		else {//smaller segments
		    size = 268435456; 
		} // to be refined, a la DSX
	    }
	    else{
		size = ram.getDataSize();
	    }
	    ram.setDataSize(size);
	    //ram.setDataSize(0);
	    size = ram.getDataSize(); // this is the hardware RAM size 

	    int cacheability_bit= 2097152; //0x00200000			      
		/* Boot Ram segments 0,1,2 */
		if(ram.getNo_ram() ==0){
		    ram.setNo_target(2);//in the following assign target number 2	
		    //mapping += "maptab.add(Segment(\"cram" + ram.getNo_ram() + "\", 0x" +(ram.getNo_ram()+1)+ "0000000, 0x00100000, IntTab("+(ram.getNo_target())+"), true));" + CR;
		    //mapping += "maptab.add(Segment(\"uram" + ram.getNo_ram() + "\", 0x" +(ram.getNo_ram()+1)+ "0200000, 0x00100000, IntTab("+(ram.getNo_target())+"), false));" + CR;	

		    /*   mapping += "maptab.add(Segment(\"cram" + ram.getNo_ram() + "\", 0x" +(ram.getNo_ram()+1)+ "0000000, "+ram.getDataSize()+", IntTab("+(ram.getNo_target())+"), true));" + CR;
			 mapping += "maptab.add(Segment(\"uram" + ram.getNo_ram() + "\", 0x" +(ram.getNo_ram()+1)+ "0200000, "+ram.getDataSize()+", IntTab("+(ram.getNo_target())+"), false));" + CR;*/
		   
 mapping += "maptab.add(Segment(\"cram" + ram.getNo_ram() + "\", 0x" +Integer.toHexString(address_start+i*size)+ ", 0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+(ram.getNo_target())+"), true));" + CR;
 mapping += "maptab.add(Segment(\"uram" + ram.getNo_ram() + "\", 0x" + Integer.toHexString(address_start+i*size+cacheability_bit+ram.getDataSize()/2)+ ", 0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+(ram.getNo_target())+"), false));" + CR;
	
	
		}
		else{
		    ram.setNo_target(7+j);
		    //mapping += "maptab.add(Segment(\"cram" + ram.getNo_ram() + "\", 0x" +(ram.getNo_ram()+1)+ "0000000, 0x00100000, IntTab("+(ram.getNo_target())+"), true));" + CR;
		    //mapping += "maptab.add(Segment(\"uram" + ram.getNo_ram() + "\", 0x" +(ram.getNo_ram()+1)+ "0200000, 0x00100000, IntTab("+(ram.getNo_target())+"), false));" + CR;
		    mapping += "maptab.add(Segment(\"cram" + ram.getNo_ram() + "\", 0x" + Integer.toHexString(address_start+i*size)+ ",  0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+(ram.getNo_target())+"), true));" + CR;
 mapping += "maptab.add(Segment(\"uram" + ram.getNo_ram() + "\", 0x" + Integer.toHexString(address_start+i*size+size/2+cacheability_bit) + " ,  0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+(ram.getNo_target())+"), false));" + CR;

		    j++;	 
		}
		i++;
	    }
      int m=0;
      for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()) {
	  /* we calculate the target number of one or several (multi-) ttys which come after the j rams and the 7 compulsory targets */	
        tty.setNo_target(7+j);		 
        /* we use a simple formula for calculating the TTY address in case of multiple (multi-) ttys */	
	/* attention this will not work for more than 10 TTYs */
	mapping += "maptab.add(Segment(\"vci_multi_tty"+m+"\" , 0xd"+tty.getNo_tty()+"200000, 0x00000010, IntTab(" +tty.getNo_target() +"), false));" + CR;
	//	mapping += "maptab.add(Segment(\"vci_multi_tty"+m+"\" , 0xe"+(m+1)+"200000, 0x00000010, IntTab(" +tty.getNo_target() +"), false));" + CR; 

        j++;
	m++;
	l=tty.getNo_target();
      }
      // }
      mapping = mapping + "maptab.add(Segment(\"vci_fd_access\", 0xd4200000, 0x00000100, IntTab("+(l+1)+"), false));" + CR;
      mapping = mapping + "maptab.add(Segment(\"vci_ethernet\",  0xd5000000, 0x00000020, IntTab("+(l+2)+"), false));" + CR;
      mapping = mapping + "maptab.add(Segment(\"vci_block_device\", 0xd1200000, 0x00000020, IntTab("+(l+3)+"), false));" + CR2;
      mapping = mapping + "maptab.add(Segment(\"vci_locks\", 0xC0200000, 0x00000100, IntTab("+(l+4)+"), false));" + CR2;
      // mapping = mapping + "maptab.add(Segment(\"mwmr_ram\", 0xA0200000,  0x00001000, IntTab("+(l+5)+"), false));" + CR2;
      //mapping = mapping + "maptab.add(Segment(\"mwmrd_ram\", 0xB0200000,  0x00003000, IntTab("+(l+6)+"), false));" + CR2;
     
      //DG 29.08.
      // mapping = mapping + "maptab.add(Segment(\"cram\", 0xA0200000,  0x00001000, IntTab("+(l+5)+"), true));" + CR2;
      //mapping = mapping + "maptab.add(Segment(\"uram\", 0xB0200000,  0x00003000, IntTab("+(l+6)+"), false));" + CR2;
      return mapping;   
    }


  ///////////////////////////////////////////////////////////////////////////////////////////////////
    //                 Mapping Table                                                                 //
    // There are 4 replicated segments in each cluster, and 8 single (not replicated) segments.      //
    // - Peripheral single segments (rom, ramdac, tg) are mapped in the cluster_rom and cluster_io.  //
    // - RAM single segments are mapped in each cluster at the first addresse of the cluster.        //
    // - Peripherals replicated segments (timer, icu, dma) are replicated in each cluster.           //
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    
    /*    MappingTable maptab(32, IntTab(8,4), IntTab(8,4), 0xFFFF0000);

    maptab.add(Segment("seg_ie"    , SEG_IE_BASE    , SEG_IE_SIZE    , IntTab(cluster_io , TGTID_IE),     false));
    maptab.add(Segment("seg_oe", SEG_OE_BASE, SEG_OE_SIZE, IntTab(cluster_io , TGTID_OE), false));
    maptab.add(Segment("seg_tty"   , SEG_TTY_BASE   , SEG_TTY_SIZE   , IntTab(cluster_io , TGTID_TTY),    false));
    maptab.add(Segment("seg_reset" , SEG_RESET_BASE , SEG_RESET_SIZE , IntTab(cluster_rom, tgtid_rom),    true));

    for (size_t c = 0; c < nc; c++) {
        uint32_t ram_base = SEG_RAM_BASE + c * CLUSTER_SIZE;
        uint32_t ram_size = SEG_RAM_SIZE;
        std::ostringstream seg_ram_name;
        seg_ram_name << "seg_ram_" << c;
        maptab.add(Segment(seg_ram_name.str(), ram_base, ram_size, IntTab(c, TGTID_RAM), true));

        uint32_t timer_base = SEG_TIM_BASE + c * CLUSTER_SIZE;
        uint32_t timer_size = SEG_TIM_SIZE;
        std::ostringstream seg_timer_name;
        seg_timer_name << "seg_timer_" << c;
        maptab.add(Segment(seg_timer_name.str(), timer_base, timer_size, IntTab(c, TGTID_TIM), true));

        uint32_t icu_base = SEG_ICU_BASE + c * CLUSTER_SIZE;
        uint32_t icu_size = SEG_ICU_SIZE;
        std::ostringstream seg_icu_name;
        seg_icu_name << "seg_icu_" << c;
        maptab.add(Segment(seg_icu_name.str(), icu_base, icu_size, IntTab(c, TGTID_ICU), false)); */

    else{
	/* clustered version */

      mapping = CR2 + "maptab.add(Segment(\"resetppc\",  0xffffff80, 0x0080, IntTab(0,1), true));" + CR;
      mapping = mapping + "maptab.add(Segment(\"resetnios\", 0x00802000, 0x1000, IntTab(0,1), true));" + CR;
      mapping = mapping + "maptab.add(Segment(\"resetzero\", 0x00000000, 0x1000, IntTab(0,1), true));" + CR;
      mapping = mapping + "maptab.add(Segment(\"resetmips\", 0xbfc00000, 0x1000, IntTab(0,1), true));" + CR;
	
      mapping += CR2 + "// RAM shared segments on cluster 0" + CR2;
      mapping += "maptab.add(Segment(\"text\", 0x60000000, 0x00100000, IntTab(0,0), true));" + CR;
      mapping += "maptab.add(Segment(\"rodata\", 0x80000000, 0x01000000, IntTab(0,1), true));" + CR;
      mapping += "maptab.add(Segment(\"data\", 0x7f000000, 0x01000000, IntTab(0,2), false)); " + CR2;
      mapping = mapping + "maptab.add(Segment(\"simhelper\", 0xd3200000, 0x00000100, IntTab(0,3), false));" + CR;	    
      mapping = mapping + "maptab.add(Segment(\"vci_fdt_rom\", 0x16200000, 0x00001000, IntTab(0,6), false));" + CR2;
      mapping = mapping + "maptab.add(Segment(\"vci_fd_access\", 0x17200000, 0x00000100, IntTab(0,7), false));" + CR;
      mapping = mapping + "maptab.add(Segment(\"vci_ethernet\",  0x18200000, 0x00000020, IntTab(0,8), false));" + CR;
      mapping = mapping + "maptab.add(Segment(\"vci_block_device\", 0x19200000, 0x00000020, IntTab(0,9), false));" + CR2;
        
    
      // uint32_t ram_base = SEG_RAM_BASE + c * CLUSTER_SIZE;
      // soft/hard_config.h:#define	 CLUSTER_SIZE  0x40000000

      //  int SEG_RAM_BASE   =        0x10000000;
     
      //the following three components are added transparently in the deployment diagram    
      /* int SEG_ICU_BASE   =          0x11200000;
      int SEG_ICU_SIZE   =          0x00000014;

      int NB_DMAS  = 1;
      int SEG_DMA_BASE   =          0x12200000;
      int SEG_DMA_SIZE  =           (NB_DMAS * 0x00000014);

      int NB_TIMERS  = 1;
      int SEG_TIM_BASE   =          0x13000000;
      int SEG_TIM_SIZE  =           (NB_TIMERS * 16 );

      int SEG_TTY_BASE   =        0x14200000;
      int SEG_TTY_SIZE   =        0x00000010;

      int CLUSTER_SIZE  =  0x40000000;*/

      

      int SEG_ICU_BASE   =          287309824;
      int SEG_ICU_SIZE   =          20;

      int NB_DMAS  = 1;
      int SEG_DMA_BASE   =          304087040;
      int SEG_DMA_SIZE  =           (NB_DMAS * 20);

      int NB_TIMERS  = 1;
      int SEG_TIM_BASE   =          318767104;
      int SEG_TIM_SIZE  =           (NB_TIMERS * 16 );

      int SEG_TTY_BASE   =        337641472;
      int SEG_TTY_SIZE   =        16;

       //int CLUSTER_SIZE  =         1073741824;
       //int CLUSTER_SIZE  = calculated 

      int CLUSTER_SIZE;

      if(nb_clusters<16) {
	  CLUSTER_SIZE = 268435456;}
      else {
	  CLUSTER_SIZE = 134217728; 
      }// to be refined, cf DSX -> dynamically adapt

      /* RAM adresses always start at 0x10000000 dec 268435456*/

      int SEG_RAM_BASE   =        268435456;    
      int    cluster = 0;

  // mapping += "maptab.add(Segment(\"cram0\", "+ (SEG_RAM_BASE + cluster * CLUSTER_SIZE)+", 0x"+SEG_RAM0_SIZE+", IntTab(0,10), true));" + CR;
  // mapping += "maptab.add(Segment(\"uram0\", "+ (SEG_RAM_BASE+0x00200000)+", 0x"+SEG_RAM0_SIZE+", IntTab(0,11), false));" + CR;
	 
  mapping += "maptab.add(Segment(\"icu" + cluster + "\",0x"+ Integer.toHexString(SEG_ICU_BASE)+", 0x"+ Integer.toHexString(SEG_ICU_SIZE)+", IntTab(0,10), false));" + CR;
  mapping += "maptab.add(Segment(\"dma" + cluster + "\", 0x"+ Integer.toHexString(SEG_DMA_BASE)+", 0x"+ Integer.toHexString(SEG_DMA_SIZE)+", IntTab(0,11), false));" + CR;
  mapping += "maptab.add(Segment(\"timer" + cluster + "\", 0x"+ Integer.toHexString(SEG_TIM_BASE)+", 0x"+ Integer.toHexString(SEG_TIM_SIZE)+", IntTab(0,12), true));" + CR;

  // all other clusters  
  for(cluster=1;cluster<nb_clusters; cluster++){
      /*     mapping += "maptab.add(Segment(\"cram" +cluster+ "\", 0x"+ (SEG_RAM_BASE + cluster * CLUSTER_SIZE)+", 0x"+SEG_RAM_SIZE+", IntTab("+cluster+","+0+"), true));" + CR;

	     mapping += "maptab.add(Segment(\"uram" + cluster + "\", 0x"+ (SEG_RAM_BASE + cluster * CLUSTER_SIZE+0x00200000)+", 0x"+SEG_RAM_SIZE+", IntTab("+cluster+","+1+"), true));" + CR;*/
	 
      mapping += "maptab.add(Segment(\"icu" + cluster + "\", 0x"+ Integer.toHexString(SEG_ICU_BASE + cluster * CLUSTER_SIZE)+", 0x"+Integer.toHexString(SEG_ICU_SIZE)+", IntTab("+cluster +","+1+"), true));" + CR;

      mapping += "maptab.add(Segment(\"dma" + cluster + "\", 0x"+ Integer.toHexString(SEG_DMA_BASE + cluster * CLUSTER_SIZE)+", 0x"+Integer.toHexString(SEG_DMA_SIZE)+", IntTab("+cluster +","+2+"), false));" + CR;
	 
 mapping += "maptab.add(Segment(\"timer" + cluster + "\", 0x"+ Integer.toHexString(SEG_TIM_BASE + cluster * CLUSTER_SIZE)+", 0x"+Integer.toHexString(SEG_TIM_SIZE)+", IntTab("+cluster +","+3+"), true));" + CR;
 	   
  }
 
  int cacheability_bit= 2097152; //0x00200000
  //RAM base address is SEG_RAM_BASE + CLUSTER_NUMBER * CLUSTER_SIZE;

  // this is the memory space covered by the RAMs of a cluster
 
  for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {						      	
      mapping += "maptab.add(Segment(\"cram" + ram.getNo_ram() + "\", 0x"+Integer.toHexString(SEG_RAM_BASE+ ram.getNo_cluster()*CLUSTER_SIZE)+", "+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+ram.getNo_cluster()+","+(ram.getNo_target())+"), true));" + CR;	  
      mapping += "maptab.add(Segment(\"uram" + ram.getNo_ram() + "\",  0x"+Integer.toHexString(SEG_RAM_BASE + ram.getNo_cluster()*CLUSTER_SIZE+cacheability_bit)+",  0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+ram.getNo_cluster()+","+(ram.getNo_target())+"), true));" + CR;	  
      }                     
     
  int nb_ram=1; //currently 1 ram per cluster
  cluster=0;       
  for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()) {	   	 	  	  if(cluster==0){tty.setNo_target(13+nb_ram);}
      else{tty.setNo_target(3+nb_ram);}	 	     
          mapping += "maptab.add(Segment(\"vci_multi_tty"+tty.getIndex()+"\" , 0x"+Integer.toHexString(SEG_TTY_BASE +  tty.getNo_cluster()* CLUSTER_SIZE)+", 0x00000010, IntTab("+tty.getNo_cluster()+","+(tty.getNo_target())+"), false));" + CR; 
	  cluster ++;
	  }
	  
    }
    return mapping;   
    }
}
