/* this class produces the lines pertaining to the segment table. Except the segments containing CHANNEL channels and those corresponding to targets in shared memory, they need not be sepcified by the user of the deployment diagram */


/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */


package ddtranslatorSoclib.toTopCell;
import ddtranslatorSoclib.*;
import java.util.*;


public class MappingTable {
	
    private final static String CR = "\n";
	private final static String CR2 = "\n\n"; 
    private static String mapping;

   
    //System.out.println("@@@@@@@@@@nb_clusters@@@@@@@@: "+nb_clusters);	

	public static String getMappingTable() {

	    int nb_clusters=5;//TopCellGenerator.avatardd.getAllCrossbar().size();
    /* depending on the cpu type, the addresses of some segments (reset etc.) differ */

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

    /* here we have a loop over the CHANNEL segments specified in the deployment diagram and we calculate their addresses in a loop; more intelligent algorithms will be proposed later */

int j=0; int k=0; int l=0;

      if(TopCellGenerator.avatardd.getAllCrossbar().size()==0){	
      
      for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {						      
        if(ram.getNo_ram() ==0){
          ram.setNo_target(2);				   
        }
        else{
          ram.setNo_target(7+j);
          mapping += "maptab.add(Segment(\"channel" + j + "\", 0x" + (6 - j) + "f000000, 0x01000000, IntTab("+ram.getNo_target()+"), false));" + CR;
          j++;
        }
      }
     
      for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()) {
	  /* we calculate the target number of one or several (multi-) ttys which come after the j rams and the 7 compulsory targets */	
        tty.setNo_target(7+j+k);
	k++;		 
        /* we use a simple formula for calculating the TTY address in case of multiple (multi-) ttys */	
	mapping += "maptab.add(Segment(\"vci_multi_tty\" , 0xd"+tty.getNo_tty()+"200000, 0x00000010, IntTab(" +tty.getNo_target() +"), false));" + CR;
        j++;
	l=tty.getNo_target();
      }
      }

      else{
  
      for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {						      
        if(ram.getNo_ram() ==0){
          ram.setNo_target(2);				   
        }
        else{
          ram.setNo_target(7+j);
          mapping += "maptab.add(Segment(\"channel" + j + "\", 0x" + (6 - j) + "f000000, 0x01000000, IntTab("+j+","+ram.getNo_target()+"), false));" + CR;
          j++;
        }
      }
      int m=0;
      for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()) {
	  /* we calculate the target number of one or several (multi-) ttys which come after the j rams and the 7 compulsory targets */	
        tty.setNo_target(7+j+k);
	k++;		 
        /* we use a simple formula for calculating the TTY address in case of multiple (multi-) ttys */
	/* only one tty per cluster currently! */	
	mapping += "maptab.add(Segment(\"vci_multi_tty\" , 0xd"+tty.getNo_tty()+"200000, 0x00000010, IntTab("+m+","+tty.getNo_target() +"), false));" + CR;
        j++; m++;
	l=tty.getNo_target();
      }
      }
      
      mapping = mapping + "maptab.add(Segment(\"vci_fd_access\", 0xd4200000, 0x00000100, IntTab("+(l+1)+"), false));" + CR;
      mapping = mapping + "maptab.add(Segment(\"vci_ethernet\",  0xd5000000, 0x00000020, IntTab("+(l+2)+"), false));" + CR;
      mapping = mapping + "maptab.add(Segment(\"vci_block_device\", 0xd1200000, 0x00000020, IntTab("+(l+3)+"), false));" + CR2;
      mapping = mapping + "maptab.add(Segment(\"vci_locks\", 0x30200000, 0x00000100, IntTab("+(l+4)+"), false));" + CR2;//DG 4.12.
