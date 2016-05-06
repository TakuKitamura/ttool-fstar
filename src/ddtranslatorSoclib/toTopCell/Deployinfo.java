/* This class generates the deployment info for the ldscript*/


/* authors: v1.0 Raja Daniela GENIUS 2015 */

/* It requires information about the mapping of channels on memory banks*/
/* channels and RAM segments are numbered canonically for generation, theit names are thus NOT those in the Deployment Diagram; this is done to simplify generation, as the name also appears in the main.c of the application and is generated canonically there, too (example "section_channel0") */

/* here is an example :
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

        int calculated_addr = 2130706432;// 0x7f000000 currently fixed for power pc
     
        String deployinfo = CR;
	int i=1;
	for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {
	    //String string_adress_start = Integer.toHexString(calculated_addr);
	    String string_adress_start = Integer.toHexString(i*268435456);
	// if((ram.getNo_ram()==0)&&(!(ram.getChannels().isEmpty()))){
	   
		//deployinfo = "#define DEPLOY_RAM" + i + "_NAME mem_ram"+ CR;
	  
		//	}
	    //	else{
	    deployinfo += "#define CACHED_RAM" + ram.getNo_ram()  + "_NAME cram" + ram.getNo_ram() + CR;
	    //}
	    deployinfo = deployinfo + "#define CACHED_RAM" + ram.getNo_ram()  + "_ADDR 0x" + (string_adress_start) + CR; // attention this must be hexadecimal	   
	    int size = 65536;//ram.getDataSize(); DG 2.5.
	    String string_size = (Integer.toHexString(size/2));//half is uram, half is cram
	    deployinfo = deployinfo + "#define CACHED_RAM" + ram.getNo_ram()  + "_SIZE 0x"+ string_size + CR; 

	    deployinfo += "#define DEPLOY_RAM" + ram.getNo_ram()  + "_NAME uram" + ram.getNo_ram() + CR;
	   
	    deployinfo = deployinfo + "#define DEPLOY_RAM" + ram.getNo_ram()  + "_ADDR 0x" + (Integer.toHexString((i*268435456+2097152))) + CR; // attention this must be hexadecimal	   	  
	    deployinfo = deployinfo + "#define DEPLOY_RAM" + ram.getNo_ram()  + "_SIZE 0x"+ string_size + CR; 
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
		    i++;
		    deployinfo_map=deployinfo_map+ "} > uram"+ram.getNo_ram()+"\\"+ CR;	
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
