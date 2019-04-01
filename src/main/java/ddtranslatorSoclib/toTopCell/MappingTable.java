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




/* this class produces the lines pertaining to the segment table. Except the segments containing CHANNEL channels and those corresponding to targets in shared memory, they need not be sepcified by the user of the deployment diagram */

/* authors: v1.0 Raja GATGOUT 2014
   v2.0 Daniela GENIUS, Julien HENON 2015 - 2016 */


package ddtranslatorSoclib.toTopCell;

import ddtranslatorSoclib.AvatarRAM;
import ddtranslatorSoclib.AvatarTTY;
import ddtranslatorSoclib.*;
import avatartranslator.AvatarRelation;
import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSignal;
import avatartranslator.AvatarSpecification;
//import ddtranslatorSoclib.AvatarCoproMWMR;
import myutil.TraceManager;

public class MappingTable {
	
    private final static String CR = "\n";
    private final static String CR2 = "\n\n"; 
    private static String mapping;
    public static AvatarddSpecification avatardd;
    
    public static String getMappingTable(AvatarddSpecification dd) {
   
	int nb_clusters=TopCellGenerator.avatardd.getAllCrossbar().size();
	int nb_rams=TopCellGenerator.avatardd.getAllRAM().size();
	int nb_ttys=TopCellGenerator.avatardd.getAllTTY().size();
	int nb_hwa=TopCellGenerator.avatardd.getAllCoproMWMR().size();
	avatardd = dd;
    
	if(nb_clusters == 0){
	    mapping = CR2 + "//-----------------------mapping table------------------------" + CR2;
	    mapping = mapping + "// ppc segments" + CR2;

	    mapping = mapping + "maptab.add(Segment(\"resetppc\",  0xffffff80, 0x0080, IntTab(1), true));" + CR;
	    mapping = mapping + "maptab.add(Segment(\"resetnios\", 0x00802000, 0x1000, IntTab(1), true));" + CR;
	    mapping = mapping + "maptab.add(Segment(\"resetzero\", 0x00000000, 0x1000, IntTab(1), true));" + CR;
	    mapping = mapping + "maptab.add(Segment(\"resetmips\", 0xbfc00000, 0x1000, IntTab(1), true));" + CR;
	
	    /*there are seven fixed targets 
	      target 3 to 6 are transparent and do not appear 
	      in the TTool deployment diagram:

	      Targets on RAM0 :
	      the text segment (target 0)
	      the reset segment (target 1)
	      the data segment (target 2)

	      Other targets :
	      the simhelper segment (target 3)
	      the icu segment (target 4)
	      the timer segment (target 5)
	      the dma segment (target 6)
	      the fdt segment (target 7)
	      
	      fd access segment (target 8)
	      ethernet segment (target 9)
	      block device segment (target 10)

	      additional RAM segments (target 10+i)
	      tty segments (target 10+i+j)
	    */

	    mapping += CR2 + "// RAM segments" + CR2;
	    mapping += "maptab.add(Segment(\"text\", 0x60000000, 0x00100000, IntTab(0), true));" + CR;
	    mapping += "maptab.add(Segment(\"rodata\", 0x80000000, 0x01000000, IntTab(1), true));" + CR;
	    mapping += "maptab.add(Segment(\"data\", 0x7f000000, 0x01000000, IntTab(2), false)); " + CR2;

	    mapping = mapping + "maptab.add(Segment(\"simhelper\", 0xd3200000, 0x00000100, IntTab(3), false));" + CR;	    
	    mapping = mapping + "maptab.add(Segment(\"vci_rttimer\", 0xd6200000, 0x00000100, IntTab(5), false));" + CR2;
	    mapping = mapping + " maptab.add(Segment(\"vci_xicu\", 0xd2200000, 0x00001000, IntTab(4), false));" + CR;
	    // mapping = mapping + "maptab.add(Segment(\"vci_dma\", 0xf2000000, 0x00001000, IntTab(6), false));" + CR2;
	    mapping = mapping + "maptab.add(Segment(\"vci_dma\", 0xf0200000, 0x00001000, IntTab(6), false));" + CR2;
	    mapping = mapping + "maptab.add(Segment(\"vci_fdt_rom\", 0xe0200000, 0x00001000, IntTab(7), false));" + CR2;
	    
	    mapping = mapping + "maptab.add(Segment(\"vci_fd_access\", 0xd4200000, 0x00000100, IntTab(8), false));" + CR;
	    mapping = mapping + "maptab.add(Segment(\"vci_ethernet\",  0xd5200000, 0x00000020, IntTab(9), false));" + CR;
	    mapping = mapping + "maptab.add(Segment(\"vci_block_device\", 0xd1200000, 0x00000020, IntTab(10), false));" + CR2;   
    
	    int address_start = 268435456;
	    int j=0; int i=0;
	    int size;
	 
	    for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {
		// if no data size is given calculate default data size
		if(ram.getDataSize()==0){
	
		    if((nb_clusters<16)||(TopCellGenerator.avatardd.getAllRAM().size()<16)){
			size = 1073741824;
		    }
		    else {//dimension segments to be smaller
			size = 268435456;		          
		    }
		}
		else{
		    size = ram.getDataSize();
		}
		ram.setDataSize(size);
	    
		size = ram.getDataSize(); // this is the hardware RAM size 

		int step = 268435456;

		int cacheability_bit= 2097152; //0x00200000	
		      
		/* Boot Ram segments 0,1,2 */
		if(ram.getIndex() ==0){
		    ram.setNo_target(2);
		   
		    mapping += "maptab.add(Segment(\"cram" + ram.getIndex() + "\", 0x" +Integer.toHexString(address_start+i*step)+ ", 0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+(ram.getNo_target())+"), true));" + CR;
		    mapping += "maptab.add(Segment(\"uram" + ram.getIndex() + "\", 0x" + Integer.toHexString(address_start+i*step+cacheability_bit+ram.getDataSize()/2)+ ", 0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+(ram.getNo_target())+"), false));" + CR;
		    i++;
		}
		else{
		    
		    ram.setNo_target(10+i);		    
		    mapping += "maptab.add(Segment(\"cram" + ram.getIndex() + "\", 0x" + Integer.toHexString(address_start+i*step)+ ",  0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+(ram.getNo_target())+"), true));" + CR;
		    mapping += "maptab.add(Segment(\"uram" + ram.getIndex() + "\", 0x" + Integer.toHexString(address_start+i*step+ram.getDataSize()/2+cacheability_bit) + " ,  0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+(ram.getNo_target())+"), false));" + CR;
		    	
		    i++;	 
		}
	    }
	
	    int tty_count=0;   
	    for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()) {
	 
		tty.setNo_target(10+nb_rams+tty_count);  //count only addtional RAMs     
		/* attention this will not work for more than 16 TTYs */
		/* TTY0 = console has a fixed address */

		if (tty.getIndex()==0){
		    mapping += "maptab.add(Segment(\"vci_multi_tty"+tty.getIndex()+"\" , 0xd"+(tty.getIndex())+"200000, 0x00000010, IntTab(" +(tty.getNo_target()) +"), false));" + CR;
		    }
		else{
		    String adr_tty = Integer.toHexString(tty.getIndex()-1);
		    mapping += "maptab.add(Segment(\"vci_multi_tty"+tty.getIndex()+"\" , 0xa"+adr_tty+"200000, 0x00000010, IntTab(" +(tty.getNo_target()) +"), false));" + CR;
		}
		tty_count++;
	    }

        int amsCluster_count = 0;
        for (AvatarAmsCluster amsCluster:TopCellGenerator.avatardd.getAllAmsCluster ()) {
            amsCluster.setNo_target(10+nb_rams+amsCluster_count + nb_ttys);
            mapping += "maptab.add(Segment(\"gpio2vci"+amsCluster.getNo_amsCluster()+"\" , 0xc"+ 
              Integer.toHexString(amsCluster.getNo_amsCluster()) +
              "200000, 0x00000010, IntTab("+amsCluster.getNo_target()+"), false));" + CR;
            amsCluster_count++;
        }

	    /* Instantiation of the MWMR wrappers for hardware accellerators */
	    /* The accelerators themselves are specifies on DIPLODOCUS level */

	    /* There are 10 segments but 3 of them, 0, 1, 2 belong to the boot RAM */
		int segment_count = (10-3)+(nb_rams-1)+nb_ttys;
	    int hwa_count=0;	    
	    int MWMR_SIZE=4096;
	    int MWMRd_SIZE=12288;
     
	    // i=0;
	   
	    for (AvatarCoproMWMR MWMRwrapper : TopCellGenerator.avatardd.getAllCoproMWMR()) {   
		mapping += "maptab.add(Segment(\"mwmr_ram"+hwa_count+"\", 0xA0"+  Integer.toHexString(2097152+MWMR_SIZE*i)+",  0x00001000, IntTab("+segment_count+"), false));" + CR; 
		mapping += "maptab.add(Segment(\"mwmrd_ram"+hwa_count+"\", 0x20"+  Integer.toHexString(2097152+MWMRd_SIZE*i)+",  0x00003000, IntTab("+(segment_count+nb_hwa)+"), false));" + CR; 	 
		hwa_count++;
		segment_count++;
	    } 
	    hwa_count=0;  

	    return mapping;   
	}

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
	    mapping = mapping + "maptab.add(Segment(\"simhelper\", 0x15200000, 0x00000100, IntTab(0,3), false));" + CR;	    
	    mapping = mapping + "maptab.add(Segment(\"vci_fdt_rom\", 0x16200000, 0x00001000, IntTab(0,7), false));" + CR2;
	    mapping = mapping + "maptab.add(Segment(\"vci_fd_access\", 0x17200000, 0x00000100, IntTab(0,8), false));" + CR;
	    mapping = mapping + "maptab.add(Segment(\"vci_ethernet\",  0x18200000, 0x00000020, IntTab(0,9), false));" + CR;
	    mapping = mapping + "maptab.add(Segment(\"vci_block_device\", 0x19200000, 0x00000020, IntTab(0,10), false));" + CR2;   
        
	    int SEG_ICU_BASE  =          285212672;
	    int SEG_ICU_SIZE  =          20;

	    int NB_DMAS  = 1;
	    int SEG_DMA_BASE  =          304087040;
	    int SEG_DMA_SIZE  =          (NB_DMAS * 20);

	    int NB_TIMERS  = 1;
	    int SEG_TIM_BASE  =          318767104;
	    int SEG_TIM_SIZE  =          (NB_TIMERS * 16 );

	    int SEG_TTY_BASE  =          337641472;
	    int SEG_TTY_SIZE  =          16;   

	    int CLUSTER_SIZE;

	    //if the user does not specify the size, take default value
	    
	    if(nb_clusters<16) {
		CLUSTER_SIZE = 268435456;}
	    else {
		CLUSTER_SIZE = 134217728; 
	    }
	    // to be refined, ideally dynamically adapt

	    /* RAM adresses always start at 0x10000000 decimal 268435456*/

	    int SEG_RAM_BASE   =        268435456;    
	    int    cluster = 0;     	 

	    mapping += "maptab.add(Segment(\"vci_rttimer\", 0x"+ Integer.toHexString(SEG_TIM_BASE)+", 0x"+ Integer.toHexString(SEG_TIM_SIZE)+", IntTab(0,5), true));" + CR;
      
	    mapping += "maptab.add(Segment(\"vci_xicu\",0x"+ Integer.toHexString(SEG_ICU_BASE)+", 0x"+ Integer.toHexString(SEG_ICU_SIZE)+", IntTab(0,4), false));" + CR;  
	    mapping += "maptab.add(Segment(\"dma\", 0x"+ Integer.toHexString(SEG_DMA_BASE)+", 0x"+ Integer.toHexString(SEG_DMA_SIZE)+", IntTab(0,6), false));" + CR; 

	    int cacheability_bit= 2097152; //address 0x00200000 

	    /* RAM base address is SEG_RAM_BASE + CLUSTER_NUMBER * CLUSTER_SIZE;
	       this is the memory space covered by the RAMs of a cluster */
    
	    for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {						      	
		mapping += "maptab.add(Segment(\"cram"+TopCellGenerator.getCrossbarIndex(ram)+"_" + ram.getIndex() + "\", 0x"+Integer.toHexString(SEG_RAM_BASE+ TopCellGenerator.getCrossbarIndex(ram)*CLUSTER_SIZE)+",  0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+TopCellGenerator.getCrossbarIndex(ram)+","+(ram.getNo_target())+"), true));" + CR;
	  
		mapping += "maptab.add(Segment(\"uram" + TopCellGenerator.getCrossbarIndex(ram)+"_" +ram.getIndex() + "\",  0x"+Integer.toHexString(SEG_RAM_BASE + TopCellGenerator.getCrossbarIndex(ram)*CLUSTER_SIZE+cacheability_bit)+",  0x"+Integer.toHexString(ram.getDataSize()/2)+", IntTab("+TopCellGenerator.getCrossbarIndex(ram)+","+(ram.getNo_target())+"), false));" + CR;	  
	    }                     
         
	    //Identify the TTYS in current cluster (as opposed to TTYs in total)
	   
	    for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()) {	   
		/* the number of fixed targets varies depending on if on cluster 0 or other clusters */
		
		int tty_no;
		int cluster_no=TopCellGenerator.getCrossbarIndex(tty);
		int cluster_rams=TopCellGenerator.rams_in_cluster(avatardd,cluster_no);
	  
		if(cluster_no==0){	  
		    tty_no=10+cluster_rams;
		}
		else{	     
		    tty_no=cluster_rams;	      
		}
	  
		mapping += "maptab.add(Segment(\"vci_multi_tty"+tty.getIndex()+"\" , 0x"+Integer.toHexString(SEG_TTY_BASE +  cluster_no* CLUSTER_SIZE+(16*tty_no))+", 0x00000010, IntTab("+cluster_no+","+tty_no+"), false));" + CR; 	                tty_no++;
	    }	  
	}
	
	return mapping;   
    }
}
