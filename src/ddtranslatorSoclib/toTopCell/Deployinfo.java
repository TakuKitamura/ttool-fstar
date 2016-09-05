/* This class generates the deployment info for the ldscript*/


/* authors: v1.0 Daniela GENIUS 2015 modified for clustered architecture 08/2016 */

/* 

/* here is an example of an ldscript :
#define CHANNEL_0_NAME channel0
#define CHANNEL_1_NAME channel1

#define DEPLOY_RAM_0_NAME channel_ram0
#define DEPLOY_RAM_0_ADDR 0x6f000000
#define DEPLOY_RAM_0_SIZE 0x01000000

#define DEPLOY_RAM_1_NAME channel_ram1
#define DEPLOY_RAM_1_ADDR 0x5f000000
#define DEPLOY_RAM_1_SIZE 0x01000000

#define MAP_A\
        .channel0 : { \
               __channel_0_start = ABSOLUTE(.);\
              *(section_channel0)\
                } > DEPLOY_RAM_0_NAME\            
        .channel1 : { \
               __channel_1_start = ABSOLUTE(.);\
              *(section_channel1) \
                } > DEPLOY_RAM_1_NAME 
 */

package ddtranslatorSoclib.toTopCell;
import ddtranslatorSoclib.*;
import java.util.*;

public class Deployinfo {

    private final static String CR = "\n";
    private final static String CR2 = "\n\n";

    /* for the moment, this is specific to PowerPC */

    public static String getDeployInfo() {
          
	int nb_clusters=TopCellGenerator.avatardd.getAllCrossbar().size();

        String deployinfo = CR;

	/* we will have to dimension the segments according to the number of clusters, number of RAMS etc. */

	/* first, determine the "step" between segments dedicated to a cluster */

	int CLUSTER_SIZE;

      //if the user does not specify the size, take default value
      if(nb_clusters<16) {
	  CLUSTER_SIZE = 268435456;
      }
      else {
	  CLUSTER_SIZE = 134217728; 
      } // to be refined, cf DSX -> dynamically adapt


	int size;

	/* there can be many RAMS, but then must be smaller dimensioned */

	int i=0;
	for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {

	    /* data memory always starts at 0x10000000 */
	    int address_start = 268435456;
	    String string_adress_start = Integer.toHexString(i*268435456);
		  
	    /* segment size is either given by the user or a default value is calculated */
	    if(ram.getDataSize()==0){
	
		if((nb_clusters<16)||(TopCellGenerator.avatardd.getAllRAM().size()<16)){
		    size = 268435456; 
		    	   
		}
		else {//smaller segments
		    size =  134217728;
		} // to be refined, a la DSX
	    }
	    else{
		size = ram.getDataSize();
	    }
	    ram.setDataSize(size);
	    //ram.setDataSize(0);
	    size = ram.getDataSize(); // this is the hardware RAM size 

	    System.out.println("***hardware RAM size"+size);

	    String string_size_half = (Integer.toHexString(size/2)); //segments on this are half uram, half cram

	    deployinfo += "#define CACHED_RAM" + ram.getNo_ram()  + "_NAME cram" + ram.getNo_ram() + CR;	    
  
	    //  deployinfo = deployinfo + "#define CACHED_RAM" + ram.getNo_ram()  + "_ADDR 0x" + Integer.toHexString(address_start+i*size) + CR; 
deployinfo = deployinfo + "#define CACHED_RAM" + ram.getNo_ram()  + "_ADDR 0x" + Integer.toHexString(address_start+i*CLUSTER_SIZE) + CR; 

// 31.08. simplifie

	    //deployinfo = deployinfo + "#define CACHED_RAM" + ram.getNo_ram()  + "_ADDR 0x" + (ram.getNo_ram()+1)+ "0000000" + CR; 

	    deployinfo = deployinfo + "#define CACHED_RAM" + ram.getNo_ram()  + "_SIZE 0x"+ string_size_half + CR; 

	    deployinfo += "#define DEPLOY_RAM" + ram.getNo_ram()  + "_NAME uram" + ram.getNo_ram() + CR;
	   	    
	    int cacheability_bit= 2097152; //0x00200000

	    // deployinfo = deployinfo + "#define DEPLOY_RAM" + ram.getNo_ram()  + "_ADDR 0x" + Integer.toHexString(address_start+i*size+size/2) + CR; 

	    //deployinfo = deployinfo + "#define DEPLOY_RAM" + ram.getNo_ram()  + "_ADDR 0x" + Integer.toHexString(address_start+i*size+size/2+cacheability_bit) + CR; 

deployinfo = deployinfo + "#define DEPLOY_RAM" + ram.getNo_ram()  + "_ADDR 0x" + Integer.toHexString(address_start+i*CLUSTER_SIZE+size/2+cacheability_bit) + CR; 

// 31.08. simplifie

//deployinfo = deployinfo + "#define DEPLOY_RAM" + ram.getNo_ram()  + "_ADDR 0x"+  (ram.getNo_ram()+1)+ "0200000" + CR; 

	    deployinfo = deployinfo + "#define DEPLOY_RAM" + ram.getNo_ram()  + "_SIZE 0x"+ (string_size_half) + CR;
	    
	    i++;
	}
	return deployinfo;	
    }

 public static String getDeployInfoMap() {
	int i=0;       
        String deployinfo_map = CR;

	deployinfo_map += "#define MAP_A\\" + CR;		
	for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {
	    if (!(ram.getChannels().isEmpty())){	
		for (AvatarChannel channel : ram.getChannels()) {
		    deployinfo_map = deployinfo_map +"\n .channel"+i+" : { \\" + CR;
		    deployinfo_map = deployinfo_map + "*(section_channel"+i+ ")\\"+ CR;
		   
		    deployinfo_map=deployinfo_map+ "} > uram"+ram.getNo_ram()+"\\"+ CR;	
		    i++;
		}
		
		i=0;
	for (AvatarChannel channel : ram.getChannels()) {
		    deployinfo_map = deployinfo_map +"\n .lock"+i+" : { \\" + CR;
		    deployinfo_map = deployinfo_map + "*(section_lock"+i+ ")\\"+ CR;		   
		    //  if(use_vcilocks) deployinfo_map=deployinfo_map+ "} > vci_locks\\"+ CR;
		    deployinfo_map=deployinfo_map+ "} > uram0\\"+ CR;//DG 27.06. no ramlocks
		    i++;
		}

	    }	    
	}
	return deployinfo_map;	
    }


public static String getProcInfo() {
	int i=0; 
     
        String procinfo = "SOCLIB_CPU_COUNT = ";
	
	for (AvatarCPU cpu : TopCellGenerator.avatardd.getAllCPU()) {
	    i++;
	}
	  
	procinfo +=  i + CR;	   
	return procinfo;	
    }


public static String getNbProc() {
	int i=0; 
     
        String nbproc = "CONFIG_CPU_MAXCOUNT ";
	
	for (AvatarCPU cpu : TopCellGenerator.avatardd.getAllCPU()) {
	    i++;
	}
	  
	nbproc +=  i + CR;	   
	return nbproc;	
    }
}
