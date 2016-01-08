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
	int i=0; 
        int calculated_addr = 2130706432;// 0x7f000000 currently fixed

       //address is fixed for the moment but can be given in the DDiagram or calculated
        String deployinfo = CR;

	/* special case: there is only one memory bank and/or at least one CHANNEL channel is mapped to it */
	
	for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {
	String string_adress_start = Integer.toHexString(calculated_addr);

	    if((ram.getNo_ram()==0)&&(!(ram.getChannels().isEmpty()))){
	   
		//deployinfo = "#define DEPLOY_RAM" + i + "_NAME mem_ram"+ CR;
	  
	}
	else{
	    deployinfo += "#define DEPLOY_RAM" + i + "_NAME channel_ram" + ram.getNo_ram() + CR;
	    //}
	    deployinfo = deployinfo + "#define DEPLOY_RAM" + i + "_ADDR 0x" + string_adress_start + CR; // attention this must be hexadecimal	   
	    int size = ram.getDataSize();
	    String string_size = Integer.toHexString(size);
	    deployinfo = deployinfo + "#define DEPLOY_RAM" + i + "_SIZE 0x"+ string_size + CR; // attention this must be hexadecimal
	    //calculated_addr=calculated_addr-16777216; // attention this must be hexadecimal	
	    calculated_addr=calculated_addr-33554432;
	    i++;
	  } 
	}
	return deployinfo;	
    }

 public static String getDeployInfoMap() {
	int i=0;       
        String deployinfo_map = CR;

	deployinfo_map += "#define MAP_A\\" + CR;		
	for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {
	    if (!(ram.getChannels().isEmpty())){
		//deployinfo_map = deployinfo_map +".channel"+i+" : { \\" + CR;
	    for (AvatarChannel channel : ram.getChannels()) {
		deployinfo_map = deployinfo_map +"\n .channel"+i+" : { \\" + CR;
		deployinfo_map = deployinfo_map + "*(section_channel"+i+ ")\\"+ CR;
		i++;
		deployinfo_map=deployinfo_map+ "} > mwmrd_ram\\"+ CR;//DG 07.12.
    /*	    if (ram.getNo_ram()==0){ 
		deployinfo_map=deployinfo_map+ "} > mem_ram\\"+ CR;
	    }
	    else{	
		deployinfo_map=deployinfo_map+ "} > channel_ram"+ ram.getNo_ram()+ "\\" +CR;
		}DG 07.12.*/
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
