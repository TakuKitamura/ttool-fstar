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

//base
// import avatartranslator.AvatarSpecification;
// import avatartranslator.AvatarRelation;
// import avatartranslator.AvatarSignal;
// import ddtranslatorSoclib.AvatarCPU;
// import ddtranslatorSoclib.AvatarRAM;

import ddtranslatorSoclib.*;
import ddtranslatorSoclib.toSoclib.*;

//add
import avatartranslator.*;
import ddtranslatorSoclib.AvatarRAM;
import ddtranslatorSoclib.AvatarTask;
import ddtranslatorSoclib.AvatarddSpecification;
import ddtranslatorSoclib.toTopCell.TopCellGenerator;
import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
//

public class Deployinfo {

    private final static String CR = "\n";
    private final static String CR2 = "\n\n";
  
    public static AvatarRelation ar;
    public static AvatarSpecification avspec;//DG 15.05.2017    
    //add
    public static AvatarddSpecification avddspec;
    private Vector<?> warnings;

    private MainFileSoclib mainFile;
    private Vector<TaskFileSoclib> taskFiles;
    private String makefile_src;
    private String makefile_SocLib;
    //
    /* for the moment, this is specific to PowerPC */

    public Deployinfo(AvatarddSpecification _avddspec, AvatarSpecification _avspec) {
        avspec = _avspec;
        avddspec = _avddspec;
        taskFiles = new Vector<TaskFileSoclib>();
    }

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
	    deployinfo = deployinfo + "#define CACHED_RAM" + ram.getNo_ram()  + "_ADDR 0x" + Integer.toHexString(address_start+i*CLUSTER_SIZE) + CR; 
	    // 31.08. simplifie
	    deployinfo = deployinfo + "#define CACHED_RAM" + ram.getNo_ram()  + "_SIZE 0x"+ string_size_half + CR; 
	    deployinfo += "#define DEPLOY_RAM" + ram.getNo_ram()  + "_NAME uram" + ram.getNo_ram() + CR; 	    
	    int cacheability_bit= 2097152; //0x00200000 
	    deployinfo = deployinfo + "#define DEPLOY_RAM" + ram.getNo_ram()  + "_ADDR 0x" + Integer.toHexString(address_start+i*CLUSTER_SIZE+size/2+cacheability_bit) + CR; 
	    // 31.08. simplifie
	    deployinfo = deployinfo + "#define DEPLOY_RAM" + ram.getNo_ram()  + "_SIZE 0x"+ (string_size_half) + CR;

	    i++;
	}
	return deployinfo;	
    }
    
    /*   public static String getDeployInfoMap() {
	int i=0;       
        String deployinfo_map = CR;
	int nb_signals=0;
	deployinfo_map += "#define MAP_A\\" + CR;		

System.out.println("@@@@@@@@   @@@@@@@@@@@@@@@@@");
	for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {
	    //if (!(ram.getChannels().isEmpty())){	
	    //	for (AvatarChannel channel : ram.getChannels()) {
System.out.println("@@@@@@@@   @@@@@@@@@@@@@@@@@");	   
 //DG 15.05.2017	   	
	    	for (AvatarRelation relation : avspec.getRelations()) {
		    //if (!(ram.getRelations().isEmpty())){
		    //
		    for(i=0; i<relation.nbOfSignals() ; i++) {//DG 15.05.2017
		    deployinfo_map = deployinfo_map +"\n .channel"+nb_signals+" : { \\" + CR;
		    deployinfo_map = deployinfo_map + "*(section_channel"+nb_signals+ ")\\"+ CR;
		   
		    deployinfo_map=deployinfo_map+ "} > uram"+ram.getNo_ram()+"\\"+ CR;	
		    i++;nb_signals++;
		}
		}
		
		i=0;nb_signals=0;
System.out.println("@@@@@@@@   @@@@@@@@@@@@@@@@@");
//	for (AvatarChannel channel : ram.getChannels()) {
	for (AvatarRelation relation : avspec.getRelations()) {
//DG 15.05.2017
System.out.println("@@@@@@@@   @@@@@@@@@@@@@@@@@");
  for(i=0; i<relation.nbOfSignals() ; i++) {//DG 15.05.2017
      System.out.println("@@@@@@@@ 2  @@@@@@@@@@@@@@@@@");

		    deployinfo_map = deployinfo_map +"\n .lock"+nb_signals+" : { \\" + CR;
		    deployinfo_map = deployinfo_map + "*(section_lock"+nb_signals+ ")\\"+ CR;		   
		    //  if(use_vcilocks) deployinfo_map=deployinfo_map+ "} > vci_locks\\"+ CR;
		    deployinfo_map=deployinfo_map+ "} > uram0\\"+ CR;//DG 27.06. no ramlocks
		    i++; nb_signals++;
		}

	}	
	}    
	return deployinfo_map;	
	}*/

    public static String getDeployInfoMap() {
	int i=0;       
        String deployinfo_map = CR;

	deployinfo_map += "#define MAP_A\\" + CR;		
	for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {
	    if (!(ram.getChannels().isEmpty())){	
		//for (AvatarChannel channel : ram.getChannels()) {
		for (i=0;i<30;i++) {
		    deployinfo_map = deployinfo_map +"\n .channel"+i+" : { \\" + CR;
		    deployinfo_map = deployinfo_map + "*(section_channel"+i+ ")\\"+ CR;
		   
		    deployinfo_map=deployinfo_map+ "} > uram"+ram.getNo_ram()+"\\"+ CR;	
		    //i++;
		}
		
		i=0;
		//for (AvatarChannel channel : ram.getChannels()) {
		for (i=0;i<30;i++) {
		    deployinfo_map = deployinfo_map +"\n .lock"+i+" : { \\" + CR;
		    deployinfo_map = deployinfo_map + "*(section_lock"+i+ ")\\"+ CR;		   
		    //  if(use_vcilocks) deployinfo_map=deployinfo_map+ "} > vci_locks\\"+ CR;
		    deployinfo_map=deployinfo_map+ "} > uram0\\"+ CR;//DG 27.06. no ramlocks
		    //i++;
		}

	    }	    
	}
	return deployinfo_map;	
	}


    //ajout C.D.
    /*    public static String getDeployInfoRam() {
        int i=0;
	String deployinfo_ram = CR;
	//List<AvatarRelation> ar= avspec.getRelations();
	//	int k = ar.nbOfSignals();
       	//if(ar !=null){
	//for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) { 
	for(AvatarRelation ar: avspec.getRelations()){
	System.out.println("test test test");
	for (i=0; i<ar.nbOfSignals();i++){
	    deployinfo_ram += "DEPLOY_RAM" + i + "_NAME (RWAL) : ORIGIN = DEPLOY_RAM" + i + "_ADDR, LENGTH = DEPLOY_RAM" + i + "_SIZE" + CR;
	    deployinfo_ram += "CACHED_RAM" + i + "_NAME (RWAL : ORIGIN = CACHED_RAM" + i + "_ADDR, LENGTH = CACHED_RAM" + i + "_SIZE" + CR;
		    //     	}
		      }
	}
	//}
	return deployinfo_ram;
	}*/

    //fin ajout C.D.



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
