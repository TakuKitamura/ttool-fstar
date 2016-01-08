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

	public static String getMappingTable() {

    /* depending on the cpu type, the addresses of some segments (reset etc.) differ */

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
      mapping = mapping + "maptab.add(Segment(\"vci_fd_access\", 0xd4200000, 0x00000100, IntTab("+(l+1)+"), false));" + CR;
      mapping = mapping + "maptab.add(Segment(\"vci_ethernet\",  0xd5000000, 0x00000020, IntTab("+(l+2)+"), false));" + CR;
      mapping = mapping + "maptab.add(Segment(\"vci_block_device\", 0xd1200000, 0x00000020, IntTab("+(l+3)+"), false));" + CR2;
      mapping = mapping + "maptab.add(Segment(\"vci_locks\", 0x30200000, 0x00000100, IntTab("+(l+4)+"), false));" + CR2;//DG 4.12.
mapping = mapping + "maptab.add(Segment(\"mwmr_ram\", 0xA0200000,  0x00001000, IntTab("+(l+5)+"), false));" + CR2;//DG 4.12.
mapping = mapping + "maptab.add(Segment(\"mwmrd_ram\", 0x20200000,  0x00003000, IntTab("+(l+6)+"), false));" + CR2;//DG 4.12.
      return mapping;
    }
    
}