mapping = mapping + "maptab.add(Segment(\"mwmr_ram\", 0xA0200000,  0x00001000, IntTab("+(l+5)+"), false));" + CR2;//DG 4.12.
mapping = mapping + "maptab.add(Segment(\"mwmrd_ram\", 0x20200000,  0x00003000, IntTab("+(l+6)+"), false));" + CR2;//DG 4.12.
     
	    }
	    else{
		//clustered version
	
 mapping = CR2 + "//-----------------------mapping table------------------------" + CR2;
      mapping = mapping + "// ppc segments" + CR2;

      mapping = mapping + "maptab.add(Segment(\"resetppc\",  0xffffff80, 0x0080, IntTab(0,1), true));" + CR;
      mapping = mapping + "maptab.add(Segment(\"resetnios\", 0x00802000, 0x1000, IntTab(0,1), true));" + CR;
      mapping = mapping + "maptab.add(Segment(\"resetzero\", 0x00000000, 0x1000, IntTab(0,1), true));" + CR;
      mapping = mapping + "maptab.add(Segment(\"resetmips\", 0xbfc00000, 0x1000, IntTab(0,1), true));" + CR;
	
      mapping += CR2 + "// RAM shared segments" + CR2;
      mapping += "maptab.add(Segment(\"text\", 0x60000000, 0x00100000, IntTab(0,0), true));" + CR;
      mapping += "maptab.add(Segment(\"rodata\", 0x80000000, 0x01000000, IntTab(0,1), true));" + CR;
      mapping += "maptab.add(Segment(\"data\", 0x7f000000, 0x01000000, IntTab(0,2), false)); " + CR2;

      mapping = mapping + "maptab.add(Segment(\"simhelper\", 0xd3200000, 0x00000100, IntTab(0,3), false));" + CR;	
      mapping = mapping + " maptab.add(Segment(\"vci_xicu\", 0xd2200000, 0x00001000, IntTab(0,4), false));" + CR;	
      mapping = mapping + "maptab.add(Segment(\"vci_rttimer\", 0xd6000000, 0x00000100, IntTab(0,5), false));" + CR2;
      mapping = mapping + "maptab.add(Segment(\"vci_fdt_rom\", 0xe0000000, 0x00001000, IntTab(0,6), false));" + CR2;

	  //fixed adresses also for the following hidden components, all in cluster 0 exclusively
	  mapping = mapping + "maptab.add(Segment(\"vci_fd_access\", 0xd4200000, 0x00000100, IntTab(0,7), false));" + CR;
	  mapping = mapping + "maptab.add(Segment(\"vci_ethernet\",  0xd5000000, 0x00000020, IntTab(0,8), false));" + CR;
	  mapping = mapping + "maptab.add(Segment(\"vci_block_device\", 0xd1200000, 0x00000020, IntTab(0,9), false));" + CR2;
     
      //DG 4.4. deal with mapping of channels: has to be revised 
      //Attention: also ldscript becomes more complicated

	  int j=0; int c;
	for (AvatarChannel channel : TopCellGenerator.avatardd.getAllMappedChannels()) {    		
	    //we need to know on which cluster the channel is mapped

	    //DG 5.4. calcul pas encore correct, il faut identifier compbien de canaux il y a par cluster, ou proceder par RAM et identifier les canaux mappe dessus puis incrementer si plusieurs canaux sur la meme ram
	   	  
	    mapping += "maptab.add(Segment(\"channel" + j+ "\", 0x" + channel.getNo_cluster() + "f000000, 0x00100000, IntTab("+channel.getNo_cluster()+","+channel.getRAMNo()+"), false));" + CR;	
		  j++;
	      }	  
	    
	for(c=0;c<nb_clusters;c++){
	      mapping = mapping + "maptab.add(Segment(\"vci_locks"+c+"\", 0x30200000, 0x00000100, IntTab("+c+",3), false));" + CR2;
	      mapping = mapping + "maptab.add(Segment(\"mwmr_ram"+c+"\", 0xA"+c+"200000,  0x00001000, IntTab("+c+",1), false));" + CR2;
	      mapping = mapping + "maptab.add(Segment(\"mwmrd_ram"+c+"\", 0x2"+c+"200000,  0x00003000, IntTab("+c+",2), false));" + CR2;   
	  }

      //now treat ram and tty
	  for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {				    	  	     
	      c=ram.getIndex();
	      ram.setNo_target(4); 
	      mapping += "maptab.add(Segment(\"vci_multi_ram"+ram.getIndex()+"\" , 0xd"+ram.getIndex()+"0200000, 0x00010000, IntTab("+ram.getIndex()+",5), false));" + CR;    
	  }

	  //one tty per cluster
	  for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()) {	   
	      c=tty.getIndex();	
	      tty.setNo_target(5);
		 
	      mapping += "maptab.add(Segment(\"vci_multi_tty"+tty.getIndex()+"\" , 0xd"+tty.getIndex()+"0200000, 0x00000010, IntTab("+tty.getIndex()+",5), false));" + CR;    
	  }
	    }
                    
	  return mapping;   
	}
}
